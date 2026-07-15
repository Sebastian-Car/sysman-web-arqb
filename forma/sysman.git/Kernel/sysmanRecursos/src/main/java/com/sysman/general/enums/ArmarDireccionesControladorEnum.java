/*-
 * ArmarDireccionesControladorEnum.java
 *
 * 1.0
 * 
 * 18/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumeracion que permite clasificar los parametros utilizados en el
 * controlador {@link com.sysman.general.ArmarDireccionesControlador}
 * 
 * @version 1.0, 18/04/2018
 * @author pespitia
 *
 */
public enum ArmarDireccionesControladorEnum {

    /** Valor: direccion */
    PR_DIRECCION("direccion");

    private final String value;

    private ArmarDireccionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
