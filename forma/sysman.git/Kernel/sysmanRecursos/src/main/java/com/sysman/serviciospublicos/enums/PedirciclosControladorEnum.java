/*
 * PedirciclosControladorEnum
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
public enum PedirciclosControladorEnum {
    ANIO("ANIO"),
    ANO("ANO"),
    SI("SI"),
    TASARECARGO("TASARECARGO"),
    PREFACTURANDO("PREFACTURANDO"),
    TB_TB3034("TB_TB3034"),
    TB_TB1678("TB_TB1678"),
    TB_TB1679("TB_TB1679"),
    TB_TB1667("TB_TB1667"),
    TB_TB3156("TB_TB3156"),
    FORMULARIO1246("1246"),
    FORMULARIO1264("1264"),
    FORMULARIO1127("1127"),
    FORMULARIO1109("1109"),
    FORMULARIO1241("1241"),
    FORMULARIO1046("1046"),
    FORMULARIO1097("1097"),
    FORMULARIO1240("1240"),
    FORMULARIO1107("1107"),
    FORMULARIO1078("1263"),
    FORMULARIO1244("1244"),
    FORMULARIO1123("1123"),
    FORMULARIO1095("1095"),
    FORMULARIO1121("1121"),
    INDPREPARADO("INDPREPARADO"),
    INDBLOQUEOMANUAL("INDBLOQUEOMANUAL"),
    PR_PERIODOATRASO("PERIODOS ATRASO PARA CAMBIO FECHA SUPENSION"),
    PARAMETRO("PARAMETRO"),
    PERIODOINICIAL("PERIODOINICIAL"),
    ANOINICIAL("ANOINICIAL");

    private final String value;

    private  PedirciclosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
