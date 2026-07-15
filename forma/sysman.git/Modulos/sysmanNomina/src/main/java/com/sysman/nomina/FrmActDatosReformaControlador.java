/*-
 * FrmActDatosReforma.java
 *
 * 1.0
 * 
 * 06/05/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCuatroRemote;
import com.sysman.nomina.enums.FrmActDatosReformaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
/**
 *
 * @version 1.0, 06/05/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmActDatosReformaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>

	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	@EJB
	private EjbNominaCuatroRemote ejbNominaCuatro;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos subirPlantilla y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivosubirPlantilla;
	private List<Registro> listaFondoP;
	private List<Registro> listaRegimen;
	private List<Registro> listaPersonal;
	private int contador;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Crea una nueva instancia de FrmActDatosReforma
	 */
	public FrmActDatosReformaControlador() {
		super();
		compania = SessionUtil.getCompania();
		contArchivosubirPlantilla = new ContenedorArchivo();
		try {
			numFormulario=2511;

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
	 * Metodo ejecutado al oprimir el boton cargarPlantilla
	 * en la vista
	 *
	 *
	 */
	public void oprimircargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;
		try (FileInputStream file = new FileInputStream(contArchivosubirPlantilla.getArchivo());) {

			if (validarArchivo()) {

				String rutaArchivo = contArchivosubirPlantilla.getArchivo().getPath();

				String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).substring(1,
						rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length()).length());

				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(file);
				} else {
					workbook = new XSSFWorkbook(file);
				}
				Sheet sheet = workbook.getSheet("PERSONAL");

				for (Row row : sheet) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}

					capturaDatosExcel(row);
				}
				cadena = cadena + "')";
				cargarDatos();

			}
		} catch (IOException | NumberFormatException| NullPointerException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton descargarPlantilla
	 * en la vista
	 * @throws IOException 
	 *
	 *
	 */
	public void oprimirdescargarPlantilla() throws IOException {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			HSSFSheet excelSheet = workbook.createSheet("PERSONAL");

			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Arial");
			font.setBold(true);

			// Tamaño de letra
			font.setFontHeightInPoints((short) 10);

			/* Estilo encabezado */
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);
			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle lockedStyle = workbook.createCellStyle();
			lockedStyle.setLocked(true);

			CellStyle unlockedStyle = workbook.createCellStyle();
			unlockedStyle.setLocked(false);

			Row row = excelSheet.createRow(0);
			Cell cell = row.createCell(0);

			cell = row.createCell(0);
			cell.setCellValue("ID DE EMPLEADO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("N° IDENTIFICACION");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("NOMBRE DEL EMPLEADO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("ESTADO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(3);
			
			cell = row.createCell(4);
			cell.setCellValue("FECHA RETIRO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(4);
			
			cell = row.createCell(5);
			cell.setCellValue("FONDO ACTUAL DE PENSIONES");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(5);

			cell = row.createCell(6);
			cell.setCellValue("FONDO PENSION ACCAI");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(6);

			cell = row.createCell(7);
			cell.setCellValue("FECHA INGRESO ACCAI");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(7);

			cell = row.createCell(8);
			cell.setCellValue("REGIMEN TRANSICION");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(8);

			CellStyle dateCellStyle = workbook.createCellStyle();
			short dateFormat = workbook.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy");
			dateCellStyle.setDataFormat(dateFormat);

			// Validación de fecha en columna F (índice 7)
			DataValidationHelper validationHelper = excelSheet.getDataValidationHelper();
			DataValidationConstraint dateConstraint = validationHelper.createDateConstraint(
					DataValidationConstraint.OperatorType.BETWEEN,
					"01/01/1900", "31/12/2100", "dd/mm/yyyy"
					);

			// Rango de celdas a validar: F2:F10000 (filas 1 a 9999, índice base 0)
			CellRangeAddressList addressList = new CellRangeAddressList(1, 10000, 7, 7); // columna F
			DataValidation validation = validationHelper.createValidation(dateConstraint, addressList);

			// Para compatibilidad con versiones modernas de Excel
			if (validation instanceof XSSFDataValidation) {
				validation.setSuppressDropDownArrow(true);
				validation.setShowErrorBox(true);
			}

			excelSheet.addValidationData(validation);

			Map<String, Object> param = new HashMap<>();
			param.put("COMPANIA", compania);

			listaFondoP = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(FrmActDatosReformaControladorUrlEnum.URL640001
											.getValue())
									.getUrl(),
									param));

			listaRegimen = new ArrayList<>();

			Registro registro3 = new Registro();
			registro3.getCampos().put("CODIGO", "0");
			registro3.getCampos().put("NOMBRE", "NO");

			Registro registro4 = new Registro();
			registro4.getCampos().put("CODIGO", "-1");
			registro4.getCampos().put("NOMBRE", "SI");

			listaRegimen.add(registro3);
			listaRegimen.add(registro4);

			if (listaFondoP.isEmpty()) {
				System.out.println("Lista Fondos esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaFondoP, 'G', 1, 10000, "FONDO");
				addValidationToSheet2(workbook, excelSheet, listaFondoP, 'F', 1, 10000, "FONDO_DE_PENSION");
			}
			addValidationToSheet2(workbook, excelSheet, listaRegimen, 'I', 1, 10000, "APLICA_REGIMEN");

			listaPersonal = RegistroConverter
					.toListRegistro(
							requestManager
							.getList(UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmActDatosReformaControladorUrlEnum.URL210167
											.getValue())
									.getUrl(),
									param));

			int rowNum = 1; 
			for (Registro reg : listaPersonal) {
				Row rowData = excelSheet.createRow(rowNum++);

				Cell cellData = rowData.createCell(0);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("ID")));
				cellData.setCellStyle(lockedStyle);

				cellData = rowData.createCell(1);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("DOCUMENTO")));
				cellData.setCellStyle(lockedStyle);

				cellData = rowData.createCell(2);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("NOMBRE")));
				cellData.setCellStyle(lockedStyle);

				cellData = rowData.createCell(3);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("ESTADO")));
				cellData.setCellStyle(lockedStyle);
				
				cellData = rowData.createCell(4);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("FECHA_DE_RETIRO")));
				cellData.setCellStyle(unlockedStyle);
				
				cellData = rowData.createCell(5);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("FONDO_DE_PENSION")));
				cellData.setCellStyle(unlockedStyle);

				cellData = rowData.createCell(6);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("FONDO")));
				cellData.setCellStyle(unlockedStyle);

				cellData = rowData.createCell(7);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("FECHA")));
				cellData.setCellStyle(unlockedStyle);

				cellData = rowData.createCell(8);
				cellData.setCellValue(SysmanFunciones.toString(reg.getCampos().get("REGIMEN")));
				cellData.setCellStyle(unlockedStyle);

				for (int i = 0; i <= 8; i++) {
					excelSheet.autoSizeColumn(i);
				}
			}
			//			excelSheet.protectSheet("Sysman10*"); 
			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Cargar Reforma Pensional.xls");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		}      
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	/**
	 * Agregar una lista desplegable a la pagina de la hoja
	 *
	 * Archivo de Excel del libro de trabajo @param, utilizado para agregar el
	 * nombre
	 * 
	 * @param targetSheet La pagina de la hoja donde se encuentra la lista en
	 *                    cascada
	 * @param options     Datos en cascada ['Baidu', 'Alibaba']
	 * @param column      La columna de la lista desplegable comienza en'A '
	 * @param fromRow     fila de inicio del limite desplegable
	 * @param endRow      limite desplegable de la fila final
	 */
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
			int fromRow, int endRow, String name) {
		String hiddenSheetName = name;
		String nameName = column + "_parent";
		// valida si la hoja ya fue creada
		Sheet optionsSheet = workbook.getSheet(hiddenSheetName);
		if (optionsSheet == null) {
			// crea la hoja
			optionsSheet = workbook.createSheet(hiddenSheetName);

			int rowIndex = 0;
			for (Registro option : options) {
				int columnIndex = 0;
				Row row = optionsSheet.createRow(rowIndex++);
				Cell cell = row.createCell(columnIndex++);
				Cell cell1 = row.createCell(columnIndex);
				String codigo = name.equals("FONDO") || name.equals("FONDO_DE_PENSION")? option.getCampos().get("NOMBRE_DEL_FONDO").toString() : option.getCampos().get("CODIGO").toString();
				String nombre = name.equals("FONDO") || name.equals("FONDO_DE_PENSION")? option.getCampos().get("ID_DEL_FONDO").toString()+" " + option.getCampos().get("NOMBRE_DEL_FONDO").toString() : option.getCampos().get("NOMBRE").toString();
				cell.setCellValue(nombre);
				cell1.setCellValue(codigo);
			}

			// Ajustar el ancho de las columnas
			optionsSheet.autoSizeColumn(0);
			optionsSheet.autoSizeColumn(1);
		}

		// creacion de la lista
		createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
		optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, (int) column - 'A',
				(int) column - 'A');
		targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}


	private static Name createName(Workbook workbook, String nameName, String formula) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		name.setRefersToFormula(formula);
		return name;
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
	 * Verifica el valor de la celda y retorna false si esta vacia.
	 * 
	 * @param celda Objeto de tipo <code>Cell</code>
	 * @return false si la celda esta vacia.
	 */
	private boolean validarCelda(Cell celda) {
		if (celda == null) {
			return false;
		}
		
		switch  (celda.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            return !celda.getStringCellValue().trim().isEmpty();
        case Cell.CELL_TYPE_NUMERIC:
            return true;
        default:
            return false;
		}

	}

	private void capturaDatosExcel(Row row) throws ParseException {
		if (row.getRowNum() > 0) {
			for (int i = 0; i <= 8; i++) {
				String val = row.getCell(i) + "";
				if (val == null || val.isEmpty() || val.equals("null")) {
					val = "NoData";
				} else if (i == 7) {
					val = row.getCell(i).getCellType() == 1 ? row.getCell(i).getStringCellValue() : SysmanFunciones.convertirAFechaCadena(row.getCell(i).getDateCellValue());
				} else {
					val = row.getCell(i).getCellType() == 1 ?  row.getCell(i).getStringCellValue() : String.valueOf((long) row.getCell(i).getNumericCellValue());
				}

				contador = contador + val.length();

				if (contador >= 3000)
				{
					cadena = cadena + "') || TO_CLOB('";
					contador = 0;
				}

				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}
	}

	private void cargarDatos() {
		String datos = null;
		try {

			datos = ejbNominaCuatro.cargarDatosReforma(compania, cadena, SessionUtil.getUser().getCodigo());
			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "reporte_datos_reforma.txt");

		} catch (SystemException | JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}


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
}
