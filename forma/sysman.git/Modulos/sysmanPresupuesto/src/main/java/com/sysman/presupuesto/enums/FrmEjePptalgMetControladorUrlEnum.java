package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmEjePptalgMetControladorUrlEnum {

    URL0017("FRMEJEPPTALGMETCONTROLADORURL0016", "7016"),

    URL0018("FRMEJEPPTALGMETCONTROLADORURL0043", "7016"),

    URL0008("FRMEJEPPTALGMETCONTROLADORURL0007", "4007"),

    URL0035("FRMEJEPPTALGMETCONTROLADORURL0034", "45020"), //45020

    URL0037("FRMEJEPPTALGMETCONTROLADORURL0036", "45018");

    private final String key;
    private final String value;

    private FrmEjePptalgMetControladorUrlEnum(String key, String value) {
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