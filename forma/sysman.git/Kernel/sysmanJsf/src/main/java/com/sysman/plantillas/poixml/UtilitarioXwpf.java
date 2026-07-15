/*-
 * UtilitarioXwpf.java
 *
 * 1.0
 * 
 * 25/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plantillas.poixml;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CustomXWPFDocument;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plantillas.UtilitarioPlantillas;
import com.sysman.plantillas.enums.PlantillasEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.ConvertirPdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Funcionalidades para trabajar con documentos de Microsoft Word 2007. Este
 * utilitario est&aacute; basado en la clase UtilitarioXwpfDocument creado por
 * jacelas.
 * 
 * @version 2.0, 25/01/2018
 * @author jrodrigueza
 *
 */
public class UtilitarioXwpf extends UtilitarioPoiXml {
    /**
     * numero de m&aacute;ximo de p&aacute;ginas que puede tener una plantilla.
     */
    private static final int NUMERO_PAGINAS = 100;
    /**
     * Fuente de texto identificada en las variables de tabla.
     */
    private String tablesFontFamily;

	@EJB
	private static ConvertirPdf convertir = new ConvertirPdf();
    /**
     * @param service
     * servicio
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla
     * @param fechaPlantilla
     * cadena que representa la fecha de la plantilla con formato Oracle
     * @param variablesModelo
     * variables personzalizadas creadas por el usuario
     * @param variablesConsulta
     * variables de reemplazo para la consulta
     */
    public UtilitarioXwpf(String codigoPlantilla,
        String fechaPlantilla, Map<String, String> variablesModelo,
        Map<String, String> variablesConsulta) {
        super(codigoPlantilla, fechaPlantilla, variablesModelo,
                        variablesConsulta, null);
    }

    /**
     * @param service
     * servicio
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla
     * @param fechaPlantilla
     * cadena que representa la fecha de la plantilla con formato Oracle
     * @param variablesModelo
     * variables personzalizadas creadas por el usuario
     * @param variablesConsulta
     * variables de reemplazo para la consulta
     * @param datosSesion
     * datos de sesi&oacute;n en caso de que no se pueda usar variables de
     * sesi&oacute;n.
     */
    public UtilitarioXwpf(String codigoPlantilla,
        String fechaPlantilla, Map<String, String> variablesModelo,
        Map<String, String> variablesConsulta, DatosSesion datosSesion) {
        super(codigoPlantilla, fechaPlantilla, variablesModelo,
                        variablesConsulta, datosSesion);
    }

    /**
     * Realiza el reemplazo de variables de usuario, de consulta y de tabla en
     * el documento referenciado como plantilla. Si el documento supera las
     * {@value #NUMERO_PAGINAS} p&aacute;ginas se genera un nuevo documento.
     * 
     * @return arreglo de documentos
     * @throws SystemException
     * si ocurren problemas en la creaci&oacute;n de documentos
     */
    private ByteArrayOutputStream[] generarPlantilla() throws SystemException {
        File plantilla;
        ByteArrayOutputStream[] arregloDocumentos;
        try {
            plantilla = getArchivoPlantilla();
            /*
             * Documento para acumular el archivo que se imprime al final
             */
            FileInputStream archivoDestino = new FileInputStream(plantilla);
            CustomXWPFDocument documentoDestino = new CustomXWPFDocument(
                            archivoDestino);
            /*
             * Documento copia de la plantilla el cual se reemplazan los valores
             */
            FileInputStream fl = new FileInputStream(plantilla);
            CustomXWPFDocument doc = new CustomXWPFDocument(fl);
            /*
             * Documento copia de la plantilla el cual se reemplazan los valores
             */
            FileInputStream fl3 = new FileInputStream(plantilla);
            CustomXWPFDocument docPlant = new CustomXWPFDocument(fl3);
            /*
             * Documento temporal de la plantilla para remplazar cuando se
             * agrega el doc al descDoc que acumula
             */
            FileInputStream fl2 = new FileInputStream(plantilla);
            CustomXWPFDocument doctmp = new CustomXWPFDocument(fl2);
            unirRuns(docPlant);
            unirRuns(doctmp);
            // Agregar salto de pagina al ultimo parrafo del documento
            XWPFParagraph pr1 = (XWPFParagraph) doc.getBodyElements()
                            .get(doc.getBodyElements().size() - 1);
            XWPFRun run = pr1.createRun();
            run.addBreak(BreakType.PAGE);
            // Agregar salto de pagina al ultimo parrafo del documento
            XWPFParagraph pr2 = (XWPFParagraph) doctmp.getBodyElements()
                            .get(doctmp.getBodyElements().size() - 1);
            XWPFRun run2 = pr2.createRun();
            run2.addBreak(BreakType.PAGE);
            XWPFParagraph pr;
            XWPFRun ru;
            FileInputStream objFl = new FileInputStream(plantilla);
            doc = new CustomXWPFDocument(objFl);
            pr = (XWPFParagraph) doc.getBodyElements()
                            .get(doc.getBodyElements().size() - 1);
            ru = pr.createRun();
            ru.addBreak(BreakType.PAGE);
            int tamanio = calcularNumeroDocumentos(datos);
            arregloDocumentos = new ByteArrayOutputStream[tamanio];
            int registroNumero = 0;
            int docuementoNumero = 0;
            FileInputStream tmp;
            /*
             * inicio de ambio de documentos a archivo final
             */
            for (int i = 0; i < datos.size(); i++) {
                tmp = new FileInputStream(plantilla);
                doc = new CustomXWPFDocument(tmp);
                unirRuns(doc);
                /*
                 * Agregar salto de pagina al ultimo parrafo del documento
                 */
                XWPFParagraph prtmp = (XWPFParagraph) doc.getBodyElements()
                                .get(doc.getBodyElements().size() - 1);
                XWPFRun runTmp = prtmp.createRun();
                runTmp.addBreak(BreakType.PAGE);
                reemplazarInformacionPlantilla(doc, datos, i, columnasConsulta,
                                variablesModelo);
                reemplazarVariablesDeTabla(doc, variablesTabla, i, datos,
                                variablesReemplazoConsulta);
                // Crear documento temporal
                copyLayout(doc, documentoDestino);
                /*
                 * Traslado de los elementos, que no estén en el cuerpo del
                 * documento, del archivo origen al destino
                 */
                exportarElementos(doc, documentoDestino);
                // Traslado del cuerpo del documento
                for (IBodyElement bodyElement : doc.getBodyElements()) {
                    if (bodyElement.getElementType().compareTo(
                                    BodyElementType.PARAGRAPH) == 0) {
                        XWPFParagraph srcPr = (XWPFParagraph) bodyElement;
                        copyStyle(doc, documentoDestino, doc.getStyles()
                                        .getStyle(srcPr.getStyleID()));
                        boolean hasImage = false;
                        XWPFParagraph dstPr = documentoDestino
                                        .createParagraph();
                        /*
                         * Extract image from source docx file and insert into
                         * destination docx file.
                         */
                        for (XWPFRun srcRun : srcPr.getRuns()) {
                            dstPr.createRun();
                            hasImage = !srcRun.getEmbeddedPictures().isEmpty();
                            for (XWPFPicture pic : srcRun
                                            .getEmbeddedPictures()) {
                                byte[] img = pic.getPictureData().getData();
                                long cx = pic.getCTPicture().getSpPr().getXfrm()
                                                .getExt().getCx();
                                long cy = pic.getCTPicture().getSpPr().getXfrm()
                                                .getExt().getCy();
                                // Working addPicture Code below...
                                String blipId = dstPr.getDocument()
                                                .addPictureData(new ByteArrayInputStream(
                                                                img),
                                                                Document.PICTURE_TYPE_PNG);
                                documentoDestino.createPictureCxCy(blipId,
                                                documentoDestino.getNextPicNameNumber(
                                                                Document.PICTURE_TYPE_PNG),
                                                cx, cy);
                            }
                        }
                        if (!hasImage) {
                            srcPr.setSpacingAfter(0);
                            srcPr.setSpacingAfterLines(0);
                            int pos = documentoDestino.getParagraphs().size()
                                - 1;
                            documentoDestino.setParagraph(srcPr, pos);
                        }
                    }
                    else
                        if (bodyElement.getElementType()
                                        .compareTo(BodyElementType.TABLE) == 0) {
                                            XWPFTable table = (XWPFTable) bodyElement;
                                            copyStyle(doc, documentoDestino, doc
                                                            .getStyles()
                                                            .getStyle(table.getStyleID()));
                                            documentoDestino.createTable();
                                            int pos = documentoDestino
                                                            .getTables().size()
                                                - 1;
                                            documentoDestino.setTable(pos,
                                                            table);
                                        }
                }
                registroNumero++;
                if (((registroNumero % NUMERO_PAGINAS) == 0)
                    || (registroNumero == datos.size())) {
                    eliminarPlantilla(docPlant, documentoDestino);
                    ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                    documentoDestino.write(out2);
                    arregloDocumentos[docuementoNumero] = out2;
                    documentoDestino = new CustomXWPFDocument(
                                    new FileInputStream(plantilla));
                    docuementoNumero++;
                }
            }
        }
        catch (IOException | InvalidFormatException e) {
            throw new SystemException(e);
        }
        return arregloDocumentos;
    }
    
    
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    private ByteArrayOutputStream[] generarPlantilla1() throws SystemException {
        File plantilla;
        ByteArrayOutputStream[] arregloDocumentos;
        try {
            plantilla = getArchivoPlantilla();
            /*
             * Documento para acumular el archivo que se imprime al final
             */
            FileInputStream archivoDestino = new FileInputStream(plantilla);
            CustomXWPFDocument documentoDestino = new CustomXWPFDocument(
                            archivoDestino);
            /*
             * Documento copia de la plantilla el cual se reemplazan los valores
             */
            FileInputStream fl = new FileInputStream(plantilla);
            CustomXWPFDocument doc = new CustomXWPFDocument(fl);
            /*
             * Documento copia de la plantilla el cual se reemplazan los valores
             */
            FileInputStream fl3 = new FileInputStream(plantilla);
            CustomXWPFDocument docPlant = new CustomXWPFDocument(fl3);
            /*
             * Documento temporal de la plantilla para remplazar cuando se
             * agrega el doc al descDoc que acumula
             */
            FileInputStream fl2 = new FileInputStream(plantilla);
            CustomXWPFDocument doctmp = new CustomXWPFDocument(fl2);
            unirRuns(docPlant);
            unirRuns(doctmp);
            // Agregar salto de pagina al ultimo parrafo del documento
            XWPFParagraph pr1 = (XWPFParagraph) doc.getBodyElements()
                            .get(doc.getBodyElements().size() - 1);
            XWPFRun run = pr1.createRun();
            run.addBreak(BreakType.PAGE);
            // Agregar salto de pagina al ultimo parrafo del documento
            XWPFParagraph pr2 = (XWPFParagraph) doctmp.getBodyElements()
                            .get(doctmp.getBodyElements().size() - 1);
            XWPFRun run2 = pr2.createRun();
            run2.addBreak(BreakType.PAGE);
            XWPFParagraph pr;
            XWPFRun ru;
            FileInputStream objFl = new FileInputStream(plantilla);
            doc = new CustomXWPFDocument(objFl);
            pr = (XWPFParagraph) doc.getBodyElements()
                            .get(doc.getBodyElements().size() - 1);
            ru = pr.createRun();
            ru.addBreak(BreakType.PAGE);
            int tamanio = calcularNumeroDocumentos(datos);
            arregloDocumentos = new ByteArrayOutputStream[tamanio];
            int registroNumero = 0;
            int docuementoNumero = 0;
            FileInputStream tmp;
            /*
             * inicio de ambio de documentos a archivo final
             */
            for (int i = 0; i < datos.size(); i++) {
                tmp = new FileInputStream(plantilla);
                doc = new CustomXWPFDocument(tmp);
                unirRuns(doc);
                /*
                 * Agregar salto de pagina al ultimo parrafo del documento
                 */
                XWPFParagraph prtmp = (XWPFParagraph) doc.getBodyElements()
                                .get(doc.getBodyElements().size() - 1);
                XWPFRun runTmp = prtmp.createRun();
                runTmp.addBreak(BreakType.PAGE);
                reemplazarInformacionPlantilla(doc, datos, i, columnasConsulta,
                                variablesModelo);
                reemplazarVariablesDeTabla3(doc, variablesTabla, i, datos,
                        variablesReemplazoConsulta);
                reemplazarVariablesDeTabla1(doc, variablesTabla, i, datos,
                                variablesReemplazoConsulta);
                // Crear documento temporal
                copyLayout(doc, documentoDestino);
                /*
                 * Traslado de los elementos, que no estén en el cuerpo del
                 * documento, del archivo origen al destino
                 */
                exportarElementos(doc, documentoDestino);
                // Traslado del cuerpo del documento
                for (IBodyElement bodyElement : doc.getBodyElements()) {
                    if (bodyElement.getElementType().compareTo(
                                    BodyElementType.PARAGRAPH) == 0) {
                        XWPFParagraph srcPr = (XWPFParagraph) bodyElement;
                        copyStyle(doc, documentoDestino, doc.getStyles()
                                        .getStyle(srcPr.getStyleID()));
                        boolean hasImage = false;
                        XWPFParagraph dstPr = documentoDestino
                                        .createParagraph();
                        /*
                         * Extract image from source docx file and insert into
                         * destination docx file.
                         */
                        for (XWPFRun srcRun : srcPr.getRuns()) {
                            dstPr.createRun();
                            hasImage = !srcRun.getEmbeddedPictures().isEmpty();
                            for (XWPFPicture pic : srcRun
                                            .getEmbeddedPictures()) {
                                byte[] img = pic.getPictureData().getData();
                                long cx = pic.getCTPicture().getSpPr().getXfrm()
                                                .getExt().getCx();
                                long cy = pic.getCTPicture().getSpPr().getXfrm()
                                                .getExt().getCy();
                                // Working addPicture Code below...
                                String blipId = dstPr.getDocument()
                                                .addPictureData(new ByteArrayInputStream(
                                                                img),
                                                                Document.PICTURE_TYPE_PNG);
                                documentoDestino.createPictureCxCy(blipId,
                                                documentoDestino.getNextPicNameNumber(
                                                                Document.PICTURE_TYPE_PNG),
                                                cx, cy);
                            }
                        }
                        if (!hasImage) {
                            srcPr.setSpacingAfter(0);
                            srcPr.setSpacingAfterLines(0);
                            int pos = documentoDestino.getParagraphs().size()
                                - 1;
                            documentoDestino.setParagraph(srcPr, pos);
                        }
                    }
                    else
                        if (bodyElement.getElementType()
                                        .compareTo(BodyElementType.TABLE) == 0) {
                                            XWPFTable table = (XWPFTable) bodyElement;
                                            copyStyle(doc, documentoDestino, doc
                                                            .getStyles()
                                                            .getStyle(table.getStyleID()));
                                            documentoDestino.createTable();
                                            int pos = documentoDestino
                                                            .getTables().size()
                                                - 1;
                                            documentoDestino.setTable(pos,
                                                            table);
                                        }
                }
                registroNumero++;
                if (((registroNumero % NUMERO_PAGINAS) == 0)
                    || (registroNumero == datos.size())) {
                    eliminarPlantilla(docPlant, documentoDestino);
                    ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                    documentoDestino.write(out2);
                    arregloDocumentos[docuementoNumero] = out2;
                    documentoDestino = new CustomXWPFDocument(
                                    new FileInputStream(plantilla));
                    docuementoNumero++;
                }
            }
        }
        catch (IOException | InvalidFormatException e) {
            throw new SystemException(e);
        }
        return arregloDocumentos;
    }

