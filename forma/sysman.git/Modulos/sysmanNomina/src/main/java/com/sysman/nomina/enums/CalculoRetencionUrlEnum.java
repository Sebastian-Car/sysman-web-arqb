package com.sysman.nomina.enums;

public enum CalculoRetencionUrlEnum {
	
	URL210168("CalculoRetencionUrlEnum210168","210168");

	 private final String key;
    private final String value;

    private CalculoRetencionUrlEnum(String key, String value) {
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
