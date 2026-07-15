/*-
 * AcumuladocostomedioControladorUrlEnum.java
 *
 * 1.0
 *
 * 09/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores generados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 *
 * @version 1.0, 09/01/2018
 * @author spina
 *
 */
public enum AcumuladocostomedioControladorUrlEnum {

    URL0001("ACUMULADOCOSTOMEDIOCONTROLADORURL0001", "471008"),

    URL0002("ACUMULADOCOSTOMEDIOCONTROLADORURL0002", "537004"),

    URL0003("ACUMULADOCOSTOMEDIOCONTROLADORURL0003", "471049"),

    URL0004("ACUMULADOCOSTOMEDIOCONTROLADORURL0004", "471050"),

    ;

    private final String key;
    private final String value;

    private AcumuladocostomedioControladorUrlEnum(String key,
        String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
