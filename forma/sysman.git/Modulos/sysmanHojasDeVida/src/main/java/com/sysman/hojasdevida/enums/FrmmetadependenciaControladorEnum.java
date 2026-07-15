/*-
 * frmmetadependenciaControladorEnum.java
 *
 * 1.0
 * 
 * 20/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumerado que almacena las constantes.
 * 
 * @version 1.0, 20/01/2018
 * @author dnino
 *
 */
public enum FrmmetadependenciaControladorEnum {

    COMPANIA("COMPANIA"),

    DEPENDENCIA("DEPENDENCIA"),

    NOMBRE("NOMBRE"),

    CODIGO_META("CODIGO_META"),

    EV_META_DEPENDENCIA("EV_META_DEPENDENCIA")

    ;

    private final String value;

    private FrmmetadependenciaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
