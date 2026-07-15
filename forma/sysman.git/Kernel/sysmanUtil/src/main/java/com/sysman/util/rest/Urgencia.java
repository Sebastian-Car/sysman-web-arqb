package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class Urgencia {
    @JsonProperty("CODPRESTADOR")//R01
    private String codPrestador;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("FECHAINICIOATENCION")//R02
    private Date fechaInicioAtencion;

    @JsonProperty("CAUSAMOTIVOATENCION")//R03
    private String causaMotivoAtencion;

    @JsonProperty("CODDIAGNOSTICOPRINCIPAL")//R04
    private String codDiagnosticoPrincipal;

    @JsonProperty("CODDIAGNOSTICOPRINCIPALE")//R05
    private String codDiagnosticoPrincipalE; 

    @JsonProperty("CODDIAGNOSTICORELACIONADOE1")//R06
    private String codDiagnosticoRelacionadoE1;

    @JsonProperty("CODDIAGNOSTICORELACIONADOE2")//R07
    private String codDiagnosticoRelacionadoE2; 

    @JsonProperty("CODDIAGNOSTICORELACIONADOE3")//R08
    private String codDiagnosticoRelacionadoE3; 

    @JsonProperty("CONDICIONDESTINOUSUARIOEGRESO")//R09
    private String condicionDestinoUsuarioEgreso; 

    @JsonProperty("CODDIAGNOSTICOCAUSAMUERTE")//R10
    private String codDiagnosticoCausaMuerte; 

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("FECHAEGRESO")//R11
    private Date fechaEgreso; 

    @JsonProperty("CONSECUTIVO")
    private Integer consecutivo;//R12

	public Urgencia() {

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
