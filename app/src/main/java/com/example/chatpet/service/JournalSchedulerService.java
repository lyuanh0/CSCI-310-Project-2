package com.example.chatpet.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.chatpet.logic.JournalGenerator;
import com.example.chatpet.logic.ChatNotificationManager;

import java.util.Date;

public class JournalSchedulerService extends Service {
    private JournalGenerator journalGenerator;
    private ChatNotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        journalGenerator = JournalGenerator.getInstance();
        notificationManager = new ChatNotificationManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Generate journal entry for today
        generateJournalEntry();

        return START_STICKY;
    }

    private void generateJournalEntry() {
        Date today = new Date();
        //journalGenerator.generateDailyEntry(today);

        // Send notification
        notificationManager.sendNotification(
                "New Journal Entry",
                "Your pet wrote a new diary entry today!"
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}