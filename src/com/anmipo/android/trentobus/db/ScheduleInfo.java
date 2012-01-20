package com.anmipo.android.trentobus.db;

/**
 * Info about a particular schedule direction/type.
 * @author Andrei Popleteev
 */
public class ScheduleInfo {
    private String direction;
    private ScheduleType type;
    private int scheduleId;
    
    public ScheduleInfo(String direction, ScheduleType type, int scheduleId) {
        this.direction = direction;
        this.type = type;
        this.scheduleId = scheduleId;
    }
    public int getScheduleId() {
        return scheduleId;
    }
    public String getName() {
        return direction;
    }
    public ScheduleType getType() {
        return type;
    }
    public String toString() {
        return "(" + type + ") " + direction;
    }
}
