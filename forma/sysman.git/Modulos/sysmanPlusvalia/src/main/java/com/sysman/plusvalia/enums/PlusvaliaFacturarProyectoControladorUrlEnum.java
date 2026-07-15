/*-
 * PlusvaliaFacturarProyectoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 21/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 21/02/2019
 * @author bcardenas
 *
 */
public enum PlusvaliaFacturarProyectoControladorUrlEnum {

    URL1767("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1767003"),

    URL1768("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1768001"),

    URL1773("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1773001"),

    URL1774("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1773005"),

    URL1795("PLUSVALIAFACTURARPROYECTOCONTROLADORURL1676", "1795001");

    private final String key;
    private final String value;

    private PlusvaliaFacturarProyectoControladorUrlEnum(String key,
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
