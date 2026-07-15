/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.jsfutil;

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.concatenatedReport;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.enums.ConstanteArchivo;
import com.sysman.util.enums.ConstanteResponse;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperCsvExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsxExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.base.datatype.AbstractDataType;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 *
 * @author cmanrique
 */

public class ReportesBean {

    public static final double FACTOR = 8.7;
    public static final int TAMANIO_MAXIMO = 870;

    public static final String SERVICIO_RUTA_APLICACIONES = "58003";
    private static final String HOJA_DATOS = "hojaDatos";
    private Map<Integer, String> rutas;

    private static ReportesBean instance;

    private static DatosSesion datos;
    private static String rutaImagen;
    private static String usuario;
    private static int modulo;
    private static String excelPlano;

    private static final Log LOGGER = LogFactory.getLog(JsfUtil.class);

    public enum FORMATOS {

        PDF, EXCEL, EXCEL97, CSV, TXT, XML
    }

    public ReportesBean() throws SysmanException {
        rutas = new HashMap<>();
        datos = null;
        cargarRutas();
    }

    public ReportesBean(DatosSesion dato) throws SysmanException {
        rutas = new HashMap<>();
        datos = dato;
        cargarRutas();
    }

    public static ReportesBean getInstance() {
        if (instance == null) {
            try {
                instance = new ReportesBean();
            }
            catch (SysmanException e) {
                LOGGER.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        cargaSesion();
        return instance;
    }

    public static ReportesBean getInstance(DatosSesion dato) {
        if (instance == null) {
            try {
                instance = new ReportesBean(dato);
            }
            catch (SysmanException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        cargaSesion();
        return instance;
    }

    public void cargarRutas() throws SysmanException {
        RequestManager re = new RequestManager();
        try {
            List<Parameter> aplicaciones = re
                            .getList(UrlServiceUtil.getUrlBeanById(
                                            SERVICIO_RUTA_APLICACIONES)
                                            .getUrl(), null);
            for (Parameter parameter : aplicaciones) {
                rutas.put((int) parameter.getFields().get("APLICACION"),
                                (String) parameter.getFields()
                                                .get("RUTA_ARCHIVOS"));
            }
        }
        catch (SystemException e) {
            throw new SysmanException(e, e.getMessage());
        }

    }

    private JasperPrint getPrint(String nombreReporte,
        Map<String, Object> parametros, Connection conection,
        boolean validacion, FORMATOS formato)
                    throws JRException, IOException, SysmanException {
        asignarConstantes(parametros, formato);
        File jasper = validarArchivo(nombreReporte);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(),
                        parametros, conection);

        if (!validacion && jasperPrint.getPages().isEmpty()) {
            throw new SysmanException("No existen datos");

        }
        return jasperPrint;
    }

    private void exportarHojaDatosExcel(String consulta, Connection conection,
        OutputStream stream)
                    throws SQLException, IOException, DRException,
                    SysmanException {
        Statement st = conection.createStatement();
        ResultSet rs = st.executeQuery(consulta);
        TextColumnBuilder<?>[] arr = new TextColumnBuilder[rs.getMetaData()
                        .getColumnCount()];
        int[] tamanios = new int[rs.getMetaData().getColumnCount()];
        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();

        getDataSource(rs, tamanios, arr, dummyCollection);
        st.close();
        rs.close();
        if (dummyCollection.isEmpty()) {
            throw new SysmanException(
                            "No existen datos con los parametros suministrados");
        }

        JasperXlsExporterBuilder xlsExporter = export.xlsExporter(stream)
                        .setDetectCellType(true)
                        .setIgnorePageMargins(true)
                        .setWhitePageBackground(false)
                        .setRemoveEmptySpaceBetweenColumns(true);

        report().setColumnTitleStyle(
                        stl.style(stl.style(stl.style().setPadding(2))
                                        .setVerticalAlignment(
                                                        VerticalAlignment.MIDDLE))
                                        .setBorder(stl.pen1Point())
                                        .setHorizontalAlignment(
                                                        HorizontalAlignment.CENTER)
                                        .setBackgroundColor(Color.LIGHT_GRAY)
                                        .bold())
                        .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
                        .addProperty(JasperProperty.EXPORT_XLS_FIT_WIDTH, "1")
                        .addProperty(JasperProperty.EXPORT_XLS_FIT_HEIGHT, "1")
                        .addProperty(JasperProperty.EXPORT_XLS_DETECT_CELL_TYPE,
                                        "1")
                        .addProperty(JasperProperty.EXPORT_CHARACTER_ENCODING,
                                        "ISO-8859-1")
                        .ignorePageWidth()
                        .ignorePagination().columns(arr)
                        .setDataSource(dummyCollection).toXls(xlsExporter);

    }

    private void exportarHojaDatosCSV(String consulta, Connection conection,
        OutputStream stream)
                    throws SQLException, IOException, DRException,
                    SysmanException {
        Statement st = conection.createStatement();
        ResultSet rs = st.executeQuery(consulta);
        TextColumnBuilder<?>[] arr = new TextColumnBuilder[rs.getMetaData()
                        .getColumnCount()];
        int[] tamanios = new int[rs.getMetaData().getColumnCount()];
        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();

        getDataSource(rs, tamanios, arr, dummyCollection);
        st.close();
        rs.close();
        if (dummyCollection.isEmpty()) {
            throw new SysmanException(
                            "No existen datos con los parametros suministrados");
        }

        JasperCsvExporterBuilder csvExporter = export.csvExporter(stream);

        report().ignorePageWidth()
                        .ignorePagination().columns(arr)
                        .setDataSource(dummyCollection).toCsv(csvExporter);
    }

    private void exportarHojaDatosPdfConcatenado(String[] consultas,
        Connection conexion, ByteArrayOutputStream stream)
                    throws SQLException, IOException, DRException {
        JasperPdfExporterBuilder pdfExporter = export.pdfExporter(stream);

        JasperReportBuilder[] reportes = new JasperReportBuilder[consultas.length];
        for (int i = 0; i < reportes.length; i++) {
            reportes[i] = crearReporte(consultas[i], conexion);
        }

        concatenatedReport().continuousPageNumbering().concatenate(reportes)
                        .toPdf(pdfExporter);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    private void exportarHojaDatosExcelUltimaConcatenado(String[] consultas,
        Connection conexion, String[] nombreHojas,
        ByteArrayOutputStream stream)
                    throws SQLException, IOException, DRException {
        JasperXlsxExporterBuilder xlsExporter = export.xlsxExporter(stream)
                        .setDetectCellType(true)
                        .setIgnorePageMargins(true)
                        .setWhitePageBackground(false)
                        .setRemoveEmptySpaceBetweenColumns(true)
                        .sheetNames(nombreHojas).setOnePagePerSheet(true);

        JasperReportBuilder[] reportes = new JasperReportBuilder[consultas.length];
        for (int i = 0; i < reportes.length; i++) {
            reportes[i] = crearReporte(consultas[i], conexion);
        }

        concatenatedReport().continuousPageNumbering().concatenate(reportes)
                        .toXlsx(xlsExporter);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    private void exportarHojaDatosExcelConcatenado(String[] consultas,
        Connection conexion, String[] nombreHojas,
        ByteArrayOutputStream stream)
                    throws SQLException, IOException, DRException {
        JasperXlsExporterBuilder xlsExporter = export.xlsExporter(stream)
                        .setDetectCellType(true)
                        .setIgnorePageMargins(true)
                        .setWhitePageBackground(false)
                        .setRemoveEmptySpaceBetweenColumns(true)
                        .sheetNames(nombreHojas).setOnePagePerSheet(true);

        JasperReportBuilder[] reportes = new JasperReportBuilder[consultas.length];
        for (int i = 0; i < reportes.length; i++) {
            reportes[i] = crearReporte(consultas[i], conexion);
        }

        concatenatedReport().continuousPageNumbering().concatenate(reportes)
                        .toXls(xlsExporter);

        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     *
     * @param consulta
     * sentencia SQL SELECT
     * @param connection
     * conexi�n existente a la base de datos
     * @return Reporte como objeto de tipo JasperReportBuilder
     * @throws SQLException
     * @throws IOException
     */
    private JasperReportBuilder crearReporte(String consulta,
        Connection connection) throws SQLException, IOException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(consulta);
        TextColumnBuilder<?>[] columnas = new TextColumnBuilder[rs.getMetaData()
                        .getColumnCount()];
        int[] tamanios = new int[rs.getMetaData().getColumnCount()];

        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
        getDataSource(rs, tamanios, columnas, dummyCollection);

        JasperReportBuilder reporte = report();
        reporte.setColumnTitleStyle(
                        stl.style(stl.style(stl.style().setPadding(2))
                                        .setVerticalAlignment(
                                                        VerticalAlignment.MIDDLE))
                                        .setBorder(stl.pen1Point())
                                        .setHorizontalAlignment(
                                                        HorizontalAlignment.CENTER)
                                        .setBackgroundColor(Color.LIGHT_GRAY)
                                        .bold())
                        .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
                        .addProperty(JasperProperty.EXPORT_XLS_FIT_WIDTH, "1")
                        .addProperty(JasperProperty.EXPORT_XLS_FIT_HEIGHT, "1")
                        .addProperty(JasperProperty.EXPORT_XLS_DETECT_CELL_TYPE,
                                        "1")
                        .ignorePageWidth().ignorePagination()
                        .columns(columnas).setDataSource(dummyCollection);
        st.close();
        rs.close();
        return reporte;
    }

    private void exportarHojaDatosExcelUltima(String consulta,
        Connection conection, OutputStream stream)
                    throws SQLException, IOException, DRException,
                    SysmanException {

        Statement st = conection.createStatement();
        ResultSet rs = st.executeQuery(consulta);

        TextColumnBuilder<?>[] arr = new TextColumnBuilder[rs.getMetaData()
                        .getColumnCount()];
        int[] tamanios = new int[rs.getMetaData().getColumnCount()];

        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();

        getDataSource(rs, tamanios, arr, dummyCollection);
        st.close();
        rs.close();
        if (dummyCollection.isEmpty()) {
            throw new SysmanException(
                            "No existen datos con los parametros suministrados");
        }

        JasperXlsxExporterBuilder xlsExporter = export.xlsxExporter(stream)
                        .setDetectCellType(true)
                        .setIgnorePageMargins(true)
                        .setWhitePageBackground(false)
                        .setRemoveEmptySpaceBetweenColumns(true);

        report().setColumnTitleStyle(
                        stl.style(stl.style(stl.style().setPadding(2))
                                        .setVerticalAlignment(
                                                        VerticalAlignment.MIDDLE))
                                        .setBorder(stl.pen1Point())
                                        .setHorizontalAlignment(
                                                        HorizontalAlignment.CENTER)
                                        .setBackgroundColor(Color.LIGHT_GRAY)
                                        .bold())
                        .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "2")
                        .addProperty(JasperProperty.EXPORT_XLS_FIT_WIDTH, "1")
                        .addProperty(JasperProperty.EXPORT_XLS_FIT_HEIGHT, "1")
                        .addProperty(JasperProperty.EXPORT_XLS_DETECT_CELL_TYPE,
                                        "1")
                        .ignorePageWidth().ignorePagination()
                        .columns(arr).setDataSource(dummyCollection)
                        .toXlsx(xlsExporter);
        stream.flush();
        stream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    private void exportarHojaDatosPdf(String consulta, Connection conection,
        OutputStream stream)
                    throws SQLException, IOException, DRException,
                    SysmanException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conection.createStatement();
            rs = st.executeQuery(consulta);

            TextColumnBuilder<?>[] arr = new TextColumnBuilder[rs.getMetaData()
                            .getColumnCount()];
            int[] tamanios = new int[rs.getMetaData().getColumnCount()];

            Collection<Map<String, Object>> dummyCollection = new ArrayList<>();

            getDataSource(rs, tamanios, arr, dummyCollection);
            st.close();
            rs.close();
            if (dummyCollection.isEmpty()) {
                throw new SysmanException(
                                "No existen datos con los parametros suministrados");
            }

            JasperPdfExporterBuilder pdfExporter = export.pdfExporter(stream);
            report().setColumnTitleStyle(
                            stl.style(stl.style(stl.style().setPadding(2))
                                            .setVerticalAlignment(
                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(
                                                            Color.LIGHT_GRAY)
                                            .bold())
                            .ignorePageWidth().ignorePagination().columns(arr)
                            .setDataSource(dummyCollection)
                            .toPdf(pdfExporter);
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void getFactorValor(ResultSet rs, int i, int[] tamanios,
        Map<String, Object> map,
        TextColumnBuilder<?>[] arr) throws SQLException, IOException {
        int factorAux = 0;
        String valor;

        valor = ("CLOB").equals(rs.getMetaData().getColumnTypeName(i))
            || ("BLOB").equals(rs.getMetaData().getColumnTypeName(i))
                ? Acciones.clobToString(rs.getClob(i))
                : rs.getString(i);
        if (("NUMBER").equals(rs.getMetaData().getColumnTypeName(i))) {
            if (rs.getMetaData().getScale(i) == 0
                && rs.getMetaData().getPrecision(i) > 0
                && rs.getMetaData().getPrecision(i) < 10) {
                map.put(rs.getMetaData().getColumnName(i),
                                rs.getInt(i));
            }
            else if (rs.getMetaData().getScale(i) == 0
                && rs.getMetaData().getPrecision(i) >= 10) {
                map.put(rs.getMetaData().getColumnName(i),
                                rs.getLong(i));
            }
            else {
                map.put(rs.getMetaData().getColumnName(i), rs.getBigDecimal(i));
                factorAux = valor != null ? 3 + valor.length() / 3 : 0;
            }
        }
        else {
            map.put(rs.getMetaData().getColumnName(i), valor);
        }
        getTamanioValor(factorAux, valor, i, tamanios, arr, rs);

    }

    private void getTamanioValor(int factorAux, String valor, int i,
        int[] tamanios, TextColumnBuilder<?>[] arr,
        ResultSet rs) throws SQLException {
        if (valor != null
            && tamanios[i - 1] < valor.length() * (FACTOR + factorAux)) {
            if (valor.length() * (FACTOR + factorAux) > TAMANIO_MAXIMO) {
                tamanios[i - 1] = TAMANIO_MAXIMO;
            }
            else {

                tamanios[i - 1] = (int) (rs.getString(i).length()
                    * (FACTOR + factorAux));

            }
            arr[i - 1].setWidth(tamanios[i - 1]);
        }
    }

    private Collection<Map<String, Object>> getDataSource(ResultSet rs,
        int[] tamanios, TextColumnBuilder<?>[] arr,
        Collection<Map<String, Object>> dummyCollection)
                    throws SQLException, IOException {
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            AbstractDataType tipo;
            if (("NUMBER").equals(rs.getMetaData().getColumnTypeName(i))) {
                if (rs.getMetaData().getScale(i) == 0
                    && rs.getMetaData().getPrecision(i) > 0
                    && rs.getMetaData().getPrecision(i) < 10) {
                    tipo = type.integerType();
                }
                else if (rs.getMetaData().getScale(i) == 0
                    && rs.getMetaData().getPrecision(i) >= 10) {
                    tipo = type.longType();
                }
                else {
                    tipo = type.bigDecimalType();
                }
            }
            else {
                tipo = type.stringType();
            }
            arr[i - 1] = col.column(rs.getMetaData().getColumnName(i),
                            rs.getMetaData().getColumnName(i), tipo);
            tamanios[i - 1] = (int) (rs.getMetaData().getColumnName(i).length()
                * FACTOR);
            arr[i - 1].setWidth(tamanios[i - 1]);
        }

        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();

            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                getFactorValor(rs, i, tamanios, map, arr);
            }

            dummyCollection.add(map);
        }
        return dummyCollection;
    }

    public TextColumnBuilder<?>[] getDataSource(String consulta,
        Collection<Map<String, Object>> dummyCollection,
        Connection conection) throws SQLException, IOException, DRException {
        Statement st = null;
        ResultSet rs = null;
        TextColumnBuilder<?>[] arr = null;
        try {
            st = conection.createStatement();
            rs = st.executeQuery(consulta);
            arr = new TextColumnBuilder[rs.getMetaData().getColumnCount()];
            int[] tamanios = new int[rs.getMetaData().getColumnCount()];
            getDataSource(rs, tamanios, arr, dummyCollection);
        }
        finally {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return arr;
    }

    private void asignarConstantes(Map<String, Object> parametros,
        FORMATOS formato) {
        Date fechaActual = new Date();
        parametros.put("PR_FECHA", fechaActual);
        parametros.put("PR_HORA", fechaActual);
        parametros.put("PR_AHORA", fechaActual);
        parametros.put("PR_IMAGENES", rutaImagen);
        parametros.put("PR_GETUSER", usuario);
        String formatoSalida = (FORMATOS.EXCEL97).equals(formato)
            ? FORMATOS.EXCEL.toString()
            : formato.toString();
        parametros.put("PR_FORMATOSALIDA", formatoSalida);
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("es", "US"));
        parametros.put(JRParameter.REPORT_TIME_ZONE, TimeZone.getDefault());

    }

    private File validarArchivo(String nombreReporte)
                    throws FileNotFoundException {
        try {
            if (datos != null) {
                cargarRutas();
            }
            else {
                Boolean recarga = (Boolean) SessionUtil
                                .getApplicationVarContainer("recargarRutas");
                if ((recarga != null) && recarga) {
                    cargarRutas();
                    SessionUtil.setApplicationVarContainer("recargarRutas",
                                    false);
                }
            }
        }
        catch (SysmanException | NamingException e) {
            Logger.getLogger(ReportesBean.class.getName()).log(Level.SEVERE,
                            null, e);

        }
        File archivo = new File(rutas.get(modulo) + "informes" + File.separator
            + nombreReporte
            + ConstanteArchivo.JASPER_COMP.getExtension());
        String archivoModulo = archivo.getAbsolutePath();
        if (!archivo.exists()) {
            archivo = new File(rutas
                            .get(SysmanConstantes.CODIGO_APLICACION_GENERAL)
                + "informes" + File.separator
                + nombreReporte + ConstanteArchivo.JASPER_COMP.getExtension());
            if (!archivo.exists()) {
                throw new FileNotFoundException(
                                "No se encuentra el archivo: " + archivoModulo
                                    + "\n" + archivo.getAbsolutePath());
            }
        }
        return archivo;
    }

    public void exportar(String nombreReporte, Map<String, Object> parametros,
        Connection conection, FORMATOS formato)
                    throws JRException, FileNotFoundException, IOException,
                    SysmanException {
        HttpServletResponse response = (HttpServletResponse) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getResponse();
        ServletOutputStream stream = null;
        JasperPrint jasperPrint;
        switch (formato) {
        case PDF:
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);
            response.setContentType(ConstanteArchivo.PDF.getContentType());
            response.addHeader(
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getName(),
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getValue()
                                + nombreReporte
                                + ConstanteArchivo.PDF.getExtension());
            stream = response.getOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
            break;
        case EXCEL:
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);
            response.setContentType(ConstanteArchivo.EXCEL.getContentType());
            response.addHeader(
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getName(),
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getValue()
                                + nombreReporte
                                + ConstanteArchivo.EXCEL.getExtension());

            stream = response.getOutputStream();

            JRXlsxExporter exporterXlsx = new JRXlsxExporter();
            SimpleXlsxReportConfiguration configuracionXlsx = new SimpleXlsxReportConfiguration();
            configuracionXlsx.setDetectCellType(true);
            configuracionXlsx.setIgnoreCellBackground(true);
            configuracionXlsx.setMaxRowsPerSheet(65000);
            configuracionXlsx.setRemoveEmptySpaceBetweenColumns(true);
            configuracionXlsx.setRemoveEmptySpaceBetweenRows(true);
            exporterXlsx.setConfiguration(configuracionXlsx);
            exporterXlsx.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterXlsx.setExporterOutput((OutputStreamExporterOutput) stream);
            exporterXlsx.exportReport();
            break;
        case EXCEL97:
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);
            response.setContentType(ConstanteArchivo.EXCEL97.getContentType());
            response.addHeader(
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getName(),
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getValue()
                                + nombreReporte
                                + ConstanteArchivo.EXCEL97.getExtension());
            JRXlsExporter exporterXls = new JRXlsExporter();
            stream = response.getOutputStream();

            SimpleXlsReportConfiguration configuracionXls = new SimpleXlsReportConfiguration();
            configuracionXls.setDetectCellType(true);
            configuracionXls.setIgnoreCellBackground(true);
            configuracionXls.setMaxRowsPerSheet(65000);
            configuracionXls.setRemoveEmptySpaceBetweenColumns(true);
            configuracionXls.setRemoveEmptySpaceBetweenRows(true);
            exporterXls.setConfiguration(configuracionXls);
            exporterXls.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterXls.setExporterOutput((OutputStreamExporterOutput) stream);
            exporterXls.exportReport();
            break;
        case CSV:
            response.setContentType(ConstanteArchivo.CSV.getContentType());
            response.addHeader(
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getName(),
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getValue()
                                + nombreReporte + ".csv");
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);

            JRCsvExporter exporterCsv = new JRCsvExporter();
            stream = response.getOutputStream();
            exporterCsv.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterCsv.setExporterOutput(
                            new SimpleWriterExporterOutput(stream));
            exporterCsv.exportReport();
            break;
        default:
        }
        if (stream != null) {
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    /**
     * Permite generar la cadena de byte que se genera al generar el
     * reporte jasper
     * 
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     * @param nombreReporte
     * nombre del reporte a generar
     * @param parametros
     * parametros que utiliza el reporte para su generaci&oacute;n
     * @param conection
     * Conexi&oacute;n desde donde se tomara el origen de los datos
     * @param formato
     * Formato de salida del reporte
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     * 
     **/
    public ByteArrayOutputStream exportarStream(
        String nombreReporte,
        Map<String, Object> parametros, Connection conection,
        FORMATOS formato) throws JRException, FileNotFoundException,
                    IOException, SysmanException {
        JasperPrint jasperPrint = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                        stream);
        //mrosero CC1238 31/03/2025 Se agregó la validación para incluir el formato PDF en la condición
        // Esto permite que los reportes en formato PDF también sean procesados correctamente
        //mrosero CREMIL 03/01/2025
        if (excelPlano == null || "NO".equals(excelPlano)|| formato == ReportesBean.FORMATOS.PDF) {
            try {
                jasperPrint = getPrint(nombreReporte, parametros, conection, false, formato);
            } catch (Exception e) {
                throw new SysmanException(e, e.getMessage());
            }
        }

        switch (formato) {
        case PDF:
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
            break;
        case EXCEL:
            if ("SI".equals(excelPlano)) {
                stream = generarExcelPlano(nombreReporte,
                                (String) parametros.get("PR_STRSQL"),
                                conection, formato);
            }
            else {
                // Remover el Encabezado de pagina cuando se exporta a
                // excel y dejarlo visible solo al inicio del archivo
                jasperPrint.setProperty(
                                "net.sf.jasperreports.export.xlsx.exclude.origin.keep.first.band.1",
                                "pageHeader");
                // Remover el Encabezado de columnas cuando se exporta
                // a
                // excel y dejarlo visible solo al inicio del archivo
                jasperPrint.setProperty(
                                "net.sf.jasperreports.export.xlsx.exclude.origin.keep.first.band.2",
                                "columnHeader");
                // Remover el Pie de pagina cuando se exporta a excel
                jasperPrint.setProperty(
                                "net.sf.jasperreports.export.xlsx.exclude.origin.band.2",
                                "pageFooter");

                JRXlsxExporter exporterXlsx = new JRXlsxExporter();
                SimpleXlsxReportConfiguration configuracionXlsx = new SimpleXlsxReportConfiguration();
                configuracionXlsx.setDetectCellType(true);
                configuracionXlsx.setMaxRowsPerSheet(65000);
                configuracionXlsx.setRemoveEmptySpaceBetweenColumns(true);
                configuracionXlsx.setRemoveEmptySpaceBetweenRows(true);
                configuracionXlsx.setWrapText(true);

                exporterXlsx.setConfiguration(configuracionXlsx);
                exporterXlsx.setExporterInput(
                                new SimpleExporterInput(jasperPrint));
                exporterXlsx.setExporterOutput(exporterOutput);
                exporterXlsx.exportReport();
            }
            break;
        case EXCEL97:
            if ("SI".equals(excelPlano)) {
                stream = generarExcelPlano(nombreReporte,
                                (String) parametros.get("PR_STRSQL"),
                                conection, formato);
            }
            else {
                // Remover el Encabezado de pagina cuando se exporta a
                // excel y dejarlo visible solo al inicio del archivo
                jasperPrint.setProperty(
                                "net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.1",
                                "pageHeader");
                // Remover el Encabezado de columnas cuando se exporta
                // a
                // excel y dejarlo visible solo al inicio del archivo
                jasperPrint.setProperty(
                                "net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.2",
                                "columnHeader");
                // Remover el Pie de pagina cuando se exporta a excel
                jasperPrint.setProperty(
                                "net.sf.jasperreports.export.xls.exclude.origin.band.2",
                                "pageFooter");

                JRXlsExporter exporterXls = new JRXlsExporter();
                SimpleXlsReportConfiguration configuracionXls = new SimpleXlsReportConfiguration();
                configuracionXls.setDetectCellType(true);
                configuracionXls.setMaxRowsPerSheet(65000);
                configuracionXls.setRemoveEmptySpaceBetweenColumns(true);
                configuracionXls.setRemoveEmptySpaceBetweenRows(true);
                configuracionXls.setWrapText(true);

                exporterXls.setConfiguration(configuracionXls);
                exporterXls.setExporterInput(
                                new SimpleExporterInput(jasperPrint));
                exporterXls.setExporterOutput(exporterOutput);
                exporterXls.exportReport();
            }
            break;
        case CSV:
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);
            JRCsvExporter exporterCsv = new JRCsvExporter();

            exporterCsv.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterCsv.setExporterOutput(
                            new SimpleWriterExporterOutput(stream));
            exporterCsv.exportReport();
            break;
        default:
        }
        return stream;
    }

