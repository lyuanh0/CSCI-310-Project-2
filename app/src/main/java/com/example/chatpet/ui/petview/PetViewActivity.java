package com.example.chatpet.ui.petview;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatpet.R;
import com.example.chatpet.data.model.Food;
import com.example.chatpet.data.model.FoodMenu;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.logic.PetManager;
import com.example.chatpet.util.ValidationUtils;

import android.os.Handler;
import android.os.CountDownTimer;

import java.util.Random;

public class PetViewActivity extends AppCompatActivity {
    private ImageView ivPet;
    private TextView tvPetName;
    private TextView tvPetLevel;
    private TextView tvPetStatus;
    private ProgressBar pbHunger, pbHappiness, pbEnergy, pbXP;
    private TextView tvHungerValue, tvHappinessValue, tvEnergyValue, tvXPValue;
    private TextView tvHealthValue;
    private Button btnFeed;
    private Button btnTuckIn;
    private Button btnLevelUp;

    private PetManager petManager;
    private Pet currentPet;
    private FoodMenu foodMenu;

    // ===== TEST: 10s stat decay =====
    private Handler statHandler;
    private Runnable statDecayRunnable;
    private static final long STAT_DECAY_INTERVAL_MS = 10_000L; // 10 seconds
    private final Random random = new Random();

    // ===== TEST Tuck-in rules =====
    private static final int HAPPINESS_BOOST_PER_TUCK = 10; // +10%
    private static final int ENERGY_BOOST_PER_TUCK = 10;
    private static final int XP_GAIN_PER_ACTION = 10;
    private static final int MAX_XP = 100;
    private static final int TUCKS_BEFORE_COOLDOWN = 3;
    private static final long COOLDOWN_MS = 3 * 60 * 1000L; // 3 minutes
    private static final long TUCK_ANIMATION_MS = 3_000L; // quick 3s "sleep" sim for testing

