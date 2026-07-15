/*
 * CertificadoestratificacionsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.serviciospublicos.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CertificadoestratificacionsControladorUrlEnum {

    URL260("CERTIFICADOESTRATIFICACIONSCONTROLADORURL260", "104012"),

    URL261("CERTIFICADOESTRATIFICACIONSCONTROLADORURL261", "104013"),

    URL8825("CERTIFICADOESTRATIFICACIONSCONTROLADORURL8825", "214005"),

    URL9613("CERTIFICADOESTRATIFICACIONSCONTROLADORURL9613", "213018");

    private final String key;
    private final String value;

    private CertificadoestratificacionsControladorUrlEnum(String key,
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
