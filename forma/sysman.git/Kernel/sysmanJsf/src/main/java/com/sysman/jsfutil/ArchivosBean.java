/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.jsfutil;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author cmanrique
 */
public class ArchivosBean {

    private static ArchivosBean instance;

    private ArchivosBean() {
    }

    public static ArchivosBean getInstance() {
        if (instance == null) {
            instance = new ArchivosBean();

        }
        return instance;
    }

    public static void generarPlano(String nombreArchivo, String contenido)
                    throws IOException {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context
                        .getExternalContext().getResponse();
        response.setContentType("txt/html");
        response.addHeader("Content-disposition",
                        "attachment; filename=" + nombreArchivo);
        ServletOutputStream stream = response.getOutputStream();
        stream.write(contenido.getBytes());
        stream.flush();
        stream.close();
        context.responseComplete();
    }

    public ByteArrayInputStream serializarPlano(String contenido) {
        return new ByteArrayInputStream(
                        contenido.getBytes());
    }
    
    public ByteArrayInputStream serializarPlano(String contenido, String encoding) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(
                        contenido.getBytes(encoding));
    }

    public StreamedContent getArchivoDescarga(InputStream contenido,
        String nombre) {
        return new DefaultStreamedContent(contenido,
                        "Content-disposition", nombre);
    }

    public StreamedContent getArchivoDescarga(InputStream contenido,
        String nombre, String contentType) {
        return new DefaultStreamedContent(contenido,
                        contentType, nombre);
    }
    
    public StreamedContent getArchivoDescarga(InputStream contenido,
        String nombre, String contentType, String encoding) {
        return new DefaultStreamedContent(contenido,
                        contentType, nombre, encoding);
    }

    public StreamedContent getStreamedContentFromFile(InputStream contenido,
        String nombre)
                        throws FileNotFoundException {
        StreamedContent archivoDescarga;
        try {
            HttpServletRequest request;
            request = (HttpServletRequest) FacesContext.getCurrentInstance()
                            .getExternalContext().getRequest();
            InputStream inputStream = contenido;
            archivoDescarga = new DefaultStreamedContent(inputStream,
                            request.getSession().getServletContext()
                                            .getMimeType(nombre),
                            nombre);
            return archivoDescarga;
        }
        catch (Exception e) {
            Logger.getLogger(ArchivosBean.class.getName())
                            .log(Level.SEVERE, null, e);
            archivoDescarga = null;
        }
        return archivoDescarga;
    }

}
