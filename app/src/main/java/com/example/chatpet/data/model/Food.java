package com.example.chatpet.data.model;

public class Food {
    private String name;
    private int hungerPoints;

    public Food(String name, int hungerPoints) {
        this.name = name;
        this.hungerPoints = hungerPoints;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHungerPoints() { return hungerPoints; }
    public void setHungerPoints(int points) { this.hungerPoints = points; }
}