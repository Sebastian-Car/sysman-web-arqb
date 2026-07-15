/*
 * FrmcreeconfiguracionsControladorUrlEnum
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
public enum FrmcreeconfiguracionsControladorUrlEnum {

    URL331("FRMCREECONFIGURACIONSCONTROLADORURL331", "677001"),

    URL6778("FRMCREECONFIGURACIONSCONTROLADORURL6778", "29127");

    private final String key;
    private final String value;

    private FrmcreeconfiguracionsControladorUrlEnum(String key, String value) {
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
