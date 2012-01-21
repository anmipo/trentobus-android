package com.anmipo.android.trentobus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.Schedule;

public class ViewScheduleActivity extends Activity {
    private static final String EXTRA_SCHEDULE_ID = "schedule";
    private static final String TAG = "Timetable";
    // height of a table row
	private static final int ROW_HEIGHT = 40;
	
    private ListView timetable;
    private ListView stopNames;
    private Schedule schedule;
    private boolean leftScrollActive = false;
    private boolean rightScrollActive = false;
    
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
//        populateTimetable();
        
        stopNames = (ListView) findViewById(R.id.stop_names);
        stopNames.setAdapter(new StopNamesAdapter(this));
        timetable = (ListView) findViewById(R.id.timetable);
        timetable.setAdapter(new TimetableAdapter(this));
        stopNames.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (!rightScrollActive) {
					leftScrollActive = (scrollState != SCROLL_STATE_IDLE);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (leftScrollActive) {
					View v = stopNames.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					timetable.setSelectionFromTop(firstVisibleItem, top);
					Log.v(TAG, "scrolling left");
				}
			}
		});
        timetable.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (!leftScrollActive) {
					rightScrollActive = (scrollState != SCROLL_STATE_IDLE);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (rightScrollActive) {
					View v = timetable.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();
					stopNames.setSelectionFromTop(firstVisibleItem, top);
					Log.v(TAG, "scrolling right");
				}
			}
		});
    }
    
    private class StopNamesAdapter extends BaseAdapter {
    	String[] items;
    	Context context;
		public StopNamesAdapter(Context context) {
			super();
			this.context = context;
			items = context.getResources().getStringArray(R.array.leftColumn);
		}
		@Override
		public String getItem(int position) {
			return items[position];
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) convertView;
			if (view == null) {
				view = createTextView(context, R.style.timetableStopName, true);
			}
			((TextView)view).setText(items[position]);
			return view;
		}
		@Override
		public int getCount() {
			return items.length;
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
    }
    
    private class TimetableAdapter extends BaseAdapter {
    	Context context;
    	String[] items;
    	
    	public TimetableAdapter(Context context) {
    		super();
    		this.context = context;
        	items = context.getResources().getStringArray(R.array.rightColumn);
		}
    	
		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) convertView;
			if (view == null) {
				view = createTextView(context, R.style.timetableTime, false);
			}
			((TextView)view).setText(items[position]);
			return view;
		}
    	
    }
    
    protected static TextView createTextView(Context context, int styleResource, boolean ellipsize) {
		TextView view = new TextView(context);
		view.setTextAppearance(context, styleResource);
		view.setSingleLine();
		if (ellipsize) {
			view.setEllipsize(TruncateAt.END);
		}
		view.setHeight(ROW_HEIGHT);
		view.setGravity(Gravity.CENTER_VERTICAL);
		return view;
    }
/*    private void populateTimetable() {
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
            timetable.addView(tableRow);
            
            stopsRow = new TableRow(this);
            stopsRow.setLayoutParams(lp);
            stopText = new TextView(this);
            stopText.setBackgroundColor(bgColor);
            stopText.setText(schedule.getStopName(row));
            stopText.setTextAppearance(this, R.style.stopNameTextView);
            stopText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            stopText.setHeight(ROW_HEIGHT);
            stopsRow.addView(stopText);
            stopNames.addView(stopsRow);
        }        
    }*/

    public static void show(Context context, int scheduleId) {
        Intent intent = new Intent(context, ViewScheduleActivity.class);
        intent.putExtra(EXTRA_SCHEDULE_ID, scheduleId);
        context.startActivity(intent);
    }
}
