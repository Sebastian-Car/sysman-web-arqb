/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.componentes;

import com.sysman.controladores.SessionUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author cmanrique
 */
@FacesComponent("FileChooserForm")
public class FileChooserForm extends UINamingContainer {

    public static final String NOMBRE_PAR_CONTENEDOR = "contenedor";

    private List<File> listaArch;
    private String rutaPlantillas;
    private boolean cargado;
    private String seleccionado;
    private StreamedContent archivoDescarga;

    public FileChooserForm() {
        //
    }

    public List<File> getListaArch() {
        rutaPlantillas = SessionUtil.getRuta(SessionUtil.getModulo())
            + "plantillas" + File.separator
            + getAttributes().get("formulario");
        if ((listaArch == null) || !cargado) {
            File directorio = new File(rutaPlantillas);
            directorio.mkdir();
            listaArch = Arrays.asList(directorio.listFiles());
            cargado = true;
        }
        return listaArch;
    }

    public String formatearFechaModificado(long time) throws ParseException {
        Date aux = new Date(time);
        return SysmanFunciones.convertirAFechaCadena(aux);
    }

    public void upload(FileUploadEvent event) {
        UploadedFile uFile = event.getFile();
        byte[] bytes = uFile.getContents();
        String nombreArch = new String(
			            uFile.getFileName().getBytes(Charset.defaultCharset()),
			            StandardCharsets.UTF_8);
        nombreArch = nombreArch.contains(File.separator)
            ? nombreArch.substring(
			                nombreArch.lastIndexOf(File.separator) + 1,
			                nombreArch.length())
            : nombreArch;
            
        File file = new File(rutaPlantillas + File.separator + nombreArch);
        
        try (FileOutputStream fStream = new FileOutputStream(file);
             BufferedOutputStream stream = new BufferedOutputStream(fStream)) {
            stream.write(bytes);
            // Opcional: forzar el volcado (flush) para mayor seguridad, aunque el close() automático lo hará.
            // stream.flush(); 
            
            cargado = false;
            seleccionado = file.getName();
            JsfUtil.agregarMensajeInformativo("El archivo " + file.getName()
                + " ha sido guardado correctamente.");
            ((ContenedorArchivo) getAttributes().get(NOMBRE_PAR_CONTENEDOR))
                .setArchivo(file);
        }
        catch (IOException ex) {
            Logger.getLogger(FileChooserForm.class.getName()).log(Level.SEVERE,
                null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void onRowSelectArchivo(SelectEvent event) {
        ((ContenedorArchivo) getAttributes().get("contenedor"))
                        .setArchivo((File) event.getObject());
    }

    public void eliminar(File archivo) {
        if (archivo.delete()) {
            JsfUtil.agregarMensajeInformativo("El archivo " + archivo.getName()
                + " ha sido eliminado correctamente.");
            if (archivo.getName().equals(seleccionado)
                || seleccionado.isEmpty()) {
                seleccionado = null;
                ((ContenedorArchivo) getAttributes().get(NOMBRE_PAR_CONTENEDOR))
                                .setArchivo(null);
            }
            cargado = false;
        }
    }

    public void descargar(File archivo) {
        try (InputStream fis = new FileInputStream(archivo)) {

            byte[] vec = new byte[(int) archivo.length()];
            fis.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), archivo.getName());
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(FileChooserForm.class.getName()).log(Level.SEVERE,
                            null, ex);
        }

    }

    public String getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(String seleccionado) {
        this.seleccionado = seleccionado;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public Boolean getRenderUserId() {
        return (Boolean) getStateHelper().eval("renderUserId", Boolean.FALSE);
    }

    public void setRenderUserId(Boolean renderUserId) {
        getStateHelper().put("renderUserId", renderUserId);
    }

}
