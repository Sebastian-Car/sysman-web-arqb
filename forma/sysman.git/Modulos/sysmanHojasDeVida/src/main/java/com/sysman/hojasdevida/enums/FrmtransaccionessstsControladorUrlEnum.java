/*
 * FrmtransaccionessstsControladorUrlEnum
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
 */
public enum FrmtransaccionessstsControladorUrlEnum {

    URL10934("FRMTRANSACCIONESSSTSCONTROLADORURL10934", "104056"),

    URL9964("FRMTRANSACCIONESSSTSCONTROLADORURL9964", "210066"),

    URL9204("FRMTRANSACCIONESSSTSCONTROLADORURL9204", "728003"),

    URL002("FRMTRANSACCIONESSSTSCONTROLADORURL9204", "104062"),

    URL1017("FRMTRANSACCIONESSSTSCONTROLADORURL1017", "727007"),

    URL457("FRMTRANSACCIONESSSTSCONTROLADORURL457", "1677001"),

    URL553("FRMTRANSACCIONESSSTSCONTROLADORURL553", "1678001")

    ;

    private final String key;
    private final String value;

    private FrmtransaccionessstsControladorUrlEnum(String key, String value)
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
