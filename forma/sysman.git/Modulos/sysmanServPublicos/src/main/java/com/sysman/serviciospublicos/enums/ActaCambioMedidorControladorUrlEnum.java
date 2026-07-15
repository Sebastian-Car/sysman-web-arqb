/*
 * ActaCambioMedidorControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ActaCambioMedidorControladorUrlEnum {

    URL6417("ACTACAMBIOMEDIDORCONTROLADORURL6417", "214007"),

    URL6418("ACTACAMBIOMEDIDORCONTROLADORURL6418", "104008"),

    URL6419("ACTACAMBIOMEDIDORCONTROLADORURL6419", "213004"),

    URL6420("ACTACAMBIOMEDIDORCONTROLADORURL6420", "213006"),

    URL6421("ACTACAMBIOMEDIDORCONTROLADORURL6421", "234024"),

    URL6422("ACTACAMBIOMEDIDORCONTROLADORURL6422", "234003"),

    URL6423("ACTACAMBIOMEDIDORCONTROLADORURL6423", "235001"),

    URL6424("ACTACAMBIOMEDIDORCONTROLADORURL6424", "235002")

    ;

    private final String key;
    private final String value;

    private ActaCambioMedidorControladorUrlEnum(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
