/*
 * SubresoluciondiansControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubresoluciondiansControladorUrlEnum {

    URL1853001("SubresoluciondiansControladorUrlEnum1853001",
                    "1853001"),
	URL1848006("SubresoluciondiansControladorUrlEnum1848006",
            "1848006"),
	URL1860001("SubresoluciondiansControladorUrlEnum1860001",
            "1860001"),
	URL1896001("SubresoluciondiansControladorUrlEnum1896001",
            "1896001"),
	URL1896002("SubresoluciondiansControladorUrlEnum11896002",
            "1896002"),
	URL1896003("SubresoluciondiansControladorUrlEnum11896003",
            "1896003");

    private final String key;
    private final String value;

    private SubresoluciondiansControladorUrlEnum(String key, String value) {
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
