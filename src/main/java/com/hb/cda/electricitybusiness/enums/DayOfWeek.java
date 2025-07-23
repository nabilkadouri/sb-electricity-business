package com.hb.cda.electricitybusiness.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DayOfWeek {
    MONDAY("Lundi"),
    TUESDAY("Mardi"),
    WEDNESDAY("Mercredi"),
    THURSDAY("Jeudi"),
    FRIDAY("Vendredi"),
    SATURDAY("Samedi"),
    SUNDAY("Dimanche");

    private final String displayValue;

    DayOfWeek(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue // Indique à Jackson d'utiliser cette méthode pour la sérialisation (Java -> JSON)
    public String getDisplayValue() {
        return displayValue;
    }

    @JsonCreator // Indique à Jackson d'utiliser cette méthode pour la désérialisation (JSON -> Java)
    public static DayOfWeek fromDisplayValue(String displayValue) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.displayValue.equalsIgnoreCase(displayValue)) {
                return day;
            }
        }
        // Gérer le cas où la valeur n'est pas trouvée (par exemple, lancer une exception)
        throw new IllegalArgumentException("Valeur de jour de la semaine inconnue pour la désérialisation: " + displayValue);
    }
}