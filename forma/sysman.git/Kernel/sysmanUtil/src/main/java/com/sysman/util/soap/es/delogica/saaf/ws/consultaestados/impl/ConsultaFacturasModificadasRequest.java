
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para consultaFacturasModificadasRequest complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaFacturasModificadasRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fechaYHoraFin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fechaYHoraInicio" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaFacturasModificadasRequest", propOrder = {
    "fechaYHoraFin",
    "fechaYHoraInicio"
})
public class ConsultaFacturasModificadasRequest {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaYHoraFin;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fechaYHoraInicio;

    /**
     * Obtiene el valor de la propiedad fechaYHoraFin.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaYHoraFin() {
        return fechaYHoraFin;
    }

    /**
     * Define el valor de la propiedad fechaYHoraFin.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaYHoraFin(XMLGregorianCalendar value) {
        this.fechaYHoraFin = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaYHoraInicio.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFechaYHoraInicio() {
        return fechaYHoraInicio;
    }

    /**
     * Define el valor de la propiedad fechaYHoraInicio.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFechaYHoraInicio(XMLGregorianCalendar value) {
        this.fechaYHoraInicio = value;
    }

}
