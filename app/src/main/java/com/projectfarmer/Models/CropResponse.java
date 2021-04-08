package com.projectfarmer.Models;

public class CropResponse{

    String crop_name, crop_image, disease, temperature, disease_model, growth_period;

    double probability;

    int irrigation_pattern;

    public CropResponse(String crop_name, String crop_image, String disease, String temperature, String disease_model, String growth_period, double probability, int irrigation_pattern) {
        this.crop_name = crop_name;
        this.crop_image = crop_image;
        this.disease = disease;
        this.temperature = temperature;
        this.disease_model = disease_model;
        this.growth_period = growth_period;
        this.probability = probability;
        this.irrigation_pattern = irrigation_pattern;
    }

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    public String getCrop_image() {
        return crop_image;
    }

    public void setCrop_image(String crop_image) {
        this.crop_image = crop_image;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDisease_model() {
        return disease_model;
    }

    public void setDisease_model(String disease_model) {
        this.disease_model = disease_model;
    }

    public String getGrowth_period() {
        return growth_period;
    }

    public void setGrowth_period(String growth_period) {
        this.growth_period = growth_period;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public int getIrrigation_pattern() {
        return irrigation_pattern;
    }

    public void setIrrigation_pattern(int irrigation_pattern) {
        this.irrigation_pattern = irrigation_pattern;
    }
}
