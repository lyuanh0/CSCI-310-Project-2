package com.example.chatpet.ui.petview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chatpet.R;
import com.example.chatpet.data.model.Food;
import com.example.chatpet.data.model.FoodMenu;
import com.example.chatpet.data.model.JournalEntry;
import com.example.chatpet.data.model.Pet;
import com.example.chatpet.data.repository.JournalRepository;
import com.example.chatpet.logic.AuthManager;
import com.example.chatpet.logic.PetManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Handler;
import android.os.CountDownTimer;

import java.time.LocalDate;
import java.util.Random;

public class PetViewFragment extends Fragment {
    private final JournalRepository journalRepo = JournalRepository.getInstance();
    private ImageView ivPet;
    private TextView tvPetName;
    private TextView tvPetLevel;
    private TextView tvPetStatus;
    private ProgressBar pbHunger, pbHappiness, pbEnergy, pbXP;
    private TextView tvHungerValue, tvHappinessValue, tvEnergyValue, tvXPValue;
    private Button btnFeed;
    private Button btnTuckIn;
    private Button btnLevelUp;

    private PetManager petManager;
    private Pet currentPet;
    private FoodMenu foodMenu;


    // ===== TEST: 10s stat decay =====
    private Handler statHandler;
    private Runnable statDecayRunnable;
    private static final long STAT_DECAY_INTERVAL_MS = 45_000L; // 45 seconds
    private final Random random = new Random();

    // ===== TEST Tuck-in rules =====
    private static final int HAPPINESS_BOOST_PER_TUCK = 10; // +10%
    private static final int ENERGY_BOOST_PER_TUCK = 10;
    private static final int XP_GAIN_PER_ACTION = 10;
    private static final int MAX_XP = 100;
    private static final int TUCKS_BEFORE_COOLDOWN = 1;
    private static final long COOLDOWN_MS =  10 * 1000L; // 1 minutes
    private static final long TUCK_ANIMATION_MS = 3_000L; // quick 3s "sleep" sim for testing

    private int tuckInCount = 0;
    private boolean isInCooldown = false;
    private CountDownTimer cooldownTimer;
    private Button btnBackground;

    private static final String[] DOG_JOKES = {
            "Why do dogs run in circles? Because it's hard to run in squares!",
            "What do you call a dog that doesn't bark? A hush puppy!",
            "What do you call a dog magician? A labracadabrador!",
            "Why did the dog sit in the shade? He was a hot dog!",
            "Why did the dog go to the bank? To make a de-paws-it!",
            "What do you give a dog with a fever? Mustard! It's the best thing for a hot dog.",
            "How do you identify a dogwood tree? By its bark!",
            "What did the dog say when he sat on sandpaper? 'Ruff!'",
            "Where do you find a dog with no legs? Right where you left him!",
            "What do you get a dog for its birthday? Pupcakes!",
            "What did the dog say when it forgot its homework? “My human ate my homework.”",
            "Who is the most famous doggy magician in history? Houndini.",
            "What kind of car does a dog drive? A Fur-rari.",
            "What do dogs get after they graduate from obedience school? Their masters.",
            "What's a dog's favorite type of workout? Pawlates!",
            "What kinds of outdoor markets do dogs despise? Flea markets.",
            "What kind of dog likes taking a bath every day? A shampoo-dle.",
            "What do dogs eat for breakfast? Pooched eggs.",
            "What do you call a dog that sneezes? A-choo-wawa.",
    };