    /**
     * Calcular el tamaño del vector de documentos si es par o impar la división
     * suma una casilla
     * 
     * @param datos
     * @return n&uacute;mero de documentos que van a ser comprimidos.
     */
    private int calcularNumeroDocumentos(List<Registro> datos) {
        if ((datos.size() % NUMERO_PAGINAS) != 0) {
            return (datos.size() / NUMERO_PAGINAS) + 1;
        }
        else {
            return datos.size() / NUMERO_PAGINAS;
        }
    }

    /**
     * 
     * @param document
     */
    private void unirRuns(CustomXWPFDocument document) {
        // Une runs en el encabezado del documento
        for (XWPFHeader header : document.getHeaderList()) {
            unirRunsCuerpo(header.getBodyElements());
        }
        // Reemplazo en Pies de Página
        for (XWPFFooter footer : document.getFooterList()) {
            unirRunsCuerpo(footer.getBodyElements());
        }
        // Une runs en el cuerpo del documento
        unirRunsCuerpo(document.getBodyElements());
    }

    /**
     * 
     * @param bodyElements
     */
    private void unirRunsCuerpo(List<IBodyElement> bodyElements) {
        for (IBodyElement bodyElement : bodyElements) {
            if (bodyElement.getElementType()
                            .compareTo(BodyElementType.PARAGRAPH) == 0) {
                unirRunsParrafos((XWPFParagraph) bodyElement);
            }
            if (bodyElement.getElementType()
                            .compareTo(BodyElementType.TABLE) == 0) {
                unirRunsTablas((XWPFTable) bodyElement);
            }
        }
    }

