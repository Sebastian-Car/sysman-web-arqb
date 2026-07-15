/*-
 * FrmdocdiandsControlador.java
 *
 * 1.0
 * 
 * 01/11/2022
 * CPEREZ
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

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
import org.primefaces.context.RequestContext;

import com.google.gson.Gson;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ComprobantecntsControladorEnum;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmdocdiandsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.APIFrida;
import com.sysman.util.rest.ParametrosFormato;
import com.sysman.util.rest.ParametrosLegalizarFactura;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaFacturasReporte;
import com.sysman.util.rest.RespuestaFridaLegalizarNotas;
import com.sysman.util.rest.RespuestaNotasReporte;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 01/11/2022
 * @author cperez2
 */
@ManagedBean
@ViewScoped
public class  FrmdocdiandsControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;

	private String nitCompania;

	private String usuario;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaInicio;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fechaFin;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>

	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmdocdiandsControlador
	 */
	public FrmdocdiandsControlador() {
		super();
		compania = SessionUtil.getCompania();
		nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		if (nitCompania.toString().contains("-")) {
			 nitCompania = nitCompania.toString().split("-")[0];
        }
		nitCompania =  nitCompania.replace(".", "");
		usuario = SessionUtil.getUser().getCodigo();
		fechaInicio = new Date();
	
		fechaFin = new Date();
		try {
			// 2372
			numFormulario = GeneralCodigoFormaEnum.ENVIARALADIAN.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
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
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarNotas
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
// INICIO NOTAS DE AJUSTE
	public void oprimirEnviarNotas() {
		archivoDescarga = null;

		String url;
		String tipoformato;
		String codigoReporte;
		String prFormato;
		String log = "";

		log = "|---------------     ENVIO DIAN NOTAS DOC SOPORTE     ---------------|";

		try {
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {
				prFormato = ejbSysmanUtil.consultarParametro(compania, "SF FORMATO FACTURACION ELECTRONICA",
						SessionUtil.getModulo(), new Date(), false);

				eliminarTabEstadoNotas();

				String respuesta;
				APIFrida api = new APIFrida();

				respuesta = api.cargarFormatoConsultarReporte(url, nitCompania, "10", "", "181",
						SysmanFunciones.convertirAFechaCadena(SysmanFunciones.convertirAFecha("18/05/2020"),
								"yyyy/MM/dd"),
						SysmanFunciones.convertirAFechaCadena(new Date(), "yyyy/MM/dd"), "0", "0", "0", "0", "1");

				Gson gson = new Gson();
				RespuestaConsultarReporte respuestaApi = gson.fromJson(respuesta, RespuestaConsultarReporte.class);

				for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi.getCuerpo().getNotas()) {

					insertarNotas(respuestaNotasReporte);

				}
//VERIFICA EL ESTADO DE NOTAS
				List<Registro> listaEstadoNotas = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmdocdiandsControladorUrlEnum.URL3562.getValue())
												.getUrl(),
										null));

				if (!listaEstadoNotas.isEmpty()) {

					for (Registro reg : listaEstadoNotas) {

						tipoformato = "06";

						Map<String, Object> param2 = new TreeMap<>();

						param2.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));
						param2.put("PREFIJO", reg.getCampos().get(GeneralParameterEnum.CLASE.getName()));

						Registro rs = RegistroConverter
								.toRegistro(requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmdocdiandsControladorUrlEnum.URL2735.getValue())
												.getUrl(),
										param2));

						if (rs != null) {

							codigoReporte = SysmanFunciones.nvl(rs.getCampos().get("FORMATO"), "30002195").toString();

						} else {
							codigoReporte = prFormato;
						}

						Registro rsCertificado = RegistroConverter
								.toRegistro(requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmdocdiandsControladorUrlEnum.URL9457.getValue())
												.getUrl(),
										null));

						if (rsCertificado != null) {

							File archivo = new File(rsCertificado.getCampos().get("RUTA_CERTIFICADO").toString());

							String nombreCertificado = archivo.getName();

							byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

							String certificado = Base64.getEncoder().encodeToString(archivoBytes);

							String passCertificado = Base64.getEncoder().encodeToString(
									rsCertificado.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

							ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

							paramLegalizar.setTipoSalida(1);
							paramLegalizar.setTestSetId(rsCertificado.getCampos().get("TES_ID").toString());
							paramLegalizar.setCodigoReporte(codigoReporte);
							
//CC_2154 se crea Apirecurso para consultar el tipo de comprobante que se esta usando deacuerdo al check NotaAjuste 
														
							Map<String, Object> paramTipo = new TreeMap<>();
							paramTipo.put(ComprobantecntsControladorEnum.COMPROBANTE.getValue(), reg.getCampos().get("NUMFORMATO").toString());
							
							Registro regT = RegistroConverter
									.toRegistro(
											requestManager.get(
													UrlServiceUtil.getInstance()
															.getUrlServiceByUrlByEnumID(
																	FrmdocdiandsControladorUrlEnum.URL1895028.getValue())
															.getUrl(),
															paramTipo));
							
							// informacion datos nota ajuste
							Map<String, Object> param1 = new TreeMap<>();

							param1.put(ComprobantecntsControladorEnum.COMPROBANTE.getValue(), reg.getCampos().get("NUMFORMATO").toString());
							param1.put(ComprobantecntsControladorEnum.TIPOCOMPROBANTE.getValue(), regT.getCampos().get("TIPO").toString());
							
							
							Registro reg1 = RegistroConverter
									.toRegistro(
											requestManager.get(
													UrlServiceUtil.getInstance()
															.getUrlServiceByUrlByEnumID(
																	FrmdocdiandsControladorUrlEnum.URL1895025.getValue())
															.getUrl(),
													param1));

							ParametrosFormato paramFormato = new ParametrosFormato();

							paramFormato.setTipoFormato(tipoformato);

							paramFormato.setNumFormato(reg.getCampos().get("NUMFORMATO").toString());

							paramFormato.setPrefijo(reg.getCampos().get(GeneralParameterEnum.CLASE.getName()).toString());

							paramFormato.setNombreCertificado(nombreCertificado);

							paramFormato.setCertificado(certificado);

							paramFormato.setPassCertificado(passCertificado);

							paramFormato.setNumDocumentoContribuyente(nitCompania);
							
							paramFormato.setTipoAjusteNota((reg1.getCampos().get("CODIGO_NOTA").toString()));

							paramLegalizar.setParamFormato(paramFormato);

							paramLegalizar.setPrefijo(paramFormato.getPrefijo());
							
							// se consulta el parametro 
							// con el fin de que se envie correo solo si tiene el paramentro en SI,
							//  asginando como valor el 99 para asi ser reconocido por el api de frida
							String paramCorreoAuth = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, 
									"MANEJA ENVIO DE CORREO A TERCERO DOCUMENTO SOPORTE", 
									SessionUtil.getModulo(), 
									new Date(), 
									false), "NO");
							if("SI".equals(paramCorreoAuth)) {
								paramLegalizar.setTipoReporte(99);
							}else {
								paramLegalizar.setTipoReporte(0);
							}
							
							String respuestaLegalizar = null;
							APIFrida apiFrida = new APIFrida();

							Gson gson2 = new Gson();

							try {
								String json = gson2.toJson(paramLegalizar, ParametrosLegalizarFactura.class);
								//String json = null;
								respuestaLegalizar = apiFrida.postFormatoLegalizar(url, json);
							} catch (SysmanException e) {
								logger.error(e.getMessage(), e);
								JsfUtil.agregarMensajeError(e.getMessage());
								log = log + "\n" + " Respuesta nota: " + reg.getCampos().get("NUMFORMATO").toString()
										+ ": " + e.getMessage();
							}
							RespuestaApi respuestaApis = gson2.fromJson(respuestaLegalizar, RespuestaApi.class);

							if (respuestaApis != null && respuestaApis.getCodigo() != 0) {
								log = log + "\n" + respuestaApis.getMensaje() + " "
										+ " NOTA DE AJUSTE DOCUMENTO SOPORTE:"
										+ reg.getCampos().get("NUMFORMATO").toString()
										+ ". Proceso terminado satisfactoriamente ";
							} else if (respuestaApis != null && respuestaApis.getCodigo() == 0) {

								log = log + "\n"
										+ crearReportesFridaNotas(respuestaApis,
												reg.getCampos().get("NUMFORMATO").toString())
										+ "LA NOTA DE AJUSTE DE DOC SOPORTE CON NUMERO " 
										+ reg.getCampos().get("NUMFORMATO").toString() + " ENVIADA";
							}
						}

					}
				}

			}

			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarDian.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		} catch (SystemException | IOException | SysmanException | ParseException | JRException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void eliminarTabEstadoNotas() {

		try {
			UrlBean urlDelete2 = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmdocdiandsControladorUrlEnum.URL1987.getValue());

			requestManager.delete(urlDelete2.getUrl(), null);
		} catch (SystemException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private void insertarNotas(RespuestaNotasReporte respuestaNotasReporte) {
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		try {

			String urlEnumId2 = FrmdocdiandsControladorUrlEnum.URL7456.getValue();

			Date fecha = formato.parse(respuestaNotasReporte.getFecha().replace("-", "/"));

			Map<String, Object> params2 = new TreeMap<>();

			params2.put(GeneralParameterEnum.CLASE.getName(), respuestaNotasReporte.getClase());
			params2.put("ESTADO", respuestaNotasReporte.getEstado());
			params2.put(GeneralParameterEnum.FECHA.getName(), fecha);
			params2.put("NUMFACTURA", respuestaNotasReporte.getNumFactura());
			params2.put("NUMFORMATO", respuestaNotasReporte.getNumFormato());
			params2.put(GeneralParameterEnum.OBSERVACION.getName(), respuestaNotasReporte.getObservacion());
			params2.put("TIPONOTA", respuestaNotasReporte.getTipoNota());
			params2.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
			params2.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

			UrlBean urlCreate2 = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId2);

			requestManager.save(urlCreate2.getUrl(), urlCreate2.getMetodo(), params2);

		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	 /**
     * @author mrosero
     * 18/08/2023
     * Funci�n creada para generar el zip y pdf de las notas, debido a que 
     * el json de respuesta de la Dian,retorna en la etiqueta "resultadoProcesoDian" un
     * array de objetos que inicia con el caracter [], lo cual se diferencia de la respuesta
     * de las facturas que inicia con el caracter {} y denota un objeto.
     * @param respuestaLegalizar
     * @param factura
     * @return
     */
	private String crearReportesFridaNotas(RespuestaApi respuestaLegalizar, String factura) {

		String log = "";

		OutputStream outZip = null;
		OutputStream outPdf = null;

		Gson gson = new Gson();

		RespuestaApi legalizar = respuestaLegalizar;
		Map<String, Object> param2 = (Map<String, Object>) legalizar.getCuerpo();
		String zip = param2.get("zip").toString(); // zip
		String pdf = null;
		if (param2.get("Reporte") != null) {
			pdf = param2.get("Reporte").toString();
		}

		Registro rsRutaArchivos;
		try {
			rsRutaArchivos = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(),
									null));

			if (rsRutaArchivos != null) {

				String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_FACTURAS"), "").toString();

				if (!ruta.isEmpty()) {

					File verificar = new File(ruta);
					if (!verificar.isDirectory()) {
						verificar.mkdirs();
					}

					if (zip != null) {

						byte[] archivoZip = Base64.getDecoder().decode(zip);

						outZip = new FileOutputStream(ruta + "/" + "NOTADOCSOPORTE" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".zip");
						outZip.write(archivoZip);
						outZip.close();

					}

					if (pdf != null) {
						byte[] archivoPdf = Base64.getDecoder().decode(pdf);

						outPdf = new FileOutputStream(ruta + "/" + "NOTADOCSOPORTE" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".pdf");
						outPdf.write(archivoPdf);
						outPdf.close();
					}
					// --- Lectura del tag de respuesta de la DIAN
					Map<String, Object> param3 = (Map<String, Object>) param2.get("resultadoProcesoDian");
					log = SysmanFunciones.nvl(param3.get("ErrorMessage"), "").toString();

				} else {
					JsfUtil.agregarMensajeAlerta("Debe crear la ruta en donde se generaran los reportes");
				}

			}

		} catch (SystemException | ParseException | IOException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

		return log;

	}
	
// FIN NOTAS DE AJUSTE	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarDocumentos
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirEnviarDocumentos() {
	     archivoDescarga = null;

	        int tipoformato;
	        String prFormato;
	        String codigoReporte;
	        String url;
	        String log = "";
	        StringBuilder pieCodigoBarra = new StringBuilder();
	        StringBuilder pieTextoBarra = new StringBuilder();
	        String codigoBarra;

	        log = "|---------------     ENVIO DIAN DOC SOPORTE     ---------------|";

	        try {
	            url = ejbSysmanUtil.consultarParametro(compania,
	                            "URL SERVICIO REST", "69", new Date(), false);

	            if (SysmanFunciones.validarVariableVacio(url)) {
	                JsfUtil.agregarMensajeAlerta(
	                                "Asegurese de configurar el parametro URL SERVICIO REST");
	            }
	            else {

	                prFormato = ejbSysmanUtil.consultarParametro(compania,
	                                "SF FORMATO FACTURACION ELECTRONICA",
	                                SessionUtil.getModulo(), new Date(), false);

	                eliminarTabEstadoFact();

	                String respuesta;
	                APIFrida api = new APIFrida();
	               
	                respuesta = api.cargarFormatoConsultarReporte(url, nitCompania,
	                                "10", "", "181",
	                                SysmanFunciones.convertirAFechaCadena(
	                                                SysmanFunciones.convertirAFecha(
	                                                                "18/05/2020"),
	                                                "yyyy/MM/dd"),
	                                SysmanFunciones.convertirAFechaCadena(
	                                                new Date(),
	                                                "yyyy/MM/dd"),
	                                "0", "0",
	                                "0","1","0");

	                Gson gson = new Gson();
	                RespuestaConsultarReporte respuestaApi = gson.fromJson(
	                                respuesta,
	                                RespuestaConsultarReporte.class);

	                for (RespuestaFacturasReporte respuestaFacturasReporte : respuestaApi
	                                .getCuerpo().getDocumentoSoporte()) {
	                    insertarFacturas(respuestaFacturasReporte);
	                }

	                List<Registro> listaEstadoFac = RegistroConverter
	                                .toListRegistro(
	                                                requestManager.getList(
	                                                                UrlServiceUtil
	                                                                                .getInstance()
	                                                                                .getUrlServiceByUrlByEnumID(
	                                                                                		FrmdocdiandsControladorUrlEnum.URL3561
	                                                                                                                .getValue())
	                                                                                .getUrl(),
	                                                                null));

	                if (!listaEstadoFac.isEmpty()) {

	                    for (Registro reg : listaEstadoFac) {
	                    	 pieCodigoBarra = new StringBuilder();
	                         pieTextoBarra = new StringBuilder();
	                        Map<String, Object> param = new TreeMap<>();

	                        param.put(GeneralParameterEnum.NUMERO.getName(), reg
	                                        .getCampos()
	                                        .get(GeneralParameterEnum.NUMERO
	                                                        .getName()));
                            tipoformato = 5;
                            

	                        // consumirEalvarez

	                        Map<String, Object> param2 = new TreeMap<>();

	                        param2.put(GeneralParameterEnum.ANO.getName(),
	                                        SysmanFunciones.ano(new Date()));
	                        param2.put("PREFIJO", reg.getCampos().get("PREFIJO"));

	                        Registro rs = RegistroConverter
	                                        .toRegistro(requestManager.get(
	                                                        UrlServiceUtil.getInstance()
	                                                                        .getUrlServiceByUrlByEnumID(
	                                                                        		FrmdocdiandsControladorUrlEnum.URL2735
	                                                                                                        .getValue())
	                                                                        .getUrl(),
	                                                        param2));

	                        if (rs != null) {

	                            codigoReporte = SysmanFunciones
	                                            .nvl(rs.getCampos().get("FORMATO"),
	                                                            "30002195")
	                                            .toString();

	                        }
	                        else {
	                            codigoReporte = prFormato;
	                        }

	                        Registro rsCertificado = RegistroConverter
	                                        .toRegistro(requestManager.get(
	                                                        UrlServiceUtil.getInstance()
	                                                                        .getUrlServiceByUrlByEnumID(
	                                                                        		FrmdocdiandsControladorUrlEnum.URL9457
	                                                                                                        .getValue())
	                                                                        .getUrl(),
	                                                        null));

	                        if (rsCertificado != null) {

	                            File archivo = new File(
	                                            rsCertificado.getCampos().get(
	                                                            "RUTA_CERTIFICADO")
	                                                            .toString());

	                            String nombreCertificado = archivo.getName();

	                            byte[] archivoBytes = Files
	                                            .readAllBytes(archivo.toPath());

	                            String certificado = Base64.getEncoder()
	                                            .encodeToString(archivoBytes);

	                            String passCertificado = Base64.getEncoder()
	                                            .encodeToString(rsCertificado
	                                                            .getCampos()
	                                                            .get("CONTRA_CERTIFICADO")
	                                                            .toString()
	                                                            .getBytes());

	                            ParametrosLegalizarFactura paramLegalizar = new ParametrosLegalizarFactura();

	                            paramLegalizar.setTipoSalida(1);
	                            paramLegalizar.setTestSetId(rsCertificado
	                                            .getCampos().get("TES_ID")
	                                            .toString());
	                            paramLegalizar.setCodigoReporte(codigoReporte);

	                            ParametrosFormato paramFormato = new ParametrosFormato();

	                            paramFormato.setTipoFormato(
	                                            Integer.toString(tipoformato));

	                            paramFormato.setNumFormato(reg
	                                            .getCampos()
	                                            .get(GeneralParameterEnum.NUMERO
	                                                            .getName())
	                                            .toString());

	                            paramFormato.setPrefijo(reg.getCampos()
	                                            .get("PREFIJO").toString());

	                            paramFormato.setNombreCertificado(
	                                            nombreCertificado);

	                            paramFormato.setCertificado(certificado);

	                            paramFormato.setPassCertificado(passCertificado);

	                            paramFormato.setNumDocumentoContribuyente(
	                                            nitCompania);

	                            // Adicion Codigo Barras

	                            boolean permiteCodigoBarra = false;

	                            paramFormato.setMuestraCodigoBarras(
	                                            permiteCodigoBarra);
	                       
	                            paramLegalizar.setParamFormato(paramFormato);
	                            paramLegalizar.setPrefijo(paramFormato.getPrefijo());
	                            
	                            // se consulta el parametro 
								// con el fin de que se envie correo solo si tiene el paramentro en SI,
								//  asginando como valor el 99 para asi ser reconocido por el api de frida
								String paramCorreoAuth = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, 
										"MANEJA ENVIO DE CORREO A TERCERO DOCUMENTO SOPORTE", 
										SessionUtil.getModulo(), 
										new Date(), 
										false), "NO");
								if("SI".equals(paramCorreoAuth)) {
									paramLegalizar.setTipoReporte(99);
								}else {
									paramLegalizar.setTipoReporte(0);
								}
	                            String respuestaLegalizar;
	                            APIFrida apiFrida = new APIFrida();

	                            Gson gson2 = new Gson();
	                            String json = gson2.toJson(paramLegalizar,
	                                            ParametrosLegalizarFactura.class);
	                            
	                            respuestaLegalizar = apiFrida
	                                            .postFormatoLegalizar(url, json);
	                           
	                            RespuestaApi respuestaApis = gson2.fromJson(
	                                            respuestaLegalizar,
	                                            RespuestaApi.class);

	                            if (respuestaApis.getCodigo() != 0) {
	                                log = log + "\n" + respuestaApis.getMensaje()
	                                    + " "
	                                    + (tipoformato == 5 ? " DOCUMENTO SOPORTE:"
	                                        : " NOTA:")
	                                    + reg
	                                                    .getCampos()
	                                                    .get(GeneralParameterEnum.NUMERO
	                                                                    .getName())
	                                                    .toString()
	                                    + ". Proceso terminado satisfactoriamente ";
	                            }
	                            else {

	                                log = log + "\n" 
                                    + (tipoformato == 5 ? " DOCUMENTO SOPORTE:"
                                        : " NOTA:")
                                    + reg
                                                    .getCampos()
                                                    .get(GeneralParameterEnum.NUMERO
                                                                    .getName())
                                                    .toString()
                                    + ". Proceso terminado satisfactoriamente " + "\n"
	                                    + crearReportesFrida(respuestaApis, reg
	                                                    .getCampos()
	                                                    .get(GeneralParameterEnum.NUMERO
	                                                                    .getName())
	                                                    .toString());

	                            }
	                        }

	                    }

	                }
	            }

	            ByteArrayInputStream streamTexto = JsfUtil
	                            .serializarPlano(log);
	            archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
	                            "LogEnvioDian.txt");

	            JsfUtil.agregarMensajeInformativo(
	                            idioma.getString("MSM_PROCESO_EJECUTADO"));
	        }
	        catch (SystemException | IOException | SysmanException
	                        | ParseException | JRException e) {

	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
		
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * metodo para el uso en envio de documento soporte a la dian
	 * 
	 * @param respuestaLegalizar
	 * @param factura
	 * @return
	 */
	private String crearReportesFrida(RespuestaApi respuestaLegalizar, String factura) {

		String log = "";

		OutputStream outZip = null;
		OutputStream outPdf = null;

		Gson gson = new Gson();
		
		RespuestaApi legalizar = respuestaLegalizar;
		Map<String, Object> param2 = (Map<String, Object>) legalizar.getCuerpo();
		String zip = param2.get("zip").toString(); // zip
		String pdf = null;
		if (param2.get("Reporte") != null) {
			pdf = param2.get("Reporte").toString();
		}

		Registro rsRutaArchivos;
		try {
			rsRutaArchivos = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(),
									null));

			if (rsRutaArchivos != null) {

				String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_FACTURAS"), "").toString();

				if (!ruta.isEmpty()) {

					File verificar = new File(ruta);
					if (!verificar.isDirectory()) {
						verificar.mkdirs();
					}

					if (zip != null) {

						byte[] archivoZip = Base64.getDecoder().decode(zip);

						outZip = new FileOutputStream(ruta + "/" + "DOCSOPORTE" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".zip");
						outZip.write(archivoZip);
						outZip.close();

					}

					if (pdf != null) {
						byte[] archivoPdf = Base64.getDecoder().decode(pdf);

						outPdf = new FileOutputStream(ruta + "/" + "DOCSOPORTE" + factura + "_"
								+ SysmanFunciones.convertirAFechaCadena(new Date(), "YYYYMMDD_HHMMSS") + ".pdf");
						outPdf.write(archivoPdf);
						outPdf.close();
					}

					// --- Lectura del tag de respuesta de la DIAN
//					Map<String, Object> param3 = (Map<String, Object>) param2.get("resultadoProcesoDian");
//					log = SysmanFunciones.nvl(param3.get("ErrorMessage"), "").toString();
					 ArrayList param3 = (ArrayList) param2.get("resultadoProcesoDian");
					param2 = (Map<String, Object>) param3.get(0);
					if (param2.get("IsValid").toString().equals("false")) {// get de errores
						log = SysmanFunciones.nvl(param2.get("ErrorMessage"), "").toString();
					}

				} else {
					JsfUtil.agregarMensajeAlerta("Debe crear la ruta en donde se generaran los reportes");
				}

			}

		} catch (SystemException | ParseException | IOException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}

		return log;

	}
	

	 private void insertarFacturas(
		        RespuestaFacturasReporte respuestaFacturasReporte) {

		        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");

		        try {

		            String urlEnumId = FrmdocdiandsControladorUrlEnum.URL9848
		                            .getValue();

		            Date fecha = formato.parse(
		                            respuestaFacturasReporte.getFecha()
		                                            .replace("-", "/"));

		            Map<String, Object> params = new TreeMap<>();

		            params.put(GeneralParameterEnum.ESTADO.getName(),
		                            respuestaFacturasReporte.getEstado());
		            params.put(GeneralParameterEnum.NUMERO.getName(),
		                            respuestaFacturasReporte.getNumFormato());
		            params.put(GeneralParameterEnum.TERCERO.getName(),
		                            respuestaFacturasReporte.getTercero());
		            params.put(GeneralParameterEnum.TOTAL.getName(),
		                            new BigDecimal(respuestaFacturasReporte
		                                            .getTotal()));
		            params.put(GeneralParameterEnum.FECHA.getName(),
		                            fecha);
		            params.put(GeneralParameterEnum.OBSERVACION.getName(),
		                            respuestaFacturasReporte.getObservacion());
		            params.put("PREFIJO",
		                            respuestaFacturasReporte.getPrefijo());
		            params.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);
		            params.put(GeneralParameterEnum.DATE_CREATED.getName(),
		                            new Date());
		            UrlBean urlCreate = UrlServiceUtil.getInstance()
		                            .getUrlServiceByUrlByEnumID(urlEnumId);

		            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
		                            params);
		        }
		        catch (SystemException | ParseException e) {
		            logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }

		    }

		    private void eliminarTabEstadoFact() {

		        try {

		            UrlBean urlDelete = UrlServiceUtil.getInstance()
		                            .getUrlServiceByUrlByEnumID(
		                            		FrmdocdiandsControladorUrlEnum.URL5474
		                                                            .getValue());

		            requestManager.delete(urlDelete.getUrl(), null);

		        }
		        catch (SystemException e) {
		            logger.error(e.getMessage(), e);
		            JsfUtil.agregarMensajeError(e.getMessage());
		        }

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
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	public String getNitCompania() {
		return nitCompania;
	}
	public void setNitCompania(String nitCompania) {
		this.nitCompania = nitCompania;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public EjbSysmanUtilRemote getEjbSysmanUtil() {
		return ejbSysmanUtil;
	}
	public void setEjbSysmanUtil(EjbSysmanUtilRemote ejbSysmanUtil) {
		this.ejbSysmanUtil = ejbSysmanUtil;
	}
	public String getCompania() {
		return compania;
	}
	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
