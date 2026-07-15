package com.sysman.rest.logica;

import java.io.Serializable;


/**
 * Pojo requerido para obtener los datos requeridos los detalles del comprobante
 * 
 * 
 * @version 1.0, 06/03/2019
 * @author  Jos&eacute; Pascual G&oacute;mez Blanco
 *
 * @param cuenta
 *            Par&aacute;metro que obtendr&aacute; el valor de la cuenta
 * @param tercero
 *            Par&aacute;metro que obtendr&aacute; el valor del tercero
 *            si no se envia por defecto se deja en VARIOS
 * @param sucursal
 *            Par&aacute;metro que obtendr&aacute; el valor de la sucursal
 *            si no se envia por defecto se deja en VARIOS
 * @param centro
 *            Par&aacute;metro que obtendr&aacute; el valor del centro de costo
 *            si no se envia por defecto se deja en VARIOS
 * @param fuenteRecurso
 *            Par&aacute;metro que obtendr&aacute; el valor de la fuente de recursos
 *            si no se envia por defecto se deja en VARIOS
 * @param auxiliar
 *            Par&aacute;metro que obtendr&aacute; el valor de la auxiliar
 *            si no se envia por defecto se deja en VARIOS
 * @param referencia
 *            Par&aacute;metro que obtendr&aacute; el valor de la referencia
 *            si no se envia por defecto se deja en VARIOS
 * @param nroDocumento
 *            Par&aacute;metro que obtendr&aacute; el valor del n&uacute;mero de documento           
 * @param descripcion
 *            Par&aacute;metro que obtendr&aacute; el valor de la descripci&oacute;n
 * @param valorDebito
 *            Par&aacute;metro que obtendr&aacute; el valor del d&eacute;bito
 * @param valorCredito
 *            Par&aacute;metro que obtendr&aacute; el valor del cr&eacute;dito            
 * @param rubro
 *            Par&aacute;metro que obtendr&aacute; de la rubro presupuestal          
 *                        
 * @return DetalleComprobante
 */
public class DetalleComprobante implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la cuenta
     */
    private String cuenta;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del tercero
     * si no se envia por defecto se deja en VARIOS
     */
    private String tercero ;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la sucursal
     * si no se envia por defecto se deja en VARIOS
     */
    private String sucursal ;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del centro de costo
     * si no se envia por defecto se deja en VARIOS
     */
    private String centroCosto;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la fuente de recursos
     * si no se envia por defecto se deja en VARIOS
     */
    private String fuenteRecurso;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la auxiliar
     * si no se envia por defecto se deja en VARIOS
     */
    private String auxiliar;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la referencia
     * si no se envia por defecto se deja en VARIOS
     */
    private String referencia;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del n&uacute;mero de documento
     */
    private String nroDocumento;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la descripci&oacute;n
     */
    private String descripcion;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del d&eacute;bito
     */
    private String valorDebito;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del credito
     */
    private String valorCredito;
    /**
     * Par&aacute;metro que obtendr&aacute; de la rubro presupuestal
     */
    private String cuentapptal;


    /**
     * M&eacute;todo Get para obtener el valor del atributo tercero
     * 
     * @return tercero
     */
    public String getTercero() {
        return tercero;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo tercero
     * 
     * @param tercero
     */

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo cuenta
     * 
     * @return cuenta
     */
    public String getCuenta() {
        return cuenta;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo cuenta
     * 
     * @param cuenta
     */

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo cuentapptal
     * 
     * @return cuentapptal
     */
    public String getCuentapptal() {
        return cuentapptal;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo cuentapptal
     * 
     * @param cuentapptal
     */
    public void setCuentapptal(String cuentapptal) {
        this.cuentapptal = cuentapptal;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo valorDebito
     * 
     * @return valorDebito
     */

    public String getValorDebito() {
        return valorDebito;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo valorDebito
     * 
     * @param valorDebito
     */
    public void setValorDebito(String valorDebito) {
        this.valorDebito = valorDebito;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo valorCredito
     * 
     * @return valorCredito
     */
    public String getValorCredito() {
        return valorCredito;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo valorCredito
     * 
     * @param valorCredito
     */
    public void setValorCredito(String valorCredito) {
        this.valorCredito = valorCredito;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo descripcion
     * 
     * @return descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo descripcion
     * 
     * @param descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * M&eacute;todo Get para obtene el valor del atributo nroDocumento
     * 
     * @return nroDocumento
     */

    public String getNroDocumento() {
        return nroDocumento;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo nroDocumento
     * 
     * @param nroDocumento
     */
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo centroCosto
     * 
     * @return centroCosto
     */

    public String getCentroCosto() {
        return centroCosto;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo centroCosto
     * 
     * @param centroCosto
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    /**
     * M&eacute;todo Get para obtener el resultado del atributo sucursal
     * 
     * @return sucursal
     */

    public String getSucursal() {
        return sucursal;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo sucursal
     * 
     * @param sucursal
     */
    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    /**
     * M&eacute;todo Get para obtener el valor del atributo auxiliar
     * 
     * @return auxiliar
     */

    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo auxiliar
     * 
     * @param auxiliar
     */

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     *  M&eacute;todo Get para obtener el valor del atributo fuenteRecurso
     *  
     * @return the fuenteRecurso
     */
    public String getFuenteRecurso() {
        return fuenteRecurso;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo fuenteRecurso
     * 
     * @param fuenteRecurso 
     */
    public void setFuenteRecurso(String fuenteRecurso) {
        this.fuenteRecurso = fuenteRecurso;
    }

    /**
     *  M&eacute;todo Get para obtener el valor del atributo referencia
     *  
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * M&eacute;todo Set para asignar un valor al atributo referencia
     * 
     * @param referencia 
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }	




}
