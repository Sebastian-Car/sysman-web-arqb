/*-
 * FrmcargarcobrosexcelControlador.java
 *
 * 1.0
 * 
 * 09/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 09/04/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmcargarcobrosexcelControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
	 * Variable encargada de almacenar temporalmente el ano de cobro
	 * de ingreso al modulo
	 */
	private String ano;
	/**
	 * Variable encargada de almacenar temporalmente el tipo de cobro
	 * de ingreso al modulo
	 */
	private String tipoCobro;

	private Date fechaComprobante;

	private Date fechaVencimiento;
	/**
	 * Variable que almacena el nombre del tipo de cobro seleccionado
	 */
	private String nombreTipoCobro;
	/**
	 * Variable que almacena el titulo del formulario
	 */
	private String titulo;
	/**
	 * Variable que almacena la informacion del excel
	 */
	private StringBuilder cadena;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;

	private static final String TITULOC = "Cargar cobros";
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Este atributo se usa como auxiliar del componente selector de
	 * archivos CargarExcel y funciona como contenedor del archivo que se
	 * debe guardar
	 */
	private ContenedorArchivo contArchivoCargarExcel;

	@EJB
	private EjbFacturacionGeneralCuatroRemote ejbFacturacionGeneralCuatro;

	/**
	 * Crea una nueva instancia de FrmcargarcobrosexcelControlador
	 */
	public FrmcargarcobrosexcelControlador() {
		super();
		compania = SessionUtil.getCompania();
		ano = (String) SessionUtil.getSessionVar(
				ConstantesFacturacionGenEnum.ANIO.getValue());

		tipoCobro = (String) SessionUtil.getSessionVar(
				ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());

		nombreTipoCobro = (String) SessionUtil.getSessionVar(
				ConstantesFacturacionGenEnum.NOMBRETIPOCOBRO
				.getValue());

		titulo = TITULOC + " " + nombreTipoCobro;

		contArchivoCargarExcel = new ContenedorArchivo();

		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_CARGAR_COBROS
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
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
		fechaComprobante = new Date();
		fechaVencimiento = new Date();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CargarExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirCargarExcel() {

		FileInputStream file = null;
		cadena = new StringBuilder();
		try
		{

			if (validarArchivo())
			{

				String rutaArchivo = contArchivoCargarExcel.getArchivo()
						.getPath();

				String extension = rutaArchivo
						.substring(rutaArchivo.indexOf('.'),
								rutaArchivo.length())
						.substring(1, rutaArchivo.substring(
								rutaArchivo.indexOf('.'),
								rutaArchivo.length()).length());

				file = new FileInputStream(new File(rutaArchivo));

				Workbook workbook = null;

				if ("xls".equals(extension))
				{
					workbook = new HSSFWorkbook(file);
				}
				else
				{
					workbook = new XSSFWorkbook(file);
				}

				if (!validarEstructuraPlantilla(workbook)) {
					return;
				}

				if (tipoCobro.equals("TUA"))
				{
					leerHoja(workbook, 0, 20, cadena, 1);
					cargarDatos();
				}
				else if (tipoCobro.equals("TRE"))
				{
					leerHoja(workbook, 0, 17, cadena, 1);
					cargarDatos();
				}

				file.close();
				workbook.close();
			}
		}
		catch (IOException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

	}

	public void leerHoja(Workbook workbook, int hoja, int columnas,
			StringBuilder cadena, int filainicial)
	{
		cadena.append("TO_CLOB('");
		Sheet sheet = workbook.getSheetAt(hoja);
		Row fila;
		Cell celda;
		int num = 0;

		for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++)
		{
			fila = sheet.getRow(i);

			if (fila != null && fila.getCell(0) != null)
			{

				for (int j = 0; j < columnas; j++)
				{
					celda = fila.getCell(j);
					String valorCelda = "";
					if (celda != null)
					{
						switch (celda.getCellType())
						{
						case Cell.CELL_TYPE_STRING:
							valorCelda = celda.getStringCellValue()
							.replace("'", " ");
							break;
						case Cell.CELL_TYPE_NUMERIC:
							if (DateUtil.isCellDateFormatted(celda))
							{
								valorCelda = new SimpleDateFormat("dd/MM/yyyy")
										.format(celda.getDateCellValue());
							}else{
								valorCelda = NumberToTextConverter
										.toText(celda.getNumericCellValue());
							}
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							valorCelda = String.valueOf(celda.getBooleanCellValue());
							break;
						case Cell.CELL_TYPE_FORMULA:
							valorCelda = celda.getCellFormula();
							break;
						default:
							valorCelda = "";
							break;
						}
						num = num + valorCelda.length();
						cadena.append(valorCelda);
					}
					else
					{
						cadena.append("");
					}
					if (num >= 4000)
					{
						cadena.append("') || TO_CLOB('");
						num = 0;
					}
					cadena.append(SysmanConstantes.SEPARADOR_COL);
				}
				cadena.append(SysmanConstantes.SEPARADOR_REG);
			}

		}
		cadena.append("')"
				+ "");
	}

	private void cargarDatos()
	{
		try
		{
			String parametro = SysmanFunciones.esBdSqlServer()
					? cadena.toString().replace("TO_CLOB(", "")
							.replace(")", "")
							: cadena.toString();

							String mensaje = ejbFacturacionGeneralCuatro.cargarCobros(compania, Integer.parseInt(ano), tipoCobro, 
									parametro, fechaComprobante, fechaVencimiento, SessionUtil.getUser().getCodigo());	

							generarArchivo(mensaje,"InconsistenciasCargueDatos"); 

		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}


	}

	public boolean validarArchivo()
	{

		if (contArchivoCargarExcel.getArchivo() == null)
		{
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4001"));
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CrearExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws IOException 
	 *
	 */
	public void oprimirCrearExcel() throws IOException {

		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			if ("TUA".equals(tipoCobro)) {

				crearPlantillaTUA(workbook);

				workbook.write(out);

				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(out.toByteArray()),
						"Plantilla_Cargue_TUA.xls"
						);

			} else if ("TRE".equals(tipoCobro)) {

				crearPlantillaTRE(workbook);

				workbook.write(out);

				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(out.toByteArray()),
						"Plantilla_Cargue_TRE.xls"
						);

			} else {

				JsfUtil.agregarMensajeAlerta(
						"No se tiene una plantilla configurada para el tipo de cobro seleccionado.");
			}

		} catch (IOException | JRException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}

	}

	private void crearPlantillaTUA(HSSFWorkbook workbook) {

		HSSFSheet excelSheet = workbook.createSheet("Plantilla");

		Row row = excelSheet.createRow(0);

		String[] columnas = obtenerColumnasTUA();

		crearCabecera(workbook, excelSheet, row, columnas);
	}

	private void crearPlantillaTRE(HSSFWorkbook workbook) {

		HSSFSheet excelSheet = workbook.createSheet("Plantilla");

		Row row = excelSheet.createRow(0);

		String[] columnas = obtenerColumnasTRE();

		crearCabecera(workbook, excelSheet, row, columnas);
	}

	private void crearCabecera(HSSFWorkbook workbook,
			HSSFSheet sheet,
			Row row,
			String[] columnas) {

		Font font = workbook.createFont();
		font.setFontName("Calibri");
		font.setBold(true);
		font.setFontHeightInPoints((short) 8);

		CellStyle style = workbook.createCellStyle();
		style.setFont(font);

		for (int i = 0; i < columnas.length; i++) {

			Cell cell = row.createCell(i);

			cell.setCellValue(columnas[i]);
			cell.setCellStyle(style);

			sheet.autoSizeColumn(i);
		}
	}

	private void generarArchivo(String salidaTexto2, String archivoNom)
	{

		archivoDescarga = null;
		try
		{
			if (salidaTexto2 != null)
			{
				ByteArrayInputStream archivo = JsfUtil
						.serializarPlano(salidaTexto2);
				archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
						archivoNom + ".txt");
			}
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		}
		catch (IOException | JRException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private boolean validarEstructuraPlantilla(Workbook workbook) {

		Sheet sheet = workbook.getSheetAt(0);

		if (sheet == null) {

			JsfUtil.agregarMensajeAlerta("El archivo no contiene hojas.");

			return false;
		}

		Row row = sheet.getRow(0);

		if (row == null) {

			JsfUtil.agregarMensajeAlerta("La plantilla no contiene encabezados.");

			return false;
		}

		String[] columnasEsperadas;

		if ("TUA".equals(tipoCobro)) {

			columnasEsperadas = obtenerColumnasTUA();

		} else if ("TRE".equals(tipoCobro)) {

			columnasEsperadas = obtenerColumnasTRE();

		} else {

			JsfUtil.agregarMensajeAlerta("No existe plantilla configurada para el tipo de cobro seleccionado.");
			return false;
		}

		DataFormatter formatter = new DataFormatter();

		for (int i = 0; i < columnasEsperadas.length; i++) {

			Cell cell = row.getCell(i);

			String valorCelda = cell != null
					? formatter.formatCellValue(cell).trim()
							: "";

					if (!columnasEsperadas[i].equalsIgnoreCase(valorCelda)) {

						JsfUtil.agregarMensajeAlerta(
								"La estructura de la plantilla es incorrecta. "
										+ "Se esperaba la columna '"
										+ columnasEsperadas[i]
												+ "' en la posición "
												+ (i + 1)
								);
						return false;
					}
		}
		return true;
	}

	private String[] obtenerColumnasTUA() {

		return new String[] {
				"VIGENCIA",
				"CODIGO CATEGORIA",
				"NIT",
				"CONCESION",
				"DOCUMENTO",
				"FEC_DOCUMENTO",
				"CODIGO DEL PROCESO/ CONCEPTO",
				"FUENTE",
				"Fop SUBTERRANEO",
				"TUA SUBTERRANEA",
				"VOLUMEN SUBTERRANEA",
				"Fop SUPERFICIAL",
				"TUA SUPERFICIAL",
				"VOLUMEN SUPERFICIAL",
				"VAL_PAGAR SUBTERRANEO",
				"VAL_PAGAR SUPERFICIAL",
				"VAL_TOTAL_TASA",
				"OBSERVACION",
				"COD UH",
				"NOMBRE UH"
		};
	}

	private String[] obtenerColumnasTRE() {

		return new String[] {
				"VIGENCIA",
				"CODIGO CATEGORIA",
				"NIT",
				"DOCUMENTO TASA",
				"FACTOR REGIONAL DBO",
				"TARIFA MINIMA DBO",
				"CARGA DBO",
				"VAL_PAGARDBO",
				"FACTOR REGIONAL SST",
				"TARIFA MINIMA SST",
				"CARGA SST",
				"VAL_PAGARSST",
				"VAL_PAGAR_TOTAL",
				"OBSERVACION",
				"COD LOCALIZACION",
				"NOMBRE LOCALIZACION",
				"APRUEBA"
		};
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
	 * Retorna la variable fechaComprobante
	 * 
	 * @return  fechaComprobante
	 */
	public Date getFechaComprobante() {
		return fechaComprobante;
	}
	/**
	 * Asigna la variable  fechaComprobante
	 * 
	 * @param  fechaComprobante
	 * Variable a asignar en  fechaComprobante
	 */
	public void setFechaComprobante(Date fechaComprobante) {
		this.fechaComprobante = fechaComprobante;
	}
	/**
	 * Retorna la variable fechaVencimiento
	 * 
	 * @return  fechaVencimiento
	 */
	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}
	/**
	 * Asigna la variable  fechaVencimiento
	 * 
	 * @param  fechaVencimiento
	 * Variable a asignar en  fechaVencimiento
	 */
	public void setFechaVencimiento(Date fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	/**
	 * Retorna el objeto contArchivoCargarExcel
	 * 
	 * @return contArchivoCargarExcel
	 */
	public ContenedorArchivo getContArchivoCargarExcel() {
		return contArchivoCargarExcel;
	}
	/**
	 * Asigna el objeto contArchivoCargarExcel
	 * 
	 * @param contArchivoCargarExcel
	 * Variable a asignar en contArchivoCargarExcel
	 */
	public void setContArchivoCargarExcel(ContenedorArchivo contArchivoCargarExcel) {
		this.contArchivoCargarExcel = contArchivoCargarExcel;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
