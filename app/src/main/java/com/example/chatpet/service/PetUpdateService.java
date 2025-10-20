package com.example.chatpet.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.example.chatpet.data.model.Pet;
import com.example.chatpet.logic.NotificationManager;
import com.example.chatpet.logic.PetManager;

public class PetUpdateService extends Service {
    private static final long UPDATE_INTERVAL = 60 * 60 * 1000; // 1 hour

    private PetManager petManager;
    private NotificationManager notificationManager;
    private Handler handler;
    private Runnable updateRunnable;

    @Override
    public void onCreate() {
        super.onCreate();

        petManager = PetManager.getInstance();
        notificationManager = new NotificationManager(this);
        handler = new Handler();

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                updatePetStatus();
                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(updateRunnable);
        return START_STICKY;
    }

    private void updatePetStatus() {
        Pet currentPet = petManager.getCurrentPet();

        if (currentPet == null) return;

        // Gradually decrease stats over time
        currentPet.decreaseHappiness(5);
        currentPet.increaseHunger(10);
        currentPet.decreaseEnergy(5);

        // Check if pet needs attention
        if (currentPet.getHunger() > 80) {
            notificationManager.sendFeedingReminder();
        }

        if (currentPet.getEnergy() < 20) {
            notificationManager.sendSleepReminder();
        }

        if (currentPet.getHappiness() < 30) {
            notificationManager.sendPlayReminder();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}