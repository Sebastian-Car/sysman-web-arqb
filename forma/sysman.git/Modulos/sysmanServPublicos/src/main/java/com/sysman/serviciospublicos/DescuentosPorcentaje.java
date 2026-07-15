/*-
 * DescuentosPorcentaje.java
 *
 * 1.0
 *
 * 16/11/2016
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
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.DescuentosPorcentajeEnum;
import com.sysman.serviciospublicos.enums.DescuentosPorcentajeUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el informe de descuentos para un
 * ciclo y un rango de codigos seleccionado
 *
 * @version 1.0, 16/11/2016
 * @author jlozano
 * 
 * @author eamaya
 * @version 2, 18/05/2017 Proceso de Refactoring
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Se cambi� el llamado del c�digo del
 * formulario
 * 
 */
@ManagedBean
@ViewScoped

public class DescuentosPorcentaje extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ciclo seleccionado
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo inicial seleccionado
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el codigo final seleccionado
     */
    private String codigoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Constante para el literal "CODIGORUTA"
     */
    private static final String CODIGO_RUTA = "CODIGORUTA";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene los codigos
     */
    private RegistroDataModelImpl listainicial;
    /**
     * Lista que contiene los codigos
     */
    private RegistroDataModelImpl listaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de DescuentosPorcentaje
     */
    public DescuentosPorcentaje() {
        super();
        compania = SessionUtil.getCompania();
        ciclo = "T";
        try {
            numFormulario = GeneralCodigoFormaEnum.DESCUENTOS_PORCENTAJE
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
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListainicial();

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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     */
    public void cargarListaCiclo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DescuentosPorcentajeUrlEnum.URL6312
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listainicial
     *
     */
    public void cargarListainicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DescuentosPorcentajeUrlEnum.URL6926
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        listainicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO_RUTA);
    }

    /**
     *
     * Carga la lista listaFinal
     *
     */
    public void cargarListaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DescuentosPorcentajeUrlEnum.URL7905
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        param.put(DescuentosPorcentajeEnum.PARAM0.getValue(),
                        codigoInicial);

        listaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO_RUTA);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (codigoInicial.compareToIgnoreCase(codigoFinal) > 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1857"));
        }
        else {
            generarInforme(ReportesBean.FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     * Genera el reporte en formato Excel
     *
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (codigoInicial.compareToIgnoreCase(codigoFinal) > 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1857"));
        }
        else {
            generarExcel();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ciclo
     *
     * Vacia los atributo codigoInicial y codigoFinal y recarga las
     * listas de los codigos
     *
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoInicial = "";
        codigoFinal = "";
        cargarListainicial();
        cargarListaFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_CICLO",
                            "T".equals(ciclo) ? "Todos" : ciclo);
            parametros.put("PR_INICIAL", codigoInicial);
            parametros.put("PR_FINAL", codigoFinal);
            parametros.put("PR_SIGLACOMPANIA",
                            SessionUtil.getCompaniaIngreso().getSigla());

            reemplazar.put("condicionCiclo", "T".equals(ciclo) ? " "
                : "AND SP_USUARIO.CICLO = " + ciclo);
            reemplazar.put("codIni", codigoInicial);
            reemplazar.put("codFin", codigoFinal);

            Reporteador.resuelveConsulta("001253InfDescuentos",
                            Integer.valueOf(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed("001253InfDescuentos",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void generarExcel() {

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("condicionCiclo", "T".equals(ciclo) ? " "
            : "AND SP_USUARIO.CICLO = " + ciclo);
        reemplazar.put("codIni", codigoInicial);
        reemplazar.put("codFin", codigoFinal);
        String strSql = Reporteador.resuelveConsulta("001253InfDescuentosExcel",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        try (Workbook workbook = new HSSFWorkbook(
                        JsfUtil.exportarHojaDatosStreamed(strSql,
                                        ConectorPool.ESQUEMA_SYSMAN,
                                        FORMATOS.EXCEL97).getStream())) {

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
            cell.setCellValue(idioma.getString("TB_TB1849"));
            cell.setCellStyle(style);
            Cell cell2 = sheet.createRow(1).createCell(0);

            cell2.setCellValue(idioma.getString("TB_TB1850")
                            .replace("s$ciclo$s", "T".equals(ciclo) ? "Todos"
                                : ciclo));
            cell2.setCellStyle(style);
            Cell cell3 = sheet.createRow(2).createCell(0);
            cell3.setCellValue(idioma.getString("TB_TB1851")
                            .replace("s$codigoInicial$s", codigoInicial)
                            .replace("s$codigoFinal$s", codigoFinal));
            cell3.setCellStyle(style);

            int filaFinal = sheet.getLastRowNum();
            Cell cellTotales = sheet.createRow(filaFinal + 1).createCell(0);
            cellTotales.setCellValue(
                            idioma.getString("TG_TOTAL_GENERAL") + " ");
            cellTotales.setCellStyle(style);
            CellStyle styleNum = workbook.createCellStyle();
            for (int i = 7; i <= (Math.max(sheet.getRow(4).getLastCellNum(), 0)
                - 1); i++) {
                Cell cellTotalesFormula = sheet.getRow(filaFinal + 1)
                                .createCell(i);
                cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
                CellReference cellRefIni = new CellReference(5, i);
                CellReference cellRefFin = new CellReference(filaFinal, i);
                String celdaIni = cellRefIni.formatAsString();
                String celdaFin = cellRefFin.formatAsString();
                cellTotalesFormula.setCellFormula(
                                "SUM(" + celdaIni + ":" + celdaFin + ")");
                styleNum.setDataFormat(workbook.createDataFormat()
                                .getFormat(" #,##0.00"));
                cellTotalesFormula.setCellStyle(styleNum);
            }

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "InfDescuentos.xls");
        }
        catch (SQLException | IOException | JRException
                        | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listainicial
     *
     * Asigna al atributo codigoInicial el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilainicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoInicial = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(CODIGO_RUTA).toString(),
                        "");

        cargarListaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaFinal
     *
     * Asigna al atributo codigoFinal el valor seleccionado
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(CODIGO_RUTA).toString(),
                        "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listainicial
     *
     * @return listainicial
     */
    public RegistroDataModelImpl getListainicial() {
        return listainicial;
    }

    /**
     * Asigna la lista listainicial
     *
     * @param listainicial
     * Variable a asignar en listainicial
     */
    public void setListainicial(RegistroDataModelImpl listainicial) {
        this.listainicial = listainicial;
    }

    /**
     * Retorna la lista listaFinal
     *
     * @return listaFinal
     */
    public RegistroDataModelImpl getListaFinal() {
        return listaFinal;
    }

    /**
     * Asigna la lista listaFinal
     *
     * @param listaFinal
     * Variable a asignar en listaFinal
     */
    public void setListaFinal(RegistroDataModelImpl listaFinal) {
        this.listaFinal = listaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
