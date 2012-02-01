package com.anmipo.android.trentobus.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.BusInfo;
import com.anmipo.android.trentobus.db.ScheduleInfo;

public class DirectionsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ScheduleInfo> directions;
    private LayoutInflater layoutInflater;
    
    public DirectionsAdapter(Context context, BusInfo busInfo) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        
        // sort (a copy of) directions to have workdays first
        directions = new ArrayList<ScheduleInfo>(busInfo.getScheduleInfos());
        Collections.sort(directions, new Comparator<ScheduleInfo>() {
			@Override
			public int compare(ScheduleInfo s1, ScheduleInfo s2) {
				return s1.type.compareTo(s2.type);
			}
		});
    }
    
    @Override
    public ScheduleInfo getItem(int position) {
        return directions.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return directions.size();
    }
    
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_schedule_info, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.direction = (TextView) view.findViewById(R.id.direction);
            holder.type = (TextView) view.findViewById(R.id.type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ScheduleInfo info = getItem(position);
        holder.direction.setText(info.direction);
        holder.type.setText(info.type.nameResourceId);
        //TODO show schedule type icon
        return view;
    }
    static class ViewHolder {
        ImageView icon;
        TextView direction;
        TextView type;
    }
}
