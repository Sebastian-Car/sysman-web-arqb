/*
 * EstComparativoEjecPptalControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum EstComparativoEjecPptalControladorUrlEnum {

    URL5083("ESTCOMPARATIVOEJECPPTALCONTROLADORURL5083",
                    "94036"),

    URL3962("ESTCOMPARATIVOEJECPPTALCONTROLADORURL3962",
                    "7001"),

    URL6083("ESTCOMPARATIVOEJECPPTALCONTROLADORURL6083",
                    "94034"),

    URL4744("ESTCOMPARATIVOEJECPPTALCONTROLADORURL4744",
                    "4001"),

    URL4332("ESTCOMPARATIVOEJECPPTALCONTROLADORURL4332",
                    " listames1 = service.getListado(ConectorPool.ESQUEMA_SYSMAN, \"SELECT NUMERO\" + \" FROM MES\" + \" WHERE COMPANIA = '\" + compania + \"'\" + \" AND ANO = \" + anio + \"\" + \" AND NUMERO NOT IN (0,13)\");");

    private final String key;
    private final String value;

    private EstComparativoEjecPptalControladorUrlEnum(String key,
        String value) {
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
