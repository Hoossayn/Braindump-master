package com.example.android.braindump;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import androidx.core.app.NotificationCompat;

public class AlarmNotificationReciever extends BroadcastReceiver {

    private List<Task> taskList;

    String desc;
    @Override
    public void onReceive(Context context, Intent intent) {

        Task task = new Task();
/*
        String action = intent.getAction();

        Log.i("Receiver", "Broadcast received: " + action);
        try {
            if (action.equals("my.action.string")) {
                desc = intent.getExtras().getString("extra");

            }
        }catch (Exception e){}*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("BrainDump")
                .setContentText(task.getDesc())
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("info");

        NotificationManager  notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
