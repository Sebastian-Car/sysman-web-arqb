package com.sysman.util.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Servicios {
	@JsonProperty("CONSULTAS")
	private List<Consulta> consultas;

	@JsonProperty("PROCEDIMIENTOS")
	private List<Procedimiento> procedimientos;

	@JsonProperty("URGENCIAS")
	private List<Urgencia> urgencias; 

	@JsonProperty("HOSPITALIZACION")
	private List<Hospitalizacion> hospitalizacion;

	@JsonProperty("RECIENNACIDOS")
	private List<RecienNacido> recienNacidos; 

	@JsonProperty("MEDICAMENTOS")
	private List<Medicamento> medicamentos;

	@JsonProperty("OTROSSERVICIOS")
	private List<OtroServicio> otrosServicios; 

	public Servicios() {
 
	}

	/**
	 * @return the consultas
	 */
	public List<Consulta> getConsultas() {
		return consultas;
	}

	/**
	 * @param consultas the consultas to set
	 */
	public void setConsultas(List<Consulta> consultas) {
		this.consultas = consultas;
	}

	/**
	 * @return the procedimientos
	 */
	public List<Procedimiento> getProcedimientos() {
		return procedimientos;
	}

	/**
	 * @param procedimientos the procedimientos to set
	 */
	public void setProcedimientos(List<Procedimiento> procedimientos) {
		this.procedimientos = procedimientos;
	}

	/**
	 * @return the urgencias
	 */
	public List<Urgencia> getUrgencias() {
		return urgencias;
	}

	/**
	 * @param urgencias the urgencias to set
	 */
	public void setUrgencias(List<Urgencia> urgencias) {
		this.urgencias = urgencias;
	}

	/**
	 * @return the hospitalizacion
	 */
	public List<Hospitalizacion> getHospitalizacion() {
		return hospitalizacion;
	}

	/**
	 * @param hospitalizacion the hospitalizacion to set
	 */
	public void setHospitalizacion(List<Hospitalizacion> hospitalizacion) {
		this.hospitalizacion = hospitalizacion;
	}

	/**
	 * @return the recienNacidos
	 */
	public List<RecienNacido> getRecienNacidos() {
		return recienNacidos;
	}

	/**
	 * @param recienNacidos the recienNacidos to set
	 */
	public void setRecienNacidos(List<RecienNacido> recienNacidos) {
		this.recienNacidos = recienNacidos;
	}

	/**
	 * @return the medicamentos
	 */
	public List<Medicamento> getMedicamentos() {
		return medicamentos;
	}

	/**
	 * @param medicamentos the medicamentos to set
	 */
	public void setMedicamentos(List<Medicamento> medicamentos) {
		this.medicamentos = medicamentos;
	}

	/**
	 * @return the otrosServicios
	 */
	public List<OtroServicio> getOtrosServicios() {
		return otrosServicios;
	}

	/**
	 * @param otrosServicios the otrosServicios to set
	 */
	public void setOtrosServicios(List<OtroServicio> otrosServicios) {
		this.otrosServicios = otrosServicios;
	}

}
