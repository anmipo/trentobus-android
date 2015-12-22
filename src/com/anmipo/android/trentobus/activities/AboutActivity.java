/*
 * Copyright (c) 2012-2015 Andrei Popleteev.
 * Licensed under the MIT license.
 */
package com.anmipo.android.trentobus.activities;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anmipo.android.trentobus.R;
import com.anmipo.android.trentobus.BusApplication;
import com.anmipo.android.trentobus.db.ScheduleManager;

public class AboutActivity extends Activity implements OnClickListener {
    private static final String APP_MARKET_URL = "market://details?id="; 
    private Button updateButton;
    private Button rateButton;
    private TextView validityText;
    private TextView versionText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_about);
        
        validityText = (TextView) findViewById(R.id.validity);
        versionText = (TextView) findViewById(R.id.version);
        updateButton = (Button) findViewById(R.id.update_button);
        rateButton = (Button) findViewById(R.id.rate_button);
        updateButton.setOnClickListener(this);
        rateButton.setOnClickListener(this);
        
        setValidityText();
        setVersionText();
    }
    
    private void setVersionText() {
        String versionNumber;
        try {
            PackageInfo pinfo = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            versionNumber = pinfo.versionName;
        } catch(NameNotFoundException nnfe) {
            versionNumber = "?";
        }
        versionText.setText(getString(R.string.version_text, versionNumber));        
    }

    private void setValidityText() {
        // format schedule validity string
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getString(R.string.date_format));
        ScheduleManager schMgr = BusApplication.scheduleManager;
        validityText.setText(getString(
                R.string.schedule_validity,
                dateFormat.format(schMgr.getValidFrom()),
                dateFormat.format(schMgr.getValidTo())));        
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.update_button:
            openAppMarket();
            break;
        case R.id.rate_button:
            openAppMarket();
            break;
        }
    }
    
    /**
     * Opens Android Market page of this application.
     */
    private void openAppMarket() {
        Intent intent = new Intent(Intent.ACTION_VIEW, 
                Uri.parse(APP_MARKET_URL + getApplicationInfo().packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(intent);
        } catch(ActivityNotFoundException anfe) {
            Toast.makeText(this, 
                    R.string.market_not_available, 
                    Toast.LENGTH_SHORT).show();
        }
    }
}
