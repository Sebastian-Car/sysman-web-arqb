/*-
 * ConfigurarplanpresupuestalControlador.java
 *
 * 1.0
 * 
 * 05/07/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cgr.ejb.impl.EjbCGRCero;
import com.sysman.cgr.enums.ConfigurarplanpresupuestalControladorUrlEnum;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.TercerosControlador;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
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
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 05/07/2022
 * @author cperez2
 */
@ManagedBean
@ViewScoped
public class ConfigurarplanpresupuestalControlador extends BeanBaseDatosAcmeImpl {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String anio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String naturaleza;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String regalias;
	private String rubroCodigo;

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAnio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaTipoVigencia;

	private Registro registroSubSfArbol;

	private Registro registroSubSfConfigGeneral;

	private Registro registroSubSfActualiza;

	/**
	 * Atributo de referencia para el subformulario SfFuenteRecurso
	 */
	private Registro registroSubSfFuenteRecurso;

	/**
	 * Atributo de referencia para el subformulario SfTipoVigencia
	 */
	private Registro registroSubSfTipoVigencia;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSector;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSectorE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPrograma;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProgramaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSupPrograma;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSupProgramaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoProducto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoProductoE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPET;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPETE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCPCDANE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCPCDANEE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoUnidEje;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoUnidEjeE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoFuente;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoFuenteE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPETRega;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoCCPETRegaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPoliticaPublica;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPoliticaPublicaE;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaDetalleSectorial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaDetalleSectorialE;

	private RegistroDataModelImpl listaTipoClasificador;
	private RegistroDataModelImpl listaTipoClasificadorE;

	private RegistroDataModelImpl listaClaseClasificador;
	private RegistroDataModelImpl listaClaseClasificadorE;
	
	private RegistroDataModelImpl listaDetalleSectorials;
    private RegistroDataModelImpl listaDetalleSectorialsE;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTipoClasificadorNu;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaTipoClasificadorNuE;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEquivalenteCuipo;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaEquivalenteCuipoE;

	private RegistroDataModelImpl listaclaseclasificadormod;

	private RegistroDataModelImpl listatipoclasificadorpost;

	private RegistroDataModelImpl listaclaseclasificadormodE;

	private RegistroDataModelImpl listatipoclasificadorpostE;

	private RegistroDataModelImpl listaSfactualiza;

	private RegistroDataModelImpl listaReservaApropiacionEquivalente;
	private RegistroDataModelImpl listaReservaApropiacionEquivalenteE;
	private RegistroDataModelImpl listaReservaCajaEquivalente;
	private RegistroDataModelImpl listaReservaCajaEquivalenteE;

	/**
	 * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
	 * el identificador del registro que se selecciono
	 */
	private String auxiliar;
	private String claseClasificador = "";
	private boolean varVolver = true;
	private String cuentaAux = "";
	private String claseAux = ""; // de cualclasificador depende depende
	private String auxiliarCodigo = "";
	private boolean valGral = true;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	// <DECLARAR_LISTAS_SUBFORM>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSfconfiggeneral;
	private RegistroDataModelImpl listaSfarbol;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSffuenterecurso;

	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSftipovigencia;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga1;

	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;

	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcelGral y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcelGral;

	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * SelecFile y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaSelecFile;
	private List<Registro> listaCompania;
	private List<Registro> listaClasificador;
	private List<Registro> listaTipoVigenciaArbol;
	private List<Registro> listaTipoClasificadorArbol;

	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;
	private long contador;

	// </DECLARAR_LISTAS_SUBFORM>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_ADICIONALES>
	/**
	 * Atributo de referencia para el subformulario
	 */
	private Registro registroSub;

	private String naturalezaCuenta = "";
	private boolean bloqTipoRec;
	private boolean bloqVigAnt;
	private boolean bloqSituaF;
	private boolean bloqTransfer;
	private boolean bloqDestEsp;
	private boolean bloqTipoNor;
	private boolean bloqNumNorm;
	private boolean bloqFecNorm;

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
	private boolean visibleFuenteregali;
	private int modelo;

	/**
	 * Variable para habilitar los campos de equivalente CUIPO
	 */
	private boolean manejaEquivalenteCUIPO;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Implementacion del EJB de EjbSysmanUtilRemote para hacer el llamado a las
	 * funciones y procedimientos que se invocan dentro del Controlador y se
	 * encuentran almacenadas en el paquete PCK_SYSMAN_UTIL
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbCGRCero EjbCGRCero;
	private String salida;

