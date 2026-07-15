/*-
 * FrmfacturasexcedentesControladorEnum.java
 *
 * 1.0
 * 
 * 30/06/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmfacturasexcedentesControladorEnum {

    IP_RECIBO_EXCEDENTES("IP_RECIBO_EXCEDENTES");

    private final String value;

    private FrmfacturasexcedentesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
