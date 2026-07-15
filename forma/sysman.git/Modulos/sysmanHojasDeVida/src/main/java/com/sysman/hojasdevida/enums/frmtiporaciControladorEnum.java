/*-
 * imprimirelegiblesControladorEnum.java
 *
 * 1.0
 * 
 * 14/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para almacenar parámetros del archivo
 * "frmtiporaciControlador".
 * 
 * @version 1.0, 14/12/2017
 * @author dnino
 *
 */
public enum frmtiporaciControladorEnum {

    PARAM0("PARAM0");

    private final String value;

    private frmtiporaciControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
