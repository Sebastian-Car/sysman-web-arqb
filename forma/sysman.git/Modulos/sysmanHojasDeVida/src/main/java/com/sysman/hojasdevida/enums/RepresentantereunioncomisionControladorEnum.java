/*-
 * CerrarConvocatoriaControladorEnum.java
 *
 * 1.0
 *
 * 29/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author spina
 *
 * @version 1.0, 18 de dic. de 2017
 *
 * Enumerado que permite gestionar las palabras clave de los
 * controladores RepresentantereunioncomisionsControlador y
 * AdreunioncomitepersonalsControlador
 */
public enum RepresentantereunioncomisionControladorEnum {

    NUMERO_COMITE("NUMERO_COMITE"),

    TIPOCOMITE("TIPOCOMITE");

    private final String value;

    private RepresentantereunioncomisionControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
