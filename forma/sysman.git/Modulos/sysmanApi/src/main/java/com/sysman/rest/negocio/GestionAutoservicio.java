package com.sysman.rest.negocio;
/*-
 * GestionAutoservicio.java
 *
 * 1.0
 *
 * 29/05/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.primefaces.model.StreamedContent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.sysman.contabilidad.reportes.ComprobantesContPresReporteador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.CompaniaDao;
import com.sysman.dao.Registro;
import com.sysman.dao.UsuarioDao;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralRemote;
import com.sysman.facturaciongeneral.ejb.impl.EjbFacturacionGeneral;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Compania;
import com.sysman.logica.DatosSesion;
import com.sysman.logica.Usuario;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.nomina.ejb.impl.EjbNominaCeroGeneral;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plantillas.UtilitarioPlantillas;
import com.sysman.recursos.ejb.EjbOrganigramaRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbOrganigrama;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.rest.enums.InvocadorClaseEnum;
import com.sysman.rest.excepcion.NegocioExcepcion;
import com.sysman.rest.logica.Comprobante;
import com.sysman.rest.logica.ParametrosComprobanteCnt;
import com.sysman.rest.logica.ParametrosDetalleComprobanteCnt;
import com.sysman.rest.logica.ParametrosEntradaDatosFamiliares;
import com.sysman.rest.logica.ParametrosEntradaDatosPersonales;
import com.sysman.rest.logica.ParametrosEntradaInventario;
import com.sysman.rest.logica.ParametrosEntradaSolicitudes;
import com.sysman.rest.logica.ParametrosExcelAfectaciones;
import com.sysman.rest.logica.ParametrosInformeFactura;
import com.sysman.rest.logica.ParametrosInformeFacturasPorCobrar;
import com.sysman.rest.logica.ParametrosLiquidacionFactura;
import com.sysman.rest.logica.ParametrosPagarFacturaGeneral;
import com.sysman.rest.logica.ParametrosRecuadoCausacion;
import com.sysman.rest.logica.ParametrosRegRecuadoCausacion;
import com.sysman.rest.logica.RespuestaConsultarFacturaGeneral;
import com.sysman.rest.logica.RespuestaDatosGeneral;
import com.sysman.rest.negocio.enums.GeneraArchivoEnum;
import com.sysman.rest.negocio.enums.GeneraArchivoUrlEnum;
import com.sysman.rest.negocio.enums.GestionAutoservicioEnum;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.reporte.PrepararReporte;
import com.sysman.util.reporte.RetornoReporte;
import com.sysman.util.rest.ConvertirPdf;
import com.sysman.util.rest.Parametro;
import com.sysman.util.rest.Respuesta;
import com.sysman.util.rest.RespuestaApi;
import com.sysman.util.rest.RespuestaApiPagArqC;
import com.sysman.util.rest.RespuestaCargarComprobanteCnt;
import com.sysman.util.rest.RespuestaCargarDetalleComprobanteCnt;
import com.sysman.util.rest.RespuestaCargarEscolaridad;
import com.sysman.util.rest.RespuestaCargarEstadoPeriodo;
import com.sysman.util.rest.RespuestaCargarGeneros;
import com.sysman.util.rest.RespuestaCargarOcupacion;
import com.sysman.util.rest.RespuestaCargarPeriodosCerrados;
import com.sysman.util.rest.RespuestaCargarPlanContable;
import com.sysman.util.rest.RespuestaCargarRangoEdad;
import com.sysman.util.rest.RespuestaCargarTercero;
import com.sysman.util.rest.RespuestaCargarTerceroPagCombo;
import com.sysman.util.rest.RespuestaCargarTipoPersona;
import com.sysman.util.rest.RespuestaCargarTipoPobl;
import com.sysman.util.rest.RespuestaCargarTipoTramite;
import com.sysman.util.rest.RespuestaCargarVulnerabilidad;
import com.sysman.util.rest.RespuestaDatosDependencias;
import com.sysman.util.rest.RespuestaListaPlanContableBasico;
import com.sysman.util.rest.RespuestaListaSaldoAuxContable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.sf.jasperreports.engine.JRException;


/**
 * Patr&oacute;n comando g&eacute;neral. Recibidor de Servicios para el comando
 * ejecutado
 *
 * @version 1.0, 29/05/2018
 * @author jgomez
 * @author jrodrigueza
 * 
 * @version 2.0, 29/12/2021
 * @author gfigueredo Se crea la funcion
 *         {@link #generaReporteAfectaciones(String, int, String, String)}, para
 *         exportar archivo excel con los datos de las afectaciones, una vez, el
 *         servicio web finalice el proceso.
 * @see #cargarRecaudoCausacion(ParametrosRecuadoCausacion)
 * @see #generaReporteAfectaciones(String, int, String, String)
 * @see #inicializarExcel(List)
 * @see #escribirHeaderAfectaciones()
 * @see #crearCelda(Row, int, Object, CellStyle)
 * @see #escribirDatos()
 * @see #exportarExcel(Registro)
 */
public class GestionAutoservicio {
	/**
	 * Identifica el contexto recibido para las solicitudes de autoservicio.
	 */
	private ParametrosEntradaSolicitudes parametrosSolicitudes;
	/**
	 * Identifica el contexto recibido para la solicitud de actualizaci&oacute;n de
	 * datos personales.
	 */
	private ParametrosEntradaDatosPersonales parametrosDatosPersonales;
	/**
	 * Identifica el contexto recibido para la solicitud de actualizai&oacute;n de
	 * datos de familiares.
	 */
	private ParametrosEntradaDatosFamiliares parametrosDatosFamiliares;

	/**
	 * Contexto de la insercion de comprobantesCnt
	 */
	private ParametrosComprobanteCnt parametrosComprobanteCnt;

	/**
	 * Contexto de la insercion de detalle comprobantesCnt
	 */
	private ParametrosDetalleComprobanteCnt parametrosDetalleComprobanteCnt;

	/**
	 * Sucursal a la que pertenece el empleado como tercero
	 */
	private String sucursal = SysmanConstantes.CONS_SUCURSAL;
	/**
	 * cargo del empleado
	 */
	private String cargo = null;
	/**
	 * sucursal del jefe directo del empleado
	 */
	private String sucursalJefeDirecto = SysmanConstantes.CONS_SUCURSAL;
	/**
	 * Cedula del jefe directo del empleado
	 */
	private String jefeDirecto = SysmanConstantes.CONS_TERCERO;
	/**
	 * Codigo interno del empelado
	 */
	private int idEmpleado = -1;
	/**
	 * Identificador del modulo de nomina
	 */
	private String moduloNomina = "6";

	/**
	 * Identificador del modulo de nomina
	 */
	private String moduloAlmacen = "10";
	
	/**
	 * Identificador del modulo de nomina
	 */
	private String moduloSF = "69";
	
