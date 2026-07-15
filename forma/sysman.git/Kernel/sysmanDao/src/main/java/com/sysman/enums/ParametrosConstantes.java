/*-
 * ParametrosConstantes.java
 *
 * 1.0
 * 
 * 7/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.enums;

/**
 * Enumeracion donde se alamcenan los nombres de los campos constantes
 * 
 * @version 1.0, 7/06/2017
 * @author cmanrique
 *
 */
public enum ParametrosConstantes {

    CODIGO("CODIGO"), PASSWORD("PASSWORD"), COMPANIA("COMPANIA"), APLICACION(
                    "APLICACION"), NOMBRE("NOMBRE");

    private final String value;

    private ParametrosConstantes(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}
