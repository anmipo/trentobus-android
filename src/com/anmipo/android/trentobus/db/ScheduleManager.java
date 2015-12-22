/*
 * Copyright (c) 2012-2015 Andrei Popleteev.
 * Licensed under the MIT license.
 */
package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
    private static final String STOP_INDEX_FILE = "stops.idx";
    private static final String VALIDITY_FILE_NAME = "validity.dat";
    private static final String TAG = "ScheduleManager";
    private Context context;
    private List<BusInfo> buses;
    // maps schedule ID to schedule info 
    private HashMap<Integer, ScheduleInfo> scheduleInfos;
    // maps schedule ID to full schedule (with timetable)
    private HashMap<Integer, Schedule> schedules;
    
    // stop name to associated schedules
    private HashMap<BusStop, List<ScheduleInfo>> stopsIndex;
    // maps schedule file name to schedule info
    private HashMap<String, ScheduleInfo> fileToScheduleInfo;

    private Date validFrom, validTo;
    
    public ScheduleManager(Context context) {
        this.context = context.getApplicationContext();
        buses = new ArrayList<BusInfo>(25);
        scheduleInfos = new HashMap<Integer, ScheduleInfo>();
        schedules = new HashMap<Integer, Schedule>();
        fileToScheduleInfo = new HashMap<String, ScheduleInfo>();
        stopsIndex = null; // to be lazy-initialized
    }
    
    /**
     * Caches schedule data from resources, if necessary.
     * @throws IOException 
     */
    public void init() throws IOException {
        loadRoutesIndex();
        loadValidityDates();
        debugPrintBuses();
    }
    
    
    private void loadValidityDates() throws IOException {
        InputStream rawIn = context.getAssets().open(SCHEDULE_PATH + VALIDITY_FILE_NAME);
        try {
            DataInputStream dataIn = new DataInputStream(rawIn);
            try {
                validFrom = new Date(dataIn.readLong());
                validTo = new Date(dataIn.readLong());
            } finally {
                if (dataIn != null) {
                    dataIn.close();
                }
            }
        } finally {
            if (rawIn != null) {
                rawIn.close();
            }
        }
    }

    /**
     * Loads index of bus stop names and schedules with these stops.
     * @throws IOException
     */
    protected void loadStopsIndex() {
        if (stopsIndex != null) {
            return;
        }
        
        stopsIndex = new HashMap<BusStop, List<ScheduleInfo>>();
        try {
            InputStream rawIn = null;
            try { 
                rawIn = context.getAssets().open(SCHEDULE_PATH + STOP_INDEX_FILE);
                DataInputStream dataIn = new DataInputStream(rawIn);
                try {
                    int numStops = dataIn.readInt();
                    for (int iStop = 0; iStop < numStops; iStop++) {
                        BusStop busStop = BusStop.readFromDataStream(dataIn);
                        int numSchedules = dataIn.readInt();
                        ArrayList<ScheduleInfo> stopSchedules = 
                                new ArrayList<ScheduleInfo>(numSchedules);
                        for (int iSch = 0; iSch < numSchedules; iSch++) {
                            String fileName = dataIn.readUTF();
                            ScheduleInfo schInfo = fileToScheduleInfo.get(fileName); 
                            stopSchedules.add(schInfo);
                        }
                        stopsIndex.put(busStop, 
                                Collections.unmodifiableList(stopSchedules));
                    }
                } finally {
                    if (dataIn != null) {
                        dataIn.close();
                    }
                }
            } finally {
                if (rawIn != null) {
                    rawIn.close();
                }
            }
        } catch (IOException ioe) {
            // This method is a lazy-loader called from typical getters.
            // 1) They should not care about IOException
            // 2) If there's an IOException, the app would crash anyway
            // Therefore we "hide" the IO exception as a runtime one.. 
            throw new RuntimeException("Cannot load stops index", ioe);
        }
    }

    /**
     * Returns a (non-modifiable) collection of all known bus stop names.
     * @return
     */
    public Collection<BusStop> getAllBusStops() {
        if (stopsIndex == null) {
            loadStopsIndex();
        }
        return Collections.unmodifiableCollection(stopsIndex.keySet());
    }
    
    /**
     * Returns a (non-modifiable) list of {@link ScheduleInfo} which include
     * a stop with name indicated by <code>busStop</code>. 
     * If stop name is unknown, returns null.
     * @param busStopName
     * @return
     */
    public List<ScheduleInfo> getScheduleInfosForStopName(String busStopName) {
        if (stopsIndex == null) {
            loadStopsIndex();
        }
        return stopsIndex.get(busStopName);
    }
    
    private void loadRoutesIndex() throws IOException {
        buses.clear();
        scheduleInfos.clear();
        schedules.clear();
        fileToScheduleInfo.clear();
        
        InputStream rawIn = null;
        try {
            rawIn = context.getAssets().open(SCHEDULE_PATH + INDEX_FILE_NAME);
    
            // unique id for each schedule (bus number & route & type)
            int scheduleId = 0;
            DataInputStream dataIn = new DataInputStream(rawIn);
            try {
                BusInfo busInfo = null;
                int itemCount = dataIn.readUnsignedShort();
                for (int i = 0; i < itemCount; i++) {
                    // The order is important, obviously
                    String scheduleDataFileName = dataIn.readUTF();
                    String busNumber = dataIn.readUTF();
                    Direction direction = Direction.readFromStream(dataIn);
                    ScheduleType schType = ScheduleType.parse(
                            dataIn.readUTF());
                    String route = dataIn.readUTF();

                    // Bus numbers are grouped in the index file, so we 
                    // don't have to check whether bus number is already 
                    // in the list -- just add a new entry once the number 
                    // changes.
                    if (busInfo == null || !busNumber.equals(busInfo.getNumber())) {
                        busInfo = new BusInfo(busNumber);
                        buses.add(busInfo);
                    }
                    ScheduleInfo schInfo = 
                            new ScheduleInfo(busNumber, direction, route, 
                                    schType, scheduleDataFileName, scheduleId); 
                    busInfo.addScheduleInfo(schInfo);
                    scheduleInfos.put(Integer.valueOf(scheduleId), schInfo);
                    fileToScheduleInfo.put(scheduleDataFileName, schInfo);
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

    private void loadSchedule(Integer scheduleId) throws IOException {
        ScheduleInfo scheduleInfo = scheduleInfos.get(scheduleId);
        Schedule schedule = new Schedule(scheduleInfo);
        
        String fileName = scheduleInfo.fileName;
        Log.d(TAG, "Loading schedule from " + fileName);
        schedule.loadFromAsset(context, SCHEDULE_PATH.concat(fileName));
        
        //and cache it for future
        schedules.put(scheduleId, schedule);
    }

    public void debugPrintBuses() {
        for (BusInfo info: buses) {
            Log.d(TAG, info.toString());
        }
    }
    
    /**
     * Returns info about all available buses (numbers, routes)
     * @return 
     */
    public List<BusInfo> getBuses() {
        return buses;
    }
    
    /**
     * Returns schedule by its ID
     * @param bus
     * @return
     * @throws  
     */
    public Schedule getSchedule(int scheduleId) {
        Integer id = Integer.valueOf(scheduleId); 
        if (!schedules.containsKey(id)) {
            try {
                loadSchedule(id);
            } catch (IOException ioe) {
                Log.wtf(TAG, "cannot load schedule id: " + id, ioe);
                throw new RuntimeException(ioe);
            }
        }
        return schedules.get(id);
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
    
    /**
     * Returns the starting date of the schedule validity.
     * @return
     */
    public Date getValidFrom() {
        return validFrom;
    }
    /**
     * Returns the ending date of the schedule validity.
     * @return
     */
    public Date getValidTo() {
        return validTo;
    }
    
    /**
     * Checks if the schedule data is up to date.
     * If it is valid, returns zero; 
     * if it is expired, returns positive value;
     * if it is not yet valid, returns negative value.
     * @return
     */
    public int getScheduleValidity() {
        Date now = new Date();
        int result = 0;
        if (now.compareTo(validFrom) < 0) {
            result = -1;
        } else if (now.compareTo(validTo) > 0) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }
}
