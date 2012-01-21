package com.anmipo.android.trentobus.view;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;

class TimetableRow extends LinearLayout {
	CharSequence[] items;
	TextView[] textViews;
	
	Context context;
	public TimetableRow(Context context) {
		super(context);
		this.context = context;
		this.setDrawingCacheEnabled(true);
		this.setWillNotDraw(true);
	}
	
	/**
	 * @param newItems
	 *            Cannot be null.
	 */
	public void setItems(CharSequence[] newItems) {
		if ((items == null) || (newItems.length != items.length)) {
			Log.d(TimetableView.TAG, "set items");
			removeAllViews();
			textViews = new TextView[newItems.length];
			for (int i = 0; i < newItems.length; i++) {
				TextView textView = TimetableView.createTextView(context, R.style.timetableTime, false);
				addView(textView, 50, TimetableView.ROW_HEIGHT);
				textViews[i] = textView;
			}
		}
		if (newItems != items) {
			items = newItems;
			for (int i = 0; i < items.length; i++) {
				textViews[i].setText(items[i]);
			}
		}
	}
}