/*
 * FrmreglahorariosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmreglahorariosControladorUrlEnum {

    URL9051("FRMREGLAHORARIOSCONTROLADORURL9051",
                    "142002"),
    
    URL4666("FRMREGLAHORARIOSCONTROLADORURL4666",
                    "141033"),
    
    URL6969("FRMREGLAHORARIOSCONTROLADORURL6969",
                    "141039"),

    URL8454("FRMREGLAHORARIOSCONTROLADORURL8454",
                    "142001");

    private final String key;
    private final String value;

    private FrmreglahorariosControladorUrlEnum(String key, String value) {
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
