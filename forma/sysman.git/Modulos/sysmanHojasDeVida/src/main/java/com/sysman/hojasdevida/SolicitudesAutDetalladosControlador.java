/*-
 * SolicitudesAutDetalladosControlador.java
 *
 * 1.0
 *
 * 05/02/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralAutoservicioEnum;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmConsultasControladorUrlEnum;
import com.sysman.hojasdevida.enums.SolicitudesAutDetalladosControladorEnum;
import com.sysman.hojasdevida.enums.SolicitudesAutDetalladosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.reporte.PrepararReporte;
import com.sysman.util.reporte.RetornoReporte;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario "Solicitudes" en Access "FRMSOLICITUDESDETALLADO", el cual es llamado desde Hojas de Vida\Autoservicio\Gestión Autoservicio\Solicitudes\ Boton Nuevo
 * o Boton Ver
 *
 *
 * @version 1, 06/02/2018 14:28:38 -- Modificado por amonroy
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class SolicitudesAutDetalladosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se relaciona el campo CODIGO en el Controlador
     */
    private final String cCodigo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el numero de solicitud que se esta trabajando, recibe 0 cuando es una nueva solicitud
     */
    private int numeroSolicitud;
    /*
     * Atributo que almacena el Numero de Identificacion Actual del solicitante
     */
    private String nitEmpleado;
    /*
     * Atributo que almacena la sucursal a la cual pertenece el solicitante
     */
    private String sucursalEmpleado;
    /**
     * Atributo que almacena el codigo del cargo al que pertenece el solicitante
     */
    private String idCargo;
    /**
     * Codigo que identifica a la persona que va a realizar la solicitud
     */
    private String idEmpleado;
    /**
     * Email del jefe directo
     */
    private String correoJefe;
    /**
     * email del empleado solicitante
     */
    private String correoSolicitante;
    /**
     * Nombre de la dependencia del solicitante
     */
    private String nombreDependencia;
    /*
     * Atributo que indica el estado actual de a solicitud con la que se esta trabajando
     */
    private String estadoSolicitud;
    /**
     * Define el bloqueo para los campos <code> CB5653, CB5654, CP52143, CP52144, CP52145, CP52146, CP52147 </code> del formulario
     */
    private boolean camposBloqueados;
    /**
     * Indicador que permite definir el bloqueo del campo "Estado" en el formulario
     */
    private boolean estadoBloqueado;

    /**
     * Atributo que toma el valor configurado para la solicitud seleccionada
     *
     * de si aplica o no seleccionar periodo de trabajo
     */

    private boolean bloqueaPeriodo;

    /**
     * Atributo que toma el valor booleano para activar o no controles graficos
     *
     * dependiendo el tipo de solicitud seleccionada.
     */

    private boolean bloqueaConsulta;
    /**
     * Atributo que toma el valor configurado para la solicitud seleccionada
     *
     * para identificar si es como tal solicitud o consulta
     */
    private String tipoSolicitud;

    /*
     * Atributo que indica el proceso de nomina para filtrar el periodo
     */
    private String proceso;
    /*
     * Atributo que almacena el codigo de reporte configurado para la solicitud
     */
    private String reporte;
    /*
     * Atributo que almacen el codigo de plantilla configurada para la solicitud
     */
    private String plantilla;

    private String fecha2;
    /**
     * Variable para saber desde que opcion de menu se accedio al formulario
     * 
     */
    private int porTramitar;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Almacena el valor de los campos llaves de la solicitud con la que se eta trabajando
     */
    private Map<String, Object> llavesSolicitud;
    /**
     * Estructura que almacena la información basica del solicitante y necesaria en el formulario como datos iniciales
     */
    private Registro rsDatosIniciales;
    /**
     * Esta variable determina si el usuario puede guardar o no
     */
    private boolean presionoNotificar;
    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Atributo que indica si el usuario loggeado tiene asignado un jefe directo
     */
    private boolean jefeDirectoAsignado;

    /**
     * Atributo que almacena el valor para enviar al parametro PR_ANO_GRAVABLE
     */
    private String ano;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo que permite listar los años definidos para el proceso uno de nomina
     */
    private List<Registro> listaano;
    /**
     * Atributo que permite listar los meses del año previamente seleccionado
     */
    private List<Registro> listames;
    /**
     * Atributo que permite listar los periodos del año y mes previamente seleccionados
     */
    private List<Registro> listaperiodo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo de Solicitud
     */
    private RegistroDataModelImpl listaSolicitud;
    /**
     * Listado de registros para el combo de Tipo Solicitud
     */
    private RegistroDataModelImpl listaTipoSolicitud;
    /**
     * Listado de registros para el combo de "Tramitado Por"
     */
    private RegistroDataModelImpl listaNombreAprobo;
    /**
     * Mensaje de envio de correo personalizado
     */
    private String mensajeCorreo;
    /**
     * Esta variable permite mostrar o no el campo motivo del rechazo
     */
    private boolean verMotivoRechazo;

    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;
    @EJB
    private EjbNominaCeroGeneralRemote ejbNominaCeroGeneral;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de SolicitudesAutDetalladosControlador
     */
    @SuppressWarnings("unchecked")
    public SolicitudesAutDetalladosControlador() {
        super();
        compania = SessionUtil.getCompania();
        nitEmpleado = SessionUtil.getUser().getCedula();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        proceso = "1";
        idEmpleado = "";
        verMotivoRechazo = false;
        presionoNotificar = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.SOLICITUDESAUT_DETALLADOS_CONTROLADOR
                            .getCodigo();// 1693
            registro = new Registro(new HashMap<String, Object>());
            mensajeCorreo = "";
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                numeroSolicitud = (int) parametrosEntrada
                                .get("numeroSolicitud");
                estadoSolicitud = (String) parametrosEntrada
                                .get("estadoSolicitud");
                bloqueaPeriodo = (boolean) parametrosEntrada
                                .get("bloqueaPeriodo");
                tipoSolicitud = (String) parametrosEntrada
                                .get("tipoSolicitud");
                reporte = (String) parametrosEntrada.get("reporte");
                plantilla = SysmanFunciones.validarCampoVacio(parametrosEntrada, "plantilla") ? null
                                : parametrosEntrada.get("plantilla").toString();
                llavesSolicitud = (Map<String, Object>) parametrosEntrada
                                .get("llavesSolicitud");
                porTramitar = (int) parametrosEntrada.get("porTramitar");

            }
            bloqueaConsulta = false;
            obtenerDatosSolicitante();
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaSolicitud();
        cargarListaTipoSolicitud();
        cargarListaNombreAprobo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaano();
        cargarListames();
        cargarListaperiodo();
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.AUT_SOLICITUDES;
        buscarLlave();
        asignarOrigenDatos();
        iniciarListas();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     * Realiza la busqueda de las URLs para la ejecucion de las operaciones CRUD
     *
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaSolicitud
     *
     */
    public void cargarListaSolicitud() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudesAutDetalladosControladorUrlEnum.URL0002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     *
     * Carga la lista listaTipoSolicitud
     *
     */
    public void cargarListaTipoSolicitud() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudesAutDetalladosControladorUrlEnum.URL0003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        SysmanFunciones.nvl(
                                        registro.getCampos()
                                                        .get(SolicitudesAutDetalladosControladorEnum.CLASE_SOLICITUD
                                                                        .getValue()),
                                        0));

        listaTipoSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        cCodigo);
    }

    /**
     *
     * Carga la lista listaNombreAprobo
     *
     */
    public void cargarListaNombreAprobo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudesAutDetalladosControladorUrlEnum.URL0004
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNombreAprobo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SolicitudesAutDetalladosControladorEnum.ID_DE_CARGO
                                        .getValue());

    }

    public void cargarListaano() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudesAutDetalladosControladorUrlEnum.URL0005
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void cargarListames() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.nvl(
                        registro.getCampos().get(GeneralParameterEnum.ANO.getName()), 0));

        try {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudesAutDetalladosControladorUrlEnum.URL0006
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void cargarListaperiodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.nvl(
                        registro.getCampos().get(GeneralParameterEnum.ANO.getName()), 0));
        param.put(GeneralParameterEnum.MES.getName(), SysmanFunciones.nvl(
                        registro.getCampos().get(GeneralParameterEnum.MES.getName()), 0));

        try {
            listaperiodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudesAutDetalladosControladorUrlEnum.URL0007
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Estado
     *
     * Realiza la validacion del Estado que se ha seleccionado: Asigna la fecha de Aprobacion y el cargo de quien aprueba
     *
     */
    public void cambiarEstado() {
        // <CODIGO_DESARROLLADO>
        if ("T".equals(retornarString(registro,
                        GeneralParameterEnum.ESTADO.getName()))) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3983"));
            return;
        }
        registro.getCampos()
                        .put(SolicitudesAutDetalladosControladorEnum.FECHA_APROBACION
                                        .getValue(), new Date());
        registro.getCampos()
                        .put(SolicitudesAutDetalladosControladorEnum.CARGO_APROBACION
                                        .getValue(),
                                        retornarString(rsDatosIniciales,
                                                        SolicitudesAutDetalladosControladorEnum.JEFE_DIRECTO
                                                                        .getValue()));
        /**
         * En este caso solo se debe mostrar el estado cuando la solicitud este en modo rechazo
         */
        if ("R".equals(registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
            verMotivoRechazo = true;
        }
        else {
            verMotivoRechazo = false;
            registro.getCampos().put("MOTIVORECHAZO", null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarano() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), null);
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
        ano = (String) registro.getCampos().get("ANO");
        cargarListames();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);

        cargarListaperiodo();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaSolicitud
     *
     * Asigna la clase de solicitud seleccionada en el formulario al registro y recarga el listado de "Tipo Solicitud"
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSolicitud(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.CLASE_SOLICITUD.getValue(),
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.TIPO_PERMISO.getValue(), null);
        registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.NOMBRETIPOSOLICITUD.getValue(), null);
        /*
         * En el tipo de solicitud se configura si el tipo requeire o no periodo de trabajo,
         *
         * si se requiere el atributo bloqueaPeriodo debe tomar el valor de false para habilitar
         *
         * los campos. Si no es requerido debera tomar el valor de true para que se inhabiliten los campos
         */
        bloqueaPeriodo = !(boolean) registroAux.getCampos().get("REQUIERE_PERIODO");
        tipoSolicitud = SysmanFunciones.nvl(registroAux.getCampos().get("TIPO_SOLICITUD"), "S").toString();
        if ("C".equals(tipoSolicitud)) {
            bloqueaConsulta = true;
        }
        else {
            bloqueaConsulta = false;
        }
        reporte = (String) registroAux.getCampos().get("REPORTE");
        plantilla = SysmanFunciones.validarCampoVacio(registroAux.getCampos(), "CODIGO_PLANTILLA") ? null
                        : registroAux.getCampos().get("CODIGO_PLANTILLA").toString();
        cargarListaTipoSolicitud();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipoSolicitud
     *
     * Realiza la asignacion del "Tipo de Permiso" qie se ha seleccionado para realizar la solicitud
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(SolicitudesAutDetalladosControladorEnum.TIPO_PERMISO
                                        .getValue(),
                                        registroAux.getCampos().get(cCodigo));
        registro.getCampos()
                        .put(SolicitudesAutDetalladosControladorEnum.NOMBRETIPOSOLICITUD
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        GeneralParameterEnum.NOMBRE
                                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaNombreAprobo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreAprobo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(SolicitudesAutDetalladosControladorEnum.CARGO_APROBACION
                                        .getValue(),
                                        registroAux.getCampos()
                                                        .get(SolicitudesAutDetalladosControladorEnum.ID_DE_CARGO
                                                                        .getValue()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnEnviar en la vista En el, se usa la plantilla 34
     */
    public void oprimirbtnEnviar() {
        // <CODIGO_DESARROLLADO>
        /**
         * Solo aplica para los permisos y las vacaciones
         */
        if (registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue()).equals("PERMISO")
                        || registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue())
                                        .equals("VACACIONES")) {

            Map<String, Object> paramEnvio = new TreeMap<>();
            paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            /**
             * Esta variable sera usada para validar a quien (es) debe ir dirigido el correo
             */
            int ingresoEmail = 0;
            /**
             * aca se valida en que estado esta el campo estado solicitud
             */
            Map<String, Object> remplazosDescripcion = new TreeMap<>();
            if ((registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()).equals("T")
                            || registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()).equals("A")
                            || registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()).equals("R")) && porTramitar == 0) {
                paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                "34");

                remplazosDescripcion.put("tipoSolicitud",
                                registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue()));
                remplazosDescripcion.put("nombreSolicitante",
                                registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETO.getValue()));
                remplazosDescripcion.put("cargoSolicitante",
                                registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRE_DEL_CARGO.getValue()));
                remplazosDescripcion.put("dependenciaSolicitante", nombreDependencia);

                ingresoEmail = 1;

            }
            else if (registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()).equals("R") && porTramitar == 1) {
                paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                "35");

                remplazosDescripcion.put("numeroSolicitud", numeroSolicitud);
                remplazosDescripcion.put("claseSolicitud",
                                registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue()));
                remplazosDescripcion.put("tipoSolicitud", registro.getCampos().get("NOMBRETIPOSOLICITUD"));
                remplazosDescripcion.put("motivoRechazo", registro.getCampos().get("MOTIVORECHAZO"));
                ingresoEmail = 2;

            }
            else {

                paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                "36");

                remplazosDescripcion.put("numeroSolicitud", numeroSolicitud);
                remplazosDescripcion.put("claseSolicitud",
                                registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue()));
                remplazosDescripcion.put("tipoSolicitud", registro.getCampos().get("NOMBRETIPOSOLICITUD"));
                ingresoEmail = 3;

            }

            Registro rsEmail = null;
            try {
                rsEmail = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(SolicitudesAutDetalladosControladorUrlEnum.URL0010
                                                                .getValue())
                                                .getUrl(),
                                                paramEnvio));
            }
            catch (SystemException e) {
                Logger.getLogger(SolicitudesAutDetalladosControlador.class.getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            if (rsEmail != null && correoJefe != null && (!correoJefe.isEmpty())) {

                String descripcionFinal = SysmanFunciones.remplazarVariableCorreo(
                                rsEmail.getCampos().get(
                                                GeneralParameterEnum.DESCRIPCION
                                                                .getName())
                                                .toString(),
                                remplazosDescripcion);

                EmailPojo email = new EmailPojo();
                email.setFrom(rsEmail.getCampos().get("ORIGEN")
                                .toString());
                if (ingresoEmail == 1) {
                    email.setTo(correoJefe);
                }
                else if (ingresoEmail == 2) {
                    email.setTo(correoSolicitante);
                }
                else {
                    email.setTo(correoSolicitante + ",talentohumano@ane.gov.co");
                }
                email.setSubject(rsEmail.getCampos().get("ASUNTO")
                                .toString());
                email.setBody(descripcionFinal);
                ApiRestClient client = new ApiRestClient();
                try {
                    client.postClient(email);

                    mensajeCorreo = "Alerta de email enviada correctamente al jefe inmediato.";
                    JsfUtil.agregarMensajeInformativo(mensajeCorreo);
                    /**
                     * dado que ya se envio el correo, se activa el indicador
                     */
                    presionoNotificar = true;
                    if (ingresoEmail == 1) {
                        registro.getCampos().put("FECHA_ENVIO_CORREO", new Date());

                        ejbHojasDeVidaCero.actualizarEnvioCorreosAutoservicio(compania,
                                        Long.parseLong(registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString()),
                                        Integer.parseInt(registro.getCampos()
                                                        .get(SolicitudesAutDetalladosControladorEnum.CLASE_SOLICITUD.getValue())
                                                        .toString()),
                                        0);
                    }
                }
                catch (IOException | SystemException e) {
                    Logger.getLogger(SolicitudesAutDetalladosControlador.class.getName()).log(Level.SEVERE, null, e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
            else {
                presionoNotificar = false;
                mensajeCorreo = "El jefe no tiene configurado un correo o la plantilla esta mal definida.";
                JsfUtil.agregarMensajeError(mensajeCorreo);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Notificar en la vista
     *
     * 1. Valida que el registro haya sido guardado 2. Verifica que el solicitante posea un Jefe Directo Asignado 3. Realiza la notificacion de la solicitud al Jefe Directo asignando valor en los
     * campos SENOTIFICA y DESTINO
     *
     */
    public void oprimirNotificar() {
        agregarRegistroNuevo(false);

        if (!validarCamposSolicitud()) {
            return;
        }

        String mensaje = idioma.getString("TB_TB3986")
                        .replace("s$nombreJefe$s",
                                        retornarString(rsDatosIniciales,
                                                        SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETOJEFE
                                                                        .getValue()));

        HashMap<String, Object> reemplazar;
        HashMap<String, Object> parametros;

        if (SysmanFunciones.validarVariableVacio(reporte) && SysmanFunciones.validarVariableVacio(plantilla)) {
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB4101"));
            return;
        }

        try {
            if (!SysmanFunciones.validarVariableVacio(reporte)) {
                ano = registro.getCampos().get(GeneralParameterEnum.ANO.getName()).toString();
                if (GeneralAutoservicioEnum.VOLANTE_PAGO.getValue().equals(registro.getCampos().get("CLASE_SOLICITUD").toString())) {
 
                	RetornoReporte retornoReporte = new RetornoReporte();
                    PrepararReporte prepararReporte = new PrepararReporte();
                        try {
                        	
							retornoReporte = prepararReporte.preparaVolante(
							                compania, Integer.parseInt(registro.getCampos().get("ID_DEMPLEADO").toString()),
							                Integer.parseInt(registro.getCampos().get("ID_DEMPLEADO").toString()), 1,
							                Integer.parseInt(ano),
							                Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.MES.getName()).toString()),
							                Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.PERIODO.getName()).toString()),
							                "0", SysmanConstantes.CONS_CENTRO);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							 JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
		                        logger.error(e.getMessage(), e);
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                   
                    reemplazar = retornoReporte.getReemplazar();
                    parametros = retornoReporte.getParametros();

                    Reporteador.resuelveConsulta(
                                    reporte,
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);

                    archivoDescarga = JsfUtil.exportarStreamed(
                                    reporte,
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.PDF);

                }

                if (GeneralAutoservicioEnum.CERTIFICADO_RETENCIONES.getValue()
                                .equals(registro.getCampos().get("CLASE_SOLICITUD").toString())) {

                    RetornoReporte retornoReporte = new RetornoReporte();
                    try {
                        PrepararReporte prepararReporte = new PrepararReporte();
                        retornoReporte = prepararReporte.prepararCertificadoDian(
                                        compania, Integer.parseInt(ano),
                                        nitEmpleado, nitEmpleado, new Date(), "GENERADO VIA WEB");
                    }
                    catch (SysmanException e) {
                        JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
                        logger.error(e.getMessage(), e);
                    }
                    reemplazar = retornoReporte.getReemplazar();
                    parametros = retornoReporte.getParametros();

                    Reporteador.resuelveConsulta(
                                    reporte,
                                    Integer.parseInt(SessionUtil.getModulo()),
                                    reemplazar, parametros);
                    archivoDescarga = JsfUtil.exportarStreamed(
                                    reporte,
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.PDF);

                }

            }
            else {

                Map<String, Object> param = new TreeMap<>();

                param.put("CODIGO", plantilla);
                param.put("TIPO", "41");
                param.put("FECHA", SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get("FECHA_SOLICITUD")));

                Registro rs;

                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                SolicitudesAutDetalladosControladorUrlEnum.URL0009
                                                                                .getValue())
                                                .getUrl(),
                                                param));
                if (rs != null) {
                    Date fecha = (Date) rs.getCampos()
                                    .get(GeneralParameterEnum.FECHA.getName());

                    String[] campos = new String[3];
                    String[] valores = new String[3];
                    campos[0] = "codigoPlantilla";
                    campos[1] = "fechaPlantilla";
                    campos[2] = "nombreDocDescarga";

                    valores[0] = plantilla;
                    valores[1] = SysmanFunciones.formatearFecha(fecha);
                    valores[2] = SysmanFunciones.concatenar((String) rs.getCampos().get("NOMBRE"),
                                    "_",
                                    nitEmpleado);

                    HashMap<String, String> variablesConsultaW = new HashMap<>();
                    variablesConsultaW.put("s$compania$s",
                                    SysmanFunciones.concatenar("'", compania,
                                                    "'"));
                    variablesConsultaW.put("s$consecutivo$s",
                                    registro.getCampos().get("CONSECUTIVO").toString());
                    variablesConsultaW.put("s$usuario$s",
                                    SysmanFunciones.concatenar("'",
                                                    SessionUtil.getUser()
                                                                    .getCodigo(),
                                                    "'"));
                    variablesConsultaW.put("s$claseSolicitud$s", registro.getCampos().get("CLASE_SOLICITUD").toString());
                    // variables por parametro para documento word
                    SessionUtil.setSessionVar("variablesConsultaWord",
                                    variablesConsultaW);
                    String numForm = String
                                    .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                    .getCodigo());
                    SessionUtil.cargarModalDatosFlash(numForm,
                                    SessionUtil.getModulo(),
                                    campos, valores);
                }
                else {
                    JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3984"));
                }

            }

            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.SENOTIFICA.getValue(), 1);

            agregarRegistroNuevo(false);

            mensaje = mensaje
                            .replace("s$consecutivo$s",
                                            retornarString(registro,
                                                            GeneralParameterEnum.CONSECUTIVO
                                                                            .getName()));

            if ("S".equals(tipoSolicitud)) {
                JsfUtil.agregarMensajeAlertaDialogo(mensaje);
            }

        }
        catch (SystemException | JRException | IOException | SysmanException | ParseException e) {
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /*
     * Metodo con el cual se valida los campos requeridos segun el tipo de solicitud
     */
    public boolean validarCamposSolicitud() {
        if ("S".equals(tipoSolicitud)) {
            if (!jefeDirectoAsignado) {
                JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3985"));

                return false;
            }
            else {

                registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.DESTINO
                                .getValue(),
                                retornarString(rsDatosIniciales,
                                                SolicitudesAutDetalladosControladorEnum.DOCUMENTOJEFE
                                                                .getValue()));
                registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.SUCURSAL_DESTINO.getValue(),
                                retornarString(rsDatosIniciales, "SUCURSALJEFE"));
            }
        }
        else {

            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.DESTINO
                            .getValue(), SysmanConstantes.CONS_TERCERO);
            registro.getCampos().put("SUCURSAL_DESTINO", SysmanConstantes.CONS_SUCURSAL);
        }

        if (!bloqueaPeriodo) {
            if (SysmanFunciones.validarCampoVacio(
                            registro.getCampos(), GeneralParameterEnum.ANO.getName())
                            || SysmanFunciones.validarCampoVacio(
                                            registro.getCampos(), GeneralParameterEnum.MES.getName())
                            || SysmanFunciones.validarCampoVacio(
                                            registro.getCampos(), GeneralParameterEnum.PERIODO.getName())) {
                JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB4048"));
                return false;
            }
        }
        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Permite definir informacion basica del solicitante
     */
    private void obtenerDatosSolicitante() {
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        nitEmpleado);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudesAutDetalladosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null) {
                rsDatosIniciales = rs;
            }
            else {
                JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3987"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
        }
    }

    /**
     * Realiza la asignacion inicial de valores a algunos campos del formulario
     */
    private void asignarDatosIniciales() {
        if (accion.equals(ACCION_INSERTAR)) {

            idEmpleado = retornarString(rsDatosIniciales,
                            GeneralParameterEnum.ID_DE_EMPLEADO.getName());
            sucursalEmpleado = retornarString(rsDatosIniciales,
                            GeneralParameterEnum.SUCURSAL.getName());
            idCargo = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.ID_DE_CARGO
                                            .getValue());

            nombreDependencia = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.NOMBREDEPENDENCIA
                                            .getValue());
            correoJefe = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.EMAIL_CORPORATIVO
                                            .getValue());

            correoSolicitante = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.EMAIL_SOLICITANTE
                                            .getValue());

            registro.getCampos().put(GeneralParameterEnum.ESTADO.getName(),
                            "T");
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.FECHA_SOLICITUD
                            .getValue(), new Date());
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.SENOTIFICA
                            .getValue(), 0);
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.FECHA_INICIO
                            .getValue(), new Date());
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.FECHA_FINAL
                            .getValue(), new Date());
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.HORA_INICIO
                            .getValue(), new Date());
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.HORA_FINAL
                            .getValue(), new Date());
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
            cargarListames();
            registro.getCampos().put(GeneralParameterEnum.MES.getName(), SysmanFunciones.mes(new Date()));
            cargarListaperiodo();
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETO.getValue(),
                            retornarString(rsDatosIniciales, SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETO.getValue()));
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.NOMBRE_DEL_CARGO.getValue(),
                            retornarString(rsDatosIniciales,
                                            SolicitudesAutDetalladosControladorEnum.NOMBRE_DEL_CARGO.getValue()));
            registro.getCampos().put("ID_DEMPLEADO", idEmpleado);
            registro.getCampos().put(SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETOJEFE.getValue(),
                            retornarString(rsDatosIniciales,
                                            SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETOJEFE.getValue()));
        }
        if (accion.equals(ACCION_MODIFICAR)) {

            cargarListames();
            cargarListaperiodo();

            nombreDependencia = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.NOMBREDEPENDENCIA
                                            .getValue());
            correoJefe = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.EMAIL_CORPORATIVO
                                            .getValue());

            correoSolicitante = retornarString(rsDatosIniciales,
                            SolicitudesAutDetalladosControladorEnum.EMAIL_SOLICITANTE
                                            .getValue());
        }

    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo dentro del registro que tambien ha sido ingresado por parametro
     *
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                        : reg.getCampos().get(campo).toString();
    }

    /**
     * Permite definir el bloqueo para los campos <code> CB5653, CB5654, CP52143, CP52144,CP52145,CP52146,CP52147 </code> de acuerdo a los parametros obtenidos del formulario "SolicitudesAut(1690)"
     */
    private void definirBloqueo() {
        camposBloqueados = false;
        if (((numeroSolicitud != 0) && !estadoSolicitud.equals("S"))
                        || (Integer.parseInt(registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.SENOTIFICA.getValue())
                                        .toString()) != 0)) {
            camposBloqueados = true;
        }

        if ("C".equals(tipoSolicitud)) {
            bloqueaConsulta = true;
        }
        else {
            bloqueaConsulta = false;
        }

        estadoBloqueado = !"T".equals(estadoSolicitud);
    }

    /**
     * Realiza el llamado a la funcion PCK_SYSMAN_UTL.FC_GENCONSECUTIVO para obtener el consecutivo de la tabla AUT_SOLICITUDES
     *
     * @return Valor a ser asignado como consecutivo en la tabla AUT_SOLICITUDES
     */
    private long generarConsecutivo() {
        long consecutivo = 0;
        try {
            consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            "AUT_SOLICITUDES",
                            "COMPANIA = ''" + compania + "''",
                            "CONSECUTIVO",
                            "1");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
        }
        return consecutivo;
    }

    /**
     * Realiza la validacion de si el usuario que intenta realizar la solictud posee un Jefe Directo configurado
     */
    private void validarJefeAsignado() {
        jefeDirectoAsignado = true;
        if (SysmanFunciones.validarCampoVacio(rsDatosIniciales.getCampos(),
                        "JEFE_DIRECTO")) {
            jefeDirectoAsignado = false;
            JsfUtil.agregarMensajeAlertaDialogo(idioma.getString("TB_TB3988"));
        }
    }

    /**
     * Envia un mensaje al usuario indicando que no se ha realizado la configuracion del Jefe Directo
     */
    public void mensajesInicioModal() {
        validarJefeAsignado();
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (llavesSolicitud == null) {
            cargarRegistro(null, ACCION_INSERTAR);
            registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            generarConsecutivo());
        }
        else {
            cargarRegistro(llavesSolicitud, ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     * Realiza la asignacion inicial de informacion que se visualiza al abrir el formulario.
     *
     * Define el valor de los indicadores para bloquear elementos en el formulario
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        /**
         * Se valida si el registro que se esta revisando esta en estado rechazado para mostrar el campo MOTIVO DE RECHAZO
         */
        if ("R".equals(registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()))) {
            verMotivoRechazo = true;
        }
        else {
            verMotivoRechazo = false;
        }
        asignarDatosIniciales();
        definirBloqueo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * Asigna los campos necesarios en la insercion que no son seleccionados en el formulario
     *
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>

        if (!validarCamposSolicitud()) {
            return false;
        }

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("CEDULA", nitEmpleado);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursalEmpleado);
        registro.getCampos().put(GeneralParameterEnum.CARGO.getName(), idCargo);
        registro.getCampos().put("USUARIO_SISTEMA",
                        SessionUtil.getUser().getCodigo());
        validarObservaciones();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return Si el proceso de insercion fue exitoso
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     * Remueve los campos innecesarios para la actualizacion
     *
     * @return Si el proceso previo a la actualizacion fue exitoso
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        /**
         * aca se valida que si el estado es rechazado y no ha presionado el boton de notificar, no lo deje guardar, solo para solicitudes tipo PERMISO O VACACIONES
         */
        if (registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue()).equals("PERMISO")
                        || registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue())
                                        .equals("VACACIONES")) {

            if ((!presionoNotificar) && porTramitar == 1 && registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()).equals("R")) {
                JsfUtil.agregarMensajeError("El estado es Rechazado, por ende debe presionar primero el boton Notificar para guardar");
                return false;
            }
            else if ((!presionoNotificar) && porTramitar == 1
                            && registro.getCampos().get(GeneralParameterEnum.ESTADO.getName()).equals("A")) {
                JsfUtil.agregarMensajeError("El estado es Aprobado, por ende debe presionar primero el boton Notificar para guardar");
                return false;
            }
        }

        validarObservaciones();
        registro.getCampos()
                        .remove(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD
                                        .getValue());
        registro.getCampos()
                        .remove(SolicitudesAutDetalladosControladorEnum.NOMBRETIPOSOLICITUD
                                        .getValue());
        registro.getCampos()
                        .remove(SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETO
                                        .getValue());
        registro.getCampos()
                        .remove(SolicitudesAutDetalladosControladorEnum.NOMBRE_DEL_CARGO
                                        .getValue());
        registro.getCampos()
                        .remove(SolicitudesAutDetalladosControladorEnum.NOMBRECOMPLETOJEFE
                                        .getValue());

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     * @return Si el proceso de actualizacion fue exitoso
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR)) {
            cargarRegistro(registro.getLlave(), ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     * @return Si el proceso previo a la eliminacion fue exitoso
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     * @return Si el proceso de eliminacion fue exitoso
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo que valida si se diligencia el campo observaciones, si esta vacio se agrega el nombre de la solicitud
     *
     * y el periodo de trabajo si asi esta configurada la solicitud
     */

    public void validarObservaciones() {
        String observacion;
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "OBSERVACIONES")) {
            observacion = (String) registro.getCampos().get(SolicitudesAutDetalladosControladorEnum.NOMBRESOLICITUD.getValue());
            if (!bloqueaPeriodo) {
                observacion = SysmanFunciones.concatenar(observacion, " -",
                                (String) registro.getCampos().get(GeneralParameterEnum.ANO.getName()), " -",
                                service.buscarEnLista((String) registro.getCampos().get(GeneralParameterEnum.MES.getName()), "MES",
                                                "NOMBRE_MES", listames),
                                " -",
                                service.buscarEnLista((String) registro.getCampos().get(GeneralParameterEnum.PERIODO.getName()), "PERIODO",
                                                "NOM_PERIODO",
                                                listaperiodo));
            }
            registro.getCampos().put("OBSERVACIONES", observacion);
        }
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     * Realiza la redireccion al formulario "SolicitudesAut(1690)"
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SOLICITUDES_AUTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que devuelve la ruta de la imagen almacenada
     *
     * @param imagen
     * @return
     */
    public String obtenerRuta(String imagen) {
        String imagenRuta = null;
        Map<String, Object> parametros = new HashMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            Registro ruta = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmConsultasControladorUrlEnum.URL647
                                                                                            .getValue())
                                                            .getUrl(),
                                            parametros));
            String registroRuta = ruta.getCampos().get("RUTA_IMAGEN")
                            .toString();
            imagenRuta = SysmanFunciones.concatenar(registroRuta.substring(0,
                            registroRuta.lastIndexOf(File.separator) + 1),
                            imagen);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeAlertaDialogo(e.getMessage());
        }

        return imagenRuta;
    }

    /**
     * Metodo que devuelve el valor del parametro ingresado por parametro
     *
     * @param nomPar
     * @return
     */
    public String consultarParametro(String nomPar, boolean validar) {
        String valor;
        Date fecha = null;
        try {
            fecha = SysmanFunciones
                            .convertirAFecha(
                                            SysmanFunciones.concatenar(fecha2));
        }
        catch (ParseException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        valor = "1";
        try {
            if (validar) {
                valor = SysmanFunciones.nvlStr(
                                ejbSysmanUtil.consultarParametro(compania,
                                                nomPar, SessionUtil.getModulo(),
                                                fecha, true),
                                "0");
            }
            else {
                valor = ejbSysmanUtil.consultarParametro(compania,
                                nomPar, SessionUtil.getModulo(), fecha, true);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return valor;

    }

    // <SET_GET_ATRIBUTOS>
    public boolean isCamposBloqueados() {
        return camposBloqueados;
    }

    public void setCamposBloqueados(boolean camposBloqueados) {
        this.camposBloqueados = camposBloqueados;
    }

    public boolean isEstadoBloqueado() {
        return estadoBloqueado;
    }

    public void setEstadoBloqueado(boolean estadoBloqueado) {
        this.estadoBloqueado = estadoBloqueado;
    }

    public boolean isBloqueaPeriodo() {
        return bloqueaPeriodo;
    }

    public void setBloqueaPeriodo(boolean bloqueaPerido) {
        this.bloqueaPeriodo = bloqueaPerido;
    }

    public boolean isBloqueaConsulta() {
        return bloqueaConsulta;
    }

    public void setBloqueaConsulta(boolean bloqueaConsulta) {
        this.bloqueaConsulta = bloqueaConsulta;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaSolicitud
     *
     * @return listaSolicitud
     */
    public RegistroDataModelImpl getListaSolicitud() {
        return listaSolicitud;
    }

    /**
     * @return the listaano
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * @param listaano
     * the listaano to set
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    /**
     * @return the listames
     */
    public List<Registro> getListames() {
        return listames;
    }

    /**
     * @param listames
     * the listames to set
     */
    public void setListames(List<Registro> listames) {
        this.listames = listames;
    }

    /**
     * @return the listaperiodo
     */
    public List<Registro> getListaperiodo() {
        return listaperiodo;
    }

    /**
     * @param listaperiodo
     * the listaperiodo to set
     */
    public void setListaperiodo(List<Registro> listaperiodo) {
        this.listaperiodo = listaperiodo;
    }

    /**
     * Asigna la lista listaSolicitud
     *
     * @param listaSolicitud
     * Variable a asignar en listaSolicitud
     */
    public void setListaSolicitud(RegistroDataModelImpl listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
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
     * Retorna la lista listaNombreAprobo
     *
     * @return listaNombreAprobo
     */
    public RegistroDataModelImpl getListaNombreAprobo() {
        return listaNombreAprobo;
    }

    /**
     * Asigna la lista listaNombreAprobo
     *
     * @param listaNombreAprobo
     * Variable a asignar en listaNombreAprobo
     */
    public void setListaNombreAprobo(RegistroDataModelImpl listaNombreAprobo) {
        this.listaNombreAprobo = listaNombreAprobo;
    }

    public boolean isVerMotivoRechazo() {
        return verMotivoRechazo;
    }

    public void setVerMotivoRechazo(boolean verMotivoRechazo) {
        this.verMotivoRechazo = verMotivoRechazo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
