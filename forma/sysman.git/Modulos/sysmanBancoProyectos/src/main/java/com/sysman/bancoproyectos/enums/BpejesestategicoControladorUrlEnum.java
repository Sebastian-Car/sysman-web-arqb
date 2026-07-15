package com.sysman.bancoproyectos.enums;

public enum BpejesestategicoControladorUrlEnum {
	
	URL1986001("BPEJEESTRATEGICOCONTROLADORURL", "1986001");

    private final String key;
    private final String value;

    private BpejesestategicoControladorUrlEnum(String key, String value) {
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
