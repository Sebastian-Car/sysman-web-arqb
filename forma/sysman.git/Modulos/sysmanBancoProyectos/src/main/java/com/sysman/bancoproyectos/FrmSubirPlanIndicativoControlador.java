/*-
 * FrmSubirPlanIndicativoControlador.java
 *
 * 1.0
 * 
 * 22/05/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.BpplanindicativosControladorEnum;
import com.sysman.bancoproyectos.enums.FrmSubirPlanIndicativoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
/**
 *
 * @version 1.0, 22/05/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmSubirPlanIndicativoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	private String vigencia;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos subirPlantilla y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivosubirPlantilla;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaVigencia;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;
	private int contador;

	private boolean procesoCorrecto = false;
	private String mensajeResultado = "";
	
	@EJB
	private EjbBancoProyectoCincoRemote proyectoCincoRemote;

	/**
	 * Crea una nueva instancia de FrmSubirPlanIndicativoControlador
	 */
	public FrmSubirPlanIndicativoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		contArchivosubirPlantilla = new ContenedorArchivo();
		try {
			//2588
			numFormulario = GeneralCodigoFormaEnum.FRM_SUBIR_PLAN_INDICATIVO_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {
				vigencia = (String) parametros
						.get(BpplanindicativosControladorEnum.VIGENCIA_LOWER
								.getValue());
			}
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarListaVigencia();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaVigencia
	 *
	 */
	public void cargarListaVigencia(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaVigencia = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmSubirPlanIndicativoControladorUrlEnum.URL4001.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cerrar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCerrar() {
		//<CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(mensajeResultado);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CargarPlantilla
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;
		if (!validarArchivo()) {
			return;
		}
		try (FileInputStream file = new FileInputStream(contArchivosubirPlantilla.getArchivo());
	         BufferedInputStream bis = new BufferedInputStream(file)) {
			 byte[] header = new byte[8];
			    bis.mark(8);
			    bis.read(header);
			    bis.reset();

			    // OLE2 (.xls real) siempre empieza con estos bytes: D0 CF 11 E0 A1 B1 1A E1
			    boolean esXlsReal = (header[0] == (byte) 0xD0 &&
			                         header[1] == (byte) 0xCF &&
			                         header[2] == (byte) 0x11 &&
			                         header[3] == (byte) 0xE0);

			    if (!esXlsReal) {
			        FacesContext.getCurrentInstance().addMessage(null,
			            new FacesMessage(FacesMessage.SEVERITY_ERROR,
			                "Error de archivo",
			                "El archivo no es un .xls válido. " +
			                "Verifique que el archivo no tenga la extensión cambiada manualmente."));
			        return;
			    }

			    workbook = new HSSFWorkbook(bis);
			    Sheet sheet = workbook.getSheet("Estructura");

			for (Row row : sheet) {
				if (row.getRowNum() == 0) {
					continue;
				}

				if (filaCompletamenteVacia(row)) {
					break;
				}

				capturaDatosExcel(row);
			}
			cadena = cadena + "')";
			System.out.println(cadena);
			cargarDatos();

		} catch (IOException | NumberFormatException| NullPointerException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}   

		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {

		if (contArchivosubirPlantilla.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}


	/**
	 * Retorna true si TODAS las celdas de la fila (columnas 0 a 4) estn vacas.
	 * Una fila con al menos un dato en cualquier columna NO se considera vaca.
	 */
	private boolean filaCompletamenteVacia(Row row) {
		for (int i = 0; i <= 4; i++) {
			Cell celda = row.getCell(i);
			if (celda == null) {
				continue;
			}
			switch (celda.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				if (!celda.getStringCellValue().trim().isEmpty()) {
					return false; // tiene dato
				}
				break;
			case Cell.CELL_TYPE_NUMERIC:
				return false; // tiene dato
			case Cell.CELL_TYPE_BOOLEAN:
				return false; // tiene dato
			default:
				break;
			}
		}
		return true;
	}

	private void capturaDatosExcel(Row row) throws ParseException {
		if (row.getRowNum() > 0) {
			for (int i = 0; i <= 13; i++) {

				Cell celda = row.getCell(i);
				String val;

				if (celda == null) {
					val = "NoData";
				} else {
					// Columnas numricas / texto
					if (celda.getCellType() == Cell.CELL_TYPE_BLANK) {
						val = "NoData";
					} else if (celda.getCellType() == Cell.CELL_TYPE_STRING) {
						val = celda.getStringCellValue().trim().isEmpty() 
								? "NoData" 
										: celda.getStringCellValue();
					} else {
						val = String.valueOf((long) celda.getNumericCellValue());
					}
				}

				val = val.replace("'", "|");

				contador = contador + val.length();

				if (contador >= 3000) {
					cadena = cadena + "') || TO_CLOB('";
					contador = 0;
				}

				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadena = cadena.substring(0, cadena.length() 
					- SysmanConstantes.SEPARADOR_COL.length());
			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}
	}

	private void cargarDatos() {
		String datos = null;
		try {

			datos = proyectoCincoRemote.cargarPlanIndicativo(compania, cadena, SessionUtil.getUser().getCodigo());
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "resultadoCarguePlanIndicativo.txt");

			mensajeResultado = datos;
			procesoCorrecto = true;

		} catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			procesoCorrecto = false;
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton DescargarPlantilla
	 * en la vista
	 *
	 *
	 */
	public void oprimirDescargarPlantilla() {
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			HSSFFont font = workbook.createFont();
			font.setFontName("Arial");
			font.setBold(true);
			font.setFontHeightInPoints((short) 10);

			HSSFCellStyle styleHeader = workbook.createCellStyle();
			styleHeader.setFont(font);
			styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND); 

			HSSFFont fontTitle = workbook.createFont();
			fontTitle.setFontName("Arial");
			fontTitle.setBold(true);
			fontTitle.setFontHeightInPoints((short) 12);

			HSSFCellStyle styleTitle = workbook.createCellStyle();
			styleTitle.setFont(fontTitle);

			HSSFCellStyle styleInstHeader = workbook.createCellStyle();
			styleInstHeader.setFont(font);
			styleInstHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			styleInstHeader.setFillPattern(CellStyle.SOLID_FOREGROUND); 

			/* 
			 * HOJA: Estructura
			 *  
			 */
			HSSFSheet sheetEstructura = workbook.createSheet("Estructura");

			String[] columnas = {
					"CODIGO_PLAN", "VIGENCIA_GUBERNAMENTAL", "VIGENCIA_FINAL",
					"DESCRIPCION", "TIPO_INDICADOR", "UNIDAD_MEDIDA",
					"INDICADOR_MEDIDA", "NOMBRE_INDICADOR", "META_CUATRIENIO",
					"LINEA_BASE", "CODIGO_DEPENDENCIA", "DESCRIPCION_META",
					"TEXTO_META", "CODIGO_SECTOR"
			};

			HSSFRow rowHeader = sheetEstructura.createRow(0);
			for (int i = 0; i < columnas.length; i++) {
				HSSFCell cell = rowHeader.createCell(i);
				cell.setCellValue(columnas[i]);
				cell.setCellStyle(styleHeader);
				sheetEstructura.autoSizeColumn(i);
			}

			/*
			 * HOJA: Instructivo
			 **/
			HSSFSheet sheetInstructivo = workbook.createSheet("Instructivo");

			HSSFRow rowTitle = sheetInstructivo.createRow(0);
			HSSFCell cellTitle = rowTitle.createCell(0);
			cellTitle.setCellValue("CARGAR PLAN INDICATIVO");
			cellTitle.setCellStyle(styleTitle);

			HSSFRow rowInstHeader = sheetInstructivo.createRow(2);
			String[] instCols = {"CAMPO", "FORMATO", "TIPO", "OBSERVACIONES"};
			for (int i = 0; i < instCols.length; i++) {
				HSSFCell c = rowInstHeader.createCell(i);
				c.setCellValue(instCols[i]);
				c.setCellStyle(styleInstHeader);
			}

			String[][] instructivoData = {
					{"CODIGO_PLAN",
						"VARCHAR2(32 CHAR)",
						"Obligatorio",
					"Código del Plan Indicativo de acuerdo con los dígitos configurados por nivel en la pestaña NIVELES. Información definida en la opción del módulo \"Niveles Plan Indicativo\""},
					{"VIGENCIA_GUBERNAMENTAL",
						"NUMBER(4,0)",
						"Obligatorio",
					"Año de inicio del plan indicativo en la entidad"},
					{"VIGENCIA_FINAL",
						"NUMBER(4,0)",
						"Obligatorio",
					"Año final del plan indicativo en la entidad"},
					{"DESCRIPCION",
						"VARCHAR2(300 CHAR)",
						"Obligatorio",
					"Descripción del plan indicativo para el nivel definido"},
					{"TIPO_INDICADOR",
						"VARCHAR2(3 CHAR)",
						"Opcional",
					"En las Meta Resultado o Meta Producto del plan indicativo es obligatorio y se debe definir una de las siguientes opciones: MI = Meta Incremento, MM = Meta Mantenimiento, MR = Meta Reducción, MG = Meta gestión"},
					{"UNIDAD_MEDIDA",
						"VARCHAR2(12 CHAR)",
						"Opcional",
					"En las Meta Resultado o Meta Producto del plan indicativo es obligatorio definir uno de los códigos de unidades configurados en la pestaña UNIDADES. Información definida en la opción del módulo \"Unidades de Producto\""},
					{"INDICADOR_MEDIDA",
						"VARCHAR2(255 CHAR)",
						"Opcional",
					"Código estructurado que vincula la meta con su posición en el plan"},
					{"NOMBRE_INDICADOR",
						"VARCHAR2(255 CHAR)",
						"Opcional",
					"Descripción textual completa de lo que se mide"},
					{"META_CUATRIENIO",
						"NUMBER(20,2)",
						"Opcional",
					"En las Meta Resultado o Meta Producto del plan indicativo es obligatorio definir el valor a medir según la unidad de medida"},
					{"LINEA_BASE",
						"NUMBER(20,2)",
						"Opcional",
					"En las Meta Resultado o Meta Producto del plan indicativo es obligatorio definir el valor de línea base según la unidad de medida"},
					{"CODIGO_DEPENDENCIA",
						"VARCHAR2(12 CHAR)",
						"Obligatorio",
					"En las Meta Producto del plan indicativo es obligatorio definir la dependencia encargada, configurados en la pestaña DEPENDENCIAS que están marcados como \"Ver En Banco\". Información definida en la opción del módulo \"Dependencias Responsables\""},
					{"DESCRIPCION_META",
						"VARCHAR2(255 CHAR)",
						"Opcional",
					"Descripción del objeto físico o resultado esperado de manera concreta de la Meta Resultado o Meta Producto del plan indicativo"},
					{"TEXTO_META",
						"VARCHAR2(4000 CHAR)",
						"Opcional",
					"Descripción que contextualiza la meta dentro de un marco global de gestión"},
					{"CODIGO_SECTOR",
						"VARCHAR2(4 CHAR)",
						"Obligatorio",
					"Código del sector configurado en la pestaña SECTOR. Información definida en la opción del módulo \"Sectores\""}
			};

			for (int i = 0; i < instructivoData.length; i++) {
				HSSFRow rowInst = sheetInstructivo.createRow(3 + i);
				for (int j = 0; j < instructivoData[i].length; j++) {
					rowInst.createCell(j).setCellValue(instructivoData[i][j]);
				}
			}

			HSSFRow rowEjemploLabel = sheetInstructivo.createRow(18);
			HSSFCell cellEjemploLabel = rowEjemploLabel.createCell(0);
			cellEjemploLabel.setCellValue("EJEMPLO DE REGISTRO ESTRUCTURA");
			cellEjemploLabel.setCellStyle(styleHeader);

			HSSFRow rowEjemploHeader = sheetInstructivo.createRow(19);
			for (int i = 0; i < columnas.length; i++) {
				HSSFCell c = rowEjemploHeader.createCell(i);
				c.setCellValue(columnas[i]);
				c.setCellStyle(styleInstHeader);
			}

			String[][] ejemplos = {
					{"01",        "2024", "2027", "GOBERNANZA, EDUCACIÓN Y ORDENAMIENTO AMBIENTAL", "",   "",  "", "GOBERNANZA, EDUCACIÓN Y ORDENAMIENTO AMBIENTAL", "",    "",    "",      "GOBERNANZA, EDUCACIÓN Y ORDENAMIENTO AMBIENTAL", "GOBERNANZA, EDUCACIÓN Y ORDENAMIENTO AMBIENTAL", "32"},
					{"0101",      "2024", "2027", "3203 - Gestión integral del recurso hídrico",    "",   "",  "", "3203 - Gestión integral del recurso hídrico",    "",    "",    "",      "3203 - Gestión integral del recurso hídrico",    "3203 - Gestión integral del recurso hídrico",    "32"},
					{"010101",    "2024", "2027", "Planificación y administración para la gestión del recurso hídrico", "", "", "", "Planificación y administración para la gestión del recurso hídrico", "", "", "", "Planificación y administración para la gestión del recurso hídrico", "Planificación y administración para la gestión del recurso hídrico", "32"},
					{"01010101",  "2024", "2027", "Monitoreo Recurso Hídrico",                      "MM", "%", "Porcentaje Cumplimiento de IPS del Municipio", "PORCENTAJE DE IPS DEL MUNICIPIO CON ACCIONES PARA ASEGURAR LA CALIDAD", "100", "0", "17/18", "PORCENTAJE DE IPS DEL MUNICIPIO CON ACCIONES", "PORCENTAJE DE IPS DEL MUNICIPIO CON ACCIONES PARA ASEGURAR LA CALIDAD PARA 5", "10"}
			};

			for (int i = 0; i < ejemplos.length; i++) {
				HSSFRow rowEj = sheetInstructivo.createRow(20 + i);
				for (int j = 0; j < ejemplos[i].length; j++) {
					rowEj.createCell(j).setCellValue(ejemplos[i][j]);
				}
			}

			for (int i = 0; i < columnas.length; i++) {
				sheetInstructivo.autoSizeColumn(i);
			}

			/*
			 * HOJAS DE REFERENCIA CON DATOS DINÁMICOS
			 */
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("COMPANIA", compania);
			param.put("VIGENCIA", vigencia);

			/* ---------- NIVELES ---------- */
			List<Registro> listaNiveles = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmSubirPlanIndicativoControladorUrlEnum.URL554026.getValue())
							.getUrl(),
							param));

			HSSFSheet sheetNiveles = workbook.createSheet("Niveles");
			HSSFRow rowNivHeader = sheetNiveles.createRow(0);
			String[] colsNiveles = {"Vigencia", "Digitos", "Descripción", "Meta Resultado", "Meta Producto", "Maneja Dependencia", "Acción"};
			for (int i = 0; i < colsNiveles.length; i++) {
				HSSFCell c = rowNivHeader.createCell(i);
				c.setCellValue(colsNiveles[i]);
				c.setCellStyle(styleHeader);
				sheetNiveles.autoSizeColumn(i);
			}
			int rowNumNiv = 1;
			for (Registro reg : listaNiveles) {
				HSSFRow row = sheetNiveles.createRow(rowNumNiv++);
				row.createCell(0).setCellValue(SysmanFunciones.toString(reg.getCampos().get("VIGENCIA")));
				row.createCell(1).setCellValue(SysmanFunciones.toString(reg.getCampos().get("DIGITOS")));
				row.createCell(2).setCellValue(SysmanFunciones.toString(reg.getCampos().get("DESCRIPCION")));
				row.createCell(3).setCellValue(SysmanFunciones.toString(reg.getCampos().get("META_RESUL")));
				row.createCell(4).setCellValue(SysmanFunciones.toString(reg.getCampos().get("META_PRODUC")));
				row.createCell(5).setCellValue(SysmanFunciones.toString(reg.getCampos().get("MANEJA_DEPEN")));
				row.createCell(6).setCellValue(SysmanFunciones.toString(reg.getCampos().get("ACCION")));
				for (int i = 0; i < colsNiveles.length; i++) {
					sheetNiveles.autoSizeColumn(i);
				}
			}

			/* ---------- UNIDADES ---------- */
			List<Registro> listaUnidades = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmSubirPlanIndicativoControladorUrlEnum.URL553006.getValue())
							.getUrl(),
							param));

			HSSFSheet sheetUnidades = workbook.createSheet("Unidades");
			HSSFRow rowUniHeader = sheetUnidades.createRow(0);
			String[] colsUnidades = {"Código Unidad", "Nombre de Unidad", "Descripción"};
			for (int i = 0; i < colsUnidades.length; i++) {
				HSSFCell c = rowUniHeader.createCell(i);
				c.setCellValue(colsUnidades[i]);
				c.setCellStyle(styleHeader);
				sheetUnidades.autoSizeColumn(i);
			}
			int rowNumUni = 1;
			for (Registro reg : listaUnidades) {
				HSSFRow row = sheetUnidades.createRow(rowNumUni++);
				row.createCell(0).setCellValue(SysmanFunciones.toString(reg.getCampos().get("UNIDAD")));
				row.createCell(1).setCellValue(SysmanFunciones.toString(reg.getCampos().get("NOMBRE")));
				row.createCell(2).setCellValue(SysmanFunciones.toString(reg.getCampos().get("DESCRIPCION")));
				for (int i = 0; i < colsUnidades.length; i++) {
					sheetUnidades.autoSizeColumn(i);
				}
			}

			/* ---------- SECTORES ---------- */
			List<Registro> listaSectores = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmSubirPlanIndicativoControladorUrlEnum.URL203009.getValue())
							.getUrl(),
							param));

			HSSFSheet sheetSectores = workbook.createSheet("Sectores");
			HSSFRow rowSecHeader = sheetSectores.createRow(0);
			String[] colsSectores = {"Código", "Descripción", "CÓDIGO FUT"};
			for (int i = 0; i < colsSectores.length; i++) {
				HSSFCell c = rowSecHeader.createCell(i);
				c.setCellValue(colsSectores[i]);
				c.setCellStyle(styleHeader);
				sheetSectores.autoSizeColumn(i);
			}
			int rowNumSec = 1;
			for (Registro reg : listaSectores) {
				HSSFRow row = sheetSectores.createRow(rowNumSec++);
				row.createCell(0).setCellValue(SysmanFunciones.toString(reg.getCampos().get("CODIGO")));
				row.createCell(1).setCellValue(SysmanFunciones.toString(reg.getCampos().get("DESCRIPCION")));
				row.createCell(2).setCellValue(SysmanFunciones.toString(reg.getCampos().get("SECTOR_PI")));
				for (int i = 0; i < colsSectores.length; i++) {
					sheetSectores.autoSizeColumn(i);
				}
			}

			/* ---------- DEPENDENCIAS ---------- */
			List<Registro> listaDependencias = RegistroConverter.toListRegistro(
					requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(FrmSubirPlanIndicativoControladorUrlEnum.URL62112.getValue())
							.getUrl(),
							param));

			HSSFSheet sheetDependencias = workbook.createSheet("Dependencias");
			HSSFRow rowDepHeader = sheetDependencias.createRow(0);
			String[] colsDependencias = {"Código", "Nombre", "¿Tiene Movimiento?", "Sigla", "Centro de Costo", "Activo", "Ver en Banco"};
			for (int i = 0; i < colsDependencias.length; i++) {
				HSSFCell c = rowDepHeader.createCell(i);
				c.setCellValue(colsDependencias[i]);
				c.setCellStyle(styleHeader);
				sheetDependencias.autoSizeColumn(i);
			}
			int rowNumDep = 1;
			for (Registro reg : listaDependencias) {
				HSSFRow row = sheetDependencias.createRow(rowNumDep++);
				row.createCell(0).setCellValue(SysmanFunciones.toString(reg.getCampos().get("CODIGO")));
				row.createCell(1).setCellValue(SysmanFunciones.toString(reg.getCampos().get("NOMBRE")));
				row.createCell(2).setCellValue(SysmanFunciones.toString(reg.getCampos().get("MOVIMIENTO")));
				row.createCell(3).setCellValue(SysmanFunciones.toString(reg.getCampos().get("SIGLA")));
				row.createCell(4).setCellValue(SysmanFunciones.toString(reg.getCampos().get("CENTRODECOSTO")));
				row.createCell(5).setCellValue(SysmanFunciones.toString(reg.getCampos().get("ACTIVO")));
				row.createCell(6).setCellValue(SysmanFunciones.toString(reg.getCampos().get("VERBANCO")));
				for (int i = 0; i < colsDependencias.length; i++) {
					sheetDependencias.autoSizeColumn(i);
				}
			}

			/*
			 * BLOQUEO DE HOJAS DE REFERENCIA
			 * Las hojas Niveles, Unidades, Sectores, Dependencias y Tipo Indicador
			 * se protegen para que el usuario no pueda modificar los datos de referencia
			 */
			String passwordProteccion = "sysman2026";
			sheetNiveles.protectSheet(passwordProteccion);
			sheetUnidades.protectSheet(passwordProteccion);
			sheetSectores.protectSheet(passwordProteccion);
			sheetDependencias.protectSheet(passwordProteccion);

			HSSFSheet sheetTipoInd = workbook.createSheet("Tipo_Indicador");

			HSSFRow rowTipoHeader = sheetTipoInd.createRow(0);
			HSSFCell cTipoCod = rowTipoHeader.createCell(0);
			cTipoCod.setCellValue("Código");
			cTipoCod.setCellStyle(styleHeader);
			HSSFCell cTipoNom = rowTipoHeader.createCell(1);
			cTipoNom.setCellValue("Nombre");
			cTipoNom.setCellStyle(styleHeader);
			sheetTipoInd.autoSizeColumn(0);
			sheetTipoInd.autoSizeColumn(1);

			String[][] tiposIndicador = {
					{"MI", "Meta Incremento"},
					{"MM", "Meta Mantenimiento"},
					{"MR", "Meta Reducción"},
					{"MG", "Meta Gestión"}
			};
			for (int i = 0; i < tiposIndicador.length; i++) {
				HSSFRow rowTipo = sheetTipoInd.createRow(i + 1);
				rowTipo.createCell(0).setCellValue(tiposIndicador[i][0]);
				rowTipo.createCell(1).setCellValue(tiposIndicador[i][1]);
				sheetTipoInd.autoSizeColumn(i);
			}
			sheetTipoInd.protectSheet(passwordProteccion);

			createName(workbook, "lista_tipo_ind", "Tipo_Indicador!$A$2:$A$5");
			DVConstraint constraintTipo = DVConstraint.createFormulaListConstraint("lista_tipo_ind");
			CellRangeAddressList regionsTipo = new CellRangeAddressList(1, 10000, 4, 4);
			sheetEstructura.addValidationData(new HSSFDataValidation(regionsTipo, constraintTipo));

			int totalUnidades = listaUnidades.size();
			createName(workbook, "lista_unidades", "Unidades!$A$2:$A$" + (totalUnidades + 1));
			DVConstraint constraintUnidad = DVConstraint.createFormulaListConstraint("lista_unidades");
			CellRangeAddressList regionsUnidad = new CellRangeAddressList(1, 10000, 5, 5);
			sheetEstructura.addValidationData(new HSSFDataValidation(regionsUnidad, constraintUnidad));

			int totalSectores = listaSectores.size();
			createName(workbook, "lista_sectores", "Sectores!$A$2:$A$" + (totalSectores + 1));
			DVConstraint constraintSector = DVConstraint.createFormulaListConstraint("lista_sectores");
			CellRangeAddressList regionsSector = new CellRangeAddressList(1, 10000, 13, 13);
			sheetEstructura.addValidationData(new HSSFDataValidation(regionsSector, constraintSector));

			int totalDependencias = listaDependencias.size();
			createName(workbook, "lista_dependencias", "Dependencias!$A$2:$A$" + (totalDependencias + 1));
			DVConstraint constraintDep = DVConstraint.createFormulaListConstraint("lista_dependencias");
			CellRangeAddressList regionsDep = new CellRangeAddressList(1, 10000, 10, 10);
			sheetEstructura.addValidationData(new HSSFDataValidation(regionsDep, constraintDep));

			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(out.toByteArray()),
					"Plantilla_Cargue_Plan_Indicativo.xls"
					);
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (IOException | JRException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}

	private static Name createName(Workbook workbook, String nameName, String formula) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		name.setRefersToFormula(formula);
		return name;
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable vigencia
	 * 
	 * @return  vigencia
	 */
	public String getVigencia() {
		return vigencia;
	}
	/**
	 * Asigna la variable  vigencia
	 * 
	 * @param  vigencia
	 * Variable a asignar en  vigencia
	 */
	public void setVigencia(String vigencia) {
		this.vigencia = vigencia;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * Retorna el objeto contArchivosubirPlantilla
	 * 
	 * @return contArchivosubirPlantilla
	 */
	public ContenedorArchivo getContArchivosubirPlantilla() {
		return contArchivosubirPlantilla;
	}
	/**
	 * Asigna el objeto contArchivosubirPlantilla
	 * 
	 * @param contArchivosubirPlantilla
	 * Variable a asignar en contArchivosubirPlantilla
	 */
	public void setContArchivosubirPlantilla(ContenedorArchivo contArchivosubirPlantilla) {
		this.contArchivosubirPlantilla = contArchivosubirPlantilla;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaVigencia
	 * 
	 * @return listaVigencia
	 */
	public List<Registro> getListaVigencia() {
		return listaVigencia;
	}
	/**
	 * Asigna la lista listaVigencia
	 * 
	 * @param listaVigencia
	 * Variable a asignar en  listaVigencia
	 */
	public void setListaVigencia(List<Registro> listaVigencia) {
		this.listaVigencia = listaVigencia;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
