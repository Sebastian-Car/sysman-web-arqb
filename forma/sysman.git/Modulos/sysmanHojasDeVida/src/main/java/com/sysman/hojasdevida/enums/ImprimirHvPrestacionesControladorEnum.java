/*-
 * ImprimirHvPrestacionesControladorEnum.java
 *
 * 1.0
 * 
 * 14 de dic. de 2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author amonroy
 * 
 * @version 1.0, 14 de dic. de 2017
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum ImprimirHvPrestacionesControladorEnum {
    EMPLEADOINICIAL("EMPLEADOINICIAL");

    private final String value;

    private ImprimirHvPrestacionesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
