/*
 * SubbpplanindicativometasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubbpplanindicativometasControladorUrlEnum {

    URL001("SUBBPPLANINDICATIVOMETASCONTROLADORURL001", "433012"),

    URL002("SUBBPPLANINDICATIVOMETASCONTROLADORURL002", "552022"),

    URL003("SUBBPPLANINDICATIVOMETASCONTROLADORURL003", "552023"),

    URL004("SUBBPPLANINDICATIVOMETASCONTROLADORURL004", "433014"),

    URL005("SUBBPPLANINDICATIVOMETASCONTROLADORURL005", "552025"),

    URL006("SUBBPPLANINDICATIVOMETASCONTROLADORURL006", "433015"),

    URL6268("SUBBPPLANINDICATIVOMETASCONTROLADORURL6268", "560001"),

    URL5845("SUBBPPLANINDICATIVOMETASCONTROLADORURL5845", "4001"),

    URL6708("SUBBPPLANINDICATIVOMETASCONTROLADORURL6708", "4047"),

    URL7145("SUBBPPLANINDICATIVOMETASCONTROLADORURL7145", "552018");

    private final String key;
    private final String value;

    private SubbpplanindicativometasControladorUrlEnum(String key,
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
