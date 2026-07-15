/*
 * IprogramaciondemantenimientoexcelControladorUrlEnum
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
public enum IprogramaciondemantenimientoexcelControladorUrlEnum {
    URL4222("IPROGRAMACIONDEMANTENIMIENTOEXCELCONTROLADORURL4222", "447002"),

    URL4298("IPROGRAMACIONDEMANTENIMIENTOEXCELCONTROLADORURL4298", "112088"),

    URL3542("IPROGRAMACIONDEMANTENIMIENTOEXCELCONTROLADORURL3542",
                    "112086");

    private final String key;
    private final String value;

    private IprogramaciondemantenimientoexcelControladorUrlEnum(String key, String value)
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
