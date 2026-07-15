/*-
 * FrmevvaloracionsControladorEnum.java
 *
 * 1.0
 * 
 * 22/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 22/02/2018
 * @author crodriguez
 *
 */
public enum FrmevvaloracionsControladorEnum {

    CLASE_EVALUACION("CLASE_EVALUACION");

    private final String value;

    private FrmevvaloracionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
