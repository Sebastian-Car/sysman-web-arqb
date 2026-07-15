/*-
 * FrmSubirClasificadores.java
 *
 * 1.0
 * 
 * 12/01/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.presupuesto.enums.FrmCargarDisponibilidadesUrlEnum;
import com.sysman.presupuesto.enums.FrmSubirClasificadoresUrlEnum;
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
 * @version 1.0, 12/01/2022
 * @author cperez2
 * Se crean las funciones para la generaci�n de la plantilla de clasificadores {@link #oprimirCrear()}
 * Se crean las funciones para el cargue de la plantilla de clasificadores {@link #oprimirCargar()}
 * @see #oprimirCargar()
 * @see #oprimirCrear()
 * 
 * 
 */
@ManagedBean
@ViewScoped
public class  FrmSubirClasificadores extends BeanBaseModal{
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
	 * archivos cargarExcel y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivocargarExcel;
	/**
	 * Este atributo se usa como auxiliar del componente referencia de archivos
	 * SelecFile y funciona como contenedor del archivo que se desea cargar
	 */
	private UploadedFile archivoCargaSelecFile;
	private List<Registro> listaCompania;
	private List<Registro> listaClasificador;
	private List<Registro> listaclasificadorAsubir;
	private Registro registro;
	/**
	 * Variable que almacena la informacion del excel
	 */
	private String cadena;
	private long contador;
	private String claseClasificador= "";
	private String ano;


