/*
 * Request Manager
 *
 * 1.0
 *
 * 12/08/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.api.clientwso2.connectors;

import com.google.gson.internal.Primitives;
import com.sysman.exc.kernel.api.clientwso2.exceptions.ClientWSO2Exception;
import com.sysman.exc.kernel.api.clientwso2.util.enums.ParametersEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.CrudException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.Token;
import com.sysman.kernel.api.clientwso2.config.ClientConfig;
import com.sysman.kernel.api.clientwso2.converters.JsonConverter;
import com.sysman.kernel.api.clientwso2.dbs.DbsDispatcherConfig;
import com.sysman.kernel.api.clientwso2.dbs.ListaConTotal;
import com.sysman.kernel.api.clientwso2.tokens.TokenManager;
import com.sysman.kernel.api.clientwso2.util.enums.HeaderEnum;
import com.sysman.kernel.api.clientwso2.util.enums.HttpMethodEnum;
import com.sysman.kernel.api.commons.util.ObjectUtility;
import com.sysman.kernel.api.commons.util.enums.JsonEnum;
import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.api.commons.util.exceptions.SysmanException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

/**
 * Clase que gestiona lo relacionado con las peticiones a API y
 * envuelve las respuestas en Objetos Registro
 */
public class RequestManager {

    private HttpClient clienteHttp;
    private String serviceHost;
    static final Logger log = Logger.getLogger(RequestManager.class);

    public RequestManager() {
        clienteHttp = new HttpClient();
        serviceHost = ClientConfig.getInstance().getServiceHost();
    }

    /**
     * Extrae el CODIGO de una URL sintetica local (ver
     * DbsDispatcherConfig.PREFIJO_LOCAL). Solo debe llamarse cuando
     * ya se verifico que url.startsWith(PREFIJO_LOCAL).
     */
    private static String extraerCodigoLocal(String url) {
        return url.substring(DbsDispatcherConfig.PREFIJO_LOCAL.length());
    }

    /**
     * Punto unico de despacho para GET/DELETE con parametros (sin
     * payload JSON armado todavia). Si la URL es una URL sintetica
     * local, ejecuta contra el .dbs correspondiente sin tocar la red;
     * si no, sigue el camino de siempre (directo o via API Manager).
     */
    private String resolverJsonConParams(String url, Map<String, Object> params,
        HttpMethodEnum httpMethodEnum) throws IOException, SystemException {
        if (url != null && url.startsWith(DbsDispatcherConfig.PREFIJO_LOCAL)) {
            return DbsDispatcherConfig.getInstance()
                            .ejecutarJson(extraerCodigoLocal(url), params);
        }
        return !ClientConfig.getInstance().isTokenRequest()
            ? processRequestUrl(url, params, httpMethodEnum)
            : processRequestAPI(url, params, httpMethodEnum);
    }

    /**
     * Punto unico de despacho para POST/PUT (con payload JSON ya
     * armado para el camino remoto). El mapa original (antes de
     * serializar) se conserva aparte para poder bindearlo tal cual
     * contra el .dbs cuando la URL es local.
     */
    private String resolverJsonConPayload(String url, String jsonContentSend,
        Map<String, Object> paramsOriginales, HttpMethodEnum httpMethodEnum)
                    throws IOException, SystemException {
        if (url != null && url.startsWith(DbsDispatcherConfig.PREFIJO_LOCAL)) {
            return DbsDispatcherConfig.getInstance()
                            .ejecutarJson(extraerCodigoLocal(url), paramsOriginales);
        }
        return !ClientConfig.getInstance().isTokenRequest()
            ? processRequestJson(url, jsonContentSend, httpMethodEnum)
            : processRequestAPI(url, jsonContentSend, httpMethodEnum);
    }

    /* Construye todos los par�metros de la URL */
    private static String buildUrl(String url, Map<String, Object> params)
                    throws SystemException {
        String parameters;
        if (ObjectUtility.isObjecNotNullOrEmpty(params, true)) {
            Set<String> keys = params.keySet();
            StringBuilder sb = new StringBuilder(SignEnum.QUESTION.getValue());
            for (String key : keys) {
                if (params.get(key) != null) {
                    sb.append(key).append(SignEnum.EQUAL.getValue())
                                    .append(encodeString(JsonConverter
                                                    .convertirTipoCadena(params
                                                                    .get(key),
                                                                    false)))
                                    .append(SignEnum.AMPERSAND.getValue());
                }
            }
            parameters = sb.substring(0, sb.length() - 1);
            url = url.concat(parameters);
        }
        return url;
    }

