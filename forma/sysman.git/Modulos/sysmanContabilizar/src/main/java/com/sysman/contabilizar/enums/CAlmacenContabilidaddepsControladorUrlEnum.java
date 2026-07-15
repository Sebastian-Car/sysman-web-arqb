/*
 * CAlmacenContabilidaddepsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CAlmacenContabilidaddepsControladorUrlEnum {

    URL30613("CALMACENCONTABILIDADDEPSCONTROLADORURL30613",
                    " listaDEPRECDEBITOSCOMODATO = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4981\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL22019("CALMACENCONTABILIDADDEPSCONTROLADORURL22019",
                    "159005"),

    URL31582("CALMACENCONTABILIDADDEPSCONTROLADORURL31582",
                    " listaDEPRECCREDITOSCOMODATO = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4982\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL29649("CALMACENCONTABILIDADDEPSCONTROLADORURL29649",
                    " listaAjusDeprecCreditoS = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4980\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL17322("CALMACENCONTABILIDADDEPSCONTROLADORURL17322",
                    " listaCostoSalDb = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4967\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL23940("CALMACENCONTABILIDADDEPSCONTROLADORURL23940",
                    " listaAjusteDepreciacionCr = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4974\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL20131("CALMACENCONTABILIDADDEPSCONTROLADORURL20131",
                    " listaCostoSalAjCr = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4970\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL18255("CALMACENCONTABILIDADDEPSCONTROLADORURL18255",
                    " listaCostoSalCr = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4968\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL24896("CALMACENCONTABILIDADDEPSCONTROLADORURL24896",
                    " listaAjusInflaDebitoS = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4975\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL22976("CALMACENCONTABILIDADDEPSCONTROLADORURL22976",
                    " listaAjusteDepreciacionDb = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4973\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL12594("CALMACENCONTABILIDADDEPSCONTROLADORURL12594",
                    " listaAjusInflaCredito = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4962\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL13538("CALMACENCONTABILIDADDEPSCONTROLADORURL13538",
                    " listaDeprecDebito = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4963\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL16381("CALMACENCONTABILIDADDEPSCONTROLADORURL16381",
                    " listaAjusDeprecCredito = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4966\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL25850("CALMACENCONTABILIDADDEPSCONTROLADORURL25850",
                    " listaAjusInflaCreditoS = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4976\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL21074("CALMACENCONTABILIDADDEPSCONTROLADORURL21074",
                    " listaDepAcumuladaDb = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4971\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL19192("CALMACENCONTABILIDADDEPSCONTROLADORURL19192",
                    " listaCostoSalAjDb = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4969\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL26797("CALMACENCONTABILIDADDEPSCONTROLADORURL26797",
                    " listaDeprecDebitoS = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4977\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL15427("CALMACENCONTABILIDADDEPSCONTROLADORURL15427",
                    " listaAjusDeprecDebito = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4965\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL28692("CALMACENCONTABILIDADDEPSCONTROLADORURL28692",
                    " listaAjusDeprecDebitoS = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4979\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL14479("CALMACENCONTABILIDADDEPSCONTROLADORURL14479",
                    " listaDeprecCredito = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4964\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL11643("CALMACENCONTABILIDADDEPSCONTROLADORURL11643",
                    "16118"),

    URL32528("CALMACENCONTABILIDADDEPSCONTROLADORURL32528",
                    " listaDebCuantia = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4983\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL33461("CALMACENCONTABILIDADDEPSCONTROLADORURL33461",
                    " listaCreCuantia = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4984\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\","),

    URL27741("CALMACENCONTABILIDADDEPSCONTROLADORURL27741",
                    " listaDeprecCreditoS = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1498_nuevo:TBCB4978\", \"SELECT PLAN_CONTABLE.CODIGO,\" + \" PLAN_CONTABLE.NOMBRE\" + \" FROM PLAN_CONTABLE\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = anio\" + \" AND (MOVIMIENTO+MAN_CEN_CTO+MAN_AUX_TER+MAN_AUX_GEN) <>0\" + \" ORDER BY CODIGO\",");

    private final String key;
    private final String value;

    private CAlmacenContabilidaddepsControladorUrlEnum(String key,
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
