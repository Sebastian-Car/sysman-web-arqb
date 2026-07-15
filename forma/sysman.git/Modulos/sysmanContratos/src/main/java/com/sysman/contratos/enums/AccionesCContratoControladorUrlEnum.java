/*-
 * AccionesCContratoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 2/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * 
 * @version 1.0, 2/08/2017
 * @author pespitia
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum AccionesCContratoControladorUrlEnum {

    URL0001("ACCIONESCCONTRATOCONTROLADORURL0001", "73012"),

    URL0002("ACCIONESCCONTRATOCONTROLADORURL0002", "73014");

    private final String key;
    private final String value;

    private AccionesCContratoControladorUrlEnum(String key, String value) {
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
