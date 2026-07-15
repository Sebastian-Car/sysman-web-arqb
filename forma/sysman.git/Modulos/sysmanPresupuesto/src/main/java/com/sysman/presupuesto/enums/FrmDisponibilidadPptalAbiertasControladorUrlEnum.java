package com.sysman.presupuesto.enums;

public enum FrmDisponibilidadPptalAbiertasControladorUrlEnum {


    URL3328("FRMDISPONIBILIDADPPTALABIERTASCONTROLADORURL", "4001"),// Combo año 

    URL3725("FRMDISPONIBILIDADPPTALABIERTASCONTROLADORURL", "94102"),

    URL4635("FRMDISPONIBILIDADPPTALABIERTASCONTROLADORURL", "94104");

    private final String key;
    private final String value;

    private FrmDisponibilidadPptalAbiertasControladorUrlEnum(String key, String value) {
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
