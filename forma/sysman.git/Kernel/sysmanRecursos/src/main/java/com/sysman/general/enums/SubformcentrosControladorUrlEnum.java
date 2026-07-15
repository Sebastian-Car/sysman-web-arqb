/*-
 * SubformcentrosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 6/04/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 6/04/2017
 * @author amonroy
 *
 */
public enum SubformcentrosControladorUrlEnum {

    URL0001("SUBFORMCENTROSCONTROLADORURL001", "39003"),

    URL0002("SUBFORMCENTROSCONTROLADORURL002", "39005"),

    URL0003("SUBFORMCENTROSCONTROLADORURL003", "39007"),

    URL0004("SUBFORMCENTROSCONTROLADORURL004", "39009");

    private final String key;
    private final String value;

    private SubformcentrosControladorUrlEnum(String key, String value) {
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
