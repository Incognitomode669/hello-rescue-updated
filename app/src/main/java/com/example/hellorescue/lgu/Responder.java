package com.example.hellorescue.lgu;

public class Responder {
    private String id;
    private String username;
    private String hashedPassword;
    private String role;

    public Responder() {} // Required for Firebase

    public Responder(String id, String username, String hashedPassword, String role) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}