/*-
 * SolicitudesAutsControladorEnum.java
 *
 * 1.0
 * 
 * 21/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.general.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 21/02/2018
 * @author amonroy
 *
 */
public enum SolicitudesAutsControladorEnum {

    PORTRAMITAR("PORTRAMITAR"),

    DESTINATARIO("DESTINATARIO"),

    IDEMPLEADO("IDEMPLEADO"),

    DESTINOO("DESTINOO"),

    CEDULA("CEDULA");

    private final String value;

    private SolicitudesAutsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
