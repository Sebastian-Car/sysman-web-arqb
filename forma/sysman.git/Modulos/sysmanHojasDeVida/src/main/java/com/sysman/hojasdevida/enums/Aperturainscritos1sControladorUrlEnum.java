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
public enum Aperturainscritos1sControladorUrlEnum {

    URL6229("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6229", "708007"),

    URL0004("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0004", "708022"),

    URL6230("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6230", "14146"),

    URL6231("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6231", "14164"),

    URL5959("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL5959", "14168"),

    URL0001("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0001", "722007"),

    URL0002("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0002", "688002"),

    URL0003("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0003", "722013"),
    
    URL4970("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL4970", "722018");

    private final String key;
    private final String value;

    private Aperturainscritos1sControladorUrlEnum(String key,
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
