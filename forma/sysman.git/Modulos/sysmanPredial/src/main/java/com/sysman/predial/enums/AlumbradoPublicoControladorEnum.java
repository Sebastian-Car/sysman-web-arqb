/*-
 * AlumbradoPublicoControladorEnum.java
 *
 * 1.0
 * 
 * 21/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial.enums;

/**
 * 
 * @version 1.0, 21/06/2017
 * @author jcrodriguez
 *
 */
public enum AlumbradoPublicoControladorEnum {
    CONCEPTO("CONCEPTO DE ALUMBRADO"), PR_FECHAFINAL("PR_FECHAFINAL"), FECHAFINAL("fechaFinal"), FECHAINICIAL(
                    "fechaInicial"), REPORTE000826(
                                    "000826ALUMBRADODISCRIMINADOREC"), REPORTE000825(
                                                    "000825ALUMBRADODISCRIMINADOCAU"), FORMATOFECHA("dd/MM/yyyy");
    private final String value;

    private AlumbradoPublicoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
