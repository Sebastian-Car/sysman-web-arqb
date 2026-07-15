package com.sysman.rest.logica;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RespuestaDatosGeneral {

	private String compania;
	private String anio;
	private String codigo;
	private String nombre;
		
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCompania() {
		return compania;
	}
	public void setCompania(String compania) {
		this.compania = compania;
	}
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}	
}