    private static String encodeString(String text) throws SystemException {
        String rta = null;
        try {
            rta = text == null ? null
                : URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
        }
        catch (UnsupportedEncodingException e) {
            throw new SystemException(e.getMessage(), e);
        }
        return rta;
    }

    /*
     * Procesa la peticion por Gestor de API (Token de seguridad) en
     * modo HTTP GET
     */
    private String processRequestAPI(String urlM,
        Map<String, Object> params, HttpMethodEnum httpMethodEnum)
                    throws IOException, SystemException {

        HttpResponse httpResponse = null;
        String url = buildUrl(urlM, params);
        String rta;
        log.info("URL ::: " + url);
        String token = pedirToken();
        if (HttpMethodEnum.GET.equals(httpMethodEnum)) {
            httpResponse = clienteHttp.doGet(url, Constans.BEARER + token);
        }
        else if (HttpMethodEnum.DELETE.equals(httpMethodEnum)) {
            httpResponse = clienteHttp.doDelete(url, Constans.BEARER + token);
        }

        if (!validarAutenticacion(httpResponse)) {
            rta = processRequestAPI(urlM, params, httpMethodEnum);
        }
        else {
            rta = clienteHttp.getResponsePayload(httpResponse);
            log.info("IMPRIME: " + rta);
            if (httpResponse != null && httpResponse.getStatusLine()
                            .getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                throw new ClientWSO2Exception(rta);
            }
        }
        return rta;
    }

    /*
     * Procesa la peticion por Gestor de API en modo HTTP POST, PUT o
     * DELETE
     */
    private String processRequestAPI(String url, String payload,
        HttpMethodEnum httpMethodEnum) throws IOException, SystemException {
        HttpResponse httpResponse = null;
        log.info("[processRequestJson] urlString : " + url);
        log.info("[processRequestJson] jsonCopntent : " +
            payload);
        String rta;
        String token = pedirToken();
        if (HttpMethodEnum.POST.equals(httpMethodEnum)) {
            httpResponse = clienteHttp.doPost(url, Constans.BEARER + token,
                            payload, HeaderEnum.ACCEPT.getValue());
        }
        else if (HttpMethodEnum.PUT.equals(httpMethodEnum)) {
            httpResponse = clienteHttp.doPut(url, Constans.BEARER + token,
                            payload, HeaderEnum.ACCEPT.getValue());
        }
        else if (HttpMethodEnum.DELETE.equals(httpMethodEnum)) {
            httpResponse = clienteHttp.doDelete(url, token);
        }

        if (!validarAutenticacion(httpResponse)) {
            rta = processRequestAPI(url, payload, httpMethodEnum);
        }
        else {
            rta = clienteHttp.getResponsePayload(httpResponse);
            log.info("IMPRIME: " + rta);
            if (httpResponse != null && httpResponse.getStatusLine()
                            .getStatusCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                throw new ClientWSO2Exception(rta);
            }
        }

        return rta;
    }

    private String pedirToken() throws SystemException {
        TokenManager tm = new TokenManager();
        Token token = ClientConfig.getInstance().getToken();
        token = token == null ? tm.getToken() : token;
        if (token == null) {
            throw new SystemException("No es posible obtener token");
        }
        else {
            return token.getAccessToken();
        }

    }

    public boolean validarAutenticacion(HttpResponse httpResponse)
                    throws SystemException {
        boolean rta = true;
        if (httpResponse != null
            && httpResponse.getStatusLine()
                            .getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            TokenManager tm = new TokenManager();
            Token aux = tm.getToken();
            if (aux == null) {
                throw new SystemException("No es posible obtener token");
            }
            ClientConfig.getInstance().setToken(aux);
            rta = false;
        }

        return rta;
    }

    // /* Procesa la peticion directamente por Servicio en modo GET */
    private String processRequestUrl(String urlStrin,
        Map<String, Object> params, HttpMethodEnum httpMethodEnum)
                    throws IOException, SystemException {
        String urlString = buildUrl(urlStrin, params);
        log.info("URL ::: " + urlString);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(httpMethodEnum.name());
        connection.addRequestProperty(HeaderEnum.ACCEPT.getKey(),
                        HeaderEnum.ACCEPT.getValue());
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new ClientWSO2Exception(extraerMensajeError(connection));
        }

