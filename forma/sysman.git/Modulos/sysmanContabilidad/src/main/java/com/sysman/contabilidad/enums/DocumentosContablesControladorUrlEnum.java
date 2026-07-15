/*
 * DocumentosContablesControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DocumentosContablesControladorUrlEnum {

    URL3036("DOCUMENTOSCONTABLESCONTROLADORURL3036",
                    "15005"),
    URL3856("DOCUMENTOSCONTABLESCONTROLADORURL3856",
                    "15003"),
    URL15070("DOCUMENTOSCONTABLESCONTROLADORURL15070",
            "15070"),
    URL15072("DOCUMENTOSCONTABLESCONTROLADORURL15072",
            "15072");

    private final String key;
    private final String value;

    private DocumentosContablesControladorUrlEnum(String key, String value) {
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
