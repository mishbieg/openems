package io.openems.edge.ess.generic.offgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.battery.api.Battery;
import io.openems.edge.batteryinverter.api.BatteryInverterConstraint;
import io.openems.edge.batteryinverter.api.ManagedSymmetricBatteryInverter;
import io.openems.edge.batteryinverter.api.OffGridBatteryInverter;
import io.openems.edge.batteryinverter.api.SymmetricBatteryInverter;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.modbusslave.ModbusSlaveTable;
import io.openems.edge.common.startstop.StartStop;
import io.openems.edge.common.startstop.StartStopConfig;
import io.openems.edge.common.startstop.StartStoppable;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.api.OffGridEss;
import io.openems.edge.ess.api.SymmetricEss;
import io.openems.edge.ess.generic.common.AbstractGenericManagedEss;
import io.openems.edge.ess.generic.common.GenericManagedEss;
import io.openems.edge.ess.generic.common.offgrid.statemachine.OffGridContext;
import io.openems.edge.ess.generic.common.offgrid.statemachine.OffGridStateMachine;
import io.openems.edge.ess.generic.common.offgrid.statemachine.OffGridStateMachine.OffGridState;
import io.openems.edge.ess.generic.common.statemachine.StateMachine;
import io.openems.edge.ess.generic.common.statemachine.StateMachine.State;
import io.openems.edge.ess.generic.symmetric.ChannelManager;
import io.openems.edge.ess.generic.symmetric.GenericManagedSymmetricEss;
import io.openems.edge.ess.power.api.Constraint;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Power;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.ess.power.api.Relationship;
import io.openems.edge.io.offgridswitch.api.OffGridSwitch;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Ess.Generic.OffGridEss", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE, //
		property = { //
				EventConstants.EVENT_TOPIC + "=" + EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE //
		} //
)

