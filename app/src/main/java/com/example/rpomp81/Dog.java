package com.example.rpomp81;

import java.io.Serializable;

public class Dog implements Serializable {
    private String name;
    private String image;
    private String description;
    private int age;

    public Dog(String name, String image, String description, int age) {
        this.name = name;
        this.image = image;
        this.description = description;
        this.age = age;
    }

    public String getName() { return name; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public int getAge() { return age; }
}
