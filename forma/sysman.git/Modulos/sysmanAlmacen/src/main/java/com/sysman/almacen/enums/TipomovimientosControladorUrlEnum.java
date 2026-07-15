/*
 * TipomovimientosControladorUrlEnum
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
public enum TipomovimientosControladorUrlEnum {

    URL4391("TIPOMOVIMIENTOSCONTROLADORURL4391",
                    "102007"),

    URL3847("TIPOMOVIMIENTOSCONTROLADORURL3847",
                    "226001"),

    URL4078("TIPOMOVIMIENTOSCONTROLADORURL4078",
                    "140003"),

    URL7338("TIPOMOVIMIENTOSCONTROLADORURL7338",
                    "41017"),

    URL4882("TIPOMOVIMIENTOSCONTROLADORURL4882",
                    "148002"),
    
    URL15048("TIPOMOVIMIENTOSCONTROLADORURL15048",
            		"15048");

    private final String key;
    private final String value;

    private TipomovimientosControladorUrlEnum(String key, String value) {
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