    private static final String[] CAT_JOKES = {
            "What do you call a pile of cats? A meow-ntain!",
            "What's a cat's favorite color? Purrrrple!",
            "Why don't cats play poker? Too many cheetahs!",
            "What do cats eat for breakfast? Mice Krispies!",
            "What do you call a cat who works in business? A purr-fessional!",
            "What's a cat's favorite subject? Hiss-tory.",
            "What do you call a cat who teaches at a university? A purr-fessor.",
            "Why shouldn’t you buy essential oils from a cat? It’s probably a purr-amid scheme.",
            "Why didn’t the cat get promoted to management? He had terrible littership skills.",
            "Why is it annoying to watch TV with cats? They keep pawsing the show.",
            "What did the cat say to the dog? “Meowt of my way!”",
            "What’s a cat’s favorite dessert? A mice cream cone.",
            "What’s a cat’s favorite jacket? A purr coat.",
            "What’s a cat’s least favorite jacket? A fleas-lined coat.",
            "What’s a cat’s favorite comedy show? Caturday Night Live.",
            "What do you call a cat prom? A Fur Ball.",
            "What is a cat’s favorite thing to drink on a summer day? White Claw.",
            "What does a cat order at a bar? A whisker sour.",
            "Why aren’t cats religious? They’re scared of dogma.",
            "Why does the cat do stand-up? He lives for the a-paws.",
    };

    private static final String[] DRAGON_JOKES = {
            "Why don't dragons ever win at poker? They always get caught bluffing fire!",
            "What do you call a dragon who loves to garden? A snap-dragon!",
            "Why did the dragon cross the road? To prove he wasn't chicken!",
            "What's a dragon's favorite snack? Fire crackers!",
            "Why don't dragons use cutlery? They have forked tongues!",
            "What do you call a dragon that's really good at singing? Talon-ted!",
            "What's a hungry dragon's favorite day of the week? Chews-day.",
            "Don't make me unleash the *dragon*!",
            "Why are dragons so good at climbing the corporate ladder? Because they know how to scale it.",
            "I would tell you a very long, bad lizard joke, but I don't want to dragon.",
            "No need to get all *fired* up about it!",
            "Dragons really know how to breathe life into a party.",
            "When dragons gossip, they always add a little fire to the tale.",
            "Be careful with my dragon jokes – they might just roast you!",
            "When life gives you dragons, make a pun to fire up your day.",
            "What would USC's slogan be if they had a dragon mascot? 'Flame on'!",
    };

    private static final String[] FISH_JOKES = {
            "What do you call a fish with no eyes? A fsh!",
            "How do fish get to school? By octobus!",
            "Why are fish so smart? They live in schools!",
            "What do you call a fish that wears a crown? King of the sea!",
            "Do you like my puns? Let minnow if you want to hear more!",
            "You're a fin-tastic human being!",
            "What did the pirate make the whale do? Walk the plank-ton.",
            "What's my favorite musician? Sabrina Carp-enter!",
            "Does something smell fish-y to you?",
            "Without a trout, my puns are unde-bait-ably the best!",
            "You haven't met my friend Frankie, but I bet you've been herring all about him!",
            "What's my favorite movie? Octo-puss in boots!",
            "An octopus is always ready for war because they’re well-armed!",
            "Where does an octopus sing? The choral reef!",
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pet_view, container, false);

        petManager = PetManager.getInstance();
//        foodMenu = new FoodMenu("dog");
//        foodMenu = new FoodMenu("cat");
//        foodMenu = new FoodMenu("dragon");
//        foodMenu = new FoodMenu("fish");
        //foodMenu = new FoodMenu(currentPet.getType());

        initializeViews(view);

        //Check if pet exists, if not show creation dialog
        if (petManager.getCurrentPet() == null) {
            showPetCreationDialog();
        } else {
            currentPet = petManager.getCurrentPet();
            foodMenu = new FoodMenu(currentPet.getType());
            updateUI();
        }

        setupListeners();
        setupStatDecayHandler();

