package com.anmipo.android.trentobus.activities;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.BusInfo;

public class BusGridAdapter extends ArrayAdapter<BusInfo> {
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
		ImageView view = (ImageView) convertView;
		if (convertView == null) {
			view = new ImageView(getContext());
		}
		BusInfo bus = buses.get(position);
		view.setImageDrawable(bus.getDrawable());
		view.setId(position);
		return view;
	}
}