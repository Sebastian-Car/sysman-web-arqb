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

import com.sysman.contratos.enums.InfDependenciasControladorEnum;
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
public class InfDependenciasReporteador {

    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "anioInicial" en el controlador
     */
    private final String anioInicialCons;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "anioFinal" en el controlador
     */
    private final String anioFinalCons;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "dependencia" en el controlador
     */
    private final String dependenciaCons;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "compania" en el controlador
     */
    private final String companiaCons;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al texto "pivotConsulta2" en el controlador
     */
    private final String pivotConsulta2Cons;
    /**
     * Instancia que mediante el llamado al metodo getListado()
     * permite asignar a a un listado de registros el resultado de una
     * consulta SQL
     */
    private FormContinuoService service = FormContinuoService.getInstance();

    public InfDependenciasReporteador() {
        anioFinalCons = InfDependenciasControladorEnum.ANIOFINAL.getValue();
        anioInicialCons = InfDependenciasControladorEnum.ANIOINICIAL.getValue();
        dependenciaCons = InfDependenciasControladorEnum.DEPENDENCIA.getValue();
        companiaCons = InfDependenciasControladorEnum.COMPANIA.getValue();
        pivotConsulta2Cons = InfDependenciasControladorEnum.PIVOT2.getValue();
    }

    /**
     * Arma la estructura de la Hoja de Datos que se va a generar.
     * Involucra la creacion de tablas y graficos
     * 
     * @param consulta
     * Consulta para obtener los registros para el listado de anios
     * @param stream
     * Buffer de memoria para almacenar la información que se
     * presentara en el informe
     * @param compania
     * Compania de ingreso a la aplicacion
     * @param anioInicial
     * Anio inicial seleccionado en el Formulario para generacion del
     * informe
     * @param anioFinal
     * Anio final seleccionado en el Formulario para generacion del
     * informe
     * @param dependencia
     * Dependencia de la cual se quiere obtener el informe
     * @param nombreDependencia
     * Nombre de la dependencia seleccionada
     * @return Si el proceso se genera adecuadamente
     */
    public boolean exportarHojaDatosExcel(String consulta,
        OutputStream stream, String compania,
        String anioInicial,
        String anioFinal, String dependencia, String nombreDependencia)
                        throws SQLException, IOException, DRException,
                        SysmanException {
        boolean respuesta;
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);

        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(companiaCons, compania);
        reemplazar.put(anioInicialCons, anioInicial);
        reemplazar.put(anioFinalCons, anioFinal);
        reemplazar.put(dependenciaCons, dependencia);
        String consulta1 = Reporteador.resuelveConsulta(
                        InfDependenciasControladorEnum.DEPENDENCIAS5.getValue(),
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        List<Registro> anios = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        consulta);
        StringBuilder pivotConsulta2 = new StringBuilder();
        String pivotAux = "";
        if (!anios.isEmpty()) {
            for (int i = 0; i < anios.size(); i++) {
                pivotConsulta2.append("" + anios.get(i).getCampos().get(
                                InfDependenciasControladorEnum.ANIO.getValue())
                    + ",");
            }
            pivotAux = pivotConsulta2.toString().substring(0,
                            pivotConsulta2.length() - 1);
        }
        Map<String, Object> reemplazar2 = new HashMap<>();
        reemplazar2.put(companiaCons, compania);
        reemplazar2.put(anioInicialCons, anioInicial);
        reemplazar2.put(anioFinalCons, anioFinal);
        reemplazar2.put(dependenciaCons, dependencia);
        reemplazar2.put(pivotConsulta2Cons, pivotAux);
        String consulta2 = Reporteador.resuelveConsulta(
                        InfDependenciasControladorEnum.DEPENDENCIAS6.getValue(),
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar2);

        Map<String, Object> reemplazar3 = new HashMap<>();
        reemplazar3.put(companiaCons, compania);
        reemplazar3.put(anioInicialCons, anioInicial);
        reemplazar3.put(anioFinalCons, anioFinal);
        reemplazar3.put(dependenciaCons, dependencia);
        reemplazar3.put(pivotConsulta2Cons, pivotAux);
        String consulta3 = Reporteador.resuelveConsulta(
                        InfDependenciasControladorEnum.DEPENDENCIAS7.getValue(),
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar2);

        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
        Collection<Map<String, Object>> dummyCollection2 = new ArrayList<>();
        Collection<Map<String, Object>> dummyCollection3 = new ArrayList<>();
        Collection<Map<String, Object>> dummyCollection4 = new ArrayList<>();

        TextColumnBuilder<?>[] arr = JsfUtil.getDataSource(consulta,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection);
        TextColumnBuilder[] arr1 = JsfUtil.getDataSource(consulta1,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection2);
        TextColumnBuilder<?>[] arr2 = JsfUtil.getDataSource(consulta2,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection3);
        TextColumnBuilder<?>[] arr3 = JsfUtil.getDataSource(consulta3,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection4);

        if (!dummyCollection.isEmpty() && !dummyCollection2.isEmpty()
            && !dummyCollection3.isEmpty()
            && !dummyCollection4.isEmpty()) {
            respuesta = true;

            JasperXlsExporterBuilder xlsExporter = export.xlsExporter(stream)
                            .setDetectCellType(true)
                            .setIgnorePageMargins(true)
                            .setWhitePageBackground(false)
                            .setRemoveEmptySpaceBetweenColumns(true);

            Bar3DChartBuilder chart = cht.bar3DChart()
                            .setTitle(nombreDependencia).setFixedWidth(1200)
                            .setTitleFont(boldFont).setCategory(arr1[0])
                            .series(cht.serie(arr1[2]).setSeries(arr1[1]));

            JasperReportBuilder subReport2 = report().ignorePageWidth()
                            .ignorePagination().addPageFooter(chart)
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
                            .ignorePageWidth().ignorePagination().columns(arr2)
                            .sortBy(arr2[0]).setDataSource(dummyCollection3);

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
                            .ignorePageWidth().ignorePagination().columns(arr3)
                            .sortBy(arr3[0]).setDataSource(dummyCollection4);

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
                                                                                            Color.WHITE)
                                                                            .bold()),
                                            cmp.subreport(subReport1),
                                            cmp.text(InfDependenciasControladorEnum.VALOR
                                                            .getValue())
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
                                            cmp.subreport(subReport3))
                            .ignorePageWidth().ignorePagination().columns(arr)
                            .setDataSource(dummyCollection)
                            .toXls(xlsExporter);
        }
        else {
            respuesta = false;

        }
        return respuesta;
    }

}
