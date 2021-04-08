package com.projectfarmer.Models;

import java.util.List;

public class Crops {

    String user_id, crop_id, ph_land_image, ph_value, ph_description, crop_name, crop_image,
            crop_temperature, crop_growth_period, crop_irrigation_pattern, crop_disease, crop_disease_model, crop_harvest_note;

    int level;
    boolean failure;

    List<Fertilizer> fertilizers;

    public Crops() {
    }

    public Crops(String user_id, String crop_id, String ph_land_image, String ph_value, String ph_description, String crop_name, String crop_image, String crop_temperature, String crop_growth_period, String crop_irrigation_pattern, String crop_disease, String crop_disease_model, String crop_harvest_note, int level, boolean failure, List<Fertilizer> fertilizers) {
        this.user_id = user_id;
        this.crop_id = crop_id;
        this.ph_land_image = ph_land_image;
        this.ph_value = ph_value;
        this.ph_description = ph_description;
        this.crop_name = crop_name;
        this.crop_image = crop_image;
        this.crop_temperature = crop_temperature;
        this.crop_growth_period = crop_growth_period;
        this.crop_irrigation_pattern = crop_irrigation_pattern;
        this.crop_disease = crop_disease;
        this.crop_disease_model = crop_disease_model;
        this.crop_harvest_note = crop_harvest_note;
        this.level = level;
        this.failure = failure;
        this.fertilizers = fertilizers;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCrop_id() {
        return crop_id;
    }

    public void setCrop_id(String crop_id) {
        this.crop_id = crop_id;
    }

    public String getPh_land_image() {
        return ph_land_image;
    }

    public void setPh_land_image(String ph_land_image) {
        this.ph_land_image = ph_land_image;
    }

    public String getPh_value() {
        return ph_value;
    }

    public void setPh_value(String ph_value) {
        this.ph_value = ph_value;
    }

    public String getPh_description() {
        return ph_description;
    }

    public void setPh_description(String ph_description) {
        this.ph_description = ph_description;
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

    public String getCrop_temperature() {
        return crop_temperature;
    }

    public void setCrop_temperature(String crop_temperature) {
        this.crop_temperature = crop_temperature;
    }

    public String getCrop_growth_period() {
        return crop_growth_period;
    }

    public void setCrop_growth_period(String crop_growth_period) {
        this.crop_growth_period = crop_growth_period;
    }

    public String getCrop_irrigation_pattern() {
        return crop_irrigation_pattern;
    }

    public void setCrop_irrigation_pattern(String crop_irrigation_pattern) {
        this.crop_irrigation_pattern = crop_irrigation_pattern;
    }

    public String getCrop_disease() {
        return crop_disease;
    }

    public void setCrop_disease(String crop_disease) {
        this.crop_disease = crop_disease;
    }

    public String getCrop_disease_model() {
        return crop_disease_model;
    }

    public void setCrop_disease_model(String crop_disease_model) {
        this.crop_disease_model = crop_disease_model;
    }

    public String getCrop_harvest_note() {
        return crop_harvest_note;
    }

    public void setCrop_harvest_note(String crop_harvest_note) {
        this.crop_harvest_note = crop_harvest_note;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public List<Fertilizer> getFertilizers() {
        return fertilizers;
    }

    public void setFertilizers(List<Fertilizer> fertilizers) {
        this.fertilizers = fertilizers;
    }
}
