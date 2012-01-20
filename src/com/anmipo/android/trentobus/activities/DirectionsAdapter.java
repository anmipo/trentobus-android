package com.anmipo.android.trentobus.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.R.id;
import com.anmipo.android.trentobus.R.layout;
import com.anmipo.android.trentobus.db.BusInfo;
import com.anmipo.android.trentobus.db.ScheduleInfo;

public class DirectionsAdapter extends BaseAdapter {
    private Context context;
    private BusInfo busInfo;
    private LayoutInflater layoutInflater;
    
    public DirectionsAdapter(Context context, BusInfo busInfo) {
        this.context = context;
        this.busInfo = busInfo;
        layoutInflater = LayoutInflater.from(context);
    }
    
    @Override
    public ScheduleInfo getItem(int position) {
        return busInfo.getScheduleInfo(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return busInfo.getScheduleCount();
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
        holder.direction.setText(getItem(position).getName());
        holder.type.setText(getItem(position).getType().toString());
        //TODO show schedule type icon
        return view;
    }
    static class ViewHolder {
        ImageView icon;
        TextView direction;
        TextView type;
    }
}
