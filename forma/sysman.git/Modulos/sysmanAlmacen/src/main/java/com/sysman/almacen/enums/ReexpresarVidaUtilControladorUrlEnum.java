package com.sysman.almacen.enums;

public enum ReexpresarVidaUtilControladorUrlEnum {

	URL112154("URL112154", "112154"),

    URL112156("URL112156", "112187"),

    URL369("URL369", "179002"),
    
    URL1749005("URL1749005", "1749005"),
	
	URL1749006("URL1749006", "1749006");

    private final String key;
    private final String value;

    private ReexpresarVidaUtilControladorUrlEnum(String key, String value) {
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