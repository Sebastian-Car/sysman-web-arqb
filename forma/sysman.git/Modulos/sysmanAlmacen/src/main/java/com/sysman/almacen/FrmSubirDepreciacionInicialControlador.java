/*-
 * FrmSubirDepreciacionInicialControlador.java
 *
 * 1.0
 * 
 * 25/08/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import com.sysman.util.ContenedorArchivo;
/**
 *
 * @version 1.0, 25/08/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmSubirDepreciacionInicialControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
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
	private String modulo;
	private String cadena;
	private int contador;
	
	@EJB
	private EjbAlmacenCincoRemote cincoRemote;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmSubirDepreciacionInicialControlador
	 */
	public FrmSubirDepreciacionInicialControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		contArchivosubirPlantilla = new ContenedorArchivo();
		try {
			//2535
			numFormulario = GeneralCodigoFormaEnum.FRM_SUBIR_DEPRECIACION_INICIAL_CONTROLADOR.getCodigo();
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
	 * Metodo ejecutado al oprimir el boton Cargar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargar() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
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
				Sheet sheet = workbook.getSheet("Estructura");

				for (Row row : sheet) {
					capturaDatosExcel(row);
				}
				cadena = cadena + "')";
				cargarDatos();

			}
		} catch (IOException | NumberFormatException| NullPointerException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Descargar
	 * en la vista
	 *
	 *
	 */
	public void oprimirDescargar() {
	    //<CODIGO_DESARROLLADO>
	    archivoDescarga = null;

	    HSSFWorkbook workbook = new HSSFWorkbook();
	    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	        /* ==========================
	         *  HOJA 1: ESTRUCTURA
	         * ========================== */
	        HSSFSheet sheetEstructura = workbook.createSheet("Estructura");

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

	        Row headerRow = sheetEstructura.createRow(0);
	        String[] headers = {"CODIGO_ELEMENTO", "PLACA", "VIDA_UTIL", "DEPRECIACION_ACUMULADA", "OBSERVACIONES"};

	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	            sheetEstructura.autoSizeColumn(i);
	        }

	        /* ==========================
	         *  HOJA 2: INSTRUCTIVO
	         * ========================== */
	        HSSFSheet sheetInfo = workbook.createSheet("Instructivo");

	        // Título general
	        Row titulo = sheetInfo.createRow(0);
	        Cell tituloCell = titulo.createCell(0);
	        tituloCell.setCellValue("CARGUE DEPRECIACION ACUMULADA INICIAL");

	        CellStyle tituloStyle = workbook.createCellStyle();
	        Font tituloFont = workbook.createFont();
	        tituloFont.setBold(true);
	        tituloFont.setFontHeightInPoints((short) 12);
	        tituloStyle.setFont(tituloFont);
	        sheetInfo.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
	        tituloCell.setCellStyle(tituloStyle);

	        // Encabezado de columnas del instructivo
	        Row encabezado = sheetInfo.createRow(2);
	        String[] campos = {"CAMPO", "FORMATO", "TIPO", "OBSERVACIONES"};
	        for (int i = 0; i < campos.length; i++) {
	            Cell cell = encabezado.createCell(i);
	            cell.setCellValue(campos[i]);
	            cell.setCellStyle(headerStyle);
	            sheetInfo.autoSizeColumn(i);
	        }

	        // Datos informativos
	        Object[][] datos = {
	                {"CODIGO_ELEMENTO", "VARCHAR2(16 CHAR)", "Obligatorio",
	                        "Código del elemento definido en la ruta ALMACEN/ARCHIVOS/INVENTARIO. " +
	                        "Este codigo debe ser a 9 dígitos y marcado con la opción Tiene Movimiento?."},
	                {"PLACA", "NUMBER(15,0)", "Obligatorio",
	                        "Numero de placa o serie del bien que ya se encuentra creado en el aplicativo que no ha tenido un proceso de calculo depreciaciones o presenta valores en cero."},
	                {"VIDA_UTIL", "NUMBER(20,2)", "Opcional",
	                        "Meses de Vida util que requieren ser modificados en el bien, si la celda se encuentra vacia se mantiene la vida util del bien, de lo contrario se actualizará los meses asignados en la plantilla."},
	                {"DEPRECIACION_ACUMULADA", "NUMBER(20,2)", "Obligatorio",
	                        "Valor de la depreciación acumulada del activo, debe ser mayor a cero y menor o igual al valor de adquisición del bien."},
	                {"OBSERVACIONES", "VARCHAR2(255 CHAR)", "Obligatorio",
	                        "Se recomienda incluir en la observación el motivo por el cual se realiza la actualización de los valores del bien."}
	        };

	        int rowIdx = 3;
	        for (Object[] fila : datos) {
	            Row row = sheetInfo.createRow(rowIdx++);
	            for (int i = 0; i < fila.length; i++) {
	                Cell cell = row.createCell(i);
	                cell.setCellValue(fila[i].toString());
	                sheetInfo.autoSizeColumn(i);
	            }
	        }

	        // Ejemplo
	        Row ejemploTitulo = sheetInfo.createRow(rowIdx + 1);
	        Cell ejTitle = ejemploTitulo.createCell(0);
	        ejTitle.setCellValue("EJEMPLO");
	        ejTitle.setCellStyle(tituloStyle);

	        Row ejemploHeader = sheetInfo.createRow(rowIdx + 3);
	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = ejemploHeader.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	            sheetInfo.autoSizeColumn(i);
	        }

	        Row ejemplo = sheetInfo.createRow(rowIdx + 4);
	        Object[] ejemploDatos = {"201400060", "3140", "220", "12000000", "POR IMPLEMENTACIÓN"};
	        for (int i = 0; i < ejemploDatos.length; i++) {
	            Cell cell = ejemplo.createCell(i);
	            cell.setCellValue(ejemploDatos[i].toString());
	            sheetInfo.autoSizeColumn(i);
	        }

	        // Generar el archivo
	        workbook.write(out);
	        workbook.close();

	        archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
	                "Cargue_Depreciacion_Acumulada_Inicial" + ConstanteArchivo.EXCEL97.getExtension());

	        JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

	    } catch (IOException | JRException e) {
	        e.printStackTrace();
	    }
	    //</CODIGO_DESARROLLADO>
	}
	
	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cerrar
	 * en la vista
	 *
	 *
	 */
	public void oprimirCerrar() {
		//<CODIGO_DESARROLLADO>
		String[] campos = {};
		Object[] valores = {};
		SessionUtil.redireccionarFormularioModalFormulario(modulo, String.valueOf(GeneralCodigoFormaEnum.FRM_DEPRECIACION_ACUMULADA_INICIAL_CONTROLADOR.getCodigo()), campos, valores, true);
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
	
	private void capturaDatosExcel(Row row) {
	    if (row.getRowNum() > 0) {
	        for (int i = 0; i <= 4; i++) {
	            String val = row.getCell(i) + "";
	            if (val == null || val.isEmpty() || val.equals("null")) {
	                val = "0";
	            } else if (i == 0|| i == 1 || i == 2 || i == 3) {
	                val = NumberToTextConverter.toText(row.getCell(i).getNumericCellValue());
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
		String retorno = null;
		try {

			retorno = cincoRemote.cargarDepreciacionInicial(compania, cadena, SessionUtil.getUser().getCodigo());

			if(retorno != null && !retorno.isEmpty()) {

			archivoDescarga = JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(retorno),
					SysmanFunciones.concatenar("Inconsistencias", ConstanteArchivo.TXT.getExtension()));
			}
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			
			

		} catch ( IOException | JRException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
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
}
