/*-
 * ParametrosImpuestos.java
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

/**
 * Clase que administra los parametros de impuestos
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametrosImpuestos {

    private Double base;

    private String descripcion;

    private Double porcentaje;

    private String tipo;

    private Double valor;

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

}
