package com.askwinston.model;

import java.util.Arrays;

public enum Courier {

    CANADA_POST("Canada Post", "canadapost.ca", "https://www.canadapost.ca/"),
    FEDEX("FedEx", "fedex.com", "https://www.fedex.com/en-ca/home.html"),
    UPS("UPS", "ups.com", "https://www.ups.com/ca/en/Home.page"),
    PUROLATOR("Purolator", "purolator.com", "https://www.purolator.com/en"),
    SWIFTPOST_ICS("Swiftpost - ICS", "swiftpost.com", "https://www.icscourier.ca/online-services/parcel-tracking.aspx"),
    SWIFTPOST_CANPAR("Swiftpost - Canpar", "swiftpost.com", "https://www.canpar.com/en/tracking/track.htm");

    private String name;
    private String shortName;
    private String link;

    Courier(String name, String shortName, String link) {
        this.name = name;
        this.shortName = shortName;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }


    @Override
    public String toString() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public static Courier getByName(String name) {
        return Arrays.stream(Courier.values())
                .filter(e -> e.name.equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No Courier with name " + name));
    }
}
