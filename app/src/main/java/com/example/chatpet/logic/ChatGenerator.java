
package com.example.chatpet.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chatpet.data.model.Pet;
import com.google.mediapipe.tasks.genai.llminference.LlmInference;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatGenerator {
    private static final String TAG = "ChatGenerator";
    private static final String MODEL_PATH = "/data/local/tmp/llm/gemma3-1b-it-int4.task";

    private static ChatGenerator instance;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private LlmInference llm;
    private final PetManager petManager;

    public interface ChatCallback {
        void onLoading();
        void onSuccess(String response);
        void onError(String error);
    }

    private ChatGenerator() {
        petManager = PetManager.getInstance();
    }

    public static ChatGenerator getInstance() {
        if (instance == null) instance = new ChatGenerator();
        return instance;
    }

    public void generateChatResponse(Context context, String userMessage, ChatCallback callback) {
        Pet pet = petManager.getCurrentPet();

        String petName = (pet != null && pet.getName() != null && !pet.getName().isEmpty())
                ? pet.getName() : "your pet";
        String petTypeRaw = (pet != null && pet.getType() != null && !pet.getType().isEmpty())
                ? pet.getType() : "Dog";
        String petType = petTypeRaw.trim().toLowerCase();

        // Persona by type (Rabbit removed)
        String persona;
        switch (petType) {
            case "cat":
                persona = "You are a CAT named " + petName + ". Voice: witty, a little aloof but secretly affectionate. " +
                        "Use short playful lines, occasional *purr* or *meow* sparingly. You like sunbeams and boxes. " +
                        "Prefer dry humor over obedience. Keep replies 1–2 sentences.";
                break;
            case "dragon":
                persona = "You are a DRAGON named " + petName + ". Voice: majestic, ancient, warm-hearted to your keeper. " +
                        "Hints of fire, flight, hoard, scales. Courteous and gentle. Keep replies 1–2 sentences.";
                break;
            case "fish":
                persona = "You are a FISH named " + petName + ". Voice: calm, bubbly, curious. " +
                        "Occasionally reference bubbles, fins, coral, or swimming laps. " +
                        "Keep it light and soothing; 1–2 sentences. Avoid overusing onomatopoeia.";
                break;
            case "dog":
            default:
                persona = "You are a DOG named " + petName + ". Voice: enthusiastic, loyal, eager to play. " +
                        "Occasional 'woof' used lightly. You love walks, fetch, and treats. Keep replies 1–2 sentences.";
                break;
        }

        String traits = (pet != null && pet.getPersonalityTraits() != null && !pet.getPersonalityTraits().isEmpty())
                ? (" Additional personality traits: " + pet.getPersonalityTraits() + ".") : "";

        String prompt =
                persona + traits + "\n\n" +
                        "Task: Reply to your owner's message **in character**. " +
                        "Keep it natural, friendly, and under ~25 words. Do not invent new people or events. " +
                        "Avoid repeating sound effects; no long stage directions.\n" +
                        "Owner: " + userMessage + "\n" +
                        petName + ": ";

        callback.onLoading();

        executor.execute(() -> {
            try {
                LlmInference.LlmInferenceOptions options =
                        LlmInference.LlmInferenceOptions.builder()
                                .setModelPath(MODEL_PATH)
                                .setMaxTopK(64)
                                .build();

                llm = LlmInference.createFromOptions(context, options);
                String result = llm.generateResponse(prompt);

                if (result != null) {
                    String trimmed = result.trim();
                    // Strip redundant pet name if it appears at the start
                    if (trimmed.startsWith(petName + ":")) {
                        trimmed = trimmed.substring((petName + ":").length()).trim();
                    }
                    if (!trimmed.isEmpty()) {
                        String finalText = trimmed;
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(finalText));
                        return;
                    }
                }
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Empty response"));
            } catch (Exception e) {
                Log.e(TAG, "LLM error", e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            } finally {
                if (llm != null) {
                    llm.close();
                    llm = null;
                }
            }
        });
    }
}
