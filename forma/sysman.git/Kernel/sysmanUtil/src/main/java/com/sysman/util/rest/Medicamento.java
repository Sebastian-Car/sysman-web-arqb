package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;

public class Medicamento {
	@JsonProperty("CODPRESTADOR") // M01
	private String codPrestador;

	@JsonProperty("NUMAUTORIZACION") // M2
	private String numAutorizacion;

	@JsonProperty("IDMIPRES") // M03
	private String idMIPRES;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@JsonProperty("FECHADISPENSADMON") // M04
	private Date fechaDispensAdmon;

	@JsonProperty("CODDIAGNOSTICOPRINCIPAL") // M05
	private String codDiagnosticoPrincipal;

	@JsonProperty("CODDIAGNOSTICORELACIONADO") // M06
	private String codDiagnosticoRelacionado;

	@JsonProperty("TIPOMEDICAMENTO") // M07
	private String tipoMedicamento;

	@JsonProperty("CODTECNOLOGIASALUD") // M08
	private String codTecnologiaSalud;

	@JsonProperty("NOMTECNOLOGIASALUD") // M09
	private String nomTecnologiaSalud;

	@JsonProperty("CONCENTRACIONMEDICAMENTO") // M10
	private Integer concentracionMedicamento;

	@JsonProperty("UNIDADMEDIDA") // M11
	private Integer unidadMedida;

	@JsonProperty("FORMAFARMACEUTICA") // M12
	private String formaFarmaceutica;

	@JsonProperty("UNIDADMINDISPENSA") // M13
	private Integer unidadMinDispensa;

	@JsonProperty("CANTIDADMEDICAMENTO") // M14
	private Integer cantidadMedicamento;

	@JsonProperty("DIASTRATAMIENTO") // M15
	private Integer diasTratamiento;

	@JsonProperty("TIPODOCUMENTOIDENTIFICACION") // M16
	private String tipoDocumentoIdentificacion;

	@JsonProperty("NUMDOCUMENTOIDENTIFICACION") // M17
	private String numDocumentoIdentificacion;

	@JsonProperty("VRUNITMEDICAMENTO") // M18
	private BigDecimal vrUnitMedicamento;

	@JsonProperty("VRSERVICIO") // M19
	private BigDecimal vrServicio;

	@JsonProperty("CONCEPTORECAUDO") // M20
	private String conceptoRecaudo;

	@JsonProperty("VALORPAGOMODERADOR") // M21
	private BigDecimal valorPagoModerador;

	@JsonProperty("NUMFEVPAGOMODERADOR") // M22
	private String numFEVPagoModerador;

	@JsonProperty("CONSECUTIVO") // M23
	private Integer consecutivo;

