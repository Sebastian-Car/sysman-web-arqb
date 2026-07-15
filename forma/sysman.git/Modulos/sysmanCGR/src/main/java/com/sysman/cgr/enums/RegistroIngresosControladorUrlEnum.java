/*
 * RegistroIngresosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RegistroIngresosControladorUrlEnum {

    URL11602("REGISTROINGRESOSCONTROLADORURL11602", "7007"),

    URL12203("REGISTROINGRESOSCONTROLADORURL12203", "7012"),

    URL10998("REGISTROINGRESOSCONTROLADORURL10998", "45004"),

    URL10132("REGISTROINGRESOSCONTROLADORURL10132", "45002"),

    URL9723("REGISTROINGRESOSCONTROLADORURL9723", "4001");

    private final String key;
    private final String value;

    private RegistroIngresosControladorUrlEnum(String key, String value) {
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
