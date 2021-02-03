package com.example.boutique.model;

public class User {
    private String email;
    private String fullname;
    private String password;
    private String phone;
    private String adresse;
    private int admin;
    public User(){}
    public User( int admin, String email, String fullname, String password, String phone, String adresse){
        this.admin = admin;
        this.email = email;
        this.fullname = fullname;
        this.password = password;
        this.phone = phone;
        this.adresse = adresse;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAdmin(int admin){
        this.admin = admin;
    }

    public int getAdmin(){
        return admin;
    }
}
