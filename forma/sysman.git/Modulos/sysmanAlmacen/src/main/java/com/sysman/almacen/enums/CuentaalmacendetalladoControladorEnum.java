/*-
 * CuentaalmacendetalladoControladorEnum.java
 *
 * 1.0
 * 
 * 18/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * 
 * @version 1.0, 18/09/2017
 * @author jcrodriguez
 *
 */
public enum CuentaalmacendetalladoControladorEnum {
    REPORTE000641("000641CuentaAlmacenElemento"),

    TG_TELEFONO3("TG_TELEFONO3"),

    TG_DIRECCION4("TG_DIRECCION4"),

    NIT("NIT"),

    COMPANIASELL("COMPANIASEL"),

    NITCOMPANIA("NITCOMPANIA");

    private final String value;

    private CuentaalmacendetalladoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
