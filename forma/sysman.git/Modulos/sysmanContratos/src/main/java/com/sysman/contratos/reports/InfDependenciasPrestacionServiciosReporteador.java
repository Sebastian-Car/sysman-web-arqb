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
import com.sysman.dao.Registro;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanFunciones;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
 * @version 1.0, 16/08/2017
 * @author jguerrero
 *
 */
public class InfDependenciasPrestacionServiciosReporteador {

    private FormContinuoService service = FormContinuoService
                    .getInstance();

    public boolean exportarHojaDatosExcel(String consulta,
        OutputStream stream,
        Map<String, Object> reemplazar,
        String nombreDependencia)
                        throws SQLException, IOException, DRException,
                        SysmanException {
        boolean respuesta = false;
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);
        List<Registro> anios = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        consulta);
        StringBuilder pivotConsulta = new StringBuilder();
        String pivotConsultaAux = "";
        if (!anios.isEmpty()) {
            for (int i = 0; i < anios.size(); i++) {
                //@asana, se agrega validación en pivot dependiendo de la base de datos
                if (SysmanFunciones.esBdSqlServer()) {
                    pivotConsulta.append("["
                                    + anios.get(i).getCampos().get("VIGENCIA") + "],");
                } else {
                    pivotConsulta.append(""
                                    + anios.get(i).getCampos().get("VIGENCIA") + ",");
                }
            }
            pivotConsultaAux = pivotConsulta.toString().substring(0,
                            pivotConsulta.length() - 1);
        }
        if (pivotConsultaAux != null) {

            reemplazar.put("pivotConsulta", pivotConsultaAux);
            String consulta1 = Reporteador.resuelveConsulta(
                            "800013DependenciasPrestacion",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            reemplazar.put("pivotConsulta", pivotConsultaAux);
            String consulta2 = Reporteador.resuelveConsulta(
                            "800014DependenciasPrestacion",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
            Collection<Map<String, Object>> dummyCollection2 = new ArrayList<>();
            Collection<Map<String, Object>> dummyCollection3 = new ArrayList<>();

            TextColumnBuilder[] arr = JsfUtil.getDataSource(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection);

            TextColumnBuilder[] arr1 = JsfUtil.getDataSource(consulta1,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection2);

            TextColumnBuilder[] arr2 = JsfUtil.getDataSource(consulta2,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection3);

            if (!dummyCollection.isEmpty() && !dummyCollection2.isEmpty()
                && !dummyCollection3.isEmpty()) {
                respuesta = true;

                JasperXlsExporterBuilder xlsExporter = export
                                .xlsExporter(stream).setDetectCellType(true)
                                .setIgnorePageMargins(true)
                                .setWhitePageBackground(false)
                                .setRemoveEmptySpaceBetweenColumns(true);

                Bar3DChartBuilder chart = cht.bar3DChart()
                                .setTitle(nombreDependencia).setFixedWidth(1200)
                                .setTitleFont(boldFont).setCategory(arr[0])
                                .series(cht.serie(arr[1]));

                JasperReportBuilder subReport2 = report().ignorePageWidth()
                                .ignorePagination().addPageFooter(chart)
                                .setDataSource(dummyCollection);

                JasperReportBuilder subReport = report()
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
                                .ignorePageWidth().ignorePagination()
                                .columns(arr1).sortBy(arr1[0])
                                .setDataSource(dummyCollection2);

                JasperReportBuilder subReport1 = report()
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
                                .ignorePageWidth().ignorePagination()
                                .columns(arr2).sortBy(arr2[0])
                                .setDataSource(dummyCollection3);

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
                                .summary(cmp.subreport(subReport2),
                                                cmp.text(" "),
                                                cmp.text("TOTAL CONTRATOS")
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
                                                cmp.subreport(subReport),
                                                cmp.text(" "),
                                                cmp.text("VALOR TOTAL CONTRATADO")
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
                                                cmp.text(" "),
                                                cmp.subreport(subReport1))
                                .ignorePageWidth().ignorePagination()
                                .columns(arr).setDataSource(dummyCollection)
                                .toXls(xlsExporter);
            }
            else {
                respuesta = false;

            }
        }

        return respuesta;

    }

}
