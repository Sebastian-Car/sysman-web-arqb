/*
 * FcregistroejecucgastosControladorUrlEnum
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
public enum FcregistroejecucgastosControladorUrlEnum {

    URL5613("FCREGISTROEJECUCGASTOSCONTROLADORURL5613", "4001"),

    URL6015("FCREGISTROEJECUCGASTOSCONTROLADORURL6015", "7007"),

    URL8131("FCREGISTROEJECUCGASTOSCONTROLADORURL8131", "20013"),

    URL8743("FCREGISTROEJECUCGASTOSCONTROLADORURL8743", "20015"),

    URL9459("FCREGISTROEJECUCGASTOSCONTROLADORURL9459", "23010"),

    URL10065("FCREGISTROEJECUCGASTOSCONTROLADORURL10065", "23019"),

    URL6469("FCREGISTROEJECUCGASTOSCONTROLADORURL6469", "45036"), // 94050

    URL7304("FCREGISTROEJECUCGASTOSCONTROLADORURL7304", "45038"); // 94056

    private final String key;
    private final String value;

    private FcregistroejecucgastosControladorUrlEnum(String key, String value) {
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
