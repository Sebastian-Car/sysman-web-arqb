/*
 * SubproyfuentesfinanciacionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubproyfuentesfinanciacionsControladorUrlEnum {

    URL6163("SUBPROYFUENTESFINANCIACIONSCONTROLADORURL6163", "4047"),

    URL5848("SUBPROYFUENTESFINANCIACIONSCONTROLADORURL5848", "562009");

    private final String key;
    private final String value;

    private SubproyfuentesfinanciacionsControladorUrlEnum(String key,
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
