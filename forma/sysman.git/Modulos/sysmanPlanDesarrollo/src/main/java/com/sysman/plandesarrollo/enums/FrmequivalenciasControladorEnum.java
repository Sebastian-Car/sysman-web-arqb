/*-
 * FrmequivalenciasControlador.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * Enumeracion que permite clasificar las cadenas del controlador
 * FrmequivalenciasControlador.
 * 
 * @version 1.0, 27/02/2018
 * @author lbotia
 *
 */
public enum FrmequivalenciasControladorEnum {

    ID("ID"),

    CODIGO("CODIGO"),

    VIGENCIA("VIGENCIA"),

    NOMSUBPROYECTO("NOMSUBPROYECTO"),

    VIGENCIA_INICIAL("VIGENCIA_INICIAL"),

    VIGENCIA_PLAN("VIGENCIA_PLAN"),

    ID_PLAN("ID_PLAN"),

    ANO("ANO"),

    DIGITOS_META_PRODUCTO("DIGITOS_META_PRODUCTO"),

    NATURALEZA("NATURALEZA"),

    VIGENCIA_META("VIGENCIA_META"),

    SUBPROYECTO("SUBPROYECTO");

    private final String value;

    private FrmequivalenciasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
