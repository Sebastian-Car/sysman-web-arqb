/*
 * CentroscostosControladorUrlEnum
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
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum CentroscostosControladorUrlEnum {

    URL15290("CENTROSCOSTOSCONTROLADORURL15290",
                    "28003"),

    URL5961("CENTROSCOSTOSCONTROLADORURL5961",
                    "28001"), 

    URL10548("CENTROSCOSTOSCONTROLADORURL10548",
                    " listacbCentroCostoDisE = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR36_nuevo:TS3:TBCB2185\", \"SELECT CODIGO, NOMBRE, MOVIMIENTO \" + \" FROM CENTRO_COSTO \" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = \" + anoSubSeleccionado + \"\","),

    URL8755("CENTROSCOSTOSCONTROLADORURL8755",
                    "20001"),

    URL8008("CENTROSCOSTOSCONTROLADORURL8008",
                    " listaAnoQR = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT ANO.NUMERO \" + \" FROM ANO \" + \" WHERE ANO.COMPANIA = '\" + compania + \"' \" + \" AND\" + \" ANO.NUMERO <> 0 ORDER BY NUMERO DESC\");"),

    URL7396("CENTROSCOSTOSCONTROLADORURL7396",
                    "30001"),

    URL8413("CENTROSCOSTOSCONTROLADORURL8413",
                    " listaANOSUB = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT NUMERO \" + \" FROM ANO \" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" ORDER BY NUMERO DESC\");"),

    URL7706("CENTROSCOSTOSCONTROLADORURL7706",
                    "31001"),

    URL14001("CENTROSCOSTOSCONTROLADORURL14001",
                    "28002"),

    URL6997("CENTROSCOSTOSCONTROLADORURL6997", "4001"),

    URL10806("CENTROSCOSTOSCONTROLADORURL10806",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"DISTRIBUCION_CENTROCOSTO\","),

    URL17084("CENTROSCOSTOSCONTROLADORURL17084",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"CENTRO_COSTOESP\","),

    URL9362("CENTROSCOSTOSCONTROLADORURL9362",
                    " listaCENTROCOSTOE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR36_nuevo:TS3:TBCB75\", \"SELECT CENTRO_COSTO.CODIGO, CENTRO_COSTO.NOMBRE , CENTRO_COSTO.MOVIMIENTO \" + \" FROM CENTRO_COSTO \" + \" WHERE CENTRO_COSTO.COMPANIA = '\" + compania + \"'\" + \" AND CENTRO_COSTO.ANO = \" + anoNomina + \"\","),

    URL13997("CENTROSCOSTOSCONTROLADORURL13997",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"DISTRIBUCION_CENTROCOSTO\","),
    URL4471("CENTROSCOSTOSCONTROLADORURL4471",
                    " listaSubdistribucionccto = service.getListado( ConectorPool.ESQUEMA_SYSMAN, \"SELECT DISTRIBUCION_CENTROCOSTO.COMPANIA, DISTRIBUCION_CENTROCOSTO.ANO, \" + \" DISTRIBUCION_CENTROCOSTO.CENTRO_COSTO, \" + \" DISTRIBUCION_CENTROCOSTO.PORCENTAJE, \" + \" DISTRIBUCION_CENTROCOSTO.COMPANIA, \" + \" (SELECT NOMBRE FROM CENTRO_COSTO WHERE COMPANIA = DISTRIBUCION_CENTROCOSTO.COMPANIA \" + \" AND ANO = DISTRIBUCION_CENTROCOSTO.ANO\" + \" AND CODIGO = DISTRIBUCION_CENTROCOSTO.CENTRO_COSTO ) NOMBRE_CCTO \" + \" FROM DISTRIBUCION_CENTROCOSTO WHERE COMPANIA='\" + compania + \"' AND ANO=\" + registro.getCampos().get(\"ANO\"),"),

    URL9966("CENTROSCOSTOSCONTROLADORURL9966",
                    " listacbCentroCostoDis = new RegistroDataModel( ConectorPool.ESQUEMA_SYSMAN, \":FR36_nuevo:TS3:TBCB2185\", \"SELECT CODIGO, NOMBRE, CASE WHEN MOVIMIENTO NOT IN (0) THEN 'Si' ELSE 'No' END MOVIMIENTO \" + \" FROM CENTRO_COSTO \" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = \" + anoSubSeleccionado + \"\","),

    URL11992("CENTROSCOSTOSCONTROLADORURL11992",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"DISTRIBUCION_CENTROCOSTO\","),
    URL20084("CENTROSCOSTOSCONTROLADORURL20084",
            "20084");

    private final String key;
    private final String value;

    private CentroscostosControladorUrlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
