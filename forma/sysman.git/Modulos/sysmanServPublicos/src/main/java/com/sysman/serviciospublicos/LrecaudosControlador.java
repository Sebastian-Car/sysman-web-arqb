/*-
 * LrecaudosControlador.java
 *
 * 1.0
 * 
 * 25/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LrecaudosControladorEnum;
import com.sysman.serviciospublicos.enums.LrecaudosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase es el controlador para el formulario que permite generar
 * el reporte de recaudos entre fechas
 * 
 * @version 1.0, 25/01/2017
 * @author spina
 * 
 * @author eamaya
 * @version 2.0, 07/06/2017 Manejo de EJBs y Correcciones SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class LrecaudosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo de servicios
     * publicos
     */
    private final String nomCompania;
    /**
     * Constante que almacena el nombre del modulo de servicios
     * publicos
     */
    private final String modulo;

    private final String constanteCondicion;

    private final String constanteOrdenAgrupado;

    private final String constanteReporte1369;

    private final String totalRecaudo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private String etTitulo;
    /**
     * Atributo usado para hacer visible o invisible el boton de
     * Generar Plano Novedades
     * 
     */
    private boolean mostrarPlanoNovedades;

    /**
     * Atributo usado para mostrar el check y la etiqueta de
     * consolidado en la vista
     * 
     */
    private boolean mostrarConsolidado;
    /**
     * Atributo usado para validar si se selecciona el check de
     * consolidado en la vista
     * 
     */
    private boolean valorCheckConsolidado;

    /**
     * Atributo que almacena la fecha inicial que fue seleccionada en
     * el formulario
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final que fue seleccionada en el
     * formulario
     */
    private Date fechaFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LrecaudosControlador
     */
    public LrecaudosControlador() {
        super();
        compania = SessionUtil.getCompania();
        nomCompania = SessionUtil.getCompaniaIngreso().getNombre();
        modulo = SessionUtil.getModulo();
        constanteCondicion = "condicion";
        constanteOrdenAgrupado = "ordenyagrupado";
        constanteReporte1369 = "001369LRecaudos";
        totalRecaudo = "TOTALRECAUDO";

        try {
            numFormulario = GeneralCodigoFormaEnum.LRECAUDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        if ("74070419".equals(SessionUtil.getMenuActual())) {
            etTitulo = "RECAUDOS ENTRE FECHAS";
        }
        else {
            etTitulo = "RESUMEN DE RECAUDOS ENTRE FECHAS";
        }

        try {
            String strAux = ejbSysmanUtil.consultarParametro(compania,
                            "PERMITE CARGAR Y RECAUDAR NOVEDADES EXTERNAS",
                            modulo, new Date(), false);

            mostrarPlanoNovedades = "SI".equals(strAux);

            strAux = ejbSysmanUtil.consultarParametro(compania,
                            "INFORME SALDOS CREDITO POR BANCO", modulo,
                            new Date(), false);

            mostrarConsolidado = "SI".equals(strAux);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     *
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envia los
     * parametros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        String reporte = "";
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put(constanteCondicion, "");
            reemplazar.put(constanteOrdenAgrupado, "");

            String strAux = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA MODULO INFORMES SERVICIOS PUBLICOS", modulo,
                            new Date(), false);

            if ("NO".equals(strAux)) {
                strAux = ejbSysmanUtil.consultarParametro(compania,
                                "NO MOSTRAR BANCO RELIQUIDACIONES 099", modulo,
                                new Date(), false);

                if ("SI".equals(strAux)) {
                    reemplazar.put(constanteCondicion,
                                    " AND V_SP_RECAUDOS.BANCO <> '099'");
                }
            }
            else {
                reemplazar.put(constanteCondicion,
                                " AND V_SP_RECAUDOS.NUMEROPAQUETE <> '888'");
            }
            reemplazar.put(constanteOrdenAgrupado,
                            " ORDER BY V_SP_RECAUDOS.COMPANIA, "
                                + "V_SP_RECAUDOS.FECHA, "
                                + "V_SP_RECAUDOS.BANCO, "
                                + "V_SP_RECAUDOS.NUMEROPAQUETE");

            strAux = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA INFORME CON RECAUDOS EXTERNOS", modulo,
                            new Date(), false);

            if ("SI".equals(strAux)) {
                reemplazar.put(constanteOrdenAgrupado,
                                " ORDER BY V_SP_RECAUDOS.FECHA,"
                                    + " V_SP_RECAUDOS.BANCO");

            }

            String consulta;

            if ("74070419".equals(SessionUtil.getMenuActual())) {
                strAux = ejbSysmanUtil.consultarParametro(compania,
                                "FORMATO CALIDAD", modulo, new Date(), false);

                if ("SI".equals(strAux)) {
                    // reporte "LRecaudosCOS"
                    consulta = "001385LRecaudosCOS";
                    reporte = "001385LRecaudosCOS";
                }
                else {
                    strAux = ejbSysmanUtil.consultarParametro(compania,
                                    "MANEJA MODULO INFORMES SERVICIOS PUBLICOS",
                                    modulo, new Date(), false);

                    if ("NO".equals(strAux)) {
                        strAux = ejbSysmanUtil.consultarParametro(compania,
                                        "MANEJA INFORME CON RECAUDOS EXTERNOS",
                                        modulo, new Date(), false);

                        if ("SI".equals(strAux)) {
                            if (valorCheckConsolidado) {
                                // reporte "001391LRecaudos4"
                                reporte = consulta = ejbSysmanUtil
                                                .consultarParametro(compania,
                                                                "NOMBRE INFORME RECAUDOS ENTRE FECHAS",
                                                                modulo,
                                                                new Date(),
                                                                false);

                            }
                            else {
                                // reporte "LRecaudos3"
                                consulta = constanteReporte1369;
                                reporte = "001382LRecaudos3";
                            }
                        }
                        else {
                            // reporte "LRecaudos2" igual a
                            // "LRecaudos"
                            consulta = constanteReporte1369;
                            reporte = constanteReporte1369;

                        }
                    }
                    else {
                        // reporte "LRecaudos"
                        consulta = constanteReporte1369;
                        reporte = constanteReporte1369;
                    }
                }
            }
            else {
                // reporte "LResumenRecaudo"
                consulta = "001390LResumenRecaudo";
                reporte = "001390LResumenRecaudo";
            }

            // HashMap reemplazar es para que reemplace en la
            // consulta almacenada en la tabla CONSULTAS

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_LRECAUDOS_FECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put("PR_FORMS_LRECAUDOS_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            if (SysmanFunciones
                            .validarVariableVacio(consulta)
                || SysmanFunciones.validarVariableVacio(reporte)) {
                return;
            }

            Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException | ParseException
                        | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        JsfUtil.agregarMensajeError(idioma.getString("TB_TB1750"));

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdGenerarPlano en la
     * vista
     *
     *
     */
    public void oprimirCmdGenerarPlano() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));

        HashMap<String, Object> param = new HashMap<>();

        param.put(LrecaudosControladorEnum.PARAM0.getValue(), fechaInicial);

        param.put(LrecaudosControladorEnum.PARAM1.getValue(), fechaFinal);

        /*
         * Recupera el registro que contiene el total de la consulta.
         */
        Registro registro;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            registro = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LrecaudosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));

            int total = 0;
            if (registro.getCampos().get(totalRecaudo) == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString("TI_EMPTY_MESSAGE"));
                return;
            }
            else {
                if (Integer.parseInt(
                                registro.getCampos().get(totalRecaudo)
                                                .toString()) != 0) {
                    total = Integer.parseInt(
                                    registro.getCampos().get(totalRecaudo)
                                                    .toString());
                }
                else {
                    JsfUtil.agregarMensajeError(
                                    idioma.getString("TI_EMPTY_MESSAGE"));
                    return;
                }
            }

            String sql = Reporteador.resuelveConsulta(
                            "800088DetalleFacturadoExterno",
                            Integer.parseInt(modulo), reemplazar);

            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(sql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());

            Sheet sheet = workbook.getSheet("Report");
            sheet.shiftRows(0, sheet.getLastRowNum(), 4);
            CellReference cellRefIniTitulo = new CellReference(0, 0);
            String celdaIniTitulo = cellRefIniTitulo.formatAsString();
            CellReference cellRefFinTitulo = new CellReference(0,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo = cellRefFinTitulo.formatAsString();
            CellRangeAddress region = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo + ":" + celdaFinTitulo);
            sheet.addMergedRegion(region);
            CellReference cellRefIniTitulo1 = new CellReference(1, 0);
            String celdaIniTitulo1 = cellRefIniTitulo1.formatAsString();
            CellReference cellRefFinTitulo1 = new CellReference(1,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo1 = cellRefFinTitulo1.formatAsString();
            CellRangeAddress region1 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo1 + ":" + celdaFinTitulo1);
            sheet.addMergedRegion(region1);

            CellReference cellRefIniTitulo2 = new CellReference(2, 0);
            String celdaIniTitulo2 = cellRefIniTitulo2.formatAsString();
            CellReference cellRefFinTitulo2 = new CellReference(2,
                            Math.max(sheet.getRow(4).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo2 = cellRefFinTitulo2.formatAsString();
            CellRangeAddress region2 = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo2 + ":" + celdaFinTitulo2);
            sheet.addMergedRegion(region2);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            Font font = workbook.createFont();
            font.setFontName("SansSerif");
            font.setBold(true);
            style.setFont(font);
            Cell cell = sheet.createRow(0).createCell(0);

            cell.setCellValue(nomCompania);
            cell.setCellStyle(style);
            Cell cell2 = sheet.createRow(1).createCell(0);

            cell2.setCellValue(idioma.getString("TB_TB2777"));
            cell2.setCellStyle(style);

            Cell cell3 = sheet.createRow(2).createCell(0);
            cell3.setCellValue(idioma.getString("TB_TB2778")
                            .replace("s$fechaInicial$s",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial))
                            .replace("s$fechaFinal$s",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaFinal)));

            cell3.setCellStyle(style);

            int filaFinal = sheet.getLastRowNum();
            Cell cellTotales = sheet.createRow(filaFinal + 1).createCell(0);
            cellTotales.setCellValue("TOTAL RECAUDO");
            cellTotales.setCellStyle(style);
            CellStyle styleNum = workbook.createCellStyle();

            Cell cellTotalesFormula = sheet.getRow(filaFinal + 1)
                            .createCell(2);
            cellTotalesFormula.setCellValue(total);
            cellTotalesFormula.setCellStyle(styleNum);
            styleNum.setDataFormat(workbook.createDataFormat()
                            .getFormat(" #,##0.00"));

            workbook.setForceFormulaRecalculation(true);

            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "800088DetalleFacturadoExterno.xls");

        }
        catch (SQLException | JRException | IOException
                        | DRException | ParseException
                        | SystemException | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getEtTitulo() {
        return etTitulo;
    }

    public void setEtTitulo(String etTitulo) {
        this.etTitulo = etTitulo;
    }

    public boolean isCarRecNovExt() {
        return mostrarPlanoNovedades;
    }

    public void setCarRecNovExt(boolean mostrarPlanoNovedades) {
        this.mostrarPlanoNovedades = mostrarPlanoNovedades;
    }

    public boolean isMostrarPlanoNovedades() {
        return mostrarPlanoNovedades;
    }

    public void setMostrarPlanoNovedades(boolean mostrarPlanoNovedades) {
        this.mostrarPlanoNovedades = mostrarPlanoNovedades;
    }

    public boolean isMostrarConsolidado() {
        return mostrarConsolidado;
    }

    public void setMostrarConsolidado(boolean mostrarConsolidado) {
        this.mostrarConsolidado = mostrarConsolidado;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getCompania() {
        return compania;
    }

    public boolean isValorCheckConsolidado() {
        return valorCheckConsolidado;
    }

    public void setValorCheckConsolidado(boolean valorCheckConsolidado) {
        this.valorCheckConsolidado = valorCheckConsolidado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
