/*
 * CotizacionesActividadesControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.hojasdevida.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum CotizacionesActividadesControladorEnum {

    SUCURSALESTABLECIMIENTO("SUCURSALESTABLECIMIENTO"),

    NITESTABLECIMIENTO("NITESTABLECIMIENTO"),

    DIRECCIONCONTACTO("DIRECCIONCONTACTO"),

    TELEFONOESTABLECIMIENTO("TELEFONOESTABLECIMIENTO"),

    DIRECCIONESTABLECIMIENTO("DIRECCIONESTABLECIMIENTO"),

    NOMBREESTABLECIMIENTO("NOMBREESTABLECIMIENTO"),

    TELEFONOCONTACTO("TELEFONOCONTACTO"),

    TIPOEVENTO("TIPOEVENTO"),

    IDEVENTO("IDEVENTO"),

    PR_FECHAINICIAL("PR_FECHAINICIAL"),

    PR_TIPOEVENTO("PR_TIPOEVENTO"),

    PR_IDEVENTO("PR_IDEVENTO");

    private final String value;

    private CotizacionesActividadesControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
