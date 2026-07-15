package com.sysman.rest.logica;

import com.sysman.rest.enums.EnumAuxiliaresVarios;

import java.io.Serializable;

/**
 * Pojo requerido para obtener los datos requeridos del Comprobante
 * 
 * @version 1.0, 25/01/2020
 * @author eamaya
 * 
 */

public class ParametrosComprobanteCnt implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String compania;

    private int ano;

    private String tipo;

    private String numero;

    private String fecha;

    private String tercero = EnumAuxiliaresVarios.TERCERO.getValue();

    private String sucursal = EnumAuxiliaresVarios.SUCURSAL.getValue();

    private String descripcion;

    private String fechaVcnDoc;

    private String texto;

    private double vlrDocumento;

    private double debito;

    private double credito;

    private double vlrAGirar;

    private String createdBy;

    private String dateCreated;

    private String modifiedBy;

    private String dateModified;
    
    private String nro_documento;
    
    private String fechapagadogn;

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaVcnDoc() {
        return fechaVcnDoc;
    }

    public void setFechaVcnDoc(String fechaVcnDoc) {
        this.fechaVcnDoc = fechaVcnDoc;
    }

    public double getVlrDocumento() {
        return vlrDocumento;
    }

    public void setVlrDocumento(double vlrDocumento) {
        this.vlrDocumento = vlrDocumento;
    }

    public double getDebito() {
        return debito;
    }

    public void setDebito(double debito) {
        this.debito = debito;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public double getVlrAGirar() {
        return vlrAGirar;
    }

    public void setVlrAGirar(double vlrAGirar) {
        this.vlrAGirar = vlrAGirar;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
    
    public String getFechapagadogn() {
        return fechapagadogn;
    }

    public void setFechapagadogn(String fechapagadogn) {
        this.fechapagadogn = fechapagadogn;
    }
    
    public String getNro_documento() {
        return nro_documento;
    }

    public void setNro_documento(String nro_documento) {
        this.nro_documento = nro_documento;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

}
