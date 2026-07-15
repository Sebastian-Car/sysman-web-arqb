/*
 * RelacioncontratoszipaControladorUrlEnum
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
public enum RelacioncontratoszipaControladorUrlEnum {

    URL4313("RELACIONCONTRATOSZIPACONTROLADORURL4313", "14038"),

    URL3642("RELACIONCONTRATOSZIPACONTROLADORURL3642", "14036"),

    URL5270("RELACIONCONTRATOSZIPACONTROLADORURL5270", "73012"),

    URL6095("RELACIONCONTRATOSZIPACONTROLADORURL6095", "73014");

    private final String key;
    private final String value;

    private RelacioncontratoszipaControladorUrlEnum(String key, String value) {
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
