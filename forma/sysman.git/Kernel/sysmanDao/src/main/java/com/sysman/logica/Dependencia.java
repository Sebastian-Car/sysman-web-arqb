/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.logica;

import java.io.Serializable;

/**
 *
 * @author cmanrique
 */
public class Dependencia implements Serializable {

	private String codigo;
	private String nombre;
	private boolean movimiento;
	private String sigla;
	private String centroDeCosto;
	private boolean comodato;
	private boolean activo;
	private boolean verBanco;

	public Dependencia(String codigo, String nombre, boolean movimiento, String sigla, String centroDeCosto,
			boolean comodato, boolean activo, boolean verBanco) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.movimiento = movimiento;
		this.sigla = sigla;
		this.centroDeCosto = centroDeCosto;
		this.comodato = comodato;
		this.activo = activo;
		this.verBanco = verBanco;
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

	public boolean isMovimiento() {
		return movimiento;
	}

	public void setMovimiento(boolean movimiento) {
		this.movimiento = movimiento;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getCentroDeCosto() {
		return centroDeCosto;
	}

	public void setCentroDeCosto(String centroDeCosto) {
		this.centroDeCosto = centroDeCosto;
	}

	public boolean isComodato() {
		return comodato;
	}

	public void setComodato(boolean comodato) {
		this.comodato = comodato;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean isVerBanco() {
		return verBanco;
	}

	public void setVerBanco(boolean verBanco) {
		this.verBanco = verBanco;
	}

}
