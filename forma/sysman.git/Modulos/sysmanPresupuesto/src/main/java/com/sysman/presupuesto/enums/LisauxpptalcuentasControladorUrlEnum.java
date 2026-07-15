/*
 * LisauxpptalcuentasControladorUrlEnum
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
public enum LisauxpptalcuentasControladorUrlEnum {

    URL6874("LISAUXPPTALCUENTASCONTROLADORURL6874",
                    "45016"),

    URL8232("LISAUXPPTALCUENTASCONTROLADORURL8232",
                    "14075"),

    URL6157("LISAUXPPTALCUENTASCONTROLADORURL6157",
                    "45014"),

    URL9520("LISAUXPPTALCUENTASCONTROLADORURL9520",
                    "20011"),

    URL8835("LISAUXPPTALCUENTASCONTROLADORURL8835",
                    "20013"),

    URL7713("LISAUXPPTALCUENTASCONTROLADORURL7713",
                    "14001"),

    URL4857("LISAUXPPTALCUENTASCONTROLADORURL4857",
                    "25008"),

    URL5448("LISAUXPPTALCUENTASCONTROLADORURL5448",
                    "25012"),

    URL5449("LISAUXPPTALCUENTASCONTROLADORURL5449",
                    "23010"),

    URL5450("LISAUXPPTALCUENTASCONTROLADORURL23019",
                    "23019"),

    URL5451("LISAUXPPTALCUENTASCONTROLADORURL5451",
                    "124002"),

    URL5452("LISAUXPPTALCUENTASCONTROLADORURL5450",
                    "25014"),

    URL5453("LISAUXPPTALCUENTASCONTROLADORURL5450",
                    "25051"),

    URL5584("LISAUXPPTALCUENTASCONTROLADORURL5584",
                    "13001"),

    URL5585("LISAUXPPTALCUENTASCONTROLADORURL5585",
                    "13035");

    private final String key;
    private final String value;

    private LisauxpptalcuentasControladorUrlEnum(String key, String value) {
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
