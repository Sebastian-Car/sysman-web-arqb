/*-
 * ImprimirElegiblesControladorEnum.java
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
 * Clase que permite consultar constantes en el controlador
 * ImprimirElegibles.
 * 
 * @version 1.0, 11/01/2018
 * @author dnino
 *
 */
public enum ImprimirElegiblesControladorEnum {

    NRO_CONVOCATORIA("NRO_CONVOCATORIA"),

    REPORTE("001676RptImprimirElegibles");

    private final String value;

    private ImprimirElegiblesControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

}
