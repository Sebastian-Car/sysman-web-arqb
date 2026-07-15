package com.sysman.precontractual.enums;

public enum CertificadoinexistenciasControladorUrlEnum {

	URL1892001("CERTIFICADOINEXISTENCIASCONTROLADORURL1892001", "1892001"),
	URL62109("CERTIFICADOINEXISTENCIASCONTROLADORURL62109", "62109"),
	URL1890001("CERTIFICADOINEXISTENCIASCONTROLADORURL1890001", "1890001"),
	URL32055("CERTIFICADOINEXISTENCIASCONTROLADORURL32055", "32055"),
	URL104073("CERTIFICADOINEXISTENCIASCONTROLADORURL104073", "104073"),
	URL104063("CERTIFICADOINEXISTENCIASCONTROLADORURL104063", "104063");

    private final String key;
    private final String value;

    private CertificadoinexistenciasControladorUrlEnum(String key, String value) {
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
