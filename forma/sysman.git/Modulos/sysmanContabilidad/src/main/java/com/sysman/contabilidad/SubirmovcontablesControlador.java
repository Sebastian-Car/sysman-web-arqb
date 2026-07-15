/*-
 * SubirmovcontablesControlador.java
 *
 * 1.0
 * 
 * 28/05/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.StreamedContent;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.ejb.impl.EjbContabilidadSeis;
import com.sysman.contabilidad.enums.SubirmovcontablesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/05/2024
 * @author User
 */
@ManagedBean
@ViewScoped
public class  SubirmovcontablesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;

	private StreamedContent archivoDescarga;

	private Boolean causaciones;

	private Boolean documentoSoporte;

	private Boolean egresos;

	private final String modulo;

	private ContenedorArchivo contArchivocargarExcel;
	
	private int proceso = 0;


	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	private List<Registro> listaCompania;
	private List<Registro> listaCentroCosto;
	private List<Registro> listaFuente;
	private List<Registro> listaAuxiliar;
	private List<Registro> listaReferencia;
	private List<Registro> listaTipoRetenciones;
	private List<Registro> listaRetenciones;

	private String cadenaHeader;

	private int contador;

	private String cadenaDetalle;
	
	private String cadenaCUenta = "";
	private String companiaArchivo ;
	private int ano ;
	
	
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	
	@EJB
	private EjbContabilidadSeisRemote ejbContabilidadSeis;
	
	/**
	 * Crea una nueva instancia de SubirmovcontablesControlador
	 */
	public SubirmovcontablesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		setContArchivocargarExcel(new ContenedorArchivo());

		try {
			numFormulario=GeneralCodigoFormaEnum.FRM_SUBIR_MOV_CONTABLES.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
			setContArchivocargarExcel(new ContenedorArchivo());
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
		causaciones = true;
	}

	public void oprimirCargar() {
		Workbook workbook = null;
		cadenaHeader = "TO_CLOB('";
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
				Sheet sheet = workbook.getSheet("ENCABEZADO");
				contador = 0;

				for (Row row : sheet) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}

					capturaDatosExcelHeader(row);
				}
				cadenaHeader = cadenaHeader + "')";


				Sheet sheet1 = workbook.getSheet("DETALLE");
				cadenaDetalle = "TO_CLOB('";
				contador = 0;
				cadenaCUenta = "";
                List<String>validacionCuentaConAfectaciones = new ArrayList<>();

                
				for (Row row : sheet1) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}
					
					capturaDatosExcelDetalle(row,validacionCuentaConAfectaciones);
					
				}
				
				cadenaDetalle = cadenaDetalle + "')";
				
				if(cadenaCUenta.length()>0) {
					archivoDescarga = null;
					if (cadenaCUenta.endsWith(",")) {
						cadenaCUenta = cadenaCUenta.substring(0, cadenaCUenta.length() - 1);
						
						try {
							String validacion = ejbContabilidadSeis.validarcuentas(companiaArchivo, ano,cadenaCUenta);
							if (validacion != null && !validacion.isEmpty()) {
								if (validacion.endsWith(",")) {
									validacion = validacion.substring(0, validacion.length() - 1);
								}
								String Mi_respuesta =  "Las siguientes cuentas tienen datos de afectados(a�o,tipo,cte,cons) y no es correcto. Por favor quite esa informaci�n de la hoja detalle e intente de nuevo:"
										              + System.lineSeparator()
										              + validacion;
								
								ByteArrayInputStream streamTexto;
								streamTexto = JsfUtil.serializarPlano(Mi_respuesta);
								
								archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
									                "DATOSERROR.txt");
								
		                        return;
					           
							}
							
						} catch (SystemException | JRException  e) {
							
							logger.error(e.getMessage(), e);
							JsfUtil.agregarMensajeError(e.getMessage());
						}
			
					}
						
					
				}
				//cadenaHeader="";
				//cadenaDetalle= "";
				
				cargarDatos();

			}
		} catch (IOException e) {
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

		if (contArchivocargarExcel.getArchivo() == null) {
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

		return !celda.getStringCellValue().isEmpty();
	}

	private void capturaDatosExcelHeader(Row row) {
		int cantColumns = 0;
		if (row.getRowNum() > 0) {
			
			if(causaciones) {
				proceso = 1;
				cantColumns = 12;
			}else if(documentoSoporte) {
				proceso = 2;
				cantColumns = 15;
			}else if(egresos) {
				proceso = 3;
				cantColumns = 14;
			}
			
			for (int i = 0; i < cantColumns; i++) {
				String val="";

				val = row.getCell(i) + "";
				if(val.equals("") || val == null || val.equals("null")) {
					val =  "NoDato";
				}

				contador = contador + val.toString().length();
				
				if  (i == 0) {
					companiaArchivo  = val; 
				}
				
				if  (i == 3) {
					 String fechaStr = val; // Fecha extra�da de Excel
				        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				        LocalDate fecha = LocalDate.parse(fechaStr, formatter);
				        
				        int year = fecha.getYear(); // Extraer el a�o
				        ano  = year; 
				}
				
				if (contador >= 3000) {
					cadenaHeader = cadenaHeader + "') || TO_CLOB('";
					contador = 0; 
				}

				cadenaHeader = cadenaHeader + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadenaHeader = cadenaHeader + SysmanConstantes.SEPARADOR_REG;
		}

	}

	private void capturaDatosExcelDetalle(Row row,List<String> validacionCuentaConAfectaciones) {
		String numeroCuenta = "";
        StringBuilder ValEvaluar = new StringBuilder();
        
		int cantColumns = 0;
		if (row.getRowNum() > 0) {

			if(causaciones) {
				proceso = 1;
				cantColumns = 19;
			}else if(documentoSoporte) {
				proceso = 2;
				cantColumns = 20;
			}else if(egresos) {
				proceso = 3;
				cantColumns = 25;
			}
			
			for (int i = 0; i < cantColumns; i++) {
				String val="";
				
				
				
				val = row.getCell(i) + "";
				if(val.equals("") || val == null || val.equals("null")) {
					val =  "NoDato";
				}

				contador = contador + val.toString().length();

				if (contador >= 3000) {
					cadenaDetalle = cadenaDetalle + "') || TO_CLOB('";
					contador = 0; 
				}

				cadenaDetalle = cadenaDetalle + val + SysmanConstantes.SEPARADOR_COL;
				 if(egresos) {
						 
						 if (i == 4) {		
							 numeroCuenta = val;
							 
						 }	 
						 if(i == 19 || i == 20 || i == 21 ||i == 22) {
							 if(val != null && !val.isEmpty()  && !val.equalsIgnoreCase("NoDato")) {
								 ValEvaluar.append(val).append(",");
							 }
						 }
						if(i==23 && ValEvaluar.length()> 0) {
							cadenaCUenta = 	cadenaCUenta + numeroCuenta + "," ;
							
							
						}
		 								 
				 }
				
			}
			
			cadenaDetalle = cadenaDetalle + SysmanConstantes.SEPARADOR_REG;
		}

	}

	private void cargarDatos() {

		try {
			String parametroHeader = (SysmanFunciones.esBdSqlServer())
					? cadenaHeader.replace("TO_CLOB(", "").replace(")", "")
							: cadenaHeader;

					String parametroDetalle = (SysmanFunciones.esBdSqlServer())
							? cadenaDetalle.replace("TO_CLOB(", "").replace(")", "")
									: cadenaDetalle;

							ejbContabilidadSeis.cargarmovcontables(compania, parametroHeader, parametroDetalle, proceso, SessionUtil.getUser().getCodigo());

							JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}


	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Crear
	 * en la vista
	 *
	 *
	 */
	public void oprimirCrear() {
		setArchivoDescarga(null);   
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {


			HSSFSheet excelSheet = workbook.createSheet("ENCABEZADO");

			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Arial");
			font.setBold(true);

			// TamaÃ±o de letra
			font.setFontHeightInPoints((short) 10);

			/* Estilo encabezado */
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);


			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			Row row = excelSheet.createRow(1);
			Cell cell = row.createCell(0);

			row = excelSheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("COMPANIA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("TIPO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("NUMERO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("FECHA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("FECHA VENCIMIENTO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(4);

			cell = row.createCell(5);
			cell.setCellValue("NIT TERCERO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(5);

			cell = row.createCell(6);
			cell.setCellValue("SUCURSAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(6);

			cell = row.createCell(7);
			cell.setCellValue("DESCRIPCION");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(7);

			cell = row.createCell(8);
			cell.setCellValue("TEXTO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(8);

			cell = row.createCell(9);
			cell.setCellValue("TIPO CONTRATO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(9);

			cell = row.createCell(10);
			cell.setCellValue("NUMERO CONTRATO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(10);

			cell = row.createCell(11);
			cell.setCellValue("FACTURA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(11);			

			if(documentoSoporte) {	

				cell = row.createCell(12);
				cell.setCellValue("PREFIJO DIAN");
				cell.setCellStyle(style);
				excelSheet.autoSizeColumn(12);	

				cell = row.createCell(13);
				cell.setCellValue("CONSECUTIVO DIAN");
				cell.setCellStyle(style);
				excelSheet.autoSizeColumn(13);	

				cell = row.createCell(14);
				cell.setCellValue("RESOLUCION DIAN");
				cell.setCellStyle(style);
				excelSheet.autoSizeColumn(14);

			}else if(egresos) {

				cell = row.createCell(12);
				cell.setCellValue("CUENTA BANCO");
				cell.setCellStyle(style);
				excelSheet.autoSizeColumn(12);	

				cell = row.createCell(13);
				cell.setCellValue("ENVIAR A PLANO");
				cell.setCellStyle(style);
				excelSheet.autoSizeColumn(13);				
			}




			HSSFSheet excelSheet2 = workbook.createSheet("DETALLE");

			row = excelSheet2.createRow(1);
			cell = row.createCell(0);

			row = excelSheet2.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("COMPANIA");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("TIPO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("NUMERO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("FECHA");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("CUENTA");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(4);

			cell = row.createCell(5);
			cell.setCellValue("VALOR DEBITO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(5);

			cell = row.createCell(6);
			cell.setCellValue("VALOR CREDITO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(6);

			cell = row.createCell(7);
			cell.setCellValue("NIT TERCERO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(7);

			cell = row.createCell(8);
			cell.setCellValue("SUCURSAL");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(8);

			cell = row.createCell(9);
			cell.setCellValue("TIPO CONTRATO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(9);

			cell = row.createCell(10);
			cell.setCellValue("NUMERO CONTRATO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(10);

			cell = row.createCell(11);
			cell.setCellValue("DESCRIPCION");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(11);

			cell = row.createCell(12);
			cell.setCellValue("CENTRO DE COSTO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(12);

			cell = row.createCell(13);
			cell.setCellValue("FUENTE DE RECURSO");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(13);	

			cell = row.createCell(14);
			cell.setCellValue("AUXILIAR GENERAL");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(14);	

			cell = row.createCell(15);
			cell.setCellValue("REFERENCIA");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(15);	

			cell = row.createCell(16);
			cell.setCellValue("TIPO RETENCION");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(16);

			cell = row.createCell(17);
			cell.setCellValue("CODIGO RETENCION");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(17);

			cell = row.createCell(18);
			cell.setCellValue("BASE GRAVABLE");
			cell.setCellStyle(style);
			excelSheet2.autoSizeColumn(18);	

			if(documentoSoporte) {

				cell = row.createCell(19);
				cell.setCellValue("CODIGO CUDS");
				cell.setCellStyle(style);
				excelSheet2.autoSizeColumn(19);

			}else if(egresos) {

				cell = row.createCell(19);
				cell.setCellValue("A�O AFECTADO");
				cell.setCellStyle(style);
				excelSheet2.autoSizeColumn(19);

				cell = row.createCell(20);
				cell.setCellValue("TIPO AFECTADO");
				cell.setCellStyle(style);
				excelSheet2.autoSizeColumn(20);

				cell = row.createCell(21);
				cell.setCellValue("CTE AFECTADO");
				cell.setCellStyle(style);
				excelSheet2.autoSizeColumn(21);

				cell = row.createCell(22);
				cell.setCellValue("CONS AFECTADO");
				cell.setCellStyle(style);
				excelSheet2.autoSizeColumn(22);

			}

			Map<String, Object> param = new HashMap<>();

			listaCompania = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL59003.getValue())
									.getUrl(),
									param));
			param.put("COMPANIA", compania);
			listaFuente = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL34007.getValue())
									.getUrl(),
									param));
			listaRetenciones = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL12009.getValue())
									.getUrl(),
									param));
			listaTipoRetenciones = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL8005.getValue())
									.getUrl(),
									param));
			listaReferencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL13049.getValue())
									.getUrl(),
									param));

			param.put("ANO", SysmanFunciones.ano(new Date()));
			listaCentroCosto = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL20049.getValue())
									.getUrl(),
									param));
			listaAuxiliar = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											SubirmovcontablesControladorUrlEnum.URL23060.getValue())
									.getUrl(),
									param));			



			// demo lista desplegable separada

			if (listaCompania.isEmpty()) {
				System.out.println("Lista Compa�ia esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, excelSheet2, listaCompania, 'A', 1, 10000, "Compania");
			}
			if (listaCentroCosto.isEmpty()) {
				System.out.println("Lista Centro Costo esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet2, null, listaCentroCosto, 'M', 1, 10000, "CentroCosto");
			}
			if (listaFuente.isEmpty()) {
				System.out.println("Lista Fuente esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet2, null, listaFuente, 'N', 1, 10000, "Fuente");
			}
			if (listaAuxiliar.isEmpty()) {
				System.out.println("Lista Auxiliar esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet2, null, listaAuxiliar, 'O', 1, 10000, "Auxiliar");
			}
			if (listaReferencia.isEmpty()) {
				System.out.println("Lista Referencia esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet2, null, listaReferencia, 'P', 1, 10000, "Referencia");
			}
			if (listaTipoRetenciones.isEmpty()) {
				System.out.println("Lista Tipo Retenciones esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet2, null, listaTipoRetenciones, 'Q', 1, 10000, "TipoRetenciones");
			}
			if (listaRetenciones.isEmpty()) {
				System.out.println("Lista Retenciones esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet2, null, listaRetenciones, 'R', 1, 10000, "Retenciones");
			}


			workbook.write(out);

			String nombreArchivo = " ";

			if(causaciones) {
				nombreArchivo = "Movimientos Contables Causaciones.xls";
			}else if(documentoSoporte) {
				nombreArchivo = "Movimientos Contables Documento Soporte.xls";
			}else if(egresos) {
				nombreArchivo = "Movimientos Contables Egresos.xls";
			}

			setArchivoDescarga(JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					nombreArchivo));

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, Sheet targetSheetFin, List<Registro> options, char column,
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
				cell.setCellValue(option.getCampos().get("CODIGO").toString());
				cell1.setCellValue(option.getCampos().get("NOMBRE").toString());
			}

			// Ajustar el ancho de las columnas
			optionsSheet.autoSizeColumn(0);
			optionsSheet.autoSizeColumn(1);
		}

		createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
		optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, (int) column - 'A',
				(int) column - 'A');
		targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
		if (targetSheetFin != null) {
			targetSheetFin.addValidationData(new HSSFDataValidation(regions, constraint));			
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

	public void cambiarCkCausaciones() {
		if (causaciones) {
			documentoSoporte = false;
			egresos = false;
		}
	}
	/**
	 * Metodo ejecutado al cambiar el control CkDocSoporte
	 * 
	 * 
	 */
	public void cambiarCkDocSoporte() {
		if (documentoSoporte) {
			causaciones = false;
			egresos = false;
		}
	}
	/**
	 * Metodo ejecutado al cambiar el control CkEgresos
	 * 
	 * 
	 */
	public void cambiarCkEgresos() {
		if (egresos) {
			causaciones = false;
			documentoSoporte = false;
		}
	}

	/**
	 * @return the documentoSoporte
	 */
	public Boolean getDocumentoSoporte() {
		return documentoSoporte;
	}
	/**
	 * @param documentoSoporte the documentoSoporte to set
	 */
	public void setDocumentoSoporte(Boolean documentoSoporte) {
		this.documentoSoporte = documentoSoporte;
	}
	/**
	 * @return the egresos
	 */
	public Boolean getEgresos() {
		return egresos;
	}
	/**
	 * @param egresos the egresos to set
	 */
	public void setEgresos(Boolean egresos) {
		this.egresos = egresos;
	}
	/**
	 * @return the causaciones
	 */
	public Boolean getCausaciones() {
		return causaciones;
	}
	/**
	 * @param causaciones the causaciones to set
	 */
	public void setCausaciones(Boolean causaciones) {
		this.causaciones = causaciones;
	}
	/**
	 * @return the listaTipoRetenciones
	 */
	public List<Registro> getListaTipoRetenciones() {
		return listaTipoRetenciones;
	}
	/**
	 * @param listaTipoRetenciones the listaTipoRetenciones to set
	 */
	public void setListaTipoRetenciones(List<Registro> listaTipoRetenciones) {
		this.listaTipoRetenciones = listaTipoRetenciones;
	}
	/**
	 * @return the listaFuente
	 */
	public List<Registro> getListaFuente() {
		return listaFuente;
	}
	/**
	 * @param listaFuente the listaFuente to set
	 */
	public void setListaFuente(List<Registro> listaFuente) {
		this.listaFuente = listaFuente;
	}
	/**
	 * @return the listaRetenciones
	 */
	public List<Registro> getListaRetenciones() {
		return listaRetenciones;
	}
	/**
	 * @param listaRetenciones the listaRetenciones to set
	 */
	public void setListaRetenciones(List<Registro> listaRetenciones) {
		this.listaRetenciones = listaRetenciones;
	}
	/**
	 * @return the listaCentroCosto
	 */
	public List<Registro> getListaCentroCosto() {
		return listaCentroCosto;
	}
	/**
	 * @param listaCentroCosto the listaCentroCosto to set
	 */
	public void setListaCentroCosto(List<Registro> listaCentroCosto) {
		this.listaCentroCosto = listaCentroCosto;
	}
	/**
	 * @return the listaCompania
	 */
	public List<Registro> getListaCompania() {
		return listaCompania;
	}
	/**
	 * @param listaCompania the listaCompania to set
	 */
	public void setListaCompania(List<Registro> listaCompania) {
		this.listaCompania = listaCompania;
	}
	/**
	 * @return the listaAuxiliar
	 */
	public List<Registro> getListaAuxiliar() {
		return listaAuxiliar;
	}
	/**
	 * @param listaAuxiliar the listaAuxiliar to set
	 */
	public void setListaAuxiliar(List<Registro> listaAuxiliar) {
		this.listaAuxiliar = listaAuxiliar;
	}
	/**
	 * @return the listaReferencia
	 */
	public List<Registro> getListaReferencia() {
		return listaReferencia;
	}
	/**
	 * @param listaReferencia the listaReferencia to set
	 */
	public void setListaReferencia(List<Registro> listaReferencia) {
		this.listaReferencia = listaReferencia;
	}
	/**
	 * @return the archivoDescarga
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * @param archivoDescarga the archivoDescarga to set
	 */
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	/**
	 * @return the contArchivocargarExcel
	 */
	public ContenedorArchivo getContArchivocargarExcel() {
		return contArchivocargarExcel;
	}
	/**
	 * @param contArchivocargarExcel the contArchivocargarExcel to set
	 */
	public void setContArchivocargarExcel(ContenedorArchivo contArchivocargarExcel) {
		this.contArchivocargarExcel = contArchivocargarExcel;
	}
	/**
	 * @return the proceso
	 */
	public int getProceso() {
		return proceso;
	}
	/**
	 * @param proceso the proceso to set
	 */
	public void setProceso(int proceso) {
		this.proceso = proceso;
	}



}