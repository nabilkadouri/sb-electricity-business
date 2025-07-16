package com.hb.cda.electricitybusiness.enums;

public enum PaymentMethod {

    CB("CB"),
    PAYPAL("PayPal");

    private final String displayValue;

    PaymentMethod(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
