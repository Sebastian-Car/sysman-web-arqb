package com.sysman.facturaciongeneral.enums;

public enum FrmconsultapagoparcialControladorUrlEnum {

    URL1955001("FRMCONSULTAPAGOPARCIALCONTROLADORURLENUM1955001", "1955001"),
    
    URL1955002("FRMCONSULTAPAGOPARCIALCONTROLADORURLENUM1955002", "1955002");

    private final String key;
    private final String value;

    private FrmconsultapagoparcialControladorUrlEnum(String key, String value) {
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
