package com.hb.cda.electricitybusiness.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingStatus {

    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    CANCELLED("Annulée");

    private final String displayValue;

    BookingStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator
    public static BookingStatus fromDisplayValue(String displayValue) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.displayValue.equalsIgnoreCase(displayValue)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Statut de réservation inconnu : " + displayValue);
    }
}