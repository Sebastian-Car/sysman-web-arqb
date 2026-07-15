/*-
 * ImprimirSaludOcupacionalControladorUrlEnum.java
 *
 * 1.0
 * 
 * 8/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 * 
 * @version 1.0, 8/02/2018
 * @author crodriguez
 *
 */
public enum ImprimirSaludOcupacionalControladorUrlEnum {

    URL_162("IMPRIMIRSALUDOCUPACIONALCONTROLADOR162", "685055")

    ;

    private final String key;
    private final String value;

    private ImprimirSaludOcupacionalControladorUrlEnum(String key,
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
