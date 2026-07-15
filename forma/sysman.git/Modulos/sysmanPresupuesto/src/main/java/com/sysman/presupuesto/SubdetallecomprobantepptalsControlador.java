package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.ObligaPedirDatosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoDosRemote;
import com.sysman.presupuesto.ejb.EjbPresupuestoTresRemote;
import com.sysman.presupuesto.enums.SubdetallecomprobantepptalsControladorEnum;
import com.sysman.presupuesto.enums.SubdetallecomprobantepptalsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Arrays;
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

/**
 *
 * @author dmaldonado
 * @version 5, 25/07/2016 17:03:36
 *
 * @author jrodrigueza
 * @version 6, 27/04/2017 12:34 Proceso de Refactoring.
 */
@ManagedBean
@ViewScoped
public class SubdetallecomprobantepptalsControlador
extends BeanBaseContinuoAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la
	 * cual inicio sesion el usuario, el valor de esta constante es asignado en
	 * el constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String fechaCons;
	private final String numeroComprobanteCons;
	private final String nombreComprobanteCons;
	private final String claseComprobanteCons;
	private final String tipoComprobanteCons;
	private final String tipoComprobanteAfecCons;
	private final String cuentaCons;
	private final String comprobanteCons;
	private final String centroCostoCons;
	private final String codigoCons;
	private final String auxiliarCons;
	private final String referenciaCons;
	private final String fuenteRecursoCons;
	private final String terceroCons;
	private final String sucursalCons;
	private final String valorDebitoCons;
	private final String valorCreditoCons;
	private final String consecutivoCons;
	private final String naturalezaCons;
	private final String modificacionDebitoCons;
	private final String modificacionCreditoCons;
	private final String debitoAfectadoCons;
	private final String creditoAfectadoCons;
	private final String numeroContratoCons;
	private final String cmptAfectadoCons;
	private final String aprDefinitivaCons;
	private final String aplazamientoTotal;
	private  String naturalezaCuenta =  "";

	// <DECLARAR_ATRIBUTOS>
	private String tituloEncabezado;
	private String descripcionEncabezado;
	private String sumaDebitos;
	private String sumaCreditos;
	private String sumaDiferencia;
	private String validarAccion;
	private double valorPresupuesto;
	private Double valorDebito;
	private boolean visibleAfectaciones;
	private String cuentaRegalias = "";
	private String cuentaAux = "";
	private String claseAux = ""; //de cualclasificador depende depende



	

	/**
	 * Titulo de la columna Papeles Contractual
	 */
	private String tituloPC;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private RegistroDataModelImpl listaReferencia;
	private RegistroDataModelImpl listaReferenciaE;
	private List<Registro> listaTipoCpteAfect;
	private List<Registro> listaTipoClasificador;
	private List<Registro> listaTipoCuentaRegalias;
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTercero;
	private RegistroDataModelImpl listaTerceroE;
	private RegistroDataModelImpl listaTipoDoc;
	private RegistroDataModelImpl listaTipoDocE;
	private RegistroDataModelImpl listaFuente;
	private RegistroDataModelImpl listaFuenteE;
	private RegistroDataModelImpl listaAuxiliar;
	private RegistroDataModelImpl listaAuxiliarE;
	private RegistroDataModelImpl listarecursoSGR;
	private RegistroDataModelImpl listarecursoSGRE;
	/**
	 * Lista que carga las cuentas debitos equivalentes al rubro seleccionado
	 */
	private RegistroDataModelImpl listaCuentaDebitoEquivalente;
	/**
	 * Lista que carga las cuentas debitos equivalentes de la grilla al rubro
	 * seleccionado
	 */
	private RegistroDataModelImpl listaCuentaDebitoEquivalenteE;
	/**
	 * Lista que carga las cuentas creditos equivalentes al rubro seleccionado
	 */
	private RegistroDataModelImpl listaCuentaCreditoEquivalente;
	/**
	 * Lista que carga las cuentas creditos equivalentes de la grilla al rubro
	 * seleccionado
	 */
	private RegistroDataModelImpl listaCuentaCreditoEquivalenteE;

	private RegistroDataModelImpl listaCodigoCPC;

	private RegistroDataModelImpl listaCodigoCPCE;

	private RegistroDataModelImpl listaFuenteCUIPO;

	private RegistroDataModelImpl listaFuenteCUIPOE;

	private RegistroDataModelImpl listaProductoCUIPO;

	private RegistroDataModelImpl listaProductoCUIPOE;

	private RegistroDataModelImpl listaCodigoCuenta;
	private RegistroDataModelImpl listaCodigoCuentaE;
	private RegistroDataModelImpl listaCmpteAfectado;
	private RegistroDataModelImpl listaCmpteAfectadoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSector;
	private RegistroDataModelImpl listaSectorE;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPrograma;
	private RegistroDataModelImpl listaProgramaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSupPrograma;
	private RegistroDataModelImpl listaSupProgramaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoProducto;
	private RegistroDataModelImpl listaCodigoProductoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoBPIN;
	private RegistroDataModelImpl listaCodigoBPINE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPET;
	private RegistroDataModelImpl listaCodigo_CCPET;

	private RegistroDataModelImpl listaCodigoCCPETE;
	private RegistroDataModelImpl listaCodigo_CCPETE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCPCDANE;
	private RegistroDataModelImpl listaCodigoCPCDANEE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoUnidEje;
	private RegistroDataModelImpl listaCodigoUnidEjeE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoFuente;
	private RegistroDataModelImpl listaCodigoFuenteE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPETRega;
	private RegistroDataModelImpl listaCodigoCCPETRegaE;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaPoliticaPublica;
	private RegistroDataModelImpl listaPoliticaPublicaE;


	/**
	 * 
	 */
	private RegistroDataModelImpl listaDetalleSectorial;
	private RegistroDataModelImpl listaDetalleSectorialE;


	private String auxiliar;
	private String auxiliarCodigo =  "";
	// </DECLARAR_LISTAS_COMBO_GRANDE>



	private String ano;
	private String mes;
	private String tipoComprobante;
	private String claseComprobante;
	private String nombreComprobante;
	private String numeroComprobante;
	private String tipoDeCuentas;
	private int indice;
	private int modelo;
	private int modeloN;
	private String claseAfectar;
	private Map<String, Object> parametrosEntrada;

	private boolean bloqValorDebito;
	private boolean bloqValorCredito;
	private boolean bloqContCredito;
	private boolean bloqContCreditoCont;
	private boolean bloqFecha;
	private String columna;
	private String modulo;
	private boolean impreso;
	private String estadoPeriodo;
	private boolean mostrarAcme;
	private boolean visiblePapeles;
	private boolean visibleConSituacionFondos;
	private boolean bloqConSituacionFondos;
	private String papeles;
	private double debitoComprobante;
	private double creditoComprobante;
	private String nombreTerceroC;
	private String sucursalC;
	private String tipoContrato;
	private String numeroContrato;
	private Registro registroIniFila;
	private Date fechaComprobante;
	private Object manejaAuxiliar;
	private Object manejaTercero;
	private Object manejaCentroCosto;
	private String terceroComprobante;
	private String sucursalComprobante;
	private String nomTerComprobante;
	private boolean papelesComprobante;
	private boolean contractualComprobante;
	private boolean bloqValorDebitoCont;
	private boolean bloqValorCreditoCont;
	private boolean bloqPoliticaPublica;
	private boolean bloqDetalleSectorial;
	private boolean bloqSector;
	private boolean bloqPrograma;
	private boolean bloqSupPrograma;
	private boolean bloqCodigoProducto;
	private boolean bloqCodigoBPIN;
	private boolean bloqCodigoCCPET;
	private boolean bloqCodigoCPCDANE;
	private boolean bloqCodigoUnidEje;
	private boolean bloqCodigoFuente;
	private boolean bloqCodigoCCPETRega;
	private boolean terceroVariosAfectar;
	private String descripcionComprobante;
	private Object manejaReferencia;
	private Object manejaFuente;
	private String tipoDocComprobante;
	private String nroDocComprobante;
	private String afectacion;
	private boolean ingreso;
	private Map ridContable;
	private Map ridPptal;
	private Double valorFinanciera;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sector;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String programa;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String supPrograma;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoProducto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoBPIN;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoCCPET;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoSGR;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoCPCDANE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoFuente;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoUnidEje;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoCCPETRega;

	/**
	 * Atributo que sirve para validar la visibilidad de los campos Cuenta
	 * Debito Equivalente , Cuenta Credito Equivalente y el boton Contabilizar
	 * 
	 */
	private String verContabilizar;

	/**
	 * Atibuto que indica si el tipo de comprobante con el que se esta
	 * trabajando posee el indicador de "Obliga Afectaciones"
	 */
	private boolean obligaAfectacion;
	/**
	 * Atributo que almacena el valor del tipo pptal recibido por parametro
	 */
	private String tipoPptal;
	/**
	 * Atributo que almacena el valor del tipo contable recibido por parametro
	 */
	private String tipoContable;
	/**
	 * Atributo que sirve para validar la visibilidad del campo Cdigo CCPET
	 * 
	 */
	private String verCCPET;
	
	
	private boolean verImpuesto;
	/**
	 * Implementacion del EJB de EjbSysmanUtilRemote para hacer el llamado a las
	 * funciones y procedimientos que se invocan dentro del Controlador y se
	 * encuentran almacenadas en el paquete PCK_SYSMAN_UTIL
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Implementacion del EJB de ejbPresupuestoDos para hacer el llamado a las
	 * funciones y procedimientos que se invocan dentro del Controlador y se
	 * encuentran almacenadas en el paquete PCK_PRESUPUESTO_COM2
	 */
	@EJB
	private EjbPresupuestoDosRemote ejbPresupuestoDos;
	/**
	 * Implementacion del EJB de EjbPresupuestoTresRemote para hacer el llamado
	 * a las funciones y procedimientos que se invocan dentro del Controlador y
	 * se encuentran almacenadas en el paquete PCK_PRESUPUESTO_COM3
	 */
	@EJB
	private EjbPresupuestoTresRemote ejbPresupuestoTres;

	@EJB
	private EjbPresupuestoCeroRemote ejbPresupuesto;
	private String politicaPublica;
	private String detalleSectorial;
	private Map<String,Object> parametroswf;

	/**
	 * Crea una nueva instancia de SubdetallecomprobantepptalsControlador
	 */
	public SubdetallecomprobantepptalsControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		fechaCons = "FECHA";
		numeroComprobanteCons = "numeroComprobante";
		nombreComprobanteCons = "nombreComprobante";
		claseComprobanteCons = "claseComprobante";
		tipoComprobanteCons = "tipoComprobante";
		tipoComprobanteAfecCons = "TIPO_CPTE_AFECT";
		cmptAfectadoCons = "CMPTE_AFECTADO";
		cuentaCons = "CUENTA";
		comprobanteCons = "COMPROBANTE";
		centroCostoCons = "CENTRO_COSTO";
		auxiliarCons = "AUXILIAR";
		referenciaCons = "REFERENCIA";
		fuenteRecursoCons = "FUENTE_RECURSO";
		terceroCons = "TERCERO";
		sucursalCons = "SUCURSAL";
		valorDebitoCons = "VALOR_DEBITO";
		valorCreditoCons = "VALOR_CREDITO";
		consecutivoCons = "CONSECUTIVO";
		naturalezaCons = "NATURALEZA";
		modificacionDebitoCons = "MODIFICACION_DEBITO";
		modificacionCreditoCons = "MODIFICACION_CREDITO";
		debitoAfectadoCons = "DEBITO_AFECTADO";
		creditoAfectadoCons = "CREDITO_AFECTADO";
		numeroContratoCons = "NUMEROCONTRATO";
		aprDefinitivaCons = "APRDEFINITIVA";
		aplazamientoTotal = "APLAZAMIENTO";
		codigoSGR = "RECURSO_SGR";
		codigoCons = GeneralParameterEnum.CODIGO.getName();
		verContabilizar = "NO";
		verCCPET = "NO";
		try {
			numFormulario = GeneralCodigoFormaEnum.SUBDETALLECOMPROBANTEPPTALS_CONTROLADOR
					.getCodigo();
			SessionUtil.setSessionVar("modulo", "3");
			validarPermisos();
			cargarFlash();
			abrirFormulario();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
            SessionUtil.cleanFlash();
        }
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del
	 * Bean ha sido creado, en este se realizan las asignaciones iniciales
	 * necesarias para la visualizacion del formulario, como son tablas,
	 * origenes de datos, inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		enumBase = GenericUrlEnum.DETALLE_COMPROBANTE_PPTAL;
		buscarLlave();
		tituloEncabezado = nombreComprobante + " No. - "
				+ numeroComprobante;
		registro = new Registro();
		reasignarOrigen();
		// <CARGAR_LISTA>
		cargarListaReferencia();
		cargarListaTipoDoc();
		cargarListaTipoCpteAfect();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>

		cargarListaTercero();
		cargarListaFuente();
		cargarListaCodigoCuenta();
		cargarListaCmpteAfectado();
		registro.getCampos().put(fechaCons, fechaComprobante);
		calcularPieDeTabla();
		validarContabilizar();
		validarCodigoCCPET();
		cargarListaRecursoSGR();
		cargarListaRecursoSGRE();
