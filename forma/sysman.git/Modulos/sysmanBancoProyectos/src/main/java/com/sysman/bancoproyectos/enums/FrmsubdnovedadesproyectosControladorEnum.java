/*
 * FrmsubdnovedadesproyectosControladorEnum
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
public enum FrmsubdnovedadesproyectosControladorEnum {

    FUENTERECURSOS("FUENTERECURSOS"),
    
    FUENTERECURSOSRUBRO("FUENTERECURSOSRUBRO"),
    
    ID_META("ID_META"),
    
    METAPRODUCTO("METAPRODUCTO"),
    
    SALDO("SALDO"),
    
    SECTOR("SECTOR"),
    
    CANTIDAD_EJECUTADA("CANTIDAD_EJECUTADA"),
    
    CANTIDAD_PROGRAMADA("CANTIDAD_PROGRAMADA"),
    
    NOMBRECOMPONENTE ("NOMBRECOMPONENTE"),
    
    VALORPROGRAMADO ("VALORPROGRAMADO"),
    
    CANTIDAD_EJE ("CANTIDAD_EJE"),

    CANTIDAD_PLAN("CANTIDAD_PLAN"),

    INDICADOR("INDICADOR"),

    VALORAPROBADO("VALORAPROBADO"),

    PAIS("PAIS"),

    VALORSOLICITADO("VALORSOLICITADO"),

    VALORDISMINUIDO("VALORDISMINUIDO"),

    FUENTE("FUENTE"),

    COMPONENTE("COMPONENTE"),

    ACTIVIDADES("ACTIVIDADES"),

    TIPO("TIPO"),

    RUBROPRESUPUESTAL("RUBROPRESUPUESTAL"),
    
    AUXILIAR("AUXILIAR"),
    
    REFERENCIA("REFERENCIA"),
    
    CENTRO_COSTO("CENTRO_COSTO"),
    
    ID("ID"),
    
    SECTORRUBRO("001"),
    
    PROGRAMARUBRO("002"),
    
    SUBPROGRAMARUBRO("003"),
    
    CODIGOPRODUCTORUBRO("004"),
    
    CODIGOBPINRUBRO("005"),
    
    CODIGOCCPETRUBRO("006"),
    
    CODIGOCPCDANERUBRO("007"),
    
    CODIGOUNIDADEJECUTORARUBRO("008"),
    
    CODIGOFUENTERUBRO("009"),
    
    CODIGOCCPETREGALIAS("010"),
    
    ARBOL("3"),
    
    CONAUXILIAR("2"),
    
    CODIGO("CODIGO"),
    
    PROGRAMA("PROGRAMA"),
    
    SUBPROGRAMA("SUBPROGRAMA"),
    
    TPC_CPCDANE("007"),
    
    TPCCPETR("010"),
    
    TPCFUENTERUBRO("009"),
    
    TPCUNIDADEJECUTORA("008"),
    
    CUENTA("CUENTA"),
    
    CODIGODETALLESECTORIAL("012"),
    ;

    private final String value;

    private FrmsubdnovedadesproyectosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
