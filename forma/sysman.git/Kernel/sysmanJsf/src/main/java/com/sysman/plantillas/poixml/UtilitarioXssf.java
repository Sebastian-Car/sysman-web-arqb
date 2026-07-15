/*-
 * UtilitarioXssf.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plantillas.poixml;

import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.commons.util.StringUtility;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plantillas.enums.PlantillasEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Funcionalidades para trabajar con documentos de hojas de
 * c&aacute;lculo.
 * 
 * @version 1.0, 18/01/2018
 * @author jrodrigueza
 *
 */
public class UtilitarioXssf extends UtilitarioPoiXml {

    /**
     * Lista de referencias a celdas que contienen variables de tabla
     */
    private List<String> referencias;
    /**
     * N&uacute;mero de registro actual. Permite identificar el
     * registro que se est&aacute; procesando.
     */
    private int numeroRegistro;

    /**
     * @param service
     * servicio
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla
     * @param fechaPlantilla
     * cadena que representa la fecha de la plantilla con formato
     * Oracle.
     * @param variablesModelo
     * @param variablesConsulta
     * variables de reemplazo para la consulta
     */
    public UtilitarioXssf(String codigoPlantilla,
        String fechaPlantilla, Map<String, String> variablesModelo,
        Map<String, String> variablesConsulta) {
        super(codigoPlantilla, fechaPlantilla, variablesModelo,
                        variablesConsulta, null);
        referencias = new ArrayList<>();
    }

    /**
     * @param service
     * servicio
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla
     * @param fechaPlantilla
     * cadena que representa la fecha de la plantilla con formato
     * Oracle.
     * @param variablesModelo
     * @param variablesConsulta
     * variables de reemplazo para la consulta
     * @param datosSesion
     * datos de sesi&oacute;n en caso de que no se pueda usar
     * variables de sesi&oacute;n.
     */
    public UtilitarioXssf(String codigoPlantilla,
        String fechaPlantilla, Map<String, String> variablesModelo,
        Map<String, String> variablesConsulta, DatosSesion datosSesion) {
        super(codigoPlantilla, fechaPlantilla, variablesModelo,
                        variablesConsulta, datosSesion);
        referencias = new ArrayList<>();
    }

