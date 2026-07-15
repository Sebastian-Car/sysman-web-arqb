/*
 * InfDependenciasModalidadSeleccionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum LegalizacionavancesControladorUrlEnum {

    URL5760("LEGALIZACIONAVANCESCONTROLADORURL5760",
                    "82071"),

    URL5358("LEGALIZACIONAVANCESCONTROLADORURL5358",
                    "82081");

    private final String key;
    private final String value;

    private LegalizacionavancesControladorUrlEnum(String key,
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
