/*-
 * RespuestaApi.java
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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase estandar para generar la respuesta de todos los servicios con
 * el fin de enviar los errores de negocio claramente
 * 
 * @version 1.1, 22/01/2020
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaCargarDetalleComprobanteCnt {

    private String tipoCpteAfect;

    private String cmpteAfectado;

    private String fechaConsignacionPlano;

    private String comprobante;

    private double valorCredito;

    private String compania;

    private String tipoCpte;

    private int anio;

    private double valorDebito;

    private String cuenta;

    private String tercero;

    private double abonoInicial;

    private double vlrDocumento;

    private String naturaleza;

    private String fecha;

    private String consecutivo;

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

    public String getFechaConsignacionPlano() {
        return fechaConsignacionPlano;
    }

    public void setFechaConsignacionPlano(String fechaConsignacionPlano) {
        this.fechaConsignacionPlano = fechaConsignacionPlano;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    public double getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(double valorCredito) {
        this.valorCredito = valorCredito;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getTipoCpte() {
        return tipoCpte;
    }

    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public double getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(double valorDebito) {
        this.valorDebito = valorDebito;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public double getAbonoInicial() {
        return abonoInicial;
    }

    public void setAbonoInicial(double abonoInicial) {
        this.abonoInicial = abonoInicial;
    }

    public double getVlrDocumento() {
        return vlrDocumento;
    }

    public void setVlrDocumento(double vlrDocumento) {
        this.vlrDocumento = vlrDocumento;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

}
