/*
 * FacturarLotesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FacturarLotesControladorUrlEnum {

    URL001("FACTURARLOTESCONTROLADORURL001", "665010"),

    URL5625("FACTURARLOTESCONTROLADORURL5625", "664016"),

    URL4457("FACTURARLOTESCONTROLADORURL4457", "664014"),

    URL86963("FACTURARLOTESCONTROLADORURL86963", "666011"),

    URL86964("FACTURARLOTESCONTROLADORURL86963", "104063"),

    URL815("FACTURARLOTESCONTROLADORURL815", "665023"),

    URL5897("FACTURARLOTESCONTROLADORURL815", "1837003")

    ;

    private final String key;
    private final String value;

    private FacturarLotesControladorUrlEnum(String key, String value) {
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
