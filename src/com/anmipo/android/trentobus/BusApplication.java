package com.anmipo.android.trentobus;

import java.io.IOException;

import com.anmipo.android.trentobus.db.ScheduleManager;

import android.app.Application;
import android.widget.Toast;

public class BusApplication extends Application {
    public static ScheduleManager scheduleManager;
    @Override
    public void onCreate() {
        super.onCreate();
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
