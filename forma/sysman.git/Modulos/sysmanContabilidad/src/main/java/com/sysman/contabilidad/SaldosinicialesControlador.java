/*-
 * SaldosinicialesControlador.java
 *
 * 1.0
 *
 * 28/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.SaldosInicialesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.ContenedorArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario Saldosiniciales su funcion principal es
 * subir datos de un excel a la tabla saldos iniciales
 *
 * @version 1.0, 28/11/2016
 * @author cperez
 * @modified spina 17/04/2017 - se eliminaron las validaciones del
 * tipo de celda al cargar el archivo de excel para poder subirlo se
 * modifica la lectura de archivos para formatos .XLS y .XLSX, se hace
 * la refactorizacion para DSS y depuracion de sonar
 * @version 3, 21/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author ybecerra
 * @version 4, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class SaldosinicialesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el numero de la fila en la que se deben empezar a subir
     * los archivos
     */
    private int filaBase;
    /**
     * Obtiene el nombre de la hoja de xml para generar el informe en
     * excel
     */
    private String nombreHoja;
    /*
     * contiene las observaciones encontradas en el archivo
     */
    private String obser = "";
    /*
     * contiene los dados del archivo
     */
    private String datos = "";
    /**
     * Constante para el literal "Hoja1"
     */
    private static final String NOMBRE_HOJA = "Hoja1";
    /**
     * Constante para el literal "MSM_TRANS_INTERRUMPIDA"
     */
    private static final String MSM_TRANS_INTERRUMPIDA = "MSM_TRANS_INTERRUMPIDA";
    /**
     * Constante para el literal "MSM_TRANS_INTERRUMPIDA"
     */
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos NombreArchivoBase y funciona como contenedor del
     * archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoNombreArchivoBase;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de hojas disponibles para el archivo seleccionado
     */
    private List<Registro> listaNombreHoja;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SaldosinicialesControlador
     */
    public SaldosinicialesControlador() {
        super();
        compania = SessionUtil.getCompania();
        contArchivoNombreArchivoBase = new ContenedorArchivo();
        filaBase = 2;
        try {
            numFormulario = GeneralCodigoFormaEnum.SALDOSINICIALES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaNombreHoja();
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
        filaBase = 2;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaNombreHoja
     */
    public void cargarListaNombreHoja() {
        try {
            listaNombreHoja = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SaldosInicialesControladorUrlEnum.URL5740
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * Para saber el valor del parametro "COMPAÑIA EQUIVALENTE NIIF"
     */
    public String unCompani() {
        // <CODIGO_DESARROLLADO>
        String unCompani = null;
        try {
            unCompani = ejbSysmanUtil.consultarParametro(compania,
                            "COMPAÑIA EQUIVALENTE NIIF",
                            SessionUtil.getModulo(), new Date(), true);

        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(idioma.getString(MSM_TRANS_INTERRUMPIDA)
                + ex.getMessage());
            Logger.getLogger(SaldosinicialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
        return unCompani;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Iniciar en la vista
     *
     * @throws JRException
     *
     */
    public void oprimirIniciar() {
        // <CODIGO_DESARROLLADO>
        if (contArchivoNombreArchivoBase.getArchivo() == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            return;
        }

        String rutaArchivo = contArchivoNombreArchivoBase.getArchivo()
                        .getPath();

        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());

        try (FileInputStream file = new FileInputStream(
                        contArchivoNombreArchivoBase.getArchivo());) {

            Workbook workbook = null;
            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(file);
            }
            else {
                workbook = new HSSFWorkbook(file);
            }

            Sheet sheet = workbook.getSheet(nombreHoja);
            datos = null;
            int rowStart = filaBase - 1;
            obser = "";
            int rowEnd = sheet.getLastRowNum();
            if (rowEnd != 0) {
                terminarExcel(rowStart, rowEnd, sheet, workbook);
            }
            workbook.setActiveSheet(workbook.getSheetIndex(sheet));
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            contArchivoNombreArchivoBase.getArchivo()
                                            .getName());
            file.close();
        }
        catch (IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (OutOfMemoryError ex) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB814"));
            Logger.getLogger(SaldosinicialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    /*
     *
     */
    public void terminarExcel(int rowStart, int rowEnd, Sheet sheet,
        Workbook workbook) {
        for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
            if (sheet.getRow(rowNum) == null) {
                // no hace nada
            }
            else {
                Row r = sheet.getRow(rowNum);
                int lastColumn = 12;
                StringBuilder bld = new StringBuilder();
                StringBuilder bldObser = new StringBuilder();
                for (int cn = 0; cn < lastColumn; cn++) {
                    Cell c = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
                    bld.append(ifCero(cn));
                    bld.append(ifUno(cn, c));
                    bldObser.append("");
                    bld.append(ifDos(cn, c));
                    bldObser.append("");
                    bld.append(ifTres(cn, c));
                    bldObser.append("");
                    bld.append(ifCuatro(cn, c));
                    bldObser.append("");
                    bld.append(ifCinco(cn, c));
                    bldObser.append("");
                    bld.append(ifSeis(cn, c));
                    bldObser.append("");
                    bld.append(ifSiete(cn, c));
                    bldObser.append("");
                    bld.append(ifOcho(cn, c));
                    bldObser.append("");
                    bld.append(ifNueve(cn, c));
                    bldObser.append("");
                    bld.append(ifDiez(cn, c));
                    bldObser.append("");
                    bld.append(ifOnce(cn, c));
                    bldObser.append("");
                    bld.append(ifDoce(cn, c));
                    bldObser.append("");
                }
                obser = bldObser.toString();
                CellStyle style = workbook.createCellStyle();
                style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                if ("".equals(obser)) {
                    datos = bld.toString();
                    cargarDatos(style, r, datos);

                }
                else {
                    mensajeError(r, style);
                }
                obser = "";
            }
        }
    }

    /*
     * retorna el id del parametro de la compania
     *
     * @return compania número
     */
    public String ifCero(int cn) {
        if (cn == 0) {
            return "" + unCompani() + ",";
        }
        else {
            return "";
        }
    }

    /*
     * Escribe el mensaje de error en la fila y columna deseada
     *
     */
    public void mensajeError(Row r, CellStyle style) {
        Cell cellOb = r.createCell(12);
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellOb.setCellValue(obser);
        cellOb.setCellStyle(style);
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifUno(int cn, Cell c) {
        if (cn == 1) {
            return datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifDos(int cn, Cell c) {
        if (cn == 2) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifTres(int cn, Cell c) {
        if (cn == 3) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifCuatro(int cn, Cell c) {
        if (cn == 4) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifCinco(int cn, Cell c) {
        if (cn == 5) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifSeis(int cn, Cell c) {
        if (cn == 6) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifSiete(int cn, Cell c) {
        if (cn == 7) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifOcho(int cn, Cell c) {
        if (cn == 8) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifNueve(int cn, Cell c) {
        if (cn == 9) {
            return "" + datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifDiez(int cn, Cell c) {
        if (cn == 10) {
            return datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifOnce(int cn, Cell c) {
        if (cn == 11) {
            return datosExce(c) + ",";
        }
        else {
            return "";
        }
    }

    /*
     * retorna el del formato correcto en excel
     *
     * @return dato EXCEL
     */
    public String ifDoce(int cn, Cell c) {
        if (cn == 12) {
            return datosExce(c);
        }
        else {
            return "";
        }
    }

    /*
     * carga o muesta el mensaje de error necesaio al momento de
     * insertar los datos al excel
     *
     */
    public void cargarDatos(CellStyle style, Row r, String datos) {
        try {
            String[] parametros = datos.split(",");
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), parametros[0]);
            param.put(GeneralParameterEnum.ANO.getName(), parametros[1]);
            param.put(GeneralParameterEnum.CODIGO.getName(), parametros[2]);
            param.put(GeneralParameterEnum.TERCERO.getName(), parametros[3]);
            param.put(GeneralParameterEnum.SUCURSAL.getName(), parametros[4]);
            param.put(GeneralParameterEnum.AUXILIAR.getName(), parametros[5]);
            param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                            parametros[6]);
            param.put(GeneralParameterEnum.REFERENCIA.getName(), parametros[7]);
            param.put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                            parametros[8]);
            param.put("SALDOINICIAL", parametros[9]);
            param.put("DEBITO", parametros[10]);
            param.put("CREDITO", parametros[11]);
            param.put(GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            param.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            SaldosInicialesControladorUrlEnum.URL5741
                                                            .getValue());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            param);

            Cell cellOb = r.createCell(12);
            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            cellOb.setCellValue(idioma.getString("TB_TB3079"));
            cellOb.setCellStyle(style);
        }
        catch (SystemException e) {
            obser = e.getMessage().intern();
            Cell cellOb = r.createCell(12);
            style.setFillForegroundColor(IndexedColors.RED.getIndex());
            cellOb.setCellValue(obser);
            cellOb.setCellStyle(style);
            logger.error(e.getMessage(), e);
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /*
     * Me devuelve el tipo de dato si es numero o date
     *
     * @return datos
     */
    public String casoNumerio(Cell c) {
        datos = null;
        if (DateUtil.isCellDateFormatted(c)) {
            datos = String.valueOf(c.getDateCellValue());
        }
        else {
            int dato = (int) Math.round(c.getNumericCellValue());
            datos = String.valueOf(dato);
        }
        return datos;
    }

    /*
     * convierte y lee el tipo de dato que se encuentra en la casilla
     * de excel
     *
     * @return dato
     */
    public String datosExce(Cell c) {
        datos = null;
        if (c == null) {
            // The spreadsheet is empty in this cell
        }
        else {
            switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                datos = c.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                datos = casoNumerio(c);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                datos = String.valueOf(c.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                datos = c.getCellFormula();
                break;
            default:
                datos = null;
            }

        }
        return datos;
    }

    /*
     * retorna el tipo de dato que se carga en el excel
     *
     * @return dato
     */
    public String evaluarDatosTypeDateyTipeNumeryc(Cell c) {
        if (DateUtil.isCellDateFormatted(c)) {
            return "TYPE_DATE";
        }
        else {
            return "TYPE_NUMERIC";
        }
    }

    /*
     * convierte y lee el tipo de dato que se encuentra en la casilla
     * de excel
     *
     * @return dato
     */
    public String evaluarDatosExce(Cell c) {
        datos = null;
        if (c == null) {
            // The spreadsheet is empty in this cell
        }
        else {
            switch (c.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                datos = "TYPE_STRING";
                break;
            case Cell.CELL_TYPE_NUMERIC:
                datos = evaluarDatosTypeDateyTipeNumeryc(c);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                datos = "TYPE_BOOLEAN";
                break;
            case Cell.CELL_TYPE_FORMULA:
                datos = "TYPE_FORMULA";
                break;
            default:
                datos = null;
            }

        }
        return datos;
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcActualizarHojas
     * en la vista
     *
     */
    public void ejecutarrcActualizarHojas() {
        // <CODIGO_DESARROLLADO>
        Workbook workbook = null;
        nombreHoja = "";
        String rutaArchivo = contArchivoNombreArchivoBase.getArchivo()
                        .getPath();
        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoNombreArchivoBase.getArchivo());) {

            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(fileIs);
            }
            else {
                workbook = new HSSFWorkbook(fileIs);
            }

            cargarNombreHojas(workbook);
            for (int i = 0; i < listaNombreHoja.size(); i++) {
                if (NOMBRE_HOJA.equals(listaNombreHoja.get(i)
                                .getCampos()
                                .get(GeneralParameterEnum.ANO.getName()))) {
                    nombreHoja = NOMBRE_HOJA;
                    break;
                }
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga la lista de hojas que contien el archivo Excel
     * seleccionado
     *
     * @param workbook
     */
    private void cargarNombreHojas(Workbook workbook) {
        listaNombreHoja.clear();
        int hojas = workbook.getNumberOfSheets();
        for (int i = 0; i < hojas; i++) {
            String hoja = workbook.getSheetAt(i).getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put(GeneralParameterEnum.ANO.getName(), hoja);
            listaNombreHoja.add(reg);
        }
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
     * Retorna la variable filaBase
     *
     * @return filaBase
     */
    public int getFilaBase() {
        return filaBase;
    }

    /**
     * Asigna la variable filaBase
     *
     * @param filaBase
     * Variable a asignar en filaBase
     */
    public void setFilaBase(int filaBase) {
        this.filaBase = filaBase;
    }

    /**
     * Retorna la variable nombreHoja
     *
     * @return nombreHoja
     */
    public String getNombreHoja() {
        return nombreHoja;
    }

    /**
     * Asigna la variable nombreHoja
     *
     * @param nombreHoja
     * Variable a asignar en nombreHoja
     */
    public void setNombreHoja(String nombreHoja) {
        this.nombreHoja = nombreHoja;
    }

    /**
     * Retorna el objeto contArchivoNombreArchivoBase
     *
     * @return contArchivoNombreArchivoBase
     */
    public ContenedorArchivo getContArchivoNombreArchivoBase() {
        return contArchivoNombreArchivoBase;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Asigna el objeto contArchivoNombreArchivoBase
     *
     * @param contArchivoNombreArchivoBase
     * Variable a asignar en contArchivoNombreArchivoBase
     */
    public void setContArchivoNombreArchivoBase(
        ContenedorArchivo contArchivoNombreArchivoBase) {
        this.contArchivoNombreArchivoBase = contArchivoNombreArchivoBase;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaNombreHoja
     *
     * @return listaNombreHoja
     */
    public List<Registro> getListaNombreHoja() {
        return listaNombreHoja;
    }

    /**
     * Asigna la lista listaNombreHoja
     *
     * @param listaNombreHoja
     * Variable a asignar en listaNombreHoja
     */
    public void setListaNombreHoja(List<Registro> listaNombreHoja) {
        this.listaNombreHoja = listaNombreHoja;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