public class GenericOffGridEssImpl
		extends AbstractGenericManagedEss<GenericManagedSymmetricEss, Battery, ManagedSymmetricBatteryInverter>
		implements GenericManagedSymmetricEss, OffGridEss, GenericManagedEss, ManagedSymmetricEss, SymmetricEss,
		OpenemsComponent, EventHandler, StartStoppable, ModbusSlave {

	private final Logger log = LoggerFactory.getLogger(GenericOffGridEssImpl.class);

	/**
	 * Manages the {@link OffGridState}s of the StateMachine.
	 */
	private final OffGridStateMachine offGridStateMachine = new OffGridStateMachine(OffGridState.UNDEFINED);
	private final StateMachine stateMachine = new StateMachine(State.UNDEFINED);

	@Reference
	private Power power;

	@Reference
	private ConfigurationAdmin cm;

	@Reference
	private ComponentManager componentManager;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	private OffGridBatteryInverter batteryInverter;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	private Battery battery;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	private OffGridSwitch offGridSwitch;

	private StartStopConfig startStopConfig = StartStopConfig.AUTO;

	private final ChannelManager channelManager = new ChannelManager(this);

	private int allowedMinSocInOffGrid;
	private int allowedMinCellVoltageInOffGrid;

	public GenericOffGridEssImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				StartStoppable.ChannelId.values(), //
				SymmetricEss.ChannelId.values(), //
				ManagedSymmetricEss.ChannelId.values(), //
				GenericManagedEss.ChannelId.values() //
		);
	}

	@Activate
	void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled(), cm, config.batteryInverter_id(),
				config.battery_id());
		this.startStopConfig = config.startStop();
		// update filter for 'Off Grid Switch'
		if (OpenemsComponent.updateReferenceFilter(cm, this.servicePid(), "offGridSwitch",
				config.ioOffGridSwitch_id())) {
			return;
		}
	}

	@Deactivate
	protected void deactivate() {
		this.getChannelManager().deactivate();
		super.deactivate();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}
		switch (event.getTopic()) {

		case EdgeEventConstants.TOPIC_CYCLE_AFTER_PROCESS_IMAGE:
			this.handleStateMachine();
			break;
		}
	}

	/**
	 * Handles the State-Machine.
	 */
	private void handleStateMachine() {
		// Store the current State
		this.channel(GenericManagedEss.ChannelId.STATE_MACHINE).setNextValue(this.stateMachine.getCurrentState());
		this.channel(GenericManagedEss.ChannelId.OFF_GRID_STATE_MACHINE)
				.setNextValue(this.offGridStateMachine.getCurrentState());

		// Initialize 'Start-Stop' Channel
		this._setStartStop(StartStop.UNDEFINED);

		// Prepare Context
		OffGridContext context = new OffGridContext(this, this.getBattery(), this.getBatteryInverter(),
				this.getOffGridSwitch(), this.allowedMinSocInOffGrid, this.allowedMinCellVoltageInOffGrid);
		// Call the StateMachine
		try {

			this.offGridStateMachine.run(context);

			this.channel(GenericManagedEss.ChannelId.RUN_FAILED).setNextValue(false);
		} catch (OpenemsNamedException e) {
			this.channel(GenericManagedEss.ChannelId.RUN_FAILED).setNextValue(true);
			this.logError(this.log, "StateMachine failed: " + e.getMessage());
		}
	}

	@Override
	public String debugLog() {
		return "SoC:" + this.getSoc().asString() //
				+ "|L:" + this.getActivePower().asString() //
				+ "|Allowed:" //
				+ this.getAllowedChargePower().asStringWithoutUnit() + ";" //
				+ this.getAllowedDischargePower().asString() //
				+ "|" + this.channel(GenericManagedEss.ChannelId.OFF_GRID_STATE_MACHINE).value().asOptionString();
	}

	/**
	 * Forwards the power request to the {@link SymmetricBatteryInverter}.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void applyPower(int activePower, int reactivePower) throws OpenemsNamedException {
		this.getBatteryInverter().run(this.getBattery(), activePower, reactivePower);
	}

	/**
	 * Retrieves PowerPrecision from {@link SymmetricBatteryInverter}.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public int getPowerPrecision() {
		return this.getBatteryInverter().getPowerPrecision();
	}

	/**
	 * Retrieves StaticConstraints from {@link SymmetricBatteryInverter}.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Constraint[] getStaticConstraints() throws OpenemsNamedException {

		List<Constraint> result = new ArrayList<Constraint>();

		// Get BatteryInverterConstraints
		BatteryInverterConstraint[] constraints = this.getBatteryInverter().getStaticConstraints();

		for (int i = 0; i < constraints.length; i++) {
			BatteryInverterConstraint c = constraints[i];
			result.add(this.getPower().createSimpleConstraint(c.description, this, c.phase, c.pwr, c.relationship,
					c.value));
		}

		// If the GenericEss is not in State "STARTED" block ACTIVE and REACTIVE Power!
		if (!this.isStarted()) {
			result.add(this.createPowerConstraint("ActivePower Constraint ESS not Started", Phase.ALL, Pwr.ACTIVE,
					Relationship.EQUALS, 0));
			result.add(this.createPowerConstraint("ReactivePower Constraint ESS not Started", Phase.ALL, Pwr.REACTIVE,
					Relationship.EQUALS, 0));
		}
		return result.toArray(new Constraint[result.size()]);
	}

	private AtomicReference<StartStop> startStopTarget = new AtomicReference<StartStop>(StartStop.UNDEFINED);

	@Override
	public void setStartStop(StartStop value) {
		if (this.startStopTarget.getAndSet(value) != value) {
			// Set only if value changed
			this.stateMachine.forceNextState(State.UNDEFINED);
			this.offGridStateMachine.forceNextState(OffGridState.UNDEFINED);
		}
	}

	@Override
	public StartStop getStartStopTarget() {
		switch (this.startStopConfig) {
		case AUTO:
			// read StartStop-Channel
			return this.startStopTarget.get();

		case START:
			// force START
			return StartStop.START;

		case STOP:
			// force STOP
			return StartStop.STOP;
		}

		assert false;
		return StartStop.UNDEFINED; // can never happen
	}

	@Override
	public ModbusSlaveTable getModbusSlaveTable(AccessMode accessMode) {
		return new ModbusSlaveTable(//
				OpenemsComponent.getModbusSlaveNatureTable(accessMode), //
				SymmetricEss.getModbusSlaveNatureTable(accessMode), //
				ManagedSymmetricEss.getModbusSlaveNatureTable(accessMode) //
		);
	}

	@Override
	protected ChannelManager getChannelManager() {
		return this.channelManager;
	}

	@Override
	protected Battery getBattery() {
		return this.battery;
	}

	@Override
	protected OffGridBatteryInverter getBatteryInverter() {
		return this.batteryInverter;
	}

	@Override
	protected ComponentManager getComponentManager() {
		return this.componentManager;
	}

	protected OffGridSwitch getOffGridSwitch() {
		return this.offGridSwitch;
	}

	@Override
	public Power getPower() {
		return this.power;
	}
}
