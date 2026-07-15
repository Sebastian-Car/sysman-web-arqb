/*
 * SubavaluosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.predial.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum SubavaluosControladorUrlEnum {

    URL7257("SUBAVALUOSCONTROLADORURL7257",
                    " listatrpcodE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR758:TBCB2576\", \"SELECT \" + \" IP_TARIFAS.TRPCOD, \" + \" IP_TARIFAS.TRPDES, \" + \" IP_TARIFAS.TRPPOR, \" + \" IP_TARIFAS.TRPRAN \" + \" FROM \" + \" IP_TARIFAS \" + \" WHERE \" + \" IP_TARIFAS.COMPANIA = '\" + compania + \"' \" + \" AND IP_TARIFAS.TRPANO = \" + anoTarifa + \"\","),

    URL6511("SUBAVALUOSCONTROLADORURL6511",
                    "376012");

    private final String key;
    private final String value;

    private SubavaluosControladorUrlEnum(String key, String value)
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