	@EJB
	private EjbPresupuestoCuatroRemote ejbPresupuestoCuatro;
	

	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmSubirClasificadores
	 */
	public FrmSubirClasificadores() {
		super();
		compania = SessionUtil.getCompania();
		ano = String.valueOf(SysmanFunciones.ano(
				new Date()));
		contArchivocargarExcel = new ContenedorArchivo();
		registro = new Registro(new HashMap<String, Object>());
		try {
			numFormulario= GeneralCodigoFormaEnum.SUBIRCLASIFICADOR_CONTROLADOR.getCodigo(); //2333
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(FrmSubirClasificadores.class.getName()).log(Level.SEVERE, null, ex);
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
		cargarListaclasificadorAsubir();
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
	public void cargarListaclasificadorAsubir() {
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try {
            listaclasificadorAsubir = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		FrmSubirClasificadoresUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Cargar en la vista
	 *
	 * DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargar() {
		registro.getCampos();
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
				Map<String, Object> param = new HashMap<>();
				param.put("COMPANIA", compania);
				param.put("ANO", String.valueOf(SysmanFunciones.ano(
						new Date())));
				listaClasificador = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												FrmSubirClasificadoresUrlEnum.URL0002.getValue())
										.getUrl(),
										param));
				Sheet sheet =  null;
				long i = 0;
				for (Registro option : listaClasificador) {
					
					if( registro.getCampos().get("CODIGO") == null || registro.getCampos().get("CODIGO").toString().equals(option.getCampos().get("CODIGO").toString())) {
						sheet = workbook.getSheet(option.getCampos().get("CODIGO").toString() +" " + option.getCampos().get("NOMBRE").toString());
						claseClasificador =  option.getCampos().get("CODIGO").toString();
						i = 1;
						contador = 0;
						for (Row row : sheet) {
							contador++;
						}
						for (Row row : sheet) {
	
							if (!validarCelda(row.getCell(0))) {
								break;
							}
							//carga cada 50 registros cuando  la cantidad de los mismos son mas  autor:cperez
							capturaDatosExcel(row);
							if(50 * (i/50)  == i || (i >= contador && !"".equals(cadena)) ) {
								if (!cadena.equals("")) { 
									cargarDatos();
									cadena =  "";
								}
							}
	
							i =  i +1;
						}
					}
				}
				Sheet sheetClasHijo = workbook.getSheet("CLASIFICADORES HIJO");
				cadena = "";
				i = 1;
				contador = 0;
				for (Row row : sheetClasHijo) {
					contador++;
				}
				for (Row row : sheetClasHijo) {
					
					if (!validarCelda(row.getCell(0))) {
						break;
					}
                    //carga cada 50 registros cuando  la cantidad de los mismos son mas  autor:cperez
					capturaDatosExcel(row, sheetClasHijo);
					if(50 * (i/50)  == i || (i >= contador && !"".equals(cadena)) ) {
						if (!cadena.equals("")) { 
							cargarDatosClasificadorHijo();
							cadena =  "";
						}
					}

					i =  i +1;
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

			for (int i = 0; i < row.getLastCellNum(); i++) {
				String val="";
				if(i == 1 || i == 4 || i == 5 || i ==6 || i ==8 ) {
					val=String.valueOf(NumberToTextConverter.toText(row.getCell(i).getNumericCellValue()));
				}else {
					val = row.getCell(i) + "";
				}
				cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
				if(i==1) {//cargar el claseclasificador 
					cadena = cadena + claseClasificador + SysmanConstantes.SEPARADOR_COL;
				}
			}
			cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());

			cadena = cadena + SysmanConstantes.SEPARADOR_REG;
		}

	}
	private void capturaDatosExcel(Row row, Sheet sheet) {//7719546 pendeinte de cuipo -- 7719857_nomina contabilizar
		if(sheet != null) {
			//cadena = "";
			if (row.getRowNum() > 0) {
	
				for (int i = 0; i < row.getLastCellNum(); i++) {
					String val="";
					if(i>1 && i < 6) {
						val=row.getCell(i).toString()+row.getCell(i+1).toString();
						cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
						i++;
					}else if(i < 6) {
						val = row.getCell(i).toString() + "";
						cadena = cadena + val + SysmanConstantes.SEPARADOR_COL;
					}
					
				}
				cadena = cadena.substring(0, cadena.length() - SysmanConstantes.SEPARADOR_COL.length());
	
				cadena = cadena + SysmanConstantes.SEPARADOR_REG;
			}
		}

	}
	private void cargarDatos() {

		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.replace("TO_CLOB(", "").replace(")", "")
							: cadena;

					ejbPresupuestoCuatro.cargarTipoClasificador(compania, parametro, SessionUtil.getUser().getCodigo());

					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 */
	private void cargarDatosClasificadorHijo() {

		try {
			String parametro = (SysmanFunciones.esBdSqlServer())
					? cadena.replace("TO_CLOB(", "").replace(")", "")
							: cadena;

					ejbPresupuestoCuatro.cargarClasificadorHijo(compania, parametro, SessionUtil.getUser().getCodigo());

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

			Map<String, Object> param = new HashMap<>();
			param.put("COMPANIA", compania);
			param.put("ANO", String.valueOf(SysmanFunciones.ano(
					new Date())));
			listaClasificador = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmSubirClasificadoresUrlEnum.URL0002.getValue())
									.getUrl(),
									param));
			listaCompania = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmSubirClasificadoresUrlEnum.URL0001.getValue())
									.getUrl(),
									param));
			HSSFSheet excelSheet =  null;
			for (Registro option : listaClasificador) {
				String nombrehoja = option.getCampos().get("CODIGO").toString() +" " + option.getCampos().get("NOMBRE").toString();
				excelSheet = workbook.createSheet(nombrehoja);
				/* Propiedades letra encabezado */
				Font font = workbook.createFont();
				font.setFontName("Calibri");
				font.setBold(true);

				// Tamaño de letra
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
				cell.setCellValue("ANO");
				excelSheet.autoSizeColumn(1);

				cell = row.createCell(2);
				cell.setCellValue("CODIGO");
				excelSheet.autoSizeColumn(2);

				cell = row.createCell(3);
				cell.setCellValue("NOMBRE");
				excelSheet.autoSizeColumn(3);

				cell = row.createCell(4);
				cell.setCellValue("SECCIONESADI");
				excelSheet.autoSizeColumn(4);

				cell = row.createCell(5);
				cell.setCellValue("MOVIMIENTO");
				excelSheet.autoSizeColumn(5);

				cell = row.createCell(6);
				cell.setCellValue("REGALIAS");
				excelSheet.autoSizeColumn(6);
				
				cell =row.createCell(7);
				cell.setCellValue("APLICA INGRESOS");
				excelSheet.autoSizeColumn(7);
				
				if ("006 CODIGO CCPET".equalsIgnoreCase(nombrehoja.trim()) || "010 CODIGO CCPET REGALIAS".equalsIgnoreCase(nombrehoja.trim())) {
					cell =row.createCell(8);
					cell.setCellValue("OBLIGA CPC DANE");
					excelSheet.autoSizeColumn(8);
				}

			}
			for (Registro option : listaClasificador) {
				excelSheet = workbook.getSheet(option.getCampos().get("CODIGO").toString() +" " +option.getCampos().get("NOMBRE").toString());
				// lista desplegable separada
				addValidationToSheet2(workbook, excelSheet, listaCompania, 'A', 1, 10000, "Compania");
				//addValidationToSheet2(workbook, excelSheet, listaClasificador, 'C', 1, 10000, "Clasificador");
				
			}
			addValidationToSheet2(workbook, excelSheet, 'A', "CLASIFICADORES HIJO");
			
			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Plantilla Clasificadores.xls");

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}

		// </CODIGO_DESARROLLADO>
	}
	/**
	 * Agregar una lista desplegable a la página de la hoja
	 *
	 * Archivo de Excel del libro de trabajo @param, utilizado para agregar e
	 * nombre
	 * 
	 * @param targetSheet La página de la hoja donde se encuentra la lista en
	 *                    cascada
	 * @param options     Datos en cascada ['Baidu', 'Alibaba']
	 * @param column      La columna de la lista desplegable comienza en'A '
	 * @param fromRow     fila de inicio del límite desplegable
	 * @param endRow      límite desplegable de la fila final
	 */
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, List<Registro> options, char column,
			int fromRow, int endRow, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet =  null;
		int i = 0;
		try {
			optionsSheet = workbook.createSheet(hiddenSheetName);
		} catch (Exception e) {
			optionsSheet = workbook.getSheet(hiddenSheetName);
			i++;
		}


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
		if(i==0) {
			createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
			optionsSheet.protectSheet("Sysman10*");
		}
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, (int) column - 'A',
				(int) column - 'A');
		targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}
	/**
	 * 
	 * @param workbook
	 * @param targetSheet
	 * @param column
	 * @param name
	 */
	public static void addValidationToSheet2(Workbook workbook, Sheet targetSheet, char column, String name) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);

		row = optionsSheet.createRow(0);

		cell = row.createCell(0);
		cell.setCellValue("COMPANIA");

		cell = row.createCell(1);
		cell.setCellValue("ANO");

		cell = row.createCell(2);
		cell.setCellValue("CODIGO CLASE PADRE");
		
		cell = row.createCell(3);
		cell.setCellValue("CODIGO TIPO PADRE");
		
		cell = row.createCell(4);
		cell.setCellValue("CODIGO CLASE HIJO");
		
		cell = row.createCell(5);
		cell.setCellValue("CODIGO TIPO HIJO");

		//		createName(workbook, nameName, hiddenSheetName + "!$A$1:$A$" + options.size());
