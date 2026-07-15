/*
 * FrmInfGruposConceptosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmInfGruposConceptosControladorUrlEnum {

    URL6416("FRMINFGRUPOSCONCEPTOSCONTROLADORURL6416", "662003"),

    URL5690("FRMINFGRUPOSCONCEPTOSCONTROLADORURL5690", "662001");

    private final String key;
    private final String value;

    private FrmInfGruposConceptosControladorUrlEnum(String key, String value) {
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
