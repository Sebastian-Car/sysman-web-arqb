package com.sysman.plandesarrollo.enums;

public enum BpordendepartamentalControladorUrlEnum {

	URL1964001("BPORDENDEPARTAMENTALCONTROLADORURL1964001","1964001"),
	URL1965001("BPORDENDEPARTAMENTALCONTROLADORURL1965001","1965001");

    private final String key;
    private final String value;

    private BpordendepartamentalControladorUrlEnum(String key, String value) {
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
