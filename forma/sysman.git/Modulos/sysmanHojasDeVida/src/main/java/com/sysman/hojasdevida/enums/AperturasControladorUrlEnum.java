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
public enum AperturasControladorUrlEnum {

    URL5181("APERTURASCONTROLADORURL5181", "688001"),

    URL6227("APERTURASCONTROLADORURL6227", "463031"),

    URL4545("APERTURASCONTROLADORURL6227", "62002"),

    URL0003("APERTURASCONTROLADORURL0003", "463039"),

    // MANUAL

    URL0004("APERTURASCONTROLADORURL0004", "753017"),

    URL0101("APERTURASCONTROLADORURL0101", "14169"),

    URL0002("APERTURASCONTROLADORURL0002", "463035"),

    // Seleccionar el manual

    URL0005("APERTURASCONTROLADORURL0005", "708024");

    private final String key;
    private final String value;

    private AperturasControladorUrlEnum(String key, String value) {
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
