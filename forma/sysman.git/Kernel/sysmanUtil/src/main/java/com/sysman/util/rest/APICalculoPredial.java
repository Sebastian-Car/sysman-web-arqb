/*-
 * CalculoPredial.java
 *
 * 1.0
 * 
 * 6 ago. 2019
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
 * Clase que se encarga de llamar el servicio que realiza el calculo
 * Predial
 * 
 * @version 1.0, 6 ago. 2019
 * @author eamaya
 *
 */
public class APICalculoPredial {

    protected ResourceBundle idioma;

    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(APIAutoServicio.class);

    public APICalculoPredial() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    public StringBuilder calcular(String compania,
        String nitCompania,
        String fechaCorte,
        int consecutivoReserva,
        boolean indAplicaLey1175,
        boolean aplicaDescuento,
        String codigoInicial,
        String codigoFinal,
        String numeroOrdenIni,
        String numeroOrdenFin,
        String usuario,
        String origen,
        String url) throws MalformedURLException, IOException, SysmanException {

        StringBuilder salida = new StringBuilder();

        HttpURLConnection connection = null;
        String msg = "";
        StringBuffer response = null;
        CalculoPredial param = new CalculoPredial();

        param.setCompania(compania);
        param.setNitCompania(nitCompania);
        param.setFechaCorte(fechaCorte);
        param.setConsecutivoReserva(consecutivoReserva);
        param.setIndAplicaLey1175(indAplicaLey1175);
        param.setAplicaDescuento(aplicaDescuento);
        param.setCodigoInicial(codigoInicial);
        param.setCodigoFinal(codigoFinal);
        param.setNumeroOrdenIni(numeroOrdenIni);
        param.setNumeroOrdenFin(numeroOrdenFin);
        param.setUsuario(usuario);
        param.setOrigen(origen);

        /*
         * GenerarToken generarToken = new GenerarToken(entidad,
         * String.valueOf(clase)); tokenGenerado =
         * generarToken.Base64Hash();
         */
        Gson gson = new Gson();
        String json = gson.toJson(param, CalculoPredial.class);

        connection = (HttpURLConnection) new URL(url)
                        .openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty(
                        APIAutoServicioEnum.CONTENT_TYPE.getValue(),
                        APIAutoServicioEnum.APPLICATIONSJON.getValue());

        /*
         * connection.setRequestProperty(
         * APIAutoServicioEnum.AUTHORIZATION.getValue(),
         * tokenGenerado);
         */

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
            RespuestaCalculoPredial respuestaApi = gson.fromJson(
                            response.toString(),
                            RespuestaCalculoPredial.class);
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

                String predioAnterior = "";
                long codigoAnterior = 0;

                for (RespuestaAlertaPredial respuestaAlertaPredial : respuestaApi
                                .getCuerpo().getAlertas()) {

                    if (!predioAnterior.equals(
                                    respuestaAlertaPredial.getPredio())) {
                        salida.append("Predio: ");
                        salida.append(respuestaAlertaPredial.getPredio());
                        salida.append("\r\n");
                        salida.append("  Codigo: ");
                        salida.append(respuestaAlertaPredial.getCodigo());
                        salida.append("\r\n");

                    }
                    else {
                        if (codigoAnterior != respuestaAlertaPredial
                                        .getCodigo()) {
                            salida.append("  Codigo: ");
                            salida.append(respuestaAlertaPredial.getCodigo());
                            salida.append("\r\n");
                        }
                    }
                    salida.append("     Mensaje: ");
                    salida.append(respuestaAlertaPredial.getMensaje());
                    salida.append("\r\n");

                    predioAnterior = respuestaAlertaPredial.getPredio();
                    codigoAnterior = respuestaAlertaPredial.getCodigo();
                }

            }
        }
        return salida;
    }

}
