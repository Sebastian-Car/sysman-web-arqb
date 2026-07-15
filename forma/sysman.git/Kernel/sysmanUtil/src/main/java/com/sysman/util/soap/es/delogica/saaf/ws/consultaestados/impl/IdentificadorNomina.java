package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Clase Java para identificadorNomina complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="identificadorNomina">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="anyo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idFiscalEmisor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idFiscalTrabajador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoNomina" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="trackID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "identificadorNomina", propOrder = { "anyo", "idFiscalEmisor", "idFiscalTrabajador", "mes", "numero",
		"tipoNomina", "trackID" })
public class IdentificadorNomina {

	protected String anyo;
	protected String idFiscalEmisor;
	protected String idFiscalTrabajador;
	protected String mes;
	protected String numero;
	protected String tipoNomina;
	protected String trackID;

	/**
	 * Obtiene el valor de la propiedad anyo.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getAnyo() {
		return anyo;
	}

	/**
	 * Define el valor de la propiedad anyo.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setAnyo(String value) {
		this.anyo = value;
	}

	/**
	 * Obtiene el valor de la propiedad idFiscalEmisor.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdFiscalEmisor() {
		return idFiscalEmisor;
	}

	/**
	 * Define el valor de la propiedad idFiscalEmisor.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setIdFiscalEmisor(String value) {
		this.idFiscalEmisor = value;
	}

	/**
	 * Obtiene el valor de la propiedad idFiscalTrabajador.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIdFiscalTrabajador() {
		return idFiscalTrabajador;
	}

	/**
	 * Define el valor de la propiedad idFiscalTrabajador.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setIdFiscalTrabajador(String value) {
		this.idFiscalTrabajador = value;
	}

	/**
	 * Obtiene el valor de la propiedad mes.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Define el valor de la propiedad mes.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setMes(String value) {
		this.mes = value;
	}

	/**
	 * Obtiene el valor de la propiedad numero.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getNumero() {
		return numero;
	}

	/**
	 * Define el valor de la propiedad numero.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setNumero(String value) {
		this.numero = value;
	}

	/**
	 * Obtiene el valor de la propiedad tipoNomina.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTipoNomina() {
		return tipoNomina;
	}

	/**
	 * Define el valor de la propiedad tipoNomina.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setTipoNomina(String value) {
		this.tipoNomina = value;
	}

	/**
	 * Obtiene el valor de la propiedad trackID.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTrackID() {
		return trackID;
	}

	/**
	 * Define el valor de la propiedad trackID.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setTrackID(String value) {
		this.trackID = value;
	}

}
