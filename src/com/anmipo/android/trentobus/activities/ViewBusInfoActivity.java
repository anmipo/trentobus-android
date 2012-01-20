package com.anmipo.android.trentobus.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.BusInfo;
import com.anmipo.android.trentobus.db.ScheduleInfo;

public class ViewBusInfoActivity extends Activity implements OnItemClickListener {
    private static final String TAG = "BusInfo";
    public static final String EXTRA_BUS_NUMBER = "busNumber";
    private BusInfo busInfo;
    private ListView scheduleList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_view_bus_info);
        String busNumber = getIntent().getStringExtra(EXTRA_BUS_NUMBER);
        busInfo = BusApplication.scheduleManager.getBusInfo(busNumber);
        
        TextView busNumberText = (TextView) findViewById(R.id.bus_number);
        busNumberText.setText(
        		this.getString(R.string.view_bus_info_header, busNumber));
        
        scheduleList = (ListView) findViewById(R.id.schedule_info);
        DirectionsAdapter adapter = new DirectionsAdapter(this, busInfo);
        scheduleList.setAdapter(adapter);
        scheduleList.setOnItemClickListener(this);
    }
    
    /**
     * Opens ViewBusInfoActivity for the given bus number
     * @param context
     *          calling activity context.
     * @param busNumber
     *          number of the bus to show the info for.
     */
    public static void show(Context context, String busNumber) {
        Intent intent = new Intent(context, ViewBusInfoActivity.class);
        intent.putExtra(EXTRA_BUS_NUMBER, busNumber);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
            int pos, long id) {
        DirectionsAdapter adapter = (DirectionsAdapter)adapterView.getAdapter();
        ScheduleInfo scheduleInfo = adapter.getItem(pos);
        ViewScheduleActivity.show(this, scheduleInfo.getScheduleId());
    }
}
