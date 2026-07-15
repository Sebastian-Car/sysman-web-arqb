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
public class Formulario implements Serializable {

	private String ruta;
	private boolean modal;
	private int anchoModal;
	private int altoModal;
	private boolean[] permisos;

	public Formulario(String ruta, boolean modal, boolean[] permisos) {
		this.ruta = ruta;
		this.modal = modal;
		this.permisos = permisos;
	}

	public Formulario(String ruta, boolean modal, int anchoModal, boolean[] permisos) {
		this.ruta = ruta;
		this.modal = modal;
		this.permisos = permisos;
		this.anchoModal = anchoModal;
	}

	public Formulario(String ruta, boolean modal, int anchoModal, int altoModal, boolean[] permisos) {
		this.ruta = ruta;
		this.modal = modal;
		this.permisos = permisos;
		this.anchoModal = anchoModal;
		this.altoModal = altoModal;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public boolean[] getPermisos() {
		return permisos;
	}

	public void setPermisos(boolean[] permisos) {
		this.permisos = permisos;
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean modal) {
		this.modal = modal;
	}

	public int getAnchoModal() {
		return anchoModal;
	}

	public void setAnchoModal(int anchoModal) {
		this.anchoModal = anchoModal;
	}

	public int getAltoModal() {
		return altoModal;
	}

	public void setAltoModal(int altoModal) {
		this.altoModal = altoModal;
	}

}
