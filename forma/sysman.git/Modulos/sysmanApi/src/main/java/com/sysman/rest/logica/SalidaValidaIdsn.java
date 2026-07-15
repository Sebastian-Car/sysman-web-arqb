/*-
 * SalidaValidaIdsn.java
 *
 * 1.0
 * 
 * 26/04/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */	

package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Clase para guardar la salida del servicio validapagoidsn
 * 
 * @version 1.0, 26/04/2019
 * @author jgomez
 *
 */
public class SalidaValidaIdsn implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Tipo de comprobante con el que se realiza el pago de la factura
     */
    private String tipo;
    /**
     * N&uacute;mero del comprobante con el cual se realiza el pago de la factura
     */
    private Long comprobante;
    /**
     * Fecha del comprobante con el cual se realiza el pago de la factura
     */
    private String fecha;
    /**
     * Tipo de comprobante de disponibilidad generado y heredado para realizar el pago de la factura
     */
    private String tipocdp;
    /**
     * N&uacute;mero de comprobante de disponibilidad generado y heredado para realizar el pago de la factura
     */
    private Long cdp;
    /**
     * Tipo de comprobante de registro generado y heredado para realizar el pago de la factura
     */
    private String tiporp;
    /**
     * N&uacute;mero de comprobante de registro generado y heredado para realizar el pago de la factura
     */
    private Long rp;
    
    public SalidaValidaIdsn() {
        
    }
    
    /**
     * @param tipo
     * @param comprobante
     * @param fecha
     * @param tipocdp
     * @param cdp
     * @param tiporp
     * @param rp
     */
    public SalidaValidaIdsn(String tipo, Long comprobante, String fecha,
        String tipocdp, Long cdp, String tiporp, Long rp) {
        this.tipo = tipo;
        this.comprobante = comprobante;
        this.fecha = fecha;
        this.tipocdp = tipocdp;
        this.cdp = cdp;
        this.tiporp = tiporp;
        this.rp = rp;
    }
    
    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }
    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    /**
     * @return the comprobante
     */
    public Long getComprobante() {
        return comprobante;
    }
    /**
     * @param comprobante the comprobante to set
     */
    public void setComprobante(Long comprobante) {
        this.comprobante = comprobante;
    }
    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }
    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    /**
     * @return the tipocdp
     */
    public String getTipocdp() {
        return tipocdp;
    }
    /**
     * @param tipocdp the tipocdp to set
     */
    public void setTipocdp(String tipocdp) {
        this.tipocdp = tipocdp;
    }
    /**
     * @return the cdp
     */
    public Long getCdp() {
        return cdp;
    }
    /**
     * @param cdp the cdp to set
     */
    public void setCdp(Long cdp) {
        this.cdp = cdp;
    }
    /**
     * @return the tiporp
     */
    public String getTiporp() {
        return tiporp;
    }
    /**
     * @param tiporp the tiporp to set
     */
    public void setTiporp(String tiporp) {
        this.tiporp = tiporp;
    }
    /**
     * @return the rp
     */
    public Long getRp() {
        return rp;
    }
    /**
     * @param rp the rp to set
     */
    public void setRp(Long rp) {
        this.rp = rp;
    }

    
}