	private int indiceSfconfiggeneral;
	/**
	 * variables del desarrollo de la pesta�a de Apropiaciones Inciales
	 */
	private ContenedorArchivo contArchivocargarApropiaciones;
	private RegistroDataModelImpl listaCCPET;
	private RegistroDataModelImpl listaprogramamga1;
	private RegistroDataModelImpl listaunidadEjecutora;
	private RegistroDataModelImpl listaCCPETE;
	private RegistroDataModelImpl listaprogramamga1E;
	private RegistroDataModelImpl listaunidadEjecutoraE;
	private Registro registroSubapropiaciones;
	private RegistroDataModelImpl listaApropiaciones;
	// </DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de ConfigurarplanpresupuestalControlador
	 */
	public ConfigurarplanpresupuestalControlador() {
		super();
		compania = SessionUtil.getCompania();
		contArchivocargarExcel = new ContenedorArchivo();
		contArchivocargarExcelGral = new ContenedorArchivo();
		contArchivocargarApropiaciones = new ContenedorArchivo();
		anio = String.valueOf(SysmanFunciones.ano(new Date()));
		manejaEquivalenteCUIPO = false;
		try {
			numFormulario = GeneralCodigoFormaEnum.CONFIGURACION_PLAN.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			registroSub = new Registro(new HashMap<String, Object>());
			registroSubSfConfigGeneral = new Registro(new HashMap<String, Object>());
			registroSubSfArbol = new Registro(new HashMap<String, Object>());
			registroSubSfFuenteRecurso = new Registro(new HashMap<String, Object>());
			registroSubSfTipoVigencia = new Registro(new HashMap<String, Object>());
			registroSubSfActualiza = new Registro(new HashMap<String, Object>());
			registroSubapropiaciones = new Registro(new HashMap<String, Object>());
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas() {
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTipoClasificador();
		cargarListaTipoClasificadorE();
		cargarListaClaseClasificador();
		cargarListaClaseClasificadorE();
		cargarListaTipoClasificadorNu();
		cargarListaTipoClasificadorNuE();
		cargarListaReservaApropiacionEquivalente();
		cargarListaReservaApropiacionEquivalenteE();
		cargarListaReservaCajaEquivalente();
		cargarListaReservaCajaEquivalenteE();
		cargarListaEquivalenteCuipo();
		cargarListaEquivalenteCuipoE();

		try {
			cargarConfiguracionCuipo("");
		} catch (SystemException e) {
			e.printStackTrace();
		}
		cargarListaAnio();
		cargarListaTipoVigencia();
		cargarListaclaseclasificadormod();
		// </CARGAR_LISTA>
	}

	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de todas las
	 * listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub() {
		// <CARGAR_LISTAS_SUBFORM>
		// anio = registro.getCampos().get("NUMERO").toString();

		cargarListaSfconfiggeneral();
		cargarListaSfarbol();
		cargarListaSffuenterecurso();
		cargarListaSftipovigencia();
		cargarListaApropiaciones();
		// </CARGAR_LISTAS_SUBFORM>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
	}

	/**
	 * En este metodo se iguala a null todas las listas de los subformularios
	 */
	@Override
	public void iniciarListasSubNulo() {
		// <CARGAR_LISTAS_SUBFORM_NULL>
		listaSfconfiggeneral = null;
		listaSfarbol = null;
		listaSffuenterecurso = null;
		listaSftipovigencia = null;
		listaApropiaciones = null;
		// </CARGAR_LISTAS_SUBFORM_NULL>
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */

	@PostConstruct
	public void inicializar() {

		enumBase = GenericUrlEnum.PLAN_PRESUPUESTALCLASI_ANO;
		String parametro = getParametro("HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO", "NO");
		String parametro1 = getParametro("ACTUALIZA SSF DESDE FUENTE", "NO");
		visibleFuenteregali = false;
		if (parametro.equals("SI") || parametro1.equals("SI")) { // bloqueado
			visibleFuenteregali = true;
		}
		naturaleza = "";
		regalias = "";
		rubroCodigo = "";
		buscarLlave();
		asignarOrigenDatos();

		cargarListaAnio();
		cambiarAnio();

		JsfUtil.ejecutarJavaScript(
				"var elements = document.getElementById('FR2361:TBFR2361_data').getElementsByTagName('tr')");
		JsfUtil.ejecutarJavaScript("var subelement = elements[0].getElementsByTagName('td')");
		JsfUtil.ejecutarJavaScript("var btn = subelement[1].getElementsByTagName('button')");
		JsfUtil.ejecutarJavaScript("document.getElementById(btn[1]['id']).click()");

		// reasignarOrigenGrilla();
	}

	/**
	 * Se realiza la asignacion de la variable origenDatos por la consulta
	 * correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	/**
	 * metodo que se lalama para asignar los datos del formulario
	 */
	@Override
	public void asignarOrigenDatos() {
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}

	/**
	 * Se realiza la asignacion de la variable origenGrilla por la consulta
	 * correspondiente de la grilla del formulario, se hace la asignacion de dicha
	 * consulta a los objetos listaInicial y listaInicialF
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */

	/**
	 * 
	 * Carga la lista listaSfconfiggeneral
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSfconfiggeneral() {

		UrlBean urlBean;

		if (naturaleza.equals("")) {
			naturaleza = "D";
		}
		if (regalias.equals("")) {
			regalias = "V";
		}
		try {

			if (regalias.equals("R")) {
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PRESUPUESTALCLASIREGA.getGridKey());
			} else {
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PRESUPUESTALCLASI.getGridKey());
			}
			Map<String, Object> param = new TreeMap<>();
			param.put("KEY_COMPANIA", compania);
			param.put("NATURALEZA", naturaleza);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);

			if (regalias.equals("R")) {
				listaSfconfiggeneral = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
						param, CacheUtil.getLlaveServicio(urlConexionCache,
								GenericUrlEnum.PLAN_PRESUPUESTALCLASIREGA.getTable()));
			} else {
				listaSfconfiggeneral = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
						param,
						CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.PLAN_PRESUPUESTALCLASI.getTable()));
			}
		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		cargarListaSector();
		cargarListaSectorE();
		cargarListaPrograma();
		cargarListaProgramaE();
		cargarListaSupPrograma();
		cargarListaSupProgramaE();
		cargarListaCodigoProducto();
		cargarListaCodigoProductoE();
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
		cargarListaDetalleSectorials(); 
		cargarListaDetalleSectorialsE();
	}

	/**
	 * 
	 * Carga la lista listaSfconfiggeneral
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSfarbol() {

		UrlBean urlBean;
		if (!compania.equals("") && !naturaleza.equals("") && !regalias.equals("")) {
			try {
				if (regalias.equals("R")) {
					urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PRESUPUESTALARBOLREGA.getGridKey());
				} else {
					urlBean = UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PRESUPUESTALARBOL.getGridKey());
				}
				Map<String, Object> param = new TreeMap<>();
				param.put("KEY_COMPANIA", compania);
				param.put("NATURALEZA", naturaleza);
				param.put(GeneralParameterEnum.ANIO.getName(), anio);

				if (regalias.equals("R")) {
					listaSfarbol = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
							CacheUtil.getLlaveServicio(urlConexionCache,
									GenericUrlEnum.PLAN_PRESUPUESTALARBOLREGA.getTable()));

				} else {
					listaSfarbol = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
							CacheUtil.getLlaveServicio(urlConexionCache,
									GenericUrlEnum.PLAN_PRESUPUESTALARBOL.getTable()));
				}
			} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

			cargarListaClaseClasificador();
			cargarListaClaseClasificadorE();
			cargarListaTipoClasificador();
			cargarListaTipoClasificadorE();

		}

	}

	/**
	 * 
	 * Carga la lista listaSfconfiggeneral
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSffuenterecurso() {

		UrlBean urlBean;
		if (!compania.equals("")) {
			try {
				urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GenericUrlEnum.FUENTE_RECURSOSCUIPO.getGridKey());
				Map<String, Object> param = new TreeMap<>();
				param.put("KEY_COMPANIA", compania);
				param.put(GeneralParameterEnum.ANIO.getName(), anio);
				listaSffuenterecurso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
						param,
						CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.FUENTE_RECURSOSCUIPO.getTable()));

			} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}

			cargarListaTipoClasificadorNu();
			cargarListaTipoClasificadorNuE();
			cargarListaReservaApropiacionEquivalente();
			cargarListaReservaApropiacionEquivalenteE();
			cargarListaReservaCajaEquivalente();
			cargarListaReservaCajaEquivalenteE();

		}

	}

	/**
	 * 
	 * Carga la lista listaSfconfiggeneral
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSftipovigencia() {

		UrlBean urlBean;
		try {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PTIPOVIGENCIA.getGridKey());
			Map<String, Object> param = new TreeMap<>();
			param.put("KEY_COMPANIA", compania);
			listaSftipovigencia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					CacheUtil.getLlaveServicio(urlConexionCache, GenericUrlEnum.FUENTE_RECURSOSCUIPO.getTable()));

		} catch (com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		cargarListaEquivalenteCuipo();
		cargarListaEquivalenteCuipoE();

	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaAnio() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAnio = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarplanpresupuestalControladorUrlEnum.URL21760.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			Logger.getLogger(TercerosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaTipoVigencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaTipoVigencia() {
		try {
			listaTipoVigencia = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarplanpresupuestalControladorUrlEnum.URL8542.getValue())
									.getUrl(),
							null));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * cargar lista Modelo combos cuipo
	 * 
	 * @throws SystemException
	 */
	public void cargarConfiguracionCuipo(String cuenta) throws SystemException {
		modelo = ejbSysmanUtil.consultarModeloAno(compania, anio);
		bloqSector = true;

		int aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "001", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqSector = true;
		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqSector = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqSector = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "002", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqPrograma = true;

		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqPrograma = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqPrograma = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "003", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqSupPrograma = true;

		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqSupPrograma = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqSupPrograma = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "004", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoProducto = true;

		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoProducto = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoProducto = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "005", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoBPIN = true;

		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoBPIN = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoBPIN = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "006", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoCCPET = true;

		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoCCPET = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoCCPET = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "007", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoCPCDANE = true;
		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoCPCDANE = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoCPCDANE = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "008", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoUnidEje = true;

		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoUnidEje = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoUnidEje = false;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "009", cuenta);

		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoFuente = true;
		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoFuente = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoFuente = false;
		}

		String parametro = getParametro("HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO", "NO");
		if (parametro.equals("SI")) { // bloqueado
			bloqCodigoFuente = true;
		}

		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "010", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqCodigoCCPETRega = true;
		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqCodigoCCPETRega = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqCodigoCCPETRega = false;
		}
		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "011", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqPoliticaPublica = true;
		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqPoliticaPublica = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqPoliticaPublica = false;
		}
		aplicacionCuenta = ejbSysmanUtil.aplicacionCuenta(compania, anio, "012", cuenta);
		if (aplicacionCuenta == 0) { // bloqueado
			bloqDetalleSectorial = true;
		} else if (aplicacionCuenta == 2) {// bloqueado
			bloqDetalleSectorial = true;
		} else if (aplicacionCuenta == 1) { // habilitado
			bloqDetalleSectorial = false;
		}

	}

	/**
	 * Trae el valor almacenado en la base de datos para el parametro ingresado.
	 *
	 * @param nombreParametro Nombre del parametro en la base de datos.
	 * @param valorDefault    Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String getParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania, nombreParametro, SessionUtil.getModulo(), new Date(),
					true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	/**
	 * 
	 * Carga la lista listaTipoClasificador Unificada cperez
	 *
	 */
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo, int aplicacion, String naturaleza) {
		RegistroDataModelImpl listaTipo = null;
		String clasePadre = "";
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		if (naturaleza.equals("")) {
			naturaleza = "D";
		}
		if (naturaleza != null && !naturaleza.isEmpty() && !naturaleza.equals("")) {

			param.put(GeneralParameterEnum.CLASE.getName(), codigo);

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL13432.getValue());
		}

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

		return listaTipo;
	}

	/*
	 * 
	 */
	public String cargarClasePadre(String codigo, int aplicacion, String Naturaleza) {
		String clasePadre = "";
		Map<String, Object> param = new TreeMap<>();
		List<Registro> listaClasePadre = null;

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), Naturaleza);
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);

		try {
			listaClasePadre = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarplanpresupuestalControladorUrlEnum.URL13431.getValue())
									.getUrl(),
							param));
			if (!listaClasePadre.isEmpty()) {
				for (Registro clase : listaClasePadre) {
					clasePadre = clase.getCampos().get("CLASEPADRE").toString();
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return clasePadre;
	}

	/*
	 * 
	 */
	public String cargarClaseHijo(String codigo, int aplicacion, String Naturaleza, String idHijo) {
		String padre = "";
		Map<String, Object> param = new TreeMap<>();
		List<Registro> listaPadre = null;
		if (naturaleza.equals("")) {
			naturaleza = "D";
		}
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CLASE.getName(), codigo);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), Naturaleza);
		param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
		param.put("IDHIJO", idHijo);

		try {
			listaPadre = RegistroConverter.toListRegistro(requestManager.getList(
					UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ConfigurarplanpresupuestalControladorUrlEnum.URL1884033.getValue())
							.getUrl(),
					param));
			if (!listaPadre.isEmpty()) {
				for (Registro id : listaPadre) {
					padre = id.getCampos().get("IDPADRE").toString();
				}
			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return padre;
	}

	/**
	 * 
	 * Carga la lista listaTipoClasificador Unificada cperez
	 *
	 */
	public RegistroDataModelImpl cargarListaTipoClasificador(String codigo, int aplicacion, String naturaleza,
			String filtro) {
		RegistroDataModelImpl listaTipo = null;
		String clasePadre = "";
		UrlBean urlBean = new UrlBean();
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		if (naturaleza.equals("")) {
			naturaleza = "D";
		}
		// if(naturaleza != null && !naturaleza.isEmpty() && !naturaleza.equals(""))
		// {
		clasePadre = cargarClasePadre(codigo, aplicacion, naturaleza);

		if (clasePadre.isEmpty() || clasePadre == null || clasePadre.equals("")) {
			param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
			param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
			param.put(GeneralParameterEnum.CLASE.getName(), codigo);

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL13433.getValue());
		} else {
			param.put("CODIGOKEY", filtro);
			param.put(GeneralParameterEnum.APLICACION.getName(), aplicacion);
			param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
			param.put(GeneralParameterEnum.CLASE.getName(), codigo);

			param.put(GeneralParameterEnum.CLASE.getName(), codigo);

			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL1884034.getValue());
		}
		listaTipo = null;
		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());

		// }
		return listaTipo;
	}

	/**
	 * 
	 * Carga la lista listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSector() {
		listaSector = cargarListaTipoClasificador("001", 2, naturalezaCuenta);
		cargarListaSectorE();
	}

	/**
	 * 
	 * Carga la lista listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSectorE() {
		listaSectorE = listaSector;
	}

	/**
	 * 
	 * Carga la lista listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPrograma() {

		listaPrograma = cargarListaTipoClasificador("002", 2, naturaleza);
		cargarListaProgramaE();
		listaprogramamga1 = listaPrograma;
		listaprogramamga1E = listaProgramaE;
	}

	/**
	 * 
	 * Carga la lista listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaProgramaE() {
		listaProgramaE = listaPrograma;
	}

	/**
	 * 
	 * Carga la lista listaSupProgramaF
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSupPrograma() {
		listaSupPrograma = cargarListaTipoClasificador("003", 2, naturaleza);
	}

	/**
	 * 
	 * Carga la lista listaSupPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaSupProgramaE() {
		listaSupProgramaE = listaSupPrograma;
	}

	/**
	 * 
	 * Carga la lista listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoProducto() {
		listaCodigoProducto = cargarListaTipoClasificador("004", 2, naturalezaCuenta);
		cargarListaCodigoProductoE();
	}

	/**
	 * 
	 * Carga la lista listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoProductoE() {
		listaCodigoProductoE = listaCodigoProducto;
	}

	/**
	 * 
	 * Carga la lista listaCodigoCCPET
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCCPET() {
		listaCodigoCCPET = cargarListaTipoClasificador("006", 2, naturalezaCuenta);
		cargarListaCodigoCCPETE();
		listaCCPET = listaCodigoCCPET;
		listaCCPETE = listaCodigoCCPETE;
	}

	/**
	 * 
	 * Carga la lista listaCodigoCCPET
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCCPETE() {
		listaCodigoCCPETE = listaCodigoCCPET;
		cargarListaCodigoCPCDANE();
	}

	/**
	 * 
	 * Carga la lista listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCPCDANE() {
		listaCodigoCPCDANE = cargarListaTipoClasificador("007", 2, naturalezaCuenta);
		cargarListaCodigoCPCDANEE();
	}

	/**
	 * 
	 * Carga la lista listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCPCDANEE() {
		listaCodigoCPCDANEE = listaCodigoCPCDANE;
	}

	/**
	 * 
	 * Carga la lista listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoUnidEje() {
		listaCodigoUnidEje = cargarListaTipoClasificador("008", 2, naturaleza);
		cargarListaCodigoUnidEjeE();
		listaunidadEjecutora = listaCodigoUnidEje;
		listaunidadEjecutoraE = listaCodigoUnidEjeE;
	}

	/**
	 * 
	 * Carga la lista listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoUnidEjeE() {
		listaCodigoUnidEjeE = cargarListaTipoClasificador("008", 2, naturaleza);
	}

	/**
	 * 
	 * Carga la lista listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoFuente() {
		listaCodigoFuente = cargarListaTipoClasificador("009", 2, naturaleza);
	}

	/**
	 * 
	 * Carga la lista listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoFuenteE() {
		listaCodigoFuenteE = cargarListaTipoClasificador("009", 2, naturaleza);
	}

	/**
	 * 
	 * Carga la lista listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCCPETRega() {
		listaCodigoCCPETRega = cargarListaTipoClasificador("010", 2, naturaleza);
		cargarListaCodigoCCPETRegaE();
	}

	/**
	 * 
	 * Carga la lista listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaCodigoCCPETRegaE() {
		listaCodigoCCPETRegaE = listaCodigoCCPETRega;
	}

	/**
	 * 
	 * Carga la lista listaPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPoliticaPublica() {
		listaPoliticaPublica = cargarListaTipoClasificador("011", 2, naturaleza);
		cargarListaPoliticaPublicaE();
	}

	/**
	 * 
	 * Carga la lista listaPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaPoliticaPublicaE() {
		listaPoliticaPublicaE = listaPoliticaPublica;
	}

	/**
	 * 
	 * Carga la lista listaDetalleSectorial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaDetalleSectorial() {
		listaDetalleSectorial = cargarListaTipoClasificador("012", 2, naturaleza);
		cargarListaDetalleSectorialE();
	}

	/**
	 * 
	 * Carga la lista listaDetalleSectorial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaDetalleSectorialE() {
		listaDetalleSectorialE = listaDetalleSectorial;
	}

	/*
	 * 
	 */
	public void cargarListaTipoClasificador() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL45084.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CODIGOCLASE.getName(), claseClasificador);

		listaTipoClasificador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		cargarListaTipoClasificadorE();
	}

	/*
	 * 
	 */
	public void cargarListaTipoClasificadorE() {
		listaTipoClasificadorE = listaTipoClasificador;
	}

	/*
	 * 
	 */
	public void cargarListaClaseClasificador() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL45098.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put("KEY_COMPANIA", compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);

		listaClaseClasificador = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGOCLASE");
	}

	/*
	 * 
	 */
	public void cargarListaClaseClasificadorE() {
		listaClaseClasificadorE = listaClaseClasificador;
	}

	/*
	 * 
	 */
	public void cargarListaTipoClasificadorNu() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL45084.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CODIGOCLASE.getName(), "009");

		listaTipoClasificadorNu = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		cargarListaTipoClasificadorNuE();
	}

	/*
	 * 
	 */
	public void cargarListaTipoClasificadorNuE() {
		listaTipoClasificadorNuE = listaTipoClasificadorNu;
	}

	/**
	 * 
	 * Carga la lista listaEquivalenteCuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEquivalenteCuipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL1891001.getValue());
		Map<String, Object> param = new TreeMap<>();

		listaEquivalenteCuipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		cargarListaEquivalenteCuipoE();

	}

	/**
	 * 
	 * Carga la lista listaEquivalenteCuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaEquivalenteCuipoE() {
		listaEquivalenteCuipoE = listaEquivalenteCuipo;
	}

	/**
	 * 
	 */
	public void cargarListaclaseclasificadormod() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL1883005.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANIO.getName(), anio);

		listaclaseclasificadormod = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

		listaclaseclasificadormodE = listaclaseclasificadormod;
	}

	public void cargarListatipoclasificadorpost() {
		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL1884058.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.CLASECLASIFICADOR.getName(),
				registro.getCampos().get("CODIGOCLASIFICADOR").toString());
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.PAGINICIO.getName(), 0);
		param.put(GeneralParameterEnum.PAGTAMANIO.getName(), 10);

		listatipoclasificadorpost = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
		listatipoclasificadorpostE = listatipoclasificadorpost;
	}

	public void cargarListaSfactualiza() {
		UrlBean urlBean;

		urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL1884060.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.PAGINICIO.getName(), 0);
		param.put(GeneralParameterEnum.PAGTAMANIO.getName(), 10);

		listaSfactualiza = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				GeneralParameterEnum.CODIGO.getName());
	}

	private void cargarTablaTmp(String codigo) {
		try {
			EjbCGRCero.cargarTablaTempActualizaciones(compania, anio, codigo);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void cargarListaApropiaciones() {
		UrlBean urlBean;
		if (!compania.equals("")) {
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.APROPIACIONES_INICIALES_PPTAL.getGridKey());
			Map<String, Object> param = new TreeMap<>();
			param.put("COMPANIA", compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			listaApropiaciones = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					GeneralParameterEnum.CODIGO.getName());

			cargarListaTipoClasificadorNu();
			cargarListaTipoClasificadorNuE();
		}
	}

	/**
	 * 
	 * Carga la lista listaReservaApropiacionEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaReservaApropiacionEquivalente() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL34061.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaReservaApropiacionEquivalente = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}

	/**
	 * 
	 * Carga la lista listaReservaApropiacionEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaReservaApropiacionEquivalenteE() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL34061.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaReservaApropiacionEquivalenteE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReservaCajaEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaReservaCajaEquivalente() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL34061.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaReservaCajaEquivalente = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
				param, true, GeneralParameterEnum.CODIGO.getName());

	}

	/**
	 * 
	 * Carga la lista listaReservaCajaEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaReservaCajaEquivalenteE() {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL34061.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaReservaCajaEquivalenteE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
				param, true, GeneralParameterEnum.CODIGO.getName());

	}
	
	  /**
	   * 
	   * Carga la lista listaDetalleSectorials
	   *
	   */
	public void cargarListaDetalleSectorials(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					ConfigurarplanpresupuestalControladorUrlEnum.URL45117
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.ANIO.getName(), anio);
    	param.put(GeneralParameterEnum.VALOR.getName(), naturaleza.equals("D")?"-1":"0");
    	

    	listaDetalleSectorials = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
    			GeneralParameterEnum.CODIGO.getName());
	}
	/**
	 * 
	 * Carga la lista listaDetalleSectorials
	 *
	 */
	public void  cargarListaDetalleSectorialsE(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					ConfigurarplanpresupuestalControladorUrlEnum.URL45117
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.ANIO.getName(), anio);
    	param.put(GeneralParameterEnum.VALOR.getName(), naturaleza.equals("D")?"-1":"0");
    	

    	listaDetalleSectorialsE = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
    			GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control TipoVigencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarTipoVigencia() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarAnio() {
		// <CODIGO_DESARROLLADO>
		cargarListaSfconfiggeneral();
		cargarListaSfarbol();
		cargarListaSffuenterecurso();
        cargarListaApropiaciones();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Naturaleza
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarNaturaleza() {
		// <CODIGO_DESARROLLADO>
		naturalezaCuenta = naturaleza;
		cargarListaSfconfiggeneral();
		cargarListaSfarbol();
		cargarListaSffuenterecurso();
		cargarListaDetalleSectorials(); 
		cargarListaDetalleSectorialsE();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Regalias
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarRegalias() {
		// <CODIGO_DESARROLLADO>
		cargarListaSfconfiggeneral();
		cargarListaSfarbol();
		cargarListaSffuenterecurso();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Regalias
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarRubro() {
		// <CODIGO_DESARROLLADO>
		cargarListaSfconfiggeneral();
		cargarListaSfarbol();
		cargarListaSffuenterecurso();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSector(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSector
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPrograma(SelectEvent event) {
		Registro registroAuxP = (Registro) event.getObject();
		registro.getCampos().put("PROGRAMA", registroAuxP.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSupPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSupPrograma(SelectEvent event) {
		Registro registroAuxSP = (Registro) event.getObject();
		registro.getCampos().put("SUBPROGRAMA", registroAuxSP.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaSupPrograma
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSupProgramaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProducto(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("COD_PROD_CUIPO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoProducto
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoProductoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCCPET
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPET(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CCPET", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCCPET
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCDANE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGO_CPC", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCPCDANE
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCPCDANEE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoUnidEje(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGOUNIDADEJE", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoUnidEje
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoUnidEjeE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFuente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("FUENTE_CUIPO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFuente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFuenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETRega(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("CODIGOCCPETREGA", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCodigoCCPETRega
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoCCPETRegaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublica(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("POLITCA_PUBLICA_CUIPO", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaPoliticaPublica
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaPublicaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaDetalleSectorial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("DETALLE_SECTORIAL", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaDetalleSectorial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorialE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	public void seleccionarFilaTipoClasificador(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
	}

	public void seleccionarFilaTipoClasificadorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	public void seleccionarFilaClaseClasificador(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.CLASECLASIFICADOR.getName(),
				registroAux.getCampos().get(GeneralParameterEnum.CODIGOCLASE.getName()));
		claseClasificador = extraerString(registroAux.getCampos().get(GeneralParameterEnum.CODIGOCLASE.getName()));
		cargarListaTipoClasificador();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaClaseClasificador
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */

	public void seleccionarFilaClaseClasificadorE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if (registroAux.getCampos().get("CODIGOCLASE") != null) {
			auxiliar = extraerString(registroAux.getCampos().get(GeneralParameterEnum.CODIGOCLASE.getName()));

		} else {
			auxiliar = "";
		}
		claseClasificador = auxiliar;

		cargarListaTipoClasificador();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoClasificadorNu
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoClasificadorNu(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSfFuenteRecurso.getCampos().put("CODIGOEQUIVALENTE", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaTipoClasificadorNu
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTipoClasificadorNuE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaEquivalenteCuipo
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaEquivalenteCuipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubSfTipoVigencia.getCampos().put("EQUIVALENTE_CUIPO", registroAux.getCampos().get("CODIGO"));
	}

	public void seleccionarFilaEquivalenteCuipoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
	}

	public void seleccionarFilaclaseclasificadormod(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if (registroAux.getCampos().get("CODIGO") == null) {
			registro.getCampos().put("CODIGOCLASIFICADOR", "");
			registro.getCampos().put("NOMBRECLASIFICADOR", "");
			listaSfactualiza = null;
		} else {
			auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
			registro.getCampos().put("CODIGOCLASIFICADOR", auxiliar);
			registro.getCampos().put("NOMBRECLASIFICADOR", extraerString(registroAux.getCampos().get("NOMBRE")));
			cargarTablaTmp(auxiliar);
			cargarListaSfactualiza();
			cargarListatipoclasificadorpost();
		}
	}

	public void seleccionarFilatipoclasificadorpost(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
		registroSubSfActualiza.getCampos().put("CODIGONUEVO", auxiliar);
	}

	public void seleccionarFilatipoclasificadorpostE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = extraerString(registroAux.getCampos().get("CODIGO"));
		registroSubSfActualiza.getCampos().put("CODIGONUEVO", auxiliar);
	}

	public void seleccionarFilaCCPET(SelectEvent event) {
		Registro reg = (Registro) event.getObject();
		auxiliar = extraerString(reg.getCampos().get("CODIGO"));
		registroSubapropiaciones.getCampos().put("CODIGO_CCPET", auxiliar);
	}

	public void seleccionarFilaprogramamga1(SelectEvent event) {
		Registro reg = (Registro) event.getObject();
		auxiliar = extraerString(reg.getCampos().get("CODIGO"));
		registroSubapropiaciones.getCampos().put("PROGRAMA", auxiliar);
	}

	public void seleccionarFilaunidadEjecutora(SelectEvent event) {
		Registro reg = (Registro) event.getObject();
		auxiliar = extraerString(reg.getCampos().get("CODIGO"));
		registroSubapropiaciones.getCampos().put("CODIGOUNIDADEJE", auxiliar);
	}

	public void seleccionarFilaCCPETE(SelectEvent event) {
		Registro reg = (Registro) event.getObject();
		auxiliar = extraerString(reg.getCampos().get("CODIGO"));
		registroSubapropiaciones.getCampos().put("CODIGO_CCPET", auxiliar);
	}

	public void seleccionarFilaprogramamga1E(SelectEvent event) {
		Registro reg = (Registro) event.getObject();
		auxiliar = extraerString(reg.getCampos().get("CODIGO"));
		registroSubapropiaciones.getCampos().put("PROGRAMA", auxiliar);
	}

	public void seleccionarFilaunidadEjecutoraE(SelectEvent event) {
		Registro reg = (Registro) event.getObject();
		auxiliar = extraerString(reg.getCampos().get("CODIGO"));
		registroSubapropiaciones.getCampos().put("CODIGOUNIDADEJE", auxiliar);
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReservaApropiacionEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReservaApropiacionEquivalente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("EQUIVALENTEAPROPIACION", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReservaApropiacionEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReservaApropiacionEquivalenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReservaCajaEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReservaCajaEquivalente(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("EQUIVALENTECAJA", registroAux.getCampos().get("CODIGO"));
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReservaCajaEquivalente
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReservaCajaEquivalenteE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = (String) registroAux.getCampos().get("CODIGO");
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDetalleSectorials
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorials(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registroSubapropiaciones.getCampos().put("DETALLESECTORIAL", registroAux.getCampos().get("CODIGO"));
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaDetalleSectorials
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaDetalleSectorialsE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
	}

	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton descargarPlantillaExcel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirdescargarPlantillaExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga1 = null;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cargarPlantillaExcel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimircargarPlantillaExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga1 = null;
		// </CODIGO_DESARROLLADO>
	}

	public void oprimiractualizar() {
		// se captura la lista y se toman los datos los cuales se enviaran a el paquete
		// que
		// hara las actualizaciones de los detalles que cumplan con las condiciones
		try {
			int aplicacion = ejbSysmanUtil.aplicacionCuenta(compania, anio,
					registro.getCampos().get("CODIGOCLASIFICADOR").toString(), "");
			if (aplicacion != 2) {

				JsfUtil.agregarMensajeInformativo(
						"El Clasificador " + registro.getCampos().get("CODIGOCLASIFICADOR").toString() + " "
								+ registro.getCampos().get("NOMBRECLASIFICADOR").toString()
								+ " No se encuentra configurado como detalle");
			}
			EjbCGRCero.actualizarTipoClasificadoresAnuevos(compania, anio,
					registro.getCampos().get("CODIGOCLASIFICADOR").toString());
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

			cargarListaSfactualiza();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * metodo que toma la accion de cargar para las apropiaciones iniciales
	 * 
	 * @throws IOException
	 */
	public void oprimircargarApropiaciones() throws IOException {
		archivoDescarga1 = null;
		Workbook workbook = null;
		cadena = "";
		valGral = false;
		try (FileInputStream file = new FileInputStream(contArchivocargarApropiaciones.getArchivo());) {
			contArchivocargarExcel = contArchivocargarApropiaciones;
			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					JsfUtil.agregarMensajeInformativo("No es un archivo de .xls");
					return;
				}
				Sheet sheet = workbook.getSheet("Apropiaciones");
				long i = 1;
				contador = 0;
				for (Row row : sheet) {
					contador++;
				}
				salida = null;
				for (Row row : sheet) {

					if (!validarCelda(row.getCell(0))) {
						break;
					}
					// carga cada 50 registros cuando la cantidad de los mismos son mas autor:cperez
					capturaDatosExcel(row);
					if (50 * (i / 50) == i || (i >= contador && !"".equals(cadena))) {
						if (!cadena.equals("")) {
							cargarDatosApropiaciones();
							cadena = "";
						}
					}

					i = i + 1;
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				if (!(salida.equals("null"))) {
					out.write(salida.substring(5).getBytes());
					archivoDescarga1 = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
							"ErroresApropiacionesIniciales.txt", "txt/html");
				}
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
				cargarListaApropiaciones();
			}
		} catch (JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			workbook.close();
			cargarListaApropiaciones();
		}
	}

	public void oprimirdescargar() throws IOException {
		archivoDescarga1 = null;
		HSSFWorkbook workbook = new HSSFWorkbook();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			HSSFSheet excelSheet = null;
			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Tahoma");
			font.setBold(true);

			// Tama�o de letra
			font.setFontHeightInPoints((short) 8);

			Map<String, Object> param = new HashMap<>();
			// lista de la grilla de la pesta�a apropiaciones iniciales
			param.put("COMPANIA", compania);
			param.put(GeneralParameterEnum.ANO.getName(), anio);
			List<Registro> listaApropiacionesTodosGrilla;

			listaApropiacionesTodosGrilla = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GenericUrlEnum.APROPIACIONES_INICIALES_PPTAL.getReadKey())
									.getUrl(),
							param));

			// demo lista desplegable separada
			addValidationToSheetApropiaciones(anio, workbook, excelSheet, listaApropiacionesTodosGrilla, 'A', 1, 10000,
					"Apropiaciones");
			workbook.write(out);

			archivoDescarga1 = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Actualizar Rubros Apropiaciones Iniciales.xls");
		} catch (IOException | SystemException | JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			workbook.close();
		}
	}

	// </METODOS_BOTONES>
	public void cambiarClaseClasificadorC(int rowNum) {
		claseClasificador = extraerString(
				listaSfarbol.getDatasource().get(rowNum).getCampos().get("CLASECLASIFICADOR"));
		if (claseClasificador.equals("") || claseClasificador == null) {
			listaSfarbol.getDatasource().get(rowNum).getCampos().put("TIPOCLASIFICADOR", "");
		}
		cargarListaTipoClasificador();
	}

	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_SUBFORM>
	public void activarEdicionSfconfiggeneral(Registro reg) throws SystemException {
		setIndiceSfconfiggeneral(listaSfconfiggeneral.getRowIndex());

		int validado = validaRubroPadreOAux(reg.getCampos().get("CODIGO").toString());

		if (validado == 0) {
			bloqSector = true;
			bloqPrograma = true;
			bloqSupPrograma = true;
			bloqCodigoProducto = true;
			bloqCodigoBPIN = true;
			bloqCodigoCCPET = true;
			bloqCodigoCPCDANE = true;
			bloqCodigoUnidEje = true;
			bloqCodigoFuente = true;
			bloqCodigoCCPETRega = true;
			bloqPoliticaPublica = true;
			bloqDetalleSectorial = true;
			bloqTipoRec = true;
			bloqVigAnt = true;
			bloqSituaF = true;
			bloqTransfer = true;
			bloqDestEsp = true;
			bloqTipoNor = true;
			bloqNumNorm = true;
			bloqFecNorm = true;
		} else {
			cargarConfiguracionCuipo(reg.getCampos().get("CODIGO").toString());
			bloqTipoRec = false;
			bloqVigAnt = false;
			bloqSituaF = false;
			bloqTransfer = false;
			bloqDestEsp = false;
			bloqTipoNor = false;
			bloqNumNorm = false;
			bloqFecNorm = false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int validaRubroPadreOAux(String cuenta) {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.CODIGO.getName(), cuenta);

		UrlBean url = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL45028.getValue());

		try {
			Registro regAux = RegistroConverter.toRegistro(requestManager.get(url.getUrl(), param));

			return (int) regAux.getCampos().get("CUENTA");
		} catch (SystemException e) {
			return 0;
		}
	}

	/**
	 * Metodo de insercion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void agregarRegistroSubafconfiggeneral() {

	}

	/**
	 * Metodo de insercion del formulario Sffuenterecurso
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void agregarRegistroSubSffuenterecurso() {
	}

	/**
	 * Metodo de insercion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void agregarRegistroSubSfarbol() {

	}

	/**
	 * Metodo de insercion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void agregarRegistroSubSfConfigGeneral() {

	}

	/**
	 * Metodo de edicion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSfconfiggeneral(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PRESUPUESTALCLASI.getUpdateKey());
			cargarConfiguracionCuipo(extraerString(reg.getCampos().get("CODIGO")));
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			reg.getLlave().put("KEY_COMPANIA", compania);
			reg.getCampos().remove("ANO");
			reg.getCampos().remove("CODIGO");
			reg.getCampos().remove("NOMBRE");
			reg.getCampos().remove("TIPOVIGENCIA");
			reg.getCampos().remove("COD_PROD_CUIPO");
			reg.getCampos().remove("CODIGO_BPIN");
			reg.getCampos().remove("CODIGO_CPC");
			reg.getCampos().remove("FUENTE_CUIPO");
			reg.getCampos().remove("COMPANIA");
			reg.getCampos().remove("NOMBREVIGENCIA");
			reg.getCampos().remove("MOVIMIENTO");
			// reg.getCampos().remove("TERCERO_CHIP");

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSfconfiggeneral();
		}
	}

	/**
	 * Metodo de insercion del formulario Sftipovigencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void agregarRegistroSubSftipovigencia() {
		try {
			int conteo;
			conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, "TIPOVIGENCIA;",
					registroSubSfTipoVigencia.getCampos());
			listaSftipovigencia.load();
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));
			}
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			registroSubSfTipoVigencia = new Registro(new HashMap<String, Object>());
		}
	}

	public void agregarRegistroSubSfactualiza() {

	}

	/**
	 * Metodo de edicion del formulario Sftipovigencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSftipovigencia(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PTIPOVIGENCIA.getUpdateKey());
			cargarConfiguracionCuipo(extraerString(reg.getCampos().get("CODIGO")));
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			reg.getCampos().remove("NOMBRECUIPO");
			reg.getLlave().remove("KEY_ANO");
			reg.getLlave().remove("KEY_COMPANIA");

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSftipovigencia();
		}

	}

	/**
	 * Metodo de eliminacion del formulario Sftipovigencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSftipovigencia(Registro reg) {
		try {
			int conteo;
			conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, "TIPOVIGENCIA;", reg.getLlave());
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			}
			listaSftipovigencia.load();
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Sftipovigencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cancelarEdicionSftipovigencia() {
		cargarListaSftipovigencia();
	}

	/**
	 * Metodo de edicion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSfarbol(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.PLAN_PRESUPUESTALARBOL.getUpdateKey());
			cargarConfiguracionCuipo(extraerString(reg.getCampos().get("CODIGO")));
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
			reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
			reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			reg.getCampos().remove("NOMBREVIGENCIA");
			reg.getCampos().remove("NOMCLASECLASIFICADOR");
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSfarbol();
		}
	}

	public void cancelarEdicionApropiaciones() {
		cargarListaApropiaciones();
	}

	/**
	 * Metodo de edicion del formulario Sffuenterecurso
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void editarRegSubSffuenterecurso(RowEditEvent event) {

		Registro reg = (Registro) event.getObject();
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.FUENTE_RECURSOSCUIPO.getUpdateKey());
			cargarConfiguracionCuipo(extraerString(reg.getCampos().get("CODIGO")));
			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			reg.getLlave().put("KEY_COMPANIA", compania);
			reg.getCampos().remove("CODIGO_CCPET");

			reg.getCampos().remove("ANO");
			reg.getCampos().remove("CODIGO");
			reg.getCampos().remove("NOMBRE");
			reg.getCampos().remove("TIPOVIGENCIA");
			reg.getCampos().remove("COD_PROD_CUIPO");
			reg.getCampos().remove("CODIGO_BPIN");
			reg.getCampos().remove("CODIGO_CPC");
			reg.getCampos().remove("FUENTE_CUIPO");
			reg.getCampos().remove("COMPANIA");

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSfconfiggeneral();
		}

	}

	public void editarRegSubSfactualiza(RowEditEvent event) {
		// ljdiaz
		Registro reg = (Registro) event.getObject();
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ConfigurarplanpresupuestalControladorUrlEnum.URL1884062.getValue());
			reg.getCampos().remove("NOMBRE");
			registroSubSfActualiza.getCampos().put("TIPOCLASIFICADOR", reg.getCampos().get("CODIGO"));

			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), registroSubSfActualiza.getCampos(),
					registroSubSfActualiza.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaSfconfiggeneral();
		}
	}

	public void editarRegSubApropiaciones(RowEditEvent event) {
		Registro reg = (Registro) event.getObject();
		try {
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GenericUrlEnum.APROPIACIONES_INICIALES_PPTAL.getUpdateKey());

			reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			// SE MANIPULA LA FEHCA PARA DEJARLA COMPATIBLE CON LA BD
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
			reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), formatFecha.format(new Date()));
			
			// se agregan llaves faltantes
			reg.getLlave().put("KEY_ANO", anio);
			reg.getLlave().put("KEY_COMPANIA", compania);
			reg.getLlave().put("KEY_CODIGO", reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
			reg.getLlave().put("KEY_TERCERO", reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()));
			reg.getLlave().put("KEY_SUCURSAL", reg.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()));
			reg.getLlave().put("KEY_AUXILIAR", reg.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()));
			reg.getLlave().put("KEY_CENTRO_COSTO", reg.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()));
			reg.getLlave().put("KEY_REFERENCIA", reg.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()));
			reg.getLlave().put("KEY_FUENTE_RECURSO", reg.getCampos().get(GeneralParameterEnum.FUENTE_RECURSO.getName()));
			

			reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.ANO.getName());
			reg.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
			reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
			reg.getCampos().remove(GeneralParameterEnum.CENTRO_COSTO.getName());
			reg.getCampos().remove(GeneralParameterEnum.AUXILIAR.getName());
			reg.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
			reg.getCampos().remove(GeneralParameterEnum.FUENTE_RECURSO.getName());
			reg.getCampos().remove("APROPIACIONINICIAL");
			reg.getCampos().remove("NOMBRE_CC");
			reg.getCampos().remove("NOMBRE_AUX");
			reg.getCampos().remove("NOMBRE_REF");
			reg.getCampos().remove("NOMBRE_FUE");
			reg.getCampos().remove("NATURALEZA");

			reg.getCampos().remove("ID");
			reg.getLlave().remove("KEY_ID");
			reg.getCampos().remove("TERCERO");
			reg.getCampos().remove("SUCURSAL");
			
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), reg.getCampos(), reg.getLlave());

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));

		} catch (SystemException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		} finally {
			cargarListaApropiaciones();
		}
	}

	/**
	 * Metodo de eliminacion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSfconfiggeneral(Registro reg) {
		try {
			int conteo;
			conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, "PLAN_PRESUPUESTAL", reg.getLlave());
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			}
			listaSfconfiggeneral.load();
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo de eliminacion del formulario Sffuenterecurso
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSffuenterecurso(Registro reg) {
		try {
			int conteo;
			conteo = Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, "FUENTE_RECURSOS", reg.getLlave());
			if (conteo > 0) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_ELIMINADO"));
			}
			listaSffuenterecurso.load();
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | SQLException
				| NamingException ex) {
			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Sffuenterecurso
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cancelarEdicionSffuenterecurso() {
		cargarListaSffuenterecurso();
		cargarListaSftipovigencia();
	}

	/**
	 * Metodo de eliminacion del formulario Sfconfiggeneral
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @param reg registro seleccionado en el subformulario
	 */
	public void eliminarRegSubSSfarbol(Registro reg) {
		//
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Sfconfiggeneral
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cancelarEdicionSfconfiggeneral() {
		// cargarListaSfconfiggeneral();
		// cargarListaSfarbol();
		// cargarListaSffuenterecurso();
		// cargarListaSftipovigencia();
	}

	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para
	 * el subformulario Sfconfiggeneral
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cancelarEdicionSfarbol() {
	}

	public void cancelarEdicionSfactualiza() {
		cargarListaSfactualiza();
	}

	// </METODOS_SUBFORM>
	// <METODOS_ADICIONALES>
	// </METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// 7737156 mperez - Permite verificar si se deben habilitar los campos de
		// equivalente CUIPO
		try {
			manejaEquivalenteCUIPO = ("SI".equals(SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania,
							"MANEJA EQUIVALENTE CUIPO EN FUENTES PARA VIGENCIA ANTERIORES", "3", new Date(), true),
					"NO")) ? false : true);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	@Override
	public void cargarRegistro() {
		// <CODIGO_DESARROLLADO>
		precargarRegistro();

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes() {
		// <CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion del registro TODO
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Detalle en la vista
	 *
	 */
	public void oprimirDetalle() {
		Map<String, Object> parametros = new HashMap<>();

		parametros.put("anio", anio);

		Direccionador direccionador = new Direccionador();
		direccionador
				.setNumForm(Integer.toString(GeneralCodigoFormaEnum.CONFIGURAR_CODIGOS_CUIPO_CONTROLADOR.getCodigo()));
		direccionador.setParametros(parametros);

		SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
	}

	public void oprimirCargar() {
		archivoDescarga1 = null;
		Workbook workbook = null;
		cadena = "";
		valGral = false;
		try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo());) {

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheet("Arbol");
				long i = 1;
				contador = 0;
				for (Row row : sheet) {
					contador++;
				}
				salida = null;
				for (Row row : sheet) {

					if (!validarCelda(row.getCell(0))) {
						break;
					}
					// carga cada 50 registros cuando la cantidad de los mismos son mas autor:cperez
					capturaDatosExcel(row);
					if (50 * (i / 50) == i || (i >= contador && !"".equals(cadena))) {
						if (!cadena.equals("")) {
							cargarDatos();
							cadena = "";
						}
					}

					i = i + 1;
				}
				generarArchivo(salida, "ErroresTipoClasificadoes");
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void oprimirCargarGnral() {
		archivoDescarga1 = null;
		Workbook workbook = null;
		cadena = "";
		valGral = true;
		try (FileInputStream file = new FileInputStream(contArchivocargarExcelGral.getArchivo());) {

			if (validarArchivo()) {

				String rutaArchivo = contArchivocargarExcelGral.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheet("ConfiguracionPPTAL");
				long i = 1;
				contador = 0;
				for (Row row : sheet) {
					contador++;
				}
				salida = null;
				for (Row row : sheet) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}
					capturaDatosExcel(row);
					if (50 * (i / 50) == i || (i >= contador && !"".equals(cadena))) {
						if (!cadena.equals("")) {
							cargarDatos();
							cadena = "";
						}
					}

					i = i + 1;
				}
				salida = salida.replace("null", "");
				generarArchivo(salida, "LogActualizar");
				JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	
	private void generarArchivo(String salidaTexto2, String archivoNom)
    {

		archivoDescarga1 = null;
        try
        {
            if (salidaTexto2 != null)
            {
                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(salidaTexto2);
                archivoDescarga1 = JsfUtil.getArchivoDescarga(archivo,
                                archivoNom + ".txt");
            }
        }
        catch (IOException | JRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

	/**
	 * Verifica el valor de la celda y retorna false si esta vacia.
	 * 
	 * @param celda Objeto de tipo <code>Cell</code>
	 * @return false si la celda esta vacia.
	 */
	private boolean validarCelda(Cell celda) {
		if (celda == null) {
			return false;
		}

		return !celda.getStringCellValue().isEmpty();
	}

	private void capturaDatosExcel(Row row) {

		if (row.getRowNum() > 0) {

			for (int i = 0; i < row.getLastCellNum(); i++) {
				String val = null;

				val = row.getCell(i) + "";
				int cuantosPuntos = val.toString().split("\\.").length;
				String[] cuantosPuntosArray = val.toString().split("\\.");
				if (cuantosPuntos == 2) {
					if (val.contains(".0"))
						val = val.substring(0, val.length() - 2);
				}
				//lvega
				if(i>=12 && i<=17) {
					val = (val == null || val.isEmpty() || val.equals("null"))?"0":val;
				}
				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}

	}

	private void cargarDatos() {

		try {
			String parametro = (SysmanFunciones.esBdSqlServer()) ? cadena.replace("TO_CLOB(", "").replace(")", "")
					: cadena;
			if (valGral != true) {

				salida = salida + EjbCGRCero.actclaarbol(compania, parametro, SessionUtil.getUser().getCodigo());
			} else {
				salida = salida + EjbCGRCero.actualizarTipoClasificadorRubro(compania, Integer.parseInt(anio), naturaleza,
						parametro, SessionUtil.getUser().getCodigo());
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/*
	 * Se crea el metodo para almacenar las apropiaciones inciales que vienen en el
	 * excel
	 */
	private void cargarDatosApropiaciones() {
		try {
			String parametro = (SysmanFunciones.esBdSqlServer()) ? cadena.replace("TO_CLOB(", "").replace(")", "")
					: cadena;

			salida = salida
					+ EjbCGRCero.actclaApropiaciones(compania, anio, parametro, SessionUtil.getUser().getCodigo());

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {
		if (valGral != true) {
			if (contArchivocargarExcel.getArchivo() == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
				return false;
			} else {
				return true;
			}
		} else if (contArchivocargarExcelGral.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}

	public void oprimirExcel() throws IOException {
		// <CODIGO_DESARROLLADO>
		archivoDescarga1 = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Map<String, Object> param = new TreeMap<>();
			param.put("KEY_COMPANIA", compania);
			param.put("NATURALEZA", naturaleza);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);
			List<Registro> misRegistrosTemp;
			// como es una consulta para un paginador, no carga la lista crear un dss
			// con la lista completa es la misma consulta pero tipo de recurso diferente.
			if (regalias.equals("R")) {
				misRegistrosTemp = RegistroConverter.toListRegistro(requestManager.getList(
						UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ConfigurarplanpresupuestalControladorUrlEnum.URL45113.getValue())
								.getUrl(),
						param));

			} else {
				misRegistrosTemp = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														GenericUrlEnum.PLAN_PRESUPUESTALCLASI.getReadKey())
												.getUrl(),
										param));
			}

			// se agrega la lista de configuracion de plan pptal
			addReporte2(workbook, misRegistrosTemp, 'A', "ConfiguracionPPTAL");

			workbook.write(out);

			archivoDescarga1 = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Actualizar Rubros Arbol.xls");

		} catch (IOException | JRException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			workbook.close();
		}

		// </CODIGO_DESARROLLADO>
	}

	public static void addReporte2(Workbook workbook, List<Registro> options, char column, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		row = optionsSheet.createRow(0);

		CellStyle cellStyle = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);
		cellStyle.setWrapText(true);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		cell = row.createCell(0);
		cell.setCellValue("RUBRO");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(1);
		cell.setCellValue("NOMBRE");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(2);
		cell.setCellValue("TIPOVIGENCIA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(3);
		cell.setCellValue("SECTOR");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(4);
		cell.setCellValue("PROGRAMA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(5);
		cell.setCellValue("SUBPROGRAMA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(6);
		cell.setCellValue("CODIGOPRODUCTO");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(7);
		cell.setCellValue("CODIGOBPIN");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(8);
		cell.setCellValue("CODIGOCCPET");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(9);
		cell.setCellValue("CODIGOCPCDANE");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(10);
		cell.setCellValue("CODIGOUNIDADEJE");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(11);
		cell.setCellValue("CODIGOFUENTE");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(12);
		cell.setCellValue("CODIGOCCPETREGA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(13);
		cell.setCellValue("CODIGOPOLITICA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(14);
		cell.setCellValue("CODIGODETALLES");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(15);
		cell.setCellValue("TIPORECURSO");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(16);
		cell.setCellValue("VIGENCIA ANTERIOR");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(17);
		cell.setCellValue("SIN SITUACION FONDOS");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(18);
		cell.setCellValue("TRASNFERENCIA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(19);
		cell.setCellValue("TERCERO");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(20);
		cell.setCellValue("DESTINACION ESPECIFICA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(21);
		cell.setCellValue("TIPO NORMA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(22);
		cell.setCellValue("NUMERO NORMA");
		cell.setCellStyle(cellStyle);

		cell = row.createCell(23);
		cell.setCellValue("FECHA NORMA");
		cell.setCellStyle(cellStyle);

		rowIndex = 1;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("NOMBRE").toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("NOMBREVIGENCIA").toString());

			cell = row.createCell(3);
			if (option.getCampos().get("SECTOR") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("SECTOR").toString());
			}

			cell = row.createCell(4);
			if (option.getCampos().get("PROGRAMA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("PROGRAMA").toString());
			}

			cell = row.createCell(5);
			if (option.getCampos().get("SUBPROGRAMA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("SUBPROGRAMA").toString());
			}
			cell = row.createCell(6);
			if (option.getCampos().get("CODIGOPRODUCTO") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOPRODUCTO").toString());
			}
			cell = row.createCell(7);
			if (option.getCampos().get("CODIGOBPIN") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOBPIN").toString());
			}
			cell = row.createCell(8);
			if (option.getCampos().get("CODIGOCCPET") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOCCPET").toString());
			}
			cell = row.createCell(9);
			if (option.getCampos().get("CODIGOCPCDANE") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOCPCDANE").toString());
			}
			cell = row.createCell(10);
			if (option.getCampos().get("CODIGOUNIDADEJE") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE").toString());
			}
			cell = row.createCell(11);
			if (option.getCampos().get("CODIGOFUENTE") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOFUENTE").toString());
			}
			cell = row.createCell(12);
			if (option.getCampos().get("CODIGOCCPETREGA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOCCPETREGA").toString());
			}
			cell = row.createCell(13);
			if (option.getCampos().get("POLITCA_PUBLICA_CUIPO") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("POLITCA_PUBLICA_CUIPO").toString());
			}
			cell = row.createCell(14);
			if (option.getCampos().get("DETALLE_SECTORIAL") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("DETALLE_SECTORIAL").toString());
			}

			cell = row.createCell(15);
			if (option.getCampos().get("TIPO_RECURSO") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("TIPO_RECURSO").toString());
			}

			cell = row.createCell(16);
			if (!(boolean) option.getCampos().get("RECAUDO_VA")) {
				cell.setCellValue("NO");
			} else {
				cell.setCellValue("SI");
			}

			cell = row.createCell(17);
			if (!(boolean) option.getCampos().get("CONSITUACIONFONDOS")) {
				cell.setCellValue("NO");
			} else {
				cell.setCellValue("SI");
			}

			cell = row.createCell(18);
			if (!(boolean) option.getCampos().get("TRANSFERENCIA")) {
				cell.setCellValue("NO");
			} else {
				cell.setCellValue("SI");
			}

			cell = row.createCell(19);
			if (option.getCampos().get("TERCERO_CHIP") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("TERCERO_CHIP").toString());
			}

			cell = row.createCell(20);
			if (!(boolean) option.getCampos().get("APLICA_DEST_ESPECIFICA")) {
				cell.setCellValue("NO");
			} else {
				cell.setCellValue("SI");
			}
			cell = row.createCell(21);
			if (option.getCampos().get("TIPO_NORMA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("TIPO_NORMA").toString());
			}
			cell = row.createCell(22);
			if (option.getCampos().get("NUMERO_NORMA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("NUMERO_NORMA").toString());
			}
			cell = row.createCell(23);
			if (option.getCampos().get("FECHA_NORMA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("FECHA_NORMA").toString());
			}
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Crear en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @throws IOException
	 *
	 */
	public void oprimirCrear() throws IOException {
		// <CODIGO_DESARROLLADO>
		archivoDescarga1 = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			// HSSFSheet excelSheet = workbook.createSheet("Plantilla");
			HSSFSheet excelSheet = null;
			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Calibri");
			font.setBold(true);

			// Tamaño±o de letra
			font.setFontHeightInPoints((short) 8);

			Map<String, Object> param = new HashMap<>();
			// lista de la grilla de la pestaña arbol
			param.put("KEY_COMPANIA", compania);
			param.put("NATURALEZA", naturaleza);
			param.put(GeneralParameterEnum.ANIO.getName(), anio);
			List<Registro> listaArbolTodosGrilla;
			// como es una consulta para un paginador, no carga la lista crear un dss
			// con la lista completa es la misma consulta pero tipo de recurso diferente.
			if (regalias.equals("R")) {
				listaArbolTodosGrilla = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												GenericUrlEnum.PLAN_PRESUPUESTALARBOLREGA.getReadKey())
										.getUrl(),
								param));

			} else {
				listaArbolTodosGrilla = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														GenericUrlEnum.PLAN_PRESUPUESTALARBOL.getReadKey())
												.getUrl(),
										param));
			}
			// Lista compa�ia
			listaCompania = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarplanpresupuestalControladorUrlEnum.URL0001.getValue())
									.getUrl(),
							param));
			param.put("COMPANIA", compania);
			param.put("ANO", String.valueOf(SysmanFunciones.ano(new Date())));
			listaClasificador = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarplanpresupuestalControladorUrlEnum.URL0002.getValue())
									.getUrl(),
							param));

			listaTipoVigenciaArbol = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ConfigurarplanpresupuestalControladorUrlEnum.URL8542.getValue())
									.getUrl(),
							param));

			/*
			 * listaTipoClasificadorArbol = RegistroConverter .toListRegistro(
			 * requestManager.getList( UrlServiceUtil.getInstance()
			 * .getUrlServiceByUrlByEnumID(
			 * ConfigurarplanpresupuestalControladorUrlEnum.URL1884040.getValue())
			 * .getUrl(), param));
			 */
			// demo lista desplegable separada
			addValidationToSheet3(workbook, excelSheet, listaArbolTodosGrilla, 'A', 1, 10000, "Arbol");
			addValidationToSheet2(workbook, excelSheet, listaCompania, 'B', 1, 10000, "Compania");
			addValidationToSheet2(workbook, excelSheet, listaTipoVigenciaArbol, 'C', 1, 10000, "TipoVigencia");
			addValidationToSheet2(workbook, excelSheet, listaClasificador, 'D', 1, 10000, "Clasificador");
			// addValidationToSheet2(workbook, excelSheet, listaTipoClasificadorArbol, 'H',
			// 1, 10000, "TipoClasificador");

			workbook.write(out);

			archivoDescarga1 = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Actualizar Rubros Arbol.xls");

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Agregar una lista desplegable a la pÃ¡gina de la hoja
	 *
	 * Archivo de Excel del libro de trabajo @param, utilizado para agregar el
	 * nombre
	 * 
	 * @param targetSheet La pÃ¡gina de la hoja donde se encuentra la lista en
	 *                    cascada
	 * @param options     Datos en cascada ['Baidu', 'Alibaba']
	 * @param column      La columna de la lista desplegable comienza en'A '
	 * @param fromRow     fila de inicio del lÃ­mite desplegable
	 * @param endRow      lÃ­mite desplegable de la fila final
	 */
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
			int fromRow, int endRow, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";

		int rowIndex = 0;
		for (Registro option : options) {
			int columnIndex = 0;
			Row row = optionsSheet.createRow(rowIndex++);
			Cell cell = row.createCell(columnIndex++);
			Cell cell1 = row.createCell(columnIndex);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());
			cell1.setCellValue(option.getCampos().get("NOMBRE").toString());
		}

		createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
		optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, column - 'A', column - 'A');
		// targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}

	/**
	 * 
	 * @param workbook
	 * @param targetSheet
	 * @param options
	 * @param column
	 * @param fromRow
	 * @param endRow
	 * @param name
	 */
	public static void addValidationToSheet3(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
			int fromRow, int endRow, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);

		row = optionsSheet.createRow(0);

		cell = row.createCell(0);
		cell.setCellValue("COMPANIA");

		cell = row.createCell(1);
		cell.setCellValue("ANO");

		cell = row.createCell(2);
		cell.setCellValue("RUBRO");

		cell = row.createCell(3);
		cell.setCellValue("TIPOVIGENCIA");

		cell = row.createCell(4);
		cell.setCellValue("CLASE CLASIFICADOR");

		cell = row.createCell(5);
		cell.setCellValue("TIPO CLASIFICADOR");
		String compania_aux = SessionUtil.getCompania();
		rowIndex = 1;
		for (Registro option : options) {
			row = optionsSheet.createRow(rowIndex++);

			cell = row.createCell(0);
			cell.setCellValue(compania_aux);
			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("ANO").toString());
			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());
			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("TIPOVIGENCIA").toString());
			cell = row.createCell(4);

			if (option.getCampos().get("CLASECLASIFICADOR") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CLASECLASIFICADOR").toString());
			}
			cell = row.createCell(5);
			if (option.getCampos().get("TIPOCLASIFICADOR") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("TIPOCLASIFICADOR").toString());
			}
		}

		//		createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
		//		optionsSheet.protectSheet("Sysman10*");
		//		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
		//		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, (int) column - 'A',
		//				(int) column - 'A');
		//		targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}
	/**
	 * 
	 * @param workbook
	 * @param targetSheet
	 * @param options
	 * @param column
	 * @param fromRow
	 * @param endRow
	 * @param name
	 */
	public static void addValidationToSheetApropiaciones(String ano, Workbook workbook, Sheet targetSheet,
			List<Registro> options, char column, int fromRow, int endRow, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);

		row = optionsSheet.createRow(0);

		cell = row.createCell(0);
		cell.setCellValue("RUBRO");

		cell = row.createCell(1);
		cell.setCellValue("NOMBRE");
		
		cell = row.createCell(2);
		cell.setCellValue("TERCERO");

		cell = row.createCell(3);
		cell.setCellValue("SUCURSAL");

		cell = row.createCell(4);
		cell.setCellValue("CENTRO_COSTO");

		cell = row.createCell(5);
		cell.setCellValue("NOMBRE CENTRO_COSTO");

		cell = row.createCell(6);
		cell.setCellValue("AUXILIAR");

		cell = row.createCell(7);
		cell.setCellValue("NOMBRE AUXILIAR");

		cell = row.createCell(8);
		cell.setCellValue("REFERENCIA");

		cell = row.createCell(9);
		cell.setCellValue("NOMBRE REFERENCIA");

		cell = row.createCell(10);
		cell.setCellValue("FUENTE_RECURSO");

		cell = row.createCell(11);
		cell.setCellValue("NOMBRE FUENTE_RECURSO");

		cell = row.createCell(12);
		cell.setCellValue("CODIGO_CCPET");

		cell = row.createCell(13);
		cell.setCellValue("PROGRAMA");

		cell = row.createCell(14);
		cell.setCellValue("CODIGOBPIN");

		cell = row.createCell(15);
		cell.setCellValue("CODIGOUNIDADEJE");
		
		cell = row.createCell(16);
		cell.setCellValue("DETALLE_SECTORIAL");

		cell = row.createCell(17);
		cell.setCellValue("SALDO INCIAL");

		String compania_aux = SessionUtil.getCompania();

		rowIndex = 1;
		for (Registro option : options) {
			row = optionsSheet.createRow(rowIndex++);

			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("NOMBRE").toString());
			
			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("TERCERO").toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("SUCURSAL").toString());

			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get("CENTRO_COSTO").toString());

			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get("NOMBRE_CC").toString());

			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get("AUXILIAR").toString());

			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get("NOMBRE_AUX").toString());

			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get("REFERENCIA").toString());

			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get("NOMBRE_REF").toString());

			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get("FUENTE_RECURSO").toString());

			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get("NOMBRE_FUE").toString());

			cell = row.createCell(12);
			if (option.getCampos().get("CODIGO_CCPET") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGO_CCPET").toString());
			}

			cell = row.createCell(13);
			if (option.getCampos().get("PROGRAMA") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("PROGRAMA").toString());
			}

			cell = row.createCell(14);
			if (option.getCampos().get("CODIGOBPIN") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOBPIN").toString());
			}

			cell = row.createCell(15);
			if (option.getCampos().get("CODIGOUNIDADEJE") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE").toString());
			}
			
			cell = row.createCell(16);
			if (option.getCampos().get("DETALLESECTORIAL") == null) {
				cell.setCellValue("");
			} else {
				cell.setCellValue(option.getCampos().get("DETALLESECTORIAL").toString());
			}
			cell = row.createCell(17);
			cell.setCellValue(option.getCampos().get("APROPIACIONINICIAL").toString());

		}

	}

	private static Name createName(Workbook workbook, String nameName, String formula) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		name.setRefersToFormula(formula);
		return name;
	}

	/**
	 * No se puede empezar con un nÃºmero
	 *
	 * @param name
	 * @return
	 */
	static String formatNameName(String name) {
		name = name.replace(" ", "").replace("-", "_").replace(":", ".");
		if (Character.isDigit(name.charAt(0))) {
			name = "_" + name;
		}

		return name;
	}
	// </METODOS_BOTONES>
	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>

	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna el objeto contArchivoSelecFile
	 * 
	 * @return contArchivoSelecFile
	 */
	public UploadedFile getArchivoCargaSelecFile() {
		return archivoCargaSelecFile;
	}

	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes() {
		// <CODIGO_DESARROLLADO>
		if (registro.getCampos().get("CLASECLASIFICADOR") == null) {
			registro.getCampos().put("TIPOCLASIFICADOR", "");
		}

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues() {

		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado antes de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes() {
		// <CODIGO_DESARROLLADO>

		return true;
	}

	/**
	 * Metodo ejecutado despues de realizar la eliminacion del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		return true;
	}

	/**
	 * Metodo ejecutado cuando se cierra el formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void ejecutarrcCerrar() {
		// <CODIGO_DESARROLLADO>
		SessionUtil.redireccionarMenu();
		// </CODIGO_DESARROLLADO>
	}

	public void ejecutarrcVolver() {
		SessionUtil.redireccionarMenu();
	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable naturaleza
	 * 
	 * @return naturaleza
	 */
	public String getNaturaleza() {
		return naturaleza;
	}

	/**
	 * Asigna la variable naturaleza
	 * 
	 * @param naturaleza Variable a asignar en naturaleza
	 */
	public void setNaturaleza(String naturaleza) {
		this.naturaleza = naturaleza;
	}

	/**
	 * Retorna la variable regalias
	 * 
	 * @return regalias
	 */
	public String getRegalias() {
		return regalias;
	}

	/**
	 * Asigna la variable regalias
	 * 
	 * @param regalias Variable a asignar en regalias
	 */
	public void setRegalias(String regalias) {
		this.regalias = regalias;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	/**
	 * Retorna la lista listaTipoVigencia
	 * 
	 * @return listaTipoVigencia
	 */
	public List<Registro> getListaTipoVigencia() {
		return listaTipoVigencia;
	}

	/**
	 * Asigna la lista listaTipoVigencia
	 * 
	 * @param listaTipoVigencia Variable a asignar en listaTipoVigencia
	 */
	public void setListaTipoVigencia(List<Registro> listaTipoVigencia) {
		this.listaTipoVigencia = listaTipoVigencia;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
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
	 * @param listaSector Variable a asignar en listaSector
	 */
	public void setListaSector(RegistroDataModelImpl listaSector) {
		this.listaSector = listaSector;
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
	 * @param listaSector Variable a asignar en listaSector
	 */
	public void setListaSectorE(RegistroDataModelImpl listaSectorE) {
		this.listaSectorE = listaSectorE;
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
	 * @param listaPrograma Variable a asignar en listaPrograma
	 */
	public void setListaPrograma(RegistroDataModelImpl listaPrograma) {
		this.listaPrograma = listaPrograma;
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
	 * @param listaPrograma Variable a asignar en listaPrograma
	 */
	public void setListaProgramaE(RegistroDataModelImpl listaProgramaE) {
		this.listaProgramaE = listaProgramaE;
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
	 * @param listaSupPrograma Variable a asignar en listaSupPrograma
	 */
	public void setListaSupPrograma(RegistroDataModelImpl listaSupPrograma) {
		this.listaSupPrograma = listaSupPrograma;
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
	 * @param listaSupPrograma Variable a asignar en listaSupPrograma
	 */
	public void setListaSupProgramaE(RegistroDataModelImpl listaSupProgramaE) {
		this.listaSupProgramaE = listaSupProgramaE;
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
	 * @param listaCodigoProducto Variable a asignar en listaCodigoProducto
	 */
	public void setListaCodigoProducto(RegistroDataModelImpl listaCodigoProducto) {
		this.listaCodigoProducto = listaCodigoProducto;
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
	 * @param listaCodigoProducto Variable a asignar en listaCodigoProducto
	 */
	public void setListaCodigoProductoE(RegistroDataModelImpl listaCodigoProductoE) {
		this.listaCodigoProductoE = listaCodigoProductoE;
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
	 * @param listaCodigoCCPET Variable a asignar en listaCodigoCCPET
	 */
	public void setListaCodigoCCPET(RegistroDataModelImpl listaCodigoCCPET) {
		this.listaCodigoCCPET = listaCodigoCCPET;
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
	 * @param listaCodigoCCPET Variable a asignar en listaCodigoCCPET
	 */
	public void setListaCodigoCCPETE(RegistroDataModelImpl listaCodigoCCPETE) {
		this.listaCodigoCCPETE = listaCodigoCCPETE;
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
	 * @param listaCodigoCPCDANE Variable a asignar en listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANE(RegistroDataModelImpl listaCodigoCPCDANE) {
		this.listaCodigoCPCDANE = listaCodigoCPCDANE;
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
	 * @param listaCodigoCPCDANE Variable a asignar en listaCodigoCPCDANE
	 */
	public void setListaCodigoCPCDANEE(RegistroDataModelImpl listaCodigoCPCDANEE) {
		this.listaCodigoCPCDANEE = listaCodigoCPCDANEE;
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
	 * @param listaCodigoUnidEje Variable a asignar en listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEje(RegistroDataModelImpl listaCodigoUnidEje) {
		this.listaCodigoUnidEje = listaCodigoUnidEje;
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
	 * @param listaCodigoUnidEje Variable a asignar en listaCodigoUnidEje
	 */
	public void setListaCodigoUnidEjeE(RegistroDataModelImpl listaCodigoUnidEjeE) {
		this.listaCodigoUnidEjeE = listaCodigoUnidEjeE;
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
	 * @param listaCodigoFuente Variable a asignar en listaCodigoFuente
	 */
	public void setListaCodigoFuente(RegistroDataModelImpl listaCodigoFuente) {
		this.listaCodigoFuente = listaCodigoFuente;
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
	 * @param listaCodigoFuente Variable a asignar en listaCodigoFuente
	 */
	public void setListaCodigoFuenteE(RegistroDataModelImpl listaCodigoFuenteE) {
		this.listaCodigoFuenteE = listaCodigoFuenteE;
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
	 * @param listaCodigoCCPETRega Variable a asignar en listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRega(RegistroDataModelImpl listaCodigoCCPETRega) {
		this.listaCodigoCCPETRega = listaCodigoCCPETRega;
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
	 * @param listaCodigoCCPETRega Variable a asignar en listaCodigoCCPETRega
	 */
	public void setListaCodigoCCPETRegaE(RegistroDataModelImpl listaCodigoCCPETRegaE) {
		this.listaCodigoCCPETRegaE = listaCodigoCCPETRegaE;
	}

	/**
	 * Retorna la lista listaPoliticaPublica
	 * 
	 * @return listaPoliticaPublica
	 */
	public RegistroDataModelImpl getListaPoliticaPublica() {
		return listaPoliticaPublica;
	}

	/**
	 * Asigna la lista listaPoliticaPublica
	 * 
	 * @param listaPoliticaPublica Variable a asignar en listaPoliticaPublica
	 */
	public void setListaPoliticaPublica(RegistroDataModelImpl listaPoliticaPublica) {
		this.listaPoliticaPublica = listaPoliticaPublica;
	}

	/**
	 * Retorna la lista listaPoliticaPublica
	 * 
	 * @return listaPoliticaPublica
	 */
	public RegistroDataModelImpl getListaPoliticaPublicaE() {
		return listaPoliticaPublicaE;
	}

	/**
	 * Asigna la lista listaPoliticaPublica
	 * 
	 * @param listaPoliticaPublica Variable a asignar en listaPoliticaPublica
	 */
	public void setListaPoliticaPublicaE(RegistroDataModelImpl listaPoliticaPublicaE) {
		this.listaPoliticaPublicaE = listaPoliticaPublicaE;
	}

	/**
	 * Retorna la lista listaDetalleSectorial
	 * 
	 * @return listaDetalleSectorial
	 */
	public RegistroDataModelImpl getListaDetalleSectorial() {
		return listaDetalleSectorial;
	}

	/**
	 * Asigna la lista listaDetalleSectorial
	 * 
	 * @param listaDetalleSectorial Variable a asignar en listaDetalleSectorial
	 */
	public void setListaDetalleSectorial(RegistroDataModelImpl listaDetalleSectorial) {
		this.listaDetalleSectorial = listaDetalleSectorial;
	}

	/**
	 * Retorna la lista listaDetalleSectorial
	 * 
	 * @return listaDetalleSectorial
	 */
	public RegistroDataModelImpl getListaDetalleSectorialE() {
		return listaDetalleSectorialE;
	}

	/**
	 * Asigna la lista listaDetalleSectorial
	 * 
	 * @param listaDetalleSectorial Variable a asignar en listaDetalleSectorial
	 */
	public void setListaDetalleSectorialE(RegistroDataModelImpl listaDetalleSectorialE) {
		this.listaDetalleSectorialE = listaDetalleSectorialE;
	}

	public String getAuxiliar() {
		return auxiliar;
	}

	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
	// <SET_GET_LISTAS_SUBFORM>
	/**
	 * Retorna la lista listaSfconfiggeneral
	 * 
	 * @return listaSfconfiggeneral
	 */
	public RegistroDataModelImpl getListaSfconfiggeneral() {
		return listaSfconfiggeneral;
	}

	/**
	 * Asigna la lista listaSfconfiggeneral
	 * 
	 * @param listaSfconfiggeneral Variable a asignar en listaSfconfiggeneral
	 */
	public void setListaSfconfiggeneral(RegistroDataModelImpl listaSfconfiggeneral) {
		this.listaSfconfiggeneral = listaSfconfiggeneral;
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
	 * @param registroSub Variable a asignar en registroSub
	 */
	public void setRegistroSub(Registro registroSub) {
		this.registroSub = registroSub;
	}

	public String getNaturalezaCuenta() {
		return naturalezaCuenta;
	}

	public void setNaturalezaCuenta(String naturalezaCuenta) {
		this.naturalezaCuenta = naturalezaCuenta;
	}

	/**
	 * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
	 *
	 * @param object Un Objeto
	 * @return String que representa al objeto
	 */
	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
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

	public boolean isBloqSector() {
		return bloqSector;
	}

	public void setBloqSector(boolean bloqSector) {
		this.bloqSector = bloqSector;
	}

	public boolean isBloqPrograma() {
		return bloqPrograma;
	}

	public void setBloqPrograma(boolean bloqPrograma) {
		this.bloqPrograma = bloqPrograma;
	}

	public boolean isBloqSupPrograma() {
		return bloqSupPrograma;
	}

	public void setBloqSupPrograma(boolean bloqSupPrograma) {
		this.bloqSupPrograma = bloqSupPrograma;
	}

	public boolean isBloqCodigoProducto() {
		return bloqCodigoProducto;
	}

	public void setBloqCodigoProducto(boolean bloqCodigoProducto) {
		this.bloqCodigoProducto = bloqCodigoProducto;
	}

	public boolean isBloqCodigoBPIN() {
		return bloqCodigoBPIN;
	}

	public void setBloqCodigoBPIN(boolean bloqCodigoBPIN) {
		this.bloqCodigoBPIN = bloqCodigoBPIN;
	}

	public boolean isBloqCodigoCCPET() {
		return bloqCodigoCCPET;
	}

	public void setBloqCodigoCCPET(boolean bloqCodigoCCPET) {
		this.bloqCodigoCCPET = bloqCodigoCCPET;
	}

	public boolean isBloqCodigoCPCDANE() {
		return bloqCodigoCPCDANE;
	}

	public void setBloqCodigoCPCDANE(boolean bloqCodigoCPCDANE) {
		this.bloqCodigoCPCDANE = bloqCodigoCPCDANE;
	}

	public boolean isBloqCodigoUnidEje() {
		return bloqCodigoUnidEje;
	}

	public void setBloqCodigoUnidEje(boolean bloqCodigoUnidEje) {
		this.bloqCodigoUnidEje = bloqCodigoUnidEje;
	}

	public boolean isBloqCodigoFuente() {
		return bloqCodigoFuente;
	}

	public void setBloqCodigoFuente(boolean bloqCodigoFuente) {
		this.bloqCodigoFuente = bloqCodigoFuente;
	}

	public boolean isBloqCodigoCCPETRega() {
		return bloqCodigoCCPETRega;
	}

	public void setBloqCodigoCCPETRega(boolean bloqCodigoCCPETRega) {
		this.bloqCodigoCCPETRega = bloqCodigoCCPETRega;
	}

	public int getModelo() {
		return modelo;
	}

	public void setModelo(int modelo) {
		this.modelo = modelo;
	}

	public String getRubroCodigo() {
		return rubroCodigo;
	}

	public void setRubroCodigo(String rubroCodigo) {
		this.rubroCodigo = rubroCodigo;
	}

	public RegistroDataModelImpl getListaSfarbol() {
		return listaSfarbol;
	}

	public void setListaSfarbol(RegistroDataModelImpl listaSfarbol) {
		this.listaSfarbol = listaSfarbol;
	}

	public RegistroDataModelImpl getListaTipoClasificador() {
		return listaTipoClasificador;
	}

	public void setListaTipoClasificador(RegistroDataModelImpl listaTipoClasificador) {
		this.listaTipoClasificador = listaTipoClasificador;
	}

	public RegistroDataModelImpl getListaClaseClasificador() {
		return listaClaseClasificador;
	}

	public void setListaClaseClasificador(RegistroDataModelImpl listaClaseClasificador) {
		this.listaClaseClasificador = listaClaseClasificador;
	}

	public RegistroDataModelImpl getListaTipoClasificadorE() {
		return listaTipoClasificadorE;
	}

	public void setListaTipoClasificadorE(RegistroDataModelImpl listaTipoClasificadorE) {
		this.listaTipoClasificadorE = listaTipoClasificadorE;
	}

	public RegistroDataModelImpl getListaClaseClasificadorE() {
		return listaClaseClasificadorE;
	}

	public void setListaClaseClasificadorE(RegistroDataModelImpl listaClaseClasificadorE) {
		this.listaClaseClasificadorE = listaClaseClasificadorE;
	}

	public String getClaseClasificador() {
		return claseClasificador;
	}

	public void setClaseClasificador(String claseClasificador) {
		this.claseClasificador = claseClasificador;
	}

	public Registro getRegistroSubSfArbol() {
		return registroSubSfArbol;
	}

	public void setRegistroSubSfArbol(Registro registroSubSfArbol) {
		this.registroSubSfArbol = registroSubSfArbol;
	}

	public Registro getRegistroSubSfConfigGeneral() {
		return registroSubSfConfigGeneral;
	}

	public void setRegistroSubSfConfigGeneral(Registro registroSubSfConfigGeneral) {
		this.registroSubSfConfigGeneral = registroSubSfConfigGeneral;
	}

	public RegistroDataModelImpl getListaSffuenterecurso() {
		return listaSffuenterecurso;
	}

	public void setListaSffuenterecurso(RegistroDataModelImpl listaSffuenterecurso) {
		this.listaSffuenterecurso = listaSffuenterecurso;
	}

	public Registro getRegistroSubSfFuenteRecurso() {
		return registroSubSfFuenteRecurso;
	}

	public void setRegistroSubSfFuenteRecurso(Registro registroSubSfFuenteRecurso) {
		this.registroSubSfFuenteRecurso = registroSubSfFuenteRecurso;
	}

	public RegistroDataModelImpl getListaTipoClasificadorNu() {
		return listaTipoClasificadorNu;
	}

	public void setListaTipoClasificadorNu(RegistroDataModelImpl listaTipoClasificadorNu) {
		this.listaTipoClasificadorNu = listaTipoClasificadorNu;
	}

	public RegistroDataModelImpl getListaTipoClasificadorNuE() {
		return listaTipoClasificadorNuE;
	}

	public void setListaTipoClasificadorNuE(RegistroDataModelImpl listaTipoClasificadorNuE) {
		this.listaTipoClasificadorNuE = listaTipoClasificadorNuE;
	}

	public boolean isVisibleFuenteregali() {
		return visibleFuenteregali;
	}

	public void setVisibleFuenteregali(boolean visibleFuenteregali) {
		this.visibleFuenteregali = visibleFuenteregali;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga1;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga1 = archivoDescarga;
	}

	public Registro getRegistroSubSfTipoVigencia() {
		return registroSubSfTipoVigencia;
	}

	public void setRegistroSubSfTipoVigencia(Registro registroSubSfTipoVigencia) {
		this.registroSubSfTipoVigencia = registroSubSfTipoVigencia;
	}

	public RegistroDataModelImpl getListaSftipovigencia() {
		return listaSftipovigencia;
	}

	public void setListaSftipovigencia(RegistroDataModelImpl listaSftipovigencia) {
		this.listaSftipovigencia = listaSftipovigencia;
	}

	public RegistroDataModelImpl getListaEquivalenteCuipo() {
		return listaEquivalenteCuipo;
	}

	public void setListaEquivalenteCuipo(RegistroDataModelImpl listaEquivalenteCuipo) {
		this.listaEquivalenteCuipo = listaEquivalenteCuipo;
	}

	public RegistroDataModelImpl getListaEquivalenteCuipoE() {
		return listaEquivalenteCuipoE;
	}

	public void setListaEquivalenteCuipoE(RegistroDataModelImpl listaEquivalenteCuipoE) {
		this.listaEquivalenteCuipoE = listaEquivalenteCuipoE;
	}

	public StreamedContent getArchivoDescarga1() {
		return archivoDescarga1;
	}

	public void setArchivoDescarga1(StreamedContent archivoDescarga1) {
		this.archivoDescarga1 = archivoDescarga1;
	}

	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}

	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

	/**
	 * Retorna el objeto contArchivocargarExcelGral
	 * 
	 * @return contArchivocargarExcelGral
	 */
	public ContenedorArchivo getContArchivocargarExcelGral() {
		return contArchivocargarExcelGral;
	}

	/**
	 * Asigna el objeto contArchivocargarExcelGral
	 * 
	 * @param contArchivocargarExcelGral Variable a asignar en
	 *                                   contArchivocargarExcelGral
	 */
	public void setContArchivocargarExcelGral(ContenedorArchivo contArchivocargarExcelGral) {
		this.contArchivocargarExcelGral = contArchivocargarExcelGral;
	}

	public List<Registro> getListaCompania() {
		return listaCompania;
	}

	public void setListaCompania(List<Registro> listaCompania) {
		this.listaCompania = listaCompania;
	}

	public List<Registro> getListaClasificador() {
		return listaClasificador;
	}

	public void setListaClasificador(List<Registro> listaClasificador) {
		this.listaClasificador = listaClasificador;
	}

	public List<Registro> getListaTipoVigenciaArbol() {
		return listaTipoVigenciaArbol;
	}

	public void setListaTipoVigenciaArbol(List<Registro> listaTipoVigenciaArbol) {
		this.listaTipoVigenciaArbol = listaTipoVigenciaArbol;
	}

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public long getContador() {
		return contador;
	}

	public void setContador(long contador) {
		this.contador = contador;
	}

	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}

	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}

	public void setArchivoCargaSelecFile(UploadedFile archivoCargaSelecFile) {
		this.archivoCargaSelecFile = archivoCargaSelecFile;
	}

	public List<Registro> getListaTipoClasificadorArbol() {
		return listaTipoClasificadorArbol;
	}

	public void setListaTipoClasificadorArbol(List<Registro> listaTipoClasificadorArbol) {
		this.listaTipoClasificadorArbol = listaTipoClasificadorArbol;
	}

	// </SET_GET_ADICIONALES>
	public boolean isVarVolver() {
		return varVolver;
	}

	public void setVarVolver(boolean varVolver) {
		this.varVolver = varVolver;
	}

	public int getIndiceSfconfiggeneral() {
		return indiceSfconfiggeneral;
	}

	public void setIndiceSfconfiggeneral(int indiceSfconfiggeneral) {
		this.indiceSfconfiggeneral = indiceSfconfiggeneral;
	}

	public String getSalida() {
		return salida;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}

	public boolean isBloqTipoRec() {
		return bloqTipoRec;
	}

	public void setBloqTipoRec(boolean bloqTipoRec) {
		this.bloqTipoRec = bloqTipoRec;
	}

	public boolean isBloqVigAnt() {
		return bloqVigAnt;
	}

	public void setBloqVigAnt(boolean bloqVigAnt) {
		this.bloqVigAnt = bloqVigAnt;
	}

	public boolean isBloqSituaF() {
		return bloqSituaF;
	}

	public void setBloqSituaF(boolean bloqSituaF) {
		this.bloqSituaF = bloqSituaF;
	}

	public boolean isBloqTransfer() {
		return bloqTransfer;
	}

	public void setBloqTransfer(boolean bloqTransfer) {
		this.bloqTransfer = bloqTransfer;
	}

	public boolean isBloqDestEsp() {
		return bloqDestEsp;
	}

	public void setBloqDestEsp(boolean bloqDestEsp) {
		this.bloqDestEsp = bloqDestEsp;
	}

	public boolean isBloqTipoNor() {
		return bloqTipoNor;
	}

	public void setBloqTipoNor(boolean bloqTipoNor) {
		this.bloqTipoNor = bloqTipoNor;
	}

	public boolean isBloqNumNorm() {
		return bloqNumNorm;
	}

	public void setBloqNumNorm(boolean bloqNumNorm) {
		this.bloqNumNorm = bloqNumNorm;
	}

	public boolean isBloqFecNorm() {
		return bloqFecNorm;
	}

	public void setBloqFecNorm(boolean bloqFecNorm) {
		this.bloqFecNorm = bloqFecNorm;
	}

	public RegistroDataModelImpl getListaclaseclasificadormod() {
		return listaclaseclasificadormod;
	}

	public void setListaclaseclasificadormod(RegistroDataModelImpl listaclaseclasificadormod) {
		this.listaclaseclasificadormod = listaclaseclasificadormod;
	}

	public RegistroDataModelImpl getListatipoclasificadorpost() {
		return listatipoclasificadorpost;
	}

	public void setListatipoclasificadorpost(RegistroDataModelImpl listatipoclasificadorpost) {
		this.listatipoclasificadorpost = listatipoclasificadorpost;
	}

	public Registro getRegistroSubSfActualiza() {
		return registroSubSfActualiza;
	}

	public void setRegistroSubSfActualiza(Registro registroSubSfActualiza) {
		this.registroSubSfActualiza = registroSubSfActualiza;
	}

	public RegistroDataModelImpl getListaclaseclasificadormodE() {
		return listaclaseclasificadormodE;
	}

	public void setListaclaseclasificadormodE(RegistroDataModelImpl listaclaseclasificadormodE) {
		this.listaclaseclasificadormodE = listaclaseclasificadormodE;
	}

	public RegistroDataModelImpl getListatipoclasificadorpostE() {
		return listatipoclasificadorpostE;
	}

	public void setListatipoclasificadorpostE(RegistroDataModelImpl listatipoclasificadorpostE) {
		this.listatipoclasificadorpostE = listatipoclasificadorpostE;
	}

	public RegistroDataModelImpl getListaSfactualiza() {
		return listaSfactualiza;
	}

	public void setListaSfactualiza(RegistroDataModelImpl listaSfactualiza) {
		this.listaSfactualiza = listaSfactualiza;
	}

	public ContenedorArchivo getContArchivocargarApropiaciones() {
		return contArchivocargarApropiaciones;
	}

	public void setContArchivocargarApropiaciones(ContenedorArchivo contArchivocargarApropiaciones) {
		this.contArchivocargarApropiaciones = contArchivocargarApropiaciones;
	}

	public RegistroDataModelImpl getListaCCPET() {
		return listaCCPET;
	}

	public void setListaCCPET(RegistroDataModelImpl listaCCPET) {
		this.listaCCPET = listaCCPET;
	}

	public RegistroDataModelImpl getListaprogramamga1() {
		return listaprogramamga1;
	}

	public void setListaprogramamga1(RegistroDataModelImpl listaprogramamga1) {
		listaprogramamga1 = listaprogramamga1;
	}

	public RegistroDataModelImpl getListaunidadEjecutora() {
		return listaunidadEjecutora;
	}

	public void setListaunidadEjecutora(RegistroDataModelImpl listaunidadEjecutora) {
		this.listaunidadEjecutora = listaunidadEjecutora;
	}

	public Registro getRegistroSubapropiaciones() {
		return registroSubapropiaciones;
	}

	public void setRegistroSubapropiaciones(Registro registroSubapropiaciones) {
		this.registroSubapropiaciones = registroSubapropiaciones;
	}

	public RegistroDataModelImpl getListaApropiaciones() {
		return listaApropiaciones;
	}

	public void setListaApropiaciones(RegistroDataModelImpl listaApropiaciones) {
		this.listaApropiaciones = listaApropiaciones;
	}

	public RegistroDataModelImpl getListaprogramamga1E() {
		return listaprogramamga1E;
	}

	public void setListaprogramamga1E(RegistroDataModelImpl listaprogramamga1e) {
		listaprogramamga1E = listaprogramamga1e;
	}

	public RegistroDataModelImpl getListaCCPETE() {
		return listaCCPETE;
	}

	public void setListaCCPETE(RegistroDataModelImpl listaCCPETE) {
		this.listaCCPETE = listaCCPETE;
	}

	public RegistroDataModelImpl getListaunidadEjecutoraE() {
		return listaunidadEjecutoraE;
	}

	public void setListaunidadEjecutoraE(RegistroDataModelImpl listaunidadEjecutoraE) {
		this.listaunidadEjecutoraE = listaunidadEjecutoraE;
	}

	/**
	 * @return the listaReservaApropiacionEquivalente
	 */
	public RegistroDataModelImpl getListaReservaApropiacionEquivalente() {
		return listaReservaApropiacionEquivalente;
	}

	/**
	 * @param listaReservaApropiacionEquivalente the listaReservaApropiacionEquivalente to set
	 */
	public void setListaReservaApropiacionEquivalente(RegistroDataModelImpl listaReservaApropiacionEquivalente) {
		this.listaReservaApropiacionEquivalente = listaReservaApropiacionEquivalente;
	}

	/**
	 * @return the listaReservaApropiacionEquivalenteE
	 */
	public RegistroDataModelImpl getListaReservaApropiacionEquivalenteE() {
		return listaReservaApropiacionEquivalenteE;
	}

	/**
	 * @param listaReservaApropiacionEquivalenteE the listaReservaApropiacionEquivalenteE to set
	 */
	public void setListaReservaApropiacionEquivalenteE(RegistroDataModelImpl listaReservaApropiacionEquivalenteE) {
		this.listaReservaApropiacionEquivalenteE = listaReservaApropiacionEquivalenteE;
	}

	/**
	 * @return the listaReservaCajaEquivalente
	 */
	public RegistroDataModelImpl getListaReservaCajaEquivalente() {
		return listaReservaCajaEquivalente;
	}

	/**
	 * @param listaReservaCajaEquivalente the listaReservaCajaEquivalente to set
	 */
	public void setListaReservaCajaEquivalente(RegistroDataModelImpl listaReservaCajaEquivalente) {
		this.listaReservaCajaEquivalente = listaReservaCajaEquivalente;
	}

	/**
	 * @return the listaReservaCajaEquivalenteE
	 */
	public RegistroDataModelImpl getListaReservaCajaEquivalenteE() {
		return listaReservaCajaEquivalenteE;
	}

	/**
	 * @param listaReservaCajaEquivalenteE the listaReservaCajaEquivalenteE to set
	 */
	public void setListaReservaCajaEquivalenteE(RegistroDataModelImpl listaReservaCajaEquivalenteE) {
		this.listaReservaCajaEquivalenteE = listaReservaCajaEquivalenteE;
	}

	/**
	 * @return the manejaEquivalenteCUIPO
	 */
	public boolean isManejaEquivalenteCUIPO() {
		return manejaEquivalenteCUIPO;
	}

	/**
	 * @param manejaEquivalenteCUIPO the manejaEquivalenteCUIPO to set
	 */
	public void setManejaEquivalenteCUIPO(boolean manejaEquivalenteCUIPO) {
		this.manejaEquivalenteCUIPO = manejaEquivalenteCUIPO;
	}

	public RegistroDataModelImpl getListaDetalleSectorials() {
		return listaDetalleSectorials;
	}

	public void setListaDetalleSectorials(RegistroDataModelImpl listaDetalleSectorials) {
		this.listaDetalleSectorials = listaDetalleSectorials;
	}

	public RegistroDataModelImpl getListaDetalleSectorialsE() {
		return listaDetalleSectorialsE;
	}

	public void setListaDetalleSectorialsE(RegistroDataModelImpl listaDetalleSectorialsE) {
		this.listaDetalleSectorialsE = listaDetalleSectorialsE;
	}
	

}
