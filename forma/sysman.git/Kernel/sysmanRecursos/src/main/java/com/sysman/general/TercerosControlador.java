package com.sysman.general;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TercerosControladorEnum;
import com.sysman.general.enums.TercerosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParametrosTercero;
import com.sysman.util.rest.ParametrosTerceroLote;
import com.sysman.util.rest.RespuestaApi;

import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author dsuesca
 * @version 1, 21/09/2015
 * @version 2, 07/04/2017 modificado por jcrodriguez
 * DESCRIPCION:*Depuracion del controlador *Refactoring y adicon de
 * DSS para las consultas
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class TercerosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * variable que almacena la compañia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * lista los tipos de documentos
     */
    private List<Registro> listaTipoDocumentoCNT;
    /**
     * listas las clases
     */
    private List<Registro> listaClase;
    /**
     * lista los regimenes
     */
    private List<Registro> listaRegimen;
    /**
     * lista los tipo de contratistas
     */
    private List<Registro> listaTipoContratista;
    /**
     * variable que almacena el estado para volver bloqueado o
     * desbloqueado el campo nit
     */
    private boolean bloqueadoNit;
    /**
     * variable que almacena el registro del formulario participaicon
     * de consorciados
     */
    private Registro registroSubSecundario296;

    /**
     * Atributo de referencia para el subformulario
     * TerceroContratosSub
     */
    private Registro registroSubTerceroContratosSub;

    /**
     * Atributo de referencia para el subformulario
     * ResponsabilidadesFiscales
     */
    private Registro registroSubResponsabilidadesFiscales;
    /**
     * Atributo de referencia para el subformulario DeudorSolidario
     */
    private Registro registroSubDeudorSolidario;
    /**
     * variable que almacena el estado del tercero por apellido
     */
    private boolean manejaTerceroPorApellido;
    /**
     * variable que almacena el estado de la restriccion
     */
    private boolean manejaRestriccion;
    /**
     * variable que almacena el estado dg actualizar
     */
    private boolean dgActualizarVisible;
    /**
     * variable que almacena el estado visible consorciado
     */
    private boolean visibleConsorciados;
    /**
     * variable que almacena el estado visible de pagos
     */
    private boolean visiblePagos;

    /**
     * 
     */

    private boolean visibleTerceroContratos;
    /**
     * 
     * 
     */

    private boolean visibleDependiente;

    /**
     * variable que almacena el estado del tercero pagos
     */
    private boolean activaTerceroPagos;
    /**
     * variable que almacena el estado del grado
     */
    private boolean iblgradovisible;
    /**
     * variable que almacena el estado
     */
    private boolean iblunidadVisible;
    /**
     * variable que almacena el estado de la unidad
     */
    private boolean unidadVisible;
    /**
     * variable que almacena el estado del grado
     */
    private boolean gradoVisible;
    /**
     * variable que almacena el estado de la fuerza visible
     */
    private boolean fuerzaVisible;
    /**
     * variable que almacena el estado de la etiqueta4
     */
    private boolean etiqueta4Visible;
    /**
     * variable que almacena el estado del campo obligaotrio tercero
     */
    private boolean campoObligatorioTercero;
    /**
     * variable que almacena el estado del tipo asociado tercero
     */
    private boolean tipoAsociadoBloqueado;
    /**
     * variable que almacena el estado del campo obligatorio tercero
     */
    private boolean campoObligatorioTerceroyN;
    /**
     * variable que almacena el estado para habilitar el embargo
     */
    private boolean habilitarEmbargos;
    /**
     * variable que almacena un mensaje al actualizar
     */
    private String msgConfirmarActualizar;
    /**
     * variable que alamcena el nombre
     */
    private String nombre;
    /**
     * variable que alamcena la sucursal
     */
    private String sucursal;
    /**
     * variable que alamcena el registro de terceros pagos
     */
    private Registro registroSubTerceroPagosSub;
    /**
     * lista la sucursal
     */
    private List<Registro> listaSucursal;
    /**
     * lista la camara de comercio
     */
    private List<Registro> listaCamaraComercio;
    /**
     * lista los paises
     */
    private List<Registro> listaPais;
    /**
     * lista los departamentos
     */
    private List<Registro> listaDepartamento;
    /**
     * lista las ciudades
     */
    private List<Registro> listaCiudad;
    /**
     * lista las unidades
     */
    private List<Registro> listaUNIDAD;
    /**
     * lista los grados
     */
    private List<Registro> listaGRADO;
    /**
     * lista las fuerzas
     */
    private List<Registro> listaFUERZA;
    /**
     * lista las zonas
     */
    private List<Registro> listaZona;
    /**
     * lista los años
     */
    private List<Registro> listaAno;
    /**
     * lista los bancos
     */
    private List<Registro> listaBanco;
    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaAnoContrato;
    /**
     * lista la informacino para el sub formulario tercero pagos
     */
    private List<Registro> listaTerceropagossub;
    /**
     * lista la informacino para el sub formulario de consorciados
     */
    private List<Registro> listaSecundario296;

    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listaTercerocontratossub;

    /**
     * lista que carga las responsabilidades fiscales del tercero
     */
    private List<Registro> listaResponsabilidadesfiscales;

    /**
     * Lista que carga los deudores solidarios
     */
    private List<Registro> listaDeudorsolidario;

    /**
     * lista la informacino para el sub formulario consorciados
     */
    private List<Registro> listaTipoEmbargo;
    /**
     * lista los nit
     */
    private RegistroDataModelImpl listaNIT;
    /**
     * lista los nit
     */
    private RegistroDataModelImpl listaNITE;
    /**
     * variable auxiliar que almacena una cadena de caracteres
     */
    private String auxiliar;

    /**
     * variable auxiliar que almacena una cadena de caracteres
     */
    private String auxiliarNombre;

    /**
     * variable auxiliar que almacena una cadena de caracteres
     */
    private String auxiliarTipoId;

    /**
     * Lista que carga los codigos de responsabilidad fiscal
     */
    private RegistroDataModelImpl listaCodigoRF;
    /**
     * Lista que carga los codigos de responsabilidad fiscal en la
     * grilla
     */
    private RegistroDataModelImpl listaCodigoRFE;

    /**
     * Lista que carga los terceros
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lsita que carga los terceros en grilla
     */
    private RegistroDataModelImpl listaTerceroE;

    /**
     * Tipo de cobro que viene del formulario Faturacion de Conceptos
     */
    private String tipoCobro;

    /**
     * Tipo de cobro que viene del formulario Faturacion de Conceptos
     */
    private String anioFacturacion;
    /**
     * lista
     */
    private RegistroDataModelImpl listaBuscar;
    /**
     * lista los nombres
     */
    private RegistroDataModelImpl listaBuscarNombre;
    /**
     * lista el nit del apoderado
     */
    private RegistroDataModelImpl listaNITAPODERADO;

    /**
     * Lista de registros de la tabla retenciones_ciiu
     */
    private RegistroDataModelImpl listaCodigoICA;

    private RegistroDataModelImpl listaCiudadExpedicion;
    
    private RegistroDataModelImpl listaCodCiiu;
    
    private RegistroDataModelImpl listaCodCiiuE;
    /**
     * Llave proveniente del formulario PContrato.
     */
    private Map<String, Object> ridPContrato;

    private Map<String, Object> ridDatosPersonales;

    private Map<String, Object> ridFacturacionConceptos;

    /**
     * variable que almacena la vigencia
     */
    private String vigencia;
    /**
     * variable que almacena la clase
     */
    private String claseF;
    /**
     * variable que almacena el titulo
     */
    private String titulo;
    /**
     * variable que almacena el mes inicial
     */
    private String mesIni;
    /**
     * variable que almacena el mes final
     */
    private String mesFin;
    /**
     * variable que almacena el año
     */
    private String anio;
    /**
     * variable que almacena el figito de verificacion
     */
    private String digitoVerificacion;
    /**
     * variable que almacena un mensaje de alerta de comprobantes de
     * embargo
     */
    private String manejaAlertaEmbargoComprobantes;

    private String validarSucursal;
    private boolean bloqueaSucursal;
    
    
	private RegistroDataModelImpl listaNUMEROLISTA;
	
	private RegistroDataModelImpl listaActividadeconomica;
    /**
     * variable Ejb
     */
    @EJB
    EjbSysmanUtilRemote ejbSysmanUtil;
    private Map<String, Object> ridWorkFlow;
    private Map<String, Object> ridContabilidad;
    private String anioContabilidad;
    private String mesContabilidad;
    private String tipoMovContabilidad;
    private String estadoComprobante;
    private String opcionMenuContabilidad;
    private boolean visibleOrdenador;
    private boolean visibleDgCompPptal;
    private boolean visibletipoEntidad;
	private String nitCompania;
    private static final String URL_SERVICIO_REST= "URL SERVICIO REST";
    private List<Registro> listaDatostercero;
    
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    private Registro registroSubdatosTercero;
    
    private Registro registroSubactividadEconomica;
	private String descripActividad;
    
    public TercerosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();
        if (nitCompania.toString().contains("-")) {
        	nitCompania = nitCompania.toString().split("-")[0];
		}
        try {
            numFormulario = GeneralCodigoFormaEnum.TERCEROS_CONTROLADOR
                            .getCodigo();

            registroSubSecundario296 = new Registro(
                            new HashMap<String, Object>());

            registro = new Registro(new HashMap<String, Object>());
            registroSubTerceroPagosSub = new Registro(
                            new HashMap<String, Object>());

            registroSubTerceroContratosSub = new Registro(
                            new HashMap<String, Object>());

            registroSubResponsabilidadesFiscales = new Registro(
                            new HashMap<String, Object>());

            registroSubDeudorSolidario = new Registro(
                            new HashMap<String, Object>());
            
            registroSubdatosTercero = new Registro(
                    new HashMap<String, Object>());
            
            registroSubactividadEconomica = new Registro(new HashMap<String, Object>());

            dgActualizarVisible = false;
            manejaRestriccion = false;
            listaDatostercero = new ArrayList<>();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                ridPContrato = (Map<String, Object>) parametrosEntrada
                                .get("rowidPcontrato");

                ridWorkFlow = (Map<String, Object>) parametrosEntrada
                                .get("rowidWorkFlow");

                ridFacturacionConceptos = (Map<String, Object>) parametrosEntrada
                                .get("rowidFacturacionConcepto");

                ridContabilidad = (Map<String, Object>) parametrosEntrada
                                .get("rowidContabilidad");

                if (ridContabilidad != null) {

                    anioContabilidad = parametrosEntrada
                                    .get("anio").toString();

                    mesContabilidad = parametrosEntrada
                                    .get("mes").toString();

                    tipoMovContabilidad = parametrosEntrada
                                    .get("tipoMov").toString();

                    estadoComprobante = parametrosEntrada
                                    .get("estadoComprobante").toString();

                    opcionMenuContabilidad = parametrosEntrada
                                    .get("opcionMenu").toString();

                    visibleOrdenador = (Boolean) parametrosEntrada
                                    .get("visibleOrdenador");

                    visibleDgCompPptal = (Boolean) parametrosEntrada
                                    .get("visibleDgCompPptal");

                }

                vigencia = cadenaVaciaH(parametrosEntrada, "vigencia");
                claseF = cadenaVaciaH(parametrosEntrada, "claseF");
                titulo = cadenaVaciaH(parametrosEntrada, "titulo");
                anioFacturacion = cadenaVaciaH(parametrosEntrada, "anio");
                tipoCobro = cadenaVaciaH(parametrosEntrada, "tipoCobro");
            }
            validarPermisos();
        }
        catch (SysmanException ex) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que valida el casteo a toString
     *
     * @param campos
     * @param var
     * @return
     */
    private String cadenaVaciaH(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    /**
     * metodo que se llama cuando se inicializa el formulario
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.TERCERO;
        buscarLlave();
        asignarOrigenDatos();

    }

    /**
     * metodo que se lalama para asignar los datos del formulario
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * metodo que se ejecuta al cerrar
     */
    public void ejecutarrcCerrar() {
        SessionUtil.cleanFlash();
        String menuActual;
        menuActual = SessionUtil.getMenuActual();
        menuActual = menuActual == null ? "NULL" : menuActual;
        // Si el formulario se abrio desde el formulario PContrato
        if ("90202".equals(menuActual) || "10020201".equals(menuActual)) {
            String ruta = "/pcontrato.sysman";
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("vigencia", vigencia);
            parametros.put("claseF", claseF);
            parametros.put("titulo", titulo);
            parametros.put("ridPcontrato", ridPContrato);
            Direccionador direccionador = new Direccionador();
            direccionador.setRuta(ruta);
            direccionador.setParametros(parametros);
            SessionUtil.redireccionar(direccionador);
        }

        else if ("21010203".equals(menuActual)) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", ridDatosPersonales);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else if ("690201".equals(menuActual)) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", ridFacturacionConceptos);
            parametros.put("anio", anioFacturacion);
            parametros.put("tipoCobro", tipoCobro);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FACTURACIONCONCEPTOS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else if ("350107".equals(menuActual)) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", ridWorkFlow);
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.FRM_PROCEDENCIA_TRAMITE_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else if (ridContabilidad != null) {

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rid", ridContabilidad);
            parametros.put("ano", anioContabilidad);

            parametros.put("mes", mesContabilidad);

            parametros.put("tipoMov", tipoMovContabilidad);

            parametros.put("generar", estadoComprobante);

            parametros.put("opcionMenu", opcionMenuContabilidad);

            parametros.put("visibleOrdenador", visibleOrdenador);

            parametros.put("indicadorNoPpto", visibleDgCompPptal);

            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer.toString(
                            GeneralCodigoFormaEnum.COMPROBANTECNTS_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else {
            SessionUtil.redireccionarMenu();
        }

    }

    /**
     * metodo que inicializa o carga los combos grandes y pequeños
     */
    @Override
    public void iniciarListas() {
        cargarListaTipoDocumentoCNT();
        cargarListaClase();
        cargarListaRegimen();
        cargarListaTipoContratista();
        cargarListaNIT();
        cargarListaNITE();
        cargarListaBuscar();
        cargarListaBuscarNombre();
        cargarListaNITAPODERADO();
        cargarListaCamaraComercio();
        cargarListaPais();
        cargarListaUNIDAD();
        cargarListaGRADO();
        cargarListaFUERZA();
        cargarListaZona();
        cargarListaAno();
        cargarListaBanco();
        cargarListaTipoEmbargo();
        cargarListaAnoContrato();
        cargarListaCodigoICA();
        cargarListaCiudadExpedicion();
        cargarListaCodigoRF();
        cargarListaCodigoRFE();
        cargarListaTercero();
        cargarListaTerceroE();
		cargarListaNUMEROLISTA();
		cargarListaCodCiiu(); 
		cargarListaCodCiiuE();
    }

    /**
     * metodo que inicializa o carga los combos grandes y pequeños
     */
    @Override
    public void iniciarListasSub() {
        cargarListaTerceropagossub();
        cargarListaSecundario296();
        cargarListaTercerocontratossub();
        cargarListaDepartamento();
        cargarListaCiudad();
        cambiarClase();
        cambiarTipoDocumentoCNT();
        cambiarTipoContratista();
        cargarListaResponsabilidadesfiscales();
        cargarListaDeudorsolidario();
        cargarListaDatostercero();
        cargarListaActividadeconomica();
    }

    /**
     * metodo que inicializa las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        listaTerceropagossub = null;
        listaSecundario296 = null;
        listaTercerocontratossub = null;
        listaDeudorsolidario = null;
        listaDatostercero=null;
        listaActividadeconomica = null;
    }

    /**
     * metodo de comprobacion de estado
     *
     * @param registro
     * @return
     */
    public boolean isTerceroVarios(Registro registro) {
        String nitAux = registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()).toString();
        String sucursalAux = registro.getCampos()
                        .get(TercerosControladorEnum.CSUCURSAL.getValue())
                        .toString();
        return nitAux.equals(TercerosControladorEnum.CADENANUEVES.getValue())
            && sucursalAux.equals(TercerosControladorEnum.CADNUEVES.getValue());

    }

    /**
     * metodo de comprobacion de estado
     *
     * @param registro
     * @return
     */
    public boolean hayMovimientosContables(Registro registro) {
        String nitAux = registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()).toString();
        String sucursalAux = registro.getCampos()
                        .get(TercerosControladorEnum.CSUCURSAL.getValue())
                        .toString();

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TercerosControladorEnum.NIT.getValue(), nitAux);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursalAux);

        try {
            List<Registro> lista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL14580
                                                                            .getValue())
                                            .getUrl(), param));
            return !lista.isEmpty();
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    /**
     * metodo de comprobacion de estado
     *
     * @param registro
     * @return
     */
    public boolean hayMovimientosPresupuestales(Registro registro) {
        String nitAux = registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()).toString();
        String sucursalAux = registro.getCampos()
                        .get(TercerosControladorEnum.CSUCURSAL.getValue())
                        .toString();
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TercerosControladorEnum.NIT.getValue(), nitAux);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursalAux);
        try {
            List<Registro> lista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL15266
                                                                            .getValue())
                                            .getUrl(), param));
            return !lista.isEmpty();
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    public void cargarListaDatostercero() {
    	APIFrida apiFrida = new APIFrida();
    	listaDatostercero = new ArrayList<>();
    	String url;
		try {
			url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_REST, "69", new Date(), false);
		
			String respuesta = apiFrida.cargarTercero(nitCompania,
	        		registro.getCampos().get("NIT")
	                                        .toString().trim(),
	                        url);
			
			Gson gson = new Gson();
	        RespuestaApi respuestaApi = gson.fromJson(respuesta,
	                        RespuestaApi.class);
	        
	        Map<String, Object> datos = (Map<String, Object>) respuestaApi.getCuerpo();
			
	        if(respuestaApi.getCodigo() == 0) {
		        registroSubdatosTercero.getCampos().put("NIT",registro.getCampos().get("NIT")
	                    .toString().trim());
		        registroSubdatosTercero.getCampos().put("RESPOFISCALES",SysmanFunciones.nvl(datos.get("responsabilidadesfiscales")
	                    ,""));	        
		        registroSubdatosTercero.getCampos().put("NOMBRE",SysmanFunciones.nvl(datos.get("nombretercero")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("APELLIDO",SysmanFunciones.nvl(datos.get("apellidotercero")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("TIPOPERSONA",SysmanFunciones.nvl(datos.get("tipoorganizacion")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("REGIMEN",SysmanFunciones.nvl(datos.get("tiporegimen")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("DIRECCION",SysmanFunciones.nvl(datos.get("direccion")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("DIRECCIONFISCAL",SysmanFunciones.nvl(datos.get("direccionfiscal")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("CODIGOPOSTAL",SysmanFunciones.nvl(datos.get("codigopostal")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("TELEFONOS",SysmanFunciones.nvl(datos.get("telefono")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("EMAIL",SysmanFunciones.nvl(datos.get("correoelectronico")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("CIUDAD",SysmanFunciones.nvl(datos.get("ciudad")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("DEPARTAMENTO",SysmanFunciones.nvl(datos.get("departamento")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("PAIS",SysmanFunciones.nvl(datos.get("pais")
	                    ,""));
		        registroSubdatosTercero.getCampos().put("TIPOID",SysmanFunciones.nvl(datos.get("tipoidentificacion")
	                    ,""));
		        
		        listaDatostercero.add(registroSubdatosTercero);
	        }else {
	        	listaDatostercero = new ArrayList<>();
	        }
	        
		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {
			Logger.getLogger(TercerosControlador.class.getName())
            					.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    	
    	
    }
    /**
     * metodo que se llama para cargar la informacion del sub
     * formulario terceros pagos
     */
    public void cargarListaTerceropagossub() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROPAGOS
                                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TercerosControladorEnum.NIT.getValue(), registro
                            .getCampos()
                            .get(TercerosControladorEnum.NIT.getValue()));
            param.put(GeneralParameterEnum.SUCURSAL.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.SUCURSAL.getName()));

            listaTerceropagossub = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            TercerosControladorEnum.TERCEROPAGOS
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    /**
     * metodo que se llama para cargar la informacion del sub
     * formulario de consorciados
     */
    public void cargarListaSecundario296() {

        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSORCIADOS
                                                            .getGridKey());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TercerosControladorEnum.NIT.getValue(), registro
                            .getCampos()
                            .get(TercerosControladorEnum.NIT.getValue()));

            listaSecundario296 = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            TercerosControladorEnum.CONSORCIADOS
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTercerocontratossub() {
        try {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROOPS
                                                            .getGridKey());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TercerosControladorEnum.NIT.getValue(), registro
                            .getCampos()
                            .get(TercerosControladorEnum.NIT.getValue()));
            param.put(GeneralParameterEnum.SUCURSAL.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.SUCURSAL.getName()));

            listaTercerocontratossub = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            TercerosControladorEnum.TERCEROOPS
                                                            .getValue()));
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }
    
    /**
     * 
     * Carga la lista listaActividadeconomica
     *
     */
    public void cargarListaActividadeconomica(){
    	  try {

              UrlBean urlBean = UrlServiceUtil.getInstance()
                      .getUrlServiceByUrlByEnumID(GenericUrlEnum.ACT_ECONOMICA_TERCERO.getGridKey());
              Map<String, Object> param = new TreeMap<>();
              param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
              param.put(GeneralParameterEnum.TERCERO.getName(), registro.getCampos().get(GeneralParameterEnum.NIT.getName()));
              param.put(GeneralParameterEnum.SUCURSAL.getName(), registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
              
              String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                      GenericUrlEnum.ACT_ECONOMICA_TERCERO.getTable());
              
              listaActividadeconomica = new RegistroDataModelImpl(
                      urlBean.getUrl(),
                      urlBean.getUrlConteo().getUrl(),
                      param, rowKey);
              
          } catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
              logger.error(e.getMessage(), e);
              JsfUtil.agregarMensajeError(e.getMessage());
          }
    }

    /**
     * 
     * Carga la lista listaResponsabilidadesfiscales
     *
     */
    public void cargarListaResponsabilidadesfiscales() {
        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCERO_OBFISCALES
                                                            .getGridKey());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TercerosControladorEnum.NIT.getValue(), registro
                            .getCampos()
                            .get(TercerosControladorEnum.NIT.getValue()));
            param.put(GeneralParameterEnum.SUCURSAL.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.SUCURSAL.getName()));

            listaResponsabilidadesfiscales = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.TERCERO_OBFISCALES
                                                            .getTable()));

        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * 
     * Carga la lista listaDeudorsolidario
     * 
     * 
     */
    public void cargarListaDeudorsolidario() {
        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DEUDOR_SOLIDARIO
                                                            .getGridKey());

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TercerosControladorEnum.NIT.getValue(), registro
                            .getCampos()
                            .get(TercerosControladorEnum.NIT.getValue()));
            param.put(GeneralParameterEnum.SUCURSAL.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.SUCURSAL.getName()));

            listaDeudorsolidario = RegistroConverter.toListRegistro(
                            requestManager.getList(urlBean.getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            GenericUrlEnum.DEUDOR_SOLIDARIO
                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama al cargar la lista de la camara de comercio
     */
    public void cargarListaCamaraComercio() {
        try {
            listaCamaraComercio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL17416
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al cargar las lista de paises
     */
    public void cargarListaPais() {
        try {
            listaPais = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL17732
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es lalmado pra cargar la lista de los departamentos
     */
    public void cargarListaDepartamento() {
        Map<String, Object> param = new TreeMap<>();
        param.put(TercerosControladorEnum.PAIS.getValue(), registro.getCampos()
                        .get(TercerosControladorEnum.PAIS.getValue()));

        try {
            listaDepartamento = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL18011
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama para cargar la lista de ciudades
     */
    public void cargarListaCiudad() {
        Map<String, Object> param = new TreeMap<>();

        param.put(TercerosControladorEnum.PAIS.getValue(), registro.getCampos()
                        .get(TercerosControladorEnum.PAIS.getValue()));
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), registro
                        .getCampos()
                        .get(GeneralParameterEnum.DEPARTAMENTO.getName()));

        try {
            listaCiudad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL18599
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama para cargar la lista de unidades
     */
    public void cargarListaUNIDAD() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaUNIDAD = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL19242
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para cargar la lista de grados
     */
    public void cargarListaGRADO() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaGRADO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL19642
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para cargar la lista de fuerzas
     */
    public void cargarListaFUERZA() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TercerosControladorEnum.GRADO.getValue(), registro.getCampos()
                        .get(TercerosControladorEnum.GRADO.getValue()));

        try {
            listaFUERZA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL19655
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que es llamado para cargar la lista de zonas
     */
    public void cargarListaZona() {
        try {
            listaZona = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL21407
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para cargar la lista de años
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL21760
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que carga la lista de bancos
     */
    public void cargarListaBanco() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaBanco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL22227
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga el listado de nit
     */
    public void cargarListaNIT() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL22669
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNIT = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TercerosControladorEnum.NIT.getValue());
    }

    /**
     * metodo que carga el lista de tipos de documentos cnt
     */
    public void cargarListaTipoDocumentoCNT() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoDocumentoCNT = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL23471
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que carga la lista de clase
     */
    public void cargarListaClase() {
        try {
            listaClase = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL23800
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista de regimen
     */
    public void cargarListaRegimen() {
        try {
            listaRegimen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL24085
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista de tipos de contratistas
     */
    public void cargarListaTipoContratista() {
        try {
            listaTipoContratista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL24352
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista de nit
     */
    public void cargarListaNITE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL22669
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNITE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TercerosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaCodigoRF
     *
     */
    public void cargarListaCodigoRF() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL285
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TIPO.getName(), "1");

        listaCodigoRF = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoRF
     *
     */
    public void cargarListaCodigoRFE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL285
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.TIPO.getName(), "1");

        listaCodigoRFE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTercero() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL14195
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NIT.getName());
    }

    /**
     * 
     * Carga la lista listaTercero
     *
     */
    public void cargarListaTerceroE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL14195
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NIT.getName());
    }
    
    
    /**
	 * 
	 * Carga la lista listanumerolista
	 *
	 */
	public void cargarListaNUMEROLISTA() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID( TercerosControladorUrlEnum.URL005.getValue());

		listaNUMEROLISTA = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}
	
	/**
	 * 
	 * Carga la lista listaCodCiiu
	 *
	 */
	public void cargarListaCodCiiu(){
		Map<String, Object> param = new TreeMap<>();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID( TercerosControladorUrlEnum.URL1952001.getValue());

		listaCodCiiu = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true, "COD_ACTIVIDAD");
	}
	/**
	 * 
	 * Carga la lista listaCodCiiu
	 *
	 */
	public void  cargarListaCodCiiuE(){
		listaCodCiiuE = listaCodCiiu;
	}


    /**
     * metodo que carga la lista de buscar
     */
    public void cargarListaBuscar() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL22669
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBuscar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TercerosControladorEnum.NIT.getValue());
    }

    /**
     * metodo que carga la lista de nombres
     */
    public void cargarListaBuscarNombre() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL22669
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBuscarNombre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TercerosControladorEnum.NIT.getValue());
    }

    /**
     * metodo que carga la lista de apoderado
     */
    public void cargarListaNITAPODERADO() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL26834
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNITAPODERADO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TercerosControladorEnum.NIT.getValue());
    }

    /**
     * 
     * Carga la lista listaCodigoICA
     *
     */
    public void cargarListaCodigoICA() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL52186
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoICA = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, TercerosControladorEnum.CIIU.getValue());

    }

    /**
     * metodo que carga la lista de tipo de embargo
     */
    public void cargarListaTipoEmbargo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoEmbargo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL27260
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaAnoContrato
     *
     */
    public void cargarListaAnoContrato() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL21760
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // listaAnoContrato = service.getListado(conectorPool, "SELECT
        // NUMERO " +
        // " FROM ANO " +
        // " WHERE COMPANIA = :COMPANIA");
    }

    /**
     * 
     * Carga la lista listaCiudadExpedicion
     *
     */
    public void cargarListaCiudadExpedicion() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TercerosControladorUrlEnum.URL5012
                                                        .getValue());

        listaCiudadExpedicion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que agrega un registro del subformulario de pagos
     */
    public void agregarRegistroSubTerceropagossub() {
        try {
            registroSubTerceroPagosSub.getCampos().put(
                            TercerosControladorEnum.NIT.getValue(),
                            registro.getCampos().get(TercerosControladorEnum.NIT
                                            .getValue()));
            registroSubTerceroPagosSub.getCampos().put(
                            TercerosControladorEnum.CSUCURSAL.getValue(),
                            registro.getCampos()
                                            .get(TercerosControladorEnum.CSUCURSAL
                                                            .getValue()));
            registroSubTerceroPagosSub.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubTerceroPagosSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubTerceroPagosSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROPAGOS
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubTerceroPagosSub.getCampos());
            cargarListaTerceropagossub();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            registroSubTerceroPagosSub = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * metodo que actualiza un registro del subformulario de pagos
     *
     * @param event
     */
    public void editarRegSubTerceropagossub(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(
                            TercerosControladorEnum.NOMBREBANCO.getValue());
            reg.getCampos().remove(
                            TercerosControladorEnum.LBESTADO_CUENTA.getValue());
            reg.getCampos().remove(
                            TercerosControladorEnum.LBTIPOCUENTA.getValue());
            reg.getCampos().remove(TercerosControladorEnum.LBFECHAAUTORIZACION
                            .getValue());
            reg.getCampos().remove(TercerosControladorEnum.LBFECHAINSCRIPCION
                            .getValue());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(TercerosControladorEnum.NIT.getValue());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROPAGOS
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            cargarListaTerceropagossub();
        }
    }

    /**
     * metodo que es llamado cuando se elimina un registro del
     * subformulario de pagos
     *
     * @param reg
     */
    public void eliminarRegSubTerceropagossub(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROPAGOS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaTerceropagossub();

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    /**
     * metodo que cancela la edicion de los subformularios
     */
    public void cancelarEdicionTerceropagossub() {
        cargarListaTerceropagossub();
        cargarListaSecundario296();
        cargarListaTercerocontratossub();
    }

    private double consultarPorcentajeConsorciados() {
        double porcentajeGrilla = 0;
        for (int i = 0; i < listaSecundario296.size(); i++) {
            Registro reg = listaSecundario296.get(i);
            porcentajeGrilla = porcentajeGrilla + Double.parseDouble(reg
                            .getCampos().get("PORCPARTICIPACION").toString());
        }
        double campo = Double.parseDouble(registroSubSecundario296.getCampos()
                        .get("PORCPARTICIPACION").toString());
        return campo + porcentajeGrilla;
    }

    private boolean validarPorcentaje() {
        double porcentaje = consultarPorcentajeConsorciados();
        if (porcentaje <= 100.00) {
            return true;
        }
        JsfUtil.agregarMensajeAlerta(
                        "la cantidad de porcentaje excede el 100% de participacion en "
                            + (porcentaje - 100));
        return false;
    }

    /**
     * metodo que agrega un registro al subfromulario de consorciados
     */
    public void agregarRegistroSubSecundario296() {
        try {
            if (validarVaciosSecundario296() && validarPorcentaje()) {

                registroSubTerceroPagosSub.getCampos().remove(
                                TercerosControladorEnum.CACTIVA.getValue());
                registroSubSecundario296.getCampos().put(
                                TercerosControladorEnum.NITCONSORCIO.getValue(),
                                registro.getCampos()
                                                .get(TercerosControladorEnum.NIT
                                                                .getValue()));
                registroSubSecundario296.getCampos().put(
                                GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                registroSubSecundario296.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                registroSubSecundario296.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.CONSORCIADOS
                                                                .getCreateKey());

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSubSecundario296.getCampos());
                cargarListaSecundario296();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));
            }

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            "Transacci?n interrumpida." + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            registroSubTerceroPagosSub = new Registro(
                            new HashMap<String, Object>());
            registroSubSecundario296 = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * metodo que se llama cuando se actualiza un registro del
     * subformulario consorciados
     *
     * @param event
     */
    public void editarRegSubSecundario296(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            if (validarVaciosSecundario296E(reg)) {
                reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
                reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
                reg.getCampos().remove(TercerosControladorEnum.NIT.getValue());
                reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                reg.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.CONSORCIADOS
                                                                .getUpdateKey());

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(),
                                reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            cargarListaSecundario296();
        }
    }

    /**
     * metodo que se encarga de validar los camops del subformulario
     *
     * @return
     */
    public boolean validarVaciosSecundario296() {
        String porc = registroSubSecundario296.getCampos()
                        .get(TercerosControladorEnum.CPORCPARTICIPACION
                                        .getValue()) == null ? ""
                                            : registroSubSecundario296
                                                            .getCampos()
                                                            .get(TercerosControladorEnum.CPORCPARTICIPACION
                                                                            .getValue())
                                                            .toString();
        if (porc.isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB28"));
            return false;
        }

        return true;

    }

    /**
     * metodo que se encarga de validar los camops del subformulario
     *
     * @return
     */
    public boolean validarVaciosSecundario296E(Registro reg) {
        if (reg.getCampos().isEmpty()) {
            return true;
        }
        else {
            for (Map.Entry<String, Object> entrySet : reg.getCampos()
                            .entrySet()) {
                String key = entrySet.getKey();
                Object value = entrySet.getValue();

                if ((value == null) || (validarCadena(value)
                    && key.equals(TercerosControladorEnum.CPORCPARTICIPACION
                                    .getValue()))) {

                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TB_TB28"));
                    return false;
                }
                else {
                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TB_TB29"));
                    return false;
                }

            }

        }
        return true;

    }

    /**
     * metodo que se encarga de validar una cadena de un registro si
     * se encuentra vacia o null
     *
     * @param value
     * @return
     */
    private boolean validarCadena(Object value) {
        if (value instanceof String) {
            return (value.toString()).isEmpty();
        }
        else {
            return false;
        }
    }

    /**
     * metodo que se encarga de eliminar un registro del subformulario
     * consorciado
     *
     * @param reg
     */
    public void eliminarRegSubSecundario296(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CONSORCIADOS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            cargarListaSecundario296();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    /**
     * metodo que se llama para cancelar la edicion
     */
    public void cancelarEdicionSecundario296() {
        cargarListaSecundario296();
        cargarListaTercerocontratossub();
    }

    /**
     * Metodo de insercion del formulario Tercerocontratossub
     * 
     */
    public void agregarRegistroSubTercerocontratossub() {
        try {
            registroSubTerceroContratosSub.getCampos().put(
                            TercerosControladorEnum.NIT.getValue(),
                            registro.getCampos().get(TercerosControladorEnum.NIT
                                            .getValue()));
            registroSubTerceroContratosSub.getCampos().put(
                            TercerosControladorEnum.CSUCURSAL.getValue(),
                            registro.getCampos()
                                            .get(TercerosControladorEnum.CSUCURSAL
                                                            .getValue()));
            registroSubTerceroContratosSub.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubTerceroContratosSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubTerceroContratosSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROOPS
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubTerceroContratosSub.getCampos());

            cargarListaTercerocontratossub();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            registroSubTerceroContratosSub = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Tercerocontratossub
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubTercerocontratossub(RowEditEvent event) {
        try {
            Registro reg = (Registro) event.getObject();

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(TercerosControladorEnum.NIT.getValue());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROOPS
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            cargarListaTercerocontratossub();
        }

    }

    /**
     * Metodo de eliminacion del formulario Tercerocontratossub
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubTercerocontratossub(Registro reg) {

        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCEROOPS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaTercerocontratossub();

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Tercerocontratossub
     *
     */
    public void cancelarEdicionTercerocontratossub() {
        cargarListaTercerocontratossub();
    }

    /**
     * Metodo de insercion del formulario Responsabilidadesfiscales
     * 
     */
    public void agregarRegistroSubResponsabilidadesfiscales() {
        try {

            registroSubResponsabilidadesFiscales.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registroSubResponsabilidadesFiscales.getCampos().put(
                            GeneralParameterEnum.IDENTIFICACION.getName(),
                            registro.getCampos().get(GeneralParameterEnum.NIT
                                            .getName()));
            registroSubResponsabilidadesFiscales.getCampos().put(
                            GeneralParameterEnum.SUCURSAL.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));

            registroSubResponsabilidadesFiscales.getCampos().remove(
                            GeneralParameterEnum.CODIGO.getName());

            registroSubResponsabilidadesFiscales.getCampos().remove(
                            GeneralParameterEnum.DESCRIPCION.getName());

            registroSubResponsabilidadesFiscales.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubResponsabilidadesFiscales.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSubResponsabilidadesFiscales.getCampos().put(
                            GeneralParameterEnum.TIPO.getName(),
                            "T");

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCERO_OBFISCALES
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubResponsabilidadesFiscales.getCampos());

            cargarListaResponsabilidadesfiscales();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

        finally {
            registroSubResponsabilidadesFiscales = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Responsabilidadesfiscales
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubResponsabilidadesfiscales(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCERO_OBFISCALES
                                                            .getUpdateKey());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            cargarListaResponsabilidadesfiscales();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaResponsabilidadesfiscales();
        }
    }

    /**
     * Metodo de eliminacion del formulario Responsabilidadesfiscales
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubResponsabilidadesfiscales(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.TERCERO_OBFISCALES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaResponsabilidadesfiscales();

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Responsabilidadesfiscales
     *
     */
    public void cancelarEdicionResponsabilidadesfiscales() {
        cargarListaResponsabilidadesfiscales();
    }

    /**
     * Metodo de insercion del formulario Deudorsolidario
     * 
     */
    public void agregarRegistroSubDeudorsolidario() {
        try {
            registroSubDeudorSolidario.getCampos().put("NIT_DEUDORPRINCIPAL",
                            registro.getCampos().get(GeneralParameterEnum.NIT
                                            .getName()));
            registroSubDeudorSolidario.getCampos().put(
                            "SUCURSAL_DEUDORPRINCIPAL",
                            registro.getCampos()
                                            .get(GeneralParameterEnum.SUCURSAL
                                                            .getName()));
            registroSubDeudorSolidario.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(), compania);

            registroSubDeudorSolidario.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubDeudorSolidario.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSubDeudorSolidario.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DEUDOR_SOLIDARIO
                                                            .getCreateKey());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubDeudorSolidario.getCampos());
            cargarListaDeudorsolidario();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(
                                            TercerosControladorEnum.CTRANSACCIONINTERRUMPIDA
                                                            .getValue())
                                + ex.getMessage());
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            registroSubDeudorSolidario = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Deudorsolidario
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubDeudorsolidario(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DEUDOR_SOLIDARIO
                                                            .getUpdateKey());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(
                            GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaDeudorsolidario();
        }
    }

    /**
     * Metodo de eliminacion del formulario Deudorsolidario
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubDeudorsolidario(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DEUDOR_SOLIDARIO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaDeudorsolidario();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Deudorsolidario
     *
     */
    public void cancelarEdicionDeudorsolidario() {
    	cargarListaDeudorsolidario();
    }

    /**
     * Metodo de insercion del formulario Actividadeconomica
     * 
     */   
    public void agregarRegistroSubActividadeconomica() {
    	try {
    		registroSubactividadEconomica.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		registroSubactividadEconomica.getCampos().put(GeneralParameterEnum.TERCERO.getName(), registro.getCampos().get(GeneralParameterEnum.NIT.getName()));
    		registroSubactividadEconomica.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(), registro.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
			registroSubactividadEconomica.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
			registroSubactividadEconomica.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
    		
    		registroSubactividadEconomica.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
    		
    		UrlBean urlCreate = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(GenericUrlEnum.ACT_ECONOMICA_TERCERO.getCreateKey());

    		requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
    				registroSubactividadEconomica.getCampos());
    		cargarListaActividadeconomica();

    		JsfUtil.agregarMensajeInformativo(
    				idioma.getString("MSM_REGISTRO_INGRESADO"));
    	} catch (SystemException ex) {
    		logger.error(ex.getMessage(),ex);
    		JsfUtil.agregarMensajeError(ex.getMessage());
    	}finally{
    		registroSubactividadEconomica = new Registro(new HashMap<String, Object>());
    	} 



    }
    /**
     * Metodo de edicion del formulario Actividadeconomica
     * 
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubActividadeconomica(RowEditEvent event) {
    	Registro reg = (Registro) event.getObject();
    	try {
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
    	    reg.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());

			String urlEnumId = GenericUrlEnum.ACT_ECONOMICA_TERCERO.getUpdateKey();
			UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaActividadeconomica();
		}
    }
    /**
     * Metodo de eliminacion del formulario Actividadeconomica
     * 
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubActividadeconomica(Registro reg) {
    	try {

			String urlEnumId = GenericUrlEnum.ACT_ECONOMICA_TERCERO.getDeleteKey();
			UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId);
			requestManager.delete(urlDelete.getUrl(), reg.getLlave());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));

			cargarListaActividadeconomica();
		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
    }
    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     * para el subformulario Actividadeconomica
     *
     */
    public void cancelarEdicionActividadeconomica(){
    	cargarListaActividadeconomica();
    }

    /**
     * metodo que se lalma al oprimir el boton de editar
     *
     * @param ac
     */
    public void oprimireditar(ActionEvent ac) {
        // heredado del bean base
    }

    /**
     * metodo que selecciona un registro
     *
     * @param event
     */
    public void seleccionarFilaNIT(SelectEvent event) {

        Registro registroAux = (Registro) event.getObject();
        nombre = registroAux.getCampos()
                        .get(TercerosControladorEnum.CNOMBRE.getValue())
                        .toString();
        sucursal = registroAux.getCampos()
                        .get(TercerosControladorEnum.CSUCURSAL.getValue())
                        .toString();

        registroSubSecundario296.getCampos().put("NIT",
                        registroAux.getCampos().get("NIT"));
        registroSubSecundario296.getCampos().put(
                        TercerosControladorEnum.CNOMBRE.getValue(), nombre);
        registroSubSecundario296.getCampos().put(
                        TercerosControladorEnum.CSUCURSAL.getValue(), sucursal);

    }

    /**
     * metodo que selecciona un registro
     *
     * @param event
     */
    public void seleccionarFilaNITE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        auxiliar = registroAux.getCampos().get("NIT").toString();
        nombre = registroAux.getCampos()
                        .get(TercerosControladorEnum.CNOMBRE.getValue())
                        .toString();
        sucursal = registroAux.getCampos()
                        .get(TercerosControladorEnum.CSUCURSAL.getValue())
                        .toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRF
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRF(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubResponsabilidadesFiscales.getCampos().put(
                        GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        registroSubResponsabilidadesFiscales.getCampos().put(
                        GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION
                                                        .getName()));

        registroSubResponsabilidadesFiscales.getCampos().put("ID_OBFISCAL",
                        registroAux.getCampos().get("ID"));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoRF
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRFE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();

        registroSubResponsabilidadesFiscales.getCampos().put(
                        GeneralParameterEnum.DESCRIPCION.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.DESCRIPCION
                                                        .getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubDeudorSolidario.getCampos().put("NIT_DEUDOR",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()));

        registroSubDeudorSolidario.getCampos().put("SUCURSAL_DEUDOR",
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registroSubDeudorSolidario.getCampos().put("TIPOID",
                        registroAux.getCampos().get("TIPOID"));

        registroSubDeudorSolidario.getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NIT.getName()), "")
                        .toString();

        auxiliarNombre = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),
                                        "")
                        .toString();

        auxiliarTipoId = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get("TIPOID"),
                                        "")
                        .toString();

    }
    
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanumerolista
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaNUMEROLISTA(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		
		registro.getCampos().put("NUMEROLISTA", registroAux.getCampos()
                .get(GeneralParameterEnum.CODIGO.getName()));
//		numeroLista = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listanumerolista
	 *
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilanumeroListaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}
	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodCiiu
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodCiiu(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubactividadEconomica.getCampos().put("COD_CIIU", registroAux.getCampos().get("COD_ACTIVIDAD"));
		registroSubactividadEconomica.getCampos().put("DESCRIPCION", registroAux.getCampos().get("DESCRIPCION"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodCiiu
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodCiiuE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("COD_ACTIVIDAD"));
		descripActividad = SysmanFunciones.toString(registroAux.getCampos().get("DESCRIPCION"));
	}
	
	/**
	 * Metodo ejecutado al cambiar el control CodCiiu en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarCodCiiuC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaActividadeconomica.getDatasource().get(rowNum % 10).getCampos().put("DESCRIPCION", descripActividad);
		//</CODIGO_DESARROLLADO>
	}


    /**
     * metodo que selecciona un registro
     *
     * @param event
     */
    public void cambiarNITC(int rowNum) {

        listaSecundario296.get(rowNum).getCampos().put(
                        TercerosControladorEnum.CNOMBRE.getValue(), nombre);
        listaSecundario296.get(rowNum).getCampos().put(
                        TercerosControladorEnum.CSUCURSAL.getValue(), sucursal);

    }

    /**
     * Metodo ejecutado al cambiar el control Tercero en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTerceroC(int rowNum) {

        listaDeudorsolidario.get(rowNum).getCampos().put(
                        GeneralParameterEnum.NOMBRE.getName(), auxiliarNombre);

        listaDeudorsolidario.get(rowNum).getCampos().put("TIPOID",
                        auxiliarTipoId);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
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

        registro.getCampos().put("EXPEDIDACEDULA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    /**
     * metodo que se llama al cambiar el nit
     */
    public void cambiarNit() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(TercerosControladorEnum.NIT.getValue(), registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()));
        try {
            List<Registro> rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TercerosControladorUrlEnum.URL38954
                                                                            .getValue())
                                            .getUrl(), param));

            if (validarSucursal.equals("NO")) {
                if ((rs != null) && !rs.isEmpty()) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB30"));
                    registro.getCampos().put(
                                    TercerosControladorEnum.NIT.getValue(),
                                    null);
                    return;
                }
            }
            if ((registro.getCampos().get(
                            TercerosControladorEnum.NIT.getValue()) != null)
                && !registro.getCampos()
                                .get(TercerosControladorEnum.NIT.getValue())
                                .toString().isEmpty()) {
                digitoVerificacion = String.valueOf(
                                ejbSysmanUtil.generarDigitoDeVerificacion(
                                                registro.getCampos()
                                                                .get(TercerosControladorEnum.NIT
                                                                                .getValue())
                                                                .toString()));

            }
        }
        catch (SystemException ex) {
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    /**
     * metodo que se llama al cambiar el estado de la cuenta
     */
    public void cambiarEstadoCuenta() {
        if ("A".equals(registroSubTerceroPagosSub.getCampos()
                        .get(TercerosControladorEnum.ESTADO_CUENTA.getValue())
                        .toString())) {
            registroSubTerceroPagosSub.getCampos()
                            .put(TercerosControladorEnum.FECHAAUTORIZACION
                                            .getValue(),
                                            new Date());
            registroSubTerceroPagosSub.getCampos().put(
                            TercerosControladorEnum.CACTIVA.getValue(), true);

        }
        else {
            registroSubTerceroPagosSub.getCampos().put(
                            TercerosControladorEnum.CACTIVA.getValue(), false);
        }
    }

    /**
     * metodo que selecciona un registro
     *
     * @param event
     */
    public void onRowSelectBuscar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(TercerosControladorEnum.NIT.getValue(),
                        registroAux.getCampos().get(TercerosControladorEnum.NIT
                                        .getValue()));
        cargarRegistro();
    }

    /**
     * metodo que selecciona un registro
     *
     * @param event
     */
    public void onRowSelectBuscarNombre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(TercerosControladorEnum.CNOMBRE.getValue(),
                        registroAux.getCampos().get(TercerosControladorEnum.NIT
                                        .getValue()));
    }

    /**
     * metodo que selecciona un registro
     *
     * @param event
     */
    public void seleccionarFilaNITAPODERADO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        TercerosControladorEnum.NITAPODERADO.getValue(),
                        registroAux.getCampos().get(TercerosControladorEnum.NIT
                                        .getValue()));
        registro.getCampos().put(
                        TercerosControladorEnum.SUCURSALAPODERADO.getValue(),
                        registroAux.getCampos()
                                        .get(TercerosControladorEnum.CSUCURSAL
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoICA
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoICA(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGOICA", registroAux.getCampos()
                        .get(TercerosControladorEnum.CIIU.getValue()));
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        iblgradovisible = false;
        iblunidadVisible = false;
        unidadVisible = false;
        gradoVisible = false;
        fuerzaVisible = false;
        etiqueta4Visible = false;

        Calendar calendario = new GregorianCalendar();
        anio = Integer.toString(calendario.get(Calendar.YEAR));

        String strManejaCamposObligatorios = valorParametro(
                        "MANEJA CAMPOS OBLIGATORIOS EN TERCEROS", "1");

        campoObligatorioTercero = "SI".equals(strManejaCamposObligatorios);

        manejaTerceroPorApellido = valParametro(
                        "MANEJA TERCERO POR APELLIDO");

        String grupo = SessionUtil.getGrupo(modulo).getCodigo();
        String strNombreGrupoTesoreria;
        String strManejaProveedores;
        String strManejaRestriccion;
        strNombreGrupoTesoreria = valorParametro(
                        "NOMBRE GRUPO TESORERIA", "0");

        String[] arrayGrupos = strNombreGrupoTesoreria.split(",");

        if (!"30105".equals(SessionUtil.getMenuActual())) {
            strManejaProveedores = valorParametro(
                            "MANEJA PROVEEDORES", "1");

            strManejaRestriccion = valorParametro(
                            "MANEJA RESTRICCION EN INSCRIPCION DE CUENTAS",
                            "1");

            if (Arrays.asList(arrayGrupos).contains(grupo)
                && "SI".equals(strManejaProveedores)
                && "SI".equals(strManejaRestriccion)) {

                visiblePagos = true;

                manejaRestriccion = true;

            }
            else {
                visiblePagos = false;
            }
        }
        else {

            visiblePagos = true;
        }

        manejaAlertaEmbargoComprobantes = valorParametro(
                        "MANEJA ALERTA DE EMBARGO EN COMPROBANTES", "1");
        manejaAlertaEmbargoComprobantes = (manejaAlertaEmbargoComprobantes == null)
            ? ""
            : manejaAlertaEmbargoComprobantes;
        mesIni = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        mesFin = String.valueOf(SysmanFunciones
                        .mes(new Date()));

        validarSucursal = valorParametro(
                        "CREAR TERCEROS CON DIFERENTES SUCURSALES", "1");
        bloqueaSucursal = validarSucursal.equals("NO");

    }

    /**
     * Funcion que retorna verdadero o falso, dependiendo del nombre
     * del parametro recibido, se evalua con equalsIgnoreCase
     *
     * @param nombreParametro
     * @return true o false
     */
    public boolean valorParametroIgnoreCase(String nombreParametro) {
        boolean respuesta = false;
        try {
            respuesta = "SI".equalsIgnoreCase(ejbSysmanUtil.consultarParametro(
                            compania, nombreParametro, modulo, new Date(),
                            true));
        }
        catch (NullPointerException ex) {
            respuesta = false;
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    /**
     * Retorna el valor del parametro recibido, si esta nulo puede
     * devolver la cadena con un "NO" o vacia
     *
     * @param nombreParametro
     * @param tipoRespuesta
     * @return nombrePar
     */
    public String valorParametro(String nombreParametro, String tipoRespuesta) {
        String nombrePar = "";
        try {

            nombrePar = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);

            nombrePar = (nombrePar == null)
                ? ""
                : nombrePar;
        }
        catch (NullPointerException ex) {
            if ("1".equals(tipoRespuesta)) {
                nombrePar = "NO";
            }
            else {
                nombrePar = "";
            }

            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return nombrePar;
    }

    /**
     * Funcion que retorna verdadero o falso, dependiendo del nombre
     * del parametro recibido, se evalua con equals
     *
     * @param nombreParametro
     * @return true o false
     */
    public boolean valParametro(String nombreParametro) {
        boolean nombrePar = false;
        try {
            nombrePar = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true));

        }
        catch (NullPointerException ex) {
            nombrePar = false;
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return nombrePar;
    }

    /**
     * metodo que se llama para cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
    	
    	 try {
			visibletipoEntidad = "SI".equals(SysmanFunciones
			         .nvlStr(ejbSysmanUtil.consultarParametro(compania,
			                 "SF MANEJA TARIFA POR TERCERO",
			                 modulo,
			                 new Date(), true), "NO")) ? true : false;
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        precargarRegistro();

        if (ACCION_INSERTAR.equals(accion)) {

            registro.getCampos().put(
                            TercerosControladorEnum.PORCDESCUENTO.getValue(),
                            0);
            registro.getCampos().put(
                            TercerosControladorEnum.CALIFICACIONCC.getValue(),
                            0);
            registro.getCampos().put(
                            TercerosControladorEnum.KCONSTRUCTOR.getValue(), 0);
            registro.getCampos().put(
                            TercerosControladorEnum.KCONSULTOR.getValue(), 0);
            registro.getCampos().put(
                            TercerosControladorEnum.KCONSULTORING.getValue(),
                            0);
            registro.getCampos().put(
                            TercerosControladorEnum.KPROVEEDOR.getValue(), 0);
            registro.getCampos().put(
                            TercerosControladorEnum.PRMD_CARTERA.getValue(), 0);
            registro.getCampos().put(TercerosControladorEnum.DIAS_PRMD_CARTERA
                            .getValue(), 0);
            registro.getCampos().put(
                            TercerosControladorEnum.MAX_CREDITO.getValue(), 0);
            registro.getCampos().put(
                            TercerosControladorEnum.VLRMAXCARTERA.getValue(),
                            0);
            registro.getCampos().put(TercerosControladorEnum.VALORULTIMAFACTURA
                            .getValue(), 0);

        }

        if (!nuevo) {
            visibleConsorciados = "1"
                            .equals(registro.getCampos()
                                            .get(TercerosControladorEnum.CTIPOCONTRATISTA
                                                            .getValue()));

            String nzNombre1 = registro.getCampos()
                            .get(TercerosControladorEnum.CNOMBREUNO
                                            .getValue()) == null ? ""
                                                : registro.getCampos()
                                                                .get(TercerosControladorEnum.CNOMBREUNO
                                                                                .getValue())
                                                                .toString();
            if (nzNombre1.isEmpty()) {
                registro.getCampos().put(
                                TercerosControladorEnum.CNOMBREUNO.getValue(),
                                registro.getCampos()
                                                .get(TercerosControladorEnum.CNOMBRE
                                                                .getValue()));
            }
            agregarRegistroNuevo(false);

        }
        if (css != null) {
            digitoVerificacion = registro.getCampos()
                            .get(TercerosControladorEnum.DIGITOVERIFICACION
                                            .getValue())
                            .toString();

            habilitarEmbargos = (boolean) registro.getCampos()
                            .get(TercerosControladorEnum.CEMBARGO.getValue());
            bloqueadoNit = true;

            visibleTerceroContratos = (boolean) registro.getCampos()
                            .get("LEY1819");

        }
        else {
            registro.getCampos().put(
                            TercerosControladorEnum.CSUCURSAL.getValue(),
                            "001");
            bloqueadoNit = false;
        }
    }

    /**
     * metodo que es llamado cuando se oprime el boton contables
     */
    public void oprimirContables() {
        int mesInicio = Integer.parseInt(mesIni);
        int mesFinal = Integer.parseInt(mesFin);
        String codigoAux = registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()).toString();
        if (SysmanFunciones.validarVariableVacio(codigoAux)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            TercerosControladorEnum.CTB32.getValue()));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mesIni)
            || SysmanFunciones.validarVariableVacio(mesFin)
            || SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            TercerosControladorEnum.CTB33.getValue()));
            return;
        }
        if (mesInicio > mesFinal) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            TercerosControladorEnum.CTB34.getValue()));
            return;
        }
        String[] campos = { "rid",
                            TercerosControladorEnum.CCODTERCERO.getValue(),
                            TercerosControladorEnum.CNOMBRETERCERO.getValue(),
                            TercerosControladorEnum.CSUCURSALTERCERO.getValue(),
                            TercerosControladorEnum.CMESINICIALQR.getValue(),
                            TercerosControladorEnum.CMESFINALQR.getValue(),
                            TercerosControladorEnum.CANOQR.getValue(),
                            TercerosControladorEnum.CFORMULARIO.getValue() };
        Object[] valores = { css, codigoAux,
                             registro.getCampos()
                                             .get(TercerosControladorEnum.CNOMBRE
                                                             .getValue())
                                             .toString(),
                             registro.getCampos()
                                             .get(TercerosControladorEnum.CSUCURSAL
                                                             .getValue())
                                             .toString(),
                             mesIni, mesFin, anio,
                             TercerosControladorEnum.CTERCERO.getValue()
                                             .toLowerCase() };
        SessionUtil.cargarModalDatosFlashCerrar("37", SessionUtil.getModulo(),
                        campos,
                        valores);

    }

    /**
     * metodo que se llama cuando ocurre un evento en el boton
     * presupuestales
     */
    public void oprimirPresupuestales() {

        int mesInicio = Integer.parseInt(mesIni);
        int mesFinal = Integer.parseInt(mesFin);
        String codigoAux = registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()).toString();
        if (SysmanFunciones.validarVariableVacio(codigoAux)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            TercerosControladorEnum.CTB32.getValue()));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mesIni)
            || SysmanFunciones.validarVariableVacio(mesFin)
            || SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            TercerosControladorEnum.CTB33.getValue()));
            return;
        }
        if (mesInicio > mesFinal) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            TercerosControladorEnum.CTB34.getValue()));
            return;
        }
        String[] campos = { "rid",
                            TercerosControladorEnum.CCODTERCERO.getValue(),
                            TercerosControladorEnum.CNOMBRETERCERO.getValue(),
                            TercerosControladorEnum.CSUCURSALTERCERO.getValue(),
                            TercerosControladorEnum.CMESINICIALQR.getValue(),
                            TercerosControladorEnum.CMESFINALQR.getValue(),
                            TercerosControladorEnum.CANOQR.getValue(),
                            TercerosControladorEnum.CFORMULARIO.getValue() };
        Object[] valores = { css, codigoAux,
                             registro.getCampos()
                                             .get(TercerosControladorEnum.CNOMBRE
                                                             .getValue())
                                             .toString(),
                             registro.getCampos()
                                             .get(TercerosControladorEnum.CSUCURSAL
                                                             .getValue())
                                             .toString(),
                             mesIni, mesFin, anio,
                             TercerosControladorEnum.CTERCERO.getValue()
                                             .toLowerCase() };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBFORMCENTROPS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se llama cuando ocurre un evento en el boton almacen
     */
    public void oprimirAlmacen() {

        int mesInicio = Integer.parseInt(mesIni);
        int mesFinal = Integer.parseInt(mesFin);
        String codigoAux = registro.getCampos()
                        .get(TercerosControladorEnum.NIT.getValue()).toString();
        if (SysmanFunciones.validarVariableVacio(codigoAux)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            TercerosControladorEnum.CTB32.getValue()));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(mesIni)
            || SysmanFunciones.validarVariableVacio(mesFin)
            || SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            TercerosControladorEnum.CTB33.getValue()));
            return;
        }
        if (mesInicio > mesFinal) {
            JsfUtil.agregarMensajeError(idioma.getString(
                            TercerosControladorEnum.CTB34.getValue()));
            return;
        }
        String[] campos = { "rid",
                            TercerosControladorEnum.CCODTERCERO.getValue(),
                            TercerosControladorEnum.CNOMBRETERCERO.getValue(),
                            TercerosControladorEnum.CSUCURSALTERCERO.getValue(),
                            TercerosControladorEnum.CMESINICIALQR.getValue(),
                            TercerosControladorEnum.CMESFINALQR.getValue(),
                            TercerosControladorEnum.CANOQR.getValue(),
                            TercerosControladorEnum.CFORMULARIO.getValue() };
        Object[] valores = { css, codigoAux,
                             registro.getCampos()
                                             .get(TercerosControladorEnum.CNOMBRE
                                                             .getValue())
                                             .toString(),
                             registro.getCampos()
                                             .get(TercerosControladorEnum.CSUCURSAL
                                                             .getValue())
                                             .toString(),
                             mesIni, mesFin, anio,
                             TercerosControladorEnum.CTERCERO.getValue()
                                             .toLowerCase() };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBFORMCENTROAS_CONTROLADOR
                                                        .getCodigo()),
                        SessionUtil.getModulo(),
                        campos,
                        valores);

    }

    /**
     * metodo que se llama al cambiar la clase
     */
    public void cambiarClase() {
        String clase = registro.getCampos()
                        .get(GeneralParameterEnum.CLASE.getName()).toString();
        tipoAsociadoBloqueado = "A".equals(clase) ? false : true;
    }

    private void addRegistro(String var, Object tipo) {
        registro.getCampos().put(var, SysmanFunciones
                        .validarCampoVacio(registro.getCampos(), var)
                            ? tipo
                            : registro.getCampos().get(var));
    }

    private void allAddregistro() {

        addRegistro("REGIMEN", "");
        addRegistro("AUTORETENEDOR", 0);
        addRegistro("KCONSTRUCTOR", 0.0);
        addRegistro("KCONSULTORING", 0);
        addRegistro("KCONSULTOR", 0.0);
        addRegistro("KPROVEEDOR", 0.0);
        addRegistro("CLASE", "");
        addRegistro("MAX_CREDITO", 0.0);
        addRegistro("PRMD_CARTERA", 0.0);
        addRegistro("DIAS_PRMD_CARTERA", 0.0);
        addRegistro("DETERIORO", 0);
        addRegistro("VLRMAXCARTERA", 0.0);
        addRegistro("VALORULTIMAFACTURA", 0.0);
        addRegistro("PORCDESCUENTO", 0.0);
        addRegistro("CLASEENTIDADOFICIAL", 0);
        addRegistro("CALIFICACIONCC", 0.0);
        addRegistro("APLICADESCUENTO", 0);
        addRegistro("EMBARGO", 0);
        addRegistro("PAGOELECTRONICO", 0);
        addRegistro("LEY1450", 0);
        addRegistro("IBC", 0.0);
        addRegistro("APORTESVOLUNTARIOS", 0.0);
        addRegistro("IND_INTERVENTOR", 0);
        addRegistro("AFC", 0.0);
        addRegistro("VALOR_EMBARGO", 0.0);
        addRegistro("DEDUCIBLE_VIVIENDA", 0.0);
        addRegistro("MEDPREPAGADA", 0.0);
        addRegistro("APORTESARL", 0.0);
        addRegistro("NOAPORTASALUD", 0);
        addRegistro("NOAPORTAPENSION", 0);
        addRegistro("ESTADO", "");
        addRegistro("COMPANIA", "");
        addRegistro("NIT", "");
        addRegistro("SUCURSAL", "");
        addRegistro("GRADO", "");
        addRegistro("TIPO_CONTRATISTA", "");
        addRegistro("ZONA", "");
        addRegistro("UNIDAD", "");
    }

    private boolean validacionCedulaNit() {
        if (("E".equals(registro.getCampos().get("TIPOID").toString()) ||
            "C".equals(registro.getCampos().get("TIPOID").toString()))
            && (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            "CIUDAD_EXPE"))) {
            JsfUtil.agregarMensajeError(
                            "Se debe diligenciar el campo Expedida en.");
            return true;
        }
        return false;
    }

    /**
     * metodo heredado del bean base para registrar
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(TercerosControladorEnum.CNOMBRE.getValue());
        String camposVacios = validarCampos();
        if (camposVacios.length() > 0) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB36") + camposVacios);
            return false;
        }
        if (validacionCedulaNit()) {
            return false;
        }
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().remove(GeneralParameterEnum.MODIFIED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_MODIFIED.getName());
        registro.getCampos()
                        .remove(TercerosControladorEnum.INSCRIPCION_PROPONENTE
                                        .getValue());
        allAddregistro();
        return true;

    }

    /**
     * metodo heredado del bean base
     */
    @Override
    public boolean insertarDespues() {
        // heredado del bean base
        return true;
    }

    /**
     * metodo que se llama al cambiar el pais
     */
    public void cambiarPais() {
        cargarListaDepartamento();
        cargarListaCiudad();
    }

    /**
     * metodo que se llama al cambiarel departamento
     */
    public void cambiarDepartamento() {
        cargarListaCiudad();
    }

    /**
     * metodo que se llama al cambiar el nombre1
     */
    public void cambiarNOMBRE1() {

        unificarNombre();
    }

    /**
     * metodo que se llama al cambiar el nombre2
     */
    public void cambiarNOMBRE2() {

        unificarNombre();
    }

    /**
     * metodo que se llama al cambiar el apellido1
     */
    public void cambiarAPELLIDO1() {

        unificarNombre();
    }

    /**
     * metodo que se llama al cambiar el apellido2
     */
    public void cambiarAPELLIDO2() {

        unificarNombre();
    }

    /**
     * metodo que valida los campos
     */
    public String validarCampos() {
        boolean obligatorios;
        String camposModificados = "";
        obligatorios = valorParametroIgnoreCase(
                        "OBLIGATORIEDAD DE CAMPOS ESPECIF. DE TERCERO");

        if (obligatorios) {
            if (registro.getCampos()
                            .get(TercerosControladorEnum.NIT.getValue())
                            .toString().isEmpty()) {
                camposModificados += "," + "NIt Cedula";
            }
            if (registro.getCampos().get(TercerosControladorEnum.CTIPOID
                            .getValue()) == null) {
                camposModificados += "," + "Tipo Identificacion";
            }
            if (registro.getCampos()
                            .get(TercerosControladorEnum.CNOMBREUNO
                                            .getValue())
                            .toString().isEmpty()) {
                camposModificados += "," + "Nombre o razon social";
            }

            String nvlTipoId = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(TercerosControladorEnum.CTIPOID
                                                            .getValue()),
                                            "")
                            .toString();

            if ((registro.getCampos()
                            .get(TercerosControladorEnum.CAPELLIDOUNO
                                            .getValue()) == null)
                && !"N".equals(nvlTipoId)) {
                camposModificados += "," + "Apellido 1";
            }
            camposModificados += camposModif();

        }

        return camposModificados;
    }

    /**
     * Llamado en el metodo validarCampos
     *
     * @return
     */
    private String camposModif() {
        String camposModificados = "";
        if (registro.getCampos().get(
                        GeneralParameterEnum.NATURALEZA.getName()) == null) {
            camposModificados += "," + "Tipo de Persona";
        }
        if (registro.getCampos()
                        .get(GeneralParameterEnum.CLASE.getName()) == null) {
            camposModificados += "," + "Clase";
        }
        if (registro.getCampos().get(
                        TercerosControladorEnum.REGIMEN.getValue()) == null) {
            camposModificados += "," + "Regimen";
        }
        if (registro.getCampos().get(GeneralParameterEnum.DIRECCION.getName())
                        .toString()
                        .isEmpty()) {
            camposModificados += "," + "Direccion";
        }
        if (registro.getCampos()
                        .get(TercerosControladorEnum.TELEFONOS.getValue())
                        .toString()
                        .isEmpty()) {
            camposModificados += "," + "Telefonos";
        }
        if (registro.getCampos()
                        .get(TercerosControladorEnum.PAIS.getValue()) == null) {
            camposModificados += "," + "Pais";
        }
        if (registro.getCampos().get(
                        GeneralParameterEnum.DEPARTAMENTO.getName()) == null) {
            camposModificados += "," + "Departamento";
        }
        if (registro.getCampos()
                        .get(GeneralParameterEnum.CIUDAD.getName()) == null) {
            camposModificados += "," + "Ciudad";
        }
        return camposModificados;
    }

    /**
     * metodo que unifica el nombre
     */
    public void unificarNombre() {
        String nombreAux = "";
        if (manejaTerceroPorApellido) {
            String nvlAux = SysmanFunciones.nvl(registro.getCampos()
                            .get(TercerosControladorEnum.CNOMBREUNO.getValue()),
                            "").toString();

            if (!nvlAux.isEmpty()) {
                nombreAux = nvlAux;
            }
            nvlAux = SysmanFunciones.nvl(
                            registro.getCampos()
                                            .get(TercerosControladorEnum.CNOMBREDOS
                                                            .getValue()),
                            "").toString();

            if (!nvlAux.isEmpty()) {
                nombreAux += " " + nvlAux;
            }
            nvlAux = SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(TercerosControladorEnum.CAPELLIDOUNO
                                                            .getValue()),
                                            "")
                            .toString();

            if (!nvlAux.isEmpty()) {
                nombreAux += " " + nvlAux;
            }
            nvlAux = SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            TercerosControladorEnum.CAPELLIDODOS.getValue())
                                ? ""
                                : registro.getCampos()
                                                .get(TercerosControladorEnum.CAPELLIDODOS
                                                                .getValue())
                                                .toString();
            if (!nvlAux.isEmpty()) {
                nombreAux += " " + nvlAux;
            }
        }
        else {
            nombreAux = nombreAux();

        }
        if (!nombreAux.isEmpty()) {
            registro.getCampos().put(TercerosControladorEnum.CNOMBRE.getValue(),
                            nombreAux);
        }
    }

    /**
     * Retorna el nombre del tercero primero los apellidos y luego los
     * nombres
     *
     * @return nombreAux
     */
    private String nombreAux() {
        String nombreAux = "";
        String nvlAux = SysmanFunciones.nvl(registro.getCampos()
                        .get(TercerosControladorEnum.CAPELLIDOUNO.getValue()),
                        "").toString();

        if (!nvlAux.isEmpty()) {
            nombreAux = nvlAux;
        }

        nvlAux = SysmanFunciones.nvl(registro.getCampos()
                        .get(TercerosControladorEnum.CAPELLIDODOS.getValue()),
                        "").toString();

        if (!nvlAux.isEmpty()) {
            nombreAux += " " + nvlAux;
        }
        nvlAux = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(TercerosControladorEnum.CNOMBREUNO
                                                        .getValue()),
                                        "")
                        .toString();

        if (!nvlAux.isEmpty()) {
            nombreAux += nvlAux;
        }
        nvlAux = SysmanFunciones
                        .nvl(registro.getCampos()
                                        .get(TercerosControladorEnum.CNOMBREDOS
                                                        .getValue()),
                                        "")
                        .toString();

        if (!nvlAux.isEmpty()) {
            nombreAux += " " + nvlAux;
        }
        return nombreAux;

    }

    /**
     * metodo heredado del bean base
     */
    @Override
    public boolean actualizarAntes() {
        if (validacionCedulaNit()) {
            return false;
        }
               
        registro.getCampos().put(TercerosControladorEnum.NIT.getValue(),
        	    SysmanFunciones.toString(registro.getCampos().get(TercerosControladorEnum.NIT.getValue()))
        	        .replaceAll("\\s+", ""));
        
        registro.getCampos().put("CODIGOPOSTAL",SysmanFunciones.toString(registro.getCampos().get("CODIGOPOSTAL"))
        	        .replaceAll("[^A-Za-z0-9]", "")); //1854_CONTABILIDAD(18/06/2025 MROSERO) Elimina todo lo que NO sea letra o número     	
                 	
        registro.getCampos().remove(TercerosControladorEnum.CNOMBRE.getValue());
        
        if (campoObligatorioTercero) {

            String camposVacios = validarCampos();

            if (camposVacios.length() > 0) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB36") + camposVacios);
                return false;
            }
        }

        if ("SI".equals(manejaAlertaEmbargoComprobantes)) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(TercerosControladorEnum.NIT.getValue(), registro
                            .getCampos()
                            .get(TercerosControladorEnum.NIT.getValue()));
            param.put(GeneralParameterEnum.SUCURSAL.getName(), registro
                            .getCampos()
                            .get(GeneralParameterEnum.SUCURSAL.getName()));

            List<Registro> lista;
            try {
                lista = RegistroConverter
                                .toListRegistro(requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                TercerosControladorUrlEnum.URL19656
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (!lista.isEmpty()) {
                    habilitarEmbargos = true;
                    registro.getCampos().put(
                                    TercerosControladorEnum.CEMBARGO.getValue(),
                                    true);
                    registro.getCampos()
                                    .put(TercerosControladorEnum.FECHA_EMBARGO
                                                    .getValue(),
                                                    lista.get(0).getCampos()
                                                                    .get(GeneralParameterEnum.FECHA
                                                                                    .getName()));
                    registro.getCampos()
                                    .put(TercerosControladorEnum.VALOR_EMBARGO
                                                    .getValue(),
                                                    lista.get(0).getCampos()
                                                                    .get(TercerosControladorEnum.VALORDEMANDA
                                                                                    .getValue()));
                    registro.getCampos()
                                    .put(TercerosControladorEnum.NRODOCUMENTO_EMBARGO
                                                    .getValue(),
                                                    lista.get(0).getCampos()
                                                                    .get(TercerosControladorEnum.PROCESO
                                                                                    .getValue()));
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB73")
                        + idioma.getString("TB_TB76")
                        + lista.get(0).getCampos()
                                        .get(TercerosControladorEnum.VALORDEMANDA
                                                        .getValue())
                        + idioma.getString("TB_TB78")
                        + lista.get(0).getCampos().get(
                                        GeneralParameterEnum.FECHA.getName())
                        + idioma.getString("TB_TB79")
                        + lista.get(0).getCampos()
                                        .get(TercerosControladorEnum.PROCESO
                                                        .getValue()));
                }
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        registro.getCampos().put(TercerosControladorEnum.CNOMBREUNO.getValue(),
                        registro.getCampos()
                                        .get(TercerosControladorEnum.CNOMBREUNO
                                                        .getValue())
                                        .toString().trim());
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.CREATED_BY.getName());
            registro.getCampos().remove(
                            GeneralParameterEnum.DATE_CREATED.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(TercerosControladorEnum.NIT.getValue());
            registro.getCampos()
                            .remove(GeneralParameterEnum.SUCURSAL.getName());
            registro.getCampos()
                            .remove(TercerosControladorEnum.INSCRIPCION_PROPONENTE
                                            .getValue());
        }

        return true;
    }

    /**
     * metodo heredado del bean base
     */
    @Override
    public boolean actualizarDespues() {
        unificarNombre();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo heredado del bean base
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>

        if (isTerceroVarios(registro)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB84"));
            return false;
        }
        else if (hayMovimientosContables(registro)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB85"));
            return false;
        }
        else if (hayMovimientosPresupuestales(registro)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB86"));
            return false;
        }
        return true;
    }

    /**
     * metodo heredado del bean base
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * metodo que se llama al cambiar el grado
     */
    public void cambiarGRADO() {

        try {
            String fuerza = service.buscarEnLista(
                            registro.getCampos()
                                            .get(TercerosControladorEnum.CGRADO
                                                            .getValue())
                                            .toString(),
                            TercerosControladorEnum.CGRADO.getValue(),
                            TercerosControladorEnum.CFUERZA.getValue(),
                            listaGRADO);
            registro.getCampos().put(TercerosControladorEnum.CFUERZA.getValue(),
                            fuerza);
        }
        catch (NullPointerException ex) {
            registro.getCampos().put(TercerosControladorEnum.CFUERZA.getValue(),
                            null);
            Logger.getLogger(TercerosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    /**
     * metodo que se llama al cambiar el contratista
     */
    public void cambiarTipoContratista() {
        visibleConsorciados = !((registro.getCampos()
                        .get(TercerosControladorEnum.CTIPOCONTRATISTA
                                        .getValue()) == null)
            || "1".equals(registro.getCampos()
                            .get(TercerosControladorEnum.CTIPOCONTRATISTA
                                            .getValue())));
    }

    /**
     * metodo que se llama al cambiar el tipo de documento
     */
    public void cambiarTipoDocumentoCNT() {

        String strManejaCamposObligatorios;
        campoObligatorioTerceroyN = false;

        strManejaCamposObligatorios = valorParametro(
                        "MANEJA CAMPOS OBLIGATORIOS EN TERCEROS", "1");

        if ("SI".equals(strManejaCamposObligatorios)
            && (registro.getCampos().get(TercerosControladorEnum.CTIPOID
                            .getValue()) != null)) {

            if ("N".equals(registro.getCampos()
                            .get(TercerosControladorEnum.CTIPOID.getValue()))) {
                campoObligatorioTerceroyN = true;

            }
        }
        else {

            campoObligatorioTerceroyN = false;

        }

        campoObligatorioTercero = "SI".equals(strManejaCamposObligatorios);

    }

    /**
     * metodo que se llama al cambiar el embargo
     */
    public void cambiarckEmbargo() {
        // <CODIGO_DESARROLLADO>
        habilitarEmbargos = (boolean) registro.getCampos()
                        .get(TercerosControladorEnum.CEMBARGO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarckAplicaLey1819() {
        // <CODIGO_DESARROLLADO>

        visibleTerceroContratos = (boolean) registro.getCampos().get("LEY1819");

        // </CODIGO_DESARROLLADO>
    }
    
    public void oprimirActualizarEnFactElectronica() {
    	archivoDescarga = null; 
    	String log = "  ----------   Terecero frida -----------------  ";
        String url;
		try {
			url = ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_REST, "69", new Date(), false);
		
    	   	String respuesta;
	        APIFrida apiFrida = new APIFrida();
	
	        respuesta = apiFrida.cargarTercero(nitCompania,
	        		registro.getCampos().get("NIT")
	                                        .toString().trim(),
	                        url);
	
	        Gson gson = new Gson();
	        RespuestaApi respuestaApi = gson.fromJson(respuesta,
	                        RespuestaApi.class);
	        if (respuestaApi.getCodigo() != 0) {
	        	log = log + "\n Creacion \n" + crearTecero(url, registro) + "\n -- Recuerde guardar los cambios en SYSMAN";
			}else {
				log = log + "\n Actaulizacion \n" + actualizaTecero(url, registro) + "\n -- Recuerde guardar los cambios en SYSMAN";
			}
	        
	        ByteArrayInputStream streamTexto = JsfUtil
                    .serializarPlano(log);
		    archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
		                    "LogTercerosFrida.txt");
		
		    cargarListaDatostercero();
		    
		    JsfUtil.agregarMensajeInformativo(
                    		idioma.getString("MSM_PROCESO_EJECUTADO"));
		    
		    
		} catch (SystemException | IOException | com.sysman.util.SysmanException | SysmanException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }
    
    private String crearTecero(String url, Registro rs)
            throws SysmanException
	{
		String respuesta = null;
		Map<String, Object> params = new TreeMap<>();
		
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), registro.getCampos().get("NIT")
	            .toString());
		
		try
		{
		    Registro reg = RegistroConverter
		                    .toRegistro(requestManager.get(
		                                    UrlServiceUtil.getInstance()
		                                                    .getUrlServiceByUrlByEnumID(
		                                                    		TercerosControladorUrlEnum.URL7354
		                                                                                    .getValue())
		                                                    .getUrl(),
		                                    params));
		
		    ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();
		
		    ParametrosTercero param = new ParametrosTercero();
		  //7742397_FACGENERAL
			if (reg.getCampos().get(GeneralParameterEnum.PAIS.getName())!= null && reg.getCampos().get(GeneralParameterEnum.PAIS.getName()).toString().equals("CO")) {
				
				param.setCodigomunicipio(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());
		
				param.setCiudad(
						SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());
		
				param.setCodigodepartamento(
						SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());
		
				param.setDepartamento(SysmanFunciones
						.nvl(reg.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());
				
				param.setPais(SysmanFunciones
						.nvl(reg.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());
			} else {
				param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());
		
				param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());
		
				param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());
		
				param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());
				
				param.setPais(SysmanFunciones
						.nvl(reg.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());
		
			}	
		
		    param.setCorreoelectronico(SysmanFunciones.nvl(
		                    rs.getCampos().get("DIRECCIONEMAIL"), reg.getCampos().get("CORREOELECTRONICO").toString())
		                    .toString());
		
		    param.setDireccion(SysmanFunciones.nvl(
		                    rs.getCampos().get(GeneralParameterEnum.DIRECCION
		                                    .getName()),
		                    reg.getCampos().get(GeneralParameterEnum.DIRECCION
	                                .getName()).toString())
		                    .toString());
		
		    param.setDireccionfiscal(SysmanFunciones.nvl(
		                    rs.getCampos().get("DIRECCIONFISCAL"), reg.getCampos().get("DIRECCIONFISCAL").toString())
		                    .toString());
		
		    param.setCodigopostal(SysmanFunciones.nvl(
		                    rs.getCampos().get("CODIGOPOSTAL"),reg.getCampos().get("CODIGOPOSTAL").toString())
		                    .toString());
		
		    param.setNumerodocumento(SysmanFunciones.nvl(
		                    rs.getCampos().get("NIT"), "")
		                    .toString().trim());
		
		    param.setTelefono(SysmanFunciones.nvl(
		                    rs.getCampos().get("TELEFONOS"), reg.getCampos().get("TELEFONO").toString())
		                    .toString());
		    
		    param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(
		    		rs.getCampos().get("NOMBRETERCERO"), SysmanFunciones.toString(
		    				reg.getCampos().get("NOMBRETERCERO").toString()))).replace("&", " ").replaceAll("\\s+", " ").trim());

                    		    
		    param.setTipoidentificacion(SysmanFunciones.nvl(
		                    rs.getCampos().get("TIPOIDENTIFICACION"), reg.getCampos().get("TIPOIDENTIFICACION").toString())
		                    .toString());
		
		    param.setDigitoverificacion(SysmanFunciones.nvl(
		                    rs.getCampos().get("DIGITOVERIFICACION"), reg.getCampos().get("DIGITOVERIFICACION").toString())
		                    .toString());
		
			param.setTipoorganizacion(SysmanFunciones.nvl(
		                    rs.getCampos().get("TIPOORGANIZACION"), reg.getCampos().get("TIPOORGANIZACION").toString())
		                    .toString());
		
		    param.setTiporegimen(SysmanFunciones.nvl(
		                    rs.getCampos().get("TIPOREGIMEN"), reg.getCampos().get("TIPOREGIMEN").toString())
		                   .toString());
		    
		    if (!listaResponsabilidadesfiscales.isEmpty())
		    {
		        String responsabilidadesFiscales = "";
		
		        for (Registro reg2 : listaResponsabilidadesfiscales)
		        {
		            responsabilidadesFiscales = responsabilidadesFiscales + ","
		                + reg2
		                                .getCampos()
		                                .get(GeneralParameterEnum.CODIGO
		                                                .getName())
		                                .toString();
		        }
		
		        param.setResponsabilidadesfiscales(
		                        responsabilidadesFiscales.substring(1,
		                                        responsabilidadesFiscales
		                                                        .length()));
		    }
		
		    paramTercero.setContribuyente(nitCompania);
		
		    List<ParametrosTercero> listaParam = new ArrayList<>();
		
		    listaParam.add(param);
		
		    paramTercero.setTerceros(listaParam);
		
		    Gson gson = new Gson();
		    String json = gson.toJson(paramTercero, ParametrosTerceroLote.class);
		    APIFrida apiFrida = new APIFrida();
		
		    respuesta = apiFrida.postTercero(url, json);
		    		    
		}catch (SystemException | IOException | com.sysman.util.SysmanException
			                | RuntimeException e){
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return idioma.getString("MSM_ERROR_CREA_TERCERO_FRIDA");
		}
	return respuesta;
	
}
    
    /**
   	 * Se crea metodo que permite realizar la actualizacion del tercero
   	 * @param tercero
   	 * @param url
   	 * @return
   	 * @throws SysmanException
   	 */
   	private String actualizaTecero(String url, Registro rs) throws SysmanException {
   		String respuesta = null;
   		Map<String, Object> params = new TreeMap<>();
		
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), registro.getCampos().get("NIT")
	            .toString());
		
		try
		{
		    Registro reg = RegistroConverter
		                    .toRegistro(requestManager.get(
		                                    UrlServiceUtil.getInstance()
		                                                    .getUrlServiceByUrlByEnumID(
		                                                    		TercerosControladorUrlEnum.URL7354
		                                                                                    .getValue())
		                                                    .getUrl(),
		                                    params));
		
		    ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();
		
		    ParametrosTercero param = new ParametrosTercero();
		  //7742397_FACGENERAL
			if (reg.getCampos().get(GeneralParameterEnum.PAIS.getName())!= null && reg.getCampos().get(GeneralParameterEnum.PAIS.getName()).toString().equals("CO")) {
				
				param.setCodigomunicipio(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());
		
				param.setCiudad(
						SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());
		
				param.setCodigodepartamento(
						SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());
		
				param.setDepartamento(SysmanFunciones
						.nvl(reg.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());
				
				param.setPais(SysmanFunciones
						.nvl(reg.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());
			} else {
				param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());
		
				param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());
		
				param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());
		
				param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());
				
				param.setPais(SysmanFunciones
						.nvl(reg.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());
		
			}	
		
		    param.setCorreoelectronico(SysmanFunciones.nvl(
		                    rs.getCampos().get("DIRECCIONEMAIL"), reg.getCampos().get("CORREOELECTRONICO").toString())
		                    .toString());
		
		    param.setDireccion(SysmanFunciones.nvl(
		                    rs.getCampos().get(GeneralParameterEnum.DIRECCION
		                                    .getName()),
		                    reg.getCampos().get(GeneralParameterEnum.DIRECCION
	                                .getName()).toString())
		                    .toString());
		
		    param.setDireccionfiscal(SysmanFunciones.nvl(
		                    rs.getCampos().get("DIRECCIONFISCAL"), reg.getCampos().get("DIRECCIONFISCAL").toString())
		                    .toString());
		
		    param.setCodigopostal(SysmanFunciones.nvl(
		                    rs.getCampos().get("CODIGOPOSTAL"),reg.getCampos().get("CODIGOPOSTAL").toString())
		                    .toString());
		
		    param.setNumerodocumento(SysmanFunciones.nvl(
		                    rs.getCampos().get("NIT"), "")
		                    .toString().trim());
		
		    param.setTelefono(SysmanFunciones.nvl(
		                    rs.getCampos().get("TELEFONOS"), reg.getCampos().get("TELEFONO").toString())
		                    .toString());
		    
		    param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(
		    		rs.getCampos().get("NOMBRETERCERO"), SysmanFunciones.toString(
		    				reg.getCampos().get("NOMBRETERCERO").toString()))).replace("&", " ").replaceAll("\\s+", " ").trim());
					
		    param.setTipoidentificacion(SysmanFunciones.nvl(
		                    rs.getCampos().get("TIPOIDENTIFICACION"), reg.getCampos().get("TIPOIDENTIFICACION").toString())
		                    .toString());
		
		    param.setDigitoverificacion(SysmanFunciones.nvl(
		                    rs.getCampos().get("DIGITOVERIFICACION"), reg.getCampos().get("DIGITOVERIFICACION").toString())
		                    .toString());
		
			param.setTipoorganizacion(SysmanFunciones.nvl(
		                    rs.getCampos().get("TIPOORGANIZACION"), reg.getCampos().get("TIPOORGANIZACION").toString())
		                    .toString());
		
		    param.setTiporegimen(SysmanFunciones.nvl(
		                    rs.getCampos().get("TIPOREGIMEN"), reg.getCampos().get("TIPOREGIMEN").toString())
		                    .toString());
		    
   			param.setContribuyente(nitCompania);

   			if (!listaResponsabilidadesfiscales.isEmpty())
		    {
		        String responsabilidadesFiscales = "";
		
		        for (Registro reg2 : listaResponsabilidadesfiscales)
		        {
		            responsabilidadesFiscales = responsabilidadesFiscales + ","
		                + reg2
		                                .getCampos()
		                                .get(GeneralParameterEnum.CODIGO
		                                                .getName())
		                                .toString();
		        }
		
		        param.setResponsabilidadesfiscales(
		                        responsabilidadesFiscales.substring(1,
		                                        responsabilidadesFiscales
		                                                        .length()));
		    }

   			paramTercero.setContribuyente(nitCompania);

   			List<ParametrosTercero> listaParam = new ArrayList<>();

   			listaParam.add(param);

   			paramTercero.setTerceros(listaParam);

   			Gson gson = new Gson();
   			String json = gson.toJson(param, ParametrosTercero.class);
   			APIFrida apiFrida = new APIFrida();

   			respuesta = apiFrida.putTercero(url, json);

   		} catch (IOException | com.sysman.util.SysmanException | RuntimeException | SystemException e) {

   			logger.error(e.getMessage(), e);
   			JsfUtil.agregarMensajeError(e.getMessage());
   			return idioma.getString("MSM_ERROR_ACTUALIZA_TERCERO_FRIDA");
   		}
   		return respuesta;

   	}

    /**
     * get y set
     *
     * @return
     */
    public boolean getVisibleConsorciados() {
        return visibleConsorciados;
    }

    public void setVisibleConsorciados(boolean visibleConsorciados) {
        this.visibleConsorciados = visibleConsorciados;
    }

    public boolean isVisiblePagos() {
        return visiblePagos;
    }

    public void setVisiblePagos(boolean visiblePagos) {
        this.visiblePagos = visiblePagos;
    }

    public boolean isActivaTerceroPagos() {
        return activaTerceroPagos;
    }

    public void setActivaTerceroPagos(boolean activaTerceroPagos) {
        this.activaTerceroPagos = activaTerceroPagos;
    }

    public boolean isIblgradovisible() {
        return iblgradovisible;
    }

    public void setIblgradovisible(boolean iblgradovisible) {
        this.iblgradovisible = iblgradovisible;
    }

    public boolean isIblunidadVisible() {
        return iblunidadVisible;
    }

    public void setIblunidadVisible(boolean iblunidadVisible) {
        this.iblunidadVisible = iblunidadVisible;
    }

    public boolean isUnidadVisible() {
        return unidadVisible;
    }

    public void setUnidadVisible(boolean unidadVisible) {
        this.unidadVisible = unidadVisible;
    }

    public boolean isGradoVisible() {
        return gradoVisible;
    }

    public void setGradoVisible(boolean gradoVisible) {
        this.gradoVisible = gradoVisible;
    }

    public boolean isFuerzaVisible() {
        return fuerzaVisible;
    }

    public void setFuerzaVisible(boolean fuerzaVisible) {
        this.fuerzaVisible = fuerzaVisible;
    }

    public boolean isEtiqueta4Visible() {
        return etiqueta4Visible;
    }

    public void setEtiqueta4Visible(boolean etiqueta4Visible) {
        this.etiqueta4Visible = etiqueta4Visible;
    }

    public List<Registro> getListaTercerocontratossub() {
        return listaTercerocontratossub;
    }

    /**
     * Asigna la lista listaTercerocontratossub
     * 
     * @param listaTercerocontratossub
     * Variable a asignar en listaTercerocontratossub
     */
    public void setListaTercerocontratossub(
        List<Registro> listaTercerocontratossub) {
        this.listaTercerocontratossub = listaTercerocontratossub;
    }

    /**
     * Retorna la lista listaResponsabilidadesfiscales
     * 
     * @return listaResponsabilidadesfiscales
     */
    public List<Registro> getListaResponsabilidadesfiscales() {
        return listaResponsabilidadesfiscales;
    }

    /**
     * Asigna la lista listaResponsabilidadesfiscales
     * 
     * @param listaResponsabilidadesfiscales
     * Variable a asignar en listaResponsabilidadesfiscales
     */
    public void setListaResponsabilidadesfiscales(
        List<Registro> listaResponsabilidadesfiscales) {
        this.listaResponsabilidadesfiscales = listaResponsabilidadesfiscales;
    }

    /**
     * Retorna la lista listaDeudorsolidario
     * 
     * @return listaDeudorsolidario
     */
    public List<Registro> getListaDeudorsolidario() {
        return listaDeudorsolidario;
    }

    /**
     * Asigna la lista listaDeudorsolidario
     * 
     * @param listaDeudorsolidario
     * Variable a asignar en listaDeudorsolidario
     */
    public void setListaDeudorsolidario(List<Registro> listaDeudorsolidario) {
        this.listaDeudorsolidario = listaDeudorsolidario;
    }

    public Registro getRegistroSubTerceroPagosSub() {
        return registroSubTerceroPagosSub;
    }

    public void setRegistroSubTerceroPagosSub(
        Registro registroSubTerceroPagosSub) {
        this.registroSubTerceroPagosSub = registroSubTerceroPagosSub;
    }

    public String getMesIni() {
        return mesIni;
    }

    public void setMesIni(String mesIni) {
        this.mesIni = mesIni;
    }

    public String getMesFin() {
        return mesFin;
    }

    public void setMesFin(String mesFin) {
        this.mesFin = mesFin;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public boolean isBloqueadoNit() {
        return bloqueadoNit;
    }

    public void setBloqueadoNit(boolean bloqueadoNit) {
        this.bloqueadoNit = bloqueadoNit;
    }

    public String getDigitoVerificacion() {
        return digitoVerificacion;
    }

    public void setDigitoVerificacion(String digitoVerificacion) {
        this.digitoVerificacion = digitoVerificacion;
    }

    public boolean isHabilitarEmbargos() {
        return habilitarEmbargos;
    }

    public void setHabilitarEmbargos(boolean habilitarEmbargos) {
        this.habilitarEmbargos = habilitarEmbargos;
    }

    public boolean isCampoObligatorioTerceroyN() {
        return campoObligatorioTerceroyN;
    }

    public void setCampoObligatorioTerceroyN(
        boolean campoObligatorioTerceroyN) {
        this.campoObligatorioTerceroyN = campoObligatorioTerceroyN;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * Retorna la lista listaCodigoRF
     * 
     * @return listaCodigoRF
     */
    public RegistroDataModelImpl getListaCodigoRF() {
        return listaCodigoRF;
    }

    /**
     * Asigna la lista listaCodigoRF
     * 
     * @param listaCodigoRF
     * Variable a asignar en listaCodigoRF
     */
    public void setListaCodigoRF(RegistroDataModelImpl listaCodigoRF) {
        this.listaCodigoRF = listaCodigoRF;
    }

    /**
     * Retorna la lista listaCodigoRF
     * 
     * @return listaCodigoRF
     */
    public RegistroDataModelImpl getListaCodigoRFE() {
        return listaCodigoRFE;
    }

    /**
     * Asigna la lista listaCodigoRF
     * 
     * @param listaCodigoRF
     * Variable a asignar en listaCodigoRF
     */
    public void setListaCodigoRFE(RegistroDataModelImpl listaCodigoRFE) {
        this.listaCodigoRFE = listaCodigoRFE;
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
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
    }

    public String getManejaAlertaEmbargoComprobantes() {
        return manejaAlertaEmbargoComprobantes;
    }

    public void setManejaAlertaEmbargoComprobantes(
        String manejaAlertaEmbargoComprobantes) {
        this.manejaAlertaEmbargoComprobantes = manejaAlertaEmbargoComprobantes;
    }

    public String getMsgConfirmarActualizar() {
        return msgConfirmarActualizar;
    }

    public void setMsgConfirmarActualizar(String msgConfirmarActualizar) {
        this.msgConfirmarActualizar = msgConfirmarActualizar;
    }

    public boolean isDgActualizarVisible() {
        return dgActualizarVisible;
    }

    public void setDgActualizarVisible(boolean dgActualizarVisible) {
        this.dgActualizarVisible = dgActualizarVisible;
    }

    public boolean isManejaRestriccion() {
        return manejaRestriccion;
    }

    public void setManejaRestriccion(boolean manejaRestriccion) {
        this.manejaRestriccion = manejaRestriccion;
    }

    public boolean isCampoObligatorioTercero() {
        return campoObligatorioTercero;
    }

    public void setCampoObligatorioTercero(boolean campoObligatorioTercero) {
        this.campoObligatorioTercero = campoObligatorioTercero;
    }

    public boolean isManejaTerceroPorApellido() {
        return manejaTerceroPorApellido;
    }

    public void setManejaTerceroPorApellido(boolean manejaTerceroPorApellido) {
        this.manejaTerceroPorApellido = manejaTerceroPorApellido;
    }

    public boolean isTipoAsociadoBloqueado() {
        return tipoAsociadoBloqueado;
    }

    public void setTipoAsociadoBloqueado(boolean tipoAsociadoBloqueado) {
        this.tipoAsociadoBloqueado = tipoAsociadoBloqueado;
    }

    public List<Registro> getListaTipoEmbargo() {
        return listaTipoEmbargo;
    }

    public void setListaTipoEmbargo(List<Registro> listaTipoEmbargo) {
        this.listaTipoEmbargo = listaTipoEmbargo;
    }

    /**
     * Retorna la lista listaAnoContrato
     * 
     * @return listaAnoContrato
     */
    public List<Registro> getListaAnoContrato() {
        return listaAnoContrato;
    }

    /**
     * Asigna la lista listaAnoContrato
     * 
     * @param listaAnoContrato
     * Variable a asignar en listaAnoContrato
     */
    public void setListaAnoContrato(List<Registro> listaAnoContrato) {
        this.listaAnoContrato = listaAnoContrato;
    }

    public List<Registro> getListaSucursal() {
        return listaSucursal;
    }

    public void setListaSucursal(List<Registro> listaSucursal) {
        this.listaSucursal = listaSucursal;
    }

    public List<Registro> getListaCamaraComercio() {
        return listaCamaraComercio;
    }

    public void setListaCamaraComercio(List<Registro> listaCamaraComercio) {
        this.listaCamaraComercio = listaCamaraComercio;
    }

    public List<Registro> getListaPais() {
        return listaPais;
    }

    public void setListaPais(List<Registro> listaPais) {
        this.listaPais = listaPais;
    }

    public List<Registro> getListaDepartamento() {
        return listaDepartamento;
    }

    public void setListaDepartamento(List<Registro> listaDepartamento) {
        this.listaDepartamento = listaDepartamento;
    }

    public List<Registro> getListaCiudad() {
        return listaCiudad;
    }

    public void setListaCiudad(List<Registro> listaCiudad) {
        this.listaCiudad = listaCiudad;
    }

    public List<Registro> getListaUNIDAD() {
        return listaUNIDAD;
    }

    public void setListaUNIDAD(List<Registro> listaUNIDAD) {
        this.listaUNIDAD = listaUNIDAD;
    }

    public List<Registro> getListaGRADO() {
        return listaGRADO;
    }

    public void setListaGRADO(List<Registro> listaGRADO) {
        this.listaGRADO = listaGRADO;
    }

    public List<Registro> getListaFUERZA() {
        return listaFUERZA;
    }

    public void setListaFUERZA(List<Registro> listaFUERZA) {
        this.listaFUERZA = listaFUERZA;
    }

    public List<Registro> getListaZona() {
        return listaZona;
    }

    public void setListaZona(List<Registro> listaZona) {
        this.listaZona = listaZona;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(List<Registro> listaBanco) {
        this.listaBanco = listaBanco;
    }

    public List<Registro> getListaTipoDocumentoCNT() {
        return listaTipoDocumentoCNT;
    }

    public void setListaTipoDocumentoCNT(List<Registro> listaTipoDocumentoCNT) {
        this.listaTipoDocumentoCNT = listaTipoDocumentoCNT;
    }

    public List<Registro> getListaClase() {
        return listaClase;
    }

    public void setListaClase(List<Registro> listaClase) {
        this.listaClase = listaClase;
    }

    public List<Registro> getListaRegimen() {
        return listaRegimen;
    }

    public void setListaRegimen(List<Registro> listaRegimen) {
        this.listaRegimen = listaRegimen;
    }

    public List<Registro> getListaTipoContratista() {
        return listaTipoContratista;
    }

    public void setListaTipoContratista(List<Registro> listaTipoContratista) {
        this.listaTipoContratista = listaTipoContratista;
    }

    public RegistroDataModelImpl getListaNIT() {
        return listaNIT;
    }

    public void setListaNIT(RegistroDataModelImpl listaNIT) {
        this.listaNIT = listaNIT;
    }

    public RegistroDataModelImpl getListaNITE() {
        return listaNITE;
    }

    public void setListaNITE(RegistroDataModelImpl listaNITE) {
        this.listaNITE = listaNITE;
    }

    public RegistroDataModelImpl getListaBuscar() {
        return listaBuscar;
    }

    public void setListaBuscar(RegistroDataModelImpl listaBuscar) {
        this.listaBuscar = listaBuscar;
    }

    public RegistroDataModelImpl getListaBuscarNombre() {
        return listaBuscarNombre;
    }

    public void setListaBuscarNombre(RegistroDataModelImpl listaBuscarNombre) {
        this.listaBuscarNombre = listaBuscarNombre;
    }

    public RegistroDataModelImpl getListaNITAPODERADO() {
        return listaNITAPODERADO;
    }

    public void setListaNITAPODERADO(RegistroDataModelImpl listaNITAPODERADO) {
        this.listaNITAPODERADO = listaNITAPODERADO;
    }

    /**
     * Retorna la lista listaCodigoICA
     * 
     * @return listaCodigoICA
     */
    public RegistroDataModelImpl getListaCodigoICA() {
        return listaCodigoICA;
    }

    /**
     * Asigna la lista listaCodigoICA
     * 
     * @param listaCodigoICA
     * Variable a asignar en listaCodigoICA
     */
    public void setListaCodigoICA(RegistroDataModelImpl listaCodigoICA) {
        this.listaCodigoICA = listaCodigoICA;
    }

    public Registro getRegistroSub() {
        return registroSubTerceroPagosSub;
    }

    public void setRegistroSub(Registro registroSubTerceroPagosSub) {
        this.registroSubTerceroPagosSub = registroSubTerceroPagosSub;
    }

    public List<Registro> getListaSecundario296() {
        return listaSecundario296;
    }

    public void setListaSecundario296(List<Registro> listaSecundario296) {
        this.listaSecundario296 = listaSecundario296;
    }

    public List<Registro> getListaTerceropagossub() {
        return listaTerceropagossub;
    }

    public void setListaTerceropagossub(List<Registro> listaTerceropagossub) {
        this.listaTerceropagossub = listaTerceropagossub;
    }

    public Registro getRegistroSubSecundario296() {
        return registroSubSecundario296;
    }

    public void setRegistroSubSecundario296(Registro registroSubSecundario296) {
        this.registroSubSecundario296 = registroSubSecundario296;
    }

    /**
     * Retorna el objeto registroSubTerceroContratosSub
     * 
     * @return registroSubTerceroContratosSub
     */
    public Registro getRegistroSubTerceroContratosSub() {
        return registroSubTerceroContratosSub;
    }

    /**
     * Asigna el objeto registroSubTerceroContratosSub
     * 
     * @param registroSubTerceroContratosSub
     * Variable a asignar en registroSubTerceroContratosSub
     */
    public void setRegistroSubTerceroContratosSub(
        Registro registroSubTerceroContratosSub) {
        this.registroSubTerceroContratosSub = registroSubTerceroContratosSub;
    }

    /**
     * Retorna el objeto registroSubResponsabilidadesFiscales
     * 
     * @return registroSubResponsabilidadesFiscales
     */
    public Registro getRegistroSubResponsabilidadesFiscales() {
        return registroSubResponsabilidadesFiscales;
    }

    /**
     * Asigna el objeto registroSubResponsabilidadesFiscales
     * 
     * @param registroSubResponsabilidadesFiscales
     * Variable a asignar en registroSubResponsabilidadesFiscales
     */
    public void setRegistroSubResponsabilidadesFiscales(
        Registro registroSubResponsabilidadesFiscales) {
        this.registroSubResponsabilidadesFiscales = registroSubResponsabilidadesFiscales;
    }

    /**
     * Retorna el objeto registroSubDeudorSolidario
     * 
     * @return registroSubDeudorSolidario
     */
    public Registro getRegistroSubDeudorSolidario() {
        return registroSubDeudorSolidario;
    }

    /**
     * Asigna el objeto registroSubDeudorSolidario
     * 
     * @param registroSubDeudorSolidario
     * Variable a asignar en registroSubDeudorSolidario
     */
    public void setRegistroSubDeudorSolidario(
        Registro registroSubDeudorSolidario) {
        this.registroSubDeudorSolidario = registroSubDeudorSolidario;
    }

    /**
     * @return the visibleTerceroContratos
     */
    public boolean isVisibleTerceroContratos() {
        return visibleTerceroContratos;
    }

    /**
     * @param visibleTerceroContratos
     * the visibleTerceroContratos to set
     */
    public void setVisibleTerceroContratos(boolean visibleTerceroContratos) {
        this.visibleTerceroContratos = visibleTerceroContratos;
    }

    /**
     * @return the visibleDependiente
     */
    public boolean isVisibleDependiente() {
        return visibleDependiente;
    }

    /**
     * @param visibleDependiente
     * the visibleDependiente to set
     */
    public void setVisibleDependiente(boolean visibleDependiente) {
        this.visibleDependiente = visibleDependiente;
    }

    /**
     * @return the listaCiudadExpedicion
     */
    public RegistroDataModelImpl getListaCiudadExpedicion() {
        return listaCiudadExpedicion;
    }

    /**
     * @param listaCiudadExpedicion
     * the listaCiudadExpedicion to set
     */
    public void setListaCiudadExpedicion(
        RegistroDataModelImpl listaCiudadExpedicion) {
        this.listaCiudadExpedicion = listaCiudadExpedicion;
    }

    public boolean isBloqueaSucursal() {
        return bloqueaSucursal;
    }

    public void setBloqueaSucursal(boolean bloqueaSucursal) {
        this.bloqueaSucursal = bloqueaSucursal;
    }

    public String getValidarSucursal() {
        return validarSucursal;
    }

    public void setValidarSucursal(String validarSucursal) {
        this.validarSucursal = validarSucursal;
    }     

	public RegistroDataModelImpl getListaNUMEROLISTA() {
		return listaNUMEROLISTA;
	}

	public void setListaNUMEROLISTA(RegistroDataModelImpl listaNUMEROLISTA) {
		this.listaNUMEROLISTA = listaNUMEROLISTA;
	}

	public boolean isVisibletipoEntidad() {
		return visibletipoEntidad;
	}

	public void setVisibletipoEntidad(boolean visibletipoEntidad) {
		this.visibletipoEntidad = visibletipoEntidad;
	}
	
	/**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

	public List<Registro> getListaDatostercero() {
		return listaDatostercero;
	}

	public void setListaDatostercero(List<Registro> listaDatostercero) {
		this.listaDatostercero = listaDatostercero;
	}

	public Registro getRegistroSubdatosTercero() {
		return registroSubdatosTercero;
	}

	public void setRegistroSubdatosTercero(Registro registroSubdatosTercero) {
		this.registroSubdatosTercero = registroSubdatosTercero;
	}

	/**
	 * @return the listaCodCiiu
	 */
	public RegistroDataModelImpl getListaCodCiiu() {
		return listaCodCiiu;
	}

	/**
	 * @param listaCodCiiu the listaCodCiiu to set
	 */
	public void setListaCodCiiu(RegistroDataModelImpl listaCodCiiu) {
		this.listaCodCiiu = listaCodCiiu;
	}

	/**
	 * @return the listaCodCiiuE
	 */
	public RegistroDataModelImpl getListaCodCiiuE() {
		return listaCodCiiuE;
	}

	/**
	 * @param listaCodCiiuE the listaCodCiiuE to set
	 */
	public void setListaCodCiiuE(RegistroDataModelImpl listaCodCiiuE) {
		this.listaCodCiiuE = listaCodCiiuE;
	}

	/**
	 * @return the listaActividadeconomica
	 */
	public RegistroDataModelImpl getListaActividadeconomica() {
		return listaActividadeconomica;
	}

	/**
	 * @param listaActividadeconomica the listaActividadeconomica to set
	 */
	public void setListaActividadeconomica(RegistroDataModelImpl listaActividadeconomica) {
		this.listaActividadeconomica = listaActividadeconomica;
	}

	/**
	 * @return the registroSubactividadEconomica
	 */
	public Registro getRegistroSubactividadEconomica() {
		return registroSubactividadEconomica;
	}

	/**
	 * @param registroSubactividadEconomica the registroSubactividadEconomica to set
	 */
	public void setRegistroSubactividadEconomica(Registro registroSubactividadEconomica) {
		this.registroSubactividadEconomica = registroSubactividadEconomica;
	}
	
	
}
