/*
 * SubnovedadcontratosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum SubnovedadcontratosControladorEnum {

    PARAM3("CLASEORDEN"),

    PARAM1("NUMERO"),

    PARAM2("TIPO"),

    PARAM0("CEDULAORDENADOR"),

    PARAM4("ANIO"),

    PARAM5("ORDENDECOMPRA"),

    PARAM6("NOVEDAD"),

    PARAM7("CLASENOVEDADORD"),

    PARAM8("CLASET"),

    PARAM9("CLASENOV"),

    PARAM11("NUMEROCONTRATO"),

    PARAM12("NOVEDADCONTRATO"),

    PARAM10("CNUMERO"),

    VLRAMORTIZADOLIBERADO("VLRAMORTIZADOLIBERADO"),

    VLRAMORTIZADOACTA("VLRAMORTIZADOACTA"),

    PORCAMORTIZADO("PORCAMORTIZADO"),

    VALORAPAGAR("VALORAPAGAR"),

    APLICA_CALIFICACION("APLICA_CALIFICACION"),

    INDPAGO("INDPAGO");

    private final String value;

    private SubnovedadcontratosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
