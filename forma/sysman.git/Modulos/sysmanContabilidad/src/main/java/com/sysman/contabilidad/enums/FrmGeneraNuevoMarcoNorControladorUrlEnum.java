/*
 * FrmGeneraNuevoMarcoNorControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmGeneraNuevoMarcoNorControladorUrlEnum {

    URL6990("FRMGENERANUEVOMARCONORCONTROLADORURL6990",
                    "4001"),

    URL7433("FRMGENERANUEVOMARCONORCONTROLADORURL7433",
                    "4001"),

    URL6197("FRMGENERANUEVOMARCONORCONTROLADORURL6197",
                    "7005"),

    URL9675("FRMGENERANUEVOMARCONORCONTROLADORURL9675",
                    "16198"),

    URL6594("FRMGENERANUEVOMARCONORCONTROLADORURL6594",
                    "7005"),

    URL7882("FRMGENERANUEVOMARCONORCONTROLADORURL7882",
                    "16196");

    private final String key;

    private final String value;

    private FrmGeneraNuevoMarcoNorControladorUrlEnum(String key, String value) {
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
