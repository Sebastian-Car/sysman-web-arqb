/*-
 * CdEstadosPingresosControladorEnum.java
 *
 * 1.0
 * 
 * 28/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto.enums;

/**
 * 
 * @version 1.0, 28/11/2017
 * @author jcrodriguez
 *
 */
public enum CdEstadosPingresosControladorEnum {
    REPORTE001527("001527CDESTADOSPINGRESOS");

    private final String value;

    private CdEstadosPingresosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
