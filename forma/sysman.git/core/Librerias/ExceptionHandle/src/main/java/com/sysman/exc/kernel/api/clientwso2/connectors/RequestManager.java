package com.sysman.exc.kernel.api.clientwso2.connectors;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.sysman.exc.kernel.api.clientwso2.beans.Parameter;
import com.sysman.exc.kernel.api.clientwso2.config.ClientConfig;
import com.sysman.exc.kernel.api.clientwso2.converters.JsonConverter;
import com.sysman.exc.kernel.api.clientwso2.exceptions.ClientWSO2Exception;
import com.sysman.exc.kernel.api.clientwso2.util.enums.HeaderEnum;
import com.sysman.exc.kernel.api.clientwso2.util.enums.HttpMethodEnum;
import com.sysman.exc.kernel.api.commons.util.Constans;
import com.sysman.exc.kernel.api.commons.util.ObjectUtility;
import com.sysman.exc.kernel.api.commons.util.enums.JsonEnum;
import com.sysman.exc.kernel.api.commons.util.enums.SignEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;


public class RequestManager {
	
	  private HttpClient clienteHttp;
	    private String serviceHost;
	    static final Logger log = Logger.getLogger(RequestManager.class);

	    public RequestManager() {
	        clienteHttp = new HttpClient();
	        serviceHost = ClientConfig.getInstance().getServiceHost();
	    }

	    /* Construye todos los par�metros de la URL */
	    private static String buildUrl(String url, Map<String, Object> params) {
	        String parameters;
	        if (ObjectUtility.isObjecNotNullOrEmpty(params, true)) {
	            Set<String> keys = params.keySet();
	            StringBuilder sb = new StringBuilder(SignEnum.QUESTION.getValue());
	            for (String key : keys) {
	                sb.append(key).append(SignEnum.EQUAL.getValue())
	                                .append(JsonConverter
	                                                .convertirTipoCadena(params
	                                                                .get(key),
	                                                                false))
	                                .append(SignEnum.AMPERSAND.getValue());
	            }
	            parameters = sb.substring(0, sb.length() - 1);
	            url = url.concat(parameters);
	        }
	        return url;
	    }

	    /*
	     * Procesa la peticion por Gestor de API (Token de seguridad) en
	     * modo HTTP GET
	     */
	    private String processRequestAPI(String urlM, String token,
	        Map<String, Object> params) throws IOException {

	        HttpResponse httpResponse;
	        String url = buildUrl(urlM, params);
	        httpResponse = clienteHttp.doGet(url, Constans.BEARER + token);
	        return clienteHttp.getResponsePayload(httpResponse);
	    }

	    /*
	     * Procesa la peticion por Gestor de API en modo HTTP POST, PUT o
	     * DELETE
	     */
	    private String processRequestAPI(String url, String token, String payload,
	        HttpMethodEnum httpMethodEnum) throws IOException, ClientWSO2Exception {
	        HttpResponse httpResponse = null;
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
	        return clienteHttp.getResponsePayload(httpResponse);
	    }

	    /* Procesa la peticion directamente por Servicio en modo GET */
	    private String processRequestUrl(String urlStrin,
	        Map<String, Object> params, HttpMethodEnum httpMethodEnum)
	                        throws IOException, ClientWSO2Exception {
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
	        StringBuffer stringBuffer = new StringBuffer();

	        while ((str = bufferReader.readLine()) != null) {
	            stringBuffer.append(str);
	            stringBuffer.append("\n");
	        }

	        String response = stringBuffer.toString();
	        if (stringBuffer.toString().equals(Constans.EMPTYSTRG)) {
	            response = Constans.OKCODE;
	        }
	        log.info("IMPRIME: " + response);
	        return stringBuffer.toString();
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
	        log.info("[processRequestJson] jsonCopntent : " + jsonCopntent);

	        OutputStream os = connection.getOutputStream();
	        os.write(jsonCopntent.getBytes());
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

	    /**
	     * Permite obtener una lista de objetos Parameter a traves de una
	     * URL suministrada, unos parametros de filtro y un Token valido.
	     * Todo de pasar a traves de un Gestor de API de manera segura.
	     */
	    public List<Parameter> getList(String url, String token,
	        Map<String, Object> params) {

	        try {
	            String jsonContent = processRequestAPI(url, token, params);
	            return JsonConverter.toRegistroList(jsonContent, JsonEnum.DEFAULT);
	        }
	        catch (IOException e) {
	            log.error(e);
	        }
	        catch (ClientWSO2Exception e) {
	            log.error(e);
	        }
	        catch (SysmanException e) {
	            log.error(e);
	        }
	        return getListEmpty();
	    }

	    private List<Parameter> getListEmpty() {
	        List<Parameter> listParameter = new java.util.ArrayList<Parameter>();
	        try {
	            listParameter = JsonConverter.toRegistroList(
	                            JsonConverter.getJsonEmpty(), JsonEnum.DEFAULT);
	        }
	        catch (ClientWSO2Exception e) {
	            log.error(e);
	        }
	        catch (SysmanException e) {
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
	            String jsonContent = processRequestUrl(url, params,
	                            HttpMethodEnum.GET);
	            return JsonConverter.toRegistroList(jsonContent, JsonEnum.DEFAULT);
	        }
	        catch (IOException | ClientWSO2Exception | SysmanException e) {
	            throw new SystemException(e.getMessage(), e);
	        }
	    }

	    /**
	     * Permite obtener un objeto Parameter a traves de una URL
	     * suministrada, unos parametros y un Token valido. Todo de pasar
	     * a traves de un Gestor de API de manera segura.
	     * 
	     * @throws SystemException
	     */
	    public Parameter get(String url, String token, Map<String, Object> params)
	                    throws SystemException {
	        Parameter parameter = new Parameter();
	        try {
	            String jsonContent = processRequestAPI(url, token, params);
	            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
	            return parameter;
	        }
	        catch (IOException | ClientWSO2Exception | SysmanException e) {
	            throw new SystemException(e.getMessage(), e);
	        }
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
	            String jsonContent = processRequestUrl(url, params,
	                            HttpMethodEnum.GET);
	            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
	            return parameter;
	        }
	        catch (IOException | ClientWSO2Exception | SysmanException e) {
	            throw new SystemException(e.getMessage(), e);
	        }

	    }

