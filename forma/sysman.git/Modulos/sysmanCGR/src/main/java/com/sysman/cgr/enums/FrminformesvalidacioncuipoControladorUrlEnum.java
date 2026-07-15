package com.sysman.cgr.enums;

public enum FrminformesvalidacioncuipoControladorUrlEnum {

    URL3043("FrmActualizarClasificadoresUrlEnum3043", "4001"),
    URL0001("FrmActualizarClasificadoresUrlEnum0001", "1889006"),
    URL0002("FrmActualizarClasificadoresUrlEnum0005", "1889005"),
    URL0003("FrmActualizarClasificadoresUrlEnum0005", "1889007"),
    URL0041("FrmActualizarClasificadoresUrlEnum0041", "1889008"),
    URL0042("FrmActualizarClasificadoresUrlEnum0042", "1889009"),
    URL0005("FrmActualizarClasificadoresUrlEnum0005", "1889004");
   
    private final String key;
    private final String value;

    private FrminformesvalidacioncuipoControladorUrlEnum(String key, String value) {
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
