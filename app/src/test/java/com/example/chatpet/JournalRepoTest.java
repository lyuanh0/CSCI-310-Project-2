package com.example.chatpet;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.logic.JournalGenerator;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class JournalRepoTest {
    private JournalRepository journalRepo;
    // Mock for dependencies
    @Mock private FirebaseAuth mockAuth;
    @Mock private FirebaseUser mockUser;
    @Mock private FirebaseDatabase mockFirebaseDatabase;
    @Mock private DatabaseReference mockRootRef;
    @Mock private DatabaseReference mockUsersRef;
    @Mock private DatabaseReference mockUidRef;
    @Mock private DatabaseReference mockJournalRef;
    @Mock private Task<Void> mockTask;

    // Mock for log and firebase database
    private MockedStatic<FirebaseDatabase> firebaseStaticMock;
    private MockedStatic<Log> logStaticMock;

    @Before
    public void setUp() {
        // Mock AuthManager
        when(mockUser.getUid()).thenReturn("test_uid");
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        AuthManager.setFirebaseAuth(mockAuth);

        // Mock logs
        logStaticMock = mockStatic(Log.class);
        logStaticMock.when(() -> Log.e(anyString(), anyString())).thenReturn(0);
        logStaticMock.when(() -> Log.i(anyString(), anyString())).thenReturn(0);
        logStaticMock.when(() -> Log.e(anyString(), anyString(), any())).thenReturn(0);

        // Mock FirebaseDatabase call chain
        firebaseStaticMock = mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockFirebaseDatabase);
        when(mockFirebaseDatabase.getReference("users")).thenReturn(mockUsersRef);
        when(mockUsersRef.child("test_uid")).thenReturn(mockUidRef);
        when(mockUidRef.child("journalEntries")).thenReturn(mockJournalRef);

        // Mock database 'write' action
        when(mockJournalRef.setValue(any())).thenReturn(mockTask);

        journalRepo = JournalRepository.getInstance();
        journalRepo.clearAllEntries();
    }

    @After
    public void tearDown() {
        logStaticMock.close();
        firebaseStaticMock.close();
    }


    // Testing save new entries
    @Test
    public void testSaveAndRetrieveEntry() {
        LocalDate today = LocalDate.now();
        JournalEntry entry = new JournalEntry(today.toString(), "Test entry");

        journalRepo.saveJournalEntry(entry);
        List<JournalEntry> entries = journalRepo.getAllJournalEntries();

        assertEquals(1, entries.size());
        assertEquals("Test entry", entries.get(0).getEntry());
        assertEquals(today, LocalDate.parse(entries.get(0).getDate()));

        // Verify call to firebase: 1 save/call
        verify(mockJournalRef, times(1)).setValue(any());
    }

    // Test empty journal
    @Test
    public void testEmptyJournal() {
        JournalEntry entry = journalRepo.getLatestEntry();

        assertNull("Latest entry of empty list", entry);

        // Verify call to firebase: 0 call
        verify(mockJournalRef, times(0)).setValue(any());
    }

    // Test return all entries sorted
    @Test
    public void testGetAllEntriesSorted() {
        JournalEntry entry1 = new JournalEntry("2025-11-13", "Entry 1");
        JournalEntry entry2 = new JournalEntry("2025-11-30", "Entry 2"); // Latest
        JournalEntry entry3 = new JournalEntry("2025-11-14", "Entry 3");
        JournalEntry entry4 = new JournalEntry("2025-10-20", "Entry 4"); // Oldest

        journalRepo.saveJournalEntry(entry1);
        journalRepo.saveJournalEntry(entry2);
        journalRepo.saveJournalEntry(entry3);
        journalRepo.saveJournalEntry(entry4);
        List<JournalEntry> entries = journalRepo.getAllJournalEntries();

        assertEquals(4, entries.size());
        assertEquals("Entry 2", entries.get(0).getEntry());
        assertEquals("Entry 3", entries.get(1).getEntry());
        assertEquals("Entry 1", entries.get(2).getEntry());
        assertEquals("Entry 4", entries.get(3).getEntry());

        // Verify call to firebase: 4 saves/calls
        verify(mockJournalRef, times(4)).setValue(any());
    }

    // Test deleting entry
    @Test
    public void testDeleteEntry() {
        LocalDate dateToDelete = LocalDate.now();
        JournalEntry entry1 = new JournalEntry(dateToDelete.toString(), "Delete");
        JournalEntry entry2 = new JournalEntry(dateToDelete.minusDays(1).toString(), "Keep");

        journalRepo.saveJournalEntry(entry1);
        journalRepo.saveJournalEntry(entry2);
        assertEquals(2, journalRepo.getAllJournalEntries().size());

        journalRepo.deleteJournalEntry(dateToDelete);

        List<JournalEntry> entries = journalRepo.getAllJournalEntries();
        assertEquals(1, entries.size());
        assertEquals("Keep", entries.get(0).getEntry());
        assertNull(journalRepo.getJournalEntryByDate(dateToDelete));

        // Verify call to firebase: 2 (saves) + 1 (delete) = 3 calls
        verify(mockJournalRef, times(3)).setValue(any());
    }

    // Test updating entry and retrieving specific entry
    @Test
    public void testUpdatAndRetrievingEntry() {
        JournalEntry entry1 = new JournalEntry("2025-11-15", "First entry");
        JournalEntry entry2 = new JournalEntry("2025-05-13", "Second entry");
        JournalEntry entry3 = new JournalEntry("2025-02-25", "Third entry");
        JournalEntry entry4 = new JournalEntry("2025-01-25", "Fourth entry");
        JournalEntry update = new JournalEntry("2025-05-13", "Changed entry");

        journalRepo.saveJournalEntry(entry1);
        journalRepo.saveJournalEntry(entry2);
        journalRepo.saveJournalEntry(entry3);
        journalRepo.saveJournalEntry(entry4);

        assertEquals(4, journalRepo.getAllJournalEntries().size());
        assertEquals(entry3, journalRepo.getJournalEntryByDate(LocalDate.parse("2025-02-25")));

        assertEquals("Second entry", journalRepo.getJournalEntryByDate(LocalDate.parse("2025-05-13")).getEntry());
        journalRepo.updateJournalEntry(LocalDate.parse(update.getDate()), update);
        assertEquals(update, journalRepo.getJournalEntryByDate(LocalDate.parse("2025-05-13")));
        assertEquals("Changed entry", journalRepo.getJournalEntryByDate(LocalDate.parse("2025-05-13")).getEntry());

        // Verify call to firebase: 4 (saves) + 1 (update) = 5 calls
        verify(mockJournalRef, times(5)).setValue(any());
    }

}