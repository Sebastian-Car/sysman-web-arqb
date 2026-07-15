/*
 * FrmFacEstadoControladorUrlEnum
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
public enum FrmRangoProduccionDianUrlEnum {

    URL9457("FRMRANGOPRODUCCIONDIANONTROLADORURL9457",
                    "1851001"),

    URL7514("FRMRANGOPRODUCCIONDIANONTROLADORURL7514",
                    "185400C"),

    URL3254("FRMRANGOPRODUCCIONDIANONTROLADORURL7514",
                    "1854001")

    ;

    private final String key;
    private final String value;

    private FrmRangoProduccionDianUrlEnum(String key, String value) {
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
