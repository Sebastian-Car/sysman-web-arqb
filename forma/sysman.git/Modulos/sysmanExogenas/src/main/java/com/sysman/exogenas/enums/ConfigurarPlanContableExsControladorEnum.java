/*
 * TarifasfgControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.exogenas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ConfigurarPlanContableExsControladorEnum {

    FORMATO("FORMATO"),

    CLASE_EXOGENA("CLASE_EXOGENA"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_ANO("KEY_ANO"),

    KEY_FORMATO("KEY_FORMATO"),

    KEY_CONCEPTO("KEY_CONCEPTO"),

    KEY_CUENTA("KEY_CUENTA"),

    EXDISTRITAL("EXDISTRITAL"),

    RETEPRACTICADA("RETEPRACTICADA"),

    RETEASUMIDA("RETEASUMIDA"),

    IVACOMUN("IVACOMUN"),

    IVASIMPLIFICADO("IVASIMPLIFICADO"),

    RETEICA("RETEICA"),

    MOSTRARF1001("MOSTRARF1001"),

    PREFIJO("PREFIJO"),

    IND_AGENTE_RETENCION("IND_AGENTE_RETENCION"),

    IND_SUJETO_RETENCION("IND_SUJETO_RETENCION"),
    
    IND_DETALLE("IND_DETALLE");

    private final String value;

    private ConfigurarPlanContableExsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
