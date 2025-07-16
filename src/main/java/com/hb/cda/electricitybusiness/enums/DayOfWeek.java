package com.hb.cda.electricitybusiness.enums;

public enum DayOfWeek {

    MONDAY("Lundi"),
    TUESDAY("Mardi"),
    WEDNESDAY("Mercredi"),
    THURSDAY("Jeudi"),
    FRIDAY("Vendredi"),
    SATURDAY("Samedi"),
    SUNDAY("Dimanche");

    private final String displayValue;

    // Constructeur pour associer la valeur française
    DayOfWeek(String displayValue) {
        this.displayValue = displayValue;
    }

    // Getter pour récupérer la valeur française
    public String getDisplayValue() {
        return displayValue;
    }
}
