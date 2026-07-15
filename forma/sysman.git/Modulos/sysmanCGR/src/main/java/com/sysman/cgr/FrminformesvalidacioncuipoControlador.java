/*-
 * FrminformesvalidacioncuipoControlador.java
 *
 * 1.0
 * 
 * 01/07/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.FrminformesvalidacioncuipoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
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
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/07/2022
 * @author ljdiaz
 */
@ManagedBean
@ViewScoped
public class FrminformesvalidacioncuipoControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String trimestre;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String reporte;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	/**
	 * lista del reporte 1. PLAN PRESUPUESTAL
	 */
	private List<Registro> listaRerpote1;
	/**
	 * lista del reporte 2. CONFIGURACIÓN CLASIFICADORES EN PLAN
	 */
	private List<Registro> listaRerpote2;
	/**
	 * lista del reporte 3. CLASIFICADORES EN CADENA PRESUPUESTAL
	 */
	private List<Registro> listaRerpote3;
	/**
	 * lista del reporte 4. CLASIFICADORES POR MOVIMIENTO gastos
	 */
	private List<Registro> listaRerpote4Gastos;
	/**
	 * lista del reporte 4. CLASIFICADORES POR MOVIMIENTO ingresos
	 */
	private List<Registro> listaRerpote4Ingresos;
	/**
	 * lista del reporte 5. CONFIGURACIÓN CLASIFICADOR PADRE - HIJO
	 */
	private List<Registro> listaRerpote5;
	
//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaano;

