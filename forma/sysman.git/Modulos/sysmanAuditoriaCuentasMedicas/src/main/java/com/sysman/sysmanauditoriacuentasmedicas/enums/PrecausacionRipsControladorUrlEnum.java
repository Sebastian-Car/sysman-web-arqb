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
public enum PrecausacionRipsControladorUrlEnum {

    URL4391("PRECAUSACIONRIPSCONTROLADORURL4391",
                    "1823003"),
	
	URL4392("PRECAUSACIONRIPSCONTROLADORURL4392",
            "1888001"),
	
	URL4393("PRECAUSACIONRIPSCONTROLADORURL4393",
            "1823005");

    private final String key;
    private final String value;

    private PrecausacionRipsControladorUrlEnum(String key, String value) {
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
