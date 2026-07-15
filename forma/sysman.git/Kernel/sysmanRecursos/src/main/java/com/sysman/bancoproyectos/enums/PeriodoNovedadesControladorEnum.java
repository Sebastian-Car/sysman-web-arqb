/*-
 * PeriodoNovedadesControladorEnum.java
 *
 * 1.0
 * 
 * 29/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 29/09/2017
 * @author jcrodriguez
 *
 */
public enum PeriodoNovedadesControladorEnum {

    PARAM0("PARAM0");

    private final String value;

    private PeriodoNovedadesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
