package com.sysman.bancoproyectos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 24/05/2016
 * 
 * @modifier amonroy
 * @version 2, 27/09/2017 Revision de buenas practicas sugeridas por
 * la herramienta SonarLint
 */
@ManagedBean
@ViewScoped
public class InformeTransparenciaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    @SuppressWarnings("unused")
    private final String compania;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos seleccionaExcel y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivoseleccionaExcel;

    /**
     * Crea una nueva instancia de InformeTransparenciaControlador
     */
    public InformeTransparenciaControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_TRANSPARENCIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InformeTransparenciaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            contArchivoseleccionaExcel = new ContenedorArchivo();
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
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo en el que se verifica que ha sido seleccionado una
     * plantilla dentro del formulario
     * 
     * @return si ha sido seleccionado alguna plantilla
     */
    private boolean oprimirExcelCondicion() {
        boolean respuesta = true;
        if (contArchivoseleccionaExcel.getArchivo() == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            respuesta = false;
        }
        return respuesta;
    }

    /**
     * Adiciona la informacion resultante de la consulta
     * 800050informeTransparencia la plantilla que ha sido cargada,
     * teniendo en cuenta la posicion de la
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (!oprimirExcelCondicion()) {
            return;
        }

        String rutaArchivo = contArchivoseleccionaExcel.getArchivo()
                        .getPath();
        String extension = rutaArchivo.substring(
                        rutaArchivo.indexOf('.'), rutaArchivo.length());

        try (FileInputStream file = new FileInputStream(
                        new File(rutaArchivo))) {

            Workbook workbook = null;

            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(file);
            }
            else {
                workbook = new HSSFWorkbook(file);
            }

            Sheet sheet = workbook.getSheetAt(0);

            CellStyle style = workbook.createCellStyle();
            CellStyle stylePorc = workbook.createCellStyle();
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setAlignment(CellStyle.ALIGN_RIGHT);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

            stylePorc.setBorderTop(CellStyle.BORDER_THIN);
            stylePorc.setTopBorderColor(IndexedColors.BLACK.getIndex());
            stylePorc.setBorderLeft(CellStyle.BORDER_THIN);
            stylePorc.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            stylePorc.setBorderRight(CellStyle.BORDER_THIN);
            stylePorc.setRightBorderColor(IndexedColors.BLACK.getIndex());
            stylePorc.setAlignment(CellStyle.ALIGN_RIGHT);
            stylePorc.setBorderBottom(CellStyle.BORDER_THIN);
            stylePorc.setBottomBorderColor(IndexedColors.BLACK.getIndex());

            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            Map<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("fechaInicial", fechaIni);
            reemplazos.put("fechaFinal", fechaFin);
            String strSql = Reporteador.resuelveConsulta(
                            "800050informeTransparencia",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos);
            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);

            int rowNum = 0;
            Cell start = searchColumnSheet("FICHA EBI", 0, 0,
                            sheet.getLastRowNum(), sheet);
            if (start != null) {
                rowNum = start.getRowIndex() + 2;
                if (!rs.isEmpty()) {
                    for (int i = 0; i < rs.size(); i++) {
                        Row row = sheet.createRow(rowNum);
                        Cell nCell = row.createCell(0);
                        nCell.setCellValue(rs.get(i).getCampos().get("FICHAEBI")
                                        .toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(1);
                        nCell.setCellValue(rs.get(i).getCampos()
                                        .get("NOMBREPROYECTO").toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(2);
                        nCell.setCellValue(Double.parseDouble(
                                        rs.get(i).getCampos().get("VALORTOTAL")
                                                        .toString()));
                        style.setDataFormat(workbook.createDataFormat()
                                        .getFormat("$ #,##0.00"));
                        nCell.setCellStyle(style);

                        nCell = row.createCell(3);
                        nCell.setCellValue(rs.get(i).getCampos()
                                        .get("FECHAREGISTRO").toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(4);
                        nCell.setCellValue(rs.get(i).getCampos()
                                        .get("VIGENCIAFIN").toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(5);
                        nCell.setCellValue(
                                        rs.get(i).getCampos()
                                                        .get("OBJETO") == null
                                                            ? " "
                                                            : rs.get(i).getCampos()
                                                                            .get("OBJETO")
                                                                            .toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(6);
                        nCell.setCellValue(rs.get(i).getCampos()
                                        .get("PRODUCTO") == null ? " "
                                            : rs.get(i).getCampos()
                                                            .get("PRODUCTO")
                                                            .toString());
                        nCell.setCellStyle(stylePorc);

                        nCell = row.createCell(7);
                        nCell.setCellValue(
                                        rs.get(i).getCampos().get("RAD") == null
                                            ? " "
                                            : rs.get(i).getCampos().get("RAD")
                                                            .toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(8);
                        nCell.setCellValue(
                                        rs.get(i).getCampos().get("REG") == null
                                            ? " "
                                            : rs.get(i).getCampos().get("REG")
                                                            .toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(9);
                        nCell.setCellValue(
                                        rs.get(i).getCampos().get("PRI") == null
                                            ? " "
                                            : rs.get(i).getCampos().get("PRI")
                                                            .toString());
                        nCell.setCellStyle(style);

                        nCell = row.createCell(10);
                        nCell.setCellValue(rs.get(i).getCampos().get("NOMBRE")
                                        .toString());
                        nCell.setCellStyle(style);

                        rowNum++;
                    }
                    workbook.setForceFormulaRecalculation(true);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    workbook.write(out);
                    out.close();
                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    new ByteArrayInputStream(out.toByteArray()),
                                    SysmanFunciones.concatenar(
                                                    "Informe Transparencia -",
                                                    String.valueOf(SysmanFunciones
                                                                    .ano(fechaInicial)),
                                                    extension));
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2361"));
                }
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3739"));
            }
            workbook.close();

        }
        catch (IOException | JRException | ParseException e) {
            Logger.getLogger(InformeContraloriaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Busca un texto especifico dentro de una hoja de datos
     * 
     * @param searchText
     * Palabra a buscar
     * @param column
     * Numero de Columna en la que se realizara la busqueda
     * @param rowStart
     * Fila de inicio a recorrer en la columna indicada
     * @param rowEnd
     * Fila final a recorrer en la columna indicada
     * @param sheet
     * Hoja de datos a analizar
     * @return La celda en la que se encuentra el texto indicado en el
     * parametro <strong>searchText<strong>
     */
    public Cell searchColumnSheet(String searchText, int column,
        int rowStart, int rowEnd, Sheet sheet) {
        Cell c = null;
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
            if ((sheet.getRow(rowNum) != null)
                && (sheet.getRow(rowNum).getCell(column) != null)) {
                Row r = sheet.getRow(rowNum);

                if (r.getCell(column, Row.RETURN_BLANK_AS_NULL) == null
                    || c != null) {
                    continue;
                }
                switch (r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                .getCellType()) {
                case HSSFCell.CELL_TYPE_STRING:
                    c = caseCeldaString(searchText, column, r);
                    break;
                case HSSFCell.CELL_TYPE_FORMULA:
                    c = caseCeldaFormula(searchText, column, r);
                    break;
                case HSSFCell.CELL_TYPE_NUMERIC:
                    c = caseCeldaNumeric(searchText, column, r);
                    break;
                default:
                    c = caseDefault(searchText, column, r);
                    break;
                }
            }
        }
        return c;
    }

    /**
     *
     * Define el condicional para el caso "HSSFCell.CELL_TYPE_STRING"
     * del switch dentro del metodo searchColumnSheet()
     *
     * @param searchText
     * Texo a buscar
     * @param column
     * Numero de la columna
     * @param r
     * Numero de la fila
     * @return La celda si posee en valor requerido
     */
    private Cell caseCeldaString(String searchText, int column, Row r) {
        Cell c = null;
        if ((searchText != null)
            && r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                            .getStringCellValue()
                            .contains(searchText)) {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
        }
        return c;
    }

    /**
     * Define el condicional para el caso "HSSFCell.CELL_TYPE_FORMULA"
     * del switch dentro del metodo searchColumnSheet()
     * 
     * @param searchText
     * Texo a buscar
     * @param column
     * Numero de la columna
     * @param r
     * Numero de la fila
     * @return La celda si posee en valor requerido
     */
    private Cell caseCeldaFormula(String searchText, int column, Row r) {
        Cell c = null;
        if ((searchText != null) && r
                        .getCell(column, Row.RETURN_BLANK_AS_NULL)
                        .getCellFormula().contains(searchText)) {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
            return c;
        }
        return c;
    }

    /**
     *
     * Define el condicional para el caso "HSSFCell.CELL_TYPE_NUMERIC"
     * del switch dentro del metodo searchColumnSheet()
     *
     * @param searchText
     * Texo a buscar
     * @param column
     * Numero de la columna
     * @param r
     * Numero de la fila
     * @return La celda si posee en valor requerido
     */
    private Cell caseCeldaNumeric(String searchText, int column, Row r) {
        Cell c = null;
        if ((searchText != null)
            && searchText.equals(String
                            .valueOf(r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                            .getNumericCellValue()))) {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
        }
        return c;
    }

    /**
     * Define el condicional para el caso "default" del switch dentro
     * del metodo searchColumnSheet()
     * 
     * @param searchText
     * Texo a buscar
     * @param column
     * Numero de la columna
     * @param r
     * Numero de la fila
     * @return La celda si posee en valor requerido
     */
    private Cell caseDefault(String searchText, int column, Row r) {
        Cell c = null;
        if ((searchText != null) && searchText.equals(
                        r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                        .getStringCellValue())) {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
            return c;
        }
        return c;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoseleccionaExcel() {
        return contArchivoseleccionaExcel;
    }

    public void setContArchivoseleccionaExcel(
        ContenedorArchivo contArchivoseleccionaExcel) {
        this.contArchivoseleccionaExcel = contArchivoseleccionaExcel;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}