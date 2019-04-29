package io.openems.common.channel;

import java.util.Optional;

import com.google.gson.JsonElement;

import io.openems.common.types.OptionsEnum;
import io.openems.common.utils.JsonUtils;

/**
 * Severity/visibility Level
 */
public enum Level implements OptionsEnum {
	/**
	 * "OK" indicates, that everything is OK and there are no messages.
	 */
	OK(0, "Ok"), //
	/**
	 * "Info" indicates, that everything is OK, but there is at least one
	 * informative messages available.
	 */
	INFO(1, "Info"), //
	/**
	 * "Warning" indicates, that there is at least one warning message available.
	 */
	WARNING(2, "Warning"), //
	/**
	 * "Fault" indicates, that there is at least one fault message available.
	 */
	FAULT(3, "Fault");

	private final int value;
	private final String name;

	private Level(int value, String name) {
		this.value = value;
		this.name = name;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OptionsEnum getUndefined() {
		return OK;
	}

	/**
	 * Gets the Level from an integer value.
	 * 
	 * @param value the integer value
	 * @return the Level
	 */
	public static Optional<Level> fromValue(int value) {
		for (Level level : Level.values()) {
			if (value == level.getValue()) {
				return Optional.of(level);
			}
		}
		return Optional.empty();
	}

	/**
	 * Gets the Level from a JsonObject.
	 * 
	 * @param element    the JsonObject
	 * @param memberName the name of the member of the JsonObject
	 * @return the Level
	 */
	public static Optional<Level> fromJson(JsonElement element, String memberName) {
		Optional<Integer> valueOpt = JsonUtils.getAsOptionalInt(element, memberName);
		if (!valueOpt.isPresent()) {
			return Optional.empty();
		}
		return Level.fromValue(valueOpt.get());
	}
}