package com.anmipo.android.trentobus.db;

/**
 * Possible types of bus schedules
 * @author Andrei Popleteev
 */
public enum ScheduleType {
    WORKDAY, HOLIDAY, ANYDAY;

    /**
     * Returns ScheduleType which corresponds to the given 
     * schedule type symbol of the J2ME version. 
     * If symbol not recognized, returns ANYDAY.
     *  
     * @param value
     * @return
     */
    public static ScheduleType parse(String value) {
        ScheduleType result = ANYDAY;
        if ("H".equals(value)) {
            result = HOLIDAY;
        } else if ("W".equals(value)) {
            result = WORKDAY;
        }
        return result;
    }
}
