/*
 * FrmnovedadesplanoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmnovedadesplanoControladorUrlEnum {

    URL4421("FRMNOVEDADESPLANOCONTROLADORURL4421", "214001"),

    URL12251("FRMNOVEDADESPLANOCONTROLADORURL12251", "217001"),

    URL12216("FRMNOVEDADESPLANOCONTROLADORURL12216", "217002"),

    URL12365("FRMNOVEDADESPLANOCONTROLADORURL12365", "213002"),

    URL12366("FRMNOVEDADESPLANOCONTROLADORURL12366", "213001"),

    URL12419("FRMNOVEDADESPLANOCONTROLADORURL12419", "215001"),

    URL12505("FRMNOVEDADESPLANOCONTROLADORURL12505", "217003");

    private final String key;
    private final String value;

    private FrmnovedadesplanoControladorUrlEnum(String key, String value) {
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
