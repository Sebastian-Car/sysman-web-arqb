/*
 * CAlmacenContabilidadTrasControladorUrlEnum
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
public enum CAlmacenContabilidadTrasControladorUrlEnum {

    URL13180("CALMACENCONTABILIDADTRASCONTROLADORURL13180",
                    "16057"),

    URL16535("CALMACENCONTABILIDADTRASCONTROLADORURL16535",
                    "16118"),

    URL14203("CALMACENCONTABILIDADTRASCONTROLADORURL14203",
                    "16057"),

    URL17787("CALMACENCONTABILIDADTRASCONTROLADORURL17787",
                    "16118"),

    URL15235("CALMACENCONTABILIDADTRASCONTROLADORURL15235",
                    "16118"),

    URL19060("CALMACENCONTABILIDADTRASCONTROLADORURL19060",
                    "16118"),

    URL10599("CALMACENCONTABILIDADTRASCONTROLADORURL10599",
                    "139012"),

    URL6969("CALMACENCONTABILIDADTRASCONTROLADORURL6969",
                    "183006"),

    URL8888("CALMACENCONTABILIDADTRASCONTROLADORURL8888",
                    "183008"),

    URL7777("CALMACENCONTABILIDADTRASCONTROLADORURL8888",
                    "183009"),

    URL5555("CALMACENCONTABILIDADTRASCONTROLADORURL5555",
                    "18300D")

    ;

    private final String key;
    private final String value;

    private CAlmacenContabilidadTrasControladorUrlEnum(String key,
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
