package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;

public class OtroServicio {

	@JsonProperty("CODPRESTADOR")
	private String codPrestador;

	@JsonProperty("NUMAUTORIZACION")
	private String numAutorizacion;

	@JsonProperty("IDMIPRES")
	private String idMIPRES;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@JsonProperty("FECHASUMINISTROTECNOLOGIA")
	private Date fechaSuministroTecnologia;

	@JsonProperty("TIPOOS")
	private String tipoOS;

	@JsonProperty("CODTECNOLOGIASALUD")
	private String codTecnologiaSalud;

	@JsonProperty("NOMTECNOLOGIASALUD")
	private String nomTecnologiaSalud;

	@JsonProperty("CANTIDADOS")
	private Integer cantidadOS;

	@JsonProperty("TIPODOCUMENTOIDENTIFICACION")
	private String tipoDocumentoIdentificacion;

	@JsonProperty("NUMDOCUMENTOIDENTIFICACION")
	private String numDocumentoIdentificacion;

	@JsonProperty("VRUNITOS")
	private BigDecimal vrUnitOS;

	@JsonProperty("VRSERVICIO")
	private BigDecimal vrServicio;

	@JsonProperty("CONCEPTORECAUDO")
	private String conceptoRecaudo;

	@JsonProperty("VALORPAGOMODERADOR")
	private BigDecimal valorPagoModerador;

	@JsonProperty("NUMFEVPAGOMODERADOR")
	private String numFEVPagoModerador;

	@JsonProperty("CONSECUTIVO")
	private Integer consecutivo;

	// Constructor, getters y setters
	public OtroServicio() {

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
	 * @return the fechaSuministroTecnologia
	 */
	public Date getFechaSuministroTecnologia() {
		return fechaSuministroTecnologia;
	}

	/**
	 * @param fechaSuministroTecnologia the fechaSuministroTecnologia to set
	 */
	public void setFechaSuministroTecnologia(Date fechaSuministroTecnologia) {
		this.fechaSuministroTecnologia = fechaSuministroTecnologia;
	}

	/**
	 * @return the tipoOS
	 */
	public String getTipoOS() {
		return tipoOS;
	}

	/**
	 * @param tipoOS the tipoOS to set
	 */
	public void setTipoOS(String tipoOS) {
		this.tipoOS = tipoOS;
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
	 * @return the cantidadOS
	 */
	public Integer getCantidadOS() {
		return cantidadOS;
	}

	/**
	 * @param cantidadOS the cantidadOS to set
	 */
	public void setCantidadOS(Integer cantidadOS) {
		this.cantidadOS = cantidadOS;
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
	 * @return the vrUnitOS
	 */
	public BigDecimal getVrUnitOS() {
		return vrUnitOS;
	}

	/**
	 * @param vrUnitOS the vrUnitOS to set
	 */
	public void setVrUnitOS(BigDecimal vrUnitOS) {
		this.vrUnitOS = vrUnitOS;
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
