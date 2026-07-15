/*-
 * InfTransaccionesSstControlador.java
 *
 * 1.0
 * 
 * 23/06/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.InfTransaccionesSstControladorEnum;
import com.sysman.hojasdevida.enums.InfTransaccionesSstControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario "Informe de
 * Transacciones" en Access "FRM_INF_TRANSACCIONES_SST", el cual es
 * llamado desde Hojas De Vida / Seguridad Y Salud En El Trabajo /
 * Informes / Informe De Transacciones
 *
 * 
 * @version 1.0, 03/01/2018
 * @author amonroy
 * 
 * Se crean combos de activida, agente, tercero
 * @version 2.0, 23/06/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class InfTransaccionesSstControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por la que se ingresa a la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el Codigo de "Tipo Transaccion Inicial"
     * que ha sido seleccionado en el formulario
     */
    private String tipoTransaccionInicial;
    /**
     * Atributo que almacena el Codigo de "Clase Evento Inicial" que
     * ha sido seleccionado en el formulario
     */
    private String claseEventoInicial;
    /**
     * Atributo que almacena el Codigo de "Tipo Transaccion Final" que
     * ha sido seleccionado en el formulario
     */
    private String tipoTransaccionFinal;
    /**
     * Atributo que almacena el Codigo de "Tercero Inicial" que ha
     * sido seleccionado en el formulario
     */
    private String terceroInicial;
    /**
     * Atributo que almacena el Codigo de "Tercero Final" que ha sido
     * seleccionado en el formulario
     */
    private String terceroFinal;
    /**
     * Atributo que almacena el Codigo de "Clase Evento Final" que ha
     * sido seleccionado en el formulario
     */
    private String claseEventoFinal;
    /**
     * Atributo que almacena el Codigo de "Actividad Inicial" que ha
     * sido seleccionado en el formulario
     */
    private String actividadInicial;
    /**
     * Atributo que almacena el Codigo de "Actividad Final" que ha
     * sido seleccionado en el formulario
     */
    private String actividadFinal;
    /**
     * Atributo que almacena el Codigo de "Agente Inicial" que ha sido
     * seleccionado en el formulario
     */
    private String agenteInicial;
    /**
     * Atributo que almacena el Codigo de "Agente Final" que ha sido
     * seleccionado en el formulario
     */
    private String agenteFinal;
    /**
     * Atributo que almacena la feha inicial con la que se desea
     * generar el informe y que ha sido seleccionada en el formulario
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la feha final con la que se desea generar
     * el informe y que ha sido seleccionada en el formulario
     */
    private Date fechaFinal;
    /**
     * Atributo que almacena el nombre de la transaccion inicial
     * seleccionada
     */
    private String nombreTransaccionInicial;
    /**
     * Atributo que almacena el nombre de la transaccion final
     * seleccionada
     */
    private String nombreTransaccionFinal;
    /**
     * Atributo que almacena el nombre del tercero inicial
     * seleccionado
     */
    private String nombreTerceroInicial;
    /**
     * Atributo que almacena el nombre del tercero final seleccionado
     */
    private String nombreTerceroFinal;
    /**
     * Atributo que almacena el nombre de la clase evento inicial
     * seleccionada
     */
    private String nombreClaseEventoInicial;
    /**
     * Atributo que almacena el nombre de la clase evento final
     * seleccionada
     */
    private String nombreClaseEventoFinal;
    /**
     * Atributo que almacena el nombre de la actividad inicial
     * seleccionada
     */
    private String nombreActividadInicial;
    /**
     * Atributo que almacena le nombre de la actividad final
     * seleccionada
     */
    private String nombreActividadFinal;
    /**
     * Atributo que almacena el nombre del agente inicial seleccionado
     */
    private String nombreAgenteInicial;
    /**
     * Atributo que almacena el nombre del agente final seleccionado
     */
    private String nombreAgenteFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo de "Tipo Transaccion
     * Inicial"
     */
    private RegistroDataModelImpl listaTipoTransaccionInicial;
    /**
     * Listado de registros para el combo de "Clase evento Inicial"
     */
    private RegistroDataModelImpl listaClaseEventoInicial;
    /**
     * Listado de registros para el combo de "Tipo Transaccion Final"
     */
    private RegistroDataModelImpl listaTipoTransaccionFinal;
    /**
     * Listado de registros para el combo de "Tercero Inicial"
     */
    private RegistroDataModelImpl listaTerceroInicial;
    /**
     * Listado de registros para el combo de "Tercero Final"
     */
    private RegistroDataModelImpl listaTerceroFinal;
    /**
     * Listado de registros para el combo de "Clase evento final"
     */
    private RegistroDataModelImpl listaClaseEventoFinal;
    /**
     * Listado de registros para el combo de "Actividad Inicial"
     */
    private RegistroDataModelImpl listaActividadInicial;
    /**
     * Listado de registros para el combo de "Actividad final"
     */
    private RegistroDataModelImpl listaActividadFinal;
    /**
     * Listado de registros para el combo de "Agente Inicial"
     */
    private RegistroDataModelImpl listaAgenteInicial;
    /**
     * Listado de registros para el combo de "Agente Final"
     */
    private RegistroDataModelImpl listaAgenteFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de InfTransaccionesSstControlador
     */
    public InfTransaccionesSstControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            // 1574
            numFormulario = GeneralCodigoFormaEnum.INF_TRANSACCIONES_SST_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
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
    public void iniciarListasSubNulo()
    {
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
    public void inicializar()
    {
        cargarListaTipoTransaccionInicial();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos()
    {
        origenDatos = "";
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoTransaccionInicial
     *
     */
    public void cargarListaTipoTransaccionInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL4138
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoTransaccionInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaClaseEventoInicial
     *
     */
    public void cargarListaClaseEventoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL4974
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);

        try
        {
            listaClaseEventoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SST_CLASE_EVENTO.getTable()));
        }
        catch (SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaTipoTransaccionFinal
     *
     */
    public void cargarListaTipoTransaccionFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL377
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);

        listaTipoTransaccionFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTerceroInicial
     *
     */
    public void cargarListaTerceroInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL395
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, InfTransaccionesSstControladorEnum.CEDULA.getValue());

    }

    /**
     * 
     * Carga la lista listaTerceroFinal
     *
     */
    public void cargarListaTerceroFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL415
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TERCEROINICIAL.getValue(), terceroInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, InfTransaccionesSstControladorEnum.CEDULA.getValue());
    }

    /**
     * 
     * Carga la lista listaClaseEventoFinal
     *
     */
    public void cargarListaClaseEventoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL435
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);
        param.put(InfTransaccionesSstControladorEnum.CLASEEVENTOINICIAL.getValue(), claseEventoInicial);

        try
        {
            listaClaseEventoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SST_CLASE_EVENTO.getTable()));
        }
        catch (SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaActividadInicial
     *
     */
    public void cargarListaActividadInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL455
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);

        try
        {
            listaActividadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SST_TRANSACCION_ACTIVIDAD.getTable()));
        }
        catch (SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaActividadFinal
     *
     */
    public void cargarListaActividadFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL474
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);
        param.put(InfTransaccionesSstControladorEnum.ACTIVIDADINICIAL.getValue(), actividadInicial);

        try
        {
            listaActividadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.SST_TRANSACCION_ACTIVIDAD.getTable()));
        }
        catch (SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaAgenteInicial
     *
     */
    public void cargarListaAgenteInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL495
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);
        listaAgenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAgenteFinal
     *
     */
    public void cargarListaAgenteFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfTransaccionesSstControladorUrlEnum.URL515
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfTransaccionesSstControladorEnum.TIPOINICIAL.getValue(), tipoTransaccionInicial);
        param.put(InfTransaccionesSstControladorEnum.TIPOFINAL.getValue(), tipoTransaccionFinal);
        param.put(InfTransaccionesSstControladorEnum.AGENTEINICIAL.getValue(), agenteInicial);

        listaAgenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ClaseEventoFinal
     * 
     * 
     */
    public void cambiarClaseEventoFinal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoTransaccionInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoTransaccionInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoTransaccionInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        nombreTransaccionInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        tipoTransaccionFinal = null;
        nombreTransaccionFinal = null;
        terceroInicial = null;
        terceroFinal = null;
        nombreTerceroInicial = null;
        nombreTerceroFinal = null;
        claseEventoInicial = null;
        claseEventoFinal = null;
        nombreClaseEventoInicial = null;
        nombreClaseEventoFinal = null;
        actividadInicial = null;
        actividadFinal = null;
        nombreActividadInicial = null;
        nombreActividadFinal = null;
        agenteInicial = null;
        agenteFinal = null;
        nombreAgenteInicial = null;
        nombreAgenteFinal = null;
        cargarListaTipoTransaccionFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseEventoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseEventoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        claseEventoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        nombreClaseEventoInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        claseEventoFinal = null;
        nombreClaseEventoFinal = null;
        cargarListaClaseEventoFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoTransaccionFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoTransaccionFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoTransaccionFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString(), "")
                        .toString();
        nombreTransaccionFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        terceroInicial = null;
        terceroFinal = null;
        nombreTerceroInicial = null;
        nombreTerceroFinal = null;
        claseEventoInicial = null;
        claseEventoFinal = null;
        nombreClaseEventoInicial = null;
        nombreClaseEventoFinal = null;
        actividadInicial = null;
        actividadFinal = null;
        nombreActividadInicial = null;
        nombreActividadFinal = null;
        agenteInicial = null;
        agenteFinal = null;
        nombreAgenteInicial = null;
        nombreAgenteFinal = null;
        cargarListaTerceroInicial();
        cargarListaClaseEventoInicial();
        cargarListaActividadInicial();
        cargarListaAgenteInicial();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = SysmanFunciones.nvl(registroAux.getCampos().get(InfTransaccionesSstControladorEnum.CEDULA.getValue()), "")
                        .toString();
        nombreTerceroInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        terceroFinal = null;
        nombreTerceroFinal = null;
        cargarListaTerceroFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTerceroFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get(InfTransaccionesSstControladorEnum.CEDULA.getValue()), "")
                        .toString();
        nombreTerceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseEventoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseEventoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        claseEventoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        nombreClaseEventoFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividadInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividadInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        actividadInicial = SysmanFunciones.nvl(registroAux.getCampos().get(InfTransaccionesSstControladorEnum.ACTIVIDAD.getValue()), "")
                        .toString();
        nombreActividadInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        actividadFinal = null;
        nombreActividadFinal = null;
        cargarListaActividadFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividadFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividadFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        actividadFinal = SysmanFunciones.nvl(registroAux.getCampos().get(InfTransaccionesSstControladorEnum.ACTIVIDAD.getValue()), "")
                        .toString();
        nombreActividadFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAgenteInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAgenteInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        agenteInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        nombreAgenteInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
        agenteFinal = null;
        nombreAgenteFinal = null;
        cargarListaAgenteFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAgenteFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAgenteFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        agenteFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
        nombreAgenteFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        try
        {
            String informe = "001610InfSstTransacciones";
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("tipoTransaccionInicial", tipoTransaccionInicial);
            reemplazar.put("tipoTransaccionFinal", tipoTransaccionFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);
            reemplazar.put("claseEventoInicial", claseEventoInicial);
            reemplazar.put("claseEventoFinal", claseEventoFinal);

            reemplazar.put("fechaInicial",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));

            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            String condicionActividad = "";
            if (!(actividadInicial.isEmpty() && actividadFinal.isEmpty()))
            {
                condicionActividad = SysmanFunciones.concatenar("AND SST_TRANSACCION_ACTIVIDAD.ACTIVIDAD BETWEEN '", actividadInicial,
                                "' AND '",
                                actividadFinal, "'");
            }
            reemplazar.put("condicionActividad", condicionActividad);
            String condicionAgente = "";
            if (!(agenteInicial.isEmpty() && agenteFinal.isEmpty()))
            {
                condicionAgente = SysmanFunciones.concatenar(" AND SST_TRANSACCIONES.AGENTE BETWEEN '", agenteInicial, "' AND '",
                                agenteFinal,
                                "'");

            }
            reemplazar.put("condicionAgente", condicionAgente);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            reemplazar.put("consultaBase", Reporteador.resuelveConsulta(
                            informe,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo),
                            reemplazar,
                            parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (ParseException | IOException | SysmanException | JRException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoTransaccionInicial
     * 
     * @return tipoTransaccionInicial
     */
    public String getTipoTransaccionInicial()
    {
        return tipoTransaccionInicial;
    }

    /**
     * Asigna la variable tipoTransaccionInicial
     * 
     * @param tipoTransaccionInicial
     * Variable a asignar en tipoTransaccionInicial
     */
    public void setTipoTransaccionInicial(String tipoTransaccionInicial)
    {
        this.tipoTransaccionInicial = tipoTransaccionInicial;
    }

    /**
     * Retorna la variable claseEventoInicial
     * 
     * @return claseEventoInicial
     */
    public String getClaseEventoInicial()
    {
        return claseEventoInicial;
    }

    /**
     * Asigna la variable claseEventoInicial
     * 
     * @param claseEventoInicial
     * Variable a asignar en claseEventoInicial
     */
    public void setClaseEventoInicial(String claseEventoInicial)
    {
        this.claseEventoInicial = claseEventoInicial;
    }

    /**
     * Retorna la variable tipoTransaccionFinal
     * 
     * @return tipoTransaccionFinal
     */
    public String getTipoTransaccionFinal()
    {
        return tipoTransaccionFinal;
    }

    /**
     * Asigna la variable tipoTransaccionFinal
     * 
     * @param tipoTransaccionFinal
     * Variable a asignar en tipoTransaccionFinal
     */
    public void setTipoTransaccionFinal(String tipoTransaccionFinal)
    {
        this.tipoTransaccionFinal = tipoTransaccionFinal;
    }

    /**
     * Retorna la variable terceroInicial
     * 
     * @return terceroInicial
     */
    public String getTerceroInicial()
    {
        return terceroInicial;
    }

    /**
     * Asigna la variable terceroInicial
     * 
     * @param terceroInicial
     * Variable a asignar en terceroInicial
     */
    public void setTerceroInicial(String terceroInicial)
    {
        this.terceroInicial = terceroInicial;
    }

    /**
     * Retorna la variable terceroFinal
     * 
     * @return terceroFinal
     */
    public String getTerceroFinal()
    {
        return terceroFinal;
    }

    /**
     * Asigna la variable terceroFinal
     * 
     * @param terceroFinal
     * Variable a asignar en terceroFinal
     */
    public void setTerceroFinal(String terceroFinal)
    {
        this.terceroFinal = terceroFinal;
    }

    /**
     * Retorna la variable claseEventoFinal
     * 
     * @return claseEventoFinal
     */
    public String getClaseEventoFinal()
    {
        return claseEventoFinal;
    }

    /**
     * Asigna la variable claseEventoFinal
     * 
     * @param claseEventoFinal
     * Variable a asignar en claseEventoFinal
     */
    public void setClaseEventoFinal(String claseEventoFinal)
    {
        this.claseEventoFinal = claseEventoFinal;
    }

    /**
     * Retorna la variable actividadInicial
     * 
     * @return actividadInicial
     */
    public String getActividadInicial()
    {
        return actividadInicial;
    }

    /**
     * Asigna la variable actividadInicial
     * 
     * @param actividadInicial
     * Variable a asignar en actividadInicial
     */
    public void setActividadInicial(String actividadInicial)
    {
        this.actividadInicial = actividadInicial;
    }

    /**
     * Retorna la variable actividadFinal
     * 
     * @return actividadFinal
     */
    public String getActividadFinal()
    {
        return actividadFinal;
    }

    /**
     * Asigna la variable actividadFinal
     * 
     * @param actividadFinal
     * Variable a asignar en actividadFinal
     */
    public void setActividadFinal(String actividadFinal)
    {
        this.actividadFinal = actividadFinal;
    }

    /**
     * Retorna la variable agenteInicial
     * 
     * @return agenteInicial
     */
    public String getAgenteInicial()
    {
        return agenteInicial;
    }

    /**
     * Asigna la variable agenteInicial
     * 
     * @param agenteInicial
     * Variable a asignar en agenteInicial
     */
    public void setAgenteInicial(String agenteInicial)
    {
        this.agenteInicial = agenteInicial;
    }

    /**
     * Retorna la variable agenteFinal
     * 
     * @return agenteFinal
     */
    public String getAgenteFinal()
    {
        return agenteFinal;
    }

    /**
     * Asigna la variable agenteFinal
     * 
     * @param agenteFinal
     * Variable a asignar en agenteFinal
     */
    public void setAgenteFinal(String agenteFinal)
    {
        this.agenteFinal = agenteFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable nombreTransaccionInicial
     * 
     * @return nombreTransaccionInicial
     */
    public String getNombreTransaccionInicial()
    {
        return nombreTransaccionInicial;
    }

    /**
     * Asigna la variable nombreTransaccionInicial
     * 
     * @param nombreTransaccionInicial
     * Variable a asignar en nombreTransaccionInicial
     */
    public void setNombreTransaccionInicial(String nombreTransaccionInicial)
    {
        this.nombreTransaccionInicial = nombreTransaccionInicial;
    }

    /**
     * Retorna la variable nombreTransaccionFinal
     * 
     * @return nombreTransaccionFinal
     */
    public String getNombreTransaccionFinal()
    {
        return nombreTransaccionFinal;
    }

    /**
     * Asigna la variable nombreTransaccionFinal
     * 
     * @param nombreTransaccionFinal
     * Variable a asignar en nombreTransaccionFinal
     */
    public void setNombreTransaccionFinal(String nombreTransaccionFinal)
    {
        this.nombreTransaccionFinal = nombreTransaccionFinal;
    }

    /**
     * Retorna la variable nombreTerceroInicial
     * 
     * @return nombreTerceroInicial
     */
    public String getNombreTerceroInicial()
    {
        return nombreTerceroInicial;
    }

    /**
     * Asigna la variable nombreTerceroInicial
     * 
     * @param nombreTerceroInicial
     * Variable a asignar en nombreTerceroInicial
     */
    public void setNombreTerceroInicial(String nombreTerceroInicial)
    {
        this.nombreTerceroInicial = nombreTerceroInicial;
    }

    /**
     * Retorna la variable nombreTerceroFinal
     * 
     * @return nombreTerceroFinal
     */
    public String getNombreTerceroFinal()
    {
        return nombreTerceroFinal;
    }

    /**
     * Asigna la variable nombreTerceroFinal
     * 
     * @param nombreTerceroFinal
     * Variable a asignar en nombreTerceroFinal
     */
    public void setNombreTerceroFinal(String nombreTerceroFinal)
    {
        this.nombreTerceroFinal = nombreTerceroFinal;
    }

    /**
     * Retorna la variable nombreClaseEventoInicial
     * 
     * @return nombreClaseEventoInicial
     */
    public String getNombreClaseEventoInicial()
    {
        return nombreClaseEventoInicial;
    }

    /**
     * Asigna la variable nombreClaseEventoInicial
     * 
     * @param nombreClaseEventoInicial
     * Variable a asignar en nombreClaseEventoInicial
     */
    public void setNombreClaseEventoInicial(String nombreClaseEventoInicial)
    {
        this.nombreClaseEventoInicial = nombreClaseEventoInicial;
    }

    /**
     * Retorna la variable nombreClaseEventoFinal
     * 
     * @return nombreClaseEventoFinal
     */
    public String getNombreClaseEventoFinal()
    {
        return nombreClaseEventoFinal;
    }

    /**
     * Asigna la variable nombreClaseEventoFinal
     * 
     * @param nombreClaseEventoFinal
     * Variable a asignar en nombreClaseEventoFinal
     */
    public void setNombreClaseEventoFinal(String nombreClaseEventoFinal)
    {
        this.nombreClaseEventoFinal = nombreClaseEventoFinal;
    }

    /**
     * Retorna la variable nombreActividadInicial
     * 
     * @return nombreActividadInicial
     */
    public String getNombreActividadInicial()
    {
        return nombreActividadInicial;
    }

    /**
     * Asigna la variable nombreActividadInicial
     * 
     * @param nombreActividadInicial
     * Variable a asignar en nombreActividadInicial
     */
    public void setNombreActividadInicial(String nombreActividadInicial)
    {
        this.nombreActividadInicial = nombreActividadInicial;
    }

    /**
     * Retorna la variable nombreActividadFinal
     * 
     * @return nombreActividadFinal
     */
    public String getNombreActividadFinal()
    {
        return nombreActividadFinal;
    }

    /**
     * Asigna la variable nombreActividadFinal
     * 
     * @param nombreActividadFinal
     * Variable a asignar en nombreActividadFinal
     */
    public void setNombreActividadFinal(String nombreActividadFinal)
    {
        this.nombreActividadFinal = nombreActividadFinal;
    }

    /**
     * Retorna la variable nombreAgenteInicial
     * 
     * @return nombreAgenteInicial
     */
    public String getNombreAgenteInicial()
    {
        return nombreAgenteInicial;
    }

    /**
     * Asigna la variable nombreAgenteInicial
     * 
     * @param nombreAgenteInicial
     * Variable a asignar en nombreAgenteInicial
     */
    public void setNombreAgenteInicial(String nombreAgenteInicial)
    {
        this.nombreAgenteInicial = nombreAgenteInicial;
    }

    /**
     * Retorna la variable nombreAgenteFinal
     * 
     * @return nombreAgenteFinal
     */
    public String getNombreAgenteFinal()
    {
        return nombreAgenteFinal;
    }

    /**
     * Asigna la variable nombreAgenteFinal
     * 
     * @param nombreAgenteFinal
     * Variable a asignar en nombreAgenteFinal
     */
    public void setNombreAgenteFinal(String nombreAgenteFinal)
    {
        this.nombreAgenteFinal = nombreAgenteFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoTransaccionInicial
     * 
     * @return listaTipoTransaccionInicial
     */
    public RegistroDataModelImpl getListaTipoTransaccionInicial()
    {
        return listaTipoTransaccionInicial;
    }

    /**
     * Asigna la lista listaTipoTransaccionInicial
     * 
     * @param listaTipoTransaccionInicial
     * Variable a asignar en listaTipoTransaccionInicial
     */
    public void setListaTipoTransaccionInicial(RegistroDataModelImpl listaTipoTransaccionInicial)
    {
        this.listaTipoTransaccionInicial = listaTipoTransaccionInicial;
    }

    /**
     * Retorna la lista listaClaseEventoInicial
     * 
     * @return listaClaseEventoInicial
     */
    public RegistroDataModelImpl getListaClaseEventoInicial()
    {
        return listaClaseEventoInicial;
    }

    /**
     * Asigna la lista listaClaseEventoInicial
     * 
     * @param listaClaseEventoInicial
     * Variable a asignar en listaClaseEventoInicial
     */
    public void setListaClaseEventoInicial(RegistroDataModelImpl listaClaseEventoInicial)
    {
        this.listaClaseEventoInicial = listaClaseEventoInicial;
    }

    /**
     * Retorna la lista listaTipoTransaccionFinal
     * 
     * @return listaTipoTransaccionFinal
     */
    public RegistroDataModelImpl getListaTipoTransaccionFinal()
    {
        return listaTipoTransaccionFinal;
    }

    /**
     * Asigna la lista listaTipoTransaccionFinal
     * 
     * @param listaTipoTransaccionFinal
     * Variable a asignar en listaTipoTransaccionFinal
     */
    public void setListaTipoTransaccionFinal(RegistroDataModelImpl listaTipoTransaccionFinal)
    {
        this.listaTipoTransaccionFinal = listaTipoTransaccionFinal;
    }

    /**
     * Retorna la lista listaTerceroInicial
     * 
     * @return listaTerceroInicial
     */
    public RegistroDataModelImpl getListaTerceroInicial()
    {
        return listaTerceroInicial;
    }

    /**
     * Asigna la lista listaTerceroInicial
     * 
     * @param listaTerceroInicial
     * Variable a asignar en listaTerceroInicial
     */
    public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial)
    {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    /**
     * Retorna la lista listaTerceroFinal
     * 
     * @return listaTerceroFinal
     */
    public RegistroDataModelImpl getListaTerceroFinal()
    {
        return listaTerceroFinal;
    }

    /**
     * Asigna la lista listaTerceroFinal
     * 
     * @param listaTerceroFinal
     * Variable a asignar en listaTerceroFinal
     */
    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal)
    {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    /**
     * Retorna la lista listaClaseEventoFinal
     * 
     * @return listaClaseEventoFinal
     */
    public RegistroDataModelImpl getListaClaseEventoFinal()
    {
        return listaClaseEventoFinal;
    }

    /**
     * Asigna la lista listaClaseEventoFinal
     * 
     * @param listaClaseEventoFinal
     * Variable a asignar en listaClaseEventoFinal
     */
    public void setListaClaseEventoFinal(RegistroDataModelImpl listaClaseEventoFinal)
    {
        this.listaClaseEventoFinal = listaClaseEventoFinal;
    }

    /**
     * Retorna la lista listaActividadInicial
     * 
     * @return listaActividadInicial
     */
    public RegistroDataModelImpl getListaActividadInicial()
    {
        return listaActividadInicial;
    }

    /**
     * Asigna la lista listaActividadInicial
     * 
     * @param listaActividadInicial
     * Variable a asignar en listaActividadInicial
     */
    public void setListaActividadInicial(RegistroDataModelImpl listaActividadInicial)
    {
        this.listaActividadInicial = listaActividadInicial;
    }

    /**
     * Retorna la lista listaActividadFinal
     * 
     * @return listaActividadFinal
     */
    public RegistroDataModelImpl getListaActividadFinal()
    {
        return listaActividadFinal;
    }

    /**
     * Asigna la lista listaActividadFinal
     * 
     * @param listaActividadFinal
     * Variable a asignar en listaActividadFinal
     */
    public void setListaActividadFinal(RegistroDataModelImpl listaActividadFinal)
    {
        this.listaActividadFinal = listaActividadFinal;
    }

    /**
     * Retorna la lista listaAgenteInicial
     * 
     * @return listaAgenteInicial
     */
    public RegistroDataModelImpl getListaAgenteInicial()
    {
        return listaAgenteInicial;
    }

    /**
     * Asigna la lista listaAgenteInicial
     * 
     * @param listaAgenteInicial
     * Variable a asignar en listaAgenteInicial
     */
    public void setListaAgenteInicial(RegistroDataModelImpl listaAgenteInicial)
    {
        this.listaAgenteInicial = listaAgenteInicial;
    }

    /**
     * Retorna la lista listaAgenteFinal
     * 
     * @return listaAgenteFinal
     */
    public RegistroDataModelImpl getListaAgenteFinal()
    {
        return listaAgenteFinal;
    }

    /**
     * Asigna la lista listaAgenteFinal
     * 
     * @param listaAgenteFinal
     * Variable a asignar en listaAgenteFinal
     */
    public void setListaAgenteFinal(RegistroDataModelImpl listaAgenteFinal)
    {
        this.listaAgenteFinal = listaAgenteFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
