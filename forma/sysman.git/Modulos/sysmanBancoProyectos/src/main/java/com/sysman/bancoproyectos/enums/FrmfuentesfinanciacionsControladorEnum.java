/*-
 * FrmfuentesfinanciacionsControladorEnum.java
 *
 * 1.0
 * 
 * 14/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 14/09/2017
 * @author pespitia
 *
 */
public enum FrmfuentesfinanciacionsControladorEnum {
    CODIGOFUENTE("CODIGOFUENTE"),

    FUENTE("fuente"),

    ORDENLB("ORDENLB");

    private final String value;

    private FrmfuentesfinanciacionsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
