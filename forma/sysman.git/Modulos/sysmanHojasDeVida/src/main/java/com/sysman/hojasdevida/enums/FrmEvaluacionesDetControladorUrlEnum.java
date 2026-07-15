/*
 * EntidadesCapacitacionControladorUrlEnum
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
public enum FrmEvaluacionesDetControladorUrlEnum {

    URL15084("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL15084", "210101"),
    URL4780("ENTIDADESCAPACITACIONCONTROLADORURL4780", "14096"),
    URL45768("FRMEVALUACIONESDETPRINCIPALCONTROLADORURL45768", "939017"),
    URL98715("FRMEVALUACIONESDETPRINCIPALCONTROLADORURL98715", "939019"),

    URL85714("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL85714", "939021"),
    URL74159("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL74159", "939023"),
    URL12754("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL12754", "939025"),
    URL65213("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL65213", "939027"),
    URL7227("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL7227", "210115"),
    URL3933("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL3933", "939029"),
    URL1234("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL1234", "104056"),
    URL8574("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL8574", "98500N"),
    URL4217("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL4217", "947007"),
    URL5248("FRMEVALUACIONESSUBDETPRINCIPALCONTROLADORURL5248", "947008");

    private final String key;
    private final String value;

    private FrmEvaluacionesDetControladorUrlEnum(String key, String value) {
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
