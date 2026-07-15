/*-
 * FrmdesviacionesControlador.java
 *
 * 1.0
 *
 * 28/10/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmdesviacionesControladorEnum;
import com.sysman.serviciospublicos.enums.FrmdesviacionesControladorUrlEnum;
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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar el archivo en excel de las desviaciones
 * registradas
 *
 * @version 1.0, 28/10/2016
 * @author ybecerra
 *
 * @author eamaya
 * @version 2, 25/05/2017 Proceso de Refacotring
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped

public class FrmdesviacionesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida que almacen el codigo del modulo por la cual
     * se ingresa en la aplicacion
     */
    private final String modulo;

    /**
     * Constante definida para almacenar la cadena "CODIGORUTA", se
     * llama en los metodos cargarListaCodigoInicial,
     * cargarListaCodigoFinal, seleccionarFilacmbCodigoInicial y
     * seleccionarFilacmbCodigoFinal
     */
    private final String codigoR;

    /**
     * Constante definida para almacenar la cadena "calibri", se llama
     * en los metodos excelConsolidad y excelDetallado
     */
    private final String tipoLetra;

    /**
     * Constante definida para almacenar la cadena "PERIODO", se llama
     * en los metodos excelConsolidado, excelDetallado
     */
    private final String per;

    /**
     * Constante definida para almacenar la cadena "NOMBRE", se llama
     * en los metodos strDetalle y excelDetallado
     */
    private final String nombre;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena un valor verdadero o false del
     * formulario, si la casilla de verificacion periodo actual esta
     * seleccionada se almacenar el valor como true (verdadero) si no
     * como false (falso)
     */
    private boolean periodo;
    /**
     * Atributo que almacena el codigo del registro seleccionado en el
     * combo estado
     */
    private String estado;
    /**
     * Atributo que almacena el codigo del ciclo del combo Ciclo
     * inicial del formulario
     */
    private String cicloInicial;
    /**
     * Atributo que almacena el codigo del ciclo del combo Ciclo final
     * del formulario
     */
    private String cicloFinal;
    /**
     * Atributo que almacena el codigo del combo Codigo inicial del
     * formulario
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el codigo del combo Codigo Final del
     * formulario
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el codigo del registro seleccionado del
     * combo tipo reporte del formulario
     */
    private String tipoReporte;
    /**
     * Atributo que almacena la fecha ingresada en el campo Fecha
     * Inicial del formulario
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha ingresa en el campo Fecha Final
     * del formulario
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo a nivel clase, se utiliza en el metodo excelDetallado.
     */
    private Cell newCellDet;

    /**
     * Atributo a nivel clase, se utiliza en los metodo
     * excelConsolidado, excelDetallado
     */
    private Workbook workbook;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo de la clase que almacena la lista del combo Ciclo
     * Inicial.
     */
    private List<Registro> listacicloInicial;
    /**
     * Atributo de la clase que almacena la lista del combo Ciclo
     * Final
     */
    private List<Registro> listacicloFinal;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo de la clase que almacena la lista del combo Codigo
     * Inicial
     */
    private RegistroDataModelImpl listacmbCodigoInicial;
    /**
     * Atributo de la clase que almacena la lista del combo Codigo
     * Final
     */
    private RegistroDataModelImpl listacmbCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmdesviacionesControlador
     */
    public FrmdesviacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoR = GeneralParameterEnum.CODIGORUTA.getName();
        tipoLetra = "calibri";
        per = "PERIODO";
        nombre = "NOMBRE";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMDESVIACIONES_CONTROLADOR
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
        cargarListacicloInicial();

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
     *
     * Carga la lista listacicloInicial
     *
     */
    public void cargarListacicloInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listacicloInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdesviacionesControladorUrlEnum.URL8594
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
     * Carga la lista listacicloFinal
     *
     */
    public void cargarListacicloFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmdesviacionesControladorEnum.PARAM0.getValue(),
                        cicloInicial);

        try {
            listacicloFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmdesviacionesControladorUrlEnum.URL9090
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
     * Carga la lista listacmbCodigoInicial
     *
     */
    public void cargarListacmbCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdesviacionesControladorUrlEnum.URL9639
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmdesviacionesControladorEnum.PARAM0.getValue(),
                        cicloInicial);
        param.put(FrmdesviacionesControladorEnum.PARAM1.getValue(),
                        cicloFinal);

        listacmbCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoR);
    }

    /**
     *
     * Carga la lista listacmbCodigoFinal
     *
     */
    public void cargarListacmbCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmdesviacionesControladorUrlEnum.URL10560
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmdesviacionesControladorEnum.PARAM0.getValue(),
                        cicloInicial);
        param.put(FrmdesviacionesControladorEnum.PARAM1.getValue(),
                        cicloFinal);
        param.put(FrmdesviacionesControladorEnum.PARAM2.getValue(),
                        codigoInicial);
        listacmbCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoR);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Impresora en la vista
     *
     *
     */
    public void oprimirImpresora() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ("C".equals(tipoReporte)) {

            excelConsolidado();

        }
        else {
            excelDetallado();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    /**
     * Metodo que devuelve una consulta , para utilizarla en el metodo
     * excelDetallado
     *
     * @return listaDesviaciones
     */
    public String consultarPeriodo() {

        String miPeriodo = null;

        if (periodo) {
            miPeriodo = service.buscarEnLista(cicloInicial, "NUMERO", per,
                            listacicloInicial);
        }
        return miPeriodo;
    }

    /**
     * Metodo que devuelve una lista de registros y se utiliza en
     * excelDetallado
     *
     * @param item
     * @return listaStrDetalle
     */
    public List<Registro> strDetalle(String item) {

        List<Registro> listaStrDetalle = null;
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmdesviacionesControladorEnum.PARAM6.getValue(),
                            item);

            listaStrDetalle = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil
                                                                            .getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmdesviacionesControladorUrlEnum.URL1313
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            for (Registro registroAux : listaStrDetalle) {
                Date fechaGeneracion = (Date) registroAux.getCampos()
                                .get("FECHAREG");

                Map<String, Object> paramPlantilla = new TreeMap<>();

                paramPlantilla.put(GeneralParameterEnum.CODIGO.getName(),
                                registroAux.getCampos().get("SUBCLASE")
                                                .toString());

                paramPlantilla.put(GeneralParameterEnum.FECHA.getName(),
                                fechaGeneracion);

                Registro rsNombre = RegistroConverter
                                .toRegistro(requestManager.get(UrlServiceUtil
                                                .getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmdesviacionesControladorUrlEnum.URL5959
                                                                                .getValue())
                                                .getUrl(), paramPlantilla));

                registroAux.getCampos().put(nombre,
                                rsNombre.getCampos().get(nombre) == null ? ""
                                    : rsNombre.getCampos().get(nombre));

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return listaStrDetalle;
    }

    public void excelDetallado() {
        HashMap<String, Object> auxiliarContador = new HashMap<>();
        String[] nombreColumnaPrincipal = { "ITEM", "CICLO", "C�DIGO INTERNO",
                                            "C�DIGO RUTA", "NOMBRES",
                                            "DIRECCI�N", "USO",
                                            "ESTRATO", "N� MEDIDOR",
                                            "LEC. ANTERIOR",
                                            "LEC. ACTUAL", "CONS. REAL",
                                            "CONS. PROMEDIO", "ESTADO PROCESO",
                                            "DECISI�N", "ANO APERTURA",
                                            "PER. APERTURA",
                                            "FECHA APERTURA", "ANO CIERRE",
                                            "PER. CIERRE", "FECHA CIERRE" };

        int[] anchoColumnaPrincipal = { 3000, 6000, 6000, 35000, 11000, 11000,
                                        6000,
                                        3000, 3200, 4800, 4800, 3000, 5000,
                                        5000, 30000,
                                        5000, 5000, 5000, 5000, 5000, 5000 };

        String[] valorColumnaPrincipal = { "ITEM", "CICLO", "CODIGOINTERNO",
                                           "CODIGORUTA",
                                           "NOMBRES", "DIRECCION", "USO",
                                           "ESTRATO",
                                           "MEDIDOR", "LECTURAINICIAL",
                                           "LECTURAFINAL",
                                           "CONSUMOREAL", "PROMEDIO", "ESTADO",
                                           "DECISION",
                                           "ANO", per, "FECHACREADA",
                                           "ANOCIERRE",
                                           "PERIODOCIERRE", "FECHACIERRE" };

        String[] nombreColDetalleUno = { "", "NOMBRE CARTA", "FECHA GENERACION",
                                         "OBSERVACIONES", "CONSECUTIVO" };

        String[] nombreColDetalleDos = { "", "", "", "", "", "", "", "LECTURAS",
                                         "",
                                         "CONSUMOS", "", "",
                                         "VALORES PENDIENTES POR COBRAR", "" };

        String[] nombreColSubDetalleDos = { "", "", "", "", "", "",
                                            "NOMBRE PERIODO", "ANTERIOR",
                                            "ACTUAL",
                                            "FACTURADO", "REAL", "PENDIENTE",
                                            "ACUEDUCTO", "ALCANTARILLADO" };

        String[] valorColDetalleUno = { "", nombre, "FECHA",
                                        "OBSERVACION", "CONSECUTIVO" };

        String[] valorColDetalleDos = { "", "", "", "", "", "", "NOMBREPERIODO",
                                        "ANTERIOR", "ACTUAL", "FACTURADO",
                                        "CONREAL",
                                        "PENDIENTE", "ACUEDUCTO",
                                        "ALCANTARILLADO" };

        workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Desviaciones");
        // Estilo para los celdas con los valores de las consultas
        Font fondo = workbook.createFont();
        fondo.setFontHeightInPoints((short) 11);
        fondo.setFontName(tipoLetra);
        CellStyle styless = workbook.createCellStyle();
        styless.setFont(fondo);
        styless.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
        HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
        palette.setColorAtIndex(HSSFColor.PALE_BLUE.index, (byte) 198,
                        (byte) 217, (byte) 241);
        styless.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styless.setBorderRight(CellStyle.BORDER_THIN);
        styless.setBorderLeft(CellStyle.BORDER_THIN);
        styless.setBorderTop(CellStyle.BORDER_THIN);
        styless.setBorderBottom(CellStyle.BORDER_THIN);
        // Estilo para las celdas de los titulos
        Font fontDetalle = workbook.createFont();
        fontDetalle.setFontHeightInPoints((short) 11);
        fontDetalle.setFontName(tipoLetra);
        fontDetalle.setColor(HSSFColor.WHITE.index);
        fontDetalle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        CellStyle styles = workbook.createCellStyle();
        styles.setFont(fontDetalle);
        styles.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
        styles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styles.setAlignment(CellStyle.ALIGN_CENTER);
        styles.setBorderRight(CellStyle.BORDER_THIN);
        styles.setBorderLeft(CellStyle.BORDER_THIN);
        styles.setBorderTop(CellStyle.BORDER_THIN);
        styles.setBorderBottom(CellStyle.BORDER_THIN);

        // Estilo para las celdas del los titulos del subDetalle
        Font fontSubDetalle = workbook.createFont();
        fontSubDetalle.setFontHeightInPoints((short) 11);
        fontSubDetalle.setFontName(tipoLetra);
        fontSubDetalle.setColor(HSSFColor.WHITE.index);
        fontSubDetalle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        CellStyle styleDet = workbook.createCellStyle();
        styleDet.setFont(fontSubDetalle);
        styleDet.setFillForegroundColor(HSSFColor.GREEN.index);
        styleDet.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        styleDet.setBorderRight(CellStyle.BORDER_THIN);
        styleDet.setBorderLeft(CellStyle.BORDER_THIN);
        styleDet.setBorderTop(CellStyle.BORDER_THIN);
        styleDet.setBorderBottom(CellStyle.BORDER_THIN);

        Row rowTitulo = sheet.createRow((short) 0);
        Cell cellTitulo = rowTitulo.createCell(0);
        cellTitulo.setCellValue(
                        "EMPRESA DE ACUEDUCTO ALCANTARILLADO Y ASEO DE YOPAL");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
        cellTitulo.setCellStyle(styles);

        Row rowTitulo1 = sheet.createRow(1);
        Cell cellTitulo1 = rowTitulo1.createCell(0);
        cellTitulo1.setCellValue("INFORME DE DESVIACIONES");
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 20));
        cellTitulo1.setCellStyle(styles);

        List<Registro> listaInformeDesviaciones = null;
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmdesviacionesControladorEnum.PARAM0.getValue(),
                            cicloInicial);
            param.put(FrmdesviacionesControladorEnum.PARAM1.getValue(),
                            cicloFinal);
            param.put(FrmdesviacionesControladorEnum.PARAM2.getValue(),
                            codigoInicial);
            param.put(FrmdesviacionesControladorEnum.PARAM3.getValue(),
                            codigoFinal);

            param.put(FrmdesviacionesControladorEnum.PARAM4.getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));

            param.put(FrmdesviacionesControladorEnum.PARAM5.getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));

            param.put(GeneralParameterEnum.ESTADO.getName(),
                            estado);

            param.put(GeneralParameterEnum.PERIODO.getName(),
                            consultarPeriodo());

            listaInformeDesviaciones = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil
                                                                            .getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            FrmdesviacionesControladorUrlEnum.URL9191
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (!listaInformeDesviaciones.isEmpty()) {

            int des = 3;

            for (Registro valorDes : listaInformeDesviaciones) {
                Row encabezado = sheet.createRow(des);

                for (int i = 0; i < nombreColumnaPrincipal.length; i++) {

                    Cell newCell = encabezado.createCell(i);
                    newCell.setCellValue(nombreColumnaPrincipal[i]);
                    newCell.setCellStyle(styles);
                    sheet.setColumnWidth(i, anchoColumnaPrincipal[i]);

                }
                des++;
                Row rowValor = sheet.createRow(des);
                for (int i = 0; i < valorColumnaPrincipal.length; i++) {
                    Cell nuevaCelda = rowValor.createCell(i);
                    nuevaCelda.setCellValue((valorDes.getCampos()
                                    .get(valorColumnaPrincipal[i]) == null) ? ""
                                        : valorDes.getCampos()
                                                        .get(valorColumnaPrincipal[i])
                                                        .toString());

                    nuevaCelda.setCellStyle(styless);

                }
                des++;

                Row encabezadoDet = sheet.createRow(des);
                for (int i = 1; i < nombreColDetalleUno.length; i++) {

                    newCellDet = encabezadoDet.createCell(i);
                    newCellDet.setCellValue(nombreColDetalleUno[i]);
                    newCellDet.setCellStyle(styleDet);

                }

                des++;

                for (int i = 6; i < nombreColDetalleDos.length; i++) {

                    newCellDet = encabezadoDet.createCell(i);
                    newCellDet.setCellValue(nombreColDetalleDos[i]);

                    CellRangeAddress region = CellRangeAddress.valueOf(
                                    "H" + des + ":" + "I" + des);
                    sheet.addMergedRegion(region);

                    CellRangeAddress regionUno = CellRangeAddress.valueOf(
                                    "J" + des + ":" + "L" + des);
                    sheet.addMergedRegion(regionUno);

                    CellRangeAddress regionDos = CellRangeAddress.valueOf(
                                    "M" + des + ":" + "N" + des);
                    sheet.addMergedRegion(regionDos);

                    newCellDet.setCellStyle(styles);
                }

                Row rowEncabezadoSubDet = sheet.createRow(des);
                encabezadoBucle(nombreColSubDetalleDos, rowEncabezadoSubDet,
                                styleDet);

                des++;
                int longInfDetalle = 0;
                int aux = des - 1;

                Map<String, Object> paramDesv = new TreeMap<>();
                paramDesv.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                paramDesv.put(FrmdesviacionesControladorEnum.PARAM6.getValue(),
                                valorDes.getCampos().get("ITEM")
                                                .toString());

                List<Registro> listaInfDetalle = null;
                try {
                    listaInfDetalle = RegistroConverter
                                    .toListRegistro(
                                                    requestManager.getList(
                                                                    UrlServiceUtil
                                                                                    .getInstance()
                                                                                    .getUrlServiceByUrlByEnumID(
                                                                                                    FrmdesviacionesControladorUrlEnum.URL1515
                                                                                                                    .getValue())
                                                                                    .getUrl(),
                                                                    paramDesv));
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

                for (Registro registro : listaInfDetalle) {
                    Row rowEncabeDet = sheet.createRow(des);

                    bucleDetalleDos(valorColDetalleDos, rowEncabeDet, registro,
                                    styless);
                    des++;
                    longInfDetalle++;
                }

                int longStrDet = 0;

                List<Registro> strDet = strDetalle(
                                valorDes.getCampos().get("ITEM").toString());

                for (Registro valorSubDet : strDet) {
                    Row rowEncabezadoSubDeta;

                    contador(auxiliarContador, longStrDet, longInfDetalle,
                                    sheet, des, aux);
                    rowEncabezadoSubDeta = (Row) auxiliarContador.get("ROW");
                    des = (int) auxiliarContador.get("DES");
                    aux = (int) auxiliarContador.get("AUX");
                    bucleDetalleUno(valorColDetalleUno, rowEncabezadoSubDeta,
                                    valorSubDet, styless);

                    longStrDet++;
                }

            }
        }
        generarArchivo();
    }

    private void encabezadoBucle(String[] nombreColSubDetalleDos,
        Row rowEncabezadoSubDet, CellStyle styleDet) {
        for (int i = 6; i < nombreColSubDetalleDos.length; i++) {
            newCellDet = rowEncabezadoSubDet.createCell(i);
            newCellDet.setCellValue(nombreColSubDetalleDos[i]);
            newCellDet.setCellStyle(styleDet);
        }
    }

    private void generarArchivo() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "DESVIACIONES.xls");
        }
        catch (IOException | JRException ex) {
            logger.error(ex);
        }
    }

    private void contador(HashMap<String, Object> obj, int longStrDet,
        int longInfDetalle, Sheet sheet, int des, int aux) {

        if (longStrDet > (longInfDetalle)) {
            obj.put("ROW", sheet.createRow(des));
            obj.put("DES", des + 1);
            obj.put("AUX", aux);
        }
        else {
            obj.put("ROW", sheet.createRow(aux));
            obj.put("DES", des);
            obj.put("AUX", aux + 1);
        }
    }

    private void bucleDetalleDos(String[] valorColDetalleDos, Row rowEncabeDet,
        Registro registro, CellStyle styless) {
        for (int i = 6; i < valorColDetalleDos.length; i++) {
            newCellDet = rowEncabeDet.createCell(i);
            newCellDet.setCellValue((registro.getCampos()
                            .get(valorColDetalleDos[i]) == null)
                                ? ""
                                : registro.getCampos()
                                                .get(valorColDetalleDos[i])
                                                .toString());
            newCellDet.setCellStyle(styless);

        }
    }

    private void bucleDetalleUno(String[] valorColDetalleUno,
        Row rowEncabezadoSubDeta, Registro valorSubDet, CellStyle styless) {
        for (int j = 1; j < valorColDetalleUno.length; j++) {
            newCellDet = rowEncabezadoSubDeta.createCell(j);
            newCellDet.setCellValue((valorSubDet.getCampos()
                            .get(valorColDetalleUno[j]) == null)
                                ? ""
                                : valorSubDet.getCampos()
                                                .get(valorColDetalleUno[j])
                                                .toString());
            newCellDet.setCellStyle(styless);
        }
    }

    /**
     * Metodo que se ejecuta al darle clic en el boton excel y
     * seleccionando en el combo Tipo Reporte "Consolidado" del
     * formulario, lo que hace este metodo exporta en un archivo excel
     * los datos de una consulta registrada con unos estilos de diseno
     */
    public void excelConsolidado() {

        HashMap<String, Object> reemplazos = new HashMap<>();
        // Reemplazos valores consulta informe
        reemplazos.put("cicloInicial", "'" + cicloInicial + "'");
        reemplazos.put("cicloFinal", "'" + cicloFinal + "'");
        reemplazos.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazos.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        reemplazos.put("codigoInicial", "'" + codigoInicial + "'");
        reemplazos.put("codigoFinal", "'" + codigoFinal + "'");

        String estadoCon;
        if ("T".equals(estado)) {
            estadoCon = "";
        }
        else {
            estadoCon = "AND SP_DESVIACIONES.ESTADO = '" + estado + "'";
        }

        reemplazos.put("condicionEstado", estadoCon);
        String periodoCon;
        if (periodo) {
            periodoCon = "AND SP_DESVIACIONES.PERIODO = '"
                + service.buscarEnLista(cicloInicial, "NUMERO", per,
                                listacicloInicial)
                + "'";
        }
        else {
            periodoCon = "";
        }

        reemplazos.put("condicionPeriodo", periodoCon);

        String strSql = Reporteador.resuelveConsulta(
                        "800066InformeDesviaciones",
                        Integer.parseInt(modulo),
                        reemplazos);

        try {
            workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());

            Sheet sheet = workbook.getSheet("Report");
            sheet.createFreezePane(0, 4);
            Font fondo = workbook.createFont();
            fondo.setFontHeightInPoints((short) 11);
            fondo.setFontName(tipoLetra);

            CellStyle styless = workbook.createCellStyle();
            styless.setFont(fondo);
            styless.setFillForegroundColor(HSSFColor.PALE_BLUE.index);
            HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
            palette.setColorAtIndex(HSSFColor.PALE_BLUE.index, (byte) 198,
                            (byte) 217, (byte) 241);
            styless.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styless.setBorderRight(CellStyle.BORDER_THIN);
            styless.setBorderLeft(CellStyle.BORDER_THIN);
            styless.setBorderTop(CellStyle.BORDER_THIN);
            styless.setBorderBottom(CellStyle.BORDER_THIN);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row newRow = sheet.getRow(i);
                for (int j = 0; j < 21; j++) {
                    Cell newCell = newRow.getCell(j);
                    newCell.setCellStyle(styless);

                }

            }
            sheet.shiftRows(0, sheet.getLastRowNum(), 3);

            Font fontDetalle = workbook.createFont();
            fontDetalle.setFontHeightInPoints((short) 11);
            fontDetalle.setFontName(tipoLetra);
            fontDetalle.setColor(HSSFColor.WHITE.index);
            fontDetalle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            CellStyle styles = workbook.createCellStyle();
            styles.setFont(fontDetalle);
            styles.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
            styles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styles.setAlignment(CellStyle.ALIGN_CENTER);
            styles.setBorderRight(CellStyle.BORDER_THIN);
            styles.setBorderLeft(CellStyle.BORDER_THIN);
            styles.setBorderTop(CellStyle.BORDER_THIN);
            styles.setBorderBottom(CellStyle.BORDER_THIN);
            for (int i = 0; i < 21; i++) {
                sheet.getRow((short) 3).getCell(i).setCellStyle(styles);
            }

            Row rowTitulo = sheet.createRow((short) 0);
            Cell cellTitulo = rowTitulo.createCell(0);
            cellTitulo.setCellValue(
                            "EMPRESA DE ACUEDUCTO ALCANTARILLADO Y ASEO DE YOPAL");
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
            cellTitulo.setCellStyle(styles);

            Row rowTitulo1 = sheet.createRow(1);
            Cell cellTitulo1 = rowTitulo1.createCell(0);
            cellTitulo1.setCellValue("INFORME DE DESVIACIONES");
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 20));
            cellTitulo1.setCellStyle(styles);

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Desviacion.xls");
        }
        catch (SQLException | IOException | JRException
                        | DRException | SysmanException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CAMBIAR>

    public void cambiarcicloInicial() {
        cicloFinal = null;
        codigoInicial = null;
        codigoFinal = null;

        cargarListacicloFinal();
    }

    public void cambiarcicloFinal() {
        codigoInicial = null;
        codigoFinal = null;
        cargarListacmbCodigoInicial();

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoR).toString();
        codigoFinal = null;
        cargarListacmbCodigoFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoR).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public boolean getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(boolean periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable estado
     *
     * @return estado
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Asigna la variable estado
     *
     * @param estado
     * Variable a asignar en estado
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna la variable cicloInicial
     *
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     *
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     *
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     *
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
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
     * Retorna la variable tipoReporte
     *
     * @return tipoReporte
     */
    public String getTipoReporte() {
        return tipoReporte;
    }

    /**
     * Asigna la variable tipoReporte
     *
     * @param tipoReporte
     * Variable a asignar en tipoReporte
     */
    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    /**
     * Retorna la variable fechaInicial
     *
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     *
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     *
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     *
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
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
     * Retorna la lista listacmbCicloInicial
     *
     * @return listacmbCicloInicial
     */
    public List<Registro> getListacicloInicial() {
        return listacicloInicial;
    }

    /**
     * Asigna la lista listacmbCicloInicial
     *
     * @param listacmbCicloInicial
     * Variable a asignar en listacmbCicloInicial
     */
    public void setListacicloInicial(List<Registro> listacicloInicial) {
        this.listacicloInicial = listacicloInicial;
    }

    /**
     * Retorna la lista listacmbCicloFinal
     *
     * @return listacmbCicloFinal
     */
    public List<Registro> getListacicloFinal() {
        return listacicloFinal;
    }

    /**
     * Asigna la lista listacmbCicloFinal
     *
     * @param listacmbCicloFinal
     * Variable a asignar en listacmbCicloFinal
     */
    public void setListacicloFinal(List<Registro> listacicloFinal) {
        this.listacicloFinal = listacicloFinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbCodigoInicial
     *
     * @return listacmbCodigoInicial
     */
    public RegistroDataModelImpl getListacmbCodigoInicial() {
        return listacmbCodigoInicial;
    }

    /**
     * Asigna la lista listacmbCodigoInicial
     *
     * @param listacmbCodigoInicial
     * Variable a asignar en listacmbCodigoInicial
     */
    public void setListacmbCodigoInicial(
        RegistroDataModelImpl listacmbCodigoInicial) {
        this.listacmbCodigoInicial = listacmbCodigoInicial;
    }

    /**
     * Retorna la lista listacmbCodigoFinal
     *
     * @return listacmbCodigoFinal
     */
    public RegistroDataModelImpl getListacmbCodigoFinal() {
        return listacmbCodigoFinal;
    }

    /**
     * Asigna la lista listacmbCodigoFinal
     *
     * @param listacmbCodigoFinal
     * Variable a asignar en listacmbCodigoFinal
     */
    public void setListacmbCodigoFinal(
        RegistroDataModelImpl listacmbCodigoFinal) {
        this.listacmbCodigoFinal = listacmbCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
