/*
* Parametro
*
* 1.0
*
* 12/08/2016
*
* Copyright Stefanini Sysman
*/
package com.sysman.kernel.api.commons.beans;
 
/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase Java Beans que permite guardar informacion de parametros a enviar entre capas.
 */
public class Parametro {
	
	private String nombre;
	private Object valor;
	private Class tipo;	
	
	public Parametro() {

	}

	public Parametro(String nombre, Object valor, Class tipo) {
		super();
		this.nombre = nombre;
		this.valor = valor;
		this.tipo = tipo;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Object getValor() {
		return valor;
	}
	
	public void setValor(Object valor) {
		this.valor = valor;
	}
	
	public Class getTipo() {
		return tipo;
	}
	
	public void setTipo(Class tipo) {
		this.tipo = tipo;
	}
}