//		cargarListaCodigoCPC();
//		cargarListaCodigoCPCE();
//		cargarListaFuenteCUIPO();
//		cargarListaFuenteCUIPOE();
//		cargarListaProductoCUIPO();
//		cargarListaProductoCUIPOE();
//		cargarListaSector();
//		cargarListaSectorE();
//		cargarListaPrograma();
//		cargarListaProgramaE();
//		cargarListaSupPrograma();
//		cargarListaSupProgramaE();
//		cargarListaCodigoProducto();
//		cargarListaCodigoProductoE();
//		cargarListaCodigoBPIN();
//		cargarListaCodigoBPINE();
//		cargarListaCodigoCCPET();
//		cargarListaCodigoCCPETE();
//		cargarListaCodigoCPCDANE();
//		cargarListaCodigoCPCDANEE();
//		cargarListaCodigoUnidEje();
//		cargarListaCodigoUnidEjeE();
//		cargarListaCodigoFuente();
//		cargarListaCodigoFuenteE();
//		cargarListaCodigoCCPETRega();
//		cargarListaCodigoCCPETRegaE();
//		cargarListaPoliticaPublica();
//		cargarListaPoliticaPublicaE();
//		cargarListaDetalleSectorial();
		try {
			cargarConfiguracionCuipo("");
		} catch (SystemException e) {
			e.printStackTrace();
		}
		// </CARGAR_LISTA_COMBO_GRANDE>
	}

	private void validarContabilizar() {
		if (modulo.equals("1")) {

			try {
				verContabilizar = SysmanFunciones.nvlStr(
						ejbSysmanUtil.consultarParametro(compania,
								"MANEJA TRANSACCIONES AUTOMATICAS",
								modulo,
								new Date(), false),
						"NO");
			}
			catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

		}

	}
	private void validarCodigoCCPET() {
		try {
			verCCPET = SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, 
							"VISIBLE CODIGO CCPET",
							modulo,
							new Date(), false), 
					"NO");
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * En este metodo se asigna al atributo origenDatos del bean base el valor
	 * de la consulta del formulario. Tambien carga la lista del formulario por
	 * primera vez
	 */
	@Override
	public void reasignarOrigen() {
		String parametro = null;
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania, "PERMITE CALCULO DEL IMPUESTO A LOS MOVIMIENTOS FINANCIEROS", "3", new Date(), true);
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		if((parametro.equals("SI"))){
			verImpuesto = true;
		}else {
			verImpuesto = false;
		}
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
		parametrosListado.put("TIPO", tipoComprobante);
		parametrosListado.put(GeneralParameterEnum.NUMERO.getName(),
				numeroComprobante);
		
		actualizarCamposSiNulos(registro,"0",1);
	}

	public void actualizarCamposSiNulos(Registro registro, Object variable, int iniciales) {
		
		List<String> camposAVerificar = Arrays.asList("SECTOR", "PROGRAMA", "SUBPROGRAMA", "COD_PROD_CUIPO",
				"CODIGO_BPIN", "CODIGO_CCPET", "CODIGO_CPC", "CODIGOUNIDADEJE", "FUENTE_CUIPO", "CODIGOCCPETREGA",
				"POLITICA_PUBLICA", "DETALLE_SECTORIAL", "RECURSO_SGR");

		Map<String, Object> campos = registro.getCampos();
		if (iniciales == 1){
			for (String campo : camposAVerificar) {
				campos.put(campo, "0");

			}
		}else{
		    for (String campo : camposAVerificar) {
				if (campos.containsKey(campo) && SysmanFunciones.toString(SysmanFunciones.nvl(campos.get(campo),"")).isEmpty()) {
		            campos.put(campo, "0");
		        }
		    }
		}
		
	}
	
	public void activarEdicion(Registro registro) {
		indice = listaInicial.getRowIndex();
		registroIniFila = new Registro();
		registroIniFila.getCampos().put(fechaCons,
				registro.getCampos().get(fechaCons));
		naturalezaCuenta =  (String) registro.getCampos().get("NATURALEZA");
		activarRegistro(registro);
		cargarListaCodigoCuentaE();
		cargarListaCmpteAfectadoE();
		cargarListaTipoDocE();
		cargarListaFuenteE();
		cargarListaTerceroE();
		cargarListaReferenciaE();
		cargarListaCuentaDebitoEquivalenteE();
		cargarListaCuentaCreditoEquivalenteE();

		cargarListaSector();
		cargarListaSectorE();
		cargarListaPrograma();
		cargarListaProgramaE();
		cargarListaSupPrograma();
		cargarListaSupProgramaE();
		cargarListaCodigoProducto();
		cargarListaCodigoProductoE();
//		cargarListaCodigoBPIN();
//		cargarListaCodigoBPINE();
		cargarListaCodigoCCPET();
		cargarListaCodigoCCPETE();
		cargarListaCodigoCPCDANE();
		cargarListaCodigoCPCDANEE();
		cargarListaCodigoUnidEje();
		cargarListaCodigoUnidEjeE();
		cargarListaCodigoFuente();
		cargarListaCodigoFuenteE();
		cargarListaCodigoCCPETRega();
		cargarListaCodigoCCPETRegaE();
		cargarListaPoliticaPublica();
		cargarListaPoliticaPublicaE();
		cargarListaDetalleSectorial();
		cargarListaDetalleSectorialE();
		
		
		try {
			cargarConfiguracionCuipo(extraerString(registro.getCampos().get("CUENTA")));
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	/**
	 * cargar lista Modelo  combos cuipo
	 * @throws SystemException 
	 */
	public void cargarConfiguracionCuipo(String cuenta) throws SystemException {
		modelo =   ejbSysmanUtil.consultarModeloAno(compania,ano);
		modeloN =  modelo;
		bloqSector =  true;

		int aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "001",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqSector =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqSector =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqSector   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "002",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqPrograma =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqPrograma =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqPrograma   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "003",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqSupPrograma =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqSupPrograma =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqSupPrograma   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "004",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoProducto =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoProducto =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoProducto   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "005",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoBPIN =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoBPIN =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoBPIN   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "006",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoCCPET =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoCCPET =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoCCPET   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "007",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoCPCDANE =  true; 
		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoCPCDANE =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoCPCDANE   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "008",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoUnidEje =  true; 

		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoUnidEje =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoUnidEje   =  false; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "009",cuenta);

		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoFuente =  true; 
		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoFuente =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoFuente   =  false; 
		}

		String parametro = getParametro(
				"HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO",
				"NO");
		if(parametro.equals("SI")) { //bloqueado
			bloqCodigoFuente =  true; 
		}

		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "010",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqCodigoCCPETRega =  true; 
		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqCodigoCCPETRega =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqCodigoCCPETRega   =  false; 
		}
		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, ""
				+ ""
				+ "011",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqPoliticaPublica =  true; 
		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqPoliticaPublica =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqPoliticaPublica   =  false; 
		}
		aplicacionCuenta =   ejbSysmanUtil.aplicacionCuenta(compania,ano, "012",cuenta);
		if(aplicacionCuenta ==  0) { //bloqueado
			bloqDetalleSectorial =  true; 
		}
		else if(aplicacionCuenta ==  1){//bloqueado
			bloqDetalleSectorial =  true; 
		}
		else if(aplicacionCuenta ==  2){ //habilitado
			bloqDetalleSectorial   =  false; 
		}
	}

	public void cargarFlash() {
		parametrosEntrada = SessionUtil.getFlash();
		if (parametrosEntrada != null) {

			ingreso = (boolean) parametrosEntrada.get("ingreso");
			if (ingreso) {
				ridContable = (Map<String, Object>) parametrosEntrada
						.get("ridContable");
				tipoPptal = extraerString(parametrosEntrada
						.get("tipoPptal"));
				tipoContable = extraerString(parametrosEntrada
						.get("tipoCont"));
				ridPptal = (Map<String, Object>) parametrosEntrada
						.get("rid");
			}
			ano = extraerString(parametrosEntrada.get("ano"));
			mes = extraerString(parametrosEntrada.get("mes"));
			tipoComprobante = extraerString(parametrosEntrada
					.get(tipoComprobanteCons));
			numeroComprobante = extraerString(parametrosEntrada
					.get(numeroComprobanteCons));
			nombreComprobante = extraerString(parametrosEntrada
					.get(nombreComprobanteCons));
			claseComprobante = extraerString(parametrosEntrada
					.get(claseComprobanteCons));
			tipoDeCuentas = extraerString(
					parametrosEntrada.get("tipoDeCuentas"));
			claseAfectar = extraerString(parametrosEntrada.get("claseAfectar"));

			terceroVariosAfectar = "DIS".equals(claseAfectar)
					|| "ADD".equals(claseAfectar)
					|| "DMD".equals(claseAfectar);

			columna = extraerString(parametrosEntrada.get("columna"));
			afectacion = extraerString(parametrosEntrada.get("afectacion"));
			impreso = (boolean) parametrosEntrada.get("impreso");
			estadoPeriodo = extraerString(
					parametrosEntrada.get("estadoPeriodo"));
			debitoComprobante = Double.valueOf(parametrosEntrada
					.get("debitoComprobante").toString());
			creditoComprobante = Double.valueOf(parametrosEntrada
					.get("creditoComprobante").toString());
			tipoContrato = extraerString(parametrosEntrada.get("tipoContrato"));
			numeroContrato = SysmanFunciones
					.nvl(parametrosEntrada.get("numeroContrato"), 0)
					.toString();
			fechaComprobante = (Date) parametrosEntrada.get("fechaComprobante");
			terceroComprobante = extraerString(parametrosEntrada
					.get("terceroComprobante"));
			sucursalComprobante = extraerString(parametrosEntrada
					.get("sucursalComprobante"));
			nomTerComprobante = extraerString(parametrosEntrada
					.get("nomTerComprobante"));
			papelesComprobante = (boolean) parametrosEntrada
					.get("papelesComprobante");
			contractualComprobante = (boolean) parametrosEntrada
					.get("contractualComprobante");
			descripcionComprobante = extraerString(parametrosEntrada
					.get("descripcionComprobante"));
			tipoDocComprobante = extraerString(parametrosEntrada
					.get("tipoDocComprobante"));
			nroDocComprobante = extraerString(parametrosEntrada
					.get("nroDocComprobante"));
			obligaAfectacion = SysmanFunciones.validarVariableVacio(
					extraerString(parametrosEntrada
							.get("obligaAfectacion"))) ? true
									: (boolean) parametrosEntrada
									.get("obligaAfectacion");

			validarAccion = extraerString(parametrosEntrada
					.get("accion"));
			
			parametroswf = (Map<String,Object>) parametrosEntrada.get("parametroswf");
			if(parametroswf != null) {
				modulo = SessionUtil.getModulo();
			}
			/*
			 * Par�metros sin usar: tipoAfectComprobante
			 */
		}
		else {
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaReferencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);
		// try {
		// listaReferencia = RegistroConverter.toListRegistro(
		// requestManager.getList(UrlServiceUtil.getInstance()
		// .getUrlServiceByUrlByEnumID(
		// SubdetallecomprobantepptalsControladorUrlEnum.URL34513
		// .getValue())
		// .getUrl(), param));
		// }
		// catch (SystemException e) {
		// logger.error(e.getMessage(), e);
		// JsfUtil.agregarMensajeError(e.getMessage());
		// }
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4860
						.getValue());

		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaReferenciaE() {
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);
		// try {
		// listaReferencia = RegistroConverter.toListRegistro(
		// requestManager.getList(UrlServiceUtil.getInstance()
		// .getUrlServiceByUrlByEnumID(
		// SubdetallecomprobantepptalsControladorUrlEnum.URL34513
		// .getValue())
		// .getUrl(), param));
		// }
		// catch (SystemException e) {
		// logger.error(e.getMessage(), e);
		// JsfUtil.agregarMensajeError(e.getMessage());
		// }
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4860
						.getValue());

		listaReferenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaTipoDoc() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL3558
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		listaTipoDoc = new RegistroDataModelImpl(urlBean.getUrl(), null, param,
				true, codigoCons);
	}

	public void cargarListaTipoDocE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL3558
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		listaTipoDocE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaTipoCpteAfect() {
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM2.getValue(),
				claseAfectar);
		try {
			listaTipoCpteAfect = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantepptalsControladorUrlEnum.URL3738
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaTercero() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL3958
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);

		listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");
	}

	public void cargarListaTerceroE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL3958
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);

		listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");
	}

	public void cargarListaAuxiliar() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4238
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);

		listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaFuente() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4238
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);

		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaAuxiliarE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4238
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);

		listaAuxiliarE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	/**
	 * 
	 * Carga la lista listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSector(){
		listaSector =   cargarListaTipoClasificador("001",2,naturalezaCuenta);
	}
	/**
	 * 
	 * Carga la lista listaSectorE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSectorE(){
		listaSectorE =   cargarListaTipoClasificador("001",2,naturalezaCuenta);
	}
	/**
	 * 
	 * Carga la lista listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPrograma(){
		//listaPrograma  =  cargarListaTipoClasificador("002",2,naturalezaCuenta);
		
	        cuentaAux = "001"+sector;
                
		UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
		
	}
	/**
	 * Carga la lista listaProgramaE
	 */
	public void cargarListaProgramaE(){
		//listaProgramaE = cargarListaTipoClasificador("002",2,naturalezaCuenta);
		
	        cuentaAux = "001"+sector;
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaProgramaE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
		
	}
	
	
	public void cargarListaRecursoSGR(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL1884003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "013");
		
		
		listarecursoSGR = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
		
	}
	
	public void cargarListaRecursoSGRE(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL1884003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "013");
		
		
		listarecursoSGRE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
		
	}
	/**
	 * 
	 * Carga la lista listaSupPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSupPrograma(){
		listaSupPrograma =   cargarListaTipoClasificador("003",2,naturalezaCuenta);
	}
	public void cargarListaSupProgramaE(){
		listaSupProgramaE =  cargarListaTipoClasificador("003",2,naturalezaCuenta);;
	}
	/**
	 * 
	 * Carga la lista listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoProducto(){
		//listaCodigoProducto  =  cargarListaTipoClasificador("004",2,naturalezaCuenta);
		
		cuentaAux = "002"+programa;
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaCodigoProducto = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
		
	}
	public void cargarListaCodigoProductoE(){
		//listaCodigoProductoE =  cargarListaTipoClasificador("004",2,naturalezaCuenta);
		
		cuentaAux = "002"+programa;
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaCodigoProductoE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 * Carga la lista listaCodigoBPIN
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
//	public void cargarListaCodigoBPIN(){
//		listaCodigoBPIN =  cargarListaTipoClasificador("005",2,naturalezaCuenta);
//	}
//	/**
//	 * 
//	 */
//	public void cargarListaCodigoBPINE(){
//		listaCodigoBPINE  =  cargarListaTipoClasificador("005",2,naturalezaCuenta);
//	}
	/**
	 * 
	 * Carga la lista listaCodigoCCPET
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCCPET(){
		listaCodigoCCPET =  cargarListaTipoClasificador("006",2,naturalezaCuenta);
	}

	public void cargarListaCodigoCCPETE(){
		listaCodigoCCPETE =  cargarListaTipoClasificador("006",2,naturalezaCuenta);
	}
	/**
	 * 
	 * Carga la lista listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCPCDANE(){
		//listaCodigoCPCDANE = cargarListaTipoClasificador("007",2,naturalezaCuenta);
		
		if(!bloqCodigoCCPET) {
			cuentaAux = "006"+codigoCCPET;
		}else if (!bloqCodigoCCPETRega) {
			cuentaAux = "010"+codigoCCPETRega;
		}
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaCodigoCPCDANE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
	}
	/**
	 * 
	 */
	public void cargarListaCodigoCPCDANEE(){		
		//listaCodigoCPCDANEE =  cargarListaTipoClasificador("007",2,naturalezaCuenta);
		
		if(!bloqCodigoCCPET) {
			cuentaAux = "006"+codigoCCPET;
		}else if (!bloqCodigoCCPETRega) {
			cuentaAux = "010"+codigoCCPETRega;
		}
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaCodigoCPCDANEE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		
		
	}
	/**
	 * 
	 * Carga la lista listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoUnidEje(){
		listaCodigoUnidEje =  cargarListaTipoClasificador("008",2,naturalezaCuenta);
	}
	/**
	 * 
	 */
	public void cargarListaCodigoUnidEjeE(){
		listaCodigoUnidEjeE =  cargarListaTipoClasificador("008",2,naturalezaCuenta);
	}


	/**
	 * 
	 * Carga la lista listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCCPETRega(){
		listaCodigoCCPETRega =  cargarListaTipoClasificador("010",2,naturalezaCuenta );
	}
	/**
	 * 
	 */
	public void cargarListaCodigoCCPETRegaE(){
		listaCodigoCCPETRegaE =  cargarListaTipoClasificador("010",2,naturalezaCuenta );
	}
	/**
	 * 
	 * Carga la lista listaPoliticaPublica
	 *
	 */
	public void cargarListaPoliticaPublica(){
		listaPoliticaPublica =  cargarListaTipoClasificador("011",2,naturalezaCuenta );
	}
	/**
	 * 
	 */
	public void cargarListaPoliticaPublicaE(){
		listaPoliticaPublicaE = listaPoliticaPublica;
	}
	/**
	 * 
	 */
	public void cargarListaDetalleSectorial(){
		//listaDetalleSectorial =  cargarListaTipoClasificador("012",2,naturalezaCuenta);
		
		if(!claseComprobante.equals("ING")) {
			cuentaAux = "008"+codigoUnidEje;
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaDetalleSectorial = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		}else if(claseComprobante.equals("ING")){
			UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    SubdetallecomprobantepptalsControladorUrlEnum.URL1884063
                                    .getValue());
		    Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		    param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		
		    listaDetalleSectorial = new RegistroDataModelImpl(urlBean.getUrl(),
		                    urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
				}
		
	}
	/**
	 * 
	 */
	public void cargarListaDetalleSectorialE(){
		//listaDetalleSectorialE =  cargarListaTipoClasificador("012",2,naturalezaCuenta);
		if(!claseComprobante.equals("ING")) {
			cuentaAux = "008"+codigoUnidEje;
                
                UrlBean urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                SubdetallecomprobantepptalsControladorUrlEnum.URL1870008
                                                .getValue());
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), ano);
                param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), cuentaAux);


                listaDetalleSectorialE = new RegistroDataModelImpl(urlBean.getUrl(),
                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
		}else if(claseComprobante.equals("ING")){
			UrlBean urlBean = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                                    SubdetallecomprobantepptalsControladorUrlEnum.URL1884063
                                    .getValue());
		    Map<String, Object> param = new TreeMap<>();
		    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		    param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		
		    listaDetalleSectorialE = new RegistroDataModelImpl(urlBean.getUrl(),
		                    urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
				}
	}
	/**
	 * 
	 * Carga la lista listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoFuente() {
//		UrlBean urlBean = UrlServiceUtil.getInstance()
//				.getUrlServiceByUrlByEnumID(
//						SubdetallecomprobantepptalsControladorUrlEnum.URL4238
//						.getValue());
//		Map<String, Object> param = new TreeMap<>();
//		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
//				compania);
//		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
//				ano);
//
//		listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param,
//				true, codigoCons);
		listaCodigoFuente =  cargarListaTipoClasificador("009",2,naturalezaCuenta);
	}

	/**
	 * 
	 */
	public void cargarListaCodigoFuenteE() {
//		UrlBean urlBean = UrlServiceUtil.getInstance()
//				.getUrlServiceByUrlByEnumID(
//						SubdetallecomprobantepptalsControladorUrlEnum.URL4238
//						.getValue());
//		Map<String, Object> param = new TreeMap<>();
//		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
//				compania);
//		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
//				ano);
//
//		listaCodigoFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param,
//				true, codigoCons);
		listaCodigoFuenteE = listaCodigoFuente;
	}
	/**
	 * 
	 * Carga la lista listaCuentaDebitoEquivalente
	 *
	 */
	public void cargarListaCuentaDebitoEquivalente() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4848
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		param.put(GeneralParameterEnum.RUBRO.getName(),
				registro.getCampos().get(cuentaCons));

		param.put(GeneralParameterEnum.AUXILIAR.getName(),
				registro.getCampos().get(GeneralParameterEnum.AUXILIAR
						.getName()));
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				registro.getCampos()
				.get(GeneralParameterEnum.CENTRO_COSTO
						.getName()));
		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				registro.getCampos()
				.get(GeneralParameterEnum.FUENTE_RECURSO
						.getName()));
		param.put(GeneralParameterEnum.REFERENCIA.getName(),
				registro.getCampos().get(GeneralParameterEnum.REFERENCIA
						.getName()));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO
						.getName()));

		listaCuentaDebitoEquivalente = new RegistroDataModelImpl(
				urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CUENTA_DEBITO");

	}

	/**
	 * 
	 * Carga la lista listaCuentaDebitoEquivalente
	 *
	 */
	public void cargarListaCuentaDebitoEquivalenteE() {

		Map<String, Object> campos = listaInicial.getDatasource().get(indice)
				.getCampos();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4848
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		param.put(GeneralParameterEnum.RUBRO.getName(),
				campos.get(cuentaCons));

		param.put(GeneralParameterEnum.AUXILIAR.getName(),
				campos.get(GeneralParameterEnum.AUXILIAR.getName()));
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				campos.get(GeneralParameterEnum.CENTRO_COSTO
						.getName()));
		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				campos.get(GeneralParameterEnum.FUENTE_RECURSO
						.getName()));
		param.put(GeneralParameterEnum.REFERENCIA.getName(),
				campos.get(GeneralParameterEnum.REFERENCIA.getName()));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				campos.get(GeneralParameterEnum.TERCERO.getName()));

		listaCuentaDebitoEquivalenteE = new RegistroDataModelImpl(
				urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CUENTA_DEBITO");
	}

	/**
	 * 
	 * Carga la lista listaCuentaCreditoEquivalente
	 *
	 */
	public void cargarListaCuentaCreditoEquivalente() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4848
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		param.put(GeneralParameterEnum.RUBRO.getName(),
				registro.getCampos().get(cuentaCons));

		param.put(GeneralParameterEnum.AUXILIAR.getName(),
				registro.getCampos().get(GeneralParameterEnum.AUXILIAR
						.getName()));
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				registro.getCampos()
				.get(GeneralParameterEnum.CENTRO_COSTO
						.getName()));
		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				registro.getCampos()
				.get(GeneralParameterEnum.FUENTE_RECURSO
						.getName()));
		param.put(GeneralParameterEnum.REFERENCIA.getName(),
				registro.getCampos().get(GeneralParameterEnum.REFERENCIA
						.getName()));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				registro.getCampos().get(GeneralParameterEnum.TERCERO
						.getName()));

		listaCuentaCreditoEquivalente = new RegistroDataModelImpl(
				urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CUENTA_CREDITO");
	}

	/**
	 * 
	 * Carga la lista listaCuentaCreditoEquivalente
	 *
	 */
	public void cargarListaCuentaCreditoEquivalenteE() {
		Map<String, Object> campos = listaInicial.getDatasource().get(indice)
				.getCampos();

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4848
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		param.put(GeneralParameterEnum.ANO.getName(),
				ano);

		param.put(GeneralParameterEnum.RUBRO.getName(),
				campos.get(cuentaCons));

		param.put(GeneralParameterEnum.AUXILIAR.getName(),
				campos.get(GeneralParameterEnum.AUXILIAR.getName()));
		param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
				campos.get(GeneralParameterEnum.CENTRO_COSTO
						.getName()));
		param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
				campos.get(GeneralParameterEnum.FUENTE_RECURSO
						.getName()));
		param.put(GeneralParameterEnum.REFERENCIA.getName(),
				campos.get(GeneralParameterEnum.REFERENCIA.getName()));
		param.put(GeneralParameterEnum.TERCERO.getName(),
				campos.get(GeneralParameterEnum.TERCERO.getName()));

		listaCuentaCreditoEquivalenteE = new RegistroDataModelImpl(
				urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CUENTA_CREDITO");

	}

	/**
	 * 
	 * Carga la lista listaCodigoCPC
	 *
	 */
	public void cargarListaCodigoCPC() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL1870001
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaCodigoCPC = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaCodigoCPC
	 *
	 */
	public void cargarListaCodigoCPCE() {
		listaCodigoCPCE = listaCodigoCPC;
	}

	/**
	 * 
	 * Carga la lista listaTipoClasificador Unificada cperez
	 *
	 */
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo,int aplicacion, String naturaleza)
	{
		RegistroDataModelImpl listaTipo = null;
		String clasePadre = "";		
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		if(naturaleza != null && !naturaleza.isEmpty() && !naturaleza.equals(""))
		{			
//			clasePadre = cargarClasePadre(codigo,aplicacion,naturaleza);		
//
//			if(clasePadre.isEmpty() || clasePadre == null || clasePadre.equals("") )
//			{
//				param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
//				param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
//				param.put(GeneralParameterEnum.CLASE.getName(), codigo);
//
//				urlBean = UrlServiceUtil.getInstance()
//						.getUrlServiceByUrlByEnumID(
//								SubdetallecomprobantepptalsControladorUrlEnum.URL13433
//								.getValue());
//			}
//			else
//			{			
//				//clasePadre = ","+clasePadre+",";
//				//codio provisional para luego con clasificadores hijo y padre debe borrarse
//				if(clasePadre.equals("006,008")) {
//					clasePadre = clasePadre.split(",")[1].toString();
//				}
//				clasePadre = clasePadre.replaceAll(",", "");
				param.put(GeneralParameterEnum.CLASE.getName(), codigo);

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SubdetallecomprobantepptalsControladorUrlEnum.URL13432
								.getValue());
//			}				

			listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());


		}		 
		return listaTipo;
	}


	/**
	 * 
	 * Carga la lista listaTipoClasificador Unificada cperez
	 *
	 */
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo,int aplicacion, String naturaleza,String filtro)
	{
		RegistroDataModelImpl listaTipo = null;
		String clasePadre = "";		
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		if(naturaleza != null && !naturaleza.isEmpty() && !naturaleza.equals(""))
		{			
			clasePadre = cargarClasePadre(codigo,aplicacion,naturaleza);		

			if(clasePadre.isEmpty() || clasePadre == null || clasePadre.equals("") )
			{
				param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
				param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
				param.put(GeneralParameterEnum.CLASE.getName(), codigo);



				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SubdetallecomprobantepptalsControladorUrlEnum.URL13433
								.getValue());
			}
			else
			{			
				param.put("CODIGOKEY", filtro);
				param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
				param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
				param.put(GeneralParameterEnum.CLASE.getName(), codigo);


				param.put(GeneralParameterEnum.CLASE.getName(), codigo);

				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(
								SubdetallecomprobantepptalsControladorUrlEnum.URL1884034
								.getValue());
			}				
			listaTipo =  null;
			listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.CODIGO.getName());


		}		 
		return listaTipo;
	}

	/*
	 * 
	 */
	public String cargarClaseHijo(String codigo,int aplicacion, String Naturaleza,String idHijo) {
		String padre = "";
		Map<String, Object> param = new TreeMap<>();
		List <Registro>  listaPadre =  null;

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), Naturaleza);
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);	
		param.put("IDHIJO", idHijo);	

		try {
			listaPadre =  RegistroConverter.toListRegistro( requestManager.getList( UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							SubdetallecomprobantepptalsControladorUrlEnum.URL1884033.getValue()).getUrl()
					,param));
			if (!listaPadre.isEmpty()) {
				for( Registro id: listaPadre) {
					padre = id.getCampos().get("IDPADRE").toString();
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return padre;
	}


	/*
	 * 
	 */
	public String cargarClasePadre(String codigo,int aplicacion, String Naturaleza) {
		String clasePadre = "";
		Map<String, Object> param = new TreeMap<>();
		List <Registro>  listaClasePadre =  null;

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), Naturaleza);
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);	

		try {
			listaClasePadre =  RegistroConverter.toListRegistro( requestManager.getList( UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							SubdetallecomprobantepptalsControladorUrlEnum.URL13431.getValue()).getUrl()
					,param));
			if (!listaClasePadre.isEmpty()) {
				for( Registro clase: listaClasePadre) {
					clasePadre = clase.getCampos().get("CLASEPADRE").toString();
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return clasePadre;
	}

	

	/**
	 * 
	 * Carga la lista listaProductoCUIPO
	 *
	 */
	public void cargarListaProductoCUIPO() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL1874001
						.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaProductoCUIPO = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaProductoCUIPO
	 *
	 */
	public void cargarListaProductoCUIPOE() {
		listaProductoCUIPOE = listaProductoCUIPO;
	}

	public void cargarListaFuenteE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4238
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);

		listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaCodigoCuenta() {

		String validarParametro = getParametro(
				"PERMITE MANEJAR INGRESO Y GASTOS EN APLAZAMIENTOS",
				"NO");

		UrlBean urlBean;
		if (validarParametro.equals("SI")) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							SubdetallecomprobantepptalsControladorUrlEnum.URL4859
							.getValue());
		}
		else {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							SubdetallecomprobantepptalsControladorUrlEnum.URL4858
							.getValue());
		}
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM2.getValue(),
				claseComprobante);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM3.getValue(),
				tipoDeCuentas);

		listaCodigoCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
		auxiliarCodigo =  "";

	}

	public void cargarListaCodigoCuentaE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL4858
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM2.getValue(),
				claseComprobante);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM3.getValue(),
				tipoDeCuentas);

		listaCodigoCuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);

	}

	public void cargarListaCmpteAfectado() {

		Map<String, Object> campos = registro.getCampos();
		if ((campos.get(tipoComprobanteAfecCons) != null)
				&& (campos.get(cuentaCons) != null)) {
			String nombreParametro = "FILTRAR POR TERCERO EN DETALLE PRESUPUESTAL";
			String parametroFiltraTercero = getParametro(nombreParametro, "NO");

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							SubdetallecomprobantepptalsControladorUrlEnum.URL5959
							.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0
					.getValue(),
					compania);
			param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1
					.getValue(),
					ano);
			param.put("TIPO", campos.get(tipoComprobanteAfecCons));
			param.put(cuentaCons, campos.get(cuentaCons));
			param.put("FECHA_DET", campos.get(fechaCons));
			param.put("AFECTACION", afectacion);
			param.put("CLASECOMPROBANTE", claseComprobante);
			param.put(centroCostoCons, campos.get(centroCostoCons));
			param.put(auxiliarCons, campos.get(auxiliarCons));
			param.put(referenciaCons, campos.get(referenciaCons));
			param.put(fuenteRecursoCons,
					campos.get(fuenteRecursoCons));
			param.put(terceroCons, terceroVariosAfectar
					? SysmanConstantes.CONS_TERCERO
							: campos.get(terceroCons));
			param.put(sucursalCons, terceroVariosAfectar
					? SysmanConstantes.CONS_SUCURSAL
							: campos.get(sucursalCons));
			param.put("FILTRA_TERCERO", parametroFiltraTercero);

			listaCmpteAfectado = new RegistroDataModelImpl(
					urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					true, GeneralParameterEnum.ID.getName());
		}
		else {
			listaCmpteAfectado = null;
		}
	}

	public void cargarListaCmpteAfectadoE() {
		Map<String, Object> campos = listaInicial.getDatasource().get(indice)
				.getCampos();
		String nombreParametro = "FILTRAR POR TERCERO EN DETALLE PRESUPUESTAL";
		String parametroFiltraTercero = getParametro(nombreParametro, "NO");

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL5959
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);
		param.put("TIPO", campos.get(tipoComprobanteAfecCons));
		param.put(cuentaCons, campos.get(cuentaCons));
		param.put("FECHA_DET", campos.get(fechaCons));
		param.put("AFECTACION", afectacion);
		param.put("CLASECOMPROBANTE", claseComprobante);
		param.put(centroCostoCons, campos.get(centroCostoCons));
		param.put(auxiliarCons, campos.get(auxiliarCons));
		param.put(referenciaCons, campos.get(referenciaCons));
		param.put(fuenteRecursoCons, campos.get(fuenteRecursoCons));
		param.put(terceroCons, campos.get(terceroCons));
		param.put(sucursalCons, campos.get(sucursalCons));
		param.put("FILTRA_TERCERO", parametroFiltraTercero);

		listaCmpteAfectadoE = new RegistroDataModelImpl(
				urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.ID.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	public void cambiarProgramarPAC() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarProgramarPACC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarfecha() {
		// <CODIGO_DESARROLLADO>
		cargarListaCmpteAfectado();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarfechaC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		if (listaInicial.getDatasource().get(rowNum % 10).getCampos()
				.get(fechaCons) == null) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
					fechaCons,
					registroIniFila.getCampos().get(fechaCons));
		}
		cargarListaCmpteAfectadoE();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTipoCpteAfect() {
		// <CODIGO_DESARROLLADO>
		cargarListaCmpteAfectado();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTipoCpteAfectC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		cargarListaCmpteAfectado();
		cargarListaCmpteAfectadoE();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCmpteAfectadoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control CuentaDebitoEquivalente en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarCuentaDebitoEquivalenteC(int rowNum) {

		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");
		// Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
		// se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA",
		// "hola ");
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control CuentaCreditoEquivalente en la
	 * fila seleccionada dentro de la grilla
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarCuentaCreditoEquivalenteC(int rowNum) {
		// Para el cambio en una fila selecciona (PARA FORMULARIOS
		// CONTINUOS) se realiza como lo muestra la siguiente linea
		// listaInicial.getDatasource().get(rowNum %
		// 10).getCampos().put("FECHALARGA", "hola ");
		// Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
		// se realiza como lo muestra la siguiente linea
		// listaInicial.get(rowNum).getCampos().put("FECHALARGA",
		// "hola ");
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCreditoAfectadoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		calcularSaldoNeto(listaInicial.getDatasource().get(rowNum % 10));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarDebitoAfectadoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		calcularSaldoNeto(listaInicial.getDatasource().get(rowNum % 10));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarValorCredito() {
		// <CODIGO_DESARROLLADO>
		if ("".equals(SysmanFunciones
				.nvl(registro.getCampos().get(valorCreditoCons), "")
				.toString().trim())) {
			registro.getCampos().put(valorCreditoCons, "0");
		}
		activarRegistroNuevo();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarValorDebito() {
		// <CODIGO_DESARROLLADO>
		if ("".equals(SysmanFunciones
				.nvl(registro.getCampos().get(valorDebitoCons), "")
				.toString().trim())) {
			registro.getCampos().put(valorDebitoCons, "0");
		}
		activarRegistroNuevo();
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarValorCreditoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		if ("".equals(SysmanFunciones
				.nvl(listaInicial.getDatasource().get(rowNum % 10)
						.getCampos().get(valorCreditoCons), "")
				.toString().trim())) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(valorCreditoCons, "0");
		}
		activarRegistro(listaInicial.getDatasource().get(rowNum % 10));
		calcularSaldoNeto(listaInicial.getDatasource().get(rowNum % 10));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarValorDebitoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		if ("".equals(SysmanFunciones
				.nvl(listaInicial.getDatasource().get(rowNum % 10)
						.getCampos().get(valorDebitoCons), "")
				.toString().trim())) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos()
			.put(valorDebitoCons, "0");
		}
		activarRegistro(listaInicial.getDatasource().get(rowNum % 10));
		calcularSaldoNeto(listaInicial.getDatasource().get(rowNum % 10));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarModificacionCreditoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		calcularSaldoNeto(listaInicial.getDatasource().get(rowNum % 10));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarModificacionDebitoC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		calcularSaldoNeto(listaInicial.getDatasource().get(rowNum % 10));
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarTerceroC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put("NOMBRETERCERO", nombreTerceroC);
		listaInicial.getDatasource().get(rowNum % 10).getCampos()
		.put(sucursalCons, sucursalC);
		// </CODIGO_DESARROLLADO>
	}

	public void cambiarCodigoCuentaC(int rowNum) {
		// <CODIGO_DESARROLLADO>
		double valorCredito = SysmanFunciones
				.nvlDbl(registro.getCampos().get(valorCreditoCons), 0);
		if ("DIS".equalsIgnoreCase(claseComprobante)
				|| "RES".equalsIgnoreCase(claseComprobante)
				|| "REO".equalsIgnoreCase(claseComprobante)
				|| "EGR".equalsIgnoreCase(claseComprobante)) {
			if (Double.doubleToRawLongBits(valorCredito) != 0) {
				bloqValorCreditoCont = false;
			}
			else {
				bloqValorCreditoCont = true;
			}
		}
		// </CODIGO_DESARROLLADO>
	}
	//7732378

		/**
		 * Metodo ejecutado al cambiar el control ValorBase
		 * 
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 */
		public void cambiarValorBase() {
			// <CODIGO_DESARROLLADO>
			// </CODIGO_DESARROLLADO>
		}

		/**
		 * Metodo ejecutado al cambiar el control ValorBase en la fila seleccionada
		 * dentro de la grilla
		 * 
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @param rowNum indice de la fila seleccionada
		 */
		public void cambiarValorBaseC(int rowNum) {

			// <CODIGO_DESARROLLADO>
			// </CODIGO_DESARROLLADO>
		}

		/**
		 * Metodo ejecutado al cambiar el control Impuesto
		 * 
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 */
		public void cambiarImpuesto() {
			// <CODIGO_DESARROLLADO>
			// </CODIGO_DESARROLLADO>
		}

		/**
		 * Metodo ejecutado al cambiar el control Impuesto en la fila seleccionada
		 * dentro de la grilla
		 * 
		 * TODO DOCUMENTACION ADICIONAL
		 * 
		 * @param rowNum indice de la fila seleccionada
		 */
		public void cambiarImpuestoC(int rowNum) {

		}
	//7732378
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaTercero(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(terceroCons,
				registroAux.getCampos().get("NIT"));
	}

	public void seleccionarFilaTerceroE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("NIT"));
		nombreTerceroC = extraerString(
				registroAux.getCampos().get("NOMBRE"));
		sucursalC = extraerString(
				registroAux.getCampos().get(sucursalCons));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferencia
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferencia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("REFERENCIA",
				registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaReferencia
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();

		auxiliar = SysmanFunciones
				.toString(registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));

	}

	public void seleccionarFilaAuxiliar(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(auxiliarCons,
				registroAux.getCampos().get(codigoCons));
	}

	public void seleccionarFilaAuxiliarE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get(codigoCons));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaDebitoEquivalente
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaDebitoEquivalente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_DEBITO",
				registroAux.getCampos().get("CUENTA_DEBITO"));

		registro.getCampos().put("CUENTA_CREDITO",
				registroAux.getCampos().get("CUENTA_CREDITO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaDebitoEquivalente
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaDebitoEquivalenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get("CUENTA_DEBITO"), "")
				.toString();

		listaInicial.getDatasource().get(indice)
		.getCampos().put("CUENTA_CREDITO",
				registroAux.getCampos()
				.get("CUENTA_CREDITO"));

	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaCreditoEquivalente
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaCreditoEquivalente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CUENTA_CREDITO",
				registroAux.getCampos().get("CUENTA_CREDITO"));

		registro.getCampos().put("CUENTA_DEBITO",
				registroAux.getCampos().get("CUENTA_DEBITO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuentaCreditoEquivalente
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuentaCreditoEquivalenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.nvl(registroAux.getCampos().get("CUENTA_CREDITO"), "")
				.toString();

		listaInicial.getDatasource().get(indice)
		.getCampos().put("CUENTA_DEBITO",
				registroAux.getCampos()
				.get("CUENTA_DEBITO"));

	}
	
	
	public void seleccionarFilarecursoSGR(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("RECURSO_SGR",
				registroAux.getCampos().get("CODIGO"));
		
	}
	
	public void seleccionarFilarecursoSGRE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones
				.toString(registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()));
	}
	


	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPC
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPC(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CPC",
				registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPC
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
				"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteCUIPO
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteCUIPO(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE_CUIPO",
				registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaFuenteCUIPO
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteCUIPOE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
				"").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProductoCUIPO
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProductoCUIPO(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("COD_PROD_CUIPO",
				registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaProductoCUIPO
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProductoCUIPOE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.nvl(
				registroAux.getCampos().get(
						GeneralParameterEnum.CODIGO.getName()),
				"").toString();
	}

	public void seleccionarFilaFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(fuenteRecursoCons,
				registroAux.getCampos().get(codigoCons));
	}

	public void seleccionarFilaFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get(codigoCons));
	}

	public void seleccionarFilaCodigoCuenta(SelectEvent event) {
		auxiliarCodigo = "";
		Registro registroAux = (Registro) event.getObject();
		if (registroAux == null) {

			return;
		}
		try {
			cargarConfiguracionCuipo(extraerString(registroAux.getCampos().get("CODIGO")));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		cuentaRegalias =  "";
		Map<String, Object> parame = new HashMap<>();
		parame.put("COMPANIA", compania);
		parame.put("ANIO", String.valueOf(SysmanFunciones.ano(
				new Date())));
		parame.put("CUENTA",registroAux.getCampos().get(codigoCons));

		try {
			listaTipoCuentaRegalias = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubdetallecomprobantepptalsControladorUrlEnum.URL45086.getValue())
									.getUrl(),
									parame));
		} catch (SystemException e) {
			e.printStackTrace();
		}

		for (Registro option : listaTipoCuentaRegalias) {
			if(option.getCampos().get("REGALIAS").toString().equals("SI") || option.getCampos().get("REGALIAS").toString().equals("NO") ) {
				cuentaRegalias       =  option.getCampos().get("REGALIAS").toString();	
			}else  {
				cuentaRegalias       = "";	
			}
		}
		registro.getCampos().put("FUENTE_CUIPO","");
		cargarListaCodigoFuente();
		cargarListaCodigoFuenteE();
		registro.getCampos().put("SECTOR","");
		registro.getCampos().put("PROGRAMA","");
		registro.getCampos().put("SUBPROGRAMA","");
		registro.getCampos().put("COD_PROD_CUIPO","");
		registro.getCampos().put("CODIGO_BPIN","");
		registro.getCampos().put("CODIGO_CCPET","");
		registro.getCampos().put("CODIGO_CPC","");
		registro.getCampos().put("CODIGOUNIDADEJE","");
		registro.getCampos().put("FUENTE_CUIPO","");
		registro.getCampos().put("CODIGOCCPETREGA","");

		if (registroAux.getCampos().isEmpty()) {
			registro.getCampos().put(cuentaCons,
					registroAux.getCampos().get(null));
			cargarListaCmpteAfectado();
			return;
		}
		if (!"MOP".equals(claseComprobante) && "0".equals(registroAux
				.getCampos().get("MAN_PAC").toString())
				&& "N".equals(registroAux.getCampos().get("MOVIMIENTO")
						.toString())) {
			/*
			 * Se deja unicamente la validacion por el campo movimiento, el cual
			 * incluye la suma de todos los auxiliares, por lo cual, en caso de
			 * que exista un indicador auxiliar activo, se vera reflejado en el
			 * campo movimiento de la consulta de Codigo_Cuenta.
			 */
			registro.getCampos().remove(cuentaCons);
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1266"));
			return;

		}
		registro.getCampos().put(cuentaCons,registroAux.getCampos().get(codigoCons));
		registro.getCampos().put(fechaCons, fechaComprobante);
		registro.getCampos().put("DESCRIPCION", descripcionComprobante);
		manejaTercero = registroAux.getCampos().get("MAN_AUX_TER");
		manejaAuxiliar = registroAux.getCampos().get("MAN_AUX_GEN");
		manejaFuente = registroAux.getCampos().get("MAN_AUX_FUE");
		manejaReferencia = registroAux.getCampos().get("MAN_AUX_REF");
		manejaCentroCosto = registroAux.getCampos().get("MAN_CEN_CTO");
		registro.getCampos().put(naturalezaCons,
				registroAux.getCampos().get(naturalezaCons));
		registro.getCampos().put(valorDebitoCons, 0);
		registro.getCampos().put(valorCreditoCons, 0);
		registro.getCampos().put("PAPELES", papelesComprobante);
		registro.getCampos().put("CONTRACTUAL", contractualComprobante);
		if ("DIS".equals(claseComprobante)) {
			registro.getCampos().put("CONSITUACIONFONDOS",
					registroAux.getCampos()
					.get("CONSITUACIONFONDOS"));
		}

		String naturaleza = extraerString(
				registroAux.getCampos().get(naturalezaCons));


		if ("D".equals(naturaleza) && (claseComprobante.equals("ADC")
				|| claseComprobante.equals("ADD") || claseComprobante.equals("ADR")
				|| claseComprobante.equals("AEG") || claseComprobante.equals("ARO")
				|| claseComprobante.equals("DIS") || claseComprobante.equals("DRX")
				|| claseComprobante.equals("RES") || claseComprobante.equals("REO")
				|| claseComprobante.equals("EGR"))) {

			bloqContCredito = true;
			bloqContCreditoCont = true;
		}
		naturalezaCuenta =  naturaleza;
		auxiliarCodigo =  "";
		cuentaAux =  "";
		
