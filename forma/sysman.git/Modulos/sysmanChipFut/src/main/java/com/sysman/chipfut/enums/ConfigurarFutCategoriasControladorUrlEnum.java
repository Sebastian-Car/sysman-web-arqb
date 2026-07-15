/*
 * AcummensualControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.chipfut.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ConfigurarFutCategoriasControladorUrlEnum {

    URL181("CONFIGURARFUTCATEGORIASCONTROLADORURL181", "4001"),

    URL208("CONFIGURARFUTCATEGORIASCONTROLADOURL208", "1686001"),
    
    URL209("CONFIGURARFUTCATEGORIASCONTROLADOURL208", "1686001"),

    URL226("CONFIGURARFUTCATEGORIASCONTROLADOURL226", "34001"),
    
    URL5478("CONFIGURARFUTCATEGORIASCONTROLADOURL5478", "1687001"),
    
    URL15084("CONFIGURARFUTCATEGORIASCONTROLADOURL15084","1031022"),
    
    URL9050("CONFIGURARFUTCATEGORIASCONTROLADOURL9050","1031024"),
    
    URL5715("CONFIGURARFUTCATEGORIASCONTROLADOURL5715","1699001")
    ;

    private final String key;
    private final String value;

    private ConfigurarFutCategoriasControladorUrlEnum(String key,
        String value)
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
