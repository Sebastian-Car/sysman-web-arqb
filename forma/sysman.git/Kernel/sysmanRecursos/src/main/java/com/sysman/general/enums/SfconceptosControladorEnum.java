/*
 * FrmInfGruposConceptosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SfconceptosControladorEnum {

    DIGITOS_REDONDEO("DIGITOS_REDONDEO"),

    CANTIDAD_PORDEFECTO("CANTIDAD_PORDEFECTO"),

    RIDCONCEPTO("ridConcepto"),

    APLICARETEFUENTE("APLICARETEFUENTE"),

    APLICAICA("APLICAICA"),

    APLICAIVA("APLICAIVA"),

    APLICADESCUENTO("APLICADESCUENTO"),

    APLICAFORMULA("APLICAFORMULA"),

    CUENTA_RECAUDO("CUENTA_RECAUDO"),

    PORCETAJE_UTILIDAD("PORCETAJE_UTILIDAD"),

    PORCENTAJERETEFUENTE("PORCENTAJERETEFUENTE"),

    PORCENTAJEIVA("PORCENTAJEIVA"),

    PORCENTAJEICA("PORCENTAJEICA"),

    FORMULA("FORMULA"),

    PORCENTAJEDESCUENTO("PORCENTAJEDESCUENTO"),

    CLASECUENTA("CLASECUENTA"),

    ID("ID"),

    TIPOCOBRO("TIPOCOBRO"),

    NOMBRECENTRO("NOMBRECENTRO"),

    NOMBREAUXILIAR("NOMBREAUXILIAR"),

    FACTOR_RED_BASE("FACTOR_RED_BASE"),

    FACTOR_RED_IVA("FACTOR_RED_IVA"),

    FACTOR_RED_BASETOTAL("FACTOR_RED_BASETOTAL"),

    FACTOR_RED_ICA("FACTOR_RED_ICA"),

    APLICAIMPOCONSUMO("APLICAIMPOCONSUMO"),
    
    APLICAAUTORENTA("APLICAAUTORENTA"),
    
    APLICAAUTOICA("APLICAAUTOICA"),
    
    PORCENTAJEAUTORENTA("PORCENTAJEAUTORENTA"),
    
    PORCENTAJEAUTOICA("PORCENTAJEAUTOICA");

    private final String value;

    private SfconceptosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
