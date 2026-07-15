/*-
 * TerceroOpsControladorUrlEnum.java
 *
 * 1.0
 * 
 * 10/07/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilidad.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 10/07/2019
 * @author bcardenas
 *
 */
public enum TerceroOpsControladorUrlEnum {

    URL0001("TERCEROOPSCONTROLADORURL", "1709001");

    private final String key;
    private final String value;

    private TerceroOpsControladorUrlEnum(String key, String value) {
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
