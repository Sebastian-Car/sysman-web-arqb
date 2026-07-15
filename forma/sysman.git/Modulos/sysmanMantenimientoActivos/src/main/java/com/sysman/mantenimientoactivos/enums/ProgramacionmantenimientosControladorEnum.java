/*
 * ProgramacionmantenimientosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.mantenimientoactivos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * EnumeraciÃ³n que permite clasificar cada uno de los parÃ¡metros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeraciÃ³n.
 */
public enum ProgramacionmantenimientosControladorEnum {

    AUTORIZACION("Autorización"),

    SOLICITUD("Solicitud"),

    TIPO("TIPO"),

    PANIO("panio"),

    TIPONOMBRE("tipoNombre"),

    SOLICITUDUPPER("SOLICITUD"),

    APROBADO("APROBADO"),

    NUMERO("numero"),

    VALORUNITARIO("VALORUNITARIO"),

    PR_STRSQL("PR_STRSQL"),

    PMES("pmes"),

    TIPOLOWER("tipo"),

    EJE("EJE"),

    RECHAZADO("RECHAZADO"),

    SOL("SOL"),

    AUT("AUT"),

    REPORTE250("000250IListaSolicitudes"),

    REPORTE252("000252IListaejecucion"),

    ANOLOWER("ano"),

    REPORTE251("000251IListaAutorizacion"),

    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),

    PR_PIEPA_FORMAT_CALIDAD("PR_PIE_DE_PAGINA_FORMATO_CALIDAD_TRASLADOS_TUNJA"),

    PIE_PAG_FORMA_CALIDAD("PIE DE PAGINA FORMATO CALIDAD TRASLADOS TUNJA"),

    PR_FECHA_FORMA_CALIDAD(
                    "PR_FECHA_FORMATO_CALIDAD_MANTENIMIENTO_DE_BIENES_TUNJA"),

    FECHA_FORMA_CALIDAD("FECHA FORMATO CALIDAD MANTENIMIENTO DE BIENES TUNJA"),

    PR_VERS_FORMA_CALIDAD(
                    "PR_VERSION_FORMATO_CALIDAD_MANTENIMIENTO_DE_BIENES_TUNJA"),

    VERS_FORMATO_CALIDAD(
                    "VERSION FORMATO CALIDAD MANTENIMIENTO DE BIENES TUNJA"),

    PR_COD_FORMA_CALIDAD(
                    "PR_CODIGO_FORMATO_CALIDAD_MANTENIMIENTO_DE_BIENES_TUNJA"),

    COD_FORMA_CALIDAD("CODIGO FORMATO CALIDAD MANTENIMIENTO DE BIENES TUNJA"),

    NUM_MANTENIMIENTO("numMantenimiento"),

    TIPO2("tipo2"),

    TOTALMANTENIMIENTO("totalMantenimiento"),

    CAMPOVISIBLE("camposVisible"),

    CAMPOVISIBLE_RES("camposVisibleRes"),

    BLOQUEADETALLE("bloqueaDetalle"),

    APROBADOLOWER("aprobado"),

    RID("rid"),

    KEY_COMPANIA("KEY_COMPANIA"),

    KEY_ANO("KEY_ANO"),

    KEY_NUMERO("KEY_NUMERO"),

    KEY_TIPO("KEY_TIPO"),

    FECHAINICIAL("FECHAINICIAL"),

    FECHAFINAL("FECHAFINAL"),

    NIT_TALLER("NIT_TALLER"),

    NUMERO_SOLICITUD("Número Solicitud:"),

    NUMERO_AUTORIZACION("Nro. Autorización:"),

    MANTENIMIENTO("MANTENIMIENTO")

    ;
    private final String value;

    private ProgramacionmantenimientosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
