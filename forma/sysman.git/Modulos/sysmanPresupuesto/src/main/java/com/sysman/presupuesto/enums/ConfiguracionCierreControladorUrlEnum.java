package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 *
 * @version 1.0
 *
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum ConfiguracionCierreControladorUrlEnum {

    URL12000("CONFIGURACIONCIERRECONTROLADORURL12000", "1765001"),

    URL12001("CONFIGURACIONCIERRECONTROLADORURL12001", "25045"),
    
    URL12002("CONFIGURACIONCIERRECONTROLADORURL12002", "1765003"),
    
    URL12003("CONFIGURACIONCIERRECONTROLADORURL12003", "1765004");

    private final String key;
    private final String value;

    private ConfiguracionCierreControladorUrlEnum(String key, String value)
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