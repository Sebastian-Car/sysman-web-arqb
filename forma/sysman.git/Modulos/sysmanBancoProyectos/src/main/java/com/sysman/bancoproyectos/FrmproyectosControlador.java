package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.enums.FrmproyectosControladorEnum;
import com.sysman.bancoproyectos.enums.FrmproyectosControladorUrlEnum;
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dmaldonado
 * @version 1, 27/08/2015
 * 
 * @author ybecerra
 * @version 2, 29/09/2017, proceso de Refactoring.
 * 
 * @version 3, 22/03/2018, <strong>pespitia</strong>:
 * <li>Se adiciona la pestania Poblacion.
 */

@ManagedBean
@ViewScoped
public class FrmproyectosControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final String moduloBancos;
    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    // <DECLARAR_ATRIBUTOS>
    private final String cargoCons;
    private final String cargoResponsableRadicCons;
    private final String codigoCons;
    private final String codigoBpimCons;
    private final String codigoNovedadCons;
    private final String conProgramacionCons;
    private final String dependenciaCons;
    private final String dependenciaResponRadicCons;
    private final String fechaCons;
    private final String interventorCons;
    private final String iRegistradoCons;
    private final String nombreCons;
    private final String nombreNovedadCons;
    private final String nombreProyectoCons;
    private final String objetivoCons;
    private final String responsableCons;
    private final String responsableRadicacionCons;
    private final String sucursalCons;
    private final String sucursalRadicCons;
    private final String tbTb2283Cons;
    private final String tbTb2330Cons;
    private final String valorAnno1Cons;
    private final String valorAnno2Cons;
    private final String valorAnno3Cons;
    private final String valorAnno4Cons;
    private final String valorDptoCons;
    private final String valorNacionCons;
    private final String valorOtraEntidadCons;
    private final String valorProgramadoCons;
    private final String valorTotalCons;
    private final String vigenciaFinCons;
    private final String vigenciaInicioCons;
    private final String accionCons;
    private final String anioFinCons;
    private final String anioIniCons;
    private final String codigoProyectoCons;
    private final String dependenciaMonitorCons;
    private final String estadoMonitorCons;
    private final String iDependenciaMonitorCons;
    private final String proyectoMonitorCons;
    private final String ridProyectoCons;
    private final String vigenciaMonitorCons;
    private final String session;
    private final String nit;

    private String nombre;
    private String nombreProyecto;
    private String tipoNovTecProy;
    private String palabraProyecto;
    private String dependencia;
    private String depRecep;
    private String aniosVigencia;
    private String cargo;
    private String nombreResponsableRadic;
    private String strValidarNombre;
    private String strCodEntidad;
    private String strConsecutivoInicial;
    private boolean cargarInfoAdicional;
    private boolean creaBPIM;
    private boolean agregaPalabra;
    private boolean eliminaComp;
    private boolean creaComp;
    private boolean blnCrear;
    private boolean blnPreguntar;
    private boolean cargaParametros;
    private boolean bloqueaPeriodicidad;
    private boolean bloqueaBotones;
    private boolean vRegistrado;
    private boolean bloqueaCKRegistrado;
    private boolean campoBloqueado;
    private String editaProyecto;
    private boolean cambioTotal;
    private boolean registraProy;
    private boolean muestraNuevo;
    private boolean muestraActualiza;
    private boolean retorn = false;
    private boolean valida = false;
    private String anoIni;
    private String anoFin;
    private String nombreDependencia;
    private String menuActual;
    private String proyectoMonitor;
    private String dependenciaMonitor;
    private String vigenciaMonitor;
    private String estadoMonitor;
    private String digitosle;
    private String digitosprog;
    private String digitossubprog;
    private String digitossector;
    private String digitometres;
    private String reporte;
    private int indiceSubproyectonovedadestecnicas;
    private String idDependenciaMonitor;
    private boolean falloGuardado;
    private boolean manejaPlanDeAccion;
    private String accionActual;
    private int vigenciaActual;
    private int aux;
    private String palabraProy;
    private Map<String, Object> ridProy;
    
    // INICIO 7714837 mperez 16/05/2022
    private String modeloPlantilla;
    private Date fecha;
    private String strNombreDocumento;
    private boolean bloqueaImprimir;
    // FIN 7714837 mperez 16/05/2022
    
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaTipoProyecto;
    private List<Registro> listaEntidadProponente;
    private List<Registro> listaInterventor;
    private List<Registro> listaSectorDNP;
    private List<Registro> listaSubsectorDNP;
    private List<Registro> listaUnidad;
    private List<Registro> listaCodigoNovedad;
    private List<Registro> listaDimension;

    /**
     * Lista que contiene los items del combo destino sireci (CB5794)
     */
    private List<Registro> listaDestinoSireci;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    private RegistroDataModelImpl listaInterventorNovedad;
    private RegistroDataModelImpl listaInterventorNovedadE;
    private RegistroDataModelImpl listaResponsableNacion;
    private RegistroDataModelImpl listaResponsableDpto;
    private RegistroDataModelImpl listaResponsableOtraEntidad;

    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaResponsable;
    private RegistroDataModelImpl listaTipoDeGasto;
    private RegistroDataModelImpl listaResponsableRadic;
    private RegistroDataModelImpl listaNombreProyectoP;
    /**
     * variable que almacena Lista de de las plantillas
     */
    private RegistroDataModelImpl listaplantilla; // 7714837 mperez 16/05/2022
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaSubproyectonovedadestecnicas;
    private List<Registro> listaSubpreguntasproyectos;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     * SubProyectoNovedadesTecnicas
     */
    private Registro registroSubSubProyectoNovedadesTecnicas;
    /**
     * Atributo de referencia para el subformulario
     * SubPreguntasProyectos
     */
    private Registro registroSubSubPreguntasProyectos;
    // </DECLARAR_ADICIONALES>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbBancoProyectoCeroRemote ejbBancoProyectoCero;
    
    private Map<String,Object> parametroswf;
	private boolean verCerrar = true;

    /**
     * Crea una nueva instancia de FrmproyectosControlador
     */
    public FrmproyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        SessionUtil.setSessionVar("modulo", "52");
        moduloBancos = SessionUtil.getModulo();
        session = SessionUtil.getUser().toString();
        nit = SessionUtil.getCompaniaIngreso().getNit();
        cargoCons = GeneralParameterEnum.CARGO.getName();
        cargoResponsableRadicCons = FrmproyectosControladorEnum.CARGO_RESPONS_RADIC
                        .getValue();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        codigoBpimCons = FrmproyectosControladorEnum.CODIGOBPIM.getValue();
        codigoNovedadCons = FrmproyectosControladorEnum.CODIGONOVEDAD
                        .getValue();
        conProgramacionCons = FrmproyectosControladorEnum.CONPROGRAMACION
                        .getValue();
        dependenciaCons = GeneralParameterEnum.DEPENDENCIA.getName();
        dependenciaResponRadicCons = FrmproyectosControladorEnum.DEPENDENCIA_RESPONS_RADIC
                        .getValue();
        fechaCons = GeneralParameterEnum.FECHA.getName();
        interventorCons = FrmproyectosControladorEnum.INTERVENTOR.getValue();
        iRegistradoCons = FrmproyectosControladorEnum.IREGISTRADO.getValue();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        nombreNovedadCons = FrmproyectosControladorEnum.NOMBRENOVEDAD
                        .getValue();
        nombreProyectoCons = FrmproyectosControladorEnum.NOMBREPROYECTO
                        .getValue();
        objetivoCons = FrmproyectosControladorEnum.OBJETIVO.getValue();
        responsableCons = GeneralParameterEnum.RESPONSABLE.getName();
        responsableRadicacionCons = FrmproyectosControladorEnum.RESPONSABLE_RADIC
                        .getValue();
        sucursalCons = GeneralParameterEnum.SUCURSAL.getName();
        sucursalRadicCons = FrmproyectosControladorEnum.SUCURSAL_RADIC
                        .getValue();
        tbTb2283Cons = FrmproyectosControladorEnum.TB_TB2283.getValue();
        tbTb2330Cons = FrmproyectosControladorEnum.TB_TB2330.getValue();
        valorAnno1Cons = FrmproyectosControladorEnum.VALORANNO1.getValue();
        valorAnno2Cons = FrmproyectosControladorEnum.VALORANNO2.getValue();
        valorAnno3Cons = FrmproyectosControladorEnum.VALORANNO3.getValue();
        valorAnno4Cons = FrmproyectosControladorEnum.VALORANNO4.getValue();
        valorDptoCons = FrmproyectosControladorEnum.VALORDPTO.getValue();
        valorNacionCons = FrmproyectosControladorEnum.VALORNACION.getValue();
        valorOtraEntidadCons = FrmproyectosControladorEnum.VALOROTRAENTIDAD
                        .getValue();
        valorProgramadoCons = FrmproyectosControladorEnum.VALORPROGRAMADO
                        .getValue();
        valorTotalCons = GeneralParameterEnum.VALORTOTAL.getName();
        vigenciaFinCons = FrmproyectosControladorEnum.VIGENCIAFIN.getValue();
        vigenciaInicioCons = FrmproyectosControladorEnum.VIGENCIAINICIO
                        .getValue();
        accionCons = FrmproyectosControladorEnum.ACCION.getValue();
        anioFinCons = FrmproyectosControladorEnum.ANOFIN.getValue();
        anioIniCons = FrmproyectosControladorEnum.ANOINI.getValue();
        codigoProyectoCons = FrmproyectosControladorEnum.CODIGOPROY.getValue();
        dependenciaMonitorCons = FrmproyectosControladorEnum.DEPENDENCIAMONITOR
                        .getValue();
        estadoMonitorCons = FrmproyectosControladorEnum.ESTADOMONITOR
                        .getValue();
        iDependenciaMonitorCons = FrmproyectosControladorEnum.IDDEPENDENCIAMONITOR
                        .getValue();
        proyectoMonitorCons = FrmproyectosControladorEnum.PROYECTOMONITOR
                        .getValue();
        ridProyectoCons = FrmproyectosControladorEnum.RIDPROYECTO.getValue();
        vigenciaMonitorCons = FrmproyectosControladorEnum.VIGENCIAMONITOR
                        .getValue();
        
        try {
            // 146
            numFormulario = GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSubSubPreguntasProyectos = new Registro(
                            new HashMap<String, Object>());
            registroSubSubProyectoNovedadesTecnicas = new Registro(
                            new HashMap<String, Object>());
            cargaParametros = true;
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                if (parametrosEntrada.get(FrmproyectosControladorEnum.RETORNO
                                .getValue()) == null) {
                    retorn = false;
                }
                else {
                    retorn = (boolean) parametrosEntrada
                                    .get(FrmproyectosControladorEnum.RETORNO
                                                    .getValue());
                    ridProy = (Map<String, Object>) parametrosEntrada
                                    .get("ridProy");
                    valida = (boolean) parametrosEntrada.get("valida");
                }

                anoIni = (String) parametrosEntrada.get(anioIniCons);
                anoFin = (String) parametrosEntrada.get(anioFinCons);
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                proyectoMonitor = (String) parametrosEntrada
                                .get(proyectoMonitorCons);
                dependenciaMonitor = (String) parametrosEntrada
                                .get(dependenciaMonitorCons);
                vigenciaMonitor = (String) parametrosEntrada
                                .get(vigenciaMonitorCons);
                estadoMonitor = (String) parametrosEntrada
                                .get(estadoMonitorCons);
                idDependenciaMonitor = (String) parametrosEntrada
                                .get(iDependenciaMonitorCons);
                parametroswf = (Map<String,Object>) parametrosEntrada.get("parametroswf");
    			if(parametroswf != null) {
    				varVolver = false;
    				verCerrar = false;
    				SessionUtil.setSessionVar("menuActual",
    						SysmanFunciones.nvl(parametroswf.get("menu"),"").toString());
    			}
                if (parametrosEntrada.get(accionCons) != null) {
                    accion = (String) parametrosEntrada.get(accionCons);
                    parametrosEntrada.remove(accionCons);
                }
                else {
                    accion = null;
                }
            }

            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;
            cargarMenu();
            try {
                String validador = SessionUtil.getUser().getDependencia()
                                .getCodigo() == null ? "null"
                                    : SysmanFunciones.toString(
                                                    SessionUtil.getUser()
                                                                    .getDependencia()
                                                                    .getCodigo());
            }
            catch (NullPointerException e) {
                SessionUtil.redireccionarMenuMensaje(SysmanConstantes.MSJ_ERROR,
                                "Se debe configurar dependencia para el usuario con el que se ingreso.",
                                false);
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)

        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
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
        cargarListaNombreProyectoP();
        cargarListaDimension();
        cargarListaDependencia();
        cargarListaResponsable();
        cargarListaTipoDeGasto();
        cargarListaResponsableRadic();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaTipoProyecto();
        cargarListaEntidadProponente();
        cargarListaInterventor();
        cargarListaSectorDNP();
        cargarListaUnidad();
        cargarListaCodigoNovedad();
        cargarListaInterventorNovedad();
        cargarListaInterventorNovedadE();
        cargarListaResponsableNacion();
        cargarListaResponsableDpto();
        cargarListaResponsableOtraEntidad();
        cargarListaDestinoSireci();        
        cargarListaPlantilla(); // 7714837 mperez 16/05/2022
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubproyectonovedadestecnicas();
        cargarListaSubpreguntasproyectos();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        cargarListaSubsectorDNP();
        validarNombreProyecto();
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubproyectonovedadestecnicas = null;
        listaSubpreguntasproyectos = null;
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
        try {
        	if(parametroswf != null) {
    			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('height','770px');");
    			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('width','1830px');");
    			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('top','45px');");
    			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').parent().parent().css('left','125px');");
    			
    			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('width','1800px');");
    			JsfUtil.ejecutarJavaScript("$(window.parent.parent.document).find('iframe').css('height','550px');");
    		}
            digitosle = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS EJE", moduloBancos, new Date(),
                            false);
            digitosprog = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS PROGRAMA", moduloBancos,
                            new Date(), false);
            digitossubprog = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS SUBPROGRAMA", moduloBancos,
                            new Date(), false);
            digitossector = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS SECTOR", moduloBancos,
                            new Date(), false);
            digitometres = ejbSysmanUtil.consultarParametro(compania,
                            "NUMERO DE DIGITOS META-RESULTADO", moduloBancos,
                            new Date(), false);
            
            bloqueaImprimir = "SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
                    "IMPRIMIR PLANTILLA DESDE FORMULARIO DE PROYECTOS", moduloBancos, new Date(),
                    false),"NO"));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        enumBase = GenericUrlEnum.PROYECTOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(FrmproyectosControladorEnum.ANOINICIAL.getValue(),
                        anoIni);

        parametrosListado.put(FrmproyectosControladorEnum.ANOFINAL.getValue(),
                        anoFin);

        try {
            String strControlaDependencia = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CONTROLAR DEPENDENCIA EN BPPIM", moduloBancos,
                            new Date(), true);
            strControlaDependencia = strControlaDependencia == null ? "NO"
                : strControlaDependencia;

            parametrosListado.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            SessionUtil.getUser().getDependencia().getCodigo());
            if ("SI".equalsIgnoreCase(strControlaDependencia)
                && (SessionUtil.getNivelUsuario(moduloBancos) != 9)) {
                urlListado = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmproyectosControladorUrlEnum.URL2616
                                                                .getValue());

            }

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaSubproyectonovedadestecnicas
     */
    public void cargarListaSubproyectonovedadestecnicas() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                        registro.getCampos().get(codigoCons));

        try {
            listaSubproyectonovedadestecnicas = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                                                                                            .getGridKey())
                                                                            .getUrl(),
                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaSubpreguntasproyectos
     */
    public void cargarListaSubpreguntasproyectos() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                        registro.getCampos().get(codigoCons));

        try {
            listaSubpreguntasproyectos = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            GenericUrlEnum.PREGUNTAS_PROYECTOS
                                                                                                            .getGridKey())
                                                                            .getUrl(),
                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            GenericUrlEnum.PREGUNTAS_PROYECTOS
                                                                            .getTable()));
        }
        catch (SystemException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista <code>listaDestinoSireci</code> asociada al
     * combo destino sireci (CB5794).
     */
    public void cargarListaDestinoSireci() {
        Map<String, Object> param = new HashMap<>();

        try {
            listaDestinoSireci = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoProyecto
     */
    public void cargarListaTipoProyecto() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoProyecto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL15021
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
     * Carga la lista listaEntidadProponente
     */
    public void cargarListaEntidadProponente() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEntidadProponente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL15523
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
     * Carga la lista listaInterventor
     */
    public void cargarListaInterventor() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaInterventor = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL16085
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
     * Carga la lista listaSectorDNP
     */
    public void cargarListaSectorDNP() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaSectorDNP = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL16561
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
     * Carga la lista listaSubsectorDNP
     */
    public void cargarListaSubsectorDNP() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmproyectosControladorEnum.CODIGOSECTORDNP.getValue(),
                        SysmanFunciones.nvl(
                                        registro.getCampos().get("SECTORDNP"),
                                        "").toString());

        try {
            listaSubsectorDNP = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL17058
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
     * Carga la lista listaUnidad
     * 
     */
    public void cargarListaUnidad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaUnidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL17597
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
     * Carga la lista listaCodigoNovedad
     */
    public void cargarListaCodigoNovedad() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmproyectosControladorEnum.TIPO.getValue(), tipoNovTecProy);

        try {
            listaCodigoNovedad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL18188
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
     * Carga la lista listaDimension
     */
    public void cargarListaDimension() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaDimension = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL18725
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
     * Carga la lista listaInterventorNovedad
     */
    public void cargarListaInterventorNovedad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL19189
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaInterventorNovedad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmproyectosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaInterventorNovedad
     */
    public void cargarListaInterventorNovedadE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL19189
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaInterventorNovedadE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmproyectosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaDependencia
     */
    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL20499
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        codigoCons);

    }

    /**
     * 
     * Carga la lista listaResponsable
     */
    public void cargarListaResponsable() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL21568
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        dependencia);

        listaResponsable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.RESPONSABLE.getName());

    }

    /**
     * 
     * Carga la lista listaTipoDeGasto
     */
    public void cargarListaTipoDeGasto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL24087
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaTipoDeGasto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        codigoCons);
    }

    /**
     * 
     * Carga la lista listaResponsableRadic
     */
    public void cargarListaResponsableRadic() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL24790
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        depRecep);

        listaResponsableRadic = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmproyectosControladorEnum.RESPONSABLE_RADIC
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaResponsableNacion
     */
    public void cargarListaResponsableNacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL26519
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaResponsableNacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmproyectosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaResponsableDpto
     */
    public void cargarListaResponsableDpto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL26519
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaResponsableDpto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmproyectosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaResponsableOtraEntidad
     */
    public void cargarListaResponsableOtraEntidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL26519
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaResponsableOtraEntidad = new RegistroDataModelImpl(
                        urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmproyectosControladorEnum.NIT.getValue());

    }

    /**
     * 
     * Carga la lista listaNombreProyectoP
     */

    public void cargarListaNombreProyectoP() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmproyectosControladorUrlEnum.URL28117
                                                        .getValue());

        listaNombreProyectoP = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true,
                        GeneralParameterEnum.CONSECUTIVO.getName());

    }
    
    /**
     * Carga la lista listaplantilla
     * @author: María Alejandra Pérez Salazar
     */    
    // INICIO 7714837 mperez 16/05/2022
    public void cargarListaPlantilla() {

        HashMap<String, Object> param = new HashMap<>();

        param.put(FrmproyectosControladorEnum.TIPO.getValue(), "23");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        				FrmproyectosControladorUrlEnum.URL56180
                                                        .getValue());
        listaplantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }
    // FIN 7714837 mperez 16/05/2022

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CodigoNovedad
     * 
     */
    public void cambiarCodigoNovedad() {
        try {

            String[] condicion = {
                                   "  COMPANIA = ''", compania,
                                   "''  AND CODIGOPROYECTO = ''",
                                   registro.getCampos().get(codigoCons)
                                                   .toString(),
                                   "''",
                                   " AND TIPO = ''",
                                   registroSubSubProyectoNovedadesTecnicas
                                                   .getCampos().get("TIPO")
                                                   .toString()

                                   , "''  "

            };

            setAux((int) ejbSysmanUtil.generarSiguienteConsecutivo(
                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                            .getTable(),
                            SysmanFunciones.concatenar(condicion),
                            GeneralParameterEnum.CONSECUTIVO.getName()));

            String[] condAux = { "COMPANIA = ''", compania,
                                 "''   AND CODIGOPROYECTO = ''",
                                 registro.getCampos()
                                                 .get(codigoCons).toString(),
                                 "''", " AND CODIGONOVEDAD =  ",
                                 registroSubSubProyectoNovedadesTecnicas
                                                 .getCampos()
                                                 .get(codigoNovedadCons)
                                                 .toString(),
                                 " AND TIPO = ''",
                                 registroSubSubProyectoNovedadesTecnicas
                                                 .getCampos()
                                                 .get("TIPO").toString(),
                                 "''"

            };
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                            ejbSysmanUtil.generarSiguienteConsecutivo(
                                                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                                                            .getTable(),
                                                            SysmanFunciones.concatenar(
                                                                            condAux),
                                                            GeneralParameterEnum.CONSECUTIVO
                                                                            .getName()));
            // </CODIGO_DESARROLLADO>
        }

        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado al cambiar el control Tipo
     * 
     */
    public void cambiarTipo() {
        // <CODIGO_DESARROLLADO>
        tipoNovTecProy = (String) registroSubSubProyectoNovedadesTecnicas
                        .getCampos()
                        .get(FrmproyectosControladorEnum.TIPO.getValue());
        switch (tipoNovTecProy) {
        case "EST":
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(nombreNovedadCons,
                                            idioma.getString("TB_TB3641"));
            break;
        case "LIC":
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(nombreNovedadCons,
                                            idioma.getString("TB_TB3642"));
            break;
        case "DIS":
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(nombreNovedadCons,
                                            idioma.getString("TB_TB3643"));
            break;
        case "OTR":
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(nombreNovedadCons,
                                            idioma.getString("TT_LB4568")
                                                            .replace(":", ""));
            break;
        default:
            break;

        }
        registroSubSubProyectoNovedadesTecnicas.getCampos()
                        .put(codigoNovedadCons, null);
        cargarListaCodigoNovedad();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Dimension
     * 
     */
    public void cambiarDimension() {
        // <CODIGO_DESARROLLADO>
        registroSubSubPreguntasProyectos.getCampos().put("RESPUESTA", null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ResponsableRadic
     * 
     */
    public void cambiarResponsableRadic() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Codigo
     * 
     */
    public void cambiarCodigo() {
    	
    	boolean consecutivoInd = false;
    	try {
    		consecutivoInd = ("SI").equals(SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
    						"MANEJA CONSECUTIVO PROYECTO INDEPENDIENTE DE CODIGO BPIN EN PROYECTOS",
    						SessionUtil.getModulo(), new Date(), false), "NO"));
    	} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);

		}
    	
    	if (!consecutivoInd) {
    	
	        if ((registro.getCampos().get(codigoBpimCons) != null)
	            && (registro.getCampos().get(codigoBpimCons) != "")) {
	            creaBPIM = true;
	        }
	        else {
	            String codigo = registro.getCampos().get(codigoCons).toString();
	            if (codigo.length() > 4) {
	                registro.getCampos().put(codigoBpimCons,
	                                SysmanFunciones.concatenar(
	                                                codigo.substring(0, 4),
	                                                strCodEntidad,
	                                                codigo.substring(codigo.length()
	                                                    - 4,
	                                                                codigo.length())));
	            }
	            else {
	                registro.getCampos().put(codigoBpimCons,
	                                SysmanFunciones.concatenar(codigo,
	                                                strCodEntidad, codigo));
	            }
	        }
        
    	}
    }

    /**
     * Metodo ejecutado al cambiar el control NombreProyecto
     * 
     */
    public void cambiarNombreProyecto() {
        if (registro.getCampos().get(nombreProyectoCons) != null) {
            registro.getCampos().put(nombreProyectoCons, registro.getCampos()
                            .get(nombreProyectoCons).toString().toUpperCase());
        }
    }

    /**
     * Metodo ejecutado al cambiar el control VigenciaInicio
     * 
     */
    public void cambiarVigenciaInicio() {

        validarAnosVigencia();

        aniosVigencia = calcularAniosVigencia(registro);

    }

    /**
     * Metodo ejecutado al cambiar el control VigenciaFin
     * 
     */
    public void cambiarVigenciaFin() {
        validarAnosVigencia();
        aniosVigencia = calcularAniosVigencia(registro);
    }

    /**
     * Metodo ejecutado al cambiar el control ValorTotal
     * 
     */
    public void cambiarValorTotal() {
        cambioTotal = true;
    }

    /**
     * Metodo ejecutado al cambiar el control ValorAnnoUno
     * 
     */
    public void cambiarValorAnnoUno() {
        double ano1 = registro.getCampos().get(valorAnno1Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno1Cons)
                            .toString());
        double ano2 = registro.getCampos().get(valorAnno2Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno2Cons)
                            .toString());
        double ano3 = registro.getCampos().get(valorAnno3Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno3Cons)
                            .toString());
        double ano4 = registro.getCampos().get(valorAnno4Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno4Cons)
                            .toString());
        double valorEval = ano1 + ano2 + ano3 + ano4;

        if (valorEval > (registro.getCampos().get(valorTotalCons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorTotalCons)
                            .toString()))) {
            JsfUtil.agregarMensajeError(idioma.getString(tbTb2330Cons));
            registro.getCampos().put(valorAnno1Cons, 0);
        }

    }

    /**
     * Metodo ejecutado al cambiar el control ValorAnnoDos
     * 
     */
    public void cambiarValorAnnoDos() {
        double ano1 = registro.getCampos().get(valorAnno1Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno1Cons)
                            .toString());
        double ano2 = registro.getCampos().get(valorAnno2Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno2Cons)
                            .toString());
        double ano3 = registro.getCampos().get(valorAnno3Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno3Cons)
                            .toString());
        double ano4 = registro.getCampos().get(valorAnno4Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno4Cons)
                            .toString());
        double valorEval = ano1 + ano2 + ano3 + ano4;

        if (valorEval > (registro.getCampos().get(valorTotalCons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorTotalCons)
                            .toString()))) {
            JsfUtil.agregarMensajeError(idioma.getString(tbTb2330Cons));
            registro.getCampos().put(valorAnno2Cons, 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control ValorAnnoTres
     * 
     */
    public void cambiarValorAnnoTres() {
        double ano1 = registro.getCampos().get(valorAnno1Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno1Cons)
                            .toString());
        double ano2 = registro.getCampos().get(valorAnno2Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno2Cons)
                            .toString());
        double ano3 = registro.getCampos().get(valorAnno3Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno3Cons)
                            .toString());
        double ano4 = registro.getCampos().get(valorAnno4Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno4Cons)
                            .toString());
        double valorEval = ano1 + ano2 + ano3 + ano4;

        if (valorEval > (registro.getCampos().get(valorTotalCons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorTotalCons)
                            .toString()))) {
            JsfUtil.agregarMensajeError(idioma.getString(tbTb2330Cons));
            registro.getCampos().put(valorAnno3Cons, 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control ValorAnnoCuatro
     * 
     */
    public void cambiarValorAnnoCuatro() {
        double ano1 = registro.getCampos().get(valorAnno1Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno1Cons)
                            .toString());
        double ano2 = registro.getCampos().get(valorAnno2Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno2Cons)
                            .toString());
        double ano3 = registro.getCampos().get(valorAnno3Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno3Cons)
                            .toString());
        double ano4 = registro.getCampos().get(valorAnno4Cons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorAnno4Cons)
                            .toString());
        double valorEval = ano1 + ano2 + ano3 + ano4;

        if (valorEval > (registro.getCampos().get(valorTotalCons) == null ? 0.0
            : Double.valueOf(registro.getCampos().get(valorTotalCons)
                            .toString()))) {
            JsfUtil.agregarMensajeError(idioma.getString(tbTb2330Cons));
            registro.getCampos().put(valorAnno4Cons, 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control ValorNacion
     * 
     */
    public void cambiarValorNacion() {
        String valor = registro.getCampos().get(valorNacionCons).toString();
        if (SysmanFunciones.validarVariableVacio(valor)) {
            registro.getCampos().put(valorNacionCons, 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control ValorDpto
     * 
     */
    public void cambiarValorDpto() {
        String valor = registro.getCampos().get(valorDptoCons).toString();
        if (SysmanFunciones.validarVariableVacio(valor)) {
            registro.getCampos().put(valorDptoCons, 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control ValorOtraEntidad
     * 
     */
    public void cambiarValorOtraEntidad() {
        String valor = registro.getCampos().get(valorOtraEntidadCons)
                        .toString();
        if (SysmanFunciones.validarVariableVacio(valor)) {
            registro.getCampos().put(valorOtraEntidadCons, 0);
        }
    }

    /**
     * Metodo ejecutado al cambiar el control dialogoEliminaComp
     * 
     */
    public void cambiardialogoEliminaComp() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control dialogoCreaComp
     * 
     */
    public void cambiardialogoCreaComp() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control dialogoCreaBPIM
     * 
     */
    public void cambiardialogoCreaBPIM() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control dialogoRegistraProy
     * 
     */
    public void cambiardialogoRegistraProy() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control dialogoAgregarPalabra
     * 
     */
    public void cambiardialogoAgregarPalabra() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control cmdTipoNovedadTecnica
     * 
     */
    public void retornarFormulariocmdTipoNovedadTecnica(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoEliminaComp en la vista
     *
     */
    public void aceptardialogoEliminaComp() {
        eliminaComp = false;
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("TB_TB2367"));
        try {
            String valorT = registro.getCampos()
                            .get(valorTotalCons) == null ? "0"
                                : registro.getCampos().get(
                                                valorTotalCons).toString();

            BigDecimal dblValorTotal = new BigDecimal(valorT);

            ejbBancoProyectoCero.eliminarComponentes(compania,
                            registro.getCampos().get(codigoCons).toString(),
                            dblValorTotal,
                            Integer.valueOf(registro.getCampos()
                                            .get(vigenciaInicioCons)
                                            .toString()),
                            SessionUtil.getUser().getCodigo());

        }

        catch (NumberFormatException | SystemException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * dialogoEliminaComp en la vista
     *
     */
    public void cancelardialogoEliminaComp() {
        eliminaComp = false;
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2368"));
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoCreaComp en la vista
     *
     */
    public void aceptardialogoCreaComp() {
        try {
            creaComp = false;
            if ("i".equalsIgnoreCase(accion) || blnCrear) {
                if (validarComponente()) {
                    return;
                }
                String valorT = registro.getCampos().get(valorTotalCons) == null
                    ? "0"
                    : registro.getCampos().get(valorTotalCons).toString();
                BigDecimal dblValorTotal = new BigDecimal(valorT);

                ejbBancoProyectoCero.insertarComponentes(compania,
                                registro.getCampos().get(codigoCons).toString(),
                                dblValorTotal,
                                Integer.valueOf(registro.getCampos()
                                                .get(vigenciaInicioCons)
                                                .toString()),
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2370"));
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * dialogoCreaComp en la vista
     *
     */
    public void cancelardialogoCreaComp() {
        creaComp = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoCreaBPIM en la vista
     *
     */
    public void aceptardialogoCreaBPIM() {
        creaBPIM = false;
        String codigo = registro.getCampos().get(codigoCons).toString();
        if (codigo.length() > 4) {
            registro.getCampos().put(codigoBpimCons, SysmanFunciones.concatenar(
                            codigo.substring(0, 4), strCodEntidad,
                            codigo.substring(codigo.length() - 4,
                                            codigo.length())));
        }
        else {
            registro.getCampos().put(codigoBpimCons, SysmanFunciones.concatenar(
                            codigo, strCodEntidad, codigo));
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * dialogoCreaBPIM en la vista
     *
     */
    public void cancelardialogoCreaBPIM() {
        creaBPIM = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoRegistraProy en la vista
     *
     */
    public void aceptardialogoRegistraProy() {

        try {
            // <CODIGO_DESARROLLADO>
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmproyectosControladorUrlEnum.URL19851
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(codigoCons));
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put("NUMEROREGISTRO",
                            registro.getCampos().get("CODIGOBPIM"));
            fields.put(FrmproyectosControladorEnum.IREGISTRADO.getValue(),
                            "-1");
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);

            vRegistrado = true;
            registro.getCampos().put(iRegistradoCons, -1);
            accion = "v";
            bloqueaPeriodicidad = true;
            bloqueaBotones = true;
            cargarRegistro(css, accion, registro.getIndice());

            bloqueaCKRegistrado = SessionUtil.getNivelGrupo(moduloBancos) != 9;
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        registraProy = false;
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * dialogoRegistraProy en la vista
     *
     */
    public void cancelardialogoRegistraProy() {
        registraProy = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoAgregarPalabra en la vista
     *
     */
    public void aceptardialogoAgregarPalabra() {
        StringBuilder nombrePa = new StringBuilder();
        agregaPalabra = false;
        registro.getCampos().put(nombreProyectoCons,
                        nombrePa.append(palabraProyecto).append(" ")
                                        .append(registro.getCampos()
                                                        .get(nombreProyectoCons)
                                                        .toString()));
    }

    public void cambiarTipoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        tipoNovTecProy = listaSubproyectonovedadestecnicas.get(rowNum)
                        .getCampos()
                        .get(FrmproyectosControladorEnum.TIPO.getValue())
                        .toString();
        cargarListaCodigoNovedad();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Dimension en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarDimensionC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaSubpreguntasproyectos.get(rowNum % 10).getCampos().put("RESPUESTA",
                        null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInterventorNovedad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInterventorNovedad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubSubProyectoNovedadesTecnicas.getCampos().put(interventorCons,
                        registroAux.getCampos()
                                        .get(FrmproyectosControladorEnum.NIT
                                                        .getValue()));
        registroSubSubProyectoNovedadesTecnicas.getCampos().put(sucursalCons,
                        registroAux.getCampos().get(sucursalCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaInterventorNovedad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInterventorNovedadE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(FrmproyectosControladorEnum.NIT.getValue())
                        .toString();
        registroSubSubProyectoNovedadesTecnicas.getCampos().put(interventorCons,
                        registroAux.getCampos()
                                        .get(FrmproyectosControladorEnum.NIT
                                                        .getValue()));
        registroSubSubProyectoNovedadesTecnicas.getCampos().put(sucursalCons,
                        registroAux.getCampos().get(sucursalCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(dependenciaCons,
                        registroAux.getCampos().get(codigoCons));
        dependencia = registroAux.getCampos().get(codigoCons).toString();
        nombreDependencia = registroAux.getCampos().get(nombreCons).toString();
        nombre = null;
        cargo = null;
        registro.getCampos().put(responsableCons, null);
        listaResponsable = null;
        cargarListaResponsable();
    }

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
        registro.getCampos().put(responsableCons,
                        registroAux.getCampos().get(responsableCons));
        nombre = registroAux.getCampos().get(nombreCons).toString();
        cargo = registroAux.getCampos().get(cargoCons).toString();
        registro.getCampos().put(sucursalCons,
                        registroAux.getCampos().get(sucursalCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoDeGasto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDeGasto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPODEGASTO",
                        registroAux.getCampos().get(codigoCons));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsableRadic
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableRadic(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            if ((registroAux.getCampos().get(responsableRadicacionCons) != null)
                && (registroAux.getCampos()
                                .get(responsableRadicacionCons) != "")) {
                registro.getCampos().put(responsableRadicacionCons, registroAux
                                .getCampos().get(responsableRadicacionCons));
                registro.getCampos().put(sucursalRadicCons,
                                registroAux.getCampos().get(sucursalCons));
                registro.getCampos().put(dependenciaResponRadicCons, depRecep);
                nombreResponsableRadic = registroAux.getCampos()
                                .get(nombreCons).toString();
                registro.getCampos().put(cargoResponsableRadicCons,
                                registroAux.getCampos().get(cargoCons));
            }
            else {
                Map<String, Object> fields = new TreeMap<>();
                fields.put(FrmproyectosControladorEnum.RESPONSABLE_RADIC
                                .getValue(), SysmanConstantes.CONS_TERCERO);

                Registro reg;

                reg = listaResponsableRadic
                                .getRegistroUnico(fields);

                registro.getCampos().put(responsableRadicacionCons,
                                SysmanConstantes.CONS_TERCERO);
                registro.getCampos().put(sucursalRadicCons,
                                SysmanConstantes.CONS_SUCURSAL);
                registro.getCampos().put(dependenciaResponRadicCons,
                                SysmanConstantes.CONS_DEPENDENCIA);
                if (reg != null) {
                    registro.getCampos().put(cargoResponsableRadicCons,
                                    reg.getCampos().get(cargoCons));
                    nombreResponsableRadic = reg.getCampos()
                                    .get(nombreCons).toString();
                }
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsableNacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableNacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!SysmanFunciones.validarVariableVacio(registroAux.getCampos().get(
                        FrmproyectosControladorEnum.NIT.getValue())
                        .toString())) {
            registro.getCampos()
                            .put(FrmproyectosControladorEnum.RESPONSABLENACION
                                            .getValue(),
                                            registroAux.getCampos()
                                                            .get(FrmproyectosControladorEnum.NIT
                                                                            .getValue()));
        }
        else {
            registro.getCampos()
                            .put(FrmproyectosControladorEnum.RESPONSABLENACION
                                            .getValue(),
                                            SysmanConstantes.CONS_TERCERO);
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsableDpto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableDpto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!SysmanFunciones.validarVariableVacio(registroAux.getCampos().get(
                        FrmproyectosControladorEnum.NIT.getValue())
                        .toString())) {
            registro.getCampos().put(FrmproyectosControladorEnum.RESPONSABLEDPTO
                            .getValue(),
                            registroAux.getCampos()
                                            .get(FrmproyectosControladorEnum.NIT
                                                            .getValue()));
        }
        else {
            registro.getCampos().put(FrmproyectosControladorEnum.RESPONSABLEDPTO
                            .getValue(),
                            SysmanConstantes.CONS_TERCERO);
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaResponsableOtraEntidad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaResponsableOtraEntidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (!SysmanFunciones.validarVariableVacio(registroAux.getCampos().get(
                        FrmproyectosControladorEnum.NIT.getValue())
                        .toString())) {
            registro.getCampos().put(
                            FrmproyectosControladorEnum.RESPONSABLEOTRAENTIDAD
                                            .getValue(),
                            registroAux
                                            .getCampos()
                                            .get(FrmproyectosControladorEnum.NIT
                                                            .getValue()));
        }
        else {
            registro.getCampos()
                            .put(FrmproyectosControladorEnum.RESPONSABLEOTRAENTIDAD
                                            .getValue(),
                                            SysmanConstantes.CONS_TERCERO);
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNombreProyectoP
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNombreProyectoP(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        palabraProyecto = null;
        palabraProy = idioma.getString("TB_TB3651").replace(
                        "s$palabraProyecto$s",
                        registroAux.getCampos()
                                        .get(FrmproyectosControladorEnum.PALABRA
                                                        .getValue())
                                        .toString());
        palabraProyecto = registroAux.getCampos()
                        .get(FrmproyectosControladorEnum.PALABRA.getValue())
                        .toString();

        agregaPalabra = true;
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaPlantilla
     * @author: María Alejandra Pérez Salazar
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaplantilla(SelectEvent event) {
        try {
            Registro registroAux = (Registro) event.getObject();
            modeloPlantilla = registroAux.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName())
                            .toString();
            fecha = SysmanFunciones.convertirAFecha(
                            registroAux.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName())
                                            .toString());
            strNombreDocumento = registroAux.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())
                            .toString();
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    public void oprimirImpresora(ActionEvent ac) {
        generarInformePlanDeAccion();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton eTitulo en la vista
     *
     */
    public void oprimireTitulo() {
        // <CODIGO_DESARROLLADO>
        crearComponentesAutomaticos(true, true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdLocalizacion en la
     * vista
     *
     */
    public void oprimirCmdLocalizacion() {
        // <CODIGO_DESARROLLADO>

        String codigo = registro.getCampos().get(codigoCons).toString();
        if (SysmanFunciones.validarVariableVacio(codigo)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }
        String[] campos = { "codigoProy", accionCons };
        String[] valores = { registro.getCampos().get(codigoCons).toString(),
                             accion };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMSUBPROYECTOSLOCALIZACIONS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos, campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdComponentes en la vista
     *
     */
    public void oprimircmdComponentes() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("codigoProy",
                        registro.getCampos().get(codigoCons).toString());
        parametros.put(ridProyectoCons, registro.getLlave());
        parametros.put(anioIniCons, anoIni);
        parametros.put(anioFinCons, anoFin);
        parametros.put(proyectoMonitorCons, proyectoMonitor);
        parametros.put(dependenciaMonitorCons, dependenciaMonitor);
        parametros.put(vigenciaMonitorCons, vigenciaMonitor);
        parametros.put(estadoMonitorCons, estadoMonitor);
        parametros.put(iDependenciaMonitorCons, idDependenciaMonitor);
        parametros.put(accionCons, accion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCOMPONENTES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdIndicadores en la vista
     *
     */
    public void oprimirCmdIndicadores() {

        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                            registro.getCampos().get(codigoCons));

            List<Registro> anios;

            anios = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL2083
                                                                            .getValue())
                                            .getUrl(), param));

            if (SysmanFunciones.validarVariableVacio(
                            registro.getCampos().get(codigoCons).toString())) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(tbTb2283Cons));
                return;
            }
            if (anios.isEmpty()) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2337"));
                return;
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(codigoProyectoCons,
                            registro.getCampos().get(codigoCons).toString());
            parametros.put(ridProyectoCons, css);
            parametros.put(anioIniCons, anoIni);
            parametros.put(anioFinCons, anoFin);
            parametros.put(proyectoMonitorCons, proyectoMonitor);
            parametros.put(dependenciaMonitorCons, dependenciaMonitor);
            parametros.put(vigenciaMonitorCons, vigenciaMonitor);
            parametros.put(estadoMonitorCons, estadoMonitor);
            parametros.put(iDependenciaMonitorCons, idDependenciaMonitor);
            parametros.put(accionCons, accion);
            parametros.put("codigoBpim",
                            registro.getCampos().get(codigoBpimCons)
                                            .toString());

            Direccionador direccionador = new Direccionador();

            direccionador.setParametros(parametros);

            if (manejaPlanDeAccion) {
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FRMINDICADORESBPS_CONTROLADOR
                                                .getCodigo()));

            }
            else {
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.SUBBPPROYECTOPLANINDICATIVOS_CONTROLADOR
                                                .getCodigo()));

            }
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
     * Metodo ejecutado al oprimir el boton Cmdresponsables en la
     * vista
     *
     */
    public void oprimirCmdresponsables() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(codigoProyectoCons,
                        registro.getCampos().get(codigoCons).toString());
        parametros.put(ridProyectoCons, css);
        parametros.put(anioIniCons, anoIni);
        parametros.put(anioFinCons, anoFin);
        parametros.put(proyectoMonitorCons, proyectoMonitor);
        parametros.put(dependenciaMonitorCons, dependenciaMonitor);
        parametros.put(vigenciaMonitorCons, vigenciaMonitor);
        parametros.put(estadoMonitorCons, estadoMonitor);
        parametros.put(iDependenciaMonitorCons,
                        idDependenciaMonitor);
        parametros.put(accionCons, accion);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.SUBRESPONSABLESPROYECTOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdModificaciones en la
     * vista
     *
     */
    public void oprimirCmdModificaciones() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }
        String[] campos = { codigoProyectoCons, accionCons };
        String[] valores = { registro.getCampos().get(codigoCons).toString(),
                             accionCons };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBMODIFICACIONESPROYECTOS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EditarProyecto en la vista
     *
     */
    public void oprimirEditarProyecto() {
        try {
            // <CODIGO_DESARROLLADO>

            if (css == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2358"));
                return;
            }

            if (vRegistrado && SessionUtil.getNivelUsuario(moduloBancos) != 9) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3762"));
                return;
            }

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmproyectosControladorUrlEnum.URL19851
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            fields.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(codigoCons));
            fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put("NUMEROREGISTRO",
                            registro.getCampos().get("CODIGOBPIM"));
            fields.put(FrmproyectosControladorEnum.IREGISTRADO.getValue(),
                            "0");
            Parameter parameter = new Parameter();
            parameter.setFields(fields);
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            parameter);
            vRegistrado = false;
            registro.getCampos().put(iRegistradoCons, 0);
            accion = "m";
            bloqueaBotones = false;
            bloqueaPeriodicidad = false;
            bloqueaCKRegistrado = SessionUtil.getNivelGrupo(moduloBancos) != 9;
            archivoDescarga = null;
            // </CODIGO_DESARROLLADO>
        }

        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdFichasTecnicas en la
     * vista
     *
     */
    public void oprimircmdFichasTecnicas() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }
        String[] campos = { codigoProyectoCons, ridProyectoCons, accionCons };
        Object[] valores = { registro.getCampos().get(codigoCons).toString(),
                             registro.getLlave(), accion };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMFICHATECNICAPROYECTOS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton eNombreProyecto en la
     * vista
     *
     */
    public void oprimireNombreProyecto(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton eResponsable en la vista
     *
     */
    public void oprimireResponsable() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(codigoProyectoCons,
                        registro.getCampos().get(codigoCons).toString());
        parametros.put(ridProyectoCons, css);
        parametros.put(anioIniCons, anoIni);
        parametros.put(anioFinCons, anoFin);
        parametros.put(proyectoMonitorCons, proyectoMonitor);
        parametros.put(dependenciaMonitorCons, dependenciaMonitor);
        parametros.put(vigenciaMonitorCons, vigenciaMonitor);
        parametros.put(estadoMonitorCons, estadoMonitor);
        parametros.put(iDependenciaMonitorCons,
                        idDependenciaMonitor);
        parametros.put(accionCons, accion);
        parametros.put("menup", "-1");

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.RESPONSABLES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, moduloBancos);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton eDependencia en la vista
     *
     */
    public void oprimireDependencia() {
        // <CODIGO_DESARROLLADO>
        boolean retorno;

        Map<String, Object> parametros = new HashMap<>();
        if (registro.getLlave().isEmpty()) {
            retorno = false;
            parametros.put(FrmproyectosControladorEnum.RETORNO.getValue(),
                            retorno);
        }
        else {
            retorno = true;
            parametros.put(FrmproyectosControladorEnum.RETORNO.getValue(),
                            retorno);
            parametros.put("ridProyecto", registro.getLlave());
        }

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.DEPENDENCIAS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdEstrucPlan en la vista
     *
     */
    public void oprimircmdEstrucPlan() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }
        String[] campos = { codigoProyectoCons, accionCons };
        String[] valores = { registro.getCampos().get(codigoCons).toString(),
                             accion };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBPROYECTORUBROS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdEntidadesFinanciacion
     * en la vista
     *
     */
    public void oprimircmdEntidadesFinanciacion() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            JsfUtil.agregarMensajeInformativo(idioma.getString(tbTb2283Cons));
            return;
        }
        String anoInicial = registro.getCampos().get(vigenciaInicioCons)
                        .toString();
        String anoFinal = registro.getCampos().get(vigenciaFinCons).toString();
        String totalProyecto = registro.getCampos().get(valorTotalCons)
                        .toString();
        String[] campos = { codigoProyectoCons, "anoInicial", "anoFinal",
                            "totalProyecto", accionCons };
        String[] valores = { registro.getCampos().get(codigoCons).toString(),
                             anoInicial, anoFinal, totalProyecto,
                             accion };
        SessionUtil.cargarModalDatosFlash(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.SUBPROYFUENTESFINANCIACIONS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdTipoNovedadTecnica en
     * la vista
     *
     */
    public void oprimircmdTipoNovedadTecnica() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cargarModalDatos(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMTIPONOVEDADTECNICAS_CONTROLADOR
                                                        .getCodigo()),
                        moduloBancos);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al dar click sobre el botón PDF
     * @author: María Alejandra Pérez Salazar
     */
    // INICIO 7714837 mperez 17/05/2022
    public void oprimirImprimir() {
        String[] camposP = new String[3];
        String[] valores = new String[3];

        camposP[0] = "codigoPlantilla";
        camposP[1] = "fechaPlantilla";
        camposP[2] = "nombreDocDescarga";

        valores[0] = modeloPlantilla;
        valores[1] = SysmanFunciones.formatearFecha(fecha);
        valores[2] = strNombreDocumento;

        HashMap<String, String> variablesConsultaW = new HashMap<>();
        variablesConsultaW.put("s$compania$s",
                        SysmanFunciones.concatenar("'", compania, "'"));
        variablesConsultaW.put("s$proyecto$s",
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CODIGO.getName()).toString());


        // variables por parametro para documento word
        SessionUtil.setSessionVar("variablesConsultaWord",
                        variablesConsultaW);
        String numForm = String
                        .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                        .getCodigo());
        SessionUtil.cargarModalDatosFlash(numForm,
                        SessionUtil.getModulo(),
                        camposP, valores);

    }
    // FIN 7714837 mperez 17/05/2022

    /**
     * 
     * Metodo de cargar el informe al darle en el boton registrar
     * 
     * @throws SystemException
     *
     */

    /**
     * 
     * Metodo ejecutado al oprimir el boton Registrar en la vista
     *
     */
    public void oprimirRegistrar() {
        // <CODIGO_DESARROLLADO>

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                            registro.getCampos().get(codigoCons));

            boolean estado = validarOprimirRegistrar();
            if (estado) {
                Registro cuentaComponentes;

                cuentaComponentes = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmproyectosControladorUrlEnum.URL49616
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.valueOf(cuentaComponentes.getCampos().get("CONTEO")
                                .toString()) == 0) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB2353"));
                    estado = false;
                }

                Registro cuentaMetas = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmproyectosControladorUrlEnum.URL46885
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (!manejaPlanDeAccion && estado
                    && Integer.valueOf(cuentaMetas.getCampos().get("CONTEO")
                                    .toString()) == 0) {

                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TB_TB2354"));
                    estado = false;

                }
                if (estado) {
                    registraProy = true;
                }
            }
            // cargarInforme(ReportesBean.FORMATOS.PDF);
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto rcVolver en la
     * vista
     *
     */
    public void ejecutarrcVolver() {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(proyectoMonitorCons,
                        proyectoMonitor);
        parametros.put(dependenciaMonitorCons,
                        dependenciaMonitor);
        parametros.put(vigenciaMonitorCons,
                        vigenciaMonitor);
        parametros.put(estadoMonitorCons,
                        estadoMonitor);
        parametros.put(iDependenciaMonitorCons,
                        idDependenciaMonitor);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.MONITORPROYECTOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());

    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>

    /**
     * Metodo de insercion del formulario Subproyectonovedadestecnicas
     */
    public void agregarRegistroSubSubproyectonovedadestecnicas() {
        try {
            if (registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .get(FrmproyectosControladorEnum.TIPO
                                            .getValue()) == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2332"));
                return;
            }

            if (registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .get(codigoNovedadCons) == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2334"));
                return;
            }
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(GeneralParameterEnum.COMPANIA.getName(),
                                            compania);
            registroSubSubProyectoNovedadesTecnicas.getCampos().put(
                            FrmproyectosControladorEnum.CODIGOPROYECTO
                                            .getValue(),
                            registro.getCampos().get(codigoCons));
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .remove("NOMBRETIPO");
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .remove("NOMBREINTERVENTOR");
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .remove(GeneralParameterEnum.FECHA.getName());
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .remove("NOMBREESTADO");
            registroSubSubProyectoNovedadesTecnicas.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubProyectoNovedadesTecnicas.getCampos()
                            .put(GeneralParameterEnum.DATE_CREATED.getName(),
                                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubProyectoNovedadesTecnicas
                                            .getCampos());

            cargarListaSubproyectonovedadestecnicas();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            registroSubSubProyectoNovedadesTecnicas = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subproyectonovedadestecnicas
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubproyectonovedadestecnicas(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(FrmproyectosControladorEnum.CODIGOPROYECTO
                            .getValue());
            reg.getCampos().remove(FrmproyectosControladorEnum.TIPO.getValue());
            reg.getCampos().remove(codigoNovedadCons);
            reg.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
            reg.getCampos().remove(GeneralParameterEnum.FECHA.getName());
            reg.getCampos().remove("NOMBRETIPO");
            reg.getCampos().remove("NOMBREINTERVENTOR");
            reg.getCampos().remove("NOMBREESTADO");
            reg.getCampos().remove("NOMBRENOVEDAD");
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            cargarListaSubproyectonovedadestecnicas();
        }
    }

    /**
     * Metodo de eliminacion del formulario
     * Subproyectonovedadestecnicas
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubproyectonovedadestecnicas(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.BPPROYECTONOVEDADESTECNICAS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubproyectonovedadestecnicas();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subproyectonovedadestecnicas
     */
    public void cancelarEdicionSubproyectonovedadestecnicas() {
        cargarListaSubproyectonovedadestecnicas();
        cargarListaSubpreguntasproyectos();
    }

    /**
     * Metodo de insercion del formulario Subpreguntasproyectos
     */
    public void agregarRegistroSubSubpreguntasproyectos() {
        try {
            registroSubSubPreguntasProyectos.getCampos().put(
                            GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSubSubPreguntasProyectos.getCampos().put(
                            GeneralParameterEnum.PROYECTO.getName(),
                            registro.getCampos().get(codigoCons));
            registroSubSubPreguntasProyectos.getCampos()
                            .remove(GeneralParameterEnum.NOMBRE.getName());

            registroSubSubPreguntasProyectos.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubPreguntasProyectos.getCampos()
                            .put(GeneralParameterEnum.DATE_CREATED.getName(),
                                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PREGUNTAS_PROYECTOS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubPreguntasProyectos
                                            .getCampos());

            cargarListaSubpreguntasproyectos();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            registroSubSubPreguntasProyectos = new Registro(
                            new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Subpreguntasproyectos
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubSubpreguntasproyectos(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.PROYECTO.getName());
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PREGUNTAS_PROYECTOS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            cargarListaSubpreguntasproyectos();
        }
    }

    /**
     * Metodo de eliminacion del formulario Subpreguntasproyectos
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubSubpreguntasproyectos(Registro reg) {
        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PREGUNTAS_PROYECTOS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubpreguntasproyectos();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subpreguntasproyectos
     */
    public void cancelarEdicionSubpreguntasproyectos() {
        cargarListaSubpreguntasproyectos();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * 
     */
    public void validarAnosVigencia() {

        if (registro.getCampos().get(FrmproyectosControladorEnum.VIGENCIAINICIO
                        .getValue()) != null
            && registro.getCampos().get(FrmproyectosControladorEnum.VIGENCIAFIN
                            .getValue()) != null) {

            int anoVigenciaInicial = Integer.parseInt(registro.getCampos()
                            .get(FrmproyectosControladorEnum.VIGENCIAINICIO
                                            .getValue())
                            .toString());
            int anoVigenciaFinal = Integer.parseInt(registro.getCampos()
                            .get(FrmproyectosControladorEnum.VIGENCIAFIN
                                            .getValue())
                            .toString());
            if (anoVigenciaInicial > anoVigenciaFinal) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3660"));
                registro.getCampos()
                                .put(FrmproyectosControladorEnum.VIGENCIAINICIO
                                                .getValue(), null);
                registro.getCampos().put(FrmproyectosControladorEnum.VIGENCIAFIN
                                .getValue(), null);
                aniosVigencia = null;
                return;

            }
        }

    }

    /**
     * @return
     */
    public boolean validarComponente() {

        if (blnPreguntar) {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.CODIGOPROYECTO
                            .getValue(),
                            registro.getCampos().get(codigoCons));

            Registro compGastos;

            try {
                compGastos = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmproyectosControladorUrlEnum.URL52142
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (compGastos != null
                    && compGastos.getCampos().get("X") != null) {
                    eliminaComp = true;
                    return true;
                }

            }
            catch (SystemException e) {

                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return false;
    }

    /**
     * 
     */
    public void cargarMenu() {
        switch (menuActual) {
        case "52020102":
            validarOpcionMenuUno();
            break;
        case "52020101":
            varVolver = false;
            muestraNuevo = muestraActualiza = true;
            break;
        case "52020402":
            validarOpcionMenuCuatro();
            break;
        case "NULL":
            SessionUtil.redireccionarMenu();
            break;
        default:
            break;

        }
    }

    /**
     * 
     */
    public void validarOpcionMenuUno() {
        varVolver = muestraNuevo = muestraActualiza = false;
        bloqueaPeriodicidad = true;
        editaProyecto = "none";
        if (rid != null) {
            accion = "v";
        }

    }

    /**
     * 
     */
    public void validarOpcionMenuCuatro() {
        varVolver = bloqueaPeriodicidad = true;
        muestraNuevo = muestraActualiza = false;

        editaProyecto = "none";
        if (rid != null) {
            accion = "v";
        }
    }

    /**
     * @param registroAux
     * @return
     */
    public String calcularAniosVigencia(Registro registroAux) {
        if ((registroAux.getCampos().get(vigenciaFinCons) == null)
            || (registroAux.getCampos().get(vigenciaInicioCons) == null)) {
            return "";
        }
        else {
            if (!registroAux.getCampos().get(vigenciaFinCons).toString()
                            .isEmpty()
                || registroAux.getCampos().get(vigenciaInicioCons).toString()
                                .isEmpty()) {

                int aux1 = (1 + Integer.valueOf(registroAux.getCampos()
                                .get(vigenciaFinCons).toString()))
                    - Integer.valueOf(registroAux.getCampos()
                                    .get(vigenciaInicioCons).toString());
                return String.valueOf(aux1);
            }
            return "";
        }

    }

    /**
     * @param event
     */
    public void retornarFormularioCmdLocalizacion(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @return
     */
    public boolean validarNombreProyecto() {
        try {
            String strNombreProyecto = registro.getCampos()
                            .get(nombreProyectoCons).toString();
            strNombreProyecto = SysmanFunciones
                            .eliminarTildes(strNombreProyecto.toLowerCase())
                            .toUpperCase();
            List<Registro> listaPalabras;

            listaPalabras = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL51104
                                                                            .getValue())
                                            .getUrl(), null));

            for (Registro listaPalabra : listaPalabras) {
                if (strNombreProyecto.contains(listaPalabra.getCampos()
                                .get(FrmproyectosControladorEnum.PALABRA
                                                .getValue())
                                .toString().toUpperCase())) {
                    return true;
                }
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return false;
    }

    /**
     * @return
     */
    public boolean validarOprimirRegistrar() {
        boolean estado = true;
        if ("v".equals(accion)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2339"));
            estado = false;
        }
        if (estado) {
            agregarRegistroNuevo(false);
        }
        if (falloGuardado) {
            estado = false;
        }
        else if (vRegistrado) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2342"));
            estado = false;
        }
        else if (registro.getCampos().get(nombreProyectoCons) == null ? true
            : registro.getCampos().get(nombreProyectoCons).toString()
                            .isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2345"));
            estado = false;
        }
        else if (registro.getCampos().get("TIPOPROYECTO") == null ? true
            : registro.getCampos().get("TIPOPROYECTO").toString().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2346"));
            estado = false;
        }
        else {
            estado = validarRegistrar(estado);
        }
        return estado;
    }

    /**
     * @param aux
     * @return
     */
    public boolean validarRegistrar(boolean aux) {
        boolean estado = aux;
        if (registro.getCampos().get("ENTIDADPROPONENTE") == null ? true
            : registro.getCampos().get("ENTIDADPROPONENTE").toString()
                            .isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2347"));
            estado = false;
        }
        else if (registro.getCampos().get(vigenciaInicioCons) == null ? true
            : registro.getCampos().get(vigenciaInicioCons).toString()
                            .isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2348"));
            estado = false;
        }
        else if (registro.getCampos().get(vigenciaFinCons) == null ? true
            : registro.getCampos().get(vigenciaFinCons).toString().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2349"));
            estado = false;
        }
        else if (!validarBotonRegistrar()) {
            estado = false;
        }
        return estado;
    }

    /**
     * @return
     */
    public boolean validarBotonRegistrar() {
        boolean rta = true;
        if (registro.getCampos().get(codigoCons) == null ? true
            : registro.getCampos().get(codigoCons).toString().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2350"));
            rta = false;
        }
        else if (registro.getCampos().get(dependenciaCons) == null ? true
            : registro.getCampos().get(dependenciaCons).toString().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2351"));
            rta = false;
        }
        else if (registro.getCampos().get("OBJETO") == null ? true
            : registro.getCampos().get("OBJETO").toString().isEmpty()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2352"));
            rta = false;
        }

        return rta;
    }

    /**
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {

        StringBuilder cond = new StringBuilder();
        try {
            if (manejaPlanDeAccion) {
                generarInformePlanDeAccion();
            }
            else {

                Map<String, Object> parametros = new HashMap<>();
                HashMap<String, Object> reemplazar = new HashMap<>();
                String formatoCaqueta = "001879certificadoregistroCaq";
                String formatoCajica = "001962CertificadoRegistroCaj";
                String formatoSTR = "001949certificadoregistro";
                String formatoFlorencia = "003003CertficadoRegsitroFlorencia";
                // INICIO MPEREZ 04/05/2022
                String formatoCastilla = "002363CertificadoRegistroCastilla";
                // FIN MPEREZ 04/05/2022

                String registrarProyecto = ejbSysmanUtil.consultarParametro(
                                compania,
                                "FORMATO INFORME REGISTRAR PROYECTO",
                                moduloBancos, new Date(),
                                false);

                if (formatoSTR.equals(registrarProyecto)) {

                    reemplazar.put("codigo",
                                    registro.getCampos().get(codigoCons));
                    reemplazar.put("cargo", cargo);
                    Date fecha = new Date();
                    parametros.put("PR_FECHA", fecha);
                    parametros.put("PR_AHORA", fecha);
                    parametros.put("PR_CIUDADCOMPANIA",
                                    SessionUtil.getCompaniaIngreso()
                                                    .getCiudad());
                    parametros.put("PR_GETUSER",
                                    SessionUtil.getUser().getCodigo());
                    parametros.put("PR_PLAN_DE_DESARROLLO",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "PLAN DE DESARROLLO",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_TITULO_INFORME_CERTIFICADO_DE_REGISTRO",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "TITULO INFORME CERTIFICADO DE REGISTRO",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_CARGO_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "CARGO JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));

                    Reporteador.resuelveConsulta(formatoSTR,
                                    Integer.parseInt(moduloBancos), reemplazar,
                                    parametros);
                    archivoDescarga = JsfUtil.exportarStreamed(
                                    formatoSTR,
                                    parametros, ConectorPool.ESQUEMA_SYSMAN,
                                    formato);
                }
                else if (formatoCaqueta.equals(registrarProyecto)) {
                    digitossector = "2";
                    reemplazar.put("compania", compania);
                    reemplazar.put("digitole", digitosle);
                    reemplazar.put("digitoprog", digitosprog);
                    reemplazar.put("digitosubprog", digitossubprog);
                    reemplazar.put("digitossector", digitossector);
                    reemplazar.put("codigo",
                                    registro.getCampos().get(codigoCons));

                    parametros.put("PR_NITCOMPANIA", nit);
                    parametros.put("PR_PLAN_DE_DESARROLLO", ejbSysmanUtil
                                    .consultarParametro(compania,
                                                    "PLAN DE DESARROLLO",
                                                    moduloBancos, new Date(),
                                                    true)
                                    .toLowerCase());
                    parametros.put("PR_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_CARGO_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "CARGO JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_NOMBRAMIENTO_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "NOMBRAMIENTO JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_DATOS_COORDINADOR_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "DATOS COORDINADOR BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_IMAGENES2",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "RUTA IMAGEN LEMA CAQUETA",
                                                    moduloBancos, new Date(),
                                                    false));
                    parametros.put("PR_GETUSER",
                                    SessionUtil.getUser().getCodigo());

                    Reporteador.resuelveConsulta(formatoCaqueta,
                                    Integer.parseInt(moduloBancos), reemplazar,
                                    parametros);

                    archivoDescarga = JsfUtil.exportarStreamed(formatoCaqueta,
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);
                }
                else if (formatoCajica.equals(registrarProyecto)) {
                    reemplazar.put("compania", compania);
                    reemplazar.put("digitoprog", digitosprog);
                    reemplazar.put("digitossector", digitossector);
                    reemplazar.put("digitodim", "2");
                    reemplazar.put("digitometres", digitometres);
                    reemplazar.put("codigo",
                                    registro.getCampos().get(codigoCons));

                    Date fecha = new Date();
                    parametros.put("PR_FECHA", fecha);
                    parametros.put("PR_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    // NUEVO
                    parametros.put("PR_CARGO_JEFE_ASESORIA_PLANEACION",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "CARGO JEFE ASESORIA DE PLANEACION",
                                                    moduloBancos, new Date(),
                                                    true));

                    parametros.put("PR_DATOS_COORDINADOR_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "DATOS COORDINADOR BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_NOMBRAMIENTO_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "NOMBRAMIENTO JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    // NUEVO
                    parametros.put("PR_PROFESIONAL_UNIVERSITARIO_EGR_ESM",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "PROFESIONAL UNIVERSITARIO",
                                                    moduloBancos, new Date(),
                                                    true));

                    Reporteador.resuelveConsulta(formatoCajica,
                                    Integer.parseInt(moduloBancos), reemplazar,
                                    parametros);

                    archivoDescarga = JsfUtil.exportarStreamed(formatoCajica,
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);
                }
                else if (formatoFlorencia.equals(registrarProyecto)) {
                    reemplazar.put("compania", compania);
                    reemplazar.put("digitoprog", digitosprog);
                    reemplazar.put("digitossector", digitossector);
                    reemplazar.put("digitodim", "2");
                    reemplazar.put("digitometres", digitometres);
                    reemplazar.put("codigo",
                                    registro.getCampos().get(codigoCons));
                    reemplazar.put("vigenciaInicioCons", anoIni);
                    reemplazar.put("vigenciaFinCons", anoFin);

                    Date fecha = new Date();
                    java.sql.Date sFecha = new java.sql.Date(fecha.getTime());
                    parametros.put("PR_FECHA", sFecha);
                    parametros.put("PR_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_CARGO_JEFE_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "CARGO JEFE BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));
                    parametros.put("PR_DATOS_COORDINADOR_BANCO_PROYECTOS",
                                    ejbSysmanUtil.consultarParametro(compania,
                                                    "DATOS COORDINADOR BANCO PROYECTOS",
                                                    moduloBancos, new Date(),
                                                    true));

                    Reporteador.resuelveConsulta(formatoFlorencia,
                                    Integer.parseInt(moduloBancos), reemplazar,
                                    parametros);

                    archivoDescarga = JsfUtil.exportarStreamed(formatoFlorencia,
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);
                }
                // INICIO 7711610 mperez 05/05/2022
                else if (formatoCastilla.equals(registrarProyecto)) {
                    reemplazar.put("compania", compania);                    
                    reemplazar.put("codigo",
                                    registro.getCampos().get(codigoCons));                     
                    reemplazar.put("sector",
                    		ejbSysmanUtil.consultarParametro(compania,
                                    "NUMERO DE DIGITOS SECTOR",
                                    moduloBancos, new Date(),
                                    true));                    
                    reemplazar.put("programa",
                    		ejbSysmanUtil.consultarParametro(compania,
                                    "NUMERO DE DIGITOS PROGRAMA",
                                    moduloBancos, new Date(),
                                    true)); 
                    
                    parametros.put("PR_PLAN_DE_DESARROLLO",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "PLAN DE DESARROLLO",
                                            moduloBancos, new Date(),
                                            true));
                    parametros.put("PR_JEFE_BANCO_PROYECTOS",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "JEFE BANCO PROYECTOS",
                                            moduloBancos, new Date(),
                                            true));
		            parametros.put("PR_CARGO_JEFE_BP",
		                            ejbSysmanUtil.consultarParametro(compania,
		                                            "CARGO JEFE BANCO PROYECTOS",
		                                            moduloBancos, new Date(),
		                                            true));
		            parametros.put("PR_IMAGEN",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "IMAGEN LOGO CASTILLA",
                                            moduloBancos, new Date(),
                                            false));
                    
		            parametros.put("PR_IMAGEN_LEMA_CASTILLA",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "IMAGEN LEMA2 CASTILLA",
                                            moduloBancos, new Date(),
                                            false));
		            
		            parametros.put("PR_IMAGEN_REDES",
                            ejbSysmanUtil.consultarParametro(compania,
                                            "IMAGEN REDES CASTILLA",
                                            moduloBancos, new Date(),
                                            false));
                    Reporteador.resuelveConsulta(formatoCastilla,
                                    Integer.parseInt(moduloBancos), reemplazar,
                                    parametros);

                    archivoDescarga = JsfUtil.exportarStreamed(formatoCastilla,
                                    parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formato);
                }
                // FIN 7711610 mperez 05/05/2022
            }
        }

        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString("MSM_INFORME_NO_EXISTE_APLICACION"));
            Logger.getLogger(FrmproyectosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
        }
        catch (SystemException | JRException | IOException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }

    }

    /**
     * 
     */
    public void generarInformePlanDeAccion() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(FrmproyectosControladorEnum.TIPO.getValue(), "23");

            Registro rPlantilla;

            rPlantilla = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL56179
                                                                            .getValue())
                                            .getUrl(), param));

            if (rPlantilla != null) {

                String proyectoActual = registro.getCampos()
                                .get(codigoCons).toString();

                Date fechaPlantilla = (Date) rPlantilla.getCampos()
                                .get(fechaCons);

                String[] campos = new String[3];
                String[] valores = new String[3];

                campos[0] = "codigoPlantilla";
                campos[1] = "fechaPlantilla";
                campos[2] = "nombreDocDescarga";

                valores[0] = rPlantilla.getCampos().get(codigoCons).toString();
                valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
                valores[2] = SysmanFunciones.concatenar(
                                "CERTIFICADO DE REGISTRO PROYECTO ",
                                proyectoActual);

                HashMap<String, String> variablesConsultaW = new HashMap<>();

                variablesConsultaW.put("s$COMPANIA$s",
                                SysmanFunciones.concatenar("'", compania, "'"));
                variablesConsultaW.put("s$COD_PROYECTO$s",
                                SysmanFunciones.concatenar("'", proyectoActual,
                                                "'"));
                variablesConsultaW.put("s$RUBROS$s",
                                SysmanFunciones.concatenar("'",
                                                obtenerRubros(proyectoActual),
                                                "'"));
                variablesConsultaW.put("s$NOMBRE_ALCALDE$s",
                                SysmanFunciones.concatenar("'",
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "ALCALDE MUNICIPAL",
                                                                moduloBancos,
                                                                new Date(),
                                                                true)

                                                , "'"));
                variablesConsultaW.put("s$PLAN_DESARROLLO$s",
                                SysmanFunciones.concatenar("'",
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "PLAN DE DESARROLLO",
                                                                moduloBancos,
                                                                new Date(),
                                                                true),
                                                "'"));
                variablesConsultaW.put("s$VIGENCIA_PDM$s",
                                SysmanFunciones.concatenar(
                                                "'",
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "VIGENCIA PDM PARA SOLICITUD CDP",
                                                                moduloBancos,
                                                                new Date(),
                                                                true),
                                                "'"));
                variablesConsultaW
                                .put("s$FIRMA_REGISTRO$s",
                                                SysmanFunciones.concatenar(
                                                                "'",
                                                                ejbSysmanUtil.consultarParametro(
                                                                                compania,
                                                                                "FIRMA REGISTRO DE PROYECTO",
                                                                                moduloBancos,
                                                                                new Date(),
                                                                                true),
                                                                "'"));
                variablesConsultaW.put("s$CARGO_REGISTRO$s",
                                SysmanFunciones.concatenar(
                                                "'",
                                                ejbSysmanUtil.consultarParametro(
                                                                compania,
                                                                "CARGO FIRMA REGISTRO DE PROYECTO",
                                                                moduloBancos,
                                                                new Date(),
                                                                true),
                                                "'"));

                HashMap<String, String> fuentes = (HashMap<String, String>) obtenerFuentes();
                if (fuentes != null) {
                    variablesConsultaW.put("s$RP$s",
                                    SysmanFunciones.concatenar("'",
                                                    SysmanFunciones.nvlStr(
                                                                    fuentes.get("RP"),
                                                                    "0"),
                                                    "'"));
                    variablesConsultaW.put("s$CN$s",
                                    SysmanFunciones.concatenar("'",
                                                    SysmanFunciones.nvlStr(
                                                                    fuentes.get("CN"),
                                                                    "0"),
                                                    "'"));
                    variablesConsultaW.put("s$SGP$s",
                                    SysmanFunciones.concatenar(
                                                    "'", SysmanFunciones.nvlStr(
                                                                    fuentes.get("SGP"),
                                                                    "0"),
                                                    "'"));
                    variablesConsultaW.put("s$CD$s",
                                    SysmanFunciones.concatenar("'",
                                                    SysmanFunciones.nvlStr(
                                                                    fuentes.get("CD"),
                                                                    "0"),
                                                    "'"));
                    variablesConsultaW.put("s$SGR$s",
                                    SysmanFunciones.concatenar(
                                                    "'", SysmanFunciones.nvlStr(
                                                                    fuentes.get("SGR"),
                                                                    "0"),
                                                    "'"));
                    variablesConsultaW.put("s$CR$s",
                                    SysmanFunciones.concatenar("'",
                                                    SysmanFunciones.nvlStr(
                                                                    fuentes.get("CR"),
                                                                    "0"),
                                                    "'"));
                    variablesConsultaW.put("s$OT$s",
                                    SysmanFunciones.concatenar("'",
                                                    SysmanFunciones.nvlStr(
                                                                    fuentes.get("OT"),
                                                                    "0"),
                                                    "'"));
                }
                else {
                    return;
                }

                HashMap<String, String> datosResponsable = (HashMap<String, String>) getResponsableProyecto(
                                proyectoActual);
                if (datosResponsable != null) {
                    variablesConsultaW.put("s$FECHA_VIABILIDAD$s",
                                    SysmanFunciones.concatenar("'",
                                                    datosResponsable.get(
                                                                    fechaCons),
                                                    "'"));
                    variablesConsultaW.put("s$EXPEDIDO_POR$s",
                                    SysmanFunciones.concatenar("'",
                                                    datosResponsable.get(
                                                                    nombreCons),
                                                    "'"));
                    variablesConsultaW.put("s$DEPENDENCIA$s",
                                    SysmanFunciones.concatenar("'",
                                                    datosResponsable.get(
                                                                    dependenciaCons),
                                                    "'"));
                }
                else {
                    return;
                }

                Map<String, String> estructuraPlan = obtenerEstructuraPlan(
                                accionActual, vigenciaActual);
                if (estructuraPlan != null) {
                    variablesConsultaW.put("s$EJE$s",
                                    SysmanFunciones.concatenar(
                                                    "'",
                                                    estructuraPlan.get("EJE"),
                                                    "'"));
                    variablesConsultaW.put("s$POLITICA$s",
                                    SysmanFunciones.concatenar("'",
                                                    estructuraPlan.get(
                                                                    "POLITICA"),
                                                    "'"));
                    variablesConsultaW.put("s$PROGRAMA$s",
                                    SysmanFunciones.concatenar("'",
                                                    estructuraPlan.get(
                                                                    "PROGRAMA"),
                                                    "'"));
                    variablesConsultaW.put("s$OBJETIVO$s",
                                    SysmanFunciones.concatenar("'",
                                                    estructuraPlan.get(
                                                                    objetivoCons),
                                                    "'"));
                    variablesConsultaW.put("s$SUBPROGRAMA$s",
                                    SysmanFunciones.concatenar("'",
                                                    estructuraPlan.get(
                                                                    "SUBPROGRAMA"),
                                                    "'"));
                }
                else {
                    return;
                }

                variablesConsultaW.put("s$METAS$s",
                                SysmanFunciones.concatenar("'",
                                                asignarNombreMetas(
                                                                proyectoActual),
                                                "'"));

                // variables por parametro para documento word
                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);
                SessionUtil.cargarModalDatosFlash(Integer
                                .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos, valores);
            }
        }
        catch (Exception e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * @param proyectoActual
     * @return
     */
    private String asignarNombreMetas(String proyectoActual) {
        String auxMetas = "";
        StringBuilder metas = new StringBuilder();
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                            vigenciaActual);
            param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                            proyectoActual);
            List<Registro> lMetas;

            lMetas = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL27044
                                                                            .getValue())
                                            .getUrl(), param));

            if (lMetas != null) {
                for (Registro m : lMetas) {
                    metas.append(m.getCampos().get("META")).append(",");
                }
                if (metas.length() > 0) {
                    auxMetas = metas.substring(0, metas.length() - 1);
                }

            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2355"));
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return auxMetas;
    }

    /**
     * @param accionP
     * @param vigenciaP
     * @return
     */
    private Map<String, String> obtenerEstructuraPlan(String accionP,
        int vigenciaP) {
        try {
            Map<String, String> estructura = null;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                            vigenciaP);
            param.put(FrmproyectosControladorEnum.ID.getValue(),
                            accionP);

            List<Registro> lEstructura;

            lEstructura = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL27577
                                                                            .getValue())
                                            .getUrl(), param));

            if (lEstructura != null) {
                estructura = new HashMap<>();
                estructura = retornarEstructura(lEstructura, estructura);

            }

            return estructura;
        }
        catch (SystemException e1) {
            JsfUtil.agregarMensajeError(e1.getMessage());
            logger.error(e1.getMessage(), e1);

        }
        return null;
    }

    /**
     * @param lEstructura
     * @param nivel
     * @param estructura
     * @return
     */
    public Map<String, String> retornarEstructura(
        List<Registro> lEstructura,
        Map<String, String> estructura) {
        String nivel;
        for (Registro e : lEstructura) {
            nivel = e.getCampos().get("NIVEL").toString();
            estructura.put(nivel,
                            e.getCampos().get("DESCRIPCION")
                                            .toString());
            if ("PROGRAMA".equals(nivel)) {
                estructura.put(objetivoCons,
                                (String) e.getCampos()
                                                .get(objetivoCons));
            }
        }

        return estructura;
    }

    /**
     * @param proyecto
     * @return
     */
    public String obtenerRubros(String proyecto) {
        StringBuilder rubros = new StringBuilder();
        String auxRubros = "";
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                            proyecto);
            List<Registro> lRubros;

            lRubros = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL3457
                                                                            .getValue())
                                            .getUrl(), param));

            if (lRubros != null) {
                for (Registro r : lRubros) {
                    rubros.append(r.getCampos().get("RUBRO").toString())
                                    .append(",");
                }

                if (rubros.length() > 0) {
                    auxRubros = rubros.substring(0, rubros.length() - 1);
                }

            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return auxRubros;
    }

    /**
     * @return
     */
    public Map<String, String> obtenerFuentes() {
        Map<String, String> fuentes = null;
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.CODIGOBPIM.getValue(),
                            registro.getCampos().get(codigoBpimCons));

            Registro rAccion = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL3494
                                                                            .getValue())
                                            .getUrl(), param));

            if (rAccion != null) {
                accionActual = rAccion.getCampos().get("ID").toString();
                vigenciaActual = Integer.parseInt(rAccion.getCampos()
                                .get("VIGENCIA_INICIAL").toString());
                param.clear();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                param.put(FrmproyectosControladorEnum.ID.getValue(),
                                accionActual);
                param.put(GeneralParameterEnum.VIGENCIA_INICIAL.getName(),
                                vigenciaActual);

                List<Registro> lFuentes;

                lFuentes = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                FrmproyectosControladorUrlEnum.URL3526
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (!lFuentes.isEmpty()) {
                    fuentes = new HashMap<>();

                    fuentes = asignarFuente(lFuentes, fuentes);

                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2356"));
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2309"));
            }

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return fuentes;
    }

    /**
     * @param lFuentes
     * @param fuentes
     * @return
     */
    public Map<String, String> asignarFuente(List<Registro> lFuentes,
        Map<String, String> fuentes) {
        for (Registro f : lFuentes) {
            fuentes.put(f.getCampos().get("TIPO_RECURSO").toString(),
                            f.getCampos().get("VALOR_FUENTE")
                                            .toString());
        }
        return fuentes;
    }

    /**
     * @param proyectoActual
     * @return
     */
    public Map<String, String> getResponsableProyecto(String proyectoActual) {
        Map<String, String> responsable = null;
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.CODIGOPROYECTO.getValue(),
                            proyectoActual);

            List<Registro> lResponsable;

            lResponsable = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL3596
                                                                            .getValue())
                                            .getUrl(), param));

            if (lResponsable != null) {
                responsable = verificarResponsableProyecto(lResponsable);

            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2357"));
            }

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return responsable;
    }

    /**
     * @param lResponsable
     * @return
     */
    public Map<String, String> verificarResponsableProyecto(
        List<Registro> lResponsable) {
        Map<String, String> responsable = new HashMap<>();
        StringBuilder nombres = new StringBuilder();
        StringBuilder dependencias = new StringBuilder();
        String auxNombres = "";
        String auxDependencias = "";
        for (Registro rResponsable : lResponsable) {
            if (responsable.get(fechaCons) == null) {
                try {
                    responsable.put(fechaCons,
                                    SysmanFunciones.convertirAFechaCadena(
                                                    (Date) rResponsable
                                                                    .getCampos()
                                                                    .get("FECHARESPONSABLE")));
                }
                catch (ParseException e) {
                    Logger.getLogger(FrmproyectosControlador.class.getName())
                                    .log(Level.SEVERE, null, e);
                }
            }
            nombres.append((String) rResponsable.getCampos()
                            .get("NOMB_RESPONSABLE")).append(",");

            dependencias.append(rResponsable.getCampos()
                            .get(dependenciaCons).toString()).append(",");
        }
        if (nombres.length() > 0 && dependencias.length() > 0) {
            auxNombres = nombres.substring(0, nombres.length() - 1);
            auxDependencias = dependencias.substring(0,
                            dependencias.length() - 1);
        }
        responsable.put(nombreCons, auxNombres);
        responsable.put(dependenciaCons, auxDependencias);
        return responsable;
    }

    /**
     * @param r
     */
    public void activarEdicionSubproyectonovedadestecnicas(Registro r) {
        indiceSubproyectonovedadestecnicas = listaSubproyectonovedadestecnicas
                        .indexOf(r);
        tipoNovTecProy = r.getCampos()
                        .get(FrmproyectosControladorEnum.TIPO.getValue())
                        .toString();
        cargarListaCodigoNovedad();
    }

    /**
     * @param registroAux
     * @return
     */
    public String asignarNombreTercero(Registro registroAux) {
        try {
            String aux1 = registroAux.getCampos().get(responsableCons)
                            .toString();
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.NIT.getValue(),
                            aux1);

            Registro auxTercero = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL3681
                                                                            .getValue())
                                            .getUrl(), param));

            return auxTercero.getCampos().get(nombreCons).toString();
        }
        catch (NullPointerException | IndexOutOfBoundsException
                        | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        return "";

    }

    /**
     * @param registroAux
     * @return
     */
    public String asignarNombreDep(Registro registroAux) {
        try {
            String aux1 = registroAux.getCampos().get(dependenciaCons)
                            .toString();

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            aux1);

            Registro auxDependencia = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL3722
                                                                            .getValue())
                                            .getUrl(), param));

            return auxDependencia.getCampos().get(nombreCons).toString();
        }
        catch (NullPointerException | IndexOutOfBoundsException
                        | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return "";

    }

    /**
     * @param registroAux
     * @return
     */
    public String asignarCargoResponsable(Registro registroAux) {
        try {
            String aux1 = registroAux.getCampos().get(responsableCons)
                            .toString();
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmproyectosControladorEnum.CEDULA.getValue(),
                            aux1);

            Registro auxCargo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmproyectosControladorUrlEnum.URL3755
                                                                            .getValue())
                                            .getUrl(), param));

            return auxCargo.getCampos().get(cargoCons).toString();
        }
        catch (NullPointerException | IndexOutOfBoundsException
                        | SystemException e) {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        return "";

    }

    /**
     * 
     */
    public void cargarParametrosIniciales() {
        if (cargaParametros) {
            try {
                depRecep = ejbSysmanUtil.consultarParametro(compania,
                                "DEPENDENCIA PARA RECEPCION DE PROYECTOS",
                                moduloBancos, new Date(), true);
                strConsecutivoInicial = ejbSysmanUtil.consultarParametro(
                                compania, "CONSECUTIVO INICIAL DE PROYECTOS",
                                moduloBancos, new Date(), true);
                strCodEntidad = ejbSysmanUtil.consultarParametro(compania,
                                "CODIGO ENTIDAD DNP", moduloBancos, new Date(),
                                true);
                strValidarNombre = ejbSysmanUtil.consultarParametro(compania,
                                "VALIDAR NOMBRE DEL PROYECTO", moduloBancos,
                                new Date(), true);
                strValidarNombre = strValidarNombre == null ? "NO"
                    : strValidarNombre;
                manejaPlanDeAccion = "SI"
                                .equals(ejbSysmanUtil.consultarParametro(
                                                compania,
                                                "MANEJA PLAN DE ACCION",
                                                moduloBancos,
                                                new Date(), true)) ? true
                                                    : false;
                String strConfiguraInfo = ejbSysmanUtil.consultarParametro(
                                compania,
                                idioma.getString("TB_TB3657"),
                                moduloBancos, new Date(), true);

                cargarInfoAdicional = ("SI")
                                .equalsIgnoreCase(strConfiguraInfo == null
                                    ? "NO"
                                    : strConfiguraInfo);
                cargaParametros = false;
            }
            catch (SystemException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
            finally {
                if (depRecep == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2359"));
                }

                if (strCodEntidad == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2363"));
                }
            }
        }
    }

    /**
     * 
     */
    public void actualizaValorTotal() {
        Double valorTotal = registro.getCampos().get(valorTotalCons) == null ? 0
            : Double.valueOf(registro.getCampos().get(valorTotalCons)
                            .toString());
        Double valorProgramado = registro.getCampos()
                        .get(valorProgramadoCons) == null ? 0
                            : Double.valueOf(registro.getCampos()
                                            .get(valorProgramadoCons)
                                            .toString());
        if ((registro.getCampos().get(conProgramacionCons) == null ? false
            : ((boolean) registro.getCampos().get(conProgramacionCons)))
            && valorTotal < valorProgramado) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2365"));
            return;
        }
        // DM: -Revisa si hubo alguna modificaciï¿½n en el valor total
        // del proyecto.
        if (cambioTotal) {
            crearComponentesAutomaticos(true, true);
        }
        cambioTotal = false;
    }

    /**
     * @param blnCrear
     * @param blnPreguntar
     * @param boton
     */
    public void crearComponentesAutomaticos(boolean blnCrear,
        boolean blnPreguntar) {
        this.blnCrear = blnCrear;
        this.blnPreguntar = blnPreguntar;
        try {

            String parCreaComp;

            parCreaComp = ejbSysmanUtil.consultarParametro(compania,
                            "CREAR COMPONENTES PREDETERMINADOS DE GASTOS",
                            moduloBancos, new Date(), true);

            if ("SI".equalsIgnoreCase(
                            parCreaComp == null ? "NO" : parCreaComp)) {
                aceptardialogoCreaComp();
            }
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * @return
     */
    public boolean validarProgramacion() {
        if (registro.getCampos().get(conProgramacionCons) == null) {
            return false;
        }
        else {
            return (boolean) registro.getCampos().get(conProgramacionCons);
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
        if (valida) {
            if (retorn)

            {

                cargarRegistro(ridProy, ACCION_MODIFICAR);
            }
            else {
                cargarRegistro(null, "i");
            }
        }

        cargarParametrosIniciales();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        try {

            cargarParametrosIniciales();

            if (ACCION_MODIFICAR.equals(accion)) {
            	
            	if("SI".equals(ejbSysmanUtil.consultarParametro(compania, "CAMPO FECHA EDITA", 
            			moduloBancos, new Date(), true))) 
            	{
            		campoBloqueado = false;
            	}
            	else
            	{
            		campoBloqueado = true;
            	}
            	
                nombre = asignarNombreTercero(registro);
                cargo = asignarCargoResponsable(registro);
                nombreDependencia = asignarNombreDep(registro);
                aniosVigencia = calcularAniosVigencia(registro);
                bloqueaPeriodicidad = (boolean) registro.getCampos()
                                .get(conProgramacionCons);
                if ((boolean) registro.getCampos().get(iRegistradoCons)) {
                    accion = "v";
                    bloqueaBotones = true;
                    bloqueaPeriodicidad = true;
                    vRegistrado = true;

                }
                else {
                    bloqueaBotones = false;
                    vRegistrado = false;
                }
                if (SessionUtil.getNivelGrupo(moduloBancos) == 9) {
                    bloqueaCKRegistrado = false;
                    editaProyecto = "block";

                }
                else {
                    bloqueaCKRegistrado = true;
                    editaProyecto = "none";
                }
                cargarListaResponsableRadic();

                Map<String, Object> fields = new TreeMap<>();
                fields.put(FrmproyectosControladorEnum.RESPONSABLE_RADIC
                                .getValue(),
                                registro.getCampos()
                                                .get(responsableRadicacionCons));

                Registro reg;

                reg = listaResponsableRadic.getRegistroUnico(fields);

                if (reg != null) {
                    registro.getCampos().put(sucursalRadicCons,
                                    reg.getCampos().get(sucursalCons));
                    registro.getCampos().put(dependenciaResponRadicCons,
                                    depRecep);
                    nombreResponsableRadic = SysmanFunciones.nvl(reg.getCampos()
                                    .get(nombreCons), "").toString();
                    registro.getCampos().put(cargoResponsableRadicCons,
                                    reg.getCampos().get(cargoCons));
                }

            }
            else if (ACCION_INSERTAR.equals(accion)) {

                campoBloqueado = false;
                registro.getCampos().put(
                                FrmproyectosControladorEnum.FECHAREGISTRO
                                                .getValue(),
                                new Date());
                registro.getCampos().put("FECHA_ACTUALIZACION", new Date());
                registro.getCampos().put("VALOREJECUTADO", 0);
                registro.getCampos().put(valorProgramadoCons, 0);
                registro.getCampos().put("VALORSOLICITADO", 0);
                bloqueaPeriodicidad = false;
                Map<String, Object> fields = new TreeMap<>();
                fields.put(FrmproyectosControladorEnum.RESPONSABLE_RADIC
                                .getValue(),
                                SysmanConstantes.CONS_TERCERO);

                Registro reg = listaResponsableRadic
                                .getRegistroUnico(fields);
                if (reg != null) {
                    registro.getCampos().put(responsableRadicacionCons,
                                    SysmanConstantes.CONS_TERCERO);
                    nombreResponsableRadic = (String) reg.getCampos()
                                    .get(nombreCons);
                    registro.getCampos().put(sucursalRadicCons,
                                    SysmanConstantes.CONS_SUCURSAL);
                    registro.getCampos().put(dependenciaResponRadicCons,
                                    SysmanConstantes.CONS_DEPENDENCIA);
                    registro.getCampos().put(cargoResponsableRadicCons,
                                    reg.getCampos().get(cargoCons));
                }

                registro.getCampos().put("VALOREJECUTADO", 0);
                registro.getCampos().put(valorProgramadoCons, 0);
                registro.getCampos().put("VALORSOLICITADO", 0);

                registro.getCampos().put(valorAnno1Cons, 0);
                registro.getCampos().put(valorAnno2Cons, 0);
                registro.getCampos().put(valorAnno3Cons, 0);
                registro.getCampos().put(valorAnno4Cons, 0);

                registro.getCampos()
                                .put(FrmproyectosControladorEnum.RESPONSABLENACION
                                                .getValue(),
                                                SysmanConstantes.CONS_TERCERO);
                registro.getCampos()
                                .put(FrmproyectosControladorEnum.RESPONSABLEDPTO
                                                .getValue(),
                                                SysmanConstantes.CONS_TERCERO);
                registro.getCampos().put(
                                FrmproyectosControladorEnum.RESPONSABLEOTRAENTIDAD
                                                .getValue(),
                                SysmanConstantes.CONS_TERCERO);

                registro.getCampos().put(valorNacionCons, 0);
                registro.getCampos().put(valorDptoCons, 0);
                registro.getCampos().put(valorOtraEntidadCons, 0);
                nombre = "";
                cargo = "";
                registro.getCampos().put("PORCEJECUCION", 0);
                registro.getCampos().put("VIGENCIAINICIO", null);
                registro.getCampos().put("VIGENCIAFIN", null);
                aniosVigencia = null;

            }

            precargarRegistro();
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        nombreProyecto = SysmanFunciones
                        .nvl(registro.getCampos().get(nombreProyectoCons), "")
                        .toString();

        registraProy = false;
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

        falloGuardado = false;
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if ("SI".equalsIgnoreCase(strValidarNombre)
            && !validarNombreProyecto()) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2366"));
            falloGuardado = true;
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(
                        registro.getCampos().get(codigoCons).toString())) {
            int anno = SysmanFunciones.getParteFecha((Date) (registro
                            .getCampos()
                            .get(FrmproyectosControladorEnum.FECHAREGISTRO
                                            .getValue()) == null
                                                ? new Date()
                                                : registro.getCampos()
                                                                .get(FrmproyectosControladorEnum.FECHAREGISTRO
                                                                                .getValue())),
                            Calendar.YEAR);
            try {

                String[] auxCod = {
                                    "COMPANIA = ''", compania,
                                    "'' AND SUBSTR(CODIGO,1,4) = ",
                                    Integer.toString(anno)
                };
                registro.getCampos().put(codigoCons,
                                ejbSysmanUtil.generarConsecutivoConValorInicial(
                                                "PROYECTOS",
                                                SysmanFunciones.concatenar(
                                                                auxCod),
                                                GeneralParameterEnum.CODIGO
                                                                .getName(),
                                                strConsecutivoInicial));

                aceptardialogoCreaBPIM();
            }
            catch (SystemException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        if (validarProgramacion()
            && (registro.getCampos().get(valorTotalCons) == null ? 0
                : Integer.valueOf(registro.getCampos().get(valorTotalCons)
                                .toString())) < (registro.getCampos()
                                                .get(valorProgramadoCons) == null
                                                    ? 0
                                                    : Integer.valueOf(registro
                                                                    .getCampos()
                                                                    .get(valorProgramadoCons)
                                                                    .toString()))) {

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2365"));
            return false;

        }
        // Revisa si cambiï¿½ el valor total, con el fin de crear los
        // componentes predeterminados de gastos, en caso de que no
        // existan.
        if (cambioTotal) {
            crearComponentesAutomaticos(true, true);
        }
        cambioTotal = false;
        return true;
        // </CODIGO_DESARROLLADO>
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
        if ("SI".equalsIgnoreCase(strValidarNombre)
            && !validarNombreProyecto()) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2366"));
            falloGuardado = true;
            return false;
        }

        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());

        }
        return true;
        // </CODIGO_DESARROLLADO>
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

        actualizaValorTotal();

        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
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
        return true;
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable palabraProyecto
     * 
     * @return palabraProyecto
     */
    public String getPalabraProyecto() {
        return palabraProyecto;
    }

    /**
     * Asigna la variable palabraProyecto
     * 
     * @param palabraProyecto
     * Variable a asignar en palabraProyecto
     */
    public void setPalabraProyecto(String palabraProyecto) {
        this.palabraProyecto = palabraProyecto;
    }

    /**
     * Retorna la variable aniosVigencia
     * 
     * @return aniosVigencia
     */
    public String getAniosVigencia() {
        return aniosVigencia;
    }

    /**
     * Asigna la variable aniosVigencia
     * 
     * @param aniosVigencia
     * Variable a asignar en aniosVigencia
     */
    public void setAniosVigencia(String aniosVigencia) {
        this.aniosVigencia = aniosVigencia;
    }

    /**
     * Retorna la variable nombreDependencia
     * 
     * @return nombreDependencia
     */
    public String getNombreDependencia() {
        return nombreDependencia;
    }

    /**
     * Asigna la variable nombreDependencia
     * 
     * @param nombreDependencia
     * Variable a asignar en nombreDependencia
     */
    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    /**
     * Retorna la variable nombre
     * 
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna la variable nombre
     * 
     * @param nombre
     * Variable a asignar en nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna la variable cargo
     * 
     * @return cargo
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Asigna la variable cargo
     * 
     * @param cargo
     * Variable a asignar en cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Retorna la variable nombreResponsableRadic
     * 
     * @return nombreResponsableRadic
     */
    public String getNombreResponsableRadic() {
        return nombreResponsableRadic;
    }

    /**
     * Asigna la variable nombreResponsableRadic
     * 
     * @param nombreResponsableRadic
     * Variable a asignar en nombreResponsableRadic
     */
    public void setNombreResponsableRadic(String nombreResponsableRadic) {
        this.nombreResponsableRadic = nombreResponsableRadic;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna la variable varVolver
     * 
     * @return varVolver
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     * 
     * @param varVolver
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    /**
     * Retorna la variable muestraNuevo
     * 
     * @return muestraNuevo
     */
    public boolean isMuestraNuevo() {
        return muestraNuevo;
    }

    /**
     * Asigna la variable muestraNuevo
     * 
     * @param muestraNuevo
     * Variable a asignar en muestraNuevo
     */
    public void setMuestraNuevo(boolean muestraNuevo) {
        this.muestraNuevo = muestraNuevo;
    }

    /**
     * Retorna la variable muestraActualiza
     * 
     * @return muestraActualiza
     */
    public boolean isMuestraActualiza() {
        return muestraActualiza;
    }

    /**
     * Asigna la variable muestraActualiza
     * 
     * @param muestraActualiza
     * Variable a asignar en muestraActualiza
     */
    public void setMuestraActualiza(boolean muestraActualiza) {
        this.muestraActualiza = muestraActualiza;
    }

    /**
     * Retorna la variable tipoNovTecProy
     * 
     * @return tipoNovTecProy
     */
    public String getTipoNovTecProy() {
        return tipoNovTecProy;
    }

    /**
     * Asigna la variable tipoNovTecProy
     * 
     * @param tipoNovTecProy
     * Variable a asignar en tipoNovTecProy
     */
    public void setTipoNovTecProy(String tipoNovTecProy) {
        this.tipoNovTecProy = tipoNovTecProy;
    }

    /**
     * Retorna la variable cargarInfoAdicional
     * 
     * @return cargarInfoAdicional
     */
    public boolean isCargarInfoAdicional() {
        return cargarInfoAdicional;
    }

    /**
     * Asigna la variable cargarInfoAdicional
     * 
     * @param cargarInfoAdicional
     * Variable a asignar en cargarInfoAdicional
     */
    public void setCargarInfoAdicional(boolean cargarInfoAdicional) {
        this.cargarInfoAdicional = cargarInfoAdicional;
    }

    /**
     * Retorna la variable creaBPIM
     * 
     * @return creaBPIM
     */
    public boolean isCreaBPIM() {
        return creaBPIM;
    }

    /**
     * Asigna la variable creaBPIM
     * 
     * @param creaBPIM
     * Variable a asignar en creaBPIM
     */
    public void setCreaBPIM(boolean creaBPIM) {
        this.creaBPIM = creaBPIM;
    }

    /**
     * Retorna la variable agregaPalabra
     * 
     * @return agregaPalabra
     */
    public boolean isAgregaPalabra() {
        return agregaPalabra;
    }

    /**
     * Asigna la variable agregaPalabra
     * 
     * @param agregaPalabra
     * Variable a asignar en agregaPalabra
     */
    public void setAgregaPalabra(boolean agregaPalabra) {
        this.agregaPalabra = agregaPalabra;
    }

    /**
     * Retorna la variable eliminaComp
     * 
     * @return eliminaComp
     */
    public boolean isEliminaComp() {
        return eliminaComp;
    }

    /**
     * Asigna la variable eliminaComp
     * 
     * @param eliminaComp
     * Variable a asignar en eliminaComp
     */
    public void setEliminaComp(boolean eliminaComp) {
        this.eliminaComp = eliminaComp;
    }

    /**
     * Retorna la variable creaComp
     * 
     * @return creaComp
     */
    public boolean isCreaComp() {
        return creaComp;
    }

    /**
     * Asigna la variable vRegistrado
     * 
     * @param vRegistrado
     * Variable a asignar en creaComp
     */
    public void setCreaComp(boolean creaComp) {
        this.creaComp = creaComp;
    }

    /**
     * Retorna la variable bloqueaPeriodicidad
     * 
     * @return bloqueaPeriodicidad
     */
    public boolean isBloqueaPeriodicidad() {
        return bloqueaPeriodicidad;
    }

    /**
     * Asigna la variable bloqueaPeriodicidad
     * 
     * @param bloqueaPeriodicidad
     * Variable a asignar en bloqueaPeriodicidad
     */
    public void setBloqueaPeriodicidad(boolean bloqueaPeriodicidad) {
        this.bloqueaPeriodicidad = bloqueaPeriodicidad;
    }

    /**
     * Retorna la variable bloqueaBotones
     * 
     * @return bloqueaBotones
     */
    public boolean isBloqueaBotones() {
        return bloqueaBotones;
    }

    /**
     * Asigna la variable bloqueaBotones
     * 
     * @param bloqueaBotones
     * Variable a asignar en bloqueaBotones
     */
    public void setBloqueaBotones(boolean bloqueaBotones) {
        this.bloqueaBotones = bloqueaBotones;
    }

    /**
     * Retorna la variable vRegistrado
     * 
     * @return vRegistrado
     */
    public boolean isvRegistrado() {
        return vRegistrado;
    }

    /**
     * Asigna la variable vRegistrado
     * 
     * @param vRegistrado
     * Variable a asignar en vRegistrado
     */
    public void setvRegistrado(boolean vRegistrado) {
        this.vRegistrado = vRegistrado;
    }

    /**
     * Retorna la variable bloqueaCKRegistrado
     * 
     * @return bloqueaCKRegistrado
     */
    public boolean isBloqueaCKRegistrado() {
        return bloqueaCKRegistrado;
    }

    /**
     * Asigna la variable bloqueaCKRegistrado
     * 
     * @param bloqueaCKRegistrado
     * Variable a asignar en bloqueaCKRegistrado
     */
    public void setBloqueaCKRegistrado(boolean bloqueaCKRegistrado) {
        this.bloqueaCKRegistrado = bloqueaCKRegistrado;
    }

    /**
     * Retorna la variable editaProyecto
     * 
     * @return editaProyecto
     */
    public String getEditaProyecto() {
        return editaProyecto;
    }

    /**
     * Asigna la variable editaProyecto
     * 
     * @param editaProyecto
     * Variable a asignar en editaProyecto
     */
    public void setEditaProyecto(String editaProyecto) {
        this.editaProyecto = editaProyecto;
    }

    /**
     * Retorna la variable registraProy
     * 
     * @return registraProy
     */
    public boolean isRegistraProy() {
        return registraProy;
    }

    /**
     * Asigna la variable registraProy
     * 
     * @param registraProy
     * Variable a asignar en registraProy
     */
    public void setRegistraProy(boolean registraProy) {
        this.registraProy = registraProy;
    }

    /**
     * Retorna la variable indiceSubproyectonovedadestecnicas
     * 
     * @return indiceSubproyectonovedadestecnicas
     */
    public int getIndiceSubproyectonovedadestecnicas() {
        return indiceSubproyectonovedadestecnicas;
    }

    /**
     * Asigna la variable indiceSubproyectonovedadestecnicas
     * 
     * @param indiceSubproyectonovedadestecnicas
     * Variable a asignar en indiceSubproyectonovedadestecnicas
     */
    public void setIndiceSubproyectonovedadestecnicas(
        int indiceSubproyectonovedadestecnicas) {
        this.indiceSubproyectonovedadestecnicas = indiceSubproyectonovedadestecnicas;
    }

    /**
     * Retorna la variable accionActual
     * 
     * @return accionActual
     */
    public String getAccionActual() {
        return accionActual;
    }

    /**
     * Asigna la variable accionActual
     * 
     * @param accionActual
     * Variable a asignar en accionActual
     */
    public void setAccionActual(String accionActual) {
        this.accionActual = accionActual;
    }

    /**
     * Retorna la variable vigenciaActual
     * 
     * @return vigenciaActual
     */
    public int getVigenciaActual() {
        return vigenciaActual;
    }

    /**
     * Asigna la variable vigenciaActual
     * 
     * @param vigenciaActual
     * Variable a asignar en vigenciaActual
     */
    public void setVigenciaActual(int vigenciaActual) {
        this.vigenciaActual = vigenciaActual;
    }

    /**
     * Retorna la variable aux
     * 
     * @return aux
     */
    public int getAux() {
        return aux;
    }

    /**
     * Asigna la variable aux
     * 
     * @param aux
     * Variable a asignar en aux
     */
    public void setAux(int aux) {
        this.aux = aux;
    }

    /**
     * Retorna la variable palabraProy
     * 
     * @return palabraProy
     */
    public String getPalabraProy() {
        return palabraProy;
    }

    /**
     * Asigna la variable palabraProy
     * 
     * @param palabraProy
     * Variable a asignar en palabraProy
     */
    public void setPalabraProy(String palabraProy) {
        this.palabraProy = palabraProy;
    }

    /**
     * Retorna la variable campoBloqueado
     * 
     * @return campoBloqueado
     */
    public boolean isCampoBloqueado() {
        return campoBloqueado;
    }

    /**
     * Asigna la variable campoBloqueado
     * 
     * @param campoBloqueado
     * Variable a asignar en campoBloqueado
     */
    public void setCampoBloqueado(boolean campoBloqueado) {
        this.campoBloqueado = campoBloqueado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaTipoProyecto
     * 
     * @return listaTipoProyecto
     */
    public List<Registro> getListaTipoProyecto() {
        return listaTipoProyecto;
    }

    /**
     * Asigna la lista listaTipoProyecto
     * 
     * @param listaTipoProyecto
     * Variable a asignar en listaTipoProyecto
     */
    public void setListaTipoProyecto(List<Registro> listaTipoProyecto) {
        this.listaTipoProyecto = listaTipoProyecto;
    }

    /**
     * Retorna la lista listaEntidadProponente
     * 
     * @return listaEntidadProponente
     */
    public List<Registro> getListaEntidadProponente() {
        return listaEntidadProponente;
    }

    /**
     * Asigna la lista listaEntidadProponente
     * 
     * @param listaEntidadProponente
     * Variable a asignar en listaEntidadProponente
     */
    public void setListaEntidadProponente(
        List<Registro> listaEntidadProponente) {
        this.listaEntidadProponente = listaEntidadProponente;
    }

    /**
     * Retorna la lista listaInterventor
     * 
     * @return listaInterventor
     */
    public List<Registro> getListaInterventor() {
        return listaInterventor;
    }

    /**
     * Asigna la lista listaInterventor
     * 
     * @param listaInterventor
     * Variable a asignar en listaInterventor
     */
    public void setListaInterventor(List<Registro> listaInterventor) {
        this.listaInterventor = listaInterventor;
    }

    /**
     * Retorna la lista listaSectorDNP
     * 
     * @return listaSectorDNP
     */
    public List<Registro> getListaSectorDNP() {
        return listaSectorDNP;
    }

    /**
     * Asigna la lista listaSectorDNP
     * 
     * @param listaSectorDNP
     * Variable a asignar en listaSectorDNP
     */
    public void setListaSectorDNP(List<Registro> listaSectorDNP) {
        this.listaSectorDNP = listaSectorDNP;
    }

    /**
     * Retorna la lista listaSubsectorDNP
     * 
     * @return listaSubsectorDNP
     */
    public List<Registro> getListaSubsectorDNP() {
        return listaSubsectorDNP;
    }

    /**
     * Asigna la lista listaSubsectorDNP
     * 
     * @param listaSubsectorDNP
     * Variable a asignar en listaSubsectorDNP
     */
    public void setListaSubsectorDNP(List<Registro> listaSubsectorDNP) {
        this.listaSubsectorDNP = listaSubsectorDNP;
    }

    /**
     * Retorna la lista listaUnidad
     * 
     * @return listaUnidad
     */
    public List<Registro> getListaUnidad() {
        return listaUnidad;
    }

    /**
     * Asigna la lista listaUnidad
     * 
     * @param listaUnidad
     * Variable a asignar en listaUnidad
     */
    public void setListaUnidad(List<Registro> listaUnidad) {
        this.listaUnidad = listaUnidad;
    }

    /**
     * Retorna la lista listaCodigoNovedad
     * 
     * @return listaCodigoNovedad
     */
    public List<Registro> getListaCodigoNovedad() {
        return listaCodigoNovedad;
    }

    /**
     * Asigna la lista listaCodigoNovedad
     * 
     * @param listaCodigoNovedad
     * Variable a asignar en listaCodigoNovedad
     */
    public void setListaCodigoNovedad(List<Registro> listaCodigoNovedad) {
        this.listaCodigoNovedad = listaCodigoNovedad;
    }

    /**
     * Retorna la lista listaDimension
     * 
     * @return listaDimension
     */
    public List<Registro> getListaDimension() {
        return listaDimension;
    }

    /**
     * Asigna la lista listaDimension
     * 
     * @param listaDimension
     * Variable a asignar en listaDimension
     */
    public void setListaDimension(List<Registro> listaDimension) {
        this.listaDimension = listaDimension;
    }

    /**
     * @return the listaDestinoSireci
     */
    public List<Registro> getListaDestinoSireci() {
        return listaDestinoSireci;
    }

    /**
     * @param listaDestinoSireci
     * the listaDestinoSireci to set
     */
    public void setListaDestinoSireci(List<Registro> listaDestinoSireci) {
        this.listaDestinoSireci = listaDestinoSireci;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaInterventorNovedad
     * 
     * @return listaInterventorNovedad
     */
    public RegistroDataModelImpl getListaInterventorNovedad() {
        return listaInterventorNovedad;
    }

    /**
     * Asigna la lista listaInterventorNovedad
     * 
     * @param listaInterventorNovedad
     * Variable a asignar en listaInterventorNovedad
     */
    public void setListaInterventorNovedad(
        RegistroDataModelImpl listaInterventorNovedad) {
        this.listaInterventorNovedad = listaInterventorNovedad;
    }

    /**
     * Retorna la lista listaInterventorNovedad
     * 
     * @return listaInterventorNovedad
     */
    public RegistroDataModelImpl getListaInterventorNovedadE() {
        return listaInterventorNovedadE;
    }

    /**
     * Asigna la lista listaInterventorNovedad
     * 
     * @param listaInterventorNovedad
     * Variable a asignar en listaInterventorNovedad
     */
    public void setListaInterventorNovedadE(
        RegistroDataModelImpl listaInterventorNovedadE) {
        this.listaInterventorNovedadE = listaInterventorNovedadE;
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
     * Retorna la variable nombreProyecto
     * 
     * @return nombreProyecto
     */
    public String getNombreProyecto() {
        return nombreProyecto;
    }

    /**
     * Asigna la variable nombreProyecto
     * 
     * @param nombreProyecto
     * Variable a asignar en nombreProyecto
     */
    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    /**
     * Retorna la lista listaDependencia
     * 
     * @return listaDependencia
     */
    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    /**
     * Asigna la lista listaDependencia
     * 
     * @param listaDependencia
     * Variable a asignar en listaDependencia
     */
    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

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
     * Retorna la lista listaTipoDeGasto
     * 
     * @return listaTipoDeGasto
     */
    public RegistroDataModelImpl getListaTipoDeGasto() {
        return listaTipoDeGasto;
    }

    /**
     * Asigna la lista listaTipoDeGasto
     * 
     * @param listaTipoDeGasto
     * Variable a asignar en listaTipoDeGasto
     */
    public void setListaTipoDeGasto(RegistroDataModelImpl listaTipoDeGasto) {
        this.listaTipoDeGasto = listaTipoDeGasto;
    }

    /**
     * Retorna la lista listaResponsableRadic
     * 
     * @return listaResponsableRadic
     */
    public RegistroDataModelImpl getListaResponsableRadic() {
        return listaResponsableRadic;
    }

    /**
     * Asigna la lista listaResponsableRadic
     * 
     * @param listaResponsableRadic
     * Variable a asignar en listaResponsableRadic
     */
    public void setListaResponsableRadic(
        RegistroDataModelImpl listaResponsableRadic) {
        this.listaResponsableRadic = listaResponsableRadic;
    }

    /**
     * Retorna la lista listaResponsableNacion
     * 
     * @return listaResponsableNacion
     */
    public RegistroDataModelImpl getListaResponsableNacion() {
        return listaResponsableNacion;
    }

    /**
     * Asigna la lista listaResponsableNacion
     * 
     * @param listaResponsableNacion
     * Variable a asignar en listaResponsableNacion
     */
    public void setListaResponsableNacion(
        RegistroDataModelImpl listaResponsableNacion) {
        this.listaResponsableNacion = listaResponsableNacion;
    }

    /**
     * Retorna la lista listaResponsableDpto
     * 
     * @return listaResponsableDpto
     */
    public RegistroDataModelImpl getListaResponsableDpto() {
        return listaResponsableDpto;
    }

    /**
     * Asigna la lista listaResponsableDpto
     * 
     * @param listaResponsableDpto
     * Variable a asignar en listaResponsableDpto
     */
    public void setListaResponsableDpto(
        RegistroDataModelImpl listaResponsableDpto) {
        this.listaResponsableDpto = listaResponsableDpto;
    }

    /**
     * Retorna la lista listaResponsableOtraEntidad
     * 
     * @return listaResponsableOtraEntidad
     */
    public RegistroDataModelImpl getListaResponsableOtraEntidad() {
        return listaResponsableOtraEntidad;
    }

    /**
     * Asigna la lista listaResponsableOtraEntidad
     * 
     * @param listaResponsableOtraEntidad
     * Variable a asignar en listaResponsableOtraEntidad
     */
    public void setListaResponsableOtraEntidad(
        RegistroDataModelImpl listaResponsableOtraEntidad) {
        this.listaResponsableOtraEntidad = listaResponsableOtraEntidad;
    }

    /**
     * Retorna la lista listaNombreProyectoP
     * 
     * @return listaNombreProyectoP
     */
    public RegistroDataModelImpl getListaNombreProyectoP() {
        return listaNombreProyectoP;
    }

    /**
     * Asigna la lista listaNombreProyectoP
     * 
     * @param listaNombreProyectoP
     * Variable a asignar en listaNombreProyectoP
     */
    public void setListaNombreProyectoP(
        RegistroDataModelImpl listaNombreProyectoP) {
        this.listaNombreProyectoP = listaNombreProyectoP;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaSubproyectonovedadestecnicas
     * 
     * @return listaSubproyectonovedadestecnicas
     */
    public List<Registro> getListaSubproyectonovedadestecnicas() {
        return listaSubproyectonovedadestecnicas;
    }

    /**
     * Asigna la lista listaSubproyectonovedadestecnicas
     * 
     * @param listaSubproyectonovedadestecnicas
     * Variable a asignar en listaSubproyectonovedadestecnicas
     */
    public void setListaSubproyectonovedadestecnicas(
        List<Registro> listaSubproyectonovedadestecnicas) {
        this.listaSubproyectonovedadestecnicas = listaSubproyectonovedadestecnicas;
    }

    /**
     * Retorna la lista listaSubpreguntasproyectos
     * 
     * @return listaSubpreguntasproyectos
     */
    public List<Registro> getListaSubpreguntasproyectos() {
        return listaSubpreguntasproyectos;
    }

    /**
     * Asigna la lista listaSubpreguntasproyectos
     * 
     * @param listaSubpreguntasproyectos
     * Variable a asignar en listaSubpreguntasproyectos
     */
    public void setListaSubpreguntasproyectos(
        List<Registro> listaSubpreguntasproyectos) {
        this.listaSubpreguntasproyectos = listaSubpreguntasproyectos;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSubSubProyectoNovedadesTecnicas
     * 
     * @return registroSubSubProyectoNovedadesTecnicas
     */
    public Registro getRegistroSubSubProyectoNovedadesTecnicas() {
        return registroSubSubProyectoNovedadesTecnicas;
    }

    /**
     * Asigna el objeto registroSubSubProyectoNovedadesTecnicas
     * 
     * @param registroSubSubProyectoNovedadesTecnicas
     * Variable a asignar en registroSubSubProyectoNovedadesTecnicas
     */
    public void setRegistroSubSubProyectoNovedadesTecnicas(
        Registro registroSubSubProyectoNovedadesTecnicas) {
        this.registroSubSubProyectoNovedadesTecnicas = registroSubSubProyectoNovedadesTecnicas;
    }

    /**
     * Retorna el objeto registroSubSubPreguntasProyectos
     * 
     * @return registroSubSubPreguntasProyectos
     */
    public Registro getRegistroSubSubPreguntasProyectos() {
        return registroSubSubPreguntasProyectos;
    }

    /**
     * Asigna el objeto registroSubSubPreguntasProyectos
     * 
     * @param registroSubSubPreguntasProyectos
     * Variable a asignar en registroSubSubPreguntasProyectos
     */
    public void setRegistroSubSubPreguntasProyectos(
        Registro registroSubSubPreguntasProyectos) {
        this.registroSubSubPreguntasProyectos = registroSubSubPreguntasProyectos;
    }

    public String getDigitosle() {
        return digitosle;
    }

    public void setDigitosle(String digitosle) {
        this.digitosle = digitosle;
    }

    public String getDigitosprog() {
        return digitosprog;
    }

    public void setDigitosprog(String digitosprog) {
        this.digitosprog = digitosprog;
    }

    public String getDigitossubprog() {
        return digitossubprog;
    }

    public void setDigitossubprog(String digitossubprog) {
        this.digitossubprog = digitossubprog;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public String getSession() {
        return session;
    }

    public String getNit() {
        return nit;
    }

    public String getDigitossector() {
        return digitossector;
    }

    public void setDigitossector(String digitossector) {
        this.digitossector = digitossector;
    }
    
    public RegistroDataModelImpl getListaplantilla() {
        return listaplantilla;
    }

    public void setListaplantilla(RegistroDataModelImpl listaplantilla) {
        this.listaplantilla = listaplantilla;
    }    

    public String getModeloPlantilla() {
        return modeloPlantilla;
    }

    public void setModeloPlantilla(String modeloPlantilla) {
        this.modeloPlantilla = modeloPlantilla;
    }
    
    public String getStrNombreDocumento() {
        return strNombreDocumento;
    }

    public void setStrNombreDocumento(String strNombreDocumento) {
        this.strNombreDocumento = strNombreDocumento;
    }

	/**
	 * @return the bloqueaImprimir
	 */
	public boolean isBloqueaImprimir() {
		return bloqueaImprimir;
	}

	/**
	 * @param bloqueaImprimir the bloqueaImprimir to set
	 */
	public void setBloqueaImprimir(boolean bloqueaImprimir) {
		this.bloqueaImprimir = bloqueaImprimir;
	}
	
	public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}
    // </SET_GET_ADICIONALES>

}