/*-
 * ParametroCuerpoEnvioFactura.java
 *
 * 1.0
 * 
 * 4/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import java.util.List;

/**
 * Clase que maneja los parametros del cuerpo de factura
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametroCuerpoEnvioFactura {

    private String acusadaRecibido;

    private int borradologico;

    private List<ParametrosCargos> cargos;

    private String createdBy;

    private String cufe;

    private String datosSoftware;

    private String descripcion;

    private double descuentoFactura;

    private double descuentoItems;

    private List<ParametrosDescuentos> descuentos;

    private String estado;

    private String fechaFinPeriodo;

    private String fechaInicioPeriodo;

    private String fechaVencimiento;

    private String fechafactura;

    private int id;

    private int idFactura;

    private String idTipoMoneda;

    private List<ParametrosImpuestos> impuestos;

    private List<ParametrosItems> items;

    private String medioPago;

    private String numCuotas;

    private String numTercero;

    private int numeroConceptos;

    private int numerofactura;

    private String observacionesContribuyente;

    private String observacionesFactura;

    private double porcentajeIva;

    private String prefijo;

    private String qr;

    private double reteFuente;

    private double reteIca;

    private double reteIva;

    private String reviso;

    private double subtotalfactura;

    private int tasaCambio;

    private String telefonoCliente;

    private String tipoDeFactura;

    private String tipoMoneda;

    private String tipoOperacion;

    private String tipoPago;

    private double totalBaseGravableIca;

    private double totalBaseGravableInc;

    private double totalBaseGravableIva;

    private double totalBaseGravableRete;

    private double totalBaseGravableReteica;

    private double totalBaseGravableReteiva;

    private double totalBaseImponible;

    private double valorIcaFactura;

    private double valorIncFactura;

    private double valorIvaFactura;

    private double valorfactura;
    
    private String prefijoOrden;
    
    private String numeroPrefijoOrden;
    
    

    public String getAcusadaRecibido() {
        return acusadaRecibido;
    }

    public void setAcusadaRecibido(String acusadaRecibido) {
        this.acusadaRecibido = acusadaRecibido;
    }

    public int getBorradologico() {
        return borradologico;
    }

    public void setBorradologico(int borradologico) {
        this.borradologico = borradologico;
    }

    public List<ParametrosCargos> getCargos() {
        return cargos;
    }

    public void setCargos(List<ParametrosCargos> cargos) {
        this.cargos = cargos;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getDatosSoftware() {
        return datosSoftware;
    }

    public void setDatosSoftware(String datosSoftware) {
        this.datosSoftware = datosSoftware;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getDescuentoFactura() {
        return descuentoFactura;
    }

    public void setDescuentoFactura(double descuentoFactura) {
        this.descuentoFactura = descuentoFactura;
    }

    public double getDescuentoItems() {
        return descuentoItems;
    }

    public void setDescuentoItems(double descuentoItems) {
        this.descuentoItems = descuentoItems;
    }

    public List<ParametrosDescuentos> getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(List<ParametrosDescuentos> descuentos) {
        this.descuentos = descuentos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaFinPeriodo() {
        return fechaFinPeriodo;
    }

    public void setFechaFinPeriodo(String fechaFinPeriodo) {
        this.fechaFinPeriodo = fechaFinPeriodo;
    }

    public String getFechaInicioPeriodo() {
        return fechaInicioPeriodo;
    }

    public void setFechaInicioPeriodo(String fechaInicioPeriodo) {
        this.fechaInicioPeriodo = fechaInicioPeriodo;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getFechafactura() {
        return fechafactura;
    }

    public void setFechafactura(String fechafactura) {
        this.fechafactura = fechafactura;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getIdTipoMoneda() {
        return idTipoMoneda;
    }

    public void setIdTipoMoneda(String idTipoMoneda) {
        this.idTipoMoneda = idTipoMoneda;
    }

    public List<ParametrosImpuestos> getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(List<ParametrosImpuestos> impuestos) {
        this.impuestos = impuestos;
    }

    public List<ParametrosItems> getItems() {
        return items;
    }

    public void setItems(List<ParametrosItems> items) {
        this.items = items;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(String numCuotas) {
        this.numCuotas = numCuotas;
    }

    public String getNumTercero() {
        return numTercero;
    }

    public void setNumTercero(String numTercero) {
        this.numTercero = numTercero;
    }

    public int getNumeroConceptos() {
        return numeroConceptos;
    }

    public void setNumeroConceptos(int numeroConceptos) {
        this.numeroConceptos = numeroConceptos;
    }

    public int getNumerofactura() {
        return numerofactura;
    }

    public void setNumerofactura(int numerofactura) {
        this.numerofactura = numerofactura;
    }

    public String getObservacionesContribuyente() {
        return observacionesContribuyente;
    }

    public void setObservacionesContribuyente(
        String observacionesContribuyente) {
        this.observacionesContribuyente = observacionesContribuyente;
    }

    public String getObservacionesFactura() {
        return observacionesFactura;
    }

    public void setObservacionesFactura(String observacionesFactura) {
        this.observacionesFactura = observacionesFactura;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public double getReteFuente() {
        return reteFuente;
    }

    public void setReteFuente(double reteFuente) {
        this.reteFuente = reteFuente;
    }

    public double getReteIca() {
        return reteIca;
    }

    public void setReteIca(double reteIca) {
        this.reteIca = reteIca;
    }

    public double getReteIva() {
        return reteIva;
    }

    public void setReteIva(double reteIva) {
        this.reteIva = reteIva;
    }

    public String getReviso() {
        return reviso;
    }

    public void setReviso(String reviso) {
        this.reviso = reviso;
    }

    public double getSubtotalfactura() {
        return subtotalfactura;
    }

    public void setSubtotalfactura(double subtotalfactura) {
        this.subtotalfactura = subtotalfactura;
    }

    public int getTasaCambio() {
        return tasaCambio;
    }

    public void setTasaCambio(int tasaCambio) {
        this.tasaCambio = tasaCambio;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getTipoDeFactura() {
        return tipoDeFactura;
    }

    public void setTipoDeFactura(String tipoDeFactura) {
        this.tipoDeFactura = tipoDeFactura;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public double getTotalBaseGravableIca() {
        return totalBaseGravableIca;
    }

    public void setTotalBaseGravableIca(double totalBaseGravableIca) {
        this.totalBaseGravableIca = totalBaseGravableIca;
    }

    public double getTotalBaseGravableInc() {
        return totalBaseGravableInc;
    }

    public void setTotalBaseGravableInc(double totalBaseGravableInc) {
        this.totalBaseGravableInc = totalBaseGravableInc;
    }

    public double getTotalBaseGravableIva() {
        return totalBaseGravableIva;
    }

    public void setTotalBaseGravableIva(double totalBaseGravableIva) {
        this.totalBaseGravableIva = totalBaseGravableIva;
    }

    public double getTotalBaseGravableRete() {
        return totalBaseGravableRete;
    }

    public void setTotalBaseGravableRete(double totalBaseGravableRete) {
        this.totalBaseGravableRete = totalBaseGravableRete;
    }

    public double getTotalBaseGravableReteica() {
        return totalBaseGravableReteica;
    }

    public void setTotalBaseGravableReteica(double totalBaseGravableReteica) {
        this.totalBaseGravableReteica = totalBaseGravableReteica;
    }

    public double getTotalBaseGravableReteiva() {
        return totalBaseGravableReteiva;
    }

    public void setTotalBaseGravableReteiva(double totalBaseGravableReteiva) {
        this.totalBaseGravableReteiva = totalBaseGravableReteiva;
    }

    public double getTotalBaseImponible() {
        return totalBaseImponible;
    }

    public void setTotalBaseImponible(double totalBaseImponible) {
        this.totalBaseImponible = totalBaseImponible;
    }

    public double getValorIcaFactura() {
        return valorIcaFactura;
    }

    public void setValorIcaFactura(double valorIcaFactura) {
        this.valorIcaFactura = valorIcaFactura;
    }

    public double getValorIncFactura() {
        return valorIncFactura;
    }

    public void setValorIncFactura(double valorIncFactura) {
        this.valorIncFactura = valorIncFactura;
    }

    public double getValorIvaFactura() {
        return valorIvaFactura;
    }

    public void setValorIvaFactura(double valorIvaFactura) {
        this.valorIvaFactura = valorIvaFactura;
    }

    public double getValorfactura() {
        return valorfactura;
    }

    public void setValorfactura(double valorfactura) {
        this.valorfactura = valorfactura;
    }

	public String getPrefijoOrden() {
		return prefijoOrden;
	}

	public void setPrefijoOrden(String prefijoOrden) {
		this.prefijoOrden = prefijoOrden;
	}

	public String getNumeroPrefijoOrden() {
		return numeroPrefijoOrden;
	}

	public void setNumeroPrefijoOrden(String numeroPrefijoOrden) {
		this.numeroPrefijoOrden = numeroPrefijoOrden;
	}
    
    

}
