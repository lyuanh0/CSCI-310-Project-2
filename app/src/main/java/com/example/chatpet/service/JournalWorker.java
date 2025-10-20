package com.example.chatpet.service;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.chatpet.logic.JournalGenerator;
import com.example.chatpet.logic.NotificationManager;

import java.util.Date;

public class JournalWorker extends Worker {
    private JournalGenerator journalGenerator;
    private NotificationManager notificationManager;

    public JournalWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        journalGenerator = JournalGenerator.getInstance();
        notificationManager = new NotificationManager(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Generate journal entry for today
            Date today = new Date();
            journalGenerator.generateDailyEntry(today);

            // Send notification
            notificationManager.sendNotification(
                    "New Journal Entry",
                    "Your pet wrote a new diary entry today!"
            );

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }
}