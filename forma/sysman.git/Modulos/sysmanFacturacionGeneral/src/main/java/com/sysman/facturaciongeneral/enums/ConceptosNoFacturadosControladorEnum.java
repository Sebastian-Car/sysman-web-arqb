/*
 * ConceptosNoFacturadosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos
 * Map<String,String> y disponibles en dicha enumeración.
 */
public enum ConceptosNoFacturadosControladorEnum {

    IMPRESO("IMPRESO"),

    FECHA_SOLICITUD("FECHA_SOLICITUD"),

    NOMBREAUXILIAR("NOMBREAUXILIAR"),

    NOMBRETERCERO("NOMBRETERCERO"),

    PORCENTAJEIVA("PORCENTAJEIVA"),

    CUENTA_RECAUDO("CUENTA_RECAUDO"),

    VALOR_UNIDAD("VALOR_UNIDAD"),

    PERMITEMODIFICARVALOR("PERMITEMODIFICARVALOR"),

    EXISTENCIA("EXISTENCIA"),

    CODIGO_COBRO("CODIGO_COBRO"),

    BASE_FIJA("BASE_FIJA"),

    VALOR_BASE("VALOR_BASE"),

    VALOR_COMPRA("VALOR_COMPRA"),

    VALOR_UNITARIO("VALOR_UNITARIO"),

    CONTRATO("CONTRATO"),

    VALOR_NETO("VALOR_NETO"),

    VALOR_DESCUENTO("VALOR_DESCUENTO"),

    VALOR_IVA("VALOR_IVA"),

    VALOR_RETEFUENTE("VALOR_RETEFUENTE"),

    VALOR_ICA("VALOR_ICA"),

    NOMBRECONCEPTO("NOMBRECONCEPTO"),

    CANTIDAD_GRUPO("CANTIDAD_GRUPO"),

    CREE("CREE"),

    CONCEPTO("CONCEPTO"),

    TIPOCOBRO("TIPOCOBRO");

    private final String value;

    private ConceptosNoFacturadosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
