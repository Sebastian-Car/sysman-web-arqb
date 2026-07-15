/*-
 * AreasmisionalesControladorEnum.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 *
 * 
 * @version 1.0, 18/01/2018
 * @author crodriguez
 *
 */
public enum AreasmisionalesControladorEnum {

    ID_DE_EMPLEADO("ID_DE_EMPLEADO");

    private final String value;

    private AreasmisionalesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
