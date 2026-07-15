/*-
 * FrmPbPoblacionBeneficiadaControladorEnum.java
 *
 * 1.0
 * 
 * 4/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo.enums;

/**
 * 
 * @version 1.0, 4/10/2019
 * @author bcardenas
 *
 */
public enum FrmPbPoblacionBeneficiadaControladorEnum {

    VALOR_PRIMINFANCIA("VALOR_PRIMINFANCIA"),

    VALOR_INFANCIA("VALOR_INFANCIA"),

    VALOR_ADOLESCENCIA("VALOR_ADOLESCENCIA"),

    VALOR_JUVENTUD("VALOR_JUVENTUD"),

    VALOR_ADULTO("VALOR_ADULTO"),

    VALOR_ADULTO_MAYOR("VALOR_ADULTO_MAYOR"),

    VALOR_TOTAL("VALOR_TOTAL"),

    VALOR_MUJERES("VALOR_MUJERES"),

    VALOR_HOMBRES("VALOR_HOMBRES"),

    VALOR_TOTGENERO("VALOR_TOTGENERO"),

    VALOR_VCA("VALOR_VCA"),

    VALOR_LGTB("VALOR_LGTB"),

    VALOR_DISCAP("VALOR_DISCAP"),

    VALOR_AFRO("VALOR_AFRO"),

    VALOR_INDIG("VALOR_INDIG");

    private final String value;

    private FrmPbPoblacionBeneficiadaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
