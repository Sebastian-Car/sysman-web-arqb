/*-
 * AnexosestudiospreviosControladorEnum.java
 *
 * 1.0
 * 
 * 22/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * 
 * @version 1.0, 22/08/2017
 * @author mvenegas
 *
 */
public enum FrmacumcomercialesprevioproysEnum {

    NUMERO_ESTUDIO("NUMERO_ESTUDIO"),

    DESCRIPCION_ACUERDO("DESCRIPCION_ACUERDO"),

    NOMBRE_PAIS("NOMBRE_PAIS"),

    ENTIDAD_ESTATAL("ENTIDAD_ESTATAL"),

    PRESUPUESTO_MAYOR_AC("PRESUPUESTO_MAYOR_AC"),

    EXPEDICION_APLICABLE("EXPEDICION_APLICABLE"),

    CONTRATACION_CUBIERTA("CONTRATACION_CUBIERTA");

    private final String value;

    private FrmacumcomercialesprevioproysEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
