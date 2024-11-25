package com.example.entrevestor;

public class Project {

    private String id;
    private String description;
    private String duration;
    private String imageUrl;
    private String link;
    private String moneyNeeded;
    private String name;
    private String userId;
    private int totalInvestors; // New field for total investors

    // No-argument constructor required for Firebase
    public Project() {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMoneyNeeded() {
        return moneyNeeded;
    }

    public void setMoneyNeeded(String moneyNeeded) {
        this.moneyNeeded = moneyNeeded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalInvestors() {
        return totalInvestors;
    }

    public void setTotalInvestors(int totalInvestors) {
        this.totalInvestors = totalInvestors;
    }
}
