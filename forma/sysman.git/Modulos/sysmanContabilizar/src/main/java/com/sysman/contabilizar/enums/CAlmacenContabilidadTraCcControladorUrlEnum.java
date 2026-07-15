/*
 * CAlmacenContabilidadTraCcControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum CAlmacenContabilidadTraCcControladorUrlEnum {

    URL12254("CALMACENCONTABILIDADTRACCCONTROLADORURL12254",
                    "16118"),

    URL16355("CALMACENCONTABILIDADTRACCCONTROLADORURL16355",
                    "16118"),

    URL10559("CALMACENCONTABILIDADTRACCCONTROLADORURL10559",
                    "16118"),

    URL13108("CALMACENCONTABILIDADTRACCCONTROLADORURL13108",
                    "16118"),

    URL11404("CALMACENCONTABILIDADTRACCCONTROLADORURL11404",
                    "16118"),

    URL15730("CALMACENCONTABILIDADTRACCCONTROLADORURL15730",
                    "16118"),

    URL17871("CALMACENCONTABILIDADTRACCCONTROLADORURL17871",
                    "20065"),

    URL17066("CALMACENCONTABILIDADTRACCCONTROLADORURL17066",
                    "139012"),

    URL9999("CALMACENCONTABILIDADTRACCCONTROLADORURL9999",
                    "745001"),
    
    URL1223("CALMACENCONTABILIDADTRACCCONTROLADORURL", "34001"),
    
    URL745006("CALMACENCONTABILIDADTRACCCONTROLADORURL", "745006"),;

    private final String key;
    private final String value;

    private CAlmacenContabilidadTraCcControladorUrlEnum(String key,
        String value)
    {
        this.key = key;
        this.value = value;
    }

    public String getKey()
    {
        return key;
    }

    public String getValue()
    {
        return value;
    }
}
