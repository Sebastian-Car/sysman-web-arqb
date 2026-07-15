/*
 * ConfiguracionFuentesFutsControladorUrlEnum
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
public enum ConfiguracionFuentesFutsControladorUrlEnum {

    URL7765("CONFIGURACIONFUENTESFUTSCONTROLADORURL7765",
                    "16145"),

    URL8074("CONFIGURACIONFUENTESFUTSCONTROLADORURL8074",
                    "4001"),

    URL8999("CONFIGURACIONFUENTESFUTSCONTROLADORURL8999",
                    "1699001"),

    URL8390("CONFIGURACIONFUENTESFUTSCONTROLADORURL8390",
                    "1699001"),

    URL7895("CONFIGURACIONFUENTESFUTSCONTROLADORURL7895",
                    "39072"),
    
    URL7865("CONFIGURACIONFUENTESFUTSCONTROLADORURL7865",
                    "39074")
    ;

    private final String key;
    private final String value;

    private ConfiguracionFuentesFutsControladorUrlEnum(String key,
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
