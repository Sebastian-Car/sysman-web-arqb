/*-
 * ActualizaparametrosretroactivosControladorEnum.java
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
 * Enumeracion que permite clasificar cada uno de los parametros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeracion.
 * 
 * @version 1.0, 15/01/2018
 * @author vmolano
 *
 */
public enum volantesDePagoControladorEnum {

    PROCESONOMINA("procesoNomina"),

    ID_TIPO("ID_DE_TIPO"),

    REPORTE000141("000141VolantesUno"),

    PROCESO("PROCESO"),

    EMPLEADOINI("empleadoIni"),

    EMPLEADOFIN("empleadoFin"),

    CENTROCOSTOINI("centroCostoIni"),

    CENTROCOSTOFIN("centroCostoFin"),

    TG_NO_EXISTE("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"),

    ID_DE_EMPLEADO("ID_DE_EMPLEADO");

    private final String value;

    private volantesDePagoControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
