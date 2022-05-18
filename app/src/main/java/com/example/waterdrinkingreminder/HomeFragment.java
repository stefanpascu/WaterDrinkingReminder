package com.example.waterdrinkingreminder;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waterdrinkingreminder.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.protobuf.StringValue;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseStore;
    String userId;

    Integer cupVolume;
    private ProgressBar progressBar;
    private Button progressButton;
    private TextView nrMl;
    private TextView cupVolumeText;
    private int totalMl = 2256; // the nr of kcal the user has to eat today
    private int mlLeft; // the nr of kcal remaining for the day
    private Button shareButton;
    private Dialog dialog;
    private Button switchCupBtn;
    private Button addReminderBtn;
    MaterialTimePicker timePicker;
    LinearLayout alarm;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Calendar calendar;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    List<String> cupSizesList;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        createNotificationChannel();
        progressBar = view.findViewById(R.id.progressBar);
        progressButton = view.findViewById(R.id.progressBtn);
        nrMl = view.findViewById(R.id.nrMl);
        shareButton =  view.findViewById(R.id.shareButton);
        addReminderBtn = view.findViewById(R.id.addReminderBtn);
        switchCupBtn = view.findViewById(R.id.switchCupBtn);
        cupVolumeText = view.findViewById(R.id.cupVolumeText);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                cupVolume = Integer.parseInt(documentSnapshot.getString("cupVolume"));
                cupVolumeText.setText("current size: " + cupVolume + " ml");
                totalMl = Integer.parseInt(documentSnapshot.getString("intake"));
                mlLeft = Integer.parseInt(documentSnapshot.getString("intakeLeft"));
                loadProgress(0);
            }
        });

        progressBar.setProgress(getPercentage(mlLeft, totalMl));
        progressButton.setOnClickListener(v -> loadProgress(cupVolume));

        // add notification every 5 seconds
//        addReminderBtn.setOnClickListener(v -> scheduleNotification(getNotification( "Proba la 5 secunde" ) , 5000 ));
//        addReminderBtn.setOnClickListener(v -> onReminderBtnClick());
        addReminderBtn.setOnClickListener(v -> showTimePicker());

        switchCupBtn.setOnClickListener(v -> goToSwitchCup());

        alarm = view.findViewById(R.id.layoutList);

        return view;
    }

    private void showTimePicker() {
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Choose a time for your alarm")
                .build();

        timePicker.show(getActivity().getSupportFragmentManager(), "foxandroid");

        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                String string1 = String.valueOf(timePicker.getHour());
                String string2 = String.valueOf(timePicker.getMinute());
                if (timePicker.getHour() == 0) {
                    string1 = "00";
                }
                if (timePicker.getMinute() == 0) {
                    string2 = "00";
                }

                addView(string1 + ":" + string2);
                setAlarm();
            }
        });

    }

    private void cancelAlarm() {
        Intent intent = new Intent(this.getActivity(), MyNotificationPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(this.getActivity(),0,intent,0);
        if (alarmManager == null){
            alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this.getActivity(), "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getActivity(), MyNotificationPublisher.class);
        pendingIntent = PendingIntent.getBroadcast(this.getActivity(),0,intent,0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,pendingIntent);

        Toast.makeText(this.getActivity(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();

    }




    private void addView(String time) {
        View alarmView = getLayoutInflater().inflate(R.layout.row_alarm,null,false);
        TextView timeText = alarmView.findViewById(R.id.time);
        timeText.setText(time.toString());
        Button imageClose = (Button) alarmView.findViewById(R.id.deleteBtn);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(alarmView);
            }
        });

        alarm.addView(alarmView);

    }


    private void removeView(View view){
        cancelAlarm();
        alarm.removeView(view);

    }



    private Notification getNotification (String content) {
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder( this.getActivity(), default_notification_channel_id )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        builder.setContentTitle( "Scheduled Notification" ) ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable.ic_water_glass ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;

        return builder.build() ;
    }

    // changes the number of kcal remaining based on the meal introduced
    private void loadProgress(int intake) {
        mlLeft -= intake;
        int percentage = getPercentage(mlLeft, totalMl);
        if(percentage < 0) {
            percentage = 0;
            mlLeft = 0;
        }
        progressBar.setProgress(percentage);
        nrMl.setText("" + mlLeft + "/" + totalMl + "ml");
        DocumentReference documentReference = firebaseStore.collection("users").document(userId);
        documentReference.update("intakeLeft", Integer.toString(mlLeft));
        if(mlLeft <= 0) {
            shareButton.setVisibility(View.VISIBLE);
        } else {
            shareButton.setVisibility(View.INVISIBLE);
        }

        shareButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I managed to drink the recommended daily amount of "+ (totalMl*1.0)/1000 +" liters of water!");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
        });
    }

    // substracts the last meal from the total nr of kcal and returns the remaining kcal in percentage value!
    private int getPercentage(int mlLeft, int totalMl) {
        return ((mlLeft) * 100) / totalMl;
    }

    public void goToSwitchCup(){
        Intent intent = new Intent(this.getActivity(), SwitchCup.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }

}





