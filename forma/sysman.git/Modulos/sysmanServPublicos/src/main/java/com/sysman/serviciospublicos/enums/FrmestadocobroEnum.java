/*
 * FrmestadocobroEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum FrmestadocobroEnum {
    SP_ESTADOSCOBRO("SP_ESTADOSCOBRO"),
    REPORTE001087("001087INFEstadoCobroIng"),
    REPORTE001086("001086INFEstadoCobro"),
    ESTADOS("ESTADOS"),
    INF_ESTADOCOBRO("INF_EstadoCobro"),
    TB_TB1626("TB_TB1626"),
    TB_TB1627("TB_TB1627"),
    INF_ESTADOCOBROING("INF_EstadoCobroIng"),
    PR_LISTADO("PR_LISTADO"),
    PR_CICLO("PR_CICLO");

    private final String value;

    private  FrmestadocobroEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
