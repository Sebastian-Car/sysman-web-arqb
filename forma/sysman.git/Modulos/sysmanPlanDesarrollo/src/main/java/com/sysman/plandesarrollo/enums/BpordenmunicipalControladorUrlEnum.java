package com.sysman.plandesarrollo.enums;

public enum BpordenmunicipalControladorUrlEnum {

	URL1966001("BPORDENMUNICIPALCONTROLADORURL1966001","1966001"),
	URL1966003("BPORDENMUNICIPALCONTROLADORURL1966003","1966003"),
	URL1966005("BPORDENMUNICIPALCONTROLADORURL1966005","1966005");

    private final String key;
    private final String value;

    private BpordenmunicipalControladorUrlEnum(String key, String value) {
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
