/*
 * EliminarComprobanteControladorUrlEnum
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
public enum EliminarComprobanteControladorUrlEnum {

    URL5748("ELIMINARCOMPROBANTECONTROLADORURL5748", "39027"),

    URL5124("ELIMINARCOMPROBANTECONTROLADORURL5124", ""),

    URL8488("ELIMINARCOMPROBANTECONTROLADORURL8488", "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"DETALLE_COMPROBANTE_CNT\","),

    URL3998("ELIMINARCOMPROBANTECONTROLADORURL3998", "15007"),

    URL2901("ELIMINARCOMPROBANTECONTROLADORURL2901", "72028"),

    URL2595("ELIMINARCOMPROBANTECONTROLADORURL2595", "4001"),

    URL7652("ELIMINARCOMPROBANTECONTROLADORURL7652", "72039"),

    URL1802("ELIMINARCOMPROBANTECONTROLADORURL1802", "75009"),

    URL1820("ELIMINARCOMPROBANTECONTROLADORURL1802", "118001"),
    
    URL1914001("COMPROBANTECNTSCONTROLADORURL1914001","1914001");

    private final String key;
    private final String value;

    private EliminarComprobanteControladorUrlEnum(String key, String value)
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
