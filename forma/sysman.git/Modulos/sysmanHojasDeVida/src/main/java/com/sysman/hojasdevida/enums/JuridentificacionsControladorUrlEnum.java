/*
 * CalificacionControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum JuridentificacionsControladorUrlEnum {

    URL6229("JURIDENTIFICACIONSCONTROLADORURL6229", "14148"),

    URL6230("JURIDENTIFICACIONSCONTROLADORURL6230", "61027"),

    URL6231("JURIDENTIFICACIONSCONTROLADORURL6231", "14150");

    private final String key;
    private final String value;

    private JuridentificacionsControladorUrlEnum(String key,
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
