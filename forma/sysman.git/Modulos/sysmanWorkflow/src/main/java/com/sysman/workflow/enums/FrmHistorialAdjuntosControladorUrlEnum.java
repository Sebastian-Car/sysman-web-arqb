package com.sysman.workflow.enums;

public enum FrmHistorialAdjuntosControladorUrlEnum {

	URL0001("FRMHISTORIALADJUNTOSCONTROLADORURLENUM", "1048008");

    private final String key;
    private final String value;

    private FrmHistorialAdjuntosControladorUrlEnum(String key,
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
