/*
 * FrmesmetasControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.precontractual.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum FrmesmetasControladorUrlEnum {

    URL249("FRMESMETASCONTROLADORURL249", "498001"),

    URL278("FRMESMETASCONTROLADORURL278", "433003"),

    URL27991("FRMESMETASCONTROLADORURL27991",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"BP_PLAN_INDICATIVO_METAS\","),

    URL30041("FRMESMETASCONTROLADORURL30041",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"ES_PROY_METAS\","),

    URL34307("FRMESMETASCONTROLADORURL34307",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"BP_PLAN_INDICATIVO_METAS\","),

    URL16640("FRMESMETASCONTROLADORURL16640",
                    " listaDependenciaE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR601_nuevo:TBCB2222\", \"SELECT DISTINCT DEPENDENCIA.CODIGO, \" + \" DEPENDENCIA.NOMBRE, \" + \" DEPENDENCIA.MOVIMIENTO \" + \" FROM DEPENDENCIA\" + \" WHERE DEPENDENCIA.COMPANIA= '\" + compania + \"'\" + \" AND DEPENDENCIA.MOVIMIENTO <> 0\","),

    URL23539("FRMESMETASCONTROLADORURL23539",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"ES_PROY_METAS\","),

    URL15946("FRMESMETASCONTROLADORURL15946", "62005"),

    URL30062("FRMESMETASCONTROLADORURL30062",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"BP_PLAN_INDICATIVO_METAS\","),

    URL25645("FRMESMETASCONTROLADORURL25645",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"ES_PROY_METAS\","),

    URL10992("FRMESMETASCONTROLADORURL10992", "503001");

    private final String key;
    private final String value;

    private FrmesmetasControladorUrlEnum(String key, String value)
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
