package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Pojo requerido para obtener los datos requeridos los detalles del
 * comprobante
 * 
 * 
 * @version 1.0, 25/01/2020
 * @author eamaya
 * 
 * 
 */
public class ParametrosDetalleComprobanteCnt implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String compania;

    private String ano;

    private String tipoCpte;

    private String comprobante;

    private String consecutivo;

    private String cuenta;

    private String cuentaPptal;

    private String fecha;

    private String naturaleza;

    private double valorDebito;

    private double valorCredito;

    private double ejecucionDebito;

    private double ejecucionCredito;

    private String descripcion;

    private String centroCosto;

    private String tercero;

    private String sucursal;

    private String auxiliar;

    private String nroDocumento;

    private String anoAfect;

    private String tipoCpteAfect;

    private String cmpteAfectado;

    private String consecutivoAfectado;

    private String dateCreated;

    private String createdBy;

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getTipoCpte() {
        return tipoCpte;
    }

    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCuentaPptal() {
        return cuentaPptal;
    }

    public void setCuentaPptal(String cuentaPptal) {
        this.cuentaPptal = cuentaPptal;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public double getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(double valorDebito) {
        this.valorDebito = valorDebito;
    }

    public double getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(double valorCredito) {
        this.valorCredito = valorCredito;
    }

    public double getEjecucionDebito() {
        return ejecucionDebito;
    }

    public void setEjecucionDebito(double ejecucionDebito) {
        this.ejecucionDebito = ejecucionDebito;
    }

    public double getEjecucionCredito() {
        return ejecucionCredito;
    }

    public void setEjecucionCredito(double ejecucionCredito) {
        this.ejecucionCredito = ejecucionCredito;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getAnoAfect() {
        return anoAfect;
    }

    public void setAnoAfect(String anoAfect) {
        this.anoAfect = anoAfect;
    }

    public String getTipoCpteAfect() {
        return tipoCpteAfect;
    }

    public void setTipoCpteAfect(String tipoCpteAfect) {
        this.tipoCpteAfect = tipoCpteAfect;
    }

    public String getCmpteAfectado() {
        return cmpteAfectado;
    }

    public void setCmpteAfectado(String cmpteAfectado) {
        this.cmpteAfectado = cmpteAfectado;
    }

    public String getConsecutivoAfectado() {
        return consecutivoAfectado;
    }

    public void setConsecutivoAfectado(String consecutivoAfectado) {
        this.consecutivoAfectado = consecutivoAfectado;
    }

}
