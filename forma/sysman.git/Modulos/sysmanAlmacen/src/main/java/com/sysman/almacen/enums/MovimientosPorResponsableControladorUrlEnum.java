/*
 * AuxiliarmovimientosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MovimientosPorResponsableControladorUrlEnum {

    URL2613("AUXILIARMOVIMIENTOSCONTROLADORURL2613", "112125"),

    URL3383("AUXILIARMOVIMIENTOSCONTROLADORURL3383", "112127"),
    
    URL7458("AUXILIARMOVIMIENTOSCONTROLADORURL7458", "61012"),
    
    URL6471("AUXILIARMOVIMIENTOSCONTROLADORURL6471", "61031");

    private final String key;
    private final String value;

    private MovimientosPorResponsableControladorUrlEnum(String key, String value) {
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
