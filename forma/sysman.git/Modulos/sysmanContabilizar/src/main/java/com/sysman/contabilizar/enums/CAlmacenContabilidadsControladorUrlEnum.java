/*
 * CAlmacenContabilidadsControladorUrlEnum
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
public enum CAlmacenContabilidadsControladorUrlEnum {

    URL13910("CALMACENCONTABILIDADSCONTROLADORURL13910",
                    "16118"),

    URL16316("CALMACENCONTABILIDADSCONTROLADORURL16316",
                    "16118"),

    URL17553("CALMACENCONTABILIDADSCONTROLADORURL17553",
                    "16118"),

    URL12710("CALMACENCONTABILIDADSCONTROLADORURL12710",
                    "16118"),

    URL15110("CALMACENCONTABILIDADSCONTROLADORURL15110",
                    "16118"),

    URL9770("CALMACENCONTABILIDADSCONTROLADORURL9770",
                    " listaUnidad = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR1481_nuevo:TS62:TBCB4867\", \"SELECT \" + \" UNIDAD.UNIDAD, \" + \" UNIDAD.NOMBRE \" + \" FROM \" + \" UNIDAD \" + \" \","),

    URL10311("CALMACENCONTABILIDADSCONTROLADORURL10311",
                    "16118"),

    URL11510("CALMACENCONTABILIDADSCONTROLADORURL11510",
                    "16118"),

    URL18791("CALMACENCONTABILIDADSCONTROLADORURL18791",
                    "16118"),

    URL8935("CALMACENCONTABILIDADSCONTROLADORURL8935",
                    "16120"),

    URL20029("CALMACENCONTABILIDADSCONTROLADORURL20029",
                    "16118"),

    URL6969("CALMACENCONTABILIDADSCONTROLADORURL6969",
                    "11200G"),

    URL1313("CALMACENCONTABILIDADSCONTROLADORURL1313",
                    "112111"),

    URL2525("CALMACENCONTABILIDADSCONTROLADORURL2525",
                    "112112"),
    
    URL3837("CALMACENCONTABILIDADSCONTROLADORURL2525",
                    "4001"),
    
    URL6475("CALMACENCONTABILIDADSCONTROLADORURL2525",
                    "4027")

    ;

    private final String key;
    private final String value;

    private CAlmacenContabilidadsControladorUrlEnum(String key, String value) {
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
