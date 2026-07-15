/*
 * DeclaracionestrategicasControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfiguracionParametrosControladorUrlEnum {

    URL3006("CONFIGURACIONPARAMETROSCONTROLADORURL3006", "1058002"),

    URL3111("CONFIGURACIONPARAMETROSCONTROLADORURL3111", "105800U"),

    URL6969("CONFIGURACIONPARAMETROSCONTROLADORURL6969", "105800R"),
    
    URL2472("CONFIGURACIONPARAMETROSCONTROLADORURL2472", "1695002"),
    
    URL5412("CONFIGURACIONPARAMETROSCONTROLADORURL5412", "169500U");

    private final String key;
    private final String value;

    private ConfiguracionParametrosControladorUrlEnum(String key,
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
