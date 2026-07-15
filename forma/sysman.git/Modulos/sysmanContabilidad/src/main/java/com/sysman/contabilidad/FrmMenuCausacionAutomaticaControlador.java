/*-
 * FrmMenuCausacionAutomaticaControlador.java
 *
 * 1.0
 * 
 * 06/06/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
					  
							
							  
								
								  
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.FrmCausacionAutomaticaControladorUrlEnum;
import com.sysman.contabilidad.enums.GenerarcptepptalCausacionControladorEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
									 
									 
import org.primefaces.model.StreamedContent;

/**
 * MENU CAUSACION AUTOMATICA
 *
 * @version 1.0, 06/06/2024
 * @author mrosero
 */
@ManagedBean
@ViewScoped
public class FrmMenuCausacionAutomaticaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	private final String modulo;
	boolean retorno = false;
	int varCausar;
	private boolean visibleAceptaCausacion = false;
	private String mes;
	private String ano;
	private String tipoComp;
	private String numeroComp;
	private String tercero;
	private String nombreComprobante;
	private double vlrBaseIva;
	private double vlrBase;
	private String opcionMenu;
	private String fecha;
	private String sucursal;
	private String clase;
	private boolean referenciadoVisible;
	private boolean valSaldoyRef;
//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	@EJB
	private EjbContabilidadTresRemote ejbContabilidadTres;
	
	@EJB
	private EjbContabilidadCincoRemote ejbContabilidadCinco;
	
	@EJB
	EjbSysmanUtilRemote ejbSysmanUtil;

	private Map<String, Object> ridComprobante;
    private List<Registro> listaSeleccionados;
	private String msjError;
	private boolean mostrarError;
	private boolean visibleErrorPrev;

	/**
	 * Crea una nueva instancia de FrmMenuCausacionAutomaticaControlador
	 */
	public FrmMenuCausacionAutomaticaControlador() {
		super();
		SessionUtil.setSessionVar("modulo", "1");
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
			if (parametrosEntrada != null) {
				ridComprobante = (Map<String, Object>) parametrosEntrada.get("rid");
				mes = extraerString(parametrosEntrada.get("mes"));
				ano = extraerString(parametrosEntrada.get("ano"));
				tipoComp = extraerString(parametrosEntrada.get("tipoComp"));
				numeroComp = extraerString(parametrosEntrada.get("numeroComp"));
				tercero = extraerString(parametrosEntrada.get("tercero"));
				nombreComprobante = extraerString(parametrosEntrada.get("nombreComprobante"));
				String vlrBaseIvaE = parametrosEntrada.get("vlrBaseIva").toString();
				vlrBaseIva = Double.parseDouble(vlrBaseIvaE);
				String vlrBaseE = parametrosEntrada.get("vlrBase").toString();
				vlrBase = Double.parseDouble(vlrBaseE);
				opcionMenu = extraerString(parametrosEntrada.get("opcionMenu"));
				sucursal = extraerString(parametrosEntrada.get("sucursal"));
				fecha = extraerString(parametrosEntrada.get("fecha"));
				clase = extraerString(parametrosEntrada.get("claseComprobante"));
			} else {
				SessionUtil.redireccionarMenuPermisos();
			}

			numFormulario = GeneralCodigoFormaEnum.FRM_MENU_CAUSACION_AUTOMATICA_CONTROLADOR.getCodigo();
			validarPermisos();
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
		abrirFormulario();
		try {
			referenciadoVisible = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA REFERENCIADO EN CAUSACION AUTOMATICA", modulo, new Date(), true), "NO").equals("SI") ? true
							: false;
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
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
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTDetalles en la vista
	 *
	 */
	public void oprimirBTDetalles() {

		// REDIRECCIONMIENTO AL FORMULARIO CAUSACION AUTOMATICA 2460 POR FORMA
		// onclick="window.top.location.href='/sysmanContabilidad/frmcausacionautomatica.sysman';"

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTGenerarCausacion en la vista
	 *
	 */
	public void oprimirBTGenerarCausacion() {
		// <CODIGO_DESARROLLADO>
		visibleAceptaCausacion = true;
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTGenerarComprobanteP en la vista
	 *
	 */
	public void oprimirBTGenerarComprobanteP() {

		JsfUtil.ejecutarJavaScript("window.location.href='/sysmanContabilidad/generarcptepptalcausacion.sysman';");
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BTPrevisualizacion en la vista
	 *
	 */
	public void oprimirBTPrevisualizacion() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		visibleErrorPrev = false;
		visibleAceptaCausacion = false;
		varCausar = 1;
		
		try {
			retorno = ejbContabilidadTres.causacionAutomatica(compania, Integer.parseInt(ano), tipoComp,
					new BigInteger(numeroComp), SessionUtil.getUser().getCodigo(), varCausar);
			
//metodo que contiene las validaciones de referenciado y saldos
			validarSaldoYReferenciados();

		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
//			ejecutaractualizarMensaje();
			
			if(msjError != null) {
				mostrarError = true;
			} else {
			
			generaReporte();
			valSaldoyRef = false;
			}
		
	}
	public void ejecutaractualizarMensaje() {
        if (!SysmanFunciones.validarVariableVacio(msjError)) {
            JsfUtil.agregarMensajeInformativo(msjError);
        }
        inicializar();
    }
	
	public void aceptarerror() {
        //<CODIGO_DESARROLLADO>
		
		mostrarError = false;
		archivoDescarga = null;
		generaReporte();
		 
		
		
		
       //</CODIGO_DESARROLLADO>
   }
	public static String extractErrorMessage(String input) {
        int startIndex = input.indexOf("@#INI#@Log");
        int endIndex = input.indexOf("@#FIN#@");
        
        if (startIndex != -1 && endIndex != -1) {
            String logContent = input.substring(startIndex, endIndex);
            String[] lines = logContent.split("\n");
            for (String line : lines) {
                if (line.contains("Validar")) {
                    return line.trim();
                }
            }
        }
        return "Error message not found";
    }
	
	private void validarSaldoYReferenciados() {
		valSaldoyRef = false;
		

			try {
				ejbContabilidadTres.validacionCausacionAutomatica(compania, Integer.parseInt(ano), tipoComp,
						new BigInteger(numeroComp), SessionUtil.getUser().getCodigo());
			} catch (NumberFormatException | SystemException e) {
				msjError=e.getMessage(); 
	            msjError = extractErrorMessage(msjError);
				valSaldoyRef = true;
			} 

		return;

	}

	private void generaReporte() {
		String reporteC;
		String reporteP;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			if (referenciadoVisible) {
				reporteC = "800629PreviewCausacionRef";
			} else {
				reporteC = "800626PreviewCausacion";
			}
				reporteP = "800630PreviewCausacionPPTAL";

			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			HashMap<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> param = new TreeMap<>();
			
			reemplazar.put("compania", compania);
			reemplazar.put("ano", ano);
			reemplazar.put("numero", numeroComp);
			reemplazar.put("tipo", tipoComp);
			
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
			param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);

			Registro rs1 = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											FrmCausacionAutomaticaControladorUrlEnum.URL1933002.getValue())
									.getUrl(),
							param));

			// MANEJO DE PARAMETROS DEL REPORTE

			String sqlC = Reporteador.resuelveConsulta(reporteC, Integer.parseInt(SessionUtil.getModulo()), reemplazar);
			String sqlP = Reporteador.resuelveConsulta(reporteP, Integer.parseInt(SessionUtil.getModulo()), reemplazar);


			// Crear el Workbook y las dos hojas
			Workbook workbook = new XSSFWorkbook();
			Sheet sheetC = workbook.createSheet("Contabilidad");
			Sheet sheetP = workbook.createSheet("Presupuesto");
			
			// Crear la hoja "Contabilidad" y llenarla con la consulta sqlC
			if (Integer.parseInt(SysmanFunciones.nvl(rs1.getCampos().get("SQLC_CUENTA"), "").toString()) > 0) {

				try (InputStream inputStreamC = JsfUtil
						.exportarHojaDatosStreamed(sqlC, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL).getStream()) {

					Workbook tempWorkbookC = new XSSFWorkbook(inputStreamC);
					Sheet tempSheetC = tempWorkbookC.getSheetAt(0);
					copySheet(tempSheetC, sheetC);

				}
			}

			// Crear la hoja "Presupuesto" y llenarla con la consulta sqlP
			if (Integer.parseInt(SysmanFunciones.nvl(rs1.getCampos().get("SQLP_CUENTA"), "").toString()) > 0) {
				try (InputStream inputStreamP = JsfUtil
						.exportarHojaDatosStreamed(sqlP, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL).getStream()) {

					Workbook tempWorkbookP = new XSSFWorkbook(inputStreamP);
					Sheet tempSheetP = tempWorkbookP.getSheetAt(0);
					copySheet(tempSheetP, sheetP);

				}
			}
			// Crear estilo de celda en negrita con fondo gris claro
			CellStyle style = workbook.createCellStyle();
			CellStyle numericStyle = workbook.createCellStyle();
			Font font = workbook.createFont();
			DataFormat format = workbook.createDataFormat();
			font.setBold(true);
			style.setFont(font);
			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			numericStyle.setDataFormat(format.getFormat("#,##0.00"));

			// Aplicar estilo
			applyStile(sheetC, style);
			applyStile(sheetP, style);
			applyNumericFormat(sheetC, numericStyle, 3, 4, 5);
			applyNumericFormat(sheetP, numericStyle, 5);
			workbook.write(out);

			archivoDescarga = JsfUtil.getArchivoDescarga(new ByteArrayInputStream(out.toByteArray()),
					"PreviewCausacion" + ".xlsx");
			workbook.close();
			
		} catch (JRException | IOException | SQLException | DRException
				| com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			
		}

	}
	
	private void copySheet(Sheet source, Sheet destination) {
		for (int i = 0; i < source.getPhysicalNumberOfRows(); i++) {
			Row sourceRow = source.getRow(i);
			Row newRow = destination.createRow(i);
			for (int j = 0; j < sourceRow.getPhysicalNumberOfCells(); j++) {
				Cell sourceCell = sourceRow.getCell(j);
				Cell newCell = newRow.createCell(j);
				newCell.setCellValue(sourceCell.toString());
			}
		}
	}

	private void applyStile(Sheet sheet, CellStyle style) {
		Row firstRow = sheet.getRow(0);
		if (firstRow != null) {
			for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
				Cell cell = firstRow.getCell(i);
				if (cell != null) {
					cell.setCellStyle(style);					
				}
				// Ajustar el ancho de la columna
				sheet.autoSizeColumn(i);
			}
		}
	}
	
	private void applyNumericFormat(Sheet sheet, CellStyle numericStyle, int... columns) {
		for (Row row : sheet) {
			if (row.getRowNum() == 0)
				continue; // Skip header row
			for (int column : columns) {
				Cell cell = row.getCell(column);
				if (cell != null) {
					if (cell.getCellTypeEnum() == CellType.STRING) {
						try {
							double value = Double.parseDouble(cell.getStringCellValue().replace(",", ""));
							cell.setCellValue(value);
							cell.setCellStyle(numericStyle);
						} catch (NumberFormatException e) {
							// Handle the exception or log it
						}
					} else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
						cell.setCellStyle(numericStyle);
					}
				}
			}
		}
	}

	
