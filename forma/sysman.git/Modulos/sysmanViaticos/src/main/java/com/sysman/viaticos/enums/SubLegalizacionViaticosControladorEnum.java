/*-
 * SubLegalizacionViaticosControladorEnum.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * 
 * @version 1.0, 19/01/2018
 * @author ybecerra
 *
 */
public enum SubLegalizacionViaticosControladorEnum {

    CODIGO_CONCEPTO("CODIGO_CONCEPTO"),

    KEY_NUMERO("KEY_NUMERO"),

    KEY_ANO("KEY_ANO"),

    TIPOVIATICO("TIPOVIATICO"),

    KEY_TIPO_VIATICO("KEY_TIPO_VIATICO"),

    NUMERO_AFECTADO("NUMERO_AFECTADO"),

    VALOR_AFECTADO("VALOR_AFECTADO"),

    SALDO("SALDO");

    private final String value;

    private SubLegalizacionViaticosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
