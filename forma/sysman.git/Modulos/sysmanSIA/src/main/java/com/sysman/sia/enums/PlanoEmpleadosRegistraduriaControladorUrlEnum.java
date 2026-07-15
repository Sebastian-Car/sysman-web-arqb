/*-
 * PlanoEmpleadosRegistraduriaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 19/09/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.sia.enums;

/**
 * 
 * @version 1.0, 19/09/2019
 * @author bcardenas
 *
 */
public enum PlanoEmpleadosRegistraduriaControladorUrlEnum {
    URL0001("PLANOEMPLEADOSREGISTRADURIACONTROLADORURL", "1809001"),

    URL0002("PLANOEMPLEADOSREGISTRADURIACONTROLADORURL", "1810001"),

    URL0003("PLANOEMPLEADOSREGISTRADURIACONTROLADORURL", "1811001"),

    URL0004("PLANOEMPLEADOSREGISTRADURIACONTROLADORURL", "210143"),

    URL0005("PLANOEMPLEADOSREGISTRADURIACONTROLADORURL", "210145");

    private final String key;
    private final String value;

    private PlanoEmpleadosRegistraduriaControladorUrlEnum(String key,
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
