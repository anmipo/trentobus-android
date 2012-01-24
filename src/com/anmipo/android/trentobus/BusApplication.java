package com.anmipo.android.trentobus;

import java.io.IOException;

import android.app.Application;
import android.content.res.Resources;
import android.widget.Toast;

import com.anmipo.android.trentobus.db.ScheduleManager;

public class BusApplication extends Application {
    public static ScheduleManager scheduleManager;
    public static Resources resources;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        resources = getResources();
        
        scheduleManager = new ScheduleManager(this);
        try {
            scheduleManager.init();
        } catch (IOException e) {
            Toast.makeText(this, 
                    R.string.error_cannot_load_schedule_index, 
                    Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }
}
