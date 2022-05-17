package com.example.waterdrinkingreminder;

import android.app.Activity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
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
    private Button addReminderBtn;
    private TextView cupVolumeText;
    private int totalMl = 2256; // the nr of kcal the user has to eat today
    private int mlLeft; // the nr of kcal remaining for the day
    private Button shareButton;
    private Dialog dialog;
    private Button switchCupBtn;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    List<String> cupSizesList;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

}





