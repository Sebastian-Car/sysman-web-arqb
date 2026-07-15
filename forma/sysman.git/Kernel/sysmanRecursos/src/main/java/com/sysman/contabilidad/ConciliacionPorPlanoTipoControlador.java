package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisGeneralRemote;
import com.sysman.contabilidad.enums.ConciliacionPorPlanoTipoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Este proceso permite realizar el cargue de un archivo plano (Excel)
 * basado en un archivo que contenga las llaves de la tabla detalle
 * comprobante
 *
 * @author jGomez
 * @version 1, 28/09/2018
 *
 * 
 */
@ManagedBean
@ViewScoped
public class ConciliacionPorPlanoTipoControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    private String cuenta;
    private String columnaTipo;
    private String columnaComprobante;
    private String columnaDebito;
    private String columnaCredito;
    private String columnaConsecutivo;
    private String columnaError;
    private String columnaFecha;
    private int filaBase;
    private Date fecha;
    private int filaFin;

    private String valorCredito;

    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoCargaPlantillaExcel;
    /**
     * Anio que se extrae de la fecha de conciliacion seleccionada.
     */
    private int ano;
    /**
     * Indicador para eliminar partidas conciliatorias.
     */
    private boolean isTexto;
    private CellStyle textoError;
    private CellStyle textoInformativo;
    private CellStyle textoDocumentoDuplicado;
    private CellStyle fechaError;
    private CellStyle fechaInformativo;
    private CellStyle fechaDocumentDuplicado;

    private RegistroDataModelImpl listaCuenta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilidadSeisGeneralRemote ejbContabilidadSeisGeneral;

    /**
     * Creates a new instance of ConciliacionPorPlanoControlador
     */
    public ConciliacionPorPlanoTipoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONCILIACION_POR_PLANO_CONTROLADOR_TIPO
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            contArchivoCargaPlantillaExcel = new ContenedorArchivo();
            fecha = SysmanFunciones.ultimoDiaDate(new Date());
            columnaTipo = "G";
            columnaComprobante = "H";
            columnaDebito = "C";
            columnaCredito = "D";
            columnaConsecutivo = "I";

            columnaError = "S";
            columnaFecha = "B";
            filaBase = 1;
            filaFin = 2;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaCuenta();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        cambiarfecha();
    }

    public void cargarListaCuenta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConciliacionPorPlanoTipoControladorUrlEnum.URL16211
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void oprimirIniciar() {
        archivoDescarga = null;
        if (!validarEstado()) {
            return;
        }
        if (filaFin < filaBase) {
            String mensaje = idioma.getString("TB_TB1308");
            mensaje = mensaje.replace("s$filaBase$s", String.valueOf(filaBase));
            mensaje = mensaje.replace("s$filaFin$s", String.valueOf(filaFin));
            JsfUtil.agregarMensajeInformativo(mensaje);
            return;
        }
        if (contArchivoCargaPlantillaExcel.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1306"));
            return;
        }

        generarConciliacion();
        // </CODIGO_DESARROLLADO>
    }

    public String exportarExcelToCsv(Workbook workbook, int filainicial,
        int filafinal) {
        StringBuilder data = new StringBuilder();
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(1);
        int num = 0;

        for (int rowNum = filainicial; rowNum <= filafinal; rowNum++) {
            row = sheet.getRow(rowNum);
            if (row == null) {
                // This whole row is empty
                // Handle it as needed
                continue;
            }
            num = num + 1;

            data.append(asignaDato(row, columnaTipo, "S"));
            data.append(asignaDato(row, columnaComprobante, "S"));
            data.append(asignaDato(row, columnaConsecutivo, "S"));
            data.append(SysmanFunciones
                            .nvlStr(asignaDato(row, columnaDebito, "N"), "0"));
            data.append(SysmanFunciones
                            .nvlStr(asignaDato(row, columnaCredito, "N"), "0"));
            data.append(asignaDato(row, columnaFecha, "D"));
            data.append("#");

            if (num >= 100) {
                data.append("') || TO_CLOB('");
            }
        }
        return data.toString();

    }

    private String asignaDato(Row row, String columna, String tipo) {
        String salida = "";
        Cell cell = row.getCell(columna.charAt(0) - 65,
                        Row.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            salida = ";";
        }
        else {
            isTexto = true;
            salida = asignarFormatoValor(cell, tipo) + ";";
        }
        return salida;
    }

    private Object asignarFormatoValor(Cell cell, String tipo) {
        Object object = null;
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                object = cell.getRichStringCellValue().getString();
                object = object.toString().replace(",", "");
                object = object.toString().replace(";", "CHR(59)");
                object = object.toString().replace("#", "CHR(35)");
                break;
            case Cell.CELL_TYPE_NUMERIC:
                object = definirValor(cell, tipo);
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                object = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                object = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_BLANK:
                object = cell;
                break;
            default:
                object = null;
                break;
            }
        }
        else {
            logger.debug("La celda viene nula");
        }
        return object;
    }

    private void generarConciliacion() {
        try (
                        FileInputStream fileInputStream = new FileInputStream(
                                        contArchivoCargaPlantillaExcel
                                                        .getArchivo());
                        Workbook workbook = new HSSFWorkbook(
                                        fileInputStream);) {

            Sheet sheet = workbook.getSheetAt(0);
            isTexto = false;
            crearEstilos(workbook);
            String respuesta = "";

            int filaInicial = filaBase;
            int filaFinal = 0;
            while (filaInicial <= filaFin) {
                filaFinal = filaInicial + 999;
                filaFinal = (filaFinal > filaFin ? filaFin : filaFinal);
                String cadena = "TO_CLOB('"
                    + exportarExcelToCsv(workbook, filaInicial - 1,
                                    filaFinal - 1)
                    + "')";

                respuesta = ejecutarFuncConciliacion(cadena, filaInicial);
                definirFormatoColError(respuesta, sheet, filaInicial - 1);
                filaInicial = filaFinal + 1;

            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            Calendar calendar = Calendar.getInstance();
            String cadenaFecha = ""
                + Integer.toString(calendar.get(Calendar.YEAR)
                    + (calendar.get(Calendar.MONTH) + 1)
                    + calendar.get(Calendar.DATE)
                    + calendar.get(Calendar.HOUR_OF_DAY)
                    + calendar.get(Calendar.MINUTE)
                    + calendar.get(Calendar.SECOND));
            String nombreArchivo = "InformeDeConciliacionde" + cadenaFecha
                + contArchivoCargaPlantillaExcel.getArchivo().getName();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            nombreArchivo);

        }
        catch (FileNotFoundException e) {

            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB1315") + e.getMessage());
        }
        catch (ClassCastException e) {

            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1363"));
        }
        catch (IOException | JRException e) {
            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void definirFormatoColError(String respuesta, Sheet sheet,
        int filaInicial) {
        String auxCadena;
        BufferedReader br = null;
        int posFila = -1;
        int posTipo = -1;

        InputStream inputStream = new ByteArrayInputStream(
                        respuesta.getBytes(Charset.forName("UTF-8")));
        br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            while ((auxCadena = br.readLine()) != null) {
                posFila = auxCadena.indexOf(";");
                posTipo = auxCadena.indexOf(";", posFila + 1);

                definirTipoMensaje(
                                auxCadena.substring(posFila + 1,
                                                posTipo),

                                auxCadena.substring(0, posFila)
                                    + " - "
                                    + auxCadena.substring(posTipo + 1),
                                sheet.getRow(Integer.parseInt(auxCadena
                                                .substring(0, posFila))
                                    - 1));
            }
        }
        catch (IOException e) {
            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String ejecutarFuncConciliacion(String cadena, int filaInicial) {
        String respuesta = null;
        // setArchivoSalida("/home/jgomez/Escritorio/prueba.txt",
        // cadena);
        try {
            respuesta = ejbContabilidadSeisGeneral.generarRtaConciliadosXTipo(
                            compania,
                            cuenta,
                            cadena, 4, 5, 1, 2, 3, 6, fecha, filaInicial,
                            SessionUtil.getUser().getCodigo());
        }
        catch (SystemException e) {
            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;
    }

    private void definirTipoMensaje(String tipoMensaje, String mensaje,
        Row row) {
        switch (tipoMensaje) {
        case "1":
            registrarMensaje(row, "Listo Antes. - " + mensaje,
                            textoDocumentoDuplicado,
                            fechaDocumentDuplicado);
            break;
        case "2":
            registrarMensaje(row, "Ok. - " + mensaje,
                            textoInformativo, fechaInformativo);
            break;
        case "3":
            registrarMensaje(row, "Inconsistencia. - " + mensaje,
                            textoError, fechaError);
            break;
        default:
            break;

        }

    }

    public static void setArchivoSalida(String strArcNom, String strArcCon) {
        BufferedWriter bfrArchivoSalida = null;

        try {
            bfrArchivoSalida = new BufferedWriter(new FileWriter(strArcNom));
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }

        try {
            bfrArchivoSalida.append(strArcCon);
        }
        catch (Exception ex) {
            Logger.getLogger(
                            ConciliacionPorPlanoTipoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            try {
                bfrArchivoSalida.close();
            }
            catch (Exception ex) {
                Logger.getLogger(
                                ConciliacionPorPlanoTipoControlador.class
                                                .getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    public void cambiarfecha() {
        // <CODIGO_DESARROLLADO>
        cuenta = null;
        if (fecha != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);
            try {
                if (calendar.get(Calendar.DATE) != SysmanFunciones
                                .ultimoDiaInt(fecha)) {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB3257"));
                    fecha = null;
                    return;
                }

                if (!validarEstado()) {
                    fecha = null;
                    return;
                }

            }
            catch (ParseException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

            ano = calendar.get(Calendar.YEAR);
            cargarListaCuenta();
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarEstado() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);
            String estadoAnio = ejbSysmanUtil
                            .verificarEstadoPeriodoAnual(compania,
                                            calendar.get(Calendar.YEAR),
                                            Integer.parseInt(modulo),
                                            2);

            String estadoMes = ejbSysmanUtil
                            .verificarEstadoPeriodoMensual(compania,
                                            calendar.get(Calendar.YEAR),
                                            calendar.get(Calendar.MONTH) + 1,
                                            Integer.parseInt(modulo),
                                            2);

            String estadoDia = ejbSysmanUtil.verificarEstadoDiario(
                            compania,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DATE),
                            Integer.parseInt(modulo),
                            2);

            if (!verificarExisteFecha(estadoAnio, estadoMes, estadoDia)) {
                return false;
            }

            if ("C".equals(estadoAnio)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3258"));
                return false;
            }
            else if ("C".equals(estadoMes)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3259"));
                return false;
            }
            else if ("C".equals(estadoDia)) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3261"));
                return false;
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    private boolean verificarExisteFecha(String estadoAnio, String estadoMes,
        String estadoDia) {
        if ("E".equals(estadoAnio) || "E".equals(estadoMes)
            || "E".equals(estadoDia)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3262"));
            return false;
        }
        return true;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuenta = ((BigInteger) registroAux.getCampos().get("ID")).toString();
    }

    private Object definirValor(Cell cell, String tipo) {
        Object object;

        if (DateUtil.isCellDateFormatted(cell)) {
            object = cell.getDateCellValue();
            if (isTexto) {
                try {
                    String fechaC = SysmanFunciones
                                    .convertirAFechaCadena((Date) object);
                    object = fechaC;
                }
                catch (ParseException e) {
                    Logger.getLogger(
                                    ConciliacionPorPlanoTipoControlador.class
                                                    .getName())
                                    .log(Level.SEVERE, null, e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
            isTexto = false;
        }
        else {
            if ("N".equals(tipo)) {
                object = cell.getNumericCellValue();
            }
            else {
                DataFormatter formatter = new DataFormatter();
                object = formatter.formatCellValue(cell);
            }

        }
        return object;
    }

    /**
     * Registra un mensaje en la celda de errores.
     *
     * @param row
     * Fila que se va a afectar.
     * @param mensaje
     * Mensaje.
     * @param color
     * Indice del color.
     */
    public void registrarMensaje(Row row, String mensaje,
        CellStyle estilo, CellStyle estiloFecha) {
        // Color del texto, Por cada celda en la fila
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                cell.setCellStyle(estilo);
            }
            else {
                Cell c = row.createCell(i);
                c.setCellStyle(estilo);
            }
        }
        // Creacion de la celda de errores.
        Cell cell = row.createCell(
                        CellReference.convertColStringToIndex(columnaError));
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellStyle(estilo);
        cell.setCellValue(new HSSFRichTextString(mensaje));
    }

    /**
     * Crea una fuente de texto, segun los parametros recibidos.
     *
     * @param workbook
     * @param fontName
     * Nombre de la fuente.
     * @param size
     * Tamanio del texto.
     * @param color
     * Color de la fuente.
     * @return Fuente de texto.
     */
    private Font getFuenteTexto(Workbook workbook, String fontName, short size,
        short color) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(size);
        font.setFontName(fontName);
        font.setColor(color);
        return font;
    }

    /**
     * Crea un estilo de celda en el workbook ingresado por parametro.
     *
     * @param Workbook
     * Libro de trabajo.
     * @param color
     * Indice asociado a un color determinado.
     * @return
     */
    private CellStyle crearEstilo(Workbook workbook, short color) {
        CellStyle style = workbook.createCellStyle();
        style.setFont(getFuenteTexto(workbook, "Arial", (short) 10, color));

        return style;
    }

    /**
     * Inicializa los estilos necesarios para registrar las revisiones
     * que se hacen sobre el archivo Excel.
     *
     * @param workbook
     * Libro de trabajo.
     */
    private void crearEstilos(Workbook workbook) {
        textoDocumentoDuplicado = crearEstilo(workbook,
                        IndexedColors.BLUE_GREY.getIndex());
        fechaError = crearEstilo(workbook, IndexedColors.ORANGE.getIndex());
        fechaInformativo = crearEstilo(workbook,
                        IndexedColors.GREEN.getIndex());
        fechaDocumentDuplicado = crearEstilo(workbook,
                        IndexedColors.BLUE_GREY.getIndex());
        textoError = crearEstilo(workbook, IndexedColors.ORANGE.getIndex());
        textoInformativo = crearEstilo(workbook,
                        IndexedColors.GREEN.getIndex());

    }

    // <SET_GET_ATRIBUTOS>

    public String getcolumnaDebito() {
        return columnaDebito;
    }

    public void setcolumnaDebito(String columnaDebito) {
        this.columnaDebito = columnaDebito;
    }

    public String getcolumnaCredito() {
        return columnaCredito;
    }

    public void setcolumnaCredito(String columnaCredito) {
        this.columnaCredito = columnaCredito;
    }

    public String getColumnaError() {
        return columnaError;
    }

    public void setColumnaError(String columnaError) {
        this.columnaError = columnaError;
    }

    public String getValorCredito() {
        return valorCredito;
    }

    public void setValorCredito(String valorCredito) {
        this.valorCredito = valorCredito;
    }

    public int getFilaBase() {
        return filaBase;
    }

    public void setFilaBase(int filaBase) {
        this.filaBase = filaBase;
    }

    public int getFilaFin() {
        return filaFin;
    }

    public void setFilaFin(int filaFin) {
        this.filaFin = filaFin;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getColumnaTipo() {
        return columnaTipo;
    }

    public void setColumnaTipo(String columnaTipo) {
        this.columnaTipo = columnaTipo;
    }

    public String getColumnaComprobante() {
        return columnaComprobante;
    }

    public void setColumnaComprobante(String columnaComprobante) {
        this.columnaComprobante = columnaComprobante;
    }

    public String getColumnaDebito() {
        return columnaDebito;
    }

    public void setColumnaDebito(String columnaDebito) {
        this.columnaDebito = columnaDebito;
    }

    public String getColumnaCredito() {
        return columnaCredito;
    }

    public void setColumnaCredito(String columnaCredito) {
        this.columnaCredito = columnaCredito;
    }

    public String getColumnaConsecutivo() {
        return columnaConsecutivo;
    }

    public void setColumnaConsecutivo(String columnaConsecutivo) {
        this.columnaConsecutivo = columnaConsecutivo;
    }

    public String getColumnaFecha() {
        return columnaFecha;
    }

    public void setColumnaFecha(String columnaFecha) {
        this.columnaFecha = columnaFecha;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoCargaPlantillaExcel() {
        return contArchivoCargaPlantillaExcel;
    }

    public void setContArchivoCargaPlantillaExcel(
        ContenedorArchivo contArchivoCargaPlantillaExcel) {
        this.contArchivoCargaPlantillaExcel = contArchivoCargaPlantillaExcel;
    }

    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    public void setListaCuenta(
        RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

}
