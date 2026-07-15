/*-
 * FrmprocedenciatramitesControladorEnum.java
 *
 * 1.0
 * 
 * 17/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 17/04/2018
 * @author lbotia
 *
 */
public enum FrmprocedenciatramitesControladorEnum {

    NIT("NIT"),

    SUCURSAL("SUCURSAL"),

    DIRECCION("DIRECCION"),

    NOMBRENIT("NOMBRENIT"),

    REPRESENTANTELEGAL("REPRESENTANTELEGAL"),

    SUCURSAL_REPRESENTANTE("SUCURSAL_REPRESENTANTE"),

    NOMBREREPRE("NOMBREREPRE");

    private final String value;

    private FrmprocedenciatramitesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
