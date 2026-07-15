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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Clase que permite el manejo de los atributos de la respuesta del
 * calculo predial
 * 
 * @version 19/05/2020
 * @author eamaya
 *
 */
@XmlRootElement
public class RespuestaCuerpoDeudaPredio {

    private String compania;
    private String sucursal;
    private String ciudad;
    private String nit;
    private String tipodoc;
    private List<RespuestaDeudaDetalles> detalles;
    private String nombre;

    /**
     * Constructor
     */

    public RespuestaCuerpoDeudaPredio(String compania, String sucursal,
        String ciudad, String nit, String tipodoc, String nombre,
        List<RespuestaDeudaDetalles> detalles) {
        super();
        this.compania = compania;
        this.sucursal = sucursal;
        this.ciudad = ciudad;
        this.nit = nit;
        this.tipodoc = tipodoc;
        this.nombre = nombre;
        this.detalles = detalles;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTipodoc() {
        return tipodoc;
    }

    public void setTipodoc(String tipodoc) {
        this.tipodoc = tipodoc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<RespuestaDeudaDetalles> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<RespuestaDeudaDetalles> detalles) {
        this.detalles = detalles;
    }

}
