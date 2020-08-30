package com.jamil.findme.Models;

public class WorkShopModel extends User {
    String WorkShopName,Description,Address;

    public WorkShopModel() {
    }

    public WorkShopModel(String uid, String name, String email, String phone,
                         String location, String image,
                         String WorkShopName, String Description,
                         String Address,String type,String password) {
        super(uid, name, email, phone, location, image,type,password);
        this.WorkShopName = WorkShopName;
        this.Description = Description;
        this.Address = Address;
    }

    public String getWorkShopName() {
        return WorkShopName;
    }

    public void setWorkShopName(String workShopName) {
        WorkShopName = workShopName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
