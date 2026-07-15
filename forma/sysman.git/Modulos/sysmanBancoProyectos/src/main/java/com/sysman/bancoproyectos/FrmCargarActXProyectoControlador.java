/*-
 * FrmCargarActXProyectoControlador.java
 *
 * 1.0
 * 
 * 19/01/2026
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
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;

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
import org.apache.poi.ss.usermodel.CellType;



import com.sysman.bancoproyectos.ejb.EjbBancoProyectoUnoRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;

import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 19/01/2026
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrmCargarActXProyectoControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 */
	private String anio;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel = new ContenedorArchivo();

//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	private List<Registro> listaAno;
	private int modulo;
	private String URL_ANIO = "4001";
	private int contador;
	private String cadena;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbBancoProyectoUnoRemote ejbBancoProyectoUno;
	private String usuario;

	
	/**
	 * Crea una nueva instancia de FrmCargarActXProyectoControlador
	 */
	public FrmCargarActXProyectoControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = -1;
		usuario = SessionUtil.getUser().getCodigo();
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_ACT_ACTIVIDADES_POR_PROYECTO.getCodigo();// 2561;
			validarPermisos();
//<INI_ADICIONAL>
//</INI_ADICIONAL>
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
//<CARGAR_LISTA>
		cargarListaAno();
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
	/**
	 * 
	 * Carga la lista listaAno
	 *
	 */
	/**
	 * 
	 * Carga la lista listaAno
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaAno = RegistroConverter.toListRegistro(requestManager
					.getList(UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(URL_ANIO).getUrl(), param));
		} catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnDescargarPlantilla en la vista
	 *
	 *
	 */
	public void oprimirbtnDescargarPlantilla() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;

		String reporte = "800080ActividadesXVigencias";
		String excelSalida = "800080ActividadesXVigencias";

		try {

			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("compania", compania);
			reemplazar.put("vigencia", anio);

			Map<String, Object> parametros = new HashMap<>();

			// GENERA EXCEL PLANO (HOJA 1)

			StreamedContent sc = JsfUtil.exportarExcelPlano(reporte, excelSalida, ConectorPool.ESQUEMA_SYSMAN,
					FORMATOS.EXCEL, reemplazar, parametros, modulo);

			InputStream is = sc.getStream();

			XSSFWorkbook workbook = new XSSFWorkbook(is);
			
			XSSFDataFormat dataFormat = workbook.createDataFormat();

			CellStyle styleTexto = workbook.createCellStyle();
			styleTexto.setDataFormat(dataFormat.getFormat("@"));
			styleTexto.setWrapText(true);


			XSSFSheet sheetDatos = workbook.getSheetAt(0); // Hoja DATOS
			XSSFRow headerDatos = sheetDatos.getRow(0);
			XSSFRow ejemploDatos = sheetDatos.getRow(1);

			XSSFCellStyle[] estilosHeader = new XSSFCellStyle[7];
			XSSFCellStyle[] estilosDatos = new XSSFCellStyle[7];

			for (int i = 0; i < 7; i++) {
				estilosHeader[i] = headerDatos.getCell(i).getCellStyle();
				estilosDatos[i] = ejemploDatos.getCell(i).getCellStyle();
			}

			// CREAR HOJA INSTRUCTIVO

			XSSFSheet sheet = workbook.createSheet("INSTRUCTIVO");

			// Estilo encabezado
			XSSFCellStyle styleHeader = workbook.createCellStyle();
			XSSFFont fontHeader = workbook.createFont();
			fontHeader.setBold(true);
			styleHeader.setFont(fontHeader);
			styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleHeader.setBorderBottom(BorderStyle.THIN);
			styleHeader.setBorderTop(BorderStyle.THIN);
			styleHeader.setBorderLeft(BorderStyle.THIN);
			styleHeader.setBorderRight(BorderStyle.THIN);

			int rowNum = 0;

			// Título
			XSSFRow rowTitulo = sheet.createRow(rowNum++);
			rowTitulo.createCell(0).setCellValue("ACTUALIZACIÓN MASIVA DE ACTIVIDADES POR PROYECTO");

			rowNum++; // línea en blanco

			// Encabezados tabla
			XSSFRow rowHeader = sheet.createRow(rowNum++);
			String[] headers = { "CAMPO", "FORMATO", "TIPO", "OBSERVACIONES" };

			for (int i = 0; i < headers.length; i++) {
				XSSFCell cell = rowHeader.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(styleHeader);
			}

			// Contenido instructivo
			Object[][] data = {
					{ "VIGENCIA", "NUMBER(4,0)", "Obligatorio", "Vigencia de las actividades creadas en el proyecto" },
					{ "CODIGO_PROYECTO", "VARCHAR2(16 CHAR)", "Obligatorio",
							"Código del proyecto creado en la ruta BANCO DE PROYECTOS/PROCESOS/PROYECTOS/REGISTRO DE PROYECTOS" },
					{ "COMPONENTE", "VARCHAR2(12 CHAR)", "Obligatorio",
							"Código del componente en el proyecto relacionado a la vigencia" },
					{ "ACTIVIDAD", "VARCHAR2(12 CHAR)", "Obligatorio",
							"Código de la actividad relacionado con el proyecto y componente en la vigencia" },
					{ "CANTIDAD", "NUMBER(20,2)", "Obligatorio",
							"Cantidad a actualizar, debe ser mayor a cero y no debe estar vacío" },
					{ "COSTO_UNITARIO", "NUMBER(20,2)", "Obligatorio",
							"Valor unitario mayor o igual a cero y no debe estar vacío" },
					{ "DESCRIPCION", "VARCHAR2(500 CHAR)", "Opcional",
							"Descripción de la actividad, no permite actualización" } };

			for (Object[] fila : data) {
				XSSFRow row = sheet.createRow(rowNum++);
				for (int i = 0; i < fila.length; i++) {
					row.createCell(i).setCellValue(fila[i].toString());
				}
			}

			rowNum++;
			sheet.createRow(rowNum++).createCell(0).setCellValue(
					"Importante: Los proyectos deben tener creadas las actividades para la vigencia a actualizar");

			rowNum += 2;

			sheet.createRow(rowNum++).createCell(0).setCellValue("EJEMPLO DE REGISTRO DILIGENCIADO");

			XSSFRow rowHeaderEjemplo = sheet.createRow(rowNum++);
			String[] columnas = { "VIGENCIA", "CODIGO_PROYECTO", "COMPONENTE", "ACTIVIDAD", "CANTIDAD",
					"COSTO_UNITARIO", "DESCRIPCION" };

			for (int i = 0; i < columnas.length; i++) {
				XSSFCell cell = rowHeaderEjemplo.createCell(i);
				cell.setCellValue(columnas[i]);
				cell.setCellStyle(estilosHeader[i]);
			}

			XSSFRow rowEjemplo = sheet.createRow(rowNum++);

			rowEjemplo.createCell(0).setCellValue("2026");
			rowEjemplo.getCell(0).setCellStyle(styleTexto);

			rowEjemplo.createCell(1).setCellValue(20240002);
			rowEjemplo.getCell(1).setCellStyle(estilosDatos[1]);

			rowEjemplo.createCell(2).setCellValue(7);
			rowEjemplo.getCell(2).setCellStyle(estilosDatos[2]);

			rowEjemplo.createCell(3).setCellValue(1647);
			rowEjemplo.getCell(3).setCellStyle(estilosDatos[3]);

			rowEjemplo.createCell(4).setCellValue(1);
			rowEjemplo.getCell(4).setCellStyle(estilosDatos[4]);

			rowEjemplo.createCell(5).setCellValue(105560758226.00);
			rowEjemplo.getCell(5).setCellStyle(estilosDatos[5]);

			rowEjemplo.createCell(6).setCellValue("Nomina para la prestacion de los servicios de educación inicial...");
			rowEjemplo.getCell(6).setCellStyle(estilosDatos[6]);

			for (int i = 0; i < 7; i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream archivo = new ByteArrayOutputStream();
			workbook.write(archivo);
			workbook.close();

			ByteArrayInputStream finalExcel = new ByteArrayInputStream(archivo.toByteArray());

			setArchivoDescarga(JsfUtil.getArchivoDescarga(finalExcel, "Plantilla_Actualizacion_Actividades.xlsx"));

		} catch (Exception e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	// </CODIGO_DESARROLLADO>

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnCargarPlantilla en la vista
	 *
	 *
	 */
	public void oprimirbtnCargarPlantilla() {

		archivoDescarga = null;

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

			Sheet sheet = workbook.getSheet("Report");

			for (Row row : sheet) {

				if (row.getRowNum() == 0) {
					continue;
				}

			    if (filaVacia(row)) {
			        continue;
			    }

				capturaDatosExcelRubrosProyecto(row);
			}

			cadena = cadena + "')";
			
			String clobErrores = ejbBancoProyectoUno.actualizarActivXProyecto(compania, cadena, usuario, anio);

			if (clobErrores != null && !clobErrores.trim().isEmpty()) {

				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clobErrores);

				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LOG_ACTUALIZAR_ACTIVIDADESXPROYECTO.txt");
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

		if (contArchivocargarExcel.getArchivo() == null) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		} else {
			return true;
		}
	}


	private void capturaDatosExcelRubrosProyecto(Row row) {

	    for (int i = 0; i < 6; i++) {

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


//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
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

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
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
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
