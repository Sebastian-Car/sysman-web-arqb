package com.sysman.identificacion;

import com.google.gson.Gson;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Conector Generico para cliente API Resfull
 * 
 * @author
 * 
 * 
 */
public class APIAutenticacion {

    /**
     * Constante que representa la instancia del Log
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
                    .getLogger(APIAutenticacion.class);

    /**
     * 
     * Cliente para el servicio que permite validar la conección con el directorio activo o ldap
     * 
     * @author jgomez
     * 
     * @serialData 18/09/2018
     * @param usuarioWindows
     * @param clave
     * @param url
     * @param tipoDirectorio
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws SysmanException
     */
    public boolean autenticacion(String usuarioWindows, String clave,
                    String url, String tipoDirectorio)
                    throws MalformedURLException, IOException, SysmanException {
        boolean salida = false;
        HttpURLConnection connection = null;
        StringBuffer response = null;
        UsuarioLdap usuario = new UsuarioLdap();
        usuario.setUser(usuarioWindows);
        usuario.setPassword(clave);
        usuario.setTypeDirectory(tipoDirectorio);
        LOG.info("URL-> {}", url);
        connection = (HttpURLConnection) new URL(url)
                        .openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Method", "POST");
        Gson gson = new Gson();
        String json = gson.toJson(usuario);
        //LOG.info("Json Entrada {}{}", this.getClass(), json);
        OutputStream os = connection.getOutputStream();
        os.write(json.getBytes(StandardCharsets.UTF_8));
        os.flush();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : "
                            + connection.getResponseCode());
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
            throw new SysmanException("El servicio es nulo");
        }
        else {
            RespuestaApi repuestaApi = gson.fromJson(response.toString(),
                            RespuestaApi.class);
            if (repuestaApi.getCodigo() != 0) {
                throw new SysmanException("Conexión Diretorio Activo: "
                                + repuestaApi.getMensaje());
            }
            else {
                salida = true;
            }
        }
        return salida;
    }

}