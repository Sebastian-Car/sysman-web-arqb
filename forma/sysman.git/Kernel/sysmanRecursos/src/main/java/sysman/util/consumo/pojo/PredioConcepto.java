package sysman.util.consumo.pojo;


import java.io.Serializable;

/**
 * Pojo que permite capturar los datos de los conceptos por vigencia del predio
 * desde el servicio expuesto en la arquitectura C y poder exponerlo en el
 * modulo de WorkFlow (Procesos Judiciales)
 * 
 * @version 1.0, 01/12/2020
 * @author Jos&eacute; Pascual G&oacute;mez Blanco
 *
 */
public class PredioConcepto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * C&oacute;digo del concepto del modulo
	 */
	private int numConcepto;
	
	/**
	 * Nombre del concepto de la vigencia
	 */
	private String nombreConcepto;
	
	/**
	 * Valor calculado del concepto
	 */
	private double valor;

	/**
	 * @return the numConcepto
	 */
	public int getNumConcepto() {
		return numConcepto;
	}

	/**
	 * @param numConcepto the numConcepto to set
	 */
	public void setNumConcepto(int numConcepto) {
		this.numConcepto = numConcepto;
	}

	/**
	 * @return the nombreConcepto
	 */
	public String getNombreConcepto() {
		return nombreConcepto;
	}

	/**
	 * @param nombreConcepto the nombreConcepto to set
	 */
	public void setNombreConcepto(String nombreConcepto) {
		this.nombreConcepto = nombreConcepto;
	}

	/**
	 * @return the valor
	 */
	public double getValor() {
		return valor;
	}

	/**
	 * @param valor the valor to set
	 */
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	
}
