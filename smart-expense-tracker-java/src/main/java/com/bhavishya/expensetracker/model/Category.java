package com.bhavishya.expensetracker.model;

public enum Category {
    FOOD("Food"), RENT("Rent"), ENTERTAINMENT("Entertainment"), TRAVEL("Travel"),
    UTILITIES("Utilities"), HEALTH("Health"), SHOPPING("Shopping"), OTHER("Other");
    private final String label;
    Category(String label) { this.label = label; }
    @Override public String toString() { return label; }
    public static Category fromLabel(String value) {
        for (Category category : values()) if (category.label.equalsIgnoreCase(value) || category.name().equalsIgnoreCase(value)) return category;
        return OTHER;
    }
}
