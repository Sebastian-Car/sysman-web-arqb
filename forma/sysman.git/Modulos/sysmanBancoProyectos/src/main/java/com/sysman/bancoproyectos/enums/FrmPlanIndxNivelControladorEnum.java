/*
 * FrmPlanIndxNivelControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmPlanIndxNivelControladorEnum {
    TG_NO_EXISTE("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"),

    MSM_INFORME_NO_EXISTE("MSM_INFORME_NO_EXISTE"),

    REPORTE000217("000217rptgrafica"),

    REPORTE000182("000182RptPlanAvancecriterio"),

    PR_STRSQL("PR_STRSQL"),

    PR_VIGENCIA("PR_VIGENCIA"),

    ANIO("ANIO"),

    NIVEL("NIVEL"),

    TB_TB1766("TB_TB1766"),

    PR_NIVEL("PR_NIVEL"),

    DIGITOS("DIGITOS"),

    PR_TITULO_PLAN_DE_DESARROLLO("PR_TITULO_PLAN_DE_DESARROLLO"),

    PR_DEPENDENCIA_DE_BANCO_DE_PROYECTOS("PR_DEPENDENCIA_DE_BANCO_DE_PROYECTOS"),

    PR_CIUDADCOMPANIA("PR_CIUDADCOMPANIA"),

    REPORTE000178("000178RptPlanxNivel"),

    PR_DEPARTAMENTOCOMPANIA("PR_DEPARTAMENTOCOMPANIA");

    private final String value;

    private FrmPlanIndxNivelControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
