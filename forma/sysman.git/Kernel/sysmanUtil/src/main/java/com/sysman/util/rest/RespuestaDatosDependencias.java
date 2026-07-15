package com.sysman.util.rest;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RespuestaDatosDependencias {

	private String codigo;
	private String nombreDep;
	private String movimenito;
	private String centroCosto;
	private String codigCentroCosto;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getNombreDep() {
		return nombreDep;
	}
	public void setNombreDep(String nombreDep) {
		this.nombreDep = nombreDep;
	}
	public String getMovimenito() {
		return movimenito;
	}
	public void setMovimenito(String movimenito) {
		this.movimenito = movimenito;
	}
	public String getCentroCosto() {
		return centroCosto;
	}
	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}
	public String getCodigCentroCosto() {
		return codigCentroCosto;
	}
	public void setCodigCentroCosto(String codigCentroCosto) {
		this.codigCentroCosto = codigCentroCosto;
	}
}
