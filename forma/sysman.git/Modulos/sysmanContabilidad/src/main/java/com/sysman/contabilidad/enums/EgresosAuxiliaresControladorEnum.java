package com.sysman.contabilidad.enums;

public enum EgresosAuxiliaresControladorEnum {

	CENTRO_COSTO("CENTRO_COSTO"),
	
	ANIO("ANIO"),
	
	CODIGOFINAL("CODIGOFINAL"),
	
	REFERENCIAINICIAL("REFERENCIAINICIAL"),
	
	FUENTEINICIAL("FUENTEINICIAL");
	
	 private final String value;

	 private EgresosAuxiliaresControladorEnum(String value) {
		 this.value = value;
	 }

	 public String getValue() {
		 return value;
	 }
}
