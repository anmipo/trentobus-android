package com.anmipo.android.trentobus.activities;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.anmipo.android.trentobus.db.BusInfo;

public class BusGridAdapter extends BaseAdapter {
    private List<BusInfo> buses;
	private Context context;
	public BusGridAdapter(Context context, List<BusInfo> buses) {
		super();
		this.context = context; 
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
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view = (ImageView) convertView;
		if (convertView == null) {
			view = new ImageView(context);
		}
		BusInfo bus = buses.get(position);
		view.setImageResource(bus.getDrawableResource());
		return view;
	}
}