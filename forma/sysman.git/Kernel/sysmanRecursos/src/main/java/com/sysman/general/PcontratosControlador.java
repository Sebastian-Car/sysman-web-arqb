package com.sysman.general;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.ejb.EjbContratosGeneralRemote;
import com.sysman.contratos.ejb.EjbContratosUnoGeneralRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.email.ApiRestClient;
import com.sysman.email.EmailPojo;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.PcontratosControladorEnum;
import com.sysman.general.enums.PcontratosControladorUrlEnum;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APISIGEC;
import com.sysman.util.rest.ParametrosSIGEC;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaApiSigec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 19/11/2015
 *
 * @author amonroy
 * @version 2, 18/01/2017 Se modifica el metodo genConsecContratos()
 * en donde se llama la funcion FC_CONSEC_CONTRATOS ubicada en el
 * paquete PCK_CONTRATOS_COM1, la cual realiza la funcionalidad que se
 * habia definido en la version anterior de este metodo.
 *
 * @author jrodriguezr
 * @version 3, 09/05/2017 Se refactoriza el codigo SQL de las listas
 * para utilizar dss.
 *
 * @author jrodrigueza
 * @version 4, 16/05/2017 Ajustes asociados con la forma y hallazgos
 * de integración.
 *
 * @author spina - refactorizo conexiones
 * @version 5, 12/06/2017
 * 
 * @author asana, se actualiza al campo CAMPO_VALORTOTAL y
 * CAMPO_VALORFINAL, la suma del valor de los items, en el caso que
 * existan, en caso contrario se mantiene el valor digitado al momento
 * de crear el comprobante
 * @version 6, 22/08/2018
 */
