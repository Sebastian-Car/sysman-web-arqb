/*
 * ModificacionesDeudaUsuarioControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum DatostercerofinanciablesControladorUrlEnum {

    URL5304("DATOSTERCEROFINANCIABLESCONTROLADORURL5304", "306001"),

    URL5305("DATOSTERCEROFINANCIABLESCONTROLADORURL5305", "306002");

    private final String key;
    private final String value;

    private DatostercerofinanciablesControladorUrlEnum(String key,
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
