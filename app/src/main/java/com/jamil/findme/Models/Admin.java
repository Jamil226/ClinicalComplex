package com.jamil.findme.Models;

public class Admin extends User {
    String age;

    public Admin() {
    }

    public Admin(String uid, String name, String email, String phone,
                 String location, String image, String type, String age, String password) {
        super(uid, name, email, phone, location, image,type,password);
        this.age = age;
    }
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
