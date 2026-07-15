/*
 * DmantenimpreventivosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DmantenimpreventivosControladorUrlEnum {

    URL32164("DMANTENIMPREVENTIVOSCONTROLADORURL32164",
                    "441001"),

    URL17950("DMANTENIMPREVENTIVOSCONTROLADORURL17950",
                    "445004"),

    URL15241("DMANTENIMPREVENTIVOSCONTROLADORURL15241",
                    "112083"),

    URL17486("DMANTENIMPREVENTIVOSCONTROLADORURL17486",
                    "444005"),

    URL19235("DMANTENIMPREVENTIVOSCONTROLADORURL19235",
                    "447001"),

    URL15935("DMANTENIMPREVENTIVOSCONTROLADORURL15935",
                    "445003"),

    URL18416("DMANTENIMPREVENTIVOSCONTROLADORURL18416",
                    "445001"),
    /**
     * 112095 getInventariosPagPlacaNoAnuladaPorElementoQuery
     */
    URL16224("DMANTENIMPREVENTIVOSCONTROLADORURL16224",
                    "112095"),

    URL001("DMANTENIMPREVENTIVOSCONTROLADORURL16224",
                    "443001"),
    
    URL002("DMANTENIMPREVENTIVOSCONTROLADORURL002",
                    "1714006");

    private final String key;
    private final String value;

    private DmantenimpreventivosControladorUrlEnum(String key, String value) {
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
