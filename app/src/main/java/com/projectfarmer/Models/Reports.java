package com.projectfarmer.Models;

public class Reports {

    String user_id, report_id, image, name, description, budget;

    public Reports() {
    }

    public Reports(String user_id, String report_id, String image, String name, String description, String budget) {
        this.user_id = user_id;
        this.report_id = report_id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.budget = budget;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReport_id() {
        return report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }
}
