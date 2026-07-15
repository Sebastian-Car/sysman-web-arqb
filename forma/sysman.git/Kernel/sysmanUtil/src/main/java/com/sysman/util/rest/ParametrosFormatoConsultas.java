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
 * @version 1.1, 21/12/2020
 * @author eamaya
 *
 */
@XmlRootElement
public class ParametrosFormatoConsultas {

    private String certificado;
    private String nombreCertificado;
    private String numContribuyente;
    private String numDocumento;
    private String passCertificado;
    private String prefijo;
    private String tipoDocumento;

    public String getCertificado() {
        return certificado;
    }

    public void setCertificado(String certificado) {
        this.certificado = certificado;
    }

    public String getNombreCertificado() {
        return nombreCertificado;
    }

    public void setNombreCertificado(String nombreCertificado) {
        this.nombreCertificado = nombreCertificado;
    }

    public String getNumContribuyente() {
        return numContribuyente;
    }

    public void setNumContribuyente(String numContribuyente) {
        this.numContribuyente = numContribuyente;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getPassCertificado() {
        return passCertificado;
    }

    public void setPassCertificado(String passCertificado) {
        this.passCertificado = passCertificado;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

}
