/*-
 * CambiosPatrimonioChipControlador.java
 *
 * 1.0
 * 
 * 23/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.CambiosPatrimonioChipControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * 
 *
 * @version 1.0, 23/03/2017
 * @author yrojas
 */
@ManagedBean
@ViewScoped

public class CambiosPatrimonioChipControlador extends BeanBaseModal {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * 
     */
    private String anio;

    /**
     * 
     */
    private String mes;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    private CellStyle style;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos ExcelBase y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoExcelBase;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * 
     */
    private List<Registro> listaAno;

    /**
     * 
     */
    private List<Registro> listaMesinicial;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de CambiosPatrimonioChipControlador
     */
    public CambiosPatrimonioChipControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        mes = "1";
        try {
            // 1374
            numFormulario = GeneralCodigoFormaEnum.CAMBIOS_PATRIMONIO_CHIP_CONTROLADOR
                            .getCodigo();

            contArchivoExcelBase = new ContenedorArchivo();
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

        cargarListaAno();
        cargarListaMesinicial();
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

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAno
     */
    public void cargarListaAno() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosPatrimonioChipControladorUrlEnum.URL5069
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaMesinicial
     */
    public void cargarListaMesinicial() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMesinicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosPatrimonioChipControladorUrlEnum.URL5408
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        FileInputStream fileIs;
        try {
            fileIs = new FileInputStream(contArchivoExcelBase.getArchivo());
            Workbook workbook = new HSSFWorkbook(fileIs);
            Sheet sheet = workbook.getSheet("Formulario");
            style = workbook.createCellStyle();
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setTopBorderColor(IndexedColors.GREEN.getIndex());
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setLeftBorderColor(IndexedColors.GREEN.getIndex());
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setRightBorderColor(IndexedColors.GREEN.getIndex());
            style.setAlignment(CellStyle.ALIGN_RIGHT);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBottomBorderColor(IndexedColors.GREEN.getIndex());
            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 16);
            font.setFontName("Arial");
            style.setFont(font);

            Cell cellMes = sheet.getRow(6).createCell(35);
            cellMes.setCellValue(mes);
            cellMes.setCellStyle(style);

            prepararInforme(sheet, "CAMBIOS PAT", 5, true, "J",
                            "FORMULAID", "AJ",
                            Integer.parseInt(mes));

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Formulario retencion 350-2014.xls");
        }
        catch (IOException | JRException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean prepararInforme(Sheet sheet,
        String strnombrehoja, int intfilainicial, boolean insertarfila,
        String strcolumnareferencia, String strCampoBuscar, String numColumna,
        int mes) {
        List<Registro> rs;
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.MES.getName(), mes);

            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiosPatrimonioChipControladorUrlEnum.URL9058
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs.isEmpty()) {
                return false;
            }
            envioDetalle(sheet, rs, strcolumnareferencia, strCampoBuscar,
                            numColumna);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    private void envioDetalle(Sheet sheet, List<Registro> rs,
        String strcolumnareferencia, String strCampoBuscar, String numColumna) {
        String signo1;
        String signo2;

        for (Registro registro : rs) {
            signo1 = "+";
            signo2 = "+";
            String busqueda = signo1 + registro.getCampos().get(strCampoBuscar)
                + signo2;
            int column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
            Cell cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            if (cell != null) {
                int numColumn = CellRangeAddress.valueOf(
                                "" + numColumna + ":" + numColumna + "")
                                .getFirstColumn();
                double aux = sheet.getRow(cell.getRowIndex()).getCell(numColumn)
                                .getNumericCellValue()
                    + ((BigDecimal) registro.getCampos().get("VALOR"))
                                    .doubleValue();
                Cell newCell = sheet.getRow(cell.getRowIndex())
                                .createCell(numColumn);
                newCell.setCellStyle(style);
                newCell.setCellValue(aux);
            }
        }
    }

    public Cell searchColumnSheet(String searchText, int column, int rowStart,
        int rowEnd, Sheet sheet) {
        Cell c = null;
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            if ((sheet.getRow(rowNum) != null)
                && (sheet.getRow(rowNum).getCell(column) != null)) {
                Row r = sheet.getRow(rowNum);
                if (r.getCell(column, Row.RETURN_BLANK_AS_NULL) == null) {
                    continue;
                }
                switch (r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                .getCellType()) {
                case HSSFCell.CELL_TYPE_STRING:
                    if ((searchText != null)
                        && r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                        .getStringCellValue()
                                        .contains(searchText)) {
                        c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                        return c;
                    }
                    break;
                case HSSFCell.CELL_TYPE_FORMULA:
                    if ((searchText != null) && r
                                    .getCell(column, Row.RETURN_BLANK_AS_NULL)
                                    .getCellFormula().contains(searchText)) {
                        c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                        return c;
                    }
                    break;
                default:
                    if ((searchText != null) && searchText.equals(
                                    r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                                    .getStringCellValue())) {
                        c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                        return c;
                    }
                    break;
                }
            }
        }
        return c;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        mes = "";
        cargarListaMesinicial();
    }
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoExcelBase
     * 
     * @return contArchivoExcelBase
     */
    public ContenedorArchivo getContArchivoExcelBase() {
        return contArchivoExcelBase;
    }

    /**
     * Asigna el objeto contArchivoExcelBase
     * 
     * @param contArchivoExcelBase
     * Variable a asignar en contArchivoExcelBase
     */
    public void setContArchivoExcelBase(
        ContenedorArchivo contArchivoExcelBase) {
        this.contArchivoExcelBase = contArchivoExcelBase;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaMesinicial
     * 
     * @return listaMesinicial
     */
    public List<Registro> getListaMesinicial() {
        return listaMesinicial;
    }

    /**
     * Asigna la lista listaMesinicial
     * 
     * @param listaMesinicial
     * Variable a asignar en listaMesinicial
     */
    public void setListaMesinicial(List<Registro> listaMesinicial) {
        this.listaMesinicial = listaMesinicial;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
