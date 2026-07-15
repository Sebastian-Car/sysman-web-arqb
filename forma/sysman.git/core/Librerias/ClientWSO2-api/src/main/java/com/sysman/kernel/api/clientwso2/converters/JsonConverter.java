/*
 * JsonConverter
 *
 * 1.0
 *
 * 12/08/2016
 *
 * Copyright Stefanini Sysman
 */
package com.sysman.kernel.api.clientwso2.converters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Primitives;
import com.sysman.exc.kernel.api.clientwso2.exceptions.ClientWSO2Exception;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.Token;
import com.sysman.kernel.api.clientwso2.util.enums.TokenEnum;
import com.sysman.kernel.api.commons.util.DateUtility;
import com.sysman.kernel.api.commons.util.NumberUtility;
import com.sysman.kernel.api.commons.util.ObjectUtility;
import com.sysman.kernel.api.commons.util.StringUtility;
import com.sysman.kernel.api.commons.util.enums.DateFormatEnum;
import com.sysman.kernel.api.commons.util.enums.JsonEnum;
import com.sysman.kernel.api.commons.util.enums.SignEnum;
import com.sysman.kernel.api.commons.util.exceptions.SysmanException;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Erwin Jose Tirado Baldovino
 * 
 * @version 1.0
 * 
 * Clase conversor para extraer un JSON a Java y viceversa.
 */
public class JsonConverter {
    /** class logger */
    private static final Logger logger = Logger.getLogger(JsonConverter.class);

    private static boolean isJsonArray(Object json) {
        if (json instanceof JSONArray) {
            return true;
        }
        return false;
    }

    private static JSONArray findJsonArray(String contain, JsonEnum jsonEnum)
                    throws ClientWSO2Exception {
        JSONArray array = null;
        JsonEnum localJsonEnum = jsonEnum;
        try {
            JSONParser parser = new JSONParser();
            if (localJsonEnum == null) {
                localJsonEnum = JsonEnum.DEFAULT;
            }
            Object json = parser.parse(contain);
            JSONObject jsonObject = (JSONObject) json;
            JSONObject elementos = (JSONObject) jsonObject
                            .get(localJsonEnum.getBase());

            if (elementos == null) {
                throw new ClientWSO2Exception(
                                "El objeto json no posee un elemento raiz");
            }

            if (isJsonArray(elementos)) {
                throw new ClientWSO2Exception(
                                "El objeto json no posee un contenedor de elementos registro");
            }


            array = (JSONArray) elementos.get(localJsonEnum.getContainer());
            
            // if (array == null) {
            // throw new ClientWSO2Exception(
            // "El objeto json no posee un contenedor de elementos
            // registro");
            // }
        }
        catch (ParseException e) {
            throw new ClientWSO2Exception("Error parseando el objeto JSON: "
                + e.getMessage());
        }
        return array;
    }

    private static Object convertToObject(Object jsonObj)
                    throws SysmanException {
        Object rta = jsonObj;

        if (jsonObj != null) {
            String cadena = jsonObj.toString();
            if (jsonObj instanceof String && DateUtility.isDate(cadena.trim(),
                            DateFormatEnum.DEFAULT)) {
                rta = StringUtility.toDate(cadena.trim(),
                                DateFormatEnum.DEFAULT);
            }
            else if (jsonObj instanceof Boolean) {
                rta = new Boolean(cadena);
            }
            else if (jsonObj instanceof Long || jsonObj instanceof Integer
                || NumberUtility.isInteger(cadena)) {
                rta = NumberUtility.toInteger(jsonObj);
            }
            else if (jsonObj instanceof Double
                || NumberUtility.isDecimal(cadena)) {
                rta = NumberUtility.toDecimal(jsonObj);
            }
        }
        return rta;
    }

