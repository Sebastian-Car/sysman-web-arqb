/*-
 * ImprimirHvSalariosControladorEnum.java
 *
 * 1.0
 * 
 * 13/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 13/12/2017
 * @author jcrodriguez
 *
 */
public enum NatAccidentesTrabajosControladorEnum {

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    ID_RIESGO("ID_RIESGO"),

    FACTOR_RIESGO("FACTOR_RIESGO"),

    ID_CONCEPTO("ID_CONCEPTO"),

    CLASE_TRANSACCION("CLASE_TRANSACCION");

    private final String value;

    private NatAccidentesTrabajosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
