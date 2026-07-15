package com.sysman.auditoria;

public class ReporteAuditoria {
	
	private String codEntidad;
	private String codProceso;
	private String referencia;
	private String fechaInicial;
	private String fechaFinal;
	private String tipoReporte;
	
	public ReporteAuditoria(String codEntidad, String codProceso, String referencia, String fechaInicial,
			String fechaFinal, String tipoReporte) {
		super();
		this.codEntidad = codEntidad;
		this.codProceso = codProceso;
		this.referencia = "";
		this.fechaInicial = fechaInicial;
		this.fechaFinal = fechaFinal;
		this.tipoReporte = tipoReporte;
	}
	/**
	 * @return the codEntidad
	 */
	public String getCodEntidad() {
		return codEntidad;
	}
	/**
	 * @param codEntidad the codEntidad to set
	 */
	public void setCodEntidad(String codEntidad) {
		this.codEntidad = codEntidad;
	}
	/**
	 * @return the codProceso
	 */
	public String getCodProceso() {
		return codProceso;
	}
	/**
	 * @param codProceso the codProceso to set
	 */
	public void setCodProceso(String codProceso) {
		this.codProceso = codProceso;
	}
	/**
	 * @return the referencia
	 */
	public String getReferencia() {
		return referencia;
	}
	/**
	 * @param referencia the referencia to set
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	/**
	 * @return the fechaInicial
	 */
	public String getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(String fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * @return the fechaFinal
	 */
	public String getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(String fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * @return the tipoReporte
	 */
	public String getTipoReporte() {
		return tipoReporte;
	}
	/**
	 * @param tipoReporte the tipoReporte to set
	 */
	public void setTipoReporte(String tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

}
