package com.anmipo.android.trentobus.activities;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.Schedule;
import com.anmipo.android.trentobus.db.ScheduleLegend;
import com.anmipo.android.trentobus.view.ScheduleView;
import com.anmipo.android.trentobus.view.TimetableView.OnSizeChangedListener;

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
        
        // We need to scroll to the forthcoming column, but this requires
        // knowledge of timetable view width, which becomes available too late. 
        // One of the most reasonable ways is a custom OnSizeChanged listener.
        // The solution is from here: http://stackoverflow.com/questions/4888624/android-need-to-use-onsizechanged-for-view-getwidth-height-in-class-extending
        timetable.setOnSizeChangedListener(new OnSizeChangedListener() {
			@Override
			public void onSizeChanged(int width, int height) {
		        // scroll to the nearest forthcoming column
		        int forthcomingCol = 
		        		schedule.getForthcomingDepartureColumn(Calendar.getInstance());
		        timetable.scrollToColumn(forthcomingCol);				
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_timetable, menu);
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_legend:
    		showLegendDescription();
    		return true;
		default:
			return false;
    	}
    }
    
    private void showLegendDescription() {
    	ScheduleLegend.Adapter adapter = new ScheduleLegend.Adapter(this);
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.schedule_legend)
			.setAdapter(adapter, null)
			.setIcon(0)
			.show();
	}

	public static void show(Context context, int scheduleId) {
        Intent intent = new Intent(context, ViewScheduleActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        context.startActivity(intent);
    }
}
