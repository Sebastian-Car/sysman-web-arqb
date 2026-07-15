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
public enum EvaluaciondocumentossubsControladorUrlEnum {

    URL5181("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL5181", "722011"),

    URL6227("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6227", "722003"),

    URL0002("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6227", "722009"),

    URL6228("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6228", "722006"),

    URL6229("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6229", "708007"),

    URL6230("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL6230", "14144"),

    URL0001("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0001", "722008"),

    URL0003("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0003", "722014"),

    URL0004("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0004", "722015"),

    URL0005("EVALUACIONDOCUMENTOSSUBSCONTROLADORURL0005", "708022");

    private final String key;
    private final String value;

    private EvaluaciondocumentossubsControladorUrlEnum(String key,
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
