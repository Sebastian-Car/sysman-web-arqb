/*-
 * VehiculoaccesoriosControladorEnum.java
 *
 * 1.0
 * 
 * 18/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.mantenimientoactivos.enums;

/**
 * 
 * @version 1.0, 18/08/2017
 * @author jcrodriguez
 *
 */
public enum VehiculoaccesoriosControladorEnum {
    FSERVICIO("FSERVICIO"),

    FREINTEGRO("FREINTEGRO"),

    FRESPONSABILIDAD("FRESPONSABILIDAD"),

    VEHICULOACCESORIOS("VEHICULOACCESORIOS"),

    CODIGOELEMENTO("CODIGOELEMENTO"),

    NOMBRELEMENTO("NOMBRELEMENTO"),

    SERIE_ELEMENTO("SERIE_ELEMENTO"),

    HERRAMIENTAS("HERRAMIENTAS"),

    OPCION("OPCION");

    private final String value;

    private VehiculoaccesoriosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
