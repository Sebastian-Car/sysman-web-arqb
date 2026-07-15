/*
 * FrmAuditoriaGlosasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.sysmanauditoriacuentasmedicas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmAuditoriaGlosasControladorUrlEnum {

    URL6485("FRMAUDITORIAGLOSASCONTROLADORURL6485",
                    "1835001"),
    URL6486("FRMAUDITORIAGLOSASCONTROLADORURL6486",
            "1835003"), 
    URL1836001("FRMAUDITORIAGLOSASCONTROLADORURL1836001",
            "1836001"),

    URL1835005("FRMAUDITORIAGLOSASCONTROLADORURL1835005",
            "1835005");

    private final String key;
    private final String value;

    private FrmAuditoriaGlosasControladorUrlEnum(String key, String value) {
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
