/*-
 * FacturadosPredialDNPControlador.java
 *
 * 1.0
 * 
 * 16/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.FacturadosPredialDNPControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma FacturadosPredialDNP asociado al formulario
 * "Informe de Facturados Predial".
 *
 * @version 1.0, 16/02/2017
 * @author yrojas
 * 
 * @author asana
 * @version 2.0 13/06/2017, Se modifica enum en formulario, y se
 * modifica conexion
 * 
 * @author eamaya
 * @version 3.0, 28/06/2017, Proceso de Refactoring DSS y Manejo de
 * EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class FacturadosPredialDNPControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que aloja el codigo del módulo desde
     * el cual el usuario inicio session
     */
    private final String modulo;

    /**
     * Constante que almacena el valor del mensaje TB_TB2835.
     */
    private final String msgVerificarParametroCons;

    /**
     * Constante que almacena el valor del la cadena de texto que se
     * va a reemplazar en el mensaje TB_TB2835.
     */
    private final String reemplazoParametroCons;

    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo asociado al valor del anio inicial que se va a manejar
     * en la consulta del combo del reporte.
     */
    private String anioInicial;

    /**
     * Atributo asociado al valor del anio final que se va a manejar
     * en la consulta del combo del reporte.
     */
    private String anioFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>

    /**
     * Atributo asociado al parametro "TITULO UNO OFICIOS" y que es
     * usado en el encabezado de los reportes.
     */
    private String parTituloUnoOficios;

    /**
     * Atributo asociado al parametro "TITULO DOS OFICIOS" y que es
     * usado en el encabezado de los reportes.
     */
    private String parTituloDosOficios;

    /**
     * Atributo asociado al parametro "TITULO TRES OFICIOS" y que es
     * usado en el encabezado de los reportes.
     */
    private String parTituloTresOficios;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    /**
     * Lista que contiene la informacion de los detalles del combo
     * anio inicial.
     */
    private List<Registro> listaAnioInicial;

    /**
     * Lista que contiene la informacion de los detalles del combo
     * anio final.
     */
    private List<Registro> listaAnioFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FacturadosPredialDNPControlador
     */
    public FacturadosPredialDNPControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        msgVerificarParametroCons = "TB_TB2835";
        reemplazoParametroCons = "#$parametro#$";
        anioInicial = Integer.toString(SysmanFunciones.ano(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURADOS_PREDIAL_DNPCONTROLADOR
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
        cargarListaAnioInicial();
        cargarListaAnioFinal();
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
        try {
            parTituloUnoOficios = ejbSysmanUtil.consultarParametro(compania,
                            "TITULO UNO OFICIOS", modulo, new Date(), false);

            if (parTituloUnoOficios == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgVerificarParametroCons)
                                                .replace(reemplazoParametroCons,
                                                                "TITULO UNO OFICIOS"));
                return;
            }

            parTituloDosOficios = ejbSysmanUtil.consultarParametro(compania,
                            "TITULO DOS OFICIOS", modulo, new Date(), false);

            if (parTituloDosOficios == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgVerificarParametroCons)
                                                .replace(reemplazoParametroCons,
                                                                "TITULO DOS OFICIOS"));
                return;
            }

            parTituloTresOficios = ejbSysmanUtil
                            .consultarParametro(compania, "TITULO TRES OFICIOS",
                                            modulo,
                                            new Date(), false);

            if (parTituloTresOficios == null) {
                JsfUtil.agregarMensajeError(
                                idioma.getString(msgVerificarParametroCons)
                                                .replace(reemplazoParametroCons,
                                                                "TITULO TRES OFICIOS"));
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaAnioInicial
     */
    public void cargarListaAnioInicial() {

        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAnioInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturadosPredialDNPControladorUrlEnum.URL8786
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista listaAnioFinal
     */
    public void cargarListaAnioFinal() {

        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.ANO.getName(), anioInicial);

            listaAnioFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturadosPredialDNPControladorUrlEnum.URL9156
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
     * Metodo ejecutado al oprimir el boton Pdf en la vista.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que arma el informe en formato excel. Estableciendo las
     * celdas, los títulos y los datos que serán mostrados
     */
    public void generarReporte(FORMATOS formato) {
        String consulta;

        try {
            Map<String, Object> parametros = new HashMap<>();

            consulta = ejecutarFuncion();

            if (SysmanFunciones.validarVariableVacio(consulta)) {
                return;
            }

            parametros.put("PR_UNO_OFICIOS", parTituloUnoOficios);
            parametros.put("PR_DOS_OFICIOS", parTituloDosOficios);
            parametros.put("PR_TRES_OFICIOS", parTituloTresOficios);
            parametros.put("PR_ANOINI", anioInicial);
            parametros.put("PR_ANOFIN", anioFinal);
            parametros.put("PR_NIT", SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_STRSQL", consulta);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001412INFFACTURADOSPREDIALDNP",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo que ejecuta la funcion que arma la consulta que sera
     * enviada al reporte.
     * 
     * @return consulta Cadena con la consulta armada.
     */
    public String ejecutarFuncion() {
        String consulta = " ";

        try {
            consulta = ejbPredialCuatro.armarConsultaFacturadosPredial(compania,
                            Integer.parseInt(anioInicial),
                            Integer.parseInt(anioFinal));

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            consulta = null;
            return consulta;
        }

        return consulta;
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que arma el informe en formato excel. Estableciendo las
     * celdas, los títulos y los datos que serán mostrados en el
     * reporte.
     */
    public void generarExcel() {
        String strSql = ejecutarFuncion();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            String nombre = "INFORME DE FACTURACIÓN IMPUESTO PREDIAL";

            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            Sheet sheet = workbook.getSheet("Report");
            sheet.shiftRows(0, sheet.getLastRowNum(), 6);
            sheet.setColumnWidth(3, 5500);
            sheet.createFreezePane(0, 7);

            Font font = workbook.createFont();
            font.setFontName("Calibri");
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setFont(font);

            Font font2 = workbook.createFont();
            font2.setFontName("Calibri");
            font2.setFontHeightInPoints((short) 11);
            font2.setBold(true);

            CellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(CellStyle.ALIGN_LEFT);
            style2.setFont(font2);

            Font font3 = workbook.createFont();
            font3.setFontName("SansSerif");
            font3.setFontHeightInPoints((short) 10);

            CellStyle style3 = workbook.createCellStyle();
            style3.setAlignment(CellStyle.ALIGN_RIGHT);
            style3.setFont(font3);

            CellReference celdaTitulo1Ini = new CellReference(0, 0);
            String titulo1Ini = celdaTitulo1Ini.formatAsString();
            CellReference celdaTitulo1Fin = new CellReference(0, 3);
            String titulo1Fin = celdaTitulo1Fin.formatAsString();

            CellRangeAddress region = CellRangeAddress
                            .valueOf("" + titulo1Ini + ":" + titulo1Fin);
            sheet.addMergedRegion(region);

            CellReference celdaTitulo2Ini = new CellReference(1, 0);
            String titulo2Ini = celdaTitulo2Ini.formatAsString();
            CellReference celdaTitulo2Fin = new CellReference(1, 3);
            String titulo2Fin = celdaTitulo2Fin.formatAsString();
            CellRangeAddress region2 = CellRangeAddress
                            .valueOf("" + titulo2Ini + ":" + titulo2Fin);
            sheet.addMergedRegion(region2);

            CellReference celdaTitulo3Ini = new CellReference(2, 0);
            String titulo3Ini = celdaTitulo3Ini.formatAsString();
            CellReference celdaTitulo3Fin = new CellReference(2, 3);
            String titulo3Fin = celdaTitulo3Fin.formatAsString();
            CellRangeAddress region3 = CellRangeAddress
                            .valueOf("" + titulo3Ini + ":" + titulo3Fin);
            sheet.addMergedRegion(region3);

            CellReference celdaTitulo4Ini = new CellReference(4, 0);
            String titulo4Ini = celdaTitulo4Ini.formatAsString();
            CellReference celdaTitulo4Fin = new CellReference(4, 3);
            String titulo4Fin = celdaTitulo4Fin.formatAsString();
            CellRangeAddress region4 = CellRangeAddress
                            .valueOf("" + titulo4Ini + ":" + titulo4Fin);
            sheet.addMergedRegion(region4);

            Cell cell1 = sheet.createRow(0).createCell(0);
            cell1.setCellValue(parTituloUnoOficios);
            cell1.setCellStyle(style);
            cell1.getStringCellValue();

            Cell cell2 = sheet.createRow(1).createCell(0);
            cell2.setCellValue(parTituloDosOficios);
            cell2.setCellStyle(style);
            cell2.getStringCellValue();

            Cell cell3 = sheet.createRow(2).createCell(0);
            cell3.setCellValue(parTituloTresOficios);
            cell3.setCellStyle(style);
            cell3.getStringCellValue();

            Cell cell4 = sheet.createRow(4).createCell(0);
            cell4.setCellValue(
                            "INFORME IMPUESTO PREDIAL - FACTURADOS - Departamento Nacional de Planeacion (DNP)");
            cell4.setCellStyle(style);
            cell4.getStringCellValue();

            Row r = sheet.createRow(6);

            Cell cell5 = r.createCell(0);
            cell5.setCellValue("AÑO");
            cell5.setCellStyle(style2);
            cell5.getStringCellValue();

            Cell cell6 = r.createCell(1);
            cell6.setCellValue("PREDIOS URBANOS");
            cell6.setCellStyle(style2);
            cell6.getStringCellValue();

            Cell cell7 = r.createCell(2);
            cell7.setCellValue("PREDIOS RURALES");
            cell7.setCellStyle(style2);
            cell7.getStringCellValue();

            Cell cell8 = r.createCell(3);
            cell8.setCellValue("TOTAL CARTERA");
            cell8.setCellStyle(style2);
            cell8.getStringCellValue();

            for (int i = 7; i <= sheet.getLastRowNum(); i++) {
                Cell cellTotalesFormula = sheet.getRow(i).createCell(3);
                cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
                CellReference cellRefIni = new CellReference(i, 1);
                CellReference cellRefFin = new CellReference(i, 2);
                String celdaIni = cellRefIni.formatAsString();
                String celdaFin = cellRefFin.formatAsString();
                cellTotalesFormula.setCellFormula(
                                "SUM(" + celdaIni + ":" + celdaFin + ")");
                style3.setDataFormat(workbook.createDataFormat()
                                .getFormat("#,##0.00"));

                cellTotalesFormula.setCellStyle(style3);

            }

            workbook.write(out);
            out.close();

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(new ByteArrayInputStream(
                                            out.toByteArray()),
                                            nombre + ".xls");
            workbook.close();

        }
        catch (JRException | IOException | DRException | SQLException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnioInicial
     * 
     */
    public void cambiarAnioInicial() {
        cargarListaAnioFinal();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anioInicial
     * 
     * @return anioInicial
     */
    public String getAnioInicial() {
        return anioInicial;
    }

    /**
     * Asigna la variable anioInicial
     * 
     * @param anioInicial
     * Variable a asignar en anioInicial
     */
    public void setAnioInicial(String anioInicial) {
        this.anioInicial = anioInicial;
    }

    /**
     * Retorna la variable anioFinal
     * 
     * @return anioFinal
     */
    public String getAnioFinal() {
        return anioFinal;
    }

    /**
     * Asigna la variable anioFinal
     * 
     * @param anioFinal
     * Variable a asignar en anioFinal
     */
    public void setAnioFinal(String anioFinal) {
        this.anioFinal = anioFinal;
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
     * Retorna la lista listaAnioInicial
     * 
     * @return listaAnioInicial
     */
    public List<Registro> getListaAnioInicial() {
        return listaAnioInicial;
    }

    /**
     * Asigna la lista listaAnioInicial
     * 
     * @param listaAnioInicial
     * Variable a asignar en listaAnioInicial
     */
    public void setListaAnioInicial(List<Registro> listaAnioInicial) {
        this.listaAnioInicial = listaAnioInicial;
    }

    /**
     * Retorna la lista listaAnioFinal
     * 
     * @return listaAnioFinal
     */
    public List<Registro> getListaAnioFinal() {
        return listaAnioFinal;
    }

    /**
     * Asigna la lista listaAnioFinal
     * 
     * @param listaAnioFinal
     * Variable a asignar en listaAnioFinal
     */
    public void setListaAnioFinal(List<Registro> listaAnioFinal) {
        this.listaAnioFinal = listaAnioFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
