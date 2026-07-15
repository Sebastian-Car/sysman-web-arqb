/*-
 * FacturasControladorEnum.java
 *
 * 1.0
 * 
 * 29/06/2017
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
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeraci√≥n.∫
 */
public enum FacturasControladorEnum {

    IP_RECIBOS_DE_PAGO("IP_RECIBOS_DE_PAGO");

    private final String value;

    private FacturasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
