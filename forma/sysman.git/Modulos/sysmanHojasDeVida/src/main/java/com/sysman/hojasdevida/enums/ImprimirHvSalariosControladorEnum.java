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
public enum ImprimirHvSalariosControladorEnum {

    NUMERO_DCTO("NUMERO_DCTO"),

    REPORTE001551("001551HojasDeVidaSalarios"),

    REPORTE001604("001604INFOSALARIAL"),

    EMPLEADOINICIAL("EMPLEADOINICIAL");

    private final String value;

    private ImprimirHvSalariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
