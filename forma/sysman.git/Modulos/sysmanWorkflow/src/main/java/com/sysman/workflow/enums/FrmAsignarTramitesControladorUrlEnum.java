/*
 * FrmAsignarTramitesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.workflow.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmAsignarTramitesControladorUrlEnum {

    URL4180("FRMASIGNARTRAMITESCONTROLADORURL4180", "1047001"),

    URL4563("FRMASIGNARTRAMITESCONTROLADORURL4563", "778003"),

    URL0001("FRMASIGNARTRAMITESCONTROLADORURL0001", "47022");

    private final String key;
    private final String value;

    private FrmAsignarTramitesControladorUrlEnum(String key, String value) {
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
