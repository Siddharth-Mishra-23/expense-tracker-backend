package com.example.expensetracker;

public class ExpenseRequest {

    private String userId;
    private double amount;
    private String description;
    private String date;      // yyyy-MM-dd
    private String category;

    public ExpenseRequest(String userId, double amount, String description, String date, String category) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
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
