package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Pojo requerido para obtener los datos requeridos para crear la factura
 * 
 * @version 1.0, 09/12/2022
 * @author cperez
 * 
 */

public class ParametrosLiquidacionFactura implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String compania;

    private int ano;
    
    private String tipoFactura;
    
    private String tercero;
    
    private String concepto;
    
    private String descripcion;

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

	public String getTipoFactura() {
		return tipoFactura;
	}

	public void setTipoFactura(String tipoFactura) {
		this.tipoFactura = tipoFactura;
	}

	public String getTercero() {
		return tercero;
	}

	public void setTercero(String tercero) {
		this.tercero = tercero;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
    
    
}
