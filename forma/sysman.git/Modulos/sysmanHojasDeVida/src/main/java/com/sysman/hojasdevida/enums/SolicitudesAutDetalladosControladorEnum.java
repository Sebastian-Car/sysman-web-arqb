/*-
 * SolicitudesAutDetalladosControladorEnum.java
 *
 * 1.0
 *
 * 21/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.hojasdevida.enums;

/**
 * Enumeración que permite clasificar cada uno de los parámetros identificados en el refactoring, para ser convertidos Map <String,String> y disponibles en dicha enumeración.
 *
 * @version 1.0, 21/02/2018
 * @author amonroy
 *
 */
public enum SolicitudesAutDetalladosControladorEnum {

    CLASE_SOLICITUD("CLASE_SOLICITUD"),

    ID_DE_CARGO("ID_DE_CARGO"),

    FECHA_APROBACION("FECHA_APROBACION"),

    CARGO_APROBACION("CARGO_APROBACION"),

    JEFE_DIRECTO("JEFE_DIRECTO"),

    NOMBRESOLICITUD("NOMBRESOLICITUD"),

    TIPO_PERMISO("TIPO_PERMISO"),

    NOMBRETIPOSOLICITUD("NOMBRETIPOSOLICITUD"),

    SENOTIFICA("SENOTIFICA"),

    DESTINO("DESTINO"),

    SUCURSAL_DESTINO("SUCURSAL_DESTINO"),

    SUCURSALJEFE("SUCURSALJEFE"),

    DOCUMENTOJEFE("DOCUMENTOJEFE"),

    NOMBRECOMPLETOJEFE("NOMBRECOMPLETOJEFE"),

    FECHA_SOLICITUD("FECHA_SOLICITUD"),

    FECHA_INICIO("FECHA_INICIO"),

    FECHA_FINAL("FECHA_FINAL"),

    HORA_INICIO("HORA_INICIO"),

    HORA_FINAL("HORA_FINAL"),

    NOMBRECOMPLETO("NOMBRECOMPLETO"),

    NOMBRE_DEL_CARGO("NOMBRE_DEL_CARGO"),

    VOLANTE_PAGO("11"),

    NOMBREDEPENDENCIA("NOMBREDEPENDENCIA"),

    EMAIL_CORPORATIVO("EMAIL_CORPORATIVO"),

    EMAIL_SOLICITANTE("EMAIL_SOLICITANTE");

    private final String value;

    private SolicitudesAutDetalladosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
