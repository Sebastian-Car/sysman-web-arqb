package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

public class NomElecGeneral {

	private NomElecEncabezado encabezado;
	private NomElecDevengados devengados;
	private NomElecDeducciones deducciones;
	private NomElecTotales totalesGenerales;

	public NomElecGeneral() {
	}

	public NomElecDevengados getDevengados() {
		return devengados;
	}

	public void setDevengados(NomElecDevengados devengados) {
		this.devengados = devengados;
	}

	public NomElecDeducciones getDeducciones() {
		return deducciones;
	}

	public void setDeducciones(NomElecDeducciones deducciones) {
		this.deducciones = deducciones;
	}

	public NomElecTotales getTotalesGenerales() {
		return totalesGenerales;
	}

	public void setTotalesGenerales(NomElecTotales totalesGenerales) {
		this.totalesGenerales = totalesGenerales;
	}

	public NomElecEncabezado getEncabezado() {
		return encabezado;
	}

	public void setEncabezado(NomElecEncabezado encabezado) {
		this.encabezado = encabezado;
	}

}
