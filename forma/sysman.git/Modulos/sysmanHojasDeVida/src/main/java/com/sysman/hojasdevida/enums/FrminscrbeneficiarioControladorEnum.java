/*-
 * FrminscrbeneficiarioControladorEnum.java
 *
 * 1.0
 * 
 * 19/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumerado reservado para almacenar constantes del controlador
 * Frminscrbeneficiario.
 * 
 * @version 1.0, 19/02/2018
 * @author dnino
 *
 */
public enum FrminscrbeneficiarioControladorEnum {

    DCTO_EMPLEADO("DCTO_EMPLEADO"),

    PARENTESCO("PARENTESCO"),

    SUCURSAL_EMPLEADO("SUCURSAL_EMPLEADO"),

    DCTO_IDENTIDAD("DCTO_IDENTIDAD"),

    TIPOEVENTO("TIPOEVENTO"),

    IDEVENTO("IDEVENTO"),

    EVENTO("EVENTO"),

    DOCUMENTO("DOCUMENTO")

    ;

    private final String value;

    private FrminscrbeneficiarioControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}