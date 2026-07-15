/*-
 * PlanDeAccionControlador.java
 *
 * 1.0
 * 
 * 19/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.CuentasControladorEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.plandesarrollo.enums.PlanDeAccionControladorEnum;
import com.sysman.plandesarrollo.enums.PlanDeAccionControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administar el plan de accion
 *
 * @version 1.0, 19/04/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class PlanDeAccionControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual ingreso en aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que almacena el valor de la vigencia recibida por
     * parametro
     */
    private String vigencia;
    /**
     * Atributo que almacena el valor del parametro recibido
     * administrador
     */
    private boolean esAdministrador;
    /**
     * * Atributo que almacena el valor del parametro recibido
     * jefeUnidad
     */
    private boolean esJefeUnidad;
    /**
     * Atributo que almacena el valor del parametro recibido numero
     */
    private String numero;
    /**
     * Atributo que almacena el valor del parametro recibido
     * predecesor
     */
    private String predecesor;
    /**
     * Atributo que almacena el valor del parametro recibido
     * digitosAccion
     */
    private int digitosAccion;
    /**
     * Atributo que almacena el valor del parametro recibido
     * digitosAccion
     */
    private int digitosMetaProducto;
    /**
     * Atributo que almacena el valor del parametro recibido
     * dependencia
     */
    private String dependencia;
    /**
     * Atributo que almacena el valor del parametro recibido tipo
     */
    private String tipo;

    /**
     * Atributo que almacena el valor del id del registro seleccionado
     */
    private String idPlan;
    /**
     * Atributo que almacena el id del arbol del registro seleccionado
     */
    private String idPlanIndicativo;

    /**
     * Atributo que almacena el nombre del proyecto seleccionado
     */
    private String titulo;

    /**
     * Atributo que almacena el nombre del proyecto seleccionado
     */
    private String tituloSub;
    /**
     * Atributo que valida si el campo de CODIGOBPIM, se bloquea o no
     */
    private boolean bloquearCodigo;
    /**
     * Atributo que valida si el subFormulario plan de accion permite
     * realizar actualizacion
     */
    private boolean permiteCrud;
    /**
     * Atributo que valida si el subformulario plan de accion fuentes
     * permite realizar operacions crud
     */
    private boolean permiteCrudSubFuentes;
    /**
     * Atributo que valida si el
     */
    private boolean esUsuarioConsulta;
    /**
     * Atributo que almacena la vigencia final recibida por parametro
     */
    private String vigenciaFinal;
    /**
     * Atributo que almacena la vigencia inicial recibida por
     * parametro
     */
    private String vigenciaInicial;
    /**
     * Atributo que almacena el valor de la sucursal del responable
     * seleccionado
     */
    private String sucursal;
    /**
     * Atributo que valida si los campos de obligaciones en el
     * subformulario de fuentes se hacen visibles o no
     */
    private boolean mostrarObligaciones;
    /**
     * Atributo que valida si los campos de pagado en el subformulario
     * de fuentes se hacen visibles o no
     */
    private boolean mostrarPagado;
    /**
     * Atributo que valida si los campos de fuentes y valor de
     * presupuesto se bloquea o no
     */
    private boolean bloqueaCampos;

    /**
     * Atributo que valida si el campo de pagado se bloquea o no
     */
    private boolean bloqueaPagado;
    /**
     * Atributo que valida si el campo de comprometido se bloquea o no
     */
    private boolean bloqueaComprometido;
    /**
     * Atributo que almacena el valor de la fuente seleccionda
     */
    private String fuenteRecurso;
    /**
     * Atributo que almacena el valor del ano seleccionado en el sub
     * formulario fuente
     */
    private String vigenciaMeta;
    /**
     * Atributo que almacena el valor del presupuesto anterior al
     * seleccionar un registro del subformulario plan de accion
     * fuentes
     */
    private String presupuestoAnterior;
    /**
     * Atributo que almacena el valor del porcentaje comprometido
     * anterior
     */
    private String porcentajeComprometidoAnt;
    /**
     * Atributo que almacena el valor comprometido anterior
     */
    private String comprometidoAnterior;
    /**
     * Atributo que almacena el valor del porcentaje pagado anterior
     */
    private String porcentajePagadoAnt;
    /**
     * Atributo que almacena el valor pagado anterior
     */
    private String pagadoAnterior;
    /**
     * Atributo que almacena el valor del porcentaje obligacion
     * anterior
     */
    private String porcentajeObligacionAnt;
    /**
     * Atributo que almacena el valor obligacion anterior
     */
    private String obligacionAnterior;
    /**
     * Atributo que almacena el nombre de id del plan seleccionado en
     * el arbol
     */
    private String nombrePlan;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de los anos de acuerdo al periodo de
     * vigencia
     */
    private List<Registro> listaVigenciaMeta;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los responsables
     */
    private RegistroDataModelImpl listaResponsable;
    /**
     * Lista de registros de los responsables
     */
    private RegistroDataModelImpl listaResponsableE;
    /**
     * Lista de registros de los codigos bpin
     */
    private RegistroDataModelImpl listaCodigobpim;
    /**
     * Lista de registros de los codigos bpin
     */
    private RegistroDataModelImpl listaCodigobpimE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Lista de registros de las fuentes
     */
    private RegistroDataModelImpl listaFuente;
    /**
     * Lista de registros de las fuentes
     */
    private RegistroDataModelImpl listaFuenteE;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista de registros de plan indicativo
     */
    private RegistroDataModelImpl listaAccion;
    /**
     * Lista de registros de las fuentes del plan indicativo
     */
    private RegistroDataModelImpl listaAccionfuente;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario Accion
     */
    private Registro registroSubAccion;
    /**
     * Atributo de referencia para el subformulario AccionFuente
     */
    private Registro registroSubAccionFuente;

    /**
     * Indice del registro activo en el subformulario
     * Formularioplanaccion.
     */
    private int indiceAccion;
    /**
     * Indice del registro activo en el subformulario
     * Formularioplanaccionfuente.
     */
    private int indiceAccionfuente;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPlanDesarrolloDosRemote ejbPlanDesarrolloDos;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlanDeAccionControlador
     */
    public PlanDeAccionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1771
            numFormulario = GeneralCodigoFormaEnum.PLAN_DE_ACCION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubAccion = new Registro(new HashMap<String, Object>());
            registroSubAccionFuente = new Registro(
                            new HashMap<String, Object>());
            cargarValoresDefecto();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                vigencia = parametrosEntrada
                                .get("vigencia").toString();
                tipo = parametrosEntrada
                                .get("tipo").toString();
                esAdministrador = (boolean) parametrosEntrada
                                .get("administrador");
                esJefeUnidad = (boolean) parametrosEntrada
                                .get("jefeUnidad");
                dependencia = SysmanFunciones.nvl(
                                parametrosEntrada.get("dependencia"),
                                "").toString();
                numero = SysmanFunciones.nvlStr(
                                parametrosEntrada.get("numero").toString(),
                                "");
                predecesor = SysmanFunciones.nvl(
                                parametrosEntrada.get("predecesor"),
                                "").toString();
                digitosAccion = (int) SysmanFunciones
                                .nvl(parametrosEntrada.get("digitosAccion"), 0);
                digitosMetaProducto = (int) SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get("digitosMetaProducto"), 0);

                esUsuarioConsulta = (boolean) parametrosEntrada
                                .get("esUsuarioConsulta");

                vigenciaFinal = SysmanFunciones.nvl(parametrosEntrada
                                .get("vigenciaFinal"), "").toString();

                vigenciaInicial = SysmanFunciones.nvl(parametrosEntrada
                                .get("vigenciaInicial"), "").toString();

                idPlanIndicativo = SysmanFunciones.nvl(parametrosEntrada
                                .get("idPlan"), "").toString();
                nombrePlan = SysmanFunciones
                                .nvl(parametrosEntrada.get("nombrePlan"), "")
                                .toString().length() > 150 ? SysmanFunciones
                                                .nvl(parametrosEntrada.get(
                                                                "nombrePlan"),
                                                                "")
                                                .toString().substring(0, 150)
                                    : SysmanFunciones
                                                    .nvl(parametrosEntrada.get(
                                                                    "nombrePlan"),
                                                                    "")
                                                    .toString();

            }
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
        cargarListaAccion();
        cargarListaVigenciaMeta();
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
        listaAccion = null;
        listaAccionfuente = null;
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
        tabla = "";
        iniciarListasSub();
        cargarListaResponsable();
        cargarListaResponsableE();
        cargarListaCodigobpim();
        cargarListaCodigobpimE();
        abrirFormulario();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaAccion
     *
     */
    public void cargarListaAccion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.PI_PLAN_INDICATIVO
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(), tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        param.put(PlanDeAccionControladorEnum.PREDECESOR.getValue(),
                        predecesor);
        param.put(PlanDeAccionControladorEnum.ACCION.getValue(),
                        digitosAccion);

        try {
            listaAccion = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.PI_PLAN_INDICATIVO
                                                            .getTable()));
        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaAccionfuente
     */
    public void cargarListaAccionfuente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL497
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeAccionControladorEnum.PLAN.getValue(),
                        idPlan);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

        try {
            listaAccionfuente = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(
                                            urlConexionCache,
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                            .getTable()));
        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigenciaMeta
     */
    public void cargarListaVigenciaMeta() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanDeAccionControladorEnum.ANOINICIAL.getValue(), vigencia);
        param.put(PlanDeAccionControladorEnum.ANOFINAL.getValue(),
                        Integer.valueOf(vigencia) + 3);
        try {
            listaVigenciaMeta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanDeAccionControladorUrlEnum.URL465
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
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsable() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL353
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PlanDeAccionControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsableE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL353
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);

        listaResponsableE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PlanDeAccionControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaCodigobpim
     *
     */
    public void cargarListaCodigobpim() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL616
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigobpim = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PlanDeAccionControladorEnum.BPIN.getValue());

    }

    /**
     * 
     * Carga la lista listaCodigobpim
     *
     */
    public void cargarListaCodigobpimE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL616
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaCodigobpimE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, PlanDeAccionControladorEnum.BPIN.getValue());

    }

    /**
     * 
     * Carga la lista listaFuente
     */
    public void cargarListaFuente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL396
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigenciaMeta);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeAccionControladorEnum.ID_PLAN.getValue(),
                        idPlan);

        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        PlanDeAccionControladorEnum.CODIGO_FUENTE.getValue());

    }

    /**
     * 
     * Carga la lista listaFuente
     */
    public void cargarListaFuenteE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlanDeAccionControladorUrlEnum.URL396
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigenciaMeta);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeAccionControladorEnum.ID_PLAN.getValue(),
                        idPlan);

        listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        PlanDeAccionControladorEnum.CODIGO_FUENTE.getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control VigenciaMeta
     * 
     * 
     */
    public void cambiarVigenciaMeta() {
        // <CODIGO_DESARROLLADO>
        vigenciaMeta = registroSubAccionFuente.getCampos()
                        .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                        .getValue()) == null
                                            ? ""
                                            : registroSubAccionFuente
                                                            .getCampos()
                                                            .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                                                            .getValue())

                                                            .toString();

        try {
            if (verificarPlanAd(Integer.valueOf(vigenciaMeta), "4")) {
                bloqueaCampos = bloqueaComprometido = bloqueaPagado = true;
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4158"));
                registroSubAccionFuente.getCampos()
                                .put(PlanDeAccionControladorEnum.VIGENCIA_META
                                                .getValue(), null);

            }
            else if (!verificarPlanAd(Integer.valueOf(vigenciaMeta), "4")
                && verificarPlanAd(Integer.valueOf(vigenciaMeta), "5")) {
                bloqueaComprometido = bloqueaPagado = true;
            }
            else {
                bloqueaCampos = bloqueaComprometido = bloqueaPagado = false;

            }

        }
        catch (NumberFormatException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        cargarListaFuente();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorPorFuentes
     */
    public void cambiarValorPorFuentes() {
        // <CODIGO_DESARROLLADO>

        try {

            verificarSaldo(registroSubAccionFuente.getCampos()
                            .get(PlanDeAccionControladorEnum.FUENTE.getValue())
                            .toString(),
                            new BigDecimal(registroSubAccionFuente
                                            .getCampos()
                                            .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                                            .getValue())
                                            .toString()),
                            0, 0);

            if (!SysmanFunciones.validarCampoVacio(
                            registroSubAccionFuente.getCampos(),
                            PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                            .getValue())) {
                cambiarComprometido();
            }
            if (!SysmanFunciones.validarCampoVacio(
                            registroSubAccionFuente.getCampos(),
                            PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                            .getValue())) {
                cambiarPagado();
            }
            if (!SysmanFunciones.validarCampoVacio(
                            registroSubAccionFuente.getCampos(),
                            PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                            .getValue())) {
                cambiarObligaciones();
            }
        }
        catch (NumberFormatException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Comprometido
     */
    public void cambiarComprometido() {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(registroSubAccionFuente
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorComprometido = new BigDecimal(registroSubAccionFuente
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorComprometido, "comprometido",
                        PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                        .getValue(),
                        0, 0,
                        PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue(),
                        "0", "0");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Pagado
     */
    public void cambiarPagado() {
        // <CODIGO_DESARROLLADO>

        BigDecimal valorPresupuesto = new BigDecimal(registroSubAccionFuente
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorPagado = new BigDecimal(registroSubAccionFuente
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                        .getValue())
                        .toString());

        validarValores(valorPresupuesto, valorPagado, "pagado",
                        PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                                        .getValue(),
                        0, 0,
                        PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                        .getValue(),
                        "0", "0");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Obligaciones
     */
    public void cambiarObligaciones() {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(registroSubAccionFuente
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorObligaciones = new BigDecimal(registroSubAccionFuente
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue())
                        .toString());

        validarValores(valorPresupuesto, valorObligaciones, "de obligaciones",
                        PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                        .getValue(),
                        0, 0,
                        PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue(),
                        "0", "0");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Responsable en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarResponsableC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaAccion.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Fuente en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFuenteC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        listaAccionfuente.getDatasource().get(rowNum % 10).getCampos()
                        .put("FUENTE", fuenteRecurso);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control VigenciaMeta en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVigenciaMetaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        vigenciaMeta = listaAccionfuente.getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                        .getValue()) == null
                                            ? ""
                                            : listaAccionfuente
                                                            .getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                                                            .getValue())
                                                            .toString();

        try {
            if (verificarPlanAd(Integer.valueOf(vigenciaMeta), "4")) {
                bloqueaCampos = bloqueaComprometido = bloqueaPagado = true;
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4158"));
                listaAccionfuente.getDatasource().get(rowNum % 10).getCampos()
                                .put(PlanDeAccionControladorEnum.VIGENCIA_META
                                                .getValue(),
                                                null);

            }
            else if (!verificarPlanAd(Integer.valueOf(vigenciaMeta), "4")
                && verificarPlanAd(Integer.valueOf(vigenciaMeta), "5")) {
                bloqueaComprometido = bloqueaPagado = true;
            }
            else {
                bloqueaCampos = bloqueaComprometido = bloqueaPagado = false;

            }

        }
        catch (NumberFormatException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        cargarListaFuenteE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorPorFuentes en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorPorFuentesC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        try {
            verificarSaldo(listaAccionfuente.getDatasource().get(rowNum % 10)
                            .getCampos()
                            .get(PlanDeAccionControladorEnum.FUENTE.getValue())
                            .toString(),
                            new BigDecimal(listaAccionfuente.getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                                            .getValue())
                                            .toString()),
                            1, rowNum);
            if (!SysmanFunciones.validarCampoVacio(
                            listaAccionfuente.getDatasource().get(rowNum % 10)
                                            .getCampos(),
                            PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                            .getValue())) {
                cambiarComprometidoC(rowNum);
            }
            if (!SysmanFunciones.validarCampoVacio(
                            listaAccionfuente.getDatasource().get(rowNum % 10)
                                            .getCampos(),
                            PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                            .getValue())) {
                cambiarPagadoC(rowNum);
            }
            if (!SysmanFunciones.validarCampoVacio(
                            listaAccionfuente.getDatasource().get(rowNum % 10)
                                            .getCampos(),
                            PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                            .getValue())) {
                cambiarObligacionesC(rowNum);
            }
        }
        catch (NumberFormatException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Comprometido en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarComprometidoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(listaAccionfuente
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorComprometido = new BigDecimal(listaAccionfuente
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorComprometido, "comprometido",
                        PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                        .getValue(),
                        1, rowNum,
                        PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue(),
                        porcentajeComprometidoAnt, comprometidoAnterior);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Pagado en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPagadoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(listaAccionfuente
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorPagado = new BigDecimal(listaAccionfuente
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorPagado, "pagado",
                        PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                                        .getValue(),
                        1, rowNum,
                        PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                        .getValue(),
                        porcentajePagadoAnt, pagadoAnterior);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Obligaciones en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarObligacionesC(int rowNum) {
        BigDecimal valorPresupuesto = new BigDecimal(listaAccionfuente
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorObligaciones = new BigDecimal(listaAccionfuente
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorObligaciones, "de obligaciones",
                        PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                        .getValue(),
                        1, rowNum,
                        PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue(),
                        porcentajeObligacionAnt, obligacionAnterior);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Eliminar en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEliminarC(int rowNum) {
        try {
            boolean validarEliminacion;

            validarEliminacion = ejbPlanDesarrolloDos
                            .validarEliminacionPlanIndicativo(compania,
                                            Integer.valueOf(vigencia), tipo,
                                            new BigInteger(numero), idPlan,
                                            new BigInteger(String.valueOf(
                                                            digitosAccion)));

            if (!SysmanFunciones
                            .validarVariableVacio(
                                            SysmanFunciones.nvl(
                                                            listaAccion.getDatasource()
                                                                            .get(rowNum
                                                                                % 10)
                                                                            .getCampos()
                                                                            .get("CODIGOBPIM"),
                                                            "").toString())) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4087"));
                listaAccion.getDatasource().get(rowNum % 10).getCampos()
                                .put("DEBE_ELIMINAR", false);

            }
            else if (validarEliminacion) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4089"));
                listaAccion.getDatasource().get(rowNum % 10).getCampos()
                                .put("DEBE_ELIMINAR", false);

            }
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsable(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubAccion.getCampos().put(
                        GeneralParameterEnum.RESPONSABLE.getName(),
                        registroAux.getCampos()
                                        .get(PlanDeAccionControladorEnum.NIT
                                                        .getValue()));
        registroSubAccion.getCampos().put(
                        GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsable
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(PlanDeAccionControladorEnum.NIT
                                                        .getValue()),
                                        "")
                        .toString();
        sucursal = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()) == null
                            ? " "
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName())
                                            .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigobpim
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigobpim(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubAccion.getCampos().put("CODIGOBPIM", registroAux.getCampos()
                        .get(PlanDeAccionControladorEnum.BPIN.getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigobpim
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigobpimE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(PlanDeAccionControladorEnum.BPIN
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubAccionFuente.getCampos().put("FUENTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        fuenteRecurso = SysmanFunciones.nvl(registroAux.getCampos().get(
                        GeneralParameterEnum.CODIGO.getName()), "").toString();

        if (!verificarFuente(fuenteRecurso)) {
            registroSubAccionFuente.getCampos().put("FUENTE", null);
            return;
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteRecurso = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
        if (!verificarFuente(fuenteRecurso)) {
            fuenteRecurso = null;
            return;
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Compras
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirCompras(Registro reg, int indice) {

        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigenciaGubernamental", vigencia);

        param.put("idPlan", reg.getCampos().get("ID_PLAN").toString());

        param.put("digitosAccion", digitosAccion);

        param.put("nombrePlan", nombrePlan);

        param.put("nombrePlanAdquisciones", reg.getCampos()
                        .get(GeneralParameterEnum.DESCRIPCION.getName()));

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_PLAN_ADQUISICIONES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);

    }

    /**
     * Metodo ejecutado al oprimir el boton Programacion
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirProgramacion(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigenciaGubernamental", vigencia);

        param.put("idPlan", reg.getCampos().get("ID_PLAN").toString());

        param.put("digitosAccion", digitosAccion);

        param.put("nombrePlan", nombrePlan);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_PROGRAMACION_FISICA_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Mas
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirMas(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        idPlan = SysmanFunciones.nvl(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.ID_PLAN.getValue()),
                        "").toString();
        titulo = SysmanFunciones
                        .nvl(reg.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
        permiteCrudSubFuentes = true;
        registroSubAccionFuente.getCampos().put(
                        PlanDeAccionControladorEnum.VIGENCIA_META.getValue(),
                        null);
        cargarListaAccionfuente();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Anexos
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirAnexos(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigenciaGubernamental", vigencia);

        param.put("idPlan", reg.getCampos().get("ID_PLAN").toString());

        param.put("digitosAccion", digitosAccion);

        param.put("nombrePlan", nombrePlan);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_ANEXO_PROYECTOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton PoblBeneficiada
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirPoblBeneficiada(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigenciaGubernamental", vigencia);

        param.put("idPlan",
                        reg.getCampos().get("ID_PLAN").toString());

        param.put("digitosAccion", digitosAccion);

        param.put("nombrePlan", nombrePlan);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_POBLACIONBENEFICIADAS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);

        // String[] campos = { "administrador", "jefeUnidad",
        // "dependencia", "predecesor", "digitosMetaProducto",
        // "esUsuarioConsulta", "vigenciaFinal", "tipo",
        // "numero",
        // "vigenciaGubernamental", "idPlan", "digitosAccion",
        // "nombrePlan" };
        // Object[] valores = { esAdministrador,
        // esJefeUnidad, dependencia, predecesor,
        // digitosMetaProducto,
        // esUsuarioConsulta, vigenciaFinal, tipo, numero,
        // vigencia,
        // reg.getCampos().get("ID_PLAN").toString(),
        // digitosAccion,
        // nombrePlan };
        //
        // SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(
        // GeneralCodigoFormaEnum.FRM_POBLACIONBENEFICIADAS_CONTROLADOR
        // .getCodigo()),
        // modulo, campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metódo ejecutado al deseleccionar el registro
     */
    public void cancelarSeleccionFila() {
        idPlan = null;
        cargarListaAccionfuente();
    }

    // </METODOS_BOTONES>

    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Accion
     */
    public void agregarRegistroSubAccion() {
        try {

            if (validarNivel()) {
                return;
            }

            registroSubAccion.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.TIPO.getValue(),
                            tipo);
            long idPlanAccion = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.PI_PLAN_INDICATIVO.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania, "'' AND TIPO = ''", tipo,
                                            "'' AND NUMERO = ", numero,
                                            " AND ESNUEVO NOT IN(0)"),
                            "ID_PLAN",
                            SysmanFunciones.padr("-9", digitosAccion, "9"));
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.ID_PLAN.getValue(),
                            idPlanAccion);
            registroSubAccion.getCampos().put(
                            GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                            vigencia);
            registroSubAccion.getCampos().put(
                            GeneralParameterEnum.NUMERO.getName(),
                            numero);
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.VIGENCIA_FINAL
                                            .getValue(),
                            vigenciaFinal);
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.DEPENDENCIA_INI
                                            .getValue(),
                            dependencia);
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.DEPENDENCIA_FIN
                                            .getValue(),
                            dependencia);
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.ESNUEVO
                                            .getValue(),
                            true);
            registroSubAccion.getCampos().put(
                            PlanDeAccionControladorEnum.PREDECESOR.getValue(),
                            predecesor);

            registroSubAccion.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubAccion.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubAccion.getCampos());
            cargarListaAccion();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            registroSubAccion = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Accion
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubAccion(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (validarNivel()) {
                return;
            }
            reg.getCampos().put("RESPONSABLE_INI", reg.getCampos()
                            .get(GeneralParameterEnum.RESPONSABLE.getName()));
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(PlanDeAccionControladorEnum.TIPO.getValue());
            reg.getCampos().remove(GeneralParameterEnum.ID_PLAN.getName());
            reg.getCampos().remove(
                            GeneralParameterEnum.VIGENCIA_INICIAL.getName());
            reg.getCampos().remove("VIGENCIA_FINAL");
            reg.getCampos().remove("DEPENDENCIA_INI");
            reg.getCampos().remove("DEPENDENCIA_FIN");
            reg.getCampos().remove("NOMBRERESPONSABLE");

            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove("PREDECESOR");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            cargarListaAccion();
        }
    }

    /**
     * Metodo de eliminacion del formulario Accion
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubAccion(Registro reg) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Accion
     */
    public void cancelarEdicionAccion() {
        cargarListaAccion();
        cargarListaAccionfuente();
    }

    /**
     * Metodo de insercion del formulario Accionfuente
     */
    public void agregarRegistroSubAccionfuente() {
        try {
            if (validarNivel()) {
                return;
            }

            registroSubAccionFuente.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubAccionFuente.getCampos().put(
                            PlanDeAccionControladorEnum.TIPO.getValue(),
                            tipo);

            registroSubAccionFuente.getCampos().put(
                            GeneralParameterEnum.NUMERO.getName(),
                            numero);
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                            .getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania, "'' AND TIPO = ''", tipo,
                                            "'' AND NUMERO = ", numero, ""),
                            GeneralParameterEnum.CONSECUTIVO.getName(), "1");
            registroSubAccionFuente.getCampos().put(
                            GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
            registroSubAccionFuente.getCampos().put(
                            PlanDeAccionControladorEnum.VIGENCIA_PLAN
                                            .getValue(),
                            vigencia);
            registroSubAccionFuente.getCampos().put(
                            PlanDeAccionControladorEnum.ID_PLAN
                                            .getValue(),
                            idPlan);
            registroSubAccionFuente.getCampos()
                            .remove(PlanDeAccionControladorEnum.NOMBREFUENTE
                                            .getValue());
            registroSubAccionFuente.getCampos()
                            .remove(PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                                            .getValue());
            registroSubAccionFuente.getCampos()
                            .remove(PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                            .getValue());
            registroSubAccionFuente.getCampos()
                            .remove(PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                            .getValue());
            registroSubAccionFuente.getCampos().put(
                            PlanDeAccionControladorEnum.ESNUEVO
                                            .getValue(),
                            true);
            registroSubAccionFuente.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubAccionFuente.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubAccionFuente.getCampos());
            cargarListaAccion();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            registroSubAccionFuente = new Registro(
                            new HashMap<String, Object>());
            registroSubAccionFuente.getCampos()
                            .put(PlanDeAccionControladorEnum.VIGENCIA_META
                                            .getValue(), null);
            cargarValoresDefecto();
        }
    }

    /**
     * Metodo de edicion del formulario Accionfuente
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubAccionfuente(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (validarNivel()) {
                return;
            }

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove(PlanDeAccionControladorEnum.TIPO.getValue());
            reg.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
            reg.getCampos().remove("VIGENCIA_PLAN");
            reg.getCampos().remove(GeneralParameterEnum.ID_PLAN.getName());
            reg.getCampos().remove(PlanDeAccionControladorEnum.NOMBREFUENTE
                            .getValue());
            reg.getCampos().remove(PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                            .getValue());
            reg.getCampos().remove(
                            PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                            .getValue());
            reg.getCampos().remove(
                            PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                            .getValue());
            reg.getCampos().remove("ESNUEVO");
            reg.getCampos().remove("DEPENDENCIA_FIN");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            cargarListaAccionfuente();
        }
    }

    /**
     * Metodo de eliminacion del formulario Accionfuente
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubAccionfuente(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));

            cargarListaAccionfuente();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Accionfuente
     */
    public void cancelarEdicionAccionfuente() {
        cargarListaAccionfuente();
    }

    /**
     * Metodo ejecutado al activar la edicion del subformulario accion
     * 
     * @param reg
     */
    public void activarEdicionAccion(Registro reg) {
        // <CODIGO_DESARROLLADO>
        indiceAccion = reg.getIndice();
        idPlan = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.ID_PLAN.getValue()));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al activar la edicion del subformulario
     * fuentes
     * 
     * @param reg
     */
    public void activarEdicionAccionfuente(Registro reg) {
        // <CODIGO_DESARROLLADO>
        indiceAccionfuente = reg.getIndice();
        vigenciaMeta = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                        .getValue()));
        presupuestoAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue()));
        comprometidoAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue()));
        porcentajeComprometidoAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                        .getValue()));
        pagadoAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                        .getValue()));
        porcentajePagadoAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                                        .getValue()));
        obligacionAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue()));
        porcentajeObligacionAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                        .getValue()));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo que valida si permite registrar, editar un nivel en plan
     * de accion
     * 
     * @return false
     */
    public boolean validarNivel() {
        if (idPlanIndicativo.length() != digitosMetaProducto) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4084"));
            return true;
        }
        return false;
    }

    /**
     * Metodo que valida a traves de un parametro si los campos de
     * obligacione y pagados se hacen visibles o no
     */
    public void validarCamposFuentes() {
        try {
            String manejaObligaciones = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania,
                                            "MANEJA OBLIGACIONES EN PLAN DE ACCION",
                                            modulo,
                                            new Date(), true), "NO");
            if ("SI".equals(manejaObligaciones)) {
                mostrarObligaciones = true;
                mostrarPagado = false;

            }
            else {
                mostrarObligaciones = false;
                mostrarPagado = true;
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo que permite agregar valores por defecto a los campos
     * numericos del subformulario de plan accion fuentes
     */
    public void cargarValoresDefecto() {
        registroSubAccionFuente.getCampos()
                        .put(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue(), 0);
        registroSubAccionFuente.getCampos()
                        .put(PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue(), 0);
        registroSubAccionFuente.getCampos().put(
                        PlanDeAccionControladorEnum.VALOR_PAGADO_FIN.getValue(),
                        0);
        registroSubAccionFuente.getCampos()
                        .put(PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue(), 0);
        registroSubAccionFuente.getCampos().put(
                        PlanDeAccionControladorEnum.PORCENTAJEPAGADO.getValue(),
                        0);
        registroSubAccionFuente.getCampos()
                        .put(PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                        .getValue(), 0);
        registroSubAccionFuente.getCampos()
                        .put(PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                        .getValue(), 0);
    }

    /**
     * Metodo que hace el llamado al metodo verificarPlanAdquisiones
     * de plan de desarrollo 2
     * 
     * @param ano
     * @param valor
     * @return
     */
    public boolean verificarPlanAd(int ano, String valor) {
        boolean bloquear = false;
        try {
            bloquear = ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            ano,
                            valor);
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return bloquear;
    }

    /**
     * Metodo que valida si la fuente seleccionada, existe en la tabla
     * PI_PLAN_INDICATIVO_FUENTES
     * 
     * @return
     */
    public boolean verificarFuente(String fuenteR) {

        boolean verificaFuente = true;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanDeAccionControladorEnum.ID_PLAN_INDICATIVO.getValue(),
                        idPlanIndicativo);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(), tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(), fuenteR);

        try {
            Registro rsFuente = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanDeAccionControladorUrlEnum.URL1632
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsFuente == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB4157")
                                .replace("s$VigenciaMeta$s", vigenciaMeta));
                verificaFuente = false;
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return verificaFuente;

    }

    /**
     * Metodo que hace el llamado al ejb verificarSaldoDisponible, el
     * cual se encarga de verificar si el valor del presupuesto excede
     * al saldo disponible
     * 
     * @param fuente
     * @param valor
     */
    public void verificarSaldo(String fuente, BigDecimal valor, int actualiza,
        int rowNum) {
        try {
            ejbPlanDesarrolloDos.verificarSaldoDisponible(compania,
                            idPlanIndicativo,
                            idPlan, Integer.valueOf(vigencia),
                            Integer.valueOf(vigenciaMeta),
                            Integer.valueOf(vigenciaInicial), tipo,
                            numero, fuente,
                            valor);
        }
        catch (NumberFormatException | SystemException e) {
            actualizarValorPresupuesto(actualiza, rowNum);
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo que actualiza a 0 el valor de presupuesto
     * 
     * @param actualiza
     */
    public void actualizarValorPresupuesto(int actualiza, int rowNum) {
        if (actualiza == 0) {
            registroSubAccionFuente
                            .getCampos()
                            .put(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                            .getValue(), 0);
        }
        else {
            listaAccionfuente.getDatasource().get(rowNum % 10)
                            .getCampos()
                            .put(PlanDeAccionControladorEnum.VALOR_PRESUPUESTO_FIN
                                            .getValue(), presupuestoAnterior);
        }

    }

    /**
     * Metodo que actualiza los campos de los porcentajes de
     * comprometido,pagado y obligaciones
     * 
     * @param valorP
     * @param valorC
     * @param actualiza
     * @param rowNum
     * @param campo
     */
    public void cambiarValores(BigDecimal valorP, BigDecimal valorC,
        int actualiza, int rowNum, String campo) {
        if (valorP.compareTo(BigDecimal.ZERO) != 0) {
            if (valorC.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal porcentaje = valorC.divide(
                                valorP, MathContext.DECIMAL128);
                if (actualiza == 0) {
                    registroSubAccionFuente.getCampos().put(
                                    campo,
                                    porcentaje
                                                    .multiply(new BigDecimal(
                                                                    100)));
                }
                else {
                    listaAccionfuente.getDatasource().get(rowNum
                        % 10).getCampos().put(
                                        campo,
                                        porcentaje
                                                        .multiply(new BigDecimal(
                                                                        100)));
                }

            }
            else {
                registroSubAccionFuente.getCampos().put(
                                PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                                .getValue(),
                                0);
            }

        }
        else {

            if (actualiza == 0) {
                registroSubAccionFuente.getCampos().put(
                                campo,
                                0);
            }
            else {
                listaAccionfuente.getDatasource().get(rowNum % 10).getCampos()
                                .put(
                                                campo, 0);
            }
        }
    }

    /**
     * Metodo que realiza la validacion de los campos comprometido,
     * pagado y obligaciones
     * 
     * @param valor
     */
    public void validarValores(BigDecimal valorPresupuesto, BigDecimal valor,
        String campoMensaje, String campoActualizaP, int actualiza,
        int rowNum, String campoActualiza, String porAnterior,
        String valorAnterior) {

        if (valorPresupuesto.compareTo(valor) < 0) {

            enviarAlertaCampos(campoMensaje, actualiza, rowNum, campoActualiza,
                            campoActualizaP, porAnterior, valorAnterior);
            return;

        }

        if (valorPresupuesto.compareTo(BigDecimal.ZERO) != 0) {
            if (valor.compareTo(BigDecimal.ZERO) != 0) {

                cambiarValores(valorPresupuesto, valor, actualiza, rowNum,
                                campoActualizaP);

            }
            else {
                cambiarValores(valorPresupuesto, valor, actualiza, rowNum,
                                campoActualizaP);
            }

        }
        else {
            cambiarValores(valorPresupuesto, valor, actualiza, rowNum,
                            campoActualizaP);
        }
    }

    /**
     * Metodo que ejecuta una alerta si se cumple con la condicion en
     * el metodo validarValores
     * 
     * @param campo
     * @param actualiza
     * @param rowNum
     * @param campoActualiza
     */
    public void enviarAlertaCampos(String campoMensaje, int actualiza,
        int rowNum, String campoActualiza, String campoActualizaP,
        String porAnterior, String valorAnterior) {
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4154")
                        .replace("s$valor$s", campoMensaje));

        if (actualiza == 0) {
            registroSubAccionFuente.getCampos().put(campoActualiza, 0);
        }
        else {
            listaAccionfuente.getDatasource().get(rowNum % 10).getCampos().put(
                            campoActualiza, valorAnterior);
            listaAccionfuente.getDatasource().get(rowNum % 10).getCampos().put(
                            campoActualizaP, porAnterior);
        }

        return;
    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        titulo = "";
        tituloSub = "";

        try {
            String codigoBpim = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CODIGO BPIM A 14 DIGITOS", modulo,
                                            new Date(), false), "NO");
            if ("SI".equals(codigoBpim)) {
                registroSubAccion
                                .getCampos().put(
                                                PlanDeAccionControladorEnum.CODIGOBPIM
                                                                .getValue(),
                                                "0000000000000");
            }

            if (!esAdministrador) {
                bloquearCodigo = true;
            }
            else {
                bloquearCodigo = false;
            }

            if (esUsuarioConsulta) {
                permiteCrud = false;
            }
            else {
                permiteCrud = true;
            }
            validarCamposFuentes();

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
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
    public boolean insertarAntes() {
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
     * @return true
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
     * @return true
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
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna el objeto indiceAccion
     * 
     * @return indiceAccion
     */
    public int getIndiceAccion() {
        return indiceAccion;
    }

    /**
     * Asigna el objeto indiceAccion
     * 
     * @param indiceAccion
     * Variable a asignar en indiceAccion
     */
    public void setIndiceAccion(int indiceAccion) {
        this.indiceAccion = indiceAccion;
    }

    /**
     * Retorna el objeto indiceAccionfuente
     * 
     * @return indiceAccionfuente
     */
    public int getIndiceAccionfuente() {
        return indiceAccionfuente;
    }

    /**
     * Asigna el objeto indiceAccionfuente
     * 
     * @param indiceAccionfuente
     * Variable a asignar en indiceAccionfuente
     */
    public void setIndiceAccionfuente(int indiceAccionfuente) {
        this.indiceAccionfuente = indiceAccionfuente;
    }

    /**
     * Retorna el objeto titulo
     * 
     * @return titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna el objeto titulo
     * 
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Retorna el objeto tituloSub
     * 
     * @return tituloSub
     */
    public String getTituloSub() {
        return tituloSub;
    }

    /**
     * Asigna el objeto tituloSub
     * 
     * @param tituloSub
     * Variable a asignar en tituloSub
     */
    public void setTituloSub(String tituloSub) {
        this.tituloSub = tituloSub;
    }

    /**
     * Retorna el objeto bloquearCodigo
     * 
     * @return bloquearCodigo
     */
    public boolean isBloquearCodigo() {
        return bloquearCodigo;
    }

    /**
     * Asigna el objeto bloquearCodigo
     * 
     * @param bloquearCodigo
     * Variable a asignar en bloquearCodigo
     */
    public void setBloquearCodigo(boolean bloquearCodigo) {
        this.bloquearCodigo = bloquearCodigo;
    }

    /**
     * Retorna el objeto permiteEditar
     * 
     * @return permiteEditar
     */
    public boolean isPermiteCrud() {
        return permiteCrud;
    }

    /**
     * Asigna el objeto permiteEditar
     * 
     * @param permiteEditar
     * Variable a asignar en permiteEditar
     */
    public void setPermiteCrud(boolean permiteCrud) {
        this.permiteCrud = permiteCrud;
    }

    /**
     * Retorna el objeto permiteCrudSubFuentes
     * 
     * @return permiteCrudSubFuentes
     */
    public boolean isPermiteCrudSubFuentes() {
        return permiteCrudSubFuentes;
    }

    /**
     * Asigna el objeto permiteCrudSubFuentes
     * 
     * @param permiteCrudSubFuentes
     * Variable a asignar en permiteCrudSubFuentes
     */
    public void setPermiteCrudSubFuentes(boolean permiteCrudSubFuentes) {
        this.permiteCrudSubFuentes = permiteCrudSubFuentes;
    }

    /**
     * Retorna el objeto mostrarObligaciones
     * 
     * @return mostrarObligaciones
     */
    public boolean isMostrarObligaciones() {
        return mostrarObligaciones;
    }

    /**
     * Asigna el objeto mostrarObligaciones
     * 
     * @param mostrarObligaciones
     * Variable a asignar en mostrarObligaciones
     */
    public void setMostrarObligaciones(boolean mostrarObligaciones) {
        this.mostrarObligaciones = mostrarObligaciones;
    }

    /**
     * Retorna el objeto mostrarPagado
     * 
     * @return mostrarPagado
     */
    public boolean isMostrarPagado() {
        return mostrarPagado;
    }

    /**
     * Asigna el objeto mostrarPagado
     * 
     * @param mostrarPagado
     * Variable a asignar en mostrarPagado
     */
    public void setMostrarPagado(boolean mostrarPagado) {
        this.mostrarPagado = mostrarPagado;
    }

    /**
     * Retorna el objeto bloqueaCampos
     * 
     * @return bloqueaCampos
     */
    public boolean isBloqueaCampos() {
        return bloqueaCampos;
    }

    /**
     * Asigna el objeto bloqueaCampos
     * 
     * @param bloqueaCampos
     * Variable a asignar en bloqueaCampos
     */
    public void setBloqueaCampos(boolean bloqueaCampos) {
        this.bloqueaCampos = bloqueaCampos;
    }

    /**
     * Retorna el objeto bloqueaPagado
     * 
     * @return bloqueaPagado
     */
    public boolean isBloqueaPagado() {
        return bloqueaPagado;
    }

    /**
     * Asigna el objeto bloqueaPagado
     * 
     * @param bloqueaPagado
     * Variable a asignar en bloqueaPagado
     */
    public void setBloqueaPagado(boolean bloqueaPagado) {
        this.bloqueaPagado = bloqueaPagado;
    }

    /**
     * Retorna el objeto bloqueaComprometido
     * 
     * @return bloqueaComprometido
     */
    public boolean isBloqueaComprometido() {
        return bloqueaComprometido;
    }

    /**
     * Asigna el objeto bloqueaComprometido
     * 
     * @param bloqueaComprometido
     * Variable a asignar en bloqueaComprometido
     */
    public void setBloqueaComprometido(boolean bloqueaComprometido) {
        this.bloqueaComprometido = bloqueaComprometido;
    }

    /**
     * Retorna el objeto fuenteRecurso
     * 
     * @return fuenteRecurso
     */
    public String getFuenteRecurso() {
        return fuenteRecurso;
    }

    /**
     * Asigna el objeto fuenteRecurso
     * 
     * @param fuenteRecurso
     * Variable a asignar en fuenteRecurso
     */
    public void setFuenteRecurso(String fuenteRecurso) {
        this.fuenteRecurso = fuenteRecurso;
    }

    /**
     * Retorna el objeto vigenciaMeta
     * 
     * @return vigenciaMeta
     */
    public String getVigenciaMeta() {
        return vigenciaMeta;
    }

    /**
     * Retorna el objeto nombrePlan
     * 
     * @return nombrePlan
     */
    public String getNombrePlan() {
        return nombrePlan;
    }

    /**
     * Asigna el objeto nombrePlan
     * 
     * @param nombrePlan
     * Variable a asignar en nombrePlan
     */
    public void setNombrePlan(String nombrePlan) {
        this.nombrePlan = nombrePlan;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigenciaMeta
     * 
     * @return listaVigenciaMeta
     */
    public List<Registro> getListaVigenciaMeta() {
        return listaVigenciaMeta;
    }

    /**
     * Asigna la lista listaVigenciaMeta
     * 
     * @param listaVigenciaMeta
     * Variable a asignar en listaVigenciaMeta
     */
    public void setListaVigenciaMeta(List<Registro> listaVigenciaMeta) {
        this.listaVigenciaMeta = listaVigenciaMeta;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsable() {
        return listaResponsable;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsable(RegistroDataModelImpl listaResponsable) {
        this.listaResponsable = listaResponsable;
    }

    /**
     * Retorna la lista listaResponsable
     * 
     * @return listaResponsable
     */
    public RegistroDataModelImpl getListaResponsableE() {
        return listaResponsableE;
    }

    /**
     * Asigna la lista listaResponsable
     * 
     * @param listaResponsable
     * Variable a asignar en listaResponsable
     */
    public void setListaResponsableE(RegistroDataModelImpl listaResponsableE) {
        this.listaResponsableE = listaResponsableE;
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

    /**
     * Retorna la lista listaFuente
     * 
     * @return listaFuente
     */
    public RegistroDataModelImpl getListaFuente() {
        return listaFuente;
    }

    /**
     * Asigna la lista listaFuente
     * 
     * @param listaFuente
     * Variable a asignar en listaFuente
     */
    public void setListaFuente(RegistroDataModelImpl listaFuente) {
        this.listaFuente = listaFuente;
    }

    /**
     * Retorna la lista listaFuente
     * 
     * @return listaFuente
     */
    public RegistroDataModelImpl getListaFuenteE() {
        return listaFuenteE;
    }

    /**
     * Asigna la lista listaFuente
     * 
     * @param listaFuente
     * Variable a asignar en listaFuente
     */
    public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
        this.listaFuenteE = listaFuenteE;
    }

    /**
     * Retorna la lista listaCodigobpim
     * 
     * @return listaCodigobpim
     */
    public RegistroDataModelImpl getListaCodigobpim() {
        return listaCodigobpim;
    }

    /**
     * Asigna la lista listaCodigobpim
     * 
     * @param listaCodigobpim
     * Variable a asignar en listaCodigobpim
     */
    public void setListaCodigobpim(RegistroDataModelImpl listaCodigobpim) {
        this.listaCodigobpim = listaCodigobpim;
    }

    /**
     * Retorna la lista listaCodigobpim
     * 
     * @return listaCodigobpim
     */
    public RegistroDataModelImpl getListaCodigobpimE() {
        return listaCodigobpimE;
    }

    /**
     * Asigna la lista listaCodigobpim
     * 
     * @param listaCodigobpim
     * Variable a asignar en listaCodigobpim
     */
    public void setListaCodigobpimE(RegistroDataModelImpl listaCodigobpimE) {
        this.listaCodigobpimE = listaCodigobpimE;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaAccion
     * 
     * @return listaAccion
     */
    public RegistroDataModelImpl getListaAccion() {
        return listaAccion;
    }

    /**
     * Asigna la lista listaAccion
     * 
     * @param listaAccion
     * Variable a asignar en listaAccion
     */
    public void setListaAccion(RegistroDataModelImpl listaAccion) {
        this.listaAccion = listaAccion;
    }

    /**
     * Retorna la lista listaAccionfuente
     * 
     * @return listaAccionfuente
     */
    public RegistroDataModelImpl getListaAccionfuente() {
        return listaAccionfuente;
    }

    /**
     * Asigna la lista listaAccionfuente
     * 
     * @param listaAccionfuente
     * Variable a asignar en listaAccionfuente
     */
    public void setListaAccionfuente(RegistroDataModelImpl listaAccionfuente) {
        this.listaAccionfuente = listaAccionfuente;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubAccion
     * 
     * @return registroSubAccion
     */
    public Registro getRegistroSubAccion() {
        return registroSubAccion;
    }

    /**
     * Asigna el objeto registroSubAccion
     * 
     * @param registroSubAccion
     * Variable a asignar en registroSubAccion
     */
    public void setRegistroSubAccion(Registro registroSubAccion) {
        this.registroSubAccion = registroSubAccion;
    }

    /**
     * Retorna el objeto registroSubAccionFuente
     * 
     * @return registroSubAccionFuente
     */
    public Registro getRegistroSubAccionFuente() {
        return registroSubAccionFuente;
    }

    /**
     * Asigna el objeto registroSubAccionFuente
     * 
     * @param registroSubAccionFuente
     * Variable a asignar en registroSubAccionFuente
     */
    public void setRegistroSubAccionFuente(Registro registroSubAccionFuente) {
        this.registroSubAccionFuente = registroSubAccionFuente;
    }
    // </SET_GET_ADICIONALES>
}
