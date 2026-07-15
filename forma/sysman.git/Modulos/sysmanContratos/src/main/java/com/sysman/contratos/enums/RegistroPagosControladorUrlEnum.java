/*-
 * AccionesCContratoControladorUrlEnum.java
 *
 * 1.0
 * 
 * 2/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.enums;

/**
 * 
 * @version 1.0, 2/08/2017
 * @author pespitia
 *
 * Enumeracion que permite clasificar cada uno de los identificadores
 * geenerados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 */
public enum RegistroPagosControladorUrlEnum {

    URL0001("REGISTROPAGOSCONTROLADORURL0001", "186004"),

    URL0002("REGISTROPAGOSCONTROLADORURL0002", "186007"),
    
    URL0003("REGISTROPAGOSCONTROLADORURL0003", "186005"),
    
    URL0004("REGISTROPAGOSCONTROLADORURL0004", "186006"),
    
    URL25190("REGISTROPAGOSCONTROLADORURL25190", "7001");

    private final String key;
    private final String value;

    private RegistroPagosControladorUrlEnum(String key, String value) {
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
