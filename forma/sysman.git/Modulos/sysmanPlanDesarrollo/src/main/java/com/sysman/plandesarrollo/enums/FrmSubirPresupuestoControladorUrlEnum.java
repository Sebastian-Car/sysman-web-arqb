/*-
 * FrmequivalenciasControladorUrlEnums.java
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
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 27/02/2018
 * @author lbotia
 *
 */
public enum FrmSubirPresupuestoControladorUrlEnum {

    URL001("FRMSUBIRPRESUPUESTOCONTROLADORURL001", "552035"),

    URL002("FRMSUBIRPRESUPUESTOCONTROLADORURL002", "4001");

    private final String key;
    private final String value;

    private FrmSubirPresupuestoControladorUrlEnum(String key, String value) {
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
