/*
 * BpplanindicativosControladorEnum
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
public enum BpplanindicativosControladorEnum {

    TIPO_META_PLAN("TIPO_META_PLAN"),

    VIGENCIA_LOWER("vigencia"),

    ID("ID"),

    RID_LOWER("rid"),

    VIGENCIA_INICIAL("VIGENCIA_INICIAL"),

    META_RESUL("META_RESUL"),

    META_PRODUC("META_PRODUC"),

    MANEJA_DEPEN("MANEJA_DEPEN"),

    RID("RID"),

    DEPENDIENTE_LOWER("dependiente"),

    CODIGO_META_LOWER("codigoMeta"),

    TRUE_LOWER("true"),

    PONDERACION("PONDERACION"),

    AVANCE("AVANCE"),

    AVANCE_FINANCIERO("AVANCE_FINANCIERO"),

    UNIDAD_MEDIDA("UNIDAD_MEDIDA"),

    META("META"),

    LB("LB"),

    No("No."),

    TIPO_META_INDICADOR("TIPO_META_INDICADOR")

    ;

    private final String value;

    private BpplanindicativosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
