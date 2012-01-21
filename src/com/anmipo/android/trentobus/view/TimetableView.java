package com.anmipo.android.trentobus.view;

import com.anmipo.android.trentobus.R;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class TimetableView extends RelativeLayout implements OnScrollListener {
    private static final String TAG = "Timetable";

	// width of the left fixed column with bus stop names
	public static final int FIXED_COLUMN_WIDTH = 150;

	private static final int ROW_HEIGHT = 40;

	// ID value for stop names view component
	private static final int STOP_NAMES_VIEW_ID = 1;
	
	private ListView stopNamesList;
	private ListView timetableList;
	private HorizontalScrollView timetableScroller;

	private boolean leftScrollActive = false;
	private boolean rightScrollActive = false;


	public TimetableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		stopNamesList = new ListView(context);
		timetableScroller = new HorizontalScrollView(context);
		timetableList = new ListView(context);
		
		stopNamesList.setAdapter(new StopNamesAdapter(context));
		stopNamesList.setOnScrollListener(this);
		timetableList.setAdapter(new TimetableAdapter(context));
		timetableList.setOnScrollListener(this);
		
		addAndLayoutChildren();
	}
	
    private void addAndLayoutChildren() {
    	stopNamesList.setId(STOP_NAMES_VIEW_ID);
    	stopNamesList.setVerticalScrollBarEnabled(false);
		LayoutParams stopNamesLayoutParams = new RelativeLayout.LayoutParams(
				FIXED_COLUMN_WIDTH, LayoutParams.MATCH_PARENT);
		addView(stopNamesList, stopNamesLayoutParams);
		
		LayoutParams scrollerLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		scrollerLayoutParams.addRule(RIGHT_OF, STOP_NAMES_VIEW_ID);
		addView(timetableScroller, scrollerLayoutParams);
		
		LayoutParams timetableLayoutParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		timetableScroller.addView(timetableList, timetableLayoutParams);		
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (view == stopNamesList) {
			if (!rightScrollActive) {
				leftScrollActive = (scrollState != SCROLL_STATE_IDLE);
			}			
		} else if (view == timetableList) {
			if (!leftScrollActive) {
				rightScrollActive = (scrollState != SCROLL_STATE_IDLE);
			}
		}
	}

	// TODO: fix de-sync issue when one of the lists is scrolled 
	// while the other is processing a fling.
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view == stopNamesList) {
			if (!rightScrollActive) {
				View v = view.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop();
				timetableList.setSelectionFromTop(firstVisibleItem, top);
				Log.v(TAG, "scrolling left");
			}
		} else if (view == timetableList) {
			if (!leftScrollActive) {
				View v = view.getChildAt(0);
				int top = (v == null) ? 0 : v.getTop();
				stopNamesList.setSelectionFromTop(firstVisibleItem, top);
				Log.v(TAG, "scrolling right");
			}
		}
	}
}
