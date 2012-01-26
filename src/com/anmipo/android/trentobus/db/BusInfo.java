package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;

/**
 * General info about a bus: number, directions.
 * 
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
	private static HashMap<String, Integer> drawableResources;

	public BusInfo(String number) {
		this.busNumber = number;
		// typically, there are 4 directions: forward/return, workday/holiday
		scheduleInfos = new ArrayList<ScheduleInfo>(4);
	}

	private static void initDrawables() {
		drawableResources = new HashMap<String, Integer>();
		drawableResources.put("A", R.drawable.bus_a);
		drawableResources.put("B", R.drawable.bus_b);
		drawableResources.put("C", R.drawable.bus_c);
		drawableResources.put("D", R.drawable.bus_d);
		drawableResources.put("1", R.drawable.bus_1);
		drawableResources.put("2", R.drawable.bus_2);
		drawableResources.put("3", R.drawable.bus_3);
		drawableResources.put("4", R.drawable.bus_4);
		drawableResources.put("5", R.drawable.bus_5);
		drawableResources.put("6", R.drawable.bus_6);
		drawableResources.put("6/", R.drawable.bus_6b);
		drawableResources.put("7", R.drawable.bus_7);
		drawableResources.put("8", R.drawable.bus_8);
		drawableResources.put("9", R.drawable.bus_9);
		drawableResources.put("10", R.drawable.bus_10);
		drawableResources.put("11", R.drawable.bus_11);
		drawableResources.put("12", R.drawable.bus_12);
		drawableResources.put("13", R.drawable.bus_13);
		drawableResources.put("14", R.drawable.bus_14);
		drawableResources.put("15", R.drawable.bus_15);
		drawableResources.put("16", R.drawable.bus_16);
		drawableResources.put("17", R.drawable.bus_17);
		drawableResources.put("NP", R.drawable.bus_np);
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
		for (ScheduleInfo info : scheduleInfos) {
			sb.append(info.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Returns main color associated with the bus (background for the number).
	 * 
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
	 * 
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
	 * Return drawable resource ID for this bus number.
	 * 
	 * @return
	 */
	public int getDrawableResource() {
		if (!resourcesLoaded) {
			loadResources();
		}
		int result;
		if (drawableResources.containsKey(busNumber)) {
			result = drawableResources.get(busNumber).intValue();
		} else {
			result = UNKNOWN_BUS_DRAWABLE_ID;
		}
		return result;
	}

	/**
	 * Loads bus colors from resources.
	 */
	protected static void loadResources() {
		Resources res = BusApplication.resources;
		String[] busNumbersStr = res.getStringArray(R.array.busNumbers);
		TypedArray bgColorsStr = res
				.obtainTypedArray(R.array.busBackgroundColors);
		TypedArray fgColorsStr = res
				.obtainTypedArray(R.array.busForegroundColors);

		mainColors = new HashMap<String, Integer>();
		auxColors = new HashMap<String, Integer>();
		for (int i = 0; i < busNumbersStr.length; i++) {
			String busNumber = busNumbersStr[i];
			mainColors.put(busNumber,
					bgColorsStr.getColor(i, DEFAULT_MAIN_COLOR));
			auxColors
					.put(busNumber, fgColorsStr.getColor(i, DEFAULT_AUX_COLOR));
		}
		initDrawables();
		resourcesLoaded = true;
	}
}