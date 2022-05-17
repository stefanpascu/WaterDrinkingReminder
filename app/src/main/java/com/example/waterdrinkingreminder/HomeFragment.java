package com.example.waterdrinkingreminder;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private Button progressButton;
    private TextView nrKcal;
    private Button addReminderBtn;
    private int maxKal = 2256; // the nr of kcal the user has to eat today
    private int currentKcal; // the nr of kcal remaining for the day
    private Button shareButton;
    private Dialog dialog;
    private Button switchCupBtn;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    List<String> moviesList;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        currentKcal = maxKal;
        progressBar = view.findViewById(R.id.progressBar);
        progressButton = view.findViewById(R.id.progressBtn);
        nrKcal = view.findViewById(R.id.nrMl);
        shareButton =  view.findViewById(R.id.shareButton);
        addReminderBtn = view.findViewById(R.id.addReminderBtn);
        switchCupBtn = view.findViewById(R.id.switchCupBtn);

        nrKcal.setText("" + currentKcal + "/" + maxKal + "ml");
        progressBar.setProgress(100);
        progressButton.setOnClickListener(v -> loadProgress(400));

        addReminderBtn.setOnClickListener(v -> scheduleNotification(getNotification( "Proba la 5 secunde" ) , 5000 ));

        switchCupBtn.setOnClickListener(v -> goToSwitchCup());

        return view;

    }

    private void scheduleNotification (Notification notification , int delay) {
        Intent notificationIntent = new Intent( this.getActivity(), MyNotificationPublisher.class ) ;
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this.getActivity(), 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock.elapsedRealtime () + delay ;
        long timeInterval = 60 * 1_000L;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP , futureInMillis, timeInterval, pendingIntent);
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
    private void loadProgress(int meal) {
        int percentage = substractKal(meal);
        if(percentage < 0) {
            percentage = 0;
            currentKcal = 0;
        }
        progressBar.setProgress(percentage);
        nrKcal.setText("" + currentKcal + "/" + maxKal + "ml");

        if(currentKcal <= 0) {
            shareButton.setVisibility(View.VISIBLE);
        } else {
            shareButton.setVisibility(View.INVISIBLE);
        }

        shareButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! I managed to drink the recommended daily amount of "+ (maxKal*1.0)/1000 +" liters of water!");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

            }
        });
    }

    // substracts the last meal from the total nr of kcal and returns the remaining kcal in percentage value!
    private int substractKal(int meal) {
        currentKcal = currentKcal - meal;
        return ((currentKcal) * 100) / maxKal;
    }

    public void goToSwitchCup(){
        Intent intent = new Intent(this.getActivity(), SwitchCup.class);
        startActivity(intent);
    }

}