    private ByteArrayOutputStream generarExcelPlano(String nombreReporte,
        String consulta, Connection conection,
        FORMATOS formato) throws JRException, IOException, SysmanException {
        try {
            return exportarHojaDatosStream(conection, consulta, formato,
                            nombreReporte);
        }
        catch (SQLException | DRException e) {
            throw new SysmanException(e, e.getMessage());
        }
    }

    /**
     * Permite generar la cadena de byte que se genera al generar el
     * reporte jasper
     * 
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     * @param nombreReporte
     * nombre del reporte a generar
     * @param parametros
     * parametros que utiliza el reporte para su generaci&oacute;n
     * @param conection
     * Conexi&oacute;n desde donde se tomara el origen de los datos
     * @param formato
     * Formato de salida del reporte
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     * @see exportarStream para generar el reporte con un solo metodo
     * independiente de la salida
     * 
     **/
    public byte[] exportarStreamedSerializado(String nombreReporte,
        Map<String, Object> parametros, Connection conection,
        FORMATOS formato) throws JRException, FileNotFoundException,
                    IOException, SysmanException {
        return (exportarStream(nombreReporte, parametros, conection, formato))
                        .toByteArray();

    }

    /**
     * Se refactoriza con el fin de utilizar el metodo de
     * exportarStream
     * 
     * @see exportarStream utilizado para generar el archivo con un
     * solo metodo
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     * @param nombreReporte
     * nombre del reporte a generar
     * @param parametros
     * parametros que utiliza el reporte para su generaci&oacute;n
     * @param conection
     * Conexi&oacute;n desde donde se tomara el origen de los datos
     * @param formato
     * Formato de salida del reporte
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     */
    public StreamedContent exportarStreamed(String nombreReporte,
        Map<String, Object> parametros, Connection conection,
        FORMATOS formato) throws JRException, FileNotFoundException,
                    IOException, SysmanException {
        StreamedContent archivoDescarga = null;
        String tipoSalida = "";
        String extensionSalida = "";
        ByteArrayInputStream salida = new ByteArrayInputStream(
                        exportarStream(nombreReporte, parametros,
                                        conection,
                                        formato).toByteArray());
        switch (formato) {
        case PDF:
            extensionSalida = ConstanteArchivo.PDF.getExtension();
            tipoSalida = ConstanteArchivo.PDF.getContentType();
            break;
        case EXCEL:
            extensionSalida = ConstanteArchivo.EXCEL.getExtension();
            tipoSalida = ConstanteArchivo.EXCEL.getContentType();
            break;
        case EXCEL97:
            extensionSalida = ConstanteArchivo.EXCEL97.getExtension();
            tipoSalida = ConstanteArchivo.EXCEL97.getContentType();
            break;
        case CSV:
            extensionSalida = ConstanteArchivo.CSV.getExtension();
            tipoSalida = ConstanteArchivo.CSV.getContentType();
            break;
        default:
        }
        archivoDescarga = new DefaultStreamedContent(
                        salida,
                        tipoSalida, nombreReporte
                            + extensionSalida);
        return archivoDescarga;

    }

