package com.anmipo.android.trentobus.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.anmipo.android.trentobus.R;

/**
 * General info about a bus: number, directions.
 * 
 * @author Andrei Popleteev
 */
public class BusInfo {
	private static final int UNKNOWN_BUS_DRAWABLE_ID = R.drawable.bus_unknown;

	private String busNumber;
	private List<ScheduleInfo> scheduleInfos;

	private static boolean resourcesLoaded = false;
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
		drawableResources.put("FV", R.drawable.bus_fv);
	}

	public String getNumber() {
		return busNumber;
	}

	protected void addScheduleInfo(ScheduleInfo scheduleInfo) {
		scheduleInfos.add(scheduleInfo);
	}

	public List<ScheduleInfo> getScheduleInfos() {
		return Collections.unmodifiableList(scheduleInfos);
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
		initDrawables();
		resourcesLoaded = true;
	}
}