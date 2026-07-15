
/*-
 * InfDependenciasModalidadSeleccionReporteador.java
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

import com.sysman.contratos.enums.InfDependenciasModalidadSeleccionControladorEnum;
import com.sysman.contratos.enums.InfDependenciasModalidadSeleccionControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;

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
 * @version 1.0, 11/08/2017
 * @author eamaya
 *
 */
public class InfDependenciasModalidadSeleccionReporteador {
    private final String anioInicialCons;
    private final String anioFinalCons;
    private final String dependenciaCons;
    private final String companiaCons;
    private final String claseContrato;
    Log logger;
    

    private FormContinuoService service = FormContinuoService.getInstance();

    public InfDependenciasModalidadSeleccionReporteador() {
        anioFinalCons = "anioFinal";
        anioInicialCons = "anioInicial";
        dependenciaCons = "dependencia";
        claseContrato = "claseContrato";
        companiaCons = "compania";
       
     

    }

    public boolean exportarHojaDatosExcel(String consulta,
        OutputStream stream, String compania, String modalidad,
        String anioInicial, String anioFinal,
        String dependencia, String nombreDependencia,RequestManager requestManager) {
        boolean respuesta = false;
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(InfDependenciasModalidadSeleccionControladorEnum.MODALIDAD
                            .getValue(),
                            modalidad);

            param.put(InfDependenciasModalidadSeleccionControladorEnum.ANIOINICIAL
                            .getValue(), anioInicial);
            param.put(InfDependenciasModalidadSeleccionControladorEnum.ANIOFINAL
                            .getValue(), anioFinal);

            param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);

            List<Registro> anios;

            anios = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfDependenciasModalidadSeleccionControladorUrlEnum.URL17470
                                                                            .getValue())
                                            .getUrl(), param));

            StringBuilder pivotConsulta = new StringBuilder();
            String pivotAux = "";
            if (!anios.isEmpty()) {
                for (int i = 0; i < anios.size(); i++) {
                    pivotConsulta.append(
                                    "" + anios.get(i).getCampos().get("ANIO")
                                        + ",");
                }
                pivotAux = pivotConsulta.toString().substring(0,
                                pivotConsulta.length() - 1);
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("modalidad", modalidad);
            reemplazar.put("compania", compania);
            reemplazar.put("anioInicial", anioInicial);
            reemplazar.put("anioFinal", anioFinal);
            reemplazar.put("dependencia", dependencia);
            reemplazar.put("pivotConsulta", pivotAux);
            String consulta1 = Reporteador.resuelveConsulta(
                            "800011DependenciasMod",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
            Collection<Map<String, Object>> dummyCollection1 = new ArrayList<>();

            TextColumnBuilder[] arr = JsfUtil.getDataSource(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection);
            TextColumnBuilder<?>[] arr1 = JsfUtil.getDataSource(consulta1,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection1);

            if (!dummyCollection.isEmpty() && !dummyCollection1.isEmpty()) {
                respuesta = true;

                JasperXlsExporterBuilder xlsExporter = export
                                .xlsExporter(stream)
                                .setDetectCellType(true)
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
                                .columns(arr1)
                                .sortBy(arr1[0])
                                .setDataSource(dummyCollection1);

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
                                                cmp.text(nombreDependencia)
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
                                                                                                Color.LIGHT_GRAY)
                                                                                .bold()),
                                                cmp.subreport(subReport1))
                                .ignorePageWidth().ignorePagination()
                                .columns(arr)
                                .setDataSource(dummyCollection)
                                .toXls(xlsExporter);

            }
            else {
                respuesta = false;
            }
        }
        catch (SystemException | DRException | IOException
                        | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }
}
