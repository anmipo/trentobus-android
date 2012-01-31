package com.anmipo.android.trentobus.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.anmipo.android.trentobus.R;

public class AboutActivity extends Activity implements OnClickListener {
	private static final String APP_MARKET_URL = "market://details?id="; 
	private Button updateButton;
	private Button rateButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_about);
		updateButton = (Button) findViewById(R.id.update_button);
		rateButton = (Button) findViewById(R.id.rate_button);
		updateButton.setOnClickListener(this);
		rateButton.setOnClickListener(this);
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
