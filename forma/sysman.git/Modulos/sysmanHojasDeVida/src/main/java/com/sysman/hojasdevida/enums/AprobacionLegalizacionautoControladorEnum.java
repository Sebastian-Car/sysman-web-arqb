/*-
 * ActividadesinscritosControladorEnum.java
 *
 * 1.0
 *
 * 3/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Clase reservada para almacenar las constantes del archivo ActividadesinscritosControlador.
 *
 * @version 1.0, 3/02/2018
 * @author mzanguna
 *
 */
public enum AprobacionLegalizacionautoControladorEnum {

    CODSOLICITUD("CODSOLICITUD"),
    TIPOVIATICO("TIPO_VIATICO"),
    NOMBRETERCERO("NOMBRETERCERO"),
    FECHA("FECHA"),
    OBJETO("OBJETO"),
    TERCERO("TERCERO"),
    ESTADOLEGALIZACION1("ESTADO_LEGALIZACION1")
    ;

    private final String value;

    private AprobacionLegalizacionautoControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
