/*-
 * APIFrida.java
 *
 * 1.0
 * 
 * 9/12/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import com.google.gson.Gson;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.rest.enums.APIAutoServicioEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Clase que se encarga de llamar los servicios de FRIDA
 * 
 * @version 1.0, 9/12/2020
 * @author eamaya
 *
 */
public class APIFrida {
	protected ResourceBundle idioma;

	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(APIFrida.class);

	public APIFrida() {
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
	}

	/**
	 * 
	 * @param numeroDocumento
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SysmanException
	 */
	public String cargarDatos(String numeroDocumento, String url)
			throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "contribuyente?numerodocumento=" + numeroDocumento;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaContribuyente respuestaApi = gson.fromJson(response.toString(), RespuestaContribuyente.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}

		return salida;
	}

	public void putContribuyente(String url, String nitCompania, String tipoDocumento, String digitoVerificacion,
			String nombreContribuyente, String correoElectronico, String claveCorreo, String correoEntrante,
			String smtp, String telefono, String direccion, String direccionFiscal, String codigoPostal, String pais,
			String departamento, String municipio, String tipoRegimen, String tipoOrganizacion,
			String responibilidadesFiscales, String responsabilidades, String identificadorSoftware, String pinSoftware,
			String imagenCorreo, String logo, String actividad, String certificado, String passCert, String codigoReporte, String testId) throws MalformedURLException, IOException, SysmanException {

		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "contribuyente?numerodocumento=" + nitCompania;

		ParametrosContribuyente param = new ParametrosContribuyente();

		param.setNumerodocumento(nitCompania);
		param.setTipoidentificacion(tipoDocumento);
		param.setDigitoverificacion(digitoVerificacion);
		param.setNombrecontribuyente(nombreContribuyente);
		param.setCorreoelectronico(correoElectronico);
		param.setClaveCorreo(claveCorreo);
		param.setCorreoEntrante(correoEntrante);
		param.setSmtp(smtp);
		param.setTelefono(telefono);
		param.setDireccion(direccion);
		param.setDireccionfiscal(direccionFiscal);
		param.setCodigopostal(codigoPostal);
		param.setPais(pais);
		param.setCodigodepartamento(departamento);
		param.setCodigomunicipio(municipio);
		param.setTiporegimen(tipoRegimen);
		param.setTipoorganizacion(tipoOrganizacion);
		param.setResponsabilidadesfiscales(responibilidadesFiscales);
		param.setTextoResponsabilidades(responsabilidades);
		param.setIdentificadorsoftware(identificadorSoftware);
		param.setPinsoftware(pinSoftware);
		param.setImgCorreo(imagenCorreo);
		param.setLogo(logo);
		param.setCiiu(actividad);
		param.setCertificado(certificado);
		param.setPassCert(passCert);
		param.setCodigoReporte(codigoReporte);
		param.setTestId(testId);
		
		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosContribuyente.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestMethod("PUT");

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {
			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}
		}

	}

	public void postContribuyente(String url, String nitCompania, String tipoDocumento, String digitoVerificacion,
			String nombreContribuyente, String correoElectronico, String claveCorreo, String correoEntrante,
			String smtp, String telefono, String direccion, String direccionFiscal, String codigoPostal, String pais,
			String departamento, String municipio, String tipoRegimen, String tipoOrganizacion,
			String responibilidadesFiscales, String responsabilidades, String identificadorSoftware, String pinSoftware,
			String imagenCorreo, String logo, String actividad, String certificado, String passCert, String codigoReporte, String testId) throws MalformedURLException, IOException, SysmanException {

		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "contribuyente";

		ParametrosContribuyente param = new ParametrosContribuyente();

		param.setNumerodocumento(nitCompania);
		param.setTipoidentificacion(tipoDocumento);
		param.setDigitoverificacion(digitoVerificacion);
		param.setNombrecontribuyente(nombreContribuyente);
		param.setCorreoelectronico(correoElectronico);
		param.setClaveCorreo(claveCorreo);
		param.setCorreoEntrante(correoEntrante);
		param.setSmtp(smtp);
		param.setTelefono(telefono);
		param.setDireccion(direccion);
		param.setDireccionfiscal(direccionFiscal);
		param.setCodigopostal(codigoPostal);
		param.setPais(pais);
		param.setCodigodepartamento(departamento);
		param.setCodigomunicipio(municipio);
		param.setTiporegimen(tipoRegimen);
		param.setTipoorganizacion(tipoOrganizacion);
		param.setResponsabilidadesfiscales(responibilidadesFiscales);
		param.setTextoResponsabilidades(responsabilidades);
		param.setIdentificadorsoftware(identificadorSoftware);
		param.setPinsoftware(pinSoftware);
		param.setImgCorreo(imagenCorreo);
		param.setLogo(logo);
		param.setCiiu(actividad);
		param.setCertificado(certificado);
		param.setPassCert(passCert);
		param.setCodigoReporte(codigoReporte);
		param.setTestId(testId);
		
		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosContribuyente.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {
			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}
		}

	}

	public String cargarRangoFacturacion(String numeroDocumento, String url)
			throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "rangofacturacion?numeroContribuyente=" + numeroDocumento;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaRangoFacturacion respuestaApi = gson.fromJson(response.toString(),
					RespuestaRangoFacturacion.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}

		return salida;

	}

	public void postRangoFacturacion(String url, String nitCompania, String prefijo, String numeroRes, String claveTec,
			String rangoIni, String rangoFin, String fechaDesde, String fechaHasta, String tipoAmbiente)
			throws IOException, SysmanException {

		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "rangofacturacion";

		ParametrosRangoFacturacion param = new ParametrosRangoFacturacion();

		param.setContribuyente(nitCompania);
		param.setPrefijo(prefijo);
		param.setNumeroresolucion(numeroRes);
		param.setClavetecnica(claveTec);
		param.setRangoinicial(rangoIni);
		param.setRangofinal(rangoFin);
		param.setFechadesde(fechaDesde);
		param.setFechahasta(fechaHasta);
		param.setIdFeTipoAmbiente(tipoAmbiente);
		param.setId("0");

		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosRangoFacturacion.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}
		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {
			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}
		}

	}

	public void putRangoFacturacion(String url, String nitCompania, String id, String prefijo, String numeroRes,
			String claveTec, String rangoIni, String rangoFin, String fechaDesde, String fechaHasta,
			String tipoAmbiente) throws IOException, SysmanException {

		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "rangofacturacion";

		ParametrosRangoFacturacion param = new ParametrosRangoFacturacion();

		param.setContribuyente(nitCompania);
		param.setPrefijo(prefijo);
		param.setNumeroresolucion(numeroRes);
		param.setClavetecnica(claveTec);
		param.setRangoinicial(rangoIni);
		param.setRangofinal(rangoFin);
		param.setFechadesde(fechaDesde);
		param.setFechahasta(fechaHasta);
		param.setIdFeTipoAmbiente(tipoAmbiente);
		param.setId(id);

		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosRangoFacturacion.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestMethod("PUT");

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {
			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}
		}

	}

	public String cargarEnvioFacatura(String nitCompania, String url)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "enviofactura?numeroContribuyente=" + nitCompania;
		connection = (HttpURLConnection) new URL(url).openConnection();
		

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaEnvioFactura respuestaApi = gson.fromJson(response.toString(), RespuestaEnvioFactura.class);

			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				//throw new SysmanException(msg);
				return msg;
			}

			else {
				salida = response.toString();
			}

		}

		return salida;
	}

	public String cargarEnvioFacatura(String nitCompania, String numeroFactura, String prefijo, String url)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "enviofactura?numeroContribuyente=" + nitCompania + "&numeroFactura=" + numeroFactura
				+ "&tipoFormato=01" + "&prefijo=" + prefijo;

		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			ParametrosEnvioFacturaFiltros respuestaApi = gson.fromJson(response.toString(),
					ParametrosEnvioFacturaFiltros.class);

			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {
				salida = response.toString();
			}

		}

		return salida;
	}

	public String cargarEnvioFacatura(String nitCompania, String tipoFormato, String numeroFactura, String prefijo, String url)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "enviofactura?numeroContribuyente=" + nitCompania + "&numeroFactura=" + numeroFactura
				+ "&tipoFormato="+ tipoFormato + "&prefijo=" + prefijo;

		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			ParametrosEnvioFacturaFiltros respuestaApi = gson.fromJson(response.toString(),
					ParametrosEnvioFacturaFiltros.class);

			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {
				salida = response.toString();
			}

		}

		return salida;
	}

	public String cargarFormatoConsultarReporte(String url, String nitCompania, String tipoConsulta,
			String numeroFactura, String estado, String fechaInicio, String fechaFin, String facturas,
			String notasDebito, String notasCredito) throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "formato/consultarReporte?tipoConsulta=" + tipoConsulta + "&numFormato=" + numeroFactura
				+ "&estado=" + estado + "&fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin + "&fact=" + facturas
				+ "&nd=" + notasDebito + "&nc=" + notasCredito + "&numContribuyente=" + nitCompania;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaConsultarReporte respuestaApi = gson.fromJson(response.toString(),
					RespuestaConsultarReporte.class);

			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {
				salida = response.toString();
			}

		}

		return salida;
	}

	/**
	 * Este metodo se sobre carga ya que se adiciona un parametro mas que es docSoporte,
	 * esto debido a que en el consumo del servicio se debe pasar el parametro ds
	 * mas no el fact, entonces para no afectar el proceso de facturacion que
	 * actualmente se lleva a cabo.
	 * 
	 * ljdiaz - se agrega el parametro notas de ajuste que corersponden a los documentos soporte.
	 * @param url
	 * @param nitCompania
	 * @param tipoConsulta
	 * @param numeroFactura
	 * @param estado
	 * @param fechaInicio
	 * @param fechaFin
	 * @param facturas
	 * @param notasDebito
	 * @param notasCredito
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SysmanException
	 */
	public String cargarFormatoConsultarReporte(String url, String nitCompania, String tipoConsulta,
			String numeroFactura, String estado, String fechaInicio, String fechaFin, String facturas,
			String notasDebito, String notasCredito, String docSoporte, String notaAjusteDs)
			throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "formato/consultarReporte?tipoConsulta=" + tipoConsulta + "&numFormato=" + numeroFactura
				+ "&estado=" + estado + "&fechaInicio=" + fechaInicio + "&fechaFin=" + fechaFin + "&ds=" + docSoporte
				+"&na=" + notaAjusteDs + "&nd=" + notasDebito + "&nc=" + notasCredito + "&numContribuyente=" + nitCompania;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaConsultarReporte respuestaApi = gson.fromJson(response.toString(),
					RespuestaConsultarReporte.class);

			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {
				salida = response.toString();
			}

		}

		return salida;
	}

	public String postFormatoConsultas(String url, String nitCompania, String clase, String numeroFactura,
			String prefijoFactura, String nombreCertificado, String certificado, String passCertificado)
			throws IOException, SysmanException {

		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "formato/consultas";

		ParametrosFormatoConsultas param = new ParametrosFormatoConsultas();

		param.setTipoDocumento(clase);
		param.setNumDocumento(numeroFactura);
		param.setPrefijo(prefijoFactura);
		param.setNombreCertificado(nombreCertificado);
		param.setCertificado(certificado);
		param.setPassCertificado(passCertificado);
		param.setNumContribuyente(nitCompania);

		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosFormatoConsultas.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		System.out.println(response);
		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaFormatoConsultas respuestaApi = gson.fromJson(response.toString(),
					RespuestaFormatoConsultas.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}

		return salida;
	}

	public String postContribuyentePruebaCorreo(String nitCompania, String url, String destinatario)
			throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "contribuyente/pruebaCorreo";

		ParametrosPruebaCorreo param = new ParametrosPruebaCorreo();

		param.setContribuyente(nitCompania);

		param.setDestinatario(destinatario);

		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosPruebaCorreo.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}

		return salida;
	}

	public String postRangoFacturacionDian(String url, String nitCompania, String nombreCertificado, String certificado,
			String passCertificado) throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "rangofacturacion/dian";

		ParametrosRangoFacturacionDian param = new ParametrosRangoFacturacionDian();

		param.setNombreCertificado(nombreCertificado);

		param.setCertificado(certificado);

		param.setPassCertificado(passCertificado);

		param.setNumContribuyente(nitCompania);

		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosRangoFacturacionDian.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaRangoFactDian respuestaApi = gson.fromJson(response.toString(), RespuestaRangoFactDian.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}
		return salida;
	}

	public String postEnvioFactura(String url, String json) throws MalformedURLException, IOException, SysmanException {
		String salida = null;

		String msg = "";
		StringBuffer response = null;

		Gson gson = new Gson();
		url = url + "enviofactura";
		URL tempURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) tempURL.openConnection();

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setDoInput(true);
		connection.setDoOutput(true);

		String jsonInputString = json;
		try (OutputStream os = connection.getOutputStream()) {
			os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
			os.flush();
		}

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			salida = response.toString();

		}
		return salida;

	}

	public String postReenvioCorreoFactura(String url, String json) throws MalformedURLException, IOException, SysmanException {
		String salida = null;

		String msg = "";
		StringBuffer response = null;

		Gson gson = new Gson();
		url = url + "formato/correos";
		URL tempURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) tempURL.openConnection();

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setDoInput(true);
		connection.setDoOutput(true);

		String jsonInputString = json;
		try (OutputStream os = connection.getOutputStream()) {
			os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
			os.flush();
		}

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			salida = response.toString();

		}
		return salida;

	}
	
	public String cargarTercero(String nitCompania, String tercero, String url)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;

		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "tercero?numcontribuyente=" + nitCompania + "&numerodocumento=" + tercero;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			salida = response.toString();

		}

		return salida;
	}

	public String postTercero(String url, String json)
			throws MalformedURLException, IOException, SysmanException, RuntimeException {
		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "tercero";

		Gson gson = new Gson();

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	public String cargarItem(String url, String codigoProducto)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "item?codigoProducto=" + codigoProducto;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);

			salida = response.toString();
		}

		return salida;

	}

	public String postItem(String url, String json) throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "item";

		Gson gson = new Gson();

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}
		return salida;
	}

	public String deleteEnvioFactura(String url, String json)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "enviofactura";
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setRequestMethod("DELETE");
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);

		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = "!" + msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			// throw new RuntimeException(msg);
			return msg;
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = "!" + idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			// throw new SysmanException(msg);
			return msg;
		} else {
			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = "!" + msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				// throw new SysmanException(msg);
				return msg;
			}

			else {

				salida = response.toString();

			}

		}
		return salida;
	}

	public String postFormatoLegalizar(String url, String json)
			throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "formato/legalizarFactura";

		Gson gson = new Gson();

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				
				salida = response.toString(); //MOD JM CC 3372
				//throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	public String postGestionApiReporte(String json)
			throws MalformedURLException, IOException, SysmanException, RuntimeException {
		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		String url = "http://150.136.157.87:7556/sysman-erp-servicio-reporte/servicio/gestionEjecucionApiReporte";

		Gson gson = new Gson();

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	public String putTercero(String url, String json)
			throws MalformedURLException, IOException, SysmanException, RuntimeException {
		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "tercero";

		Gson gson = new Gson();

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestMethod(APIAutoServicioEnum.PUT.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaApi respuestaApi = gson.fromJson(response.toString(), RespuestaApi.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			//	throw new SysmanException(msg);
				return msg;
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	public String consultarTodasLasNominas(String url, String tipoNomina, String pagInicio, String pagTamanio,
			boolean ignorarPaginado, String fechaIni, String fechaFin, String nitEmpleador)
			throws MalformedURLException, IOException, SysmanException {

		String salida = null;
		Gson gson = new Gson();
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;

		url = url + "envioNominaElectronica/pagNomina?tipoNomina=" + tipoNomina + "&pagInicio=" + pagInicio
				+ "&pagTamanio=" + pagTamanio + "&ignorarPaginado=" + ignorarPaginado + "&fechaIni=" + fechaIni
				+ "&fechaFin=" + fechaFin + "&nitEmpleador=" + nitEmpleador;
		connection = (HttpURLConnection) new URL(url).openConnection();

		connection.setDoOutput(true);
		connection.setRequestMethod("GET");

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaConsultarReporte respuestaApi = gson.fromJson(response.toString(),
					RespuestaConsultarReporte.class);

			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {
				salida = response.toString();
			}

		}

		return salida;
	}

	public String consultarEstadoNomina(String url, String codigoTipoNomina, String fechaGenNie008,
			String numeroDocumentoNie045, String numero, String nitEmpleador, String usuarioAccion, String passCert,
			String testID, String nombreCertificado, String certBase64) throws IOException, SysmanException {

		String salida = null;
		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "formato/consultas";

		ParametrosConsultarNomina param = new ParametrosConsultarNomina();

		param.setCodigoTipoNomina(codigoTipoNomina);
		param.setFechaGenNie008(fechaGenNie008);
		param.setNumeroDocumentoNie045(numeroDocumentoNie045);
		param.setNombreCertificado(numero);
		param.setNitEmpleador(nitEmpleador);
		param.setUsuarioAccion(usuarioAccion);
		param.setPassCert(passCert);
		param.setTestID(testID);
		param.setNombreCertificado(nombreCertificado);
		param.setCertBase64(certBase64);

		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosConsultarNomina.class);

		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes());
		os.flush();
		os.close();
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}
		System.out.println(response);
		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			RespuestaFormatoConsultas respuestaApi = gson.fromJson(response.toString(),
					RespuestaFormatoConsultas.class);
			if (respuestaApi.getCodigo() != 0) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApi.getMensaje());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				throw new SysmanException(msg);
			}

			else {

				salida = response.toString();

			}

		}

		return salida;
	}
	
	/**
     * Metodo por el cual se obtiene el XML de un documento ya enviado.
     * @autor Luis Jacobo Diaz Muñoz
     */
	public String getXmlDocumento(String url, String contribuyente, String numDocumento, String prefijo, String tipoFormato) throws MalformedURLException, IOException, SysmanException {
		String salida = null;
		String msg = "";
		StringBuffer response = null;
		
		url = url + "enviofactura/obtenerXml?tipoDocumento="+tipoFormato+"&numDocumento="+numDocumento+"&numContribuyente="+contribuyente+"&prefijo="+prefijo;
		
		URL tempURL = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) tempURL.openConnection();

		connection = (HttpURLConnection) new URL(url)
                .openConnection();
		connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        
		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new RuntimeException(msg);
		}

		BufferedReader br = new BufferedReader(
				new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
		String output;
		response = new StringBuffer();
		while ((output = br.readLine()) != null) {
			response.append(output);
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

			throw new SysmanException(msg);
		} else {

			salida = response.toString();

		}
		return salida;

	}
}
