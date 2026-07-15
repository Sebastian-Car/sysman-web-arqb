/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.jsfutil;

import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plantillas.enums.PlantillasEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * @author jacelas
 * 
 * @deprecated use
 * {@link #com.sysman.plantillas.poixml.UtilitarioXwpf} para la
 * generación de plantillas de MS Word.
 */
@Deprecated
public class UtilitarioXwpfDocument {

    private static FormContinuoService service;
    private static int NUMERO_PAGINAS = 100;
    /**
     * Fuente de texto identificada en las variables de tabla.
     */
    private String tablesFontFamily;
    /**
     * Recurso para acceder al properties de idiomas.
     */
    private ResourceBundle idioma;

    public UtilitarioXwpfDocument(FormContinuoService service) {
        UtilitarioXwpfDocument.service = service;
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    public UtilitarioXwpfDocument() {
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
    }

    public FormContinuoService getService() {
        return service;
    }

    public static void setService(FormContinuoService service) {
        UtilitarioXwpfDocument.service = service;
    }

    /**
     * Reemplazo de variables que representan tablas.
     * 
     * @param doc
     * @param etiquetasTabla
     * @param i
     * @param datos
     * @param variablesConsultaWord
     * @throws Exception
     */
    public void reemplazarVariablesDeTabla(CustomXWPFDocument doc,
        List<Registro> etiquetasTabla, int i,
        List<Registro> datos, HashMap<String, String> variablesConsultaWord)
                    throws Exception {
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
                    else if (bodyElement.getElementType()
                                    .compareTo(BodyElementType.TABLE) == 0) {
                        reemplazarTablasInternas((XWPFTable) bodyElement,
                                        datos.get(i).getCampos(),
                                        etiquetasTabla, variablesConsultaWord);
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
        HashMap<String, String> variablesConsultaWord, IBody contenedor)
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
                String nombre = (String) camposEtiquetas.get("NOMBRE");
                if (text.contains(nombre)) {
                    String consulta = armarConsulta(camposEtiquetas,
                                    camposDatos, variablesConsultaWord);
                    List<String> columnas = service.getCamposListado(
                                    ConectorPool.ESQUEMA_SYSMAN, consulta);
                    List<Registro> datos = service.getListado(
                                    ConectorPool.ESQUEMA_SYSMAN, consulta);

                    String sql = "SELECT ETIQUETA, FORMATO \n"
                        + "FROM MODELO_VARIABLES \n"
                        + "WHERE PLANTILLA = '"
                        + camposEtiquetas.get("PLANTILLA") + "' \n"
                        + "AND FECHA = "
                        + SysmanFunciones.formatearFecha(
                                        (Date) camposEtiquetas.get("FECHA"))
                        + " \n"
                        + "AND TIPO = 'T' AND DESCRIPCION = '"
                        + camposEtiquetas.get("NOMBRE") + "'";
                    List<Registro> tiposDatos = service.getListado(
                                    ConectorPool.ESQUEMA_SYSMANK, sql);
                    datos = formatoColumnas(datos, tiposDatos);
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
     * Reemplaza el texto que tenga el parrafo.
     * 
     * @param paragraph
     * Parrafo a afectar.
     * @param texto
     * Texto que va a insertar en el parrafo.
     */
    private void cambiarTexto(XWPFParagraph paragraph, String texto) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int i = runs.size() - 1; i > 0; i--) {
            paragraph.removeRun(i);
        }
        XWPFRun run = runs.get(0);
        run.setText(texto, 0);
    }

    /**
     * Busqueda de variables en celdas de tablas internas, que deban
     * ser reemplazadas por tablas.
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
        HashMap<String, String> variablesConsultaWord) throws SystemException {
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
                        else if (bodyElement.getElementType().compareTo(
                                        BodyElementType.TABLE) == 0) {
                            reemplazarTablasInternas((XWPFTable) bodyElement,
                                            campos, etiquetasTabla,
                                            variablesConsultaWord);
                        }
                    }
                }
            }
        }
    }

    /**
     * Permite crear una tabla interna. Para tablas que están dentro
     * de una celda de otra tabla.
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
        XWPFTableCell cell = null;
        String modoCreacion = camposEtiquetas.get("MODO_CREACION").toString();
        boolean manejaEstilos = (boolean) camposEtiquetas.get("MANEJA_ESTILOS");

        for (int i = 0; i < numeroColumnas; i++) {
            cell = row.createCell();
            if (Arrays.asList("D", "E").contains(modoCreacion)) {
                XWPFParagraph cellParagraph = cell.getParagraphs().get(0);
                XWPFRun cellRun = cellParagraph.createRun();
                cellRun.setFontFamily(tablesFontFamily);
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
        CTJc jc = (tblPr.isSetJc() ? tblPr.getJc() : tblPr.addNewJc());
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
     * Crea una tabla en base al documento, columnas y filas
     * ingresadas por parámetro.
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
        String modoCreacion = camposEtiquetas.get("MODO_CREACION").toString();
        boolean manejaEstilos = (boolean) camposEtiquetas.get("MANEJA_ESTILOS");
        boolean conEncabezado = Arrays.asList("D", "E").contains(modoCreacion);

        XWPFTable table = new XWPFTable(ctTbl, contenedor,
                        conEncabezado ? numeroDatos + 1 : numeroDatos,
                        numeroColumnas);
        row = table.getRow(fila);
        row.setHeight(100);
        for (int i = 0; i < numeroColumnas; i++) {
            if (conEncabezado) {
                cell = row.getCell(i);
                cell.setText(columnas.get(i));
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

    /**
     * Construcción de la consulta que define la estructutra de la
     * tabla.
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
        HashMap<String, String> variablesConsultaWord) throws SystemException {
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
        consulta = reemplazaVariables(consulta, variablesConsultaWord);
        return consulta;
    }

    /**
     * Reemplazo de variables en parrafos y tablas existentes en el
     * documento.
     * 
     * @param doc
     * @param docPlant
     * @param datos
     * @param i
     * @param columnas
     * @param variables
     */
    private void reemplazarInformacionPlantilla(CustomXWPFDocument doc,
        CustomXWPFDocument docPlant,
        List<Registro> datos, int i, List<String> columnas,
        HashMap<String, String> variables) {
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
     * Metodo que recibe string texto y busca etiquetas para remplazar
     * en base a las columnas de la consulta de las plantillas de
     * word. Se agrego el reemplazar etiquedas de un vector de
     * etiquetas tambien
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
        HashMap<String, String> variables) {
        if (text != null) {
            // Busca que contenga alguna etiqueta
            if (text.contains("<#") && text.contains("#>")) {
                // Recorre el vector de columnas y si encuentra una
                // etiqueta con el
                // mismo nombre la reemplaza
                for (int j = 0; j < columnas.size(); j++) {
                    if (text.contains("<#" + columnas.get(j) + "#>")) {
                        String val = (String) datos.get(i).getCampos()
                                        .get(columnas.get(j));
                        text = text.replace("<#" + columnas.get(j) + "#>",
                                        val != null ? val : " ");
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
                        text = text.replace(e.getKey().toString(),
                                        e.getValue().toString());
                    }
                    posVar++;
                }
            }
        }
        return text;
    }

    public String reemplazaVariables(String sqlTraerCampos,
        HashMap<String, String> variablesConsultaWord) {
        for (Map.Entry<String, String> entrySet : variablesConsultaWord
                        .entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            sqlTraerCampos = sqlTraerCampos.toUpperCase()
                            .replace(key.toUpperCase(), value);

        }
        return sqlTraerCampos;
    }

    private List<Registro> formatoColumnas(List<Registro> datos,
        List<Registro> tiposDatos) {

        for (int i = 0; i < datos.size(); i++) {

            for (int j = 0; j < tiposDatos.size(); j++) {
                if (tiposDatos.get(j).getCampos()
                                .get(GeneralParameterEnum.FORMATO.getName())
                                .toString()
                                .equals("M")) {
                    try {

                        String tmp = datos.get(i).getCampos().get(tiposDatos
                                        .get(j).getCampos()
                                        .get(PlantillasEnum.ETIQUETA.getValue())
                                        .toString().replace("<#", "")
                                        .replace("#>", "")).toString();
                        /*
                         * Formato para valores tipo Moneda. Se omite
                         * el símbolo de moneda.
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

                        datos.get(i).getCampos().put(tiposDatos.get(j)
                                        .getCampos()
                                        .get(PlantillasEnum.ETIQUETA.getValue())
                                        .toString()
                                        .replace("<#", "").replace("#>", ""),
                                        valor);

                    }
                    catch (Exception e) {
                        Logger.getLogger(UtilitarioXwpfDocument.class.getName())
                                        .log(Level.SEVERE, null, e);
                    }

                }
                else if (tiposDatos.get(j).getCampos()
                                .get(GeneralParameterEnum.FORMATO.getName())
                                .toString()
                                .equals("D")) {

                    try {

                        Date tmp = (Date) datos.get(i).getCampos()
                                        .get(tiposDatos.get(j).getCampos()
                                                        .get(PlantillasEnum.ETIQUETA
                                                                        .getValue())
                                                        .toString()
                                                        .replace("<#", "")
                                                        .replace("#>", ""));

                        String valor = SysmanFunciones.convertirAFechaCadena(
                                        tmp, "DD/MM/YYYY");

                        datos.get(i).getCampos().put(tiposDatos.get(j)
                                        .getCampos()
                                        .get(PlantillasEnum.ETIQUETA.getValue())
                                        .toString()
                                        .replace("<#", "").replace("#>", ""),
                                        valor);

                    }
                    catch (Exception e) {
                        Logger.getLogger(UtilitarioXwpfDocument.class.getName())
                                        .log(Level.SEVERE, null, e);
                    }

                }
                else if (tiposDatos.get(j).getCampos()
                                .get(GeneralParameterEnum.FORMATO.getName())
                                .toString()
                                .equals("L")) {

                    try {

                        Date tmp = (Date) datos.get(i).getCampos()
                                        .get(tiposDatos.get(j).getCampos()
                                                        .get(PlantillasEnum.ETIQUETA
                                                                        .getValue())
                                                        .toString()
                                                        .replace("<#", "")
                                                        .replace("#>", ""));

                        String valor = SysmanFunciones.convertirAFechaCadena(
                                        tmp, "dd 'de' MMMM 'de' YYYY ");

                        datos.get(i).getCampos().put(tiposDatos.get(j)
                                        .getCampos()
                                        .get(PlantillasEnum.ETIQUETA.getValue())
                                        .toString()
                                        .replace("<#", "").replace("#>", ""),
                                        valor);

                    }
                    catch (Exception e) {
                        Logger.getLogger(UtilitarioXwpfDocument.class.getName())
                                        .log(Level.SEVERE, null, e);
                    }

                }
                else if (tiposDatos.get(j).getCampos()
                                .get(GeneralParameterEnum.FORMATO.getName())
                                .toString()
                                .equals("P")) {

                    Integer valor = 100
                        * Integer
                                        .valueOf(
                                                        datos.get(i)
                                                                        .getCampos()
                                                                        .get(tiposDatos.get(
                                                                                        j)
                                                                                        .getCampos()
                                                                                        .get(PlantillasEnum.ETIQUETA
                                                                                                        .getValue())
                                                                                        .toString()
                                                                                        .replace("<#", "")
                                                                                        .replace("#>", ""))
                                                                        .toString());

                    datos.get(i).getCampos().put(tiposDatos.get(j).getCampos()
                                    .get(PlantillasEnum.ETIQUETA.getValue())
                                    .toString()
                                    .replace("<#", "").replace("#>", ""),
                                    valor + "%");

                }
            }
        }
        return datos;
    }

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

    public StreamedContent exportarPlantillaWord(String codigoPlantilla,
        HashMap<String, String> variables,
        String condicion, HashMap<String, String> variablesConsultaWord,
        String fechaPlantilla,
        String nombreDocumento) throws Exception {
        /*-
         * ********************************************
         * cargarDatosPlantillas => getModeloPlantilla
         * ********************************************
         */
        // ARCHIVOS PLANTILLA Buscar plantilla en la tabla WORD TIPO
        List<Registro> registro = service.getListado(
                        ConectorPool.ESQUEMA_SYSMANK,
                        "SELECT   \n" + "     MODELO_PLANTILLA.ROWID RID,   \n"
                            + "     MODELO_PLANTILLA.CODIGO,   \n"
                            + "     MODELO_PLANTILLA.NOMBRE,   \n"
                            + "     MODELO_PLANTILLA.PLANTILLA,   \n"
                            + "     MODELO_PLANTILLA.TIPO, "
                            + "    MODELO_TIPO.NOMBRE as  NOMBRETIPO,  \n"
                            + "     MODELO_PLANTILLA.VERSION,   \n"
                            + "     MODELO_PLANTILLA.FECHA,   \n"
                            + "     MODELO_PLANTILLA.CONSULTA,   \n"
                            + "     MODELO_PLANTILLA.CREATED_BY,   \n"
                            + "     MODELO_PLANTILLA.DATE_CREATED,   \n"
                            + "     MODELO_PLANTILLA.MODIFIED_BY,   \n"
                            + "     MODELO_PLANTILLA.DATE_MODIFIED   , "
                            + "TIPO_VARIABLES_CONSULTA  \n" + "  FROM   \n"
                            + "     MODELO_PLANTILLA INNER JOIN MODELO_TIPO "
                            + "      ON MODELO_PLANTILLA.TIPO = MODELO_TIPO.CODIGO  "
                            + "  WHERE MODELO_PLANTILLA.CODIGO ='"
                            + codigoPlantilla
                            + "' AND MODELO_PLANTILLA.FECHA =  "
                            + fechaPlantilla);
        if (registro.isEmpty()) {
            return null;
        }

        // -------------------------------------------------------------------------------------------------------
        // Traer columnas de la consulta
        // -------------------------------------------------------------------------------------------------------

        /*-
         * ***************************************
         * cargarDatosPlantillas
         * ***************************************
         */
        String sqlTraerCampos = (String) registro.get(0).getCampos()
                        .get(PlantillasEnum.CONSULTA.getValue());

        sqlTraerCampos = Reporteador.reemplazaSql(sqlTraerCampos);

        sqlTraerCampos = reemplazaVariables(sqlTraerCampos,
                        variablesConsultaWord);

        List<String> columnas = service.getCamposListado(
                        ConectorPool.ESQUEMA_SYSMAN,
                        sqlTraerCampos);

        // -------------------------------------------------------------------------------------------------------
        // Traer campos de la consulta
        // -------------------------------------------------------------------------------------------------------
        List<Registro> datos = service.getListadoCadena(
                        ConectorPool.ESQUEMA_SYSMAN,
                        sqlTraerCampos);

        if (datos.isEmpty()) {
            throw new SystemException(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        }

        // String tiposDatos[] =
        String sqlVariables = "SELECT ETIQUETA,FORMATO  FROM MODELO_VARIABLES  WHERE PLANTILLA ='"
            + codigoPlantilla
            + "'  AND  FECHA  = " + fechaPlantilla + " AND TIPO = 'C'";
        List<Registro> tiposEtiquetas = service
                        .getListado(ConectorPool.ESQUEMA_SYSMANK, sqlVariables);

        datos = formatoColumnas(datos, tiposEtiquetas);
        // -------------------------------------------------------------------------------------------------------
        // Traer campos de la ETIQUETA TABLA
        // -------------------------------------------------------------------------------------------------------
        List<Registro> etiquetasTmp = null;
        /*-
         * ***************************************
         * cargarDatosPlantillas
         * ***************************************
         */
        String sql = "SELECT T.NOMBRE, T.CONSULTA, T.ENLACE_PRINCIPAL, " +
            " T.ENLACE_SECUNDARIO, T.CONDICION, T.TIPO_VARIABLES_CONSULTA, " +
            " T.MODO_CREACION, M.MANEJA_ESTILOS FROM MODELO_TABLA T " +
            " INNER JOIN MODO_CREACION_TABLA M ON T.MODO_CREACION = M.CODIGO " +
            " WHERE T.PLANTILLA = '" + registro.get(0).getCampos().get("CODIGO")
                            .toString()
            + "' AND  T.FECHA  = " + fechaPlantilla;

        etiquetasTmp = service.getListado(ConectorPool.ESQUEMA_SYSMANK, sql);

        // -------------------------------------------------------------------------------------------------------
        // ABRIR PLANTILLA
        // -------------------------------------------------------------------------------------------------------
        String ruta = SessionUtil.getRuta(-1) + "plantillasword/"
            + (String) registro.get(0).getCampos().get("NOMBRETIPO") + "/"
            + (String) registro.get(0).getCampos().get("PLANTILLA");
        /*-
         * ***************************************
         * UtilitarioXwpf.exportarPlantilla
         * ***************************************
         */
        File plantilla = new File(ruta);

        if (!plantilla.isFile()) {
            String msg = idioma.getString("TB_TB4113");
            msg = msg.replace("s$ruta$s", ruta);
            throw new FileNotFoundException(msg);
        }

        try {
            // documento para acumular el archivo que se imprime al
            // final
            FileInputStream archivoDestino = new FileInputStream(ruta);
            CustomXWPFDocument documentoDestino = new CustomXWPFDocument(
                            archivoDestino);
            // Documento copia de la plantilla el cual se reemplazan
            // los valores
            FileInputStream fl = new FileInputStream(ruta);
            CustomXWPFDocument doc = new CustomXWPFDocument(fl);
            // Documento copia de la plantilla el cual se reemplazan
            // los valores
            FileInputStream fl3 = new FileInputStream(ruta);
            CustomXWPFDocument docPlant = new CustomXWPFDocument(fl3);
            // Documento temporal de la plantilla para remplazar
            // cuando se
            // agrega el doc al descDoc que acumula
            FileInputStream fl2 = new FileInputStream(ruta);
            CustomXWPFDocument doctmp = new CustomXWPFDocument(fl2);

            unirRuns(docPlant);
            unirRuns(doctmp);

            // -----------------------------------------------------------------------------------------------------------------------------------------------
            // Agregar salto de pagina al ultimo parrafo del documento
            // -----------------------------------------------------------------------------------------------------------------------------------------------
            XWPFParagraph pr1 = (XWPFParagraph) doc.getBodyElements()
                            .get(doc.getBodyElements().size() - 1);
            XWPFRun run = pr1.createRun();
            run.addBreak(BreakType.PAGE);
            // -----------------------------------------------------------------------------------------------------------------------------------------------
            // Agregar salto de pagina al ultimo parrafo del documento
            // -----------------------------------------------------------------------------------------------------------------------------------------------
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

            // Calcular el tamaño del vector de documentos si es par o
            // impar la división suma una
            // casilla
            int tamanio = 0;

            if ((datos.size() % NUMERO_PAGINAS) != 0) {
                tamanio = (datos.size() / NUMERO_PAGINAS) + 1;
            }
            else {
                tamanio = datos.size() / NUMERO_PAGINAS;
            }

            ByteArrayOutputStream arregloDocumentos[] = new ByteArrayOutputStream[tamanio];
            int registroNumero = 0;
            int docuementoNumero = 0;

            FileInputStream tmp;

            // -----------------------------------------------------------------------------------------------------------------------------------------------
            // ------------------------------INICIO DE CAMBIO DE
            // DOCUMENTOS A
            // ARCHIVO FINAL
            // -----------------------------------------------
            // -----------------------------------------------------------------------------------------------------------------------------------------------
            for (int i = 0; i < datos.size(); i++) {

                tmp = new FileInputStream(ruta);
                doc = new CustomXWPFDocument(tmp);
                unirRuns(doc);
                // -----------------------------------------------------------------------------------------------------------------------------------------------
                // Agregar salto de pagina al ultimo parrafo del
                // documento
                // -----------------------------------------------------------------------------------------------------------------------------------------------
                XWPFParagraph prtmp = (XWPFParagraph) doc.getBodyElements()
                                .get(doc.getBodyElements().size() - 1);
                XWPFRun runTmp = prtmp.createRun();
                runTmp.addBreak(BreakType.PAGE);
                reemplazarInformacionPlantilla(doc, docPlant, datos, i,
                                columnas, variables);
                reemplazarVariablesDeTabla(doc, etiquetasTmp, i, datos,
                                variablesConsultaWord);
                // -----------------------------------------------------------------------------------------------------------------------------------------------
                // Crear documento temporal
                // -----------------------------------------------------------------------------------------------------------------------------------------------
                copyLayout(doc, documentoDestino);

                // Traslado de los elementos, que no estén en el
                // cuerpo del documento, del archivo
                // origen al destino
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
                        // Extract image from source docx file and
                        // insert into
                        // destination docx file.
                        for (XWPFRun srcRun : srcPr.getRuns()) {
                            // You need next code when you want to
                            // call
                            // XWPFParagraph.removeRun().
                            dstPr.createRun();
                            hasImage = !srcRun.getEmbeddedPictures().isEmpty();
                            for (XWPFPicture pic : srcRun
                                            .getEmbeddedPictures()) {
                                byte[] img = pic.getPictureData().getData();
                                long cx = pic.getCTPicture().getSpPr().getXfrm()
                                                .getExt().getCx();
                                long cy = pic.getCTPicture().getSpPr().getXfrm()
                                                .getExt().getCy();
                                try {
                                    // Working addPicture Code
                                    // below...
                                    String blipId = dstPr.getDocument()
                                                    .addPictureData(new ByteArrayInputStream(
                                                                    img),
                                                                    Document.PICTURE_TYPE_PNG);
                                    documentoDestino.createPictureCxCy(blipId,
                                                    documentoDestino.getNextPicNameNumber(
                                                                    Document.PICTURE_TYPE_PNG),
                                                    cx, cy);
                                }
                                catch (InvalidFormatException e1) {
                                    Logger.getLogger(
                                                    UtilitarioXwpfDocument.class
                                                                    .getName())
                                                    .log(Level.SEVERE, null,
                                                                    e1);
                                }
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
                    else if (bodyElement.getElementType()
                                    .compareTo(BodyElementType.TABLE) == 0) {
                        XWPFTable table = (XWPFTable) bodyElement;
                        copyStyle(doc, documentoDestino, doc.getStyles()
                                        .getStyle(table.getStyleID()));
                        documentoDestino.createTable();
                        int pos = documentoDestino.getTables().size() - 1;
                        documentoDestino.setTable(pos, table);
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
                                    new FileInputStream(ruta));
                    docuementoNumero++;
                }
            }
            // -----------------------------------------------------------------------------------------------------------------------------------------------
            // Se crea el vector de nombres del documento,y el vector
            // de
            // inputstream partiendo del vector de output stream
            // escrito por la
            // plantilla y los registros
            // ----------------------------------------------------------------------------------------------------------------------------------------------
            String nombresDocumentos[] = new String[arregloDocumentos.length];
            ByteArrayInputStream inputStreamDocumentos[] = new ByteArrayInputStream[arregloDocumentos.length];
            for (int i = 0; i < arregloDocumentos.length; i++) {
                inputStreamDocumentos[i] = new ByteArrayInputStream(
                                arregloDocumentos[i].toByteArray());
                nombresDocumentos[i] = (nombreDocumento != "" ? nombreDocumento
                    : "Documentos") + "-" + i + ".docx";
            }

            try {
                return JsfUtil.exportarComprimidoGeneralStreamed(
                                inputStreamDocumentos, nombresDocumentos);
            }
            catch (JRException | SQLException | DRException ex) {
                Logger.getLogger(UtilitarioXwpfDocument.class.getName())
                                .log(Level.SEVERE, null, ex);
            }

        }
        catch (IOException ex) {
            Logger.getLogger(UtilitarioXwpfDocument.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // Copy Styles of Table and Paragraph.
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

    public void eliminarPlantilla(CustomXWPFDocument docPlant,
        CustomXWPFDocument destDoc) {
        List<IBodyElement> bodyPlant = docPlant.getBodyElements();
        // quitar comentario importantes
        for (int i = bodyPlant.size() - 1; i > -1; i--) {
            destDoc.removeBodyElement(i);
        }
    }

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
     * Recorre los runs que se encuentren en la lista de parrafos,
     * para reemplazar cualquier variable que se encuentre. Método
     * alternativo a:
     * {@link #replaceAllBodyElements(List, List, List, int, HashMap)}
     * 
     * @param paragraphs
     * @param columnas
     * reemplazar.
     * @param datos
     * @param indice
     * @param variables
     */
    @SuppressWarnings("unused")
    private void reemplazarParrafos(List<XWPFParagraph> paragraphs,
        List<String> columnas, List<Registro> datos, int indice,
        HashMap<String, String> variables) {
        for (int i = 0; i < paragraphs.size(); i++) {
            XWPFParagraph paragraph = paragraphs.get(i);
            List<XWPFRun> runs = paragraph.getRuns();
            for (int j = 0; j < runs.size(); j++) {
                XWPFRun run = runs.get(j);
                try {
                    String text = run.getText(0) != null ? run.getText(0) : "";
                    if (text.trim().equals("<#") || text.trim().equals("<")) {
                        // Varios runs conforman una variable
                        for (int k = j + 1; k < runs.size(); k++) {
                            String next = runs.get(k).getText(0) != null
                                ? runs.get(k).getText(0)
                                : "";
                            if (next.trim().equals("#>")
                                || next.trim().equals(">")) {
                                for (int l = j + 1; l <= k; l++) {
                                    text = text + runs.get(l).getText(0);
                                    runs.get(l).setText("", 0);
                                }
                                run.setText(cambiarReemplazarTexto(text,
                                                columnas, datos, indice,
                                                variables), 0);
                                break;
                            }
                        }
                    }
                    else {
                        run.setText(cambiarReemplazarTexto(text, columnas,
                                        datos, indice, variables), 0);
                    }
                }
                catch (Exception e) {
                    Logger.getLogger(UtilitarioXwpfDocument.class.getName())
                                    .log(Level.SEVERE, null, e);
                }
            }
            paragraph.setSpacingAfter(0);
            paragraph.setSpacingAfterLines(0);
        }
    }

    /**
     * Reemplaza las variables que encuentre en los Runs de los
     * parrafos, del cuerpo o tablas existentes, en la lista de
     * elementos ingresada por parametro. Método basado en la solución
     * propuesta en: http://stackoverflow.com/a/30131160
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
        HashMap<String, String> variables) {
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
     * Busca los parrafos que existan en la tabla principal y tablas
     * internas, para hacer el respectivo reemplazo de variables.
     * 
     * @param table
     * @param columnas
     * @param datos
     * @param indice
     * @param variables
     */
    private void replaceTable(XWPFTable table, List<String> columnas,
        List<Registro> datos, int indice,
        HashMap<String, String> variables) {
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
        HashMap<String, String> variables) {
        List<XWPFRun> runs = paragraph.getRuns();
        for (int j = 0; j < runs.size(); j++) {
            XWPFRun run = runs.get(j);
            String text = run.getText(0) != null ? run.getText(0) : "";
            if (text.contains("<#") && text.contains("#>")) {
                // La variable viene como un único run
                run.setText(cambiarReemplazarTexto(text, columnas, datos,
                                indice, variables), 0);
            }
            else if ("<#".trim().equals(text) || "<".trim().equals(text)
                || text.contains("<#") || text.contains("<")) {
                // Varios runs conforman una variable
                for (int k = j + 1; k < runs.size(); k++) {
                    String next = runs.get(k).getText(0) != null
                        ? runs.get(k).getText(0)
                        : "";
                    if ("#>".trim().equals(next) || ">".trim().equals(next)
                        || next.contains("#>") || next.contains(">")) {
                        for (int l = j + 1; l <= k; l++) {
                            text = text + runs.get(l).getText(0);
                            runs.get(l).setText("", 0);
                        }
                        run.setText(cambiarReemplazarTexto(text, columnas,
                                        datos, indice, variables), 0);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Traslado del texto de los encabezados y pies de página del
     * archivo origen al destino.
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
            else if (bodyElement.getElementType()
                            .compareTo(BodyElementType.TABLE) == 0) {
                exportarTablas((XWPFTable) bodyElement,
                                (XWPFTable) bodyElementOrigen);
            }
        }
    }

    /**
     * Traslado de los runs que contengan los parrafos del documento
     * origen a los parrafos del documento destino.
     * 
     * @param paragraph
     * Parrafo a afectar.
     * @param paragraphOrigen
     * Parrafo origen. Elemento en el que ya fueron reemplazadas las
     * variables.
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
     * Recorrido para trasladar los parrafos que contengan las tablas
     * del documento origen a las tablas del documento destino.
     * 
     * @param table
     * Tabla que se va a afectar.
     * @param tableOrigen
     * Tabla origen. Elemento en el que ya fueron reemplazadas las
     * variables.
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

}
