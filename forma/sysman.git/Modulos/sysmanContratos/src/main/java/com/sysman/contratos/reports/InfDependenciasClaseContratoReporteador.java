/*-
 * InfDependenciasClaseContratoReporteador.java
 *
 * 1.0
 * 
 * 11/08/2017
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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
 * 
 * Esta clase es una auxiliar para el controlador
 * InfDependenciasClaseContratoControlador, desde esta se delega la
 * creacion de una hoja de datos la cual esta muy personalizada,
 * buscado desacoplarlo del controlador en si.
 * 
 * @see {@link com.sysman.contratos.InfDependenciasClaseContratoControlador}
 * 
 * @version 1.0, 11/08/2017
 * @author eamaya
 *
 */
public class InfDependenciasClaseContratoReporteador {
    private final String anioInicialCons;
    private final String anioFinalCons;
    private final String dependenciaCons;
    private final String companiaCons;
    private final String claseContrato;

    private FormContinuoService service = FormContinuoService.getInstance();

    public InfDependenciasClaseContratoReporteador() {
        anioFinalCons = "anioFinal";
        anioInicialCons = "anioInicial";
        dependenciaCons = "dependencia";
        claseContrato = "claseContrato";
        companiaCons = "compania";
    }

    public boolean exportarHojaDatosExcel(String consulta,
        OutputStream stream, String compania,
        String anioInicial,
        String anioFinal, String dependencia, String nombreDependencia,
        String claseContrato)
                        throws SQLException, IOException, DRException,
                        SysmanException {
        boolean respuesta = false;

        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);

        List<Registro> anios = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        consulta);
        StringBuilder pivotConsulta1 = new StringBuilder();
        if (!anios.isEmpty()) {

            for (int i = 0; i < anios.size(); i++) {
                if (i == anios.size() - 1) {
                    pivotConsulta1.append("'"
                        + anios.get(i).getCampos().get("ANIO") + "'");
                }
                else {

                    pivotConsulta1.append(
                                    "'" + anios.get(i).getCampos().get("ANIO")
                                        + "',");
                }
            }
        }
        if (pivotConsulta1.length() != 0) {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("claseContrato", claseContrato);
            reemplazar.put("anioInicial", anioInicial);
            reemplazar.put("anioFinal", anioFinal);
            reemplazar.put("dependencia", dependencia);
            reemplazar.put("pivotConsulta1", pivotConsulta1);
            String consulta1 = Reporteador.resuelveConsulta(
                            "800009DependenciasClase",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
            Collection<Map<String, Object>> dummyCollection2 = new ArrayList<>();

            TextColumnBuilder[] arr = JsfUtil.getDataSource(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection);
            TextColumnBuilder<?>[] arr1 = JsfUtil.getDataSource(consulta1,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection2);

            if (!dummyCollection.isEmpty() && !dummyCollection2.isEmpty()) {
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

                JasperReportBuilder subReport2 = report().addPageFooter(chart)
                                .setDataSource(dummyCollection)
                                .ignorePageWidth().ignorePagination();

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
                                .summary(cmp.text(nombreDependencia),
                                                cmp.subreport(subReport2),
                                                cmp.text("VALOR TOTAL CONTRATADO"),
                                                cmp.subreport(subReport))
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
