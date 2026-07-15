/*
 * DesviacionesControladorUrlEnum
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
public enum DesviacionesControladorUrlEnum {

    URL12137("DESVIACIONESCONTROLADORURL12137", "104010"),

    URL43490("DESVIACIONESCONTROLADORURL43490", "213038"),

    URL48462("DESVIACIONESCONTROLADORURL48462", "213041"),

    URL15423("DESVIACIONESCONTROLADORURL15423", "365001"),

    URL51952("DESVIACIONESCONTROLADORURL51952", "213044"),

    URL17756("DESVIACIONESCONTROLADORURL17756", "365002"),

    URL51232("DESVIACIONESCONTROLADORURL51232", "104014"),

    URL18455("DESVIACIONESCONTROLADORURL18455", "104011"),

    URL30992("DESVIACIONESCONTROLADORURL30992", "333002"),

    URL18179("DESVIACIONESCONTROLADORURL18179", "334001"),

    URL50568("DESVIACIONESCONTROLADORURL50568", "332002"),

    URL44795("DESVIACIONESCONTROLADORURL44795",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, desviacionesCarta,"),

    URL54722("DESVIACIONESCONTROLADORURL54722",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, cartaPregunta,"),

    URL53718("DESVIACIONESCONTROLADORURL53718",
                    " Registro rsTotal = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, \"SELECT NVL(SUM(SP_DESVIACIONES_HISTORIA.CONSUMOACUREAL - SP_DESVIACIONES_HISTORIA.CONSUMOACUFACTURADO),0) PENDIENTE,\" + \" NVL(SUM(SP_DESVIACIONES_HISTORIA.VALORACUREAL - SP_DESVIACIONES_HISTORIA.VALORACUFACTURADO),0) PENDIENTEACU,\" + \" NVL(SUM(SP_DESVIACIONES_HISTORIA.VALORALCREAL - SP_DESVIACIONES_HISTORIA.VALORALCFACTURADO),0) PENDIENTEALC,\" + \" NVL(SUM(SP_DESVIACIONES_HISTORIA.METROSMICRO),"),

    URL47091("DESVIACIONESCONTROLADORURL47091",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, cartaPregunta,"),

    URL58012("DESVIACIONESCONTROLADORURL58012",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, desviacionesHistoria,"),

    URL41173("DESVIACIONESCONTROLADORURL41173",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"SP_DESVIACIONES_CARTA\",");

    private final String key;
    private final String value;

    private DesviacionesControladorUrlEnum(String key, String value)
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
