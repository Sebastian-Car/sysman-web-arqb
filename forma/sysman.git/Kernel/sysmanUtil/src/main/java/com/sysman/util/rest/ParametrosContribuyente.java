/*-
 * RespuestaAutoServicio.java
 *
 * 1.0
 * 
 * 26/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/*-
 * RespuestaApi.java
 *
 * 1.0
 * 
 * 18/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @version 1.1, 11/12/2020
 * @author eamaya
 *
 */
@XmlRootElement
public class ParametrosContribuyente {
    private String celularContribuyente;
    private String ciiu;
    private String ciudad;
    private String claveCorreo;
    private String codigodepartamento;
    private String codigomunicipio;
    private String codigopostal;
    private String correoEntrante;
    private String correoelectronico;
    private String createdBy;
    private String departamento;
    private String digitoverificacion;
    private String direccion;
    private String direccionfiscal;
    private String idContribuyente;
    private String identificadorsoftware;
    private String imgCorreo;
    private String logo;
    private String modifiedBy;
    private String nombrecontribuyente;
    private String numerodocumento;
    private String pais;
    private String pinsoftware;
    private String responsabilidadesfiscales;
    private String smtp;
    private String telefono;
    private String certificado;
    private String passCert;
    private String codigoReporte;
    private String testId;
    
    public String getCelularContribuyente() {
        return celularContribuyente;
    }

    public void setCelularContribuyente(String celularContribuyente) {
        this.celularContribuyente = celularContribuyente;
    }

    public String getCiiu() {
        return ciiu;
    }

    public void setCiiu(String ciiu) {
        this.ciiu = ciiu;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getClaveCorreo() {
        return claveCorreo;
    }

    public void setClaveCorreo(String claveCorreo) {
        this.claveCorreo = claveCorreo;
    }

    public String getCodigodepartamento() {
        return codigodepartamento;
    }

    public void setCodigodepartamento(String codigodepartamento) {
        this.codigodepartamento = codigodepartamento;
    }

    public String getCodigomunicipio() {
        return codigomunicipio;
    }

    public void setCodigomunicipio(String codigomunicipio) {
        this.codigomunicipio = codigomunicipio;
    }

    public String getCodigopostal() {
        return codigopostal;
    }

    public void setCodigopostal(String codigopostal) {
        this.codigopostal = codigopostal;
    }

    public String getCorreoEntrante() {
        return correoEntrante;
    }

    public void setCorreoEntrante(String correoEntrante) {
        this.correoEntrante = correoEntrante;
    }

    public String getCorreoelectronico() {
        return correoelectronico;
    }

    public void setCorreoelectronico(String correoelectronico) {
        this.correoelectronico = correoelectronico;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getDigitoverificacion() {
        return digitoverificacion;
    }

    public void setDigitoverificacion(String digitoverificacion) {
        this.digitoverificacion = digitoverificacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccionfiscal() {
        return direccionfiscal;
    }

    public void setDireccionfiscal(String direccionfiscal) {
        this.direccionfiscal = direccionfiscal;
    }

    public String getIdContribuyente() {
        return idContribuyente;
    }

    public void setIdContribuyente(String idContribuyente) {
        this.idContribuyente = idContribuyente;
    }

    public String getIdentificadorsoftware() {
        return identificadorsoftware;
    }

    public void setIdentificadorsoftware(String identificadorsoftware) {
        this.identificadorsoftware = identificadorsoftware;
    }

    public String getImgCorreo() {
        return imgCorreo;
    }

    public void setImgCorreo(String imgCorreo) {
        this.imgCorreo = imgCorreo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getNombrecontribuyente() {
        return nombrecontribuyente;
    }

    public void setNombrecontribuyente(String nombrecontribuyente) {
        this.nombrecontribuyente = nombrecontribuyente;
    }

    public String getNumerodocumento() {
        return numerodocumento;
    }

    public void setNumerodocumento(String numerodocumento) {
        this.numerodocumento = numerodocumento;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPinsoftware() {
        return pinsoftware;
    }

    public void setPinsoftware(String pinsoftware) {
        this.pinsoftware = pinsoftware;
    }

    public String getResponsabilidadesfiscales() {
        return responsabilidadesfiscales;
    }

    public void setResponsabilidadesfiscales(String responsabilidadesfiscales) {
        this.responsabilidadesfiscales = responsabilidadesfiscales;
    }

    public String getSmtp() {
        return smtp;
    }

    public void setSmtp(String smtp) {
        this.smtp = smtp;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTextoResponsabilidades() {
        return textoResponsabilidades;
    }

    public void setTextoResponsabilidades(String textoResponsabilidades) {
        this.textoResponsabilidades = textoResponsabilidades;
    }

    public String getTipoidentificacion() {
        return tipoidentificacion;
    }

    public void setTipoidentificacion(String tipoidentificacion) {
        this.tipoidentificacion = tipoidentificacion;
    }

    public String getTipoorganizacion() {
        return tipoorganizacion;
    }

    public void setTipoorganizacion(String tipoorganizacion) {
        this.tipoorganizacion = tipoorganizacion;
    }

    public String getTiporegimen() {
        return tiporegimen;
    }

    public void setTiporegimen(String tiporegimen) {
        this.tiporegimen = tiporegimen;
    }

    
    public String getCertificado() {
		return certificado;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

	public String getPassCert() {
		return passCert;
	}

	public void setPassCert(String passCert) {
		this.passCert = passCert;
	}

	public String getCodigoReporte() {
		return codigoReporte;
	}

	public void setCodigoReporte(String codigoReporte) {
		this.codigoReporte = codigoReporte;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}






	private String textoResponsabilidades;
    private String tipoidentificacion;
    private String tipoorganizacion;
    private String tiporegimen;


}
