/*
 * FrmAprobacionViaticosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.viaticos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmRecepcionLegalizacionesControladorUrlEnum {

    URL2624("FRMRECEPCIONLEGALIZACIONESCONTROLADORURL2624",
                    "761016"),

    URL8482("FRMRECEPCIONLEGALIZACIONESCONTROLADORURL8482",
                    "76100R"),

    URL5661("FRMRECEPCIONLEGALIZACIONESCONTROLADORURL5661",
                    "76100U");

    private final String key;
    private final String value;

    private FrmRecepcionLegalizacionesControladorUrlEnum(String key,
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
