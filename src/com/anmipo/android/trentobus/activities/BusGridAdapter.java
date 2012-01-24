package com.anmipo.android.trentobus.activities;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.BusInfo;

public class BusGridAdapter extends ArrayAdapter<BusInfo> {
	private static final int ITEM_VERTICAL_PADDING = 20;
    private List<BusInfo> buses;
	
	public BusGridAdapter(Context context, List<BusInfo> buses) {
		super(context, R.layout.item_bus_number);
		this.buses = buses;
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
		BusInfo bus = buses.get(position);
		view.setBackgroundColor(bus.getMainColor());
		view.setTextColor(bus.getAuxColor());
		view.setText(bus.getNumber());
		view.setId(position);
		return view;
	}
}