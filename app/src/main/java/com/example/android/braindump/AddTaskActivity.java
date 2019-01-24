package com.example.android.braindump;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.security.spec.ECField;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {


    final int REQUEST_PERMISSION_CODE = 1009;
    static final int TIME_DIALOG_ID = 1111;
    public static EditText editTextDesc;
    MediaRecorder mediaRecorder;
    String pathSave = "";
    ImageView imageView, reminder;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    public static int hour, min;
    public static String aTime;

    AlarmManager alarmManager;
    Intent intent;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

//        picker.setIs24HourView(true);

        if (!checkPermissionFromDevice())
            requestPermission();


        editTextDesc = findViewById(R.id.editTextDesc);
        imageView = (ImageView) findViewById(R.id.voice_record);
        reminder = (ImageView)findViewById(R.id.reminder);
        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                saveTask();
            }
        });

            imageView.setOnTouchListener(new View.OnTouchListener() {
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
        reminder.setOnClickListener(new View.OnClickListener() {

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

                String amPm;
                if (selectedHour >= 12) {
                    amPm = "PM";
                } else {
                    amPm = "AM";
                }
                aTime = new StringBuilder().append(selectedHour).append(':').append(selectedMinute).append(" ").append(amPm).toString();

                Log.d("aTime",aTime);

                String [] a = aTime.split(":", 2);
                Log.d("hour", a[0]);
                Log.d("minute",a[1]);

                String hour = a[0];
                String minuteValue = a[1];
                int minute = 0;

                if(minuteValue.contains("AM")){
                    minuteValue = minuteValue.replace(" AM","");
                    // Log.d("hour", ab);
                    minute = Integer.parseInt(minuteValue);
                }else if(minuteValue.contains("PM")){
                    minuteValue = minuteValue.replace(" PM","");
                    //Log.d("hour", bb);
                    minute = Integer.parseInt(minuteValue);     
                }
               

                int hourConverter = Integer.parseInt(hour);
                Calendar calender = Calendar.getInstance();
                intent = new Intent(AddTaskActivity.this,AlarmNotificationReciever.class);
                pendingIntent = PendingIntent.getBroadcast(AddTaskActivity.this,0, intent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() + (hourConverter*60 + minute)*60  , pendingIntent);


              /*  Intent intent = new Intent("my.action.string");
                intent.putExtra("extra",editTextDesc.getText().toString() );
                sendBroadcast(intent);*/

            }
        }, hour, min, false);//Yes 24 hour time
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
      //  saveTask();


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

    String formattedDate= dateFormat.format(date);
    private void saveTask() {
        //final String sTask = editTextTask.getText().toString().trim();
        final String sDesc = editTextDesc.getText().toString().trim();
        final String audioDecs = pathSave.toLowerCase();
        final String remindAt = aTime;
        final String savedAt = formattedDate;
//        final String sFinishBy = editTextFinishBy.getText().toString().trim();


        if (sDesc.isEmpty()){
            editTextDesc.setError("Desc required");
            editTextDesc.requestFocus();
            return;
        }


        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                Task task = new Task();
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

}
