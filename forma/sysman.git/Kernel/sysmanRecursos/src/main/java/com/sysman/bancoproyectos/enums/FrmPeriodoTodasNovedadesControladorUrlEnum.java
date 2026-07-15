/*-
 * FrmPeriodoTodasNovedadesControladorUrlEnum.java
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
public enum FrmPeriodoTodasNovedadesControladorUrlEnum {

    URL0001("FRMPERIODOTODASNOVEDADESCONTROLADOR",
                    "4001"),

    URL0002("FRMPERIODOTODASNOVEDADESCONTROLADOR", "218016");

    private final String key;
    private final String value;

    private FrmPeriodoTodasNovedadesControladorUrlEnum(String key,
        String value) {
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