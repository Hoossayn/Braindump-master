package com.example.android.braindump;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.android.braindumps.ReminderNotification;
import com.example.android.braindumps.ReminderWithAudio;

import java.io.IOException;
import java.util.List;

import androidx.core.app.NotificationCompat;

public class AlarmNotificationReciever extends BroadcastReceiver {

    public static final String DESC = " com.example.android.braindump.DESC";
    public static final String AUDIO_DESC = " com.example.android.braindump.AUDIO_DESC";
    public static final String ID = " com.example.android.braindump.ID";
    public static String PLAY_AUDIO = "action.play.audio";


    @Override
    public void onReceive(Context context, Intent intent) {


        String desc = intent.getStringExtra(DESC);
        String audio = intent.getStringExtra(AUDIO_DESC);
        intent.setAction(PLAY_AUDIO);


     if (audio.isEmpty()) {
                ReminderNotification.notify(context, desc);
            } else {

                ReminderWithAudio.notify(context, desc, audio);

            }
    }

}
