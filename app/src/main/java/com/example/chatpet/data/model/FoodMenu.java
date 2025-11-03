package com.example.chatpet.data.model;

import java.util.ArrayList;
import java.util.List;

public class FoodMenu {
    private List<Food> menu;

    public FoodMenu() {
        this.menu = new ArrayList<>();
        initializeMenu();
    }

    private void initializeMenu() {
        menu.add(new Food("Kibble", 20));
        menu.add(new Food("Treat", 10));
        menu.add(new Food("Bone", 30));
        menu.add(new Food("Fish", 25));
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