package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoValidacionFactura {

    @JsonProperty("RESULTSTATE")
    private boolean resultState;

    @JsonProperty("PROCESOID")
    private String procesoId;

    @JsonProperty("NUMFACTURA")
    private String numFactura;

    @JsonProperty("CODIGOUNICOVALIDACION")
    private String codigoUnicoValidacion;

    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    @JsonProperty("FECHAVALIDACION")
    private Date fechaValidacion;
    
    @JsonDeserialize(using = FlexibleDateDeserializer.class)
    @JsonProperty("FECHARADICACION")
    private Date fechaRadicacion;

    @JsonProperty("RESULTADOSVALIDACION")
    private List<ResultadoValidacionDetalle> resultadosValidacion;
    
    @JsonProperty("RUTAARCHIVOS")
    private String rutaArchivos;

    // Getters y Setters

    public boolean isResultState() {
        return resultState;
    }

    public void setResultState(boolean resultState) {
        this.resultState = resultState;
    }

    public String getProcesoId() {
        return procesoId;
    }

    public void setProcesoId(String procesoId) {
        this.procesoId = procesoId;
    }

    public String getNumFactura() {
        return numFactura;
    }

    public void setNumFactura(String numFactura) {
        this.numFactura = numFactura;
    }

    public String getCodigoUnicoValidacion() {
        return codigoUnicoValidacion;
    }

    public void setCodigoUnicoValidacion(String codigoUnicoValidacion) {
        this.codigoUnicoValidacion = codigoUnicoValidacion;
    }

    public Date getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(Date fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }
    
	public Date getFechaRadicacion() {
		return fechaRadicacion;
	}

	public void setFechaRadicacion(Date fechaRadicacion) {
		this.fechaRadicacion = fechaRadicacion;
	}

	public List<ResultadoValidacionDetalle> getResultadosValidacion() {
        return resultadosValidacion;
    }

    public void setResultadosValidacion(List<ResultadoValidacionDetalle> resultadosValidacion) {
        this.resultadosValidacion = resultadosValidacion;
    }

	public String getRutaArchivos() {
		return rutaArchivos;
	}

	public void setRutaArchivos(String rutaArchivos) {
		this.rutaArchivos = rutaArchivos;
	}
    
    
}
