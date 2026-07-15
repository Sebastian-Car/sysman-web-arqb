/*
 * FrmImportarRipsControladorUrlEnum
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
public enum FrmImportarRipsControladorUrlEnum {

    URL4391("FRMIMPORTARRIPSCONTROLADORURL4391",
                    "14001"),
    URL4392("FRMIMPORTARRIPSCONTROLADORURL4392",
                    "1823001"),
    URL4393("FRMIMPORTARRIPSCONTROLADORURL4393",
                    "1823002"),
    URL4394("FRMIMPORTARRIPSCONTROLADORURL4394",
            "1886003"),
    URL4395("FRMIMPORTARRIPSCONTROLADORURL4395",
            "1885001"),
    URL4396("FRMIMPORTARRIPSCONTROLADORURL001",
            "1823006"), 
    URL1885003("FRMIMPORTARRIPSCONTROLADORURL1885003","1885003");

    private final String key;
    private final String value;

    private FrmImportarRipsControladorUrlEnum(String key, String value) {
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
