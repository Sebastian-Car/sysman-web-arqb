package com.sysman.util.rest;

import java.util.List;

public class RespuestaCuerpoConsultarNomina {

	private List<RespuestaNominaReporte> datos;

	/**
	 * @return the nominas
	 */
	public List<RespuestaNominaReporte> getDatos() {
		return datos;
	}

	/**
	 * @param nominas the nominas to set
	 */
	public void setDatos(List<RespuestaNominaReporte> datos) {
		this.datos = datos;
	}
}


