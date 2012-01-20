package com.anmipo.android.trentobus.activities;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.R.array;
import com.anmipo.android.trentobus.R.layout;
import com.anmipo.android.trentobus.R.style;
import com.anmipo.android.trentobus.db.BusInfo;

public class BusGridAdapter extends ArrayAdapter<BusInfo> {
	private static final int ITEM_VERTICAL_PADDING = 20;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF000000;
    private static final int DEFAULT_FOREGROUND_COLOR = 0xFFFFFFFF;
    private List<BusInfo> buses;
	private int[] bgColors;
	private int[] fgColors;
	
	public BusGridAdapter(Context context, List<BusInfo> buses) {
		super(context, R.layout.item_bus_number);
		this.buses = buses;
		initColors();
	}
	
	private void initColors() {
	    Resources res = getContext().getResources();
	    int size = buses.size();
	    
	    //N.B. buses in schedule and in resources might differ, also in count
        String[] busNumbersStr = res.getStringArray(R.array.busNumbers);
        String[] bgColorsStr = res.getStringArray(R.array.busBackgroundColors);
        String[] fgColorsStr = res.getStringArray(R.array.busForegroundColors);
        bgColors = new int[size];
        fgColors = new int[size];
        for (int i = 0; i < size; i++) {
            String busNumber = buses.get(i).getNumber();
            int pos = flatIndexOf(busNumbersStr, busNumber);
            if (pos >= 0) {
                bgColors[i] = Color.parseColor(bgColorsStr[pos]);
                fgColors[i] = Color.parseColor(fgColorsStr[pos]);
            } else {
                bgColors[i] = DEFAULT_BACKGROUND_COLOR;
                fgColors[i] = DEFAULT_FOREGROUND_COLOR;
            }
        }    
	}
	
	//TODO make this right, there should be something in the libraries
    private int flatIndexOf(String[] array, String value) {
        int result = -1;
        for (int i = 0; i < array.length; i++) {
            if (value.equals(array[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    @Override
	public int getCount() {
		return buses.size();
	}
	
	@Override
	public BusInfo getItem(int position) {
		return buses.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) convertView;
		if (convertView == null) {
			view = new TextView(getContext());
			view.setGravity(Gravity.CENTER);
			view.setTextAppearance(getContext(), R.style.busNumberButton);
			view.setPadding(0, ITEM_VERTICAL_PADDING, 0, ITEM_VERTICAL_PADDING);
		}
		view.setBackgroundColor(bgColors[position]);
		view.setTextColor(fgColors[position]);
		view.setText(buses.get(position).getNumber());
		view.setId(position);
		return view;
	}
}