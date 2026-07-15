/*
 * ActualizarSaldosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmarmonizacionpdsControladorUrlEnum {

    URL3222("FRMARMONIZACIONPDSCONTROLADORURL3222", "433016"),

    URL17434("FRMARMONIZACIONPDSCONTROLADORURL17434", "576014"),
	
	URL001("FRMARMONIZACIONPDSCONTROLADORURL001","576018");

    private final String key;
    private final String value;

    private FrmarmonizacionpdsControladorUrlEnum(String key, String value) {
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
