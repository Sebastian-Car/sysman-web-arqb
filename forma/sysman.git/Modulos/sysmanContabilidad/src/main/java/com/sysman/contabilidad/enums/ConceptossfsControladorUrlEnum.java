/*
 * ConceptossfsControladorUrlEnum
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
public enum ConceptossfsControladorUrlEnum {

    URL5857("CONCEPTOSSFSCONTROLADORURL5857",
                    "8005"),

    URL7039("CONCEPTOSSFSCONTROLADORURL7039",
                    " listaCODIGOE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FR574_nuevo:TBCB2070\", \"SELECT RETENCIONES.CODIGO, \n\" + \" RETENCIONES.NOMBRE \n\" + \" FROM RETENCIONES\n\" + \" WHERE RETENCIONES.COMPANIA = '\" + compania + \"' \n\" + \" AND RETENCIONES.TIPO = '\" + auxTipo + \"' \n\" + \" AND RETENCIONES.ANO = \" + anio + \"\n\" + \" ORDER BY RETENCIONES.COMPANIA, RETENCIONES.TIPO, RETENCIONES.ANO \","),

    URL9032("CONCEPTOSSFSCONTROLADORURL9032",
                    "Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, \"RETENCIONESCONCEPTO\","),

    URL4251("CONCEPTOSSFSCONTROLADORURL4251",
                    " listaRetencionesconcepto = service.getListado( ConectorPool.ESQUEMA_SYSMAN, \"SELECT DISTINCT\" + \" RETENCIONESCONCEPTO.COMPANIA,\n\" + \" RETENCIONESCONCEPTO.TIPO,\n\" + \" RETENCIONESCONCEPTO.ANO,\n\" + \" RETENCIONESCONCEPTO.CODIGO,\n\" + \" RETENCIONESCONCEPTO.TIPOCOBRO,\n\" + \" RETENCIONESCONCEPTO.CONCEPTO,\n\" + \" TIPORETENCION.NOMBRE,\n\" + \" RETENCIONES.NOMBRE AS NOMBRERETENCION\n\" + \" FROM RETENCIONESCONCEPTO INNER JOIN TIPORETENCION\n\" + \" ON TIPORETENCION.CODIGO = RETENCIONESCONCEPTO.TIPO\n\" + \" INNER JOIN RETENCIONES\n\" + \" ON TIPORETENCION.CODIGO = RETENCIONES.TIPO\n\" + \" AND RETENCIONESCONCEPTO.ANO = RETENCIONES.ANO\" + \" AND RETENCIONESCONCEPTO.CODIGO = RETENCIONES.CODIGO\n\" + \" WHERE RETENCIONESCONCEPTO.COMPANIA='\" + compania + \"' \" + \" AND RETENCIONESCONCEPTO.ANO='\" + registro.getCampos().get(\"ANO\") + \"' AND RETENCIONESCONCEPTO.TIPOCOBRO='\" + registro.getCampos().get(\"TIPOCOBRO\") + \"' AND RETENCIONESCONCEPTO.CONCEPTO='\" + registro.getCampos().get(\"CODIGO\") + \"'\","),

    URL11324("CONCEPTOSSFSCONTROLADORURL11324",
                    "Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, \"RETENCIONESCONCEPTO\","),

    URL7893("CONCEPTOSSFSCONTROLADORURL7893",
                    "Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, \"RETENCIONESCONCEPTO\","),

    URL1666("CONCEPTOSSFSCONTROLADORURL1666", "29035"),

    URL6969("CONCEPTOSSFSCONTROLADORURL6969", "72016"),

    URL6227("CONCEPTOSSFSCONTROLADORURL6227",
                    "12001");

    private final String key;
    private final String value;

    private ConceptossfsControladorUrlEnum(String key, String value) {
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