//		cargarListaFuenteCUIPO();
//		cargarListaFuenteCUIPOE();
		/*
		 * Limpia el comprobante afectado seleccionado cuando se cambia de
		 * cuenta
		 */
		registro.getCampos().put(tipoComprobanteAfecCons, null);
		registro.getCampos().put(cmptAfectadoCons, null);

		cargarListaCuentaDebitoEquivalente();
		cargarListaCuentaCreditoEquivalente();

		activarRegistroNuevo();

		
		
		sector =  "";
		programa  = "";
		supPrograma = "";
		codigoProducto = "";
		codigoBPIN   = "";
		codigoCCPET = "";
		codigoCPCDANE= "";
		codigoUnidEje = "";
		codigoFuente = "";
		codigoCCPETRega = "";
		Registro regTemp = null;
		Map<String, Object> param = new HashMap<>();
		param.put("COMPANIA", compania);
		param.put("ANIO", ano);
		param.put("CUENTA",registroAux.getCampos().get(codigoCons));
		// SE CARGA EL REGITRO DEL DETALLE PARA COMPLETAR LA INFORMACION A MOSTRAR
		try {
			param.put("ANO", String.valueOf(SysmanFunciones.ano(
					new Date())));
			param.put("NUMERO",numeroComprobante);
			param.put("TIPO",tipoComprobante);
			List<Registro> listRegTemp = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubdetallecomprobantepptalsControladorUrlEnum.URL38066.getValue())
									.getUrl(),
									param));;
									
			if(!listRegTemp.isEmpty())
				regTemp = listRegTemp.get(0);
			
			param.remove("NUMERO");
			param.remove("ANO");
			param.remove("TIPO");
			listaTipoClasificador = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ObligaPedirDatosControladorUrlEnum.URL0005.getValue())
									.getUrl(),
									param));
		} catch (SystemException e) {
			e.printStackTrace();
		}
		
		for (Registro option : listaTipoClasificador) {

			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("001")) {
				sector       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("SECTOR") != null) {
				sector       = regTemp.getCampos().get("SECTOR").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("002")) {
				programa       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("PROGRAMA") != null) {
				programa       = regTemp.getCampos().get("PROGRAMA").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("003")) {
				supPrograma       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("SUBPROGRAMA") != null) {
				supPrograma       = regTemp.getCampos().get("SUBPROGRAMA").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("004")) {
				codigoProducto       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("COD_PROD_CUIPO") != null) {
				codigoProducto       = regTemp.getCampos().get("COD_PROD_CUIPO").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("005")) {
				codigoBPIN       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("CODIGO_BPIN") != null) {
				codigoBPIN       = regTemp.getCampos().get("CODIGO_BPIN").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("006")) {
				codigoCCPET       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("CODIGO_CCPET") != null) {
				codigoCCPET       = regTemp.getCampos().get("CODIGO_CCPET").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("007")) {
				codigoCPCDANE       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("CODIGO_CPC") != null) {
				codigoCPCDANE       = regTemp.getCampos().get("CODIGO_CPC").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("008")) {
				codigoUnidEje       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("CODIGOUNIDADEJE") != null) {
				codigoUnidEje       = regTemp.getCampos().get("CODIGOUNIDADEJE").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("009")) {
				String parametro = getParametro(
						"HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO",
						"NO");
				if(!parametro.equals("SI")) {					
						codigoFuente       =  option.getCampos().get("TIPOCLASIFICADOR").toString();
						
				}else {
					codigoFuente       = "";
				}
			}else if(regTemp != null && regTemp.getCampos().get("FUENTE_CUIPO") != null) {
				String parametro = getParametro(
						"HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO",
						"NO");
				if(!parametro.equals("SI")) {					
						codigoFuente       =  regTemp.getCampos().get("FUENTE_CUIPO").toString();;
						
				}else {
					codigoFuente       = "";
				}
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("010")) {
				codigoCCPETRega       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("CODIGOCCPETREGA") != null) {
				codigoCCPETRega       = regTemp.getCampos().get("CODIGOCCPETREGA").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("011")) {
				politicaPublica    =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("POLITICA_PUBLICA") != null) {
				politicaPublica       = regTemp.getCampos().get("POLITICA_PUBLICA").toString();
			};
			if(option.getCampos().get("CLASECLASIFICADOR").toString().equals("012")) {
				detalleSectorial       =  option.getCampos().get("TIPOCLASIFICADOR").toString();	
			}else if(regTemp != null && regTemp.getCampos().get("DETALLE_SECTORIAL") != null) {
				detalleSectorial       = regTemp.getCampos().get("DETALLE_SECTORIAL").toString();
			};



		}

		int aplicacionCuenta;
		int aplicacionCuenta006;
		try {
			aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania,ano, "010",registroAux.getCampos().get(codigoCons).toString());
			aplicacionCuenta006 = ejbSysmanUtil.aplicacionCuenta(compania,ano, "006",registroAux.getCampos().get(codigoCons).toString());
			
			if(cuentaRegalias.equals("SI") ) {
				codigoCCPET = "";
				bloqCodigoCCPET = true; 
				if(aplicacionCuenta ==  2) {
					bloqCodigoCCPETRega   =  false; 
				}
				else {
					codigoCCPETRega = "";
					bloqCodigoCCPETRega   =  true; 
				}
				
			}else {
				codigoCCPETRega = "";
				bloqCodigoCCPETRega =  true; 
				if(aplicacionCuenta006 ==  2) {
				bloqCodigoCCPET = false; 
				}
				else {
					codigoCCPET = "";
					bloqCodigoCCPET = true; 
				}
//					if(aplicacionCuenta ==  0) { //bloqueado
//						bloqCodigoCCPETRega =  true; 
//					}
//					else if(aplicacionCuenta ==  1){//bloqueado
//						bloqCodigoCCPETRega =  true; 
//					}
//					else {
//						bloqCodigoCCPETRega =  true; 
//					}
				}
			
			
		} catch (SystemException e) {
			e.printStackTrace();
		}
		registro.getCampos().put("SECTOR",sector);
		registro.getCampos().put("PROGRAMA",programa);
		registro.getCampos().put("SUBPROGRAMA",supPrograma);
		registro.getCampos().put("COD_PROD_CUIPO",codigoProducto);
		registro.getCampos().put("CODIGO_BPIN",codigoBPIN);
		registro.getCampos().put("CODIGO_CCPET",codigoCCPET);
		registro.getCampos().put("CODIGO_CPC",codigoCPCDANE);
		registro.getCampos().put("CODIGOUNIDADEJE",codigoUnidEje);
		registro.getCampos().put("FUENTE_CUIPO",codigoFuente);
		registro.getCampos().put("CODIGOCCPETREGA",codigoCCPETRega);
		registro.getCampos().put("POLITICA_PUBLICA",politicaPublica);
		registro.getCampos().put("DETALLE_SECTORIAL",detalleSectorial);
		
		cargarListaSector();
                cargarListaPrograma();
                cargarListaSupProgramaE();
                cargarListaSupPrograma();
                cargarListaCodigoProducto();
