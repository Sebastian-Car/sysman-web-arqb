package com.sysman.rest.logica;

import java.io.Serializable;
import java.util.List;

import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.rest.enums.EnumAuxiliaresVarios;


/**
 * Pojo requerido para obtener los datos requeridos del Comprobante
 * 
 * @version 1.0, 04/03/2019
 * @author  Jos&eacute; Pascual G&oacute;mez Blanco
 *
 * @param compania
 *            Par&aacute;metro que obtendr&aacute; el valor de la compañia
 * @param tipo
 *            Par&aacute;metro que obtendr&aacute; el valor del tipo del comprobante            
 * @param numero
 *            Par&aacute;metro que obtendr&aacute; el valor de n&uacute;mero del comprobante
 * @param ano
 *            Par&aacute;metro que obtendr&aacute; el valor del año           
 * @param fecha
 *            Par&aacute;metro que obtendr&aacute; el valor de la fecha
 * @param tercero
 *            Par&aacute;metro que obtendr&aacute; el valor del tercero
 *            si no se envia por defecto se deja en VARIOS
 * @param sucursal
 *            Par&aacute;metro que obtendr&aacute; el valor de la sucursal
 *            si no se envia por defecto se deja en VARIOS
 * @param centroCosto
 *            Par&aacute;metro que obtendr&aacute; el valor del centro de costo
 *            si no se envia por defecto se deja en VARIOS
 * @param auxiliar
 *            Par&aacute;metro que obtendr&aacute; el valor del auxiliar
 *            si no se envia por defecto se deja en VARIOS           
 * @param referencia
 *            Par&aacute;metro que obtendr&aacute; el valor de la referencia
 *            si no se envia por defecto se deja en VARIOS                                    
 * @param fuenteRecurso
 *            Par&aacute;metro que obtendr&aacute; el valor de la fuente de recursos
 *            si no se envia por defecto se deja en VARIOS                                    
 * @param descripcion
 *            Par&aacute;metro que obtendr&aacute; el valor de la descripci&oacute;n
 * @param texto
 * 			  Es el texto que se registra a nivel del detalle                                                  
 * @param simplifica
 *            Par&aacute;metro que indica si la contabilizaci&oacute;n se realiza 
 *            simplificandola                              
 * @param omitirPptal
 *            Par&aacute;metro que indica si la contabilizaci&oacute;n crea el 
 *            comprobante presupuestal equivalente                                    
 * @param conciliar
 *            Par&aacute;metro que indica si la contabilizaci&oacute;n se realiza 
 *            concilia, es decir las cuentas de banco quedan conciliadas
 * @param noNetea
 *            Par&aacute;metro que indica si la contabilizaci&oacute;n se realiza 
 *            netando o no; las cuentas en su d&eacute;bito y cr&eacute;dito        
 * @param contratista
 *            Par&aacute;metro que indica si en contabilizaci&oacute;n se
 *            reportan los datos del contratista                                                   
 * @param tipoContrato
 *            Si el parametro contratista es true; en este par&aacute;metro 
 *            se reportara el tipo de contrato que se quiere pagar
 * @param contrato
 *            Si el parametro contratista es true; en este par&aacute;metro 
 *            se reportara el n&uacute;mero de contrato que se quiere pagar           
 * @param nroDocumento
 *            Par&aacute;metro que obtendr&aacute; el valor del n&uacute;mero de documento                                                                
 * @param almDep
 *            Par&aacute;metro que indica si a la contabilizaci&oacute;n se envia la 
 *            dependencia desde almacen          
 * @param respetaTercero
 *            Par&aacute;metro que indica si la contabilizaci&oacute;n respeta el tercero 
 *            enviado por detalle o se toma el VARIOS para simplificar    
 * @param respetaAuxiliar
 *            Par&aacute;metro que indica si la contabilizaci&oacute;n respeta el auxiliar 
 *            enviado por detalle o se toma el VARIOS para simplificar  
 * @param reemplaza
 * 			  Par&aacute;metro que indica si la contabilizaci&oacute;n reemplza el 
 * 			  comprobante si este ya existe en la base de datos	 	
 * @param usuario
 *            Par&aacute;metro que identifica el usuario que realiza la contabilizaci&oacute;n   
 * @param nitEntidad
 *            Parámetro que obtendrá obtendra el nit de la entidad para validar
 *            que se envie a la entidad correcta          
 * @return Comprobante
 */


