package com.anmipo.android.trentobus.db;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
    private static final String VALIDITY_FILE_NAME = "validity.dat";
    private static final String TAG = "ScheduleManager";
    private Context context;
    private List<BusInfo> buses;
    private HashMap<Integer, ScheduleInfo> scheduleInfos;
    private HashMap<Integer, Schedule> schedules;
    private Date validFrom, validTo;
    
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
                            new ScheduleInfo(busNumber, direction, schType, 
                            		scheduleDataFileName, scheduleId); 
                    busInfo.addScheduleInfo(schInfo);
                    scheduleInfos.put(Integer.valueOf(scheduleId), schInfo);
//                    loadSchedule(scheduleDataFileName, schInfo);
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
