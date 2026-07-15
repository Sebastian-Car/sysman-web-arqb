package com.sysman.presupuesto.enums;

public enum DisponibilidadesporrubroresControladorUrlEnum {
    URL4084("LISEJECRESECONSCONTROLADORURL4084", "94102"),

    URL3655("LISEJECRESECONSCONTROLADORURL3655", "4001"),

    URL4833("LISEJECRESECONSCONTROLADORURL4833", "94104");

    private final String key;
    private final String value;

    private DisponibilidadesporrubroresControladorUrlEnum(String key,
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
