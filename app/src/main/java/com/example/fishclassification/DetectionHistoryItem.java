package com.example.fishclassification;

public class DetectionHistoryItem {
    private String currentDate;
    private String label;

    // Constructor
    public DetectionHistoryItem(String currentDate, String label) {
        this.currentDate = currentDate;
        this.label = label;
    }

    // Default constructor
    public DetectionHistoryItem() {
    }

    // Getter for currentDate
    public String getCurrentDate() {
        return currentDate;
    }

    // Setter for currentDate
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    // Getter for label
    public String getLabel() {
        return label;
    }

    // Setter for label
    public void setLabel(String label) {
        this.label = label;
    }
}
