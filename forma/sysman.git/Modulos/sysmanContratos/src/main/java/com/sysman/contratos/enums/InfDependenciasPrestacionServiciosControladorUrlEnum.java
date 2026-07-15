/*
 * InfDependenciasPrestacionServiciosControladorUrlEnum
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
public enum InfDependenciasPrestacionServiciosControladorUrlEnum {

    URL5277("INFDEPENDENCIASPRESTACIONSERVICIOSCONTROLADORURL5277", "4001"),

    URL6120("INFDEPENDENCIASPRESTACIONSERVICIOSCONTROLADORURL6120", "62002"),

    URL14459("INFDEPENDENCIASPRESTACIONSERVICIOSCONTROLADORURL14459", ""),

    URL5691("INFDEPENDENCIASPRESTACIONSERVICIOSCONTROLADORURL5691", "4016");

    private final String key;
    private final String value;

    private InfDependenciasPrestacionServiciosControladorUrlEnum(String key,
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
