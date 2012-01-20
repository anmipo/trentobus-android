package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.List;

/**
 * General info about a bus: number, directions.
 * @author Andrei Popleteev
 */
public class BusInfo {
    private String number;
    private List<ScheduleInfo> scheduleInfos;

    public BusInfo(String number) {
        this.number = number;
        //typically, there are 4 directions: forward/return, workday/holiday
        scheduleInfos = new ArrayList<ScheduleInfo>(4);
    }
    
    public String getNumber() {
        return number;
    }
    protected void addScheduleInfo(ScheduleInfo scheduleInfo) {
        scheduleInfos.add(scheduleInfo);
    }

    public ScheduleInfo getScheduleInfo(int index) {
        return scheduleInfos.get(index);
    }
    public int getScheduleCount() {
        return scheduleInfos.size();
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bus ");
        sb.append(number);
        sb.append('\n');
        for (ScheduleInfo info: scheduleInfos) {
            sb.append(info.toString());
            sb.append('\n');
        }
        return sb.toString();
    }

    //TODO remove this debug method
    public List<ScheduleInfo> getScheduleInfo() {
        return scheduleInfos;
    }
}