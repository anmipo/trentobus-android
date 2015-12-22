/*
 * Copyright (c) 2012-2015 Andrei Popleteev.
 * Licensed under the MIT license.
 */
package com.anmipo.android.trentobus.activities;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.anmipo.android.common.EulaChecker;
import com.anmipo.android.common.EulaChecker.EulaListener;
import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.db.BusInfo;

public class BusChoiceActivity extends Activity implements OnItemClickListener {
    protected static final String TAG = "BusChoice";
    
    private GridView busGrid;
    private TextView validityStatusView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_buses);
        
        busGrid = (GridView) findViewById(R.id.buses);
        validityStatusView = (TextView) findViewById(R.id.validity_status);
        
        List<BusInfo> buses = BusApplication.scheduleManager.getBuses();
        BusGridAdapter adapter = new BusGridAdapter(this, buses);
        busGrid.setAdapter(adapter);
        busGrid.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        checkScheduleValidity();
        
        EulaChecker.checkEulaAccepted(this, new EulaListener() {
            @Override
            public void onEulaAccepted(boolean accepted) {
                if (!accepted) {
                    finish();
                }
            }
        });
    }
    
    private void checkScheduleValidity() {
        int validity = BusApplication.scheduleManager.getScheduleValidity();
        
        if (validity < 0) {
            validityStatusView.setText(R.string.schedule_is_not_yet_valid);
            validityStatusView.setVisibility(View.VISIBLE);
        } else if (validity > 0) {
            validityStatusView.setText(R.string.schedule_is_out_of_date);
            validityStatusView.setVisibility(View.VISIBLE);
        } else {
            validityStatusView.setVisibility(View.GONE);            
        }
    }

    @Override
    protected void onPause() {
        EulaChecker.dismiss();
        super.onPause();
    }
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View item, 
            int pos, long id) {
        
        BusInfo busInfo =
            ((BusGridAdapter)adapterView.getAdapter()).getItem(pos);
        Log.d(TAG, "clicked: " + busInfo);
        showBusInfo(busInfo);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void showBusInfo(BusInfo busInfo) {
        ViewBusInfoActivity.show(this, busInfo.getNumber());
    }
}