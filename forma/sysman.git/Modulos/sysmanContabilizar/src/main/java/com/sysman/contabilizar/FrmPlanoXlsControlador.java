/*-
 * FrmPlanoXlsControlador.java
 *
 * 1.0
 * 
 * 08/02/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilizar;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.contabilidad.ConciliacionPorPlanoTipoControlador;
import com.sysman.contabilizar.ejb.EjbContabilizarCeroRemote;
import com.sysman.contabilizar.enums.FrmPlanoXlsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;
/**
 *
 * @version 1.0, 08/02/2021
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class  FrmPlanoXlsControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String modulo ;
	//<DECLARAR_ATRIBUTOS>
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>

	private RegistroDataModelImpl listatipocpte;
//	private RegistroDataModelImpl listaCUENTACONTRA;
	private RegistroDataModelImpl listaauxiliarcontra;
	private RegistroDataModelImpl listaCCostoContra;
	 private RegistroDataModelImpl listaFuenteRecursosContra;
	 private RegistroDataModelImpl listaReferenciaContra;

	private String tipoCpte;
	private String consecutivo;
	private boolean porTercero;
	private boolean ckAgrupado;
	private boolean ckRetenciones;
	private boolean ckAplicaAux;
	private String columnaFecha;
	private String cuenta;
	private String descripcion;
	private int filaBase;
	private String columnaError;
	private String valorDebito;
	private String valorCredito;
	private String fuenteRecursos;
	private String referencia;
	private String centroCosContra;
	private String fuenteRecursosContra;
	private String referenciaContra;
	private String baseGravable;
	private String numeroDocumento;
	private String tercero;
	private String sucursal;
	private String centroCosto;
	private String auxiliar;
//	private String cuentaContra;
	private String columnaTexto;
	private String numeroContrato;
	private String auxiliarContra;
	private String tipoContrato;
	private int filaFin;
	private String texto;

	private ContenedorArchivo contArchivoPlantillaExcel;
	private boolean isTexto;
	private Workbook workbook;

	/**
	 * Este atributo se usa como auxiliar del componente referencia de
	 * archivos cargarPlantilla y funciona como contenedor del archivo que se
	 * desea cargar
	 */
	private UploadedFile archivoCargacargarPlantilla;
	private StreamedContent archivoDescarga;

	@EJB
	private EjbContabilizarCeroRemote ejbContabilizarCero ;
	private CellStyle textoDocumentoDuplicado;
	private CellStyle fechaDocumentDuplicado;
	private CellStyle textoInformativo;
	private CellStyle fechaInformativo;
	private CellStyle fechaError;
	private CellStyle textoError;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmPlanoXlsControlador
	 */
	public FrmPlanoXlsControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario=2243;
			validarPermisos();
			//<INI_ADICIONAL>
			columnaFecha = "A";
			cuenta = "B";
			valorDebito = "C";
			valorCredito = "D";
			descripcion = "E";
			columnaTexto = "F";
			baseGravable = "G";
			numeroContrato = "H";
			numeroDocumento = "I";
			tercero = "J";
			sucursal ="K";
			centroCosto = "L";
			fuenteRecursos = "M";
			referencia = "N";
			auxiliar ="O";
			tipoContrato="P";
			columnaError ="Z";
			filaBase = 2;
			filaFin = 10;
			porTercero = false;
			tipoCpte = "COM";
			consecutivo = SysmanFunciones.ano(new Date())+"000001";


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
		cargarListatipocpte();
//		cargarListaCUENTACONTRA();
		cargarListaauxiliarcontra();
		cargarListaCCostoContra();
		cargarListaFuenteRecursosContra();
		cargarListaReferenciaContra();
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
		//	generarConsecutivo();
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listatipocpte
	 *
	 */
	public void cargarListatipocpte(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmPlanoXlsControladorUrlEnum.URL0001
						.getValue());

		listatipocpte = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());


	}
