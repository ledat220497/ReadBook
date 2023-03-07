package com.example.readbook.Model;

public class User {
    private String nameUser;
    private String emailUser;
    private String urlUser;

    public User(String nameUser, String emailUser, String urlUser) {
        this.nameUser = nameUser;
        this.emailUser = emailUser;
        this.urlUser = urlUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getUrlUser() {
        return urlUser;
    }

    public void setUrlUser(String urlUser) {
        this.urlUser = urlUser;
    }
}
