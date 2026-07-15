/*-
 * SolicitudesAutsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 19/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum SolicitudesAutsControladorUrlEnum {

    URL0001("SOLICITUDESAUTDETALLADOSCONTROLADORURL0001", "210111"),

    URL0002("SOLICITUDESAUTDETALLADOSCONTROLADORURL0002", "1006001");

    private final String key;
    private final String value;

    private SolicitudesAutsControladorUrlEnum(String key,
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
