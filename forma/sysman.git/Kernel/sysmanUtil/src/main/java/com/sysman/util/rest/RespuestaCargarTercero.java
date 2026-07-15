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
public class RespuestaCargarTercero {
    private String nit;

    private String sucursal;

    private String compania;

    private String nombre;

    private String direccion;

    private String pais;

    private String departamento;

    private String ciudad;

    private String telefono;

    private String tipoId;

    private String nitCedula;

    private String nombre1;

    private String nombre2;

    private String apellildo1;

    private String apellildo2;

    private String propietario;

    private String clase;

    private String naturaleza;

    private String tipoAsociado;

    private String regimen;

    private double porcDescuento;

    private String fax;

    private String email;

    private String autoretenedor;

    private String claseEntidadOficial;

    private String aplicaDescuento;

    private String expedidaCedula;

    private String digitoVerificacion;

    private String orden;

    private String estado;

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoId() {
        return tipoId;
    }

    public void setTipoId(String tipoId) {
        this.tipoId = tipoId;
    }

    public String getNitCedula() {
        return nitCedula;
    }

    public void setNitCedula(String nitCedula) {
        this.nitCedula = nitCedula;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellildo1() {
        return apellildo1;
    }

    public void setApellildo1(String apellildo1) {
        this.apellildo1 = apellildo1;
    }

    public String getApellildo2() {
        return apellildo2;
    }

    public void setApellildo2(String apellildo2) {
        this.apellildo2 = apellildo2;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public String getTipoAsociado() {
        return tipoAsociado;
    }

    public void setTipoAsociado(String tipoAsociado) {
        this.tipoAsociado = tipoAsociado;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public double getPorcDescuento() {
        return porcDescuento;
    }

    public void setPorcDescuento(double porcDescuento) {
        this.porcDescuento = porcDescuento;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAutoretenedor() {
        return autoretenedor;
    }

    public void setAutoretenedor(String autoretenedor) {
        this.autoretenedor = autoretenedor;
    }

    public String getClaseEntidadOficial() {
        return claseEntidadOficial;
    }

    public void setClaseEntidadOficial(String claseEntidadOficial) {
        this.claseEntidadOficial = claseEntidadOficial;
    }

    public String getAplicaDescuento() {
        return aplicaDescuento;
    }

    public void setAplicaDescuento(String aplicaDescuento) {
        this.aplicaDescuento = aplicaDescuento;
    }

    public String getExpedidaCedula() {
        return expedidaCedula;
    }

    public void setExpedidaCedula(String expedidaCedula) {
        this.expedidaCedula = expedidaCedula;
    }

    public String getDigitoVerificacion() {
        return digitoVerificacion;
    }

    public void setDigitoVerificacion(String digitoVerificacion) {
        this.digitoVerificacion = digitoVerificacion;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
