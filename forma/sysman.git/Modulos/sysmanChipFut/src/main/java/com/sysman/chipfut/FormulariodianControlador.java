/*-
 * FormulariodianControlador.java
 *
 * 1.0
 *
 * 24/03/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.FormulariodianControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Formulario que permite observar los informes contables DIAN de un
 * periodo de un anio determinado. Se accede desde la ruta Panel
 * Principal/Entes de Control/Chip-Fut/Estados Financieros/Formulario
 * anual DIAN.
 *
 * @version 1.0, 24/03/2017
 * @author lcortes
 */
@ManagedBean
@ViewScoped
public class FormulariodianControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que identifica el anio seleccionado.
     */
    private String anio;
    /**
     * Atributo que identifica el mes seleccionado.
     */
    private String mesInicial;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos CargarPlantilla y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivoCargarPlantilla;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de anios para los cuales
     */
    private List<Registro> listaAno;
    /**
     * Lista que carga los meses
     */
    private List<Registro> listaMesInicial;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FormulariodianControlador
     */
    public FormulariodianControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mesInicial = Integer.toString(SysmanFunciones
                        .mes(new Date()));
        try {
            // 1379
            numFormulario = GeneralCodigoFormaEnum.FORMULARIODIAN_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            contArchivoCargarPlantilla = new ContenedorArchivo();
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
        cargarListaMesInicial();
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
        /*
         * FR1379-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FormulariodianControladorUrlEnum.URL5542
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
     *
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FormulariodianControladorUrlEnum.URL6208
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
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Workbook workbook;
        CellStyle style;
        if (SysmanFunciones.validarVariableVacio(anio)
            || SysmanFunciones.validarVariableVacio(mesInicial)) {
            JsfUtil.agregarMensajeAlerta(
                            "Debe seleccionar el año y el mes para generar el informe.");
            return;
        }
        String campoBuscar = "FORMULAID";
        String columnaReferencia = "A";
        int filaInicial = 5;
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoCargarPlantilla.getArchivo());) {
            if (contArchivoCargarPlantilla.getArchivo() == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB856"));
                return;
            }
            workbook = new HSSFWorkbook(fileIs);
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

            /*
             * Cell cellMes = sheet.getRow(6).createCell(35);
             * cellMes.setCellValue(mesInicial);
             * cellMes.setCellStyle(style);
             */

            String strSql1 = "SELECT PLAN_CONTABLE.CODIGO FORMULAID,\n"
                + "       ''                   FORMULA,\n"
                + "       ''                   FORMULA1,\n"
                + "       ''                   FORMULA2,\n"
                + "       ''                   FORMULA3,\n"
                + "       ''                   FORMULA4,\n"
                + "       ''                   FORMULA5,\n"
                + "       ''                   FORMULA6,\n"
                + "       ''                   FORMULA7,\n"
                + "       ''                   FORMULA8,\n"
                + "       ''                   FORMULA9,\n"
                + "       ''                   FORMULA10,\n"
                + "       ''                   FORMULA11,\n"
                + "       PLAN_CONTABLE.SALDO3 VALOR\n"
                + "  FROM PLAN_CONTABLE\n"
                + " WHERE PLAN_CONTABLE.COMPANIA = '" + compania + "'  \n"
                + "   AND PLAN_CONTABLE.ANO      = " + anio
                + " ORDER BY PLAN_CONTABLE.CODIGO";

            Sheet sheet = workbook.getSheet("1732-2");
            prepararInforme(sheet, strSql1, "1732-2", filaInicial,
                            columnaReferencia, campoBuscar);

            /*
             * String strSql2 =
             * "SELECT PLAN_CONTABLE.CODIGO FORMULAID,\n" +
             * "       ''                   FORMULA,\n" +
             * "       ''                   FORMULA1,\n" +
             * "       ''                   FORMULA2,\n" +
             * "       ''                   FORMULA3,\n" +
             * "       ''                   FORMULA4,\n" +
             * "       ''                   FORMULA5,\n" +
             * "       ''                   FORMULA6,\n" +
             * "       ''                   FORMULA7,\n" +
             * "       ''                   FORMULA8,\n" +
             * "       ''                   FORMULA9,\n" +
             * "       ''                   FORMULA10,\n" +
             * "       ''                   FORMULA11,\n" +
             * "       PLAN_CONTABLE.SALDO3 VALOR\n" +
             * "  FROM PLAN_CONTABLE\n" +
             * " WHERE PLAN_CONTABLE.COMPANIA = '" + compania +
             * "'  \n" + "   AND PLAN_CONTABLE.ANO      = " + anio +
             * " ORDER BY PLAN_CONTABLE.CODIGO";
             */
            /*
             * prepararInforme(sheet, strSql2, "Formulario", true,
             * "CD", "FORMULAID", "A"); String strSql3 =
             * " SELECT DETALLE_COMPROBANTE_CNT.CUENTA AS FORMULAID, \n"
             * +
             * "SUM(NVL(DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,0)) AS VALOR \n"
             * + "FROM PLAN_CONTABLE \n" +
             * "  INNER JOIN DETALLE_COMPROBANTE_CNT \n" +
             * "  ON  PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA \n"
             * +
             * "  AND PLAN_CONTABLE.ANO      = DETALLE_COMPROBANTE_CNT.ANO \n"
             * +
             * "  AND PLAN_CONTABLE.CODIGO   = DETALLE_COMPROBANTE_CNT.CUENTA \n"
             * + "  INNER JOIN TIPO_COMPROBANTE \n" +
             * "  ON  DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA \n"
             * +
             * "  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO \n"
             * + "WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = '" +
             * compania + "' \n" +
             * "  AND DETALLE_COMPROBANTE_CNT.ANO      = " + anio +
             * " \n" + "  AND DETALLE_COMPROBANTE_CNT.MES      = " +
             * mesInicial + " \n " +
             * "  AND PLAN_CONTABLE.CLASECUENTA        IN ('I') \n" +
             * "  AND TIPO_COMPROBANTE.CLASE_CONTABLE  NOT IN('Z','E','G') \n"
             * + "GROUP BY DETALLE_COMPROBANTE_CNT.CUENTA \n" +
             * "ORDER BY DETALLE_COMPROBANTE_CNT.CUENTA ";
             * prepararInforme(sheet, strSql3, "Formulario", true,
             * "CE", "FORMULAID", "BO");
             */
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Formulario retencion 350-2014.xls");
        }
        catch (FileNotFoundException e) {
            Logger.getLogger(FormulariodianControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (IOException | JRException ex) {
            Logger.getLogger(FormulariodianControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean prepararInforme(Sheet sheet, String strSql,
        String strnombreHoja, int filaInicial, String strcolumnareferencia,
        String strCampoBuscar) {
        List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        strSql);
        if (rs.isEmpty()) {
            return false;
        }
        envioDetalle(sheet, rs, filaInicial, strcolumnareferencia,
                        strCampoBuscar);
        return true;
    }

    private void envioDetalle(Sheet sheet, List<Registro> rs,
        int intfilaInicial,
        String strcolumnareferencia, String strCampoBuscar) {
        String signo1 = null;
        String signo2 = null;

        for (Registro registro : rs) {
            signo1 = "+";
            signo2 = "-";
            String busqueda = signo1 + registro.getCampos().get(strCampoBuscar)
                + signo2;
            int column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
            Cell cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            if (cell != null) {
                /*
                 * int numColumn = CellRangeAddress.valueOf( "" +
                 * numColumna + ":" + numColumna + "")
                 * .getFirstColumn(); double aux =
                 * sheet.getRow(cell.getRowIndex()).getCell(numColumn)
                 * .getNumericCellValue() + ((BigDecimal)
                 * registro.getCampos().get("VALOR")) .doubleValue();
                 * Cell newCell = sheet.getRow(cell.getRowIndex())
                 * .createCell(numColumn);
                 * newCell.setCellStyle(style);
                 * newCell.setCellValue(aux);
                 */
            }
            signo1 = "+";
            signo2 = "+";
            busqueda = signo1 + registro.getCampos().get(strCampoBuscar)
                + signo2;
            column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
            cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            if (cell != null) {
                /*
                 * int numColumn = CellRangeAddress.valueOf( "" +
                 * numColumna + ":" + numColumna + "")
                 * .getFirstColumn(); double aux =
                 * sheet.getRow(cell.getRowIndex()).getCell(numColumn)
                 * .getNumericCellValue() + ((BigDecimal)
                 * registro.getCampos().get("VALOR")) .doubleValue();
                 * Cell newCell = sheet.getRow(cell.getRowIndex())
                 * .createCell(numColumn);
                 * newCell.setCellStyle(style);
                 * newCell.setCellValue(aux);
                 */
            }
            signo1 = "-";
            signo2 = "+";
            busqueda = signo1 + registro.getCampos().get(strCampoBuscar)
                + signo2;
            column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
            cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            if (cell != null) {
                /*
                 * int numColumn = CellRangeAddress.valueOf( "" +
                 * numColumna + ":" + numColumna + "")
                 * .getFirstColumn(); double aux =
                 * sheet.getRow(cell.getRowIndex()).getCell(numColumn)
                 * .getNumericCellValue() + ((BigDecimal)
                 * registro.getCampos().get("VALOR")) .doubleValue();
                 * Cell newCell = sheet.getRow(cell.getRowIndex())
                 * .createCell(numColumn);
                 * newCell.setCellStyle(style);
                 * newCell.setCellValue(aux);
                 */
            }
            signo1 = "-";
            signo2 = "-";
            busqueda = signo1 + registro.getCampos().get(strCampoBuscar)
                + signo2;
            column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
            cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            if (cell != null) {
                /*
                 * int numColumn = CellRangeAddress.valueOf( "" +
                 * numColumna + ":" + numColumna + "")
                 * .getFirstColumn(); double aux =
                 * sheet.getRow(cell.getRowIndex()).getCell(numColumn)
                 * .getNumericCellValue() + ((BigDecimal)
                 * registro.getCampos().get("VALOR")) .doubleValue();
                 * Cell newCell = sheet.getRow(cell.getRowIndex())
                 * .createCell(numColumn);
                 * newCell.setCellStyle(style);
                 * newCell.setCellValue(aux);
                 */
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
        // <CODIGO_DESARROLLADO>
        mesInicial = null;
        cargarListaMesInicial();
        // </CODIGO_DESARROLLADO>
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
     * Retorna la variable mesInicial
     *
     * @return mesInicial
     */
    public String getMesInicial() {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     *
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoCargarPlantilla
     *
     * @return contArchivoCargarPlantilla
     */
    public ContenedorArchivo getContArchivoCargarPlantilla() {
        return contArchivoCargarPlantilla;
    }

    /**
     * Asigna el objeto contArchivoCargarPlantilla
     *
     * @param contArchivoCargarPlantilla
     * Variable a asignar en contArchivoCargarPlantilla
     */
    public void setContArchivoCargarPlantilla(
        ContenedorArchivo contArchivoCargarPlantilla) {
        this.contArchivoCargarPlantilla = contArchivoCargarPlantilla;
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
     * Retorna la lista listaMesInicial
     *
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial() {
        return listaMesInicial;
    }

    /**
     * Asigna la lista listaMesInicial
     *
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial) {
        this.listaMesInicial = listaMesInicial;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
