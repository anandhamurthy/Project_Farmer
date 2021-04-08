package com.projectfarmer.Models;

public class Fertilizer {

    String image, material, name;

    public Fertilizer() {
    }

    public Fertilizer(String image, String material, String name) {
        this.image = image;
        this.material = material;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
