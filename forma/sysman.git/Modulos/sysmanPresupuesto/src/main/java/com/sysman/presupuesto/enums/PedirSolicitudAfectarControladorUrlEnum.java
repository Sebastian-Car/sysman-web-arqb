/*
 * PedirSolicitudAfectarControladorUrlEnum
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
public enum PedirSolicitudAfectarControladorUrlEnum {

    URL5945("PEDIRSOLICITUDAFECTARCONTROLADORURL5945", "130005"),
    
    URL8572("PEDIRSOLICITUDAFECTARCONTROLADORURL8572", "431003"),
    
    URL431012("PEDIRSOLICITUDAFECTARCONTROLADORURL8572", "431012"),

    URL9837("PEDIRSOLICITUDAFECTARCONTROLADORURL9837", "218001");

    private final String key;
    private final String value;

    private PedirSolicitudAfectarControladorUrlEnum(String key, String value) {
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
