/*-
 * frmEvTipoRequisitoControladorEnum.java
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
 * Enumerado para las constantes empleadas en el archivo frmevtiporequisito.
 * 
 * @version 1.0, 15/01/2018
 * @author dnino
 *
 */
public enum FrmevtiporequisitoControladorEnum {
    COMPANIA("COMPANIA"),
    
    CODIGO("CODIGO"),
    
    NOMBRE("NOMBRE");

    private final String value;

    private FrmevtiporequisitoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
