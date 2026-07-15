package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Pojo requerido para obtener los datos requeridos del Comprobante
 * 
 * @version 1.0, 25/01/2020
 * @author eamaya
 * 
 */

public class ParametrosPagarFacturaGeneral implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String referencia;

    private String fechaPago;

    private String valorPagado;

    private String banco;

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getValorPagado() {
        return valorPagado;
    }

    public void setValorPagado(String valorPagado) {
        this.valorPagado = valorPagado;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

}
