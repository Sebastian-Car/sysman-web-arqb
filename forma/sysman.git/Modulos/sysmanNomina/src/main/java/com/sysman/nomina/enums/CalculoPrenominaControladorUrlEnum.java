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
 * @version 1.0, 18/08/2017
 * @author pespitia
 *
 */
public enum CalculoPrenominaControladorUrlEnum {

    URL0001("PERSONAL0001", "210076"),
    URL0002("CENTROCOSTO0002", "20062"),
    URL0003("TIPOSEMPLEADOO0003", "540003")
    ;

    private final String key;
    private final String value;

    private CalculoPrenominaControladorUrlEnum(String key,
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
