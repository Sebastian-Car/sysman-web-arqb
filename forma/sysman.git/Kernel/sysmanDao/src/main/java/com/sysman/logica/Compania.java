/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.logica;

import java.io.Serializable;

/**
 *
 * @author cmanrique
 */
public class Compania implements Serializable {

    private String codigo;
    private String nombre;
    private String rutaImagen;
    private String sigla;
    private String moneda;
    private String codigoPais;
    private String codigoDepartamento;
    private String codigoCiudad;
    private String pais;
    private String departamento;
    private String ciudad;
    private String nit;
    private String direccion;
    private String telefono;
    private boolean retieneFuente;
    private boolean retieneIva;
    private boolean retieneIca;
    private boolean retieneTimbre;
    private boolean consolidada;
    private String fax;
    private String codigoDane;
    private String mision;
    private String vision;
    private String email;
    private String paginaWeb;
    private int tipoEntidad;
    private String codigosChip;
    private String firmaFactura;
    private String codigoWeb;
    private String contacto;
    private String rutaSticker;
    private String rutaVigiladoPor;
    private String nuir;
    private String codigoContaduria;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
    }

    public String getCodigoCiudad() {
        return codigoCiudad;
    }

    public void setCodigoCiudad(String codigoCiudad) {
        this.codigoCiudad = codigoCiudad;
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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isRetieneFuente() {
        return retieneFuente;
    }

    public void setRetieneFuente(boolean retieneFuente) {
        this.retieneFuente = retieneFuente;
    }

    public boolean isRetieneIva() {
        return retieneIva;
    }

    public void setRetieneIva(boolean retieneIva) {
        this.retieneIva = retieneIva;
    }

    public boolean isRetieneIca() {
        return retieneIca;
    }

    public void setRetieneIca(boolean retieneIca) {
        this.retieneIca = retieneIca;
    }

    public boolean isRetieneTimbre() {
        return retieneTimbre;
    }

    public void setRetieneTimbre(boolean retieneTimbre) {
        this.retieneTimbre = retieneTimbre;
    }

    public boolean isConsolidada() {
        return consolidada;
    }

    public void setConsolidada(boolean consolidada) {
        this.consolidada = consolidada;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getCodigoDane() {
        return codigoDane;
    }

    public void setCodigoDane(String codigoDane) {
        this.codigoDane = codigoDane;
    }

    public String getMision() {
        return mision;
    }

    public void setMision(String mision) {
        this.mision = mision;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public int getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(int tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public String getCodigosChip() {
        return codigosChip;
    }

    public void setCodigosChip(String codigosChip) {
        this.codigosChip = codigosChip;
    }

    public String getFirmaFactura() {
        return firmaFactura;
    }

    public void setFirmaFactura(String firmaFactura) {
        this.firmaFactura = firmaFactura;
    }

    public String getCodigoWeb() {
        return codigoWeb;
    }

    public void setCodigoWeb(String codigoWeb) {
        this.codigoWeb = codigoWeb;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getRutaSticker() {
        return rutaSticker;
    }

    public void setRutaSticker(String rutaSticker) {
        this.rutaSticker = rutaSticker;
    }

    public String getRutaVigiladoPor() {
        return rutaVigiladoPor;
    }

    public void setRutaVigiladoPor(String rutaVigiladoPor) {
        this.rutaVigiladoPor = rutaVigiladoPor;
    }

    /**
     * @return the nuir
     */
    public String getNuir() {
        return nuir;
    }

    /**
     * @param nuir
     * the nuir to set
     */
    public void setNuir(String nuir) {
        this.nuir = nuir;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo
     * the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoContaduria() {
        return codigoContaduria;
    }

    public void setCodigoContaduria(String codigoContaduria) {
        this.codigoContaduria = codigoContaduria;
    }
    
    

}
