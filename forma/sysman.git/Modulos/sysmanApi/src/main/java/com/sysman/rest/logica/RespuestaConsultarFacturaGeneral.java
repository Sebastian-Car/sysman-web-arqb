package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Pojo requerido para obtener los datos requeridos del Comprobante
 * 
 * @version 1.0, 25/01/2020
 * @author eamaya
 * 
 */

public class RespuestaConsultarFacturaGeneral implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String fechaLimite;

    private double valorFactura;

    private String estado;

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public double getValorFactura() {
        return valorFactura;
    }

    public void setValorFactura(double valorFactura) {
        this.valorFactura = valorFactura;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
