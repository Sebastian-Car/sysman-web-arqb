/*-
 * ResumenPorReferenciaControladorUrlEnum.java
 *
 * 1.0
 * 
 * 23/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 23/10/2018
 * @author bcardenas
 *
 */
public enum ResumenPorReferenciaControladorUrlEnum {

    URL0001("RESUMENPORREFERENCIACONTROLADORURL0001", "471002"),

    URL0002("RESUMENPORREFERENCIACONTROLADORURL0002", "13028"),

    URL0003("RESUMENPORREFERENCIACONTROLADORURL0003", "13030"),

    URL0004("RESUMENPORREFERENCIACONTROLADORURL0004", "16005"),

    URL0005("RESUMENPORREFERENCIACONTROLADORURL0005", "16003");

    private final String key;
    private final String value;

    private ResumenPorReferenciaControladorUrlEnum(String key,
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
