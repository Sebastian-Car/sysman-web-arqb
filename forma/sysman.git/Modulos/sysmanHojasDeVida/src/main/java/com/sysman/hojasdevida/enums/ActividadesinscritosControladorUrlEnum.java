/*-
 * ActividadesinscritosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 3/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para almacenar las urls empleadas en el controlador
 * 
 * @version 1.0, 3/02/2018
 * @author dnino
 *
 */
public enum ActividadesinscritosControladorUrlEnum {

    // URL404("ACTIVIDADESINSCRITOSCONTROLADORURL404", "776001"),

    URL404("ACTIVIDADESINSCRITOSCONTROLADORURL404", "685052"),

    URL180("ACTIVIDADESINSCRITOSCONTROLADORURL180", "740009");

    private final String key;
    private final String value;

    private ActividadesinscritosControladorUrlEnum(String key, String value) {
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
