package com.sysman.contabilidad.enums;

public enum FrmeliminardocsoporteControladorUrlEnum {
	
	URL2564("FRMELIMINARFAEURL2564", "1862001"),

    URL4589("FRMELIMINARFAEURL4589", "186200C"),;

    private final String key;
    private final String value;

    private FrmeliminardocsoporteControladorUrlEnum(String key, String value) {
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
