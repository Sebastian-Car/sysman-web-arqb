package com.sysman.util.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.rest.enums.APIAutoServicioEnum;

public class ApiNominaElectronica {
	protected ResourceBundle idioma;

	/**
	 * Constante que representa la instancia del Log
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(APIFrida.class);

	public ApiNominaElectronica() {
		idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
	}
	
	///crudProveedorServicio
	
	public void putProveedorNE(String url, String nitCompania, String ambiente, String usuario) throws MalformedURLException, IOException, SysmanException {

		HttpURLConnection connection = null;

		String msg = "";
		StringBuffer response = null;

		url = url + "crudProveedorServicio/crud";

		ParametrosEmpleadoresNominaElectronica param = new ParametrosEmpleadoresNominaElectronica();

		param.setNit(nitCompania);
		param.setAmbiente(ambiente.equals("0")?"1":"2");
		param.setVersionResolucion("2");
		param.setModifiedBy(usuario);
		
		Gson gson = new Gson();
		String json = gson.toJson(param, ParametrosEmpleadoresNominaElectronica.class);

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
}
