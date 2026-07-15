/*
 * ConfigurarcuentasdeudasControladorUrlEnum
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
public enum ConfigurarcuentasdeudasControladorUrlEnum {

    
    URL5565("CONFIGURARCUENTASDEUDASCONTROLADORURL5565",
                    "1031013"),
    
    URL5250("CONFIGURARCUENTASDEUDASCONTROLADORURL5250",
                    "4001"),
    
    URL19563("CONFIGURARCUENTASDEUDASCONTROLADORURL19563",
                    "1690006"),
    
    
    URL87541("CONFIGURARCUENTASDEUDASCONTROLADORURL87541",
                    "1690006"),
    
    URL5568("CONFIGURARCUENTASDEUDASCONTROLADORURL5568",
                    "1031015");

    private final String key;
    private final String value;

    private ConfigurarcuentasdeudasControladorUrlEnum(String key,
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