	public Medicamento() {

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
	 * @return the idMIPRES
	 */
	public String getIdMIPRES() {
		return idMIPRES;
	}

	/**
	 * @param idMIPRES the idMIPRES to set
	 */
	public void setIdMIPRES(String idMIPRES) {
		this.idMIPRES = idMIPRES;
	}

	/**
	 * @return the fechaDispensAdmon
	 */
	public Date getFechaDispensAdmon() {
		return fechaDispensAdmon;
	}

	/**
	 * @param fechaDispensAdmon the fechaDispensAdmon to set
	 */
	public void setFechaDispensAdmon(Date fechaDispensAdmon) {
		this.fechaDispensAdmon = fechaDispensAdmon;
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
	 * @return the codDiagnosticoRelacionado
	 */
	public String getCodDiagnosticoRelacionado() {
		return codDiagnosticoRelacionado;
	}

	/**
	 * @param codDiagnosticoRelacionado the codDiagnosticoRelacionado to set
	 */
	public void setCodDiagnosticoRelacionado(String codDiagnosticoRelacionado) {
		this.codDiagnosticoRelacionado = codDiagnosticoRelacionado;
	}

	/**
	 * @return the tipoMedicamento
	 */
	public String getTipoMedicamento() {
		return tipoMedicamento;
	}

	/**
	 * @param tipoMedicamento the tipoMedicamento to set
	 */
	public void setTipoMedicamento(String tipoMedicamento) {
		this.tipoMedicamento = tipoMedicamento;
	}

	/**
	 * @return the codTecnologiaSalud
	 */
	public String getCodTecnologiaSalud() {
		return codTecnologiaSalud;
	}

	/**
	 * @param codTecnologiaSalud the codTecnologiaSalud to set
	 */
	public void setCodTecnologiaSalud(String codTecnologiaSalud) {
		this.codTecnologiaSalud = codTecnologiaSalud;
	}

	/**
	 * @return the nomTecnologiaSalud
	 */
	public String getNomTecnologiaSalud() {
		return nomTecnologiaSalud;
	}

	/**
	 * @param nomTecnologiaSalud the nomTecnologiaSalud to set
	 */
	public void setNomTecnologiaSalud(String nomTecnologiaSalud) {
		this.nomTecnologiaSalud = nomTecnologiaSalud;
	}

	/**
	 * @return the concentracionMedicamento
	 */
	public Integer getConcentracionMedicamento() {
		return concentracionMedicamento;
	}

	/**
	 * @param concentracionMedicamento the concentracionMedicamento to set
	 */
	public void setConcentracionMedicamento(Integer concentracionMedicamento) {
		this.concentracionMedicamento = concentracionMedicamento;
	}

	/**
	 * @return the unidadMedida
	 */
	public Integer getUnidadMedida() {
		return unidadMedida;
	}

	/**
	 * @param unidadMedida the unidadMedida to set
	 */
	public void setUnidadMedida(Integer unidadMedida) {
		this.unidadMedida = unidadMedida;
	}

	/**
	 * @return the formaFarmaceutica
	 */
	public String getFormaFarmaceutica() {
		return formaFarmaceutica;
	}

	/**
	 * @param formaFarmaceutica the formaFarmaceutica to set
	 */
	public void setFormaFarmaceutica(String formaFarmaceutica) {
		this.formaFarmaceutica = formaFarmaceutica;
	}

	/**
	 * @return the unidadMinDispensa
	 */
	public Integer getUnidadMinDispensa() {
		return unidadMinDispensa;
	}

	/**
	 * @param unidadMinDispensa the unidadMinDispensa to set
	 */
	public void setUnidadMinDispensa(Integer unidadMinDispensa) {
		this.unidadMinDispensa = unidadMinDispensa;
	}

	/**
	 * @return the cantidadMedicamento
	 */
	public Integer getCantidadMedicamento() {
		return cantidadMedicamento;
	}

	/**
	 * @param cantidadMedicamento the cantidadMedicamento to set
	 */
	public void setCantidadMedicamento(Integer cantidadMedicamento) {
		this.cantidadMedicamento = cantidadMedicamento;
	}

	/**
	 * @return the diasTratamiento
	 */
	public Integer getDiasTratamiento() {
		return diasTratamiento;
	}

	/**
	 * @param diasTratamiento the diasTratamiento to set
	 */
	public void setDiasTratamiento(Integer diasTratamiento) {
		this.diasTratamiento = diasTratamiento;
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
	 * @return the vrUnitMedicamento
	 */
	public BigDecimal getVrUnitMedicamento() {
		return vrUnitMedicamento;
	}

	/**
	 * @param vrUnitMedicamento the vrUnitMedicamento to set
	 */
	public void setVrUnitMedicamento(BigDecimal vrUnitMedicamento) {
		this.vrUnitMedicamento = vrUnitMedicamento;
	}

	/**
	 * @return the vrServicio
	 */
	public BigDecimal getVrServicio() {
		return vrServicio;
	}

	/**
	 * @param vrServicio the vrServicio to set
	 */
	public void setVrServicio(BigDecimal vrServicio) {
		this.vrServicio = vrServicio;
	}

	/**
	 * @return the conceptoRecaudo
	 */
	public String getConceptoRecaudo() {
		return conceptoRecaudo;
	}

	/**
	 * @param conceptoRecaudo the conceptoRecaudo to set
	 */
	public void setConceptoRecaudo(String conceptoRecaudo) {
		this.conceptoRecaudo = conceptoRecaudo;
	}

	/**
	 * @return the valorPagoModerador
	 */
	public BigDecimal getValorPagoModerador() {
		return valorPagoModerador;
	}

	/**
	 * @param valorPagoModerador the valorPagoModerador to set
	 */
	public void setValorPagoModerador(BigDecimal valorPagoModerador) {
		this.valorPagoModerador = valorPagoModerador;
	}

	/**
	 * @return the numFEVPagoModerador
	 */
	public String getNumFEVPagoModerador() {
		return numFEVPagoModerador;
	}

	/**
	 * @param numFEVPagoModerador the numFEVPagoModerador to set
	 */
	public void setNumFEVPagoModerador(String numFEVPagoModerador) {
		this.numFEVPagoModerador = numFEVPagoModerador;
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
