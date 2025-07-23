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

    @JsonValue // Cette annotation indique à Jackson d'utiliser cette méthode pour la sérialisation (Java -> JSON)
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator // Cette annotation indique à Jackson d'utiliser cette méthode pour la désérialisation (JSON -> Java)
    public static BookingStatus fromDisplayValue(String displayValue) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.displayValue.equalsIgnoreCase(displayValue)) {
                return status;
            }
        }
        // Gérer le cas où la valeur n'est pas trouvée, par exemple en lançant une exception
        throw new IllegalArgumentException("Statut de réservation inconnu : " + displayValue);
    }
}