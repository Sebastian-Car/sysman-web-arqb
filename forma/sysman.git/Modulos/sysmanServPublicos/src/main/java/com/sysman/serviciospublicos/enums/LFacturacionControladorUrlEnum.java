/*
 * LFacturacionControladorUrlEnum
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
public enum LFacturacionControladorUrlEnum {
    URL13622("LFACTURACIONCONTROLADORURL13622", "214067"),

    URL13660("LFACTURACIONCONTROLADORURL13660", "214031"),

    URL16008("LFACTURACIONCONTROLADORURL16008", "242008"),

    URL16796("LFACTURACIONCONTROLADORURL16796", "310005"),

    URL22996("LFACTURACIONCONTROLADORURL22996", "107008"),

    URL17839("LFACTURACIONCONTROLADORURL17839", "107006"),

    URL15001("LFACTURACIONCONTROLADORURL15001", "310005"),

    URL19207("LFACTURACIONCONTROLADORURL19207", "213142"),

    URL22012("LFACTURACIONCONTROLADORURL22012", "213146"),

    URL14203("LFACTURACIONCONTROLADORURL14203", "242005"),

    URL20914("LFACTURACIONCONTROLADORURL20914", "213144");
    private final String key;
    private final String value;

    private LFacturacionControladorUrlEnum(String key, String value)
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
