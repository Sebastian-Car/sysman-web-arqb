/*-
 * RespuestaCuerpoConsultarReporte.java
 *
 * 1.0
 * 
 * 21/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * Cuerpo de respueta consultar reporte
 * 
 * @version 1.0, 21/12/2020
 * @author eamaya
 *
 */
public class RespuestaCuerpoConsultarReporte {

    private List<RespuestaFacturasReporte> facturas;

    private List<RespuestaNotasReporte> notas;
    
    private List<RespuestaFacturasReporte> documentosSoporte;

    public List<RespuestaFacturasReporte> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<RespuestaFacturasReporte> facturas) {
        this.facturas = facturas;
    }

    public List<RespuestaNotasReporte> getNotas() {
        return notas;
    }

    public void setNotas(List<RespuestaNotasReporte> notas) {
        this.notas = notas;
    }

	public List<RespuestaFacturasReporte> getDocumentoSoporte() {
		return documentosSoporte;
	}

	public void setDocumentoSoporte(List<RespuestaFacturasReporte> documentosSoporte) {
		this.documentosSoporte = documentosSoporte;
	}

}