//		optionsSheet.protectSheet("Sysman10*");
//		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
//		CellRangeAddressList regions = new CellRangeAddressList(fromRow, endRow, (int) column - 'A',
//				(int) column - 'A');
//		targetSheet.addValidationData(new HSSFDataValidation(regions, constraint));
	}
	private static Name createName(Workbook workbook, String nameName, String formula) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		name.setRefersToFormula(formula);
		return name;
	}

	/**
	 * No se puede empezar con un número
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
	// </METODOS_BOTONES>
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>


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
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
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

	public List<Registro> getListaClasificador() {
		return listaClasificador;
	}

	public void setListaClasificador(List<Registro> listaClasificador) {
		this.listaClasificador = listaClasificador;
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

	public String getCadena() {
		return cadena;
	}
	public void setCadena(String cadena) {
		this.cadena = cadena;
	}
	public long getContador() {
		return contador;
	}
	public void setContador(long contador) {
		this.contador = contador;
	}
	public String getClaseClasificador() {
		return claseClasificador;
	}
	public void setClaseClasificador(String claseClasificador) {
		this.claseClasificador = claseClasificador;
	}
	public EjbPresupuestoCuatroRemote getEjbPresupuestoCuatro() {
		return ejbPresupuestoCuatro;
	}
	public void setEjbPresupuestoCuatro(EjbPresupuestoCuatroRemote ejbPresupuestoCuatro) {
		this.ejbPresupuestoCuatro = ejbPresupuestoCuatro;
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
	public List<Registro> getListaclasificadorAsubir() {
		return listaclasificadorAsubir;
	}
	public void setListaclasificadorAsubir(List<Registro> listaclasificadorAsubir) {
		this.listaclasificadorAsubir = listaclasificadorAsubir;
	}
	public Registro getRegistro() {
		return registro;
	}
	public void setRegistro(Registro registro) {
		this.registro = registro;
	}
}
