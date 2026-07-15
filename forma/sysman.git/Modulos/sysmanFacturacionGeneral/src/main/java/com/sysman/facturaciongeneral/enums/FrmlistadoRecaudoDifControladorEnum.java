/*-
 * FrmlistadoRecaudoDifEnum.java
 *
 * 1.0
 * 
 * 8/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.facturaciongeneral.enums;

/**
 * 
 * @version 1.0, 8/11/2017
 * @author jcrodriguez
 *
 */
public enum FrmlistadoRecaudoDifControladorEnum {
    FORMATO("DD/MM/YYYY HH24:mi:ss"),

    INFORME001484("001484INFRECAUDODIF"),

    ANOCOBRO("ANOCOBRO"),

    NUMEROFACTURA("NUMEROFACTURA"),

    NUMERO_FACTURA("NUMERO_FACTURA"),

    TIPOCOBRO("TIPOCOBRO");

    private final String value;

    private FrmlistadoRecaudoDifControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
