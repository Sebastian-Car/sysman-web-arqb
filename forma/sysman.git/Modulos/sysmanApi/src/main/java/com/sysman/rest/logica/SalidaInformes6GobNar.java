package com.sysman.rest.logica;

import java.io.Serializable;

/**
 * Clase para guardar la salida del servicio SalidaInformesGobNar
 * 
 * @version 1.0, 13/08/2024
 * @author
 *
 */
public class SalidaInformes6GobNar implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String compania;
	private String cuenta;
	private String codigo;
	private String nombre;
	private String movimiento;
	private String tipoRecurso;
	private String fuenteRecurso;
	private String apropiado;
	private String modificaciones;
	private String totalPresupuesto;
	private String recaudosAnteriores;
	private String recaudosMes;
	private String recaudosAcumulados;
	private String porRecaudar;
	private String porcRecaudado;
	
	public SalidaInformes6GobNar() {
		
	}
	
	public SalidaInformes6GobNar(String compania, String cuenta, String codigo, String nombre, String movimiento, String tipoRecurso, String fuenteRecurso, String apropiado, String modificaciones, String totalPresupuesto, String recaudosAnteriores, String recaudosMes, String recaudosAcumulados, String porRecaudar, String porcRecaudado) {
		this.compania = compania;
        this.cuenta = cuenta;
        this.codigo = codigo;
        this.nombre = nombre;
        this.movimiento = movimiento;
        this.tipoRecurso = tipoRecurso;
        this.fuenteRecurso = fuenteRecurso;
        this.apropiado = apropiado;
        this.modificaciones = modificaciones;
        this.totalPresupuesto = totalPresupuesto;
        this.recaudosAnteriores = recaudosAnteriores;
        this.recaudosMes = recaudosMes;
        this.recaudosAcumulados = recaudosAcumulados;
        this.porRecaudar = porRecaudar;
        this.porcRecaudado = porcRecaudado;
	}

	public String getCompania() {
		return compania;
	}

	public void setCompania(String compania) {
		this.compania = compania;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
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

	public String getMovimiento() {
		return movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	public String getTipoRecurso() {
		return tipoRecurso;
	}

	public void setTipoRecurso(String tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}

	public String getFuenteRecurso() {
		return fuenteRecurso;
	}

	public void setFuenteRecurso(String fuenteRecurso) {
		this.fuenteRecurso = fuenteRecurso;
	}

	public String getApropiado() {
		return apropiado;
	}

	public void setApropiado(String apropiado) {
		this.apropiado = apropiado;
	}

	public String getModificaciones() {
		return modificaciones;
	}

	public void setModificaciones(String modificaciones) {
		this.modificaciones = modificaciones;
	}

	public String getTotalPresupuesto() {
		return totalPresupuesto;
	}

	public void setTotalPresupuesto(String totalPresupuesto) {
		this.totalPresupuesto = totalPresupuesto;
	}

	public String getRecaudosAnteriores() {
		return recaudosAnteriores;
	}

	public void setRecaudosAnteriores(String recaudosAnteriores) {
		this.recaudosAnteriores = recaudosAnteriores;
	}

	public String getRecaudosMes() {
		return recaudosMes;
	}

	public void setRecaudosMes(String recaudosMes) {
		this.recaudosMes = recaudosMes;
	}

	public String getRecaudosAcumulados() {
		return recaudosAcumulados;
	}

	public void setRecaudosAcumulados(String recaudosAcumulados) {
		this.recaudosAcumulados = recaudosAcumulados;
	}

	public String getPorRecaudar() {
		return porRecaudar;
	}

	public void setPorRecaudar(String porRecaudar) {
		this.porRecaudar = porRecaudar;
	}

	public String getPorcRecaudado() {
		return porcRecaudado;
	}

	public void setPorcRecaudado(String porcRecaudado) {
		this.porcRecaudado = porcRecaudado;
	}
	
}
