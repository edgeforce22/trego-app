package com.example.tregoapp.mechanic.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tregoapp.R;
import com.example.tregoapp.MainActivity;
import com.example.tregoapp.mechanic.model.FCMTokenRequest;
import com.example.tregoapp.mechanic.repository.Repository;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "service_request_channel";

    @Override
    public void onNewToken(String token) {
        Repository repository = new Repository(this);
        FCMTokenRequest request = new FCMTokenRequest(repository.getSavedUserId(), token);
        repository.updateMechanicFcmToken(request, null);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("API_FCM_MESSAGE", remoteMessage.toString());

        if(remoteMessage.getData().size() > 0){

            String serviceId = remoteMessage.getData().get("serviceId");
            String requestStatus = remoteMessage.getData().get("requestStatus");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");

            showNotification(serviceId, requestStatus, title, body);
        }
    }

    private void showNotification(String serviceId, String requestStatus, String title, String body) {

        createChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("serviceId", serviceId);
        intent.putExtra("requestStatus", requestStatus);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "service_request_channel")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        // ✅ Permission check
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManager manager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Service Requests",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0,1000,500,1000});

            channel.setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
            );

            manager.createNotificationChannel(channel);
        }
    }
}