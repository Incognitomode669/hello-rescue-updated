package com.example.hellorescue.client;

public class Hotline {
    private String key;
    private String role;
    private String number;

    public Hotline() {} // Empty constructor needed for Firebase

    public Hotline(String key, String role, String number) {
        this.key = key;
        this.role = role;
        this.number = number;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
}