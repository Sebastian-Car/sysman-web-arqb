/*
 * MedidorGeoreferencialControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum MedidorGeoreferencialControladorUrlEnum {

    URL9253("MEDIDORGEOREFERENCIALCONTROLADORURL9253", "214006"),

    URL8568("MEDIDORGEOREFERENCIALCONTROLADORURL8568", "214005"),

    URL10053("MEDIDORGEOREFERENCIALCONTROLADORURL10053", "289002"),

    URL10814("MEDIDORGEOREFERENCIALCONTROLADORURL10814", "289003"),

    URL11679("MEDIDORGEOREFERENCIALCONTROLADORURL11679", "213168"),

    URL444("MEDIDORGEOREFERENCIALCONTROLADORURL444", "213172"),

    URL12634("MEDIDORGEOREFERENCIALCONTROLADORURL12634", "213170"),

    URL490("MEDIDORGEOREFERENCIALCONTROLADORURL490", "213174"),

    URL809("MEDIDORGEOREFERENCIALCONTROLADORURL809", "213176");

    private final String key;
    private final String value;

    private MedidorGeoreferencialControladorUrlEnum(String key, String value)
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
