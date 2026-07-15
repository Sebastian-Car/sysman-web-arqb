/*
 * FinanciablesfacturasControladorUrlEnum
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
public enum FinanciablesfacturasControladorUrlEnum {

    URL95086("FINANCIABLESFACTURASCONTROLADORURL95086", "213091"),

    URL20799("FINANCIABLESFACTURASCONTROLADORURL20799", "214051"),

    URL79530("FINANCIABLESFACTURASCONTROLADORURL79530", "227012"),

    URL77961("FINANCIABLESFACTURASCONTROLADORURL77961", "214050"),

    URL80142("FINANCIABLESFACTURASCONTROLADORURL80142", "307013"),

    URL26506("FINANCIABLESFACTURASCONTROLADORURL26506", "227009"),

    URL80902("FINANCIABLESFACTURASCONTROLADORURL80902", "307014"),

    URL22729("FINANCIABLESFACTURASCONTROLADORURL22729", "227014"),

    URL18854("FINANCIABLESFACTURASCONTROLADORURL18854", "215003"),

    URL72075("FINANCIABLESFACTURASCONTROLADORURL72075", "213080"),

    URL1947("FINANCIABLESFACTURASCONTROLADORURL1947", "307020"),

    URL19155("FINANCIABLESFACTURASCONTROLADORURL19155", "307009"),

    URL16211("FINANCIABLESFACTURASCONTROLADORURL16211", "307008"),

    URL28694("FINANCIABLESFACTURASCONTROLADORURL28694", "215013"),

    URL22880("FINANCIABLESFACTURASCONTROLADORURL22880", "227001");

    private final String key;
    private final String value;

    private FinanciablesfacturasControladorUrlEnum(String key, String value)
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
