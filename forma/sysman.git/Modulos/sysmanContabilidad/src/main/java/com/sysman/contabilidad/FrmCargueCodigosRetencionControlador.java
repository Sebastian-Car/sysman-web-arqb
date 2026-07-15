/*-
 * FrmCargueCodigosRetencionControlador.java
 *
 * 1.0
 * 
 * 19/06/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.enums.GeneralCodigoFormaEnum;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import java.io.ByteArrayInputStream;
import com.sysman.util.enums.ConstanteArchivo;
import java.io.IOException;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import com.sysman.exception.SystemException;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 19/06/2026
 * @author CristianFerneySuescu
 */
@ManagedBean
@ViewScoped
public class  FrmCargueCodigosRetencionControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	private String cadena;
	private int contador;
	private boolean procesoCorrecto = false;
	
	private int dia;
	private int mes;
	/*
	 * Variable usada para almacenar el numero del formulario, esta variable
	 */
	private String mensajeResultado = "";


	//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos subirPlantillla y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivosubirPlantillla;
	/**
	 * Crea una nueva instancia de FrmCargueCodigosRetencion
	 */

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	@EJB
	 EjbContabilidadTresRemote ejbContabilidadTres;

	public FrmCargueCodigosRetencionControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		contArchivosubirPlantillla = new ContenedorArchivo();
		
		 mes = SysmanFunciones.mes(new Date());
		 dia = SysmanFunciones.dia(new Date());
		
		
		//Numero formulario 2593
		try {
			numFormulario=2593;
			numFormulario = GeneralCodigoFormaEnum.FRM_CARGUE_CODIGOS_RETENCION.getCodigo();
			validarPermisos();

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

		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */

	@Override
	public void abrirFormulario(){

	}


	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cerrar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCerrar() {
		RequestContext.getCurrentInstance().closeDialog(mensajeResultado);

	}

	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {

		if (contArchivosubirPlantillla.getArchivo() == null) {
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
	    try (FileInputStream file = new FileInputStream(contArchivosubirPlantillla.getArchivo());) {

	        workbook = WorkbookFactory.create(file);

	        Sheet sheet = workbook.getSheet("Estructura");
	        if (sheet == null) {
	            // Si no existe la hoja Estructura, toma la primera del libro
	            sheet = workbook.getSheetAt(0);
	        }
	        
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

	    } catch (IOException | NumberFormatException | NullPointerException | ParseException
	            | org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	    //</CODIGO_DESARROLLADO>
	}


	/**
	 * Retorna true si TODAS las celdas de la fila (columnas 0 a 4) estÃ¯Â¿Â½n vacÃ¯Â¿Â½as.
	 * Una fila con al menos un dato en cualquier columna NO se considera vacÃ¯Â¿Â½a.
	 */
	private boolean filaCompletamenteVacia(Row row) {
		for (int i = 0; i <= 12; i++) {
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
			for (int i = 0; i <= 12; i++) {

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
					// Columnas numÃ¯Â¿Â½ricas / texto
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

	        datos = ejbContabilidadTres.cargarMasivoRetencion(
	                compania,
	                cadena,
	                mes,
	                dia,
	                SessionUtil.getUser().getCodigo());

	        ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(datos);
	        archivoDescarga = JsfUtil.getArchivoDescarga(
	                streamTexto,
	                "resultadoCargueRetencion.txt");

	        mensajeResultado = datos;
	        procesoCorrecto = true;

	    } catch (SystemException | JRException | IOException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	        procesoCorrecto = false;
	    }
	}

	public void oprimirDescargarPlantilla() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;

		HSSFWorkbook workbook = new HSSFWorkbook();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			/* ==========================
			 * ESTILOS COMPARTIDOS
			 * ========================== */
			Font headerFont = workbook.createFont();
			headerFont.setFontName("Arial");
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 10);

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

			Font normalFont = workbook.createFont();
			normalFont.setFontName("Arial");
			normalFont.setFontHeightInPoints((short) 9);

			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setFont(normalFont);
			borderStyle.setVerticalAlignment(VerticalAlignment.TOP);
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);

			CellStyle wrapStyle = workbook.createCellStyle();
			wrapStyle.setFont(normalFont);
			wrapStyle.setWrapText(true);
			wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);
			wrapStyle.setBorderBottom(BorderStyle.THIN);
			wrapStyle.setBorderTop(BorderStyle.THIN);
			wrapStyle.setBorderLeft(BorderStyle.THIN);
			wrapStyle.setBorderRight(BorderStyle.THIN);

			Font tituloFont = workbook.createFont();
			tituloFont.setFontName("Arial");
			tituloFont.setBold(true);
			tituloFont.setFontHeightInPoints((short) 9);

			CellStyle tituloStyle = workbook.createCellStyle();
			tituloStyle.setFont(tituloFont);

			/* ==========================
			 * HOJA 1: ESTRUCTURA
			 * ========================== */
			HSSFSheet sheetEstructura = workbook.createSheet("Estructura");

			String[] headers = {
					"AÑO",
					"TIPO_RETENCION",
					"CODIGO_RETENCION",
					"NOMBRE_RETENCION",
					"CUENTA_CREDITO",
					"CUENTA_CREDITO_1",
					"LIMITE_INFERIOR",
					"PORC_BASE",
					"PORC_APLICAR",
					"APL_LEY_1819",
					"FACTOR_REDONDEO",
					"MODIFICA_VALOR",
					"MODIFICA_BASE"
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
			tituloCell.setCellValue("CARGUE MASIVO CÓDIGOS DE RETENCIÓN");
			tituloCell.setCellStyle(tituloStyle);
			sheetInfo.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

			// Encabezados del instructivo
			String[] campos = {"CAMPO", "FORMATO", "TIPO", "VALIDACIÓN / REGLA DE NEGOCIO"};
			Row encabezado = sheetInfo.createRow(2);
			for (int i = 0; i < campos.length; i++) {
				Cell cell = encabezado.createCell(i);
				cell.setCellValue(campos[i]);
				cell.setCellStyle(headerStyle);
			}

			// Datos del instructivo
			Object[][] datos = {
					{
						"AÑO",
						"NUMBER(4,0)",
						"Obligatorio",
						"El año debe existir previamente en la tabla de Configuración de Estados y no estar en estado cerrado para permitir la carga."
					},
					{
						"TIPO_RETENCION",
						"VARCHAR2(3 CHAR)",
						"Obligatorio",
						"Debe coincidir exactamente con los códigos creados en Tipos de Retenciones (ej. FUE, ICA, IVA). Longitud máxima de 3 caracteres."
					},
					{
						"CODIGO_RETENCION",
						"VARCHAR2(3 CHAR)",
						"Obligatorio",
						"Máximo 3 caracteres alfanuméricos. El sistema validará que no existan duplicados para la misma combinación de Año + Tipo + Código."
					},
					{
						"NOMBRE_RETENCION",
						"VARCHAR2(255 CHAR)",
						"Obligatorio",
						"Descripción clara del concepto. No se permiten caracteres especiales como comillas o asteriscos."
					},
					{
						"CUENTA_CREDITO",
						"VARCHAR2(20 CHAR)",
						"Obligatorio",
						"Debe existir en el Plan Contable del año seleccionado con el indicador 'Tiene Movimiento' activo. Solo se admiten cuentas de Clase I (Impuestos)."
					},
					{
						"CUENTA_CREDITO_1",
						"VARCHAR2(20 CHAR)",
						"Condicional",
						"Requerida para retenciones que aplican Ley 1819 (Rentas). Debe cumplir las mismas validaciones de CUENTA_CREDITO."
					},
					{
						"LIMITE_INFERIOR",
						"NUMBER(20,2)",
						"Opcional",
						"Valor base mínimo para aplicar la retención. Si se deja vacío, el sistema asumirá cero (0)."
					},
					{
						"PORC_BASE",
						"NUMBER(5,2)",
						"Obligatorio",
						"Porcentaje de la base del movimiento sobre el cual se aplicará el impuesto (generalmente 100.00)."
					},
					{
						"PORC_APLICAR",
						"NUMBER(5,2)",
						"Obligatorio",
						"Tarifa del impuesto a aplicar. Admite decimales para precisión en el cálculo (ej. 3.50)."
					},
					{
						"APL_LEY_1819",
						"BOOLEAN (0 / -1)",
						"Obligatorio",
						"-1 para SI, 0 para NO. Activa la lógica de depuración de renta para contratistas asalariados bajo el Art. 383 E.T."
					},
					{
						"FACTOR_REDONDEO",
						"Lista (1,10,100,1000)",
						"Obligatorio",
						"Define el ajuste de decimales. Valores permitidos: 1, 10, 100, 1000. Crítico para legalizaciones de viáticos."
					},
					{
						"MODIFICA_VALOR",
						"BOOLEAN (0 / -1)",
						"Obligatorio",
						"-1 para SI, 0 para NO. Si está en 0, el sistema bloqueará cambios manuales en el valor del impuesto durante la causación."
					},
					{
						"MODIFICA_BASE",
						"BOOLEAN (0 / -1)",
						"Obligatorio",
						"-1 para SI, 0 para NO. Si está en 0, el sistema bloqueará la edición manual de la base gravable en el comprobante."
					}
			};

			// Anchos fijos
			sheetInfo.setColumnWidth(0, 22 * 256); // CAMPO
			sheetInfo.setColumnWidth(1, 22 * 256); // FORMATO
			sheetInfo.setColumnWidth(2, 14 * 256); // TIPO
			sheetInfo.setColumnWidth(3, 50 * 256); // VALIDACIÓN

			int rowIdx = 3;
			for (Object[] fila : datos) {
				Row row = sheetInfo.createRow(rowIdx++);
				for (int i = 0; i < fila.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(fila[i].toString());
					cell.setCellStyle(i == 3 ? wrapStyle : borderStyle);
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

			// Dato de ejemplo
			Row ejemplo = sheetInfo.createRow(rowIdx + 3);
			Object[] ejemploDatos = {
					"2025",        // AÑO
					"ICA",         // TIPO_RETENCION
					"ICA",         // CODIGO_RETENCION
					"IMPUESTO DE INDUSTRIA Y COMERCIO", // NOMBRE_RETENCION
					"24080501",    // CUENTA_CREDITO
					"24080502",    // CUENTA_CREDITO_1
					"0",           // LIMITE_INFERIOR
					"100.00",      // PORC_BASE
					"0.69",        // PORC_APLICAR
					"0",           // APL_LEY_1819
					"1",           // FACTOR_REDONDEO
					"-1",          // MODIFICA_VALOR
					"0"            // MODIFICA_BASE
			};

			for (int i = 0; i < ejemploDatos.length; i++) {
				Cell cell = ejemplo.createCell(i);
				cell.setCellValue(ejemploDatos[i].toString());
				cell.setCellStyle(borderStyle);
			}
			ejemplo.setHeight((short) -1);

			/* ==========================
			 * GENERAR ARCHIVO
			 * ========================== */
			workbook.write(out);
			workbook.close();

			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(out.toByteArray()),
					"PlantillaCodigosRetencion" + ConstanteArchivo.EXCEL97.getExtension()
					);

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
		//</CODIGO_DESARROLLADO>
	}


	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * Retorna el objeto contArchivosubirPlantillla
	 * 
	 * @return contArchivosubirPlantillla
	 */
	public ContenedorArchivo getContArchivosubirPlantillla() {
		return contArchivosubirPlantillla;
	}
	/**
	 * Asigna el objeto contArchivosubirPlantillla
	 * 
	 * @param contArchivosubirPlantillla
	 * Variable a asignar en contArchivosubirPlantillla
	 */
	public void setContArchivosubirPlantillla(ContenedorArchivo contArchivosubirPlantillla) {
		this.contArchivosubirPlantillla = contArchivosubirPlantillla;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