	/**
	 * Constante del usuario que realiza la petici&oacute;n del autoservicio
	 */
	private static final String USUARIO = "AUTOSERVICIO";
	/**
	 * Constante de la calve del usuario que realiza la petici&oacute;n del
	 * autoservicio
	 */
	private static final String CLAVE = "SYSAUTOSERVICIOMAN";
	/**
	 * Constante mensaje enviado al certificado de ingresos y retenciones
	 * autoservicio
	 */
	private static final String MENSAJECERTIFICADORETENCION = "GENERADO VIA WEB";
	/**
	 * Extensi&oacute;n asociada a documentos PDF.
	 */
	public static final CharSequence EXTENSION_PDF = "pdf";
	/**
	 * Permite utilizar las constantes de idioma
	 */
	private ResourceBundle idioma;
	/**
	 * Gestiona las peticiones al API
	 */
	private RequestManager requestManager;
	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GestionAutoservicio.class);
	/**
	 * C&oacute;digo que identifica las plantillas asociadas a Autoservicio.
	 */
	private static final Object TIPO_AUTOSERVICIO = "41";
	/**
	 * Define el tipo de permiso predeterminado para las consultas de autoservicio.
	 */
	private static final int TIPO_PERMISO_CONSULTA = 1;
	/**
	 * Identificador del usuario que realizada una operaci&oacute;n determinada
	 * desde la API de autoservicio.
	 */
	private static final String USUARIO_API = "API-USER\\";
	/**
	 * EJB para consumir procedimientos/funciones del paquete de n&oacute;mina cero.
	 */
	@EJB
	private EjbNominaCeroGeneralRemote ejbNominaCeroGeneral = new EjbNominaCeroGeneral();

	@EJB
	private EjbOrganigramaRemote ejbOrganigrama = new EjbOrganigrama();

	@EJB
	private EjbFacturacionGeneralRemote ejbFacturacionGeneralCero = new EjbFacturacionGeneral();	

	/**
	 * Indica la salida del informe para ver si se debe convertir a pdf o no
	 */
	private String formatoFinal;
	/**
	 * consecutivo generado al crear el documento
	 */
	private long consecutivo;
	/**
	 * identifica la extension del documento generado
	 */
	private String extension;

	/**
	 * conjunto de datos relacionados con la sesi&oacute;n
	 */
	private DatosSesion datosSesion;
	/**
	 * Constante para la etiquetea del tipo de comprobante enviado
	 */
	private static final String TIPOCPTE = "tipoCpte";
	/**
	 * Constante para la etiquetea del n&uacute; de comprobante enviado
	 */
	private static final String NUMEROCPTE = "numeroCpte";
	/**
	 * Constante que representa el modulo de contabilidad
	 */
	private static final String MODULOCONTABILIDAD = "1";

	private static final String SERVICIO_API = "1710001";
	private static final int DEFAULT_SIZE = 1000;
	private static final int MAX_SIZE = 2000;

	/**
	 * constante que identifica el servicio que consulta los datos del tipo de
	 * comprobante
	 */
	private static final String URL15031 = "15031";
	
	private static final String CERTIFICADO_CLOUDMERSIVE = "GlobalSign.crt";

	/**
	 * Implementacion del EJB de SysmanUtil para acceder a funciones y/o
	 * procedimientos definidos en el paquete PCK_SYSMAN_UTL
	 */
	@EJB
	private static EjbSysmanUtilRemote ejbSysmanUtil = new EjbSysmanUtil();

	@EJB
	private static ConvertirPdf convertir = new ConvertirPdf();

	public GestionAutoservicio() {
		requestManager = new RequestManager();
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
	}

	/**
	 * Variable usada para creación de archivo excel
	 */
	private XSSFWorkbook workbook;
	/**
	 * Variables usada para creación de hoja de excel
	 */
	private XSSFSheet sheet;
	/**
	 * Lista de objetos para almacenar los datos de la BD
	 */
	private List<Object> excel;

	private StreamedContent archivoDescarga;
	private ReportesBean instance;

	/**
	 * Retorna el String que representa el reporte generado
	 *
	 * @param contexto parametros de entrada
	 * @return objeto respuesta con el consecutivo, formato y ruta abstracta en
	 *         donde queda alojado el archivo.
	 * @throws NegocioExcepcion
	 */
	public Respuesta generarSolicitud(ParametrosEntradaSolicitudes contexto) throws NegocioExcepcion {
		byte[] base64 = obtenerDocumentoFinal(contexto);
		String rutaRelativa = crearArchivoTemporal(base64, extension);

		if (base64 != null) {
			Respuesta respuesta = new Respuesta();
			respuesta.setConsecutivo(consecutivo);
			respuesta.setFormato(formatoFinal);
			respuesta.setArchivo(rutaRelativa);
			return respuesta;
		} else {
			return new Respuesta();
		}
	}

	public RespuestaApi generarSolicitudPropia(ParametrosEntradaSolicitudes contexto) throws NegocioExcepcion, SysmanException {
		byte[] base64 = obtenerDocumentoFinal(contexto);
		byte[] baseFinal = null;
		String rutaRelativa = crearArchivoTemporal(base64, extension);
		if (base64 != null) {
			if (GeneraArchivoEnum.WORD.getValue().equals(formatoFinal)) {
                archivoDescarga = null;
                File plantilla = new File(rutaRelativa);

                try {
                	
                	String rutaCertificado = (String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(datosSesion.getCompania(),
        					GestionAutoservicioEnum.GENERAR_RUTA_CLOUDMERSIVE.getValue(), "87",
        					new Date(), false), "/opt/sysman/CERTIFICADO/CLOUDMERSIVE/");
                	rutaCertificado = rutaCertificado+CERTIFICADO_CLOUDMERSIVE;
                	baseFinal =  convertir.convertirAPdfDocumento(rutaRelativa, true, rutaCertificado);
                	if (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(parametrosSolicitudes.getCompania(),
        					"MANEJA CONTRASENA EN INFORMES DE AUTOSERVICIO","87",new Date(),false),"NO").equals("SI")) {
                		baseFinal = protegerPdf(baseFinal,parametrosSolicitudes.getCedula());
                	}
                	plantilla.delete();

                }
                catch (com.sysman.util.SysmanException | SystemException | DocumentException | IOException e) {
                        NegocioExcepcion error = new NegocioExcepcion(idioma
                                        .getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_NOREPORTE
                                                        .getValue()));
                        error.initCause(new Exception(idioma.getString(
                                        GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONFI
                                        .getValue())));
                        throw error;
                }

			} else {
				baseFinal = base64;
				File plantilla2 = new File(rutaRelativa);
				plantilla2.delete();

			}

			RespuestaApi respuestas = new RespuestaApi();
			respuestas.setCodigo(0); 
			respuestas.setMensaje("OK");
			respuestas.setCuerpo(baseFinal); 
			return respuestas;

		} else {
			return new RespuestaApi();
		}
	}

	
	private byte[] protegerPdf(byte[] pdfOriginal,String password) 
			throws DocumentException, IOException {
		PdfReader reader = null;
		ByteArrayOutputStream out = null;
		PdfStamper pdfPss = null;	    

	    try {
	    	reader = new PdfReader(pdfOriginal);
	    	out = new ByteArrayOutputStream();
	    	pdfPss = new PdfStamper(reader,out);	    		    	

	    	pdfPss.setEncryption(password.getBytes(),"SYSMANADMIN".getBytes(),PdfWriter.ALLOW_PRINTING,PdfWriter.ENCRYPTION_AES_128);
			
	    	pdfPss.close();
	    	pdfPss = null;			
			return out.toByteArray();
	    } finally {	    	
	    	if (pdfPss != null) {
	        	pdfPss.close();
	        }	
	    	if (reader != null) {
	            reader.close();
	        }
	        if (out != null) {
	            out.close();
	        }	                
	    }
	}

	
	
	

	/**
	 *
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	private byte[] obtenerDocumentoFinal(ParametrosEntradaSolicitudes contexto) throws NegocioExcepcion {
		parametrosSolicitudes = contexto;
		Registro claseSolicitud;

		claseSolicitud = cargarDatosSolicitud();

		String reporte = SysmanFunciones.toString(claseSolicitud.getCampos().get(GeneraArchivoEnum.REPORTE.getValue()));
		String plantilla = SysmanFunciones
				.toString(claseSolicitud.getCampos().get(GeneraArchivoEnum.PLANTILLA.getValue()));

		if (SysmanFunciones.validarVariableVacio(reporte) && SysmanFunciones.validarVariableVacio(plantilla)) {

			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_NOREPORTE.getValue()));
			error.initCause(
					new Exception(idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONFI.getValue())));
			throw error;
		}
		cargarDatosDeEmpleado();
		consecutivo = insertaSolicitud();
		datosSesion = iniciarSession(parametrosSolicitudes.getCompania());
		HashMap<String, Object> salida = new HashMap<>();

		byte[] base64 = null;
		if (!SysmanFunciones.validarVariableVacio(reporte)) {
			extension = EXTENSION_PDF.toString();
			formatoFinal = GeneraArchivoEnum.PDF.getValue();
			base64 = obtenerDocumentoPdf(reporte);
		} else if (!SysmanFunciones.validarVariableVacio(plantilla)) {
			base64 = obtenerDocumentoPlantillaSolicitudes(plantilla, consecutivo, salida);
			extension = (String) salida.get(GestionAutoservicioEnum.EXTENSION.getValue());
			formatoFinal = extension.contains(UtilitarioPlantillas.EXTENSION_WORD_97_2003)
					? GeneraArchivoEnum.WORD.getValue()
							: GeneraArchivoEnum.EXCEL.getValue();
		}
		return base64;
	}

	/**
	 * Obtiene un archivo en formato PDF para un reporte determinado.
	 *
	 * @param reporte c&oacute;digo del reporte
	 * @return arreglo de bytes que representa el informe en PDF
	 * @throws NegocioExcepcion
	 */
	private byte[] obtenerDocumentoPdf(String reporte) throws NegocioExcepcion {

		HashMap<String, Object> reemplazar = new HashMap<>();
		HashMap<String, Object> parametros = new HashMap<>();
		String rutaEncabezado =  "";
		String rutaPiePagina =  "";
		final String MENSAJE = idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_PARAMETROS.getValue());
		if (InvocadorClaseEnum.VOLANTE_PAGO.getClase() == parametrosSolicitudes.getClase()) {
			datosSesion.setModulo(moduloNomina);
			RetornoReporte retornoReporte;
			try
			{
				rutaEncabezado = ejbSysmanUtil.consultarParametro(
						parametrosSolicitudes.getCompania(),
						"RUTA IMAGEN ENCABEZADO VOLANTE DE PAGOS",
						moduloNomina, new Date(), false);
				rutaPiePagina = ejbSysmanUtil.consultarParametro(
						parametrosSolicitudes.getCompania(),
						"RUTA IMAGEN PIEPAGINA VOLANTE DE PAGOS",
						moduloNomina, new Date(), false);
			}
			catch (Exception e)
			{
				LOG.error("Error obteniendo las rutas de las imagenes de encabezado y/o pie de página "
						+ "->> mensaje ->> {} / causa ->> {}",
						e.getMessage(),
						e.getStackTrace());
			}

			try {
				PrepararReporte prepararReporte = new PrepararReporte(datosSesion);
				retornoReporte = prepararReporte.preparaVolante(parametrosSolicitudes.getCompania(), idEmpleado,
						idEmpleado, parametrosSolicitudes.getProceso(), parametrosSolicitudes.getAno(),
						parametrosSolicitudes.getMes(), parametrosSolicitudes.getPeriodo(), "0",
						SysmanConstantes.CONS_CENTRO, rutaEncabezado, rutaPiePagina );
				//se remplaza el valor de reporte por el obtenido en parametro del sistema con nombre la tabla ano y este valor es cambiado desde el modulo de Nomina
				reporte=ejbSysmanUtil.consultarParametro( parametrosSolicitudes.getCompania(),
						"FORMATO VOLANTE DE PAGO", datosSesion.getModulo(), new Date(), false);
				reemplazar = retornoReporte.getReemplazar();
				parametros = retornoReporte.getParametros();

			} catch (Exception e) {
				LOG.error(MENSAJE + " ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
				NegocioExcepcion error = new NegocioExcepcion(MENSAJE);
				error.initCause(new Exception(MENSAJE));
				throw error;

			}


		}

		if (InvocadorClaseEnum.CERTIFICADO_RETENCION.getClase() == parametrosSolicitudes.getClase()) {
			datosSesion.setModulo(moduloNomina);
			RetornoReporte retornoReporte;
			try {
				PrepararReporte prepararReporte = new PrepararReporte(datosSesion);
				retornoReporte = prepararReporte.prepararCertificadoDian(parametrosSolicitudes.getCompania(),
						parametrosSolicitudes.getAno(), parametrosSolicitudes.getCedula(),
						parametrosSolicitudes.getCedula(), new Date(), MENSAJECERTIFICADORETENCION);
			} catch (SysmanException e) {
				LOG.error(MENSAJE + " ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
				NegocioExcepcion error = new NegocioExcepcion(MENSAJE);
				error.initCause(new Exception(MENSAJE));
				throw error;
			}
			reemplazar = retornoReporte.getReemplazar();
			parametros = retornoReporte.getParametros();
		}

		/*
		 * De aqui en adelante todos los reportes deben pasar por esta porci&oacute;n de
		 * codigo lo cual es estandar para cada autoservicio
		 */
		try {
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(datosSesion.getModulo()), reemplazar, parametros,
					datosSesion);
		} catch (NumberFormatException | SysmanException e) {
			LOG.error("Error resolviendo consulta de reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(e.getMessage());
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONSULTA.getValue())));
			throw error;
		}

		try {
			if (SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(parametrosSolicitudes.getCompania(),
					"MANEJA CONTRASENA EN INFORMES DE AUTOSERVICIO","87",new Date(),false),"NO").equals("SI")) {
				ByteArrayInputStream bytePss = JsfUtil.serializarReporteContrasenia(reporte,parametros,ConectorPool.ESQUEMA_SYSMAN,
						ReportesBean.FORMATOS.PDF,parametrosSolicitudes.getCedula(),datosSesion);
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = bytePss.read(buffer)) != -1) {
					byteStream.write(buffer,0,bytesRead);
				}
				return byteStream.toByteArray();
			} else {
				return JsfUtil.serializarReporteBase64(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
						ReportesBean.FORMATOS.PDF, datosSesion);
			}
		} catch (JRException | IOException | SysmanException | SystemException e) {
			LOG.error("Error serializando el reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(), e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(e.getMessage());
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_SERIALIZA.getValue())));
			throw error;

		}
	}

	/**
	 * Trae los datos de la solicitud para extraer el reporte o plantilla necesaria
	 * para el informe
	 *
	 * @return nombre, tipo de solicitud, reporte y c&oacute;digo de la plantilla
	 *         para la clase de solicitud.
	 * @throws NegocioExcepcion en caso de que se generen errores capturando los
	 *                          datos de la solicitud.
	 */
	private Registro cargarDatosSolicitud() throws NegocioExcepcion {
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.CODIGO.getName(), parametrosSolicitudes.getClase());
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1008003.getValue());
			Parameter parameters = requestManager.get(urlBean.getUrl(), params);
			if (parameters == null) {
				String msg = idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CLASE.getValue());
				msg = msg.replace(GestionAutoservicioEnum.REEM_CLASE.getValue(), "" + parametrosSolicitudes.getClase());
				NegocioExcepcion error = new NegocioExcepcion(msg);
				error.initCause(new Exception(
						idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONFI.getValue())));
				throw error;
			}
			return RegistroConverter.toRegistro(parameters);

		} catch (SystemException e) {
			LOG.error("Error capturando los datos de la clase de solicitud ->> mensaje ->> {} / causa ->> {}",
					e.getMessage(), e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_DATOSCLASE.getValue()));
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_DATOSCLASE.getValue())));
			throw error;
		}

	}

	/**
	 * Escribe el archivo en disco a partir del arreglo de bytes.
	 *
	 * @param arregloBytes archivo serializado
	 * @param extension
	 * @return ruta abstracta en donde qued&oacute; alojado el archivo.
	 * @throws NegocioExcepcion en caso de que se presenten problemas al crear el
	 *                          archivo.
	 */
	private String crearArchivoTemporal(byte[] arregloBytes, String extension) throws NegocioExcepcion {
		Random random = new Random();
		int entero = random.nextInt();
		StringBuilder stringBuilder = new StringBuilder();
		/*stringBuilder.append("..");
		stringBuilder.append(File.separator);
		stringBuilder.append("..");
		stringBuilder.append(File.separator);
		stringBuilder.append("tmp");
		stringBuilder.append(File.separator);
		 */
		String ruta="";
		try {
			ruta = (String) SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(datosSesion.getCompania(),
					GestionAutoservicioEnum.GENERAR_RUTA_RELATIVA.getValue(), "87",
					new Date(), false), "");

		} catch (SystemException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		stringBuilder.append(ruta);
		stringBuilder.append(Math.abs(entero));
		stringBuilder.append(".");
		stringBuilder.append(extension);
		String pathname = stringBuilder.toString();


		File file = new File(pathname);
		file.getParentFile().mkdirs();
		try {
			FileUtils.writeByteArrayToFile(file, arregloBytes);
			LOG.info("Working Directory: {}", System.getProperty("user.dir"));
			LOG.info("Canonical Path: {}", file.getCanonicalPath());
			return pathname;
		} catch (IOException e) {
			LOG.error("No hay permisos para escribir el temporal. ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_TEMPORAL.getValue()));
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_TEMPORAL.getValue())));
			throw error;
		}

	}

	/**
	 * Genera un documento de MS Word/Excel a partir de una plantilla definida para
	 * la solicitud/consulta de autoservicio.
	 *
	 * @param codigoPlantilla c&oacute;digo de la plantilla
	 * @param consecutivo     n&uacute;mero de la solicitud de autoservicio
	 *                        previamente creada.
	 * @param salida          posibles parametros de salida.
	 * @return arreglo de bytes que representa un archivo de MS Word/Excel
	 * @throws NegocioExcepcion
	 */
	private byte[] obtenerDocumentoPlantillaSolicitudes(String codigoPlantilla, long consecutivo,
			Map<String, Object> salida) throws NegocioExcepcion {
		HashMap<String, String> variablesConsulta = new HashMap<>();
		variablesConsulta.put(GestionAutoservicioEnum.REEM_COMPANIA.getValue(),
				SysmanFunciones.concatenar(GestionAutoservicioEnum.COMILLA.getValue(),
						parametrosSolicitudes.getCompania(), GestionAutoservicioEnum.COMILLA.getValue()));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_CONSECUTIVO.getValue(),
				SysmanFunciones.toString(consecutivo));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_USUARIO.getValue(), SysmanFunciones.concatenar(
				GestionAutoservicioEnum.COMILLA.getValue(), USUARIO, GestionAutoservicioEnum.COMILLA.getValue()));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_CLASESOLICITUD.getValue(),
				SysmanFunciones.toString(parametrosSolicitudes.getClase()));
		return obtenerDocumentoPlantilla(codigoPlantilla, salida, variablesConsulta);
	}

	/**
	 * Genera un documento de MS Word/Excel a partir de una plantilla definida para
	 * la actualizaci&oacute;n de datos.
	 *
	 * @param codigoPlantilla c&oacute;digo de la plantilla
	 * @param consecutivo     n&uacute;mero de la solicitud de autoservicio
	 *                        previamente creada.
	 * @param salida          posibles parametros de salida.
	 * @param compania
	 * @return arreglo de bytes que representa un archivo de MS Word/Excel
	 * @throws NegocioExcepcion
	 */
	private byte[] obtenerDocumentoPlantillaActualizacionDatos(String codigoPlantilla, Long consecutivo,
			HashMap<String, Object> salida, String compania) throws NegocioExcepcion {
		HashMap<String, String> variablesConsulta = new HashMap<>();
		variablesConsulta.put(GestionAutoservicioEnum.REEM_COMPANIA.getValue(), SysmanFunciones.concatenar(
				GestionAutoservicioEnum.COMILLA.getValue(), compania, GestionAutoservicioEnum.COMILLA.getValue()));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_CONSECUTIVO.getValue(),
				SysmanFunciones.toString(consecutivo));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_IDEMPLEADO.getValue(), SysmanFunciones.toString(idEmpleado));
		return obtenerDocumentoPlantilla(codigoPlantilla, salida, variablesConsulta);
	}

	/**
	 * Genera un documento de MS Word/Excel a partir de una plantilla definida.
	 *
	 * @param codigoPlantilla  c&oacute;digo de la plantilla
	 * @param consecutivo      n&uacute;mero de la solicitud de autoservicio
	 *                         previamente creada.
	 * @param salida           posibles parametros de salida.
	 * @param variableConsulta variables para resolver consulta de la plantilla.
	 * @return arreglo de bytes que representa un archivo de MS Word/Excel
	 * @throws NegocioExcepcion
	 */
	private byte[] obtenerDocumentoPlantilla(String codigoPlantilla, Map<String, Object> salida,
			HashMap<String, String> variablesConsulta) throws NegocioExcepcion {
		Map<String, Object> datosPlantilla = traerDatosPlantilla(codigoPlantilla);
		Date fechaPlantilla = (Date) datosPlantilla.get(GeneralParameterEnum.FECHA.getName());
		try {
			return UtilitarioPlantillas.serializarDocumento(codigoPlantilla, fechaPlantilla, variablesConsulta,
					datosSesion, salida);
		} catch (SystemException e) {
			LOG.error("Error serializando documento plantilla. ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(e.getMessage());
			error.initCause(new Exception(e.getCause()));
			throw error;
		}
	}

	/**
	 * Trae el c&oacute;digo, nombre y fecha de la plantilla.
	 *
	 * @param plantilla c&oacute;digo de plantilla
	 * @return map con los datos de la plantilla
	 * @throws NegocioExcepcion
	 */
	private Map<String, Object> traerDatosPlantilla(String plantilla) throws NegocioExcepcion {
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.CODIGO.getName(), plantilla);
		params.put(GeneralParameterEnum.TIPO.getName(), TIPO_AUTOSERVICIO);
		String fechaSolicitud;
		try {
			fechaSolicitud = SysmanFunciones.convertirAFechaCadena(new Date());
		} catch (ParseException e) {
			throw new NegocioExcepcion(e);
		}
		params.put(GeneralParameterEnum.FECHA.getName(), fechaSolicitud);
		String urlEnumId = GeneraArchivoUrlEnum.URL104064.getValue();
		String url = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
		Parameter parameters;
		try {
			parameters = requestManager.get(url, params);
		} catch (SystemException e) {
			LOG.error("Error obteniendo los datos de la plantilla. ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());

			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_PLANTILLA.getValue()));
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_PLANTILLA.getValue())));
			throw error;
		}
		Registro registro = RegistroConverter.toRegistro(parameters);
		if (registro != null) {
			return registro.getCampos();
		} else {
			String msg = idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_PLANTILLANO.getValue());
			msg = msg.replace(GestionAutoservicioEnum.REEM_PLANTILLAS.getValue(), plantilla);
			NegocioExcepcion error = new NegocioExcepcion(msg);
			error.initCause(
					new Exception(idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONFI.getValue())));
			throw error;
		}
	}

	/**
	 * Metodo que permite inicializar la variables de sessi&oacute;n necesarias para
	 * que la generaci&oacute;n de los reportes se siga realizando
	 *
	 * @param compania
	 *
	 * @param cedula
	 * @throws NegocioExcepcion
	 */

	private DatosSesion iniciarSession(String compania) throws NegocioExcepcion {
		return iniciarSession(compania, true);
	}

	private DatosSesion iniciarSession(String compania, boolean consultarParametro) throws NegocioExcepcion {
		String usuario = USUARIO;
		String contrasena = CLAVE;
		Usuario user;
		DatosSesion datos = new DatosSesion();
		try {
			UsuarioDao udao = new UsuarioDao();
			CompaniaDao cdao = new CompaniaDao();
			udao.validarUsuario(usuario, contrasena, compania);
			user = udao.getUsuario();
			if (user != null) {
				datos.setUsuario(usuario);
				datos.setCompania(compania);
				Compania companiaIngreso = cdao.validarCompania(compania);
				datos.setCompaniaIngreso(companiaIngreso);
				if (consultarParametro) {
					datos.setExcelPlano(ejbSysmanUtil.consultarParametro(compania,
							GestionAutoservicioEnum.GENERAR_REPORTES_EN_EXCEL_DESDE_CONSULTA.getValue(), "-1",
							new Date(), true));
				}
				datos.setUser(user);
			} else {
				String msg = idioma.getString(GestionAutoservicioEnum.MSM_USUARIO_CONTRASENIA_INCORRECTO.getValue());
				NegocioExcepcion error = new NegocioExcepcion(msg);
				error.initCause(new Exception(
						idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_PERMISO.getValue())));
				throw error;

			}
		} catch (SysmanException | SystemException e) {
			String mensaje = idioma.getString(GestionAutoservicioEnum.MSM_USUARIO_CONTRASENIA_INCORRECTO.getValue());
			NegocioExcepcion error = new NegocioExcepcion(mensaje);
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_PERMISO.getValue())));
			throw error;
		}
		return datos;
	}

	/**
	 * Carga los datos del empleado como: identificaodr de empleado, sucursal de
	 * empleado, cargo de empleado, identificador de jefe directo y sucursal del
	 * jefe directo.
	 *
	 * @throws NegocioExcepcion en caso de que se presenten errores al consultar o
	 *                          no existan datos para el empleado dado.
	 */
	private void cargarDatosDeEmpleado() throws NegocioExcepcion {
		Map<String, Object> empleado;
		String compania = SysmanConstantes.CONS_COMPANIA_DEFAULT;
		int ano = 0;
		String cedula = null;
		int mes = 0;
		int periodo = 0;
		int proceso = 0;
		boolean aplicaHistoricos = false;
		if (parametrosSolicitudes != null) {
			compania = parametrosSolicitudes.getCompania();
			ano = parametrosSolicitudes.getAno();
			cedula = parametrosSolicitudes.getCedula();
			mes = parametrosSolicitudes.getMes();
			periodo = parametrosSolicitudes.getPeriodo();
			proceso = parametrosSolicitudes.getProceso();
			aplicaHistoricos = !(esSolicitud() || Arrays.asList(InvocadorClaseEnum.CERTIFICADO_RETENCION.getClase(),
					InvocadorClaseEnum.CERTIFICADO_LABORAL_CON_FUNCIONES.getClase(),
					InvocadorClaseEnum.VACACIONES_PENDIENTES.getClase(), InvocadorClaseEnum.MANUAL_FUNCIONES.getClase())
					.contains(parametrosSolicitudes.getClase()));
		} else if (parametrosDatosPersonales != null) {
			compania = parametrosDatosPersonales.getCompania();
			cedula = parametrosDatosPersonales.getCedula();
		} else if (parametrosDatosFamiliares != null) {
			compania = parametrosDatosFamiliares.getCompania();
			cedula = parametrosDatosFamiliares.getCedulaEmpleado();
		}
		// valida si consulta empleado por año o por historicos
		if (aplicaHistoricos) {
			empleado = traerDatosEmpleadoConHistorico(compania, ano, mes, periodo, proceso, cedula);
		} else {
			// toma el año actual para extraer los datos el empleado
			ano = Calendar.getInstance().get(Calendar.YEAR);
			empleado = traerDatosEmpleadoPorAno(compania, ano, cedula);
		}

		if (empleado.isEmpty()) {
			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_EMPLEADO.getValue()));
			error.initCause(new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_EMPLEADO.getValue())));
			throw error;
		}

		idEmpleado = (Integer) empleado.get(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
		sucursal = SysmanFunciones.toString(empleado.get(GeneralParameterEnum.SUCURSAL.getName()));
		cargo = SysmanFunciones.toString(empleado.get(GeneraArchivoEnum.ID_DE_CARGO.getValue()));
		jefeDirecto = SysmanFunciones.toString(empleado.get(GeneraArchivoEnum.JEFE_DIRECTO.getValue()));
		sucursalJefeDirecto = SysmanFunciones
				.toString(empleado.get(GeneraArchivoEnum.SUCURSAL_JEFE_DIRECTO.getValue()));
	}

	/**
	 * Consulta los datos del empleado y los carga en las variables de clase
	 * determinadas.
	 *
	 * @param compania c&oacute;digo de la compañia.
	 * @param ano      año del hist&oacute;rico
	 * @param cedula   n&uacute;mero de documento
	 * @return parametros de salida configurados en el DSS.
	 * @throws NegocioExcepcion en caso de que se presenten errores al consultar o
	 *                          no existan datos para el empleado dado.
	 */
	private Map<String, Object> traerDatosEmpleadoPorAno(String compania, int ano, String cedula)
			throws NegocioExcepcion {
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.ANO.getName(), ano);
		params.put(GeneralParameterEnum.NUMERO_DCTO.getName(), cedula);
		String idServicio = GeneraArchivoUrlEnum.URL210134.getValue();
		Parameter parameter;
		try {
			parameter = getRegistro(idServicio, params);
		} catch (SystemException e) {
			LOG.error("Error al consultar el empleado por año->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			String msg = idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_EMPLEADOTIPO.getValue());
			msg = msg.replace(GestionAutoservicioEnum.REEM_TIPO.getValue(),
					GestionAutoservicioEnum.REEM_ANO.getValue());
			NegocioExcepcion error = new NegocioExcepcion(msg);
			error.initCause(new NegocioExcepcion(msg));
			throw error;
		}
		return parameter != null ? parameter.getFields() : new HashMap<String, Object>();
	}

	/**
	 * Consulta los datos del empleado y los carga en las variables de clase
	 * determinadas
	 *
	 * @param compania c&oacute;digo de la compañia.
	 * @param ano      año del hist&oacute;rico
	 * @param mes      mes del hist&oacute;rico
	 * @param periodo  periodo del hist&oacute;rico
	 * @param proceso  n&uacute;mero de proceso del hist&oacute;rico
	 * @param cedula   n&uacute;mero de documento
	 * @return parametros de salida configurados en el DSS.
	 * @throws NegocioExcepcion en caso de que se presenten errores al consultar o
	 *                          no existan datos para el empleado dado.
	 */
	private Map<String, Object> traerDatosEmpleadoConHistorico(String compania, int ano, int mes, int periodo,
			int proceso, String cedula) throws NegocioExcepcion {
		Map<String, Object> params = new TreeMap<>();
		params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		params.put(GeneralParameterEnum.NUMERO_DCTO.getName(), cedula);
		params.put(GeneralParameterEnum.ANO.getName(), ano);
		params.put(GeneralParameterEnum.MES.getName(), mes);
		params.put(GeneralParameterEnum.PERIODO.getName(), periodo);
		params.put(GeneraArchivoEnum.PROCESO.getValue(), proceso);
		String idServicio = GeneraArchivoUrlEnum.URL210130.getValue();
		Parameter parameter;
		try {
			parameter = getRegistro(idServicio, params);
		} catch (SystemException e) {
			LOG.error("Error al consultar el empleado con historico ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());

			String msg = idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_EMPLEADOTIPO.getValue());
			msg = msg.replace(GestionAutoservicioEnum.REEM_TIPO.getValue(),
					GestionAutoservicioEnum.REEM_HISTORICO.getValue());
			NegocioExcepcion error = new NegocioExcepcion(msg);
			error.initCause(new NegocioExcepcion(msg));
			throw error;
		}
		return parameter != null ? parameter.getFields() : new HashMap<String, Object>();
	}

	/**
	 * Permite obtener un registro consumiendo un servicio determinado identificado
	 * por un identificador y unos parametros de entrada.
	 *
	 * @param idServicio        identificador del servicio
	 * @param parametrosEntrada parametros que recibe el servicio
	 * @return registro que representa los parametros de salida retornados de la
	 *         petici&oacute;n GET
	 * @throws SystemException en caso de que se presenten problemas al procesar la
	 *                         petici&oacute;n.
	 */
	private Parameter getRegistro(String idServicio, Map<String, Object> parametrosEntrada) throws SystemException {
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(idServicio);
		requestManager = new RequestManager();
		if (urlBean == null) {
			idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
			String msg = idioma.getString(GestionAutoservicioEnum.TB_TB4134.getValue());
			msg = msg.replace(GestionAutoservicioEnum.REEM_IDSERVICIO.getValue(), idServicio);
			throw new SystemException(msg);
		}
		return requestManager.get(urlBean.getUrl(), parametrosEntrada);
	}

	/**
	 * Registro de solicitud para actualizaic&oacute;n de datos personales.
	 *
	 * @param contexto parametros de entrada
	 *
	 * @throws NegocioExcepcion en caso de que se presenten problemas al registrar
	 *                          la solicitud.
	 * @return objeto respuesta con el consecutivo, formato y ruta abstracta en
	 *         donde queda alojado el archivo.
	 */
	public Respuesta solicitarActualizacionDatosPersonales(ParametrosEntradaDatosPersonales contexto)
			throws NegocioExcepcion {
		parametrosDatosPersonales = contexto;

		Map<String, Object> llaves = insertarActualizacionDatosPersonales();

		Long consecutivo = Long.valueOf(SysmanFunciones.toString(
				llaves.get(GestionAutoservicioEnum.KEY_.getValue() + GeneralParameterEnum.CONSECUTIVO.getName())));

		datosSesion = iniciarSession(parametrosDatosPersonales.getCompania());

		String codigoPlantilla = "4100_1";
		HashMap<String, Object> salida = new HashMap<>();
		byte[] base64 = obtenerDocumentoPlantillaActualizacionDatos(codigoPlantilla, consecutivo, salida,
				parametrosDatosPersonales.getCompania());
		String extension = (String) salida.get(GestionAutoservicioEnum.EXTENSION.getValue());
		String formato = extension.contains(UtilitarioPlantillas.EXTENSION_WORD_97_2003)
				? GeneraArchivoEnum.WORD.getValue()
						: GeneraArchivoEnum.EXCEL.getValue();

				String rutaRelativa = crearArchivoTemporal(base64, extension);

				if (base64 != null) {
					Respuesta respuesta = new Respuesta();
					respuesta.setConsecutivo(consecutivo);
					respuesta.setFormato(formato);
					respuesta.setArchivo(rutaRelativa);
					return respuesta;
				} else {
					return new Respuesta();
				}
	}

	/**
	 * lleva a cabo el registro de la solicitud de actualizaci&oacute;n de datos
	 * personales.
	 *
	 * @return consecutivo para la solicitud.
	 * @throws NegocioExcepcion
	 */
	private Map<String, Object> insertarActualizacionDatosPersonales() throws NegocioExcepcion {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), parametrosDatosPersonales.getCompania());
		parametros.put(GeneralParameterEnum.CREATED_BY.getName(), USUARIO_API + parametrosDatosPersonales.getCedula());
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
		parametros.put(GeneralParameterEnum.NUMERO_DCTO.getName(), parametrosDatosPersonales.getCedula());

		cargarDatosDeEmpleado();
		parametros.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
		parametros.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), idEmpleado);

		// campos opcionales
		parametros.put(GestionAutoservicioEnum.CALZADO.getValue(), parametrosDatosPersonales.getTallaCalzado());
		parametros.put(GestionAutoservicioEnum.CAMISA.getValue(), parametrosDatosPersonales.getTallaCamisa());
		parametros.put(GestionAutoservicioEnum.CHAQUETA.getValue(), parametrosDatosPersonales.getTallaChaqueta());
		parametros.put(GestionAutoservicioEnum.CIUDAD_HAB.getValue(), parametrosDatosPersonales.getCiudad());
		parametros.put(GestionAutoservicioEnum.DEPARTAMENTO_HAB.getValue(),
				parametrosDatosPersonales.getDepartamento());
		parametros.put(GeneralParameterEnum.DIRECCION.getName(), parametrosDatosPersonales.getDireccion());
		parametros.put(GestionAutoservicioEnum.EMAIL_PERSONAL.getValue(),
				parametrosDatosPersonales.getCorreoElectronico());
		parametros.put(GestionAutoservicioEnum.PAIS_HAB.getValue(), parametrosDatosPersonales.getPais());
		parametros.put(GestionAutoservicioEnum.PANTALON.getValue(), parametrosDatosPersonales.getTallaPantalon());
		parametros.put(GeneralParameterEnum.TELEFONOS.getName(), parametrosDatosPersonales.getTelefono());

		RegistroAutoservicio autoservicio = new RegistroAutoservicio();
		return autoservicio.postAutPersonal(parametros);
	}

	/**
	 * lleva a cabo el registro de la solicitud o consulta de autoservicio.
	 *
	 * @return consecutivo para la solicitud.
	 * @throws NegocioExcepcion
	 */
	private long insertaSolicitud() throws NegocioExcepcion {
		Map<String, Object> parametroInsert = new HashMap<>();
		parametroInsert.put(GeneralParameterEnum.COMPANIA.getName(), parametrosSolicitudes.getCompania());
		parametroInsert.put(GeneraArchivoEnum.CLASE_SOLICITUD.getValue(), parametrosSolicitudes.getClase());
		parametroInsert.put(GeneralParameterEnum.CEDULA.getName(), parametrosSolicitudes.getCedula());
		parametroInsert.put(GeneralParameterEnum.OBSERVACIONES.getName(), parametrosSolicitudes.getObservacion());
		parametroInsert.put(GeneraArchivoEnum.ID_DEMPLEADO.getValue(), idEmpleado);
		parametroInsert.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
		parametroInsert.put(GeneraArchivoEnum.ID_DE_CARGO.getValue(), cargo);
		parametroInsert.put(GeneraArchivoEnum.USUARIO_SISTEMA.getValue(),
				USUARIO_API + parametrosSolicitudes.getCedula());
		parametroInsert.put(GeneraArchivoEnum.JEFE_DIRECTO.getValue(), jefeDirecto);
		parametroInsert.put(GeneraArchivoEnum.SUCURSAL_JEFE_DIRECTO.getValue(), sucursalJefeDirecto);

		int tipoPermiso;
		int ano;
		int mes;
		int periodo;
		Date fechaInicio;
		Date horaInicio;
		Date fechaFinal;
		Date horaFinal;
		if (esSolicitud()) {
			tipoPermiso = parametrosSolicitudes.getTipoSolicitud();
			// Si es solicitud toma el mes actual
			mes = Calendar.getInstance().get(Calendar.MONTH);
			// Si es solicitud toma el año actual
			ano = Calendar.getInstance().get(Calendar.YEAR);
			// el periodo puede ser nulo cuando es una solicitud
			periodo = 0;

			Calendar calendarFechaIni = SysmanFunciones.parseIso8601(parametrosSolicitudes.getFechaInicial());
			Calendar calendarHoraIni = SysmanFunciones.parseIso8601(parametrosSolicitudes.getFechaInicial());
			Calendar calendarFechaFin = SysmanFunciones.parseIso8601(parametrosSolicitudes.getFechaFinal());
			Calendar calendarHoraFin = SysmanFunciones.parseIso8601(parametrosSolicitudes.getFechaFinal());
			fechaInicio = SysmanFunciones.obtenerParteFecha(calendarFechaIni);
			horaInicio = SysmanFunciones.obtenerParteTiempo(calendarHoraIni);
			fechaFinal = SysmanFunciones.obtenerParteFecha(calendarFechaFin);
			horaFinal = SysmanFunciones.obtenerParteTiempo(calendarHoraFin);
		} else {
			tipoPermiso = TIPO_PERMISO_CONSULTA;
			mes = parametrosSolicitudes.getMes();
			ano = parametrosSolicitudes.getAno();
			periodo = parametrosSolicitudes.getPeriodo();
			fechaInicio = SysmanFunciones.obtenerParteFecha(Calendar.getInstance());
			horaInicio = SysmanFunciones.obtenerParteTiempo(Calendar.getInstance());
			fechaFinal = SysmanFunciones.obtenerParteFecha(Calendar.getInstance());
			horaFinal = SysmanFunciones.obtenerParteTiempo(Calendar.getInstance());
		}

		parametroInsert.put(GeneraArchivoEnum.TIPO_PERMISO.getValue(), tipoPermiso);
		parametroInsert.put(GeneralParameterEnum.MES.getName(), mes);
		parametroInsert.put(GeneralParameterEnum.ANO.getName(), ano);
		parametroInsert.put(GeneralParameterEnum.PERIODO.getName(), periodo == 0 ? null : periodo);
		parametroInsert.put(GeneralParameterEnum.FECHA_INICIO.getName(), fechaInicio);
		parametroInsert.put(GeneraArchivoEnum.HORA_INICIO.getValue(), horaInicio);
		parametroInsert.put(GeneralParameterEnum.FECHA_FINAL.getName(), fechaFinal);
		parametroInsert.put(GeneraArchivoEnum.HORA_FINAL.getValue(), horaFinal);

		RegistroAutoservicio autoservicio = new RegistroAutoservicio();

		Map<String, Object> llaves = autoservicio.registrarSolicitud(parametroInsert);
		return Long.valueOf(SysmanFunciones.toString(
				llaves.get(GestionAutoservicioEnum.KEY_.getValue() + GeneralParameterEnum.CONSECUTIVO.getName())));
	}

	/**
	 * Revisa si la petici&oacute;n es de tipo Solicitud, es decir, que tenga un
	 * c&oacute;digo y un tipo de solicitud.
	 *
	 * @return verdadero si la petici&oacute;n es de tipo de solicitud de
	 *         autoservicios.
	 */
	private boolean esSolicitud() {
		return parametrosSolicitudes.getTipoSolicitud() > 0;
	}

	/**
	 * Registro de solicitud para actualizaic&oacute;n de datos de familiares del
	 * empleado.
	 *
	 * @param contexto parametros de entrada
	 * @return objeto respuesta con el consecutivo, formato y ruta abstracta en
	 *         donde queda alojado el archivo.
	 * @throws NegocioExcepcion
	 */
	public Respuesta solicitarActualizacionDatosFamiliares(ParametrosEntradaDatosFamiliares contexto)
			throws NegocioExcepcion {
		parametrosDatosFamiliares = contexto;

		Map<String, Object> llaves = insertarActualizacionDatosFamiliares();
		Long consecutivo = Long.valueOf(SysmanFunciones.toString(
				llaves.get(GestionAutoservicioEnum.KEY_.getValue() + GeneralParameterEnum.CONSECUTIVO.getName())));

		datosSesion = iniciarSession(parametrosDatosFamiliares.getCompania());

		String codigoPlantilla = "4100_2";
		HashMap<String, Object> salida = new HashMap<>();

		HashMap<String, String> variablesConsulta = new HashMap<>();
		variablesConsulta.put(GestionAutoservicioEnum.REEM_COMPANIA.getValue(),
				SysmanFunciones.concatenar(GestionAutoservicioEnum.COMILLA.getValue(),
						parametrosDatosFamiliares.getCompania(), GestionAutoservicioEnum.COMILLA.getValue()));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_CONSECUTIVO.getValue(),
				SysmanFunciones.toString(consecutivo));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_CEDULA.getValue(),
				SysmanFunciones.concatenar(GestionAutoservicioEnum.COMILLA.getValue(),
						parametrosDatosFamiliares.getCedulaEmpleado(), GestionAutoservicioEnum.COMILLA.getValue()));
		variablesConsulta.put(GestionAutoservicioEnum.REEM_SUCURSAL.getValue(), SysmanFunciones.concatenar(
				GestionAutoservicioEnum.COMILLA.getValue(), sucursal, GestionAutoservicioEnum.COMILLA.getValue()));
		byte[] base64 = obtenerDocumentoPlantilla(codigoPlantilla, salida, variablesConsulta);

		String extension = (String) salida.get(GestionAutoservicioEnum.EXTENSION.getValue());
		String formato = extension.contains(UtilitarioPlantillas.EXTENSION_WORD_97_2003)
				? GeneraArchivoEnum.WORD.getValue()
						: GeneraArchivoEnum.EXCEL.getValue();

				String rutaRelativa = crearArchivoTemporal(base64, extension);

				if (base64 != null) {
					Respuesta respuesta = new Respuesta();
					respuesta.setConsecutivo(consecutivo);
					respuesta.setFormato(formato);
					respuesta.setArchivo(rutaRelativa);
					return respuesta;
				} else {
					return new Respuesta();
				}
	}

	/**
	 * Registra la solicitud de actualizaci&oacute;n de datos de familiares.
	 *
	 * @return llaves de la tabla de familiares de autoservicio.
	 * @throws NegocioExcepcion en caso de que se presente algun problema realizando
	 *                          la inserci&oacute;n
	 */
	private Map<String, Object> insertarActualizacionDatosFamiliares() throws NegocioExcepcion {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put(GeneralParameterEnum.COMPANIA.getName(), parametrosDatosFamiliares.getCompania());
		parametros.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
		parametros.put(GeneralParameterEnum.CREATED_BY.getName(),
				USUARIO_API + parametrosDatosFamiliares.getCedulaEmpleado());
		parametros.put(GeneralParameterEnum.DCTO_EMPLEADO.getName(), parametrosDatosFamiliares.getCedulaEmpleado());

		cargarDatosDeEmpleado();
		parametros.put(GeneralParameterEnum.SUCURSAL_EMPLEADO.getName(), sucursal);
		parametros.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);

		parametros.put(GeneralParameterEnum.PARENTESCO.getName(), parametrosDatosFamiliares.getParentesco());
		parametros.put(GeneralParameterEnum.DCTO_IDENTIDAD.getName(), parametrosDatosFamiliares.getTipoDocumento());
		parametros.put(GeneralParameterEnum.IDENTIFICACION.getName(), parametrosDatosFamiliares.getNumeroDocumento());
		parametros.put(GeneralParameterEnum.NOMBRE.getName(), parametrosDatosFamiliares.getNombre());
		parametros.put(GeneralParameterEnum.APELLIDO1.getName(), parametrosDatosFamiliares.getPrimerApellido());
		parametros.put(GeneralParameterEnum.APELLIDO2.getName(), parametrosDatosFamiliares.getSegundoApellido());
		parametros.put(GeneralParameterEnum.DIRECCION.getName(), parametrosDatosFamiliares.getDireccion());
		parametros.put(GeneralParameterEnum.ESTADO_ACTUAL.getName(), parametrosDatosFamiliares.getEstado());

		Calendar calendarFechaNacimiento = SysmanFunciones.parseIso8601(parametrosDatosFamiliares.getFechaNacimiento());
		parametros.put(GestionAutoservicioEnum.FECHANCTO.getValue(), calendarFechaNacimiento.getTime());

		parametros.put(GeneralParameterEnum.SEXO.getName(), parametrosDatosFamiliares.getGenero());
		parametros.put(GeneralParameterEnum.TELEFONO.getName(), parametrosDatosFamiliares.getTelefono());
		parametros.put(GestionAutoservicioEnum.SALUD.getValue(),
				parametrosDatosFamiliares.isBeneficiaroSalud() ? -1 : 0);
		parametros.put(GestionAutoservicioEnum.OCUPACION.getValue(), parametrosDatosFamiliares.getOcupacion());
		parametros.put(GeneralParameterEnum.OBSERVACIONES.getName(), parametrosDatosFamiliares.getObservaciones());

		RegistroAutoservicio autoservicio = new RegistroAutoservicio();
		return autoservicio.postAutFamiliares(parametros);
	}

	public RespuestaApi generarInventarioIndividual(ParametrosEntradaInventario contexto) throws NegocioExcepcion {
		String reporte = GestionAutoservicioEnum.PAR_INFORMEINVENTARIO.getValue();
		byte[] base64 = null;
		try {
			String compania = contexto.getCompania();
			datosSesion = iniciarSession(compania, false);
			datosSesion.setModulo(moduloAlmacen);
			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();

			reemplazar.put(GestionAutoservicioEnum.REEM_COMP.getValue(), contexto.getCompania());
			reemplazar.put(GestionAutoservicioEnum.REEM_RESPONSABLE.getValue(), contexto.getCedula());

			parametros.put("PR_OBSERVACIONES", " ");
			parametros.put("PR_IMAGENES", datosSesion.getCompaniaIngreso().getRutaImagen());

			parametros.put("PR_MOSTAR", true);

			parametros.put("PR_ORDENADOR_ALMACEN", consultarParametro(compania, "ORDENADOR ALMACEN", moduloAlmacen));
			parametros.put("PR_SUBDIRECCION_ADMINISTRATIVA_Y_FINANCIERA",
					consultarParametro(compania, "SUBDIRECCION ADMINISTRATIVA Y FINANCIERA", moduloAlmacen));

			parametros.put("PR_NOMBRE_ADMINISTRADOR_Y_FINANZAS",
					consultarParametro(compania, "NOMBRE ADMINISTRADOR Y FINANZAS", moduloAlmacen));
			parametros.put("PR_CLAUSULA_INFORME_INVENTARIO_INDIVIDUAL",
					consultarParametro(compania, "CLAUSULA INFORME INVENTARIO INDIVIDUAL", moduloAlmacen));
			parametros.put("PR_COORDINADOR_ALMACEN",
					consultarParametro(compania, "COORDINADOR ALMACEN", moduloAlmacen));
			parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
					consultarParametro(compania, "CARGO COORDINADOR ALMACEN", moduloAlmacen));

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(datosSesion.getModulo()), reemplazar, parametros,
					datosSesion);

			base64 = JsfUtil.serializarReporteBase64(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.PDF, datosSesion);
		} catch (NumberFormatException | SysmanException | JRException | IOException e) {
			LOG.error("Error resolviendo consulta de reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(e.getMessage());
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONSULTA.getValue())));
			throw error;
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(base64);
		return respuesta;

	}

	public RespuestaApi infoInventario(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL141146.getValue());
		List<Parameter> parameters = null;
		RespuestaApi respuesta = new RespuestaApi();
		try {

			parameters = requestManager.getList(urlBean.getUrl(), contexto);
			respuesta.setCodigo(0);
			respuesta.setMensaje("OK");
			respuesta.setCuerpo(parameters);

		} catch (SystemException e) {
			throw new NegocioExcepcion(e.getMessage(), e.getCause());
		}

		return respuesta;
	}

	private String consultarParametro(String compania, String parametro, String modulo) throws NegocioExcepcion {
		String rta = "";
		try {
			if (GestionAutoservicioEnum.NITCENTROHISTORICO.getValue()
					.equals(datosSesion.getCompaniaIngreso().getNit())) {
				Registro rsParam;
				Map<String, Object> params = new TreeMap<>();
				params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

				params.put(GestionAutoservicioEnum.PARAM.getValue(), parametro);

				UrlBean urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL67002.getValue());
				Parameter parameters;

				parameters = requestManager.get(urlBean.getUrl(), params);
				rsParam = RegistroConverter.toRegistro(parameters);
				rta = SysmanFunciones.nvl(rsParam.getCampos().get(GestionAutoservicioEnum.VALOR.getValue()), " ")
						.toString();

			} else {
				rta = ejbSysmanUtil.consultarParametro(compania, parametro, modulo, new Date(), true);
			}

		} catch (SystemException e) {
			LOG.error("Error capturando los datos de parámetros ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_DATOSCLASE.getValue()));
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_DATOSCLASE.getValue())));
			throw error;
		}

		return rta;
	}

	public RespuestaApi generarOrganigrama(String contexto) throws NegocioExcepcion {

		String json = null;

		try {
			json = ejbOrganigrama.jsonOrganigramaBmanga(contexto);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(json);
		return respuesta;

	}

	public RespuestaApi cargarRecaudoCausacion(ParametrosRecuadoCausacion contexto) {

		/*
		 * gfigueredo 04/01/2022 Ticket 7706218  Se genera el archivo excel 
		 * con la configuración de los conceptos
		 * 
		 * gfigueredo 18/02/2022 Ticket 7710192 . Se comenta generación de archivo de
		 * conceptos desde postman. Solo se genera desde la ruta CONTABILIDAD O FACTURACION GENERAL/
		 * INFORMES/LISTADOS/CONCEPTOS/(check detalle)
		 */

		//generaReporteConceptos(contexto.getCompania(), contexto.getAnio(),
		//		contexto.getUsuario());

		RespuestaApi respuesta = new RespuestaApi();

		String retorno = null;
		int num = 0;

		StringBuilder cadena = new StringBuilder();

		cadena.append("TO_CLOB('");

		for (ParametrosRegRecuadoCausacion registros : contexto.getRegistros()) {

			cadena.append(registros.getFechaDeReporte());
			cadena.append(SysmanConstantes.SEPARADOR_COL);

			cadena.append(registros.getCodigo());
			cadena.append(SysmanConstantes.SEPARADOR_COL);

			cadena.append(registros.getValor());
			cadena.append(SysmanConstantes.SEPARADOR_COL);

			cadena.append(registros.getCuentaBancaria());
			cadena.append(SysmanConstantes.SEPARADOR_COL);

			cadena.append(registros.getObservacion());
			cadena.append(SysmanConstantes.SEPARADOR_COL);
			
			cadena.append(registros.getReciboUnico());
			cadena.append(SysmanConstantes.SEPARADOR_COL);

			cadena.append(SysmanConstantes.SEPARADOR_REG);

			num = num + cadena.length();

			if (num >= 10000) {
				cadena.append("') || TO_CLOB('");
				num = 0;
			}

		}

		cadena.append("')" + "");

		try {
			retorno = ejbFacturacionGeneralCero.cargarRecaudoCausacion(contexto.getCompania(), cadena.toString(),
					contexto.getTipoCobro(), contexto.getAnio(), contexto.getNitEntidad(), contexto.getUsuario(), contexto.getLoteNum());
		} catch (SystemException e) {
			String mensaje = e.getMessage();
			for (int i = 0; i < e.getMessage().length(); i++) {
				if (mensaje.charAt(i) == ':') {
					mensaje = mensaje.substring(i + 2);
					break;
				}
			}

			respuesta.setCodigo(5);
			respuesta.setMensaje("ERROR");
			respuesta.setCuerpo(mensaje);
			return respuesta;

		}
		/*
		 * gfigueredo 29/12/2021 Ticket 7703247 Se genera el archivo excel de las
		 * afectaciones contables
		 */
		// se comenta la generacion del reporte por requisito de historico de archivos en el ticket 7727003 ljdiaz
//		generaReporteAfectaciones(contexto.getCompania(), contexto.getAnio(), contexto.getTipoCobro(),
//				contexto.getUsuario());
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(retorno);
		return respuesta;

	}



	/**
	 * @author gfigueredo Función encargada de generar el reporte de conceptos    
	 * @param compania
	 * @param ano
	 * @param usuario
	 */
	public void generaReporteConceptos(String compania, int ano, String usuario) {
		Registro rsRutaArchivos;
		String nombreArchivo = "CONCEPTOS.xlsx";
		ConectorPool con = new ConectorPool();

		try {

			Map<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("APLICACION", 1);

			reemplazar.put("compania", compania);
			reemplazar.put("ano", ano);
			reemplazar.put("usuario", usuario);

			String sql = Reporteador.resuelveConsulta("800509ConceptosWebService", 1, reemplazar);


			con.conectar(ConectorPool.ESQUEMA_SYSMAN);

			Statement st = con.getConection().createStatement();
			ResultSet rs = st.executeQuery(sql);

			ArrayList<Object> objeto = new ArrayList<>();

			while (rs.next()) {
				String compania1 = rs.getString("COMPANIA");
				int ano1 = rs.getInt("ANO");
				String tipoCobro1 = rs.getString("TIPOCOBRO");
				String nombreTopo = rs.getString("NOMBRE_TOPO");
				String codigo = rs.getString("CODIGO");
				String nombre = rs.getString("NOMBRE_CONCEPTO");
				int descuento = rs.getInt("DESCUENTO");
				int valorBase = rs.getInt("VALOR_BASE");
				String unidad = (String) SysmanFunciones.nvl(rs.getString("UNIDAD"), "N.A");
				String cuentaDebBase = (String) SysmanFunciones.nvl(rs.getString("CUENTADEBITOBASE"), "N.A");
				String cuentaCredBase = (String) SysmanFunciones.nvl(rs.getString("CUENTACREDITOBASE"), "N.A");
				String cuentaDebBaseAv = (String) SysmanFunciones.nvl(rs.getString("CUENTADEBITOBASE_AV"), "N.A");
				String cuentaCredBaseAv = (String) SysmanFunciones.nvl(rs.getString("CUENTACREDITOBASE_AV"), "N.A");
				String cuentaRecaudo = (String) SysmanFunciones.nvl(rs.getString("CUENTA_RECAUDO"), "N.A");
				int estado = rs.getInt("ESTADO");
				String claseConcepto = (String) SysmanFunciones.nvl(rs.getString("CLASE_CONCEPTO"), "N.A");
				String referencia = rs.getString("REFERENCIA");
				String fuenteRecurso = rs.getString("FUENTE_RECURSO");
				String centroCosto = rs.getString("CENTRO_COSTO");
				String auxiliar = rs.getString("AUXILIAR");

				ArrayList<Object> detalle = new ArrayList<>();
				detalle.add(compania1);
				detalle.add(ano1);
				detalle.add(tipoCobro1);
				detalle.add(nombreTopo);
				detalle.add(codigo);
				detalle.add(nombre);
				detalle.add(descuento);
				detalle.add(valorBase);
				detalle.add(unidad);
				detalle.add(cuentaDebBase);
				detalle.add(cuentaCredBase);
				detalle.add(cuentaDebBaseAv);
				detalle.add(cuentaCredBaseAv);
				detalle.add(cuentaRecaudo);
				detalle.add(estado);
				detalle.add(claseConcepto);
				detalle.add(referencia);
				detalle.add(fuenteRecurso);
				detalle.add(centroCosto);
				detalle.add(auxiliar);

				objeto.add(detalle);
			}

			rsRutaArchivos = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL58004.getValue()).getUrl(), parametros));

			inicializarExcel(objeto);
			escribirHeaderConceptos();
			exportarExcel(rsRutaArchivos,nombreArchivo);

			st.close();
			rs.close();

		} catch (NamingException | SQLException | IOException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		} finally {
			try {
				con.getConection().close();
			}
			catch (SQLException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
	}


	/**
	 * @author gfigueredo Función encargada de generar el reporte de afectaciones
	 *         contables
	 * @param compania
	 * @param ano
	 * @param tipoCobro
	 * @param usuario
	 * Se cambia el tipo de metodo, se ajusta para ser llamado desde getExcelAfetaciones, el cual ahora dara mas detalle al nombre, para ser buscados mas adelante ticket 7727003
	 */
	public RespuestaApi generaReporteAfectaciones(ParametrosExcelAfectaciones contexto) {	
	Registro rsRutaArchivos = null;
	RespuestaApi respuesta = new RespuestaApi();
		ConectorPool con = new ConectorPool();
		String nombreArchivo = "AFECTACION_CONTABLE_"+contexto.getAnio()+"_"+contexto.getMes()+"_"+contexto.getDia()+".xlsx";
		try {			
			
			Map<String, Object> reemplazar = new HashMap<>();
			Map<String, Object> parametros = new HashMap<>();

			parametros.put("APLICACION", 1);

			reemplazar.put("compania", contexto.getCompania());
			reemplazar.put("ano", contexto.getAnio()-1);
			reemplazar.put("fecha", contexto.getAnio()+"/"+contexto.getMes()+"/"+contexto.getDia());
			reemplazar.put("usuario", contexto.getUsuario());
			
			String sql = Reporteador.resuelveConsulta("800508AfectacionesContablesWebService", 1, reemplazar);


			con.conectar(ConectorPool.ESQUEMA_SYSMAN);

			Statement st = con.getConection().createStatement();
			ResultSet rs = st.executeQuery(sql);

			ArrayList<Object> objeto = new ArrayList<>();
			int cont = 0;
			while (rs.next()) {
				cont = cont+1;
				String tipoCmpte = rs.getString("TIPO_CPTE");
				Date fecha = (Date) SysmanFunciones.nvl(rs.getDate("FECHA"), new Date());
				int comprobante = rs.getInt("COMPROBANTE");
				String cuentapptal = (String) SysmanFunciones.nvl(rs.getString("CUENTAPPTAL"), "N.A");
				String cuenta = rs.getString("CODIGO_CUENTA");
				int valDebito = rs.getInt("VALOR_DEBITO");
				int valCredito = rs.getInt("VALOR_CREDITO");
				String centroCosto = rs.getString("CENTRO_COSTO_DET");
				String referencia = rs.getString("REFERENCIA_DET");
				String fuente = rs.getString("FUENTE_RECURSO_DET");
				String auxiliar = rs.getString("AUXILIAR_DET");
				String descripcion = rs.getString("DESCRIPCION");
				String tipoCobroInforme = rs.getString("TIPOCOBRO");
				String nombreCobroInforme = rs.getString("NOMBRE_TIPO");
				String codigoConcepto = rs.getString("CODIGO");
				String nombreConcepto = rs.getString("NOMBRE_CONCEPTO");
				String cuentaDebitoBase = rs.getString("CUENTADEBITOBASE");
				String cuentaCreditoBase = rs.getString("CUENTACREDITOBASE");
				String cuentaDebitoAV = rs.getString("CUENTADEBITOBASE_AV");
				String cuentaCreditoAV = rs.getString("CUENTACREDITOBASE_AV");
				String cuentaRecaudo = rs.getString("CUENTA_RECAUDO");
				String claseConcepto = rs.getString("CLASE_CONCEPTO");
				String referenciaInforme = rs.getString("REFERENCIA");
				String fuenteRecurso = rs.getString("FUENTE_RECURSO");
				String centroCostoInforme = rs.getString("CENTRO_COSTO");
				String auxiliarInforme =  rs.getString("AUXILIAR");

				ArrayList<Object> detalle = new ArrayList<>();
				detalle.add(tipoCmpte);
				detalle.add(fecha);
				detalle.add(comprobante);
				detalle.add(cuentapptal);
				detalle.add(cuenta);
				detalle.add(valDebito);
				detalle.add(valCredito);
				detalle.add(centroCosto);
				detalle.add(referencia);
				detalle.add(fuente);
				detalle.add(auxiliar);
				detalle.add(descripcion);
				detalle.add(tipoCobroInforme);
				detalle.add(nombreCobroInforme);
				detalle.add(codigoConcepto);
				detalle.add(nombreConcepto);
				detalle.add(cuentaDebitoBase);
				detalle.add(cuentaCreditoBase);
				detalle.add(cuentaDebitoAV);
				detalle.add(cuentaCreditoAV);
				detalle.add(cuentaRecaudo);
				detalle.add(claseConcepto);
				detalle.add(referenciaInforme);
				detalle.add(fuenteRecurso);
				detalle.add(centroCostoInforme);
				detalle.add(auxiliarInforme);
								
				objeto.add(detalle);
				System.out.println("contanto: "+cont);
			}

			rsRutaArchivos = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL58004.getValue()).getUrl(), parametros));
			
			inicializarExcel(objeto);
			escribirHeaderAfectaciones();
			exportarExcel(rsRutaArchivos,nombreArchivo);

			st.close();
			rs.close();

		} catch (NamingException | SQLException | IOException | SystemException e) {
			JsfUtil.agregarMensajeError(e.getMessage());
		}finally {
			try {
				con.getConection().close();
			}
			catch (SQLException e) {
				JsfUtil.agregarMensajeError(e.getMessage());
			}
		}
		
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo("Generado con exito <>"+rsRutaArchivos.getCampos().get("RUTA_ARCHIVOS").toString()+"<>"+nombreArchivo);
		return respuesta;
	}

	/**
	 * @author gfigueredo Funcion encargada de inicializar el libro de excel
	 * @param excel
	 */
	public void inicializarExcel(List<Object> excel) {
		this.excel = excel;
		workbook = new XSSFWorkbook();
	}

	/**
	 * @author gfigueredo 
	 * Función encargada de establecer los estilos de la hoja de
	 *         excel y definir los nombres de las columnas.
	 */
	private void escribirHeaderAfectaciones() {
		sheet = workbook.createSheet("Afectaciones");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		crearCelda(row, 0, "TIPO COMPROBANTE", style);
		crearCelda(row, 1, "FECHA", style);
		crearCelda(row, 2, "NO. COMPROBANTE", style);
		crearCelda(row, 3, "CTA. PTO", style);
		crearCelda(row, 4, "CUENTA", style);
		crearCelda(row, 5, "DEBITO", style);
		crearCelda(row, 6, "CREDITO", style);
		crearCelda(row, 7, "CENTRO DE COSTO", style);
		crearCelda(row, 8, "REFERENCIA", style);
		crearCelda(row, 9, "FUENTE DE RECURSO", style);
		crearCelda(row, 10, "AUXILIAR", style);
		crearCelda(row, 11, "DESCRIPCION", style);
		crearCelda(row, 12, "TIPOCOBRO", style);
		crearCelda(row, 13, "NOMBRE TIPO", style);
		crearCelda(row, 14, "CODIGO", style);
		crearCelda(row, 15, "NOMBRE CONCEPTO", style);
		crearCelda(row, 16, "CUENTADEBITOBASE", style);
		crearCelda(row, 17, "CUENTACREDITOBASE", style);
		crearCelda(row, 18, "CUENTADEBITOBASE AV", style);
		crearCelda(row, 19, "CUENTACREDITOBASE AV", style);
		crearCelda(row, 20, "CUENTA RECAUDO", style);
		crearCelda(row, 21, "CLASE CONCEPTO", style);
		crearCelda(row, 22, "REFERENCIA", style);
		crearCelda(row, 23, "FUENTE RECURSO", style);
		crearCelda(row, 24, "CENTRO COSTO", style);
		crearCelda(row, 25, "AUXILIAR", style);
	}

	/**
	 * @author gfigueredo 
	 * Función encargada de establecer los estilos de la hoja de
	 *         excel y definir los nombres de las columnas.
	 */
	private void escribirHeaderConceptos() {
		sheet = workbook.createSheet("Conceptos");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		crearCelda(row, 0, "COMPANIA", style);
		crearCelda(row, 1, "ANO", style);
		crearCelda(row, 2, "TIPO COBRO", style);
		crearCelda(row, 3, "NOMBRE TOPO", style);
		crearCelda(row, 4, "CODIGO", style);
		crearCelda(row, 5, "NOMBRE CONCEPTO", style);
		crearCelda(row, 6, "DESCUENTO", style);
		crearCelda(row, 7, "VALOR BASE", style);
		crearCelda(row, 8, "UNIDAD", style);
		crearCelda(row, 9, "CUENTADEBITOBASE", style);
		crearCelda(row, 10, "CUENTACREDITOBASE", style);
		crearCelda(row, 11, "CUENTADEBITOBASE_AV", style);
		crearCelda(row, 12, "CUENTACREDITOBASE_AV", style);
		crearCelda(row, 13, "CUENTA RECAUDO", style);
		crearCelda(row, 14, "ESTADO", style);
		crearCelda(row, 15, "CLASE CONCEPTO", style);
		crearCelda(row, 16, "REFERENCIA", style);
		crearCelda(row, 17, "FUENTE RECURSO", style);
		crearCelda(row, 18, "CENTRO DE COSTO", style);
		crearCelda(row, 19, "AUXILIAR", style);

	}

	/**
	 * @author gfigueredo Función encargada de crear una celda en el libro de excel
	 * @param row
	 * @param columnCount
	 * @param value
	 * @param style
	 */
	private void crearCelda(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	/**
	 * @author gfigueredo Función encargada de escribir la inforación en el libro de
	 *         excel
	 */
	private void escribirDatos() {
		int rowCount = 1;

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);

		for (int i = 0; i < excel.size(); i++) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			for (int j = 0; j < ((ArrayList<?>) excel.get(i)).size(); j++) {
				String valor = "";
				if(((ArrayList<?>) excel.get(i)).get(j) != null) {
					valor = ((ArrayList<?>) excel.get(i)).get(j).toString();
					if (valor == null)
						valor = "null";
				}else {
					valor = "null";
				}
				crearCelda(row, columnCount++, valor, style);
			}

		}
	}

	/**
	 * @author gfigueredo Función encargada de exportar el archivo de excel
	 * @param rsRutaArchivos
	 * @throws IOException
	 */
	public void exportarExcel(Registro rsRutaArchivos,String nombreArchivo) throws IOException {
		escribirDatos();

		if (rsRutaArchivos != null) {

			String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_ARCHIVOS"), "").toString();

			if (!ruta.isEmpty()) {

				File verificar = new File(ruta);
				if (!verificar.isDirectory()) {
					verificar.mkdirs();
				}
				/*
				 * try (FileOutputStream outputStream = new FileOutputStream(ruta + "/" +
				 * "AFECTACION_CONTABLE" + "_" + SysmanFunciones.convertirAFechaCadena(new
				 * Date(), "YYYYMMDD_HHMMSS") + ".xlsx")) {
				 */
				try (FileOutputStream outputStream = new FileOutputStream(ruta + "/" + nombreArchivo)) {
					workbook.write(outputStream);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				JsfUtil.agregarMensajeAlerta(
						"Verificar la configuración de la ruta en la apliación en la configuración del sistema.");
			}
		}

	}

	/**
	 * @author gfigueredo
	 * Ticket 7706218 - Metodo encargado de asignar el valor de la ruta y el nombre del archivo excel de afectaciones
	 * @return valores
	 */
	public Map<String, Object> getRutaAfectaciones() {
		Map<String, Object> valores = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("APLICACION", 1);
		Registro rsRutaArchivos;

		try {
			rsRutaArchivos = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL58004.getValue()).getUrl(), parametros));
			if (rsRutaArchivos != null) {

				String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_ARCHIVOS"), "").toString();
				valores.put("nombre", "AFECTACION_CONTABLE.xlsx");
				valores.put("ruta", ruta);
				valores.put("fechacreacion", SysmanFunciones.convertirAFechaCadena(new Date(), "dd_MM_YYYY"));

			}
		} catch (SystemException | ParseException e) {
			e.printStackTrace();
		}

		return valores;

	}

	/**
	 * @author gfigueredo
	 * Ticket 7706218 - Metodo encargado de asignar el valor de la ruta y el nombre del archivo excel de conceptos
	 * @return valores
	 */
	public Map<String, Object> getRutaConceptos() {
		Map<String, Object> valores = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("APLICACION", 1);
		Registro rsRutaArchivos;

		try {
			rsRutaArchivos = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL58004.getValue()).getUrl(), parametros));
			if (rsRutaArchivos != null) {

				String ruta = SysmanFunciones.nvl(rsRutaArchivos.getCampos().get("RUTA_ARCHIVOS"), "").toString();
				valores.put("nombre", "CONCEPTOS.xlsx");
				valores.put("ruta", ruta);

			}
		} catch (SystemException e) {
			e.printStackTrace();
		}

		return valores;

	}

	public RespuestaApi insertarComprobanteCnt(ParametrosComprobanteCnt contexto) {
		parametrosComprobanteCnt = contexto;

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL72099.getValue());

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), parametrosComprobanteCnt.getCompania());

			parametros.put(GeneralParameterEnum.ANO.getName(), parametrosComprobanteCnt.getAno());

			parametros.put(GeneralParameterEnum.TIPO.getName(), parametrosComprobanteCnt.getTipo());

			parametros.put(GeneralParameterEnum.NUMERO.getName(), parametrosComprobanteCnt.getNumero());

			parametros.put(GeneralParameterEnum.FECHA.getName(),
					SysmanFunciones.convertirAFecha(parametrosComprobanteCnt.getFecha()));

			parametros.put(GeneralParameterEnum.TERCERO.getName(), parametrosComprobanteCnt.getTercero());

			parametros.put(GeneralParameterEnum.SUCURSAL.getName(), parametrosComprobanteCnt.getSucursal());

			parametros.put(GeneralParameterEnum.DESCRIPCION.getName(), parametrosComprobanteCnt.getDescripcion());

			parametros.put("FECHA_VCN_DOC", SysmanFunciones.convertirAFecha(parametrosComprobanteCnt.getFechaVcnDoc()));

			parametros.put("VLR_DOCUMENTO", parametrosComprobanteCnt.getVlrDocumento());

			parametros.put("DEBITO", parametrosComprobanteCnt.getDebito());

			parametros.put("CREDITO", parametrosComprobanteCnt.getCredito());

			parametros.put("VLRAGIRAR", parametrosComprobanteCnt.getVlrAGirar());

			parametros.put("TEXTO", parametrosComprobanteCnt.getTexto());
			
			parametros.put("FECHAPAGADOGN", parametrosComprobanteCnt.getFechapagadogn());
			
			parametros.put("NRO_DOCUMENTO", parametrosComprobanteCnt.getNro_documento());

			parametros.put(GeneralParameterEnum.CREATED_BY.getName(), parametrosComprobanteCnt.getCreatedBy());

			parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
					SysmanFunciones.convertirAFecha(parametrosComprobanteCnt.getDateCreated()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			parametros = requestManager.save(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException | ParseException e) {

			RespuestaApi respuesta = new RespuestaApi();
			respuesta.setCodigo(5);
			respuesta.setMensaje(e.getCause().getMessage());
			respuesta.setCuerpo("");
			return respuesta;
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(parametros);
		return respuesta;

	}

	public RespuestaApi actualizarComprobanteCnt(ParametrosComprobanteCnt contexto) {
		parametrosComprobanteCnt = contexto;

		int update = 0;

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL72100.getValue());

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put("KEY_COMPANIA", parametrosComprobanteCnt.getCompania());

			parametros.put("KEY_ANO", parametrosComprobanteCnt.getAno());

			parametros.put("KEY_TIPO", parametrosComprobanteCnt.getTipo());

			parametros.put("KEY_NUMERO", parametrosComprobanteCnt.getNumero());

			parametros.put("VLR_DOCUMENTO", parametrosComprobanteCnt.getVlrDocumento());

			parametros.put("DEBITO", parametrosComprobanteCnt.getDebito());

			parametros.put("CREDITO", parametrosComprobanteCnt.getCredito());

			parametros.put("VLRAGIRAR", parametrosComprobanteCnt.getVlrAGirar());

			parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), parametrosComprobanteCnt.getModifiedBy());

			parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					SysmanFunciones.convertirAFecha(parametrosComprobanteCnt.getDateModified()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			update = requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException | ParseException e) {
			
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(update);
		return respuesta;

	}

	public RespuestaApi actualizarComprobanteCntTexto(ParametrosComprobanteCnt contexto) {
		parametrosComprobanteCnt = contexto;

		int update = 0;

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL72103.getValue());

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put("KEY_COMPANIA", parametrosComprobanteCnt.getCompania());

			parametros.put("KEY_ANO", parametrosComprobanteCnt.getAno());

			parametros.put("KEY_TIPO", parametrosComprobanteCnt.getTipo());

			parametros.put("KEY_NUMERO", parametrosComprobanteCnt.getNumero());

			parametros.put("VLR_DOCUMENTO", parametrosComprobanteCnt.getVlrDocumento());

			parametros.put("DEBITO", parametrosComprobanteCnt.getDebito());

			parametros.put("CREDITO", parametrosComprobanteCnt.getCredito());

			parametros.put("VLRAGIRAR", parametrosComprobanteCnt.getVlrAGirar());

			parametros.put("TEXTO", parametrosComprobanteCnt.getTexto());

			parametros.put("DESCRIPCION", parametrosComprobanteCnt.getDescripcion());

			parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), parametrosComprobanteCnt.getModifiedBy());

			parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					SysmanFunciones.convertirAFecha(parametrosComprobanteCnt.getDateModified()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			update = requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException | ParseException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(update);
		return respuesta;

	}

	public RespuestaApi actualizarComprobanteCntAnulado(ParametrosComprobanteCnt contexto) {
		parametrosComprobanteCnt = contexto;

		int update = 0;

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL72104.getValue());

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put("KEY_COMPANIA", parametrosComprobanteCnt.getCompania());

			parametros.put("KEY_ANO", parametrosComprobanteCnt.getAno());

			parametros.put("KEY_TIPO", parametrosComprobanteCnt.getTipo());

			parametros.put("KEY_NUMERO", parametrosComprobanteCnt.getNumero());

			parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), parametrosComprobanteCnt.getModifiedBy());

			parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
					SysmanFunciones.convertirAFecha(parametrosComprobanteCnt.getDateModified()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			update = requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException | ParseException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(update);
		return respuesta;

	}

	public RespuestaApi eliminarComprobanteCnt(ParametrosComprobanteCnt contexto) {

		parametrosComprobanteCnt = contexto;

		int delete = 0;

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put("KEY_COMPANIA", parametrosComprobanteCnt.getCompania());
			parametros.put("KEY_ANO", parametrosComprobanteCnt.getAno());
			parametros.put("KEY_TIPO", parametrosComprobanteCnt.getTipo());
			parametros.put("KEY_NUMERO", parametrosComprobanteCnt.getNumero());

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL7200D.getValue());

			delete = requestManager.delete(urlBean.getUrl(), parametros);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(delete);
		return respuesta;

	}

	public RespuestaApi insertarDetalleComprobanteCnt(ParametrosDetalleComprobanteCnt contexto) {

		parametrosDetalleComprobanteCnt = contexto;

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39093.getValue());

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), parametrosDetalleComprobanteCnt.getCompania());

			parametros.put(GeneralParameterEnum.ANO.getName(), parametrosDetalleComprobanteCnt.getAno());

			parametros.put(GeneralParameterEnum.TIPO_CPTE.getName(), parametrosDetalleComprobanteCnt.getTipoCpte());

			parametros.put(GeneralParameterEnum.COMPROBANTE.getName(),
					parametrosDetalleComprobanteCnt.getComprobante());

			parametros.put(GeneralParameterEnum.CONSECUTIVO.getName(),
					parametrosDetalleComprobanteCnt.getConsecutivo());

			// para el caso de tasas se evalua el año y la cuenta para que en caso del año afectado 
						// sea menor a 2017 este tome la cuenta equivalente que inicia por 13
			String cuenta = parametrosDetalleComprobanteCnt.getCuenta();
			if(parametrosDetalleComprobanteCnt.getAnoAfect() != null && parametrosDetalleComprobanteCnt.getTipoCpteAfect() != null && parametrosDetalleComprobanteCnt.getCmpteAfectado() != null) {
				if(Integer.parseInt(parametrosDetalleComprobanteCnt.getAnoAfect()) <= 2017 && cuenta != null && cuenta.startsWith("14")) {
					
					Map<String, Object> parametrosCuenta = new HashMap<>();
					
					parametrosCuenta.put(GeneralParameterEnum.COMPANIA.getName(), parametrosDetalleComprobanteCnt.getCompania());
					parametrosCuenta.put(GeneralParameterEnum.ANO.getName(), parametrosDetalleComprobanteCnt.getAnoAfect());
					parametrosCuenta.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
					
					Registro rs = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16219.getValue()).getUrl(), parametrosCuenta));
				
					if(rs != null) {
						cuenta = rs.getCampos().get("COD_EQUI_CARTERA").toString();
					}
				}
			}
			parametros.put(GeneralParameterEnum.CUENTA.getName(), cuenta);
						
			parametros.put("CUENTAPPTAL", parametrosDetalleComprobanteCnt.getCuentaPptal());

			parametros.put(GeneralParameterEnum.FECHA.getName(),
					SysmanFunciones.convertirAFecha(parametrosDetalleComprobanteCnt.getFecha()));

			parametros.put(GeneralParameterEnum.NATURALEZA.getName(), parametrosDetalleComprobanteCnt.getNaturaleza());

			parametros.put("VALOR_DEBITO", parametrosDetalleComprobanteCnt.getValorDebito());

			parametros.put("VALOR_CREDITO", parametrosDetalleComprobanteCnt.getValorCredito());

			parametros.put("EJECUCION_DEBITO", parametrosDetalleComprobanteCnt.getEjecucionDebito());

			parametros.put("EJECUCION_CREDITO", parametrosDetalleComprobanteCnt.getEjecucionCredito());

			parametros.put(GeneralParameterEnum.DESCRIPCION.getName(),
					parametrosDetalleComprobanteCnt.getDescripcion());

			parametros.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
					parametrosDetalleComprobanteCnt.getCentroCosto());

			parametros.put(GeneralParameterEnum.TERCERO.getName(), parametrosDetalleComprobanteCnt.getTercero());

			parametros.put(GeneralParameterEnum.SUCURSAL.getName(), parametrosDetalleComprobanteCnt.getSucursal());

			parametros.put(GeneralParameterEnum.AUXILIAR.getName(), parametrosDetalleComprobanteCnt.getAuxiliar());

			parametros.put("NRO_DOCUMENTO", parametrosDetalleComprobanteCnt.getNroDocumento());

			parametros.put("ANO_AFECT", parametrosDetalleComprobanteCnt.getAnoAfect());

			parametros.put("TIPO_CPTE_AFECT", parametrosDetalleComprobanteCnt.getTipoCpteAfect());

			parametros.put("CMPTE_AFECTADO", parametrosDetalleComprobanteCnt.getCmpteAfectado());

			parametros.put("CONSECUTIVOAFECTADO", parametrosDetalleComprobanteCnt.getConsecutivoAfectado());

			parametros.put(GeneralParameterEnum.CREATED_BY.getName(), parametrosDetalleComprobanteCnt.getCreatedBy());

			parametros.put(GeneralParameterEnum.DATE_CREATED.getName(),
					SysmanFunciones.convertirAFecha(parametrosDetalleComprobanteCnt.getDateCreated()));

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			parametros = requestManager.save(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException e) {

			RespuestaApi respuesta = new RespuestaApi();
			respuesta.setCodigo(2);
			respuesta.setMensaje(e.getCause().getMessage());
			respuesta.setCuerpo("");
			return respuesta;

		} catch (ParseException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(parametros);
		return respuesta;

	}

	public RespuestaApi actualizarDetalleComprobanteCnt(ParametrosDetalleComprobanteCnt contexto) {

		parametrosDetalleComprobanteCnt = contexto;

		int update = 0;

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39094.getValue());

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put(GeneralParameterEnum.COMPANIA.getName(), parametrosDetalleComprobanteCnt.getCompania());

			parametros.put(GeneralParameterEnum.TIPO.getName(), parametrosDetalleComprobanteCnt.getTipoCpte());

			parametros.put(GeneralParameterEnum.NUMERO.getName(), parametrosDetalleComprobanteCnt.getComprobante());

			Parameter parameter = new Parameter();
			parameter.setFields(parametros);

			update = requestManager.update(urlBean.getUrl(), urlBean.getMetodo(), parameter);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(update);
		return respuesta;

	}

	public RespuestaApi eliminarDetalleComprobantesCnt(ParametrosDetalleComprobanteCnt contexto) {

		parametrosDetalleComprobanteCnt = contexto;

		int delete = 0;

		Map<String, Object> parametros = new HashMap<>();

		try {

			parametros.put("KEY_COMPANIA", parametrosDetalleComprobanteCnt.getCompania());
			parametros.put("KEY_ANO", parametrosDetalleComprobanteCnt.getAno());
			parametros.put("KEY_TIPO_CPTE", parametrosDetalleComprobanteCnt.getTipoCpte());
			parametros.put("KEY_COMPROBANTE", parametrosDetalleComprobanteCnt.getComprobante());

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39099.getValue());

			delete = requestManager.delete(urlBean.getUrl(), parametros);
		} catch (SystemException e) {

			RespuestaApi respuesta = new RespuestaApi();
			respuesta.setCodigo(2);
			respuesta.setMensaje(e.getCause().getMessage());
			respuesta.setCuerpo("");
			return respuesta;

		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(delete);
		return respuesta;

	}

	public RespuestaApi generarInventarioDependencia(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL141136.getValue());
		List<Parameter> parameters = null;

		try {

			parameters = requestManager.getList(urlBean.getUrl(), contexto);

		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(parameters);
		return respuesta;

	}

	public RespuestaApi consultarFacturaGeneral(Map<String, Object> contexto) throws NegocioExcepcion {

		RespuestaConsultarFacturaGeneral respuestaFactura = new RespuestaConsultarFacturaGeneral();

		try {

			Registro rs = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL661065.getValue()).getUrl(), contexto));

			if (rs != null) {

				respuestaFactura.setFechaLimite(rs.getCampos().get("FECHA_LIMITE").toString());

				respuestaFactura.setValorFactura(Double.parseDouble(rs.getCampos().get("VALOR_TOTAL").toString()));

				respuestaFactura.setEstado(
						SysmanFunciones.nvl(rs.getCampos().get(GeneralParameterEnum.ESTADO.getName()), "").toString());

			} else {
				RespuestaApi respuesta = new RespuestaApi();
				respuesta.setCodigo(5);
				respuesta.setMensaje("ERROR");
				respuesta.setCuerpo("El numero de factura no se encuentra registrada en el sistema");
				return respuesta;
			}

		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(respuestaFactura);
		return respuesta;

	}

	public RespuestaApi pagarFacturaGeneral(ParametrosPagarFacturaGeneral contexto) {

		RespuestaApi respuesta = new RespuestaApi();
		String numeroFactura;
		String compania;
		String usuario;
		int tipoRecaudo;
		String tipoFactura;
		Date fechaPago;
		String cuentaBanco;
		boolean causaEnRecaudo;
		Date fechaLiminte;
		Date fechaExpedicion;
		BigDecimal valorPagoContexto;
		BigDecimal valorPago;
		String estadoFactura;
		String referencia;
		try {
			referencia = contexto.getReferencia();

			numeroFactura = referencia.substring(referencia.length() - 10,
					referencia.length());

			tipoRecaudo = Integer.parseInt(referencia.substring(0, 5));

			fechaPago = SysmanFunciones
					.convertirAFecha(contexto.getFechaPago());

			/* valor de pago enviado desde contexto **/
			valorPagoContexto = new BigDecimal(contexto.getValorPagado());

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.FACTURA.getName(), numeroFactura);
			param.put("TIPORECAUDO", tipoRecaudo);

			// consultarFactura
			Registro rs = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									GeneraArchivoUrlEnum.URL661065
									.getValue())
							.getUrl(),
							param));

			if (rs != null) {

				compania = rs.getCampos()
						.get(GeneralParameterEnum.COMPANIA.getName())
						.toString();
				usuario = rs.getCampos()
						.get(GeneralParameterEnum.CREATED_BY.getName())
						.toString();

				/* Estado de la factura */
				estadoFactura = rs.getCampos()
						.get(GeneralParameterEnum.ESTADO.getName())
						.toString();

				/* fecha limite de la factura */
				fechaLiminte = SysmanFunciones.convertirAFecha(rs.getCampos()
						.get("FECHA_LIMITE").toString());
				/* valor pago registrado en factura */
				valorPago = new BigDecimal(
						rs.getCampos().get("VALOR_TOTAL").toString());

				/* fecha de expedicion de la factura */
				fechaExpedicion = SysmanFunciones.convertirAFecha(rs.getCampos()
						.get("FECHA_EX").toString());
				// ljdiaz 26/10/2023 - Se realiza validacion de las cuentas de recaudo relacionadas a los conceptos de la factura a recaudar
				// se usa como primera validacion puesto que sin cuentas configuradas no debe seguir el proceso.
				// se usan los parametros que vienen del inicio del metodo
				UrlBean urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL661074.getValue());
				List<Registro> verificaCuentaRecaudoConceptos = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), param));
				String conceptosNoConfig = "";
				for(Registro reg: verificaCuentaRecaudoConceptos) {						
					if(reg.getCampos().get("CUENTA_RECAUDO") == null) {
						conceptosNoConfig = conceptosNoConfig+"El Concepto: "+reg.getCampos().get("CODIGO").toString()+" No tiene cuenta de recaudo configurada \n";
					}				
				}
				if(conceptosNoConfig != "") {
					respuesta = new RespuestaApi();
					respuesta.setCodigo(5);
					respuesta.setMensaje(conceptosNoConfig);
					respuesta.setCuerpo(null);
					return respuesta;
				}				
				
				if (estadoFactura.replace(".", "").toLowerCase()
						.contains("factura paga")) {
					respuesta = new RespuestaApi();
					respuesta.setCodigo(5);
					respuesta.setMensaje("Factura paga.");
					respuesta.setCuerpo(null);
					return respuesta;
				}
				else if (estadoFactura.replace(".", "").toLowerCase()
						.contains("factura anulada")) {
					respuesta = new RespuestaApi();
					respuesta.setCodigo(5);
					respuesta.setMensaje("Factura Anulada.");
					respuesta.setCuerpo(null);
					return respuesta;
				}
				else if (fechaExpedicion.compareTo(fechaPago) > 0) {
					respuesta = new RespuestaApi();
					respuesta.setCodigo(5);
					respuesta.setMensaje(
							"No es posible recaudar la factura debido a que la fecha de pago es inferior"
									+ " a la de expedición. Por favor, verifique la fecha de pago.");
					respuesta.setCuerpo(null);
					return respuesta;
				}
				else if (fechaLiminte.compareTo(fechaPago) < 0) {
					respuesta = new RespuestaApi();
					respuesta.setCodigo(5);
					respuesta.setMensaje("La fecha límite de pago ya culminó.");
					respuesta.setCuerpo(null);
					return respuesta;
				}
				else if (valorPagoContexto.compareTo(valorPago) != 0) {
					respuesta = new RespuestaApi();
					respuesta.setCodigo(5);
					respuesta.setMensaje(
							"Error el valor de la factura es diferente al valor registrado en el sistema.");
					respuesta.setCuerpo(null);
					return respuesta;
				}
				else {
					// Consultar tipo de Cobro
					param = new TreeMap<>();
					param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
					param.put(GeneralParameterEnum.ANIO.getName(),
							SysmanFunciones.ano(fechaPago));
					param.put("TIPORECAUDO", tipoRecaudo);

					rs = RegistroConverter
							.toRegistro(requestManager.get(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											GeneraArchivoUrlEnum.URL665027
											.getValue())
									.getUrl(),
									param));

					tipoFactura = rs.getCampos()
							.get(GeneralParameterEnum.CODIGO.getName())
							.toString();

					causaEnRecaudo = (boolean) rs.getCampos()
							.get("INTERFAZ_RECAUDO");

					// Realiza causacion
					if (causaEnRecaudo) {

						ejbFacturacionGeneralCero.interfazarFactura(compania,
								tipoFactura, new BigInteger(numeroFactura),
								fechaPago,
								true, false, usuario);
					}
				}
			}
			else {
				respuesta = new RespuestaApi();
				respuesta.setCodigo(5);
				respuesta.setMensaje("ERROR");
				respuesta.setCuerpo("El número de factura no se encuentra registrada en el sistema.");
				return respuesta;
			}
			// ljdiaz 25/10/2023 - Luego de eliminada la seccion de la obtencion de la cuenta de las tablas de los bancos, 
			// se enviara la cuenta vacia, ya que el paquete en pl/sql validara que este vacia y usara el pago parcial para la obtencion 
			// de los datos de la cuenta desde los conceptos 
			ejbFacturacionGeneralCero.recaudarFactura(compania,
					tipoFactura,
					new BigInteger(numeroFactura),
					"Recaudo Factura " + numeroFactura,
					"", fechaPago,
					SysmanFunciones.ano(fechaPago), false,
					usuario);
		}
		catch (SystemException | ParseException e) {
			respuesta = new RespuestaApi();
			respuesta.setCodigo(5);
			respuesta.setMensaje("ERROR");
			respuesta.setCuerpo(e.getMessage());
			return respuesta;
		}

		respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo("OK");
		return respuesta;
	}


	public RespuestaApi cargarTercero(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL14194.getValue());

		List<RespuestaCargarTercero> listaTerceros = new ArrayList<>();

		List<Registro> parameters = null;

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					RespuestaCargarTercero respuestaTercero = new RespuestaCargarTercero();

					respuestaTercero.setNit(reg.getCampos().get(GeneralParameterEnum.NIT.getName()).toString());

					respuestaTercero
					.setSucursal(reg.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString());

					respuestaTercero
					.setCompania(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()).toString());

					respuestaTercero.setNombre(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString());

					respuestaTercero.setDireccion(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.DIRECCION.getName()), "").toString());

					respuestaTercero.setPais(SysmanFunciones.nvl(reg.getCampos().get("PAIS"), "").toString());

					respuestaTercero.setDepartamento(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.DEPARTAMENTO.getName()), "").toString());

					respuestaTercero.setCiudad(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.CIUDAD.getName()), "").toString());

					respuestaTercero.setTelefono(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.TELEFONOS.getName()), "").toString());

					respuestaTercero.setTipoId(SysmanFunciones.nvl(reg.getCampos().get("TIPOID"), "").toString());

					respuestaTercero
					.setNitCedula(SysmanFunciones.nvl(reg.getCampos().get("NIT_CEDULA"), "").toString());

					respuestaTercero.setNombre1(SysmanFunciones.nvl(reg.getCampos().get("NOMBRE1"), "").toString());

					respuestaTercero.setNombre2(SysmanFunciones.nvl(reg.getCampos().get("NOMBRE2"), "").toString());

					respuestaTercero
					.setApellildo1(SysmanFunciones.nvl(reg.getCampos().get("APELLIDO1"), "").toString());

					respuestaTercero
					.setApellildo2(SysmanFunciones.nvl(reg.getCampos().get("APELLIDO2"), "").toString());

					respuestaTercero
					.setPropietario(SysmanFunciones.nvl(reg.getCampos().get("PROPIETARIO"), "").toString());

					respuestaTercero.setClase(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.CLASE.getName()), "").toString());

					respuestaTercero.setNaturaleza(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()), "").toString());

					respuestaTercero
					.setTipoAsociado(SysmanFunciones.nvl(reg.getCampos().get("TIPO_ASOCIADO"), "").toString());

					respuestaTercero.setRegimen(SysmanFunciones.nvl(reg.getCampos().get("REGIMEN"), "").toString());

					respuestaTercero.setPorcDescuento(Double.parseDouble(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.PORCDESCUENTO.getName()), "0").toString()));

					respuestaTercero.setFax(SysmanFunciones.nvl(reg.getCampos().get("FAX"), "").toString());

					respuestaTercero.setEmail(SysmanFunciones.nvl(reg.getCampos().get("EMAIL"), "").toString());

					respuestaTercero
					.setAutoretenedor(SysmanFunciones.nvl(reg.getCampos().get("AUTORETENEDOR"), "").toString());

					respuestaTercero.setClaseEntidadOficial(
							SysmanFunciones.nvl(reg.getCampos().get("CLASEENTIDADOFICIAL"), "").toString());

					respuestaTercero.setAplicaDescuento(
							SysmanFunciones.nvl(reg.getCampos().get("APLICADESCUENTO"), "").toString());

					respuestaTercero.setExpedidaCedula(
							SysmanFunciones.nvl(reg.getCampos().get("EXPEDIDACEDULA"), "").toString());

					respuestaTercero.setDigitoVerificacion(
							SysmanFunciones.nvl(reg.getCampos().get("DIGITOVERIFICACION"), "").toString());

					respuestaTercero.setOrden(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.ORDEN.getName()), "").toString());

					respuestaTercero.setEstado(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.ESTADO.getName()), "").toString());

					listaTerceros.add(i, respuestaTercero);

					i++;

				}

			}

		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaTerceros);
		return respuesta;

	}

	public RespuestaApi cargarComprobanteCnt(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL72098.getValue());

		List<RespuestaCargarComprobanteCnt> listaComprobantes = new ArrayList<>();

		List<Registro> parameters = null;

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					RespuestaCargarComprobanteCnt params = new RespuestaCargarComprobanteCnt();

					params.setCompania(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()).toString());

					params.setTipo(reg.getCampos().get(GeneralParameterEnum.TIPO.getName()).toString());

					params.setNumero(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.NUMERO.getName()), "")
							.toString());

					params.setAnio(Integer.parseInt(reg.getCampos().get(GeneralParameterEnum.ANO.getName())

							.toString()));

					params.setFechaVcnDoc(SysmanFunciones.nvl(reg.getCampos().get("FECHA_VCN_DOC"), "").toString());

					params.setVlrDocumento(Double
							.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VLR_DOCUMENTO"), "0").toString()));

					params.setSucursal(reg.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString());

					params.setTercero(reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString());

					params.setVlrAGirar(
							Double.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VLRAGIRAR"), "0").toString()));

					params.setFecha(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.FECHA.getName()), "")
							.toString());

					listaComprobantes.add(i, params);

					i++;

				}

			}

		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaComprobantes);
		return respuesta;

	}

	public RespuestaApi cargarConsecutivoComprobanteCnt(Map<String, Object> contexto) throws NegocioExcepcion {

		long consecutivo = 0;

		String criterio = "COMPANIA = ''" + contexto.get(GeneralParameterEnum.COMPANIA.getName()) + "'' AND ANO ="
				+ contexto.get(GeneralParameterEnum.ANIO.getName()) + " AND TIPO = ''"
				+ contexto.get(GeneralParameterEnum.TIPO.getName()) + "''";

		try {
			consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo("COMPROBANTE_CNT", criterio,
					GeneralParameterEnum.NUMERO.getName());
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(consecutivo);
		return respuesta;

	}

	public RespuestaApi cargarDetalleComprobanteCnt(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39092.getValue());

		List<RespuestaCargarDetalleComprobanteCnt> listaComprobantes = new ArrayList<>();

		List<Registro> parameters = null;

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					RespuestaCargarDetalleComprobanteCnt params = new RespuestaCargarDetalleComprobanteCnt();

					params.setTipoCpteAfect(SysmanFunciones.nvl(reg.getCampos().get("TIPO_CPTE_AFECT"), "").toString());

					params.setCmpteAfectado(SysmanFunciones.nvl(reg.getCampos().get("CMPTE_AFECTADO"), "").toString());

					params.setFechaConsignacionPlano(
							SysmanFunciones.nvl(reg.getCampos().get("FECHA_CONSIGNACIONPLANO"), "").toString());

					params.setComprobante(SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()), "").toString());

					params.setValorCredito(Double.parseDouble(reg.getCampos().get("VALOR_CREDITO").toString()));

					params.setCompania(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()).toString());

					params.setTipoCpte(reg.getCampos().get(GeneralParameterEnum.TIPO_CPTE.getName()).toString());

					params.setAnio(Integer.parseInt(reg.getCampos().get(GeneralParameterEnum.ANO.getName())

							.toString()));

					params.setValorDebito(Double.parseDouble(reg.getCampos().get("VALOR_DEBITO").toString()));

					params.setCuenta(SysmanFunciones.nvl(reg.getCampos().get("CUENTA"), "").toString());

					params.setTercero(reg.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString());

					params.setAbonoInicial(Double
							.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("ABONOINICIAL"), "0").toString()));

					params.setVlrDocumento(Double
							.parseDouble(SysmanFunciones.nvl(reg.getCampos().get("VLR_DOCUMENTO"), "0").toString()));

					params.setNaturaleza(reg.getCampos().get(GeneralParameterEnum.NATURALEZA.getName()).toString());

					params.setFecha(reg.getCampos().get(GeneralParameterEnum.FECHA.getName()).toString());

					params.setConsecutivo(reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString());

					listaComprobantes.add(i, params);

					i++;

				}

			}

		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaComprobantes);
		return respuesta;

	}

	public RespuestaApi generarComprobanteContable(Comprobante comprobante) throws NegocioExcepcion {
		byte[] base64 = null;
		String compania = comprobante.getCompania();
		String tipo = comprobante.getTipo();
		String numero = comprobante.getNumero();
		int ano = comprobante.getAno();
		try {
			/*
			 * Se consulta el tipo de comprobante para traer el formato a imprimir
			 */
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.CODIGO.getName(), compania);
			Map<String, Object> campos = null;
			String formatoComprobante = "";
			param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.TIPOMOVIMIENTO.getName(), tipo);
			campos = null;
			try {
				campos = ejecutarSelectConRegistroUnico(URL15031, param);
			} catch (SystemException e) {
				throw new NegocioExcepcion(new StringBuilder()
						.append("Error al consultar el tipo de comprobante para imprimir").toString());
			}
			if (campos == null) {
				throw new NegocioExcepcion(
						new StringBuilder().append("No se encuentra formato configurado para imprimir").toString());
			} else {
				formatoComprobante = SysmanFunciones.toString(campos.get("FORMATO"));
			}

			datosSesion = iniciarSession(compania, false);
			datosSesion.setModulo(MODULOCONTABILIDAD);
			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();

			reemplazar.put("centroCosto", SysmanConstantes.CONS_CENTRO);
			reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
			reemplazar.put(TIPOCPTE, tipo);
			reemplazar.put(NUMEROCPTE, "= '" + numero + "'");
			reemplazar.put("numeroPptoInicial", numero);
			reemplazar.put("numeroPptoFinal", numero);
			reemplazar.put("modulo", MODULOCONTABILIDAD);

			Map<String, Object> valores = new HashMap<>();
			valores.put("informe", formatoComprobante);
			valores.put("formato", comprobante.getFormato());
			valores.put("nombreCompania", datosSesion.getCompaniaIngreso().getNombre());
			valores.put("nitCompania", datosSesion.getCompaniaIngreso().getNit());
			valores.put("lote", false);

			ComprobantesContPresReporteador comprobantesContPresReporteador = new ComprobantesContPresReporteador(
					ejbSysmanUtil, datosSesion);

			base64 = comprobantesContPresReporteador.generarInformeBase64(valores, parametros, reemplazar);

		} catch (Exception e) {
			LOG.error("Error resolviendo consulta de reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(
					e.getMessage() == null ? "Error resolviendo consulta de reporte" : e.getMessage());
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONSULTA.getValue())));
			throw error;
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(base64);
		return respuesta;

	}

	public RespuestaApi cargarGeneros() throws NegocioExcepcion {

		Map<String, Object> contexto = null;
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39095.getValue());
		List<RespuestaCargarGeneros> listaGeneros = new ArrayList<>();
		List<Registro> parameters = null;
		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarGeneros params = new RespuestaCargarGeneros();

					params.setCodigo(reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaGeneros.add(params);
					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaGeneros);
		return respuesta;
	}

	/**
	 * api que consulta estado de periodo
	 **/
	public RespuestaApi cargarEstadoPeriodo(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39096.getValue());
		List<RespuestaCargarEstadoPeriodo> estadoPeriodo = new ArrayList<>();
		List<Registro> parameters = null;
		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarEstadoPeriodo params = new RespuestaCargarEstadoPeriodo();

					params.setEstado(reg.getCampos().get(GeneralParameterEnum.ESTADO.getName()).toString());
					estadoPeriodo.add(params);
					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(estadoPeriodo);
		return respuesta;

	}

	/**
	 * api que consulta estado de periodo
	 **/
	public RespuestaApi cargarPeriodosCerrados(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39097.getValue());
		List<RespuestaCargarPeriodosCerrados> listaperiodos = new ArrayList<>();
		List<Registro> parameters = null;
		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarPeriodosCerrados params = new RespuestaCargarPeriodosCerrados();

					params.setPeriodo(reg.getCampos().get(GeneralParameterEnum.PERIODO.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					params.setCompania(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()).toString());
					params.setNit(reg.getCampos().get(GeneralParameterEnum.NITCOMPANIA.getName()).toString());
					listaperiodos.add(params);
					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaperiodos);
		return respuesta;

	}

	public RespuestaApi cargarTipotramites(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL39098.getValue());
		List<RespuestaCargarTipoTramite> listatipotramites = new ArrayList<>();
		List<Registro> parameters = null;
		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarTipoTramite params = new RespuestaCargarTipoTramite();

					params.setCodigoTramite(
							reg.getCampos().get(GeneralParameterEnum.CODIGOTRAMITE.getName()).toString());
					params.setNombreTramite(
							reg.getCampos().get(GeneralParameterEnum.NOMBRETRAMITE.getName()).toString());
					listatipotramites.add(params);
					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listatipotramites);
		return respuesta;
	}



	/**
	 * api que consulta datos del plan contable si se necesita anexar un campo mas realizarlo en este metodo
	 **/
	public RespuestaApi cargarPlanContable(Map<String, Object> contexto) throws NegocioExcepcion {
		UrlBean urlBean = null;
	    if(contexto.get("CODIGO").equals("PLANCONTABLE")) {
	    	 urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16214.getValue());
	    }else {
	    	 urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16213.getValue());
	    }
		
		List<RespuestaCargarPlanContable> listaperiodos = new ArrayList<>();
		List<Registro> parameters = null;
		try {
		   
			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaCargarPlanContable params = new RespuestaCargarPlanContable();
					params.setCompania(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()),"").toString());
					params.setAno(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.ANO.getName()),"").toString());
					params.setCodigo(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()),"").toString());
					params.setNombre(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()),"").toString());
					params.setNaturaleza(SysmanFunciones.nvl(reg.getCampos().get("NATURALEZA"),"").toString());
					params.setMovimiento(SysmanFunciones.nvl(reg.getCampos().get("MOVIMIENTO"),"").toString());
					params.setMan_cen_cto(SysmanFunciones.nvl(reg.getCampos().get("MAN_CEN_CTO"),"").toString());
					params.setMan_aux_ter(SysmanFunciones.nvl(reg.getCampos().get("MAN_AUX_TER"),"").toString());
					params.setMan_aux_gen(SysmanFunciones.nvl(reg.getCampos().get("MAN_AUX_GEN"),"").toString());
					params.setMan_aux_ref(SysmanFunciones.nvl(reg.getCampos().get("MAN_AUX_REF"),"").toString());
					params.setMan_aux_fue(SysmanFunciones.nvl(reg.getCampos().get("MAN_AUX_FUE"),"").toString());
					params.setObliga_tercero(SysmanFunciones.nvl(reg.getCampos().get("OBLIGA_TERCERO"),"").toString());
					params.setObliga_centro(SysmanFunciones.nvl(reg.getCampos().get("OBLIGA_CENTRO"),"").toString());
					params.setObliga_auxiliar(SysmanFunciones.nvl(reg.getCampos().get("OBLIGA_AUXILIAR"),"").toString());
					params.setObliga_referencia(SysmanFunciones.nvl(reg.getCampos().get("OBLIGA_REFERENCIA"),"").toString());
					params.setObliga_fuente(SysmanFunciones.nvl(reg.getCampos().get("OBLIGA_FUENTE"),"").toString());
					params.setDinamica(SysmanFunciones.nvl(reg.getCampos().get("DINAMICA"),"").toString());
					params.setPresupuesto_anual(SysmanFunciones.nvl(reg.getCampos().get("PRESUPUESTO_ANUAL"),"").toString());
					params.setCorriente(SysmanFunciones.nvl(reg.getCampos().get("CORRIENTE"),"").toString());
					params.setFormato(SysmanFunciones.nvl(reg.getCampos().get("FORMATO"),"").toString());
					params.setClasecuenta(SysmanFunciones.nvl(reg.getCampos().get("CLASECUENTA"),"").toString());
					params.setBloqueacuenta(SysmanFunciones.nvl(reg.getCampos().get("BLOQUEACUENTA"),"").toString());
					params.setSaldoinicial(SysmanFunciones.nvl(reg.getCampos().get("SALDOINICIAL"),"").toString());
					params.setSaldo0(SysmanFunciones.nvl(reg.getCampos().get("SALDO0"),"").toString());
					params.setSaldo1(SysmanFunciones.nvl(reg.getCampos().get("SALDO1"),"").toString());
					params.setSaldo2(SysmanFunciones.nvl(reg.getCampos().get("SALDO2"),"").toString());
					params.setSaldo3(SysmanFunciones.nvl(reg.getCampos().get("SALDO3"),"").toString());
					params.setSaldo4(SysmanFunciones.nvl(reg.getCampos().get("SALDO4"),"").toString());
					params.setSaldo5(SysmanFunciones.nvl(reg.getCampos().get("SALDO5"),"").toString());
					params.setSaldo6(SysmanFunciones.nvl(reg.getCampos().get("SALDO6"),"").toString());
					params.setSaldo7(SysmanFunciones.nvl(reg.getCampos().get("SALDO7"),"").toString());
					params.setSaldo8(SysmanFunciones.nvl(reg.getCampos().get("SALDO8"),"").toString());
					params.setSaldo9(SysmanFunciones.nvl(reg.getCampos().get("SALDO9"),"").toString());
					params.setSaldo10(SysmanFunciones.nvl(reg.getCampos().get("SALDO10"),"").toString());
					params.setSaldo11(SysmanFunciones.nvl(reg.getCampos().get("SALDO11"),"").toString());
					params.setSaldo12(SysmanFunciones.nvl(reg.getCampos().get("SALDO12"),"").toString());
					params.setSaldo13(SysmanFunciones.nvl(reg.getCampos().get("SALDO13"),"").toString());
					params.setNeto0(SysmanFunciones.nvl(reg.getCampos().get("NETO0"),"").toString());
					params.setNeto1(SysmanFunciones.nvl(reg.getCampos().get("NETO1"),"").toString());
					params.setNeto2(SysmanFunciones.nvl(reg.getCampos().get("NETO2"),"").toString());
					params.setNeto3(SysmanFunciones.nvl(reg.getCampos().get("NETO3"),"").toString());
					params.setNeto4(SysmanFunciones.nvl(reg.getCampos().get("NETO4"),"").toString());
					params.setNeto5(SysmanFunciones.nvl(reg.getCampos().get("NETO5"),"").toString());
					params.setNeto6(SysmanFunciones.nvl(reg.getCampos().get("NETO6"),"").toString());
					params.setNeto7(SysmanFunciones.nvl(reg.getCampos().get("NETO7"),"").toString());
					params.setNeto8(SysmanFunciones.nvl(reg.getCampos().get("NETO8"),"").toString());
					params.setNeto9(SysmanFunciones.nvl(reg.getCampos().get("NETO9"),"").toString());
					params.setNeto10(SysmanFunciones.nvl(reg.getCampos().get("NETO10"),"").toString());
					params.setNeto11(SysmanFunciones.nvl(reg.getCampos().get("NETO11"),"").toString());
					params.setNeto12(SysmanFunciones.nvl(reg.getCampos().get("NETO12"),"").toString());
					params.setNeto13(SysmanFunciones.nvl(reg.getCampos().get("NETO13"),"").toString());
					params.setDebito0(SysmanFunciones.nvl(reg.getCampos().get("DEBITO0"),"").toString());
					params.setDebito1(SysmanFunciones.nvl(reg.getCampos().get("DEBITO1"),"").toString());
					params.setDebito2(SysmanFunciones.nvl(reg.getCampos().get("DEBITO2"),"").toString());
					params.setDebito3(SysmanFunciones.nvl(reg.getCampos().get("DEBITO3"),"").toString());
					params.setDebito4(SysmanFunciones.nvl(reg.getCampos().get("DEBITO4"),"").toString());
					params.setDebito5(SysmanFunciones.nvl(reg.getCampos().get("DEBITO5"),"").toString());
					params.setDebito6(SysmanFunciones.nvl(reg.getCampos().get("DEBITO6"),"").toString());
					params.setDebito7(SysmanFunciones.nvl(reg.getCampos().get("DEBITO7"),"").toString());
					params.setDebito8(SysmanFunciones.nvl(reg.getCampos().get("DEBITO8"),"").toString());
					params.setDebito9(SysmanFunciones.nvl(reg.getCampos().get("DEBITO9"),"").toString());
					params.setDebito10(SysmanFunciones.nvl(reg.getCampos().get("DEBITO10"),"").toString());
					params.setDebito11(SysmanFunciones.nvl(reg.getCampos().get("DEBITO11"),"").toString());
					params.setDebito12(SysmanFunciones.nvl(reg.getCampos().get("DEBITO12"),"").toString());
					params.setDebito13(SysmanFunciones.nvl(reg.getCampos().get("DEBITO13"),"").toString());
					params.setCredito0(SysmanFunciones.nvl(reg.getCampos().get("CREDITO0"),"").toString());
					params.setCredito1(SysmanFunciones.nvl(reg.getCampos().get("CREDITO1"),"").toString());
					params.setCredito2(SysmanFunciones.nvl(reg.getCampos().get("CREDITO2"),"").toString());
					params.setCredito3(SysmanFunciones.nvl(reg.getCampos().get("CREDITO3"),"").toString());
					params.setCredito4(SysmanFunciones.nvl(reg.getCampos().get("CREDITO4"),"").toString());
					params.setCredito5(SysmanFunciones.nvl(reg.getCampos().get("CREDITO5"),"").toString());
					params.setCredito6(SysmanFunciones.nvl(reg.getCampos().get("CREDITO6"),"").toString());
					params.setCredito7(SysmanFunciones.nvl(reg.getCampos().get("CREDITO7"),"").toString());
					params.setCredito8(SysmanFunciones.nvl(reg.getCampos().get("CREDITO8"),"").toString());
					params.setCredito9(SysmanFunciones.nvl(reg.getCampos().get("CREDITO9"),"").toString());
					params.setCredito10(SysmanFunciones.nvl(reg.getCampos().get("CREDITO10"),"").toString());
					params.setCredito11(SysmanFunciones.nvl(reg.getCampos().get("CREDITO11"),"").toString());
					params.setCredito12(SysmanFunciones.nvl(reg.getCampos().get("CREDITO12"),"").toString());
					params.setCredito13(SysmanFunciones.nvl(reg.getCampos().get("CREDITO13"),"").toString());
					params.setAjuste0(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE0"),"").toString());
					params.setAjuste1(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE1"),"").toString());
					params.setAjuste2(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE2"),"").toString());
					params.setAjuste3(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE3"),"").toString());
					params.setAjuste4(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE4"),"").toString());
					params.setAjuste5(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE5"),"").toString());
					params.setAjuste6(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE6"),"").toString());
					params.setAjuste7(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE7"),"").toString());
					params.setAjuste8(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE8"),"").toString());
					params.setAjuste9(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE9"),"").toString());
					params.setAjuste10(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE10"),"").toString());
					params.setAjuste11(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE11"),"").toString());
					params.setAjuste12(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE12"),"").toString());
					params.setAjuste13(SysmanFunciones.nvl(reg.getCampos().get("AJUSTE13"),"").toString());
					params.setGeneradesembolso(SysmanFunciones.nvl(reg.getCampos().get("GENERADESEMBOLSO"),"").toString());
					params.setPorcretencion(SysmanFunciones.nvl(reg.getCampos().get("PORCRETENCION"),"").toString());
					params.setCreditoexterno(SysmanFunciones.nvl(reg.getCampos().get("CREDITOEXTERNO"),"").toString());
					params.setPasarsaldo(SysmanFunciones.nvl(reg.getCampos().get("PASARSALDO"),"").toString());
					params.setCod_equiv(SysmanFunciones.nvl(reg.getCampos().get("COD_EQUIV"),"").toString());
					params.setTransaccional5544(SysmanFunciones.nvl(reg.getCampos().get("TRANSACCIONAL5544"),"").toString());
					params.setDestino(SysmanFunciones.nvl(reg.getCampos().get("DESTINO"),"").toString());
					params.setFormatoegreso(SysmanFunciones.nvl(reg.getCampos().get("FORMATOEGRESO"),"").toString());
					params.setBanco(SysmanFunciones.nvl(reg.getCampos().get("BANCO"),"").toString());
					params.setPermiteconsolidar(SysmanFunciones.nvl(reg.getCampos().get("PERMITECONSOLIDAR"),"").toString());
					params.setMan_fact_arrendamiento(SysmanFunciones.nvl(reg.getCampos().get("MAN_FACT_ARRENDAMIENTO"),"").toString());
					params.setNotransaccional5544(SysmanFunciones.nvl(reg.getCampos().get("NOTRANSACCIONAL5544"),"").toString());
					params.setNoreportarreciprocas(SysmanFunciones.nvl(reg.getCampos().get("NOREPORTARRECIPROCAS"),"").toString());
					params.setTerceroequivalentereciprocas(SysmanFunciones.nvl(reg.getCampos().get("TERCEROEQUIVALENTERECIPROCAS"),"").toString());
					params.setConceptoex(SysmanFunciones.nvl(reg.getCampos().get("CONCEPTOEX"),"").toString());
					params.setCuenta_bancaria(SysmanFunciones.nvl(reg.getCampos().get("CUENTA_BANCARIA"),"").toString());
					params.setTerceroex(SysmanFunciones.nvl(reg.getCampos().get("TERCEROEX"),"").toString());
					params.setSucursalex(SysmanFunciones.nvl(reg.getCampos().get("SUCURSALEX"),"").toString());
					params.setTipodescuento_sia(SysmanFunciones.nvl(reg.getCampos().get("TIPODESCUENTO_SIA"),"").toString());
					params.setCodbanco_sia(SysmanFunciones.nvl(reg.getCampos().get("CODBANCO_SIA"),"").toString());
					params.setNumerocuenta_sia(SysmanFunciones.nvl(reg.getCampos().get("NUMEROCUENTA_SIA"),"").toString());
					params.setDestinocuentabanco(SysmanFunciones.nvl(reg.getCampos().get("DESTINOCUENTABANCO"),"").toString());
					params.setCodbanco_serec(SysmanFunciones.nvl(reg.getCampos().get("CODBANCO_SEREC"),"").toString());
					params.setNumerocuenta_serec(SysmanFunciones.nvl(reg.getCampos().get("NUMEROCUENTA_SEREC"),"").toString());
					params.setCuenta_pptal(SysmanFunciones.nvl(reg.getCampos().get("CUENTA_PPTAL"),"").toString());
					params.setEsoficial(SysmanFunciones.nvl(reg.getCampos().get("ESOFICIAL"),"").toString());
					params.setFuente(SysmanFunciones.nvl(reg.getCampos().get("FUENTE"),"").toString());
					params.setEquivpr_debito(SysmanFunciones.nvl(reg.getCampos().get("EQUIVPR_DEBITO"),"").toString());
					params.setEquivpr_credito(SysmanFunciones.nvl(reg.getCampos().get("EQUIVPR_CREDITO"),"").toString());
					params.setIvaex(SysmanFunciones.nvl(reg.getCampos().get("IVAEX"),"").toString());
					params.setRetepracticada(SysmanFunciones.nvl(reg.getCampos().get("RETEPRACTICADA"),"").toString());
					params.setReteasumida(SysmanFunciones.nvl(reg.getCampos().get("RETEASUMIDA"),"").toString());
					params.setIvacomun(SysmanFunciones.nvl(reg.getCampos().get("IVACOMUN"),"").toString());
					params.setIvasimplificado(SysmanFunciones.nvl(reg.getCampos().get("IVASIMPLIFICADO"),"").toString());
					params.setExdistrital(SysmanFunciones.nvl(reg.getCampos().get("EXDISTRITAL"),"").toString());
					params.setId_niif(SysmanFunciones.nvl(reg.getCampos().get("ID_NIIF"),"").toString());
					params.setCodigo_niif(SysmanFunciones.nvl(reg.getCampos().get("CODIGO_NIIF"),"").toString());
					params.setMan_distri_ccosto(SysmanFunciones.nvl(reg.getCampos().get("MAN_DISTRI_CCOSTO"),"").toString());
					params.setReteica(SysmanFunciones.nvl(reg.getCampos().get("RETEICA"),"").toString());
					params.setCree_practicada(SysmanFunciones.nvl(reg.getCampos().get("CREE_PRACTICADA"),"").toString());
					params.setCree_asumida(SysmanFunciones.nvl(reg.getCampos().get("CREE_ASUMIDA"),"").toString());
					params.setCcbalance(SysmanFunciones.nvl(reg.getCampos().get("CCBALANCE"),"").toString());
					params.setReportasaldoreciprocas(SysmanFunciones.nvl(reg.getCampos().get("REPORTASALDORECIPROCAS"),"").toString());
					params.setMen(SysmanFunciones.nvl(reg.getCampos().get("MEN"),"").toString());
					params.setVerificar_mov(SysmanFunciones.nvl(reg.getCampos().get("VERIFICAR_MOV"),"").toString());
					params.setCod_flujocaja(SysmanFunciones.nvl(reg.getCampos().get("COD_FLUJOCAJA"),"").toString());
					params.setCreated_by(SysmanFunciones.nvl(reg.getCampos().get("CREATED_BY"),"").toString());
					params.setModified_by(SysmanFunciones.nvl(reg.getCampos().get("MODIFIED_BY"),"").toString());
					params.setAplica_deterioro(SysmanFunciones.nvl(reg.getCampos().get("APLICA_DETERIORO"),"").toString());
					params.setDeb_reco_det(SysmanFunciones.nvl(reg.getCampos().get("DEB_RECO_DET"),"").toString());
					params.setCre_reco_det(SysmanFunciones.nvl(reg.getCampos().get("CRE_RECO_DET"),"").toString());
					params.setDeb_caus_det(SysmanFunciones.nvl(reg.getCampos().get("DEB_CAUS_DET"),"").toString());
					params.setCre_caus_det(SysmanFunciones.nvl(reg.getCampos().get("CRE_CAUS_DET"),"").toString());
					params.setDeb_rec_det(SysmanFunciones.nvl(reg.getCampos().get("DEB_REC_DET"),"").toString());
					params.setCre_rec_det(SysmanFunciones.nvl(reg.getCampos().get("CRE_REC_DET"),"").toString());
					params.setDate_modified(SysmanFunciones.nvl(reg.getCampos().get("DATE_MODIFIED"),"").toString());
					params.setDate_created(SysmanFunciones.nvl(reg.getCampos().get("DATE_CREATED"),"").toString());
					params.setCheque(SysmanFunciones.nvl(reg.getCampos().get("CHEQUE"),"").toString());
					params.setReportar_100(SysmanFunciones.nvl(reg.getCampos().get("REPORTAR_100"),"").toString());
					params.setTercero_reciprocas(SysmanFunciones.nvl(reg.getCampos().get("TERCERO_RECIPROCAS"),"").toString());
					params.setInd_circularunica(SysmanFunciones.nvl(reg.getCampos().get("IND_CIRCULARUNICA"),"").toString());
					params.setCuentas_maestras_salud(SysmanFunciones.nvl(reg.getCampos().get("CUENTAS_MAESTRAS_SALUD"),"").toString());
					params.setFecha_conciliacion(SysmanFunciones.nvl(reg.getCampos().get("FECHA_CONCILIACION"),"").toString());
					params.setSaldo_conciliacion(SysmanFunciones.nvl(reg.getCampos().get("SALDO_CONCILIACION"),"").toString());
					params.setObserv_conciliacion(SysmanFunciones.nvl(reg.getCampos().get("OBSERV_CONCILIACION"),"").toString());
					params.setMostrarf1001(SysmanFunciones.nvl(reg.getCampos().get("MOSTRARF1001"),"").toString());
					params.setInd_agente_retencion(SysmanFunciones.nvl(reg.getCampos().get("IND_AGENTE_RETENCION"),"").toString());
					params.setInd_sujeto_retencion(SysmanFunciones.nvl(reg.getCampos().get("IND_SUJETO_RETENCION"),"").toString());
					params.setCodigo_fut(SysmanFunciones.nvl(reg.getCampos().get("CODIGO_FUT"),"").toString());
					params.setNaturaleza_cgn(SysmanFunciones.nvl(reg.getCampos().get("NATURALEZA_CGN"),"").toString());
					params.setMostrar_en_flujo(SysmanFunciones.nvl(reg.getCampos().get("MOSTRAR_EN_FLUJO"),"").toString());
					params.setContraprestacion(SysmanFunciones.nvl(reg.getCampos().get("CONTRAPRESTACION"),"").toString());
					params.setConcepto_flujo_cgn(SysmanFunciones.nvl(reg.getCampos().get("CONCEPTO_FLUJO_CGN"),"").toString());
					params.setMostrar_en_flujo_cgn(SysmanFunciones.nvl(reg.getCampos().get("MOSTRAR_EN_FLUJO_CGN"),"").toString());
					params.setDebito_reversion_det_actual(SysmanFunciones.nvl(reg.getCampos().get("DEBITO_REVERSION_DET_ACTUAL"),"").toString());
					params.setCredito_reversion_det_actual(SysmanFunciones.nvl(reg.getCampos().get("CREDITO_REVERSION_DET_ACTUAL"),"").toString());
					params.setDebito_reversion_det_anterior(SysmanFunciones.nvl(reg.getCampos().get("DEBITO_REVERSION_DET_ANTERIOR"),"").toString());
					params.setCredito_reversion_det_anterior(SysmanFunciones.nvl(reg.getCampos().get("CREDITO_REVERSION_DET_ANTERIOR"),"").toString());
					params.setCod_equi_cartera(SysmanFunciones.nvl(reg.getCampos().get("COD_EQUI_CARTERA"),"").toString());
					listaperiodos.add(params);

					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaperiodos);
		return respuesta;

	}

	public RespuestaApi cargarTipoPobl(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1032005.getValue());
		List<RespuestaCargarTipoPobl> listaTipoPobl = new ArrayList<>();
		List<Registro> parameters = null;
		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarTipoPobl params = new RespuestaCargarTipoPobl();

					params.setCodigo(reg.getCampos().get(GeneralParameterEnum.ID.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaTipoPobl.add(params);
					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}		

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaTipoPobl);
		return respuesta;
	}

	public RespuestaApi cargarRangoEdad(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1032005.getValue());

		List<RespuestaCargarRangoEdad> listaRangoEdad = new ArrayList<>();
		List<Registro> parameters = null;

		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if(!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarRangoEdad params = new RespuestaCargarRangoEdad();

					params.setCodigo(reg.getCampos().get(GeneralParameterEnum.ID.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaRangoEdad.add(params);
					i++;

				}
			}

		}  catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaRangoEdad);
		return respuesta;

	}

	public RespuestaApi cargarVulnerabilidad(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1032005.getValue());

		List<RespuestaCargarVulnerabilidad> listaVulnerabilidad = new ArrayList<>();
		List<Registro> parameters = null;

		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if(!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarVulnerabilidad params = new RespuestaCargarVulnerabilidad();

					params.setCodigo(reg.getCampos().get(GeneralParameterEnum.ID.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaVulnerabilidad.add(params);
					i++;

				}
			}

		}  catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaVulnerabilidad);
		return respuesta;

	}

	public RespuestaApi cargarTipoPersona(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1032005.getValue());

		List<RespuestaCargarTipoPersona> listaTipoPersona = new ArrayList<>();
		List<Registro> parameters = null;

		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if(!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarTipoPersona params = new RespuestaCargarTipoPersona();

					params.setId(reg.getCampos().get(GeneralParameterEnum.ID.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaTipoPersona.add(params);
					i++;

				}
			}

		}  catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaTipoPersona);
		return respuesta;

	}

	public RespuestaApi cargarOcupacion(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1032005.getValue());

		List<RespuestaCargarOcupacion> listaOcupacion = new ArrayList<>();
		List<Registro> parameters = null;

		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if(!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarOcupacion params = new RespuestaCargarOcupacion();

					params.setCodigo(reg.getCampos().get(GeneralParameterEnum.ID.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaOcupacion.add(params);
					i++;

				}
			}

		}  catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaOcupacion);
		return respuesta;

	}

	public RespuestaApi cargarEscolaridad(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL1032005.getValue());

		List<RespuestaCargarEscolaridad> listaEscolaridad = new ArrayList<>();
		List<Registro> parameters = null;

		try {
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if(!parameters.isEmpty()) {
				for (Registro reg : parameters) {

					RespuestaCargarEscolaridad params = new RespuestaCargarEscolaridad();

					params.setCodigo(reg.getCampos().get(GeneralParameterEnum.ID.getName()).toString());
					params.setNombre(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString());
					listaEscolaridad.add(params);
					i++;

				}
			}

		}  catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaEscolaridad);
		return respuesta;

	}
	
	public RespuestaApi pagarLiquidacionFactura(ParametrosLiquidacionFactura contexto) {

		RespuestaApi respuesta = new RespuestaApi();
    	String datosR[] = null;
    	Map<String, String>  retor = new HashMap<>();
		String compania;
        int ano;
	    String tipoFactura;
	    String tercero;
	    String concepto;
	    String descripcion;
	    String datos;
	    compania = contexto.getCompania();
	    ano =  contexto.getAno();
	    tipoFactura =  contexto.getTipoFactura();
	    tercero =  contexto.getTercero();
	    concepto =  contexto.getConcepto();
	    descripcion =  contexto.getDescripcion();
	    
	    try {
	    	datos  =  ejbFacturacionGeneralCero.facturarConceptosLiquida(
	    			                                                      compania
	    			                                                     ,ano
	    			                                                     ,tipoFactura
	    			                                                     ,tercero
	    			                                                     ,concepto
	    			                                                     ,descripcion
	    			                                                     ,System.getenv("USERNAME")
	    			                                                     );
	    	
	    	datosR	 = datos.split(",R,");
            for (int i=0;i<datosR.length;i++) {
	    		retor.put(datosR[i].split(",V,")[0] , datosR[i].split(",V,")[1]); 
			}
	    	
	    	
		} catch (SystemException e) {
			respuesta.setCodigo(2);
			respuesta.setMensaje(e.getCause().getMessage());
			respuesta.setCuerpo("No se terminó el proceso");
			return respuesta;
		}
	    
	    
		respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(retor);
		return respuesta;
	}	
	
	
	/**
	 * Ejecuta un servicio DSS que corresponde a una consulta que devuelve un
	 * &uacute;nico registro.
	 * 
	 * @param idServicio identificador del servicio
	 * @param params     parametros para resolver la consulta
	 * @return mapa de datos con los campos devueltos por la consulta
	 * @throws SystemException
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> ejecutarSelectConRegistroUnico(String idServicio, Map<String, Object> params)
			throws SystemException {
		UrlServiceUtil urlService = UrlServiceUtil.getInstance();
		UrlBean urlBean = urlService.getUrlServiceByUrlByEnumID(idServicio);
		RequestManager requestManager = new RequestManager();
		String url = urlBean.getUrl();
		Parameter parameter = requestManager.get(url, params);
		return parameter.getFields();
	}
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
	
	/**
	 * api que consulta datos de las dependencias de la entidad. 
	 * 
	 **/
	public RespuestaApi obtenerDependenciasContab(Map<String, Object> contexto) throws NegocioExcepcion {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL62111.getValue());
		
		List<RespuestaDatosDependencias> listaDependencia = new ArrayList<>();
		List<Registro> parameters = null;
		try {			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaDatosDependencias params = new RespuestaDatosDependencias();
					params.setCodigo(reg.getCampos().get("CODIGO").toString());
					params.setNombreDep(reg.getCampos().get("NOMBRE").toString());
					params.setMovimenito(reg.getCampos().get("MOVIMIENTO").toString());
					params.setCentroCosto(reg.getCampos().get("NOMCENTROCOSTO").toString());
					params.setCodigCentroCosto(reg.getCampos().get("CC_CODIGO").toString());
					listaDependencia.add(params);

					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaDependencia);
		return respuesta;

	}
	/**
	 * obtiene listado de plancontable para cremil
	 * @param contexto
	 * @return
	 */
	public RespuestaApi obtenerListaPlanCotableCremil(Map<String, Object> contexto) {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16225.getValue());
	    
		List<RespuestaListaPlanContableBasico> listaperiodos = new ArrayList<>();
		List<Registro> parameters = null;
		try {
		   // ojo no consulta aun
			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaListaPlanContableBasico params = new RespuestaListaPlanContableBasico();
					params.setCompania(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName()),"").toString());
					params.setAno(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.ANO.getName()),"").toString());
					params.setCodigo(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()),"").toString());
					params.setCod_equiv(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.COD_EQUIV.getName()),"").toString());
					params.setNombre(SysmanFunciones.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()),"").toString());
					params.setNaturaleza(SysmanFunciones.nvl(reg.getCampos().get("NATURALEZA"),"").toString());
					params.setClasecuenta(SysmanFunciones.nvl(reg.getCampos().get("CLASECUENTA"),"").toString());
					params.setBloqueacuenta(SysmanFunciones.nvl(reg.getCampos().get("BLOQUEACUENTA"),"").toString());
					params.setCodClaseCuenta(SysmanFunciones.nvl(reg.getCampos().get("CODIGOCLASECUENTA"),"").toString());
					
					listaperiodos.add(params);

					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaperiodos);
		return respuesta;

	}
	/**
	 * api que consulta datos de las Fuentes recursos de la entidad por compania y año vigencia. 
	 * 
	 **/
	public RespuestaApi obtenerFuenteRecursos(Map<String, Object> contexto) throws NegocioExcepcion {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL34007.getValue());
		
		List<RespuestaDatosGeneral> listaFuenteRecursos = new ArrayList<>();
		List<Registro> parameters = null;
		try {			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaDatosGeneral params = new RespuestaDatosGeneral();
					params.setCodigo(reg.getCampos().get("CODIGO").toString());
					params.setCompania(contexto.get("COMPANIA").toString());
					params.setAnio(contexto.get("ANO").toString());
					params.setNombre(reg.getCampos().get("NOMBRE").toString());
					listaFuenteRecursos.add(params);
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaFuenteRecursos);
		return respuesta;

	}
	/**
	 * api que consulta datos de las Fuentes recursos de la entidad por compania y año vigencia. 
	 * 
	 **/
	public RespuestaApi obtenerCentroCosto(Map<String, Object> contexto) throws NegocioExcepcion {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL20024.getValue());
		
		List<RespuestaDatosGeneral> listaFuenteRecursos = new ArrayList<>();
		List<Registro> parameters = null;
		try {			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaDatosGeneral params = new RespuestaDatosGeneral();
					params.setCodigo(reg.getCampos().get("CODIGO").toString());
					params.setCompania(contexto.get("COMPANIA").toString());
					params.setAnio(contexto.get(GeneralParameterEnum.ANOACTUAL.getName()).toString());
					params.setNombre(reg.getCampos().get("NOMBRE").toString());
					listaFuenteRecursos.add(params);
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaFuenteRecursos);
		return respuesta;
	}
	/**
	 * api que consulta datos de las Fuentes recursos de la entidad por compania y año vigencia. 
	 * 
	 **/
	public RespuestaApi obtenerAuxiliar(Map<String, Object> contexto) throws NegocioExcepcion {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL23014.getValue());
		
		List<RespuestaDatosGeneral> listaFuenteRecursos = new ArrayList<>();
		List<Registro> parameters = null;
		try {			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaDatosGeneral params = new RespuestaDatosGeneral();
					params.setCodigo(reg.getCampos().get("CODIGO").toString());
					params.setCompania(contexto.get("COMPANIA").toString());
					params.setAnio(contexto.get(GeneralParameterEnum.ANOACTUAL.getName()).toString());
					params.setNombre(reg.getCampos().get("NOMBRE").toString());
					listaFuenteRecursos.add(params);
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaFuenteRecursos);
		return respuesta;
	}
	/**
	 * api que consulta datos de las Fuentes recursos de la entidad por compania y año vigencia. 
	 * 
	 **/
	public RespuestaApi obtenerReferencia(Map<String, Object> contexto) throws NegocioExcepcion {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL13049.getValue());
		
		List<RespuestaDatosGeneral> listaFuenteRecursos = new ArrayList<>();
		List<Registro> parameters = null;
		try {			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaDatosGeneral params = new RespuestaDatosGeneral();
					params.setCodigo(reg.getCampos().get("CODIGO").toString());
					params.setCompania(contexto.get("COMPANIA").toString());
					params.setAnio(contexto.get("ANO").toString());
					params.setNombre(reg.getCampos().get("NOMBRE").toString());
					listaFuenteRecursos.add(params);
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaFuenteRecursos);
		return respuesta;
	}
	
	/**
	 * obtiene listado de datos de la tabla SALDO_AUX_CONTABLE.
	 * @param contexto
	 * @return
	 */
	public RespuestaApi obtenerListaSaldoAuxContable(Map<String, Object> contexto) {
		UrlBean urlBean = null;
	    urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL219005.getValue());
	    
		List<RespuestaListaSaldoAuxContable> listaAuxContable = new ArrayList<>();
		List<Registro> parameters = null;
		try {
			
			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));
			int i = 0;
			if (!parameters.isEmpty()) {
				for (Registro reg : parameters) {
					RespuestaListaSaldoAuxContable params = new RespuestaListaSaldoAuxContable();
					params.setCompania(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName())));
					params.setAno(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.ANO.getName())));
					params.setCodigo(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.CODIGO.getName())));
					params.setCentro_costo(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName())));
					params.setNeto1(SysmanFunciones.toString(reg.getCampos().get("NETO1")));
					params.setNeto2(SysmanFunciones.toString(reg.getCampos().get("NETO2")));
					params.setNeto3(SysmanFunciones.toString(reg.getCampos().get("NETO3")));
					params.setNeto4(SysmanFunciones.toString(reg.getCampos().get("NETO4")));
					params.setNeto5(SysmanFunciones.toString(reg.getCampos().get("NETO5")));
					params.setNeto6(SysmanFunciones.toString(reg.getCampos().get("NETO6")));
					params.setNeto7(SysmanFunciones.toString(reg.getCampos().get("NETO7")));
					params.setNeto8(SysmanFunciones.toString(reg.getCampos().get("NETO8")));
					params.setNeto9(SysmanFunciones.toString(reg.getCampos().get("NETO9")));
					params.setNeto10(SysmanFunciones.toString(reg.getCampos().get("NETO10")));
					params.setNeto11(SysmanFunciones.toString(reg.getCampos().get("NETO11")));
					params.setNeto12(SysmanFunciones.toString(reg.getCampos().get("NETO12")));
					
					listaAuxContable.add(params);

					i++;
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(listaAuxContable);
		return respuesta;

	}
	
	public RespuestaApi reporteCuentasxCobrarCremil(Map<String, Object> contexto) {
		byte[] base64 = null;
		String reporte = null;
		String compania = SysmanFunciones.toString(contexto.get("compania"));
		ReportesBean.FORMATOS formato = (int) contexto.get("tipoformato") == 1 ? ReportesBean.FORMATOS.PDF
				: ReportesBean.FORMATOS.EXCEL97;
		
		String nombreInicial = traerNombre(SysmanFunciones.toString(contexto.get("terceroInicial")),SysmanFunciones.toString(contexto.get("compania")));
		String nombreFinal = traerNombre(SysmanFunciones.toString(contexto.get("terceroFinal")),SysmanFunciones.toString(contexto.get("compania")));		
		
		try {
			datosSesion = iniciarSession(compania, false);
			datosSesion.setModulo(MODULOCONTABILIDAD);
			datosSesion.setExcelPlano(null);
            // Convertir el String a un objeto Date
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaCorteDate = parser.parse(SysmanFunciones.toString(contexto.get("fechaCorte")));

			SimpleDateFormat formatoDeseado = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es","ES"));
			String fechaFormateada = formatoDeseado.format(fechaCorteDate);

			String grupoContabilidad = SysmanFunciones
					.toString(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania, "GRUPO DE CONTABILIDAD",
							datosSesion.getModulo(), new Date(), false), ""));

			String areaCartera = SysmanFunciones.toString(SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"AREA DE CARTERA", datosSesion.getModulo(), new Date(), false), ""));
			
			// Preparar parámetros del reporte
			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("fechaCorte", contexto.get("fechaCorte"));
			reemplazar.put("terceroInicial", contexto.get("terceroInicial"));
			reemplazar.put("terceroFinal", contexto.get("terceroFinal"));
			reemplazar.put("porVencimiento", (boolean) contexto.get("porVencimiento") ? "COMPROBANTE_CNT.FECHA_VCN_DOC"  : "DETALLE_COMPROBANTE_CNT.FECHA");
			reemplazar.put("cuentaInicial", contexto.get("cuentaInicial"));
			reemplazar.put("cuentaFinal", contexto.get("cuentaFinal"));
			reemplazar.put("compania", contexto.get("compania"));

			// Preparar parámetros "contexto"
			Map<String, Object> parametrosReporte = new HashMap<>();
			parametrosReporte.put("PR_FORMS_ANALISISCARTERACXC_TERCEROINICIAL", nombreInicial);
			parametrosReporte.put("PR_FORMS_ANALISISCARTERACXC_TERCEROFINAL", nombreFinal);
			parametrosReporte.put("PR_FORMS_ANALISISCARTERACXC_FECHACORTE", contexto.get("fechaCorte"));
			parametrosReporte.put("PR_CON_TOTAL_TERCERO",
					(boolean) contexto.get("porVencimiento") && (boolean) contexto.get("tercero"));
			parametrosReporte.put("PR_NOMBRECOMPANIA", datosSesion.getCompaniaIngreso().getNombre());
			parametrosReporte.put("PR_EXCEL", formato.equals(ReportesBean.FORMATOS.EXCEL97) ? false : true);
			parametrosReporte.put("PR_GRUPO_DE_CONTABILIDAD", grupoContabilidad);
			parametrosReporte.put("PR_AREA_DE_CARTERA", areaCartera);
			parametrosReporte.put("PR_FECHACORTE",fechaFormateada);
			parametrosReporte.put("PR_USER", contexto.get("usuario"));
			
			if ((boolean) contexto.get("edades")) {
				reporte = GestionAutoservicioEnum.REPORTE002685.getValue(); // "002685AnalisisCarteraCXC_MayorizarEdades"
			} else if ((boolean) contexto.get("financiables")) {
				reporte = GestionAutoservicioEnum.REPORTE002683.getValue(); // "002683AnalisisCarteraCXC_Financiables"
			} else if ((boolean) contexto.get("consolidado")) {
				reporte = GestionAutoservicioEnum.REPORTE002678.getValue(); // "002678AnalisisCarteraCXC_Consolidado"
			} else {
				reporte = GestionAutoservicioEnum.REPORTE000748.getValue(); // "000748AnalisisCarteraCXCFechavcntoxtercero"
			}

			if ((boolean) contexto.get("formatoEspecial")) {
				if ((boolean) contexto.get("tercero")) {
					reporte = GestionAutoservicioEnum.REPORTE800640.getValue(); // "800640ANALISISCARTERAXTERCERO"
					datosSesion.setExcelPlano("SI");
					formato = ReportesBean.FORMATOS.EXCEL97;
				}
					datosSesion.setExcelPlano("SI");
					formato = ReportesBean.FORMATOS.EXCEL97;
			}
			instance = new ReportesBean(datosSesion);
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(datosSesion.getModulo()), reemplazar,
					parametrosReporte, datosSesion);
			base64 = JsfUtil.serializarReporteBase64(reporte, parametrosReporte, ConectorPool.ESQUEMA_SYSMAN, formato,
					datosSesion);

		} catch (Exception e) {
			throw new RuntimeException("Error con el reporte: " + e.getMessage(), e);
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("Informe generado exitosamente");
		respuesta.setCuerpo(base64);
		return respuesta;
	}
	
	private String traerNombre(String nit, String compania) {
		String rta = "";
		Map<String, Object> param = new TreeMap<>();
		Registro rsParam;
		Parameter parameters = null;
		param.put(GeneralParameterEnum.NIT.getName(), nit);
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL14121.getValue());

			parameters = requestManager.get(urlBean.getUrl(), param);
			rsParam = RegistroConverter.toRegistro(parameters);
			rta = SysmanFunciones.nvl(rsParam.getCampos().get(GestionAutoservicioEnum.NOMBRE.getValue()), nit)
					.toString();

		} catch (SystemException e) {
			e.printStackTrace();
		}

		return rta;

	}
	
	/**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApiPagArqC cargarTerceroPag(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL14195.getValue());

		List<Map<String, Object>> listaTerceros = new ArrayList<>();

		List<Registro> parameters = null;
		
		Registro totalRegistros = null;
		
		RespuestaCargarTerceroPagCombo respuestaFinal = new RespuestaCargarTerceroPagCombo();

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					Map<String, Object> respuestaTercero = new TreeMap<>();

					respuestaTercero.put(GeneralParameterEnum.NIT.getName().toString(), 
							reg.getCampos().get(GeneralParameterEnum.NIT.getName()));

					respuestaTercero.put(GeneralParameterEnum.COMPANIA.getName().toString(),
						contexto.get(GeneralParameterEnum.COMPANIA.getName()));

					respuestaTercero.put(GeneralParameterEnum.NOMBRE.getName() ,SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString());

					listaTerceros.add(i, respuestaTercero);

					i++;

				}

			}
			
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL14196.getValue());
			
			Parameter parametersTotales = requestManager.get(urlBean.getUrl(), contexto);
			
			totalRegistros = RegistroConverter.toRegistro(parametersTotales);
			
			respuestaFinal.setTotales("null");
			
			respuestaFinal.setConteo(totalRegistros.getCampos().get(GeneralParameterEnum.TOTAL.getName()).toString());
			
			respuestaFinal.setDatos(listaTerceros);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApiPagArqC respuesta = new RespuestaApiPagArqC();
		respuesta.setOk("true");
		respuesta.setCodigo(0);
		respuesta.setMensaje(null);
		respuesta.setCuerpo(respuestaFinal);
		return respuesta;
	}
	/**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApiPagArqC cargarTerceroPagFinal(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL14048.getValue());

		List<Map<String, Object>> listaTerceros = new ArrayList<>();

		List<Registro> parameters = null;
		
		Registro totalRegistros = null;
		
		RespuestaCargarTerceroPagCombo respuestaFinal = new RespuestaCargarTerceroPagCombo();

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					Map<String, Object> respuestaTercero = new TreeMap<>();

					respuestaTercero.put(GeneralParameterEnum.NIT.getName().toString(), 
							reg.getCampos().get(GeneralParameterEnum.NIT.getName()));

					respuestaTercero.put(GeneralParameterEnum.COMPANIA.getName().toString(),
						contexto.get(GeneralParameterEnum.COMPANIA.getName()));

					respuestaTercero.put(GeneralParameterEnum.NOMBRE.getName() ,SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString());

					listaTerceros.add(i, respuestaTercero);

					i++;

				}

			}
			
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL14049.getValue());
			
			Parameter parametersTotales = requestManager.get(urlBean.getUrl(), contexto);
			
			totalRegistros = RegistroConverter.toRegistro(parametersTotales);
			
			respuestaFinal.setTotales("null");
			
			respuestaFinal.setConteo(totalRegistros.getCampos().get(GeneralParameterEnum.TOTAL.getName()).toString());
			
			respuestaFinal.setDatos(listaTerceros);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApiPagArqC respuesta = new RespuestaApiPagArqC();
		respuesta.setOk("true");
		respuesta.setCodigo(0);
		respuesta.setMensaje(null);
		respuesta.setCuerpo(respuestaFinal);
		return respuesta;
	}
	/**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApiPagArqC cargarCuentasCarteraPag(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16209.getValue());

		List<Map<String, Object>> listaTerceros = new ArrayList<>();

		List<Registro> parameters = null;
		
		Registro totalRegistros = null;
		
		RespuestaCargarTerceroPagCombo respuestaFinal = new RespuestaCargarTerceroPagCombo();

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					Map<String, Object> respuestaTercero = new TreeMap<>();

					respuestaTercero.put(GeneralParameterEnum.CODIGO.getName().toString(), 
							reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

					respuestaTercero.put(GeneralParameterEnum.COMPANIA.getName().toString(),
						contexto.get(GeneralParameterEnum.COMPANIA.getName()));

					respuestaTercero.put(GeneralParameterEnum.NOMBRE.getName() ,SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString());

					listaTerceros.add(i, respuestaTercero);

					i++;

				}

			}
			
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16210.getValue());
			
			Parameter parametersTotales = requestManager.get(urlBean.getUrl(), contexto);
			
			totalRegistros = RegistroConverter.toRegistro(parametersTotales);
			
			respuestaFinal.setTotales("null");
			
			respuestaFinal.setConteo(totalRegistros.getCampos().get(GeneralParameterEnum.TOTAL.getName()).toString());
			
			respuestaFinal.setDatos(listaTerceros);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApiPagArqC respuesta = new RespuestaApiPagArqC();
		respuesta.setOk("true");
		respuesta.setCodigo(0);
		respuesta.setMensaje(null);
		respuesta.setCuerpo(respuestaFinal);
		return respuesta;
	}
	/**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApiPagArqC cargarCuentasCarteraPagFinal(Map<String, Object> contexto) throws NegocioExcepcion {

		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16207.getValue());

		List<Map<String, Object>> listaTerceros = new ArrayList<>();

		List<Registro> parameters = null;
		
		Registro totalRegistros = null;
		
		RespuestaCargarTerceroPagCombo respuestaFinal = new RespuestaCargarTerceroPagCombo();

		try {

			parameters = RegistroConverter.toListRegistro(requestManager.getList(urlBean.getUrl(), contexto));

			int i = 0;

			if (!parameters.isEmpty()) {

				for (Registro reg : parameters) {

					Map<String, Object> respuestaTercero = new TreeMap<>();

					respuestaTercero.put(GeneralParameterEnum.CODIGO.getName().toString(), 
							reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()));

					respuestaTercero.put(GeneralParameterEnum.COMPANIA.getName().toString(),
						contexto.get(GeneralParameterEnum.COMPANIA.getName()));

					respuestaTercero.put(GeneralParameterEnum.NOMBRE.getName() ,SysmanFunciones
							.nvl(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "").toString());

					listaTerceros.add(i, respuestaTercero);

					i++;

				}

			}
			
			urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL16208.getValue());
			
			Parameter parametersTotales = requestManager.get(urlBean.getUrl(), contexto);
			
			totalRegistros = RegistroConverter.toRegistro(parametersTotales);
			
			respuestaFinal.setTotales("null");
			
			respuestaFinal.setConteo(totalRegistros.getCampos().get(GeneralParameterEnum.TOTAL.getName()).toString());
			
			respuestaFinal.setDatos(listaTerceros);
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApiPagArqC respuesta = new RespuestaApiPagArqC();
		respuesta.setOk("true");
		respuesta.setCodigo(0);
		respuesta.setMensaje(null);
		respuesta.setCuerpo(respuestaFinal);
		return respuesta;
	}
	

	public RespuestaApi consultarParam(Parametro contexto) throws NegocioExcepcion {
		String rta = null;
		RespuestaApi respuesta = new RespuestaApi();
		try {
			
				rta = ejbSysmanUtil.consultarParametro(contexto.getCompania(), contexto.getNombreParametro(), contexto.getAplicacion(), new Date(), true);

				
			if(rta == null) {
				respuesta.setCodigo(5);
				respuesta.setMensaje("No se encontraron datos para el parámetro: " + contexto.getNombreParametro() + " o no existe.");
                LOG.info("No se encontraron datos para el parámetro: {}", contexto.getNombreParametro());
;
			}else {
				respuesta.setCodigo(0);
				respuesta.setMensaje("OK");
				respuesta.setCuerpo(rta);
			}
			
			

		} catch (SystemException e) {
			LOG.error("Error capturando los datos de parámetros ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_DATOSCLASE.getValue()));
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_DATOSCLASE.getValue())));
			throw error;
		}
		


		return respuesta;
	}
	
	/**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApi generarInformefactura(ParametrosInformeFactura contexto) throws NegocioExcepcion {
		byte[] base64 = null;
		try {
			String compania = contexto.getCompania();
			String codigoEan = " ";
			datosSesion = iniciarSession(compania, false);
			datosSesion.setModulo(moduloSF);
			String reporte = obtenerFormato(compania, contexto.getTipo(), moduloSF);
			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();
			
			if (ejbSysmanUtil.consultarParametro(compania,
                    "SF CODIGO EAN POR CADA TIPO DE COBRO",
                    datosSesion.getModulo(), new Date(), false)
                    .equals("NO"))
		    {
		
		        codigoEan = SysmanFunciones
		                        .nvl(ejbSysmanUtil.consultarParametro(compania,
		                                        "SF CODIGO EAN",
		                                        datosSesion.getModulo(),
		                                        new Date(), false), "")
		                        .toString();
		    }
		    else
		    {
		        Map<String, Object> param = new TreeMap<>();
		        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		        param.put("ANO", contexto.getAnio());
		        param.put(GeneralParameterEnum.CODIGO.getName(), contexto.getTipo());
		        Registro rs = RegistroConverter
		                        .toRegistro(requestManager.get(
		                                        UrlServiceUtil.getInstance()
		                                                        .getUrlServiceByUrlByEnumID(
		                                                        		GeneraArchivoUrlEnum.URL665023
		                                                                                        .getValue())
		                                                        .getUrl(),
		                                        param));
		        codigoEan = rs.getCampos().get("CODIGOEAN").toString();
		    }

			reemplazar.put(GestionAutoservicioEnum.REEM_COMP.getValue(), contexto.getCompania());
			reemplazar.put("anio", contexto.getAnio());
			reemplazar.put("tipoFactura", contexto.getTipo());
			reemplazar.put("facturaInicial", contexto.getFactura());
			reemplazar.put("facturaFinal", contexto.getFactura());
			reemplazar.put("codigoEan", codigoEan);


			parametros.put("PR_NOMBRECOMPANIA",
					datosSesion.getCompaniaIngreso().getNombre());
			parametros.put("PR_NITCOMPANIA",
					datosSesion.getCompaniaIngreso().getNit());

			parametros.put("PR_DIRECCIONCOMPANIA",
					datosSesion.getCompaniaIngreso().getDireccion());
			parametros.put("PR_TELEFONOCOMPANIA",
					datosSesion.getCompaniaIngreso().getTelefono());
			parametros.put("PR_CIUDADCOMPANIA",
					datosSesion.getCompaniaIngreso().getCiudad());
			parametros.put("PR_USUARIO", datosSesion.getUser().getCodigo());

			parametros.put("PR_CUENTABANCO1",
					consultarParametro(compania,"SF BANCO CUENTA 1", moduloSF));
			parametros.put("PR_CUENTABANCO2",
					consultarParametro(compania,"SF BANCO CUENTA 2",moduloSF));
			parametros.put("PR_CUENTABANCO3",
					consultarParametro(compania,"SF BANCO CUENTA 3", moduloSF));
			parametros.put("PR_CUENTABANCO4",
					consultarParametro(compania,"SF BANCO CUENTA 4", moduloSF));
			parametros.put("PR_SF_CPTO_PPAL_PRODESARROLLO", consultarParametro(compania, "SF CONCEPTO PRINCIPAL PRODESARROLLO", moduloSF));
			parametros.put("PR_SF_MANEJA_CODIGO_BARRAS",
					"SI".equalsIgnoreCase(consultarParametro(compania, "SF MANEJA CODIGO DE BARRAS", moduloSF)));
			parametros.put("PR_ENCABEZADO", consultarParametro(compania,"ENCABEZADO FORMATO FACTURA",moduloSF));
			parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
			parametros.put("PR_COPIA", true);
			parametros.put("PR_ORIGINAL", true);

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(datosSesion.getModulo()), reemplazar, parametros,
					datosSesion);

			base64 = JsfUtil.serializarReporteBase64(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					ReportesBean.FORMATOS.PDF, datosSesion);
		} catch (  NumberFormatException | SysmanException | JRException | IOException | SystemException e) {
			LOG.error("Error resolviendo consulta de reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(e.getMessage());
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONSULTA.getValue())));
			throw error;
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(base64);
		return respuesta;

	}
	
	 /**
     * Permite obtener el nombre del formato con el que se desea
     * generar la factura
     *
     * @return nombre del formato a generar
     */
    private String obtenerFormato(String compania, String tipoCobro, String modulo)
    {
        String formato = "";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        params.put("TIPOCOBRO", tipoCobro);

        try
        {
            Registro rs = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(GeneraArchivoUrlEnum.URL665010
                                                                                            .getValue())
                                                            .getUrl(),
                                            params));

            if ((rs != null)
                && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                                "FORMATO_FACTURA"))
            {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else
            {
                formato = consultarParametro(compania,"SF FORMATO FACTURACION", modulo);
            }

        }
        catch ( NegocioExcepcion | SystemException  e)
        {
        	LOG.error("Error resolviendo consulta de reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
        }

        return formato;
    }
    
    /**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApi generarInffacturasPorCobrar(ParametrosInformeFacturasPorCobrar contexto) throws NegocioExcepcion {
		byte[] base64 = null;
		try {
			String compania = contexto.getCompania();
			datosSesion = iniciarSession(compania, false);
			datosSesion.setModulo(moduloSF);
			String reporte = "002909InformeRelacionValoresAdeudados";
			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();

			reemplazar.put(GestionAutoservicioEnum.REEM_COMP.getValue(), contexto.getCompania());
			reemplazar.put("fechaInicial", contexto.getFechaInicial());
			reemplazar.put("fechaFinal", contexto.getFechaFinal());
			
			parametros.put("PR_FECHAINICIAL", contexto.getFechaInicial());
		    parametros.put("PR_FECHAFINAL",contexto.getFechaFinal());
		    parametros.put("PR_NOMBRECOMPANIA", datosSesion.getCompaniaIngreso().getNombre());

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(datosSesion.getModulo()), reemplazar, parametros,
					datosSesion);

			base64 = JsfUtil.serializarReporteBase64(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN,
					(("1").equals(contexto.getTipoFormato())) ? ReportesBean.FORMATOS.PDF:ReportesBean.FORMATOS.EXCEL, 
							datosSesion);
		} catch (  NumberFormatException | SysmanException | JRException | IOException e) {
			LOG.error("Error resolviendo consulta de reporte ->> mensaje ->> {} / causa ->> {}", e.getMessage(),
					e.getCause());
			NegocioExcepcion error = new NegocioExcepcion(e.getMessage());
			error.initCause(new Exception(
					idioma.getString(GestionAutoservicioEnum.MSG_GESTIONAUTOSERVICIO_CONSULTA.getValue())));
			throw error;
		}

		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(base64);
		return respuesta;

	}
	
	/**
	 * 
	 * @param contexto
	 * @return
	 * @throws NegocioExcepcion
	 */
	public RespuestaApi consultarPaginado(Map<String, Object> contexto) {
		List<RespuestaDatosGeneral> lista = new ArrayList<>();
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(SysmanFunciones.toString(contexto.get(GeneralParameterEnum.URL.getName())));

			int pagina = Integer.parseInt(SysmanFunciones.toString(
					contexto.get(GeneralParameterEnum.PAGINICIO.getName())));

			int size = DEFAULT_SIZE;

			if (contexto.get(GeneralParameterEnum.PAGTAMANIO.getName()) != null) {
				int sizeParam = Integer.parseInt(SysmanFunciones.toString(
						contexto.get(GeneralParameterEnum.PAGTAMANIO.getName())));

				if (sizeParam > 0 && sizeParam <= MAX_SIZE) {
					size = sizeParam;
				}
			}

			int offset = (pagina - 1) * size;

			contexto.put(GeneralParameterEnum.PAGINICIO.getName(), offset);
			contexto.put(GeneralParameterEnum.PAGTAMANIO.getName(), size);

			List<Registro> parameters =
					RegistroConverter.toListRegistro(
							requestManager.getList(urlBean.getUrl(), contexto)
							);

			if (parameters != null) {
				for (Registro reg : parameters) {
					RespuestaDatosGeneral r = new RespuestaDatosGeneral();
					r.setCompania(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.COMPANIA.getName())));
					r.setAnio(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.ANO.getName())));
					r.setCodigo(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.CODIGO.getName())));
					r.setNombre(SysmanFunciones.toString(reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName())));
					lista.add(r);
				}
			}
		} catch (SystemException e) {

			JsfUtil.agregarMensajeError(e.getMessage());
		}
		RespuestaApi respuesta = new RespuestaApi();
		respuesta.setCodigo(0);
		respuesta.setMensaje("OK");
		respuesta.setCuerpo(lista);
		return respuesta;

	}
}
