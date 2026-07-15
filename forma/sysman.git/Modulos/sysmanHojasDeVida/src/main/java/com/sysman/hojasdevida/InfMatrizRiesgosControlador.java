/*-
 * InfMatrizRiesgosControlador.java
 *
 * 1.0
 * 
 * 03/10/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.InfMatrizRiesgosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite imprimir la matriz de riesgos.
 *
 * @version 1.0, 03/10/2018
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class InfMatrizRiesgosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String matriz;
    private int numReg;
    private String actividad;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaMatriz;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InfMatrizRiesgosControlador
     */
    public InfMatrizRiesgosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INF_MATRIZRIESGOS_CONTROLADOR
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
        cargarListaMatriz();
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
     * Carga la lista listaMatriz
     *
     */
    public void cargarListaMatriz() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfMatrizRiesgosControladorUrlEnum.URL6229
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaMatriz = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "VERSION");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    @SuppressWarnings("deprecation")
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        archivoDescarga = null;
        numReg=0;
        
        Map<String, Object> param3 = new HashMap<>();
        param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        Registro reg = RegistroConverter.toRegistro(
                        requestManager.get(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        InfMatrizRiesgosControladorUrlEnum.URL8711
                                                                        .getValue())
                                        .getUrl(), param3));
        
        
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("factores",reg.getCampos().get("LISTA").toString());
        reemplazos.put("version", matriz);

        String strSql = Reporteador.resuelveConsulta(
                        "900011DetalleMatrizRiesgos",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos);

       
            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            Sheet sheet = workbook.getSheetAt(0);
            sheet.shiftRows(0, sheet.getLastRowNum(), 12);
            sheet.createFreezePane(0, 13);
            

            Font font2 = workbook.createFont();
            font2.setFontName("Tahoma");
            font2.setFontHeightInPoints((short) 13);
            font2.setBold(true);

            Font font3 = workbook.createFont();
            font3.setFontName("Tahoma");
            font3.setFontHeightInPoints((short) 10);
            font3.setBold(true);
            
            
            Font font4 = workbook.createFont();
            font3.setFontName("Tahoma");
            font3.setFontHeightInPoints((short) 10);

            CellStyle style2 = workbook.createCellStyle();
            style2.setFont(font2);

            CellStyle style3 = workbook.createCellStyle();
            style3.setAlignment(CellStyle.ALIGN_CENTER);
            style3.setFont(font3);

            CellStyle style4 = workbook.createCellStyle();
            style4.setAlignment(CellStyle.ALIGN_CENTER);
            style4.setBorderBottom((short) 1);
            style4.setBorderLeft((short) 1);
            style4.setBorderTop((short) 1);
            style4.setBorderRight((short) 1);
            style4.setFont(font3);
            
            CellStyle style5 = workbook.createCellStyle();
            style5.setAlignment(CellStyle.ALIGN_CENTER);
            style5.setBorderBottom((short) 1);
            style5.setBorderLeft((short) 1);
            style5.setBorderTop((short) 1);
            style5.setBorderRight((short) 1);
            style5.setFont(font4);

            Row row = sheet.createRow(0);
            Cell cell = row.createCell(5);
            cell.setCellValue(idioma.getString("TT_LB48615"));
            cell.setCellStyle(style2);
            
            CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);
            CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);

            cell = row.createCell(23);
            cell.setCellValue("SST-M-001");
            cell.setCellStyle(style2);

            row = sheet.createRow(2);
            cell = row.createCell(23);
            cell.setCellValue("Versión");
            cell.setCellStyle(style2);

            row = sheet.createRow(4);
            cell = row.createCell(23);
            cell.setCellValue("Fecha: "
                + SysmanFunciones.convertirAFechaCadena(new Date(),
                                "dd'-'MM'-'yyyy"));
            cell.setCellStyle(style2);
            
            cell = row.createCell(5);
            cell.setCellValue(idioma.getString("TT_LB48616"));
            cell.setCellStyle(style2);
            
            CellUtil.setVerticalAlignment(cell, VerticalAlignment.CENTER);
            CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);

            // **************************************
            
            Map<String, Object> param2 = new HashMap<>();
            param2.put("KEY_COMPANIA", compania);
            param2.put("KEY_VERSION", matriz);

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfMatrizRiesgosControladorUrlEnum.URL4123
                                                                            .getValue())
                                            .getUrl(), param2));
            

            row = sheet.createRow(8);

            cell = row.createCell(0);
            cell.setCellValue("Tiempo: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(2);
            cell.setCellValue(reg.getCampos().get("TIEMPO").toString());
            cell.setCellStyle(style3);

            cell = row.createCell(4);
            cell.setCellValue("INICIO: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(6);
            cell.setCellValue(SysmanFunciones.convertirAFechaCadena((Date) reg.getCampos().get(GeneralParameterEnum.FECHA_INICIO.getName())));
            cell.setCellStyle(style3);

            cell = row.createCell(8);
            cell.setCellValue("FIN: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(10);
            cell.setCellValue(SysmanFunciones.convertirAFechaCadena((Date) reg.getCampos().get(GeneralParameterEnum.FECHA_FINAL.getName())));
            cell.setCellStyle(style3);

            cell = row.createCell(12);
            cell.setCellValue("ARL: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(14);
            cell.setCellValue(reg.getCampos().get("NOMBRE_ARL").toString());
            cell.setCellStyle(style3);

            cell = row.createCell(17);
            cell.setCellValue("ACTIVIDAD ECONOMICA: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(20);
            cell.setCellValue(reg.getCampos().get("ACTIVIDAD_ECONOMICA").toString());
            cell.setCellStyle(style3);

            // **************************************

            row = sheet.createRow(9);

            cell = row.createCell(0);
            cell.setCellValue("FECHA ELABORACIÓN: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(3);
            cell.setCellValue(SysmanFunciones.convertirAFechaCadena((Date) reg.getCampos().get("FECHA_ELABORACION")));
            cell.setCellStyle(style3);
            

            cell = row.createCell(6);
            cell.setCellValue("FECHA ACTUALIZACIÓN: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(9);
            cell.setCellValue(SysmanFunciones.convertirAFechaCadena((Date) reg.getCampos().get("FECHA_ACTUALIZACION")));
            cell.setCellStyle(style3);
            

            cell = row.createCell(12);
            cell.setCellValue("No. TRABAJADORES: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(14);
            cell.setCellValue(reg.getCampos().get("NUMERO_TRABAJADORES").toString());
            cell.setCellStyle(style3);

            cell = row.createCell(17);
            cell.setCellValue("ELABORACIÓN: ");
            cell.setCellStyle(style3);
            
            cell = row.createCell(20);
            cell.setCellValue(reg.getCampos().get("ELABORACION").toString());
            cell.setCellStyle(style3);


            // **************************************

            row = sheet.createRow(11);
            
            cell = row.createCell(0);
            cell.setCellValue("DEPENDENCIA");
            cell.setCellStyle(style4);
  
            cell = row.createCell(1);
            cell.setCellValue("PROCESO");
            cell.setCellStyle(style4);

            cell = row.createCell(2);
            cell.setCellValue("ZONA/LUGAR");
            cell.setCellStyle(style4);

            cell = row.createCell(3);
            cell.setCellValue("ACTIVIDADES");
            cell.setCellStyle(style4);

            cell = row.createCell(4);
            cell.setCellValue("TAREAS");
            cell.setCellStyle(style4);

            cell = row.createCell(5);
            cell.setCellValue("RUTINARIAS (SI O NO)");
            cell.setCellStyle(style4);

            Row row2 = sheet.createRow(12);

            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            List<Registro> lista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfMatrizRiesgosControladorUrlEnum.URL5746
                                                                            .getValue())
                                            .getUrl(), param));

            int aux = 0;
            for (int i = 0; i < lista.size(); i++) {

                cell = row.createCell(6 + aux);
                cell.setCellValue(lista.get(i).getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString());
                cell.setCellStyle(style3);

                if (Integer.parseInt(lista.get(i).getCampos()
                                .get(GeneralParameterEnum.CANTIDAD
                                                .getName())
                                .toString()) > 1) {
                    unirCeldas(workbook, sheet, 11, 6 + aux, 11, (6 - 1
                        + Integer.parseInt(lista.get(i).getCampos()
                                        .get(GeneralParameterEnum.CANTIDAD
                                                        .getName())
                                        .toString()))
                        + aux,
                                    2);
                }else{
                    cell.setCellStyle(style4);       
                }
                generarFactores(sheet, style4, lista.get(i).getCampos()
                                .get("GRUPO")
                                .toString(), 6 + aux, row2);

                if (i >= 0) {
                    aux = aux + Integer.parseInt(lista.get(i).getCampos()
                                    .get(GeneralParameterEnum.CANTIDAD
                                                    .getName())
                                    .toString());

                }
            }
            
           

            unirCeldas(workbook, sheet, 0, 0, 5, 4, 1);
            unirCeldas(workbook, sheet, 0, 5, 3, 22, 1);
            unirCeldas(workbook, sheet, 4, 5, 5, 22, 1);
            unirCeldas(workbook, sheet, 0, 23, 1, 26, 1);
            unirCeldas(workbook, sheet, 2, 23, 3, 26, 1);
            unirCeldas(workbook, sheet, 4, 23, 5, 26, 1);

            unirCeldas(workbook, sheet, 8, 0, 8, 1, 2);
            unirCeldas(workbook, sheet, 8, 2, 8, 3, 2);
            unirCeldas(workbook, sheet, 8, 4, 8, 5, 2);
            unirCeldas(workbook, sheet, 8, 6, 8, 7, 2);
            unirCeldas(workbook, sheet, 8, 8, 8, 9, 2);
            unirCeldas(workbook, sheet, 8, 10, 8, 11, 2);
            unirCeldas(workbook, sheet, 8, 12, 8, 13, 2);
            unirCeldas(workbook, sheet, 8, 14, 8, 16, 2);
            unirCeldas(workbook, sheet, 8, 17, 8, 19, 2);
            unirCeldas(workbook, sheet, 8, 20, 8, 26, 2);

            unirCeldas(workbook, sheet, 9, 0, 9, 2, 2);
            unirCeldas(workbook, sheet, 9, 3, 9, 5, 2);
            unirCeldas(workbook, sheet, 9, 6, 9, 8, 2);
            unirCeldas(workbook, sheet, 9, 9, 9, 11, 2);
            unirCeldas(workbook, sheet, 9, 12, 9, 13, 2);
            unirCeldas(workbook, sheet, 9, 14, 9, 16, 2);
            unirCeldas(workbook, sheet, 9, 17, 9, 19, 2);
            unirCeldas(workbook, sheet, 9, 20, 9, 26, 2);

            unirCeldas(workbook, sheet, 11, 0, 12, 0, 2);
            unirCeldas(workbook, sheet, 11, 1, 12, 1, 2);
            unirCeldas(workbook, sheet, 11, 2, 12, 2, 2);
            unirCeldas(workbook, sheet, 11, 3, 12, 3, 2);
            unirCeldas(workbook, sheet, 11, 4, 12, 4, 2);
            unirCeldas(workbook, sheet, 11, 5, 12, 5, 2);
            
            
            
            for (int j = 13; j < sheet.getLastRowNum()+1 ; j++) {
                row = sheet.getRow(j);
                for (int k = 0; k < numReg+6; k++) {
                    cell =  row.getCell(k);
                    cell.setCellStyle(style5);
                }
            }

            
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(10);
            sheet.autoSizeColumn(11);
            sheet.autoSizeColumn(12);
            sheet.autoSizeColumn(13);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);
            sheet.autoSizeColumn(16);
            sheet.autoSizeColumn(17);
            sheet.autoSizeColumn(18);
            sheet.autoSizeColumn(19);
            sheet.autoSizeColumn(20);
            sheet.autoSizeColumn(21);
            sheet.autoSizeColumn(22);
            sheet.autoSizeColumn(23);
            sheet.autoSizeColumn(24);
            
            

            workbook.write(out);

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(new ByteArrayInputStream(
                                            out.toByteArray()),
                                            "MatrizDeRiesgos.xls");

            workbook.close();
        }
        catch (JRException | IOException | ParseException | SystemException
                        | SQLException | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void generarFactores(Sheet sheet, CellStyle style3, String grupo,
        int aux, Row row) throws SystemException {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("GRUPO", grupo);

        List<Registro> lista = RegistroConverter.toListRegistro(
                        requestManager.getList(UrlServiceUtil.getInstance()
                                        .getUrlServiceByUrlByEnumID(
                                                        InfMatrizRiesgosControladorUrlEnum.URL8254
                                                                        .getValue())
                                        .getUrl(), param));
        numReg=numReg+lista.size();

        for (int i = 0; i < lista.size(); i++) {
            Cell cell = row.createCell(aux + i);
            cell.setCellValue(lista.get(i).getCampos()
                            .get("NOMBRE_FACTOR")
                            .toString());
            cell.setCellStyle(style3);
        }

    }

    @SuppressWarnings("deprecation")
    public void unirCeldas(Workbook workbook, Sheet sheet, int a, int b, int x,
        int y, int tipo) {
        CellReference celdaTitulo3Ini = new CellReference(a, b);
        String titulo3Ini = celdaTitulo3Ini.formatAsString();
        CellReference celdaTitulo3Fin = new CellReference(x, y);
        String titulo3Fin = celdaTitulo3Fin.formatAsString();
        CellRangeAddress region3 = CellRangeAddress
                        .valueOf("" + titulo3Ini + ":" + titulo3Fin);
        sheet.addMergedRegion(region3);

        if (tipo == 1) {
            RegionUtil.setBorderBottom(CellStyle.BORDER_DOUBLE,
                            region3, sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_DOUBLE,
                            region3, sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_DOUBLE,
                            region3, sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_DOUBLE,
                            region3, sheet, workbook);
           

        }
        else {
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                            region3, sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                            region3, sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                            region3, sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                            region3, sheet, workbook);
        }

        RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                        region3, sheet, workbook);
        RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                        region3, sheet, workbook);
        RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                        region3, sheet, workbook);
        RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                        region3, sheet, workbook);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaMatriz
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMatriz(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        matriz = registroAux.getCampos().get("VERSION").toString();
        actividad = registroAux.getCampos().get("ACTIVIDAD_ECONOMICA")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable matriz
     * 
     * @return matriz
     */
    public String getMatriz() {
        return matriz;
    }

    /**
     * Asigna la variable matriz
     * 
     * @param matriz
     * Variable a asignar en matriz
     */
    public void setMatriz(String matriz) {
        this.matriz = matriz;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaMatriz
     * 
     * @return listaMatriz
     */
    public RegistroDataModelImpl getListaMatriz() {
        return listaMatriz;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    /**
     * Asigna la lista listaMatriz
     * 
     * @param listaMatriz
     * Variable a asignar en listaMatriz
     */
    public void setListaMatriz(RegistroDataModelImpl listaMatriz) {
        this.listaMatriz = listaMatriz;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
