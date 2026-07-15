/*
 * InformeSiaControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.contratos.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map
 * <String,String> y disponibles en dicha enumeración.
 */
public enum InformeSiaControladorEnum {

    PARAM0("FILTRA POR FECHA DE DILIGENCIAMIENTO"),

    PARAM1("MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO"),

    PARAM2(""),

    PARAM3("REGIMEN"),

    PARAM4("PRESUPUESTO"),

    PARAM5("ORIGENPPTO"),

    PARAM6("NUMEROCONTRATO"),

    PARAM7("MODALIDADSELECCION"),

    PARAM8("PROCEDIMIENTO"),

    PARAM9("SIAF20"),

    PARAM10("TIPOGASTO"),

    PARAM11("SECTOR"),

    PARAM12("OBJETOCONTRATO"),

    PARAM13("VALORINICIAL"),

    PARAM14("NITTERCERO"),

    PARAM15("NOMBRECONTRATISTA"),

    PARAM16("TIPOPERSONA"),

    PARAM17("FECHASUSCRIP"),

    PARAM18("CEDULAINTERVENTOR"),

    PARAM19("INTERVENTOR"),

    PARAM20("VINCULAINTERVENTOR"),

    PARAM21("NUMEROUND"),

    PARAM22("PLAZODEENTREGA"),

    PARAM23("ANTICIPOPACTADO"),

    PARAM24("ANTICIPOS"),

    PARAM25("ACTAINICIO"),

    PARAM26("FECHAFINALIZACION"),

    PARAM27("FECHAPUBLICACION"),

    PARAM28("NUMEROACTO"),

    PARAM29("FECHAACTO"),

    PARAM30("PROPIOS"),

    PARAM31("REGALIAS"),

    PARAM32("SGP"),

    PARAM33("VLRCOLHUMANITARIA"),

    PARAM34("FECHA_AUTO_VF"),

    PARAM35("VFANOINICIAL"),

    PARAM36("VFANOFINAL"),

    PARAM37("VFAUTORIZADO"),

    PARAM38("VFAPRINICIAL"),

    PARAM39("VFPERIODO"),

    PARAM40("VFSALDO"),

    PARAM41("NIT"),

    PARAM42("NITCONSOSORCIADO"),

    PARAM43("NOMBRECONSORCIADO"),

    PARAM44("PRORROGA"),

    PARAM45("VLRADICION"),

    PARAM46("FECHALIQUIDA"),

    PARAM47("SECOP"),

    PARAM48("_F20_A1_AGR.csv"),

    PARAM49("800040F20A1"),

    PARAM50("800041F20A2"),

    PARAM51("_F20_B_AGR.csv"),

    PARAM52("_F20_1C_AGR.csv"),

    PARAM53("800043F20C1"),

    PARAM54("800040F20A1Condicion"),

    PARAM55("800040F20A1Condicion2"),

    PARAM56("800042F20B"),

    PARAM57("vigencia"),

    PARAM58(""),

    PARAM59(""),

    ;

    private final String value;

    private InformeSiaControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
