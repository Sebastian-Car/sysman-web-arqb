package com.sysman.rest.logica;

public class ParametrosInformeFacturasPorCobrar {
	
    private String compania;
    private String fechaInicial;
    private String fechaFinal;
    private String nitEntidad;
    private String tipoFormato;
	
    public String getCompania() {
		return compania;
	}

	public void setCompania(String compania) {
		this.compania = compania;
	}

	public String getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(String fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public String getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(String fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public String getNitEntidad() {
		return nitEntidad;
	}

	public void setNitEntidad(String nitEntidad) {
		this.nitEntidad = nitEntidad;
	}
	
	public String getTipoFormato() {
		return tipoFormato;
	}

	public void setTipoFormato(String tipoFormato) {
		this.tipoFormato = tipoFormato;
	} 
	
	public ParametrosInformeFacturasPorCobrar(String compania, String fechaInicial, String fechaFinal, String nitEntidad, String tipoFormato) {
		super();
		this.setCompania(compania);
		this.setFechaInicial(fechaInicial);
		this.setFechaFinal(fechaFinal);
		this.setNitEntidad(nitEntidad);
		this.setTipoFormato(tipoFormato);
	}	  
}
