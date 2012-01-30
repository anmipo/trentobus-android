package com.anmipo.android.trentobus.db;

/**
 * Info about a particular schedule direction/type.
 * @author Andrei Popleteev
 */
public class ScheduleInfo {
    final public String busNumber;
    final public String direction;
    final public ScheduleType type;
    final public int scheduleId;
	final public String fileName;
    
    /**
     * @param direction
     *            String describing route direction.
     * @param type
     *            Type of the schedule (see {@link ScheduleType}).
     * @param dataFileName
     *            Name of the data file (in /assets) containing the timetable.
     * @param scheduleId
     *            Unique ID of this schedule.
     */
    public ScheduleInfo(String busNumber, String direction, ScheduleType type, 
    		String dataFileName, int scheduleId) {
    	this.busNumber = busNumber;
        this.direction = direction;
        this.type = type;
        this.fileName = dataFileName;
        this.scheduleId = scheduleId;
    }
    public String toString() {
        return "(" + type + ") " + direction;
    }
}
