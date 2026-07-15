/*-
 * PlanDeMetasControlador.java
 *
 * 1.0
 * 
 * 23/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.plandesarrollo.enums.PlanDeAccionControladorEnum;
import com.sysman.plandesarrollo.enums.PlanDeAccionControladorUrlEnum;
import com.sysman.plandesarrollo.enums.PlanDeMetasControladorEnum;
import com.sysman.plandesarrollo.enums.PlanDeMetasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para administrar el plan de metas y de fuentes
 *
 * @version 1.0, 23/07/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class PlanDeMetasControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almancena el codigo del modulo por el cual se
     * ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor de la vigencia recibida por
     * parametro
     */
    private String vigencia;
    /**
     * Atributo que almacena el valor del parametro recibido tipo
     */
    private String tipo;

    /**
     * Atributo que almacena el id del arbol del registro seleccionado
     */
    private String idPlanIndicativo;
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
     * Atributo que almacena la vigencia final recibida por parametro
     */
    private String vigenciaFinal;
    /**
     * Atributo que almacena la vigencia inicial recibida por
     * parametro
     */
    private String vigenciaInicial;
    /**
     * Atributo que permite validar si los campos del subformulario
     * fuentes se bloquean o no
     */
    private boolean bloquearCampos;
    /**
     * Atributo que valida si el
     */
    private boolean esUsuarioConsulta;
    /**
     * Atributo que almacena el valor del ano seleccionado en el sub
     * formulario fuente meta
     */
    private String vigenciaMetaFuente;

    private String nombreFuente;
    /**
     * Atributo que almacena el nombre de id del plan seleccionado en
     * el arbol
     */
    private String nombrePlan;
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
     * Atributo que almacena el valor de la vigencia meta anterior del
     * registro seleccionado en el subformulario "Asignacion por
     * fuentes"
     */
    private String vigenciaMetaFueAnt;

    /**
     * Indice del registro activo en el subformulario Formulario
     * Asignacion por fuentes .
     */
    private int indicePlanindicativofuentes;
    /**
     * Atributo que valida si el subformulario plan de accion fuentes
     * permite realizar operacions crud
     */
    private boolean permiteCrudSubFuentes;
    /**
     * Atributo que almacena el valor del presupuesto anterior al
     * seleccionar un registro del subformulario plan de accion
     * fuentes
     */
    private String presupuestoFueAnterior;
    /**
     * Atributo que almacena el valor del porcentaje comprometido
     * anterior
     */
    private String porcentajeComprometidoFueAnt;
    /**
     * Atributo que almacena el valor comprometido anterior
     */
    private String comprometidoFueAnterior;
    /**
     * Atributo que almacena el valor del porcentaje pagado anterior
     */
    private String porcentajePagadoFueAnt;
    /**
     * Atributo que almacena el valor pagado anterior
     */
    private String pagadoFueAnterior;
    /**
     * Atributo que almacena el valor del porcentaje obligacion
     * anterior
     */
    private String porcentajeObligacionFueAnt;
    /**
     * Atributo que almacena el valor obligacion anterior
     */
    private String obligacionFueAnterior;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registro de anos segun la vigencia seleccionada
     */
    private List<Registro> listaVigenciaMeta;
    /**
     * Lista de registro de anos segun la vigencia seleccionada
     */
    private List<Registro> listaVigenciaMetaFuente;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registro de las fuentes segun el ano seleccionado
     */
    private RegistroDataModelImpl listaFuente;
    /**
     * Lista de registro de las fuentes segun el ano seleccionado
     */
    private RegistroDataModelImpl listaFuenteE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista de registros del subformulario metas
     */
    private RegistroDataModelImpl listaPlanindicativometas;
    /**
     * Lista de registro del subformulario fuentes
     */
    private RegistroDataModelImpl listaPlanindicativofuentes;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPlanDesarrolloDosRemote ejbPlanDesarrolloDos;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     * PlanIndicativoMetas
     */
    private Registro registroSubPlanIndicativoMetas;
    /**
     * Atributo de referencia para el subformulario
     * PlanIndicativoFuentes
     */
    private Registro registroSubPlanIndicativoFuentes;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlanDeMetasControlador
     */
    public PlanDeMetasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 1867
            numFormulario = GeneralCodigoFormaEnum.PLAN_DE_METAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubPlanIndicativoMetas = new Registro(
                            new HashMap<String, Object>());
            registroSubPlanIndicativoFuentes = new Registro(
                            new HashMap<String, Object>());
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
                                .get("predecesor"), "").toString();
                nombrePlan = SysmanFunciones.nvl(parametrosEntrada
                                .get("nombrePlan"), "").toString();

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
        cargarListaPlanindicativometas();
        cargarListaPlanindicativofuentes();
        cargarListaVigenciaMeta();
        cargarListaVigenciaMetaFuente();
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
        listaPlanindicativometas = null;
        listaPlanindicativofuentes = null;
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
        abrirFormulario();

    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    /**
     * 
     * Carga la lista listaPlanindicativometas
     *
     */
    public void cargarListaPlanindicativometas() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.PI_PLAN_INDICATIVO_METAS
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);
        param.put(PlanDeMetasControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeMetasControladorEnum.PLAN.getValue(),
                        idPlanIndicativo);

        try {
            listaPlanindicativometas = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_METAS
                                                            .getTable()));
        }
        catch (SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPlanindicativofuentes
     *
     */
    public void cargarListaPlanindicativofuentes() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                        .getGridKey());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeAccionControladorEnum.PLAN.getValue(),
                        idPlanIndicativo);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

        try {
            listaPlanindicativofuentes = new RegistroDataModelImpl(
                            urlBean.getUrl(),
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
     *
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
                                                            PlanDeMetasControladorUrlEnum.URL405
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
     * Carga la lista listaVigenciaMetaFuente
     *
     */
    public void cargarListaVigenciaMetaFuente() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PlanDeAccionControladorEnum.ANOINICIAL.getValue(), vigencia);
        param.put(PlanDeAccionControladorEnum.ANOFINAL.getValue(),
                        Integer.valueOf(vigencia) + 3);
        try {
            listaVigenciaMetaFuente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlanDeMetasControladorUrlEnum.URL405
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
     * Carga la lista listaFuente
     *
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
                        vigenciaMetaFuente);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeAccionControladorEnum.ID_PLAN.getValue(),
                        idPlanIndicativo);

        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        PlanDeAccionControladorEnum.CODIGO_FUENTE.getValue());

    }

    /**
     * Carga la lista listaFuente al editar registro del
     * subformulario.
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
                        vigenciaMetaFuente != null ? vigenciaMetaFuente
                            : vigenciaMetaFueAnt);
        param.put(PlanDeAccionControladorEnum.TIPO.getValue(),
                        tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        numero);
        param.put(PlanDeAccionControladorEnum.ID_PLAN.getValue(),
                        idPlanIndicativo);

        listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        PlanDeAccionControladorEnum.CODIGO_FUENTE.getValue());

        listaFuenteE.load();
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Fuente en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFuenteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaPlanindicativofuentes.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBREFUENTE", nombreFuente);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control VigenciaMetaFuente
     * 
     * 
     */
    public void cambiarVigenciaMetaFuente() {
        // <CODIGO_DESARROLLADO>
        vigenciaMetaFuente = registroSubPlanIndicativoFuentes.getCampos()
                        .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                        .getValue()) == null
                                            ? ""
                                            : registroSubPlanIndicativoFuentes
                                                            .getCampos()
                                                            .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                                                            .getValue())

                                                            .toString();
        cargarListaFuente();
        if (!esAdministrador) {
            bloquearCampos = verificarPlanAd(
                            Integer.parseInt(vigenciaMetaFuente), "2");
        }
        else {
            bloquearCampos = false;
        }
        validarVigenciaMetaFuente(1, 0);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorPorFuentesF
     * 
     * 
     */
    public void cambiarValorPorFuentesF() {
        // <CODIGO_DESARROLLADO>
        try {

            if (!SysmanFunciones.validarCampoVacio(
                            registroSubPlanIndicativoFuentes.getCampos(),
                            PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                            .getValue())) {
                cambiarComprometidoF();
            }
            if (!SysmanFunciones.validarCampoVacio(
                            registroSubPlanIndicativoFuentes.getCampos(),
                            PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                            .getValue())) {
                cambiarPagadoF();
            }
            if (!SysmanFunciones.validarCampoVacio(
                            registroSubPlanIndicativoFuentes.getCampos(),
                            PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                            .getValue())) {
                cambiarObligacionesF();
            }
        }
        catch (NumberFormatException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ComprometidoF
     * 
     * 
     */
    public void cambiarComprometidoF() {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(
                        registroSubPlanIndicativoFuentes
                                        .getCampos()
                                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                                        .getValue())
                                        .toString());
        BigDecimal valorComprometido = new BigDecimal(
                        registroSubPlanIndicativoFuentes
                                        .getCampos()
                                        .get(PlanDeMetasControladorEnum.VALOR_COMPROMETIDO_FIN
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
     * Metodo ejecutado al cambiar el control PagadoF
     * 
     * 
     */
    public void cambiarPagadoF() {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(
                        registroSubPlanIndicativoFuentes
                                        .getCampos()
                                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                                        .getValue())
                                        .toString());
        BigDecimal valorPagado = new BigDecimal(registroSubPlanIndicativoFuentes
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PAGADO_FIN
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
     * Metodo ejecutado al cambiar el control ObligacionesF
     * 
     * 
     */
    public void cambiarObligacionesF() {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(
                        registroSubPlanIndicativoFuentes
                                        .getCampos()
                                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                                        .getValue())
                                        .toString());
        BigDecimal valorObligaciones = new BigDecimal(
                        registroSubPlanIndicativoFuentes
                                        .getCampos()
                                        .get(PlanDeMetasControladorEnum.VALOR_OBLIGACIONES_FIN
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
     * Metodo ejecutado al cambiar el control VigenciaMetaFuente en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVigenciaMetaFuenteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        vigenciaMetaFuente = listaPlanindicativofuentes.getDatasource()
                        .get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                        .getValue()) == null
                                            ? ""
                                            : listaPlanindicativofuentes
                                                            .getDatasource()
                                                            .get(rowNum % 10)
                                                            .getCampos()
                                                            .get(PlanDeAccionControladorEnum.VIGENCIA_META
                                                                            .getValue())
                                                            .toString();
        cargarListaFuenteE();

        if (!esAdministrador) {
            bloquearCampos = verificarPlanAd(
                            Integer.parseInt(vigenciaMetaFuente), "2");
        }
        else {
            bloquearCampos = false;
        }

        validarVigenciaMetaFuente(2, rowNum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorPorFuentesF en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorPorFuentesFC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        try {

            if (!SysmanFunciones.validarCampoVacio(
                            listaPlanindicativofuentes.getDatasource()
                                            .get(rowNum % 10).getCampos(),
                            PlanDeAccionControladorEnum.VALOR_COMPROMETIDO_FIN
                                            .getValue())) {
                cambiarComprometidoFC(rowNum);
            }
            if (!SysmanFunciones.validarCampoVacio(
                            listaPlanindicativofuentes.getDatasource()
                                            .get(rowNum % 10).getCampos(),
                            PlanDeAccionControladorEnum.VALOR_PAGADO_FIN
                                            .getValue())) {
                cambiarPagadoFC(rowNum);
            }
            if (!SysmanFunciones.validarCampoVacio(
                            listaPlanindicativofuentes.getDatasource()
                                            .get(rowNum % 10).getCampos(),
                            PlanDeAccionControladorEnum.VALOR_OBLIGACIONES_FIN
                                            .getValue())) {
                cambiarObligacionesFC(rowNum);
            }
        }
        catch (NumberFormatException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ComprometidoF en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarComprometidoFC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(listaPlanindicativofuentes
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorComprometido = new BigDecimal(listaPlanindicativofuentes
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorComprometido, "comprometido",
                        PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                        .getValue(),
                        1, rowNum,
                        PlanDeMetasControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue(),
                        porcentajeComprometidoFueAnt, comprometidoFueAnterior);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PagadoF en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarPagadoFC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(listaPlanindicativofuentes
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorPagado = new BigDecimal(listaPlanindicativofuentes
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PAGADO_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorPagado, "pagado",
                        PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                                        .getValue(),
                        1, rowNum,
                        PlanDeMetasControladorEnum.VALOR_PAGADO_FIN
                                        .getValue(),
                        porcentajePagadoFueAnt, pagadoFueAnterior);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ObligacionesF en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarObligacionesFC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        BigDecimal valorPresupuesto = new BigDecimal(listaPlanindicativofuentes
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue())
                        .toString());
        BigDecimal valorObligaciones = new BigDecimal(listaPlanindicativofuentes
                        .getDatasource().get(rowNum % 10)
                        .getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue())
                        .toString());
        validarValores(valorPresupuesto, valorObligaciones, "de obligaciones",
                        PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                        .getValue(),
                        1, rowNum,
                        PlanDeMetasControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue(),
                        porcentajeObligacionFueAnt, obligacionFueAnterior);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubPlanIndicativoFuentes.getCampos().put("FUENTE",
                        registroAux.getCampos().get("CODIGO_FUENTE"));
        registroSubPlanIndicativoFuentes.getCampos().put("NOMBREFUENTE",
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuente
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();

        nombreFuente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO_FUENTE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Planindicativometas
     * 
     */
    public void agregarRegistroSubPlanindicativometas() {
        try {
            int conteo;
            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                            "PI_PLAN_INDICATIVO_METAS",
                            registroSubPlanIndicativoMetas.getCampos());
            cargarListaPlanindicativometas();
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubPlanIndicativoMetas = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Planindicativometas
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubPlanindicativometas(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            int conteo;
            conteo = Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                            "PI_PLAN_INDICATIVO_METAS", reg.getCampos(),
                            reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaPlanindicativometas();
        }
    }

    /**
     * Metodo de eliminacion del formulario Planindicativometas
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubPlanindicativometas(Registro reg) {
        try {
            int conteo;
            conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN,
                            "PI_PLAN_INDICATIVO_METAS", reg.getLlave());
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaPlanindicativometas();
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Planindicativometas
     *
     */
    public void cancelarEdicionPlanindicativometas() {
        cargarListaPlanindicativometas();
        cargarListaPlanindicativofuentes();
    }

    /**
     * Metodo de insercion del formulario Planindicativofuentes
     * 
     */
    public void agregarRegistroSubPlanindicativofuentes() {
        try {
            if (validarNivel()) {
                return;
            }
            registroSubPlanIndicativoFuentes.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubPlanIndicativoFuentes.getCampos().put(
                            PlanDeAccionControladorEnum.TIPO.getValue(),
                            tipo);

            registroSubPlanIndicativoFuentes.getCampos().put(
                            GeneralParameterEnum.NUMERO.getName(),
                            numero);
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                            .getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania, "'' AND TIPO = ''", tipo,
                                            "'' AND NUMERO = ", numero, ""),
                            GeneralParameterEnum.CONSECUTIVO.getName(), "1");
            registroSubPlanIndicativoFuentes.getCampos().put(
                            GeneralParameterEnum.CONSECUTIVO.getName(),
                            consecutivo);
            registroSubPlanIndicativoFuentes.getCampos().put(
                            PlanDeAccionControladorEnum.VIGENCIA_PLAN
                                            .getValue(),
                            vigencia);
            registroSubPlanIndicativoFuentes.getCampos().put(
                            PlanDeAccionControladorEnum.ID_PLAN
                                            .getValue(),
                            idPlanIndicativo);
            registroSubPlanIndicativoFuentes.getCampos()
                            .remove(PlanDeAccionControladorEnum.NOMBREFUENTE
                                            .getValue());
            registroSubPlanIndicativoFuentes.getCampos()
                            .remove(PlanDeAccionControladorEnum.PORCENTAJEPAGADO
                                            .getValue());
            registroSubPlanIndicativoFuentes.getCampos()
                            .remove(PlanDeAccionControladorEnum.PORCENTAJEOBLIGACIONES
                                            .getValue());
            registroSubPlanIndicativoFuentes.getCampos()
                            .remove(PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                            .getValue());
            registroSubPlanIndicativoFuentes.getCampos().put(
                            PlanDeAccionControladorEnum.ESNUEVO
                                            .getValue(),
                            true);
            registroSubPlanIndicativoFuentes.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubPlanIndicativoFuentes.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSubPlanIndicativoFuentes.getCampos()
                            .remove("NOMBREFUENTE");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubPlanIndicativoFuentes.getCampos());

            ejbPlanDesarrolloDos.mayorizarMetasyFuentes(compania,
                            Integer.valueOf(vigencia), tipo, numero,
                            SessionUtil.getUser().getCodigo());
            cargarListaPlanindicativofuentes();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);

        }
        finally {
            registroSubPlanIndicativoFuentes = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Planindicativofuentes
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubPlanindicativofuentes(RowEditEvent event) {
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
            reg.getCampos().remove("NOMBREFUENTE");
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
            ejbPlanDesarrolloDos.mayorizarMetasyFuentes(compania,
                            Integer.valueOf(vigencia), tipo, numero,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaPlanindicativofuentes();
        }
    }

    /**
     * Metodo de eliminacion del formulario Planindicativofuentes
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubPlanindicativofuentes(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PI_PLAN_INDICATIVO_FUENTES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(CuentasControladorEnum.MSM_REGISTRO_ELIMINADO
                                            .getValue()));

            cargarListaPlanindicativofuentes();
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Planindicativofuentes
     *
     */
    public void cancelarEdicionPlanindicativofuentes() {
        cargarListaPlanindicativofuentes();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
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
     * Metodo que hace el llamado al metodo verificarPlanAdquisiones,
     * funcion en access planAdquisicionesCerrado de plan de
     * desarrollo 2
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
     * Metodo que se ejecuta al cambiar la vigencia del subformulario
     * "Asignacion por fuentes"
     * 
     * @return
     */
    private boolean validarVigenciaMetaFuente(int opcion, int rowNum) {

        if (verificarPlanAd(Integer.parseInt(vigenciaMetaFuente), "2")
            && esAdministrador) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4180").replace(
                            "s$vigenciaMetaFuentes$s", vigenciaMetaFuente));
            if (opcion == 1) {
                registroSubPlanIndicativoFuentes.getCampos()
                                .put(PlanDeMetasControladorEnum.VIGENCIA_META
                                                .getValue(), null);
            }
            else {
                listaPlanindicativofuentes.getDatasource().get(rowNum
                    % 10).getCampos().put(
                                    PlanDeMetasControladorEnum.VIGENCIA_META
                                                    .getValue(),
                                    vigenciaMetaFueAnt);
            }
            return true;
        }

        return false;
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
                    registroSubPlanIndicativoFuentes.getCampos().put(
                                    campo,
                                    porcentaje
                                                    .multiply(new BigDecimal(
                                                                    100)));
                }
                else {
                    listaPlanindicativofuentes.getDatasource().get(rowNum
                        % 10).getCampos().put(
                                        campo,
                                        porcentaje
                                                        .multiply(new BigDecimal(
                                                                        100)));
                }

            }
            else {
                registroSubPlanIndicativoFuentes.getCampos().put(
                                PlanDeAccionControladorEnum.PORCENTAJECOMPROMETIDO
                                                .getValue(),
                                0);
            }

        }
        else {

            if (actualiza == 0) {
                registroSubPlanIndicativoFuentes.getCampos().put(
                                campo,
                                0);
            }
            else {
                listaPlanindicativofuentes.getDatasource().get(rowNum % 10)
                                .getCampos().put(
                                                campo, 0);
            }
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
            registroSubPlanIndicativoFuentes.getCampos().put(campoActualiza, 0);
        }
        else {
            listaPlanindicativofuentes.getDatasource().get(rowNum % 10)
                            .getCampos().put(
                                            campoActualiza, valorAnterior);
            listaPlanindicativofuentes.getDatasource().get(rowNum % 10)
                            .getCampos().put(
                                            campoActualizaP, porAnterior);
        }

        return;
    }

    /**
     * Metodo ejecutado al activar la edicion del subformulario
     * fuentes
     * 
     * @param reg
     */

    public void activarEdicionPlanindicativofuentes(Registro reg) {
        // <CODIGO_DESARROLLADO>
        indicePlanindicativofuentes = reg.getIndice();
        vigenciaMetaFueAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.VIGENCIA_META
                                        .getValue()));
        presupuestoFueAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PRESUPUESTO_FIN
                                        .getValue()));
        comprometidoFueAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_COMPROMETIDO_FIN
                                        .getValue()));
        porcentajeComprometidoFueAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.PORCENTAJECOMPROMETIDO
                                        .getValue()));
        pagadoFueAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_PAGADO_FIN
                                        .getValue()));
        porcentajePagadoFueAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.PORCENTAJEPAGADO
                                        .getValue()));
        obligacionFueAnterior = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.VALOR_OBLIGACIONES_FIN
                                        .getValue()));
        porcentajeObligacionFueAnt = String.valueOf(reg.getCampos()
                        .get(PlanDeMetasControladorEnum.PORCENTAJEOBLIGACIONES
                                        .getValue()));
        cargarListaFuenteE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que valida si permite registrar, editar un nivel en plan
     * de accion
     * 
     * @return false
     */
    public boolean validarNivel() {
        if (idPlanIndicativo.length() != digitosMetaProducto) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4182"));
            return true;
        }
        return false;
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
        if (esUsuarioConsulta) {
            permiteCrudSubFuentes = false;
        }
        else {
            permiteCrudSubFuentes = true;
        }
        validarCamposFuentes();
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

    // <SET_GET_ATRIBUTOS>
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
     * Retorna el objeto indicePlanindicativofuentes
     * 
     * @return indicePlanindicativofuentes
     */
    public int getIndicePlanindicativofuentes() {
        return indicePlanindicativofuentes;
    }

    /**
     * Asigna el objeto indicePlanindicativofuentes
     * 
     * @param indicePlanindicativofuentes
     * Variable a asignar en indicePlanindicativofuentes
     */
    public void setIndicePlanindicativofuentes(
        int indicePlanindicativofuentes) {
        this.indicePlanindicativofuentes = indicePlanindicativofuentes;
    }

    /**
     * Retorna el objeto indicePlanindicativofuentes
     * 
     * @return indicePlanindicativofuentes
     */
    public boolean isPermiteCrudSubFuentes() {
        return permiteCrudSubFuentes;
    }

    /**
     * Asigna el objeto indicePlanindicativofuentes
     * 
     * @param indicePlanindicativofuentes
     * Variable a asignar en indicePlanindicativofuentes
     */
    public void setPermiteCrudSubFuentes(boolean permiteCrudSubFuentes) {
        this.permiteCrudSubFuentes = permiteCrudSubFuentes;
    }

    /**
     * Retorna el objeto bloquearCampos
     * 
     * @return bloquearCampos
     */
    public boolean isBloquearCampos() {
        return bloquearCampos;
    }

    /**
     * Asigna el objeto bloquearCampos
     * 
     * @param bloquearCampos
     * Variable a asignar en bloquearCampos
     */
    public void setBloquearCampos(boolean bloquearCampos) {
        this.bloquearCampos = bloquearCampos;
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

    /**
     * Retorna la lista listaVigenciaMetaFuente
     * 
     * @return listaVigenciaMetaFuente
     */
    public List<Registro> getListaVigenciaMetaFuente() {
        return listaVigenciaMetaFuente;
    }

    /**
     * Asigna la lista listaVigenciaMetaFuente
     * 
     * @param listaVigenciaMetaFuente
     * Variable a asignar en listaVigenciaMetaFuente
     */
    public void setListaVigenciaMetaFuente(
        List<Registro> listaVigenciaMetaFuente) {
        this.listaVigenciaMetaFuente = listaVigenciaMetaFuente;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaPlanindicativometas
     * 
     * @return listaPlanindicativometas
     */
    public RegistroDataModelImpl getListaPlanindicativometas() {
        return listaPlanindicativometas;
    }

    /**
     * Asigna la lista listaPlanindicativometas
     * 
     * @param listaPlanindicativometas
     * Variable a asignar en listaPlanindicativometas
     */
    public void setListaPlanindicativometas(
        RegistroDataModelImpl listaPlanindicativometas) {
        this.listaPlanindicativometas = listaPlanindicativometas;
    }

    /**
     * Retorna la lista listaPlanindicativofuentes
     * 
     * @return listaPlanindicativofuentes
     */
    public RegistroDataModelImpl getListaPlanindicativofuentes() {
        return listaPlanindicativofuentes;
    }

    /**
     * Asigna la lista listaPlanindicativofuentes
     * 
     * @param listaPlanindicativofuentes
     * Variable a asignar en listaPlanindicativofuentes
     */
    public void setListaPlanindicativofuentes(
        RegistroDataModelImpl listaPlanindicativofuentes) {
        this.listaPlanindicativofuentes = listaPlanindicativofuentes;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubPlanIndicativoMetas
     * 
     * @return registroSubPlanIndicativoMetas
     */
    public Registro getRegistroSubPlanIndicativoMetas() {
        return registroSubPlanIndicativoMetas;
    }

    /**
     * Asigna el objeto registroSubPlanIndicativoMetas
     * 
     * @param registroSubPlanIndicativoMetas
     * Variable a asignar en registroSubPlanIndicativoMetas
     */
    public void setRegistroSubPlanIndicativoMetas(
        Registro registroSubPlanIndicativoMetas) {
        this.registroSubPlanIndicativoMetas = registroSubPlanIndicativoMetas;
    }

    /**
     * Retorna el objeto registroSubPlanIndicativoFuentes
     * 
     * @return registroSubPlanIndicativoFuentes
     */
    public Registro getRegistroSubPlanIndicativoFuentes() {
        return registroSubPlanIndicativoFuentes;
    }

    /**
     * Asigna el objeto registroSubPlanIndicativoFuentes
     * 
     * @param registroSubPlanIndicativoFuentes
     * Variable a asignar en registroSubPlanIndicativoFuentes
     */
    public void setRegistroSubPlanIndicativoFuentes(
        Registro registroSubPlanIndicativoFuentes) {
        this.registroSubPlanIndicativoFuentes = registroSubPlanIndicativoFuentes;
    }

    // </SET_GET_ADICIONALES>
}
