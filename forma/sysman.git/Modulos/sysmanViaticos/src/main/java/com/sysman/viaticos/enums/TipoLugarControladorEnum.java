/*-
 * TipoLugarControladorEnum.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * utilizados en el llamado de servicios.
 * 
 * @version 1.0, 19/01/2018
 * @author pespitia
 *
 */
public enum TipoLugarControladorEnum {

    CODTLUGAR("CODTLUGAR");

    private final String value;

    private TipoLugarControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
