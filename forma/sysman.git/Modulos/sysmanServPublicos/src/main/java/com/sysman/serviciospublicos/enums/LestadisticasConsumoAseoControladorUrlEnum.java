/*
 * LestadisticasConsumoAseoControladorUrlEnum
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
public enum LestadisticasConsumoAseoControladorUrlEnum {

    URL11173("LESTADISTICASCONSUMOASEOCONTROLADORURL11173",
                    "227003"),

    URL10378("LESTADISTICASCONSUMOASEOCONTROLADORURL10378",
                    "227007"),

    URL9627("LESTADISTICASCONSUMOASEOCONTROLADORURL9627",
                    "227001"),

    URL8947("LESTADISTICASCONSUMOASEOCONTROLADORURL8947",
                    "214031"),

    URL12023("LESTADISTICASCONSUMOASEOCONTROLADORURL12023",
                    "227007"), 
    
    URL3604("LESTADISTICASCONSUMOASEOCONTROLADORURL3604",
                    "215031");

    private final String key;
    private final String value;

    private LestadisticasConsumoAseoControladorUrlEnum(String key,
        String value) {
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
