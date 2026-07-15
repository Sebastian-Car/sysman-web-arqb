/*
 * DevengosDescuentosControladorUrlEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.nomina.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum DevengosDescuentosControladorUrlEnum {

    URL7434("DEVENGOSDESCUENTOSCONTROLADORURL7434", "7001"),

    URL7086("DEVENGOSDESCUENTOSCONTROLADORURL7086", "537004"),

    URL5665("DEVENGOSDESCUENTOSCONTROLADORURL5665", "471008"),

    URL6665("DEVENGOSDESCUENTOSCONTROLADORURL6665", "471010"),

    URL6127("DEVENGOSDESCUENTOSCONTROLADORURL6127", "7027"),

    URL232("DEVENGOSDESCUENTOSCONTROLADORURL232", "471057"), 
    
    URL9898("DEVENGOSDESCUENTOSCONTROLADORURL9898","620017"),
    
    URL9999("DEVENGOSDESCUENTOSCONTROLADORURL9999","620018"),
    
    URL8282("DEVENGOSDESCUENTOSCONTROLADORURL8282","210135");

    private final String key;
    private final String value;

    private DevengosDescuentosControladorUrlEnum(String key, String value) {
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
