
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaEstadosFacturasResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaEstadosFacturasResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://impl.consultaestados.ws.saaf.delogica.es/}generalResponse">
 *       &lt;sequence>
 *         &lt;element name="infoEstadosFacturas" type="{http://impl.consultaestados.ws.saaf.delogica.es/}infoEstadosFactura" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaEstadosFacturasResponse", propOrder = {
    "infoEstadosFacturas"
})
public class ConsultaEstadosFacturasResponse
    extends GeneralResponse
{

    @XmlElement(nillable = true)
    protected List<InfoEstadosFactura> infoEstadosFacturas;

    /**
     * Gets the value of the infoEstadosFacturas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the infoEstadosFacturas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfoEstadosFacturas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InfoEstadosFactura }
     * 
     * 
     */
    public List<InfoEstadosFactura> getInfoEstadosFacturas() {
        if (infoEstadosFacturas == null) {
            infoEstadosFacturas = new ArrayList<InfoEstadosFactura>();
        }
        return this.infoEstadosFacturas;
    }

}
