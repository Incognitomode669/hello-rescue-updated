package com.example.hellorescue.client.submitreport_police;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PoliceReport {
    private String id;          // Firebase unique key
    private String type;        // Incident type from dropdown
    private String description;
    private double latitude;
    private double longitude;
    private String imageUrl;    // Optional camera image
    private long timestamp;
    private String reporterPhilId;  // Reporter's PhilID
    private String reporterName;    // Reporter's full name
    private String status;      // Report status (e.g., "pending", "responded")
    private String address;     // Location address
    private String mobileNumber; // Reporter's mobile number

    // Empty constructor required for Firebase
    public PoliceReport() {
    }

    // Constructor with essential fields
    public PoliceReport(String type, String description, double latitude, double longitude, String imageUrl) {
        this.type = type;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.timestamp = System.currentTimeMillis();
        this.status = "pending"; // Default status
    }

    // Getters
    public String getId() {
        return id;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReporterPhilId() {
        return reporterPhilId;
    }

    public void setReporterPhilId(String reporterPhilId) {
        this.reporterPhilId = reporterPhilId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    // Helper method to convert timestamp to readable date
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}

