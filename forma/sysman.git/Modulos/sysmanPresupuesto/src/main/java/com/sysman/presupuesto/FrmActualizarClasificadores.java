/*-
 * FrmActualizarClasificadores.java
 *
 * 1.0
 * 
 * 15/03/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.ejb.EjbPresupuestoCuatroRemote;
import com.sysman.presupuesto.enums.FrmActualizarClasificadoresEnum;
import com.sysman.presupuesto.enums.FrmActualizarClasificadoresUrlEnum;
import com.sysman.presupuesto.enums.FrmSubirClasificadoresUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
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
 * @version 1.0, 15/03/2022
 * @author cperez
 */
@ManagedBean
@ViewScoped
public class  FrmActualizarClasificadores extends BeanBaseModal{
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	private String anio;
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	private List<Registro> listaanio;
	// </DECLARAR_LISTAS>
	private List<Registro> listaMovimientosPptal;
	private List<Registro> listaCodigos23;
	private List<Registro> listaClasificaNoConfig;
	private List<Registro> listaCodigosChip;
	private List<Registro> listaEjecucionGastos;
	private List<Registro> listaSumaProgramacionIngProgGastos;
	private List<Registro> listaCodigoCCPETCPC;
	private List<Registro> listaRubrosIngreso;
	private List<Registro> listaMovimientosPptalIngresos;



	// <DECLARAR_LISTAS_COMBO_GRANDE>
	// </DECLARAR_LISTAS_COMBO_GRANDE>
	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtl;
	@EJB
	private EjbPresupuestoCuatroRemote ejbPresupuestoCuatro;


