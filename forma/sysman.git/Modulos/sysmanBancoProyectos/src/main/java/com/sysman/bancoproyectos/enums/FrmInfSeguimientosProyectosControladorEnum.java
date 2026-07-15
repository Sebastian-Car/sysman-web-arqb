package com.sysman.bancoproyectos.enums;

public enum FrmInfSeguimientosProyectosControladorEnum {
	
	CODIGO("CODIGO"),
	PROYECTOFINAL("proyectoFinal"),
	PROYECTOINICIAL("proyectoInicial");
	
private final String value;
	
	private  FrmInfSeguimientosProyectosControladorEnum (String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

}
