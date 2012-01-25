package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

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
	private static final int UNKNOWN_BUS_DRAWABLE_ID = R.drawable.bus_unknown;
    
	private String busNumber;
    private List<ScheduleInfo> scheduleInfos;
    
    private static boolean resourcesLoaded = false;
    private static HashMap<String, Integer> mainColors;
    private static HashMap<String, Integer> auxColors;
    private static HashMap<String, Drawable> drawables;
    
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
    	if (!resourcesLoaded) {
    		loadResources();
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
    	if (!resourcesLoaded) {
    		loadResources();
    	}
    	int result = DEFAULT_AUX_COLOR;
    	if (auxColors.containsKey(busNumber)) {
    		result = auxColors.get(busNumber); 
    	}
    	return result;
    }

    /**
     * Return drawable with bus number.
     * @return
     */
    public Drawable getDrawable() {
    	if (!resourcesLoaded) {
    		loadResources();
    	}
    	Drawable result;
    	if (drawables.containsKey(busNumber)) {
    		result = drawables.get(busNumber);
    	} else {
    		result = BusApplication.resources.getDrawable(
    				UNKNOWN_BUS_DRAWABLE_ID);
    	}
    	return result;
    }
    
    /**
     * Loads bus colors from resources.
     */
	protected static void loadResources() {
		Resources res = BusApplication.resources;
		
        String[] busNumbersStr = res.getStringArray(R.array.busNumbers);
        TypedArray bgColorsStr = res.obtainTypedArray(R.array.busBackgroundColors);
        TypedArray fgColorsStr = res.obtainTypedArray(R.array.busForegroundColors);
        TypedArray drawableArray = res.obtainTypedArray(R.array.busDrawables);

        mainColors = new HashMap<String, Integer>();
		auxColors = new HashMap<String, Integer>();
		drawables = new HashMap<String, Drawable>();
        for (int i = 0; i < busNumbersStr.length; i++) {
        	String busNumber = busNumbersStr[i];
            mainColors.put(busNumber, 
            		bgColorsStr.getColor(i, DEFAULT_MAIN_COLOR));
            auxColors.put(busNumber, 
            		fgColorsStr.getColor(i, DEFAULT_AUX_COLOR));
            drawables.put(busNumber, drawableArray.getDrawable(i));
        }  
        resourcesLoaded = true;
	}
}