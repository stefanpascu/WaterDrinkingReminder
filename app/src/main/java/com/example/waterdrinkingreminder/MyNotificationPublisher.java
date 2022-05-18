package com.example.waterdrinkingreminder;

import android.app.Notification ;
import android.app.NotificationChannel ;
import android.app.NotificationManager ;
import android.app.PendingIntent;
import android.content.BroadcastReceiver ;
import android.content.Context ;
import android.content.Intent ;
import static com.example.waterdrinkingreminder.HomeFragment.NOTIFICATION_CHANNEL_ID ;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class MyNotificationPublisher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"foxandroid")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Water Drinking Reminder")
                .setContentText("Remember to stay hydrated!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        Random rand = new Random();

        // Generate random integers in range 0 to 999
        int id_notif = rand.nextInt(100000);

        notificationManagerCompat.notify(id_notif,builder.build());


    }
}