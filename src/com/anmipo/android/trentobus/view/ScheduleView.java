package com.anmipo.android.trentobus.view;

import com.anmipo.android.trentobus.db.Schedule;

import android.content.Context;
import android.util.AttributeSet;

public class ScheduleView extends TimetableView {

	public ScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setSchedule(Schedule schedule) {
		setData(schedule.getStopNames(), 
				schedule.getLegends(), schedule.getTimes());
	}
}
