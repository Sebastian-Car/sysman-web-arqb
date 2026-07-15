
package com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para consultaEstadosFicherosYFacturasResponse complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="consultaEstadosFicherosYFacturasResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://impl.consultaestados.ws.saaf.delogica.es/}generalResponse">
 *       &lt;sequence>
 *         &lt;element name="infoEstadosFacturas" type="{http://impl.consultaestados.ws.saaf.delogica.es/}infoEstadosFicheroYFactura" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "consultaEstadosFicherosYFacturasResponse", propOrder = {
    "infoEstadosFacturas"
})
public class ConsultaEstadosFicherosYFacturasResponse
    extends GeneralResponse
{

    @XmlElement(nillable = true)
    protected List<InfoEstadosFicheroYFactura> infoEstadosFacturas;

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
     * {@link InfoEstadosFicheroYFactura }
     * 
     * 
     */
    public List<InfoEstadosFicheroYFactura> getInfoEstadosFacturas() {
        if (infoEstadosFacturas == null) {
            infoEstadosFacturas = new ArrayList<InfoEstadosFicheroYFactura>();
        }
        return this.infoEstadosFacturas;
    }

}
