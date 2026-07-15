/*
 * DigitalizacionContratosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DigitalizacionContratosControladorUrlEnum {

    URL9331("DIGITALIZACIONCONTRATOSCONTROLADORURL9331",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"DI_CONTRATO\",");

    private final String key;
    private final String value;

    private DigitalizacionContratosControladorUrlEnum(String key,
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
