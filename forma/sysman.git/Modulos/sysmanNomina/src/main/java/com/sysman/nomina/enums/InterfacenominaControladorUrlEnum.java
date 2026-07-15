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
 * Enumeracion que permite clasificar cada uno de los identificadores generados en el refactoring y asociados al codigo legacy obtenido con patrones de busqueda.
 *
 * @version 1.0, 23/06/2018
 * @author mzanguna
 *
 */
public enum InterfacenominaControladorUrlEnum {

    URL4440("INTERFACENOMINA", "471073"),
    URL4470("INTER","7034"),
    URL4430("ENOMINACONTABILIDADCONTROLADORURLENUM4430","471069"),
    URL4420("ENOMINACONTABILIDADCONTROLADORURLENUM4420","537010");

    private final String key;
    private final String value;

    private InterfacenominaControladorUrlEnum(String key,
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
