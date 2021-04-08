package com.projectfarmer.Models;

import java.util.List;

public class Products {

    String user_id, product_id, image, name, description, tips, price;

    public Products() {
    }

    public Products(String user_id, String product_id, String image, String name, String description, String tips, String price) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.image = image;
        this.name = name;
        this.description = description;
        this.tips = tips;
        this.price = price;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
