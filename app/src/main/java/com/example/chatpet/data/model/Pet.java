package com.example.chatpet.data.model;

import java.util.Date;
import java.util.Random;

public class Pet {
    private String name;
    private String type;
    private Date creationDate;
    private int age;
    private int level;
    private int health;
    private int hunger; // 0 = starving, 100 = full
    private int happiness;// 0 = sad, 100 = happy
    private int energy; // 0 = tired, 100 = energized
    private String currentStatus;
    private String personalityTraits;
    private String imageUrl;

    // XP System
    private int totalXP;
    private int xpLevel2 = 200;
    private int xpLevel3 = 300;
    //private int maxXP = xpLevel3;
    private int maxXP = 100;  // Level up threshold

    private Random random = new Random();

    public Pet() {
        // this.health = 100;
        this.hunger = 50;
        this.happiness = 50;
        this.energy = 50;
        this.level = 1;
        // this.age = 0;
        this.currentStatus = "awake";
        this.creationDate = new Date();
        this.totalXP = 0;
    }

    public Pet(String name, String type) {
        this();
        this.name = name;
        this.type = type;
        //this.level = 1;
        //this.totalXP = 0;
        //this.maxXP = 10000;
    }

    public void addXP(int xp){
        totalXP += xp;
        checkLevelUp();
    }

    private void checkLevelUp(){
        /*
        if (totalXP >= xpLevel3) {
            level = 3;
            // maxxed out!!!!
        } else if (totalXP >= xpLevel2){
            level = 2;
            // intermediate level
        } else {
            level = 1;
        }
        */
        if (totalXP >= maxXP) {
            level++;
            totalXP = 0; // reset XP after leveling
        }
    }

    public void increaseXP(int amount) {
        this.totalXP = Math.min(maxXP, this.totalXP + amount);
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
        increaseHunger(food.getHungerPoints()); // fills hunger
        increaseHappiness(10);
        decreaseEnergy(10);                     // eating uses energy
        increaseXP(10);                         // gain XP
    }

    public void tuck() {
        this.currentStatus = "sleeping";
        increaseHappiness(10);
        increaseEnergy(20); // regain energy
        increaseXP(10);     // gain XP
    }

    public void wakeUp() {
        this.currentStatus = "awake";
        increaseEnergy(70);
    }

    public void decreaseRandomStats() {
        int hungerDrop = random.nextInt(10) + 1;     // 1–10
        int energyDrop = random.nextInt(10) + 1;     // 1–10
        int happinessDrop = random.nextInt(10) + 1;  // 1–10

        decreaseHunger(hungerDrop);
        decreaseEnergy(energyDrop);
        decreaseHappiness(happinessDrop);
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
    public int getTotalXP(){
        return totalXP;
    }

    // returns the amount of XP needed to get to next level.
    public int getCurrentLevelXP(){
        if (level == 1) {
            return totalXP;
        }
        else if (level == 2){
            return totalXP - 100;
        } else {
            // return totalXP - 300; // this is tricky
            return 300;
        }
    }

    public int getXPToNextLevel(){
        // XP needed for next lvl
        if (level == 1) {
            return xpLevel2;
        } else if (level == 2){
            return xpLevel3 - xpLevel2;
        } else {
//            return 300; // to make the bar full
            return 0; // because you're already maxxed out
        }
    }

    public int getXPProgress(){
        if (level >= 3){
            return 100; // max level
        }
        int current = getCurrentLevelXP();
        int needed = getXPToNextLevel();
        return (int) ((current / (float) needed ) * 100);
    }
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