package sysman.util.consumo.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Pojo que permite capturar los datos de las vigencias (Facturado) del predio
 * desde el servicio expuesto en la arquitectura C y poder exponerlo en el
 * modulo de WorkFlow (Procesos Judiciales)
 * 
 * @version 1.0, 01/12/2020
 * @author Jos&eacute; Pascual G&oacute;mez Blanco
 *
 */
public class PredioFacturado implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * registra la vigencia a la cual pertenecen los datos del facturado
	 */
	private int vigencia;

	/**
	 * valor del avaluo para la vigencia del facturado
	 */
	private double avaluo;

	/**
	 * registra el porcentaje de la tarifa sobre el cual se calculo
	 */
	private double porcentajeTarifa;

	/**
	 * C&oacute;digo de la tarifa sobre la cual se calculo
	 */
	private String tarifa;

	/**
	 * valor del calculo para la vigencia del facturado
	 */
	private double total;

	/**
	 * Listado de conceptos que se tienen por cada vigencia
	 */
	private List<PredioConcepto> conceptos;

	/**
	 * @return the vigencia
	 */
	public int getVigencia() {
		return vigencia;
	}

	/**
	 * @param vigencia the vigencia to set
	 */
	public void setVigencia(int vigencia) {
		this.vigencia = vigencia;
	}

	/**
	 * @return the avaluo
	 */
	public Double getAvaluo() {
		return avaluo;
	}

	/**
	 * @param avaluo the avaluo to set
	 */
	public void setAvaluo(Double avaluo) {
		this.avaluo = avaluo;
	}

	/**
	 * @return the porcentajeTarifa
	 */
	public Double getPorcentajeTarifa() {
		return porcentajeTarifa;
	}

	/**
	 * @param porcentajeTarifa the porcentajeTarifa to set
	 */
	public void setPorcentajeTarifa(Double porcentajeTarifa) {
		this.porcentajeTarifa = porcentajeTarifa;
	}

	/**
	 * @return the tarifa
	 */
	public String getTarifa() {
		return tarifa;
	}

	/**
	 * @param tarifa the tarifa to set
	 */
	public void setTarifa(String tarifa) {
		this.tarifa = tarifa;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
	}

	/**
	 * @return the conceptos
	 */
	public List<PredioConcepto> getConceptos() {
		return conceptos;
	}

	/**
	 * @param conceptos the conceptos to set
	 */
	public void setConceptos(List<PredioConcepto> conceptos) {
		this.conceptos = conceptos;
	}

}