//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control AceptaCausacion
	 * 
	 */
	public void cambiarAceptaCausacion() {
		// <CODIGO_DESARROLLADO>

		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Aceptar del dialogo AceptaCausacion en
	 * la vista
	 *
	 */
	public void aceptarAceptaCausacion() {
		// <CODIGO_DESARROLLADO>
		varCausar = 2;
		validarSaldoYReferenciados();	
		if(!valSaldoyRef) {
			try {

				Map<String, Object> param = new TreeMap<>();
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.ANO.getName(), ano);
				param.put(GeneralParameterEnum.TIPO.getName(), tipoComp);
				param.put(GeneralParameterEnum.NUMERO.getName(), numeroComp);

				listaSeleccionados = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										FrmCausacionAutomaticaControladorUrlEnum.URL38071.getValue())
								.getUrl(),
								param));

				if (!listaSeleccionados.isEmpty()) {

					try {
						retorno = ejbContabilidadTres.causacionAutomatica(compania, Integer.parseInt(ano), tipoComp,
								new BigInteger(numeroComp), SessionUtil.getUser().getCodigo(), varCausar);

						generarComprobantePptalvarios();
						JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_INGRESADO"));


					} catch (NumberFormatException e) {
						logger.error(e.getMessage(), e);
						JsfUtil.agregarMensajeError(e.getMessage());
					}

				} else {

					JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4453"));
				}

			} catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		} else {
			visibleErrorPrev = true;
			//JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4455"));
			// </CODIGO_DESARROLLADO>
		}
	}

	public boolean generarComprobantePptalvarios() {
		varCausar = 2;
		try {
			
			ejbContabilidadCinco.generarComprobantePresupuestalVarios(compania, Integer.parseInt(ano), tipoComp,
					new BigInteger(numeroComp), true, listaComprobanteAfectar(listaSeleccionados),
					"(''" + tercero + "'',''" + sucursal + "'')", SessionUtil.getUser().getCodigo(),
					listaComprobanteAfectarCAuto(listaSeleccionados), varCausar);
			return true;
		} catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			return false;
		}
	}

	private String listaComprobanteAfectar(List<Registro> listaAfectar) {
		String comprobante;
		StringBuilder rta = new StringBuilder();

		for (Registro reg : listaAfectar) {
			rta.append("(''" + SysmanFunciones.ano((Date) reg.getCampos().get(GeneralParameterEnum.FECHA.getName()))
					+ "'',").append("''" + reg.getCampos().get(GeneralParameterEnum.TIPO_CPTE.getName()) + "'',")
					.append("''" + reg.getCampos().get(GenerarcptepptalCausacionControladorEnum.COMPROBANTE.getValue())
							+ "'')")
					.append(",");

		}
		comprobante = rta.toString();
		comprobante = comprobante.substring(0, comprobante.length() - 1);

		return comprobante;
	}

	private String listaComprobanteAfectarCAuto(List<Registro> listaAfectar) {
		String comprobante;
		StringBuilder rta = new StringBuilder();

		for (Registro reg : listaAfectar) {
			String consecutivo = (String.format("%05d", reg.getCampos().get("CONSECUTIVO")).replace(' ', '0'));

			rta.append(".col.")
					.append("(.reg."
							+ SysmanFunciones.ano((Date) reg.getCampos().get(GeneralParameterEnum.FECHA.getName()))
							+ ".reg.,")
					.append(".reg." + reg.getCampos().get(GeneralParameterEnum.TIPO_CPTE.getName()) + ".reg.,")
					.append(".reg." + consecutivo + ".reg.,")
					.append(".reg."
							+ reg.getCampos().get(GenerarcptepptalCausacionControladorEnum.COMPROBANTE.getValue())
							+ ".reg.,")
					.append(".reg." + reg.getCampos().get("VALOR_DEBITO") + ".reg.)");
			// .append(".col.");

		}
		comprobante = rta.append(".col.").toString();
		// comprobante = comprobante.substring(0, comprobante.length() - 1);

		return comprobante;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	private String extraerString(Object object) {
		return object != null ? object.toString() : null;
	}

	public boolean isVisibleAceptaCausacion() {
		return visibleAceptaCausacion;
	}

	public void setVisibleAceptaCausacion(boolean visibleAceptaCausacion) {
		this.visibleAceptaCausacion = visibleAceptaCausacion;
	}

	public String getMsjError() {
		return msjError;
	}

	public void setMsjError(String msjError) {
		this.msjError = msjError;
	}

	public boolean isMostrarError() {
		return mostrarError;
	}

	public void setMostrarError(boolean mostrarError) {
		this.mostrarError = mostrarError;
	}

	/**
	 * @return the visibleErrorPrev
	 */
	public boolean isVisibleErrorPrev() {
		return visibleErrorPrev;
	}

	/**
	 * @param visibleErrorPrev the visibleErrorPrev to set
	 */
	public void setVisibleErrorPrev(boolean visibleErrorPrev) {
		this.visibleErrorPrev = visibleErrorPrev;
	}
	

}
