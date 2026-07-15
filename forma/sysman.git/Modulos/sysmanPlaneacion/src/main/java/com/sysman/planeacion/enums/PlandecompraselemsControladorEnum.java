/*
 * PlandecompraselemsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.planeacion.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeraci&oacute;n que permite clasificar cada uno de los
 * par&aacute;metros identificados en el refactoring, para ser
 * convertidos Map <String,String> y disponibles en dicha
 * enumeraci&oacute;n.
 */
public enum PlandecompraselemsControladorEnum {
    /**
     * Par&aacute;metro PLANAPROBADO.
     */
    PLANAPROBADO("PLANAPROBADO"),

    /**
     * Par&aacute;metro VLRASIGNADO.
     */
    VLRASIGNADO("VLRASIGNADO"),

    /**
     * Par&aacute;metro FUENTE_DE_RECURSOS.
     */
    FUENTE_DE_RECURSOS("FUENTE_DE_RECURSOS"),

    /**
     * Par&aacute;metro VLRADICION.
     */
    VLRADICION("VLRADICION"),

    /**
     * Par&aacute;metro VLRREDUCCION.
     */
    VLRREDUCCION("VLRREDUCCION"),

    /**
     * Par&aacute;metro VLRTRASLADO.
     */
    VLRTRASLADO("VLRTRASLADO"),

    /**
     * Par&aacute;metro VLRAPRINICIAL.
     */
    VLRAPRINICIAL("VLRAPRINICIAL"),

    /**
     * Par&aacute;metro ADICION.
     */
    ADICION("ADICION"),

    /**
     * Par&aacute;metro REDUCCION.
     */
    REDUCCION("REDUCCION"),

    /**
     * Par&aacute;metro TRASLADO.
     */
    TRASLADO("TRASLADO"),

    /**
     * Par&aacute;metro APROPIADO.
     */
    APROPIADO("APROPIADO"),

    /**
     * Par&aacute;metro VLRPROGRAMADO.
     */
    VLRPROGRAMADO("VLRPROGRAMADO"),

    /**
     * Par&aacute;metro VLRDIFERENCIA.
     */
    VLRDIFERENCIA("VLRDIFERENCIA"),

    /**
     * Par&aacute;metro VLREJECUTADO.
     */
    VLREJECUTADO("VLREJECUTADO"),

    /**
     * Par&aacute;metro ANIO.
     */
    ANIO("ANIO"),

    /**
     * Par&aacute;metro CODIGOSUBPROYECTO.
     */
    CODIGOSUBPROYECTO("CODIGOSUBPROYECTO"),

    /**
     * Par&aacute;metro SUBPROYECTO.
     */
    SUBPROYECTO("SUBPROYECTO"),

    /**
     * Par&aacute;metro APRDEFINITIVA.
     */
    APRDEFINITIVA("APRDEFINITIVA"), 
    
    COD_RUBRO("COD_RUBRO");

    private final String value;

    private PlandecompraselemsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
