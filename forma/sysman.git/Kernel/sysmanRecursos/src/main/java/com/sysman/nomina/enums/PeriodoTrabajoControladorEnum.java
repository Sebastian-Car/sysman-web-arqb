/*
 * PeriodoTrabajoControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum PeriodoTrabajoControladorEnum {

    NOM_PERIODO("NOM_PERIODO"),

    NOMPERNOMINA("nombrePeriodoNomina"),

    PERNOMINA("periodoNomina"),

    NOMBREMESNOMINA("NOMBRE_MES"),

    NOMBREMES("nombreMesNomina"),

    MESNOMINA("mesNomina"),

    ANIONOMINA("anioNomina"),

    NOMBRE_PROCESO("NOMBRE_PROCESO"),

    NOMPROCESONOMINA("nombreProcesoNomina"),

    PROCESONOMINA("procesoNomina"),

    MENUNOMINA("/menu.sysman");

    private final String value;

    private PeriodoTrabajoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
