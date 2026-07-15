/*-
 * PrerrequisitosproponentesEnum.java
 *
 * 1.0
 * 
 * 5/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.precontractual.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 5/09/2017
 * @author pespitia
 *
 */
public enum PrerrequisitosproponentesControladorEnum {

    VALORFORMATO("VALORFORMATO"),

    VALOR2("VALOR2"),

    VALOR1("VALOR1"),

    TIPO("TIPO"),

    PROPONENTE("PROPONENTE"),

    CONSECUTIVODETALLE("CONSECUTIVODETALLE"),

    TRANSACCION("TRANSACCION");

    private final String value;

    private PrerrequisitosproponentesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
