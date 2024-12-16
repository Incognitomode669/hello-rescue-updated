package com.example.hellorescue.responderpolice;

public class Hotline {
    private String key;
    private String number;
    private String role;

    public Hotline() {
    }

    public Hotline(String number, String role) {
        this.number = number;
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
