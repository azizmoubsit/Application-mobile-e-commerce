package com.example.boutique.model;

import android.widget.Button;

import com.example.boutique.R;

public class Order {
    private String email, address, city, date, name, phone, total;
    public Order() {
    }

    public Order(String email, String address, String city, String date, String name, String phone, String total) {
        this.email = email;
        this.address = address;
        this.city = city;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
