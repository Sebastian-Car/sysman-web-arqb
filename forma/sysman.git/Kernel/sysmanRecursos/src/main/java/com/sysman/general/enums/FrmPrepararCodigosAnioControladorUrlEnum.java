/*
 * EntidadesCapacitacionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.general.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum FrmPrepararCodigosAnioControladorUrlEnum {

    URL001("FRMPREPARARCODIGOSANIOCONTROLADORURLENUM001", "4001"), // 1684001

    URL002("FRMPREPARARCODIGOSANIOCONTROLADORURLENUM002", "4032"),

    URL003("FRMPREPARARCODIGOSANIOCONTROLADORURLENUM003", "1031005");

    private final String key;
    private final String value;

    private FrmPrepararCodigosAnioControladorUrlEnum(String key,
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
