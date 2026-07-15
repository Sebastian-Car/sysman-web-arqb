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
 * @version 1.0, 22/06/2018
 * @author ybecerra
 *
 */
public enum FrmtransaccionessstsControladorEnum {
    AGENTE("AGENTE"),

    NOMBREAGENTE("NOMBREAGENTE"),

    CLASE_AGENTE("CLASE_AGENTE");

    private final String value;

    private FrmtransaccionessstsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
