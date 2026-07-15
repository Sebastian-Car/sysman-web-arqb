package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ActsaldodispptalControladorUrlEnum {

    URL12000("ActsaldodispptalControladorURL12000", "4062"),

    URL12001("ActsaldodispptalControladorURL12001", "75040"),

    URL12002("ActsaldodispptalControladorURL12002", "75042");

    private final String key;
    private final String value;

    private ActsaldodispptalControladorUrlEnum(String key, String value)
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