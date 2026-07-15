/*-
 * ResumentotalcuneControlador.java
 *
 * 1.0
 * 
 * 22/09/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.nomina.enums.NominaresumentotalControladorUrlEnum;
import com.sysman.nomina.enums.ResumentotalcuneControladorEnum;
import com.sysman.nomina.enums.ResumentotalcuneControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.ApiPayRoll;
import com.sysman.util.rest.DatosNomElectronica;
import com.sysman.util.rest.PojoNominaElectronica;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDeducciones;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecDevengados;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecEncabezado;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecGeneral;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NomElecTotales;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.NominaXmlBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.Registration;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.StringReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del modal resumen Cune
 *
 * @version 1.0, 22/09/2021
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ResumentotalcuneControlador extends BeanBaseModal {
	
    final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    final static ZoneId COLOMBIA_ZONE = ZoneId.of("America/Bogota");
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Tasa representativa del mercado NIE200
	 */
	private String trm;

	/**
	 * Indica si debe generar solo el Json para validar antes de
	 * enviar
	 */
	private boolean soloJson;

	/**
	 * Tipo de n&oacute;mina: Normal, Ajustes
	 */
	private String tipoNom;

	/**
	 * Tipo de n&oacute;mina: Normal, Ajustes, Utilizada para generar
	 * el json
	 */
	private String tipoNomGenJson;

	/**
	 * Consecutivo cuando aplica n&oacute:mina de ajuste
	 */
	private String consecutivo;
	/**
	 * Fecha del reporte NIE203
	 */
	private Date fechaReporte;

	/**
	 * Indicador de n&oacute;mina
	 */
	private boolean nomAjuste;
	
	/**
     * Variable encargada de mostrar o no el dialogo de confirmacion.
     */
    private boolean dialogoVisible;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	@EJB
	private EjbNominaUnoRemote ejbNominaUno;
	/**
	 * Empleado Inicial para generar Json
	 */
	private String empleadoIni;
	/**
	 * Empleado Inicial para generar Json
	 */
	private String empleadoFin;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	/** Proceso de Nom **/
	private String proceso;
	/** Anio de Nom **/
	private String ano;
	/** Mes de Nom **/
	private String mes;
	/** Periodo de Nom **/
	private String periodo;
	private List<Registro> listaPeriodo1;

	/**
	 */
	private RegistroDataModelImpl listaCbEmpleadoIni;
	/**
	 */
	private RegistroDataModelImpl listaCbEmpleadoFin;
	/*
	 * 
	 */
	private String empleado;
	/**
	 * 
	 */
	private RegistroDataModelImpl listaempleado;
	private boolean soloXML;
	/**
	 * Crea una nueva instancia de ResumentotalcuneControlador
	 */
    private static final String MANEJA_NOMINA_INVOWAY= "NOMINA INVOWAY / FRIDA";

    
	public ResumentotalcuneControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.RESUMEN_TOTA_CUNE
					.getCodigo();
			validarPermisos();
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
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
	public void inicializar() {
		abrirFormulario();
		proceso = (String) SessionUtil.getSessionVar("procesoNomina");
		ano = (String) SessionUtil.getSessionVar("anioNomina");
		mes = (String) SessionUtil.getSessionVar("mesNomina");
		periodo = (String) SessionUtil.getSessionVar("periodoNomina");
		fechaReporte = new Date();
		nomAjuste = false;
		consecutivo = "1";
		tipoNom = "T_NE_BASE";
		tipoNomGenJson = "T_NE_BASE";
		empleado = null;
		cargarListaCbEmpleadoIni();
		cargarListaCbEmpleadoFin();
		cargarListaempleado();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario() {
		// n.a
	}

	/**
	 * Carga la lista listaCbEmpleadoIni
	 */
	public void cargarListaempleado() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ResumentotalcuneControladorUrlEnum.URL0003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
				ano);

		param.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
				mes);

		param.put(ResumentotalcuneControladorEnum.UN_TIPONOM.getValue(),
				tipoNomGenJson);

		listaempleado = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				ResumentotalcuneControladorEnum.NUMDCTO.getValue());

	}
	/**
	 * Carga la lista listaCbEmpleadoIni
	 */
	public void cargarListaCbEmpleadoIni() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ResumentotalcuneControladorUrlEnum.URL0003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
				ano);

		param.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
				mes);

		param.put(ResumentotalcuneControladorEnum.UN_TIPONOM.getValue(),
				tipoNomGenJson);

		listaCbEmpleadoIni = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true,
				ResumentotalcuneControladorEnum.NUMDCTO.getValue());

	}
	/**
	 * Carga la lista listaCbEmpleadoFin
	 */
	public void cargarListaCbEmpleadoFin() {
		listaCbEmpleadoFin = listaCbEmpleadoIni;
	}

	public void cambiarCbTipoNomina() {
		if (ResumentotalcuneControladorEnum.NOM_AJUSTE.getValue()
				.equals(tipoNom)) {
			nomAjuste = true;
		}
		else {
			nomAjuste = false;
			consecutivo = "1";
		}

	}
	
	/**
	 * Metodo ejecutado al cambiar el control ckSoloJson
	 * 
	 * 
	 */
	public void cambiarckSoloJson() {
		if (soloJson) {
			soloXML = false;
		}

	}

	/**
	 * Metodo ejecutado al cambiar el control ckXML
	 * 
	 * 
	 */
	public void cambiarckXML() {
		if (soloXML) {
			soloJson = false;
		}
	}

	public void cambiarCbTipoNominaGenJson() {
		cargarListaCbEmpleadoIni();
		cargarListaCbEmpleadoFin();
		cargarListaempleado();
	}

	/**
	 * Guarda los historicos en la tabla Historicos Cune
	 */
	public void oprimirBtEjcAgruparHis() {
		try {
			ejbNominaUno.guardahistoricoCune(compania, Integer.parseInt(ano),
					Integer.parseInt(mes),
					SessionUtil.getUser().getCodigo());
			JsfUtil.agregarMensajeInformativo(
					idioma.getString("MSM_PROCESO_EJECUTADO"));
		}
		catch (NumberFormatException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Metodo ejecutado al oprimir el boton BtEjecActualizarNom en la
	 * vista, Actualiza la tabla NOMINA_CUNE
	 */
	public void oprimirBtEjecActualizarNom() {
		if (ResumentotalcuneControladorEnum.NOM_AJUSTE.getValue()
				.equals(tipoNom)
				&& "1".equals(consecutivo)) {
			JsfUtil.agregarMensajeAlerta(
					ResumentotalcuneControladorEnum.MSJ_CONSEC
					.getValue());
		}
		else {			
			try {		
				if("SI".equals(SysmanFunciones
     					.nvl(ejbSysmanUtil.consultarParametro(compania, "UNICO CONSECUTIVO EN NOMINA ELECTRONICA",
     							SessionUtil.getModulo(), new Date(), true), "NO")))
				{
					 dialogoVisible = true;
				}
				else 
				{
					ejbNominaUno.actNominaCune(compania, Integer.parseInt(ano),
							Integer.parseInt(mes), tipoNom,
							Integer.parseInt(consecutivo), fechaReporte,
							new BigDecimal(trm),
							SessionUtil.getUser().getCodigo(),
							empleado);
					JsfUtil.agregarMensajeInformativo(
							idioma.getString("MSM_PROCESO_EJECUTADO"));
				}
			}
			catch (NumberFormatException | SystemException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}

	/**
	 * Metodo ejecutado al oprimir el boton oprimirBtGenNominaCune en
	 * la vista
	 */
	public void oprimirBtGenNominaCune() {
		archivoDescarga = null;
		try {

			Map<String, Object> parSqlCune = new TreeMap<>();
			parSqlCune.put(ResumentotalcuneControladorEnum.UN_COMPANIA
					.getValue(),
					compania);

			parSqlCune.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
					ano);

			parSqlCune.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
					mes);

			List<Registro> lsNomCune = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0002
									.getValue())
							.getUrl(),
							parSqlCune));

			if (!lsNomCune.isEmpty()) {
				HSSFWorkbook workbookCune = new HSSFWorkbook();
				HSSFSheet sheet = workbookCune
						.createSheet("cune");

				HSSFRow rowDetalleCune;
				HSSFRow rowEncabezadoCune = sheet.createRow(0);
				HSSFCell cellCune;
				int filaCune = 1;
				for (Registro rs : lsNomCune) {
					rowDetalleCune = sheet.createRow(filaCune);
					int columnaCune = 0;
					// Ordeno los campos
					SortedSet<String> keysCune = new TreeSet<>(
							rs.getCampos().keySet());

					// Encabezados
					for (String key : keysCune) {
						if (filaCune == 1) {
							cellCune = rowEncabezadoCune
									.createCell(columnaCune);
							if (Arrays.asList("AANO", "BMES", "CNUMERO_DCTO",
									"DNOMBRECOMPLETO",
									"GCONS_TIPONOMINA", "HPERIODO")
									.contains(key)) {
								cellCune.setCellValue(
										key.substring(1, key.length()));
							}
							else {
								cellCune.setCellValue(key);
							}
						}

						// Detalle
						cellCune = rowDetalleCune.createCell(columnaCune);
						if (Arrays.asList("AANO", "BMES", "CNUMERO_DCTO",
								"DNOMBRECOMPLETO", "GCONS_TIPONOMINA",
								"HPERIODO")
								.contains(key)) {
							cellCune.setCellValue(rs.getCampos().get(key)
									.toString());
						}
						else {
							if (rs.getCampos().get(key) != null) {
								if (Arrays.asList(
										ResumentotalcuneControladorEnum.NIE002
										.getValue(),
										ResumentotalcuneControladorEnum.NIE003
										.getValue(),
										ResumentotalcuneControladorEnum.NIE004
										.getValue(),
										ResumentotalcuneControladorEnum.NIE005
										.getValue(),
										ResumentotalcuneControladorEnum.NIE008
										.getValue(),
										ResumentotalcuneControladorEnum.NIE203
										.getValue(),
										ResumentotalcuneControladorEnum.NIE109
										.getValue(),
										ResumentotalcuneControladorEnum.NIE110
										.getValue(),
										ResumentotalcuneControladorEnum.NIE136
										.getValue(),
										ResumentotalcuneControladorEnum.NIE137
										.getValue())
										.contains(key)) {
									cellCune.setCellValue(
											(Date) rs.getCampos()
											.get(key));

								}
								else if (Arrays.asList(
										ResumentotalcuneControladorEnum.NIE199
										.getValue(),
										ResumentotalcuneControladorEnum.NIE043
										.getValue(),
										ResumentotalcuneControladorEnum.NIE064
										.getValue(),
										ResumentotalcuneControladorEnum.NIE056
										.getValue())
										.contains(key)) {
									cellCune.setCellValue((boolean) rs
											.getCampos().get(key));
								}
								else if (Arrays.asList(
										ResumentotalcuneControladorEnum.NIE010
										.getValue(),
										ResumentotalcuneControladorEnum.NIE012
										.getValue(),
										"NIE013",
										ResumentotalcuneControladorEnum.NIE014
										.getValue(),
										ResumentotalcuneControladorEnum.NIE015
										.getValue(),
										"NIE016",
										"NIE024", "NIE025", "NIE030",
										"NIE031",
										ResumentotalcuneControladorEnum.NIE041
										.getValue(),
										ResumentotalcuneControladorEnum.NIE042
										.getValue(),
										ResumentotalcuneControladorEnum.NIE044
										.getValue(),
										ResumentotalcuneControladorEnum.NIE046
										.getValue(),
										ResumentotalcuneControladorEnum.NIE047
										.getValue(),
										ResumentotalcuneControladorEnum.NIE048
										.getValue(),
										ResumentotalcuneControladorEnum.NIE049
										.getValue(),
										"NIE050",
										ResumentotalcuneControladorEnum.NIE051
										.getValue(),
										ResumentotalcuneControladorEnum.NIE052
										.getValue(),
										ResumentotalcuneControladorEnum.NIE053
										.getValue(),
										ResumentotalcuneControladorEnum.NIE061
										.getValue(),
										ResumentotalcuneControladorEnum.NIE063
										.getValue(),
										ResumentotalcuneControladorEnum.NIE065
										.getValue(),
										ResumentotalcuneControladorEnum.NIE066
										.getValue(),
										ResumentotalcuneControladorEnum.NIE067
										.getValue(),
										ResumentotalcuneControladorEnum.NIE068
										.getValue(),
										ResumentotalcuneControladorEnum.NIE146
										.getValue(),
										ResumentotalcuneControladorEnum.NIE161
										.getValue(),
										ResumentotalcuneControladorEnum.NIE164
										.getValue(),
										"NIE175", "NIE202")
										.contains(key)) {
									cellCune.setCellValue(
											rs.getCampos().get(key)
											.toString());
								}
								else {
									cellCune.setCellValue(Double.parseDouble(
											rs.getCampos().get(key)
											.toString()));
								}
							}
						}
						columnaCune += 1;
					}
					filaCune += 1;
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				workbookCune.write(out);
				out.close();
				workbookCune.close();
				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(out.toByteArray()),
						"NominaCune.xls");
			}
			else {
				JsfUtil.agregarMensajeAlerta(idioma.getString(
						ResumentotalcuneControladorEnum.TB_TB4255
						.getValue()));
			}

		}
		catch (IOException | JRException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Normaliza y formatea un String XML aplicando indentación estándar de 2 espacios
	 * y codificación UTF-8. Se ejecuta justo antes de generar el archivo descargable
	 * para asegurar que el XML entregado a Invoway esté bien formado.
	 *
	 * @param xml   String XML generado por {@link NominaXmlBuilder#build}.
	 * @return      XML formateado con indentación y declaración UTF-8.
	 * @throws Exception si el XML de entrada está mal formado o falla la transformación.
	 */
	public String normalizarXml(String xml) throws Exception {
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

	    StreamSource source = new StreamSource(new StringReader(xml));
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);

	    transformer.transform(source, result);

	    return writer.toString();
	}
	

	

/**
 * Valida que un registro de nómina tenga los datos mínimos requeridos para
 * generar un XML válido. Si no pasa esta validación, {@link #generarModeloXml}
 * retorna {@code null} y el empleado se omite del proceso.
 *
 * <p>El registro se considera válido si cumple al menos una de estas condiciones:</p>
 * <ul>
 *   <li><b>Tiene básico:</b> número de documento ({@code NIE045})
 *       y sueldo ({@code NIE062}) mayor a cero.</li>
 *   <li><b>Tiene vacaciones:</b> cantidad ({@code NIE111}) y pago ({@code NIE112})
 *       mayores a cero.</li>
 *   <li><b>Tiene incapacidad:</b> cantidad ({@code NIE125}) y pago ({@code NIE126})
 *       mayores a cero.</li>
 * </ul>
 *
 * <p>Adicionalmente, si el consecutivo ({@code NIE009}) es nulo o vacío,
 * el registro se rechaza sin importar los demás campos.</p>
 *
 * @param rs    Registro consultado desde BD con los datos del empleado.
 * @return      {@code true} si el registro tiene datos suficientes para generar
 *              el XML, {@code false} si debe omitirse.
 */
	private boolean esNominaValida(Registro rs) {

	    String consecutivo = getRsRow(rs, "NIE009");
	    if (consecutivo == null || consecutivo.trim().isEmpty()) {
	        return false;
	    }

	    String numeroDocumento = getString(rs,"NIE045"); 
	    BigDecimal sueldo = getBigDecimal(rs,"NIE062");

	    boolean tieneBasico = numeroDocumento != null && !numeroDocumento.equals("") && numeroDocumento != "0"
	                       && sueldo != null && sueldo.doubleValue() > 0;

	    Double vacCantidad = getDouble(rs, "NIE111");
	    Double vacPago = getDouble(rs, "NIE112");

	    boolean tieneVacaciones = vacCantidad != null && vacCantidad > 0
	                          && vacPago != null && vacPago > 0;

	    Double incCantidad = getDouble(rs, "NIE125");
	    Double incPago = getDouble(rs, "NIE126");

	    boolean tieneIncapacidad = incCantidad != null && incCantidad > 0
	                           && incPago != null && incPago > 0;

	    return tieneBasico || tieneVacaciones || tieneIncapacidad;
	}
	
	/**
	 * Construye el modelo de datos completo ({@link NomElecGeneral}) para un empleado,
	 * consultando toda la información necesaria desde la BD a través de la URL
	 * {@code URL0006}. Este modelo es el insumo que {@link NominaXmlBuilder#build}
	 * convierte en XML.
	 *
	 * <p>La consulta retorna un único {@link Registro} con todos los campos del
	 * documento de nómina electrónica, identificados por códigos {@code NIExx}
	 * definidos por la DIAN. A partir de ese registro se construyen todas las
	 * secciones del modelo:</p>
	 *
	 * <p>Para nómina de ajuste ({@code esAjuste = true}) se agrega el bloque
	 * {@code predecesor} con operación {@code "R"} y el número del documento
	 * original referenciado ({@code NIE012}), y el prefijo/número de secuencia
	 * se antepone con {@code "A"}.</p>
	 *
	 * <p>Si el registro no existe o no pasa la validación de {@code esNominaValida},
	 * retorna {@code null} y muestra un mensaje de error en la interfaz.</p>
	 *
	 * @param tipoNomina      Tipo de nómina (ej: {@code "T_NE_BASE"} para base,
	 *                        {@code "T_NE_AJUSTE"} para ajuste).
	 * @param numDocumento    Número de documento del empleado.
	 * @param constipoNomina  Consecutivo del tipo de nómina.
	 * @param esAjuste        {@code true} si es nómina de ajuste; activa el bloque
	 *                        predecesor y el prefijo {@code "A"} en la secuencia.
	 * @return                Modelo {@link NomElecGeneral} con todos los datos del
	 *                        empleado listos para ser convertidos a XML, o {@code null}
	 *                        si el registro no es válido.
	 */
	public NomElecGeneral generarModeloXml(
	        String tipoNomina, String numDocumento, int constipoNomina,Boolean esAjuste) {

	    
	    NomElecGeneral general = new NomElecGeneral();

	    try {

	    	Map<String, Object> paramDet = new HashMap<>();
			paramDet.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
					compania);
			paramDet.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
					ano);
			paramDet.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
					mes);
			paramDet.put(ResumentotalcuneControladorEnum.UN_NUMERO_DCTO.getValue(),
					numDocumento);
			paramDet.put(ResumentotalcuneControladorEnum.UN_CONSEC.getValue(),
					constipoNomina);
			
				Registro rs = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										ResumentotalcuneControladorUrlEnum.URL0006
										.getValue())
								.getUrl(),
								paramDet));
			
				if(rs == null) {
		              JsfUtil.agregarMensajeError("No hay datos");
				}
				
				if (!esNominaValida(rs)) {
					JsfUtil.agregarMensajeError("Registro omitido por no tener datos válidos: " + numDocumento);
				    return null;
				}

	        // ================= ENCABEZADO =================
	        NomElecEncabezado enc = new NomElecEncabezado();
	        
		     // ================= PREDECESOR =================
	        if(esAjuste) {
	   	     NomElecEncabezado.Predecesor predecesor = new NomElecEncabezado.Predecesor();
	   	     predecesor.setOperacion("R");
	   	     predecesor.setNumeroPredecesor(getString(rs,"NIE012"));
	   	     enc.setPredecesor(predecesor);
	        }
	        


	     // ================= PERIODO (NIE002 - NIE006) =================
	     NomElecEncabezado.Periodo periodo = new NomElecEncabezado.Periodo();
	     periodo.setFechaIngreso(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE002"),"yyyy-MM-dd"));
	     periodo.setFechaRetiro(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE003"),"yyyy-MM-dd"));
	     periodo.setFechaLiquidacionInicio(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE004"),"yyyy-MM-dd"));
	     periodo.setFechaLiquidacionFin(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE005"),"yyyy-MM-dd"));
	     periodo.setTiempoLaborado(getBigDecimal(rs,"NIE006"));
	     enc.setPeriodo(periodo);


	     // ================= NUMERO SECUENCIA XML (NIE010-012) =================
	     NomElecEncabezado.NumeroSecuenciaXML sec = new NomElecEncabezado.NumeroSecuenciaXML();
	     if(esAjuste) {
	    	 
	    	 sec.setPrefijo("A" + getString(rs,"NIE010"));
	    	 sec.setConsecutivo(getLong(rs,"NIE011"));
	    	 sec.setNumero("A" + getString(rs,"NIE012"));
	    	 
	     }else {
	    	 
	    	 sec.setPrefijo(getString(rs,"NIE010"));
	    	 sec.setConsecutivo(getLong(rs,"NIE011"));
	    	 sec.setNumero(getString(rs,"NIE012"));
	    	 
	     }
	     enc.setNumeroSecuenciaXML(sec);


	     // ================= LUGAR GENERACION XML (NIE013-016) =================
	     NomElecEncabezado.LugarGeneracionXML lugar = new NomElecEncabezado.LugarGeneracionXML();
	     lugar.setPais(getString(rs,"NIE013"));
	     lugar.setDepartamento(getString(rs,"NIE014"));
	     lugar.setMunicipio(getString(rs,"NIE014")+ getString(rs,"NIE015"));
	     lugar.setIdioma(getString(rs,"NIE016"));
	     enc.setLugarGeneracionXML(lugar);


	     // ================= INFORMACION GENERAL (NIE029-031,200) =================
	     NomElecEncabezado.InformacionGeneral info = new NomElecEncabezado.InformacionGeneral();
	     info.setPeriodoNomina(getString(rs,"NIE029"));
	     info.setTipoMoneda(getString(rs,"NIE030"));
	     info.setTRM(getBigDecimal(rs,"NIE200"));
	     info.setNotas(getString(rs,"NIE031"));
	     enc.setInformacionGeneral(info);


	     // ================= EMPLEADOR (NIE032-038) =================
			
	     NomElecEncabezado.Empleador emp = new NomElecEncabezado.Empleador();
	     emp.setRazonSocial(SessionUtil.getCompaniaIngreso().getNombre());
	     emp.setPrimerApellido(getString(rs,"NIE210"));
	     emp.setSegundoApellido(getString(rs,"NIE211"));
	     emp.setPrimerNombre(getString(rs,"NIE212"));
	     emp.setOtrosNombres(getString(rs,"NIE213"));
	     emp.setNIT(SessionUtil.getCompaniaIngreso().getNit());
	     emp.setDV(Integer.parseInt(String.valueOf(ejbSysmanUtil
					.generarDigitoDeVerificacion(
							SessionUtil.getCompaniaIngreso().getNit()))));
	     emp.setPais(getString(rs,"NIE013"));
	     emp.setDepartamento(SessionUtil.getCompaniaIngreso().getCodigoDepartamento());
	     emp.setMunicipio(SessionUtil.getCompaniaIngreso().getCodigoDepartamento() + SessionUtil.getCompaniaIngreso().getCodigoCiudad());
	     emp.setDireccion(SessionUtil.getCompaniaIngreso().getDireccion());
	     enc.setEmpleador(emp);


	     // ================= TRABAJADOR (NIE041-063) =================
	     NomElecEncabezado.Trabajador trab = new NomElecEncabezado.Trabajador();
	     trab.setTipoTrabajador(getString(rs,"NIE041"));
	     trab.setSubtipoTrabajador(getString(rs,"NIE042"));
	     trab.setAltoRiesgoPension(getBoolean(rs,"NIE043"));
	     trab.setTipoDocumento(getString(rs,"NIE044"));
	     trab.setNumeroDocumento(getString(rs,"NIE045"));
	     trab.setPrimerApellido(getString(rs,"NIE046"));
	     trab.setSegundoApellido(getString(rs,"NIE047"));
	     trab.setPrimerNombre(getString(rs,"NIE048"));
	     trab.setOtrosNombres(getString(rs,"NIE049"));
	     trab.setPaisTrabajo(getString(rs,"NIE050"));
	     trab.setDepartamentoTrabajo(getString(rs,"NIE051"));
	     trab.setMunicipioTrabajo(getString(rs,"NIE051") + getString(rs,"NIE052"));
	     trab.setDireccionTrabajo(getString(rs,"NIE053"));
	     trab.setSalarioIntegral(getBoolean(rs,"NIE056"));
	     trab.setTipoContrato(getString(rs,"NIE061"));
	     trab.setSueldo(getBigDecimal(rs,"NIE062"));
	     trab.setCodigoTrabajador(getString(rs,"NIE063"));
	     enc.setTrabajador(trab);


	     // ================= PAGO (NIE064-068) =================
	     NomElecEncabezado.Pago pago = new NomElecEncabezado.Pago();
	     pago.setForma("1");
	     pago.setMedio(getString(rs,"NIE065"));
	     pago.setBanco(getString(rs,"NIE066"));
	     pago.setTipoCuenta(getString(rs,"NIE067"));
	     pago.setNumeroCuenta(getString(rs,"NIE068"));
	     enc.setPago(pago);


	     // ================= FECHAS DE PAGO (NIE203) =================
	     NomElecEncabezado.FechasPagos fechasPagos = new NomElecEncabezado.FechasPagos();
	     String fechaPago = SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE203"),"yyyy-MM-dd");


	     if (fechaPago != null) {
	         List<String> fechas = new ArrayList<>();
	         fechas.add(fechaPago);
	         fechasPagos.setFechaPago(fechas);
	     }

	     enc.setFechasPagos(fechasPagos);	     
	     general.setEncabezado(enc);
	     
	     

	     // ================= DEVENGADOS =================
	        NomElecDevengados dev = new NomElecDevengados();

	        // ================= BASICO =================
	        NomElecDevengados.Basico bas = new NomElecDevengados.Basico();
	        bas.setDiasTrabajados(getDouble(rs,"NIE069"));
	        bas.setSueldoTrabajado(getBigDecimal(rs,"NIE070"));
	        dev.setBasico(bas);

	        // ================= TRANSPORTE =================
	        NomElecDevengados.Transporte tr = new NomElecDevengados.Transporte();
	        tr.setAuxilioTransporte(getBigDecimal(rs,"NIE071"));
	        tr.setViaticoManuAlojS(getBigDecimal(rs,"NIE072"));
	        tr.setViaticoManuAlojNS(getBigDecimal(rs,"NIE073"));
	        dev.setTransporte(tr);

	        // ================= HORAS EXTRAS =================
	        List<NomElecDevengados.HorasExtrasDevengadas.HorasExtras> horas = new ArrayList<>();

			addHoraExtra(horas, "HED",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE074"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE075"), "yyyy-MM-dd HH:mm:ss"),
					25.0, getDouble(rs, "NIE076"), getDouble(rs, "NIE078"));
			addHoraExtra(horas, "HEN",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE079"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE080"), "yyyy-MM-dd HH:mm:ss"),
					75.0, getDouble(rs, "NIE081"), getDouble(rs, "NIE083"));
			addHoraExtra(horas, "HRN",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE084"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE085"), "yyyy-MM-dd HH:mm:ss"),
					35.0, getDouble(rs, "NIE086"), getDouble(rs, "NIE088"));
			addHoraExtra(horas, "HEDDF",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE089"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE090"), "yyyy-MM-dd HH:mm:ss"),
					100.0, getDouble(rs, "NIE091"), getDouble(rs, "NIE093"));
			addHoraExtra(horas, "HRDDF",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE094"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE095"), "yyyy-MM-dd HH:mm:ss"),
					75.0, getDouble(rs, "NIE096"), getDouble(rs, "NIE098"));
			addHoraExtra(horas, "HENDF",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE099"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE100"), "yyyy-MM-dd HH:mm:ss"),
					150.0, getDouble(rs, "NIE101"), getDouble(rs, "NIE103"));
			addHoraExtra(horas, "HRNDF",
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE104"), "yyyy-MM-dd HH:mm:ss"),
					SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE104"), "yyyy-MM-dd HH:mm:ss"),
					110.0, getDouble(rs, "NIE106"), getDouble(rs, "NIE108"));

			if(!horas.isEmpty()){
	            NomElecDevengados.HorasExtrasDevengadas hed = new NomElecDevengados.HorasExtrasDevengadas();
	            hed.setHorasExtras(horas);
	            dev.setHorasExtrasDevengadas(hed);
	        }

	        // ================= VACACIONES =================
	        NomElecDevengados.Vacaciones vac = new NomElecDevengados.Vacaciones();

	        List<NomElecDevengados.Vacaciones.VacacionesComunes> comunes = new ArrayList<>();
	        if(getDouble(rs,"NIE111") != null){
	            NomElecDevengados.Vacaciones.VacacionesComunes vc =
	                    new NomElecDevengados.Vacaciones.VacacionesComunes();
	            vc.setFechaInicio(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE109"), "yyyy-MM-dd"));
	            vc.setFechaFin(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE110"), "yyyy-MM-dd"));
	            vc.setCantidad(cantidadDian(getRsRow(rs, "NIE111")));
	            vc.setPago(getBigDecimal(rs, "NIE112"));
	            comunes.add(vc);
	        }
	        vac.setVacacionesComunes(comunes);

	        List<NomElecDevengados.Vacaciones.VacacionesCompensadas> compensadas = new ArrayList<>();
	        if(getDouble(rs,"NIE115") != null){
	            NomElecDevengados.Vacaciones.VacacionesCompensadas vcomp =
	                    new NomElecDevengados.Vacaciones.VacacionesCompensadas();
	            vcomp.setCantidad(getDouble(rs,"NIE115"));
	            vcomp.setPago(getBigDecimal(rs,"NIE116"));
	            compensadas.add(vcomp);
	        }
	        vac.setVacacionesCompensadas(compensadas);

	        dev.setVacaciones(vac);

	        // ================= PRIMAS =================
	        NomElecDevengados.Primas pri = new NomElecDevengados.Primas();
	        pri.setCantidad(getDouble(rs,"NIE117"));
	        pri.setPago(getBigDecimal(rs,"NIE118"));
	        pri.setPagoNS(getBigDecimal(rs,"NIE119"));
	        dev.setPrimas(pri);

	        // ================= CESANTIAS =================
	        NomElecDevengados.Cesantias ces = new NomElecDevengados.Cesantias();
	        ces.setPago(getBigDecimal(rs,"NIE120"));
	        ces.setPorcentaje(getDouble(rs,"NIE121"));
	        ces.setPagoIntereses(getBigDecimal(rs,"NIE122"));
	        dev.setCesantias(ces);

	        // ================= INCAPACIDADES =================
	        List<NomElecDevengados.Incapacidades.Incapacidad> incapList = new ArrayList<>();
	        if(getDouble(rs,"NIE125") != null){
	            NomElecDevengados.Incapacidades.Incapacidad inc =
	                    new NomElecDevengados.Incapacidades.Incapacidad();
	            inc.setFechaInicio(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE123"), "yyyy-MM-dd"));
	            inc.setFechaFin(SysmanFunciones.convertirAFechaCadena((Date) rs.getCampos().get("NIE124"), "yyyy-MM-dd"));
	            inc.setCantidad(getDouble(rs,"NIE125"));
	            inc.setTipo(getString(rs,"NIE126"));
	            inc.setPago(getBigDecimal(rs,"NIE127"));
	            incapList.add(inc);
	        }
	        if(!incapList.isEmpty()){
	            NomElecDevengados.Incapacidades incap = new NomElecDevengados.Incapacidades();
	            incap.setIncapacidad(incapList);
	            dev.setIncapacidades(incap);
	        }

	     // ================= LICENCIAS =================
	        List<NomElecDevengados.Licencias.LicenciaMP> mpList = new ArrayList<>();
	        List<NomElecDevengados.Licencias.LicenciaR> rList = new ArrayList<>();
	        List<NomElecDevengados.Licencias.LicenciaNR> nrList = new ArrayList<>();

	        // ----- Licencia MP -----
	        if (getDouble(rs,"NIE130") != null) {

	            NomElecDevengados.Licencias.LicenciaMP mp =
	                    new NomElecDevengados.Licencias.LicenciaMP();

	            mp.setFechaInicio(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE128"), "yyyy-MM-dd"));

	            mp.setFechaFin(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE129"), "yyyy-MM-dd"));

	            mp.setCantidad(getDouble(rs,"NIE130"));
	            mp.setPago(getBigDecimal(rs,"NIE131"));

	            mpList.add(mp);
	        }

	        // ----- Licencia Remunerada -----
	        if (getDouble(rs,"NIE134") != null) {

	            NomElecDevengados.Licencias.LicenciaR lr =
	                    new NomElecDevengados.Licencias.LicenciaR();

	            lr.setFechaInicio(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE132"), "yyyy-MM-dd"));

	            lr.setFechaFin(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE133"), "yyyy-MM-dd"));

	            lr.setCantidad(getDouble(rs,"NIE134"));
	            lr.setPago(getBigDecimal(rs,"NIE135"));

	            rList.add(lr);
	        }

	        // ----- Licencia No Remunerada -----
	        if (getDouble(rs,"NIE138") != null) {

	            NomElecDevengados.Licencias.LicenciaNR lnr =
	                    new NomElecDevengados.Licencias.LicenciaNR();

	            lnr.setFechaInicio(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE136"), "yyyy-MM-dd"));

	            lnr.setFechaFin(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE137"), "yyyy-MM-dd"));

	            lnr.setCantidad(getDouble(rs,"NIE138"));

	            nrList.add(lnr);
	        }

	        if (!mpList.isEmpty() || !rList.isEmpty() || !nrList.isEmpty()) {

	            NomElecDevengados.Licencias licencias =
	                    new NomElecDevengados.Licencias();

	            if (!mpList.isEmpty()) {
	                licencias.setLicenciaMP(mpList);
	            }

	            if (!rList.isEmpty()) {
	                licencias.setLicenciaR(rList);
	            }

	            if (!nrList.isEmpty()) {
	                licencias.setLicenciaNR(nrList);
	            }

	            dev.setLicencias(licencias);
	        }
	        
	     // ================= BONIFICACIONES =================
	        List<NomElecDevengados.Bonificaciones.Bonificacion> bonList = new ArrayList<>();

	        if (getDouble(rs,"NIE139") != null || getDouble(rs,"NIE140") != null) {

	            NomElecDevengados.Bonificaciones.Bonificacion bon =
	                    new NomElecDevengados.Bonificaciones.Bonificacion();

	            bon.setBonificacionS(getDouble(rs,"NIE139"));
	            bon.setBonificacionNS(getDouble(rs,"NIE140"));

	            bonList.add(bon);
	        }

	        if (!bonList.isEmpty()) {
	            NomElecDevengados.Bonificaciones bonificaciones =
	                    new NomElecDevengados.Bonificaciones();
	            bonificaciones.setBonificacion(bonList);
	            dev.setBonificaciones(bonificaciones);
	        }
	        
	     // ================= AUXILIOS =================
	        List<NomElecDevengados.Auxilios.Auxilio> auxList = new ArrayList<>();

	        if (getDouble(rs,"NIE141") != null || getDouble(rs,"NIE142") != null) {

	            NomElecDevengados.Auxilios.Auxilio aux =
	                    new NomElecDevengados.Auxilios.Auxilio();

	            aux.setAuxilioS(getBigDecimal(rs,"NIE141"));
	            aux.setAuxilioNS(getBigDecimal(rs,"NIE142"));

	            auxList.add(aux);
	        }

	        if (!auxList.isEmpty()) {
	            NomElecDevengados.Auxilios auxilios =
	                    new NomElecDevengados.Auxilios();
	            auxilios.setAuxilio(auxList);
	            dev.setAuxilios(auxilios);
	        }
	        
	     // ================= HUELGAS LEGALES =================
	        List<NomElecDevengados.HuelgasLegales.HuelgaLegal> huelgaList = new ArrayList<>();

	        if (getDouble(rs,"NIE145") != null) {

	            NomElecDevengados.HuelgasLegales.HuelgaLegal hl =
	                    new NomElecDevengados.HuelgasLegales.HuelgaLegal();

	            hl.setFechaInicio(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE143"), "yyyy-MM-dd"));

	            hl.setFechaFin(SysmanFunciones.convertirAFechaCadena(
	                    (Date) rs.getCampos().get("NIE144"), "yyyy-MM-dd"));

	            hl.setCantidad(getDouble(rs,"NIE145"));

	            huelgaList.add(hl);
	        }

	        if (!huelgaList.isEmpty()) {
	            NomElecDevengados.HuelgasLegales huelgas =
	                    new NomElecDevengados.HuelgasLegales();
	            huelgas.setHuelgaLegal(huelgaList);
	            dev.setHuelgasLegales(huelgas);
	        }
	        
	     // ================= OTROS CONCEPTOS =================
	        List<NomElecDevengados.OtrosConceptos.OtroConcepto> otrosList = new ArrayList<>();

	        if (getString(rs,"NIE146") != null) {

	            NomElecDevengados.OtrosConceptos.OtroConcepto oc =
	                    new NomElecDevengados.OtrosConceptos.OtroConcepto();

	            oc.setDescripcionConcepto(getString(rs,"NIE146"));
	            oc.setConceptoS(getDouble(rs,"NIE147"));
	            oc.setConceptoNS(getDouble(rs,"NIE148"));

	            otrosList.add(oc);
	        }

	        if (!otrosList.isEmpty()) {
	            NomElecDevengados.OtrosConceptos otros =
	                    new NomElecDevengados.OtrosConceptos();
	            otros.setOtroConcepto(otrosList);
	            dev.setOtrosConceptos(otros);
	        }
	        
	     // ================= COMPENSACIONES =================
	        List<NomElecDevengados.Compensaciones.Compensacion> compList = new ArrayList<>();

	        if (getDouble(rs,"NIE149") != null || getDouble(rs,"NIE150") != null) {

	            NomElecDevengados.Compensaciones.Compensacion comp =
	                    new NomElecDevengados.Compensaciones.Compensacion();

	            comp.setCompensacionO(getDouble(rs,"NIE149"));
	            comp.setCompensacionE(getDouble(rs,"NIE150"));

	            compList.add(comp);
	        }

	        if (!compList.isEmpty()) {
	            NomElecDevengados.Compensaciones compensaciones =
	                    new NomElecDevengados.Compensaciones();
	            compensaciones.setCompensacion(compList);
	            dev.setCompensaciones(compensaciones);
	        }
	        
	     // ================= BONOS EPCTV =================
	        List<NomElecDevengados.BonoEPCTVs.BonoEPCTV> bonoList = new ArrayList<>();

	        if (getDouble(rs,"NIE151") != null) {

	            NomElecDevengados.BonoEPCTVs.BonoEPCTV bono =
	                    new NomElecDevengados.BonoEPCTVs.BonoEPCTV();

	            bono.setPagoS(getBigDecimal(rs,"NIE151"));
	            bono.setPagoNS(getBigDecimal(rs,"NIE152"));
	            bono.setPagoAlimentacionS(getBigDecimal(rs,"NIE153"));
	            bono.setPagoAlimentacionNS(getBigDecimal(rs,"NIE154"));

	            bonoList.add(bono);
	        }

	        if (!bonoList.isEmpty()) {
	            NomElecDevengados.BonoEPCTVs bonos =
	                    new NomElecDevengados.BonoEPCTVs();
	            bonos.setBonoEPCTV(bonoList);
	            dev.setBonoEPCTVs(bonos);
	        }
	        
	        
	       // ================= COMISIONES =================
	        List<Double> comList = new ArrayList<>();

	        if (getDouble(rs,"NIE155") != null) {
	            comList.add(getDouble(rs,"NIE155"));
	        }

	        if (!comList.isEmpty()) {

	            NomElecDevengados.Comisiones comisiones =
	                    new NomElecDevengados.Comisiones();

	            comisiones.setComision(comList);

	            dev.setComisiones(comisiones);
	        }
	        
	        // ================= OTROS DEVENGOS =================
	        NomElecDevengados.OtrosDevengos otros = new NomElecDevengados.OtrosDevengos();
	        otros.setDotacion(getBigDecimal(rs,"NIE156"));
	        otros.setApoyoSost(getBigDecimal(rs,"NIE157"));
	        otros.setTeletrabajo(getBigDecimal(rs,"NIE158"));
	        otros.setBonifRetiro(getBigDecimal(rs,"NIE159"));
	        otros.setIndemnizacion(getBigDecimal(rs,"NIE160"));
	        otros.setReintegro(getBigDecimal(rs,"NIE201"));
	        dev.setOtrosDevengos(otros);

	        // ================= PAGOS TERCEROS =================
	        if(getDouble(rs,"NIE193") != null){
	            NomElecDevengados.PagosTerceros pt = new NomElecDevengados.PagosTerceros();
	            pt.setPagoTercero(Arrays.asList(getBigDecimal(rs,"NIE193")));
	            dev.setPagosTerceros(pt);
	        }

	        // ================= ANTICIPOS =================
	        if(getDouble(rs,"NIE194") != null){
	            NomElecDevengados.Anticipos ant = new NomElecDevengados.Anticipos();
	            ant.setAnticipo(Arrays.asList(getDouble(rs,"NIE194")));
	            dev.setAnticipos(ant);
	        }

	        general.setDevengados(dev);

	        // ================= DEDUCCIONES =================
	        NomElecDeducciones ded = new NomElecDeducciones();

	        // ================= SALUD =================
	        Double val = getDouble(rs, "NIE163");

	        BigDecimal saludValor = (val != null)
	                ? BigDecimal.valueOf(val)
	                : BigDecimal.ZERO;

	        if (saludValor.compareTo(BigDecimal.ZERO) > 0) {
	            NomElecDeducciones.Salud salud = new NomElecDeducciones.Salud();
	            salud.setPorcentaje(BigDecimal.valueOf(4.0));
	            salud.setDeduccion(saludValor);
	            ded.setSalud(salud);
	        }
	        // ================= PENSION =================

	        Double pensionValor = getDouble(rs,"NIE166");

	        if (pensionValor != null && pensionValor > 0) {
	            NomElecDeducciones.FondoPension pension =
	                    new NomElecDeducciones.FondoPension();
	            pension.setPorcentaje(BigDecimal.valueOf(4.0));
	            pension.setDeduccion(BigDecimal.valueOf(pensionValor));
	            ded.setFondoPension(pension);
	        }
	        
	        Double fsp = getDouble(rs,"NIE168");
	        Double fspAdicional = getDouble(rs,"NIE170");

	        if ((fsp != null && fsp > 0) || (fspAdicional != null && fspAdicional > 0)) {

	            NomElecDeducciones.FondoSP fondoSP =
	                    new NomElecDeducciones.FondoSP();

	            fondoSP.setDeduccionSP(BigDecimal.valueOf(fsp));
	            fondoSP.setDeduccionSub(BigDecimal.valueOf(fspAdicional)); 

	            ded.setFondoSP(fondoSP);
	        }
	        
	        // ================= SINDICATO =================
	     Double sindicatoValor = getDouble(rs,"NIE172");
	     if (sindicatoValor != null && sindicatoValor > 0) {
	         NomElecDeducciones.Sindicato sindicato = new NomElecDeducciones.Sindicato();
	         
	         String condNie171 = new StringBuilder(SysmanFunciones
						.nvlStr("0".equals(getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE171
								.getValue()))
								? "1"
										: getRsRow(rs,
												ResumentotalcuneControladorEnum.NIE171
												.getValue()),
										"1")).append(".00").toString();
	         
	         String porcentaje = "0".equals(sindicatoValor)? "0"	: condNie171;
								
			 sindicato.setPorcentaje(Double.parseDouble(porcentaje));
	         sindicato.setDeduccion(sindicatoValor);
	         ded.getSindicatos().add(sindicato);
	     }
	        // ================= SANCIONES =================

	     Double sancionPublica = getDouble(rs,"NIE173");
	     Double sancionPrivada = getDouble(rs,"NIE174");

	     if ((sancionPublica != null && sancionPublica > 0) ||
	         (sancionPrivada != null && sancionPrivada > 0)) {

	         NomElecDeducciones.Sanciones sanciones = new NomElecDeducciones.Sanciones();
	         NomElecDeducciones.Sancion sancion = new NomElecDeducciones.Sancion();

	         sancion.setSancionPublic(sancionPublica);
	         sancion.setSancionPriv(sancionPrivada);

	         sanciones.getSanciones().add(sancion);
	         ded.setSanciones(sanciones);
	     }
	     
	        // ================= LIBRANZAS =================
	     Double libranzaValor = getDouble(rs,"NIE176");
	     if (libranzaValor != null && libranzaValor > 0) {
	         NomElecDeducciones.Libranza libranza = new NomElecDeducciones.Libranza();
	         libranza.setDescripcion("Libranza nómina");
	         libranza.setDeduccion(libranzaValor);
	         ded.getLibranzas().add(libranza);
	     }
	        // ================= PAGOS TERCEROS =================
	     Double pagoTercero = getDouble(rs,"NIE195");
	     if (pagoTercero != null && pagoTercero > 0) {
	    	 NomElecDeducciones.PagosTerceros pt = new NomElecDeducciones.PagosTerceros();
	    	 pt.getPagos().add(pagoTercero);
	    	 ded.setPagosTerceros(pt);
	     }
	        // ================= ANTICIPOS =================
	     Double anticipo = getDouble(rs,"NIE196");
	     if (anticipo != null && anticipo > 0) {
	    	 NomElecDeducciones.Anticipos ant = new NomElecDeducciones.Anticipos();
	    	 ant.getAnticipos().add(anticipo);
	    	 ded.setAnticipos(ant);
	     }
	        // ================= OTRAS DEDUCCIONES =================
	     Double otras = getDouble(rs,"NIE197");
	     if (otras != null && otras > 0) {
	    	 if (ded.getOtrasDeducciones() == null) {
	    		 ded.setOtrasDeducciones(new NomElecDeducciones.OtrasDeducciones());
	    	 }
	    	 ded.getOtrasDeducciones().getOtraDeduccion().add(otras);
	     }
	     
	     Double pensionVoluntaria = getDouble(rs,"NIE198");

	     if (pensionVoluntaria != null && pensionVoluntaria > 0) {
	         ded.setPensionVoluntaria(pensionVoluntaria);
	     }
	     
	     ded.setRetencionFuente(getDouble(rs,"NIE177"));

	     ded.setAfc(getDouble(rs,"NIE179"));

	     ded.setCooperativa(getDouble(rs,"NIE180"));

	     ded.setEmbargoFiscal(getDouble(rs,"NIE181"));

	     ded.setPlanComplementarios(getDouble(rs,"NIE182"));

	     ded.setEducacion(getDouble(rs,"NIE183"));

	     ded.setReintegro(getDouble(rs,"NIE184"));

	     ded.setDeuda(getDouble(rs,"NIE185"));


	        general.setDeducciones(ded);

	        // ================= TOTALES =================
	        NomElecTotales tot = new NomElecTotales();

	        tot.setRedondeo(getBigDecimal(rs,"NIE186"));
	        BigDecimal devengados = getBigDecimal(rs,"NIE187");
	        BigDecimal deducciones = getBigDecimal(rs,"NIE188");
	        
	        if (devengados == null) devengados = BigDecimal.ZERO;
	        if (deducciones == null) deducciones = BigDecimal.ZERO;

	        BigDecimal totalComprobante = devengados
	                .subtract(deducciones)
	                .setScale(2, RoundingMode.HALF_UP);

	        tot.setDevengadosTotal(devengados);
	        tot.setDeduccionesTotal(deducciones);
	        tot.setComprobanteTotal(totalComprobante);

	        general.setTotalesGenerales(tot);

	    } catch (Exception e) {
	        logger.error("Error generando modelo XML nómina", e);
	    }

	    return general;
	}
	
    
    public static LocalDateTime getLocalDateTime(ResultSet rs, String column) {
        try {
            Object value = rs.getObject(column);
            if (value == null) return null;

            if (value instanceof Timestamp)
                return ((Timestamp) value).toLocalDateTime();

            if (value instanceof LocalDateTime)
                return (LocalDateTime) value;

            return LocalDateTime.parse(value.toString());

        } catch (Exception e) {
            throw new RuntimeException("Error leyendo columna LocalDateTime: " + column, e);
        }
    }

    public static String getHoraColombia() {
        OffsetDateTime now = OffsetDateTime.now(COLOMBIA_ZONE);
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ssXXX"));
    }

	public static Integer getInteger(Registro rs, String column) {
        try {
            Object value = rs.getCampos().get(column);
            if (value == null) return null;
            if (value instanceof Integer) return (Integer) value;
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo columna Integer: " + column, e);
        }
    }

	public static BigDecimal getBigDecimal(Registro rs, String column) {
	    try {
	        Object value = rs.getCampos().get(column);
	        if (value == null) return null;

	        BigDecimal bd;

	        if (value instanceof BigDecimal) {
	            bd = (BigDecimal) value;
	        } else {
	            bd = new BigDecimal(value.toString());
	        }

	        return bd.setScale(2, RoundingMode.HALF_UP);

	    } catch (Exception e) {
	        throw new RuntimeException("Error leyendo columna BigDecimal: " + column, e);
	    }
	}

	public static Long getLong(Registro rs, String column) {
        try {
            Object value = rs.getCampos().get(column);
            if (value == null) return null;
            if (value instanceof Long) return (Long) value;
            return Long.parseLong(value.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo columna Long: " + column, e);
        }
    }

	private String getString(Registro rs, String column) {
        String value = SysmanFunciones.toString(rs.getCampos().get(column));
		if (value == null) return null;
		value = value.trim();
		return value.isEmpty() ? null : value;
    }
    
    public static Boolean getBoolean(Registro rs, String column) {
        try {
            Object value = rs.getCampos().get(column);
            if (value == null) return null;

            if (value instanceof Boolean) return (Boolean) value;

            String str = value.toString().trim().toLowerCase();

            if (str.equals("1") || str.equals("true") || str.equals("s"))
                return true;

            if (str.equals("0") || str.equals("false") || str.equals("n"))
                return false;

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo columna Boolean: " + column, e);
        }
    }

	private void addHoraExtra(
	        List<NomElecDevengados.HorasExtrasDevengadas.HorasExtras> lista,
	        String tipo,
	        String HoraInicio, String HoraFin, Double porcentaje,
	        Double cantidad,
	        Double pago) {

	    if (cantidad != null && cantidad > 0) {
	        NomElecDevengados.HorasExtrasDevengadas.HorasExtras h =
	                new NomElecDevengados.HorasExtrasDevengadas.HorasExtras();
	        h.setTipo(tipo);
	        h.setHoraInicio(HoraInicio);
	        h.setHoraFin(HoraFin);
	        h.setCantidad(cantidad);
	        h.setPorcentaje(porcentaje);
	        h.setPago(pago);
	        lista.add(h);
	    }
	}

	private Double getDouble(Registro rs, String key) {
	    Object val = rs.getCampos().get(key);
	    if (val == null) return 0d;
	    return Double.parseDouble(val.toString());
	}
	
	
	
	
	/**
	 * Método disparado desde el botón de envío en la interfaz. Gestiona la generación
	 * y descarga del XML de nómina electrónica (individual o masivo).
	 *
	 * <p>El flujo cuando {@code soloXML = true}, que es el usado para
	 * la integración con Invoway es:</p>
	 * <ol>
	 *   <li>Consulta los empleados según compañía, año, mes y rango de
	 *       documento ({@code empleadoIni} hasta {@code empleadoFin}).</li>
	 *   <li>Por cada empleado llama a {@link #generarModeloXml} para construir el
	 *       modelo de datos.</li>
	 *   <li>Pasa el modelo a {@link NominaXmlBuilder#build} para generar el XML.</li>
	 *   <li>Normaliza el XML con {@link #normalizarXml}.</li>
	 *   <li>Si es un solo empleado, descarga el XML directamente. Si son varios,
	 *       los empaqueta en un ZIP.</li>
	 * </ol>
	 *
	 * <p>El nombre del archivo generado sigue la nomenclatura exigida por la DIAN:
	 * {@code NE_NITCOMPANIA_AA_CONSECUTIVO.xml} para nómina base y
	 * {@code ANE_NITCOMPANIA_AA_CONSECUTIVO.xml} para nómina de ajuste.</p>
	 *
	 * 
	 * @see #generarModeloXml
	 * @see NominaXmlBuilder#build
	 */
	public void oprimirBtEjcJson() {
		String resEPR;
		String log;
		boolean estado = false;
		boolean genLog = false;
		log = "|---------------     ENVIO NOMINA ELECTRONICA      ---------------|";
		
		archivoDescarga = null;
		Map<String, Object> paramCune = new TreeMap<>();
		paramCune.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
				compania);

		paramCune.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
				ano);

		paramCune.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
				mes);
		if (soloXML) {
			paramCune.put(ResumentotalcuneControladorEnum.UN_TIPONOM.getValue(), ResumentotalcuneControladorEnum.NOM_BASE.getValue());
		} else {
			paramCune.put(ResumentotalcuneControladorEnum.UN_TIPONOM.getValue(), tipoNomGenJson);
		}

		paramCune.put("UN_NUMERODOC_INI", empleadoIni);
		paramCune.put("UN_NUMERODOC_FIN", empleadoFin);

		try {
			List<Registro> lRs1 = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0004
									.getValue())
							.getUrl(),
							paramCune));
			if (lRs1.isEmpty()) {
				JsfUtil.agregarMensajeInformativo(
						idioma.getString(
								ResumentotalcuneControladorEnum.TB_TB4255
								.getValue()));
			}
			else {
				
				// Consulto los datos del certificado
                String url = ejbSysmanUtil.consultarParametro(compania,
                                "URL SERVICIO REST NOMINA ELECTRONICA",
                                SessionUtil.getModulo(),
                                new Date(), false);

				
				if (!soloJson && !soloXML) {
					PojoNominaElectronica datosNomina = new PojoNominaElectronica();
					consultarCertificado(datosNomina);
					ByteArrayInputStream[] salidas = new ByteArrayInputStream[lRs1
					                                                          .size() + 1];
					String[] nombresArchivos = new String[lRs1
					                                      .size() + 1];
					String par31 = getNitPar31().replace(".", "").replace("-", "");
					if (par31.length() > 9) {
						par31 = par31.substring(0, 9);
					}

					int i = 0;
					for (Registro rs1 : lRs1) {
						datosNomina.setDatosNomina(generarJsonDetallado(rs1
								.getCampos()
								.get(ResumentotalcuneControladorEnum.NUMDCTO
										.getValue())
								.toString(),
								Integer.parseInt(rs1.getCampos()
										.get(ResumentotalcuneControladorEnum.CONS_TIPONOMINA
												.getValue())
										.toString()),
								tipoNomGenJson, par31));
						// Genera el Json para imprimirlos

						GsonBuilder builder = new GsonBuilder();
						builder.setPrettyPrinting();
						Gson gson = builder.create();
						String json = gson.toJson(datosNomina, PojoNominaElectronica.class);	
						json = json.replaceAll("\\[\\s*\\]", "[{ \n\t}]");
						json = json.replaceAll("\\[\\s*\\{", "[{");
						json = json.replaceAll("\\  }\\s*\\]", "}]");						

						salidas[i] = JsfUtil.serializarPlano(json);
						nombresArchivos[i] = new StringBuilder("JsonNomina")
								.append(rs1.getCampos().get(
										ResumentotalcuneControladorEnum.NOMBRECOMPLETO
										.getValue())
										.toString())
								.append(convertirFechaFormato(
										(Date) rs1.getCampos().get(
												ResumentotalcuneControladorEnum.NIE008
												.getValue()),"yyyyMMdd"))
								.append("_")
								.append(convertirFechaFormato(
										(Date) rs1.getCampos().get(
												ResumentotalcuneControladorEnum.NIE008
												.getValue()),"HHmmss"))
								.append(".json")
								.toString();
						
						//enviarPayRoll(url, json);
						estado = ejbNominaUno.actEstNominaCune(
 																compania,
 																Integer.parseInt(ano),
 																Integer.parseInt(mes),
 																tipoNomGenJson, 
 																Integer.parseInt(rs1.getCampos().get(ResumentotalcuneControladorEnum.CONS_TIPONOMINA.getValue()).toString()),
 																rs1.getCampos().get(ResumentotalcuneControladorEnum.NUMDCTO.getValue()).toString(),
 																"C"
	        		            								);
						
						if (!estado ) {
							resEPR = enviarPayRoll2(url, json);
							if ( !"0".equals(resEPR)) {
								if ( genLog == false ) {
									genLog = true;
								}
								log = log + "\n" + rs1
										.getCampos()
										.get(ResumentotalcuneControladorEnum.NUMDCTO
												.getValue())
										.toString()
										+ " - No enviado: "
										+ resEPR.replace("\n","");
							} 
							else {
								if ( tipoNomGenJson.equals("T_NE_BASE")) {
									estado = ejbNominaUno.actEstNominaCune(
			 								compania,
			 								Integer.parseInt(ano),
			 								Integer.parseInt(mes),
			 								tipoNomGenJson, 
			 								Integer.parseInt(rs1.getCampos().get(ResumentotalcuneControladorEnum.CONS_TIPONOMINA.getValue()).toString()),
			 								rs1.getCampos().get(ResumentotalcuneControladorEnum.NUMDCTO.getValue()).toString(),
				        		            "A"
				        		            );
									
								}
							}
						}
						else {
							if ( genLog == false ) {
								genLog = true;
							}
							log = log + "\n" + rs1
									.getCampos()
									.get(ResumentotalcuneControladorEnum.NUMDCTO
											.getValue())
									.toString()
									+ " - No enviado: "
									+ idioma.getString("MSG_NOMINAELECTRONICA_ENVIADO").replace("\n","");
						}
						i++;

					}
					
					//Archivo LOG
					if (genLog) {			
						salidas[i] = JsfUtil.serializarPlano(log);
						nombresArchivos[i] = "EnvioNomina.log"; 
					}
					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
					archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
							salidas, nombresArchivos);
					JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
				}else {
				    if (soloXML) {

				    	Boolean esAjuste = ResumentotalcuneControladorEnum.NOM_AJUSTE.getValue().equals(tipoNomGenJson)?true:false;
						
				        try {

				            if (lRs1.size() == 1) {

				                Registro rs1 = lRs1.get(0);

				                // ================= 1. Generar modelo =================
				                NomElecGeneral modelo = generarModeloXml(
				                        tipoNomGenJson,
				                        rs1.getCampos()
				                                .get(ResumentotalcuneControladorEnum.NUMDCTO.getValue())
				                                .toString(),
				                        Integer.parseInt(
				                                rs1.getCampos()
				                                        .get(ResumentotalcuneControladorEnum.CONS_TIPONOMINA.getValue())
				                                        .toString()
				                        ),esAjuste
				                );
				                
				                Date fechaActual = new Date();
				                String anoActual = SysmanFunciones.toString(SysmanFunciones.ano(fechaActual));

				                // ================= 2. Generar XML =================
				                NominaXmlBuilder builder = new NominaXmlBuilder();
				                String xmlGenerado = builder.build(modelo, esAjuste);

				                // ================= 3. Normalizar =================
				                String xmlFinal = normalizarXml(xmlGenerado);

				                // ================= 4. Descargar XML =================
				                String indicador =esAjuste?"ANE":"NE";
				                String nombreArchivo = indicador + "_" +
		                        		SessionUtil.getCompaniaIngreso().getNit().toString() + "_" +
		                        		anoActual.substring(2) + "_" +
		                                modelo.getEncabezado()
		                                        .getNumeroSecuenciaXML()
		                                        .getNumero();
				                
				                
				                archivoDescarga =
				                        JsfUtil.exportarXmlStreamed(nombreArchivo, xmlFinal);

				            } else {

				                ByteArrayInputStream[] salidas =
				                        new ByteArrayInputStream[lRs1.size()];

				                String[] nombresArchivos =
				                        new String[lRs1.size()];

				                int i = 0;

				                for (Registro rs1 : lRs1) {

				                    try {

				                        NomElecGeneral modelo = generarModeloXml(
				                                tipoNomGenJson,
				                                rs1.getCampos()
				                                        .get(ResumentotalcuneControladorEnum.NUMDCTO.getValue())
				                                        .toString(),
				                                Integer.parseInt(
				                                        rs1.getCampos()
				                                                .get(ResumentotalcuneControladorEnum.CONS_TIPONOMINA.getValue())
				                                                .toString()
				                                ),esAjuste
				                        );

				                        if(!(modelo==null)) {
				                        NominaXmlBuilder builder = new NominaXmlBuilder();
				                        
				                        String xmlGenerado = builder.build(modelo, esAjuste);

				                        String xmlFinal = normalizarXml(xmlGenerado);

				                        salidas[i] = new ByteArrayInputStream(
				                                xmlFinal.getBytes("UTF-8")
				                        );

				                        /*
				                         * Documento de pago de nómina.
											Se debe generar un documento de este tipo por cada empleado-mes. El nombre del archivo seguirá la
											siguiente nomenclatura: NE_IDFISCALPROVEEDOR_AÑO_CONSECUTIVO.xml.
											 NE – Nómina electrónica.
											 IDFISCAL – NIT de la ENTIDAD emisora de las facturas sin dígito de control.
											 AÑO – dos últimos dígitos del año.
											 CONSECUTIVO – consecutivo de archivos enviados.
				                         */
				                        
				                        Date fechaActual = new Date();
				                        String anoActual = SysmanFunciones.toString(SysmanFunciones.ano(fechaActual));
				                        
				                        String indicador = esAjuste?"ANE":"NE";
				                        nombresArchivos[i] = indicador + "_" +
				                        		SessionUtil.getCompaniaIngreso().getNit().toString() + "_" +
				                        		anoActual.substring(2) + "_" +
				                                modelo.getEncabezado()
				                                        .getNumeroSecuenciaXML()
				                                        .getNumero()
				                                + ".xml";

				                        i++;
				                        }

				                    } catch (Exception exEmpleado) {
				                        exEmpleado.printStackTrace();
				                        JsfUtil.agregarMensajeError(
				                                "Error generando XML  "
				                        );
				                    }
				                }

				                archivoDescarga =
				                        JsfUtil.exportarComprimidoGeneralStreamed(
				                                salidas,
				                                nombresArchivos
				                        );
				            }

				        } catch (Exception e) {
				            e.printStackTrace();
				            JsfUtil.agregarMensajeError(
				                    "Error generando XML de nómina: " + e.getMessage()
				            );
				        }
				        
					}else {
						
					DatosNomElectronica datosNomina = new DatosNomElectronica();
					consultarDatosEntidad(datosNomina);
					ByteArrayInputStream[] salidas = new ByteArrayInputStream[lRs1
					                                                          .size()];
					String[] nombresArchivos = new String[lRs1
					                                      .size()];
					String par31 = getNitPar31().replace(".", "").replace("-", "");
					if (par31.length() > 9) {
						par31 = par31.substring(0, 9);
					}

					int i = 0;
					for (Registro rs1 : lRs1) {
						datosNomina.setDatosNomina(generarJsonDetallado(rs1
								.getCampos()
								.get(ResumentotalcuneControladorEnum.NUMDCTO
										.getValue())
								.toString(),
								Integer.parseInt(rs1.getCampos()
										.get(ResumentotalcuneControladorEnum.CONS_TIPONOMINA
												.getValue())
										.toString()),
								tipoNomGenJson, par31));
						// Genera el Json para imprimirlos

						GsonBuilder builder = new GsonBuilder();
						builder.setPrettyPrinting();
						Gson gson = builder.create();
						String json = gson.toJson(datosNomina, DatosNomElectronica.class);
						json = json.replaceAll("\\[\\s*\\]", "[{ \n\t}]");
						json = json.replaceAll("\\[\\s*\\{", "[{");
						json = json.replaceAll("\\  }\\s*\\]", "}]");	

						salidas[i] = JsfUtil.serializarPlano(json);
						nombresArchivos[i] = new StringBuilder("")
								.append(ano + "_" + mes + "_")
								.append(rs1.getCampos().get(
										ResumentotalcuneControladorEnum.NOMBRECOMPLETO
										.getValue())
										.toString())
								.append("_")
								.append(rs1.getCampos().get(
										ResumentotalcuneControladorEnum.NUMDCTO
										.getValue())
										.toString())
								.append("_")
								.append(rs1.getCampos().get(
										ResumentotalcuneControladorEnum.CONS_TIPONOMINA
										.getValue())
										.toString())
								.append("_json.txt")
								.toString();

						i++;

					}

					archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
							salidas, nombresArchivos);
					}
				}
			}
		}
		catch (SystemException | JRException | IOException | SQLException
				| DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Realiza el consumo del api Payroll
	 * 
	 * @param url
	 * Url base del servicio
	 * @param json
	 * Json para validar
	 * @throws SystemException
	 */
	private void enviarPayRoll(String url, String json) throws SystemException {
		ApiPayRoll api = new ApiPayRoll();
		try {
			api.postNominaElectronica(url, json);
		}
		catch (IOException | com.sysman.util.SysmanException e) {
			logger.info("enviarPayRoll url: " + url);
			logger.info("enviarPayRoll Json: " + json);
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(
					ResumentotalcuneControladorEnum.MSGCERTERRORPOST
					.getValue());
			throw new SystemException(e);
		}

	}
	
	private String enviarPayRoll2(String url, String json) throws SystemException {
		ApiPayRoll api = new ApiPayRoll();
		try {
			api.postNominaElectronica(url, json);
			return("0");
		}
		catch (IOException | com.sysman.util.SysmanException e) {
			logger.info("enviarPayRoll url: " + url);
			logger.info("enviarPayRoll Json: " + json);
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(
					ResumentotalcuneControladorEnum.MSGCERTERRORPOST
					.getValue());
			return(e.getMessage());
		}

	}
	
	private String cantidadDian (String valor) {
		return String.format( "%.0f", Float.parseFloat(valor)).replace(",",".");
	}

	/**
	 * Genera el detalle del Json
	 * 
	 * @param numDocumento
	 * Documento de la persona a generar
	 * @param consTipoNom
	 * Consecutivo del tipo de n&oacute;mina, Si es diferente a 1 es
	 * n&pacute;mina de ajustes
	 * @param tipoNomina
	 * Tipo de n&oacute;mina
	 * @param par31
	 * Nit de la entidad de la tabla parametros de entrada
	 */
	public Map<String, Object> generarJsonDetallado(String numDocumento,
			int consTipoNom, String tipoNomina, String par31) {
		Date nie192 = null;

		Map<String, Object> datos = new LinkedHashMap<>();
		Map<String, Object> paramDet = new HashMap<>();
		paramDet.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
				compania);
		paramDet.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
				ano);
		paramDet.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
				mes);
		paramDet.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
				mes);
		paramDet.put(ResumentotalcuneControladorEnum.UN_NUMERO_DCTO.getValue(),
				numDocumento);
		paramDet.put(ResumentotalcuneControladorEnum.UN_CONSEC.getValue(),
				consTipoNom);
		try {
			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0006
									.getValue())
							.getUrl(),
							paramDet));

			if ((boolean) rs.getCampos()
					.get(ResumentotalcuneControladorEnum.NIE199
							.getValue())) {
				nie192 = (Date) rs.getCampos()
						.get("NIEA192");
			}

			datos.put(ResumentotalcuneControladorEnum.NIE199.getValue(),
					(boolean) rs.getCampos().get(
							ResumentotalcuneControladorEnum.NIE199
							.getValue())
					? ResumentotalcuneControladorEnum.TRUE
							.getValue()
							: ResumentotalcuneControladorEnum.FALSE
							.getValue());

			if ((boolean) rs.getCampos()
					.get(ResumentotalcuneControladorEnum.NIE199
							.getValue())) {
				datos.put("NIAE214",
						ResumentotalcuneControladorEnum.NOM_AJUSTE
						.getValue().equals(tipoNomina)
						? "1"
								: "2");
				datos.put("NIAE192", convertirFecha(nie192));
			}
			datos.put("NIE204", "");
			datos.put(ResumentotalcuneControladorEnum.NIE002.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE002
									.getValue())));

			datos.put(ResumentotalcuneControladorEnum.NIE003.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE003
									.getValue())));
			datos.put(ResumentotalcuneControladorEnum.NIE004.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE004
									.getValue())));
			datos.put(ResumentotalcuneControladorEnum.NIE005.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE005
									.getValue())));
			datos.put("NIE006", getRsRow(rs, "NIE006"));
			datos.put(ResumentotalcuneControladorEnum.NIE008.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE008
									.getValue())));
			datos.put("NIE009", getRsRow(rs, "NIE009"));
			datos.put(ResumentotalcuneControladorEnum.NIE010.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE010
							.getValue()));
			datos.put("NIE011", getRsRow(rs, "NIE011"));
			datos.put(ResumentotalcuneControladorEnum.NIE012.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE012
							.getValue()));
			datos.put("NIE013", "CO");
			datos.put(ResumentotalcuneControladorEnum.NIE014.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE014
							.getValue()));
			datos.put(ResumentotalcuneControladorEnum.NIE015.getValue(),
					new StringBuilder(
							getRsRow(rs, ResumentotalcuneControladorEnum.NIE014
									.getValue()))
					.append(getRsRow(
							rs,
							ResumentotalcuneControladorEnum.NIE015
							.getValue()))
					.toString());
			datos.put("NIE016", "es");
			datos.put("NIE017", par31); // Nit Stefanini
			datos.put("NIE018", String.valueOf(ejbSysmanUtil
					.generarDigitoDeVerificacion(
							par31)));
			datos.put("NIE029", SysmanFunciones
					.nvl(getRsRow(rs, "NIE029"), "5"));
			datos.put("NIE030", "COP");
			// Se deja vacio viene del campo NIE031 pero este nunca se
			// llena
			datos.put("Notas", new ArrayList<Map<String, Object>>());
			datos.put("NIE033", par31);
			datos.put("NIE034",
					String.valueOf(ejbSysmanUtil
							.generarDigitoDeVerificacion(
									par31)));
			datos.put(ResumentotalcuneControladorEnum.NIE041.getValue(),
					SysmanFunciones
					.nvl(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE041
							.getValue()),
							"01"));
			datos.put(ResumentotalcuneControladorEnum.NIE042.getValue(),
					SysmanFunciones
					.nvl(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE042
							.getValue()),
							"00"));
			datos.put(ResumentotalcuneControladorEnum.NIE043.getValue(),
					(boolean) rs.getCampos().get(
							ResumentotalcuneControladorEnum.NIE043
							.getValue())
					? ResumentotalcuneControladorEnum.TRUE
							.getValue()
							: ResumentotalcuneControladorEnum.FALSE
							.getValue());
			datos.put(ResumentotalcuneControladorEnum.NIE044.getValue(),
					SysmanFunciones
					.nvl(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE044
							.getValue()),
							"13"));
			datos.put("NIE045", SysmanFunciones
					.nvl(getRsRow(rs, "NIE045"), ""));
			datos.put(ResumentotalcuneControladorEnum.NIE046.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE046
							.getValue()));
			datos.put(ResumentotalcuneControladorEnum.NIE047.getValue(),
					SysmanFunciones
					.nvl(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE047
							.getValue()),
							" ")); //mod JM CC 2482
			datos.put(ResumentotalcuneControladorEnum.NIE048.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE048
							.getValue()));
			datos.put(ResumentotalcuneControladorEnum.NIE049.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE049
							.getValue())
					.equals(" ")
					? ""
							: getRsRow(rs, ResumentotalcuneControladorEnum.NIE049
									.getValue()));
			datos.put("NIE050", "CO");
			datos.put(ResumentotalcuneControladorEnum.NIE051.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE051
							.getValue()));
			datos.put(ResumentotalcuneControladorEnum.NIE052.getValue(),
					new StringBuilder(
							getRsRow(rs, ResumentotalcuneControladorEnum.NIE051
									.getValue())).append(
											getRsRow(rs, ResumentotalcuneControladorEnum.NIE052
													.getValue())));
			datos.put(ResumentotalcuneControladorEnum.NIE053.getValue(),
					SysmanFunciones
					.nvl(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE053
							.getValue()),
							""));
			datos.put(ResumentotalcuneControladorEnum.NIE056.getValue(),
					(boolean) rs.getCampos().get(
							ResumentotalcuneControladorEnum.NIE056
							.getValue())
					? ResumentotalcuneControladorEnum.TRUE
							.getValue()
							: ResumentotalcuneControladorEnum.FALSE
							.getValue());
			datos.put(ResumentotalcuneControladorEnum.NIE061.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE061
							.getValue()));
			//datos.put("NIE062",
			//		new StringBuilder(getRsRow(rs, "NIE062"))
			//		.append(".00")
			//		.toString());
			
			datos.put("NIE062",
					new StringBuilder(String.format( "%.2f", Float.parseFloat(getRsRow(rs, "NIE062"))).replace(",","."))
					.toString()
					);
			datos.put(ResumentotalcuneControladorEnum.NIE063.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE063
							.getValue()));
			datos.put(ResumentotalcuneControladorEnum.NIE064.getValue(),
					SysmanFunciones
					.nvl(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE064
							.getValue()),
							"1"));
			datos.put(ResumentotalcuneControladorEnum.NIE065.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE065
							.getValue()));

			if ("42".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE065
					.getValue()))) {
				datos.put(ResumentotalcuneControladorEnum.NIE066.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE066
								.getValue()));
				datos.put(ResumentotalcuneControladorEnum.NIE067.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE067
								.getValue()));
				datos.put(ResumentotalcuneControladorEnum.NIE068.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE068
								.getValue()));
			}
			List<Map<String, String>> fpago = new ArrayList<>();
			Map<String, String> mfpago = new HashMap<>();
			mfpago.put(ResumentotalcuneControladorEnum.NIE203.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE203
									.getValue())));
			fpago.add(mfpago);
			datos.put("FechaPago", fpago);

			datos.put("NIE069", getRsRow(rs, "NIE069"));
			datos.put("NIE070", getRsRow(rs, "NIE070"));

			List<Map<String, String>> viaticos = new ArrayList<>();
			Map<String, String> mviaticos = new HashMap<>();
			
			if (!"0".equals(getRsRow(rs, "NIE071"))) {
					mviaticos.put("NIE071",
							new StringBuilder(getRsRow(rs, "NIE071"))
							.append(".00")
							.toString());
			}
			
			if (!"0".equals(getRsRow(rs,ResumentotalcuneControladorEnum.NIE072.getValue()))) {
					mviaticos.put(ResumentotalcuneControladorEnum.NIE072.getValue(),
							new StringBuilder(getRsRow(rs,
									ResumentotalcuneControladorEnum.NIE072
									.getValue())).append(
									".00")
							.toString());
			}
			
			if (!"0".equals(getRsRow(rs, "NIE073"))) {
					mviaticos.put("NIE073",
							new StringBuilder(getRsRow(rs, "NIE073"))
							.append(".00")
							.toString());
			}
			viaticos.add(mviaticos);
			datos.put("Transporte", viaticos);

			List<Map<String, String>> hed = new ArrayList<>();
			Map<String, String> mhed = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE078
					.getValue()))) {
				mhed.put("NIE074", "");
				mhed.put("NIE075", "");
				mhed.put("NIE076", cantidadDian(getRsRow(rs, "NIE076")));
				mhed.put("NIE077", "0".equals(
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE078
								.getValue())) ? "0.00"
										: "25.00");
				mhed.put(ResumentotalcuneControladorEnum.NIE078.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE078
								.getValue()));
				hed.add(mhed);
			}
			datos.put("HED", hed);

			List<Map<String, String>> hen = new ArrayList<>();
			Map<String, String> mhen = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE083
					.getValue()))) {
				mhen.put("NIE079", "");
				mhen.put("NIE080", "");
				mhen.put("NIE081", cantidadDian(getRsRow(rs, "NIE081")));
				mhen.put("NIE082", "0".equals(
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE083
								.getValue())) ? "0.00"
										: "75.00");
				mhen.put(ResumentotalcuneControladorEnum.NIE083.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE083
								.getValue()));
				hen.add(mhen);
			}
			datos.put("HEN", hen);

			List<Map<String, String>> hrn = new ArrayList<>();
			Map<String, String> mhrn = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE088
					.getValue()))) {
				mhrn.put("NIE084", "");
				mhrn.put("NIE085", "");			
				mhrn.put("NIE086", cantidadDian(getRsRow(rs, "NIE086")));
				mhrn.put("NIE087", "0".equals(
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE088
								.getValue())) ? "0.00"
										: "35.00");
				mhrn.put(ResumentotalcuneControladorEnum.NIE088.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE088
								.getValue()));
				hrn.add(mhrn);

			}
			datos.put("HRN", hrn);

			List<Map<String, String>> heddf = new ArrayList<>();
			Map<String, String> mheddf = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE093
					.getValue()))) {
				mheddf.put("NIE089", "");
				mheddf.put("NIE090", "");
				mheddf.put("NIE091", cantidadDian(getRsRow(rs, "NIE091")));
				mheddf.put("NIE092",
						"0".equals(getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE093
								.getValue()))
						? "0.00"
								: "100.00");
				mheddf.put(ResumentotalcuneControladorEnum.NIE093.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE093
								.getValue()));
				heddf.add(mheddf);
			}
			datos.put("HEDDF", heddf);

			List<Map<String, String>> hrddf = new ArrayList<>();
			Map<String, String> mhrddf = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE098
					.getValue()))) {
				/* Se comentan las dos lineas de codigo porque no es necesario el envio a la DIAN MPEREZ - 3532
				mhrddf.put("NIE094", par31);
				mhrddf.put("NIE095", par31);*/
				mhrddf.put("NIE096", cantidadDian(getRsRow(rs, "NIE096")));
				mhrddf.put("NIE097", "0".equals(getRsRow(rs,
						ResumentotalcuneControladorEnum.NIE098
						.getValue())) ? "0.00"
								: "75.00");
				mhrddf.put(ResumentotalcuneControladorEnum.NIE098.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE098
								.getValue()));
				hrddf.add(mhrddf);
			}
			datos.put("HRDDF", hrddf);

			List<Map<String, String>> hendf = new ArrayList<>();
			Map<String, String> mhendf = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE103
					.getValue()))) {
				mhendf.put("NIE099", "");
				mhendf.put("NIE100", "");
				mhendf.put("NIE101", cantidadDian(getRsRow(rs, "NIE101")));
				mhendf.put("NIE102", "0".equals(getRsRow(rs,
						ResumentotalcuneControladorEnum.NIE103
						.getValue())) ? "0.00"
								: "150.00");
				mhendf.put(ResumentotalcuneControladorEnum.NIE103.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE103
								.getValue()));
				hendf.add(mhendf);
			}
			datos.put("HENDF", hendf);

			List<Map<String, String>> hrndf = new ArrayList<>();
			Map<String, String> mhrndf = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE108
					.getValue()))) {
				mhrndf.put("NIE104", ""); // mod JM CC 4541 no tenemos esa info asi que enviaremos vacio
				mhrndf.put("NIE105", ""); // mod JM CC 4541 no tenemos esa info asi que enviaremos vacio
				mhrndf.put("NIE106", cantidadDian(getRsRow(rs, "NIE106")));
				mhrndf.put("NIE107", "0".equals(getRsRow(rs,
						ResumentotalcuneControladorEnum.NIE108
						.getValue())) ? "0.00"
								: "110.00");
				mhrndf.put(ResumentotalcuneControladorEnum.NIE108.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE108
								.getValue()));
				hrndf.add(mhrndf);
			}

			datos.put("HRNDF", hrndf);

			List<Map<String, String>> vacacionesComun = new ArrayList<>();
			Map<String, String> mvacacionesComun = new HashMap<>();
			mvacacionesComun.put(
					ResumentotalcuneControladorEnum.NIE109.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE109
									.getValue())));
			mvacacionesComun.put(
					ResumentotalcuneControladorEnum.NIE110.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE110
									.getValue())));
			mvacacionesComun.put("NIE111", cantidadDian(getRsRow(rs, "NIE111")));
			mvacacionesComun.put("NIE112",
					getRsRow(rs, "NIE112"));
			vacacionesComun.add(mvacacionesComun);
			datos.put("VacacionesComunes", vacacionesComun);

			List<Map<String, String>> vacacionesCompen = new ArrayList<>();
			Map<String, String> mvacacionesCompen = new HashMap<>();
			mvacacionesCompen.put("NIE115",
    				String.format( "%.0f", Float.parseFloat(getRsRow(rs, "NIE115"))).replace(",",".") );

			mvacacionesCompen.put("NIE116",
					getRsRow(rs, "NIE116"));
			vacacionesCompen.add(mvacacionesCompen);
			datos.put("VacacionesCompensadas", vacacionesCompen);

			List<Map<String, String>> primas = new ArrayList<>();
			Map<String, String> mprimas = new HashMap<>();
			mprimas.put("NIE117", getRsRow(rs, "NIE117"));
			mprimas.put("NIE118", getRsRow(rs, "NIE118"));
			mprimas.put("NIE119", getRsRow(rs, "NIE119"));
			primas.add(mprimas);
			datos.put("Primas", primas);

			List<Map<String, String>> cesantias = new ArrayList<>();
			Map<String, String> mcesantias = new HashMap<>();
			mcesantias.put("NIE120", getRsRow(rs, "NIE120"));
			mcesantias.put("NIE121",
					!"0".equals(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE122
							.getValue()))
					? "12.00"
							: "0.00");
			mcesantias.put(ResumentotalcuneControladorEnum.NIE122.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE122
							.getValue()));
			cesantias.add(mcesantias);
			datos.put("Cesantias", cesantias);

			List<Map<String, String>> incapacidad = new ArrayList<>();
			Map<String, String> mincapacidad = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE127
					.getValue()))) {
				mincapacidad.put("NIE123", "");
				mincapacidad.put("NIE124", "");
				mincapacidad.put("NIE125",
						!"0".equals(getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE127
								.getValue()))
						? getRsRow(rs, "NIE125")
	
								: "0");
				mincapacidad.put("NIE126",
						!"0".equals(getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE127
								.getValue()))
						? getRsRow(rs, "NIE126")
	
								: "0".substring(0,
										1));
				mincapacidad.put(ResumentotalcuneControladorEnum.NIE127.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE127
								.getValue()));
				incapacidad.add(mincapacidad);
			}
			datos.put("Incapacidad", incapacidad);

			List<Map<String, String>> licenciaMp = new ArrayList<>();
			Map<String, String> mlicenciaMp = new HashMap<>();
			mlicenciaMp.put("NIE128", "");
			mlicenciaMp.put("NIE129", "");
			mlicenciaMp.put("NIE130", getRsRow(rs, "NIE130"));
			mlicenciaMp.put("NIE131", getRsRow(rs, "NIE131"));
			licenciaMp.add(mlicenciaMp);
			datos.put("LicenciaMP", licenciaMp);

			List<Map<String, String>> licenciaR = new ArrayList<>();
			Map<String, String> mlicenciaR = new HashMap<>();
			mlicenciaR.put("NIE132", "");
			mlicenciaR.put("NIE133", "");
			mlicenciaR.put("NIE134", getRsRow(rs, "NIE134"));
			mlicenciaR.put("NIE135", getRsRow(rs, "NIE135"));
			licenciaR.add(mlicenciaR);
			datos.put("LicenciaR", licenciaR);

			List<Map<String, String>> licenciaNr = new ArrayList<>();
			Map<String, String> mlicenciaNr = new HashMap<>();
			mlicenciaNr.put(ResumentotalcuneControladorEnum.NIE136.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE136
									.getValue())));
			mlicenciaNr.put(ResumentotalcuneControladorEnum.NIE137.getValue(),
					convertirFecha(
							(Date) rs.getCampos().get(
									ResumentotalcuneControladorEnum.NIE137
									.getValue())));
			mlicenciaNr.put("NIE138", getRsRow(rs, "NIE138"));
			licenciaNr.add(mlicenciaNr);
			datos.put("LicenciaNR", licenciaNr);

			List<Map<String, String>> bonificacion = new ArrayList<>();
			Map<String, String> mbonificacion = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE139
					.getValue()))) {
				mbonificacion.put(
						ResumentotalcuneControladorEnum.NIE139
						.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE139
								.getValue()));
				
				if (!"0".equals(getRsRow(rs, "NIE140"))) {
					mbonificacion.put("NIE140",
						getRsRow(rs, "NIE140"));
				
				}
				bonificacion.add(mbonificacion);
			}
			datos.put("Bonificacion", bonificacion);

			List<Map<String, String>> auxilio = new ArrayList<>();
			Map<String, String> mauxilio = new HashMap<>();
			
			if ( !"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE141.getValue()))) {
					mauxilio.put(ResumentotalcuneControladorEnum.NIE141.getValue(),
							getRsRow(rs, ResumentotalcuneControladorEnum.NIE141
							.getValue()));
			}
			
			if ( !"0".equals(getRsRow(rs,"NIE142"))) {
					mauxilio.put("NIE142", getRsRow(rs, "NIE142"));
			}		
			auxilio.add(mauxilio);
						
			datos.put("Auxilio", auxilio);

			List<Map<String, String>> huelgaLegal = new ArrayList<>();
			Map<String, String> mhuelgaLegal = new HashMap<>();
			mhuelgaLegal.put("NIE143", "");
			mhuelgaLegal.put("NIE144", "");
			mhuelgaLegal.put("NIE145", getRsRow(rs, "NIE145"));
			huelgaLegal.add(mhuelgaLegal);
			datos.put("HuelgaLegal", huelgaLegal);

			List<Map<String, String>> otroConcepto = new ArrayList<>();
			Map<String, String> motroConcepto = new HashMap<>();
			if (!"0".equals(getRsRow(rs, ResumentotalcuneControladorEnum.NIE148
					.getValue()))) {
				motroConcepto.put(
						ResumentotalcuneControladorEnum.NIE146
						.getValue(),
						SysmanFunciones.nvlStr(
								getRsRow(rs, ResumentotalcuneControladorEnum.NIE146
										.getValue()),
								"OTROS CONCEPTOS."));
				motroConcepto.put("NIE147",
						getRsRow(rs, "NIE147"));
				motroConcepto.put(
						ResumentotalcuneControladorEnum.NIE148
						.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE148
								.getValue()));
				otroConcepto.add(motroConcepto);
			}
			datos.put("OtroConcepto", otroConcepto);

			List<Map<String, String>> compensacion = new ArrayList<>();
			Map<String, String> mcompensacion = new HashMap<>();
			mcompensacion.put("NIE149",
					getRsRow(rs, "NIE149"));
			mcompensacion.put("NIE150",
					getRsRow(rs, "NIE150"));
			compensacion.add(mcompensacion);
			datos.put("Compensacion", compensacion);

			List<Map<String, String>> bonoEpcTv = new ArrayList<>();
			Map<String, String> mbonoEpcTv = new HashMap<>();
			if (!"0".equals(
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE152
							.getValue()))) {
				mbonoEpcTv.put("NIE151",
						getRsRow(rs, "NIE151"));
				mbonoEpcTv.put(ResumentotalcuneControladorEnum.NIE152
						.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE152
								.getValue()));
				mbonoEpcTv.put("NIE153",
						getRsRow(rs, "NIE153"));
				mbonoEpcTv.put("NIE154",
						getRsRow(rs, "NIE154"));
				bonoEpcTv.add(mbonoEpcTv);
			}
			datos.put("BonoEPCTV", bonoEpcTv);

			List<Map<String, String>> comision = new ArrayList<>();
			Map<String, String> mcomision = new HashMap<>();
			if (!"0".equals(
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE155
							.getValue()))) {
				mcomision.put(ResumentotalcuneControladorEnum.NIE155.getValue(),
						getRsRow(rs, ResumentotalcuneControladorEnum.NIE155
								.getValue()));
				comision.add(mcomision);
			}
			datos.put("Comision", comision);

			List<Map<String, String>> devengosPorTerc = new ArrayList<>();
			Map<String, String> mdevengosPorTerc = new HashMap<>();
			if (!"0".equals(
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE193
							.getValue()))) {
				mdevengosPorTerc.put(
						ResumentotalcuneControladorEnum.NIE193
						.getValue(),
						getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE193
								.getValue()));
				devengosPorTerc.add(mdevengosPorTerc);
			}
			datos.put("Devengados-PagoTercero", devengosPorTerc);

			List<Map<String, String>> devengosAnti = new ArrayList<>();
			if (!"0".equals(getRsRow(rs,
					ResumentotalcuneControladorEnum.NIE194
					.getValue()))) {
				Map<String, String> mdevengosAnti = new HashMap<>();
				mdevengosAnti.put(
						ResumentotalcuneControladorEnum.NIE194
						.getValue(),
						getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE194
								.getValue()));
				devengosAnti.add(mdevengosAnti);
			}
			datos.put("Devengados-Anticipo", devengosAnti);

			datos.put("NIE156", getRsRow(rs, "NIE156"));
			datos.put("NIE157", getRsRow(rs, "NIE157"));
			datos.put("NIE158", getRsRow(rs, "NIE158"));
			datos.put("NIE159", getRsRow(rs, "NIE159"));
			datos.put("NIE160", getRsRow(rs, "NIE160"));
			datos.put("NIE201", getRsRow(rs, "NIE201"));
			datos.put(ResumentotalcuneControladorEnum.NIE161.getValue(), String.format( "%.2f", Float.parseFloat(getRsRow(rs, ResumentotalcuneControladorEnum.NIE161.getValue()))).replace(",",".") );
	        datos.put(ResumentotalcuneControladorEnum.NIE163.getValue(),
	                            getRsRow(rs, ResumentotalcuneControladorEnum.NIE163
	                                            .getValue()));
	        datos.put(ResumentotalcuneControladorEnum.NIE164.getValue(), String.format( "%.2f", Float.parseFloat(getRsRow(rs, ResumentotalcuneControladorEnum.NIE164.getValue()))).replace(",",".") );
	
			datos.put(ResumentotalcuneControladorEnum.NIE166.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE166
							.getValue()));
			datos.put("NIE167",
					"0".equals(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE168
							.getValue()))
					? "0.00"
							: new StringBuilder(getRsRow(rs, "NIE167").replace(".0", "")).append(".00").toString());
			datos.put(ResumentotalcuneControladorEnum.NIE168.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE168
							.getValue()));
			datos.put("NIE169",
					"0".equals(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE170
							.getValue()))
					? "0.00"
							: new StringBuilder(getRsRow(rs, "NIE169").replace(".0", "")).append(".00").toString());

			datos.put(ResumentotalcuneControladorEnum.NIE170.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE170
							.getValue()));

			List<Map<String, String>> sindicato = new ArrayList<>();
			Map<String, String> msindicato = new HashMap<>();
			String condNie171 = new StringBuilder(SysmanFunciones
					.nvlStr("0".equals(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE171
							.getValue()))
							? "1"
									: getRsRow(rs,
											ResumentotalcuneControladorEnum.NIE171
											.getValue()),
									"1")).append(".00").toString();
			msindicato.put(ResumentotalcuneControladorEnum.NIE171.getValue(),
					"0".equals(getRsRow(rs,
							ResumentotalcuneControladorEnum.NIE172
							.getValue()))
					? "0.00"
							: condNie171);
			msindicato.put(ResumentotalcuneControladorEnum.NIE172.getValue(),
					getRsRow(rs, ResumentotalcuneControladorEnum.NIE172
							.getValue()));
			sindicato.add(msindicato);
			datos.put("Sindicato", sindicato);

			List<Map<String, String>> sancion = new ArrayList<>();
			Map<String, String> msancion = new HashMap<>();
			msancion.put("NIE173", getRsRow(rs, "NIE173"));
			msancion.put("NIE174", getRsRow(rs, "NIE174"));
			sancion.add(msancion);
			datos.put("Sancion", sancion);

			List<Object> libranza = new ArrayList<>();
			List<Registro> lRs2 = getNie176(numDocumento);
			for (Registro rs2 : lRs2) {
				Map<String, String> mlibranza = new LinkedHashMap<>();
				mlibranza.put("NIE175", rs2.getCampos().get("NOMBRE_CONCEPTO")
						.toString());
				mlibranza.put("NIE176",
						rs2.getCampos().get("TOTALVALOR").toString());
				libranza.add(mlibranza);
			}
			datos.put("Libranza", libranza);

			List<Map<String, String>> deduccionPagoTer = new ArrayList<>();
			if (!"0".equals(getRsRow(rs,
					ResumentotalcuneControladorEnum.NIE195
					.getValue()))) {
				Map<String, String> mdeduccionPagoTer = new HashMap<>();
				mdeduccionPagoTer.put(
						ResumentotalcuneControladorEnum.NIE195
						.getValue(),
						getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE195
								.getValue()));
				deduccionPagoTer.add(mdeduccionPagoTer);
			}
			datos.put("Deducciones-PagoTercero", deduccionPagoTer);

			List<Map<String, String>> deduccionAnticipo = new ArrayList<>();
			if (!"0".equals(getRsRow(rs,
					ResumentotalcuneControladorEnum.NIE196
					.getValue()))) {
				Map<String, String> mdeduccionAnticipo = new HashMap<>();
				mdeduccionAnticipo.put(
						ResumentotalcuneControladorEnum.NIE196
						.getValue(),
						getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE196
								.getValue()));
				deduccionAnticipo.add(mdeduccionAnticipo);
			}
			datos.put("Deducciones-Anticipo", deduccionAnticipo);

			List<Map<String, String>> otraDeduccion = new ArrayList<>();
			if (!"0".equals(getRsRow(rs,
					ResumentotalcuneControladorEnum.NIE197
					.getValue()))) {
				Map<String, String> motraDeduccion = new HashMap<>();
				motraDeduccion.put(
						ResumentotalcuneControladorEnum.NIE197
						.getValue(),
						getRsRow(rs,
								ResumentotalcuneControladorEnum.NIE197
								.getValue()));
				otraDeduccion.add(motraDeduccion);
			}
			datos.put("OtraDeduccion", otraDeduccion);

			datos.put("NIE198", getRsRow(rs, "NIE198"));
			datos.put("NIE177", getRsRow(rs, "NIE177"));
			datos.put("NIE179", getRsRow(rs, "NIE179"));
			datos.put("NIE180", getRsRow(rs, "NIE180"));
			datos.put("NIE181", getRsRow(rs, "NIE181"));
			datos.put("NIE182", getRsRow(rs, "NIE182"));
			datos.put("NIE183", getRsRow(rs, "NIE183"));
			datos.put("NIE184", getRsRow(rs, "NIE184"));
			datos.put("NIE185", getRsRow(rs, "NIE185"));
			datos.put("NIE186", getRsRow(rs, "NIE186"));
			datos.put("NIE187", new StringBuilder(getRsRow(rs, "NIE187"))
													.append(".00")
													.toString());
			datos.put("NIE188", new StringBuilder(getRsRow(rs, "NIE188"))
													.append(".00")
													.toString());
			datos.put("NIE189", new StringBuilder(getRsRow(rs, "NIE189"))
													.append(".00")
													.toString());

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return datos;

	}

	private String getNitPar31() {
		String rta = "";
		Map<String, Object> param31 = new TreeMap<>();

		param31.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
				compania);

		try {
			Registro rsPar31 = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0007
									.getValue())
							.getUrl(),
							param31));
			if (rsPar31 != null) {
				rta = rsPar31.getCampos()
						.get(GeneralParameterEnum.NIT.getName())
						.toString();
			}

		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return rta;
	}

	/**
	 * Convierte la fecha a String
	 * 
	 * @param fecha
	 * fecha a convertir
	 * @return
	 */
	private String convertirFecha(Date fecha) {
		String rta = "";
		if (fecha != null) {
			try {
				rta = SysmanFunciones.convertirAFechaCadena(fecha,
						"yyyy-MM-dd");
			}
			catch (ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		return rta;
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtPdfResumen en la vista
	 * 
	 */
	public void oprimirBtPdfResumen() {
		archivoDescarga = null;
		generarNombreReporte(FORMATOS.PDF);
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtExcelResumen en la vista
	 *
	 * 
	 */
	public void oprimirBtExcelResumen() {
		archivoDescarga = null;
		generarNombreReporte(FORMATOS.EXCEL97);
	}

	/**
	 * Metodo llamado en el oprimir Presentar y Excel
	 */
	public void generarNombreReporte(ReportesBean.FORMATOS formato) {
		try {
			cargarListaPeriodo1();
			String nombreReporte = SysmanFunciones.nvlStr(consultarParametro(
					"FORMATO RESUMEN TOTAL NOMINA", false),
					"000560ResumenTotal");

			obtenerReporte(formato, nombreReporte);
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	public void obtenerReporte(FORMATOS formatos, String reporte) {
		String encabezado;
		String entre;
		String headerEspecial;
		boolean validarTitulo = false;
		try {
			archivoDescarga = null;
			String fechaInicial = fechInicial();
			String fechaFinal = fechFinal();

			HashMap<String, Object> reemplazar = new HashMap<>();

			reemplazar.put("fechaInicial", fechaInicial);
			reemplazar.put("fechaFinal", fechaFinal);
			reemplazar.put(ResumentotalcuneControladorEnum.PERIODO1.getValue(),
					periodo);
			reemplazar.put(ResumentotalcuneControladorEnum.PERIODO1.getValue(),
					periodo);
			reemplazar.put(ResumentotalcuneControladorEnum.ANO1.getValue(),
					ano);
			reemplazar.put(ResumentotalcuneControladorEnum.ANO1.getValue(),
					ano);
			reemplazar.put(ResumentotalcuneControladorEnum.MES1.getValue(),
					mes);
			reemplazar.put(ResumentotalcuneControladorEnum.MES1.getValue(),
					mes);
			reemplazar.put("proceso", proceso);

			Map<String, Object> parametros = new HashMap<>();
			entre = SysmanFunciones.concatenar("Entre: ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes)],
					" de ", ano, " Periodo ", periodo(periodo), " y ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes)],
					" de ", ano, " Periodo ", periodo(periodo));
			encabezado = SysmanFunciones.concatenar("Entre: ", "Perï¿½odo ",
					SysmanFunciones.initCap(
							service.buscarEnLista(periodo,
									GeneralParameterEnum.PERIODO
									.getName(),
									"NOM_PERIODO",
									listaPeriodo1)),
					" de ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes)],
					" de ", ano, " y Perï¿½odo ",
					SysmanFunciones.initCap(
							service.buscarEnLista(periodo,
									GeneralParameterEnum.PERIODO
									.getName(),
									"NOM_PERIODO",
									listaPeriodo1)),
					" de ",
					SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
					                                           .parseInt(mes)],
					" de ", ano);

			String nombreGerente = consultarParametro("NOMBRE DEL GERENTE",
					false);

			String cargoGerente = consultarParametro("CARGO DEL GERENTE",
					false);
			String nomCargoTesoreroPaga = consultarParametro(
					"NOMBRE DEL CARGO TESORERO PAGADOR", false);
			String cargoJefeNomina = consultarParametro(
					"CARGO DEL JEFE DE NOMINA", false);
			String cargoTesoreroPagador = consultarParametro(
					"CARGO DEL TESORERO PAGADOR", false);
			String nombreJefeRecursosHumanos = consultarParametro(
					"NOMBRE DEL JEFE DE RECURSOS HUMANOS", false);
			String nomQuienFirmaSolDisPresupuestal = consultarParametro(
					"NOMBRE DE QUIEN FIRMA SOLICITUD DISPONIBILIDAD PRESUPUESTAL",
					false);
			String cargoQuienLiquidaNomina = consultarParametro(
					"CARGO DE QUIEN LIQUIDA NOMINA", false);
			String nomQuienLiquidaNomina = consultarParametro(
					"NOMBRE DE QUIEN LIQUIDA NOMINA", false);
			String cargoQuienFirmaSolDispoPresu = consultarParametro(
					"CARGO DE QUIEN FIRMA SOLICITUD DISPONIBILIDAD PRESUPUESTAL",
					false);
			String nombreJefeTesoreroPresupuesto = consultarParametro(
					"NOMBRE DE JEFE DE PRESUPUESTO", false);

			// inicio dcastiblanco
			String nomQuienAutorizaNomina = consultarParametro(
					"NOMBRE DE QUIEN AUTORIZA NOMINA", false);
			String cargoQuienAutorizaNomina = consultarParametro(
					"CARGO DE QUIEN AUTORIZA NOMINA", false);
			String nomQuienRevisaNomina = consultarParametro(
					"NOMBRE DE QUIEN REVISA NOMINA", false);
			String cargoQuienRevisaNomina = consultarParametro(
					"CARGO DE QUIEN REVISA NOMINA", false);
			String elaboradoPor = consultarParametro(
					"ELABORADO POR", false);
			String nombreJefeRecursos = consultarParametro(
					"NOMBRE JEFE RECURSOS HUMANOS", false);
			String cargoJefeRecursos = consultarParametro(
					"CARGO JEFE RECURSOS HUMANOS", false);
			String elaboro = consultarParametro(
					"ELABORO RESUMEN TOTAL NOMINA", false);
			String jefeDesarrolloHumano = consultarParametro(
					"NOMBRE JEFE DESARROLLO HUMANO", false);
			String cargoJefeDesarrolloHumano = consultarParametro(
					"CARGO JEFE DESARROLLO HUMANO", false);
			String jefeNomina = consultarParametro(
					"NOMBRE JEFE NOMINA", false);
			String cargoResponsableNomina = consultarParametro(
					"CARGO RESPONSABLE DE NOMINA", false);
			// fin dcastiblanco

			headerEspecial = consultarParametro(
					"FORMATOS ESPECIALES BUCARAMANGA",
					true);
			String validarFirmas = consultarParametro("MOSTRAR FIRMAS FND",
					true);

			String cargoJP = ejbSysmanUtil.consultarParametro(compania,
					"CARGO DEL JEFE DE PRESUPUESTO",
					SessionUtil.getModulo(),
					new Date(), false);

			String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

			if ("890201222".equals(SessionUtil.getCompaniaIngreso().getNit())) {
				validarTitulo = "002".equals(
						SessionUtil.getCompaniaIngreso().getCodigo());
			}

			parametros.put("PR_NOMBREEMPRESA",
					SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_ELABORO_RESUMEN_TOTAL_NOMINA", elaboro);
			parametros.put("PR_ENTRE", entre);
			parametros.put("PR_ENCABEZADO", encabezado);
			parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO",
					cargoJP);
			parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_SOLICITUD_DISPONIBILIDAD_PRESUPUESTAL",
					nomQuienFirmaSolDisPresupuestal);
			parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",
					cargoQuienLiquidaNomina);
			parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",
					nomQuienLiquidaNomina);
			parametros.put("PR_CARGO_DE_QUIEN_FIRMA_SOLICITUD_DISPONIBILIDAD_PRESUPUESTAL",
					cargoQuienFirmaSolDispoPresu);
			parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS",
					nombreJefeRecursosHumanos);
			parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
					cargoTesoreroPagador);
			parametros.put("PR_ELABORADO_POR",
					elaboradoPor);
			parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
			parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
					nomCargoTesoreroPaga);
			parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
			parametros.put("PR_CARGO_DEL_JEFE_DE_NOMINA", cargoJefeNomina);
			parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
					nombreJefeTesoreroPresupuesto);
			parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
					nomQuienAutorizaNomina);
			parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
					cargoQuienAutorizaNomina);
			parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
					nomQuienRevisaNomina);
			parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
					cargoQuienRevisaNomina);
			parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
					nombreJefeRecursos);
			parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
					cargoJefeRecursos);

			parametros.put("PR_VALIDAR_FIRMAS",
					validarFirmas.equals("SI"));

			parametros.put("PR_HEADER_ESPECIAL",
					headerEspecial.equals("SI"));
			parametros.put("PR_IMAGEN_ESPECIAL", sticker);
			parametros.put("PR_VALIDAR_TITULO", validarTitulo);

			parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO",
					jefeDesarrolloHumano);
			parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO",
					cargoJefeDesarrolloHumano);
			parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
			parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA",
					cargoResponsableNomina);

			// 	IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muï¿½oz)
			parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
			// FIN IMPLEMENTACION MARCA_BLANCA

			generarInformeNomina(reemplazar, parametros, formatos, reporte);
		}
		catch (SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.error(e.getMessage(), e);

		}

	}

	public void generarInformeNomina(Map<String, Object> reemplazar,
			Map<String, Object> parametros,
			ReportesBean.FORMATOS formato, String reporte) {

		try {
			Reporteador.resuelveConsulta(reporte,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);
		}
		catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * Metodo llamado en obtenerReporte
	 *
	 * @return
	 */
	private String fechInicial() {
		String fecIni;
		fecIni = SysmanFunciones.concatenar(
				proceso.length() == 1 ? "0" + proceso : proceso, ano,
						mes.length() == 1 ? "0" + mes : mes,
								periodo.length() == 1 ? "0" + periodo : periodo);
		return fecIni;
	}

	/**
	 * Metodo llamado en obtenerReporte
	 *
	 * @return
	 */
	private String fechFinal() {
		String fecFinal;
		fecFinal = SysmanFunciones.concatenar(
				proceso.length() == 1 ? "0" + proceso : proceso, ano,
						mes.length() == 1 ? "0" + mes : mes,
								periodo.length() == 1 ? "0" + periodo : periodo);
		return fecFinal;

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtGenConceptos en la vista
	 *
	 */
	public void oprimirBtGenConceptos() {
		archivoDescarga = null;
		HashMap<String, Object> reemplazarGenCn = new HashMap<>();
		reemplazarGenCn.put(ResumentotalcuneControladorEnum.PARANIO.getValue(),
				ano);
		reemplazarGenCn.put(ResumentotalcuneControladorEnum.PARMES.getValue(),
				mes);

		Map<String, Object> parametrosGenCn = new HashMap<>();
		parametrosGenCn.put("PR_NOMBRECOMPANIA",
				SessionUtil.getCompaniaIngreso().getNombre());
		try {
			Reporteador.resuelveConsulta(
					ResumentotalcuneControladorEnum.INFORME2.getValue(),
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazarGenCn, parametrosGenCn);

			archivoDescarga = JsfUtil.exportarStreamed(
					ResumentotalcuneControladorEnum.INFORME2.getValue(),
					parametrosGenCn,
					ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
		}
		catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtGenKardex en la vista
	 * 
	 * 
	 */
	public void oprimirBtGenKardex() {
		archivoDescarga = null;
		int fila = 1;
		try {

			Map<String, Object> param = new TreeMap<>();
			param.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
					compania);

			param.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
					ano);

			param.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
					mes);

			List<Registro> listaDeducciones = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0001
									.getValue())
							.getUrl(),
							param));

			if (!listaDeducciones.isEmpty()) {
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook
						.createSheet("Kardex");

				HSSFRow rowDetalle;
				HSSFRow rowEncabezado = sheet.createRow(0);
				HSSFCell cell;

				for (Registro registro : listaDeducciones) {
					rowDetalle = sheet.createRow(fila);
					int columna = 0;
					// Ordeno los campos
					SortedSet<String> keys = new TreeSet<>(
							registro.getCampos().keySet());

					// Encabezados
					for (String key : keys) {
						if (fila == 1) {
							cell = rowEncabezado.createCell(columna);
							if (Arrays.asList("BANO", "ANOMBRES", "CTOTALDIAS",
									"DNUMERO_DCTO", "CMES")
									.contains(key)) {
								cell.setCellValue(
										key.substring(1, key.length()));
							}
							else {
								cell.setCellValue(key);
							}
						}

						// Detalle
						cell = rowDetalle.createCell(columna);
						if (registro.getCampos().get(key) != null) {
							if (Arrays.asList("BANO", "ANOMBRES", "CTOTALDIAS",
									"DNUMERO_DCTO", "CMES")
									.contains(key)) {
								cell.setCellValue(registro.getCampos().get(key)
										.toString());
							}
							else {
								cell.setCellValue(Double.parseDouble(
										registro.getCampos().get(key)
										.toString()));
							}
						}
						columna += 1;
					}
					fila += 1;
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				workbook.write(out);
				out.close();
				workbook.close();
				archivoDescarga = JsfUtil.getArchivoDescarga(
						new ByteArrayInputStream(out.toByteArray()),
						"Kardex.xls");
			}
			else {
				JsfUtil.agregarMensajeAlerta(idioma.getString(
						ResumentotalcuneControladorEnum.TB_TB4255
						.getValue()));
			}

		}
		catch (IOException | JRException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
			logger.info("Fila: " + fila);
		}

	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtGenCamposReq en la vista
	 * 
	 */
	public void oprimirBtGenCamposReq() {
		archivoDescarga = null;
		String salida = null;
		ByteArrayInputStream streamTexto;
		try {
			salida = ejbNominaUno.revisionDatosCune(compania,
					Integer.parseInt(ano), Integer.parseInt(mes));

			streamTexto = JsfUtil.serializarPlano(salida);

			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
					new StringBuilder("Revisiï¿½n campos requeridos")
					.append(".txt").toString());
		}
		catch (NumberFormatException | SystemException | IOException
				| JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCbEmpleadoIni
	 * 
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbEmpleadoIni(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		empleadoIni = registroAux.getCampos()
				.get(ResumentotalcuneControladorEnum.NUMDCTO.getValue())
				.toString();
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCbEmpleadoFin
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCbEmpleadoFin(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		empleadoFin = registroAux.getCampos()
				.get(ResumentotalcuneControladorEnum.NUMDCTO.getValue())
				.toString();
	}
	/**
	 * 
	 * @param event
	 */
	public void seleccionarFilaempleado(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		if(registroAux.getCampos().isEmpty()) {
			empleado = null;
		}else {
			empleado = registroAux.getCampos()
				.get(ResumentotalcuneControladorEnum.NUMDCTO.getValue())
				.toString();
		}
	}
	/**
	 * Consulta un par&aacute;metro del sistema de la
	 * aplicaci&oacute;n actual
	 * 
	 * @param nombre
	 * Nombre del ar&aacute;metro
	 * @param mayus
	 * Indicador de mayus
	 * @return Valor del par&aacute;metro
	 * @throws SystemException
	 * Excepci&oacute;n de negocio
	 */
	private String consultarParametro(String nombre, boolean mayus)
			throws SystemException {
		return ejbSysmanUtil.consultarParametro(compania, nombre,
				SessionUtil.getModulo(),
				new Date(), mayus);
	}

	public String getTrm() {
		return trm;
	}

	public void setTrm(String trm) {
		this.trm = trm;
	}

	/**
	 * Metodo llamado en obtenerReporte
	 *
	 * @param periodo
	 * @return
	 */
	private String periodo(String periodo) {
		String per;
		per = periodo.length() == 1 ? "0" + periodo : periodo;
		return per;
	}

	public void cargarListaPeriodo1() {
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.MES.getName(), mes);

			listaPeriodo1 = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									NominaresumentotalControladorUrlEnum.URL5682
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * Retorna la variable fechaReporte
	 * 
	 * @return fechaReporte
	 */
	public Date getFechaReporte() {
		return fechaReporte;
	}

	/**
	 * Asigna la variable fechaReporte
	 * 
	 * @param fechaReporte
	 * Variable a asignar en fechaReporte
	 */
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}

	/**
	 * Retorna la variable empleadoIni
	 * 
	 * @return empleadoIni
	 */
	public String getEmpleadoIni() {
		return empleadoIni;
	}

	/**
	 * Asigna la variable empleadoIni
	 * 
	 * @param empleadoIni
	 * Variable a asignar en empleadoIni
	 */
	public void setEmpleadoIni(String empleadoIni) {
		this.empleadoIni = empleadoIni;
	}

	public String getEmpleadoFin() {
		return empleadoFin;
	}

	public void setEmpleadoFin(String empleadoFin) {
		this.empleadoFin = empleadoFin;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	/**
	 * Retorna la lista listaCbEmpleadoIni
	 * 
	 * @return listaCbEmpleadoIni
	 */
	public RegistroDataModelImpl getListaCbEmpleadoIni() {
		return listaCbEmpleadoIni;
	}

	/**
	 * Asigna la lista listaCbEmpleadoIni
	 * 
	 * @param listaCbEmpleadoIni
	 * Variable a asignar en listaCbEmpleadoIni
	 */
	public void setListaCbEmpleadoIni(
			RegistroDataModelImpl listaCbEmpleadoIni) {
		this.listaCbEmpleadoIni = listaCbEmpleadoIni;
	}

	/**
	 * Retorna la lista listaCbEmpleadoFin
	 * 
	 * @return listaCbEmpleadoFin
	 */
	public RegistroDataModelImpl getListaCbEmpleadoFin() {
		return listaCbEmpleadoFin;
	}

	/**
	 * Asigna la lista listaCbEmpleadoFin
	 * 
	 * @param listaCbEmpleadoFin
	 * Variable a asignar en listaCbEmpleadoFin
	 */
	public void setListaCbEmpleadoFin(
			RegistroDataModelImpl listaCbEmpleadoFin) {
		this.listaCbEmpleadoFin = listaCbEmpleadoFin;
	}

	public String getTipoNom() {
		return tipoNom;
	}

	public void setTipoNom(String tipoNom) {
		this.tipoNom = tipoNom;
	}

	public String getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	public boolean isNomAjuste() {
		return nomAjuste;
	}

	public void setNomAjuste(boolean nomAjuste) {
		this.nomAjuste = nomAjuste;
	}

	public String getTipoNomGenJson() {
		return tipoNomGenJson;
	}

	public void setTipoNomGenJson(String tipoNomGenJson) {
		this.tipoNomGenJson = tipoNomGenJson;
	}

	public boolean isSoloJson() {
		return soloJson;
	}

	public void setSoloJson(boolean soloJson) {
		this.soloJson = soloJson;
	}

	/**
	 * Busca entre el Registro el campo y evalua los nulos
	 * 
	 * @param rs
	 * @param campo
	 * @return
	 */
	private String getRsRow(Registro rs, String campo) {
		String rta = "";
		try {
			if (rs.getCampos().containsKey(campo)) {
				rta = SysmanFunciones.nvl(
						rs.getCampos().get(campo),
						"").toString();
			}
			else {
				logger.info("No existe el campo: " + campo);
			}
		}
		catch (Exception e) {
			logger.info("Error con el campo: " + campo);
		}
		return rta;
	}

	/**
	 * Consulta por tipo de documento los historicos del campo nie 176
	 * 
	 * @param numDocumento
	 * num documento de la persona
	 * @return Listado de conceptos y valores
	 */
	private List<Registro> getNie176(String numDocumento) {
		List<Registro> rta = new ArrayList<>();
		Map<String, Object> paramNie176 = new TreeMap<>();
		paramNie176.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
				compania);

		paramNie176.put(ResumentotalcuneControladorEnum.UN_ANO.getValue(),
				ano);

		paramNie176.put(ResumentotalcuneControladorEnum.UN_MES.getValue(),
				mes);

		paramNie176.put(ResumentotalcuneControladorEnum.UN_NUMERO_DCTO
				.getValue(),
				numDocumento);

		try {
			rta = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0008
									.getValue())
							.getUrl(),
							paramNie176));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return rta;

	}
	
	/**
    *
    * Metodo ejecutado al oprimir el boton Aceptar del dialogo
    * recaudar en la vista
    *
    */
   public void aceptarNominaElectronicaUnificada()
   {
       dialogoVisible = false;
       try {				
				ejbNominaUno.actNominaCune(compania, Integer.parseInt(ano),
						Integer.parseInt(mes), tipoNom,
						Integer.parseInt(consecutivo), fechaReporte,
						new BigDecimal(trm),
						SessionUtil.getUser().getCodigo(),
						empleado);
				JsfUtil.agregarMensajeInformativo(
						idioma.getString("MSM_PROCESO_EJECUTADO"));
		}
		catch (NumberFormatException | SystemException e) 
       {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
   }
   
   /**
   *
   * Metodo ejecutado al oprimir el boton Cancelar del dialogo
   *
   */
  public void cancelarNominaElectronicaUnificada()
  {
      // <CODIGO_DESARROLLADO>
      dialogoVisible = false;
      // </CODIGO_DESARROLLADO>
  }

	/**
	 * Consulta los datos del certificado
	 * 
	 * @throws SystemException
	 * 
	 */
	private void consultarCertificado(PojoNominaElectronica datosNomina)
			throws SystemException {
		Registro rsCert = null;
		Map<String, Object> parCert = new TreeMap<>();
		parCert.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
				compania);

		try {
			rsCert = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0009
									.getValue())
							.getUrl(),
							parCert));

			if (rsCert == null) {
				JsfUtil.agregarMensajeInformativo(
						ResumentotalcuneControladorEnum.MSGCERT
						.getValue());
			}
			else {

				datosNomina.setPruebaDeRegistro(String.valueOf("1".equals(rsCert
						.getCampos().get("TIPO_AMBIENTE").toString())));
				datosNomina.setNitEmpleador(
						SessionUtil.getCompaniaIngreso().getNit());
				datosNomina.setNitProveedor(
						SessionUtil.getCompaniaIngreso().getNit());
				datosNomina.setVersion("2");
				datosNomina.setTestId(
						rsCert.getCampos().get("TES_ID").toString());
				datosNomina.setSoftwarePin(rsCert.getCampos()
						.get("PIN_SOFTWARE").toString());
				datosNomina.setUsuarioAccion(SessionUtil.getUser().getCodigo());

				File archivo = new File(
						rsCert.getCampos().get("RUTA_CERTIFICADO")
						.toString());

				String nombreCertificado = archivo
						.getName();

				byte[] archivoBytes = Files
						.readAllBytes(archivo
								.toPath());

				String certificado = Base64.getEncoder()
						.encodeToString(archivoBytes);

				String passCertificado = Base64.getEncoder()
						.encodeToString(rsCert
								.getCampos()
								.get("CONTRA_CERTIFICADO")
								.toString()
								.getBytes());

				datosNomina.setNombreCertificado(nombreCertificado);
				datosNomina.setPassCertificado(passCertificado);
				datosNomina.setCertificadoBase64(certificado);
			}
		}
		catch (IOException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(
					ResumentotalcuneControladorEnum.MSGCERTERROR
					.getValue());
			throw new SystemException(e);
		}
	}
	

	/**
	 * Consulta los datos de la entidad
	 * 
	 * @throws SystemException
	 * 
	 */
	private void consultarDatosEntidad(DatosNomElectronica datosNomina)
			throws SystemException {
		Registro rsCert = null;
		Map<String, Object> parCert = new TreeMap<>();
		parCert.put(ResumentotalcuneControladorEnum.UN_COMPANIA.getValue(),
				compania);

		try {
			rsCert = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									ResumentotalcuneControladorUrlEnum.URL0009
									.getValue())
							.getUrl(),
							parCert));

			if (rsCert == null) {
				JsfUtil.agregarMensajeInformativo(
						ResumentotalcuneControladorEnum.MSGCERT
						.getValue());
			}
			else {

				datosNomina.setPruebaDeRegistro(String.valueOf("1".equals(rsCert
						.getCampos().get("TIPO_AMBIENTE").toString())));
				datosNomina.setNitEmpleador(
						SessionUtil.getCompaniaIngreso().getNit());
				datosNomina.setNitProveedor(
						SessionUtil.getCompaniaIngreso().getNit());
				datosNomina.setVersion("2");
				datosNomina.setTestId(
						rsCert.getCampos().get("TES_ID").toString());
				datosNomina.setSoftwarePin(rsCert.getCampos()
						.get("PIN_SOFTWARE").toString());
				datosNomina.setUsuarioAccion(SessionUtil.getUser().getCodigo());		
				
			}
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(
					ResumentotalcuneControladorEnum.MSGCERTERROR
					.getValue());
			throw new SystemException(e);
		}
	}
	
	/**
	 * Convierte la fecha a String con formato pasado como parametro
	 * 
	 * @param fecha
	 * fecha a convertir
	 * @return
	 */
	private String convertirFechaFormato(Date fecha, String formato) {
		String rta = "";
		if (fecha != null) {
			try {
				rta = SysmanFunciones.convertirAFechaCadena(fecha,
						formato);
			}
			catch (ParseException e) {
				logger.error(e.getMessage(), e);
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		return rta;
	}

	public String getEmpleado() {
		return empleado;
	}

	public void setEmpleado(String empleado) {
		this.empleado = empleado;
	}

	public RegistroDataModelImpl getListaempleado() {
		return listaempleado;
	}

	public void setListaempleado(RegistroDataModelImpl listaempleado) {
		this.listaempleado = listaempleado;
	}

	public boolean isDialogoVisible() {
		return dialogoVisible;
	}

	public void setDialogoVisible(boolean dialogoVisible) {
		this.dialogoVisible = dialogoVisible;
	}

	public boolean isSoloXML() {
		return soloXML;
	}

	public void setSoloXML(boolean soloXML) {
		this.soloXML = soloXML;
	}

	
}
