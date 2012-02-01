package com.anmipo.android.trentobus.db;

import com.anmipo.android.trentobus.R;

/**
 * Possible types of bus schedules
 * 
 * @author Andrei Popleteev
 */
public enum ScheduleType {
	WORKDAY(R.string.schedule_type_workday), HOLIDAY(
			R.string.schedule_type_holiday);

	/** Specifies string resource ID with the enum instance name. */
	final public int nameResourceId;

	/**
	 * @param nameResourceId
	 *            Specifies string resource ID with the enum instance name.
	 */
	private ScheduleType(int nameResourceId) {
		this.nameResourceId = nameResourceId;
	}

	/**
	 * Returns ScheduleType which corresponds to the given schedule type symbol
	 * of the J2ME version. If the symbol is not recognized, throws an
	 * IllegalStateException.
	 * 
	 * @param value
	 * @throws IllegalStateException
	 * @return
	 */
	public static ScheduleType parse(String value) throws IllegalStateException {
		ScheduleType result;
		if ("H".equals(value)) {
			result = HOLIDAY;
		} else if ("W".equals(value)) {
			result = WORKDAY;
		} else {
			throw new IllegalStateException("Invalid schedule type: " + value);
		}
		return result;
	}
}
