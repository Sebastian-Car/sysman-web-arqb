package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;



public class Hospitalizacion {
	@JsonProperty("CODPRESTADOR") // H01
	private String codPrestador;

	@JsonProperty("VIAINGRESOSERVICIOSALUD")	
	private String viaIngresoServicioSalud;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@JsonProperty("FECHAINICIOATENCION") // H03
	private Date fechaInicioAtencion;

	@JsonProperty("NUMAUTORIZACION") // H04
	private String numAutorizacion;

	@JsonProperty("CAUSAMOTIVOATENCION") // H05
	private String causaMotivoAtencion;

	@JsonProperty("CODDIAGNOSTICOPRINCIPAL") // H06
	private String codDiagnosticoPrincipal;

	@JsonProperty("CODDIAGNOSTICOPRINCIPALE") // H07
	private String codDiagnosticoPrincipalE;

	@JsonProperty("CODDIAGNOSTICORELACIONADOE1") // H8
	private String codDiagnosticoRelacionadoE1;

	@JsonProperty("CODDIAGNOSTICORELACIONADOE2") // H09
	private String codDiagnosticoRelacionadoE2;

	@JsonProperty("CODDIAGNOSTICORELACIONADOE3") // H10
	private String codDiagnosticoRelacionadoE3;

	@JsonProperty("CODCOMPLICACION") // H11
	private String codComplicacion;

	@JsonProperty("CONDICIONDESTINOUSUARIOEGRESO") // H12
	private String condicionDestinoUsuarioEgreso;

	@JsonProperty("CODDIAGNOSTICOCAUSAMUERTE") // H13
	private String codDiagnosticoCausaMuerte;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@JsonProperty("FECHAEGRESO") // H14
	private Date fechaEgreso;

	@JsonProperty("CONSECUTIVO") // H15
	private Integer consecutivo;

	// Constructor, getters y setters
	public Hospitalizacion() {

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
	 * @return the viaIngresoServicioSalud
	 */
	public String getViaIngresoServicioSalud() {
		return viaIngresoServicioSalud;
	}

	/**
	 * @param viaIngresoServicioSalud the viaIngresoServicioSalud to set
	 */
	public void setViaIngresoServicioSalud(String viaIngresoServicioSalud) {
		this.viaIngresoServicioSalud = viaIngresoServicioSalud;
	}

	/**
	 * @return the fechaInicioAtencion
	 */
	public Date getFechaInicioAtencion() {
		return fechaInicioAtencion;
	}

	/**
	 * @param fechaInicioAtencion the fechaInicioAtencion to set
	 */
	public void setFechaInicioAtencion(Date fechaInicioAtencion) {
		this.fechaInicioAtencion = fechaInicioAtencion;
	}

	/**
	 * @return the numAutorizacion
	 */
	public String getNumAutorizacion() {
		return numAutorizacion;
	}

	/**
	 * @param numAutorizacion the numAutorizacion to set
	 */
	public void setNumAutorizacion(String numAutorizacion) {
		this.numAutorizacion = numAutorizacion;
	}

	/**
	 * @return the causaMotivoAtencion
	 */
	public String getCausaMotivoAtencion() {
		return causaMotivoAtencion;
	}

	/**
	 * @param causaMotivoAtencion the causaMotivoAtencion to set
	 */
	public void setCausaMotivoAtencion(String causaMotivoAtencion) {
		this.causaMotivoAtencion = causaMotivoAtencion;
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
	 * @return the codDiagnosticoPrincipalE
	 */
	public String getCodDiagnosticoPrincipalE() {
		return codDiagnosticoPrincipalE;
	}

	/**
	 * @param codDiagnosticoPrincipalE the codDiagnosticoPrincipalE to set
	 */
	public void setCodDiagnosticoPrincipalE(String codDiagnosticoPrincipalE) {
		this.codDiagnosticoPrincipalE = codDiagnosticoPrincipalE;
	}

	/**
	 * @return the codDiagnosticoRelacionadoE1
	 */
	public String getCodDiagnosticoRelacionadoE1() {
		return codDiagnosticoRelacionadoE1;
	}

	/**
	 * @param codDiagnosticoRelacionadoE1 the codDiagnosticoRelacionadoE1 to set
	 */
	public void setCodDiagnosticoRelacionadoE1(String codDiagnosticoRelacionadoE1) {
		this.codDiagnosticoRelacionadoE1 = codDiagnosticoRelacionadoE1;
	}

	/**
	 * @return the codDiagnosticoRelacionadoE2
	 */
	public String getCodDiagnosticoRelacionadoE2() {
		return codDiagnosticoRelacionadoE2;
	}

	/**
	 * @param codDiagnosticoRelacionadoE2 the codDiagnosticoRelacionadoE2 to set
	 */
	public void setCodDiagnosticoRelacionadoE2(String codDiagnosticoRelacionadoE2) {
		this.codDiagnosticoRelacionadoE2 = codDiagnosticoRelacionadoE2;
	}

	/**
	 * @return the codDiagnosticoRelacionadoE3
	 */
	public String getCodDiagnosticoRelacionadoE3() {
		return codDiagnosticoRelacionadoE3;
	}

	/**
	 * @param codDiagnosticoRelacionadoE3 the codDiagnosticoRelacionadoE3 to set
	 */
	public void setCodDiagnosticoRelacionadoE3(String codDiagnosticoRelacionadoE3) {
		this.codDiagnosticoRelacionadoE3 = codDiagnosticoRelacionadoE3;
	}

	/**
	 * @return the codComplicacion
	 */
	public String getCodComplicacion() {
		return codComplicacion;
	}

	/**
	 * @param codComplicacion the codComplicacion to set
	 */
	public void setCodComplicacion(String codComplicacion) {
		this.codComplicacion = codComplicacion;
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
