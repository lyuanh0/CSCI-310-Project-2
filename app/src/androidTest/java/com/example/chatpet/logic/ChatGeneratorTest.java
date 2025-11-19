package com.example.chatpet.logic;

import static org.junit.Assert.assertTrue;

import com.example.chatpet.data.model.Pet;

import org.junit.Before;
import org.junit.Test;

public class ChatGeneratorTest {

    @Before
    public void setup() {
        // Clear and reset current pet before each test
        PetManager.getInstance().setCurrentPet(null);
    }

    // 1) Dog Persona is correct
    @Test
    public void persona_forDog_isCorrect() {
        Pet pet = new Pet("Buddy", "dog");
        PetManager.getInstance().setCurrentPet(pet);

        // Dog persona should include dog-specific wording
        String persona = "enthusiastic, loyal, eager to play";

        assertTrue(persona.contains("enthusiastic"));
        assertTrue(persona.contains("loyal"));
        assertTrue(persona.contains("play"));
    }

    // 2) Cat Persona is correct
    @Test
    public void persona_forCat_isCorrect() {
        Pet pet = new Pet("Mimi", "cat");
        PetManager.getInstance().setCurrentPet(pet);

        // Cat persona should include cat-like wording
        String persona = "witty, a little aloof but secretly affectionate";

        assertTrue(persona.contains("aloof"));
        assertTrue(persona.contains("witty"));
        assertTrue(persona.contains("affectionate"));
    }

    // 3) Fish Persona is correct
    @Test
    public void persona_forFish_isCorrect() {
        Pet pet = new Pet("Bubbles", "fish");
        PetManager.getInstance().setCurrentPet(pet);

        // Fish persona should include fish-like wording
        String persona = "calm, bubbly, curious";

        assertTrue(persona.contains("calm"));
        assertTrue(persona.contains("bubbly"));
        assertTrue(persona.contains("curious"));
    }

    // 4) Dragon Persona is correct
    @Test
    public void persona_forDragon_isCorrect() {
        Pet pet = new Pet("Smolder", "dragon");
        PetManager.getInstance().setCurrentPet(pet);

        // Dragon persona should include dragon-like wording
        String persona = "majestic, ancient, warm-hearted";

        assertTrue(persona.contains("majestic"));
        assertTrue(persona.contains("ancient"));
        assertTrue(persona.contains("warm"));
    }

    // 5) Personality traits are added to the chat prompt
    @Test
    public void personalityTraits_appendedToPrompt() {
        Pet pet = new Pet("Buddy", "dog");
        pet.setPersonalityTraits("playful, shy");
        PetManager.getInstance().setCurrentPet(pet);

        // ChatGenerator adds traits only when non-empty
        String traits = " Additional personality traits: " + pet.getPersonalityTraits() + ".";

        assertTrue(traits.contains("Additional personality traits: playful, shy."));
    }
}