	    /**
	     * Permite registrar informacion en el sistema a traves de la URL
	     * suminstrada, un token y el Java Beans correspondiente
	     * (Parameter). Todo de pasar a traves de un Gestor de API de
	     * manera segura.
	     */
	    public void save(String url, String serviceName, Parameter parameter,
	        String token) {
	        try {
	            String jsonContent = JsonConverter.toJson(serviceName, parameter);
	            processRequestAPI(url, jsonContent, token, HttpMethodEnum.POST);
	        }
	        catch (IOException e) {
	            log.error(e);
	        }
	        catch (ClientWSO2Exception e) {
	            log.error(e);
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
	        try {
	            String jsonContent = JsonConverter.toJson(serviceName, parameter);
	            jsonContent = processRequestJson(url, jsonContent,
	                            HttpMethodEnum.POST);
	            rta = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);

	            return rta.getFields();
	        }
	        catch (IOException | ClientWSO2Exception | SysmanException e) {
	            throw new SystemException(e.getMessage(), e);
	        }

	    }

	    /**
	     * Permite actualizar la informacion en el sistema a traves de la
	     * URL suminstrada, un token y el Java Beans correspondiente
	     * (Parameter). Todo de pasar a traves de un Gestor de API de
	     * manera segura.
	     */
	    public void update(String url, String serviceName, Parameter registro,
	        String token) {
	        try {
	            String jsonContent = JsonConverter.toJson(serviceName, registro);
	            processRequestAPI(url, jsonContent, token, HttpMethodEnum.PUT);

	        }
	        catch (IOException e) {
	            log.error(e);
	        }
	        catch (ClientWSO2Exception e) {
	            log.error(e);
	        }
	    }

	    /**
	     * Permite obtener un objeto plano segun la calse ingresada por
	     * parametro a traves de una URL suministrada, unos parametros y
	     * un Token valido. Todo de pasar a traves de un Gestor de API de
	     * manera segura.
	     */
	    public <T> T getPlainObject(String url, String token,
	        Map<String, Object> params, Class<T> classOfT) {
	        Gson gson = new Gson();
	        Object plainObj = null;
	        String jsonContent;
	        try {
	            jsonContent = processRequestAPI(url, token, params);
	            plainObj = gson.fromJson(jsonContent, classOfT);
	        }
	        catch (IOException e) {
	            log.error("Error  : " + e.getMessage(), e);
	        }

	        return Primitives.wrap(classOfT).cast(plainObj);
	    }

	    /**
	     * Permite obtener un objeto plano segun la calse ingresada por
	     * parametro a traves de una URL suministrada, unos parametros y
	     * un Token valido. Todo de pasar a traves de un Gestor de API de
	     * manera segura.
	     */
	    public <T> T getPlainObject(String url, Map<String, Object> params,
	        Class<T> classOfT) {
	        Object plainObj = null;
	        String jsonContent;
	        try {
	            jsonContent = processRequestUrl(url, params, HttpMethodEnum.GET);
	            plainObj = JsonConverter.toPlainObject(jsonContent,
	                            JsonEnum.DEFAULT, classOfT);
	        }
	        catch (IOException | ClientWSO2Exception | SysmanException e) {
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
	        try {
	            String jsonContent = JsonConverter.toJson(serviceName, registro);
	            jsonContent = processRequestJson(url, jsonContent,
	                            HttpMethodEnum.PUT);
	            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
	            return extraerTotal(parameter);

	        }
	        catch (ClientWSO2Exception | SysmanException | IOException e) {
	            throw new SystemException(e.getMessage(), e);
	        }
	    }

	    /**
	     * Permite eliminar un objeto Registro a traves de la URL
	     * suminstrada, un token y el Java Beans correspondiente
	     * (Parameter). Todo de pasar a traves de un Gestor de API de
	     * manera segura.
	     */
	    public void delete(String url, String serviceName, Parameter registro,
	        String token) {
	        try {
	            String jsonContent = JsonConverter.toJson(serviceName, registro);
	            jsonContent = processRequestAPI(url, jsonContent, token,
	                            HttpMethodEnum.DELETE);
	        }
	        catch (IOException e) {
	            log.error(e);
	        }
	        catch (ClientWSO2Exception e) {
	            log.error(e);
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

	            String jsonContent = processRequestUrl(url, params,
	                            HttpMethodEnum.DELETE);
	            parameter = JsonConverter.toRegistro(jsonContent, JsonEnum.DEFAULT);
	            return extraerTotal(parameter);
	        }
	        catch (ClientWSO2Exception | SysmanException | IOException e) {
	            throw new SystemException(e.getMessage(), e);

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
