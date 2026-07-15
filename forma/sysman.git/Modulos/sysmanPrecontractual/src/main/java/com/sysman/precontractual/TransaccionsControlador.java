package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
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
import com.sysman.precontractual.ejb.EjbPrecontractualUnoRemote;
import com.sysman.precontractual.enums.TransaccionsControladorEnum;
import com.sysman.precontractual.enums.TransaccionsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 17/12/2015
 * 
 * @modifier amonroy
 * @version 2, 04/09/2017 Proceso de Refactoring, Revision de buenas
 * practicas sugeridas por la herramienta SonarLint e implementación
 * de EJBs para llamado de funciones y procedimientos
 */
@ManagedBean
@ViewScoped
public class TransaccionsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo del modulo en el que se esta
     * trabajando actualmente
     */
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo COMPANIA en el formulario, almacena el texto
     * COMPANIA
     */
    private final String cCompania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo en el formulario, almacena el texto
     * CONSECUTIVO
     */
    private final String cConsecutivo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CONSECUTIVODETALLE en el formulario, almacena
     * el texto CONSECUTIVODETALLE
     */
    private final String cConsecutivoDetalle;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo COTIZA_ELEMENTOS en el formulario, almacena el
     * texto COTIZA_ELEMENTOS
     */
    private final String cCotizaElementos;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo DESCRIPCION en el formulario, almacena el
     * texto DESCRIPCION
     */
    private final String cDescripcion;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ESTADO en el formulario, almacena el texto
     * ESTADO
     */
    private final String cEstado;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ESTADOETAPA en el formulario, almacena el
     * texto ESTADOETAPA
     */
    private final String cEstadoEtapa;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ESTUDIOPREVIO en el formulario, almacena el
     * texto ESTUDIOPREVIO
     */
    private final String cEstudioPrevio;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo FECHAFINAL en el formulario, almacena el texto
     * FECHAFINAL
     */
    private final String cFechaFinal;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo FECHAINICIAL en el formulario, almacena el
     * texto FECHAINICIAL
     */
    private final String cFechaInicial;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo HEREDAPROPONENTE en el formulario, almacena el
     * texto HEREDAPROPONENTE
     */
    private final String cHeredaProponente;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo IDELEMENTO en el formulario, almacena el texto
     * IDELEMENTO
     */
    private final String cIdElemento;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo IDETAPA en el formulario, almacena el texto
     * IDETAPA
     */
    private final String cIdEtapa;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE_ESTUDIO en el formulario, almacena el
     * texto NOMBRE_ESTUDIO
     */
    private final String cNombreEstudio;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMESTADOVIG en el formulario, almacena el
     * texto NOMESTADOVIG
     */
    private final String cNomEstadoVig;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOM_TIPO en el formulario, almacena el texto
     * NOM_TIPO
     */
    private final String cNomTipo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NUM_PROCESO en el formulario, almacena el
     * texto NUM_PROCESO
     */
    private final String cNumProceso;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ORDEN en el formulario, almacena el texto
     * ORDEN
     */
    private final String cOrden;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo TRANSACCION en el formulario, almacena el
     * texto TRANSACCION
     */
    private final String cTransaccion;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo HORA en el formulario, almacena el texto HORA
     */
    private final String cHora;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo TRANSACCIONITEMINVENTARIO en el formulario,
     * almacena el texto TRANSACCIONITEMINVENTARIO
     */
    private final String cTransaccionItemInventario;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al elemento almacenado en el archivo de properties
     * "TB_TB2229" en el formulario, almacena el texto "TB_TB2229"
     */
    private final String cTb2229;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al elemento almacenado en el archivo de properties
     * "TB_TB560" en el formulario, almacena el texto "TB_TB560"
     */
    private final String cTb560;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "consecutivoDetalle" en el formulario
     */
    private final String cConsecutivoDetalleStr;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "consecutivoTransaccion" en el formulario
     */
    private final String cConsecutivoTransaccion;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "dd/MM/yyyy" en el formulario, el cual define
     * el tipo de formateo para una fecha
     */
    private final String cFormatoFecha;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "desdeMonitor" en el formulario
     */
    private final String cDesdeMonitor;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "estadoEtapa" en el formulario
     */
    private final String cEstadoEtapaStr;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "idEtapa" en el formulario
     */
    private final String cIdEtapaStr;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "nombreEtapa" en el formulario
     */
    private final String cNombreEtapa;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "tipoContrato" en el formulario
     */
    private final String cTipoContrato;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "transaccion" en el formulario
     */
    private final String cTransaccionStr;

    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    private Registro registroSubtransaccionSubEtapa;
    private Registro registroSubtransaccionSubItems;
    private List<Registro> listaTransaccionsubetapa;
    private List<Registro> listaTransaccionsubitems;
    private RegistroDataModelImpl listaidEtapa;
    private RegistroDataModelImpl listaidEtapaE;
    private String auxiliar;
    private RegistroDataModelImpl listaidItem;
    private RegistroDataModelImpl listaidItemE;
    private RegistroDataModelImpl listaEstudioPrevio;
    private String anio;
    private String tipoContrato;
    private String etVigencia;
    private String etEstadoVig;
    private String tituloSubEtapa;
    private String tituloSubProponente;
    private String valorUnitTotal;
    private String valorIvaTotal;
    private String valorDescTotal;
    private String valorTotal;
    private String transaccion;
    private String condicion;
    private String codEstudio;
    private String estadoVigencia;
    private String estadoProceso;
    private boolean bloqueaCampos; // Bloquear los campos dependiendo
    // la condicion del editar
    // transaccion
    private boolean editableEstPrevio; // Habilitar combo estudio
    // previo
    private boolean heredaProponente;
    private boolean bloqueaEstado;
    private boolean bloqueaEstadoSub;
    private boolean cambiarEstudio; // Condicion dialogo Estudio
    // Previo
    private String controlDependencia;
    private String redonValorUnitarioIVA;
    private String digRedoValorUnitarioIVA;
    private String redondeoTotal;
    private String digRedonTotal;
    private int indiceTransaccionsubetapa;
    private Long consecutivoN;
    private String estAnterior;
    private String nombreTipoContrato;
    private String nombEstadoVigencia;
    private String desdeMonitor;
    private String strConsecutivo;
    private boolean modificar;
    private boolean fechaFinNull;
    private boolean herPropAct;
    private boolean herPropSig;
    private boolean ultimaEtapa;
    private String consecutivoSig;
    private String consecutivoAct;

    private String estActualAux;
    /**
     * Atributo que permite identificar si ha sido enviado un mensaje
     * de alerta al usuario y si es necesario realizar un return en el
     * metodo cambiarEstadoC()
     */
    private boolean indicadorReturn;
    /**
     * Atributo que almacena el consecutivo de la transaccion que se
     * esta trabajando, cuando es redireccionado desde el formulario
     * "Monitoretapas"
     */
    private String transaccionPar;
    /**
     * Atributo que almacena el orden de la etapa que ha sido
     * seleccionada en el combo "Etapa"
     */
    private int ordenEtapa;
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a
     * las funciones definidas en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de ejbPrecontractualUno para hacer el
     * llamado a las funciones y procedimientos definidos en el
     * paquete PCK_PRECONTRACTUAL1
     */
    @EJB
    private EjbPrecontractualUnoRemote ejbPrecontractualUno;

    private Map<String,Object> parametroswf;
	private boolean verCerrar = true;
    /**
     * Crea una nueva instancia de TransaccionsControlador
     */
    public TransaccionsControlador() {
        super();
        compania = SessionUtil.getCompania();
        SessionUtil.setSessionVar("modulo", "19");
        modulo = SessionUtil.getModulo();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();
        cConsecutivoDetalle = TransaccionsControladorEnum.CONSECUTIVODETALLE
                        .getValue();
        cCotizaElementos = TransaccionsControladorEnum.COTIZA_ELEMENTOS
                        .getValue();
        cDescripcion = GeneralParameterEnum.DESCRIPCION.getName();
        cEstado = GeneralParameterEnum.ESTADO.getName();
        cEstadoEtapa = TransaccionsControladorEnum.ESTADOETAPA.getValue();
        cEstudioPrevio = TransaccionsControladorEnum.ESTUDIOPREVIO.getValue();
        cFechaFinal = GeneralParameterEnum.FECHAFINAL.getName();
        cFechaInicial = GeneralParameterEnum.FECHAINICIAL.getName();
        cHeredaProponente = TransaccionsControladorEnum.HEREDAPROPONENTE
                        .getValue();
        cIdElemento = TransaccionsControladorEnum.IDELEMENTO.getValue();
        cIdEtapa = TransaccionsControladorEnum.IDETAPA.getValue();
        cNombreEstudio = TransaccionsControladorEnum.NOMBRE_ESTUDIO.getValue();
        cNomEstadoVig = TransaccionsControladorEnum.NOMESTADOVIG.getValue();
        cNomTipo = TransaccionsControladorEnum.NOM_TIPO.getValue();
        cNumProceso = TransaccionsControladorEnum.NUM_PROCESO.getValue();
        cOrden = GeneralParameterEnum.ORDEN.getName();
        cTransaccion = TransaccionsControladorEnum.TRANSACCION.getValue();
        cHora = TransaccionsControladorEnum.HORA.getValue();
        cTransaccionItemInventario = TransaccionsControladorEnum.TRANSACCIONITEMINVENTARIO
                        .getValue();
        cTb2229 = TransaccionsControladorEnum.TB_TB2229.getValue();
        cTb560 = TransaccionsControladorEnum.TB_TB560.getValue();
        cConsecutivoDetalleStr = TransaccionsControladorEnum.CONSECUTIVODETALLESTR
                        .getValue();
        cConsecutivoTransaccion = TransaccionsControladorEnum.CONSECUTIVOTRANSACCION
                        .getValue();
        cFormatoFecha = TransaccionsControladorEnum.FORMATOFECHA.getValue();
        cDesdeMonitor = TransaccionsControladorEnum.DESDEMONITOR.getValue();
        cEstadoEtapaStr = TransaccionsControladorEnum.ESTADOETAPASTR.getValue();
        cIdEtapaStr = TransaccionsControladorEnum.IDETAPASTR.getValue();
        cNombreEtapa = TransaccionsControladorEnum.NOMBREETAPA.getValue();
        cTipoContrato = TransaccionsControladorEnum.TIPOCONTRATO.getValue();
        cTransaccionStr = TransaccionsControladorEnum.TRANSACCIONSTR.getValue();

        try {
            numFormulario = GeneralCodigoFormaEnum.TRANSACCIONS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            registroSubtransaccionSubEtapa = new Registro(
                            new HashMap<String, Object>());
            registroSubtransaccionSubItems = new Registro(
                            new HashMap<String, Object>());
            etVigencia = idioma.getString("TG_VIGENCIA4");
            etEstadoVig = idioma.getString("TB_TB3560");

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                rid = (Map<String, Object>) parametros.get("rid");

                tipoContrato = (String) parametros.get(cTipoContrato);
                anio = String.valueOf(parametros.get("anio"));
                estadoVigencia = (String) parametros.get("estadoVigencia");
                estadoProceso = (String) parametros.get("estadoProceso");
                condicion = (String) SysmanFunciones
                                .nvl(parametros.get("condicion"), "");
                parametroswf = (Map<String,Object>) parametros.get("parametroswf");
    			if(parametroswf != null) {
    				varVolver = false;
    				verCerrar = false;
    			}

                if (rid != null) {
                    desdeMonitor = (String) SysmanFunciones
                                    .nvl(parametros.get(cDesdeMonitor), "NO");
                    nombEstadoVigencia = "A".equals(estadoVigencia) ? "Activo"
                        : "Cancelado";
                    transaccionPar = (String) parametros.get(cTransaccionStr);
                    validarDesdeMonitor();

                }
                else {
                    varVolver = rid != null;
                    modificar = true;
                    nombEstadoVigencia = (String) parametros
                                    .get("nombEstadoVigencia");
                    nombreTipoContrato = (String) parametros
                                    .get("nombreTipoContrato");
                    desdeMonitor = (String) parametros.get(cDesdeMonitor);
                    transaccionPar = SysmanFunciones
                                    .nvl(parametros.get(cTransaccionStr), "")
                                    .toString();

                }
            }

        }
        catch (Exception ex) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
			SessionUtil.cleanFlash();
		}
    }

    @PostConstruct
    public void inicializar() {
    	if(parametroswf != null) {
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
			
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");
		}
        enumBase = GenericUrlEnum.TRANSACCION;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                        tipoContrato);
        parametrosListado.put(TransaccionsControladorEnum.ANOTX.getValue(),
                        anio);
        parametrosListado.put(cTransaccion, transaccionPar);

    }

    @Override
    public void iniciarListas() {
        cargarListaidItem();
        cargarListaidItemE();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaTransaccionsubetapa();
        cargarListaTransaccionsubitems();
        cargarListaidEtapa();
        cargarListaidEtapaE();
    }

    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     * 
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.redireccionar("/monitoretapa.sysman");
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTransaccionsubetapa() {
        transaccion = retornarString(registro, cConsecutivo);

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                            tipoContrato);
            param.put(TransaccionsControladorEnum.TRANSACCIONPAR.getValue(),
                            transaccion);

            listaTransaccionsubetapa = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.D_TRANSACCION
                                                                            .getGridKey())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            GenericUrlEnum.D_TRANSACCION
                                                            .getTable()));

        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaTransaccionsubitems() {
        codEstudio = retornarString(registro, cEstudioPrevio);
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                            tipoContrato);
            param.put(TransaccionsControladorEnum.TRANSACCIONPAR.getValue(),
                            transaccion);
            param.put(TransaccionsControladorEnum.CODESTUDIO.getValue(),
                            codEstudio);

            listaTransaccionsubitems = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionsControladorUrlEnum.URL11581
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            cTransaccionItemInventario));

        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaidEtapa() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionsControladorUrlEnum.URL12382
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                        tipoContrato);
        param.put(TransaccionsControladorEnum.TRANSACCION.getValue(),
                        transaccion);

        listaidEtapa = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdEtapa);
    }

    public void cargarListaidEtapaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionsControladorUrlEnum.URL13155
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                        tipoContrato);
        param.put(TransaccionsControladorEnum.TRANSACCION.getValue(),
                        transaccion);

        listaidEtapaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdEtapa);
    }

    public void cargarListaidItem() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionsControladorUrlEnum.URL13927
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaidItem = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdElemento);
    }

    public void cargarListaidItemE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionsControladorUrlEnum.URL14665
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaidItemE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cIdElemento);
    }

    /**
     * Realiza la carga del listado para el combo de estudio previo
     * dependiendo del valor del parametro "CONTROLA DEPENDENCIA EN
     * PRECONTRATOS"
     */
    public void cargarlistaEstudioPrevio() {
        UrlBean urlBean;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                        String.valueOf(tipoContrato));

        if ("SI".equalsIgnoreCase(controlDependencia) && (SessionUtil
                        .getNivelUsuario(SessionUtil.getModulo()) == 9)) {
            // Dependencia la traen de la tabla cuenta
            param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            SessionUtil.getUser().getDependencia().getCodigo());
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            TransaccionsControladorUrlEnum.URL16900.getValue());
        }
        else {
            urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                            TransaccionsControladorUrlEnum.URL16901.getValue());
        }
        listaEstudioPrevio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cEstudioPrevio);
    }

    /**
     * Evalua el estado de la etapa siguiente a la seleccionada para
     * permitir o no la insercion de una etapa
     * 
     * @param regEtapa
     * Etapa que se desea registrar
     * @return Verdadero, si el estado de la siguiente etapa es
     * PENDIENTE
     */
    private boolean permitirInsercion() {
        boolean respuesta = false;
        try {

            Map<String, Object> params = new TreeMap<>();
            params.put(cCompania, compania);
            params.put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
            params.put(cTransaccion, transaccion);
            params.put(TransaccionsControladorEnum.ORDENACTUAL.getValue(),
                            ordenEtapa);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionsControladorUrlEnum.URL007
                                                                            .getValue())
                                            .getUrl(), params));
            // Validacion para la ultima etapa
            if (rs == null) {
                return true;
            }

            String estadoSiguiente = SysmanFunciones
                            .nvl(rs.getCampos().get(cEstado), "").toString();
            if ("P".equals(estadoSiguiente)) {
                respuesta = true;
            }
            else {
                respuesta = false;
                String estado = "A".equals(estadoSiguiente)
                    ? "ACTIVO"
                    : "C".equals(estadoSiguiente) ? "CERRADO" : "SUSPENDIDO";
                String mensaje = idioma.getString("TB_TB3753");
                mensaje = mensaje.replace("s$estado$s", estado);
                JsfUtil.agregarMensajeAlerta(mensaje);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    public void agregarRegistroSubTransaccionsubetapa() {
        try {

            if (SysmanFunciones.validarCampoVacio(
                            registroSubtransaccionSubEtapa.getCampos(),
                            cEstadoEtapa)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2220"));
                return;
            }
            if (fechaFinNull) {
                registroSubtransaccionSubEtapa.getCampos().put(
                                TransaccionsControladorEnum.HORA.getValue(),
                                null);
            }

            if (!permitirInsercion()) {
                return;
            }

            registroSubtransaccionSubEtapa.getCampos().put(cCompania,
                            compania);
            registroSubtransaccionSubEtapa.getCampos().put(
                            GeneralParameterEnum.TIPOCONTRATO.getName(),
                            tipoContrato);
            registroSubtransaccionSubEtapa.getCampos().put(cTransaccion,
                            transaccion);
            long consecutivoSubEtapa = ejbSysmanUtil
                            .generarConsecutivoConValorInicial("D_TRANSACCION",
                                            SysmanFunciones.concatenar(
                                                            " COMPANIA = ''",
                                                            compania,
                                                            "'' AND TIPOCONTRATO = ''",
                                                            tipoContrato,
                                                            "''  AND TRANSACCION = ",
                                                            transaccion, " "),
                                            "CONSECUTIVODETALLE",
                                            "1");
            registroSubtransaccionSubEtapa.getCampos().put(cConsecutivoDetalle,
                            consecutivoSubEtapa);

            registroSubtransaccionSubEtapa.getCampos().put(cOrden, ordenEtapa);

            registroSubtransaccionSubEtapa.getCampos().put(cEstado,
                            registroSubtransaccionSubEtapa.getCampos()
                                            .get(cEstadoEtapa));
            registroSubtransaccionSubEtapa.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubtransaccionSubEtapa.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSubtransaccionSubEtapa.getCampos().remove(cDescripcion);
            registroSubtransaccionSubEtapa.getCampos()
                            .remove(TransaccionsControladorEnum.FECHAINICIALLB
                                            .getValue());
            registroSubtransaccionSubEtapa.getCampos()
                            .remove(TransaccionsControladorEnum.FECHAFINALLB
                                            .getValue());
            registroSubtransaccionSubEtapa.getCampos().remove(cEstadoEtapa);
            registroSubtransaccionSubEtapa.getCampos()
                            .remove(TransaccionsControladorEnum.NOM_ESTADOETAPA
                                            .getValue());
            registroSubtransaccionSubEtapa.getCampos()
                            .remove(cHeredaProponente);
            registroSubtransaccionSubEtapa.getCampos()
                            .remove(cCotizaElementos);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_TRANSACCION
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubtransaccionSubEtapa
                                            .getCampos());
            cargarListaTransaccionsubetapa();
            cargarListaidEtapa();
            cargarListaidEtapaE();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubtransaccionSubEtapa = new Registro(
                            new HashMap<String, Object>());
            cargarRegistro();
        }
    }

    public void editarRegSubTransaccionsubetapa(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (fechaFinNull) {
                reg.getCampos().put(TransaccionsControladorEnum.HORA.getValue(),
                                null);
            }

            reg.getCampos().put(cEstado, reg.getCampos().get(cEstadoEtapa));

            reg.getCampos().remove(cDescripcion);
            reg.getCampos().remove(TransaccionsControladorEnum.FECHAINICIALLB
                            .getValue());
            reg.getCampos().remove(TransaccionsControladorEnum.FECHAFINALLB
                            .getValue());
            reg.getCampos().remove(cEstadoEtapa);
            reg.getCampos().remove(TransaccionsControladorEnum.NOM_ESTADOETAPA
                            .getValue());
            reg.getCampos().remove(cHeredaProponente);
            reg.getCampos().remove(cCotizaElementos);

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_TRANSACCION
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
            if ("A".equals(estActualAux) && !ultimaEtapa &&
                herPropAct && herPropSig) {
                actualizarInfoProponentes();
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaTransaccionsubetapa();
            cargarListaidEtapa();
            cargarListaidEtapaE();
        }
    }

    public void eliminarRegSubTransaccionsubetapa(Registro reg) {
        try {
            if (!"P".equals(reg.getCampos().get(cEstadoEtapa).toString())) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2221"));
                return;
            }

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.D_TRANSACCION
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaTransaccionsubetapa();
            cargarListaidEtapa();
            cargarListaidEtapaE();
        }
        catch (SystemException ex) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionTransaccionsubetapa() {
        cargarListaTransaccionsubetapa();
    }

    public void agregarRegistroSubTransaccionsubitems() {
        // Para este subformulario no se encuentra habilitado el
        // proceso de Insercion, unicamente visualizacion
    }

    public void editarRegSubTransaccionsubitems(RowEditEvent event) {
        // Para este subformulario no se encuentra habilitado el
        // proceso de Actualizacion, unicamente visualizacion
    }

    public void eliminarRegSubTransaccionsubitems(Registro reg) {
        // Para este subformulario no se encuentra habilitado el
        // proceso de eliminacion, unicamente visualizacion
    }

    public void cancelarEdicionTransaccionsubitems() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Adiciona los parametros necesario para realizar la redireccion
     * de regreso al formulario de transacciones
     * 
     * @param parametros
     * Estructura que almacena los paranetros a enviar en la
     * redireccion a formularios
     */
    private void asignarParametrosRedireccion(Map<String, Object> parametros) {
        // Parametros para envio de regreso
        parametros.put("estadoVigencia", estadoVigencia);
        parametros.put("estadoProceso", estadoProceso);
        parametros.put("anio", anio);
        parametros.put("condicion", condicion);
        parametros.put(cDesdeMonitor, desdeMonitor);
        parametros.put("transaccionPar", transaccionPar);
        parametros.put("rid", css);
    }

    /**
     * Metodo ejecutado al oprimir el boton Prerrequisitos
     * 
     * Redirecciona al formulario "Prerrequisitosetapa"(431)
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirPrerrequisitos(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(), cIdEtapa)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2224"));
            return;
        }
        String nombreEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cDescripcion), "NO");
        String estadoEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cEstadoEtapa), "NO");

        Map<String, Object> parametros = new HashMap<>();
        asignarParametrosRedireccion(parametros);
        parametros.put(cTipoContrato, tipoContrato);
        parametros.put("consecutivop", transaccion);
        parametros.put(cConsecutivoDetalleStr, reg.getCampos()
                        .get(cConsecutivoDetalle).toString());
        parametros.put("etapa", reg.getCampos().get(cIdEtapa).toString());
        parametros.put("modificar", Boolean.toString(modificar));
        parametros.put(cEstadoEtapaStr, estadoEtapa);
        parametros.put(cNombreEtapa,
                        !"NO".equals(nombreEtapa) ? nombreEtapa : "");

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PREREQUISITOSETAPAS_CONTROLADOR
                                        .getCodigo()));// 431
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton GenDocumento
     * 
     * Redirecciona al formulario "Generarmodeloetapa"(579)
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirGenDocumento(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(), cIdEtapa)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2225"));
            return;
        }
        String nombreEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cDescripcion), "NO");

        String fecha = null;
        try {
            fecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName()),
                            cFormatoFecha);
        }
        catch (ParseException e) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

        String consecutivo = reg.getCampos().get(cConsecutivoDetalle)
                        .toString();

        String desdeTransaccion = "true";

        Map<String, Object> parametros = new HashMap<>();
        asignarParametrosRedireccion(parametros);
        parametros.put(cTipoContrato, tipoContrato);
        parametros.put(cConsecutivoTransaccion, transaccion);
        parametros.put(cConsecutivoDetalleStr, consecutivo);
        parametros.put(cIdEtapaStr, reg.getCampos().get(cIdEtapa).toString());
        parametros.put(cNombreEtapa, nombreEtapa);
        parametros.put("fecha", fecha);
        parametros.put("heredaProponente",
                        reg.getCampos().get(cHeredaProponente).toString());
        parametros.put("desdeTransaccion", desdeTransaccion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.GENERARMODELOETAPA_CONTROLADOR
                                        .getCodigo()));// 579
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton observaciones
     * 
     * Redirecciona al formulario "Observacionesetapa"(438)
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirobservaciones(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(), cIdEtapa)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2227"));
            return;
        }
        String nombreEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cDescripcion), "NO");
        String estadoEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cEstadoEtapa), "NO");

        Map<String, Object> parametros = new HashMap<>();
        asignarParametrosRedireccion(parametros);
        parametros.put(cTipoContrato, tipoContrato);
        parametros.put(cConsecutivoTransaccion, transaccion);
        parametros.put(cConsecutivoDetalleStr,
                        reg.getCampos().get(cConsecutivoDetalle).toString());
        parametros.put(cIdEtapaStr, reg.getCampos().get(cIdEtapa).toString());
        parametros.put(cNombreEtapa, nombreEtapa);
        parametros.put("modificar", Boolean.toString(modificar));
        parametros.put(cEstadoEtapaStr, estadoEtapa);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.OBSERVACIONESETAPAS_CONTROLADOR
                                        .getCodigo()));// 438
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Proponentes
     * 
     * Redirecciona al formulario "Prponenteetapa"(588)
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirProponentes(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(reg.getCampos(), cIdEtapa)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2225"));
            return;
        }

        String nombreEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cDescripcion), "NO");
        String estadoEtapa = SysmanFunciones
                        .nvlStr(retornarString(reg, cEstadoEtapa), "NO");

        Map<String, Object> parametros = new HashMap<>();
        asignarParametrosRedireccion(parametros);
        parametros.put(cTipoContrato, tipoContrato);
        parametros.put(cConsecutivoTransaccion, transaccion);
        parametros.put(cConsecutivoDetalleStr,
                        reg.getCampos().get(cConsecutivoDetalle).toString());
        parametros.put(cIdEtapaStr, reg.getCampos().get(cIdEtapa).toString());
        parametros.put(cNombreEtapa, nombreEtapa);
        parametros.put(cEstadoEtapaStr, estadoEtapa);
        parametros.put("cotizaInventario",
                        reg.getCampos().get(cCotizaElementos).toString());
        parametros.put("redonValorUnitarioIVA", redonValorUnitarioIVA);
        parametros.put("digRedoValorUnitarioIVA", digRedoValorUnitarioIVA);
        parametros.put("redondeoTotal", redondeoTotal);
        parametros.put("digRedonTotal", digRedonTotal);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PROPONENTEETAPAS_CONTROLADOR
                                        .getCodigo()));// 588
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    public void oprimiranexos(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        String titulo = idioma.getString("TB_TB3562");

        String codigo = String
                        .valueOf(reg.getCampos().get(cConsecutivoDetalle));

        String[] campos = { "titulo", "codigo", "vigencia", "tipoContrato",
                            "transaccion", "consecutivo", "esCreador" };
        String[] valores = { titulo, codigo, anio, tipoContrato, transaccion,
                             reg.getCampos().get(cConsecutivoDetalle)
                                             .toString(),
                             "A".equals(reg.getCampos().get("ESTADOETAPA")
                                             .toString()) ? "false" : "true" };

        SessionUtil.cargarModalDatosFlash(String.valueOf(
                        GeneralCodigoFormaEnum.ANEXOSESTUDIOSPREVIOS_CONTROLADOR
                                        .getCodigo()),
                        SessionUtil.getModulo(), campos, valores);// 944
        // ANEXOSESTUDIOSPREVIOS_CONTROLADOR
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarestadoEtapa() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control estadoProceso
     * 
     * 
     */
    public void cambiarestadoProceso() {
        // <CODIGO_DESARROLLADO>
        cargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarEstado() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiardescripcion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Ejecuta el procedimiento que realiza la configuracion de los
     * items de inventario relacionados con el estudio previo
     */
    private boolean ejecutarCambiarestudioPrevio() {
        boolean respuesta = false;
        try {

            respuesta = ejbPrecontractualUno.cambiarEstudioPrevio(
                            compania,
                            tipoContrato,
                            Long.parseLong(transaccion),
                            new BigInteger(registro.getCampos()
                                            .get(cEstudioPrevio).toString()),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * EstudioPrevio en la vista
     *
     * Actualiza los valores del subformulario "transaccionesSubItems"
     *
     */
    public void aceptarEstudioPrevio() {
        // <CODIGO_DESARROLLADO>
        cambiarEstudio = false;
        editableEstPrevio = true;

        boolean poseeItems = ejecutarCambiarestudioPrevio();
        if (!poseeItems) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2228"));
            editableEstPrevio = false;
        }

        cargarListaTransaccionsubitems();
        calcularTotales();
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarEstudioPrevio() {
        // <CODIGO_DESARROLLADO>
        cambiarEstudio = false;
        editableEstPrevio = false;
        registro.getCampos().put("ESTUDIOPREVIO", null);
        registro.getCampos().put("NOMBRE_ESTUDIO", null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control idEtapa en la fila
     * seleccionada dentro de la grilla del subformulario
     * TransaccionSubetapa
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiaridEtapaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        listaTransaccionsubetapa.get(rowNum).getCampos().put(cOrden,
                        ordenEtapa);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ESTADO en la fila
     * seleccionada dentro de la grilla del subformulario
     * "transaccionSubEtapa"
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarestadoEtapaC(int rowNum) {
        indicadorReturn = false;
        String estadoAnterior;
        String estadoActual = listaTransaccionsubetapa.get(rowNum).getCampos()
                        .get(cEstadoEtapa).toString();
        int tamLista = listaTransaccionsubetapa.size();
        int rAux = tamLista - 1;
        if (rowNum == rAux) {
            ultimaEtapa = true;
        }
        else {
            herPropSig = Boolean.parseBoolean(listaTransaccionsubetapa
                            .get(rowNum + 1).getCampos().get(cHeredaProponente)
                            .toString());
            consecutivoSig = listaTransaccionsubetapa.get(rowNum + 1)
                            .getCampos().get(cConsecutivoDetalle).toString();
        }
        herPropAct = Boolean.parseBoolean(listaTransaccionsubetapa.get(rowNum)
                        .getCampos().get(cHeredaProponente).toString());
        consecutivoAct = listaTransaccionsubetapa.get(rowNum).getCampos()
                        .get(cConsecutivoDetalle).toString();
        estActualAux = idioma.getString("TB_TB3563");

        // Verifica si posee etapas anteriores
        if (rowNum == 0) {
            estadoAnterior = "NO";
        }
        else {
            estadoAnterior = listaTransaccionsubetapa.get(rowNum - 1)
                            .getCampos().get(cEstadoEtapa).toString();
            estadoAnterior = SysmanFunciones.nvl(estadoAnterior, "NO")
                            .toString();
            estadoActual = SysmanFunciones.nvl(estadoActual, "NO").toString();
        }

        // Evalua si es posible realizar el cambio de estado de la
        // etapa dependiendo el valor de las etapas anterior y
        // siguiente
        for (int i = 0; i < tamLista; i++) {
            listaTransaccionsubetapa.get(i).getCampos().put(cEstado,
                            listaTransaccionsubetapa.get(i).getCampos()
                                            .get(cEstadoEtapa));

            evaluarEtapaAnterior(estadoActual, rowNum, i);

            evaluarEtapaSiguiente(estadoActual, rowNum, i);
        }

        if (!"S".equals(estadoActual)) {
            evaluarEstadoNoSuspendido(estadoActual, estadoAnterior, rowNum);
        }
        else if ("S".equals(estadoActual) && "A".equals(estAnterior)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2240"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstadoEtapa,
                            estAnterior);
            return;
        }

        if (indicadorReturn) {
            return;
        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Evalua el estado de la etapa anterior en relacion al estado de
     * la etapa actual y realiza la asignacion de los campos ESTADO y
     * ESTADOETAPA al registro que se esta trabajando
     * 
     * @param estadoActual
     * Valor de estado de la etapa actual
     * @param rowNum
     * Registro en el que se esta trabajando
     * @param i
     * indicador del ciclo que recorre los registros del subformulario
     * "transaccionSubEtapa"
     */

    private void evaluarEtapaAnterior(String estadoActual, int rowNum, int i) {
        if (i < rowNum) {
            String estadoAnt = listaTransaccionsubetapa.get(i)
                            .getCampos().get(cEstadoEtapa).toString();
            if ("A".equals(estadoAnt) && "A".equals(estadoActual)) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(cTb2229));
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstado, estAnterior);
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstadoEtapa, estAnterior);
                indicadorReturn = true;
            }
            if ("S".equals(estadoAnt) && "S".equals(estadoActual)) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(cTb2229));
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstado, estAnterior);
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstadoEtapa, estAnterior);
                indicadorReturn = true;
            }
            if (evaluarEtapaAnteriorCondicion(estadoAnt, estadoActual)) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2232"));
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstado, estAnterior);
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstadoEtapa, estAnterior);
                indicadorReturn = true;
            }

            evaluarEtapaAnteriorAux(estadoActual, rowNum, i, estadoAnt);
        }

    }

    /**
     * Evalua que el estado anterior no este cerrado y que el estado
     * actual este abierto
     * 
     * @param estadoAnt
     * Estado de la etapa anterior
     * @param estadoActual
     * Estado de la etapa actual
     * @return Verdadero si cumple la condicion
     */
    private boolean evaluarEtapaAnteriorCondicion(String estadoAnt,
        String estadoActual) {
        return !"C".equals(estadoAnt) && "A".equals(estadoActual);
    }

    /**
     * Evalua el estado de la etapa anterior en relacion al estado e
     * la etapa actual y realiza la asignacion de los campos ESTADO y
     * ESTADOETAPA al registro que se esta trabajando
     * 
     * @param estadoActual
     * Valor de estado de la etapa actual
     * @param rowNum
     * Registro en el que se esta trabajando
     * @param i
     * indicador del ciclo que recorre los registros del subformulario
     * "transaccionSubEtapa"
     * @param estadoAnt
     * Valor de estado de la etapa anterior
     */
    private void evaluarEtapaAnteriorAux(String estadoActual, int rowNum,
        int i, String estadoAnt) {
        if (!"C".equals(estadoAnt) && "C".equals(estadoActual)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2233"));
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstado, estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estAnterior);
            indicadorReturn = true;
        }
        if ("C".equals(estadoActual) && (i == (rowNum + 1))) {
            listaTransaccionsubetapa.get(i).getCampos().put(cEstado,
                            "A");
            listaTransaccionsubetapa.get(i).getCampos()
                            .put(cEstadoEtapa, "A");
            estActualAux = "A";
        }
        if ("A".equals(estActualAux) && (i != (rowNum + 1))) {
            listaTransaccionsubetapa.get(i).getCampos().put(cEstado,
                            "P");
            listaTransaccionsubetapa.get(i).getCampos()
                            .put(cEstadoEtapa, "P");
        }
    }

    /**
     * Evalua si la etapa actual se encuentra abierta o suspendida y
     * realiza la asignacion de los campos ESTADO y ESTADOETAPA al
     * registro que se esta trabajando
     * 
     * @param estadoActual
     * Valor de estado de la etapa actual
     * @param rowNum
     * Registro en el que se esta trabajando
     * @param i
     * indicador del ciclo que recorre los registros del subformulario
     * "transaccionSubEtapa"
     */
    private void evaluarEtapaSiguiente(String estadoActual, int rowNum, int i) {
        if (i > rowNum) {
            String estadoEtSig = SysmanFunciones.nvl(listaTransaccionsubetapa
                            .get(i).getCampos().get(cEstadoEtapa), "")
                            .toString();
            if ("A".equals(estadoEtSig)) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(cTb2229));
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstado, estAnterior);
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstadoEtapa, estAnterior);
                indicadorReturn = true;
            }
            if ("S".equals(estadoEtSig) && "S".equals(estadoActual)) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TB_TB2230"));
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstado, estAnterior);
                listaTransaccionsubetapa.get(rowNum).getCampos()
                                .put(cEstadoEtapa, estAnterior);
                indicadorReturn = true;
            }

            evaluarEtapaSiguienteActual(estadoActual, rowNum, i);
        }
    }

    /**
     * Evalua si la etapa actual se encuentra abierta o cerrada y
     * realiza la asignacion de los campos ESTADO y ESTADOETAPA al
     * registro que se esta trabajando
     * 
     * @param estadoActual
     * Valor de estado de la etapa actual
     * @param rowNum
     * Registro en el que se esta trabajando
     * @param i
     * indicador del ciclo que recorre los registros del subformulario
     * "transaccionSubEtapa"
     */
    private void evaluarEtapaSiguienteActual(String estadoActual, int rowNum,
        int i) {
        if ("A".equals(estadoActual)) {
            listaTransaccionsubetapa.get(i).getCampos().put(cEstado,
                            "P");
            listaTransaccionsubetapa.get(i).getCampos()
                            .put(cEstadoEtapa, "P");
        }
        if ("C".equals(estadoActual) && (i == (rowNum + 1))) {
            listaTransaccionsubetapa.get(i).getCampos().put(cEstado,
                            "A");
            listaTransaccionsubetapa.get(i).getCampos()
                            .put(cEstadoEtapa, "A");
            estActualAux = "A";
        }
        if ("A".equals(estActualAux) && (i != (rowNum + 1))) {
            listaTransaccionsubetapa.get(i).getCampos().put(cEstado,
                            "P");
            listaTransaccionsubetapa.get(i).getCampos()
                            .put(cEstadoEtapa, "P");
        }
    }

    /**
     * Validaciones que se realizan cuando el estado de la etapa
     * actual NO es SUSPENDIDO, asigna valor a los campos ESTADO y
     * ESTADOETAPA dentro del registro del subformulario
     * 
     * @param estadoActual
     * Estado de la etapa actual
     * @param estadoAnterior
     * Estado de la etapa anterior
     * @param rowNum
     * Registro en el que se esta trabajando
     */
    private void evaluarEstadoNoSuspendido(String estadoActual,
        String estadoAnterior, int rowNum) {

        if ("C".equals(estAnterior)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2235"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estAnterior);
            indicadorReturn = true;
        }
        else if ("P".equals(estadoActual) && "A".equals(estadoAnterior)) {
            // Activar Etapa
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estadoActual);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estadoActual);
        }

        if ("A".equals(estadoActual)
            && ("A".equals(estadoAnterior) || "P".equals(estadoAnterior))) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2236"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estAnterior);
            indicadorReturn = true;
        }
        else {
            evaluarEstadoNoSuspendidoAux(estadoActual, estadoAnterior, rowNum);
        }
    }

    /**
     * Metodo auxiliar al metodo evaluarEstadoNoSuspendido(), continua
     * con las validaciones de dicho metodo
     * 
     * @param estadoActual
     * Estado de la etapa actual
     * @param estadoAnterior
     * Estado de la etapa anterior
     * @param rowNum
     * Registro en el que se esta trabajando
     */
    private void evaluarEstadoNoSuspendidoAux(String estadoActual,
        String estadoAnterior, int rowNum) {
        if ("C".equals(estadoActual)
            && ("A".equals(estadoAnterior) || "P".equals(estadoAnterior))) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2237"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estAnterior);
            indicadorReturn = true;
        }
        else if ("C".equals(estAnterior)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2238"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estAnterior);
            indicadorReturn = true;
        }
        else if ("P".equals(estadoActual) && "C".equals(estadoAnterior)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2239"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estAnterior);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estAnterior);
            indicadorReturn = true;
        }
        else {
            // Modificar etapa
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cEstado,
                            estadoActual);
            listaTransaccionsubetapa.get(rowNum).getCampos()
                            .put(cEstadoEtapa, estadoActual);
        }

    }

    public void cambiarValor() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control fechaInicial
     * 
     */
    public void cambiarfechaInicial() {
        Date fechaIni = (Date) registroSubtransaccionSubEtapa.getCampos()
                        .get(cFechaInicial);
        Date fechaFin = (Date) registroSubtransaccionSubEtapa.getCampos()
                        .get(cFechaFinal);
        Date fechaPrecontrato = (Date) registro.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName());

        if (fechaIni.before(fechaPrecontrato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3747"));
            registroSubtransaccionSubEtapa.getCampos().put(cFechaInicial,
                            null);
        }

        if (!fechaIni.before(fechaFin)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(cTb560));
            registroSubtransaccionSubEtapa.getCampos().put(cFechaInicial,
                            null);
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechaFinal() {
        Date fechaIni = (Date) registroSubtransaccionSubEtapa.getCampos()
                        .get(cFechaInicial);
        Date fechaFin = (Date) registroSubtransaccionSubEtapa.getCampos()
                        .get(cFechaFinal);
        if (fechaFin.before(fechaIni)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB528"));
            registroSubtransaccionSubEtapa.getCampos().put(cFechaFinal, null);
            registroSubtransaccionSubEtapa.getCampos().put(cHora, null);
            fechaFinNull = true;
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarHora() {
        if (fechaFinNull) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2241"));
            registroSubtransaccionSubEtapa.getCampos().put(cHora, null);
        }
        else {
            try {
                Date dfechaFin = (Date) registroSubtransaccionSubEtapa
                                .getCampos().get(cFechaFinal);
                String hora = registroSubtransaccionSubEtapa.getCampos()
                                .get(cHora).toString().substring(11, 19);
                SimpleDateFormat sdf = new SimpleDateFormat(cFormatoFecha);
                String fechaFin = sdf.format(dfechaFin);
                hora = SysmanFunciones.concatenar(fechaFin, " ", hora);
                dfechaFin = SysmanFunciones.convertirAFechaHora(hora);
                registroSubtransaccionSubEtapa.getCampos().put(cHora,
                                dfechaFin);
                fechaFinNull = false;
            }
            catch (ParseException ex) {
                Logger.getLogger(TransaccionsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechaInicialC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Date fechaIni = (Date) listaTransaccionsubetapa.get(rowNum).getCampos()
                        .get(cFechaInicial);
        Date fechaFin = (Date) listaTransaccionsubetapa.get(rowNum).getCampos()
                        .get(cFechaFinal);
        Date fechaPrecontrato = (Date) registro.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName());
        if (fechaIni.before(fechaPrecontrato)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3747"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cFechaInicial,
                            null);
        }
        if (!fechaIni.before(fechaFin)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(cTb560));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cFechaInicial,
                            null);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechaFinalC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        Date fechaIni = (Date) listaTransaccionsubetapa.get(rowNum).getCampos()
                        .get(cFechaInicial);
        Date fechaFin = (Date) listaTransaccionsubetapa.get(rowNum).getCampos()
                        .get(cFechaFinal);
        if (fechaFin.before(fechaIni)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB528"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cFechaFinal,
                            null);
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cHora, null);
            fechaFinNull = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarHoraC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (fechaFinNull) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2241"));
            listaTransaccionsubetapa.get(rowNum).getCampos().put(cHora, null);
        }
        else {
            try {
                String hora = listaTransaccionsubetapa.get(rowNum).getCampos()
                                .get(cHora).toString().substring(11, 19);
                Date dfechaFin = (Date) listaTransaccionsubetapa.get(rowNum)
                                .getCampos().get(cFechaFinal);
                SimpleDateFormat sdf = new SimpleDateFormat(cFormatoFecha);

                if (dfechaFin != null) {

                    String fechaFin = sdf.format(dfechaFin);
                    hora = SysmanFunciones.concatenar(fechaFin, " ", hora);
                    dfechaFin = SysmanFunciones.convertirAFechaHora(hora);
                    listaTransaccionsubetapa.get(rowNum).getCampos().put(cHora,
                                    dfechaFin);
                    fechaFinNull = false;
                }
            }
            catch (ParseException ex) {
                Logger.getLogger(TransaccionsControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void activarEdicionTransaccionsubetapa(Registro reg) {
        indiceTransaccionsubetapa = reg.getIndice();
        estAnterior = SysmanFunciones.nvlStr(retornarString(reg, cEstadoEtapa),
                        "NO");

        bloqueaEstadoSub = false;

    }

    public void oprimirVariables(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaidEtapa(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubtransaccionSubEtapa.getCampos().put(cIdEtapa,
                        registroAux.getCampos().get(cIdEtapa));
        registroSubtransaccionSubEtapa.getCampos().put(cDescripcion,
                        registroAux.getCampos().get(cDescripcion));
        ordenEtapa = Integer.parseInt(
                        registroAux.getCampos().get(cOrden).toString());
    }

    public void seleccionarFilaidEtapaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cIdEtapa)
                        .toString();
        registroSubtransaccionSubEtapa.getCampos().put(cDescripcion,
                        registroAux.getCampos().get(cDescripcion));
        ordenEtapa = Integer.parseInt(
                        registroAux.getCampos().get(cOrden).toString());
    }

    public void seleccionarFilaidItem(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubtransaccionSubItems.getCampos().put(cIdElemento,
                        registroAux.getCampos().get(cIdElemento));
    }

    public void seleccionarFilaidItemE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cIdElemento).toString();
    }

    public void seleccionarFilaEstudioPrevio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cEstudioPrevio,
                        registroAux.getCampos().get(cEstudioPrevio));
        registro.getCampos().put(cNombreEstudio,
                        registroAux.getCampos().get(cNombreEstudio));
        cambiarEstudio = true;
    }

    /**
     * Calcula los valores totales que son visualizados en la parte
     * inferior del subformulario "transaccionSubItems"
     */
    public void calcularTotales() {

        Registro regAux;
        valorUnitTotal = "";
        valorIvaTotal = "";
        valorDescTotal = "";
        valorTotal = "";

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                        tipoContrato);
        params.put(TransaccionsControladorEnum.TRANSACCIONPAR.getValue(),
                        transaccion);
        params.put(TransaccionsControladorEnum.CODESTUDIO.getValue(),
                        codEstudio);

        try {
            regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionsControladorUrlEnum.URL003
                                                                            .getValue())
                                            .getUrl(), params));

            String formatoMoneda = "$ #,##0.00";
            valorUnitTotal = new java.text.DecimalFormat(formatoMoneda)
                            .format(retornarDouble(regAux,
                                            TransaccionsControladorEnum.VALUNITOTAL
                                                            .getValue()));
            valorIvaTotal = new java.text.DecimalFormat(formatoMoneda)
                            .format(retornarDouble(regAux,
                                            TransaccionsControladorEnum.VALIVATOTAL
                                                            .getValue()));
            valorDescTotal = new java.text.DecimalFormat(formatoMoneda)
                            .format(retornarDouble(regAux,
                                            TransaccionsControladorEnum.VALDESCTOTAL
                                                            .getValue()));
            valorTotal = new java.text.DecimalFormat(formatoMoneda)
                            .format(retornarDouble(regAux,
                                            TransaccionsControladorEnum.VALTOTAL
                                                            .getValue()));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Realiza el llamado al procedimiento
     * PCK_PRECONTRACTUAL1.PR_ACTUALIZARINFOPROPONENTES que realiza la
     * actualizacion o insercion de la informacion relacionada con el
     * proponente en las tablas PRE_REQUISITOS_ETAPA,PROPONENTE,
     * PRERREQUISITOS_PROPONENTE y PROPONENTE_ITEMINVENTARIO
     */
    public void actualizarInfoProponentes() {

        try {

            ejbPrecontractualUno.actualizarInfoProponentes(compania,
                            tipoContrato,
                            Long.parseLong(transaccion),
                            Integer.parseInt(consecutivoAct),
                            Integer.parseInt(consecutivoSig),
                            SessionUtil.getUser().getCodigo());

        }
        catch (NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        calcularTotales();
        // Evalua si aplica redondeo para IVA
        redonValorUnitarioIVA = getParametro(
                        "REDONDEAR UNITARIO CON IVA EN O.C.", "NO");

        redonValorUnitarioIVA = SysmanFunciones
                        .nvl(redonValorUnitarioIVA, "NO").toString();

        if ("SI".equalsIgnoreCase(redonValorUnitarioIVA)) {
            digRedoValorUnitarioIVA = getParametro(
                            "DIGITOS REDONDEO UNITARIO CON IVA O.C.", "0");
        }

        // Evalua si aplica redondeo al valor total
        redondeoTotal = getParametro("REDONDEAR VALOR TOTAL EN O.C.", "NO");

        if ("SI".equalsIgnoreCase(redondeoTotal)) {
            digRedonTotal = getParametro("DIGITOS REDONDEO VALOR TOTAL O.C.",
                            "0");
        }

        digRedoValorUnitarioIVA = SysmanFunciones
                        .nvl(digRedoValorUnitarioIVA, "0").toString();
        digRedonTotal = SysmanFunciones.nvl(digRedonTotal, "0").toString();

        // Evalua el parametro de Control de Dependencia para
        // ajustar la consulta del combo "Estudio Previo"

        controlDependencia = getParametro(
                        "CONTROLA DEPENDENCIA EN PRECONTRATOS", "NO");

        cargarlistaEstudioPrevio();
        evaluarPrivilegios();

        if ("C".equals(estadoVigencia)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2244"));
        }

        /*
         * FR419-AL_ABRIR Private Sub Form_Open(Cancel As Integer) -
         * Se agregaron los parametros para la apertura del contrato -
         * todos se deja con 2 decimales - Se cargan los valores de
         * reduccion y de iva de acuerdo a los paramentros existentes
         * en plan de compras Dim
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        try {
            precargarRegistro();
            registro.getCampos().put(cNomEstadoVig, nombEstadoVigencia);

            registroSubtransaccionSubEtapa.getCampos().put(cFechaInicial,
                            new Date());
            registroSubtransaccionSubEtapa.getCampos().put(cFechaFinal,
                            new Date());
            Date dfechaFin = (Date) registroSubtransaccionSubEtapa.getCampos()
                            .get(cFechaFinal);
            SimpleDateFormat sdf = new SimpleDateFormat(cFormatoFecha);
            String hora = sdf.format(dfechaFin);
            dfechaFin = SysmanFunciones.convertirAFecha(hora);
            registroSubtransaccionSubEtapa.getCampos().put(cHora, dfechaFin);
            fechaFinNull = false;
            bloqueaEstadoSub = true;
            if ((listaTransaccionsubetapa != null)
                && !listaTransaccionsubetapa.isEmpty()) {
                registroSubtransaccionSubEtapa.getCampos().put(cEstadoEtapa,
                                "P");
            }
            else {
                registroSubtransaccionSubEtapa.getCampos().put(cEstadoEtapa,
                                "A");
            }
            if (css != null) {
                nombreTipoContrato = retornarString(registro, cNomTipo);
                nombEstadoVigencia = retornarString(registro, cNomEstadoVig);
                estadoProceso = retornarString(registro, cEstado);
                rid = registro.getLlave();
                strConsecutivo = SysmanFunciones.padl(
                                retornarString(registro, cConsecutivo), 8,
                                "0");

                editableEstPrevio = verificarEstudioActivo();

                calcularTotales();
                if (!"i".equals(accion)
                    && SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                    cNumProceso)) {
                    registro.getCampos().put(cNumProceso,
                                    SysmanFunciones.concatenar(tipoContrato,
                                                    " - ", anio, " - ",
                                                    strConsecutivo));
                }
            }
            else {
                registro.getCampos().put(cCompania, compania);
                registro.getCampos().put(
                                GeneralParameterEnum.TIPOCONTRATO.getName(),
                                tipoContrato);
                registro.getCampos().put(cNomTipo, nombreTipoContrato);
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                anio);
                registro.getCampos().put(cNomEstadoVig, nombEstadoVigencia);
                registro.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                                new Date());
                registro.getCampos().put(cEstado, "AC");
                cambiarEstudio = false;
                editableEstPrevio = true;
                bloqueaEstado = true;
                listaTransaccionsubetapa = null;
                listaTransaccionsubitems = null;
            }

        }
        catch (ParseException ex) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean insertarAntes() {
        try {

            String manejaConsecutivoUnico = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA CONSECUTIVO UNICO PROCESOS PRECONTRACTUAL",
                            SessionUtil.getModulo(),
                            new Date(), true);

            manejaConsecutivoUnico = manejaConsecutivoUnico == null ? "NO"
                : manejaConsecutivoUnico;

            if ("SI".equals(manejaConsecutivoUnico)) {
                consecutivoN = ejbSysmanUtil.generarSiguienteConsecutivo(
                                cTransaccion,
                                SysmanFunciones.concatenar(" COMPANIA = ''",
                                                compania, "''  "),
                                cConsecutivo);
            }

            else {

                consecutivoN = ejbSysmanUtil.generarConsecutivoConValorInicial(
                                cTransaccion,
                                SysmanFunciones.concatenar(" COMPANIA = ''",
                                                compania,
                                                "'' AND TIPOCONTRATO = ''",
                                                tipoContrato, "''  "),
                                cConsecutivo,
                                "1");
            }

            String lconsecutivo = SysmanFunciones
                            .padl(String.valueOf(consecutivoN), 8, "0");
            registro.getCampos().put(cConsecutivo, consecutivoN);
            registro.getCampos().put(cNumProceso, SysmanFunciones.concatenar(
                            tipoContrato, " - ", anio, " - ", lconsecutivo));
            return true;

        }
        catch (SystemException ex) {
            Logger.getLogger(TransaccionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            return false;
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        bloqueaEstado = false;

        try {
            ejbPrecontractualUno.crearDetallesProceso(compania,
                            tipoContrato,
                            consecutivoN,
                            (Date) registro.getCampos().get("FECHA"),
                            SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().remove(cNomTipo);
        registro.getCampos().remove(
                        TransaccionsControladorEnum.NOM_ESTADO.getValue());
        registro.getCampos().remove(GeneralParameterEnum.COD_ESTUDIO.getName());
        registro.getCampos().remove(cNombreEstudio);
        registro.getCampos().remove(cDescripcion);
        registro.getCampos().remove(cNomEstadoVig);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(cNomEstadoVig, nombEstadoVigencia);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        String elementos = null;
        String consecutivoTransaccion = registro.getCampos().get(cConsecutivo)
                        .toString();

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(TransaccionsControladorEnum.TIPO_CONTRATO.getValue(),
                        tipoContrato);
        params.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        consecutivoTransaccion.substring(7));

        try {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionsControladorUrlEnum.URL004
                                                                            .getValue())
                                            .getUrl(), params));

            elementos = retornarString(regAux,
                            TransaccionsControladorEnum.ELEMENTOS.getValue());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (!"0".equals(elementos)) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2245"));
            return false;
        }
        else {
            return true;
        }
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * De acuerdo a los valores recibidos por parametro de
     * estadoVigencia, estadoProceso y desdeMonitor, configura los
     * privilegios de edicion en el formulario
     */
    private void evaluarPrivilegios() {
        if (rid != null) {
            strConsecutivo = SysmanFunciones.padl(
                            retornarString(registro, cConsecutivo), 8, "0");

            if (!"C".equals(estadoVigencia)) {
                accion = "m";
            }
            else {
                accion = "v";
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2244"));
            }
            if ("AC".equals(estadoProceso)) {
                accion = "m";
            }

            if ("CE".equals(estadoProceso)) {
                editableEstPrevio = true;
            }

            if ("true".equals(desdeMonitor)) {
                accion = "v";
            }

        }
    }

    /**
     * Evalua si el formulario ha sido redirecciondo desde el
     * formulario "MonitoretapasControlador" y permite la redireccion
     * al mismo
     */
    private void validarDesdeMonitor() {
        if ("true".equals(desdeMonitor)) {
            accion = "v";
            varVolver = true;
            modificar = false;
        }
    }

    /**
     * Obtiene la cantidad de items en inventario por estudio previo
     * 
     * @param estudioPrevio
     * Estudio previo que se quiere analizar
     * @return Cantidad de Items
     */
    private int evaluarCantidadItems(int estudioPrevio) {
        int items = 0;
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(TransaccionsControladorEnum.ESTUDIOPREVIO.getValue(),
                        estudioPrevio);

        try {
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TransaccionsControladorUrlEnum.URL006
                                                                            .getValue())
                                            .getUrl(), params));
            items = Integer.parseInt(
                            rs.getCampos().get("ELEMENTOS").toString());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return items;
    }

    /**
     * Realiza las validaciones para bloquear el combo de estudio
     * previo, teniendo en cuenta el estado del proceso, y la cantidad
     * de items disponibles en iventario por estudio previo
     * seleccionado
     * 
     * @return Verdadero si cumple las condiciones
     */
    public boolean verificarEstudioActivo() {
        boolean bloqueado;
        String estadoActual = registro.getCampos().get("ESTADO")
                        .toString();
        int estudioPrevio = retornarEntero(registro, "ESTUDIOPREVIO");
        int cntItems = evaluarCantidadItems(estudioPrevio);

        if ("AC".equals(estadoActual)) {
            bloqueado = estudioPrevio != 0 && cntItems > 0;
        }
        else {
            bloqueado = true;
        }

        return bloqueado;
    }

    public boolean isVarVolver() {
        return varVolver;
    }

    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    public List<Registro> getListaTransaccionsubetapa() {
        return listaTransaccionsubetapa;
    }

    public void setListaTransaccionsubetapa(
        List<Registro> listaTransaccionsubetapa) {
        this.listaTransaccionsubetapa = listaTransaccionsubetapa;
    }

    public List<Registro> getListaTransaccionsubitems() {
        return listaTransaccionsubitems;
    }

    public void setListaTransaccionsubitems(
        List<Registro> listaTransaccionsubitems) {
        this.listaTransaccionsubitems = listaTransaccionsubitems;
    }

    public RegistroDataModelImpl getListaidEtapa() {
        return listaidEtapa;
    }

    public void setListaidEtapa(RegistroDataModelImpl listaidEtapa) {
        this.listaidEtapa = listaidEtapa;
    }

    public RegistroDataModelImpl getListaidEtapaE() {
        return listaidEtapaE;
    }

    public void setListaidEtapaE(RegistroDataModelImpl listaidEtapaE) {
        this.listaidEtapaE = listaidEtapaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaidItem() {
        return listaidItem;
    }

    public void setListaidItem(RegistroDataModelImpl listaidItem) {
        this.listaidItem = listaidItem;
    }

    public RegistroDataModelImpl getListaidItemE() {
        return listaidItemE;
    }

    public void setListaidItemE(RegistroDataModelImpl listaidItemE) {
        this.listaidItemE = listaidItemE;
    }

    public RegistroDataModelImpl getlistaEstudioPrevio() {
        return listaEstudioPrevio;
    }

    public void setlistaEstudioPrevio(
        RegistroDataModelImpl listaEstudioPrevio) {
        this.listaEstudioPrevio = listaEstudioPrevio;
    }

    public Registro getRegistroSubtransaccionSubEtapa() {
        return registroSubtransaccionSubEtapa;
    }

    public void setRegistroSubtransaccionSubEtapa(
        Registro registroSubtransaccionSubEtapa) {
        this.registroSubtransaccionSubEtapa = registroSubtransaccionSubEtapa;
    }

    public Registro getRegistroSubtransaccionSubItems() {
        return registroSubtransaccionSubItems;
    }

    public void setRegistroSubtransaccionSubItems(
        Registro registroSubtransaccionSubItems) {
        this.registroSubtransaccionSubItems = registroSubtransaccionSubItems;
    }

    public String getEtVigencia() {
        return etVigencia;
    }

    public void setEtVigencia(String etVigencia) {
        this.etVigencia = etVigencia;
    }

    public String getEtEstadoVig() {
        return etEstadoVig;
    }

    public void setEtEstadoVig(String etEstadoVig) {
        this.etEstadoVig = etEstadoVig;
    }

    public String getTituloSubEtapa() {
        return tituloSubEtapa;
    }

    public void setTituloSubEtapa(String tituloSubEtapa) {
        this.tituloSubEtapa = tituloSubEtapa;
    }

    public String getTituloSubProponente() {
        return tituloSubProponente;
    }

    public void setTituloSubProponente(String tituloSubProponente) {
        this.tituloSubProponente = tituloSubProponente;
    }

    public String getValorUnitTotal() {
        return valorUnitTotal;
    }

    public void setValorUnitTotal(String valorUnitTotal) {
        this.valorUnitTotal = valorUnitTotal;
    }

    public String getValorIvaTotal() {
        return valorIvaTotal;
    }

    public void setValorIvaTotal(String valorIvaTotal) {
        this.valorIvaTotal = valorIvaTotal;
    }

    public String getValorDescTotal() {
        return valorDescTotal;
    }

    public void setValorDescTotal(String valorDescTotal) {
        this.valorDescTotal = valorDescTotal;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public boolean isHeredaProponente() {
        return heredaProponente;
    }

    public void setHeredaProponente(boolean heredaProponente) {
        this.heredaProponente = heredaProponente;
    }

    public boolean isBloqueaEstado() {
        return bloqueaEstado;
    }

    public void setBloqueaEstado(boolean bloqueaEstado) {
        this.bloqueaEstado = bloqueaEstado;
    }

    public boolean isBloqueaEstadoSub() {
        return bloqueaEstadoSub;
    }

    public void setBloqueaEstadoSub(boolean bloqueaEstadoSub) {
        this.bloqueaEstadoSub = bloqueaEstadoSub;
    }

    public boolean isCambiarEstudio() {
        return cambiarEstudio;
    }

    public void setCambiarEstudio(boolean cambiarEstudio) {
        this.cambiarEstudio = cambiarEstudio;
    }

    public boolean isEditableEstPrevio() {
        return editableEstPrevio;
    }

    public void setEditableEstPrevio(boolean editableEstPrevio) {
        this.editableEstPrevio = editableEstPrevio;
    }

    public boolean isBloqueaCampos() {
        return bloqueaCampos;
    }

    public void setBloqueaCampos(boolean bloqueaCampos) {
        this.bloqueaCampos = bloqueaCampos;
    }

    public int getIndiceTransaccionsubetapa() {
        return indiceTransaccionsubetapa;
    }

    public void setIndiceTransaccionsubetapa(int indice) {
        this.indiceTransaccionsubetapa = indice;
    }

    public String getStrConsecutivo() {
        return strConsecutivo;
    }

    public void setStrConsecutivo(String strConsecutivo) {
        this.strConsecutivo = strConsecutivo;
    }

    public String getEstadoVigencia() {
        return estadoVigencia;
    }

    public void setEstadoVigencia(String estadoVigencia) {
        this.estadoVigencia = estadoVigencia;
    }
    
    public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
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
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private int retornarEntero(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? 0
            : Integer.parseInt(reg.getCampos().get(campo).toString());
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param rs
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return
     */
    private double retornarDouble(Registro rs, String campo) {
        return SysmanFunciones.validarCampoVacio(rs.getCampos(), campo)
            ? 0
            : Double.parseDouble(rs.getCampos().get(campo).toString());
    }

    /**
     * Obtiene el valor almacenado en la base de datos para el
     * parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

}