//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrminformesvalidacioncuipoControlador
	 */
	public FrminformesvalidacioncuipoControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = 2362;
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
		cargarListaano();
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
	 * Carga la lista listaano
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaano() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaano = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrminformesvalidacioncuipoControladorUrlEnum.URL3043.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 * @throws IOException 
	 *
	 */
	public void oprimirExcel() throws IOException {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			Map<String, Object> param = new HashMap<>();
			param.put("COMPANIA", compania);
			param.put("ANO", String.valueOf(SysmanFunciones.ano(new Date())));
			//tirmestres seleccionado
			if(trimestre.equals("1")) {
				param.put("FECHA_INICIAL", "01/01/"+ano);
				param.put("FECHA_FINAL", "30/03/"+ano);
			}else if(trimestre.equals("2")) {
				param.put("FECHA_INICIAL","01/04/"+ano);
				param.put("FECHA_FINAL", "30/06/"+ano);
			}else if(trimestre.equals("3")) {
				param.put("FECHA_INICIAL", "01/07/"+ano);
				param.put("FECHA_FINAL", "30/09/"+ano);
			}else if(trimestre.equals("4")) {
				param.put("FECHA_INICIAL", "01/10/"+ano);
				param.put("FECHA_FINAL", "31/12/"+ano);
			}
			//reporte 1 PLAN PRESUPUESTAL
			if(reporte.equals("1")) {
				
				listaRerpote1 = RegistroConverter
	 					.toListRegistro(
	 							requestManager.getList(
	 									UrlServiceUtil.getInstance()
	 									.getUrlServiceByUrlByEnumID(
	 											FrminformesvalidacioncuipoControladorUrlEnum.URL0001.getValue())
	 									.getUrl(),
	 									param));
				// se agrega el reportes a la hoja del archivo
				addReporte1(workbook, listaRerpote1, 'A', "PLAN PRESUPUESTAL", "Informe Validacion - PLAN PRESUPUESTAL");
			}else if(reporte.equals("2")) {//reporte 2 CONFIGURACIÓN CLASIFICADORES EN PLAN
				listaRerpote2 = RegistroConverter
	 					.toListRegistro(
	 							requestManager.getList(
	 									UrlServiceUtil.getInstance()
	 									.getUrlServiceByUrlByEnumID(
	 											FrminformesvalidacioncuipoControladorUrlEnum.URL0002.getValue())
	 									.getUrl(),
	 									param));
				// se agrega el reporte a la hoja del archvivo 
				addReporte2(workbook, listaRerpote2, 'A', "CONFIGURACIÓN CLASIFICADORES EN PLAN", "Informe Validacion - Configuraciones CLASIFICADORES EN PLAN");
			}else if(reporte.equals("3")) {
				//reporte 3
				listaRerpote3 = RegistroConverter
	 					.toListRegistro(
	 							requestManager.getList(
	 									UrlServiceUtil.getInstance()
	 									.getUrlServiceByUrlByEnumID(
	 											FrminformesvalidacioncuipoControladorUrlEnum.URL0003.getValue())
	 									.getUrl(),
	 									param));
				
				addReporte3(workbook, listaRerpote3, 'A', "CLASIFICADORES EN CADENA PRESUPUESTAL", "Informe Validacion - CLASIFICADORES EN CADENA PRESUPUESTAL");
			}else if(reporte.equals("4")) {
				//reporte 4 CLASIFICADORES POR MOVIMIENTO gastos
				listaRerpote4Gastos =  RegistroConverter
	 					.toListRegistro(
	 							requestManager.getList(
	 									UrlServiceUtil.getInstance()
	 									.getUrlServiceByUrlByEnumID(
	 											FrminformesvalidacioncuipoControladorUrlEnum.URL0041.getValue())
	 									.getUrl(),
	 									param));
				addReporte4(workbook, listaRerpote4Gastos, 'A', "CLASIFICADORES POR MOVIMIENTO GASTOS", "Informe Validacion - CLASIFICADORES POR MOVIMIENTO GASTOS", 1);
				// ingresos
				listaRerpote4Ingresos =  RegistroConverter
	 					.toListRegistro(
	 							requestManager.getList(
	 									UrlServiceUtil.getInstance()
	 									.getUrlServiceByUrlByEnumID(
	 											FrminformesvalidacioncuipoControladorUrlEnum.URL0042.getValue())
	 									.getUrl(),
	 									param));
				addReporte4(workbook, listaRerpote4Ingresos, 'B', "CLASIFICADORES EN CADENA PRESUPUESTAL INGRESOS", "Informe Validacion - CLASIFICADORES EN CADENA PRESUPUESTAL INGRESOS", 2);
			}else if(reporte.equals("5")) {
				//reporte 5 CONFIGURACIÓN CLASIFICADOR PADRE - HIJO
				listaRerpote5 = RegistroConverter
	 					.toListRegistro(
	 							requestManager.getList(
	 									UrlServiceUtil.getInstance()
	 									.getUrlServiceByUrlByEnumID(
	 											FrminformesvalidacioncuipoControladorUrlEnum.URL0005.getValue())
	 									.getUrl(),
	 									param));
				addReporte5(workbook, listaRerpote5, 'A', "CONFIGURACIÓN CLASIFICADOR PADRE - HIJO", "Informe Validacion - Configuraciones PADRE - HIJO");
			}
			
			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"Informe de Validación.xls");

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		}
 		finally {
 			workbook.close();
 		}        
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	private static Name createName(Workbook workbook, String nameName) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		//name.setRefersToFormula(formula);
		return name;
	}
	public static void addReporte1(Workbook workbook, List<Registro> options, char column,
			String name,String Msj) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(Msj);

		row = optionsSheet.createRow(2);

		cell = row.createCell(0);
		cell.setCellValue("COMPANIA");

		cell = row.createCell(1);
		cell.setCellValue("ANO");

		cell = row.createCell(2);
		cell.setCellValue("CODIGO");

		cell = row.createCell(3);
		cell.setCellValue("REGALIAS");
		
		cell = row.createCell(4);
		cell.setCellValue("NATURALEZA");
		
		cell = row.createCell(5);
		cell.setCellValue("COD_CLASECLASIFICADOR");
		
		cell = row.createCell(6);
		cell.setCellValue("CLASECLASIFICADOR");
		
		cell = row.createCell(7);
		cell.setCellValue("COD_TIPOCLASIFICADOR");
		
		cell = row.createCell(8);
		cell.setCellValue("CLASIFICADOR");
		
		cell = row.createCell(9);
		cell.setCellValue("TIPOVIGENCIA");
		
		cell = row.createCell(10);
		cell.setCellValue("VIGENCIA");
		
		cell = row.createCell(11);
		cell.setCellValue("VIGENCIA_GASTO");
		
		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("COMPANIA").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("ANO").toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("REGALIAS").toString());
			
			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get("NATURALEZA").toString());
			
			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get("COD_CLASECLASIFICADOR").toString());
			
			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get("CLASECLASIFICADOR").toString());
			
			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get("COD_TIPOCLASIFICADOR").toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get("CLASIFICADOR").toString());
			
			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get("TIPOVIGENCIA").toString());
			
			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get("VIGENCIA").toString());
			
			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get("VIGENCIA_GASTO").toString());
		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
	}
	public static void addReporte2(Workbook workbook, List<Registro> options, char column,
			String name,String Msj) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(Msj);

		row = optionsSheet.createRow(2);

		cell = row.createCell(0);
		cell.setCellValue("ANO");

		cell = row.createCell(1);
		cell.setCellValue("RUBRO");

		cell = row.createCell(2);
		cell.setCellValue("NATURALEZA");

		cell = row.createCell(3);
		cell.setCellValue("ESREGALIAS");
		
		cell = row.createCell(4);
		cell.setCellValue("TIPOVIGENCIA");
		
		cell = row.createCell(5);
		cell.setCellValue("VIGENCIA");
		
		cell = row.createCell(6);
		cell.setCellValue("VIGENCIA_GASTO");
		
		cell = row.createCell(7);
		cell.setCellValue("SECTOR");
		
		cell = row.createCell(8);
		cell.setCellValue("SECTOR_NOMBRE");
		
		cell = row.createCell(9);
		cell.setCellValue("PROGRAMA");
		
		cell = row.createCell(10);
		cell.setCellValue("PROGRAMA_NOMBRE");
		
		cell = row.createCell(11);
		cell.setCellValue("SUBPROGRAMA");
		
		cell = row.createCell(12);
		cell.setCellValue("SUBPROGRAMA_NOMBRE");
		
		cell = row.createCell(13);
		cell.setCellValue("CODIGOPRODUCTO");
		
		cell = row.createCell(14);
		cell.setCellValue("CODIGOPRODUCTO_NOMBRE");
		
		cell = row.createCell(15);
		cell.setCellValue("CODIGOBPIN");
		
		cell = row.createCell(16);
		cell.setCellValue("CODIGOBPIN_NOMBRE");
		
		cell = row.createCell(17);
		cell.setCellValue("CODIGOCCPET");
		
		cell = row.createCell(18);
		cell.setCellValue("CODIGOCCPET_NOMBRE");
		
		cell = row.createCell(19);
		cell.setCellValue("CODIGOCPCDANE");
		
		cell = row.createCell(20);
		cell.setCellValue("CODIGOCPCDANE_NOMBRE");
		
		cell = row.createCell(21);
		cell.setCellValue("CODIGOUNIDADEJE");
		
		cell = row.createCell(22);
		cell.setCellValue("CODIGOUNIDADEJE_NOMBRE");
		
		cell = row.createCell(23);
		cell.setCellValue("CODIGOFUENTE");
		
		cell = row.createCell(24);
		cell.setCellValue("CODIGOFUENTE_NOMBRE");
		
		cell = row.createCell(25);
		cell.setCellValue("CODIGOCCPETREGA");
		
		cell = row.createCell(26);
		cell.setCellValue("CODIGOCCPETREGA_NOMBRE");
		
		cell = row.createCell(27);
		cell.setCellValue("CODIGOPOLITICA");
		
		cell = row.createCell(28);
		cell.setCellValue("CODIGOPOLITICA_NOMBRE");
		
		cell = row.createCell(29);
		cell.setCellValue("CODIGODETALLES");
		
		cell = row.createCell(30);
		cell.setCellValue("CODIGODETALLES_NOMBRE");
		
		cell = row.createCell(31);
		cell.setCellValue("SECTOR_CAN");
		
		cell = row.createCell(32);
		cell.setCellValue("PROGRAMA_CAN");
		
		cell = row.createCell(33);
		cell.setCellValue("SUBPROGRAMA_CAN");
		
		cell = row.createCell(34);
		cell.setCellValue("CODIGOPRODUCTO_CAN");
		
		cell = row.createCell(35);
		cell.setCellValue("CODIGOBPIN_CAN");
		
		cell = row.createCell(36);
		cell.setCellValue("CODIGOCCPET_CAN");
		
		cell = row.createCell(37);
		cell.setCellValue("CODIGOCPCDANE_CAN");
		
		cell = row.createCell(38);
		cell.setCellValue("CODIGOUNIDADEJE_CAN");
		
		cell = row.createCell(39);
		cell.setCellValue("CODIGOFUENTE_CAN");
		
		cell = row.createCell(40);
		cell.setCellValue("CODIGOCCPETREGA_CAN");
		
		cell = row.createCell(41);
		cell.setCellValue("CODIGOPOLITICA_CAN");
		
		cell = row.createCell(42);
		cell.setCellValue("CODIGODETALLES_CAN");
		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("ANO").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("RUBRO").toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("NATURALEZA").toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("ESREGALIAS").toString());
			
			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get("TIPOVIGENCIA").toString());
			
			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get("VIGENCIA").toString());

			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get("VIGENCIA_GASTO").toString());
			
			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get("SECTOR").toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get("SECTOR_NOMBRE").toString());
			
			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get("PROGRAMA").toString());
			
			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get("PROGRAMA_NOMBRE").toString());
			
			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA").toString());
			
			cell = row.createCell(12);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA_NOMBRE").toString());
			
			cell = row.createCell(13);
			cell.setCellValue(option.getCampos().get("CODIGOPRODUCTO").toString());
			
			cell = row.createCell(14);
			cell.setCellValue(option.getCampos().get("CODIGOPRODUCTO_NOMBRE").toString());
			
			cell = row.createCell(15);
			cell.setCellValue(option.getCampos().get("CODIGOBPIN").toString());
			
			cell = row.createCell(16);
			cell.setCellValue(option.getCampos().get("CODIGOBPIN_NOMBRE").toString());
			
			cell = row.createCell(17);
			cell.setCellValue(option.getCampos().get("CODIGOCCPET").toString());
			
			cell = row.createCell(18);
			cell.setCellValue(option.getCampos().get("CODIGOCCPET_NOMBRE").toString());
			
			cell = row.createCell(19);
			cell.setCellValue(option.getCampos().get("CODIGOCPCDANE").toString());
			
			cell = row.createCell(20);
			cell.setCellValue(option.getCampos().get("CODIGOCPCDANE_NOMBRE").toString());
			
			cell = row.createCell(21);
			cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE").toString());
			
			cell = row.createCell(22);
			cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE_NOMBRE").toString());
			
			cell = row.createCell(23);
			cell.setCellValue(option.getCampos().get("CODIGOFUENTE").toString());
			
			cell = row.createCell(24);
			cell.setCellValue(option.getCampos().get("CODIGOFUENTE_NOMBRE").toString());
			
			cell = row.createCell(25);
			cell.setCellValue(option.getCampos().get("CODIGOCCPETREGA").toString());
			
			cell = row.createCell(26);
			cell.setCellValue(option.getCampos().get("CODIGOCCPETREGA_NOMBRE").toString());
			
			cell = row.createCell(27);
			cell.setCellValue(option.getCampos().get("CODIGOPOLITICA").toString());
			
			cell = row.createCell(28);
			cell.setCellValue(option.getCampos().get("CODIGOPOLITICA_NOMBRE").toString());
			
			cell = row.createCell(29);
			cell.setCellValue(option.getCampos().get("CODIGODETALLES").toString());
			
			cell = row.createCell(30);
			cell.setCellValue(option.getCampos().get("CODIGODETALLES_NOMBRE").toString());
			
			cell = row.createCell(31);
			cell.setCellValue(option.getCampos().get("SECTOR_CAN").toString());
			
			cell = row.createCell(32);
			cell.setCellValue(option.getCampos().get("PROGRAMA_CAN").toString());
			
			cell = row.createCell(33);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA_CAN").toString());
			
			cell = row.createCell(34);
			cell.setCellValue(option.getCampos().get("CODIGOPRODUCTO_CAN").toString());
			
			cell = row.createCell(35);
			cell.setCellValue(option.getCampos().get("CODIGOBPIN_CAN").toString());
			
			cell = row.createCell(36);
			cell.setCellValue(option.getCampos().get("CODIGOCCPET_CAN").toString());
			
			cell = row.createCell(37);
			cell.setCellValue(option.getCampos().get("CODIGOCPCDANE_CAN").toString());
			
			cell = row.createCell(38);
			cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE_CAN").toString());
			
			cell = row.createCell(39);
			cell.setCellValue(option.getCampos().get("CODIGOFUENTE_CAN").toString());
			
			cell = row.createCell(40);
			cell.setCellValue(option.getCampos().get("CODIGOCCPETREGA_CAN").toString());
			
			cell = row.createCell(41);
			cell.setCellValue(option.getCampos().get("CODIGOPOLITICA_CAN").toString());
			
			cell = row.createCell(42);
			cell.setCellValue(option.getCampos().get("CODIGODETALLES_CAN").toString());
	
		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
	}
	public static void addReporte3(Workbook workbook, List<Registro> options, char column,
			String name,String Msj) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(Msj);

		row = optionsSheet.createRow(2);

		cell = row.createCell(0);
		cell.setCellValue("COMPANIA");

		cell = row.createCell(1);
		cell.setCellValue("ANO DIS");

		cell = row.createCell(2);
		cell.setCellValue("RUBRO");

		cell = row.createCell(3);
		cell.setCellValue("TIPO DIS");
		
		cell = row.createCell(4);
		cell.setCellValue("COMPROBANTE DIS");
		
		cell = row.createCell(5);
		cell.setCellValue("FECHA DIS");
		
		cell = row.createCell(6);
		cell.setCellValue("CONSECUTIVO DIS");
		
		cell = row.createCell(7);
		cell.setCellValue("VALOR DIS");
		
		cell = row.createCell(8);
		cell.setCellValue("SECTOR DIS");
		
		cell = row.createCell(9);
		cell.setCellValue("PROGRAMA DIS");
		
		cell = row.createCell(10);
		cell.setCellValue("SUBPROGRAMA DIS");
		
		cell = row.createCell(11);
		cell.setCellValue("PRODUCTO DIS");

		cell = row.createCell(12);
		cell.setCellValue("CODIGO BPIN DIS");

		cell = row.createCell(13);
		cell.setCellValue("CODIGO CCPET DIS");

		cell = row.createCell(14);
		cell.setCellValue("CODIGO CPC DIS");

		cell = row.createCell(15);
		cell.setCellValue("CODIGO UNIDAD EJECUTA DIS");
		
		cell = row.createCell(16);
		cell.setCellValue("FUENTE DIS");
		
		cell = row.createCell(17);
		cell.setCellValue("CODIGO CCPET REGALIAS DIS");
		
		cell = row.createCell(18);
		cell.setCellValue("CCODIGO POLITICA DIS");
		
		cell = row.createCell(19);
		cell.setCellValue("DETALLE SECTORIAL DIS");
		
		cell = row.createCell(20);
		cell.setCellValue("TIPO RES");
		
		cell = row.createCell(21);
		cell.setCellValue("RES");
		
		cell = row.createCell(22);
		cell.setCellValue("FECHA RES");
		
		cell = row.createCell(23);
		cell.setCellValue("CONSECUTIVO RES");
		
		cell = row.createCell(24);
		cell.setCellValue("VALOR RES");

		cell = row.createCell(25);
		cell.setCellValue("SECTOR RES");

		cell = row.createCell(26);
		cell.setCellValue("PROGRAMA RES");

		cell = row.createCell(27);
		cell.setCellValue("SUBPROGRAMA RES");
		
		cell = row.createCell(28);
		cell.setCellValue("PRODUCTO RES");
		
		cell = row.createCell(29);
		cell.setCellValue("CODIGO BPIN RES");
		
		cell = row.createCell(30);
		cell.setCellValue("CODIGO CCPET RES");
		
		cell = row.createCell(31);
		cell.setCellValue("CODIGO CPC RES");
		
		cell = row.createCell(32);
		cell.setCellValue("CODIGO UNIDAD EJECUTA RES");
		
		cell = row.createCell(33);
		cell.setCellValue("FUENTE RES");
		
		cell = row.createCell(34);
		cell.setCellValue("CODIGO CCPET REGALIAS RES");
		
		cell = row.createCell(35);
		cell.setCellValue("CODIGO POLITICA RES");

		cell = row.createCell(36);
		cell.setCellValue("DETALLE SECTORIAL RES");

		cell = row.createCell(37);
		cell.setCellValue("TIPO REO");

		cell = row.createCell(38);
		cell.setCellValue("REO");

		cell = row.createCell(39);
		cell.setCellValue("FECHA REO");
		
		cell = row.createCell(40);
		cell.setCellValue("CONSECUTIVO");
		
		cell = row.createCell(41);
		cell.setCellValue("VALOR REO");
		
		cell = row.createCell(42);
		cell.setCellValue("SECTOR REO");
		
		cell = row.createCell(43);
		cell.setCellValue("PROGRAMA REO");
		
		cell = row.createCell(44);
		cell.setCellValue("SUBPROGRAMA REO");
		
		cell = row.createCell(45);
		cell.setCellValue("PRODUCTO REO");
		
		cell = row.createCell(46);
		cell.setCellValue("CODIGO BPIN REO");
		
		cell = row.createCell(47);
		cell.setCellValue("CODIGO CCPET REO");

		cell = row.createCell(48);
		cell.setCellValue("CODIGO CPC REO");

		cell = row.createCell(49);
		cell.setCellValue("CODIGO UNIDAD EJECUTA REO");

		cell = row.createCell(50);
		cell.setCellValue("FUENTE REO");

		cell = row.createCell(51);
		cell.setCellValue("CODIGO CCPET REGALIAS REO");
		
		cell = row.createCell(52);
		cell.setCellValue("CCODIGO POLITICA REO");
		
		cell = row.createCell(53);
		cell.setCellValue("DETALLE SECTORIAL REO");
		
		cell = row.createCell(54);
		cell.setCellValue("TIPO EGR");
		
		cell = row.createCell(55);
		cell.setCellValue("EGR");
		
		cell = row.createCell(56);
		cell.setCellValue("FECHA EGR");
		
		cell = row.createCell(57);
		cell.setCellValue("CONSECUTIVO EGR");
		
		cell = row.createCell(58);
		cell.setCellValue("VALOR EGR");
		
		cell = row.createCell(59);
		cell.setCellValue("SECTOR EGR");

		cell = row.createCell(60);
		cell.setCellValue("PROGRAMA EGR");

		cell = row.createCell(61);
		cell.setCellValue("SUBPROGRAMA EGR");

		cell = row.createCell(62);
		cell.setCellValue("PRODUCTO EGR");

		cell = row.createCell(63);
		cell.setCellValue("CODIGO BPIN EGR");
		
		cell = row.createCell(64);
		cell.setCellValue("CODIGO CCPET EGR");
		
		cell = row.createCell(65);
		cell.setCellValue("CODIGO CPC EGR");
		
		cell = row.createCell(66);
		cell.setCellValue("CODIGO UNIDAD EJECUTA EGR");
		
		cell = row.createCell(67);
		cell.setCellValue("FUENTE EGR");
		
		cell = row.createCell(68);
		cell.setCellValue("CODIGO CCPET REGALIAS EGR");
		
		cell = row.createCell(69);
		cell.setCellValue("CODIGO POLITICA EGR");
		
		cell = row.createCell(70);
		cell.setCellValue("DETALLE SECTORIAL EGR");
		
		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("COMPANIA").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("AÑO_DIS").toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("RUBRO").toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("TIPO_DIS").toString());
			
			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get("DIS").toString());
			
			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get("FECHA_DIS").toString());
			
			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get("CONSECUTIVO_PPTO").toString());
			
			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get("VALOR_DIS").toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get("SECTOR_DIS").toString());
			
			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get("PROGRAMA_DIS").toString());
			
			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA_DIS").toString());

			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get("PRODUCTO_DIS").toString());

			cell = row.createCell(12);
			cell.setCellValue(option.getCampos().get("CODIGO_BPIN_DIS").toString());

			cell = row.createCell(13);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_DIS").toString());
			
			cell = row.createCell(14);
			cell.setCellValue(option.getCampos().get("CODIGO_CPC_DIS").toString());
			
			cell = row.createCell(15);
			cell.setCellValue(option.getCampos().get("CODIGO_UNIDAD_EJECUTA_DIS").toString());
			
			cell = row.createCell(16);
			cell.setCellValue(option.getCampos().get("FUENTE_DIS").toString());
			
			cell = row.createCell(17);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_REGALIAS_DIS").toString());
			
			cell = row.createCell(18);
			cell.setCellValue(option.getCampos().get("CCODIGO_POLITICA_DIS").toString());
			
			cell = row.createCell(19);
			cell.setCellValue(option.getCampos().get("DETALLE_SECTORIAL_DIS").toString());
			
			cell = row.createCell(20);
			cell.setCellValue(option.getCampos().get("TIPO_RES").toString());

			cell = row.createCell(21);
			cell.setCellValue(option.getCampos().get("RES").toString());

			cell = row.createCell(22);
			cell.setCellValue(option.getCampos().get("FECHA_RES").toString());

			cell = row.createCell(23);
			cell.setCellValue(option.getCampos().get("CONSECUTIVO_RES").toString());
			
			cell = row.createCell(24);
			cell.setCellValue(option.getCampos().get("VALOR_RES").toString());
			
			cell = row.createCell(25);
			cell.setCellValue(option.getCampos().get("SECTOR_RES").toString());
			
			cell = row.createCell(26);
			cell.setCellValue(option.getCampos().get("PROGRAMA_RES").toString());
			
			cell = row.createCell(27);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA_RES").toString());
			
			cell = row.createCell(28);
			cell.setCellValue(option.getCampos().get("PRODUCTO_RES").toString());
			
			cell = row.createCell(29);
			cell.setCellValue(option.getCampos().get("CODIGO_BPIN_RES").toString());
			
			cell = row.createCell(30);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_RES").toString());

			cell = row.createCell(31);
			cell.setCellValue(option.getCampos().get("CODIGO_CPC_RES").toString());

			cell = row.createCell(32);
			cell.setCellValue(option.getCampos().get("CODIGO_UNIDAD_EJECUTA_RES").toString());

			cell = row.createCell(33);
			cell.setCellValue(option.getCampos().get("FUENTE_RES").toString());
			
			cell = row.createCell(34);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_REGALIAS_RES").toString());
			
			cell = row.createCell(35);
			cell.setCellValue(option.getCampos().get("CODIGO_POLITICA_RES").toString());
			
			cell = row.createCell(36);
			cell.setCellValue(option.getCampos().get("DETALLE_SECTORIAL_RES").toString());
			
			cell = row.createCell(37);
			cell.setCellValue(option.getCampos().get("TIPO_REO").toString());
			
			cell = row.createCell(38);
			cell.setCellValue(option.getCampos().get("REO").toString());
			
			cell = row.createCell(39);
			cell.setCellValue(option.getCampos().get("FECHA_REO").toString());
			
			cell = row.createCell(40);
			cell.setCellValue(option.getCampos().get("CONSECUTIVO").toString());

			cell = row.createCell(41);
			cell.setCellValue(option.getCampos().get("VALOR_REO").toString());

			cell = row.createCell(42);
			cell.setCellValue(option.getCampos().get("SECTOR_REO").toString());

			cell = row.createCell(43);
			cell.setCellValue(option.getCampos().get("PROGRAMA_REO").toString());
			
			cell = row.createCell(44);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA_REO").toString());
			
			cell = row.createCell(45);
			cell.setCellValue(option.getCampos().get("PRODUCTO_REO").toString());
			
			cell = row.createCell(46);
			cell.setCellValue(option.getCampos().get("CODIGO_BPIN_REO").toString());
			
			cell = row.createCell(47);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_REO").toString());
			
			cell = row.createCell(48);
			cell.setCellValue(option.getCampos().get("CODIGO_CPC_REO").toString());
			
			cell = row.createCell(49);
			cell.setCellValue(option.getCampos().get("CODIGO_UNIDAD_EJECUTA_REO").toString());
			
			cell = row.createCell(50);
			cell.setCellValue(option.getCampos().get("FUENTE_REO").toString());

			cell = row.createCell(51);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_REGALIAS_REO").toString());

			cell = row.createCell(52);
			cell.setCellValue(option.getCampos().get("CCODIGO_POLITICA_REO").toString());

			cell = row.createCell(53);
			cell.setCellValue(option.getCampos().get("DETALLE_SECTORIAL_REO").toString());
			
			cell = row.createCell(54);
			cell.setCellValue(option.getCampos().get("TIPO_EGR").toString());
			
			cell = row.createCell(55);
			cell.setCellValue(option.getCampos().get("EGR").toString());
			
			cell = row.createCell(56);
			cell.setCellValue(option.getCampos().get("FECHA_EGR").toString());
			
			cell = row.createCell(57);
			cell.setCellValue(option.getCampos().get("CONSECUTIVO_EGR").toString());
			
			cell = row.createCell(58);
			cell.setCellValue(option.getCampos().get("VALOR_EGR").toString());
			
			cell = row.createCell(59);
			cell.setCellValue(option.getCampos().get("SECTOR_EGR").toString());
			
			cell = row.createCell(60);
			cell.setCellValue(option.getCampos().get("PROGRAMA_EGR").toString());

			cell = row.createCell(61);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA_EGR").toString());

			cell = row.createCell(62);
			cell.setCellValue(option.getCampos().get("PRODUCTO_EGR").toString());

			cell = row.createCell(63);
			cell.setCellValue(option.getCampos().get("CODIGO_BPIN_EGR").toString());
			
			cell = row.createCell(64);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_EGR").toString());
			
			cell = row.createCell(65);
			cell.setCellValue(option.getCampos().get("CODIGO_CPC_EGR").toString());
			
			cell = row.createCell(66);
			cell.setCellValue(option.getCampos().get("CODIGO_UNIDAD_EJECUTA_EGR").toString());
			
			cell = row.createCell(67);
			cell.setCellValue(option.getCampos().get("FUENTE_EGR").toString());
			
			cell = row.createCell(68);
			cell.setCellValue(option.getCampos().get("CODIGO_CCPET_REGALIAS_EGR").toString());
			
			cell = row.createCell(69);
			cell.setCellValue(option.getCampos().get("CODIGO_POLITICA_EGR").toString());
			
			cell = row.createCell(70);
			cell.setCellValue(option.getCampos().get("DETALLE_SECTORIAL_EGR").toString());
			
		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
	}
	public static void addReporte4(Workbook workbook, List<Registro> options, char column,
			String name,String Msj, int gastoOingreso) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(Msj);

		row = optionsSheet.createRow(2);

		cell = row.createCell(0);
		cell.setCellValue("AÑO");

		cell = row.createCell(1);
		cell.setCellValue("RUBRO");

		cell = row.createCell(2);
		cell.setCellValue("TIPO_CPTE");

		cell = row.createCell(3);
		cell.setCellValue("COMPROBANTE");

		cell = row.createCell(4);
		cell.setCellValue("CONSECUTIVO");
		
		cell = row.createCell(5);
		cell.setCellValue("FECHA");
		
		if(gastoOingreso == 1) {
			cell = row.createCell(6);
			cell.setCellValue("VALOR_DEBITO");
		}else if(gastoOingreso == 2) {
			cell = row.createCell(6);
			cell.setCellValue("VALOR_CREDITO");
		}

		cell = row.createCell(7);
		cell.setCellValue("SECTOR");

		cell = row.createCell(8);
		cell.setCellValue("PROGRAMA");

		cell = row.createCell(9);
		cell.setCellValue("SUBPROGRAMA");

		cell = row.createCell(10);
		cell.setCellValue("CODIGOPRODUCTO");

		cell = row.createCell(11);
		cell.setCellValue("CODIGOBPIN");

		cell = row.createCell(12);
		cell.setCellValue("CODIGOCCPET");

		cell = row.createCell(13);
		cell.setCellValue("CODIGOCPCDANE");

		cell = row.createCell(14);
		cell.setCellValue("CODIGOUNIDADEJE");

		cell = row.createCell(15);
		cell.setCellValue("CODIGOFUENTE");

		cell = row.createCell(16);
		cell.setCellValue("CODIGOCCPETREGA");

		cell = row.createCell(17);
		cell.setCellValue("POLITICAPUBLICA");
		
		cell = row.createCell(18);
		cell.setCellValue("DETALLESECTORIAL");
		
		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("ANO").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("RUBRO").toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("TIPO_CPTE").toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("COMPROBANTE").toString());

			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get("CONSECUTIVO").toString());

			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get("FECHA").toString());
			
			if(gastoOingreso == 1) {
				cell = row.createCell(6);
				cell.setCellValue(option.getCampos().get("VALOR_DEBITO").toString());
			}else if(gastoOingreso == 2) {
				cell = row.createCell(6);
				cell.setCellValue(option.getCampos().get("VALOR_CREDITO").toString());
			}

			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get("SECTOR").toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get("PROGRAMA").toString());

			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get("SUBPROGRAMA").toString());

			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get("CODIGOPRODUCTO").toString());

			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get("CODIGOBPIN").toString());

			cell = row.createCell(12);
			cell.setCellValue(option.getCampos().get("CODIGOCCPET").toString());

			cell = row.createCell(13);
			cell.setCellValue(option.getCampos().get("CODIGOCPCDANE").toString());

			cell = row.createCell(14);
			cell.setCellValue(option.getCampos().get("CODIGOUNIDADEJE").toString());

			cell = row.createCell(15);
			cell.setCellValue(option.getCampos().get("CODIGOFUENTE").toString());

			cell = row.createCell(16);
			cell.setCellValue(option.getCampos().get("CODIGOCCPETREGA").toString());
			
			cell = row.createCell(17);
			cell.setCellValue(option.getCampos().get("POLITICAPUBLICA").toString());
			
			cell = row.createCell(18);
			cell.setCellValue(option.getCampos().get("DETALLESECTORIAL").toString());

		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);

	}
	public static void addReporte5(Workbook workbook, List<Registro> options, char column,
			String name,String Msj) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(Msj);

		row = optionsSheet.createRow(2);

		cell = row.createCell(0);
		cell.setCellValue("CLASIFICADOR PADRE");

		cell = row.createCell(1);
		cell.setCellValue("NOMBRE CLASIFICADOR PADRE");

		cell = row.createCell(2);
		cell.setCellValue("CLASIFICADOR HIJO");

		cell = row.createCell(3);
		cell.setCellValue("NOMBRE CLASIFICADOR HIJO");
		
		cell = row.createCell(4);
		cell.setCellValue("FECHA");

		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get("CLASFIFICADORPADRE").toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get("NOMBREPADRE").toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get("CODCLASIFICADORHIJO").toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get("NOMBREHIJO").toString());
			
			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get("FECHA").toString());
		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);
	}
