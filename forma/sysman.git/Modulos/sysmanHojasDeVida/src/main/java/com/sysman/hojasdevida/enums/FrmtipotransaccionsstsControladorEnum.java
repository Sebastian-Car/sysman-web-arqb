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
 * @version 1.0, 28/12/2017
 * @author jcrodriguez
 *
 */
public enum FrmtipotransaccionsstsControladorEnum {
    FECHAGENERACION("FECHAGENERACION"),

    TIPO("TIPO"),

    NOMBRE_CLASE("NOMBRE_CLASE"),

    RUTAFORMATO_PLANTILLA("RUTAFORMATO_PLANTILLA");

    private final String value;

    private FrmtipotransaccionsstsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
