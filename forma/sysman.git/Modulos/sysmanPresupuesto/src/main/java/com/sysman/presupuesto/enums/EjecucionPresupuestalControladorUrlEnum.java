package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum EjecucionPresupuestalControladorUrlEnum {

    URL12000("EJECUCIONPRESUPUESTALURL12000", "4001"),

    URL12001("EJECUCIONPRESUPUESTALURL12001", "7001"),

    URL12002("EJECUCIONPRESUPUESTALURL12002", "430043"),
    
    URL12003("EJECUCIONPRESUPUESTALURL12003", "430045");

    private final String key;
    private final String value;

    private EjecucionPresupuestalControladorUrlEnum(String key, String value)
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