    /**
     * Permite cargar y obtener un objeto JsonArray a partir de un
     * contenido json.
     */
    private static JsonArray getJsonArray(String jsonContent,
        JsonEnum jsonEnum) {

        com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
        com.google.gson.JsonElement jsonElement = parser.parse(jsonContent);

        com.google.gson.JsonObject jsonObject = jsonElement.getAsJsonObject();
        com.google.gson.JsonElement jsonBase = jsonObject
                        .get(jsonEnum.getBase());

        com.google.gson.JsonObject jsonObjectContainer = jsonBase
                        .getAsJsonObject();
        com.google.gson.JsonElement jsonContainer = jsonObjectContainer
                        .get(jsonEnum.getContainer());
        com.google.gson.JsonArray gsonArray = jsonContainer.getAsJsonArray();
        return gsonArray;
    }

    private static Parameter createRegistro(JSONObject element)
                    throws SysmanException {
        Parameter parametro = new Parameter();
        Set<Entry<String, Object>> entries = element.entrySet();
        for (Entry<String, Object> entry : entries) {
            Object value = convertToObject(element.get(entry.getKey()));
            parametro.getFields().put(entry.getKey(), value);
        }
        return parametro;
    }

    /**
     * Permite convertir un objeto JSON al objeto Java Beans Registro
     */
    public static Parameter toRegistro(String jsonContent, JsonEnum jsonEnum)
                    throws ClientWSO2Exception, SysmanException {
        Parameter parametro = null;
        JSONArray array = findJsonArray(jsonContent, jsonEnum);
        if (array == null) {
            return parametro;
        }

        JSONObject job;
        if (array.get(0) != null) {
            job = (JSONObject) array.get(0);
            parametro = createRegistro(job);
        }
        return parametro;
    }

    /**
     * Permite convertir un objeto JSON a una lista de objetos Java
     * Beans Registro
     * 
     * @throws ClientWSO2Exception
     * @throws SysmanException
     */
    public static List<Parameter> toRegistroList(String jsonContent,
        JsonEnum jsonE) throws ClientWSO2Exception, SysmanException {
        JsonEnum jsonEnum = jsonE;
        List<Parameter> registros = new ArrayList<>();
        if (jsonEnum == null) {
            jsonEnum = JsonEnum.DEFAULT;
        }
        JSONArray array = findJsonArray(jsonContent, jsonEnum);
        if (array == null || array.isEmpty()) {
            return registros;
        }
        
        Object firstElement = array.get(0);
        if (!(firstElement instanceof JSONObject)) {
            return registros; 
        }
        
        JSONObject myarray = (JSONObject) firstElement;
        Object myarraysub = myarray.getOrDefault("myarray", -1);
      	JSONObject job;
        
         if(myarraysub == null || myarraysub.equals(-1)) {

             for (int i = 0; i < array.size(); i++) {
                 job = (JSONObject) array.get(i);
                 if (job != null) {
                	 registros.add(createRegistro(job));
                 }
             }
             
         }else {
        	 
        	 String myjsonContent = "["+myarraysub.toString()+"]"; 
        	 JSONParser parser = new JSONParser();
        	 try {
                 // Parsear el string JSON
                 JSONArray jsonArray = (JSONArray) parser.parse(myjsonContent);
                 
                 for (int i = 0; i < jsonArray.size(); i++) {
                     job = (JSONObject) jsonArray.get(i);
                     if (job != null) {
                    	 registros.add(createRegistro(job));
                     } 
                 } 
             } catch (Exception e) {
                 e.printStackTrace();
             }
  
        	 
         }

        return registros;
    }

    /**
     * Permite convertir una estructura JSON a un POJO
     */
    public static <T> T toPlainObject(String jsonContent, JsonEnum jsonEnum,
        Class<T> classOfT)
                    throws ClientWSO2Exception, SysmanException {
        Gson gson = new Gson();
        Object plainObj = null;
        JSONArray array = findJsonArray(jsonContent, jsonEnum);
        com.google.gson.JsonObject jo = new JsonObject();
        if (array == null) {
            throw new ClientWSO2Exception(
                            "No existe un arreglo en el objeto JSON");
        }
        com.google.gson.JsonArray gsonArray = getJsonArray(jsonContent,
                        jsonEnum);
        Iterator<com.google.gson.JsonElement> iterator = gsonArray.iterator();
        if (iterator.hasNext()) {
            com.google.gson.JsonElement je = iterator.next();
            jo = je.getAsJsonObject();
            plainObj = gson.fromJson(jo, classOfT);
        }
        return Primitives.wrap(classOfT).cast(plainObj);
    }

