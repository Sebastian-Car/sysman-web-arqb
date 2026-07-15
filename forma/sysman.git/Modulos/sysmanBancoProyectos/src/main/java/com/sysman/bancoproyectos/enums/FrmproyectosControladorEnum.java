/*
 * FrmproyectosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.bancoproyectos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum FrmproyectosControladorEnum {

    FECHAREGISTRO("FECHAREGISTRO"),

    VIGENCIAMONITOR("vigenciaMonitor"),

    RIDPROYECTO("ridProyecto"),

    PROYECTOMONITOR("proyectoMonitor"),

    IDDEPENDENCIAMONITOR("idDependenciaMonitor"),

    ESTADOMONITOR("estadoMonitor"),

    DEPENDENCIAMONITOR("dependenciaMonitor"),

    CODIGOPROY("codigoProyecto"),

    ANOINI("anoIni"),

    ANOFIN("anoFin"),

    ACCION("accion"),

    VIGENCIAINICIO("VIGENCIAINICIO"),

    VIGENCIAFIN("VIGENCIAFIN"),

    VALORPROGRAMADO("VALORPROGRAMADO"),

    VALOROTRAENTIDAD("VALOROTRAENTIDAD"),

    VALORNACION("VALORNACION"),

    VALORDPTO("VALORDPTO"),

    VALORANNO4("VALORANNO4"),

    VALORANNO3("VALORANNO3"),

    VALORANNO2("VALORANNO2"),

    VALORANNO1("VALORANNO1"),

    TB_TB2330("TB_TB2330"),

    TB_TB2283("TB_TB2283"),

    SUCURSAL_RADIC("SUCURSAL_RADIC"),

    OBJETIVO("OBJETIVO"),

    NOMBREPROYECTO("NOMBREPROYECTO"),

    NOMBRENOVEDAD("NOMBRENOVEDAD"),

    DEPENDENCIA_RESPONS_RADIC("DEPENDENCIA_RESPONS_RADIC"),

    CONPROGRAMACION("CONPROGRAMACION"),

    CODIGONOVEDAD("CODIGONOVEDAD"),

    CARGO_RESPONS_RADIC("CARGO_RESPONS_RADIC"),

    PALABRA("PALABRA"),

    RETORNO("retorno"),

    CEDULA("CEDULA"),

    CODIGOBPIM("CODIGOBPIM"),

    ID("ID"),

    IREGISTRADO("IREGISTRADO"),

    RESPONSABLEOTRAENTIDAD("RESPONSABLEOTRAENTIDAD"),

    RESPONSABLEDPTO("RESPONSABLEDPTO"),

    NIT("NIT"),

    RESPONSABLENACION("RESPONSABLENACION"),

    RESPONSABLE_RADIC("RESPONSABLE_RADIC"),

    INTERVENTOR("INTERVENTOR"),

    TIPO("TIPO"),

    CODIGOSECTORDNP("CODIGOSECTORDNP"),

    CODIGOPROYECTO("CODIGOPROYECTO"),

    ANOFINAL("ANOFINAL"),

    ANOINICIAL("ANOINICIAL");

    private final String value;

    private FrmproyectosControladorEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
