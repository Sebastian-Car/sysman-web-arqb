/*
 * RelcontratosfechasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RelcontratosfechasControladorUrlEnum {

    URL5158("RELCONTRATOSFECHASCONTROLADORURL5158", "73031"),

    URL7562("RELCONTRATOSFECHASCONTROLADORURL7562", "73032"),

    URL5811("RELCONTRATOSFECHASCONTROLADORURL5811", "14040"),

    URL4182("RELCONTRATOSFECHASCONTROLADORURL4182", "14090"),

    URL6843("RELCONTRATOSFECHASCONTROLADORURL6842", "62015"),// "62034"),

    URL6842("RELCONTRATOSFECHASCONTROLADORURL6842",  "62013");//"62036");

    private final String key;
    private final String value;

    private RelcontratosfechasControladorUrlEnum(String key, String value) {
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
