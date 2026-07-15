/*
 * RegiscuentasporpagarcgrControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.cgr.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RegiscuentasporpagarcgrControladorUrlEnum {

    URL9587("REGISCUENTASPORPAGARCGRCONTROLADORURL9587", "45018"),

    URL8199("REGISCUENTASPORPAGARCGRCONTROLADORURL8199", "7012"),

    URL8912("REGISCUENTASPORPAGARCGRCONTROLADORURL8912", "4002"),

    URL10394("REGISCUENTASPORPAGARCGRCONTROLADORURL10394", "45020"),

    URL7573("REGISCUENTASPORPAGARCGRCONTROLADORURL7573", "7007");

    private final String key;
    private final String value;

    private RegiscuentasporpagarcgrControladorUrlEnum(String key,
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
