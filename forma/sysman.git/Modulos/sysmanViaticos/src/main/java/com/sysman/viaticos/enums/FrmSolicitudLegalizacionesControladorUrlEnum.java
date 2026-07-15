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
public enum FrmSolicitudLegalizacionesControladorUrlEnum {

    URL4547("FRMAPROBACIONVIATICOSCONTROLADORURL4547",
                    "761018"),

    URL1457("FRMAPROBACIONVIATICOSCONTROLADORURL1457",
                    "76100C"),

    URL2135("FRMAPROBACIONVIATICOSCONTROLADORURL2135",
                    "76100U"),

    URL8524("FRMAPROBACIONVIATICOSCONTROLADORURL8524",
                    "76100D"),

    URL2451("FRMAPROBACIONVIATICOSCONTROLADORURL2451",
                    "76100R");

    private final String key;
    private final String value;

    private FrmSolicitudLegalizacionesControladorUrlEnum(String key,
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
