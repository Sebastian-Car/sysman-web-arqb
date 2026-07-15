/*-
 * ParametrosTercero.java
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
 * Clase que envia los parametros al servicio que recibe los terceros
 * 
 * @version 1.0, 4/01/2021
 * @author eamaya
 *
 */
public class ParametrosTercero {
    /**
     * @value contribuyente
     */
    private String contribuyente;
    /**
     * Propiedad para modificado por
     */
    private String modifiedBy;
    /**
     * Propiedad para campo de auditoria creado por
     */
    private String createdBy;
    /**
     * @value ciudad
     */
    private String ciudad;
    /**
     * @value departamento
     */
    private String departamento;
    /**
     * @value direccion
     */
    private String direccion;
    /**
     * @value nombretercero
     */
    private String nombretercero;
    /**
     * @value nombretercero
     */
    private String apellidotercero;
    /**
     * @value numerodocumento
     */
    private String numerodocumento;
    /**
     * @value telefono
     */
    private String telefono;
    /**
     * @value pais
     */
    private String pais;
    /**
     * @value correoelectronico
     */
    private String correoelectronico;
    /**
     * @value tipoidentificacion
     */
    private String tipoidentificacion;
    /**
     * @value tiporegimen
     */
    private String tiporegimen;
    /**
     * @value tipoorganizacion
     */
    private String tipoorganizacion;

    private String codigodepartamento;

    private String codigomunicipio;

    private String codigopostal;

    private String digitoverificacion;

    private String direccionfiscal;

    private String responsabilidadesfiscales;

    /**
     * @return the contribuyente
     */
    public String getContribuyente() {
        return contribuyente;
    }

    /**
     * @param contribuyente
     * the contribuyente to set
     */
    public void setContribuyente(String contribuyente) {
        this.contribuyente = contribuyente;
    }

    /**
     * @return the tipoidentificacion
     */

    public String getTipoidentificacion() {
        return tipoidentificacion;
    }

    /**
     * @param tipoidentificacion
     * the tipoidentificacion to set
     */
    public void setTipoidentificacion(String tipoidentificacion) {
        this.tipoidentificacion = tipoidentificacion;
    }

    /**
     * @return the pais
     */
    public String getPais() {
        return pais;
    }

    /**
     * @param pais
     * the pais to set
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono
     * the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return the direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * @param direccion
     * the direccion to set
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * @return the departamento
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * @param departamento
     * the departamento to set
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * @return the ciudad
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * @param ciudad
     * the ciudad to set
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * @return the nombretercero
     */
    public String getNombretercero() {
        return nombretercero;
    }

    /**
     * @param nombretercero
     * the nombretercero to set
     */
    public void setNombretercero(String nombretercero) {
        this.nombretercero = nombretercero;
    }

    /**
     * @return the numerodocumento
     */
    public String getNumerodocumento() {
        return numerodocumento;
    }

    /**
     * @param numerodocumento
     * the numerodocumento to set
     */
    public void setNumerodocumento(String numerodocumento) {
        this.numerodocumento = numerodocumento;
    }

    /**
     * @return the correoelectronico
     */
    public String getCorreoelectronico() {
        return correoelectronico;
    }

    /**
     * @param correoelectronico
     * the correoelectronico to set
     */
    public void setCorreoelectronico(String correoelectronico) {
        this.correoelectronico = correoelectronico;
    }

    /**
     * @return the tiporegimen
     */
    public String getTiporegimen() {
        return tiporegimen;
    }

    /**
     * @param tiporegimen
     * the tiporegimen to set
     */
    public void setTiporegimen(String tiporegimen) {
        this.tiporegimen = tiporegimen;
    }

    /**
     * @return the tipoorganizacion
     */
    public String getTipoorganizacion() {
        return tipoorganizacion;
    }

    /**
     * @param tipoorganizacion
     * the tipoorganizacion to set
     */
    public void setTipoorganizacion(String tipoorganizacion) {
        this.tipoorganizacion = tipoorganizacion;
    }

    /**
     * @return the cretedBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param cretedBy
     * the cretedBy to set
     */
    public void setCreatedBy(String cretedBy) {
        this.createdBy = cretedBy;
    }

    /**
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy
     * the modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the apellidotercero
     */
    public String getApellidotercero() {
        return apellidotercero;
    }

    /**
     * @param apellidotercero
     * the apellidotercero to set
     */
    public void setApellidotercero(String apellidotercero) {
        this.apellidotercero = apellidotercero;
    }

    /**
     * @return the codigodepartamento
     */
    public String getCodigodepartamento() {
        return codigodepartamento;
    }

    /**
     * @param codigodepartamento
     * the codigodepartamento to set
     */
    public void setCodigodepartamento(String codigodepartamento) {
        this.codigodepartamento = codigodepartamento;
    }

    /**
     * @return the codigomunicipio
     */
    public String getCodigomunicipio() {
        return codigomunicipio;
    }

    /**
     * @param codigomunicipio
     * the codigomunicipio to set
     */
    public void setCodigomunicipio(String codigomunicipio) {
        this.codigomunicipio = codigomunicipio;
    }

    /**
     * @return the codigopostal
     */
    public String getCodigopostal() {
        return codigopostal;
    }

    /**
     * @param codigopostal
     * the codigopostal to set
     */
    public void setCodigopostal(String codigopostal) {
        this.codigopostal = codigopostal;
    }

    /**
     * @return the digitoverificacion
     */
    public String getDigitoverificacion() {
        return digitoverificacion;
    }

    /**
     * @param digitoverificacion
     * the digitoverificacion to set
     */
    public void setDigitoverificacion(String digitoverificacion) {
        this.digitoverificacion = digitoverificacion;
    }

    /**
     * @return the direccionfiscal
     */
    public String getDireccionfiscal() {
        return direccionfiscal;
    }

    /**
     * @param direccionfiscal
     * the direccionfiscal to set
     */
    public void setDireccionfiscal(String direccionfiscal) {
        this.direccionfiscal = direccionfiscal;
    }

    /**
     * @return the responsabilidadesfiscales
     */
    public String getResponsabilidadesfiscales() {
        return responsabilidadesfiscales;
    }

    /**
     * @param responsabilidadesfiscales
     * the responsabilidadesfiscales to set
     */
    public void setResponsabilidadesfiscales(String responsabilidadesfiscales) {
        this.responsabilidadesfiscales = responsabilidadesfiscales;
    }
}
