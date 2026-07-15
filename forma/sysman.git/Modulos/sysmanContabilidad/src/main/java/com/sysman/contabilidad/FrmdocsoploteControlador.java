/*-
 * FrmdocsoploteControlador.java
 *
 * 1.0
 * 
 * 27/10/2022
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;

import com.google.gson.Gson;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.google.gson.JsonSyntaxException;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ComprobantecntsControladorEnum;
import com.sysman.contabilidad.enums.ComprobantecntsControladorUrlEnum;
import com.sysman.contabilidad.enums.FrmdocsoploteControladorEnum;
import com.sysman.contabilidad.enums.FrmdocsoploteControladorUrlEnum;
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
import com.sysman.util.rest.ParamItem;
import com.sysman.util.rest.ParamItems;
import com.sysman.util.rest.ParametroCuerpoEnvioFactura;
import com.sysman.util.rest.ParametroDeleteEnvioFactura;
import com.sysman.util.rest.ParametrosCargos;
import com.sysman.util.rest.ParametrosDescuentos;
import com.sysman.util.rest.ParametrosEnvioFactura;
import com.sysman.util.rest.ParametrosEnvioFacturaFiltros;
import com.sysman.util.rest.ParametrosImpuestos;
import com.sysman.util.rest.ParametrosItems;
import com.sysman.util.rest.ParametrosItemsImpuestos;
import com.sysman.util.rest.ParametrosTercero;
import com.sysman.util.rest.ParametrosTerceroLote;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaConsultarReporte;
import com.sysman.util.rest.RespuestaEnvioFactura;
import com.sysman.util.rest.RespuestaNotasReporte;

import com.sysman.util.soap.ApiInvoway;
import com.sysman.util.soap.es.delogica.saaf.ws.consultaestados.impl.Documento;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;

/**
 * TODO Clase que representa al controlador de la pagina de envio a frida de
 * documento soporte.
 *
 * @version 1.0, 27/10/2022
 * @author ljdiaz
 */
@ManagedBean
@ViewScoped
public class FrmdocsoploteControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;

	private String nitCompania;
	
	private int anio;

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
	/**
	 *  represetna el total del documento soporte
	 */
	double totalFactura = 0;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	private String url;

	private boolean esNota;


