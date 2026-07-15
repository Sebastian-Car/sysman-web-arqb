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
public enum InfDependenciasModalidadSeleccionControladorUrlEnum {

    URL5760("INFDEPENDENCIASMODALIDADSELECCIONCONTROLADORURL5760",
                    "4027"),

    URL4814("INFDEPENDENCIASMODALIDADSELECCIONCONTROLADORURL4814",
                    "108001"),

    URL5358("INFDEPENDENCIASMODALIDADSELECCIONCONTROLADORURL5358",
                    "4001"),

    URL17470("INFDEPENDENCIASMODALIDADSELECCIONCONTROLADORURL17470",
                    "82060"),

    URL6256("INFDEPENDENCIASMODALIDADSELECCIONCONTROLADORURL6256",
                    "62032"),
    
    URL5555("INFDEPENDENCIASMODALIDADSELECCIONCONTROLADORURL5555",
                    "82061")
    
    ;

    private final String key;
    private final String value;

    private InfDependenciasModalidadSeleccionControladorUrlEnum(String key,
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
