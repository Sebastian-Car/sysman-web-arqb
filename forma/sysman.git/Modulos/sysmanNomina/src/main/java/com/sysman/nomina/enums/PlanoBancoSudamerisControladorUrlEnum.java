/*
 * PrepararEmbargosControladorUrlEnum
 *
 * 1.0
 *
 * 19/10/2017
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum PlanoBancoSudamerisControladorUrlEnum {

    URL28520("PLANOBANCOSUDAMERICCONTROLADORURL28520", "471002"), // Ano

    URL28521("PLANOBANCOSUDAMERICCONTROLADORURL28521", "7024"), // Mes

    URL28522("PLANOBANCOSUDAMERICCONTROLADORURL28522", "471025"), // Periodo

    URL28523("PLANOBANCOSUDAMERICCONTROLADORURL28523", "459001"), // Banco
    
    URL28524("PLANOBANCOSUDAMERICCONTROLADORURL28523", "459016"); //BancoSudameris
    
    private final String key;
    private final String value;

    private PlanoBancoSudamerisControladorUrlEnum(String key, String value)
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
