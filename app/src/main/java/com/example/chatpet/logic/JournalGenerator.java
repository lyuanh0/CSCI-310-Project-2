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