/*-
 * FrmSubirDepreciacionAcumuladaControlador.java
 *
 * 1.0
 * 
 * 20/05/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
import com.sysman.util.enums.ConstanteArchivo;
/**
 *
 * @version 1.0, 20/05/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmSubirDepreciacionAcumuladaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	//<DECLARAR_ATRIBUTOS>
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

	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;
	private int contador;

	private boolean procesoCorrecto = false;
	private String mensajeResultado = "";
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbAlmacenCincoRemote almacenCincoRemote;
	/**
	 * Crea una nueva instancia de FrmSubirDepreciacionAcumuladaControlador
	 */
	public FrmSubirDepreciacionAcumuladaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		contArchivosubirPlantilla = new ContenedorArchivo();
		try {
			//2587
			numFormulario = GeneralCodigoFormaEnum.FRM_SUBIR_DEPRECIACION_ACUMULADA_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
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
		try (FileInputStream file = new FileInputStream(contArchivosubirPlantilla.getArchivo());) {


			String rutaArchivo = contArchivosubirPlantilla.getArchivo().getPath();

			String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
					rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

			if ("xls".equals(extension)) {
				workbook = new HSSFWorkbook(file);
			} else {
				workbook = new XSSFWorkbook(file);
			}
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

			cargarDatos();

		} catch (IOException | NumberFormatException| NullPointerException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}   
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * Retorna true si TODAS las celdas de la fila (columnas 0 a 4) están vacías.
	 * Una fila con al menos un dato en cualquier columna NO se considera vacía.
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
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton DescargarPlantilla
	 * en la vista
	 *
	 *
	 */
	public void oprimirDescargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;

		HSSFWorkbook workbook = new HSSFWorkbook();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			/* ==========================
			 * HOJA 1: ESTRUCTURA
			 * ========================== */
			HSSFSheet sheetEstructura = workbook.createSheet("Estructura");

			// Fuente para encabezados
			Font headerFont = workbook.createFont();
			headerFont.setFontName("Arial");
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);

			// Estilo de encabezado (gris + negrita + bordes)
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// Fuente normal
			Font normalFont = workbook.createFont();
			normalFont.setFontName("Arial");
			normalFont.setFontHeightInPoints((short) 9);

			// Estilo con bordes
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setFont(normalFont);
			borderStyle.setVerticalAlignment(VerticalAlignment.TOP);
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);

			// Estilo con bordes + ajuste de texto
			CellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setFont(normalFont);
			wrapStyle.setWrapText(true);
			wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
			wrapStyle.setBorderBottom(BorderStyle.THIN);
			wrapStyle.setBorderTop(BorderStyle.THIN);
			wrapStyle.setBorderLeft(BorderStyle.THIN);
			wrapStyle.setBorderRight(BorderStyle.THIN);

			// Estilo de título
			Font tituloFont = workbook.createFont();
			tituloFont.setFontName("Arial");
			tituloFont.setBold(true);
			tituloFont.setFontHeightInPoints((short) 9);

			CellStyle tituloStyle = workbook.createCellStyle();
			tituloStyle.setFont(tituloFont);

			// Encabezados de la hoja Estructura
			String[] headers = {
					"CODIGO_ELEMENTO",
					"PLACA",
					"PERIODO_AFECTAR",
					"DEPRECIACION_ACUMULADA",
					"OBSERVACIONES"
			};

			Row headerRow = sheetEstructura.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
				sheetEstructura.autoSizeColumn(i);
			}

			/* ==========================
			 * HOJA 2: INSTRUCTIVO
			 * ========================== */
			HSSFSheet sheetInfo = workbook.createSheet("Instructivo");

			// Título principal
			Row titulo = sheetInfo.createRow(0);
			Cell tituloCell = titulo.createCell(0);
			tituloCell.setCellValue("CARGUE DEPRECIACION ACUMULADA NO CALCULADA");
			tituloCell.setCellStyle(tituloStyle);

			// Combinar celdas del título
			sheetInfo.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

			// Encabezados del instructivo
			String[] campos = {"CAMPO", "FORMATO", "TIPO", "OBSERVACIONES"};

			Row encabezado = sheetInfo.createRow(2);
			for (int i = 0; i < campos.length; i++) {
				Cell cell = encabezado.createCell(i);
				cell.setCellValue(campos[i]);
				cell.setCellStyle(headerStyle);
			}

			// Datos informativos
			Object[][] datos = {
					{
						"CODIGO_ELEMENTO",
						"VARCHAR2(16 CHAR)",
						"Obligatorio",
						"Codigo del Elemento definido en la ruta ALMACEN/ARCHIVOS/INVENTARIO\n"
								+ "Este codigo debe ser a 9 digitos y marcado con la opción Tiene Movimiento?"
					},
					{
						"PLACA",
						"NUMBER(15,0)",
						"Obligatorio",
						"Numero de placa o serie del bien que ya se encuentra creado en el aplicativo que no ha tenido un proceso de calculo depreciaciones o presenta valores en cero."
					},
					{
						"PERIODO_AFECTAR",
						"DATE(dd/mm/aaaa)",
						"Obligatorio",
						"Determina el periodo en el que se registrará la depreciación. Debe permitir el formato día/mes/año."
					},
					{
						"DEPRECIACION_ACUMULADA",
						"NUMBER(20,2)",
						"Obligatorio",
						"Valor de la depreciación acumulada del activo, debe ser mayor a cero "
								+ "y menor o igual al valor de adquisición del bien."
					},
					{
						"OBSERVACIONES",
						"VARCHAR2(255 CHAR)",
						"Obligatorio",
						"Se recomienda incluir en la observación el motivo por el cual "
								+ "se realiza la actualización de los valores del bien."
					}
			};

			// Ancho fijo de columnas
			sheetInfo.setColumnWidth(0, 20 * 256); // CAMPO
			sheetInfo.setColumnWidth(1, 20 * 256); // FORMATO
			sheetInfo.setColumnWidth(2, 14 * 256); // TIPO
			sheetInfo.setColumnWidth(3, 35 * 256); // OBSERVACIONES

			// Insertar datos informativos
			int rowIdx = 3;
			for (Object[] fila : datos) {
				Row row = sheetInfo.createRow(rowIdx++);

				for (int i = 0; i < fila.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(fila[i].toString());

					if (i == 3) {
						cell.setCellStyle(wrapStyle);
					} else {
						sheetInfo.autoSizeColumn(i);
						cell.setCellStyle(borderStyle);
					}
				}

				row.setHeight((short) -1);
			}

			/* ==========================
			 * SECCIÓN EJEMPLO
			 * ========================== */
			Row ejemploTitulo = sheetInfo.createRow(rowIdx + 1);
			Cell ejTitle = ejemploTitulo.createCell(0);
			ejTitle.setCellValue("EJEMPLO");
			ejTitle.setCellStyle(tituloStyle);

			// Encabezado del ejemplo
			Row encabezadoEjem = sheetInfo.createRow(rowIdx + 2);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = encabezadoEjem.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// Datos del ejemplo
			Row ejemplo = sheetInfo.createRow(rowIdx + 3);
			Object[] ejemploDatos = {
					"20140006",
					"3140",
					"01/01/2026",
					"12000000",
					"POR IMPLEMENTACIÓN"
			};

			for (int i = 0; i < ejemploDatos.length; i++) {
				Cell cell = ejemplo.createCell(i);
				cell.setCellValue(ejemploDatos[i].toString());

				if (i == 3) {
					cell.setCellStyle(wrapStyle);
				} else {
					cell.setCellStyle(borderStyle);
					sheetInfo.autoSizeColumn(i);
				}
			}

			ejemplo.setHeight((short) -1);

			/* ==========================
			 * GENERAR ARCHIVO
			 * ========================== */
			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(out.toByteArray()),
					"Cargue Depreciacion Acumulada No Calculada"
							+ ConstanteArchivo.EXCEL97.getExtension()
					);

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO")
					);

		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
		//</CODIGO_DESARROLLADO>
	}

	private void capturaDatosExcel(Row row) throws ParseException {
		if (row.getRowNum() > 0) {
			for (int i = 0; i <= 4; i++) {

				Cell celda = row.getCell(i);
				String val;

				if (celda == null) {
					val = "NoData";
				} else if (i == 2) {
					// Columna PERIODO_AFECTAR
					if (celda.getCellType() == Cell.CELL_TYPE_BLANK) {
						val = "NoData";
					} else if (celda.getCellType() == Cell.CELL_TYPE_STRING) {
						val = celda.getStringCellValue().trim().isEmpty() 
								? "NoData" 
										: celda.getStringCellValue();
					} else {
						val = SysmanFunciones.convertirAFechaCadena(
								celda.getDateCellValue());
					}
				} else {
					// Columnas numéricas / texto
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

			datos = almacenCincoRemote.cargarDepreciacionNoCalculada(compania, cadena, SessionUtil.getUser().getCodigo());
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "resultadoCargueDepreciacionAcumulada.txt");

			mensajeResultado = datos;
			procesoCorrecto = true;

		} catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			procesoCorrecto = false;
		}

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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @return the procesoCorrecto
	 */
	public boolean isProcesoCorrecto() {
		return procesoCorrecto;
	}
	/**
	 * @param procesoCorrecto the procesoCorrecto to set
	 */
	public void setProcesoCorrecto(boolean procesoCorrecto) {
		this.procesoCorrecto = procesoCorrecto;
	}
}
