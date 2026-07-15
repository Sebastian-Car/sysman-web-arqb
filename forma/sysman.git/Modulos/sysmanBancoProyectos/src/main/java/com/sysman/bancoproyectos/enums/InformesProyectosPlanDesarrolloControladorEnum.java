/*-
 * InformesProyectosPlanDesarrolloControladorEnum.java
 *
 * 1.0
 * 
 * 25/09/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos.enums;

/**
 * 
 * @version 1.0, 25/09/2017
 * @author jcrodriguez
 *
 */
public enum InformesProyectosPlanDesarrolloControladorEnum {
    TG_NO_EXISTE("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"),

    REPORTE000180("000180RPTPROYECTOSSECTORES"),

    REPORTE000188("000188RPTPROYECTOSSECTORESUBICACION"),

    MSM_INFORME_NO_EXISTE("MSM_INFORME_NO_EXISTE"),

    REPORTE000189("000189RPTREGISTROPROYECTOSSECTORES"),

    REPORTE000193("000193RPTPROYECTOSREGISTRADOSVIGENCIA"),

    ID_SUBPROGRAMA("ID_SUBPROGRAMA"),

    ID_PROGRAMA("ID_PROGRAMA"),

    ID_SECTOR("ID_SECTOR"),

    ID_DIMENSION("ID_DIMENSION"),

    PR_VIGENCIA("PR_VIGENCIA"),

    DIMENSION("DIMENSION"),

    ANIOFILTRO("ANIOFILTRO"),

    CODIGO_INCIIAL("CODIGO_INCIIAL"),

    ID("ID"),

    TODOS("TODOS"),

    PR_STRSQL("PR_STRSQL");

    private final String value;

    private InformesProyectosPlanDesarrolloControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
