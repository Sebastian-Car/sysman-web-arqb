/*-
 * PojoNominaElectronica.java
 *
 * 1.0
 * 
 * 28/10/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Pojo para env&iacute;r los datos de n&oacute;mina
 * electr&oacute;nica
 * 
 * @version 1.0, 28/10/2021
 * @author mzanguna
 *
 */
@XmlRootElement
public class PojoNominaElectronica {
    private String pruebaDeRegistro;
    private String nitEmpleador;
    private String nitProveedor;
    private String version;
    private String testId;
    private String softwarePin;    
    private String usuarioAccion;
    private Map<String, Object> datosNomina;
    private String nombreCertificado;
    private String passCertificado;
    private String certificadoBase64;


    public String getPruebaDeRegistro() {
        return pruebaDeRegistro;
    }

    public void setPruebaDeRegistro(String pruebaDeRegistro) {
        this.pruebaDeRegistro = pruebaDeRegistro;
    }

    public String getNitEmpleador() {
        return nitEmpleador;
    }

    public void setNitEmpleador(String nitEmpleador) {
        this.nitEmpleador = nitEmpleador;
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNombreCertificado() {
        return nombreCertificado;
    }

    public void setNombreCertificado(String nombreCertificado) {
        this.nombreCertificado = nombreCertificado;
    }

    public String getCertificadoBase64() {
        return certificadoBase64;
    }

    public void setCertificadoBase64(String certificadoBase64) {
        this.certificadoBase64 = certificadoBase64;
    }

    public String getPassCertificado() {
        return passCertificado;
    }

    public void setPassCertificado(String passCertificado) {
        this.passCertificado = passCertificado;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getSoftwarePin() {
        return softwarePin;
    }

    public void setSoftwarePin(String softwarePin) {
        this.softwarePin = softwarePin;
    }

    public Map<String, Object> getDatosNomina() {
        return datosNomina;
    }

    public void setDatosNomina(Map<String, Object> datosNomina) {
        this.datosNomina = datosNomina;
    }

    public String getUsuarioAccion() {
        return usuarioAccion;
    }

    public void setUsuarioAccion(String usuarioAccion) {
        this.usuarioAccion = usuarioAccion;
    }
}
