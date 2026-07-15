/*-
 * FrmComisionesControlador.java
 *
 * 1.0
 * 
 * 15 sept. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.FrmComisionesControladorEnum;
import com.sysman.viaticos.enums.FrmComisionesControladorUrlEnum;
import com.sysman.viaticos.enums.FrmViaticosControladorEnum;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite realizar la solicitud de comision de
 * viaticos
 *
 * @version 1.0, 15/09/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmComisionesControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo que indica si el boton que genera reporte esta
     * bloqueado
     */
    private boolean bloqueoReporte;

    /**
     * Atributo que permite visualizar algunos objetos de la forma si
     * el tipo de viatico es al interior o al exterior
     */
    private boolean visibleInterior;

    /**
     * Atributo que permite visualizar la pestaña de tramites mintic
     */
    private boolean visibleMintic;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que carga las ciudades destino
     */
    private List<Registro> listaCiudadeDestino;

    /**
     * lista que carga los paises destino
     */
    private List<Registro> listaPaisDestino;
    /**
     * Lista que carga los departamentos destino
     */
    private List<Registro> listaDepartamentoDestino;
    /**
     * Lista que carga las ciudades origen
     */
    private List<Registro> listaCiudadOrigen;
    /**
     * Lista que carga los paises origen
     */
    private List<Registro> listaPaisOrigen;
    /**
     * Lista que carga los departamentos origen
     */
    private List<Registro> listaDepartamentoOrigen;

    /**
     * Lista que carga los paises de regreso
     */
    private List<Registro> listaPaisRegreso;
    /**
     * Lista que carga los departamentos de regreso
     */
    private List<Registro> listaDepartamentoRegreso;
    /**
     * Lista que carga las ciudades de regreso
     */
    private List<Registro> listaCiudadRegreso;

    // </DECLARAR_ATRIBUTOS>
    private boolean editarJefeD;
    private String Valor;
    private boolean visibleUsuario;
    private String usuario;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Lista que carga las dependencias
     */
    private RegistroDataModelImpl listaDependencia;

    /**
     * lista que carga los tipos de visita
     */
    private RegistroDataModelImpl listaTipoVisita;

    /**
     * Atributo que almacena los dias habiles entre las dos fechas que
     * se seleccionan en la vista
     */
    private String diasHabiles;

    /**
     * Lista que carga los tipos de solicitud
     */
    private RegistroDataModelImpl listaTipoSolicitud;

    /**
     * Lista que carga los terceros
     */
    private RegistroDataModelImpl listaTercero;

    /**
     * Lista que carga los bancos
     */
    private RegistroDataModelImpl listaBanco;

    /**
     * Lista que carga las clases de transporte
     */
    private RegistroDataModelImpl listaClaseTransporte;

    /**
     * Lista que carga la ciudad de expedicion del documento del
     * documento
     */
    private RegistroDataModelImpl listaCiudadExpedicion;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmComisionesControlador
     */
    public FrmComisionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        bloqueoReporte = false;
        visibleUsuario = false;

        try {
            numFormulario = 1922;
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
        cargarListaTipoSolicitud();
        cargarListaDependencia();
        cargarListaTipoVisita();
        cargarListaTercero();
        cargarListaBanco();
        cargarListaClaseTransporte();
        cargarListaCiudadExpedicion();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaPaisDestino();
        cargarListaPaisOrigen();
        cargarListaPaisRegreso();

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
        enumBase = GenericUrlEnum.VI_VIATICOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
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
     * Carga la lista listaCiudadeDestino
     *
     */
    public void cargarListaCiudadeDestino() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS",
                        registro.getCampos().get(
                                        FrmComisionesControladorEnum.PAIS_DESTINO
                                                        .getValue()));
        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos()
                                        .get("DEPARTAMENTO_DESTINO"));

        try {
            listaCiudadeDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL8277
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
     * Carga la lista listaTVisita
     *
     */
    public void cargarListaTipoVisita() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL8275
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoVisita = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODTVISITA");

    }

    /**
     * 
     * Carga la lista listaPaisDestino
     *
     */
    public void cargarListaPaisDestino() {
        try {
            listaPaisDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL9022
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaDepartamentoDestino
     *
     */
    public void cargarListaDepartamentoDestino() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get(
                        FrmComisionesControladorEnum.PAIS_DESTINO.getValue()));

        try {
            listaDepartamentoDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL9123
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
     * Carga la lista listaTsolicitud
     *
     */
    public void cargarListaTipoSolicitud() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL16052
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmComisionesControladorEnum.CODTIPO.getValue());
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL6545
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ROWNUM");
    }

    /**
     * 
     * Carga la lista listaBanco
     *
     */
    public void cargarListaBanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL8784
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.BANCO.getName());
    }

    /**
     * 
     * Carga la lista listaClaseTransporte
     *
     */
    public void cargarListaClaseTransporte() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL1524
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaClaseTransporte = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaciudadExpedicion
     *
     */
    public void cargarListaCiudadExpedicion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL4247
                                                        .getValue());

        listaCiudadExpedicion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCiudadOrigen
     *
     */
    public void cargarListaCiudadOrigen() {
        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get(
                        FrmComisionesControladorEnum.PAIS_ORIGEN.getValue()));

        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO_ORIGEN"));

        try {
            listaCiudadOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL8277
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
     * Carga la lista listaPaisOrigen
     *
     */
    public void cargarListaPaisOrigen() {
        try {
            listaPaisOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL9122
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaPaisRegreso
     *
     */
    public void cargarListaPaisRegreso() {
        try {
            listaPaisRegreso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL4562
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaDepartamentoRegreso
     *
     */
    public void cargarListaDepartamentoRegreso() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_REGRESO"));

        try {
            listaDepartamentoRegreso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL4521
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
     * Carga la lista listaCiudadRegreso
     *
     */
    public void cargarListaCiudadRegreso() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get("PAIS_REGRESO"));

        param.put(FrmViaticosControladorEnum.DEPARTAMENTO.getValue(),
                        registro.getCampos().get("DEPARTAMENTO_REGRESO"));

        try {
            listaCiudadRegreso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL4621
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
     * Carga la lista listaDepartamentoOrigen
     *
     */
    public void cargarListaDepartamentoOrigen() {

        Map<String, Object> param = new TreeMap<>();

        param.put("PAIS", registro.getCampos().get(
                        FrmComisionesControladorEnum.PAIS_ORIGEN.getValue()));

        try {
            listaDepartamentoOrigen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL9123
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
     * Carga la lista listaDependencia
     *
     */
    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmComisionesControladorUrlEnum.URL6646
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ROWNUM");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PaisDestino
     * 
     */
    public void cambiarPaisDestino() {
        // <CODIGO_DESARROLLADO>
        cargarListaDepartamentoDestino();
        cargarListaCiudadeDestino();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoDestino
     * 
     */
    public void cambiarDepartamentoDestino() {
        // <CODIGO_DESARROLLADO>
        cargarListaCiudadeDestino();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PaisOrigen
     * 
     */
    public void cambiarPaisOrigen() {
        // <CODIGO_DESARROLLADO>
        cargarListaDepartamentoOrigen();
        cargarListaCiudadOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoOrigen
     * 
     */
    public void cambiarDepartamentoOrigen() {
        // <CODIGO_DESARROLLADO>
        cargarListaCiudadOrigen();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TipoComision
     */
    public void cambiarTipoComision() {

        if ("2".equals(registro.getCampos().get("TIPOCOMISION"))) {

            registro.getCampos().put(
                            FrmComisionesControladorEnum.PAIS_DESTINO
                                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getCodigoPais());

            cargarListaDepartamentoDestino();

            visibleInterior = true;
            visibleMintic = false;

        }
        else {
            visibleInterior = false;
            visibleMintic = true;

            registro.getCampos().put(
                            FrmComisionesControladorEnum.PAIS_DESTINO
                                            .getValue(),
                            "");
        }
    }

    /**
     * Metodo ejecutado al cambiar el control PaisRegreso
     * 
     */
    public void cambiarPaisRegreso() {
        cargarListaDepartamentoRegreso();
        cargarListaCiudadRegreso();
    }

    /**
     * Metodo ejecutado al cambiar el control DepartamentoRegreso
     * 
     */
    public void cambiarDepartamentoRegreso() {
        cargarListaCiudadRegreso();
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicio
     * 
     */
    public void cambiarFechaInicio() {
        int anioFechaSolicitud = SysmanFunciones.ano((Date) registro.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName()));
        int anioFechaInicio = SysmanFunciones.ano((Date) registro.getCampos()
                        .get(FrmViaticosControladorEnum.FECHAINICIO
                                        .getValue()));

        if (anioFechaInicio < anioFechaSolicitud) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3972"));
            registro.getCampos()
                            .put(FrmViaticosControladorEnum.FECHAINICIO
                                            .getValue(), null);
        }

        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFin
     * 
     */
    public void cambiarFechaFin() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptSabado
     * 
     */
    public void cambiarOptSabado() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptDomingo
     * 
     */
    public void cambiarOptDomingo() {
        cargarDiasHabiles();
    }

    /**
     * Metodo ejecutado al cambiar el control OptFestivo
     * 
     */
    public void cambiarOptFestivo() {
        cargarDiasHabiles();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                                        .getValue()));

        registro.getCampos().put("JEFE_DEPENDENCIA",
                        registroAux.getCampos().get("RESPONSABLE"));

        registro.getCampos().put("SUCURSAL_JEFE",
                        registroAux.getCampos().get("SUCURSAL_RESPONSABLE"));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBRERESPONSABLE
                                        .getValue(),
                        registroAux.getCampos().get("NOMBRE_RESPONSABLE"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoVisita
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoVisita(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TVISITA",
                        registroAux.getCampos().get("CODTVISITA"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoSolicitud
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TSOLICITUD",
                        registroAux.getCampos().get("CODTIPO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TERCERO", registroAux.getCampos().get("NIT"));

        registro.getCampos().put("SUCURSAL",
                        registroAux.getCampos().get("SUCURSAL"));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBRETERCERO.getValue(),
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.DEPENDENCIA
                                                        .getName()));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                        .getValue(),
                        registroAux.getCampos().get(
                                        FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                                        .getValue()));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBRERESPONSABLE
                                        .getValue(),
                        registroAux.getCampos().get("NOMBRE_RESPONSABLE"));

        registro.getCampos().put("SUCURSAL_JEFE",
                        registroAux.getCampos().get("SUCURSAL_RESPONSABLE"));

        registro.getCampos().put("JEFE_DEPENDENCIA",
                        registroAux.getCampos().get("RESPONSABLE"));

        registro.getCampos().put("FECHA_NACIMIENTO",
                        registroAux.getCampos().get("FECHANACIMIENTO"));

        registro.getCampos().put("CIUDAD_EXPE",
                        registroAux.getCampos().get("CIUDAD"));

        registro.getCampos().put("NOMBRE_EXPE",
                        registroAux.getCampos().get("EXPEDIDACEDULA"));

        registro.getCampos().put(GeneralParameterEnum.BANCO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.BANCO.getName()));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBREBANCO.getValue(),
                        registroAux.getCampos().get(
                                        FrmComisionesControladorEnum.NOMBREBANCO
                                                        .getValue()));

        registro.getCampos().put("CTA_BANCARIA",
                        registroAux.getCampos().get("CUENTA"));

        registro.getCampos().put("TELEFONOS",
                        registroAux.getCampos().get("TELEFONOS"));

        registro.getCampos().put("CORREO_ELECTRONICO",
                        registroAux.getCampos().get("DIRECCIONEMAIL"));

        registro.getCampos().put("SALARIO_COMISIONADO",
                        registroAux.getCampos().get("SALARIO_BASE_IBC"));

        JsfUtil.agregarMensajeAlertaDialogo(
                        "Debe validar esta información y en caso de requerir actualizarla o modificarla debe acercase a Talento Humano");

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaBanco
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.BANCO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.BANCO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseTransporte
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseTransporte(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CLASE_TRANSPORTE",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put(
                        FrmComisionesControladorEnum.NOMBRECLASETRANSPORTE
                                        .getValue(),
                        registroAux.getCampos().get("DESCRIPCION"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCiudadExpedicion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiudadExpedicion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CIUDAD_EXPE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registro.getCampos().put("DEPARTAMENTO_EXPE",
                        registroAux.getCampos().get("CODIGOD"));

        registro.getCampos().put("PAIS_EXPE",
                        registroAux.getCampos().get("CODIGOP"));

        registro.getCampos().put(
                        "NOMBRE_EXPE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Reporte en la vista
     *
     */
    public void oprimirReporte() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {

        if (css != null) {

            Map<String, Object> reemplazos = new TreeMap<>();
            Map<String, Object> parametros = new TreeMap<>();
            try {
                reemplazos.put("compania", compania);
                reemplazos.put("solicitud", registro.getCampos().get(
                                FrmViaticosControladorEnum.CODSOLICITUD
                                                .getValue()));

                parametros.put("PR_JEFE_DEPENDENCIA",
                                registro.getCampos().get("NOMBRERESPONSABLE"));

                Reporteador.resuelveConsulta("001933SolicitudComision",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001933SolicitudComision", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

    }

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
        try {
            precargarRegistro();
            diasHabiles = "";

            Valor = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE MODIFICAR JEFE DEPENDENCIA EN SOLICITUD DE COMISION",
                            SessionUtil.getModulo(), new Date(),
                            false);

            editarJefeD = Valor.equals("SI");

            usuario = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                            compania,
                            "NO PERMITE MANEJAR CTA BANCARIA Y SALARIO EN COMISION",
                            SessionUtil.getModulo(), new Date(),
                            false), "").toString();
            if (usuario
                            .contains(SessionUtil.getUser().getCodigo())) {

                visibleUsuario = true;

            }
            else if (usuario
                            .contains(SessionUtil
                                            .getGrupo(SessionUtil.getModulo())
                                            .getCodigo())) {
                visibleUsuario = true;
            }

            bloqueoReporte = false;
            if (accion.equals(ACCION_INSERTAR)) {

                registro.getCampos().put(
                                FrmComisionesControladorEnum.FECHAINICIO
                                                .getValue(),
                                new Date());

                registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                                new Date());

                registro.getCampos().put("ESTADO", "P");

                registro.getCampos().put(
                                FrmComisionesControladorEnum.PAIS_ORIGEN
                                                .getValue(),
                                SessionUtil.getCompaniaIngreso()
                                                .getCodigoPais());

                cargarListaDepartamentoOrigen();

                registro.getCampos().put("DEPARTAMENTO_ORIGEN",
                                SessionUtil.getCompaniaIngreso()
                                                .getCodigoDepartamento());

                cargarListaCiudadOrigen();

                registro.getCampos().put("CIUDAD_ORIGEN",
                                SessionUtil.getCompaniaIngreso()
                                                .getCodigoCiudad());

                bloqueoReporte = true;

            }
            else {

                cargarListaDepartamentoOrigen();
                cargarListaCiudadOrigen();

                cargarListaDepartamentoDestino();
                cargarListaCiudadeDestino();

                cargarListaDepartamentoRegreso();
                cargarListaCiudadRegreso();
                cargarDiasHabiles();

                if ("2".equals(registro.getCampos().get("TIPOCOMISION")
                                .toString())) {

                    visibleInterior = true;
                    visibleMintic = false;
                }
                else {
                    visibleInterior = false;
                    visibleMintic = true;
                }
            }
        }
        catch (SystemException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().remove(
                        FrmComisionesControladorEnum.NOMBRE_EXPE.getValue());

        registro.getCampos()
                        .remove(FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                        .getValue());

        registro.getCampos().remove(
                        FrmComisionesControladorEnum.NOMBRETERCERO.getValue());

        registro.getCampos()
                        .remove(FrmComisionesControladorEnum.NOMBRERESPONSABLE
                                        .getValue());

        registro.getCampos()
                        .remove(FrmComisionesControladorEnum.NOMBRECLASETRANSPORTE
                                        .getValue());

        registro.getCampos().remove(
                        FrmComisionesControladorEnum.NOMBREBANCO.getValue());

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano((Date) registro.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName())));

        registro.getCampos().put(
                        FrmViaticosControladorEnum.NUMSOLICITUD.getValue(),
                        generarConsecutivoNumSolicitud());

        registro.getCampos().put(
                        FrmViaticosControladorEnum.CODSOLICITUD.getValue(),
                        generarConsecutivoCodSolicitud());

        registro.getCampos().put("ESTADO", "P");

        registro.getCampos().put("ESTADO_MINTIC", "P");

        registro.getCampos().put("TIPO_VIATICO", "1");

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
     */
    @Override
    public boolean actualizarAntes() {
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());

            registro.getCampos().remove(
                            FrmComisionesControladorEnum.NOMBREDEPENDENCIA
                                            .getValue());

            registro.getCampos()
                            .remove(FrmComisionesControladorEnum.NOMBRETERCERO
                                            .getValue());

            registro.getCampos().remove(
                            FrmComisionesControladorEnum.NOMBRERESPONSABLE
                                            .getValue());

            registro.getCampos().remove(FrmComisionesControladorEnum.NOMBREBANCO
                            .getValue());

            registro.getCampos().remove(
                            FrmComisionesControladorEnum.NOMBRECLASETRANSPORTE
                                            .getValue());

            registro.getCampos().remove(FrmComisionesControladorEnum.NOMBRE_EXPE
                            .getValue());
        }
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
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private Object generarConsecutivoNumSolicitud() {
        long numeroSolicitud = 0;

        try {
            numeroSolicitud = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "VI_VIATICOS", "COMPANIA = " + compania,
                            FrmViaticosControladorEnum.NUMSOLICITUD.getValue());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return numeroSolicitud;
    }

    private Object generarConsecutivoCodSolicitud() {
        long codigoSolicitud = 0;

        String manejaConsecutivo;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {

            Registro regCodSolicitud = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmComisionesControladorUrlEnum.URL8276
                                                                            .getValue())
                                            .getUrl(), param));

            if (regCodSolicitud != null) {

                manejaConsecutivo = ejbSysmanUtil.consultarParametro(compania,
                                "MANEJA CONSECUTIVO INICIAL", modulo,
                                new Date(),
                                false);
                if ("SI".equals(manejaConsecutivo)) {
                    codigoSolicitud = Long
                                    .parseLong(ejbSysmanUtil.consultarParametro(
                                                    compania,
                                                    "CONSECUTIVO INICIAL DE SOLICITUD",
                                                    modulo,
                                                    new Date(),
                                                    false));
                }
                else {
                    codigoSolicitud = ejbSysmanUtil.generarSiguienteConsecutivo(
                                    "VI_VIATICOS", "COMPANIA = " + compania,
                                    FrmViaticosControladorEnum.CODSOLICITUD
                                                    .getValue());
                }
            }
            else {

                codigoSolicitud = Long.parseLong(SysmanFunciones.ano(new Date())
                    + SysmanFunciones.padl("1", 6, "0"));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return codigoSolicitud;
    }

    private boolean validarFechasVacias() {
        if (registro.getCampos()
                        .get(FrmViaticosControladorEnum.FECHAFIN
                                        .getValue()) == null
            || registro.getCampos().get(
                            FrmViaticosControladorEnum.FECHAINICIO
                                            .getValue()) == null) {

            diasHabiles = "0";
            return true;
        }
        return false;
    }

    private boolean validarFechas() {

        if (SysmanFunciones.comparaFechas((Date) registro.getCampos()
                        .get(FrmViaticosControladorEnum.FECHAFIN
                                        .getValue()),
                        (Date) registro.getCampos()
                                        .get(FrmViaticosControladorEnum.FECHAINICIO
                                                        .getValue()))) {

            registro.getCampos()
                            .put(FrmViaticosControladorEnum.FECHAFIN
                                            .getValue(), null);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3713"));

            diasHabiles = "0";

            return true;
        }
        return false;

    }

    private void cargarDiasHabiles() {

        if (validarFechasVacias()) {
            return;
        }

        if (validarFechas()) {

            return;
        }

        int numMaxDias = 0;

        boolean sabado = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get("SABADO"), false);

        boolean domingo = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get("DOMINGO"), false);

        boolean festivo = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get("FESTIVO"), false);

        try {

            numMaxDias = Integer
                            .parseInt(ejbSysmanUtil.consultarParametro(compania,
                                            "NUMERO MAXIMO DE DIAS DE COMISION",
                                            modulo,
                                            new Date(), false));

            diasHabiles = SysmanFunciones.nvl(
                            ejbSysmanUtil.retornarDiasHabilesViaticos(compania,
                                            (Date) registro.getCampos()
                                                            .get(FrmComisionesControladorEnum.FECHAINICIO
                                                                            .getValue()),
                                            (Date) registro.getCampos()
                                                            .get(FrmComisionesControladorEnum.FECHAFIN
                                                                            .getValue()),
                                            sabado, domingo, festivo),
                            "0").toString();

            if (Integer.parseInt(diasHabiles) > numMaxDias) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3953")
                                .replace("#$diasHabiles#$", diasHabiles));

                diasHabiles = "0";
                registro.getCampos()
                                .put(FrmViaticosControladorEnum.FECHAFIN
                                                .getValue(), null);
            }

            registro.getCampos().put("NODIAS", diasHabiles);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaCiudadeDestino
     * 
     * @return listaCiudadeDestino
     */
    public List<Registro> getListaCiudadeDestino() {
        return listaCiudadeDestino;
    }

    /**
     * Asigna la lista listaCiudadeDestino
     * 
     * @param listaCiudadeDestino
     * Variable a asignar en listaCiudadeDestino
     */
    public void setListaCiudadeDestino(List<Registro> listaCiudadeDestino) {
        this.listaCiudadeDestino = listaCiudadeDestino;
    }

    /**
     * Retorna la lista listaPaisDestino
     * 
     * @return listaPaisDestino
     */
    public List<Registro> getListaPaisDestino() {
        return listaPaisDestino;
    }

    /**
     * Asigna la lista listaPaisDestino
     * 
     * @param listaPaisDestino
     * Variable a asignar en listaPaisDestino
     */
    public void setListaPaisDestino(List<Registro> listaPaisDestino) {
        this.listaPaisDestino = listaPaisDestino;
    }

    /**
     * Retorna la lista listaDepartamentoDestino
     * 
     * @return listaDepartamentoDestino
     */
    public List<Registro> getListaDepartamentoDestino() {
        return listaDepartamentoDestino;
    }

    /**
     * Asigna la lista listaDepartamentoDestino
     * 
     * @param listaDepartamentoDestino
     * Variable a asignar en listaDepartamentoDestino
     */
    public void setListaDepartamentoDestino(
        List<Registro> listaDepartamentoDestino) {
        this.listaDepartamentoDestino = listaDepartamentoDestino;
    }

    /**
     * Retorna la lista listaCiudadOrigen
     * 
     * @return listaCiudadOrigen
     */
    public List<Registro> getListaCiudadOrigen() {
        return listaCiudadOrigen;
    }

    /**
     * Asigna la lista listaCiudadOrigen
     * 
     * @param listaCiudadOrigen
     * Variable a asignar en listaCiudadOrigen
     */
    public void setListaCiudadOrigen(List<Registro> listaCiudadOrigen) {
        this.listaCiudadOrigen = listaCiudadOrigen;
    }

    /**
     * Retorna la lista listaPaisOrigen
     * 
     * @return listaPaisOrigen
     */
    public List<Registro> getListaPaisOrigen() {
        return listaPaisOrigen;
    }

    /**
     * Asigna la lista listaPaisOrigen
     * 
     * @param listaPaisOrigen
     * Variable a asignar en listaPaisOrigen
     */
    public void setListaPaisOrigen(List<Registro> listaPaisOrigen) {
        this.listaPaisOrigen = listaPaisOrigen;
    }

    /**
     * Retorna la lista listaDepartamentoOrigen
     * 
     * @return listaDepartamentoOrigen
     */
    public List<Registro> getListaDepartamentoOrigen() {
        return listaDepartamentoOrigen;
    }

    /**
     * Asigna la lista listaDepartamentoOrigen
     * 
     * @param listaDepartamentoOrigen
     * Variable a asignar en listaDepartamentoOrigen
     */
    public void setListaDepartamentoOrigen(
        List<Registro> listaDepartamentoOrigen) {
        this.listaDepartamentoOrigen = listaDepartamentoOrigen;
    }

    /**
     * Retorna la lista listaPaisRegreso
     * 
     * @return listaPaisRegreso
     */
    public List<Registro> getListaPaisRegreso() {
        return listaPaisRegreso;
    }

    /**
     * Asigna la lista listaPaisRegreso
     * 
     * @param listaPaisRegreso
     * Variable a asignar en listaPaisRegreso
     */
    public void setListaPaisRegreso(List<Registro> listaPaisRegreso) {
        this.listaPaisRegreso = listaPaisRegreso;
    }

    /**
     * Retorna la lista listaDepartamentoRegreso
     * 
     * @return listaDepartamentoRegreso
     */
    public List<Registro> getListaDepartamentoRegreso() {
        return listaDepartamentoRegreso;
    }

    /**
     * Asigna la lista listaDepartamentoRegreso
     * 
     * @param listaDepartamentoRegreso
     * Variable a asignar en listaDepartamentoRegreso
     */
    public void setListaDepartamentoRegreso(
        List<Registro> listaDepartamentoRegreso) {
        this.listaDepartamentoRegreso = listaDepartamentoRegreso;
    }

    /**
     * Retorna la lista listaCiudadRegreso
     * 
     * @return listaCiudadRegreso
     */
    public List<Registro> getListaCiudadRegreso() {
        return listaCiudadRegreso;
    }

    /**
     * Asigna la lista listaCiudadRegreso
     * 
     * @param listaCiudadRegreso
     * Variable a asignar en listaCiudadRegreso
     */
    public void setListaCiudadRegreso(List<Registro> listaCiudadRegreso) {
        this.listaCiudadRegreso = listaCiudadRegreso;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    /**
     * Retorna la lista listaTipoVisita
     * 
     * @return listaTipoVisita
     */
    public RegistroDataModelImpl getListaTipoVisita() {
        return listaTipoVisita;
    }

    /**
     * Asigna la lista listaTipoVisita
     * 
     * @param listaTipoVisita
     * Variable a asignar en listaTipoVisita
     */
    public void setListaTipoVisita(RegistroDataModelImpl listaTipoVisita) {
        this.listaTipoVisita = listaTipoVisita;
    }

    /**
     * Retorna la lista listaTipoSolicitud
     * 
     * @return listaTipoSolicitud
     */
    public RegistroDataModelImpl getListaTipoSolicitud() {
        return listaTipoSolicitud;
    }

    /**
     * Asigna la lista listaTipoSolicitud
     * 
     * @param listaTipoSolicitud
     * Variable a asignar en listaTipoSolicitud
     */
    public void setListaTipoSolicitud(
        RegistroDataModelImpl listaTipoSolicitud) {
        this.listaTipoSolicitud = listaTipoSolicitud;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaBanco
     * 
     * @return listaBanco
     */
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    /**
     * Asigna la lista listaBanco
     * 
     * @param listaBanco
     * Variable a asignar en listaBanco
     */
    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    /**
     * Retorna la lista listaClaseTransporte
     * 
     * @return listaClaseTransporte
     */
    public RegistroDataModelImpl getListaClaseTransporte() {
        return listaClaseTransporte;
    }

    /**
     * Asigna la lista listaClaseTransporte
     * 
     * @param listaClaseTransporte
     * Variable a asignar en listaClaseTransporte
     */
    public void setListaClaseTransporte(
        RegistroDataModelImpl listaClaseTransporte) {
        this.listaClaseTransporte = listaClaseTransporte;
    }

    /**
     * Retorna la lista listaCiudadExpedicion
     * 
     * @return listaCiudadExpedicion
     */
    public RegistroDataModelImpl getListaCiudadExpedicion() {
        return listaCiudadExpedicion;
    }

    /**
     * Asigna la lista listaCiudadExpedicion
     * 
     * @param listaCiudadExpedicion
     * Variable a asignar en listaCiudadExpedicion
     */
    public void setListaCiudadExpedicion(
        RegistroDataModelImpl listaCiudadExpedicion) {
        this.listaCiudadExpedicion = listaCiudadExpedicion;
    }

    public String getDiasHabiles() {
        return diasHabiles;
    }

    public void setDiasHabiles(String diasHabiles) {
        this.diasHabiles = diasHabiles;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public boolean isBloqueoReporte() {
        return bloqueoReporte;
    }

    public void setBloqueoReporte(boolean bloqueoReporte) {
        this.bloqueoReporte = bloqueoReporte;
    }

    public boolean isVisibleInterior() {
        return visibleInterior;
    }

    public void setVisibleInterior(boolean visibleInterior) {
        this.visibleInterior = visibleInterior;
    }

    public boolean isVisibleMintic() {
        return visibleMintic;
    }

    public void setVisibleMintic(boolean visibleMintic) {
        this.visibleMintic = visibleMintic;
    }

    /**
     * @return the editarJefeD
     */
    public boolean isEditarJefeD() {
        return editarJefeD;
    }

    /**
     * @param editarJefeD
     * the editarJefeD to set
     */
    public void setEditarJefeD(boolean editarJefeD) {
        this.editarJefeD = editarJefeD;
    }

    /**
     * @return the visibleUsuario
     */
    public boolean isVisibleUsuario() {
        return visibleUsuario;
    }

    /**
     * @param visibleUsuario
     * the visibleUsuario to set
     */
    public void setVisibleUsuario(boolean visibleUsuario) {
        this.visibleUsuario = visibleUsuario;
    }

    /**
     * @return the valor
     */
    public String getValor() {
        return Valor;
    }

    /**
     * @param valor
     * the valor to set
     */
    public void setValor(String valor) {
        Valor = valor;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario
     * the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    // </SET_GET_ADICIONALES>
}
