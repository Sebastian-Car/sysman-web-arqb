/*
 * CuotaspartesdetallesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum CuotaspartesdetallesControladorEnum {

    NOMBRE_TERCERO("NOMBRE_TERCERO"),

    RID_LOWER("rid"),

    IDEMPLEADO_LOWER("idEmpleado"),

    NOMBREEMPLEADO_LOWER("nombreEmpleado"),

    NIT("NIT"),

    IDEMPLEADO("IDEMPLEADO"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO"),

    RNUM("RNUM"),

    RID("RID"),

    FECHAINICIALM_LB("FECHAINICIALM_LB"),

    FECHAFINALM_LB("FECHAFINALM_LB")

    ;
    private final String value;

    private CuotaspartesdetallesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
