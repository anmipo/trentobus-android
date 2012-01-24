package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Color;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;

/**
 * General info about a bus: number, directions.
 * @author Andrei Popleteev
 */
public class BusInfo {
	// bus colors for those not defined in resources
    private static final int DEFAULT_MAIN_COLOR = Color.BLACK;
	private static final int DEFAULT_AUX_COLOR = Color.WHITE;
    
	private String busNumber;
    private List<ScheduleInfo> scheduleInfos;
    
    private static boolean colorsInitialized = false;
    private static HashMap<String, Integer> mainColors;
    private static HashMap<String, Integer> auxColors;
    
    public BusInfo(String number) {
        this.busNumber = number;
        //typically, there are 4 directions: forward/return, workday/holiday
        scheduleInfos = new ArrayList<ScheduleInfo>(4);
    }
    
    public String getNumber() {
        return busNumber;
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
        sb.append(busNumber);
        sb.append('\n');
        for (ScheduleInfo info: scheduleInfos) {
            sb.append(info.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
    
    /**
     * Returns main color associated with the bus (background for the number).
     * @return
     */
    public int getMainColor() {
    	if (!colorsInitialized) {
    		initColors();
    	}
    	int result = DEFAULT_MAIN_COLOR;
    	if (mainColors.containsKey(busNumber)) {
    		result = mainColors.get(busNumber); 
    	}
    	return result;
    }
    /**
     * Returns auxiliary color associated with the bus (for bus number text). 
     * @return
     */
    public int getAuxColor() {
    	if (!colorsInitialized) {
    		initColors();
    	}
    	int result = DEFAULT_AUX_COLOR;
    	if (auxColors.containsKey(busNumber)) {
    		result = auxColors.get(busNumber); 
    	}
    	return result;
    }

    /**
     * Loads bus colors from resources.
     */
	protected static void initColors() {
		Resources res = BusApplication.resources;
		
        String[] busNumbersStr = res.getStringArray(R.array.busNumbers);
        String[] bgColorsStr = res.getStringArray(R.array.busBackgroundColors);
        String[] fgColorsStr = res.getStringArray(R.array.busForegroundColors);

        mainColors = new HashMap<String, Integer>();
		auxColors = new HashMap<String, Integer>();
        for (int i = 0; i < busNumbersStr.length; i++) {
        	String busNumber = busNumbersStr[i];
            mainColors.put(busNumber, Color.parseColor(bgColorsStr[i]));
            auxColors.put(busNumber, Color.parseColor(fgColorsStr[i]));
        }  
	}
}