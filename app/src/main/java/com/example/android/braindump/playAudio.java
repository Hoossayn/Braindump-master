package com.example.android.braindump;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.android.braindumps.ReminderWithAudio.PLAY_AUDIO;


public class playAudio extends IntentService {

    static MediaPlayer mediaPlayer;
    public static String AUDIO_PLAYER = "com.example.android.braindump.AUDIO_PLAYER";
    public static final String PLAY_AUDIO = "action.play.audio";


    public playAudio(String name) {
        super(name);
    }

    public playAudio() {
        super("playAudio");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        playSomething(intent);
        Toast.makeText(this, "play button clicked", Toast.LENGTH_SHORT).show();
        Log.d("playAudioIntent", "the debugger reaches here");
    }


    public void playSomething(Intent playIntent) {
        playIntent.setAction(PLAY_AUDIO);
        String audio = playIntent.getStringExtra(AUDIO_PLAYER);
        switch (playIntent.getAction()) {
            case PLAY_AUDIO:
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audio);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

