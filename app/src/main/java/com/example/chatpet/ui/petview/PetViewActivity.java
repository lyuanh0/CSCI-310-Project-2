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

public class PetViewActivity extends AppCompatActivity {
    private ImageView ivPet;
    private TextView tvPetName;
    private TextView tvPetLevel;
    private TextView tvPetStatus;
    private ProgressBar pbHunger;
    private ProgressBar pbHappiness;
    private ProgressBar pbEnergy;
    private ProgressBar pbHealth;
    private TextView tvHungerValue;
    private TextView tvHappinessValue;
    private TextView tvEnergyValue;
    private TextView tvHealthValue;
    private Button btnFeed;
    private Button btnTuckIn;
    private Button btnLevelUp;

    private PetManager petManager;
    private Pet currentPet;
    private FoodMenu foodMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_view);

        petManager = PetManager.getInstance();
        foodMenu = new FoodMenu();

        initializeViews();

        // Check if pet exists, if not show creation dialog
        if (petManager.getCurrentPet() == null) {
            showPetCreationDialog();
        } else {
            currentPet = petManager.getCurrentPet();
            updateUI();
        }

        setupListeners();
    }

    private void initializeViews() {
        ivPet = findViewById(R.id.iv_pet);
        tvPetName = findViewById(R.id.tv_pet_name);
        tvPetLevel = findViewById(R.id.tv_pet_level);
        tvPetStatus = findViewById(R.id.tv_pet_status);
        pbHunger = findViewById(R.id.pb_hunger);
        pbHappiness = findViewById(R.id.pb_happiness);
        pbEnergy = findViewById(R.id.pb_energy);
        pbHealth = findViewById(R.id.pb_health);
        tvHungerValue = findViewById(R.id.tv_hunger_value);
        tvHappinessValue = findViewById(R.id.tv_happiness_value);
        tvEnergyValue = findViewById(R.id.tv_energy_value);
        tvHealthValue = findViewById(R.id.tv_health_value);
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

        String[] petTypes = {"Dog", "Cat", "Bird", "Rabbit"};

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
        if (!petManager.canFeed()) {
            Toast.makeText(this, "Your pet is not hungry right now!", Toast.LENGTH_SHORT).show();
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
            foodNames[i] = food.getName() + " (-" + food.getHungerPoints() + " hunger)";
        }

        builder.setItems(foodNames, (dialog, which) -> {
            Food selectedFood = foodMenu.getMenu().get(which);
            petManager.feedPet(selectedFood);

            Toast.makeText(this, currentPet.getName() + " ate " + selectedFood.getName() + "!",
                    Toast.LENGTH_SHORT).show();

            updateUI();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void handleTuckIn() {
        if (!petManager.canTuckIn()) {
            Toast.makeText(this, "Your pet is not tired yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        petManager.tuckInPet();
        Toast.makeText(this, currentPet.getName() + " is sleeping...", Toast.LENGTH_SHORT).show();
        updateUI();

        // Simulate sleep for 5 seconds (in production, this would be longer)
        btnTuckIn.setEnabled(false);
        btnFeed.setEnabled(false);

        new android.os.Handler().postDelayed(() -> {
            petManager.wakeUpPet();
            Toast.makeText(this, currentPet.getName() + " woke up feeling refreshed!",
                    Toast.LENGTH_SHORT).show();
            updateUI();
            btnTuckIn.setEnabled(true);
            btnFeed.setEnabled(true);
        }, 5000);
    }

    private void handleLevelUp() {
        if (!petManager.canLevelUp()) {
            Toast.makeText(this, "Not enough happiness to level up!", Toast.LENGTH_SHORT).show();
            return;
        }

        petManager.levelUpPet();
        Toast.makeText(this, "Level Up! " + currentPet.getName() + " is now level " +
                currentPet.getLevel() + "!", Toast.LENGTH_LONG).show();
        updateUI();
    }

    private void updateUI() {
        if (currentPet == null) return;

        tvPetName.setText(currentPet.getName());
        tvPetLevel.setText("Level " + currentPet.getLevel());
        tvPetStatus.setText("Status: " + currentPet.getCurrentStatus());

        // Update progress bars
        pbHunger.setProgress(currentPet.getHunger());
        pbHappiness.setProgress(currentPet.getHappiness());
        pbEnergy.setProgress(currentPet.getEnergy());
        pbHealth.setProgress(currentPet.getHealth());

        // Update text values
        tvHungerValue.setText(currentPet.getHunger() + "%");
        tvHappinessValue.setText(currentPet.getHappiness() + "%");
        tvEnergyValue.setText(currentPet.getEnergy() + "%");
        tvHealthValue.setText(currentPet.getHealth() + "%");

        // Update button states
        btnFeed.setEnabled(petManager.canFeed());
        btnTuckIn.setEnabled(petManager.canTuckIn());
        btnLevelUp.setEnabled(petManager.canLevelUp());

        // Update pet image based on type and level
        updatePetImage();
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
        if (currentPet != null) {
            currentPet = petManager.getCurrentPet();
            updateUI();
        }
    }
}