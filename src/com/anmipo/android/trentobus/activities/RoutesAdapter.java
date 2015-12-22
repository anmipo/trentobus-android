/*
 * Copyright (c) 2012-2015 Andrei Popleteev.
 * Licensed under the MIT license.
 */
package com.anmipo.android.trentobus.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.BusInfo;
import com.anmipo.android.trentobus.db.ScheduleInfo;

public class RoutesAdapter extends BaseAdapter {
    private ArrayList<ScheduleInfo> routes;
    private LayoutInflater layoutInflater;
    
    public RoutesAdapter(Context context, BusInfo busInfo) {
        layoutInflater = LayoutInflater.from(context);
        
        // sort (a copy of) routes to have workdays first
        routes = new ArrayList<ScheduleInfo>(busInfo.getScheduleInfos());
        Collections.sort(routes, new Comparator<ScheduleInfo>() {
            @Override
            public int compare(ScheduleInfo s1, ScheduleInfo s2) {
                return s1.type.compareTo(s2.type);
            }
        });
    }
    
    @Override
    public ScheduleInfo getItem(int position) {
        return routes.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return routes.size();
    }
    
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_schedule_info, null);
            holder = new ViewHolder();
            holder.route = (TextView) view.findViewById(R.id.direction);
            holder.type = (TextView) view.findViewById(R.id.type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ScheduleInfo info = getItem(position);
        holder.route.setText(info.route);
        holder.type.setText(info.type.nameResourceId);
        return view;
    }
    static class ViewHolder {
        TextView route;
        TextView type;
    }
}
