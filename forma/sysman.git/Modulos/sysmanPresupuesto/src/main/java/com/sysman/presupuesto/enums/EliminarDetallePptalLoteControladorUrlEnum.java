/*
 * EliminarDetallePptalLoteControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EliminarDetallePptalLoteControladorUrlEnum {

    URL4249("ELIMINARDETALLEPPTALLOTECONTROLADORURL4249",
                    "38052"),
    URL1914001("ELIMINARDETALLEPPTALLOTECONTROLADORURL1914002","1914001");

    private final String key;
    private final String value;

    private EliminarDetallePptalLoteControladorUrlEnum(String key,
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