    /**
     * Permite generar la cadena en un ByteArrayOutputStream que se
     * genera al generar el reporte jasper y le aplica
     * contrase&ntilde;a al documento pdf
     * 
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     * @param nombreReporte
     * nombre del reporte a generar
     * @param parametros
     * parametros que utiliza el reporte para su generaci&oacute;n
     * @param conection
     * Conexi&oacute;n desde donde se tomara el origen de los datos
     * @param formato
     * Formato de salida del reporte
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     * 
     **/
    public ByteArrayOutputStream exportarStreamedSerializadoContrasena(
        String nombreReporte, Map<String, Object> parametros,
        Connection conection, FORMATOS formato, String password)
                    throws JRException, FileNotFoundException, IOException,
                    SysmanException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                        stream);
        JasperPrint jasperPrint;
        if (formato.equals(FORMATOS.PDF)) {
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);

            SimplePdfExporterConfiguration exportConfiguracionPdf = new SimplePdfExporterConfiguration();
            exportConfiguracionPdf.setOwnerPassword("SYSMANADMIN");
            exportConfiguracionPdf.setUserPassword(password);
            exportConfiguracionPdf.setEncrypted(true);
            exportConfiguracionPdf.setAllowedPermissionsHint("PRINTING");

            JRPdfExporter exporterPDF = new JRPdfExporter();
            exporterPDF.setConfiguration(exportConfiguracionPdf);
            exporterPDF.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterPDF.setExporterOutput(exporterOutput);
            exporterPDF.exportReport();
        }
        return stream;
    }

    /**
     * Permite descargar el archivo en pdf con contraseña
     * 
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     *
     * @param nombreReporte
     * @param parametros
     * @param conection
     * @param formato
     * @param password
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     */
    public StreamedContent exportarConContrasena(String nombreReporte,
        Map<String, Object> parametros,
        Connection conection, FORMATOS formato, String password)
                    throws JRException, FileNotFoundException, IOException,
                    SysmanException {
        byte[] salida;
        StreamedContent archivoDescarga = null;
        if (formato.equals(FORMATOS.PDF)) {
            salida = exportarStreamedSerializadoContrasena(nombreReporte,
                            parametros,
                            conection, formato, password).toByteArray();
            archivoDescarga = new DefaultStreamedContent(
                            new ByteArrayInputStream(salida),
                            ConstanteArchivo.PDF.getContentType(), nombreReporte
                                + ConstanteArchivo.PDF.getExtension());
        }
        return archivoDescarga;
    }

    public void exportarConContrasenaStreamed(String nombreReporte,
        Map<String, Object> parametros,
        Connection conection, FORMATOS formato, String password)
                    throws JRException, FileNotFoundException, IOException,
                    SysmanException {
        HttpServletResponse response = (HttpServletResponse) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getResponse();
        ServletOutputStream stream = null;
        JasperPrint jasperPrint;
        if (formato.equals(FORMATOS.PDF)) {
            jasperPrint = getPrint(nombreReporte, parametros, conection, false,
                            formato);
            response.addHeader(
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getName(),
                            ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                                            .getValue()
                                + nombreReporte
                                + ConstanteArchivo.PDF.getExtension());

            JRPdfExporter exporterPDF = new JRPdfExporter();
            stream = response.getOutputStream();

            SimplePdfExporterConfiguration exportConfiguracionPdf = new SimplePdfExporterConfiguration();
            exportConfiguracionPdf.setOwnerPassword("SYSMANADMIN");
            exportConfiguracionPdf.setUserPassword(password);
            exportConfiguracionPdf.setEncrypted(true);

            exporterPDF.setConfiguration(exportConfiguracionPdf);
            exporterPDF.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterPDF.setExporterOutput((OutputStreamExporterOutput) stream);
            exporterPDF.exportReport();
        }
        if (stream != null) {
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    public ByteArrayInputStream exportarConContrasenaSerializado(
        String nombreReporte, Map<String, Object> parametros,
        Connection conection, FORMATOS formato, String password)
                    throws JRException, FileNotFoundException, IOException,
                    SysmanException {
        byte[] salida = null;

        if (formato.equals(FORMATOS.PDF)) {
            salida = exportarStreamedSerializadoContrasena(nombreReporte,
                            parametros,
                            conection, formato, password).toByteArray();

        }
        return new ByteArrayInputStream(salida);
    }

    public void exportarHojaDatos(Connection conection, String sql,
        FORMATOS formato)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        HttpServletResponse response = (HttpServletResponse) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getResponse();
        ServletOutputStream stream = null;
        switch (formato) {
        case PDF:
            response.setContentType(ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                            .getName());
            response.addHeader(ConstanteArchivo.PDF.getContentType(),
                            "attachment; filename=hojaDatos.pdf");
            stream = response.getOutputStream();
            exportarHojaDatosPdf(sql, conection, stream);
            break;
        case EXCEL:
            response.setContentType(ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                            .getName());
            response.addHeader(ConstanteArchivo.EXCEL.getContentType(),
                            "attachment; filename=hojaDatos.xlsx");
            stream = response.getOutputStream();
            exportarHojaDatosExcelUltima(sql, conection, stream);
            break;
        case EXCEL97:
            response.setContentType(ConstanteResponse.CONTENT_DISP_ATT_FILENAME
                            .getName());
            response.addHeader(ConstanteArchivo.EXCEL97.getContentType(),
                            "attachment; filename=hojaDatos.xls");
            stream = response.getOutputStream();
            exportarHojaDatosExcel(sql, conection, stream);
            break;
        default:
        }
        if (stream != null) {
            stream.flush();
            stream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    /**
     * Permite generar una consulta como ByteArrayOutputStream con el
     * fin de que los demas metodos dependa de ella
     * 
     * @param conection
     * @param sql
     * @param formato
     * @param nombreArchivo
     * @return
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public ByteArrayOutputStream exportarHojaDatosStream(Connection conection,
        String sql, FORMATOS formato,
        String nombreArchivo) throws JRException, IOException, SQLException,
                    DRException, SysmanException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        switch (formato) {
        case PDF:
            exportarHojaDatosPdf(sql, conection, stream);
            break;
        case EXCEL97:
            exportarHojaDatosExcel(sql, conection, stream);
            break;
        case EXCEL:
            exportarHojaDatosExcelUltima(sql, conection, stream);
            break;
        case CSV:
            exportarHojaDatosCSV(sql, conection, stream);
            break;
        default:
        }
        return stream;
    }

    public StreamedContent exportarHojaDatosStreamed(Connection conection,
        String sql, FORMATOS formato,
        String nombreArchivo) throws JRException, IOException, SQLException,
                    DRException, SysmanException {
        ByteArrayOutputStream stream = exportarHojaDatosStream(conection, sql,
                        formato, nombreArchivo);
        StreamedContent archivoDescarga = null;
        String tipoSalida = "";
        String extensionSalida = "";
        switch (formato) {
        case PDF:
            extensionSalida = ConstanteArchivo.PDF.getExtension();
            tipoSalida = ConstanteArchivo.PDF.getContentType();
            break;
        case EXCEL:
            extensionSalida = ConstanteArchivo.EXCEL.getExtension();
            tipoSalida = ConstanteArchivo.EXCEL.getContentType();
            break;
        case EXCEL97:
            extensionSalida = ConstanteArchivo.EXCEL97.getExtension();
            tipoSalida = ConstanteArchivo.EXCEL97.getContentType();
            break;
        case CSV:
            extensionSalida = ConstanteArchivo.CSV.getExtension();
            tipoSalida = ConstanteArchivo.CSV.getContentType();
            break;
        default:
        }
        archivoDescarga = new DefaultStreamedContent(
                        new ByteArrayInputStream(stream.toByteArray()),
                        tipoSalida, nombreArchivo + extensionSalida);

        return archivoDescarga;
    }

    public StreamedContent exportarHojaDatosStreamed(Connection conection,
        String sql, FORMATOS formato)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        return exportarHojaDatosStreamed(conection, sql, formato,
                        HOJA_DATOS);

    }

    public StreamedContent exportarHojaDatosStreamed(Connection conexion,
        String[] consultas, FORMATOS formato,
        String[] nombreHojas) throws SQLException, IOException, DRException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        StreamedContent archivoDescarga = null;
        String tipoSalida = "";
        String extensionSalida = "";
        switch (formato) {
        case PDF:
            exportarHojaDatosPdfConcatenado(consultas, conexion, stream);
            tipoSalida = ConstanteArchivo.PDF.getContentType();
            extensionSalida = ConstanteArchivo.PDF.getExtension();
            break;
        case EXCEL:
            exportarHojaDatosExcelUltimaConcatenado(consultas, conexion,
                            nombreHojas, stream);
            tipoSalida = ConstanteArchivo.EXCEL.getContentType();
            extensionSalida = ConstanteArchivo.EXCEL.getExtension();
            break;
        case EXCEL97:
            exportarHojaDatosExcelConcatenado(consultas, conexion, nombreHojas,
                            stream);
            tipoSalida = ConstanteArchivo.EXCEL97.getContentType();
            extensionSalida = ConstanteArchivo.EXCEL97.getExtension();
            break;
        default:
        }
        archivoDescarga = new DefaultStreamedContent(
                        new ByteArrayInputStream(stream.toByteArray()),
                        tipoSalida,
                        HOJA_DATOS + extensionSalida);
        return archivoDescarga;
    }

    /**
     * Obtiene el arreglo de bytes de un reporte determinado
     * 
     * @param nombreReporte
     * c&oacute;digo que identifica el reporte
     * @param parametros
     * par&aacute;metros que recibe el reporte.
     * @param conection
     * conexi&oacute;n a la base de datos.
     * @param formato
     * tipo de archivo que se desea generar.
     * @return reporte como arreglo de bytes.
     * @throws JRException
     * en caso de que se presenten problemas al compilar y construir
     * el reporte en iReport.
     * @throws IOException
     * en caso de que no se pueda recuperar el archivo.
     * @throws SysmanException
     * en caso de que no existan datos para mostrar el informe.
     */
    public byte[] serializarReporteBase64(String nombreReporte,
        Map<String, Object> parametros, Connection conection,
        FORMATOS formato) throws JRException, IOException, SysmanException {

        ByteArrayOutputStream stream = exportarStream(nombreReporte, parametros,
                        conection, formato);
        return stream != null ? stream.toByteArray() : new byte[0];
    }

    public ByteArrayInputStream serializarReporte(String nombreReporte,
        Map<String, Object> parametros,
        Connection conection, FORMATOS formato)
                    throws JRException, IOException, SysmanException {

        byte[] salida = serializarReporteBase64(nombreReporte, parametros,
                        conection, formato);

        return new ByteArrayInputStream(salida);
    }

    public ByteArrayInputStream serializarHojaDatos(Connection conection,
        String sql, FORMATOS formato)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        ByteArrayOutputStream stream = null;
        switch (formato) {
        case PDF:
            stream = new ByteArrayOutputStream();
            exportarHojaDatosPdf(sql, conection, stream);
            break;
        case EXCEL:
            stream = new ByteArrayOutputStream();
            exportarHojaDatosExcelUltima(sql, conection, stream);
            break;
        case EXCEL97:
            stream = new ByteArrayOutputStream();
            exportarHojaDatosExcel(sql, conection, stream);
            break;
        default:
        }
        return new ByteArrayInputStream(
                        stream != null ? stream.toByteArray() : new byte[0]);
    }

    public void zipFiles(String[] files, String nombreReporte,
        Map<String, Object> parametros, Connection conection)
                    throws IOException, JRException {
        asignarConstantes(parametros, FORMATOS.PDF);
        File jasper = validarArchivo(nombreReporte);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(),
                        parametros, conection);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, stream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte[] bytes = stream.toByteArray();
        for (String fileName : files) {
            zos.putNextEntry(new ZipEntry(
                            fileName + ConstanteArchivo.PDF.getExtension()));
            int bytesRead = stream.size();
            zos.write(bytes, 0, bytesRead);
            zos.closeEntry();
        }
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        HttpServletResponse response = (HttpServletResponse) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getResponse();
        response.setContentType(ConstanteArchivo.ZIP.getContentType());
        response.addHeader(
                        ConstanteResponse.CONTENT_DISP_ATT_FILENAME.getName(),
                        ConstanteResponse.CONTENT_DISP_ATT_FILENAME.getValue()
                            + nombreReporte + ".zip");
        ServletOutputStream streamSal = response.getOutputStream();

        streamSal.write(baos.toByteArray());
        streamSal.flush();
        streamSal.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public ByteArrayOutputStream generarDataComprimidoReportes(
        ByteArrayOutputStream baos, ZipOutputStream zos,
        String[] nombres, Map<String, Object>[] listaParametros,
        Connection conection, FORMATOS... formatos)
                    throws IOException, JRException, SysmanException {
        boolean ind = baos == null;
        baos = baos == null ? new ByteArrayOutputStream() : baos;
        zos = zos == null ? new ZipOutputStream(baos) : zos;
        String nombreReporte;
        Map<String, Object> parametros;
        FORMATOS formato = null;
        String extensionSalida = "";

        boolean multiFormato = formatos.length > 1;
        if (!multiFormato) {
            formato = formatos[0];
        }

        for (int i = 0; i < nombres.length; i++) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            nombreReporte = nombres[i];
            parametros = listaParametros[i];
            extensionSalida = "";
            if (nombreReporte == null) {
                continue;
            }
            if (multiFormato) {
                formato = formatos[i];
            }

            stream = exportarStream(nombreReporte, parametros, conection,
                            formato);
            switch (formato) {
            case PDF:
                extensionSalida = ConstanteArchivo.PDF.getExtension();
                break;
            case EXCEL:
                extensionSalida = ConstanteArchivo.EXCEL.getExtension();
                break;
            case EXCEL97:
                extensionSalida = ConstanteArchivo.EXCEL97.getExtension();
                break;
            case CSV:
                extensionSalida = ConstanteArchivo.CSV.getExtension();
                break;
            default:
            }
            zos.putNextEntry(new ZipEntry(nombreReporte + extensionSalida));
            int bytesRead = stream.size();
            zos.write(stream.toByteArray(), 0, bytesRead);
            zos.closeEntry();
        }
        if (ind) {
            zos.flush();
            baos.flush();
            zos.close();
            baos.close();
        }
        return baos;
    }

    private ByteArrayOutputStream generarDataComprimidoHojasDatos(
        ByteArrayOutputStream baos, ZipOutputStream zos,
        String[] nombres, String[] consultas, Connection conection,
        FORMATOS... formatos)
                    throws IOException, JRException, SQLException, DRException,
                    SysmanException {
        boolean ind = baos == null;
        baos = baos == null ? new ByteArrayOutputStream() : baos;
        zos = zos == null ? new ZipOutputStream(baos) : zos;
        FORMATOS formato = null;
        boolean multiFormato = formatos.length > 1;
        if (!multiFormato) {
            formato = formatos[0];
        }
        for (int i = 0; i < consultas.length; i++) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (multiFormato) {
                formato = formatos[i];
            }
            switch (formato) {
            case PDF:
                exportarHojaDatosPdf(consultas[i], conection, stream);
                zos.putNextEntry(new ZipEntry(nombres[i]
                    + ConstanteArchivo.PDF.getExtension()));
                break;
            case EXCEL:
                exportarHojaDatosExcelUltima(consultas[i], conection, stream);
                zos.putNextEntry(new ZipEntry(nombres[i]
                    + ConstanteArchivo.EXCEL.getExtension()));
                break;
            case EXCEL97:
                exportarHojaDatosExcel(consultas[i], conection, stream);
                zos.putNextEntry(new ZipEntry(nombres[i]
                    + ConstanteArchivo.EXCEL97.getExtension()));
                break;
            default:
            }
            int bytesRead = stream.size();
            zos.write(stream.toByteArray(), 0, bytesRead);
            zos.closeEntry();
        }
        if (ind) {
            zos.flush();
            baos.flush();
            zos.close();
            baos.close();
        }
        return baos;
    }

    public void exportarZip(ByteArrayOutputStream baos) throws IOException {
        HttpServletResponse response = (HttpServletResponse) FacesContext
                        .getCurrentInstance().getExternalContext()
                        .getResponse();
        response.setContentType(ConstanteArchivo.ZIP.getContentType());
        response.addHeader(
                        ConstanteResponse.CONTENT_DISP_ATT_FILENAME.getName(),
                        ConstanteResponse.CONTENT_DISP_ATT_FILENAME.getValue());
        ServletOutputStream streamSal = response.getOutputStream();

        streamSal.write(baos.toByteArray());
        streamSal.flush();
        streamSal.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * las hojas de datos especificadas segun los parametros
     *
     * @param nombres
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     */
    public void generarComprimidoHojasDatos(String[] nombres,
        String[] consultas, Connection conection,
        FORMATOS... formatos) throws IOException, JRException, SQLException,
                    DRException, SysmanException {
        ByteArrayOutputStream baos = generarDataComprimidoHojasDatos(null, null,
                        nombres, consultas, conection,
                        formatos);
        exportarZip(baos);
    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * las hojas de datos especificadas segun los parametros
     *
     * @param nombres
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @return
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     */
    public StreamedContent generarComprimidoHojasDatosStreamed(String[] nombres,
        String[] consultas,
        Connection conection, FORMATOS... formatos)
                    throws IOException, JRException, SQLException, DRException,
                    SysmanException {
        ByteArrayOutputStream baos = generarDataComprimidoHojasDatos(null, null,
                        nombres, consultas, conection,
                        formatos);
        return exportarZipStreamed(baos);
    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * los reportes especificados segun los parametros
     *
     * @param nombres
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametros
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     */
    public void generarComprimidoReporte(String[] nombres,
        Map<String, Object>[] listaParametros, Connection conection,
        FORMATOS... formatos) throws IOException, JRException, SQLException,
                    DRException, SysmanException {
        ByteArrayOutputStream baos = generarDataComprimidoReportes(null, null,
                        nombres, listaParametros, conection,
                        formatos);
        exportarZip(baos);

    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * los reportes especificados segun los parametros
     *
     * @param nombres
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametros
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @return
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     */
    public StreamedContent generarComprimidoReporteStreamed(String[] nombres,
        Map<String, Object>[] listaParametros,
        Connection conection, FORMATOS... formatos)
                    throws IOException, JRException, SQLException, DRException,
                    SysmanException {
        ByteArrayOutputStream baos = generarDataComprimidoReportes(null, null,
                        nombres, listaParametros, conection,
                        formatos);
        return exportarZipStreamed(baos);
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Formato en el que se deben generar todos los reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Formato en el que se deben generar todas las hojas de datos
     * @param conection
     * Conexion con la base de datos
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public void generarComprimidoMixto(String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        FORMATOS formatoReportes, String[] nombresHojas,
        String[] consultasHojas, FORMATOS formatoHojas,
        Connection conection) throws IOException, JRException, SQLException,
                    DRException, SysmanException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        generarDataComprimidoReportes(baos, zos, nombresReportes,
                        listaParametrosReportes, conection, formatoReportes);
        generarDataComprimidoHojasDatos(baos, zos, nombresHojas, consultasHojas,
                        conection, formatoHojas);

        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
        exportarZip(baos);
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Formato en el que se deben generar todos los reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Formato en el que se deben generar todas las hojas de datos
     * @param conection
     * Conexion con la base de datos
     * @return
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public StreamedContent generarComprimidoMixtoStreamed(
        String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes, FORMATOS formatoReportes,
        String[] nombresHojas,
        String[] consultasHojas, FORMATOS formatoHojas, Connection conection)
                    throws IOException, JRException, SQLException, DRException,
                    SysmanException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        generarDataComprimidoReportes(baos, zos, nombresReportes,
                        listaParametrosReportes, conection, formatoReportes);
        generarDataComprimidoHojasDatos(baos, zos, nombresHojas, consultasHojas,
                        conection, formatoHojas);

        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
        return exportarZipStreamed(baos);
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Vector de formatos en el que se deben generar todos los
     * reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Vector de formatos en el que se deben generar todas las hojas
     * de datos
     * @param conection
     * Conexion con la base de datos
     * @return
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public StreamedContent generarComprimidoMixtoStreamed(
        String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        FORMATOS[] formatoReportes, String[] nombresHojas,
        String[] consultasHojas, FORMATOS[] formatoHojas, Connection conection)
                    throws IOException, JRException, SQLException, DRException,
                    SysmanException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        generarDataComprimidoReportes(baos, zos, nombresReportes,
                        listaParametrosReportes, conection, formatoReportes);
        generarDataComprimidoHojasDatos(baos, zos, nombresHojas, consultasHojas,
                        conection, formatoHojas);
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
        return exportarZipStreamed(baos);
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Vector de formatos en el que se deben generar todos los
     * reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Vector de formatos en el que se deben generar todas las hojas
     * de datos
     * @param conection
     * Conexion con la base de datos
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public void generarComprimidoMixto(String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        FORMATOS[] formatoReportes, String[] nombresHojas,
        String[] consultasHojas, FORMATOS[] formatoHojas,
        Connection conection) throws IOException, JRException, SQLException,
                    DRException, SysmanException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        generarDataComprimidoReportes(baos, zos, nombresReportes,
                        listaParametrosReportes, conection, formatoReportes);
        generarDataComprimidoHojasDatos(baos, zos, nombresHojas, consultasHojas,
                        conection, formatoHojas);
        zos.flush();
        baos.flush();
        zos.close();
        baos.close();
        exportarZip(baos);
    }

    public void generarComprimidoGeneral(ByteArrayInputStream[] salidas,
        String[] nombresArchivos) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            byte[] array;
            for (int i = 0; i < salidas.length; i++) {
                if (salidas[i] == null) {
                    continue;
                }
                zos.putNextEntry(new ZipEntry(nombresArchivos[i]));
                array = IOUtils.toByteArray(salidas[i]);
                zos.write(array, 0, array.length);
                zos.closeEntry();
            }
            zos.flush();
            baos.flush();
            zos.close();
            baos.close();
            exportarZip(baos);
        }
        catch (IOException ex) {
            Logger.getLogger(ReportesBean.class.getName()).log(Level.SEVERE,
                            null, ex);
        }

    }

    public StreamedContent generarComprimidoGeneralStreamed(
        ByteArrayInputStream[] salidas, String[] nombresArchivos) {
        return generarComprimidoGeneralStreamed(salidas, nombresArchivos,
                        "reportes");
    }

    /**
     *
     * @param salidas
     * Arreglo de InputStreams con cada uno de los archivos.
     * @param nombresArchivos
     * Arreglo de cadenas, con los nombres de cada uno de los
     * archivos.
     * @param nombreComprimido
     * Nombre, sin incluir extensi&oacute;n, que va a tener el archivo
     * comprimido que se va a generar.
     * @return archivoDescarga
     */
    public StreamedContent generarComprimidoGeneralStreamed(
        ByteArrayInputStream[] salidas, String[] nombresArchivos,
        String nombreComprimido) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        StreamedContent archivoDescarga = null;
        try {
            byte[] array;
            for (int i = 0; i < salidas.length; i++) {
                if (salidas[i] == null) {
                    continue;
                }
                zos.putNextEntry(new ZipEntry(nombresArchivos[i]));
                array = IOUtils.toByteArray(salidas[i]);
                zos.write(array, 0, array.length);
                zos.closeEntry();
            }
            zos.flush();
            baos.flush();
            zos.close();
            baos.close();
            archivoDescarga = new DefaultStreamedContent(
                            new ByteArrayInputStream(baos.toByteArray()),
                            ConstanteArchivo.ZIP.getContentType(),
                            nombreComprimido
                                + ConstanteArchivo.ZIP.getExtension());
        }
        catch (IOException ex) {
            Logger.getLogger(ReportesBean.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
        return archivoDescarga;
    }

    /**
     *
     * @param salidas
     * Arreglo de InputStreams con cada uno de los archivos.
     * @param nombresArchivos
     * Arreglo de cadenas, con los nombres de cada uno de los
     * archivos.
     * @param nombreComprimido
     * Nombre, sin incluir extensi&oacute;n, que va a tener el archivo
     * comprimido que se va a generar.
     * @return archivoDescarga
     */
    public byte[] generarComprimidoGeneralSerializado(
        ByteArrayInputStream[] salidas, String[] nombresArchivos,
        String nombreComprimido) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        byte[] archivoDescarga = new byte[0];
        try {
            byte[] array;
            for (int i = 0; i < salidas.length; i++) {
                if (salidas[i] == null) {
                    continue;
                }
                zos.putNextEntry(new ZipEntry(nombresArchivos[i]));
                array = IOUtils.toByteArray(salidas[i]);
                zos.write(array, 0, array.length);
                zos.closeEntry();
            }
            zos.flush();
            baos.flush();
            zos.close();
            baos.close();
            archivoDescarga = baos.toByteArray();
        }
        catch (IOException ex) {
            Logger.getLogger(ReportesBean.class.getName()).log(Level.SEVERE,
                            null, ex);
        }
        return archivoDescarga;
    }

    public StreamedContent exportarZipStreamed(ByteArrayOutputStream baos)
                    throws IOException {
        StreamedContent archivoDescarga;
        archivoDescarga = new DefaultStreamedContent(
                        new ByteArrayInputStream(baos.toByteArray()),
                        ConstanteResponse.CONTENT_DISP_ATT_FILENAME.getName(),
                        "reportes.zip");
        return archivoDescarga;
    }

    /**
     * Retorna un arreglo de bytes corrrespondiente a un reporte en
     * formato PDF.
     *
     * @param nombreReporte
     * Nombre del reporte
     * @param parametros
     * Parametros que recibe el reporte
     * @param conexion
     * Conexi&oacute;n con la base de datos
     * @return reporte en formato PDF
     * @throws JRException
     * @throws IOException
     * @author jrodrigueza
     */
    private byte[] getPDFBytesArray(String nombreReporte,
        Map<String, Object> parametros, Connection conexion)
                    throws JRException, IOException {
        String rutaReportes = rutas.get(modulo) + "informes" + File.separator;
        File jasper = new File(rutaReportes + nombreReporte
            + ConstanteArchivo.JASPER_COMP.getExtension());
        return JasperRunManager.runReportToPdf(jasper.getPath(), parametros,
                        conexion);
    }

    /**
     * Retorna un arreglo de bytes correspondiente a un reporte en
     * formato XLS.
     *
     * @param nombreReporte
     * Nombre del reporte
     * @param parametros
     * Parametros que recibe el reporte
     * @param conexion
     * Conexi&oacute;n con la base de datos
     * @return reporte en formato XLS
     * @throws JRException
     * @throws IOException
     * @author jrodrigueza
     */
    private byte[] getXLSBytesArray(String nombreReporte,
        Map<String, Object> parametros, Connection conexion)
                    throws JRException, IOException {
        String rutaReportes = rutas.get(modulo) + "informes" + File.separator;
        File jasper = new File(rutaReportes + nombreReporte
            + ConstanteArchivo.JASPER_COMP.getExtension());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(),
                        parametros, conexion);

        ByteArrayOutputStream xlsStream = new ByteArrayOutputStream();
        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsStream);
        exporter.exportReport();

        return xlsStream.toByteArray();
    }

    /**
     * Retorna un arreglo de bytes correspondiente a un reporte en
     * formato XLSX.
     *
     * @param nombreReporte
     * Nombre del reporte
     * @param parametros
     * Parametros que recibe el reporte
     * @param conexion
     * Conexi&oacute;n con la base de datos
     * @return reporte en formato XLSX
     * @throws JRException
     * @throws IOException
     * @author jrodrigueza
     */
    private byte[] getXLSXBytesArray(String nombreReporte,
        Map<String, Object> parametros, Connection conexion)
                    throws JRException, IOException {
        String rutaReportes = rutas.get(modulo) + "informes" + File.separator;
        File jasper = new File(rutaReportes + nombreReporte
            + ConstanteArchivo.JASPER_COMP.getExtension());
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(),
                        parametros, conexion);

        ByteArrayOutputStream xlsxStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsxStream);
        exporter.exportReport();

        return xlsxStream.toByteArray();
    }

    /**
     * Genera un reporte según un formato determinado como arreglo de
     * bytes.
     *
     * @param parametros
     * Parametros que recibe el reporte
     * @param nombreReporte
     * Nombre del reporte
     * @param formato
     * Formato del reporte
     * @param conexion
     * Nombre de la conexi&oacute;n
     * @return reporte como arreglo de bytes
     * @author jrodrigueza
     * @throws SysmanException
     */
    public byte[] generarReporte(String nombreReporte,
        Map<String, Object> parametros, FORMATOS formato,
        String conexion) throws NamingException, JRException, IOException,
                    SysmanException {
        ConectorPool cp = new ConectorPool();
        byte[] reportBytesArray = null;
        try {
            asignarConstantes(parametros, formato);
            cp.conectar(conexion);
            switch (formato) {
            case PDF:
                reportBytesArray = getPDFBytesArray(nombreReporte, parametros,
                                cp.getConection());
                break;
            case EXCEL97:
                reportBytesArray = getXLSBytesArray(nombreReporte, parametros,
                                cp.getConection());
                break;
            case EXCEL:
                reportBytesArray = getXLSXBytesArray(nombreReporte, parametros,
                                cp.getConection());
                break;
            default:
            }
        }
        catch (SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                cp.getConection().close();
            }
            catch (SQLException ex) {
                Logger.getLogger(ReportesBean.class.getName()).log(Level.SEVERE,
                                null, ex);
            }
        }
        return reportBytesArray;
    }

    private static void cargaSesion() {
        if (datos != null) {
            rutaImagen = datos.getCompaniaIngreso().getRutaImagen();
            usuario = datos.getUser().getCodigo();
            modulo = Integer.parseInt(datos.getModulo());
            excelPlano = datos.getExcelPlano();
        }
        else {
            rutaImagen = SessionUtil.getCompaniaIngreso().getRutaImagen();
            usuario = SessionUtil.getUser().getCodigo();
            modulo = Integer.parseInt(SessionUtil.getModulo());
            excelPlano = SessionUtil.getExcePlano();
        }

    }

    /**
     * Se refactoriza con el fin de utilizar el metodo de
     * exportarStream
     * 
     * @see exportarStream utilizado para generar el archivo con un
     * solo metodo
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     * @param nombreReporte
     * nombre del reporte a generar
     * @param parametros
     * parametros que utiliza el reporte para su generaci&oacute;n
     * @param json
     * Parametro que representa el json que se desea para generar el
     * reporte
     * @param formato
     * Formato de salida del reporte
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     */
    public StreamedContent exportarStreamed(String nombreReporte,
        Map<String, Object> parametros, String json,
        FORMATOS formato) throws JRException,
                    IOException, SysmanException {
        StreamedContent archivoDescarga = null;
        String tipoSalida = "";
        String extensionSalida = "";

        ByteArrayInputStream jsonDataStream = new ByteArrayInputStream(
                        json.getBytes());
        // Create json datasource from json stream
        JsonDataSource ds = new JsonDataSource(jsonDataStream);
        ds.setLocale(Locale.US);

        ByteArrayInputStream salida = new ByteArrayInputStream(
                        exportarStream(nombreReporte, parametros,
                                        ds,
                                        formato).toByteArray());
        switch (formato) {
        case PDF:
            extensionSalida = ConstanteArchivo.PDF.getExtension();
            tipoSalida = ConstanteArchivo.PDF.getContentType();
            break;
        case EXCEL:
            extensionSalida = ConstanteArchivo.EXCEL.getExtension();
            tipoSalida = ConstanteArchivo.EXCEL.getContentType();
            break;
        case EXCEL97:
            extensionSalida = ConstanteArchivo.EXCEL97.getExtension();
            tipoSalida = ConstanteArchivo.EXCEL97.getContentType();
            break;
        case CSV:
            extensionSalida = ConstanteArchivo.CSV.getExtension();
            tipoSalida = ConstanteArchivo.CSV.getContentType();
            break;
        default:
        }
        archivoDescarga = new DefaultStreamedContent(
                        salida,
                        tipoSalida, nombreReporte
                            + extensionSalida);
        return archivoDescarga;

    }

    /**
     * Permite generar la cadena de byte que se genera al generar el
     * reporte jasper
     * 
     * @author Jos&eacute; Pascual G&oacute;mez Blanco
     * @param nombreReporte
     * nombre del reporte a generar
     * @param parametros
     * parametros que utiliza el reporte para su generaci&oacute;n
     * @param jsonData
     * Conexi&oacute;n desde donde se tomara el origen de los datos
     * @param formato
     * Formato de salida del reporte
     * @return
     * @throws JRException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SysmanException
     * 
     **/
    public ByteArrayOutputStream exportarStream(
        String nombreReporte,
        Map<String, Object> parametros, JsonDataSource jsonData,
        FORMATOS formato) throws JRException,
                    IOException, SysmanException {
        JasperPrint jasperPrint;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                        stream);
        try {
            jasperPrint = getPrint(nombreReporte, parametros, jsonData, false,
                            formato);
        }
        catch (Exception e) {
            throw new SysmanException(e, e.getMessage());
        }

        switch (formato) {
        case PDF:
            JasperExportManager.exportReportToPdfStream(jasperPrint, stream);
            break;
        case EXCEL:

            // Remover el Encabezado de pagina cuando se exporta a
            // excel y dejarlo visible solo al inicio del archivo
            jasperPrint.setProperty(
                            "net.sf.jasperreports.export.xlsx.exclude.origin.keep.first.band.1",
                            "pageHeader");
            // Remover el Encabezado de columnas cuando se exporta
            // a
            // excel y dejarlo visible solo al inicio del archivo
            jasperPrint.setProperty(
                            "net.sf.jasperreports.export.xlsx.exclude.origin.keep.first.band.2",
                            "columnHeader");
            // Remover el Pie de pagina cuando se exporta a excel
            jasperPrint.setProperty(
                            "net.sf.jasperreports.export.xlsx.exclude.origin.band.2",
                            "pageFooter");

            JRXlsxExporter exporterXlsx = new JRXlsxExporter();
            SimpleXlsxReportConfiguration configuracionXlsx = new SimpleXlsxReportConfiguration();
            configuracionXlsx.setDetectCellType(true);
            configuracionXlsx.setMaxRowsPerSheet(65000);
            configuracionXlsx.setRemoveEmptySpaceBetweenColumns(true);
            configuracionXlsx.setRemoveEmptySpaceBetweenRows(true);
            configuracionXlsx.setWrapText(true);

            exporterXlsx.setConfiguration(configuracionXlsx);
            exporterXlsx.setExporterInput(
                            new SimpleExporterInput(jasperPrint));
            exporterXlsx.setExporterOutput(exporterOutput);
            exporterXlsx.exportReport();

            break;
        case EXCEL97:

            // Remover el Encabezado de pagina cuando se exporta a
            // excel y dejarlo visible solo al inicio del archivo
            jasperPrint.setProperty(
                            "net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.1",
                            "pageHeader");
            // Remover el Encabezado de columnas cuando se exporta
            // a
            // excel y dejarlo visible solo al inicio del archivo
            jasperPrint.setProperty(
                            "net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.2",
                            "columnHeader");
            // Remover el Pie de pagina cuando se exporta a excel
            jasperPrint.setProperty(
                            "net.sf.jasperreports.export.xls.exclude.origin.band.2",
                            "pageFooter");

            JRXlsExporter exporterXls = new JRXlsExporter();
            SimpleXlsReportConfiguration configuracionXls = new SimpleXlsReportConfiguration();
            configuracionXls.setDetectCellType(true);
            configuracionXls.setMaxRowsPerSheet(65000);
            configuracionXls.setRemoveEmptySpaceBetweenColumns(true);
            configuracionXls.setRemoveEmptySpaceBetweenRows(true);
            configuracionXls.setWrapText(true);

            exporterXls.setConfiguration(configuracionXls);
            exporterXls.setExporterInput(
                            new SimpleExporterInput(jasperPrint));
            exporterXls.setExporterOutput(exporterOutput);
            exporterXls.exportReport();

            break;
        case CSV:
            jasperPrint = getPrint(nombreReporte, parametros, jsonData, false,
                            formato);
            JRCsvExporter exporterCsv = new JRCsvExporter();

            exporterCsv.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporterCsv.setExporterOutput(
                            new SimpleWriterExporterOutput(stream));
            exporterCsv.exportReport();
            break;
        default:
        }
        return stream;
    }
    
    
    public void exportarXml(String nombreArchivo, String xmlGenerado)
            throws IOException {

        HttpServletResponse response = (HttpServletResponse)
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getResponse();

        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");

        response.addHeader(
                "Content-Disposition",
                "attachment; filename=\"" + nombreArchivo + ".xml\""
        );

        ServletOutputStream stream = response.getOutputStream();
        stream.write(xmlGenerado.getBytes("UTF-8"));
        stream.flush();
        stream.close();

        FacesContext.getCurrentInstance().responseComplete();
    }
    /**
     * 
     * Permite imprimir reporte desde un json
     * 
     * @param nombreReporte
     * @param parametros
     * @param jsonData
     * @param validacion
     * @param formato
     * @return
     * @throws JRException
     * @throws IOException
     * @throws SysmanException
     */
    private JasperPrint getPrint(String nombreReporte,
        Map<String, Object> parametros, JsonDataSource jsonData,
        boolean validacion, FORMATOS formato)
                    throws JRException, IOException, SysmanException {
        asignarConstantes(parametros, formato);
        File jasper = validarArchivo(nombreReporte);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasper.getPath(),
                        parametros, jsonData);

        if (!validacion && jasperPrint.getPages().isEmpty()) {
            throw new SysmanException("No existen datos");

        }
        return jasperPrint;
    }
}
