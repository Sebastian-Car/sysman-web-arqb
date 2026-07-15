/*
 * CAlmacenContabilidadBajaControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilizar.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CAlmacenContabilidadBajaControladorUrlEnum {

    URL20888("CALMACENCONTABILIDADBAJACONTROLADORURL20888",
                    "183010"),

    URL14271("CALMACENCONTABILIDADBAJACONTROLADORURL14271",
                    "16118"),

    URL25587("CALMACENCONTABILIDADBAJACONTROLADORURL25587",
                    "183012"),

    URL11457("CALMACENCONTABILIDADBAJACONTROLADORURL11457",
                    "139019"),

    URL19121("CALMACENCONTABILIDADBAJACONTROLADORURL19121",
                    "183013"),

    URL28803("CALMACENCONTABILIDADBAJACONTROLADORURL28803",
                    "18300D");

    private final String key;
    private final String value;

    private CAlmacenContabilidadBajaControladorUrlEnum(String key,
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
