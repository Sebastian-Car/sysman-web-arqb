/*-
 * SalidaInformes4GobNar.java
 *
 * 1.0
 * 
 * 17/10/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Clase para guardar la salida del servicio SalidaInformesGobNar
 * 
 * @version 1.0, 17/10/2022
 * @author mrosero
 *
 */
public class SalidaInformes4GobNar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String codigo;
	private String nombre;
	private String destino;
	private String naturaleza;
	private String movimiento;
	private String tipovigencia;
	private String sector;
	private String programa;
	private String subPrograma;
	private String codigoProducto;
	private String codigoBPIN;
	private String codigoCCPET;
	private String codigoCPCDANE;
	private String codigoUnidadEjecutora;
	private String codigoFuente;
	private String codigoCCPETRegalias;
	private String politicaPublica;
	private String detalleSectorial;
	private String tipoRecurso;
	private String codigoSIA;
	private String dependencia;
	private String nombreDependencia;
	private String codigo_equiv;  //JM CC 3155



	public SalidaInformes4GobNar() {

	}


	/**
	 * @return 
	 * 
	 */


	public SalidaInformes4GobNar(String naturaleza, String movimiento,String tipovigencia, String sector, String programa,
			String subPrograma, String codigoProducto, String codigoBPIN, String codigoCCPET, String codigoCPCDANE,
			String codigoUnidadEjecutora, String codigoFuente, String codigoCCPETRegalias, String politicaPublica,
			String detalleSectorial, String tipoRecurso, String codigoSIA, String destino, String nombre,
			String codigo,String dependencia, String nombreDependencia, String codigo_equiv) {

		this.codigo = codigo;
		this.nombre = nombre;
		this.destino = destino;
		this.naturaleza = naturaleza;
		this.movimiento = movimiento;
		this.tipovigencia = tipovigencia;
		this.sector = sector;
		this.programa = programa;
		this.subPrograma = subPrograma;
		this.codigoProducto = codigoProducto;
		this.codigoBPIN = codigoBPIN;
		this.codigoCCPET = codigoCCPET;
		this.codigoCPCDANE = codigoCPCDANE;
		this.codigoUnidadEjecutora = codigoUnidadEjecutora;
		this.codigoFuente = codigoFuente;
		this.codigoCCPETRegalias = codigoCCPETRegalias;
		this.politicaPublica = politicaPublica;
		this.detalleSectorial = detalleSectorial;
		this.tipoRecurso = tipoRecurso;
		this.codigoSIA = codigoSIA;
		this.dependencia = dependencia;
		this.nombreDependencia = nombreDependencia;
		this.codigo_equiv = codigo_equiv;  //JM CC 3155

	}


	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getDestino() {
		return destino;
	}


	public void setDestino(String destino) {
		this.destino = destino;
	}


	public String getNaturaleza() {
		return naturaleza;
	}


	public void setNaturaleza(String naturaleza) {
		this.naturaleza = naturaleza;
	}


	public String getMovimiento() {
		return movimiento;
	}


	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}


	public String getTipovigencia() {
		return tipovigencia;
	}


	public void setTipovigencia(String tipovigencia) {
		this.tipovigencia = tipovigencia;
	}


	public String getSector() {
		return sector;
	}


	public void setSector(String sector) {
		this.sector = sector;
	}


	public String getPrograma() {
		return programa;
	}


	public void setPrograma(String programa) {
		this.programa = programa;
	}


	public String getSubPrograma() {
		return subPrograma;
	}


	public void setSubPrograma(String subPrograma) {
		this.subPrograma = subPrograma;
	}


	public String getCodigoProducto() {
		return codigoProducto;
	}


	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}


	public String getCodigoBPIN() {
		return codigoBPIN;
	}


	public void setCodigoBPIN(String codigoBPIN) {
		this.codigoBPIN = codigoBPIN;
	}


	public String getCodigoCCPET() {
		return codigoCCPET;
	}


	public void setCodigoCCPET(String codigoCCPET) {
		this.codigoCCPET = codigoCCPET;
	}


	public String getCodigoCPCDANE() {
		return codigoCPCDANE;
	}


	public void setCodigoCPCDANE(String codigoCPCDANE) {
		this.codigoCPCDANE = codigoCPCDANE;
	}


	public String getCodigoUnidadEjecutora() {
		return codigoUnidadEjecutora;
	}


	public void setCodigoUnidadEjecutora(String codigoUnidadEjecutora) {
		this.codigoUnidadEjecutora = codigoUnidadEjecutora;
	}


	public String getCodigoFuente() {
		return codigoFuente;
	}


	public void setCodigoFuente(String codigoFuente) {
		this.codigoFuente = codigoFuente;
	}


	public String getCodigoCCPETRegalias() {
		return codigoCCPETRegalias;
	}


	public void setCodigoCCPETRegalias(String codigoCCPETRegalias) {
		this.codigoCCPETRegalias = codigoCCPETRegalias;
	}


	public String getPoliticaPublica() {
		return politicaPublica;
	}


	public void setPoliticaPublica(String politicaPublica) {
		this.politicaPublica = politicaPublica;
	}


	public String getDetalleSectorial() {
		return detalleSectorial;
	}


	public void setDetalleSectorial(String detalleSectorial) {
		this.detalleSectorial = detalleSectorial;
	}


	public String getTipoRecurso() {
		return tipoRecurso;
	}


	public void setTipoRecurso(String tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}


	public String getCodigoSIA() {
		return codigoSIA;
	}


	public void setCodigoSIA(String codigoSIA) {
		this.codigoSIA = codigoSIA;
	}


	public String getDependencia() {
		return dependencia;
	}


	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}


	public String getNombreDependencia() {
		return nombreDependencia;
	}


	public void setNombreDependencia(String nombreDependencia) {
		this.nombreDependencia = nombreDependencia;
	}
	
	public String getCodigoEquiv() {
		return codigo_equiv;
	}


	public void setCodigoEquiv(String codigo_equiv) {
		this.codigo_equiv = codigo_equiv;
	}
	

}
