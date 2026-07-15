/*-
 * IcontratacionControladorEnum.java
 *
 * 1.0
 * 
 * 8/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.planeacion.enums;

/**
 * 
 * @version 1.0, 8/09/2017
 * @author jcrodriguez
 *
 */
public enum IcontratacionControladorEnum {
    PR_FECHAS("PR_FECHAS"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    REPORTE000465("000465IContratacion"),

    REPORTE000466("000466IContratacionEspecial");

    private final String value;

    private IcontratacionControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