@ManagedBean
@ViewScoped
public class PcontratosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en la aplicacion
     */
    private String modulo;

    /**
     * Codigo del usuario que ingresa a la aplicacion
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    private int nivelUsuario;
    private boolean muestraDialogo;
    private boolean muestraDialogoActualiza;
    private boolean muestraDialogoActualizaIva;
    private boolean muestraDialogoConfirmar;
    private boolean muestraEliminarInfPptal;
    private boolean muestraCambiarConsecutivo;
    private String nuevoConsecutivo;
    private String vigencia;
    private String titulo;
    private boolean diasVisible;
    private boolean botonCambiar;
    private boolean mostrarReintegro;
    /**
     * Tipo de orden de compra.
     */
    private String claseF;
    private String lblTitulo;
    private String tipoContrato;
    private String copiarDe;
    private double tipoAdjudicacionCuantia;
    private double txtSubPorAdmon;
    private double txtSubPorImprevistos;
    private double txtSubPorUtilidades;
    private String modelo;
    private String nombreAbog;
    private String cargoAbog;
    private String auxiliarColUno;
    private String fuenteRecursosColDos;
    private String claseDisponibilidaD;
    private String fecha;
    private String auxiliarColumn3;
    private String valorAportes;
    private boolean convenioLocked;
    private String manejaIvaAsumido;
    private String manejaNominaContratistas;
    private String controlaTamanioNumeroContrato;
    private String controlaValorContratoSegunDisp;
    private String controlaItemsPlanCompras;
    private String manejaWorkflow;
    private String manejaCamposObligatorios;
    private String tipoContratoCamposObligatorios;
    private String manejaCamposObligatoriosAdicionales;
    private String manejaCamposObligatoriosAdicionalesidsn;
    private String bloqueaFechaInicializacionContrato;
    private String textoGarantiaContratos;
    private String manejaSelectorClaseContrato;
    private String manejaBancoProyectos;
    private String registraPoryectosContratos;
    private String manejaProgramacionFormasPago;
    private String muestraContratosVigencia;
    private String tiposContratosTercerosAportantes;
    private String redondearUnitarioIvaOC;
    private String digitosRedondeoUnitarioIvaOc;
    private String redondearValorTotalOC;
    private String valorSalarioMinimo;
    private String manejaMeses30dias;
    private String manejaPagoEstampillas;
    private String verTipoContratoCasanare;
    private String controlContratosValorCuantia;
    private String manejaControlReporteSICE;
    private String permiteEliminarInformacionPptal;
    private String manejaCuatroPorMil;
    private String filtroDependenciaMovimiento;
    private String digitosRedondeoValorIVAOC;
    private String redondearValorIvaOC;
    private String digitosRedondeoGranTotalOC;
    private String redondearGranTotalOC;
    private String digitosRedondeoValorTotalOC;
    private String unidadDuracionContrato;
    private String obligaInformacionPresupuestalContratos;
    private String claseContableMantenimientoContrPagos;
    private boolean btnFormaPagoVisible;
    private String indicadorPolizas;
    private boolean etiqueta1006Visible;
    private boolean tipoContrSECOPVisible;
    private boolean etiqueta1008Visible;
    private boolean funcionVisible;
    private boolean impreso;
    private boolean convenio;
    private String numconvenio;
    private double totalOrdenCompra;
    private boolean numconvenioEnabled;
    private boolean datosConvenioVisible;
    private boolean txtaportestVisible;
    private boolean aportestVisible;
    private boolean txtaporteseVisible;
    private boolean aporteseVisible;
    private boolean obrapublicaVisible;
    private boolean reimprimir;
    private boolean impresoEnabled;
    private String calificaContratista;
    private boolean texto562Visible;
    private boolean etiqueta563Visible;
    private boolean etiqueta632Visible;
    private boolean etiqueta635Visible;
    private boolean convenioVisible;
    private boolean numconvenioVisible;
    private boolean txtValorTotalLocked;
    private boolean etiqueta629Visible;
    private String txtDigRedoValorUnitarioIVA;
    private String txtDigRedonTotal;
    private int txtDigitosRedondeoGranTotal;
    private int txtDigitosRedondeoValorIVA;
    private String nominaContratistas;
    private boolean cuatroPorMil;
    private boolean programaVisible;
    private boolean proyectoVisible;
    private boolean cuadro625Visible;
    private boolean etNumPersonasVisible;
    private boolean numPersonasVisible;
    private boolean etiqueta325Visible;
    private boolean calcularIVAVisible;
    private boolean txtHonorariosVisible;
    private boolean eqtVehiculoVisible;
    private boolean txtVehiculoVisible;
    private boolean etqTopeVehiculoVisible;
    private boolean txtTopeVehiculoVisible;
    private boolean cmdEliminarInfPptalVisible;
    private boolean reportarSICEVisible;
    private boolean eReportarSICEVisible;
    private boolean valorContratoCuantiaVisible;
    private boolean eqtHonorariosVisible;
    private boolean etiqueta537Visible;
    private boolean clasificacionContratoVisible;
    private boolean clasR6Visible;
    private boolean seleccionarRequisicionesEnabled;
    private boolean componenteBPVisible;
    private boolean ordendecompraAuxiliaresVisible;
    private boolean etiqueta52Visible;
    private boolean auxiliarVisible;
    private boolean txtAuxiliarVisible;
    private boolean etiqueta53Visible;
    private boolean fuenteRecursosVisible;
    private boolean etiqueta211Visible;
    private boolean destinoVisible;
    private boolean etiqueta525Visible;
    private boolean etiqueta528Visible;
    private double totalCuatroXmil;
    private String nombreTercero;
    private String txtEvalValor;
    private boolean terceroLocked;
    private boolean ordenDeServicioPage1Enabled;
    private boolean ordenDeServicioPage2Enabled;
    private boolean ordenDeServicioPage3Enabled;
    private boolean itemsCEnabled;
    private boolean datosDelContratoEnabled;
    private boolean contratistaInterventorEnabled;
    private boolean informacionJuridicaEnabled;
    private boolean informacionPresupuestalEnabled;
    private boolean informacionFuncionalEnabled;
    private boolean pagina33Enabled;
    private boolean fechaDiligenciamientoLocked;
    private boolean diasLocked;
    private boolean plazoEntregaLocked;
    private boolean unidadPlazoLocked;
    private boolean lugarDeEntregaLocked;
    private boolean tipoAdjudicacionLocked;
    private boolean sectorLocked;
    private boolean tipoContratosLocked;
    private boolean tipoVeeduriaLocked;
    private boolean tipologiaLocked;
    private boolean numeroActaLocked;
    private boolean claseContratoLocked;
    private boolean descripcionLocked;
    private boolean formaDePagoLocked;
    private boolean fechaLocked;
    private boolean objetoContratoLocked;
    private boolean garantiaLocked;
    private boolean fechaFinalizacionLocked;
    private boolean duracionLocked;
    private boolean dependenciaLocked;
    private boolean considerandosLocked;
    private boolean valorFinalLocked;
    private boolean texto217Locked;
    private boolean texto219Locked;
    private boolean interventorLocked;
    private boolean valorTotalLocked;
    private boolean cmdCopiarDeEnabled;
    private boolean importatodoEnabled;
    private boolean mismoDia;
    private boolean fechaInicio;
    private boolean aportantes;
    private String bloqueaFechaDiligenciamientoContratos;
    private boolean mostrarFuente;
    private String controlaValorVsItems;
	private boolean paramManejaReportesAppui;
	private boolean paramManejaControlDeContratos;
	private boolean manejaReportesAppui;
	private boolean vigenciaContratoPorFirma;
	
    /**
     * Tipo de formato para la lista de modelos.
     */
    private String formato;
    /**
     * Permite visualizar el botón Arrendamiento en la pestaña
     * informacion General.
     */
    private boolean arrendamientofVisible;
    private String auxiliar;
    private String actividad;
    private String especialidad;
    private int indiceClasificacionproponentes;
    private boolean codEquivalenteLocked;
    private boolean actualizaPlanDeComprasLocked;
    private boolean subPContratoPermiteAgregar;
    private boolean subPContratoPermiteEliminar;
    private boolean subPContratoPermiteEditar;
    private boolean porcIVAGlobalLocked;
    private boolean porcDescGlobalLocked;
    private String imprimeInformeF50;
    private boolean pagoEstampillasVisible;
    private boolean etqPagoEstampillasVisible;
    private String fechaFirmaCierreAdjudicacionIgual;
    private boolean copiarDeEnabled;
    private String modeloPlantilla;
    private String fechaFormato;
    private boolean btnTercerosAportantesVisible;
    private String fechaFormatoPoliza;
    private double txtValorIva;
    private double txtValorDescuento;
    private double txtSub;
    private double txtAjusteAlPeso;
    private String mensajeActualizaIVA;
    private String disponibilidadConcatenado;
    private String reservaConcatenado;
    private boolean cargarSub;
    private boolean tituloAportantesVisible;
    private boolean enviarEmailVisible;
    private boolean cargaProyContratos;
    private boolean cargaCofinanciadores;
    private boolean cargaSecop;
    private boolean cargaDestino;
    private boolean cargaDestinoOtro;
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
    private String obligaCampos12;
    private String obligaCampos13;
    private String obligaCampos14;
    private String obligaCampos15;
    private String obligaCampos16;
    private String obligaCampos17;
    private String obligaCampos18;
    private String obligaCampos19;
    private String obligaCampos20;
    private String obligaCampos21;
    private String obligaCampos22;
    private String obligaCampos23;
    private String obligaCampos24;
    private String obligaCampos25;
    private String obligaCampos26;
    private String obligaCampos27;
    private String obligaCampos28;
    private String obligaCampos29;
    private boolean sigecVisible;
    private boolean mayoracero;
    /**
     * Valor de los items extraídos del detalle.
     */
    private double evalValor;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaOrdenador;
    private Registro listaValorTotal;
    private Registro listaModificacionTiempo;
    private List<Registro> listaTipoAdjudicacion;
    private List<Registro> listaSector;
    private List<Registro> listaTIPOLOGIA;
    private List<Registro> listaTipoContratoSECOP;
    private List<Registro> listaDestino;
    private List<Registro> listaCOMPONENTEBP;
    private List<Registro> listaActividadSub;
    /**
     * Lista de registros de la tabla CGR_CODIGOS
     */
    private RegistroDataModelImpl listaCgrConcepto;
    /**
     * Lista de registros de la tabla TIPODESTINO
     */
    private RegistroDataModelImpl listaCgrTipoGasto;
    
    private List<Registro> listaPais;

	private List<Registro> listaDepartamento;

	private List<Registro> listaCiudad;

	private List<Registro> listaEstadoCivil;

	private List<Registro> listaNivelEmpleo;

	private RegistroDataModelImpl listaNivelEducativo;

	private List<Registro> listaDepartamentoTrabajo;
	
	 private RegistroDataModelImpl listaServidorPublico;

	 private RegistroDataModelImpl listaProfesion;

	 private RegistroDataModelImpl listatipoDiscapacidad;
	 
	 private Map<String, Object> parameters = new TreeMap<>();
	

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaGrupoSub;
    private RegistroDataModelImpl listaGrupoSubE;
    /**
     */
    private RegistroDataModelImpl listaEspecialidadSub;
    /**
     */
    private RegistroDataModelImpl listaEspecialidadSubE;
    private RegistroDataModelImpl listaListaModelos;
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaNumconvenio;
    private RegistroDataModelImpl listaCopiarDe;
    private RegistroDataModelImpl listaCEDULAINTERVENTOR;
    private RegistroDataModelImpl listaClaseDisponibilidad;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaModelos;
    private RegistroDataModelImpl listalistaRubros;
    private RegistroDataModelImpl listaFuenteRecurso;
    private RegistroDataModelImpl listaCedAbog;
    /**
     * Lista de registros de la tabla CGR_SEGMENTO
     */
    private RegistroDataModelImpl listaCgrSegmento;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private List<Registro> listaClasificacionproponentes;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;
    private static final String FILTRAR_LM2S = "PF('LM2s').filter()";
    private static final String FORMATO_FECHA = "dd/MM/yyyy";
    private static final String LLAVE_PCONTRATO = "ridPcontrato";
    private static final String MSG_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    private static final String MSG_CONTRATO_REGISTRADO = "TB_TB2083";
    private static final String CAMPO_CLASEORDEN = "CLASEORDEN";
    private static final String CAMPO_NUMERO = "NUMERO";
    private static final String CAMPO_PLAZODEENTREGA = "PLAZODEENTREGA";
    private static final String CAMPO_VALORFINAL = "VALORFINAL";
    private static final String CAMPO_FECHA = GeneralParameterEnum.FECHA
                    .getName();
    private static final String CAMPO_UNIDAD_PLAZO = "UNIDAD_PLAZO";
    private static final String CAMPO_VALORTOTAL = "VALORTOTAL";
    private static final String CAMPO_VALORTOTALOP = "VALORTOTALOP";
    private static final String CAMPO_FECHAFINALIZACION = "FECHAFINALIZACION";
    private static final String CAMPO_IMPRESO = "IMPRESO";
    private static final String CAMPO_CODIGO = "CODIGO";
    private static final String CAMPO_ACTUALIZAPLANDECOMPRAS = "ACTUALIZAPLANDECOMPRAS";
    private static final String CAMPO_VALOR_CONTRATO = "VALOR_CONTRATO";
    private static final String CAMPO_TIPODIA = "TIPODIA";
    private static final String CAMPO_TIPOADJUDICACION = "TIPOADJUDICACION";
    private static final String CAMPO_TERCERO = "TERCERO";
    private static final String CAMPO_SUCURSAL = "SUCURSAL";
    private static final String CAMPO_RUBRONOMBRE = "RUBRONOMBRE";
    private static final String CAMPO_RUBRO = "RUBRO";
    private static final String CAMPO_PRECIOS_UNITARIOS = "PRECIOS_UNITARIOS";
    private static final String CAMPO_PORCIVAGLOBAL = "PORCIVAGLOBAL";
    private static final String CAMPO_PORCDESCGLOBAL = "PORCDESCGLOBAL";
    private static final String CAMPO_NUM_CONVENIO = "NUM_CONVENIO";
    private static final String CAMPO_NOMBRECONTRATISTA = "NOMBRECONTRATISTA";
    private static final String CAMPO_CEDULAINTERVENTOR = "CEDULAINTERVENTOR";
    private static final String CAMPO_PORC_ANTICIPO = "PORC_ANTICIPO";
    private static final String CAMPO_NOMBRE = "NOMBRE";
    private static final String CAMPO_IMPORTARPRE = "IMPORTARPRE";
    private static final String CAMPO_FECHAFIRMA = "FECHAFIRMA";
    private static final String CAMPO_ESPECIALIDAD = "ESPECIALIDAD";
    private static final String CAMPO_DISPONIBILIDAD = "DISPONIBILIDAD";
    private static final String CAMPO_DEPENDENCIA = "DEPENDENCIA";
    private static final String CAMPO_CONVENIO = "CONVENIO";
    private static final String CAMPO_CLASEDISPONIBILIDAD = "CLASEDISPONIBILIDAD";
    private static final String CAMPO_NUMPERSONASCONTRATADAS = "NUMPERSONASCONTRATADAS";
    private static final String CAMPO_AUXILIAR = "AUXILIAR";
    private static final String CAMPO_ACTIVIDAD = "ACTIVIDAD";
    private static final String CAMPO_COMPANIA = "COMPANIA";
    private static final String CAMPO_VALORPAGOS = "VALORPAGOS";
    private static final String CAMPO_VALOR_ANTICIPO = "VALOR_ANTICIPO";
    private static final String CAMPO_PORINPREVISTOS = "PORINPREVISTOS";
    private static final String CAMPO_PORUTILIDADES = "PORUTILIDADES";
    private static final String CAMPO_OBJETOCONTRATO = "OBJETOCONTRATO";
    private static final String CAMPO_DURACION = "DURACION";
    private static final String CAMPO_PORADMINISTRACION = "PORADMINISTRACION";
    private static final String TXTBEANTB_TB2103 = "TB_TB2103";
    private static final String TXTBEANTB_TB2104 = "TB_TB2104";
    private static final String TXTBEANTB_TB2101 = "TB_TB2101";
    private static final String PARAMETRO_NUMERO_ORDEN = "numeroOrden";
    private static final String PARAMETRO_RETORNO_FORMULARIO = "retornoFormulario";
    private static final String PARAMETRO_CLASE_ORDEN = "claseOrden";
    private static final String PARAMETRO_TITULO = "titulo";
    private static final String PARAMETRO_VIGENCIA = "vigencia";
    private static final String PARAMETRO_CLASEF = "claseF";
    private static final String PARAMETRO_RETORNA = "retorna";
    private static final String PARAMETRO_FECHAFIRMA = "fechaFirma";
    private static final String TABLA_CLASIFICACION_PROPONENTES = "CLASIFICACION_PROPONENTES";
    private static final String CAMPO_PLATAFORMA = "PLATAFORMA";
    private static final String CAMPO_PAIS = "ID_PAIS";
    private static final String CAMPO_KEY_COMPANIA = "KEY_COMPANIA";
    private static final String CAMPO_KEY_NUMEROCONTRATO = "KEY_NUMERO_CONTRATO";
    private static final String CAMPO_NUMEROCONTRATO = "NUMERO_CONTRATO";
    private static final String CAMPO_KEY_TIPOCONTRATO = "KEY_TIPO_CONTRATO";
    private static final String CAMPO_TIPOCONTRATO = "TIPO_CONTRATO";
    private static final String CAMPO_DEP = "ID_DEPARTAMENTO";
    private static final String CAMPO_MUNICIPIO = "ID_MUNICIPIO";
    private static final String CAMPO_ESTADO_CIVIL = "ESTADO_CIVIL";
    private static final String CAMPO_NUMERO_HIJOS = "NUMERO_HIJOS";
    private static final String CAMPO_SERVIDOR_PUBLICO = "TIPO_SERVIDOR_PUBLICO";
    private static final String CAMPO_NIVEL_EMPLEO = "NIVEL_EMPLEO";
    private static final String CAMPO_NIVEL_EDUCATIVO = "NIVEL_EDUCACION";
    private static final String CAMPO_PROFESION = "ID_PROFESION";
    private static final String CAMPO_DEP_TRABAJO = "DEPARTAMENTO_TRABAJO";
    private static final String CAMPO_DISCAPACIDAD = "ES_DISCAPACITADO";
    private static final String CAMPO_TIPO_DISCAPACIDAD = "TIPO_DISCAPACIDAD";
    private static final String CAMPO_BENEFICIARIO = "ES_BENEFICIARIO";
    private static final String CAMPO_SALARIO = "SALARIO";
    // </DECLARAR_ADICIONALES>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContratosGeneralRemote ejbContratosCero;

    @EJB
    private EjbContratosUnoGeneralRemote ejbContratosUno;
    private Object modificacionTiempo;
	private Map<String, String>  camposBorde;
	private Map<String, String>  camposBordeSigec;
	private Map<String, String> camposBordeFechaFirma;
	private Map<String, String>  camposBordeCGR;
	private boolean obligaCampos;
	  /**
     * Activa el check ActaInicio
     */
	private boolean habilitaActasInicio;
	private boolean obligaCampoSigec = false;
	private boolean visibleSujeto;
	//campos Informacion funcional
	private String codProfesion;
	private String codPais;
	private String codDepartamento;
	private String codMunicipio;
	private String codEstadoCivil;
	private String codServPublico;
	private String codNivelEmpleo;
	private String codNivelEducativo;
	private String codDepTrabajo;
	private String codDiscapacidad;
	private boolean esDiscapacitado;
	private boolean esBeneficiario;
	private double salario;
	private String nombrePais;
	private String nombreDepartamento;
	private String nombreMunicipio;
	private String nombreEstadoCivil;
	private String numeroHijos;
	private String valorNumeroHijos;
	private String nombreServPublico;
	private String nombreNivelEmpleo;
	private String nombreNivelEducativo;
	private String nombreProfesion;
	private String nombreDepTrabajo;
	private String nombreDiscapacidad;
	private String codDependencia;
	private String nombreDependencia;
	private double vlrAIU;
    private double vlrImpConsumo;
    private String topTotal;
	
	//lvega 21/05/25
	private Map<String,Object> parametroswf;
	private boolean verCerrar = true;
	private boolean obligaCamposCGR;
	private boolean parametroCGR;
	private boolean aplicaAIU;
    /**
     * Crea una nueva instancia de PcontratosControlador
     */
    @SuppressWarnings("unchecked")
    public PcontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        try {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
    		nivelUsuario = SessionUtil.getNivelUsuario(modulo);
            // <INI_ADICIONAL>
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                vigencia = (String) parametrosEntrada.get(PARAMETRO_VIGENCIA);
                claseF = (String) parametrosEntrada.get(PARAMETRO_CLASEF);
                titulo = (String) parametrosEntrada.get(PARAMETRO_TITULO);
                aportantes = Boolean.parseBoolean(
                                SysmanFunciones.nvl(parametrosEntrada
                                                .get("convenio"), 0)
                                                .toString());
                obligaCampos = Boolean.parseBoolean(SysmanFunciones.nvl(parametrosEntrada
                                .get("obligaCampos"), 0)
                                .toString());
                
                habilitaActasInicio = Boolean.parseBoolean(SysmanFunciones.nvl(parametrosEntrada
                        .get("habilitaActasInicio"), 0)
                        .toString()); 

                visibleSujeto = Boolean.parseBoolean(SysmanFunciones.nvl(parametrosEntrada
                        .get("poseeEstampilla"), 0)
                        .toString());
                
                if (parametrosEntrada.get(LLAVE_PCONTRATO) != null) {
                    rid = (Map<String, Object>) parametrosEntrada
                                    .get(LLAVE_PCONTRATO);
                }
            }
            else {
                SessionUtil.redireccionarMenu();
            }
            lblTitulo = titulo;
            tipoContrato = claseF;
            numFormulario = GeneralCodigoFormaEnum.PCONTRATOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // </INI_ADICIONAL>
        }
        catch (SysmanException | NamingException ex) {
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
        cargarListaTercero();
        cargarListaDependencia();
        cargarListanumconvenio();
        cargarListaCopiarDe();
        cargarListaCEDULAINTERVENTOR();
        cargarListaClaseDisponibilidad();
        cargarListaAuxiliar();
        cargarListaEspecialidadSub();
        cargarListaEspecialidadSubE();
        cargarListaGrupoSub();
        cargarListaGrupoSubE();
        cargarListaCgrSegmento();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaOrdenador();
        cargarListaTipoAdjudicacion();
        cargarListasector();
        cargarListaTIPOLOGIA();
        cargarListaTipoContratoSECOP();
        cargarListaListaModelos();
        cargarListaDestino();
        cargarListaCOMPONENTEBP();
        cargarListaActividadSub();
        cargarListaCgrConcepto();
        cargarListaCgrTipoGasto();
        cargarListaFuenteRecurso();
        cargarListaCedAbog();
        cargarListaServidorPublico();
        cargarListaProfesion(); 
        cargarListatipoDiscapacidad();
        cargarListaPais();
        cargarListaDepartamento();
        cargarListaEstadoCivil();
        cargarListaNivelEmpleo();
        cargarListaNivelEducativo();
        cargarListaDepartamentoTrabajo();
        // </CARGAR_LISTA>

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaClasificacionproponentes();
        cargarListalistaRubros();
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
        listaClasificacionproponentes = null;
        cargarListalistaRubros();
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

        enumBase = GenericUrlEnum.ORDENDECOMPRA;
        buscarLlave();
        asignarOrigenDatos();
        cargarParametros();
 
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(PcontratosControladorEnum.PARAM0.getValue(), claseF);
		sigecVisible = false;

		Registro aux;
		try {
			aux = RegistroConverter.toRegistro(
                    requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PcontratosControladorUrlEnum.URL73058
                                                            .getValue())
                            .getUrl(), param));
			
			
			boolean valor = (boolean) aux.getCampos().get("ESTAMPILLA_SIGEC");

				if (valor) {
					sigecVisible = true;
				}

		} catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		Registro consaldo;
		try {
			consaldo = RegistroConverter.toRegistro(
                    requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PcontratosControladorUrlEnum.URL73062
                                                            .getValue())
                            .getUrl(), param));
			
			
			boolean valor = (boolean) consaldo.getCampos().get("CON_SALDO");

				if (valor) {
					mayoracero = true;
				}

		} catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
		}
        
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
        parametrosListado.put(PcontratosControladorEnum.PARAM0.getValue(),
                        claseF);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), vigencia);
        
        vigenciaContratoPorFirma = getParametro("MANEJA VIGENCIA DE CONTRATO POR FECHA DE FIRMA",
        		"NO").equals("SI");  

        if (vigenciaContratoPorFirma) {

        	
        	urlListado = UrlServiceUtil.getInstance()
        			.getUrlServiceByUrlByEnumID(
        					PcontratosControladorUrlEnum.URL82128.getValue());    
        	
        	
        }
    }

    /**
     * 
     * Carga la lista listaClasificacionproponentes
     */
    public void cargarListaClasificacionproponentes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos()
                                        .get(CAMPO_CLASEORDEN));
        param.put(GeneralParameterEnum.NUMERO.getName(), registro.getCampos()
                        .get(CAMPO_NUMERO));

        try {
            listaClasificacionproponentes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL30725
                                                                            .getValue())
                                            .getUrl(),
                                            param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            TABLA_CLASIFICACION_PROPONENTES));
        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaOrdenador
     */
    public void cargarListaOrdenador() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaOrdenador = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL32205
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
     * Carga la lista listaTipoAdjudicacion
     */
    public void cargarListaTipoAdjudicacion() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaTipoAdjudicacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL33289
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
     * Carga la lista listaSector
     * 
     */
    public void cargarListasector() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaSector = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL33892
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
     * Carga la lista listaTIPOLOGIA
     */
    public void cargarListaTIPOLOGIA() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTIPOLOGIA = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL34247
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
     * Carga la lista listaTipoContratoSECOP
     * 
     */
    public void cargarListaTipoContratoSECOP() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaTipoContratoSECOP = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL34641
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
     * Carga la lista listaDestino
     */
    public void cargarListaDestino() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaDestino = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL35446
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
     * Carga la lista listaCOMPONENTEBP
     */
    public void cargarListaCOMPONENTEBP() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), auxiliarColumn3);
        try {
            listaCOMPONENTEBP = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL35872
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
     * Carga la lista listaActividadSub
     */
    public void cargarListaActividadSub() {
        Map<String, Object> param = new TreeMap<>();
        try {
            listaActividadSub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL36429
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
     * Carga la lista listaCgrConcepto
     *
     */
    public void cargarListaCgrConcepto() {
    	
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                PcontratosControladorUrlEnum.URL795
                                                .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA",compania);

        listaCgrConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
        		urlBean.getUrlConteo().getUrl(),param,
                true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCgrTipoGasto
     *
     */
    public void cargarListaCgrTipoGasto() {
    	
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                                PcontratosControladorUrlEnum.URL827
                                                .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA",compania);

        listaCgrTipoGasto = new RegistroDataModelImpl(urlBean.getUrl(),
                urlBean.getUrlConteo().getUrl(),param,
                true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaGrupoSub
     */
    public void cargarListaGrupoSub() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL36430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(PcontratosControladorEnum.CODIGOGRUPO.getValue(),
                        especialidad);

        listaGrupoSub = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaGrupoSub
     */
    public void cargarListaGrupoSubE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL36430
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(PcontratosControladorEnum.CODIGOGRUPO.getValue(),
                        especialidad);

        listaGrupoSubE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaEspecialidadSub
     */
    public void cargarListaEspecialidadSub() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL36431
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

        listaEspecialidadSub = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaEspecialidadSub
     */
    public void cargarListaEspecialidadSubE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL36431
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ACTIVIDAD.getName(), actividad);

        listaEspecialidadSubE = new RegistroDataModelImpl(urlBean.getUrl(),
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
                                        PcontratosControladorUrlEnum.URL38432
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaDependencia
     */
    public void cargarListaDependencia() {
        String urldependencia = "SI".equals(filtroDependenciaMovimiento)
            ? PcontratosControladorUrlEnum.URL40630.getValue()
            : PcontratosControladorUrlEnum.URL40631.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urldependencia);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     * 
     * Carga la lista listaNumconvenio
     */
    public void cargarListanumconvenio() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL41225
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        listaNumconvenio = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_NUMERO);
    }

    /**
     * 
     * Carga la lista listaCopiarDe
     */
    public void cargarListaCopiarDe() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL43001
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);

        try {
            param.put("FECHAINI",
                            SysmanFunciones.convertirAFechaCadena(registro
                                            .getCampos()
                                            .get(CAMPO_FECHA) == null
                                                ? new Date()
                                                : (Date) registro
                                                                .getCampos()
                                                                .get(CAMPO_FECHA)));

            listaCopiarDe = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CAMPO_NUMERO);
        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaCEDULAINTERVENTOR
     */
    public void cargarListaCEDULAINTERVENTOR() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL44283
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCEDULAINTERVENTOR = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    /**
     * 
     * Carga la lista listaListaModelos
     * 
     */
    public void cargarListaListaModelos() {
        Map<String, Object> param = new TreeMap<>();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL35348
                                                        .getValue());
        listaListaModelos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     * 
     * Carga la lista listaCgrSegmento
     *
     */
    public void cargarListaCgrSegmento() {
        Map<String, Object> param = new TreeMap<>();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL1064
                                                        .getValue());
        listaCgrSegmento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     * 
     * Carga la lista listaClaseDisponibilidad
     */
    public void cargarListaClaseDisponibilidad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL44921
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaClaseDisponibilidad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);

    }

    /**
     * 
     * Carga la lista listaAuxiliar
     * 
     */
    public void cargarListaAuxiliar() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL45785
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     * 
     * Carga la lista listaModelos
     * 
     */
    public void cargarListaModelos() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL32819
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.CLASEORDEN
                                                        .getName())
                                        .toString());
        param.put(GeneralParameterEnum.TIPO.getName(),
                        2);
        listaModelos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CAMPO_CODIGO);
    }

    /**
     *
     * Carga la lista listalistaRubros.
     */
    public void cargarListalistaRubros() {
        claseDisponibilidaD = registro.getCampos()
                        .get(CAMPO_CLASEDISPONIBILIDAD) == null ? "DIS"
                            : registro.getCampos()
                                            .get(CAMPO_CLASEDISPONIBILIDAD)
                                            .toString();
        UrlBean urlBean;
        if (mayoracero) {
        	urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    PcontratosControladorUrlEnum.URL75067
                                                    .getValue());
        }
        else {
        	urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL39511
                                                        .getValue());
        }
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(GeneralParameterEnum.CLASE.getName(),
                        claseDisponibilidaD);
        String[] llaveRubros = null;
        try {
            llaveRubros = CacheUtil.getLlaveServicio(urlConexionCache,
                            "COMPROBANTE_PPTAL");
            listalistaRubros = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            false, llaveRubros,
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            if (claseDisponibilidaD != null) {

                String resDis;
                if ("DIS".equals(claseDisponibilidaD)) {
                    resDis = SysmanFunciones.nvl(registro.getCampos()
                                    .get(CAMPO_DISPONIBILIDAD),
                                    "").toString();
                }
                else if ("RES".equals(claseDisponibilidaD)) {
                    resDis = SysmanFunciones.nvl(
                                    registro.getCampos()
                                                    .get("RUBRORESERVA"),
                                    "").toString();
                }
                else {
                    resDis = "";
                }

                resDis = resDis == null ? ""
                    : resDis;
                String[] listanumeros = resDis.split(",");

                for (int i = 0; i < listanumeros.length; i++) {
                    Registro r = new Registro();
                    r.getCampos().put(CAMPO_COMPANIA, compania);
                    r.getCampos().put("ANO", vigencia);
                    r.getCampos().put("TIPO", claseDisponibilidaD);
                    r.getCampos().put(CAMPO_NUMERO, listanumeros[i]);
                    r.asignarLlave(llaveRubros);
                    listalistaRubros.getLlavesSeleccionadas().add(r.getLlave()
                                    .toString());

                    try {
                        listalistaRubros.getSeleccionados().add(listalistaRubros
                                        .getRegistroUnico(r.getCampos()));
                    }
                    catch (SystemException e) {
                        logger.error(e.getMessage(), e);
                        JsfUtil.agregarMensajeError(e.getMessage());
                    }
                }
            }

            JsfUtil.ejecutarJavaScript(FILTRAR_LM2S);
        }
    }
    
    public void cargarListaFuenteRecurso() {

         String urlEnum = PcontratosControladorUrlEnum.URL1223.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        listaFuenteRecurso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        CAMPO_CODIGO);
    	
    }
    
    public void cargarListaCedAbog() {
    	String urlEnum = PcontratosControladorUrlEnum.URL14205.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                         .getUrlServiceByUrlByEnumID(urlEnum);
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCedAbog = new RegistroDataModelImpl(urlBean.getUrl(),
                       urlBean.getUrlConteo().getUrl(), param, true,
                       GeneralParameterEnum.NIT.getName());
    }

    public void cargarvalorTotal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("TERCERO", registro.getCampos().get("TERCERO"));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                		registro.getCampos().get(CAMPO_CLASEORDEN));
        
        try {

            listaValorTotal = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL45691
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (listaValorTotal != null) {

                registro.getCampos().put("VALORTOTAL",
                                listaValorTotal.getCampos().get("VALORTOTAL"));
                
                registro.getCampos().put("VLR_DEVUELTO",
                				listaValorTotal.getCampos().get("VALORREINTEGRO"));
              
               if(!registro.getCampos().get("VLR_DEVUELTO").equals(registro.getCampos().get("VALORTOTAL")) ) {
            	   registro.getCampos().put("VACIA", "N");
               }else {
               registro.getCampos().put("VACIA",listaValorTotal.getCampos().get("VACIA"));
               }
               //registro.getCampos().put("VLR_DEVUELTO",registro.getCampos().get("VALORTOTAL"));
                
            }else {
            	registro.getCampos().put("VACIA", "N");
            }

        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarModificacionTiempo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CLASEORDEN", registro.getCampos().get("CLASEORDEN"));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        try {

            listaModificacionTiempo = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL45692
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

            if (listaModificacionTiempo != null) {

                modificacionTiempo = listaModificacionTiempo.getCampos()
                                .get("TOTAL");
                registro.getCampos().put("MODIFICACION_TIEMPO",
                                SysmanFunciones.nvl(modificacionTiempo, ""));
            }

        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>

    
    private void cargarListaDepartamentoTrabajo() {
    	  
   	 listaDepartamentoTrabajo = listaDepartamento;
   	}
   	private void cargarListaNivelEducativo() {
   		UrlBean urlBean = UrlServiceUtil.getInstance()
   				.getUrlServiceByUrlByEnumID(
   						PcontratosControladorUrlEnum.URL639007
   						.getValue());
   		Map<String, Object> param = new TreeMap<>();
   		param.put(GeneralParameterEnum.TIPO.getName(), "N");
   		listaNivelEducativo = new RegistroDataModelImpl(urlBean.getUrl(),
   				urlBean.getUrlConteo().getUrl(), param, true, "CODIGOPROF");
   	}
   	
   	private void cargarListaNivelEmpleo() {
   		 Map<String, Object> param = new TreeMap<>();
   	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
   	        try
   	        {
   	            listaNivelEmpleo = RegistroConverter
   	                            .toListRegistro(
   	                                            requestManager
   	                                                            .getList(
   	                                                                            UrlServiceUtil.getInstance()
   	                                                                                            .getUrlServiceByUrlByEnumID(
   	                                                                                                            PcontratosControladorUrlEnum.URL462001
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
   	
   	
   	private void cargarListaEstadoCivil() {
   		
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
                                                                                                             PcontratosControladorUrlEnum.URL615001
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

      	private void cargarListaCiudad() {
      		   Map<String, Object> param = new TreeMap<>();
      	        param.put("PAIS", codPais);
      	        param.put(GeneralParameterEnum.DEPARTAMENTO.getName(),codDepartamento);

   	        try
   	        {
   	            listaCiudad = RegistroConverter
   	                            .toListRegistro(
   	                                            requestManager
   	                                                            .getList(
   	                                                                            UrlServiceUtil.getInstance()
   	                                                                                            .getUrlServiceByUrlByEnumID(
   	                                                                                            		PcontratosControladorUrlEnum.URL5001
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

      	private void cargarListaDepartamento() {
      		
      		  Map<String, Object> param = new TreeMap<>();
      	        param.put("PAIS", codPais);

   	        try
   	        {
   	            listaDepartamento = RegistroConverter
   	                            .toListRegistro(
   	                                            requestManager
   	                                                            .getList(
   	                                                                            UrlServiceUtil.getInstance()
   	                                                                                            .getUrlServiceByUrlByEnumID(
   	                                                                                                            PcontratosControladorUrlEnum.URL2005
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

   	private void cargarListaPais() {
   		  try
   	        {
   	            listaPais = RegistroConverter
   	                            .toListRegistro(
   	                                            requestManager
   	                                                            .getList(
   	                                                                            UrlServiceUtil.getInstance()
   	                                                                                            .getUrlServiceByUrlByEnumID(
   	                                                                                                            PcontratosControladorUrlEnum.URL1001
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

   	private void cargarListatipoDiscapacidad() {
   		UrlBean urlBean = UrlServiceUtil.getInstance()
     			.getUrlServiceByUrlByEnumID(
     					PcontratosControladorUrlEnum.URL1948001
     					.getValue());
     	Map<String, Object> param = new TreeMap<>();

     	listatipoDiscapacidad = new RegistroDataModelImpl(urlBean.getUrl(),
     			urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");
   	}

   	private void cargarListaProfesion() {
   		UrlBean urlBean = UrlServiceUtil.getInstance()
     			.getUrlServiceByUrlByEnumID(
     					PcontratosControladorUrlEnum.URL639007
     					.getValue());
     	Map<String, Object> param = new TreeMap<>();
     	param.put(GeneralParameterEnum.TIPO.getName(), "P");

     	listaProfesion = new RegistroDataModelImpl(urlBean.getUrl(),
     			urlBean.getUrlConteo().getUrl(), param, true, "CODIGOPROF");		
   	}

   	private void cargarListaServidorPublico() {
   		UrlBean urlBean = UrlServiceUtil.getInstance()
     			.getUrlServiceByUrlByEnumID(
     					PcontratosControladorUrlEnum.URL1949001
     					.getValue());
     	Map<String, Object> param = new TreeMap<>();
     	listaServidorPublico = new RegistroDataModelImpl(urlBean.getUrl(),
     			urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");    	
   		
   	}
   	
    public void cambiarEscalafon()
    {
    	
    }
     
     private String retornarString(Registro reg, String campo)
     {
         return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
             : reg.getCampos().get(campo).toString();

     }
     
       
       /**
        * Metodo ejecutado al cambiar el control Pais
        * 
        * 
        */
     public void cambiarPais() {
	  codDepartamento = null;
	  codMunicipio = null;
	  codDepTrabajo = null;
        cargarListaDepartamento();
        cargarListaDepartamentoTrabajo();
     }
       /**
        * Metodo ejecutado al cambiar el control Departamento
        * 
        * 
        */
     public void cambiarDepartamento() {
         codMunicipio = null;
         cargarListaCiudad();
    }
    
    public void cambiarTipoAdjudicacion() {
        double salarioMinimo = Double.parseDouble(
                        SysmanFunciones.nvlStr(valorSalarioMinimo, "0"));
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get("TIPOADJUDICACION"));
        try {
            Registro regSalario = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL1113
                                                                            .getValue())
                                            .getUrl(), param));

            int valorSalario = Integer.parseInt(regSalario.getCampos()
                            .get("SALARIOS_MINIMOS_CUANTIA").toString());

            tipoAdjudicacionCuantia = salarioMinimo * valorSalario;
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void cambiarModificacionTiempo() {

    }

    /**
     * Metodo ejecutado al cambiar el control Ordenador
     */
    public void cambiarOrdenador() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("SUCURSAL_ORDENADOR", service
                        .buscarEnLista((String) registro.getCampos()
                                        .get("ORDENADOR"), "CEDULA",
                                        CAMPO_SUCURSAL,
                                        listaOrdenador));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control UnidadPlazo
     * 
     */
    public void cambiarUnidadPlazo() {
        // <CODIGO_DESARROLLADO>
        try {
            fecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get(CAMPO_FECHA),
                            FORMATO_FECHA);
            int unidadPlazo = getIntVal(CAMPO_UNIDAD_PLAZO);
            int plazoEntrega = getIntVal(CAMPO_PLAZODEENTREGA);

            if ((unidadPlazo == 1)
                && (registro.getCampos().get(CAMPO_TIPODIA) == null)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3697"));
            }
            Date fechaAux;
            if (unidadPlazo == 2) {
                fechaAux = SysmanFunciones.sumarRestarMesesFecha(
                                (Date) registro.getCampos().get(CAMPO_FECHA),
                                plazoEntrega);
                registro.getCampos().put(CAMPO_FECHAFINALIZACION, fechaAux);
                registro.getCampos().put(CAMPO_TIPODIA, null);
                diasVisible = false;
            }
            else if (unidadPlazo == 3) {
                fechaAux = SysmanFunciones.convertirAFecha(
                                fecha);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fechaAux);
                calendar.add(Calendar.YEAR, plazoEntrega);
                fechaAux = calendar.getTime();

                registro.getCampos().put(CAMPO_FECHAFINALIZACION, fechaAux);
                registro.getCampos().put(CAMPO_TIPODIA, null);
                diasVisible = false;
            }
            else {
                diasVisible = true;
            }

        }
        catch (ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Dias
     * 
     */
    public void cambiarDias() {
        // <CODIGO_DESARROLLADO>
        validarDiasCalendario();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TIPOLOGIA
     * 
     * 
     */
    public void cambiarTIPOLOGIA() {
        // <CODIGO_DESARROLLADO>
        int tipologia = Integer.parseInt(
                        registro.getCampos().get("TIPOLOGIA").toString());
        if (validarTipologia(tipologia)) {
            etNumPersonasVisible = true;
            numPersonasVisible = true;
            registro.getCampos().put(CAMPO_NUMPERSONASCONTRATADAS, 1);
        }
        else {
            etNumPersonasVisible = false;
            numPersonasVisible = false;
            registro.getCampos().put(CAMPO_NUMPERSONASCONTRATADAS, 0);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ActividadSub
     * 
     */
    public void cambiarActividadSub() {
        // <CODIGO_DESARROLLADO>
        actividad = SysmanFunciones
                        .nvl(registroSub.getCampos().get(CAMPO_ACTIVIDAD), "")
                        .toString();
        cargarListaEspecialidadSub();
        cargarListaGrupoSubE();
        registroSub.getCampos().put(CAMPO_ESPECIALIDAD, null);
        registroSub.getCampos().put(PcontratosControladorEnum.GRUPO.getValue(),
                        null);

        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarDiscapacitado() {
		if(!esDiscapacitado) {
			nombreDiscapacidad = null;
			codDiscapacidad = null;
		}
   }

    /**
     * Metodo ejecutado al cambiar el control Fecha
     * 
     */
    public void cambiarFecha() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(manejaNominaContratistas)) {
            String numero = (String) registro.getCampos().get(CAMPO_NUMERO);
            if (!revisarContratistas(manejaNominaContratistas, tipoContrato,
                            numero)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(MSG_CONTRATO_REGISTRADO));
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinalizacion
     * 
     */
    public void cambiarFechaFinalizacion() {
        try { // <CODIGO_DESARROLLADO>
            String numero = registro.getCampos().get(CAMPO_NUMERO).toString();
            String strFecha = "";
            String fechaFin = "";

            strFecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get(CAMPO_FECHA));
            fechaFin = SysmanFunciones.convertirAFechaCadena((Date) registro
                            .getCampos().get(CAMPO_FECHAFINALIZACION));

            if ("SI".equals(manejaNominaContratistas)
                && !revisarContratistas(manejaNominaContratistas, tipoContrato,
                                numero)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(MSG_CONTRATO_REGISTRADO));

            }
            registro.getCampos().put(CAMPO_UNIDAD_PLAZO, "1");
            int num = 0;
            if ("1".equals(SysmanFunciones.nvl(registro.getCampos().get(CAMPO_TIPODIA),"2"))) {
                registro.getCampos().put(CAMPO_TIPODIA, "1");
                diasVisible = true;
                num = ejbSysmanUtil.retornarDiasHabilesEntreFechas(
                                compania,
                                SysmanFunciones.convertirAFecha(strFecha),
                                SysmanFunciones.convertirAFecha(fechaFin),
                                false);
                
            }
            else {

            	diasVisible = true;
                registro.getCampos().put(CAMPO_TIPODIA, "2");
            	num = SysmanFunciones.calcularDiferenciaDias(
                                SysmanFunciones.convertirAFecha(strFecha),
                                SysmanFunciones.convertirAFecha(fechaFin));
            	
            	if ("SI".equals(manejaMeses30dias)) {
            		num = num + (calcularAcumulado(num)*-1);
                }
            	
            }
            registro.getCampos().put(CAMPO_PLAZODEENTREGA, num);
            
        }
        catch (ParseException | SystemException ex) {
            Logger.getLogger(PcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MSG_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control mismodia
     * 
     */
    public void cambiarmismodia() {

        if (mismoDia) {
            fechaInicio = false;
        }
        if (!SysmanFunciones.nvl(registro.getCampos().get(CAMPO_TIPODIA), "")
                        .toString().isEmpty()) {
            validarDiasCalendario();
        }
    }

    /**
     * Metodo ejecutado al cambiar el control fechainicio
     * 
     */
    public void cambiarfechainicio() {
        if (fechaInicio) {
            mismoDia = false;
        }

        if (!SysmanFunciones.nvl(registro.getCampos().get(CAMPO_TIPODIA), "")
                        .toString().isEmpty()) {
            validarDiasCalendario();
        }
    }

    /**
     * Metodo ejecutado al cambiar el control PlazoDeEntrega
     * 
     */
    public void cambiarPlazoDeEntrega() {
        // <CODIGO_DESARROLLADO>
        int plazoEntrega = getIntVal(CAMPO_PLAZODEENTREGA);
        if (plazoEntrega < 0) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2084"));
            registro.getCampos().put(CAMPO_PLAZODEENTREGA, "");
            return;
        }
        if (registro.getCampos().get(CAMPO_UNIDAD_PLAZO) == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2085"));
            return;
        }

        if ("1".equals(registro.getCampos().get(CAMPO_TIPODIA))) {
            if (String.valueOf(plazoEntrega).length() <= 4) {
                int nDias = calcularNumeroDias(calcularUnidadPlazo(),
                                plazoEntrega);
                Date fechaFinal = traerFechaFinal(nDias);
                registro.getCampos().put(CAMPO_FECHAFINALIZACION, fechaFinal);
            }
        }
        else {
            registro.getCampos().put(CAMPO_FECHAFINALIZACION,
                            getFechaFinalizacion(plazoEntrega));
        }

        if (!SysmanFunciones.nvl(registro.getCampos().get(CAMPO_TIPODIA), "")
                        .toString().isEmpty()) {
            validarDiasCalendario();
        }
        else if (!SysmanFunciones
                        .nvl(registro.getCampos().get(CAMPO_UNIDAD_PLAZO), "")
                        .toString().isEmpty()) {
            cambiarUnidadPlazo();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control txtValorTotal
     * 
     */
    public void cambiartxtValorTotal() {
        // <CODIGO_DESARROLLADO>
        /* 113015 */
        Registro valorPPTO = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        try {
            valorPPTO = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61658
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        double dblPPTO = 0;
        if (valorPPTO != null) {
            dblPPTO = Double.parseDouble(
                            valorPPTO.getCampos().get("VALORPPTO") == null
                                ? "0.0"
                                : (String) valorPPTO.getCampos()
                                                .get("VALORPPTO"));
        }
        double dblTotal = Double.parseDouble(
                        registro.getCampos().get(CAMPO_VALORTOTAL) == null ? "0"
                            : (String) registro.getCampos()
                                            .get(CAMPO_VALORTOTAL));
        if (dblTotal > dblPPTO) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2086"));
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorFinal
     * 
     * 
     */
    public void cambiarValorFinal() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(controlContratosValorCuantia)) {
            int salarioMinimo;
            int valorCuantia;
            int dblFinal = Integer.parseInt(
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(CAMPO_VALORFINAL),
                                            "0").toString());
            if (SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(CAMPO_TIPOADJUDICACION), "")
                            .toString().isEmpty()) {
                salarioMinimo = 0;
            }
            else {
                salarioMinimo = Integer
                                .parseInt(service
                                                .buscarEnLista((String) registro
                                                                .getCampos()
                                                                .get(CAMPO_TIPOADJUDICACION),
                                                                CAMPO_CODIGO,
                                                                "SALARIOS_MINIMOS_CUANTIA",
                                                                listaTipoAdjudicacion));
            }

            valorCuantia = Integer.parseInt(valorSalarioMinimo)
                * salarioMinimo;
            if (valorCuantia < dblFinal) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2087"));
                registro.getCampos().put(CAMPO_VALORFINAL, 0);
                return;
            }
        }
        else {
            registro.getCampos().put(CAMPO_VALORTOTAL,
                            registro.getCampos().get(CAMPO_VALORFINAL));
        }
        // Revisa liquidacion pago contratistas
        if ((registro.getCampos().get(CAMPO_NUMERO) == null)
            && ("SI".equals(manejaNominaContratistas))) {
            String numero = (String) registro.getCampos().get(CAMPO_NUMERO);
            if (!revisarContratistas(manejaNominaContratistas, tipoContrato,
                            numero)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(MSG_CONTRATO_REGISTRADO));
            }
        }
        registro.getCampos().put(CAMPO_VALORTOTAL,
                        registro.getCampos().get(CAMPO_VALORFINAL));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Texto219
     * 
     */
    public void cambiarTexto219() {
        // <CODIGO_DESARROLLADO>
        double dblTotal = Double.parseDouble(
                        registro.getCampos().get(CAMPO_VALORTOTAL) == null ? "0"
                            : (String) registro.getCampos()
                                            .get(CAMPO_VALORTOTAL));
        double monedaRef = Double.parseDouble(
                        registro.getCampos().get("MONEDAREF") == null ? "0"
                            : (String) registro.getCampos().get("MONEDAREF"));
        registro.getCampos().put("VALORMONEDAREF", dblTotal / monedaRef);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorcAnticipio
     * 
     */
    public void cambiarPorcAnticipio() {
        // <CODIGO_DESARROLLADO>
        double dblPorcAnticipo = getDblVal(CAMPO_PORC_ANTICIPO);
        double valorFinal = getDblVal(CAMPO_VALORFINAL);
        registro.getCampos().put(CAMPO_VALOR_ANTICIPO,
                        (dblPorcAnticipo / 100) * valorFinal);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ValorAnticipio
     * 
     */
    public void cambiarValorAnticipio() {
        // <CODIGO_DESARROLLADO>
        double dblValorAnticipo = getDblVal(registro, CAMPO_VALOR_ANTICIPO);
        double valorFinal = Double.parseDouble(
                        registro.getCampos().get(CAMPO_VALORFINAL) == null ? "0"
                            : registro.getCampos()
                                            .get(CAMPO_VALORFINAL).toString());
        registro.getCampos()
                        .put(CAMPO_PORC_ANTICIPO, SysmanFunciones.redondear(
                                        dblValorAnticipo / valorFinal, 4)
                            * 100);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Texto719
     * 
     */
    public void cambiarTexto719() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(fechaFirmaCierreAdjudicacionIgual)) {
            registro.getCampos().put("FECHACIERRE",
                            registro.getCampos().get(CAMPO_FECHAFIRMA));
            registro.getCampos().put("FECHAADJUDICACION",
                            registro.getCampos().get(CAMPO_FECHAFIRMA));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorcIVAGlobal
     * 
     */
    public void cambiarPorcIVAGlobal() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61655
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (reg != null) {
            mensajeActualizaIVA = "Cambiamos el Iva al "
                + registro.getCampos().get(CAMPO_PORCIVAGLOBAL)
                + " % a todos los items";
            muestraDialogoActualizaIva = true;
        }
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control PorAdministracion
     * 
     * 
     */
    public void cambiarPorAdministracion() {
    	//<CODIGO_DESARROLLADO>
    	sumaPorcentajes();
    	//</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control PorImprevistos
     * 
     * 
     */
    public void cambiarPorImprevistos() {
    	//<CODIGO_DESARROLLADO>
    	sumaPorcentajes();
    	//</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado al cambiar el control PorUtilidad
     * 
     * 
     */
    public void cambiarPorUtilidad() {
    	//<CODIGO_DESARROLLADO>
    	sumaPorcentajes();
    	//</CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CONVENIO
     * 
     */
    public void cambiarCONVENIO() {
        // <CODIGO_DESARROLLADO>
        convenio = (boolean) registro.getCampos().get(CAMPO_CONVENIO);
        numconvenioEnabled = !convenio;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ACTUALIZAPLANDECOMPRAS
     * 
     */
    public void cambiarACTUALIZAPLANDECOMPRAS() {
        // <CODIGO_DESARROLLADO>
        impreso = registro.getCampos().get(CAMPO_IMPRESO) == null ? false
            : (boolean) registro.getCampos().get(CAMPO_IMPRESO);
        if (impreso) {
            if (nivelUsuario == 9) {
                actualizaPlanDeComprasLocked = false;
            }
            else {
                actualizaPlanDeComprasLocked = true;
            }
        }
        boolean actualizaPlandeCompras = registro.getCampos()
                        .get(CAMPO_ACTUALIZAPLANDECOMPRAS) == null ? false
                            : (boolean) registro.getCampos()
                                            .get(CAMPO_ACTUALIZAPLANDECOMPRAS);
        if (!actualizaPlandeCompras) {
            itemsCEnabled = false;
            contratistaInterventorEnabled = false;
            informacionJuridicaEnabled = false;
            datosDelContratoEnabled = false;
            informacionPresupuestalEnabled = false;
            pagina33Enabled = false;
            terceroLocked = false;
            fechaDiligenciamientoLocked = false;
            diasLocked = false;
            plazoEntregaLocked = false;
            unidadPlazoLocked = false;
            lugarDeEntregaLocked = false;
            tipoAdjudicacionLocked = false;
            sectorLocked = false;
            tipoContratosLocked = false;
            tipoVeeduriaLocked = false;
            tipologiaLocked = false;
            numeroActaLocked = false;
            claseContratoLocked = false;
            descripcionLocked = false;
            formaDePagoLocked = false;
            fechaLocked = false;
            objetoContratoLocked = false;
            garantiaLocked = false;
            fechaFinalizacionLocked = false;
            duracionLocked = false;
            dependenciaLocked = false;
            codEquivalenteLocked = false;
            considerandosLocked = false;
            txtValorTotalLocked = false;
            valorFinalLocked = false;
            texto217Locked = false;
            texto219Locked = false;
            formaDePagoLocked = false;
            interventorLocked = false;
            valorTotalLocked = false;
        }
        else {
            itemsCEnabled = true;
            contratistaInterventorEnabled = true;
            informacionJuridicaEnabled = true;
            datosDelContratoEnabled = true;
            informacionPresupuestalEnabled = true;
            pagina33Enabled = true;
            terceroLocked = true;
            fechaDiligenciamientoLocked = true;
            diasLocked = true;
            plazoEntregaLocked = true;
            unidadPlazoLocked = true;
            lugarDeEntregaLocked = true;
            tipoAdjudicacionLocked = true;
            sectorLocked = true;
            tipoContratosLocked = true;
            tipoVeeduriaLocked = true;
            tipologiaLocked = true;
            numeroActaLocked = true;
            claseContratoLocked = true;
            descripcionLocked = true;
            formaDePagoLocked = true;
            fechaLocked = true;
            objetoContratoLocked = true;
            garantiaLocked = true;
            fechaFinalizacionLocked = true;
            duracionLocked = true;
            dependenciaLocked = true;
            codEquivalenteLocked = true;
            considerandosLocked = true;
            txtValorTotalLocked = true;
            valorFinalLocked = true;
            texto217Locked = true;
            texto219Locked = true;
            formaDePagoLocked = true;
            interventorLocked = true;
            valorTotalLocked = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EMERGENCIAIN
     * 
     */
    public void cambiarEMERGENCIAIN() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        String[] campos = { "rid", "claseOrden", "numeroOrden",
                            PARAMETRO_VIGENCIA, PARAMETRO_CLASEF,
                            PARAMETRO_TITULO };
        Object[] valores = { css, registro.getCampos()
                        .get(GeneralParameterEnum.CLASEORDEN.getName()),
                             registro.getCampos()
                                             .get(GeneralParameterEnum.NUMERO
                                                             .getName()),
                             vigencia, tipoContrato, titulo };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.URGENCIAMANIFIESTAS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
    }

    /**
     * Metodo ejecutado al cambiar el control Impreso
     * 
     */
    public void cambiarImpreso() {
        // <CODIGO_DESARROLLADO>
        impreso = registro.getCampos().get(CAMPO_IMPRESO) == null ? false
            : (boolean) registro.getCampos().get(CAMPO_IMPRESO);
        if (impreso) {
            actualizaPlanDeComprasLocked = nivelUsuario != 9;
            if (!terceroLocked) {
                terceroLocked = true;
                porcIVAGlobalLocked = true;
                porcDescGlobalLocked = true;
                subPContratoPermiteAgregar = false;
                subPContratoPermiteEliminar = false;
                subPContratoPermiteEditar = false;
                ordenDeServicioPage1Enabled = true;
                ordenDeServicioPage2Enabled = true;
                ordenDeServicioPage3Enabled = true;
                fechaLocked = true;
                fechaFinalizacionLocked = true;
                duracionLocked = true;
                dependenciaLocked = true;
                codEquivalenteLocked = true;
                objetoContratoLocked = true;
                descripcionLocked = true;
                formaDePagoLocked = true;
                interventorLocked = true;
                txtValorTotalLocked = true;
                valorTotalLocked = true;
                seleccionarRequisicionesEnabled = false;
            }
        }
        else {
            String estado = SysmanFunciones.nvlStr(
                            (String) registro.getCampos().get("ESTADO"), "");
            if ("A".equals(estado)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2069"));
                return;
            }
            if (terceroLocked) {
                terceroLocked = false;
                porcIVAGlobalLocked = false;
                porcDescGlobalLocked = false;
                subPContratoPermiteAgregar = true;
                subPContratoPermiteEliminar = true;
                subPContratoPermiteEditar = true;
                ordenDeServicioPage1Enabled = false;
                ordenDeServicioPage2Enabled = false;
                ordenDeServicioPage3Enabled = false;
                fechaLocked = false;
                fechaFinalizacionLocked = false;
                duracionLocked = false;
                dependenciaLocked = false;
                codEquivalenteLocked = false;
                objetoContratoLocked = false;
                descripcionLocked = false;
                formaDePagoLocked = false;
                interventorLocked = false;
                txtValorTotalLocked = false;
                valorTotalLocked = false;
                seleccionarRequisicionesEnabled = false;

            }
        }
        impresoEnabled = !impreso && (nivelUsuario != 9);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control extraerValor
     * 
     */
    public void cambiarextraerValor() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control actualiza
     * 
     */
    public void cambiaractualiza() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control dialogoConfirmar
     * 
     */
    public void cambiardialogoConfirmar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control eliminarInfPptal
     * 
     */
    public void cambiareliminarInfPptal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Ejecuta el procedimiento que cambia el consecutivo en
     * contratos.
     *
     * @param nuevoNumero
     * Nuevo consecutivo.
     */
    private void cambiarConsecutivo(String nuevoNumero) {
        // Llama procedimiento

        try {

            ejbContratosCero.cambiarConsecutivoContrato(compania, claseF,
                            Integer.parseInt(vigencia),
                            Long.parseLong(registro.getCampos()
                                            .get(CAMPO_NUMERO).toString()),
                            Long.parseLong(nuevoNumero), usuario);
            
            if(informacionFuncionalEnabled) {
            Map<String, Object> fields = new TreeMap<>();
            fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            fields.put(GeneralParameterEnum.TIPO.getName(),
                            registro.getCampos().get(CAMPO_CLASEORDEN));
            fields.put(PcontratosControladorEnum.NUMERO_CONTRATO.getValue(),
                            registro.getCampos().get(CAMPO_NUMERO));
            fields.put(GeneralParameterEnum.NUMERO.getName(),
            				Long.parseLong(nuevoNumero));

            UrlBean urlActualiza = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PcontratosControladorUrlEnum.URL1959002
                                                            .getValue());
            
                requestManager.update(urlActualiza.getUrl(),
                                urlActualiza.getMetodo(), fields, fields);
            }
            

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que se ejecuta al cerrar el formulario modal Forma de
     * Pago.
     *
     * @param event
     */
    public void retornarFormularioBtnFormaDePago() {
        // <CODIGO_DESARROLLADO>
        // Recarga el registro, en caso de que se halla modificado
        cargarRegistro(css, ACCION_MODIFICAR);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CambiarTipoContrato
     * 
     */
    public void retornarFormularioCambiarTipoContrato(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getObject() == null) {
            return;
        }
        Direccionador direccionador = (Direccionador) event.getObject();
        String ruta = direccionador.getRuta();
        if (direccionador.getNumForm() != null) {
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
        else if ((ruta != null) && (direccionador.getParametros() != null)) {
            SessionUtil.redireccionar(direccionador);
        }
        else if (ruta != null) {
            SessionUtil.redireccionar(ruta);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control importatodo
     * 
     */
    public void retornarFormularioimportatodo() {
        // <CODIGO_DESARROLLADO>
        cargarRegistro(css, ACCION_MODIFICAR);
        if (Boolean.parseBoolean(
                        registro.getCampos().get(CAMPO_IMPORTARPRE)
                                        .toString())) {
            importatodoEnabled = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control TercerosAportantes
     * 
     */
    public void retornarFormularioTercerosAportantes() {
        // <CODIGO_DESARROLLADO>
        /* PARA REVISAR POSIBLE TRIGGER */
        registro.getCampos().put("APORTESTE",
                        traerAportesTercero());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * extraerValor en la vista
     *
     */
    public void aceptarextraerValor() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("VALORFINAL", evalValor);
        cambiarValorFinal();
        cambiarPorcAnticipio();
        cambiarValorAnticipio();
        agregarRegistroNuevo(false);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * actualiza en la vista
     *
     */
    public void aceptaractualiza() {
        // <CODIGO_DESARROLLADO>

        String numero = SysmanFunciones
                        .nvl(registro.getCampos().get(CAMPO_NUMERO), "")
                        .toString();
        actualizarCompras(numero);
        muestraDialogoActualiza = false;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * actualiza en la vista
     *
     */
    public void cancelaractualiza() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(CAMPO_ACTUALIZAPLANDECOMPRAS, false);
        muestraDialogoActualiza = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * actualizaIVASubPcontrato en la vista
     *
     */
    public void aceptaractualizaIVASubPcontrato() {
        // <CODIGO_DESARROLLADO>

        boolean salida;

        try {
            salida = ejbContratosUno.actualizaIvaDetalle(compania,
                            registro.getCampos().get(CAMPO_CLASEORDEN)
                                            .toString(),
                            Long.parseLong(registro.getCampos()
                                            .get(CAMPO_NUMERO).toString()),
                            new BigDecimal(registro.getCampos()
                                            .get(CAMPO_PORCIVAGLOBAL)
                                            .toString()),
                            redondearValorIvaOC,
                            redondearValorTotalOC,
                            redondearUnitarioIvaOC,
                            new BigDecimal(txtDigRedoValorUnitarioIVA),
                            new BigDecimal(Integer.toString(
                                            txtDigitosRedondeoValorIVA)),
                            new BigDecimal(txtDigRedonTotal), usuario);

            if (salida) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3778"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        extraerTotales();
        muestraDialogoActualizaIva = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogoConfirmar en la vista
     *
     */
    public void aceptardialogoConfirmar() {
        // <CODIGO_DESARROLLADO>
        limpiarOrdenDeCompra();
        ejecutarInsertarRubros();
        muestraDialogoConfirmar = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     */
    public void cancelardialogoConfirmar() {
        ejecutarInsertarRubros();
        muestraDialogoConfirmar = false;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * eliminarInfPptal en la vista
     *
     */
    public void aceptareliminarInfPptal() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CAMPO_CLASEORDEN));
        fields.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL1649
                                                        .getValue());
        try {
            requestManager.delete(urlDelete.getUrl(),
                            fields);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarRegistro(css, ACCION_MODIFICAR);
        disponibilidadConcatenado = getCadenaRubros("DIS");
        reservaConcatenado = getCadenaRubros("RES");
        cargarListalistaRubros();
        if (!listalistaRubros.getSeleccionados().isEmpty()) {
            listalistaRubros.getSeleccionados().clear();
            listalistaRubros.getLlavesSeleccionadas().clear();
        }

        muestraEliminarInfPptal = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * cambiarConsecutivo en la vista
     *
     * 
     */
    public void aceptarcambiarConsecutivo() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(controlaTamanioNumeroContrato)
            && (nuevoConsecutivo.length() != 8)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2757"));
            muestraCambiarConsecutivo = false;
            return;
        }

        // Aceptar del dialogo
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        nuevoConsecutivo);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61658
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rs == null) {
            cambiarConsecutivo(nuevoConsecutivo);
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2758"));
        }
        muestraCambiarConsecutivo = false;

        Map<String, Object> nuevaLlave = css;
        nuevaLlave.put("KEY_NUMERO", nuevoConsecutivo);
        cargarRegistro(nuevaLlave, accion);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ActividadSub en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarActividadSubC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        actividad = listaClasificacionproponentes.get(rowNum).getCampos()
                        .get(CAMPO_ACTIVIDAD) == null
                            ? " "
                            : listaClasificacionproponentes.get(rowNum)
                                            .getCampos()
                                            .get(CAMPO_ACTIVIDAD)
                                            .toString();

        if (cargarSub) {
            especialidad = null;
            listaClasificacionproponentes.get(rowNum).getCampos()
                            .put(CAMPO_ESPECIALIDAD, especialidad);
            listaClasificacionproponentes.get(rowNum).getCampos().put(
                            PcontratosControladorEnum.GRUPO.getValue(),
                            null);
        }
        else {
            cargarSub = true;
        }
        cargarListaEspecialidadSubE();
        cargarListaGrupoSubE();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EspecialidadSub en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEspecialidadSubC(int rowNum) {
        // <CODIGO_DESARROLLADO>

        if (indiceClasificacionproponentes == -1) {
            listaClasificacionproponentes.get(rowNum).getCampos()
                            .put(PcontratosControladorEnum.GRUPO.getValue(),
                                            null);
        }
        indiceClasificacionproponentes = -1;
        especialidad = listaClasificacionproponentes.get(rowNum).getCampos()
                        .get(CAMPO_ESPECIALIDAD) == null
                            ? " "
                            : listaClasificacionproponentes.get(rowNum)
                                            .getCampos()
                                            .get(CAMPO_ESPECIALIDAD)
                                            .toString();

        cargarListaGrupoSubE();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
    	codDiscapacidad = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
    	nombreDiscapacidad = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
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
    public void seleccionarFilaNivelEducativo(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	codNivelEducativo = SysmanFunciones.toString(registroAux.getCampos().get("CODIGOPROF"));
    	nombreNivelEducativo = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_PROFESION"));
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
    	codProfesion = SysmanFunciones.toString(registroAux.getCampos().get("CODIGOPROF"));
    	nombreProfesion = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE_PROFESION"));
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
    	codServPublico = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
    	nombreServPublico = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaGrupoSub
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaGrupoSub(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put("GRUPO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaGrupoSub
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaGrupoSubE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEspecialidadSub
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEspecialidadSub(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(CAMPO_ESPECIALIDAD, registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
        especialidad = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        registroSub.getCampos().put(PcontratosControladorEnum.GRUPO.getValue(),
                        null);
        cargarListaGrupoSub();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEspecialidadSub
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEspecialidadSubE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        especialidad = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        registroSub.getCampos().put(PcontratosControladorEnum.GRUPO.getValue(),
                        null);
        cargarListaGrupoSubE();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_TERCERO,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put(CAMPO_SUCURSAL,
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
        registro.getCampos().put(CAMPO_NOMBRECONTRATISTA,
                        registroAux.getCampos().get(CAMPO_NOMBRE));
        registro.getCampos().put("CEDULACONTRATISTA",
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("SUCURSALCONTRATISTA",
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
        nombreTercero = (String) registroAux.getCampos().get(CAMPO_NOMBRE);

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
        codDependencia = SysmanFunciones.toString(registroAux.getCampos().get(CAMPO_CODIGO));
        nombreDependencia = SysmanFunciones.toString(registroAux.getCampos().get(CAMPO_NOMBRE));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaNumconvenio
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaNumconvenio(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_NUM_CONVENIO,
                        registroAux.getCampos().get(CAMPO_NUMERO));
        if (registro.getCampos().get(CAMPO_NUM_CONVENIO) == null) {
            registro.getCampos().put("CLASE_CONVENIO", "");
        }
        else {
            registro.getCampos().put("CLASE_CONVENIO",
                            registroAux.getCampos().get("TIPO"));
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCopiarDe
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCopiarDe(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        copiarDe = registroAux.getCampos().get(CAMPO_NUMERO).toString();

        if ((copiarDe == null) || "".equals(copiarDe)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2082"));
        }
        else {
            copiarContratos(
                            registroAux.getCampos().get(CAMPO_CLASEORDEN)
                                            .toString());
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCEDULAINTERVENTOR
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCEDULAINTERVENTOR(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_CEDULAINTERVENTOR,
                        registroAux.getCampos().get("NIT"));
        registro.getCampos().put("INTERVENTOR",
                        registroAux.getCampos().get(CAMPO_NOMBRE));
        registro.getCampos().put("SUCURSALINTERVENTOR",
                        registroAux.getCampos().get(CAMPO_SUCURSAL));
    }
    
    public void seleccionarFilaFuenteRecurso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("FUENTEDERECURSO",
                        registroAux.getCampos().get(CAMPO_CODIGO));
    }
    
    public void seleccionarFilaCedAbog(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put("ABOGADOENCARG", registroAux.getCampos().get(GeneralParameterEnum.NIT.getName()));
    	nombreAbog = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    	cargoAbog = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CARGO.getName()),"").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaListaModelos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaListaModelos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        if (registroAux != null) {
            modelo = SysmanFunciones
                            .nvl(registroAux.getCampos().get(CAMPO_CODIGO), "")
                            .toString();
            try {
                String strFecha = (String) registroAux.getCampos()
                                .get(CAMPO_FECHA);
                if (!SysmanFunciones.validarVariableVacio(strFecha)) {
                    fechaFormatoPoliza = SysmanFunciones.formatearFecha(
                                    SysmanFunciones.convertirAFecha(strFecha,
                                                    FORMATO_FECHA));
                }
            }
            catch (ParseException ex) {
                Logger.getLogger(PcontratosControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(MSG_TRANS_INTERRUMPIDA)
                                    + ex.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2081"));
        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCgrSegmento
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCgrSegmento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CGR_SEGMENTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCgrConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCgrConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CGR_CONCEPTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCgrTipoGasto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCgrTipoGasto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CGR_TIPO_GASTO", registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()));
    }
    
    
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseDisponibilidad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseDisponibilidad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_CLASEDISPONIBILIDAD,
                        registroAux.getCampos().get(CAMPO_CODIGO));
        claseDisponibilidaD = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CLASE"), "")
                        .toString();

        cargarListalistaRubros();
        listalistaRubros.getSeleccionados().clear();
        listalistaRubros.getLlavesSeleccionadas().clear();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * PLATAFORMA
     *
     * @param event
     * objeto que encapsula la accion proveniente del combo
     */
    public void seleccionarFilaPlataforma(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PLATAFORMA",
                        registroAux.getCampos().get("CODIGO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAuxiliar
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(CAMPO_AUXILIAR,
                        registroAux.getCampos().get(CAMPO_CODIGO));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModelos
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModelos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modeloPlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CAMPO_CODIGO), "")
                        .toString();
        try {
            fechaFormato = SysmanFunciones.formatearFecha(
                            SysmanFunciones.convertirAFecha(
                                            SysmanFunciones
                                                            .nvl(registroAux.getCampos()
                                                                            .get(CAMPO_FECHA),
                                                                            "")
                                                            .toString(),
                                            FORMATO_FECHA));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listalistaRubros.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilalistaRubros(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista FilaCDC.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCDC(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_CONTRATO_CAS",
                        registroAux.getCampos().get("SIGLA"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnEnviarSIGEC en la vista
     *
     */
	public String oprimirBtnEnviarSIGEC() {
		// <CODIGO_DESARROLLADO>
		/*
		 * RUTINA PARA A. SERVICIO REPORTE DE ACTO/DOCUMENTO
		 */
		String url;
		String token = null;
		String log = null;
		archivoDescarga = null;

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST GATEWAY SIGEC", "1", new Date(), false);
			token = ejbSysmanUtil.consultarParametro(compania, "TOKEN AUTORIZACION SIGEC", "1", new Date(), false);
		
			log = "|---------------         LOG DE LOGICA SERVICIO ACTO DOCUMENTO / SIGEC        ---------------|";

			log = log + "\n" + servicioActo_Documento(token, url);
			
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "Log.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return log;
		// </CODIGO_DESARROLLADO>
	}

	private String servicioActo_Documento(String token, String url) {
		String respuesta = "";
		String json = "";

		Map<String, Object> params = new TreeMap<>();
		SimpleDateFormat formatFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), registro.getCampos().get(CAMPO_TERCERO));
		params.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
		params.put(GeneralParameterEnum.NUMERO.getName(), 
                registro.getCampos().get(CAMPO_NUMERO));
		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PcontratosControladorUrlEnum.URL001.getValue())
											.getUrl(),
									params));

			ParametrosSIGEC param = new ParametrosSIGEC();
			String fechaInicio = formatFecha.format(rs.getCampos().get(PcontratosControladorEnum.FECHAINICIO.getValue()));
			String fechaFin = formatFecha.format(rs.getCampos().get(PcontratosControladorEnum.FECHAFINALIZACION.getValue()));
			BigInteger valorBigInteger = (BigInteger) rs.getCampos().get(PcontratosControladorEnum.PLATAFORMA.getValue());
			BigInteger valorBigInteger1 = (BigInteger) rs.getCampos().get(PcontratosControladorEnum.VALORTOTAL.getValue());
			
			Integer plataforma = valorBigInteger.intValue();
			Integer valorTotal = valorBigInteger1.intValue();			
			
			param.setPlatform(plataforma);
			param.setActDocumentCode(SysmanFunciones.nvl(rs.getCampos().get(PcontratosControladorEnum.EQUIV_SIGEC.getValue()), "").toString());
			param.setGeneratorFactValue(valorTotal);
			param.setPayerDocumentParametricTypeCode(SysmanFunciones.nvl(rs.getCampos().get(PcontratosControladorEnum.SIGEC.getValue()), "").toString());
			param.setTaxpayerDocumentNumber(SysmanFunciones.nvl(rs.getCampos().get(PcontratosControladorEnum.TERCERO.getValue()), "").toString());
			param.setTaxpayerName(SysmanFunciones.nvl(rs.getCampos().get(PcontratosControladorEnum.NOMBRE.getValue()), "").toString());
			param.setGeneratorFactStartDate(fechaInicio);
			param.setGeneratorFactEndDate(fechaFin);
			param.setParametricActDocumentCodeType(SysmanFunciones.nvl(rs.getCampos().get(PcontratosControladorEnum.TIPO_SIGEC.getValue()), "").toString());

			Gson gson = new Gson();
			json = gson.toJson(param, ParametrosSIGEC.class);
			APISIGEC apiSigec = new APISIGEC();

			respuesta = apiSigec.postActoDocumento(token, url, json);
			RespuestaApiSigec respuestaApiSigec = gson.fromJson(respuesta, RespuestaApiSigec.class);
			respuestaApiSigec.getMessage();

		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta +  "\n"  +
				"|------------------------------- JSON --------------------------------------------|"
				+ "\n" + json;

	}

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnFormaDePago en la vista
     *
     */
    public void oprimirBtnFormaDePago() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "tipoContrato", "valorTotalContrato",
                            "numeroContrato" };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CAMPO_VALORTOTAL)),
                             String.valueOf(registro.getCampos()
                                             .get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.FORMADEPAGOS_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Nov en la vista
     *
     */
    public void oprimirNov() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.SUBNOVEDADPCONTRATOS_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
  
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Doc en la vista
     *
     */
    public void oprimirDoc() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.DOCUMENTOS_PCONTRATO_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Obrapublica en la vista
     *
     */
    public void oprimirObrapublica() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatosFlash(
                        String.valueOf(GeneralCodigoFormaEnum.OBRAPUBLICA_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Arrendamientof en la vista
     *
     */
    public void oprimirArrendamientof(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        throw new UnsupportedOperationException(
                        "Sin implementar llamada a formulario Arrendamiento");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdCopiarDe en la vista
     *
     */
    public void oprimircmdCopiarDe() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        if ((registro.getCampos().get(CAMPO_NUMERO) != null) && (claseF != null)
            && (css != null)) {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CAMPO_NUMERO));

            Registro rs = null;
            try {
                rs = RegistroConverter.toRegistro(
                                requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                PcontratosControladorUrlEnum.URL61655
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            if (rs != null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2070"));
                copiarDeEnabled = true;
            }
            else {
                copiarDeEnabled = false;
                copiarDe = "";
                fecha = SysmanFunciones.formatearFecha(
                                (Date) registro.getCampos()
                                                .get(CAMPO_FECHA));
                cargarListaCopiarDe();
            }
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2071"));
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CambiarTipoContrato en la
     * vista
     *
     */
    public void oprimirCambiarTipoContrato() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_VIGENCIA };
        String[] valores = { tipoContrato, vigencia };
        SessionUtil.cargarModal(
                        String.valueOf(GeneralCodigoFormaEnum.PERIODO_CONTRATOS_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btNotaCredito
     * en la vista
     *
     *
     */
    public void oprimirbtNotaCredito() {
    	//<CODIGO_DESARROLLADO>       
    	agregarRegistroNuevo(false);
    	Map<String, Object> parametros = new HashMap<>();
    	parametros.put("claseorden",claseF);
    	parametros.put("ordendecompra", SysmanFunciones.toString(registro.getCampos().get(CAMPO_NUMERO)));
    	parametros.put("vigencia", vigencia);
    	parametros.put("titulo", titulo);
    	parametros.put("aportantes", aportantes);
    	parametros.put(LLAVE_PCONTRATO, css);

    	Direccionador direccionador = new Direccionador();

    	direccionador.setParametros(parametros);
    	direccionador.setNumForm(Integer
    			.toString(GeneralCodigoFormaEnum.FRM_NOTAS_CREDITO_CONTROLADOR
    					.getCodigo()));
    	SessionUtil.redireccionarForma(direccionador,
    			SessionUtil.getModulo());

    	//</CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdImpresora en la vista
     *
     */
    public void oprimiractualizaPlanCompras() {
        // <CODIGO_DESARROLLADO>

        String numero = SysmanFunciones
                        .nvl(registro.getCampos().get(CAMPO_NUMERO), "")
                        .toString();
        if ("SI".equals(controlaValorContratoSegunDisp)) {

            BigInteger vlrTotal = new BigInteger(SysmanFunciones
                            .nvl(registro.getCampos().get("VALORTOTAL"), "0")
                            .toString());
            BigInteger vlrDisp = new BigInteger(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get("VALORDISPONIBILIDAD"), "0")
                            .toString());
            if (vlrTotal.compareTo(vlrDisp) > 0) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2072"));
                return;

            }
        }
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61655
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rs != null) {
            extraerValores();
        }
        if ("SI".equals(controlaItemsPlanCompras)) {
            muestraDialogoActualiza = true;
            return;
        }
        else {
            actualizarCompras(numero);
        }
        impreso = (boolean) SysmanFunciones
                        .nvl(registro.getCampos().get(CAMPO_IMPRESO), false);
        if (impreso) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB2042"));
            return;
        }
        registro.getCampos().put(CAMPO_IMPRESO, true);
        validarTerceroBloqueado();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     */
    private void validarTerceroBloqueado() {
        if (!terceroLocked) {
            ordenDeServicioPage1Enabled = false;
            ordenDeServicioPage2Enabled = false;
            ordenDeServicioPage3Enabled = false;
            terceroLocked = true;
            fechaLocked = true;
            fechaFinalizacionLocked = true;
            duracionLocked = true;
            dependenciaLocked = true;
            objetoContratoLocked = true;
            descripcionLocked = true;
            formaDePagoLocked = true;
            interventorLocked = true;
            txtValorTotalLocked = true;
            valorTotalLocked = true;
            seleccionarRequisicionesEnabled = false;
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
     *
     * 
     */
    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(modeloPlantilla)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3737"));
            return;
        }

        if (listaModelos.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2074"));
        }
        else {
            String strNombreDocumento = titulo + " No. "
                + SysmanFunciones.nvl(registro.getCampos().get(CAMPO_NUMERO),
                                "0").toString();
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = modeloPlantilla;
            valores[1] = fechaFormato;
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$claseOrden$s", tipoContrato);
            variablesConsultaW.put("s$rubro$s", disponibilidadConcatenado);
            variablesConsultaW.put("s$numeroOrden$s",
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(CAMPO_NUMERO),
                                            "0").toString());

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos,
                            valores);

            if (!(boolean) registro.getCampos()
                            .get(GeneralParameterEnum.IMPRESO.getName())) {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PcontratosControladorUrlEnum.URL2857
                                                                .getValue());
                Map<String, Object> fields = new TreeMap<>();
                fields.put(GeneralParameterEnum.IMPRESO.getName(), "-1");
                fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                fields.put(PcontratosControladorEnum.KEY_COMPANIA.getValue(),
                                compania);
                fields.put(PcontratosControladorEnum.KEY_CLASEORDEN.getValue(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.CLASEORDEN
                                                                .getName()));
                fields.put(PcontratosControladorEnum.KEY_NUMERO.getValue(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.NUMERO
                                                                .getName()));
                Parameter parameter = new Parameter();
                parameter.setFields(fields);

                try {
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(),
                                    parameter);
                }
                catch (SystemException e) {
                    JsfUtil.agregarMensajeError(e.getMessage());
                    logger.error(e.getMessage(), e);

                }

                cargarRegistro(css, ACCION_MODIFICAR);

            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton SeleccionarRequisiciones
     * en la vista
     *
     */
    public void oprimirSeleccionarRequisiciones() {
        // <CODIGO_DESARROLLADO>
        try {
            Object numeroOrden = String
                            .valueOf(registro.getCampos().get(CAMPO_NUMERO));

            agregarRegistroNuevo(false);

            ejbContratosUno.seleccionarRequisiciones(compania, usuario);

            String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN,
                                "txtRedonValorUnitarioIVA",
                                "txtDigRedoValorUnitarioIVA",
                                "txtRedondeoTotal", "txtDigRedonTotal",
                                "porcDescGlobal", "porcIVAGlobal" };
            Object[] valores = { tipoContrato,
                                 numeroOrden,
                                 redondearUnitarioIvaOC,
                                 txtDigRedoValorUnitarioIVA,
                                 redondearValorTotalOC,
                                 txtDigRedonTotal,
                                 registro.getCampos().get(CAMPO_PORCDESCGLOBAL)
                                                 .toString(),
                                 registro.getCampos().get(CAMPO_PORCIVAGLOBAL)
                                                 .toString() };
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.AUXORDENDESUMINISTROS_CONTROLADOR
                                            .getCodigo()),
                            modulo,
                            campos, valores);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton importatodo en la vista
     *
     */
    public void oprimirimportatodo() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos()
                        .get(CAMPO_TIPOADJUDICACION) != null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2075"));
        }
        else {
            agregarRegistroNuevo(false);
            String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN,
                                "confirmar" };
            Object[] valores = { tipoContrato,
                                 registro.getCampos().get(CAMPO_NUMERO),
                                 registro.getCampos().get(CAMPO_IMPORTARPRE) };
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPORTAR_ITEMS_CONTROLADOR
                                            .getCodigo()),
                            modulo,
                            campos, valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnTercero en la vista
     *
     */
    public void oprimirBtnTercero() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rowidPcontrato", registro.getLlave());
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put(PARAMETRO_TITULO, titulo);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.TERCEROS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton TercerosAportantes en la
     * vista
     *
     */
    public void oprimirTercerosAportantes() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.TERCEROS_APORTANTES_CONTROLADOR
                                        .getCodigo()),
                        modulo, campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnCambiar en la vista
     *
     */
    public void oprimirBtnCambiar() {
        // <CODIGO_DESARROLLADO>
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61656
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rs != null) {
            boolean revisarContratistas = !"0"
                            .equals(rs.getCampos().get("PERSONAL"));
            if ("SI".equals(manejaNominaContratistas) && !revisarContratistas) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2756"));
            }
            if (css != null) {
                muestraCambiarConsecutivo = true;

            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando406 en la vista
     *
     */
    public void oprimirComando406() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put("tercero",
                        String.valueOf(registro.getCampos()
                                        .get(CAMPO_TERCERO)));
        parametros.put("valorFinal",
                        String.valueOf(registro.getCampos()
                                        .get(CAMPO_VALORFINAL)));
        parametros.put("dependencia", String
                        .valueOf(registro.getCampos().get(CAMPO_DEPENDENCIA)));
        parametros.put("valoresUnitarios", String.valueOf(
                        registro.getCampos().get(CAMPO_PRECIOS_UNITARIOS)));
        parametros.put("txtValorTotal",
                        String.valueOf(registro.getCampos()
                                        .get(CAMPO_VALORTOTAL)));
        parametros.put("actualizaPlanDeCompras", String.valueOf(
                        registro.getCampos()
                                        .get(CAMPO_ACTUALIZAPLANDECOMPRAS)));
        parametros.put("subPContratoPermiteAgregar",
                        String.valueOf(subPContratoPermiteAgregar));
        parametros.put("subPContratoPermiteEditar",
                        String.valueOf(subPContratoPermiteEditar));
        parametros.put("subPContratoPermiteEliminar",
                        String.valueOf(subPContratoPermiteEliminar));
        parametros.put("porcDescGlobalLocked",
                        String.valueOf(porcDescGlobalLocked));
        parametros.put("porcIVAGlobalLocked",
                        String.valueOf(porcIVAGlobalLocked));
        parametros.put("porcIVAGlobal",
                        registro.getCampos().get(CAMPO_PORCIVAGLOBAL)
                                        .toString());
        parametros.put("porcDescGlobal",
                        registro.getCampos().get(CAMPO_PORCDESCGLOBAL)
                                        .toString());
        parametros.put("rowidPcontrato", registro.getLlave());
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put(PARAMETRO_TITULO, titulo);

        parametros.put("convenio", aportantes);
        parametros.put("preciosUni", SysmanFunciones.toString(registro.getCampos().get("PRECIOS_UNITARIOS")));
        parametros.put("sumaPorc",sumaPorcentajes());

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SUBPCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando445 en la vista
     *
     */
    public void oprimirComando445() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        JSONObject llaves = new JSONObject();
        try {
            llaves.put(CAMPO_CLASEORDEN, "'" + tipoContrato + "'");
            llaves.put("NUMEROCONTRATO",
                            "'" + registro.getCampos().get(CAMPO_NUMERO) + "'");
            String tabla = "SUPERVISORES";
            String numero = String
                            .valueOf(registro.getCampos().get(CAMPO_NUMERO));

            String[] campos = { "tabla", "llaves", "numero", "esCreador" };
            String[] valores = { tabla, llaves.toString(), numero, "false" };
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.DSUPERVISORES_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos,
                            valores);
        }
        catch (JSONException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BT615 en la vista
     *
     * 
     */
    public void oprimirBT615() {
        // <CODIGO_DESARROLLADO>
        Registro rs = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL61657
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rs == null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2076"));
        }
        else {
            agregarRegistroNuevo(false);
            String[] campos = { "tipoContrato", "numeroContrato", "anio" };
            String[] valores = { tipoContrato, String.valueOf(
                            registro.getCampos().get(CAMPO_NUMERO)), vigencia };
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.ESTAMPILLAS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos,
                            valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton imprimirInfJuridica en la
     * vista
     *
     */
    public void oprimirimprimirInfJuridica() {
        // <CODIGO_DESARROLLADO>
        if (listaListaModelos.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2080"));
        }
        else if (SysmanFunciones.validarVariableVacio(modelo)) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2081"));
            return;
        }
        else {
            String strNombreDocumento = "Poliza Contrato No."
                + SysmanFunciones
                                .nvl(registro.getCampos().get(CAMPO_NUMERO), "")
                                .toString();
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = modelo;
            valores[1] = fechaFormatoPoliza;
            valores[2] = strNombreDocumento;

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$claseOrden$s", "'" + tipoContrato + "'");
            variablesConsultaW.put("s$numeroOrden$s", "'"
                + SysmanFunciones
                                .nvl(registro.getCampos().get(CAMPO_NUMERO), "")
                                .toString()
                + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos,
                            valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton multass en la vista
     *
     * 
     */
    public void oprimirmultass() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put(LLAVE_PCONTRATO, registro.getLlave());
        parametros.put(PARAMETRO_TITULO, titulo);
        parametros.put("tipoTransaccion", "M");
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.MULTASSANCIONES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton sancioness en la vista
     *
     * 
     */
    public void oprimirsancioness() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put(LLAVE_PCONTRATO, registro.getLlave());
        parametros.put(PARAMETRO_TITULO, titulo);
        parametros.put("tipoTransaccion", "S");
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.MULTASSANCIONES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerPolizas en la vista
     *
     */
    public void oprimirVerPolizas() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put("valorContrato",
                        String.valueOf(registro.getCampos()
                                        .get(CAMPO_VALORFINAL)));
        parametros.put(LLAVE_PCONTRATO, registro.getLlave());
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        parametros.put(PARAMETRO_TITULO, titulo);
        try {
            parametros.put(PARAMETRO_FECHAFIRMA,
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos().get(
                                                            CAMPO_FECHAFIRMA)));
        }
        catch (ParseException ex) {
            Logger.getLogger(PcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MSG_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SUBPOLIZAS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerArchivo en la vista
     *
     */
    public void oprimirVerArchivo() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN };
        String[] valores = { tipoContrato, String
                        .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
        SessionUtil.cargarModalDatos(
                        String.valueOf(GeneralCodigoFormaEnum.DIGITALIZACION_CONTRATOS_CONTROLADOR
                                        .getCodigo()),
                        modulo,
                        campos, valores);
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton proyectosCont
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirproyectosCont() {
    	String numeroContrato = String.valueOf(registro.getCampos().get(CAMPO_NUMERO));

		String[] campos = {"tipoContrato", "numeroContrato"};
		String[] valores = {tipoContrato, numeroContrato};
		
		SessionUtil.cargarModalDatosFlash(
		                String.valueOf(GeneralCodigoFormaEnum.PROYECTOS_CONT_CONTROLADOR
		                                .getCodigo()),
		                modulo, campos, valores);
    }
        
    /**
     * 
     * Metodo ejecutado al oprimir el boton cofinanciadores
     * en la vista
     *
     *
     */
    public void oprimircofinanciadores() {
    	String numeroContrato = String.valueOf(registro.getCampos().get(CAMPO_NUMERO));

		String[] campos = {"tipoContrato", "numeroContrato"};
		String[] valores = {tipoContrato, numeroContrato};
		
		SessionUtil.cargarModalDatosFlash(
		                String.valueOf(GeneralCodigoFormaEnum.COFINANCIADORES_CONTROLADOR
		                                .getCodigo()),
		                modulo, campos, valores);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CalcularIVA en la vista
     *
     * 
     */
    public void oprimirCalcularIVA(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Quitar en la vista
     *
     */
    public void oprimirQuitar() {
        // <CODIGO_DESARROLLADO>
        if (!listalistaRubros.getSeleccionados().isEmpty()) {
            listalistaRubros.getSeleccionados().clear();
            listalistaRubros.getLlavesSeleccionadas().clear();
            listalistaRubros.getFiltradoMultiple().clear();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Confirmar en la vista
     *
     */
    public void oprimirConfirmar() {
        // <CODIGO_DESARROLLADO>

        String dis = SysmanFunciones
                        .nvl(registro.getCampos().get("DISPONIBILIDAD"), "")
                        .toString();
        String res = SysmanFunciones
                        .nvl(registro.getCampos().get("RESERVA"), "")
                        .toString();
        if (!dis.isEmpty() || !res.isEmpty()) {
            muestraDialogoConfirmar = true;
        }
        else {
            ejecutarInsertarRubros();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerDisponibilidades en la
     * vista
     *
     */
    public void oprimirVerDisponibilidades() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put("tipoPPTO", "DIS");
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put(LLAVE_PCONTRATO, registro.getLlave());
        parametros.put(PARAMETRO_TITULO, titulo);
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        try {
            parametros.put(PARAMETRO_FECHAFIRMA,
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos().get(
                                                            CAMPO_FECHAFIRMA)));
        }
        catch (ParseException ex) {
            Logger.getLogger(PcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MSG_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.ORDENDECOMPRAPPTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton btnOrdenCompraAux en la
     * vista
     *
     */
    public void oprimirbtnOrdenCompraAux() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        parametros.put(LLAVE_PCONTRATO, registro.getLlave());
        parametros.put(PARAMETRO_TITULO, titulo);
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.ORDENDECOMPRAAUXILIARES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton RegistrosRelacionados en
     * la vista
     *
     */
    public void oprimirRegistrosRelacionados() {
        // <CODIGO_DESARROLLADO>
        agregarRegistroNuevo(false);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(PARAMETRO_CLASEF, tipoContrato);
        parametros.put("tipoPPTO", "RES");
        parametros.put(PARAMETRO_NUMERO_ORDEN,
                        String.valueOf(registro.getCampos().get(CAMPO_NUMERO)));
        parametros.put(LLAVE_PCONTRATO, registro.getLlave());
        parametros.put(PARAMETRO_TITULO, titulo);
        parametros.put(PARAMETRO_VIGENCIA, vigencia);
        try {
            parametros.put(PARAMETRO_FECHAFIRMA,
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registro.getCampos().get(
                                                            CAMPO_FECHAFIRMA)));
        }
        catch (ParseException ex) {
            Logger.getLogger(PcontratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(MSG_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
        SessionUtil.setSessionVar(PARAMETRO_RETORNO_FORMULARIO,
                        PARAMETRO_RETORNA);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.ORDENDECOMPRAPPTOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdEliminarInfPptal en la
     * vista
     *
     * 
     */
    public void oprimirCmdEliminarInfPptal() {
        // <CODIGO_DESARROLLADO>
        muestraEliminarInfPptal = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarEmail en la vista
     *
     * 
     */
    public void oprimirEnviarEmail() {

        Map<String, Object> remplazosDescripcion = new TreeMap<>();
        remplazosDescripcion.put("numero", registro.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()));

        Map<String, Object> paramEnvio = new TreeMap<>();
        paramEnvio.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        paramEnvio.put(GeneralParameterEnum.CONSECUTIVO.getName(), "38");

        try {
            Registro rsEmail = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL8254
                                                                            .getValue())
                                            .getUrl(),
                                            paramEnvio));

            if (rsEmail != null) {
                String descripcionFinal = remplazarVariable(
                                rsEmail.getCampos().get(
                                                GeneralParameterEnum.DESCRIPCION
                                                                .getName())
                                                .toString(),
                                remplazosDescripcion);

                EmailPojo email = new EmailPojo();
                email.setFrom(rsEmail.getCampos().get("ORIGEN")
                                .toString());
                email.setTo(rsEmail.getCampos().get("CORREOS_DESTINO")
                                .toString());
                email.setSubject(rsEmail.getCampos().get("ASUNTO").toString());
                email.setBody(descripcionFinal);

                ApiRestClient client = new ApiRestClient();

                String respuesta;

                respuesta = client.postClient(email);

                Gson gson = new Gson();
                RespuestaApi respuestaApi = gson.fromJson(
                                respuesta,
                                RespuestaApi.class);

                if (respuestaApi.getCodigo() != 0) {
                    JsfUtil.agregarMensajeError(
                                    respuestaApi.getCuerpo().toString());

                }
                else {
                    JsfUtil.agregarMensajeInformativo(
                                    "Correo enviado correctamente");

                }
            }
            else {
                JsfUtil.agregarMensajeError(
                                "El correo no se envió");
            }
        }
        catch (SystemException | IOException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public String remplazarVariable(String descripcion,
        Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(
                            SysmanFunciones.concatenar("s$", entry.getKey(),
                                            "$s"),
                            SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnInformeAlmacen en la
     * vista
     *
     */
    public void oprimirBtnInformeAlmacen() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String informe = obtenerFormatoOrden();
            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("claseOrden", tipoContrato);
            reemplazar.put("ordenDeCompra",
                            registro.getCampos().get(CAMPO_NUMERO));
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_DIRECCIONCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getDireccion());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_TELEFONOCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getTelefono());
            parametros.put("PR_CARGO_ORDENADOR_ALMACEN",
                            getParametro("CARGO ORDENADOR ALMACEN", " "));
            parametros.put("PR_CARGO_SUBDIRECTOR",
                            getParametro("CARGO SUBDIRECTOR", " "));
            parametros.put("PR_CARGO_COORDINADOR_DE_SUMINISTROS", getParametro(
                            "CARGO COORDINADOR DE SUMINISTROS", " "));
            parametros.put("PR_CARGO_EXAMINADOR_DOCUMENTOS",
                            getParametro("CARGO EXAMINADOR DOCUMENTOS", " "));
            parametros.put("FIRMA_1_DE_ORDEN_DE_COMPRA",
                    getParametro("FIRMA 1 DE ORDEN DE COMPRA", " "));
            parametros.put("FIRMA_2_DE_ORDEN_DE_COMPRA",
                    getParametro("FIRMA 2 DE ORDEN DE COMPRA", " "));
            parametros.put("FIRMA_3_DE_ORDEN_DE_COMPRA",
                    getParametro("FIRMA 3 DE ORDEN DE COMPRA", " "));
            parametros.put("PIE_DE_PAGINA_DE_ORDEN_DE_COMPRA",
                    getParametro("Pie de página de orden de compra", " "));

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Realiza la consulta del formato definido para el Tipo de Orden
     * de Compra con el quese esta trabajando
     * 
     * @return Formato definido en la tabla TIPOORDENDECOMPRA
     */
    private String obtenerFormatoOrden() {
        String formatoOrden = "";
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL3713
                                                                            .getValue())
                                            .getUrl(), params));
            formatoOrden = SysmanFunciones.validarCampoVacio(
                            rs.getCampos(), "FORMATO_ALMACEN") ? " "
                                : rs.getCampos().get("FORMATO_ALMACEN")
                                                .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return formatoOrden;
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto
     * abrirUrgenciaManifiesta en la vista
     *
     */
    public void ejecutarabrirUrgenciaManifiesta() {
        // <CODIGO_DESARROLLADO>
        if (Boolean.parseBoolean(
                        registro.getCampos().get("EMERGENCIAIN").toString())) {
            String[] campos = { PARAMETRO_CLASE_ORDEN, PARAMETRO_NUMERO_ORDEN };
            String[] valores = { tipoContrato, String
                            .valueOf(registro.getCampos().get(CAMPO_NUMERO)) };
            SessionUtil.cargarModalDatos(
                            String.valueOf(GeneralCodigoFormaEnum.URGENCIAMANIFIESTAS_CONTROLADOR
                                            .getCodigo()),
                            modulo, campos,
                            valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Clasificacionproponentes
     */
    public void agregarRegistroSubClasificacionproponentes() {
        try {
            registroSub.getCampos().put(CAMPO_COMPANIA, compania);
            registroSub.getCampos().put(CAMPO_CLASEORDEN,
                            registro.getCampos().get(CAMPO_CLASEORDEN));
            registroSub.getCampos().put(CAMPO_NUMERO,
                            registro.getCampos().get(CAMPO_NUMERO));
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CLASIFICACION_PROPONENTES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaClasificacionproponentes();
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    /**
     * Metodo de edicion del formulario Clasificacionproponentes
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubClasificacionproponentes(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        if ((reg.getCampos().get(CAMPO_ACTIVIDAD) == null)
            || (reg.getCampos().get(CAMPO_ESPECIALIDAD) == null)
            || (reg.getCampos().get("GRUPO") == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2068"));
            return;
        }
        try {

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.CLASEORDEN.getName());
            reg.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
            reg.getCampos().remove("NOMBREGRUPO");
            reg.getCampos().remove("NOMBREESP");
            reg.getCampos().remove("NOMBREACTIVIDAD");
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CLASIFICACION_PROPONENTES
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            cargarListaClasificacionproponentes();
        }

    }

    /**
     * Metodo de eliminacion del formulario Clasificacionproponentes
     * 
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubClasificacionproponentes(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.CLASIFICACION_PROPONENTES
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaClasificacionproponentes();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Clasificacionproponentes.
     */
    public void cancelarEdicionClasificacionproponentes() {
        cargarListaClasificacionproponentes();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * 
     */
    private void ejecutarInsertarRubros() {
        if (!listalistaRubros.getSeleccionados().isEmpty()) {

            for (int i = 0; i < listalistaRubros.getSeleccionados()
                            .size(); i++) {

                try {

                    ejbContratosUno.insertaPpto(compania, claseF,
                                    Long.parseLong(registro.getCampos()
                                                    .get(CAMPO_NUMERO)
                                                    .toString()),
                                    claseDisponibilidaD,
                                    Long.parseLong(listalistaRubros
                                                    .getSeleccionados().get(i)
                                                    .getCampos()
                                                    .get(CAMPO_NUMERO)
                                                    .toString()),
                                    listalistaRubros.getSeleccionados().get(i)
                                                    .getCampos()
                                                    .get(CAMPO_FECHA)
                                                    .toString(),
                                    registro.getCampos().get(CAMPO_TERCERO)
                                                    .toString(),
                                    registro.getCampos().get(CAMPO_SUCURSAL)
                                                    .toString(),
                                    usuario);

                }
                catch (NumberFormatException | SystemException e) {
                    JsfUtil.agregarMensajeError(e.getMessage());
                    logger.error(e.getMessage(), e);

                }
            }
        }
        else {
            limpiarOrdenDeCompra();
        }

        cargarRegistro(css, ACCION_MODIFICAR);
        disponibilidadConcatenado = getCadenaRubros("DIS");
        reservaConcatenado = getCadenaRubros("RES");
        cargarListalistaRubros();
        if (!listalistaRubros.getSeleccionados().isEmpty()) {
            listalistaRubros.getSeleccionados().clear();
            listalistaRubros.getLlavesSeleccionadas().clear();
        }
        muestraDialogoConfirmar = false;
    }

    /**
     * Metodo para actualizar la fecha de finalizacion del contrato
     * para cuando son habiles o calendario
     */
    private void validarDiasCalendario() {
        Date fechaf;
        try {
            int plazoEntrega = getIntVal(CAMPO_PLAZODEENTREGA);
            int tipoDia = getIntVal("TIPODIA");
            int unidad = getIntVal("UNIDAD_PLAZO");
            fecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get(CAMPO_FECHA),
                            FORMATO_FECHA);
            int dias;

            if ((unidad == 1)
                && (tipoDia == 1)) {

                if (mismoDia) {
                    fechaf = ejbSysmanUtil.retornarFechaMasDiasHabiles(compania,
                                    (Date) registro.getCampos()
                                                    .get(CAMPO_FECHA),
                                    plazoEntrega, false);
                }
                else {
                    fechaf = ejbSysmanUtil.retornarFechaMasDiasHabiles(compania,
                                    (Date) registro.getCampos()
                                                    .get(CAMPO_FECHA),
                                    plazoEntrega + 1, false);
                }

            }
            else {

                if ("SI".equals(manejaMeses30dias)) {
                    dias = plazoEntrega + calcularAcumulado(plazoEntrega);
                }
                else {
                    dias = plazoEntrega;
                }

                if (mismoDia) {
                    fechaf = SysmanFunciones.sumarRestarDiasFecha(
                                    (Date) registro.getCampos()
                                                    .get(CAMPO_FECHA),
                                    dias - 1);
                }
                else {
                    fechaf = SysmanFunciones.sumarRestarDiasFecha(
                                    (Date) registro.getCampos()
                                                    .get(CAMPO_FECHA),
                                    dias);
                }

            }
            registro.getCampos().put(CAMPO_FECHAFINALIZACION, fechaf);
        }
        catch (SystemException | ParseException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * @param tipologia
     * @return
     */
    private boolean validarTipologia(int tipologia) {
        boolean valida = (tipologia == 49) || (tipologia == 21);
        valida = valida && ((tipologia <= 29) || (tipologia >= 31));
        valida = valida && (tipologia <= 40);
        return valida;
    }

    /**
     * @param numero
     */
    private void actualizarCompras(String numero) {

        try {

            ejbContratosCero.actualizarPlanDeCompras(compania, claseF,
                            Long.parseLong(numero),
                            Long.parseLong(numero), Integer.parseInt(modulo),
                            SysmanFunciones.ano(
                                            (Date) registro.getCampos()
                                                            .get(CAMPO_FECHA)),
                            usuario);

            registro.getCampos().put(CAMPO_ACTUALIZAPLANDECOMPRAS, true);
            if (SysmanFunciones.esBdSqlServer()) {

                registro.getCampos().put("VALOR_CONTRATO",
                                new BigDecimal((double) registro.getCampos()
                                                .get("VALOR_CONTRATO")));
            }
            agregarRegistroNuevo(false);
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * @param reg
     * @param indice
     */
    public void oprimirCargarRuta1(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * @param reg
     * @param indice
     */
    public void oprimirAbrir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     */
    private void limpiarOrdenDeCompra() {
        Map<String, Object> fields = new TreeMap<>();
        fields.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        fields.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CAMPO_CLASEORDEN));
        fields.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        fields.put(GeneralParameterEnum.CLASE.getName(), claseDisponibilidaD);

        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PcontratosControladorUrlEnum.URL1979
                                                        .getValue());
        try {
            requestManager.delete(urlDelete.getUrl(),
                            fields);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * @param r
     */
    public void activarEdicionClasificacionproponentes(Registro r) {

        indiceClasificacionproponentes = listaClasificacionproponentes
                        .indexOf(r);

        actividad = r.getCampos().get(GeneralParameterEnum.ACTIVIDAD.getName())
                        .toString();
        especialidad = r.getCampos().get(CAMPO_ESPECIALIDAD).toString();
        cargarListaEspecialidadSubE();
        cargarListaGrupoSubE();

        cargarSub = false;
    }

    /**
     * @return
     */
    private int calcularUnidadPlazo() {
        int unidadPlazo = 0;
        if (registro.getCampos().get(CAMPO_UNIDAD_PLAZO) instanceof String) {
            unidadPlazo = Integer.parseInt((String) (registro.getCampos()
                            .get(CAMPO_UNIDAD_PLAZO) == null ? "0"
                                : registro.getCampos()
                                                .get(CAMPO_UNIDAD_PLAZO)));
        }
        if (registro.getCampos().get(CAMPO_UNIDAD_PLAZO) instanceof Integer) {
            unidadPlazo = (int) (registro.getCampos()
                            .get(CAMPO_UNIDAD_PLAZO) == null ? 0
                                : registro.getCampos().get(CAMPO_UNIDAD_PLAZO));
        }
        return unidadPlazo;
    }

    /**
     * @param plazoEntrega
     * @return
     */
    private Date getFechaFinalizacion(int plazoEntrega) {
        try {
            fecha = SysmanFunciones.convertirAFechaCadena(
                            (Date) registro.getCampos().get(CAMPO_FECHA),
                            FORMATO_FECHA);
            Date fechaFinalizacion = SysmanFunciones.convertirAFecha(fecha);
            int dias;
            if ("SI".equals(manejaMeses30dias)) {
                dias = plazoEntrega + calcularAcumulado(plazoEntrega);
            }
            else {
                dias = plazoEntrega;
            }
            return SysmanFunciones.sumarRestarDiasFecha(fechaFinalizacion,
                            dias);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }

    /**
     * @param plazoEntrega
     * @return
     * @throws ParseException
     */
    private int calcularAcumulado(double plazoEntrega)
                    throws ParseException {
        int acumulado = 0;
        Date fechaAprobar = (Date) registro.getCampos().get(CAMPO_FECHA);
        for (int i = 0; i <= plazoEntrega; i++) {
            fechaAprobar = SysmanFunciones.sumarRestarDiasFecha(fechaAprobar,
                            1);
            if (SysmanFunciones.getParteFecha(fechaAprobar,
                            Calendar.DAY_OF_MONTH) == 31) {
                acumulado = acumulado + 1;
            }
            if (SysmanFunciones.getParteFecha(fechaAprobar,
                            Calendar.MONTH) == 1) {
                if ((SysmanFunciones.getParteFecha(fechaAprobar,
                                Calendar.DAY_OF_MONTH) == 29)
                    && (SysmanFunciones.getParteFecha(
                                    SysmanFunciones.ultimoDiaDate(fechaAprobar),
                                    Calendar.DAY_OF_MONTH) == 29)) {
                    acumulado = acumulado - 1;
                }
                else if ((SysmanFunciones.getParteFecha(fechaAprobar,
                                Calendar.DAY_OF_MONTH) == 28)
                    && (SysmanFunciones.getParteFecha(
                                    SysmanFunciones.ultimoDiaDate(fechaAprobar),
                                    Calendar.DAY_OF_MONTH) == 28)) {
                    acumulado = acumulado - 2;
                }
            }
        }
        return acumulado;
    }

    /**
     * @param nDias
     * @return
     */
    private Date traerFechaFinal(int nDias) {
        Date strFecha;
        try {
            strFecha = (Date) registro.getCampos().get(CAMPO_FECHA);

            return ejbSysmanUtil.retornarFechaMasDiasHabiles(compania, strFecha,
                            nDias, false);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return null;
        }
    }

    /**
     * @param unidadPlazo
     * @param plazoEntrega
     * @return
     */
    private int calcularNumeroDias(int unidadPlazo, int plazoEntrega) {
        if (unidadPlazo == 1) {
            return plazoEntrega;
        }
        else if (unidadPlazo == 2) {
            return plazoEntrega * 30;
        }
        else {
            return plazoEntrega * 365;
        }
    }

    /**
     * 
     */
    private void cargarParametros() {
        String nombreParametro = "FECHA FIRMA = CIERRE = ADJUDICACION";
        fechaFirmaCierreAdjudicacionIgual = getParametro(nombreParametro, "NO");

        nombreParametro = "BLOQUEAR FECHA INICIALIZACION CONTRATOS";
        bloqueaFechaInicializacionContrato = getParametro(nombreParametro,
                        "SI");

        nombreParametro = "BLOQUEAR FECHA DILIGENCIAMIENTO CONTRATOS";
        bloqueaFechaDiligenciamientoContratos = getParametro(nombreParametro,
                        "NO");

        nombreParametro = "MANEJA IVA ASUMIDO EN VALOR DE PAGOS";
        manejaIvaAsumido = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA NOMINA DE CONTRATISTAS";
        manejaNominaContratistas = getParametro(nombreParametro, "NO");

        nombreParametro = idioma.getString("TB_TB3730");
        controlaTamanioNumeroContrato = getParametro(nombreParametro, "NO");

        nombreParametro = "CONTROLA VALOR CONTRATO SEGUN VALOR DISPONIBILIDAD";
        controlaValorContratoSegunDisp = getParametro(nombreParametro, "NO");

        nombreParametro = "CONTROLA ITEMS DE PLAN DE COMPRAS";
        controlaItemsPlanCompras = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA WORKFLOW EN CONTRATOS";
        manejaWorkflow = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA CAMPOS OBLIGATORIOS EN CONTRATOS";
        manejaCamposObligatorios = getParametro(nombreParametro, "NO");

        nombreParametro = "TIPO DE CONTRATOS CON CAMPOS OBLIGATORIOS";
        tipoContratoCamposObligatorios = getParametro(nombreParametro, "");

        nombreParametro = "MANEJA CAMPOS OBLIGATORIOS ADICIONALES EN CONTRATOS";
        manejaCamposObligatoriosAdicionales = getParametro(nombreParametro,
                        "NO");

        nombreParametro = "MANEJA CAMPOS OBLIGATORIOS ADICIONALES EN CONTRATOS IDSN";
        manejaCamposObligatoriosAdicionalesidsn = getParametro(nombreParametro,
                        "NO");

        nombreParametro = "CLASE CONTABLE MANTENIMIENTO CONTRATOS PAGOS";
        claseContableMantenimientoContrPagos = getParametro(nombreParametro,
                        "E");

        nombreParametro = "OBLIGA INFORMACION PRESUPUESTAL EN CONTRATOS";
        obligaInformacionPresupuestalContratos = getParametro(nombreParametro,
                        "NO");

        nombreParametro = "MANEJA BANCO DE PROYECTOS";
        manejaBancoProyectos = getParametro(nombreParametro, "NO");

        nombreParametro = "REGISTRAR PROYECTOS EN LOS CONTRATOS";
        registraPoryectosContratos = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA PROGRAMACION DE FORMAS DE PAGO";
        manejaProgramacionFormasPago = getParametro(nombreParametro, "NO");

        nombreParametro = "MUESTRA CONTRATOS POR VIGENCIA";
        muestraContratosVigencia = getParametro(nombreParametro, "NO");

        nombreParametro = "TIPOS DE CONTRATOS CON TERCEROS APORTANTES";
        tiposContratosTercerosAportantes = getParametro(nombreParametro, " ");

        nombreParametro = "REDONDEAR UNITARIO CON IVA EN O.C.";
        redondearUnitarioIvaOC = getParametro(nombreParametro, "NO");

        nombreParametro = "DIGITOS REDONDEO UNITARIO CON IVA O.C.";
        digitosRedondeoUnitarioIvaOc = getParametro(nombreParametro, "2");

        nombreParametro = "REDONDEAR VALOR TOTAL EN O.C.";
        redondearValorTotalOC = getParametro(nombreParametro, "NO");

        nombreParametro = "DIGITOS REDONDEO VALOR TOTAL O.C.";
        digitosRedondeoValorTotalOC = getParametro(nombreParametro, "2");

        nombreParametro = "REDONDEAR VALOR TOTAL EN O.C.";
        redondearGranTotalOC = getParametro(nombreParametro, "NO");

        nombreParametro = "DIGITOS REDONDEO GRAN TOTAL O.C.";
        digitosRedondeoGranTotalOC = getParametro(nombreParametro, "2");

        nombreParametro = "REDONDEAR VALOR IVA EN O.C.";
        redondearValorIvaOC = getParametro(nombreParametro, "NO");

        nombreParametro = "DIGITOS REDONDEO VALOR IVA EN O.C.";
        digitosRedondeoValorIVAOC = getParametro(nombreParametro, "2");

        nombreParametro = "FILTRO DE DEPENDENCIA CON MOVIMIENTO";
        filtroDependenciaMovimiento = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA CUATRO POR MIL";
        manejaCuatroPorMil = getParametro(nombreParametro, "NO");

        nombreParametro = "PERMITE ELIMINAR INF. PPTAL. EN CONTRATOS";
        permiteEliminarInformacionPptal = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA CONTROL DE REPORTE EN SICE";
        manejaControlReporteSICE = getParametro(nombreParametro, "NO");

        nombreParametro = "CONTROL CONTRATOS VALOR POR CUANTIAS";
        controlContratosValorCuantia = getParametro(nombreParametro, "NO");

        nombreParametro = "VER TIPO CONTRATO CASANARE";
        verTipoContratoCasanare = getParametro(nombreParametro, "NO");

        nombreParametro = "MANEJA PAGO DE ESTAMPILLAS";
        manejaPagoEstampillas = getParametro(nombreParametro, "NO");

        nombreParametro = idioma.getString("TB_TB3731");
        manejaMeses30dias = getParametro(nombreParametro, "NO");

        nombreParametro = "VALOR SALARIO MINIMO";
        valorSalarioMinimo = getParametro(nombreParametro, null);

        nombreParametro = "IMPRIME INFORME F50_7_PERSONAL Y COSTOS";
        imprimeInformeF50 = getParametro(nombreParametro, "NO");

        nombreParametro = "CALIFICA CONTRATISTA EN ACTA DE TERMINACION";
        calificaContratista = getParametro(nombreParametro, "NO");

        enviarEmailVisible = "SI".equals(
                        getParametro("ENVIAR CORREO CONTRATOS CORPOBOYACA",
                                        "NO"));
        
        mostrarFuente = "SI".equals(
                getParametro("MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN",
                                "NO"));
        
        nombreParametro = "CONTROLA VALOR DEL CONTRATO VS ITEMS";
        controlaValorVsItems = getParametro(nombreParametro, "NO");
        
        cargaProyContratos = "SI".equals(
                getParametro("MANEJA PROYECTOS EN CONTRATOS",
                                "NO"));
        
        cargaCofinanciadores = "SI".equals(
                getParametro("MANEJA COFINANCIADORES EN CONTRATOS",
                                "NO"));
        
        cargaSecop = "SI".equals(
                getParametro("MANEJA CAMPO SECOP EN CONTRATO",
                                "NO"));
        
        if("SI".equals(getParametro("MANEJA OTRO DESTINO EN CONTRATOS","NO"))) {
        	cargaDestinoOtro = true;
        	cargaDestino = false;
        } else {
        	cargaDestinoOtro = false;
        	cargaDestino = true;
        }
    }

    /**
     * 
     */
    private void cargarDigitosRedondeo() {
        txtDigRedoValorUnitarioIVA = "0";
        txtDigRedonTotal = "0";
        if ("SI".equals(redondearUnitarioIvaOC)) {
            digitosRedondeoUnitarioIvaOc = digitosRedondeoUnitarioIvaOc == null
                ? "2"
                : digitosRedondeoUnitarioIvaOc;
            txtDigRedoValorUnitarioIVA = digitosRedondeoUnitarioIvaOc;
        }
        if ("SI".equals(redondearValorTotalOC)) {
            txtDigRedonTotal = digitosRedondeoValorTotalOC;
        }
        if ("SI".equals(redondearGranTotalOC)) {
            txtDigitosRedondeoGranTotal = Integer
                            .parseInt(digitosRedondeoGranTotalOC);
        }
        else {
            txtDigitosRedondeoGranTotal = 2;
        }
        if ("SI".equals(redondearValorIvaOC)) {
            txtDigitosRedondeoValorIVA = Integer
                            .parseInt(digitosRedondeoValorIVAOC);
        }
        else {
            txtDigitosRedondeoValorIVA = 2;
        }
    }

    /**
     * @return
     */
    private boolean validarTipoOrdenCompra() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PcontratosControladorUrlEnum.URL3156
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2089")
                            .replace("#$claseF#$", claseF));
            return false;
        }
        else {
            formato = SysmanFunciones
                            .nvl(reg.getCampos().get("TIPOFORMATO"), "ODC")
                            .toString();
            boolean tieneConvenio = getBlnVal(reg, CAMPO_CONVENIO);
            etiqueta632Visible = !tieneConvenio;
            etiqueta635Visible = !tieneConvenio;
            convenioVisible = !tieneConvenio;
            numconvenioVisible = !tieneConvenio;
            txtValorTotalLocked = !tieneConvenio;
            etiqueta629Visible = !tieneConvenio;
            return true;
        }
    }

    /**
     * @return
     */
    private boolean arrienda() {
        String sql = "SELECT  TIPOORDENDECOMPRA.ARRENDAMIENTO\n"
            + "FROM    TIPOORDENDECOMPRA\n"
            + "WHERE    TIPOORDENDECOMPRA.COMPANIA  ='" + compania + "'"
            + "AND   TIPOORDENDECOMPRA.CODIGO    ='" + claseF + "'\n";
        Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);
        return getBlnVal(reg, "ARRENDAMIENTO");
    }
    /**
     * 
     */
    private void bloquearSegunTercero() {
        impreso = getBlnVal(CAMPO_IMPRESO);
        boolean actualizaPlanDeCompras = getBlnVal(
                        CAMPO_ACTUALIZAPLANDECOMPRAS);
        if (impreso || actualizaPlanDeCompras) {
            ordenDeServicioPage1Enabled = !terceroLocked;
            ordenDeServicioPage2Enabled = !terceroLocked;
            ordenDeServicioPage3Enabled = !terceroLocked;
            itemsCEnabled = !terceroLocked;
            datosDelContratoEnabled = !terceroLocked;
            contratistaInterventorEnabled = !terceroLocked;
            informacionJuridicaEnabled = !terceroLocked;
            informacionPresupuestalEnabled = !terceroLocked;
            fechaDiligenciamientoLocked = !terceroLocked;
            diasLocked = !terceroLocked;
            plazoEntregaLocked = !terceroLocked;
            unidadPlazoLocked = !terceroLocked;
            lugarDeEntregaLocked = !terceroLocked;
            tipoAdjudicacionLocked = !terceroLocked;
            sectorLocked = !terceroLocked;
            tipologiaLocked = !terceroLocked;
            numeroActaLocked = !terceroLocked;
            descripcionLocked = !terceroLocked;
            formaDePagoLocked = !terceroLocked;
            fechaLocked = !terceroLocked;
            objetoContratoLocked = !terceroLocked;
            garantiaLocked = !terceroLocked;
            fechaFinalizacionLocked = !terceroLocked;
            duracionLocked = !terceroLocked;
            dependenciaLocked = !terceroLocked;
            considerandosLocked = !terceroLocked;
            txtValorTotalLocked = !terceroLocked;
            valorFinalLocked = !terceroLocked;
            texto217Locked = !terceroLocked;
            texto219Locked = !terceroLocked;
            interventorLocked = !terceroLocked;
            valorTotalLocked = !terceroLocked;
            copiarDeEnabled = !terceroLocked;
            terceroLocked = !terceroLocked;

            seleccionarRequisicionesEnabled = terceroLocked;
            cmdCopiarDeEnabled = terceroLocked;
        }
        if (terceroLocked) {
            if (!ordenDeServicioPage1Enabled) {
                ordenDeServicioPage1Enabled = false;
            }
            if (!ordenDeServicioPage2Enabled) {
                ordenDeServicioPage1Enabled = false;
            }
            if (!ordenDeServicioPage3Enabled) {
                ordenDeServicioPage1Enabled = false;
            }
            itemsCEnabled = true;
            datosDelContratoEnabled = true;
            contratistaInterventorEnabled = true;
            informacionJuridicaEnabled = true;
            informacionPresupuestalEnabled = true;
            pagina33Enabled = true;
            fechaLocked = true;
            fechaFinalizacionLocked = true;
            duracionLocked = true;
            dependenciaLocked = true;
            objetoContratoLocked = true;
            descripcionLocked = true;
            formaDePagoLocked = true;
            interventorLocked = true;
            txtValorTotalLocked = true;
            valorTotalLocked = true;
            seleccionarRequisicionesEnabled = true;
        }
    }

    /**
     * @param tipo
     * @return
     */
    private String getCadenaRubros(String tipo) {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CAMPO_CLASEORDEN));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        param.put("TIPO", tipo);
        Registro regConcatDis = null;
        try {
            regConcatDis = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL3692
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return (String) (regConcatDis == null ? ""
            : regConcatDis.getCampos().get(CAMPO_RUBRONOMBRE));
    }

    /**
     * @return
     */
    private Object traerAportesTercero() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        registro.getCampos().get(CAMPO_CLASEORDEN));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        registro.getCampos().get(CAMPO_NUMERO));
        Registro registroAportes = null;
        try {
            registroAportes = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL3693
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return registroAportes != null
            ? registroAportes.getCampos().get("TOTAL")
            : "0";
    }

    /**
     * 
     */
    private void extraerTotales() {
        if (css != null) {
            // URL3712
            Registro registroSubPContrato = null;
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            registro.getCampos().get(CAMPO_CLASEORDEN));
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            registro.getCampos().get(CAMPO_NUMERO));
            param.put("DIGITOSREDONDEOGRANTOTAL", txtDigitosRedondeoGranTotal);
            try {
                registroSubPContrato = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PcontratosControladorUrlEnum.URL3712
                                                                                .getValue())
                                                .getUrl(), param));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            if (registroSubPContrato != null) {
                txtSub = getDblVal(registroSubPContrato, "TXTSUBTOTAL");
                totalCuatroXmil = getDblVal(registroSubPContrato,
                                "TXTTOTALCUATRO");
                if(SysmanFunciones.toString(registro.getCampos().get("PRECIOS_UNITARIOS")).equals("false")) {
                	totalOrdenCompra = getDblVal(registroSubPContrato, "TXTTOTAL");
                }else {
                	totalOrdenCompra = getDblVal(registroSubPContrato, "TOTALCONAIU");
                }

                if ((getDblVal(registroSubPContrato, "TXTTOTAL") > 0.0 ) && controlaValorVsItems.equals("NO")) {

                	if(SysmanFunciones.toString(registro.getCampos().get("PRECIOS_UNITARIOS")).equals("false")) {
                		registro.getCampos().put(CAMPO_VALORTOTAL, getDblVal(
                				registroSubPContrato, "TXTTOTAL"));
                		registro.getCampos().put(CAMPO_VALORFINAL, getDblVal(
                				registroSubPContrato, "TXTTOTAL"));
                	}else {
                		registro.getCampos().put(CAMPO_VALORTOTAL, getDblVal(
                				registroSubPContrato, "TOTALCONAIU"));
                		registro.getCampos().put(CAMPO_VALORFINAL, getDblVal(
                				registroSubPContrato, "TOTALCONAIU"));
                	}

                }

                txtSubPorImprevistos = txtSub
                    * (getDblVal(CAMPO_PORINPREVISTOS) / 100);
                txtSubPorAdmon = txtSub
                    * (getDblVal(CAMPO_PORADMINISTRACION) / 100);
                txtSubPorUtilidades = txtSub
                    * (getDblVal(CAMPO_PORUTILIDADES) / 100);
                txtValorIva = getDblVal(registroSubPContrato, "TXTIVA");
                txtValorDescuento = getDblVal(registroSubPContrato,
                                "TXTTOTALDESCUENTO");

                txtAjusteAlPeso = getDblVal(registroSubPContrato,
                                "TXTAJUSTEALPESO");
                vlrImpConsumo = getDblVal(registroSubPContrato,
                		"TXTIMPCONSUMO");
                vlrAIU = getDblVal(registroSubPContrato,
                		"TXTVALOR_AIU");

            }
        }
    }

    private void eliminarCamposNoPropios() {
        registro.getCampos().remove(CAMPO_VALOR_CONTRATO);
        registro.getCampos().remove("RUBRORESERVA");
        registro.getCampos().remove("RESERVA");
        registro.getCampos().remove("DESCRIPCION_RUBROS");
        registro.getCampos().remove("FUENTERECURSOS");
        registro.getCampos().remove("FECHADEDISPONIBILIDAD");
        registro.getCampos().remove("CODIGOS_RUBROS");
        registro.getCampos().remove(CAMPO_RUBRO);
        registro.getCampos().remove(CAMPO_DISPONIBILIDAD);
        registro.getCampos().remove(CAMPO_AUXILIAR);
        registro.getCampos().remove("RECPUBLICACION");
        registro.getCampos().remove("CODIGORUP");
        registro.getCampos().remove("NOMBRE_DEPENDENCIA");
        registro.getCampos().remove("MODIFICACION_TIEMPO");

        registro.getCampos().remove("FECHAREGISTRO");
    }

    private void actualizarCamposNoNulos() {
        registro.getCampos().put("R6_CLASIFICACION",
                        getIntVal("R6_CLASIFICACION"));
        String nombreCampo = "INDICADORENVIO";
        Object object = registro.getCampos().get(nombreCampo);
        registro.getCampos().put(nombreCampo, object instanceof Boolean
            ? getBlnVal(nombreCampo)
            : getIntVal(nombreCampo));

        nombreCampo = "INDICADORCUMPLIMIENTO";
        object = registro.getCampos().get(nombreCampo);
        registro.getCampos().put(nombreCampo, object instanceof Boolean
            ? getBlnVal(nombreCampo)
            : getIntVal(nombreCampo));
    }

    /**
     * Validacion de campos obligatorios.
     *
     * @return Verdadero si falta diligencia algun campo.
     */
    private boolean faltanCamposObligatorios() {
        Map<String, Object> campos = registro.getCampos();
        if (estaVacio(campos, "ORDENADOR", "TB_TB2091")) {
            return true;
        }
        if ("SI".equals(manejaCamposObligatorios)
            && (tipoContratoCamposObligatorios.indexOf(tipoContrato) >= 0)
            && faltanCamposObligatorios(campos)) {
            return true;
        }
        if ((mostrarFuente == true)
                && estaVacio(campos, "FUENTEDERECURSO", "TB_TB4452")) {
                return true;
            }
            
        if ("SI".equals(obligaInformacionPresupuestalContratos)
            && faltaInformacionPresupuestal(campos)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2110"));
        }
        return false;

    }

    /**
     * @param campos
     * @return
     */
    private boolean faltanCamposObligatorios(Map<String, Object> campos) {
        if (estaVacio(campos, "PROFESION", "TB_TB2092")
            || estaVacio(campos, CAMPO_TERCERO, "TB_TB2093")
            || estaVacio(campos, "TIPOCONTRATISTA", "TB_TB2094")
            || estaVacio(campos, "INTERVENTOR", "TB_TB2095")) {
            return true;
        }
        if (faltanObligatoriosAdi(campos)) {
            return true;
        }

        return false;
    }

    /**
     * @param campos
     * @return
     */
    private boolean faltanObligatoriosAdi(Map<String, Object> campos) {
        if (estaVacio(campos, CAMPO_CEDULAINTERVENTOR, "TB_TB2096")
            || estaVacio(campos, "CARGOINTERVENTOR", "TB_TB2097")
            || estaVacio(campos, "TIPOINTERVENTOR", "TB_TB2098")
            || estaVacio(campos, CAMPO_FECHAFIRMA, "TB_TB2099")) {
            return true;
        }
        if (estaVacio(campos, CAMPO_TIPOADJUDICACION, "TB_TB2100")
            || faltanCamposObligatoriosAdicionales(campos)
            || faltanCamposObligatoriosIDSN(campos)) {
            return true;
        }
        return false;
    }

    /**
     * @param campos
     * @return
     */
    private boolean faltanCamposObligatoriosIDSN(Map<String, Object> campos) {
        if ("SI".equals(manejaCamposObligatoriosAdicionalesidsn)) {
            if (estaVacio(campos, CAMPO_OBJETOCONTRATO, TXTBEANTB_TB2101)
                || estaVacio(campos, CAMPO_DURACION, TXTBEANTB_TB2103)
                || estaVacio(campos, CAMPO_PLAZODEENTREGA, TXTBEANTB_TB2104)) {
                return true;
            }
            if (faltanObligatoriosAdiIDSN(campos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param campos
     * @return
     */
    private boolean faltanObligatoriosAdiIDSN(Map<String, Object> campos) {
        if (estaVacio(campos, CAMPO_VALORFINAL, "TB_TB2105")
            || estaVacio(campos, "GARANTIA", "TB_TB2107")
            || estaVacio(campos, "TIPOLOGIA", "TB_TB2108")
            || estaVacio(campos, "FORMADEPAGO", "TB_TB2109")) {
            return true;
        }
        return false;
    }

    /**
     * @param campos
     * @return
     */
    private boolean faltanCamposObligatoriosAdicionales(
        Map<String, Object> campos) {
        if ("SI".equals(manejaCamposObligatoriosAdicionales)) {

            boolean rta3 = estaVacio(campos, CAMPO_PLAZODEENTREGA,
                            TXTBEANTB_TB2104)
                || estaVacio(campos, CAMPO_UNIDAD_PLAZO, TXTBEANTB_TB2104);
            if (validarCamposAdi(campos) || rta3
                || validarObligatorioDia(campos)) {
                return true;
            }
        }
        else if (campos.get("FECHA_NACTO") == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2106"));
            return true;
        }
        return false;
    }

    private boolean validarObligatorioDia(Map<String, Object> campos) {
        if ("1".equals(registro.getCampos().get(CAMPO_UNIDAD_PLAZO))
            && estaVacio(campos, CAMPO_TIPODIA, TXTBEANTB_TB2104)) {

            return true;
        }
        return false;
    }

    /**
     * @param campos
     * @return
     */
    private boolean validarCamposAdi(Map<String, Object> campos) {
        if (estaVacio(campos, CAMPO_OBJETOCONTRATO,
                        TXTBEANTB_TB2101)
            || estaVacio(campos, CAMPO_FECHAFINALIZACION, "TB_TB2102")
            || estaVacio(campos, CAMPO_DURACION, TXTBEANTB_TB2103)) {
            return true;
        }

        if (estaVacio(campos, CAMPO_OBJETOCONTRATO,
                        TXTBEANTB_TB2101)
            || estaVacio(campos, CAMPO_FECHAFINALIZACION, "TB_TB2102")
            || estaVacio(campos, CAMPO_DURACION, TXTBEANTB_TB2103)
            || estaVacio(campos, CAMPO_VALORFINAL, "TB_TB2105")) {
            return true;
        }
        return false;
    }

    /**
     * @param campos
     * @return
     */
    private boolean faltaInformacionPresupuestal(Map<String, Object> campos) {
        return SysmanFunciones.validarCampoVacio(campos, CAMPO_DISPONIBILIDAD)
            ||
            SysmanFunciones.validarCampoVacio(campos, "FECHADEDISPONIBILIDAD")
            || SysmanFunciones.validarCampoVacio(campos, CAMPO_RUBRO);
    }

    /**
     * 
     */
    private void extraerValores() {
        if (!accion.equals(ACCION_MODIFICAR)) {
            setMuestraDialogo(false);
            return;
        }
        /* Se ajusto el proceso se pasa a PLSQL */

        String rta = null;
        try {
            rta = ejbContratosUno.extraerValores(compania, claseF,
                            Long.parseLong(registro.getCampos()
                                            .get(CAMPO_NUMERO).toString()),
                            new BigDecimal((registro.getCampos()
                                            .get(CAMPO_VALORFINAL) == null
                                                ? "0"
                                                : registro.getCampos().get(
                                                                CAMPO_VALORFINAL))
                                                                                .toString()),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rta != null) {
            String[] valores = rta.split(",");
            if (valores.length > 1) {
                txtEvalValor = idioma.getString(valores[0]);
                txtEvalValor = txtEvalValor.replace("s$evalValor$s",
                                valores[1]);
                evalValor = Double.parseDouble(valores[1]);
            }
            registro.getCampos().put(CAMPO_VALORFINAL,
                            SysmanFunciones.nvl(
                                            registro.getCampos()
                                                            .get(CAMPO_VALORFINAL),
                                            0));
            if (Double.parseDouble(registro.getCampos()
                            .get(CAMPO_VALORFINAL).toString()) < evalValor) {
                muestraDialogo = true;
            }
        }
    }

    /**
     * @param compania
     * @param tipoContrato
     * @param numero
     * @return
     */
    private boolean revisarContratistas(String compania, String tipoContrato,
        String numero) {
        boolean revisarContratistas = true;
        if (registro.getCampos().get("NOMINACONTRATISTAS") == null ? false
            : (boolean) registro.getCampos().get("NOMINACONTRATISTAS")) {
            revisarContratistas = false;
        }

        // reviso si el registro ya existe si es asi actualizo los
        // datos
        // 210001 --
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipoContrato);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        List<Registro> rs = null;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PcontratosControladorUrlEnum.URL73654
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((rs != null) && !rs.isEmpty()) {
            revisarContratistas = false;
        }
        return revisarContratistas;
    }

    /**
     * @param strClase
     */
    private void copiarContratos(String strClase) {

        String rta = null;
        try {
            rta = ejbContratosUno.copiarContrato(compania, strClase,
                            Long.parseLong(copiarDe),
                            SysmanFunciones.ano((Date) registro.getCampos()
                                            .get(CAMPO_FECHA)),
                            Long.parseLong(registro.getCampos()
                                            .get(CAMPO_NUMERO).toString()),
                            usuario);

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (rta != null) {
            JsfUtil.agregarMensajeInformativo(rta);
        }
        cmdCopiarDeEnabled = false;
    }

    /**
     * 
     */
    private void genConsecContratos() {
        BigDecimal numero;
        if (css == null) {

            try {
                numero = ejbContratosUno.ConsecContrato(compania, claseF,
                                Integer.parseInt(vigencia), usuario);

                registro.getCampos().put(CAMPO_NUMERO, numero.longValue());

            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        JsfUtil.ejecutarJavaScript(FILTRAR_LM2S);
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault) {
        String parametro = null;
        try {

            parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            nombreParametro,
                            SessionUtil.getModulo(),
                            new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Trae el valor de un campo del registro de tipo entero. En caso
     * de nulo trae el valor en cero.
     *
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>int</code>
     */
    private int getIntVal(String nombreCampo) {
        return getIntVal(registro, nombreCampo);
    }

    /**
     * Trae el valor de un campo del registro de tipo entero. En caso
     * de nulo trae el valor en cero.
     *
     * @param unRegistro
     * El objeto Registro que se va a evaluar.
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>int</code>
     */
    private int getIntVal(Registro unRegistro, String nombreCampo) {
        Object object = unRegistro.getCampos().get(nombreCampo);
        return object == null ? 0 : Integer.parseInt(object.toString());
    }

    /**
     * Trae el valor de un campo del registro de tipo decimal. En caso
     * de nulo trae el valor en cero.
     *
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>double</code>
     */
    private double getDblVal(String nombreCampo) {
        return getDblVal(registro, nombreCampo);
    }

    /**
     * Trae el valor de un campo del registro de tipo decimal. En caso
     * de nulo trae el valor en cero.
     *
     * @param unRegistro
     * El objeto Registro que se va a evaluar.
     * @param nombreCampo
     * Nombre del campo en el registro.
     * @return Valor del campo numerico como <code>double</code>
     */
    private double getDblVal(Registro unRegistro, String nombreCampo) {
        Object object = unRegistro.getCampos().get(nombreCampo);
        return object == null ? 0.0 : Double.parseDouble(object.toString());
    }

    /**
     * Trae el valor de un campo del registro de tipo boleano. En caso
     * de nulo trae el valor como falso.
     *
     * @param nombreCampo
     * @return Valor del campo <code>boolean</code>.
     */
    private boolean getBlnVal(String nombreCampo) {
        return getBlnVal(registro, nombreCampo);
    }

    /**
     * Trae el valor de un campo del registro de tipo boleano. En caso
     * de nulo trae el valor como falso. * @param unRegistro El objeto
     * Registro que se va a evaluar.
     *
     * @param nombreCampo
     * @return Valor del campo <code>boolean</code>.
     */
    private boolean getBlnVal(Registro unRegistro, String nombreCampo) {
        Object object = unRegistro.getCampos().get(nombreCampo);
        return object == null ? false : Boolean.parseBoolean(object.toString());
    }

    /**
     * Verifica si el campo esta nulo o vacio, de ser el caso muestra
     * el mensaje de alerta.
     *
     * @param campos
     * Map que contiene el campo.
     * @param nombreCampo
     * Nombre del campo a validar.
     * @param textoProperties
     * Nombre de la propiedad que contiene el mensaje de alerta.
     * @return Verdadero si el campo esta vacio o nulo.
     */
    private boolean estaVacio(Map<String, Object> campos, String nombreCampo,
        String textoProperties) {
        if (SysmanFunciones.validarCampoVacio(campos, nombreCampo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(textoProperties));
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
        btnFormaPagoVisible = "SI".equals(manejaProgramacionFormasPago);
        indicadorPolizas = "0";
        etiqueta1006Visible = "SI".equals(imprimeInformeF50);
        tipoContrSECOPVisible = "SI".equals(imprimeInformeF50);
        etiqueta1008Visible = "SI".equals(imprimeInformeF50);
        funcionVisible = "SI".equals(imprimeInformeF50);
        obrapublicaVisible = "COP".equals(claseF);
        texto562Visible = !"SI".equals(calificaContratista);
        etiqueta563Visible = !"SI".equals(calificaContratista);
        diasVisible = true;
        if (tiposContratosTercerosAportantes.contains(claseF) || aportantes) {
            tituloAportantesVisible = true;
            datosConvenioVisible = true;
            txtaportestVisible = true;
            aportestVisible = true;
            txtaporteseVisible = true;
            aporteseVisible = true;
            btnTercerosAportantesVisible = true;
        }
        if (!validarTipoOrdenCompra()) {
            return;
        }
        cargarDigitosRedondeo();
        cuatroPorMil = "SI".equals(manejaCuatroPorMil);
        programaVisible = !"SI".equals(manejaBancoProyectos);
        proyectoVisible = !"SI".equals(manejaBancoProyectos);
        if ("SI".equals(manejaNominaContratistas)) {
            boolean visible = nominaContratistas == null ? false : true;
            eqtHonorariosVisible = visible;
            txtHonorariosVisible = visible;
            eqtVehiculoVisible = visible;
            txtVehiculoVisible = visible;
            etqTopeVehiculoVisible = visible;
            txtTopeVehiculoVisible = visible;
        }
        cmdEliminarInfPptalVisible = "SI"
                        .equals(permiteEliminarInformacionPptal);
        // MANEJA CONTROL DE REPORTE EN SICE
        reportarSICEVisible = "SI".equals(manejaControlReporteSICE);
        eReportarSICEVisible = "SI".equals(manejaControlReporteSICE);
        // Se agrego para poder cambiar de Hoja de Datos A
        // Formulario
        valorContratoCuantiaVisible = "SI".equals(controlContratosValorCuantia);
        etiqueta537Visible = "SI".equals(controlContratosValorCuantia);

        clasificacionContratoVisible = "SI".equals(verTipoContratoCasanare);
        clasR6Visible = "SI".equals(verTipoContratoCasanare);

        impreso = getBlnVal(CAMPO_IMPRESO);
        seleccionarRequisicionesEnabled = impreso;
        // RefrescarElementos
        pagoEstampillasVisible = "SI".equals(manejaPagoEstampillas);
        etqPagoEstampillasVisible = "SI".equals(manejaPagoEstampillas);
        calcularIVAVisible = "SI".equals(manejaIvaAsumido);

        etNumPersonasVisible = false;
        numPersonasVisible = false;
        etiqueta325Visible = "SI".equals(manejaBancoProyectos);
        componenteBPVisible = "SI".equals(manejaBancoProyectos);

        boolean visible = "SI".equals(manejaBancoProyectos)
            && "SI".equals(registraPoryectosContratos);
        ordendecompraAuxiliaresVisible = visible;
        etiqueta52Visible = !visible;
        auxiliarVisible = !visible;
        txtAuxiliarVisible = !visible;
        etiqueta53Visible = !visible;
        fuenteRecursosVisible = !visible;
        etiqueta211Visible = !visible;
        destinoVisible = !visible;
        etiqueta325Visible = !visible;
        componenteBPVisible = !visible;

        botonCambiar = getParametro("PERMITE MODIFICAR CONSECUTIVOS CONTRATOS",
                        " ").equals("NO");
        
        mostrarReintegro = getParametro("DESAGREGAR ELEMENTOS EN ENTRADA SEGUN LOTE",
				" ").equals("SI");        

        programaVisible = "NO".equals(manejaBancoProyectos);
        proyectoVisible = "NO".equals(manejaBancoProyectos);
        etiqueta525Visible = "NO".equals(manejaBancoProyectos);
        etiqueta528Visible = "NO".equals(manejaBancoProyectos);
        aplicaAIU = "SI".equals(getParametro("APLICAR AIU EN ENTRADAS DE ALMACEN", "NO"));
        
        topTotal = aplicaAIU?"333px":"308px";
        
        try {
	        paramManejaReportesAppui = "SI".equals(SysmanFunciones
		            .nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA REPORTES APPUI",
		                "-1", new Date(), true), "NO"));
		        
		        paramManejaControlDeContratos = "SI".equals(SysmanFunciones
		            .nvl(ejbSysmanUtil.consultarParametro(compania, "MANEJA CONTROL DE CONTRATOS",
		                "-1", new Date(), true), "NO"));
		       //CC_1499 MROSERO 
                parametroCGR = "SI".equals(SysmanFunciones
                        .nvl(ejbSysmanUtil.consultarParametro(compania, "NUEVOS CAMPOS OBLIGATORIOS EN CONTROL DE CONTRATOS",
                            "-1", new Date(), true), "NO"));
                
                if(parametroCGR && obligaCampos) {
                	obligaCamposCGR = true;
                }else {
                	obligaCamposCGR = false;
                }                	  
                
		        if(paramManejaReportesAppui && !paramManejaControlDeContratos) {
		        	manejaReportesAppui = true;
		        }else {
		        	manejaReportesAppui = false;
		        }
                    
        } catch (SystemException e) {
            e.printStackTrace();
           
        }

        // <CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        importatodoEnabled = false;
        JsfUtil.ejecutarJavaScript("recorrerPestaniaUnica();");
        if (ACCION_INSERTAR.equals(accion)) {
        	vaciarInfFuncional();
            nombreTercero = "";
            nombreDependencia = "";
            disponibilidadConcatenado = "";
            txtValorIva = 0;
            totalCuatroXmil = 0;
            totalOrdenCompra = 0;
            txtSubPorImprevistos = 0;
            txtSubPorAdmon = 0;
            txtSubPorUtilidades = 0;
            txtValorIva = 0;
            txtValorDescuento = 0;
            txtSub = 0;
            txtAjusteAlPeso = 0;
            dependenciaLocked = false;
            valorFinalLocked = false;
            duracionLocked = false;
            plazoEntregaLocked = false;
            diasLocked = false;
            lugarDeEntregaLocked = false;
            tipoAdjudicacionLocked = false;
            sectorLocked = false;
            tipologiaLocked = false;
            numeroActaLocked = false;
            descripcionLocked = false;
            formaDePagoLocked = false;
            garantiaLocked = false;
            objetoContratoLocked = false;
            considerandosLocked = false;
            texto217Locked = false;
            texto219Locked = false;
            registro.getCampos().put(CAMPO_FECHA,
                            Calendar.getInstance().getTime());
            registro.getCampos().put("FECHADILIGENCIAMIENTO",
                            Calendar.getInstance().getTime());
            registro.getCampos().put("ESTADO", "V");
            registro.getCampos().put(GeneralParameterEnum.CLASE.getName(),
                            tipoContrato);
            nombreAbog = "";
            cargoAbog = "";
            
            if(habilitaActasInicio) {
            	 registro.getCampos().put("APLICA_ACTAS_INICIO", false);
            } else {
            	 registro.getCampos().put("APLICA_ACTAS_INICIO", true);
            }
            vlrImpConsumo = 0;
            vlrAIU = 0;

        }
        else {
            cargarListaModelos();
            if ("1".equals(SysmanFunciones.nvl(
                            registro.getCampos().get(CAMPO_UNIDAD_PLAZO), "")
                            .toString())) {
                diasVisible = true;
            }
            else {
                diasVisible = false;
            }

            if (SessionUtil.getSessionVar(
                            PARAMETRO_RETORNO_FORMULARIO) == null) {
                JsfUtil.ejecutarJavaScript(FILTRAR_LM2S);
            }
            else {
                SessionUtil.removeSessionVar(PARAMETRO_RETORNO_FORMULARIO);
            }
            extraerTotales();
            registro.getCampos().put("APORTESTE", traerAportesTercero());

            disponibilidadConcatenado = getCadenaRubros("DIS");
            reservaConcatenado = getCadenaRubros("RES");
            nombreTercero = registro.getCampos()
                            .get(CAMPO_NOMBRECONTRATISTA) == null
                                ? "NINGUNO"
                                : (String) registro.getCampos()
                                                .get(CAMPO_NOMBRECONTRATISTA);
            codDependencia = SysmanFunciones.toString(registro.getCampos().get("DEPENDENCIA"));
            nombreDependencia = SysmanFunciones.toString(registro.getCampos().get("NOMBRE_DEPENDENCIA"));
            importatodoEnabled = getBlnVal(CAMPO_IMPORTARPRE);
            copiarDeEnabled = true;
            convenio = getBlnVal(CAMPO_CONVENIO);
            numconvenio = registro.getCampos().get(CAMPO_NUM_CONVENIO)
                            .toString();

            if (convenio) {
                convenioLocked = true;
                numconvenioEnabled = numconvenio != null;
            }
            else {
                convenioLocked = false;
                numconvenioEnabled = true;
            }

            if (Double.parseDouble(registro.getCampos().get(CAMPO_VALORTOTAL)
                            .toString()) != 0.0) {

                registro.getCampos().put(CAMPO_VALOR_CONTRATO,
                                registro.getCampos().get(CAMPO_VALORTOTAL));
                registro.getCampos().put(CAMPO_VALOR_CONTRATO,
                                registro.getCampos().get(CAMPO_VALORTOTAL));
            }

            bloquearSegunTercero();
            if (SysmanFunciones.esBdSqlServer()) {

                registro.getCampos().put("VALORTOTAL", new BigDecimal(registro
                                .getCampos().get("VALORTOTAL").toString()));
                registro.getCampos().put("VALORFINAL", new BigDecimal(registro
                                .getCampos().get("VALORFINAL").toString()));
            }
            seleccionarRequisicionesEnabled = impreso;

            extraerValores();
            impresoEnabled = impreso && (nivelUsuario == 9);
            
            nombreAbog = SysmanFunciones.nvl(registro.getCampos().get("NOMBREABOG"), "").toString();
            cargoAbog = SysmanFunciones.nvl(registro.getCampos().get("CARGOABOG"),"").toString();

        }
        fechaDiligenciamientoLocked = "SI"
                        .equals(bloqueaFechaDiligenciamientoContratos);
        fechaLocked = "SI".equals(bloqueaFechaInicializacionContrato);
        arrendamientofVisible = arrienda();
        cargarBorde();
        /*
         * Se oculta el boton "Arrendamiento" debido a que no se ha
         * implementado la llamada al formulario corresondiente.
         */
        arrendamientofVisible = false;
        listalistaRubros.getLlavesSeleccionadas().clear();
        listalistaRubros.getSeleccionados().clear();
        cargarvalorTotal();
        cargarModificacionTiempo();
        
        
        
		Map<String, Object> param = new TreeMap<>();
		param.put(CAMPO_COMPANIA, SysmanFunciones.nvlStr(compania, ""));
		param.put(GeneralParameterEnum.CODIGO.getName(),SysmanFunciones.nvlStr(SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.CLASE.getName())),""));

		Registro rsExiste;
		try {
			rsExiste = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													PcontratosControladorUrlEnum.URL73061.getValue())
											.getUrl(),
									param));
			if (rsExiste != null) {
				if (Boolean.TRUE.equals(Boolean.valueOf(rsExiste.getCampos().get("INF_FUNCIONAL").toString()))) {
					informacionFuncionalEnabled = true;
 					cargarInformacionFuncional();
 		            cargarListaDepartamento();
 		            cargarListaDepartamentoTrabajo();
 		            cargarListaCiudad();
				} else {
					informacionFuncionalEnabled = false;
				}
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
    
    private void vaciarInfFuncional() {
    	if(informacionFuncionalEnabled) {
            codPais = null;
            nombrePais = null;
            codDepartamento = null;
            nombreDepartamento = null;
            codMunicipio = null;
            nombreMunicipio = null;
            codEstadoCivil = null;
            nombreEstadoCivil = null;
            numeroHijos = null;
            valorNumeroHijos = null;
            codServPublico = null;
            nombreServPublico = null;
            esBeneficiario = false;
            esDiscapacitado = false;
            nombreNivelEmpleo = null;
            codNivelEmpleo = null;
            salario = 0;
            nombreNivelEducativo = null;
            codNivelEducativo = null;
            nombreProfesion = null;
            codProfesion = null;
            nombreDepTrabajo = null;
            codDepTrabajo = null;
            nombreDiscapacidad = null;
            codDiscapacidad = null;
    	}
	}

	private void cargarInformacionFuncional() {
    	Map<String, Object> param = new TreeMap<>();
 		param.put(CAMPO_KEY_COMPANIA, SysmanFunciones.nvlStr(compania, ""));
 		param.put(CAMPO_KEY_NUMEROCONTRATO, SysmanFunciones.nvlStr(
 						SysmanFunciones.toString(registro.getCampos()
 									.get(CAMPO_NUMERO)), ""));
 		param.put(CAMPO_KEY_TIPOCONTRATO,SysmanFunciones.nvlStr(
 						SysmanFunciones.toString(registro.getCampos()
 									.get(CAMPO_CLASEORDEN)),""));
 		 
 		Registro rsData;
 		try {
 			rsData = RegistroConverter
 					.toRegistro(
 							requestManager.get(
 									UrlServiceUtil.getInstance()
 											.getUrlServiceByUrlByEnumID(
 													PcontratosControladorUrlEnum.URL195900R.getValue())
 											.getUrl(),
 									param));
 			if (rsData != null) {
 				codPais = SysmanFunciones.toString(rsData.getCampos().get("ID_PAIS"));
 				nombrePais = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_PAIS"));
 				codDepartamento = SysmanFunciones.toString(rsData.getCampos().get("ID_DEPARTAMENTO"));
 				nombreDepartamento = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_DEPARTAMENTO"));
 				codMunicipio = SysmanFunciones.toString(rsData.getCampos().get("ID_MUNICIPIO"));
 				nombreMunicipio = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_MUNICIPIO"));
 				codEstadoCivil = SysmanFunciones.toString(rsData.getCampos().get("ESTADO_CIVIL"));
 				nombreEstadoCivil = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_ESTADO_CIVIL"));
 				numeroHijos = SysmanFunciones.toString(rsData.getCampos().get("NUMERO_HIJOS"));
 				valorNumeroHijos = SysmanFunciones.toString(rsData.getCampos().get("VALOR_NUMERO_HIJOS"));
 				codServPublico = SysmanFunciones.toString(rsData.getCampos().get("TIPO_SERVIDOR_PUBLICO"));
 				nombreServPublico = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_SERVIDOR_PUBLICO"));
 				esBeneficiario = (boolean) rsData.getCampos().get(CAMPO_BENEFICIARIO);
 				nombreNivelEmpleo = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_NIVEL_EMPLEO"));
 				codNivelEmpleo = SysmanFunciones.toString(rsData.getCampos().get("NIVEL_EMPLEO"));
 				salario = Double.parseDouble(SysmanFunciones.toString(rsData.getCampos().get("SALARIO")));
 				nombreNivelEducativo = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_NIVEL_EDUCACION"));
 				codNivelEducativo = SysmanFunciones.toString(rsData.getCampos().get("NIVEL_EDUCACION"));
 				nombreProfesion = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_PROFESION"));
 				codProfesion = SysmanFunciones.toString(rsData.getCampos().get("ID_PROFESION"));
 				nombreDepTrabajo = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_DEPTRABAJO"));
 				codDepTrabajo = SysmanFunciones.toString(rsData.getCampos().get("DEPARTAMENTO_TRABAJO"));
 				nombreDiscapacidad = SysmanFunciones.toString(rsData.getCampos().get("NOMBRE_DISCAPACIDAD"));
 				codDiscapacidad = SysmanFunciones.toString(rsData.getCampos().get("TIPO_DISCAPACIDAD"));
 				esDiscapacitado = (boolean) rsData.getCampos().get("ES_DISCAPACITADO");
 			}else {
 				vaciarInfFuncional();
 			}
 		} catch (SystemException e) {
 			logger.error(e.getMessage(), e);
 			JsfUtil.agregarMensajeError(e.getMessage());
 		}
	}

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
		obligaCampoSigec = registro.getCampos().get("SUJETOAREPORTAR").equals(true)?true:false;

        actualizarCamposNoNulos();
        eliminarCamposNoPropios();
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseF);
        registro.getCampos().put(GeneralParameterEnum.CLASE.getName(),
                        tipoContrato);

        registro.getCampos().put("MODIFICACION_TIEMPO",
                        SysmanFunciones.nvl(modificacionTiempo, ""));
        genConsecContratos();
        registro.getCampos().remove("VACIA");
        if(cambiarBorde()) {
         JsfUtil.agregarMensajeAlerta("Por favor, verifique que todos los campos requeridos estén completos en cada pestaña del formulario.");
        	return false;
        }
        if(validarSupervisor()) {
            JsfUtil.agregarMensajeAlerta("No se ha registrado la información del supervisor.");
        }
        if(sumaPorcentajes() == null) {
        	return false;
        }
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
        Date fechaAux = (Date) registro.getCampos().get(CAMPO_FECHA);

        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3681")
                        .replace("s$numero$s",
                                        registro.getCampos().get("NUMERO")
                                                        .toString())
                        .replace("s$ano$s", Integer.toString(
                                        SysmanFunciones.ano(fechaAux))));

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
		obligaCampoSigec = registro.getCampos().get("SUJETOAREPORTAR").equals(true)?true:false;
    	String descripcion;

        if (faltanCamposObligatorios()) {
            return false;
        }
        actualizarCamposNoNulos();
        eliminarCamposNoPropios();

        if(cambiarBorde()) {
        	JsfUtil.agregarMensajeAlerta("Por favor, verifique que todos los campos requeridos estén completos en cada pestaña del formulario.");
        	return false;
        }
        if(informacionFuncionalEnabled) {
        	parameters.put(CAMPO_PAIS, SysmanFunciones.nvlStr(SysmanFunciones.toString(codPais),""));
            parameters.put(CAMPO_NUMEROCONTRATO, SysmanFunciones.nvlStr(
            		SysmanFunciones.toString(registro.getLlave().get(GeneralParameterEnum.KEY_NUMERO.getName())),
            		SysmanFunciones.toString(registro.getCampos().get(CAMPO_NUMERO))));
            parameters.put(CAMPO_DEP,SysmanFunciones.nvlStr(SysmanFunciones.toString(codDepartamento),""));
            parameters.put(CAMPO_MUNICIPIO,SysmanFunciones.nvlStr(SysmanFunciones.toString(codMunicipio),""));
            parameters.put(CAMPO_ESTADO_CIVIL,SysmanFunciones.nvlStr(SysmanFunciones.toString(codEstadoCivil),""));
            parameters.put(CAMPO_NUMERO_HIJOS,SysmanFunciones.nvlStr(SysmanFunciones.toString(numeroHijos),""));
            parameters.put(CAMPO_SERVIDOR_PUBLICO,SysmanFunciones.nvlStr(SysmanFunciones.toString(codServPublico),""));
            parameters.put(CAMPO_NIVEL_EMPLEO,SysmanFunciones.nvlStr(SysmanFunciones.toString(codNivelEmpleo),""));
            parameters.put(CAMPO_NIVEL_EDUCATIVO,SysmanFunciones.nvlStr(SysmanFunciones.toString(codNivelEducativo),""));
            parameters.put(CAMPO_PROFESION,SysmanFunciones.nvlStr(SysmanFunciones.toString(codProfesion),""));
            parameters.put(CAMPO_DEP_TRABAJO,SysmanFunciones.nvlStr(SysmanFunciones.toString(codDepTrabajo),""));
            parameters.put(CAMPO_DISCAPACIDAD,SysmanFunciones.nvl(esDiscapacitado,0).equals(true)?-1:0);
            parameters.put(CAMPO_BENEFICIARIO,SysmanFunciones.nvl(esBeneficiario,0).equals(true)?-1:0);
            parameters.put(CAMPO_TIPO_DISCAPACIDAD,SysmanFunciones.nvlStr(SysmanFunciones.toString(codDiscapacidad),""));
            parameters.put(CAMPO_SALARIO,SysmanFunciones.nvlStr(SysmanFunciones.toString(salario),""));
      	  if(!validarParametrosNoNulos(parameters)) {
      		  return false;
      	  }
      	  if(registro.getCampos().get("FECHA_NACTO")==null)
      	  {
      		JsfUtil.agregarMensajeError("Por favor, diligencie la fecha de Nacimiento del Contratista");
        	return false;
      	  }

        }
        
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos().remove(CAMPO_COMPANIA);
            registro.getCampos().remove("CLASEORDEN");
           // registro.getCampos().remove(CAMPO_NUMERO);
            registro.getCampos().remove("NOMBRE_DEPENDENCIA");
            registro.getCampos().remove("MODIFICACION_TIEMPO");
            registro.getCampos().remove("NOMBREABOG");
            registro.getCampos().remove("CARGOABOG");
         //   registro.getCampos().remove("VLR_DEVUELTO");
        }
        registro.getCampos().put(CAMPO_PRECIOS_UNITARIOS,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_PRECIOS_UNITARIOS), "0"));
        registro.getCampos().put("VALORINTERVENCION",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("VALORINTERVENCION"), "0"));
        registro.getCampos().put(CAMPO_NUMPERSONASCONTRATADAS,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_NUMPERSONASCONTRATADAS),
                                        "0"));
        registro.getCampos().put("TOPEVEHICULO",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("TOPEVEHICULO"), "0"));
        registro.getCampos().put("APORTESEN",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("APORTESEN"), "0"));
        registro.getCampos().put(CAMPO_PORINPREVISTOS,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_PORINPREVISTOS), "0"));
        registro.getCampos().put("CAPACIDADTOTALCONTRATACION",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("CAPACIDADTOTALCONTRATACION"),
                                        "0"));
        registro.getCampos().put(CAMPO_NUM_CONVENIO,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_NUM_CONVENIO), "0")
                                        .toString());
        registro.getCampos().put(CAMPO_PORCDESCGLOBAL,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_PORCDESCGLOBAL), "0"));
        registro.getCampos().put("HONORARIOS",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("HONORARIOS"), "0"));
        registro.getCampos().put(CAMPO_PORCIVAGLOBAL,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("PORCIVAGLOBAL"), "0"));
        registro.getCampos().put(CAMPO_CLASEDISPONIBILIDAD,
                        SysmanFunciones.nvlStr(registro.getCampos()
                                        .get(CAMPO_CLASEDISPONIBILIDAD)
                                        .toString(),
                                        "DIS"));
        registro.getCampos().put(CAMPO_VALORPAGOS,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_VALORPAGOS), "0"));
        registro.getCampos().put("CAPACIDADRESIDUAL",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("CAPACIDADRESIDUAL"), "0"));
        registro.getCampos().put("VALRECIBO",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("VALRECIBO"), "0"));
        registro.getCampos().put(CAMPO_CEDULAINTERVENTOR,
                        SysmanFunciones.nvlStr(registro.getCampos()
                                        .get(CAMPO_CEDULAINTERVENTOR)
                                        .toString(),
                                        SysmanConstantes.CONS_TERCERO));
        registro.getCampos().put("VEHICULO",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("VEHICULO"), 0));
        registro.getCampos().put(CAMPO_PORADMINISTRACION,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_PORADMINISTRACION), 0));
        registro.getCampos().put(CAMPO_PORUTILIDADES,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_PORUTILIDADES), 0));
        registro.getCampos().put(CAMPO_VALOR_ANTICIPO,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_VALOR_ANTICIPO), 0));
        registro.getCampos().put(CAMPO_PORC_ANTICIPO,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_PORC_ANTICIPO), 0));
        registro.getCampos().put("CALIFICACION",
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get("CALIFICACION"), 0));
        registro.getCampos().put(CAMPO_VALORTOTALOP,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(CAMPO_VALORTOTALOP), 0));
        registro.getCampos().put("MODIFICACION_TIEMPO",
                        SysmanFunciones.nvl(modificacionTiempo, ""));
        registro.getCampos().put("DEPENDENCIA",codDependencia);
        

        
        
        descripcion = SysmanFunciones.nvlStr(SysmanFunciones.toString(registro.getCampos().get("DESCRIPCION")),"");
        if (descripcion.contains("'")) {
        descripcion = descripcion.replace("'", "|");
	    registro.getCampos().put("DESCRIPCION", descripcion);
        }
        
        
        if(validarSupervisor()) {
            JsfUtil.agregarMensajeAlerta("No se ha registrado la información del supervisor.");
        }
        if(sumaPorcentajes() == null) {
        	return false;
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
       	cargarListaCopiarDe();
       	if(informacionFuncionalEnabled) {
     		if (!enviarInfFuncional()) {
     			return false;
     		}
     		parameters.clear();
     	}
       	
    	 if (!ACCION_INSERTAR.equals(accion)) {
             // Para volver a cargar los campos que fueron eliminados.
             cargarRegistro(css, ACCION_MODIFICAR);
         }
    	 // </CODIGO_DESARROLLADO>
         return true;
     }

    
	private boolean enviarInfFuncional() {
		try {
		UrlBean urlActualiza = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        PcontratosControladorUrlEnum.URL195900U
                                        .getValue());
		UrlBean urlCrea = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(
                        PcontratosControladorUrlEnum.URL195900C
                                        .getValue());
		

          Parameter parameter = new Parameter();
          
          Map<String, Object> param = new TreeMap<>();
    	  param.put(CAMPO_COMPANIA,compania);
    	  param.put(CAMPO_TIPOCONTRATO,tipoContrato);
    	  param.put(CAMPO_NUMEROCONTRATO, SysmanFunciones.nvlStr(
            		SysmanFunciones.toString(registro.getLlave().get(GeneralParameterEnum.KEY_NUMERO.getName())),
            		SysmanFunciones.toString(registro.getCampos().get(CAMPO_NUMERO))));      		     			

      	     Registro rsExiste = RegistroConverter.toRegistro(
      	                        requestManager.get(UrlServiceUtil.getInstance()
      	                                        .getUrlServiceByUrlByEnumID(
      	                                        		PcontratosControladorUrlEnum.URL1959001
      	                                                                        .getValue())
      	                                        .getUrl(), param));

				if (rsExiste.getCampos().get("TOTAL").toString().equals("0")) {

					parameters.put("CREATED_BY", SessionUtil.getUser().getCodigo());
					parameters.put("DATE_CREATED", new Date());
					parameters.put(CAMPO_COMPANIA, compania);
					parameters.put(CAMPO_TIPOCONTRATO, registro.getCampos().get(CAMPO_CLASEORDEN).toString());

					parameter.setFields(parameters);
					requestManager.save(urlCrea.getUrl(), urlCrea.getMetodo(), parameter);

				} else {

					parameters.put(CAMPO_KEY_COMPANIA, compania);
					parameters.put(CAMPO_KEY_NUMEROCONTRATO,registro.getLlave().get(GeneralParameterEnum.KEY_NUMERO.getName()).toString());
					parameters.put(CAMPO_KEY_TIPOCONTRATO,tipoContrato );
					parameters.put("MODIFIED_BY", SessionUtil.getUser().getCodigo());
					parameters.put("DATE_MODIFIED", new Date());
					validarParametrosNoNulos(parameters);
					parameter.setFields(parameters);

					requestManager.update(urlActualiza.getUrl(), urlActualiza.getMetodo(), parameter);
					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

				}
      		
		} catch (SystemException e) {
			e.printStackTrace();
		}
		return true;
	}
	
    private boolean validarParametrosNoNulos(Map<String, Object> parameters) {
    	 boolean retorno = true;
         for (Map.Entry<String, Object> entry : parameters.entrySet()) {
        	 if (entry.getKey().equals((CAMPO_SALARIO))) {
            	 Double valorSalario = Double.parseDouble(SysmanFunciones.toString(entry.getValue()));
            	 if(valorSalario > 0) {
            		 continue;
            	 }else {
                     JsfUtil.agregarMensajeError("El campo '" + entry.getKey() + "' debe ser mayor a 0.");
                     retorno = false;
            	 }
        	 }
             if (entry.getValue() == null || entry.getValue() == "") {
                 if (entry.getKey().equals(CAMPO_TIPO_DISCAPACIDAD) && !esDiscapacitado) {
                     parameters.put(CAMPO_TIPO_DISCAPACIDAD, null);
                     continue; 
                 }
                    JsfUtil.agregarMensajeError("El campo '" + entry.getKey() + "' no puede estar vacío.");
                 retorno = false;
             }
         }
         return retorno;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        boolean salida = false;

        try {
            salida = ejbContratosUno.eliminarOrdendeCompra(compania,
                            registro.getCampos().get(CAMPO_CLASEORDEN)
                                            .toString(),
                            Long.parseLong(registro.getCampos()
                                            .get(CAMPO_NUMERO).toString()));
            
       	    Map<String, Object> parametros = new TreeMap<>();
            
            parametros.put(CAMPO_KEY_COMPANIA, compania);
			parametros.put(CAMPO_KEY_NUMEROCONTRATO,registro.getLlave().get(GeneralParameterEnum.KEY_NUMERO.getName()).toString());
			parametros.put(CAMPO_KEY_TIPOCONTRATO,tipoContrato );
			
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                            PcontratosControladorUrlEnum.URL195900D
                                            .getValue());
			requestManager.delete(urlDelete.getUrl(), parametros);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return salida;
        // </CODIGO_DESARROLLADO>
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
    	try {
        	Map<String, Object> param = new TreeMap<>();

        	param.put(CAMPO_COMPANIA,compania);
        	param.put(CAMPO_TIPOCONTRATO, registro.getCampos().get(CAMPO_CLASEORDEN).toString());
        	param.put(CAMPO_NUMEROCONTRATO, SysmanFunciones.nvlStr(SysmanFunciones.toString(registro.getLlave().get(GeneralParameterEnum.KEY_NUMERO.getName())),
              		SysmanFunciones.toString(registro.getCampos().get(CAMPO_NUMERO))));	     			

        	UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(PcontratosControladorUrlEnum.URL195900D.getValue());
        	
    			requestManager.delete(urlDelete.getUrl(), param);
    		} catch (SystemException e) {
    			e.printStackTrace();
    		}
        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    public void cargarBorde() {
    	obligaCampos1 = obligaCampos2 = obligaCampos3 = obligaCampos4 = obligaCampos5 = 
    	obligaCampos6 = obligaCampos7 = obligaCampos8 = obligaCampos9 = obligaCampos10 = 
    	obligaCampos11 = obligaCampos12 = obligaCampos13 = obligaCampos14 = obligaCampos15 = 
    	obligaCampos16 = obligaCampos17 = obligaCampos18 = obligaCampos19 = obligaCampos20 = 
    	obligaCampos21 = obligaCampos22 = obligaCampos23 = obligaCampos24 = obligaCampos25 = 
    	obligaCampos26 =obligaCampos27 =obligaCampos28 =obligaCampos29 ="#000000 solid 1px";
    }
    
    public void validadorCampos() {
    	camposBorde = new HashMap<>();
        camposBorde.put("FECHA", "obligaCampos1");
        camposBorde.put("FECHAFIRMA", "obligaCampos2");
        camposBorde.put("DURACION", "obligaCampos3");
        camposBorde.put("PLAZODEENTREGA", "obligaCampos4");
        camposBorde.put("LUGARDEENTREGA", "obligaCampos5");
        camposBorde.put("LINK_SECOPII", "obligaCampos6");
        camposBorde.put("DESTINOFIN", "obligaCampos7");
        camposBorde.put("GARANTIA", "obligaCampos8");
        camposBorde.put("OBJETOCONTRATO", "obligaCampos9");
        camposBorde.put("CEDULACONTRATISTA", "obligaCampos10");
        camposBorde.put("PROFESION", "obligaCampos11");
        camposBorde.put("FECHA_NACTO", "obligaCampos12");
        camposBorde.put("TIPOCONTRATISTA", "obligaCampos13");
        camposBorde.put("CEDULAINTERVENTOR", "obligaCampos14");
        camposBorde.put("INTERVENTOR", "obligaCampos15");
        camposBorde.put("CARGOINTERVENTOR", "obligaCampos16");
        camposBorde.put("TIPOINTERVENTOR", "obligaCampos17");
        camposBorde.put("NUMEROCONTRATOINTERV", "obligaCampos18");
        camposBorde.put("NOPROCESO", "obligaCampos19");
        camposBorde.put("FECHAADJUDICACION", "obligaCampos20");
        camposBorde.put("FECHALEGALIZACION", "obligaCampos21");
        camposBorde.put("FECHASUSCRIPCION", "obligaCampos22");
        camposBorde.put("TIPOADJUDICACION", "obligaCampos23");
    }

    public boolean cambiarBorde() {
    	Map<String, String> campos = null;
    	boolean rta = false;
    	if(obligaCampos) {
    		validadorCampos();
    		campos = camposBorde;
    		if(cambiaBordeCampos(campos)) {
    			rta = true;
    		}
    	}
       if(obligaCampoSigec) {
   		    validadorCamposSigec();
    		campos = camposBordeSigec;
    		if(cambiaBordeCampos(campos)) {
    			rta = true;
    		}
    	}
      	
      if(vigenciaContratoPorFirma) {
	   	   validadorCampoFechaFirma();
	  		campos = camposBordeFechaFirma;
	  		if(cambiaBordeCampos(campos)) {
				rta = true;
			}
	   	}
       //CC_1499 MROSERO        
       if(obligaCamposCGR) {
    	   validadorCamposCGR();
   		campos = camposBordeCGR;
   		if(cambiaBordeCampos(campos)){
   			rta = true;
   		}
   		
   	}
	      return rta;

    }

    private boolean cambiaBordeCampos(Map<String, String> campos) {
    	boolean rta = false;
    	 if (campos!= null) {
 	    	for (Map.Entry<String, String> entry : campos.entrySet()) {
 	            String campo = entry.getKey();
 	            String variable = entry.getValue();
 	
 	            if (SysmanFunciones.validarCampoVacio(registro.getCampos(), campo)) {
 	                asignarEstilo(variable, "#FF0000 solid 1px");
 	                rta = true;
 	            }else {
 	            	asignarEstilo(variable, "#000000 solid 1px");
 	            }
 	        }
     	}
         return rta;		
	}

	private void validadorCamposSigec() {
    	camposBordeSigec = new HashMap<>();
        camposBordeSigec.put("FECHAFINALIZACION", "obligaCampos24");
        camposBordeSigec.put("PLATAFORMA", "obligaCampos25");
        camposBordeSigec.put("EQUIV_SIGEC", "obligaCampos26");		
	}
	
	private void validadorCamposCGR() {
    	camposBordeCGR = new HashMap<>();
    	camposBordeCGR.put("CGR_CONCEPTO", "obligaCampos27");
    	camposBordeCGR.put("CGR_TIPO_GASTO", "obligaCampos28");
    	camposBordeCGR.put("CGR_SEGMENTO", "obligaCampos29");		
	}
	
	private void validadorCampoFechaFirma() {
		camposBordeFechaFirma = new HashMap<>();
		camposBordeFechaFirma.put("FECHAFIRMA", "obligaCampos2");
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
            case "obligaCampos12": obligaCampos12 = estilo; break;
            case "obligaCampos13": obligaCampos13 = estilo; break;
            case "obligaCampos14": obligaCampos14 = estilo; break;
            case "obligaCampos15": obligaCampos15 = estilo; break;
            case "obligaCampos16": obligaCampos16 = estilo; break;
            case "obligaCampos17": obligaCampos17 = estilo; break;
            case "obligaCampos18": obligaCampos18 = estilo; break;
            case "obligaCampos19": obligaCampos19 = estilo; break;
            case "obligaCampos20": obligaCampos20 = estilo; break;
            case "obligaCampos21": obligaCampos21 = estilo; break;
            case "obligaCampos22": obligaCampos22 = estilo; break;
            case "obligaCampos23": obligaCampos23 = estilo; break;
            case "obligaCampos24": obligaCampos24 = estilo; break;
            case "obligaCampos25": obligaCampos25 = estilo; break;
            case "obligaCampos26": obligaCampos26 = estilo; break;
            case "obligaCampos27": obligaCampos27 = estilo; break;
            case "obligaCampos28": obligaCampos28 = estilo; break;
            case "obligaCampos29": obligaCampos29 = estilo; break;        
        }
    }
	
	public boolean validarSupervisor() {

		boolean rta = false;
    	if(obligaCampos) {
    		
			Map<String, Object> params = new TreeMap<>();
			
			params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			params.put(GeneralParameterEnum.CLASEORDEN.getName(), claseF);
			params.put(GeneralParameterEnum.NUMEROCONTRATO.getName(), 
					registro.getCampos().get(CAMPO_NUMERO));
			try {
				Registro rs = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														PcontratosControladorUrlEnum.URL196002.getValue())
												.getUrl(),
										params));

				int cantidads = (int) rs.getCampos().get(GeneralParameterEnum.NUMERO.getName());
	
				if (cantidads == 0){
					rta = true;
				}

			} catch (SystemException |  RuntimeException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
			}
		
		}
       
	    return rta;

    }

	public BigDecimal sumaPorcentajes() {
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal porcAdmin;
		BigDecimal porcImprevistos;
		BigDecimal porcUtilidad;

		porcAdmin = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("PORADMINISTRACION"),0)));
		porcImprevistos = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("PORINPREVISTOS"),0)));
		porcUtilidad = new BigDecimal(SysmanFunciones.toString(SysmanFunciones.nvl(registro.getCampos().get("PORUTILIDADES"),0)));

		total = total.add(porcAdmin);
		total = total.add(porcImprevistos);
		total = total.add(porcUtilidad);
		
		if (total.compareTo(new BigDecimal("100")) > 0) {
	        JsfUtil.agregarMensajeAlerta(
	            idioma.getString("TB_TB4502")
	        );
	        return null;
	    }

		return total;
	}

	public void ejecutarrcCerrar() {
    	try {
    		if (parametroswf != null) {
    			Map<String,Object> parametros = new TreeMap<>();
    			parametros.put("PR_ROWKEY",parametroswf.get("PR_ROWKEY"));

    			SessionUtil.removeSessionVarContainer("parametroswf");

    			Direccionador direccionador = new Direccionador();
    			direccionador.setNumForm(Integer.toString(
    					GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR.getCodigo()));

    			direccionador.setParametros(parametros);
    			SessionUtil.redireccionarForma(direccionador,"35");
    		} else {
    			SessionUtil.redireccionar("/menu.sysman");
    		}
    	} catch (NamingException e) {
    		logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable modeloPlantilla
     * 
     * @return modeloPlantilla
     */
    public String getModeloPlantilla() {
        return modeloPlantilla;
    }

    /**
     * Asigna la variable modeloPlantilla
     * 
     * @param modeloPlantilla
     * Variable a asignar en modeloPlantilla
     */
    public void setModeloPlantilla(String modeloPlantilla) {
        this.modeloPlantilla = modeloPlantilla;
    }

    /**
     * Retorna la variable disponibilidadConcatenado
     * 
     * @return disponibilidadConcatenado
     */
    public String getDisponibilidadConcatenado() {
        return disponibilidadConcatenado;
    }

    /**
     * Asigna la variable disponibilidadConcatenado
     * 
     * @param disponibilidadConcatenado
     * Variable a asignar en disponibilidadConcatenado
     */
    public void setDisponibilidadConcatenado(String disponibilidadConcatenado) {
        this.disponibilidadConcatenado = disponibilidadConcatenado;
    }

    /**
     * Retorna la variable reservaConcatenado
     * 
     * @return reservaConcatenado
     */
    public String getReservaConcatenado() {
        return reservaConcatenado;
    }

    /**
     * Asigna la variable reservaConcatenado
     * 
     * @param reservaConcatenado
     * Variable a asignar en reservaConcatenado
     */
    public void setReservaConcatenado(String reservaConcatenado) {
        this.reservaConcatenado = reservaConcatenado;
    }

    /**
     * Retorna la variable muestraDialogoActualizaIva
     * 
     * @return muestraDialogoActualizaIva
     */
    public void setMuestraDialogoActualizaIva(
        boolean muestraDialogoActualizaIva) {
        this.muestraDialogoActualizaIva = muestraDialogoActualizaIva;
    }

    /**
     * Asigna la variable muestraDialogoActualizaIva
     * 
     * @param muestraDialogoActualizaIva
     * Variable a asignar en muestraDialogoActualizaIva
     */
    public boolean isMuestraDialogoActualizaIva() {
        return muestraDialogoActualizaIva;
    }

    /**
     * Retorna la variable txtEvalValor
     * 
     * @return txtEvalValor
     */
    public String getTxtEvalValor() {
        return txtEvalValor;
    }

    /**
     * Asigna la variable txtEvalValor
     * 
     * @param txtEvalValor
     * Variable a asignar en txtEvalValor
     */
    public void setTxtEvalValor(String txtEvalValor) {
        this.txtEvalValor = txtEvalValor;
    }

    /**
     * Retorna la variable formato
     * 
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     * 
     * @param formato
     * Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Retorna la variable nombreTercero
     * 
     * @return nombreTercero
     */
    public String getNombreTercero() {
        return nombreTercero;
    }

    /**
     * Asigna la variable nombreTercero
     * 
     * @param nombreTercero
     * Variable a asignar en nombreTercero
     */
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    /**
     * Retorna la variable ordenDeServicioPage1Enabled
     * 
     * @return ordenDeServicioPage1Enabled
     */
    public boolean isOrdenDeServicioPage1Enabled() {
        return ordenDeServicioPage1Enabled;
    }

    /**
     * Asigna la variable ordenDeServicioPage1Enabled
     * 
     * @param ordenDeServicioPage1Enabled
     * Variable a asignar en ordenDeServicioPage1Enabled
     */
    public void setOrdenDeServicioPage1Enabled(
        boolean ordenDeServicioPage1Enabled) {
        this.ordenDeServicioPage1Enabled = ordenDeServicioPage1Enabled;
    }

    /**
     * Retorna la variable ordenDeServicioPage2Enabled
     * 
     * @return ordenDeServicioPage2Enabled
     */
    public boolean isOrdenDeServicioPage2Enabled() {
        return ordenDeServicioPage2Enabled;
    }

    /**
     * Asigna la variable ordenDeServicioPage2Enabled
     * 
     * @param ordenDeServicioPage2Enabled
     * Variable a asignar en ordenDeServicioPage2Enabled
     */
    public void setOrdenDeServicioPage2Enabled(
        boolean ordenDeServicioPage2Enabled) {
        this.ordenDeServicioPage2Enabled = ordenDeServicioPage2Enabled;
    }

    /**
     * Retorna la variable ordenDeServicioPage3Enabled
     * 
     * @return ordenDeServicioPage3Enabled
     */
    public boolean isOrdenDeServicioPage3Enabled() {
        return ordenDeServicioPage3Enabled;
    }

    /**
     * Asigna la variable ordenDeServicioPage3Enabled
     * 
     * @param ordenDeServicioPage3Enabled
     * Variable a asignar en ordenDeServicioPage3Enabled
     */
    public void setOrdenDeServicioPage3Enabled(
        boolean ordenDeServicioPage3Enabled) {
        this.ordenDeServicioPage3Enabled = ordenDeServicioPage3Enabled;
    }

    /**
     * Retorna la variable totalOrdenCompra
     * 
     * @return totalOrdenCompra
     */
    public double getTxtTotal() {
        return totalOrdenCompra;
    }

    /**
     * Asigna la variable totalOrdenCompra
     * 
     * @param totalOrdenCompra
     * Variable a asignar en totalOrdenCompra
     */
    public void setTxtTotal(double txtTotal) {
        this.totalOrdenCompra = txtTotal;
    }

    /**
     * Retorna la variable actualizaPlanDeComprasLocked
     * 
     * @return actualizaPlanDeComprasLocked
     */
    public boolean isActualizaPlanDeComprasLocked() {
        return actualizaPlanDeComprasLocked;
    }

    /**
     * Asigna la variable actualizaPlanDeComprasLocked
     * 
     * @param actualizaPlanDeComprasLocked
     * Variable a asignar en actualizaPlanDeComprasLocked
     */
    public void setActualizaPlanDeComprasLocked(
        boolean actualizaPlanDeComprasLocked) {
        this.actualizaPlanDeComprasLocked = actualizaPlanDeComprasLocked;
    }

    /**
     * Retorna la variable eqtHonorariosVisible
     * 
     * @return eqtHonorariosVisible
     */
    public boolean isEqtHonorariosVisible() {
        return eqtHonorariosVisible;
    }

    /**
     * Asigna la variable eqtHonorariosVisible
     * 
     * @param eqtHonorariosVisible
     * Variable a asignar en eqtHonorariosVisible
     */
    public void setEqtHonorariosVisible(boolean eqtHonorariosVisible) {
        this.eqtHonorariosVisible = eqtHonorariosVisible;
    }

    /**
     * Retorna la variable txtHonorariosVisible
     * 
     * @return txtHonorariosVisible
     */
    public boolean isTxtHonorariosVisible() {
        return txtHonorariosVisible;
    }

    /**
     * Asigna la variable txtHonorariosVisible
     * 
     * @param txtHonorariosVisible
     * Variable a asignar en txtHonorariosVisible
     */
    public void setTxtHonorariosVisible(boolean txtHonorariosVisible) {
        this.txtHonorariosVisible = txtHonorariosVisible;
    }

    /**
     * Retorna la variable eqtVehiculoVisible
     * 
     * @return eqtVehiculoVisible
     */
    public boolean isEqtVehiculoVisible() {
        return eqtVehiculoVisible;
    }

    /**
     * Asigna la variable eqtVehiculoVisible
     * 
     * @param eqtVehiculoVisible
     * Variable a asignar en eqtVehiculoVisible
     */
    public void setEqtVehiculoVisible(boolean eqtVehiculoVisible) {
        this.eqtVehiculoVisible = eqtVehiculoVisible;
    }

    /**
     * Retorna la variable txtVehiculoVisible
     * 
     * @return txtVehiculoVisible
     */
    public boolean isTxtVehiculoVisible() {
        return txtVehiculoVisible;
    }

    /**
     * Asigna la variable txtVehiculoVisible
     * 
     * @param txtVehiculoVisible
     * Variable a asignar en txtVehiculoVisible
     */
    public void setTxtVehiculoVisible(boolean txtVehiculoVisible) {
        this.txtVehiculoVisible = txtVehiculoVisible;
    }

    /**
     * Retorna la variable etqTopeVehiculoVisible
     * 
     * @return etqTopeVehiculoVisible
     */
    public boolean isEtqTopeVehiculoVisible() {
        return etqTopeVehiculoVisible;
    }

    /**
     * Asigna la variable etqTopeVehiculoVisible
     * 
     * @param etqTopeVehiculoVisible
     * Variable a asignar en etqTopeVehiculoVisible
     */
    public void setEtqTopeVehiculoVisible(boolean etqTopeVehiculoVisible) {
        this.etqTopeVehiculoVisible = etqTopeVehiculoVisible;
    }

    /**
     * Retorna la variable txtTopeVehiculoVisible
     * 
     * @return txtTopeVehiculoVisible
     */
    public boolean isTxtTopeVehiculoVisible() {
        return txtTopeVehiculoVisible;
    }

    /**
     * Asigna la variable txtTopeVehiculoVisible
     * 
     * @param txtTopeVehiculoVisible
     * Variable a asignar en txtTopeVehiculoVisible
     */
    public void setTxtTopeVehiculoVisible(boolean txtTopeVehiculoVisible) {
        this.txtTopeVehiculoVisible = txtTopeVehiculoVisible;
    }

    /**
     * Retorna la variable cmdEliminarInfPptalVisible
     * 
     * @return cmdEliminarInfPptalVisible
     */
    public boolean isCmdEliminarInfPptalVisible() {
        return cmdEliminarInfPptalVisible;
    }

    /**
     * Asigna la variable cmdEliminarInfPptalVisible
     * 
     * @param cmdEliminarInfPptalVisible
     * Variable a asignar en cmdEliminarInfPptalVisible
     */
    public void setCmdEliminarInfPptalVisible(
        boolean cmdEliminarInfPptalVisible) {
        this.cmdEliminarInfPptalVisible = cmdEliminarInfPptalVisible;
    }

    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Retorna la variable nuevoConsecutivo
     * 
     * @return nuevoConsecutivo
     */
    public String getNuevoConsecutivo() {
        return nuevoConsecutivo;
    }

    /**
     * Asigna la variable nuevoConsecutivo
     * 
     * @param nuevoConsecutivo
     * Variable a asignar en nuevoConsecutivo
     */
    public void setNuevoConsecutivo(String nuevoConsecutivo) {
        this.nuevoConsecutivo = nuevoConsecutivo;
    }

    /**
     * Retorna la variable titulo
     * 
     * @return titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna la variable titulo
     * 
     * @param titulo
     * Variable a asignar en titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Retorna la variable claseF
     * 
     * @return claseF
     */
    public String getClaseF() {
        return claseF;
    }

    /**
     * Asigna la variable claseF
     * 
     * @param claseF
     * Variable a asignar en claseF
     */
    public void setClaseF(String claseF) {
        this.claseF = claseF;
    }

    /**
     * Retorna la variable subPContratoPermiteAgregar
     * 
     * @return subPContratoPermiteAgregar
     */
    public boolean isSubPContratoPermiteAgregar() {
        return subPContratoPermiteAgregar;
    }

    /**
     * Asigna la variable subPContratoPermiteAgregar
     * 
     * @param subPContratoPermiteAgregar
     * Variable a asignar en subPContratoPermiteAgregar
     */
    public void setSubPContratoPermiteAgregar(
        boolean subPContratoPermiteAgregar) {
        this.subPContratoPermiteAgregar = subPContratoPermiteAgregar;
    }

    /**
     * Retorna la variable subPContratoPermiteEliminar
     * 
     * @return subPContratoPermiteEliminar
     */
    public boolean isSubPContratoPermiteEliminar() {
        return subPContratoPermiteEliminar;
    }

    /**
     * Asigna la variable subPContratoPermiteEliminar
     * 
     * @param subPContratoPermiteEliminar
     * Variable a asignar en subPContratoPermiteEliminar
     */
    public void setSubPContratoPermiteEliminar(
        boolean subPContratoPermiteEliminar) {
        this.subPContratoPermiteEliminar = subPContratoPermiteEliminar;
    }

    /**
     * Retorna la variable subPContratoPermiteEditar
     * 
     * @return subPContratoPermiteEditar
     */
    public boolean isSubPContratoPermiteEditar() {
        return subPContratoPermiteEditar;
    }

    /**
     * Asigna la variable subPContratoPermiteEditar
     * 
     * @param subPContratoPermiteEditar
     * Variable a asignar en subPContratoPermiteEditar
     */
    public void setSubPContratoPermiteEditar(
        boolean subPContratoPermiteEditar) {
        this.subPContratoPermiteEditar = subPContratoPermiteEditar;
    }

    /**
     * Retorna la variable porcIVAGlobalLocked
     * 
     * @return porcIVAGlobalLocked
     */
    public boolean isPorcIVAGlobalLocked() {
        return porcIVAGlobalLocked;
    }

    /**
     * Asigna la variable porcIVAGlobalLocked
     * 
     * @param porcIVAGlobalLocked
     * Variable a asignar en porcIVAGlobalLocked
     */
    public void setPorcIVAGlobalLocked(boolean porcIVAGlobalLocked) {
        this.porcIVAGlobalLocked = porcIVAGlobalLocked;
    }

    /**
     * Retorna la variable porcDescGlobalLocked
     * 
     * @return porcDescGlobalLocked
     */
    public boolean isPorcDescGlobalLocked() {
        return porcDescGlobalLocked;
    }

    /**
     * Asigna la variable porcDescGlobalLocked
     * 
     * @param porcDescGlobalLocked
     * Variable a asignar en porcDescGlobalLocked
     */
    public void setPorcDescGlobalLocked(boolean porcDescGlobalLocked) {
        this.porcDescGlobalLocked = porcDescGlobalLocked;
    }

    /**
     * Retorna la variable lblTitulo
     * 
     * @return lblTitulo
     */
    public String getLblTitulo() {
        return lblTitulo;
    }

    /**
     * Asigna la variable lblTitulo
     * 
     * @param lblTitulo
     * Variable a asignar en lblTitulo
     */
    public void setLblTitulo(String lblTitulo) {
        this.lblTitulo = lblTitulo;
    }

    /**
     * Retorna la variable btnTercerosAportantesVisible
     * 
     * @return btnTercerosAportantesVisible
     */
    public boolean isBtnTercerosAportantesVisible() {
        return btnTercerosAportantesVisible;
    }

    /**
     * Asigna la variable btnTercerosAportantesVisible
     * 
     * @param btnTercerosAportantesVisible
     * Variable a asignar en btnTercerosAportantesVisible
     */
    public void setBtnTercerosAportantesVisible(
        boolean btnTercerosAportantesVisible) {
        this.btnTercerosAportantesVisible = btnTercerosAportantesVisible;
    }

    /**
     * Retorna la variable tipoContrato
     * 
     * @return tipoContrato
     */
    public String getTipoContrato() {
        return tipoContrato;
    }

    /**
     * Asigna la variable tipoContrato
     * 
     * @param tipoContrato
     * Variable a asignar en tipoContrato
     */
    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    /**
     * Retorna la variable copiarDe
     * 
     * @return copiarDe
     */
    public String getCopiarDe() {
        return copiarDe;
    }

    /**
     * Asigna la variable copiarDe
     * 
     * @param copiarDe
     * Variable a asignar en copiarDe
     */
    public void setCopiarDe(String copiarDe) {
        this.copiarDe = copiarDe;
    }

    /**
     * Retorna la variable tipoAdjudicacionCuantia
     * 
     * @return tipoAdjudicacionCuantia
     */
    public double getTipoAdjudicacionCuantia() {
        return tipoAdjudicacionCuantia;
    }

    /**
     * Asigna la variable tipoAdjudicacionCuantia
     * 
     * @param tipoAdjudicacionCuantia
     * Variable a asignar en tipoAdjudicacionCuantia
     */
    public void setTipoAdjudicacionCuantia(double tipoAdjudicacionCuantia) {
        this.tipoAdjudicacionCuantia = tipoAdjudicacionCuantia;
    }

    /**
     * Retorna la variable txtSubPorAdmon
     * 
     * @return txtSubPorAdmon
     */
    public double getTxtSubPorAdmon() {
        return txtSubPorAdmon;
    }

    /**
     * Asigna la variable txtSubPorAdmon
     * 
     * @param txtSubPorAdmon
     * Variable a asignar en txtSubPorAdmon
     */
    public void setTxtSubPorAdmon(double txtSubPorAdmon) {
        this.txtSubPorAdmon = txtSubPorAdmon;
    }

    /**
     * Retorna la variable txtSubPorImprevistos
     * 
     * @return txtSubPorImprevistos
     */
    public double getTxtSubPorImprevistos() {
        return txtSubPorImprevistos;
    }

    /**
     * Asigna la variable txtSubPorImprevistos
     * 
     * @param txtSubPorImprevistos
     * Variable a asignar en txtSubPorImprevistos
     */
    public void setTxtSubPorImprevistos(double vlrPorImprevistos) {
        this.txtSubPorImprevistos = vlrPorImprevistos;
    }

    /**
     * Retorna la variable txtSubPorUtilidades
     * 
     * @return txtSubPorUtilidades
     */
    public double getTxtSubPorUtilidades() {
        return txtSubPorUtilidades;
    }

    /**
     * Asigna la variable txtSubPorUtilidades
     * 
     * @param txtSubPorUtilidades
     * Variable a asignar en txtSubPorUtilidades
     */
    public void setTxtSubPorUtilidades(double txtSubPorUtilidades) {
        this.txtSubPorUtilidades = txtSubPorUtilidades;
    }

    /**
     * Retorna la variable modelo
     * 
     * @return modelo
     */
    public String getModelo() {
        return modelo;
    }

    /**
     * Asigna la variable modelo
     * 
     * @param modelo
     * Variable a asignar en modelo
     */
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    
    /**
     * Retorna la variable nombreAbog
     * 
     * @return  nombreAbog
     */
    public String getNombreAbog() {
        return nombreAbog;
    }
    
    /**
     * Asigna la variable  nombreAbog
     * 
     * @param  nombreAbog
     * Variable a asignar en  nombreAbog
     */
    public void setNombreAbog(String nombreAbog) {
        this.nombreAbog = nombreAbog;
    }
    
    /**
     * Retorna la variable cargoAbog
     * 
     * @return  cargoAbog
     */
    public String getCargoAbog() {
        return cargoAbog;
    }
    
    /**
     * Asigna la variable  cargoAbog
     * 
     * @param  cargoAbog
     * Variable a asignar en  cargoAbog
     */
    public void setCargoAbog(String cargoAbog) {
        this.cargoAbog = cargoAbog;
    }

    /**
     * Retorna la variable auxiliarColUno
     * 
     * @return auxiliarColUno
     */
    public String getAuxiliarColUno() {
        return auxiliarColUno;
    }

    /**
     * Asigna la variable auxiliarColUno
     * 
     * @param auxiliarColUno
     * Variable a asignar en auxiliarColUno
     */
    public void setAUXILIARCOL1(String auxiliarColUno) {
        this.auxiliarColUno = auxiliarColUno;
    }

    /**
     * Retorna la variable fuenteRecursosColDos
     * 
     * @return fuenteRecursosColDos
     */
    public String getFuenteRecursosColDos() {
        return fuenteRecursosColDos;
    }

    /**
     * Asigna la variable fuenteRecursosColDos
     * 
     * @param fuenteRecursosColDos
     * Variable a asignar en fuenteRecursosColDos
     */
    public void setFuenteRecursosColDos(String fuenteRecursosColDos) {
        this.fuenteRecursosColDos = fuenteRecursosColDos;
    }

    /**
     * Retorna la variable convenioLocked
     * 
     * @return convenioLocked
     */
    public boolean isConvenioLocked() {
        return convenioLocked;
    }

    /**
     * Asigna la variable convenioLocked
     * 
     * @param convenioLocked
     * Variable a asignar en convenioLocked
     */
    public void setConvenioLocked(boolean convenioLocked) {
        this.convenioLocked = convenioLocked;
    }

    /**
     * Retorna la variable btnFormaPagoVisible
     * 
     * @return btnFormaPagoVisible
     */
    public boolean isBtnFormaPagoVisible() {
        return btnFormaPagoVisible;
    }

    /**
     * Asigna la variable btnFormaPagoVisible
     * 
     * @param btnFormaPagoVisible
     * Variable a asignar en btnFormaPagoVisible
     */
    public void setBtnFormaPagoVisible(boolean btnFormaPagoVisible) {
        this.btnFormaPagoVisible = btnFormaPagoVisible;
    }

    /**
     * Retorna la variable etiqueta1006Visible
     * 
     * @return etiqueta1006Visible
     */
    public boolean isEtiqueta1006Visible() {
        return etiqueta1006Visible;
    }

    /**
     * Asigna la variable etiqueta1006Visible
     * 
     * @param etiqueta1006Visible
     * Variable a asignar en etiqueta1006Visible
     */
    public void setEtiqueta1006Visible(boolean etiqueta1006Visible) {
        this.etiqueta1006Visible = etiqueta1006Visible;
    }

    /**
     * Retorna la variable tipoContrSECOPVisible
     * 
     * @return tipoContrSECOPVisible
     */
    public boolean isTipoContrSECOPVisible() {
        return tipoContrSECOPVisible;
    }

    /**
     * Asigna la variable tipoContrSECOPVisible
     * 
     * @param tipoContrSECOPVisible
     * Variable a asignar en tipoContrSECOPVisible
     */
    public void setTipoContrSECOPVisible(boolean tipoContrSECOPVisible) {
        this.tipoContrSECOPVisible = tipoContrSECOPVisible;
    }

    /**
     * Retorna la variable etiqueta1008Visible
     * 
     * @return etiqueta1008Visible
     */
    public boolean isEtiqueta1008Visible() {
        return etiqueta1008Visible;
    }

    /**
     * Asigna la variable etiqueta1008Visible
     * 
     * @param etiqueta1008Visible
     * Variable a asignar en etiqueta1008Visible
     */
    public void setEtiqueta1008Visible(boolean etiqueta1008Visible) {
        this.etiqueta1008Visible = etiqueta1008Visible;
    }

    /**
     * Retorna la variable funcionVisible
     * 
     * @return funcionVisible
     */
    public boolean isFuncionVisible() {
        return funcionVisible;
    }

    /**
     * Asigna la variable funcionVisible
     * 
     * @param funcionVisible
     * Variable a asignar en funcionVisible
     */
    public void setFuncionVisible(boolean funcionVisible) {
        this.funcionVisible = funcionVisible;
    }

    /**
     * Retorna la variable impreso
     * 
     * @return impreso
     */
    public boolean isImpreso() {
        return impreso;
    }

    /**
     * Asigna la variable impreso
     * 
     * @param impreso
     * Variable a asignar en impreso
     */
    public void setImpreso(boolean impreso) {
        this.impreso = impreso;
    }

    /**
     * Retorna la variable convenio
     * 
     * @return convenio
     */
    public boolean isConvenio() {
        return convenio;
    }

    /**
     * Asigna la variable codEquivalenteLocked
     * 
     * @param codEquivalenteLocked
     * Variable a asignar en codEquivalenteLocked
     */
    public boolean isCodEquivalenteLocked() {
        return codEquivalenteLocked;
    }

    /**
     * Retorna la variable codEquivalenteLocked
     * 
     * @return codEquivalenteLocked
     */
    public void setCodEquivalenteLocked(boolean codEquivalenteLocked) {
        this.codEquivalenteLocked = codEquivalenteLocked;
    }

    /**
     * Asigna la variable convenio
     * 
     * @param convenio
     * Variable a asignar en convenio
     */
    public void setConvenio(boolean convenio) {
        this.convenio = convenio;
    }

    /**
     * Retorna la variable numconvenioEnabled
     * 
     * @return numconvenioEnabled
     */
    public boolean isNumconvenioEnabled() {
        return numconvenioEnabled;
    }

    /**
     * Asigna la variable numconvenioEnabled
     * 
     * @param numconvenioEnabled
     * Variable a asignar en numconvenioEnabled
     */
    public void setNumconvenioEnabled(boolean numconvenioEnabled) {
        this.numconvenioEnabled = numconvenioEnabled;
    }

    /**
     * Retorna la variable datosConvenioVisible
     * 
     * @return datosConvenioVisible
     */
    public boolean isDatosConvenioVisible() {
        return datosConvenioVisible;
    }

    /**
     * Asigna la variable datosConvenioVisible
     * 
     * @param datosConvenioVisible
     * Variable a asignar en datosConvenioVisible
     */
    public void setDatosConvenioVisible(boolean datosConvenioVisible) {
        this.datosConvenioVisible = datosConvenioVisible;
    }

    /**
     * Retorna la variable txtaportestVisible
     * 
     * @return txtaportestVisible
     */
    public boolean isTxtaportestVisible() {
        return txtaportestVisible;
    }

    /**
     * Asigna la variable txtaportestVisible
     * 
     * @param txtaportestVisible
     * Variable a asignar en txtaportestVisible
     */
    public void setTxtaportestVisible(boolean txtaportestVisible) {
        this.txtaportestVisible = txtaportestVisible;
    }

    /**
     * Retorna la variable aportestVisible
     * 
     * @return aportestVisible
     */
    public boolean isAportestVisible() {
        return aportestVisible;
    }

    /**
     * Asigna la variable aportestVisible
     * 
     * @param aportestVisible
     * Variable a asignar en aportestVisible
     */
    public void setAportestVisible(boolean aportestVisible) {
        this.aportestVisible = aportestVisible;
    }

    /**
     * Retorna la variable txtaporteseVisible
     * 
     * @return txtaporteseVisible
     */
    public boolean isTxtaporteseVisible() {
        return txtaporteseVisible;
    }

    /**
     * Asigna la variable txtaporteseVisible
     * 
     * @param txtaporteseVisible
     * Variable a asignar en txtaporteseVisible
     */
    public void setTxtaporteseVisible(boolean txtaporteseVisible) {
        this.txtaporteseVisible = txtaporteseVisible;
    }

    /**
     * Retorna la variable aporteseVisible
     * 
     * @return aporteseVisible
     */
    public boolean isAporteseVisible() {
        return aporteseVisible;
    }

    /**
     * Asigna la variable aporteseVisible
     * 
     * @param aporteseVisible
     * Variable a asignar en aporteseVisible
     */
    public void setAporteseVisible(boolean aporteseVisible) {
        this.aporteseVisible = aporteseVisible;
    }

    /**
     * Retorna la variable obrapublicaVisible
     * 
     * @return obrapublicaVisible
     */
    public boolean isObrapublicaVisible() {
        return obrapublicaVisible;
    }

    /**
     * Asigna la variable obrapublicaVisible
     * 
     * @param obrapublicaVisible
     * Variable a asignar en obrapublicaVisible
     */
    public void setObrapublicaVisible(boolean obrapublicaVisible) {
        this.obrapublicaVisible = obrapublicaVisible;
    }

    /**
     * Retorna la variable reimprimir
     * 
     * @return reimprimir
     */
    public boolean isReimprimir() {
        return reimprimir;
    }

    /**
     * Asigna la variable reimprimir
     * 
     * @param reimprimir
     * Variable a asignar en reimprimir
     */
    public void setReimprimir(boolean reimprimir) {
        this.reimprimir = reimprimir;
    }

    /**
     * Retorna la variable impresoEnabled
     * 
     * @return impresoEnabled
     */
    public boolean isImpresoEnabled() {
        return impresoEnabled;
    }

    /**
     * Asigna la variable impresoEnabled
     * 
     * @param impresoEnabled
     * Variable a asignar en impresoEnabled
     */
    public void setImpresoEnabled(boolean impresoEnabled) {
        this.impresoEnabled = impresoEnabled;
    }

    /**
     * Retorna la variable texto562Visible
     * 
     * @return texto562Visible
     */
    public boolean isTexto562Visible() {
        return texto562Visible;
    }

    /**
     * Asigna la variable texto562Visible
     * 
     * @param texto562Visible
     * Variable a asignar en texto562Visible
     */
    public void setTexto562Visible(boolean texto562Visible) {
        this.texto562Visible = texto562Visible;
    }

    /**
     * Retorna la variable etiqueta563Visible
     * 
     * @return etiqueta563Visible
     */
    public boolean isEtiqueta563Visible() {
        return etiqueta563Visible;
    }

    /**
     * Asigna la variable etiqueta563Visible
     * 
     * @param etiqueta563Visible
     * Variable a asignar en etiqueta563Visible
     */
    public void setEtiqueta563Visible(boolean etiqueta563Visible) {
        this.etiqueta563Visible = etiqueta563Visible;
    }

    /**
     * Retorna la variable etiqueta632Visible
     * 
     * @return etiqueta632Visible
     */
    public boolean isEtiqueta632Visible() {
        return etiqueta632Visible;
    }

    /**
     * Asigna la variable etiqueta632Visible
     * 
     * @param etiqueta632Visible
     * Variable a asignar en etiqueta632Visible
     */
    public void setEtiqueta632Visible(boolean etiqueta632Visible) {
        this.etiqueta632Visible = etiqueta632Visible;
    }

    /**
     * Retorna la variable etiqueta635Visible
     * 
     * @return etiqueta635Visible
     */
    public boolean isEtiqueta635Visible() {
        return etiqueta635Visible;
    }

    /**
     * Asigna la variable etiqueta635Visible
     * 
     * @param etiqueta635Visible
     * Variable a asignar en etiqueta635Visible
     */
    public void setEtiqueta635Visible(boolean etiqueta635Visible) {
        this.etiqueta635Visible = etiqueta635Visible;
    }

    /**
     * Retorna la variable convenioVisible
     * 
     * @return convenioVisible
     */
    public boolean isConvenioVisible() {
        return convenioVisible;
    }

    /**
     * Asigna la variable convenioVisible
     * 
     * @param convenioVisible
     * Variable a asignar en convenioVisible
     */
    public void setConvenioVisible(boolean convenioVisible) {
        this.convenioVisible = convenioVisible;
    }

    /**
     * Retorna la variable numconvenioVisible
     * 
     * @return numconvenioVisible
     */
    public boolean isNumconvenioVisible() {
        return numconvenioVisible;
    }

    /**
     * Asigna la variable numconvenioVisible
     * 
     * @param numconvenioVisible
     * Variable a asignar en numconvenioVisible
     */
    public void setNumconvenioVisible(boolean numconvenioVisible) {
        this.numconvenioVisible = numconvenioVisible;
    }

    /**
     * Retorna la variable txtValorTotalLocked
     * 
     * @return txtValorTotalLocked
     */
    public boolean isTxtValorTotalLocked() {
        return txtValorTotalLocked;
    }

    /**
     * Asigna la variable lblTtxtValorTotalLockeditulo
     * 
     * @param txtValorTotalLocked
     * Variable a asignar en txtValorTotalLocked
     */
    public void setTxtValorTotalLocked(boolean txtValorTotalLocked) {
        this.txtValorTotalLocked = txtValorTotalLocked;
    }

    /**
     * Retorna la variable etiqueta629Visible
     * 
     * @return etiqueta629Visible
     */
    public boolean isEtiqueta629Visible() {
        return etiqueta629Visible;
    }

    /**
     * Asigna la variable etiqueta629Visible
     * 
     * @param etiqueta629Visible
     * Variable a asignar en etiqueta629Visible
     */
    public void setEtiqueta629Visible(boolean etiqueta629Visible) {
        this.etiqueta629Visible = etiqueta629Visible;
    }

    /**
     * Retorna la variable programaVisible
     * 
     * @return programaVisible
     */
    public boolean isProgramaVisible() {
        return programaVisible;
    }

    /**
     * Asigna la variable programaVisible
     * 
     * @param programaVisible
     * Variable a asignar en programaVisible
     */
    public void setProgramaVisible(boolean programaVisible) {
        this.programaVisible = programaVisible;
    }

    /**
     * Retorna la variable proyectoVisible
     * 
     * @return proyectoVisible
     */
    public boolean isProyectoVisible() {
        return proyectoVisible;
    }

    /**
     * Asigna la variable proyectoVisible
     * 
     * @param proyectoVisible
     * Variable a asignar en proyectoVisible
     */
    public void setProyectoVisible(boolean proyectoVisible) {
        this.proyectoVisible = proyectoVisible;
    }

    /**
     * Retorna la variable cuadro625Visible
     * 
     * @return cuadro625Visible
     */
    public boolean isCuadro625Visible() {
        return cuadro625Visible;
    }

    /**
     * Asigna la variable cuadro625Visible
     * 
     * @param cuadro625Visible
     * Variable a asignar en cuadro625Visible
     */
    public void setCuadro625Visible(boolean cuadro625Visible) {
        this.cuadro625Visible = cuadro625Visible;
    }

    /**
     * Retorna la variable etNumPersonasVisible
     * 
     * @return etNumPersonasVisible
     */
    public boolean isEtNumPersonasVisible() {
        return etNumPersonasVisible;
    }

    /**
     * Asigna la variable etNumPersonasVisible
     * 
     * @param etNumPersonasVisible
     * Variable a asignar en etNumPersonasVisible
     */
    public void setEtNumPersonasVisible(boolean etNumPersonasVisible) {
        this.etNumPersonasVisible = etNumPersonasVisible;
    }

    /**
     * Retorna la variable numPersonasVisible
     * 
     * @return numPersonasVisible
     */
    public boolean isNumPersonasVisible() {
        return numPersonasVisible;
    }

    /**
     * Asigna la variable numPersonasVisible
     * 
     * @param numPersonasVisible
     * Variable a asignar en numPersonasVisible
     */
    public void setNumPersonasVisible(boolean numPersonasVisible) {
        this.numPersonasVisible = numPersonasVisible;
    }

    /**
     * Retorna la variable etiqueta325Visible
     * 
     * @return etiqueta325Visible
     */
    public boolean isEtiqueta325Visible() {
        return etiqueta325Visible;
    }

    /**
     * Asigna la variable etiqueta325Visible
     * 
     * @param etiqueta325Visible
     * Variable a asignar en etiqueta325Visible
     */
    public void setEtiqueta325Visible(boolean etiqueta325Visible) {
        this.etiqueta325Visible = etiqueta325Visible;
    }

    /**
     * Retorna la variable calcularIVAVisible
     * 
     * @return calcularIVAVisible
     */
    public boolean isCalcularIVAVisible() {
        return calcularIVAVisible;
    }

    /**
     * Asigna la variable calcularIVAVisible
     * 
     * @param calcularIVAVisible
     * Variable a asignar en calcularIVAVisible
     */
    public void setCalcularIVAVisible(boolean calcularIVAVisible) {
        this.calcularIVAVisible = calcularIVAVisible;
    }

    /**
     * Retorna la variable claseDisponibilidaD
     * 
     * @return claseDisponibilidaD
     */
    public String getClaseDisponibilidaD() {
        return claseDisponibilidaD;
    }

    /**
     * Asigna la variable claseDisponibilidaD
     * 
     * @param claseDisponibilidaD
     * Variable a asignar en claseDisponibilidaD
     */
    public void setClaseDisponibilidaD(String claseDisponibilidaD) {
        this.claseDisponibilidaD = claseDisponibilidaD;
    }

    /**
     * Retorna la variable fecha
     * 
     * @return fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Asigna la variable fecha
     * 
     * @param fecha
     * Variable a asignar en fecha
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Retorna la variable auxiliarColumn3
     * 
     * @return auxiliarColumn3
     */
    public String getAuxiliarColumn3() {
        return auxiliarColumn3;
    }

    /**
     * Asigna la variable auxiliarColumn3
     * 
     * @param auxiliarColumn3
     * Variable a asignar en auxiliarColumn3
     */
    public void setAuxiliarColumn3(String auxiliarColumn3) {
        this.auxiliarColumn3 = auxiliarColumn3;
    }

    /**
     * Retorna la variable valorAportes
     * 
     * @return valorAportes
     */
    public String getValorAportes() {
        return valorAportes;
    }

    /**
     * Asigna la variable valorAportes
     * 
     * @param valorAportes
     * Variable a asignar en valorAportes
     */
    public void setValorAportes(String valorAportes) {
        this.valorAportes = valorAportes;
    }

    /**
     * Retorna la variable manejaIvaAsumido
     * 
     * @return manejaIvaAsumido
     */
    public String getManejaIvaAsumido() {
        return manejaIvaAsumido;
    }

    /**
     * Asigna la variable manejaIvaAsumido
     * 
     * @param manejaIvaAsumido
     * Variable a asignar en manejaIvaAsumido
     */
    public void setManejaIvaAsumido(String manejaIvaAsumido) {
        this.manejaIvaAsumido = manejaIvaAsumido;
    }

    /**
     * Retorna la variable manejaNominaContratistas
     * 
     * @return manejaNominaContratistas
     */
    public String getManejaNominaContratistas() {
        return manejaNominaContratistas;
    }

    /**
     * Asigna la variable manejaNominaContratistas
     * 
     * @param manejaNominaContratistas
     * Variable a asignar en manejaNominaContratistas
     */
    public void setManejaNominaContratistas(String manejaNominaContratistas) {
        this.manejaNominaContratistas = manejaNominaContratistas;
    }

    /**
     * Retorna la variable controlaTamanioNumeroContrato
     * 
     * @return controlaTamanioNumeroContrato
     */
    public String getControlaTamanioNumeroContrato() {
        return controlaTamanioNumeroContrato;
    }

    /**
     * Asigna la variable controlaTamanioNumeroContrato
     * 
     * @param controlaTamanioNumeroContrato
     * Variable a asignar en controlaTamanioNumeroContrato
     */
    public void setControlaTamanioNumeroContrato(
        String controlaTamanioNumeroContrato) {
        this.controlaTamanioNumeroContrato = controlaTamanioNumeroContrato;
    }

    /**
     * Retorna la variable controlaValorContratoSegunDisp
     * 
     * @return controlaValorContratoSegunDisp
     */
    public String getControlaValorContratoSegunDisp() {
        return controlaValorContratoSegunDisp;
    }

    /**
     * Asigna la variable controlaValorContratoSegunDisp
     * 
     * @param controlaValorContratoSegunDisp
     * Variable a asignar en controlaValorContratoSegunDisp
     */
    public void setControlaValorContratoSegunDisp(
        String controlaValorContratoSegunDisp) {
        this.controlaValorContratoSegunDisp = controlaValorContratoSegunDisp;
    }

    /**
     * Retorna la variable controlaItemsPlanCompras
     * 
     * @return controlaItemsPlanCompras
     */
    public String getControlaItemsPlanCompras() {
        return controlaItemsPlanCompras;
    }

    /**
     * Asigna la variable controlaItemsPlanCompras
     * 
     * @param controlaItemsPlanCompras
     * Variable a asignar en controlaItemsPlanCompras
     */
    public void setControlaItemsPlanCompras(String controlaItemsPlanCompras) {
        this.controlaItemsPlanCompras = controlaItemsPlanCompras;
    }

    /**
     * Retorna la variable manejaWorkflow
     * 
     * @return manejaWorkflow
     */
    public String getManejaWorkflow() {
        return manejaWorkflow;
    }

    /**
     * Asigna la variable manejaWorkflow
     * 
     * @param manejaWorkflow
     * Variable a asignar en manejaWorkflow
     */
    public void setManejaWorkflow(String manejaWorkflow) {
        this.manejaWorkflow = manejaWorkflow;
    }

    /**
     * Retorna la variable manejaCamposObligatorios
     * 
     * @return manejaCamposObligatorios
     */
    public String getManejaCamposObligatorios() {
        return manejaCamposObligatorios;
    }

    /**
     * Asigna la variable manejaCamposObligatorios
     * 
     * @param manejaCamposObligatorios
     * Variable a asignar en manejaCamposObligatorios
     */
    public void setManejaCamposObligatorios(String manejaCamposObligatorios) {
        this.manejaCamposObligatorios = manejaCamposObligatorios;
    }

    /**
     * Retorna la variable tipoContratoCamposObligatorios
     * 
     * @return tipoContratoCamposObligatorios
     */
    public String getTipoContratoCamposObligatorios() {
        return tipoContratoCamposObligatorios;
    }

    /**
     * Asigna la variable tipoContratoCamposObligatorios
     * 
     * @param tipoContratoCamposObligatorios
     * Variable a asignar en tipoContratoCamposObligatorios
     */
    public void setTipoContratoCamposObligatorios(
        String tipoContratoCamposObligatorios) {
        this.tipoContratoCamposObligatorios = tipoContratoCamposObligatorios;
    }

    /**
     * Retorna la variable manejaCamposObligatoriosAdicionales
     * 
     * @return manejaCamposObligatoriosAdicionales
     */
    public String getManejaCamposObligatoriosAdicionales() {
        return manejaCamposObligatoriosAdicionales;
    }

    /**
     * Asigna la variable manejaCamposObligatoriosAdicionales
     * 
     * @param manejaCamposObligatoriosAdicionales
     * Variable a asignar en manejaCamposObligatoriosAdicionales
     */
    public void setManejaCamposObligatoriosAdicionales(
        String manejaCamposObligatoriosAdicionales) {
        this.manejaCamposObligatoriosAdicionales = manejaCamposObligatoriosAdicionales;
    }

    /**
     * Retorna la variable manejaCamposObligatoriosAdicionalesidsn
     * 
     * @return manejaCamposObligatoriosAdicionalesidsn
     */
    public String getManejaCamposObligatoriosAdicionalesidsn() {
        return manejaCamposObligatoriosAdicionalesidsn;
    }

    /**
     * Asigna la variable manejaCamposObligatoriosAdicionalesidsn
     * 
     * @param manejaCamposObligatoriosAdicionalesidsn
     * Variable a asignar en manejaCamposObligatoriosAdicionalesidsn
     */
    public void setManejaCamposObligatoriosAdicionalesidsn(
        String manejaCamposObligatoriosAdicionalesidsn) {
        this.manejaCamposObligatoriosAdicionalesidsn = manejaCamposObligatoriosAdicionalesidsn;
    }

    /**
     * Retorna la variable bloqueaFechaInicializacionContrato
     * 
     * @return bloqueaFechaInicializacionContrato
     */
    public String getBloqueaFechaInicializacionContrato() {
        return bloqueaFechaInicializacionContrato;
    }

    /**
     * Asigna la variable bloqueaFechaInicializacionContrato
     * 
     * @param bloqueaFechaInicializacionContrato
     * Variable a asignar en bloqueaFechaInicializacionContrato
     */
    public void setBloqueaFechaInicializacionContrato(
        String bloqueaFechaInicializacionContrato) {
        this.bloqueaFechaInicializacionContrato = bloqueaFechaInicializacionContrato;
    }

    /**
     * Retorna la variable textoGarantiaContratos
     * 
     * @return textoGarantiaContratos
     */
    public String getTextoGarantiaContratos() {
        return textoGarantiaContratos;
    }

    /**
     * Asigna la variable textoGarantiaContratos
     * 
     * @param textoGarantiaContratos
     * Variable a asignar en textoGarantiaContratos
     */
    public void setTextoGarantiaContratos(String textoGarantiaContratos) {
        this.textoGarantiaContratos = textoGarantiaContratos;
    }

    /**
     * Retorna la variable manejaSelectorClaseContrato
     * 
     * @return manejaSelectorClaseContrato
     */
    public String getManejaSelectorClaseContrato() {
        return manejaSelectorClaseContrato;
    }

    /**
     * Asigna la variable manejaSelectorClaseContrato
     * 
     * @param manejaSelectorClaseContrato
     * Variable a asignar en manejaSelectorClaseContrato
     */
    public void setManejaSelectorClaseContrato(
        String manejaSelectorClaseContrato) {
        this.manejaSelectorClaseContrato = manejaSelectorClaseContrato;
    }

    /**
     * Retorna la variable totalCuatroXmil
     * 
     * @return totalCuatroXmil
     */
    public double getTxtTotalCuatroXmil() {
        return totalCuatroXmil;
    }

    /**
     * Asigna la variable totalCuatroXmil
     * 
     * @param totalCuatroXmil
     * Variable a asignar en totalCuatroXmil
     */
    public void setTxtTotalCuatroXmil(double txtTotalCuatroXmil) {
        this.totalCuatroXmil = txtTotalCuatroXmil;
    }

    /**
     * Retorna la variable manejaBancoProyectos
     * 
     * @return manejaBancoProyectos
     */
    public String getManejaBancoProyectos() {
        return manejaBancoProyectos;
    }

    /**
     * Asigna la variable manejaBancoProyectos
     * 
     * @param manejaBancoProyectos
     * Variable a asignar en manejaBancoProyectos
     */
    public void setManejaBancoProyectos(String manejaBancoProyectos) {
        this.manejaBancoProyectos = manejaBancoProyectos;
    }

    /**
     * Retorna la variable registraPoryectosContratos
     * 
     * @return registraPoryectosContratos
     */
    public String getRegistraPoryectosContratos() {
        return registraPoryectosContratos;
    }

    /**
     * Asigna la variable registraPoryectosContratos
     * 
     * @param registraPoryectosContratos
     * Variable a asignar en registraPoryectosContratos
     */
    public void setRegistraPoryectosContratos(
        String registraPoryectosContratos) {
        this.registraPoryectosContratos = registraPoryectosContratos;
    }

    /**
     * Retorna la variable manejaProgramacionFormasPago
     * 
     * @return manejaProgramacionFormasPago
     */
    public String getManejaProgramacionFormasPago() {
        return manejaProgramacionFormasPago;
    }

    /**
     * Asigna la variable manejaProgramacionFormasPago
     * 
     * @param manejaProgramacionFormasPago
     * Variable a asignar en manejaProgramacionFormasPago
     */
    public void setManejaProgramacionFormasPago(
        String manejaProgramacionFormasPago) {
        this.manejaProgramacionFormasPago = manejaProgramacionFormasPago;
    }

    /**
     * Retorna la variable muestraContratosVigencia
     * 
     * @return muestraContratosVigencia
     */
    public String getMuestraContratosVigencia() {
        return muestraContratosVigencia;
    }

    /**
     * Asigna la variable muestraContratosVigencia
     * 
     * @param muestraContratosVigencia
     * Variable a asignar en muestraContratosVigencia
     */
    public void setMuestraContratosVigencia(String muestraContratosVigencia) {
        this.muestraContratosVigencia = muestraContratosVigencia;
    }

    /**
     * Retorna la variable tiposContratosTercerosAportantes
     * 
     * @return tiposContratosTercerosAportantes
     */
    public String getTiposContratosTercerosAportantes() {
        return tiposContratosTercerosAportantes;
    }

    /**
     * Asigna la variable tiposContratosTercerosAportantes
     * 
     * @param tiposContratosTercerosAportantes
     * Variable a asignar en tiposContratosTercerosAportantes
     */
    public void setTiposContratosTercerosAportantes(
        String tiposContratosTercerosAportantes) {
        this.tiposContratosTercerosAportantes = tiposContratosTercerosAportantes;
    }

    /**
     * Retorna la variable redondearUnitarioIvaOC
     * 
     * @return redondearUnitarioIvaOC
     */
    public String getRedondearUnitarioIvaOC() {
        return redondearUnitarioIvaOC;
    }

    /**
     * Asigna la variable redondearUnitarioIvaOC
     * 
     * @param redondearUnitarioIvaOC
     * Variable a asignar en redondearUnitarioIvaOC
     */
    public void setRedondearUnitarioIvaOC(String redondearUnitarioIvaOC) {
        this.redondearUnitarioIvaOC = redondearUnitarioIvaOC;
    }

    /**
     * Retorna la variable muestraDialogoActualiza
     * 
     * @return muestraDialogoActualiza
     */
    public boolean isMuestraDialogoActualiza() {
        return muestraDialogoActualiza;
    }

    /**
     * Asigna la variable muestraDialogoActualiza
     * 
     * @param muestraDialogoActualiza
     * Variable a asignar en muestraDialogoActualiza
     */
    public void setMuestraDialogoActualiza(boolean muestraDialogoActualiza) {
        this.muestraDialogoActualiza = muestraDialogoActualiza;
    }

    /**
     * Retorna la variable muestraDialogoConfirmar
     * 
     * @return muestraDialogoConfirmar
     */
    public boolean isMuestraDialogoConfirmar() {
        return muestraDialogoConfirmar;
    }

    /**
     * Asigna la variable muestraDialogoConfirmar
     * 
     * @param muestraDialogoConfirmar
     * Variable a asignar en muestraDialogoConfirmar
     */
    public void setMuestraDialogoConfirmar(boolean muestraDialogoConfirmar) {
        this.muestraDialogoConfirmar = muestraDialogoConfirmar;
    }

    /**
     * Retorna la variable muestraEliminarInfPptal
     * 
     * @return muestraEliminarInfPptal
     */
    public boolean isMuestraEliminarInfPptal() {
        return muestraEliminarInfPptal;
    }

    /**
     * Asigna la variable muestraEliminarInfPptal
     * 
     * @param muestraEliminarInfPptal
     * Variable a asignar en muestraEliminarInfPptal
     */
    public void setMuestraEliminarInfPptal(boolean muestraEliminarInfPptal) {
        this.muestraEliminarInfPptal = muestraEliminarInfPptal;
    }

    /**
     * Retorna la variable muestraCambiarConsecutivo
     * 
     * @return muestraCambiarConsecutivo
     */
    public boolean isMuestraCambiarConsecutivo() {
        return muestraCambiarConsecutivo;
    }

    /**
     * Asigna la variable muestraCambiarConsecutivo
     * 
     * @param muestraCambiarConsecutivo
     * Variable a asignar en muestraCambiarConsecutivo
     */
    public void setMuestraCambiarConsecutivo(
        boolean muestraCambiarConsecutivo) {
        this.muestraCambiarConsecutivo = muestraCambiarConsecutivo;
    }

    /**
     * Retorna la variable pagoEstampillasVisible
     * 
     * @return pagoEstampillasVisible
     */
    public boolean isPagoEstampillasVisible() {
        return pagoEstampillasVisible;
    }

    /**
     * Asigna la variable pagoEstampillasVisible
     * 
     * @param pagoEstampillasVisible
     * Variable a asignar en pagoEstampillasVisible
     */
    public void setPagoEstampillasVisible(boolean pagoEstampillasVisible) {
        this.pagoEstampillasVisible = pagoEstampillasVisible;
    }

    /**
     * Retorna la variable etqPagoEstampillasVisible
     * 
     * @return etqPagoEstampillasVisible
     */
    public boolean isEtqPagoEstampillasVisible() {
        return etqPagoEstampillasVisible;
    }

    /**
     * Asigna la variable etqPagoEstampillasVisible
     * 
     * @param etqPagoEstampillasVisible
     * Variable a asignar en etqPagoEstampillasVisible
     */
    public void setEtqPagoEstampillasVisible(
        boolean etqPagoEstampillasVisible) {
        this.etqPagoEstampillasVisible = etqPagoEstampillasVisible;
    }

    /**
     * Retorna la variable digitosRedondeoUnitarioIvaOc
     * 
     * @return digitosRedondeoUnitarioIvaOc
     */
    public String getDigitosRedondeoUnitarioIvaOc() {
        return digitosRedondeoUnitarioIvaOc;
    }

    /**
     * Asigna la variable digitosRedondeoUnitarioIvaOc
     * 
     * @param digitosRedondeoUnitarioIvaOc
     * Variable a asignar en digitosRedondeoUnitarioIvaOc
     */
    public void setDigitosRedondeoUnitarioIvaOc(
        String digitosRedondeoUnitarioIvaOc) {
        this.digitosRedondeoUnitarioIvaOc = digitosRedondeoUnitarioIvaOc;
    }

    /**
     * Retorna la variable redondearValorTotalOC
     * 
     * @return redondearValorTotalOC
     */
    public String getRedondearValorTotalOC() {
        return redondearValorTotalOC;
    }

    /**
     * Asigna la variable redondearValorTotalOC
     * 
     * @param redondearValorTotalOC
     * Variable a asignar en redondearValorTotalOC
     */
    public void setRedondearValorTotalOC(String redondearValorTotalOC) {
        this.redondearValorTotalOC = redondearValorTotalOC;
    }

    /**
     * Retorna la variable valorSalarioMinimo
     * 
     * @return valorSalarioMinimo
     */
    public String getValorSalarioMinimo() {
        return valorSalarioMinimo;
    }

    /**
     * Asigna la variable valorSalarioMinimo
     * 
     * @param valorSalarioMinimo
     * Variable a asignar en valorSalarioMinimo
     */
    public void setValorSalarioMinimo(String valorSalarioMinimo) {
        this.valorSalarioMinimo = valorSalarioMinimo;
    }

    /**
     * Retorna la variable manejaMeses30dias
     * 
     * @return manejaMeses30dias
     */
    public String getManejaMeses30dias() {
        return manejaMeses30dias;
    }

    /**
     * Asigna la variable manejaMeses30dias
     * 
     * @param manejaMeses30dias
     * Variable a asignar en manejaMeses30dias
     */
    public void setManejaMeses30dias(String manejaMeses30dias) {
        this.manejaMeses30dias = manejaMeses30dias;
    }

    /**
     * Retorna la variable mismoDia
     * 
     * @return mismoDia
     */
    public boolean isMismoDia() {
        return mismoDia;
    }

    /**
     * Asigna la variable mismoDia
     * 
     * @param mismoDia
     * Variable a asignar en mismoDia
     */
    public void setMismoDia(boolean mismoDia) {
        this.mismoDia = mismoDia;
    }

    /**
     * Retorna la variable fechaInicio
     * 
     * @return fechaInicio
     */
    public boolean isFechaInicio() {
        return fechaInicio;
    }

    /**
     * Asigna la variable fechaInicio
     * 
     * @param fechaInicio
     * Variable a asignar en fechaInicio
     */
    public void setFechaInicio(boolean fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Retorna la variable muestraDialogo
     * 
     * @return muestraDialogo
     */
    public boolean isMuestraDialogo() {
        return muestraDialogo;
    }

    /**
     * Asigna la variable muestraDialogo
     * 
     * @param muestraDialogo
     * Variable a asignar en muestraDialogo
     */
    public void setMuestraDialogo(boolean muestraDialogo) {
        this.muestraDialogo = muestraDialogo;
    }

    /**
     * Retorna la variable manejaPagoEstampillas
     * 
     * @return manejaPagoEstampillas
     */
    public String getManejaPagoEstampillas() {
        return manejaPagoEstampillas;
    }

    /**
     * Asigna la variable manejaPagoEstampillas
     * 
     * @param manejaPagoEstampillas
     * Variable a asignar en manejaPagoEstampillas
     */
    public void setManejaPagoEstampillas(String manejaPagoEstampillas) {
        this.manejaPagoEstampillas = manejaPagoEstampillas;
    }

    /**
     * Retorna la variable verTipoContratoCasanare
     * 
     * @return verTipoContratoCasanare
     */
    public String getVerTipoContratoCasanare() {
        return verTipoContratoCasanare;
    }

    /**
     * Asigna la variable verTipoContratoCasanare
     * 
     * @param verTipoContratoCasanare
     * Variable a asignar en verTipoContratoCasanare
     */
    public void setVerTipoContratoCasanare(String verTipoContratoCasanare) {
        this.verTipoContratoCasanare = verTipoContratoCasanare;
    }

    /**
     * Retorna la variable controlContratosValorCuantia
     * 
     * @return controlContratosValorCuantia
     */
    public String getControlContratosValorCuantia() {
        return controlContratosValorCuantia;
    }

    /**
     * Asigna la variable controlContratosValorCuantia
     * 
     * @param controlContratosValorCuantia
     * Variable a asignar en controlContratosValorCuantia
     */
    public void setControlContratosValorCuantia(
        String controlContratosValorCuantia) {
        this.controlContratosValorCuantia = controlContratosValorCuantia;
    }

    /**
     * Retorna la variable manejaControlReporteSICE
     * 
     * @return manejaControlReporteSICE
     */
    public String getManejaControlReporteSICE() {
        return manejaControlReporteSICE;
    }

    /**
     * Asigna la variable manejaControlReporteSICE
     * 
     * @param manejaControlReporteSICE
     * Variable a asignar en manejaControlReporteSICE
     */
    public void setManejaControlReporteSICE(String manejaControlReporteSICE) {
        this.manejaControlReporteSICE = manejaControlReporteSICE;
    }

    /**
     * Retorna la variable permiteEliminarInformacionPptal
     * 
     * @return permiteEliminarInformacionPptal
     */
    public String getPermiteEliminarInformacionPptal() {
        return permiteEliminarInformacionPptal;
    }

    /**
     * Asigna la variable permiteEliminarInformacionPptal
     * 
     * @param permiteEliminarInformacionPptal
     * Variable a asignar en permiteEliminarInformacionPptal
     */
    public void setPermiteEliminarInformacionPptal(
        String permiteEliminarInformacionPptal) {
        this.permiteEliminarInformacionPptal = permiteEliminarInformacionPptal;
    }

    /**
     * Retorna la variable manejaCuatroPorMil
     * 
     * @return manejaCuatroPorMil
     */
    public String getManejaCuatroPorMil() {
        return manejaCuatroPorMil;
    }

    /**
     * Asigna la variable manejaCuatroPorMil
     * 
     * @param manejaCuatroPorMil
     * Variable a asignar en manejaCuatroPorMil
     */
    public void setManejaCuatroPorMil(String manejaCuatroPorMil) {
        this.manejaCuatroPorMil = manejaCuatroPorMil;
    }

    /**
     * Retorna la variable filtroDependenciaMovimiento
     * 
     * @return filtroDependenciaMovimiento
     */
    public String getFiltroDependenciaMovimiento() {
        return filtroDependenciaMovimiento;
    }

    /**
     * Asigna la variable filtroDependenciaMovimiento
     * 
     * @param filtroDependenciaMovimiento
     * Variable a asignar en filtroDependenciaMovimiento
     */
    public void setFiltroDependenciaMovimiento(
        String filtroDependenciaMovimiento) {
        this.filtroDependenciaMovimiento = filtroDependenciaMovimiento;
    }

    /**
     * Retorna la variable digitosRedondeoValorIVAOC
     * 
     * @return digitosRedondeoValorIVAOC
     */
    public String getDigitosRedondeoValorIVAOC() {
        return digitosRedondeoValorIVAOC;
    }

    /**
     * Asigna la variable digitosRedondeoValorIVAOC
     * 
     * @param digitosRedondeoValorIVAOC
     * Variable a asignar en digitosRedondeoValorIVAOC
     */
    public void setDigitosRedondeoValorIVAOC(String digitosRedondeoValorIVAOC) {
        this.digitosRedondeoValorIVAOC = digitosRedondeoValorIVAOC;
    }

    /**
     * Retorna la variable redondearValorIvaOC
     * 
     * @return redondearValorIvaOC
     */
    public String getRedondearValorIvaOC() {
        return redondearValorIvaOC;
    }

    /**
     * Asigna la variable redondearValorIvaOC
     * 
     * @param redondearValorIvaOC
     * Variable a asignar en redondearValorIvaOC
     */
    public void setRedondearValorIvaOC(String redondearValorIvaOC) {
        this.redondearValorIvaOC = redondearValorIvaOC;
    }

    /**
     * Retorna la variable digitosRedondeoGranTotalOC
     * 
     * @return digitosRedondeoGranTotalOC
     */
    public String getDigitosRedondeoGranTotalOC() {
        return digitosRedondeoGranTotalOC;
    }

    /**
     * Asigna la variable digitosRedondeoGranTotalOC
     * 
     * @param digitosRedondeoGranTotalOC
     * Variable a asignar en digitosRedondeoGranTotalOC
     */
    public void setDigitosRedondeoGranTotalOC(
        String digitosRedondeoGranTotalOC) {
        this.digitosRedondeoGranTotalOC = digitosRedondeoGranTotalOC;
    }

    /**
     * Retorna la variable redondearGranTotalOC
     * 
     * @return redondearGranTotalOC
     */
    public String getRedondearGranTotalOC() {
        return redondearGranTotalOC;
    }

    /**
     * Asigna la variable redondearGranTotalOC
     * 
     * @param redondearGranTotalOC
     * Variable a asignar en redondearGranTotalOC
     */
    public void setRedondearGranTotalOC(String redondearGranTotalOC) {
        this.redondearGranTotalOC = redondearGranTotalOC;
    }

    /**
     * Retorna la variable digitosRedondeoValorTotalOC
     * 
     * @return digitosRedondeoValorTotalOC
     */
    public String getDigitosRedondeoValorTotalOC() {
        return digitosRedondeoValorTotalOC;
    }

    /**
     * Asigna la variable digitosRedondeoValorTotalOC
     * 
     * @param digitosRedondeoValorTotalOC
     * Variable a asignar en digitosRedondeoValorTotalOC
     */
    public void setDigitosRedondeoValorTotalOC(
        String digitosRedondeoValorTotalOC) {
        this.digitosRedondeoValorTotalOC = digitosRedondeoValorTotalOC;
    }

    /**
     * Retorna la variable unidadDuracionContrato
     * 
     * @return unidadDuracionContrato
     */
    public String getUnidadDuracionContrato() {
        return unidadDuracionContrato;
    }

    /**
     * Asigna la variable unidadDuracionContrato
     * 
     * @param unidadDuracionContrato
     * Variable a asignar en unidadDuracionContrato
     */
    public void setUnidadDuracionContrato(String unidadDuracionContrato) {
        this.unidadDuracionContrato = unidadDuracionContrato;
    }

    /**
     * Retorna la variable obligaInformacionPresupuestalContratos
     * 
     * @return obligaInformacionPresupuestalContratos
     */
    public String getObligaInformacionPresupuestalContratos() {
        return obligaInformacionPresupuestalContratos;
    }

    /**
     * Asigna la variable obligaInformacionPresupuestalContratos
     * 
     * @param obligaInformacionPresupuestalContratos
     * Variable a asignar en obligaInformacionPresupuestalContratos
     */
    public void setObligaInformacionPresupuestalContratos(
        String obligaInformacionPresupuestalContratos) {
        this.obligaInformacionPresupuestalContratos = obligaInformacionPresupuestalContratos;
    }

    /**
     * Retorna la variable claseContableMantenimientoContrPagos
     * 
     * @return claseContableMantenimientoContrPagos
     */
    public String getClaseContableMantenimientoContrPagos() {
        return claseContableMantenimientoContrPagos;
    }

    /**
     * Asigna la variable claseContableMantenimientoContrPagos
     * 
     * @param claseContableMantenimientoContrPagos
     * Variable a asignar en claseContableMantenimientoContrPagos
     */
    public void setClaseContableMantenimientoContrPagos(
        String claseContableMantenimientoContrPagos) {
        this.claseContableMantenimientoContrPagos = claseContableMantenimientoContrPagos;
    }

    /**
     * Retorna la variable indicadorPolizas
     * 
     * @return indicadorPolizas
     */
    public String getIndicadorPolizas() {
        return indicadorPolizas;
    }

    /**
     * Asigna la variable indicadorPolizas
     * 
     * @param indicadorPolizas
     * Variable a asignar en indicadorPolizas
     */
    public void setIndicadorPolizas(String indicadorPolizas) {
        this.indicadorPolizas = indicadorPolizas;
    }

    /**
     * Retorna la variable numconvenio
     * 
     * @return numconvenio
     */
    public String getNumconvenio() {
        return numconvenio;
    }

    /**
     * Asigna la variable numconvenio
     * 
     * @param numconvenio
     * Variable a asignar en numconvenio
     */
    public void setNumconvenio(String numconvenio) {
        this.numconvenio = numconvenio;
    }

    /**
     * Retorna la variable txtValorIva
     * 
     * @return txtValorIva
     */
    public double getTxtValorIva() {
        return txtValorIva;
    }

    /**
     * Asigna la variable txtValorIva
     * 
     * @param txtValorIva
     * Variable a asignar en txtValorIva
     */
    public void setTxtValorIva(double txtValorIva) {
        this.txtValorIva = txtValorIva;
    }

    /**
     * Retorna la variable txtValorDescuento
     * 
     * @return txtValorDescuento
     */
    public double getTxtValorDescuento() {
        return txtValorDescuento;
    }

    /**
     * Asigna la variable txtValorDescuento
     * 
     * @param txtValorDescuento
     * Variable a asignar en txtValorDescuento
     */
    public void setTxtValorDescuento(double txtValorDescuento) {
        this.txtValorDescuento = txtValorDescuento;
    }

    /**
     * Retorna la variable txtSub
     * 
     * @return txtSub
     */
    public double getTxtSub() {
        return txtSub;
    }

    /**
     * Asigna la variable txtSub
     * 
     * @param txtSub
     * Variable a asignar en txtSub
     */
    public void setTxtSub(double txtSub) {
        this.txtSub = txtSub;
    }

    /**
     * Retorna la variable txtAjusteAlPeso
     * 
     * @return txtAjusteAlPeso
     */
    public double getTxtAjusteAlPeso() {
        return txtAjusteAlPeso;
    }

    /**
     * Asigna la variable txtAjusteAlPeso
     * 
     * @param txtAjusteAlPeso
     * Variable a asignar en txtAjusteAlPeso
     */
    public void setTxtAjusteAlPeso(double txtAjusteAlPeso) {
        this.txtAjusteAlPeso = txtAjusteAlPeso;
    }

    /**
     * Retorna la variable calificaContratista
     * 
     * @return calificaContratista
     */
    public String getCalificaContratista() {
        return calificaContratista;
    }

    /**
     * Asigna la variable calificaContratista
     * 
     * @param calificaContratista
     * Variable a asignar en calificaContratista
     */
    public void setCalificaContratista(String calificaContratista) {
        this.calificaContratista = calificaContratista;
    }

    /**
     * Retorna la variable txtDigRedoValorUnitarioIVA
     * 
     * @return txtDigRedoValorUnitarioIVA
     */
    public String getTxtDigRedoValorUnitarioIVA() {
        return txtDigRedoValorUnitarioIVA;
    }

    /**
     * Asigna la variable txtDigRedoValorUnitarioIVA
     * 
     * @param txtDigRedoValorUnitarioIVA
     * Variable a asignar en txtDigRedoValorUnitarioIVA
     */
    public void setTxtDigRedoValorUnitarioIVA(
        String txtDigRedoValorUnitarioIVA) {
        this.txtDigRedoValorUnitarioIVA = txtDigRedoValorUnitarioIVA;
    }

    /**
     * Retorna la variable txtDigRedonTotal
     * 
     * @return txtDigRedonTotal
     */
    public String getTxtDigRedonTotal() {
        return txtDigRedonTotal;
    }

    /**
     * Asigna la variable txtDigRedonTotal
     * 
     * @param txtDigRedonTotal
     * Variable a asignar en txtDigRedonTotal
     */
    public void setTxtDigRedonTotal(String txtDigRedonTotal) {
        this.txtDigRedonTotal = txtDigRedonTotal;
    }

    /**
     * Retorna la variable txtDigitosRedondeoGranTotal
     * 
     * @return txtDigitosRedondeoGranTotal
     */
    public int getTxtDigitosRedondeoGranTotal() {
        return txtDigitosRedondeoGranTotal;
    }

    /**
     * Asigna la variable txtDigitosRedondeoGranTotal
     * 
     * @param txtDigitosRedondeoGranTotal
     * Variable a asignar en txtDigitosRedondeoGranTotal
     */
    public void setTxtDigitosRedondeoGranTotal(
        int txtDigitosRedondeoGranTotal) {
        this.txtDigitosRedondeoGranTotal = txtDigitosRedondeoGranTotal;
    }

    /**
     * Retorna la variable txtDigitosRedondeoValorIVA
     * 
     * @return txtDigitosRedondeoValorIVA
     */
    public int getTxtDigitosRedondeoValorIVA() {
        return txtDigitosRedondeoValorIVA;
    }

    /**
     * Asigna la variable txtDigitosRedondeoValorIVA
     * 
     * @param txtDigitosRedondeoValorIVA
     * Variable a asignar en txtDigitosRedondeoValorIVA
     */
    public void setTxtDigitosRedondeoValorIVA(int txtDigitosRedondeoValorIVA) {
        this.txtDigitosRedondeoValorIVA = txtDigitosRedondeoValorIVA;
    }

    /**
     * Retorna la variable nominaContratistas
     * 
     * @return nominaContratistas
     */
    public String getNominaContratistas() {
        return nominaContratistas;
    }

    /**
     * Asigna la variable nominaContratistas
     * 
     * @param nominaContratistas
     * Variable a asignar en nominaContratistas
     */
    public void setNominaContratistas(String nominaContratistas) {
        this.nominaContratistas = nominaContratistas;
    }

    /**
     * Retorna la variable reportarSICEVisible
     * 
     * @return reportarSICEVisible
     */
    public boolean isReportarSICEVisible() {
        return reportarSICEVisible;
    }

    /**
     * Asigna la variable reportarSICEVisible
     * 
     * @param reportarSICEVisible
     * Variable a asignar en reportarSICEVisible
     */
    public void setReportarSICEVisible(boolean reportarSICEVisible) {
        this.reportarSICEVisible = reportarSICEVisible;
    }

    /**
     * Retorna la variable eReportarSICEVisible
     * 
     * @return eReportarSICEVisible
     */
    public boolean iseReportarSICEVisible() {
        return eReportarSICEVisible;
    }

    /**
     * Asigna la variable eReportarSICEVisible
     * 
     * @param eReportarSICEVisible
     * Variable a asignar en eReportarSICEVisible
     */
    public void seteReportarSICEVisible(boolean eReportarSICEVisible) {
        this.eReportarSICEVisible = eReportarSICEVisible;
    }

    /**
     * Retorna la variable valorContratoCuantiaVisible
     * 
     * @return valorContratoCuantiaVisible
     */
    public boolean isValorContratoCuantiaVisible() {
        return valorContratoCuantiaVisible;
    }

    /**
     * Asigna la variable valorContratoCuantiaVisible
     * 
     * @param valorContratoCuantiaVisible
     * Variable a asignar en valorContratoCuantiaVisible
     */
    public void setValorContratoCuantiaVisible(
        boolean valorContratoCuantiaVisible) {
        this.valorContratoCuantiaVisible = valorContratoCuantiaVisible;
    }

    /**
     * Retorna la variable etiqueta537Visible
     * 
     * @return etiqueta537Visible
     */
    public boolean isEtiqueta537Visible() {
        return etiqueta537Visible;
    }

    /**
     * Asigna la variable etiqueta537Visible
     * 
     * @param etiqueta537Visible
     * Variable a asignar en etiqueta537Visible
     */
    public void setEtiqueta537Visible(boolean etiqueta537Visible) {
        this.etiqueta537Visible = etiqueta537Visible;
    }

    /**
     * Retorna la variable clasificacionContratoVisible
     * 
     * @return clasificacionContratoVisible
     */
    public boolean isClasificacionContratoVisible() {
        return clasificacionContratoVisible;
    }

    /**
     * Asigna la variable clasificacionContratoVisible
     * 
     * @param clasificacionContratoVisible
     * Variable a asignar en clasificacionContratoVisible
     */
    public void setClasificacionContratoVisible(
        boolean clasificacionContratoVisible) {
        this.clasificacionContratoVisible = clasificacionContratoVisible;
    }

    /**
     * Retorna la variable clasR6Visible
     * 
     * @return clasR6Visible
     */
    public boolean isClasR6Visible() {
        return clasR6Visible;
    }

    /**
     * Asigna la variable clasR6Visible
     * 
     * @param clasR6Visible
     * Variable a asignar en clasR6Visible
     */
    public void setClasR6Visible(boolean clasR6Visible) {
        this.clasR6Visible = clasR6Visible;
    }

    /**
     * Retorna la variable componenteBPVisible
     * 
     * @return componenteBPVisible
     */
    public boolean isComponenteBPVisible() {
        return componenteBPVisible;
    }

    /**
     * Asigna la variable componenteBPVisible
     * 
     * @param componenteBPVisible
     * Variable a asignar en componenteBPVisible
     */
    public void setComponenteBPVisible(boolean componenteBPVisible) {
        this.componenteBPVisible = componenteBPVisible;
    }

    /**
     * Retorna la variable ordendecompraAuxiliaresVisible
     * 
     * @return ordendecompraAuxiliaresVisible
     */
    public boolean isOrdendecompraAuxiliaresVisible() {
        return ordendecompraAuxiliaresVisible;
    }

    /**
     * Asigna la variable ordendecompraAuxiliaresVisible
     * 
     * @param ordendecompraAuxiliaresVisible
     * Variable a asignar en ordendecompraAuxiliaresVisible
     */
    public void setOrdendecompraAuxiliaresVisible(
        boolean ordendecompraAuxiliaresVisible) {
        this.ordendecompraAuxiliaresVisible = ordendecompraAuxiliaresVisible;
    }

    /**
     * Retorna la variable etiqueta52Visible
     * 
     * @return etiqueta52Visible
     */
    public boolean isEtiqueta52Visible() {
        return etiqueta52Visible;
    }

    /**
     * Asigna la variable etiqueta52Visible
     * 
     * @param etiqueta52Visible
     * Variable a asignar en etiqueta52Visible
     */
    public void setEtiqueta52Visible(boolean etiqueta52Visible) {
        this.etiqueta52Visible = etiqueta52Visible;
    }

    /**
     * Retorna la variable auxiliarVisible
     * 
     * @return auxiliarVisible
     */
    public boolean isAuxiliarVisible() {
        return auxiliarVisible;
    }

    /**
     * Asigna la variable auxiliarVisible
     * 
     * @param auxiliarVisible
     * Variable a asignar en auxiliarVisible
     */
    public void setAuxiliarVisible(boolean auxiliarVisible) {
        this.auxiliarVisible = auxiliarVisible;
    }

    /**
     * Retorna la variable txtAuxiliarVisible
     * 
     * @return txtAuxiliarVisible
     */
    public boolean isTxtAuxiliarVisible() {
        return txtAuxiliarVisible;
    }

    /**
     * Asigna la variable txtAuxiliarVisible
     * 
     * @param txtAuxiliarVisible
     * Variable a asignar en txtAuxiliarVisible
     */
    public void setTxtAuxiliarVisible(boolean txtAuxiliarVisible) {
        this.txtAuxiliarVisible = txtAuxiliarVisible;
    }

    /**
     * Retorna la variable etiqueta53Visible
     * 
     * @return etiqueta53Visible
     */
    public boolean isEtiqueta53Visible() {
        return etiqueta53Visible;
    }

    /**
     * Asigna la variable etiqueta53Visible
     * 
     * @param etiqueta53Visible
     * Variable a asignar en etiqueta53Visible
     */
    public void setEtiqueta53Visible(boolean etiqueta53Visible) {
        this.etiqueta53Visible = etiqueta53Visible;
    }

    /**
     * Retorna la variable fuenteRecursosVisible
     * 
     * @return fuenteRecursosVisible
     */
    public boolean isFuenteRecursosVisible() {
        return fuenteRecursosVisible;
    }

    /**
     * Asigna la variable fuenteRecursosVisible
     * 
     * @param fuenteRecursosVisible
     * Variable a asignar en fuenteRecursosVisible
     */
    public void setFuenteRecursosVisible(boolean fuenteRecursosVisible) {
        this.fuenteRecursosVisible = fuenteRecursosVisible;
    }

    /**
     * Retorna la variable etiqueta211Visible
     * 
     * @return etiqueta211Visible
     */
    public boolean isEtiqueta211Visible() {
        return etiqueta211Visible;
    }

    /**
     * Asigna la variable etiqueta211Visible
     * 
     * @param etiqueta211Visible
     * Variable a asignar en etiqueta211Visible
     */
    public void setEtiqueta211Visible(boolean etiqueta211Visible) {
        this.etiqueta211Visible = etiqueta211Visible;
    }

    /**
     * Retorna la variable destinoVisible
     * 
     * @return destinoVisible
     */
    public boolean isDestinoVisible() {
        return destinoVisible;
    }

    /**
     * Asigna la variable destinoVisible
     * 
     * @param destinoVisible
     * Variable a asignar en destinoVisible
     */
    public void setDestinoVisible(boolean destinoVisible) {
        this.destinoVisible = destinoVisible;
    }

    /**
     * Retorna la variable tituloAportantesVisible
     * 
     * @return tituloAportantesVisible
     */
    public boolean isTituloAportantesVisible() {
        return tituloAportantesVisible;
    }

    /**
     * Asigna la variable tituloAportantesVisible
     * 
     * @param tituloAportantesVisible
     * Variable a asignar en tituloAportantesVisible
     */
    public void setTituloAportantesVisible(boolean tituloAportantesVisible) {
        this.tituloAportantesVisible = tituloAportantesVisible;
    }

    /**
     * Retorna la variable etiqueta525Visible
     * 
     * @return etiqueta525Visible
     */
    public boolean isEtiqueta525Visible() {
        return etiqueta525Visible;
    }

    /**
     * Asigna la variable etiqueta525Visible
     * 
     * @param etiqueta525Visible
     * Variable a asignar en etiqueta525Visible
     */
    public void setEtiqueta525Visible(boolean etiqueta525Visible) {
        this.etiqueta525Visible = etiqueta525Visible;
    }

    /**
     * Retorna la variable copiarDeEnabled
     * 
     * @return copiarDeEnabled
     */
    public boolean isCopiarDeEnabled() {
        return copiarDeEnabled;
    }

    /**
     * Asigna la variable copiarDeEnabled
     * 
     * @param copiarDeEnabled
     * Variable a asignar en copiarDeEnabled
     */
    public void setCopiarDeEnabled(boolean copiarDeEnabled) {
        this.copiarDeEnabled = copiarDeEnabled;
    }

    /**
     * Retorna la variable etiqueta528Visible
     * 
     * @return etiqueta528Visible
     */
    public boolean isEtiqueta528Visible() {
        return etiqueta528Visible;
    }

    /**
     * Asigna la variable etiqueta528Visible
     * 
     * @param etiqueta528Visible
     * Variable a asignar en etiqueta528Visible
     */
    public void setEtiqueta528Visible(boolean etiqueta528Visible) {
        this.etiqueta528Visible = etiqueta528Visible;
    }

    /**
     * Retorna la variable terceroLocked
     * 
     * @return terceroLocked
     */
    public boolean isTerceroLocked() {
        return terceroLocked;
    }

    /**
     * Asigna la variable terceroLocked
     * 
     * @param terceroLocked
     * Variable a asignar en terceroLocked
     */
    public void setTerceroLocked(boolean terceroLocked) {
        this.terceroLocked = terceroLocked;
    }

    /**
     * Retorna la variable itemsCEnabled
     * 
     * @return itemsCEnabled
     */
    public boolean isItemsCEnabled() {
        return itemsCEnabled;
    }

    /**
     * Asigna la variable itemsCEnabled
     * 
     * @param itemsCEnabled
     * Variable a asignar en itemsCEnabled
     */
    public void setItemsCEnabled(boolean itemsCEnabled) {
        this.itemsCEnabled = itemsCEnabled;
    }

    /**
     * Retorna la variable datosDelContratoEnabled
     * 
     * @return datosDelContratoEnabled
     */
    public boolean isDatosDelContratoEnabled() {
        return datosDelContratoEnabled;
    }

    /**
     * Asigna la variable datosDelContratoEnabled
     * 
     * @param datosDelContratoEnabled
     * Variable a asignar en datosDelContratoEnabled
     */
    public void setDatosDelContratoEnabled(boolean datosDelContratoEnabled) {
        this.datosDelContratoEnabled = datosDelContratoEnabled;
    }

    /**
     * Retorna la variable contratistaInterventorEnabled
     * 
     * @return contratistaInterventorEnabled
     */
    public boolean isContratistaInterventorEnabled() {
        return contratistaInterventorEnabled;
    }

    /**
     * Asigna la variable contratistaInterventorEnabled
     * 
     * @param contratistaInterventorEnabled
     * Variable a asignar en contratistaInterventorEnabled
     */
    public void setContratistaInterventorEnabled(
        boolean contratistaInterventorEnabled) {
        this.contratistaInterventorEnabled = contratistaInterventorEnabled;
    }

    /**
     * Retorna la variable informacionJuridicaEnabled
     * 
     * @return informacionJuridicaEnabled
     */
    public boolean isInformacionJuridicaEnabled() {
        return informacionJuridicaEnabled;
    }

    /**
     * Asigna la variable informacionJuridicaEnabled
     * 
     * @param informacionJuridicaEnabled
     * Variable a asignar en informacionJuridicaEnabled
     */
    public void setInformacionJuridicaEnabled(
        boolean informacionJuridicaEnabled) {
        this.informacionJuridicaEnabled = informacionJuridicaEnabled;
    }

    /**
     * Retorna la variable informacionPresupuestalEnabled
     * 
     * @return informacionPresupuestalEnabled
     */
    public boolean isInformacionPresupuestalEnabled() {
        return informacionPresupuestalEnabled;
    }

    /**
     * Asigna la variable informacionPresupuestalEnabled
     * 
     * @param informacionPresupuestalEnabled
     * Variable a asignar en informacionPresupuestalEnabled
     */
    public void setInformacionPresupuestalEnabled(
        boolean informacionPresupuestalEnabled) {
        this.informacionPresupuestalEnabled = informacionPresupuestalEnabled;
    }

    /**
     * Retorna la variable pagina33Enabled
     * 
     * @return pagina33Enabled
     */
    public boolean isPagina33Enabled() {
        return pagina33Enabled;
    }

    /**
     * Asigna la variable pagina33Enabled
     * 
     * @param pagina33Enabled
     * Variable a asignar en pagina33Enabled
     */
    public void setPagina33Enabled(boolean pagina33Enabled) {
        this.pagina33Enabled = pagina33Enabled;
    }

    /**
     * Retorna la variable mensajeActualizaIVA
     * 
     * @return mensajeActualizaIVA
     */
    public String getMensajeActualizaIVA() {
        return mensajeActualizaIVA;
    }

    /**
     * Retorna la variable fechaDiligenciamientoLocked
     * 
     * @return fechaDiligenciamientoLocked
     */
    public boolean isFechaDiligenciamientoLocked() {
        return fechaDiligenciamientoLocked;
    }

    /**
     * Asigna la variable fechaDiligenciamientoLocked
     * 
     * @param fechaDiligenciamientoLocked
     * Variable a asignar en fechaDiligenciamientoLocked
     */
    public void setFechaDiligenciamientoLocked(
        boolean fechaDiligenciamientoLocked) {
        this.fechaDiligenciamientoLocked = fechaDiligenciamientoLocked;
    }

    /**
     * Retorna la variable diasLocked
     * 
     * @return diasLocked
     */
    public boolean isDiasLocked() {
        return diasLocked;
    }

    /**
     * Asigna la variable diasLocked
     * 
     * @param diasLocked
     * Variable a asignar en diasLocked
     */
    public void setDiasLocked(boolean diasLocked) {
        this.diasLocked = diasLocked;
    }

    /**
     * Retorna la variable plazoEntregaLocked
     * 
     * @return plazoEntregaLocked
     */
    public boolean isPlazoEntregaLocked() {
        return plazoEntregaLocked;
    }

    /**
     * Asigna la variable plazoEntregaLocked
     * 
     * @param plazoEntregaLocked
     * Variable a asignar en plazoEntregaLocked
     */
    public void setPlazoEntregaLocked(boolean plazoEntregaLocked) {
        this.plazoEntregaLocked = plazoEntregaLocked;
    }

    /**
     * Retorna la variable unidadPlazoLocked
     * 
     * @return unidadPlazoLocked
     */
    public boolean isUnidadPlazoLocked() {
        return unidadPlazoLocked;
    }

    /**
     * Asigna la variable unidadPlazoLocked
     * 
     * @param unidadPlazoLocked
     * Variable a asignar en unidadPlazoLocked
     */
    public void setUnidadPlazoLocked(boolean unidadPlazoLocked) {
        this.unidadPlazoLocked = unidadPlazoLocked;
    }

    /**
     * Retorna la variable lugarDeEntregaLocked
     * 
     * @return lugarDeEntregaLocked
     */
    public boolean isLugarDeEntregaLocked() {
        return lugarDeEntregaLocked;
    }

    /**
     * Asigna la variable lugarDeEntregaLocked
     * 
     * @param lugarDeEntregaLocked
     * Variable a asignar en lugarDeEntregaLocked
     */
    public void setLugarDeEntregaLocked(boolean lugarDeEntregaLocked) {
        this.lugarDeEntregaLocked = lugarDeEntregaLocked;
    }

    /**
     * Retorna la variable tipoAdjudicacionLocked
     * 
     * @return tipoAdjudicacionLocked
     */
    public boolean isTipoAdjudicacionLocked() {
        return tipoAdjudicacionLocked;
    }

    /**
     * Asigna la variable tipoAdjudicacionLocked
     * 
     * @param tipoAdjudicacionLocked
     * Variable a asignar en tipoAdjudicacionLocked
     */
    public void setTipoAdjudicacionLocked(boolean tipoAdjudicacionLocked) {
        this.tipoAdjudicacionLocked = tipoAdjudicacionLocked;
    }

    /**
     * Retorna la variable sectorLocked
     * 
     * @return sectorLocked
     */
    public boolean isSectorLocked() {
        return sectorLocked;
    }

    /**
     * Asigna la variable sectorLocked
     * 
     * @param sectorLocked
     * Variable a asignar en sectorLocked
     */
    public void setSectorLocked(boolean sectorLocked) {
        this.sectorLocked = sectorLocked;
    }

    /**
     * Retorna la variable tipoContratosLocked
     * 
     * @return tipoContratosLocked
     */
    public boolean isTipoContratosLocked() {
        return tipoContratosLocked;
    }

    /**
     * Asigna la variable tipoContratosLocked
     * 
     * @param tipoContratosLocked
     * Variable a asignar en tipoContratosLocked
     */
    public void setTipoContratosLocked(boolean tipoContratosLocked) {
        this.tipoContratosLocked = tipoContratosLocked;
    }

    /**
     * Retorna la variable tipoVeeduriaLocked
     * 
     * @return tipoVeeduriaLocked
     */
    public boolean isTipoVeeduriaLocked() {
        return tipoVeeduriaLocked;
    }

    /**
     * Asigna la variable tipoVeeduriaLocked
     * 
     * @param tipoVeeduriaLocked
     * Variable a asignar en tipoVeeduriaLocked
     */
    public void setTipoVeeduriaLocked(boolean tipoVeeduriaLocked) {
        this.tipoVeeduriaLocked = tipoVeeduriaLocked;
    }

    /**
     * Retorna la variable tipologiaLocked
     * 
     * @return tipologiaLocked
     */
    public boolean isTipologiaLocked() {
        return tipologiaLocked;
    }

    /**
     * Asigna la variable tipologiaLocked
     * 
     * @param tipologiaLocked
     * Variable a asignar en tipologiaLocked
     */
    public void setTipologiaLocked(boolean tipologiaLocked) {
        this.tipologiaLocked = tipologiaLocked;
    }

    /**
     * Retorna la variable numeroActaLocked
     * 
     * @return numeroActaLocked
     */
    public boolean isNumeroActaLocked() {
        return numeroActaLocked;
    }

    /**
     * Asigna la variable numeroActaLocked
     * 
     * @param numeroActaLocked
     * Variable a asignar en numeroActaLocked
     */
    public void setNumeroActaLocked(boolean numeroActaLocked) {
        this.numeroActaLocked = numeroActaLocked;
    }

    /**
     * Retorna la variable claseContratoLocked
     * 
     * @return claseContratoLocked
     */
    public boolean isClaseContratoLocked() {
        return claseContratoLocked;
    }

    /**
     * Asigna la variable claseContratoLocked
     * 
     * @param claseContratoLocked
     * Variable a asignar en claseContratoLocked
     */
    public void setClaseContratoLocked(boolean claseContratoLocked) {
        this.claseContratoLocked = claseContratoLocked;
    }

    /**
     * Retorna la variable descripcionLocked
     * 
     * @return descripcionLocked
     */
    public boolean isDescripcionLocked() {
        return descripcionLocked;
    }

    /**
     * Asigna la variable descripcionLocked
     * 
     * @param descripcionLocked
     * Variable a asignar en descripcionLocked
     */
    public void setDescripcionLocked(boolean descripcionLocked) {
        this.descripcionLocked = descripcionLocked;
    }

    /**
     * Retorna la variable formaDePagoLocked
     * 
     * @return formaDePagoLocked
     */
    public boolean isFormaDePagoLocked() {
        return formaDePagoLocked;
    }

    /**
     * Asigna la variable formaDePagoLocked
     * 
     * @param formaDePagoLocked
     * Variable a asignar en formaDePagoLocked
     */
    public void setFormaDePagoLocked(boolean formaDePagoLocked) {
        this.formaDePagoLocked = formaDePagoLocked;
    }

    /**
     * Retorna la variable cuatroPorMil
     * 
     * @return cuatroPorMil
     */
    public boolean isCuatroPorMil() {
        return cuatroPorMil;
    }

    /**
     * Asigna la variable cuatroPorMil
     * 
     * @param cuatroPorMil
     * Variable a asignar en cuatroPorMil
     */
    public void setCuatroPorMil(boolean cuatroPorMil) {
        this.cuatroPorMil = cuatroPorMil;
    }

    /**
     * Retorna la variable fechaLocked
     * 
     * @return fechaLocked
     */
    public boolean isFechaLocked() {
        return fechaLocked;
    }

    /**
     * Asigna la variable fechaLocked
     * 
     * @param fechaLocked
     * Variable a asignar en fechaLocked
     */
    public void setFechaLocked(boolean fechaLocked) {
        this.fechaLocked = fechaLocked;
    }

    /**
     * Retorna la variable objetoContratoLocked
     * 
     * @return objetoContratoLocked
     */
    public boolean isObjetoContratoLocked() {
        return objetoContratoLocked;
    }

    /**
     * Asigna la variable objetoContratoLocked
     * 
     * @param objetoContratoLocked
     * Variable a asignar en objetoContratoLocked
     */
    public void setObjetoContratoLocked(boolean objetoContratoLocked) {
        this.objetoContratoLocked = objetoContratoLocked;
    }

    /**
     * Retorna la variable garantiaLocked
     * 
     * @return garantiaLocked
     */
    public boolean isGarantiaLocked() {
        return garantiaLocked;
    }

    /**
     * Asigna la variable garantiaLocked
     * 
     * @param garantiaLocked
     * Variable a asignar en garantiaLocked
     */
    public void setGarantiaLocked(boolean garantiaLocked) {
        this.garantiaLocked = garantiaLocked;
    }

    /**
     * Retorna la variable fechaFinalizacionLocked
     * 
     * @return fechaFinalizacionLocked
     */
    public boolean isFechaFinalizacionLocked() {
        return fechaFinalizacionLocked;
    }

    /**
     * Asigna la variable fechaFinalizacionLocked
     * 
     * @param fechaFinalizacionLocked
     * Variable a asignar en fechaFinalizacionLocked
     */
    public void setFechaFinalizacionLocked(boolean fechaFinalizacionLocked) {
        this.fechaFinalizacionLocked = fechaFinalizacionLocked;
    }

    /**
     * Retorna la variable duracionLocked
     * 
     * @return duracionLocked
     */
    public boolean isDuracionLocked() {
        return duracionLocked;
    }

    /**
     * Asigna la variable duracionLocked
     * 
     * @param duracionLocked
     * Variable a asignar en duracionLocked
     */
    public void setDuracionLocked(boolean duracionLocked) {
        this.duracionLocked = duracionLocked;
    }

    /**
     * Retorna la variable dependenciaLocked
     * 
     * @return dependenciaLocked
     */
    public boolean isDependenciaLocked() {
        return dependenciaLocked;
    }

    /**
     * Asigna la variable dependenciaLocked
     * 
     * @param dependenciaLocked
     * Variable a asignar en dependenciaLocked
     */
    public void setDependenciaLocked(boolean dependenciaLocked) {
        this.dependenciaLocked = dependenciaLocked;
    }

    /**
     * Retorna la variable considerandosLocked
     * 
     * @return considerandosLocked
     */
    public boolean isConsiderandosLocked() {
        return considerandosLocked;
    }

    /**
     * Asigna la variable considerandosLocked
     * 
     * @param considerandosLocked
     * Variable a asignar en considerandosLocked
     */
    public void setConsiderandosLocked(boolean considerandosLocked) {
        this.considerandosLocked = considerandosLocked;
    }

    /**
     * Retorna la variable valorFinalLocked
     * 
     * @return valorFinalLocked
     */
    public boolean isValorFinalLocked() {
        return valorFinalLocked;
    }

    /**
     * Asigna la variable valorFinalLocked
     * 
     * @param valorFinalLocked
     * Variable a asignar en valorFinalLocked
     */
    public void setValorFinalLocked(boolean valorFinalLocked) {
        this.valorFinalLocked = valorFinalLocked;
    }

    /**
     * Retorna la variable texto217Locked
     * 
     * @return texto217Locked
     */
    public boolean isTexto217Locked() {
        return texto217Locked;
    }

    /**
     * Asigna la variable texto217Locked
     * 
     * @param texto217Locked
     * Variable a asignar en texto217Locked
     */
    public void setTexto217Locked(boolean texto217Locked) {
        this.texto217Locked = texto217Locked;
    }

    /**
     * Retorna la variable texto219Locked
     * 
     * @return texto219Locked
     */
    public boolean isTexto219Locked() {
        return texto219Locked;
    }

    /**
     * Asigna la variable texto219Locked
     * 
     * @param texto219Locked
     * Variable a asignar en texto219Locked
     */
    public void setTexto219Locked(boolean texto219Locked) {
        this.texto219Locked = texto219Locked;
    }

    /**
     * Retorna la variable interventorLocked
     * 
     * @return interventorLocked
     */
    public boolean isInterventorLocked() {
        return interventorLocked;
    }

    /**
     * Asigna la variable interventorLocked
     * 
     * @param interventorLocked
     * Variable a asignar en interventorLocked
     */
    public void setInterventorLocked(boolean interventorLocked) {
        this.interventorLocked = interventorLocked;
    }

    /**
     * Retorna la variable valorTotalLocked
     * 
     * @return valorTotalLocked
     */
    public boolean isValorTotalLocked() {
        return valorTotalLocked;
    }

    /**
     * Asigna la variable valorTotalLocked
     * 
     * @param valorTotalLocked
     * Variable a asignar en valorTotalLocked
     */
    public void setValorTotalLocked(boolean valorTotalLocked) {
        this.valorTotalLocked = valorTotalLocked;
    }

    /**
     * Retorna la variable cmdCopiarDeEnabled
     * 
     * @return cmdCopiarDeEnabled
     */
    public boolean isCmdCopiarDeEnabled() {
        return cmdCopiarDeEnabled;
    }

    /**
     * Asigna la variable cmdCopiarDeEnabled
     * 
     * @param cmdCopiarDeEnabled
     * Variable a asignar en cmdCopiarDeEnabled
     */
    public void setCmdCopiarDeEnabled(boolean cmdCopiarDeEnabled) {
        this.cmdCopiarDeEnabled = cmdCopiarDeEnabled;
    }

    /**
     * Retorna la variable importatodoEnabled
     * 
     * @return importatodoEnabled
     */
    public boolean isImportatodoEnabled() {
        return importatodoEnabled;
    }

    /**
     * Asigna la variable importatodoEnabled
     * 
     * @param importatodoEnabled
     * Variable a asignar en importatodoEnabled
     */
    public void setImportatodoEnabled(boolean importatodoEnabled) {
        this.importatodoEnabled = importatodoEnabled;
    }

    /**
     * Retorna la variable arrendamientofVisible
     * 
     * @return arrendamientofVisible
     */
    public boolean isArrendamientofVisible() {
        return arrendamientofVisible;
    }

    /**
     * Asigna la variable arrendamientofVisible
     * 
     * @param arrendamientofVisible
     * Variable a asignar en arrendamientofVisible
     */
    public void setArrendamientofVisible(boolean arrendamientofVisible) {
        this.arrendamientofVisible = arrendamientofVisible;
    }

    /**
     * Retorna la variable indiceClasificacionproponentes
     * 
     * @return indiceClasificacionproponentes
     */
    public int getIndiceClasificacionproponentes() {
        return indiceClasificacionproponentes;
    }

    /**
     * Asigna la variable indiceClasificacionproponentes
     * 
     * @param indiceClasificacionproponentes
     * Variable a asignar en indiceClasificacionproponentes
     */
    public void setIndiceClasificacionproponentes(
        int indiceClasificacionproponentes) {
        this.indiceClasificacionproponentes = indiceClasificacionproponentes;
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
     * Retorna la variable seleccionarRequisicionesEnabled
     * 
     * @return seleccionarRequisicionesEnabled
     */
    public boolean isSeleccionarRequisicionesEnabled() {
        return seleccionarRequisicionesEnabled;
    }

    /**
     * Asigna la variable seleccionarRequisicionesEnabled
     * 
     * @param seleccionarRequisicionesEnabled
     * Variable a asignar en seleccionarRequisicionesEnabled
     */
    public void setSeleccionarRequisicionesEnabled(
        boolean seleccionarRequisicionesEnabled) {
        this.seleccionarRequisicionesEnabled = seleccionarRequisicionesEnabled;
    }

    /**
     * Retorna la variable diasVisible
     * 
     * @return diasVisible
     */
    public boolean isDiasVisible() {
        return diasVisible;
    }

    /**
     * Asigna la variable diasVisible
     * 
     * @param diasVisible
     * Variable a asignar en diasVisible
     */
    public void setDiasVisible(boolean diasVisible) {
        this.diasVisible = diasVisible;
    }
    
    /**
     * Retorna la variable cargaProyContratos
     * 
     * @return cargaProyContratos
     */
    public boolean isCargaProyContratos() {
        return cargaProyContratos;
    }
    
    
    /**
     * Retorna la variable cargaCofinanciadores
     * 
     * @return cargaCofinanciadores
     */
    public boolean isCargaCofinanciadores() {
        return cargaCofinanciadores;
    }

    /**
     * Asigna la variable cargaCofinanciadores
     * 
     * @param cargaCofinanciadores
     * Variable a asignar en cargaCofinanciadores
     */
    public void setCargaCofinanciadores(boolean cargaCofinanciadores) {
        this.cargaCofinanciadores = cargaCofinanciadores;
    }
    
    /**
     * Asigna la variable cargaProyContratos
     * 
     * @param cargaProyContratos
     * Variable a asignar en cargaProyContratos
     */
    public void setCargaProyContratos(boolean cargaProyContratos) {
        this.cargaProyContratos = cargaProyContratos;
    }
    
    /**
     * Retorna la variable cargaSecop
     * 
     * @return cargaSecop
     */
    public boolean isCargaSecop() {
        return cargaSecop;
    }

    /**
     * Asigna la variable cargaSecop
     * 
     * @param cargaSecop
     * Variable a asignar en cargaSecop
     */
    public void setCargaSecop(boolean cargaSecop) {
        this.cargaSecop = cargaSecop;
    }
    
    /**
     * Retorna la variable cargaDestino
     * 
     * @return cargaDestino
     */
    public boolean isCargaDestino() {
        return cargaDestino;
    }

    /**
     * Asigna la variable cargaDestino
     * 
     * @param cargaDestino
     * Variable a asignar en cargaDestino
     */
    public void setCargaDestino(boolean cargaDestino) {
        this.cargaDestino = cargaDestino;
    }
    
    /**
     * Retorna la variable cargaDestinoOtro
     * 
     * @return cargaDestinoOtro
     */
    public boolean isCargaDestinoOtro() {
        return cargaDestinoOtro;
    }

    /**
     * Asigna la variable cargaDestinoOtro
     * 
     * @param cargaDestinoOtro
     * Variable a asignar en cargaDestinoOtro
     */
    public void setCargaDestinoOtro(boolean cargaDestinoOtro) {
        this.cargaDestinoOtro = cargaDestinoOtro;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaOrdenador
     * 
     * @return listaOrdenador
     */
    public List<Registro> getListaOrdenador() {
        return listaOrdenador;
    }

    /**
     * Asigna la lista listaOrdenador
     * 
     * @param listaOrdenador
     * Variable a asignar en listaOrdenador
     */
    public void setListaOrdenador(List<Registro> listaOrdenador) {
        this.listaOrdenador = listaOrdenador;
    }

    /**
     * Retorna la lista listaTipoAdjudicacion
     * 
     * @return listaTipoAdjudicacion
     */
    public List<Registro> getListaTipoAdjudicacion() {
        return listaTipoAdjudicacion;
    }

    /**
     * Asigna la lista listaTipoAdjudicacion
     * 
     * @param listaTipoAdjudicacion
     * Variable a asignar en listaTipoAdjudicacion
     */
    public void setListaTipoAdjudicacion(List<Registro> listaTipoAdjudicacion) {
        this.listaTipoAdjudicacion = listaTipoAdjudicacion;
    }

    /**
     * Retorna la lista listaSector
     * 
     * @return listaSector
     */
    public List<Registro> getListaSector() {
        return listaSector;
    }

    /**
     * Asigna la lista listaSector
     * 
     * @param listaSector
     * Variable a asignar en listaSector
     */
    public void setListaSector(List<Registro> listaSector) {
        this.listaSector = listaSector;
    }

    /**
     * Retorna la lista listaTIPOLOGIA
     * 
     * @return listaTIPOLOGIA
     */
    public List<Registro> getListaTIPOLOGIA() {
        return listaTIPOLOGIA;
    }

    /**
     * Asigna la lista listaTIPOLOGIA
     * 
     * @param listaTIPOLOGIA
     * Variable a asignar en listaTIPOLOGIA
     */
    public void setListaTIPOLOGIA(List<Registro> listaTIPOLOGIA) {
        this.listaTIPOLOGIA = listaTIPOLOGIA;
    }

    /**
     * Retorna la lista listaTipoContratoSECOP
     * 
     * @return listaTipoContratoSECOP
     */
    public List<Registro> getListaTipoContratoSECOP() {
        return listaTipoContratoSECOP;
    }

    /**
     * Asigna la lista listaTipoContratoSECOP
     * 
     * @param listaTipoContratoSECOP
     * Variable a asignar en listaTipoContratoSECOP
     */
    public void setListaTipoContratoSECOP(
        List<Registro> listaTipoContratoSECOP) {
        this.listaTipoContratoSECOP = listaTipoContratoSECOP;
    }

    /**
     * Retorna la lista listaDestino
     * 
     * @return listaDestino
     */
    public List<Registro> getListaDestino() {
        return listaDestino;
    }

    /**
     * Asigna la lista listaDestino
     * 
     * @param listaDestino
     * Variable a asignar en listaDestino
     */
    public void setListaDestino(List<Registro> listaDestino) {
        this.listaDestino = listaDestino;
    }

    /**
     * Retorna la lista listaCOMPONENTEBP
     * 
     * @return listaCOMPONENTEBP
     */
    public List<Registro> getListaCOMPONENTEBP() {
        return listaCOMPONENTEBP;
    }

    /**
     * Asigna la lista listaCOMPONENTEBP
     * 
     * @param listaCOMPONENTEBP
     * Variable a asignar en listaCOMPONENTEBP
     */
    public void setListaCOMPONENTEBP(List<Registro> listaCOMPONENTEBP) {
        this.listaCOMPONENTEBP = listaCOMPONENTEBP;
    }

    /**
     * Retorna la lista listaActividadSub
     * 
     * @return listaActividadSub
     */
    public List<Registro> getListaActividadSub() {
        return listaActividadSub;
    }

    /**
     * Asigna la lista listaActividadSub
     * 
     * @param listaActividadSub
     * Variable a asignar en listaActividadSub
     */
    public void setListaActividadSub(List<Registro> listaActividadSub) {
        this.listaActividadSub = listaActividadSub;
    }

    /**
     * Retorna la lista listaCgrConcepto
     * 
     * @return listaCgrConcepto
     */
    public RegistroDataModelImpl getListaCgrConcepto() {
        return listaCgrConcepto;
    }

    /**
     * Asigna la lista listaCgrConcepto
     * 
     * @param listaCgrConcepto
     * Variable a asignar en listaCgrConcepto
     */
    public void setListaCgrConcepto(RegistroDataModelImpl listaCgrConcepto) {
        this.listaCgrConcepto = listaCgrConcepto;
    }

    /**
     * Retorna la lista listaCgrTipoGasto
     * 
     * @return listaCgrTipoGasto
     */
    public RegistroDataModelImpl getListaCgrTipoGasto() {
        return listaCgrTipoGasto;
    }

    /**
     * Asigna la lista listaCgrTipoGasto
     * 
     * @param listaCgrTipoGasto
     * Variable a asignar en listaCgrTipoGasto
     */
    public void setListaCgrTipoGasto(RegistroDataModelImpl listaCgrTipoGasto) {
        this.listaCgrTipoGasto = listaCgrTipoGasto;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna la lista listaGrupoSub
     * 
     * @return listaGrupoSub
     */
    public RegistroDataModelImpl getListaGrupoSub() {
        return listaGrupoSub;
    }

    /**
     * Asigna la lista listaGrupoSub
     * 
     * @param listaGrupoSub
     * Variable a asignar en listaGrupoSub
     */
    public void setListaGrupoSub(RegistroDataModelImpl listaGrupoSub) {
        this.listaGrupoSub = listaGrupoSub;
    }

    /**
     * Retorna la lista listaGrupoSub
     * 
     * @return listaGrupoSub
     */
    public RegistroDataModelImpl getListaGrupoSubE() {
        return listaGrupoSubE;
    }

    /**
     * Asigna la lista listaGrupoSub
     * 
     * @param listaGrupoSub
     * Variable a asignar en listaGrupoSub
     */
    public void setListaGrupoSubE(RegistroDataModelImpl listaGrupoSubE) {
        this.listaGrupoSubE = listaGrupoSubE;
    }

    /**
     * Retorna la lista listaEspecialidadSub
     * 
     * @return listaEspecialidadSub
     */
    public RegistroDataModelImpl getListaEspecialidadSub() {
        return listaEspecialidadSub;
    }

    /**
     * Asigna la lista listaEspecialidadSub
     * 
     * @param listaEspecialidadSub
     * Variable a asignar en listaEspecialidadSub
     */
    public void setListaEspecialidadSub(
        RegistroDataModelImpl listaEspecialidadSub) {
        this.listaEspecialidadSub = listaEspecialidadSub;
    }

    /**
     * Retorna la lista listaEspecialidadSub
     * 
     * @return listaEspecialidadSub
     */
    public RegistroDataModelImpl getListaEspecialidadSubE() {
        return listaEspecialidadSubE;
    }

    /**
     * Asigna la lista listaEspecialidadSub
     * 
     * @param listaEspecialidadSub
     * Variable a asignar en listaEspecialidadSub
     */
    public void setListaEspecialidadSubE(
        RegistroDataModelImpl listaEspecialidadSubE) {
        this.listaEspecialidadSubE = listaEspecialidadSubE;
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
     * Retorna la lista listaNumconvenio
     * 
     * @return listaNumconvenio
     */
    public RegistroDataModelImpl getListaNumconvenio() {
        return listaNumconvenio;
    }

    /**
     * Asigna la lista listaNumconvenio
     * 
     * @param listaNumconvenio
     * Variable a asignar en listaNumconvenio
     */
    public void setListaNumconvenio(RegistroDataModelImpl listanumconvenio) {
        this.listaNumconvenio = listanumconvenio;
    }

    /**
     * Retorna la lista listaCopiarDe
     * 
     * @return listaCopiarDe
     */
    public RegistroDataModelImpl getListaCopiarDe() {
        return listaCopiarDe;
    }

    /**
     * Asigna la lista listaCopiarDe
     * 
     * @param listaCopiarDe
     * Variable a asignar en listaCopiarDe
     */
    public void setListaCopiarDe(RegistroDataModelImpl listaCopiarDe) {
        this.listaCopiarDe = listaCopiarDe;
    }

    /**
     * Retorna la lista listaCEDULAINTERVENTOR
     * 
     * @return listaCEDULAINTERVENTOR
     */
    public RegistroDataModelImpl getListaCEDULAINTERVENTOR() {
        return listaCEDULAINTERVENTOR;
    }

    /**
     * Asigna la lista listaCEDULAINTERVENTOR
     * 
     * @param listaCEDULAINTERVENTOR
     * Variable a asignar en listaCEDULAINTERVENTOR
     */
    public void setListaCEDULAINTERVENTOR(
        RegistroDataModelImpl listaCEDULAINTERVENTOR) {
        this.listaCEDULAINTERVENTOR = listaCEDULAINTERVENTOR;
    }

    /**
     * Retorna la lista listaListaModelos
     * 
     * @return listaListaModelos
     */
    public RegistroDataModelImpl getListaListaModelos() {
        return listaListaModelos;
    }

    /**
     * Asigna la lista listaListaModelos
     * 
     * @param listaListaModelos
     * Variable a asignar en listaListaModelos
     */
    public void setListaListaModelos(RegistroDataModelImpl listaListaModelos) {
        this.listaListaModelos = listaListaModelos;
    }

    /**
     * Retorna la lista listaClaseDisponibilidad
     * 
     * @return listaClaseDisponibilidad
     */
    public RegistroDataModelImpl getListaClaseDisponibilidad() {
        return listaClaseDisponibilidad;
    }

    /**
     * Asigna la lista listaClaseDisponibilidad
     * 
     * @param listaClaseDisponibilidad
     * Variable a asignar en listaClaseDisponibilidad
     */
    public void setListaClaseDisponibilidad(
        RegistroDataModelImpl listaClaseDisponibilidad) {
        this.listaClaseDisponibilidad = listaClaseDisponibilidad;
    }

    /**
     * Retorna la lista listaAuxiliar
     * 
     * @return listaAuxiliar
     */
    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    /**
     * Asigna la lista listaAuxiliar
     * 
     * @param listaAuxiliar
     * Variable a asignar en listaAuxiliar
     */
    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    /**
     * Retorna la lista listaModelos
     * 
     * @return listaModelos
     */
    public RegistroDataModelImpl getListaModelos() {
        return listaModelos;
    }

    /**
     * Asigna la lista listaModelos
     * 
     * @param listaModelos
     * Variable a asignar en listaModelos
     */
    public void setListaModelos(RegistroDataModelImpl listaModelos) {
        this.listaModelos = listaModelos;
    }

    /**
     * Retorna la lista listalistaRubros
     * 
     * @return listalistaRubros
     */
    public RegistroDataModelImpl getListalistaRubros() {
        return listalistaRubros;
    }

    /**
     * Asigna la lista listalistaRubros
     * 
     * @param listalistaRubros
     * Variable a asignar en listalistaRubros
     */
    public void setListalistaRubros(RegistroDataModelImpl listalistaRubros) {
        this.listalistaRubros = listalistaRubros;
    }

    /**
     * Retorna la lista listaCgrSegmento
     * 
     * @return listaCgrSegmento
     */
    public RegistroDataModelImpl getListaCgrSegmento() {
        return listaCgrSegmento;
    }

    /**
     * Asigna la lista listaCgrSegmento
     * 
     * @param listaCgrSegmento
     * Variable a asignar en listaCgrSegmento
     */
    public void setListaCgrSegmento(RegistroDataModelImpl listaCgrSegmento) {
        this.listaCgrSegmento = listaCgrSegmento;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    /**
     * Retorna la lista listaClasificacionproponentes
     * 
     * @return listaClasificacionproponentes
     */
    public List<Registro> getListaClasificacionproponentes() {
        return listaClasificacionproponentes;
    }

    /**
     * Asigna la lista listaClasificacionproponentes
     * 
     * @param listaClasificacionproponentes
     * Variable a asignar en listaClasificacionproponentes
     */
    public void setListaClasificacionproponentes(
        List<Registro> listaClasificacionproponentes) {
        this.listaClasificacionproponentes = listaClasificacionproponentes;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     * 
     * @return registroSub
     */
    public Registro getRegistroSub() {
        return registroSub;
    }

    /**
     * Asigna el objeto registroSub
     * 
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    /**
     * @return the botonCambiar
     */
    public boolean isBotonCambiar() {
        return botonCambiar;
    }

    /**
     * @param botonCambiar
     * the botonCambiar to set
     */
    public void setBotonCambiar(boolean botonCambiar) {
        this.botonCambiar = botonCambiar;
    }

    public Registro getListaValorTotal() {
        return listaValorTotal;
    }

    public void setListaValorTotal(Registro listaValorTotal) {
        this.listaValorTotal = listaValorTotal;
    }

    public Registro getListaModificacionTiempo() {
        return listaModificacionTiempo;
    }

    public void setListaModificacionTiempo(Registro listaModificacionTiempo) {
        this.listaModificacionTiempo = listaModificacionTiempo;
    }

    public boolean isEnviarEmailVisible() {
        return enviarEmailVisible;
    }

    public void setEnviarEmailVisible(boolean enviarEmailVisible) {
        this.enviarEmailVisible = enviarEmailVisible;
    }

	/**
	 * @return the listaFuenteRecurso
	 */
	public RegistroDataModelImpl getListaFuenteRecurso() {
		return listaFuenteRecurso;
	}

	/**
	 * @param listaFuenteRecurso the listaFuenteRecurso to set
	 */
	public void setListaFuenteRecurso(RegistroDataModelImpl listaFuenteRecurso) {
		this.listaFuenteRecurso = listaFuenteRecurso;
	}
	
	/**
     * Retorna la lista listaCedAbog
     * 
     * @return listaCedAbog
     */
    public RegistroDataModelImpl getListaCedAbog() {
        return listaCedAbog;
    }
    
    /**
     * Asigna la lista listaCedAbog
     * 
     * @param listaCedAbog
     * Variable a asignar en  listaCedAbog
     */
    public void setListaCedAbog(RegistroDataModelImpl listaCedAbog) {
        this.listaCedAbog = listaCedAbog;
    }

	/**
	 * @return the mostrarFuente
	 */
	public boolean isMostrarFuente() {
		return mostrarFuente;
	}

	/**
	 * @param mostrarFuente the mostrarFuente to set
	 */
	public void setMostrarFuente(boolean mostrarFuente) {
		this.mostrarFuente = mostrarFuente;
	}
    
	/**
	 * @return the mostrarReintegro
	 */
	public boolean isMostrarReintegro() {
		return mostrarReintegro;
	}

	/**
	 * @param mostrarReintegro the mostrarReintegro to set
	 */
	public void setMostrarReintegro(boolean mostrarReintegro) {
		this.mostrarReintegro = mostrarReintegro;
	}

	/**
	 * @return the manejaReportesAppui
	 */
	public boolean isManejaReportesAppui() {
		return manejaReportesAppui;
	}

	/**
	 * @param manejaReportesAppui the manejaReportesAppui to set
	 */
	public void setManejaReportesAppui(boolean manejaReportesAppui) {
		this.manejaReportesAppui = manejaReportesAppui;
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
	 * @return the obligaCampos12
	 */
	public String getObligaCampos12() {
		return obligaCampos12;
	}

	/**
	 * @param obligaCampos12 the obligaCampos12 to set
	 */
	public void setObligaCampos12(String obligaCampos12) {
		this.obligaCampos12 = obligaCampos12;
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

	/**
	 * @param obligaCampos14 the obligaCampos14 to set
	 */
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

	/**
	 * @param obligaCampos17 the obligaCampos17 to set
	 */
	public void setObligaCampos17(String obligaCampos17) {
		this.obligaCampos17 = obligaCampos17;
	}

	/**
	 * @return the obligaCampos18
	 */
	public String getObligaCampos18() {
		return obligaCampos18;
	}

	/**
	 * @param obligaCampos18 the obligaCampos18 to set
	 */
	public void setObligaCampos18(String obligaCampos18) {
		this.obligaCampos18 = obligaCampos18;
	}

	/**
	 * @return the obligaCampos19
	 */
	public String getObligaCampos19() {
		return obligaCampos19;
	}

	/**
	 * @param obligaCampos19 the obligaCampos19 to set
	 */
	public void setObligaCampos19(String obligaCampos19) {
		this.obligaCampos19 = obligaCampos19;
	}

	/**
	 * @return the obligaCampos20
	 */
	public String getObligaCampos20() {
		return obligaCampos20;
	}

	/**
	 * @param obligaCampos20 the obligaCampos20 to set
	 */
	public void setObligaCampos20(String obligaCampos20) {
		this.obligaCampos20 = obligaCampos20;
	}

	/**
	 * @return the obligaCampos21
	 */
	public String getObligaCampos21() {
		return obligaCampos21;
	}

	/**
	 * @param obligaCampos21 the obligaCampos21 to set
	 */
	public void setObligaCampos21(String obligaCampos21) {
		this.obligaCampos21 = obligaCampos21;
	}

	/**
	 * @return the obligaCampos22
	 */
	public String getObligaCampos22() {
		return obligaCampos22;
	}

	/**
	 * @param obligaCampos22 the obligaCampos22 to set
	 */
	public void setObligaCampos22(String obligaCampos22) {
		this.obligaCampos22 = obligaCampos22;
	}

	/**
	 * @return the obligaCampos23
	 */
	public String getObligaCampos23() {
		return obligaCampos23;
	}

	/**
	 * @param obligaCampos23 the obligaCampos23 to set
	 */
	public void setObligaCampos23(String obligaCampos23) {
		this.obligaCampos23 = obligaCampos23;
	}
	
	
	

	/**
	 * @return the obligaCampos24
	 */
	public String getObligaCampos24() {
		return obligaCampos24;
	}

	/**
	 * @param obligaCampos24 the obligaCampos24 to set
	 */
	public void setObligaCampos24(String obligaCampos24) {
		this.obligaCampos24 = obligaCampos24;
	}

	/**
	 * @return the obligaCampos25
	 */
	public String getObligaCampos25() {
		return obligaCampos25;
	}

	/**
	 * @param obligaCampos25 the obligaCampos25 to set
	 */
	public void setObligaCampos25(String obligaCampos25) {
		this.obligaCampos25 = obligaCampos25;
	}

	/**
	 * @return the obligaCampos26
	 */
	public String getObligaCampos26() {
		return obligaCampos26;
	}

	/**
	 * @param obligaCampos26 the obligaCampos26 to set
	 */
	public void setObligaCampos26(String obligaCampos26) {
		this.obligaCampos26 = obligaCampos26;
	}

	public String getObligaCampos27() {
		return obligaCampos27;
	}

	public void setObligaCampos27(String obligaCampos27) {
		this.obligaCampos27 = obligaCampos27;
	}

	public String getObligaCampos28() {
		return obligaCampos28;
	}

	public void setObligaCampos28(String obligaCampos28) {
		this.obligaCampos28 = obligaCampos28;
	}

	public String getObligaCampos29() {
		return obligaCampos29;
	}

	public void setObligaCampos29(String obligaCampos29) {
		this.obligaCampos29 = obligaCampos29;
	}

	public boolean isSigecVisible() {
		return sigecVisible;
	}
	public void setSigecVisible(boolean sigecVisible) {
		this.sigecVisible = sigecVisible;
	}

	/**
	 * @return the obligaCampoSigec
	 */
	public boolean isObligaCampoSigec() {
		return obligaCampoSigec;
	}

	/**
	 * @param obligaCampoSigec the obligaCampoSigec to set
	 */
	public void setObligaCampoSigec(boolean obligaCampoSigec) {
		this.obligaCampoSigec = obligaCampoSigec;
	}

	/**
	 * @return the visibleSujeto
	 */
	public boolean isVisibleSujeto() {
		return visibleSujeto;
	}

	/**
	 * @param visibleSujeto the visibleSujeto to set
	 */
	public void setVisibleSujeto(boolean visibleSujeto) {
		this.visibleSujeto = visibleSujeto;
	}

	/**
	 * @return the habilitaActasInicio
	 */
	public boolean isHabilitaActasInicio() {
		return habilitaActasInicio;
	}

	/**
	 * @param habilitaActasInicio the habilitaActasInicio to set
	 */
	public void setHabilitaActasInicio(boolean habilitaActasInicio) {
		this.habilitaActasInicio = habilitaActasInicio;
	}
	
	/**
	 * @return the vigenciaContratoPorFirma
	 */
	public boolean isVigenciaContratoPorFirma() {
		return vigenciaContratoPorFirma;
	}

	/**
	 * @param vigenciaContratoPorFirma the vigenciaContratoPorFirma to set
	 */
	public void setVigenciaContratoPorFirma(boolean vigenciaContratoPorFirma) {
		this.vigenciaContratoPorFirma = vigenciaContratoPorFirma;
	}
	
	public boolean isVerCerrar() {
		return verCerrar;
	}

	public void setVerCerrar(boolean verCerrar) {
		this.verCerrar = verCerrar;
	}
	
	/**
	 * @return the listaPais
	 */
	public List<Registro> getListaPais() {
		return listaPais;
	}

	/**
	 * @param listaPais the listaPais to set
	 */
	public void setListaPais(List<Registro> listaPais) {
		this.listaPais = listaPais;
	}
	

	/**
	 * @return the listaDepartamento
	 */
	public List<Registro> getListaDepartamento() {
		return listaDepartamento;
	}

	/**
	 * @param listaDepartamento the listaDepartamento to set
	 */
	public void setListaDepartamento(List<Registro> listaDepartamento) {
		this.listaDepartamento = listaDepartamento;
	}
	/**
	 * @return the listaCiudad
	 */
	public List<Registro> getListaCiudad() {
		return listaCiudad;
	}

	/**
	 * @param listaCiudad the listaCiudad to set
	 */
	public void setListaCiudad(List<Registro> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

	/**
	 * @return the listaEstadoCivil
	 */
	public List<Registro> getListaEstadoCivil() {
		return listaEstadoCivil;
	}
	

	/**
	 * @param listaEstadoCivil the listaEstadoCivil to set
	 */
	public void setListaEstadoCivil(List<Registro> listaEstadoCivil) {
		this.listaEstadoCivil = listaEstadoCivil;
	}

	/**
	 * @return the listaNivelEmpleo
	 */
	public List<Registro> getListaNivelEmpleo() {
		return listaNivelEmpleo;
	}

	/**
	 * @param listaNivelEmpleo the listaNivelEmpleo to set
	 */
	public void setListaNivelEmpleo(List<Registro> listaNivelEmpleo) {
		this.listaNivelEmpleo = listaNivelEmpleo;
	}

	/**
	 * @return the listaNivelEducativo
	 */
	public RegistroDataModelImpl getListaNivelEducativo() {
		return listaNivelEducativo;
	}

	/**
	 * @param listaNivelEducativo the listaNivelEducativo to set
	 */
	public void setListaNivelEducativo(RegistroDataModelImpl listaNivelEducativo) {
		this.listaNivelEducativo = listaNivelEducativo;
	}

	/**
	 * @return the listaDepartamentoTrabajo
	 */
	public List<Registro> getListaDepartamentoTrabajo() {
		return listaDepartamentoTrabajo;
	}

	/**
	 * @param listaDepartamentoTrabajo the listaDepartamentoTrabajo to set
	 */
	public void setListaDepartamentoTrabajo(List<Registro> listaDepartamentoTrabajo) {
		this.listaDepartamentoTrabajo = listaDepartamentoTrabajo;
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
	 * @return the informacionFuncionalEnabled
	 */
	public boolean isInformacionFuncionalEnabled() {
		return informacionFuncionalEnabled;
	}

	/**
	 * @param informacionFuncionalEnabled the informacionFuncionalEnabled to set
	 */
	public void setInformacionFuncionalEnabled(boolean informacionFuncionalEnabled) {
		this.informacionFuncionalEnabled = informacionFuncionalEnabled;
	}


	/**
	 * @return the esDiscapacitado
	 */
	public boolean isEsDiscapacitado() {
		return esDiscapacitado;
	}

	/**
	 * @param esDiscapacitado the esDiscapacitado to set
	 */
	public void setEsDiscapacitado(boolean esDiscapacitado) {
		this.esDiscapacitado = esDiscapacitado;
	}
	
	 //CC_1499 MROSERO 
	public boolean isObligaCamposCGR() {
		return obligaCamposCGR;
	}

	public void setObligaCamposCGR(boolean obligaCamposCGR) {
		this.obligaCamposCGR = obligaCamposCGR;
	}
	
    // </SET_GET_ADICIONALES>
	
	/**
	 * @return the codProfesion
	 */
	public String getCodProfesion() {
		return codProfesion;
	}

	/**
	 * @param codProfesion the codProfesion to set
	 */
	public void setCodProfesion(String codProfesion) {
		this.codProfesion = codProfesion;
	}

	/**
	 * @return the codPais
	 */
	public String getCodPais() {
		return codPais;
	}

	/**
	 * @param codPais the codPais to set
	 */
	public void setCodPais(String codPais) {
		this.codPais = codPais;
	}

	/**
	 * @return the codDepartamento
	 */
	public String getCodDepartamento() {
		return codDepartamento;
	}

	/**
	 * @param codDepartamento the codDepartamento to set
	 */
	public void setCodDepartamento(String codDepartamento) {
		this.codDepartamento = codDepartamento;
	}

	/**
	 * @return the codMunicipio
	 */
	public String getCodMunicipio() {
		return codMunicipio;
	}

	/**
	 * @param codMunicipio the codMunicipio to set
	 */
	public void setCodMunicipio(String codMunicipio) {
		this.codMunicipio = codMunicipio;
	}

	/**
	 * @return the nombrePais
	 */
	public String getNombrePais() {
		return nombrePais;
	}

	/**
	 * @param nombrePais the nombrePais to set
	 */
	public void setNombrePais(String nombrePais) {
		this.nombrePais = nombrePais;
	}

	
	/**
	 * @return the nombreDepartamento
	 */
	public String getNombreDepartamento() {
		return nombreDepartamento;
	}

	/**
	 * @param nombreDepartamento the nombreDepartamento to set
	 */
	public void setNombreDepartamento(String nombreDepartamento) {
		this.nombreDepartamento = nombreDepartamento;
	}
	
	/**
	 * @return the nombreMunicipio
	 */
	public String getNombreCiudad() {
		return nombreMunicipio;
	}

	/**
	 * @param nombreMunicipio the nombreMunicipio to set
	 */
	public void setNombreCiudad(String nombreMunicipio) {
		this.nombreMunicipio = nombreMunicipio;
	}

	/**
	 * @return the nombreEstadoCivil
	 */
	public String getNombreEstadoCivil() {
		return nombreEstadoCivil;
	}

	/**
	 * @param nombreEstadoCivil the nombreEstadoCivil to set
	 */
	public void setNombreEstadoCivil(String nombreEstadoCivil) {
		this.nombreEstadoCivil = nombreEstadoCivil;
	}
	
	/**
	 * @return the numeroHijos
	 */
	public String getNumeroHijos() {
		return numeroHijos;
	}

	/**
	 * @param numeroHijos the numeroHijos to set
	 */
	public void setNumeroHijos(String numeroHijos) {
		this.numeroHijos = numeroHijos;
	}

	/**
	 * @return the nombreServPublico
	 */
	public String getNombreServPublico() {
		return nombreServPublico;
	}

	/**
	 * @param nombreServPublico the nombreServPublico to set
	 */
	public void setNombreServPublico(String nombreServPublico) {
		this.nombreServPublico = nombreServPublico;
	}

	/**
	 * @return the nombreNivelEmpleo
	 */
	public String getNombreNivelEmpleo() {
		return nombreNivelEmpleo;
	}

	/**
	 * @param nombreNivelEmpleo the nombreNivelEmpleo to set
	 */
	public void setNombreNivelEmpleo(String nombreNivelEmpleo) {
		this.nombreNivelEmpleo = nombreNivelEmpleo;
	}

	/**
	 * @return the nombreNivelEducativo
	 */
	public String getNombreNivelEducativo() {
		return nombreNivelEducativo;
	}

	/**
	 * @param nombreNivelEducativo the nombreNivelEducativo to set
	 */
	public void setNombreNivelEducativo(String nombreNivelEducativo) {
		this.nombreNivelEducativo = nombreNivelEducativo;
	}

	/**
	 * @return the nombreProfesion
	 */
	public String getNombreProfesion() {
		return nombreProfesion;
	}

	/**
	 * @param nombreProfesion the nombreProfesion to set
	 */
	public void setNombreProfesion(String nombreProfesion) {
		this.nombreProfesion = nombreProfesion;
	}

	/**
	 * @return the nombreDepTrabajo
	 */
	public String getNombreDepTrabajo() {
		return nombreDepTrabajo;
	}

	/**
	 * @param nombreDepTrabajo the nombreDepTrabajo to set
	 */
	public void setNombreDepTrabajo(String nombreDepTrabajo) {
		this.nombreDepTrabajo = nombreDepTrabajo;
	}
	
	/**
	 * @return the nombreDiscapacidad
	 */
	public String getNombreDiscapacidad() {
		return nombreDiscapacidad;
	}

	/**
	 * @param nombreDiscapacidad the nombreDiscapacidad to set
	 */
	public void setNombreDiscapacidad(String nombreDiscapacidad) {
		this.nombreDiscapacidad = nombreDiscapacidad;
	}

	/**
	 * @return the esBeneficiario
	 */
	public boolean isEsBeneficiario() {
		return esBeneficiario;
	}

	/**
	 * @param esBeneficiario the esBeneficiario to set
	 */
	public void setEsBeneficiario(boolean esBeneficiario) {
		this.esBeneficiario = esBeneficiario;
	}

	/**
	 * @return the codEstadoCivil
	 */
	public String getCodEstadoCivil() {
		return codEstadoCivil;
	}

	/**
	 * @param codEstadoCivil the codEstadoCivil to set
	 */
	public void setCodEstadoCivil(String codEstadoCivil) {
		this.codEstadoCivil = codEstadoCivil;
	}

	/**
	 * @return the codServPublico
	 */
	public String getCodServPublico() {
		return codServPublico;
	}

	/**
	 * @param codServPublico the codServPublico to set
	 */
	public void setCodServPublico(String codServPublico) {
		this.codServPublico = codServPublico;
	}

	/**
	 * @return the codNivelEmpleo
	 */
	public String getCodNivelEmpleo() {
		return codNivelEmpleo;
	}

	/**
	 * @param codNivelEmpleo the codNivelEmpleo to set
	 */
	public void setCodNivelEmpleo(String codNivelEmpleo) {
		this.codNivelEmpleo = codNivelEmpleo;
	}

	/**
	 * @return the codNivelEducativo
	 */
	public String getCodNivelEducativo() {
		return codNivelEducativo;
	}

	/**
	 * @param codNivelEducativo the codNivelEducativo to set
	 */
	public void setCodNivelEducativo(String codNivelEducativo) {
		this.codNivelEducativo = codNivelEducativo;
	}

	/**
	 * @return the codDepTrabajo
	 */
	public String getCodDepTrabajo() {
		return codDepTrabajo;
	}

	/**
	 * @param codDepTrabajo the codDepTrabajo to set
	 */
	public void setCodDepTrabajo(String codDepTrabajo) {
		this.codDepTrabajo = codDepTrabajo;
	}

	/**
	 * @return the codDiscapacidad
	 */
	public String getCodDiscapacidad() {
		return codDiscapacidad;
	}

	/**
	 * @param codDiscapacidad the codDiscapacidad to set
	 */
	public void setCodDiscapacidad(String codDiscapacidad) {
		this.codDiscapacidad = codDiscapacidad;
	}

	/**
	 * @return the salario
	 */
	public double getSalario() {
		return salario;
	}

	/**
	 * @param salario the salario to set
	 */
	public void setSalario(double salario) {
		this.salario = salario;
	}

	/**
	 * @return the nombreMunicipio
	 */
	public String getNombreMunicipio() {
		return nombreMunicipio;
	}

	/**
	 * @param nombreMunicipio the nombreMunicipio to set
	 */
	public void setNombreMunicipio(String nombreMunicipio) {
		this.nombreMunicipio = nombreMunicipio;
	}

	/**
	 * @return the valorNumeroHijos
	 */
	public String getValorNumeroHijos() {
		return valorNumeroHijos;
	}

	/**
	 * @param valorNumeroHijos the valorNumeroHijos to set
	 */
	public void setValorNumeroHijos(String valorNumeroHijos) {
		this.valorNumeroHijos = valorNumeroHijos;
	}

	/**
	 * @return the codDependencia
	 */
	public String getCodDependencia() {
		return codDependencia;
	}

	/**
	 * @param codDependencia the codDependencia to set
	 */
	public void setCodDependencia(String codDependencia) {
		this.codDependencia = codDependencia;
	}

	/**
	 * @return the nombreDependencia
	 */
	public String getNombreDependencia() {
		return nombreDependencia;
	}

	/**
	 * @param nombreDependencia the nombreDependencia to set
	 */
	public void setNombreDependencia(String nombreDependencia) {
		this.nombreDependencia = nombreDependencia;
	}

	/**
	 * @return the vlrAIU
	 */
	public double getVlrAIU() {
		return vlrAIU;
	}

	/**
	 * @param vlrAIU the vlrAIU to set
	 */
	public void setVlrAIU(double vlrAIU) {
		this.vlrAIU = vlrAIU;
	}

	/**
	 * @return the vlrImpConsumo
	 */
	public double getVlrImpConsumo() {
		return vlrImpConsumo;
	}

	/**
	 * @param vlrImpConsumo the vlrImpConsumo to set
	 */
	public void setVlrImpConsumo(double vlrImpConsumo) {
		this.vlrImpConsumo = vlrImpConsumo;
	}

	/**
	 * @return the topTotal
	 */
	public String getTopTotal() {
		return topTotal;
	}

	/**
	 * @param topTotal the topTotal to set
	 */
	public void setTopTotal(String topTotal) {
		this.topTotal = topTotal;
	}
}