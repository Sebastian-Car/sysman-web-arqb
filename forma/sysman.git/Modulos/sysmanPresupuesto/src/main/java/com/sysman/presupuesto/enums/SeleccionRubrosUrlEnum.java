/*-
 * SeleccionRubrosUrlEnum.java
 *
 * 1.0
 * 
 * 2/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
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
public enum SeleccionRubrosUrlEnum {

    URL94119("SELECCIONRUBROSCONTROLADORURL4178", "94119"),

    URL94117("SELECCIONRUBROSCONTROLADORURL4140", "94117");

    private final String key;
    private final String value;

    private SeleccionRubrosUrlEnum(String key, String value) {
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
