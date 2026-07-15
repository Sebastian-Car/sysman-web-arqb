/*-
 * ActualizaparametrosretroactivosControladorUrlEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina.enums;

/**
 * Enumeracion que permite clasificar cada uno de los identificadores
 * generados en el refactoring y asociados al codigo legacy obtenido
 * con patrones de busqueda.
 * 
 * @version 1.0, 18/08/2017
 * @author pespitia
 *
 */
public enum DiscoBancoBogotaControladorUrlEnum {

    URL0001("LUGARPARQUEOSCONTROLADORURL0001", "471002"), // ano

    URL0002("LUGARPARQUEOSCONTROLADORURL0002", "7024"), // mes

    URL0003("LUGARPARQUEOSCONTROLADORURL0003", "471025"), // periodo

    URL0004("LUGARPARQUEOSCONTROLADORURL0004", "459001"), // banco

    URL0005("LUGARPARQUEOSCONTROLADORURL0005", "614001"), // establecimiento

    URL0006("LUGARPARQUEOSCONTROLADORURL0006", "20001") // centro
                                                        // costo

    ;

    private final String key;
    private final String value;

    private DiscoBancoBogotaControladorUrlEnum(String key,
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
