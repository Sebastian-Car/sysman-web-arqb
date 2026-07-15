/*-
 * PersonalcesantiasControladorEnum.java
 *
 * 1.0
 * 
 * 4/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * @version 1.0, 4/01/2018
 * @author jcrodriguez
 *
 */
public enum PersonalcesantiasControladorEnum {
    FECHAINICIAL("fechaInicial"),

    FECHAFINAL("fechaFinal"),

    REPORTE001625("001625cesantiasParcialesTODOSENTREFECHAS"),

    REPORTE001622("001622CesantiasParcialesTODOSFechas"),

    REPORTE001621("001621CesantiasParciales(Empleado)"),

    REPORTE001620("001620CesantiasParcialesTODOSResumen"),

    ID_DE_EMPLEADO_AUX("ID_DE_EMPLEADO_AUX"),

    NOM("NOM"),

    CESANTIA("CESANTIA"),

    INTERES("INTERES"),

    PROMVARIABLES("PROMVARIABLES"),

    RETIRADAS("RETIRADAS"),

    DIAS("DIAS"),

    DIAS_LICENCIAS("DIAS_LICENCIAS"),

    PARCIALES("PARCIALES");

    private final String value;

    private PersonalcesantiasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
