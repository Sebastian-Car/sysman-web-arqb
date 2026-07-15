package com.sysman.general.enums;

public enum FrmRegistrarPagoRecaudoControladorUrlEnum {

    URL0001("FRMREGISTRARPAGORECAUDOCONTROLADORURL", "1803001"),

    URL0002("FRMREGISTRARPAGORECAUDOCONTROLADORURL", "1804001");

    private final String key;
    private final String value;

    private FrmRegistrarPagoRecaudoControladorUrlEnum(String key,
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
