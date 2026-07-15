/*-
 * FrmCargarTerceros.java
 *
 * 1.0
 * 
 * 14/06/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.BufferedWriter;
import com.sysman.beanbase.BeanBaseModal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.enums.FrmCargarTercerosUrlEnum;
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
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @version 1.0, 14/06/2023
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmCargarTerceros extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
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
	private List<Registro> listaTipoIdentificacion;
	private List<Registro> listaNaturaleza;
	private List<Registro> listaEntidad;
	private List<Registro> listaClase;
	private List<Registro> listaPais;
	private List<Registro> listaDepartamento;
	private List<Registro> listaCiudad;
	private List<Registro> listaRegimen;
	private List<Registro> listaResponsabilidadFiscal;

	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;
	
	/**
         * Variable que almacena la cantidad de registros agregados en la cadena
         */
        private int contador = 0;

	@EJB
	private EjbContabilidadCincoRemote ejbContabilidadCinco;

	/**
	 * Crea una nueva instancia de FrmCargarTerceros
	 */
	public FrmCargarTerceros() {
		super();
		compania = SessionUtil.getCompania();
		contArchivocargarExcel = new ContenedorArchivo();
		try {
			// 2214
			numFormulario = GeneralCodigoFormaEnum.FRM_CARGAR_TERCEROS.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(FrmCargarTerceros.class.getName()).log(Level.SEVERE, null, ex);
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

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 *
	 */
	public void oprimirCargar() {
		Workbook workbook = null;
		cadena = "TO_CLOB('";
                contador = 0;
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
				Sheet sheet = workbook.getSheet("TERCEROS");

				for (Row row : sheet) {
					if (!validarCelda(row.getCell(0))) {
						break;
					}

					capturaDatosExcel(row);
				}
				cadena = cadena + "')";
				cargarDatos();

			}
		} catch (IOException | NumberFormatException| NullPointerException e) {
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
	        for (int i = 0; i < 24; i++) {
	            String val = row.getCell(i) + "";
	            if (val == null || val.isEmpty() || val.equals("null")) {
	                val = "NoDato";
	            } else if (i == 2|| i == 13 || i == 18) {
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
			String parametro = (SysmanFunciones.esBdSqlServer()) ? cadena.replace("TO_CLOB(", "").replace(")", "")
					: cadena;

			retorno = ejbContabilidadCinco.cargarTerceros(compania, parametro, SessionUtil.getUser().getCodigo());

			retorno = retorno.replace("-", System.getProperty("line.separator"));

			archivoDescarga = JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(retorno),
					SysmanFunciones.concatenar("Reporte de Carga", ".txt"));
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | JRException | IOException e) {
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
	 * 
	 * @throws IOException
	 *
	 */
	public void oprimirCrear() throws IOException {
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			HSSFSheet excelSheet = workbook.createSheet("TERCEROS");

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
			
			Row row = excelSheet.createRow(0);
			Cell cell = row.createCell(0);

			cell = row.createCell(0);
			cell.setCellValue("TIPO IDENTIFICACION");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(0);

			cell = row.createCell(1);
			cell.setCellValue("NIT");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(1);

			cell = row.createCell(2);
			cell.setCellValue("DV");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(2);

			cell = row.createCell(3);
			cell.setCellValue("ENTIDAD OFICIAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(3);

			cell = row.createCell(4);
			cell.setCellValue("CODIGO EQUIVALENTEE");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(4);

			cell = row.createCell(5);
			cell.setCellValue("NATURALEZA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(5);

			cell = row.createCell(6);
			cell.setCellValue("CLASE");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(6);

			cell = row.createCell(7);
			cell.setCellValue("APELLIDO1");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(7);

			cell = row.createCell(8);
			cell.setCellValue("APELLIDO2");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(8);

			cell = row.createCell(9);
			cell.setCellValue("NOMBRE1 O RAZON SOCIAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(9);

			cell = row.createCell(10);
			cell.setCellValue("NOMBRE2");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(10);

			cell = row.createCell(11);
			cell.setCellValue("DIRECCION");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(11);

			cell = row.createCell(12);
			cell.setCellValue("DIRECCION FISCAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(12);

			cell = row.createCell(13);
			cell.setCellValue("CÓDIGO POSTAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(13);

			cell = row.createCell(14);
			cell.setCellValue("PAIS");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(14);

			cell = row.createCell(15);
			cell.setCellValue("DEPARTAMENTO");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(15);

			cell = row.createCell(16);
			cell.setCellValue("CIUDAD");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(16);

			cell = row.createCell(17);
			cell.setCellValue("REGIMEN");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(17);

			cell = row.createCell(18);
			cell.setCellValue("TELEFONOS");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(18);

			cell = row.createCell(19);
			cell.setCellValue("DIRECCION EMAIL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(19);

			cell = row.createCell(20);
			cell.setCellValue("PAIS EXPEDICION CÉDULA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(20);

			cell = row.createCell(21);
			cell.setCellValue("DEPARTAMENTO  EXPEDICIÓN CEDULA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(21);

			cell = row.createCell(22);
			cell.setCellValue("CIUDAD EXPEDICIÓN CÉDULA");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(22);

			cell = row.createCell(23);
			cell.setCellValue("RESPONSABILIDAD FISCAL");
			cell.setCellStyle(style);
			excelSheet.autoSizeColumn(23);

			Map<String, Object> param = new HashMap<>();
			param.put("COMPANIA", compania);
			param.put("TIPO", 1);

			listaTipoIdentificacion = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0002.getValue())
													.getUrl(),
											param));

			listaClase = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0003.getValue())
													.getUrl(),
											param));

			listaPais = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0004.getValue())
													.getUrl(),
											param));

			listaDepartamento = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0005.getValue())
													.getUrl(),
											param));

			listaCiudad = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0006.getValue())
													.getUrl(),
											param));

			listaRegimen = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0007.getValue())
													.getUrl(),
											param));

			listaResponsabilidadFiscal = RegistroConverter
					.toListRegistro(
							requestManager
									.getList(
											UrlServiceUtil.getInstance()
													.getUrlServiceByUrlByEnumID(
															FrmCargarTercerosUrlEnum.URL0008.getValue())
													.getUrl(),
											param));

			//
			listaNaturaleza = new ArrayList<>();

			// Crear objetos de la clase Registro y asignarles valores de campos
			Registro registro1 = new Registro();
			registro1.getCampos().put("CODIGO", "N");
			registro1.getCampos().put("NOMBRE", "Natural");

			Registro registro2 = new Registro();
			registro2.getCampos().put("CODIGO", "J");
			registro2.getCampos().put("NOMBRE", "Juridica");

			// Agregar los objetos Registro a la lista listaNaturaleza
			listaNaturaleza.add(registro1);
			listaNaturaleza.add(registro2);
