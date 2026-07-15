/*
 * ConciliacionPptalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConciliacionPptalControladorUrlEnum {

    URL6339("CONCILIACIONPPTALCONTROLADORURL6339", "14048"),

    URL5616("CONCILIACIONPPTALCONTROLADORURL5616", "14036"),

    URL3592("CONCILIACIONPPTALCONTROLADORURL3592", "45018"),

    URL4546("CONCILIACIONPPTALCONTROLADORURL4546", "45020");

    private final String key;
    private final String value;

    private ConciliacionPptalControladorUrlEnum(String key, String value) {
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