	/**
	 * Creates a new instance of RevisarafectacionesControlador
	 */
	public FrmActualizarClasificadores() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.ACTUALIZARCLASIFICADORES
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			Logger.getLogger(FrmActualizarClasificadores.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		cargarListaanio();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		anio = String.valueOf(SysmanFunciones.getParteFecha(
				new Date(),
				Calendar.YEAR));
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaanio() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaanio = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmActualizarClasificadoresUrlEnum.URL3043
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	public void oprimirInconsistencias() throws IOException {
		archivoDescarga = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

			Map<String, Object> param = new HashMap<>();
			param.put(FrmActualizarClasificadoresEnum.COMPANIA.getValue(), compania);
			param.put(FrmActualizarClasificadoresEnum.ANO.getValue(), String.valueOf(SysmanFunciones.ano(
					new Date())));

			listaMovimientosPptal = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmActualizarClasificadoresUrlEnum.URL0001.getValue())
									.getUrl(),
									param));
			listaCodigos23 = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmActualizarClasificadoresUrlEnum.URL0002.getValue())
									.getUrl(),
									param));
			listaClasificaNoConfig = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmActualizarClasificadoresUrlEnum.URL0003.getValue())
									.getUrl(),
									param));
			//			listaCodigosChip = RegistroConverter
			//					.toListRegistro(
			//							requestManager.getList(
			//									UrlServiceUtil.getInstance()
			//									.getUrlServiceByUrlByEnumID(
			//											FrmActualizarClasificadoresUrlEnum.URL0004.getValue())
			//									.getUrl(),
			//									param));
			//			listaEjecucionGastos = RegistroConverter
			//					.toListRegistro(
			//							requestManager.getList(
			//									UrlServiceUtil.getInstance()
			//									.getUrlServiceByUrlByEnumID(
			//											FrmActualizarClasificadoresUrlEnum.URL0005.getValue())
			//									.getUrl(),
			//									param));
			//			listaSumaProgramacionIngProgGastos = RegistroConverter
			//					.toListRegistro(
			//							requestManager.getList(
			//									UrlServiceUtil.getInstance()
			//									.getUrlServiceByUrlByEnumID(
			//											FrmActualizarClasificadoresUrlEnum.URL0006.getValue())
			//									.getUrl(),
			//									param));
			listaCodigoCCPETCPC = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmActualizarClasificadoresUrlEnum.URL0007.getValue())
									.getUrl(),
									param));
			//			listaRubrosIngreso = RegistroConverter
			//					.toListRegistro(
			//							requestManager.getList(
			//									UrlServiceUtil.getInstance()
			//									.getUrlServiceByUrlByEnumID(
			//											FrmActualizarClasificadoresUrlEnum.URL0008.getValue())
			//									.getUrl(),
			//									param));
			listaMovimientosPptalIngresos = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmActualizarClasificadoresUrlEnum.URL0009.getValue())
											.getUrl(),
									param));

			addMovimientosPptal(workbook, listaMovimientosPptal, 'A', "MovimientosPptales", idioma.getString("TB_TB4402"));
			addCodigos23(workbook, listaCodigos23, 'B', "Codigos2_3", idioma.getString("TB_TB4403"));
			addMovimientosPptal(workbook, listaClasificaNoConfig, 'C', "ClasificaNoConfig", idioma.getString("TB_TB4404"));
			//			addToSheet(workbook, listaCodigosChip, 'D', "CodigosChip", idioma.getString("TB_TB4405"));
			//			addToSheet(workbook, listaEjecucionGastos, 'E', "EjecucionGastos", idioma.getString("TB_TB4406"));
			//			addToSheet(workbook, listaSumaProgramacionIngProgGastos, 'F', "SumaProgramacionIngProgGastos", idioma.getString("TB_TB4407"));
			addCodigoCCPETCP(workbook, listaCodigoCCPETCPC, 'G', "CodigoCCPETCPC", idioma.getString("TB_TB4408"));
			//			addToSheet(workbook, listaRubrosIngreso, 'H', "RubrosIngreso", idioma.getString("TB_TB4409"));
			addMovimientosPptal(workbook, listaMovimientosPptalIngresos, 'I', "MovimientosPptalesIngesos", idioma.getString("TB_TB4402"));

			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"InconsistenciasClasificadores.xls");

		} catch (IOException | JRException | SystemException e) {
			e.printStackTrace();
		} finally {
			workbook.close();
		}
	}
	
	public static void addMovimientosPptal(Workbook workbook, List<Registro> options, char column,
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
		cell.setCellValue(FrmActualizarClasificadoresEnum.COMPANIA.getValue());

		cell = row.createCell(1);
		cell.setCellValue(FrmActualizarClasificadoresEnum.ANO.getValue());

		cell = row.createCell(2);
		cell.setCellValue(FrmActualizarClasificadoresEnum.TIPO_CPTE.getValue());

		cell = row.createCell(3);
		cell.setCellValue(FrmActualizarClasificadoresEnum.COMPROBANTE.getValue());

		cell = row.createCell(4);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CUENTA.getValue());
		
		cell = row.createCell(5);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CONSECUTIVO.getValue());
		
		cell = row.createCell(6);
		cell.setCellValue(FrmActualizarClasificadoresEnum.FECHA.getValue());
		
		cell = row.createCell(7);
		cell.setCellValue(FrmActualizarClasificadoresEnum.VALOR_CREDITO.getValue());
		
		cell = row.createCell(8);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CONTRACREDITO.getValue());

		cell = row.createCell(9);
		cell.setCellValue(FrmActualizarClasificadoresEnum.SECTOR.getValue());

		cell = row.createCell(10);
		cell.setCellValue(FrmActualizarClasificadoresEnum.PROGRAMA.getValue());

		cell = row.createCell(11);
		cell.setCellValue(FrmActualizarClasificadoresEnum.SUBPROGRAMA.getValue());

		cell = row.createCell(12);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOPRODUCTO.getValue());

		cell = row.createCell(13);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOBPIN.getValue());

		cell = row.createCell(14);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOCCPET.getValue());

		cell = row.createCell(15);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOCPCDANE.getValue());

		cell = row.createCell(16);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOUNIDADEJE.getValue());

		cell = row.createCell(17);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOFUENTE.getValue());

		cell = row.createCell(18);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOCCPETREGA.getValue());
		
		cell = row.createCell(19);
		cell.setCellValue(FrmActualizarClasificadoresEnum.POLITICAPUBLICA.getValue());
		
		cell = row.createCell(20);
		cell.setCellValue(FrmActualizarClasificadoresEnum.DETALLESECTORIAL.getValue());

		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.COMPANIA.getValue()).toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.ANO.getValue()).toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.TIPO_CPTE.getValue()).toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.COMPROBANTE.getValue()).toString());

			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CUENTA.getValue()).toString());
			
			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CONSECUTIVO.getValue()).toString());
			
			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.FECHA.getValue()).toString());
			
			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.VALOR_CREDITO.getValue()).toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CONTRACREDITO.getValue()).toString());

			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.SECTOR.getValue()).toString());

			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.PROGRAMA.getValue()).toString());

			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.SUBPROGRAMA.getValue()).toString());

			cell = row.createCell(12);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOPRODUCTO.getValue()).toString());

			cell = row.createCell(13);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOBPIN.getValue()).toString());

			cell = row.createCell(14);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOCCPET.getValue()).toString());

			cell = row.createCell(15);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOCPCDANE.getValue()).toString());

			cell = row.createCell(16);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOUNIDADEJE.getValue()).toString());

			cell = row.createCell(17);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOFUENTE.getValue()).toString());

			cell = row.createCell(18);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOCCPETREGA.getValue()).toString());
			
			cell = row.createCell(19);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.POLITICAPUBLICA.getValue()).toString());

			cell = row.createCell(20);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.DETALLESECTORIAL.getValue()).toString());

		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);

	}
	public static void addCodigos23(Workbook workbook, List<Registro> options, char column,
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
		cell.setCellValue(FrmActualizarClasificadoresEnum.COMPANIA.getValue());

		cell = row.createCell(1);
		cell.setCellValue(FrmActualizarClasificadoresEnum.ANO.getValue());

		cell = row.createCell(2);
		cell.setCellValue(FrmActualizarClasificadoresEnum.TIPO_CPTE.getValue());

		cell = row.createCell(3);
		cell.setCellValue(FrmActualizarClasificadoresEnum.COMPROBANTE.getValue());

		cell = row.createCell(4);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CUENTA.getValue());
		
		cell = row.createCell(5);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CONSECUTIVO.getValue());
		
		cell = row.createCell(6);
		cell.setCellValue(FrmActualizarClasificadoresEnum.FECHA.getValue());
		
		cell = row.createCell(7);
		cell.setCellValue(FrmActualizarClasificadoresEnum.VALOR_CREDITO.getValue());
		
		cell = row.createCell(8);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CONTRACREDITO.getValue());

		cell = row.createCell(9);
		cell.setCellValue(FrmActualizarClasificadoresEnum.SECTOR.getValue());

		cell = row.createCell(10);
		cell.setCellValue(FrmActualizarClasificadoresEnum.PROGRAMA.getValue());

		cell = row.createCell(11);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOPRODUCTO.getValue());

		cell = row.createCell(12);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOBPIN.getValue());

		cell = row.createCell(13);
		cell.setCellValue(FrmActualizarClasificadoresEnum.DETALLESECTORIAL.getValue());

		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.COMPANIA.getValue()).toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.ANO.getValue()).toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.TIPO_CPTE.getValue()).toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.COMPROBANTE.getValue()).toString());

			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CUENTA.getValue()).toString());

			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CONSECUTIVO.getValue()).toString());
			
			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.FECHA.getValue()).toString());
			
			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.VALOR_CREDITO.getValue()).toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CONTRACREDITO.getValue()).toString());
			
			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.SECTOR.getValue()).toString());

			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.PROGRAMA.getValue()).toString());

			cell = row.createCell(11);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOPRODUCTO.getValue()).toString());
			
			cell = row.createCell(12);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOBPIN.getValue()).toString());
			
			cell = row.createCell(13);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.DETALLESECTORIAL.getValue()).toString());

		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);

	}
	
	public static void addCodigoCCPETCP(Workbook workbook, List<Registro> options, char column,
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
		cell.setCellValue(FrmActualizarClasificadoresEnum.COMPANIA.getValue());

		cell = row.createCell(1);
		cell.setCellValue(FrmActualizarClasificadoresEnum.ANO.getValue());

		cell = row.createCell(2);
		cell.setCellValue(FrmActualizarClasificadoresEnum.TIPO_CPTE.getValue());

		cell = row.createCell(3);
		cell.setCellValue(FrmActualizarClasificadoresEnum.COMPROBANTE.getValue());

		cell = row.createCell(4);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CUENTA.getValue());
		
		cell = row.createCell(5);
		cell.setCellValue(FrmActualizarClasificadoresEnum.FECHA.getValue());
		
		cell = row.createCell(6);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CONSECUTIVO.getValue());
		
		cell = row.createCell(7);
		cell.setCellValue(FrmActualizarClasificadoresEnum.VALOR_CREDITO.getValue());
		
		cell = row.createCell(8);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CONTRACREDITO.getValue());
		
		cell = row.createCell(9);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOCCPET.getValue());

		cell = row.createCell(10);
		cell.setCellValue(FrmActualizarClasificadoresEnum.CODIGOCPCDANE.getValue());




		rowIndex = 3;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.COMPANIA.getValue()).toString());

			cell = row.createCell(1);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.ANO.getValue()).toString());

			cell = row.createCell(2);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.TIPO_CPTE.getValue()).toString());

			cell = row.createCell(3);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.COMPROBANTE.getValue()).toString());

			cell = row.createCell(4);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CUENTA.getValue()).toString());

			cell = row.createCell(5);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.FECHA.getValue()).toString());
			
			cell = row.createCell(6);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CONSECUTIVO.getValue()).toString());
			
			cell = row.createCell(7);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.VALOR_CREDITO.getValue()).toString());
			
			cell = row.createCell(8);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CONTRACREDITO.getValue()).toString());
			
			cell = row.createCell(9);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOCCPET.getValue()).toString());

			cell = row.createCell(10);
			cell.setCellValue(option.getCampos().get(FrmActualizarClasificadoresEnum.CODIGOCPCDANE.getValue()).toString());



		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);

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
	 */
	public static void addToSheet(Workbook workbook, List<Registro> options, char column,
			String name,String Msj) {
		String hiddenSheetName = name;
		Sheet optionsSheet = workbook.createSheet(hiddenSheetName);
		String nameName = column + "_parent";
		int columnIndex = 0;
		int rowIndex = 0;
		Row row = optionsSheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(Msj);
		rowIndex = 2;
		for (Registro option : options) {
			columnIndex = 0;
			row = optionsSheet.createRow(rowIndex++);
			cell = row.createCell(columnIndex++);
			Cell cell1 = row.createCell(columnIndex);
			cell.setCellValue(option.getCampos().get("CODIGO").toString());
			cell1.setCellValue(option.getCampos().get("NOMBRE").toString());
		}

		createName(workbook, nameName);
		//optionsSheet.protectSheet("Sysman10*");
		DVConstraint constraint = DVConstraint.createFormulaListConstraint(nameName);

	}
	private static Name createName(Workbook workbook, String nameName) {
		Name name = workbook.createName();
		name.setNameName(nameName);
		//name.setRefersToFormula(formula);
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

	public void oprimirAceptar() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		try {
			boolean aux = ejbPresupuestoCuatro.actualizarClasificadoresPptal(
					compania,
					Integer.parseInt(anio),
					SessionUtil.getUser().getCodigo());

			if (aux) {
				ejecutarmensajeArchivo();
			}
		}
		catch (SystemException e) {
			Logger.getLogger(RevisarafectacionesControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	/**
	 *
	 * Metodo invocado al ejecutar el comando remoto mensajeArchivo en
	 * la vista
	 *
	 */
	public void ejecutarmensajeArchivo() {
		// <CODIGO_DESARROLLADO>
		JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4401"));
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	// </METODOS_COMBOS_GRANDES>

	// <SET_GET_ATRIBUTOS>

	public String getAnio() {
		return anio;
	}

	public void setAnio(String anio) {
		this.anio = anio;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	public List<Registro> getListaMovimientosPptal() {
		return listaMovimientosPptal;
	}

	public void setListaMovimientosPptal(List<Registro> listaMovimientosPptal) {
		this.listaMovimientosPptal = listaMovimientosPptal;
	}

	public List<Registro> getListaCodigos23() {
		return listaCodigos23;
	}

	public void setListaCodigos23(List<Registro> listaCodigos23) {
		this.listaCodigos23  = listaCodigos23;
	}
	public List<Registro> getListaClasificaNoConfig() {
		return listaClasificaNoConfig;
	}

	public void setListaClasificaNoConfig(List<Registro> listaClasificaNoConfig) {
		this.listaClasificaNoConfig = listaClasificaNoConfig;
	}

	public List<Registro> getListaEjecucionGastos() {
		return listaEjecucionGastos;
	}

	public void setListaEjecucionGastos(List<Registro> listaEjecucionGastos) {
		this.listaEjecucionGastos = listaEjecucionGastos;
	}

	public List<Registro> getListaSumaProgramacionIngProgGastos() {
		return listaSumaProgramacionIngProgGastos;
	}

	public void setListaSumaProgramacionIngProgGastos(List<Registro> listaSumaProgramacionIngProgGastos) {
		this.listaSumaProgramacionIngProgGastos = listaSumaProgramacionIngProgGastos;
	}

	public List<Registro> getListaCodigoCCPETCPC() {
		return listaCodigoCCPETCPC;
	}

	public void setListaCodigoCCPETCPC(List<Registro> listaCodigoCCPETCPC) {
		this.listaCodigoCCPETCPC = listaCodigoCCPETCPC;
	}


	public List<Registro> getListaCodigosChip() {
		return listaCodigosChip;
	}

	public void setListaCodigosChip(List<Registro> listaCodigosChip) {
		this.listaCodigosChip = listaCodigosChip;
	}

	public List<Registro> getListaRubrosIngreso() {
		return listaRubrosIngreso;
	}

	public void setListaRubrosIngreso(List<Registro> listaRubrosIngreso) {
		this.listaRubrosIngreso = listaRubrosIngreso;
	}

	public List<Registro> getListaanio() {
		return listaanio;
	}

	public void setListaanio(List<Registro> listaanio) {
		this.listaanio = listaanio;
	}
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public List<Registro> getListaMovimientosPptalIngresos() {
		return listaMovimientosPptalIngresos;
	}

	public void setListaMovimientosPptalIngresos(List<Registro> listaMovimientosPptalIngresos) {
		this.listaMovimientosPptalIngresos = listaMovimientosPptalIngresos;
	}
}
