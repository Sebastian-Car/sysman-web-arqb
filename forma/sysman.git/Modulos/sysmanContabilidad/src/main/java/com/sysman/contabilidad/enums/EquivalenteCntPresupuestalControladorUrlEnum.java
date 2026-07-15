/*
 * EquivalenteCntPresupuestalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EquivalenteCntPresupuestalControladorUrlEnum {

    URL10445("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL10445",
                    "16118"),

    URL12604("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL12604",
                    " listaCuentaCreditoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1829:TBCB6037\", \"SELECT \" + \" PLAN_CONTABLE.ID, \" + \" PLAN_CONTABLE.NOMBRE \" + \" FROM \" + \" PLAN_CONTABLE \" + \" WHERE \" + \" (\" + \" ((PLAN_CONTABLE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_CONTABLE.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_CONTABLE.MAN_CEN_CTO) <> 0)\" + \" ) \" + \" OR \" + \" (\" + \" ((PLAN_CONTABLE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_CONTABLE.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_CONTABLE.MAN_AUX_TER) <> 0)\" + \" ) \" + \" OR \" + \" (\" + \" ((PLAN_CONTABLE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_CONTABLE.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_CONTABLE.MAN_AUX_GEN) <> 0)\" + \" ) \" + \" ORDER BY \" + \" PLAN_CONTABLE.ID \" + \" \","),

    URL16290("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL16290",
                    " listaRubroPptalE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1829:TBCB6038\", \"SELECT \" + \" PLAN_PRESUPUESTAL.ID, \" + \" PLAN_PRESUPUESTAL.NOMBRE, \" + \" PLAN_PRESUPUESTAL.MOVIMIENTO \" + \" FROM \" + \" PLAN_PRESUPUESTAL \" + \" WHERE \" + \" (\" + \" ((PLAN_PRESUPUESTAL.MOVIMIENTO) = TRUE) \" + \" AND \" + \" ((PLAN_PRESUPUESTAL.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_PRESUPUESTAL.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_PRESUPUESTAL.NATURALEZA) = 'D')\" + \" ) \" + \" ORDER BY \" + \" PLAN_PRESUPUESTAL.COMPANIA, \" + \" PLAN_PRESUPUESTAL.ANO, \" + \" PLAN_PRESUPUESTAL.ID \" + \" \","),

    URL4993("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL4993",
                    "4001"),

    URL8028("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL8028",
                    " listaCuentaDebitoE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR1829:TBCB6036\", \"SELECT \" + \" PLAN_CONTABLE.ID, \" + \" PLAN_CONTABLE.NOMBRE, \" + \" PLAN_CONTABLE.MAN_CEN_CTO, \" + \" PLAN_CONTABLE.MAN_AUX_TER, \" + \" PLAN_CONTABLE.MAN_AUX_GEN \" + \" FROM \" + \" PLAN_CONTABLE \" + \" WHERE \" + \" (\" + \" ((PLAN_CONTABLE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_CONTABLE.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_CONTABLE.MAN_CEN_CTO) <> 0)\" + \" ) \" + \" OR \" + \" (\" + \" ((PLAN_CONTABLE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_CONTABLE.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_CONTABLE.MAN_AUX_TER) <> 0)\" + \" ) \" + \" OR \" + \" (\" + \" ((PLAN_CONTABLE.COMPANIA) = '\" + compania + \"') \" + \" AND \" + \" ((PLAN_CONTABLE.ANO) = GETYEAR()) \" + \" AND \" + \" ((PLAN_CONTABLE.MAN_AUX_GEN) <> 0)\" + \" ) \" + \" ORDER BY \" + \" PLAN_CONTABLE.ID, \" + \" PLAN_CONTABLE.NOMBRE \" + \" \","),

    URL5613("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL5613",
                    "16118"),

    URL14757("EQUIVALENTECNTPRESUPUESTALCONTROLADORURL14757",
                    "1031025"); //45061

    private final String key;
    private final String value;

    private EquivalenteCntPresupuestalControladorUrlEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
