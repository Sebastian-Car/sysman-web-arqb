/*-
 * ImprimirHojasDeVidaControladorUrlEnum.java
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
 * @version 1.0, 14/12/2017
 * @author jguerrero
 *
 */
public enum NatsublicenciasControladorUrlEnum {
    URL0001("NATSUBLICENCIASCONTROLADORURL0001", "626006"),

    URL0002("NATSUBLICENCIASCONTROLADORURL0002", "626007"),

    URL0003("NATSUBLICENCIASCONTROLADORURL0003", "626012"),

    URL0004("NATSUBLICENCIASCONTROLADORURL0004", "626009"),

    URL0005("NATSUBLICENCIASCONTROLADORURL0005", "626011"),

    URL0006("NATSUBLICENCIASCONTROLADORURL0006", "7013"),

    URL0007("NATSUBLICENCIASCONTROLADORURL0007", "626013"),

    URL0008("NATSUBLICENCIASCONTROLADORURL0008", "4001"),

    URL0009("NATSUBLICENCIASCONTROLADORURL0009", "707001"),

    URL0010("NATSUBLICENCIASCONTROLADORURL0010", "471055"),

    ;

    private final String key;
    private final String value;

    private NatsublicenciasControladorUrlEnum(String key,
        String value) {
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
