/*-
 * SubfinanciablesdeudaControladorUrlEnum.java
 *
 * 1.0
 *
 * 17/06/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.serviciospublicos.enums;

/**
 * Enumerado para las urls necesarias para las consultas de las listas
 * en el controlador SubfinanciablesdeudaControlador.
 *
 * @version 1.0, 17/06/2017
 * @author lcortes
 *
 */
public enum SubfinanciablesdeudaControladorUrlEnum {

    URL0001("SUBFINANCIABLESDEUDACONTROLADORURL0001", "309014"),

    URL0002("SUBFINANCIABLESDEUDACONTROLADORURL0002", "309016"),

    URL0003("SUBFINANCIABLESDEUDACONTROLADORURL0003", "309018"),

    URL0004("SUBFINANCIABLESDEUDACONTROLADORURL0004", "309019"),

    URL0005("SUBFINANCIABLESDEUDACONTROLADORURL0005", "327001"),

    URL0006("SUBFINANCIABLESDEUDACONTROLADORURL0006", "327003"),

    ;

    private final String key;
    private final String value;

    private SubfinanciablesdeudaControladorUrlEnum(String key, String value) {
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
