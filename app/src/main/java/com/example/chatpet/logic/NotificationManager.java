package com.example.chatpet.logic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.example.chatpet.R;
import com.example.chatpet.ui.MainActivity;

public class NotificationManager {
    private static final String CHANNEL_ID = "ChatPet_Notifications";
    private static final String CHANNEL_NAME = "ChatPet Reminders";
    private static final int NOTIFICATION_ID = 1001;

    private Context context;
    private android.app.NotificationManager systemNotificationManager;

    public NotificationManager(Context context) {
        this.context = context;
        systemNotificationManager = (android.app.NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for pet care reminders");
            systemNotificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // You'll need to add this icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        systemNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendFeedingReminder() {
        sendNotification("Time to Feed!", "Your pet is hungry! 🍖");
    }

    public void sendPlayReminder() {
        sendNotification("Play Time!", "Your pet wants to play with you! 🎾");
    }

    public void sendSleepReminder() {
        sendNotification("Bedtime!", "Your pet is tired and needs rest! 😴");
    }
}