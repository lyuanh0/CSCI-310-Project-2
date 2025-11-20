package com.example.chatpet;

import static org.junit.Assert.*;

import com.example.chatpet.data.model.Food;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.logic.PetManager;

import org.junit.Before;
import org.junit.Test;

public class PetLevelUpWhiteBoxTest {
    private Pet pet;

    @Before
    public void setUp() {
        // Create a fresh pet before each test
        pet = new Pet("TestPet", "dog");
        pet.setLevel(1);
        pet.setTotalXP(0);
        // Initialize the journal entry to avoid null pointer

    }

    // WHITE BOX TEST 1: XP Addition Logic
    @Test
    public void testAddXPIncrementsTotalXP() {
        // Given: Pet starts with 0 XP
        assertEquals(0, pet.getTotalXP());

        // When: Add 50 XP
        pet.addXP(50);

        // Then: Total XP should be 50
        assertEquals(50, pet.getTotalXP());
    } // DONE

    // WHITE BOX TEST 2: Level Threshold Algorithm--just under the limit
    @Test
    public void testLevelUpThresholdLogic() {
        // Given: Pet at level 1 with 99 XP (just under threshold)
        pet.setTotalXP(99);

        // When: Check if should level up
        pet.tryLevelUp();

        // Then: Should still be level 1
        assertEquals(1, pet.getLevel());
        assertEquals(99, pet.getTotalXP());

        // When: Add 1 more XP (now at 100, meets threshold)
        pet.addXP(1);

        // Then: Should be level 2, XP reset to 0
        assertEquals(2, pet.getLevel());
        assertEquals(0, pet.getTotalXP());
    } // DONE

    // WHITE BOX TEST 3: GetMaxXPForLevel Returns Correct Values
    @Test
    public void testGetMaxXPForLevelReturnsCorrectThresholds() {
        // Given: Pet at level 1
        assertEquals(1, pet.getLevel());

        // Then: Max XP should be 100
        assertEquals(100, pet.getMaxXPForLevel());

        // When: Manually set to level 2
        pet.setLevel(2);

        // Then: Max XP should be 200
        assertEquals(200, pet.getMaxXPForLevel());

        // When: Manually set to level 3
        pet.setLevel(3);

        // Then: Max XP should be 300
        assertEquals(300, pet.getMaxXPForLevel());

        // TODO: DOUBLE CHECK THIS ONE
        // Branch 4: Beyond max (default case)
        pet.setLevel(4);
        assertEquals(300, pet.getMaxXPForLevel());
    } // DONE

    // TEST 4: Level Up Threshold - Boundary Value (over)
    @Test
    public void testLevelUpThresholdOver() {
        // Given: Pet at level 1 with 150 XP
        pet.setTotalXP(150);

        // When: Try to level up
        pet.tryLevelUp();

        // Then: Should be level 2, XP reset to 0
        assertEquals(2, pet.getLevel());
        assertEquals(0, pet.getTotalXP());
    } // DONE

    // WHITE BOX TEST 5: Max Level Cap - Cannot Level Beyond 3
    @Test
    public void testCannotLevelUpBeyondMaxLevel() {
        // Given: Pet at level 3 with enough XP to theoretically level up
        pet.setLevel(3);
        pet.setTotalXP(300);

        // When: Try to level up
        pet.tryLevelUp();

        // Then: Should stay at level 3 (max level cap enforced)
        assertEquals(3, pet.getLevel());
        assertEquals(300, pet.getTotalXP());
    } // DONE

}