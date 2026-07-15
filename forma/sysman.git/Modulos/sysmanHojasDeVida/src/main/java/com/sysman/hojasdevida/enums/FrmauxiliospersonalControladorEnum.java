/*-
 * NatsubpensionsControladorEnum.java
 *
 * 1.0
 * 
 * 24/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Se encarga de almacenar los enumerados necesarios para el
 * funcionamiento del controlador NatsubpensionsControlador
 * 
 * @version 1.0, 24/01/2018
 * @author mvenegas
 *
 */
public enum FrmauxiliospersonalControladorEnum {

    NAT_INCENTIVOS("NAT_INCENTIVOS"),

    NUMERO_DCTO("NUMERO_DCTO")

    ;

    private final String value;

    private FrmauxiliospersonalControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
