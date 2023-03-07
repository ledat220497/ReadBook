package com.example.readbook.Activity.Admin.modeladmin;

public class ModelUser {
    String  name, profileimage;

    public ModelUser() {
    }

    public ModelUser(String name, String profileimage) {
        this.name = name;
        this.profileimage = profileimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
