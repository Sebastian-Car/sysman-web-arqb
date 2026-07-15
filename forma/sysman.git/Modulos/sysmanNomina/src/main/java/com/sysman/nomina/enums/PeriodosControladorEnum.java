/*-
 * PeriodosControladorEnum.java
 *
 * 1.0
 * 
 * 18/10/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 18/10/2017
 * @author jcrodriguez
 *
 */
public enum PeriodosControladorEnum {
    NOMBRE_PROCESO("NOMBRE_PROCESO"),

    REPORTE001475("001475IMPRIMIRFORMATONOMINA"),

    DIAS("DIAS"),

    RETROACTIVO("RETROACTIVO"),

    VACACIONES("VACACIONES"),

    FORMATO("dd/MM/yyyy"),

    NOM_PERIODO("NOM_PERIODO"),

    FECHAINICIO("FECHAINICIO"),

    FECHAFINAL("FECHAFINAL"),

    ESTADO("ESTADO"),

    DIFERIDOS("DIFERIDOS"),

    ACUMULADO("ACUMULADO"),

    ID_DE_PROCESO("ID_DE_PROCESO"),

    PROCESO_SESION("PROCESO_SESION");

    private final String value;

    private PeriodosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
