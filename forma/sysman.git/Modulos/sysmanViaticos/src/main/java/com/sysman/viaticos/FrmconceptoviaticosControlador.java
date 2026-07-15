/*-
 * FrmconceptoviaticosControlador.java
 *
 * 1.0
 * 
 * 19/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.FrmconceptoviaticosControladorEnum;
import com.sysman.viaticos.enums.FrmconceptoviaticosControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Esta conte se encarga de realizar las funciones CRUD del formulario
 * 'FRM_CONCEPTO_VIATICOS_'
 *
 * @version 1.0, 19/01/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmconceptoviaticosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Variable encargada de almacenar los registros de la listaAno
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Variable encargada de almacenar los registros de la
     * listaComprobanteDisponibilidad
     */
    private RegistroDataModelImpl listaComprobanteDisponibilidad;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCuentaPptal
     */
    private RegistroDataModelImpl listaCuentaPptal;
    /**
     * Variable encargada de almacenar los registros de la
     * listaTipoComprobanteCnt
     */
    private RegistroDataModelImpl listaTipoComprobanteCnt;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCtaCausacionDebito
     */
    private RegistroDataModelImpl listaCtaCausacionDebito;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCtaCausacionCredito
     */
    private RegistroDataModelImpl listaCtaCausacionCredito;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCtaLegalizacionDebito
     */
    private RegistroDataModelImpl listaCtaLegalizacionDebito;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCtaLegalizacionCredito
     */
    private RegistroDataModelImpl listaCtaLegalizacionCredito;
    /**
     * Variable encargada de almacenar los registros de la
     * listaTipoComprobanteTes
     */
    private RegistroDataModelImpl listaTipoComprobanteTes;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCtaCausacionDebito
     */
    private RegistroDataModelImpl listaTipoComprobanteLeg;
    /**
     * Variable encargada de almacenar los registros de la
     * listaAuxiliar
     */
    private RegistroDataModelImpl listaAuxiliar;
    /**
     * Variable encargada de almacenar los registros de la
     * listaFuenteRecurso
     */
    private RegistroDataModelImpl listaFuenteRecurso;
    /**
     * Variable encargada de almacenar los registros de la
     * listaReferencia
     */
    private RegistroDataModelImpl listaReferencia;
    /**
     * Variable encargada de almacenar los registros de la
     * listaTipoPptal
     */
    private RegistroDataModelImpl listaTipoPptal;
    /**
     * Variable encargada de almacenar los registros de la
     * listaCuadro_combinado1605
     */
    private RegistroDataModelImpl listaCuadroCombinado;

    /*
     * Constante que sirve para almacenar el consecutivo del codigo en
     * la tabla ev_tipo_funciones 0000x
     */
    private long consecutivo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmconceptoviaticosControlador
     */
    public FrmconceptoviaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        consecutivo = 0;
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_CONCEPTO_VIATICOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoPptal();
        cargarListaTipoComprobanteCnt();
        cargarListaTipoComprobanteTes();
        cargarListaTipoComprobanteLeg();
        cargarListaCuadroCombinado();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.VI_CONCEPTO_VIATICOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     * 
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmconceptoviaticosControladorUrlEnum.URL100
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaComprobanteDisponibilidad
     *
     */
    public void cargarListaComprobanteDisponibilidad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));

        param.put(FrmconceptoviaticosControladorEnum.TIPOPPTAL.getValue(),
                        registro.getCampos()
                                        .get(FrmconceptoviaticosControladorEnum.TIPO_CPTE_PPTAL
                                                        .getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL103
                                                        .getValue());
        listaComprobanteDisponibilidad = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.NUMERO.getName());

    }

    /**
     * 
     * Carga la lista listaCuentaPptal
     *
     * 
     */
    public void cargarListaCuentaPptal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));

        param.put(FrmconceptoviaticosControladorEnum.COMPROBANTEDISPONIBILIDAD
                        .getValue(),
                        registro.getCampos()
                                        .get(FrmconceptoviaticosControladorEnum.COMPROBANTE_DISPON
                                                        .getValue()));

        param.put(FrmconceptoviaticosControladorEnum.TIPOPPTAL.getValue(),
                        registro.getCampos()
                                        .get(FrmconceptoviaticosControladorEnum.TIPO_CPTE_PPTAL
                                                        .getValue()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL104
                                                        .getValue());
        listaCuentaPptal = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CUENTA.getName());
    }

    /**
     * 
     * Carga la lista listaTipoComprobanteCnt
     *
     */
    public void cargarListaTipoComprobanteCnt() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL105
                                                        .getValue());
        listaTipoComprobanteCnt = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCtaCausacionDebito
     *
     * 
     */
    public void cargarListaCtaCausacionDebito() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));

        param.put(FrmconceptoviaticosControladorEnum.NATURALEZA.getValue(),
                        "D");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL101
                                                        .getValue());
        listaCtaCausacionDebito = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmconceptoviaticosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCtaCausacionCredito
     *
     * 
     */
    public void cargarListaCtaCausacionCredito() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL112
                                                        .getValue());
        listaCtaCausacionCredito = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmconceptoviaticosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCtaLegalizacionDebito
     *
     * 
     */
    public void cargarListaCtaLegalizacionDebito() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));

        param.put(FrmconceptoviaticosControladorEnum.NATURALEZA.getValue(),
                        "D");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL101
                                                        .getValue());
        listaCtaLegalizacionDebito = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmconceptoviaticosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaCtaLegalizacionCredito
     *
     * 
     */
    public void cargarListaCtaLegalizacionCredito() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.ANO
                                                        .getName()));

        param.put(FrmconceptoviaticosControladorEnum.NATURALEZA.getValue(),
                        "D");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL101
                                                        .getValue());
        listaCtaLegalizacionCredito = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmconceptoviaticosControladorEnum.ID.getValue());
    }

    /**
     * 
     * Carga la lista listaTipoComprobanteTes
     *
     * 
     */
    public void cargarListaTipoComprobanteTes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL106
                                                        .getValue());
        listaTipoComprobanteTes = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoComprobanteLeg
     *
     * 
     */
    public void cargarListaTipoComprobanteLeg() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL106
                                                        .getValue());
        listaTipoComprobanteLeg = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTipoPptal
     *
     * 
     */
    public void cargarListaTipoPptal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL102
                                                        .getValue());

        listaTipoPptal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCuadro_combinado1605
     *
     * 
     */
    public void cargarListaCuadroCombinado() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoviaticosControladorUrlEnum.URL111
                                                        .getValue());

        listaCuadroCombinado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        /*
         * ESTO SE HACE PARA LIMPIAR EL COMBO AL CAMBIAR EL AÑO DADO
         * QUE AL CAMBIAR EL ANO LA CONSULTA DE ESTOS COMBOS CAMBIA
         * POR ENDE SE DEBEN RECARGAR
         */
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_CAUSACION_DEBITO
                                        .getValue(), null);
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_CAUSACION_CREDITO
                                        .getValue(), null);
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_LEGALIZACION_CREDITO
                                        .getValue(), null);
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_LEGALIZACION_DEBITO
                                        .getValue(), null);
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.COMPROBANTE_DISPON
                                        .getValue(), null);
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.TIPO_CPTE_PPTAL
                                        .getValue(), null);
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_PPTAL
                                        .getValue(),
                                        null);
        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        null);

        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), null);

        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        null);

        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        null);
        /*
         * CARGAR LAS CONSULTAS DE LOS COMBOS
         */
        cargarListaCtaCausacionDebito();
        cargarListaCtaCausacionCredito();
        cargarListaCtaLegalizacionCredito();
        cargarListaCtaLegalizacionDebito();
        cargarListaComprobanteDisponibilidad();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaComprobanteDisponibilidad
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaComprobanteDisponibilidad(SelectEvent event) {

        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        null);

        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), null);

        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        null);

        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        null);

        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_PPTAL
                                        .getValue(),
                                        null);

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.COMPROBANTE_DISPON
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.NUMERO
                                                                        .getName()));
        cargarListaCuentaPptal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuentaPptal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaPptal(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(FrmconceptoviaticosControladorEnum.CUENTA_PPTAL
                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CUENTA.getName()));

        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CENTRO_COSTO
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.AUXILIAR
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.FUENTE_RECURSO
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.REFERENCIA
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobanteCnt
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobanteCnt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.TIPO_CPTE_CNT
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(GeneralParameterEnum.CODIGO
                                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCausacionDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCausacionDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_CAUSACION_DEBITO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmconceptoviaticosControladorEnum.ID
                                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaCausacionCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaCausacionCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_CAUSACION_CREDITO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmconceptoviaticosControladorEnum.ID
                                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaLegalizacionDebito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaLegalizacionDebito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_LEGALIZACION_DEBITO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmconceptoviaticosControladorEnum.ID
                                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCtaLegalizacionCredito
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCtaLegalizacionCredito(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_LEGALIZACION_CREDITO
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(FrmconceptoviaticosControladorEnum.ID
                                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobanteTes
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobanteTes(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.TIPO_CPTE_CNT_TES
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobanteLeg
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobanteLeg(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.TIPO_CPTE_CNT_LEG
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoPptal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoPptal(SelectEvent event) {

        registro.getCampos().put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        null);

        registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(), null);

        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        null);

        registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                        null);

        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.COMPROBANTE_DISPON
                                        .getValue(), null);

        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.CUENTA_PPTAL
                                        .getValue(), null);

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.TIPO_CPTE_PPTAL
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));

        cargarListaComprobanteDisponibilidad();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCuadro_combinado1605
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuadroCombinado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmconceptoviaticosControladorEnum.TIPO_CONCEPTO
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.CODIGO
                                                                        .getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (ACCION_MODIFICAR.equals(accion)) {
            cargarListaCtaCausacionDebito();
            cargarListaCtaCausacionCredito();
            cargarListaCtaLegalizacionCredito();
            cargarListaCtaLegalizacionDebito();
            cargarListaTipoPptal();
            cargarListaComprobanteDisponibilidad();
            cargarListaCuentaPptal();
            cargarListaTipoComprobanteCnt();
            cargarListaTipoComprobanteTes();
            cargarListaTipoComprobanteLeg();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(

                            GenericUrlEnum.VI_CONCEPTO_VIATICOS.getTable(),
                            SysmanFunciones.concatenar("COMPANIA=''", compania,
                                            "''"),
                            FrmconceptoviaticosControladorEnum.CODIGO_CONCEPTO
                                            .getValue());

            registro.getCampos()
                            .put(FrmconceptoviaticosControladorEnum.CODIGO_CONCEPTO
                                            .getValue(),
                                            consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            registro.getCampos()
                            .remove(FrmconceptoviaticosControladorEnum.CODIGO_CONCEPTO
                                            .getValue());

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaComprobanteDisponibilidad
     * 
     * @return listaComprobanteDisponibilidad
     */
    public RegistroDataModelImpl getListaComprobanteDisponibilidad() {
        return listaComprobanteDisponibilidad;
    }

    /**
     * Asigna la lista listaComprobanteDisponibilidad
     * 
     * @param listaComprobanteDisponibilidad
     * Variable a asignar en listaComprobanteDisponibilidad
     */
    public void setListaComprobanteDisponibilidad(
        RegistroDataModelImpl listaComprobanteDisponibilidad) {
        this.listaComprobanteDisponibilidad = listaComprobanteDisponibilidad;
    }

    /**
     * Retorna la lista listaCuentaPptal
     * 
     * @return listaCuentaPptal
     */
    public RegistroDataModelImpl getListaCuentaPptal() {
        return listaCuentaPptal;
    }

    /**
     * Asigna la lista listaCuentaPptal
     * 
     * @param listaCuentaPptal
     * Variable a asignar en listaCuentaPptal
     */
    public void setListaCuentaPptal(RegistroDataModelImpl listaCuentaPptal) {
        this.listaCuentaPptal = listaCuentaPptal;
    }

    /**
     * Retorna la lista listaTipoComprobanteCnt
     * 
     * @return listaTipoComprobanteCnt
     */
    public RegistroDataModelImpl getListaTipoComprobanteCnt() {
        return listaTipoComprobanteCnt;
    }

    /**
     * Asigna la lista listaTipoComprobanteCnt
     * 
     * @param listaTipoComprobanteCnt
     * Variable a asignar en listaTipoComprobanteCnt
     */
    public void setListaTipoComprobanteCnt(
        RegistroDataModelImpl listaTipoComprobanteCnt) {
        this.listaTipoComprobanteCnt = listaTipoComprobanteCnt;
    }

    /**
     * Retorna la lista listaCtaCausacionDebito
     * 
     * @return listaCtaCausacionDebito
     */
    public RegistroDataModelImpl getListaCtaCausacionDebito() {
        return listaCtaCausacionDebito;
    }

    /**
     * Asigna la lista listaCtaCausacionDebito
     * 
     * @param listaCtaCausacionDebito
     * Variable a asignar en listaCtaCausacionDebito
     */
    public void setListaCtaCausacionDebito(
        RegistroDataModelImpl listaCtaCausacionDebito) {
        this.listaCtaCausacionDebito = listaCtaCausacionDebito;
    }

    /**
     * Retorna la lista listaCtaCausacionCredito
     * 
     * @return listaCtaCausacionCredito
     */
    public RegistroDataModelImpl getListaCtaCausacionCredito() {
        return listaCtaCausacionCredito;
    }

    /**
     * Asigna la lista listaCtaCausacionCredito
     * 
     * @param listaCtaCausacionCredito
     * Variable a asignar en listaCtaCausacionCredito
     */
    public void setListaCtaCausacionCredito(
        RegistroDataModelImpl listaCtaCausacionCredito) {
        this.listaCtaCausacionCredito = listaCtaCausacionCredito;
    }

    /**
     * Retorna la lista listaCtaLegalizacionDebito
     * 
     * @return listaCtaLegalizacionDebito
     */
    public RegistroDataModelImpl getListaCtaLegalizacionDebito() {
        return listaCtaLegalizacionDebito;
    }

    /**
     * Asigna la lista listaCtaLegalizacionDebito
     * 
     * @param listaCtaLegalizacionDebito
     * Variable a asignar en listaCtaLegalizacionDebito
     */
    public void setListaCtaLegalizacionDebito(
        RegistroDataModelImpl listaCtaLegalizacionDebito) {
        this.listaCtaLegalizacionDebito = listaCtaLegalizacionDebito;
    }

    /**
     * Retorna la lista listaCtaLegalizacionCredito
     * 
     * @return listaCtaLegalizacionCredito
     */
    public RegistroDataModelImpl getListaCtaLegalizacionCredito() {
        return listaCtaLegalizacionCredito;
    }

    /**
     * Asigna la lista listaCtaLegalizacionCredito
     * 
     * @param listaCtaLegalizacionCredito
     * Variable a asignar en listaCtaLegalizacionCredito
     */
    public void setListaCtaLegalizacionCredito(
        RegistroDataModelImpl listaCtaLegalizacionCredito) {
        this.listaCtaLegalizacionCredito = listaCtaLegalizacionCredito;
    }

    /**
     * Retorna la lista listaTipoComprobanteTes
     * 
     * @return listaTipoComprobanteTes
     */
    public RegistroDataModelImpl getListaTipoComprobanteTes() {
        return listaTipoComprobanteTes;
    }

    /**
     * Asigna la lista listaTipoComprobanteTes
     * 
     * @param listaTipoComprobanteTes
     * Variable a asignar en listaTipoComprobanteTes
     */
    public void setListaTipoComprobanteTes(
        RegistroDataModelImpl listaTipoComprobanteTes) {
        this.listaTipoComprobanteTes = listaTipoComprobanteTes;
    }

    /**
     * Retorna la lista listaTipoComprobanteLeg
     * 
     * @return listaTipoComprobanteLeg
     */
    public RegistroDataModelImpl getListaTipoComprobanteLeg() {
        return listaTipoComprobanteLeg;
    }

    /**
     * Asigna la lista listaTipoComprobanteLeg
     * 
     * @param listaTipoComprobanteLeg
     * Variable a asignar en listaTipoComprobanteLeg
     */
    public void setListaTipoComprobanteLeg(
        RegistroDataModelImpl listaTipoComprobanteLeg) {
        this.listaTipoComprobanteLeg = listaTipoComprobanteLeg;
    }

    /**
     * Retorna la lista listaAuxiliar
     * 
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    /**
     * Asigna la lista listaAuxiliar
     * 
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * Retorna la lista listaFuenteRecurso
     * 
     * @return listaFuenteRecurso
     */
    public RegistroDataModelImpl getListaFuenteRecurso() {
        return listaFuenteRecurso;
    }

    /**
     * Asigna la lista listaFuenteRecurso
     * 
     * @param listaFuenteRecurso
     * Variable a asignar en listaFuenteRecurso
     */
    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso) {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    /**
     * Retorna la lista listaReferencia
     * 
     * @return listaReferencia
     */
    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }

    /**
     * Asigna la lista listaReferencia
     * 
     * @param listaReferencia
     * Variable a asignar en listaReferencia
     */
    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }

    /**
     * Retorna la lista listaTipoPptal
     * 
     * @return listaTipoPptal
     */
    public RegistroDataModelImpl getListaTipoPptal() {
        return listaTipoPptal;
    }

    /**
     * Asigna la lista listaTipoPptal
     * 
     * @param listaTipoPptal
     * Variable a asignar en listaTipoPptal
     */
    public void setListaTipoPptal(RegistroDataModelImpl listaTipoPptal) {
        this.listaTipoPptal = listaTipoPptal;
    }

    /**
     * Retorna la lista listaCuadro_combinado1605
     * 
     * @return listaCuadro_combinado1605
     */
    public RegistroDataModelImpl getListaCuadroCombinado() {
        return listaCuadroCombinado;
    }

    /**
     * Asigna la lista listaCuadro_combinado1605
     * 
     * @param listaCuadro_combinado1605
     * Variable a asignar en listaCuadro_combinado1605
     */
    public void setListaCuadroCombinado(
        RegistroDataModelImpl listaCuadroCombinado) {
        this.listaCuadroCombinado = listaCuadroCombinado;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
