package com.sysman.general.enums;

public enum ConfigEntesControladorUrlEnum {
	
    URL1907001("CONFIGENTESCONTROLADOR1907001", "1907001"),
    
    URL1750006("CONFIGENTESCONTROLADOR1750006", "1750006"),
    
	URL1750005("CONFIGENTESCONTROLADOR1750005", "1750005"),
	
	URL1032010("CONFIGENTESCONTROLADOR1032010","1032010");

    private final String key;
    private final String value;

    private ConfigEntesControladorUrlEnum(String key,
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
