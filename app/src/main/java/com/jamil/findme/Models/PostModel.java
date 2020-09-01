package com.jamil.findme.Models;

public class PostModel {
    String ProductName,location, Price, Description, Type, Model, Image, WorkShop, post_id, user_id, time;

    public PostModel(String productName, String price,String location
            , String description, String type, String model, String image,
                     String workShop, String post_id, String user_id,String time) {
        this.ProductName = productName;
        this.Price = price;
        this.location = location;
        this.Description = description;
        this.Type = type;
        this.Model = model;
        this.Image = image;
        this.WorkShop = workShop;
        this.post_id = post_id;
        this.user_id = user_id;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public PostModel() {
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getWorkShop() {
        return WorkShop;
    }

    public void setWorkShop(String workShop) {
        WorkShop = workShop;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

