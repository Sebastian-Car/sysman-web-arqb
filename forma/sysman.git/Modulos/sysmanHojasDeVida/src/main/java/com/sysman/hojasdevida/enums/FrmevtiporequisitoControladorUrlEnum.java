/*-
 * FrmEvTipoRequisitoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 15/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para implementación de Url's de servicio.
 * 
 * @version 1.0, 15/01/2018
 * @author dnino
 *
 */
public enum FrmevtiporequisitoControladorUrlEnum {
    URL325("FRMTIPORACICONTROLADORURL325",
                    "FRMTIPORACICONTROLADORURL325");

    private final String key;
    private final String value;

    private FrmevtiporequisitoControladorUrlEnum(String key, String value) {
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
