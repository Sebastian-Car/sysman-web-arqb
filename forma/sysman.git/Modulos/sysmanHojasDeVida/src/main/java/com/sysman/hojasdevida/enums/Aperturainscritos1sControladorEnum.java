/*-
 * CerrarConvocatoriaControladorEnum.java
 *
 * 1.0
 *
 * 29/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * @author spina
 *
 * @version 1.0, 18 de dic. de 2017
 *
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum Aperturainscritos1sControladorEnum {

    NRO_CONVOCATORIA("NRO_CONVOCATORIA"),

    NAT_APERTURA_INSCRITOS("NAT_APERTURA_INSCRITOS"),
    
    TELEFONOS("TELEFONOS"),
    
    DIRECCIONEMAIL("DIRECCIONEMAIL");

    private final String value;

    private Aperturainscritos1sControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
