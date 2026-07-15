package com.sysman.presupuesto.enums;

public enum unidadejecutorasControladorUrlEnum {
    URL4002("UNIDADEJECUTORACONTROLADORURL4002", "4002"),
	URL4016("UNIDADEJECUTORACONTROLADORURL4002", "4016");
	

    private final String key;
    private final String value;

    private unidadejecutorasControladorUrlEnum(String key, String value) {
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
