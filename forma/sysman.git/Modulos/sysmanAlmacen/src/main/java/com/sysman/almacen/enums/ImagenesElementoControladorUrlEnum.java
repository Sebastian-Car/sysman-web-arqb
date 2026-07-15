/*
 * InventarioFisicoPorDependenciaControladorUrlEnum
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
public enum ImagenesElementoControladorUrlEnum {

    URL7530("IMAGENESELEMENTOCONTROLADORURL7530",
                    "112117"),

    URL8418("IMAGENESELEMENTOCONTROLADORURL8418",
                    "141091"),

    URL8419("IMAGENESELEMENTOCONTROLADORURL8419",
                    "1059001"),

    URL8420("IMAGENESELEMENTOCONTROLADORURL8420",
                    "1059002"),

    URL8421("IMAGENESELEMENTOCONTROLADORURL8421",
                    "1059003"),

    URL8422("IMAGENESELEMENTOCONTROLADORURL8422",
                    "105900U"),

    URL8423("IMAGENESELEMENTOCONTROLADORURL8423",
                    "105900C"),

    URL8424("IMAGENESELEMENTOCONTROLADORURL8424",
                    "1059004"),

    URL8425("IMAGENESELEMENTOCONTROLADORURL8425",
                    "1059005"),

    URL8426("IMAGENESELEMENTOCONTROLADORURL8426",
                    "105900D"),

    URL8427("IMAGENESELEMENTOCONTROLADORURL8427",
                    "105900R"),

    URL8428("IMAGENESELEMENTOCONTROLADORURL8428",
                    "105900G")

    ;

    private final String key;
    private final String value;

    private ImagenesElementoControladorUrlEnum(String key,
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
