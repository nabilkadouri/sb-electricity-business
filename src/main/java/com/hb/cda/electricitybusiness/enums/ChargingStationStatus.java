package com.hb.cda.electricitybusiness.enums;

public enum ChargingStationStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    CANCELLED("Annulée");

    private final String displayValue;

    ChargingStationStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
