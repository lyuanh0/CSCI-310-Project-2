package com.example.chatpet.data.model;

import java.util.Date;

public class Pet {
    private String name;
    private String type;
    private Date creationDate;
    private int age;
    private int level;
    private int health;
    private int hunger;
    private int happiness;
    private int energy;
    private String currentStatus;
    private String personalityTraits;
    private String imageUrl;

    public Pet() {
        this.health = 100;
        this.hunger = 50;
        this.happiness = 50;
        this.energy = 100;
        this.level = 1;
        this.age = 0;
        this.currentStatus = "happy";
        this.creationDate = new Date();
    }

    public Pet(String name, String type) {
        this();
        this.name = name;
        this.type = type;
    }

    // Status update methods
    public void increaseHealth(int amount) {
        this.health = Math.min(100, this.health + amount);
    }

    public void increaseEnergy(int amount) {
        this.energy = Math.min(100, this.energy + amount);
    }

    public void increaseHappiness(int amount) {
        this.happiness = Math.min(100, this.happiness + amount);
    }

    public void increaseHunger(int amount) {
        this.hunger = Math.min(100, this.hunger + amount);
    }

    public void decreaseHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public void decreaseEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    public void decreaseHappiness(int amount) {
        this.happiness = Math.max(0, this.happiness - amount);
    }

    public void decreaseHunger(int amount) {
        this.hunger = Math.max(0, this.hunger - amount);
    }

    public void levelUp() {
        this.level++;
        updatePersonality();
    }

    public void feed(Food food) {
        decreaseHunger(food.getHungerPoints());
        increaseHealth(5);
    }

    public void tuck() {
        this.currentStatus = "sleeping";
    }

    public void wakeUp() {
        this.currentStatus = "awake";
        increaseEnergy(50);
    }

    public void updatePersonality() {
        // Logic to update personality based on level and interactions
        if (level >= 5) {
            personalityTraits = "Playful and energetic";
        } else if (level >= 3) {
            personalityTraits = "Curious and friendly";
        } else {
            personalityTraits = "Shy and timid";
        }
    }

    public void updateStatus() {

    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Date getCreationDate() { return creationDate; }
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = hunger; }

    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) { this.happiness = happiness; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String status) { this.currentStatus = status; }

    public String getPersonalityTraits() { return personalityTraits; }
    public void setPersonalityTraits(String traits) { this.personalityTraits = traits; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String url) { this.imageUrl = url; }
}