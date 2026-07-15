/*-
 * TransaccionModelosControlador.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * 
 * @version 1.0, 21/11/2018
 * @author ybecerra
 *
 */
public enum RetencionesciuusControladorUrlEnum {

    URL150("RETENCIONESCIUUSCONTROLADORURL150", "4001"),

    URL189("RETENCIONESCIUUSCONTROLADORURL189", "12014"),

    URL235("RETENCIONESCIUUSCONTROLADORURL235", "1758001"),

    URL237("RETENCIONESCIUUSCONTROLADORURL237", "1758002"),
    
    URL238("RETENCIONESCIUUSCONTROLADORURL238", "1758005");

    private final String key;
    private final String value;

    private RetencionesciuusControladorUrlEnum(String key,
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
