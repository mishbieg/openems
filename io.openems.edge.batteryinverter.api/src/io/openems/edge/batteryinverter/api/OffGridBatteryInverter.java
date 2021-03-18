package io.openems.edge.batteryinverter.api;

import java.util.Optional;

import org.osgi.annotation.versioning.ProviderType;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Unit;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.BooleanReadChannel;
import io.openems.edge.common.channel.BooleanWriteChannel;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.EnumWriteChannel;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.offgrid.GridType;
import io.openems.edge.common.startstop.StartStoppable;

@ProviderType
public interface OffGridBatteryInverter
		extends ManagedSymmetricBatteryInverter, SymmetricBatteryInverter, StartStoppable {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		/**
		 * Grid-Type.
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Enum
		 * <li>Range: 0=3P4W, 1=3P3W
		 * </ul>
		 */
		GRID_TYPE(Doc.of(GridType.values())),

		/**
		 * Off-Grid-Frequency.
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Integer
		 * <li>Range: 40-60
		 * </ul>
		 */
		OFF_GRID_FREQUENCY(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.READ_WRITE) //
				.unit(Unit.HERTZ)),

		/**
		 * On-Grid-Command
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Boolean
		 * </ul>
		 */
		ON_GRID_CMD(Doc.of(OpenemsType.BOOLEAN) //
				.accessMode(AccessMode.READ_WRITE)), //

		/**
		 * Off-Grid-Command
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Boolean
		 * </ul>
		 */
		OFF_GRID_CMD(Doc.of(OpenemsType.BOOLEAN) //
				.accessMode(AccessMode.READ_WRITE)), //

		/**
		 * Mod-On-Command
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: FalseTrue
		 * </ul>
		 */
		MOD_ON_CMD(Doc.of(OpenemsType.BOOLEAN) //
				.accessMode(AccessMode.READ_WRITE)), //

		/**
		 * Mod-Off-Command
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: FalseTrue
		 * </ul>
		 */
		MOD_OFF_CMD(Doc.of(FalseTrue.values()) //
				.accessMode(AccessMode.READ_WRITE)), //

		/**
		 * Set-Intern-DC-Relay
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Integer
		 * </ul>
		 */
		SET_INTERN_DC_RELAY(Doc.of(OpenemsType.INTEGER) //
				.accessMode(AccessMode.READ_WRITE) //
				.unit(Unit.NONE)),
		/**
		 * Clear-Failure-Command
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Boolean
		 * </ul>
		 */
		CLEAR_FAILURE_CMD(Doc.of(OpenemsType.BOOLEAN) //
				.accessMode(AccessMode.READ_WRITE)),
		/**
		 * Inverter-State
		 * 
		 * <ul>
		 * <li>Interface: {@link OffGridBatteryInverter}
		 * <li>Type: Boolean
		 * </ul>
		 */
		INVERTER_STATE(Doc.of(OpenemsType.BOOLEAN) //
				.accessMode(AccessMode.READ_WRITE)),;

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}

	}

	/**
	 * Gets the Channel for {@link ChannelId#GRID_TYPE}.
	 * 
	 * @return the Channel
	 */
	public default Channel<GridType> getGridTypeChannel() {
		return this.channel(ChannelId.GRID_TYPE);
	}

	/**
	 * Does the battery has 3 phase-4 wire or 3 phase-3 wire grid type ? See
	 * {@link ChannelId#GRID_TYPE}.
	 * 
	 * @return the Channel {@link Value}
	 */
	public default GridType getGridType() {
		return this.getGridTypeChannel().value().asEnum();
	}

	/**
	 * Internal method to set the 'nextValue' on {@link ChannelId#GRID_TYPE}
	 * Channel.
	 * 
	 * @param value the next value
	 */
	public default void _setGridType(GridType value) {
		this.getGridTypeChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#FREQUENCY}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getOffGridFrequencyChannel() {
		return this.channel(ChannelId.OFF_GRID_FREQUENCY);
	}

	/**
	 * Gets the * {@link ChannelId#OFF_GRID_FREQUENCY}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getOffGridFrequency() {
		return this.getOffGridFrequencyChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#OFF_GRID_FREQUENCY} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setOffGridFrequency(Integer value) {
		this.getOffGridFrequencyChannel().setNextValue(value);
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#OFF_GRID_FREQUENCY} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setOffGridFrequency(int value) {
		this.getOffGridFrequencyChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#OFF_GRID_FREQUENCY}.
	 *
	 * @return the Channel
	 */
	public default IntegerWriteChannel getSetOffGridFrequencyChannel() {
		return this.channel(ChannelId.OFF_GRID_FREQUENCY);
	}

	/**
	 * Sets an Off Grid Frequency set point in [Hz]. See
	 * {@link ChannelId#SET_ACTIVE_POWER_EQUALS}.
	 * 
	 * @param value the next write value
	 * @throws OpenemsNamedException on error
	 */
	public default void setOffGridFrequency(Integer value) throws OpenemsNamedException {
		this.getSetOffGridFrequencyChannel().setNextWriteValue(value);
	}

	/**
	 * Starts the inverter.
	 * 
	 * @throws OpenemsNamedException on error
	 */
	public default void setInverterOn() throws OpenemsNamedException {
		EnumWriteChannel setdataModOnCmd = this.channel(ChannelId.MOD_ON_CMD);
		setdataModOnCmd.setNextWriteValue(FalseTrue.TRUE); // true = START

		// TODO change the param later
		softStart(true);

	}

	/**
	 * Stops the inverter.
	 * 
	 * @throws OpenemsNamedException on error
	 */
	public default void setInverterOff() throws OpenemsNamedException {
		EnumWriteChannel setdataModOffCmd = this.channel(ChannelId.MOD_OFF_CMD);
		setdataModOffCmd.setNextWriteValue(FalseTrue.TRUE); // true = STOP
	}

	/**
	 * Executes a Soft-Start. Sets the internal DC relay. Once this register is set
	 * to 1, the PCS will start the soft-start procedure, otherwise, the PCS will do
	 * nothing on the DC input Every time the PCS is powered off, this register will
	 * be cleared to 0. In some particular cases, the BMS wants to re-softstart, the
	 * EMS should actively clear this register to 0, after BMS soft-started, set it
	 * to 1 again.
	 *
	 * @throws OpenemsNamedException on error
	 */
	public default void softStart(boolean switchOn) throws OpenemsNamedException {
		IntegerWriteChannel setDcRelay = this.channel(ChannelId.SET_INTERN_DC_RELAY);
		setDcRelay.setNextWriteValue(switchOn ? 1 : 0);
	}

	public default boolean stateOnOff() {
		BooleanReadChannel v = this.channel(ChannelId.INVERTER_STATE);
		Optional<Boolean> stateOff = v.getNextValue().asOptional();
		return stateOff.isPresent() && stateOff.get();
	}

	/**
	 * set the module to on grid mode. Reading back value makes no sense
	 * 
	 * @throws OpenemsNamedException
	 */
	public default void setOngridCommand() throws OpenemsNamedException {
		BooleanWriteChannel setdataGridOffCmd = this.channel(ChannelId.ON_GRID_CMD);
		// IntegerWriteChannel setdataGridOffCmd =
		// this.channel(EssSinexcel.ChannelId.ON_GRID_CMD);
		setdataGridOffCmd.setNextWriteValue(true); // 1: true, other: illegal
	}

	/**
	 * set the module to off grid mode. Reading back value makes no sense
	 * 
	 * @throws OpenemsNamedException
	 */
	public default void setOffgridCommand() throws OpenemsNamedException {
		EnumWriteChannel setdataGridOffCmd = this.channel(ChannelId.OFF_GRID_CMD);
		setdataGridOffCmd.setNextWriteValue(FalseTrue.TRUE); // 1: true, other: illegal
	}

	public default void setclearFailureCommand() throws OpenemsNamedException {
		EnumWriteChannel setClearFailureCmd = this.channel(ChannelId.CLEAR_FAILURE_CMD);
		setClearFailureCmd.setNextWriteValue(FalseTrue.TRUE); // 1: true, other: illegal
	}

}