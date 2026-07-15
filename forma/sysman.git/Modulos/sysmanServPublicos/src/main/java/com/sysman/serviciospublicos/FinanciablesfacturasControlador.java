/*-
 * FinanciablesControlador.Java
 *
 * 1.0
 *
 * 19 de sept. de 2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyac�.
 * All rights reserved.
 */

package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSieteRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FinanciablesfacturasControladorEnum;
import com.sysman.serviciospublicos.enums.FinanciablesfacturasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase que contiene la migracion de la pestana Financiables del
 * formulario Factura. Contiene dos subformulario: Financiables en
 * Access SubFinanciablesConsulta y Financiables Pendientes en Access
 * SubFinanciablesOtros.
 *
 * @author acaceres
 * @version 1, 08/09/2016
 *
 * @author ybecerra
 * @version 2, 23/05/2017, proceso de Refactoring y revision sonar
 *
 * -- Modificado por lcortes 20/06/2017 10:50. Se modifica el llamado
 * al metodo redireccionar por redireccionarDeModalAModal en los
 * metodos oprimircmdDetalle y oprimirCmdTotal.
 */

@ManagedBean
@ViewScoped

public class FinanciablesfacturasControlador extends BeanBaseDatosAcmeImpl
{

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para hacer referencia al numero de modulo
     * ingresado en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Almacenara el nombre del concepto seleccionado en el
     * subformulario Financiables.
     */
    private String nombreConcepto;

    /**
     * Almacenara el nombre del concepto seleccionado en el
     * subformulario Financiables pendientes.
     */
    private String nombreConceptoO;

    /**
     * Almacenara el ciclo que se trae por parametro desde el
     * formulario Factura.
     */
    private String ciclo = "";

    /**
     * Almacenara el codigo de la ruta que se trae por parametro desde
     * el formulario Factura.
     */
    private String codigoRuta = "";

    /**
     * Almacenara el ano que se trae por parametro desde el formulario
     * Factura.
     */
    private String ano = "";

    /**
     * Almacenara el periodo que se trae por parametro desde el
     * formulario Factura.
     */
    private String periodo = "";
    /**
     * Variable auxiliar
     */
    private Date fechaAux;

    /**
     * Alamcenara el codigo interno que se trae por parametro desde el
     * formulario Factura
     */
    private String codigoInterno = "";

    /**
     * Almacenara el concepto que se trae desde el combo concepto.
     */
    private String concepto = "";

    /**
     * Permite hacer visible el boton cmdDetalle en el subformulario
     * Financiables.
     */
    private boolean cmdDetalleVisible = false;

    /**
     * Permite hacer visible el boton cmdTotal en el subformulario
     * Financiables.
     */
    private boolean cmdTotalVisible = false;

    /**
     * Variable que permite hacer visible el campo codigo almacen en
     * el subformulario Financiables
     */
    private boolean codigoAlmacenVisible = false;

    /**
     * Permite bloquear el campo codigo almacen en el subformulario
     * Financiables.
     */
    private boolean codigoAlmacenbloquedo = false;

    /**
     * Muestra el dialogo de verificacion cuando se ejecuta el evento
     * del ckeck bloqueado
     */
    private boolean dialogoUBloqVisible = false;

    /**
     * Muestra el dialogo verificacion cuando se va a agregar un nuevo
     * registro
     */
    private boolean dialogoAFinVisible = false;

    /**
     * Almacena el valor que se ingresa en el campo NumeroCuotas del
     * subformulario Financiables
     */
    private int numeroCuotas = 0;

    /**
     * Almacena el valor que se ingresa en el campo NroCuota del
     * subformulario Financiables
     */
    private int nroCuotas = 0;

    /**
     * Almacena el valor que se ingresa o se calcula en el campo
     * SaldoFinanciable del subformulario Financiables
     */
    private double saldoFinanciable;

    /**
     * Almacena el valor que se calcula en el campo ValorCuota en el
     * subformulario Financiables
     */
    private double valorCuota;

    /**
     * Almacena el codigo ruta Final que se enviara al formulario
     * factura
     */
    private String codRutaFin;

    /**
     * Almacena el numero del ciclo final que se enviara al formulario
     * Factura
     */
    private String cicloFin;

    /**
     * Numero de la factura que se trae desde el formulario Factura.
     * Numero usado para hacer validaciones al cambiar el check
     * bloqueado
     */
    private String numeroFactura = "1";

    /**
     * Almacenara el valor los periodos de no cobro para realizar
     * validaciones. Se trae como parametro del formulario Factura de
     * la tabla SP_USUARIO
     */
    private String periodoNoCobroFin = "1";

    /**
     * Se debe traer del formulario factura. // FACTURA!txtFimm
     * Almacenara los planos son financiables
     */
    private String txtFimm = "NULL";

    /**
     * Se trae desde el formulario Factura de la tabla SP_USUARIOS
     * Indica la lectura que trae el codigo de ruta seleccionado
     *
     */
    private String lectura = "0";
    private String mensajeCalculo;

    /**
     * Almacenara el codigo del banco que se trae del formulario
     * FacturaIntegrado
     */
    String bancoPerProceso;

    /**
     * Almacenara los periodos que no se han cobrado, que se traen del
     * formulario Factura de la tabla SP_USUARIO
     */
    String periodosNoCobradosFac = "";
    /**
     * Atributo que valida si la edicion del registro del
     * subFormulario Financiables, se edita despues del aceptar de un
     * dialogo o no
     */
    boolean noDialogo = true;

    /**
     * Atributo definido para almancenar el valor recibido de la
     * funcion prepararAnoPeriodoSiguiente
     */
    String anoSig;
    /**
     * Atributo definido para almacenar el valor recibido de la
     * funcion prepararAnoPeriodoSiguiente
     */
    String perSig;
    /**
     * Atributo que valida accion del llamado al metodo
     * insertarAuditoriaGeneral
     */
    boolean validarAux;

    /**
     * Atributo que valida si despues de cumplir una condicion se debe
     * visulizar un dialogo o no
     */
    boolean auxBancoPer = false;
    boolean dialogo = false;
    boolean aceptarDialogo = false;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>

    /** Lista que se carga en el combo bloqueadoHastaAno */
    private List<Registro> listabloqueadoHastaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    /** Lista que se carga en el combo concepto */
    private RegistroDataModelImpl listaConcepto;

    /**
     * Lista que se carga en el combo concepto en la grilla del
     * subformulario Financiables
     */
    private RegistroDataModelImpl listaConceptoE;

    /** Lista que se carga en el combo bloqueadoHastaPeriodo */
    private RegistroDataModelImpl listabloqueadoHastaPeriodo;

    /**
     * Lista que se carga en el combo bloqueadoHastaPeriodo en la
     * grilla del subformulario Financiables
     */
    private RegistroDataModelImpl listabloqueadoHastaPeriodoE;

    /** Lista que se carga en el combo periodo */
    private RegistroDataModelImpl listaperiodo;

    /**
     * Lista que se carga en el combo periodo de la grilla del
     * subformulario Financiables
     */
    private RegistroDataModelImpl listaperiodoE;

    /**
     * Lista que se carga en el combo concepto del subformulario
     * Financiables Pendientes
     */
    private RegistroDataModelImpl listaConceptoO;

    /**
     * Lista que se carga en el combo concepto, en la grilla el
     * subformulario Financiables pendientes
     */
    private RegistroDataModelImpl listaConceptoOE;
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>

    /**
     * Lista que se carga en el subformulario Financiables consulta
     */
    private List<Registro> listaSubfinanciablesconsulta;

    /** Lista que se carga en el subformulario Financiables Otros */
    private List<Registro> listaSubfinanciablesotros;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>

    private Registro registroSubSubFinanciablesConsulta;
    private Registro registroSubSubFinanciablesOtros;
    /**
     * auxiliar creado para tomar el registro seleccionado en el
     * subFormulario Financiable, con el fin de llevarlo a una
     * validacion en el aceptar de un dialogo
     */
    private Registro registroAuxiliarFin;

    /**
     * Variable creada para guardar el registro de la grilla antes de
     * ser editado el registro seleccionado en el subformulario
     * SubInfoOperacion
     */
    private Registro regFinanciable;

    /**
     * Atributo que calida si se hace llamado o no a la funcion
     * calcular facturacion
     */
    private boolean calcula;
    /**
     * Si el atributo es valor true, realiza la accion insercion o
     * edicion, si se le da clic al boton aceptar del dialogo
     * dialogoUnoBloqueado
     */
    private boolean auxBloqueo = false;
    /**
     * Atributo que valida si el dialogo financiableaplicaperiodo se
     * hace visible o no
     */
    private boolean aplicaPeriodo;

    /**
     * Atributo que almacena el valor retornado de la funcion
     * validarFinanciable
     */
    private boolean validarFinanciable;

    /**
     * Atributo que contiene la fecha de creacion del financiable para
     * enviar por parametro a la funcion validarFinanciable, esta
     * fecha se cambia dependiendo de la accion a realizar en el
     * subFormulario Financiables
     */
    private Date fecha;
    /**
     * Variable que permite almacenar el valor del indice de la fila
     * del subformulario Financiable que va a ser editada.
     */
    private int indiceSubfinanciablesconsulta;