public class Comprobante implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Constructor vacio
     */
    public Comprobante() {

    }
    /**
     * @param nitEntidad
     * @param compania
     * @param tipo
     * @param numero
     * @param ano
     * @param formato
     */
    public Comprobante(long nitEntidad, String compania, String tipo,
        String numero, int ano, FORMATOS formato) {
        this.nitEntidad = nitEntidad;
        this.compania = compania;
        this.tipo = tipo;
        this.numero = numero;
        this.ano = ano;
        this.formato = formato;
    }
    /**
     * Parámetro que obtendrá obtendra el nit de la entidad para validar
     * que se envie a la entidad correcta
     */
    private long nitEntidad;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la compañia
     */
    private String compania;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del tipo del comprobante
     */
    private String tipo;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de n&uacute;mero del comprobante
     */
    private String numero;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del año 
     */
    private int ano;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la fecha
     */
    private String fecha;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del tercero
     * si no se envia por defecto se deja en VARIOS
     */
    private String tercero = EnumAuxiliaresVarios.TERCERO.getValue();
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la sucursal
     * si no se envia por defecto se deja en VARIOS
     */
    private String sucursal = EnumAuxiliaresVarios.SUCURSAL.getValue();
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del centro de costo
     * si no se envia por defecto se deja en VARIOS
     */
    private String centroCosto = EnumAuxiliaresVarios.CENTROCOSTO.getValue();
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la auxiliar
     * si no se envia por defecto se deja en VARIOS
     */
    private String auxiliar = EnumAuxiliaresVarios.AUXILIAR.getValue();
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la referencia
     * si no se envia por defecto se deja en VARIOS
     */
    private String referencia= EnumAuxiliaresVarios.REFERENCIA.getValue();
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la fuente de recursos
     * si no se envia por defecto se deja en VARIOS
     */
    private String fuenteRecurso= EnumAuxiliaresVarios.FUENTERECURSO.getValue();
    /**
     * Par&aacute;metro que obtendr&aacute; el valor de la descripci&oacute;n
     */
    private String descripcion;
    /**
     * Es el texto que se registra a nivel del detalle
     */
    private String texto ="";
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n se realiza 
     * simplificandola 
     */
    private boolean simplifica = false;
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n crea el 
     * comprobante presupuestal equivalente 
     */
    private boolean omitirPptal = false;
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n se realiza 
     * concilia, es decir las cuentas de banco quedan conciliadas
     */
    private boolean concilia = false;
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n se realiza 
     * netando o no; las cuentas en su d&eacute;bito y cr&eacute;dito   
     */
    private boolean noNetea = false;
    /**
     * Par&aacute;metro que indica si en contabilizaci&oacute;n se
     * reportan los datos del contratista  
     */
    private boolean contratista = false;
    /**
     * Si el parametro contratista es true; en este par&aacute;metro 
     * se reportara el tipo de contrato que se quiere pagar
     */
    private String tipoContrato="";
    /**
     * Si el parametro contratista es true; en este par&aacute;metro 
     * se reportara el n&uacute;mero de contrato que se quiere pagar
     */
    private int contrato;
    /**
     * Par&aacute;metro que obtendr&aacute; el valor del n&uacute;mero de documento
     */
    private String nroDocumento;
    /**
     * Par&aacute;metro que indica si a la contabilizaci&oacute;n se envia la 
     * dependencia desde almacen
     */
    private boolean almDep = false;
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n respeta el tercero 
     * enviado por detalle o se toma el VARIOS para simplificar
     */
    private boolean respetaTercero= false;
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n respeta el auxiliar 
     * enviado por detalle o se toma el VARIOS para simplificar
     */
    private boolean respetaAuxiliar= false;
    /**
     * Par&aacute;metro que indica si la contabilizaci&oacute;n reemplaza el 
     * comprobante si este ya existe en la base de datos
     */
    private boolean reemplaza= false;
    /**
     * Par&aacute;metro que identifica el usuario que realiza la contabilizaci&oacute;n
     */
    private String usuario;

    /**
     * Par&aacute;metro que identifica el usuario que realiza la contabilizaci&oacute;n
     */
    private ReportesBean.FORMATOS formato;
    /**
     * Listado de detalles que se crearan con el comprobante contable
     */
    private List<DetalleComprobante> detalle;
    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }
    /**
     * @param compania the compa&ntilde;ia to set
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }
    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }
    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }
    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }
    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }
    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }
    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }
    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    /**
     * @return the tercero
     */
    public String getTercero() {
        return tercero;
    }
    /**
     * @param tercero the tercero to set
     */
    public void setTercero(String tercero) {
        this.tercero = tercero;
    }
    /**
     * @return the sucursal
     */
    public String getSucursal() {
        return sucursal;
    }
    /**
     * @param sucursal the sucursal to set
     */
    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
    /**
     * @return the centroCosto
     */
    public String getCentroCosto() {
        return centroCosto;
    }
    /**
     * @param centroCosto the centroCosto to set
     */
    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }
    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }
    /**
     * @param auxiliar the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    /**
     * @return the referencia
     */
    public String getReferencia() {
        return referencia;
    }
    /**
     * @param referencia the referencia to set
     */
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    /**
     * @return the fuenteRecurso
     */
    public String getFuenteRecurso() {
        return fuenteRecurso;
    }
    /**
     * @param fuenteRecurso the fuenteRecurso to set
     */
    public void setFuenteRecurso(String fuenteRecurso) {
        this.fuenteRecurso = fuenteRecurso;
    }
    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }
    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    /**
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }
    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }
    /**
     * @return the simplifica
     */
    public boolean isSimplifica() {
        return simplifica;
    }
    /**
     * @param simplifica the simplifica to set
     */
    public void setSimplifica(boolean simplifica) {
        this.simplifica = simplifica;
    }
    /**
     * @return the omitirPptal
     */
    public boolean isOmitirPptal() {
        return omitirPptal;
    }
    /**
     * @return the formato
     */
    public ReportesBean.FORMATOS getFormato() {
        return formato;
    }
    /**
     * @param formato the formato to set
     */
    public void setFormato(ReportesBean.FORMATOS formato) {
        this.formato = formato;
    }
    /**
     * @param omitirPptal the omitirPptal to set
     */
    public void setOmitirPptal(boolean omitirPptal) {
        this.omitirPptal = omitirPptal;
    }
    /**
     * @return the concilia
     */
    public boolean isConcilia() {
        return concilia;
    }
    /**
     * @param concilia the concilia to set
     */
    public void setConcilia(boolean concilia) {
        this.concilia = concilia;
    }
    /**
     * @return the noNetea
     */
    public boolean isNoNetea() {
        return noNetea;
    }
    /**
     * @param noNetea the noNetea to set
     */
    public void setNoNetea(boolean noNetea) {
        this.noNetea = noNetea;
    }
    /**
     * @return the contratista
     */
    public boolean isContratista() {
        return contratista;
    }
    /**
     * @param contratista the contratista to set
     */
    public void setContratista(boolean contratista) {
        this.contratista = contratista;
    }
    /**
     * @return the tipoContrato
     */
    public String getTipoContrato() {
        return tipoContrato;
    }
    /**
     * @param tipoContrato the tipoContrato to set
     */
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
    /**
     * @return the contrato
     */
    public int getContrato() {
        return contrato;
    }
    /**
     * @param contrato the contrato to set
     */
    public void setContrato(int contrato) {
        this.contrato = contrato;
    }
    /**
     * @return the nroDocumento
     */
    public String getNroDocumento() {
        return nroDocumento;
    }
    /**
     * @param nroDocumento the nroDocumento to set
     */
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }
    /**
     * @return the almDep
     */
    public boolean isAlmDep() {
        return almDep;
    }
    /**
     * @param almDep the almDep to set
     */
    public void setAlmDep(boolean almDep) {
        this.almDep = almDep;
    }
    /**
     * @return the respetaTercero
     */
    public boolean isRespetaTercero() {
        return respetaTercero;
    }
    /**
     * @param respetaTercero the respetaTercero to set
     */
    public void setRespetaTercero(boolean respetaTercero) {
        this.respetaTercero = respetaTercero;
    }
    /**
     * @return the respetaAuxiliar
     */
    public boolean isRespetaAuxiliar() {
        return respetaAuxiliar;
    }
    /**
     * @param respetaAuxiliar the respetaAuxiliar to set
     */
    public void setRespetaAuxiliar(boolean respetaAuxiliar) {
        this.respetaAuxiliar = respetaAuxiliar;
    }
    /**
     * @return the reemplaza
     */
    public boolean isReemplaza() {
        return reemplaza;
    }
    /**
     * @param reemplaza the reemplaza to set
     */
    public void setReemplaza(boolean reemplaza) {
        this.reemplaza = reemplaza;
    }
    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }
    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    /**
     * @return the detalle
     */
    public List<DetalleComprobante> getDetalle() {
        return detalle;
    }
    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(List<DetalleComprobante> detalle) {
        this.detalle = detalle;
    }
    /**
     * @return the nitEntidad
     */
    public long getNitEntidad() {
        return nitEntidad;
    }
    /**
     * @param nitEntidad the nitEntidad to set
     */
    public void setNitEntidad(long nitEntidad) {
        this.nitEntidad = nitEntidad;
    }


}
