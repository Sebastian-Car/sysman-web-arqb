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
public enum ActualizarSueldosControladorUrlEnum {

    URL0001("ACTUALIZARSUELDOSCONTROLADORURL0001", "4001"),

    URL0002("ACTUALIZARSUELDOSCONTROLADORURL0002", "4048");

    private final String key;
    private final String value;

    private ActualizarSueldosControladorUrlEnum(String key,
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
