/*-
 * ValorizacionCrearAcuerdosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 17/05/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/05/2019
 * @author bcardenas
 *
 */
public enum ValorizacionCrearAcuerdosControladorUrlEnum {

    URL1767("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1767003"),

    URL1768("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1768008"),

    URL1796("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1796001");

    private final String key;
    private final String value;

    private ValorizacionCrearAcuerdosControladorUrlEnum(String key,
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
