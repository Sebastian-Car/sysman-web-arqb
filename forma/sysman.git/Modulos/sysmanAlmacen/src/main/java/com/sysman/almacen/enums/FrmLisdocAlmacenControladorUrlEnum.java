/*-
 * FrmLisdocAlmacenControladorUrlEnum.java
 *
 * 1.0
 * 
 * 4/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.almacen.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 4/07/2018
 * @author bcardenas
 *
 */

public enum FrmLisdocAlmacenControladorUrlEnum {

    // tipocomprobante

    URL0001("FRMLISDOCALMACENCONTROLADORURL0001", "139003");

    private final String key;
    private final String value;

    private FrmLisdocAlmacenControladorUrlEnum(String key, String value) {
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