/**
 * Clase: FrmplanaccionestpreviosControladorUrlEnum.java
 *
 * 
 * 8/10/2018
 * 
 * Copyright (c) 2018 Stefanini Sysman. Paipa, Boyaca. All rights
 * reserved.
 */

package com.sysman.precontractual.enums;

/**
 * Enumerado de servicios para el controlador
 * FrmplanaccionestpreviosControlador
 * 
 * @version 1.0, 8/10/2018
 * @author mvenegas
 *
 */
public enum FrmplanaccionestpreviosControladorUrlEnum {

    URL001("PLANACCION_ESTPREVIO001", "4001"), // AÑO

    URL002("PLANACCION_ESTPREVIO002", "552048"), // PLAN INDICATIVO

    URL003("PLANACCION_ESTPREVIO003", "1034009"); // PLAN
                                                  // ADQUISICIONES

    private final String key;
    private final String value;

    private FrmplanaccionestpreviosControladorUrlEnum(String key, String value) {
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