//	Ticket 7702141-09/11/2021(jcrojas2): Se comentan lineas relacionadas con el campo CuentaContra,
//	ya que se pidio eliminar este campo dentro del proceso.
//	/**
//	 * 
//	 * Carga la lista listaCUENTACONTRA
//	 *
//	 */
//	public void cargarListaCUENTACONTRA(){
//
//		Map<String, Object> param = new TreeMap<>();
//		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
//		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date())); 
//
//		UrlBean urlBean = UrlServiceUtil.getInstance()
//				.getUrlServiceByUrlByEnumID(
//						FrmPlanoXlsControladorUrlEnum.URL0003
//						.getValue());
//
//		listaCUENTACONTRA = new RegistroDataModelImpl(urlBean.getUrl(),
//				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
//	}
	/**
	 * 
	 * Carga la lista listaauxiliarcontra
	 *
	 */
	public void cargarListaauxiliarcontra(){

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date())); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmPlanoXlsControladorUrlEnum.URL0002
						.getValue());

		listaauxiliarcontra = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	 /**
     * 
     * Carga la lista listaCCostoContra
     *
     */
	public void cargarListaCCostoContra()
	{
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date())); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmPlanoXlsControladorUrlEnum.URL0004
						.getValue());

		listaCCostoContra = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
     * 
     * Carga la lista listaFuenteRecursosContra
     *
     */
	public void cargarListaFuenteRecursosContra()
	{
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date())); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmPlanoXlsControladorUrlEnum.URL0005
						.getValue());

		listaFuenteRecursosContra = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	/**
     * 
     * Carga la lista listaReferenciaContra
     *
     */
	public void cargarListaReferenciaContra()
	{
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date())); 

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmPlanoXlsControladorUrlEnum.URL0006
						.getValue());

		listaReferenciaContra = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Iniciar
	 * en la vista
	 *
	 *
	 */
	public void oprimirIniciar() {
		//<CODIGO_DESARROLLADO>

		try {
			archivoDescarga = null;
			InputStream is;
			workbook = null;
			String plano = archivoCargacargarPlantilla.getFileName();
			if (SysmanFunciones.validarVariableVacio(plano)) {
				JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
				return;
			}
//			if (cuentaContra.isEmpty()) {
//				JsfUtil.agregarMensajeError(idioma.getString("TB_TB4358"));
//				return;
//			}

			if(!ckAplicaAux) {

				auxiliarContra = "0";

			}else {
				if (auxiliarContra.isEmpty()) {
					JsfUtil.agregarMensajeError(idioma.getString("TB_TB4358"));
					return;
				}
			}
			if(texto.isEmpty()) {

				texto = ".";

			}


			is = archivoCargacargarPlantilla.getInputstream();

			String rutaArchivo = archivoCargacargarPlantilla.getFileName();
			String extension = FilenameUtils.getExtension(rutaArchivo);

			if (workbook == null) {
				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(is);
				}
				else {
					workbook = new XSSFWorkbook(is);
				}
			}

			Sheet sheet = workbook.getSheetAt(1);
			isTexto = false;
			crearEstilos(workbook);
			String respuesta = null;

			int filaInicial = filaBase;
			int filaFinal = 0;
			while (filaInicial <= filaFin) {
				filaFinal = filaInicial + 999;
				filaFinal = (filaFinal > filaFin ? filaFin : filaFinal);
				String cadena = "TO_CLOB('"
						+ exportarExcelToCsv(workbook, filaInicial - 1,
								filaFinal - 1)
						+ "')";

				respuesta = ejecutarFuncPlanoXls(cadena, filaInicial);
				if(!respuesta.equals("") || !respuesta.isEmpty()) {
					definirFormatoColError(respuesta, sheet, filaInicial - 1);
				}
				filaInicial = filaFinal + 1;
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			out.close();
			workbook.close();
			Calendar calendar = Calendar.getInstance();
			String cadenaFecha = ""
					+ Integer.toString(calendar.get(Calendar.YEAR)
							+ (calendar.get(Calendar.MONTH) + 1)
							+ calendar.get(Calendar.DATE)
							+ calendar.get(Calendar.HOUR_OF_DAY)
							+ calendar.get(Calendar.MINUTE)
							+ calendar.get(Calendar.SECOND));
			String nombreArchivo = "Informede" + cadenaFecha
					+ archivoCargacargarPlantilla.getFileName();
			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(out.toByteArray()),
					nombreArchivo);

		} catch (IOException | JRException e) {
			e.printStackTrace();
		}
		//</CODIGO_DESARROLLADO>
	}

	public void cargarArchivoprueba(FileUploadEvent event){
		//<CODIGO_DESARROLLADO>
		InputStream is;
		workbook = null;
		try {
			is = event.getFile().getInputstream();

			if (is == null) {
				return;
			}
			String rutaArchivo = event.getFile().getFileName();
			String extension = FilenameUtils.getExtension(rutaArchivo);

			if (workbook == null) {
				if ("xls".equals(extension)) {
					workbook = new HSSFWorkbook(is);
				}
				else {
					workbook = new XSSFWorkbook(is);
				}
			}


			//	Sheet sheet = workbook.getSheetAt(0);
			isTexto = false;
			//crearEstilos(workbook);
			String respuesta = "";

			int filaInicial = filaBase;
			int filaFinal = 0;
			while (filaInicial <= filaFin) {
				filaFinal = filaInicial + 999;
				filaFinal = (filaFinal > filaFin ? filaFin : filaFinal);
				String cadena = "TO_CLOB('"
						+ exportarExcelToCsv(workbook, filaInicial - 1,
								filaFinal - 1)
						+ "')";

				respuesta = ejecutarFuncPlanoXls(cadena, filaInicial);
				filaInicial = filaFinal + 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//</CODIGO_DESARROLLADO>
	}

	private void generarPlano() {
		// TODO Auto-generated method stub
		try (
				FileInputStream fileInputStream = new FileInputStream(
						contArchivoPlantillaExcel
						.getArchivo());
				Workbook workbook = new HSSFWorkbook(
						fileInputStream);) {

			Sheet sheet = workbook.getSheetAt(0);
			isTexto = false;
			//crearEstilos(workbook);
			String respuesta = "";

			int filaInicial = filaBase;
			int filaFinal = 0;
			while (filaInicial <= filaFin) {
				filaFinal = filaInicial + 999;
				filaFinal = (filaFinal > filaFin ? filaFin : filaFinal);
				String cadena = "TO_CLOB('"
						+ exportarExcelToCsv(workbook, filaInicial - 1,
								filaFinal - 1)
						+ "')";

				respuesta = ejecutarFuncPlanoXls(cadena, filaInicial);
				//	definirFormatoColError(respuesta, sheet, filaInicial - 1);
				filaInicial = filaFinal + 1;

			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			out.close();
			workbook.close();

		}
		catch ( IOException e) {

			Logger.getLogger(
					ConciliacionPorPlanoTipoControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(
					idioma.getString("TB_TB1315") + e.getMessage());
		}

	}
	private void definirFormatoColError(String respuesta, Sheet sheet,
			int filaInicial) {
		String auxCadena;
		BufferedReader br = null;
		int posFila = -1;
		int posTipo = -1;

		InputStream inputStream = new ByteArrayInputStream(
				respuesta.getBytes(Charset.forName("UTF-8")));
		br = new BufferedReader(new InputStreamReader(inputStream));
		try {
			while ((auxCadena = br.readLine()) != null) {
				posFila = auxCadena.indexOf(";");
				posTipo = auxCadena.indexOf(";", posFila + 1);

				definirTipoMensaje(auxCadena.substring(posFila + 1,
                        posTipo),

						auxCadena.substring(0, posFila)
						+ " - "
						+ auxCadena.substring(posTipo + 1),
						sheet.getRow(Integer.parseInt(auxCadena
								.substring(0, posFila))
								- 1));
			}
		}
		catch (IOException e) {
			Logger.getLogger(
					ConciliacionPorPlanoTipoControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	private void definirTipoMensaje(String tipoMensaje, String mensaje,
			Row row) {
		switch (tipoMensaje) {
		case "1":
			registrarMensaje(row, "Listo Antes. - " + mensaje,
					textoDocumentoDuplicado,
					fechaDocumentDuplicado);
			break;
		case "2":
			registrarMensaje(row, "Ok. - " + mensaje,
					textoInformativo, fechaInformativo);
			break;
		case "3":
			registrarMensaje(row, "Inconsistencia. - " + mensaje,
					textoError, fechaError);
			break;
		default:
			break;

		}

	}
	private void crearEstilos(Workbook workbook) {
		// TODO Auto-generated method stub
		textoDocumentoDuplicado = crearEstilo(workbook,
				IndexedColors.BLUE_GREY.getIndex());
		fechaError = crearEstilo(workbook, IndexedColors.ORANGE.getIndex());
		fechaInformativo = crearEstilo(workbook,
				IndexedColors.GREEN.getIndex());
		fechaDocumentDuplicado = crearEstilo(workbook,
				IndexedColors.BLUE_GREY.getIndex());
		textoError = crearEstilo(workbook, IndexedColors.ORANGE.getIndex());
		textoInformativo = crearEstilo(workbook,
				IndexedColors.GREEN.getIndex());

	}
	/**
	 * Registra un mensaje en la celda de errores.
	 *
	 * @param row
	 * Fila que se va a afectar.
	 * @param mensaje
	 * Mensaje.
	 * @param color
	 * Indice del color.
	 */
	public void registrarMensaje(Row row, String mensaje,
			CellStyle estilo, CellStyle estiloFecha) {
		// Color del texto, Por cada celda en la fila
		for (int i = 0; i < row.getLastCellNum(); i++) {
			Cell cell = row.getCell(i);
			if (cell != null) {
				cell.setCellStyle(estilo);
			}
			else {
				Cell c = row.createCell(i);
				c.setCellStyle(estilo);
			}
		}
		// Creacion de la celda de errores.
		Cell cell = row.createCell(
				CellReference.convertColStringToIndex(columnaError));
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellStyle(estilo);
		cell.setCellValue(new HSSFRichTextString(mensaje));
	}

	/**
	 * Crea un estilo de celda en el workbook ingresado por parametro.
	 *
	 * @param Workbook
	 * Libro de trabajo.
	 * @param color
	 * Indice asociado a un color determinado.
	 * @return
	 */
	private CellStyle crearEstilo(Workbook workbook, short color) {
		CellStyle style = workbook.createCellStyle();
		style.setFont(getFuenteTexto(workbook, "Arial", (short) 10, color));

		return style;
	}
	/**
	 * Crea una fuente de texto, segun los parametros recibidos.
	 *
	 * @param workbook
	 * @param fontName
	 * Nombre de la fuente.
	 * @param size
	 * Tamanio del texto.
	 * @param color
	 * Color de la fuente.
	 * @return Fuente de texto.
	 */
	private Font getFuenteTexto(Workbook workbook, String fontName, short size,
			short color) {
		Font font = workbook.createFont();
		font.setFontHeightInPoints(size);
		font.setFontName(fontName);
		font.setColor(color);
		return font;
	}

	public String exportarExcelToCsv(Workbook workbook, int filainicial,
			int filafinal) {
		StringBuilder data = new StringBuilder();
		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(1);
		int num = 0;

		for (int rowNum = filainicial; rowNum <= filafinal; rowNum++) {
			row = sheet.getRow(rowNum);
			if (row == null) {
				// This whole row is empty
				// Handle it as needed
				continue;
			}
			num = num + 1;

			data.append(asignaDato(row, columnaFecha, "D"));
			data.append(SysmanFunciones
					.nvlStr(asignaDato(row, cuenta , "N"), "0"));
			data.append(SysmanFunciones
					.nvlStr(asignaDato(row, valorDebito, "N"), "0"));
			data.append(SysmanFunciones
					.nvlStr(asignaDato(row, valorCredito, "N"), "0"));
			data.append(asignaDato(row, descripcion, "S"));
			data.append(asignaDato(row, columnaTexto, "S"));
			data.append(asignaDato(row, baseGravable, "N"));
			data.append(asignaDato(row, numeroContrato, "S"));
			data.append(asignaDato(row, numeroDocumento, "S"));
			data.append(asignaDato(row, tercero, "S"));
			data.append(asignaDato(row, sucursal, "S"));
			data.append(asignaDato(row, centroCosto, "S"));
			data.append(asignaDato(row, fuenteRecursos, "S"));
			data.append(asignaDato(row, referencia, "S"));
			data.append(asignaDato(row, auxiliar, "S"));
			data.append(asignaDato(row, tipoContrato, "S"));
			data.append("#");

			if (num >= 100) {
				data.append("') || TO_CLOB('");
			}
		}
		return data.toString();

	}
	private String asignaDato(Row row, String columna, String tipo) {
		String salida = "";
		Cell cell = row.getCell(columna.charAt(0) - 65,
				Row.RETURN_BLANK_AS_NULL);
		if (cell == null) {
			salida = ";";
		}
		else {
			isTexto = true;
			salida = asignarFormatoValor(cell, tipo) + ";";
		}
		return salida;
	}
	private Object asignarFormatoValor(Cell cell, String tipo) {
		Object object = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				object = cell.getRichStringCellValue().getString();
				object = object.toString().replace(",", "");
				object = object.toString().replace(";", "CHR(59)");
				object = object.toString().replace("#", "CHR(35)");
				break;
			case Cell.CELL_TYPE_NUMERIC:
				object = definirValor(cell, tipo);
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				object = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				object = cell.getCellFormula();
				break;
			case Cell.CELL_TYPE_BLANK:
				object = cell;
				break;
			default:
				object = null;
				break;
			}
		}
		else {
			logger.debug("La celda viene nula");
		}
		return object;
	}

	private Object definirValor(Cell cell, String tipo) {
		Object object;

		if (DateUtil.isCellDateFormatted(cell)) {
			object = cell.getDateCellValue();
			if (isTexto) {
				try {
					String fechaC = SysmanFunciones
							.convertirAFechaCadena((Date) object);
					object = fechaC;
				}
				catch (ParseException e) {
					Logger.getLogger(
							ConciliacionPorPlanoTipoControlador.class
							.getName())
					.log(Level.SEVERE, null, e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
			}
			isTexto = false;
		}
		else {
			if ("N".equals(tipo)) {
				object = cell.getNumericCellValue();
			}
			else {
				DataFormatter formatter = new DataFormatter();
				object = formatter.formatCellValue(cell);
			}

		}
		return object;
	}

	private String ejecutarFuncPlanoXls(String cadena, int filaInicial) {
		String respuesta = null;
		//		 setArchivoSalida("/home/jgomez/Escritorio/prueba.txt",
		//		 cadena);
		try {
			respuesta = ejbContabilizarCero.cargarInterfazporXls(compania, tipoCpte, consecutivo, 
					cadena, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, ckRetenciones, porTercero, 
					ckAgrupado, ckAplicaAux,Integer.parseInt(modulo), filaInicial, 
					auxiliarContra, SysmanFunciones.nvlStr(centroCosContra, "99999999999999999999"), SysmanFunciones.nvlStr(fuenteRecursosContra,"99999999999999999999"), 
					SysmanFunciones.nvlStr(referenciaContra,"99999999999999999999"),texto,SessionUtil.getUser().getCodigo());
			
			if(!respuesta.equals("")) {
			 JsfUtil.agregarMensajeInformativo(
                     idioma.getString("MSM_PROCESO_EJECUTADO"));
			}
		}
		catch (SystemException e) {
			Logger.getLogger(
					ConciliacionPorPlanoTipoControlador.class.getName())
			.log(Level.SEVERE, null, e);

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton archivo
	 * en la vista
	 *
	 *
	 */
	public void oprimirarchivo() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarPortercero() {

		if(!porTercero) {
			ckRetenciones = false;
		}

	}

	public void cambiarretenciones() {

	}


	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CodificarRetenciones
	 * en la vista
	 *
	 *
	 */
	public void oprimirCodificarRetenciones() {
		//<CODIGO_DESARROLLADO>


		Map<String, Object> param = new TreeMap<>();
		param.put("ano", SysmanFunciones.ano(new Date()));
		param.put("tipoComp", tipoCpte);
		param.put("numeroComp", consecutivo);


		Direccionador direccionador = new Direccionador();

		direccionador.setParametros(param);

		direccionador.setNumForm(Integer.toString(
				GeneralCodigoFormaEnum.COMPROBANTECNTRETENCIONSD_CONTROLADOR
				.getCodigo()));

		SessionUtil.redireccionarForma(direccionador, modulo);

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listatipocpte
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilatipocpte(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoCpte= registroAux.getCampos().get("CODIGO").toString();

		generarConsecutivo();
	}

//	/**
//	 * 
//	 * Metodo ejecutado al seleccionar una fila de la lista
//	 * listaCUENTACONTRA
//	 *
//	 *
//	 * @param event
//	 * objeto que encapsula la accion proveniente de la vista
//	 */
//	public void seleccionarFilaCUENTACONTRA(SelectEvent event) {
//		Registro registroAux = (Registro) event.getObject();
//		cuentaContra= registroAux.getCampos().get("CODIGO").toString();
//	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaauxiliarcontra
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaauxiliarcontra(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarContra= registroAux.getCampos().get("CODIGO").toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCCostoContra
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCCostoContra(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		centroCosContra= registroAux.getCampos().get("CODIGO").toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaFuenteRecursosContra
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaFuenteRecursosContra(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		fuenteRecursosContra= registroAux.getCampos().get("CODIGO").toString();
	}
	/**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaReferenciaContra
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaReferenciaContra(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
		referenciaContra= registroAux.getCampos().get("CODIGO").toString();
	}

	public void generarConsecutivo() {

		//		try {
		//			if(!tipoCpte.isEmpty()) {
		//				consecutivo = ejbContabilidadCpte.enumerarComprobanteCnt(
		//						compania, SysmanFunciones.ano(new Date()), tipoCpte,
		//						BigInteger.ZERO,"");
		//			}
		//
		//		}
		//		catch (NumberFormatException | SystemException e) {
		//			logger.error(e.getMessage(), e);
		//			JsfUtil.agregarMensajeError(e.getMessage());
		//		}
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>

	/**
	 * @return the contArchivoPlantillaExcel
	 */
	public ContenedorArchivo getContArchivoPlantillaExcel() {
		return contArchivoPlantillaExcel;
	}
//	/**
//	 * @return the listaCUENTACONTRA
//	 */
//	public RegistroDataModelImpl getListaCUENTACONTRA() {
//		return listaCUENTACONTRA;
//	}
	/**
	 * @return the listaauxiliarcontra
	 */
	public RegistroDataModelImpl getListaauxiliarcontra() {
		return listaauxiliarcontra;
	}
	/**
	 * @return the listaCCostoContra
	 */
	public RegistroDataModelImpl getListaCCostoContra() {
		return listaCCostoContra;
	}
	/**
	 * @return the listaFuenteRecursosContra
	 */
	public RegistroDataModelImpl getListaFuenteRecursosContra() {
		return listaFuenteRecursosContra;
	}
	/**
	 * @return the listaReferenciaContra
	 */
	public RegistroDataModelImpl getListaReferenciaContra() {
		return listaReferenciaContra;
	}
//	/**
//	 * @param listaCUENTACONTRA the listaCUENTACONTRA to set
//	 */
//	public void setListaCUENTACONTRA(RegistroDataModelImpl listaCUENTACONTRA) {
//		this.listaCUENTACONTRA = listaCUENTACONTRA;
//	}
	/**
	 * @param listaauxiliarcontra the listaauxiliarcontra to set
	 */
	public void setListaauxiliarcontra(RegistroDataModelImpl listaauxiliarcontra) {
		this.listaauxiliarcontra = listaauxiliarcontra;
	}
	/**
	 * @param listaCCostoContra the listaCCostoContra to set
	 */
	public void setListaCCostoContra(RegistroDataModelImpl listaCCostoContra) {
		this.listaCCostoContra = listaCCostoContra;
	}
	/**
	 * @param listaFuenteRecursosContra the listaFuenteRecursosContra to set
	 */
	public void setListaFuenteRecursosContra(RegistroDataModelImpl listaFuenteRecursosContra) {
		this.listaFuenteRecursosContra = listaFuenteRecursosContra;
	}
	/**
	 * @param listaReferenciaContra the listaReferenciaContra to set
	 */
	public void setListaReferenciaContra(RegistroDataModelImpl listaReferenciaContra) {
		this.listaReferenciaContra = listaReferenciaContra;
	}
	/**
	 * @param contArchivoPlantillaExcel the contArchivoPlantillaExcel to set
	 */
	public void setContArchivoPlantillaExcel(ContenedorArchivo contArchivoPlantillaExcel) {
		this.contArchivoPlantillaExcel = contArchivoPlantillaExcel;
	}
	/**
	 * @return the tipoCpte
	 */
	public String getTipoCpte() {
		return tipoCpte;
	}
	/**
	 * @param tipoCpte the tipoCpte to set
	 */
	public void setTipoCpte(String tipoCpte) {
		this.tipoCpte = tipoCpte;
	}
	/**
	 * @return the listatipocpte
	 */
	public RegistroDataModelImpl getListatipocpte() {
		return listatipocpte;
	}
	/**
	 * @param listatipocpte the listatipocpte to set
	 */
	public void setListatipocpte(RegistroDataModelImpl listatipocpte) {
		this.listatipocpte = listatipocpte;
	}

	/**
	 * @return the columnaFecha
	 */
	public String getColumnaFecha() {
		return columnaFecha;
	}
	/**
	 * @return the cuenta
	 */
	public String getCuenta() {
		return cuenta;
	}
	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}
	/**
	 * @return the filaBase
	 */
	public int getFilaBase() {
		return filaBase;
	}
	/**
	 * @return the columnaError
	 */
	public String getColumnaError() {
		return columnaError;
	}
	/**
	 * @return the valorDebito
	 */
	public String getValorDebito() {
		return valorDebito;
	}
	/**
	 * @return the valorCredito
	 */
	public String getValorCredito() {
		return valorCredito;
	}
	/**
	 * @return the fuenteRecursos
	 */
	public String getFuenteRecursos() {
		return fuenteRecursos;
	}
	/**
	 * @return the referencia
	 */
	public String getReferencia() {
		return referencia;
	}
	/**
	 * @return the centroCosContra
	 */
	public String getCentroCosContra() {
		return centroCosContra;
	}
	/**
	 * @return the fuenteRecursosContra
	 */
	public String getFuenteRecursosContra() {
		return fuenteRecursosContra;
	}
	/**
	 * @return the referenciaContra
	 */
	public String getReferenciaContra() {
		return referenciaContra;
	}
	/**
	 * @return the baseGravable
	 */
	public String getBaseGravable() {
		return baseGravable;
	}
	/**
	 * @return the numeroDocumento
	 */
	public String getNumeroDocumento() {
		return numeroDocumento;
	}
	/**
	 * @return the tercero
	 */
	public String getTercero() {
		return tercero;
	}
	/**
	 * @return the sucursal
	 */
	public String getSucursal() {
		return sucursal;
	}
	/**
	 * @return the centroCosto
	 */
	public String getCentroCosto() {
		return centroCosto;
	}
	/**
	 * @return the auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
//	/**
//	 * @return the cuentaContra
//	 */
//	public String getCuentaContra() {
//		return cuentaContra;
//	}
	/**
	 * @return the columnaTexto
	 */
	public String getColumnaTexto() {
		return columnaTexto;
	}
	/**
	 * @return the numeroContrato
	 */
	public String getNumeroContrato() {
		return numeroContrato;
	}
	/**
	 * @return the auxiliarContra
	 */
	public String getAuxiliarContra() {
		return auxiliarContra;
	}
	/**
	 * @return the tipoContrato
	 */
	public String getTipoContrato() {
		return tipoContrato;
	}
	/**
	 * @return the filaFin
	 */
	public int getFilaFin() {
		return filaFin;
	}
	/**
	 * @return the texto
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * @param columnaFecha the columnaFecha to set
	 */
	public void setColumnaFecha(String columnaFecha) {
		this.columnaFecha = columnaFecha;
	}
	/**
	 * @param cuenta the cuenta to set
	 */
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	/**
	 * @param filaBase the filaBase to set
	 */
	public void setFilaBase(int filaBase) {
		this.filaBase = filaBase;
	}
	/**
	 * @param columnaError the columnaError to set
	 */
	public void setColumnaError(String columnaError) {
		this.columnaError = columnaError;
	}
	/**
	 * @param valorDebito the valor to set
	 */
	public void setValorDebito(String valorDebito) {
		this.valorDebito = valorDebito;
	}
	/**
	 * @param valorCredito the valor to set
	 */
	public void setValorCredito(String valorCredito) {
		this.valorCredito = valorCredito;
	}
	/**
	 * @param fuenteRecursos the valor to set
	 */
	public void setFuenteRecursos(String fuenteRecursos) {
		this.fuenteRecursos = fuenteRecursos;
	}
	/**
	 * @param referencia the valor to set
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	/**
	 * @param centroCosContra the valor to set
	 */
	public void setCentroCosContra(String centroCosContra) {
		this.centroCosContra = centroCosContra;
	}
	/**
	 * @param fuenteRecursosContra the valor to set
	 */
	public void setFuenteRecursosContra(String fuenteRecursosContra) {
		this.fuenteRecursosContra = fuenteRecursosContra;
	}
	/**
	 * @param referenciaContra the valor to set
	 */
	public void setReferenciaContra(String referenciaContra) {
		this.referenciaContra = referenciaContra;
	}
	/**
	 * @param baseGravable the baseGravable to set
	 */
	public void setBaseGravable(String baseGravable) {
		this.baseGravable = baseGravable;
	}
	/**
	 * @param numeroDocumento the numeroDocumento to set
	 */
	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}
	/**
	 * @param tercero the tercero to set
	 */
	public void setTercero(String tercero) {
		this.tercero = tercero;
	}
	/**
	 * @param sucursal the sucursal to set
	 */
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	/**
	 * @param centroCosto the centroCosto to set
	 */
	public void setCentroCosto(String centroCosto) {
		this.centroCosto = centroCosto;
	}
	/**
	 * @param auxiliar the auxiliar to set
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar = auxiliar;
	}
//	/**
//	 * @param cuentaContra the cuentaContra to set
//	 */
//	public void setCuentaContra(String cuentaContra) {
//		this.cuentaContra = cuentaContra;
//	}
	/**
	 * @param columnaTexto the columnaTexto to set
	 */
	public void setColumnaTexto(String columnaTexto) {
		this.columnaTexto = columnaTexto;
	}
	/**
	 * @param numeroContrato the numeroContrato to set
	 */
	public void setNumeroContrato(String numeroContrato) {
		this.numeroContrato = numeroContrato;
	}
	/**
	 * @param auxiliarContra the auxiliarContra to set
	 */
	public void setAuxiliarContra(String auxiliarContra) {
		this.auxiliarContra = auxiliarContra;
	}
	/**
	 * @param tipoContrato the tipoContrato to set
	 */
	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}
	/**
	 * @param filaFin the filaFin to set
	 */
	public void setFilaFin(int filaFin) {
		this.filaFin = filaFin;
	}
	/**
	 * @param texto the texto to set
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}
	/**
	 * @return the consecutivo
	 */
	public String getConsecutivo() {
		return consecutivo;
	}
	/**
	 * @param consecutivo the consecutivo to set
	 */
	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}
	/**
	 * @return the porTercero
	 */
	public boolean isPorTercero() {
		return porTercero;
	}
	/**
	 * @return the ckAgrupado
	 */
	public boolean isCkAgrupado() {
		return ckAgrupado;
	}
	/**
	 * @return the ckRetenciones
	 */
	public boolean isCkRetenciones() {
		return ckRetenciones;
	}
	/**
	 * @return the ckAplicaAux
	 */
	public boolean isCkAplicaAux() {
		return ckAplicaAux;
	}
	/**
	 * @param porTercero the porTercero to set
	 */
	public void setPorTercero(boolean porTercero) {
		this.porTercero = porTercero;
	}
	/**
	 * @param ckAgrupado the ckAgrupado to set
	 */
	public void setCkAgrupado(boolean ckAgrupado) {
		this.ckAgrupado = ckAgrupado;
	}
	/**
	 * @param ckRetenciones the ckRetenciones to set
	 */
	public void setCkRetenciones(boolean ckRetenciones) {
		this.ckRetenciones = ckRetenciones;
	}
	/**
	 * @param ckAplicaAux the ckAplicaAux to set
	 */
	public void setCkAplicaAux(boolean ckAplicaAux) {
		this.ckAplicaAux = ckAplicaAux;
	}

	/**
	 * Retorna el objeto contArchivocargarPlantilla
	 * 
	 * @return contArchivocargarPlantilla
	 */
	public UploadedFile getArchivoCargacargarPlantilla() {
		return archivoCargacargarPlantilla;
	}
	/**
	 * Asigna el objeto contArchivocargarPlantilla
	 * 
	 * @param contArchivocargarPlantilla
	 * Variable a asignar en contArchivocargarPlantilla
	 */
	public void setArchivoCargacargarPlantilla(UploadedFile archivoCargacargarPlantilla) {
		this.archivoCargacargarPlantilla = archivoCargacargarPlantilla;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
