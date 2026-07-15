package com.sysman.nomina;

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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.PersonalsControladorEnum;
import com.sysman.nomina.enums.PersonalsControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 24/06/2015
 * @modified jguerrero
 * @version 2. 26/10/2017 Se realizo el refactory de las consultas sql en el controlador. Adem�s se ajustaron los errores del sonar.
 *
 * @version 2, 03/04/2018
 * @modified mvenegas Se corrigio un error de integridad de datos referente a el establecimiento.
 * @version 3, 25/05/2018
 * @modified se agrega componentes graficos y por consiguiente metodos para seleccionar el jefe directo de un empleado
 *
 * @version 4, 10/08/2018
 * @modified se agrego validaci�n para el campo email corporativo validandolo por parametro (CORREO_CORPORATIVO) se creo el cambiarEmailCorporativo y se agrego el metodo verificarEmail.
 *
 * @version 5, 18/09/2018
 * @modified mzanguna: Se agrega par�metro MANEJA CORREO INSTITUCIONAL para que controle validaci�n de correo corporativo.
 */

@ManagedBean
@ViewScoped
public class PersonalsControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private final String usuario;

    private StreamedContent archivoDescarga;
    private String procesoNomina;
    private String anoNomina;
    private String mesNomina;
    private String periodoNomina;
    private String moduloNomina;
    private Registro registroSubHistCargos;
    private Registro registroSubProfesionesEmpleado;
    private List<Registro> listaDctoIdentidad;
    private List<Registro> listaPaisExp;
    private List<Registro> listaDptoExp;
    private List<Registro> listaCiudExp;
    private List<Registro> listaPaisNac;
    private List<Registro> listaDepartamentoNac;
    private List<Registro> listaCiudNac;
    private List<Registro> listaSexo;
    private List<Registro> listaEstadoCivil;
    private List<Registro> listaEscalafon;
    private List<Registro> listaIdDeCargo;
    private List<Registro> listaIdDeTipo;
    private List<Registro> listaDeCarrera;
    private List<Registro> listaSede;
    private List<Registro> listaBanco;
    private List<Registro> listaTipoCuenta;
    private List<Registro> listaEstadoActual;
    private List<Registro> listaFondoSindicato;
    private List<Registro> listaFondoSalud;
    private List<Registro> listaIdDelFondo;
    private List<Registro> listaFondoPensionVol;
    private List<Registro> listaFondoRiesgos;
    private List<Registro> listaFondoCesantias;
    private List<Registro> listaCajaCompensacion;
    private List<Registro> listaMedicinaPrepagada;
    private List<Registro> listaFondoAfc;
    private List<Registro> listaRegimen;

    private List<Registro> listaCausaRetiro;
    private List<Registro> listaPaisHab;
    private List<Registro> listaDepartamentoHab;
    private List<Registro> listaCiudadHab;
    private List<Registro> listaPaisLabora;
    private List<Registro> listaDepartamentoLabora;
    private List<Registro> listaCiudadLabora;
    private List<Registro> listaClasePensionado;

    private List<Registro> listaDctoIdentidadcausante;
    private List<Registro> listaHistcargos;
    private List<Registro> listaProfesionesempleado;
    private List<Registro> listaHConcepto;
    private List<Registro> listaAno;
    private List<Registro> listaServicioAsociado;
    private List<Registro> listaFondoSindical2;
    private List<Registro> listaFondoSindicato3;
    private List<Registro> listaEscalafonTemp;
    private List<Registro> listaEstablecimiento;
    private List<Registro> listaListaEmpleadoEsc;
    private List<Registro> listaTipoDocDependiente;
    private List<Registro> listaParentescoDependiente;
    private List<Registro> listaFondoACCAI;
    /**
     * Lista que carga los establecimientos educativos
     */

    private RegistroDataModelImpl listaCodigoProf;
    private RegistroDataModelImpl listaCodigoProfE;
    private RegistroDataModelImpl listaListaEncargoComis;
    private RegistroDataModelImpl listaCargoTemporal;
    private String auxiliar;
    private RegistroDataModelImpl listaNumeroDcto;
    private RegistroDataModelImpl listaIdDeCategoria;
    private RegistroDataModelImpl listaTipoActo;
    private RegistroDataModelImpl listaTipoActoE;
    private RegistroDataModelImpl listaEmpleado;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaIdCentroDeCosto;
    private RegistroDataModelImpl listaCodigoEstablecimiento;
    private RegistroDataModelImpl listaTipoActividad;
    private RegistroDataModelImpl listaTipovinculacion;
    private RegistroDataModelImpl listaSubtipocotizante;
    private RegistroDataModelImpl listaGrupocontable;
    private RegistroDataModelImpl listaNumeroPatronal;
    private RegistroDataModelImpl listaTipopensionado;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaIdJefe;
    private RegistroDataModelImpl listaFuenteRecurso;
    private RegistroDataModelImpl listaManual;
    private RegistroDataModelImpl ListaLocalidad;
    private RegistroDataModelImpl listaCIIU; 
    private RegistroDataModelImpl listatipoDiscapacidad;
    private RegistroDataModelImpl listanivelEducativo;
    private RegistroDataModelImpl listaProfesion;
    private RegistroDataModelImpl listaServidorPublico;
    private String edadEmpleado;
    private String diasPendientesVac;
    private String paisExp;
    private String dptoExp;
    private String paisNac;
    private String dptoNac;
    private String paisLab;
    private String dptoLab;
    private String paisHab;
    private String dptoHab;
    private String nombrecompleto;
    private String codEscalafon;
    private String codEscalafontem;
    private Date fechaDistrito;
    private String primerNombre;
    private String segundoNombre;
    private boolean visibleSia;
    private boolean visibleSui;
    private boolean visibleManual;
    private boolean visibleIdipron;
    private boolean visibleObsRes; // JM CC 1162
    private boolean visiblemesPen63;
    private boolean visibleSabadoHabil;
    private String tituloEtiqueta;
    private boolean pensionado;
    private boolean parManejaCorreoInst;
    private String paramCorreo;
    private boolean verDependientes = false;

    private String nombreDependencia;
    private String nombreCentroCosto;
    private String nombreUbicacion;
    private String nombreAuxiliar;
    private String nombreReferencia;
    private String nombreTipoActividad;
    private String nombreVinculacion;
    private String nombreCotizante;
    private String nombreGrupoContable;
    private String nombreTipoPensionado;
    private String nombreJefe;
    private String nombreFuente;
    private String anio;

    private boolean parManejaHV;
    private String estadoActual;
    private String idEmpleado;
    private String anoConsulta;
    private String correo;

    private String cantProrrogas;
    private String salario;
    private boolean visibleDgRetiro;
    private boolean visibleReajusteSalud;
    private boolean visibleSindicatos;

    private String condTipoVinculacion;
    private static final String DIASINTERRUPCION = "DIASINTERRUPCION";
    private static final String INGRESO_DISTRITO = "INGRESO_DISTRITO";
    private static final String INGRESODISTRITOREAL = "INGRESODISTRITOREAL";
    private static final String TIPOVINCULACION = "TIPOVINCULACION";
    private static final String FECHANCTO = "FECHANCTO";
    private static final String ID_DE_TIPO = "ID_DE_TIPO";
    private static final String NOMBRE_EMPLEADO = "nombreEmpleado";
    private static final String DESCRIPCIONPROF = "DESCRIPCIONPROF";
    private static final String CLASE_FONDO = "claseFondo";
    private static final String DEPARTAMENTO_NAC = "DEPARTAMENTO_NAC";
    private static final String DEPARTAMENTO_CED = "DEPARTAMENTO_CED";
    private static final String DEPARTAMENTO_LABORA = "DEPARTAMENTO_LABORA";
    private static final String DEPARTAMENTO_HAB = "DEPARTAMENTO_HAB";
    private static final String ESCALAFON = "ESCALAFON";
    private static final String HISTORIAL_DE_CARGOS = "HISTORIAL_DE_CARGOS";
    private static final String NUMERO_DCTO = GeneralParameterEnum.NUMERO_DCTO
                    .getName();
    private static final String SUCURSAL = GeneralParameterEnum.SUCURSAL
                    .getName();
    private static final String ID_DE_EMPLEADO = GeneralParameterEnum.ID_DE_EMPLEADO
                    .getName();
    private static final String DETALLE_PROFESIONES = "DETALLE_PROFESIONES";
    private static final String TIPOACTO = "TIPOACTO";
    private static final String ID_DE_CATEGORIA = "ID_DE_CATEGORIA";
    private static final String COMPANIAA = GeneralParameterEnum.COMPANIA
                    .getName();
    private static final String NOMBRE_EGRESADODE = "NOMBRE_EGRESADODE";
    private static final String SUCURSAL_DE = "SUCURSAL_DE";
    private static final String NIT = "NIT";
    private static final String NOMBRE_PROFESION = "NOMBRE_PROFESION";
    private static final String NOMBRE = GeneralParameterEnum.NOMBRE.getName();
    private static final String SALARIO_BASE = "SALARIO_BASE";
    private static final String NUMERO_DCTOCAUSANTE = "NUMERO_DCTOCAUSANTE";
    private static final String CODIGOPROF = "CODIGOPROF";
    private static final String ID_EMPLEADO = "idEmpleado";
    private static final String FECHATERCONTRATOANT = "FECHATERCONTRATOANT";
    private static final String ID_DE_CARGO = "ID_DE_CARGO";
    private static final String MESESPRORROGAS = "MESESPRORROGAS";
    private static final String FECHATERCONTRATO = "FECHATERCONTRATO";
    private static final String FECHA_DE_INGRESO = "FECHA_DE_INGRESO";
    private static final String NOMBRECOMPLETO = "NOMBRECOMPLETO";
    private static final String PRORROGAS = "PRORROGAS";
    private static final String PR_NOMBREEMPRESA = "PR_NOMBREEMPRESA";
    private static final String SALARIO_BASE_IBC = "SALARIO_BASE_IBC";
    private static final String DE_CARRERA = "DE_CARRERA";
    private static final String FECHA_INICIO_COMISION = "FECHA_INICIO_COMISION";
    private static final String FECHA_FINAL_COMISION = "FECHA_FINAL_COMISION";
    private static final String TOTAL_DIAS_COMISION = "TOTAL_DIAS_COMISION";
    private static final String FECHA_AFC = "FECHA_AFC";
    private static final String FECHAMEDICINA = "FECHAMEDICINA";
    private static final String FECHACAJACOMPENSACION = "FECHACAJACOMPENSACION";
    private static final String FECHAFONDOCESANTIA = "FECHAFONDOCESANTIA";
    private static final String FECHAFONDOPENSIONVOL = "FECHAFONDOPENSIONVOL";
    private static final String NIN99 = "NIN99";
    private static final String DEPENDENCIA = "DEPENDENCIA";
    private static final String ID_CENTRO_DE_COSTO = "ID_CENTRO_DE_COSTO";
    private static final String NUMERODOC = "NUMERODOC";
    private static final String FECHAACTO = "FECHAACTO";
    private static final String CODCARGO = "CODCARGO";
    private static final String SALARIOCONS = "SALARIO";
    private static final String CODSECRE = "CODSECRE";
    private static final String CODSECCION = "CODSECCION";
    private static final String TIPOFUN = "TIPOFUN";
    private static final String TIPOVINCULO = "TIPOVINCULO";
    private static final String DOCUMENTO = "DOCUMENTO";
    private static final String NUM_DOCUMENTO = "NUM_DOCUMENTO";
    private static final String FECHA_DOCUMENTO = "FECHA_DOCUMENTO";
    private static final String FECHA_DE_RETIRO = "FECHA_DE_RETIRO";
    private static final String DECLARANTES384 = "DECLARANTES384";
    private static final String FECHA_DECLARANTES384 = "FECHA_DECLARANTES384";


    private Registro registroAux;
    private Registro registroAuxP;

    private StringBuilder parametrosFuncion;
    private InputStream is;
    private ContenedorArchivo contArchivoActividadesCiiu;

    private String CIIU_riesgo;
    private String CIIU_actividad;
    private String CIIU_subactividad;
    
    private Map<String, String>  camposBorde;
    private String obligaCampos1;
    private String obligaCampos2;
    private String obligaCampos3;
    private String obligaCampos4;
    private String obligaCampos5;
    private String obligaCampos6;
    private String obligaCampos7;
    private String obligaCampos8;
    private String obligaCampos9;
    private String obligaCampos10;
    private String obligaCampos11;
    private String obligaCampos13;
    private String obligaCampos14;
    private String obligaCampos15;
    private String obligaCampos16;
    private String obligaCampos17;
    private String obligaCampos18;
    

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    @SuppressWarnings("unchecked")
    public PersonalsControlador()
    {
        super();

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null)
        {
            rid = (Map<String, Object>) parametros.get("ridR");
        }

        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        try
        {
            procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
            anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
            mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
            periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
            moduloNomina = SessionUtil.getModulo();
            numFormulario = GeneralCodigoFormaEnum.PERSONALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSubHistCargos = new Registro(new HashMap<String, Object>());
            registroSubProfesionesEmpleado = new Registro(
                            new HashMap<String, Object>());
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }
    
    
    @Override
    public void iniciarListas()
    {
	    cargarListaTipoActo();
        cargarListaTipoActoE();
        cargarListaCodigoProf();
        cargarListaCodigoProfE();
        cargarListaNumeroDcto();
        cargarListaIdDeCategoria();
        cargarListaEmpleado();
        cargarListaDctoIdentidad();
        cargarListaPaisExp();
        cargarListaPaisNac();
        cargarListaSexo();
        cargarListaEstadoCivil();
        cargarListaEscalafon();
        cargarListaHConcepto();
        cargarListaIdDeTipo();
        cargarListaDependencia();
        cargarListaIdCentroDeCosto();
        cargarListaCodigoEstablecimiento();
        cargarListaDeCarrera();
        cargarListaTipoActividad();
        cargarListaSede();
        cargarListaTipovinculacion();
        cargarListaSubtipocotizante();
        cargarListaBanco();
        cargarListaTipoCuenta();
        cargarListaEstadoActual();
        cargarListaCausaRetiro();
        cargarListaFondoSindicato();
        cargarListaFondoSindical2();
        cargarListaFondoSindicato3();
        cargarListaFondoSalud();
        cargarListaIdDelFondo();
        cargarListaFondoPensionVol();
        cargarListaFondoRiesgos();
        cargarListaFondoCesantias();
        cargarListaCajaCompensacion();
        cargarListaMedicinaPrepagada();
        cargarListaFondoAfc();
        cargarListaRegimen();
        cargarListaGrupocontable();
        cargarListaNumeroPatronal();
        cargarListaCausaRetiro();
        cargarListaPaisHab();
        cargarListaPaisLabora();
        cargarListaClasePensionado();
        cargarListaTipopensionado();
        cargarListaDctoIdentidadcausante();
        cargarListaEstadoActual();
        cargarListaAno();
        cargarListaServicioAsociado();
        cargarListaAuxiliar();
        cargarListaReferencia();
        cargarListaIdJefe();
        cargarListaFuenteRecurso();
        cargarListaEstablecimiento();
        cargarListaManual();
        cargarListaListaEmpleadoEsc();
        cargarListaListaEncargoComis();
        cargarlistaCargoTemporal();
        cargarlistaEscalafonTemp();
        cargarListaCIIU();
        cargarlistaTipoDocDependiente();
        cargarlistaParentescoDependiente();
        cargarListatipoDiscapacidad(); 
        cargarListanivelEducativo(); 
        cargarListaProfesion();
        cargarListaServidorPublico();
        cargarListaFondoACCAI();
    }

    @Override
    public void iniciarListasSub()
    {
        cargarListaHistcargos();
        cargarListaProfesionesempleado();
        cargarListaPaisNac();
        paisNac = retornarString(registro, "PAIS_NAC");
        cargarListaDepartamentoNac();
        dptoNac = retornarString(registro, DEPARTAMENTO_NAC);
        cargarListaCiudNac();
        paisExp = retornarString(registro, "PAIS_CED");
        cargarListaDptoExp();
        dptoExp = retornarString(registro, DEPARTAMENTO_CED);
        cargarListaCiudExp();
        paisLab = retornarString(registro, "PAIS_LABORA");
        cargarListaDepartamentoLabora();
        dptoLab = retornarString(registro, DEPARTAMENTO_LABORA);
        cargarListaCiudadLabora();
        paisHab = retornarString(registro, "PAIS_HAB");
        cargarListaDepartamentoHab();
        dptoHab = retornarString(registro, DEPARTAMENTO_HAB);
        cargarListaCiudadHab();
        codEscalafon = retornarString(registro, ESCALAFON);
        codEscalafontem = retornarString(registro, ESCALAFON);
        cargarListaIdDeCargo();
        cargarListaIdDeCategoria();
        cargarListaManual();

    }

    @Override
    public void iniciarListasSubNulo()
    {
        listaHistcargos = null;
        listaProfesionesempleado = null;
    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.PERSONAL;
        buscarLlave();
        asignarOrigenDatos();
        
     
        
    }

    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
    }

    public void cargarListaHistcargos()
    {

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("NUMERODOCUMENTO", registro.getCampos().get(NUMERO_DCTO));
            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            registro.getCampos().get(SUCURSAL));

            listaHistcargos = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.HISTORIAL_DE_CARGOS
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            HISTORIAL_DE_CARGOS));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaProfesionesempleado()
    {

        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(NUMERO_DCTO));

            listaProfesionesempleado = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            GenericUrlEnum.DETALLE_PROFESIONES
                                                                                                                            .getGridKey())
                                                                                            .getUrl(),
                                                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            UrlServiceCache.SYSMANDSUNIST,
                                                            DETALLE_PROFESIONES));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarlistaTipoDocDependiente()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
        	listaTipoDocDependiente = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL29469
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 209001
    }
    
    public void cargarlistaParentescoDependiente()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
        	listaParentescoDependiente = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL609001
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 209001
    }
    
    /**
     * 
     * Carga la lista listaFondoACCAI
     *
     */
    public void cargarListaFondoACCAI(){
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

    	try
    	{
    		listaFondoACCAI = RegistroConverter
    				.toListRegistro(
    						requestManager
    						.getList(
    								UrlServiceUtil.getInstance()
    								.getUrlServiceByUrlByEnumID(
    										PersonalsControladorUrlEnum.URL39173
    										.getValue())
    								.getUrl(),
    								param));
    	}
    	catch (SystemException e)
    	{
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    public void cargarListaDctoIdentidad()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaDctoIdentidad = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL29469
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 209001
    }

    public void cargarListaPaisExp()
    {

        try
        {
            listaPaisExp = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL29859
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 1001
    }

    public void cargarListaDptoExp()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisExp);

        try
        {
            listaDptoExp = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30103
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // PAIS 2005
    }

    public void cargarListaCiudExp()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisExp);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoExp);

        try
        {
            listaCiudExp = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30468
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 5001 PAIS DEPARTAMENTO
    }

    public void cargarListaPaisNac()
    {
        try
        {
            listaPaisNac = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30953
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 1001
    }

    public void cargarListaDepartamentoNac()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisNac);

        try
        {
            listaDepartamentoNac = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL31220
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // PAIS 2005
    }

    public void cargarListaCiudNac()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisNac);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoNac);

        try
        {
            listaCiudNac = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30468
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 5001 PAIS DEPARTAMENTO
    }

    public void cargarListaSexo()
    {
        try
        {
            listaSexo = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL32145
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 192001
    }

    public void cargarListaEstadoCivil()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaEstadoCivil = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL32395
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 615001
    }

    public void cargarListaEscalafon()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaEscalafon = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL32767
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 462001
    }

    public void cargarListaIdDeCargo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ESCALAFON, codEscalafon);

        try
        {
            listaIdDeCargo = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL33106
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 463002 ESCALAFON
    }

    public void cargarListaHConcepto()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaHConcepto = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL33590
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 151023
    }

    public void cargarListaIdDeTipo()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaIdDeTipo = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL34036
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 540003
    }

    public void cargarListaDependencia()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL34428
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 62038
    }

    public void cargarListaIdCentroDeCosto()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoNomina);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL34860
                                                        .getValue());

        listaIdCentroDeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 20024 ANOACTUAL
    }

    /**
     *
     * Carga la lista listaAuxiliar
     *
     */
    public void cargarListaAuxiliar()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ANIO", anoNomina);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL31715
                                                        .getValue());

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaReferencia
     *
     */
    public void cargarListaReferencia()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoNomina);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL50875
                                                        .getValue());

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaIdJefe()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL40316
                                                        .getValue());

        listaIdJefe = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.ID_DE_EMPLEADO.getName());
    }

    /**
     *
     * Carga la lista listaFuenteRecurso
     *
     */
    public void cargarListaFuenteRecurso()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL40314
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 34001
    }

    public void cargarListaCodigoEstablecimiento()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL35438
                                                        .getValue());

        listaCodigoEstablecimiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 614002
    }

    public void cargarListaDeCarrera()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaDeCarrera = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL35863
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 618001
    }

    public void cargarListaTipoActividad()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL36252
                                                        .getValue());

        listaTipoActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 632001
    }

    /**
     *
     * Carga la lista listaServicioAsociado
     *
     */
    public void cargarListaServicioAsociado()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaServicioAsociado = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL48751
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSede()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaSede = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL36525
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 633001
    }

    public void cargarListaTipovinculacion()
    {
        UrlBean urlBean;
        if (pensionado)
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PersonalsControladorUrlEnum.URL90318
                                                            .getValue());
        }
        else
        {
            urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PersonalsControladorUrlEnum.URL36859
                                                            .getValue());
        }

        listaTipovinculacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());
        // 634001
    }

    public void cargarListaSubtipocotizante()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL37152
                                                        .getValue());

        listaSubtipocotizante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 635001
    }

    public void cargarListaBanco()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaBanco = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL37438
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 459003
    }

    public void cargarListaTipoCuenta()
    {
        try
        {
            listaTipoCuenta = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL37767
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 636001
    }

    public void cargarListaCausaRetiro()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCausaRetiro = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL38050
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 465001
    }

    public void cargarListaFondoSindicato()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoSindicato = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL38412
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 637001
    }

    public void cargarListaFondoSindical2()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoSindical2 = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL38412
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 637001
    }

    public void cargarListaFondoSindicato3()
    {
        listaFondoSindicato3 = listaFondoSindical2;
    }

    /**
     *
     * Carga la lista listaEstablecimiento
     *
     */
    public void cargarListaEstablecimiento()
    {
        try
        {
            listaEstablecimiento = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL56450
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFondoSalud()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoSalud = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL38801
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 638001
    }

    public void cargarListaIdDelFondo()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaIdDelFondo = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL39173
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 640001
    }

    public void cargarListaFondoPensionVol()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoPensionVol = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL39553
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 641001

    }

    public void cargarListaFondoRiesgos()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoRiesgos = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL39931
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 642001
    }

    public void cargarListaFondoCesantias()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoCesantias = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL40315
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 643001
    }

    public void cargarListaCajaCompensacion()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCajaCompensacion = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL40709
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 644001
    }

    public void cargarListaMedicinaPrepagada()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaMedicinaPrepagada = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL41090
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 645001
    }

    public void cargarListaFondoAfc()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaFondoAfc = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL41475
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 646001
    }

    public void cargarListaRegimen()
    {

        try
        {
            listaRegimen = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL41844
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 647001
    }

    public void cargarListaGrupocontable()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL42129
                                                        .getValue());

        listaGrupocontable = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 648001
    }

    public void cargarListaNumeroPatronal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL42412
                                                        .getValue());

        listaNumeroPatronal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.SUCURSAL.getName());

        // 631001
    }

    public void cargarListaPaisHab()
    {

        try
        {
            listaPaisHab = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30953
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDepartamentoHab()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisHab);

        try
        {
            listaDepartamentoHab = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30103
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCiudadHab()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisHab);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoHab);

        try
        {
            listaCiudadHab = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30468
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPaisLabora()
    {

        try
        {
            listaPaisLabora = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30953
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDepartamentoLabora()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisLab);

        try
        {
            listaDepartamentoLabora = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30103
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCiudadLabora()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", paisLab);
        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(), dptoLab);

        try
        {
            listaCiudadLabora = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL30468
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaClasePensionado()
    {

        try
        {
            listaClasePensionado = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL45154
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 649001
    }

    public void cargarListaTipopensionado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL45445
                                                        .getValue());

        listaTipopensionado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());

        // 650001
    }

    public void cargarListaDctoIdentidadcausante()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaDctoIdentidadcausante = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL45739
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 209001
    }

    public void cargarListaEstadoActual()
    {
        try
        {
            listaEstadoActual = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL46142
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 651001
    }

    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL46374
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 4001
    }

    public void cargarListaTipoActo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL47067
                                                        .getValue());
        listaTipoActo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        TIPOACTO);

        // 652001

    }

    public void cargarListaTipoActoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL47067
                                                        .getValue());
        listaTipoActoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        TIPOACTO);
    }

    public void cargarListaCodigoProf()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL48167
                                                        .getValue());
        listaCodigoProf = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        CODIGOPROF);

        // 639001
    }

    public void cargarListaCodigoProfE()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL48167
                                                        .getValue());
        listaCodigoProfE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        CODIGOPROF);

    }

    public void cargarListaNumeroDcto()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL50529
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNumeroDcto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        NIT);
        // 14124
    }

    public void cargarListaIdDeCategoria()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL51354
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoNomina);
        param.put(ESCALAFON, codEscalafon);

        listaIdDeCategoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ID_DE_CATEGORIA);

        // 31002 ANO ESCALAFON
    }

    public void cargarListaEmpleado()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL52224
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        ID_DE_EMPLEADO);

        // 210043
    }

    public void cargarListaManual()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL40317
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registro != null
                            ? registro.getCampos().get(
                                            GeneralParameterEnum.DEPENDENCIA
                                                            .getName())
                            : "");
        param.put(GeneralParameterEnum.CARGO.getName(),
                        registro != null ? registro.getCampos().get(ID_DE_CARGO)
                            : "");

        listaManual = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NUMERO_MANUAL");
    }

    /**
     *
     * Carga la lista listaListaEmpleadoEsc
     *
     */
    public void cargarListaListaEmpleadoEsc()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaListaEmpleadoEsc = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL52525
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaListaEncargoComis()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL11111
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaListaEncargoComis = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "ID_DE_CARGO");

    }

    public void cargarlistaEscalafonTemp()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaEscalafonTemp = RegistroConverter
                            .toListRegistro(
                                            requestManager
                                                            .getList(
                                                                            UrlServiceUtil.getInstance()
                                                                                            .getUrlServiceByUrlByEnumID(
                                                                                                            PersonalsControladorUrlEnum.URL32767
                                                                                                                            .getValue())
                                                                                            .getUrl(),
                                                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarlistaCargoTemporal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL32154
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ESCALAFON.getName(), codEscalafontem);

        listaCargoTemporal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "ID_DE_CARGO");

    }

    public void cargarListaCIIU()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL1897001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCIIU = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "ID_CIIU");
    }
    /**
     * 
     * Carga la lista listatipoDiscapacidad
     *
     */
    public void cargarListatipoDiscapacidad(){
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					PersonalsControladorUrlEnum.URL1948001
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();

    	listatipoDiscapacidad = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }
    /**
     * 
     * Carga la lista listanivelEducativo
     *
     */
    public void cargarListanivelEducativo(){
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					PersonalsControladorUrlEnum.URL639007
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.TIPO.getName(), "N");

    	listanivelEducativo = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true, "CODIGOPROF");
    }
    /**
     * 
     * Carga la lista listaProfesion
     *
     */
    public void cargarListaProfesion(){

    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					PersonalsControladorUrlEnum.URL639007
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.TIPO.getName(), "P");

    	listaProfesion = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true, "CODIGOPROF");
    }
    
    /**
     * 
     * Carga la lista listaServidorPublico
     *
     */
    public void cargarListaServidorPublico(){
    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					PersonalsControladorUrlEnum.URL1949001
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();

    	listaServidorPublico = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
    }

    public void agregarRegistroSubHistcargos()
    {
        try
        {
            registroSubHistCargos.getCampos().put(COMPANIAA, compania);
            registroSubHistCargos.getCampos().put(NUMERODOC,
                            registro.getCampos().get(NUMERO_DCTO));
            registroSubHistCargos.getCampos().put(SUCURSAL,
                            registro.getCampos().get(SUCURSAL));

            registroSubHistCargos.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubHistCargos.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.HISTORIAL_DE_CARGOS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubHistCargos.getCampos());

            cargarListaHistcargos();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubHistCargos = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubHistcargos(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.HISTORIAL_DE_CARGOS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaHistcargos();
        }
    }

    public void eliminarRegSubHistcargos(Registro reg)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put("APLICACION", modulo);
        param.put("USUARIO", usuario);

        try
        {

            Registro regNivel = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PersonalsControladorUrlEnum.URL40318
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if (Integer.parseInt(SysmanFunciones
                            .nvl(regNivel.getCampos().get("NIVEL_USUARIO"), "0")
                            .toString()) == 9)
            {
                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.HISTORIAL_DE_CARGOS
                                                                .getDeleteKey());
                requestManager.delete(urlDelete.getUrl(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));

                cargarListaHistcargos();
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4247"));

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionHistcargos()
    {
        cargarListaHistcargos();
        cargarListaProfesionesempleado();
    }

    public void agregarRegistroSubProfesionesempleado()
    {
        try
        {
            registroSubProfesionesEmpleado.getCampos().put(COMPANIAA, compania);
            registroSubProfesionesEmpleado.getCampos().put(ID_DE_EMPLEADO,
                            registro.getCampos().get(ID_DE_EMPLEADO));
            registroSubProfesionesEmpleado.getCampos().put(NUMERO_DCTO,
                            registro.getCampos().get(NUMERO_DCTO));

            registroSubProfesionesEmpleado.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            registroSubProfesionesEmpleado.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DETALLE_PROFESIONES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubProfesionesEmpleado.getCampos());

            cargarListaProfesionesempleado();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSubProfesionesEmpleado = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubProfesionesempleado(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            reg.getCampos().remove("RID");
            reg.getCampos().remove("TIPO_LB");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            reg.getCampos().remove("NOMBRE_ESTABLECIMIENTO");

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DETALLE_PROFESIONES
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaProfesionesempleado();
        }
    }

    public void eliminarRegSubProfesionesempleado(Registro reg)
    {
        try
        {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.DETALLE_PROFESIONES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaProfesionesempleado();
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionProfesionesempleado()
    {
        cargarListaProfesionesempleado();
    }

    public void oprimirsedes()
    {
        // <CODIGO_DESARROLLADO>

        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.SEDES_CONTROLADOR
                                        .getCodigo()),
                        moduloNomina);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircartaprorroga()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try
        {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            // FORMATO CARTA PRORROGA se debe crear la variable para
            // cargar el reporte desde los par�metros
            reemplazar.put(ID_EMPLEADO,
                            registro.getCampos().get(ID_DE_EMPLEADO));

            DateFormat fechaLarga = DateFormat.getDateInstance(DateFormat.LONG);
            String nombreGerente = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL GERENTE",
                            SessionUtil.getModulo(), new Date(), true);

            String cargoGerente = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL GERENTE",
                            SessionUtil.getModulo(), new Date(), true);

            String ciudadExpide = ejbSysmanUtil.consultarParametro(compania,
                            "CIUDAD DONDE SE EXPIDE CERTIFICACION LABORAL",
                            SessionUtil.getModulo(), new Date(), true);

            String ciudadFechaExpide = ejbSysmanUtil.consultarParametro(
                            compania,
                            "CIUDAD DONDE SE EXPIDE CERTIFICACION LABORAL",
                            SessionUtil.getModulo(), new Date(), true);

            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            Date fechaTerContrato = SysmanFunciones
                            .sumarRestarDiasFecha((Date) registro.getCampos()
                                            .get(FECHATERCONTRATOANT), 1);
            SimpleDateFormat formatoDia = new SimpleDateFormat("d");
            SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM");
            SimpleDateFormat formatoAno = new SimpleDateFormat("yyyy");
            NumberFormat formatoMoneda = NumberFormat
                            .getCurrencyInstance(Locale.US);
            String diaActual = formatoDia.format(new Date());
            String mesActual = formatoMes.format(new Date());
            String anoActual = formatoAno.format(new Date());
            String nombreCargo = ejbNominaCero.getNombreCargo(compania,
                            retornarString(registro, ID_DE_CARGO),
                            retornarString(registro, ESCALAFON));

            String salarioCargo = String.valueOf(ejbNominaCero.getSalarioCargo(
                            compania,
                            retornarString(registro, ESCALAFON),
                            retornarString(registro, ID_DE_CATEGORIA),
                            Integer.parseInt(SysmanFunciones
                                            .nvl(registro.getCampos()
                                                            .get("ANO"), "0")
                                            .toString())));

            String formatoSalario = formatoMoneda
                            .format(Double.parseDouble(salarioCargo));
            String ltSalarioCargo = SysmanFunciones
                            .moneda(Double.parseDouble(salarioCargo), 0);
            String mesesProrroga = SysmanFunciones
                            .moneda(Double.parseDouble((String) registro
                                            .getCampos().get(MESESPRORROGAS)),
                                            1);

            String cuerpoInforme = idioma.getString("TB_TB3749");

            cuerpoInforme = cuerpoInforme
                            .replace("s$mesesProrroga$s", mesesProrroga)
                            .replace("s$mesesNumero$s",
                                            retornarString(registro,
                                                            MESESPRORROGAS))
                            .replace("s$fechaTercontrato$s",
                                            fechaLarga.format(fechaTerContrato))
                            .replace("s$fechaTercontratoFinal$s", fechaLarga
                                            .format(registro.getCampos().get(
                                                            FECHATERCONTRATO)))
                            .replace("s$nombreCargo$s", nombreCargo)
                            .replace("s$salarioCargo$s", ltSalarioCargo)
                            .replace("s$formatoSalario$s", formatoSalario);

            String jefeRH = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL JEFE DE RECURSOS HUMANOS",
                            SessionUtil.getModulo(), new Date(), true);

            String cargoRH = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO JEFE RECURSOS HUMANOS",
                            SessionUtil.getModulo(), new Date(), true);

            String codigoFormato = ejbSysmanUtil.consultarParametro(compania,
                            "CODIGO FORMATO CARTA PRORROGA",
                            SessionUtil.getModulo(), new Date(), true);

            String elaboradoPor = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN PROYECTA PRORROGA",
                            SessionUtil.getModulo(), new Date(), true);

            String revisadoPor = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN REVISA PRORROGA",
                            SessionUtil.getModulo(), new Date(), true);

            String aprobadoPor = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE QUIEN APRUEBA PRORROGA",
                            SessionUtil.getModulo(), new Date(), true);

            String textoNotificacion = idioma.getString("TB_TB3752");

            textoNotificacion = textoNotificacion
                            .replace("s$ciudadExpide$s", ciudadExpide)
                            .replace("s$diaActual$s", SysmanFunciones
                                            .moneda(Double.parseDouble(
                                                            diaActual), 1)
                                            .toLowerCase())
                            .replace("s$diaActualL$s", diaActual)
                            .replace("s$mesActual$s", mesActual)
                            .replace("s$anoActual$s", anoActual)
                            .replace("s$numeroResol$s", retornarString(registro,
                                            "NUMERO_RESOLUCION"))
                            .replace("s$fechaResol$s", fechaLarga
                                            .format(registro.getCampos().get(
                                                            "FECHA_RESOLUCION")))
                            .replace("s$nombreCompleto$s", retornarString(
                                            registro, NOMBRECOMPLETO));

            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
            parametros.put("PR_CIUDAD_DONDE_SE_EXPIDE_CERTIFICACION_LABORAL",
                            ciudadFechaExpide);
            parametros.put(PR_NOMBREEMPRESA, nombreEmpresa);
            parametros.put("PR_CUERPOINFORME", cuerpoInforme);
            parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS", jefeRH);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", cargoRH);
            parametros.put("PR_CODIGO_FORMATO_CARTA_PRORROGA", codigoFormato);
            parametros.put("PR_NOMBRE_DE_QUIEN_PROYECTA_PRORROGA",
                            elaboradoPor);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_PRORROGA", revisadoPor);
            parametros.put("PR_NOMBRE_DE_QUIEN_APRUEBA_PRORROGA", aprobadoPor);
            parametros.put("PR_NOTIFICACION", textoNotificacion);

            Reporteador.resuelveConsulta("000017CARTAPRORROGACONTRATO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000017CARTAPRORROGACONTRATO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirFSALUD()
    {
        // <CODIGO_DESARROLLADO>

        redireccionarFondo("EPS");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFPENSION()
    {
        // <CODIGO_DESARROLLADO>

        redireccionarFondo("AFP");
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFPENSIONVOL()
    {
        // <CODIGO_DESARROLLADO>
        redireccionarFondo("APV");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFRIESGOS()
    {
        // <CODIGO_DESARROLLADO>
        redireccionarFondo("ARL");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFCESANTIAS()
    {
        // <CODIGO_DESARROLLADO>
        redireccionarFondo("CES");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFCOMPENSACION()
    {

        // <CODIGO_DESARROLLADO>

        redireccionarFondo("CCF");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirFMEDICINA()
    {
        // <CODIGO_DESARROLLADO>

        redireccionarFondo("FMP");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPENSIONAFC()
    {
        // <CODIGO_DESARROLLADO>

        redireccionarFondo("AFC");

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirListaChequeo()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            archivoDescarga = null;
            String reporte = "001619PERSONALCHEQUEO";
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("compania", compania);
            reemplazar.put("idEmpleado",
                            registro.getCampos().get("ID_DE_EMPLEADO"));

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirsupervivencias()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        try
        {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            reemplazar.put("rangoInicial", SysmanFunciones.formatearFecha(
                            SysmanFunciones.convertirAFecha(SysmanFunciones
                                            .concatenar("01/", mesNomina, "/",
                                                            anoNomina))));

            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            parametros.put(PR_NOMBREEMPRESA, nombreEmpresa);
            parametros.put("PR_MESTRABAJO", mesNomina);
            parametros.put("PR_ANOTRABAJO", anoNomina);

            Reporteador.resuelveConsulta("000026Listadosupervivenciasmes",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000026Listadosupervivenciasmes", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException | SystemException
                        | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // <CODIGO_DESARROLLADO>
    }

    public void oprimircopiaremeplado()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirKardex()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirReteTipos()
    {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "rid", ID_EMPLEADO, NOMBRE_EMPLEADO };
        Object[] valores = { css, retornarString(registro, ID_DE_EMPLEADO),
                             retornarString(registro, NOMBRECOMPLETO) };
        SessionUtil.cargarModalDatosFlashCerrar(
                        String.valueOf(GeneralCodigoFormaEnum.RETENCIONPERSONALS_CONTROLADOR
                                        .getCodigo()),
                        moduloNomina, campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSuip()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirLLAMADOS()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirconsultarvacaciones()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(ID_EMPLEADO, registro.getCampos().get(ID_DE_EMPLEADO));
        String sql = Reporteador.resuelveConsulta("800026VacacionesEmpleado",
                        Integer.parseInt(moduloNomina),
                        reemplazar);

        try
        {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97, "Vacaciones");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCargaCIIU()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(ID_EMPLEADO, registro.getCampos().get(ID_DE_EMPLEADO));
        String sql = Reporteador.resuelveConsulta("800549ActividadesCIIUPersonal",
                        Integer.parseInt(moduloNomina),
                        reemplazar);

        try
        {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97, "ActividadesEconomicasCIIU");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariosedes()
    {
        cargarListaSede();

    }

    public void oprimirACTUALIZAHISTORIALCARGOS()
    {
        try
        {
            // <CODIGO_DESARROLLADO>

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PersonalsControladorUrlEnum.URL61183
                                                            .getValue());

            Date fechaActoAux = (Date) registro.getCampos()
                            .get(FECHA_DE_INGRESO);

            Date fechaDocAux = (Date) registro.getCampos()
                            .get(FECHA_DE_INGRESO);

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(NUMERODOC, registro.getCampos().get(NUMERO_DCTO));
            fields.put(SUCURSAL, registro.getCampos().get(SUCURSAL));
            fields.put(TIPOACTO, "1");
            fields.put(FECHAACTO, fechaActoAux);
            fields.put(CODCARGO, registro.getCampos().get(ID_DE_CARGO));
            fields.put(SALARIOCONS, registro.getCampos().get(SALARIO_BASE_IBC));
            fields.put(CODSECRE, registro.getCampos().get(DEPENDENCIA));
            fields.put(CODSECCION,
                            registro.getCampos().get(ID_CENTRO_DE_COSTO));
            fields.put(TIPOFUN, "E");
            fields.put(TIPOVINCULO, registro.getCampos().get(DE_CARRERA));
            fields.put(DOCUMENTO, "DEC");
            fields.put(NUM_DOCUMENTO, "0");
            fields.put(FECHA_DOCUMENTO, fechaDocAux);

            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(fields);
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

            cargarRegistro(css, accion, registro.getIndice());
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirProyectos()
    {
        // <CODIGO_DESARROLLADO>

        String[] campos = { ID_EMPLEADO, NOMBRE_EMPLEADO };
        Object[] valores = { retornarString(registro, ID_DE_EMPLEADO),
                             retornarString(registro, NOMBRECOMPLETO) };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.PROYECTOSPORPERSONAS_CONTROLADOR
                                        .getCodigo()),
                        moduloNomina,
                        campos, valores);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdAbrirHV()
    {
        // heredado del bean base
    }

    public void oprimirINCREMENTARIBC()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            ejbNominaCero.incrementarIbc(compania,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally
        {

            cargarRegistro(css, accion, registro.getIndice());
        }

        // </CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirconsulta()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimiractualizartodasfotos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirconsultarvacacionestodos()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        String sql = Reporteador.resuelveConsulta("800027VacacionesTodos",
                        Integer.parseInt(moduloNomina), null);

        try
        {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97, "VacacionesTodos");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircontratosporvencer()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        try
        {

            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String nombreEmpresa = ejbNominaCero.getDatoEmpresa(compania, 0);

            Date fechaInicial = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anoNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), true,
                            false);

            Date fechaFinal = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anoNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), false,
                            false);

            reemplazar.put("fechaInicio", SysmanFunciones
                            .formatearFechaCadena(fechaInicial, "DD/MM/YYYY"));
            reemplazar.put("fechaFinal", SysmanFunciones
                            .formatearFechaCadena(fechaFinal, "DD/MM/YYYY"));

            parametros.put(PR_NOMBREEMPRESA, nombreEmpresa);

            Reporteador.resuelveConsulta("000003PersonalVenceContrato",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000003PersonalVenceContrato", parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdIndCorreccion()
    {
        // <CODIGO_DESARROLLADO>

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalsControladorUrlEnum.URL43663
                                                        .getValue());
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put("IND_CORRECCION", "-1");

        fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        Parameter parameter = new Parameter();
        parameter.setFields(fields);

        try
        {
            int rta = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(), parameter);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2641")
                            .replace("#$rta#$", String.valueOf(rta)));

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCuotasPartes()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", css);
        parametros.put(ID_EMPLEADO, retornarString(registro, ID_DE_EMPLEADO));
        parametros.put(NOMBRE_EMPLEADO,
                        retornarString(registro, NOMBRECOMPLETO));

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.CUOTASPARTESDETALLES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton ProgramacionVacaciones en la vista
     *
     *
     */
    public void oprimirProgramacionVacaciones()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("rid", css);
        Direccionador direccionador = new Direccionador();
        direccionador
                        .setNumForm(String.valueOf(
                                        GeneralCodigoFormaEnum.SUBFORMDETALLEVACACIONES_CONTROLADOR
                                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador, moduloNomina);

        // </CODIGO_DESARROLLADO>
    }

    public void requeryEscalafon()
    {
        // <CODIGO_DESARROLLADO>
        codEscalafon = retornarString(registro, ESCALAFON);

        cargarListaIdDeCargo();
        cargarListaIdDeCategoria();

    }

    public void cambiarEscalafon()
    {

        codEscalafon = retornarString(registro, ESCALAFON);
        cargarListaIdDeCargo();
        cargarListaIdDeCategoria();
        registro.getCampos().put(ID_DE_CATEGORIA, null);

    }

    public void cambiarEscalafonTemp()
    {

        codEscalafontem = retornarString(registro, "ESCALAFON_TEMP");
        // cargarlistaEscalafonTemp();
        cargarlistaCargoTemporal();
        registro.getCampos().put("NOMBRECARGOTEMP", null);

    }

    public void cambiarBanco()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("CUENTA", "0");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProrrogas()
    {
        // <CODIGO_DESARROLLADO
        if (!validaciones())
        {
            return;
        }

        int prorrogaAct = Integer.parseInt(
                        registro.getCampos().get(PRORROGAS).toString());

        int sigProrroga = Integer.parseInt(
                        registro.getCampos().get(PRORROGAS).toString())
            + 1;
        if (prorrogaAct > sigProrroga)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2648"));
            registro.getCampos().put(PRORROGAS, cantProrrogas);
            return;
        }

        if (prorrogaAct > 1)
        {
            validarProrrogaMayor1(prorrogaAct);
        }
        else if (prorrogaAct == 1)
        {
            validarProrrogaIgual1();
        }
        cargarListaHistcargos();

    }

    /**
     * Metodo ejecutado al cambiar el control EmailCorporativo
     *
     * Se valida el parametro de entrada debe ser igual al correo que se ingreso
     *
     */

    // lbotia

    public void cambiarEmailCorporativo()
    {
        // <CODIGO_DESARROLLADO>

        if (verificarEmail())
        {

            if (paramCorreo.equals(correo) || !parManejaCorreoInst)
            {
                registro.getCampos().put(
                                PersonalsControladorEnum.EMAIL_CORPORATIVO
                                                .getValue(),
                                registro.getCampos().get(
                                                PersonalsControladorEnum.EMAIL_CORPORATIVO
                                                                .getValue()));
            }

            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4188"));
                registro.getCampos()
                                .put(PersonalsControladorEnum.EMAIL_CORPORATIVO
                                                .getValue(), "");

            }
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarManual()
    {
        if (visibleManual && SysmanFunciones.validarCampoVacio(
                        registro.getCampos(), "NUMERO_MANUAL"))
        {
            JsfUtil.agregarMensajeErrorDialogo(idioma.getString("TB_TB4156"));
            return false;
        }
        else
        {
            return true;
        }
    }

    // lbotia
    private boolean verificarEmail()
    {

        if (SysmanFunciones.validarEmail(
                        registro.getCampos().get(
                                        PersonalsControladorEnum.EMAIL_CORPORATIVO
                                                        .getValue())
                                        .toString()))
        {
            String cadena = registro.getCampos()
                            .get(PersonalsControladorEnum.EMAIL_CORPORATIVO
                                            .getValue())
                            .toString();

            int c = cadena.indexOf("@");

            correo = cadena.substring(c);

            return true;
        }
        else
        {
            JsfUtil.agregarMensajeAlerta("No es un email valido");
            return false;
        }
    }

    private void validarProrrogaIgual1()
    {
        try
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2646").replace("#$meses#$",
                                            String.valueOf(registro.getCampos()
                                                            .get(MESESPRORROGAS))));
            registro.getCampos().put(FECHATERCONTRATOANT,
                            registro.getCampos().get(FECHATERCONTRATO));

            registro.getCampos().put(FECHATERCONTRATO,
                            SysmanFunciones.sumarRestarMesesFecha(
                                            (Date) registro.getCampos().get(
                                                            FECHATERCONTRATO),
                                            Integer.parseInt((String) registro
                                                            .getCampos()
                                                            .get(MESESPRORROGAS))));

            Date fechaActo = (Date) (registro.getCampos()
                            .get(FECHA_DE_INGRESO) == null ? new Date()
                                : registro.getCampos().get(FECHA_DE_INGRESO));
            Date fechaDcto = (Date) (registro.getCampos()
                            .get(FECHATERCONTRATO) == null ? new Date()
                                : registro.getCampos().get(FECHATERCONTRATO));

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PersonalsControladorUrlEnum.URL61183
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(NUMERODOC, registro.getCampos().get(NUMERO_DCTO));
            fields.put(SUCURSAL, registro.getCampos().get(SUCURSAL));
            fields.put(TIPOACTO, "16");
            fields.put(FECHAACTO, fechaActo);
            fields.put(CODCARGO, registro.getCampos().get(ID_DE_CARGO));
            fields.put(SALARIOCONS, registro.getCampos().get(SALARIO_BASE_IBC));
            fields.put(CODSECRE, registro.getCampos().get(DEPENDENCIA));
            fields.put(CODSECCION,
                            registro.getCampos().get(ID_CENTRO_DE_COSTO));
            fields.put(TIPOFUN, "E");
            fields.put(TIPOVINCULO, registro.getCampos().get(DE_CARRERA));
            fields.put(DOCUMENTO, "DEC");
            fields.put(NUM_DOCUMENTO, "0");
            fields.put(FECHA_DOCUMENTO, fechaDcto);

            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(fields);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void validarProrrogaMayor1(int cProrrogas)
    {
        try
        {
            if (cProrrogas > 3)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2645"));
            }
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2646").replace("#$meses#$",
                                            String.valueOf(registro.getCampos()
                                                            .get(MESESPRORROGAS))));
            registro.getCampos().put(FECHATERCONTRATOANT,
                            registro.getCampos().get(FECHATERCONTRATO));
            registro.getCampos().put(FECHATERCONTRATO,
                            SysmanFunciones.sumarRestarMesesFecha(
                                            (Date) registro.getCampos().get(
                                                            FECHATERCONTRATO),
                                            Integer.parseInt((String) registro
                                                            .getCampos()
                                                            .get(MESESPRORROGAS))));

            Date fechaActo = (Date) (registro.getCampos()
                            .get(FECHATERCONTRATOANT) == null ? new Date()
                                : registro.getCampos()
                                                .get(FECHATERCONTRATOANT));
            Date fechaDcto = (Date) (registro.getCampos()
                            .get(FECHATERCONTRATO) == null ? new Date()
                                : registro.getCampos().get(FECHATERCONTRATO));

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PersonalsControladorUrlEnum.URL61183
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(NUMERODOC, registro.getCampos().get(NUMERO_DCTO));
            fields.put(SUCURSAL, registro.getCampos().get(SUCURSAL));
            fields.put(TIPOACTO, "16");
            fields.put(FECHAACTO, fechaActo);
            fields.put(CODCARGO, registro.getCampos().get(ID_DE_CARGO));
            fields.put(SALARIOCONS, registro.getCampos().get(SALARIO_BASE_IBC));
            fields.put(CODSECRE, registro.getCampos().get(DEPENDENCIA));
            fields.put(CODSECCION,
                            registro.getCampos().get(ID_CENTRO_DE_COSTO));
            fields.put(TIPOFUN, "E");
            fields.put(TIPOVINCULO, registro.getCampos().get(DE_CARRERA));
            fields.put(DOCUMENTO, "DEC");
            fields.put(NUM_DOCUMENTO, "0");
            fields.put(FECHA_DOCUMENTO, fechaDcto);

            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(fields);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarPaisExp()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(DEPARTAMENTO_CED, null);
        registro.getCampos().put("EXPEDIDA", null);
        paisExp = retornarString(registro, "PAIS_CED");
        cargarListaDptoExp();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDptoExp()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("EXPEDIDA", null);
        dptoExp = retornarString(registro, DEPARTAMENTO_CED);
        cargarListaCiudExp();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPaisNac()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(DEPARTAMENTO_NAC, null);
        registro.getCampos().put("CIUDAD_NAC", null);
        paisNac = retornarString(registro, "PAIS_NAC");
        cargarListaDepartamentoNac();
    }

    public void cambiarDepartamentoNac()
    {
        // <CODIGO_DESARROLLADO>
        dptoNac = retornarString(registro, DEPARTAMENTO_NAC);
        registro.getCampos().put("CIUDAD_NAC", null);
        cargarListaCiudNac();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaDeIngreso()
    {
        // <CODIGO_DESARROLLADO>

        if (registro.getCampos().get(INGRESODISTRITOREAL) == null)
        {
            registro.getCampos().put(INGRESODISTRITOREAL,
                            registro.getCampos().get(FECHA_DE_INGRESO));
            fechaDistrito = registro.getCampos().get(FECHA_DE_INGRESO) != null
                ? SysmanFunciones.sumarRestarDiasFecha(
                                (Date) registro.getCampos()
                                                .get(FECHA_DE_INGRESO),
                                Integer.parseInt(
                                                SysmanFunciones.nvl(registro
                                                                .getCampos()
                                                                .get(DIASINTERRUPCION),
                                                                "0")
                                                                .toString()))
                : null;
            registro.getCampos().put(INGRESO_DISTRITO, fechaDistrito);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDiasInterrupcion()
    {
        fechaDistrito = registro.getCampos().get(INGRESODISTRITOREAL) != null
            ? SysmanFunciones.sumarRestarDiasFecha(
                            (Date) registro.getCampos()
                                            .get(INGRESODISTRITOREAL),
                            Integer.parseInt(
                                            SysmanFunciones.nvl(registro
                                                            .getCampos()
                                                            .get(DIASINTERRUPCION),
                                                            "0").toString()))
            : null;
        registro.getCampos().put(INGRESO_DISTRITO, fechaDistrito);
    }

    /**
     * Metodo ejecutado al cambiar el control FechaDeRetiro
     *
     *
     */
    public void cambiarFechaDeRetiro()
    {
        // <CODIGO_DESARROLLADO>
        visibleDgRetiro = true;

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarIngresoDistritoReal()
    {
        fechaDistrito = registro.getCampos().get(INGRESODISTRITOREAL) != null
            ? SysmanFunciones.sumarRestarDiasFecha(
                            (Date) registro.getCampos()
                                            .get(INGRESODISTRITOREAL),
                            Integer.parseInt(
                                            SysmanFunciones.nvl(registro
                                                            .getCampos()
                                                            .get(DIASINTERRUPCION),
                                                            "0").toString()))
            : null;
        registro.getCampos().put(INGRESO_DISTRITO, fechaDistrito);
    }

    public void cambiarIdDeCargo()
    {
        cargarListaManual();
    }

    public void cambiarIdDeTipo()
    {
        // <CODIGO_DESARROLLADO>
        if ("04".equals(retornarString(registro, ID_DE_TIPO)))
        {
            registro.getCampos().put(TIPOVINCULACION, "12");
        }
        else if ("05".equals(retornarString(registro, ID_DE_TIPO)))
        {
            registro.getCampos().put(TIPOVINCULACION, "19");
        }
        else
        {
            registro.getCampos().put(TIPOVINCULACION, "1");

        }

        if ("99".equals(retornarString(registro, ID_DE_TIPO)))
        {
            tituloEtiqueta = idioma.getString("TB_TB3755");
            pensionado = true;
        }
        else
        {
            pensionado = false;
            if (visibleIdipron)
            {
                tituloEtiqueta = idioma.getString("TB_TB3905_2");
            }
            else
            {
                tituloEtiqueta = idioma.getString("TB_TB3905");
            }
        }

        cargarListaTipovinculacion();
        cargarListaManual();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDependientes384()
    {

        ajustarFecha("FECHA_DEPENDIENTES384", "DEPENDIENTES384");
        
        boolean checkDep = (boolean) registro.getCampos().get("DEPENDIENTES384");
        if (checkDep)
        {
        	verDependientes = true;
        	//registro.getCampos().put(fecha, new Date());
        
        }
        else {
       		registro.getCampos().put("DCTO_IDENTIDAD_DEPEN", null);
      		registro.getCampos().put("NUMERO_IDEN_DEPEN", null);
       		registro.getCampos().put("NOMBRES_DEPEN", null);
       		registro.getCampos().put("PARENTESCO_DEPEN", null);
       		verDependientes = false;
        }

    }

    public void cambiarDeclaranteS384()
    {
        ajustarFecha(FECHA_DECLARANTES384, DECLARANTES384);

    }

    public void cambiarReteMinima()
    {
        if ((Boolean) registro.getCampos().get("RETE_MINIMA"))
        {
            registro.getCampos().put(DECLARANTES384, true);
            registro.getCampos().put(FECHA_DECLARANTES384, new Date());
            registro.getCampos().put("FECHA_RETE_MINIMA", new Date());
        }
        else
        {
            registro.getCampos().put(DECLARANTES384, false);
            registro.getCampos().put(FECHA_DECLARANTES384, null);
            registro.getCampos().put("FECHA_RETE_MINIMA", null);
        }
    }

    public void cambiarFondoSalud()
    {
        if (registro.getCampos().get("FECHAFONDOSALUD") == null)
        {
            registro.getCampos().put("FECHAFONDOSALUD",
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
    }

    public void cambiarIdDelFondo()
    {
        if (registro.getCampos().get("FECHAFONDOPENSION") == null)
        {
            registro.getCampos().put("FECHAFONDOPENSION",
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
    }

    public void cambiarFondoPensionVol()
    {
        if (registro.getCampos().get(FECHAFONDOPENSIONVOL) == null
            && !registro.getCampos().get("FONDO_PENSION_VOL").equals(NIN99))
        {
            registro.getCampos().put(FECHAFONDOPENSIONVOL,
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
        else if (registro.getCampos().get("FONDO_PENSION_VOL").equals(NIN99))
        {
            registro.getCampos().put(FECHAFONDOPENSIONVOL, null);
        }
    }

    public void cambiarFondoRiesgos()
    {
        if (registro.getCampos().get("FECHAFONDORIESGOS") == null)
        {
            registro.getCampos().put("FECHAFONDORIESGOS",
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
    }

    public void cambiarFondoCesantias()
    {

        if (registro.getCampos().get(FECHAFONDOCESANTIA) == null
            && !registro.getCampos().get("FONDO_CESANTIAS").equals(NIN99))
        {
            registro.getCampos().put(FECHAFONDOCESANTIA,
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
        else if (registro.getCampos().get("FONDO_CESANTIAS").equals(NIN99))
        {
            registro.getCampos().put(FECHAFONDOCESANTIA, null);
        }
    }

    public void cambiarCajaCompensacion()
    {
        if (registro.getCampos().get(FECHACAJACOMPENSACION) == null
            && !registro.getCampos().get("CAJA_COMPENSACION").equals(NIN99))
        {
            registro.getCampos().put(FECHACAJACOMPENSACION,
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
        else if (registro.getCampos().get("CAJA_COMPENSACION").equals(NIN99))
        {
            registro.getCampos().put(FECHACAJACOMPENSACION, null);
        }
    }

    public void cambiarMedicinaPrepagada()
    {
        if (registro.getCampos().get(FECHAMEDICINA) == null
            && !registro.getCampos().get("MEDICINA_PREPAGADA")
                            .equals(NIN99))
        {
            registro.getCampos().put(FECHAMEDICINA,
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
        else if (registro.getCampos().get("MEDICINA_PREPAGADA").equals(NIN99))
        {
            registro.getCampos().put(FECHAMEDICINA, null);
        }
    }

    public void cambiarFondoAfc()
    {
        if (registro.getCampos().get(FECHA_AFC) == null
            && !registro.getCampos().get("FONDO_AFC").equals(NIN99))
        {
            registro.getCampos().put(FECHA_AFC,
                            registro.getCampos().get(FECHA_DE_INGRESO));
        }
        else if (registro.getCampos().get("FONDO_AFC").equals(NIN99))
        {
            registro.getCampos().put(FECHA_AFC, null);
        }
    }

    public void cambiarPaisHab()
    {

        registro.getCampos().put(DEPARTAMENTO_HAB, null);
        registro.getCampos().put("CIUDAD_HAB", null);
        paisHab = retornarString(registro, "PAIS_HAB");
        cargarListaDepartamentoHab();

    }

    public void cambiarDepartamentoHab()
    {

        registro.getCampos().put("CIUDAD_HAB", null);
        dptoHab = retornarString(registro, DEPARTAMENTO_HAB);
        cargarListaCiudadHab();

    }

    public void cambiarPaisLabora()
    {

        registro.getCampos().put(DEPARTAMENTO_LABORA, null);
        registro.getCampos().put("CIUDAD_LABORA", null);
        paisLab = retornarString(registro, "PAIS_LABORA");
        cargarListaDepartamentoLabora();

    }

    public void cambiarDepartamentoLabora()
    {
        registro.getCampos().put("CIUDAD_LABORA", null);
        dptoLab = retornarString(registro, DEPARTAMENTO_LABORA);
        cargarListaCiudadLabora();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaSuperViviencia()
    {
        Date fechaVen = registro.getCampos().get("FECHA_SUPERVIVIENCIA") != null
            ? SysmanFunciones.sumarRestarDiasFecha((Date) registro.getCampos()
                            .get("FECHA_SUPERVIVIENCIA"), 89)
            : null;
        registro.getCampos().put("FECHA_VEN_SUPERVIVIENCIA", fechaVen);
    }

    public void cambiarFechaNcto()
    {
        // <CODIGO_DESARROLLADO>
        edadEmpleado = registro.getCampos().get(FECHANCTO) != null
            ? SysmanFunciones.calcularEdad(
                            (Date) registro.getCampos().get(FECHANCTO))
            : "0";
        // <CODIGO_DESARROLLADO>
    }

    public void cambiarEstadoActual()
    {
        if (!"6".equals(retornarString(registro, "ESTADO_ACTUAL")))
        {
            registro.getCampos().put(FECHA_INICIO_COMISION, null);
            registro.getCampos().put(FECHA_FINAL_COMISION, null);
            registro.getCampos().put(TOTAL_DIAS_COMISION, "0");
        }

        if ("3".equals(retornarString(registro, "ESTADO_ACTUAL"))
            && SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            FECHA_DE_RETIRO))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3754"));
        }

    }

    public void cambiarFechaInicioComision()
    {
        String diasComision;

        try
        {
            if (registro.getCampos().get(FECHA_INICIO_COMISION) != null
                && registro.getCampos().get(FECHA_FINAL_COMISION) != null)
            {

                diasComision = String.valueOf(
                                ejbSysmanUtil.retornarDiasComerciales(
                                                (Date) registro.getCampos().get(
                                                                FECHA_INICIO_COMISION),
                                                (Date) registro.getCampos().get(
                                                                FECHA_FINAL_COMISION)));

                registro.getCampos().put(TOTAL_DIAS_COMISION, diasComision);
            }
            else
            {
                registro.getCampos().put(TOTAL_DIAS_COMISION, "0");
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarFechaFinalComision()
    {
        String diasComision;

        try
        {
            if (registro.getCampos().get(FECHA_INICIO_COMISION) != null
                && registro.getCampos().get(FECHA_FINAL_COMISION) != null)
            {
                diasComision = String.valueOf(
                                ejbSysmanUtil.retornarDiasComerciales(
                                                (Date) registro.getCampos().get(
                                                                FECHA_INICIO_COMISION),
                                                (Date) registro.getCampos().get(
                                                                FECHA_FINAL_COMISION)));

                registro.getCampos().put(TOTAL_DIAS_COMISION, diasComision);
            }
            else
            {
                registro.getCampos().put(TOTAL_DIAS_COMISION, "0");
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarFechaIniProgvac()
    {

        try
        {
            Date fechaFinal = ejbSysmanUtil.retornarFechaMasDiasHabiles(
                            compania,
                            (Date) registro.getCampos().get("FECHAINIPROGVAC"),
                            15, false);

            registro.getCampos().put("FECHAFINPROGVAC", fechaFinal);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarTipoActoC(int rowNum)
    {
        // heredado del bean base
    }

    public void cambiarTipoC(int rowNum)
    {
        // heredado del bean base
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCIIU
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCIIU(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_CIIU", registroAux.getCampos().get("ID_CIIU"));
        CIIU_riesgo = registroAux.getCampos().get("CLASE_RIESGO").toString();
        setCIIU_actividad(registroAux.getCampos().get("COD_CIIU").toString());
        setCIIU_subactividad(registroAux.getCampos().get("COD_SUBACTIVIDAD").toString());
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
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("COD_AUXILIAR", registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreAuxiliar = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilaListaEncargoComis(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_DE_CARGOESCALERA", registroAux.getCampos().get("ID_DE_CARGO"));
        registro.getCampos().put("NOMBRECARGO", registroAux.getCampos().get("NOMBRE_DEL_CARGO"));

    }

    public void seleccionarFilaCargoTemporal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ID_DE_CARGO_TEMPORAL", registroAux.getCampos().get("ID_DE_CARGO"));
        registro.getCampos().put("NOMBRECARGOTEMP", registroAux.getCampos().get("NOMBRE_DEL_CARGO"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaReferencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaReferencia(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("REFERENCIA", registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreReferencia = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaIdJefe
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdJefe(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("JEFE_DIRECTO",
                        registroAux.getCampos().get("ID_DE_EMPLEADO"));
        nombreJefe = (String) registroAux.getCampos().get("NOMBRE");
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaFuenteRecurso
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFuenteRecurso(SelectEvent event)
    {
        {
            Registro registroAux = (Registro) event.getObject();
            registro.getCampos().put("FUENTE_DE_RECURSO",
                            registroAux.getCampos().get("CODIGO"));
            nombreFuente = (String) registroAux.getCampos().get("NOMBRE");

        }
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipopensionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipopensionado(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("TIPOPENSIONADO", registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreTipoPensionado = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaNumeroPatronal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumeroPatronal(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("NUMEROPATRONAL",
                        registroAux1.getCampos().get("SUCURSAL"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaGrupocontable
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaGrupocontable(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("GRUPOCONTABLE", registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreGrupoContable = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipovinculacion
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipovinculacion(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("TIPOVINCULACION",
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreVinculacion = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaSubtipocotizante
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaSubtipocotizante(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("SUBTIPOCOTIZANTE",
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreCotizante = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaTipoActividad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoActividad(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("TIPOACTIVIDAD", registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreTipoActividad = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    public void seleccionarFilaManual(SelectEvent event)
    {
        Registro registroAuxil = (Registro) event.getObject();
        registro.getCampos().put("NUMERO_MANUAL",
                        registroAuxil.getCampos().get("NUMERO_MANUAL"));
        registro.getCampos().put("VERSION_MANUAL",
                        registroAuxil.getCampos().get("VERSION"));

        // registro.getCampos().put("NUMERO_MANUAL", arg1)
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoEstablecimiento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoEstablecimiento(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_ESTABLECIMIENTO",
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreUbicacion = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaIdCentroDeCosto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaIdCentroDeCosto(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("ID_CENTRO_DE_COSTO",
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        nombreCentroCosto = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaDependencia
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDependencia(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put("DEPENDENCIA", registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        nombreDependencia = registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaManual();
    }

    public void seleccionarFilaTipoActo(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registroSubHistCargos.getCampos().put(TIPOACTO,
                        registroAux1.getCampos().get(TIPOACTO));
        registroSubHistCargos.getCampos().put(CODCARGO,
                        registro.getCampos().get(ID_DE_CARGO));
        registroSubHistCargos.getCampos().put(SALARIOCONS,
                        registro.getCampos().get(SALARIO_BASE_IBC));
        registroSubHistCargos.getCampos().put(FECHAACTO, new Date());
        registroSubHistCargos.getCampos().put(CODSECRE,
                        registro.getCampos().get(DEPENDENCIA));
        registroSubHistCargos.getCampos().put(CODSECCION,
                        registro.getCampos().get(ID_CENTRO_DE_COSTO));
        registroSubHistCargos.getCampos().put(TIPOVINCULO,
                        registro.getCampos().get(DE_CARRERA));
    }

    public void seleccionarFilaTipoActoE(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        auxiliar = (String) registroAux1.getCampos().get(TIPOACTO);
    }

    public void seleccionarFilaCodigoProf(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registroSubProfesionesEmpleado.getCampos().put(CODIGOPROF,
                        registroAux1.getCampos().get(CODIGOPROF));
        registroSubProfesionesEmpleado.getCampos().put(NOMBRE_PROFESION,
                        registroAux1.getCampos().get(NOMBRE_PROFESION));
        registroSubProfesionesEmpleado.getCampos().put(DESCRIPCIONPROF,
                        registroAux1.getCampos().get(DESCRIPCIONPROF));
        registroSubProfesionesEmpleado.getCampos().put("TIPO",
                        registroAux1.getCampos().get("TIPO"));
    }

    public void seleccionarFilaCodigoProfE(SelectEvent event)
    {
        registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, CODIGOPROF);
        registroSubProfesionesEmpleado.getCampos().put(NOMBRE_PROFESION,
                        registroAux.getCampos().get(NOMBRE_PROFESION));
        registroSubProfesionesEmpleado.getCampos().put(DESCRIPCIONPROF,
                        registroAux.getCampos().get(DESCRIPCIONPROF));
        registroSubProfesionesEmpleado.getCampos().put("TIPO",
                        registroAux.getCampos().get("TIPO"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaListaEmpleadoEsc
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaListaEmpleadoEsc(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("NOMBRECOMPLETO", registroAux.getCampos().get("ID_DE_EMPLEADO"));
    }

    public void cambiarCodigoProfC(int rowNum)
    {

        listaProfesionesempleado.get(rowNum).getCampos().put(NOMBRE_PROFESION,
                        registroAux.getCampos().get(NOMBRE_PROFESION));
        listaProfesionesempleado.get(rowNum).getCampos().put(DESCRIPCIONPROF,
                        registroAux.getCampos().get(DESCRIPCIONPROF));
        listaProfesionesempleado.get(rowNum).getCampos().put("TIPO",
                        registroAux.getCampos().get("TIPO"));

    }

    public void cambiarEgreadoDeC(int rowNum)
    {

        listaProfesionesempleado.get(rowNum).getCampos().put("EGRESADO_DE",
                        registroAuxP.getCampos().get(NIT));
        listaProfesionesempleado.get(rowNum).getCampos().put(SUCURSAL_DE,
                        registroAuxP.getCampos().get(SUCURSAL));
        listaProfesionesempleado.get(rowNum).getCampos().put(NOMBRE_EGRESADODE,
                        registroAuxP.getCampos().get(NOMBRE));

    }

    public void seleccionarFilaEgreadoDe(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registroSubProfesionesEmpleado.getCampos().put("EGRESADO_DE",
                        registroAux1.getCampos().get(NIT));
        registroSubProfesionesEmpleado.getCampos().put(SUCURSAL_DE,
                        registroAux1.getCampos().get(SUCURSAL));
        registroSubProfesionesEmpleado.getCampos().put(NOMBRE_EGRESADODE,
                        registroAux1.getCampos().get(NOMBRE));
    }

    public void seleccionarFilaEgreadoDeE(SelectEvent event)
    {
        registroAuxP = (Registro) event.getObject();
        auxiliar = retornarString(registroAuxP, NIT);
        registroSubProfesionesEmpleado.getCampos().put(SUCURSAL_DE,
                        registroAuxP.getCampos().get(SUCURSAL));
        registroSubProfesionesEmpleado.getCampos().put(NOMBRE_EGRESADODE,
                        registroAuxP.getCampos().get(NOMBRE));
    }

    public void seleccionarFilaNumeroDcto(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put(NUMERO_DCTO,
                        registroAux1.getCampos().get(NIT));
        registro.getCampos().put(SUCURSAL,
                        registroAux1.getCampos().get(SUCURSAL));
        registro.getCampos().put("APELLIDO1",
                        registroAux1.getCampos().get("APELLIDO1"));
        registro.getCampos().put("APELLIDO2",
                        registroAux1.getCampos().get("APELLIDO2"));
        registro.getCampos().put("DCTO_IDENTIDAD",
                        registroAux1.getCampos().get("TIPOID"));
        primerNombre = SysmanFunciones
                        .nvl(registroAux1.getCampos().get("NOMBRE1"), "")
                        .toString();
        segundoNombre = SysmanFunciones
                        .nvl(registroAux1.getCampos().get("NOMBRE2"), "")
                        .toString();
        if ("".equals(segundoNombre))
        {
            registro.getCampos().put("NOMBRES", primerNombre);
        }
        else
        {
            registro.getCampos().put("NOMBRES", SysmanFunciones
                            .concatenar(primerNombre, " ", segundoNombre));
        }
    }

    public void seleccionarFilaIdDeCategoria(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put(ID_DE_CATEGORIA,
                        registroAux1.getCampos().get(ID_DE_CATEGORIA));
        registro.getCampos().put(SALARIO_BASE_IBC,
                        registroAux1.getCampos().get(SALARIO_BASE));
        salario = retornarString(registroAux1, SALARIO_BASE);
    }

    public void seleccionarFilaEmpleado(SelectEvent event)
    {
        Registro registroAux1 = (Registro) event.getObject();
        idEmpleado = retornarString(registroAux1, ID_DE_EMPLEADO);
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoDiscapacidad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoDiscapacidad(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("TIPO_DISCAPACIDAD", registroAux.getCampos().get("CODIGO"));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listanivelEducativo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilanivelEducativo(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("NIVEL_EDUCATIVO", registroAux.getCampos().get("CODIGOPROF"));
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaProfesion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProfesion(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("PROFESION", registroAux.getCampos().get("CODIGOPROF"));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaServidorPublico
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaServidorPublico(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("SERVIDOR_PUBLICO", registroAux.getCampos().get("CODIGO"));
    }

    public void aceptarCONSULTABT()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try
        {
        	//JM CC 1553 05/06/2025 
        	String consulta = (String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"FORMATO PARA REPORTE ESTADO ACTUAL DE PERSONAL", modulo, new Date(), true),"800028EmpleadosEstado"); 
        	
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("estadoActual", estadoActual);
            String sql = Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(moduloNomina),
                            reemplazar);

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL97, "ConsultaAbt");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void aceptarCopiarEmpleado()
    {
        // <CODIGO_DESARROLLADO>

        try
        {
            ejbNominaUno.putCopiaPersona(compania,
                            Integer.parseInt(idEmpleado), Integer.parseInt(anoNomina));
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4357"));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // <CODIGO_DESARROLLADO>
    }

    public void aceptarKardex()
    {

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();

            // <CODIGO_DESARROLLADO>
            String nombreEmpleado;
            String condicionPivot = ejbNominaUno.getPivotKardexNomina(compania,
                            Integer.parseInt(anoConsulta),
                            Integer.parseInt(SysmanFunciones.nvl(
                                            registro.getCampos().get(
                                                            ID_DE_EMPLEADO),
                                            "0").toString()),
                            Integer.parseInt(SysmanFunciones.nvl(
                                            registro.getCampos().get(
                                                            ID_DE_EMPLEADO),
                                            "0").toString()),
                            1,
                            1, true);

            if (!SysmanFunciones.validarVariableVacio(condicionPivot))
            {
                nombreEmpleado = (String) registro.getCampos()
                                .get(NOMBRECOMPLETO);
                if (nombreEmpleado.length() > 30)
                {
                    nombreEmpleado = nombreEmpleado.substring(0, 30);
                }
                reemplazar.put(ID_EMPLEADO,
                                registro.getCampos().get(ID_DE_EMPLEADO));
                reemplazar.put(NOMBRE_EMPLEADO,
                                nombreEmpleado.trim().replace(" ", "_"));
                reemplazar.put("anoConsulta", anoConsulta);
                reemplazar.put("condicionPivot", condicionPivot);

                String sql = Reporteador.resuelveConsulta(
                                "800029KardexEmpleado",
                                Integer.parseInt(moduloNomina),
                                reemplazar);

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL97, "Kardex");

            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2948")
                                .replace("s$ano$s", anoConsulta));
            }

        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo Retiro en la vista
     *
     *
     */
    public void aceptarRetiro()
    {
        // <CODIGO_DESARROLLADO>
        visibleDgRetiro = false;

        try
        {
            Date fechaActo;
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            FECHATERCONTRATOANT))
            {
                fechaActo = (Date) registro.getCampos()
                                .get(FECHATERCONTRATOANT);
            }
            else
            {
                fechaActo = SysmanFunciones.sumarRestarDiasFecha((Date) registro
                                .getCampos().get(FECHA_DE_RETIRO), -1);
            }

            Date fechaDcto = SysmanFunciones.sumarRestarDiasFecha(
                            (Date) registro.getCampos().get(FECHA_DE_RETIRO),
                            -1);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PersonalsControladorUrlEnum.URL47617
                                                            .getValue());

            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(NUMERODOC, registro.getCampos().get(NUMERO_DCTO));
            fields.put(TIPOACTO, "5");
            fields.put(FECHAACTO, fechaActo);
            fields.put(SUCURSAL, registro.getCampos().get(SUCURSAL));

            fields.put(CODCARGO, registro.getCampos().get(ID_DE_CARGO));
            fields.put(SALARIOCONS, registro.getCampos().get(SALARIO_BASE_IBC));
            fields.put(CODSECRE, registro.getCampos().get(DEPENDENCIA));
            fields.put(CODSECCION,
                            registro.getCampos().get(ID_CENTRO_DE_COSTO));
            fields.put(TIPOFUN, "E");
            fields.put(TIPOVINCULO, registro.getCampos().get(DE_CARRERA));
            fields.put(DOCUMENTO, "DEC");
            fields.put(NUM_DOCUMENTO, "0");
            fields.put(FECHA_DOCUMENTO, fechaDcto);

            fields.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            fields.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            Parameter parameter = new Parameter();

            parameter.setFields(fields);

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

            agregarRegistroNuevo(false);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarListaHistcargos();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton FACCAI
     * en la vista
     *
     *
     */
    public void oprimirFACCAI() {
    	//<CODIGO_DESARROLLADO>
    	redireccionarFondo("AFP");
    	//</CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        try
        {
            visibleSui = "SI".equals(ejbSysmanUtil.consultarParametro(compania,
                            "EMPRESA DE SERVICIOS PUBLICOS",
                            moduloNomina, new Date(), true)) ? true : false;

            visibleManual = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA MANUAL DE FUNCIONES EN PERSONAL",
                            moduloNomina, new Date(), true)) ? true : false;

            paramCorreo = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CORREO_CORPORATIVO", moduloNomina,
                                            new Date(), true),
                            " ").toLowerCase();

            parManejaCorreoInst = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania, "MANEJA CORREO INSTITUCIONAL",
                            moduloNomina, new Date(), true)) ? true : false;

            visibleReajusteSalud = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "APLICA REAJUSTE EN SALUD PENSIONADOS",
                            moduloNomina, new Date(), true)) ? true : false;

            visibleIdipron = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "CAMPOS PERSONAL IDIPRON",
                            moduloNomina, new Date(), true)) ? true : false;

            visiblemesPen63 = "SI".equals(ejbSysmanUtil.consultarParametro(
                            compania,
                            "GENERAR PLANO PENSIONADOS CAMPO 63",
                            moduloNomina, new Date(), true)) ? true : false;

            visibleSindicatos = "NO".equals(SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "CALCULAR SINDICATO CON MULTIPLE APORTE",
                                            moduloNomina, new Date(), true),
                            "NO")) ? true : false;
            
            visibleSabadoHabil = "SI".equals(SysmanFunciones.nvlStr(
                    ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA SABADO HABIL POR EMPLEADO",
                            moduloNomina, new Date(), true), "NO")) ? true : false;
            
            visibleObsRes = "SI".equals(ejbSysmanUtil.consultarParametro(
                    compania,
                    "MANEJA CAMPO OBSERVACION EN DATOS DE RESIDENCIA DE PERSONAL",
                    moduloNomina, new Date(), true)) ? true : false; //JM CC 1162
            
            visibleSia = visibleSui;
            

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void cargarRegistro()
    {

        precargarRegistro();

        CIIU_riesgo = "";
        CIIU_actividad = "";
        CIIU_subactividad = "";

        try
        {
            parManejaHV = "SI".equalsIgnoreCase(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA HOJAS DE VIDA",
                                            SessionUtil.getModulo(), new Date(),
                                            true), "NO"));

            primerNombre = segundoNombre = nombreDependencia = nombreCentroCosto = nombreUbicacion = nombreAuxiliar = nombreReferencia = nombreTipoActividad = nombreVinculacion = nombreCotizante = nombreGrupoContable = nombreTipoPensionado = null;
            nombreJefe = null;
            if ("i".equals(accion))
            {

                registro.getCampos().put(DIASINTERRUPCION, "0");
                registro.getCampos().put("ANO", anoNomina);
                registro.getCampos().put(TIPOVINCULACION, "1");
                registro.getCampos().put("TIPORET", "01");
                registro.getCampos().put("REDONDEAR", "0");
                registro.getCampos().put("SINDICATO", "0");
                registro.getCampos().put("PRIMA_TECNICA", "0");
                registro.getCampos().put("CUPOSALUD", "0");
                registro.getCampos().put("PROCESORETENCION", "1");
                registro.getCampos().put("EMBARGADO", "0");
                registro.getCampos().put("REGIMEN", "2");
                registro.getCampos().put("VALORXCANCELAR", "0");
                registro.getCampos().put("MESADA_PENSIONAL", "10");
                registro.getCampos().put("VR_PENSION_AFP", "0");
                registro.getCampos().put("VR_PENSION_EMPRESA", "01");
                registro.getCampos().put("PORC_SINDICATO", "0");
                registro.getCampos().put("PROPIETARIO", "0");
                registro.getCampos().put(SALARIO_BASE_IBC, "0");
                registro.getCampos().put("CUPOSALUD_ANTERIOR", "0");
                registro.getCampos().put("CLASE_PENSIONADO", "N");
                registro.getCampos().put("TIPO_PENSIONADO", "P");
                registro.getCampos().put("TIPO_SALARIO", "F");
                registro.getCampos().put("PORCRETEFUENTE", "0");
                registro.getCampos().put("SITUADO_S", "0");
                registro.getCampos().put("SITUADO_P", "0");
                registro.getCampos().put("SITUADO_R", "0");
                registro.getCampos().put("SITUADO_ISS", "0");
                registro.getCampos().put("SUBTIPOCOTIZANTE", "0");
                registro.getCampos().put("EXTRANJERO_OBLIGADO", "0");
                registro.getCampos().put("COLOMBIANO_RESIDENTE", "0");
                registro.getCampos().put("TIPOPENSIONADO", "0");
                registro.getCampos().put("PENSIONCOMPARTIDA", "0");
                registro.getCampos().put("INDNOPATRONOSALUD", "0");
                registro.getCampos().put("INDNOPATRONOPENSION", "0");
                registro.getCampos().put("VALOR_CONTRATO", "0");
                registro.getCampos().put("RETE_1470", "0");
                registro.getCampos().put("SITUADO_S", "0");
                registro.getCampos().put(PRORROGAS, "0");
                registro.getCampos().put(MESESPRORROGAS, "0");
                registro.getCampos().put("DEPENDIENTES384", "0");
                registro.getCampos().put(DECLARANTES384, "0");
                registro.getCampos().put("ACTA_POSESION", "0");
                registro.getCampos().put("IND_CORRECCION", "0");
                registro.getCampos().put("EXONERADO_APORTES", "0");
                registro.getCampos().put("RETE_MINIMA", "0");
                registro.getCampos().put("TIENE_SUBSIDIO", "0");
                registro.getCampos().put("MANEJA_POLIZA", "0");
                registro.getCampos().put(NUMERO_DCTOCAUSANTE,
                                "999999999999999999");
                registro.getCampos().put("NIVELSIIF", "Nivel Administrativo");
                registro.getCampos().put("GRUPOCONTABLE", "A");
                registro.getCampos().put("CUENTA", "0");
                cantProrrogas = "0";
                salario = "0";
                diasPendientesVac = "0";
                

            }
            else if ("m".equals(accion) || "v".equals(accion))
            {
                nombrecompleto = SysmanFunciones.concatenar(
                                registro.getCampos().get(
                                                GeneralParameterEnum.NOMBRES
                                                                .getName())
                                                .toString(),
                                " ",
                                registro.getCampos().get("APELLIDO1")
                                                .toString(),
                                " ",
                                registro.getCampos().get("APELLIDO2")
                                                .toString());

                String[] aux = registro.getCampos()
                                .get(GeneralParameterEnum.NOMBRES.getName())
                                .toString().split(" ");
                primerNombre = aux[0];
                if (aux.length > 1)
                {
                    segundoNombre = aux[1];
                }

                pensionado = "99".equals(retornarString(registro, ID_DE_TIPO))
                    ? true
                    : false;

                if ("99".equals(retornarString(registro, ID_DE_TIPO)))
                {
                    tituloEtiqueta = idioma.getString("TB_TB3755");
                }
                else
                {
                    if (visibleIdipron)
                    {
                        tituloEtiqueta = idioma.getString("TB_TB3905_2");
                    }
                    else
                    {
                        tituloEtiqueta = idioma.getString("TB_TB3905");
                    }
                }
                cargarListaTipovinculacion();
                nombreDependencia = retornarString(registro,
                                "NOMBREDEPENDENCIA");
                nombreCentroCosto = retornarString(registro, "NOMBRECENTRO");
                nombreUbicacion = retornarString(registro,
                                "NOMBREESTABLECIMIENTO");
                nombreAuxiliar = retornarString(registro, "NOMBREAUXILIAR");
                nombreReferencia = retornarString(registro, "NOMBREREFRENCIA");
                nombreTipoActividad = retornarString(registro,
                                "NOMBRETIPO_ACTIVIDAD");
                nombreVinculacion = retornarString(registro,
                                "NOMBRETIPO_VINCULACION");
                nombreCotizante = retornarString(registro,
                                "NOMBRESUB_TIPOCOTIZANTE");
                nombreGrupoContable = retornarString(registro,
                                "NOMBREGRUPOCONTABLE");
                nombreTipoPensionado = retornarString(registro,
                                "NOMBRETIPOPENSIONADO");
                nombreJefe = retornarString(registro, "NOMJEFEDIRECTO");

                if (registro.getCampos().get("ID_CIIU") != null)
                {
                    CIIU_riesgo = retornarString(registro, "ID_CIIU").substring(0, 1);
                    CIIU_actividad = retornarString(registro, "ID_CIIU").substring(1, 5);
                    CIIU_subactividad = retornarString(registro, "ID_CIIU").substring(5, 7);
                }

                /*cargarListaTipovinculacion();*/
                if (registro.getCampos().get(ID_DE_CATEGORIA) != null)
                {
                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put("ID_DE_CATEGORIA",
                                    registro.getCampos().get(ID_DE_CATEGORIA));

                    Registro regCate = listaIdDeCategoria
                                    .getRegistroUnico(parametros);
                    salario = retornarString(regCate, SALARIO_BASE);

                }
                cantProrrogas = retornarString(registro, PRORROGAS);

                if (registro.getCampos().get(FECHANCTO) != null)
                {
                    edadEmpleado = SysmanFunciones.calcularEdad(
                                    (Date) registro.getCampos().get(FECHANCTO));
                }
                else
                {
                    edadEmpleado = "";
                }

                diasPendientesVac = String.valueOf(
                                ejbNominaCero.diasPendientesVacaciones(compania,
                                                Integer.parseInt(SysmanFunciones
                                                                .nvl(registro.getCampos()
                                                                                .get(ID_DE_EMPLEADO),
                                                                                "0")
                                                                .toString()),
                                                new Date()));
                
                
                boolean checkDep = (boolean) registro.getCampos().get("DEPENDIENTES384");
                if (checkDep)
                {
                	verDependientes = true;
                }
                else {
               		verDependientes = false;
                }


            }
            
	
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
    	if(ValidacionCamposObligatorios()) {
    		JsfUtil.agregarMensajeAlerta("Por favor, verifique que todos los campos requeridos están completos en cada pestaña del formulario.");
        	return false;
    	}
        try
        {
            String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "''");
            double codigoEmpleado = ejbSysmanUtil.generarSiguienteConsecutivo(
                            tabla, criterio, "ID_DE_EMPLEADO");

            registro.getCampos().put(COMPANIAA, compania);
            registro.getCampos().put(ID_DE_EMPLEADO, codigoEmpleado);
        }
        catch (SystemException ex)
        {
            Logger.getLogger(PersonalsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
    	
    	if(ValidacionCamposObligatorios()) {
    		JsfUtil.agregarMensajeAlerta("Por favor, verifique que todos los campos requeridos están completos en cada pestaña del formulario.");
        	return false;
    	}
        if (validarManual())
        {
            if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            NUMERO_DCTOCAUSANTE))
            {
                registro.getCampos().put(NUMERO_DCTOCAUSANTE,
                                SysmanConstantes.CONS_TERCERO);
            }
            if (registro.getCampos().get("SEDE") == null)
            {
                registro.getCampos().remove("SEDE");
            }

            if (accion.equals(ACCION_MODIFICAR))
            {
                registro.getCampos().remove("NOMBREDEPENDENCIA");
                registro.getCampos().remove("NOMBRECENTRO");
                registro.getCampos().remove("NOMBREESTABLECIMIENTO");
                registro.getCampos().remove("NOMBREAUXILIAR");
                registro.getCampos().remove("NOMBREREFRENCIA");
                registro.getCampos().remove("NOMBRETIPO_ACTIVIDAD");
                registro.getCampos().remove("NOMBRETIPO_VINCULACION");
                registro.getCampos().remove("NOMBRESUB_TIPOCOTIZANTE");
                registro.getCampos().remove("NOMBRE_DEL_CARGO");
                registro.getCampos().remove("NOMBREGRUPOCONTABLE");
                registro.getCampos().remove("NOMBRETIPOPENSIONADO");
                registro.getCampos().remove("NOMJEFEDIRECTO");
                registro.getCampos().remove("NOMBRECARGO");
                registro.getCampos().remove("NOMBRECARGOTEMP");
            }

            return true;

        }
        else
        {
            return false;
        }

    }
    
    public boolean ValidacionCamposObligatorios() {
    	boolean rta = false;
    	Map<String, String> campos = null;
    	// validamos si hay campos obligatorios
    	campos = validadorCampos();
    	  if (campos != null && !campos.isEmpty()) {
    	
    		  rta = cambiaBordeCampos(campos);
    	  }
    	//cambiamos bordes
    	 return rta;
    }
    
    private boolean cambiaBordeCampos(Map<String, String> campos) {
    	boolean rta = false;
   	 if (campos!= null) {
	    	for (Map.Entry<String, String> entry : campos.entrySet()) {
	            String campo = entry.getKey();
	            String variable = entry.getValue();
	            if (campo ==  "PROGRAMA_JOVEN" || campo ==  "DISCAPACIDAD") {
	            	if (SysmanFunciones.validarCampoVacioB(registro.getCampos(), campo) ) {
		            	
	            		asignarEstilo(variable, "false");
		                
		            }else {
		            	 asignarEstilo(variable, "true");
			                rta = true;
		            }	
	            }else {
	            
		            if (SysmanFunciones.validarCampoVacio(registro.getCampos(), campo) ) {
		            	
		                asignarEstilo(variable, "#FF0000 solid 1px");
		                rta = true;
		                
		            }else {
		            	asignarEstilo(variable, "#000000 solid 1px");
		            }
	            }
	        }
    	}
   	return rta; 		
	}
    
	private  Map<String, String>  validadorCampos() {
		Map<String, String> validadorCampos = null;
	
		try {
			if("SI".equals(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"HABILITAR CAMPOS OBLIGATORIOS EN ARCHIVO PERSONAL", modulo, new Date(), true),"NO")))
				{
					camposBorde = new HashMap<>();
			        camposBorde.put("FECHANCTO", "obligaCampos1");
			        camposBorde.put("PAIS_NAC", "obligaCampos2");
			        camposBorde.put("DEPARTAMENTO_NAC", "obligaCampos3");	
			        camposBorde.put("CIUDAD_NAC", "obligaCampos4");
			        camposBorde.put("ESTADO_CIVIL", "obligaCampos5");
			        camposBorde.put("FECHA_DE_INGRESO", "obligaCampos6");	
			        camposBorde.put("ESCALAFON", "obligaCampos7");
			        camposBorde.put("SERVIDOR_PUBLICO", "obligaCampos9");	
			        camposBorde.put("PROGRAMA_JOVEN", "obligaCampos10");
			        camposBorde.put("N_HIJOS", "obligaCampos11");
			        camposBorde.put("DISCAPACIDAD", "obligaCampos18");
			        camposBorde.put("NIVEL_EDUCATIVO", "obligaCampos13");
			        camposBorde.put("PROFESION", "obligaCampos14");
			        camposBorde.put("PAIS_LABORA", "obligaCampos15");
			        camposBorde.put("DEPARTAMENTO_LABORA", "obligaCampos16");
			        camposBorde.put("CIUDAD_LABORA", "obligaCampos17");
			        camposBorde.put("TIPO_DISCAPACIDAD", "obligaCampos8");
			        validadorCampos= camposBorde;
				}
		}
		 catch (SystemException  e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	
    	
        return validadorCampos;
	}

	  
	   
	   
	   private void asignarEstilo(String variable, String estilo) {
	        switch (variable) {
	            case "obligaCampos1": obligaCampos1 = estilo; break;
	            case "obligaCampos2": obligaCampos2 = estilo; break;
	            case "obligaCampos3": obligaCampos3 = estilo; break;
	            case "obligaCampos4": obligaCampos4 = estilo; break;
	            case "obligaCampos5": obligaCampos5 = estilo; break;
	            case "obligaCampos6": obligaCampos6 = estilo; break;
	            case "obligaCampos7": obligaCampos7 = estilo; break;
	            case "obligaCampos8": obligaCampos8 = estilo; break;
	            case "obligaCampos9": obligaCampos9 = estilo; break;
	            case "obligaCampos10": obligaCampos10 = estilo; break;
	            case "obligaCampos11": obligaCampos11 = estilo; break;
	            case "obligaCampos13": obligaCampos13 = estilo; break;
	            case "obligaCampos14": obligaCampos14 = estilo; break;
	            case "obligaCampos15": obligaCampos15 = estilo; break;
	            case "obligaCampos16": obligaCampos16 = estilo; break;
	            case "obligaCampos17": obligaCampos17 = estilo; break;
	            case "obligaCampos18": obligaCampos18 = estilo; break;
	           
	            
	        }
	   }

    
    

    @Override
    public boolean actualizarDespues()
    {
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        return true;
    }

    public Registro getRegistroSubHistCargos()
    {
        return registroSubHistCargos;
    }

    public void setRegistroSubHistCargos(Registro registroSubHistCargos)
    {
        this.registroSubHistCargos = registroSubHistCargos;
    }

    public Registro getRegistroSubProfesionesEmpleado()
    {
        return registroSubProfesionesEmpleado;
    }

    public void setRegistroSubProfesionesEmpleado(
        Registro registroSubProfesionesEmpleado)
    {
        this.registroSubProfesionesEmpleado = registroSubProfesionesEmpleado;
    }

    public List<Registro> getListaDctoIdentidad()
    {
        return listaDctoIdentidad;
    }

    public void setListaDctoIdentidad(List<Registro> listaDctoIdentidad)
    {
        this.listaDctoIdentidad = listaDctoIdentidad;
    }

    public List<Registro> getListaPaisExp()
    {
        return listaPaisExp;
    }

    public void setListaPaisExp(List<Registro> listaPaisExp)
    {
        this.listaPaisExp = listaPaisExp;
    }

    public List<Registro> getListaDptoExp()
    {
        return listaDptoExp;
    }

    public void setListaDptoExp(List<Registro> listaDptoExp)
    {
        this.listaDptoExp = listaDptoExp;
    }

    public List<Registro> getListaCiudExp()
    {
        return listaCiudExp;
    }

    public void setListaCiudExp(List<Registro> listaCiudExp)
    {
        this.listaCiudExp = listaCiudExp;
    }

    public List<Registro> getListaPaisNac()
    {
        return listaPaisNac;
    }

    public void setListaPaisNac(List<Registro> listaPaisNac)
    {
        this.listaPaisNac = listaPaisNac;
    }

    public List<Registro> getListaDepartamentoNac()
    {
        return listaDepartamentoNac;
    }

    public void setListaDepartamentoNac(List<Registro> listaDepartamentoNac)
    {
        this.listaDepartamentoNac = listaDepartamentoNac;
    }

    public List<Registro> getListaCiudNac()
    {
        return listaCiudNac;
    }

    public void setListaCiudNac(List<Registro> listaCiudNac)
    {
        this.listaCiudNac = listaCiudNac;
    }

    public List<Registro> getListaSexo()
    {
        return listaSexo;
    }

    public void setListaSexo(List<Registro> listaSexo)
    {
        this.listaSexo = listaSexo;
    }

    public List<Registro> getListaEstadoCivil()
    {
        return listaEstadoCivil;
    }

    public void setListaEstadoCivil(List<Registro> listaEstadoCivil)
    {
        this.listaEstadoCivil = listaEstadoCivil;
    }

    public List<Registro> getListaEscalafon()
    {
        return listaEscalafon;
    }

    public void setListaEscalafon(List<Registro> listaEscalafon)
    {
        this.listaEscalafon = listaEscalafon;
    }

    public List<Registro> getListaIdDeCargo()
    {
        return listaIdDeCargo;
    }

    public void setListaIdDeCargo(List<Registro> listaIdDeCargo)
    {
        this.listaIdDeCargo = listaIdDeCargo;
    }

    public List<Registro> getListaIdDeTipo()
    {
        return listaIdDeTipo;
    }

    public void setListaIdDeTipo(List<Registro> listaIdDeTipo)
    {
        this.listaIdDeTipo = listaIdDeTipo;
    }

    public RegistroDataModelImpl getListaDependencia()
    {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia)
    {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaIdCentroDeCosto()
    {
        return listaIdCentroDeCosto;
    }

    public RegistroDataModelImpl getListaCIIU()
    {
        return listaCIIU;
    }

    public void setListaCIIU(RegistroDataModelImpl listaCIIU)
    {
        this.listaCIIU = listaCIIU;
    }

    public void setListaIdCentroDeCosto(
        RegistroDataModelImpl listaIdCentroDeCosto)
    {
        this.listaIdCentroDeCosto = listaIdCentroDeCosto;
    }

    public RegistroDataModelImpl getListaCodigoEstablecimiento()
    {
        return listaCodigoEstablecimiento;
    }

    public void setListaCodigoEstablecimiento(
        RegistroDataModelImpl listaCodigoEstablecimiento)
    {
        this.listaCodigoEstablecimiento = listaCodigoEstablecimiento;
    }

    public List<Registro> getListaDeCarrera()
    {
        return listaDeCarrera;
    }

    public void setListaDeCarrera(List<Registro> listaDeCarrera)
    {
        this.listaDeCarrera = listaDeCarrera;
    }

    public RegistroDataModelImpl getListaTipoActividad()
    {
        return listaTipoActividad;
    }

    public void setListaTipoActividad(
        RegistroDataModelImpl listaTipoActividad)
    {
        this.listaTipoActividad = listaTipoActividad;
    }

    public List<Registro> getListaSede()
    {
        return listaSede;
    }

    public void setListaSede(List<Registro> listaSede)
    {
        this.listaSede = listaSede;
    }

    public RegistroDataModelImpl getListaTipovinculacion()
    {
        return listaTipovinculacion;
    }

    public void setListaTipovinculacion(
        RegistroDataModelImpl listaTipovinculacion)
    {
        this.listaTipovinculacion = listaTipovinculacion;
    }

    public RegistroDataModelImpl getListaSubtipocotizante()
    {
        return listaSubtipocotizante;
    }

    public void setListaSubtipocotizante(
        RegistroDataModelImpl listaSubtipocotizante)
    {
        this.listaSubtipocotizante = listaSubtipocotizante;
    }

    public List<Registro> getListaBanco()
    {
        return listaBanco;
    }

    public void setListaBanco(List<Registro> listaBanco)
    {
        this.listaBanco = listaBanco;
    }

    public List<Registro> getListaTipoCuenta()
    {
        return listaTipoCuenta;
    }

    public void setListaTipoCuenta(List<Registro> listaTipoCuenta)
    {
        this.listaTipoCuenta = listaTipoCuenta;
    }

    public List<Registro> getListaEstadoActual()
    {
        return listaEstadoActual;
    }

    public void setListaEstadoActual(List<Registro> listaEstadoActual)
    {
        this.listaEstadoActual = listaEstadoActual;
    }

    public List<Registro> getListaCausaRetiro()
    {
        return listaCausaRetiro;
    }

    public void setListaCausaRetiro(List<Registro> listaCausaRetiro)
    {
        this.listaCausaRetiro = listaCausaRetiro;
    }

    public List<Registro> getListaFondoSindicato()
    {
        return listaFondoSindicato;
    }

    public void setListaFondoSindicato(List<Registro> listaFondoSindicato)
    {
        this.listaFondoSindicato = listaFondoSindicato;
    }

    public List<Registro> getListaFondoSalud()
    {
        return listaFondoSalud;
    }

    public void setListaFondoSalud(List<Registro> listaFondoSalud)
    {
        this.listaFondoSalud = listaFondoSalud;
    }

    public List<Registro> getListaIdDelFondo()
    {
        return listaIdDelFondo;
    }

    public void setListaIdDelFondo(List<Registro> listaIdDelFondo)
    {
        this.listaIdDelFondo = listaIdDelFondo;
    }

    public List<Registro> getListaFondoPensionVol()
    {
        return listaFondoPensionVol;
    }

    public void setListaFondoPensionVol(List<Registro> listaFondoPensionVol)
    {
        this.listaFondoPensionVol = listaFondoPensionVol;
    }

    public List<Registro> getListaFondoRiesgos()
    {
        return listaFondoRiesgos;
    }

    public void setListaFondoRiesgos(List<Registro> listaFondoRiesgos)
    {
        this.listaFondoRiesgos = listaFondoRiesgos;
    }

    public List<Registro> getListaFondoCesantias()
    {
        return listaFondoCesantias;
    }

    public void setListaFondoCesantias(List<Registro> listaFondoCesantias)
    {
        this.listaFondoCesantias = listaFondoCesantias;
    }

    public List<Registro> getListaCajaCompensacion()
    {
        return listaCajaCompensacion;
    }

    public void setListaCajaCompensacion(List<Registro> listaCajaCompensacion)
    {
        this.listaCajaCompensacion = listaCajaCompensacion;
    }

    public List<Registro> getListaMedicinaPrepagada()
    {
        return listaMedicinaPrepagada;
    }

    public void setListaMedicinaPrepagada(
        List<Registro> listaMedicinaPrepagada)
    {
        this.listaMedicinaPrepagada = listaMedicinaPrepagada;
    }

    public List<Registro> getListaFondoAfc()
    {
        return listaFondoAfc;
    }

    public void setListaFondoAfc(List<Registro> listaFondoAfc)
    {
        this.listaFondoAfc = listaFondoAfc;
    }

    public List<Registro> getListaRegimen()
    {
        return listaRegimen;
    }

    public void setListaRegimen(List<Registro> listaRegimen)
    {
        this.listaRegimen = listaRegimen;
    }

    public RegistroDataModelImpl getListaGrupocontable()
    {
        return listaGrupocontable;
    }

    public void setListaGrupocontable(
        RegistroDataModelImpl listaGrupocontable)
    {
        this.listaGrupocontable = listaGrupocontable;
    }

    public RegistroDataModelImpl getListaNumeroPatronal()
    {
        return listaNumeroPatronal;
    }

    public void setListaNumeroPatronal(
        RegistroDataModelImpl listaNumeroPatronal)
    {
        this.listaNumeroPatronal = listaNumeroPatronal;
    }

    public RegistroDataModelImpl getListaTipoActo()
    {
        return listaTipoActo;
    }

    public void setListaTipoActo(RegistroDataModelImpl listaTipoActo)
    {
        this.listaTipoActo = listaTipoActo;
    }

    public RegistroDataModelImpl getListaTipoActoE()
    {
        return listaTipoActoE;
    }

    public void setListaTipoActoE(RegistroDataModelImpl listaTipoActoE)
    {
        this.listaTipoActoE = listaTipoActoE;
    }

    public List<Registro> getListaPaisHab()
    {
        return listaPaisHab;
    }

    public void setListaPaisHab(List<Registro> listaPaisHab)
    {
        this.listaPaisHab = listaPaisHab;
    }

    public List<Registro> getListaDepartamentoHab()
    {
        return listaDepartamentoHab;
    }

    public void setListaDepartamentoHab(List<Registro> listaDepartamentoHab)
    {
        this.listaDepartamentoHab = listaDepartamentoHab;
    }

    public List<Registro> getListaCiudadHab()
    {
        return listaCiudadHab;
    }

    public void setListaCiudadHab(List<Registro> listaCiudadHab)
    {
        this.listaCiudadHab = listaCiudadHab;
    }

    public List<Registro> getListaPaisLabora()
    {
        return listaPaisLabora;
    }

    public void setListaPaisLabora(List<Registro> listaPaisLabora)
    {
        this.listaPaisLabora = listaPaisLabora;
    }

    public List<Registro> getListaDepartamentoLabora()
    {
        return listaDepartamentoLabora;
    }

    public void setListaDepartamentoLabora(
        List<Registro> listaDepartamentoLabora)
    {
        this.listaDepartamentoLabora = listaDepartamentoLabora;
    }

    public List<Registro> getListaCiudadLabora()
    {
        return listaCiudadLabora;
    }

    public void setListaCiudadLabora(List<Registro> listaCiudadLabora)
    {
        this.listaCiudadLabora = listaCiudadLabora;
    }

    public List<Registro> getListaClasePensionado()
    {
        return listaClasePensionado;
    }

    public void setListaClasePensionado(List<Registro> listaClasePensionado)
    {
        this.listaClasePensionado = listaClasePensionado;
    }

    public RegistroDataModelImpl getListaTipopensionado()
    {
        return listaTipopensionado;
    }

    public void setListaTipopensionado(
        RegistroDataModelImpl listaTipopensionado)
    {
        this.listaTipopensionado = listaTipopensionado;
    }

    public List<Registro> getListaDctoIdentidadcausante()
    {
        return listaDctoIdentidadcausante;
    }

    public void setListaDctoIdentidadcausante(
        List<Registro> listaDctoIdentidadcausante)
    {
        this.listaDctoIdentidadcausante = listaDctoIdentidadcausante;
    }

    public List<Registro> getListaHistcargos()
    {
        return listaHistcargos;
    }

    public void setListaHistcargos(List<Registro> listaHistcargos)
    {
        this.listaHistcargos = listaHistcargos;
    }

    public List<Registro> getListaProfesionesempleado()
    {
        return listaProfesionesempleado;
    }

    public void setListaProfesionesempleado(
        List<Registro> listaProfesionesempleado)
    {
        this.listaProfesionesempleado = listaProfesionesempleado;
    }

    public RegistroDataModelImpl getListaCodigoProf()
    {
        return listaCodigoProf;
    }

    public void setListaCodigoProf(RegistroDataModelImpl listaCodigoProf)
    {
        this.listaCodigoProf = listaCodigoProf;
    }

    public RegistroDataModelImpl getListaCodigoProfE()
    {
        return listaCodigoProfE;
    }

    public void setListaCodigoProfE(RegistroDataModelImpl listaCodigoProfE)
    {
        this.listaCodigoProfE = listaCodigoProfE;
    }

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaNumeroDcto()
    {
        return listaNumeroDcto;
    }

    public void setListaNumeroDcto(RegistroDataModelImpl listaNumeroDcto)
    {
        this.listaNumeroDcto = listaNumeroDcto;
    }

    public List<Registro> getListaHConcepto()
    {
        return listaHConcepto;
    }

    public void setListaHConcepto(List<Registro> listaHConcepto)
    {
        this.listaHConcepto = listaHConcepto;
    }

    public RegistroDataModelImpl getListaIdDeCategoria()
    {
        return listaIdDeCategoria;
    }

    public void setListaIdDeCategoria(
        RegistroDataModelImpl listaIdDeCategoria)
    {
        this.listaIdDeCategoria = listaIdDeCategoria;
    }

    public String getEdadEmpleado()
    {
        return edadEmpleado;
    }

    public void setEdadEmpleado(String edadEmpleado)
    {
        this.edadEmpleado = edadEmpleado;
    }

    public String getDiasPendientesVac()
    {
        return diasPendientesVac;
    }

    public void setDiasPendientesVac(String diasPendientesVac)
    {
        this.diasPendientesVac = diasPendientesVac;
    }

    public boolean isParManejaHV()
    {
        return parManejaHV;
    }

    public void setParManejaHV(boolean parManejaHV)
    {
        this.parManejaHV = parManejaHV;
    }

    public String getEstadoActual()
    {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual)
    {
        this.estadoActual = estadoActual;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public String getAnoConsulta()
    {
        return anoConsulta;
    }

    public void setAnoConsulta(String anoConsulta)
    {
        this.anoConsulta = anoConsulta;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public String getIdEmpleado()
    {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado)
    {
        this.idEmpleado = idEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado)
    {
        this.listaEmpleado = listaEmpleado;
    }

    public RegistroDataModelImpl getListaEmpleado()
    {
        return listaEmpleado;
    }

    public String getSalario()
    {
        return salario;
    }

    public void setSalario(String salario)
    {
        this.salario = salario;
    }

    public String getCondTipoVinculacion()
    {
        return condTipoVinculacion;
    }

    public void setCondTipoVinculacion(String condTipoVinculacion)
    {
        this.condTipoVinculacion = condTipoVinculacion;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVisibleDgRetiro()
    {
        return visibleDgRetiro;
    }

    public void setVisibleDgRetiro(boolean visibleDgRetiro)
    {
        this.visibleDgRetiro = visibleDgRetiro;
    }

    public boolean isVisibleReajusteSalud()
    {
        return visibleReajusteSalud;
    }

    public void setVisibleReajusteSalud(boolean visibleReajusteSalud)
    {
        this.visibleReajusteSalud = visibleReajusteSalud;
    }

    private boolean validaciones()
    {
        if (registro.getCampos().get(FECHATERCONTRATO) == null)
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2643"));
            registro.getCampos().put(PRORROGAS, cantProrrogas);
            return false;
        }
        if (registro.getCampos().get(MESESPRORROGAS) == null
            || "0".equals(retornarString(registro, MESESPRORROGAS)))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2644"));
            registro.getCampos().put(PRORROGAS, cantProrrogas);
            return false;
        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        PRORROGAS))
        {
            registro.getCampos().put(PRORROGAS, 0);
        }

        return true;
    }

    private void redireccionarFondo(String tipoFondo)
    {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridR", css);
        parametros.put(CLASE_FONDO, tipoFondo);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.FONDOS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

    private void ajustarFecha(String fecha, String check)
    {
        boolean checkDep = (boolean) registro.getCampos().get(check);
        if (checkDep)
        {

            registro.getCampos().put(fecha, new Date());
        }
        else
        {
            registro.getCampos().put(fecha, null);
        }
    }

    public String getPrimerNombre()
    {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre)
    {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre()
    {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre)
    {
        this.segundoNombre = segundoNombre;
    }

    public boolean isVisibleSia()
    {
        return visibleSia;
    }

    public void setVisibleSia(boolean visibleSia)
    {
        this.visibleSia = visibleSia;
    }

    public List<Registro> getListaServicioAsociado()
    {
        return listaServicioAsociado;
    }

    public void setListaServicioAsociado(List<Registro> listaServicioAsociado)
    {
        this.listaServicioAsociado = listaServicioAsociado;
    }

    public String getTituloEtiqueta()
    {
        return tituloEtiqueta;
    }

    public void setTituloEtiqueta(String tituloEtiqueta)
    {
        this.tituloEtiqueta = tituloEtiqueta;
    }

    public boolean isVisibleSui()
    {
        return visibleSui;
    }

    public void setVisibleSui(boolean visibleSui)
    {
        this.visibleSui = visibleSui;
    }

    public boolean isVisibleIdipron()
    {
        return visibleIdipron;
    }

    public void setVisibleIdipron(boolean visibleIdipron)
    {
        this.visibleIdipron = visibleIdipron;
    }
    
    public boolean isVisibleObsRes()
    {
        return visibleObsRes;
    }

    public void setVisibleObsRes(boolean visibleObsRes)
    {
        this.visibleObsRes = visibleObsRes;
    }

    public List<Registro> getListaFondoSindical2()
    {
        return listaFondoSindical2;
    }

    public void setListaFondoSindical2(List<Registro> listaFondoSindical2)
    {
        this.listaFondoSindical2 = listaFondoSindical2;
    }

    public List<Registro> getListaFondoSindicato3()
    {
        return listaFondoSindicato3;
    }

    public void setListaFondoSindicato3(List<Registro> listaFondoSindicato3)
    {
        this.listaFondoSindicato3 = listaFondoSindicato3;
    }

    /**
     * Retorna la lista listaEstablecimiento
     *
     * @return listaEstablecimiento
     */
    public List<Registro> getListaEstablecimiento()
    {
        return listaEstablecimiento;
    }

    /**
     * Asigna la lista listaEstablecimiento
     *
     * @param listaEstablecimiento
     * Variable a asignar en listaEstablecimiento
     */
    public void setListaEstablecimiento(List<Registro> listaEstablecimiento)
    {
        this.listaEstablecimiento = listaEstablecimiento;
    }

    public RegistroDataModelImpl getListaAuxiliar()
    {
        return listaAuxiliar;
    }

    public void oprimirbtnCargar()
    {
        SessionUtil.cargarModalDatos(String.valueOf(GeneralCodigoFormaEnum.IMPORTARACTCIIU_CONTROLADOR.getCodigo()),
                        moduloNomina);

    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar)
    {
        this.listaAuxiliar = listaAuxiliar;
    }

    public RegistroDataModelImpl getListaIdJefe()
    {
        return listaIdJefe;
    }

    public void setListaIdJefe(RegistroDataModelImpl listaIdJefe)
    {
        this.listaIdJefe = listaIdJefe;
    }

    public RegistroDataModelImpl getListaFuenteRecurso()
    {
        return listaFuenteRecurso;
    }

    public void setListaFuenteRecurso(
        RegistroDataModelImpl listaFuenteRecurso)
    {
        this.listaFuenteRecurso = listaFuenteRecurso;
    }

    public RegistroDataModelImpl getListaReferencia()
    {
        return listaReferencia;
    }

    public void setListaReferencia(RegistroDataModelImpl listaReferencia)
    {
        this.listaReferencia = listaReferencia;
    }

    public String getNombreDependencia()
    {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia)
    {
        this.nombreDependencia = nombreDependencia;
    }

    public String getNombreCentroCosto()
    {
        return nombreCentroCosto;
    }

    public void setNombreCentroCosto(String nombreCentroCosto)
    {
        this.nombreCentroCosto = nombreCentroCosto;
    }

    public String getNombreUbicacion()
    {
        return nombreUbicacion;
    }

    public void setNombreUbicacion(String nombreUbicacion)
    {
        this.nombreUbicacion = nombreUbicacion;
    }

    public String getNombreAuxiliar()
    {
        return nombreAuxiliar;
    }

    public void setNombreAuxiliar(String nombreAuxiliar)
    {
        this.nombreAuxiliar = nombreAuxiliar;
    }

    public String getNombreReferencia()
    {
        return nombreReferencia;
    }

    public void setNombreReferencia(String nombreReferencia)
    {
        this.nombreReferencia = nombreReferencia;
    }

    public String getNombreJefe()
    {
        return nombreJefe;
    }

    public void setNombreJefe(String nombreJefe)
    {
        this.nombreJefe = nombreJefe;
    }

    public String getNombreFuente()
    {
        return nombreFuente;
    }

    public void setNombreFuente(String nombreFuente)
    {
        this.nombreFuente = nombreFuente;
    }

    public String getNombreTipoActividad()
    {
        return nombreTipoActividad;
    }

    public void setNombreTipoActividad(String nombreTipoActividad)
    {
        this.nombreTipoActividad = nombreTipoActividad;
    }

    public String getNombreVinculacion()
    {
        return nombreVinculacion;
    }

    public void setNombreVinculacion(String nombreVinculacion)
    {
        this.nombreVinculacion = nombreVinculacion;
    }

    public String getNombreCotizante()
    {
        return nombreCotizante;
    }

    public void setNombreCotizante(String nombreCotizante)
    {
        this.nombreCotizante = nombreCotizante;
    }

    public String getNombreGrupoContable()
    {
        return nombreGrupoContable;
    }

    public void setNombreGrupoContable(String nombreGrupoContable)
    {
        this.nombreGrupoContable = nombreGrupoContable;
    }

    public String getNombreTipoPensionado()
    {
        return nombreTipoPensionado;
    }

    public void setNombreTipoPensionado(String nombreTipoPensionado)
    {
        this.nombreTipoPensionado = nombreTipoPensionado;
    }

    public RegistroDataModelImpl getListaManual()
    {
        return listaManual;
    }

    public void setListaManual(RegistroDataModelImpl listaManual)
    {
        this.listaManual = listaManual;
    }

    public boolean isVisibleManual()
    {
        return visibleManual;
    }

    public void setVisibleManual(boolean visibleManual)
    {
        this.visibleManual = visibleManual;
    }

    /**
     * @return the correo
     */
    public String getCorreo()
    {
        return correo;
    }

    /**
     * @param correo
     * the correo to set
     */
    public void setCorreo(String correo)
    {
        this.correo = correo;
    }

    /**
     * @return the paramCorreo
     */
    public String getparamCorreo()
    {
        return paramCorreo;
    }

    /**
     * @param paramCorreo
     * the paramCorreo to set
     */
    public void setparamCorreo(String paramCorreo)
    {
        paramCorreo = paramCorreo;
    }

    public String getModulo()
    {
        return modulo;
    }

    public String getNombrecompleto()
    {
        return nombrecompleto;
    }

    public void setNombrecompleto(String nombrecompleto)
    {
        this.nombrecompleto = nombrecompleto;
    }

    public RegistroDataModelImpl getListaLocalidad()
    {
        return ListaLocalidad;
    }

    public void setListaLocalidad(RegistroDataModelImpl listaLocalidad)
    {
        ListaLocalidad = listaLocalidad;
    }

    public List<Registro> getListaEscalafonTemp()
    {
        return listaEscalafonTemp;
    }

    public void setListaEscalafonTemp(List<Registro> listaEscalafonTemp)
    {
        this.listaEscalafonTemp = listaEscalafonTemp;
    }

    public List<Registro> getListaListaEmpleadoEsc()
    {
        return listaListaEmpleadoEsc;
    }

    public void setListaListaEmpleadoEsc(List<Registro> listaListaEmpleadoEsc)
    {
        this.listaListaEmpleadoEsc = listaListaEmpleadoEsc;
    }

    public RegistroDataModelImpl getListaListaEncargoComis()
    {
        return listaListaEncargoComis;
    }

    public void setListaListaEncargoComis(RegistroDataModelImpl listaListaEncargoComis)
    {
        this.listaListaEncargoComis = listaListaEncargoComis;
    }

    public RegistroDataModelImpl getListaCargoTemporal()
    {
        return listaCargoTemporal;
    }

    public void setListaCargoTemporal(RegistroDataModelImpl listaCargoTemporal)
    {
        this.listaCargoTemporal = listaCargoTemporal;
    }

    public boolean isVisiblemesPen63()
    {
        return visiblemesPen63;
    }

    public void setVisiblemesPen63(boolean visiblemesPen63)
    {
        this.visiblemesPen63 = visiblemesPen63;
    }
    
    
    public boolean isVisibleSabadoHabil()
    {
        return visibleSabadoHabil;
    }

    public void setVisibleSabadoHabil(boolean visibleSabadoHabil)
    {
        this.visibleSabadoHabil = visibleSabadoHabil;
    }

    public boolean isVisibleSindicatos()
    {
        return visibleSindicatos;
    }

    public void setVisibleSindicatos(boolean visibleSindicatos)
    {
        this.visibleSindicatos = visibleSindicatos;
    }

    public String getCIIU_riesgo()
    {
        return CIIU_riesgo;
    }

    public void setCIIU_riesgo(String CIIU_riesgo)
    {
        this.CIIU_riesgo = CIIU_riesgo;
    }

    public String getCIIU_subactividad()
    {
        return CIIU_subactividad;
    }

    public void setCIIU_subactividad(String cIIU_subactividad)
    {
        CIIU_subactividad = cIIU_subactividad;
    }

    public String getCIIU_actividad()
    {
        return CIIU_actividad;
    }

    public void setCIIU_actividad(String cIIU_actividad)
    {
        CIIU_actividad = cIIU_actividad;
    }

	public List<Registro> getListaTipoDocDependiente() {
		return listaTipoDocDependiente;
	}

	public void setListaTipoDocDependiente(List<Registro> listaTipoDocDependiente) {
		this.listaTipoDocDependiente = listaTipoDocDependiente;
	}

	public List<Registro> getListaParentescoDependiente() {
		return listaParentescoDependiente;
	}

	public void setListaParentescoDependiente(List<Registro> listaParentescoDependiente) {
		this.listaParentescoDependiente = listaParentescoDependiente;
	}

	public boolean isVerDependientes() {
		return verDependientes;
	}

	public void setVerDependientes(boolean verDependientes) {
		this.verDependientes = verDependientes;
	}

	/**
	 * @return the listatipoDiscapacidad
	 */
	public RegistroDataModelImpl getListatipoDiscapacidad() {
		return listatipoDiscapacidad;
	}

	/**
	 * @param listatipoDiscapacidad the listatipoDiscapacidad to set
	 */
	public void setListatipoDiscapacidad(RegistroDataModelImpl listatipoDiscapacidad) {
		this.listatipoDiscapacidad = listatipoDiscapacidad;
	}

	/**
	 * @return the listanivelEducativo
	 */
	public RegistroDataModelImpl getListanivelEducativo() {
		return listanivelEducativo;
	}

	/**
	 * @param listanivelEducativo the listanivelEducativo to set
	 */
	public void setListanivelEducativo(RegistroDataModelImpl listanivelEducativo) {
		this.listanivelEducativo = listanivelEducativo;
	}

	/**
	 * @return the listaProfesion
	 */
	public RegistroDataModelImpl getListaProfesion() {
		return listaProfesion;
	}

	/**
	 * @param listaProfesion the listaProfesion to set
	 */
	public void setListaProfesion(RegistroDataModelImpl listaProfesion) {
		this.listaProfesion = listaProfesion;
	}

	/**
	 * @return the listaServidorPublico
	 */
	public RegistroDataModelImpl getListaServidorPublico() {
		return listaServidorPublico;
	}

	/**
	 * @param listaServidorPublico the listaServidorPublico to set
	 */
	public void setListaServidorPublico(RegistroDataModelImpl listaServidorPublico) {
		this.listaServidorPublico = listaServidorPublico;
	}
	
	/**
	 * @return the obligaCampos1
	 */
	public String getObligaCampos1() {
		return obligaCampos1;
	}

	/**
	 * @param obligaCampos1 the obligaCampos1 to set
	 */
	public void setObligaCampos1(String obligaCampos1) {
		this.obligaCampos1 = obligaCampos1;
	}

	/**
	 * @return the obligaCampos2
	 */
	public String getObligaCampos2() {
		return obligaCampos2;
	}

	/**
	 * @param obligaCampos2 the obligaCampos2 to set
	 */
	public void setObligaCampos2(String obligaCampos2) {
		this.obligaCampos2 = obligaCampos2;
	}

	/**
	 * @return the obligaCampos3
	 */
	public String getObligaCampos3() {
		return obligaCampos3;
	}

	/**
	 * @param obligaCampos3 the obligaCampos3 to set
	 */
	public void setObligaCampos3(String obligaCampos3) {
		this.obligaCampos3 = obligaCampos3;
	}

	/**
	 * @return the obligaCampos4
	 */
	public String getObligaCampos4() {
		return obligaCampos4;
	}

	/**
	 * @param obligaCampos4 the obligaCampos4 to set
	 */
	public void setObligaCampos4(String obligaCampos4) {
		this.obligaCampos4 = obligaCampos4;
	}

	/**
	 * @return the obligaCampos5
	 */
	public String getObligaCampos5() {
		return obligaCampos5;
	}

	/**
	 * @param obligaCampos5 the obligaCampos5 to set
	 */
	public void setObligaCampos5(String obligaCampos5) {
		this.obligaCampos5 = obligaCampos5;
	}

	/**
	 * @return the obligaCampos6
	 */
	public String getObligaCampos6() {
		return obligaCampos6;
	}

	/**
	 * @param obligaCampos6 the obligaCampos6 to set
	 */
	public void setObligaCampos6(String obligaCampos6) {
		this.obligaCampos6 = obligaCampos6;
	}

	/**
	 * @return the obligaCampos7
	 */
	public String getObligaCampos7() {
		return obligaCampos7;
	}

	/**
	 * @param obligaCampos7 the obligaCampos7 to set
	 */
	public void setObligaCampos7(String obligaCampos7) {
		this.obligaCampos7 = obligaCampos7;
	}

	/**
	 * @return the obligaCampos8
	 */
	public String getObligaCampos8() {
		return obligaCampos8;
	}

	/**
	 * @param obligaCampos8 the obligaCampos8 to set
	 */
	public void setObligaCampos8(String obligaCampos8) {
		this.obligaCampos8 = obligaCampos8;
	}

	/**
	 * @return the obligaCampos9
	 */
	public String getObligaCampos9() {
		return obligaCampos9;
	}

	/**
	 * @param obligaCampos9 the obligaCampos9 to set
	 */
	public void setObligaCampos9(String obligaCampos9) {
		this.obligaCampos9 = obligaCampos9;
	}

	/**
	 * @return the obligaCampos10
	 */
	public String getObligaCampos10() {
		return obligaCampos10;
	}

	/**
	 * @param obligaCampos10 the obligaCampos10 to set
	 */
	public void setObligaCampos10(String obligaCampos10) {
		this.obligaCampos10 = obligaCampos10;
	}

	/**
	 * @return the obligaCampos11
	 */
	public String getObligaCampos11() {
		return obligaCampos11;
	}

	/**
	 * @param obligaCampos11 the obligaCampos11 to set
	 */
	public void setObligaCampos11(String obligaCampos11) {
		this.obligaCampos11 = obligaCampos11;
	}

	
	/**
	 * @return the obligaCampos13
	 */
	public String getObligaCampos13() {
		return obligaCampos13;
	}

	/**
	 * @param obligaCampos13 the obligaCampos13 to set
	 */
	public void setObligaCampos13(String obligaCampos13) {
		this.obligaCampos13 = obligaCampos13;
	}

	/**
	 * @return the obligaCampos14
	 */
	public String getObligaCampos14() {
		return obligaCampos14;
	}
	
	public void setObligaCampos14(String obligaCampos14) {
		this.obligaCampos14 = obligaCampos14;
	}
	
	/**
	 * @return the obligaCampos15
	 */
	public String getObligaCampos15() {
		return obligaCampos15;
	}

	/**
	 * @param obligaCampos15 the obligaCampos15 to set
	 */
	public void setObligaCampos15(String obligaCampos15) {
		this.obligaCampos15 = obligaCampos15;
	}

	/**
	 * @return the obligaCampos16
	 */
	public String getObligaCampos16() {
		return obligaCampos16;
	}

	/**
	 * @param obligaCampos16 the obligaCampos16 to set
	 */
	public void setObligaCampos16(String obligaCampos16) {
		this.obligaCampos16 = obligaCampos16;
	}

	/**
	 * @return the obligaCampos17
	 */
	public String getObligaCampos17() {
		return obligaCampos17;
	}
	
	public void setObligaCampos17(String obligaCampos17) {
		this.obligaCampos17 = obligaCampos17;
	}
	
	public String getObligaCampos18() {
		return obligaCampos18;
	}
	
	public void setObligaCampos18(String obligaCampos18) {
		this.obligaCampos18 = obligaCampos18;
	}

	/**
	 * @return the listaFondoACCAI
	 */
	public List<Registro> getListaFondoACCAI() {
		return listaFondoACCAI;
	}

	/**
	 * @param listaFondoACCAI the listaFondoACCAI to set
	 */
	public void setListaFondoACCAI(List<Registro> listaFondoACCAI) {
		this.listaFondoACCAI = listaFondoACCAI;
	}

}