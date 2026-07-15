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
public enum FrmAprobacionLegalizacionesControladorUrlEnum {

    URL2124("FRMAPROBACIONLEGALIZACIONESCONTROLADORURL2124",
                    "761014"),

    URL3435("FRMAPROBACIONLEGALIZACIONESCONTROLADORURL3435",
                    "76100R"),

    URL7576("FRMAPROBACIONLEGALIZACIONESCONTROLADORURL7576",
                    "76100U"),

    URL2125("FRMAPROBACIONLEGALIZACIONESCONTROLADORURL2125",
                    "52002")

    ;

    private final String key;
    private final String value;

    private FrmAprobacionLegalizacionesControladorUrlEnum(String key,
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