    /**
     * 
     * @param table
     */
    private void unirRunsTablas(XWPFTable table) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (IBodyElement bodyElement : cell.getBodyElements()) {
                    if (bodyElement.getElementType().compareTo(
                                    BodyElementType.PARAGRAPH) == 0) {
                        unirRunsParrafos((XWPFParagraph) bodyElement);
                    }
                    if (bodyElement.getElementType()
                                    .compareTo(BodyElementType.TABLE) == 0) {
                        unirRunsTablas((XWPFTable) bodyElement);
                    }
                }
            }
        }
    }

    /**
     * 
     * @param paragraph
     */
    private void unirRunsParrafos(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String text = run.getText(0) != null ? run.getText(0) : "";
            if ("<".equals(text.trim()) || "<#".equals(text.trim())
                || text.contains("<#") || text.contains("<")) {
                StringBuilder compilado = new StringBuilder(text);
                for (int j = i + 1; j < runs.size(); j++) {
                    String next = runs.get(j).getText(0) != null
                        ? runs.get(j).getText(0)
                        : "";
                    if (">".equals(next.trim()) || "#>".equals(next)
                        || next.contains("#>")
                        || next.contains(">")) {
                        for (int k = i + 1; k <= j; k++) {
                            compilado.append(runs.get(k).getText(0) != null
                                ? runs.get(k).getText(0)
                                : " ");
                            runs.get(k).setText("", 0);
                        }
                    }
                }
                run.setText(compilado.toString(), 0);
            }
        }
    }

    /**
     * Reemplazo de variables en parrafos y tablas existentes en el documento.
     * 
     * @param doc
     * @param docPlant
     * @param datos
     * @param i
     * @param columnas
     * @param variables
     */
    private void reemplazarInformacionPlantilla(CustomXWPFDocument doc,
        List<Registro> datos, int i, List<String> columnas,
        Map<String, String> variables) {
        // Reemplazo en Encabezados
        for (XWPFHeader header : doc.getHeaderList()) {
            replaceAllBodyElements(header.getBodyElements(), columnas, datos, i,
                            variables);
        }
        // Reemplazo en Pies de Página
        for (XWPFFooter footer : doc.getFooterList()) {
            replaceAllBodyElements(footer.getBodyElements(), columnas, datos, i,
                            variables);
        }
        // Reemplazo en el cuerpo del documento
        replaceAllBodyElements(doc.getBodyElements(), columnas, datos, i,
                        variables);
    }

    /**
     * Reemplaza las variables que encuentre en los Runs de los parrafos, del
     * cuerpo o tablas existentes, en la lista de elementos ingresada por
     * parametro. Basado en la solución propuesta en:
     * http://stackoverflow.com/a/30131160
     * 
     * @param bodyElements
     * @param columnas
     * @param datos
     * @param i
     * @param variables
     * 
     */
    private void replaceAllBodyElements(List<IBodyElement> bodyElements,
        List<String> columnas, List<Registro> datos, int i,
        Map<String, String> variables) {
        for (IBodyElement bodyElement : bodyElements) {
            if (bodyElement.getElementType()
                            .compareTo(BodyElementType.PARAGRAPH) == 0) {
                replaceParagraph((XWPFParagraph) bodyElement, columnas, datos,
                                i, variables);
            }
            if (bodyElement.getElementType()
                            .compareTo(BodyElementType.TABLE) == 0) {
                replaceTable((XWPFTable) bodyElement, columnas, datos, i,
                                variables);
            }
        }
    }

    /**
     * Remplazo de variables según el parrafo ingresado por parámetro.
     * 
     * @param paragraph
     * @param columnas
     * @param datos
     * @param indice
     * @param variables
     */
    private void replaceParagraph(XWPFParagraph paragraph,
        List<String> columnas, List<Registro> datos, int indice,
        Map<String, String> variables) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int j = 0; j < runs.size(); j++) {
            XWPFRun run = runs.get(j);
            String text = run.getText(0) != null ? run.getText(0) : "";
            if (text.contains("<#") && text.contains("#>")) {
                if (text.equals("<#FIRMADIGITAL#>")) {

                    Map<String, Object> v = datos.get(indice).getCampos();
                    String a = (String) v.get("FIRMADIGITAL");
                    if (a != null && !a.isEmpty()) {
                        try {
                            InputStream pic = new FileInputStream(a);
                            run.addPicture(pic, XWPFDocument.PICTURE_TYPE_PNG,
                                            "image file", Units.toEMU(250),
                                            Units.toEMU(100));
                        }
                        catch (InvalidFormatException | IOException e1) {
                            logger.error(e1.getMessage(), e1);
                            JsfUtil.agregarMensajeError(e1.getMessage());
                        }
                    }
                }
                else if (text.contains("<#IMG_ASP_")) {

                    Map<String, Object> v = datos.get(indice).getCampos();
                    String a = (String) v.get(text.replaceAll("<#|#>", ""));
                    if (a != null && !a.isEmpty()) {
                        try {
                        	File imageFile = new File(a);
                            BufferedImage bufferedImage = ImageIO.read(imageFile);
                            double width = bufferedImage.getWidth() <= 490 ? bufferedImage.getWidth(): 490;
                            double height = bufferedImage.getHeight() <= 490 ? bufferedImage.getHeight(): 490;                            
                            
                            InputStream pic = new FileInputStream(a);
                            run.addPicture(pic, XWPFDocument.PICTURE_TYPE_PNG,
                                            "image file", Units.toEMU(width),
                                            Units.toEMU(height));
                        }
                        catch (InvalidFormatException | IOException e1) {
                            logger.error(e1.getMessage(), e1);
                            JsfUtil.agregarMensajeError(e1.getMessage());
                        }
                    }
                }
                else {
                    // La variable viene como un único run
                    String texto = cambiarReemplazarTexto(text, columnas,
                                    datos,
                                    indice, variables);

                    guardarTextoRun(texto, run);
                }

            }
            else
                if ("<#".trim().equals(text) || "<".trim().equals(text)
                    || text.contains("<#") || text.contains("<")) {
                        // Varios runs conforman una variable
                        for (int k = j + 1; k < runs.size(); k++) {
                            String next = runs.get(k).getText(0) != null
                                ? runs.get(k).getText(0)
                                : "";
                            if ("#>".trim().equals(next)
                                || ">".trim().equals(next)
                                || next.contains("#>") || next.contains(">")) {
                                for (int l = j + 1; l <= k; l++) {
                                    text = text + runs.get(l).getText(0);
                                    runs.get(l).setText("", 0);
                                }
                                String texto = cambiarReemplazarTexto(text,
                                                columnas,
                                                datos, indice, variables);
                                guardarTextoRun(texto, run);
                                break;
                            }
                        }
                    }
        }

    }

    /**
     * Metodo que recibe string texto y busca etiquetas para remplazar en base a
     * las columnas de la consulta de las plantillas de word. Se agrego el
     * reemplazar etiquedas de un vector de etiquetas tambien
     * 
     * @param text
     * @param columnas
     * @param datos
     * @param i
     * @param variables
     * @return
     */
    private String cambiarReemplazarTexto(String text, List<String> columnas,
        List<Registro> datos, int i,
        Map<String, String> variables) {
        if (text != null) {
            // Busca que contenga alguna etiqueta
            if (text.contains("<#") && text.contains("#>")) {
                /*
                 * Recorre el vector de columnas y si encuentra una etiqueta con
                 * el mismo nombre la reemplaza
                 */
                for (int j = 0; j < columnas.size(); j++) {
                    if (text.contains("<#" + columnas.get(j) + "#>")) {
                        String valor = SysmanFunciones.toString(datos.get(i)
                                        .getCampos().get(columnas.get(j)));
                        text = text.replace("<#" + columnas.get(j) + "#>",
                                        valor != null ? valor : " ");
                    }
                }
                // Recorre el vector de variables y si encuentra una
                // etiqueta con el
                // mismo nombre la reemplaza
                Iterator it = variables.entrySet().iterator();
                int posVar = 0;
                while (it.hasNext()) {
                    Map.Entry e = (Map.Entry) it.next();
                    if (text.contains(e.getKey().toString())) {
                        String valor = e.getValue().toString();
                        text = text.replace(e.getKey().toString(), valor);
                    }
                    posVar++;
                }
            }
        }
        return text;
    }

    /**
     * Busca los parrafos que existan en la tabla principal y tablas internas,
     * para hacer el respectivo reemplazo de variables.
     * 
     * @param table
     * @param columnas
     * @param datos
     * @param indice
     * @param variables
     */
    private void replaceTable(XWPFTable table, List<String> columnas,
        List<Registro> datos, int indice,
        Map<String, String> variables) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (IBodyElement bodyElement : cell.getBodyElements()) {
                    if (bodyElement.getElementType().compareTo(
                                    BodyElementType.PARAGRAPH) == 0) {
                        replaceParagraph((XWPFParagraph) bodyElement, columnas,
                                        datos, indice, variables);
                    }
                    if (bodyElement.getElementType()
                                    .compareTo(BodyElementType.TABLE) == 0) {
                        replaceTable((XWPFTable) bodyElement, columnas, datos,
                                        indice, variables);
                    }
                }
            }
        }
    }

    /**
     * Reemplazo de variables que representan tablas.
     * 
     * @param doc
     * @param etiquetasTabla
     * @param i
     * @param datos
     * @param variablesConsultaWord
     * @throws SystemException
     */
    public void reemplazarVariablesDeTabla(CustomXWPFDocument doc,
        List<Registro> etiquetasTabla, int i,
        List<Registro> datos, Map<String, String> variablesConsultaWord)
                    throws SystemException {
        if (doc != null) {
            List<IBodyElement> elements = doc.getBodyElements();
            if (elements != null) {
                for (int j = 0; j < elements.size(); j++) {
                    IBodyElement bodyElement = elements.get(j);
                    if (bodyElement.getElementType().compareTo(
                                    BodyElementType.PARAGRAPH) == 0) {
                        reemplazarTablasEnParrafos((XWPFParagraph) bodyElement,
                                        datos.get(i).getCampos(),
                                        etiquetasTabla,
                                        variablesConsultaWord, doc);
                    }
                    else
                        if (bodyElement.getElementType()
                                        .compareTo(BodyElementType.TABLE) == 0) {
                                            reemplazarTablasInternas(
                                                            (XWPFTable) bodyElement,
                                                            datos.get(i).getCampos(),
                                                            etiquetasTabla,
                                                            variablesConsultaWord);
                                        }
                }
            }
        }
    }
    
    
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    public void reemplazarVariablesDeTabla1(CustomXWPFDocument doc,
            List<Registro> etiquetasTabla, int i,
            List<Registro> datos, Map<String, String> variablesConsultaWord)
                        throws SystemException {
            if (doc != null) {
                List<IBodyElement> elements = doc.getBodyElements();
                if (elements != null) {
                    for (int j = 0; j < elements.size(); j++) {
                        IBodyElement bodyElement = elements.get(j);
                        if (bodyElement.getElementType().compareTo(
                                        BodyElementType.PARAGRAPH) == 0) {
                            reemplazarTablasEnParrafos1((XWPFParagraph) bodyElement,
                                            datos.get(i).getCampos(),
                                            etiquetasTabla,
                                            variablesConsultaWord, doc);
                        }
                        else
                            if (bodyElement.getElementType()
                                            .compareTo(BodyElementType.TABLE) == 0) {
                                                reemplazarTablasInternas(
                                                                (XWPFTable) bodyElement,
                                                                datos.get(i).getCampos(),
                                                                etiquetasTabla,
                                                                variablesConsultaWord);
                                            }
                    }
                }
            }
        }

    /**
     * Busqueda de variables en celdas de tablas internas, que deban ser
     * reemplazadas por tablas.
     * 
     * @param table
     * @param campos
     * @param etiquetasTabla
     * @param variablesConsultaWord
     * @param doc
     * @throws SystemException
     */
    private void reemplazarTablasInternas(XWPFTable table,
        Map<String, Object> campos, List<Registro> etiquetasTabla,
        Map<String, String> variablesConsultaWord) throws SystemException {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                List<IBodyElement> elements = cell.getBodyElements();
                if (elements != null) {
                    for (int i = 0; i < elements.size(); i++) {
                        IBodyElement bodyElement = elements.get(i);
                        if (bodyElement.getElementType().compareTo(
                                        BodyElementType.PARAGRAPH) == 0) {
                            reemplazarTablasEnParrafos(
                                            (XWPFParagraph) bodyElement, campos,
                                            etiquetasTabla,
                                            variablesConsultaWord, cell);
                        }
                        else
                            if (bodyElement.getElementType().compareTo(
                                            BodyElementType.TABLE) == 0) {
                                                reemplazarTablasInternas(
                                                                (XWPFTable) bodyElement,
                                                                campos,
                                                                etiquetasTabla,
                                                                variablesConsultaWord);
                                            }
                    }
                }
            }
        }
    }

    /**
     * Busqueda de variables que deban ser reemplazadas por tablas.
     * 
     * @param paragraph
     * @param camposDatos
     * @param etiquetasTabla
     * @param variablesConsultaWord
     * @param contenedor
     * Elemento en el que está posicionado el parrafo.
     * @throws SystemException
     */
    private void reemplazarTablasEnParrafos(XWPFParagraph paragraph,
        Map<String, Object> camposDatos, List<Registro> etiquetasTabla,
        Map<String, String> variablesConsultaWord, IBody contenedor)
                    throws SystemException {
        String text = paragraph.getText();
        if (paragraph.getRuns() != null && !paragraph.getRuns().isEmpty()) {
            tablesFontFamily = paragraph.getRuns().get(0).getFontFamily();
        }
        else {
            tablesFontFamily = "Arial";
        }
        if (text.contains("<#") || text.contains("#>")) {
            for (Registro registro : etiquetasTabla) {
                Map<String, Object> camposEtiquetas = registro.getCampos();
                String nombre = (String) camposEtiquetas
                                .get(GeneralParameterEnum.NOMBRE.getName());
                if (text.contains(nombre)) {
                    String consulta = armarConsulta(camposEtiquetas,
                                    camposDatos, variablesConsultaWord);
                    List<String> columnas = service.getCamposListado(
                                    ConectorPool.ESQUEMA_SYSMAN, consulta);
                    List<Registro> datos = service.getListado(
                                    ConectorPool.ESQUEMA_SYSMAN, consulta);

                    String sql = "SELECT ETIQUETA, FORMATO \n"
                        + "FROM MODELO_VARIABLES \n"
                        + "WHERE PLANTILLA = '" + codigoPlantilla + "' \n"
                        + "AND FECHA = " + fechaPlantilla + " \n"
                        + "AND TIPO = 'T' AND DESCRIPCION = '"
                        + camposEtiquetas.get(
                                        GeneralParameterEnum.NOMBRE.getName())
                        + "'";
                    List<Registro> tiposDatos = service.getListado(
                                    ConectorPool.ESQUEMA_SYSMANK, sql);
                    try {
                        datos = formatoColumnas(datos, tiposDatos);
                    }
                    catch (ParseException e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (!datos.isEmpty()) {
                        if (contenedor instanceof CustomXWPFDocument) {
                            int pos = contenedor.getXWPFDocument()
                                            .getPosOfParagraph(paragraph);
                            contenedor.getXWPFDocument().removeBodyElement(pos);
                            if (pos != -1) {
                                XWPFTable tabla = crearTabla(columnas, datos,
                                                contenedor, camposEtiquetas);
                                contenedor.getXWPFDocument().insertTable(pos,
                                                tabla);
                            }
                        }
                        else {
                            cambiarTexto(paragraph, "");
                            XmlCursor cursor = paragraph.getCTP().newCursor();
                            XWPFTable tabla = contenedor.insertNewTbl(cursor);
                            crearTabla(columnas, datos, tabla, camposEtiquetas);
                        }
                    }
                    else {
                        cambiarTexto(paragraph, "");
                    }
                }
            }
        }
    }
    
    
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    private void reemplazarTablasEnParrafos1(XWPFParagraph paragraph,
            Map<String, Object> camposDatos, List<Registro> etiquetasTabla,
            Map<String, String> variablesConsultaWord, IBody contenedor)
                        throws SystemException {
            String text = paragraph.getText();
            if (paragraph.getRuns() != null && !paragraph.getRuns().isEmpty()) {
                tablesFontFamily = paragraph.getRuns().get(0).getFontFamily();
            }
            else {
                tablesFontFamily = "Arial";
            }
            if (text.contains("<#") || text.contains("#>")) {
                for (Registro registro : etiquetasTabla) {
                    Map<String, Object> camposEtiquetas = registro.getCampos();
                    String nombre = (String) camposEtiquetas
                                    .get(GeneralParameterEnum.NOMBRE.getName());
                    if (text.contains(nombre)) {
                        String consulta = armarConsulta(camposEtiquetas,
                                        camposDatos, variablesConsultaWord);
                        List<String> columnas = service.getCamposListado(
                                        ConectorPool.ESQUEMA_SYSMAN, consulta);
                        List<Registro> datos = service.getListado(
                                        ConectorPool.ESQUEMA_SYSMAN, consulta);

                        String sql = "SELECT ETIQUETA, FORMATO \n"
                            + "FROM MODELO_VARIABLES \n"
                            + "WHERE PLANTILLA = '" + codigoPlantilla + "' \n"
                            + "AND FECHA = " + fechaPlantilla + " \n"
                            + "AND TIPO = 'T' AND DESCRIPCION = '"
                            + camposEtiquetas.get(
                                            GeneralParameterEnum.NOMBRE.getName())
                            + "'";
                        List<Registro> tiposDatos = service.getListado(
                                        ConectorPool.ESQUEMA_SYSMANK, sql);
                        try {
                            datos = formatoColumnas(datos, tiposDatos);
                        }
                        catch (ParseException e) {
                            logger.error(e.getMessage(), e);
                        }
                        if (!datos.isEmpty()) {
                            if (contenedor instanceof CustomXWPFDocument) {
                                int pos = contenedor.getXWPFDocument()
                                                .getPosOfParagraph(paragraph);
                                contenedor.getXWPFDocument().removeBodyElement(pos);
                                if (pos != -1) {
                                    XWPFTable tabla = crearTabla1(columnas, datos,
                                                    contenedor, camposEtiquetas);
                                    contenedor.getXWPFDocument().insertTable(pos,
                                                    tabla);
                                }
                            }
                            else {
                                cambiarTexto(paragraph, "");
                                XmlCursor cursor = paragraph.getCTP().newCursor();
                                XWPFTable tabla = contenedor.insertNewTbl(cursor);
                                crearTabla(columnas, datos, tabla, camposEtiquetas);
                            }
                        }
                        else {
                            cambiarTexto(paragraph, "");
                        }
                    }
                }
            }
        }

    /**
     * Crea una tabla en base al documento, columnas y filas ingresadas por
     * parámetro.
     * 
     * @param columnas
     * Columnas de la tabla.
     * @param datos
     * Lista de datos que representan las filas de la tabla.
     * @param doc
     * Documento base para crear la tabla.
     * @return
     */
    private XWPFTable crearTabla(List<String> columnas, List<Registro> datos,
        IBody contenedor, Map<String, Object> camposEtiquetas) {
        XWPFTableRow row;
        XWPFTableCell cell;
        CTTbl ctTbl = CTTbl.Factory.newInstance();
        int numeroColumnas = columnas.size();
        int numeroDatos = datos.size();
        int fila = 0;
        String modoCreacion = camposEtiquetas
                        .get(PlantillasEnum.MODO_CREACION.getValue())
                        .toString();
        boolean manejaEstilos = false;
        Object object = camposEtiquetas
                        .get(PlantillasEnum.MANEJA_ESTILOS.getValue());
        if (object instanceof Number) {
            int indicador = Integer
                            .parseInt(SysmanFunciones.toString(object));
            manejaEstilos = indicador != 0;
        }
        else {
            manejaEstilos = Boolean
                            .parseBoolean(SysmanFunciones.toString(object));
        }
        boolean conEncabezado = Arrays.asList("D", "E").contains(modoCreacion);

        XWPFTable table = new XWPFTable(ctTbl, contenedor,
                        conEncabezado ? numeroDatos + 1 : numeroDatos,
                        numeroColumnas);
        row = table.getRow(fila);
        row.setHeight(100);
        for (int i = 0; i < numeroColumnas; i++) {
            if (conEncabezado) {
                cell = row.getCell(i);
                XWPFParagraph cellParagraph = cell.getParagraphs().get(0);
                XWPFRun cellRun = cellParagraph.createRun();
                cellRun.setFontFamily(tablesFontFamily);
                cellRun.setFontSize(8);
                cellRun.setText(columnas.get(i), 0);
//                cell.setText(columnas.get(i));
            }
        }
        for (int i = 0; i < numeroDatos; i++) {
            Map<String, Object> campos = datos.get(i).getCampos();
            if (conEncabezado || i > 0) {
                // Inicia en 1 para saltar la fila del encabezado
                fila++;
                row = table.getRow(fila);
                row.setHeight(100);

            }
            for (int j = 0; j < numeroColumnas; j++) {
                cell = row.getCell(j);
                String texto = String.valueOf(campos.get(columnas.get(j)));
                guardarTextoCelda(cell, texto != null ? texto : " ");
            }
        }
        table.getCTTbl().getTblPr().addNewJc().setVal(STJc.CENTER);

        // Bordes
        if (!manejaEstilos) {
            table.getCTTbl().getTblPr().unsetTblBorders();
        }
        return table;
    }
    
    
    private XWPFTable crearTabla1(List<String> columnas, List<Registro> datos,
            IBody contenedor, Map<String, Object> camposEtiquetas) {
            XWPFTableRow row;
            XWPFTableCell cell;
            CTTbl ctTbl = CTTbl.Factory.newInstance();
            int numeroColumnas = columnas.size();
            int numeroDatos = datos.size();
            int fila = 0;
            String modoCreacion = camposEtiquetas
                            .get(PlantillasEnum.MODO_CREACION.getValue())
                            .toString();
            boolean manejaEstilos = false;
            Object object = camposEtiquetas
                            .get(PlantillasEnum.MANEJA_ESTILOS.getValue());
            if (object instanceof Number) {
                int indicador = Integer
                                .parseInt(SysmanFunciones.toString(object));
                manejaEstilos = indicador != 0;
            }
            else {
                manejaEstilos = Boolean
                                .parseBoolean(SysmanFunciones.toString(object));
            }
            boolean conEncabezado = Arrays.asList("D", "E").contains(modoCreacion);

            XWPFTable table = new XWPFTable(ctTbl, contenedor,
                            conEncabezado ? numeroDatos + 1 : numeroDatos,
                            numeroColumnas);
            row = table.getRow(fila);
            row.setHeight(100);
            for (int i = 0; i < numeroColumnas; i++) {
                if (conEncabezado) {
                    cell = row.getCell(i);
                    XWPFParagraph cellParagraph = cell.getParagraphs().get(0);
                    XWPFRun cellRun = cellParagraph.createRun();
                    cellRun.setFontFamily(tablesFontFamily);
                    cellRun.setFontSize(8);
                    cellRun.setText(columnas.get(i), 0);
                }
            }
            for (int i = 0; i < numeroDatos; i++) {
                Map<String, Object> campos = datos.get(i).getCampos();
                if (conEncabezado || i > 0) {
                    // Inicia en 1 para saltar la fila del encabezado
                    fila++;
                    row = table.getRow(fila);
                    row.setHeight(100);

                }
                for (int j = 0; j < numeroColumnas; j++) {
                    cell = row.getCell(j);
                    String texto = String.valueOf(campos.get(columnas.get(j)));
                    XWPFParagraph cellParagraph = cell.getParagraphs().get(0);
                    XWPFRun cellRun = cellParagraph.createRun();
                    cellRun.setFontFamily(tablesFontFamily);
                    cellRun.setFontSize(8);
                    cellRun.setText(texto, 0);
                }
            }
            table.getCTTbl().getTblPr().addNewJc().setVal(STJc.CENTER);

            // Bordes
            if (!manejaEstilos) {
                table.getCTTbl().getTblPr().unsetTblBorders();
            }
            return table;
        }
    
    

    /**
     * Reemplaza el texto que tenga el parrafo.
     * 
     * @param paragraph
     * Parrafo a afectar.
     * @param texto
     * Texto que va a insertar en el parrafo.
     */
    private void cambiarTexto(XWPFParagraph paragraph, String texto) {
    	if (paragraph == null) return;
    	if (texto == null) texto = "";

    	List<XWPFRun> runs = paragraph.getRuns();
    	if (runs == null || runs.isEmpty()) {
    		paragraph.createRun().setText(texto);
    		return;
    	}

    	for (int i = runs.size() - 1; i > 0; i--) {
    		paragraph.removeRun(i);
    	}
    	paragraph.getRuns().get(0).setText(texto, 0);
    }

    /**
     * Permite crear una tabla interna. Para tablas que están dentro de una
     * celda de otra tabla.
     * 
     * @param columnas
     * Columnas de la tabla.
     * @param datos
     * Lista de datos que representan las filas de la tabla.
     * @param table
     * Tabla creada por medio de un <code>XmlCursor</code>
     * @param camposEtiquetas
     * @return
     */
    private XWPFTable crearTabla(List<String> columnas, List<Registro> datos,
        XWPFTable table, Map<String, Object> camposEtiquetas) {
        int numeroRegistros = datos.size();
        int numeroColumnas = columnas.size();
        XWPFTableRow row = table.createRow();
        XWPFTableCell cell;
        String modoCreacion = camposEtiquetas
                        .get(PlantillasEnum.MODO_CREACION.getValue())
                        .toString();
        boolean manejaEstilos = false;
        Object object = camposEtiquetas
                        .get(PlantillasEnum.MANEJA_ESTILOS.getValue());
        if (object instanceof Number) {
            int indicador = Integer
                            .parseInt(SysmanFunciones.toString(object));
            manejaEstilos = indicador != 0;
        }
        else {
            manejaEstilos = Boolean
                            .parseBoolean(SysmanFunciones.toString(object));
        }

        for (int i = 0; i < numeroColumnas; i++) {
            cell = row.createCell();
            if (Arrays.asList("D", "E").contains(modoCreacion)) {
                XWPFParagraph cellParagraph = cell.getParagraphs().get(0);
                XWPFRun cellRun = cellParagraph.createRun();
                cellRun.setFontFamily(tablesFontFamily);
                cellRun.setFontSize(8);
                cellRun.setText(columnas.get(i), 0);
            }
        }

        for (int i = 0; i < numeroRegistros; i++) {
            Map<String, Object> campos = datos.get(i).getCampos();
            if (Arrays.asList("D", "E").contains(modoCreacion) || i != 0) {
                row = table.createRow();
            }
            for (int j = 0; j < numeroColumnas; j++) {
                cell = row.getCell(j);
                String texto = String.valueOf(campos.get(columnas.get(j)));
                if (cell != null) {
                    guardarTextoCelda(cell, texto != null ? texto : " ");
                }
            }
        }
        // Alineación de la tabla
        CTTblPr tblPr = table.getCTTbl().addNewTblPr();
        CTJc jc = tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc();
        jc.setVal(STJc.CENTER);
        // Ancho
        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        width.setW(BigInteger.valueOf(5000));
        width.setType(STTblWidth.PCT);
        tblPr.setTblW(width);
        // Bordes
        if (manejaEstilos) {
            CTTblBorders tblBorders = tblPr.addNewTblBorders();
            tblBorders.addNewBottom().setVal(STBorder.SINGLE);
            tblBorders.addNewRight().setVal(STBorder.SINGLE);
            tblBorders.addNewLeft().setVal(STBorder.SINGLE);
            tblBorders.addNewTop().setVal(STBorder.SINGLE);
            tblBorders.addNewInsideH().setVal(STBorder.SINGLE);
            tblBorders.addNewInsideV().setVal(STBorder.SINGLE);
        }
        return table;
    }

    /**
     * Construcción de la consulta que define la estructutra de la tabla.
     * 
     * @param etiquetas
     * @param datos
     * @param variablesConsultaWord
     * @return Sentencia SQL que contiene los datos de la tabla.
     * @throws SystemException
     * en caso de que se presenten problemas al armar la consulta
     */
    private String armarConsulta(Map<String, Object> etiquetas,
        Map<String, Object> datos,
        Map<String, String> variablesConsultaWord) throws SystemException {
        String consulta = (String) etiquetas
                        .get(PlantillasEnum.CONSULTA.getValue());
        consulta = Reporteador.reemplazaSql(consulta);
        String[] enlacePrincipal = ((String) etiquetas
                        .get(PlantillasEnum.ENLACE_PRINCIPAL.getValue()))
                                        .split(",");
        String[] enlaceSecundario = ((String) etiquetas
                        .get(PlantillasEnum.ENLACE_SECUNDARIO.getValue()))
                                        .split(",");
        StringBuilder condicion = new StringBuilder();
        for (int i = 0; i < enlaceSecundario.length; i++) {

            String columnaSecundaria = enlaceSecundario[i];
            String columnaPrincipal = enlacePrincipal[i];
            Object valor = datos.get(columnaPrincipal);
            if (valor != null) {
                condicion.append((i > 0 ? "AND " : "") + columnaSecundaria
                    + "='" + valor + "' \n");
            }
            else {
                String msg = idioma.getString("TB_TB4114");
                msg = msg.replace("s$columnaPrincipal$s", columnaPrincipal);
                msg = msg.replace("s$variableTabla$s", SysmanFunciones
                                .toString(etiquetas.get("NOMBRE")));
                throw new SystemException(msg);
            }
        }
        consulta = consulta.replace("s$CONDICIONENLACE$s",
                        condicion.toString());
        String condicionUsuario = (String) etiquetas
                        .get(PlantillasEnum.CONDICION.getValue());
        if (condicionUsuario != null) {
            condicionUsuario = Reporteador.reemplazaSql(condicionUsuario);
            consulta = consulta.replace("s$CONDICIONUSUARIO$s",
                            condicionUsuario);
        }
        else {
            consulta = consulta.replace("s$CONDICIONUSUARIO$s", "");
        }
        consulta = Reporteador.reemplazarInicial(variablesConsultaWord,
                        consulta);
        return consulta;
    }

    /**
     * 
     * @param datos
     * @param tiposDatos
     * @return
     * @throws ParseException
     */
    public List<Registro> formatoColumnas(List<Registro> datos,
        List<Registro> tiposDatos) throws ParseException {
        for (int i = 0; i < datos.size(); i++) {
            Map<String, Object> camposDatos = datos.get(i).getCampos();
            for (int j = 0; j < tiposDatos.size(); j++) {
                Map<String, Object> camposTipos = tiposDatos.get(j).getCampos();
                String formato = SysmanFunciones.toString(
                                camposTipos.get(PlantillasEnum.FORMATO
                                                .getValue()));
                String etiqueta = SysmanFunciones.toString(
                                camposTipos.get(PlantillasEnum.ETIQUETA
                                                .getValue()));
                etiqueta = etiqueta.replace("<#", "");
                etiqueta = etiqueta.replace("#>", "");
                if ("M".equals(formato)) {
                    String tmp = SysmanFunciones
                                    .toString(camposDatos.get(etiqueta));
                    /*
                     * Formato para valores tipo Moneda. Se omite el símbolo de
                     * moneda.
                     */
                    NumberFormat formatoMoneda = NumberFormat
                                    .getCurrencyInstance(
                                                    new Locale("en", "US"));
                    DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatoMoneda)
                                    .getDecimalFormatSymbols();
                    decimalFormatSymbols.setCurrencySymbol("");
                    ((DecimalFormat) formatoMoneda).setDecimalFormatSymbols(
                                    decimalFormatSymbols);
                    String valor = formatoMoneda
                                    .format(Double.valueOf(tmp));
                    datos.get(i).getCampos().put(etiqueta, valor);
                }
                else if ("D".equals(formato)) {
                    Date tmp = (Date) camposDatos.get(etiqueta);
                    String valor = null;
                    valor = SysmanFunciones.convertirAFechaCadena(
                                    tmp, "DD/MM/YYYY");
                    datos.get(i).getCampos().put(etiqueta, valor);
                }
                else if ("L".equals(formato)) {
                    Date tmp = (Date) camposDatos.get(etiqueta);
                    String valor = null;
                    valor = SysmanFunciones.convertirAFechaCadena(
                                    tmp, "dd 'de' MMMM 'de' YYYY ");
                    datos.get(i).getCampos().put(etiqueta, valor);
                }
                else if ("P".equals(formato)) {
                    Integer valor = 100 * Integer.valueOf(SysmanFunciones
                                    .toString(camposDatos.get(etiqueta)));
                    datos.get(i).getCampos().put(etiqueta, valor + "%");
                }
            }
        }
        return datos;
    }

    /**
     * 
     * @param srcDoc
     * @param destDoc
     */
    private static void copyLayout(XWPFDocument srcDoc, XWPFDocument destDoc) {
        CTPageMar pgMar = srcDoc.getDocument().getBody().getSectPr().getPgMar();

        BigInteger bottom = pgMar.getBottom();
        BigInteger footer = pgMar.getFooter();
        BigInteger gutter = pgMar.getGutter();
        BigInteger header = pgMar.getHeader();
        BigInteger left = pgMar.getLeft();
        BigInteger right = pgMar.getRight();
        BigInteger top = pgMar.getTop();

        CTPageMar addNewPgMar = destDoc.getDocument().getBody().addNewSectPr()
                        .addNewPgMar();

        addNewPgMar.setBottom(bottom);
        addNewPgMar.setFooter(footer);
        addNewPgMar.setGutter(gutter);
        addNewPgMar.setHeader(header);
        addNewPgMar.setLeft(left);
        addNewPgMar.setRight(right);
        addNewPgMar.setTop(top);

        CTPageSz pgSzSrc = srcDoc.getDocument().getBody().getSectPr().getPgSz();

        BigInteger code = pgSzSrc.getCode();
        BigInteger h = pgSzSrc.getH();
        STPageOrientation.Enum orient = pgSzSrc.getOrient();
        BigInteger w = pgSzSrc.getW();

        CTPageSz addNewPgSz = destDoc.getDocument().getBody().addNewSectPr()
                        .addNewPgSz();

        addNewPgSz.setCode(code);
        addNewPgSz.setH(h);
        addNewPgSz.setOrient(orient);
        addNewPgSz.setW(w);
    }

    /**
     * Traslado del texto de los encabezados y pies de p&aacute;gina del archivo
     * origen al destino.
     * 
     * @param documentoOrigen
     * Documento origen.
     * @param documentoDestino
     * Documento destino.
     */
    private void exportarElementos(CustomXWPFDocument documentoOrigen,
        CustomXWPFDocument documentoDestino) {
        // Traslado de Encabezados
        List<XWPFHeader> headers = documentoDestino.getHeaderList();
        List<XWPFHeader> headersOrigen = documentoOrigen.getHeaderList();
        for (int i = 0; i < headers.size(); i++) {
            exportarElementos(headers.get(i).getBodyElements(),
                            headersOrigen.get(i).getBodyElements());
        }
        // Traslado de Pies de Página
        List<XWPFFooter> footers = documentoDestino.getFooterList();
        List<XWPFFooter> footersOrigen = documentoOrigen.getFooterList();
        for (int i = 0; i < footers.size(); i++) {
            exportarElementos(footers.get(i).getBodyElements(),
                            footersOrigen.get(i).getBodyElements());
        }
    }

    /**
     * Traslado de los elementos del documento origen al destino.
     * 
     * @param bodyElements
     * Elementos del documento destino.
     * @param bodyElementsOrigen
     * Elementos del documento origen.
     */
    private void exportarElementos(List<IBodyElement> bodyElements,
        List<IBodyElement> bodyElementsOrigen) {
        for (int i = 0; i < bodyElements.size(); i++) {
            IBodyElement bodyElement = bodyElements.get(i);
            IBodyElement bodyElementOrigen = bodyElementsOrigen.get(i);
            if (bodyElement.getElementType()
                            .compareTo(BodyElementType.PARAGRAPH) == 0) {
                exportarParrafos((XWPFParagraph) bodyElement,
                                (XWPFParagraph) bodyElementOrigen);
            }
            else
                if (bodyElement.getElementType()
                                .compareTo(BodyElementType.TABLE) == 0) {
                                    exportarTablas((XWPFTable) bodyElement,
                                                    (XWPFTable) bodyElementOrigen);
                                }
        }
    }

    /**
     * Traslado de los runs que contengan los parrafos del documento origen a
     * los parrafos del documento destino.
     * 
     * @param paragraph
     * Parrafo a afectar.
     * @param paragraphOrigen
     * Parrafo origen. Elemento en el que ya fueron reemplazadas las variables.
     */
    private void exportarParrafos(XWPFParagraph paragraph,
        XWPFParagraph paragraphOrigen) {
        List<XWPFRun> runs = paragraph.getRuns();
        List<XWPFRun> runsOrigen = paragraphOrigen.getRuns();
        for (int i = 0; i < runs.size(); i++) {
            XWPFRun run = runs.get(i);
            String text = runsOrigen.get(i).getText(0);
            run.setText(text, 0);
        }
    }

    /**
     * Recorrido para trasladar los parrafos que contengan las tablas del
     * documento origen a las tablas del documento destino.
     * 
     * @param table
     * Tabla que se va a afectar.
     * @param tableOrigen
     * Tabla origen. Elemento en el que ya fueron reemplazadas las variables.
     */
    private void exportarTablas(XWPFTable table, XWPFTable tableOrigen) {
        List<XWPFTableRow> rows = table.getRows();
        List<XWPFTableRow> rowsOrigen = tableOrigen.getRows();
        for (int i = 0; i < rows.size(); i++) {
            List<XWPFTableCell> cells = rows.get(i).getTableCells();
            List<XWPFTableCell> cellsOrigen = rowsOrigen.get(i).getTableCells();
            for (int j = 0; j < cells.size(); j++) {
                List<IBodyElement> bodyElements = cells.get(j)
                                .getBodyElements();
                List<IBodyElement> bodyElementsOrigen = cellsOrigen.get(j)
                                .getBodyElements();
                exportarElementos(bodyElements, bodyElementsOrigen);
            }
        }
    }

    /**
     * Copy Styles of Table and Paragraph.
     * 
     * @param srcDoc
     * @param destDoc
     * @param style
     */
    private static void copyStyle(XWPFDocument srcDoc, XWPFDocument destDoc,
        XWPFStyle style) {
        if ((destDoc == null) || (style == null)) {
            return;
        }
        if (destDoc.getStyles() == null) {
            destDoc.createStyles();
        }
        List<XWPFStyle> usedStyleList = srcDoc.getStyles()
                        .getUsedStyleList(style);
        for (XWPFStyle xwpfStyle : usedStyleList) {
            destDoc.getStyles().addStyle(xwpfStyle);
        }
    }

    /**
     * 
     * @param docPlant
     * @param destDoc
     */
    private void eliminarPlantilla(CustomXWPFDocument docPlant,
        CustomXWPFDocument destDoc) {
        List<IBodyElement> bodyPlant = docPlant.getBodyElements();
        for (int i = bodyPlant.size() - 1; i > -1; i--) {
            destDoc.removeBodyElement(i);
        }
    }

    /**
     * Exporta el comprimido con el(los) documento(s) generados a partir de la
     * plantilla.
     * 
     * @param nombreDocumento
     * nombre personalizado del documento. Opcional.
     * 
     * @return comprimido con los documentos de MS Word.
     * @throws SystemException
     * si ocurren problemas en la creación de documentos
     */
    public StreamedContent exportarPlantilla(String nombreDocumento)
                    throws SystemException {
        if (!datosValidos()) {
            throw new SystemException(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        }
        ByteArrayOutputStream[] arregloDocumentos = generarPlantilla();
        /*
         * Se crea el vector de nombres del documento,y el vector de inputstream
         * partiendo del vector de output stream escrito por la plantilla y los
         * registros
         */
        String[] nombresDocumentos = new String[arregloDocumentos.length];
        ByteArrayInputStream[] inputStreamDocumentos = new ByteArrayInputStream[arregloDocumentos.length];
        for (int i = 0; i < arregloDocumentos.length; i++) {
            inputStreamDocumentos[i] = new ByteArrayInputStream(
                            arregloDocumentos[i].toByteArray());
            String filename = SysmanFunciones.validarVariableVacio(
                            nombreDocumento)
                                ? UtilitarioPlantillas.NOMBRE_ARCHIVO_WORD
                                                .toString()
                                : nombreDocumento;
            nombresDocumentos[i] = filename + "-" + i + "."
                + UtilitarioPlantillas.EXTENSION_WORD;
        }
        try {
            return JsfUtil.exportarComprimidoGeneralStreamed(
                            inputStreamDocumentos, nombresDocumentos);
        }
        catch (JRException | IOException | SQLException | DRException e) {
            throw new SystemException(e);
        }
    }
    
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    public StreamedContent exportarPlantilla1(String nombreDocumento)
    		throws SystemException {
    	if (!datosValidos()) {
    		throw new SystemException(idioma.getString(
    				"TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
    	}
    	ByteArrayOutputStream[] arregloDocumentos = generarPlantilla1();
    	/*
    	 * Se crea el vector de nombres del documento,y el vector de inputstream
    	 * partiendo del vector de output stream escrito por la plantilla y los
    	 * registros
    	 */
    	String[] nombresDocumentos = new String[arregloDocumentos.length];
    	ByteArrayInputStream[] inputStreamDocumentos = new ByteArrayInputStream[arregloDocumentos.length];
    	for (int i = 0; i < arregloDocumentos.length; i++) {
    		inputStreamDocumentos[i] = new ByteArrayInputStream(
    				arregloDocumentos[i].toByteArray());
    		String filename = SysmanFunciones.validarVariableVacio(
    				nombreDocumento)
    				? UtilitarioPlantillas.NOMBRE_ARCHIVO_WORD
    						.toString()
    						: nombreDocumento;
    						nombresDocumentos[i] = filename + "-" + i + "."
    								+ UtilitarioPlantillas.EXTENSION_WORD;
    	}
    	try {
    		if(SessionUtil.getModulo().equals("9")) {
    			ByteArrayInputStream inputStreamDocumento = inputStreamDocumentos[0];
    			return JsfUtil.getArchivoDescargaStreamed(inputStreamDocumento, nombreDocumento + "."
						+ UtilitarioPlantillas.EXTENSION_WORD);
    		} else {
	    		return JsfUtil.exportarComprimidoGeneralStreamed(
	    				inputStreamDocumentos, nombresDocumentos);
    		}
    	}
    	catch (JRException | IOException | SQLException | DRException e) {
    		throw new SystemException(e);
    	}
    }
    
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    public StreamedContent exportarPlantillaPdf(String nombreDocumento, String rutaCertificado, String rutaPlantilla, boolean permiteEliminar)
    		throws SystemException {
    	if (!datosValidos()) {
    		throw new SystemException(idioma.getString(
    				"TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
    	}
    	ByteArrayOutputStream[] arregloDocumentos = generarPlantilla1();
		byte[] baseFinal = null;

    	/*
    	 * Se crea el vector de nombres del documento,y el vector de inputstream
    	 * partiendo del vector de output stream escrito por la plantilla y los
    	 * registros
    	 */
    	try {
           
    	String[] nombresDocumentos = new String[arregloDocumentos.length];
    	ByteArrayInputStream[] inputStreamDocumentos = new ByteArrayInputStream[arregloDocumentos.length];
    	ByteArrayInputStream pdfStream = null;
    	for (int i = 0; i < arregloDocumentos.length; i++) {
    		inputStreamDocumentos[i] = new ByteArrayInputStream(
    				arregloDocumentos[i].toByteArray());
    		 ByteArrayOutputStream wordOutputStream = arregloDocumentos[i];
    		 byte[] wordBytes = wordOutputStream.toByteArray();
    		 Path tempFile = Files.createTempFile("plantilla_", ".docx");
    		 Files.write(tempFile, wordBytes);
    		 String rutaRelativa = tempFile.toString();

    		 try {
    		     // Siempre convierte sin eliminar (los temporales se eliminan en finally)
    		     baseFinal = convertir.convertirAPdfDocumento(rutaRelativa, permiteEliminar, rutaCertificado);
    		     pdfStream = new ByteArrayInputStream(baseFinal);
    		 } finally {
    		     try {
    		         Files.deleteIfExists(tempFile);
    		     } catch (IOException ex) {
    		         logger.warn("No se pudo eliminar temporal: " + tempFile, ex);
    		     }
    		 }

                    inputStreamDocumentos[i] = pdfStream;
                    
    		String filename = SysmanFunciones.validarVariableVacio(
    				nombreDocumento)
    				? UtilitarioPlantillas.NOMBRE_ARCHIVO_WORD
    						.toString()
    						: nombreDocumento;
    						nombresDocumentos[i] = filename + "-" + i + "."
    								+ UtilitarioPlantillas.EXTENSION_PDF;
    	}
    	
    	if (SessionUtil.getModulo().equals("1") || SessionUtil.getModulo().equals("3")
    			|| SessionUtil.getModulo().equals("9") || SessionUtil.getModulo().equals("10")){
    		    return JsfUtil.getArchivoDescargaStreamed(pdfStream, nombreDocumento+ "."
						+ UtilitarioPlantillas.EXTENSION_PDF);
    	}else {
         		return JsfUtil.exportarComprimidoGeneralStreamed(
         				inputStreamDocumentos, nombresDocumentos);
    	}
         	}
    	catch (JRException | IOException | SQLException | DRException  | SysmanException  e) {
    		throw new SystemException(e);
    	}
    }


    private String crearArchivoTemporal(String rutaPlantilla, byte[] arregloBytes) {

		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(rutaPlantilla);
		String pathname = stringBuilder.toString();

		File file = new File(pathname);
		file.getParentFile().mkdirs();

			try {
				FileUtils.writeByteArrayToFile(file, arregloBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return pathname;
 

	}
    
    /**
     * Exporta el arreglo de bytes con el documento generado a partir de la
     * plantilla.
     * 
     * @return arreglo de bytes del documento
     * @throws SystemException
     * en caso de que se presente alg&uacute;n problema al exportar el documento
     * en MS Word.
     */
    public byte[] serializarPlantilla()
                    throws SystemException {
        if (!datosValidos()) {
            throw new SystemException(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        }
        ByteArrayOutputStream[] arregloDocumentos = generarPlantilla();
        ByteArrayOutputStream byteArrayOutputStream = arregloDocumentos[0];
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Adiciona los saltos de linea al enviar el texto a una celda
     * 
     * @param celda
     * La celda en la que se insertara el texto
     * @param texto
     * Texto a almacenar en la celda que ingresa por parametros
     */
    private void guardarTextoCelda(XWPFTableCell celda, String texto) {
        celda.removeParagraph(0);
        XWPFParagraph parrafo = celda.addParagraph();
        XWPFRun run = parrafo.createRun();
        run.setFontFamily(tablesFontFamily);
        guardarTextoRun(texto, run);
    }

    /**
     * Procesa los saltos de l&iacute;nea y los materializa en el documento.
     * 
     * @param texto
     * texto que se va amostrar en el documento
     * @param run
     * regi&oacute;n de texto que se est&aacute; procesando
     */
    private void guardarTextoRun(String texto, XWPFRun run) {
        if (texto.contains("\r\n")) {
            String[] lineas = texto.split("\r\n");
            run.setText(lineas[0], 0);
            for (int i = 1; i < lineas.length; i++) {
                run.addBreak();
                run.setText(lineas[i]);
            }
        }
        else {
            run.setText(texto, 0);
        }
    }

    /**
     * Exporta el serializado de cada plantilla para ser comprimida en la
     * descarga final
     *
     * 
     * @param nombreDocumento
     * nombre personalizado del documento. Opcional.
     * 
     * @return comprimido con los documentos de MS Word.
     * @throws SystemException
     * si ocurren problemas en la creación de documentos
     */
    public StreamedContent exportarPlantillaMasiva(String nombreDocumento)
                    throws SystemException {
        if (!datosValidos()) {
            throw new SystemException(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        }
        ByteArrayOutputStream[] arregloDocumentos = generarPlantilla();
        ByteArrayOutputStream byteArrayOutputStream = arregloDocumentos[0];
        StreamedContent plantilla;
        InputStream is = new ByteArrayInputStream(
                        byteArrayOutputStream.toByteArray());

        plantilla = new DefaultStreamedContent(is);
        return plantilla;
    }
    
    /**
     * Reemplazo de variables que representan tablas.
     * 
     * @param doc
     * @param etiquetasTabla
     * @param i
     * @param datos
     * @param variablesConsultaWord
     * @throws SystemException
     */
    public void reemplazarVariablesDeTabla3(CustomXWPFDocument doc,
    		List<Registro> etiquetasTabla, int i,
    		List<Registro> datos, Map<String, String> variablesConsultaWord)
    				throws SystemException {
    	if (doc != null) {
    		List<IBodyElement> elements = doc.getBodyElements();
    		if (elements != null) {
    			for (int j = 0; j < elements.size(); j++) {
    				IBodyElement bodyElement = elements.get(j);
    				if (bodyElement.getElementType()
    						.compareTo(BodyElementType.TABLE) == 0) {
    					reemplazarTablasInternas3(
    							(XWPFTable) bodyElement,
    							datos.get(i).getCampos(),
    							etiquetasTabla,
    							variablesConsultaWord);
    				}
    			}
    		}
    	}
    }
    
    /**
     * Busqueda de variables en celdas de tablas internas, que deban ser
     * reemplazadas por tablas.
     * 
     * @param table
     * @param campos
     * @param etiquetasTabla
     * @param variablesConsultaWord
     * @param doc
     * @throws SystemException
     */
    private void reemplazarTablasInternas3(XWPFTable table,
    		Map<String, Object> campos, List<Registro> etiquetasTabla,
    		Map<String, String> variablesConsultaWord) throws SystemException {

    	// IMPORTANTE: snapshot de filas para no modificar la lista mientras iteramos
    	List<XWPFTableRow> rows = new ArrayList<>(table.getRows());

    	for (XWPFTableRow row : rows) {
    		for (XWPFTableCell cell : row.getTableCells()) {

    			// También es más seguro hacer snapshot de los elementos del cuerpo
    			List<IBodyElement> elements = new ArrayList<>(cell.getBodyElements());
    			for (IBodyElement bodyElement : elements) {

    				if (bodyElement.getElementType() == BodyElementType.PARAGRAPH) {

    					reemplazarTablasEnParrafos3(
    							(XWPFParagraph) bodyElement,
    							campos,
    							etiquetasTabla,
    							variablesConsultaWord,
    							cell);

    				}
    			}
    		}
    	}
    }
    
    /**
     * Busqueda de variables que deban ser reemplazadas por tablas.
     * 
     * @param paragraph
     * @param camposDatos
     * @param etiquetasTabla
     * @param variablesConsultaWord
     * @param contenedor
     * Elemento en el que está posicionado el parrafo.
     * @throws SystemException
     */
    private void reemplazarTablasEnParrafos3(XWPFParagraph paragraph,
            Map<String, Object> camposDatos, List<Registro> etiquetasTabla,
            Map<String, String> variablesConsultaWord, IBody contenedor)
                    throws SystemException {

        String text = paragraph.getText();

        if (paragraph.getRuns() != null && !paragraph.getRuns().isEmpty()) {
            tablesFontFamily = paragraph.getRuns().get(0).getFontFamily();
        } else {
            tablesFontFamily = "Arial";
        }

        // Solo entramos si hay algo con delimitadores <# #>
        if (text.contains("<#") || text.contains("#>")) {

            for (Registro registro : etiquetasTabla) {

                Map<String, Object> camposEtiquetas = registro.getCampos();
                // Aquí viene el nombre COMPLETO, por ejemplo "<#TABLA#>" o "<#RESUMEN_TABLA#>"
                String nombre = SysmanFunciones.toString(
                        camposEtiquetas.get(GeneralParameterEnum.NOMBRE.getName()));

                if (nombre == null || nombre.isEmpty()) {
                    continue;
                }

                // === CAMBIO IMPORTANTE ===
                // En vez de buscar "TABLA#>" a lo bruto, buscamos el nombre exacto de la variable
                if (text.contains(nombre)) {

                    String consulta = armarConsulta(camposEtiquetas,
                            camposDatos, variablesConsultaWord);

                    List<String> columnas = service.getCamposListado(
                            ConectorPool.ESQUEMA_SYSMAN, consulta);
                    List<Registro> datos = service.getListado(
                            ConectorPool.ESQUEMA_SYSMAN, consulta);

                    String sql = "SELECT ETIQUETA, FORMATO \n"
                            + "FROM MODELO_VARIABLES \n"
                            + "WHERE PLANTILLA = '" + codigoPlantilla + "' \n"
                            + "AND FECHA = " + fechaPlantilla + " \n"
                            + "AND TIPO = 'T' AND DESCRIPCION = '"
                            + camposEtiquetas.get(
                                    GeneralParameterEnum.NOMBRE.getName())
                            + "'";

                    List<Registro> tiposDatos = service.getListado(
                            ConectorPool.ESQUEMA_SYSMANK, sql);

                    try {
                        datos = formatoColumnas(datos, tiposDatos);
                    } catch (ParseException e) {
                        logger.error(e.getMessage(), e);
                    }

                    if (!datos.isEmpty()) {

                        String modoCreacion = SysmanFunciones.toString(
                                camposEtiquetas.get("MODO_CREACION"));

                        // Usar FILA de la propia plantilla
                        if ("P".equalsIgnoreCase(modoCreacion)
                                && paragraph.getBody() instanceof XWPFTableCell) {

                        	XWPFTableCell cell = (XWPFTableCell) paragraph.getBody();
                            XWPFTableRow filaTemplate = cell.getTableRow();
                            XWPFTable tabla = filaTemplate.getTable();
                            
                            // 2) Ahora sí, limpiar el marcador del párrafo original si quieres
                            eliminarPrimeraVariableParrafo(paragraph);

                            // 1) Usar la fila como plantilla con los placeholders aún intactos
                            llenarTablaDesdeFilaTemplate(tabla, filaTemplate, columnas, datos);

                        } else {
                            // Crear tabla desde cero (comportamiento anterior)
                            if (contenedor instanceof CustomXWPFDocument) {
                                int pos = contenedor.getXWPFDocument()
                                        .getPosOfParagraph(paragraph);
                                contenedor.getXWPFDocument().removeBodyElement(pos);
                                if (pos != -1) {
                                    XWPFTable tabla = crearTabla(columnas, datos,
                                            contenedor, camposEtiquetas);
                                    contenedor.getXWPFDocument().insertTable(pos,
                                            tabla);
                                }
                            } else {
                                cambiarTexto(paragraph, "");
                                XmlCursor cursor = paragraph.getCTP().newCursor();
                                XWPFTable tabla = contenedor.insertNewTbl(cursor);
                                crearTabla(columnas, datos, tabla, camposEtiquetas);
                            }
                        }

                    } else {
                        // Sin datos: simplemente quitamos el marcador
                        cambiarTexto(paragraph, "");
                    }

                    // MUY IMPORTANTE:
                    // Ya procesamos la variable de esta tabla, no queremos
                    // que otro "registro" vuelva a intentar usar el mismo párrafo
                    return;
                }
            }
        }
    }
    
    /**
     * Elimina SOLO la primera variable con formato <#...#> que encuentre
     * en el párrafo. No toca las demás variables ni el resto del texto.
     */
    private void eliminarPrimeraVariableParrafo(XWPFParagraph paragraph) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) {
            return;
        }

        final String INICIO = "<#";
        final String FIN = "#>";
        boolean eliminado = false;

        for (int i = 0; i < runs.size() && !eliminado; i++) {
            XWPFRun run = runs.get(i);
            String text = run.getText(0);
            if (text == null) {
                continue;
            }

            int idxIni = text.indexOf(INICIO);
            if (idxIni == -1) {
                continue;
            }
            int idxFin = text.indexOf(FIN, idxIni + INICIO.length());
            if (idxFin == -1) {
                continue;
            }

            // Armamos el texto SIN la variable
            String before = text.substring(0, idxIni);
            String after  = text.substring(idxFin + FIN.length());

            if (before.isEmpty() && after.isEmpty()) {
                // El run SOLO tenía la variable -> borramos el run completo
                paragraph.removeRun(i);
            } else {
                // El run tenía más texto, reescribimos sin la variable
                run.setText(before + after, 0);
            }

            eliminado = true;
        }
    }
    
    /**
     * Inserta nuevas filas a partir de una fila plantilla ya diseñada en Word.
     * columns: nombres de columnas devueltos por la consulta (alias).
     * datos: registros devueltos por la consulta.
     */
    private void llenarTablaDesdeFilaTemplate(XWPFTable tabla,
            XWPFTableRow filaTemplate,
            List<String> columnas,
            List<Registro> datos) {

        if (tabla == null || filaTemplate == null || datos == null || datos.isEmpty()) {
            return;
        }

        // Índice de la fila de la plantilla dentro de la tabla
        int idxTemplate = tabla.getRows().indexOf(filaTemplate);

        // Guardamos un MOLDE del CTRow ANTES de modificar la fila de la plantilla
        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow ctMolde =
                (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow)
                filaTemplate.getCtRow().copy();

        // 1) Crear filas nuevas (para registros 2..N) copiando el MOLDE
        for (int i = 1; i < datos.size(); i++) {
            int insertIndex = idxTemplate + i;

            // Insertar un CTRow nuevo en la tabla
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow ctNew =
                    tabla.getCTTbl().insertNewTr(insertIndex);

            // Copiar el molde
            ctNew.set(ctMolde.copy());

            // Crear la XWPFTableRow a partir del CTRow recién insertado
            XWPFTableRow nuevaFila = new XWPFTableRow(ctNew, tabla);

            // Mantener sincronizada la lista de filas de la tabla
            tabla.getRows().add(insertIndex, nuevaFila);
        }

        // 2) Rellenar TODAS las filas (incluida la original) con sus datos
        for (int i = 0; i < datos.size(); i++) {
            XWPFTableRow row = tabla.getRow(idxTemplate + i);
            Registro reg = datos.get(i);
            // Aquí se llamará a reemplazarMarcadoresEnFila, 
            // y ahora row.getTableCells() contendrá las celdas de las filas copiadas.
            reemplazarMarcadoresEnFila(row, columnas, reg.getCampos());
        }

        // Nota: NO borramos la fila plantilla, solo la transformamos en la fila del primer registro.
    }
    
    /**
     * Copia una fila manteniendo formato de celdas, párrafos y runs.
     */
    private XWPFTableRow copiarFila(XWPFTable tabla,
    		XWPFTableRow templateRow,
    		int index) {

    	XWPFTableRow nueva = tabla.insertNewTableRow(index);
    	nueva.getCtRow().setTrPr(templateRow.getCtRow().getTrPr());

    	// eliminar celdas por defecto
    	for (int i = nueva.getTableCells().size() - 1; i >= 0; i--) {
    		nueva.removeCell(i);
    	}

    	for (XWPFTableCell cellTemplate : templateRow.getTableCells()) {
    		XWPFTableCell cellNueva = nueva.addNewTableCell();
    		cellNueva.getCTTc().setTcPr(cellTemplate.getCTTc().getTcPr());

    		// quitar párrafo default
    		if (!cellNueva.getParagraphs().isEmpty()) {
    			cellNueva.removeParagraph(0);
    		}

    		for (XWPFParagraph pTemplate : cellTemplate.getParagraphs()) {
    			XWPFParagraph pNueva = cellNueva.addParagraph();
    			pNueva.getCTP().setPPr(pTemplate.getCTP().getPPr());

    			for (XWPFRun rTemplate : pTemplate.getRuns()) {
    				XWPFRun rNueva = pNueva.createRun();
    				rNueva.getCTR().setRPr(rTemplate.getCTR().getRPr());
    				rNueva.setText(rTemplate.toString(), 0);
    			}
    		}
    	}

    	return nueva;
    }
    
    /**
     * Reemplaza en una fila los placeholders <#COLUMNA#> por el valor del registro.
     */
    private void reemplazarMarcadoresEnFila(XWPFTableRow row,
    		List<String> columnas, // ya no la usamos, pero la dejo por firma
    		Map<String, Object> valores) {

    	for (XWPFTableCell cell : row.getTableCells()) {
    		for (XWPFParagraph p : cell.getParagraphs()) {
    			String text = p.getText();
    			if (text == null) {
    				continue;
    			}

    			// Recorremos TODAS las llaves del registro
    			for (Map.Entry<String, Object> entry : valores.entrySet()) {
    				String key = entry.getKey();
    				Object val = entry.getValue();

    				// Ajuste por mayúsculas/minúsculas si hace falta
    				String marcador = "<#" + key + "#>";
    				String marcadorUpper = "<#" + key.toUpperCase() + "#>";

    				if (text.contains(marcador) || text.contains(marcadorUpper)) {
    					String valorTexto = (val == null) ? "" : val.toString();
    					text = text.replace(marcador, valorTexto);
    					text = text.replace(marcadorUpper, valorTexto);
    				}
    			}

    			cambiarTexto(p, text);
    		}
    	}
    }

}
