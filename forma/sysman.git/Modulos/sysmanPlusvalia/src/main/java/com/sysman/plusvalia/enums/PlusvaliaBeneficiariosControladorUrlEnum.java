/*-
 * PlusvaliaBeneficiariosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 5/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 5/02/2019
 * @author bcardenas
 *
 */
public enum PlusvaliaBeneficiariosControladorUrlEnum {

    URL14040("PLUSVALIABENEFICIARIOSCONTROLADORURL14040", "14040"),

    URL367("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "367208"),

    URL1776("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1776001"),

    URL00C("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "176800C"),

    URL00U("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1768006"),

    URL00G("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "176800G"),

    URL00R("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1768007"),

    URL00D("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "176800D"),

    URL1780("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1780001"),

    URL1791001("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1791001"),

    URL1791003("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1791003"),

    URL1795("PLUSVALIABENEFICIARIOSCONTROLADORURL367", "1795001");

    private final String key;
    private final String value;

    private PlusvaliaBeneficiariosControladorUrlEnum(String key, String value) {
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
