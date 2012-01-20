package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * Facade for accessing schedule data.
 * @author Andrei Popleteev
 */
public class ScheduleManager {
    
    private static final String SCHEDULE_PATH = "schedule/";
    private static final String INDEX_FILE_NAME = "bus.idx";
    private static final String TAG = "ScheduleManager";
    private Context context;
    private List<BusInfo> buses;
    private HashMap<Integer, ScheduleInfo> scheduleInfos;
    private HashMap<Integer, Schedule> schedules;
    
    public ScheduleManager(Context context) {
        this.context = context.getApplicationContext();
        buses = new ArrayList<BusInfo>(25);
        scheduleInfos = new HashMap<Integer, ScheduleInfo>();
        schedules = new HashMap<Integer, Schedule>();
    }
    
    /**
     * Caches schedule data from resources, if necessary.
     * @throws IOException 
     */
    public void init() throws IOException {
        loadIndex();
        debugPrintBuses();
    }
    
    
    private void loadIndex() throws IOException {
        buses.clear();
        scheduleInfos.clear();
        schedules.clear();
        
        InputStream rawIn = null;
        try {
            rawIn = context.getAssets().open(SCHEDULE_PATH + INDEX_FILE_NAME);
    
            // unique id for each schedule (bus number & direction & type)
            int scheduleId = 0;
            DataInputStream dataIn = new DataInputStream(rawIn);
            try {
                BusInfo busInfo = null;
                int itemCount = dataIn.readUnsignedShort();
                for (int i = 0; i < itemCount; i++) {
                    // The order is important, obviously
                    String scheduleDataFileName = dataIn.readUTF();
                    String busNumber = dataIn.readUTF();
                    ScheduleType schType = ScheduleType.parse(
                            dataIn.readUTF());
                    String direction = dataIn.readUTF();

                    // Bus numbers are grouped in the index file, so we 
                    // don't have to check whether bus number is already 
                    // in the list -- just add a new entry once the number 
                    // changes.
                    if (busInfo == null || !busNumber.equals(busInfo.getNumber())) {
                        busInfo = new BusInfo(busNumber);
                        buses.add(busInfo);
                    }
                    ScheduleInfo schInfo = 
                            new ScheduleInfo(direction, schType, scheduleId); 
                    busInfo.addScheduleInfo(schInfo);
                    scheduleInfos.put(Integer.valueOf(scheduleId), schInfo);
                    loadSchedule(scheduleDataFileName, schInfo);
                    scheduleId++;
                }
            } finally {
                dataIn.close();
            }
        } finally {
            if (rawIn != null) {
                rawIn.close();
            }
        }
    }

    private void loadSchedule(String fileName, ScheduleInfo scheduleInfo) 
            throws IOException {
        Schedule schedule = new Schedule(scheduleInfo);
        schedule.loadFromAsset(context, SCHEDULE_PATH.concat(fileName));
        schedules.put(Integer.valueOf(scheduleInfo.getScheduleId()), schedule);
    }

    public void debugPrintBuses() {
        for (BusInfo info: buses) {
            Log.d(TAG, info.toString());
        }
    }
    
    /**
     * Returns info about all available buses (numbers, directions)
     * @return 
     */
    public List<BusInfo> getBuses() {
        return buses;
    }
    
    /**
     * Returns schedule by its ID
     * @param bus
     * @return
     */
    public Schedule getSchedule(int scheduleId) {
        return schedules.get(Integer.valueOf(scheduleId));
    }

    /**
     * Returns the info associated with the given bus number.
     * If bus number not found, returns null.
     * @param busNumber
     * @return
     */
    public BusInfo getBusInfo(String busNumber) {
        BusInfo result = null;
        for (BusInfo busInfo: buses) {
            if (busNumber.equals(busInfo.getNumber())) {
                result = busInfo;
                break;
            }
        }
        
        if (result == null) {
            Log.wtf(TAG, "getBusInfo returns null for bus '" + busNumber + "'");
        }
        return result;
    }
}
