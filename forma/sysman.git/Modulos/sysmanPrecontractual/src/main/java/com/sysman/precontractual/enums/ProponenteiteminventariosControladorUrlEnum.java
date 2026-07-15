/*
 * ProponenteiteminventariosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ProponenteiteminventariosControladorUrlEnum {

    URL8648("PROPONENTEITEMINVENTARIOSCONTROLADORURL8648", "533002"),

    URL9602("PROPONENTEITEMINVENTARIOSCONTROLADORURL9602", "533001"),

    URL12833("PROPONENTEITEMINVENTARIOSCONTROLADORURL12833", "533005"),

    URL17652("PROPONENTEITEMINVENTARIOSCONTROLADORURL17652", "520006"),

    URL17653("PROPONENTEITEMINVENTARIOSCONTROLADORURL17653", "533006");

    private final String key;
    private final String value;

    private ProponenteiteminventariosControladorUrlEnum(String key,
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
