/*-
 * FrmsubirauxiliaresControlador.java
 *
 * 1.0
 * 
 * 20/02/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Font;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.CellType;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.FrmsubirauxiliaresControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroGeneralRemote;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;


/**
 * Formulario donde se descarga las plantillas de los auxiliares 
 *
 * @version 1.0, 20/02/2026
 * @author ncardenas
 */
@ManagedBean
@ViewScoped
public class  FrmsubirauxiliaresControlador extends BeanBaseModal{

	private final String compania ;
	
	private final String HOJA_FUENTE = "FUENTE_RECURSO";
	
	private final String HOJA_CENTRO_COSTO = "CENTRO_COSTOS" ;
	
	private final String HOJA_REFERENCIA = "REFERENCIA" ;
	
	private final String HOJA_AUXILIAR_GENERAL = "AUXILIAR_GENERAL" ;

	private int modulo;

	private String usuario;
	
	private int contador;
	
	private String cadena;

	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private int seleccion;
	
	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos cargarExcel y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel = new ContenedorArchivo();

	private List<Registro> listaCompania;
	
	@EJB
	private EjbPresupuestoCuatroGeneralRemote cuatroGeneralRemote;
	
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmsubirauxiliaresControlador
	 */
	public FrmsubirauxiliaresControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = -1;
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_SUBIRAUXILIARES.getCodigo();// 2569;
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
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
	 * Metodo ejecutado al oprimir el boton btnDescargarPlantilla
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws SystemException 
	 *
	 */
	public void oprimirbtnDescargarPlantilla()  {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Map<String, Object> param = new HashMap<>();
			//HSSFSheet excelSheet = workbook.createSheet("FUENTE RECURSO");
			listaCompania = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmsubirauxiliaresControladorUrlEnum.URL59003.getValue()
											).getUrl(),
									param
									)
							);

			CellStyle style = crearEstiloEncabezado(workbook);

			// crearHojaCompania(workbook, listaCompania);
			crearHojaFuenteRecurso(workbook, style);
			crearHojaCentroCostos(workbook, style);
			crearHojaReferencia(workbook, style);
			crearHojaAuxiliarGeneral(workbook, style);
			
			Sheet companiaSheet = workbook.getSheet("COMPANIA");

			if (companiaSheet != null) {

			    int index = workbook.getSheetIndex(companiaSheet);

			    workbook.setSheetOrder("COMPANIA", workbook.getNumberOfSheets() - 1);

			    // volver a aplicar el nombre (esto es la clave)
			    Name name = workbook.getName("A_parent");

			    if (name != null) {

			        name.setRefersToFormula(
			            "COMPANIA!$A$1:$A$" + listaCompania.size()
			        );
			    }
			}

			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Auxiliares.xls");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (IOException | JRException | SystemException  e) {
			logger.error(e.getMessage(),e);
			JsfUtil.agregarMensajeError(e.getMessage());
		
		} 


		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * metodo para crear titulos encabezado generico
	 * 
	 */
	private void crearEncabezados(HSSFSheet sheet, String[] columnas, CellStyle style) {

		Row row = sheet.createRow(0);

		for (int i = 0; i < columnas.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnas[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn(i);
		}
	}

	/**
	 * 
	 * metodo para enviar fuente de recurso
	 * 
	 */
	private void crearHojaFuenteRecurso(HSSFWorkbook workbook, CellStyle style) {

		HSSFSheet sheet = workbook.createSheet(HOJA_FUENTE);

		String[] columnas = {
				"COMPANIA",
				"ANO",
				"CÓDIGO",
				"NOMBRE",
				"TIPO",
				"CÓDIGO DNP",
				"CÓDIGO SIA",
				"SIN SITUACIÓN FONDOS",
				"VIGENCIA ACTUAL EQUIVALENTE CUIPO",
				"RESERVA APROPIACIÓN EQUIVALENTE CUIPO",
				"RESERVA CAJA EQUIVALENTE CUIPO",
				"CODIGO CLEOPATRA",
				"CODIGO EQUIVALENTE SGTE VIGENCIA"
		};

		crearEncabezados(sheet, columnas, style);
		addValidationToSheet2(workbook, sheet, listaCompania, 'A', 1, 10000, "COMPANIA");
	}

	/**
	 * 
	 * metodo para enviar centro de costos
	 * 
	 */
	private void crearHojaCentroCostos(HSSFWorkbook workbook, CellStyle style) {

		HSSFSheet sheet = workbook.createSheet(HOJA_CENTRO_COSTO);

		String[] columnas = {
				"COMPANIA",
				"ANO",
				"CODIGO",
				"NOMBRE",
				"TIPO",
				"CATEGORIA",
				"MOVIMIENTO",
				"ACTIVO",
				"DISTRIBUIR",
				"CODIGO EQUIVALENTE SGTE VIGENCIA"
		};

		crearEncabezados(sheet, columnas, style);
		addValidationToSheet2(workbook, sheet, listaCompania, 'A', 1, 10000, "COMPANIA");
	}


	private void crearHojaReferencia(HSSFWorkbook workbook, CellStyle style) {
		HSSFSheet sheet = workbook.createSheet(HOJA_REFERENCIA);

		String[] columnas = {
				"COMPANIA",
				"ANO",
				"CODIGO",
				"NOMBRE",
				"MOVIMIENTO",
				"CODIGO EQUIVALENTE SGTE VIGENCIA"
		};

		crearEncabezados(sheet, columnas, style);
		addValidationToSheet2(workbook, sheet, listaCompania, 'A', 1, 10000, "COMPANIA");
	}

	private void crearHojaAuxiliarGeneral(HSSFWorkbook workbook, CellStyle style) {
		HSSFSheet sheet = workbook.createSheet(HOJA_AUXILIAR_GENERAL);

		String[] columnas = {
				"COMPANIA",
				"ANO",
				"CODIGO",
				"NOMBRE",
				"MOVIMIENTO",
				"CODIGO EQUIVALENTE SGTE VIGENCIA"
		};

		crearEncabezados(sheet, columnas, style);
		addValidationToSheet2(workbook, sheet, listaCompania, 'A', 1, 10000, "COMPANIA");

	}

	
	/**
	 * 
	 * metodo para  estilo de encabezado en archivo excel - generico -
	 * 
	 */
	private CellStyle crearEstiloEncabezado(HSSFWorkbook workbook) {

		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBold(true);
		font.setFontHeightInPoints((short) 10);

		CellStyle style = workbook.createCellStyle();
		style.setFont(font);

		//		groundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		//		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		return style;
	}
	
	/**
	 * Agregar una lista desplegable a la pÃƒÂ¡gina de la hoja
	 *
	 * Archivo de Excel del libro de trabajo @param, utilizado para agregar el
	 * nombre
	 * 
	 * @param targetSheet La pÃƒÂ¡gina de la hoja donde se encuentra la lista en
	 *                    cascada
	 * @param options     Datos en cascada ['Baidu', 'Alibaba']
	 * @param column      La columna de la lista desplegable comienza en'A '
	 * @param fromRow     fila de inicio del lÃƒÂ­mite desplegable
	 * @param endRow      lÃƒÂ­mite desplegable de la fila final
	 */
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
	        int fromRow, int endRow, String name) {

	    String hiddenSheetName = name;
	    Sheet optionsSheet = workbook.getSheet(hiddenSheetName);
	    String nameName = column + "_parent";

	    // Solo crear la hoja si NO existe
	    if (optionsSheet == null) {

	        optionsSheet = workbook.createSheet(hiddenSheetName);
	        int rowIndex = 0;
	        for (Registro option : options) {

	            Row row = optionsSheet.createRow(rowIndex++);

	            Cell cellCodigo = row.createCell(0);
	            Cell cellNombre = row.createCell(1);

	            cellCodigo.setCellValue(option.getCampos().get("CODIGO").toString());
	            cellNombre.setCellValue(option.getCampos().get("NOMBRE").toString());
	        }

	        // Crear el nombre definido solo si no existe
	        Name namedRange = workbook.getName(nameName);

	        if (namedRange == null) {

	            namedRange = workbook.createName();
	            namedRange.setNameName(nameName);
	            namedRange.setRefersToFormula(hiddenSheetName + "!$A$1:$A$" + options.size());
	        }

	        optionsSheet.protectSheet("Sysman10*");
	    }

	    // Crear la validación SIEMPRE
	    DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);

	    CellRangeAddressList regions = new CellRangeAddressList(
	            fromRow,
	            endRow,
	            column - 'A',
	            column - 'A'
	    );

	    targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}
	
	private static Name createName(Workbook workbook, String nameName, String formula) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		name.setRefersToFormula(formula);
		return name;
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnCargarPlantilla
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnCargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;
		
		if (!validarArchivo()) {
			return;
		}
		File archivo = contArchivocargarExcel.getArchivo();
		try (FileInputStream file = new FileInputStream(archivo)) {
			String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();
			String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf('.') + 1);
			Sheet sheet = null;
			String tabla = null;
			//Row header = null;
			int column = 0;
			int registrosProcesados = 0;

			if ("xls".equalsIgnoreCase(extension)) {
				workbook = new HSSFWorkbook(file);
			} else {
				workbook = new XSSFWorkbook(file);
			}

			switch (seleccion) {
			   case 1:// fuente de recurso
			   	sheet = workbook.getSheet(HOJA_FUENTE);    	
			   	column = 13;
			   	tabla = "FUENTE_RECURSOS";
			   	break;
			   case 2://centro de costo
			   	sheet = workbook.getSheet(HOJA_CENTRO_COSTO);
				column = 10;
				tabla = "CENTRO_COSTO";
			   	break;
			   case 3:// referencia
			   	sheet = workbook.getSheet(HOJA_REFERENCIA);
			   	column =  6;
			   	tabla = "REFERENCIA";
			   	break;
			   case 4:// auxiliar general
			   	sheet = workbook.getSheet(HOJA_AUXILIAR_GENERAL);
			   	column =  6;
			   	tabla = "AUXILIAR";
			   	break;
			   default://todas       
			}
			 
			 
			for (Row row : sheet) {

					if (row.getRowNum() == 0) {
						continue;
					}
					if (filaVacia(row)) {
						continue;
					}
					capturaDatosExcelAuxiliaresGenerales(row, column);
					registrosProcesados ++;
			}

			cadena = cadena + "')";
				
			if (registrosProcesados == 0) {
				JsfUtil.agregarMensajeAlerta("La hoja " + sheet.getSheetName() + " no tiene información.");
				return;
			}
				
				
			String clobErrores = cuatroGeneralRemote.cargarAuxiliares(tabla, cadena, SessionUtil.getUser().getCodigo());
						
			if (clobErrores != null && !clobErrores.trim().isEmpty()) {
			
				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clobErrores);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LOG.txt");
			}
			
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4504"));
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}	

		//</CODIGO_DESARROLLADO>
	}
	
	
	//Método para validar si una fila está vacía
	private boolean filaVacia(Row row) {

	    if (row == null) {
	        return true;
	    }

	    DataFormatter formatter = new DataFormatter();

	    for (int c = 0; c < 7; c++) { 
	        Cell cell = row.getCell(c);

	        if (cell != null) {
	            String val = formatter.formatCellValue(cell);
	            if (val != null && !val.trim().isEmpty()) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {

		if (contArchivocargarExcel == null || contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}
	
	private void capturaDatosExcelAuxiliaresGenerales(Row row, int column) {

	    for (int i = 0; i < column; i++) {

	        Cell cell = row.getCell(i);
	        String val = "";

	        if (cell != null) {

	            int cellType = cell.getCellType();

	            if (cellType == Cell.CELL_TYPE_NUMERIC) {

	                BigDecimal bd = BigDecimal.valueOf(cell.getNumericCellValue());
	                bd = bd.stripTrailingZeros();
	                //val = bd.toPlainString().replace('.', ',');
	                val = bd.toPlainString();

	            } else if (cellType == Cell.CELL_TYPE_STRING) {

	                val = cell.getStringCellValue().trim();
	                val = normalizarTexto(val);

	            } else {
	                val = "";
	            }
	        }

	        if (val == null || val.isEmpty() || "null".equalsIgnoreCase(val)) {
	            val = "NoDato";
	        }

	        contador += val.length();

	        if (contador >= 3000) {
	            cadena = cadena + "') || TO_CLOB('";
	            contador = 0;
	        }

	        cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
	    }

	    cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());
	    cadena = cadena + SysmanConstantes.SEPARADOR_REG;
	}
	
	private String normalizarTexto(String texto) {

	    if (texto == null) {
	        return "";
	    }

	    // Reemplazar saltos de línea por espacio
	    texto = texto.replaceAll("\\r\\n|\\r|\\n", " ");

	    // Reemplazar tabulaciones
	    texto = texto.replaceAll("\\t", " ");

	    // Quitar espacios múltiples
	    texto = texto.replaceAll(" +", " ");

	    // 4. Trim final
	    return texto.trim();
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
	 * Retorna la variable seleccion
	 * 
	 * @return  seleccion
	 */
	
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	public int getSeleccion() {
		return seleccion;
	}
	public void setSeleccion(int seleccion) {
		this.seleccion = seleccion;
	}
	/**
	 * Retorna el objeto contArchivocargarExcel
	 * 
	 * @return contArchivocargarExcel
	 */
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}
	/**
	 * Asigna el objeto contArchivocargarExcel
	 * 
	 * @param contArchivocargarExcel
	 * Variable a asignar en contArchivocargarExcel
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

	public int getModulo() {
		return modulo;
	}
	public void setModulo(int modulo) {
		this.modulo = modulo;
	}

	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public int getContador() {
		return contador;
	}
	public void setContador(int contador) {
		this.contador = contador;
	}
	public String getCadena() {
		return cadena;
	}

	
	
	
	
	
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
