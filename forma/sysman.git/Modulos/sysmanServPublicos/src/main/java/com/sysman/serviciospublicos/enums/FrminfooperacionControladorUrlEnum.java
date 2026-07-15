/*
 * FrminfooperacionControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrminfooperacionControladorUrlEnum {

    URL0001("FRMINFOOPERACIONCONTROLADORURL0001", "307015"),

    URL0002("FRMINFOOPERACIONCONTROLADORURL0002", "30700C"), // 307013

    URL0003("FRMINFOOPERACIONCONTROLADORURL0003", "30700D"),

    URL0004("FRMINFOOPERACIONCONTROLADORURL0004", "307017"),

    URL0005("FRMINFOOPERACIONCONTROLADORURL0005", "301009"),

    URL0006("FRMINFOOPERACIONCONTROLADORURL0006", "307018"),

    URL0007("FRMINFOOPERACIONCONTROLADORURL0007", "307019"),

    URL0008("FRMINFOOPERACIONCONTROLADORURL0008", "213210"),

    URL0009("FRMINFOOPERACIONCONTROLADORURL0009", "104047"),

    URL0010("FRMINFOOPERACIONCONTROLADORURL0010", "227013"), // sinuso

    URL31413("FRMINFOOPERACIONCONTROLADORURL31413", "362005"),

    URL33171("FRMINFOOPERACIONCONTROLADORURL33171", "227021"),

    URL30892("FRMINFOOPERACIONCONTROLADORURL30892", "227022"),

    URL40032("FRMINFOOPERACIONCONTROLADORURL40032", "214005"),

    URL27177("FRMINFOOPERACIONCONTROLADORURL27177", "213108"),

    URL41509("FRMINFOOPERACIONCONTROLADORURL41509", "104024"), // 104024
                                                               // //104036

    URL29800("FRMINFOOPERACIONCONTROLADORURL29800", "215021"),

    URL32035("FRMINFOOPERACIONCONTROLADORURL32035", "213110"),

    URL42914("FRMINFOOPERACIONCONTROLADORURL42914", "104027");

    private final String key;
    private final String value;

    private FrminfooperacionControladorUrlEnum(String key, String value) {
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