        BufferedReader bufferReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(),
                                        StandardCharsets.UTF_8));
        String str;
        StringBuilder stringBuilder = new StringBuilder();

        while ((str = bufferReader.readLine()) != null) {
            stringBuilder.append(str);
            stringBuilder.append("\n");
        }

        String response = stringBuilder.toString();
        if (stringBuilder.toString().equals(Constans.EMPTYSTRG)) {
            response = Constans.OKCODE;
        }
        log.info("IMPRIME: " + response);
        return stringBuilder.toString();
    }

    /*
     * Procesa la peticion directamente por Servicio en modo POST, PUT
     * o DELETE
     */
    private static String processRequestJson(String urlString,
        String jsonCopntent, HttpMethodEnum httpMethodEnum)
                    throws IOException, ClientWSO2Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(httpMethodEnum.name());
        connection.addRequestProperty(HeaderEnum.CONTENT_TYPE.getKey(),
                        HeaderEnum.CONTENT_TYPE.getValue());
        connection.addRequestProperty(HeaderEnum.ACCEPT.getKey(),
                        HeaderEnum.ACCEPT.getValue());

        // REMOVE

        log.info("[processRequestJson] urlString : " + urlString);
        log.info("[processRequestJson] jsonCopntent : " +
            jsonCopntent);

        OutputStream os = connection.getOutputStream();
        os.write(jsonCopntent.getBytes(StandardCharsets.UTF_8));
        os.flush();
        log.info("Valor: " + connection.getResponseCode());
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new ClientWSO2Exception(extraerMensajeError(connection));
        }

        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8));

        String str;

        StringBuilder rta = new StringBuilder();

        while ((str = bufferReader.readLine()) != null) {
            rta.append(str);
            rta.append("\n");
        }
        connection.disconnect();

        String response = rta.toString();
        log.info("IMPRIME: " + response);
        return response;
    }

    private List<Parameter> getListEmpty() {
        List<Parameter> listParameter = new java.util.ArrayList<>();
        try {
            listParameter = JsonConverter.toRegistroList(
                            JsonConverter.getJsonEmpty(), JsonEnum.DEFAULT);
        }
        catch (ClientWSO2Exception | SysmanException e) {
            log.error(e);
        }
        return listParameter;
    }

    /**
     * Permite obtener una lista de objetos Parameter a traves de la
     * URL suminstrada (Servicio REST, DSS, etc).
     * 
     * @throws SystemException
     */
    public List<Parameter> getList(String url, Map<String, Object> params)
                    throws SystemException {
        try {
            String jsonContent = resolverJsonConParams(url, params, HttpMethodEnum.GET);
            return JsonConverter.toRegistroList(jsonContent, JsonEnum.DEFAULT);
        }
        catch (IOException | SysmanException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.PARAMETERS.getText(), params.toString());
            throw new CrudException(e.getMessage(), e, parExc);
        }
    }

    /**
     * Igual que getList + get(urlConteo) juntos, pero en una sola
     * llamada: si la URL es local, trae filas y total en un solo
     * viaje contra el .dbs (sin segunda consulta HTTP); si es
     * remota, mantiene el comportamiento de siempre (dos llamadas,
     * una por cada URL).
     *
     * @throws SystemException
     */
    public ListaConTotal getListConTotal(String url, String urlConteo,
        Map<String, Object> params) throws SystemException {
        if (url != null && url.startsWith(DbsDispatcherConfig.PREFIJO_LOCAL)) {
            return DbsDispatcherConfig.getInstance()
                            .ejecutarListaConConteo(extraerCodigoLocal(url), params);
        }
        List<Parameter> list = getList(url, params);
        Parameter tem = get(urlConteo, params);
        int total = (Integer) tem.getFields().get("TOTAL");
        return new ListaConTotal(list, total);
    }

    /**
     * Igual que get(urlConteo,...) para obtener solo el TOTAL, con
     * despacho local/remoto. Se usa cuando el conteo se necesita
     * antes de pedir la lista completa (por ejemplo, para traer
     * "todos los seleccionados" de una sola vez).
     *
     * @throws SystemException
     */
    public int getConteo(String url, String urlConteo, Map<String, Object> params)
                    throws SystemException {
        if (url != null && url.startsWith(DbsDispatcherConfig.PREFIJO_LOCAL)) {
            return DbsDispatcherConfig.getInstance()
                            .ejecutarConteo(extraerCodigoLocal(url), params);
        }
        Parameter tem = get(urlConteo, params);
        return (Integer) tem.getFields().get("TOTAL");
    }

    /**
     * Permite obtener un objeto Parameter a traves de la URL
     * suminstrada (Servicio REST, DSS, etc).
     * 
     * @throws SystemException
     */
    public Parameter get(String url, Map<String, Object> params)
                    throws SystemException {
        Parameter parameter = new Parameter();
        try {
            String jsonContent = resolverJsonConParams(url, params, HttpMethodEnum.GET);
            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
            return parameter;
        }
        catch (IOException | SysmanException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.PARAMETERS.getText(), params.toString());
            throw new CrudException(e.getMessage(), e, parExc);
        }

    }

    /**
     * Permite registrar informacion en el sistema a traves de la URL
     * suminstrada (Servicio REST, DSS, etc).
     * 
     * @return
     * 
     * @throws SystemException
     */
    public Map<String, Object> save(String url, String serviceName,
        Parameter parameter)
                    throws SystemException {
        Parameter rta = new Parameter();
        String jsonContentSend = null;
        try {
            jsonContentSend = JsonConverter.toJson(serviceName,
                            parameter,
                            true);
            String jsonContent = resolverJsonConPayload(url, jsonContentSend,
                            parameter.getFields(), HttpMethodEnum.POST);
            rta = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);

            return rta.getFields();
        }
        catch (IOException | SysmanException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.SERVICE.getText(), serviceName);
            parExc.put(ParametersEnum.PARAMETERS.getText(), jsonContentSend);
            throw new CrudException(e.getMessage(), e, parExc);
        }

    }

    /**
     * Permite registrar informacion en el sistema a traves de la URL
     * suminstrada (Servicio REST, DSS, etc).
     * 
     * @return
     * 
     * @throws SystemException
     */
    public Map<String, Object> save(String url, String serviceName,
        Map<String, Object> parameterMap)
                    throws SystemException {
        Parameter rta = new Parameter();
        Parameter reqParameter = new Parameter();
        reqParameter.setFields(parameterMap);
        String jsonContentSend = null;
        try {
            jsonContentSend = JsonConverter.toJson(serviceName,
                            reqParameter, true);
            String jsonContent = resolverJsonConPayload(url, jsonContentSend,
                            parameterMap, HttpMethodEnum.POST);
            rta = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);

            return rta.getFields();
        }
        catch (IOException | SysmanException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.SERVICE.getText(), serviceName);
            parExc.put(ParametersEnum.PARAMETERS.getText(), jsonContentSend);

            throw new CrudException(e.getMessage(), e, parExc);
        }

    }

    /**
     * Permite registrar informacion en el sistema a traves de la URL
     * suminstrada (Servicio REST, DSS, etc).
     * 
     * @return
     * 
     * @throws SystemException
     */
    public Map<String, Object> saveLogError(String url, String serviceName,
        Map<String, Object> parameterMap)
                    throws SystemException {
        Parameter rta = new Parameter();
        Parameter reqParameter = new Parameter();
        reqParameter.setFields(parameterMap);
        String jsonContentSend = null;
        try {
            jsonContentSend = JsonConverter.toJson(serviceName,
                            reqParameter, true);
            String jsonContent = !ClientConfig.getInstance().isTokenRequest()
                ? processRequestJson(url, jsonContentSend,
                                HttpMethodEnum.POST)
                : processRequestAPI(url,
                                jsonContentSend,
                                HttpMethodEnum.POST);
            rta = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);

            return rta.getFields();
        }
        catch (IOException | ClientWSO2Exception | SysmanException e) {
            throw new SystemException(e.getMessage(), e);
        }

    }

    /**
     * Permite registrar informacion en el sistema a traves de la URL
     * suminstrada (Servicio REST, DSS, etc). Retorna el número de
     * filas insertadas segun una estructura de respuesta
     * preestablecida
     * 
     * @return
     * 
     * @throws SystemException
     */
    public int saveCount(String url, String serviceName,
        Map<String, Object> parameterMap)
                    throws SystemException {
        Parameter rta = new Parameter();
        Parameter reqParameter = new Parameter();
        reqParameter.setFields(parameterMap);
        String jsonContentSend = null;
        try {
            jsonContentSend = JsonConverter.toJson(serviceName,
                            reqParameter, true);
            String jsonContent = resolverJsonConPayload(url, jsonContentSend,
                            parameterMap, HttpMethodEnum.POST);
            rta = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
            return extraerTotal(rta);

        }
        catch (IOException | SysmanException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.SERVICE.getText(), serviceName);
            parExc.put(ParametersEnum.PARAMETERS.getText(), jsonContentSend);
            throw new CrudException(e.getMessage(), e);
        }

    }

    /**
     * Permite obtener un objeto plano segun la clase ingresada por
     * parametro a traves de una URL suministrada, unos parametros y
     * un Token valido. To do de pasar a traves de un Gestor de API de
     * manera segura.
     */
    public <T> T getPlainObject(String url, Map<String, Object> params,
        Class<T> classOfT) {
        Object plainObj = null;
        String jsonContent;
        try {
            jsonContent = !ClientConfig.getInstance().isTokenRequest()
                ? processRequestUrl(url, params, HttpMethodEnum.GET)
                : processRequestAPI(url, params, HttpMethodEnum.GET);
            plainObj = JsonConverter.toPlainObject(jsonContent,
                            JsonEnum.DEFAULT, classOfT);
        }
        catch (IOException | SysmanException
                        | SystemException e) {
            log.error("Error  : " + e.getMessage(), e);
        }

        return Primitives.wrap(classOfT).cast(plainObj);
    }

    /**
     * Permite actualizar directamente a traves de la URL suminstrada
     * (Servicio REST, DSS, etc).
     * 
     * @throws SystemException
     */
    public int update(String url, String serviceName, Parameter registro)
                    throws SystemException {
        Parameter parameter = new Parameter();
        String jsonContentSend = null;
        try {
            jsonContentSend = JsonConverter.toJson(serviceName, registro,
                            false);
            String jsonContent = resolverJsonConPayload(url, jsonContentSend,
                            registro.getFields(), HttpMethodEnum.PUT);
            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
            return extraerTotal(parameter);

        }
        catch (SysmanException | IOException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.SERVICE.getText(), serviceName);
            parExc.put(ParametersEnum.PARAMETERS.getText(), jsonContentSend);

            throw new CrudException(e.getMessage(), e, parExc);
        }
    }

    /**
     * Permite actualizar directamente a traves de la URL suminstrada
     * (Servicio REST, DSS, etc).
     * 
     * @throws SystemException
     */
    public int update(String url, String serviceName,
        Map<String, Object> campos, Map<String, Object> llaves)
                    throws SystemException {
        Parameter parameter = new Parameter();
        Parameter registro = new Parameter();
        String jsonContentSend = null;
        try {
            registro.setFields(campos);
            registro.getFields().putAll(llaves);
            jsonContentSend = JsonConverter.toJson(serviceName, registro,
                            false);
            String jsonContent = resolverJsonConPayload(url, jsonContentSend,
                            registro.getFields(), HttpMethodEnum.PUT);
            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
            return extraerTotal(parameter);

        }
        catch (SysmanException | IOException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.SERVICE.getText(), serviceName);
            parExc.put(ParametersEnum.PARAMETERS.getText(), jsonContentSend);
            throw new CrudException(e.getMessage(), e, parExc);
        }
    }

    /**
     * Permite eliminar directamente a traves de la URL suminstrada
     * (Servicio REST, DSS, etc).
     * 
     * @throws SysException
     */
    public int delete(String url, Map<String, Object> params)
                    throws SystemException {
        Parameter parameter = new Parameter();
        try {
            String jsonContent = resolverJsonConParams(url, params, HttpMethodEnum.DELETE);
            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
            return extraerTotal(parameter);
        }
        catch (SysmanException | IOException e) {
            throw new SystemException(e.getMessage(), e);
        }
        catch (ClientWSO2Exception e) {
            Map<String, Object> parExc = new HashMap<>();
            parExc.put(ParametersEnum.URL.getText(), url);
            parExc.put(ParametersEnum.PARAMETERS.getText(), params.toString());
            throw new CrudException(e.getMessage(), e, parExc);
        }
    }

    private static String extraerMensajeError(HttpURLConnection connection)
                    throws IOException {
        BufferedReader bufferReader = new BufferedReader(
                        new InputStreamReader(
                                        connection.getErrorStream(),
                                        StandardCharsets.UTF_8));
        String str;
        StringBuilder rta = new StringBuilder();

        while ((str = bufferReader.readLine()) != null) {
            rta.append(str);
            rta.append("\n");
        }
        return rta.toString();
    }

    private int extraerTotal(Parameter par) {
        return (Integer) par.getFields().get("TOTAL");
    }

}
