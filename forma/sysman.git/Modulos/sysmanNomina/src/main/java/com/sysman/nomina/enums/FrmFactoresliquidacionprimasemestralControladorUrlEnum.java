package com.sysman.nomina.enums;

public enum FrmFactoresliquidacionprimasemestralControladorUrlEnum {

    URL001("FACTORESLIQUIDACIONPRIMASEMESTRAL001","471008"),  
    URL002("FACTORESLIQUIDACIONPRIMASEMESTRAL002","471028"),  
    URL003("FACTORESLIQUIDACIONPRIMASEMESTRAL003","471010");

    private final String key;
    private final String value;

    private  FrmFactoresliquidacionprimasemestralControladorUrlEnum(String key, String value) {
        this.key   = key; 
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