    /**
     * Permite convertir un objeto JSON al objeto Java Beans Token
     */
    public static Token toToken(String accessTokenJson) {
        JSONParser parser = new JSONParser();

        Token token = new Token();
        try {
            Object obj = parser.parse(accessTokenJson);
            JSONObject jsonObject = (JSONObject) obj;
            token.setAccessToken((String) jsonObject
                            .get(TokenEnum.ACCESS_TOKEN.getName()));
            long expiresIn = ((Long) jsonObject
                            .get(TokenEnum.EXPIRES_IN.getName())).intValue();
            token.setExpiresIn(expiresIn);
            token.setRefreshToken((String) jsonObject
                            .get(TokenEnum.REFRESH_TOKEN.getName()));
            token.setTokenType((String) jsonObject
                            .get(TokenEnum.TOKEN_TYPE.getName()));

        }
        catch (ParseException e) {
            logger.error(e);
        }
        return token;
    }

    private static String createJsonRegistro(Parameter parametro,
        boolean eliminaNulos) {
        StringBuilder sb = new StringBuilder();
        if (ObjectUtility.isObjecNotNullOrEmpty(parametro.getFields(), true)) {
            Set<String> keys = parametro.getFields().keySet();
            for (String key : keys) {

                if (parametro.getFields().get(key) != null
                    && !parametro.getFields().get(key).toString().isEmpty()) {
                    sb.append(SignEnum.DOUBLE_QUOTES.getValue()).append(key)
                                    .append(SignEnum.DOUBLE_QUOTES.getValue())
                                    .append(SignEnum.SPACE.getValue())
                                    .append(SignEnum.TWO_POINTS.getValue())
                                    .append(SignEnum.SPACE.getValue());

                    sb.append(SignEnum.DOUBLE_QUOTES.getValue())
                                    .append(convertirTipoCadena(
                                                    parametro.getFields()
                                                                    .get(key),
                                                    true))
                                    .append(SignEnum.DOUBLE_QUOTES.getValue())

                                    .append(SignEnum.COMMA.getValue());
                }
                else if (!eliminaNulos) {
                    sb.append(SignEnum.DOUBLE_QUOTES.getValue()).append(key)
                                    .append(SignEnum.DOUBLE_QUOTES.getValue())
                                    .append(SignEnum.SPACE.getValue())
                                    .append(SignEnum.TWO_POINTS.getValue())
                                    .append(SignEnum.SPACE.getValue());

                    sb.append("null")
                                    .append(SignEnum.COMMA.getValue());
                }
            }
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static String convertirTipoCadena(Object valor, boolean jsonFormat) {
        String result;
        if (valor instanceof Date) {
            result = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",
                            new Locale("es", "ES")).format(valor);
        }
        else if (valor instanceof Number) {
            result = String.valueOf(valor);
        }
        else if (valor instanceof Boolean) {
            result = (boolean) valor ? "-1" : "0";
        }
        else {
            result = jsonFormat ? JSONObject.escape((String) (valor))
                : (String) (valor);
        }
        return result;
    }

    /**
     * Permite convertir un objeto Java Beans Registro en una cadena
     * de String JSON, extrayendo el mapa contenido y generando la
     * estructura apropiada.
     */
    public static String toJson(String base, Parameter parametro,
        boolean eliminaNulos) {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class",
                        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                        "org.apache.velocity.runtime.log.Log4JLogChute");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();

        Template t = ve.getTemplate("json.vm");
        VelocityContext context = new VelocityContext();
        context.put("base", base);
        context.put("fields", createJsonRegistro(parametro, eliminaNulos));
        StringWriter writer = new StringWriter();
        t.merge(context, writer);

        String jsonContent = writer.toString();

        return jsonContent;
    }

    public static String getJsonEmpty() {
        return "{\"elementos\": {\"elemento\":[{\"response\": \"Error, No se pudo obtener respuesta\"}]}}";
    }

}
