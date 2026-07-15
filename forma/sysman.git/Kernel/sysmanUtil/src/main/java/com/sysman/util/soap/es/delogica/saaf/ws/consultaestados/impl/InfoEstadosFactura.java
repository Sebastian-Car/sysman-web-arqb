
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para infoEstadosFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="infoEstadosFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="estadoDIAN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="estadoEnvioCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fechaAlta" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaEstadoDIAN" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaEstadoEnvioCliente" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaFactura" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="identificadorFactura" type="{http://impl.consultaestados.ws.saaf.delogica.es/}identificadorFactura" minOccurs="0"/>
 *         &lt;element name="observacionesEstadoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="observacionesEstadoDIAN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UUID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "infoEstadosFactura", propOrder = {
    "estadoDIAN",
    "estadoEnvioCliente",
    "fechaAlta",
    "fechaEstadoDIAN",
    "fechaEstadoEnvioCliente",
    "fechaFactura",
    "identificadorFactura",
    "observacionesEstadoCliente",
    "observacionesEstadoDIAN",
    "uuid"
})
public class InfoEstadosFactura {

    protected String estadoDIAN;
    protected String estadoEnvioCliente;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaAlta;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaEstadoDIAN;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaEstadoEnvioCliente;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaFactura;
    protected IdentificadorFactura identificadorFactura;
    protected String observacionesEstadoCliente;
    protected String observacionesEstadoDIAN;
    @XmlElement(name = "UUID")
    protected String uuid;

    /**
     * Obtiene el valor de la propiedad estadoDIAN.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoDIAN() {
        return estadoDIAN;
    }

    /**
     * Define el valor de la propiedad estadoDIAN.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoDIAN(String value) {
        this.estadoDIAN = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoEnvioCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoEnvioCliente() {
        return estadoEnvioCliente;
    }

    /**
     * Define el valor de la propiedad estadoEnvioCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoEnvioCliente(String value) {
        this.estadoEnvioCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaAlta.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaAlta() {
        return fechaAlta;
    }

    /**
     * Define el valor de la propiedad fechaAlta.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaAlta(XMLGregorianCalendar value) {
        this.fechaAlta = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaEstadoDIAN.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaEstadoDIAN() {
        return fechaEstadoDIAN;
    }

    /**
     * Define el valor de la propiedad fechaEstadoDIAN.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaEstadoDIAN(XMLGregorianCalendar value) {
        this.fechaEstadoDIAN = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaEstadoEnvioCliente.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaEstadoEnvioCliente() {
        return fechaEstadoEnvioCliente;
    }

    /**
     * Define el valor de la propiedad fechaEstadoEnvioCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaEstadoEnvioCliente(XMLGregorianCalendar value) {
        this.fechaEstadoEnvioCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaFactura.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaFactura() {
        return fechaFactura;
    }

    /**
     * Define el valor de la propiedad fechaFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaFactura(XMLGregorianCalendar value) {
        this.fechaFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad identificadorFactura.
     * 
     * @return
     *     possible object is
     *     {@link IdentificadorFactura }
     *     
     */
    public IdentificadorFactura getIdentificadorFactura() {
        return identificadorFactura;
    }

    /**
     * Define el valor de la propiedad identificadorFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentificadorFactura }
     *     
     */
    public void setIdentificadorFactura(IdentificadorFactura value) {
        this.identificadorFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad observacionesEstadoCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacionesEstadoCliente() {
        return observacionesEstadoCliente;
    }

    /**
     * Define el valor de la propiedad observacionesEstadoCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacionesEstadoCliente(String value) {
        this.observacionesEstadoCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad observacionesEstadoDIAN.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObservacionesEstadoDIAN() {
        return observacionesEstadoDIAN;
    }

    /**
     * Define el valor de la propiedad observacionesEstadoDIAN.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObservacionesEstadoDIAN(String value) {
        this.observacionesEstadoDIAN = value;
    }

    /**
     * Obtiene el valor de la propiedad uuid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUUID() {
        return uuid;
    }

    /**
     * Define el valor de la propiedad uuid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUUID(String value) {
        this.uuid = value;
    }

}
