package com.darling.mototaxi.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.darling.mototaxi.R;

public class NotificacionHelper extends ContextWrapper {

    private  static  final String CHANNEL_ID = "com.darling.mototaxi";
    private  static  final String CHANNEL_NAME = "Mototaxi";

    private NotificationManager manager;

    public NotificacionHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            createChannels();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void createChannels(){

        NotificationChannel notificationChannel = new
                NotificationChannel(
                        CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);
    }

    public   NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body, PendingIntent intent, Uri soundUri){
        return  new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car);
    }

    public NotificationCompat.Builder getNotificationOlAPI(String title, String body, PendingIntent intent, Uri soundUri){
        return  new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(intent)
                .setSmallIcon(R.drawable.ic_car);
    }


}
