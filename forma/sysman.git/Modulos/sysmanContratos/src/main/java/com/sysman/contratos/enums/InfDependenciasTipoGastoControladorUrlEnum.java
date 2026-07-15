/*
 * InfDependenciasTipoGastoControladorUrlEnum
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
public enum InfDependenciasTipoGastoControladorUrlEnum {

    URL6052("INFDEPENDENCIASTIPOGASTOCONTROLADORURL6052", "62032"),

    URL5131("INFDEPENDENCIASTIPOGASTOCONTROLADORURL5131", "4001"),

    URL5548("INFDEPENDENCIASTIPOGASTOCONTROLADORURL5548", "4027");

    private final String key;
    private final String value;

    private InfDependenciasTipoGastoControladorUrlEnum(String key,
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
