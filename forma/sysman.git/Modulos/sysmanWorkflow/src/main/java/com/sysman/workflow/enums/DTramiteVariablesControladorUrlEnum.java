/*-
 * DTramiteVariablesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 10/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.workflow.enums;

/**
 * Enumerado que contiene los codigos de los DSS utilizados en el
 * controlador
 * {@link com.sysman.workflow.DTramiteVariablesControlador}.
 * 
 * @version 1.0, 10/05/2018
 * @author pespitia
 *
 */
public enum DTramiteVariablesControladorUrlEnum {

    URL0002("FRMDNODOSCONTROLADORURL0002", "1048001"),

    URL0001("FRMDNODOSCONTROLADORURL0001", "1037004"),

    URL004("FRMDNODOSCONTROLADORURL004", "86004"),

    URL003("FRMDNODOSCONTROLADORURL003", "86003"),

    URL002("FRMDNODOSCONTROLADORURL002", "86002"),

    URL001("FRMDNODOSCONTROLADORURL001", "86001"),
    
    URL005("FRMDNODOSCONTROLADORURL001", "1042011"),
    
    URL006("FRMDNODOSCONTROLADORURL001", "1859001"),
    
    URL007("FRMDNODOSCONTROLADORURL", "1048011"),
    
    URL008("FRMDNODOSCONTROLADORURL", "1048012"),
    
    URL009("FRMDNODOSCONTROLADORURL", "1878001");

    private final String key;
    private final String value;

    private DTramiteVariablesControladorUrlEnum(String key, String value) {
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
