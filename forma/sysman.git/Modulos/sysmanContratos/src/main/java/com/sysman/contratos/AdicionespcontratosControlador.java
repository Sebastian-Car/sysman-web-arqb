package com.sysman.contratos;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.ejb.EjbContratosCeroRemote;
import com.sysman.contratos.ejb.EjbContratosDosRemote;
import com.sysman.contratos.ejb.EjbContratosUnoRemote;
import com.sysman.contratos.enums.AdicionespcontratosControladorEnum;
import com.sysman.contratos.enums.AdicionespcontratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.PcontratosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
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

/**
 *
 * @author dsuesca
 * @version 1, 12/01/2016
 *
 * @author ybecerra
 * @version 2, 23/08/2017, proceso de Refactoring
 *
 */
@ManagedBean
@ViewScoped

public class AdicionespcontratosControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante definida para almacenar el codigo del modulo por el cual se ingresa en la aplicacion
     */
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String modeloPlantilla;
    /**
     * Variable que valida si el dialogo cambiar consecutivo se hace visible o no
     */
    private boolean muestraCambiarConsecutivo;
    /**
     * Variable que almacena el codigo digitado por el usuario en el dialogo que se abre del boton cambiar
     */
    private String nuevoConsecutivo;
    /**
     * Variable que valida si el boton cambiar esta activo o inactivo
     */
    private boolean cambiarActivo;
    private String plantilla;
    private String numeroDoc;
    private String anio;
    private String tipoContrato;
    private String tipoAfectado;
    private String nombreContrato;
    private String vigencia;
    private String modelosPlantilla;
    private String claseDisponibilidaD;
    private String nombreTercero;
    private String nombreDependencia;
    private String nombCesion;
    private String nombreAuxiliar;
    private boolean mostrarDialogoCesion;
    private String etiquetaMensaje;
    private String tituloMensajes;
    private Registro registroAuxTercero;
    private String nombreOrdenador;
    private String globalTipoPPto = "";
    private boolean bloqueadoConsecutivoAdiciones;
    private Double vValorTotalContr;
    private BigDecimal valorFinalContrato;
    private Integer vNumeracionunica;
    private boolean verificacion306;
    private boolean visibleEnviarNominaCesion;
    private boolean visibleNitCesion;
    private boolean visibleNombreCesion;
    private boolean visibleRegistroCesion;
    private boolean visibleAfectaItems;
    private boolean visibleFormaDePago;
    private boolean bloqueadoTercero;
    private boolean bloqueadoObjetoContrato;
    private boolean bloqueadofecha;
    private boolean subpCONTRATOAllowAdditions;
    private boolean subpCONTRATOAllowDeletions;
    private boolean subpCONTRATOAllowEdits;
    private boolean bloqueadoComando89;
    private boolean bloqueadoComando291;
    private boolean bloqueadoConfirmar;
    private boolean bloqueadoFuenteRecursos;
    private boolean bloqueadoAuxiliar;
    private boolean bloqueadodestino;
    private boolean bloqueadoValorTotalF;
    private boolean bloqueadofechaFinal;
    private boolean visibleVerificacion306;
    private String lblTitulo;
    private String vformato;
    private String vTxtTipoModelo;
    private String vTxtRedondeoGranTotal;
    private String vTxtDigitosRedondeoGranTotal;
    private boolean manejaActualizacionPlanDeCompras;
    private String botonOrigenAlerta;
    private String fechaFormato;
    private boolean retorno = true;
    private String maneja;
    private String tipoFormato;
    private Double txtSubtotal;
    private Double txtIva;
    private Double txtAjustealpeso;
    private Double txtTotalDescuento;
    private Double txtTotal;
    private String modeloPlantillaPolizas;
    private Date fechaFormatoPolizas;
    private Date fechaf;
    private boolean reemplazar;
    private String auxiliar;
    private String disponibilidadConcatenado;
    private String reservaConcatenado;
    private boolean bloqueaConAdi;
    private boolean calcularMismoDia;
    private boolean mostrarDialogoConfirmar;
    /**
     * Indica si el proceso de creacion de novedades permite mostrar los adjuntos.
     * true: los adjuntos son visibles.
     * false: los adjuntos no se muestran.
     */
    private boolean visibleAdjuntos;  
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaClaseNovedad;
    private List<Registro> listaNumeroPpto;
    private List<Registro> listaTipoPpto;
    private List<Registro> listaTipo;
    private List<Registro> listaCuadroCombinado25;
    private List<Registro> listaFuenteRecursos;
    private List<Registro> listaDestino;
    private List<Registro> listaCEDULAINTERVENTOR;
    private List<Registro> listaSeleccionados;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaClaseDisponibilidad;
    private RegistroDataModelImpl listaInterventor;
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaNumeroAfectado;
    private RegistroDataModelImpl listaNitCesion;
    private RegistroDataModelImpl listaModelos;
    private RegistroDataModelImpl listaOrdenador;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaPlantilla;
    private RegistroDataModelImpl listaRubros;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    private static final String CTEVIGENCIA = "vigencia";
    private static final String CTETIPOCONTRATO = "tipoContrato";
    private static final String CTETIPOAFECTADO = "tipoAfectado";
    private static final String CTERETORNOFORMULARIO = "retornoFormulario";
    private static final String CTERETORNA = "retorna";
    private static final String CTENUMEROORDEN = "numeroOrden";
    private static final String CTENOMBRECONTRATO = "nombreContrato";
    private static final String CTEMENSAJE = "mensaje";
    private static final String CTEVALORTOTAL = "VALORTOTAL";
    private static final String CTETIPOAFECTADOCAMPO = "TIPOAFECTADO";
    private static final String CTETERCERO = "TERCERO";
    private static final String CTETBTB2045 = "TB_TB2045";
    private static String CTETITULOSYSMAN = "Software";
    private static final String CTESUCURSAL = "SUCURSAL";
    private static final String CTERUBRORESERVA = "RUBRORESERVA";
    private static final String CTETBTB1323 = "TB_TB1323";
    private static final String CTEPROYECTO = "PROYECTO";
    private static final String CTEORDENADORMODIF = "ORDENADOR_MODIF";
    private static final String CTEOBJETOCONTRATO = "OBJETOCONTRATO";
    private static final String CTENUMEROAFECTADOCAMPO = "NUMEROAFECTADO";
    private static final String CTENUMERO = "NUMERO";
    private static final String CTENOMBRE = "NOMBRE";
    private static final String CTENITCESION = "NITCESION";
    private static final String CTEMSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    private static final String CTEMSM_PROCESO_EJECUTADO = "MSM_PROCESO_EJECUTADO";
    private static final String CTECODIGO = "CODIGO";
    private static final String CTECLASEDISPONIBILIDAD = "CLASEDISPONIBILIDAD";
    private static final String CTECLASENOVEDAD = "CLASENOVEDAD";
    private static final String CTECONSECUTIVOADICIONES = "CONSECUTIVOADICIONES";
    private static final String CTEFECHA = "FECHA";
    private static final String CTEDISPONIBILIDAD = "DISPONIBILIDAD";
    private static final String CTEFECHAFINAL = "FECHAFINAL";
    private static final String CTEDURACION = "DURACION";
    private static final String CTEDEPENDENCIA = "DEPENDENCIA";
    private static final String CTECLASEORDEN = "CLASEORDEN";
    private static final String CTEMODCONSECUADICIONES = "MODIFICA CONSECUTIVO ADICIONES";
    private static final String CTEFECHAINICIAL = "FECHAINICIAL";
    private static final String CTEEXPEDIDACEDULA = "EXPEDIDACEDULA";
    private static final String CTEMANPROGRFORMASPAGO = "MANEJA PROGRAMACION DE FORMAS DE PAGO";
    private static final String CTERUBRO = "RUBRO";
    private static final String CTETIPODIA = "TIPODIA";
    private static final String CTEFECHAFINALMODIF = "FECHAFINALMODIF";
    private static final String CTEFECHAFINALACTUAL = "FECHAFINALACTUAL";
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbContratosCeroRemote ejbContratosCero;

    @EJB
    private EjbContratosUnoRemote ejbContratosUno;

    @EJB
    private EjbContratosDosRemote ejbContratosDos;

    private Map<String,Object> parametroswf;
	private boolean verCerrar = true;
    /**
     * Crea una nueva instancia de AdicionespcontratosControlador
     */
    @SuppressWarnings("unchecked")
    public AdicionespcontratosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        CTETITULOSYSMAN = JsfUtil.getTituloPaginaEmpresaParametrizada()+CTETITULOSYSMAN;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ADICIONESPCONTRATOS_CONTROLADOR
                            .getCodigo();
            registro = new Registro(new HashMap<String, Object>());
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {

                anio = (String) parametrosEntrada.get("anio");
                tipoContrato = (String) parametrosEntrada.get(CTETIPOCONTRATO);

                tipoAfectado = (String) parametrosEntrada.get(CTETIPOAFECTADO);
                nombreContrato = (String) parametrosEntrada
                                .get(CTENOMBRECONTRATO);
                vigencia = (String) parametrosEntrada.get(CTEVIGENCIA);
                parametroswf = (Map<String,Object>) parametrosEntrada.get("parametroswf");
    			if(parametroswf != null) {
    				verCerrar = false;
    				SessionUtil.setSessionVar("modulo", "9");
    		        modulo = SessionUtil.getModulo();
    			}
                if (parametrosEntrada.get("rid") != null)
                {
                    rid = (Map<String, Object>) parametrosEntrada
                                    .get("rid");
                }

            }

            if (SessionUtil.getSessionVar(CTEVALORTOTAL) != null)
            {
                registro.getCampos().put(CTEVALORTOTAL,
                                Double.parseDouble(SessionUtil
                                                .getSessionVar(CTEVALORTOTAL)
                                                .toString()));
                SessionUtil.removeSessionVar(CTEVALORTOTAL);
            }
            tipoFormato = "9"; // viene definido como ruta del modelo
            // desde
            // acces, ** Se cambia para que apunte a los tipos "MODELO
            // POLIZAS" para tener nificadas las polizas y mostrar
            // todos los modelos de plizas

            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(AdicionespcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {

        cargarListaTercero();
        cargarListaNumeroAfectado();
        cargarListaModelos();
        cargarListaOrdenador();
        cargarListaAuxiliar();
        cargarListaDependencia();
        cargarListaClaseNovedad();
        cargarListaTipoPpto();
        cargarListaFuenteRecursos();
        cargarListaDestino();
        cargarListaInterventor();
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        cargarListaRubros();
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        cargarListaRubros();
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
    	if(parametroswf != null) {
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
			
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");
		}
        tabla = GenericUrlEnum.ORDENDECOMPRA.getTable();
        buscarLlave();
        asignarOrigenDatos();
        inicializarVariablesAuxiliaresUno();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     */
    @Override
    public void asignarOrigenDatos()
    {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContrato);
        parametrosListado.put(AdicionespcontratosControladorEnum.TIPOAFECTADO
                        .getValue(), tipoAfectado);
        parametrosListado.put(AdicionespcontratosControladorEnum.FECHAORDEN
                        .getValue(), vigencia);
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL296
                                                        .getValue());
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL303
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL434
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL438
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL448
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaClaseNovedad
     */
    public void cargarListaClaseNovedad()
    {
        try
        {
            listaClaseNovedad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL844
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaDependencia
     */
    public void cargarListaDependencia()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL9771
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaNumeroPpto
     */
    public void cargarListaNumeroPpto()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AdicionespcontratosControladorEnum.TIPOPPTO.getValue(),
                        globalTipoPPto);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.CLASEORDEN.getName()));
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));
        param.put(AdicionespcontratosControladorEnum.TIPOAFECTADO.getValue(),
                        tipoAfectado);
        param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO.getValue(),
                        registro.getCampos()
                                        .get(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                                                        .getValue()));

        try
        {
            listaNumeroPpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL8975
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista listaTipoPpto
     */
    public void cargarListaTipoPpto()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaTipoPpto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL938
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaTipo
     */
    public void cargarListaTipo()
    {
        try
        {
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL960
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaClaseDisponibilidad
     */
    public void cargarListaClaseDisponibilidad()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL11590
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaClaseDisponibilidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaFuenteRecursos
     */
    public void cargarListaFuenteRecursos()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaFuenteRecursos = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL993
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaDestino
     */
    public void cargarListaDestino()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        try
        {
            listaDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL9204
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     * Carga la lista listaInterventor
     */
    public void cargarListaInterventor()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL1036
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaInterventor = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AdicionespcontratosControladorEnum.NIT.getValue());

    }

    /**
     *
     * Carga la lista listaTercero
     */
    public void cargarListaTercero()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL685
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AdicionespcontratosControladorEnum.NIT.getValue());

    }

    /**
     *
     * Carga la lista listaNumeroAfectado
     */
    public void cargarListaNumeroAfectado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL10183
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoAfectado);
        param.put(GeneralParameterEnum.TERCERO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.TERCERO.getName()));
        param.put(GeneralParameterEnum.SUCURSAL.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName()));

        listaNumeroAfectado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.NUMERO.getName());

    }

    /**
     *
     * Carga la lista listaNitCesion
     */
    public void cargarListaNitCesion()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL685
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNitCesion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AdicionespcontratosControladorEnum.NIT.getValue());

        Map<String, Object> fields = new TreeMap<>();
        fields.put(AdicionespcontratosControladorEnum.NIT.getValue(),
                        registro.getCampos().get(CTENITCESION));

        try
        {
            nombCesion = listaNitCesion.getRegistroUnico(fields) == null
                ? ""
                : listaTercero.getRegistroUnico(fields).getCampos()
                                .get(CTENOMBRE).toString();
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Carga la lista listaModelos
     */
    public void cargarListaModelos()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL10879
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(AdicionespcontratosControladorEnum.TIPO.getValue(),
                        tipoFormato);
        listaModelos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaOrdenador
     */
    public void cargarListaOrdenador()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL11226
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaOrdenador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AdicionespcontratosControladorEnum.CEDULA.getValue());
    }

    /**
     *
     * Carga la lista listaAuxiliar
     */
    public void cargarListaAuxiliar()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL11968
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaPlantilla
     */
    public void cargarListaPlantilla()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL12331
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.FORMATO.getName(),
                        vformato);
        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaRubros
     *
     */
    public void cargarListaRubros()
    {

        claseDisponibilidaD = registro.getCampos()
                        .get(CTECLASEDISPONIBILIDAD) == null ? "DIS"
                            : registro.getCampos()
                                            .get(CTECLASEDISPONIBILIDAD)
                                            .toString();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AdicionespcontratosControladorUrlEnum.URL13109
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        claseDisponibilidaD);

        String[] llaveRubros = null;
        try
        {
            llaveRubros = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.COMPROBANTE_PPTAL.getTable());
            listaRubros = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            false, llaveRubros,
                            true);

        }
        catch (SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally
        {
            if (claseDisponibilidaD != null)
            {
                String resDis;
                if ("DIS".equals(claseDisponibilidaD))
                {
                    resDis = SysmanFunciones.nvl(registro.getCampos()
                                    .get(CTEDISPONIBILIDAD),
                                    "").toString();
                }
                else if ("RES".equals(claseDisponibilidaD))
                {
                    resDis = SysmanFunciones.nvl(
                                    registro.getCampos()
                                                    .get(CTERUBRORESERVA),
                                    "").toString();
                }
                else
                {
                    resDis = "";
                }

                resDis = resDis == null ? ""
                    : resDis;
                String[] listanumeros = resDis.split(",");
                for (int i = 0; i < listanumeros.length; i++)
                {
                    Registro r = new Registro();
                    r.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    r.getCampos().put("ANO", vigencia);
                    r.getCampos().put("TIPO", claseDisponibilidaD);
                    r.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                                    listanumeros[i]);
                    r.asignarLlave(llaveRubros);
                    listaRubros.getLlavesSeleccionadas().add(r.getLlave()
                                    .toString());

                    try
                    {
                        listaRubros.getSeleccionados().add(listaRubros
                                        .getRegistroUnico(r.getCampos()));
                    }
                    catch (SystemException e)
                    {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
            }

        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control NumeroAfectado
     *
     */
    public void cambiarNumeroAfectado()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarOrdenador()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarClaseDisponibilidad()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control fecha
     *
     */
    public void cambiarfecha()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control NitCesion
     *
     */
    public void cambiarNitCesion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Dia
     *
     */
    public void cambiarDia()
    {
        // <CODIGO_DESARROLLADO>
        validarDiasCalendario();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Calcula
     *
     */
    public void cambiarcalcula()
    {
        validarDiasCalendario();
    }

    /**
     * Metodo ejecutado al cambiar el control Duracion
     *
     */
    public void cambiarDuracion()
    {
        // <CODIGO_DESARROLLADO>

        int plazoEntrega = getIntVal(CTEDURACION);
        if (plazoEntrega < 0)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2084"));
            registro.getCampos().put(CTEDURACION, "");
            return;
        }

        if (registro.getCampos().get(CTETIPODIA) == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3697"));
            return;
        }

        if (!SysmanFunciones.nvl(registro.getCampos().get(CTETIPODIA), "")
                        .toString().isEmpty())
        {
            validarDiasCalendario();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     *
     */
    public void cambiarFechaInicial()
    {
        if (registro.getCampos().get(CTEFECHA) != null
            && !registro.getCampos().get(CTEFECHA).toString().isEmpty())
        {
            registro.getCampos().put(CTEFECHAFINAL,
                            registro.getCampos().get(CTEFECHA));
        }
    }

    /**
     * Metodo ejecutado al cambiar el control VALORTOTALF
     *
     */
    public void cambiarValorTotalF()
    {
        try
        {
            String claseNovedad = registro.getCampos()
                            .get(CTECLASENOVEDAD) == null
                                ? ""
                                : registro.getCampos()
                                                .get(CTECLASENOVEDAD)
                                                .toString();
            if ("D".equals(claseNovedad))
            {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                                tipoAfectado);
                param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                                .getValue(),
                                registro.getCampos()
                                                .get(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                                                                .getValue()));

                Registro rs;

                rs = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                AdicionespcontratosControladorUrlEnum.URL950
                                                                                .getValue())
                                                .getUrl(), param));

                vValorTotalContr = rs == null
                    || rs.getCampos().get(CTEVALORTOTAL) == null ? 0.0
                        : Double.parseDouble(rs.getCampos().get(CTEVALORTOTAL)
                                        .toString());
                vValorTotalContr = vValorTotalContr
                    + Double.parseDouble(registro.getCampos().get(CTEVALORTOTAL)
                                    .toString());
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Metodo ejecutado al cambiar el control ClaseNovedad
     *
     */
    public void cambiarClaseNovedad()
    {
        if (css == null)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2051"));
            return;
        }
        evaluarYCambiarClaseNovedad();
    }

    /**
     * Metodo ejecutado al cambiar el control Impreso
     *
     */
    public void cambiarImpreso()
    {
        bloquear();
    }

    /**
     * Metodo ejecutado al cambiar el control dialogo
     *
     */
    public void cambiardialogo()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo cambiarConsecutivo en la vista
     *
     */
    public void aceptarcambiarConsecutivo()
    {
        // <CODIGO_DESARROLLADO>
        String controlaTamanioNumeroContrato;
        try
        {
            controlaTamanioNumeroContrato = ejbSysmanUtl.consultarParametro(
                            compania, idioma.getString("TB_TB3730"), modulo,
                            new Date(), true);

            if ("SI".equals(controlaTamanioNumeroContrato)
                && nuevoConsecutivo.length() != 8)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2757"));
                muestraCambiarConsecutivo = false;
                return;
            }

            Registro rs;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);
            param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                            nuevoConsecutivo);
            param.put(GeneralParameterEnum.ANO.getName(), anio);

            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61658
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (rs == null)
            {
                ejbContratosCero.cambiarConsecutivoContrato(compania,
                                tipoContrato,
                                Integer.parseInt(anio),
                                Long.valueOf(registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName())
                                                .toString()),
                                Long.valueOf(nuevoConsecutivo),
                                SessionUtil.getUser().getCodigo());
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2758"));
            }

            muestraCambiarConsecutivo = false;

            Map<String, Object> nuevaLlave = css;
            nuevaLlave.put("KEY_NUMERO", nuevoConsecutivo);
            cargarRegistro(nuevaLlave, accion);

        }
        catch (SystemException e)
        {
            muestraCambiarConsecutivo = false;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control aportesTerceros
     *
     */
    public void retornarFormularioaportesTerceros()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        tipoContrato);

        try
        {
            registro.getCampos().put("APORTESTE_AD",
                            RegistroConverter.toRegistro(
                                            requestManager.get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            AdicionespcontratosControladorUrlEnum.URL1504
                                                                                            .getValue())
                                                            .getUrl(), param))
                                            .getCampos()
                                            .get(GeneralParameterEnum.TOTAL
                                                            .getName()));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Metodo ejecutado al cambiar el control ItemsModificacion
     *
     */
    public void retornarFormularioItemsModificacion(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     */
    public void aceptarconfirmar()
    {
        limpiarOrdenDeCompra();
        confirmarTransaccion();
        mostrarDialogoConfirmar = false;
    }

    /**
     *
     */
    public void cancelarconfirmar()
    {
        confirmarTransaccion();
        mostrarDialogoConfirmar = false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo dialogo en la vista
     *
     */
    public void aceptardialogo()
    {

        mostrarDialogoCesion = false;

        switch (botonOrigenAlerta)
        {
        case "selectTercero":
            actualizarComprobante();
            break;
        case "Registrocesion":
        	agregarRegistroNuevo(false);
            registrarCesion();
            break;
        case "pasarNomina":
            enviarNominaCesion();
            break;
        case "afectaItem":
            afectarItemYAsignarOrigenDatos();
            break;

        default:
            break;
        }
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString(CTEMSM_PROCESO_EJECUTADO));
    }

    /**
     *
     */
    private void limpiarOrdenDeCompra()
    {
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CTECLASEORDEN));
        fields.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CTENUMERO));
        fields.put(GeneralParameterEnum.CLASE.getName(), claseDisponibilidaD);

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL1979
                                                        .getValue());
        try
        {
            requestManager.delete(urlDelete.getUrl(),
                            fields);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo dialogo en la vista
     *
     */
    public void cancelardialogo()
    {
        // <CODIGO_DESARROLLADO>

        mostrarDialogoCesion = false;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaVencimiento
     *
     */
    public void cambiarFechaVencimiento()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     *
     */
    public void cambiarFechaFinal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control SeleccionarRequisiciones
     *
     */
    public void retornarFormularioSeleccionarRequisiciones()
    {

        if (retorno)
        {
            if (SessionUtil.getSessionVar(CTEMENSAJE) != null)
            {
                JsfUtil.agregarMensajeAlerta(
                                (String) SessionUtil.getSessionVar(CTEMENSAJE));
                SessionUtil.removeSessionVar(CTEMENSAJE);
                retorno = false;
                return;
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2063"));

            }
        }

        retorno = true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event)
    {
        registroAuxTercero = (Registro) event.getObject();

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CTENUMEROAFECTADOCAMPO)
            || "0".equals(registro.getCampos().get(CTENUMEROAFECTADOCAMPO)))
        {
            registro.getCampos().put(CTETERCERO,
                            registroAuxTercero.getCampos().get("NIT"));
            registro.getCampos().put(CTESUCURSAL,
                            registroAuxTercero.getCampos().get(CTESUCURSAL));
            nombreTercero = (String) registroAuxTercero.getCampos()
                            .get(CTENOMBRE);
            cargarListaNumeroAfectado();
            return;
        }
        else
        {
            if (css != null)
            {
                botonOrigenAlerta = "selectTercero";
                etiquetaMensaje = idioma.getString("TB_TB3471");
                tituloMensajes = CTETITULOSYSMAN;
                mostrarDialogoCesion = true;
            }
            else
            {
                registro.getCampos().remove(CTENUMEROAFECTADOCAMPO);
                cargarListaAuxiliar();
            }
        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaDependencia
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTEDEPENDENCIA,
                        registroAux.getCampos().get(CTECODIGO));
        nombreDependencia = registroAux.getCampos().get(CTENOMBRE).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaInterventor
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInterventor(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("INTERVENTOR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put("CEDULAINTERVENTOR",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSALINTERVENTOR",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantilla(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        plantilla = registroAux.getCampos().get(CTECODIGO).toString();

        try
        {
            fechaFormato = SysmanFunciones.formatearFecha(
                            SysmanFunciones.convertirAFecha(
                                            (String) registroAux.getCampos()
                                                            .get(CTEFECHA),
                                            "dd/MM/yyyy"));

        }
        catch (ParseException ex)
        {

            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaClaseDisponibilidad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseDisponibilidad(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTECLASEDISPONIBILIDAD,
                        registroAux.getCampos().get(CTECODIGO));
        claseDisponibilidaD = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CLASE.getName()),
                                        "")
                        .toString();

        cargarListaRubros();
        listaRubros.getLlavesSeleccionadas().clear();
        listaRubros.getSeleccionados().clear();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaNumeroAfectado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroAfectado(SelectEvent event)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AdicionespcontratosControladorEnum.TIPOAFECTADO.getValue(),
                        tipoAfectado);
        param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO.getValue(),
                        registro.getCampos()
                                        .get(CTENUMEROAFECTADOCAMPO));

        try
        {
            validarCssYCargarListado(event);

            if (!((BigInteger) registro.getCampos().get(CTENUMEROAFECTADOCAMPO))
                            .toString().isEmpty())
            {

                actualizarOrdenCompra();
            }

            boolean modificaciones = ejbSysmanUtl.consultarParametro(compania,
                            CTEMODCONSECUADICIONES, modulo, new Date(),
                            true) == null
                                ? false
                                : "SI".equals(ejbSysmanUtl.consultarParametro(
                                                compania,
                                                CTEMODCONSECUADICIONES, modulo,
                                                new Date(),
                                                true));

            if (modificaciones)
            {
                bloqueaConAdi = false;
            }
            else
            {
                bloqueaConAdi = true;
            }

            registro.getCampos().put(CTECONSECUTIVOADICIONES,
                            ejbContratosUno.getConsecutivoOrdenCompra(
                                            compania, tipoAfectado,
                                            Long.valueOf(registro
                                                            .getCampos()
                                                            .get(CTENUMEROAFECTADOCAMPO)
                                                            .toString())));

            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO));
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoAfectado);
            param.put(GeneralParameterEnum.TERCERO.getName(),
                            registro.getCampos().get(CTETERCERO));
            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            registro.getCampos().get(CTESUCURSAL));
            Registro valorTotal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL1450
                                                                            .getValue())
                                            .getUrl(), param));

            if (valorTotal != null
                && valorTotal.getCampos().get(CTEVALORTOTAL) != null)
            {
                vValorTotalContr = Double.parseDouble((String) valorTotal
                                .getCampos().get(CTEVALORTOTAL));
            }
            else
            {
                vValorTotalContr = 0.0;
            }
            
            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoAfectado);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString().isEmpty() ? "0"
                                                : registro
                                                                .getCampos()
                                                                .get(CTENUMEROAFECTADOCAMPO)
                                                                .toString());
            Registro rs;

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL3857
                                                                            .getValue())
                                            .getUrl(), param));
            
            fechaf = (Date) rs.getCampos().get("FECHAFINALIZACION"); 
            Date fechaI = (Date) rs.getCampos().get("FECHA"); 
            
            registro.getCampos().put("FECHAFINAL",fechaf);
            registro.getCampos().put("FECHAINICIAL",fechaI);
                  
            agregarRegistroNuevo(false);
            
            

        }
        catch (SystemException ex)
        {

            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaNitCesion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNitCesion(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTENITCESION,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSALCESION",
                        registroAux.getCampos().get(CTESUCURSAL));

        nombCesion = registroAux.getCampos().get(CTENOMBRE).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaModelos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModelos(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        modeloPlantillaPolizas = registroAux.getCampos()
                        .get(CTECODIGO).toString();

        fechaFormatoPolizas = (Date) registroAux.getCampos()
                        .get(CTEFECHA);

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaOrdenador
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaOrdenador(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTEORDENADORMODIF,
                        registroAux.getCampos().get("CEDULA"));
        nombreOrdenador = registroAux.getCampos().get(CTENOMBRE).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaAuxiliar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CTEPROYECTO,
                        registroAux.getCampos().get(CTECODIGO));
        nombreAuxiliar = (String) registroAux.getCampos().get("NOMBREPROYECTO");
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaRubros
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaRubros(SelectEvent event)
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton ItemsModificacion en la vista
     *
     */
    public void oprimirItemsModificacion()
    {
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        SessionUtil.setSessionVar(CTERETORNOFORMULARIO, CTERETORNA);
        // Parametros que deben eliminarse de
        parametros.put(CTENUMEROORDEN,
                        registro.getCampos().get(CTENUMERO).toString());
        parametros.put("numeroAfectado", registro.getCampos()
                        .get(CTENUMEROAFECTADOCAMPO).toString());
        parametros.put("dependencia",
                        registro.getCampos().get(CTEDEPENDENCIA).toString());
        parametros.put("maneja", maneja);
        // Parametros que se deben devolver
        parametros.put("rid", registro.getLlave());
        parametros.put("anio", String.valueOf(anio));
        parametros.put(CTETIPOCONTRATO, String.valueOf(tipoContrato));
        parametros.put(CTENOMBRECONTRATO, nombreContrato);
        parametros.put(CTEVIGENCIA, vigencia);
        parametros.put(CTETIPOAFECTADO, tipoAfectado);

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBPCONTRATOADI_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        // se agrega
        // variable que afirma que el formulario se redireccion√≤,para
        // solucionar el problema de la lista LM5
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }
    
    /**
     * Metodo que se ejecuta al oprimir la opcion de ver adjuntos.
     * Carga un modal con los datos necesarios para mostrar los archivos
     * asociados al contrato actual.
     */
    public void oprimirVerAdjuntos() {
    	
        String[] campos = { "claseOrden", CTENUMEROORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CTENUMERO)) };
          SessionUtil.cargarModalDatos(
                          String.valueOf(GeneralCodigoFormaEnum.DIGITALIZACION_CONTRATOS_CONTROLADOR
                                          .getCodigo()),
                          modulo,
                          campos, valores);
        
    }
    
    /**
     *
     * Metodo ejecutado al oprimir el boton CmbModNo en la vista
     *
     */
    public void oprimirCmbModNo()
    {
        // <CODIGO_DESARROLLADO>
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.CLASEORDEN.getName()));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.NUMERO.getName()));
        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            AdicionespcontratosControladorUrlEnum.URL1585
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (!"0".equals(rs.getCampos()
                            .get(GeneralParameterEnum.CANTIDAD.getName())
                            .toString()))
            {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3808"));
                return;
            }
            else
            {

                muestraCambiarConsecutivo = true;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Importarpagos en la vista
     *
     */
    public void oprimirpagos()
    {
        String[] campos = { CTETIPOCONTRATO, "valorTotalContrato",
                            "numeroContrato" };
        String[] valores = { tipoContrato,
                             registro.getCampos().get(CTEVALORTOTAL).toString(),
                             registro.getCampos().get(CTENUMERO).toString() };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FORMADEPAGOS_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton cmdImpresora en la vista
     *
     */
    public void oprimircmdImpresora()
    {
        // <CODIGO_DESARROLLADO>
        if ((boolean) registro.getCampos().get("IMPRESO"))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2042"));
            return;
        }
        if ("R".equals(vTxtTipoModelo))
        {
            // DoCmd.OpenReport Me!fORMATO,
            // acViewNormal;************************** PENDIENTE POR
            // SABER QUE
            // INFORME IMPRIMIR
        }
        else
        {
            listaPlantilla.load();
            if (listaPlantilla.getDatasource().isEmpty())
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2043"));
                return;
            }
            else
            {
                if (plantilla.isEmpty())
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1884"));
                    return;
                }
                prepararYEnviarValores();
            }

        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Requisiciones en la vista
     *
     */
    public void oprimirSeleccionarRequisiciones()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "claseOrden", CTENUMEROORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CTENUMERO)) };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.AUXORDENDESUMINISTROS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton EnviarNominaCESION en la vista
     *
     */
    public void oprimirEnviarNominaCESION()
    {
        botonOrigenAlerta = "pasarNomina";
        etiquetaMensaje = idioma.getString("TB_TB3444");
        tituloMensajes = CTETITULOSYSMAN;
        mostrarDialogoCesion = true;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Registrocesion en la vista
     *
     */
    public void oprimirRegistrocesion()
    {
        
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(CTENITCESION) == null
            || registro.getCampos().get(CTENITCESION).toString().isEmpty())
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2044"));
            return;
        }
        botonOrigenAlerta = "Registrocesion";
        etiquetaMensaje = idioma.getString("TB_TB3445");
        tituloMensajes = CTETITULOSYSMAN;
        mostrarDialogoCesion = true;

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton verPolizas en la vista
     *
     */
    public void oprimirverPolizas()
    {
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();

        // Parametros que deben eliminarse
        parametros.put("numeroContrato",
                        String.valueOf(registro.getCampos().get(CTENUMERO)));
        parametros.put("numeroAfectado", String
                        .valueOf(registro.getCampos()
                                        .get(CTENUMEROAFECTADOCAMPO)));

        // Parametros que se deben devolver
        parametros.put("rid", registro.getLlave());
        parametros.put("anio", String.valueOf(anio));
        parametros.put(CTETIPOCONTRATO, String.valueOf(tipoContrato));
        parametros.put(CTETIPOAFECTADO, String.valueOf(tipoAfectado));
        parametros.put(CTENOMBRECONTRATO, nombreContrato);
        parametros.put(CTEVIGENCIA, vigencia);

        SessionUtil.setSessionVar(CTERETORNOFORMULARIO, CTERETORNA);// se
        // agrega
        // variable que afirma que el formulario se redireccion√≤,
        // para solucionar el problema de la lista LM5
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.SUBPOLIZASMODIFICACIONES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton aportesTerceros en la vista
     *
     */
    public void oprimiraportesTerceros()
    {
        String[] campos = { "claseOrden", CTENUMEROORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CTENUMERO)) };
        SessionUtil.cargarModalDatos(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.TERCEROS_APORTANTES_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(), campos,
                        valores);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton ImportarItems en la vista
     *
     */
    public void oprimirImportarItems()
    {
        // ****************** IMPORTAR ITEMS ***********************
        // <CODIGO_DESARROLLADO>

        try
        {

            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CTENUMEROAFECTADOCAMPO)
                || "0".equals(registro.getCampos()
                                .get(CTENUMEROAFECTADOCAMPO)))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString(CTETBTB2045));
                return;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CTENUMERO));

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL1868
                                                                            .getValue())
                                            .getUrl(), param));

            if ("0".equals(rs.getCampos().get(CTENUMERO).toString()))
            {
                ejbContratosCero.afectarItems(compania,
                                Long.valueOf(registro.getCampos()
                                                .get(CTENUMEROAFECTADOCAMPO)
                                                .toString()),
                                tipoAfectado,
                                Long.valueOf(registro.getCampos().get(CTENUMERO)
                                                .toString()),
                                tipoContrato,
                                SessionUtil.getUser().getCodigo());

            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2046"));
            }

            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(AdicionespcontratosControladorEnum.TIPOAFECTADO
                            .getValue(), tipoAfectado);
            param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                            .getValue(),
                            registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO));

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL1899
                                                                            .getValue())
                                            .getUrl(), param));

            registro.getCampos().put(CTEVALORTOTAL,
                            rs.getCampos().get(CTEVALORTOTAL));

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AdicionespcontratosControladorUrlEnum.URL1911
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            tipoContrato);
            fields.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CTENUMERO));
            fields.put(GeneralParameterEnum.VALORTOTAL.getName(),
                            rs.getCampos().get(CTEVALORTOTAL));
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(CTEMSM_PROCESO_EJECUTADO));
            asignarVariablesItems();
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException
                        | SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);

        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton AfectaItem en la vista
     *
     */
    public void oprimirAfectaItem()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CTENUMEROAFECTADOCAMPO)
            || "0".equals(registro.getCampos().get(CTENUMEROAFECTADOCAMPO)))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(CTETBTB2045));
            return;
        }
        botonOrigenAlerta = "afectaItem";
        etiquetaMensaje = idioma.getString("TB_TB3446")
                        .replace("s$numeroAfectado$s", registro.getCampos()
                                        .get(CTENUMEROAFECTADOCAMPO)
                                        .toString());
        tituloMensajes = CTETITULOSYSMAN;
        mostrarDialogoCesion = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Importarpagos en la vista
     *
     */
    public void oprimirImportarpagos()
    {

        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CTENUMEROAFECTADOCAMPO)
            || "0".equals(registro.getCampos().get(CTENUMEROAFECTADOCAMPO)))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(CTETBTB2045));
            return;
        }

        try
        {
            ejbContratosCero.importarPagos(compania, tipoContrato,
                            Long.valueOf(registro.getCampos().get(CTENUMERO)
                                            .toString()),
                            tipoAfectado,
                            Long.valueOf(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString()),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(CTEMSM_PROCESO_EJECUTADO));
        }
        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton afectarpagos en la vista
     *
     */
    public void oprimirafectarpagos()
    {

        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CTENUMEROAFECTADOCAMPO)
            || "0".equals(registro.getCampos()
                            .get(CTENUMEROAFECTADOCAMPO)))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(CTETBTB2045));
            return;
        }

        try
        {
            ejbContratosCero.afectarPagos(compania, tipoContrato,
                            Long.valueOf(registro.getCampos().get(CTENUMERO)
                                            .toString()),
                            tipoAfectado,
                            Long.valueOf(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString()),
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2047")
                            .replace("#$numeroAfectado#$",
                                            (String) registro.getCampos()
                                                            .get(CTENUMEROAFECTADOCAMPO))
                            .replace("#$tipoAfectado#$", tipoAfectado));
            asignarOrigenDatos();
        }
        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton imprimirRes en la vista
     *
     */
    public void oprimirimprimirRes()
    {
        if (listaModelos == null
            || listaModelos.getDatasource() == null
            || listaModelos.getDatasource().isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2048")
                            .replace("#$tipoFormato#$", tipoFormato));
            return;
        }

        if (SysmanFunciones.validarVariableVacio(modeloPlantillaPolizas))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2049"));
            return;
        }

        String strNombreDocumento = tipoContrato
            + registro.getCampos().get(CTENUMERO);
        String[] campos = new String[3];
        String[] valores = new String[3];
        campos[0] = "codigoPlantilla";
        campos[1] = "fechaPlantilla";
        campos[2] = "nombreDocDescarga";

        valores[0] = modeloPlantillaPolizas;
        valores[1] = SysmanFunciones.formatearFecha(fechaFormatoPolizas);
        valores[2] = strNombreDocumento;

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s", "'" + compania + "'");
        variablesConsultaW.put("s$CLASEORDEN$s", "'" + tipoContrato + "'");
        variablesConsultaW.put("s$NUMERO$s",
                        registro.getCampos().get(CTENUMERO).toString());
        // variables por parametro para documento word
        SessionUtil.setSessionVar("variablesConsultaWord", variablesConsultaW);

        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton AfectarPolizas en la vista
     *
     */
    public void oprimirAfectarPolizas()
    {
        try
        {
            // AFECTAR POLIZAS **********************
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CTENUMEROAFECTADOCAMPO)
                || "0".equals(registro.getCampos()
                                .get(CTENUMEROAFECTADOCAMPO)))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString(CTETBTB2045));
                return;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CTENUMERO));
            Registro rs;

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL2145
                                                                            .getValue())
                                            .getUrl(), param));

            if ("0".equals(rs.getCampos().get("REGISTROS").toString()))
            {

                boolean afectaPoliza = ejbContratosCero.afectarPolizas(compania,
                                Integer.valueOf(registro.getCampos()
                                                .get(CTENUMEROAFECTADOCAMPO)
                                                .toString()),
                                tipoAfectado,
                                Long.valueOf(registro.getCampos().get(CTENUMERO)
                                                .toString()),
                                tipoContrato,
                                SessionUtil.getUser().getCodigo());
                if (afectaPoliza)
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1114"));
                }
                else
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB3468").replace(
                                                    "s$NumeroAfectado$s",
                                                    registro.getCampos()
                                                                    .get(CTENUMEROAFECTADOCAMPO)
                                                                    .toString()));

                }

            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2050"));
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Quitar en la vista
     *
     */
    public void oprimirQuitar()
    {
        // <CODIGO_DESARROLLADO>
        if (!listaRubros.getSeleccionados().isEmpty())
        {
            listaRubros.getSeleccionados().clear();
            listaRubros.getLlavesSeleccionadas().clear();
            listaRubros.getFiltradoMultiple().clear();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Confirmar en la vista
     *
     */
    public void oprimirConfirmar()
    {

        String dis = SysmanFunciones
                        .nvl(registro.getCampos().get("DISPONIBILIDAD"), "")
                        .toString();
        String res = SysmanFunciones
                        .nvl(registro.getCampos().get(CTERUBRORESERVA), "")
                        .toString();

        if (!dis.isEmpty() || !res.isEmpty())
        {
            reemplazar = true;

            mostrarDialogoConfirmar = true;
        }
        else
        {
            confirmarTransaccion();
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton VerDisponibilidades en la vista
     *
     */
    public void oprimirVerDisponibilidades()
    {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String rowid = (String) registro.getCampos().get("RID");
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("tipoPPTO", "DIS");
        parametros.put(CTENUMEROORDEN,
                        String.valueOf(registro.getCampos().get(CTENUMERO)));
        parametros.put("titulo", lblTitulo);
        parametros.put("claseF", tipoContrato);

        // Parametros que se deben devolver
        parametros.put(CTETIPOCONTRATO, tipoContrato);
        parametros.put(CTEVIGENCIA, vigencia);
        parametros.put(CTENOMBRECONTRATO, nombreContrato);
        parametros.put(CTETIPOAFECTADO, tipoAfectado);
        parametros.put("rid", rowid);

        try
        {
            parametros.put("fechaFirma",
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos().get(
                                                            "FECHAFIRMA")));
        }
        catch (ParseException ex)
        {
            Logger.getLogger(AdicionesdecontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(CTEMSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }

        SessionUtil.setSessionVar(CTERETORNOFORMULARIO, CTERETORNA);// se
        // agrega
        // variable que afirma que el formulario se redireccion√≤,
        // para solucionar el problema de la lista LM5
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.ORDENDECOMPRAPPTOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton oprimirInformacionBancoP en la vista
     *
     */
    public void oprimirInformacionBancoP()
    {

        agregarRegistroNuevo(false);
        String rowid = (String) registro.getCampos().get("RID");
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("claseF", tipoContrato);
        parametros.put(CTENUMEROORDEN,
                        String.valueOf(registro.getCampos().get(CTENUMERO)));

        parametros.put("titulo", nombreContrato);

        // Parametros que se deben devolver
        parametros.put(CTEVIGENCIA, vigencia);
        parametros.put(CTETIPOCONTRATO, tipoContrato);
        parametros.put(CTENOMBRECONTRATO, nombreContrato);
        parametros.put(CTETIPOAFECTADO, tipoAfectado);
        parametros.put("rid", rowid);

        SessionUtil.setSessionVar(CTERETORNOFORMULARIO, CTERETORNA);// se
        // agrega
        // variable que afirma que el formulario se redireccion√≤,
        // para
        // solucionar el problema de la lista LM5
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.ORDENDECOMPRAAUXILIARES_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    /**
     *
     */
    public void inicializarVariablesAuxiliaresUno()
    {

        try
        {

            vTxtRedondeoGranTotal = ejbSysmanUtl.consultarParametro(compania,
                            "REDONDEAR GRAN TOTAL EN O.C.", modulo, new Date(),
                            true);
            vTxtRedondeoGranTotal = formatearEstadoVariable(
                            vTxtRedondeoGranTotal,
                            "NO");
            if ("SI".equals(vTxtRedondeoGranTotal))
            {
                vTxtDigitosRedondeoGranTotal = ejbSysmanUtl.consultarParametro(
                                compania, "DIGITOS REDONDEO GRAN TOTAL O.C.",
                                modulo, new Date(), true);
                vTxtDigitosRedondeoGranTotal = formatearEstadoVariable(
                                vTxtDigitosRedondeoGranTotal,
                                "0");
            }
            else
            {
                vTxtDigitosRedondeoGranTotal = "2";
            }
            obtenerParametrosYConfigurarBooleanos();

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    private void obtenerParametrosYConfigurarBooleanos()

    {
        String valorParametro;
        try
        {
            valorParametro = ejbSysmanUtl.consultarParametro(compania,
                            "MANEJA ACTUALIZACION PLAN DE COMPRAS", modulo,
                            new Date(), true);

            manejaActualizacionPlanDeCompras = valorParametro == null ? false
                : "SI".equals(valorParametro);

            subpCONTRATOAllowAdditions = true;
            subpCONTRATOAllowDeletions = true;
            subpCONTRATOAllowEdits = true;
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * @param vformatear
     * @param valorSi
     * @return
     */
    private String formatearEstadoVariable(String vformatear, String valorSi)
    {
        return vformatear == null ? valorSi : vformatear;
    }

    /**
     *
     */
    private void prepararYEnviarValores()
    {
        String lugarExpCedulaSupervisor;
        String lugarExpCedulaInterventor;
        String vigenciaContrato = "";
        String claseNovedad = "";
        String numeroModificacion = "";
        String considerandos = "";

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName()));
            param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            tipoContrato);
            param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                            .getValue(),
                            registro.getCampos()
                                            .get(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                                                            .getValue()));

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL12726
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null)
            {

                claseNovedad = asignarNombre(rs,
                                GeneralParameterEnum.NOMBRE.getName());

                numeroModificacion = asignarNombre(rs, CTECONSECUTIVOADICIONES);

                vigenciaContrato = asignarNombre(rs, "VIGENCIAF");

                considerandos = asignarNombre(rs, "CONSIDERANDOS");

            }

            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(AdicionespcontratosControladorEnum.NUMEROCONTRATO
                            .getValue(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.NUMERO
                                                            .getName()));
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);

            Registro rsExpedidaT;

            rsExpedidaT = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL1359
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsExpedidaT != null
                && rsExpedidaT.getCampos().get(CTEEXPEDIDACEDULA) != null)
            {
                lugarExpCedulaSupervisor = rsExpedidaT.getCampos()
                                .get(CTEEXPEDIDACEDULA).toString();
            }
            else
            {
                lugarExpCedulaSupervisor = "";
            }

            param.clear();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.NUMERO
                                                            .getName()));

            Registro rsExpedida = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL1380
                                                                            .getValue())
                                            .getUrl(), param));
            if (rsExpedida != null
                && rsExpedida.getCampos().get(CTEEXPEDIDACEDULA) != null)
            {
                lugarExpCedulaInterventor = rsExpedida.getCampos()
                                .get(CTEEXPEDIDACEDULA).toString();
            }
            else
            {
                lugarExpCedulaInterventor = "";

            }
            String strNombreDocumento = "Modificaciones al Contrato";
            String[] campos = new String[3];
            String[] valores = new String[3];

            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = plantilla;
            valores[1] = fechaFormato;
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$claseOrden$s",
                            "'" + tipoAfectado + "'");
            variablesConsultaW.put("s$numero$s", registro.getCampos()
                            .get(CTENUMEROAFECTADOCAMPO).toString());

            variablesConsultaW.put("s$VIGENCIACONTRATO$s",
                            vigenciaContrato);
            variablesConsultaW.put("s$CLASENOVEDAD$s", claseNovedad);
            variablesConsultaW.put("s$NUMEROMODIFICACION$s",
                            numeroModificacion);
            variablesConsultaW.put("s$CONSIDERANDOS$s", considerandos);

            variablesConsultaW.put("s$LUGAREXPCEDULASUPERVISOR$s",
                            "'" + lugarExpCedulaSupervisor + "'");
            variablesConsultaW.put("s$LUGAREXPCEDULAINTERVENTOR$s",
                            "'" + lugarExpCedulaInterventor + "'");

            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), campos,
                            valores);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * llamado en prepararYEnviarValores
     *
     * @param rs
     * @return nomnbre del registro recibo por parametro
     */
    private String asignarNombre(Registro rs, String campo)
    {
        String nombre;
        if (rs.getCampos().get(campo) != null)
        {
            nombre = rs.getCampos().get(campo).toString();
        }
        else
        {
            nombre = "";
        }
        return nombre;
    }

    /**
     *
     */
    private void evaluarYCambiarClaseNovedad()
    {
        try
        {

            String claseNovedad = registro.getCampos()
                            .get(CTECLASENOVEDAD) == null ? ""
                                : (String) registro.getCampos()
                                                .get(CTECLASENOVEDAD);
            if ("C".equals(claseNovedad))
            {
                visibleEnviarNominaCesion = true;
                visibleNitCesion = true;
                visibleNombreCesion = true;
                cargarListaNitCesion();
                String objetoContrato = registro.getCampos()
                                .get(CTEOBJETOCONTRATO) == null ? ""
                                    : (String) registro.getCampos()
                                                    .get(CTEOBJETOCONTRATO);
                if (objetoContrato.isEmpty())
                {
                    visibleRegistroCesion = true;
                }
                else
                {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2052"));
                }
            }
            else
            {
                visibleRegistroCesion = false;
                visibleEnviarNominaCesion = false;
                visibleNitCesion = false;
                visibleNombreCesion = false;
            }
            String auxPar = ejbSysmanUtl.consultarParametro(compania,
                            CTEMANPROGRFORMASPAGO, modulo, new Date(), true);

            boolean manejaProgramacionDeFormasDePago = auxPar == null ? false
                : "SI".equals(auxPar);

            if ("M".equals(claseNovedad))
            {
                visibleAfectaItems = true;
                if (manejaProgramacionDeFormasDePago)
                {
                    visibleFormaDePago = true;
                }
            }
            else
            {
                visibleAfectaItems = false;
                if (manejaProgramacionDeFormasDePago)
                {
                    visibleFormaDePago = false;

                }
            }

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     */
    public void bloquear()
    {
        
    	Object valor = registro != null && registro.getCampos() != null
    			? registro.getCampos().get("IMPRESO")
    					: null;

    			boolean impreso = false;

    			if (valor instanceof Boolean) {
    				impreso = (Boolean) valor;
    			} else if (valor instanceof Number) {
    				impreso = ((Number) valor).intValue() != 0;
    			} else if (valor instanceof String) {
    				impreso = "1".equals(valor)
    						|| "-1".equals(valor)
    						|| "true".equalsIgnoreCase((String) valor)
    						|| "SI".equalsIgnoreCase((String) valor);
    			}
    	
    	if (impreso)
        {
            if (!bloqueadoTercero)
            {
                bloqueadoTercero = true;
                bloqueadoObjetoContrato = true;
                bloqueadofecha = true;
                subpCONTRATOAllowAdditions = false;
                subpCONTRATOAllowDeletions = false;
                subpCONTRATOAllowEdits = false;
                bloqueadoComando89 = true;
                bloqueadoComando291 = true;
                bloqueadoConfirmar = true;
                bloqueadoFuenteRecursos = true;
                bloqueadoAuxiliar = true;
                bloqueadodestino = true;
            }
        }
        else
        {

            if (bloqueadoTercero)
            {
                bloqueadoTercero = false;
                bloqueadoObjetoContrato = false;
                bloqueadofecha = false;
                subpCONTRATOAllowAdditions = true;
                subpCONTRATOAllowDeletions = true;
                subpCONTRATOAllowEdits = true;
                bloqueadoComando89 = false;
                bloqueadoComando291 = false;
                bloqueadoConfirmar = false;
                bloqueadoFuenteRecursos = false;
                bloqueadoAuxiliar = false;
                bloqueadodestino = false;
            }
        }

    }

    /**
     *
     */
    private void registrarCesion()

    {
        Map<String, Object> fields = new TreeMap<>();
        fields.put(CTENITCESION, registro.getCampos().get(
                        CTENITCESION));

        try
        {
            String mensaje;
                             
           {
                mensaje = idioma.getString("TB_TB3459").replace("s$fecha$s",
                                SysmanFunciones.convertirAFechaCadena(
                                                new Date()));

                mensaje = mensaje.replace("s$tercero$s",
                                registro.getCampos().get(CTETERCERO)
                                                .toString());
                mensaje = mensaje.replace("s$nombreTercero$s", nombreTercero);
                mensaje = mensaje.replace("s$nitCesion$s",
                                registro.getCampos().get(CTENITCESION)
                                                .toString());
                mensaje = mensaje.replace("s$nombreCesion$s", nombCesion);

            }
            

            ejbContratosDos.registrarCesion(compania,
                            Long.valueOf(registro.getCampos().get(CTENUMERO)
                                            .toString()),
                            tipoContrato,
                            Long.valueOf(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString()),
                            tipoAfectado, SessionUtil.getUser().getCodigo(),
                            mensaje,
                            registro.getCampos().get(CTENITCESION).toString(),
                            registro.getCampos().get("SUCURSALCESION")
                                            .toString());
            cargarRegistro(css, ACCION_MODIFICAR);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(CTETBTB1323));
        }
        catch (NumberFormatException | SystemException | ParseException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    private void afectarItemYAsignarOrigenDatos()

    {

        try
        {
            ejbContratosDos.afectarItem(compania,
                            Long.valueOf(registro.getCampos().get(CTENUMERO)
                                            .toString()),
                            tipoContrato, Long.valueOf(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString()),
                            tipoAfectado, SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB3807").replace(
                                            "s$contratoAfectado$s",
                                            registro.getCampos()
                                                            .get(CTENUMEROAFECTADOCAMPO)
                                                            .toString()));
            asignarOrigenDatos();
        }
        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    private void confirmarTransaccion()
    {
        try
        {

            if (listaRubros.getSeleccionados().isEmpty())
            {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB686"));
                return;
            }
            else
            {
                for (int i = 0; i < listaRubros.getSeleccionados()
                                .size(); i++)
                {

                    ejbContratosUno.insertaPpto(compania,
                                    tipoContrato,
                                    Long.valueOf(registro.getCampos()
                                                    .get(CTENUMERO)
                                                    .toString()),
                                    claseDisponibilidaD,
                                    Long.parseLong(listaRubros
                                                    .getSeleccionados().get(i)
                                                    .getCampos().get(CTENUMERO)
                                                    .toString()),
                                    listaRubros.getSeleccionados().get(i).getCampos().get(CTEFECHA).toString(),

                                    registro.getCampos().get(CTETERCERO)
                                                    .toString(),
                                    registro.getCampos().get(CTESUCURSAL)
                                                    .toString(),
                                    SessionUtil.getUser().getCodigo());

                }

            }
            cargarRegistro(css, ACCION_MODIFICAR);
            disponibilidadConcatenado = getCadenaRubros("DIS");
            reservaConcatenado = getCadenaRubros("RES");

        }
        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * @param tipo
     * @return
     */
    private String getCadenaRubros(String tipo)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CTECLASEORDEN));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CTENUMERO));
        param.put("TIPO", tipo);
        Registro regConcatDis = null;
        try
        {
            regConcatDis = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL3692
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return (String) (regConcatDis == null ? ""
            : regConcatDis.getCampos().get("RUBRONOMBRE"));
    }

    /**
     *
     */
    private void actualizarComprobante()
    {
        try
        {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AdicionespcontratosControladorUrlEnum.URL3142
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(AdicionespcontratosControladorEnum.NITCESION.getValue(),
                            registro.getCampos().get(CTETERCERO));
            param.put(AdicionespcontratosControladorEnum.SUCURSALCESION
                            .getValue(),
                            registro.getCampos().get(CTESUCURSAL));
            param.put(AdicionespcontratosControladorEnum.TIPOCONTRATO
                            .getValue(), tipoAfectado);
            param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                            .getValue(),
                            registro.getCampos().get(CTENUMEROAFECTADOCAMPO));
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            Parameter parameter = new Parameter();
            parameter.setFields(param);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            registro.getCampos().put(CTETERCERO,
                            registroAuxTercero.getCampos().get("NIT"));
            registro.getCampos().put(CTESUCURSAL, registroAuxTercero
                            .getCampos().get(CTESUCURSAL));
            nombreTercero = (String) registroAuxTercero.getCampos()
                            .get(CTENOMBRE);
            cargarListaNumeroAfectado();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(CTETBTB1323));

        }
        catch (

        SystemException e)

        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    public void enviarNominaCesion()
    {

        try
        {
            if (registro.getCampos().get(CTENUMEROAFECTADOCAMPO) == null
                || "0".equals(registro.getCampos()
                                .get(CTENUMEROAFECTADOCAMPO)
                                .toString()))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2053"));
                return;
            }

            boolean enviarNomima = ejbContratosDos.enviarNominaCesion(compania,
                            Long.valueOf(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString()),
                            registro.getCampos()
                                            .get(CTETIPOAFECTADOCAMPO)
                                            .toString(),
                            registro.getCampos().get(CTENITCESION).toString(),
                            (Date) registro.getCampos()
                                            .get(CTEFECHAINICIAL),
                            (Date) registro.getCampos()
                                            .get(CTEFECHAFINAL),
                            new BigDecimal(registro.getCampos()
                                            .get(CTEVALORTOTAL).toString()),
                            nombCesion, SessionUtil.getUser().getCodigo());

            if (enviarNomima)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2054"));
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2058"));
            }

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB837"));

        }

        catch (NumberFormatException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    private void actualizarOrdenCompra()
    {
        try
        {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AdicionespcontratosControladorUrlEnum.URL3258
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(AdicionespcontratosControladorEnum.ORDENADORMODIF
                            .getValue(),
                            registro.getCampos().get(CTEORDENADORMODIF));
            param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            param.put(AdicionespcontratosControladorEnum.TIPOAFECTADO
                            .getValue(),
                            registro.getCampos().get(CTETIPOAFECTADOCAMPO));
            param.put(AdicionespcontratosControladorEnum.NUMEROAFECTADO
                            .getValue(),
                            registro.getCampos().get(CTENUMEROAFECTADOCAMPO));

            Parameter parameter = new Parameter();
            parameter.setFields(param);

            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
        }

        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * @param event
     */
    private void validarCssYCargarListado(SelectEvent event)
    {
        try
        {
            if (css == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2051"));
                return;
            }
            // ACA YA ESTA GUARDADO EL REGISTRO
            Registro registroAux = (Registro) event.getObject();

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), registro
                            .getCampos().get(CTECLASEORDEN));
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CTENUMERO));

            List<Registro> rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL3307
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs.isEmpty())
            {
                registro.getCampos().put(CTENUMEROAFECTADOCAMPO,
                                registroAux.getCampos().get(CTENUMERO));
                registro.getCampos().put(CTEORDENADORMODIF,
                                registroAux.getCampos().get("ORDENADOR"));
                Map<String, Object> fields = new TreeMap<>();
                fields.put(AdicionespcontratosControladorEnum.CEDULA.getValue(),
                                registroAux.getCampos().get("ORDENADOR"));

                if (!listaOrdenador.isVacio())
                {
                    nombreOrdenador = listaOrdenador
                                    .getRegistroUnico(fields)
                                    .getCampos()
                                    .get(CTENOMBRE).toString();
                }
                else
                {
                    nombreOrdenador = "";
                }

                cargarListaNumeroPpto();
            }
            else
            {
                JsfUtil.agregarMensajeInformativoDialogo(
                                idioma.getString("TB_TB2060"));
                registro.getCampos().remove(CTENUMEROAFECTADOCAMPO);
                registro.getCampos().remove(CTEORDENADORMODIF);
                return;
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     */
    private void validaManejaPlanYNovedades()
    {
        if (manejaActualizacionPlanDeCompras
            && registro.getCampos().get(CTENUMERO).equals(
                            registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)))
        {
            visibleVerificacion306 = true;
            verificacion306 = true;
        }
        else
        {
            visibleVerificacion306 = false;
        }
        String claseNovedad = registro.getCampos()
                        .get(CTECLASENOVEDAD) == null ? ""
                            : (String) registro.getCampos()
                                            .get(CTECLASENOVEDAD);

        bloqueadofechaFinal = registro.getCampos()
                        .get(CTEFECHAFINAL) != null;

        if ("M".equals(claseNovedad))
        {
            ocultarDesocultarFormaDePago();
        }
        else
        {
            visibleAfectaItems = false;
        }

        if ("C".equals(claseNovedad))
        {
            visibleEnviarNominaCesion = true;
            visibleNitCesion = true;
            visibleNombreCesion = true;
            cargarListaNitCesion();
        }
        else
        {
            visibleEnviarNominaCesion = false;
            visibleNitCesion = false;
            visibleNombreCesion = false;
        }
    }

    /**
     *
     */
    private void ocultarDesocultarFormaDePago()
    {
        try
        {
            visibleAfectaItems = true;
            String valorParametro = ejbSysmanUtl.consultarParametro(compania,
                            CTEMANPROGRFORMASPAGO, modulo, new Date(), true);

            if (valorParametro == null ? false
                : "SI".equals(valorParametro))
            {
                visibleFormaDePago = true;
            }
            else
            {
                visibleFormaDePago = false;

            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     */
    private void verificarSesionDeOtroFormulario()
    {
        // se verifica si la variable de sesi√≤n existe, si existe
        // quiere decir
        // que viene de otro formulario
        // en este caso no se filtra la lista y se elimina la variable
        if (SessionUtil.getSessionVar(CTERETORNOFORMULARIO) == null)
        {
            JsfUtil.ejecutarJavaScript(
                            AdicionespcontratosControladorEnum.FILTRARLM5
                                            .getValue());
        }
        else
        {
            SessionUtil.removeSessionVar(CTERETORNOFORMULARIO);
        }

        cargarListaRubros();
        cargarListaClaseDisponibilidad();
        validarListaRubros();
    }

    /**
     *
     */
    private void validarListaRubros()
    {
        try
        {
            if (css != null)
            {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                nvl(registro.getCampos()
                                                .get(CTECLASEDISPONIBILIDAD),
                                                ""));

                String clase;

                clase = (String) nvl(listaClaseDisponibilidad
                                .getRegistroUnico(fields).getCampos()
                                .get(GeneralParameterEnum.CLASE.getName()),
                                "DIS");

                if (listaRubros != null
                    && !listaRubros.getSeleccionados().isEmpty())
                {
                    listaRubros.getSeleccionados().clear();
                    listaRubros.getLlavesSeleccionadas().clear();
                    limpiarListaRubros();
                }

                if (listaRubros != null)
                {

                    configurarTipos(clase);
                }
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo llamado en validarListaRubros
     */
    private void limpiarListaRubros()
    {
        if (listaRubros.getFiltradoMultiple() != null)
        {
            listaRubros.getFiltradoMultiple().clear();
        }
    }

    private void configurarTipos(String clase)
    {
        if ("DIS".equals(clase))
        {

            configurarDisponibilidad();
        }
        else if ("RES".equals(clase))
        {
            configurarReserva();
        }
    }

    /**
     *
     */
    private void configurarReserva()
    {
        if (listaRubros.getSeleccionados() != null
            && !listaRubros.getSeleccionados().isEmpty())
        {
            listaRubros.getSeleccionados().clear();
        }
        if (listaRubros.getLlavesSeleccionadas() != null
            && !listaRubros.getLlavesSeleccionadas()
                            .isEmpty())
        {
            listaRubros.getLlavesSeleccionadas().clear();
        }
        if (listaRubros.getFiltradoMultiple() != null
            && !listaRubros.getFiltradoMultiple().isEmpty())
        {
            listaRubros.getFiltradoMultiple().clear();
        }
        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CTERUBRORESERVA))
        {
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CTERUBRORESERVA));
            listaRubros.setSeleccionados(fields);
        }
    }

    private void configurarDisponibilidad()
    {
        if (listaRubros.getSeleccionados() != null
            && !listaRubros.getSeleccionados().isEmpty())
        {
            listaRubros.getSeleccionados().clear();
        }
        if (listaRubros.getLlavesSeleccionadas() != null
            && !listaRubros.getLlavesSeleccionadas()
                            .isEmpty())
        {
            listaRubros.getLlavesSeleccionadas().clear();
        }
        if (listaRubros.getFiltradoMultiple() != null
            && !listaRubros.getFiltradoMultiple().isEmpty())
        {
            listaRubros.getFiltradoMultiple().clear();
        }

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        CTEDISPONIBILIDAD))
        {

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CTEDISPONIBILIDAD));

            listaRubros.setSeleccionados(fields);
        }
    }

    /**
     *
     */
    public void asignarVariablesItems()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AdicionespcontratosControladorEnum.DIGITOSREDONDEAR
                        .getValue(), vTxtDigitosRedondeoGranTotal);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CTECLASEORDEN));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CTENUMERO));

        try
        {
            Registro registroSubPcontrato = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL3592
                                                                            .getValue())
                                            .getUrl(), param));

            txtIva = Double.parseDouble(registroSubPcontrato.getCampos()
                            .get("IVA").toString());
            txtTotalDescuento = Double
                            .parseDouble(registroSubPcontrato.getCampos()
                                            .get("TOTALDESCUENTO")
                                            .toString());
            txtSubtotal = Double.parseDouble(registroSubPcontrato.getCampos()
                            .get("SUBTOTAL").toString());
            txtAjustealpeso = Double
                            .parseDouble(registroSubPcontrato.getCampos()
                                            .get("AJUSTEALPESO").toString());
            txtTotal = txtSubtotal - txtTotalDescuento + txtIva
                + txtAjustealpeso + 0;
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CTEPROYECTO))
            {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos().get(CTEPROYECTO));

                nombreAuxiliar = nvl(listaAuxiliar
                                .getRegistroUnico(fields)
                                .getCampos().get("NOMBREPROYECTO"), "")
                                                .toString();

            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     *
     */
    public void abrirFormularioUnico()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AdicionespcontratosControladorEnum.TIPOCONTRATO.getValue(),
                        tipoContrato);

        Registro rs;
        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL3646
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null)
            {
                if ("E".equals(rs.getCampos().get("MANEJA")))
                {
                    maneja = "1";
                }
                else if ("S".equals(rs.getCampos().get("MANEJA")))
                {
                    maneja = "2";
                }
                else
                {
                    maneja = "3";
                }

                lblTitulo = rs.getCampos().get(CTENOMBRE).toString();

                vformato = rs.getCampos().get("TIPOFORMATO") == null ? ""
                    : rs.getCampos().get("TIPOFORMATO").toString();
                vTxtTipoModelo = rs.getCampos().get("TIPO_MODELO") == null ? ""
                    : rs.getCampos().get("TIPO_MODELO").toString();
                cargarListaPlantilla();

            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    public void asignarNombresCampos()
    {
        try
        {
            Map<String, Object> fieldTercero = new TreeMap<>();
            fieldTercero.put(AdicionespcontratosControladorEnum.NIT.getValue(),
                            registro.getCampos().get(CTETERCERO));

            nombreTercero = listaTercero.getRegistroUnico(fieldTercero) == null
                ? ""
                : listaTercero.getRegistroUnico(fieldTercero)
                                .getCampos()
                                .get(CTENOMBRE).toString();

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(CTEDEPENDENCIA));

            nombreDependencia = listaDependencia
                            .getRegistroUnico(fields) == null
                                ? ""
                                : listaDependencia
                                                .getRegistroUnico(fields)
                                                .getCampos().get(CTENOMBRE)
                                                .toString();
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * @return
     */
    private boolean adicionarValorTotal()

    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoAfectado);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO)
                                            .toString().isEmpty() ? "0"
                                                : registro
                                                                .getCampos()
                                                                .get(CTENUMEROAFECTADOCAMPO)
                                                                .toString());

            String claseNovedad = (String) SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(CTECLASENOVEDAD), "");
            Registro rs;

            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL3857
                                                                            .getValue())
                                            .getUrl(), param));

            if (("M".equals(claseNovedad) || "D".equals(claseNovedad) || "O".equals(claseNovedad) || "Z".equals(claseNovedad))
                && !validarClaseNovedad(rs, claseNovedad))
            {

                return false;

            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return true;
    }

    /**
     * @param rs
     * @param claseNovedad
     * @return
     */
    private boolean validarClaseNovedad(Registro rs, String claseNovedad)
    {

        Integer valorPlazo = null;
        Boolean excede = false;
        Boolean mayora50 = false;
        int porcentajeAumentoMaximo = 0;
        if (rs != null)
        {
            registro.getCampos().put(CTEFECHAINICIAL,
                            rs.getCampos().get(CTEFECHA));
            vValorTotalContr = Double.parseDouble(
                            nvl(rs.getCampos().get(CTEVALORTOTAL),
                                            "0.0")
                                                            .toString());
            valorFinalContrato = new BigDecimal(SysmanFunciones
                            .nvl(rs.getCampos().get("VALORFINAL"),
                                            "0.0")
                            .toString());
            valorPlazo = SysmanFunciones.validarCampoVacio(
                            rs.getCampos(), "PLAZODEENTREGA") ? 0
                                : Integer.parseInt(rs.getCampos()
                                                .get("PLAZODEENTREGA")
                                                .toString());
            BigDecimal valorTotal = new BigDecimal(SysmanFunciones.nvl(
                            registro.getCampos()
                                            .get(CTEVALORTOTAL),
                            "0.0").toString());
            BigDecimal valorContrato = new BigDecimal(vValorTotalContr);

            try {
				mayora50 = SysmanFunciones.nvlStr(
						SysmanFunciones.toString(
								ejbSysmanUtl.consultarParametro(compania,"ADICIONAR MAS DEL 50 POR CIENTO DEL CONTRATO" , modulo, new Date(),true)),"NO").equals("SI");
				if(mayora50) {
					porcentajeAumentoMaximo = Integer.parseInt(SysmanFunciones.toString(SysmanFunciones.nvl(
									ejbSysmanUtl.consultarParametro(compania,"PORCENTAJE MAXIMO PARA ADICIONES EN CONTRATO ORIGINAL" , modulo, new Date(),true),0)));
					
					if(porcentajeAumentoMaximo>0) {
						// Calcula el valor m·ximo permitido para el valor total
						BigDecimal valorMaximoPermitido = valorContrato.multiply(new BigDecimal(porcentajeAumentoMaximo / 100.0));
						
						// Verifica si el valor total excede el lÌmite
						excede = valorTotal.compareTo(valorMaximoPermitido) > 0;
					}else {
						excede = valorTotal.compareTo(valorContrato
	                            .divide(new BigDecimal(2))) > 0;
					}
				}else {
				
					excede = valorTotal.compareTo(valorContrato
	                            .divide(new BigDecimal(2))) > 0;
				}
            
            if (("M".equals(claseNovedad) || "D".equals(claseNovedad) || "Z".equals(claseNovedad)) && excede)
            {
            	if(mayora50) {
            		JsfUtil.agregarMensajeAlerta(
            				idioma.getString("TB_TB4457").replace(
                                    "s$porcmax$s",SysmanFunciones.toString(porcentajeAumentoMaximo)));
            		return false;
            	}else {            		
            		JsfUtil.agregarMensajeAlerta(
            				idioma.getString("TB_TB2061"));
            		return false;
            	}
            }

            } catch (SystemException e) {
            	JsfUtil.agregarMensajeError(e.getMessage());
            	logger.error(e.getMessage(), e);
            }
        }
        else
        {
            valorFinalContrato = new BigDecimal(0.0);
        }
        actualizarSiNoBloqueadoValorTotal(valorPlazo);
        actualizarCamposParametros(claseNovedad, rs, valorPlazo);
        return true;
    }

    /**
     * Trae el valor de un campo del registro de tipo entero. En caso de nulo trae el valor en cero.
     *
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>int</code>
     */
    private int getIntVal(String nombreCampo)
    {
        return getIntVal(registro, nombreCampo);
    }

    /**
     * Trae el valor de un campo del registro de tipo entero. En caso de nulo trae el valor en cero.
     *
     * @param unRegistro
     * El objeto Registro que se va a evaluar.
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>int</code>
     */
    private int getIntVal(Registro unRegistro, String nombreCampo)
    {
        Object object = unRegistro.getCampos().get(nombreCampo);
        return object == null ? 0 : Integer.parseInt(object.toString());
    }

    /**
     * Metodo para actualizar la fecha de finalizacion del contrato para cuando son habiles o calendario
     */
    private void validarDiasCalendario()
    {
        
        try
        {
            int plazoEntrega = getIntVal(CTEDURACION);

            if ("1".equals(registro.getCampos().get(CTETIPODIA).toString()))
            {

                if (calcularMismoDia)
                {
                    fechaf = ejbSysmanUtl.retornarFechaMasDiasHabiles(compania,
                                    (Date) registro.getCampos()
                                                    .get(GeneralParameterEnum.FECHAFINAL
                                                                    .getName()),
                                    plazoEntrega, false);
                }
                else
                {
                    fechaf = ejbSysmanUtl.retornarFechaMasDiasHabiles(compania,
                                    (Date) registro.getCampos()
                                                    .get(GeneralParameterEnum.FECHAFINAL
                                                                    .getName()),
                                    plazoEntrega + 1, false);
                }

            }
            else
            {

                if (calcularMismoDia)
                {
                    fechaf = SysmanFunciones.sumarRestarDiasFecha(
                                    (Date) registro.getCampos().get(
                                                    GeneralParameterEnum.FECHAFINAL
                                                                    .getName()),
                                    plazoEntrega - 1);
                }
                else
                {
                    fechaf = SysmanFunciones.sumarRestarDiasFecha(
                                    (Date) registro.getCampos().get(
                                                    GeneralParameterEnum.FECHAFINAL
                                                                    .getName()),
                                    plazoEntrega);
                }

            }

            registro.getCampos().put("FECHAFINALMODIF",
                            fechaf);
            registro.getCampos().put("FECHAFINALACTUAL",
                            fechaf);
            agregarRegistroNuevo(false);

        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     *
     */
    private void actualizarSiNoBloqueadoValorTotal(Integer valor)

    {
        Integer valorPlazo = valor;
        BigDecimal suma;
        if (registro.getCampos().get(CTEFECHAFINAL) != null
            && !bloqueadofechaFinal)
        {
            bloqueadofechaFinal = true;
        }
        else if (registro.getCampos().get(CTEFECHAFINAL) == null)
        {
            bloqueadofechaFinal = false;
        }
        valorPlazo = (valorPlazo == null ? 0 : valorPlazo)
            + (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            CTEDURACION) ? 0
                                : Integer.parseInt(registro
                                                .getCampos()
                                                .get(CTEDURACION)
                                                .toString()));

        if (!bloqueadoValorTotalF)
        {
            BigDecimal valorTotalAdi = new BigDecimal(SysmanFunciones
                            .nvl(registro.getCampos().get(CTEVALORTOTAL), "0.0")
                            .toString());
            
            BigDecimal valorContrato = new BigDecimal(vValorTotalContr);
            suma = valorContrato.add(valorTotalAdi);

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AdicionespcontratosControladorUrlEnum.URL3942
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.VALORTOTAL.getName(),
                            suma);
            fields.put(AdicionespcontratosControladorEnum.FECHAFINALIZACION
                            .getValue(),
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(CTEFECHAFINAL),
                                            new Date()));
            fields.put(AdicionespcontratosControladorEnum.PLAZOENTREGA
                            .getValue(),
                            valorPlazo);
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            tipoAfectado);
            fields.put(GeneralParameterEnum.NUMERO.getName(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO), 0));

            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            try
            {
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);
            }
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

            //bloqueadoValorTotalF = true;
        }
    }

    /**
     *
     */
    private void actualizarCamposParametros(String claseNovedad, Registro rs,
        int valorPlazo)
    {
        if ("O".equals(claseNovedad) || "Z".equals(claseNovedad))
        {
            if (rs != null && !rs.getCampos().isEmpty()
                && rs.getCampos().get(CTEFECHA) != null)
            {
                registro.getCampos().put(CTEFECHAINICIAL,
                                rs.getCampos().get(CTEFECHA));
            }
            if (registro.getCampos().get(CTEFECHAFINAL) != null
                && !bloqueadofechaFinal)
            {
                bloqueadofechaFinal = true;
            }
            else if (registro.getCampos().get(CTEFECHAFINAL) == null)
            {
                bloqueadofechaFinal = false;
            }
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            AdicionespcontratosControladorUrlEnum.URL4014
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            fields.put(AdicionespcontratosControladorEnum.FECHAFINALIZACION
                            .getValue(),fechaf);
            fields.put(AdicionespcontratosControladorEnum.PLAZOENTREGA
                            .getValue(),
                            valorPlazo);
            fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            tipoAfectado);
            fields.put(GeneralParameterEnum.NUMERO.getName(),
                            SysmanFunciones.nvl(registro.getCampos()
                                            .get(CTENUMEROAFECTADOCAMPO), 0));
            Parameter parameter = new Parameter();
            parameter.setFields(fields);

            try
            {
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                parameter);
            }
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

        }
    }

    /**
     *
     */
    private void asignarNombreOrdenador()
    {
        try
        {
            if (registro != null)
            {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(AdicionespcontratosControladorEnum.CEDULA.getValue(),
                                nvl(registro.getCampos()
                                                .get(CTEORDENADORMODIF),
                                                "0"));

                if (registro.getCampos().get(CTEORDENADORMODIF) != null
                    && listaOrdenador.getRegistroUnico(fields) != null)
                {

                    nombreOrdenador = nvl(
                                    listaOrdenador
                                                    .getRegistroUnico(fields)
                                                    .getCampos().get(CTENOMBRE),
                                    "").toString();
                }
                else
                {
                    nombreOrdenador = null;
                }
            }
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // Metodo heredado

    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
        try
        {
            // <CODIGO_DESARROLLADO>
            precargarRegistro();

            if (registro.getCampos().get(CTEFECHAFINALMODIF) == null)
            {
                registro.getCampos().put(CTEFECHAFINALACTUAL,
                                registro.getCampos()
                                                .get(GeneralParameterEnum.FECHAFINAL
                                                                .getName()));
            }
            else
            {
                registro.getCampos().put(CTEFECHAFINALACTUAL,
                                registro.getCampos().get(CTEFECHAFINALMODIF));
            }

            if (css == null)
            {
            	
            	 visibleAdjuntos = false;
           	
                registro.getCampos().put(CTEFECHA,
                                new Date());
                registro.getCampos().put(CTEVALORTOTAL, "0");
                bloqueadoValorTotalF = false;
                if (manejaActualizacionPlanDeCompras)
                {
                    visibleVerificacion306 = true;
                    verificacion306 = false;
                }
                else
                {
                    visibleVerificacion306 = false;
                }
                nombreTercero = null;
                nombreDependencia = null;
                registro.getCampos().put(
                                GeneralParameterEnum.FECHAINICIAL.getName(),
                                new Date());
                cambiarActivo = true;
            }
            else
            	 visibleAdjuntos = true;
            {
                asignarNombreOrdenador();
                if (registro != null)
                {
                    Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(GeneralParameterEnum.NUMERO.getName(),
                                    registro.getCampos()
                                                    .get(CTENUMERO));
                    param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                                    tipoContrato);

                    registro.getCampos().put("APORTESTE_AD",
                                    RegistroConverter.toRegistro(
                                                    requestManager.get(
                                                                    UrlServiceUtil.getInstance()
                                                                                    .getUrlServiceByUrlByEnumID(
                                                                                                    AdicionespcontratosControladorUrlEnum.URL4135
                                                                                                                    .getValue())
                                                                                    .getUrl(),
                                                                    param))

                                                    .getCampos().get("TOTAL"));

                    visibleRegistroCesion = SysmanFunciones.validarCampoVacio(
                                    registro.getCampos(),
                                    "OBJETO")
                        && registro.getCampos().get(CTECLASENOVEDAD) != null
                        && "C".equals(registro.getCampos()
                                        .get(CTECLASENOVEDAD));
                    disponibilidadConcatenado = getCadenaRubros("DIS");
                    reservaConcatenado = getCadenaRubros("RES");
                }

                bloquear();
                cargarListaNumeroAfectado();
                validaManejaPlanYNovedades();
                asignarNombresCampos();
                cambiarActivo = false;

            }
            abrirFormularioUnico();
            asignarVariablesItems();

            verificarSesionDeOtroFormulario();
            listaRubros.getLlavesSeleccionadas().clear();
            listaRubros.getSeleccionados().clear();
            boolean modificaciones = ejbSysmanUtl.consultarParametro(compania,
                            CTEMODCONSECUADICIONES, modulo, new Date(),
                            true) == null
                                ? false
                                : "SI".equals(ejbSysmanUtl.consultarParametro(
                                                compania,
                                                CTEMODCONSECUADICIONES, modulo,
                                                new Date(),
                                                true));
            if (modificaciones)
            {
                bloqueaConAdi = false;
            }
            else
            {
                bloqueaConAdi = true;
            }
        }

        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {

        registro.getCampos().put(CTETIPOAFECTADOCAMPO, tipoAfectado);
        registro.getCampos().put(CTECLASEORDEN, tipoContrato);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try
        {
            registro.getCampos().put(CTENUMERO,
                            ejbContratosUno.ConsecContrato(compania,
                                            tipoContrato,
                                            SysmanFunciones
                                                            .ano(new Date()),
                                            SessionUtil.getUser()
                                                            .getCodigo()));
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

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
        Date fechaAux = (Date) registro.getCampos()
                        .get(GeneralParameterEnum.FECHA.getName());

        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3733")
                        .replace("s$numero$s",
                                        registro.getCampos().get("NUMERO")
                                                        .toString())
                        .replace("s$ano$s", Integer.toString(
                                        SysmanFunciones.ano(fechaAux))));
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {

        if (ACCION_MODIFICAR.equals(accion))
        {
            if (!adicionarValorTotal())
            {
                registro.getCampos().put(CTEVALORTOTAL,"0");

                return false;
            }
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA
                                            .getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.NUMERO.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.CLASEORDEN
                                            .getName());

        }
        else
        {
            registro.getCampos().put("CONSECUTIVOADICIONES", 0);

        }

        registro.getCampos().remove("FUENTERECURSOS");
        registro.getCampos().remove(CTEDISPONIBILIDAD);
        registro.getCampos().remove("FECHADEDISPONIBILIDAD");
        registro.getCampos().remove("NOMBRETERCERO");
        registro.getCampos().remove(CTERUBRO);
        registro.getCampos().remove("RESERVA");
        registro.getCampos().remove("FECHAREGISTRO");
        registro.getCampos().remove(CTERUBRORESERVA);

        if (registro.getCampos().get(CTECLASEDISPONIBILIDAD) == null)
        {
            registro.getCampos().put(CTECLASEDISPONIBILIDAD, "DIS");
        }

        if (css == null)
        {
            bloqueadoValorTotalF = false;
        }

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {

        String numeroAfectado = registro.getCampos()
                        .get(CTENUMEROAFECTADOCAMPO) == null ? ""
                            : SysmanFunciones
                                            .nvl(registro.getCampos().get(
                                                            CTENUMEROAFECTADOCAMPO),
                                                            "")
                                            .toString();

        if (verificacion306
            && numeroAfectado != null)

        {
            try
            {

                ejbContratosCero.actualizarPlanDeCompras(compania, tipoContrato,
                                Long.valueOf(registro.getCampos()
                                                .get(CTENUMERO).toString()),
                                Long.valueOf(registro.getCampos()
                                                .get(CTENUMERO).toString()),
                                Integer.valueOf(modulo), SysmanFunciones.ano(
                                                (Date) registro.getCampos()
                                                                .get(CTEFECHA)),
                                SessionUtil.getUser().getCodigo());

            }
            catch (NumberFormatException | SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }

        }
     // CC_1649 grojas: Se ejecuta el proceso de extraerValores para actualizar el contrato con la informaciÛn correcta,
     // considerando todas las modificaciones que se hayan realizado sobre Èl.
        try {
        	
        	Object campoNumeroAfectado = registro.getCampos().get(CTENUMEROAFECTADOCAMPO);
        	BigDecimal valorContrato = (valorFinalContrato != null) ? valorFinalContrato : BigDecimal.ZERO;
        	
        	if (campoNumeroAfectado != null && !campoNumeroAfectado.toString().trim().isEmpty()) {
        	
	        	String rta = ejbContratosUno.extraerValores(compania, tipoAfectado,
	        			Long.parseLong(SysmanFunciones.nvl(registro.getCampos().get(CTENUMEROAFECTADOCAMPO),0).toString()),
	        			valorContrato,SessionUtil.getUser().getCodigo());
	        	
	        	if (!"OK".equalsIgnoreCase(rta)) {
	        		JsfUtil.agregarMensajeError(rta);
	            }
        	}
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
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
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CTENUMERO));

        Registro rs;
        try
        {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL1868
                                                                            .getValue())
                                            .getUrl(), param));

            if (!"0".equals(rs.getCampos().get(CTENUMERO).toString()))
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2062"));
                return false;
            }
            
            
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AdicionespcontratosControladorUrlEnum.URL3857
                                                                            .getValue())
                                            .getUrl(), param));
            
            if (!"0".equals(SysmanFunciones.nvl(rs.getCampos().get(CTEVALORTOTAL),"0").toString()) 
                            || !"0".equals(SysmanFunciones.nvl(rs.getCampos().get(CTEDURACION),"0").toString()) )
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4446"));
                return false;
            }
            
            
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
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

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public String getModeloPlantilla()
    {
        return modeloPlantilla;
    }

    public void setModeloPlantilla(String modeloPlantilla)
    {
        this.modeloPlantilla = modeloPlantilla;
    }

    public String getNumeroDoc()
    {
        return numeroDoc;
    }

    public void setNumeroDoc(String numeroDoc)
    {
        this.numeroDoc = numeroDoc;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getTipoContrato()
    {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
    }

    public String getTipoAfectado()
    {
        return tipoAfectado;
    }

    public void setTipoAfectado(String tipoAfectado)
    {
        this.tipoAfectado = tipoAfectado;
    }

    public String getNombreContrato()
    {
        return nombreContrato;
    }

    public void setNombreContrato(String nombreContrato)
    {
        this.nombreContrato = nombreContrato;
    }

    public String getVigencia()
    {
        return vigencia;
    }

    public void setVigencia(String vigencia)
    {
        this.vigencia = vigencia;
    }

    public String getModelosPlantilla()
    {
        return modelosPlantilla;
    }

    public void setModelosPlantilla(String modelosPlantilla)
    {
        this.modelosPlantilla = modelosPlantilla;
    }

    public String getNitTercero()
    {
        return nombreTercero;
    }

    public void setNitTercero(String nitTercero)
    {
        this.nombreTercero = nitTercero;
    }

    public String getNombreTercero()
    {
        return nombreTercero;
    }

    public void setNombreTercero(String nombreTercero)
    {
        this.nombreTercero = nombreTercero;
    }

    public String getNombreAuxiliar()
    {
        return nombreAuxiliar;
    }

    public void setNombreAuxiliar(String nombreAuxiliar)
    {
        this.nombreAuxiliar = nombreAuxiliar;
    }

    public String getEtiquetaMensaje()
    {
        return etiquetaMensaje;
    }

    public void setEtiquetaMensaje(String etiquetaMensaje)
    {
        this.etiquetaMensaje = etiquetaMensaje;
    }

    public String getTituloMensajes()
    {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes)
    {
        this.tituloMensajes = tituloMensajes;
    }

    public boolean isMostrarDialogoCesion()
    {
        return mostrarDialogoCesion;
    }

    public void setMostrarDialogoCesion(boolean mostrarDialogoCesion)
    {
        this.mostrarDialogoCesion = mostrarDialogoCesion;
    }

    public String getNombreOrdenador()
    {
        return nombreOrdenador;
    }

    public void setNombreOrdenador(String nombreOrdenador)
    {
        this.nombreOrdenador = nombreOrdenador;
    }

    public String getGlobalTipoPPto()
    {
        return globalTipoPPto;
    }

    public void setGlobalTipoPPto(String globalTipoPPto)
    {
        this.globalTipoPPto = globalTipoPPto;
    }

    public boolean isBloqueadoConsecutivoAdiciones()
    {
        return bloqueadoConsecutivoAdiciones;
    }

    public void setBloqueadoConsecutivoAdiciones(
        boolean bloqueadoConsecutivoAdiciones)
    {
        this.bloqueadoConsecutivoAdiciones = bloqueadoConsecutivoAdiciones;
    }

    public boolean isVisibleEnviarNominaCesion()
    {
        return visibleEnviarNominaCesion;
    }

    public void setVisibleEnviarNominaCesion(
        boolean visibleEnviarNominaCesion)
    {
        this.visibleEnviarNominaCesion = visibleEnviarNominaCesion;
    }

    public boolean isVisibleNitCesion()
    {
        return visibleNitCesion;
    }

    public void setVisibleNitCesion(boolean visibleNitCesion)
    {
        this.visibleNitCesion = visibleNitCesion;
    }

    public boolean isVisibleNombreCesion()
    {
        return visibleNombreCesion;
    }

    public void setVisibleNombreCesion(boolean visibleNombreCesion)
    {
        this.visibleNombreCesion = visibleNombreCesion;
    }

    public boolean isVisibleRegistroCesion()
    {
        return visibleRegistroCesion;
    }

    public void setVisibleRegistroCesion(boolean visibleRegistroCesion)
    {
        this.visibleRegistroCesion = visibleRegistroCesion;
    }

    public boolean isVisibleAfectaItems()
    {
        return visibleAfectaItems;
    }

    public void setVisibleAfectaItems(boolean visibleAfectaItems)
    {
        this.visibleAfectaItems = visibleAfectaItems;
    }

    public boolean isVisibleFormaDePago()
    {
        return visibleFormaDePago;
    }

    public void setVisibleFormaDePago(boolean visibleFormaDePago)
    {
        this.visibleFormaDePago = visibleFormaDePago;
    }

    public Double getVValorTotalContr()
    {
        return vValorTotalContr;
    }

    public void setVValorTotalContr(Double vValorTotalContr)
    {
        this.vValorTotalContr = vValorTotalContr;
    }

    public boolean isBloqueadoTercero()
    {
        return bloqueadoTercero;
    }

    public void setBloqueadoTercero(boolean bloqueadoTercero)
    {
        this.bloqueadoTercero = bloqueadoTercero;
    }

    public boolean isBloqueadoObjetoContrato()
    {
        return bloqueadoObjetoContrato;
    }

    public void setBloqueadoObjetoContrato(boolean bloqueadoObjetoContrato)
    {
        this.bloqueadoObjetoContrato = bloqueadoObjetoContrato;
    }

    public boolean isBloqueadofecha()
    {
        return bloqueadofecha;
    }

    public void setBloqueadofecha(boolean bloqueadofecha)
    {
        this.bloqueadofecha = bloqueadofecha;
    }

    public boolean isSubpCONTRATOAllowAdditions()
    {
        return subpCONTRATOAllowAdditions;
    }

    public void setSubpCONTRATOAllowAdditions(
        boolean subpCONTRATOAllowAdditions)
    {
        this.subpCONTRATOAllowAdditions = subpCONTRATOAllowAdditions;
    }

    public boolean isSubpCONTRATOAllowDeletions()
    {
        return subpCONTRATOAllowDeletions;
    }

    public void setSubpCONTRATOAllowDeletions(
        boolean subpCONTRATOAllowDeletions)
    {
        this.subpCONTRATOAllowDeletions = subpCONTRATOAllowDeletions;
    }

    public boolean isSubpCONTRATOAllowEdits()
    {
        return subpCONTRATOAllowEdits;
    }

    public void setSubpCONTRATOAllowEdits(boolean subpCONTRATOAllowEdits)
    {
        this.subpCONTRATOAllowEdits = subpCONTRATOAllowEdits;
    }

    public boolean isBloqueadoComando89()
    {
        return bloqueadoComando89;
    }

    public void setBloqueadoComando89(boolean bloqueadoComando89)
    {
        this.bloqueadoComando89 = bloqueadoComando89;
    }

    public boolean isBloqueadoComando291()
    {
        return bloqueadoComando291;
    }

    public void setBloqueadoComando291(boolean bloqueadoComando291)
    {
        this.bloqueadoComando291 = bloqueadoComando291;
    }

    public boolean isBloqueadoConfirmar()
    {
        return bloqueadoConfirmar;
    }

    public void setBloqueadoConfirmar(boolean bloqueadoConfirmar)
    {
        this.bloqueadoConfirmar = bloqueadoConfirmar;
    }

    public boolean isBloqueadoFuenteRecursos()
    {
        return bloqueadoFuenteRecursos;
    }

    public void setBloqueadoFuenteRecursos(boolean bloqueadoFuenteRecursos)
    {
        this.bloqueadoFuenteRecursos = bloqueadoFuenteRecursos;
    }

    public boolean isBloqueadoAuxiliar()
    {
        return bloqueadoAuxiliar;
    }

    public void setBloqueadoAuxiliar(boolean bloqueadoAuxiliar)
    {
        this.bloqueadoAuxiliar = bloqueadoAuxiliar;
    }

    public boolean isBloqueadodestino()
    {
        return bloqueadodestino;
    }

    public void setBloqueadodestino(boolean bloqueadodestino)
    {
        this.bloqueadodestino = bloqueadodestino;
    }

    public Integer getVNumeracionunica()
    {
        return vNumeracionunica;
    }

    public void setVNumeracionunica(Integer vNumeracionunica)
    {
        this.vNumeracionunica = vNumeracionunica;
    }

    public boolean isBloqueadoValorTotalF()
    {
        return bloqueadoValorTotalF;
    }

    public void setBloqueadoValorTotalF(boolean bloqueadoValorTotalF)
    {
        this.bloqueadoValorTotalF = bloqueadoValorTotalF;
    }

    public boolean isBloqueadofechaFinal()
    {
        return bloqueadofechaFinal;
    }

    public void setBloqueadofechaFinal(boolean bloqueadofechaFinal)
    {
        this.bloqueadofechaFinal = bloqueadofechaFinal;
    }

    public boolean isVisibleVerificacion306()
    {
        return visibleVerificacion306;
    }

    public void setVisibleVerificacion306(boolean visibleVerificacion306)
    {
        this.visibleVerificacion306 = visibleVerificacion306;
    }

    public Object getLblTitulo()
    {
        return lblTitulo;
    }

    public void setLblTitulo(String lblTitulo)
    {
        this.lblTitulo = lblTitulo;
    }

    public boolean isVerificacion306()
    {
        return verificacion306;
    }

    public void setVerificacion306(boolean verificacion306)
    {
        this.verificacion306 = verificacion306;
    }

    public Object getVFormato()
    {
        return vformato;
    }

    public void setVFormato(String vFormato)
    {
        this.vformato = vFormato;
    }

    public String getVTxtTipoModelo()
    {
        return vTxtTipoModelo;
    }

    public void setVTxtTipoModelo(String vTxtTipoModelo)
    {
        this.vTxtTipoModelo = vTxtTipoModelo;
    }

    public String getVTxtRedondeoGranTotal()
    {
        return vTxtRedondeoGranTotal;
    }

    public void setVTxtRedondeoGranTotal(String vTxtRedondeoGranTotal)
    {
        this.vTxtRedondeoGranTotal = vTxtRedondeoGranTotal;
    }

    public String getVTxtDigitosRedondeoGranTotal()
    {
        return vTxtDigitosRedondeoGranTotal;
    }

    public void setVTxtDigitosRedondeoGranTotal(
        String vTxtDigitosRedondeoGranTotal)
    {
        this.vTxtDigitosRedondeoGranTotal = vTxtDigitosRedondeoGranTotal;
    }

    public String getNombCesion()
    {
        return nombCesion;
    }

    public void setNombCesion(String nombCesion)
    {
        this.nombCesion = nombCesion;
    }

    public boolean isManejaActualizacionPlanDeCompras()
    {
        return manejaActualizacionPlanDeCompras;
    }

    public void setManejaActualizacionPlanDeCompras(
        boolean manejaActualizacionPlanDeCompras)
    {
        this.manejaActualizacionPlanDeCompras = manejaActualizacionPlanDeCompras;
    }

    public String getBotonOrigenAlerta()
    {
        return botonOrigenAlerta;
    }

    public void setBotonOrigenAlerta(String botonOrigenAlerta)
    {
        this.botonOrigenAlerta = botonOrigenAlerta;
    }

    public RegistroDataModelImpl getListaPlantilla()
    {
        return listaPlantilla;
    }

    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla)
    {
        this.listaPlantilla = listaPlantilla;
    }

    public String getPlantilla()
    {
        return plantilla;
    }

    public void setPlantilla(String plantilla)
    {
        this.plantilla = plantilla;
    }

    public Double getTxtSubtotal()
    {
        return txtSubtotal;
    }

    public void setTxtSubtotal(Double txtSubtotal)
    {
        this.txtSubtotal = txtSubtotal;
    }

    public Double getTxtIva()
    {
        return txtIva;
    }

    public void setTxtIva(Double txtIva)
    {
        this.txtIva = txtIva;
    }

    public Double getTxtTotalDescuento()
    {
        return txtTotalDescuento;
    }

    public void setTxtTotalDescuento(Double txtTotalDescuento)
    {
        this.txtTotalDescuento = txtTotalDescuento;
    }

    public Double getTxtTotal()
    {
        return txtTotal;
    }

    public void setTxtTotal(Double txtTotal)
    {
        this.txtTotal = txtTotal;
    }

    public Double getTxtAjustealpeso()
    {
        return txtAjustealpeso;
    }

    public void setTxtAjustealpeso(Double txtAjustealpeso)
    {
        this.txtAjustealpeso = txtAjustealpeso;
    }

    public String getFechaFormato()
    {
        return fechaFormato;
    }

    public void setFechaFormato(String fechaFormato)
    {
        this.fechaFormato = fechaFormato;
    }

    public boolean isRetorno()
    {
        return retorno;
    }

    public void setRetorno(boolean retorno)
    {
        this.retorno = retorno;
    }

    public String getManeja()
    {
        return maneja;
    }

    public void setManeja(String maneja)
    {
        this.maneja = maneja;
    }

    public String getTipoFormato()
    {
        return tipoFormato;
    }

    public void setTipoFormato(String tipoFormato)
    {
        this.tipoFormato = tipoFormato;
    }

    public String getModeloPlantillaPolizas()
    {
        return modeloPlantillaPolizas;
    }

    public void setModeloPlantillaPolizas(String modeloPlantillaPolizas)
    {
        this.modeloPlantillaPolizas = modeloPlantillaPolizas;
    }

    public RegistroDataModelImpl getListaRubros()
    {
        return listaRubros;
    }

    public void setListaRubros(RegistroDataModelImpl listaRubros)
    {
        this.listaRubros = listaRubros;
    }

    public Date getFechaFormatoPolizas()
    {
        return fechaFormatoPolizas;
    }

    public void setFechaFormatoPolizas(Date fechaFormatoPolizas)
    {
        this.fechaFormatoPolizas = fechaFormatoPolizas;
    }

    public boolean isReemplazar()
    {
        return reemplazar;
    }

    public void setReemplazar(boolean reemplazar)
    {
        this.reemplazar = reemplazar;
    }

    public String getDisponibilidadConcatenado()
    {
        return disponibilidadConcatenado;
    }

    public void setDisponibilidadConcatenado(String disponibilidadConcatenado)
    {
        this.disponibilidadConcatenado = disponibilidadConcatenado;
    }

    public String getReservaConcatenado()
    {
        return reservaConcatenado;
    }

    public void setReservaConcatenado(String reservaConcatenado)
    {
        this.reservaConcatenado = reservaConcatenado;
    }

    public String getNombreDependencia()
    {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia)
    {
        this.nombreDependencia = nombreDependencia;
    }

    public boolean isBloqueaConAdi()
    {
        return bloqueaConAdi;
    }

    public void setBloqueaConAdi(boolean bloqueaConAdi)
    {
        this.bloqueaConAdi = bloqueaConAdi;
    }

    /**
     * Retorna la variable calcularMismoDia
     *
     * @return calcularMismoDia
     */
    public boolean isCalcularMismoDia()
    {
        return calcularMismoDia;
    }

    /**
     * Asigna la variable calcularMismoDia
     *
     * @param calcularMismoDia
     * Variable a asignar en calcularMismoDia
     */
    public void setCalcularMismoDia(boolean calcularMismoDia)
    {
        this.calcularMismoDia = calcularMismoDia;
    }

    /**
     * Retorna la variable claseDisponibilidaD
     *
     * @return claseDisponibilidaD
     */
    public String getClaseDisponibilidaD()
    {
        return claseDisponibilidaD;
    }

    /**
     * Asigna la variable claseDisponibilidaD
     *
     * @param claseDisponibilidaD
     * Variable a asignar en claseDisponibilidaD
     */
    public void setClaseDisponibilidaD(String claseDisponibilidaD)
    {
        this.claseDisponibilidaD = claseDisponibilidaD;
    }

    /**
     * Retorna la variable mostrarDialogoConfirmar
     *
     * @return mostrarDialogoConfirmar
     */
    public boolean isMostrarDialogoConfirmar()
    {
        return mostrarDialogoConfirmar;
    }

    /**
     * Asigna la variable mostrarDialogoConfirmar
     *
     * @param mostrarDialogoConfirmar
     * Variable a asignar en mostrarDialogoConfirmar
     */
    public void setMostrarDialogoConfirmar(boolean mostrarDialogoConfirmar)
    {
        this.mostrarDialogoConfirmar = mostrarDialogoConfirmar;
    }

    /**
     * Retorna la variable valorFinalContrato
     *
     * @return valorFinalContrato
     */
    public BigDecimal getValorFinalContrato()
    {
        return valorFinalContrato;
    }

    /**
     * Asigna la variable valorFinalContrato
     *
     * @param valorFinalContrato
     * Variable a asignar en valorFinalContrato
     */
    public void setValorFinalContrato(BigDecimal valorFinalContrato)
    {
        this.valorFinalContrato = valorFinalContrato;
    }

    public boolean isMuestraCambiarConsecutivo()
    {
        return muestraCambiarConsecutivo;
    }

    public void setMuestraCambiarConsecutivo(
        boolean muestraCambiarConsecutivo)
    {
        this.muestraCambiarConsecutivo = muestraCambiarConsecutivo;
    }

    public String getNuevoConsecutivo()
    {
        return nuevoConsecutivo;
    }

    public void setNuevoConsecutivo(String nuevoConsecutivo)
    {
        this.nuevoConsecutivo = nuevoConsecutivo;
    }

    public boolean isCambiarActivo()
    {
        return cambiarActivo;
    }

    public void setCambiarActivo(boolean cambiarActivo)
    {
        this.cambiarActivo = cambiarActivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    public List<Registro> getListaClaseNovedad()
    {
        return listaClaseNovedad;
    }

    public void setListaClaseNovedad(List<Registro> listaClaseNovedad)
    {
        this.listaClaseNovedad = listaClaseNovedad;
    }

    public List<Registro> getListaNumeroPpto()
    {
        return listaNumeroPpto;
    }

    public void setListaNumeroPpto(List<Registro> listaNumeroPpto)
    {
        this.listaNumeroPpto = listaNumeroPpto;
    }

    public List<Registro> getListaTipoPpto()
    {
        return listaTipoPpto;
    }

    public void setListaTipoPpto(List<Registro> listaTipoPpto)
    {
        this.listaTipoPpto = listaTipoPpto;
    }

    public List<Registro> getListaTipo()
    {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo)
    {
        this.listaTipo = listaTipo;
    }

    public List<Registro> getListaCuadroCombinado25()
    {
        return listaCuadroCombinado25;
    }

    public void setListaCuadroCombinado25(
        List<Registro> listaCuadroCombinado25)
    {
        this.listaCuadroCombinado25 = listaCuadroCombinado25;
    }

    public List<Registro> getListaFuenteRecursos()
    {
        return listaFuenteRecursos;
    }

    public void setListaFuenteRecursos(List<Registro> listaFuenteRecursos)
    {
        this.listaFuenteRecursos = listaFuenteRecursos;
    }

    public List<Registro> getListaDestino()
    {
        return listaDestino;
    }

    public void setListaDestino(List<Registro> listaDestino)
    {
        this.listaDestino = listaDestino;
    }

    public List<Registro> getListaCEDULAINTERVENTOR()
    {
        return listaCEDULAINTERVENTOR;
    }

    public void setListaCEDULAINTERVENTOR(
        List<Registro> listaCEDULAINTERVENTOR)
    {
        this.listaCEDULAINTERVENTOR = listaCEDULAINTERVENTOR;
    }

    public List<Registro> getListaSeleccionados()
    {
        return listaSeleccionados;
    }

    public void setListaSeleccionados(List<Registro> listaSeleccionados)
    {
        this.listaSeleccionados = listaSeleccionados;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaClaseDisponibilidad()
    {
        return listaClaseDisponibilidad;
    }

    public void setListaClaseDisponibilidad(
        RegistroDataModelImpl listaClaseDisponibilidad)
    {
        this.listaClaseDisponibilidad = listaClaseDisponibilidad;
    }

    public RegistroDataModelImpl getListaInterventor()
    {
        return listaInterventor;
    }

    public void setListaInterventor(RegistroDataModelImpl listaInterventor)
    {
        this.listaInterventor = listaInterventor;
    }

    public RegistroDataModelImpl getListaTercero()
    {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero)
    {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaNumeroAfectado()
    {
        return listaNumeroAfectado;
    }

    public void setListaNumeroAfectado(
        RegistroDataModelImpl listaNumeroAfectado)
    {
        this.listaNumeroAfectado = listaNumeroAfectado;
    }

    public RegistroDataModelImpl getListaNitCesion()
    {
        return listaNitCesion;
    }

    public void setListaNitCesion(RegistroDataModelImpl listaNitCesion)
    {
        this.listaNitCesion = listaNitCesion;
    }

    public RegistroDataModelImpl getListaModelos()
    {
        return listaModelos;
    }

    public void setListaModelos(RegistroDataModelImpl listaModelos)
    {
        this.listaModelos = listaModelos;
    }

    public RegistroDataModelImpl getListaOrdenador()
    {
        return listaOrdenador;
    }

    public void setListaOrdenador(RegistroDataModelImpl listaOrdenador)
    {
        this.listaOrdenador = listaOrdenador;
    }

    public RegistroDataModelImpl getListaAuxiliar()
    {
        return listaAuxiliar;
    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
    {
        this.listaAuxiliar = listaAuxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroAuxTercero()
    {
        return registroAuxTercero;
    }

    public void setRegistroAuxTercero(Registro registroAuxTercero)
    {
        this.registroAuxTercero = registroAuxTercero;
    }
    // </SET_GET_ADICIONALES>
    public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}
	/**
	 * @return the visibleAdjuntos
	 */
	public boolean isVisibleAdjuntos() {
		return visibleAdjuntos;
	}

	/**
	 * @param visibleAdjuntos the visibleAdjuntos to set
	 */
	public void setVisibleAdjuntos(boolean visibleAdjuntos) {
		this.visibleAdjuntos = visibleAdjuntos;
	}
	
}
