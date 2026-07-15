/*-
 * FrmEvaluacionesDetControlador.java
 *
 * 1.0
 * 
 * 07/02/2018
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
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmEvaluacionesDetControladorEnum;
import com.sysman.hojasdevida.enums.FrmEvaluacionesDetControladorUrlEnum;
import com.sysman.hojasdevida.enums.FrmEvaluacionesSubDetPrincipalControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * Formulario que sirve para visualizar las evaluaciones por
 * evaluador.
 *
 * @version 1.0, 07/02/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmEvaluacionesDetControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consCedulaEvaluado;
    private final String consCedulaEvaluador;
    private final String consSucursalEvaluado;
    private final String consSucursalEvaluador;
    private final String consNumeroEvaluacion;
    private final String consClaseEvaluacion;
    private final String consTipoEvaluacion;
    private final String consTipo;
    private final String consMenu;

    private final String nitAne;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor asignado la clase de evaluacion
     */
    private String clase;
    /**
     * Atributo que contiene el valor asignado al tipo de evaluacion
     */
    private String tipo;
    /**
     * Atributo que contiene el valor asignado al numero de evaluacion
     */
    private String evaluacion;
    /**
     * Atributo que contiene el valor asignado al titulo que contendra
     * el formulario
     */
    private String titulo;
    private Map<String, Object> parametrosEntrada;
    private String plantilla;
    private String codPlantilla;
    private String nombrePlantilla;
    private Date fechaPlantilla;
    private boolean manejaCantidad;
    private boolean manejaCantidadCom;
    private int cantidadMinima;
    private int cantidadMinimaCom;
    /**
     * Atributo que permite visualizar los botones de acuerdo a la
     * clase de evaluacion con la cual se ingreso.
     */
    private boolean visibleBotones;

    private boolean visibleReclamaciones;

    private boolean visibleCompromisos;

    private boolean visibleAjuste;

    private boolean bloqueadoEmpleado;

    /**
     * Atributo que permite la visulaizacion de botones para la ANE
     */
    private boolean visibleAne;
    /**
     * Atributo que permite bloquear los botones mientras no se realic
     * una insercion.
     */
    private boolean bloqueadoBotones;
    /**
     * Atributo que contiene el valor asignado al escalafon del
     * evaluador en la forma del formulario.
     */
    private String escalafonEvaluador;
    /**
     * Atributo que contiene el valor asignado al escalafon del
     * evaluado en la forma del formulario.
     */
    private String escalafonEvaluado;
    /**
     * Atributo que contiene el valor asignado al cargo del evaluador
     * en la forma del formulario.
     */
    private String cargoEvaluador;
    /**
     * Atributo que contiene el valor asignado al cargo del evaluado
     * en la forma del formulario.
     */
    private String cargoEvaluado;
    /**
     * Atributo que contiene el valor asignado al empleado evaluador
     * en la forma del formulario.
     */
    private String empleadoEvaluador;
    /**
     * Atributo que contiene el valor asignado al empleado evaluado en
     * la forma del formulario.
     */
    private String empleadoEvaluado;
    /**
     * Atributo que contiene el valor asignado al evaluador comision
     * en la forma del formulario.
     */
    private String evaluadorComision;
    /**
     * Atributo que contiene el valor asignado a la cedula del
     * evaluado en la forma del formulario.
     */
    private String cedulaEvaluado;
    /**
     * Atributo que contiene el valor asignado a la cedula del
     * evaluador en la forma del formulario.
     */
    private String cedulaEvaluador;
    /**
     * Atributo que contiene el valor asignado a la sucursal del
     * evaluado en la forma del formulario.
     */
    private String sucursalEvaluado;
    /**
     * Atributo que contiene el valor asignado a la sucursal del
     * evaluador en la forma del formulario.
     */
    private String sucursalEvaluador;

    private int permiteInsertar;

    private boolean bloqueadoCom;

    private String tituloDetalle;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los detalles del combo evaluador comision.
     */
    private RegistroDataModelImpl listacmbEvaluadorComision;

    /**
     * Lista que contiene los detalles del combo empleado evaluado.
     */
    private RegistroDataModelImpl listacmbCodEmpEvaluado;

    /** Lista que contiene los detalles del combo cargo evaluado. */
    private RegistroDataModelImpl listacmbCargoEvaluado;

    /**
     * Lista que contiene los detalles del combo escalafon evaluado.
     */
    private RegistroDataModelImpl listacmbEscalafonEvaluado;

    /** Lista que contiene los detalles del combo cargo evaluador. */
    private RegistroDataModelImpl listacmbCargoEvaluador;

    /**
     * Lista que contiene los detalles del combo escalafon evaluador.
     */
    private RegistroDataModelImpl listacmbEscalafonEvaluador;

    /**
     * Lista que contiene los detalles del combo empleado evaluador.
     */
    private RegistroDataModelImpl listacmbCodEmpEvaluador;

    /**
     * Lista que contiene las plantillas para generar el informe.
     */
    private RegistroDataModelImpl listaPlantilla;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasDeVidaCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de FrmEvaluacionesDetControlador
     */
    @SuppressWarnings("unchecked")
    public FrmEvaluacionesDetControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        consCedulaEvaluado = "cedulaEvaluado";
        consCedulaEvaluador = "cedulaEvaluador";
        consClaseEvaluacion = "claseEvaluacion";
        consNumeroEvaluacion = "numeroEvaluacion";
        consSucursalEvaluado = "sucursalEvaluado";
        consSucursalEvaluador = "sucursalEvaluador";
        consTipoEvaluacion = "tipoEvaluacion";
        consTipo = "TIPO_EVALUACION";
        consMenu = "21100203";

        nitAne = SessionUtil.getCompaniaIngreso().getNit();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                            .getCodigo();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                evaluacion = (String) parametrosEntrada.get("evaluacion");
                tipo = (String) parametrosEntrada.get("tipo");
                clase = (String) SessionUtil.getSessionVar(
                                ConstantesHojasDeVidaEnum.CLASE_EVALUACION
                                                .getValue());
                permiteInsertar = "1".equals(clase)
                    || consMenu.equals(SessionUtil.getMenuActual()) ? -1 : 0;

            }
            titulo = idioma.getString("TB_TB3989").replace("#$usuario#$",
                            SysmanFunciones.concatenar(
                                            "1".equals(clase)
                                                ? "EVALUACIÓN 360 "
                                                : "CNSC SEMESTRAL ",
                                            SessionUtil.getUser()
                                                            .getApellido1(),
                                            " ",
                                            SessionUtil.getUser()
                                                            .getApellido2(),
                                            " ",
                                            SessionUtil.getUser()
                                                            .getNombre1()));

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbEvaluadorComision();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
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
        enumBase = GenericUrlEnum.EV_DETALLE_EVALUACION;
        buscarLlave();
        asignarOrigenDatos();
        cargarListaPlantilla();
    }

    public void consultarEvaluador() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                            SessionUtil.getUser().getCedula());

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEvaluacionesDetControladorUrlEnum.URL7227
                                                                            .getValue())
                                            .getUrl(), param));

            empleadoEvaluador = reg
                            .getCampos().get("NOMBREEMPLEADO").toString();

            sucursalEvaluador = reg
                            .getCampos()
                            .get(GeneralParameterEnum.SUCURSAL.getName())
                            .toString();

            cedulaEvaluador = reg
                            .getCampos()
                            .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                            .toString();

            cargoEvaluador = reg
                            .getCampos().get("NOMBRE_DEL_CARGO").toString();
            escalafonEvaluador = reg
                            .getCampos().get("NOMBRE").toString();

            registro.getCampos().put("CODIGO_EMPLEADO_EVALUADOR",
                            reg.getCampos().get("ID_DE_EMPLEADO"));
            registro.getCampos().put("CARGO_EVALUADOR",
                            reg.getCampos().get("ID_DE_CARGO"));
            registro.getCampos().put("ESCALAFON_EVALUADOR", reg.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), clase);
        parametrosListado.put("CEDULA", SessionUtil.getUser().getCedula());
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
                        evaluacion);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(),
                        tipo);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbEvaluadorComision
     *
     */
    public void cargarListacmbEvaluadorComision() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesDetControladorUrlEnum.URL15084
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbEvaluadorComision = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     * 
     * Carga la lista listacmbCodEmpEvaluado
     *
     */
    public void cargarListacmbCodEmpEvaluado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesDetControladorUrlEnum.URL45768
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        SessionUtil.getUser().getCedula());
        param.put(GeneralParameterEnum.CLASE.getName(),
                        clase);

        param.put(GeneralParameterEnum.TIPO.getName(),
                        tipo);

        listacmbCodEmpEvaluado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmEvaluacionesDetControladorEnum.CODIGO_EMPLEADO_EVALUADO
                                        .getValue());
    }

    /**
     * Carga la lista listaPlantilla
     *
     */
    public void cargarListaPlantilla() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEvaluacionesDetControladorUrlEnum.URL1234
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmEvaluacionesDetControladorEnum.TIPO.getValue(), "51");
        param.put(FrmEvaluacionesDetControladorEnum.FECHACONSULTA.getValue(),
                        new Date());

        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    public void cambiarAjuste() {
        visibleAjuste = (boolean) registro.getCampos().get("AJUSTE");
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbEvaluadorComision
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbEvaluadorComision(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EVALUADOR_COMISION",
                        registroAux.getCampos().get("ID_DE_EMPLEADO"));
        evaluadorComision = registroAux.getCampos().get("NOMBRECOMPLETO")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodEmpEvaluado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodEmpEvaluado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrmEvaluacionesDetControladorEnum.CODIGO_EMPLEADO_EVALUADO
                                        .getValue(),
                        registroAux
                                        .getCampos()
                                        .get(FrmEvaluacionesDetControladorEnum.CODIGO_EMPLEADO_EVALUADO
                                                        .getValue()));
        empleadoEvaluado = registroAux
                        .getCampos().get("NOMBREEMPLEADO").toString();

        sucursalEvaluado = registroAux
                        .getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName())
                        .toString();

        cedulaEvaluado = registroAux
                        .getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                        .toString();

        cargoEvaluado = registroAux
                        .getCampos().get("NOMBRE_DEL_CARGO").toString();
        escalafonEvaluado = registroAux
                        .getCampos().get("NOMBRE").toString();

        registro.getCampos().put("CARGO_EVALUADO",
                        registroAux.getCampos().get("ID_DE_CARGO"));
        registro.getCampos().put("ESCALAFON_EVALUADO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codPlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        plantilla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        nombrePlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    public void oprimirDecisionRecurso() {

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        parametros.put("CedulaEvaluado", registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                        .getValue())
                        .toString());
        parametros.put("CedulaEvaluador", registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                        .getValue())
                        .toString());
        parametros.put("SucursalEvaluado", registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                        .getValue())
                        .toString());
        parametros.put("SucursalEvaluador", registro.getCampos().get(
                        FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                        .getValue())
                        .toString());
        parametros.put("TipoEvaluacion", tipo);
        parametros.put("ClaseEvaluacion", clase);
        parametros.put("NumeroEvaluacion", evaluacion);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_DESICIONRECURSO_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdDetalleEvaluacion en la
     * vista
     *
     */
    public void oprimircmdDetalleEvaluacion() {
        // <CODIGO_DESARROLLADO>

        try {
            if (!(boolean) registro.getCampos().get("REG_DETALLE")) {
                ejbHojasDeVidaCero.registrarDetallesEvaluacion(compania,
                                new BigInteger(evaluacion),
                                Integer.parseInt(clase),
                                registro.getCampos().get(
                                                FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                                                .getValue())
                                                .toString(),
                                registro.getCampos().get(
                                                FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                                                .getValue())
                                                .toString(),
                                registro.getCampos().get(
                                                FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                                                .getValue())
                                                .toString(),
                                registro.getCampos().get(
                                                FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                                                .getValue())
                                                .toString(),
                                registro.getCampos().get("ESCALAFON_EVALUADOR")
                                                .toString(),
                                registro.getCampos().get("ESCALAFON_EVALUADO")
                                                .toString(),
                                registro.getCampos().get(consTipo).toString(),
                                registro.getCampos().get(
                                                FrmEvaluacionesDetControladorEnum.CARGO_EVALUADOR
                                                                .getValue())
                                                .toString(),
                                registro.getCampos().get(
                                                FrmEvaluacionesDetControladorEnum.CARGO_EVALUADO
                                                                .getValue())
                                                .toString(),
                                registro.getCampos()
                                                .get("CODIGO_EMPLEADO_EVALUADOR")
                                                .toString(),
                                registro.getCampos()
                                                .get("CODIGO_EMPLEADO_EVALUADO")
                                                .toString(),
                                registro.getCampos().get("EVALUADOR_COMISION")
                                                .toString(),
                                SessionUtil.getUser().getCodigo());
            }

            Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put("cerrar", "1");
            parametros.put(consCedulaEvaluado,
                            registro.getCampos().get(
                                            FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                                            .getValue())
                                            .toString());
            parametros.put(consCedulaEvaluador, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                            .getValue())
                            .toString());

            parametros.put(consSucursalEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                            .getValue())
                            .toString());

            parametros.put(consSucursalEvaluador, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                            .getValue())
                            .toString());

            parametros.put("cargoEvaluado", registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CARGO_EVALUADO
                                            .getValue())
                            .toString());

            parametros.put("cargoEvaluador", registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CARGO_EVALUADOR
                                            .getValue())
                            .toString());

            parametros.put("escalafonEvaluado", registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.ESCALAFON_EVALUADO
                                            .getValue())
                            .toString());

            parametros.put("escalafonEvaluador", registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.ESCALAFON_EVALUADOR
                                            .getValue())
                            .toString());
            parametros.put("tipo", tipo);
            parametros.put("evaluacion", evaluacion);
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDETPRINCIPAL_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbCompromisos en la vista
     *
     */
    public void oprimircmbCompromisos() {
        // <CODIGO_DESARROLLADO>
        try {
            int anio = Integer.parseInt(registro.getCampos()
                            .get(GeneralParameterEnum.ANO
                                            .getName())
                            .toString());
            int periodo = Integer.parseInt(registro.getCampos()
                            .get(GeneralParameterEnum.PERIODO
                                            .getName())
                            .toString());
            Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(consCedulaEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consCedulaEvaluador, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                            .getValue())
                            .toString());
            parametros.put(consSucursalEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consSucursalEvaluador, registro.getCampos().get(
                            FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                            .getValue())
                            .toString());

            parametros.put("CODIGO_EVALUADO", registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CODIGO_EMPLEADO_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consTipoEvaluacion, tipo);
            parametros.put(consNumeroEvaluacion, evaluacion);
            parametros.put(GeneralParameterEnum.DEPENDENCIA.getName()
                            .toLowerCase(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.DEPENDENCIA
                                                            .getName()));
            parametros.put(GeneralParameterEnum.ANO.getName().toLowerCase(),
                            anio);
            parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                            periodo);

            if (consMenu.equals(SessionUtil.getMenuActual())) {
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FRM_COMPROMISOS_LABORALES_CONTROLADOR
                                                .getCodigo()));
            }
            else {
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.COMPROMISOS_ACORDADOS_CONTROLADOR
                                                .getCodigo()));

                ejbHojasDeVidaCero.generarCompromisos(compania,
                                new BigInteger(evaluacion),
                                Integer.parseInt(clase),
                                anio, registro.getCampos()
                                                .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                                                .getValue())
                                                .toString(),
                                registro.getCampos()
                                                .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                                                .getValue())
                                                .toString(),
                                periodo, tipo,
                                SessionUtil.getUser().getCodigo());

            }
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbEvidencias en la vista
     *
     */
    public void oprimirReclamaciones() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", css);
        parametros.put(consCedulaEvaluado, registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                        .getValue())
                        .toString());
        parametros.put(consSucursalEvaluado, registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                        .getValue())
                        .toString());
        parametros.put(consTipoEvaluacion, tipo);
        parametros.put(consClaseEvaluacion, clase);
        parametros.put(consNumeroEvaluacion, evaluacion);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.RECLAMACIONES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbEvidencias en la vista
     *
     */
    public void oprimircmbEvidencias() {
        // <CODIGO_DESARROLLADO>
        try {
            int anio = Integer.parseInt(registro.getCampos()
                            .get(GeneralParameterEnum.ANO
                                            .getName())
                            .toString());

            ejbHojasDeVidaCero.heredarEvidencias(compania,
                            new BigInteger(evaluacion), registro.getCampos()
                                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                                            .getValue())
                                            .toString(),
                            registro.getCampos()
                                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                                            .getValue())
                                            .toString(),
                            Integer.parseInt(clase), tipo,
                            anio, 1, SessionUtil.getUser().getCodigo());

            Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(consCedulaEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consCedulaEvaluador, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                            .getValue())
                            .toString());
            parametros.put(consSucursalEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consSucursalEvaluador, registro.getCampos().get(
                            FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                            .getValue())
                            .toString());
            parametros.put(consTipoEvaluacion, tipo);
            parametros.put(consClaseEvaluacion, clase);
            parametros.put(consNumeroEvaluacion, evaluacion);

            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRMEVEVIDENCIAS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbAcciones en la vista
     *
     */
    public void oprimircmbAcciones() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", css);
        parametros.put(consCedulaEvaluado, registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                        .getValue())
                        .toString());
        parametros.put(consCedulaEvaluador, registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                        .getValue())
                        .toString());
        parametros.put(consSucursalEvaluado, registro.getCampos()
                        .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                        .getValue())
                        .toString());
        parametros.put(consSucursalEvaluador, registro.getCampos().get(
                        FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                        .getValue())
                        .toString());
        parametros.put(consTipoEvaluacion, tipo);
        parametros.put(consClaseEvaluacion, clase);
        parametros.put(consNumeroEvaluacion, evaluacion);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMEVACCIONESMEJORAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbAccionesComp en la
     * vista
     *
     */
    public void oprimircmbAccionesComp() {
        // <CODIGO_DESARROLLADO>
        try {
            int anio = Integer.parseInt(registro.getCampos()
                            .get(GeneralParameterEnum.ANO
                                            .getName())
                            .toString());

            ejbHojasDeVidaCero.heredarEvidencias(compania,
                            new BigInteger(evaluacion), registro.getCampos()
                                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                                            .getValue())
                                            .toString(),
                            registro.getCampos()
                                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                                            .getValue())
                                            .toString(),
                            Integer.parseInt(clase), tipo,
                            anio, 2, SessionUtil.getUser().getCodigo());

            Direccionador direccionador = new Direccionador();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", css);
            parametros.put(consCedulaEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consCedulaEvaluador, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                            .getValue())
                            .toString());
            parametros.put(consSucursalEvaluado, registro.getCampos()
                            .get(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                            .getValue())
                            .toString());
            parametros.put(consSucursalEvaluador, registro.getCampos().get(
                            FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                            .getValue())
                            .toString());
            parametros.put(consTipoEvaluacion, tipo);
            parametros.put(consClaseEvaluacion, clase);
            parametros.put(consNumeroEvaluacion, evaluacion);
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.FRMEVACCIONESMEJORASCOMPORTAMENTALES_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarGeneracion(int opcion) {
        boolean estado = false;
        try {
            manejaCantidad = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CANTIDAD DE COMPROMISOS",
                            SessionUtil.getModulo(), new Date(), true));

            manejaCantidadCom = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CANTIDAD DE COMPETENCIAS",
                            SessionUtil.getModulo(), new Date(), true));

            if (manejaCantidad && 1 == opcion) {
                Map<String, Object> param2 = new TreeMap<>();

                param2.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param2.put("CLASE_EVALUACION",
                                clase);
                param2.put(consTipo,
                                tipo);
                param2.put("CEDULA_EVALUADO",
                                registro.getCampos().get("CEDULA_EVALUADO")
                                                .toString());
                param2.put("SUCURSAL_EVALUADO",
                                registro.getCampos()
                                                .get("SUCURSAL_EVALUADO")
                                                .toString());
                param2.put(GeneralParameterEnum.ANO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.ANO
                                                                .getName())
                                                .toString());
                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmEvaluacionesDetControladorUrlEnum.URL8574
                                                                                .getValue())
                                                .getUrl(), param2));

                if (Integer.parseInt(reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString()) < cantidadMinima) {
                    estado = true;
                }
            }
            else if (manejaCantidadCom && 2 == opcion) {
                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param.put("EVALUACION".toUpperCase(),
                                evaluacion);
                param.put(GeneralParameterEnum.CLASE.getName(),
                                clase);
                param.put(GeneralParameterEnum.TIPO.getName(),
                                tipo);
                param.put("CEDULA_EVALUADO",
                                registro.getCampos().get("CEDULA_EVALUADO")
                                                .toString());
                param.put("CEDULA_EVALUADOR",
                                registro.getCampos().get("CEDULA_EVALUADOR")
                                                .toString());

                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmEvaluacionesSubDetPrincipalControladorUrlEnum.URL8574
                                                                                .getValue())
                                                .getUrl(), param));
                if (Integer.parseInt(reg.getCampos()
                                .get(GeneralParameterEnum.TOTAL.getName())
                                .toString()) < cantidadMinimaCom) {
                    estado = true;
                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return estado;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbGenerar en la vista
     *
     */
    public void oprimircmbGenerar() {
        // <CODIGO_DESARROLLADO>
        if (validarGeneracion(1)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB4165").replace(
                                            "#cantidad#",
                                            String.valueOf(cantidadMinima)));
            return;
        }

        if (validarGeneracion(2)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB4189").replace(
                                            "#cantidad#",
                                            String.valueOf(cantidadMinimaCom)));
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("codigoPlantilla", codPlantilla);
        param.put("fechaPlantilla",
                        SysmanFunciones.formatearFecha(
                                        fechaPlantilla));
        param.put("nombreDocDescarga", nombrePlantilla);

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s", compania);
        variablesConsultaW.put("s$numero$s", evaluacion);
        variablesConsultaW.put("s$clase$s", clase);
        variablesConsultaW.put("s$tipo$s", tipo);
        variablesConsultaW.put("s$cedulaEvaluado$s",
                        registro.getCampos().get("CEDULA_EVALUADO")
                                        .toString());
        variablesConsultaW.put("s$sucursalEvaluado$s",
                        registro.getCampos()
                                        .get("SUCURSAL_EVALUADO")
                                        .toString());
        variablesConsultaW.put("s$cedulaEvaluador$s",
                        registro.getCampos()
                                        .get("CEDULA_EVALUADOR")
                                        .toString());
        variablesConsultaW.put("s$sucursalEvaluador$s",
                        registro.getCampos()
                                        .get("SUCURSAL_EVALUADOR")
                                        .toString());

        SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
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
        try {

            if ("900.334.265-3".equals(nitAne)) {
                visibleAne = true;
            }

            if ("1".equals(clase)) {
                visibleBotones = visibleCompromisos = false;

            }
            else {
                if (consMenu.equals(SessionUtil.getMenuActual())) {
                    visibleBotones = false;
                    visibleCompromisos = true;
                }
                else {
                    visibleCompromisos = visibleBotones = true;
                }
            }

            if (consMenu.equals(SessionUtil.getMenuActual())
                && "3".equals(clase)) {
                visibleReclamaciones = true;
            }
            else {
                visibleReclamaciones = false;
            }

            tituloDetalle = "3".equals(clase) ? idioma.getString("TB_TB4163")
                : idioma.getString("TB_TB4162");

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            evaluacion);
            param.put(GeneralParameterEnum.CLASE.getName(),
                            clase);
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEvaluacionesDetControladorUrlEnum.URL4217
                                                                            .getValue())
                                            .getUrl(), param));
            cantidadMinima = Integer.parseInt(
                            reg.getCampos().get("MIN_COMPROMISOS").toString());

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEvaluacionesDetControladorUrlEnum.URL5248
                                                                            .getValue())
                                            .getUrl(), param));
            cantidadMinimaCom = Integer.parseInt(
                            reg.getCampos().get("MIN_COMPETENCIAS").toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        consultarEvaluador();
        cargarListacmbCodEmpEvaluado();
        if (accion.equals(ACCION_MODIFICAR)) {
            bloqueadoEmpleado = true;
            visibleAjuste = (boolean) registro.getCampos().get("AJUSTE");
            escalafonEvaluado = registro.getCampos().get("ESCALAFONEVALUADO")
                            .toString();
            escalafonEvaluador = registro.getCampos().get("ESCALAFON")
                            .toString();
            cargoEvaluado = registro.getCampos().get("CARGOEVALUADO")
                            .toString();
            cargoEvaluador = registro.getCampos().get("CARGO").toString();
            empleadoEvaluado = registro.getCampos().get("NOMBREEVALUADO")
                            .toString();
            empleadoEvaluador = registro.getCampos().get("NOMBREEVALUADOR")
                            .toString();
            evaluadorComision = registro.getCampos().get("NOMBRECOMISION")
                            .toString();
            // tipo = registro.getCampos().get(consTipo).toString();
            bloqueadoCom = false;
            if (consMenu.equals(SessionUtil.getMenuActual())
                && !"900.334.265-3".equals(nitAne)) {
                bloqueadoBotones = true;
            }
            else {
                bloqueadoBotones = false;
            }
        }
        else {
            bloqueadoEmpleado = visibleAjuste = false;
            escalafonEvaluado = cargoEvaluado = empleadoEvaluado = evaluadorComision = null;
            bloqueadoBotones = true;
            bloqueadoCom = true;
            registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            new Date());
            try {
                registro.getCampos().put("HORA",
                                SysmanFunciones.convertirAFecha("01/01/1970 "
                                    + SysmanFunciones.convertirAHoraCadena(
                                                    new Date()),
                                                "dd/MM/yyyy HH:mm:ss"));
            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put("NUMERO_EVALUACION", evaluacion);
        registro.getCampos().put("CLASE_EVALUACION", clase);
        registro.getCampos().put("TIPO_EVALUACION", tipo);
        registro.getCampos()
                        .put(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADO
                                        .getValue(), cedulaEvaluado);
        registro.getCampos()
                        .put(FrmEvaluacionesDetControladorEnum.CEDULA_EVALUADOR
                                        .getValue(), cedulaEvaluador);
        registro.getCampos()
                        .put(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADO
                                        .getValue(), sucursalEvaluado);
        registro.getCampos()
                        .put(FrmEvaluacionesDetControladorEnum.SUCURSAL_EVALUADOR
                                        .getValue(), sucursalEvaluador);
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("CARGO");
        registro.getCampos().remove("CARGOEVALUADO");
        registro.getCampos().remove("ESCALAFON");
        registro.getCampos().remove("ESCALAFONEVALUADO");
        registro.getCampos().remove("NOMBREEVALUADOR");
        registro.getCampos().remove("NOMBREEVALUADO");
        registro.getCampos().remove("NOMBRECOMISION");
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.DEPENDENCIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
        if (!visibleAjuste) {
            registro.getCampos().put("FECHA_AJUSTE", null);
            registro.getCampos().put("MOTIVO_AJUSTE", null);
        }
        else {
            if (registro.getCampos().get("FECHA_AJUSTE") == null
                || registro.getCampos().get("MOTIVO_AJUSTE") == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4190"));
                return false;
            }
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

    // <SET_GET_ATRIBUTOS>

    public boolean isVisibleBotones() {
        return visibleBotones;
    }

    public void setVisibleBotones(boolean visibleBotones) {
        this.visibleBotones = visibleBotones;
    }

    public String getEscalafonEvaluador() {
        return escalafonEvaluador;
    }

    public void setEscalafonEvaluador(String escalafonEvaluador) {
        this.escalafonEvaluador = escalafonEvaluador;
    }

    public String getEscalafonEvaluado() {
        return escalafonEvaluado;
    }

    public void setEscalafonEvaluado(String escalafonEvaluado) {
        this.escalafonEvaluado = escalafonEvaluado;
    }

    public String getCargoEvaluador() {
        return cargoEvaluador;
    }

    public void setCargoEvaluador(String cargoEvaluador) {
        this.cargoEvaluador = cargoEvaluador;
    }

    public String getCargoEvaluado() {
        return cargoEvaluado;
    }

    public void setCargoEvaluado(String cargoEvaluado) {
        this.cargoEvaluado = cargoEvaluado;
    }

    public String getEmpleadoEvaluador() {
        return empleadoEvaluador;
    }

    public void setEmpleadoEvaluador(String empleadoEvaluador) {
        this.empleadoEvaluador = empleadoEvaluador;
    }

    public String getEmpleadoEvaluado() {
        return empleadoEvaluado;
    }

    public void setEmpleadoEvaluado(String empleadoEvaluado) {
        this.empleadoEvaluado = empleadoEvaluado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEvaluadorComision() {
        return evaluadorComision;
    }

    public void setEvaluadorComision(String evaluadorComision) {
        this.evaluadorComision = evaluadorComision;
    }

    public boolean isBloqueadoBotones() {
        return bloqueadoBotones;
    }

    public void setBloqueadoBotones(boolean bloqueadoBotones) {
        this.bloqueadoBotones = bloqueadoBotones;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbEvaluadorComision
     * 
     * @return listacmbEvaluadorComision
     */
    public RegistroDataModelImpl getListacmbEvaluadorComision() {
        return listacmbEvaluadorComision;
    }

    /**
     * Asigna la lista listacmbEvaluadorComision
     * 
     * @param listacmbEvaluadorComision
     * Variable a asignar en listacmbEvaluadorComision
     */
    public void setListacmbEvaluadorComision(
        RegistroDataModelImpl listacmbEvaluadorComision) {
        this.listacmbEvaluadorComision = listacmbEvaluadorComision;
    }

    /**
     * Retorna la lista listacmbCodEmpEvaluado
     * 
     * @return listacmbCodEmpEvaluado
     */
    public RegistroDataModelImpl getListacmbCodEmpEvaluado() {
        return listacmbCodEmpEvaluado;
    }

    /**
     * Asigna la lista listacmbCodEmpEvaluado
     * 
     * @param listacmbCodEmpEvaluado
     * Variable a asignar en listacmbCodEmpEvaluado
     */
    public void setListacmbCodEmpEvaluado(
        RegistroDataModelImpl listacmbCodEmpEvaluado) {
        this.listacmbCodEmpEvaluado = listacmbCodEmpEvaluado;
    }

    /**
     * Retorna la lista listacmbCargoEvaluado
     * 
     * @return listacmbCargoEvaluado
     */
    public RegistroDataModelImpl getListacmbCargoEvaluado() {
        return listacmbCargoEvaluado;
    }

    /**
     * Asigna la lista listacmbCargoEvaluado
     * 
     * @param listacmbCargoEvaluado
     * Variable a asignar en listacmbCargoEvaluado
     */
    public void setListacmbCargoEvaluado(
        RegistroDataModelImpl listacmbCargoEvaluado) {
        this.listacmbCargoEvaluado = listacmbCargoEvaluado;
    }

    /**
     * Retorna la lista listacmbEscalafonEvaluado
     * 
     * @return listacmbEscalafonEvaluado
     */
    public RegistroDataModelImpl getListacmbEscalafonEvaluado() {
        return listacmbEscalafonEvaluado;
    }

    /**
     * Asigna la lista listacmbEscalafonEvaluado
     * 
     * @param listacmbEscalafonEvaluado
     * Variable a asignar en listacmbEscalafonEvaluado
     */
    public void setListacmbEscalafonEvaluado(
        RegistroDataModelImpl listacmbEscalafonEvaluado) {
        this.listacmbEscalafonEvaluado = listacmbEscalafonEvaluado;
    }

    /**
     * Retorna la lista listacmbCargoEvaluador
     * 
     * @return listacmbCargoEvaluador
     */
    public RegistroDataModelImpl getListacmbCargoEvaluador() {
        return listacmbCargoEvaluador;
    }

    /**
     * Asigna la lista listacmbCargoEvaluador
     * 
     * @param listacmbCargoEvaluador
     * Variable a asignar en listacmbCargoEvaluador
     */
    public void setListacmbCargoEvaluador(
        RegistroDataModelImpl listacmbCargoEvaluador) {
        this.listacmbCargoEvaluador = listacmbCargoEvaluador;
    }

    /**
     * Retorna la lista listacmbEscalafonEvaluador
     * 
     * @return listacmbEscalafonEvaluador
     */
    public RegistroDataModelImpl getListacmbEscalafonEvaluador() {
        return listacmbEscalafonEvaluador;
    }

    /**
     * Asigna la lista listacmbEscalafonEvaluador
     * 
     * @param listacmbEscalafonEvaluador
     * Variable a asignar en listacmbEscalafonEvaluador
     */
    public void setListacmbEscalafonEvaluador(
        RegistroDataModelImpl listacmbEscalafonEvaluador) {
        this.listacmbEscalafonEvaluador = listacmbEscalafonEvaluador;
    }

    /**
     * Retorna la lista listacmbCodEmpEvaluador
     * 
     * @return listacmbCodEmpEvaluador
     */
    public RegistroDataModelImpl getListacmbCodEmpEvaluador() {
        return listacmbCodEmpEvaluador;
    }

    /**
     * Asigna la lista listacmbCodEmpEvaluador
     * 
     * @param listacmbCodEmpEvaluador
     * Variable a asignar en listacmbCodEmpEvaluador
     */
    public void setListacmbCodEmpEvaluador(
        RegistroDataModelImpl listacmbCodEmpEvaluador) {
        this.listacmbCodEmpEvaluador = listacmbCodEmpEvaluador;
    }

    public RegistroDataModelImpl getListaPlantilla() {
        return listaPlantilla;
    }

    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
        this.listaPlantilla = listaPlantilla;
    }

    public int getPermiteInsertar() {
        return permiteInsertar;
    }

    public void setPermiteInsertar(int permiteInsertar) {
        this.permiteInsertar = permiteInsertar;
    }

    public boolean isBloqueadoCom() {
        return bloqueadoCom;
    }

    public void setBloqueadoCom(boolean bloqueadoCom) {
        this.bloqueadoCom = bloqueadoCom;
    }

    public String getTituloDetalle() {
        return tituloDetalle;
    }

    public void setTituloDetalle(String tituloDetalle) {
        this.tituloDetalle = tituloDetalle;
    }

    public boolean isVisibleReclamaciones() {
        return visibleReclamaciones;
    }

    public void setVisibleReclamaciones(boolean visibleReclamaciones) {
        this.visibleReclamaciones = visibleReclamaciones;
    }

    public boolean isVisibleCompromisos() {
        return visibleCompromisos;
    }

    public void setVisibleCompromisos(boolean visibleCompromisos) {
        this.visibleCompromisos = visibleCompromisos;
    }

    public boolean isVisibleAjuste() {
        return visibleAjuste;
    }

    public void setVisibleAjuste(boolean visibleAjuste) {
        this.visibleAjuste = visibleAjuste;
    }

    public boolean isBloqueadoEmpleado() {
        return bloqueadoEmpleado;
    }

    public void setBloqueadoEmpleado(boolean bloqueadoEmpleado) {
        this.bloqueadoEmpleado = bloqueadoEmpleado;
    }

    public boolean isVisibleAne() {
        return visibleAne;
    }

    public void setVisibleAne(boolean visibleAne) {
        this.visibleAne = visibleAne;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
