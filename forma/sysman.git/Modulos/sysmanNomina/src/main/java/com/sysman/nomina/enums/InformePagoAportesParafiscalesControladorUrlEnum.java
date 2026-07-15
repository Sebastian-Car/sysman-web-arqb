/*-
 * InformePagoAportesParafiscalesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * * Enum necesario para traer datos de los combos utilizando el dss
 * correspondientes
 * 
 * @version 1.0, 27/02/2018
 * @author crodriguez
 *
 */
public enum InformePagoAportesParafiscalesControladorUrlEnum {

    URL_200("INFORMEPAGOPARAFISCALES200", "537007"),

    URL_226("INFORMEPAGOPARAFISCALES226", "471061"),

    URL_252("INFORMEPAGOPARAFISCALES252", "7030"),

    URL_281("INFORMEPAGOPARAFISCALES281", "471029"),

    ;

    private final String key;
    private final String value;

    private InformePagoAportesParafiscalesControladorUrlEnum(String key,
        String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
