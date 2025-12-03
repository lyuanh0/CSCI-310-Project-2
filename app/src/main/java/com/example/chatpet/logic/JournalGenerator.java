package com.example.chatpet.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.data.model.User;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.service.JournalWorker;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import androidx.lifecycle.ViewModel;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class JournalGenerator extends ViewModel{
    private static final String MODEL_PATH = "/data/local/tmp/llm/gemma3-1b-it-int4.task";
    private static final String TAG = "JournalGenerator";
    private LlmInference llm;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static JournalGenerator instance;
    private JournalRepository journalRepository;
    private JournalEntry journalEntry;
    private ChatManager chatManager;
    private PetManager petManager;
    private User user;

    // Interface to communicate with the UI (JournalActivity)
    public interface LlmCallback {
        void onLoading();
        void onSuccess(String result);
        void onError(String errorMessage);
    }

    private JournalGenerator() {
        journalRepository = JournalRepository.getInstance();
        chatManager = ChatManager.getInstance();
        petManager = PetManager.getInstance();
    }

    public static JournalGenerator getInstance() {
        if (instance == null) {
            instance = new JournalGenerator();
        }
        return instance;
    }

    public void generateDailyEntry(Context context, LocalDate date, LlmCallback callback) {
        Pet pet = petManager.getCurrentPet();
        List<Message> messages = chatManager.getMessages();

        journalEntry = journalRepository.getJournalEntryByDate(date);
        String report = journalEntry.getReport();

        Log.i(TAG, "Generating " + date + "  entry with report: " + report);

        // Run LLM generation
        generateJournalEntry(context, MODEL_PATH, report, new LlmCallback() {
            @Override
            public void onLoading() {
                callback.onLoading();
            }

            @Override
            public void onSuccess(String result) {
                journalEntry.setEntry(result);
                Log.i(TAG, "Journal entry saved for " + date.toString());
                callback.onSuccess(result);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error during LLM generation: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }

    public void generateJournalEntry(Context context, String modelPath, String report, LlmCallback callback) {
        Log.i(TAG, "Starting LLM journal generation for: " + report);
        Pet pet = petManager.getCurrentPet();
        callback.onLoading();

        executor.execute(() -> {
            try {
                // Create configuration options
                LlmInference.LlmInferenceOptions options =
                        LlmInference.LlmInferenceOptions.builder()
                                .setModelPath(modelPath)
                                .setMaxTopK(64)
                                .build();

                // Create the LLM inference engine
                llm = LlmInference.createFromOptions(context, options);
                Log.d(TAG, "LlmInference instance created.");

                // Run the model (blocking)
                Pet currPet = petManager.getCurrentPet();
                user = journalRepository.getUser();

                String prompt = "Write a diary entry from the perspective of the pet " + currPet.getType() +
                        " with the personality of " + currPet.getPersonalityTraits() + ". " +
                        "Your response MUST begin immediately with the diary entry text itself. " +
                        "The first word of your output must be part of the diary entry. " +
                        "Do not include 'Okay', 'Well', ellipses (...), greetings, openings, or any prefaces. " +

                        "Make the entry somewhat short. Use natural diary-style voice. " +
                        "Do NOT mention or add any dates. " +

                        "Do not invent new events, interactions, characters, or details. " +
                        "You must base the entry strictly on the provided interactions. " +
                        "Explicitly state out each interaction. " +
                        "You may describe them vaguely, but do not add new ones. " +

                        "The owner's name is " + user.getUsername() + " with they/them pronouns." +
                        "Here are the interactions: " + report + " " +
                        "Return ONLY the diary entry text. Nothing else.";

                //String result = llm.generateResponse("Write a diary entry (without the date) in the pet " + pet.getType() +"'s perspective with this daily report: " + report);
                Log.i(TAG, "uname: " + user.getUsername());

                Log.i(TAG, "Entry for report: \n" + report);

                String result = llm.generateResponse(prompt);

                if (result != null) {
                    Log.i(TAG, "LLM Result: " + result);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                } else {
                    Log.e(TAG, "LLM result was null");
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("LLM returned no result."));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error generating LLM response: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            } finally {
                if (llm != null) {
                    llm.close();
                    llm = null;
                    Log.d(TAG, "LlmInference instance closed.");
                }
            }
        });
    }

    // Schedule the worker to run at 11:59 PM today
    public void scheduleJournalWork(Context context) {
        LocalDate today = LocalDate.now();
        String key = "last_scheduled_date";

        SharedPreferences prefs = context.getSharedPreferences("journal_prefs", Context.MODE_PRIVATE);
        String lastScheduledDate = prefs.getString(key, "");

        // Prevent re-scheduling multiple times per day
        if (today.toString().equals(lastScheduledDate)) {
            Log.i(TAG, "Journal work already scheduled for " + LocalDate.now());
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = today.atTime(23, 59);
        long delay = Duration.between(now, targetTime).toMillis();

        if (delay <= 0) {
            Log.w(TAG, "It's already past target time today. Skipping schedule.");
            return;
        }

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(JournalWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("daily_journal_work")
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                "daily_journal_work",
                ExistingWorkPolicy.REPLACE,
                workRequest
        );

        // Save today's date so won't schedule multiple times
        prefs.edit().putString(key, today.toString()).apply();

        Log.i(TAG, "Scheduled " + LocalDate.now() + " end-of-day journal generation in " + delay / 1000 / 60 + " minutes.");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
        if (llm != null) {
            llm.close();
            llm = null;
            Log.d(TAG, "LlmInference closed in onCleared.");
        }
    }

}

