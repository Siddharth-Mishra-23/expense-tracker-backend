package com.example.expensetracker;

public class Expense {

    private String id;
    private String userId;
    private double amount;
    private String description;
    private String date;     // yyyy-MM-dd
    private String category;

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }
}
