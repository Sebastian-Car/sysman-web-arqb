/*
 * NatSubLicRemunsControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * 
 * version 2.0
 * 
 * Se cambió el servicio asociado a la URL6451 de 707004 a 700001 
 * por requerimiento de la ANE.
 * 
 * version 3.0 @dnino
 *  
 *  * Se cambió el servicio asociado a la URL7452 de 707003 a 700001 
 * para evitar cambios en el controlador princial.
 * 
 */
public enum NatSubLicRemunsControladorUrlEnum {

    URL6502("NATSUBLICREMUNSCONTROLADORURL6502", "627004"),

    URL8754("NATSUBLICREMUNSCONTROLADORURL8754", "626001"),

    URL4571("NATSUBLICREMUNSCONTROLADORURL4571", "626015"),

    URL6201("NATSUBLICREMUNSCONTROLADORURL6201", "626005"),

    URL4968("NATSUBLICREMUNSCONTROLADORURL4968", "626004"),

    URL5473("NATSUBLICREMUNSCONTROLADORURL5473", "62600D"),

    URL5644("NATSUBLICREMUNSCONTROLADORURL5644", "626003"),

    URL7452("NATSUBLICREMUNSCONTROLADORURL5644", "700001"),
    
    URL6541("NATSUBLICREMUNSCONTROLADORURL6541", "700001"),

    URL4234("NATSUBLICREMUNSCONTROLADORURL4234", "471002"),

    URL7850("NATSUBLICREMUNSCONTROLADORURL7850", "627006"),

    URL4698("NATSUBLICREMUNSCONTROLADORURL4698", "7031"),

    URL5784("NATSUBLICREMUNSCONTROLADORURL5784", "471031"),

    URL5785("NATSUBLICREMUNSCONTROLADORURL5785", "626017"),

    URL5786("NATSUBLICREMUNSCONTROLADORURL5786", "627010"),

    URL5788("NATSUBLICREMUNSCONTROLADORURL5788", "626019");

    private final String key;
    private final String value;

    private NatSubLicRemunsControladorUrlEnum(String key, String value)
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
