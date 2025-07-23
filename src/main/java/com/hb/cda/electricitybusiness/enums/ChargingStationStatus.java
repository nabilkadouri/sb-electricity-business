package com.hb.cda.electricitybusiness.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargingStationStatus {
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    CANCELLED("Annulée");

    private final String displayValue;

    ChargingStationStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue // Cette annotation indique à Jackson d'utiliser cette méthode pour la sérialisation (Java -> JSON)
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator // Cette annotation indique à Jackson d'utiliser cette méthode pour la désérialisation (JSON -> Java)
    public static ChargingStationStatus fromDisplayValue(String displayValue) {
        for (ChargingStationStatus status : ChargingStationStatus.values()) {
            if (status.displayValue.equalsIgnoreCase(displayValue)) {
                return status;
            }
        }
        // Gérer le cas où la valeur n'est pas trouvée (par exemple, lancer une exception)
        throw new IllegalArgumentException("Statut de borne de recharge inconnu : " + displayValue);
    }
}