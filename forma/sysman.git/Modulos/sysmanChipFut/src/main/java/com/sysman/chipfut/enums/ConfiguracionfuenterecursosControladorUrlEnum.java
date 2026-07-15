/*
 * ConfiguracionfuenterecursosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfiguracionfuenterecursosControladorUrlEnum {

    URL7996("CONFIGURACIONFUENTERECURSOSCONTROLADORURL7996",
                    "1686003"),

    URL13469("CONFIGURACIONFUENTERECURSOSCONTROLADORURL13469",
                    "34050"),

    URL6785("CONFIGURACIONFUENTERECURSOSCONTROLADORURL6785",
                    "4072"),

    URL7330("CONFIGURACIONFUENTERECURSOSCONTROLADORURL7330",
                    "1686003"),

    URL6331("CONFIGURACIONFUENTERECURSOSCONTROLADORURL6331",
                    "4001"),

    URL9780("CONFIGURACIONFUENTERECURSOSCONTROLADORURL9780",
                    "34049"),

    URL9781("CONFIGURACIONFUENTERECURSOSCONTROLADORURL9781",
                    "34051"),

    URL9782("CONFIGURACIONFUENTERECURSOSCONTROLADORURL9782",
                    "34053"),

    URL9783("CONFIGURACIONFUENTERECURSOSCONTROLADORURL9783",
                    "34054"),

    URL9784("CONFIGURACIONFUENTERECURSOSCONTROLADORURL9784",
                    "3400D")

    ;

    private final String key;
    private final String value;

    private ConfiguracionfuenterecursosControladorUrlEnum(String key,
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
