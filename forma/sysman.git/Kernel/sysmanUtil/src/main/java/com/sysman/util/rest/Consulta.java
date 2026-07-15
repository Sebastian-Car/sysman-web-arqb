package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal; // Para valores monetarios
import java.util.Date; // Para fechas y horas

public class Consulta {
    @JsonProperty("CODPRESTADOR")//C01
    private String codPrestador;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("FECHAINICIOATENCION")//C02
    private Date fechaInicioAtencion; // "YYYY-MM-DD HH:mm"

    @JsonProperty("NUMAUTORIZACION")//C03
    private String numAutorizacion;

    @JsonProperty("CODCONSULTA")//C04
    private String codConsulta;

    @JsonProperty("MODALIDADGRUPOSERVICIOTECSAL")//C05
    private String modalidadGrupoServicioTecSal;

    @JsonProperty("GRUPOSERVICIOS")//C06
    private String grupoServicios;

    @JsonProperty("CODSERVICIO")//C07
    private Integer codServicio; 

    @JsonProperty("FINALIDADTECNOLOGIASALUD")//C08
    private String finalidadTecnologiaSalud;

    @JsonProperty("CAUSAMOTIVOATENCION")//C09
    private String causaMotivoAtencion;

    @JsonProperty("CODDIAGNOSTICOPRINCIPAL")//C10
    private String codDiagnosticoPrincipal;

    @JsonProperty("CODDIAGNOSTICORELACIONADO1")//C11
    private String codDiagnosticoRelacionado1;

    @JsonProperty("CODDIAGNOSTICORELACIONADO2")//C12
    private String codDiagnosticoRelacionado2;

    @JsonProperty("CODDIAGNOSTICORELACIONADO3")//C13
    private String codDiagnosticoRelacionado3;

    @JsonProperty("TIPODIAGNOSTICOPRINCIPAL")//C14
    private String tipoDiagnosticoPrincipal;

    @JsonProperty("TIPODOCUMENTOIDENTIFICACION")//C15
    private String tipoDocumentoIdentificacion; 

    @JsonProperty("NUMDOCUMENTOIDENTIFICACION")//C16
    private String numDocumentoIdentificacion; 

    @JsonProperty("VRSERVICIO")//C17
    private BigDecimal vrServicio;

    @JsonProperty("CONCEPTORECAUDO")//C18
    private String conceptoRecaudo;

    @JsonProperty("VALORPAGOMODERADOR")//C19
    private BigDecimal valorPagoModerador;

    @JsonProperty("NUMFEVPAGOMODERADOR")//C20
    private String numFEVPagoModerador;

    @JsonProperty("CONSECUTIVO")//C21
    private Integer consecutivo;
    
    
	public Consulta() {

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
	 * @return the codConsulta
	 */
	public String getCodConsulta() {
		return codConsulta;
	}


	/**
	 * @param codConsulta the codConsulta to set
	 */
	public void setCodConsulta(String codConsulta) {
		this.codConsulta = codConsulta;
	}


	/**
	 * @return the modalidadGrupoServicioTecSal
	 */
	public String getModalidadGrupoServicioTecSal() {
		return modalidadGrupoServicioTecSal;
	}


	/**
	 * @param modalidadGrupoServicioTecSal the modalidadGrupoServicioTecSal to set
	 */
	public void setModalidadGrupoServicioTecSal(String modalidadGrupoServicioTecSal) {
		this.modalidadGrupoServicioTecSal = modalidadGrupoServicioTecSal;
	}


	/**
	 * @return the grupoServicios
	 */
	public String getGrupoServicios() {
		return grupoServicios;
	}


	/**
	 * @param grupoServicios the grupoServicios to set
	 */
	public void setGrupoServicios(String grupoServicios) {
		this.grupoServicios = grupoServicios;
	}


	/**
	 * @return the codServicio
	 */
	public Integer getCodServicio() {
		return codServicio;
	}


	/**
	 * @param codServicio the codServicio to set
	 */
	public void setCodServicio(Integer codServicio) {
		this.codServicio = codServicio;
	}


	/**
	 * @return the finalidadTecnologiaSalud
	 */
	public String getFinalidadTecnologiaSalud() {
		return finalidadTecnologiaSalud;
	}


	/**
	 * @param finalidadTecnologiaSalud the finalidadTecnologiaSalud to set
	 */
	public void setFinalidadTecnologiaSalud(String finalidadTecnologiaSalud) {
		this.finalidadTecnologiaSalud = finalidadTecnologiaSalud;
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
	 * @return the codDiagnosticoRelacionado1
	 */
	public String getCodDiagnosticoRelacionado1() {
		return codDiagnosticoRelacionado1;
	}


	/**
	 * @param codDiagnosticoRelacionado1 the codDiagnosticoRelacionado1 to set
	 */
	public void setCodDiagnosticoRelacionado1(String codDiagnosticoRelacionado1) {
		this.codDiagnosticoRelacionado1 = codDiagnosticoRelacionado1;
	}


	/**
	 * @return the codDiagnosticoRelacionado2
	 */
	public String getCodDiagnosticoRelacionado2() {
		return codDiagnosticoRelacionado2;
	}


	/**
	 * @param codDiagnosticoRelacionado2 the codDiagnosticoRelacionado2 to set
	 */
	public void setCodDiagnosticoRelacionado2(String codDiagnosticoRelacionado2) {
		this.codDiagnosticoRelacionado2 = codDiagnosticoRelacionado2;
	}


	/**
	 * @return the codDiagnosticoRelacionado3
	 */
	public String getCodDiagnosticoRelacionado3() {
		return codDiagnosticoRelacionado3;
	}


	/**
	 * @param codDiagnosticoRelacionado3 the codDiagnosticoRelacionado3 to set
	 */
	public void setCodDiagnosticoRelacionado3(String codDiagnosticoRelacionado3) {
		this.codDiagnosticoRelacionado3 = codDiagnosticoRelacionado3;
	}


	/**
	 * @return the tipoDiagnosticoPrincipal
	 */
	public String getTipoDiagnosticoPrincipal() {
		return tipoDiagnosticoPrincipal;
	}


	/**
	 * @param tipoDiagnosticoPrincipal the tipoDiagnosticoPrincipal to set
	 */
	public void setTipoDiagnosticoPrincipal(String tipoDiagnosticoPrincipal) {
		this.tipoDiagnosticoPrincipal = tipoDiagnosticoPrincipal;
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
