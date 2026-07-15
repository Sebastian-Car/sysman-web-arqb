/*-
 * imprimirelegiblesControladorUrlEnum.java
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
 * Archivo que contiene la referencia a las consultas del Formulario
 * "frmtiporaci".
 * 
 * @version 1.0, 14/12/2017
 * @author dnino
 *
 */
public enum frmtiporaciControladorUrlEnum {

    URL320("FRMTIPORACICONTROLADORURL320",
                    "FRMTIPORACICONTROLADORURL320");

    private final String key;
    private final String value;

    private frmtiporaciControladorUrlEnum(String key, String value) {
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
