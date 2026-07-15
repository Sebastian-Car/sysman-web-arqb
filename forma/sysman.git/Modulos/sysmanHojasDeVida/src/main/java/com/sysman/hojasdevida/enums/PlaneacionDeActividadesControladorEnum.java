/*-
 * FrmconceptoviaticosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Este enumerado me relaciona los servicios necesarios para el
 * controlador FrmconceptoviaticosControlador
 * 
 * @version 1.0, 18/01/2018
 * @author mvenegas
 *
 */
public enum PlaneacionDeActividadesControladorEnum {

    TB_TB3957("TB_TB3957"),

    PR_TITULO("PR_TITULO"),

    PR_TXTANOINICIAL("PR_TXTANOINICIAL"),

    PR_TXTANOFINAL("PR_TXTANOFINAL"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    NUMERO_OCHO("8"),

    NUMERO("104");

    private final String value;

    private PlaneacionDeActividadesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
