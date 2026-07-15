package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class Usuario {
	@JsonProperty("TIPODOCUMENTOIDENTIFICACION") // U01
	private String tipoDocumentoIdentificacion;

	@JsonProperty("NUMDOCUMENTOIDENTIFICACION") // U02
	private String numDocumentoIdentificacion;

	@JsonProperty("TIPOUSUARIO") // U03
	private String tipoUsuario;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("FECHANACIMIENTO") // U04
	private Date fechaNacimiento;

	@JsonProperty("CODSEXO") // U05
	private String codSexo;

	@JsonProperty("CODPAISRESIDENCIA") // U06
	private String codPaisResidencia;

	@JsonProperty("CODMUNICIPIORESIDENCIA") // U07
	private String codMunicipioResidencia;

	@JsonProperty("CODZONATERRITORIALRESIDENCIA") // U08
	private String codZonaTerritorialResidencia;

	@JsonProperty("INCAPACIDAD") // U09
	private String incapacidad;

	@JsonProperty("CONSECUTIVO") // U10
	private Integer consecutivo;

	@JsonProperty("CODPAISORIGEN") // U11
	private String codPaisOrigen;

	@JsonProperty("SERVICIOS")
	private Servicios servicios; // Objeto anidado de tipo Servicios

	public Usuario() {
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
	 * @return the tipoUsuario
	 */
	public String getTipoUsuario() {
		return tipoUsuario;
	}

	/**
	 * @param tipoUsuario the tipoUsuario to set
	 */
	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
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
	 * @return the codSexo
	 */
	public String getCodSexo() {
		return codSexo;
	}

	/**
	 * @param codSexo the codSexo to set
	 */
	public void setCodSexo(String codSexo) {
		this.codSexo = codSexo;
	}

	/**
	 * @return the codPaisResidencia
	 */
	public String getCodPaisResidencia() {
		return codPaisResidencia;
	}

	/**
	 * @param codPaisResidencia the codPaisResidencia to set
	 */
	public void setCodPaisResidencia(String codPaisResidencia) {
		this.codPaisResidencia = codPaisResidencia;
	}

	/**
	 * @return the codMunicipioResidencia
	 */
	public String getCodMunicipioResidencia() {
		return codMunicipioResidencia;
	}

	/**
	 * @param codMunicipioResidencia the codMunicipioResidencia to set
	 */
	public void setCodMunicipioResidencia(String codMunicipioResidencia) {
		this.codMunicipioResidencia = codMunicipioResidencia;
	}

	/**
	 * @return the codZonaTerritorialResidencia
	 */
	public String getCodZonaTerritorialResidencia() {
		return codZonaTerritorialResidencia;
	}

	/**
	 * @param codZonaTerritorialResidencia the codZonaTerritorialResidencia to set
	 */
	public void setCodZonaTerritorialResidencia(String codZonaTerritorialResidencia) {
		this.codZonaTerritorialResidencia = codZonaTerritorialResidencia;
	}

	/**
	 * @return the incapacidad
	 */
	public String getIncapacidad() {
		return incapacidad;
	}

	/**
	 * @param incapacidad the incapacidad to set
	 */
	public void setIncapacidad(String incapacidad) {
		this.incapacidad = incapacidad;
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

	/**
	 * @return the codPaisOrigen
	 */
	public String getCodPaisOrigen() {
		return codPaisOrigen;
	}

	/**
	 * @param codPaisOrigen the codPaisOrigen to set
	 */
	public void setCodPaisOrigen(String codPaisOrigen) {
		this.codPaisOrigen = codPaisOrigen;
	}

	/**
	 * @return the servicios
	 */
	public Servicios getServicios() {
		return servicios;
	}

	/**
	 * @param servicios the servicios to set
	 */
	public void setServicios(Servicios servicios) {
		this.servicios = servicios;
	}

}
