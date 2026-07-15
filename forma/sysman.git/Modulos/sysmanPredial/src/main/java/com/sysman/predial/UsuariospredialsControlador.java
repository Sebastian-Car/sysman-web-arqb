package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.enums.UsuariospredialsControladorEnum;
import com.sysman.predial.enums.UsuariospredialsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APICalculoPredial;

import java.io.ByteArrayInputStream;
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
 *
 * @author sdaza
 * @version 6, 08/06/2016 16:40:05 -- Modificado por sdaza
 * @version , 13/036/2017 sdaza-- se realizan ajuste por alertas de
 * sonar
 *
 * @author ybecerra
 * @version 7, 28/06/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class UsuariospredialsControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;

    /** Constante a nivel de clase que aloja el valor CLASE_PREDIO */
    private final String clasePredio;

    /** Constante a nivel de clase que aloja el valor CODIGO */
    private final String cCodigo;

    /** Constante a nivel de clase que aloja el valor DIRECCION */
    private final String direccion;

    /**
     * Constante a nivel de clase que aloja el valor direccionPredio
     */
    private final String direccionPredio;

    /** Constante a nivel de clase que aloja el valor INDBORRADO */
    private final String indBorrado;

    /** Constante a nivel de clase que aloja el valor NOMBRE */
    private final String nombre;

    /**
     * Constante a nivel de clase que aloja el valor nomPropietario
     */
    private final String nombrePropietario;

    /**
     * Constante a nivel de clase que aloja el valor NUMERO_PROCESO
     */
    private final String numeroProceso;

    /** Constante a nivel de clase que aloja el valor NUMERO_ORDEN */
    private final String numeroOrden;

    /** Constante a nivel de clase que aloja el valor nroOrden */
    private final String nroOrden;

    /** Constante a nivel de clase que aloja el valor retorna */
    private final String retorna;

    /**
     * Constante a nivel de clase que aloja el valor retornoFormulario
     */
    private final String retornaFormulario;

    /** Constante a nivel de clase que aloja el valor TB_TB225 */
    private final String tb255;

    /** Constante a nivel de clase que aloja el valor TIPO_MUTACION */
    private final String tipoMutacion;

    /** Constante a nivel de clase que aloja el valor TRPCOD */
    private final String trpcod;

    /** Constante a nivel de clase que aloja el valor TRPRAN */
    private final String trpran;

    /** Constante a nivel de clase que aloja el valor UBICACION */
    private final String ubicacion;

    /** Constante a nivel de clase que aloja el valor accion */
    private final String cAccion;

    /** Constante a nivel de clase que aloja el valor codigoPredio */
    private final String codigoPredio;

    /**
     * Constante a nivel de clase que aloja el valor
     * MSM_TRANS_INTERRUMPIDA
     */
    private final String mensaje1;

    /**
     * Constante a nivel de clase que aloja el valor
     * ESTRATO_SOCIOECONOMICO
     */
    private final String estratoSocioE;

    /**
     * Constante a nivel de clase que aloja el valor indReserva
     */
    private final String indicadorReserva;
    /**
     * Constante a nivel de clase que aloja el valor INDICADOR_RESERVA
     */
    private final String campoIndicadorReserva;

    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String alertaPagoDoble;
    private String labCobro;
    private String estadoPredio;
    private String alerta1607;
    private String acuerdo;
    private String procesoJudicial;
    private String sector;
    private String manzana;
    private String predio;
    private String mejora;
    private String tipoP;
    private String porcReserva;
    private String nomUbicacion;
    private String nomTipoPredio;
    private String nomClasePredio;
    private String nomTipoMutacion;
    private String tituloParametro;
    private String descripTarifa;
    private String nomEstratoSoc;
    private String pais;
    private String dpto;
    private String anoActual;
    private String codTarifaActual;
    private String rangoTarifaActual;
    private StreamedContent archivoDescarga;
    private boolean indVerDgEstado;
    private boolean indVerBtBancos;
    private boolean indVerBtCaja;
    private boolean indVerBtFinanciar;
    private boolean indVerBtConsAcuerdos;
    private boolean indVerBtEstCuenta;
    private boolean indVerBtActivar;
    private boolean indVerBtInfCatastral;
    private boolean indVerBtRecProp;
    private boolean indVerDaminificado;
    private boolean indVerLey10032015;
    private boolean indVerCodEquiv;
    private boolean bloqExento;
    private boolean verOtroFactor;
    private String cantPZ;
    private String tituloForm;
    /**
     * Constante definida para almacenar la cadena "CODIGO_ESTRATO"
     */
    private String cCodigoEstrato;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTarifaActual;
    private List<Registro> listaTipoNit;
    private List<Registro> listaCmbPaisNotificacion;
    private List<Registro> listaCmbDptoNotificacion;
    private List<Registro> listaCmbCiudadNotificacion;
    private List<Registro> listaFormatoEstrato;
    private List<Registro> listaPagBan;
    private List<Registro> listaPagBan1;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaUbicacion;
    private RegistroDataModelImpl listaTipo;
    private RegistroDataModelImpl listaClasePredio;
    private RegistroDataModelImpl listatipoMutacion;
    private RegistroDataModelImpl listaTrpcod;
    private RegistroDataModelImpl listaEstratoSocioeconomico;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    private String verCptos;
    private String manOtroFactor;
    private String leyDescuentos;
    private String activarBtBancos;
    private String activarBtCaja;
    private String inicioFacturacion;
    private boolean generaFacturacion;
    private boolean generaFacturacionD;
    private String manejaFinanciables;
    private boolean usuarioManejaAcuerdos;
    private String usuarioManejaEstCuenta;
    private String permiteCalculoAutom;
    private String manejaIndDamnificado;
    private boolean admonBloqPredios;
    private String manejaInfCatastral;
    private String manejaRecPropietario;
    private String calcularLey10032015;
    private String codCatastral30;
    private String nivelAdmonExento;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private boolean tienePagosDobles;
    private Map<String, Object> parametrosEntrada;
    private boolean verBotones;
    private boolean visibleUsuarios;

    /**
     * Constante que identifica el servicio que busca la URL y tipo de
     * conexión
     */
    private static final String SERVICIO_API = "1710001";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    // </DECLARAR_ADICIONALES>
    @SuppressWarnings("unchecked")
    public UsuariospredialsControlador() {
        super();
        compania = SessionUtil.getCompania();

        tipoMutacion = UsuariospredialsControladorEnum.PARAM3.getValue();
        trpcod = UsuariospredialsControladorEnum.PARAM0.getValue();
        trpran = UsuariospredialsControladorEnum.PARAM1.getValue();
        ubicacion = GeneralParameterEnum.UBICACION.getName();
        cAccion = UsuariospredialsControladorEnum.PARAM4.getValue();
        codigoPredio = UsuariospredialsControladorEnum.PARAM5.getValue();
        numeroOrden = GeneralParameterEnum.NUMERO_ORDEN.getName();
        nroOrden = UsuariospredialsControladorEnum.PARAM6.getValue();
        retorna = UsuariospredialsControladorEnum.PARAM7.getValue();
        retornaFormulario = UsuariospredialsControladorEnum.PARAM8.getValue();
        clasePredio = UsuariospredialsControladorEnum.PARAM9.getValue();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        direccion = GeneralParameterEnum.DIRECCION.getName();
        direccionPredio = UsuariospredialsControladorEnum.PARAM10.getValue();
        estratoSocioE = UsuariospredialsControladorEnum.PARAM11.getValue();
        indBorrado = UsuariospredialsControladorEnum.PARAM12.getValue();
        mensaje1 = Constantes.MSM_TRANS_INTERRUMPIDA;
        nombre = GeneralParameterEnum.NOMBRE.getName();
        nombrePropietario = UsuariospredialsControladorEnum.PARAM13.getValue();
        numeroProceso = UsuariospredialsControladorEnum.PARAM14.getValue();
        indicadorReserva = UsuariospredialsControladorEnum.PARAM15.getValue();
        campoIndicadorReserva = UsuariospredialsControladorEnum.PARAM16
                        .getValue();
        tb255 = UsuariospredialsControladorEnum.PARAM17
                        .getValue();
        cCodigoEstrato = UsuariospredialsControladorEnum.CODIGO_ESTRATO
                        .getValue();

        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.USUARIOSPREDIALS_CONTROLADOR
                            .getCodigo();
            indicadorClonarPermisos = true;
            validarPermisos();
            // <INI_ADICIONAL>

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid"); // necesaria
                                                                          // para
                                                                          // redireccionar
                                                                          // entre
                                                                          // los
                                                                          // menus
                accion = (String) parametrosEntrada.get(cAccion);
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(UsuariospredialsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        cargarListaUbicacion();
        cargarListaTipo();
        cargarListaClasePredio();
        cargarListatipoMutacion();
        cargarListaTrpcod();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoNit();
        cargarListaCmbPaisNotificacion();
        cargarListaCmbDptoNotificacion();
        cargarListaCmbCiudadNotificacion();
        cargarListaEstratoSocioeconomico();
        cargarListaFormatoEstrato();
        cargarListaPagBan();
        cargarListaPagBan1();
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
        enumBase = GenericUrlEnum.IP_USUARIOS_PREDIAL;
        buscarLlave();
        cargarParametros();
        asignarOrigenDatos();
        if ("600101".equals(SessionUtil.getMenuActual())) {
            tituloForm = idioma.getString("TB_TB3263");
            verBotones = false;
            visibleUsuarios=true;
        }
        else {
            tituloForm = idioma.getString("TB_TB3264");
            permisos[2] = false;
            verBotones = true;
            visibleUsuarios=false;
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
        if (!"600101".equals(SessionUtil.getMenuActual())) {
            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            UsuariospredialsControladorUrlEnum.URL50088
                                                            .getValue());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista ListaTarifaActual
     */
    public void cargarListaTarifaActual() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoActual);
        param.put(UsuariospredialsControladorEnum.PARAM0.getValue(),
                        codTarifaActual);
        param.put(UsuariospredialsControladorEnum.PARAM1.getValue(),
                        rangoTarifaActual);

        try {
            listaTarifaActual = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL398
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
     * Carga la lista ListaTipoNit
     */
    public void cargarListaTipoNit() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoNit = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL23561
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
     * Carga la lista ListaCmbPaisNotificacion
     */
    public void cargarListaCmbPaisNotificacion() {

        try {
            listaCmbPaisNotificacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL23968
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista ListaCmbDptoNotificacion
     */
    public void cargarListaCmbDptoNotificacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(UsuariospredialsControladorEnum.PARAM2.getValue(),
                        pais);

        try {
            listaCmbDptoNotificacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL468
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
     * Carga la lista ListaCmbCiudadNotificacion
     */
    public void cargarListaCmbCiudadNotificacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(UsuariospredialsControladorEnum.PARAM2.getValue(),
                        pais);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dpto);

        try {
            listaCmbCiudadNotificacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL493
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
     * Carga la lista ListaFormatoEstrato
     */
    public void cargarListaFormatoEstrato() {
        try {
            listaFormatoEstrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL25493
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista ListaPagBan
     */
    public void cargarListaPagBan() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            listaPagBan = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL25760
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
     * Carga la lista ListaPagBan1
     */
    public void cargarListaPagBan1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            listaPagBan1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL25760
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
     * Carga la lista ListaUbicacion
     */
    public void cargarListaUbicacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariospredialsControladorUrlEnum.URL26457
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUbicacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     *
     * Carga la lista ListaTipo
     */
    public void cargarListaTipo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariospredialsControladorUrlEnum.URL27067
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     *
     * Carga la lista ListaClasePredio
     */
    public void cargarListaClasePredio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariospredialsControladorUrlEnum.URL27630
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        listaClasePredio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    /**
     *
     * Carga la lista ListatipoMutacion
     */
    public void cargarListatipoMutacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariospredialsControladorUrlEnum.URL28112
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listatipoMutacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     *
     * Carga la lista ListaTrpcod
     */
    public void cargarListaTrpcod() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariospredialsControladorUrlEnum.URL28766
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoActual);

        listaTrpcod = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, trpcod);

    }

    /**
     *
     * Carga la lista ListaEstratoSocioeconomico
     */
    public void cargarListaEstratoSocioeconomico() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsuariospredialsControladorUrlEnum.URL30119
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        listaEstratoSocioeconomico = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoEstrato);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Evento que se ejecuta al cambiar el registro del combo
     * cambiarIndcar del formulario
     */
    public void cambiarIndcar() {
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3504"));

        String indcarAnterior = !(boolean) registro.getCampos().get("INDCAR")
            ? "-1"
            : "0";
        String indcarActual = (boolean) registro.getCampos().get("INDCAR")
            ? "-1"
            : "0";

        agregarRegistroNuevo(false);
        try {
            ejbPredialCero.insertarCambiosEnAuditoria(compania,
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString(),
                            SessionUtil.getUser().getCodigo(), "219",
                            indcarAnterior,
                            indcarActual,
                            idioma.getString("TB_TB3505"),
                            new Date(),
                            new Date(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString());
            ejbPredialCero.insertarCambiosEnAuditoria(compania,
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName())
                                            .toString(),
                            SessionUtil.getUser().getCodigo(), "614",
                            indcarAnterior,
                            indcarActual,
                            idioma.getString("TB_TB3505"),
                            new Date(),
                            new Date(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString());
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Evento que se ejecuta al cambiar el registro del combo
     * cambiarIndexe del formulario
     */
    public void cambiarIndexe() {

        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3504"));

        String indexeAnterior = !(boolean) registro.getCampos().get("INDEXE")
            ? "-1"
            : "0";
        String indexeActual = (boolean) registro.getCampos().get("INDEXE")
            ? "-1"
            : "0";
        agregarRegistroNuevo(false);
        try {
            ejbPredialCero.insertarCambiosEnAuditoria(compania,
                            registro.getCampos().get("CODIGO").toString(),
                            SessionUtil.getUser().getCodigo(), "613",
                            indexeAnterior,
                            indexeActual,
                            idioma.getString("TB_TB3505"),
                            new Date(),
                            new Date(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString());
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Evento que se ejecuta al cambiar el registro del combo
     * cambiarCodigoNoActivo del formulario
     */
    public void cambiarCodigoNoActivo() {
        String codigoNoactivo = "-1";
        if ((boolean) registro.getCampos().get("CODIGO_NO_ACTIVO")) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3506"));
            codigoNoactivo = "0";
        }
        agregarRegistroNuevo(false);
        try {
            ejbPredialCero.insertarCambiosEnAuditoria(compania,
                            registro.getCampos().get("CODIGO").toString(),
                            SessionUtil.getUser().getCodigo(), "215",
                            codigoNoactivo,
                            (boolean) registro.getCampos()
                                            .get("CODIGO_NO_ACTIVO")
                                                ? "-1"
                                                : "0",
                            idioma.getString("TB_TB3505"),
                            new Date(),
                            new Date(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString());
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Evento que se ejecuta al cambiar el registro del combo
     * CmbPaisNotificacion del formulario
     */
    public void cambiarCmbPaisNotificacion() {
        // <CODIGO_DESARROLLADO>
        pais = SysmanFunciones.nvlStr(registro.getCampos()
                        .get("PAIS_NOTIFICACION").toString(), "");
        registro.getCampos().put("DEPARTAMENTO_NOTIFICACION", null);
        registro.getCampos().put("CIUDAD_NOTIFICACION", null);
        cargarListaCmbDptoNotificacion();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evento que se ejecuta al cambiar el registro del combo
     * CmbDptoNotificacion del formulario
     */
    public void cambiarCmbDptoNotificacion() {
        // <CODIGO_DESARROLLADO>
        dpto = (String) registro.getCampos().get("DEPARTAMENTO_NOTIFICACION");
        registro.getCampos().put("CIUDAD_NOTIFICACION", null);
        cargarListaCmbCiudadNotificacion();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al seleccionar la casilla de verificacion
     * chkPlusvalia
     */
    public void cambiarchkPlusvalia() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * FilaUbicacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUbicacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(ubicacion,
                        registroAux.getCampos().get(cCodigo));
        nomUbicacion = registroAux.getCampos().get(nombre).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista FilaTipo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO", registroAux.getCampos().get(cCodigo));
        nomTipoPredio = registroAux.getCampos().get(nombre).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * FilaClasePredio
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClasePredio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(clasePredio,
                        registroAux.getCampos().get(cCodigo));
        nomClasePredio = registroAux.getCampos().get("DESCRIPCION").toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * FilatipoMutacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoMutacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(tipoMutacion,
                        registroAux.getCampos().get(cCodigo));
        nomTipoMutacion = registroAux.getCampos().get(nombre).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista FilaTrpcod
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTrpcod(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(trpcod,
                        registroAux.getCampos().get(trpcod));
        registro.getCampos().put(trpran,
                        registroAux.getCampos().get(trpran));
        descripTarifa = registroAux.getCampos()
                        .get(UsuariospredialsControladorEnum.TRPDES.getValue())
                        .toString();
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3504"));
        agregarRegistroNuevo(false);

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * FilaEstratoSocioeconomico
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEstratoSocioeconomico(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(estratoSocioE,
                        registroAux.getCampos().get(cCodigoEstrato));
        nomEstratoSoc = registroAux.getCampos().get("NOMBRE_ESTRATO")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton AvaluosAnteriores en la
     * vista
     *
     */
    public void oprimirAvaluosAnteriores() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = extraerString(registro.getCampos().get(cCodigo));
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, registro.getCampos().get(numeroOrden));
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        parametros.put(direccionPredio,
                        registro.getCampos().get(direccion));
        if ((boolean) registro.getCampos().get(indBorrado)) {
            parametros.put(indicadorReserva, false);
        }
        else {
            parametros.put(indicadorReserva,
                            registro.getCampos().get(campoIndicadorReserva));
        }
        parametros.put(cAccion, accion);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBAVALUOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Facturados en la vista
     *
     */
    public void oprimirFacturados() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, registro.getCampos().get(numeroOrden));
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        parametros.put(direccionPredio,
                        registro.getCampos().get(direccion));
        if ((boolean) registro.getCampos().get(indBorrado)) {
            parametros.put(indicadorReserva, false);
        }
        else {
            parametros.put(indicadorReserva,
                            registro.getCampos().get(campoIndicadorReserva));
        }
        parametros.put(cAccion, accion);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBCONCEPTOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Calculo en la vista
     *
     */
    public void oprimirCalculo() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        StringBuilder salida = null;
        ByteArrayInputStream streamTexto;
        try {

            String url = armarUrl();

            APICalculoPredial api = new APICalculoPredial();

            salida = api.calcular(compania,
                            SessionUtil.getCompaniaIngreso().getNit(),
                            SysmanFunciones.convertirAFechaCadena(new Date(),
                                            "yyyy-MM-dd'T'HH:mm:ss-0500"),
                            0, false, false,
                            registro.getCampos().get(cCodigo).toString(),
                            registro.getCampos().get(cCodigo).toString(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO_ORDEN
                                                            .getName())
                                            .toString(),
                            SessionUtil.getUser().getCodigo(), "B", url);

            if (!SysmanFunciones.validarVariableVacio(salida.toString())) {

                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB4329"));

                streamTexto = JsfUtil.serializarPlano(salida.toString());

                archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                                "Alertas.txt");

            }
            else {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));

            }
        }
        catch (IOException | SysmanException | ParseException | JRException
                        | com.sysman.util.SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private String armarUrl() throws SysmanException {

        String url = "";

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametros.put(GeneralParameterEnum.CODIGO.getName(), "100");

        Registro rs = new Registro();
        RequestManager requestManager = new RequestManager();
        try {
            rs = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getUrlBeanById(SERVICIO_API)
                                            .getUrl(),
                            parametros));
        }
        catch (NullPointerException | SystemException e) {
            throw new SysmanException(idioma.getString("TB_TB4230"));

        }
        if (rs == null) {
            throw new SysmanException(idioma.getString("TB_TB4232"));
        }
        else if (rs.getCampos().get(GeneralParameterEnum.URL.getName())
                        .toString() == null) {
            throw new SysmanException(idioma.getString("TB_TB4231"));
        }
        url = rs.getCampos().get(GeneralParameterEnum.URL.getName())
                        .toString();
        return url;
    }

    /**
     * Verifica si el numero de orden es correcto
     *
     * @return true, de no ser correto.
     */
    public boolean validarNumeroOrden() {
        // validar si el numero de orden es correcto
        if (!registro.getCampos().get(numeroOrden)
                        .equals(SysmanConstantes.NUMERO_ORDEN_PREDIAL)) {
            String[] cadena = { idioma.getString(mensaje1), " ",
                                idioma.getString("TB_TB840")

            };
            JsfUtil.agregarMensajeAlerta(
                            SysmanFunciones.concatenar(cadena));
            return true;
        }

        return false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Bancos en la vista
     *
     */
    public void oprimirBancos() {
        archivoDescarga = null;

        if (validarNumeroOrden() || !tieneDeuda()) {
            return;
        }

        // validar permisos de facturacion

        if (!generaFacturacion || !generaFacturacionD) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB396"));
            return;
        }

        if ("SI".equals(permiteCalculoAutom)) {
            oprimirCalculo();
        }

        // aca se valida la tarifa teniendo en cuenta un metodo o
        // funciï¿½n, se toma el campo trpcod para validaciï¿½n
        // temporal
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), trpcod)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB847"));
            return;
        }

        validarProcesoFacturacion();

        // </CODIGO_DESARROLLADO>
    }

    public void validarProcesoFacturacion() {
        // validaciï¿½n cuando se ha iniciado proceso de facturaciï¿½n
        // en
        // lotes.
        if ("SI".equals(inicioFacturacion)) {
            indVerDgEstado = true;
        }
        else {
            indVerDgEstado = false;
            aceptardgEstadoFact();
        }
        // validar calculo automï¿½tico
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton aceptar del dialogo
     * dgEstadoFact
     *
     */
    public void aceptardgEstadoFact() {
        int indOtros;
        // validaciï¿½n de visibilidad de conceptos 19 y 20
        if ("NO".equals(verCptos)) {
            if (SessionUtil.getCompaniaIngreso().getNombre()
                            .equals(idioma.getString("TB_TB2766"))) {
                indOtros = 1; // si es Sogamoso en la columna de otros
                              // visualiza la suma de los conceptos
                              // 15,16,17,20
            }
            else {
                indOtros = 2; // Si el parï¿½metro es No en la columna
                              // de otros visualiza la suma de los
                              // conceptos 15,16,17,18
            }
        }
        else {
            indOtros = 3; // cuando el parï¿½metro se encuentra en SI,
                          // en el formulario de valores facturados se
                          // visualizar en una caja de texto la suma
                          // de los conceptos del 15 al 20

        }
        if ("SI".equals(permiteCalculoAutom)) {
            oprimirCalculo();
        }
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        parametros.put("idPropietario",
                        registro.getCampos().get("NIT").toString());
        parametros.put(nombrePropietario,
                        registro.getCampos().get(nombre).toString());
        parametros.put("pagoAno",
                        registro.getCampos().get("PAGO_ANO").toString());
        parametros.put("indOtros", indOtros);
        parametros.put("avaluoAno",
                        registro.getCampos().get("AVALUO_ANO").toString());
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.RECIBOPREFUSAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * este metodo verifica si el consecutivo real de la tabla
     * IP_NUMEROSDEFACTURA es de tipo normal y por lo menos un
     * registro se encuentra en -1 true
     *
     * @return
     */
    private boolean validarConsecutivoReal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        Registro reg;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL322
                                                                            .getValue())
                                            .getUrl(), param));
            if ((reg == null) || (Integer
                            .parseInt(reg.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName())
                                            .toString()) == 0)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3299"));
                return true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton caja en la vista
     *
     */
    public void oprimircaja() {
        archivoDescarga = null;
        if (validarConsecutivoReal()) {
            return;
        }
        if (!registro.getCampos().get(numeroOrden)
                        .equals(SysmanConstantes.NUMERO_ORDEN_PREDIAL)) {
            String[] cadena = { idioma.getString(mensaje1), " ",
                                idioma.getString("TB_TB840")

            };
            JsfUtil.agregarMensajeAlerta(
                            SysmanFunciones.concatenar(cadena));
            return;
        }
        if ("SI".equals(permiteCalculoAutom)) {
            oprimirCalculo();
        }
        if (!tieneDeuda()) {
            return;
        }

        if ("SI".equals(permiteCalculoAutom)) {
            oprimirCalculo();
        }

        String[] campos = { codigoPredio, nroOrden, nombrePropietario,
                            "anoPago", "avaluo" };
        String[] valores = { registro.getCampos().get(cCodigo).toString(),
                             registro.getCampos().get(numeroOrden).toString(),
                             registro.getCampos().get(nombre).toString(),
                             registro.getCampos().get("PAGO_ANO").toString(),
                             registro.getCampos().get("AVALUO_ANO")
                                             .toString() };

        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PREDIAL_REC_CAJAS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Pagos en la vista
     *
     */
    public void oprimirPagos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        parametros.put(direccionPredio,
                        registro.getCampos().get(direccion));
        parametros.put("codigoPadre", registro.getCampos().get("CODIGOANT1"));
        parametros.put(cAccion, accion);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.UPAGOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Resoluciones en la vista
     *
     */
    public void oprimirResoluciones() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        parametros.put(direccionPredio,
                        registro.getCampos().get(direccion));
        parametros.put("paisPredio",
                        registro.getCampos().get("PAIS").toString());
        parametros.put("dptoPredio", registro.getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO.getName())
                        .toString());
        parametros.put("ciudadPredio", registro.getCampos()
                        .get(GeneralParameterEnum.MUNICIPIO.getName())
                        .toString());
        parametros.put(cAccion, accion);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.URESOLUCIONES_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Financiar en la vista
     *
     */
    public void oprimirFinanciar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!tieneDeuda()) {
            return;
        }

        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CREAR_ACUERDOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Propietarios en la vista
     *
     */
    public void oprimirverPropietarios() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = extraerString(registro.getCampos().get(cCodigo));
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, registro.getCampos().get(numeroOrden));
        parametros.put(nombrePropietario, SysmanFunciones
                        .nvl(registro.getCampos().get(nombre), ""));
        parametros.put(direccionPredio,
                        SysmanFunciones.nvl(registro.getCampos().get(direccion),
                                        ""));
        parametros.put("estadoPredio",
                        (boolean) registro.getCampos().get(indBorrado)
                            ? "BORRADO"
                            : "");
        parametros.put(cAccion, accion);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PROPIETARIOSPORPREDIOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Facturas en la vista
     *
     */
    public void oprimirFacturas() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        parametros.put(direccionPredio,
                        SysmanFunciones.nvl(registro.getCampos().get(direccion),
                                        ""));
        parametros.put("ultimaFactura",
                        registro.getCampos().get("NUMERO_FACTURA").toString());
        if ((boolean) registro.getCampos().get(indBorrado)) {
            parametros.put(indicadorReserva, false);
        }
        else {
            parametros.put(indicadorReserva,
                            registro.getCampos().get(campoIndicadorReserva));
        }
        parametros.put(cAccion, accion);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FACTURAS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdEstadoCuenta en la
     * vista
     *
     */
    public void oprimirCmdEstadoCuenta() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (generaFacturacion || generaFacturacionD) {
            generarEstadoCta(FORMATOS.PDF);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdPagosAAnt en la vista
     *
     */
    public void oprimirCmdPagosAAnt() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarRptePagosAnosAnt(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdInfCatastral en la
     * vista
     *
     */
    public void oprimirCmdInfCatastral() {
        try {
            // <CODIGO_DESARROLLADO>
            archivoDescarga = null;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cCodigo));

            List<Registro> infCatastral;

            infCatastral = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL26108
                                                                            .getValue())
                                            .getUrl(), param));

            if (!infCatastral.isEmpty()) {
                int cant = Integer.parseInt((infCatastral.get(0)
                                .getCampos().get("CANT")).toString());
                if (cant == 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB892"));
                    return;
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB892"));
                return;
            }

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("rid", css);
            String codPredio = (String) registro.getCampos().get(cCodigo);
            parametros.put(codigoPredio, codPredio);
            parametros.put(nroOrden, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            Direccionador direccionador = new Direccionador();

            direccionador.setParametros(parametros);
            SessionUtil.setSessionVar(retornaFormulario, retorna);

            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.INFORMACIONCATASTRALS_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton ReciboPorPropietario en la
     * vista
     *
     */
    public void oprimirReciboPorPropietario() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (generaFacturacion) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3265"));
        }
        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            UsuariospredialsControladorUrlEnum.URL1261
                                                            .getValue());
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("rid", css);
            String codPredio = (String) registro.getCampos().get(cCodigo);
            parametros.put(codigoPredio, codPredio);
            parametros.put("nitPropietario",
                            registro.getCampos().get("NIT").toString());
            parametros.put(nombrePropietario,
                            registro.getCampos().get(nombre).toString());
            Direccionador direccionador = new Direccionador();

            direccionador.setParametros(parametros);
            SessionUtil.setSessionVar(retornaFormulario, retorna);

            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PRE_RECIBO_PROPS_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdNotif en la vista
     *
     */
    public void oprimirCmdNotif() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        String codPredio = (String) registro.getCampos().get(cCodigo);

        String[] campos = { "codigoPredio" };
        Object[] valores = { codPredio };

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMNOTIUSUS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdAcuerdos en la vista
     *
     */
    public void oprimircmdAcuerdos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        String numOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        parametros.put(codigoPredio, codPredio);
        parametros.put("numOrden", numOrden);
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ACUERDOSUSUARIOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdAbonos en la vista
     *
     */
    public void oprimircmdAbonos() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();

        parametros.put("rid", css);
        String codPredio = (String) registro.getCampos().get(cCodigo);
        parametros.put(codigoPredio, codPredio);
        parametros.put(nroOrden, SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        parametros.put(nombrePropietario, registro.getCampos().get(nombre));
        parametros.put(direccionPredio,
                        registro.getCampos().get(direccion));
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar(retornaFormulario, retorna);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBFRMABONOSUSUARIOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public boolean tieneDeuda() {
        // Validar si el predio a consultar tiene vigencias pendientes
        // de pago
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cCodigo));
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);

            List<Registro> vigPendientes;

            vigPendientes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL1385
                                                                            .getValue())
                                            .getUrl(), param));

            if (!vigPendientes.isEmpty()) {
                int cant = Integer.parseInt((vigPendientes.get(0)
                                .getCampos().get("ANOSFACT")).toString());
                if (cant == 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB211"));
                    return false;
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB211"));
                return false;
            }

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());

            logger.error(e.getMessage(), e);

        }
        return true;
    }

    public void generarRptePagosAnosAnt(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;

        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(codigoPredio, registro.getCampos().get(cCodigo));
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            parametros.put("PR_NOMBRE_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            String[] cadena = { "NIT ",
                                SessionUtil.getCompaniaIngreso().getNit()

            };
            parametros.put("PR_NIT_COMPANIA",
                            SysmanFunciones.concatenar(cadena));
            parametros.put("PR_NOMPROPIETARIO",
                            registro.getCampos().get(nombre));
            parametros.put("PR_DIRPREDIO",
                            registro.getCampos().get(direccion));
            Reporteador.resuelveConsulta("000883INFHISTORICOPAGOANOSANT",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000883INFHISTORICOPAGOANOSANT", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generarEstadoCta(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(codigoPredio, registro.getCampos().get(cCodigo));
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            parametros.put("PR_NOMBRE_COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            String[] cadena = { "NIT ",
                                SessionUtil.getCompaniaIngreso().getNit()

            };
            parametros.put("PR_NIT_COMPANIA",
                            SysmanFunciones.concatenar(cadena));

            String[] cad = { "ESTADO DE CUENTA A FECHA  ", SysmanFunciones
                            .convertirAFechaCadena(new Date())

            };
            parametros.put("PR_TITULO_REPORTE",
                            SysmanFunciones.concatenar(cad));
            Reporteador.resuelveConsulta("000891ESTADOCUENTASTD",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("000891ESTADOCUENTASTD",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarParametros() {
        try {
            verCptos = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "VISUALIZAR CONCEPTOS 19 Y 20 EN LIQUIDACION",
                                            modulo, new Date(), true), "NO");

            manOtroFactor = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE OTRO FACTOR", modulo,
                                            new Date(), true), "NO");

            leyDescuentos = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "TITULO OPCION DESCUENTOS ESPECIALES",
                                            modulo, new Date(), true),
                                            "Parametro - TITULO OPCION DESCUENTOS ESPECIALES");

            activarBtBancos = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "ACTIVAR BOTON DE PREPAGO EN BANCOS",
                                            modulo, new Date(), true), "NO");

            inicioFacturacion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "INICIO FACTURACION", modulo,
                                            new Date(), true), "NO");

            generaFacturacion = ejbPredialCero
                            .consultarNombreUsuarioEnParametro(compania,
                                            Integer.parseInt(modulo),
                                            "USUARIOS MANEJO FACTURACION",
                                            SessionUtil.getUser().getCodigo());

            generaFacturacionD = ejbPredialCero
                            .consultarNombreUsuarioEnParametro(compania,
                                            Integer.parseInt(modulo),
                                            "USUARIOS MANEJO FACTURACION 2",
                                            SessionUtil.getUser().getCodigo());

            activarBtCaja = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "ACTIVAR BOTON DE PAGO EN CAJA",
                                            modulo, new Date(),
                                            true), "NO");

            manejaFinanciables = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA SISTEMA DE FINANCIABLES",
                                            modulo, new Date(), true), "NO");

            usuarioManejaAcuerdos = ejbPredialCero
                            .consultarNombreUsuarioEnParametro(compania,
                                            Integer.parseInt(modulo),
                                            "USUARIOS MANEJO ACUERDOS",
                                            SessionUtil.getUser().getCodigo());

            permiteCalculoAutom = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CALCULO AUTOMATICO", modulo,
                                            new Date(), true), "NO");

            SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MENSAJE1 CALCULO AUTOMATICO",
                                            modulo, new Date(), true), "");

            manejaIndDamnificado = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "INDICA SI ES DAMNIFICADO O NO",
                                            modulo, new Date(), true), "");

            usuarioManejaEstCuenta = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "ESTADO DE CUENTA USUARIOS PREDIAL",
                                            modulo, new Date(), true), "NO");

            admonBloqPredios = ejbPredialCero.consultarNombreUsuarioEnParametro(
                            compania, Integer.parseInt(modulo),
                            "ADMINISTRADOR / BLOQUEAR PREDIOS",
                            SessionUtil.getUser().getCodigo());

            manejaInfCatastral = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            idioma.getString("TB_TB3266"),
                                            modulo, new Date(), true), "NO");

            manejaRecPropietario = ejbSysmanUtil.consultarParametro(compania,
                            "HABILITA RECIBO POR PROPIETARIO", modulo,
                            new Date(), true);

            manejaRecPropietario = manejaRecPropietario == null ? "NO"
                : manejaRecPropietario;

            ejbPredialCero.consultarNombreUsuarioEnParametro(compania,
                            Integer.parseInt(modulo),
                            "USUARIOS QUE MODIFICAN FORMATO CERTIFICADO CATASTRAL",
                            SessionUtil.getUser().getCodigo());

            calcularLey10032015 = ejbSysmanUtil.consultarParametro(compania,
                            "CALCULO LEY 10/03/2015", modulo, new Date(), true);

            calcularLey10032015 = calcularLey10032015 == null ? "NO"
                : calcularLey10032015;

            codCatastral30 = ejbSysmanUtil.consultarParametro(compania,
                            "ACTUALIZO CEDULAS CATASTRALES A 30 DIGITOS",
                            modulo, new Date(), true);

            codCatastral30 = codCatastral30 == null ? "NO" : codCatastral30;

            nivelAdmonExento = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL ADMINISTRADOR EXCENTO Y CAR", modulo,
                            new Date(), true);

            nivelAdmonExento = nivelAdmonExento == null ? "99"
                : nivelAdmonExento;

        }
        catch (SystemException e) {
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void mostrarMensajePredio() {
        // mensaje para predios que tiene activo el indicador de
        // proceso de cobro, controlado por el campo PROCESO_DE_COBRO
        String[] cadena = { idioma.getString("TB_TB213"),
                            registro.getCampos().get(numeroProceso) == null
                                ? " "
                                : registro.getCampos()
                                                .get(numeroProceso).toString()

        };
        labCobro = SysmanFunciones.concatenar(cadena);
    }

    /**
     * Determina si el predio tiene pagos dobles.
     */
    public void validarPagosDobles() {
        // Validar si el predio tiene pagos dobles activo para
        // visualizar el mensaje correspondiente
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cCodigo));
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            registro.getCampos().get(numeroOrden));

            List<Registro> auxPagDbl;

            auxPagDbl = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL1705
                                                                            .getValue())
                                            .getUrl(), param));

            if (!auxPagDbl.isEmpty()) {
                tienePagosDobles = true;
                String[] cadena = { idioma.getString("TB_TB214"), auxPagDbl
                                .get(0).getCampos().get("PREANO").toString()

                };
                alertaPagoDoble = SysmanFunciones.concatenar(cadena);
            }
            else {
                tienePagosDobles = false;
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Determina si el predio tiene descuentos especiales
     */
    public void validarDescuentosEspeciales() {
        // Validar si el predio tiene descuentos especiales
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cCodigo));
            param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            registro.getCampos().get(numeroOrden));

            List<Registro> auxLey1607;

            auxLey1607 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL1750
                                                                            .getValue())
                                            .getUrl(), param));

            if (!auxLey1607.isEmpty()) {
                String[] cadena = { idioma.getString("TB_TB215"), " ",
                                    leyDescuentos

                };
                alerta1607 = SysmanFunciones.concatenar(cadena);
            }
            else {
                alerta1607 = "";
            }

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Determina que valor debe tener la variable acuerdo.
     */
    public void asignarAcuerdo() {
        // asignar valor de la variable acuerdo
        String[] cadena = { idioma.getString("TB_TB217"), registro.getCampos()
                        .get("OBSERVACIONES_ACUERDO") == null
                            ? ""
                            : registro.getCampos()
                                            .get("OBSERVACIONES_ACUERDO")
                                            .toString()

        };
        acuerdo = SysmanFunciones.concatenar(cadena);
    }

    /**
     * Determina si el predio tiene procesos judiciales.
     */
    public void asignarProcesoJudicial() {
        // asignar valor para predios con procesos judiciales
        String[] cadena = { idioma.getString("TB_TB223"), registro
                        .getCampos().get("ETAPA_PROCESOJUD") == null
                            ? ""
                            : registro.getCampos()
                                            .get("ETAPA_PROCESOJUD").toString(),
                            " No. ", registro.getCampos()
                                            .get(numeroProceso) == null
                                                ? ""
                                                : registro.getCampos()
                                                                .get(numeroProceso)
                                                                .toString()

        };
        procesoJudicial = SysmanFunciones.concatenar(cadena);
    }

    public String asignarValorCampo(Registro reg, String campo) {
        if (reg != null) {
            return reg.getCampos().get(campo).toString();
        }
        return idioma.getString(tb255);
    }

    /**
     * Asigna a las variables del bean los valores de los registros
     * seleccionados en cada combo respectivamente
     */
    public void obtenerValoresCombos() {
        // buscar nombres de textos asociados a combos
        try {
            if (registro.getCampos().get(ubicacion) != null) {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(ubicacion));

                Registro regCombo;

                regCombo = listaUbicacion.getRegistroUnico(fields);

                nomUbicacion = asignarValorCampo(regCombo, nombre);
            }

            if (registro.getCampos().get("TIPO") != null) {

                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get("TIPO"));

                Registro regCombo = listaTipo.getRegistroUnico(fields);

                nomTipoPredio = asignarValorCampo(regCombo,
                                nombre);

            }

            if (registro.getCampos().get(clasePredio) != null) {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(clasePredio));

                Registro regCombo = listaClasePredio.getRegistroUnico(fields);

                nomClasePredio = asignarValorCampo(regCombo, "DESCRIPCION");

            }

            if (registro.getCampos().get(estratoSocioE) != null) {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(cCodigoEstrato,
                                registro.getCampos().get(estratoSocioE));

                Registro regCombo = listaEstratoSocioeconomico
                                .getRegistroUnico(fields);

                nomEstratoSoc = asignarValorCampo(regCombo, "NOMBRE_ESTRATO");
            }

            if (registro.getCampos().get(tipoMutacion) != null) {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(tipoMutacion));

                Registro regCombo = listatipoMutacion.getRegistroUnico(fields);

                nomTipoMutacion = asignarValorCampo(regCombo, nombre);
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Extrae la cadena que representa al objeto, solo si es diferente
     * de nulo.
     *
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object) {
        return object != null ? object.toString() : null;
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        verOtroFactor = false;

        precargarRegistro();
        anoActual = String.valueOf(SysmanFunciones.ano(new Date()));
        descripTarifa = SysmanFunciones.nvlStr(
                        (String) registro.getCampos()
                                        .get(UsuariospredialsControladorEnum.TRPDES
                                                        .getValue()),
                        "");

        // El estado de predio serï¿½ visible dependiendo del valor
        // del
        // campo INDBORRADO
        estadoPredio = idioma.getString("TB_TB226");

        mostrarMensajePredio();

        // formatear cï¿½dula catatral
        String codigo = extraerString(registro.getCampos().get(cCodigo));

        tipoP = codigo.substring(0, 2);
        sector = codigo.substring(2, 4);
        manzana = codigo.substring(4, 8);
        predio = codigo.substring(8, 12);
        mejora = codigo.substring(12, 15);

        validarPagosDobles();

        validarDescuentosEspeciales();

        asignarAcuerdo();

        asignarProcesoJudicial();

        obtenerValoresCombos();

        // actualizar tarifa

        cargarListaTrpcod();
        codTarifaActual = (String) registro.getCampos().get(trpcod);
        rangoTarifaActual = (String) registro.getCampos().get(trpran);
        cargarListaTarifaActual();

        // validar controles que dependen de parï¿½metro NOMBRE OTRO
        // FACTOR
        if (manOtroFactor.length() > 5) {
            verOtroFactor = true;
            tituloParametro = manOtroFactor;
            tituloParametro = tituloParametro.toLowerCase();
        }

        validarVisibilidad();

        // valida si el predio esta inactivo se oculta el boton de
        // consulta de acuerdos
        indVerBtConsAcuerdos = (boolean) registro.getCampos().get(indBorrado)
            ? false
            : indVerBtConsAcuerdos;
        indVerCodEquiv = "SI".equals(codCatastral30) ? true : false;
        indVerLey10032015 = "SI".equals(calcularLey10032015) ? true : false;

        bloqExento = SessionUtil.getNivelUsuario(modulo) == Integer
                        .parseInt(nivelAdmonExento) ? false : true;

        determinarCantcertificados();

    }

    /**
     * Determina la cantidad de certificados que tiene un predio
     */
    public void determinarCantcertificados() {
        // Consultar la cantidad de certificados de paz y salvos que
        // tiene un predio y actualizar el campo correspondiente
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(cCodigo));
            param.put(GeneralParameterEnum.ANO.getName(), anoActual);

            List<Registro> auxCantPZ;

            auxCantPZ = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UsuariospredialsControladorUrlEnum.URL1978
                                                                            .getValue())
                                            .getUrl(), param));

            if (!auxCantPZ.isEmpty()) {
                cantPZ = auxCantPZ.get(0).getCampos().get("CANT").toString();
            }
            else {
                cantPZ = "0";
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Determina cuales controles se ocultan o hacen visibles.
     */
    public void validarVisibilidad() {
        // validar visibilidad de botones
        indVerBtBancos = "SI".equals(activarBtBancos) ? true : false;
        indVerBtCaja = "SI".equals(activarBtCaja) ? true : false;
        indVerBtFinanciar = "SI".equals(manejaFinanciables) ? true : false;
        indVerBtEstCuenta = "SI".equals(usuarioManejaEstCuenta) ? true : false;
        indVerBtActivar = admonBloqPredios ? true : false;
        indVerBtInfCatastral = "SI".equals(manejaInfCatastral) ? true : false;
        indVerBtRecProp = "SI".equals(manejaRecPropietario) ? true : false;
        indVerDaminificado = "SI".equals(manejaIndDamnificado) ? true : false;
        indVerBtConsAcuerdos = usuarioManejaAcuerdos ? true : false;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get("OTROSDESC") == null) {
            registro.getCampos().put("OTROSDESC", "0");
        }

        if ("m".equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.NUMERO_ORDEN.getName());
            registro.getCampos().remove(
                            UsuariospredialsControladorEnum.TRPDES.getValue());

        }
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
    public boolean actualizarDespues() {
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
    public boolean eliminarAntes() {
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
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable verBotones
     *
     * @return verBotones
     */
    public boolean isVerBotones() {
        return verBotones;
    }

    /**
     * Asigna la variable verBotones
     *
     * @param verBotones
     * Variable a asignar en verBotones
     */
    public void setVerBotones(boolean verBotones) {
        this.verBotones = verBotones;
    }

    /**
     * Retorna la variable alertaPagoDoble
     *
     * @return alertaPagoDoble
     */
    public String getAlertaPagoDoble() {
        return alertaPagoDoble;
    }

    /**
     * Asigna la variable alertaPagoDoble
     *
     * @param alertaPagoDoble
     * Variable a asignar en alertaPagoDoble
     */
    public void setAlertaPagoDoble(String alertaPagoDoble) {
        this.alertaPagoDoble = alertaPagoDoble;
    }

    /**
     * Retorna la variable labCobro
     *
     * @return labCobro
     */
    public String getLabCobro() {
        return labCobro;
    }

    /**
     * Asigna la variable labCobro
     *
     * @param labCobro
     * Variable a asignar en labCobro
     */
    public void setLabCobro(String labCobro) {
        this.labCobro = labCobro;
    }

    /**
     * Retorna la variable alerta1607
     *
     * @return alerta1607
     */
    public String getAlerta1607() {
        return alerta1607;
    }

    /**
     * Asigna la variable alerta1607
     *
     * @param alerta1607
     * Variable a asignar en alerta1607
     */
    public void setAlerta1607(String alerta1607) {
        this.alerta1607 = alerta1607;
    }

    /**
     * Retorna la variable acuerdo
     *
     * @return acuerdo
     */
    public String getAcuerdo() {
        return acuerdo;
    }

    /**
     * Asigna la variable acuerdo
     *
     * @param acuerdo
     * Variable a asignar en acuerdo
     */
    public void setAcuerdo(String acuerdo) {
        this.acuerdo = acuerdo;
    }

    /**
     * Retorna la variable procesoJudicial
     *
     * @return procesoJudicial
     */
    public String getProcesoJudicial() {
        return procesoJudicial;
    }

    /**
     * Asigna la variable procesoJudicial
     *
     * @param procesoJudicial
     * Variable a asignar en procesoJudicial
     */
    public void setProcesoJudicial(String procesoJudicial) {
        this.procesoJudicial = procesoJudicial;
    }

    /**
     * Retorna la variable sector
     *
     * @return sector
     */
    public String getSector() {
        return sector;
    }

    /**
     * Asigna la variable sector
     *
     * @param sector
     * Variable a asignar en sector
     */
    public void setSector(String sector) {
        this.sector = sector;
    }

    /**
     * Retorna la variable manzana
     *
     * @return manzana
     */
    public String getManzana() {
        return manzana;
    }

    /**
     * Asigna la variable manzana
     *
     * @param manzana
     * Variable a asignar en manzana
     */
    public void setManzana(String manzana) {
        this.manzana = manzana;
    }

    /**
     * Retorna la variable predio
     *
     * @return predio
     */
    public String getPredio() {
        return predio;
    }

    /**
     * Asigna la variable predio
     *
     * @param predio
     * Variable a asignar en predio
     */
    public void setPredio(String predio) {
        this.predio = predio;
    }

    /**
     * Retorna la variable mejora
     *
     * @return mejora
     */
    public String getMejora() {
        return mejora;
    }

    /**
     * Asigna la variable mejora
     *
     * @param mejora
     * Variable a asignar en mejora
     */
    public void setMejora(String mejora) {
        this.mejora = mejora;
    }

    /**
     * Retorna la variable tipoP
     *
     * @return tipoP
     */
    public String getTipoP() {
        return tipoP;
    }

    /**
     * Asigna la variable tipoP
     *
     * @param tipoP
     * Variable a asignar en tipoP
     */
    public void setTipoP(String tipoP) {
        this.tipoP = tipoP;
    }

    /**
     * Retorna la variable porcReserva
     *
     * @return porcReserva
     */
    public String getPorcReserva() {
        return porcReserva;
    }

    /**
     * Asigna la variable porcReserva
     *
     * @param porcReserva
     * Variable a asignar en porcReserva
     */
    public void setPorcReserva(String porcReserva) {
        this.porcReserva = porcReserva;
    }

    /**
     * Retorna la variable nomUbicacion
     *
     * @return nomUbicacion
     */
    public String getNomUbicacion() {
        return nomUbicacion;
    }

    /**
     * Asigna la variable nomUbicacion
     *
     * @param nomUbicacion
     * Variable a asignar en nomUbicacion
     */
    public void setNomUbicacion(String nomUbicacion) {
        this.nomUbicacion = nomUbicacion;
    }

    /**
     * Retorna la variable nomTipoPredio
     *
     * @return nomTipoPredio
     */
    public String getNomTipoPredio() {
        return nomTipoPredio;
    }

    /**
     * Asigna la variable nomTipoPredio
     *
     * @param nomTipoPredio
     * Variable a asignar en nomTipoPredio
     */
    public void setNomTipoPredio(String nomTipoPredio) {
        this.nomTipoPredio = nomTipoPredio;
    }

    /**
     * Retorna la variable nomClasePredio
     *
     * @return nomClasePredio
     */
    public String getNomClasePredio() {
        return nomClasePredio;
    }

    /**
     * Asigna la variable nomClasePredio
     *
     * @param nomClasePredio
     * Variable a asignar en nomClasePredio
     */
    public void setNomClasePredio(String nomClasePredio) {
        this.nomClasePredio = nomClasePredio;
    }

    /**
     * Retorna la variable estadoPredio
     *
     * @return estadoPredio
     */
    public String getEstadoPredio() {
        return estadoPredio;
    }

    /**
     * Asigna la variable estadoPredio
     *
     * @param estadoPredio
     * Variable a asignar en estadoPredio
     */
    public void setEstadoPredio(String estadoPredio) {
        this.estadoPredio = estadoPredio;
    }

    /**
     * Retorna la variable nomTipoMutacion
     *
     * @return nomTipoMutacion
     */
    public String getNomTipoMutacion() {
        return nomTipoMutacion;
    }

    /**
     * Asigna la variable nomTipoMutacion
     *
     * @param nomTipoMutacion
     * Variable a asignar en nomTipoMutacion
     */
    public void setNomTipoMutacion(String nomTipoMutacion) {
        this.nomTipoMutacion = nomTipoMutacion;
    }

    /**
     * Retorna la variable tituloParametro
     *
     * @return tituloParametro
     */
    public String getTituloParametro() {
        return tituloParametro;
    }

    /**
     * Asigna la variable tituloParametro
     *
     * @param tituloParametro
     * Variable a asignar en tituloParametro
     */
    public void setTituloParametro(String tituloParametro) {
        this.tituloParametro = tituloParametro;
    }

    /**
     * Retorna la variable descripTarifa
     *
     * @return descripTarifa
     */
    public String getDescripTarifa() {
        return descripTarifa;
    }

    /**
     * Asigna la variable descripTarifa
     *
     * @param descripTarifa
     * Variable a asignar en descripTarifa
     */
    public void setDescripTarifa(String descripTarifa) {
        this.descripTarifa = descripTarifa;
    }

    /**
     * Retorna la variable nomEstratoSoc
     *
     * @return nomEstratoSoc
     */
    public String getNomEstratoSoc() {
        return nomEstratoSoc;
    }

    /**
     * Asigna la variable nomEstratoSoc
     *
     * @param nomEstratoSoc
     * Variable a asignar en nomEstratoSoc
     */
    public void setNomEstratoSoc(String nomEstratoSoc) {
        this.nomEstratoSoc = nomEstratoSoc;
    }

    /**
     * Retorna la variable archivoDescarga
     *
     * @return archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable indVerBtBancos
     *
     * @return indVerBtBancos
     */
    public boolean isIndVerBtBancos() {
        return indVerBtBancos;
    }

    /**
     * Asigna la variable indVerBtBancos
     *
     * @param indVerBtBancos
     * Variable a asignar en indVerBtBancos
     */
    public void setIndVerBtBancos(boolean indVerBtBancos) {
        this.indVerBtBancos = indVerBtBancos;
    }

    /**
     * Retorna la variable indVerBtCaja
     *
     * @return indVerBtCaja
     */
    public boolean isIndVerBtCaja() {
        return indVerBtCaja;
    }

    /**
     * Asigna la variable indVerBtCaja
     *
     * @param indVerBtCaja
     * Variable a asignar en indVerBtCaja
     */
    public void setIndVerBtCaja(boolean indVerBtCaja) {
        this.indVerBtCaja = indVerBtCaja;
    }

    /**
     * Retorna la variable indVerBtFinanciar
     *
     * @return indVerBtFinanciar
     */
    public boolean isIndVerBtFinanciar() {
        return indVerBtFinanciar;
    }

    /**
     * Asigna la variable indVerBtFinanciar
     *
     * @param indVerBtFinanciar
     * Variable a asignar en indVerBtFinanciar
     */
    public void setIndVerBtFinanciar(boolean indVerBtFinanciar) {
        this.indVerBtFinanciar = indVerBtFinanciar;
    }

    /**
     * Retorna la variable indVerBtConsAcuerdos
     *
     * @return indVerBtConsAcuerdos
     */
    public boolean isIndVerBtConsAcuerdos() {
        return indVerBtConsAcuerdos;
    }

    /**
     * Asigna la variable indVerBtConsAcuerdos
     *
     * @param indVerBtConsAcuerdos
     * Variable a asignar en indVerBtConsAcuerdos
     */
    public void setIndVerBtConsAcuerdos(boolean indVerBtConsAcuerdos) {
        this.indVerBtConsAcuerdos = indVerBtConsAcuerdos;
    }

    /**
     * Retorna la variable indVerBtEstCuenta
     *
     * @return indVerBtEstCuenta
     */
    public boolean isIndVerBtEstCuenta() {
        return indVerBtEstCuenta;
    }

    /**
     * Asigna la variable indVerBtEstCuenta
     *
     * @param indVerBtEstCuenta
     * Variable a asignar en indVerBtEstCuenta
     */
    public void setIndVerBtEstCuenta(boolean indVerBtEstCuenta) {
        this.indVerBtEstCuenta = indVerBtEstCuenta;
    }

    /**
     * Retorna la variable indVerBtActivar
     *
     * @return indVerBtActivar
     */
    public boolean isIndVerBtActivar() {
        return indVerBtActivar;
    }

    /**
     * Asigna la variable indVerBtActivar
     *
     * @param indVerBtActivar
     * Variable a asignar en indVerBtActivar
     */
    public void setIndVerBtActivar(boolean indVerBtActivar) {
        this.indVerBtActivar = indVerBtActivar;
    }

    /**
     * Retorna la variable indVerBtInfCatastral
     *
     * @return indVerBtInfCatastral
     */
    public boolean isIndVerBtInfCatastral() {
        return indVerBtInfCatastral;
    }

    /**
     * Asigna la variable indVerBtInfCatastral
     *
     * @param indVerBtInfCatastral
     * Variable a asignar en indVerBtInfCatastral
     */
    public void setIndVerBtInfCatastral(boolean indVerBtInfCatastral) {
        this.indVerBtInfCatastral = indVerBtInfCatastral;
    }

    /**
     * Retorna la variable indVerBtRecProp
     *
     * @return indVerBtRecProp
     */
    public boolean isIndVerBtRecProp() {
        return indVerBtRecProp;
    }

    /**
     * Asigna la variable indVerBtRecProp
     *
     * @param indVerBtRecProp
     * Variable a asignar en indVerBtRecProp
     */
    public void setIndVerBtRecProp(boolean indVerBtRecProp) {
        this.indVerBtRecProp = indVerBtRecProp;
    }

    /**
     * Retorna la variable indVerDaminificado
     *
     * @return indVerDaminificado
     */
    public boolean isIndVerDaminificado() {
        return indVerDaminificado;
    }

    /**
     * Asigna la variable indVerDaminificado
     *
     * @param indVerDaminificado
     * Variable a asignar en indVerDaminificado
     */
    public void setIndVerDaminificado(boolean indVerDaminificado) {
        this.indVerDaminificado = indVerDaminificado;
    }

    /**
     * Retorna la variable cantPZ
     *
     * @return cantPZ
     */
    public String getCantPZ() {
        return cantPZ;
    }

    /**
     * Asigna la variable cantPZ
     *
     * @param cantPZ
     * Variable a asignar en cantPZ
     */
    public void setCantPZ(String cantPZ) {
        this.cantPZ = cantPZ;
    }

    /**
     * Retorna la variable indVerCodEquiv
     *
     * @return indVerCodEquiv
     */
    public boolean isIndVerCodEquiv() {
        return indVerCodEquiv;
    }

    /**
     * Asigna la variable indVerCodEquiv
     *
     * @param indVerCodEquiv
     * Variable a asignar en indVerCodEquiv
     */
    public void setIndVerCodEquiv(boolean indVerCodEquiv) {
        this.indVerCodEquiv = indVerCodEquiv;
    }

    /**
     * Retorna la variable indVerLey10032015
     *
     * @return indVerLey10032015
     */
    public boolean isIndVerLey10032015() {
        return indVerLey10032015;
    }

    /**
     * Asigna la variable indVerLey10032015
     *
     * @param indVerLey10032015
     * Variable a asignar en indVerLey10032015
     */
    public void setIndVerLey10032015(boolean indVerLey10032015) {
        this.indVerLey10032015 = indVerLey10032015;
    }

    /**
     * Retorna la variable bloqExento
     *
     * @return bloqExento
     */
    public boolean isBloqExento() {
        return bloqExento;
    }

    /**
     * Asigna la variable bloqExento
     *
     * @param bloqExento
     * Variable a asignar en bloqExento
     */
    public void setBloqExento(boolean bloqExento) {
        this.bloqExento = bloqExento;
    }

    /**
     * Retorna la variable tituloForm
     *
     * @return tituloForm
     */
    public String getTituloForm() {
        return tituloForm;
    }

    /**
     * Asigna la variable tituloForm
     *
     * @param tituloForm
     * Variable a asignar en tituloForm
     */
    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTarifaActual
     *
     * @return listaTarifaActual
     */
    public List<Registro> getListaTarifaActual() {
        return listaTarifaActual;
    }

    /**
     * Asigna la lista listaTarifaActual
     *
     * @param listaTarifaActual
     * Variable a asignar en listaTarifaActual
     */
    public void setListaTarifaActual(List<Registro> listaTarifaActual) {
        this.listaTarifaActual = listaTarifaActual;
    }

    /**
     * Retorna la lista listaTipoNit
     *
     * @return listaTipoNit
     */
    public List<Registro> getListaTipoNit() {
        return listaTipoNit;
    }

    /**
     * Asigna la lista listaTipoNit
     *
     * @param listaTipoNit
     * Variable a asignar en listaTipoNit
     */
    public void setListaTipoNit(List<Registro> listaTipoNit) {
        this.listaTipoNit = listaTipoNit;
    }

    /**
     * Retorna la lista listaCmbPaisNotificacion
     *
     * @return listaCmbPaisNotificacion
     */
    public List<Registro> getListaCmbPaisNotificacion() {
        return listaCmbPaisNotificacion;
    }

    /**
     * Asigna la lista listaCmbPaisNotificacion
     *
     * @param listaCmbPaisNotificacion
     * Variable a asignar en listaCmbPaisNotificacion
     */
    public void setListaCmbPaisNotificacion(
        List<Registro> listaCmbPaisNotificacion) {
        this.listaCmbPaisNotificacion = listaCmbPaisNotificacion;
    }

    /**
     * Retorna la lista listaCmbDptoNotificacion
     *
     * @return listaCmbDptoNotificacion
     */
    public List<Registro> getListaCmbDptoNotificacion() {
        return listaCmbDptoNotificacion;
    }

    /**
     * Asigna la lista listaCmbDptoNotificacion
     *
     * @param listaCmbDptoNotificacion
     * Variable a asignar en listaCmbDptoNotificacion
     */
    public void setListaCmbDptoNotificacion(
        List<Registro> listaCmbDptoNotificacion) {
        this.listaCmbDptoNotificacion = listaCmbDptoNotificacion;
    }

    /**
     * Retorna la lista listaCmbCiudadNotificacion
     *
     * @return listaCmbCiudadNotificacion
     */
    public List<Registro> getListaCmbCiudadNotificacion() {
        return listaCmbCiudadNotificacion;
    }

    /**
     * Asigna la lista listaCmbCiudadNotificacion
     *
     * @param listaCmbCiudadNotificacion
     * Variable a asignar en listaCmbCiudadNotificacion
     */
    public void setListaCmbCiudadNotificacion(
        List<Registro> listaCmbCiudadNotificacion) {
        this.listaCmbCiudadNotificacion = listaCmbCiudadNotificacion;
    }

    /**
     * Retorna la lista listaFormatoEstrato
     *
     * @return listaFormatoEstrato
     */
    public List<Registro> getListaFormatoEstrato() {
        return listaFormatoEstrato;
    }

    /**
     * Asigna la lista listaFormatoEstrato
     *
     * @param listaFormatoEstrato
     * Variable a asignar en listaFormatoEstrato
     */
    public void setListaFormatoEstrato(List<Registro> listaFormatoEstrato) {
        this.listaFormatoEstrato = listaFormatoEstrato;
    }

    /**
     * Retorna la lista listaPagBan
     *
     * @return listaPagBan
     */
    public List<Registro> getListaPagBan() {
        return listaPagBan;
    }

    /**
     * Asigna la lista listaPagBan
     *
     * @param listaPagBan
     * Variable a asignar en listaPagBan
     */
    public void setListaPagBan(List<Registro> listaPagBan) {
        this.listaPagBan = listaPagBan;
    }

    /**
     * Retorna la lista listaPagBan1
     *
     * @return listaPagBan1
     */
    public List<Registro> getListaPagBan1() {
        return listaPagBan1;
    }

    /**
     * Asigna la lista listaPagBan1
     *
     * @param listaPagBan1
     * Variable a asignar en listaPagBan1
     */
    public void setListaPagBan1(List<Registro> listaPagBan1) {
        this.listaPagBan1 = listaPagBan1;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaUbicacion
     *
     * @return listaUbicacion
     */
    public RegistroDataModelImpl getListaUbicacion() {
        return listaUbicacion;
    }

    /**
     * Asigna la lista listaUbicacion
     *
     * @param listaUbicacion
     * Variable a asignar en listaUbicacion
     */
    public void setListaUbicacion(RegistroDataModelImpl listaUbicacion) {
        this.listaUbicacion = listaUbicacion;
    }

    /**
     * Retorna la lista listaTipo
     *
     * @return listaTipo
     */
    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    /**
     * Asigna la lista listaTipo
     *
     * @param listaTipo
     * Variable a asignar en listaTipo
     */
    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    /**
     * Retorna la lista listaClasePredio
     *
     * @return listaClasePredio
     */
    public RegistroDataModelImpl getListaClasePredio() {
        return listaClasePredio;
    }

    /**
     * Asigna la lista listaClasePredio
     *
     * @param listaClasePredio
     * Variable a asignar en listaClasePredio
     */
    public void setListaClasePredio(RegistroDataModelImpl listaClasePredio) {
        this.listaClasePredio = listaClasePredio;
    }

    /**
     * Retorna la lista listatipoMutacion
     *
     * @return listatipoMutacion
     */
    public RegistroDataModelImpl getListatipoMutacion() {
        return listatipoMutacion;
    }

    /**
     * Asigna la lista listatipoMutacion
     *
     * @param listatipoMutacion
     * Variable a asignar en listatipoMutacion
     */
    public void setListatipoMutacion(RegistroDataModelImpl listatipoMutacion) {
        this.listatipoMutacion = listatipoMutacion;
    }

    /**
     * Retorna la lista listaTrpcod
     *
     * @return listaTrpcod
     */
    public RegistroDataModelImpl getListaTrpcod() {
        return listaTrpcod;
    }

    /**
     * Asigna la lista listaTrpcod
     *
     * @param listaTrpcod
     * Variable a asignar en listaTrpcod
     */
    public void setListaTrpcod(RegistroDataModelImpl listaTrpcod) {
        this.listaTrpcod = listaTrpcod;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna la lista listaEstratoSocioeconomico
     *
     * @return listaEstratoSocioeconomico
     */

    public RegistroDataModelImpl getListaEstratoSocioeconomico() {
        return listaEstratoSocioeconomico;
    }

    /**
     * Asigna la lista listaEstratoSocioeconomico
     *
     * @param listaEstratoSocioeconomico
     * Variable a asignar en listaEstratoSocioeconomico
     */
    public void setListaEstratoSocioeconomico(
        RegistroDataModelImpl listaEstratoSocioeconomico) {
        this.listaEstratoSocioeconomico = listaEstratoSocioeconomico;
    }

    /**
     * Retorna la lista tienePagosDobles
     *
     * @return tienePagosDobles
     */
    public boolean isTienePagosDobles() {
        return tienePagosDobles;
    }

    /**
     * Asigna la lista tienePagosDobles
     *
     * @param tienePagosDobles
     * Variable a asignar en tienePagosDobles
     */
    public void setTienePagosDobles(boolean tienePagosDobles) {
        this.tienePagosDobles = tienePagosDobles;
    }

    /**
     * Retorna la lista indVerDgEstado
     *
     * @return indVerDgEstado
     */
    public boolean isIndVerDgEstado() {
        return indVerDgEstado;
    }

    /**
     * Asigna la lista indVerDgEstado
     *
     * @param indVerDgEstado
     * Variable a asignar en indVerDgEstado
     */
    public void setIndVerDgEstado(boolean indVerDgEstado) {
        this.indVerDgEstado = indVerDgEstado;
    }

    public String getActivarBtCaja() {
        return activarBtCaja;
    }

    public void setActivarBtCaja(String activarBtCaja) {
        this.activarBtCaja = activarBtCaja;
    }

    /**
     * Retorna la lista verOtroFactor
     *
     * @return verOtroFactor
     */
    public boolean isVerOtroFactor() {
        return verOtroFactor;
    }

    /**
     * Asigna la lista verOtroFactor
     *
     * @param verOtroFactor
     * Variable a asignar en verOtroFactor
     */
    public void setVerOtroFactor(boolean verOtroFactor) {
        this.verOtroFactor = verOtroFactor;
    }

    /**
     * Retorna la lista permiteCalculoAutom
     *
     * @return permiteCalculoAutom
     */
    public String getPermiteCalculoAutom() {
        return permiteCalculoAutom;
    }

    /**
     * Asigna la lista permiteCalculoAutom
     *
     * @param permiteCalculoAutom
     * Variable a asignar en permiteCalculoAutom
     */
    public void setPermiteCalculoAutom(String permiteCalculoAutom) {
        this.permiteCalculoAutom = permiteCalculoAutom;
    }

	public boolean isVisibleUsuarios() {
		return visibleUsuarios;
	}

	public void setVisibleUsuarios(boolean visibleUsuarios) {
		this.visibleUsuarios = visibleUsuarios;
	}



    // </SET_GET_ADICIONALES>
}
