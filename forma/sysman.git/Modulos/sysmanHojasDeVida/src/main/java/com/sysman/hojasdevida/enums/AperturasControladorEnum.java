/*-
 * AperturasControladorEnum.java
 *
 * 1.0
 * 
 * 4/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 4/07/2018
 * @author lbotia
 *
 */
public enum AperturasControladorEnum {

    NOMBRE_CATEGORIA("NOMBRE_CATEGORIA"),

    SUELDO("SUELDO"),

    PLAZAS("PLAZAS"),

    CODIGOCARGO("CODIGOCARGO"),

    CONVOCATORIA("convocatoria"),

    NOMBRE_CARGO("NOMBRE_CARGO"),

    NRO_CONVOCATORIA("NRO_CONVOCATORIA"),

    GRADOCARGO("GRADOCARGO"),

    NOMBRE_DEPENDENCIA("NOMBRE_DEPENDENCIA")

    ;

    private final String value;

    private AperturasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}