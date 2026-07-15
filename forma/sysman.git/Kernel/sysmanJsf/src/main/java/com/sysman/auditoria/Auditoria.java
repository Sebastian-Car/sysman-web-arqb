package com.sysman.auditoria;

import java.util.Map;

public class Auditoria {

	private String referencia;
	private String accion;
	private Map<String, Object> valAnterior;
	private Map<String, Object> valActual;
	private String usuario;
	private String ip;
	private String equipo;
	private String fechaCreacion;
	private Entidad entidad;
	private Procesos procesosDto;

	public Auditoria() {}

	/**
	 * @return the referencia
	 */
	public String getReferencia() {
		return referencia;
	}

	/**
	 * @param referencia the referencia to set
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	/**
	 * @return the accion
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * @param accion the accion to set
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * @return the valAnterior
	 */
	public Map<String, Object> getValAnterior() {
		return valAnterior;
	}

	/**
	 * @param valAnterior the valAnterior to set
	 */
	public void setValAnterior(Map<String, Object> valAnterior) {
		this.valAnterior = valAnterior;
	}

	/**
	 * @return the valActual
	 */
	public Map<String, Object> getValActual() {
		return valActual;
	}

	/**
	 * @param valActual the valActual to set
	 */
	public void setValActual(Map<String, Object> valActual) {
		this.valActual = valActual;
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the equipo
	 */
	public String getEquipo() {
		return equipo;
	}

	/**
	 * @param equipo the equipo to set
	 */
	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	/**
	 * @return the fechaCreacion
	 */
	public String getFechaCreacion() {
		return fechaCreacion;
	}

	/**
	 * @param fechaCreacion the fechaCreacion to set
	 */
	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	/**
	 * @return the entidad
	 */
	public Entidad getEntidad() {
		return entidad;
	}

	/**
	 * @param entidad the entidad to set
	 */
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	/**
	 * @return the procesosDto
	 */
	public Procesos getProcesosDto() {
		return procesosDto;
	}

	/**
	 * @param procesosDto the procesosDto to set
	 */
	public void setProcesosDto(Procesos procesosDto) {
		this.procesosDto = procesosDto;
	}

}