    Registro registroDialogo;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;
    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;
    @EJB
    private EjbServiciosPublicosSieteRemote ejbServPublicosSiete;
    @EJB
    private EjbServiciosPublicosOchoRemote ejbServPublicosOcho;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FinanciablesfacturasControlador
     */
    public FinanciablesfacturasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FINANCIABLESFACTURAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {

                /* Parametros que se traen del formulario Factura */
                ciclo = (String) parametrosEntrada
                                .get(GeneralParameterEnum.CICLO.getName()
                                                .toLowerCase());
                codigoRuta = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM11
                                                .getValue());
                ano = parametrosEntrada.get("ano").toString();
                periodo = (String) parametrosEntrada
                                .get(GeneralParameterEnum.PERIODO.getName()
                                                .toLowerCase());
                codigoInterno = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM12
                                                .getValue());
                bancoPerProceso = (String) parametrosEntrada
                                .get("bancoperproceso");
                txtFimm = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM13
                                                .getValue());
                numeroFactura = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM14
                                                .getValue());
                lectura = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM15
                                                .getValue());
                periodoNoCobroFin = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM16
                                                .getValue());

                periodosNoCobradosFac = (String) parametrosEntrada
                                .get(FinanciablesfacturasControladorEnum.PARAM17
                                                .getValue());
            }

            // <INI_ADICIONAL>
            registroSubSubFinanciablesConsulta = new Registro(
                            new HashMap<String, Object>());
            registroSubSubFinanciablesOtros = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FinanciablesfacturasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubfinanciablesconsulta();
        cargarListaSubfinanciablesotros();
        // </CARGAR_LISTAS_SUBFORM>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubfinanciablesconsulta = null;
        listaSubfinanciablesotros = null;
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
        tabla = "SP_USUARIO";
        buscarLlave();

        /*
         * Inicializacion los valores por defecto de los campos del
         * formulario
         */

        registroSubSubFinanciablesConsulta.getCampos()
                        .put(GeneralParameterEnum.DATE_CREATED.getName(),
                                        new Date());
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.PARAM1.getValue(),
                        0);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                        .getValue(),
                                        0);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                        .getValue(),
                        0);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.PARAM2.getValue(),
                        0);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                        .getValue(), 0);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(GeneralParameterEnum.PERIODO.getName(), periodo);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.PARAM6
                                        .getValue(),
                                        ano);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.PARAM9
                                        .getValue(), periodo);
        iniciarListasSub();

        /*
         * Cargar listas de los subformularios
         */
        cargarListaConcepto();
        cargarListaConceptoE();
        cargarListabloqueadoHastaPeriodo();
        cargarListabloqueadoHastaPeriodoE();
        cargarListaperiodo();
        cargarListaperiodoE();
        cargarListaConceptoO();
        cargarListaConceptoOE();
        cargarListabloqueadoHastaAno();
        asignarOrigenDatos();
        reasignarOrigenGrilla();
        abrirFormulario();

    }

    /**
     * Metodo que carga la lista del subformulario Financiables
     * Consulta (Financiables)
     */
    public void cargarListaSubfinanciablesconsulta()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            listaSubfinanciablesconsulta = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesfacturasControladorUrlEnum.URL16211
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.SP_FINANCIABLES
                                                            .getTable()));
        }
        catch (SystemException | SysmanException e)
        {
            Logger.getLogger(FinanciablesfacturasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que carga la lista del subformulario Financiable otros
     * (Financiables Pendientes)
     */
    public void cargarListaSubfinanciablesotros()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            listaSubfinanciablesotros = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesfacturasControladorUrlEnum.URL19155
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.SP_FINANCIABLES
                                                            .getTable()));

        }
        catch (SystemException | SysmanException e)
        {
            Logger.getLogger(FinanciablesfacturasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * Metodo que carga la lista del combo concepto del subformulario
     * Financiables
     */
    public void cargarListaConcepto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL18854
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo que carga la lista del combo Concepto en la grilla del
     * subformulario Financiables
     */
    public void cargarListaConceptoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL18854
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * Metodo que carga la lista del combo bloqueadoHastaPeriodo en el
     * subformulario Financiables
     */
    public void cargarListabloqueadoHastaPeriodo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL22729
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(FinanciablesfacturasControladorEnum.PARAM0.getValue(),
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString());

        param.put(GeneralParameterEnum.PERIODO.getName(),
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(GeneralParameterEnum.PERIODO
                                                        .getName())
                                        .toString());

        listabloqueadoHastaPeriodo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.MES.getName());

    }

    /**
     * Metodo que carga la lista del combo bloqueadoHastaPeriodo en la
     * grilla del subformulario Financiable
     */
    public void cargarListabloqueadoHastaPeriodoE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL22729
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(FinanciablesfacturasControladorEnum.PARAM0.getValue(),
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString());

        param.put(GeneralParameterEnum.PERIODO.getName(),
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(GeneralParameterEnum.PERIODO
                                                        .getName())
                                        .toString());

        listabloqueadoHastaPeriodoE = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.MES.getName());
    }

    /**
     * Metodo que carga la lista del combo bloqueadoHastaAno del
     * subformulario Financiables
     */
    public void cargarListabloqueadoHastaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listabloqueadoHastaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FinanciablesfacturasControladorUrlEnum.URL22880
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Meotodo que carga la lista del combo periodo en el
     * subformulario Financiables
     */
    public void cargarListaperiodo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL26506
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString());

        listaperiodo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.MES.getName());
    }

    /**
     * Metodo que carga la lista del combo periodo en la grilla del
     * subformulario Financiables
     */
    public void cargarListaperiodoE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL26506
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString());

        listaperiodoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.MES.getName());
    }

    /**
     * Metodo que carga la lista del combo concepto en el
     * subformulario Financiables Pendientes.
     */
    public void cargarListaConceptoO()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL28694
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConceptoO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * Metodo que carga la lista del combo concepto en la grilla del
     * subformulario Financiables Pendientes.
     */
    public void cargarListaConceptoOE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FinanciablesfacturasControladorUrlEnum.URL28694
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConceptoOE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo que se ejecuta cuando se cambia el combo concepto en la
     * grilla del subformulario Financiables
     *
     * @param rowNum
     * numero de la fila que se tiene seleccionada
     */
    public void cambiarConceptoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaSubfinanciablesconsulta.get(rowNum).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        nombreConcepto);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el combo concepto en la
     * grilla del subformulario Financiables Pendientes
     *
     * @param rowNum
     * numero de la fila que se tiene seleccionada
     */
    public void cambiarConceptoOC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        listaSubfinanciablesotros.get(rowNum).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        nombreConceptoO);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el combo bloqueado hasta
     * ano en la grilla del subformulario Financiables
     *
     * @param rowNum
     * numero de la fila que se tiene seleccionada
     */
    public void cambiarbloqueadoHastaAnoC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        String bloqueHastaAno = listaSubfinanciablesconsulta.get(rowNum)
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                        .getValue())
                        .toString();

        if ((Integer.parseInt(bloqueHastaAno) < Integer.parseInt(ano))
            || SysmanFunciones.validarVariableVacio(bloqueHastaAno))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1600"));
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el combo bloqueado hasta
     * periodo en la grilla del subformulario Financiables
     *
     * @param rowNum
     * numero de la fila que se tiene seleccionada en la grilla
     */
    public void cambiarbloqueadoHastaPeriodoC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        String bloqueaHastaAno = listaSubfinanciablesconsulta.get(rowNum)
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                        .getValue())
                        .toString();
        int bloqueaHastaPeri = Integer
                        .parseInt(listaSubfinanciablesconsulta.get(rowNum)
                                        .getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM9
                                                        .getValue())
                                        .toString());

        if ((bloqueaHastaAno.equals(ano)
            && (bloqueaHastaPeri < Integer.valueOf(periodo)))
            || ("").equals(bloqueaHastaAno))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1601"));
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del campo
     * monto a Financiar en la grilla del subformulario Financiables.
     *
     * @param rowNum
     * Numero de la fila que se tiene seleccionada en la grilla
     */
    public void cambiarMontoFinanciarC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>

        /*
         * Almacena el valor que se ingresa o se calcula en el campo
         * MontoFinanciar del subformulario Financiables
         */

        double montoFinanciar;
        int nroCuota = Integer.parseInt(listaSubfinanciablesconsulta.get(rowNum)
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM2
                                        .getValue())
                        .toString());
        montoFinanciar = Double.parseDouble(
                        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM1
                                                        .getValue())
                                        .toString());
        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                        .put(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                        .getValue(), montoFinanciar);
        int numeroCuota = Integer.parseInt(
                        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                                        .getValue())
                                        .toString());
        if (nroCuota > 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1602"));
            return;
        }

        int ceroVal = 0;
        if ((int) montoFinanciar != ceroVal)
        {
            listaSubfinanciablesconsulta.get(rowNum).getCampos()
                            .put(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                            .getValue(), montoFinanciar);
        }

        if (numeroCuota > 0)
        {
            valorCuota = montoFinanciar / numeroCuota;
            listaSubfinanciablesconsulta.get(rowNum).getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(), valorCuota);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del campo
     * monto a Financiar en la grilla del subformulario Financiables
     * Pendientes.
     *
     * @param rowNum
     * Numero de la fila que se tiene seleccionada en la grilla
     */
    public void cambiarMontoFinanciarOC(int rowNum)
    {
        double montoFinanciar;

        montoFinanciar = Double.parseDouble(
                        listaSubfinanciablesotros.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM1
                                                        .getValue())
                                        .toString());
        listaSubfinanciablesotros.get(rowNum).getCampos()
                        .put(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                        .getValue(), montoFinanciar);
        int numeroCuota = Integer.parseInt(
                        listaSubfinanciablesotros.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                                        .getValue())
                                        .toString());

        int ceroVal = 0;
        if ((int) montoFinanciar != ceroVal)
        {
            listaSubfinanciablesotros.get(rowNum).getCampos()
                            .put(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                            .getValue(), montoFinanciar);
        }

        if (numeroCuota > 0)
        {
            valorCuota = montoFinanciar / numeroCuota;
            listaSubfinanciablesotros.get(rowNum).getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(), valorCuota);
        }
    }

    /**
     * Metodo que se ejecuta cuando se cambia el valor del campo
     * numero de cuotas en la grilla del subformulario Financiables
     *
     * @param rowNum
     * numero de la fila que se tiene seleccionada en la grilla
     */
    public void cambiarNumeroCuotasC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        numeroCuotas = Integer.parseInt(
                        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                                        .getValue())
                                        .toString());
        saldoFinanciable = Double.parseDouble(
                        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                                        .getValue())
                                        .toString());
        valorCuota = saldoFinanciable / numeroCuotas;
        if (numeroCuotas > 0)
        {
            listaSubfinanciablesconsulta.get(rowNum).getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(), valorCuota);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el valor del campo nro
     * cuota en la grilla del subformulario Financiables
     *
     * @param rowNum
     * numero de la fila que se tienen seleccionada en la grilla
     */
    public void cambiarNroCuotaC(int rowNum)
    {
        // <CODIGO_DESARROLLADO>
        int nroCuota = Integer.parseInt(listaSubfinanciablesconsulta.get(rowNum)
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM2
                                        .getValue())
                        .toString());
        if (nroCuota > 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1603"));
            return;
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del campo
     * NumeroCuotas en la grilla del subformulario Financiables
     * Pendientes
     *
     * @param rowNum
     * numero de linea seleccionada en la grilla
     */
    public void cambiarNumeroCuotasOC(int rowNum)
    {

        // <CODIGO_DESARROLLADO>
        numeroCuotas = Integer.parseInt(
                        listaSubfinanciablesotros.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                                        .getValue())
                                        .toString());
        saldoFinanciable = Double.parseDouble(
                        listaSubfinanciablesotros.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                                        .getValue())
                                        .toString());
        valorCuota = saldoFinanciable / numeroCuotas;
        if (numeroCuotas > 0)
        {
            listaSubfinanciablesotros.get(rowNum).getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(), valorCuota);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el valor del campo
     * MontoFinanciar en el subformulario Financiables
     */
    public void cambiarMontoFinanciar()
    {
        // <CODIGO_DESARROLLADO>
        double montoFinanciar;
        montoFinanciar = Double.parseDouble(registroSubSubFinanciablesConsulta
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM1
                                        .getValue())
                        .toString());
        numeroCuotas = Integer.valueOf(registroSubSubFinanciablesConsulta
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                        .getValue())
                        .toString());

        nroCuotas = Integer.parseInt(registroSubSubFinanciablesConsulta
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM2
                                        .getValue())
                        .toString());

        if (nroCuotas > 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1602"));
            return;
        }

        int ceroVal = 0;
        if ((int) montoFinanciar != ceroVal)
        {
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                            .getValue(), montoFinanciar);
        }

        if (numeroCuotas > 0)
        {
            saldoFinanciable = Double
                            .parseDouble(registroSubSubFinanciablesConsulta
                                            .getCampos()
                                            .get(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                                            .getValue())
                                            .toString());
            valorCuota = saldoFinanciable / numeroCuotas;
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(),
                                            valorCuota);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el valor del campo
     * NumeroCuotas en el subformulario Financiables
     */
    public void cambiarNumeroCuotas()
    {
        // <CODIGO_DESARROLLADO>
        numeroCuotas = Integer.parseInt(registroSubSubFinanciablesConsulta
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                        .getValue())
                        .toString());

        if (numeroCuotas > 0)
        {
            saldoFinanciable = Double
                            .parseDouble(registroSubSubFinanciablesConsulta
                                            .getCampos()
                                            .get(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                                            .getValue())
                                            .toString());
            valorCuota = saldoFinanciable / numeroCuotas;
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(),
                                            valorCuota);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control SaldoFinanciable
     *
     *
     */
    public void cambiarSaldoFinanciable()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorCuota
     *
     *
     */
    public void cambiarValorCuota()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del campo
     * nroCuotas en el subformulario Financiables
     */
    public void cambiarNroCuota()
    {
        // <CODIGO_DESARROLLADO>
        nroCuotas = Integer.parseInt(registroSubSubFinanciablesConsulta
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM2
                                        .getValue())
                        .toString());
        if (nroCuotas > 0)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1603"));
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del campo
     * bloqueadoHastaAno en el subformulario Financiables
     */
    public void cambiarbloqueadoHastaAno()
    {
        // <CODIGO_DESARROLLADO>
        int bloqueadoHastaAno = Integer
                        .parseInt(registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString());

        String bloHAno = String.valueOf(bloqueadoHastaAno);
        if ((bloqueadoHastaAno < Integer.valueOf(ano))
            || SysmanFunciones.validarVariableVacio(bloHAno))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1604"));
            return;
        }

        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.PARAM9
                                        .getValue(), "");
        cargarListabloqueadoHastaPeriodo();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se cambia el valor del check
     * bloqueado en el subformulario financiables
     */
    public void cambiarBLOQUEADO()
    {
        // <CODIGO_DESARROLLADO>

        if (!SysmanFunciones.validarVariableVacio(bancoPerProceso))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1606"));
            return;
        }

        if (SysmanFunciones.validarVariableVacio(
                        registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString())
            || SysmanFunciones.validarVariableVacio(
                            registroSubSubFinanciablesConsulta.getCampos()
                                            .get(FinanciablesfacturasControladorEnum.PARAM9
                                                            .getValue())
                                            .toString()))
        {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1607"));
            return;
        }

        if (!"0".equals(periodoNoCobroFin))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1608"));
            return;
        }

        if (!(boolean) registroSubSubFinanciablesConsulta.getCampos().get(
                        FinanciablesfacturasControladorEnum.BLOQUEADO
                                        .getValue())
            && !"0".equals(numeroFactura))
        {
            registroAuxiliarFin = (Registro) registroSubSubFinanciablesConsulta
                            .getCampos();
            fechaAux = (Date) registroSubSubFinanciablesConsulta.getCampos()
                            .get(
                                            GeneralParameterEnum.DATE_CREATED
                                                            .getName());

            auxBloqueo = true;
            dialogoUBloqVisible = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarBLOQUEADOC(int rowNum)
    {

        fechaAux = (Date) listaSubfinanciablesconsulta.get(rowNum).getCampos()
                        .get(GeneralParameterEnum.DATE_CREATED.getName());

        dialogo = true;
        registroDialogo.getCampos().put("BLOQUEADO",
                        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                        .get(
                                                        FinanciablesfacturasControladorEnum.BLOQUEADO
                                                                        .getValue()));

        if (!SysmanFunciones.validarVariableVacio(bancoPerProceso))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1606"));
            return;
        }

        if (SysmanFunciones.validarVariableVacio(
                        listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString())
            || SysmanFunciones.validarVariableVacio(
                            listaSubfinanciablesconsulta.get(rowNum).getCampos()
                                            .get(FinanciablesfacturasControladorEnum.PARAM9
                                                            .getValue())
                                            .toString()))
        {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1607"));
            return;
        }

        if (!"0".equals(periodoNoCobroFin))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1608"));
            return;
        }

        if (!(boolean) listaSubfinanciablesconsulta.get(rowNum).getCampos().get(
                        FinanciablesfacturasControladorEnum.BLOQUEADO
                                        .getValue())
            && !"0".equals(numeroFactura))
        {
            auxBloqueo = true;

            dialogoUBloqVisible = true;
        }
    }

    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    /**
     * Metodo que se ejecuta cuando se cambia el valor del combo
     * concepto en el subformulario Financiables
     *
     * @param event
     */
    public void seleccionarFilaConcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.CODIGO
                                                                            .getName())
                                                            .toString());
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.NOMBRE
                                                                            .getName())
                                                            .toString());
        concepto = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        if (("12").equals(concepto))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB3199"));
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(GeneralParameterEnum.CONCEPTO.getName(), null);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            GeneralParameterEnum.NOMBRE.getName(),
                            null);
            return;

        }

    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del combo
     * concepto en la grilla del subformulario Financiables
     *
     * @param event
     */
    public void seleccionarFilaConceptoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        nombreConcepto = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
    }

    /**
     * Metodo que se ejecuta cuando se cambia el valor del campo
     * bloqueadoHastaPeriodo en el subformulario Financiables
     *
     * @param event
     */
    public void seleccionarFilabloqueadoHastaPeriodo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.PARAM9
                                        .getValue(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.MES
                                                        .getName()) == null ? ""
                                                            : registroAux.getCampos()
                                                                            .get(
                                                                                            GeneralParameterEnum.MES
                                                                                                            .getName())
                                                                            .toString());

        int bloqueadoHastaAno = Integer
                        .parseInt(registroSubSubFinanciablesConsulta.getCampos()
                                        .get(FinanciablesfacturasControladorEnum.PARAM6
                                                        .getValue())
                                        .toString());
        String bloqueaHastaPeriodo = registroSubSubFinanciablesConsulta
                        .getCampos()
                        .get(FinanciablesfacturasControladorEnum.PARAM9
                                        .getValue())
                        .toString();

        if ((bloqueadoHastaAno == Integer.parseInt(ano)) &&
            ((Integer.parseInt(bloqueaHastaPeriodo) < Integer
                            .parseInt(periodo))
                || ("").equals(bloqueaHastaPeriodo)))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1610"));
            return;
        }

    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del combo
     * bloqueado hasta periodo en la grilla del subformulario
     * Financiables
     *
     * @param event
     */
    public void seleccionarFilabloqueadoHastaPeriodoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.MES.getName()) == null
                            ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.MES
                                                            .getName())
                                            .toString();
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del combo
     * periodo en el subformulario Financiables
     *
     * @param event
     */
    public void seleccionarFilaperiodo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.PERIODO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.MES
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.MES
                                                                            .getName())
                                                            .toString());
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del combo
     * periodo en el subformulario Financiables
     *
     * @param event
     */
    public void seleccionarFilaperiodoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.MES.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.MES
                                                            .getName())
                                            .toString();
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del combo
     * concepto en el subformulario Financiables Pendientes.
     *
     * @param event
     */
    public void seleccionarFilaConceptoO(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubFinanciablesOtros.getCampos().put(
                        GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO
                                        .getName()) == null ? ""
                                            : registroAux.getCampos()
                                                            .get(GeneralParameterEnum.CODIGO
                                                                            .getName())
                                                            .toString());
        nombreConceptoO = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();

        if (ACCION_MODIFICAR.equals(accion))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1611"));
            return;

        }
    }

    /**
     * Metodo que se ejecuta cuando se modifica el valor del combo
     * concepto en la grilla del subformulario Financiables Pendientes
     *
     * @param event
     */
    public void seleccionarFilaConceptoOE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString();
        nombreConceptoO = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()) == null ? ""
                            : registroAux.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName())
                                            .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Metodo que se ejecuta cuando se genera el evento del boton
     * cmdDetalle en la grilla, redirecciona al formulario
     * subFinanciablesdeuda.
     *
     * @param indice
     */
    public void oprimircmdDetalle(Registro reg, int rowNun)
    {
        Direccionador direccionador = new Direccionador();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.CICLO.getName().toLowerCase(),
                        ciclo);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM11.getValue(),
                        codigoRuta);
        parametros.put("ano", reg.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        reg.getCampos().get(
                                        GeneralParameterEnum.PERIODO.getName())
                                        .toString());
        parametros.put(FinanciablesfacturasControladorEnum.PARAM12.getValue(),
                        codigoInterno);
        parametros.put("bancoPerProceso", bancoPerProceso);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM17.getValue(),
                        periodosNoCobradosFac);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM13.getValue(),
                        txtFimm);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM14.getValue(),
                        numeroFactura);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM15.getValue(),
                        lectura);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM16.getValue(),
                        periodoNoCobroFin);
        parametros.put("marcaDetalleFact", "1");
        parametros.put("financiable", "1");
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBFINANCIABLESDEUDA_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejeucta cuando se genera el evento del boton
     * cmdTotal en la grilla del Subformulario Financiables,
     * redirecciona al formulario subFinanciablesDeuda
     *
     */
    public void oprimirCmdTotal(Registro reg, int rowNun)
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.CICLO.getName().toLowerCase(),
                        ciclo);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM11.getValue(),
                        codigoRuta);
        parametros.put("ano", reg.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()).toString());
        parametros.put(GeneralParameterEnum.PERIODO.getName().toLowerCase(),
                        reg.getCampos().get(
                                        GeneralParameterEnum.PERIODO.getName())
                                        .toString());
        parametros.put(FinanciablesfacturasControladorEnum.PARAM12.getValue(),
                        codigoInterno);
        parametros.put("bancoPerProceso", bancoPerProceso);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM17.getValue(),
                        periodosNoCobradosFac);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM13.getValue(),
                        txtFimm);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM14.getValue(),
                        numeroFactura);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM15.getValue(),
                        lectura);
        parametros.put(FinanciablesfacturasControladorEnum.PARAM16.getValue(),
                        periodoNoCobroFin);
        parametros.put("marcaDetalleFact", "2");
        parametros.put("financiable", "1");
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBFINANCIABLESDEUDA_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarDeModalAModal(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirsalir()
    {

        RequestContext.getCurrentInstance()
                        .closeDialog(ciclo);
    }
    // </METODOS_BOTONES>

    // <METODOS_SUBFORM>
    /**
     * Metodo que se ejecuta cuando se agrega un registro al
     * subformulario financiables
     */
    public void agregarRegistroSubSubfinanciablesconsulta()
    {
        try
        {

            validarAux = true;
            fecha = new Date();
            Registro regAux = registroRemoverC();
            registroAuxiliarFin = regAux;

            // Se ingresan el valor de los campos obligatorios en el
            // subformulario Financiables

            if (!auxBloqueo)
            {
                if ("P".equals(txtFimm))
                {
                    // Se crea dialogo para preguntar si desea agregar
                    // el
                    // financiable al siguiente periodo.
                    dialogoAFinVisible = true; // aceptarDialogAgregarFinanciable
                    aplicaPeriodo = false;

                }
                else
                {

                    validarPeriodosNoCobro();

                    validarFinanciable = ejbServPublicosOcho.validarFinanciable(
                                    compania,
                                    Integer.valueOf(ciclo),
                                    Integer.valueOf(ano),
                                    periodo,
                                    true, txtFimm, Long.parseLong(lectura),
                                    bancoPerProceso,
                                    periodosNoCobradosFac, fecha);

                    ejecutarAccion(validarFinanciable);
                }
            }
            cargarListaSubfinanciablesotros();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally
        {
            registroSubSubFinanciablesConsulta = new Registro(
                            new HashMap<String, Object>());

            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.PARAM1
                                            .getValue(),
                            0);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                            .getValue(),
                            0);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                            .getValue(),
                            0);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM2
                                            .getValue(),
                                            0);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(),
                                            0);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            GeneralParameterEnum.PERIODO.getName(),
                            periodo);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.PARAM6
                                            .getValue(),
                            ano);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM9
                                            .getValue(), periodo);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(GeneralParameterEnum.DATE_CREATED.getName(),
                                            new Date());

        }
    }

    /**
     * Metodo se ejecuta cuando se actualiza un registro en el
     * subformulario Financiables consulta
     *
     * @param event
     */
    public void editarRegSubSubfinanciablesconsulta(RowEditEvent event)
    {
        validarAux = false;
        Registro reg = (Registro) event.getObject();
        registroAuxiliarFin = reg;
        fecha = (Date) reg.getCampos()
                        .get(GeneralParameterEnum.DATE_CREATED.getName());
        try
        {

            if (!auxBloqueo)
            {

                if ("P".equals(txtFimm))
                {
                    // Se crea dialogo para preguntar si desea agregar
                    // el
                    // financiable al siguiente periodo.
                    dialogoAFinVisible = true; // aceptarDialogAgregarFinanciable
                    aplicaPeriodo = false;

                }
                else
                {
                    validarPeriodosNoCobro();

                    validarFinanciable = ejbServPublicosOcho.validarFinanciable(
                                    compania,
                                    Integer.valueOf(ciclo),
                                    Integer.valueOf(ano),
                                    periodo,
                                    true, txtFimm, Long.parseLong(lectura),
                                    bancoPerProceso,
                                    periodosNoCobradosFac, fecha);

                    ejecutarAccion(validarFinanciable);
                }
            }
        }
        catch (SystemException ex)
        {
            Logger.getLogger(FinanciablesfacturasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaSubfinanciablesconsulta();

        }
    }

    /**
     * Metodo que se ejecuta cuando se elimina un registro del
     * subformulario Financiables, realiza las validaciones
     * correspondientes para determinar si el registro se puede
     * eliminar o no
     *
     * @param reg
     * registro del subformulariofinanciablesconsulta
     */
    public void eliminarRegSubSubfinanciablesconsulta(Registro reg)
    {
        try
        {
            registroAuxiliarFin = reg;
            int eliminar = ejbServPublicosOcho.eliminarFacturado(compania,
                            Integer.valueOf(ciclo), codigoRuta,
                            Integer.valueOf(ano), periodo, bancoPerProceso,
                            Integer.valueOf(reg.getCampos()
                                            .get(GeneralParameterEnum.CONCEPTO
                                                            .getName())
                                            .toString()),
                            SessionUtil.getUser().getCodigo(), codigoInterno);

            if (eliminar == 0)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3196"));
                return;
            }
            else if (eliminar == 2)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1644"));
                return;
            }
            else
            {
                if (eliminar == 4)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1714"));
                }
                String mensaje = !bancoPerProceso.isEmpty() ? ""
                    : idioma.getString("TB_TB3197");
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3198")
                    + mensaje);
                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.SP_FINANCIABLES
                                                                .getDeleteKey());
                requestManager.delete(urlDelete.getUrl(), reg.getLlave());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));

                calcularDespues();
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());

        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.PARAM1
                                        .getValue(),
                        0);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                        .getValue(),
                        0);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                        .getValue(),
                        0);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.PARAM2
                                        .getValue(),
                                        0);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                        .getValue(),
                                        0);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.PARAM6
                                        .getValue(),
                        ano);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(FinanciablesfacturasControladorEnum.PARAM9
                                        .getValue(), periodo);
        cargarListaSubfinanciablesconsulta();
    }

    public void cancelarEdicionSubfinanciablesconsulta()
    {
        cargarListaSubfinanciablesconsulta();
        cargarListaSubfinanciablesotros();
    }

    /**
     * Metodo que se ejecuta cuando se va a agregar un registro en el
     * subformulario Financiables Pendientes
     */
    public void agregarRegistroSubSubfinanciablesotros()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se va a actualizar un registro del
     * subformulario Financiables Pendientes
     *
     * @param event
     */
    public void editarRegSubSubfinanciablesotros(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        // <CODIGO_DESARROLLADO>
        try
        {

            reg.getCampos().remove(GeneralParameterEnum.CICLO.getName());
            reg.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
            reg.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
            reg.getCampos().remove(GeneralParameterEnum.CONCEPTO.getName());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(FinanciablesfacturasControladorEnum.VALORCUOTA
                            .getValue(),
                            valorCuota);
            reg.getCampos().remove("NOMBREO");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FinanciablesfacturasControladorUrlEnum.URL1947
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

            cargarListaSubfinanciablesotros();
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta cuando se elimina un registro del
     * subformulario Financiables Pendientes
     *
     * @param reg
     */
    public void eliminarRegSubSubfinanciablesotros(Registro reg)
    {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_FINANCIABLES
                                                            .getDeleteKey());

            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubfinanciablesotros();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que se ejecuta cuando el registro que se va a modificar
     * es el concepto = 12. Debido a que concepto no se permite
     * modificar.
     *
     * @return true: el registro no puede ser modificado.
     */
    public boolean saldo12()
    {
        boolean saldo12 = false;
        if (("12").equals(registroSubSubFinanciablesConsulta.getCampos()
                        .get(GeneralParameterEnum.CONCEPTO.getName())))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1625"));
            saldo12 = true;
        }
        return saldo12;
    }

    public void cancelarEdicionSubfinanciablesotros()
    {
        cargarListaSubfinanciablesotros();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Metodo ejecutado al agregar o editar un financiable
     *
     * @param respuesta
     */
    private void ejecutarAccion(boolean respuesta)
    {

        if (respuesta
            && !(boolean) registroAuxiliarFin.getCampos()
                            .get(FinanciablesfacturasControladorEnum.BLOQUEADO
                                            .getValue()))
        {

            if (validarDialogoVisible())
            {
                aplicaPeriodo = true; // aceptarfinanciableAplicaPeriodo
                dialogoAFinVisible = false;

            }
            else
            {

                if (accionarSubFinanciables())
                {
                    calcula = true;
                    calcularFactura();
                    return;
                }
            }

        }
        else if (respuesta && accionarSubFinanciables())
        {
            calcula = false;
            calcularFactura();
            return;

        }
    }

    /**
     * Metodo llamado en ejecutarAccion
     *
     * @return verdadero si la condicion se cumple
     */
    private boolean validarDialogoVisible()
    {
        if (!"P".equals(txtFimm)
            && SysmanFunciones.validarVariableVacio(bancoPerProceso)
            && !"0".equals(numeroFactura) && !auxBancoPer)
        {
            return true;
        }
        return false;
    }

    // ETAPA 01
    /**
     * Se ejecuta el llamado a la funcion FC_AUDITORIAGENERAL,
     * dependiendo la accion que se ejecute en el subFormulario
     * Financiable, el valor del parametro UN_SUBPROCESO, cambiara a
     * creacion o edicion
     *
     * @param validacion
     */
    private void insertarAuditoriaGeneral(boolean validacion)
    {
        try
        {
            if (validacion)
            {

                ejbServiciosPublicosTres.auditoriaGeneral(compania,
                                SessionUtil.getUser().getCodigo(),
                                "FINANCIABLES",
                                "Creacion", Integer.valueOf(ano), periodo,
                                codigoInterno,
                                "Monto:"
                                    + SysmanFunciones
                                                    .nvlStr(registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.PARAM1
                                                                                    .getValue())
                                                                    .toString(),
                                                                    "0")
                                    + ";Saldo: "
                                    + SysmanFunciones
                                                    .nvlStr(registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                                                                    .getValue())
                                                                    .toString(),
                                                                    "0")
                                    + ";Cuotas: "
                                    + SysmanFunciones
                                                    .nvlStr(registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                                                                    .getValue())
                                                                    .toString(),
                                                                    "0")
                                    + "; Cuota: "
                                    + SysmanFunciones
                                                    .nvlStr(registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.PARAM2
                                                                                    .getValue())
                                                                    .toString(),
                                                                    "0")
                                    + ";Valor Cuota:"
                                    + SysmanFunciones
                                                    .nvlStr(registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.VALORCUOTA
                                                                                    .getValue())
                                                                    .toString(),
                                                                    "0")
                                    + ";Concepto: "
                                    + SysmanFunciones
                                                    .nvlStr(registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(GeneralParameterEnum.CONCEPTO
                                                                                    .getName())
                                                                    .toString(),
                                                                    "0")
                                    + " ");

            }
            else
            {
                ejbServiciosPublicosTres.auditoriaGeneral(compania,
                                SessionUtil.getUser().getCodigo(),
                                "FINANCIABLES",
                                "Edicion", Integer.valueOf(ano), periodo,
                                codigoInterno,
                                "Monto:"
                                    + SysmanFunciones.nvl(
                                                    registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.PARAM1
                                                                                    .getValue()),
                                                    "0")
                                    + ";Saldo: "
                                    + SysmanFunciones.nvl(
                                                    registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                                                                    .getValue()),
                                                    "0")
                                    + ";Cuotas: "
                                    + SysmanFunciones.nvl(
                                                    registroAuxiliarFin
                                                                    .getCampos()
                                                                    .get(FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                                                                    .getValue()),
                                                    "0")
                                    + ";Cuota: "
                                    + SysmanFunciones.nvl(registroAuxiliarFin
                                                    .getCampos()
                                                    .get(FinanciablesfacturasControladorEnum.PARAM2
                                                                    .getValue()),
                                                    "0")
                                    + ";Valor Cuota: "
                                    + SysmanFunciones.nvl(registroAuxiliarFin
                                                    .getCampos()
                                                    .get(FinanciablesfacturasControladorEnum.VALORCUOTA
                                                                    .getValue()),
                                                    "0")
                                    + ";Concepto:"
                                    + SysmanFunciones.nvl(
                                                    registroSubSubFinanciablesConsulta
                                                                    .getCampos()
                                                                    .get("CONCEPTO"),
                                                    "0")
                                    + "");
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // Access -> Form_BeforeUpdate
    /**
     * Metodo que se ejecuta al agregar o editar un registro del
     * subFormulario Financiables, teniendo en cuenta que si el
     * usuario seleccionado tiene como estado de facturacion en plano
     * en 'P - Usuario en terreno' TABLA SP_USUARIO -- CAMPO FIMM --
     * valor P
     */
    public void aceptarDialogAgregarFinanciable()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            validarPeriodosNoCobro();

            validarFinanciable = ejbServPublicosOcho.validarFinanciable(
                            compania,
                            Integer.valueOf(ciclo), Integer.valueOf(ano),
                            periodo,
                            true, txtFimm, Long.parseLong(lectura),
                            bancoPerProceso,
                            periodosNoCobradosFac, fecha);

            ejecutarAccion(validarFinanciable);

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que realiza la accion de agregar o editar un financible
     * dependiendo de un parametro que se cambia en los metodos de
     * agregar y editar subFinanciablesConsulta
     *
     * @return retorna verdadero si la accion se ejecuto correctamente
     */
    private boolean accionarSubFinanciables()
    {
        try
        {
            if (validarAux)
            {

                if ("P".equals(txtFimm)
                    || !SysmanFunciones.validarVariableVacio(bancoPerProceso))
                {
                    registroAuxiliarFin.getCampos().put(
                                    GeneralParameterEnum.ANO.getName(),
                                    anoSig);
                    registroAuxiliarFin.getCampos().put(
                                    GeneralParameterEnum.PERIODO.getName(),
                                    perSig);
                }
                else
                {
                    registroAuxiliarFin.getCampos().put(
                                    GeneralParameterEnum.ANO.getName(),
                                    ano);
                    registroAuxiliarFin.getCampos().put(
                                    GeneralParameterEnum.PERIODO.getName(),
                                    periodo);
                }

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FinanciablesfacturasControladorUrlEnum.URL80142
                                                                .getValue());

                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                registroAuxiliarFin.getCampos());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_REGISTRO_INGRESADO"));
                insertarAuditoriaGeneral(validarAux);
                cargarListaSubfinanciablesconsulta();
                cargarListaSubfinanciablesotros();
                return true;
            }
            else
            {
                boolean diaAux = dialogo == aceptarDialogo;

                if (diaAux && !validarDialogoBloqueado()
                                .equals(idioma.getObject(
                                                "MSM_PROCESO_EJECUTADO")
                                                .toString().replace(".", "")))
                {
                    return false;
                }

                Registro reg = registroRemover(registroAuxiliarFin);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FinanciablesfacturasControladorUrlEnum.URL80902
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                reg.getCampos(),
                                reg.getLlave());

                cargarListaSubfinanciablesconsulta();
                insertarAuditoriaGeneral(validarAux);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "MSM_REGISTRO_MODIFICADO"));
                mensajeCalculo = validarDialogoBloqueado();
                return true;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;
    }

    public String validarDialogoBloqueado()
    {
        String respuesta = "";
        if (dialogo == aceptarDialogo)
        {

            try
            {
                respuesta = ejbServPublicosSiete.calcularFacturacion(
                                compania, Integer.valueOf(ciclo),
                                codigoRuta,
                                codigoRuta, false, false,
                                SessionUtil.getUser().getCodigo());

                return respuesta;
            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        return respuesta;
    }

    /**
     * Metodo para cargar los valores del registro seleccionado en la
     * edicion del sub formulario financiables
     *
     * @param reg:
     * equivale al registro que contiene los campos almacenados del
     * registro seleccionado.
     */
    public void activarEdicionSubfinanciablesconsulta(Registro reg)
    {
        indiceSubfinanciablesconsulta = reg.getIndice();
        regFinanciable = new Registro(new HashMap<>(reg.getCampos()));
        registroDialogo = reg;

    }

    /**
     * Metodo ejecutado al agregar o editar un financiable
     *
     * @return retorna verdadero si el periodo no cobro es diferente
     * de 0
     */
    public void validarPeriodosNoCobro()
    {
        if (!SysmanFunciones.validarVariableVacio(bancoPerProceso))
        {

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1537"));
            calcula = false;
            auxBancoPer = true;
        }
        else if (Integer.parseInt(periodoNoCobroFin) != 0)
        {

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3185").replace(
                                            "s$periodoNoCobroFin$s",
                                            periodoNoCobroFin));
        }
    }

    /**
     * Metodo que se ejecuta si le da clic en el boton aceptar del
     * dialogo
     */
    public void aceptarfinanciableAplicaPeriodo()
    {

        calcula = true;
        if (accionarSubFinanciables())
        {

            calcularFactura();
            return;
        }
        // DESPUES DE LLAMADA ACTUALIZAR REGISTRO DE FACTURA
        // periodo actual
    }

    /**
     * Metodo que se ejecuta si el atributo calcula se encuentra en
     * verdadero
     */
    private void calcularFactura()
    {
        try
        {
            if (calcula)
            {

                String respuesta = ejbServPublicosSiete.calcularFacturacion(
                                compania, Integer.valueOf(ciclo), codigoRuta,
                                codigoRuta, false, false,
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(respuesta);
                return;
            }
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Campos a remover, para realizar una insercion del subFormulario
     * Financiables
     *
     * @param reg
     * @return
     */
    private Registro registroRemoverC()
    {
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.CODIGORUTA.getName(),
                        codigoRuta);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(GeneralParameterEnum.CICLO.getName(), ciclo);
        registroSubSubFinanciablesConsulta.getCampos()
                        .put(GeneralParameterEnum.ANO.getName(), ano);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.PERIODO.getName(),
                        periodo);
        registroSubSubFinanciablesConsulta.getCampos().put("ANOINICIAL",
                        ano);
        registroSubSubFinanciablesConsulta.getCampos().put("PERIODOINICIAL",
                        periodo);
        registroSubSubFinanciablesConsulta.getCampos().put(
                        FinanciablesfacturasControladorEnum.PARAM6
                                        .getValue(),
                        ano);
        registroSubSubFinanciablesConsulta.getCampos()
                        .remove(GeneralParameterEnum.NOMBRE.getName());
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());
        registroSubSubFinanciablesConsulta.getCampos().put(
                        GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        if ((boolean) registroSubSubFinanciablesConsulta.getCampos()
                        .get(FinanciablesfacturasControladorEnum.BLOQUEADO
                                        .getValue()))
        {
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM18
                                            .getValue(),
                                            new Date());
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM19
                                            .getValue(),
                                            SessionUtil.getUser().getCodigo());
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM20
                                            .getValue(),
                                            new Date());
        }
        else
        {
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM18
                                            .getValue(),
                                            null);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM19
                                            .getValue(),
                                            null);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM20
                                            .getValue(),
                                            null);
        }
        return registroSubSubFinanciablesConsulta;
    }

    /**
     * Campos a remover, para realizar una edicion del subFormulario
     * Financiables
     *
     * @param reg
     * @return
     */
    private Registro registroRemover(Registro reg)
    {

        reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        reg.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        reg.getCampos().put("KEY_CONCEPTO",
                        reg.getCampos()
                                        .get(GeneralParameterEnum.CONCEPTO
                                                        .getName()));
        reg.getCampos().remove(GeneralParameterEnum.CONCEPTO.getName());
        reg.getCampos().remove(GeneralParameterEnum.CODIGORUTA.getName());
        reg.getCampos().remove(
                        FinanciablesfacturasControladorEnum.PARAM2.getValue());
        reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        reg.getCampos().put("PERIODOINICIAL",
                        periodo);
        reg.getCampos().put("ANOINICIAL",
                        ano);
        reg.getCampos().remove(GeneralParameterEnum.DATE_CREATED.getName());
        reg.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                        new Date());

        if ("P".equals(txtFimm)
            || !SysmanFunciones.validarVariableVacio(bancoPerProceso))
        {
            reg.getCampos().put(
                            GeneralParameterEnum.ANO.getName(),
                            anoSig);
            reg.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                            perSig);
            reg.getCampos().put("KEY_ANO", anoSig);
            reg.getCampos().put("KEY_PERIODO", perSig);
        }
        else
        {
            reg.getCampos().put(
                            GeneralParameterEnum.ANO.getName(),
                            ano);
            reg.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                            periodo);
            reg.getCampos().put("KEY_ANO", ano);
            reg.getCampos().put("KEY_PERIODO", periodo);
        }

        reg.getCampos().put("KEY_COMPANIA", compania);
        reg.getCampos().put("KEY_CICLO", ciclo);
        reg.getCampos().put("KEY_CODIGORUTA", codigoRuta);
        if ((boolean) reg.getCampos().get(
                        FinanciablesfacturasControladorEnum.BLOQUEADO
                                        .getValue()))
        {
            reg.getCampos().put(FinanciablesfacturasControladorEnum.PARAM18
                            .getValue(), new Date());
            reg.getCampos().put(FinanciablesfacturasControladorEnum.PARAM19
                            .getValue(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(FinanciablesfacturasControladorEnum.PARAM20
                            .getValue(), new Date());
        }
        else
        {
            reg.getCampos().put(FinanciablesfacturasControladorEnum.PARAM18
                            .getValue(), null);
            reg.getCampos().put(FinanciablesfacturasControladorEnum.PARAM19
                            .getValue(),
                            null);
            reg.getCampos().put(FinanciablesfacturasControladorEnum.PARAM20
                            .getValue(), null);
        }

        return reg;
    }

    /**
     * Metodo que se ejecuta cuando se da click en No en el dialogo
     * AgregarFinanciables. Cierra la ventana del dialogo
     */
    public void cancelarDialogAgregarFinanciable()
    {
        // <CODIGO_DESARROLLADO>
        dialogoAFinVisible = false;
        if (validarAux)
        {
            registroSubSubFinanciablesConsulta.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.PARAM1
                                            .getValue(),
                            0);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.SALDOFINANCIABLE
                                            .getValue(),
                            0);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.NUMEROCUOTAS
                                            .getValue(),
                            0);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM2
                                            .getValue(),
                                            0);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.VALORCUOTA
                                            .getValue(),
                                            0);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            GeneralParameterEnum.PERIODO.getName(),
                            periodo);
            registroSubSubFinanciablesConsulta.getCampos().put(
                            FinanciablesfacturasControladorEnum.PARAM6
                                            .getValue(),
                            ano);
            registroSubSubFinanciablesConsulta.getCampos()
                            .put(FinanciablesfacturasControladorEnum.PARAM9
                                            .getValue(), periodo);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al darle clic en si del dialogo que se
     * visuliza despues de realizar validaciones en cambiarBloqueado
     */
    public void aceptardialogoUnoBloqueado()
    {
        // <CODIGO_DESARROLLADO>
        calcula = true;
        aceptarDialogo = true;
        validarAux = false;
        validarPeriodosNoCobro();

        try
        {
            validarFinanciable = ejbServPublicosOcho.validarFinanciable(
                            compania,
                            Integer.valueOf(ciclo),
                            Integer.valueOf(ano),
                            periodo,
                            true, txtFimm, Long.parseLong(lectura),
                            bancoPerProceso,
                            periodosNoCobradosFac, fechaAux);
            if (dialogo)
            {
                registroAuxiliarFin = registroDialogo;
            }
            accionarSubFinanciables();
            JsfUtil.agregarMensajeInformativo(mensajeCalculo);
            cargarListaSubfinanciablesconsulta();

        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // Pendiente hacer el llamado a la funci�n:
        // CalculoFacturacion

    }

    /**
     * Metodo que se ejecuta cuando se da click en el boton no del
     * dialogo DialogoUnoBloqueado
     */
    public void cancelardialogoUnoBloqueado()
    {
        // <CODIGO_DESARROLLADO>
        dialogoUBloqVisible = false;
        auxBloqueo = false;
        aplicaPeriodo = false;
        cargarListaSubfinanciablesconsulta();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al darle clic en el boton Siguiente
     * Per�odo, del dialogo financiableAplicaPeriodo
     */
    public void cancelarfinanciableAplicaPeriodo()
    {

        if (!validarAux)
        {
            registroAuxiliarFin = regFinanciable;
        }

        registroAuxiliarFin.getCampos().put(
                        FinanciablesfacturasControladorEnum.BLOQUEADO
                                        .getValue(),
                        -1);

        if (accionarSubFinanciables())
        {
            return;
        }

        aplicaPeriodo = false;
        // Siguiente periodo
    }

    /**
     * Despues de eliminar un financiable, hacer el calculo de
     * facturacion de nuevo
     */
    public void calcularDespues()
    {
        if (!(boolean) registroAuxiliarFin.getCampos().get(
                        FinanciablesfacturasControladorEnum.BLOQUEADO
                                        .getValue())
            && SysmanFunciones.validarVariableVacio(bancoPerProceso))
        {

            try
            {
                ejbServPublicosSiete.calcularFacturacion(
                                compania, Integer.valueOf(ciclo),
                                codigoRuta,
                                codigoRuta, false, false,
                                SessionUtil.getUser().getCodigo());
            }
            catch (NumberFormatException | SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
            return;

        }
    }

    /**
     * Metodo usado para asegurar que al cerrar el formulario, retorne
     * al formularios facturaintegrado.
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        HashMap<String, Object> ridR = new HashMap<>();
        ridR.put(compania, compania);
        ridR.put(ciclo, ciclo);
        ridR.put(codigoRuta, codigoRuta);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridR);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FACTURAINTEGRADOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionar(direccionador);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        try
        {
            anoSig = ejbServiciosPublicosCero
                            .prepararAnoPeriodoSiguiente(
                                            compania, Integer.valueOf(ano),
                                            periodo, "0",
                                            "");

            perSig = ejbServiciosPublicosCero
                            .prepararAnoPeriodoSiguiente(
                                            compania, Integer.valueOf(ano),
                                            periodo, "1",
                                            "");

            // <CODIGO_DESARROLLADO>
            codRutaFin = "";
            cicloFin = "";

            String codigoAlmacen = registroSubSubFinanciablesConsulta
                            .getCampos()
                            .get(FinanciablesfacturasControladorEnum.PARAM3
                                            .getValue()) == null ? ""
                                                : registroSubSubFinanciablesConsulta
                                                                .getCampos()
                                                                .get(FinanciablesfacturasControladorEnum.PARAM3
                                                                                .getValue())
                                                                .toString();
            String parametro = ejbSysmanUtl.consultarParametro(compania,
                            "MANEJA CODIGO DE ALMACEN", modulo, new Date(),
                            true);

            if (parametro == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1510"));
                return;
            }

            if ("SI".equals(parametro))
            {
                codigoAlmacenVisible = true;
            }
            else
            {
                codigoAlmacenVisible = false;
            }

            if (!("").equals(codigoAlmacen))
            {
                codigoAlmacenbloquedo = true;
            }
            else
            {
                codigoAlmacenbloquedo = false;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            Logger.getLogger(FinanciablesfacturasControlador.class.getName())
                            .log(Level.SEVERE, null, e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
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

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombreConcepto
     *
     * @return nombreConcepto
     */
    public String getNombreConcepto()
    {
        return nombreConcepto;
    }

    /**
     * Asigna la variable nombreConcepto
     *
     * @param nombreConcepto
     * Variable a asignar en nombreConcepto
     */
    public void setNombreConcepto(String nombreConcepto)
    {
        this.nombreConcepto = nombreConcepto;
    }

    /**
     * Retorna la variable nombreConceptoO
     *
     * @return nombreConceptoO
     */
    public String getNombreConceptoO()
    {
        return nombreConceptoO;
    }

    /**
     * Asigna la variable nombreConceptoO
     *
     * @param nombreConceptoO
     * Variable a asignar en nombreConceptoO
     */
    public void setNombreConceptoO(String nombreConceptoO)
    {
        this.nombreConceptoO = nombreConceptoO;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoRuta
     *
     * @return codigoRuta
     */
    public String getCodigoRuta()
    {
        return codigoRuta;
    }

    /**
     * Asigna la variable codigoRuta
     *
     * @param codigoRuta
     * Variable a asignar en codigoRuta
     */
    public void setCodigoRuta(String codigoRuta)
    {
        this.codigoRuta = codigoRuta;
    }

    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public String getAno()
    {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano)
    {
        this.ano = ano;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable cmdDetalleVisible
     *
     * @return cmdDetalleVisible
     */
    public boolean isCmdDetalleVisible()
    {
        return cmdDetalleVisible;
    }

    /**
     * Asigna la variable cmdDetalleVisible
     *
     * @param cmdDetalleVisible
     * Variable a asignar en cmdDetalleVisible
     */
    public void setCmdDetalleVisible(boolean cmdDetalleVisible)
    {
        this.cmdDetalleVisible = cmdDetalleVisible;
    }

    /**
     * Retorna la variable cmdTotalVisible
     *
     * @return cmdTotalVisible
     */
    public boolean isCmdTotalVisible()
    {
        return cmdTotalVisible;
    }

    /**
     * Asigna la variable cmdTotalVisible
     *
     * @param cmdTotalVisible
     * Variable a asignar en cmdTotalVisible
     */
    public void setCmdTotalVisible(boolean cmdTotalVisible)
    {
        this.cmdTotalVisible = cmdTotalVisible;
    }

    /**
     * Retorna la variable codigoAlmacenVisible
     *
     * @return codigoAlmacenVisible
     */
    public boolean isCodigoAlmacenVisible()
    {
        return codigoAlmacenVisible;
    }

    /**
     * Asigna la variable codigoAlmacenVisible
     *
     * @param codigoAlmacenVisible
     * Variable a asignar en codigoAlmacenVisible
     */
    public void setCodigoAlmacenVisible(boolean codigoAlmacenVisible)
    {
        this.codigoAlmacenVisible = codigoAlmacenVisible;
    }

    /**
     * Retorna la variable codigoAlmacenbloquedo
     *
     * @return codigoAlmacenbloquedo
     */
    public boolean isCodigoAlmacenbloquedo()
    {
        return codigoAlmacenbloquedo;
    }

    /**
     * Asigna la variable codigoAlmacenbloquedo
     *
     * @param codigoAlmacenbloquedo
     * Variable a asignar en codigoAlmacenbloquedo
     */
    public void setCodigoAlmacenbloquedo(boolean codigoAlmacenbloquedo)
    {
        this.codigoAlmacenbloquedo = codigoAlmacenbloquedo;
    }

    /**
     * Retorna la variable dialogoUBloqVisible
     *
     * @return dialogoUBloqVisible
     */
    public boolean isDialogoUBloqVisible()
    {
        return dialogoUBloqVisible;
    }

    /**
     * Asigna la variable dialogoUBloqVisible
     *
     * @param dialogoUBloqVisible
     * Variable a asignar en dialogoUBloqVisible
     */
    public void setDialogoUBloqVisible(boolean dialogoUBloqVisible)
    {
        this.dialogoUBloqVisible = dialogoUBloqVisible;
    }

    /**
     * Retorna la variable dialogoAFinVisible
     *
     * @return dialogoAFinVisible
     */
    public boolean isDialogoAFinVisible()
    {
        return dialogoAFinVisible;
    }

    /**
     * Asigna la variable dialogoAFinVisible
     *
     * @param dialogoAFinVisible
     * Variable a asignar en dialogoAFinVisible
     */
    public void setDialogoAFinVisible(boolean dialogoAFinVisible)
    {
        this.dialogoAFinVisible = dialogoAFinVisible;
    }

    /**
     * Retorna la variable codigoInterno
     *
     * @return codigoInterno
     */
    public String getCodigoInterno()
    {
        return codigoInterno;
    }

    /**
     * Asigna la variable codigoInterno
     *
     * @param codigoInterno
     * Variable a asignar en codigoInterno
     */
    public void setCodigoInterno(String codigoInterno)
    {
        this.codigoInterno = codigoInterno;
    }

    /**
     * Retorna la variable concepto
     *
     * @return concepto
     */
    public String getConcepto()
    {
        return concepto;
    }

    /**
     * Asigna la variable concepto
     *
     * @param concepto
     * Variable a asignar en concepto
     */
    public void setConcepto(String concepto)
    {
        this.concepto = concepto;
    }

    /**
     * Retorna la variable codRutaFin
     *
     * @return codRutaFin
     */
    public String getCodRutaFin()
    {
        return codRutaFin;
    }

    /**
     * Asigna la variable codRutaFin
     *
     * @param codRutaFin
     * Variable a asignar en codRutaFin
     */
    public void setCodRutaFin(String codRutaFin)
    {
        this.codRutaFin = codRutaFin;
    }

    /**
     * Retorna la variable cicloFin
     *
     * @return cicloFin
     */
    public String getCicloFin()
    {
        return cicloFin;
    }

    /**
     * Asigna la variable cicloFin
     *
     * @param cicloFin
     * Variable a asignar en cicloFin
     */
    public void setCicloFin(String cicloFin)
    {
        this.cicloFin = cicloFin;
    }

    /**
     * Retorna la variable periodoNoCobroFin
     *
     * @return periodoNoCobroFin
     */
    public String getPeriodoNoCobroFin()
    {
        return periodoNoCobroFin;
    }

    /**
     * Asigna la variable periodoNoCobroFin
     *
     * @param periodoNoCobroFin
     * Variable a asignar en periodoNoCobroFin
     */
    public void setPeriodoNoCobroFin(String periodoNoCobroFin)
    {
        this.periodoNoCobroFin = periodoNoCobroFin;
    }

    /**
     * Retorna la variable lectura
     *
     * @return lectura
     */
    public String getLectura()
    {
        return lectura;
    }

    /**
     * Asigna la variable lectura
     *
     * @param lectura
     * Variable a asignar en lectura
     */
    public void setLectura(String lectura)
    {
        this.lectura = lectura;
    }

    /**
     * Retorna la variable periodosNoCobradosFac
     *
     * @return periodosNoCobradosFac
     */
    public String getPeriodosNoCobradosFac()
    {
        return periodosNoCobradosFac;
    }

    /**
     * Asigna la variable periodosNoCobradosFac
     *
     * @param periodosNoCobradosFac
     * Variable a asignar en periodosNoCobradosFac
     */
    public void setPeriodosNoCobradosFac(String periodosNoCobradosFac)
    {
        this.periodosNoCobradosFac = periodosNoCobradosFac;
    }

    /**
     * Asigna la variable bancoPerProceso
     *
     * @param bancoPerProceso
     * Variable a asignar en bancoPerProceso
     */
    public void setBancoPerProceso(String bancoPerProceso)
    {
        this.bancoPerProceso = bancoPerProceso;
    }

    /**
     * Asigna la variable aplicaPeriodo
     *
     * @param aplicaPeriodo
     * Variable a asignar en aplicaPeriodo
     */
    public boolean isAplicaPeriodo()
    {
        return aplicaPeriodo;
    }

    /**
     * Asigna la variable aplicaPeriodo
     *
     * @param aplicaPeriodo
     * Variable a asignar en aplicaPeriodo
     */
    public void setAplicaPeriodo(boolean aplicaPeriodo)
    {
        this.aplicaPeriodo = aplicaPeriodo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listabloqueadoHastaAno
     *
     * @return listabloqueadoHastaAno
     */
    public List<Registro> getListabloqueadoHastaAno()
    {
        return listabloqueadoHastaAno;
    }

    /**
     * Asigna la lista listabloqueadoHastaAno
     *
     * @param listabloqueadoHastaAno
     * Variable a asignar en listabloqueadoHastaAno
     */
    public void setListabloqueadoHastaAno(
        List<Registro> listabloqueadoHastaAno)
    {
        this.listabloqueadoHastaAno = listabloqueadoHastaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaConcepto()
    {
        return listaConcepto;
    }

    public void setListaConcepto(RegistroDataModelImpl listaConcepto)
    {
        this.listaConcepto = listaConcepto;
    }

    public RegistroDataModelImpl getListaConceptoE()
    {
        return listaConceptoE;
    }

    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE)
    {
        this.listaConceptoE = listaConceptoE;
    }

    /**
     * Retorna la lista listabloqueadoHastaPeriodo
     *
     * @return listabloqueadoHastaPeriodo
     */
    public RegistroDataModelImpl getListabloqueadoHastaPeriodo()
    {
        return listabloqueadoHastaPeriodo;
    }

    /**
     * Asigna la lista listabloqueadoHastaPeriodo
     *
     * @param listabloqueadoHastaPeriodo
     * Variable a asignar en listabloqueadoHastaPeriodo
     */
    public void setListabloqueadoHastaPeriodo(
        RegistroDataModelImpl listabloqueadoHastaPeriodo)
    {
        this.listabloqueadoHastaPeriodo = listabloqueadoHastaPeriodo;
    }

    /**
     * Retorna la lista listabloqueadoHastaPeriodo
     *
     * @return listabloqueadoHastaPeriodo
     */
    public RegistroDataModelImpl getListabloqueadoHastaPeriodoE()
    {
        return listabloqueadoHastaPeriodoE;
    }

    /**
     * Asigna la lista listabloqueadoHastaPeriodo
     *
     * @param listabloqueadoHastaPeriodo
     * Variable a asignar en listabloqueadoHastaPeriodo
     */
    public void setListabloqueadoHastaPeriodoE(
        RegistroDataModelImpl listabloqueadoHastaPeriodoE)
    {
        this.listabloqueadoHastaPeriodoE = listabloqueadoHastaPeriodoE;
    }

    public RegistroDataModelImpl getListaperiodo()
    {
        return listaperiodo;
    }

    public void setListaperiodo(RegistroDataModelImpl listaperiodo)
    {
        this.listaperiodo = listaperiodo;
    }

    public RegistroDataModelImpl getListaperiodoE()
    {
        return listaperiodoE;
    }

    public void setListaperiodoE(RegistroDataModelImpl listaperiodoE)
    {
        this.listaperiodoE = listaperiodoE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaConceptoO()
    {
        return listaConceptoO;
    }

    public void setListaConceptoO(RegistroDataModelImpl listaConceptoO)
    {
        this.listaConceptoO = listaConceptoO;
    }

    public RegistroDataModelImpl getListaConceptoOE()
    {
        return listaConceptoOE;
    }

    public void setListaConceptoOE(RegistroDataModelImpl listaConceptoOE)
    {
        this.listaConceptoOE = listaConceptoOE;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubfinanciablesconsulta()
    {
        return listaSubfinanciablesconsulta;
    }

    public void setListaSubfinanciablesconsulta(
        List<Registro> listaSubfinanciablesconsulta)
    {
        this.listaSubfinanciablesconsulta = listaSubfinanciablesconsulta;
    }

    public List<Registro> getListaSubfinanciablesotros()
    {
        return listaSubfinanciablesotros;
    }

    public void setListaSubfinanciablesotros(
        List<Registro> listaSubfinanciablesotros)
    {
        this.listaSubfinanciablesotros = listaSubfinanciablesotros;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSubFinanciablesConsulta
     *
     * @return registroSubSubFinanciablesConsulta
     */
    public Registro getRegistroSubSubFinanciablesConsulta()
    {
        return registroSubSubFinanciablesConsulta;
    }

    /**
     * Asigna el objeto registroSubSubFinanciablesConsulta
     *
     * @param registroSubSubFinanciablesConsulta
     * Variable a asignar en registroSubSubFinanciablesConsulta
     */
    public void setRegistroSubSubFinanciablesConsulta(
        Registro registroSubSubFinanciablesConsulta)
    {
        this.registroSubSubFinanciablesConsulta = registroSubSubFinanciablesConsulta;
    }

    /**
     * Retorna el objeto registroSubSubFinanciablesOtros
     *
     * @return registroSubSubFinanciablesOtros
     */
    public Registro getRegistroSubSubFinanciablesOtros()
    {
        return registroSubSubFinanciablesOtros;
    }

    /**
     * Asigna el objeto registroSubSubFinanciablesOtros
     *
     * @param registroSubSubFinanciablesOtros
     * Variable a asignar en registroSubSubFinanciablesOtros
     */
    public void setRegistroSubSubFinanciablesOtros(
        Registro registroSubSubFinanciablesOtros)
    {
        this.registroSubSubFinanciablesOtros = registroSubSubFinanciablesOtros;
    }

    public int getIndiceSubfinanciablesconsulta()
    {
        return indiceSubfinanciablesconsulta;
    }

    public void setIndiceSubfinanciablesconsulta(
        int indiceSubfinanciablesconsulta)
    {
        this.indiceSubfinanciablesconsulta = indiceSubfinanciablesconsulta;
    }

    // </SET_GET_ADICIONALES>

    public void reasignarOrigenGrilla()
    {
        // El origen de grilla se asigna a cada uno de los
        // subformularios.

    }

    @Override
    public void asignarOrigenDatos()
    {
        // El origen de datos se asigna a cada uno de los
        // subformularios

    }
}