    /**
     * exporta plantilla en MS Excel 2007
     * 
     * @return documento plantilla en MS Excel
     * @throws SystemException
     * en caso de que se presente alg&uacute;n problema al exportar el
     * comprimido con los documentos generados.
     */
    public StreamedContent exportarPlantilla() throws SystemException {
        try {
            Map<String, Object> map = generarPlantilla();
            ByteArrayInputStream[] salidas = (ByteArrayInputStream[]) map
                            .get(PlantillasEnum.STR_SALIDAS.getValue());
            String[] nombresArchivos = (String[]) map.get(
                            PlantillasEnum.STR_NOMBRES_ARCHIVOS.getValue());
            return JsfUtil.exportarComprimidoGeneralStreamed(salidas,
                            nombresArchivos);
        }
        catch (JRException | IOException | SQLException | DRException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Exporta el arreglo de bytes con el documento generado a partir
     * de la plantilla.
     * 
     * @return areglo de bytes del documento
     * @throws SystemException
     * en caso de que se presente alg&uacute;n problema al exportar el
     * documento en MS Excel.
     */
    public byte[] serializarPlantilla() throws SystemException {
        Map<String, Object> map = generarPlantilla();
        ByteArrayInputStream[] salidas = (ByteArrayInputStream[]) map
                        .get(PlantillasEnum.STR_SALIDAS.getValue());
        ByteArrayInputStream arrayInputStream = salidas[0];
        try {
            return IOUtils.toByteArray(arrayInputStream);
        }
        catch (IOException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Recuperando los datos configurados a la plantilla realiza los
     * reemplazos de variables de plantilla y de tabla.
     * 
     * @return map Objeto clave/valor que contiene los vectores con lo
     * siguiente:
     * <ul>
     * <li><i>salidas</i> => arreglo de bytes con los documentos
     * generados de tipo <code>ByteArrayInputStream</code>.</li>
     * <li><i>nombresArchivos</i> => arreglo con los nombres de cada
     * documento respectivamente de tipo <code>String</code>.</li>
     * </ul>
     * @throws SystemException
     * en caso de que no exista informaci&oacute;n al resolver la
     * consulta de platnilla.
     */
    private Map<String, Object> generarPlantilla() throws SystemException {
        if (!datosValidos()) {
            throw new SystemException(idioma.getString(
                            "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        }
        numeroRegistro = 0;
        int totalRegistros = datos.size();
        String[] nombresArchivos = new String[totalRegistros];
        ByteArrayInputStream[] salidas = new ByteArrayInputStream[totalRegistros];
        String nombrePlantilla = SysmanFunciones.toString(modeloPlantilla
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        String archivoPlantilla = SysmanFunciones
                        .toString(modeloPlantilla
                                        .get(PlantillasEnum.PLANTILLA
                                                        .getValue()));
        String extension = FilenameUtils.getExtension(archivoPlantilla);
        while (numeroRegistro < totalRegistros) {
            try (FileInputStream fileInputStream = new FileInputStream(
                            getArchivoPlantilla());
                            Workbook wb = new XSSFWorkbook(fileInputStream);) {
                reemplazarVariablesConsulta(wb);
                if (!variablesTabla.isEmpty()) {
                    buscarVariablesTabla(wb);
                    reemplazarVariablesTabla(wb);
                }
                recalcularFormulas(wb);
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                wb.write(arrayOutputStream);
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
                                arrayOutputStream.toByteArray());
                nombresArchivos[numeroRegistro] = nombrePlantilla + "-"
                    + numeroRegistro + "." + extension;
                salidas[numeroRegistro] = arrayInputStream;
            }
            catch (IOException | SysmanException e) {
                throw new SystemException(e);
            }
            numeroRegistro++;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(PlantillasEnum.STR_SALIDAS.getValue(), salidas);
        map.put(PlantillasEnum.STR_NOMBRES_ARCHIVOS.getValue(),
                        nombresArchivos);
        return map;
    }

    /**
     * Recalcula las formulas existentes en la hoja de c&aacute;lculo;
     * 
     * @param wb
     * libro de trabajo
     */
    public static void recalcularFormulas(Workbook wb) {
        FormulaEvaluator evaluator = wb.getCreationHelper()
                        .createFormulaEvaluator();
        evaluator.evaluateAll();
    }

    private void reemplazarVariablesTabla(Workbook wb) throws SysmanException {
        List<Registro> tuplas;
        for (String referencia : referencias) {
            String[] celda = referencia.split(";");
            String sheetName = celda[0];
            int rowIndex = Integer.parseInt(celda[1]);
            int columnIndex = Integer.parseInt(celda[2]);
            Sheet sheet = wb.getSheet(sheetName);
            Row row = sheet.getRow(rowIndex);
            Cell cell = row.getCell(columnIndex);
            Map<String, Object> datosTabla = traerDatosTabla(
                            cell.getStringCellValue());
            String consulta = (String) datosTabla
                            .get(PlantillasEnum.CONSULTA.getValue());
            char modoCreacion = (char) datosTabla
                            .get(PlantillasEnum.MODO_CREACION.getValue());
            boolean manejaEstilos = (boolean) datosTabla
                            .get(PlantillasEnum.MANEJA_ESTILOS.getValue());
            cell.setCellValue("");
            if (consulta != null) {
                tuplas = service.getListado(
                                ConectorPool.ESQUEMA_SYSMAN, consulta);
                if (tuplas != null && !tuplas.isEmpty()) {
                    List<String> columnas = service.getCamposListado(
                                    ConectorPool.ESQUEMA_SYSMAN, consulta);
                    int firstRowNum = rowIndex;
                    if ('D' == modoCreacion) {
                        crearEncabezadoTabla(row, columnIndex, columnas,
                                        manejaEstilos);
                        firstRowNum++;
                    }
                    // desplazamiento de filas segun numero registros
                    if (Arrays.asList('D', 'C').contains(modoCreacion)
                        && sheet.getLastRowNum() > 0) {
                        sheet.shiftRows(firstRowNum + 1, sheet.getLastRowNum(),
                                        tuplas.size());
                    }
                    if ('P' == modoCreacion) {
                        poblarTablaExistente(sheet, firstRowNum, columnIndex,
                                        tuplas, columnas, manejaEstilos);
                    }
                    else {
                        crearCuerpoTabla(sheet, firstRowNum, columnIndex,
                                        tuplas,
                                        columnas, manejaEstilos);
                    }
                }
                else {
                    // Si no hay datos para mostrar en la tabla
                    cell.setCellValue("");
                }
            }
        }
    }

    /**
     * Crea el encabezado de la tabla
     * 
     * @param row
     * fila en la que se debe pintar el encabezado
     * @param columnIndex
     * n&uacute;mero de columna
     * @param columnas
     * nombre de los campos a traer
     * @param manejaEstilos
     */
    private void crearEncabezadoTabla(Row row, int columnIndex,
        List<String> columnas, boolean manejaEstilos) {
        for (int i = 0; i < columnas.size(); i++) {
            Cell titleCell = row.createCell(columnIndex + i);
            titleCell.setCellValue(columnas.get(i));
            if (manejaEstilos) {
                CellStyle style = crearEstiloTitulo(
                                row.getSheet().getWorkbook());
                titleCell.setCellStyle(style);
            }
        }
    }

    /**
     * Crea el cuerpo de la tabla
     * 
     * @param sheet
     * hoja de trabajo
     * @param firstRowNum
     * fila inicial
     * @param columnIndex
     * columna inicial
     * @param tuplas
     * registros que contienen los datos d ela tabla
     * @param columnas
     * nombre de los campos a traer
     * @param manejaEstilos
     */
    private void crearCuerpoTabla(Sheet sheet, int firstRowNum, int columnIndex,
        List<Registro> tuplas, List<String> columnas, boolean manejaEstilos) {
        for (int i = 0; i < tuplas.size(); i++) {
            Row newRow = sheet.getRow(firstRowNum + i);
            if (newRow == null) {
                newRow = sheet.createRow(firstRowNum + i);
            }
            Map<String, Object> campos = tuplas.get(i).getCampos();
            for (int j = 0; j < columnas.size(); j++) {
                Object valorCelda = campos.get(columnas.get(j));
                Cell newCell = newRow.getCell(columnIndex + j);
                if (newCell == null) {
                    newCell = newRow.createCell(
                                    columnIndex + j);
                }
                asignarValorCelda(newCell, valorCelda);
                if (manejaEstilos) {
                    newCell.setCellStyle(
                                    crearEstiloCuerpo(sheet.getWorkbook()));
                }
            }

        }
    }

    /**
     * Recorre una tabla existente e inserta cada uno de las
     * registros. En caso de que la hayan mas registros que filas
     * disponibles en la tabla, se detiene la inserción.
     * 
     * @param sheet
     * hoja de trabajo
     * @param firstRowNum
     * fila inicial
     * @param columnIndex
     * columna inicial
     * @param tuplas
     * registros que contienen los datos d ela tabla
     * @param columnas
     * nombre de los campos a traer
     * @param manejaEstilos
     */
    private void poblarTablaExistente(Sheet sheet, int firstRowNum,
        int columnIndex,
        List<Registro> tuplas, List<String> columnas, boolean manejaEstilos) {
        List<CellRangeAddress> regionsList = new ArrayList<>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            regionsList.add(sheet.getMergedRegion(i));
        }
        int ultimaFila = 0;
        int incrementoFila = 0;
        int ultimaColumna = 0;
        boolean sinEspacioDeTabla = false;
        sheet.getRow(firstRowNum).getCell(columnIndex).setCellValue("");
        for (int i = 0; i < tuplas.size(); i++) {
            if (sinEspacioDeTabla) {
                break;
            }
            Row newRow = sheet.getRow(firstRowNum + incrementoFila);
            if (newRow == null) {
                newRow = sheet.createRow(firstRowNum + incrementoFila);
            }
            Map<String, Object> campos = tuplas.get(i).getCampos();
            for (int j = 0; j < columnas.size(); j++) {
                Object valorCelda = campos.get(columnas.get(j));
                Cell newCell = newRow.getCell(columnIndex + ultimaColumna);
                if (newCell == null) {
                    newCell = newRow.createCell(
                                    columnIndex + ultimaColumna);
                }
                if (j > 0 && Cell.CELL_TYPE_BLANK != newCell.getCellType()) {
                    sinEspacioDeTabla = true;
                    break;
                }
                asignarValorCelda(newCell, valorCelda);
                if (manejaEstilos) {
                    newCell.setCellStyle(
                                    crearEstiloCuerpo(sheet.getWorkbook()));
                }
                // sin celdas combinadas en el lugar de la tabla
                ultimaFila = firstRowNum + incrementoFila;
                for (CellRangeAddress region : regionsList) {
                    if (region.isInRange(newCell.getRowIndex(),
                                    newCell.getColumnIndex())) {
                        // si hay celdas combinadas
                        ultimaFila = region.getLastRow();
                        ultimaColumna = region.getLastColumn();
                    }
                }
                ultimaColumna++;
            }
            ultimaFila++;
            incrementoFila = ultimaFila - firstRowNum;
            ultimaColumna = 0;
        }
    }

    /**
     * Asigna valor a una celda dependiento del tipo de objeto pasado
     * por par&aacute;metro
     * 
     * @param cell
     * celda a la que se le va a dar valor
     * @param value
     * valor que tomar&aacute; la celda
     */
    public static void asignarValorCelda(Cell cell, Object value) {
        if (value instanceof String) {
            String string = SysmanFunciones
                            .nvlStr(SysmanFunciones.toString(value), "");
            cell.setCellValue(string);
        }
        else if (value instanceof Boolean) {
            cell.setCellValue((boolean) value);
        }
        else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        else if (value instanceof Number) {
            Double decimal = ((Number) value).doubleValue();
            cell.setCellValue(decimal);
        }
        else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        }
    }

    /**
     * 
     * @param wb
     * @return
     */
    private CellStyle crearEstiloTitulo(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        crearEstiloBordes(style);
        return style;
    }

    /**
     * 
     * @param wb
     * @return
     */
    private CellStyle crearEstiloCuerpo(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        crearEstiloBordes(style);
        return style;
    }

    private void crearEstiloBordes(CellStyle style) {
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        ((XSSFCellStyle) style).setBottomBorderColor(
                        IndexedColors.GREY_40_PERCENT.getIndex());
        ((XSSFCellStyle) style).setTopBorderColor(
                        IndexedColors.GREY_40_PERCENT.getIndex());
        ((XSSFCellStyle) style).setLeftBorderColor(
                        IndexedColors.GREY_40_PERCENT.getIndex());
        ((XSSFCellStyle) style).setRightBorderColor(
                        IndexedColors.GREY_40_PERCENT.getIndex());
    }

    /**
     * Trae los datos asociados a la variable de tabla
     * 
     * @param variableTabla
     * nombre que identifica la tabla
     * @return map con los datos de la tabla
     * @throws SysmanException
     * en caso de que ocurra un error al ejecutar la consulta SQL.
     */
    private Map<String, Object> traerDatosTabla(String variableTabla)
                    throws SysmanException {
        for (Registro variable : variablesTabla) {
            Map<String, Object> campos = variable.getCampos();
            String nombre = SysmanFunciones.toString(
                            campos.get(GeneralParameterEnum.NOMBRE.getName()));
            if (variableTabla.equals("<#" + nombre + "#>")) {
                String consulta = armarConsulta(campos);
                char modoCreacion = SysmanFunciones
                                .toString(campos.get(
                                                PlantillasEnum.MODO_CREACION
                                                                .getValue()))
                                .charAt(0);

                String cadena = SysmanFunciones.toString(
                                campos.get(PlantillasEnum.MANEJA_ESTILOS
                                                .getValue()));

                boolean manejaEstilos;
                if (StringUtility.isBoolean(cadena)) {
                    manejaEstilos = (boolean) campos.get(
                                    PlantillasEnum.MANEJA_ESTILOS.getValue());
                }
                else {
                    int numero = Integer.parseInt(cadena);
                    manejaEstilos = numero != 0;
                }

                Map<String, Object> datosTabla = new HashMap<>();
                datosTabla.put(PlantillasEnum.CONSULTA.getValue(),
                                consulta);
                datosTabla.put(PlantillasEnum.MODO_CREACION.getValue(),
                                modoCreacion);
                datosTabla.put(PlantillasEnum.MANEJA_ESTILOS.getValue(),
                                manejaEstilos);
                return datosTabla;
            }
        }
        return null;
    }

    /**
     * Construye la consulta de tabla por medio del enlace prinicpal,
     * enlace secundario y condici&oacute;n enlace.
     * 
     * @param campos
     * campos de los que se compone la variable de tabla
     * @return consulta v&aacute;lida
     * @throws SysmanException
     * en caso de las columnas de los enlaces no sean v&aacute;lidos.
     */
    private String armarConsulta(Map<String, Object> campos)
                    throws SysmanException {
        String consulta = SysmanFunciones.toString(
                        campos.get(PlantillasEnum.CONSULTA.getValue()));
        consulta = Reporteador.reemplazaSql(consulta);
        String aux = SysmanFunciones.toString(
                        campos.get(PlantillasEnum.ENLACE_PRINCIPAL.getValue()));
        String[] enlacePrincipal = aux != null ? aux.split(",") : null;
        aux = SysmanFunciones.toString(campos
                        .get(PlantillasEnum.ENLACE_SECUNDARIO.getValue()));
        String[] enlaceSecundario = aux != null ? aux.split(",") : null;
        StringBuilder condicion = new StringBuilder();
        int length = enlacePrincipal != null ? enlacePrincipal.length : 0;
        for (int i = 0; i < length; i++) {
            String columnaSecundaria = enlaceSecundario[i];
            String columnaPrincipal = enlacePrincipal[i];
            Map<String, Object> camposDatos = datos.get(numeroRegistro)
                            .getCampos();
            Object valor = camposDatos.get(columnaPrincipal);
            if (valor != null) {
                condicion.append((i > 0 ? "AND " : "") + columnaSecundaria
                    + "='" + valor + "' \n");
            }
            else {
                String variableConsulta = SysmanFunciones.toString(campos
                                .get(GeneralParameterEnum.NOMBRE.getName()));
                String mensaje = idioma.getString("TB_TB4112");
                mensaje = mensaje.replace("s$columnaPrincipal$s",
                                columnaPrincipal);
                mensaje = mensaje.replace("s$variableConsulta$s",
                                variableConsulta);
                throw new SysmanException(mensaje);
            }
        }
        consulta = consulta.replace("s$CONDICIONENLACE$s",
                        condicion.toString());
        String condicionUsuario = SysmanFunciones
                        .toString(campos.get(
                                        PlantillasEnum.CONDICION.getValue()));
        if (condicionUsuario != null) {
            condicionUsuario = Reporteador.reemplazaSql(condicionUsuario);
            consulta = consulta.replace("s$CONDICIONUSUARIO$s",
                            condicionUsuario);
        }
        else {
            consulta = consulta.replace("s$CONDICIONUSUARIO$s", "");
        }
        consulta = Reporteador.reemplazarInicial(variablesReemplazoConsulta,
                        consulta);
        return consulta;
    }

    /**
     * reemplazo de las variables de usuario de tipo consulta en la
     * plantilla
     * 
     * @param wb
     * libro de trabajo
     */
    private void reemplazarVariablesConsulta(Workbook wb) {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            for (Row row : sheet) {
                for (Cell cell : row) {
                    reemplazarVariablesConsulta(cell);
                }
            }
        }
    }

    /**
     * asigna el valor final de la celda
     * 
     * @param cell
     * celda a evaluar
     */
    private void reemplazarVariablesConsulta(Cell cell) {
        if (cell != null
            && Cell.CELL_TYPE_BLANK != cell.getCellType()) {
            Object contenidoCelda = traerValor(cell);
            String valorFinal = reemplazarVariables(
                            SysmanFunciones.toString(
                                            contenidoCelda));
            asignarValorFinal(cell, valorFinal);
        }
    }

    /**
     * b&uacute;squeda de variables de tabla en el libro de trabajo
     * 
     * @param wb
     * libro de trabajo
     */
    private void buscarVariablesTabla(Workbook wb) {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(i);
            Iterator<Row> rows = sheet.iterator();
            while (rows.hasNext()) {
                Row row = rows.next();
                Iterator<Cell> cells = row.iterator();
                while (cells.hasNext()) {
                    XSSFCell cell = (XSSFCell) cells.next();
                    buscarVariablesTabla(cell);
                }
            }
        }
    }

    /**
     * b&uacute;squeda de variables de tabla en celdas
     * 
     * @param cell
     * celda a evaluar
     * @param lastRow
     * @param rows
     * @param fila
     * n&uacute;mero de fila actual
     * @param columna
     * n&uacute;mero de columna actual
     * @return n&uacute;mero de fila
     */
    private void buscarVariablesTabla(XSSFCell cell) {
        if (cell != null
            && Cell.CELL_TYPE_BLANK != cell.getCellType()) {
            Object contenidoCelda = traerValor(cell);

            for (Registro registro : variablesTabla) {
                Map<String, Object> campos = registro.getCampos();
                String nombre = SysmanFunciones.toString(campos
                                .get(GeneralParameterEnum.NOMBRE.getName()));
                if (contenidoCelda.equals("<#" + nombre + "#>")) {
                    // hoja,fila,columna
                    String referencia = cell.getSheet().getSheetName() + ";"
                        + cell.getRowIndex() + ";" + cell.getColumnIndex();
                    referencias.add(referencia);
                }
            }
        }
    }

    /**
     * trae el valor segun el formato de la celda
     * 
     * @param cell
     * celda a evaluar
     * @return valor de la celda como objeto
     */
    private Object traerValor(Cell cell) {
        Object object = null;
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            object = cell.getRichStringCellValue().getString();
            break;
        case Cell.CELL_TYPE_NUMERIC:
            object = DateUtil.isCellDateFormatted(cell)
                ? cell.getDateCellValue()
                : cell.getNumericCellValue();
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            object = cell.getBooleanCellValue();
            break;
        case Cell.CELL_TYPE_FORMULA:
            object = cell.getCellFormula();
            break;
        default:
            break;
        }
        return object;
    }

    /**
     * Busca una variable y reemplaza por su respectivo valor
     * 
     * @param texto
     * contenido de la celda
     * @return remplazo
     */
    private String reemplazarVariables(String texto) {
        String regex = "<#([0-9]|[a-z]|[A-Z]|[_])+#>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(texto);
        StringBuffer sb = new StringBuffer(texto.length());
        while (matcher.find()) {
            String cadena = matcher.group();
            String reemplazo = traerValorReemplazo(cadena);
            if (reemplazo != null)
                matcher.appendReplacement(sb, reemplazo);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * trae el valor de reemlazo para la variable especificada
     * 
     * @param variable
     * variable de reemplazo
     * @return valor de reemplazo
     */
    private String traerValorReemplazo(String variable) {
        for (int i = 0; i < columnasConsulta.size(); i++) {
            String columna = columnasConsulta.get(i);
            if (variable.equals("<#" + columna + "#>")) {
                String valor = SysmanFunciones.toString(
                                datos.get(numeroRegistro).getCampos()
                                                .get(columna));
                valor = SysmanFunciones.nvlStr(valor, "");
                return valor;
            }
        }
        return null;
    }

    /**
     * Asigna el valor final para la celda actual
     * 
     * @param cell
     * celda evaluada
     * @param contenidoCelda
     * valor final
     */
    private void asignarValorFinal(Cell cell, String contenidoCelda) {
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_STRING:
            cell.setCellValue(contenidoCelda);
            break;
        case Cell.CELL_TYPE_NUMERIC:
            if (!DateUtil.isCellDateFormatted(cell)) {
                cell.setCellValue(Double.valueOf(contenidoCelda));
            }
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            cell.setCellValue(Boolean.getBoolean(contenidoCelda));
            break;
        default:
            break;
        }
    }

}