    private int tuckInCount = 0;
    private boolean isInCooldown = false;
    private CountDownTimer cooldownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_view);

        petManager = PetManager.getInstance();
        foodMenu = new FoodMenu();
        initializeViews();

        //Check if pet exists, if not show creation dialog
        if (petManager.getCurrentPet() == null) {
            showPetCreationDialog();
        } else {
            currentPet = petManager.getCurrentPet();
            updateUI();
        }

        setupListeners();
        setupStatDecayHandler();
    }

    private void initializeViews() {
        ivPet = findViewById(R.id.iv_pet);
        tvPetName = findViewById(R.id.tv_pet_name);
        tvPetLevel = findViewById(R.id.tv_pet_level);
        tvPetStatus = findViewById(R.id.tv_pet_status);
        pbHunger = findViewById(R.id.pb_hunger);
        pbHappiness = findViewById(R.id.pb_happiness);
        pbEnergy = findViewById(R.id.pb_energy);
        pbXP = findViewById(R.id.pb_xp); // added for xp
        tvHungerValue = findViewById(R.id.tv_hunger_value);
        tvHappinessValue = findViewById(R.id.tv_happiness_value);
        tvEnergyValue = findViewById(R.id.tv_energy_value);
        tvXPValue = findViewById(R.id.tv_xp_value); // added for xp
        btnFeed = findViewById(R.id.btn_feed);
        btnTuckIn = findViewById(R.id.btn_tuck_in);
        btnLevelUp = findViewById(R.id.btn_level_up);
    }

    private void setupListeners() {
        btnFeed.setOnClickListener(v -> handleFeed());
        btnTuckIn.setOnClickListener(v -> handleTuckIn());
        btnLevelUp.setOnClickListener(v -> handleLevelUp());
    }

    private void showPetCreationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Pet");

        // Changed this to have four options
        String[] petTypes = {"Dog", "Cat", "Dragon", "Rabbit"};

        builder.setItems(petTypes, (dialog, which) -> {
            String selectedType = petTypes[which];
            showNameInputDialog(selectedType);
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void showNameInputDialog(String petType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name Your " + petType);

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter pet name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String petName = input.getText().toString().trim();

            String error = ValidationUtils.getPetNameError(petName);
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                showNameInputDialog(petType);
                return;
            }

            currentPet = petManager.createPet(petName, petType);
            Toast.makeText(this, "Welcome, " + petName + "!", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            showPetCreationDialog();
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void handleFeed() {
        /*
        if (!petManager.canFeed()) {
            Toast.makeText(this, "Your pet is not hungry right now!", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (currentPet == null) return;

        if (currentPet.getEnergy() <= 0) {
            Toast.makeText(this, currentPet.getName() + " is too tired to eat! Try tucking in first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPet.getHunger() >= 100) {
            Toast.makeText(this, currentPet.getName() + " is full!", Toast.LENGTH_SHORT).show();
            return;
        }
        showFoodMenu();
    }

    private void showFoodMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Food");

        String[] foodNames = new String[foodMenu.getMenu().size()];
        for (int i = 0; i < foodMenu.getMenu().size(); i++) {
            Food food = foodMenu.getMenu().get(i);
            foodNames[i] = food.getName() + " (+" + food.getHungerPoints() + ")";
        }

        builder.setItems(foodNames, (dialog, which) -> {
            Food selectedFood = foodMenu.getMenu().get(which);
            petManager.feedPet(selectedFood);
            currentPet.increaseHappiness(10);
            //currentPet.increaseXP(10);
            Toast.makeText(this, "Fed " + currentPet.getName() + "! +10 XP", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void handleTuckIn() {
        // TEST: ignore PetManager.canTuckIn() and allow tucking unless in cooldown
        if (isInCooldown) {
            Toast.makeText(this, "Please wait until cooldown ends.", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPet.tuck();
        currentPet.increaseEnergy(ENERGY_BOOST_PER_TUCK);
        currentPet.increaseHappiness(HAPPINESS_BOOST_PER_TUCK);

        currentPet.increaseXP(10);
        clampPetStats();
        updateUI();
        Toast.makeText(this, currentPet.getName() + " is resting and feeling happier!", Toast.LENGTH_SHORT).show();

        // Apply instant “sleep” + +10 happiness, then 3s simulated animation lockout
        //performTuckOnce();

        tuckInCount++;
        if (tuckInCount >= TUCKS_BEFORE_COOLDOWN) {
            startCooldown();
        } else {
            // Short disable to simulate sleep animation
            btnTuckIn.setEnabled(false);
            btnTuckIn.postDelayed(() -> {
                if (!isInCooldown) {
                    btnTuckIn.setEnabled(true);
                    btnTuckIn.setText("Tuck In");
                }
            }, TUCK_ANIMATION_MS);
        }
    }

    private void performTuckOnce() {
        if (currentPet == null) return;

        // Simulate going to sleep instantly
        currentPet.tuck();

        // +10% Happiness (cap at 100)
        currentPet.increaseHappiness(HAPPINESS_BOOST_PER_TUCK);
        clampPetStats();
        updateUI();

        Toast.makeText(this,
                currentPet.getName() + " is happy!!",
                Toast.LENGTH_SHORT).show();

        currentPet.increaseXP(10);
    }

    private void startCooldown() {
        isInCooldown = true;
        btnTuckIn.setEnabled(false);

        //Show countdown on the button
        cooldownTimer = new CountDownTimer(COOLDOWN_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long m = seconds / 60;
                long s = seconds % 60;
                btnTuckIn.setText(String.format("Sleeping… %02d:%02d", m, s));
            }

            @Override
            public void onFinish() {
                isInCooldown = false;
                tuckInCount = 0;
                btnTuckIn.setEnabled(true);
                btnTuckIn.setText("Tuck In");
                Toast.makeText(PetViewActivity.this,
                        "You can tuck in again!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void handleLevelUp() {
        if (currentPet == null) return;

        if (!petManager.canLevelUp()) {
            Toast.makeText(this, "Not enough XP to level up!", Toast.LENGTH_SHORT).show();
            return;
        }

        petManager.levelUpPet();
        Toast.makeText(this, "Level Up! " + currentPet.getName() + " is now level " +
                currentPet.getLevel() + "!", Toast.LENGTH_LONG).show();
        updateUI();
    }
    private void setupStatDecayHandler() {
        statHandler = new Handler();
        statDecayRunnable = new Runnable() {
            @Override
            public void run() {
                currentPet = petManager.getCurrentPet();

                if (currentPet != null) {
                    int hungerDrop = random.nextInt(10) + 1;
                    int happinessDrop = random.nextInt(10) + 1;
                    int energyDrop = random.nextInt(10) + 1;

                    currentPet.decreaseHunger(hungerDrop);
                    currentPet.decreaseHappiness(happinessDrop);
                    currentPet.decreaseEnergy(energyDrop);

                    clampPetStats();
                    petManager.setCurrentPet(currentPet);
                    updateUI();
                }
                statHandler.postDelayed(this, STAT_DECAY_INTERVAL_MS);
            }
        };
    }

    private void updateUI() {
        if (currentPet == null) return;

        tvPetName.setText(currentPet.getName());
        tvPetLevel.setText("Level " + currentPet.getLevel());
        tvPetStatus.setText("Status: " + currentPet.getCurrentStatus());

        pbHunger.setProgress(currentPet.getHunger());
        pbHappiness.setProgress(currentPet.getHappiness());
        pbEnergy.setProgress(currentPet.getEnergy());
        pbXP.setProgress(currentPet.getTotalXP() % 100);

        tvHungerValue.setText(currentPet.getHunger() + "%");
        tvHappinessValue.setText(currentPet.getHappiness() + "%");
        tvEnergyValue.setText(currentPet.getEnergy() + "%");
        tvXPValue.setText((currentPet.getTotalXP() % 100) + "/100");

        btnFeed.setEnabled(currentPet.getEnergy() > 0 && currentPet.getHunger() < 100);
        if (!isInCooldown) {
            btnTuckIn.setEnabled(true);
            btnTuckIn.setText("Tuck In");
        }

        // Update pet image based on type and level
        updatePetImage();
    }

    private void clampPetStats() {
        currentPet.setHunger(Math.max(0, Math.min(100, currentPet.getEnergy())));
        currentPet.setHappiness(Math.max(0, Math.min(100, currentPet.getHappiness())));
        currentPet.setEnergy(Math.max(0, Math.min(100, currentPet.getHunger())));
    }

    private void updatePetImage() {
        // TODO: Load appropriate image based on pet type and level
        // For now, use placeholder
        String petType = currentPet.getType().toLowerCase();
        int level = currentPet.getLevel();

        // Example: ivPet.setImageResource(R.drawable.pet_dog_level_1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if (currentPet != null) {
            currentPet = petManager.getCurrentPet();
            updateUI();
        }*/

        // START TEST MODE STAT DECAY HERE
        if (statHandler != null && statDecayRunnable != null) {
            statHandler.postDelayed(statDecayRunnable, STAT_DECAY_INTERVAL_MS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop TEST MODE stat decay when leaving screen (avoid leaks)
        if (statHandler != null && statDecayRunnable != null) {
            statHandler.removeCallbacks(statDecayRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up cooldown timer if running
        if (cooldownTimer != null) {
            cooldownTimer.cancel();
        }
        if (statHandler != null && statDecayRunnable != null) {
            statHandler.removeCallbacks(statDecayRunnable);
        }
    }
}