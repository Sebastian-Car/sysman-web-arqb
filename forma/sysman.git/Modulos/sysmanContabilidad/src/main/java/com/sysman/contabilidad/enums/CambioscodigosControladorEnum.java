/*
 * CambioscodigosControladorEnum
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
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum CambioscodigosControladorEnum {


    STRNUMERO("STRNUMERO"),
    ANIO("ANIO"),
    CONSECUTIVO("CONSECUTIVO"),
    MSM_REGISTRO_INGRESADO("MSM_REGISTRO_INGRESADO"),
    MSM_REGISTRO_MODIFICADO("MSM_REGISTRO_MODIFICADO"),
    TIPO("TIPO"),
    CODIGOANTERIOR("CODIGOANTERIOR"),
    REALIZADO("REALIZADO"),
    CODIGONUEVO("CODIGONUEVO"),
    NOMBREANTERIOR("NOMBREANTERIOR"),
    NOMBRENUEVO("NOMBRENUEVO"),
    KEY_COMPANIA("KEY_COMPANIA"),
    KEY_ANO("KEY_ANO"),
    KEY_NUMERO("KEY_NUMERO"),
    KEY_CONSECUTIVO("KEY_CONSECUTIVO"),
    KEY_TIPO("KEY_TIPO"),
    RECLASIFICAR_NIIF("RECLASIFICAR_NIIF"),
    D_RECLASIFICAR_NIIF("D_RECLASIFICAR_NIIF"),
    MSM_REGISTRO_ELIMINADO("MSM_REGISTRO_ELIMINADO");

    private final String value;

    private  CambioscodigosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
