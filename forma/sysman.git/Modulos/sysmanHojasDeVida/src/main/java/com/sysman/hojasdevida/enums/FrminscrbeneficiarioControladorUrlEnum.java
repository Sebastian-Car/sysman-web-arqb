/*-
 * FrminscrbeneficiarioControladorUrlEnum.java
 *
 * 1.0
 * 
 * 20/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para almacenar las Url's del controlador
 * FrminscrbeneficiarioControlador.
 * 
 * @version 1.0, 20/02/2018
 * @author dnino
 *
 */
public enum FrminscrbeneficiarioControladorUrlEnum {

    URL801("FRMINSCRBENEFICIARIOCONTROLADORURLENUM801", "609002"),

    URL802("FRMINSCRBENEFICIARIOCONTROLADORURLENUM802", "209004");

    private final String key;
    private final String value;

    private FrminscrbeneficiarioControladorUrlEnum(String key, String value) {
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
