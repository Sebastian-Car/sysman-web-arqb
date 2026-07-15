/*
 * FacturacionconceptosControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.facturaciongeneral.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos
 * Map<String,String> y disponibles en dicha enumeración.
 */
public enum FacturacionconceptosControladorEnum {

    OBSERVACIONES("OBSERVACIONES"),

    FECHA_SOLICITUD("FECHA_SOLICITUD"),

    PARAM25("PARAM25"),

    PARAM24("PARAM24"),

    PARAM9("PARAM9"),

    PARAM23("PARAM23"),

    PARAM22("PARAM22"),

    PARAM21("PARAM21"),

    PARAM6("PARAM6"),

    PARAM20("PARAM20"),

    PARAM5("PARAM5"),

    PARAM8("PARAM8"),

    PARAM7("PARAM7"),

    PARAM2("PARAM2"),

    PARAM1("PARAM1"),

    PARAM4("PARAM4"),

    PARAM3("PARAM3"),

    PARAM0("PARAM0"),

    PARAM19("PARAM19"),

    PARAM18("PARAM18"),

    PARAM17("PARAM17"),

    PARAM16("PARAM16"),

    PARAM15("PARAM15"),

    PARAM14("PARAM14"),

    PARAM13("PARAM13"),

    PARAM12("PARAM12"),

    PARAM11("PARAM11"),

    PARAM10("PARAM10"),

    APLICAFORMULA("APLICAFORMULA"),

    BASE_FIJA("BASE_FIJA"),

    VALOR_UNITARIO("VALOR_UNITARIO"),

    VALOR_UNIDAD("VALOR_UNIDAD"),

    VALOR_NETO("VALOR_NETO"),

    VALOR_BASE("VALOR_BASE"),

    VALOR_IMPOCONSUMO("VALOR_IMPOCONSUMO"),

    ANULADA("ANULADA"),

    DESCUENTO_FACTURA("DESCUENTO_FACTURA"),

    BASEIMPUESTOIVA("BASEIMPUESTOIVA"),

    PORCIVA("PORCIVA"),

    FECHA_VENCIMIENTO("FECHA_VENCIMIENTO"),

    NUMEROFACTURA("NUMEROFACTURA"),
    
    FACTURA_DOLARES("FACTURA_DOLARES"),
    
    VALOR_UNITARIO_DOLARES("VALOR_UNITARIO_DOLARES"),
    
    TRM_DOLARES_FAC("TRM_DOLARES_FAC"),
    
    VALOR_TOTAL_DOLARES("VALOR_TOTAL_DOLARES"),
    
    SALDO_ACTUAL_CONCEPTO("SALDO_ACTUAL"),
    
    TIPO_FACTURA("TIPO_FACTURA"),

    NUMERO_FACTURA("NUMERO_FACTURA"),
    
    CENTRO_UTILIDAD("CENTRO_UTILIDAD"),
    
    NOMBRE_CENTRO_UTILIDAD("NOMBRECENTROUTILIDAD"),
    
    VALOR ("VALOR"),
    
	VALOR_RETEIVA("VALOR_RETEIVA"),

	CONSECUTIVO("CONSECUTIVO"),

	TIPOCONTRATOSIGEC("TIPOCONTRATOSIGEC"),
	
	NROCONTRATOSIGEC("NROCONTRATOSIGEC"),
	
	PLATAFORMA("PLATAFORMA"), 
	    
    EQUIV_SIGEC("EQUIV_SIGEC"), 
    
    VALORTOTAL("VALORTOTAL"), 
    
    SIGEC("SIGEC"), 
    
    TERCERO("TERCERO"), 
    
    NOMBRE("NOMBRE"), 
    
    FECHAINICIO("FECHAINICIO"), 
    
    FECHAFINALIZACION("FECHAFINALIZACION"), 
    
    TIPO_SIGEC("TIPO_SIGEC"),
	
	FECHA_EXPEDICION("FECHA_EXPEDICION"),
	
	AJUSTE_DECIMAL("AJUSTE_DECIMAL");


    private final String value;

    private FacturacionconceptosControladorEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
