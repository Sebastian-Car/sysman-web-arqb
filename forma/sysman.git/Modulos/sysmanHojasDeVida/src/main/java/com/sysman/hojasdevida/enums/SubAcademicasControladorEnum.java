/*
 * TarifasfgControladorEnum
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
public enum SubAcademicasControladorEnum {

    NOMBRECIUDADACA("NOMBRECIUDADACA"),

    NOMBREDEPARTAMENTOACA("NOMBREDEPARTAMENTOACA"),

    NOMBREPAISACA("NOMBREPAISACA"),

    KEY_SUCURSAL("KEY_SUCURSAL"),

    KEY_NUMERO_DCTO("KEY_NUMERO_DCTO"),

    HORAS("HORAS"),

    NOMBREMES("NOMBREMES"),

    GRADUADO("GRADUADO"),

    NOMBRECIUDAD("NOMBRECIUDAD"),

    NOMBREDEPARTAMENTO("NOMBREDEPARTAMENTO"),

    NOMBREPAIS("NOMBREPAIS"),

    DEPTO("DEPTO"),

    DIAINICIO("DIAINICIO"),

    MESINICIO("MESINICIO"),

    ANOINICIO("ANOINICIO"),

    FECHATERMINACION("FECHATERMINACION"),

    FECHAINICIO("FECHAINICIO"),

    TB_TB3868("TB_TB3868"),

    TB_TB3869("TB_TB3869"),

    MSM_REGISTRO_INGRESADO("MSM_REGISTRO_INGRESADO"),

    NOE_CODIGOPERSONA("NOE_CODIGOPERSONA"),

    ESTABLECIMIENTO("ESTABLECIMIENTO"),

    NIVELAPROBADO("NIVELAPROBADO"),

    TITULOOBTENIDO("TITULOOBTENIDO"),

    MODALIDAD("MODALIDAD"),

    DIATERMINACION("DIATERMINACION"),

    MESTERMINACION("MESTERMINACION"),

    ANOTERMINACION("ANOTERMINACION"),

    DIA("DIA"),

    NOMBREMODALIDAD("NOMBREMODALIDAD"),

    NFA_CODIGOPERSONA("NFA_CODIGOPERSONA"),

    MSM_REGISTRO_ELIMINADO("MSM_REGISTRO_ELIMINADO"),

    GRADO("GRADO"),

    NEB_CODIGOPERSONA("NEB_CODIGOPERSONA"),

    PAIS("PAIS"),

    CODIGOPROF("CODIGOPROF"),

    NOMBRE_PROFESION("NOMBRE_PROFESION"),

    NIT("NIT"),

    NOMBREESTABLECIMIENTO("NOMBREESTABLECIMIENTO"),
    
    PAIST("PAISTERMINO"),
    
    DEPART("DEPTERMINO")
    ;

    private final String value;

    private SubAcademicasControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
