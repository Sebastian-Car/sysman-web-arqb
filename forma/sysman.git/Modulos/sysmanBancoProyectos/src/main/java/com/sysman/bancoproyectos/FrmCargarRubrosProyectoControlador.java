/*-
 * FrmCargarRubrosProyectoControlador.java
 *
 * 1.0
 * 
 * 23/12/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.bancoproyectos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoUnoRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanConstantes;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
import com.sysman.util.ContenedorArchivo;

/**
 *
 * @version 1.0, 23/12/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmCargarRubrosProyectoControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	public String usuario;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel = new ContenedorArchivo();
	private String cadena;
	private int contador;

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbBancoProyectoUnoRemote ejbBancoProyectoUno;

	/**
	 * Crea una nueva instancia de FrmCargarRubrosProyectoControlador
	 */
	public FrmCargarRubrosProyectoControlador() {
		super();
		compania = SessionUtil.getCompania();
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_CARGAR_RUBROS_POR_PROYECTO.getCodigo();
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
//<CARGAR_LISTA>
//</CARGAR_LISTA>
//<CARGAR_LISTA_COMBO_GRANDE>
//</CARGAR_LISTA_COMBO_GRANDE>
//<CREAR_ARBOLES>
//</CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnCargarPlantilla en la vista
	 *
	 *
	 */
	public void oprimirbtnCargarPlantilla() {

		Workbook workbook = null;
		cadena = "TO_CLOB('";
		contador = 0;

		if (!validarArchivo()) {
			return;
		}

		try (FileInputStream file = new FileInputStream(contArchivocargarExcel.getArchivo())) {

			String rutaArchivo = contArchivocargarExcel.getArchivo().getPath();
			String extension = rutaArchivo.substring(rutaArchivo.lastIndexOf('.') + 1);

			if ("xls".equalsIgnoreCase(extension)) {
				workbook = new HSSFWorkbook(file);
			} else {
				workbook = new XSSFWorkbook(file);
			}

			Sheet sheet = workbook.getSheet("ENCABEZADO");

			for (Row row : sheet) {

				if (row.getRowNum() == 0) {
					continue;
				}


				capturaDatosExcelRubrosProyecto(row);
			}

			cadena = cadena + "')";

			String clobErrores = ejbBancoProyectoUno.cargarRubrosProyecto(compania, cadena, usuario);

			if (clobErrores != null && !clobErrores.trim().isEmpty()) {

				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clobErrores);

				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "RESULTADO_CARGUE_RUBROS_PROYECTO.txt");
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			try {
				if (workbook != null) {
					workbook.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	public void ejecutaractualizarMensaje() {
		JsfUtil.agregarMensajeInformativo("Proceso Finalizado");
	}

	/**
	 * Valida que se suba un archivo
	 * 
	 * @return
	 */
	public boolean validarArchivo() {

		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}


	private void capturaDatosExcelRubrosProyecto(Row row) {

		DataFormatter formatter = new DataFormatter();

		for (int i = 0; i < 7; i++) {

	        Cell cell = row.getCell(i);
	        String val = "";

	        if (cell != null) {
	            val = formatter.formatCellValue(cell);
	        }

	        if (val == null || val.trim().isEmpty() || "null".equalsIgnoreCase(val.trim())) {
	            val = "NoDato";
	        } else {
	            val = val.trim();
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

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnDescargarPlantilla en la vista
	 *
	 *
	 */
	public void oprimirbtnDescargarPlantilla() {
		setArchivoDescarga(null);
		HSSFWorkbook workbook = new HSSFWorkbook();

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Font fontHeader = workbook.createFont();
			fontHeader.setFontName("Arial");
			fontHeader.setBold(true);
			fontHeader.setFontHeightInPoints((short) 10);

			CellStyle styleHeader = workbook.createCellStyle();
			styleHeader.setFont(fontHeader);
			styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleHeader.setBorderBottom(BorderStyle.THIN);
			styleHeader.setBorderTop(BorderStyle.THIN);
			styleHeader.setBorderLeft(BorderStyle.THIN);
			styleHeader.setBorderRight(BorderStyle.THIN);

			CellStyle styleNormal = workbook.createCellStyle();
			styleNormal.setBorderBottom(BorderStyle.THIN);
			styleNormal.setBorderTop(BorderStyle.THIN);
			styleNormal.setBorderLeft(BorderStyle.THIN);
			styleNormal.setBorderRight(BorderStyle.THIN);
			styleNormal.setWrapText(true);
			
			HSSFDataFormat dataFormat = workbook.createDataFormat();

			CellStyle styleTexto = workbook.createCellStyle();
			styleTexto.setDataFormat(dataFormat.getFormat("@"));
			styleTexto.setWrapText(true);

			Font fontTitle = workbook.createFont();
			fontTitle.setFontName("Arial");
			fontTitle.setBold(true);
			fontTitle.setFontHeightInPoints((short) 12);

			CellStyle styleTitle = workbook.createCellStyle();
			styleTitle.setFont(fontTitle);

			HSSFSheet excelSheet = workbook.createSheet("ENCABEZADO");
			Row row = excelSheet.createRow(0);

			String[] encabezados = { "PROYECTO", "VIGENCIA", "RUBRO", "FUENTE", "CENTRO_COSTO", "REFERENCIA",
					"AUXILIAR" };

			for (int i = 0; i < encabezados.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(encabezados[i]);
				cell.setCellStyle(styleHeader);
				excelSheet.autoSizeColumn(i);
			}
			
			Row dataRow = excelSheet.createRow(1);
			for (int i = 0; i < encabezados.length; i++) {
			    Cell cell = dataRow.createCell(i);
			    cell.setCellValue("");
			    cell.setCellStyle(styleTexto);
			}

			/*
			 * ========================= HOJA INSTRUCTIVO =========================
			 */
			HSSFSheet sheetInst = workbook.createSheet("INSTRUCTIVO");

			int fila = 0;

			Row rowTitle = sheetInst.createRow(fila++);
			Cell cellTitle = rowTitle.createCell(0);
			cellTitle.setCellValue("CARGUE RUBROS POR PROYECTO");
			cellTitle.setCellStyle(styleTitle);

			fila++; 

			Row rowHead = sheetInst.createRow(fila++);
			String[] cols = { "CAMPO", "FORMATO", "TIPO", "OBSERVACIONES" };

			for (int i = 0; i < cols.length; i++) {
				Cell cell = rowHead.createCell(i);
				cell.setCellValue(cols[i]);
				cell.setCellStyle(styleHeader);
			}

			Object[][] data = { { "PROYECTO", "VARCHAR2(16 CHAR)", "Obligatorio",
					"Código del proyecto debe estar creado en la ruta BANCO DE PROYECTOS / PROCESOS / REGISTRO DE PROYECTOS" },

					{ "VIGENCIA", "NUMBER(4,0)", "Obligatorio", "Vigencia de los rubros a cargar en cada proyecto" },

					{ "RUBRO", "VARCHAR2(100 CHAR)", "Obligatorio",
							"Código de rubro presupuestal debe estar creado en el plan presupuestal para la vigencia definida en el campo VIGENCIA" },

					{ "FUENTE", "VARCHAR2(20 CHAR)", "Obligatorio",
							"Código de la fuente de recurso debe estar creada para la vigencia definida en el campo VIGENCIA" },

					{ "CENTRO_COSTO", "VARCHAR2(20 CHAR)", "Obligatorio",
							"Código del centro de costo debe estar creado para la vigencia definida en el campo VIGENCIA" },

					{ "REFERENCIA", "VARCHAR2(20 CHAR)", "Obligatorio",
							"Código de la referencia debe estar creada para la vigencia definida en el campo VIGENCIA" },

					{ "AUXILIAR", "VARCHAR2(32 CHAR)", "Obligatorio",
							"Código del auxiliar debe estar creado para la vigencia definida en el campo VIGENCIA" } };

			for (Object[] filaData : data) {
				Row r = sheetInst.createRow(fila++);
				for (int i = 0; i < filaData.length; i++) {
					Cell c = r.createCell(i);
					c.setCellValue(filaData[i].toString());
					c.setCellStyle(styleNormal);
				}
			}

			fila++; 

			
			Row rowNota = sheetInst.createRow(fila++);
			Cell cellNota = rowNota.createCell(0);
			cellNota.setCellValue(
					"Importante: El presupuesto debe estar cargado en el aplicativo para la vigencia, antes de iniciar el cargue de rubros por proyecto");
			cellNota.setCellStyle(styleNormal);
			
			fila++; 

			Row rowEjemploTitulo = sheetInst.createRow(fila++);
			Cell cellEjemploTitulo = rowEjemploTitulo.createCell(0);
			cellEjemploTitulo.setCellValue("EJEMPLO");
			cellEjemploTitulo.setCellStyle(styleTitle);

			Row rowEjemploHeader = sheetInst.createRow(fila++);

			String[] encabezadoEjemplo = {
			    "PROYECTO", "VIGENCIA", "RUBRO", "FUENTE",
			    "CENTRO_COSTO", "REFERENCIA", "AUXILIAR"
			};

			for (int i = 0; i < encabezadoEjemplo.length; i++) {
			    Cell cell = rowEjemploHeader.createCell(i);
			    cell.setCellValue(encabezadoEjemplo[i]);
			    cell.setCellStyle(styleHeader);
			}

			Row rowEjemploData = sheetInst.createRow(fila++);

			String[] datosEjemplo = {
			    "20240002",
			    "2026",
			    "2.3.1.01.01.001.01-12-22-2201-1090002",
			    "1.2.4.1.01",
			    "99999999999999999999",
			    "99999999999999999999",
			    "220101700"
			};

			for (int i = 0; i < datosEjemplo.length; i++) {
			    Cell cell = rowEjemploData.createCell(i);
			    cell.setCellValue(datosEjemplo[i]);
			    cell.setCellStyle(styleNormal);
			}

			
			for (int i = 0; i < encabezadoEjemplo.length; i++) {
			    sheetInst.autoSizeColumn(i);
			}


			workbook.write(out);

			String nombreArchivo = "Rubros por proyecto.xls";
			setArchivoDescarga(JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), nombreArchivo));

		} catch (IOException | JRException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
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
	 * @param contArchivocargarExcel Variable a asignar en contArchivocargarExcel
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}

	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
