package com.marcus.util;

public enum AddressField {
    ADDRESSES("addresses"),
    CITY("city"),
    STREET("street"),
    ZIP_CODE("zipCode"),
    STATE("state"),
    COUNTRY("country");

    private final String name;

    AddressField(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
