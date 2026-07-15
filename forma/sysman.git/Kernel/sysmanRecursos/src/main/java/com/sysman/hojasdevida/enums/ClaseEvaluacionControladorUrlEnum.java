/*-
 * ClaseEvaluacionControladorUrlEnum.java
 *
 * 1.0
 * 
 * 25/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enum necesario para traer datos del combo de funcionaro utilizando
 * el dss correspondiente
 * 
 * @version 1.0, 25/01/2018
 * @author crodriguez
 *
 */
public enum ClaseEvaluacionControladorUrlEnum {

    URL128("CLASEEVALUACIONCONTROLADORURL128", "938001");

    private final String key;
    private final String value;

    private ClaseEvaluacionControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
