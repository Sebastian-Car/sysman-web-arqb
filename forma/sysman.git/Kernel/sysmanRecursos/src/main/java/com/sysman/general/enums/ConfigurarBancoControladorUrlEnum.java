package com.sysman.general.enums;

public enum ConfigurarBancoControladorUrlEnum {

    URL0001("VALORIZACIONLISTAACUERDOCONTROLADORURL", "36002") 
    ;

    private final String key;
    private final String value;

    private ConfigurarBancoControladorUrlEnum(String key,
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