//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ANO
	 * 
	 * @return ANO
	 */
	public String getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ANO
	 * 
	 * @param ANO Variable a asignar en ANO
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable trimestre
	 * 
	 * @return trimestre
	 */
	public String getTrimestre() {
		return trimestre;
	}

	/**
	 * Asigna la variable trimestre
	 * 
	 * @param trimestre Variable a asignar en trimestre
	 */
	public void setTrimestre(String trimestre) {
		this.trimestre = trimestre;
	}

	/**
	 * Retorna la variable reporte
	 * 
	 * @return reporte
	 */
	public String getReporte() {
		return reporte;
	}

	/**
	 * Asigna la variable reporte
	 * 
	 * @param reporte Variable a asignar en reporte
	 */
	public void setReporte(String reporte) {
		this.reporte = reporte;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaano
	 * 
	 * @return listaano
	 */
	public List<Registro> getListaano() {
		return listaano;
	}

	/**
	 * Asigna la lista listaano
	 * 
	 * @param listaano Variable a asignar en listaano
	 */
	public void setListaano(List<Registro> listaano) {
		this.listaano = listaano;
	}
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>

	public List<Registro> getListaRerpote5() {
		return listaRerpote5;
	}

	public void setListaRerpote5(List<Registro> listaRerpote5) {
		this.listaRerpote5 = listaRerpote5;
	}

	public List<Registro> getListaRerpote2() {
		return listaRerpote2;
	}

	public void setListaRerpote2(List<Registro> listaRerpote2) {
		this.listaRerpote2 = listaRerpote2;
	}
}
