/*-
 * APIAutoServicio.java
 *
 * 1.0
 * 
 * 25/09/2018
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import org.castor.core.util.Base64Decoder;

import io.gsonfire.builders.JsonArrayBuilder;

/**
 * Conector Generico para cliente API Resfull
 * 
 * @author
 * 
 * 
 */
public class APIPdf {

    protected ResourceBundle idioma;

    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(APIPdf.class);

    /**
     * constructor para cargar los mensajes
     */
    public APIPdf() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    /**
     * 
     * Consume el servicio de autoservicio propio
     * 
     * @author jgomez
     * @param compania
     * @param entidad
     * @param clase
     * @param cedula
     * @param proceso
     * @param ano
     * @param mes
     * @param periodo
     * @param observacion
     * @param url
     * @return el archivo serializado para descargar
     * 
     * @throws MalformedURLException
     * @throws IOException
     * @throws SysmanException
     */
    @SuppressWarnings("unchecked")
    public ByteArrayInputStream convertirPdf(String compania,
        String nombreArchivo,
        String archivoBase, String url)
                    throws MalformedURLException, IOException, SysmanException {
        ByteArrayInputStream bsal = null;
        HttpURLConnection connection = null;
        StringBuffer response = null;
        String msg = "";

        ParametrosConvertirPdf param = new ParametrosConvertirPdf();
        param.setNombreArchivo(nombreArchivo);
        param.setArchivoBase64(archivoBase);

        Gson gson = new Gson();
        String json = gson.toJson(param, ParametrosConvertirPdf.class);

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
                                        (connection.getInputStream())));
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
            RespuestaAutoServicio respuestaApi = gson.fromJson(
                            response.toString(),
                            RespuestaAutoServicio.class);
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

                Base64Decoder decoder = new Base64Decoder();

                @SuppressWarnings("static-access")
                byte[] decodedBytes = decoder
                                .decode(respuestaApi.getCuerpo().toString());
                bsal = new ByteArrayInputStream(decodedBytes);
            }
        }
        return bsal;
    }

    public String cargarItem(String url)
                    throws MalformedURLException, IOException, SysmanException {
        String salida = null;
        Gson gson = new Gson();
        JsonArrayBuilder json;
        HttpURLConnection connection = null;
        String msg = "";
        StringBuffer response = null;
        connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setDoOutput(true);
        connection.setRequestMethod("GET");

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
                        new InputStreamReader((connection.getInputStream()),
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

            salida = response.toString();
        }

        return salida;

    }

}