package com.anmipo.android.trentobus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.Schedule;
import com.anmipo.android.trentobus.view.ScheduleView;

public class ViewScheduleActivity extends Activity {
    private static final String EXTRA_SCHEDULE_ID = "schedule";
    private static final String TAG = "Timetable";
	
    private ScheduleView timetable;
    private Schedule schedule;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_timetable);
        int scheduleId = getIntent().getIntExtra(EXTRA_SCHEDULE_ID, -1);
        if (scheduleId < 0) {
            Log.wtf(TAG, "Invalid schedule id");
            throw new RuntimeException("Invalid schedule id");
        }
        schedule = BusApplication.scheduleManager.getSchedule(scheduleId);
        setTitle(getString(R.string.view_schedule_title,
        		schedule.getScheduleInfo().getBusNumber(),
        		schedule.getScheduleInfo().getDirection()));
        timetable = (ScheduleView) findViewById(R.id.timetable);
        timetable.setSchedule(schedule);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_timetable, menu);
		return true;
    }
    
    public static void show(Context context, int scheduleId) {
        Intent intent = new Intent(context, ViewScheduleActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        context.startActivity(intent);
    }
}
