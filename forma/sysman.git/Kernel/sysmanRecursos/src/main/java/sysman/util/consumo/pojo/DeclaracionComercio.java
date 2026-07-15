package sysman.util.consumo.pojo;

import java.io.Serializable;
import java.util.List;

public class DeclaracionComercio implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * registra la vigencia a la cual pertenecen los datos del facturado
	 */
	private int vigencias;

	/**
	 * valor del avaluo para la vigencia del facturado
	 */
	private int periodo;

	/**
	 * registra el porcentaje de la tarifa sobre el cual se calculo
	 */
	private String tipoDeclaracion;

	/**
	 * C&oacute;digo de la tarifa sobre la cual se calculo
	 */
	private String consecutivo;
	
	/**
	 * Listado de conceptos que se tienen por cada vigencia
	 */
	private List<ComercioConcepto> conceptos;


	/**
	 * @return the vigencias
	 */
	public int getVigencias() {
		return vigencias;
	}

	/**
	 * @param vigencias the vigencias to set
	 */
	public void setVigencias(int vigencias) {
		this.vigencias = vigencias;
	}

	/**
	 * @return the periodo
	 */
	public int getPeriodo() {
		return periodo;
	}

	/**
	 * @param periodo the periodo to set
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/**
	 * @return the tipoDeclaracion
	 */
	public String getTipoDeclaracion() {
		return tipoDeclaracion;
	}

	/**
	 * @param tipoDeclaracion the tipoDeclaracion to set
	 */
	public void setTipoDeclaracion(String tipoDeclaracion) {
		this.tipoDeclaracion = tipoDeclaracion;
	}

	/**
	 * @return the consecutivo
	 */
	public String getConsecutivo() {
		return consecutivo;
	}

	/**
	 * @param consecutivo the consecutivo to set
	 */
	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	/**
	 * @return the conceptos
	 */
	public List<ComercioConcepto> getConceptos() {
		return conceptos;
	}

	/**
	 * @param conceptos the conceptos to set
	 */
	public void setConceptos(List<ComercioConcepto> conceptos) {
		this.conceptos = conceptos;
	}

	


}
