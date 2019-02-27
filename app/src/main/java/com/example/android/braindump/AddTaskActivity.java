package com.example.android.braindump;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {


    public static int NOTIFICATION_ID = 10;
    public static String PLAY_AUDIO = "com.example.android.braindump.PLAY_AUDIO";
    private NotificationManager notificationManager;
    public Context context;
    final int REQUEST_PERMISSION_CODE = 1009;
    static final int TIME_DIALOG_ID = 1111;
    public static EditText editTextDesc;
    MediaRecorder mediaRecorder;
    String pathSave = "";
    ImageView recordButton, reminder;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    public static int mHour, mMin;
    public static String aTime;
    FloatingActionButton alarmButton;
    AlarmManager alarmManager;
    Intent intent;
    PendingIntent pendingIntent;
    private Task task = new Task();
    private Calendar calender;
    private int minute;
    private int hourConverter;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        if (!checkPermissionFromDevice())
            requestPermission();


        editTextDesc = findViewById(R.id.editTextDesc);
        recordButton = (ImageView) findViewById(R.id.voice_record);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmButton = findViewById(R.id.fab);


        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                saveTask();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordButton.requestFocus();

            }
        });
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    startRecording();
                    Toast.makeText(AddTaskActivity.this, "recording", Toast.LENGTH_LONG).show();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    mediaRecorder.stop();
                    Toast.makeText(AddTaskActivity.this, "recording stopped", Toast.LENGTH_LONG).show();
                    seTimer();

                }

                return true;
            }
        });
        alarmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                seTimer();


            }
        });



    }

    private void seTimer() {

        timePickerDialog = new TimePickerDialog(AddTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                mHour = selectedHour;
                mMin = selectedMinute;

                String amPm;
                if (selectedHour >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                aTime = new StringBuilder().append(selectedHour).append(':').append(selectedMinute).append(" ").append(amPm).toString();



                String[] a = aTime.split(":", 2);


                String hour = a[0];
                String minuteValue = a[1];
                minute = 0;

                if (minuteValue.contains("AM")) {
                    minuteValue = minuteValue.replace(" AM", "");

                    minute = Integer.parseInt(minuteValue);
                } else if (minuteValue.contains("PM")) {
                    minuteValue = minuteValue.replace(" PM", "");

                    minute = Integer.parseInt(minuteValue);
                }


                hourConverter = Integer.parseInt(hour);
                calender = Calendar.getInstance();

            }
        }, mHour, mMin, false);//Yes 24 hour time
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();


    }

    private void startRecording() {
        if (checkPermissionFromDevice()) {

            pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                    UUID.randomUUID().toString() + "audio_record.3gp";
            setUpMediaRecorder();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

           /* btnPlayRecorded.setEnabled(false);
            btnStopRecorded.setEnabled(false);*/

        } else {
            requestPermission();
        }
    }

    private void setUpMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }

    }

    private boolean checkPermissionFromDevice() {

        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result ==
                PackageManager.PERMISSION_GRANTED;
    }

    Date date = new Date();

    String strDateFormat = "hh:mm a";

    DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

    String formattedDate = dateFormat.format(date);

    private void saveTask() {
        //final String sTask = editTextTask.getText().toString().trim();
        final String sDesc = editTextDesc.getText().toString().trim();
        final String audioDecs = pathSave.toLowerCase();
        final String remindAt = aTime;
        final String savedAt = formattedDate;
//        final String sFinishBy = editTextFinishBy.getText().toString().trim();


        if (sDesc.isEmpty()) {
            editTextDesc.setError("Field can't be empty");
            editTextDesc.requestFocus();
            return;
        }

        if (remindAt == null) {
            Toast.makeText(this, "Click the alarm icon to set alarm", Toast.LENGTH_SHORT).show();
            return;
        }

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                task = new Task();
                // if(!sDesc.isEmpty()) {
                task.setDesc(sDesc);
                //}else {
                task.setAudioDesc(audioDecs);
                task.setRemindAt(remindAt);
                task.setUpdatedAt(savedAt);
                //}

                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .insert(task);
                return null;
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


                intent = new Intent(AddTaskActivity.this, AlarmNotificationReciever.class);
                intent.putExtra(AlarmNotificationReciever.DESC, sDesc);
                intent.putExtra(AlarmNotificationReciever.AUDIO_DESC, task.getAudioDesc());
                intent.putExtra(playAudio.AUDIO_PLAYER, task.getAudioDesc());
                intent.putExtra(AlarmNotificationReciever.ID, task.getId());
                pendingIntent = PendingIntent.getBroadcast(AddTaskActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                Date date = new Date();
                Calendar cal_alarm = Calendar.getInstance();
                Calendar cal_now = Calendar.getInstance();

                cal_alarm.setTime(date);
                cal_now.setTime(date);

                cal_alarm.set(Calendar.HOUR_OF_DAY, mHour);
                cal_alarm.set(Calendar.MINUTE, mMin);
                cal_alarm.set(Calendar.SECOND, 0);

                if (cal_alarm.before(cal_now)) {
                    cal_alarm.add(Calendar.DATE, 1);

                }


                try {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }


}
