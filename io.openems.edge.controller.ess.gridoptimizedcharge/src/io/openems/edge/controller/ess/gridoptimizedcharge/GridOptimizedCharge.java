package io.openems.edge.controller.ess.gridoptimizedcharge;

import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;

public interface GridOptimizedCharge extends Controller, OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		DELAY_CHARGE_STATE(Doc.of(DelayChargeState.values()) //
				.text("Current state of the delayed charge function")),
		SELL_TO_GRID_LIMIT_STATE(Doc.of(SellToGridLimitState.values()) //
				.text("Current state of the sell to grid limit function")),
		DELAY_CHARGE_MAXIMUM_CHARGE_LIMIT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT) //
				.text("Delay-Charge power limitation")), //
		SELL_TO_GRID_LIMIT_MINIMUM_CHARGE_LIMIT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT) //
				.text("Sell to grid limit charge power limitation")),
		TARGET_MINUTE_ACTUAL(Doc.of(OpenemsType.INTEGER) //
				.text("Actual target minute calculated from prediction without buffer hours")),
		TARGET_MINUTE_ADJUSTED(Doc.of(OpenemsType.INTEGER) //
				.text("Adjusted target minute calculated from prediction including the buffer hours"));
		;

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
	 * Gets the Channel for {@link ChannelId#DELAY_CHARGE_STATE}.
	 *
	 * @return the Channel
	 */
	public default Channel<DelayChargeState> getDelayChargeStateChannel() {
		return this.channel(ChannelId.DELAY_CHARGE_STATE);
	}

	/**
	 * Gets the Status of the grid optimized self consumption. See
	 * {@link ChannelId#DELAY_CHARGE_STATE}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default DelayChargeState getDelayChargeState() {
		return this.getDelayChargeStateChannel().value().asEnum();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#DELAY_CHARGE_STATE} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setDelayChargeState(DelayChargeState value) {
		this.getDelayChargeStateChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#SELL_TO_GRID_LIMIT_STATE}.
	 *
	 * @return the Channel
	 */
	public default Channel<DelayChargeState> getSellToGridLimitStateChannel() {
		return this.channel(ChannelId.SELL_TO_GRID_LIMIT_STATE);
	}

	/**
	 * Gets the Status of the grid optimized self consumption. See
	 * {@link ChannelId#SELL_TO_GRID_LIMIT_STATE}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default DelayChargeState getSellToGridLimitState() {
		return this.getSellToGridLimitStateChannel().value().asEnum();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#SELL_TO_GRID_LIMIT_STATE} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setSellToGridLimitState(SellToGridLimitState value) {
		this.getSellToGridLimitStateChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#DELAY_CHARGE_MAXIMUM_CHARGE_LIMIT}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getDelayChargeLimitChannel() {
		return this.channel(ChannelId.DELAY_CHARGE_MAXIMUM_CHARGE_LIMIT);
	}

	/**
	 * Gets the delay charge power limit in [W]. See
	 * {@link ChannelId#DELAY_CHARGE_MAXIMUM_CHARGE_LIMIT}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getDelayChargeLimit() {
		return this.getDelayChargeLimitChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#DELAY_CHARGE_MAXIMUM_CHARGE_LIMIT} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setDelayChargeLimit(Integer value) {
		this.getDelayChargeLimitChannel().setNextValue(value);
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#DELAY_CHARGE_MAXIMUM_CHARGE_LIMIT} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setDelayChargeLimit(int value) {
		this.getDelayChargeLimitChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#SELL_TO_GRID_LIMIT_MINIMUM_CHARGE_LIMIT}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getSellToGridLimitChargeLimitChannel() {
		return this.channel(ChannelId.SELL_TO_GRID_LIMIT_MINIMUM_CHARGE_LIMIT);
	}

	/**
	 * Gets the sell to grid limit charge power limit in [W]. See
	 * {@link ChannelId#SELL_TO_GRID_LIMIT_MINIMUM_CHARGE_LIMIT}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getSellToGridLimitChargeLimit() {
		return this.getSellToGridLimitChargeLimitChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#SELL_TO_GRID_LIMIT_MINIMUM_CHARGE_LIMIT} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setSellToGridLimitChargeLimit(Integer value) {
		this.getSellToGridLimitChargeLimitChannel().setNextValue(value);
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#SELL_TO_GRID_LIMIT_MINIMUM_CHARGE_LIMIT} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setSellToGridLimitChargeLimit(int value) {
		this.getSellToGridLimitChargeLimitChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#TARGET_MINUTE_ACTUAL}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getTargetMinuteActualChannel() {
		return this.channel(ChannelId.TARGET_MINUTE_ACTUAL);
	}

	/**
	 * Gets the actual target minute of the Day. See
	 * {@link ChannelId#TARGET_MINUTE_ACTUAL}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getTargetMinuteActual() {
		return this.getTargetMinuteActualChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#TARGET_MINUTE_ACTUAL} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setTargetMinuteActual(Integer value) {
		this.getTargetMinuteActualChannel().setNextValue(value);
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#TARGET_MINUTE_ACTUAL} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setTargetMinuteActual(int value) {
		this.getTargetMinuteActualChannel().setNextValue(value);
	}

	/**
	 * Gets the Channel for {@link ChannelId#TARGET_MINUTE_ADJUSTED}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getTargetMinuteAdjustedChannel() {
		return this.channel(ChannelId.TARGET_MINUTE_ADJUSTED);
	}

	/**
	 * Gets the adjusted target minute of the Day. See
	 * {@link ChannelId#TARGET_MINUTE_ADJUSTED}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getTargetMinuteAdjusted() {
		return this.getTargetMinuteAdjustedChannel().value();
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#TARGET_MINUTE_ADJUSTED} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setTargetMinuteAdjusted(Integer value) {
		this.getTargetMinuteAdjustedChannel().setNextValue(value);
	}

	/**
	 * Internal method to set the 'nextValue' on
	 * {@link ChannelId#TARGET_MINUTE_ADJUSTED} Channel.
	 *
	 * @param value the next value
	 */
	public default void _setTargetMinuteAdjusted(int value) {
		this.getTargetMinuteAdjustedChannel().setNextValue(value);
	}

}