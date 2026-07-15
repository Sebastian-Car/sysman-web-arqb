/*-
 * PrepararReporteUrlEnums.java
 *
 * 1.0
 *
 * 30/05/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.reporte.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 30/05/2018
 * @author jgomez
 *
 */
public enum PrepararReporteUrlEnums {

    URL647("FRMCONSULTASCONTROLADORURL647", "59020"),

    URL648("FRMCONSULTASCONTROLADORURL647", "4071"),

    ;
    private final String key;
    private final String value;

    private PrepararReporteUrlEnums(String key, String value)
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
