package com.example.chatpet.logic;

import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.model.Food;
import com.example.chatpet.data.repository.PetRepository;

public class PetManager {
    private static PetManager instance;
    private PetRepository petRepository;
    private Pet currentPet;

    private static final int LEVEL_UP_THRESHOLD = 100;
    private static final int FEED_THRESHOLD = 30;
    private static final int SLEEP_THRESHOLD = 20;

    private PetManager() {
        petRepository = PetRepository.getInstance();
    }

    public static PetManager getInstance() {
        if (instance == null) {
            instance = new PetManager();
        }
        return instance;
    }

    public Pet createPet(String name, String type) {
        Pet pet = new Pet(name, type);
        petRepository.savePet(pet);
        currentPet = pet;
        return pet;
    }

    public Pet getCurrentPet() {
        return currentPet;
    }

    public void setCurrentPet(Pet pet) {
        this.currentPet = pet;
    }

    public void feedPet(Food food) {
        if (currentPet == null) return;

        currentPet.feed(food);
        checkLevelUp();
        petRepository.updatePet(currentPet);
    }

    public boolean canFeed() {
        return currentPet != null && currentPet.getHunger() > FEED_THRESHOLD;
    }

    public void tuckInPet() {
        if (currentPet == null) return;

        currentPet.tuck();
        petRepository.updatePet(currentPet);
    }

    public boolean canTuckIn() {
        return currentPet != null &&
                currentPet.getEnergy() < SLEEP_THRESHOLD &&
                !"sleeping".equals(currentPet.getCurrentStatus());
    }

    public void wakeUpPet() {
        if (currentPet == null) return;

        currentPet.wakeUp();
        petRepository.updatePet(currentPet);
    }

    public void updateHappiness(int amount) {
        if (currentPet == null) return;

        if (amount > 0) {
            currentPet.increaseHappiness(amount);
        } else {
            currentPet.decreaseHappiness(Math.abs(amount));
        }

        checkLevelUp();
        petRepository.updatePet(currentPet);
    }

    private void checkLevelUp() {
        if (currentPet == null) return;

        // Level up if happiness is at max
        if (currentPet.getHappiness() >= LEVEL_UP_THRESHOLD) {
            currentPet.levelUp();
            currentPet.setHappiness(50); // Reset to 50 after level up
        }
    }

    public boolean canLevelUp() {
        return currentPet != null && currentPet.getHappiness() >= LEVEL_UP_THRESHOLD;
    }

    public void levelUpPet() {
        if (currentPet == null) return;

        currentPet.levelUp();
        currentPet.updatePersonality();
        petRepository.updatePet(currentPet);
    }
}