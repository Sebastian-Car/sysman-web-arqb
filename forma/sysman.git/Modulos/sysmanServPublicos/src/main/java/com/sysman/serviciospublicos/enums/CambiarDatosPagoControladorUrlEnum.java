/*
 * CambiarDatosPagoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CambiarDatosPagoControladorUrlEnum {

    URL3530("CAMBIARDATOSPAGOCONTROLADORURL3530", "228001"),

    URL5152("CAMBIARDATOSPAGOCONTROLADORURL5152", "345001"),

    URL5780("CAMBIARDATOSPAGOCONTROLADORURL5780", "345001");

    private final String key;
    private final String value;

    private CambiarDatosPagoControladorUrlEnum(String key, String value) {
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
