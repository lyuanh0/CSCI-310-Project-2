package com.example.chatpet.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.chatpet.logic.JournalGenerator;
import com.example.chatpet.logic.ChatNotificationManager;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JournalWorker extends Worker {
    private static final String TAG = "JournalWorker";
    private final JournalGenerator journalGenerator;
    private final ChatNotificationManager notificationManager;

    public JournalWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        journalGenerator = JournalGenerator.getInstance();
        notificationManager = new ChatNotificationManager(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Running end-of-day journal generation...");

        JournalGenerator generator = JournalGenerator.getInstance();

        generator.generateDailyEntry(getApplicationContext(), LocalDate.now(), new JournalGenerator.LlmCallback() {
            @Override
            public void onLoading() {}

            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "Successfully generated journal entry: " + result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to generate journal entry: " + errorMessage);
            }
        });

        return Result.success();
    }
}