package com.sysman.transautomaticas.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum ComprobantePptalAfectarControladorUrlEnum {
    
    URL004("COMPROBANTEPPTALAFECTARCONTROLADORURL004", "1822002"),
    
    URL010("COMPROBANTECNTAFECTARCONTROLADORURL010", "75059"),

    URL009("COMPROBANTEPPTALAFECTARCONTROLADORURL009", "124004"),
    
    URL008("COMPROBANTEPPTALAFECTARCONTROLADORURL008", "1723004"),
    
    ;

    private final String key;
    private final String value;

    private ComprobantePptalAfectarControladorUrlEnum(String key, String value) {
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
