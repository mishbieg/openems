
package io.openems.edge.meter.bgetech;

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
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.channel.AccessMode;
import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ElementToChannelConverter;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.FloatDoublewordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlave;
import io.openems.edge.common.modbusslave.ModbusSlaveTable;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.AsymmetricMeter;
import io.openems.edge.meter.api.MeterType;
import io.openems.edge.meter.api.SymmetricMeter;

@Designate(ocd = Config.class, factory = true)
@Component(name = "Meter.BGE-TECH.DRT428M2", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class MeterBgeTechDRT428M2 extends AbstractOpenemsModbusComponent
		implements AsymmetricMeter, SymmetricMeter, OpenemsComponent, ModbusComponent, ModbusSlave {

	private MeterType meterType = MeterType.CONSUMPTION_METERED;

	@Reference
	protected ConfigurationAdmin cm;

	public MeterBgeTechDRT428M2() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				SymmetricMeter.ChannelId.values(), //
				AsymmetricMeter.ChannelId.values(), //
				MeterBgeTechDRT428M2ChannelId.values() //
		);
	}

	@Override
	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	@Activate
	void activate(ComponentContext context, Config config) throws OpenemsException {
		this.meterType = config.type();

		if (super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm,
				"Modbus", config.modbus_id())) {
			return;
		}
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public MeterType getMeterType() {
		return this.meterType;
	}

	@Override
	protected ModbusProtocol defineModbusProtocol() throws OpenemsException {
		ModbusProtocol modbusProtocol = new ModbusProtocol(this,
				new FC3ReadRegistersTask(14, Priority.HIGH,
						m(new FloatDoublewordElement(14))
								.m(AsymmetricMeter.ChannelId.VOLTAGE_L1, ElementToChannelConverter.SCALE_FACTOR_3)
								.m(SymmetricMeter.ChannelId.VOLTAGE, ElementToChannelConverter.SCALE_FACTOR_3).build(),
						m(AsymmetricMeter.ChannelId.VOLTAGE_L2, new FloatDoublewordElement(16),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.VOLTAGE_L3, new FloatDoublewordElement(18),
								ElementToChannelConverter.SCALE_FACTOR_3), //

						m(SymmetricMeter.ChannelId.FREQUENCY, new FloatDoublewordElement(20),
								ElementToChannelConverter.SCALE_FACTOR_3), //

						m(new FloatDoublewordElement(22))
								.m(AsymmetricMeter.ChannelId.CURRENT_L1, ElementToChannelConverter.SCALE_FACTOR_3)
								.m(SymmetricMeter.ChannelId.CURRENT, ElementToChannelConverter.SCALE_FACTOR_3).build(),
						m(AsymmetricMeter.ChannelId.CURRENT_L2, new FloatDoublewordElement(24),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.CURRENT_L3, new FloatDoublewordElement(26),
								ElementToChannelConverter.SCALE_FACTOR_3), //

						m(SymmetricMeter.ChannelId.ACTIVE_POWER, new FloatDoublewordElement(28),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.ACTIVE_POWER_L1, new FloatDoublewordElement(30),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.ACTIVE_POWER_L2, new FloatDoublewordElement(32),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.ACTIVE_POWER_L3, new FloatDoublewordElement(34),
								ElementToChannelConverter.SCALE_FACTOR_3), //

						m(SymmetricMeter.ChannelId.REACTIVE_POWER, new FloatDoublewordElement(36),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.REACTIVE_POWER_L1, new FloatDoublewordElement(38),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.REACTIVE_POWER_L2, new FloatDoublewordElement(40),
								ElementToChannelConverter.SCALE_FACTOR_3), //
						m(AsymmetricMeter.ChannelId.REACTIVE_POWER_L3, new FloatDoublewordElement(42),
								ElementToChannelConverter.SCALE_FACTOR_3), //

						m(MeterBgeTechDRT428M2ChannelId.TOTAL_APPARENT_POWER, new FloatDoublewordElement(44)),
						m(MeterBgeTechDRT428M2ChannelId.L1_APPARENT_POWER, new FloatDoublewordElement(46)),
						m(MeterBgeTechDRT428M2ChannelId.L2_APPARENT_POWER, new FloatDoublewordElement(48)),
						m(MeterBgeTechDRT428M2ChannelId.L3_APPARENT_POWER, new FloatDoublewordElement(50)),

						m(MeterBgeTechDRT428M2ChannelId.TOTAL_POWER_FATCTOR, new FloatDoublewordElement(52)),
						m(MeterBgeTechDRT428M2ChannelId.L1_POWER_FATCTOR, new FloatDoublewordElement(54)),
						m(MeterBgeTechDRT428M2ChannelId.L2_POWER_FATCTOR, new FloatDoublewordElement(56)),
						m(MeterBgeTechDRT428M2ChannelId.L3_POWER_FATCTOR, new FloatDoublewordElement(58))),

				new FC3ReadRegistersTask(256, Priority.HIGH, //
						m(MeterBgeTechDRT428M2ChannelId.TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(256)),
						m(MeterBgeTechDRT428M2ChannelId.L1_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(258)),
						m(MeterBgeTechDRT428M2ChannelId.L2_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(260)),
						m(MeterBgeTechDRT428M2ChannelId.L3_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(262)),

						m(MeterBgeTechDRT428M2ChannelId.FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(264)),
						m(MeterBgeTechDRT428M2ChannelId.L1_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(266)),
						m(MeterBgeTechDRT428M2ChannelId.L2_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(268)),
						m(MeterBgeTechDRT428M2ChannelId.L3_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(270)),

						m(MeterBgeTechDRT428M2ChannelId.REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(272)),
						m(MeterBgeTechDRT428M2ChannelId.L1_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(274)),
						m(MeterBgeTechDRT428M2ChannelId.L2_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(276)),
						m(MeterBgeTechDRT428M2ChannelId.L3_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(278)),

						m(MeterBgeTechDRT428M2ChannelId.TOTAL_REACTIVE_ENERGY, new FloatDoublewordElement(280)),
						m(MeterBgeTechDRT428M2ChannelId.L1_REACTIVE_ENERGY, new FloatDoublewordElement(282)),
						m(MeterBgeTechDRT428M2ChannelId.L2_REACTIVE_ENERGY, new FloatDoublewordElement(284)),
						m(MeterBgeTechDRT428M2ChannelId.L3_REACTIVE_ENERGY, new FloatDoublewordElement(286)),

						m(MeterBgeTechDRT428M2ChannelId.FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(288)),
						m(MeterBgeTechDRT428M2ChannelId.L1_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(290)),
						m(MeterBgeTechDRT428M2ChannelId.L2_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(292)),
						m(MeterBgeTechDRT428M2ChannelId.L3_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(294)),

						m(MeterBgeTechDRT428M2ChannelId.REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(296)),
						m(MeterBgeTechDRT428M2ChannelId.L1_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(298)),
						m(MeterBgeTechDRT428M2ChannelId.L2_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(300)),
						m(MeterBgeTechDRT428M2ChannelId.L3_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(302)),

						m(MeterBgeTechDRT428M2ChannelId.T1_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(304)),
						m(MeterBgeTechDRT428M2ChannelId.T1_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(306)),
						m(MeterBgeTechDRT428M2ChannelId.T1_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(308)),
						m(MeterBgeTechDRT428M2ChannelId.T1_TOTAL_REACTIVE_ENERGY, new FloatDoublewordElement(310)),
						m(MeterBgeTechDRT428M2ChannelId.T1_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(312)),
						m(MeterBgeTechDRT428M2ChannelId.T1_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(314)),

						m(MeterBgeTechDRT428M2ChannelId.T2_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(316)),
						m(MeterBgeTechDRT428M2ChannelId.T2_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(318)),
						m(MeterBgeTechDRT428M2ChannelId.T2_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(320)),
						m(MeterBgeTechDRT428M2ChannelId.T2_TOTAL_REACTIVE_ENERGY, new FloatDoublewordElement(322)),
						m(MeterBgeTechDRT428M2ChannelId.T2_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(324)),
						m(MeterBgeTechDRT428M2ChannelId.T2_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(326)),

						m(MeterBgeTechDRT428M2ChannelId.T3_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(328)),
						m(MeterBgeTechDRT428M2ChannelId.T3_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(330)),
						m(MeterBgeTechDRT428M2ChannelId.T3_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(332)),
						m(MeterBgeTechDRT428M2ChannelId.T3_TOTAL_REACTIVE_ENERGY, new FloatDoublewordElement(334)),
						m(MeterBgeTechDRT428M2ChannelId.T3_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(336)),
						m(MeterBgeTechDRT428M2ChannelId.T3_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(338)),

						m(MeterBgeTechDRT428M2ChannelId.T4_TOTAL_ACTIVE_ENERGY, new FloatDoublewordElement(340)),
						m(MeterBgeTechDRT428M2ChannelId.T4_FORWARD_ACTIVE_ENERGY, new FloatDoublewordElement(342)),
						m(MeterBgeTechDRT428M2ChannelId.T4_REVERSE_ACTIVE_ENERGY, new FloatDoublewordElement(344)),
						m(MeterBgeTechDRT428M2ChannelId.T4_TOTAL_REACTIVE_ENERGY, new FloatDoublewordElement(346)),
						m(MeterBgeTechDRT428M2ChannelId.T4_FORWARD_REACTIVE_ENERGY, new FloatDoublewordElement(348)),
						m(MeterBgeTechDRT428M2ChannelId.T4_REVERSE_REACTIVE_ENERGY, new FloatDoublewordElement(350))));

		return modbusProtocol;
	}

	@Override
	public String debugLog() {
		StringBuilder logout = new StringBuilder();
		logout.append("P: " + this.getActivePower().asString());
		logout.append(" | U: " + this.getVoltage().asString());
		logout.append(" | I: " + this.getCurrent().asString());
		logout.append(" | Q: " + this.getReactivePower().asString());
		logout.append(" | E-IN: " + this.getActiveConsumptionEnergy().asString());
		logout.append(" | E-OUT: " + this.getActiveProductionEnergy().asString());
		logout.append(" | F: " + this.getFrequency().asString());
		logout.append(" | U-L1: " + this.getVoltageL1().asString());
		logout.append(" | U-L2: " + this.getVoltageL2().asString());
		logout.append(" | U-L3: " + this.getVoltageL3().asString());
		logout.append(" | I-L1: " + this.getCurrentL1().asString());
		logout.append(" | I-L2: " + this.getCurrentL2().asString());
		logout.append(" | I-L3: " + this.getCurrentL3().asString());
		logout.append(" | P-L1: " + this.getActivePowerL1().asString());
		logout.append(" | P-L2: " + this.getActivePowerL2().asString());
		logout.append(" | P-L3: " + this.getActivePowerL3().asString());
		logout.append(" | Q-L1: " + this.getReactivePowerL1().asString());
		logout.append(" | Q-L2: " + this.getReactivePowerL2().asString());
		logout.append(" | Q-L3: " + this.getReactivePowerL3().asString());

		return logout.toString();
	}

	@Override
	public ModbusSlaveTable getModbusSlaveTable(AccessMode accessMode) {
		return new ModbusSlaveTable(OpenemsComponent.getModbusSlaveNatureTable(accessMode),
				SymmetricMeter.getModbusSlaveNatureTable(accessMode),
				AsymmetricMeter.getModbusSlaveNatureTable(accessMode));
	}

}
