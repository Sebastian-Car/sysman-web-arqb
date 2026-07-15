/*-
 * FrmPeriodoTodasNovedadesControladorEnum.java
 *
 * 1.0
 * 
 * 22/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 22/05/2018
 * @author lbotia
 *
 */
public enum FrmPeriodoTodasNovedadesControladorEnum {

    TIPO("TIPO"),

    CLASET("CLASET");

    private final String value;

    private FrmPeriodoTodasNovedadesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
