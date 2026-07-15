/*
* AimregistroejecucgastoscxpsControladorEnum
*
* 1.0
*
* 09/09/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.presupuesto.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum AimregistroejecucgastoscxpsControladorEnum {
   
    IDIOMA1("TB_TB219"),
    IDIOMA2("TB_TB1030"),
    IDIOMA3("TB_TB1031"),
    IDIOMA4("TT_FR981"),
    ANIO("ANIO"),
    ID("ID"),  
    SYSDATE("SYSDATE"),   
    CUENTAFINAL("cuentaFinal"),
    APROPIACIONVIGENTE("apropiacionVigente"),
    OBLIGACION("obligacion"),
    PAGOSACUM("pagosAcum"),
    COMPROMISO("compromiso"),
    CUENTAINICIALL("cuentaInicial"),
    MES("mes"),
    NIVEL("nivel"),
    PAGOSMES("pagosMes"),
    MILES("miles"),
    OBLIGACIONMES("obligacionMes"),
    CONTRALORIA_DEPARTAMENTAL("CONTRALORIA DEPARTAMENTAL"),
    FIRMA1("FIRMA1 EN RESOLUCION 036 ESPECIAL"),
    FIRMA2("FIRMA2 EN RESOLUCION 036 ESPECIAL"),
    FIRMA3("FIRMA3 EN RESOLUCION 036 ESPECIAL"),
    CARGO1("CARGO1 EN RESOLUCION 036 ESPECIAL"),
    CARGO2("CARGO2 EN RESOLUCION 036 ESPECIAL"),
    CARGO3("CARGO3 EN RESOLUCION 036 ESPECIAL"),
    PR_FORMATO("PR_FORMATO"),
    PR_CONTRALORIADEPARTAMENTAL("PR_CONTRALORIADEPARTAMENTAL"),
    PR_NOMBRECOMPANIA("PR_NOMBRECOMPANIA"),
    PR_NOMBREMES("PR_NOMBREMES"),
    PR_FIRMARESOLUCION1("PR_FIRMARESOLUCION1"),
    PR_FIRMARESOLUCION2("PR_FIRMARESOLUCION2"),
    PR_FIRMARESOLUCION3("PR_FIRMARESOLUCION3"),
    PR_CARGORESOLUCION1("PR_CARGORESOLUCION1"),
    PR_CARGORESOLUCION2("PR_CARGORESOLUCION2"),
    PR_CARGORESOLUCION3("PR_CARGORESOLUCION3"),
    NOMBREREOPRTE1("000986REGISTROEJECUCGASTOSRP036AIM"),
    NOMBREREPORTE2("000983REGISTROEJECUCGASTOSCXP036AIM"),
    MSM_TRANS_INTERRUMPIDA("MSM_TRANS_INTERRUMPIDA"), 
    CUENTAINICIAL("CUENTAINICIAL");
        	
	private final String value;
	
	private  AimregistroejecucgastoscxpsControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
