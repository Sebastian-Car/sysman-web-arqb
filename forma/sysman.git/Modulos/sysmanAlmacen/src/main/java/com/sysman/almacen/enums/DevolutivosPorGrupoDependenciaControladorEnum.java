/*
 *  DevolutivosPorGrupoDependenciaControladorEnum
 *
 * 1.0
 *
 * 09/09/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.almacen.enums;

/**
 * @author Processors-api
 * 
 * @version 1.0
 * 
 * Enumeración que permite clasificar cada uno de los parámetros
 * identificados en el refactoring, para ser convertidos Map<String,String>
 * y disponibles en dicha enumeración.
 */ 
public enum DevolutivosPorGrupoDependenciaControladorEnum  {
	cmbelementodesde("cmbelementodesde"),
	cmbelementohasta("cmbelementohasta"),
	codigoinicial("cmbcodigoinicial"),
	codigofinal("cmbcodigofinal"),
    REPORTE001865("001865DevolutivosPorGrupoDependenciaAGr"),
	REPORTE001863("001863DevolutivosPorGrupoYDependencia"),
	REPORTE001866("001866DevolutivosporDependenciaGrupo"),
	REPORTE001869("001869DevolutivosPorDependenciaGrupoAGr"),
	DIGITOSAGRUPACIONINVENTARIO("DIGITOSAGRUPACIONINVENTARIO"),
	DIGITOSREPORTE("digitosagrupacioninventario");
        	
	private final String value;
	
	private   DevolutivosPorGrupoDependenciaControladorEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
