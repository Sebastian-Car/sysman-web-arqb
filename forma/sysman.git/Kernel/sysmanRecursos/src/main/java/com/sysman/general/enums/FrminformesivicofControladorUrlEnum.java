package com.sysman.general.enums;

public enum FrminformesivicofControladorUrlEnum {

    URL4001("FRMINFORMESIVICOFCONTROLADORURLENUM4001", "4001"),
    
	URL7001("FRMINFORMESIVICOFCONTROLADORURLENUM7001", "7001"),
	
	URL7012("FRMINFORMESIVICOFCONTROLADORURLENUM7012", "7012"),
	
	URL1750001("FRMINFORMESIVICOFCONTROLADORURLENUM1750001", "1750001"),
	
	URL1750008("FRMINFORMESIVICOFCONTROLADORURLENUM1750008", "1750008");

    private final String key;
    private final String value;

    private FrminformesivicofControladorUrlEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
