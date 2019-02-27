package com.example.android.braindumps;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.example.android.braindump.AddTaskActivity;
import com.example.android.braindump.AlarmNotificationReciever;
import com.example.android.braindump.MainActivity;
import com.example.android.braindump.R;
import com.example.android.braindump.Task;
import com.example.android.braindump.TasksAdapter;
import com.example.android.braindump.playAudio;
import com.example.android.braindump.snoozeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;

/**
 * Helper class for showing and canceling reminder with audio
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class ReminderWithAudio {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "ReminderWithAudio";
    public static final String PLAY_AUDIO = "action.play.audio";
    private static Intent playAudioIntent;



    public static void notify(final Context context,
                              final String title, final String  audioDesc) {

        final Resources res = context.getResources();
        // This image is used as the notification's large icon (thumbnail).
        // TODO: Remove this if your notification has no relevant thumbnail.
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.example_picture);

        String sound = audioDesc;
        Uri soundUri = Uri.parse(sound);
        Intent noteActivityIntent = new Intent(context, AddTaskActivity.class);
        //noteActivityIntent.putExtra(NoteActivity.NOTE_ID, noteId);


        playAudioIntent = new Intent(context, playAudio.class);
        playAudioIntent.putExtra(playAudio.AUDIO_PLAYER, audioDesc);
        playAudioIntent.setAction(PLAY_AUDIO);

        Intent snoozeIntent = new Intent(context, snoozeService.class);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.ic_stat_reminder)
                .setContentTitle(title)
                .setContentText("Tap to open")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .setLargeIcon(picture)

                // Set ticker text (preview) information for this notification.
                .setTicker("Perform Task")
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                noteActivityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))

                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title)
                        .setBigContentTitle("New task to perform")
                        .setSummaryText("Brain Dump"))

                .setSound(soundUri)
                .addAction(
                        R.drawable.ic_play_arrow_black_24dp,
                        res.getString(R.string.action_play),
                        PendingIntent.getService(
                                context,
                                0,
                                playAudioIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT))

                .addAction(
                        R.mipmap.ic_launcher_round,
                        "Snooze",
                        PendingIntent.getService(
                                context,
                                1,
                                snoozeIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )


                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}
