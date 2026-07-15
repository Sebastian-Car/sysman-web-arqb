package com.sysman.rest.logica;


public class PqrAnexo {
	/**
	 * Parametro para el proceso
	 */
	private String anexo;
	/**
	 * Parametro que almacena num de radicado para una consulta
	 */
	private String nombre;
	/**
	 * Parámetro que establecera el tipo de proceso que se va ha realizar en el caso
	 * va a ser siempre pqr
	 */
	private String valor;
	/**
	 * @return the anexo
	 */
	public String getAnexo() {
		return anexo;
	}
	/**
	 * @param anexo the anexo to set
	 */
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}
	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}
	
}
