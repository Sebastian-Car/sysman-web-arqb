/*-
 * ListadosBancosControlador.java
 *
 * 1.0
 * 
 * 12/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.enums.ListadosBancosControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario "Listados para Bancos" en
 * Access "LISTADOS_BANCOS", el cual es llamado desde Panel
 * Principal\Nomina\Informes\Planillas\Devengos\Pago Bancos
 *
 * @version 1.0, 12/01/2018
 * @author amonroy
 */
@ManagedBean
@ViewScoped
public class ListadosBancosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante que almacena el codigo del modulo al cual accede el usuario
	 */
	private final String modulo;
	/**
	 * Constante que almacena el valor "800.091.594-4", el cual corresponde al Nit
	 * de la entidad para la cual se genera la plantilla
	 * "RELACION_PAGOS_NOMINA_EXCEL"
	 */
	private final String cNitCompania;
	/**
	 * Contante que almacena el texto "s$anio$s" el cual es un valor que se envia
	 * como reempalzo a las consultas
	 */
	private final String cReemplazoAnio;

	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo que almacena el codigo del "Tipo de Informe" seleccionado en el
	 * formulario
	 */
	private int tipoInforme;
	/**
	 * Atributo que almacena el anio de trabajo con el que se desea generar el
	 * informe
	 */
	private String anio;
	/**
	 * Atributo que almacena el mes de trabajo con el que se desea generar el
	 * informe
	 */
	private String mes;
	/**
	 * Atributo que almacena el periodo de trabajo con el que se desea generar el
	 * informe
	 */
	private String periodo;
	/**
	 * Atributo que almacena el codigo del proceso que ha sido seleccionado al
	 * ingresar al modulo de Nomina
	 */
	private String idProceso;
	/**
	 * Atributo que almacena la fecha seleccionada en el campo "Fecha Elaboracion",
	 * de la seccion "Relación de Nómina Excel "
	 */
	private Date fechaElaboracion;
	/**
	 * Atributo que almacena el valor ingresado en el campo "Concepto Pago"
	 */
	private String conceptoPago;
	/**
	 * Implementacion del EJB de EjbNominaCeroRemote para hacer el llamado a las
	 * funciones que se invocan dentro del Controlador y se encuentran almacenadas
	 * en el paquete PCK_NOMINA
	 */
	@EJB
	private EjbNominaCeroRemote ejbNominaCero;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * Este atributo se usa como auxiliar del componente selector de archivos
	 * ArchivoBase y funciona como contenedor del archivo que se debe guardar
	 */
	private ContenedorArchivo contArchivoArchivoBase;
	/**
	 * Implementacion del EJB de SysmanUtil para acceder a funciones y/o
	 * procedimientos definidos en el paquete PCK_SYSMAN_UTL
	 */
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Indica si el boton de "Informe Banco Occidente" ha sido oprimido
	 */
	private String headerEspecial;

	private boolean bancoOccidente;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	/**
	 * Listado de registros para el combo de anio
	 */
	private List<Registro> listaAnio;
	/**
	 * Listado de registros para el combo de mes
	 */
	private List<Registro> listaMes;
	/**
	 * Listado de registros para el combo de Periodo
	 */
	private List<Registro> listaPeriodo;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de ListadosBancosControlador
	 */
	public ListadosBancosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		cNitCompania = "800.091.594-4";
		cReemplazoAnio = "s$anio$s";
		try {
			numFormulario = GeneralCodigoFormaEnum.LISTADOS_BANCOS_CONTROLADOR.getCodigo();// 1592
			validarPermisos();
			// <INI_ADICIONAL>
			anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "").toString();

			mes = SysmanFunciones.nvl(SessionUtil.getSessionVar("mesNomina"), "").toString();

			periodo = SysmanFunciones.nvl(SessionUtil.getSessionVar("periodoNomina"), "").toString();

			idProceso = SysmanFunciones.nvl(SessionUtil.getSessionVar("procesoNomina"), "").toString();
			tipoInforme = 1;
			fechaElaboracion = new Date();
			conceptoPago = obtenerNombreConcepto();
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
			contArchivoArchivoBase = new ContenedorArchivo();
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
		cargarListaAnio();
		cargarListaMes();
		cargarListaPeriodo();
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
	/**
	 * 
	 * Carga la lista listaAnio
	 *
	 */
	public void cargarListaAnio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

		try {
			listaAnio = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ListadosBancosControladorUrlEnum.URL5765.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaMes
	 *
	 */
	public void cargarListaMes() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);

		try {
			listaMes = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ListadosBancosControladorUrlEnum.URL6657.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaPeriodo
	 *
	 */
	public void cargarListaPeriodo() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), idProceso);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.MES.getName(), mes);

		try {
			listaPeriodo = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ListadosBancosControladorUrlEnum.URL8051.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnPdf en la vista
	 *
	 * Hace el llamado al metodo "generarInforme" indicando el formato con el que se
	 * desea generar el informe
	 */
	public void oprimirBtnPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel en la vista
	 *
	 * Hace el llamado al metodo "generarInforme" indicando el formato con el que se
	 * desea generar el informe
	 *
	 */
	public void oprimirBtnExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnBancoOccidente en la vista
	 *
	 * Hace el llamado al metodo "generarInforme" indicando el formato con el que se
	 * desea generar el informe
	 *
	 */
	public void oprimirBtnBancoOccidente() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		bancoOccidente = true;
		generarInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Asigna la informacion relacionada con el encabeado del informe
	 * 
	 * @param sheet         Hoja en la que se esta trabajando
	 * @param conRetefuente Permite identifivar si la hoja que se esta trabajando es
	 *                      "NOMINA_CON_RETE_FUENTE"
	 */
	private void completarEncabezadoInforme(Sheet sheet, boolean conRetefuente) {

		Cell cell1 = sheet.getRow(5).getCell(0);
		try {
			// codigo que aplica para la implentacion de marca blanca
			String msj = idioma.getString("TB_TB3947").replace("s$nombreEmpresa$s",
					ejbNominaCero.getDatoEmpresa(compania, 0));
			msj = msj.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
			cell1.setCellValue(msj);
			cell1 = sheet.getRow(5).getCell(conRetefuente ? 9 : 7);
			cell1.setCellValue(SysmanFunciones.convertirAFechaCadena(new Date()));
			cell1 = sheet.getRow(7).getCell(0);
			cell1.setCellValue(idioma.getString("TB_TB3948")
					.replace("s$nombreMes$s", ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes)))
					.replace(cReemplazoAnio, anio));
			cell1 = sheet.getRow(9).getCell(0);
			cell1.setCellValue(idioma.getString("TB_TB3949")
					.replace("s$fecha$s", SysmanFunciones.convertirAFechaCadena(new Date()))
					.replace("s$hora$s", SysmanFunciones.convertirAHoraCadena(new Date())));
			cell1 = sheet.getRow(2006).getCell(conRetefuente ? 3 : 2);
			cell1.setCellValue(consultarParametro("NOMBRE DEL JEFE DE RECURSOS HUMANOS", ""));
			cell1 = sheet.getRow(2007).getCell(conRetefuente ? 3 : 2);
			cell1.setCellValue(consultarParametro("CARGO JEFE RECURSOS HUMANOS", ""));
		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Realiza el llenado de la informacion en la hoja "NOMINA_SIN_RETE_FUENTE" o
	 * "NOMINA_PENSIONADOS_SIN_CTA"
	 * 
	 * @param workbook      Instancia del libro con el que se esta trabajando
	 * @param sheet         Hoja actual que se va a completar
	 * @param reemplazos    Estructura de datos que almacena los valores de
	 *                      reemplazo a enviar en la consulta que trae la
	 *                      informacion que se plasmara en la hoja de datos
	 * @param sinRetefuente Como la estructura de la plantilla para las hojas
	 *                      "NOMINA_SIN_RETE_FUENTE" y "NOMINA_PENSIONADOS_SIN_CTA"
	 *                      son iguales, se envia un indicador para saber en que
	 *                      hoja se esta trabajando para cambiar el nombre de la
	 *                      consulta
	 */
	private void asignarDatosSinRetefuente(Workbook workbook, Sheet sheet, Map<String, Object> reemplazos,
			boolean sinRetefuente) {
		String strSql = Reporteador.resuelveConsulta(
				sinRetefuente ? "800131RelacionPagosNominaSinRetefuente" : "800131RelacionPagosNominaPensionados",
				Integer.parseInt(SessionUtil.getModulo()), reemplazos);

		List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);

		/*
		 * Definicion estilo de la celda
		 * 
		 * styleR:Alineacion de la columna a la derecha
		 * 
		 * styleL:Alineacion de la columna a la izquierda
		 */
		CellStyle styleR = workbook.createCellStyle();
		styleR.setBorderTop(CellStyle.BORDER_THIN);
		styleR.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setBorderLeft(CellStyle.BORDER_THIN);
		styleR.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setBorderRight(CellStyle.BORDER_THIN);
		styleR.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setBorderBottom(CellStyle.BORDER_THIN);
		styleR.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setAlignment(CellStyle.ALIGN_RIGHT);

		CellStyle styleL = workbook.createCellStyle();
		styleL.setBorderTop(CellStyle.BORDER_THIN);
		styleL.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setBorderLeft(CellStyle.BORDER_THIN);
		styleL.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setBorderRight(CellStyle.BORDER_THIN);
		styleL.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setBorderBottom(CellStyle.BORDER_THIN);
		styleL.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setAlignment(CellStyle.ALIGN_LEFT);

		/// Paso de Datos
		int rowNum = 12;
		if (!rs.isEmpty()) {
			for (int i = 0; i < rs.size(); i++) {

				Row row = sheet.getRow(rowNum);
				Cell nCell = row.getCell(0);

				int consecutivo = i + 1;
				nCell.setCellValue(consecutivo);
				nCell.setCellStyle(styleR);

				nCell = row.createCell(1);
				nCell.setCellValue(retornarString(rs.get(i), GeneralParameterEnum.NUMERO_DCTO.getName()));
				nCell.setCellStyle(styleR);

				Cell totalCell = row.createCell(2);
				totalCell.setCellType(Cell.CELL_TYPE_NUMERIC);
				totalCell.setCellValue(retornarDoble(rs.get(i), GeneralParameterEnum.TOTAL.getName()));
				totalCell.setCellStyle(styleR);

				nCell = row.createCell(3);
				nCell.setCellValue(retornarString(rs.get(i), "NOMBRECOMPLETO"));
				nCell.setCellStyle(styleL);

				nCell = row.createCell(4);
				nCell.setCellValue(retornarString(rs.get(i), "NOMBREBANCO"));
				nCell.setCellStyle(styleL);

				nCell = row.createCell(5);
				nCell.setCellValue(retornarString(rs.get(i), "TIPOCUENTA"));
				nCell.setCellStyle(styleL);

				nCell = row.createCell(6);
				nCell.setCellValue(retornarString(rs.get(i), GeneralParameterEnum.CUENTA.getName()));
				nCell.setCellStyle(styleR);

				nCell = row.createCell(7);
				nCell.setCellValue(retornarString(rs.get(i), GeneralParameterEnum.CONCEPTO.getName()));
				nCell.setCellStyle(styleL);

				rowNum++;
			}
		}

		// Elimina las filas que se encuentran en blanco partiendo de
		// la ultima fila que posee información
		for (int i = rowNum; i < 2000; i++) {
			eliminarFila(sheet, rowNum);
		}

		workbook.setActiveSheet(workbook.getSheetIndex(sheet));
		workbook.setForceFormulaRecalculation(true);
	}

	/**
	 * Realiza el llenado de la informacion en la hoja "NOMINA_CON_RETE_FUENTE"
	 * 
	 * @param workbook   Instancia del libro con el que ha sido cargado
	 * @param sheet      Hoja actual en la que se completara la informacion
	 * @param reemplazos Estructura de datos que almacena los valores de reemplazo a
	 *                   enviar en la consulta que trae la informacion que se
	 *                   plasmara en la hoja de datos
	 */
	private void asignarDatosConRetefuente(Workbook workbook, Sheet sheet, Map<String, Object> reemplazos) {
		String strSql = Reporteador.resuelveConsulta("800131RelacionPagosNominaConRetefuente",
				Integer.parseInt(SessionUtil.getModulo()), reemplazos);

		List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);

		/*
		 * Definicion estilo de la celda
		 * 
		 * styleR:Alineacion de la columna a la derecha
		 * 
		 * styleL:Alineacion de la columna a la izquierda
		 */
		CellStyle styleR = workbook.createCellStyle();
		styleR.setBorderTop(CellStyle.BORDER_THIN);
		styleR.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setBorderLeft(CellStyle.BORDER_THIN);
		styleR.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setBorderRight(CellStyle.BORDER_THIN);
		styleR.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setBorderBottom(CellStyle.BORDER_THIN);
		styleR.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleR.setAlignment(CellStyle.ALIGN_RIGHT);

		CellStyle styleL = workbook.createCellStyle();
		styleL.setBorderTop(CellStyle.BORDER_THIN);
		styleL.setTopBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setBorderLeft(CellStyle.BORDER_THIN);
		styleL.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setBorderRight(CellStyle.BORDER_THIN);
		styleL.setRightBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setBorderBottom(CellStyle.BORDER_THIN);
		styleL.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleL.setAlignment(CellStyle.ALIGN_LEFT);

		/// Paso de Datos
		int rowNum = 12;
		if (!rs.isEmpty()) {
			for (int i = 0; i < rs.size(); i++) {

				Row row = sheet.getRow(rowNum);
				Cell nCell = row.getCell(0);

				int consecutivo = i + 1;
				nCell.setCellValue(consecutivo);
				nCell.setCellStyle(styleR);

				nCell = row.createCell(1);
				nCell.setCellValue(retornarString(rs.get(i), GeneralParameterEnum.NUMERO_DCTO.getName()));
				nCell.setCellStyle(styleR);

				Cell retFteCell = row.createCell(3);
				retFteCell.setCellType(Cell.CELL_TYPE_NUMERIC);
				retFteCell.setCellValue(retornarDoble(rs.get(i), "RETFTE"));
				retFteCell.setCellStyle(styleR);

				Cell totalCell = row.createCell(4);
				totalCell.setCellType(Cell.CELL_TYPE_NUMERIC);
				totalCell.setCellValue(retornarDoble(rs.get(i), GeneralParameterEnum.TOTAL.getName()));
				totalCell.setCellStyle(styleR);

				nCell = row.createCell(5);
				nCell.setCellValue(retornarString(rs.get(i), "NOMBRECOMPLETO"));
				nCell.setCellStyle(styleL);

				nCell = row.createCell(6);
				nCell.setCellValue(retornarString(rs.get(i), "NOMBREBANCO"));
				nCell.setCellStyle(styleL);

				nCell = row.createCell(7);
				nCell.setCellValue(retornarString(rs.get(i), "TIPOCUENTA"));
				nCell.setCellStyle(styleL);

				nCell = row.createCell(8);
				nCell.setCellValue(retornarString(rs.get(i), GeneralParameterEnum.CUENTA.getName()));
				nCell.setCellStyle(styleR);

				nCell = row.createCell(9);
				nCell.setCellValue(retornarString(rs.get(i), GeneralParameterEnum.CONCEPTO.getName()));
				nCell.setCellStyle(styleL);

				rowNum++;
			}
		}

		// Elimina las filas que se encuentran en blanco partiendo de
		// la ultima fila que posee información
		for (int i = rowNum; i < 2000; i++) {
			eliminarFila(sheet, rowNum);
		}

		workbook.setActiveSheet(workbook.getSheetIndex(sheet));
		workbook.setForceFormulaRecalculation(true);
	}

	/**
	 * Elimina una fila de una Hoja de Datos
	 * 
	 * @param sheet  La hoja de datos en la que se eliminará la fila
	 * @param rowNum Número de la fila que se desea eliminar
	 */
	private void eliminarFila(Sheet sheet, int rowNum) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowNum >= 0 && rowNum < lastRowNum) {
			sheet.shiftRows(rowNum + 1, lastRowNum, -1);
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnRelacionExcel en la vista
	 *
	 * Asigna informacion a la plantilla que ha sido seleccionada; La hoja
	 * "NOMINA_SIN_RETE_FUENTE" es completada para cualquier entidad, mientas que
	 * las hojas "NOMINA_CON_RETE_FUENTE" y "NOMINA_PENSIONADOS_SIN_CTA" han sido
	 * condicionadas para la entidad con Nit: 800.091.594-4
	 *
	 */
	public void oprimirBtnRelacionExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (contArchivoArchivoBase.getArchivo() == null) {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
			return;
		}

		String rutaArchivo = contArchivoArchivoBase.getArchivo().getPath();

		String extension = rutaArchivo.substring(rutaArchivo.indexOf('.'), rutaArchivo.length());

		try (FileInputStream file = new FileInputStream(new File(rutaArchivo));) {

			Workbook workbook = null;

			if (".xlsx".equals(extension)) {
				workbook = new XSSFWorkbook(file);
			} else {
				workbook = new HSSFWorkbook(file);
			}

			// Definicion de parametros de consulta
			Map<String, Object> reemplazos = new HashMap<>();
			reemplazos.put("conceptoPago", conceptoPago);
			reemplazos.put("idProceso", idProceso);
			reemplazos.put("anio", anio);
			reemplazos.put("mes", mes);
			reemplazos.put("periodo", periodo);
			reemplazos.put("condicionConcepto",
					cNitCompania.equals(SessionUtil.getCompaniaIngreso().getNit())
							? " AND HISTORICOS.ID_DE_CONCEPTO IN (144,125) "
							: " AND HISTORICOS.ID_DE_CONCEPTO IN (144) ");
			reemplazos.put("reemplazoHaving", definirReemplazoHaving());

			// sinRetefuente
			Sheet sheet = workbook.getSheet("NOMINA_SIN_RETE_FUENTE");
			completarEncabezadoInforme(sheet, false);
			asignarDatosSinRetefuente(workbook, sheet, reemplazos, true);

			if (cNitCompania.equals(SessionUtil.getCompaniaIngreso().getNit()) && "001".equals(compania)) {
				// con retefuente
				sheet = workbook.getSheet("NOMINA_CON_RETE_FUENTE");
				completarEncabezadoInforme(sheet, true);
				asignarDatosConRetefuente(workbook, sheet, reemplazos);
			}

			if (cNitCompania.equals(SessionUtil.getCompaniaIngreso().getNit()) && "002".equals(compania)) {
				// Pensionados
				sheet = workbook.getSheet("NOMINA_PENSIONADOS_SIN_CTA");
				completarEncabezadoInforme(sheet, false);
				asignarDatosSinRetefuente(workbook, sheet, reemplazos, false);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			out.close();
			workbook.close();

			String nombreArchivo = idioma.getString("TB_TB3951");
			nombreArchivo = nombreArchivo.replace(cReemplazoAnio, anio);
			nombreArchivo = nombreArchivo.replace("s$mes$s", mes);
			nombreArchivo = nombreArchivo.replace("s$periodo$s", periodo);
			nombreArchivo = nombreArchivo.replace("s$extension$s", extension);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()), nombreArchivo);
		} catch (IOException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	private String definirReemplazoHaving() {
		return cNitCompania.equals(SessionUtil.getCompaniaIngreso().getNit())
				? " HAVING  SUM( CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN (125) THEN VALOR ELSE 0 END) IN (0)  "
				: " ";
	}

	/**
	 * Define las acciones necesarias para generar el informe realiza el reemplazo
	 * de valores en la consulta del informe y envía los parámetros definidos
	 * 
	 * @param formato Formato seleccionado por el usuario para generar el informe
	 */
	private void generarInforme(FORMATOS formato) {
		try {

			headerEspecial = ejbSysmanUtil.consultarParametro(compania, "FORMATOS ESPECIALES BUCARAMANGA", modulo,
					new Date(), true);

			String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

			String informe = definirInforme();
			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			Map<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("compania", compania);
			reemplazar.put("proceso", idProceso);
			reemplazar.put("anio", anio);
			reemplazar.put("mes", mes);
			reemplazar.put("periodo", periodo);

			// PARAMETROS PARA GENERACION DE INFORME
			Map<String, Object> parametros = new HashMap<>();

			boolean mostrarFND = SessionUtil.getCompaniaIngreso().getNit().equals("800.244.322-6") ? true : false;

			parametros.put("PR_MOSTRAR_FND", mostrarFND);
			parametros.put("PR_NOMBRE_DEL_GERENTE",
					ejbSysmanUtil.consultarParametro(compania, "NOMBRE DEL GERENTE", modulo, new Date(), true));
			parametros.put("PR_CARGO_DEL_GERENTE",
					ejbSysmanUtil.consultarParametro(compania, "CARGO DEL GERENTE", modulo, new Date(), true));
			parametros.put("PR_NOMBRE_TESORERO_PAGADOR", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DEL CARGO TESORERO PAGADOR", modulo, new Date(), true));
			parametros.put("PR_CARGO_TESORERO_PAGADOR",
					ejbSysmanUtil.consultarParametro(compania, "CARGO DEL TESORERO PAGADOR", modulo, new Date(), true));
			parametros.put("PR_NOMBRE_QUIEN_AUTORIZA_NOMINA", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true));
			parametros.put("PR_CARGO_QUIEN_AUTORIZA_NOMINA", ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN AUTORIZA NOMINA", modulo, new Date(), true));
			parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE JEFE DE PRESUPUESTO", modulo, new Date(), true));
			parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO", ejbSysmanUtil.consultarParametro(compania,
					"CARGO DEL JEFE DE PRESUPUESTO", modulo, new Date(), true));
			parametros.put("PR_NOMBRE_QUIEN_REVISA_NOMINA", ejbSysmanUtil.consultarParametro(compania,
					"NOMBRE DE QUIEN REVISA NOMINA", modulo, new Date(), true));
			parametros.put("PR_CARGO_QUIEN_REVISA_NOMINA", ejbSysmanUtil.consultarParametro(compania,
					"CARGO DE QUIEN REVISA NOMINA", modulo, new Date(), true));

			parametros.put("PR_NOMBREEMPRESA", SessionUtil.getCompaniaIngreso().getNombre());

			parametros.put("PR_ANIO", anio);
			parametros.put("PR_NOMBREMES", ejbSysmanUtil.mostrarNombreDeMes(Integer.parseInt(mes)));

			parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI") ? true : false);
			parametros.put("PR_IMAGEN_ESPECIAL", sticker);

			parametros.put("PR_PERIODO", periodo);

			String jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE DESARROLLO HUMANO",
					modulo, new Date(), false);
			String jefeNomina = ejbSysmanUtil.consultarParametro(compania, "NOMBRE JEFE NOMINA", modulo, new Date(),
					false);
			String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
					"CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false);
			String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania, "CARGO RESPONSABLE DE NOMINA",
					modulo, new Date(), false);

			parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
			parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
			parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
			parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);

			if (informe.toUpperCase().contains("PAIPA")) {
				parametros.put("PR_PAIPA", true);
				informe = "001661ListadoBancos";
			}

			Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(informe, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (JRException | IOException | SysmanException | NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			bancoOccidente = false;
		}

	}

	/**
	 * Obtiene el valor almacenado en la base de datos para el parametro ingresado.
	 * 
	 * @param nombreParametro Nombre del parametro a consultar en la base de datos.
	 * @param valorDefault    Valor por omision en caso de nulo.
	 * @return valor asignado al parametro
	 */
	private String consultarParametro(String nombreParametro, String valorDefault) {
		String parametro = null;
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania, nombreParametro, modulo, new Date(), false);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return parametro != null ? parametro : valorDefault;
	}

	/**
	 * Define el informe a generar de acuerdo al boton que ha sido oprimido en el
	 * formulario
	 * 
	 * @return nombre del informe a generar
	 */
	private String definirInforme() {
		String informeBancos = consultarParametro("FORMATO LISTADO BANCOS", "001661ListadoBancos");

		String informe;

		if (tipoInforme == 3) {
			informe = "002289ListadoBancosFuenteyProyectoCB";
		}
			else {
			informe = tipoInforme == 1 ? informeBancos : "001660ListadoBancosCheques";
			}
			
		
		if (bancoOccidente) {
			informe = "001662ListadoBancosOccidente";
		}
		return informe;
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Anio
	 * 
	 * Recarga el listado de refistro para el seleccionar el mes
	 * 
	 */
	public void cambiarAnio() {
		// <CODIGO_DESARROLLADO>
		mes = periodo = null;
		cargarListaMes();
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * Metodo ejecutado al cambiar el control Mes
	 * 
	 * Recarga el listado de refistro para el seleccionar el periodo
	 * 
	 */
	public void cambiarMes() {
		// <CODIGO_DESARROLLADO>
		periodo = null;
		cargarListaPeriodo();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	/**
	 * Realiza los reemplzos de anio y mes en el Texto en Bean TB_TB3902
	 * 
	 * @return Cadena con los valores de reemplazo
	 */
	private String obtenerNombreConcepto() {
		String concepto = idioma.getString("TB_TB3931").replace(cReemplazoAnio, anio);
		concepto = concepto.replace("s$mes$s", mes);
		return concepto;
	}

	/**
	 * Evalua si el campo ingresado por parametro se encuentra nulo dentro del
	 * registro que tambien ha sido ingresado por parametro
	 * 
	 * @param reg   Registro en el que se desea evaluar el campo
	 * @param campo Campo que se desea consultar
	 * @return Cadena vacia o el valor del campo
	 */
	private String retornarString(Registro reg, String campo) {
		return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "" : reg.getCampos().get(campo).toString();
	}

	/**
	 * Evalua si el campo ingresado por parametro se encuentra nulo dentro del
	 * registro que tambien ha sido ingresado por parametro
	 * 
	 * @param rs    Registro en el que se desea evaluar el campo
	 * @param campo Campo que se desea consultar
	 * @return
	 */
	private double retornarDoble(Registro rs, String campo) {
		return SysmanFunciones.validarCampoVacio(rs.getCampos(), campo) ? 0
				: Double.parseDouble(rs.getCampos().get(campo).toString());
	}

	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoInforme
	 * 
	 * @return tipoInforme
	 */
	public int getTipoInforme() {
		return tipoInforme;
	}

	/**
	 * Asigna la variable tipoInforme
	 * 
	 * @param tipoInforme Variable a asignar en tipoInforme
	 */
	public void setTipoInforme(int tipoInforme) {
		this.tipoInforme = tipoInforme;
	}

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
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable periodo
	 * 
	 * @return periodo
	 */
	public String getPeriodo() {
		return periodo;
	}

	/**
	 * Asigna la variable periodo
	 * 
	 * @param periodo Variable a asignar en periodo
	 */
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna la variable fechaElaboracion
	 * 
	 * @return fechaElaboracion
	 */
	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	/**
	 * Asigna la variable fechaElaboracion
	 * 
	 * @param fechaElaboracion Variable a asignar en fechaElaboracion
	 */
	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	/**
	 * Retorna la variable conceptoPago
	 * 
	 * @return conceptoPago
	 */
	public String getConceptoPago() {
		return conceptoPago;
	}

	/**
	 * Asigna la variable conceptoPago
	 * 
	 * @param conceptoPago Variable a asignar en conceptoPago
	 */
	public void setConceptoPago(String conceptoPago) {
		this.conceptoPago = conceptoPago;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna el objeto contArchivoArchivoBase
	 * 
	 * @return contArchivoArchivoBase
	 */
	public ContenedorArchivo getContArchivoArchivoBase() {
		return contArchivoArchivoBase;
	}

	/**
	 * Asigna el objeto contArchivoArchivoBase
	 * 
	 * @param contArchivoArchivoBase Variable a asignar en contArchivoArchivoBase
	 */
	public void setContArchivoArchivoBase(ContenedorArchivo contArchivoArchivoBase) {
		this.contArchivoArchivoBase = contArchivoArchivoBase;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAnio
	 * 
	 * @return listaAnio
	 */
	public List<Registro> getListaAnio() {
		return listaAnio;
	}

	/**
	 * Asigna la lista listaAnio
	 * 
	 * @param listaAnio Variable a asignar en listaAnio
	 */
	public void setListaAnio(List<Registro> listaAnio) {
		this.listaAnio = listaAnio;
	}

	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}

	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes Variable a asignar en listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}

	/**
	 * Retorna la lista listaPeriodo
	 * 
	 * @return listaPeriodo
	 */
	public List<Registro> getListaPeriodo() {
		return listaPeriodo;
	}

	/**
	 * Asigna la lista listaPeriodo
	 * 
	 * @param listaPeriodo Variable a asignar en listaPeriodo
	 */
	public void setListaPeriodo(List<Registro> listaPeriodo) {
		this.listaPeriodo = listaPeriodo;
	}

	public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
