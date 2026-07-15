/*-
 * DevengosDescuentosControlador.java
 *
 * 1.0
 * 
 * 09/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;
import com.sysman.nomina.ejb.EjbNominaUnoLocal;
import com.sysman.nomina.enums.DevengosDescuentosControladorEnum;
import com.sysman.nomina.enums.DevengosDescuentosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar informe de los descuentos y los devengos
 *
 * @version 1.0, 09/01/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class DevengosDescuentosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante definida para almacenar el codigo del modulo por la que se ingresa
	 * en la aplicacion
	 */
	private final String modulo;

	// <DECLARAR_ATRIBUTOS>
	/**
	 * Variable que almacena el valor de la casilla Listado de extras
	 */
	private boolean extras;
	/**
	 * Variable que almacena el valor de la casilla listado de pensionados
	 */
	private boolean pensionados;
	/**
	 * Variable que almacena el valor de la casilla viaticos
	 */
	private boolean viaticos;
	/**
	 * Variable que almacena el valor de la casilla Listado de primas
	 */
	private boolean primas;
	/**
	 * Variable que almacena el valor de la casilla Planilla de devengos
	 */
	private boolean informePrenomina;
	/**
	 * Variable que almacena el valor de la casilla Totales
	 */
	private boolean totales;

	/**
	 * Variable que almacena el valor de la casilla Maestra En Excel
	 */
	private boolean maestra;

	/**
	 * Variable que almacena el numero seleccionado en el combo ano
	 */
	private String ano;
	/**
	 * Variable que almacena el numero de mes seleccionado
	 */
	private String mes;
	/**
	 * Variable que almacena el codigo seleccionado en el combo periodo
	 */
	private String periodo;
	/**
	 * Variable que almacena el codigo seleccionado en el combo proceso
	 */
	private String proceso;
	/**
	 * Variable que almacena el numero del mes seleccionado en le combo mes
	 */
	private String mesViaticos;
	/**
	 * Variable que almacena el texto digitado en el campo observacion
	 */
	private String observacion;
	/**
	 * Variable que almacen el texto digitado en el campo concepto de pago
	 */
	private String conceptoPago;
	/**
	 * Variable que valida si la etiqueta y combo mes se cargan o no
	 */
	private boolean visibleMesViatico;
	/**
	 * Variable que valida si la etiqueta y casilla de verificacion Planilla de
	 * Devengos (PRELIMINAR) se cargan o no
	 */
	private boolean visiblePlanillaDevengos;
	/**
	 * Variable que define el nombre del informe a generar
	 */
	private String nombreReporte;
	/**
	 * Variable que define que consulta tomara el informe a generar
	 */
	private String consultaInforme;

	/**
	 * Variable que que almacena el valor del parametro MANEJA COMPARACION DE
	 * PRENOMINA
	 */
	private String comparacionPrenomina;
	/**
	 * Atributo que valida que consulta, se generar� de acuerdo al evento del boton
	 * seleccionado
	 */
	private boolean revisarExtras;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	private String conFormato = "NO";

	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Lista de registros de la tabla periodos
	 */
	private List<Registro> listaAno;
	/**
	 * Lista de registros de la tabla periodos
	 */
	private List<Registro> listaMesViaticos;
	
	/**
	 * Lista de registros de la tabla periodos
	 */
	private List<Registro> listaMes;
	
	/**
	 * Lista de registros de la tabla periodos
	 */
	private List<Registro> listaPeriodo;
	/**
	 * Lista de registros de la tabla procesos de nomina
	 */
	private List<Registro> listaProceso;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	private String headerEspecial;

	private String nombrePeriodoNomina;
	private String nombreMes;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	@EJB
	private EjbNominaCeroRemote ejbNominaCero;
	@EJB
	private EjbNominaUnoLocal ejbNominaUno;
	@EJB
	private EjbNominaSieteRemote ejbNominaSiete;

	XSSFWorkbook workbook;
	XSSFSheet sheet;
	XSSFSheet sheetPersonal;

	CellStyle styleEncabezado;
	CellStyle styleTextos;
	CellStyle styleMoneda;

	/**
	 * Crea una nueva instancia de DevengosDescuentosControlador
	 */
	public DevengosDescuentosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		workbook = new XSSFWorkbook();
		nombrePeriodoNomina = SysmanFunciones.nvl(SessionUtil.getSessionVar("nombrePeriodoNomina"), "").toString();
		try {
			// 1582
			numFormulario = GeneralCodigoFormaEnum.DEVENGOS_Y_DESCUENTOS_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		proceso = SessionUtil.getSessionVar("procesoNomina").toString();
		ano = SessionUtil.getSessionVar("anioNomina").toString();
		mes = SessionUtil.getSessionVar("mesNomina").toString();
		mesViaticos = SessionUtil.getSessionVar("mesNomina").toString();
		nombreMes = SessionUtil.getSessionVar("nombreMesNomina").toString();
		periodo = SessionUtil.getSessionVar("periodoNomina").toString();

		// <CARGAR_LISTA>
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();
		cargarListaProceso();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		conceptoPago = "Salario";

		try {
			String tipoPensionado = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"TIPO DE NOMINA ACTIVOS O PENSIONADOS", modulo, new Date(), true), "NO");

			if ("PENSIONADOS".equals(tipoPensionado)) {
				pensionados = true;
			}

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.MES.getName(), mes);
			param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

			Registro rsObservacion = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL232.getValue())
											.getUrl(),
									param));

			observacion = SysmanFunciones
					.nvl(rsObservacion.getCampos().get(GeneralParameterEnum.OBSERVACION.getName()), "").toString();

			String manejaPrenomina = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA COMPARACION DE PRENOMINA", modulo, new Date(), true), "NO");

			conFormato = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"FORMATEAR EXCEL PLANILLA DEVENGOS Y DESCUENTOS NOMINA", modulo, new Date(), true), "NO");

			if ("SI".equals(manejaPrenomina)) {
				visiblePlanillaDevengos = true;
			} else {
				visiblePlanillaDevengos = false;
			}

		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 */
	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL5665.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

		try {
			listaMes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL6127.getValue())
											.getUrl(),
									param));
			listaMesViaticos = listaMes;
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * 
	 * Carga la lista listaPeriodo
	 */
	public void cargarListaPeriodo() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		try {
			listaPeriodo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL6665.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * 
	 * Carga la lista listaProceso
	 */
	public void cargarListaProceso() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaProceso = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL7086.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 */
	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (validarListas()) {
			return;
		}
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Revisarextrasmayores en la vista
	 *
	 */
	public void oprimirRevisarExtras() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (validarListas()) {
			return;
		}
		revisarExtras = true;
		generarArchivoExtras();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Presentar en la vista
	 *
	 */
	public void oprimirPresentar() {
		
         
		archivoDescarga = null;
		if (validarListas()) {
			return;
		}
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
	 *
	 */
	public void oprimirEnviarExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (validarListas()) {
			return;
		}
		revisarExtras = false;
		if ("SI".equals(conFormato)) {
			generarArchivoExtras();
			if (maestra) {
				generarArchivoMaestra();
			}
		} else {
			
				generarArchivoPlano();
		}

		// </CODIGO_DESARROLLADO>
	}

	private void generarArchivoMaestra() {

		String strSql = null;
		ConectorPool conexion = new ConectorPool();
		String sqlExcel = "";
		List<String> encabezados = null;
		ResultSet valores = null;
		Statement consulta = null;
		HashMap<String, Object> reemplazar = new HashMap<>();

		workbook = new XSSFWorkbook();

		try {
			reemplazar.put("proceso", proceso);
			reemplazar.put("ano", ano);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);

			reemplazar.put("condicionPivot", generarPivotMaestra());

			sqlExcel = Reporteador.resuelveConsulta("800147DevengosYDescuentosMaestra", Integer.parseInt(modulo),
					reemplazar);

			encabezados = service.getCamposListado(ConectorPool.ESQUEMA_SYSMAN, sqlExcel);

			encabezados.remove(4);
			encabezados.remove(3);

			conexion.conectar(ConectorPool.ESQUEMA_SYSMAN);
			consulta = conexion.getConection().createStatement();
			valores = consulta.executeQuery(sqlExcel);

			long existeDatos = service.getConteoConsulta(revisarExtras ? strSql : sqlExcel);

			if (existeDatos != 0) {
				armarExcelMaestra(encabezados, valores);
			} else {
				ejecutaractualizaAlerta();

			}

		} catch (SQLException | SystemException | NamingException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		} finally {

			try {
				if (consulta != null) {
					consulta.close();
				}
				if (valores != null) {
					valores.close();
				}
			} catch (

			SQLException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}

	}

	private void armarExcelMaestra(List<String> encabezados, ResultSet valores) {

		sheet = workbook.createSheet("Devengos_y_Descuentos");
		sheetPersonal = workbook.createSheet("Personal");

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			// Crear titulo Hoja Conceptos Maestra
			crearTitulos(sheet);

			// Crear titulo Hoja Personal
			crearTitulos(sheetPersonal);

			// Crear encabezados
			crearEncabezados(encabezados);

			// Crear encabezados personal
			crearEncabezadosPersonal();

			// Asignar valores a la hoja Conceptos Maestra
			asignarValores(valores);

			// Asignar valores a la hoja Personal
			asignarValoresPersonal();

			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"DEVENGOS_Y_DESCUENTOS_MAESTRA_" + ano + "_" + mes + "_" + periodo + ".xlsx");

			workbook.close();

		} catch (IOException | JRException | NumberFormatException ex) {

			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

	}

	private void asignarValoresPersonal() {

		int fila = 4;

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

		try {
			List<Registro> listaPersonal = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL8282.getValue())
											.getUrl(),
									param));

			for (Registro valor : listaPersonal) {

				Row rowValores = sheetPersonal.createRow(fila);

				Cell cellCedula = rowValores.createCell(0);
				cellCedula.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("CEDULA"), "").toString());
				cellCedula.setCellStyle(styleTextos);

				Cell cellNombreEmpleado = rowValores.createCell(1);
				cellNombreEmpleado.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_EMPLEADO"), "").toString().toUpperCase());
				cellNombreEmpleado.setCellStyle(styleTextos);

				Cell cellFechaIngreso = rowValores.createCell(2);
				cellFechaIngreso.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FECHA_DE_INGRESO"), "").toString().toUpperCase());
				cellFechaIngreso.setCellStyle(styleTextos);

				Cell cellFechaRetiro = rowValores.createCell(3);
				cellFechaRetiro.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FECHA_DE_RETIRO"), "").toString().toUpperCase());
				cellFechaRetiro.setCellStyle(styleTextos);

				Cell celliIdCentroCosto = rowValores.createCell(4);
				celliIdCentroCosto.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("ID_CENTRO_DE_COSTO"), "").toString().toUpperCase());
				celliIdCentroCosto.setCellStyle(styleTextos);

				Cell cellNombreCentroCosto = rowValores.createCell(5);
				cellNombreCentroCosto.setCellValue(SysmanFunciones
						.nvl(valor.getCampos().get("NOMBRE_CENTRO_DE_COSTO"), "").toString().toUpperCase());
				cellNombreCentroCosto.setCellStyle(styleTextos);

				Cell cellDependencia = rowValores.createCell(6);
				cellDependencia.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()), "")
								.toString().toUpperCase());
				cellDependencia.setCellStyle(styleTextos);

				Cell cellNombreDependencia = rowValores.createCell(7);
				cellNombreDependencia.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_DEPENDENCIA"), "").toString().toUpperCase());
				cellNombreDependencia.setCellStyle(styleTextos);

				Cell cellIdCargo = rowValores.createCell(8);
				cellIdCargo.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("ID_DE_CARGO"), "").toString().toUpperCase());
				cellIdCargo.setCellStyle(styleTextos);

				Cell cellNombreCargo = rowValores.createCell(9);
				cellNombreCargo.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_DE_CARGO"), "").toString().toUpperCase());
				cellNombreCargo.setCellStyle(styleTextos);

				Cell cellFactorRiesgo = rowValores.createCell(10);
				cellFactorRiesgo.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FACTOR_RIESGO"), "").toString().toUpperCase());
				cellFactorRiesgo.setCellStyle(styleTextos);

				Cell cellEntidadBancaria = rowValores.createCell(11);
				cellEntidadBancaria.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("ENTIDAD_BANCARIA"), "").toString().toUpperCase());
				cellEntidadBancaria.setCellStyle(styleTextos);

				Cell cellNombreBanco = rowValores.createCell(12);
				cellNombreBanco
						.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("BANCO"), "").toString().toUpperCase());
				cellNombreBanco.setCellStyle(styleTextos);

				Cell cellNitBanco = rowValores.createCell(13);
				cellNitBanco.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_BANCO"), "").toString().toUpperCase());
				cellNitBanco.setCellStyle(styleTextos);

				Cell cellCuenta = rowValores.createCell(14);
				cellCuenta.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("CUENTA"), "").toString().toUpperCase());
				cellCuenta.setCellStyle(styleTextos);

				Cell cellTipoCuenta = rowValores.createCell(15);
				cellTipoCuenta.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("TIPOCUENTA"), "").toString().toUpperCase());
				cellTipoCuenta.setCellStyle(styleTextos);

				Cell cellNitPension = rowValores.createCell(16);
				cellNitPension.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_PENSION"), "").toString().toUpperCase());
				cellNitPension.setCellStyle(styleTextos);

				Cell cellNombreFondo = rowValores.createCell(17);
				cellNombreFondo.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_DEL_FONDO"), "").toString().toUpperCase());
				cellNombreFondo.setCellStyle(styleTextos);

				Cell cellNitSalud = rowValores.createCell(18);
				cellNitSalud.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_SALUD"), "").toString().toUpperCase());
				cellNitSalud.setCellStyle(styleTextos);

				Cell cellNombreSalud = rowValores.createCell(19);
				cellNombreSalud.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_FONDO_SALUD"), "").toString().toUpperCase());
				cellNombreSalud.setCellStyle(styleTextos);

				Cell cellNitRiesgos = rowValores.createCell(20);
				cellNitRiesgos.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_RIESGOS"), "").toString().toUpperCase());
				cellNitRiesgos.setCellStyle(styleTextos);

				Cell cellNombreRiesgos = rowValores.createCell(21);
				cellNombreRiesgos.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_FONDO_RIESGOS"), "")
						.toString().toUpperCase());
				cellNombreRiesgos.setCellStyle(styleTextos);

				Cell cellNitCesantias = rowValores.createCell(22);
				cellNitCesantias.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_CESANTIAS"), "").toString().toUpperCase());
				cellNitCesantias.setCellStyle(styleTextos);

				Cell cellNombreCesantias = rowValores.createCell(23);
				cellNombreCesantias.setCellValue(SysmanFunciones
						.nvl(valor.getCampos().get("NOMBRE_FONDO_CESANTIAS"), "").toString().toUpperCase());
				cellNombreCesantias.setCellStyle(styleTextos);

				Cell cellNitAfc = rowValores.createCell(24);
				cellNitAfc.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_AFC"), "").toString().toUpperCase());
				cellNitAfc.setCellStyle(styleTextos);

				Cell cellNombreAfc = rowValores.createCell(25);
				cellNombreAfc.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_FONDO_AFC"), "").toString().toUpperCase());
				cellNombreAfc.setCellStyle(styleTextos);

				Cell cellNitVoluntario = rowValores.createCell(26);
				cellNitVoluntario.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_VOL"), "").toString().toUpperCase());
				cellNitVoluntario.setCellStyle(styleTextos);

				Cell cellNombreVoluntario = rowValores.createCell(27);
				cellNombreVoluntario.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_DEL_FONDO_VOL"), "")
						.toString().toUpperCase());
				cellNombreVoluntario.setCellStyle(styleTextos);

				Cell cellNitSindicato = rowValores.createCell(28);
				cellNitSindicato.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_SINDICATO"), "").toString().toUpperCase());
				cellNitSindicato.setCellStyle(styleTextos);

				Cell cellNombreSindicato = rowValores.createCell(29);
				cellNombreSindicato.setCellValue(SysmanFunciones
						.nvl(valor.getCampos().get("NOMBRE_FONDO_SINDICATO"), "").toString().toUpperCase());
				cellNombreSindicato.setCellStyle(styleTextos);

				Cell cellCajaCompensacion = rowValores.createCell(30);
				cellCajaCompensacion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("CAJA_COMPENSACION"), "").toString().toUpperCase());
				cellCajaCompensacion.setCellStyle(styleTextos);

				Cell cellNombreCajaCompensacion = rowValores.createCell(31);
				cellNombreCajaCompensacion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NOMBRE_CAJA"), "").toString().toUpperCase());
				cellNombreCajaCompensacion.setCellStyle(styleTextos);

				Cell cellNitMedicina = rowValores.createCell(32);
				cellNitMedicina.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIT_MEDICINA"), "").toString().toUpperCase());
				cellNitMedicina.setCellStyle(styleTextos);

				Cell cellNombreMedicina = rowValores.createCell(33);
				cellNombreMedicina.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("MEDICINA_PREPAGADA"), "").toString().toUpperCase());
				cellNombreMedicina.setCellStyle(styleTextos);

				Cell cellPreocesoRentencion = rowValores.createCell(34);
				cellPreocesoRentencion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("PROCESORETENCION"), "").toString().toUpperCase());
				cellPreocesoRentencion.setCellStyle(styleTextos);

				Cell cellSexo = rowValores.createCell(35);
				cellSexo.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("SEXO"), "").toString().toUpperCase());
				cellSexo.setCellStyle(styleTextos);

				Cell cellFechaInicio = rowValores.createCell(36);
				cellFechaInicio.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FECHA_INICIO"), "").toString().toUpperCase());
				cellFechaInicio.setCellStyle(styleTextos);

				Cell cellFechaFinal = rowValores.createCell(37);
				cellFechaFinal.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FECHA_FINAL"), "").toString().toUpperCase());
				cellFechaFinal.setCellStyle(styleTextos);

				Cell cellFechaInicioDisfrute = rowValores.createCell(38);
				cellFechaInicioDisfrute.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("INICIO_DISFRUTE"), "").toString().toUpperCase());
				cellFechaInicioDisfrute.setCellStyle(styleTextos);

				Cell cellFechaFinalDisfrute = rowValores.createCell(39);
				cellFechaFinalDisfrute.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FINAL_DISFRUTE"), "").toString().toUpperCase());
				cellFechaFinalDisfrute.setCellStyle(styleTextos);

				Cell cellRegimen = rowValores.createCell(40);
				cellRegimen.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("REGIMEN"), "").toString().toUpperCase());
				cellRegimen.setCellStyle(styleTextos);

				Cell cellRh = rowValores.createCell(41);
				cellRh.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("RH"), "").toString().toUpperCase());
				cellRh.setCellStyle(styleTextos);

				Cell cellGrupoSanguineo = rowValores.createCell(42);
				cellGrupoSanguineo.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("GRUPOSANGINEO"), "").toString().toUpperCase());
				cellGrupoSanguineo.setCellStyle(styleTextos);

				Cell cellGrupoContable = rowValores.createCell(43);
				cellGrupoContable.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("GRUPOCONTABLE"), "").toString().toUpperCase());
				cellGrupoContable.setCellStyle(styleTextos);

				Cell cellFormaNombramiento = rowValores.createCell(44);
				cellFormaNombramiento.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FORMA_NOMBRAMIENTO"), "").toString().toUpperCase());
				cellFormaNombramiento.setCellStyle(styleTextos);

				Cell cellSalarioBase = rowValores.createCell(45);
				cellSalarioBase.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("SALARIO_BASE_IBC"), "").toString().toUpperCase());
				cellSalarioBase.setCellStyle(styleTextos);

				Cell cellCuentaVivienda = rowValores.createCell(46);
				cellCuentaVivienda.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("CUENTA_VIVIENDA"), "").toString().toUpperCase());
				cellCuentaVivienda.setCellStyle(styleTextos);

				Cell cellCuentaViviendaDos = rowValores.createCell(47);
				cellCuentaViviendaDos.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("CUENTA_VIVIENDA2"), "").toString().toUpperCase());
				cellCuentaViviendaDos.setCellStyle(styleTextos);

				Cell cellFechaCumplBoni = rowValores.createCell(48);
				cellFechaCumplBoni.setCellValue(SysmanFunciones
						.nvl(valor.getCampos().get("FECHA_CUMPLIMIENTO_BONIFICACIO"), "").toString().toUpperCase());
				cellFechaCumplBoni.setCellStyle(styleTextos);

				Cell cellTipoVinculacion = rowValores.createCell(49);
				cellTipoVinculacion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("TIPOVINCULACION"), "").toString().toUpperCase());
				cellTipoVinculacion.setCellStyle(styleTextos);

				Cell cellAreaMiso = rowValores.createCell(50);
				cellAreaMiso.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("AREAMISOADM"), "").toString().toUpperCase());
				cellAreaMiso.setCellStyle(styleTextos);

				Cell cellEmailPersonal = rowValores.createCell(51);
				cellEmailPersonal.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("EMAIL_PERSONAL"), "").toString().toUpperCase());
				cellEmailPersonal.setCellStyle(styleTextos);

				Cell cellEmailCorporativo = rowValores.createCell(52);
				cellEmailCorporativo.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("EMAIL_CORPORATIVO"), "").toString().toUpperCase());
				cellEmailCorporativo.setCellStyle(styleTextos);

				Cell cellActaPosesion = rowValores.createCell(53);
				cellActaPosesion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("ACTA_POSESION"), "").toString().toUpperCase());
				cellActaPosesion.setCellStyle(styleTextos);

				Cell cellFechaActaPosesion = rowValores.createCell(54);
				cellFechaActaPosesion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("FECHA_ACTA_POSESION"), "").toString().toUpperCase());
				cellFechaActaPosesion.setCellStyle(styleTextos);

				Cell cellFechaInicioComision = rowValores.createCell(55);
				cellFechaInicioComision.setCellValue(SysmanFunciones
						.nvl(valor.getCampos().get("FECHA_INICIO_COMISION"), "").toString().toUpperCase());
				cellFechaInicioComision.setCellStyle(styleTextos);

				Cell cellFechaFinComision = rowValores.createCell(56);
				cellFechaFinComision.setCellValue(SysmanFunciones.nvl(valor.getCampos().get("FECHA_FINAL_COMISION"), "")
						.toString().toUpperCase());
				cellFechaFinComision.setCellStyle(styleTextos);

				Cell cellTotalDiasComision = rowValores.createCell(57);
				cellTotalDiasComision.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("TOTAL_DIAS_COMISION"), "").toString().toUpperCase());
				cellTotalDiasComision.setCellStyle(styleTextos);

				Cell cellFechaExpeCedula = rowValores.createCell(58);
				cellFechaExpeCedula.setCellValue(SysmanFunciones
						.nvl(valor.getCampos().get("FECHA_EXPEDICION_CEDULA"), "").toString().toUpperCase());
				cellFechaExpeCedula.setCellStyle(styleTextos);

				Cell cellNivelSiif = rowValores.createCell(59);
				cellNivelSiif.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("NIVELSIIF"), "").toString().toUpperCase());
				cellNivelSiif.setCellStyle(styleTextos);

				Cell cellDireccion = rowValores.createCell(60);
				cellDireccion.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("DIRECCION"), "").toString().toUpperCase());
				cellDireccion.setCellStyle(styleTextos);

				Cell cellTelefono = rowValores.createCell(61);
				cellTelefono.setCellValue(
						SysmanFunciones.nvl(valor.getCampos().get("TELEFONOS"), "").toString().toUpperCase());
				cellTelefono.setCellStyle(styleTextos);

				fila++;
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void crearEncabezadosPersonal() {

		String sqlExcel;

		Map<String, Object> reemplazar = new TreeMap<>();

		List<String> encabezados = null;

		reemplazar.put("proceso", proceso);
		reemplazar.put("ano", ano);
		reemplazar.put("mes", mes);
		reemplazar.put("periodo", periodo);

		sqlExcel = Reporteador.resuelveConsulta("800148ListadoPersonalMaestra", Integer.parseInt(modulo), reemplazar);

		encabezados = service.getCamposListado(ConectorPool.ESQUEMA_SYSMAN, sqlExcel);

		XSSFRow rowEncabezados = sheetPersonal.createRow(3);

		for (int i = 0; i < encabezados.size(); i++) {

			XSSFCell cellEncabezadoTres = rowEncabezados.createCell(i);
			cellEncabezadoTres.setCellValue(encabezados.get(i).replace("'", "").replace("_", " "));
			cellEncabezadoTres.setCellStyle(styleEncabezado);
			sheetPersonal.autoSizeColumn(i);

		}

	}

	private Object generarPivotMaestra() {
		StringBuilder cadenaPivot = new StringBuilder();

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

		try {
			List<Registro> registro = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL9999.getValue())
											.getUrl(),
									param));

			for (Registro reg : registro) {

				cadenaPivot.append("'" + reg.getCampos().get("NOMBRECONCEPTO") + "'" + ",");

			}

			if (!SysmanFunciones.validarVariableVacio(cadenaPivot.toString())) {

				cadenaPivot.deleteCharAt(cadenaPivot.length() - 1);

			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return cadenaPivot;
	}

	/**
	 * 
	 * Metodo invocado al ejecutar el comando remoto actualizaAlerta en la vista
	 *
	 */
	public void ejecutaractualizaAlerta() {
		// <CODIGO_DESARROLLADO>
		JsfUtil.agregarMensajeError(idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 */
	public void cambiarAno() {
		// <CODIGO_DESARROLLADO>
		mes = null;
		periodo = null;
		cargarListaMes();
		cargarListaPeriodo();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 */
	public void cambiarMes() {
		// <CODIGO_DESARROLLADO>
		periodo = null;
		cargarListaPeriodo();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Proceso
	 * 
	 */
	public void cambiarProceso() {
		// <CODIGO_DESARROLLADO>
		ano = null;
		mes = null;
		periodo = null;
		cargarListaAno();
		cargarListaMes();
		cargarListaPeriodo();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Viaticos
	 * 
	 */
	public void cambiarViaticos() {

		if (viaticos) {
			visibleMesViatico = true;
			conceptoPago = idioma.getString("TT_LB40855");
			primas = informePrenomina = pensionados = extras = totales = maestra = false;
		} else {
			visibleMesViatico = false;
			conceptoPago = idioma.getString("TG_SALARIO");

		}

		nombreReporte = "";

	}

	/**
	 * Metodo ejecutado al cambiar el control Primas
	 * 
	 */
	public void cambiarPrimas() {

		if (primas) {
			informePrenomina = totales = extras = pensionados = viaticos = visibleMesViatico = maestra = false;
		}
		nombreReporte = "";
	}

	/**
	 * Metodo ejecutado al cambiar el control Totales
	 * 
	 */
	public void cambiartotales() {
		if (totales) {
			informePrenomina = extras = pensionados = viaticos = visibleMesViatico = primas = maestra = false;
		}
		nombreReporte = "";
	}

	/**
	 * Metodo ejecutado al cambiar el control Maestra
	 * 
	 * 
	 */
	public void cambiarMaestra() {
		if (maestra) {
			informePrenomina = extras = pensionados = viaticos = visibleMesViatico = primas = totales = false;
		}
		nombreReporte = "";
	}

	/**
	 * Metodo ejecutado al cambiar el control Informe de Prenomina
	 * 
	 */
	public void cambiarInformePrenomina() {

		if (informePrenomina) {
			primas = extras = totales = pensionados = viaticos = visibleMesViatico = maestra = false;
		}
		nombreReporte = "";

	}

	/**
	 * Metodo ejecutado al cambiar el control Extras
	 * 
	 */
	public void cambiarExtras() {

		if (extras) {
			primas = informePrenomina = totales = pensionados = viaticos = visibleMesViatico = maestra = false;
		}
		nombreReporte = "";

	}

	/**
	 * Metodo ejecutado al cambiar el control Pensionados
	 * 
	 */
	public void cambiarPensionados() {
		if (pensionados) {
			primas = informePrenomina = totales = extras = viaticos = visibleMesViatico = maestra = false;
		}
		nombreReporte = "";

	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	/**
	 * Metodo que se ejecuta al darle clic al boton Revisar extras superior, realiza
	 * la generacion de un archivo en excel
	 */
	public void generarArchivoExtras() {
		String strSql = null;
		ConectorPool conexion = new ConectorPool();
		String sqlExcel = "";
		List<String> encabezados = null;
		ResultSet valores = null;
		Statement consulta = null;
		HashMap<String, Object> reemplazar = new HashMap<>();
		String alias = null;
		String condicion;

		workbook = new XSSFWorkbook();

		Map<String, Object> retorno = generarPivot();
		condicion = retorno != null ? retorno.get("cadena").toString() : null;
		if (SysmanFunciones.esBdSqlServer()) {
			alias = retorno != null ? retorno.get("alias").toString() : null;
		}

		try {
			reemplazar.put("proceso", proceso);
			reemplazar.put("ano", ano);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);
			reemplazar.put("condicionPivot", condicion);
			if (SysmanFunciones.esBdSqlServer()) {
				reemplazar.put("aliasPivot", alias);
			}

			if (revisarExtras) {
				strSql = Reporteador.resuelveConsulta("800127RevisarExtras", Integer.parseInt(modulo), reemplazar);
			} else {

				sqlExcel = Reporteador.resuelveConsulta("800143DevengosDescuentos", Integer.parseInt(modulo),
						reemplazar);

				encabezados = service.getCamposListado(ConectorPool.ESQUEMA_SYSMAN, sqlExcel);

				encabezados.remove(4);
				encabezados.remove(3);

				conexion.conectar(ConectorPool.ESQUEMA_SYSMAN);
				consulta = conexion.getConection().createStatement();
				valores = consulta.executeQuery(sqlExcel);

			}

			long existeDatos = service.getConteoConsulta(revisarExtras ? strSql : sqlExcel);

			if (existeDatos != 0 && revisarExtras) {
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN,
						ReportesBean.FORMATOS.EXCEL);
			} else if (existeDatos != 0) {
				armarExcel(encabezados, valores);
			} else {
				ejecutaractualizaAlerta();

			}

		} catch (JRException | IOException | SQLException | DRException | SysmanException | SystemException
				| NamingException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		} finally {

			try {
				if (consulta != null) {
					consulta.close();
				}
				if (valores != null) {
					valores.close();
				}
			} catch (

			SQLException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	public void generarArchivoPlano() {
			
	    	try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	    		
	            String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
	            String reporte = "";
	            
	            if(maestra) {
	            		reporte = "3"; //800147DevengosYDescuentosMaestra
	            	}else if (revisarExtras) { 
	            			reporte = "2"; //800127RevisarExtras
	            		} else {
	            			reporte = "1"; //800143DevengosDescuentos
	            }
	            
	    		String sql = ejbNominaSiete.getArmarConsultatDevengosDescuentos(compania,ano,mes,proceso,periodo,reporte);
	    		
	    		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
	                    ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL, reporte);
	    		
	    		Workbook workbook = new XSSFWorkbook(
	    				JsfUtil.exportarHojaDatosStreamed(sql,
	    						ConectorPool.ESQUEMA_SYSMAN,
	    						FORMATOS.EXCEL).getStream());

	    		Sheet sheet = workbook.getSheetAt(0);

	    		sheet.shiftRows(0, sheet.getLastRowNum(),2);

	    		sheet.createFreezePane(0,3);

	    		Font font2 = workbook.createFont();
	    		font2.setFontName("Calibri");
	    		font2.setFontHeightInPoints((short) 11);
	    		font2.setBold(false);

	    			Row r = sheet.createRow(0);
	    			Cell cell1 = r.createCell(0);
	    			cell1 = r.createCell(1);
	    			cell1.setCellValue(nombreCompania.toUpperCase());
	    			
	    			Row r2 = sheet.createRow(1);
	    			Cell cell2 = r2.createCell(0);
	    			cell2 = r2.createCell(1);
	    			cell2.setCellValue("Nomina del periodo "
	    					+ ejbNominaUno.getNombrePeriodo(compania, Integer.parseInt(proceso), Integer.parseInt(ano),
	    							Integer.parseInt(mes), Integer.parseInt(periodo))
	    					+ " de " + ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes)) + " de " + ano+"".toUpperCase());	    			
	    			    			
	    		workbook.write(out);

	    		archivoDescarga = JsfUtil.getArchivoDescarga(
	    				new ByteArrayInputStream(out.toByteArray()), "DEVENGOS_Y_DESCUENTOS_" + ano + "_" + mes + "_" + periodo + ".xlsx");
	    		workbook.close(); 	

	    	}
	    	catch ( JRException | IOException | DRException | SQLException | SysmanException | NumberFormatException | SystemException e) {
	    		logger.error(e.getMessage(), e);
	    		JsfUtil.agregarMensajeError(e.getMessage());
	    	} 

	
			//ejecutaractualizaAlerta();


	}

	private void armarExcel(List<String> encabezados, ResultSet valores) {

		sheet = workbook.createSheet("Devengos_y_Descuentos");

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			// Crear titulo Excel
			crearTitulos(sheet);

			// Crear encabezados
			crearEncabezados(encabezados);

			// Asignar valores a la hoja
			asignarValores(valores);

			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"DEVENGOS_Y_DESCUENTOS_" + ano + "_" + mes + "_" + periodo + ".xlsx");

			workbook.close();

		} catch (IOException | JRException | NumberFormatException ex) {

			logger.error(ex.getMessage(), ex);
			JsfUtil.agregarMensajeError(ex.getMessage());
		}

	}

	private void crearTitulos(XSSFSheet sheetEncabezado) {
		try {

			// Fuente encabezados
			XSSFFont font = workbook.createFont();
			font.setFontHeightInPoints((short) 10);
			font.setFontName("Tahoma");
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			// Estilo de encabezados
			styleEncabezado = workbook.createCellStyle();
			styleEncabezado.setAlignment(CellStyle.ALIGN_CENTER);
			styleEncabezado.setBorderBottom((short) 1);
			styleEncabezado.setBorderLeft((short) 1);
			styleEncabezado.setBorderTop((short) 1);
			styleEncabezado.setBorderRight((short) 1);
			styleEncabezado.setFont(font);

			// Fuente textos
			XSSFFont fontTextos = workbook.createFont();
			fontTextos.setFontHeightInPoints((short) 9);
			fontTextos.setFontName("Tahoma");

			// Estilo textos
			styleMoneda = workbook.createCellStyle();
			styleTextos = workbook.createCellStyle();
			styleTextos.setAlignment(CellStyle.ALIGN_LEFT);
			styleTextos.setBorderBottom((short) 1);
			styleTextos.setBorderLeft((short) 1);
			styleTextos.setBorderTop((short) 1);
			styleTextos.setBorderRight((short) 1);
			styleTextos.setFont(fontTextos);

			// Estilo modeda

			styleMoneda.setAlignment(CellStyle.ALIGN_RIGHT);
			styleMoneda.setBorderBottom((short) 1);
			styleMoneda.setBorderLeft((short) 1);
			styleMoneda.setBorderTop((short) 1);
			styleMoneda.setBorderRight((short) 1);
			styleMoneda.setFont(fontTextos);
			styleMoneda.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

			// Primer titulo
			XSSFRow rowTitulo = sheetEncabezado.createRow(0);
			XSSFCell cellTitulo = rowTitulo.createCell(1);
			cellTitulo.setCellValue(SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());

			cellTitulo.setCellStyle(styleEncabezado);
			CellUtil.setAlignment(cellTitulo, workbook, CellStyle.ALIGN_CENTER);

			// Region1 del titulo
			CellRangeAddress range1 = new CellRangeAddress(0, 0, 1, 4);

			RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, range1, sheetEncabezado, workbook);
			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, range1, sheetEncabezado, workbook);
			RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, range1, sheetEncabezado, workbook);
			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, range1, sheetEncabezado, workbook);

			// Segundo titulo
			XSSFRow rowTituloDos = sheetEncabezado.createRow(1);
			XSSFCell cellTituloDos = rowTituloDos.createCell(1);

			cellTituloDos.setCellValue("Nomina del periodo "
					+ ejbNominaUno.getNombrePeriodo(compania, Integer.parseInt(proceso), Integer.parseInt(ano),
							Integer.parseInt(mes), Integer.parseInt(periodo))
					+ " de " + ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes)) + " de " + ano);

			cellTituloDos.setCellStyle(styleEncabezado);
			CellUtil.setAlignment(cellTituloDos, workbook, CellStyle.ALIGN_CENTER);

			// Region2 del subtitulo
			CellRangeAddress range2 = new CellRangeAddress(1, 1, 1, 4);

			RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, range2, sheetEncabezado, workbook);
			RegionUtil.setBorderTop(CellStyle.BORDER_THIN, range2, sheetEncabezado, workbook);
			RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, range2, sheetEncabezado, workbook);
			RegionUtil.setBorderRight(CellStyle.BORDER_THIN, range2, sheetEncabezado, workbook);

			sheetEncabezado.addMergedRegion(range1);
			sheetEncabezado.addMergedRegion(range2);

			sheetEncabezado.createFreezePane(1, 4);

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void crearEncabezados(List<String> encabezados) {
		XSSFRow rowEncabezados = sheet.createRow(3);

		XSSFCell cellEncabezado = rowEncabezados.createCell(0);
		cellEncabezado.setCellValue(encabezados.get(3).replace("'", "").replace("_", " "));
		cellEncabezado.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(0);

		XSSFCell cellEncabezadoDos = rowEncabezados.createCell(1);
		cellEncabezadoDos.setCellValue(encabezados.get(4).replace("'", "").replace("_", " "));
		cellEncabezadoDos.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(1);

		XSSFCell cellEncabezadocuantro = rowEncabezados.createCell(2);
		cellEncabezadocuantro.setCellValue(encabezados.get(5).replace("'", "").replace("_", " "));
		cellEncabezadocuantro.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(2);

		XSSFCell cellEncabezadoCinco = rowEncabezados.createCell(3);
		cellEncabezadoCinco.setCellValue(encabezados.get(6).replace("'", "").replace("_", " "));
		cellEncabezadoCinco.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(3);

		XSSFCell cellEncabezadoSeis = rowEncabezados.createCell(4);
		cellEncabezadoSeis.setCellValue(encabezados.get(7).replace("'", "").replace("_", " "));
		cellEncabezadoSeis.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(4);

		XSSFCell cellEncabezadoSiete = rowEncabezados.createCell(5);
		cellEncabezadoSiete.setCellValue(encabezados.get(8).replace("'", "").replace("_", " "));
		cellEncabezadoSiete.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(5);

		XSSFCell cellEncabezadoOcho = rowEncabezados.createCell(6);
		cellEncabezadoOcho.setCellValue(encabezados.get(9).replace("'", "").replace("_", " "));
		cellEncabezadoOcho.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(6);

		XSSFCell cellEncabezadoNueve = rowEncabezados.createCell(7);
		cellEncabezadoNueve.setCellValue(encabezados.get(10).replace("'", "").replace("_", " "));
		cellEncabezadoNueve.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(7);

		XSSFCell cellEncabezadoDiez = rowEncabezados.createCell(8);
		cellEncabezadoDiez.setCellValue(encabezados.get(11).replace("'", "").replace("_", " "));
		cellEncabezadoDiez.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(8);

		XSSFCell cellEncabezadoOnce = rowEncabezados.createCell(9);
		cellEncabezadoOnce.setCellValue(encabezados.get(12).replace("'", "").replace("_", " "));
		cellEncabezadoOnce.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(9);

		XSSFCell cellEncabezadoDoce = rowEncabezados.createCell(10);
		cellEncabezadoDoce.setCellValue(encabezados.get(0).replace("'", "").replace("_", " "));
		cellEncabezadoDoce.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(10);

		XSSFCell cellEncabezadoTrece = rowEncabezados.createCell(11);
		cellEncabezadoTrece.setCellValue(encabezados.get(1).replace("'", "").replace("_", " "));
		cellEncabezadoTrece.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(11);

		XSSFCell cellEncabezadoCatorce = rowEncabezados.createCell(12);
		cellEncabezadoCatorce.setCellValue(encabezados.get(2).replace("'", "").replace("_", " "));
		cellEncabezadoCatorce.setCellStyle(styleEncabezado);
		sheet.autoSizeColumn(12);

		encabezados.remove(3);
		encabezados.remove(4);

		for (int i = 11; i < encabezados.size(); i++) {

			XSSFCell cellEncabezadoTres = rowEncabezados.createCell(i + 2);
			cellEncabezadoTres.setCellValue(encabezados.get(i).replace("'", "").replace("_", " "));
			cellEncabezadoTres.setCellStyle(styleEncabezado);
			sheet.autoSizeColumn(i + 2);

		}

	}

	private void asignarValores(ResultSet valores) {

		int i = 4;
		int j;
		try {

			while (valores.next()) {

				int nColumnas = valores.getMetaData().getColumnCount();

				XSSFRow rowValores = sheet.createRow(i);

				XSSFCell cellCodUbicacion = rowValores.createCell(0);
				cellCodUbicacion.setCellValue(SysmanFunciones.nvlStr(valores.getString(6), ""));
				cellCodUbicacion.setCellStyle(styleTextos);
				sheet.autoSizeColumn(0);

				XSSFCell cellUbicacion = rowValores.createCell(1);
				cellUbicacion.setCellValue(SysmanFunciones.nvlStr(valores.getString(7), ""));
				cellUbicacion.setCellStyle(styleTextos);
				sheet.autoSizeColumn(1);

				XSSFCell cellCodFuente = rowValores.createCell(2);
				cellCodFuente.setCellValue(SysmanFunciones.nvlStr(valores.getString(8), ""));
				cellCodFuente.setCellStyle(styleTextos);
				sheet.autoSizeColumn(2);

				XSSFCell cellFuente = rowValores.createCell(3);
				cellFuente.setCellValue(SysmanFunciones.nvlStr(valores.getString(9), ""));
				cellFuente.setCellStyle(styleTextos);
				sheet.autoSizeColumn(3);

				XSSFCell cellCodProyecto = rowValores.createCell(4);
				cellCodProyecto.setCellValue(SysmanFunciones.nvlStr(valores.getString(10), ""));
				cellCodProyecto.setCellStyle(styleTextos);
				sheet.autoSizeColumn(4);

				XSSFCell cellProyecto = rowValores.createCell(5);
				cellProyecto.setCellValue(SysmanFunciones.nvlStr(valores.getString(11), ""));
				cellProyecto.setCellStyle(styleTextos);
				sheet.autoSizeColumn(5);

				XSSFCell cellFondoSalud = rowValores.createCell(6);
				cellFondoSalud.setCellValue(SysmanFunciones.nvlStr(valores.getString(12), ""));
				cellFondoSalud.setCellStyle(styleTextos);
				sheet.autoSizeColumn(6);

				XSSFCell cellFondoPensiones = rowValores.createCell(7);
				cellFondoPensiones.setCellValue(SysmanFunciones.nvlStr(valores.getString(13), ""));
				cellFondoPensiones.setCellStyle(styleTextos);
				sheet.autoSizeColumn(7);

				XSSFCell cellCedula = rowValores.createCell(8);
				cellCedula.setCellValue(SysmanFunciones.nvlStr(valores.getString(14), ""));
				cellCedula.setCellStyle(styleTextos);
				sheet.autoSizeColumn(8);

				XSSFCell cellNombreEmpleado = rowValores.createCell(9);
				cellNombreEmpleado.setCellValue(SysmanFunciones.nvlStr(valores.getString(15), ""));
				cellNombreEmpleado.setCellStyle(styleTextos);
				sheet.autoSizeColumn(9);

				XSSFCell cellDevengos = rowValores.createCell(10);
				cellDevengos.setCellValue(Double.parseDouble(SysmanFunciones.nvlStr(valores.getString(1), "0.0")));
				cellDevengos.setCellStyle(styleMoneda);
				sheet.autoSizeColumn(10);

				XSSFCell cellDescuentos = rowValores.createCell(11);
				cellDescuentos.setCellValue(Double.parseDouble(SysmanFunciones.nvlStr(valores.getString(2), "0.0")));
				cellDescuentos.setCellStyle(styleMoneda);
				sheet.autoSizeColumn(11);

				XSSFCell cellNetos = rowValores.createCell(12);
				cellNetos.setCellValue(Double.parseDouble(SysmanFunciones.nvlStr(valores.getString(3), "0.0")));
				cellNetos.setCellStyle(styleMoneda);
				sheet.autoSizeColumn(12);

				XSSFCell cellCuentas = rowValores.createCell(13);
				cellCuentas.setCellValue(SysmanFunciones.nvlStr(valores.getString(16), ""));
				cellCuentas.setCellStyle(styleTextos);
				sheet.autoSizeColumn(13);

				for (j = 14; j <= nColumnas - 3; j++) {
					XSSFCell cellValorConceptos = rowValores.createCell(j);
					cellValorConceptos
							.setCellValue(Double.parseDouble(SysmanFunciones.nvlStr(valores.getString(j + 3), "0.0")));
					cellValorConceptos.setCellStyle(styleMoneda);

					sheet.autoSizeColumn(j);
				}

				i++;

			}

			/*
			 * XSSFRow rowValoresTotales = sheet.createRow(i);
			 * 
			 * XSSFCell cellValorTotales = rowValoresTotales.createCell(2);
			 * cellValorTotales.setCellFormula( "SUM($C$5:" + "$C$" + i + ")");
			 */

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private Map<String, Object> generarPivot() {

		Map<String, Object> miRetorno = new HashMap<>();
		StringBuilder cadenaPivot = new StringBuilder();
		Map<String, Object> param = new TreeMap<>();
		StringBuilder aliasPivot = new StringBuilder();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.MES.getName(), mes);
		param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

		try {
			List<Registro> registro = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													DevengosDescuentosControladorUrlEnum.URL9898.getValue())
											.getUrl(),
									param));

			for (Registro reg : registro) {

				if (SysmanFunciones.esBdSqlServer()) {
					cadenaPivot.append("[" + reg.getCampos().get("NOMBRECONCEPTO") + "]" + ",");
					aliasPivot.append("ISNULL([" + reg.getCampos().get("NOMBRECONCEPTO") + "],0) AS ["
							+ reg.getCampos().get("NOMBRECONCEPTO") + "],");

				} else {
					cadenaPivot.append("'" + reg.getCampos().get("NOMBRECONCEPTO") + "'" + ",");
				}
			}

			if (!SysmanFunciones.validarVariableVacio(cadenaPivot.toString())) {

				cadenaPivot.deleteCharAt(cadenaPivot.length() - 1);
				if (SysmanFunciones.esBdSqlServer()) {
					aliasPivot.deleteCharAt(aliasPivot.length() - 1);

				}
			}

			miRetorno.put("cadena", cadenaPivot);
			if (SysmanFunciones.esBdSqlServer()) {

				miRetorno.put("alias", aliasPivot);
			}
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return miRetorno;
	}

	/**
	 * Define las acciones necesarias para generar el informe realiza el reemplazo
	 * de valores en la consulta del informe y env�a los par�metros definidos
	 * 
	 * @param formato Formato seleccionado por el usuario para generar el informe
	 */
	public void generarInforme(ReportesBean.FORMATOS formato) {
		archivoDescarga = null;

		try {

			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put(compania, compania);
			reemplazar.put("proceso", proceso);
			reemplazar.put("ano", ano);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);

			nombreReporte = "";

			Map<String, Object> parametros = new HashMap<>();

			cargarParametros(parametros);

			consultaInforme = DevengosDescuentosControladorEnum.PLANILLAVIATICOSUES.getValue();	

			if (pensionados) {
				String pensionados1 = ejbSysmanUtil.consultarParametro(compania, "PLANILLA DE DEVENGOS Y DESCUENTOS",
						modulo, new Date(), false);

				if (pensionados1.equals("002209PlanillaDescuentosCDCHONDAPENSIONADOS")) {

					pensionados1 = nombreReporte;
					pensionados1 = consultaInforme;
				}

			}
			String devengos = ejbSysmanUtil.consultarParametro(compania, "PLANILLA DE DEVENGOS Y DESCUENTOS", modulo,
					new Date(), false);

			String pensionados = ejbSysmanUtil.consultarParametro(compania, "PLANILLA DE DEVENGOS Y DESCUENTOS", modulo,
					new Date(), false);

			String viaticosReporte = ejbSysmanUtil.consultarParametro(compania, "FORMATO PLANILLA DE VIATICOS", modulo,
					new Date(), false);
			
			if (viaticos)
			{
				nombreReporte = viaticosReporte;
			}

			if (!validarViaticosPrimasExtras() && !validarProcesoNueveOcho()
					&& "800.091.594-4".equals(SessionUtil.getCompaniaIngreso().getNit())) {
				comparacionPrenomina = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"MANEJA COMPARACION DE PRENOMINA", modulo, new Date(), true), "NO");
				reemplazar.put("estadoPersonal", " ");
				validarInformeSegunNit(reemplazar, parametros);
				// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz mu�oz)
				parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
				// FIN IMPLEMENTACION MARCA_BLANCA
			}

			if (SysmanFunciones.validarVariableVacio(nombreReporte)) {
				nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"PLANILLA DE DEVENGOS Y DESCUENTOS", modulo, new Date(), false),
						"001678PLANILLADEVENGOSCDCCBOYACA");
				consultaInforme = nombreReporte;
				
				if (nombreReporte.toUpperCase().equals("PLANILLADEDEVENGOSYDESCUENTOSRV_1")) {
					nombreReporte = "002487PlanillaDevengosRV_1";
					consultaInforme = "002487PlanillaDevengosRV_1";
				}

			} else if (devengos.equals("001877DEVENGOSCDCGOBCAQUETA")) {
				nombreReporte = "001877DEVENGOSCDCGOBCAQUETA";
				consultaInforme = "001877DEVENGOSCDCGOBCAQUETA";

			} else if (devengos.equals("002028PLANILLADEVENGOSCDCFND")) {
				nombreReporte = "002028PLANILLADEVENGOSCDCFND";
				consultaInforme = "002028PLANILLADEVENGOSCDCFND";

			}

			else if (pensionados.equals("001896PlanillaDescuentosPensionadosGOBCAQUETA")) {
				consultaInforme = "001896PlanillaDescuentosPensionadosGOBCAQUETA";
				nombreReporte = "001896PlanillaDescuentosPensionadosGOBCAQUETA";
				cargarParametros(parametros);

			} else if ("001770PlanillaUPC".equals(nombreReporte)) {
				consultaInforme = nombreReporte;

				String nombreRevisor = ejbSysmanUtil.consultarParametro(compania, "CARGO JEFE RECURSOS HUMANOS", modulo,
						new Date(), true);
				parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS", nombreRevisor);

			} else if (devengos.equals("001953PLANILLADEVENGOSCDCGOBCAQUETA01") && periodo.equals("1")) {
				consultaInforme = devengos;
				nombreReporte = devengos;
			} else if (devengos.equals("001954PLANILLADEVENGOSCDCGOBCAQUETA07") && periodo.equals("7")) {
				consultaInforme = devengos;
				nombreReporte = devengos;

			} else if (viaticos && viaticosReporte.equals("001827PLANILLAVIATICOSIDSN")) {
				consultaInforme = "001827PLANILLAVIATICOSIDSN";
				nombreReporte = "001827PLANILLAVIATICOSIDSN";
			} else if (devengos.equals("001954PLANILLADEVENGOSCDCGOBCAQUETA07") && periodo.equals("7")) {
				consultaInforme = devengos;
				nombreReporte = devengos;

			} else if (devengos.equals("002589PlanillaDescuentosCDSINCHI")) {
				consultaInforme = devengos;
				nombreReporte = devengos;

			}

			parametros.put("PR_PERIODO", ("Periodo " + nombrePeriodoNomina + " de " + nombreMes));

			Reporteador.resuelveConsulta(consultaInforme, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(nombreReporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

			if (devengos.equals("002184PlanillaDevengosCDCHONDA")) {
				String[] informe = new String[2];
				informe[0] = devengos;
				informe[1] = "002185PlanillaDescuentosCDCHONDA";
				Reporteador.resuelveConsulta(informe[0], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
						parametros);

				ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

				salidas[0] = JsfUtil.serializarReporte(informe[0], parametros, ConectorPool.ESQUEMA_SYSMAN,
						ReportesBean.FORMATOS.PDF);

				Reporteador.resuelveConsulta(informe[1], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
						parametros);

				salidas[1] = JsfUtil.serializarReporte(informe[1], parametros, ConectorPool.ESQUEMA_SYSMAN,
						ReportesBean.FORMATOS.PDF);
				String[] nombresArchivos = new String[2];

				if (ReportesBean.FORMATOS.PDF.equals(ReportesBean.FORMATOS.PDF)) {
					nombresArchivos[0] = "002184PlanillaDevengosCDCHONDA.pdf";
					nombresArchivos[1] = "002185PlanillaDescuentosCDCHONDA.pdf";
				}

				archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

			} else if (devengos.equals("002412PlanillaDevengosCortolima") || devengos.equals("002413PlanillaDescuentosCortolima")) {
				String[] informe = new String[2];
				informe[0] = "002412PlanillaDevengosCortolima";
				informe[1] = "002413PlanillaDescuentosCortolima";
				Reporteador.resuelveConsulta(informe[0], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
						parametros);

				ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

				salidas[0] = JsfUtil.serializarReporte(informe[0], parametros, ConectorPool.ESQUEMA_SYSMAN,
						formato);

				Reporteador.resuelveConsulta(informe[1], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
						parametros);

				salidas[1] = JsfUtil.serializarReporte(informe[1], parametros, ConectorPool.ESQUEMA_SYSMAN,
						formato);
				String[] nombresArchivos = new String[2];

				if (ReportesBean.FORMATOS.PDF.equals(formato)) {
					nombresArchivos[0] = "002412PlanillaDevengosCortolima.pdf";
					nombresArchivos[1] = "002413PlanillaDescuentosCortolima.pdf";
				}
				else {
					nombresArchivos[0] = "002412PlanillaDevengosCortolima.xlsx";
					nombresArchivos[1] = "002413PlanillaDescuentosCortolima.xlsx";
				}

				archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

			} else if (devengos.toUpperCase().equals("PLANILLADEDEVENGOSYDESCUENTOSRV_1") || devengos.toUpperCase().equals("002487PLANILLADEVENGOSRV_1") || devengos.toUpperCase().equals("002488PLANILLADESCUENTOSRV_1") ) {
				String[] informe = new String[2];
				informe[0] = "002487PlanillaDevengosRV_1";
				informe[1] = "002488PlanillaDescuentosRV_1";
				Reporteador.resuelveConsulta(informe[0], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
						parametros);

				ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

				salidas[0] = JsfUtil.serializarReporte(informe[0], parametros, ConectorPool.ESQUEMA_SYSMAN,formato);
				
				Reporteador.resuelveConsulta(informe[1], Integer.valueOf(SessionUtil.getModulo()), reemplazar,
						parametros);

				salidas[1] = JsfUtil.serializarReporte(informe[1], parametros, ConectorPool.ESQUEMA_SYSMAN,formato);
				String[] nombresArchivos = new String[2];

				if (formato.equals(ReportesBean.FORMATOS.PDF)) {
					nombresArchivos[0] = "002487PlanillaDevengosRV_1.pdf";
					nombresArchivos[1] = "002488PlanillaDescuentosRV_1.pdf";
				}
				else {
					nombresArchivos[0] = "002487PlanillaDevengosRV_1.xls";
					nombresArchivos[1] = "002488PlanillaDescuentosRV_1.xls";
				}

				archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

			}

		} catch (SystemException | SysmanException | JRException | IOException | SQLException | DRException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	/**
	 * Metodo que valida la accion de los indicadores de Viaticos y Totales para
	 * generar el informe correspondiente
	 * 
	 * @return
	 */
	private boolean validarTotalesViaticos() {
		try {
			if (viaticos && "1".equals(periodo)) {

				nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"FORMATO PLANILLA DE VIATICOS", modulo, new Date(), true),
						DevengosDescuentosControladorEnum.PLANILLAVIATICOSUES.getValue());

				return true;

			} else if (totales) {

				nombreReporte = consultaInforme = "001699RESUMENPAGOSDENOMINA";

				return true;

			}
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
		return false;
	}

	/**
	 * Metodo que valida la accion de los indicadores de Viaticos y Listado de
	 * primas para generar el informe correspondiente
	 * 
	 * @return
	 */
	private boolean validarViaticosPrimasExtras() {

		try {
			String horasExtras = SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "LIQUIDAN HORAS EXTRAS", modulo, new Date(), true),
					"NO");
			if (validarTotalesViaticos()) {

				return true;

			} else if (primas) {
				nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"PLANILLA DE DEVENGOS Y DESCUENTOS PRIMAS", modulo, new Date(), true),
						"001627PLANILLADEVENGOSCDCUESPRIMAS");
				return true;

			} else if ("SI".equals(horasExtras) && extras) {
				nombreReporte = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
						"FORMATO INFORME EXTRAS MENSUALES", modulo, new Date(), true), "001629PlanillaResumenEXTRAS");
				consultaInforme = "001629PlanillaResumenEXTRAS";
			}
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}
		return false;
	}

	/**
	 * Metodo que valida la generacion del informe si el proceso seleccionado es el
	 * 98
	 * 
	 * @return
	 */
	public boolean validarProcesoNueveOcho() {
		if ("98".equals(proceso)) {
			if (pensionados && "800095728-2".equals(SessionUtil.getCompaniaIngreso().getNit())) {

				if (validarReportesPrimas()) {
					return true;
				}
			} else {
				nombreReporte = "001636PlanillaDescuentosPensionadosFLORENCIA";
				consultaInforme = "001636PlanillaDescuentosPensionadosFLORENCIA";
				return true;
			}
		}

		return false;

	}

	/**
	 * Metodo que valida que informe se va a generar segun periodo y mes
	 * seleccionado
	 * 
	 * @return
	 */
	public boolean validarReportesPrimas() {
		if ("4".equals(periodo)) {
			if ("6".equals(mes)) {
				nombreReporte = "001637PlanillaPrimadeJunioPensionados";
				consultaInforme = "001637PlanillaPrimadeJunioPensionados";
				return true;
			} else if ("12".equals(mes)) {
				nombreReporte = "001640PlanillaPrimadeNavidadPensionados";
				return true;
			}
		}

		return false;
	}

	/**
	 * Metodo que valida el informe a generar cuando el nit de la compania es igual
	 * a 800.091.594-4
	 * 
	 * @param reemplazar
	 * @param parametros
	 * @return
	 */
	public boolean validarInformeSegunNit(Map<String, Object> reemplazar, Map<String, Object> parametros) {
		if ("1".equals(periodo)) {
			consultaInforme = "001641PLANILLADEVGOBCAQUETAPRELIMINAR";
			if ("SI".equals(comparacionPrenomina) && informePrenomina) {
				reemplazar.put("estadoPersonal", "  AND PERSONAL.ESTADO_ACTUAL NOT IN(0)");
				parametros.put(DevengosDescuentosControladorEnum.PR_PRELIMINAR.getValue(), " PRELIMINAR");

			} else {

				parametros.put(DevengosDescuentosControladorEnum.PR_PRELIMINAR.getValue(), " ");
			}
			nombreReporte = "001641PLANILLADEVGOBCAQUETAPRELIMINAR";
			return true;
		} else if (validarPeriodoSiete(parametros)) {
			return true;

		} else {

			if ("SI".equals(comparacionPrenomina) && informePrenomina) {

				nombreReporte = "001675DEVENGOSCDCGOBCAQUETA03";
				return true;

			}

		}
		return false;
	}

	/**
	 * Metodo que valida el periodo 7 para cuando se ingresa con la compania que
	 * tiene el numero de nit 800.091.594-4
	 * 
	 * @param parametros
	 * @return
	 */
	public boolean validarPeriodoSiete(Map<String, Object> parametros) {
		if ("7".equals(periodo)) {
			if ("SI".equals(comparacionPrenomina) && informePrenomina) {
				parametros.put(DevengosDescuentosControladorEnum.PR_PRELIMINAR.getValue(), " PRELIMINAR");
			} else {
				parametros.put(DevengosDescuentosControladorEnum.PR_PRELIMINAR.getValue(), " ");
			}
			nombreReporte = "001645PLANILLADEVGOBCAQUETA07";
			return true;

		}
		return false;
	}

	/**
	 * Metodo que valida si los el proceso, a�o, mes y periodo estan vacios
	 * 
	 * @return
	 */
	public boolean validarListas() {
		if (SysmanFunciones.validarVariableVacio(proceso)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3959"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(ano)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB3960"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(mes)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2571"));
			return true;
		}
		if (SysmanFunciones.validarVariableVacio(periodo)) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2572"));
			return true;
		}
		return false;
	}

	/**
	 * Realiza el reemplazo de los parametros definidos en cada reporte
	 * 
	 * @param param
	 */
	public void cargarParametros(Map<String, Object> param) {

		try {

			headerEspecial = ejbSysmanUtil.consultarParametro(compania, "FORMATOS ESPECIALES BUCARAMANGA", modulo,
					new Date(), true);

			String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

			param.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getSigla().toUpperCase());
			Date fechaPeriodoIni = ejbNominaCero.getFechaPeriodoIniFin(compania, Integer.parseInt(proceso),
					Integer.parseInt(ano), Integer.parseInt(mes), Integer.parseInt(periodo), true, false);

			Date fechaPeriodoFin = ejbNominaCero.getFechaPeriodoIniFin(compania, Integer.parseInt(proceso),
					Integer.parseInt(ano), Integer.parseInt(mes), Integer.parseInt(periodo), false, false);
			param.put("PR_PERIODOINICIO", SysmanFunciones.convertirAFechaCadena(fechaPeriodoIni));
			param.put("PR_PERIODOFINAL", SysmanFunciones.convertirAFechaCadena(fechaPeriodoFin));
			param.put("PR_CONCEPTOPAGO", conceptoPago.toUpperCase());
			param.put("PR_OBSERVACION", observacion);
			param.put("PR_NOMBREDELGERENTE", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", modulo, new Date(), true), " "));
			param.put("PR_FORMS_DEVENGOS_Y_DESCUENTOS_OBSERVACION", observacion);
			param.put("PR_CARGODELGERENTE", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", modulo, new Date(), true), " "));
			param.put("PR_NOMBREDEQUIENAUTORIZA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true), " "));
			param.put("PR_CARGODEQUIENAUTORIZA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true), " "));
			param.put("PR_NOMBREDEQUIENREVISA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN REVISA NOMINA", modulo, new Date(), true), " "));
			param.put("PR_CARGODEQUIENREVISA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN REVISA NOMINA", modulo, new Date(), true), " "));
			param.put("PR_NOMBREDEQUIENLIQUIDA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN LIQUIDA NOMINA", modulo, new Date(), true), " "));
			param.put("PR_CARGODEQUIENLIQUIDA", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN LIQUIDA NOMINA", modulo, new Date(), true), " "));
			// bcardenas: Se agregan parametros de firmas para el
			// reporte con indicador viaticos
			param.put("PR_NOMBRE_DEL_GERENTE", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", modulo, new Date(), true), " "));
			param.put("PR_CARGO_DEL_GERENTE", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", modulo, new Date(), true), " "));

			param.put("PR_NOMBRE_DE_QUIEN_APRUEBA_PLANILLA_VIATICOS",
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"NOMBRE DE QUIEN APRUEBA PLANILLA VIATICOS", modulo, new Date(), true), " "));
			param.put("PR_CARGO_DE_QUIEN_APRUEBA_PLANILLA_VIATICOS",
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"CARGO DE QUIEN APRUEBA PLANILLA VIATICOS", modulo, new Date(), true), " "));
			param.put("PR_NOMBRE_DE_QUIEN_ELABORA_PLANILLA_VIATICOS",
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"NOMBRE DE QUIEN ELABORA PLANILLA VIATICOS", modulo, new Date(), true), " "));
			param.put("PR_CARGO_DE_QUIEN_ELABORA_PLANILLA_VIATICOS",
					SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
							"CARGO DE QUIEN ELABORA PLANILLA VIATICOS", modulo, new Date(), true), " "));

			param.put("PR_NOMBRE_SECRETARIO_GENERAL", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE SECRETARIO GENERAL", modulo, new Date(), true), " "));
			param.put("PR_CARGO_SECRETARIO_GENERAL", SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CARGO SECRETARIO GENERAL", modulo, new Date(), true), " "));
			// bcardenas

			// Inicio dcastiblanco
			param.put("PR_ELABORADO_POR", SysmanFunciones.nvlStr(
					ejbSysmanUtil.consultarParametro(compania, "ELABORADO POR", modulo, new Date(), true), " "));
			param.put("PR_NOMBREDEQUIENAUTORIZANOMINA", SysmanFunciones.nvlStr(ejbSysmanUtil
					.consultarParametro(compania, "NOMBRE DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true), " "));
			// Fin dcastiblanco

			param.put("PR_MESVIATICOS",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mesViaticos)].toUpperCase());
			
			if (viaticos && "1".equals(periodo)) {
				param.put("PR_MESVIATICOS",
						SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)].toUpperCase());

			} else {
				param.put("PR_NOMBREMES",
						SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.parseInt(mes)].toUpperCase());

				param.put("PR_VALORPERIODO", "7".equals(periodo) ? "** VACACIONES **" : "ADMINISTRACION");
				param.put("PR_NOMBREDELJEFEDERECURSOSHUMANOS", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "NOMBRE JEFE RECURSOS HUMANOS", modulo, new Date(), true), " "));
				param.put("PR_CARGOJEFERECURSOSHUMANOS", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "CARGO JEFE RECURSOS HUMANOS", modulo, new Date(), true), " "));
				param.put("PR_NOMBREDELCARGOTESOREROPAGADOR",
						SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
								"NOMBRE DEL CARGO TESORERO PAGADOR", modulo, new Date(), true), " "));
				param.put("PR_CARGODELTESOREROPAGADOR", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "CARGO DEL TESORERO PAGADOR", modulo, new Date(), true), " "));
				param.put("PR_NOMBREDELSECRETARIOADMINISTRATIVO",
						SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
								"NOMBRE DEL SECRETARIO ADMINISTRATIVO", modulo, new Date(), true), " "));
				param.put("PR_NOMBREDEJEFEDEPRESUPUESTO", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "NOMBRE DE JEFE DE PRESUPUESTO", modulo, new Date(), true), " "));
				param.put("PR_CARGODELJEFEDEPRESUPUESTO", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "CARGO DEL JEFE DE PRESUPUESTO", modulo, new Date(), true), " "));
				param.put("PR_NOMBREJEFERECURSOSHUMANOS", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "NOMBRE JEFE RECURSOS HUMANOS", modulo, new Date(), true), " "));
				param.put("PR_CARGODEQUIENAUTORIZANOMINA",
						SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
								"CARGO DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true), " "));

				param.put("PR_NOMBRE_SECRETARIO_GENERAL", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "NOMBRE SECRETARIO GENERAL", modulo, new Date(), true), " "));
				param.put("PR_CARGO_SECRETARIA_GENERAL", SysmanFunciones.nvlStr(ejbSysmanUtil
						.consultarParametro(compania, "CARGO SECRETARIO GENERAL", modulo, new Date(), true), " "));
				param.put("PR_NOMBRE_DEL_CONTADOR", SysmanFunciones.nvlStr(
						ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL CONTADOR", modulo, new Date(), true),
						" "));
				param.put("PR_CARGO_DEL_CONTADOR", SysmanFunciones.nvlStr(
						ejbSysmanUtil.consultarParametro(compania, "CARGO DEL CONTADOR", modulo, new Date(), true),
						" "));
				String nomPeriodo = service.buscarEnLista(periodo, "PERIODO", "NOM_PERIODO", listaPeriodo);
				param.put("PR_NOMBREPERIODO", nomPeriodo.toUpperCase());

				param.put("PR_ENCABEZADO", SysmanFunciones
						.concatenar(SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer.valueOf(mes)], " de ", ano));

				// LMOSQUERA
				String jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
						"NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
				String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
						"CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false);
				String jefeNomina = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE NOMINA", modulo, new Date(),
						false);
				String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
						"CARGO RESPONSABLE DE NOMINA", modulo, new Date(), false);

				param.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
				param.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
				param.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
				param.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);

			}

			if ((pensionados && !primas && !extras && !viaticos && !totales && !maestra)
					|| (!pensionados && !primas && !extras && !viaticos && !totales && !maestra)) {
				param.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI") ? true : false);
				param.put("PR_IMAGEN_ESPECIAL", sticker);
			} else {
				param.put("PR_HEADER_ESPECIAL", headerEspecial.equals("NO") ? true : false);
				param.put("PR_IMAGEN_ESPECIAL", sticker);
			}
			
			// IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz mu�oz)
            param.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
		} catch (NumberFormatException | SystemException | ParseException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable extras
	 * 
	 * @return extras
	 */
	public boolean getExtras() {
		return extras;
	}

	/**
	 * Asigna la variable extras
	 * 
	 * @param extras Variable a asignar en extras
	 */
	public void setExtras(boolean extras) {
		this.extras = extras;
	}

	/**
	 * Retorna la variable pensionados
	 * 
	 * @return pensionados
	 */
	public boolean getPensionados() {
		return pensionados;
	}

	/**
	 * Asigna la variable pensionados
	 * 
	 * @param pensionados Variable a asignar en pensionados
	 */
	public void setPensionados(boolean pensionados) {
		this.pensionados = pensionados;
	}

	/**
	 * Retorna la variable viaticos
	 * 
	 * @return viaticos
	 */
	public boolean getViaticos() {
		return viaticos;
	}

	/**
	 * Asigna la variable viaticos
	 * 
	 * @param viaticos Variable a asignar en viaticos
	 */
	public void setViaticos(boolean viaticos) {
		this.viaticos = viaticos;
	}

	/**
	 * Retorna la variable primas
	 * 
	 * @return primas
	 */
	public boolean getPrimas() {
		return primas;
	}

	/**
	 * Asigna la variable primas
	 * 
	 * @param primas Variable a asignar en primas
	 */
	public void setPrimas(boolean primas) {
		this.primas = primas;
	}

	/**
	 * Retorna la variable informePrenomina
	 * 
	 * @return informePrenomina
	 */
	public boolean getInformePrenomina() {
		return informePrenomina;
	}

	/**
	 * Asigna la variable informePrenomina
	 * 
	 * @param informePrenomina Variable a asignar en informePrenomina
	 */
	public void setInformePrenomina(boolean informePrenomina) {
		this.informePrenomina = informePrenomina;
	}

	/**
	 * Retorna la variable totales
	 * 
	 * @return totales
	 */
	public boolean isTotales() {
		return totales;
	}

	/**
	 * Asigna la variable totales
	 * 
	 * @param totales Variable a asignar en totales
	 */
	public void setTotales(boolean totales) {
		this.totales = totales;
	}

	public boolean isMaestra() {
		return maestra;
	}

	public void setMaestra(boolean maestra) {
		this.maestra = maestra;
	}

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable periodo
	 * 
	 * @return periodo
	 */
	public String getPeriodo() {
		return periodo;
	}

	/**
	 * Asigna la variable periodo
	 * 
	 * @param periodo Variable a asignar en periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna la variable proceso
	 * 
	 * @return proceso
	 */
	public String getProceso() {
		return proceso;
	}

	/**
	 * Asigna la variable proceso
	 * 
	 * @param proceso Variable a asignar en proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	/**
	 * Retorna la variable mesViaticos
	 * 
	 * @return mesViaticos
	 */
	public String getMesViaticos() {
		return mesViaticos;
	}

	/**
	 * Asigna la variable mesViaticos
	 * 
	 * @param mesViaticos Variable a asignar en mesViaticos
	 */
	public void setMesViaticos(String mesViaticos) {
		this.mesViaticos = mesViaticos;
	}

	/**
	 * Retorna la variable observacion
	 * 
	 * @return observacion
	 */
	public String getObservacion() {
		return observacion;
	}

	/**
	 * Asigna la variable observacion
	 * 
	 * @param observacion Variable a asignar en observacion
	 */
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	/**
	 * Retorna la variable conceptoPago
	 * 
	 * @return conceptoPago
	 */
	public String getConceptoPago() {
		return conceptoPago;
	}

	/**
	 * Asigna la variable conceptoPago
	 * 
	 * @param conceptoPago Variable a asignar en conceptoPago
	 */
	public void setConceptoPago(String conceptoPago) {
		this.conceptoPago = conceptoPago;
	}

	/**
	 * Retorna la variable visibleMesViatico
	 * 
	 * @return visibleMesViatico
	 */
	public boolean isVisibleMesViatico() {
		return visibleMesViatico;
	}

	/**
	 * Asigna la variable visibleMesViatico
	 * 
	 * @param visibleMesViatico Variable a asignar en visibleMesViatico
	 */
	public void setVisibleMesViatico(boolean visibleMesViatico) {
		this.visibleMesViatico = visibleMesViatico;
	}

	/**
	 * Retorna la variable visiblePlanillaDevengos
	 * 
	 * @return visiblePlanillaDevengos
	 */
	public boolean isVisiblePlanillaDevengos() {
		return visiblePlanillaDevengos;
	}

	/**
	 * Asigna la variable visiblePlanillaDevengos
	 * 
	 * @param visiblePlanillaDevengos Variable a asignar en visiblePlanillaDevengos
	 */
	public void setVisiblePlanillaDevengos(boolean visiblePlanillaDevengos) {
		this.visiblePlanillaDevengos = visiblePlanillaDevengos;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}

	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}

	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes Variable a asignar en listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}

	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo Variable a asignar en listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}

	/**
	 * Retorna la lista listaProceso
	 * 
	 * @return listaProceso
	 */
	public List<Registro> getListaProceso() {
		return listaProceso;
	}

	/**
	 * Asigna la lista listaProceso
	 * 
	 * @param listaProceso Variable a asignar en listaProceso
	 */
	public void setListaProceso(List<Registro> listaProceso) {
		this.listaProceso = listaProceso;
	}

	public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

	public List<Registro> getListaMesViaticos() {
		return listaMesViaticos;
	}

	public void setListaMesViaticos(List<Registro> listaMesViaticos) {
		this.listaMesViaticos = listaMesViaticos;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

}
