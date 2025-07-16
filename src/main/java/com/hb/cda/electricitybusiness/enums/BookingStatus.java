package com.hb.cda.electricitybusiness.enums;

public enum BookingStatus {

    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    CANCELLED("Annulée");

    private final String displayValue;

    BookingStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
