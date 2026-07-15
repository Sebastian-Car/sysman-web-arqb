/*-
 * ConceptosbsControladorEnum.java
 *
 * 1.0
 *
 * 20/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los parametros.
 *
 * @version 1.0, 20/02/2018
 * @author pespitia
 *
 */
public enum ConceptosbsControladorEnum {

    M_60506("60506"),

    C_RETRO("C_RETRO"),

    C_CTA_CREDITO("Cta Credito "),

    C_CTA_DEBITO("Cta Debito "),

    FACTOR_RETEFUENTE("FACTOR_RETEFUENTE"),

    FACTOR_OTROSPAGOS("FACTOR_OTROSPAGOS"),

    FACTOR_PRESTACIONAL("FACTOR_PRESTACIONAL"),

    FACTOR_EXTRALEGALES("FACTOR_EXTRALEGALES"),

    FACTOR_LEGALES("FACTOR_LEGALES"),

    FACTOR_SALARIAL("FACTOR_SALARIAL"),

    FACTOR_PRESTACIONAL_EMB("FACTOR_PRESTACIONAL_EMB"),

    FACTOR_SUELDOS("FACTOR_SUELDOS"),

    FACTOR_SALARIAL_EMB("FACTOR_SALARIAL_EMB"),

    FACTOR_SUELDOS_EMB("FACTOR_SUELDOS_EMB"),

    TERCEROINTERFAZ("TERCEROINTERFAZ"),

    NOMCONCEPTORETRO("NOMCONCEPTORETRO"),

    NOMCONCEPTORELA("NOMCONCEPTORELA"),

    INDRENTACN379("INDRENTACN379"),

    INDRETATOTALINC("INDRETATOTALINC"),
    
    TIPO_ENTIDAD("TIPO_ENTIDAD");

    private final String value;

    private ConceptosbsControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
