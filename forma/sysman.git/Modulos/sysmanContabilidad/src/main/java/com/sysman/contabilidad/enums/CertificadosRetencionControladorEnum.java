/*
 * CertificadosRetencionControladorEnum
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
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum CertificadosRetencionControladorEnum {

    PARAM3("PARAM3"),
    PARAM4("PARAM4"),
    PARAM1("PARAM1"),
    PARAM2("TERCEROINICIAL"),
    PARAM0("PARAM0"),
    PARAM9("TERCERO1"),
    PARAM5("CUENTAINICIAL"),
    FRMDIAN("FORMATO_DIAN"),
    CEDULADIAN("CEDULAFIRMACERT_DIAN"),
    NOMBREDIAN("NOMBREFIRMACERT_DIAN");
    

    private final String value;

    private CertificadosRetencionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
