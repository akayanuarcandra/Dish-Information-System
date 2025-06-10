package com.dish.model;

public class Dish {
    private int id;
    private String name;
    private String type;
    private double price;
    private String ingredients;
    private String introduction;
    private String photoPath;

    // Constructor for creating a NEW dish before saving to DB (ID is not known yet)
    public Dish(String name, String type, double price, String ingredients, String introduction, String photoPath) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.ingredients = ingredients;
        this.introduction = introduction;
        this.photoPath = photoPath;
    }

    // Constructor for loading an EXISTING dish from DB (ID is known)
    public Dish(int id, String name, String type, double price, String ingredients, String introduction, String photoPath) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.ingredients = ingredients;
        this.introduction = introduction;
        this.photoPath = photoPath;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public String getIngredients() { return ingredients; }
    public String getIntroduction() { return introduction; }
    public String getPhotoPath() { return photoPath; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setPrice(double price) { this.price = price; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    @Override
    public String toString() {
        return "Dish { " +
               "id = " + id +
               ", name = '" + name + '\'' +
               '}';
    }
    
    public String toFileString() {
        return String.join(";",
                String.valueOf(id),
                name.replace(";", ","),
                type.replace(";", ","),
                String.valueOf(price),
                ingredients.replace(";", ","),
                introduction.replace(";", ","),
                photoPath.replace(";", ",")
        );
    }
    
    public static Dish fromFileString(String line) {
        String[] parts = line.split(";", 7);
        if (parts.length == 7) {
            try {
                return new Dish(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        Double.parseDouble(parts[3]),
                        parts[4],
                        parts[5],
                        parts[6]
                );
            } catch (NumberFormatException e) {
                System.err.println("Error parsing dish from line: " + line + " - " + e.getMessage());
                return null;
            }
        }
        System.err.println("Invalid dish data line: " + line);
        return null;
    }
}