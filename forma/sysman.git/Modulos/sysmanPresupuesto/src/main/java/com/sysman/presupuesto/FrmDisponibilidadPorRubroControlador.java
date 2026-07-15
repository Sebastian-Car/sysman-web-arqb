/*-
 * FrmDisponibilidadPorRubroControlador.java
 *
 * 1.0
 * 
 * 04/06/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.EjecucionGastosCaqControladorEnum;
import com.sysman.presupuesto.enums.FrSolicDispRubroControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @version 1.0, 04/06/2019
 * @author obarragan
 */
@ManagedBean
@ViewScoped
public class FrmDisponibilidadPorRubroControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    private boolean ckFuente;
    private boolean ckReferencia;
    private boolean ckAuxiliar;
    private boolean ckCentroCosto;
    private String centroInicial;
    private String centroFinal;
    private String auxiliarInicial;
    private String auxiliarFinal;
    private String referenciaInicial;
    private String referenciaFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private int anio;
    private String rubroInicial;
    private String rubroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreCentroInicial;
    private String nombreCentroFinal;
    private String nombreAuxiliarInicial;
    private String nombreAuxiliarFinal;
    private String nombreReferenciaInicial;
    private String nombreReferenciaFinal;
    private String nombreFuenteInicial;
    private String nombreFuenteFinal;
    private String nombreRubroInicial;
    private String nombreRubroFinal;
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>

    // <DECLARAR_PARAMETROS>

    // </DECLARAR_PARAMETROS>

    // <DECLARAR_LISTAS>

    private List<Registro> listaAnio;

    // </DECLARAR_LISTAS>

    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaCentroInicial;
    private RegistroDataModelImpl listaCentroFinal;
    private RegistroDataModelImpl listaAuxiliarInicial;
    private RegistroDataModelImpl listaAuxiliarFinal;
    private RegistroDataModelImpl listaReferenciInicial;
    private RegistroDataModelImpl listaReferenciaFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    private RegistroDataModelImpl listaRubroInicial;
    private RegistroDataModelImpl listaRubroFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmDisponibilidadPorRubroControlador
     */
    public FrmDisponibilidadPorRubroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FR_SOLIC_DISP_RUBRO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            anio = SysmanFunciones.ano(new Date());
            fechaInicial = new Date();
            fechaFinal = new Date();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaRubroInicial();
        cargarListaCentroInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciInicial();

        cargarListaFuenteInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

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

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnio
     *
     * 
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrSolicDispRubroControladorUrlEnum.URL4828
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCentroInicial
     *
     */
    public void cargarListaCentroInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL003
                                                        .getValue());

        listaCentroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCentroFinal
     *
     */
    public void cargarListaCentroFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL004
                                                        .getValue());

        listaCentroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAuxiliarInicial
     *
     */
    public void cargarListaAuxiliarInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL007
                                                        .getValue());

        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaAuxiliarFinal
     *
     * 
     */
    public void cargarListaAuxiliarFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", anio);
        param.put("CODIGOFINAL", auxiliarInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL008
                                                        .getValue());

        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferenciInicial
     *
     */
    public void cargarListaReferenciInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL009
                                                        .getValue());

        listaReferenciInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaReferenciaFinal
     *
     * 
     */
    public void cargarListaReferenciaFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", anio);
        param.put("REFERENCIAINICIAL", referenciaInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL010
                                                        .getValue());

        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteInicial
     *
     * 
     */
    public void cargarListaFuenteInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL011
                                                        .getValue());

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaFuenteFinal
     *
     */
    public void cargarListaFuenteFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANO", anio);
        param.put("FUENTEINICIAL", fuenteInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL012
                                                        .getValue());

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaRubroInicial
     *
     * 
     */
    public void cargarListaRubroInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL536
                                                        .getValue());

        listaRubroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaRubroFinal
     *
     */
    public void cargarListaRubroFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(EjecucionGastosCaqControladorEnum.CUENTAINICIAL.getValue(),
                        rubroInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrSolicDispRubroControladorUrlEnum.URL557
                                                        .getValue());
        listaRubroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT_PDF en la vista
     *
     *
     */
    public void oprimirBT_PDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BT_EXCEL en la vista
     *
     *
     */
    public void oprimirBT_EXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // <METODO GENERAR INFORME>
    public void generarInforme(FORMATOS formato) {
        if (!validarFechas()) {
            return;
        }
    	try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("anio", anio);
            reemplazar.put("fechaInicial", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put("rubroInicial", rubroInicial);
            reemplazar.put("rubroFinal", rubroFinal);

            reemplazar.put("manCen", ckCentroCosto ? "1" : "0");
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);

            reemplazar.put("manAux", ckAuxiliar ? "1" : "0");
            reemplazar.put("auxiliarInicial", auxiliarInicial);
            reemplazar.put("auxiliarFinal", auxiliarFinal);

            reemplazar.put("manRef", ckReferencia ? "1" : "0");
            reemplazar.put("referenciaInicial", referenciaInicial);
            reemplazar.put("referenciaFinal", referenciaFinal);

            reemplazar.put("manFue", ckFuente ? "1" : "0");
            reemplazar.put("fuenteInicial", fuenteInicial);
            reemplazar.put("fuenteFinal", fuenteFinal);

            Reporteador.resuelveConsulta("002012SolicitudDeDisponibilidad",
                            Integer.parseInt(modulo), reemplazar, parametros);

            // Parametros diseño reporte
            parametros.put("PR_COMPANIA", compania);

            parametros.put("PR_ANIO", anio);
            parametros.put("PR_FECHA_INICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHA_FINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_RUBRO_INICIAL", rubroInicial);
            parametros.put("PR_RUBRO_FINAL", rubroFinal);

            parametros.put("PR_CENTRO_VISIBLE", ckCentroCosto?true:false);
            parametros.put("PR_CENTRO_INICIAL", centroInicial);
            parametros.put("PR_CENTRO_FINAL", centroFinal);

            parametros.put("PR_AUX_VISIBLE", ckAuxiliar?true:false);
            parametros.put("PR_AUX_INICIAL", auxiliarInicial);
            parametros.put("PR_AUX_FINAL", auxiliarFinal);

            parametros.put("PR_REF_VISIBLE", ckReferencia?true:false);
            parametros.put("PR_REF_INICIAL", referenciaInicial);
            parametros.put("PR_REF_FINAL", referenciaFinal);

            parametros.put("PR_FUENTE_VISIBLE", ckFuente?true:false);
            parametros.put("PR_FUENTE_INICIAL", fuenteInicial);
            parametros.put("PR_FUENTE_FINAL", fuenteFinal);

            // Parametros diseño reporte

            archivoDescarga = JsfUtil.exportarStreamed(
                            "002012SolicitudDeDisponibilidad",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage()); 
        }
    }

    // </METODO GENERAR INFORME>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        rubroInicial = null;
        nombreRubroInicial = null;
        rubroFinal = null;
        nombreRubroFinal = null;
        centroInicial = null;
        nombreCentroInicial = null;
        centroFinal = null;
        nombreCentroFinal = null;
        auxiliarInicial = null;
        nombreAuxiliarInicial = null;
        auxiliarFinal = null;
        nombreAuxiliarFinal = null;
        referenciaInicial = null;
        nombreReferenciaInicial = null;
        referenciaFinal = null;
        nombreReferenciaFinal = null;
        fuenteInicial = null;
        nombreFuenteInicial = null;
        fuenteFinal = null;
        nombreFuenteFinal = null;
        inicializar();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckFuente
     * 
     * 
     * 
     */
    public void cambiarckFuente() {
        // <CODIGO_DESARROLLADO>
        fuenteInicial = null;
        nombreFuenteInicial = null;
        fuenteFinal = null;
        nombreFuenteFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckReferencia
     * 
     * 
     */
    public void cambiarckReferencia() {
        // <CODIGO_DESARROLLADO>
        referenciaInicial = null;
        nombreReferenciaInicial = null;
        referenciaFinal = null;
        nombreReferenciaFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckAuxiliar
     * 
     * 
     * 
     */
    public void cambiarckAuxiliar() {
        // <CODIGO_DESARROLLADO>
        auxiliarInicial = null;
        nombreAuxiliarInicial = null;
        auxiliarFinal = null;
        nombreAuxiliarFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckCentroCosto
     * 
     * 
     * 
     */
    public void cambiarckCentroCosto() {
        // <CODIGO_DESARROLLADO>
        centroInicial = null;
        nombreCentroInicial = null;
        centroFinal = null;
        nombreCentroFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCentroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaCentroFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCentroFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCentroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCentroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreAuxiliarInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaAuxiliarFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliarFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreAuxiliarFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreReferenciaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaReferenciaFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        referenciaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreReferenciaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreFuenteInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaFuenteFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreFuenteFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rubroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreRubroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaRubroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaRubroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        rubroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreRubroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ckFuente
     * 
     * @return ckFuente
     */
    public boolean getCkFuente() {
        return ckFuente;
    }

    /**
     * Asigna la variable ckFuente
     * 
     * @param ckFuente
     * Variable a asignar en ckFuente
     */
    public void setCkFuente(boolean ckFuente) {
        this.ckFuente = ckFuente;
    }

    /**
     * Retorna la variable ckReferencia
     * 
     * @return ckReferencia
     */
    public boolean getCkReferencia() {
        return ckReferencia;
    }

    /**
     * Asigna la variable ckReferencia
     * 
     * @param ckReferencia
     * Variable a asignar en ckReferencia
     */
    public void setCkReferencia(boolean ckReferencia) {
        this.ckReferencia = ckReferencia;
    }

    /**
     * Retorna la variable ckAuxiliar
     * 
     * @return ckAuxiliar
     */
    public boolean getCkAuxiliar() {
        return ckAuxiliar;
    }

    /**
     * Asigna la variable ckAuxiliar
     * 
     * @param ckAuxiliar
     * Variable a asignar en ckAuxiliar
     */
    public void setCkAuxiliar(boolean ckAuxiliar) {
        this.ckAuxiliar = ckAuxiliar;
    }

    /**
     * Retorna la variable ckCentroCosto
     * 
     * @return ckCentroCosto
     */
    public boolean getCkCentroCosto() {
        return ckCentroCosto;
    }

    /**
     * Asigna la variable ckCentroCosto
     * 
     * @param ckCentroCosto
     * Variable a asignar en ckCentroCosto
     */
    public void setCkCentroCosto(boolean ckCentroCosto) {
        this.ckCentroCosto = ckCentroCosto;
    }

    /**
     * Retorna la variable centroInicial
     * 
     * @return centroInicial
     */
    public String getCentroInicial() {
        return centroInicial;
    }

    /**
     * Asigna la variable centroInicial
     * 
     * @param centroInicial
     * Variable a asignar en centroInicial
     */
    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    /**
     * Retorna la variable centroFinal
     * 
     * @return centroFinal
     */
    public String getCentroFinal() {
        return centroFinal;
    }

    /**
     * Asigna la variable centroFinal
     * 
     * @param centroFinal
     * Variable a asignar en centroFinal
     */
    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
    }

    /**
     * Retorna la variable auxiliarInicial
     * 
     * @return auxiliarInicial
     */
    public String getAuxiliarInicial() {
        return auxiliarInicial;
    }

    /**
     * Asigna la variable auxiliarInicial
     * 
     * @param auxiliarInicial
     * Variable a asignar en auxiliarInicial
     */
    public void setAuxiliarInicial(String auxiliarInicial) {
        this.auxiliarInicial = auxiliarInicial;
    }

    /**
     * Retorna la variable auxiliarFinal
     * 
     * @return auxiliarFinal
     */
    public String getAuxiliarFinal() {
        return auxiliarFinal;
    }

    /**
     * Asigna la variable auxiliarFinal
     * 
     * @param auxiliarFinal
     * Variable a asignar en auxiliarFinal
     */
    public void setAuxiliarFinal(String auxiliarFinal) {
        this.auxiliarFinal = auxiliarFinal;
    }

    /**
     * Retorna la variable referenciaInicial
     * 
     * @return referenciaInicial
     */
    public String getReferenciaInicial() {
        return referenciaInicial;
    }

    /**
     * Asigna la variable referenciaInicial
     * 
     * @param referenciaInicial
     * Variable a asignar en referenciaInicial
     */
    public void setReferenciaInicial(String referenciaInicial) {
        this.referenciaInicial = referenciaInicial;
    }

    /**
     * Retorna la variable referenciaFinal
     * 
     * @return referenciaFinal
     */
    public String getReferenciaFinal() {
        return referenciaFinal;
    }

    /**
     * Asigna la variable referenciaFinal
     * 
     * @param referenciaFinal
     * Variable a asignar en referenciaFinal
     */
    public void setReferenciaFinal(String referenciaFinal) {
        this.referenciaFinal = referenciaFinal;
    }

    /**
     * Retorna la variable fuenteInicial
     * 
     * @return fuenteInicial
     */
    public String getFuenteInicial() {
        return fuenteInicial;
    }

    /**
     * Asigna la variable fuenteInicial
     * 
     * @param fuenteInicial
     * Variable a asignar en fuenteInicial
     */
    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    /**
     * Retorna la variable fuenteFinal
     * 
     * @return fuenteFinal
     */
    public String getFuenteFinal() {
        return fuenteFinal;
    }

    /**
     * Asigna la variable fuenteFinal
     * 
     * @param fuenteFinal
     * Variable a asignar en fuenteFinal
     */
    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable rubroInicial
     * 
     * @return rubroInicial
     */
    public String getRubroInicial() {
        return rubroInicial;
    }

    /**
     * Asigna la variable rubroInicial
     * 
     * @param rubroInicial
     * Variable a asignar en rubroInicial
     */
    public void setRubroInicial(String rubroInicial) {
        this.rubroInicial = rubroInicial;
    }

    /**
     * Retorna la variable rubroFinal
     * 
     * @return rubroFinal
     */
    public String getRubroFinal() {
        return rubroFinal;
    }

    /**
     * Asigna la variable rubroFinal
     * 
     * @param rubroFinal
     * Variable a asignar en rubroFinal
     */
    public void setRubroFinal(String rubroFinal) {
        this.rubroFinal = rubroFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable nombreCentroInicial
     * 
     * @return nombreCentroInicial
     */
    public String getNombreCentroInicial() {
        return nombreCentroInicial;
    }

    /**
     * Asigna la variable nombreCentroInicial
     * 
     * @param nombreCentroInicial
     * Variable a asignar en nombreCentroInicial
     */
    public void setNombreCentroInicial(String nombreCentroInicial) {
        this.nombreCentroInicial = nombreCentroInicial;
    }

    /**
     * Retorna la variable nombreCentroFinal
     * 
     * @return nombreCentroFinal
     */
    public String getNombreCentroFinal() {
        return nombreCentroFinal;
    }

    /**
     * Asigna la variable nombreCentroFinal
     * 
     * @param nombreCentroFinal
     * Variable a asignar en nombreCentroFinal
     */
    public void setNombreCentroFinal(String nombreCentroFinal) {
        this.nombreCentroFinal = nombreCentroFinal;
    }

    /**
     * Retorna la variable nombreAuxiliarInicial
     * 
     * @return nombreAuxiliarInicial
     */
    public String getNombreAuxiliarInicial() {
        return nombreAuxiliarInicial;
    }

    /**
     * Asigna la variable nombreAuxiliarInicial
     * 
     * @param nombreAuxiliarInicial
     * Variable a asignar en nombreAuxiliarInicial
     */
    public void setNombreAuxiliarInicial(String nombreAuxiliarInicial) {
        this.nombreAuxiliarInicial = nombreAuxiliarInicial;
    }

    /**
     * Retorna la variable nombreAuxiliarFinal
     * 
     * @return nombreAuxiliarFinal
     */
    public String getNombreAuxiliarFinal() {
        return nombreAuxiliarFinal;
    }

    /**
     * Asigna la variable nombreAuxiliarFinal
     * 
     * @param nombreAuxiliarFinal
     * Variable a asignar en nombreAuxiliarFinal
     */
    public void setNombreAuxiliarFinal(String nombreAuxiliarFinal) {
        this.nombreAuxiliarFinal = nombreAuxiliarFinal;
    }

    /**
     * Retorna la variable nombreReferenciaInicial
     * 
     * @return nombreReferenciaInicial
     */
    public String getNombreReferenciaInicial() {
        return nombreReferenciaInicial;
    }

    /**
     * Asigna la variable nombreReferenciaInicial
     * 
     * @param nombreReferenciaInicial
     * Variable a asignar en nombreReferenciaInicial
     */
    public void setNombreReferenciaInicial(String nombreReferenciaInicial) {
        this.nombreReferenciaInicial = nombreReferenciaInicial;
    }

    /**
     * Retorna la variable nombreReferenciaFinal
     * 
     * @return nombreReferenciaFinal
     */
    public String getNombreReferenciaFinal() {
        return nombreReferenciaFinal;
    }

    /**
     * Asigna la variable nombreReferenciaFinal
     * 
     * @param nombreReferenciaFinal
     * Variable a asignar en nombreReferenciaFinal
     */
    public void setNombreReferenciaFinal(String nombreReferenciaFinal) {
        this.nombreReferenciaFinal = nombreReferenciaFinal;
    }

    /**
     * Retorna la variable nombreFuenteInicial
     * 
     * @return nombreFuenteInicial
     */
    public String getNombreFuenteInicial() {
        return nombreFuenteInicial;
    }

    /**
     * Asigna la variable nombreFuenteInicial
     * 
     * @param nombreFuenteInicial
     * Variable a asignar en nombreFuenteInicial
     */
    public void setNombreFuenteInicial(String nombreFuenteInicial) {
        this.nombreFuenteInicial = nombreFuenteInicial;
    }

    /**
     * Retorna la variable nombreFuenteFinal
     * 
     * @return nombreFuenteFinal
     */
    public String getNombreFuenteFinal() {
        return nombreFuenteFinal;
    }

    /**
     * Asigna la variable nombreFuenteFinal
     * 
     * @param nombreFuenteFinal
     * Variable a asignar en nombreFuenteFinal
     */
    public void setNombreFuenteFinal(String nombreFuenteFinal) {
        this.nombreFuenteFinal = nombreFuenteFinal;
    }

    /**
     * Retorna la variable nombreRubroInicial
     * 
     * @return nombreRubroInicial
     */
    public String getNombreRubroInicial() {
        return nombreRubroInicial;
    }

    /**
     * Asigna la variable nombreRubroInicial
     * 
     * @param nombreRubroInicial
     * Variable a asignar en nombreRubroInicial
     */
    public void setNombreRubroInicial(String nombreRubroInicial) {
        this.nombreRubroInicial = nombreRubroInicial;
    }

    /**
     * Retorna la variable nombreRubroFinal
     * 
     * @return nombreRubroFinal
     */
    public String getNombreRubroFinal() {
        return nombreRubroFinal;
    }

    /**
     * Asigna la variable nombreRubroFinal
     * 
     * @param nombreRubroFinal
     * Variable a asignar en nombreRubroFinal
     */
    public void setNombreRubroFinal(String nombreRubroFinal) {
        this.nombreRubroFinal = nombreRubroFinal;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCentroInicial
     * 
     * @return listaCentroInicial
     */
    public RegistroDataModelImpl getListaCentroInicial() {
        return listaCentroInicial;
    }

    /**
     * Asigna la lista listaCentroInicial
     * 
     * @param listaCentroInicial
     * Variable a asignar en listaCentroInicial
     */
    public void setListaCentroInicial(
        RegistroDataModelImpl listaCentroInicial) {
        this.listaCentroInicial = listaCentroInicial;
    }

    /**
     * Retorna la lista listaCentroFinal
     * 
     * @return listaCentroFinal
     */
    public RegistroDataModelImpl getListaCentroFinal() {
        return listaCentroFinal;
    }

    /**
     * Asigna la lista listaCentroFinal
     * 
     * @param listaCentroFinal
     * Variable a asignar en listaCentroFinal
     */
    public void setListaCentroFinal(RegistroDataModelImpl listaCentroFinal) {
        this.listaCentroFinal = listaCentroFinal;
    }

    /**
     * Retorna la lista listaAuxiliarInicial
     * 
     * @return listaAuxiliarInicial
     */
    public RegistroDataModelImpl getListaAuxiliarInicial() {
        return listaAuxiliarInicial;
    }

    /**
     * Asigna la lista listaAuxiliarInicial
     * 
     * @param listaAuxiliarInicial
     * Variable a asignar en listaAuxiliarInicial
     */
    public void setListaAuxiliarInicial(
        RegistroDataModelImpl listaAuxiliarInicial) {
        this.listaAuxiliarInicial = listaAuxiliarInicial;
    }

    /**
     * Retorna la lista listaAuxiliarFinal
     * 
     * @return listaAuxiliarFinal
     */
    public RegistroDataModelImpl getListaAuxiliarFinal() {
        return listaAuxiliarFinal;
    }

    /**
     * Asigna la lista listaAuxiliarFinal
     * 
     * @param listaAuxiliarFinal
     * Variable a asignar en listaAuxiliarFinal
     */
    public void setListaAuxiliarFinal(
        RegistroDataModelImpl listaAuxiliarFinal) {
        this.listaAuxiliarFinal = listaAuxiliarFinal;
    }

    /**
     * Retorna la lista listaReferenciInicial
     * 
     * @return listaReferenciInicial
     */
    public RegistroDataModelImpl getListaReferenciInicial() {
        return listaReferenciInicial;
    }

    /**
     * Asigna la lista listaReferenciInicial
     * 
     * @param listaReferenciInicial
     * Variable a asignar en listaReferenciInicial
     */
    public void setListaReferenciInicial(
        RegistroDataModelImpl listaReferenciInicial) {
        this.listaReferenciInicial = listaReferenciInicial;
    }

    /**
     * Retorna la lista listaReferenciaFinal
     * 
     * @return listaReferenciaFinal
     */
    public RegistroDataModelImpl getListaReferenciaFinal() {
        return listaReferenciaFinal;
    }

    /**
     * Asigna la lista listaReferenciaFinal
     * 
     * @param listaReferenciaFinal
     * Variable a asignar en listaReferenciaFinal
     */
    public void setListaReferenciaFinal(
        RegistroDataModelImpl listaReferenciaFinal) {
        this.listaReferenciaFinal = listaReferenciaFinal;
    }

    /**
     * Retorna la lista listaFuenteInicial
     * 
     * @return listaFuenteInicial
     */
    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    /**
     * Asigna la lista listaFuenteInicial
     * 
     * @param listaFuenteInicial
     * Variable a asignar en listaFuenteInicial
     */
    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    /**
     * Retorna la lista listaFuenteFinal
     * 
     * @return listaFuenteFinal
     */
    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    /**
     * Asigna la lista listaFuenteFinal
     * 
     * @param listaFuenteFinal
     * Variable a asignar en listaFuenteFinal
     */
    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }

    /**
     * Retorna la lista listaRubroInicial
     * 
     * @return listaRubroInicial
     */
    public RegistroDataModelImpl getListaRubroInicial() {
        return listaRubroInicial;
    }

    /**
     * Asigna la lista listaRubroInicial
     * 
     * @param listaRubroInicial
     * Variable a asignar en listaRubroInicial
     */
    public void setListaRubroInicial(RegistroDataModelImpl listaRubroInicial) {
        this.listaRubroInicial = listaRubroInicial;
    }

    /**
     * Retorna la lista listaRubroFinal
     * 
     * @return listaRubroFinal
     */
    public RegistroDataModelImpl getListaRubroFinal() {
        return listaRubroFinal;
    }

    /**
     * Asigna la lista listaRubroFinal
     * 
     * @param listaRubroFinal
     * Variable a asignar en listaRubroFinal
     */
    public void setListaRubroFinal(RegistroDataModelImpl listaRubroFinal) {
        this.listaRubroFinal = listaRubroFinal;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    
    private boolean validarFechas() {
        boolean rta = true;
        if (fechaFinal.before(fechaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB75"));
            rta = false;
        }
        return rta;
    }
}
