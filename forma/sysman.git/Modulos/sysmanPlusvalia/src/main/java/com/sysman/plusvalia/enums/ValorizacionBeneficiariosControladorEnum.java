/*-
 * ValorizacionBeneficiariosControladorEnum.java
 *
 * 1.0
 * 
 * 22/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plusvalia.enums;

/**
 * TODO Ingrese una descripcion para la clase.
 * 
 * @version 1.0, 22/03/2019
 * @author bcardenas
 *
 */
public enum ValorizacionBeneficiariosControladorEnum {

    NIT("NIT"),

    FACTOR("FACTOR"),

    CLASE_FACTOR("CLASE_FACTOR"),

    NOMBRETERCERO("NOMBRETERCERO"),

    IP_CODIGO("IP_CODIGO"),

    IP_USUARIO("IP_USUARIO"),

    IP_NUMERO_ORDEN("IP_NUMERO_ORDEN"),

    MATRICULA_INMOBILIARIA("MATRICULA_INMOBILIARIA"),

    DIRECCION("DIRECCION"),

    DIRECCION_CORRESPONDENCIA("DIRECCION_CORRESPONDENCIA"),

    NOMBRE_USUARIO("NOMBRE_USUARIO"),

    DESTINACION_ECONOMICA("DESTINACION_ECONOMICA"),

    FACTOR_DESTINACION_ECONOMICA("FACTOR_DESTINACION_ECONOMICA"),

    GRADO_BENEFICIO("GRADO_BENEFICIO"),

    FACTOR_GRADO_BENEFICIO("FACTOR_GRADO_BENEFICIO");

    private final String value;

    private ValorizacionBeneficiariosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
