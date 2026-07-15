package com.sysman.email;

import com.google.gson.Gson;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class ApiRestClient {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON1 = "application/json; charset=utf-8";
    private static final String APPLICATION_JSON2 = "application/json";
    private static final String ACCEPT = "Accept";
    private static final String METHOD = "Method";
    private static final String POST = "POST";
    private static final String MENSAJE_ERROR = "Error al optener respuesta de la API.  Por favor contacte con el administrador del sistema";
    private static final String MESSAJE_HTTP = "Failed : HTTP error code : ";

    public String postClient(EmailPojo email) throws IOException {
        return postClient(email, null);
    }

    public String postClient(EmailPojo email, String compania)
                    throws IOException {
        HttpURLConnection connection = null;
        StringBuilder response = null;
        connection = (HttpURLConnection) new URL(extraerUrlApi(compania))
                        .openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON1);
        connection.setRequestProperty(ACCEPT, APPLICATION_JSON2);
        connection.setRequestProperty(METHOD, POST);
        Gson gson = new Gson();
        String json = gson.toJson(email);
        OutputStream os;
        os = connection.getOutputStream();
        os.write(json.getBytes(StandardCharsets.UTF_8));
        os.flush();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(new StringBuilder(MESSAJE_HTTP)
                            .append(connection.getResponseCode()).toString());
        }

        BufferedReader br = new BufferedReader(
                        new InputStreamReader((connection.getInputStream()),
                                        StandardCharsets.UTF_8));
        String output;
        response = new StringBuilder();
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        String rtaResponse = MENSAJE_ERROR;
        if (response.length() > 0) {
            rtaResponse = response.toString();
        }
        return rtaResponse;
    }

    /**
     * Este metodo extrae una URL de la tabla URLSERVICIO segun el
     * codigo
     * 
     * @param compania
     * c&oacute;digo de la compa&ntilde;ia
     * @return
     */
    private String extraerUrlApi(String compania) {
        String salida = null;
        RequestManager requestManager = new RequestManager();

        Map<String, Object> parametrosURL = new TreeMap<>();
        String codigoCompania = compania == null ? SessionUtil.getCompania()
            : compania;
        parametrosURL.put(GeneralParameterEnum.COMPANIA.getName(),
                        codigoCompania);
        parametrosURL.put(GeneralParameterEnum.CODIGO.getName(), "22");

        Registro cadenaURL;
        try {
            cadenaURL = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "1710001")
                                            .getUrl(), parametrosURL));

            salida = cadenaURL.getCampos().get("URL").toString();
            System.out.println("URL CORREOS => " + salida);
        }
        catch (Exception e) {
            JsfUtil.agregarMensajeError(
                            "No se Encuentra la URL del servicio para envio de alertas.");
        }
        return salida;
    }

    /**
     * Este metodo extrae una URL de la tabla URLSERVICIO segun el
     * codigo
     * 
     * @return String
     */
    public String extraerUrlApi() {
        return extraerUrlApi(null);
    }

}
