package com.sysman.rest.logica;

public class ParametrosInformeFactura {
	
    private String compania;
    private String tipo;
    private String anio;
    private String factura;
    private String nitEntidad;
	public String getCompania() {
		return compania;
	}
	public void setCompania(String compania) {
		this.compania = compania;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public String getNitEntidad() {
		return nitEntidad;
	}
	public void setNitEntidad(String nitEntidad) {
		this.nitEntidad = nitEntidad;
	}
	public ParametrosInformeFactura(String compania, String tipo, String anio, String factura, String nitEntidad) {
		super();
		this.compania = compania;
		this.tipo = tipo;
		this.anio = anio;
		this.factura = factura;
		this.nitEntidad = nitEntidad;
	}
   
}
