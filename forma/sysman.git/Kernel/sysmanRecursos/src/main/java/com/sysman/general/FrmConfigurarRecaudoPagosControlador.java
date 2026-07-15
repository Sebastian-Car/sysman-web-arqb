/*-
 * FrmConfigurarRecaudoPagosControlador.java
 *
 * 1.0
 * 
 * 15/07/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmRegistrarPagoRecaudoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroGeneralLocal;
import com.sysman.services.RegistroDataModelImpl;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 15/07/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class FrmConfigurarRecaudoPagosControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    private String bancoPago;
    private Date fechaActual;
    private String nombreBanco;
    private String numeroPaquetes;
    private String numeroCupon;
    private String valorReportado;
    private String codigoBarras;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private final String BANCO = "BANCO";
    private final String NOMBREBANCO = "NOMBREBANCO";
    private final String APLICACION = "APLICACION";
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBancoPago;
    private RegistroDataModelImpl listaBancoPagoE;

    @EJB
    private EjbPlusvaliaCeroGeneralLocal ejbPlusvaliaCeroGeneralRemote;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmConfigurarRecaudoPagosControlador
     */
    public FrmConfigurarRecaudoPagosControlador() {
        super();

        modulo = SessionUtil.getModulo();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().toString();

        try {
            numFormulario = 2096;
            validarPermisos();
            fechaActual = new Date();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.GN_PAGO_BANCO_REC;

        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBancoPago();
        cargarListaBancoPagoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        // <CODIGO_DESARROLLADO>
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(APLICACION, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaBancoPago
     *
     */
    public void cargarListaBancoPago() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRegistrarPagoRecaudoControladorUrlEnum.URL0001// 1803001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("APLICACION", SessionUtil.getModulo());

        listaBancoPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.BANCO.getName());

    }

    /**
     * 
     * Carga la lista listaBancoPago
     *
     */
    public void cargarListaBancoPagoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmRegistrarPagoRecaudoControladorUrlEnum.URL0001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("APLICACION", SessionUtil.getModulo());

        listaBancoPagoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.BANCO.getName());

    }

    /**
     * Metodo ejecutado al cambiar el control FechaActual
     * 
     * 
     */
    public void cambiarFechaActual() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NombreBanco
     * 
     * 
     */
    public void cambiarNombreBanco() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Paquetes
     * 
     * 
     */
    public void cambiarPaquetes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Cupones
     * 
     * 
     */
    public void cambiarCupones() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorReportado
     * 
     * 
     */
    public void cambiarValorReportado() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Referencia
     * 
     * 
     */
    public void cambiarReferencia() {
        // <CODIGO_DESARROLLADO>

        try {
            ejbPlusvaliaCeroGeneralRemote.insertConfigurarPago(compania,
                            fechaActual,
                            bancoPago,
                            numeroPaquetes,
                            Integer.parseInt(numeroCupon),
                            valorReportado,
                            codigoBarras,
                            Integer.parseInt(modulo),
                            usuario);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarForma();
        codigoBarras = null;
        reasignarOrigen();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoPago
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        bancoPago = registroAux.getCampos().get(BANCO).toString();
        nombreBanco = registroAux.getCampos().get(NOMBREBANCO).toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaBancoPago
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBancoPagoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get(BANCO);
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2096-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir GetIdModulo, Me.Name End Sub
         *//*
            * FR2096-AL_ABRIR Private Sub Form_Load() DoCmd.Restore
            * End Sub
            */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable bancoPago
     * 
     * @return bancoPago
     */
    public String getBancoPago() {
        return bancoPago;
    }

    /**
     * Asigna la variable bancoPago
     * 
     * @param bancoPago
     * Variable a asignar en bancoPago
     */
    public void setBancoPago(String bancoPago) {
        this.bancoPago = bancoPago;
    }

    /**
     * Retorna la variable fechaActual
     * 
     * @return fechaActual
     */
    public Date getFechaActual() {
        return fechaActual;
    }

    /**
     * Asigna la variable fechaActual
     * 
     * @param fechaActual
     * Variable a asignar en fechaActual
     */
    public void setFechaActual(Date fechaActual) {
        this.fechaActual = fechaActual;
    }

    /**
     * Retorna la variable nombreBanco
     * 
     * @return nombreBanco
     */
    public String getNombreBanco() {
        return nombreBanco;
    }

    /**
     * Asigna la variable nombreBanco
     * 
     * @param nombreBanco
     * Variable a asignar en nombreBanco
     */
    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    /**
     * Retorna la variable numeroPaquetes
     * 
     * @return numeroPaquetes
     */
    public String getNumeroPaquetes() {
        return numeroPaquetes;
    }

    /**
     * Asigna la variable numeroPaquetes
     * 
     * @param numeroPaquetes
     * Variable a asignar en numeroPaquetes
     */
    public void setNumeroPaquetes(String numeroPaquetes) {
        this.numeroPaquetes = numeroPaquetes;
    }

    /**
     * Retorna la variable numeroCupon
     * 
     * @return numeroCupon
     */
    public String getNumeroCupon() {
        return numeroCupon;
    }

    /**
     * Asigna la variable numeroCupon
     * 
     * @param numeroCupon
     * Variable a asignar en numeroCupon
     */
    public void setNumeroCupon(String numeroCupon) {
        this.numeroCupon = numeroCupon;
    }

    /**
     * Retorna la variable valorReportado
     * 
     * @return valorReportado
     */
    public String getValorReportado() {
        return valorReportado;
    }

    /**
     * Asigna la variable valorReportado
     * 
     * @param valorReportado
     * Variable a asignar en valorReportado
     */
    public void setValorReportado(String valorReportado) {
        this.valorReportado = valorReportado;
    }

    /**
     * Retorna la variable codigoBarras
     * 
     * @return codigoBarras
     */
    public String getCodigoBarras() {
        return codigoBarras;
    }

    /**
     * Asigna la variable codigoBarras
     * 
     * @param codigoBarras
     * Variable a asignar en codigoBarras
     */
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaBancoPago
     * 
     * @return listaBancoPago
     */
    public RegistroDataModelImpl getListaBancoPago() {
        return listaBancoPago;
    }

    /**
     * Asigna la lista listaBancoPago
     * 
     * @param listaBancoPago
     * Variable a asignar en listaBancoPago
     */
    public void setListaBancoPago(RegistroDataModelImpl listaBancoPago) {
        this.listaBancoPago = listaBancoPago;
    }

    /**
     * Retorna la lista listaBancoPago
     * 
     * @return listaBancoPago
     */
    public RegistroDataModelImpl getListaBancoPagoE() {
        return listaBancoPagoE;
    }

    /**
     * Asigna la lista listaBancoPago
     * 
     * @param listaBancoPago
     * Variable a asignar en listaBancoPago
     */
    public void setListaBancoPagoE(RegistroDataModelImpl listaBancoPagoE) {
        this.listaBancoPagoE = listaBancoPagoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
