/*
 * CuentasControladorEnum
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
public enum CuentasControladorEnum {
    RUTA("RUTA"),

    CIUDAD("CIUDAD"),

    FORMULARIO("FORMULARIO"),

    NOMBRE_TERCERO("NOMBRE_TERCERO"),

    NOMBRE_DEPENDENCIA("NOMBRE_DEPENDENCIA"),

    NOMBRE_COMPANIA("NOMBRE_COMPANIA"),

    NIVEL_GRUPO("NIVEL_GRUPO"),

    APLICACION("APLICACION"),

    PAIS("PAIS"),

    MSM_REGISTRO_ELIMINADO("MSM_REGISTRO_ELIMINADO"),

    MSM_REGISTRO_INGRESADO("MSM_REGISTRO_INGRESADO"),

    MSM_REGISTRO_MODIFICADO("MSM_REGISTRO_MODIFICADO"),

    CEDULA("CEDULA"),

    CODIGO_FORMULARIO("CODIGO_FORMULARIO"),

    DESCRIPCION_FORMULARIO("DESCRIPCION_FORMULARIO"),

    GRUPO("GRUPO"),

    NOMBRE_FORMULARIO("NOMBRE_FORMULARIO"),

    PASSWORD("PASSWORD"),

    REGION("REGION"),

    TIPOCUENTA("TIPOCUENTA"),

    DEPARTAMENTO("DEPARTAMENTO"),
    
    ACCESODENEGADO("ACCESODENEGADO"),
    
    CODIGO("CODIGO"),
    
    KEY_COMPANIA("KEY_COMPANIA"),
    
    KEY_TIPO("KEY_TIPO"),
    
    KEY_USUARIO("KEY_USUARIO"),
    
    KEY_APLICACION("KEY_APLICACION"),
    
    VER("VER");

    private final String value;

    private CuentasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
