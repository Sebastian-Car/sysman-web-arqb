/*-
 * FrmReporteAuditoriaControlador.java
 *
 * 1.0
 * 
 * 13/04/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;

import com.google.gson.Gson;
import com.sysman.auditoria.ReporteAuditoria;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GeneralParametrosEnum;
import com.sysman.general.enums.FrmReporteAuditoriaControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 13/04/2026
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmReporteAuditoriaControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	//<DECLARAR_ATRIBUTOS>
	private String tabla;
	private String proceso;
	private Date fechaFinal;
	private Date fechaInicial;
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
	private RegistroDataModelImpl listaTabla;
	private RegistroDataModelImpl listaProceso;
	private String codProceso;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmReporteAuditoriaControlador
	 */
	public FrmReporteAuditoriaControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario=2580;
			validarPermisos();
			//<INI_ADICIONAL>
			fechaInicial = fechaFinal = new Date();
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
		cargarListaTabla(); 
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
	/**
	 * 
	 * Carga la lista listaTabla
	 *
	 */
	public void cargarListaTabla(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmReporteAuditoriaControladorUrlEnum.URL1991003.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTabla = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.NOMBRE_TABLA.getName());
	}
	/**
	 * 
	 * Carga la lista listaProceso
	 *
	 */
	public void cargarListaProceso(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmReporteAuditoriaControladorUrlEnum.URL1991005.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put("TABLA", tabla);

		listaProceso = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Excel
	 * en la vista
	 *
	 *
	 */
	public void oprimirExcel() {
		//<CODIGO_DESARROLLADO>
		generarReporte("XLSX", "ReporteAuditoria.xlsx", 
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Pdf
	 * en la vista
	 *
	 *
	 */
	public void oprimirPdf() {
		//<CODIGO_DESARROLLADO>
		generarReporte("PDF", "ReporteAuditoria.pdf", "application/pdf");
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton Txt
	 * en la vista
	 *
	 *
	 */
	public void oprimirTxt() {
		//<CODIGO_DESARROLLADO>
		generarReporte("TXT", "ReporteAuditoria.txt", "text/plain");
		//</CODIGO_DESARROLLADO>
	}

	private void generarReporte(String tipoReporte, String nombreArchivo, String contentType) {
		archivoDescarga = null; 
		try {
			// URL del parámetro de sistema
			String urlReporte = JsfUtil.obtenerParametrosGeneral(GeneralParametrosEnum.URL_REPORTE_AUDITORIA.getName());
			if (urlReporte == null || urlReporte.isEmpty()) {
				JsfUtil.agregarMensajeError("No está configurada la URL del reporte de auditoría");
				return;
			}

			// Construir entidad
			String codEntidad = SessionUtil.getCompaniaIngreso().getCodigo()
					+ "_" + SessionUtil.getCompaniaIngreso().getSigla();

			ReporteAuditoria reporteAuditoria = new ReporteAuditoria(
					codEntidad,
					codProceso != null ? codProceso : "",
							"",
							SysmanFunciones.convertirAFechaCadena(fechaInicial),
							SysmanFunciones.convertirAFechaCadena(fechaFinal),
							tipoReporte
					);

			// Llamar servicio y obtener bytes
			byte[] bytes = consumirServicioReporte(urlReporte, reporteAuditoria);

			if (bytes == null || bytes.length == 0) {
				JsfUtil.agregarMensajeError("El servicio no retornó datos");
				return;
			}

			// Usar el utilitario estándar del proyecto
			archivoDescarga = JsfUtil.getArchivoDescarga(
					new ByteArrayInputStream(bytes),
					nombreArchivo,
					contentType);

		} catch (Exception e) {
			logger.error("[ReporteAuditoria] Error generando reporte " + tipoReporte, e);
			JsfUtil.agregarMensajeError("Error generando el reporte: " + e.getMessage());
		}
	}

	/**
	 * Llama al servicio REST de auditoría y retorna los bytes del archivo generado.
	 */
	private byte[] consumirServicioReporte(String urlReporte,
			ReporteAuditoria reporteAuditoria) throws IOException {

		Gson gson = new Gson();
		String json = gson.toJson(reporteAuditoria);
		logger.info("[ReporteAuditoria] Request URL: " + urlReporte);
		logger.info("[ReporteAuditoria] Request Body: " + json);

		URL url = new URL(urlReporte);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		try {
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			try (java.io.OutputStream os = connection.getOutputStream()) {
				os.write(json.getBytes(StandardCharsets.UTF_8));
				os.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("[ReporteAuditoria] Response code: " + responseCode);

			if (responseCode >= 200 && responseCode < 300) {
				byte[] bytes;
				try (InputStream is = connection.getInputStream()) {
					bytes = leerBytes(is);
				}

				String respuestaJson = new String(bytes, StandardCharsets.UTF_8);

				// Parsear respuesta del servicio
				java.lang.reflect.Type type =
						new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
						Map<String, Object> respuesta = gson.fromJson(respuestaJson, type);

						Object codigo  = respuesta.get("codigo");
						Object cuerpo  = respuesta.get("cuerpo");
						Object mensaje = respuesta.get("mensaje");

						logger.info("[ReporteAuditoria] Codigo respuesta servicio: " + codigo);

						// Validar error interno del servicio
						if (cuerpo == null) {
							String msgError = mensaje != null
									? String.valueOf(mensaje)
											: "Error interno del servicio de auditoría (código: " + codigo + ")";
							throw new IOException(msgError);
						}

						// Decodificar Base64 del cuerpo
						String base64 = String.valueOf(cuerpo);
						byte[] archivoByte = java.util.Base64.getDecoder().decode(base64);

						logger.info("[ReporteAuditoria] Archivo decodificado, tamaño: " 
								+ archivoByte.length + " bytes");

						return archivoByte;
			} else {
				try (InputStream es = connection.getErrorStream()) {
					if (es != null) {
						String error = new String(leerBytes(es), StandardCharsets.UTF_8);
						logger.error("[ReporteAuditoria] Error HTTP: " + error);
					}
				}
				throw new IOException("Servicio retornó código HTTP: " + responseCode);
			}
		} finally {
			connection.disconnect();
		}
	}

	/**
	 * Lee todos los bytes de un InputStream compatible con Java 1.8
	 */
	private byte[] leerBytes(InputStream inputStream) throws IOException {
		java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
		byte[] chunk = new byte[8192];
		int bytesRead;
		while ((bytesRead = inputStream.read(chunk)) != -1) {
			buffer.write(chunk, 0, bytesRead);
		}
		return buffer.toByteArray();
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaTabla
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaTabla(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tabla = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE_TABLA.getName()));
		cargarListaProceso();
		proceso = null;
		codProceso = null;

	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProceso
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProceso(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proceso = SysmanFunciones.toString(registroAux.getCampos().get("ACCION"));
		codProceso = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.PROCESOJUD.getName()));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tabla
	 * 
	 * @return  tabla
	 */
	public String getTabla() {
		return tabla;
	}
	/**
	 * Asigna la variable  tabla
	 * 
	 * @param  tabla
	 * Variable a asignar en  tabla
	 */
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	/**
	 * Retorna la variable proceso
	 * 
	 * @return  proceso
	 */
	public String getProceso() {
		return proceso;
	}
	/**
	 * Asigna la variable  proceso
	 * 
	 * @param  proceso
	 * Variable a asignar en  proceso
	 */
	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
	/**
	 * @return the fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * @param fechaFinal the fechaFinal to set
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	/**
	 * @return the fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * @param fechaInicial the fechaInicial to set
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
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
	/**
	 * @return the listaTabla
	 */
	public RegistroDataModelImpl getListaTabla() {
		return listaTabla;
	}
	/**
	 * @param listaTabla the listaTabla to set
	 */
	public void setListaTabla(RegistroDataModelImpl listaTabla) {
		this.listaTabla = listaTabla;
	}
	/**
	 * @return the listaProceso
	 */
	public RegistroDataModelImpl getListaProceso() {
		return listaProceso;
	}
	/**
	 * @param listaProceso the listaProceso to set
	 */
	public void setListaProceso(RegistroDataModelImpl listaProceso) {
		this.listaProceso = listaProceso;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}
