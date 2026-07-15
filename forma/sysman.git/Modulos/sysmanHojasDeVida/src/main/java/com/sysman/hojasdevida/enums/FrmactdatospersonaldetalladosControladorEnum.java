/*
 * CalificacionControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmactdatospersonaldetalladosControladorEnum {

    CONSECUTIVO("CONSECUTIVO"),

    IDEMPLEADO("IDEMPLEADO"),

    ACCION("ACCION"),

    ACTUALIZADO("ACTUALIZADO"),

    ENVIADO("ENVIADO"),

    TIPO("TIPO"),

    PAIS_HAB("PAIS_HAB"),

    SUCURSAL("SUCURSAL"),

    DEPARTAMENTO_HAB("DEPARTAMENTO_HAB"),

    AUTORIZACION("AUTORIZACION"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    NOMBRE_ARCHIVO("NOMBRE_ARCHIVO"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_CODIGO_DOCUMENTO("KEY_CODIGO_DOCUMENTO"),

    KEY_AUTORIZACION("KEY_AUTORIZACION"),

    KEY_ID_DE_EMPLEADO("KEY_ID_DE_EMPLEADO"),

    CODIGO_DOCUMENTO("CODIGO_DOCUMENTO"),

    OBSERVACION("OBSERVACION"),

    AUTPERSONAL_DOCUMENTO("AUTPERSONAL_DOCUMENTO");

    private final String value;

    private FrmactdatospersonaldetalladosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
