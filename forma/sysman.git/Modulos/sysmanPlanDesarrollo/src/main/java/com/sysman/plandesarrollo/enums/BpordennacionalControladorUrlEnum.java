package com.sysman.plandesarrollo.enums;

public enum BpordennacionalControladorUrlEnum {
	
	URL1961001("BPORDENNACIONALCONTROLADORURL1961001","1961001"),
	URL1962001("BPORDENNACIONALCONTROLADORURL1962001","1962001"),
	URL1963001("BPORDENNACIONALCONTROLADORURL1963001","1963001");

    private final String key;
    private final String value;

    private BpordennacionalControladorUrlEnum(String key, String value) {
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
