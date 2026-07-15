/*-
 * GeneracionPlanosDaviviendaControlador.java
 *
 * 1.0
 * 
 * 09/07/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.GeneracionPlanosDaviviendaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase para generar el archivo plano de Davivienda.
 *
 * @version 1.0, 09/07/2021
 * @author gfigueredo
 */
@ManagedBean
@ViewScoped
public class GeneracionPlanosDaviviendaControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante a nivel de clase que almacena el codigo del usuario
	 */
	private final String usuario;
	/**
	 * Constante a nivel de clase que almacena el código banco
	 */
	private String codigoBanco;
	/**
	 * Constante a nivel de clase que almacena el año
	 */
	private int ano;
	/**
	 * Constante a nivel de clase que almacena el tipo de egreso
	 */
	private String tipoEgreso;
	/**
	 * Constante a nivel de clase que almacena el egreso inicial
	 */
	private String egresoInicial;
	/**
	 * Constante a nivel de clase que almacena el egreso final
	 */
	private String egresoFinal;
	/**
	 * Constante a nivel de clase que almacena la cuenta inicial
	 */
	private String cuentaInicial;
	/**
	 * Constante a nivel de clase que almacena la cuenta final
	 */
	private String cuentaFinal;
	/**
	 * Constante a nivel de clase que almacena el tipo cuenta cliente
	 */
	private String tipoCuentaCliente;
	/**
	 * Constante a nivel de clase que almacena la clase transacción
	 */
	private String claseTransaccion;
	/**
	 * Constante a nivel de clase que almacena el tipo aplicación
	 */
	private String tipoAplicacion;
	/**
	 * Constante a nivel de clase que almacena la cuenta debitar
	 */
	private String cuentaDebitar;
	/**
	 * Constante a nivel de clase que almacena el nit cuenta
	 */
	private String nitCuenta;
	/**
	 * Variable que almacena el valor de fechaPago
	 */
	private Date fechaPago;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	/**
	 * Listado de registros para el combo CbAno
	 */
	private List<Registro> listaCbAno;

	/**
	 * Listado de registros para el combo CbBanco
	 */
	private RegistroDataModelImpl listaCbBanco;
	/**
	 * Listado de registros para el combo CbTipoEgreso
	 */
	private RegistroDataModelImpl listaCbTipoEgreso;
	/**
	 * Listado de registros para el combo CbEgresoInicial
	 */
	private RegistroDataModelImpl listaCbEgresoInicial;
	/**
	 * Listado de registros para el combo CbEgresoFinal
	 */
	private RegistroDataModelImpl listaCbEgresoFinal;
	/**
	 * Listado de registros para el combo CbCuentaInicial
	 */
	private RegistroDataModelImpl listaCbCuentaInicial;
	/**
	 * Listado de registros para el combo CbCuentaFinal
	 */
	private RegistroDataModelImpl listaCbCuentaFinal;

	@EJB
	EjbContabilidadTresRemote ejbContabilidadTresRemote;

	@EJB
	EjbSysmanUtil ejbSysmanUtil;

	// </DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de GeneracionPlanosDaviviendaControlador
	 */
	public GeneracionPlanosDaviviendaControlador() {
		super();
		compania = SessionUtil.getCompania();
		usuario = SessionUtil.getUser().getCodigo();
		ano = SysmanFunciones.ano(new Date());
		fechaPago = new Date();
		try {
			numFormulario = GeneralCodigoFormaEnum.GENERACION_PLANOS_DAVIVIENDA_CONTROLADOR.getCodigo();
			
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
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
		// <CARGAR_LISTA>
		cargarListaCbAno();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCbBanco();
		cargarListaCbTipoEgreso();
		cargarListaCbEgresoInicial();
		cargarListaCbEgresoFinal();
		cargarListaCbCuentaInicial();
		cargarListaCbCuentaFinal();
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
	 * Carga la lista listaCbAno
	 *
	 */
	public void cargarListaCbAno() {
		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		try {
			listaCbAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									GeneracionPlanosDaviviendaControladorUrlEnum.URL007.getValue())
							.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Carga la lista listaCbBanco
	 *
	 */
	public void cargarListaCbBanco() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL004.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCbBanco = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"BANCO");
	}

	/**
	 * 
	 * Carga la lista listaCbTipoEgreso
	 *
	 * 
	 */
	public void cargarListaCbTipoEgreso() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaCbTipoEgreso = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaCbEgresoInicial
	 *
	 * 
	 */
	public void cargarListaCbEgresoInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL001.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoEgreso);

		listaCbEgresoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NUMERO");
	}

	/**
	 * 
	 * Carga la lista listaCbEgresoFinal
	 *
	 * 
	 */
	public void cargarListaCbEgresoFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL002.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPO.getName(), tipoEgreso);
		param.put("NUMEROINICIAL", egresoInicial);

		listaCbEgresoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NUMERO");
	}

	/**
	 * 
	 * Carga la lista listaCbCuentaInicial
	 *
	 * 
	 */
	public void cargarListaCbCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL005.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaCbCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	/**
	 * 
	 * Carga la lista listaCbCuentaFinal
	 *
	 * 
	 */
	public void cargarListaCbCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL006.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put("CUENTAINICIAL", cuentaInicial);

		listaCbCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"CODIGO");
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnGenerarArchivo en la vista
	 *
	 * 
	 *
	 */
	public void oprimirBtnGenerarArchivo() {
		// <CODIGO_DESARROLLADO>
		if("014".equals(codigoBanco) || "012".equals(codigoBanco)){
			generarPlanoSudamerisItau();	
			return;
		}
		archivoDescarga = null;
		generarInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control CbAno
	 * 
	 * 
	 * 
	 */
	public void cambiarCbAno() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
		cuentaFinal = null;
		cuentaInicial = null;
		egresoFinal = null;
		egresoInicial = null;
		tipoEgreso = null;
		cargarListaCbTipoEgreso();
		cargarListaCbCuentaInicial();
		cargarListaCbCuentaFinal();
		cargarListaCbEgresoInicial();
		cargarListaCbEgresoFinal();
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCbBanco
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbBanco(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoBanco = registroAux.getCampos().get("BANCO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCbTipoEgreso
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbTipoEgreso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipoEgreso = registroAux.getCampos().get("CODIGO").toString();

		cargarListaCbEgresoInicial();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCbEgresoInicial
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbEgresoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		egresoInicial = registroAux.getCampos().get("NUMERO").toString();

		cargarListaCbEgresoFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCbEgresoFinal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbEgresoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		egresoFinal = registroAux.getCampos().get("NUMERO").toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCbCuentaInicial
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = registroAux.getCampos().get("CODIGO").toString();

		cargarListaCbCuentaFinal();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCbCuentaFinal
	 *
	 * 
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = registroAux.getCampos().get("CODIGO").toString();
	}

	public void generarInforme(FORMATOS formato) {

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Map<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			reemplazar.put("compania", compania);
			reemplazar.put("ano", ano);
			reemplazar.put("tipo", tipoEgreso);
			reemplazar.put("cuentaInicial", cuentaInicial);
			reemplazar.put("cuentaFinal", cuentaFinal);
			reemplazar.put("numeroInicial", egresoInicial);
			reemplazar.put("numeroFinal", egresoFinal);

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			parametros.put(GeneralParameterEnum.USUARIO.getName(), SessionUtil.getUser().getCodigo());
			parametros.put("NUMEROINICIAL", egresoInicial);
			parametros.put("NUMEROFINAL", egresoFinal);
			parametros.put("ANO", ano);
			parametros.put("TIPO", tipoEgreso);
			parametros.put("CUENTAINICIAL", cuentaInicial);
			parametros.put("CUENTAFINAL", cuentaFinal);

			String clob = ejbContabilidadTresRemote.inconsistenciasPlanoBancolombia(compania, ano, tipoEgreso,
					cuentaInicial, cuentaFinal, egresoInicial, egresoFinal, usuario);

			String planoHeader = "";
			String planoDetalle = "";

			if (clob.equals("0")) {

				String sql = Reporteador.resuelveConsulta("800439GenerarArchivoPlanoDavivienda",
						Integer.parseInt(SessionUtil.getModulo()), reemplazar);

				Workbook workbook = new XSSFWorkbook(
						JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato).getStream());

				Sheet sheet = workbook.getSheetAt(0);

				sheet.shiftRows(0, sheet.getLastRowNum(), 2);

				sheet.createFreezePane(0, 3);

				Font font2 = workbook.createFont();
				font2.setFontName("Calibri");
				font2.setFontHeightInPoints((short) 11);
				font2.setBold(false);

				CellStyle style2 = workbook.createCellStyle();
				style2.setAlignment(CellStyle.ALIGN_CENTER);
				style2.setFont(font2);
				style2.setBorderBottom(CellStyle.BORDER_THIN);
				style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
				style2.setBorderLeft(CellStyle.BORDER_THIN);
				style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());
				style2.setBorderTop(CellStyle.BORDER_THIN);
				style2.setTopBorderColor(IndexedColors.BLACK.getIndex());
				style2.setBorderRight(CellStyle.BORDER_THIN);
				style2.setRightBorderColor(IndexedColors.BLACK.getIndex());

				Row r = sheet.createRow(0);

				Cell cell1 = r.createCell(0);
				cell1.setCellValue("RC");

				cell1 = r.createCell(1);
				cell1.setCellValue("NIT EMPRESA");

				cell1 = r.createCell(2);
				cell1.setCellValue("CODIGO SERVICIO");

				cell1 = r.createCell(3);
				cell1.setCellValue("CODIGO SUBSERVICIO");

				cell1 = r.createCell(4);
				cell1.setCellValue("NRO CUENTA A DEBITAR");

				cell1 = r.createCell(5);
				cell1.setCellValue("TIPO DE CUENTA A DEBITAR");

				cell1 = r.createCell(6);
				cell1.setCellValue("CODIGO DEL BANCO");

				cell1 = r.createCell(7);
				cell1.setCellValue("TOTAL TRASLADOS");

				cell1 = r.createCell(8);
				cell1.setCellValue("N TOTAL TRASLADOS");

				cell1 = r.createCell(9);
				cell1.setCellValue("FECHA DE PAGO");

				cell1 = r.createCell(10);
				cell1.setCellValue("HORA PROCESO");

				cell1 = r.createCell(11);
				cell1.setCellValue("CODIGO OPERADOR");

				cell1 = r.createCell(12);
				cell1.setCellValue("CODIGO NO PROCESADO");

				cell1 = r.createCell(13);
				cell1.setCellValue("FECHA GENERACION");

				cell1 = r.createCell(14);
				cell1.setCellValue("HORA GENERACION");

				cell1 = r.createCell(15);
				cell1.setCellValue("INDICADOR DE INSCRIPCION");

				cell1 = r.createCell(16);
				cell1.setCellValue("TIPO IDENTIFICACION");

				cell1 = r.createCell(17);
				cell1.setCellValue("NUMERO CLIENTE");

				cell1 = r.createCell(18);
				cell1.setCellValue("OFICINA RECAUDO");

				cell1 = r.createCell(19);
				cell1.setCellValue("CAMPO FUTURO");

				Row r1 = sheet.createRow(1);

				String rc = "RC";
				cell1 = r1.createCell(0);
				cell1.setCellValue(rc);
				planoHeader = planoHeader + rc;

				// NIT EMPRESA
				String nitEmpresa = StringUtils.leftPad(nitCuenta, 16, "0");
				cell1 = r1.createCell(1);
				cell1.setCellValue(nitEmpresa);
				planoHeader = planoHeader + nitEmpresa;								
				
				cell1 = r1.createCell(2);
				cell1.setCellValue("0000");
				planoHeader = planoHeader + "0000";

				cell1 = r1.createCell(3);
				cell1.setCellValue("0000");
				planoHeader = planoHeader + "0000";								

				cell1 = r1.createCell(4);
				cell1.setCellValue(StringUtils.leftPad(cuentaDebitar, 16, "0"));
				planoHeader = planoHeader + StringUtils.leftPad(cuentaDebitar, 16, "0");

				// CC - CA
				String tipoCuentaDeb = "";
				if (tipoCuentaCliente.equals("E"))
					tipoCuentaDeb = "CA";
				else if (tipoCuentaCliente.equals("D"))
					tipoCuentaDeb = "CC";

				cell1 = r1.createCell(5);
				cell1.setCellValue(tipoCuentaDeb);
				planoHeader = planoHeader + tipoCuentaDeb;

				cell1 = r1.createCell(6);
				cell1.setCellValue(StringUtils.leftPad(codigoBanco, 6, "0"));
				planoHeader = planoHeader + StringUtils.leftPad(codigoBanco, 6, "0");

				// conteo
				cell1 = r1.createCell(8);
				cell1.setCellValue(StringUtils.leftPad((sheet.getLastRowNum() - 2) + "", 6, "0"));

				String pattern = "YYYMMdd";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
				String fechaP = simpleDateFormat.format(fechaPago);

				cell1 = r1.createCell(9);
				cell1.setCellValue(StringUtils.leftPad(fechaP, 8, "0"));

			 	String pattern1 = "HHmmss";
				SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
				String fechaP1 = simpleDateFormat1.format(new Date());

				cell1 = r1.createCell(10);
				cell1.setCellValue(StringUtils.leftPad(fechaP1, 6, "0"));

				cell1 = r1.createCell(11);
				cell1.setCellValue("0000");

				cell1 = r1.createCell(12);
				cell1.setCellValue("9999");
				
				cell1 = r1.createCell(13);
				cell1.setCellValue("00000000");

				cell1 = r1.createCell(14);
				cell1.setCellValue("000000");

				cell1 = r1.createCell(15);
				cell1.setCellValue("00");

				cell1 = r1.createCell(16);
				cell1.setCellValue("01");

				cell1 = r1.createCell(17);
				cell1.setCellValue("000000000000");

				cell1 = r1.createCell(18);
				cell1.setCellValue("0000");

				cell1 = r1.createCell(19);
				cell1.setCellValue("0000000000000000000000000000000000000000");

				/*
				 * cell1 = r1.createCell(6);
				 * cell1.setCellValue(ejbSysmanUtil.mostrarNombreDeMes(SysmanFunciones.mes(new
				 * Date())));
				 */
				double sumatoria = 0;
				for (int i = 3; i < sheet.getLastRowNum() + 1; i++) {
					Row r3 = sheet.getRow(i);
					for (int j = 0; j < 17; j++) {

						Cell cell2 = r3.getCell(j);
						cell2.setCellStyle(style2);
						planoDetalle = planoDetalle + r3.getCell(j);

						if (j == 6) {
							String val = cell2.getStringCellValue() + "";
							val = val.substring(0,val.length()-2) + "." +  val.substring(val.length()-2);
							if (val.equals(""))
								val = "0";
							sumatoria = sumatoria + Double.parseDouble(val);
						}
					}
					planoDetalle = planoDetalle + "\r\n";
				}

				String str = String.format("%.2f", sumatoria).replace(",", ".");
				long intNumber = Integer.parseInt(str.substring(0, str.indexOf('.')));
				String decNumberInt = str.substring(str.indexOf('.') + 1);
				// Sumatoria
				cell1 = r1.createCell(7);
				cell1.setCellValue(StringUtils.leftPad(intNumber + "", 16, "0") + decNumberInt);
				planoHeader = planoHeader + StringUtils.leftPad(intNumber + "", 16, "0") + decNumberInt;
				planoHeader = planoHeader + StringUtils.leftPad((sheet.getLastRowNum() - 2) + "", 6, "0");
				planoHeader = planoHeader + StringUtils.leftPad(fechaP, 8, "0");
				planoHeader = planoHeader + StringUtils.leftPad(fechaP1, 6, "0");
				planoHeader = planoHeader + "0000";
				planoHeader = planoHeader + "9999";
				planoHeader = planoHeader + "00000000";
				planoHeader = planoHeader + "000000";
				planoHeader = planoHeader + "00";
				planoHeader = planoHeader + "01";
				planoHeader = planoHeader + "000000000000";
				planoHeader = planoHeader + "0000";
				planoHeader = planoHeader + "0000000000000000000000000000000000000000";

				planoHeader = planoHeader + "\r\n" + planoDetalle;

				workbook.write(out);

				out.close();

				// archivoDescarga = JsfUtil.getArchivoDescarga(new
				// ByteArrayInputStream(out.toByteArray()),
				// "ArchivoPlanoDavivienda.xlsx");
				// ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(planoHeader);
				// archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
				// "planoDavivienda.txt");

				ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

				salidas[0] = JsfUtil.serializarPlano(planoHeader);

				salidas[1] = new ByteArrayInputStream(out.toByteArray());

				String[] nombresArchivos = new String[2];
				nombresArchivos[0] = "planoDavivienda.txt";
				nombresArchivos[1] = "ArchivoPlanoDavivienda.xlsx";

				archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas, nombresArchivos);

				workbook.close();

				Parameter parameter = new Parameter();
				parameter.setFields(parametros);

				UrlBean urlUpdate = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GeneracionPlanosDaviviendaControladorUrlEnum.URL008.getValue());
				requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);

			}

			else {

				ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(clob);
				archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "InconsistenciasTerceros.txt");
			}

		} catch (JRException | IOException | SQLException | DRException | SysmanException | NumberFormatException
				| SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage() + ". Verifique que no hayan sido generados previamente.");
		}

	}
	/**
	 * Genera el plano para Banco Sudameris o Itaú
	 * Selecciona el método del EJB según el código del banco
	 */
	public void generarPlanoSudamerisItau() {
	    archivoDescarga = null;
	    
	    try {
	        
	        String cuentaPrincipalAfiliadaParsed = "0";
	        String identificadorParsed = "0";
	        String codigoVerificacionParsed = "0"; 
	        
	        String tipoCuentaClienteParsed = (tipoCuentaCliente != null && !tipoCuentaCliente.isEmpty())
	                ? tipoCuentaCliente : "";
	        
	        String claseTransaccionParsed = (claseTransaccion != null && !claseTransaccion.isEmpty())
	                ? claseTransaccion : "";
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        String fechaPagoStr = sdf.format(fechaPago);
	        
	        String salidaPlano = null;
	        
	        
	        if ("012".equals(codigoBanco)) { // Sudameris
	            salidaPlano = ejbContabilidadTresRemote.generarPlanoSudameris(
	                    compania,                    
	                    ano,                         
	                    egresoInicial,               
	                    egresoFinal,                 
	                    fechaPagoStr,                
	                    cuentaDebitar,              
	                    cuentaPrincipalAfiliadaParsed, 
	                    codigoBanco,                 
	                    tipoEgreso,                  
	                    cuentaInicial,               
	                    cuentaFinal,                 
	                    identificadorParsed,         
	                    codigoVerificacionParsed,    
	                    tipoCuentaClienteParsed,     
	                    claseTransaccionParsed       
	            );
	        } 
	        else if ("014".equals(codigoBanco)) { // Itau
	            salidaPlano = ejbContabilidadTresRemote.generarPlanoItau(
	                    compania,                    
	                    ano,                         
	                    egresoInicial,               
	                    egresoFinal,                 
	                    fechaPagoStr,                
	                    cuentaDebitar,               
	                    cuentaPrincipalAfiliadaParsed, 
	                    codigoBanco,                 
	                    tipoEgreso,                  
	                    cuentaInicial,               
	                    cuentaFinal,                 
	                    identificadorParsed,         
	                    codigoVerificacionParsed,    
	                    tipoCuentaClienteParsed,
	                    nitCuenta
	            );
	        } 
	        else {
	            throw new SystemException("Código de banco no válido para Sudameris/Itaú: " + codigoBanco);
	        }
	        
	        // Validar respuesta del EJB
	        if (salidaPlano != null && !salidaPlano.equals("ERROR") && !salidaPlano.isEmpty()) {
	            String nombreArchivo = obtenerNombreArchivoPorBanco(codigoBanco);
	            
	            archivoDescarga = JsfUtil.getArchivoDescarga(
	                    JsfUtil.serializarPlano(salidaPlano),
	                    nombreArchivo
	            );
	            
	            JsfUtil.agregarMensajeInformativo("Plano generado exitosamente.");
	        } else {
	            JsfUtil.agregarMensajeError(
	                    "No se encontraron comprobantes contables o se produjo un error al generar el plano.");
	        }
	        
	    } catch (NumberFormatException e) {
	        logger.error("Error en formato de números: " + e.getMessage(), e);
	        JsfUtil.agregarMensajeError("Error en el formato de los datos numéricos.");
	    } catch (SystemException e) {
	        logger.error("Error de validación: " + e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    } catch (Exception e) {
	        logger.error("Error inesperado: " + e.getMessage(), e);
	        JsfUtil.agregarMensajeError("Error inesperado al generar el plano: " + e.getMessage());
	    }
	}

	
	/**
	 * Retorna el nombre del archivo según el código del banco
	 */
	private String obtenerNombreArchivoPorBanco(String codigoBanco) {
	    switch (codigoBanco) {
	        case "014": // Banco Itau
	            return  "Archivo Plano BancoItau.txt";
	        case "012": // Banco Sudameris
	            return  "Archivo Plano BancoSudameris.txt";
	        default:
	            return " Plano_" + codigoBanco + ".txt";
	    }
	}


	// </METODOS_COMBOS_GRANDES>
	// <METODOS_ARBOL>
	// </METODOS_ARBOL>
	// <SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable codigoBanco
	 * 
	 * @return codigoBanco
	 */
	public String getCodigoBanco() {
		return codigoBanco;
	}

	/**
	 * Asigna la variable codigoBanco
	 * 
	 * @param codigoBanco Variable a asignar en codigoBanco
	 */
	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	/**
	 * Retorna la variable ano
	 * 
	 * @return ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * Asigna la variable ano
	 * 
	 * @param ano Variable a asignar en ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * Retorna la variable tipoEgreso
	 * 
	 * @return tipoEgreso
	 */
	public String getTipoEgreso() {
		return tipoEgreso;
	}

	/**
	 * Asigna la variable tipoEgreso
	 * 
	 * @param tipoEgreso Variable a asignar en tipoEgreso
	 */
	public void setTipoEgreso(String tipoEgreso) {
		this.tipoEgreso = tipoEgreso;
	}

	/**
	 * Retorna la variable egresoInicial
	 * 
	 * @return egresoInicial
	 */
	public String getEgresoInicial() {
		return egresoInicial;
	}

	/**
	 * Asigna la variable egresoInicial
	 * 
	 * @param egresoInicial Variable a asignar en egresoInicial
	 */
	public void setEgresoInicial(String egresoInicial) {
		this.egresoInicial = egresoInicial;
	}

	/**
	 * Retorna la variable egresoFinal
	 * 
	 * @return egresoFinal
	 */
	public String getEgresoFinal() {
		return egresoFinal;
	}

	/**
	 * Asigna la variable egresoFinal
	 * 
	 * @param egresoFinal Variable a asignar en egresoFinal
	 */
	public void setEgresoFinal(String egresoFinal) {
		this.egresoFinal = egresoFinal;
	}

	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	/**
	 * Asigna la variable cuentaInicial
	 * 
	 * @param cuentaInicial Variable a asignar en cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}

	/**
	 * Asigna la variable cuentaFinal
	 * 
	 * @param cuentaFinal Variable a asignar en cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	/**
	 * Retorna la variable tipoCuentaCliente
	 * 
	 * @return tipoCuentaCliente
	 */
	public String getTipoCuentaCliente() {
		return tipoCuentaCliente;
	}

	/**
	 * Asigna la variable tipoCuentaCliente
	 * 
	 * @param tipoCuentaCliente Variable a asignar en tipoCuentaCliente
	 */
	public void setTipoCuentaCliente(String tipoCuentaCliente) {
		this.tipoCuentaCliente = tipoCuentaCliente;
	}

	/**
	 * Retorna la variable claseTransaccion
	 * 
	 * @return claseTransaccion
	 */
	public String getClaseTransaccion() {
		return claseTransaccion;
	}

	/**
	 * Asigna la variable claseTransaccion
	 * 
	 * @param claseTransaccion Variable a asignar en claseTransaccion
	 */
	public void setClaseTransaccion(String claseTransaccion) {
		this.claseTransaccion = claseTransaccion;
	}

	/**
	 * Retorna la variable tipoAplicacion
	 * 
	 * @return tipoAplicacion
	 */
	public String getTipoAplicacion() {
		return tipoAplicacion;
	}

	/**
	 * Asigna la variable tipoAplicacion
	 * 
	 * @param tipoAplicacion Variable a asignar en tipoAplicacion
	 */
	public void setTipoAplicacion(String tipoAplicacion) {
		this.tipoAplicacion = tipoAplicacion;
	}

	/**
	 * Retorna la variable cuentaDebitar
	 * 
	 * @return cuentaDebitar
	 */
	public String getCuentaDebitar() {
		return cuentaDebitar;
	}

	/**
	 * Asigna la variable cuentaDebitar
	 * 
	 * @param cuentaDebitar Variable a asignar en cuentaDebitar
	 */
	public void setCuentaDebitar(String cuentaDebitar) {
		this.cuentaDebitar = cuentaDebitar;
	}

	/**
	 * Retorna la variable nitCuenta
	 * 
	 * @return nitCuenta
	 */
	public String getNitCuenta() {
		return nitCuenta;
	}

	/**
	 * Asigna la variable nitCuenta
	 * 
	 * @param nitCuenta Variable a asignar en nitCuenta
	 */
	public void setNitCuenta(String nitCuenta) {
		this.nitCuenta = nitCuenta;
	}

	/**
	 * Retorna la variable fechaPago
	 * 
	 * @return fechaPago
	 */
	public Date getFechaPago() {
		return fechaPago;
	}

	/**
	 * Asigna la variable fechaPago
	 * 
	 * @param fechaPago Variable a asignar en fechaPago
	 */
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	/**
	 * Retorna la lista listaCbAno
	 * 
	 * @return listaCbAno
	 */
	public List<Registro> getListaCbAno() {
		return listaCbAno;
	}

	/**
	 * Asigna la lista listaCbAno
	 * 
	 * @param listaCbAno Variable a asignar en listaCbAno
	 */
	public void setListaCbAno(List<Registro> listaCbAno) {
		this.listaCbAno = listaCbAno;
	}

	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * Retorna la lista listaCbBanco
	 * 
	 * @return listaCbBanco
	 */
	public RegistroDataModelImpl getListaCbBanco() {
		return listaCbBanco;
	}

	/**
	 * Asigna la lista listaCbBanco
	 * 
	 * @param listaCbBanco Variable a asignar en listaCbBanco
	 */
	public void setListaCbBanco(RegistroDataModelImpl listaCbBanco) {
		this.listaCbBanco = listaCbBanco;
	}

	/**
	 * Retorna la lista listaCbTipoEgreso
	 * 
	 * @return listaCbTipoEgreso
	 */
	public RegistroDataModelImpl getListaCbTipoEgreso() {
		return listaCbTipoEgreso;
	}

	/**
	 * Asigna la lista listaCbTipoEgreso
	 * 
	 * @param listaCbTipoEgreso Variable a asignar en listaCbTipoEgreso
	 */
	public void setListaCbTipoEgreso(RegistroDataModelImpl listaCbTipoEgreso) {
		this.listaCbTipoEgreso = listaCbTipoEgreso;
	}

	/**
	 * Retorna la lista listaCbEgresoInicial
	 * 
	 * @return listaCbEgresoInicial
	 */
	public RegistroDataModelImpl getListaCbEgresoInicial() {
		return listaCbEgresoInicial;
	}

	/**
	 * Asigna la lista listaCbEgresoInicial
	 * 
	 * @param listaCbEgresoInicial Variable a asignar en listaCbEgresoInicial
	 */
	public void setListaCbEgresoInicial(RegistroDataModelImpl listaCbEgresoInicial) {
		this.listaCbEgresoInicial = listaCbEgresoInicial;
	}

	/**
	 * Retorna la lista listaCbEgresoFinal
	 * 
	 * @return listaCbEgresoFinal
	 */
	public RegistroDataModelImpl getListaCbEgresoFinal() {
		return listaCbEgresoFinal;
	}

	/**
	 * Asigna la lista listaCbEgresoFinal
	 * 
	 * @param listaCbEgresoFinal Variable a asignar en listaCbEgresoFinal
	 */
	public void setListaCbEgresoFinal(RegistroDataModelImpl listaCbEgresoFinal) {
		this.listaCbEgresoFinal = listaCbEgresoFinal;
	}

	/**
	 * Retorna la lista listaCbCuentaInicial
	 * 
	 * @return listaCbCuentaInicial
	 */
	public RegistroDataModelImpl getListaCbCuentaInicial() {
		return listaCbCuentaInicial;
	}

	/**
	 * Asigna la lista listaCbCuentaInicial
	 * 
	 * @param listaCbCuentaInicial Variable a asignar en listaCbCuentaInicial
	 */
	public void setListaCbCuentaInicial(RegistroDataModelImpl listaCbCuentaInicial) {
		this.listaCbCuentaInicial = listaCbCuentaInicial;
	}

	/**
	 * Retorna la lista listaCbCuentaFinal
	 * 
	 * @return listaCbCuentaFinal
	 */
	public RegistroDataModelImpl getListaCbCuentaFinal() {
		return listaCbCuentaFinal;
	}

	/**
	 * Asigna la lista listaCbCuentaFinal
	 * 
	 * @param listaCbCuentaFinal Variable a asignar en listaCbCuentaFinal
	 */
	public void setListaCbCuentaFinal(RegistroDataModelImpl listaCbCuentaFinal) {
		this.listaCbCuentaFinal = listaCbCuentaFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}
