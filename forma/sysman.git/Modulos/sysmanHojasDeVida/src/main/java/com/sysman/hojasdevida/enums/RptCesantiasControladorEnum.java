/*-
 * RptcesantiasControladorEnum.java
 *
 * 1.0
 * 
 * 11/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 11/01/2018
 * @author dnino
 *
 */
public enum RptCesantiasControladorEnum {

    REPORTE001581("001581HojasDeVidaListadoCesantias"),

    REPORTE001584("001584RhvseguridadCesantias"),

    EMPLEADOINICIAL("EMPLEADOINICIAL"),

    NUMERO_DCTO("NUMERO_DCTO");

    private final String value;

    private RptCesantiasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
