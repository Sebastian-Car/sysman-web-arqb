/*-
 * EsfactoresporestproysControladorEnum.java
 *
 * 1.0
 * 
 * 24/08/2017
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
 * @version 1.0, 24/08/2017
 * @author pespitia
 *
 */
public enum EtapaPreDocControladorEnum {

    TIPO("TIPO"),

    ETAPA("ETAPA"),
    
    MODULO("MODULO");

    private final String value;

    private EtapaPreDocControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
