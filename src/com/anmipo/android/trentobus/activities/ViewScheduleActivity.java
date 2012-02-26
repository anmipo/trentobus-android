package com.anmipo.android.trentobus.activities;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.Schedule;
import com.anmipo.android.trentobus.db.ScheduleLegend;
import com.anmipo.android.trentobus.db.ScheduleLegend.Adapter;
import com.anmipo.android.trentobus.view.ScheduleView;
import com.anmipo.android.trentobus.view.TimetableView.OnCellClickListener;
import com.anmipo.android.trentobus.view.TimetableView.OnSizeChangedListener;

public class ViewScheduleActivity extends Activity implements OnCellClickListener {
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
        		schedule.getScheduleInfo().busNumber,
        		schedule.getScheduleInfo().route));
        timetable = (ScheduleView) findViewById(R.id.timetable);
        timetable.setSchedule(schedule);
        timetable.setOnCellClickListener(this);
        
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
    	super.onPrepareOptionsMenu(menu);
    	getMenuInflater().inflate(R.menu.menu_timetable, menu);
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_legend:
    		showGeneralLegendDescription();
    		return true;
		default:
			return false;
    	}
    }

    @Override
    public void onCellSingleTap(int col, int row) {
		if (row == -1 && col >= 0) {
			showLegendForColumn(col);
		} else if (col == -1 && row >= 0) {
			showStopNameForRow(row);
		}
    }

    @Override
    public void onCellLongPress(int col, int row) {
    	// TODO: implement long press handling
    	Log.d(TAG, "long press: " + col + ", " + row);
    }
    
    private void showStopNameForRow(int row) {
		Toast.makeText(this, schedule.getStopName(row), 
				Toast.LENGTH_SHORT).show();
	}

	protected void showLegendForColumn(int col) {
    	Adapter adapter = schedule.getLegends()[col]
    			.getDescriptionsAdapter(this);
    	if (adapter.getCount() > 0) {
    		showLegendDialog(adapter);
    	} else {
    		Toast.makeText(this, R.string.freq_nothing_special, 
    				Toast.LENGTH_SHORT).show();
    	}
	}

    protected void showGeneralLegendDescription() {
    	showLegendDialog(new ScheduleLegend.Adapter(this));
	}

    protected void showLegendDialog(ScheduleLegend.Adapter adapter) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.schedule_legend)
			.setAdapter(adapter, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.setIcon(0)
			.show();
    }
    
	public static void show(Context context, int scheduleId) {
        Intent intent = new Intent(context, ViewScheduleActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        context.startActivity(intent);
    }

}