//
			listaEntidad = new ArrayList<>();

			// Crear objetos de la clase Registro y asignarles valores de campos
			Registro registro3 = new Registro();
			registro3.getCampos().put("CODIGO", "0");
			registro3.getCampos().put("NOMBRE", "NO");

			Registro registro4 = new Registro();
			registro4.getCampos().put("CODIGO", "-1");
			registro4.getCampos().put("NOMBRE", "SI");

			// Agregar los objetos Registro a la lista listaNaturaleza
			listaEntidad.add(registro3);
			listaEntidad.add(registro4);

			if (listaTipoIdentificacion.isEmpty()) {
				System.out.println("Lista Compañia esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaTipoIdentificacion, 'A', 1, 10000, "TIPO_ID");
			}

			if (listaClase.isEmpty()) {
				System.out.println("Lista Clase esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaClase, 'G', 1, 10000, "CLASE");
			}
			if (listaPais.isEmpty()) {
				System.out.println("Lista Pais esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaPais, 'O', 1, 10000, "PAIS");
				addValidationToSheet2(workbook, excelSheet, listaPais, 'U', 1, 10000, "PAIS");
			}
			if (listaDepartamento.isEmpty()) {
				System.out.println("Lista Departamento esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaDepartamento, 'P', 1, 10000, "DEPARTAMENTO");
				addValidationToSheet2(workbook, excelSheet, listaDepartamento, 'V', 1, 10000, "DEPARTAMENTO");
			}
			if (listaCiudad.isEmpty()) {
				System.out.println("Lista Ciudad esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaCiudad, 'Q', 1, 10000, "CIUDAD");
				addValidationToSheet2(workbook, excelSheet, listaCiudad, 'W', 1, 10000, "CIUDAD");
			}
			if (listaRegimen.isEmpty()) {
				System.out.println("Lista Regimen esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaRegimen, 'R', 1, 10000, "REGIMEN");
			}
			if (listaResponsabilidadFiscal.isEmpty()) {
				System.out.println("Lista Responsabilidad Fiscal esta vacia");
			} else {
				addValidationToSheet2(workbook, excelSheet, listaResponsabilidadFiscal, 'X', 1, 10000,
						"RESPONSABILIDAD_FISCAL");
			}
			addValidationToSheet2(workbook, excelSheet, listaNaturaleza, 'F', 1, 10000, "NATURALEZA");
			addValidationToSheet2(workbook, excelSheet, listaEntidad, 'D', 1, 10000, "ENTIDAD_OFICIAL");
			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Cargar Terceros.xls");
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}
	}

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
				cell.setCellValue(option.getCampos().get("CODIGO").toString());
				cell1.setCellValue(option.getCampos().get("NOMBRE").toString());
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
	 * No se puede empezar con un numero
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

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>	

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

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 * 
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

	public List<Registro> getListaTipoIdentificacion() {
		return listaTipoIdentificacion;
	}

	public void ListaTipoIdentificacion(List<Registro> listaTipoIdentificacion) {
		this.listaTipoIdentificacion = listaTipoIdentificacion;
	}

	public List<Registro> getListaResponsabilidadFiscal() {
		return listaResponsabilidadFiscal;
	}

	public void setListaResponsabilidadFiscal(List<Registro> listaResponsabilidadFiscal) {
		this.listaResponsabilidadFiscal = listaResponsabilidadFiscal;
	}

	public List<Registro> getListaRegimen() {
		return listaRegimen;
	}

	public void setListaRegimen(List<Registro> listaRegimen) {
		this.listaRegimen = listaRegimen;
	}

	public List<Registro> getListaPais() {
		return listaPais;
	}

	public void setListaPais(List<Registro> listaPais) {
		this.listaPais = listaPais;
	}

	public List<Registro> getListaNaturaleza() {
		return listaNaturaleza;
	}

	public void setListaNaturaleza(List<Registro> listaNaturaleza) {
		this.listaNaturaleza = listaNaturaleza;
	}

	public List<Registro> getListaDepartamento() {
		return listaDepartamento;
	}

	public void setListaDepartamento(List<Registro> listaDepartamento) {
		this.listaDepartamento = listaDepartamento;
	}

	public List<Registro> getListaCiudad() {
		return listaCiudad;
	}

	public void setListaCiudad(List<Registro> listaCiudad) {
		this.listaCiudad = listaCiudad;
	}

	public List<Registro> getListaClase() {
		return listaClase;
	}

	public void setListaClase(List<Registro> listaClase) {
		this.listaClase = listaClase;
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

	public String getCadena() {
		return cadena;
	}

	public void setCadena(String cadena) {
		this.cadena = cadena;
	}

	public String getCompania() {
		return compania;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
