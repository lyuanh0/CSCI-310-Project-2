package com.example.chatpet.data.model;

import java.util.ArrayList;
import java.util.List;

public class FoodMenu {
    private List<Food> menu;

    public FoodMenu(String petType) {
        this.menu = new ArrayList<>();
        initializeMenu(petType);
    }

    private void initializeMenu(String petType) {
                switch (petType.toLowerCase()) {
            case "dog":
                menu.add(new Food("Pizza", 10));
                menu.add(new Food("Kibble", 20));
                menu.add(new Food("Bone", 30));
                break;

            case "cat":
                menu.add(new Food("Pizza", 10));
                menu.add(new Food("Kibble", 20));
                menu.add(new Food("Fish", 30));
                break;

            case "dragon":
                menu.add(new Food("Pizza", 10));
                menu.add(new Food("Magma Rock", 20));
                menu.add(new Food("Fire Crystals", 30));
                break;

            case "fish":
                menu.add(new Food("Pizza", 10));
                menu.add(new Food("Kibble", 20));
                menu.add(new Food("Kelp", 30));
                break;

            default:
//                menu.add(new Food("Mystery #1", 10));
//                menu.add(new Food("Mystery #2", 20));
//                menu.add(new Food("Mystery #3", 30));
                break;
        }
    }

    public void displayMenu() {
        for (Food food : menu) {
            System.out.println(food.getName() + " + Hunger Points: " + food.getHungerPoints());
        }
    }

    public Food getFood(String name) {
        for (Food food : menu) {
            if (food.getName().equalsIgnoreCase(name)) {
                return food;
            }
        }
        return null;
    }

    public List<Food> getMenu() { return menu; }
}