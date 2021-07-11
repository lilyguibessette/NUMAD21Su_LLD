package edu.neu.madcourse.numad21su_lld.fcm_server;



import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import edu.neu.madcourse.numad21su_lld.R;
import edu.neu.madcourse.numad21su_lld.ReceivedActivity;

// Reference: Firebase Demo 3 from coursework
// Reference: https://github.com/firebase/quickstart-android

public class FCMServer extends FirebaseMessagingService {
    private static final String TAG = FCMServer.class.getSimpleName();
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNewToken(String newToken) {
        Log.d(TAG, "Refreshed token: " + newToken);
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        // log sending details from the message
        myClassifier(remoteMessage);
        Log.e("msgId", remoteMessage.getMessageId());
        Log.e("senderId", remoteMessage.getSenderId());
    }

    private void myClassifier(RemoteMessage remoteMessage) {
        String identifier = remoteMessage.getFrom();

        // if the identifier information is not null and has a topic, show the notification
        if (identifier != null) {
            if (identifier.contains("topic")) {
                if (remoteMessage.getNotification() != null) {
                    showNotification(remoteMessage.getNotification());
                }

            // if it doesn't but it has a message size greater than 0, also show the notification
            } else {
                if (remoteMessage.getData().size() > 0) {
                    showNotification(remoteMessage.getNotification());
                }
            }
        }
    }

    private void showNotification(RemoteMessage.Notification remoteMessageNotification) {

        Intent intent = new Intent(this, ReceivedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        // Configure the notification channel
        notificationChannel.setDescription(CHANNEL_DESCRIPTION);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        // designing the notification bar with the icon in it
        notification = builder.setContentTitle(remoteMessageNotification.getTitle())
                .setContentText("You got a sticker!")
                .setSmallIcon(R.mipmap.muncha_crunch_logo)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        Integer.parseInt(Objects.requireNonNull(
                                remoteMessageNotification.getBody()))))
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);
    }
}