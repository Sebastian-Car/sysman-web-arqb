/*
 * DplancompraselemsControladorEnum
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
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 */
public enum DplancompraselemsControladorEnum {

    /**
     * Par&aacute;metro CODIGOELEMENTO.
     */
    CODIGOELEMENTO("CODIGOELEMENTO"),

    /**
     * Par&aacute;metro CEDULA.
     */
    CEDULA("CEDULA"),

    /**
     * Par&aacute;metro VALOR_UNITARIO.
     */
    VALOR_UNITARIO("VALOR_UNITARIO"),

    /**
     * Par&aacute;metro NOMBRELARGO.
     */
    NOMBRELARGO("NOMBRELARGO"),

    /**
     * Par&aacute;metro VALORACOMPRAR.
     */
    VALORACOMPRAR("VALORACOMPRAR"),

    /**
     * Par&aacute;metro COMPRADO.
     */
    COMPRADO("COMPRADO"),

    /**
     * Par&aacute;metro VALORTOTALCOMPRADO.
     */
    VALORTOTALCOMPRADO("VALORTOTALCOMPRADO"),

    /**
     * Par&aacute;metro SALDO_TOTALACOMPRAR.
     */
    SALDO_TOTALACOMPRAR("SALDO_TOTALACOMPRAR"),

    /**
     * Par&aacute;metro ENTRADAS.
     */
    ENTRADAS("ENTRADAS"),

    /**
     * Par&aacute;metro SALIDAS.
     */
    SALIDAS("SALIDAS"),

    /**
     * Par&aacute;metro SALDO_CANTIDAD.
     */
    SALDO_CANTIDAD("SALDO_CANTIDAD"),

    /**
     * Par&aacute;metro MAXVALOR.
     */
    MAXVALOR("MAXVALOR"),

    /**
     * Par&aacute;metro FUENTE_DE_RECURSOS.
     */
    FUENTE_DE_RECURSOS("FUENTE_DE_RECURSOS"),

    /**
     * Par&aacute;metro NOMBREMES.
     */
    NOMBREMES("NOMBREMES"),

    /**
     * Par&aacute;metro SUMA.
     */
    SUMA("SUMA"),

    /**
     * Par&aacute;metro TB_TB3615.
     */
    TB_TB3615("TB_TB3615"),

    /**
     * Variable s$anoPC$s
     */
    VAR_ANO_PC("s$anoPC$s"),

    /**
     * Variable s$accion$s
     */
    VAR_ACCION("s$accion$s"),

    /**
     * Variable MODALIDAD_CONTRATACION.
     */
    MODALIDAD_CONTRATACION("MODALIDAD_CONTRATACION");

    private final String value;

    private DplancompraselemsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
