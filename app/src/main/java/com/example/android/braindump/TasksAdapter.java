package com.example.android.braindump;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.provider.Settings.System.DATE_FORMAT;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

    private Context mCtx;
    private List<Task> taskList;
    MediaPlayer mediaPlayer;
    String pathSave = "";
    public Integer state = 0;
    MediaRecorder mediaRecorder;
    Context context;


    public TasksAdapter(Context mCtx, List<Task> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
    }

    public TasksAdapter(Context context, String audioPath) {
        this.mCtx = context;
        this.pathSave = audioPath;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_tasks, parent, false);
        
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Task t = taskList.get(position);

        try {
            if (t.getAudioDesc().isEmpty()) {
                holder.textViewDesc.setText(t.getDesc());
                holder.audioDecs.setVisibility(View.GONE);
                holder.reminder.setText(t.getRemindAt());
                holder.savedAt.setText(t.getUpdatedAt());
                holder.playButton.setVisibility(View.GONE);
            } else {
                holder.textViewDesc.setText(t.getDesc());
                holder.audioDecs.setText(t.getAudioDesc());
                holder.reminder.setText(t.getRemindAt());
                holder.savedAt.setText(t.getUpdatedAt());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewDesc, reminder, audioDecs, savedAt;
        ImageView playButton, stopButton;

        public TasksViewHolder(View itemView) {
            super(itemView);

            playButton = itemView.findViewById(R.id.play_button);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            audioDecs = itemView.findViewById(R.id.audioDecs);
            reminder = itemView.findViewById(R.id.reminder_textView);
            savedAt = itemView.findViewById(R.id.textViewFinishBy);
            // playButtonLayout = itemView.findViewById(R.id.playButtonLayout);

            String audioDecsValue = audioDecs.getText().toString();
            //   textViewFinishBy = itemView.findViewById(R.id.textViewFinishBy);

           /* if(audioDecsValue == ""){
                audioDecs.setVisibility(View.GONE);
            }*/
            itemView.setOnClickListener(this);


            playButton.setOnClickListener((v) -> {
                switch (state) {
                    case 0:
                        //Stuff Start
                        Task task = taskList.get(getAdapterPosition());

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(task.getAudioDesc());
                            mediaPlayer.prepare();
                            playButton.setBackgroundResource(R.drawable.stop_black_24dp);
                            mediaPlayer.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        state = 1;
                        // playButton.setBackgroundResource(R.drawable.play_arrow_black_24dp);
                        break;
                    case 1:
                        //Stuff Stop
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            setUpMediaRecorder();
                        }
                        playButton.setBackgroundResource(R.drawable.play_arrow_black_24dp);
                        state = 0;
                        break;
                }
            });

        }

        @Override
        public void onClick(View view) {
            Task task = taskList.get(getAdapterPosition());

            Intent intent = new Intent(mCtx, UpdateTaskActivity.class);
            intent.putExtra("task", task);
            mCtx.startActivity(intent);
        }

    }


    private void setUpMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }


}