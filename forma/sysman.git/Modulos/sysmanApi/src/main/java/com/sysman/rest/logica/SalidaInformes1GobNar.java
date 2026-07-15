/*-
 * SalidaInformes1GobNar.java
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
public class SalidaInformes1GobNar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String codigocuenta;
	private String nombrerubro;
	private String movimiento;
	private String destino;
	private String bpid;
	private String apropiacioninicial;
	private String adicion;
	private String reduccion;
	private String credito;
	private String contracredito;
	private String aplazamiento;
	private String desplazaminento;
	private String apropiacionvigente;
	private String disponibilidades;
	private String saldodisponible;
	private String compromisos;
	private String disponibilidadesabiertas;
	private String obligacion;
	private String pagos;
	private String obligacionesporpagar;

	public SalidaInformes1GobNar() {

	}

	/**
	 * @return
	 * 
	 */

	public SalidaInformes1GobNar(String codigocuenta, String nombrerubro, String movimiento, String destino, String bpid,
			String apropiacioninicial, String adicion, String reduccion, String credito, String contracredito, String aplazamiento,
			String desplazaminento, String apropiacionvigente, String disponibilidades, String saldodisponible,
			String compromisos, String disponibilidadesabiertas, String obligacion, String pagos, String obligacionesporpagar) {

		this.codigocuenta = codigocuenta;
		this.nombrerubro = nombrerubro;
		this.movimiento = movimiento;
		this.destino = destino;
		this.bpid = bpid;
		this.apropiacioninicial = apropiacioninicial;
		this.adicion = adicion;
		this.reduccion = reduccion;
		this.credito = credito;
		this.contracredito = contracredito;
		this.aplazamiento = aplazamiento;
		this.desplazaminento = desplazaminento;
		this.apropiacionvigente = apropiacionvigente;
		this.disponibilidades = disponibilidades;
		this.saldodisponible = saldodisponible;
		this.compromisos = compromisos;
		this.disponibilidadesabiertas = disponibilidadesabiertas;
		this.obligacion = obligacion;
		this.pagos = pagos;
		this.obligacionesporpagar = obligacionesporpagar;
	}

	public String getCodigocuenta() {
		return codigocuenta;
	}

	public void setCodigocuenta(String codigocuenta) {
		this.codigocuenta = codigocuenta;
	}

	public String getNombrerubro() {
		return nombrerubro;
	}

	public void setNombrerubro(String nombrerubro) {
		this.nombrerubro = nombrerubro;
	}

	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getBpid() {
		return bpid;
	}

	public void setBpid(String bpid) {
		this.bpid = bpid;
	}

	public String getApropiacioninicial() {
		
		return apropiacioninicial;
	}

	public void setApropiacioninicial(String apropiacioninicial) {
		this.apropiacioninicial = apropiacioninicial;
	}

	public String getAdicion() {
		return adicion;
	}

	public void setAdicion(String adicion) {
		this.adicion = adicion;
	}

	public String getReduccion() {
		return reduccion;
	}

	public void setReduccion(String reduccion) {
		this.reduccion = reduccion;
	}

	public String getCredito() {
		return credito;
	}

	public void setCredito(String credito) {
		this.credito = credito;
	}

	public String getContracredito() {
		return contracredito;
	}

	public void setContracredito(String contracredito) {
		this.contracredito = contracredito;
	}

	public String getAplazamiento() {
		return aplazamiento;
	}

	public void setAplazamiento(String aplazamiento) {
		this.aplazamiento = aplazamiento;
	}

	public String getDesplazaminento() {
		return desplazaminento;
	}

	public void setDesplazaminento(String desplazaminento) {
		this.desplazaminento = desplazaminento;
	}

	public String getApropiacionvigente() {
		return apropiacionvigente;
	}

	public void setApropiacionvigente(String apropiacionvigente) {
		this.apropiacionvigente = apropiacionvigente;
	}

	public String getDisponibilidades() {
		return disponibilidades;
	}

	public void setDisponibilidades(String disponibilidades) {
		this.disponibilidades = disponibilidades;
	}

	public String getSaldodisponible() {
		return saldodisponible;
	}

	public void setSaldodisponible(String saldodisponible) {
		this.saldodisponible = saldodisponible;
	}

	public String getCompromisos() {
		return compromisos;
	}

	public void setCompromisos(String compromisos) {
		this.compromisos = compromisos;
	}

	public String getDisponibilidadesabiertas() {
		return disponibilidadesabiertas;
	}

	public void setDisponibilidadesabiertas(String disponibilidadesabiertas) {
		this.disponibilidadesabiertas = disponibilidadesabiertas;
	}

	public String getObligacion() {
		return obligacion;
	}

	public void setObligacion(String obligacion) {
		this.obligacion = obligacion;
	}

	public String getPagos() {
		return pagos;
	}

	public void setPagos(String pagos) {
		this.pagos = pagos;
	}

	public String getObligacionesporpagar() {
		return obligacionesporpagar;
	}

	public void setObligacionesporpagar(String obligacionesporpagar) {
		this.obligacionesporpagar = obligacionesporpagar;
	}

}