//              cargarListaCodigoBPIN();
                cargarListaCodigoCCPET();
                cargarListaCodigoCPCDANE();
                cargarListaCodigoUnidEje();
                cargarListaCodigoFuente();
                cargarListaCodigoFuenteE();
                cargarListaCodigoCCPETRega();
                cargarListaPoliticaPublica();
                cargarListaDetalleSectorial();
                
        		actualizarCamposSiNulos(registro,"0",2);

	}


	public void seleccionarFilaCodigoCuentaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get(codigoCons));

	}



	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSector(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("PROGRAMA", "");
		registro.getCampos().put("COD_PROD_CUIPO", "");
		sector =  extraerString( registroAux.getCampos().get("CODIGO"));
		cargarListaPrograma();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		sector =  auxiliar;
		registro.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
                registro.getCampos().put("PROGRAMA", "");
                registro.getCampos().put("COD_PROD_CUIPO", "");
		cargarListaProgramaE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPrograma(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		programa =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("PROGRAMA", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("COD_PROD_CUIPO", "");
		cargarListaCodigoProducto();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		programa =  auxiliar;
		cargarListaCodigoProductoE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSupPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSupPrograma(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SUBPROGRAMA", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSupPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSupProgramaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProducto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarCodigo = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("COD_PROD_CUIPO", registroAux.getCampos().get("CODIGO"));
		
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProductoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("COD_PROD_CUIPO", registroAux.getCampos().get("CODIGO"));
				
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoBPIN
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoBPIN(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_BPIN", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoBPIN
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoBPINE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCCPET
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPET(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoCCPET =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGO_CCPET", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("CODIGO_CPC", "");
		cargarListaCodigoCPCDANE();
		
		
	}
	/**
	 * Evento que se activa al seleccionar el combo Cdigo CCPET del formulario
	 *
	 * @param event
	 */
	public void seleccionarFilaCodigoCCPETE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		codigoCCPET =  auxiliar;
		registro.getCampos().put("DETALLE_SECTORIAL", "");
		cargarListaCodigoCPCDANEE();
		
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCDANE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CPC", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCDANEE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoUnidEje(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoUnidEje =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
                registro.getCampos().put("CODIGOUNIDADEJE", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("DETALLE_SECTORIAL", "");
		cargarListaDetalleSectorial();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoUnidEjeE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		codigoUnidEje = auxiliar;
		registro.getCampos().put("DETALLE_SECTORIAL", "");
		cargarListaDetalleSectorialE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE_CUIPO", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETRega(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoCCPETRega =  SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "").toString();
		registro.getCampos().put("CODIGOCCPETREGA", registroAux.getCampos().get("CODIGO"));
		registro.getCampos().put("CODIGO_CPC", "");
		cargarListaCodigoCPCDANE();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETRegaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString( registroAux.getCampos().get("CODIGO"));
		codigoCCPETRega =  auxiliar;
		cargarListaCodigoCPCDANEE();
	}

	public void onRowSelectTipoDoc(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("TIPO_DOCUMENTO",
				registroAux.getCampos().get(codigoCons));
	}

	public void onRowSelectTipoDocE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get(codigoCons));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublica(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("POLITICA_PUBLICA", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublicaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDetalleSectorial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DETALLE_SECTORIAL", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDetalleSectorial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorialE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  extraerString(registroAux.getCampos().get("CODIGO"));
	}
	public void oprimirVolver897() {
		// <CODIGO_DESARROLLADO>
		if (!"".equals(SysmanFunciones.nvl(registro.getCampos().get(cuentaCons),
				""))) {
			String[] campos = { "ano", "mes", tipoComprobanteCons,
					numeroComprobanteCons, nombreComprobanteCons,
					claseComprobanteCons,
					"claseAfectar", "columna", "codigoCuenta",
					"manejaCentroCosto", "manejaTercero",
					"manejaAuxiliar",
					"manejaFuente", "manejaReferencia",
					"terceroComprobante", "sucursalComprobante",
			"nomTerComprobante" };
			String[] valores = { ano, mes, tipoComprobante, numeroComprobante,
					nombreComprobante,
					claseComprobante, claseAfectar, columna,
					registro.getCampos().get(cuentaCons)
					.toString(),
					manejaCentroCosto.toString(),
					manejaTercero.toString(),
					manejaAuxiliar.toString(),
					manejaFuente.toString(),
					manejaReferencia.toString(),
					terceroComprobante, sucursalComprobante,
					nomTerComprobante };
			SessionUtil.cargarModalDatosFlashCerrar(String.valueOf(
					GeneralCodigoFormaEnum.OBLIGA_PEDIR_DATOS_CONTROLADOR
					.getCodigo()),
					modulo, campos, valores);
		}
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioVolver897() {
		// <CODIGO_DESARROLLADO> 
		Map<String, Object> parametros897 = SessionUtil.getFlash();
		registro.getCampos()
		.put(auxiliarCons, SysmanFunciones.nvl(
				parametros897.get("auxiliar"),
				registro.getCampos()
				.get(auxiliarCons)));
		registro.getCampos().put(fuenteRecursoCons,
				SysmanFunciones.nvl(parametros897.get("fuente"),
						registro.getCampos().get(
								fuenteRecursoCons)));
		registro.getCampos().put(referenciaCons,
				SysmanFunciones.nvl(parametros897.get("referencia"),
						registro.getCampos()
						.get(referenciaCons)));
		registro.getCampos()
		.put(terceroCons, SysmanFunciones.nvl(
				parametros897.get("tercero"),
				registro.getCampos().get(terceroCons)));
		registro.getCampos()
		.put(sucursalCons, SysmanFunciones.nvl(
				parametros897.get("sucursal"),
				registro.getCampos()
				.get(sucursalCons)));
		registro.getCampos().put(centroCostoCons,
				SysmanFunciones.nvl(parametros897.get("centroCosto"),
						registro.getCampos()
						.get(centroCostoCons)));
		String parametro = getParametro(
				"HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO",
				"NO");
		
		codigoFuente =  extraerString(parametros897.get("fuente"));
		
		if(parametro.equals("SI")) {					
			registro.getCampos().put("FUENTE_CUIPO",
					SysmanFunciones.nvl(parametros897.get("codigoFuente"),
							codigoFuente));
			JsfUtil.ejecutarJavaScript("document.getElementById(\"FRFR903:CB7961\").value=\""+registro.getCampos().get("FUENTE_CUIPO").toString()+"\"");
				
		}		
		
		SessionUtil.cleanFlash();
		double valorCredito = SysmanFunciones
				.nvlDbl(registro.getCampos().get(valorCreditoCons), 0);
		if ("DIS".equalsIgnoreCase(claseComprobante)
				|| "RES".equalsIgnoreCase(claseComprobante)
				|| "REO".equalsIgnoreCase(claseComprobante)
				|| "EGR".equalsIgnoreCase(claseComprobante)) {
			if (Double.doubleToRawLongBits(valorCredito) != 0) {
				bloqValorCredito = false;
			}
			else {
				bloqValorCredito = true;
			}
		}
		cargarListaCmpteAfectado();
		// </CODIGO_DESARROLLADO>
	}

	private void redireccionarTipoComprobatenRes(int rowNum, String[] campos) {
		if ("RES".equals(claseComprobante) || "DMR".equals(claseComprobante)
				|| "ADR".equals(claseComprobante)) {
			String[] valores = { ano, tipoComprobante, nombreComprobante,
					numeroComprobante,
					listaInicial.getDatasource().get(rowNum)
					.getCampos().get(cuentaCons)
					.toString(),
					listaInicial.getDatasource().get(rowNum)
					.getCampos()
					.get(consecutivoCons)
					.toString(),
					"0".equals(listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorCreditoCons)
							.toString())
					? listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorDebitoCons)
							.toString()
							: listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorCreditoCons)
							.toString(),
							"P", claseComprobante };
			SessionUtil.cargarModalDatosFlashCerrar(
					Integer.toString(
							GeneralCodigoFormaEnum.SUBPACPROGCOMPEJECS_CONTROLADOR
							.getCodigo()),
					modulo, campos,
					valores);
		}

	}

	private void redireccionarTipoComprobatenReo(int rowNum, String[] campos) {
		if ("REO".equals(claseComprobante)
				|| "DRO".equals(claseComprobante)
				|| "ARO".equals(claseComprobante)) {
			String[] valores = { ano, tipoComprobante, nombreComprobante,
					numeroComprobante,
					listaInicial.getDatasource().get(rowNum)
					.getCampos().get(cuentaCons)
					.toString(),
					listaInicial.getDatasource().get(rowNum)
					.getCampos()
					.get(consecutivoCons)
					.toString(),
					"0".equals(listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorCreditoCons)
							.toString())
					? listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorDebitoCons)
							.toString()
							: listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorCreditoCons)
							.toString(),
							"C", claseComprobante };
			SessionUtil.cargarModalDatosFlashCerrar(Integer.toString(
					GeneralCodigoFormaEnum.SUBPACPROGCOMPEJECS_CONTROLADOR
					.getCodigo()),
					modulo, campos,
					valores);
		}
	}

	public void oprimirVolver902() {
		// <CODIGO_DESARROLLADO>
		int rowNum = indice;
		if ((listaInicial.getDatasource().get(rowNum).getCampos()
				.get(cuentaCons) == null)
				|| !((boolean) listaInicial.getDatasource().get(rowNum).getCampos()
						.get("PROGRAMARPAC"))) {
			return;
		}
		String[] campos = { "ano", "tipoComprobante", "nombreComprobante",
				"numeroComprobante", "cuenta", "consecutivo",
				"valor",
				"subPac", "claseComprobante" };
		redireccionarTipoComprobatenRes(rowNum, campos);
		// Esta parte se adiciono el 18-11-2004 para manejo de PAC
		// Comprometido. Pilar Moreno
		redireccionarTipoComprobatenReo(rowNum, campos);
		if ("EGR".equals(claseComprobante)
				|| "DEG".equals(claseComprobante)
				|| "AEG".equals(claseComprobante)) {
			String[] valores = { ano, tipoComprobante, nombreComprobante,
					numeroComprobante,
					listaInicial.getDatasource().get(rowNum)
					.getCampos().get(cuentaCons)
					.toString(),
					listaInicial.getDatasource().get(rowNum)
					.getCampos()
					.get(consecutivoCons)
					.toString(),
					"0".equals(listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorCreditoCons)
							.toString())
					? listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorDebitoCons)
							.toString()
							: listaInicial.getDatasource()
							.get(rowNum)
							.getCampos()
							.get(valorCreditoCons)
							.toString(),
							"E", claseComprobante };
			SessionUtil.cargarModalDatosFlash(Integer.toString(
					GeneralCodigoFormaEnum.SUBPACPROGCOMPEJECS_CONTROLADOR
					.getCodigo()),
					modulo, campos, valores);
		}
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Contabilizar en la vista
	 *
	 *
	 */
	public void oprimirContabilizar() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>

		try {
			ejbPresupuesto.contabilizarComprobantePptal(compania,
					Integer.parseInt(ano), tipoComprobante,
					new BigInteger(numeroComprobante),
					SessionUtil.getUser().getCodigo());

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO"));

		}
		catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EliminarDetalleLote en la vista
	 *
	 */
	public void oprimirEliminarDetalleLote() {
		String[] campos = { "comprobante", "tipo", "anio" };
		Object[] valores = { numeroComprobante, tipoComprobante, ano };

		SessionUtil.cargarModalDatosFlash(
				Integer.toString(
						GeneralCodigoFormaEnum.ELIMINAR_DETALLE_PPTAL_LOTE_CONTROLADOR
						.getCodigo()),
				modulo, campos,
				valores);
	}

	public void retornarFormularioVolver902() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public void retornarFormularioObliga() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>

	}

	/**
	 * Metodo ejecutado al cambiar el control EliminarDetalleLote
	 * 
	 */
	public void retornarFormularioEliminarDetalleLote(SelectEvent event) {
		inicializar();
	}

	/**
	 * Evento que se activa al seleccionar el combo Cmp. Afect. del formulario
	 * de Nuevo Registro.
	 *
	 * @param event
	 */
	public void seleccionarFilaCmpteAfectado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if ((registroAux != null) && !registroAux.getCampos().isEmpty()) {
			registro.getCampos().put(cmptAfectadoCons,
					registroAux.getCampos().get(comprobanteCons));
			registro.getCampos().put("ANO_AFECT", ano);
			registro.getCampos().put("CONSECUTIVOPPTO",
					registroAux.getCampos().get(consecutivoCons));
			String naturaleza = extraerString(
					registroAux.getCampos().get(naturalezaCons));
			BigDecimal valor = new BigDecimal(
					extraerString(registroAux.getCampos()
							.get("VALOR")));

			if ("D".equals(naturaleza) && (claseComprobante.equals("DMD")
					|| claseComprobante.equals("DMR")
					|| claseComprobante.equals("DRO")
					|| claseComprobante.equals("DEG"))) {
				registro.getCampos().put(valorCreditoCons, valor);
			}
			else if ("D".equals(naturaleza)) {
				registro.getCampos().put(valorDebitoCons, valor);
			}
			else {
				registro.getCampos().put(valorCreditoCons, valor);
			}
		}
	}

	/**
	 * Evento que se activa al seleccionar el combo Cmp. Afect. del formulario
	 * continuo.
	 *
	 * @param event
	 */
	public void seleccionarFilaCmpteAfectadoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get(comprobanteCons));
		registro.getCampos().put("ANO_AFECT", ano);
		registro.getCampos().put("CONSECUTIVOPPTO",
				registroAux.getCampos().get(consecutivoCons));
	}

	// </METODOS_COMBOS_GRANDES>

	/**
	 * Validaciones realizadas antes de cerrar el formulario.
	 */
	public void ejecutarrcCerrar() {
		if (!diferenciaValida()) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2721"));
			return;
		}

		Direccionador direccionador = new Direccionador();
		if (ingreso) {
			parametrosEntrada.put("rid", ridContable);
			parametrosEntrada.put("ridPptal", ridPptal);
			parametrosEntrada.put("tipoPptal", tipoPptal);
			parametrosEntrada.put("tipoComprobante", tipoContable);
			parametrosEntrada.put("accion", validarAccion);
			parametrosEntrada.put("parametroswf",parametroswf);
		}
		direccionador.setParametros(parametrosEntrada);
		direccionador.setNumForm(Integer
				.toString(GeneralCodigoFormaEnum.COMPROBANTEPPTALS_CONTROLADOR
						.getCodigo()));
		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

	}

	/**
	 * Valida que el total de creditos sea igual al de contracreditos para las
	 * cuentas que tengan configurado el tipo de vigencia: "Vigencia Actual".
	 * Para clases de tipo Reduccion, Adicion o Traslado.
	 *
	 * @return Verdadero si la diferencia entre creditos y contracredito es cero
	 * o no aplica la validacion.
	 */
	private boolean diferenciaValida() {
		boolean valida = false;
		try {
			BigInteger comprobante = new BigInteger(numeroComprobante);
			valida = ejbPresupuestoDos.diferenciaValidaVA(compania,
					Integer.parseInt(ano), tipoComprobante, comprobante,
					claseComprobante);
		}
		catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return valida;
	}

	public void calcularPieDeTabla() {
		Map<String, Object> param = new TreeMap<>();
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM0.getValue(),
				compania);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM1.getValue(),
				ano);
		param.put(SubdetallecomprobantepptalsControladorEnum.PARAM3.getValue(),
				tipoComprobante);
		param.put(GeneralParameterEnum.COMPROBANTE.getName(),
				numeroComprobante);
		Registro regAux = null;
		try {
			regAux = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantepptalsControladorUrlEnum.URL11399
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
		if (regAux == null) {
			DecimalFormat df = new DecimalFormat("#,##0.00", dfs);
			sumaDebitos = df.format(debitoComprobante);
			sumaCreditos = df.format(creditoComprobante);
			sumaDiferencia = df.format(debitoComprobante - creditoComprobante);
			return;
		}
		debitoComprobante = Double.valueOf(
				regAux.getCampos().get("SUMA_DEBITO").toString());
		creditoComprobante = Double.valueOf(
				regAux.getCampos().get("SUMA_CREDITO").toString());
		parametrosEntrada.put("debitoComprobante", debitoComprobante);
		parametrosEntrada.put("creditoComprobante", creditoComprobante);
		DecimalFormat df = new DecimalFormat("#,##0.00", dfs);
		sumaDebitos = df.format(debitoComprobante);
		sumaCreditos = df.format(creditoComprobante);
		sumaDiferencia = df.format(debitoComprobante - creditoComprobante);

	}

	private void validarDebitoCredito() {
		if (!"0".equals(SysmanFunciones
				.nvl(registro.getCampos()
						.get(valorCreditoCons), "0")
				.toString())) {
			bloqValorDebito = true;
			bloqValorCredito = false;
		}
		else
			if (!"0".equals(SysmanFunciones
					.nvl(registro.getCampos()
							.get(valorDebitoCons), "0")
					.toString())) {
				bloqValorCredito = true;
				bloqValorDebito = false;
			}
			else {
				bloqValorCredito = false;
				bloqValorDebito = false;
			}
	}

	private void logicaBloqueoCreditoDebito() {
		if ("D".equals(SysmanFunciones
				.nvl(registro.getCampos()
						.get(naturalezaCons),
						"D"))) {
			bloqValorCredito = true;
			bloqValorDebito = false;
		}
		else {
			bloqValorCredito = false;
			bloqValorDebito = true;
		}
	}

	private void bloquearClaseComprobante() {
		if ("D".equals(columna)) {
			bloqValorCredito = true;
		}
		else if ("C".equals(columna)) {
			bloqValorDebito = true;
		}
		else {
			if ("ADC".equals(claseComprobante)) {
				logicaBloqueoCreditoDebito();
			}
			else if ("RED".equals(claseComprobante)) {
				if ("C".equals(SysmanFunciones
						.nvl(registro.getCampos()
								.get(naturalezaCons),
								"D"))) {
					bloqValorCredito = true;
					bloqValorDebito = false;
				}
				else {
					bloqValorCredito = false;
					bloqValorDebito = true;
				}
			}
			else {
				validarDebitoCredito();
			}
		}
	}

	/**
	 * currentNuevo
	 */
	private void activarRegistroNuevo() {
		bloqValorDebito = false;
		bloqValorCredito = false;
		if ("REC".equals(claseComprobante)) {
			if (!"0".equals(SysmanFunciones
					.nvl(registro.getCampos().get(valorCreditoCons),
							"0")
					.toString())) {
				bloqValorDebito = true;
				bloqValorCredito = false;
			}
			else
				if (!"0".equals(SysmanFunciones
						.nvl(registro.getCampos().get(valorDebitoCons),
								"0")
						.toString())) {
					bloqValorCredito = true;
					bloqValorDebito = false;
				}
				else {
					bloqValorCredito = false;
					bloqValorDebito = false;
				}
		}

		else if ("RIN".equals(tipoComprobante)) {
			bloqValorCredito = false;
			bloqValorDebito = false;
		}

		else {
			bloquearClaseComprobante();
		}

		double valorCredito = SysmanFunciones
				.nvlDbl(registro.getCampos().get(valorCreditoCons), 0);
		if ("DIS".equalsIgnoreCase(claseComprobante)
				|| "RES".equalsIgnoreCase(claseComprobante)
				|| "REO".equalsIgnoreCase(claseComprobante)
				|| "EGR".equalsIgnoreCase(claseComprobante)) {
			if (Double.doubleToRawLongBits(valorCredito) != 0) {
				bloqValorCredito = false;
			}
			else {
				bloqValorCredito = true;
			}
		}
	}

	private void validarBloqueos() {
		if (!"0".equals(SysmanFunciones
				.nvl(listaInicial.getDatasource().get(indice)
						.getCampos().get(valorCreditoCons),
						"0")
				.toString())) {
			bloqValorDebitoCont = true;
			bloqValorCreditoCont = false;
		}
		else
			if (!"0".equals(
					SysmanFunciones.nvl(listaInicial.getDatasource()
							.get(indice).getCampos()
							.get(valorDebitoCons), "0")
					.toString())) {
				bloqValorCreditoCont = true;
				bloqValorDebitoCont = false;
			}
			else {
				bloqValorCreditoCont = false;
				bloqValorDebitoCont = false;
			}
	}

	private void validarBloquesdebitoCreditoCont() {
		if (!"0".equals(SysmanFunciones
				.nvl(listaInicial.getDatasource()
						.get(indice).getCampos()
						.get(valorCreditoCons), "0")
				.toString())) {
			bloqValorDebitoCont = true;
			bloqValorCreditoCont = false;
		}
		else
			if (!"0".equals(SysmanFunciones
					.nvl(listaInicial.getDatasource()
							.get(indice).getCampos()
							.get(valorDebitoCons), "0")
					.toString())) {
				bloqValorCreditoCont = true;
				bloqValorDebitoCont = false;
			}
			else {
				bloqValorCreditoCont = false;
				bloqValorDebitoCont = false;
			}
	}

	private void validarBloqueosNaturalezaC() {
		if ("C".equals(SysmanFunciones
				.nvl(listaInicial.getDatasource()
						.get(indice).getCampos()
						.get(naturalezaCons),
						"D"))) {
			bloqValorCreditoCont = true;
			bloqValorDebitoCont = false;
		}
		else {
			bloqValorCreditoCont = false;
			bloqValorDebitoCont = true;
		}
	}

	private void validarBloqueosNaturalezaD() {
		if ("D".equals(SysmanFunciones
				.nvl(listaInicial.getDatasource()
						.get(indice).getCampos()
						.get(naturalezaCons),
						"D"))) {
			bloqValorCreditoCont = true;
			bloqValorDebitoCont = false;
		}
		else {
			bloqValorCreditoCont = false;
			bloqValorDebitoCont = true;
		}
	}

	private void bloqueValorCreditoCont() {
		if ("D".equals(columna)) {
			bloqValorCreditoCont = true;
		}
		else if ("C".equals(columna)) {
			bloqValorDebitoCont = true;
		}
		else {
			if ("ADC".equals(claseComprobante)) {
				validarBloqueosNaturalezaD();

			}
			else if ("RED".equals(claseComprobante)) {
				validarBloqueosNaturalezaC();
			}
			else {
				validarBloquesdebitoCreditoCont();
			}
		}
	}

	private void activarRegistro(Registro registro) {

		bloqValorDebitoCont = false;
		bloqValorCreditoCont = false;
		if ("REC".equals(claseComprobante)) {
			validarBloqueos();
		}
		else {
			bloqueValorCreditoCont();

		}

		double valorCredito = SysmanFunciones
				.nvlDbl(registro.getCampos().get(valorCreditoCons), 0);
		double valorDebito = SysmanFunciones
				.nvlDbl(registro.getCampos().get(valorDebitoCons), 0);
		if ("DIS".equalsIgnoreCase(claseComprobante)
				|| "RES".equalsIgnoreCase(claseComprobante)
				|| "REO".equalsIgnoreCase(claseComprobante)
				|| "EGR".equalsIgnoreCase(claseComprobante)) {
			if (Double.doubleToRawLongBits(valorCredito) != 0) {
				bloqValorCreditoCont = false;
			}
			else {
				bloqValorCreditoCont = true;
			}
		}
		else if ("RIN".equals(tipoComprobante)) {
			bloqValorCreditoCont = false;
			bloqValorDebitoCont = false;
		}
		else
			if ("ING".equalsIgnoreCase(claseComprobante)
					&& Double.doubleToRawLongBits(valorDebito) != 0) {
				bloqValorDebitoCont = false;
			}
		
		sector = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("SECTOR"),
                                "").toString();
                
		programa = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("PROGRAMA"),
                                "").toString();
                
		codigoCCPET = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("CODIGO_CCPET"),
                                "").toString();
		
		codigoUnidEje = SysmanFunciones.nvl(listaInicial.getDatasource().get(indice%10).getCampos().get("CODIGOUNIDADEJE"),
                                "").toString();
		
                
		cargarListaProgramaE();       
		cargarListaCodigoProductoE();
		cargarListaCodigoCPCDANEE();
		cargarListaDetalleSectorialE();	
						
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		if ("MOP".equals(claseComprobante)) {
			bloqFecha = false;

		}
		if (!"MOP".equals(claseComprobante)){ 
			// Activar campo fecha

			bloqFecha = true;

		}
		
		open();

		// </CODIGO_DESARROLLADO>
	}

	public void open() {
		if (validarAccion.equals(ACCION_VER)) {
			mostrarAcme = false;
		}
		else {

			if (impreso || "C".equals(estadoPeriodo)) {
				mostrarAcme = false;
			}
			else {
				mostrarAcme = true;
			}
			if (visiblePapeles) {
				visiblePapeles = true;
				tituloPC = "Papeles";
				papeles = "PAPELES";
			}
			else {
				visiblePapeles = false;
				tituloPC = "Contractual";
				papeles = "CONTRACTUAL";
			}
			if ("RES".equals(claseComprobante) || "REO".equals(claseComprobante)
					|| "EGR".equals(claseComprobante)) {
				visibleConSituacionFondos = true;
				if (!"RES".equals(claseComprobante)) {
					bloqConSituacionFondos = true;
				}
			}
		}
		return;
	}

	public void calcularSaldoNeto(Registro registro) {
		Map<String, Object> detalle = registro.getCampos();
		int anio = (int) detalle.get(GeneralParameterEnum.ANO.getName());
		String tipo = extraerString(detalle
				.get(GeneralParameterEnum.TIPO_CPTE.getName()));
		BigInteger comprobante = new BigInteger(extraerString(detalle
				.get(GeneralParameterEnum.COMPROBANTE.getName())));
		int consecutivo = (int) detalle
				.get(GeneralParameterEnum.CONSECUTIVO.getName());
		String cuenta = extraerString(detalle
				.get(GeneralParameterEnum.CUENTA.getName()));
		BigDecimal debito = new BigDecimal(
				extraerString(detalle.get(valorDebitoCons)));
		BigDecimal credito = new BigDecimal(
				extraerString(detalle.get(valorCreditoCons)));
		try {
			BigDecimal saldoNeto = ejbPresupuestoTres.calcularSaldoNeto(
					compania, anio, tipo, comprobante, consecutivo,
					cuenta, debito, credito);
			listaInicial.getDatasource().get(indice % 10).getCampos()
			.put("SALDONETO", saldoNeto);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}

	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		String claseClas = "";
		String codigo = "";
		if(!bloqCodigoCCPET) {
			claseClas= "006";
			codigo = registro.getCampos().get("CODIGO_CCPET").toString();
		}else if (!bloqCodigoCCPETRega) {
			claseClas = "010";
			codigo = registro.getCampos().get("CODIGOCCPETREGA").toString();
		}
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.CLASECLASIFICADOR.getName(), claseClas); 
		param.put(GeneralParameterEnum.CODIGOCCPET.getName(), codigo);

		//registro.getCampos().put("FUENTE_CUIPO", codigoFuente);

		Registro cpcobliga;
		try {
			cpcobliga = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantepptalsControladorUrlEnum.URL1884075
									.getValue())
							.getUrl(), param));

			if(cpcobliga != null) {
				boolean obliga = (boolean) cpcobliga.getCampos().get("OBLIGACPCDANE");

				if(!bloqCodigoCCPET)
				{
					if ( (SysmanFunciones.validarVariableVacio(SysmanFunciones.toString(registro.getCampos().get("CODIGO_CCPET"))) || SysmanFunciones.validarVariableVacio(SysmanFunciones.toString(registro.getCampos().get("CODIGO_CPC")))) && obliga) {
						JsfUtil.agregarMensajeInformativo(
								idioma.getString("TB_TB4482"));
						return false;

					}
				}else if(!bloqCodigoCCPETRega){
						if ( (SysmanFunciones.validarVariableVacio(SysmanFunciones.toString(registro.getCampos().get("CODIGOCCPETREGA"))) || SysmanFunciones.validarVariableVacio(SysmanFunciones.toString(registro.getCampos().get("CODIGO_CPC")))) && obliga) {
							JsfUtil.agregarMensajeInformativo(
									idioma.getString("TB_TB4483"));
							return false;

						}
					}
				
			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		registro.getCampos().put("COMPANIA", compania);
		registro.getCampos().put("ANO", ano);
		registro.getCampos().put("TIPO_CPTE", tipoComprobante);
		registro.getCampos().put(comprobanteCons, numeroComprobante);

		//registro.getCampos().put("FUENTE_CUIPO", codigoFuente);

		// Genera consecutivo
		try {
			String tabla = "DETALLE_COMPROBANTE_PPTAL";
			String criterio = "COMPANIA = ''" + compania + "'' "
					+ " AND ANO = " + ano
					+ " AND TIPO_CPTE = ''" + tipoComprobante + "''"
					+ " AND COMPROBANTE = ''" + numeroComprobante + "''";
			Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
					tabla, criterio, consecutivoCons, "1");
			registro.getCampos().put(consecutivoCons, consecutivo);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		actualizarCamposSiNulos(registro,"0",2);
		return true;
	}

	@Override
	public boolean insertarDespues() {
		actualizarCamposSiNulos(registro,"0",1);
		return true;
	}

	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>

		if (!validarValorAplazamiento() || !validarCpteAfectado()
				|| !validarValorFinanciera()) {
			return false;
		}
		
		if (registro.getCampos().get("CMPTE_SOLICI_AFECTADO") != null &&
			registro.getCampos().get("TIPOT") != null &&
		    registro.getCampos().get("CLASET") != null &&
		    registro.getCampos().get("DEPENDENCIA") != null &&
		    registro.getCampos().get("CONSECUTIVOPPTO") != null &&
		    claseComprobante.equals("DMD")) 
		{
			try {
				Map<String, Object> parametros = new HashMap<>(); 
				parametros.put("VALORDISMINUIDO", registro.getCampos().get("VALOR_CREDITO"));
				parametros.put("COMPANIA", compania);
				parametros.put("TIPOT", registro.getCampos().get("TIPOT"));
				parametros.put("CLASET", registro.getCampos().get("CLASET"));
				parametros.put("NOVEDAD", registro.getCampos().get("CMPTE_SOLICI_AFECTADO"));
				parametros.put("DEPENDENCIA", registro.getCampos().get("DEPENDENCIA"));
				parametros.put("CODIGO", registro.getCampos().get("CONSECUTIVOPPTO"));
				Parameter parameter = new Parameter();
	
				parameter.setFields(parametros);
				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(SubdetallecomprobantepptalsControladorUrlEnum.URL131036.getValue());
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			} catch (SystemException e) {
				e.printStackTrace();
			}
		}

		registro.getCampos().remove("CMPTE_SOLICI_AFECTADO");
		registro.getCampos().remove("TIPOT");
		registro.getCampos().remove("CLASET");
		registro.getCampos().put("TIPOCONTRATO", tipoContrato);
		registro.getCampos().put(numeroContratoCons, numeroContrato);
		registro.getCampos().put(auxiliarCons,
				registro.getCampos().get(auxiliarCons));
		registro.getCampos().put(terceroCons,
				registro.getCampos().get(terceroCons));
		registro.getCampos().put(sucursalCons,
				registro.getCampos().get(sucursalCons));
		registro.getCampos().put(centroCostoCons,
				registro.getCampos().get(centroCostoCons));
		registro.getCampos().put(fuenteRecursoCons,
				registro.getCampos().get(fuenteRecursoCons));
		registro.getCampos().put(referenciaCons,
				registro.getCampos().get(referenciaCons));
		registro.getCampos().put(valorDebitoCons,
				registro.getCampos().get(valorDebitoCons));
		registro.getCampos().put(valorCreditoCons,
				registro.getCampos().get(valorCreditoCons));
		registro.getCampos().put(debitoAfectadoCons,
				registro.getCampos().get(debitoAfectadoCons));
		registro.getCampos().put(creditoAfectadoCons,
				registro.getCampos().get(creditoAfectadoCons));
		registro.getCampos().put("DEBITO_AFECTADOCNT",
				registro.getCampos().get("DEBITO_AFECTADOCNT"));
		registro.getCampos().put("CREDITO_AFECTADOCNT",
				registro.getCampos().get("CREDITO_AFECTADOCNT"));
		registro.getCampos().put(modificacionDebitoCons,
				registro.getCampos().get(modificacionDebitoCons));
		registro.getCampos().put(modificacionCreditoCons,
				registro.getCampos().get(modificacionCreditoCons));
		registro.getCampos().put("SALDO", registro.getCampos().get("SALDO"));
		registro.getCampos().put("VALORCOMPROMISO",
				registro.getCampos().get("VALORCOMPROMISO"));
		registro.getCampos().put("DISPONIBILIDADNETA",
				registro.getCampos().get("DISPONIBILIDADNETA"));
		registro.getCampos().put("DISACUMULADAS",
				registro.getCampos().get("DISACUMULADAS"));
		registro.getCampos().put("RESACUMULADAS",
				registro.getCampos().get("RESACUMULADAS"));
		registro.getCampos().put(aprDefinitivaCons,
				registro.getCampos().get(aprDefinitivaCons));
		registro.getCampos().put("PAC_DISPONIBLE",
				registro.getCampos().get("PAC_DISPONIBLE"));
		registro.getCampos().put("PORCENTAJEDISTRIBUIDO",
				registro.getCampos().get("PORCENTAJEDISTRIBUIDO"));
		registro.getCampos().put("TIPO_DOCUMENTO", tipoDocComprobante);
		registro.getCampos().put("NRO_DOCUMENTO", nroDocComprobante);
		registro.getCampos().put("RECURSO_SGR", registro.getCampos().get("RECURSO_SGR"));
		registro.getCampos().remove("CREATED_BY");
		registro.getCampos().remove("DEPENDENCIA");

		actualizarCamposSiNulos(registro,"0",2);

		// </CODIGO_DESARROLLADO>
		return true;
	}

	private boolean validarValorAplazamiento() {
		if ("APL".equals(claseComprobante)) {

			Map<String, Object> param = new TreeMap<>();

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

			param.put(GeneralParameterEnum.ANO.getName(), ano);

			param.put(GeneralParameterEnum.CODIGO.getName(),
					registro.getCampos().get(cuentaCons));

			//se realizo una implementacion quemada del campo para que valide la totalidad de los montos asi el usuario ingrese en meses intermedios
			param.put(GeneralParameterEnum.MES.getName(), "13"); 
		
			Registro rsApropiacion = null;

			try {
				 rsApropiacion = RegistroConverter.toRegistro(
						requestManager.get(UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										SubdetallecomprobantepptalsControladorUrlEnum.URL4545
										.getValue())
								.getUrl(), param));

				if (rsApropiacion != null) {
					double valorCredito = Double.parseDouble(registro.getCampos()
							.get("VALOR_CREDITO").toString());
					
					double valorDebito = Double.parseDouble(registro.getCampos()
							.get("VALOR_DEBITO").toString());
					
					String naturaleza = getNaturalezaCuenta();
					
					if((valorDebito > 0)&& (valorCredito > 0)) {
						JsfUtil.agregarMensajeInformativo(
								idioma.getString("TB_TB4411"));
						return false;
					}
					
					if("D".equals(naturaleza)) {
						
						if(valorDebito > 0) {
							/* descongelar */
							
							double valorAplazamiento= Math.abs(Double.parseDouble(rsApropiacion
									.getCampos().get(aplazamientoTotal)
									.toString()));
							
							if(valorDebito > valorAplazamiento) {
				
								JsfUtil.agregarMensajeInformativo(
										idioma.getString("TB_TB4328"));
								return false;
							}
							
							
						}else {
							/*congelar*/
							
							double valorApropiacion = Double.parseDouble(rsApropiacion
									.getCampos().get(aprDefinitivaCons)
									.toString());

							if (valorCredito > valorApropiacion) {
								JsfUtil.agregarMensajeInformativo(
										idioma.getString("TB_TB3191"));
								return false;
							}
							
						}
					}
					
					
					
					if("C".equals(naturaleza)) {
						
						if(valorDebito > 0) {
							/*congelar */
							
							double valorApropiacion = Double.parseDouble(rsApropiacion
									.getCampos().get(aprDefinitivaCons)
									.toString());

							if (valorDebito > valorApropiacion) {
								JsfUtil.agregarMensajeInformativo(
										idioma.getString("TB_TB3191"));
								return false;
							}
						
							
							
						}else {
							/*descongelar*/
							
							double valorAplazamiento= Math.abs(Double.parseDouble(rsApropiacion
									.getCampos().get(aplazamientoTotal)
									.toString()));
							
							if(valorCredito > valorAplazamiento) {
								JsfUtil.agregarMensajeInformativo(
										idioma.getString("TB_TB4328"));
								return false;
							}
						
							
						}
					}
					
					
					
					

				
				}
				else {
					JsfUtil.agregarMensajeInformativo(
							idioma.getString("TB_TB3190"));
					return false;
				}

			}
			catch (SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
				return false;
			}

		}

		return true;
	}

	/**
	 * Si el tipo de comprobante posee el indicador de "Obliga Afectacion",
	 * valida que los campos TIPO_CPTE_AFECT y CMPTE_AFECTADO posean valor
	 *
	 * @return Verdadero cuando posee el indicador y los campos han sido
	 * completados en el formulario
	 */
	private boolean validarCpteAfectado() {
		if (obligaAfectacion
				&& SysmanFunciones.validarCampoVacio(registro.getCampos(),
						tipoComprobanteAfecCons)
				&& SysmanFunciones.validarCampoVacio(registro.getCampos(),
						cmptAfectadoCons)) {
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4054"));
			return false;
		}
		return true;
	}

	/**
	 * Valida que el valor de la solicitud no supere el ingresado en la
	 * imputacion presupuestal
	 *
	 * @return Verdadero cuando posee el indicador y los campos han sido
	 * completados en el formulario
	 */
	private boolean validarValorFinanciera() {

		Map<String, Object> paramDisminuido = new HashMap<>();
		paramDisminuido.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		paramDisminuido.put(GeneralParameterEnum.CLASE.getName(),
				registro.getCampos().get("CLASET") == null
				? registro.getCampos().get("CLASET")
						: registro.getCampos().get("CLASET").toString());
		paramDisminuido.put(GeneralParameterEnum.TIPO.getName(),
				registro.getCampos().get("TIPOT"));
		paramDisminuido.put(GeneralParameterEnum.CODIGO.getName(),
				registro.getCampos().get("CMPTE_SOLICI_AFECTADO"));

		Registro rsValorTotal;
		try {
			rsValorTotal = RegistroConverter.toRegistro(
					requestManager.get(UrlServiceUtil
							.getInstance()
							.getUrlServiceByUrlByEnumID(
									SubdetallecomprobantepptalsControladorUrlEnum.URL29289
									.getValue())
							.getUrl(),
							paramDisminuido));

			if (rsValorTotal != null) {

				valorFinanciera = Double.valueOf(SysmanFunciones.nvl(
						rsValorTotal
						.getCampos()
						.get("VALORTOTAL"),
						"0").toString());

				valorDebito = Double.valueOf(registro.getCampos()
						.get("VALOR_DEBITO").toString());

				/*
                DecimalFormat format = new DecimalFormat("#0.##");

                long valorDeb = Long.parseLong(
                        format.format(valorDebito).replaceAll(",", ""));
                long valorFinanc = Long.parseLong(format.format(valorFinanciera)
                        .replaceAll(",", ""));
                        if(valorDeb > valorFinanc) { 
				 */

				if (valorFinanciera.compareTo(valorDebito) < 0) {
					JsfUtil.agregarMensajeAlerta(
							idioma.getString("TB_TB4328"));
					return false;
				}


			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return true;

	}

	@Override
	public boolean actualizarDespues() {
		// <CODIGO_DESARROLLADO>
		calcularPieDeTabla();
		registro.getCampos().put(fechaCons, fechaComprobante);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>
		UrlBean updateTercero = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						SubdetallecomprobantepptalsControladorUrlEnum.URL1861
						.getValue());
		Map<String, Object> parametros = new TreeMap<>();

		try {
			parametros.put(GeneralParameterEnum.COMPANIA.getName(),
					compania);
			parametros.put(GeneralParameterEnum.ANO.getName(),
					registro.getCampos().get(GeneralParameterEnum.ANO
							.getName()));

			parametros.put(GeneralParameterEnum.TIPO.getName(),
					registro.getCampos().get("TIPO_CPTE"));

			parametros.put(GeneralParameterEnum.COMPROBANTE.getName(),
					registro.getCampos().get(
							GeneralParameterEnum.COMPROBANTE
							.getName()));

			parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
					registro.getCampos().get(
							GeneralParameterEnum.CONSECUTIVO
							.getName()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			requestManager.update(updateTercero.getUrl(),
					updateTercero.getMetodo(),
					parameter);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		try {
	    	
	    	Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos().get(GeneralParameterEnum.ANO.getName()));
	        param.put(GeneralParameterEnum.COMPROBANTE.getName(), registro.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()));
	        param.put(GeneralParameterEnum.CONSECUTIVO.getName(), registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
	        param.put(GeneralParameterEnum.TIPO_CPTE.getName(), tipoComprobante);
	        
	        Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                                    UrlServiceUtil.getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                    		SubdetallecomprobantepptalsControladorUrlEnum.URL38073.getValue())
                                                    .getUrl(),
                                    param));
	
	        if (rs != null)
	        {
	            String tipo = SysmanFunciones.toString(rs.getCampos().get("TIPO_CPTE"));
	            String comprobante = SysmanFunciones.toString(rs.getCampos().get("COMPROBANTE"));
	            Date fecha = (Date) rs.getCampos().get("FECHA");
	            String fechaC = SysmanFunciones.convertirAFechaCadena(fecha);
	           
	            JsfUtil.agregarMensajeAlerta("La orden ya se encuentra afectada por el tipo: " 
	            	    + tipo + ", comprobante: " + comprobante + ", fecha: " + fechaC);

	        	
	        	return false;
	        }

		

		} catch (SystemException | ParseException e) {
			Logger.getLogger(SubdetallecomprobantepptalsControlador.class
                    .getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	
		// </CODIGO_DESARROLLADO>
		return true;
	}

	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		if(registro.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString().equals("webservice")) {
	        // se elimina el detalle del comprobante pero de la tabla detalle_cpte_afect_ws
		    	try {
			    	int delete = 0;
			    	
			    	Map<String, Object> param = new TreeMap<>();
			        param.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
			        param.put(GeneralParameterEnum.KEY_ANO.getName(), ano);
			        param.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(), numeroComprobante);
			        param.put(GeneralParameterEnum.KEY_CONSECUTIVO_CPTE.getName(), registro.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()));
			        param.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(), tipoComprobante);
			        //param.put(GeneralParameterEnum.KEY_DESCRIPCION.getName(), registro.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()));
			        
			        UrlBean urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(SubdetallecomprobantepptalsControladorUrlEnum.URL1914002.getValue());
		
				
					delete = requestManager.delete(urlBean.getUrl(), param);
				} catch (SystemException e) {
					Logger.getLogger(SubdetallecomprobantepptalsControlador.class
		                    .getName()).log(Level.SEVERE, null, e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
	    	}
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
	 * pueden remover valores auxiliares que no se desee o se deban enviar en el
	 * registro
	 */
	@Override
	public void removerCombos() {
		registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
		registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
		registro.getCampos().remove(GeneralParameterEnum.TIPO_CPTE.getName());
		registro.getCampos().remove(GeneralParameterEnum.COMPROBANTE.getName());
		registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
		registro.getCampos().remove("NOMBRETERCERO");
		registro.getCampos().remove("NOMBREPPTAL");
		registro.getCampos().remove(GeneralParameterEnum.DESTINO.getName());
		registro.getCampos().remove("SALDONETO");
	}

	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y edicion del
	 * registro se usa cuando se desean agregar valores al registro despues de
	 * dichas acciones
	 */
	@Override
	public void asignarValoresRegistro() {
		actualizarCamposSiNulos(registro,"0",1);
	}

	// <SET_GET_ATRIBUTOS>

	public String getTituloEncabezado() {
		return tituloEncabezado;
	}

	public void setTituloEncabezado(String tituloEncabezado) {
		this.tituloEncabezado = tituloEncabezado;
	}

	public String getDescripcionEncabezado() {
		return descripcionEncabezado;
	}

	public void setDescripcionEncabezado(String descripcionEncabezado) {
		this.descripcionEncabezado = descripcionEncabezado;
	}

	public String getSumaDebitos() {
		return sumaDebitos;
	}

	public void setSumaDebitos(String sumaDebitos) {
		this.sumaDebitos = sumaDebitos;
	}

	public String getSumaCreditos() {
		return sumaCreditos;
	}

	public void setSumaCreditos(String sumaCreditos) {
		this.sumaCreditos = sumaCreditos;
	}

	public String getSumaDiferencia() {
		return sumaDiferencia;
	}

	public void setSumaDiferencia(String sumaDiferencia) {
		this.sumaDiferencia = sumaDiferencia;
	}

	public int getIndice() {
		return indice;
	}

	public void setIndice(int indice) {
		this.indice = indice;
	}

	public String getTituloPC() {
		return tituloPC;
	}

	public void setTituloPC(String tituloPC) {
		this.tituloPC = tituloPC;
	}

	public boolean isBloqValorDebito() {
		return bloqValorDebito;
	}

	public void setBloqValorDebito(boolean bloqValorDebito) {
		this.bloqValorDebito = bloqValorDebito;
	}

	public boolean isBloqValorCredito() {
		return bloqValorCredito;
	}

	public void setBloqValorCredito(boolean bloqValorCredito) {
		this.bloqValorCredito = bloqValorCredito;
	}

	public boolean isBloqContCredito() {
		return bloqContCredito;
	}

	public void setBloqContCredito(boolean bloqContCredito) {
		this.bloqContCredito = bloqContCredito;
	}

	public boolean isBloqContCreditoCont() {
		return bloqContCreditoCont;
	}

	public void setBloqContCreditoCont(boolean bloqContCreditoCont) {
		this.bloqContCreditoCont = bloqContCreditoCont;
	}

	public boolean isBloqValorDebitoCont() {
		return bloqValorDebitoCont;
	}

	public void setBloqValorDebitoCont(boolean bloqValorDebitoCont) {
		this.bloqValorDebitoCont = bloqValorDebitoCont;
	}

	public boolean isBloqValorCreditoCont() {
		return bloqValorCreditoCont;
	}

	public void setBloqValorCreditoCont(boolean bloqValorCreditoCont) {
		this.bloqValorCreditoCont = bloqValorCreditoCont;
	}

	public boolean isBloqFecha() {
		return bloqFecha;
	}

	public void setBloqFecha(boolean bloqFecha) {
		this.bloqFecha = bloqFecha;
	}

	public boolean isVisibleConSituacionFondos() {
		return visibleConSituacionFondos;
	}

	public void setVisibleConSituacionFondos(
			boolean visibleConSituacionFondos) {
		this.visibleConSituacionFondos = visibleConSituacionFondos;
	}

	public boolean isBloqConSituacionFondos() {
		return bloqConSituacionFondos;
	}

	public void setBloqConSituacionFondos(boolean bloqConSituacionFondos) {
		this.bloqConSituacionFondos = bloqConSituacionFondos;
	}

	public boolean isMostrarAcme() {
		return mostrarAcme;
	}

	public void setMostrarAcme(boolean mostrarAcme) {
		this.mostrarAcme = mostrarAcme;
	}

	public String getPapeles() {
		return papeles;
	}

	public void setPapeles(String papeles) {
		this.papeles = papeles;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>

	public List<Registro> getListaTipoCpteAfect() {
		return listaTipoCpteAfect;
	}

	/**
	 * @return the listaReferencia
	 */
	public RegistroDataModelImpl getListaReferencia() {
		return listaReferencia;
	}

	/**
	 * @return the listaReferenciaE
	 */
	public RegistroDataModelImpl getListaReferenciaE() {
		return listaReferenciaE;
	}

	/**
	 * @param listaReferencia
	 * the listaReferencia to set
	 */
	public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
		this.listaReferencia = listaReferencia;
	}

	/**
	 * @param listaReferenciaE
	 * the listaReferenciaE to set
	 */
	public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
		this.listaReferenciaE = listaReferenciaE;
	}

	public void setListaTipoCpteAfect(List<Registro> listaTipoCpteAfect) {
		this.listaTipoCpteAfect = listaTipoCpteAfect;
	}

	public RegistroDataModelImpl getListaTercero() {
		return listaTercero;
	}

	public void setListaTercero(RegistroDataModelImpl listaTercero) {
		this.listaTercero = listaTercero;
	}

	public RegistroDataModelImpl getListaTerceroE() {
		return listaTerceroE;
	}

	public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
		this.listaTerceroE = listaTerceroE;
	}

	public RegistroDataModelImpl getListaFuente() {
		return listaFuente;
	}

	public void setListaFuente(RegistroDataModelImpl listaFuente) {
		this.listaFuente = listaFuente;
	}

	public RegistroDataModelImpl getListaFuenteE() {
		return listaFuenteE;
	}

	public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
		this.listaFuenteE = listaFuenteE;
	}

	public RegistroDataModelImpl getListaAuxiliar() {
		return listaAuxiliar;
	}

	public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}

	public RegistroDataModelImpl getListaAuxiliarE() {
		return listaAuxiliarE;
	}

	public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
		this.listaAuxiliarE = listaAuxiliarE;
	}

	/**
	 * Retorna la lista listaCuentaDebitoEquivalente
	 * 
	 * @return listaCuentaDebitoEquivalente
	 */
	public RegistroDataModelImpl getListaCuentaDebitoEquivalente() {
		return listaCuentaDebitoEquivalente;
	}

	/**
	 * Asigna la lista listaCuentaDebitoEquivalente
	 * 
	 * @param listaCuentaDebitoEquivalente
	 * Variable a asignar en listaCuentaDebitoEquivalente
	 */
	public void setListaCuentaDebitoEquivalente(
			RegistroDataModelImpl listaCuentaDebitoEquivalente) {
		this.listaCuentaDebitoEquivalente = listaCuentaDebitoEquivalente;
	}

	/**
	 * Retorna la lista listaCuentaDebitoEquivalente
	 * 
	 * @return listaCuentaDebitoEquivalente
	 */
	public RegistroDataModelImpl getListaCuentaDebitoEquivalenteE() {
		return listaCuentaDebitoEquivalenteE;
	}

	/**
	 * Asigna la lista listaCuentaDebitoEquivalente
	 * 
	 * @param listaCuentaDebitoEquivalente
	 * Variable a asignar en listaCuentaDebitoEquivalente
	 */
	public void setListaCuentaDebitoEquivalenteE(
			RegistroDataModelImpl listaCuentaDebitoEquivalenteE) {
		this.listaCuentaDebitoEquivalenteE = listaCuentaDebitoEquivalenteE;
	}

	/**
	 * Retorna la lista listaCuentaCreditoEquivalente
	 * 
	 * @return listaCuentaCreditoEquivalente
	 */
	public RegistroDataModelImpl getListaCuentaCreditoEquivalente() {
		return listaCuentaCreditoEquivalente;
	}

	/**
	 * Asigna la lista listaCuentaCreditoEquivalente
	 * 
	 * @param listaCuentaCreditoEquivalente
	 * Variable a asignar en listaCuentaCreditoEquivalente
	 */
	public void setListaCuentaCreditoEquivalente(
			RegistroDataModelImpl listaCuentaCreditoEquivalente) {
		this.listaCuentaCreditoEquivalente = listaCuentaCreditoEquivalente;
	}

	/**
	 * Retorna la lista listaCuentaCreditoEquivalente
	 * 
	 * @return listaCuentaCreditoEquivalente
	 */
	public RegistroDataModelImpl getListaCuentaCreditoEquivalenteE() {
		return listaCuentaCreditoEquivalenteE;
	}

	/**
	 * Asigna la lista listaCuentaCreditoEquivalente
	 * 
	 * @param listaCuentaCreditoEquivalente
	 * Variable a asignar en listaCuentaCreditoEquivalente
	 */
	public void setListaCuentaCreditoEquivalenteE(
			RegistroDataModelImpl listaCuentaCreditoEquivalenteE) {
		this.listaCuentaCreditoEquivalenteE = listaCuentaCreditoEquivalenteE;
	}

	public RegistroDataModelImpl getListaCodigoCuenta() {
		return listaCodigoCuenta;
	}

	public void setListaCodigoCuenta(RegistroDataModelImpl listaCodigoCuenta) {
		this.listaCodigoCuenta = listaCodigoCuenta;
	}

	public RegistroDataModelImpl getListaCodigoCuentaE() {
		return listaCodigoCuentaE;
	}

	public void setListaCodigoCuentaE(
			RegistroDataModelImpl listaCodigoCuentaE) {
		this.listaCodigoCuentaE = listaCodigoCuentaE;
	}

	public RegistroDataModelImpl getListaCmpteAfectado() {
		return listaCmpteAfectado;
	}

	public void setListaCmpteAfectado(
			RegistroDataModelImpl listaCmpteAfectado) {
		this.listaCmpteAfectado = listaCmpteAfectado;
	}

	public RegistroDataModelImpl getListaCmpteAfectadoE() {
		return listaCmpteAfectadoE;
	}

	public void setListaCmpteAfectadoE(
			RegistroDataModelImpl listaCmpteAfectadoE) {
		this.listaCmpteAfectadoE = listaCmpteAfectadoE;
	}

	public RegistroDataModelImpl getListaTipoDoc() {
		return listaTipoDoc;
	}

	public void setListaTipoDoc(RegistroDataModelImpl listaTipoDoc) {
		this.listaTipoDoc = listaTipoDoc;
	}

	public RegistroDataModelImpl getListaTipoDocE() {
		return listaTipoDocE;
	}

	public void setListaTipoDocE(RegistroDataModelImpl listaTipoDocE) {
		this.listaTipoDocE = listaTipoDocE;
	}

	/**
	 * Retorna la lista listaCodigoCPC
	 * 
	 * @return listaCodigoCPC
	 */
	public RegistroDataModelImpl getListaCodigoCPC() {
		return listaCodigoCPC;
	}

	/**
	 * Asigna la lista listaCodigoCPC
	 * 
	 * @param listaCodigoCPC
	 * Variable a asignar en listaCodigoCPC
	 */
	public void setListaCodigoCPC(RegistroDataModelImpl listaCodigoCPC) {
		this.listaCodigoCPC = listaCodigoCPC;
	}

	/**
	 * Retorna la lista listaCodigoCPC
	 * 
	 * @return listaCodigoCPC
	 */
	public RegistroDataModelImpl getListaCodigoCPCE() {
		return listaCodigoCPCE;
	}

	/**
	 * Asigna la lista listaCodigoCPC
	 * 
	 * @param listaCodigoCPC
	 * Variable a asignar en listaCodigoCPC
	 */
	public void setListaCodigoCPCE(RegistroDataModelImpl listaCodigoCPCE) {
		this.listaCodigoCPCE = listaCodigoCPCE;
	}

	/**
	 * Retorna la lista listaFuenteCUIPO
	 * 
	 * @return listaFuenteCUIPO
	 */
	public RegistroDataModelImpl getListaFuenteCUIPO() {
		return listaFuenteCUIPO;
	}

	/**
	 * Asigna la lista listaFuenteCUIPO
	 * 
	 * @param listaFuenteCUIPO
	 * Variable a asignar en listaFuenteCUIPO
	 */
	public void setListaFuenteCUIPO(RegistroDataModelImpl listaFuenteCUIPO) {
		this.listaFuenteCUIPO = listaFuenteCUIPO;
	}

	/**
	 * Retorna la lista listaFuenteCUIPO
	 * 
	 * @return listaFuenteCUIPO
	 */
	public RegistroDataModelImpl getListaFuenteCUIPOE() {
		return listaFuenteCUIPOE;
	}

	/**
	 * Asigna la lista listaFuenteCUIPO
	 * 
	 * @param listaFuenteCUIPO
	 * Variable a asignar en listaFuenteCUIPO
	 */
	public void setListaFuenteCUIPOE(RegistroDataModelImpl listaFuenteCUIPOE) {
		this.listaFuenteCUIPOE = listaFuenteCUIPOE;
	}

	/**
	 * Retorna la lista listaProductoCUIPO
	 * 
	 * @return listaProductoCUIPO
	 */
	public RegistroDataModelImpl getListaProductoCUIPO() {
		return listaProductoCUIPO;
	}

	/**
	 * Asigna la lista listaProductoCUIPO
	 * 
	 * @param listaProductoCUIPO
	 * Variable a asignar en listaProductoCUIPO
	 */
	public void setListaProductoCUIPO(
			RegistroDataModelImpl listaProductoCUIPO) {
		this.listaProductoCUIPO = listaProductoCUIPO;
	}

	/**
	 * Retorna la lista listaProductoCUIPO
	 * 
	 * @return listaProductoCUIPO
	 */
	public RegistroDataModelImpl getListaProductoCUIPOE() {
		return listaProductoCUIPOE;
	}
	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSectorE() {
		return listaSectorE;
	}
	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en  listaSector
	 */
	public void setListaSectorE(RegistroDataModelImpl listaSectorE) {
		this.listaSectorE = listaSectorE;
	}

	/**
	 * Retorna la lista listaPrograma
	 * 
	 * @return listaPrograma
	 */
	public RegistroDataModelImpl getListaProgramaE() {
		return listaProgramaE;
	}
	/**
	 * Asigna la lista listaPrograma
	 * 
	 * @param listaPrograma
	 * Variable a asignar en  listaPrograma
	 */
	public void setListaProgramaE(RegistroDataModelImpl listaProgramaE) {
		this.listaProgramaE = listaProgramaE;
	}

	/**
	 * Retorna la lista listaSupPrograma
	 * 
	 * @return listaSupPrograma
	 */
	public RegistroDataModelImpl getListaSupProgramaE() {
		return listaSupProgramaE;
	}
	/**
	 * Asigna la lista listaSupPrograma
	 * 
	 * @param listaSupPrograma
	 * Variable a asignar en  listaSupPrograma
	 */
	public void setListaSupProgramaE(RegistroDataModelImpl listaSupProgramaE) {
		this.listaSupProgramaE = listaSupProgramaE;
	}

	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProductoE() {
		return listaCodigoProductoE;
	}
	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en  listaCodigoProducto
	 */
	public void setListaCodigoProductoE(RegistroDataModelImpl listaCodigoProductoE) {
		this.listaCodigoProductoE = listaCodigoProductoE;
	}

	/**
	 * Retorna la lista listaCodigoBPIN
	 * 
	 * @return listaCodigoBPIN
	 */
	public RegistroDataModelImpl getListaCodigoBPINE() {
		return listaCodigoBPINE;
	}
	/**
	 * Asigna la lista listaCodigoBPIN
	 * 
	 * @param listaCodigoBPIN
	 * Variable a asignar en  listaCodigoBPIN
	 */
	public void setListaCodigoBPINE(RegistroDataModelImpl listaCodigoBPINE) {
		this.listaCodigoBPINE = listaCodigoBPINE;
	}

	/**
	 * Retorna la lista listaCodigoCCPET
	 * 
	 * @return listaCodigoCCPET
	 */
	public RegistroDataModelImpl getListaCodigoCCPETE() {
		return listaCodigoCCPETE;
	}




	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en listaCodigoCCPET

	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigoCCPET
	 */
	public void setListaCodigoCCPETE(RegistroDataModelImpl listaCodigoCCPETE) {
		this.listaCodigoCCPETE = listaCodigoCCPETE;
	}
	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigo_CCPET
	 */
	public void setListaCodigo_CCPETE(RegistroDataModelImpl listaCodigo_CCPETE) {
		this.listaCodigo_CCPETE = listaCodigo_CCPETE;
	}
	/**
	 * Retorna la lista listaCodigo_CCPET
	 * 
	 * @return listaCodigo_CCPET
	 */
	public RegistroDataModelImpl getListaCodigo_CCPETE() {
		return listaCodigo_CCPETE;
	}

	/**
	 * Retorna la lista listaCodigoCPCDANE
	 * 
	 * @return listaCodigoCPCDANE
	 */
	public RegistroDataModelImpl getListaCodigoCPCDANEE() {
		return listaCodigoCPCDANEE;
	}
	/**
	 * Asigna la lista listaCodigoCPCDANE
	 * 
	 * @param listaCodigoCPCDANE
	 * Variable a asignar en  listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANEE(RegistroDataModelImpl listaCodigoCPCDANEE) {
		this.listaCodigoCPCDANEE = listaCodigoCPCDANEE;
	}

	/**
	 * Retorna la lista listaCodigoUnidEje
	 * 
	 * @return listaCodigoUnidEje
	 */
	public RegistroDataModelImpl getListaCodigoUnidEjeE() {
		return listaCodigoUnidEjeE;
	}
	/**
	 * Asigna la lista listaCodigoUnidEje
	 * 
	 * @param listaCodigoUnidEje
	 * Variable a asignar en  listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEjeE(RegistroDataModelImpl listaCodigoUnidEjeE) {
		this.listaCodigoUnidEjeE = listaCodigoUnidEjeE;
	}

	/**
	 * Retorna la lista listaCodigoFuente
	 * 
	 * @return listaCodigoFuente
	 */
	public RegistroDataModelImpl getListaCodigoFuenteE() {
		return listaCodigoFuenteE;
	}
	/**
	 * Asigna la lista listaCodigoFuente
	 * 
	 * @param listaCodigoFuente
	 * Variable a asignar en  listaCodigoFuente
	 */
	public void setListaCodigoFuenteE(RegistroDataModelImpl listaCodigoFuenteE) {
		this.listaCodigoFuenteE = listaCodigoFuenteE;
	}

	/**
	 * Retorna la lista listaCodigoCCPETRega
	 * 
	 * @return listaCodigoCCPETRega
	 */
	public RegistroDataModelImpl getListaCodigoCCPETRegaE() {
		return listaCodigoCCPETRegaE;
	}
	/**
	 * Asigna la lista listaCodigoCCPETRega
	 * 
	 * @param listaCodigoCCPETRega
	 * Variable a asignar en  listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRegaE(RegistroDataModelImpl listaCodigoCCPETRegaE) {
		this.listaCodigoCCPETRegaE = listaCodigoCCPETRegaE;
	}

	/**
	 * Asigna la lista listaProductoCUIPO
	 * 
	 * @param listaProductoCUIPO
	 * Variable a asignar en listaProductoCUIPO
	 */
	public void setListaProductoCUIPOE(
			RegistroDataModelImpl listaProductoCUIPOE) {
		this.listaProductoCUIPOE = listaProductoCUIPOE;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public void cancel(String key, int rowNum) {
		if (rowNum != -1) {
			listaInicial.getDatasource().get(rowNum % 10).getCampos().put("",
					"");
		}
	}

	/**
	 * Trae el valor almacenado en la base de datos para el parametro ingresado.
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
			parametro = ejbSysmanUtil.consultarParametro(compania,
					nombreParametro, SessionUtil.getModulo(),
					new Date(), true);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	private String ObtenerValorSinExponencial(String numero) {
		String numeroExponencial = String.valueOf(numero);
		String[] valorAdyacente;
		String[] valorSubyacente;
		int cicloPermitido = 0;
		if (numeroExponencial.indexOf("E") != -1) {
			valorAdyacente = numeroExponencial.split(".");
			valorSubyacente = valorAdyacente[1].split("E");
			numeroExponencial = valorAdyacente[0].toString()
					+ valorSubyacente[0].toString();
			cicloPermitido = valorSubyacente[0].length();
			cicloPermitido = Integer.parseInt(valorSubyacente[1])
					- cicloPermitido;
			for (int x = 0; x < cicloPermitido; x++) {
				numeroExponencial += "0";
			}
		}
		return numeroExponencial;

	}

	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
	 *
	 * @param object
	 * Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}

	public void setVisibleAfectaciones(boolean visibleafectaciones) {
		this.visibleAfectaciones = visibleafectaciones;
	}
	private void activarDesactivarCampo() {
		bloqFecha = !("MOP".equals(claseComprobante));
		if ("MOP".equals(claseComprobante)) {
			// Activar campo fecha
			visibleAfectaciones = true;
		}
	}
	
	/**
	 * @return the verImpuesto
	 */
	public boolean isVerImpuesto() {
		return verImpuesto;
	}

	/**
	 * @param verImpuesto the verImpuesto to set
	 */
	public void setVerImpuesto(boolean verImpuesto) {
		this.verImpuesto = verImpuesto;
	}

	public String getVerContabilizar() {
		return verContabilizar;
	}

	public void setVerContabilizar(String verContabilizar) {
		this.verContabilizar = verContabilizar;
	}
	
	/**
	 * Retorna la variable programa
	 * 
	 * @return  programa
	 */
	public String getPrograma() {
		return programa;
	}
	/**
	 * Asigna la variable  programa
	 * 
	 * @param  programa
	 * Variable a asignar en  programa
	 */
	public void setPrograma(String programa) {
		this.programa = programa;
	}
	/**
	 * Retorna la variable supPrograma
	 * 
	 * @return  supPrograma
	 */
	public String getSupPrograma() {
		return supPrograma;
	}
	/**
	 * Asigna la variable  supPrograma
	 * 
	 * @param  supPrograma
	 * Variable a asignar en  supPrograma
	 */
	public void setSupPrograma(String supPrograma) {
		this.supPrograma = supPrograma;
	}
	/**
	 * Retorna la variable codigoProducto
	 * 
	 * @return  codigoProducto
	 */
	public String getCodigoProducto() {
		return codigoProducto;
	}
	/**
	 * Asigna la variable  codigoProducto
	 * 
	 * @param  codigoProducto
	 * Variable a asignar en  codigoProducto
	 */
	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}
	/**
	 * Retorna la variable codigoBPIN
	 * 
	 * @return  codigoBPIN
	 */
	public String getCodigoBPIN() {
		return codigoBPIN;
	}
	/**
	 * Asigna la variable  codigoBPIN
	 * 
	 * @param  codigoBPIN
	 * Variable a asignar en  codigoBPIN
	 */
	public void setCodigoBPIN(String codigoBPIN) {
		this.codigoBPIN = codigoBPIN;
	}
	/**
	 * Retorna la variable codigoCCPET
	 * 
	 * @return  codigoCCPET
	 */
	public String getCodigoCCPET() {
		return codigoCCPET;
	}
	/**
	 * Asigna la variable  codigoCCPET
	 * 
	 * @param  codigoCCPET
	 * Variable a asignar en  codigoCCPET
	 */
	public void setCodigoCCPET(String codigoCCPET) {
		this.codigoCCPET = codigoCCPET;
	}
	/**
	 * Retorna la variable codigoCPCDANE
	 * 
	 * @return  codigoCPCDANE
	 */
	public String getCodigoCPCDANE() {
		return codigoCPCDANE;
	}
	/**
	 * Asigna la variable  codigoCPCDANE
	 * 
	 * @param  codigoCPCDANE
	 * Variable a asignar en  codigoCPCDANE
	 */
	public void setCodigoCPCDANE(String codigoCPCDANE) {
		this.codigoCPCDANE = codigoCPCDANE;
	}
	/**
	 * Retorna la variable codigoUnidEje
	 * 
	 * @return  codigoUnidEje
	 */
	public String getCodigoUnidEje() {
		return codigoUnidEje;
	}
	/**
	 * Asigna la variable  codigoUnidEje
	 * 
	 * @param  codigoUnidEje
	 * Variable a asignar en  codigoUnidEje
	 */
	public void setCodigoUnidEje(String codigoUnidEje) {
		this.codigoUnidEje = codigoUnidEje;
	}
	/**
	 * Retorna la variable codigoFuente
	 * 
	 * @return  codigoFuente
	 */
	public String getCodigoFuente() {
		return codigoFuente;
	}
	/**
	 * Asigna la variable  codigoFuente
	 * 
	 * @param  codigoFuente
	 * Variable a asignar en  codigoFuente
	 */
	public void setCodigoFuente(String codigoFuente) {
		this.codigoFuente = codigoFuente;
	}
	/**
	 * Retorna la variable codigoCCPETRega
	 * 
	 * @return  codigoCCPETRega
	 */
	public String getCodigoCCPETRega() {
		return codigoCCPETRega;
	}
	/**
	 * Asigna la variable  codigoCCPETRega
	 * 
	 * @param  codigoCCPETRega
	 * Variable a asignar en  codigoCCPETRega
	 */
	public void setCodigoCCPETRega(String codigoCCPETRega) {
		this.codigoCCPETRega = codigoCCPETRega;
	}
	public int getModelo()
	{
		return modelo;
	}

	public void setModelo( int modelo)
	{
		this.modelo = modelo;
	}

	public int getModeloN()
	{
		return modeloN;
	}

	public void setModeloN( int modeloN)
	{
		this.modeloN = modeloN;
	}

	public String getVerCCPET()
	{
		return verCCPET;
	}

	public void setVerCCPET(String verCCPET)
	{
		this.verCCPET = verCCPET;
	}




	public void setbloqSector(boolean bloqSector)
	{
		this.bloqSector = bloqSector;
	}



	public boolean getbloqSector()
	{
		return bloqSector;
	}

	public void setbloqPrograma(boolean bloqPrograma)
	{
		this.bloqPrograma = bloqPrograma;
	}
	public boolean getbloqPrograma()
	{
		return bloqPrograma;
	}

	public void setbloqSupPrograma(boolean bloqSupPrograma)
	{
		this.bloqSupPrograma = bloqSupPrograma;
	}
	public boolean getbloqSupPrograma()
	{
		return bloqSupPrograma;
	}

	public void setbloqCodigoProducto(boolean bloqCodigoProducto)
	{
		this.bloqCodigoProducto = bloqCodigoProducto;
	}
	public boolean getbloqCodigoProducto()
	{
		return bloqCodigoProducto;
	}


	public void setbloqCodigoBPIN(boolean bloqCodigoBPIN)
	{
		this.bloqCodigoBPIN = bloqCodigoBPIN;
	}
	public boolean getbloqCodigoBPIN()
	{
		return bloqCodigoBPIN;
	}

	public void setbloqCodigoCCPET(boolean bloqCodigoCCPET)
	{
		this.bloqCodigoCCPET = bloqCodigoCCPET;
	}
	public boolean getbloqCodigoCCPET()
	{
		return bloqCodigoCCPET;
	}

	public void setbloqCodigoCPCDANE(boolean bloqCodigoCPCDANE)
	{
		this.bloqCodigoCPCDANE = bloqCodigoCPCDANE;
	}
	public boolean getbloqCodigoCPCDANE()
	{
		return bloqCodigoCPCDANE;
	}


	public void setbloqCodigoUnidEje(boolean bloqCodigoUnidEje)
	{
		this.bloqCodigoUnidEje = bloqCodigoUnidEje;
	}
	public boolean getbloqCodigoUnidEje()
	{
		return bloqCodigoUnidEje;
	}

	public void setbloqCodigoFuente(boolean bloqCodigoFuente)
	{
		this.bloqCodigoFuente = bloqCodigoFuente;
	}
	public boolean getbloqCodigoFuente()
	{
		return bloqCodigoFuente;
	}

	public void setbloqCodigoCCPETRega(boolean bloqCodigoCCPETRega)
	{
		this.bloqCodigoCCPETRega = bloqCodigoCCPETRega;
	}
	public boolean getbloqCodigoCCPETRega()
	{
		return bloqCodigoCCPETRega;
	}




	/**
	 * Retorna la variable sector
	 * 
	 * @return  sector
	 */
	public String getSector() {
		return sector;
	}
	/**
	 * Asigna la variable  sector
	 * 
	 * @param  sector
	 * Variable a asignar en  sector
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}

	/**
	 * Retorna la lista listaSector
	 * 
	 * @return listaSector
	 */
	public RegistroDataModelImpl getListaSector() {
		return listaSector;
	}
	/**
	 * Asigna la lista listaSector
	 * 
	 * @param listaSector
	 * Variable a asignar en  listaSector
	 */
	public void setListaSector(RegistroDataModelImpl listaSector) {
		this.listaSector = listaSector;
	}

	/**
	 * Retorna la lista listaPrograma
	 * 
	 * @return listaPrograma
	 */
	public RegistroDataModelImpl getListaPrograma() {
		return listaPrograma;
	}
	/**
	 * Asigna la lista listaPrograma
	 * 
	 * @param listaPrograma
	 * Variable a asignar en  listaPrograma
	 */
	public void setListaPrograma(RegistroDataModelImpl listaPrograma) {
		this.listaPrograma = listaPrograma;
	}
	/**
	 * Retorna la lista listaSupPrograma
	 * 
	 * @return listaSupPrograma
	 */
	public RegistroDataModelImpl getListaSupPrograma() {
		return listaSupPrograma;
	}
	/**
	 * Asigna la lista listaSupPrograma
	 * 
	 * @param listaSupPrograma
	 * Variable a asignar en  listaSupPrograma
	 */
	public void setListaSupPrograma(RegistroDataModelImpl listaSupPrograma) {
		this.listaSupPrograma = listaSupPrograma;
	}
	/**
	 * Retorna la lista listaCodigoProducto
	 * 
	 * @return listaCodigoProducto
	 */
	public RegistroDataModelImpl getListaCodigoProducto() {
		return listaCodigoProducto;
	}
	/**
	 * Asigna la lista listaCodigoProducto
	 * 
	 * @param listaCodigoProducto
	 * Variable a asignar en  listaCodigoProducto
	 */
	public void setListaCodigoProducto(RegistroDataModelImpl listaCodigoProducto) {
		this.listaCodigoProducto = listaCodigoProducto;
	}
	/**
	 * Retorna la lista listaCodigoBPIN
	 * 
	 * @return listaCodigoBPIN
	 */
	public RegistroDataModelImpl getListaCodigoBPIN() {
		return listaCodigoBPIN;
	}
	/**
	 * Asigna la lista listaCodigoBPIN
	 * 
	 * @param listaCodigoBPIN
	 * Variable a asignar en  listaCodigoBPIN
	 */
	public void setListaCodigoBPIN(RegistroDataModelImpl listaCodigoBPIN) {
		this.listaCodigoBPIN = listaCodigoBPIN;
	}
	/**
	 * Retorna la lista listaCodigoCCPET
	 * 
	 * @return listaCodigoCCPET
	 */
	public RegistroDataModelImpl getListaCodigoCCPET() {
		return listaCodigoCCPET;
	}
	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigoCCPET
	 */
	public void setListaCodigoCCPET(RegistroDataModelImpl listaCodigoCCPET) {
		this.listaCodigoCCPET = listaCodigoCCPET;
	}


	/**
	 * Retorna la lista listaCodigoCCPET
	 * 
	 * @return listaCodigoCCPET
	 */
	public RegistroDataModelImpl getListaCodigo_CCPET() {
		return listaCodigo_CCPET;
	}
	/**
	 * Asigna la lista listaCodigoCCPET
	 * 
	 * @param listaCodigoCCPET
	 * Variable a asignar en  listaCodigoCCPET
	 */
	public void setListaCodigo_CCPET(RegistroDataModelImpl listaCodigo_CCPET) {
		this.listaCodigo_CCPET = listaCodigo_CCPET;
	}
	/**
	 * Retorna la lista listaCodigoCPCDANE
	 * 
	 * @return listaCodigoCPCDANE
	 */
	public RegistroDataModelImpl getListaCodigoCPCDANE() {
		return listaCodigoCPCDANE;
	}
	/**
	 * Asigna la lista listaCodigoCPCDANE
	 * 
	 * @param listaCodigoCPCDANE
	 * Variable a asignar en  listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANE(RegistroDataModelImpl listaCodigoCPCDANE) {
		this.listaCodigoCPCDANE = listaCodigoCPCDANE;
	}
	/**
	 * Retorna la lista listaCodigoUnidEje
	 * 
	 * @return listaCodigoUnidEje
	 */
	public RegistroDataModelImpl getListaCodigoUnidEje() {
		return listaCodigoUnidEje;
	}
	/**
	 * Asigna la lista listaCodigoUnidEje
	 * 
	 * @param listaCodigoUnidEje
	 * Variable a asignar en  listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEje(RegistroDataModelImpl listaCodigoUnidEje) {
		this.listaCodigoUnidEje = listaCodigoUnidEje;
	}
	/**
	 * Retorna la lista listaCodigoFuente
	 * 
	 * @return listaCodigoFuente
	 */
	public RegistroDataModelImpl getListaCodigoFuente() {
		return listaCodigoFuente;
	}
	/**
	 * Asigna la lista listaCodigoFuente
	 * 
	 * @param listaCodigoFuente
	 * Variable a asignar en  listaCodigoFuente
	 */
	public void setListaCodigoFuente(RegistroDataModelImpl listaCodigoFuente) {
		this.listaCodigoFuente = listaCodigoFuente;
	}
	/**
	 * Retorna la lista listaCodigoCCPETRega
	 * 
	 * @return listaCodigoCCPETRega
	 */
	public RegistroDataModelImpl getListaCodigoCCPETRega() {
		return listaCodigoCCPETRega;
	}
	/**
	 * Asigna la lista listaCodigoCCPETRega
	 * 
	 * @param listaCodigoCCPETRega
	 * Variable a asignar en  listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRega(RegistroDataModelImpl listaCodigoCCPETRega) {
		this.listaCodigoCCPETRega = listaCodigoCCPETRega;
	}
	public List<Registro> getListaTipoClasificador() {
		return listaTipoClasificador;
	}

	public void setListaTipoClasificador(List<Registro> listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}

	public List<Registro> getListaTipoCuentaRegalias() {
		return listaTipoCuentaRegalias;
	}

	public void setListaTipoCuentaRegalias(List<Registro> listaTipoCuentaRegalias) {
		this.listaTipoCuentaRegalias = listaTipoCuentaRegalias;
	}

	public boolean isBloqPoliticaPublica() {
		return bloqPoliticaPublica;
	}

	public void setBloqPoliticaPublica(boolean bloqPoliticaPublica) {
		this.bloqPoliticaPublica = bloqPoliticaPublica;
	}

	public boolean isBloqDetalleSectorial() {
		return bloqDetalleSectorial;
	}

	public void setBloqDetalleSectorial(boolean bloqDetalleSectorial) {
		this.bloqDetalleSectorial = bloqDetalleSectorial;
	}
	public RegistroDataModelImpl getListaPoliticaPublica() {
		return listaPoliticaPublica;
	}

	public void setListaPoliticaPublica(RegistroDataModelImpl listaPoliticaPublica) {
		this.listaPoliticaPublica = listaPoliticaPublica;
	}

	public RegistroDataModelImpl getListaPoliticaPublicaE() {
		return listaPoliticaPublicaE;
	}

	public void setListaPoliticaPublicaE(RegistroDataModelImpl listaPoliticaPublicaE) {
		this.listaPoliticaPublicaE = listaPoliticaPublicaE;
	}

	public RegistroDataModelImpl getListaDetalleSectorial() {
		return listaDetalleSectorial;
	}

	public void setListaDetalleSectorial(RegistroDataModelImpl listaDetalleSectorial) {
		this.listaDetalleSectorial = listaDetalleSectorial;
	}

	public RegistroDataModelImpl getListaDetalleSectorialE() {
		return listaDetalleSectorialE;
	}

	public void setListaDetalleSectorialE(RegistroDataModelImpl listaDetalleSectorialE) {
		this.listaDetalleSectorialE = listaDetalleSectorialE;
	}

	public String getNaturalezaCuenta() {
		return naturalezaCuenta;
	}

	public void setNaturalezaCuenta(String naturalezaCuenta) {
		this.naturalezaCuenta = naturalezaCuenta;
	}
	public String getAuxiliarCodigo() {
		return auxiliarCodigo;
	}

	public void setAuxiliarCodigo(String auxiliarCodigo) {
		this.auxiliarCodigo = auxiliarCodigo;
	}
	public String getCuentaAux() {
		return cuentaAux;
	}

	public void setCuentaAux(String cuentaAux) {
		this.cuentaAux = cuentaAux;
	}
	public String getClaseAux() {
		return claseAux;
	}

	public void setClaseAux(String claseAux) {
		this.claseAux = claseAux;
	}

	/**
	 * @return the listarecursoSGR
	 */
	public RegistroDataModelImpl getListarecursoSGR() {
		return listarecursoSGR;
	}

	/**
	 * @param listarecursoSGR the listarecursoSGR to set
	 */
	public void setListarecursoSGR(RegistroDataModelImpl listarecursoSGR) {
		this.listarecursoSGR = listarecursoSGR;
	}

	/**
	 * @return the listarecursoSGRE
	 */
	public RegistroDataModelImpl getListarecursoSGRE() {
		return listarecursoSGRE;
	}

	/**
	 * @param listarecursoSGRE the listarecursoSGRE to set
	 */
	public void setListarecursoSGRE(RegistroDataModelImpl listarecursoSGRE) {
		this.listarecursoSGRE = listarecursoSGRE;
	}

	/**
	 * @return the codigoSGR
	 */
	public String getCodigoSGR() {
		return codigoSGR;
	}

	/**
	 * @param codigoSGR the codigoSGR to set
	 */
	public void setCodigoSGR(String codigoSGR) {
		this.codigoSGR = codigoSGR;
	}

	
}
