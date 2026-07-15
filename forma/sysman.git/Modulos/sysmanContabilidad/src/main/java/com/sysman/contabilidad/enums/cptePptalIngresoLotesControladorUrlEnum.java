/*
 * CptePptalIngresoLotesControladorUrlEnum
 *
 * 1.0
 *
 * 30/12/2025
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

public enum cptePptalIngresoLotesControladorUrlEnum {

    URL4162("CPTEPPTALINGRESOLOTESCONTROLADORURL4009", "4009"),
    URL4409("CPTEPPTALINGRESOLOTESCONTROLADORURL4409", "7003"),
    URL15087("CPTEPPTALINGRESOLOTESCONTROLADORURL15087", "15087"),
    URL72135("CPTEPPTALINGRESOLOTESCONTROLADORURL72135", "72135");
    

    private final String key;
    private final String value;

    private cptePptalIngresoLotesControladorUrlEnum(String key, String value) {
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