        return view;
    }

    private void initializeViews(View view) {
        ivPet = view.findViewById(R.id.iv_pet);
        tvPetName = view.findViewById(R.id.tv_pet_name);
        tvPetLevel = view.findViewById(R.id.tv_pet_level);
        tvPetStatus = view.findViewById(R.id.tv_pet_status);
        pbHunger = view.findViewById(R.id.pb_hunger);
        pbHappiness = view.findViewById(R.id.pb_happiness);
        pbEnergy = view.findViewById(R.id.pb_energy);
        pbXP = view.findViewById(R.id.pb_xp); // added for xp
        tvHungerValue = view.findViewById(R.id.tv_hunger_value);
        tvHappinessValue = view.findViewById(R.id.tv_happiness_value);
        tvEnergyValue = view.findViewById(R.id.tv_energy_value);
        tvXPValue = view.findViewById(R.id.tv_xp_value); // added for xp
        btnFeed = view.findViewById(R.id.btn_feed);
        btnTuckIn = view.findViewById(R.id.btn_tuck_in);
        btnLevelUp = view.findViewById(R.id.btn_level_up);
        btnBackground = view.findViewById(R.id.btn_background);
    }

    private void setupListeners() {
        btnFeed.setOnClickListener(v -> handleFeed());
        btnTuckIn.setOnClickListener(v -> handleTuckIn());
        btnLevelUp.setOnClickListener(v -> handleLevelUp());
        btnBackground.setOnClickListener(v -> handleBackgroundChange());
        ivPet.setOnClickListener(v-> showRandomJoke());
    }
    private void showRandomJoke() {
        if (currentPet == null) return;

        String[] jokes;
        switch (currentPet.getType().toLowerCase()) {
            case "dog":
                jokes = DOG_JOKES;
                break;

            case "cat":
                jokes = CAT_JOKES;
                break;

            case "dragon":
                jokes = DRAGON_JOKES;
                break;

            case "fish":
                jokes = FISH_JOKES;
                break;

            default:
                jokes = new String[] {"Your pet says is feeling playful!",
                        "Your pet says hi!",
                        "Your pet wonders what you've been up to today!"};
                break;
        }

        int randomIndex = random.nextInt(jokes.length);
        String joke = jokes[randomIndex];

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(currentPet.getName() + " says:");
        builder.setMessage(joke);
        builder.setPositiveButton("Haha!", null);
        builder.show();
    }
    private void handleBackgroundChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Background");

        String[] backgrounds = {"Forest", "Ocean", "Backyard", "Flower Field"};
        int[] drawableIds = {
                R.drawable.verticalforest,
                R.drawable.verticalocean,
                R.drawable.verticalbackyard,
                R.drawable.verticalflowerfield
        };

        builder.setItems(backgrounds, (dialog, which) -> {
            View rootView = getView();
            if (rootView != null) {
                rootView.setBackgroundResource(drawableIds[which]);
                Toast.makeText(requireContext(),
                        "Background changed to " + backgrounds[which],
                        Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showPetCreationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Your Pet");

        // Changed this to have four options
        String[] petTypes = {"Dog", "Cat", "Dragon", "Fish"};

        builder.setItems(petTypes, (dialog, which) -> {
            String selectedType = petTypes[which];
            showNameInputDialog(selectedType);
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void showNameInputDialog(String petType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Name Your " + petType);

        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("Enter pet name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String petName = input.getText().toString().trim();

            String error = getPetNameError(petName);
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                showNameInputDialog(petType);
                return;
            }

            currentPet = petManager.createPet(petName, petType);
            currentPet.setCurrentStatus("awake");
            //add pet to database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(AuthManager.currentUser().getUid());
            ref.child("currentPet").setValue(currentPet).addOnCompleteListener((task -> {
                if(task.isSuccessful()){
                    Toast.makeText(requireContext(), "New pet saved", Toast.LENGTH_SHORT).show();                }
            }));


            Toast.makeText(requireContext(), "Welcome, " + petName + "!", Toast.LENGTH_SHORT).show();
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
        //if (currentPet == null) return;

        if (currentPet == null) {
            Toast.makeText(requireContext(),
                    "No pet found. Please create a pet first.",
                    Toast.LENGTH_SHORT).show();
            showPetCreationDialog();
            return;
        }
        //cannot feed when sleeping
        if ("sleeping".equalsIgnoreCase(currentPet.getCurrentStatus())) {
            Toast.makeText(requireContext(), currentPet.getName() + " is sleeping. Wait until awake!", Toast.LENGTH_SHORT).show();
            return;
        }

        //if (currentPet.getEnergy() <= 0) {
        //    Toast.makeText(requireContext(), currentPet.getName() + " is too tired to eat! Try tucking in first.", Toast.LENGTH_SHORT).show();
        //    return;
        //}
        if (currentPet.getHunger() >= 100) {
            Toast.makeText(requireContext(), currentPet.getName() + " is full!", Toast.LENGTH_SHORT).show();
            return;
        }
        showFoodMenu();
    }

    private void showFoodMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Food");

        String[] foodNames = new String[foodMenu.getMenu().size()];
        for (int i = 0; i < foodMenu.getMenu().size(); i++) {
            Food food = foodMenu.getMenu().get(i);
            foodNames[i] = food.getName() + " (+" + food.getHungerPoints() + ")";
        }

        builder.setItems(foodNames, (dialog, which) -> {
            Food selectedFood = foodMenu.getMenu().get(which);

            JournalEntry today = journalRepo.getJournalEntryByDate(LocalDate.now());
            today.addToReport("Was fed " + selectedFood.getName() + ".");

            petManager.feedPet(selectedFood);
            currentPet.increaseHappiness(10);
            currentPet.increaseXP(10); //increase XP after opening menu?

            //update stats after feeding
            petManager.setCurrentPet(currentPet);
            updateUI();

            Toast.makeText(requireContext(), "Fed " + currentPet.getName() + "! +10 XP", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void handleTuckIn() {
        if (currentPet == null) {
            Toast.makeText(requireContext(),
                    "No pet found. Please create a pet first.",
                    Toast.LENGTH_SHORT).show();
            showPetCreationDialog();
            return;
        }

        if (isInCooldown) {
            Toast.makeText(requireContext(), "Please wait until cooldown ends.", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPet.tuck();
        currentPet.setCurrentStatus("awake");
        currentPet.increaseEnergy(ENERGY_BOOST_PER_TUCK);
        currentPet.increaseHappiness(HAPPINESS_BOOST_PER_TUCK);
        currentPet.increaseXP(XP_GAIN_PER_ACTION);

        clampPetStats();
        startCooldown();
        updateUI();
        Toast.makeText(requireContext(), currentPet.getName() + " is resting and feeling happier!", Toast.LENGTH_SHORT).show();

    }

//    private void performTuckOnce() {
//        if (currentPet == null) return;
//
//        // Simulate going to sleep instantly
//        currentPet.tuck();
//
//        // +10% Happiness (cap at 100)
//        currentPet.increaseHappiness(HAPPINESS_BOOST_PER_TUCK);
//        clampPetStats();
//        updateUI();
//
//        Toast.makeText(requireContext(),
//                currentPet.getName() + " is happy!!",
//                Toast.LENGTH_SHORT).show();
//
//        currentPet.increaseXP(10);
//    }

    private void startCooldown() {
        JournalEntry today = journalRepo.getJournalEntryByDate(LocalDate.now());
        today.addToReport("Was tucked in.");

        isInCooldown = true;
        btnTuckIn.setEnabled(false);
        currentPet.setCurrentStatus("sleeping");//stays sleeping
        currentPet.setIsSleeping(true);
        updateUI();
        Toast.makeText(requireContext(), currentPet.getName() + " fell asleep...", Toast.LENGTH_SHORT).show();

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
                currentPet.setCurrentStatus("awake");//wake up after cooldown
                currentPet.setIsSleeping(false);
                currentPet.wakeUp();
                updateUI();
                //btnTuckIn.setEnabled(true);
                //btnTuckIn.setText("Tuck In");
                Toast.makeText(requireContext(),
                        "You can tuck in again!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void handleLevelUp() {
        if (currentPet == null) return;

        int currentXP = currentPet.getTotalXP();
        int level = currentPet.getLevel();

        int requiredXP;
        if (level == 1) {
            requiredXP = 100;
        } else if (level == 2) {
            requiredXP = 200;
        } else {
            requiredXP = 300;
        }

        if (currentXP < requiredXP) {
            Toast.makeText(requireContext(), "Not enough XP to level up!", Toast.LENGTH_SHORT).show();
            return;
        }

        //if (!petManager.canLevelUp()) {
        //    Toast.makeText(this, "Not enough XP to level up!", Toast.LENGTH_SHORT).show();
        //    return;
        //}

        petManager.levelUpPet();

        currentPet.setTotalXP(0);
        petManager.setCurrentPet(currentPet);

        if (currentPet.getLevel() != 3){
            Toast.makeText(requireContext(), "Level Up! " + currentPet.getName() + " is now level " +
                    currentPet.getLevel() + "!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(requireContext(), "Your pet " + currentPet.getName() + " has reached level 3 (Max lvl)!"
                    , Toast.LENGTH_LONG).show();
        }

        updateUI();
    }
    private void setupStatDecayHandler() {
        statHandler = new Handler();
        statDecayRunnable = new Runnable() {
            @Override
            public void run() {
                currentPet = petManager.getCurrentPet();

                if (currentPet != null) {
                    int hungerDrop = random.nextInt(10) + 1;//1-10 randomly
                    int happinessDrop = random.nextInt(10) + 1;//1-10
                    int energyDrop = random.nextInt(10) + 1;//1-10

                    currentPet.decreaseHunger(hungerDrop);
                    currentPet.decreaseHappiness(happinessDrop);
                    currentPet.decreaseEnergy(energyDrop);

                    //clampPetStats();
                    petManager.setCurrentPet(currentPet);
                    updateUI();
                    //add pet to database
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(AuthManager.currentUser().getUid());
                    ref.child("currentPet").setValue(currentPet).addOnCompleteListener((task -> {
                        if(task.isSuccessful()){
                        }
                    }));
                }
                statHandler.postDelayed(this, STAT_DECAY_INTERVAL_MS);
            }
        };
    }

    private void updateUI() {
        if (currentPet == null) return;

        clampPetStats();

        tvPetName.setText(currentPet.getName());
        tvPetLevel.setText("Level: " + currentPet.getLevel());
        tvPetStatus.setText("Status: " + currentPet.getCurrentStatus());

        pbHunger.setProgress(currentPet.getHunger());
        pbHappiness.setProgress(currentPet.getHappiness());
        pbEnergy.setProgress(currentPet.getEnergy());

        int level = currentPet.getLevel();
        int totalXP = currentPet.getTotalXP();
        int maxXP;
        if (level == 1) {
            maxXP = 100;
        } else if (level == 2) {
            maxXP = 200;
        } else {
            maxXP = 300;
        }

        int xpProgress = (int) ((totalXP / (float) maxXP) * 100);
        xpProgress = Math.min(100, xpProgress);
        pbXP.setProgress(xpProgress);

        int displayXP = Math.min(totalXP, maxXP);
        tvXPValue.setText(displayXP + "/" + maxXP);

        //pbXP.setProgress(currentPet.getTotalXP() % 100);
        tvHungerValue.setText(currentPet.getHunger() + "%");
        tvHappinessValue.setText(currentPet.getHappiness() + "%");
        tvEnergyValue.setText(currentPet.getEnergy() + "%");
        //tvXPValue.setText((currentPet.getTotalXP() % 100) + "/100");


        btnFeed.setEnabled("awake".equalsIgnoreCase(currentPet.getCurrentStatus())
                && currentPet.getEnergy() > 0 && currentPet.getHunger() < 100);
        //btnFeed.setEnabled(currentPet.getEnergy() > 0 && currentPet.getHunger() < 100);
        // ADDED THIS TO ENABLE/DISABLE the level up
        btnLevelUp.setEnabled((currentPet.getLevel() < 3) && (xpProgress == 100));



        if (isInCooldown) {
            btnTuckIn.setEnabled(false);
        } else if (currentPet.getEnergy() >= 80) {

            btnTuckIn.setEnabled(false);
            btnTuckIn.setText("Energized");
        } else {

            btnTuckIn.setEnabled(true);
            btnTuckIn.setText("Tuck In");
        }

        updatePetImage();

    }

    /*
    private void clampPetStats() {
        currentPet.setHunger(Math.max(0, Math.min(100, currentPet.getEnergy())));
        currentPet.setHappiness(Math.max(0, Math.min(100, currentPet.getHappiness())));
        currentPet.setEnergy(Math.max(0, Math.min(100, currentPet.getHunger())));
    }
    */
    private void clampPetStats() {
        currentPet.setHunger(Math.max(0, Math.min(100, currentPet.getHunger())));
        currentPet.setHappiness(Math.max(0, Math.min(100, currentPet.getHappiness())));
        currentPet.setEnergy(Math.max(0, Math.min(100, currentPet.getEnergy())));
    }
    private void updatePetImage() {
        if (currentPet == null) return;

        String petType = currentPet.getType().toLowerCase();
        int level = currentPet.getLevel();
        boolean isSleeping = currentPet.isSleeping();

        int resId = 0;

        if (petType.equals("dog")) {
            if (isSleeping) {
                switch (level) {
                    case 1: resId = R.drawable.dogsleep1; break;
                    case 2: resId = R.drawable.dogsleep2; break;
                    case 3: resId = R.drawable.dogsleep3; break;
                    default: resId = R.drawable.dogsleep1;
                }
            } else {
                switch (level) {
                    case 1: resId = R.drawable.dogawake1; break;
                    case 2: resId = R.drawable.dogawake2; break;
                    case 3: resId = R.drawable.dogawake3; break;
                    default: resId = R.drawable.dogawake1;
                }
            }
        } else if (petType.equals("cat")) {
            if (isSleeping) {
                switch (level) {
                    case 1: resId = R.drawable.catsleep1; break;
                    case 2: resId = R.drawable.catsleep2; break;
                    case 3: resId = R.drawable.catsleep3; break;
                    default: resId = R.drawable.catsleep1;
                }
            } else {
                switch (level) {
                    case 1: resId = R.drawable.catawake1; break;
                    case 2: resId = R.drawable.catawake2; break;
                    case 3: resId = R.drawable.catawake3; break;
                    default: resId = R.drawable.catawake1;
                }
            }
        } else if (petType.equals("dragon")) {
            if (isSleeping) {
                switch (level) {
                    case 1: resId = R.drawable.dragonsleep1; break;
                    case 2: resId = R.drawable.dragonsleep2; break;
                    case 3: resId = R.drawable.dragonsleep3; break;
                    default: resId = R.drawable.dragonsleep1;
                }
            } else {
                switch (level) {
                    case 1: resId = R.drawable.dragonawake1; break;
                    case 2: resId = R.drawable.dragonawake2; break;
                    case 3: resId = R.drawable.dragonawake3; break;
                    default: resId = R.drawable.dragonawake1;
                }
            }
        } else if (petType.equals("fish")) {
            if (isSleeping) {
                switch (level) {
                    case 1: resId = R.drawable.fishsleep1; break;
                    case 2: resId = R.drawable.fishsleep2; break;
                    case 3: resId = R.drawable.fishsleep3; break;
                    default: resId = R.drawable.fishsleep1;
                }
            } else {
                switch (level) {
                    case 1: resId = R.drawable.fishawake1; break;
                    case 2: resId = R.drawable.fishawake2; break;
                    case 3: resId = R.drawable.fishawake3; break;
                    default: resId = R.drawable.fishawake1;
                }
            }
        }

        if (resId != 0) {
            ivPet.setImageResource(resId);
        }
    }

    @Override
    public void onResume() {
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
    public void onPause() {
        super.onPause();
        // Stop TEST MODE stat decay when leaving screen (avoid leaks)
        if (statHandler != null && statDecayRunnable != null) {
            statHandler.removeCallbacks(statDecayRunnable);
        }
    }

    public static String getPetNameError(String petName) {
        if (petName == null || petName.trim().isEmpty()) {
            return "Pet name cannot be empty";
        }

        int length = petName.trim().length();
        if (length < 1) {
            return "Pet name must be at least 1 character";
        }

        if (length > 15) {
            return "Pet name must be at most 15 characters";
        }

        return null;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Clean up cooldown timer if running
//        if (cooldownTimer != null) {
//            cooldownTimer.cancel();
//        }
//        if (statHandler != null && statDecayRunnable != null) {
//            statHandler.removeCallbacks(statDecayRunnable);
//        }
//    }
}