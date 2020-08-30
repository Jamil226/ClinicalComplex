package com.jamil.findme.Models;

import java.util.ArrayList;

public class Visitor extends User {
    private boolean selected;
    private ArrayList<Visitor> arrayList;

    public Visitor(ArrayList<Visitor> arrayList) {
        this.arrayList = arrayList;
    }

    public Visitor(boolean selected) {
        this.selected = selected;
    }

    public Visitor() {
    }

    public Visitor(String uid, String name, String email,
                   String phone, String location, String image,String type,String password) {
        super(uid, name, email, phone, location, image,type,password);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ArrayList<Visitor> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Visitor> arrayList) {
        this.arrayList = arrayList;
    }
}
