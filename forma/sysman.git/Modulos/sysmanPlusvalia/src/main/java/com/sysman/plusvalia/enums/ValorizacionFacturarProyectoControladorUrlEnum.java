/*-
 * ValorizacionFacturarProyectoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 16/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 16/05/2019
 * @author bcardenas
 *
 */
public enum ValorizacionFacturarProyectoControladorUrlEnum {

    URL1767("VALORIZACIONFACTURARPROYECTOCONTROLADORURL1767", "1767007");

    private final String key;
    private final String value;

    private ValorizacionFacturarProyectoControladorUrlEnum(String key,
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
