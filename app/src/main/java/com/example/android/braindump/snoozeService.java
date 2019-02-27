package com.example.android.braindump;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;

public class snoozeService extends IntentService {
    public snoozeService() {
        super("snoozeService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    public snoozeService(String name) {
        super(name);
    }

    AlarmManager alarmManager;
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Intent snoozeIntent = new Intent(snoozeService.this, AlarmNotificationReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(snoozeService.this, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_alarm.setTime(date);
        cal_now.setTime(date);

       /* cal_alarm.set(Calendar.HOUR_OF_DAY, mHour);
        cal_alarm.set(Calendar.MINUTE, mMin);
        cal_alarm.set(Calendar.SECOND, 0);*/

        long delay = 600000; //10 min in milliseconds
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
    }
}
