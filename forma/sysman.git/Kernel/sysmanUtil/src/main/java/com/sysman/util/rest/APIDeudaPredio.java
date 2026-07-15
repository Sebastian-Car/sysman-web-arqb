/*-
 * CalculoPredial.java
 *
 * 1.0
 * 
 * 15/05/2020
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
import java.util.ResourceBundle;

/**
 * Clase que se encarga de llamar el servicio que carga la deuda de un
 * codigo de predio
 * 
 * @version 1.0, 15/05/2020
 * @author eamaya
 *
 */
public class APIDeudaPredio {

    protected ResourceBundle idioma;

    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(APIAutoServicio.class);

    public APIDeudaPredio() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    public String cargarDatos(String compania,
        String codigoPredio,
        int descuentoLey,
        String usuario,
        String url) throws MalformedURLException, IOException, SysmanException {

        String salida;

        HttpURLConnection connection = null;
        String msg = "";
        StringBuffer response = null;
        DeudaPredio param = new DeudaPredio();

        param.setIdCompania(compania);
        param.setCodigoPredio(codigoPredio);
        param.setDescuentoley(descuentoLey);
        param.setUser(usuario);

        Gson gson = new Gson();
        String json = gson.toJson(param, DeudaPredio.class);

        url = url + armarUrl(json);
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

            RespuestaDeudaPredio respuestaApi = gson.fromJson(
                            response.toString(),
                            RespuestaDeudaPredio.class);
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

                salida = response.toString();

            }

        }
        return salida;
    }

    private String armarUrl(String json) {

        return "?"
            + json.replaceAll("[\\{}]", "").replace(",", "&").replace("\"", "")
                            .replace(":", "=");
    }

    public String financiar(String compania,
        String codigoPredio,
        int[] aniosFinanciar,
        int indAplicaLey1175,
        String usuario,
        String url) throws MalformedURLException, IOException, SysmanException {

        String salida = null;

        HttpURLConnection connection = null;

        String msg = "";
        StringBuffer response = null;
        FinanciarPredio param = new FinanciarPredio();

        param.setIdCompania(compania);
        param.setCodPredio(codigoPredio);
        param.setAniosFinanciar(aniosFinanciar);
        param.setIndAplicaLey1175(indAplicaLey1175);
        param.setUser(usuario);

        Gson gson = new Gson();
        String json = gson.toJson(param, FinanciarPredio.class);

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
        }

        return salida;
    }

}
