/*-
 * APISIGEC.java
 *
 * 1.0
 * 
 * 05/03/2024
 * 
 * Copyright (c) 2016 Sysman.
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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ResourceBundle;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Clase que se encarga de llamar los servicios de SIGEC
 * 
 * @version 1.0, 27/03/2024
 * @author mrosero
 *
 */
public class APISIGEC {
	protected ResourceBundle idioma;

	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(APISIGEC.class);

	public APISIGEC() {
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
	}

	public String postActoDocumento(String token, String url, String json) throws IOException, SysmanException {
		String salida = null;
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;
		String output;

		url = url + "/interoperabilityActoDocumento/registrar";

		Gson gson = new Gson();
		try {
			// parametro para habilitar o no el certificado
			disableCertificate();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.AUTHORIZATION.getValue(), token);
		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
		os.flush();
		os.close();

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
				&& (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
						|| connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)) {

			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

		}

		if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
				|| connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {

			BufferedReader br = new BufferedReader(
					new InputStreamReader((connection.getErrorStream()), StandardCharsets.UTF_8));

			response = new StringBuffer();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
		} else {
			BufferedReader br = new BufferedReader(
					new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));

			response = new StringBuffer();
			
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {

			RespuestaApiSigec respuestaApiSigec = gson.fromJson(response.toString(), RespuestaApiSigec.class);
			if (respuestaApiSigec.getHttpstatus() != HttpURLConnection.HTTP_OK) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApiSigec.getMessage());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				return msg;
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	public String postLiquidacion(String token, String url, String json) throws IOException, SysmanException {
		String salida = null;
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;
		String output;

		url = url + "/interoperabilityLiquidacion/registrar";

		Gson gson = new Gson();
		try {
			// parametro para habilitar o no el certificado
			disableCertificate();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.AUTHORIZATION.getValue(), token);
		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
		os.flush();
		os.close();

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
				&& (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
						|| connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)) {

			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

		}

		if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
				|| connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {

			BufferedReader br = new BufferedReader(
					new InputStreamReader((connection.getErrorStream()), StandardCharsets.UTF_8));

			response = new StringBuffer();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
		} else {
			BufferedReader br = new BufferedReader(
					new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));

			response = new StringBuffer();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {

			RespuestaApiSigec respuestaApiSigec = gson.fromJson(response.toString(), RespuestaApiSigec.class);
			if (respuestaApiSigec.getHttpstatus() != HttpURLConnection.HTTP_OK) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApiSigec.getMessage());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				return msg;
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	public String postPago(String token, String url, String json) throws IOException, SysmanException {
		String salida = null;
		HttpURLConnection connection = null;
		String msg = "";
		StringBuffer response = null;
		String output;

		url = url + "/interoperabilityPago/registrar";

		Gson gson = new Gson();
		try {
			// parametro para habilitar o no el certificado
			disableCertificate();
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestProperty(APIAutoServicioEnum.CONTENT_TYPE.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());
		connection.setRequestProperty(APIAutoServicioEnum.AUTHORIZATION.getValue(), token);
		connection.setRequestProperty(APIAutoServicioEnum.ACCEPT.getValue(),
				APIAutoServicioEnum.APPLICATIONSJON.getValue());

		connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(), APIAutoServicioEnum.POST.getValue());

		OutputStream os = connection.getOutputStream();
		os.write(json.getBytes(APIAutoServicioEnum.UTF_8.getValue()));
		os.flush();
		os.close();

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK
				&& (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
						|| connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)) {

			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA.getValue()).toString();
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(), url);
			msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), "" + connection.getResponseCode());
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());

		}

		if (connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST
				|| connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {

			BufferedReader br = new BufferedReader(
					new InputStreamReader((connection.getErrorStream()), StandardCharsets.UTF_8));
			response = new StringBuffer();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
		} else {
			BufferedReader br = new BufferedReader(
					new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));

			response = new StringBuffer();
			while ((output = br.readLine()) != null) {
				response.append(output);
			}
		}

		if (response.toString() == null) {
			msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL.getValue()).toString();
			LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
			throw new SysmanException(msg);
		} else {

			RespuestaApiSigec respuestaApiSigec = gson.fromJson(response.toString(), RespuestaApiSigec.class);
			if (respuestaApiSigec.getHttpstatus() != HttpURLConnection.HTTP_OK) {
				msg = idioma.getString(APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION.getValue()).toString();
				msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(), respuestaApiSigec.getMessage());
				LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue() + this.getClass());
				return msg;
			}

			else {

				salida = response.toString();

			}

		}
		return salida;

	}

	private void disableCertificate() throws NoSuchAlgorithmException, KeyManagementException {

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	}

}
