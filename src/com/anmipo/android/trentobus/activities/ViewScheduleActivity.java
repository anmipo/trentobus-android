package com.anmipo.android.trentobus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.R.color;
import com.anmipo.android.trentobus.R.id;
import com.anmipo.android.trentobus.R.layout;
import com.anmipo.android.trentobus.R.style;
import com.anmipo.android.trentobus.db.Schedule;

public class ViewScheduleActivity extends Activity {
    private static final String EXTRA_SCHEDULE_ID = "schedule";
    private static final String TAG = "Timetable";
    private TableLayout timeTable;
    private TableLayout stopsTable;
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
        
        timeTable = (TableLayout) findViewById(R.id.table);
        stopsTable = (TableLayout) findViewById(R.id.stop_names);
        populateTimetable();
    }
    
    private void populateTimetable() {
        int oddBgColor = getResources().getColor(R.color.tableRowOdd);
        int evenBgColor = getResources().getColor(R.color.tableRowEven);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
        int ROW_HEIGHT = 30;
        int rowCount = schedule.getRowCount();
        int colCount = schedule.getColCount();
        int bgColor;
        TableRow tableRow, stopsRow;
        TextView text, stopText;
        for (int row = 0; row < rowCount; row++) {
            bgColor = (row % 2 == 0) ? evenBgColor : oddBgColor; 
            tableRow = new TableRow(this);
            for (int col = 0; col < colCount; col++) {
                text = new TextView(this);
                text.setText(schedule.getTime(row, col));
                text.setBackgroundColor(bgColor);
                text.setGravity(Gravity.CENTER);
                text.setEms(3);
                text.setHeight(ROW_HEIGHT);
                tableRow.addView(text); 
            }
            timeTable.addView(tableRow);
            
            stopsRow = new TableRow(this);
            stopsRow.setLayoutParams(lp);
            stopText = new TextView(this);
            stopText.setBackgroundColor(bgColor);
            stopText.setText(schedule.getStopName(row));
            stopText.setTextAppearance(this, R.style.stopNameTextView);
            stopText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            stopText.setHeight(ROW_HEIGHT);
            stopsRow.addView(stopText);
            stopsTable.addView(stopsRow);
        }        
    }

    public static void show(Context context, int scheduleId) {
        Intent intent = new Intent(context, ViewScheduleActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        context.startActivity(intent);
    }
}
