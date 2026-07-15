/*-
 * ActualizaparametrosretroactivosControladorUrlEnum.java
 *
 * 1.0
 *
 * 18/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores generados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 *
 * @version 1.0, 28/03/2019
 * @author mzanguna
 *
 */
public enum PagosespecialesControladorUrlEnum {

    URL0001("PAGOESPECIALCONTROLADORURL0001", "4001"),

    URL0002("PAGOESPECIALCONTROLADORURL0002", "151036"),

    URL0003("PAGOESPECIALCONTROLADORURL0003", "607018"),

    URL0004("PAGOESPECIALCONTROLADORURL0004", "1782001"),

    URL0005("PAGOESPECIALCONTROLADORURL0005", "1782003"),

    URL0006("PAGOESPECIALCONTROLADORURL0006", "1782004"),

    URL0007("PAGOESPECIALCONTROLADORURL0007", "1782005"),

    URL0019("PERIODOSGET", "7023");
    ;

    private final String key;
    private final String value;

    private PagosespecialesControladorUrlEnum(String key,
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
