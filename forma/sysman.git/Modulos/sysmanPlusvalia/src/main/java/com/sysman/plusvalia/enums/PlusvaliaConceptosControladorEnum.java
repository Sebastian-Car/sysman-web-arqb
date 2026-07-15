/*-
 * PlusvaliaConceptosControladorEnum.java
 *
 * 1.0
 * 
 * 7/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 7/02/2019
 * @author bcardenas
 *
 */
public enum PlusvaliaConceptosControladorEnum {

    CUENTA_CREDITO_CAUSACION("CUENTA_CREDITO_CAUSACION"),

    CUENTA_DEBITO_RECAUDO("CUENTA_DEBITO_RECAUDO"),

    CUENTA_DEBITO_CAUSACION("CUENTA_DEBITO_CAUSACION"),

    CUENTA_CREDITO_RECAUDO("CUENTA_CREDITO_RECAUDO"),

    NOMBRECREDITOCAUSACION("NOMBRECREDITOCAUSACION"),

    NOMBREDEBITOCAUSACION("NOMBREDEBITOCAUSACION"),

    NOMBRECREDITORECAUDO("NOMBRECREDITORECAUDO"),

    NOMBREDEBITORECAUDO("NOMBREDEBITORECAUDO"),

    CODIGO_PROYECTO("CODIGO_PROYECTO"),

    ID_PROYECTO("ID_PROYECTO");

    private final String value;

    private PlusvaliaConceptosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
