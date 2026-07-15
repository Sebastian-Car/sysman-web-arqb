/*-
 * InfDependenciasReporteador.java
 *
 * 1.0
 *
 * 10/08/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.reports;

import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

/**
 * Esta clase es una auxiliar para el controlador
 * InfDependenciasControlador, desde esta se delega la creacion de una
 * hoja de datos la cual esta muy personalizada, buscado desacoplarlo
 * del controlador en si.
 *
 * @see {@link com.sysman.contratos.InfDependenciasControlador}
 *
 * @version 1.0, 10/08/2017
 * @author cmanrique
 *
 */
public class InfDependenciasTipoGastoReporteador {

    public InfDependenciasTipoGastoReporteador() {
        // No tiene Sentencias
    }

    public boolean exportarHojaDatosExcel(String consulta,
        Map<String, Object> reemplazar,
        OutputStream stream, String nombreDependencia)
                        throws SQLException, IOException, DRException,
                        SysmanException {
        boolean respuesta;
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);

        String consulta1 = Reporteador.resuelveConsulta(
                        "800016DependenciasTipoGasto",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);

        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
        Collection<Map<String, Object>> dummyCollection1 = new ArrayList<>();

        if (!dummyCollection.isEmpty() && !dummyCollection1.isEmpty()) {
            respuesta = true;
            
            TextColumnBuilder[] arr = JsfUtil.getDataSource(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection);
            TextColumnBuilder[] arr2 = JsfUtil.getDataSource(consulta1,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection1);

            JasperXlsExporterBuilder xlsExporter = export.xlsExporter(stream)
                            .setDetectCellType(true)
                            .setIgnorePageMargins(true)
                            .setWhitePageBackground(false)
                            .setRemoveEmptySpaceBetweenColumns(true);

            Bar3DChartBuilder chart2 = cht.bar3DChart()
                            .setTitle(nombreDependencia).setFixedWidth(800)
                            .setTitleFont(boldFont).setCategory(arr2[0])
                            .series(cht.serie(arr2[1]), cht.serie(arr2[2]));

            JasperReportBuilder subReport4 = report().ignorePageWidth()
                            .ignorePagination().addPageFooter(chart2)
                            .setDataSource(dummyCollection1);

            JasperReportBuilder subReport3 = report()
                            .setColumnTitleStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(
                                                            Color.LIGHT_GRAY)
                                            .bold())
                            .setColumnStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(Color.WHITE)
                                            .bold())
                            .ignorePageWidth().ignorePagination().columns(arr2)
                            .sortBy(arr2[0]).setDataSource(dummyCollection1);

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
                            .setColumnStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(Color.WHITE)
                                            .bold())
                            .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW,
                                            "2")
                            .addProperty(JasperProperty.EXPORT_XLS_FIT_WIDTH,
                                            "1")
                            .addProperty(JasperProperty.EXPORT_XLS_FIT_HEIGHT,
                                            "1")
                            .addProperty(JasperProperty.EXPORT_XLS_DETECT_CELL_TYPE,
                                            "1")
                            .ignorePageWidth().ignorePagination()
                            .summary(
                                            cht.bar3DChart().setTitle(
                                                            nombreDependencia)
                                                            .setFixedWidth(800)
                                                            .setTitleFont(boldFont)
                                                            .setCategory(
                                                                            arr[0])
                                                            .series(cht.serie(
                                                                            arr[1]),
                                                                            cht.serie(arr[2]))
                                                            .setUseSeriesAsCategory(
                                                                            true),
                                            cmp.text("TOTAL CONTRATADO")
                                                            .setStyle(stl
                                                                            .style(stl.style(
                                                                                            stl.style().setPadding(
                                                                                                            2))
                                                                                            .setVerticalAlignment(
                                                                                                            VerticalAlignment.MIDDLE))
                                                                            .setBorder(stl.pen1Point())
                                                                            .setHorizontalAlignment(
                                                                                            HorizontalAlignment.CENTER)
                                                                            .setBackgroundColor(
                                                                                            Color.WHITE)
                                                                            .bold()),
                                            cmp.subreport(subReport3),
                                            cmp.subreport(subReport4))
                            .columns(arr).setDataSource(dummyCollection)
                            .toXls(xlsExporter);
        }
        else {
            respuesta = false;
        }
        return respuesta;

    }

}
