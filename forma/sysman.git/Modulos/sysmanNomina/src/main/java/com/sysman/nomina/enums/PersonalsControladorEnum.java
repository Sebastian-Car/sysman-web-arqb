/*
 * PersonalsControladorEnum
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
 * identificados en el refactoring, para ser convertidos
 * Map<String,String> y disponibles en dicha enumeración.
 */
public enum PersonalsControladorEnum {

    EMAIL_CORPORATIVO("EMAIL_CORPORATIVO"),

    PARAM24("PARAM24"),

    PARAM9("PARAM9"),

    PARAM23("PARAM23"),

    PARAM22("PARAM22"),

    PARAM21("PARAM21"),

    PARAM6("PARAM6"),

    PARAM20("PARAM20"),

    PARAM5("PARAM5"),

    PARAM8("PARAM8"),

    PARAM7("PARAM7"),

    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("PARAM0"),

    PARAM19("PARAM19"),

    PARAM18("PARAM18"),

    PARAM17("PARAM17"),

    PARAM16("PARAM16"),

    PARAM15("PARAM15"),

    PARAM14("PARAM14"),

    PARAM13("PARAM13"),

    PARAM12("PARAM12"),

    PARAM11("PARAM11"),

    PARAM10("PARAM10");

    private final String value;

    private PersonalsControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
