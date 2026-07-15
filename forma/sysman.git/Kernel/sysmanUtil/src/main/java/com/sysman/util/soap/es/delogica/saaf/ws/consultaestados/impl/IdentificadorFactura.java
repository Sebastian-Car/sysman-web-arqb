
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para identificadorFactura complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="identificadorFactura">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="anyo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idFiscalEmisor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="numeroDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tipoDocumento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "identificadorFactura", propOrder = {
    "anyo",
    "idFiscalEmisor",
    "numeroDocumento",
    "tipoDocumento"
})
public class IdentificadorFactura {

    protected String anyo;
    protected String idFiscalEmisor;
    protected String numeroDocumento;
    protected String tipoDocumento;

    /**
     * Obtiene el valor de la propiedad anyo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnyo() {
        return anyo;
    }

    /**
     * Define el valor de la propiedad anyo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnyo(String value) {
        this.anyo = value;
    }

    /**
     * Obtiene el valor de la propiedad idFiscalEmisor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdFiscalEmisor() {
        return idFiscalEmisor;
    }

    /**
     * Define el valor de la propiedad idFiscalEmisor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdFiscalEmisor(String value) {
        this.idFiscalEmisor = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Define el valor de la propiedad numeroDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroDocumento(String value) {
        this.numeroDocumento = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoDocumento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Define el valor de la propiedad tipoDocumento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoDocumento(String value) {
        this.tipoDocumento = value;
    }

}
