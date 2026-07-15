/*
 * ResumenAportesAutoControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ResumenAportesAutoControladorUrlEnum {

    URL5072("RESUMENAPORTESAUTOCONTROLADORURL5072",
                    "471039"),

    URL4631("RESUMENAPORTESAUTOCONTROLADORURL4631",
                    "471066"),

    URL5555("RESUMENAPORTESAUTOCONTROLADORURL5555",
                    "537008"),

    URL6666("RESUMENAPORTESAUTOCONTROLADORURL6666",
                    "471046");

    private final String key;
    private final String value;

    private ResumenAportesAutoControladorUrlEnum(String key, String value) {
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
