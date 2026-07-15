/*-
 * ApiPayRoll.java
 *
 * 1.0
 * 
 * 28/10/2021
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Clase que se encarga de llamar los servicios de PayRoll (Nomina
 * Electr&oacute;nica
 * 
 * @version 1.0, 28/10/2021
 * @author mzanguna
 *
 */
public class ApiPayRoll {

    protected ResourceBundle idioma;

    public ApiPayRoll() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(APIFrida.class);

    public String postNominaElectronica(String url,
        String json)
                    throws IOException, SysmanException {
        String salida = null;
        HttpURLConnection connection = null;

        String msg = "";
        StringBuffer response = null;

        url = new StringBuilder(url).append("envioNominaElectronica")
                        .toString();

        connection = (HttpURLConnection) new URL(url)
                        .openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty(
                        APIAutoServicioEnum.CONTENT_TYPE.getValue(),
                        APIAutoServicioEnum.APPLICATIONSJON.getValue());

        connection.setRequestProperty(
                        APIAutoServicioEnum.ACCEPT.getValue(),
                        APIAutoServicioEnum.APPLICATIONSJON.getValue());

        connection.setRequestProperty(APIAutoServicioEnum.METHOD.getValue(),
                        APIAutoServicioEnum.POST.getValue());

        OutputStream os = connection.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            msg = idioma.getString(
                            APIAutoServicioEnum.MSG_APIAUTOSERVICIO_URLERRADA
                                            .getValue())
                            .toString();
            msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_URL.getValue(),
                            url);
            msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(),
                            "" + connection.getResponseCode());
            LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue()
                + this.getClass());
            throw new RuntimeException(msg);
        }

        BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                        (connection.getInputStream()),
                                        StandardCharsets.UTF_8));
        String output;
        response = new StringBuffer();
        while ((output = br.readLine()) != null) {
            response.append(output);
        }

        if (response.toString() == null) {
            msg = idioma.getString(
                            APIAutoServicioEnum.MSG_APIAUTOSERVICIO_SERVICIONULL
                                            .getValue())
                            .toString();
            LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue()
                + this.getClass());
            throw new SysmanException(msg);
        }
        else {
            Gson gson = new Gson();
            RespuestaApi respuestaApi = gson.fromJson(
                            response.toString(),
                            RespuestaApi.class);            
            
            if (respuestaApi.getCodigo() != 0) {
                msg = idioma.getString(
                                APIAutoServicioEnum.MSG_APIAUTOSERVICIO_CONECCION
                                                .getValue())
                                .toString();
                msg = msg.replace(APIAutoServicioEnum.REEMPLAZO_CONE.getValue(),
                                respuestaApi.getMensaje());
                LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue()
                    + this.getClass());
                throw new SysmanException(msg);
            }
            else {
            	
				RespuestaCuerpo cuerpo = gson.fromJson(gson.toJson(respuestaApi.getCuerpo()), RespuestaCuerpo.class);
				
				if ( !cuerpo.getValido() ) {
				    String erroresCuerpo = "";
			        
			        if(!cuerpo.getAcumuladoErrores().isEmpty()) {
			        	for(String error : cuerpo.getAcumuladoErrores()) {
			        		erroresCuerpo += error;
			        	}
			        }
			        
			        msg = erroresCuerpo;
			        LOG.error(msg + APIAutoServicioEnum.REEMPLZAO_SIG.getValue()
			            + this.getClass());
			        throw new SysmanException(msg);
				}
				else {
					salida = response.toString();		            
				}	            	            	
            }            
        }
        return salida;

    }

}
