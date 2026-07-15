/*-
 * ImprimirElegiblesControladorUrlEnum.java
 *
 * 1.0
 * 
 * 11/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumerado que almacena las URL del controlador ImprimirElegibles.
 * 
 * @version 1.0, 11/01/2018
 * @author dnino
 *
 */
public enum ImprimirElegiblesControladorUrlEnum {

    URL320("IMPRIMIRELEGIBLESCONTROLADORURL320", "708025");

    private final String key;                                                                    
    private final String value;

    private ImprimirElegiblesControladorUrlEnum(String key, String value) {
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
