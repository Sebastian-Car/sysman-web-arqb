/*
 * ActualizaConfiguracionControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contabilidad.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores geenerados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 */
public enum GeneracionplanosbancosControladorUrlEnum {

    URL001("GENERACIONPLANOSBANCOS001", "36002"), // NUMERO DEL BANCO

    URL002("GENERACIONPLANOSBANCOS002", "4001"), // AÑO

    URL003("GENERACIONPLANOSBANCOS003", "72081"), // EGRESO INICIAL

    URL004("GENERACIONPLANOSBANCOS004", "72083"), // EGRESO FINAL

    URL005("GENERACIONPLANOSBANCOS005", "15063"), // TIPO

    URL006("GENERACIONPLANOSBANCOS006", "14001"), // TERCERO INICIAL

    URL007("GENERACIONPLANOSBANCOS007", "14176"), // TERCERO FINAL

    URL008("GENERACIONPLANOSBANCOS008", "29141"), // CUENTA INICIAL

    URL009("GENERACIONPLANOSBANCOS009", "29143"), // CUENTA FINAL
    
    URL010("GENERACIONPLANOSBANCOS009", "72130") // PAGOS BANCO DE OCCIDENTE

    ;

    private final String key;
    private final String value;

    private GeneracionplanosbancosControladorUrlEnum(String key, String value)
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
