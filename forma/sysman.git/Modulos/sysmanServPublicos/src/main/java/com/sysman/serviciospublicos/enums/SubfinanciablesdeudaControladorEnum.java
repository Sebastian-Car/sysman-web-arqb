/*-
 * SubfinanciablesdeudaControladorEnum.java
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
 * Enumerado para los nombres de los parametros necesarios para las
 * consultas de las listas en el controlador
 * SubfinanciablesdeudaControlador.
 *
 * @version 1.0, 17/06/2017
 * @author lcortes
 *
 */
public enum SubfinanciablesdeudaControladorEnum {

    PARAM0("DETFACT"), PARAM1("SP_FACTURADO");

    private final String value;

    private SubfinanciablesdeudaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
