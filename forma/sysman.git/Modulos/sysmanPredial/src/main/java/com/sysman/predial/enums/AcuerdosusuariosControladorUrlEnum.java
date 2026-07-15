/*
 * AcuerdosusuariosControladorUrlEnum
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
public enum AcuerdosusuariosControladorUrlEnum {

    URL14672("ACUERDOSUSUARIOSCONTROLADORURL14672",
                    "368003"),

    URL18701("ACUERDOSUSUARIOSCONTROLADORURL18701",
                    "368004"),

    URL17796("ACUERDOSUSUARIOSCONTROLADORURL17796",
                    "371003"),

    URL13477("ACUERDOSUSUARIOSCONTROLADORURL13477",
                    "4002"),

    URL16528("ACUERDOSUSUARIOSCONTROLADORURL16528",
                    " listaCmbAcuerdosE = new RegistroDataModel(ConectorPool.ESQUEMA_SYSMAN, \":FRFR960:TBCB3161\", \"SELECT \" + \" IP_ACUERDOS.CODIGOACUERDO, \" + \" IP_ACUERDOS.PREANOI, \" + \" IP_ACUERDOS.PREANO, \" + \" IP_ACUERDOS.RESOLUCION, \" + \" CASE IP_ACUERDOS.CANCELADO WHEN 0 THEN 'No' ELSE 'Si' END CANCELADO, \" + \" CASE IP_ACUERDOS.ANULADO WHEN 0 THEN 'No' ELSE 'Si' END ANULADO, \" + \" IP_ACUERDOS.PREDIO, \" + \" IP_ACUERDOS.APLICA_DSCESP \" + \" FROM \" + \" IP_ACUERDOS \" + \" WHERE \" + \" IP_ACUERDOS.COMPANIA = '\" + compania + \"' \" + \" AND IP_ACUERDOS.PREDIO = '\" + codigoPredio + \"' \" + \" \","),

    URL14084("ACUERDOSUSUARIOSCONTROLADORURL14084",
                    "104008"),

    URL15258("ACUERDOSUSUARIOSCONTROLADORURL15258",
                    "371001"),

    URL6969("ACUERDOSUSUARIOSCONTROLADORURL6969",
                    "368001");

    private final String key;
    private final String value;

    private AcuerdosusuariosControladorUrlEnum(String key, String value) {
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