//</DECLARAR_ATRIBUTOS>
//<DECLARAR_PARAMETROS>
//</DECLARAR_PARAMETROS>
//<DECLARAR_LISTAS>
//</DECLARAR_LISTAS>
//<DECLARAR_LISTAS_COMBO_GRANDE>
//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmdocsoploteControlador
	 */
	public FrmdocsoploteControlador() {
		super();
		compania = SessionUtil.getCompania();
		nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		anio = SysmanFunciones.ano(new Date());
		usuario = SessionUtil.getUser().getCodigo();
		fechaInicio = new Date();
		fechaFin = new Date();
		try {
			// 2371
			numFormulario = GeneralCodigoFormaEnum.ENVIARDIANDOCSOPORTE.getCodigo();
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
		if (nitCompania.contains("-")) {
			int fin = nitCompania.indexOf("-");
			nitCompania = nitCompania.substring(0, fin);
		}
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>
	/**
	 * Envío masivo de documentos soporte a Invoway, disparado desde el botón
	 * "Enviar Doc Soporte" en la interfaz.
	 *
	 * <p>Verifica que el parámetro {@code "MANEJA FACTURACION ELECTRONICA EXTERNA"}
	 * esté en {@code "SI"} y que la URL del WS esté configurada. Luego consulta
	 * todos los documentos tipo {@code "DSE"} en el rango de fechas seleccionado
	 * y los envía uno a uno llamando a {@link #envioInvoway}.</p>
	 *
	 * <p>Al finalizar genera un archivo {@code LogEnvioDian.txt} descargable con
	 * el resultado de cada envío. Si el parámetro no es {@code "SI"}, ejecuta
	 * la lógica de envío por FRIDA (facturador anterior).</p>
	 */
	public void oprimirEnviarDocSoporte() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		String log;
		try {

			String manejaInvoway = ejbSysmanUtil.consultarParametro(compania,
        			"MANEJA FACTURACION ELECTRONICA EXTERNA", "69", new Date(), false);
        	if(manejaInvoway.equals("SI")){
        		log = "|---------------     ENVIO DOC SOPORTE INVOWAY      ---------------|";
        		url = ejbSysmanUtil.consultarParametro(compania,
        				"URL SERVICIO SOAP", "69", new Date(), false);
        		
        		if (SysmanFunciones.validarVariableVacio(url)) {
        			JsfUtil.agregarMensajeAlerta(
        					"Asegurese de configurar el parametro URL SERVICIO SOAP");
        		}else {
        			try {
        				
	                
	                Map<String, Object> parametros = new TreeMap<>();
	    	        
	                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/YYYY");
	                parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	                parametros.put("FECHAINI", formatoFecha.format(fechaInicio));
	                parametros.put("FECHAFIN", formatoFecha.format(fechaFin));
	                
	        		
	    			List<Registro> listaFactSysman = RegistroConverter
	                            .toListRegistro(
	                                            requestManager
	                                                            .getList(
	                                                                            UrlServiceUtil.getInstance()
	                                                                                            .getUrlServiceByUrlByEnumID(
	                                                                                            		FrmdocsoploteControladorUrlEnum.URL1895016
	                                                                                                                            .getValue())
	                                                                                            .getUrl(), parametros));
	            		if (!listaFactSysman.isEmpty())
	    	            {    	
	    	                for (Registro reg : listaFactSysman)
	    	                {
	    	                	String tipoCobro = SysmanFunciones.toString(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.TIPOCOBRO.getName()),""));
	    	                	
	    	                	if(tipoCobro.equals("DSE")){//omitirla porque no es necesaria 
	    	                		log = log  +  "\n" + envioInvoway(SysmanFunciones.toString(
	    	                				SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.NUMEROFACTURA.getName()),"1")),
	    	                				SysmanFunciones.toString(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.TIPOCOBRO.getName()),"")),
	    	                				SysmanFunciones.toString(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()),"")),false);
	    	                	}
	    	                }
	    	            }
						ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
						archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnvioDian.txt");

						JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
	    	       
					} catch (SystemException  e1) {
						logger.error(e1.getMessage(), e1);
						JsfUtil.agregarMensajeError(e1.getMessage());
					}
				}

			} else {
				log = "|---------------     ENVIO DOC SOPORTE FRIDA      ---------------|";
			
			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);

			Calendar c = Calendar.getInstance();
			c.setTime(fechaFin);
			c.add(Calendar.DATE, 1);
			fechaFin = c.getTime();

			Map<String, Object> param = new TreeMap<>();
			
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaInicio));
			param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaFin));
			param.put(ComprobantecntsControladorEnum.NITCOMPANIA.getValue(), nitCompania);
			
			List<Registro> listaFactSysman = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmdocsoploteControladorUrlEnum.URL1895016.getValue())
											.getUrl(),
									param));
			if (!listaFactSysman.isEmpty()) {

				for (Registro reg : listaFactSysman) {
								
					String respuesta;
					APIFrida apiFrida = new APIFrida();

					respuesta = apiFrida.cargarTercero(nitCompania, reg.getCampos().get("NUMTERCERO").toString(), url);

					Gson gson = new Gson();
					RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);

					if (respuestaApi.getCodigo() != 0) {
						String respuestaTercero = crearTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
						if(respuestaTercero.contains("NO_OK")) {
							respuestaTercero = respuestaTercero.replaceAll("NO_OK", "");
							log = log + "\n verificar tercero y reevniar \n " + respuesta;
						}
						log = log + "\n" + respuestaTercero;
					}else {
						log = log + "\n" + actualizaTecero(reg.getCampos().get("NUMTERCERO").toString(), url);
					}					
				}

				for (Registro reg : listaFactSysman) {
					if (reg.getCampos().get("CODIGOPRODUCTODIAN") != null) {
						String codigoProducto = reg.getCampos().get("CODIGOPRODUCTODIAN").toString().trim();
					    // Reemplazar espacios por guion bajo
					    codigoProducto = codigoProducto.replace(" ", "_");
						validarProducto(url, codigoProducto);
					}

					String prefijo = SysmanFunciones.nvl(reg.getCampos().get("PREFIJO"), "").toString();
 					// EL NUMERO DEL COMPROBANTE EN LA CONTABLIDAD
					String numeroFactura = SysmanFunciones.nvl(reg.getCampos().get("NUMEROFACTURA"), "").toString();
					// EL NUMERO DEL CONSECUTIVO CON EL SE CARGARA A FRIDA Y LUEGO A LA DIAN Y ESTA RESPALDADO POR LA RESOLUCION.
					String consecutivoDian = SysmanFunciones.nvl(reg.getCampos().get("CONSECUTIVODIAN"), "").toString();
					
					boolean facExiste = false;

					String tipoFormato;
					if ("NC".equals(prefijo))
						tipoFormato = "02";
					else {
						tipoFormato = "ND".equals(prefijo) ? "03" : "05";
					}
					String respuesta = null;
					APIFrida api = new APIFrida();
					try {
					    respuesta = api.cargarEnvioFacatura(nitCompania, url);

					    Gson gson = new Gson();
					    RespuestaEnvioFactura respuestaApi = null;

					    if (respuesta != null && respuesta.trim().startsWith("{")) {
					        // Intentamos parsear solo si es JSON
					        respuestaApi = gson.fromJson(respuesta, RespuestaEnvioFactura.class);

					        if (respuestaApi.getCuerpo() == null || respuestaApi.getCuerpo().isEmpty()) {
					            logger.error(log + " respuestaApi.getCuerpo() es null o vacío" + " de la Factura: " + numeroFactura);
					            return;
					        }
					for (int i = 0; i < respuestaApi.getCuerpo().size(); i++) {
						if (respuestaApi.getCuerpo().get(i) == null) {
						    logger.error("respuestaApi.getCuerpo() es null");
						    return;
						}
						facExiste = false;
						List<Object> datos = (List<Object>) respuestaApi.getCuerpo().get(i);

						if (numeroFactura != null && numeroFactura
								.equals(new DecimalFormat("#.####################################")
										.format(datos.get(0)))
								&& prefijo.equals(datos.get(1))) {
							facExiste = true;
						}
						// Borrar Factura
						if (facExiste) {
							Registro rs = RegistroConverter
									.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL9457.getValue())
											.getUrl(), null));

							if (rs != null) {

								File archivo = new File(rs.getCampos().get("RUTA_CERTIFICADO").toString());

								String nombreCertificado = archivo.getName();

								byte[] archivoBytes = Files.readAllBytes(archivo.toPath());

								String certificado = Base64.getEncoder().encodeToString(archivoBytes);

								String passCertificado = Base64.getEncoder().encodeToString(
										rs.getCampos().get("CONTRA_CERTIFICADO").toString().getBytes());

								ParametroDeleteEnvioFactura paramDelete = new ParametroDeleteEnvioFactura();

								paramDelete.setTipoFormato(tipoFormato);
								paramDelete.setNumFormato(numeroFactura);
								paramDelete.setPrefijo(prefijo);
								paramDelete.setCertificado(certificado);
								paramDelete.setNombreCertificado(nombreCertificado);
								paramDelete.setPassCertificado(passCertificado);
								paramDelete.setNumDocumentoContribuyente(nitCompania);

								Gson gson2 = new Gson();
								String json = gson2.toJson(paramDelete, ParametroDeleteEnvioFactura.class);

								APIFrida apiFrida = new APIFrida();

								String val = apiFrida.deleteEnvioFactura(url, json);
								log = log + "\n" + val;

								if (val.startsWith("!")) {
									ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
									archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
											"LogEnviarDian.txt");

									JsfUtil.agregarMensajeInformativo(
											idioma.getString("MSM_PROCESO_EJECUTADO"));
									return;
								}

							}

						}
					}
					}} catch (JsonSyntaxException e) {
					    logger.error("Error al parsear JSON en cargarEnvioFacatura: " + respuesta, e);
					    return;
					}	

					log = log + "\n" + exportarFacturas(url, numeroFactura, consecutivoDian, prefijo,
							SysmanFunciones.nvl(reg.getCampos().get("TIPOCOBRO"), "").toString());
					
				}
				

			}else {
				log = log + idioma.getString("MSM_DOCUMENTOS_SIN_CUDS") + "\n";
			}
			c.add(Calendar.DATE, -1);
			fechaFin = c.getTime();

			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarDocSoporte.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			}

		} catch (SystemException | IOException | SysmanException | JRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	
	
	/**
	 * Orquesta el envío de un documento soporte o nota de ajuste individual a Invoway.
	 * Consulta los datos del comprobante, retenciones y productos desde la BD,
	 * construye el objeto {@link Documento} con {@link InvowayDocumentoBuilder}
	 * y lo envía al Web Service mediante {@link ApiInvoway#postEnvioFactura}.
	 *
	 * <p>Determina automáticamente si es documento soporte o nota de ajuste según
	 * el parámetro {@code esNota}, lo que cambia el tipo de documento, subtipo,
	 * y las URLs de consulta de productos usadas.</p>
	 *
	 * @param numeroFactura    Número interno del comprobante en Sysman.
	 * @param tipocobro        Tipo de comprobante (ej: {@code "DSE"} para documento soporte,
	 *                         {@code "NCA"} para nota de ajuste).
	 * @param consecutivoDian  Consecutivo DIAN asignado al documento.
	 * @param esNota           {@code true} si es nota de ajuste, {@code false} si es
	 *                         documento soporte.
	 * @return                 Mensaje de respuesta de Invoway concatenado con el número
	 *                         de comprobante y consecutivo DIAN, para registro en el log.
	 */
	private String envioInvoway(String numeroFactura, String tipocobro, String consecutivoDian, Boolean esNota) {
		String respuesta = "";
		try {
		String nitCompania = SessionUtil.getCompaniaIngreso().getNit();

		
		
		 Map<String, Object> param = new TreeMap<>();
	        
	        SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
	        

	        
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitCompania);
	        param.put(GeneralParameterEnum.NUMEROFACTURA.getName(),numeroFactura);
	        param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipocobro);
	        param.put("TIPOCOMPROBANTE", tipocobro);
	        param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaInicio));
	        param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaFin));

	        String tipoDocumento = "";      
	        String subTipoDocumento = "";    
	        String tipoOperacion = "";  
	        String urlFactura = "";
        	String urlRetenciones = "";
        	String urlProductos = "";
        
        	urlFactura = FrmdocsoploteControladorUrlEnum.URL1895026.getValue();

        	if(!esNota) {
        		 tipoDocumento = "CC";      
    	         subTipoDocumento = "05";    
    	         tipoOperacion = "10"; 
    	        
	        	 urlRetenciones = FrmdocsoploteControladorUrlEnum.URL1895006.getValue();
	        	 urlProductos = FrmdocsoploteControladorUrlEnum.URL1895007.getValue();
        	}else {
	        	 tipoDocumento = "NA";      
	   	         subTipoDocumento = "95";    
	   	         tipoOperacion = "10";
   	         
	        	 urlRetenciones = FrmdocsoploteControladorUrlEnum.URL1895006.getValue();
	        	 urlProductos = FrmdocsoploteControladorUrlEnum.URL1895030.getValue();
	        }
     
			Registro factura = RegistroConverter
			                    .toRegistro(requestManager.get(
			                                    UrlServiceUtil.getInstance()
			                                                    .getUrlServiceByUrlByEnumID(urlFactura)
			                                                    .getUrl(),
			                                    param));
			
		        
		        Registro impuestosRetenciones = RegistroConverter
                     .toRegistro(requestManager.get(
                                     UrlServiceUtil.getInstance()
                                                     .getUrlServiceByUrlByEnumID(urlRetenciones)
                                                     .getUrl(),
                                     param));
		        
		     // se agregan los productos 
		        List<Registro> productos = RegistroConverter
		        .toListRegistro(
		                requestManager.getList(
		                                UrlServiceUtil
		                                                .getInstance()
		                                                .getUrlServiceByUrlByEnumID(urlProductos)
		                                                .getUrl(),
		                                param));
		        
		        List<Registro> listaImpuestosTemp = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL7452.getValue())
										.getUrl(),
								param));

				
		        ApiInvoway apiInvoway = new ApiInvoway();
		        
		        // se consulta los parametros para traer el usuario y la contraseña del ws invoway
		        String user = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		        		ComprobantecntsControladorEnum.USUARIO_FACT_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"");
		        
		        String pass = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
		        		ComprobantecntsControladorEnum.CLAVE_FACT_ELECTRONICA_EXTERNA.getValue(), "69", new Date(), false),"");
	            		      
		        Registro documentosReferenciados = new Registro();
		        
		        if (esNota && (factura != null)) {
		            String prefijoRef = SysmanFunciones.nvlStr(
		                SysmanFunciones.toString(factura.getCampos().get("PREFIJO_DIAN_AFECT")), "");
		            String numRef = SysmanFunciones.nvlStr(
		                SysmanFunciones.toString(factura.getCampos().get("CONSECUTIVO_DIAN_AFECT")), "");
		            
		            documentosReferenciados.getCampos().put("numDocumentoRef", prefijoRef + numRef);
		        }
		        
		        documentosReferenciados = esNota?documentosReferenciados:null;
		        
		        Documento documento = InvowayDocumentoBuilder.construir(
		                factura,
		                productos,
		                nitCompania,
		                consecutivoDian,
		                impuestosRetenciones,
		                listaImpuestosTemp,
		                tipoDocumento,
		                subTipoDocumento,
		                tipoOperacion,
		                documentosReferenciados,
		                compania,
		                fechaInicio,
		                fechaFin,
		                tipocobro
		        );
		        
		        if (esNota) {
		            documento.setMotivoRect("5");
		        }
		        
		        String xmlGenerado = documentoToXml(documento);
		        System.out.println("=== XML DOCUMENTO INVOWAY ===\n" + xmlGenerado);
		        
					respuesta = apiInvoway.postEnvioFactura(url, documento, pass, user)+" Comprobante Número : "+numeroFactura +
							" Consecutivo DIAN : " + consecutivoDian;

	                
		} catch (IOException | SystemException | SysmanException  e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
		
		return respuesta;
		
	}
	
	/**
	 * Convierte un objeto {@link Documento} a su representación XML en texto plano
	 * usando JAXB. Usado principalmente para imprimir el XML en consola durante
	 * depuración antes del envío a Invoway.
	 *
	 * @param documento Objeto a serializar.
	 * @return          String con el XML formateado, o un mensaje de error si falla.
	 */
	private String documentoToXml(Documento documento) {
	    try {
	        JAXBContext context = JAXBContext.newInstance(Documento.class);
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	        
	        StringWriter sw = new StringWriter();
	        marshaller.marshal(documento, sw);
	        return sw.toString();
	    } catch (JAXBException e) {
	        logger.error("Error serializando Documento a XML: " + e.getMessage(), e);
	        return "ERROR: " + e.getMessage();
	    }
	}
	
	private String exportarFacturas(String url, String numeroFactura, String consecutivoDian, String prefijo, String tipoFactura) {
		String respuesta = null;
		String datosFacturaBancos;
		String strObservaciones;
		double valorIVAConcepto = 0;
		double sumProdPorFactura = 0;
		double descuento = 0;
		String numeroResolucion = null;

		double redondeodescuento = 0;
		int digRedDian;

		int nConceptos = 0;

		ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();

		ParametroCuerpoEnvioFactura paramCuerpoFactura = new ParametroCuerpoEnvioFactura();

		Map<String, Object> param = new TreeMap<>();
		/**
		 * @author ljdiaz en este punto se manupula la hora de la fecha solicitud del
		 *         registro para que este no la lleve en ceros pues el systema requiere
		 *         que este entre 1 y 12
		 */
		SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaInicio));
		param.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaFin));
		param.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
		param.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);
		param.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitCompania);

		try {

			digRedDian = Integer.parseInt(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"SF NUMERO DIGITOS REDONDEO DIAN", SessionUtil.getModulo(), new Date(), false), "0").toString());
			// consula los datos de la factura para armar y consultar la informacion que
			// pertence al JSON final
			Registro rs2 = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													ComprobantecntsControladorUrlEnum.URL3564.getValue())
											.getUrl(),
									param));
			
			if (rs2 != null) {

				Map<String, Object> param2 = new TreeMap<>();
				param2.put(GeneralParameterEnum.CODIGO.getName(),
						rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));


				formatFecha = new SimpleDateFormat("yyyy-MM-dd");

				paramFactura.setCreatedBy(rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString());
				paramFactura.setNumerocontribuyente(nitCompania);

				datosFacturaBancos = ejbSysmanUtil.consultarParametro(compania, "DATOS FACTURA BANCOS",
						SessionUtil.getModulo(), new Date(), false);

				strObservaciones = SysmanFunciones
						.nvl(rs2.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()), "").toString();
				// el tipo de factura determina que es un documento soporte que se va a enviar
				paramCuerpoFactura.setTipoDeFactura("05");

				paramCuerpoFactura.setNumTercero(SysmanFunciones.nvl(rs2.getCampos().get("NUMTERCERO"), "").toString());

				paramCuerpoFactura.setFechafactura(
						SysmanFunciones.nvl(formatFecha.format(rs2.getCampos().get("FECHAFACTURA")), "").toString());

				paramCuerpoFactura.setFechaVencimiento(SysmanFunciones
						.nvl(formatFecha.format(rs2.getCampos().get("FECHA_VENCIMIENTO")), "").toString());
				
				// obetenemos el nuemero de resolucion configurado
				param = new TreeMap<>();

				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put(GeneralParameterEnum.CODIGO.getName(), tipoFactura);
				Registro rs = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1896005.getValue())
										.getUrl(),
								param));
				if (rs != null) {
					numeroResolucion = SysmanFunciones.nvl(rs.getCampos().get("NUMERO"), "").toString();
					prefijo = SysmanFunciones.nvl(rs.getCampos().get("PREFIJO"), "").toString();
					paramCuerpoFactura.setPrefijo(prefijo);
				}
				
				// se valida si el consecutivo esta en fecha y en el rango correspondiente a la
				// resulucion
				param = new TreeMap<>();
				formatFecha = new SimpleDateFormat("dd/MM/YYYY");
				param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param.put("FECHARESO", formatFecha.format(new Date()));
				param.put(GeneralParameterEnum.NUMERO.getName(), numeroResolucion);
				param.put("CONSECUTIVOUSAR", consecutivoDian);
				List<Registro> aux = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895015.getValue())
										.getUrl(),
								param));
				for (Registro aux1 : aux) {
					if (Integer.parseInt(SysmanFunciones.nvl(aux1.getCampos().get("EXISTE"), "").toString()) == 0) {
						JsfUtil.agregarMensajeError(
								"Por favor verifque la configuracion, pues su fecha o consecutivo no coinciden con la resuolucion");
						return "Por favor verifque la configuracion, pues su fecha o consecutivo no coinciden con la resuolucion";
					}
				}
			
				paramCuerpoFactura.setNumerofactura(Integer.parseInt(consecutivoDian));
				// OJO VERIFICAR COMO SE OBTIENE EL PREFIJO 
				paramCuerpoFactura.setPrefijo(prefijo);
				

				paramCuerpoFactura
						.setTelefonoCliente(SysmanFunciones.nvl(rs2.getCampos().get("TELEFONOS"), "").toString());

				paramCuerpoFactura.setTipoPago(SysmanFunciones.nvl(rs2.getCampos().get("TIPO_PAGO"), "").toString());

				paramCuerpoFactura.setMedioPago(SysmanFunciones.nvl(rs2.getCampos().get("MEDIOPAGO"), "").toString());

				paramCuerpoFactura
						.setTipoMoneda(SysmanFunciones.nvl(rs2.getCampos().get("TIPO_MONEDA"), "").toString());
				

				paramCuerpoFactura
						.setTipoOperacion(SysmanFunciones.nvl(rs2.getCampos().get("TIPOFACDIAN"), "").toString());

				paramCuerpoFactura.setObservacionesFactura(strObservaciones + "<br/>" + datosFacturaBancos);

				paramCuerpoFactura.setReviso(SysmanFunciones.nvl(
						ejbSysmanUtil.consultarParametro(compania, "SF NOMBRE REVISO EN FACTURA",
								SessionUtil.getModulo(), new Date(), false),
						rs2.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString()).toString());

				paramCuerpoFactura
						.setDatosSoftware(
								SysmanFunciones
										.nvl(ejbSysmanUtil.consultarParametro(compania,
												"DATOS SOFTWARE FACTURA ELECTRONICA", "69", new Date(), false), "")
										.toString());

				// Productos

				List<ParametrosItems> listaParamItems = new ArrayList<>();
				int contador = 0;
				Map<String, Object> param3 = new TreeMap<>();
				/**
				 * @author ldiaz en este punto se manupula la hora de la fecha solicitud del
				 *         registro para que este no la lleve en ceros pues el systema requiere
				 *         que este entre 1 y 12
				 */
				formatFecha = new SimpleDateFormat("dd/MM/yyyy");

				param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param3.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaInicio));
				param3.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaFin));
				param3.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
				param3.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);
				param3.put(GeneralParameterEnum.NITCOMPANIA.getName(), nitCompania);

				List<Registro> listaProductos = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL2974.getValue())
										.getUrl(),
								param3));
				if(listaProductos.size()>1) {
					Double acumtotaldebitos = 0.0;
					Registro regtemp = new Registro();
					for(Registro r : listaProductos) {
						regtemp = r;
						acumtotaldebitos = acumtotaldebitos + Double.parseDouble(r.getCampos().get("VALORUNITARIO").toString());
					}
					regtemp.getCampos().put("VALORUNITARIO",acumtotaldebitos);
					regtemp.getCampos().put("TOTALITEM",acumtotaldebitos);
					regtemp.getCampos().put("SUBTOTAL",acumtotaldebitos);
					listaProductos.clear();
					listaProductos.add(regtemp);
				}
				if (!listaProductos.isEmpty()) {

					for (Registro reg3 : listaProductos) {

						valorIVAConcepto = SysmanFunciones.nvlDbl(reg3.getCampos().get("BASEIMPUESTOIVA"), 0)
								* (SysmanFunciones.nvlDbl(reg3.getCampos().get("PORCIVA"), 0) / 100);

						sumProdPorFactura = sumProdPorFactura
								+ (SysmanFunciones.nvlDbl(reg3.getCampos().get("VALORUNITARIO").toString().replace(",", "."), 0)

										* SysmanFunciones.nvlDbl("1", 0))
								- SysmanFunciones.nvlDbl(reg3.getCampos().get("VALOR_DESCUENTO"), 0) + valorIVAConcepto;

						ParametrosItems paramItems = new ParametrosItems();

						paramItems.setCodigoproducto(
								SysmanFunciones.nvl(reg3.getCampos().get("CODIGOPRODUCTO"), "").toString());
						/**
						 * @author ljdiaz
						 * @descrpcion: se cambia el tipo de dato que recibe la cantidad de el producto,
						 *              para que esta reciba cantidades decimales y entenera.
						 */
						paramItems.setCantidad(Double
								.parseDouble("1"));

						paramItems.setDescripcionproducto(
								SysmanFunciones.nvl(reg3.getCampos().get("DESCRIPCIONPRODUCTO"), "").toString());

						paramItems.setValorunitario(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("VALORUNITARIO").toString().replace(",", "."), "0").toString()));

						paramItems.setTipoDescuento("05");

						paramItems.setDescuentoItem(
								SysmanFunciones.nvl(reg3.getCampos().get("VALOR_DESCUENTO"), "0").toString());

						paramItems.setTotalitem(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("TOTALITEM").toString().replace(",", "."), "0").toString()));

						List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();

						ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
						paramItemImpuestos.setTipo("01");

						paramItemImpuestos.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOIVA"), "0").toString()));

						paramItemImpuestos.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOIVA"), "0").toString()) == 0 ? 0
										: Double.parseDouble(
												SysmanFunciones.nvl(reg3.getCampos().get("PORCIVA"), "0").toString()));

						paramItemImpuestos.setValor(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("VALORIMPUESTO"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
						paramItemImpuestos2.setTipo("06");

						paramItemImpuestos2.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEFUENTE"), "0").toString()));

						paramItemImpuestos2.setPorcentaje(Double.parseDouble(SysmanFunciones
								.nvl(reg3.getCampos().get("BASEIMPUESTORETEFUENTE"), "0").toString()) == 0 ? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJERETEFUENTEX"), "0").toString()));

						paramItemImpuestos2.setValor(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("VALOR_RETEFUENTE"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
						paramItemImpuestos3.setTipo("03");

						paramItemImpuestos3.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOICA"), "0").toString()));

						paramItemImpuestos3.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOICA"), "0").toString()) == 0 ? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJEICAX"), "0").toString()));

						paramItemImpuestos3.setValor(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("VALOR_ICA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
						paramItemImpuestos4.setTipo("05");

						paramItemImpuestos4.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEIVA"), "0").toString()));

						paramItemImpuestos4.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEIVA"), "0").toString()) == 0
										? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJERETEIVADX"), "0").toString()));

						paramItemImpuestos4.setValor(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("RETEIVA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
						paramItemImpuestos5.setTipo("07");

						paramItemImpuestos5.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEICA"), "0").toString()));

						paramItemImpuestos5.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTORETEICA"), "0").toString()) == 0
										? 0
										: Double.parseDouble(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJERETEICAX"), "0").toString()));

						paramItemImpuestos5.setValor(Double
								.parseDouble(SysmanFunciones.nvl(reg3.getCampos().get("RETEICA"), "0").toString()));

						ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
						paramItemImpuestos6.setTipo("02");

						paramItemImpuestos6.setBase(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOINC"), "0").toString()));

						paramItemImpuestos6.setPorcentaje(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("BASEIMPUESTOINC"), "0").toString()) == 0 ? 0
										: Integer.parseInt(SysmanFunciones
												.nvl(reg3.getCampos().get("PORCENTAJEINCX"), "0").toString()));

						paramItemImpuestos6.setValor(Double.parseDouble(
								SysmanFunciones.nvl(reg3.getCampos().get("IMPUESTO_INC"), "0").toString()));

						if (paramItemImpuestos.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos);
						}
						if (paramItemImpuestos2.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos2);
						}
						if (paramItemImpuestos3.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos3);
						}
						if (paramItemImpuestos4.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos4);
						}
						if (paramItemImpuestos5.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos5);
						}
						if (paramItemImpuestos6.getBase() != 0.0) {
							listaParamItemImpuestos.add(paramItemImpuestos6);
						}

						paramItems.setImpuestos(listaParamItemImpuestos);

						listaParamItems.add(contador, paramItems);

						contador++;

						nConceptos++;
					}

				}
				// agregar lista items
				paramCuerpoFactura.setItems(listaParamItems);
				Map<String, Object> param4 = new TreeMap<>();
				/**
				 * @author ldiaz en este punto se manupula la hora de la fecha solicitud del
				 *         registro para que este no la lleve en ceros pues el systema requiere
				 *         que este entre 1 y 12
				 */
				
				formatFecha = new SimpleDateFormat("dd/MM/yyyy");

				param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param4.put(ComprobantecntsControladorEnum.FECHAINI.getValue(), formatFecha.format(fechaInicio));
				param4.put(ComprobantecntsControladorEnum.FECHAFIN.getValue(), formatFecha.format(fechaFin));
				param4.put(ComprobantecntsControladorEnum.NUMEROFACTURA.getValue(), numeroFactura);
				param4.put(ComprobantecntsControladorEnum.TIPOCOBRO.getValue(), tipoFactura);
				
				List<ParametrosCargos> listaParamCargos = new ArrayList<>();
				List<ParametrosDescuentos> listaParamDescuentos = new ArrayList<>();

				Registro rs4 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL4587.getValue())
										.getUrl(),
								param4));

				if (rs4 != null) {
					totalFactura = SysmanFunciones.nvlDbl(
							(SysmanFunciones.nvlDbl(rs4.getCampos().get(GeneralParameterEnum.TOTAL.getName()), 0)
									- (SysmanFunciones.nvlDbl(rs4.getCampos().get("DESCUENTO_FACTURA"), 0) / nConceptos)
									- descuento),
							0);

					if (sumProdPorFactura < totalFactura && (totalFactura - sumProdPorFactura) > 1) {

						ParametrosCargos paramCargos = new ParametrosCargos();

						paramCargos.setValor((int) SysmanFunciones.redondear(totalFactura - sumProdPorFactura, 2));

						listaParamCargos.add(paramCargos);

					} else if (sumProdPorFactura > totalFactura && (sumProdPorFactura - totalFactura) > 1) {

						redondeodescuento = SysmanFunciones.redondear(sumProdPorFactura - totalFactura, 2);

						ParametrosDescuentos paramDescuento = new ParametrosDescuentos();

						paramDescuento.setTipo("05");

						paramDescuento.setValor((int) redondeodescuento);

						listaParamDescuentos.add(paramDescuento);

					}
				}
				paramCuerpoFactura.setSubtotalfactura(totalFactura);

				paramCuerpoFactura.setValorfactura(totalFactura);

				paramCuerpoFactura.setNumeroConceptos(nConceptos);

				if (listaParamCargos.size() != 0)
					paramCuerpoFactura.setCargos(listaParamCargos);
				if (listaParamDescuentos.size() != 0)
					paramCuerpoFactura.setDescuentos(listaParamDescuentos);

				List<Registro> listaImpuestosTemp = RegistroConverter
						.toListRegistro(requestManager.getList(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL7452.getValue())
										.getUrl(),
								param4));

				if (!listaImpuestosTemp.isEmpty()) {
					double descuentos = 0.0;
					for(Registro r : listaImpuestosTemp) {
						if (r.getCampos().get("TIPORETENCION").toString().equals("FUE")) {
							paramCuerpoFactura.setReteFuente(
									Double.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString()));
							paramCuerpoFactura.setTotalBaseGravableRete(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALORBASE"), "0").toString()));
						}else if (r.getCampos().get("TIPORETENCION").toString().equals("ICA")) {
							paramCuerpoFactura.setReteIca(
									Double.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString()));
							paramCuerpoFactura.setTotalBaseGravableReteica(Double
									.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALORBASE"), "0").toString()));
						} else {
							descuentos = descuentos + Double.parseDouble(SysmanFunciones.nvl(r.getCampos().get("VALOR"), "0").toString());
						}
					}	
					paramCuerpoFactura.setDescuentoFactura(descuentos);
				}
				// se completa la informacion basica del documento a enviar. -ljdiaz
				rs4 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL1895010.getValue())
										.getUrl(),
								param4));

				if (rs4 != null) {
					paramCuerpoFactura.setSubtotalfactura(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("SUBTOTALFACTURA"), "0").toString()));
					paramCuerpoFactura.setTotalBaseGravableIva(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
					paramCuerpoFactura.setTotalBaseGravableInc(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLE"), "0").toString()));
					paramCuerpoFactura.setTotalBaseImponible(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					paramCuerpoFactura.setValorIvaFactura(
							Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
					paramCuerpoFactura.setDescuentoItems(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTOPARCIAL"), "0").toString()));
//					paramCuerpoFactura.setDescuentoFactura(Double.parseDouble(
//							SysmanFunciones.nvl(rs4.getCampos().get("DESCUENTO_FACTURA"), "0").toString()));
					paramCuerpoFactura
							.setDescripcion(SysmanFunciones.nvl(rs4.getCampos().get("OBSERVACIONES"), "0").toString());
				}
				// fin
				List<ParametrosImpuestos> listaParamImpuestos = new ArrayList<>();
				// impuestos
				// PORCENTAJEIVAX
				Registro rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5768.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos.setTipo("01");
					paramImpuestos.setBase(0);
					paramImpuestos.setPorcentaje(0);
					paramImpuestos.setValor(0);

					// listaParamImpuestos.add(0, paramImpuestos);

				} else {
					paramImpuestos.setTipo("01");
					paramImpuestos.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOIVA"), "0").toString()));

					paramImpuestos.setPorcentaje(
							Double.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("PORCIVA"), "0").toString()));

					paramImpuestos.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALORIMPUESTO"), "0").toString()));

					// listaParamImpuestos.add(0, paramImpuestos);
				}
				if (paramImpuestos.getBase() != 0) {
					listaParamImpuestos.add(0, paramImpuestos);
				}
				// PORCENTAJERETEFUENTEX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5769.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos2 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos2.setTipo("06");
					paramImpuestos2.setBase(0);
					paramImpuestos2.setPorcentaje(0);
					paramImpuestos2.setValor(0);

					// listaParamImpuestos.add(1, paramImpuestos2);

				} else {
					paramImpuestos2.setTipo("06");
					paramImpuestos2.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEFUENTE"), "0").toString()));

					paramImpuestos2.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos2.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALOR_RETEFUENTE"), "0").toString()));

					// listaParamImpuestos.add(1, paramImpuestos2);
				}
				if (paramImpuestos2.getBase() != 0) {
					listaParamImpuestos.add(1, paramImpuestos2);
				}
				// PORCENTAJEICAX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5770.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos3 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos3.setTipo("03");
					paramImpuestos3.setBase(0);
					paramImpuestos3.setPorcentaje(0);
					paramImpuestos3.setValor(0);

					// listaParamImpuestos.add(2, paramImpuestos3);

				} else {
					paramImpuestos3.setTipo("03");
					paramImpuestos3.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOICA"), "0").toString()));

					paramImpuestos3.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos3.setValor(Double
							.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("SUMADEVALOR_ICA"), "0").toString()));

					// listaParamImpuestos.add(2, paramImpuestos3);
				}
				if (paramImpuestos3.getBase() != 0) {
					listaParamImpuestos.add(2, paramImpuestos3);
				}
				// PORCENTAJERETEIVADX
				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5771.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos4 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos4.setTipo("05");
					paramImpuestos4.setBase(0);
					paramImpuestos4.setPorcentaje(0);
					paramImpuestos4.setValor(0);

					// listaParamImpuestos.add(3, paramImpuestos4);

				} else {
					paramImpuestos4.setTipo("05");
					paramImpuestos4.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEIVA"), "0").toString()));

					paramImpuestos4.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos4.setValor(Double
							.parseDouble(SysmanFunciones.nvl(rs5.getCampos().get("SUMADERETEIVA"), "0").toString()));

					// listaParamImpuestos.add(3, paramImpuestos4);
				}
				if (paramImpuestos4.getBase() != 0) {
					listaParamImpuestos.add(3, paramImpuestos4);
				}
				// PORCENTAJERETEICAX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5772.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos5 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos5.setTipo("07");
					paramImpuestos5.setBase(0);
					paramImpuestos5.setPorcentaje(0);
					paramImpuestos5.setValor(0);

					// listaParamImpuestos.add(4, paramImpuestos5);

				} else {
					paramImpuestos5.setTipo("07");
					paramImpuestos5.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTORETEICA"), "0").toString()));

					paramImpuestos5.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos5.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEIMPUESTO_INC"), "0").toString()));

					// listaParamImpuestos.add(4, paramImpuestos5);
				}
				if (paramImpuestos5.getBase() != 0) {
					listaParamImpuestos.add(4, paramImpuestos5);
				}
				// PORCENTAJEINCX

				rs5 = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												ComprobantecntsControladorUrlEnum.URL5773.getValue())
										.getUrl(),
								param4));

				ParametrosImpuestos paramImpuestos6 = new ParametrosImpuestos();
				if (rs5 == null) {

					paramImpuestos6.setTipo("02");
					paramImpuestos6.setBase(0);
					paramImpuestos6.setPorcentaje(0);
					paramImpuestos6.setValor(0);

					// listaParamImpuestos.add(5, paramImpuestos6);

				} else {
					paramImpuestos6.setTipo("02");
					paramImpuestos6.setBase(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEBASEIMPUESTOINC"), "0").toString()));

					paramImpuestos6.setPorcentaje(Double.parseDouble(SysmanFunciones
							.nvl(rs5.getCampos().get(GeneralParameterEnum.PORCENTAJE.getName()), "0").toString()));

					paramImpuestos6.setValor(Double.parseDouble(
							SysmanFunciones.nvl(rs5.getCampos().get("SUMADEIMPUESTO_INC"), "0").toString()));

					// listaParamImpuestos.add(5, paramImpuestos6);
				}
				if (paramImpuestos6.getBase() != 0) {
					listaParamImpuestos.add(5, paramImpuestos6);
				}
				paramCuerpoFactura.setImpuestos(listaParamImpuestos);
			}

			List<ParametroCuerpoEnvioFactura> listaCuerpoFactura = new ArrayList<>();
			listaCuerpoFactura.add(paramCuerpoFactura);

			paramFactura.setFacturas(listaCuerpoFactura);

			APIFrida api2 = new APIFrida();

			Gson gson2 = new Gson();
			String json = gson2.toJson(paramFactura, ParametrosEnvioFactura.class);

			respuesta = api2.postEnvioFactura(url, json);

			RespuestaApi respuestaApi = gson2.fromJson(respuesta, RespuestaApi.class);

			if (respuestaApi.getCodigo() != 0) {
				respuesta = respuestaApi.getMensaje();
			}
			
			return respuesta + "\n" + json;

		} catch (SystemException | IOException | com.sysman.util.SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return respuesta;

	}

	private void validarProducto(String url, String codigoProducto) {

		String respuesta;
		APIFrida api = new APIFrida();
		Gson gson = new Gson();

		try {
			respuesta = api.cargarItem(url, codigoProducto);

			RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);

			if (respuestaApi.getCodigo() != 0) {
				crearProducto(url, codigoProducto);
			}
		} catch (IOException | SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	private void crearProducto(String url, String codigoProducto) {

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(new Date()));

		param.put(GeneralParameterEnum.CODIGO.getName(), codigoProducto);

		try {
			Registro rs = RegistroConverter.toRegistro(requestManager.get(
					UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(ComprobantecntsControladorUrlEnum.URL2486.getValue()).getUrl(),
					param));

			if (rs != null) {
				ParamItem params = new ParamItem();
				ParamItems paramsItems = new ParamItems();

				List<ParamItem> listaParams = new ArrayList<>();

				params.setCodigoProducto(codigoProducto);
				params.setCreatedBy(usuario);
				params.setDescripcionProducto(
						SysmanFunciones.nvl(rs.getCampos().get("DESCRIPCIONPRODUCTO"), "").toString());

				params.setUnidadMedida(SysmanFunciones.nvl(rs.getCampos().get("UNIDADMEDIDA"), "").toString());

				params.setValorItem(SysmanFunciones.nvl(rs.getCampos().get("VALORITEM"), "").toString());

				listaParams.add(params);

				paramsItems.setItems(listaParams);

				Gson gson = new Gson();
				String json = gson.toJson(paramsItems, ParamItems.class);
				APIFrida apiFrida = new APIFrida();

				apiFrida.postItem(url, json);

			}
		} catch (SystemException | IOException | SysmanException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private String crearTecero(String tercero, String url) throws SysmanException {
		String respuesta = null;
		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), tercero);

		try {
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL7354.getValue())
									.getUrl(),
									params));

			ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();

			ParametrosTercero param = new ParametrosTercero();
			
			//7742397_FACGENERAL
		if (rs.getCampos().get(GeneralParameterEnum.PAIS.getName()).equals("CO")) {
			
			param.setCodigomunicipio(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());

			param.setCiudad(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());

			param.setCodigodepartamento(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());

			param.setDepartamento(SysmanFunciones
					.nvl(rs.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());

		} else {
			param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());

			param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());

			param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());

			param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());			

		}
		
			param.setCorreoelectronico(SysmanFunciones.nvl(rs.getCampos().get("CORREOELECTRONICO"), "").toString());

			param.setDireccion(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.DIRECCION.getName()), "").toString());

			param.setDireccionfiscal(SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONFISCAL"), "").toString());

			param.setCodigopostal(SysmanFunciones.nvl(rs.getCampos().get("CODIGOPOSTAL"), "").toString());

			param.setNumerodocumento(SysmanFunciones.nvl(rs.getCampos().get("NUMERODOCUMENTO"), "").toString());

			param.setTelefono(SysmanFunciones.nvl(rs.getCampos().get("TELEFONO"), "").toString());

			param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "")).replace("&", "").replaceAll("\\s+", " ").trim());
			
			param.setTipoidentificacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOIDENTIFICACION"), "").toString());

			param.setDigitoverificacion(SysmanFunciones.nvl(rs.getCampos().get("DIGITOVERIFICACION"), "").toString());

			param.setPais(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());			

			param.setTipoorganizacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOORGANIZACION"), "").toString());

			param.setTiporegimen(SysmanFunciones.nvl(rs.getCampos().get("TIPOREGIMEN"), "").toString());

			// Iteracion sobre obligaciones fisacles del tercero

			Map<String, Object> param2 = new TreeMap<>();

			param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.NIT.getName(), tercero);

			List<Registro> listaObligaciones = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL2054.getValue())
									.getUrl(),
									param2));

			if (!listaObligaciones.isEmpty()) {
				String responsabilidadesFiscales = "";

				for (Registro reg : listaObligaciones) {
					responsabilidadesFiscales = responsabilidadesFiscales + ","
							+ reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
				}

				param.setResponsabilidadesfiscales(
						responsabilidadesFiscales.substring(1, responsabilidadesFiscales.length()));
			}

			paramTercero.setContribuyente(nitCompania);

			List<ParametrosTercero> listaParam = new ArrayList<>();

			listaParam.add(param);

			paramTercero.setTerceros(listaParam);

			Gson gson = new Gson();
			String json = gson.toJson(paramTercero, ParametrosTerceroLote.class);
			APIFrida apiFrida = new APIFrida();

			respuesta = apiFrida.postTercero(url, json);
			
			RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);
			
			Map<String, Object> cuerpoRespuesta = (Map<String, Object>) respuestaApi.getCuerpo();
			Map<String, Object> fallidos = (Map<String, Object>) cuerpoRespuesta.get("fallidos");
			Map<String, Object> exitosos = (Map<String, Object>) cuerpoRespuesta.get("exitosos");
 			if(!fallidos.isEmpty() && exitosos.isEmpty()) {
 				Map<String, Object> mensajeFallidos = (Map<String, Object>) cuerpoRespuesta.get("fallidos");
				respuesta = "\n El tercero: " + tercero + " presenta los siguientes errores en la informacion \n " + mensajeFallidos.get(tercero) + "NO_OK";
			}else {
				respuesta = "\n El tercero "+ tercero +" se creo con exito.";
			}
		} catch (SystemException | IOException | com.sysman.util.SysmanException | RuntimeException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;

	}
	/**
	 * Se crea metodo que permite realizar la actualizacion del tercero
	 * @param tercero
	 * @param url
	 * @return
	 * @throws SysmanException
	 */
	private String actualizaTecero(String tercero, String url) throws SysmanException {
		String respuesta = null;
		Map<String, Object> params = new TreeMap<>();

		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NIT.getName(), tercero);

		try {
			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL7354.getValue())
									.getUrl(),
							params));

			ParametrosTerceroLote paramTercero = new ParametrosTerceroLote();

			ParametrosTercero param = new ParametrosTercero();
			//7742397_FACGENERAL
			if (rs.getCampos().get(GeneralParameterEnum.PAIS.getName()).equals("CO")) {
				
				param.setCodigomunicipio(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGOCIUDAD.getName()), "").toString());

				param.setCiudad(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());

				param.setCodigodepartamento(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.CODIGODEPARTAMENTO.getName()), "").toString());

				param.setDepartamento(SysmanFunciones
						.nvl(rs.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());

			} else {
				param.setCodigomunicipio(GeneralParameterEnum.CODMUNICIPIO.getName());

				param.setCiudad(GeneralParameterEnum.NOMCIUDAD.getName());

				param.setCodigodepartamento(GeneralParameterEnum.CODDEPARTAMENTO.getName());

				param.setDepartamento(GeneralParameterEnum.NOMCIUDAD.getName());			

			}
		
			param.setCorreoelectronico(SysmanFunciones.nvl(rs.getCampos().get("CORREOELECTRONICO"), "").toString());

			param.setDireccion(
					SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.DIRECCION.getName()), "").toString());

			param.setDireccionfiscal(SysmanFunciones.nvl(rs.getCampos().get("DIRECCIONFISCAL"), "").toString());

			param.setCodigopostal(SysmanFunciones.nvl(rs.getCampos().get("CODIGOPOSTAL"), "").toString());

			param.setNumerodocumento(SysmanFunciones.nvl(rs.getCampos().get("NUMERODOCUMENTO"), "").toString());

			param.setTelefono(SysmanFunciones.nvl(rs.getCampos().get("TELEFONO"), "").toString());
			
			param.setNombretercero(SysmanFunciones.toString(SysmanFunciones.nvl(rs.getCampos().get("NOMBRETERCERO"), "")).replace("&", "").replaceAll("\\s+", " ").trim());

			param.setTipoidentificacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOIDENTIFICACION"), "").toString());

			param.setDigitoverificacion(SysmanFunciones.nvl(rs.getCampos().get("DIGITOVERIFICACION"), "").toString());
			
			param.setPais(SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.PAIS.getName()), "").toString());
			
			param.setTipoorganizacion(SysmanFunciones.nvl(rs.getCampos().get("TIPOORGANIZACION"), "").toString());

			param.setTiporegimen(SysmanFunciones.nvl(rs.getCampos().get("TIPOREGIMEN"), "").toString());
			
			param.setContribuyente(nitCompania);

			// Iteracion sobre obligaciones fisacles del tercero

			Map<String, Object> param2 = new TreeMap<>();

			param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.NIT.getName(), tercero);

			List<Registro> listaObligaciones = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											ComprobantecntsControladorUrlEnum.URL2054.getValue())
									.getUrl(),
							param2));

			if (!listaObligaciones.isEmpty()) {
				String responsabilidadesFiscales = "";

				for (Registro reg : listaObligaciones) {
					responsabilidadesFiscales = responsabilidadesFiscales + ","
							+ reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
				}

				param.setResponsabilidadesfiscales(
						responsabilidadesFiscales.substring(1, responsabilidadesFiscales.length()));
			}

			paramTercero.setContribuyente(nitCompania);

			List<ParametrosTercero> listaParam = new ArrayList<>();

			listaParam.add(param);

			paramTercero.setTerceros(listaParam);

			Gson gson = new Gson();
			String json = gson.toJson(param, ParametrosTercero.class);
			APIFrida apiFrida = new APIFrida();
			

			respuesta = apiFrida.putTercero(url, json);
			if (respuesta != null && respuesta.trim().startsWith("{")) {
			    RespuestaApi respuestaApi = gson.fromJson(respuesta, RespuestaApi.class);
			    //CC2210 mrosero
			    if ("OK".equalsIgnoreCase(respuestaApi.getMensaje())) {
			        respuesta = "Tercero " + tercero + " fue actualizado correctamente.";
			    } else {
			        respuesta = "Tercero " + tercero + " fue actualizado, Nota: el servicio no devolvió una confirmación clara. Recomendamos verificar los datos.";
			    }

			} else {
			    // Es una cadena simple (como "OK" o un error en texto plano)
			    String mensajePlano = respuesta.trim().replace("\"", "");
			    
			    if ("OK".equalsIgnoreCase(mensajePlano)) {
			        respuesta = "Tercero " + tercero + " fue actualizado correctamente.";
			    } else {
			        respuesta = "Tercero " + tercero + " fue actualizado, Nota: el sistema no devolvió una confirmación clara. Recomendamos verificar los datos.";
			    }
			}


		} catch (SystemException | RuntimeException | IOException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return respuesta;

	}
	
	/**
	 * Envío masivo de notas de ajuste a Invoway, disparado desde el botón
	 * "Enviar Notas" en la interfaz.
	 *
	 * <p>Funciona igual que {@link #oprimirEnviarDocSoporte} pero consulta
	 * los comprobantes por una URL diferente y llama a {@link #envioInvoway}
	 * con {@code esNota = true}, lo que activa la referencia al documento
	 * soporte afectado y establece el motivo de rectificación en {@code "5"}.</p>
	 *
	 * <p>Genera el archivo {@code LogEnviarNotas.txt} al finalizar.</p>
	 */
	public void oprimirEnviarNotaDocSoporte() {

		archivoDescarga = null;
		String log = "";

		try {

			String manejaInvoway = ejbSysmanUtil.consultarParametro(compania, "MANEJA FACTURACION ELECTRONICA EXTERNA",
					"69", new Date(), false);
			if (manejaInvoway.equals("SI")) {
				url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO SOAP", "69", new Date(), false);

				if (SysmanFunciones.validarVariableVacio(url)) {
					JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO SOAP");
				} else {
					try {

						log = "|---------------     ENVIO NOTAS INVOWAY      ---------------|";
						Map<String, Object> parametros = new TreeMap<>();

						SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy"); 
						parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
						parametros.put("FECHAINI", formatoFecha.format(fechaInicio));
						parametros.put("FECHAFIN", formatoFecha.format(fechaFin));

						List<Registro> listaFactSysman = RegistroConverter.toListRegistro(requestManager.getList(
																UrlServiceUtil.getInstance()
																		.getUrlServiceByUrlByEnumID(
																				FrmdocsoploteControladorUrlEnum.URL1895029.getValue())
																		.getUrl(),
																parametros));
						
						if (!listaFactSysman.isEmpty()) {
							for (Registro reg : listaFactSysman) {
								log = log  +  "\n" + envioInvoway(SysmanFunciones.toString(
			        					SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.NUMEROFACTURA.getName()),"1")),
			        					SysmanFunciones.toString(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.TIPOCOBRO.getName()),"")),
			        					SysmanFunciones.toString(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CONSECUTIVODIAN.getName()),"")),true);
	    	                }
						}
						
						ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
						archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarNotas.txt");

						JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

					} catch (SystemException e1) {
						logger.error(e1.getMessage(), e1);
						JsfUtil.agregarMensajeError(e1.getMessage());
					}
				}

			} else {
				
			
			log = "|---------------     ENVIO NOTAS FRIDA      ---------------|";

			url = ejbSysmanUtil.consultarParametro(compania, "URL SERVICIO REST", "69", new Date(), false);
			
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy");
			
			Calendar c = Calendar.getInstance();
			c.setTime(fechaFin);
			c.add(Calendar.DATE, 1);
			fechaFin = c.getTime();

			if (SysmanFunciones.validarVariableVacio(url)) {
				JsfUtil.agregarMensajeAlerta("Asegurese de configurar el parametro URL SERVICIO REST");
			} else {
				String respuesta;
				APIFrida api = new APIFrida();

				respuesta = api.cargarFormatoConsultarReporte(url, nitCompania, "10", "", "181",
						SysmanFunciones.convertirAFechaCadena(fechaInicio, "yyyy/MM/dd"),
						SysmanFunciones.convertirAFechaCadena(fechaFin, "yyyy/MM/dd"), "0", "0", "0","0","1");

				Gson gson = new Gson();
				RespuestaConsultarReporte respuestaApi = gson.fromJson(respuesta, RespuestaConsultarReporte.class);

				String comprobantes = "";

				for (RespuestaNotasReporte respuestaNotasReporte : respuestaApi.getCuerpo().getNotas()) {

					comprobantes = comprobantes + "," + respuestaNotasReporte.getNumFormato();

				}

				Map<String, Object> param = new TreeMap<>();

				comprobantes = SysmanFunciones.nvlStr(comprobantes, SysmanConstantes.CONS_FUENTE);

				param.put("FECHAINICIAL", formatFecha.format(fechaInicio));
				param.put("FECHAFINAL", formatFecha.format(fechaFin));
				// CONSULTA LAS NOTAS TIPO CNA QUE TENGAS EL TIPO DE NOTA DNS
				List<Registro> listaComprobantes = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmdocsoploteControladorUrlEnum.URL8921.getValue())
												.getUrl(),
										param));

				if (!listaComprobantes.isEmpty()) {

					for (Registro reg : listaComprobantes) {
						//MOD JC CC 2459 tipo de comprobante 
						log = log + "\n"
								+ exportarNotas(
										reg.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString(),
										reg.getCampos().get("NUMERO_FACTURA").toString(),
										reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString(),
										reg.getCampos().get("CODIGO_NOTA").toString(),
										reg.getCampos().get("PREFIJO").toString(),
										reg.getCampos().get("TIPO_MEDIOPAGO").toString(),
										reg.getCampos().get("TIPO_PAGO").toString(),
										reg.getCampos().get("TIPO_MONEDA").toString(),
										reg.getCampos().get("TIPOFACDIAN").toString(),
										reg.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()).toString(),
										reg.getCampos().get(GeneralParameterEnum.VALOR.getName()).toString(), url,
										reg.getCampos().get("TIPO_COMPROBANTE").toString());
					}

				}

			}
			c.add(Calendar.DATE, -1);
			fechaFin = c.getTime();

			ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(log);
			archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto, "LogEnviarNotas.txt");

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
			}

		} catch (SystemException | IOException | SysmanException | ParseException | JRException e) {

			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	private String exportarNotas(String comprobante, String numeroFactura, String fecha, String codigoProducto,
			String prefijodse, String tipoMedioPago, String tipoPago, String tipoMoneda, String tipoFacDIAN,
			String descripcion, String valor, String url, String tipo_comprobante) {

		String log = null;
		int idFactura;
		String tipoNNota = "06";// tipo formato para nota
		String tipoFormato = "05";// tipo formato para doc soporte
		ParametrosEnvioFactura paramFactura = new ParametrosEnvioFactura();

		Map<String, Object> param = new TreeMap<>();

		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("NUMEROFACTURA", comprobante);
		param.put("TIPOCOMPROBANTE", tipo_comprobante); //JM CC 2459

		try {
			// informacion de la nota de lo valores de la nota
			Registro rs = RegistroConverter
					.toRegistro(
							requestManager.get(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													FrmdocsoploteControladorUrlEnum.URL2154.getValue())
											.getUrl(),
									param));

			if (rs != null) {

				Map<String, Object> param2 = new TreeMap<>();
				param2.put(GeneralParameterEnum.CODIGO.getName(),
						rs.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()));

				Registro rs2 = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmdocsoploteControladorUrlEnum.URL9512.getValue())
												.getUrl(),
										param2));

				paramFactura.setCreatedBy(rs2.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());

				paramFactura.setNumerocontribuyente(nitCompania);

				String respuesta = null;
				APIFrida api = new APIFrida();

				respuesta = api.cargarEnvioFacatura(nitCompania, tipoFormato, numeroFactura, prefijodse, url);

				Gson gson = new Gson();
				ParametrosEnvioFacturaFiltros respuestaApi = gson.fromJson(respuesta,
						ParametrosEnvioFacturaFiltros.class);

				idFactura = respuestaApi.getCuerpo().getId();

				ParametroCuerpoEnvioFactura paramCuerpo = new ParametroCuerpoEnvioFactura();

				paramCuerpo.setTipoDeFactura(tipoNNota);
				paramCuerpo.setIdFactura(idFactura);
				paramCuerpo.setNumTercero(rs.getCampos().get("NUMTERCERO").toString());
				paramCuerpo.setFechafactura(SysmanFunciones.nvl(rs.getCampos().get("FECHAFACTURA"), "").toString());
				paramCuerpo.setFechaVencimiento(
						SysmanFunciones.nvl(rs.getCampos().get("FECHA_VENCIMIENTO"), "").toString());
				paramCuerpo.setNumerofactura(Integer.parseInt(comprobante));
				paramCuerpo.setTelefonoCliente(rs.getCampos().get("TELEFONOS").toString());
				paramCuerpo.setTipoPago(tipoPago);
				paramCuerpo.setMedioPago(tipoMedioPago);
				paramCuerpo.setTipoMoneda(tipoMoneda);
				paramCuerpo.setPrefijo("NA");
				paramCuerpo.setTipoOperacion(tipoFacDIAN);
				paramCuerpo.setObservacionesFactura(SysmanFunciones
						.nvl(rs.getCampos().get(GeneralParameterEnum.OBSERVACIONES.getName()), "").toString());
				paramCuerpo.setReviso(SysmanFunciones.nvl(
						ejbSysmanUtil.consultarParametro(compania, "SF NOMBRE REVISO EN FACTURA",
								SessionUtil.getModulo(), new Date(), false),
						rs.getCampos().get(GeneralParameterEnum.CREATED_BY.getName()).toString()).toString());

				// paramCuerpo.setcorreoEntrante("");
				paramCuerpo
						.setDatosSoftware(
								SysmanFunciones
										.nvl(ejbSysmanUtil.consultarParametro(compania,
												"DATOS SOFTWARE FACTURA ELECTRONICA", "69", new Date(), false), "")
										.toString());
				ParametrosItems paramItems = new ParametrosItems();

				paramItems.setCodigoproducto(codigoProducto);

				paramItems.setCantidad(
						Double.parseDouble(rs.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString()));

				paramItems.setDescripcionproducto(descripcion);

				paramItems.setValorunitario(Double.parseDouble(valor));

				paramItems.setTipoDescuento("05");

				paramItems.setDescuentoItem("0");

				paramItems.setTotalitem(Double.parseDouble(valor));

				String fechaFExpedicion;
				Date fechaIExpedicionC;
				Date fechaFExpedicionC;
				Map<String, Object> param4 = new TreeMap<>();

				fechaFExpedicion = rs.getCampos().get("FECHAFACTURA").toString();

				fechaIExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
				Calendar c = Calendar.getInstance();
				c.setTime(fechaIExpedicionC);
				c.add(Calendar.DATE, -1);
				fechaIExpedicionC = c.getTime();

				fechaFExpedicionC = SysmanFunciones.convertirAFecha(fechaFExpedicion, "yyyy-MM-dd");
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fechaFExpedicionC);
				fechaFExpedicionC = c1.getTime();

				param4.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param4.put("FECHAINICIAL", fechaIExpedicionC);
				param4.put("FECHAFINAL", fechaFExpedicionC);
				param4.put("NUMEROFACTURA", comprobante);
				param4.put("TIPONOTA", (rs.getCampos().get("TIPO").toString()));

				// informacion de los valores desde la NOTA
				Registro rs4 = RegistroConverter
						.toRegistro(
								requestManager.get(
										UrlServiceUtil.getInstance()
												.getUrlServiceByUrlByEnumID(
														FrmdocsoploteControladorUrlEnum.URL7452.getValue())
												.getUrl(),
										param4));

				List<ParametrosItemsImpuestos> listaParamItemImpuestos = new ArrayList<>();
				List<ParametrosImpuestos> listaParamImpuestosGnrl = new ArrayList<>();
				ParametrosImpuestos paramImpuesGnrl = new ParametrosImpuestos();
				ParametrosItems paramItemsSinImpesto = new ParametrosItems();
				// se traslada su declaracion
				List<ParametrosItems> listaParamItems = new ArrayList<>();
				// logica en el caso que el impuesto sea IVA.
				if (!rs4.getCampos().get("BASEGRAVABLEIVA").toString().equals("0")
						&& !rs4.getCampos().get("IVA").toString().equals("0")) {
					// se settean dentro del array impuestos en el detalle
					ParametrosItemsImpuestos paramItemImpuestos = new ParametrosItemsImpuestos();
					paramItemImpuestos.setTipo("01");
					paramItemImpuestos.setBase(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					paramItemImpuestos.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));
					paramItemImpuestos.setValor(
							Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
					listaParamItemImpuestos.add(paramItemImpuestos);
					paramCuerpo.setPorcentajeIva(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));
					// se settean en el general del detalle.
					paramItems.setValorunitario(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					Double totalItemDetalle = Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString())
							+ Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString());
					paramItems.setTotalitem(totalItemDetalle);
					// se settean los impuestos al array de impuestos de la factura en general

					paramImpuesGnrl.setBase(Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));
					paramImpuesGnrl.setTipo("01");
					paramImpuesGnrl.setValor(
							Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));
					paramImpuesGnrl.setPorcentaje(Double.parseDouble(SysmanFunciones.nvl("19", 0).toString()));
					listaParamImpuestosGnrl.add(paramImpuesGnrl);
					paramCuerpo.setImpuestos(listaParamImpuestosGnrl);

					double totalItemAjuste = Double
							.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("TOTAL"), "0").toString())
							- (paramImpuesGnrl.getBase() + paramImpuesGnrl.getValor());
					if (totalItemAjuste > 0) {
						// se foramtea el valor para que traiga solo dos decimales
						DecimalFormat df = new DecimalFormat("#");

						paramItemsSinImpesto.setCodigoproducto(codigoProducto);

						paramItemsSinImpesto.setCantidad(1);

						paramItemsSinImpesto.setDescripcionproducto(descripcion);

						paramItemsSinImpesto.setValorunitario(Double.parseDouble(df.format(totalItemAjuste)));

						paramItemsSinImpesto.setTipoDescuento("05");

						paramItemsSinImpesto.setDescuentoItem("0");

						paramItemsSinImpesto.setTotalitem(Double.parseDouble(df.format(totalItemAjuste)));

						List<ParametrosItemsImpuestos> listaTempSinImpuesto = new ArrayList<>();
						paramItemsSinImpesto.setImpuestos(listaTempSinImpuesto);

						listaParamItems.add(paramItemsSinImpesto);

					}
				}

				ParametrosItemsImpuestos paramItemImpuestos2 = new ParametrosItemsImpuestos();
				paramItemImpuestos2.setTipo("06");

				paramItemImpuestos2.setBase(0);

				paramItemImpuestos2.setPorcentaje(0);

				paramItemImpuestos2.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos3 = new ParametrosItemsImpuestos();
				paramItemImpuestos3.setTipo("03");

				paramItemImpuestos3.setBase(0);

				paramItemImpuestos3.setPorcentaje(0);

				paramItemImpuestos3.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos4 = new ParametrosItemsImpuestos();
				paramItemImpuestos4.setTipo("05");

				paramItemImpuestos4.setBase(0);

				paramItemImpuestos4.setPorcentaje(0);

				paramItemImpuestos4.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos5 = new ParametrosItemsImpuestos();
				paramItemImpuestos5.setTipo("07");

				paramItemImpuestos5.setBase(0);

				paramItemImpuestos5.setPorcentaje(0);

				paramItemImpuestos5.setValor(0);

				ParametrosItemsImpuestos paramItemImpuestos6 = new ParametrosItemsImpuestos();
				paramItemImpuestos6.setTipo("02");

				paramItemImpuestos6.setBase(0);

				paramItemImpuestos6.setPorcentaje(0);

				paramItemImpuestos6.setValor(0);

				if (paramItemImpuestos2.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos2);
				}
				if (paramItemImpuestos3.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos3);
				}
				if (paramItemImpuestos4.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos4);
				}
				if (paramItemImpuestos5.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos5);
				}
				if (paramItemImpuestos6.getBase() != 0.0) {
					listaParamItemImpuestos.add(paramItemImpuestos6);
				}

				paramItems.setImpuestos(listaParamItemImpuestos);

				listaParamItems.add(paramItems);

				paramCuerpo.setItems(listaParamItems);

				paramCuerpo.setSubtotalfactura(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("SUBTOTALFACTURA"), "0").toString()));

				paramCuerpo.setValorfactura(
						Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("TOTAL"), "0").toString()));
				paramCuerpo.setReteFuente(0);
				paramCuerpo.setReteIva(0);
				paramCuerpo.setTotalBaseGravableIca(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEICA"), "0").toString()));
				paramCuerpo.setTotalBaseGravableInc(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEINC"), "0").toString()));
				paramCuerpo.setTotalBaseGravableIva(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLEIVA"), "0").toString()));
				paramCuerpo.setTotalBaseGravableRete(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETE"), "0").toString()));
				paramCuerpo.setTotalBaseGravableIca(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEICA"), "0").toString()));
				paramCuerpo.setTotalBaseGravableReteiva(Double
						.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEGRAVABLERETEIVA"), "0").toString()));
				paramCuerpo.setTotalBaseImponible(
						Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("BASEIMPONIBLE"), "0").toString()));

				paramCuerpo.setValorIvaFactura(
						Double.parseDouble(SysmanFunciones.nvl(rs4.getCampos().get("IVA"), "0").toString()));

				paramCuerpo.setValorIcaFactura(0);
				paramCuerpo.setNumeroConceptos(listaParamItems.size());
				paramCuerpo.setReteIca(0);
				paramCuerpo.setValorIncFactura(0);
				paramCuerpo.setDescuentoItems(0);
				paramCuerpo.setDescuentoFactura(0);
				paramCuerpo.setDescripcion(descripcion);

				List<ParametroCuerpoEnvioFactura> listaParamCuerpo = new ArrayList<>();

				listaParamCuerpo.add(paramCuerpo);

				paramFactura.setFacturas(listaParamCuerpo);

				APIFrida api2 = new APIFrida();

				Gson gson2 = new Gson();

				String json = gson2.toJson(paramFactura, ParametrosEnvioFactura.class);
				if (json.toString().contains(FrmdocsoploteControladorEnum.IMPUESTOIVA.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.IMPUESTOIVA.getValue(), " ");
				}
				if (json.toString().contains(FrmdocsoploteControladorEnum.VALORIMPUESTOIVA.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.VALORIMPUESTOIVA.getValue(), " ");
				}
				if (json.toString().contains(FrmdocsoploteControladorEnum.IMPUESTOICA.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.IMPUESTOICA.getValue(), " ");
				}
				if (json.toString().contains(FrmdocsoploteControladorEnum.VALORIMPUESTOICA.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.VALORIMPUESTOICA.getValue(), " ");
				}
				if (json.toString().contains(FrmdocsoploteControladorEnum.IMPUESTOIMPOCONSUMO.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.IMPUESTOIMPOCONSUMO.getValue(), " ");
				}
				if (json.toString().contains(FrmdocsoploteControladorEnum.VALORIMPUESTOIMPOCONSUMO.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.VALORIMPUESTOIMPOCONSUMO.getValue(),
							" ");
				}
				if (json.toString().contains(FrmdocsoploteControladorEnum.BASEGRAVAVLEDETALLE.getValue())) {
					json = json.toString().replace(FrmdocsoploteControladorEnum.BASEGRAVAVLEDETALLE.getValue(), " ");
				}

				log = api2.postEnvioFactura(url, json);

				RespuestaApi respuestaApi2 = gson2.fromJson(respuesta, RespuestaApi.class);

				if (respuestaApi2.getCodigo() != 0) {
					log = respuestaApi2.getMensaje().toString();
					
				}else {
					log= log + " LA NOTA DE AJUSTE DE DOC SOPORTE CON NUMERO "+ comprobante +" SE ENVIO CON EXITO " + json + "\n";
				}
				
				return log;

			}

		} catch (SystemException | IOException | SysmanException | ParseException e) {
			log = log + " " + e.getMessage();
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		return log;
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
	 * Retorna la variable fechaInicio
	 * 
	 * @return fechaInicio
	 */
	public Date getFechaInicio() {
		return fechaInicio;
	}

	/**
	 * Asigna la variable fechaInicio
	 * 
	 * @param fechaInicio Variable a asignar en fechaInicio
	 */
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	/**
	 * Retorna la variable fechaFin
	 * 
	 * @return fechaFin
	 */
	public Date getFechaFin() {
		return fechaFin;
	}

	/**
	 * Asigna la variable fechaFin
	 * 
	 * @param fechaFin Variable a asignar en fechaFin
	 */
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
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
//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
//</SET_GET_LISTAS_COMBO_GRANDE>
}
