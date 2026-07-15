/*-
 * FamiliaresControladorEnum.java
 *
 * 1.0
 *
 * 28/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 *
 * @version 1.0, 28/12/2017
 * @author spina
 *
 */
public enum FamiliaresControladorEnum {
    DCTOEMPLEADO("DCTOEMPLEADO"),

    FONDO_SALUD("FONDO_SALUD"),

    NIT("NIT"),

    FECHANCTO("FECHANCTO"),

    IDENTIFICACION("IDENTIFICACION"),

    NOMBRE_FONDO_SALUD("NOMBRE_FONDO_SALUD"),

    POLIZA("POLIZA"),

    SUCURSAL_EMPLEADO("SUCURSAL_EMPLEADO"),

    DCTO_EMPLEADO("DCTO_EMPLEADO");

    private final String value;

    private FamiliaresControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
