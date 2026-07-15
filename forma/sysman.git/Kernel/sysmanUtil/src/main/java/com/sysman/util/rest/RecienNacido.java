package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class RecienNacido {

	@JsonProperty("CODPRESTADOR")
	private String codPrestador;

	@JsonProperty("TIPODOCUMENTOIDENTIFICACION")
	private String tipoDocumentoIdentificacion;

	@JsonProperty("NUMDOCUMENTOIDENTIFICACION")
	private String numDocumentoIdentificacion;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@JsonProperty("FECHANACIMIENTO")
	private Date fechaNacimiento;

	@JsonProperty("EDADGESTACIONAL")
	private Integer edadGestacional;

	@JsonProperty("NUMCONSULTASCPRENATAL")
	private Integer numConsultasCPrenatal;

	@JsonProperty("CODSEXOBIOLOGICO")
	private String codSexoBiologico;

	@JsonProperty("PESO")
	private Integer peso;

	@JsonProperty("CODDIAGNOSTICOPRINCIPAL")
	private String codDiagnosticoPrincipal;

	@JsonProperty("CONDICIONDESTINOUSUARIOEGRESO")
	private String condicionDestinoUsuarioEgreso;

	@JsonProperty("CODDIAGNOSTICOCAUSAMUERTE")
	private String codDiagnosticoCausaMuerte;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@JsonProperty("FECHAEGRESO")
	private Date fechaEgreso;

	@JsonProperty("CONSECUTIVO")
	private Integer consecutivo;

	// Constructor, getters y setters
	public RecienNacido() {

	}

	/**
	 * @return the codPrestador
	 */
	public String getCodPrestador() {
		return codPrestador;
	}

	/**
	 * @param codPrestador the codPrestador to set
	 */
	public void setCodPrestador(String codPrestador) {
		this.codPrestador = codPrestador;
	}

	/**
	 * @return the tipoDocumentoIdentificacion
	 */
	public String getTipoDocumentoIdentificacion() {
		return tipoDocumentoIdentificacion;
	}

	/**
	 * @param tipoDocumentoIdentificacion the tipoDocumentoIdentificacion to set
	 */
	public void setTipoDocumentoIdentificacion(String tipoDocumentoIdentificacion) {
		this.tipoDocumentoIdentificacion = tipoDocumentoIdentificacion;
	}

	/**
	 * @return the numDocumentoIdentificacion
	 */
	public String getNumDocumentoIdentificacion() {
		return numDocumentoIdentificacion;
	}

	/**
	 * @param numDocumentoIdentificacion the numDocumentoIdentificacion to set
	 */
	public void setNumDocumentoIdentificacion(String numDocumentoIdentificacion) {
		this.numDocumentoIdentificacion = numDocumentoIdentificacion;
	}

	/**
	 * @return the fechaNacimiento
	 */
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	/**
	 * @param fechaNacimiento the fechaNacimiento to set
	 */
	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	/**
	 * @return the edadGestacional
	 */
	public Integer getEdadGestacional() {
		return edadGestacional;
	}

	/**
	 * @param edadGestacional the edadGestacional to set
	 */
	public void setEdadGestacional(Integer edadGestacional) {
		this.edadGestacional = edadGestacional;
	}

	/**
	 * @return the numConsultasCPrenatal
	 */
	public Integer getNumConsultasCPrenatal() {
		return numConsultasCPrenatal;
	}

	/**
	 * @param numConsultasCPrenatal the numConsultasCPrenatal to set
	 */
	public void setNumConsultasCPrenatal(Integer numConsultasCPrenatal) {
		this.numConsultasCPrenatal = numConsultasCPrenatal;
	}

	/**
	 * @return the codSexoBiologico
	 */
	public String getCodSexoBiologico() {
		return codSexoBiologico;
	}

	/**
	 * @param codSexoBiologico the codSexoBiologico to set
	 */
	public void setCodSexoBiologico(String codSexoBiologico) {
		this.codSexoBiologico = codSexoBiologico;
	}

	/**
	 * @return the peso
	 */
	public Integer getPeso() {
		return peso;
	}

	/**
	 * @param peso the peso to set
	 */
	public void setPeso(Integer peso) {
		this.peso = peso;
	}

	/**
	 * @return the codDiagnosticoPrincipal
	 */
	public String getCodDiagnosticoPrincipal() {
		return codDiagnosticoPrincipal;
	}

	/**
	 * @param codDiagnosticoPrincipal the codDiagnosticoPrincipal to set
	 */
	public void setCodDiagnosticoPrincipal(String codDiagnosticoPrincipal) {
		this.codDiagnosticoPrincipal = codDiagnosticoPrincipal;
	}

	/**
	 * @return the condicionDestinoUsuarioEgreso
	 */
	public String getCondicionDestinoUsuarioEgreso() {
		return condicionDestinoUsuarioEgreso;
	}

	/**
	 * @param condicionDestinoUsuarioEgreso the condicionDestinoUsuarioEgreso to set
	 */
	public void setCondicionDestinoUsuarioEgreso(String condicionDestinoUsuarioEgreso) {
		this.condicionDestinoUsuarioEgreso = condicionDestinoUsuarioEgreso;
	}

	/**
	 * @return the codDiagnosticoCausaMuerte
	 */
	public String getCodDiagnosticoCausaMuerte() {
		return codDiagnosticoCausaMuerte;
	}

	/**
	 * @param codDiagnosticoCausaMuerte the codDiagnosticoCausaMuerte to set
	 */
	public void setCodDiagnosticoCausaMuerte(String codDiagnosticoCausaMuerte) {
		this.codDiagnosticoCausaMuerte = codDiagnosticoCausaMuerte;
	}

	/**
	 * @return the fechaEgreso
	 */
	public Date getFechaEgreso() {
		return fechaEgreso;
	}

	/**
	 * @param fechaEgreso the fechaEgreso to set
	 */
	public void setFechaEgreso(Date fechaEgreso) {
		this.fechaEgreso = fechaEgreso;
	}

	/**
	 * @return the consecutivo
	 */
	public Integer getConsecutivo() {
		return consecutivo;
	}

	/**
	 * @param consecutivo the consecutivo to set
	 */
	public void setConsecutivo(Integer consecutivo) {
		this.consecutivo = consecutivo;
	}

}
