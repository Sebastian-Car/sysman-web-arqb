package com.sysman.util.rest;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;

public class Procedimiento {
	@JsonProperty("CODPRESTADOR")
    private String codPrestador; //P01

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("FECHAINICIOATENCION") 
    private Date fechaInicioAtencion;//P02
    
    @JsonProperty("IDMIPRES") //P03
    private String idMIPRES;

    @JsonProperty("NUMAUTORIZACION")
    private String numAutorizacion; //P04 

    @JsonProperty("CODPROCEDIMIENTO")
    private String codProcedimiento; //P05

    @JsonProperty("VIAINGRESOSERVICIOSALUD") 
    private String viaIngresoServicioSalud ; //P06

    @JsonProperty("MODALIDADGRUPOSERVICIOTECSAL") 
    private String modalidadGrupoServicioTecSal ; //P07

    @JsonProperty("GRUPOSERVICIOS") //P08
    private String grupoServicios;

    @JsonProperty("CODSERVICIO") //P09
    private Integer codServicio;

    @JsonProperty("FINALIDADTECNOLOGIASALUD") //P10
    private String finalidadTecnologiaSalud; 

    @JsonProperty("TIPODOCUMENTOIDENTIFICACION") //P11
    private String tipoDocumentoIdentificacion; 

    @JsonProperty("NUMDOCUMENTOIDENTIFICACION") //P12
    private String numDocumentoIdentificacion; 

    @JsonProperty("CODDIAGNOSTICOPRINCIPAL") //P13
    private String codDiagnosticoPrincipal; 

    @JsonProperty("CODDIAGNOSTICORELACIONADO") //P14
    private String codDiagnosticoRelacionado;

    @JsonProperty("CODCOMPLICACION") //P15
    private String codComplicacion;

    @JsonProperty("VRSERVICIO") //P16
    private BigDecimal vrServicio;

    @JsonProperty("CONCEPTORECAUDO") //P17
    private String conceptoRecaudo;

    @JsonProperty("VALORPAGOMODERADOR") //P18
    private BigDecimal valorPagoModerador;

    @JsonProperty("NUMFEVPAGOMODERADOR") //P19
    private String numFEVPagoModerador;

    @JsonProperty("CONSECUTIVO") //P20
    private Integer consecutivo;
    
    
	public Procedimiento() {

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
	 * @return the fechalnicioAtencion
	 */
	public Date getFechaInicioAtencion() {
		return fechaInicioAtencion;
	}


	/**
	 * @param fechalnicioAtencion the fechalnicioAtencion to set
	 */
	public void setFechaInicioAtencion(Date fechalnicioAtencion) {
		this.fechaInicioAtencion = fechalnicioAtencion;
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
	 * @return the codProcedimiento
	 */
	public String getCodProcedimiento() {
		return codProcedimiento;
	}


	/**
	 * @param codProcedimiento the codProcedimiento to set
	 */
	public void setCodProcedimiento(String codProcedimiento) {
		this.codProcedimiento = codProcedimiento;
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
