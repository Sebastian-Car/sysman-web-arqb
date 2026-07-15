/*-
 * FrmCargarDisponibilidades.java
 *
 * 1.0
 * 
 * 27/05/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.presupuesto.enums.FrmCargarDisponibilidadesUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @version 1.0, 27/05/2021
 * @author eorozco
 * 
 * @version 2.0, 09/06/2021
 * @author gfigueredo
 * Se crean las funciones para la generaciÃ³n de la plantilla de disponibilidades {@link #oprimirCrear()}
 * Se crean las funciones para el cargue de la plantilla de disponibilidades {@link #oprimirCargar()}
 * @see #oprimirCargar()
 * @see #oprimirCrear()
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class FrmCargarDisponibilidades extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>

	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * cargarExcel y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;
	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * SelecFile y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaSelecFile;
	private List<Registro> listaCompania;
	private List<Registro> listaFuente;
	private List<Registro> listaTipComprobante;
	private List<Registro> listaDependencia;
	private List<Registro> listaDestino;
	private List<Registro> listaSector;
	private List<Registro> listaPrograma;
	private List<Registro> listaSubprograma;
	private List<Registro> listaProducto;
	private List<Registro> listaBPIN;
	private List<Registro> listaCCPET;
	private List<Registro> listaCPC;
	private List<Registro> listaUnidadEjecutora;
	private List<Registro> listaFuenteFinanc;
	private List<Registro> listaCCPETRegalias;
	private List<Registro> listaPoliticaPublica;
	private List<Registro> listaDetalleSectorial;
	private List<Registro> listaReferencia;
	
	
	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;


	@EJB
	private EjbPresupuestoCuatroRemote ejbPresupuestoCuatro;

	/**
	 * Crea una nueva instancia de FrmCargarDisponibilidades
	 */
	public FrmCargarDisponibilidades() {
		super();
		compania = SessionUtil.getCompania();
		contArchivocargarExcel = new ContenedorArchivo();
		try {
			// 124
			numFormulario = GeneralCodigoFormaEnum.FRM_CARGAR_DISPONIBILIDADES.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(FrmCargarDisponibilidades.class.getName()).log(Level.SEVERE, null, ex);
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
		// <CARGAR_LISTA>
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
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargar() {

		Workbook workbook = null;
		cadena = "";
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
				Sheet sheet = workbook.getSheet("Plantilla");

				for (Row row : sheet) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}

					capturaDatosExcel(row);
				}
				cargarDatos();

			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

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

	private void capturaDatosExcel(Row row) {

		if (row.getRowNum() > 0) {

			for (int i = 0; i < 25; i++) {
				String val="";
				if(i==2 || i==8 || i==9  || i==11 ) {
					 val=NumberToTextConverter.toText(row.getCell(i).getNumericCellValue());
				}else {
					val = row.getCell(i) + "";
					if(val.equals("") || val == null || val.equals("null")) {
                        val =  "NoDato";
                    }
				}
				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
			}
			cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}

	}

	private void cargarDatos() {

		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.replace("TO_CLOB(", "").replace(")", "")
							: cadena;

					ejbPresupuestoCuatro.cargarComprobanteDetallePptal(compania, parametro, SessionUtil.getUser().getCodigo());

					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException e) {
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
	 * 
	 * Metodo ejecutado al oprimir el boton Crear en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 * 
	 * @throws IOException
	 *
	 */
	public void oprimirCrear() throws IOException {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			HSSFSheet excelSheet = workbook.createSheet("Plantilla");

			/* Propiedades letra encabezado */
			Font font = workbook.createFont();
			font.setFontName("Calibri");
			font.setBold(true);

			// TamaÃƒÂ±o de letra
			font.setFontHeightInPoints((short) 8);

			/* Estilo encabezado */
			CellStyle style = workbook.createCellStyle();
			style.setFont(font);

			Row row = excelSheet.createRow(1);
			Cell cell = row.createCell(0);

			row = excelSheet.createRow(0);
			cell = row.createCell(0);
			cell.setCellValue("COMPANIA");
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("TIPO COMPROBANTE");
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("NUMERO COMPROBANTE");
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("FECHA");
			excelSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("DESTINO");
			excelSheet.autoSizeColumn(4);

			cell = row.createCell(5);
			cell.setCellValue("DEPEND. SOLICITANTE");
			excelSheet.autoSizeColumn(5);

			cell = row.createCell(6);
			cell.setCellValue("DESCRIPCION");
			excelSheet.autoSizeColumn(6);

			cell = row.createCell(7);
			cell.setCellValue("CODIGO CUENTA");
			excelSheet.autoSizeColumn(7);

			cell = row.createCell(8);
			cell.setCellValue("VALOR DEBITO");
			excelSheet.autoSizeColumn(8);

			cell = row.createCell(9);
			cell.setCellValue("VALOR CREDITO");
			excelSheet.autoSizeColumn(9);

			cell = row.createCell(10);
			cell.setCellValue("FUENTE");
			excelSheet.autoSizeColumn(10);

			cell = row.createCell(11);
			cell.setCellValue("CONSECUTIVO");
			excelSheet.autoSizeColumn(11);

			cell = row.createCell(12);
			cell.setCellValue("REFERENCIA");
			excelSheet.autoSizeColumn(12);
			
			

			cell = row.createCell(13);
			cell.setCellValue("Sector");
			excelSheet.autoSizeColumn(13);	
			
			cell = row.createCell(14);
			cell.setCellValue("Programa");
			excelSheet.autoSizeColumn(14);	
			
			cell = row.createCell(15);
			cell.setCellValue("Subprograma");
			excelSheet.autoSizeColumn(15);	
			
			cell = row.createCell(16);
			cell.setCellValue("Código Producto");
			excelSheet.autoSizeColumn(16);	
			
			cell = row.createCell(17);
			cell.setCellValue("Código BPIN");
			excelSheet.autoSizeColumn(17);	
			
			cell = row.createCell(18);
			cell.setCellValue("Código CCPET");
			excelSheet.autoSizeColumn(18);	
			
			cell = row.createCell(19);
			cell.setCellValue("Código CPC");
			excelSheet.autoSizeColumn(19);	
			
			cell = row.createCell(20);
			cell.setCellValue("Código Unidad Ejecutora");
			excelSheet.autoSizeColumn(20);	

			cell = row.createCell(21);
			cell.setCellValue("Código Fuente");
			excelSheet.autoSizeColumn(21);
			
			cell = row.createCell(22);
			cell.setCellValue("Código CCPET Regalias");
			excelSheet.autoSizeColumn(22);
			
			cell = row.createCell(23);
			cell.setCellValue("Política Pública");
			excelSheet.autoSizeColumn(23);
			
			cell = row.createCell(24);
			cell.setCellValue("Detalle Sectorial");
			excelSheet.autoSizeColumn(24);
			
			

			Map<String, Object> param = new HashMap<>();

			listaCompania = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0001.getValue())
									.getUrl(),
									param));
			param.put("COMPANIA", compania);
			listaFuente = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0002.getValue())
									.getUrl(),
									param));
			listaTipComprobante = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0003.getValue())
									.getUrl(),
									param));
			listaDependencia = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0004.getValue())
									.getUrl(),
									param));
			listaDestino = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0005.getValue())
									.getUrl(),
									param));
			
			listaSector = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0006.getValue())
									.getUrl(),
									param));
			
			listaPrograma = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0007.getValue())
									.getUrl(),
									param));
			
			listaSubprograma = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0009.getValue())
									.getUrl(),
									param));
			
			listaProducto = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0008.getValue())
									.getUrl(),
									param));
			
			listaBPIN = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0010.getValue())
									.getUrl(),
									param));
			
			listaCCPET = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0011.getValue())
									.getUrl(),
									param));
			
			listaCPC = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0012.getValue())
									.getUrl(),
									param));
			
			listaUnidadEjecutora = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0013.getValue())
									.getUrl(),
									param));
			
			listaFuenteFinanc = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0014.getValue())
									.getUrl(),
									param));
			
			listaCCPETRegalias = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0015.getValue())
									.getUrl(),
									param));
			
			listaPoliticaPublica = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0016.getValue())
									.getUrl(),
									param));
			
			listaDetalleSectorial = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCargarDisponibilidadesUrlEnum.URL0017.getValue())
									.getUrl(),
									param));
			
			listaReferencia = RegistroConverter
                                        .toListRegistro(
                                                        requestManager.getList(
                                                                        UrlServiceUtil.getInstance()
                                                                        .getUrlServiceByUrlByEnumID(
                                                                                        FrmCargarDisponibilidadesUrlEnum.URL0018.getValue())
                                                                        .getUrl(),
                                                                        param));
			
			// demo lista desplegable separada
			
			if (listaCompania.isEmpty()) {
				System.out.println("Lista Compañia esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaCompania, 'A', 1, 10000, "Compania");
				}
			if (listaFuente.isEmpty()) {
				System.out.println("Lista Fuente esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaFuente, 'K', 1, 10000, "Fuente");
				}
			if (listaTipComprobante.isEmpty()) {
				System.out.println("Lista Tipo Comprobante esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaTipComprobante, 'B', 1, 10000, "TipoComprobante");
				}
			if (listaDependencia.isEmpty()) {
				System.out.println("Lista Dependencia esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaDependencia, 'F', 1, 10000, "Dependencia");
				}
			if (listaDestino.isEmpty()) {
				System.out.println("Lista Destino esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaDestino, 'E', 1, 10000, "Destino");
				}
			if (listaSector.isEmpty()) {
				System.out.println("Lista Sector esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaSector, 'N', 1, 10000, "Sector");
				}
			if (listaPrograma.isEmpty()) {
				System.out.println("Lista Programa esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaPrograma, 'O', 1, 10000, "Programa");
				}
			if (listaSubprograma.isEmpty()) {
				System.out.println("Lista SubPrograma esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaSubprograma, 'P', 1, 10000, "Subprograma");
				}
			if (listaProducto.isEmpty()) {
				System.out.println("Lista CodigoProducto esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaProducto, 'Q', 1, 10000, "CodigoProducto");
				}
			if (listaBPIN.isEmpty()) {
				System.out.println("Lista CodigoBPIN esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaBPIN, 'R', 1, 10000, "CodigoBPIN");
				}
			if (listaCCPET.isEmpty()) {
				System.out.println("Lista CodigoCCPET esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaCCPET, 'S', 1, 10000, "CodigoCCPET");
				}
			if (listaCPC.isEmpty()) {
				System.out.println("Lista CodigoCPC esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaCPC, 'T', 1, 10000, "CodigoCPC");
				}
			if (listaUnidadEjecutora.isEmpty()) {
				System.out.println("Lista UnidadEjecutora esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaUnidadEjecutora, 'U', 1, 10000,"CodigoUnidadEjecutora");
				}
			if (listaFuenteFinanc.isEmpty()) {
				System.out.println("Lista Fuente de Financiacion esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaFuenteFinanc, 'V', 1, 10000, "CodigoFuenteF");
				}
			if (listaCCPETRegalias.isEmpty()) {
				System.out.println("Lista Codigo CCPET Regalias esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaCCPETRegalias, 'W', 1, 10000, "CodigoCCPETRegalias");
				}

			if (listaPoliticaPublica.isEmpty()) {
				System.out.println("Lista Politica Publica esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaPoliticaPublica, 'X', 1, 10000, "PoliticaPublica");
				}

			if (listaDetalleSectorial.isEmpty()) {
				System.out.println("Lista DetalleSectorial esta vacia");
				} else {
					addValidationToSheet2(workbook, excelSheet, listaDetalleSectorial, 'Y', 1, 10000, "DetalleSectorial");

				}
			
			if (listaReferencia.isEmpty()) {
                            System.out.println("Lista Referencia esta vacia");
                            } else {
                                    addValidationToSheet2(workbook, excelSheet, listaReferencia, 'M', 1, 10000, "Referencia");

                            }

			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Disponibilidades.xls");

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}

		// </CODIGO_DESARROLLADO>
	}
	// </METODOS_BOTONES>

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
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";

		int rowIndex = 0;
		for (Registro option : options) {
			int columnIndex = 0;
			Row row = optionsSheet.createRow(rowIndex++);
			Cell cell = row.createCell(columnIndex++);
			Cell cell1 = row.createCell(columnIndex);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());
			cell1.setCellValue(option.getCampos().get("NOMBRE").toString());
		}

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
	 * No se puede empezar con un nÃƒÂºmero
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

	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna el objeto contArchivoSelecFile
	 * 
	 * @return contArchivoSelecFile
	 */
	public UploadedFile getArchivoCargaSelecFile() {
		return archivoCargaSelecFile;
	}

	/**
	 * Asigna el objeto contArchivoSelecFile
	 * 
	 * @param contArchivoSelecFile Variable a asignar en contArchivoSelecFile
	 */
	public void setArchivoCargaSelecFile(UploadedFile archivoCargaSelecFile) {
		this.archivoCargaSelecFile = archivoCargaSelecFile;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
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

	public List<Registro> getListaCompania() {
		return listaCompania;
	}

	public void setListaCompania(List<Registro> listaCompania) {
		this.listaCompania = listaCompania;
	}

	public List<Registro> getListaFuente() {
		return listaFuente;
	}

	public void setListaFuente(List<Registro> listaFuente) {
		this.listaFuente = listaFuente;
	}

	public List<Registro> getListaTipComprobante() {
		return listaTipComprobante;
	}

	public void setListaTipComprobante(List<Registro> listaTipComprobante) {
		this.listaTipComprobante = listaTipComprobante;
	}

	public List<Registro> getListaDependencia() {
		return listaDependencia;
	}

	public void setListaDependencia(List<Registro> listaDependencia) {
		this.listaDependencia = listaDependencia;
	}

	public List<Registro> getListaDestino() {
		return listaDestino;
	}

	public void setListaDestino(List<Registro> listaDestino) {
		this.listaDestino = listaDestino;
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

}
