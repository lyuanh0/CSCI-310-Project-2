package com.example.chatpet.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.data.remote.LLMClient;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.List;

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

    public List<JournalEntry> getAllEntries() {
        return journalRepository.getAllJournalEntries();
    }

    public void generateDailyEntry(Context context, LocalDate date, LlmCallback callback) {
        Pet pet = petManager.getCurrentPet();
        List<Message> messages = chatManager.getMessages();

        journalEntry = journalRepository.getJournalEntryByDate(date);
        String report = journalEntry.getReport();

        Log.i(TAG, "Generating daily entry with report: " + report);

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
                String prompt = "Write a diary entry from the perspective of the pet dog based on today's interactions." +
                        "Do not include any unnecessary explanations or introductions." +
                        "Do not invent extra events or characters that are not mentioned in the report." +
                        "Keep it somewhat short and in the style of a dog's inner thoughts." +
                        "These are the interactions that happened: " + report;
                //String result = llm.generateResponse("Write a diary entry (without the date) in the pet " + pet.getType() +"'s perspective with this daily report: " + report);
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

/*
package com.example.chatpet.logic;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.model.Message;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.data.remote.LLMClient;

import java.util.Date;
import java.util.List;

public class JournalGenerator {
    private static JournalGenerator instance;
    private JournalRepository journalRepository;
    private LLMClient llmClient;
    private ChatManager chatManager;
    private PetManager petManager;

    private JournalGenerator() {
        journalRepository = JournalRepository.getInstance();
        llmClient = new LLMClient();
        chatManager = ChatManager.getInstance();
        petManager = PetManager.getInstance();
    }

    public static JournalGenerator getInstance() {
        if (instance == null) {
            instance = new JournalGenerator();
        }
        return instance;
    }

    public JournalEntry generateDailyEntry(Date date) {
        Pet currentPet = petManager.getCurrentPet();

        // Get conversation history from today
        List<Message> messages = chatManager.getMessages();

        // Generate journal entry using LLM
        String entryText = llmClient.generateJournalEntry(currentPet, messages, date);

        JournalEntry entry = new JournalEntry(date, entryText);
        journalRepository.saveJournalEntry(entry);

        return entry;
    }

    public List<JournalEntry> getAllEntries() {
        return journalRepository.getAllJournalEntries();
    }

    public JournalEntry getEntryByDate(Date date) {
        return journalRepository.getJournalEntryByDate(date);
    }
}
*/