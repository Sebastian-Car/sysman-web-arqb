/*
 * EstadodetesoreriaaControladorUrlEnum
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
public enum EstadodetesoreriaaControladorUrlEnum {

    URL6322("ESTADODETESORERIAACONTROLADORURL6322", "16003"), URL4089(
                    "ESTADODETESORERIAACONTROLADORURL4089",
                    "4002"), URL4631(
                                    "ESTADODETESORERIAACONTROLADORURL4631",
                                    " listaMesInicial = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT NUMERO\" + \" FROM MES\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = \" + anoInicial + \"\" + \" AND NUMERO NOT IN 0\");"), URL5430(
                                                    "ESTADODETESORERIAACONTROLADORURL5430",
                                                    "16005"), URL5029(
                                                                    "ESTADODETESORERIAACONTROLADORURL5029",
                                                                    " listaMesFinal = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT NUMERO\" + \" FROM MES\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = \" + anoInicial + \"\" + \" AND NUMERO NOT IN 0\");");

    private final String key;
    private final String value;

    private EstadodetesoreriaaControladorUrlEnum(String key, String value) {
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
