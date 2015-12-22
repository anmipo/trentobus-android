package com.anmipo.android.trentobus.db;

/**
 * Info about a particular schedule route/type.
 * @author Andrei Popleteev
 */
public class ScheduleInfo {
    final public String busNumber;
    final public Direction direction;
    final public String route;
    final public ScheduleType type;
    final public int scheduleId;
    final public String fileName;
    
    /**
     * @param route
     *            String describing the route's main nodes.
     * @param direction
     *            Direction of the route (forward/return/undefined)
     * @param type
     *            Type of the schedule (see {@link ScheduleType}).
     * @param dataFileName
     *            Name of the data file (in /assets) containing the timetable.
     * @param scheduleId
     *            Unique ID of this schedule.
     */
    public ScheduleInfo(String busNumber, Direction direction, String route, 
            ScheduleType type, String dataFileName, int scheduleId) {
        this.busNumber = busNumber;
        this.direction = direction;
        this.route = route;
        this.type = type;
        this.fileName = dataFileName;
        this.scheduleId = scheduleId;
    }
    public String toString() {
        return "(" + type + ") " + route;
    }
}
