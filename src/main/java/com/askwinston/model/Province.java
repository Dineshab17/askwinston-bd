package com.askwinston.model;

public enum Province {
    AB("Alberta"),
    BC("British Columbia"),
    MB("Manitoba"),
    //	MR("Maritimes"),
    NB("New Brunswick"),
    NL("Newfoundland and Labrador"),
    NS("Nova Scotia"),
    NT("Northwest Territories"),
    NU("Nunavut"),
    ON("Ontario"),
    PE("Prince Edward Island"),
    QC("Quebec"),
    SK("Saskatchewan"),
    YT("Yukon");

    private String name;

    Province(String name) {
        this.name = name;
    }

    public static Province fromName(String name) {
        for (Province province : Province.values()) {
            if (province.name.equals(name)) {
                return province;
            }
        }
        throw new IllegalArgumentException("Province with name [" + name + "] not found");
    }

    public String getName() {
        return name;
    }
}
