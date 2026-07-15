package com.sysman.contabilidad.enums;



public enum FrmDeterioroDeCarteraUrlEnum {

    URL4061("DETERIOROCARTERAURL4061", "7024"),
    
    URL1865003("DETERIOROCARTERAURL1865003", "1865003"),

    URL3516("DETERIOROCARTERAURL3516", "4001");


    private final String key;
    private final String value;

    private FrmDeterioroDeCarteraUrlEnum(String key, String value) {
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
