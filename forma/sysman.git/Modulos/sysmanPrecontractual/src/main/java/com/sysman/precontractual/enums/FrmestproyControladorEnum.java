/*
 * FrmestpreviounspscsControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmestproyControladorEnum {

    CANTIDAD_EJE("CANTIDAD_EJE"),

    COD_PROY("COD_PROY"),

    VISUALIZAR("visualizar"),

    COMPONENTE("COMPONENTE"),

    COSTOTOTAL("COSTOTOTAL"),

    COSTOUNITARIO("COSTOUNITARIO"),

    DESCRIPCION_META("DESCRIPCION_META"),

    NOMBREACTIVIDAD("NOMBREACTIVIDAD"),

    NOMBRECOMPONENTE("NOMBRECOMPONENTE"),

    NOMBREPROYECTO("NOMBREPROYECTO"),

    NOMBRETIPO("NOMBRETIPO"),

    PORCEJECUTADO("PORCEJECUTADO"),

    T_COMPONENTE("T_COMPONENTE"),

    VALOREJECUTADO("VALOREJECUTADO"),

    VALORPROGRAMADO("VALORPROGRAMADO"),

    VALOR_SOLICITADO_ACTIV("VALOR_SOLICITADO_ACTIV"),

    ESCREADORLOWER("esCreador"),

    CANTIDAD_META("CANTIDAD_META"),

    COD_INDICADOR("COD_INDICADOR"),

    RIDLOWER("rid"),

    RIDMETASLOWER("ridMetas"),

    TXT_COD_ESTUDIOLOWER("txtCodEstudio"),

    VIGENCIA_PERIODOLOWER("vigenciaPeriodo"),

    VOBOLOWER("voBo"),

    DEPENDENCIALOWER("dependencia"),

    METASLOWER("metas"),

    TIPO_COMPONENTE("TIPO_COMPONENTE"),

    ES_METAS_PI("ES_METAS_PI"),

    TIPO_COMPROBANTE("TIPO_COMPROBANTE"),

    TIPOCOMPONENTE("TIPOCOMPONENTE"),

    TIPO_META("TIPO_META"),

    VIGENCIA_INICIAL("VIGENCIA_INICIAL"),

    MANEJA_PLAN_DE_ACCION("MANEJA PLAN DE ACCION"),

    TIPOMETA("TIPOMETA"),

    TCOMPONENTELOWER("tComponente"),

    CODPROYLOWER("codProy"),

    COMPONENTELOWER("componente"),

    ACTIVIDADLOWER("actividad"),

    COD_ESTUDIOLOWER("codEstudio")

    ;

    private final String value;

    private FrmestproyControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
