package com.example.chatpet.data.repository;

import com.example.chatpet.data.model.Pet;
import java.util.HashMap;
import java.util.Map;


// TO BE HONEST I'M NOT TOTALLY SURE WHY WE NEED THIS FILE.
// BUT LET'S KEEP IT FOR NOW AND SEE WHEN WE CAN USE IT.

public class PetRepository {
    private static PetRepository instance;
    private Map<String, Pet> pets; // Key: pet name, Value: Pet object

    private PetRepository() {
        pets = new HashMap<>();
    }

    public static PetRepository getInstance() {
        if (instance == null) {
            instance = new PetRepository();
        }
        return instance;
    }

    public boolean savePet(Pet pet) {
        pets.put(pet.getName(), pet);
        return true;
    }

    public Pet getPetByName(String name) {
        return pets.get(name);
    }

    public boolean updatePet(Pet pet) {
        if (!pets.containsKey(pet.getName())) {
            return false;
        }
        pets.put(pet.getName(), pet);
        return true;
    }

    public boolean deletePet(String name) {
        return pets.remove(name) != null;
    }
}