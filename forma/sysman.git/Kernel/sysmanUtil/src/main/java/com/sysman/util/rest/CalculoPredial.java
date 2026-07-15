/*-
 * CalculoPredial.java
 *
 * 1.0
 * 
 * 6 ago. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

/**
 * Clase que permite el manejo de los atributos del calculo de Predial
 * 
 * @version 1.0, 6 ago. 2019
 * @author eamaya
 *
 */
public class CalculoPredial {

    private String compania;
    private String nitCompania;
    private String fechaCorte;
    private int consecutivoReserva;
    private boolean indAplicaLey1175;
    private boolean aplicaDescuento;
    private String codigoInicial;
    private String codigoFinal;
    private String numeroOrdenIni;
    private String numeroOrdenFin;
    private String usuario;
    private String origen;

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getNitCompania() {
        return nitCompania;
    }

    public void setNitCompania(String nitCompania) {
        this.nitCompania = nitCompania;
    }

    public String getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(String fechaCorte2) {
        this.fechaCorte = fechaCorte2;
    }

    public int getConsecutivoReserva() {
        return consecutivoReserva;
    }

    public void setConsecutivoReserva(int consecutivoReserva2) {
        this.consecutivoReserva = consecutivoReserva2;
    }

    public boolean isIndAplicaLey1175() {
        return indAplicaLey1175;
    }

    public void setIndAplicaLey1175(boolean indAplicaLey1175) {
        this.indAplicaLey1175 = indAplicaLey1175;
    }

    public boolean isAplicaDescuento() {
        return aplicaDescuento;
    }

    public void setAplicaDescuento(boolean aplicaDescuento) {
        this.aplicaDescuento = aplicaDescuento;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getNumeroOrdenIni() {
        return numeroOrdenIni;
    }

    public void setNumeroOrdenIni(String numeroOrdenIni) {
        this.numeroOrdenIni = numeroOrdenIni;
    }

    public String getNumeroOrdenFin() {
        return numeroOrdenFin;
    }

    public void setNumeroOrdenFin(String numeroOrdenFin) {
        this.numeroOrdenFin = numeroOrdenFin;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

}
