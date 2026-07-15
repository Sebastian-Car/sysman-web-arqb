package com.sysman.contratos.enums;

public enum FrmGestionContractualControladorUrlEnum {

	 URL0001("INFORMESIACONTROLADORURL4660", "108001");

    private final String key;
    private final String value;

    private FrmGestionContractualControladorUrlEnum(String key, String value) {
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
