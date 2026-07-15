package com.sysman.util.rest;

public class ParametrosEmpleadoresNominaElectronica {
	private String nit;
	private String ambiente;
	private String versionResolucion;
	private String modifiedBy;
	
	public String getNit() {
		return nit;
	}
	public void setNit(String nit) {
		this.nit = nit;
	}
	public String getAmbiente() {
		return ambiente;
	}
	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}
	public String getVersionResolucion() {
		return versionResolucion;
	}
	public void setVersionResolucion(String versionResolucion) {
		this.versionResolucion = versionResolucion;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}