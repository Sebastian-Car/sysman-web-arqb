package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Clase Java para infoEstadosNomina complex type.
 * 
 * <p>
 * El siguiente fragmento de esquema especifica el contenido que se espera que
 * haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="infoEstadosNomina">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CUNE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="codigoInternoTrabajador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estadoDIAN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaAlta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaEstadoDIAN" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaFinLiq" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaIniLiq" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="identificadorNomina" type="{http://impl.consultaestados.ws.saaf.delogica.es/}identificadorNomina" minOccurs="0"/>
 *         &lt;element name="observacionesEstadoDIAN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoEstadosNomina", propOrder = { "cune", "codigoInternoTrabajador", "estadoDIAN", "fechaAlta",
		"fechaEstadoDIAN", "fechaFinLiq", "fechaIniLiq", "identificadorNomina", "observacionesEstadoDIAN" })
@XmlSeeAlso({ InfoEstadosFicheroYNomina.class })
public class InfoEstadosNomina {

	@XmlElement(name = "CUNE")
	protected String cune;
	protected String codigoInternoTrabajador;
	protected String estadoDIAN;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar fechaAlta;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar fechaEstadoDIAN;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar fechaFinLiq;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar fechaIniLiq;
	protected IdentificadorNomina identificadorNomina;
	protected String observacionesEstadoDIAN;

	/**
	 * Obtiene el valor de la propiedad cune.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCUNE() {
		return cune;
	}

	/**
	 * Define el valor de la propiedad cune.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setCUNE(String value) {
		this.cune = value;
	}

	/**
	 * Obtiene el valor de la propiedad codigoInternoTrabajador.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodigoInternoTrabajador() {
		return codigoInternoTrabajador;
	}

	/**
	 * Define el valor de la propiedad codigoInternoTrabajador.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setCodigoInternoTrabajador(String value) {
		this.codigoInternoTrabajador = value;
	}

	/**
	 * Obtiene el valor de la propiedad estadoDIAN.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEstadoDIAN() {
		return estadoDIAN;
	}

	/**
	 * Define el valor de la propiedad estadoDIAN.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setEstadoDIAN(String value) {
		this.estadoDIAN = value;
	}

	/**
	 * Obtiene el valor de la propiedad fechaAlta.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getFechaAlta() {
		return fechaAlta;
	}

	/**
	 * Define el valor de la propiedad fechaAlta.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setFechaAlta(XMLGregorianCalendar value) {
		this.fechaAlta = value;
	}

	/**
	 * Obtiene el valor de la propiedad fechaEstadoDIAN.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getFechaEstadoDIAN() {
		return fechaEstadoDIAN;
	}

	/**
	 * Define el valor de la propiedad fechaEstadoDIAN.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setFechaEstadoDIAN(XMLGregorianCalendar value) {
		this.fechaEstadoDIAN = value;
	}

	/**
	 * Obtiene el valor de la propiedad fechaFinLiq.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getFechaFinLiq() {
		return fechaFinLiq;
	}

	/**
	 * Define el valor de la propiedad fechaFinLiq.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setFechaFinLiq(XMLGregorianCalendar value) {
		this.fechaFinLiq = value;
	}

	/**
	 * Obtiene el valor de la propiedad fechaIniLiq.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getFechaIniLiq() {
		return fechaIniLiq;
	}

	/**
	 * Define el valor de la propiedad fechaIniLiq.
	 * 
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setFechaIniLiq(XMLGregorianCalendar value) {
		this.fechaIniLiq = value;
	}

	/**
	 * Obtiene el valor de la propiedad identificadorNomina.
	 * 
	 * @return possible object is {@link IdentificadorNomina }
	 * 
	 */
	public IdentificadorNomina getIdentificadorNomina() {
		return identificadorNomina;
	}

	/**
	 * Define el valor de la propiedad identificadorNomina.
	 * 
	 * @param value allowed object is {@link IdentificadorNomina }
	 * 
	 */
	public void setIdentificadorNomina(IdentificadorNomina value) {
		this.identificadorNomina = value;
	}

	/**
	 * Obtiene el valor de la propiedad observacionesEstadoDIAN.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getObservacionesEstadoDIAN() {
		return observacionesEstadoDIAN;
	}

	/**
	 * Define el valor de la propiedad observacionesEstadoDIAN.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setObservacionesEstadoDIAN(String value) {
		this.observacionesEstadoDIAN = value;
	}

}
