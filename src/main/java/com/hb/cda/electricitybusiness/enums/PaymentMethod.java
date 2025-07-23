package com.hb.cda.electricitybusiness.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {

    CB("CB"),
    PAYPAL("PayPal");

    private final String displayValue;

    PaymentMethod(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue // Cette annotation indique à Jackson d'utiliser cette méthode pour la sérialisation (Java -> JSON)
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator // Cette annotation indique à Jackson d'utiliser cette méthode pour la désérialisation (JSON -> Java)
    public static PaymentMethod fromDisplayValue(String displayValue) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.displayValue.equalsIgnoreCase(displayValue)) {
                return method;
            }
        }
        // Gérer le cas où la valeur n'est pas trouvée (par exemple, lancer une exception)
        throw new IllegalArgumentException("Méthode de paiement inconnue : " + displayValue);
    }
}