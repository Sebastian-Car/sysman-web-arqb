/*-
 * FrmtipotransaccionsstsControladorEnum.java
 *
 * 1.0
 * 
 * 28/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * 
 * @version 1.0, 03/05/2018
 * @author ybecerra
 *
 */
public enum TestigosControladorEnum {

    KEY_CONSECUTIVO("KEY_CONSECUTIVO"),

    KEY_NUMERO_DCTO("KEY_NUMERO_DCTO"),

    KEY_SUCURSAL("KEY_SUCURSAL"),

    KEY_FACTOR_RIESGO("KEY_FACTOR_RIESGO"),

    FACTOR_RIESGO("FACTOR_RIESGO"),

    KEY_TIPO_TRANSACCION("KEY_TIPO_TRANSACCION"),

    TIPO_TRANSACCION("TIPO_TRANSACCION"),

    KEY_CLASE_TRANSACCION("KEY_CLASE_TRANSACCION"),

    CLASE_TRANSACCION("CLASE_TRANSACCION")

    ;

    private final String value;

    private TestigosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
