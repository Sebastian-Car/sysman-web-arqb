/*-
 * ConvertirPdf.java
 *
 * 1.0
 * 
 * 16/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.util.rest;

import com.cloudmersive.client.ConvertDocumentApi;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.apache.lucene.store.InputStreamDataInput;

/**
 * @author José Pascual Gómez
 *
 */
public class ConvertirPdf {
    /**
     * Convierte a PDF un archivo .docx gracias a Docx4J
     * 
     * @param ruta
     * del archivo en .docx que se desea convertir
     * @return El archivo en byte
     * @throws FileNotFoundException
     * @throws NegocioExcepcion
     */
    /*
     * public byte[] convertirPDF(String ruta) throws SysmanException
     * { byte[] salida = null; String rutaSalida = ruta + ".pdf"; try
     * { File archivo = new File(ruta); String rutaReal =
     * archivo.getCanonicalPath(); InputStream is = new
     * FileInputStream(rutaReal);
     * 
     * WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
     * .load(is); OutputStream out = new FileOutputStream( new
     * File(rutaSalida));
     * 
     * if (!Docx4J.pdfViaFO()) { Docx4J.toPDF(wordMLPackage, out); }
     * File f = new File(rutaSalida); try { salida =
     * Files.readAllBytes(f.getAbsoluteFile().toPath()); } catch
     * (IOException e) { throw new SysmanException(e.getMessage()); }
     * } catch (Docx4JException | IOException e) { throw new
     * SysmanException(e.getMessage()); } return salida; }
     */
    /**
     * Convierte un docx a PDF
     * 
     * @param ruta
     * @return Cadena de byte que representa el archivo en formato pdf
     * @throws SysmanException
     */
    public byte[] convertirAPdfDocx(String ruta, Boolean eliminaOrigen)
                    throws SysmanException {

        byte[] result = null;
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // Configure API key authorization: Apikey
        ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient
                        .getAuthentication("Apikey");
        Apikey.setApiKey("f0c513bc-8c00-4491-830e-3e83b015feb6");
        // Uncomment the following line to set a prefix for the API
        // key, e.g. "Token" (defaults to null)
        // Apikey.setApiKeyPrefix("Token");

        ConvertDocumentApi apiInstance = new ConvertDocumentApi();
        File inputFile = new File(ruta);
        try {
            if (ruta.endsWith(".docx")) {
                result = apiInstance.convertDocumentDocxToPdf(inputFile);
            } else if (ruta.endsWith(".doc")) {
                result = apiInstance.convertDocumentDocToPdf(inputFile);
            }
            if (eliminaOrigen) {
                inputFile.delete();
            }
        } catch (ApiException e) {
            throw new SysmanException(e.getMessage());
        }
        return result;
    }

    /**
     * Convierte un xlsx o un xls a PDF
     * 
     * @param ruta
     * @return Cadena de byte que representa el archivo en formato pdf
     * @throws SysmanException
     */
    public byte[] convertirAPdfXlsx(String ruta, Boolean eliminaOrigen)
                    throws SysmanException {

        byte[] result = null;
        ApiClient defaultClient = Configuration.getDefaultApiClient();

        // Configure API key authorization: Apikey
        ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient
                        .getAuthentication("Apikey");
        Apikey.setApiKey("f0c513bc-8c00-4491-830e-3e83b015feb6");
        // Uncomment the following line to set a prefix for the API
        // key, e.g. "Token" (defaults to null)
        // Apikey.setApiKeyPrefix("Token");

        ConvertDocumentApi apiInstance = new ConvertDocumentApi();
        File inputFile = new File(ruta);
        try {
            if (ruta.endsWith(".xlsx")) {
                result = apiInstance.convertDocumentXlsxToPdf(inputFile);
            } else if (ruta.endsWith(".xls")) {
                result = (byte[]) apiInstance
                                .convertDocumentXlsToPdf(inputFile);
            }
            if (eliminaOrigen) {
                inputFile.delete();
            }
        } catch (ApiException e) {
            throw new SysmanException(e.getMessage());
        }
        return result;
    }

    /**
     * Convierte un documento a PDF detectando por si solo el tipo de
     * documento a convertir
     * 
     * @param ruta
     * @return Cadena de byte que representa el archivo en formato pdf
     * @throws SysmanException
     * @throws FileNotFoundException 
     */
    public byte[] convertirAPdfDocumento(String ruta, Boolean eliminaOrigen, String rutaCertificado)
                    throws SysmanException, FileNotFoundException {

        byte[] result = null;
        ApiClient defaultClient = Configuration.getDefaultApiClient();

            File cacertsFile = new File(rutaCertificado);
            if (cacertsFile.exists()) {
                InputStream sslCaCert = new FileInputStream(cacertsFile);
                defaultClient.setSslCaCert(sslCaCert);
            } 
        // Configure API key authorization: Apikey
        ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient
                        .getAuthentication("Apikey");
        Apikey.setApiKey("f0c513bc-8c00-4491-830e-3e83b015feb6");
        //Apikey.setApiKey("01f4a473-c287-4f96-9235-33c52a4f33a2");
        // Uncomment the following line to set a prefix for the API
        // key, e.g. "Token" (defaults to null)
        // Apikey.setApiKeyPrefix("Token"); 
 
        ConvertDocumentApi apiInstance = new ConvertDocumentApi();
        File inputFile = new File(ruta);
        try {
            result = apiInstance.convertDocumentAutodetectToPdf(inputFile);
            if (eliminaOrigen) {
                inputFile.delete();
            }
        } catch (ApiException e) {
            throw new SysmanException(e.getMessage());
        }
        return result;
    }

}