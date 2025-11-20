package com.example.chatpet;

import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;

import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.model.Food;

public class PetTest {
    private Pet pet;
    private Food pizza;

    //set up
    @Before
    public void setUp() {
        pet = new Pet("Buddy", "dog");
        //pet.setToday(new JournalEntry());
        pizza = new Food("Pizza", 10);
    }

    //test feeding tests
    @Test
    public void testFeedIncreasesHungerAndXP() {
        int oldHunger = pet.getHunger();
        int oldXP = pet.getTotalXP();

        pet.feed(pizza);

        assertTrue(pet.getHunger() > oldHunger);
        assertTrue(pet.getTotalXP() > oldXP);
    }

    @Test
    public void testCannotFeedWhenHungerIsFull() {
        pet.setHunger(100);
        int oldXP = pet.getTotalXP();
        int oldEnergy = pet.getEnergy();

        //feed
        pet.feed(pizza);

        //Assert
        assertEquals("Hunger should stay at 100 when already full", 100, pet.getHunger());
        assertEquals("XP should not increase when hunger is full", oldXP, pet.getTotalXP());
        assertEquals("Energy should not decrease when feeding fails", oldEnergy, pet.getEnergy());
    }

    @Test
    public void testFeedDoesNotExceedMaxHunger() {
        pet.setHunger(95);
        pet.feed(pizza);
        assertTrue(pet.getHunger() <= 100);
    }

    //test tucking in tests
    @Test
    public void testTuckInIncreasesEnergyAndHappiness() {
        int oldEnergy = pet.getEnergy();
        int oldHappiness = pet.getHappiness();

        pet.tuck();

        assertTrue(pet.getEnergy() > oldEnergy);
        assertTrue(pet.getHappiness() > oldHappiness);
    }

    @Test
    public void testTuckInIncreasesXP() {
        int oldXP = pet.getTotalXP();
        pet.tuck();
        assertTrue(pet.getTotalXP() > oldXP);
    }

    //test stat boundaries
    @Test
    public void testHungerDoesNotGoBelowZero() {
        pet.setHunger(0);
        pet.decreaseHunger(10);
        assertEquals(0, pet.getHunger());
    }

    @Test
    public void testEnergyDoesNotExceed100() {
        pet.setEnergy(95);
        pet.increaseEnergy(10);
        assertEquals(100, pet.getEnergy());
    }

    //test level up logic

    @Test
    public void testLevelUpStopsAtLevel3() {
        pet.setLevel(3);
        pet.setTotalXP(300);
        pet.addXP(50);
        assertEquals(3, pet.getLevel()); // no more increase
    }
}

