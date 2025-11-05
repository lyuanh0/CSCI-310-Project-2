package com.example.chatpet.util;

public class Constants {
    // Shared Preferences Keys
    public static final String PREFS_NAME = "ChatPetPrefs";
    public static final String KEY_CURRENT_USER = "current_user";
    public static final String KEY_CURRENT_PET = "current_pet";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Pet Stats Thresholds
    public static final int MAX_HUNGER = 100;
    public static final int MAX_HAPPINESS = 100;
    public static final int MAX_ENERGY = 100;
    public static final int MAX_HEALTH = 100;

    public static final int FEED_THRESHOLD = 30;
    public static final int SLEEP_THRESHOLD = 20;
    public static final int LEVEL_UP_THRESHOLD = 100;

    // Pet Types
    public static final String PET_TYPE_DOG = "Dog";
    public static final String PET_TYPE_CAT = "Cat";
    public static final String PET_TYPE_BIRD = "Bird";
    public static final String PET_TYPE_FISH = "Fish";

    // Food Items
    public static final String FOOD_KIBBLE = "Kibble";
    public static final String FOOD_TREAT = "Treat";
    public static final String FOOD_BONE = "Bone";
    public static final String FOOD_FISH = "Fish";

    // Notification IDs
    public static final int NOTIF_FEEDING = 1001;
    public static final int NOTIF_PLAYING = 1002;
    public static final int NOTIF_SLEEPING = 1003;

    // Journal Settings
    public static final String JOURNAL_TIME = "00:00"; // Midnight
    public static final int JOURNAL_WORK_REQUEST_ID = 5000;

    // API Settings
    public static final String OPENAI_API_KEY = "YOUR_API_KEY_HERE";
    public static final String OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    // Database Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PETS = "pets";
    public static final String TABLE_JOURNAL = "journal_entries";
    public static final String TABLE_MESSAGES = "messages";
}