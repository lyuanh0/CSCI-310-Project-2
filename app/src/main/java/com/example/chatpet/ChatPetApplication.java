package com.example.chatpet;

import android.app.Application;
import com.example.chatpet.service.WorkManagerHelper;

public class ChatPetApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Schedule daily journal generation when app first starts
        WorkManagerHelper.scheduleDailyJournalGeneration(this);
    }
}