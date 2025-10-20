package com.example.chatpet.service;

import android.content.Context;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkManagerHelper {
    private static final String JOURNAL_WORK_TAG = "journal_generation";

    /**
     * Schedule daily journal generation at midnight
     */
    public static void scheduleDailyJournalGeneration(Context context) {
        // Calculate time until midnight
        long delayInMillis = getMillisUntilMidnight();

        PeriodicWorkRequest journalWork = new PeriodicWorkRequest.Builder(
                JournalWorker.class,
                24, TimeUnit.HOURS // Repeat every 24 hours
        )
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .addTag(JOURNAL_WORK_TAG)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                JOURNAL_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                journalWork
        );
    }

    /**
     * Cancel journal generation work
     */
    public static void cancelJournalGeneration(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(JOURNAL_WORK_TAG);
    }

    private static long getMillisUntilMidnight() {
        Calendar now = Calendar.getInstance();
        Calendar midnight = Calendar.getInstance();
        midnight.add(Calendar.DAY_OF_MONTH, 1);
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        return midnight.getTimeInMillis() - now.getTimeInMillis();
    }
}