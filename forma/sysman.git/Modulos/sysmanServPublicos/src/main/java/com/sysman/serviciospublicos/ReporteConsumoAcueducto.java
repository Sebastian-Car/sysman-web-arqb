/*-
 * ReporteConsumoAcueducto.java
 *
 * 1.0
 *
 * 17/11/2016
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
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ReporteConsumoAcueductoEnum;
import com.sysman.serviciospublicos.enums.ReporteConsumoAcueductoUrlEnum;
import com.sysman.util.ContenedorArchivo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite generar el reporte Valor consumo acueducto
 * para un rango de anios y periodos seleccionados
 *
 * @version 1.0, 17/11/2016
 * @author jlozano
 *
 * @version 2, 13/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @version 3, 15/06/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 */

@ManagedBean
@ViewScoped
public class ReporteConsumoAcueducto extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del ciclo seleccionado
     */
    private String ciclo;
    /**
     * Atributo que almacena el valor del anio inicial seleccionado
     */
    private String anoInicial;
    /**
     * Atributo que almacena el valor del periodo inicial seleccionado
     */
    private String periodoInicial;
    /**
     * Atributo que almacena el valor del anio final seleccionado
     */
    private String anoFinal;
    /**
     * Atributo que almacena el valor del periodo final seleccionado
     */
    private String periodoFinal;
    /**
     * Atributo que almacena el valor del nombre de la hoja
     */
    private String nombreHoja;
    /**
     * Atributo que almacena el valor del nombre del archivo
     * seleccionado
     */
    private String nombreArchivo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos selectorPlantilla y funciona como contenedor del
     * archivo que se debe guardar
     */
    private ContenedorArchivo contArchivoselectorPlantilla;
    /**
     * Constante para el literal "PERIODO"
     */
    private static final String PERIODO = "PERIODO";
    /**
     * Constante para el literal "2. Tar. alc."
     */
    private static final String NOMBRE_HOJA = "7. Cons. ac. $ .";
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena los anios disponibles
     */
    private List<Registro> listaAnoInicial;
    /**
     * Lista que almacena los periodos disponibles
     */
    private List<Registro> listaPeriodoInicial;
    /**
     * Lista que almacena los anios disponibles
     */
    private List<Registro> listaAnoFinal;
    /**
     * Lista que almacena los periodos disponibles
     */
    private List<Registro> listaPeriodoFinal;
    /**
     * Lista de hojas disponibles para el archivo seleccionado
     */
    private List<Registro> listaNombreHoja;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ReporteConsumoAcueducto
     */
    public ReporteConsumoAcueducto() {
        super();
        compania = SessionUtil.getCompania();
        anoInicial = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        anoFinal = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.YEAR));
        periodoInicial = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.MONTH)
            + 1);
        periodoFinal = String.valueOf(GregorianCalendar.getInstance()
                        .get(GregorianCalendar.MONTH)
            + 1);
        nombreArchivo = "sol_tarifas_2003_ac_alc";
        contArchivoselectorPlantilla = new ContenedorArchivo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REPORTE_CONSUMO_ACUEDUCTO
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
        cargarListaAnoInicial();
        cargarListaPeriodoInicial();
        cargarListaAnoFinal();
        cargarListaPeriodoFinal();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnoInicial
     *
     */
    public void cargarListaAnoInicial() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteConsumoAcueductoUrlEnum.URL7404
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
     * Carga la lista listaPeriodoInicial
     *
     */
    public void cargarListaPeriodoInicial() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.ANO.getName(),anoInicial);
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteConsumoAcueductoUrlEnum.URL8027
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
     * Carga la lista listaAnoFinal
     *
     */
    public void cargarListaAnoFinal() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.ANO.getName(),anoInicial);

            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteConsumoAcueductoUrlEnum.URL8802
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
     * Carga la lista listaPeriodoFinal
     *
     */
    public void cargarListaPeriodoFinal() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(ReporteConsumoAcueductoEnum.PARAM0.getValue(),anoFinal);
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.MES.getName(),periodoInicial);
            
            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteConsumoAcueductoUrlEnum.URL9418
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista de hojas que contien el archivo Excel
     * seleccionado
     */
    public void cargarListaNombreHoja() {
        try {
            listaNombreHoja = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteConsumoAcueductoUrlEnum.URL10334
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
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     * Genera el reporte en formato Excel
     *
     */
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    public String obtenerNombreMes(String mes) {
        String res = "";
        try {
            res = ejbSysmanUtilRemote.mostrarNombreDeMes(Integer.parseInt(mes));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return res;
    }

    private void generarExcel() {
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(ReporteConsumoAcueductoEnum.PARAM1.getValue(),anoInicial);
        param.put(ReporteConsumoAcueductoEnum.PARAM0.getValue(),anoFinal);
        param.put(ReporteConsumoAcueductoEnum.PARAM2.getValue(),periodoInicial);
        param.put(ReporteConsumoAcueductoEnum.PARAM3.getValue(),periodoFinal);
        
        List<Registro> listaPivot=null;
        try {
            listaPivot = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteConsumoAcueductoUrlEnum.URL14562
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e2) {
            logger.error(e2.getMessage(), e2);
            JsfUtil.agregarMensajeError(e2.getMessage());
        }
        
        
        String inPivot = "";
        StringBuilder aux = new StringBuilder();
        for (Registro registro : listaPivot) {
            aux.append("'" + registro.getCampos().get(PERIODO) + "' AS \""
                + registro.getCampos().get(PERIODO) + "\",");
        }
        inPivot = aux.toString();
        if (inPivot.length() > 0) {
            inPivot = inPivot.substring(0, inPivot.length() - 1);
        }
        HashMap<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("anoInicial", anoInicial);
        reemplazos.put("periodoInicial", periodoInicial);
        reemplazos.put("anoFinal", anoFinal);
        reemplazos.put("periodoFinal", periodoFinal);
        reemplazos.put("inPivot", inPivot);

        String strSql = Reporteador.resuelveConsulta(
                        "800067InformeConsumoAcueducto",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos);
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoselectorPlantilla.getArchivo());) {

            Workbook workbook = new HSSFWorkbook(fileIs);
            Sheet sheet = workbook.getSheet(nombreHoja);
            sheet.shiftRows(0, sheet.getLastRowNum(), 7);

            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);

            CellStyle style = workbook.createCellStyle();
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setLeftBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setRightBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setTopBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            
            
            CellStyle style2 = workbook.createCellStyle();   
            style2.setAlignment(CellStyle.ALIGN_CENTER);
            style2.setBorderBottom(CellStyle.BORDER_THIN);
            style2.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style2.setBorderLeft(CellStyle.BORDER_THIN);
            style2.setLeftBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style2.setBorderRight(CellStyle.BORDER_THIN);
            style2.setRightBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            style2.setBorderTop(CellStyle.BORDER_THIN);
            style2.setTopBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
            
            style2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            style2.setFillPattern(CellStyle.SOLID_FOREGROUND);
            
            
            CellStyle style4 = workbook.createCellStyle();   
            style4.setAlignment(CellStyle.ALIGN_LEFT);
            style4.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
            style4.setFillPattern(CellStyle.SOLID_FOREGROUND);
            
            CellStyle style3 = workbook.createCellStyle();  
            style3.setAlignment(CellStyle.ALIGN_CENTER);
            
            Font font2 = workbook.createFont();
            font2.setFontName("Arial");
            font2.setFontHeightInPoints((short) 12);
            font2.setBold(true);
            style2.setFont(font2);
            style3.setFont(font2);
            style4.setFont(font2);
            
            Font font = workbook.createFont();
            font.setFontName("Arial");
            font.setBold(false);
            style.setFont(font);
            

            CellReference cellRefIniTitulo2 =  new CellReference(1, 0);
            StringBuilder celdaIniTitulo2 = new StringBuilder(cellRefIniTitulo2.formatAsString());
            CellReference cellRefFinTitulo2 = new CellReference(1,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            StringBuilder celdaFinTitulo2 = new StringBuilder(cellRefFinTitulo2.formatAsString());
            CellRangeAddress regionTitulo = CellRangeAddress.valueOf(
                             celdaIniTitulo2.append(":").append(celdaFinTitulo2.toString()).toString());
            sheet.addMergedRegion(regionTitulo);

            CellReference cellRefIniTitulo =  new CellReference(2, 0);
            StringBuilder celdaIniTitulo = new StringBuilder(cellRefIniTitulo.formatAsString());
            CellReference cellRefFinTitulo = new CellReference(2,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            StringBuilder celdaFinTitulo = new StringBuilder(cellRefFinTitulo.formatAsString());
            CellRangeAddress region = CellRangeAddress.valueOf(
                             celdaIniTitulo.append(":").append(celdaFinTitulo.toString()).toString());
            sheet.addMergedRegion(region);
            
            CellReference cellRefIniCorriente =  new CellReference(3, 0);
            StringBuilder celdaIniCorriente = new StringBuilder(cellRefIniCorriente.formatAsString());
            CellReference cellRefFinCorriente = new CellReference(3,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            StringBuilder celdaFinCorriente = new StringBuilder(cellRefFinCorriente.formatAsString());
            CellRangeAddress regionCorriente = CellRangeAddress.valueOf(
                             celdaIniCorriente.append(":").append(celdaFinCorriente.toString()).toString());
            sheet.addMergedRegion(regionCorriente);

            CellReference cellRefIniTitulo1 = new CellReference(5, 0);
            StringBuilder celdaIniTitulo1 = new StringBuilder(cellRefIniTitulo1.formatAsString());
            CellReference cellRefFinTitulo1 = new CellReference(5,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            StringBuilder celdaFinTitulo1 = new StringBuilder(cellRefFinTitulo1.formatAsString());
            CellRangeAddress region1 = CellRangeAddress.valueOf(
                            celdaIniTitulo1.append(":").append(celdaFinTitulo1.toString()).toString());
            sheet.addMergedRegion(region1);

            CellReference cellRefIniTitulo3 = new CellReference(6, 0);
            StringBuilder celdaIniTitulo3 = new StringBuilder(cellRefIniTitulo3.formatAsString());
            CellReference cellRefFinTitulo3 = new CellReference(6,
                            Math.max(sheet.getRow(7).getLastCellNum(), 0)
                                - 1);
            StringBuilder celdaFinTitulo3 =  new StringBuilder(cellRefFinTitulo3.formatAsString());
            CellRangeAddress region2 = CellRangeAddress.valueOf(
                            celdaIniTitulo3.append(":").append(celdaFinTitulo3.toString()).toString());
            sheet.addMergedRegion(region2);

            
             
            Cell cell = sheet.getRow(5).createCell(0);
            StringBuilder cadena= new StringBuilder();
            cadena.append(idioma.getString("TG_CIUDAD2"))
                  .append(" ")
                  .append(SessionUtil.getCompaniaIngreso().getCiudad())
                  .append(StringUtils.repeat(" ", 20))
                  .append(idioma.getString("TG_DEPARTAMENTO"))
                  .append(": ").append(SessionUtil.getCompaniaIngreso().getDepartamento());
            cell.setCellValue(cadena.toString());
            cell.setCellStyle(style3);
            
            Cell cell2 = sheet.getRow(6).createCell(0);
            StringBuilder cadenaAux= new StringBuilder();
            cadenaAux.append(idioma.getString("TB_TB2055")).append(" ").append(SessionUtil.getCompaniaIngreso().getNombre());
            cell2.setCellValue(cadenaAux.toString());
            cell2.setCellStyle(style3);
            
            Cell cell4 = sheet.getRow(1).createCell(0);
            cell4.setCellValue("SERVICIO DE ACUEDUCTO");
            cell4.setCellStyle(style3);
            
            Cell cell5 = sheet.getRow(3).createCell(0);
            cell5.setCellValue("$ Corrientes");
            cell5.setCellStyle(style3);
            
            
            Cell cellCuadro = sheet.getRow(0).createCell(0);
            cellCuadro.setCellValue("Cuadro No. 01 ");
            cellCuadro.setCellStyle(style4);
            
            StringBuilder cadenaAux2= new StringBuilder();
            cadenaAux2.append(idioma.getString("TB_TB2056")).append(" ").append(anoInicial.equals(anoFinal)? anoInicial : anoInicial + "-" + anoFinal);
            Cell cell3 = sheet.getRow(2).createCell(0);
            cell3.setCellValue(cadenaAux2.toString());
            cell3.setCellStyle(style3);
            
            Row encabezado = sheet.getRow(7);
            int index = 1;
            crearEncabezadoExcel(listaPivot, encabezado, index, style2);
            
            int salto=0;
            int referencia=9;
            for (Registro registro : rs) {
                Row row;
                if(registro.getIndice()==26 && rs.size() == 28){
                   referencia+=4;
                   salto=0;
                }else if(registro.getIndice()==26){
                    referencia+=1;
                    salto=2;
                }
                if(salto==4){
                    referencia+=registro.getIndice()<=26?3:2;
                    row = sheet.getRow(referencia);
                    salto=0;
                }else{
                    referencia+=1;
                    row = sheet.getRow(referencia);
                }
                index = 1;
                for (Registro reg : listaPivot) {
                    Cell cellAux = row.createCell(index);
                    Object value = registro.getCampos()
                                    .get(reg.getCampos().get(PERIODO).toString()) != null
                                        ? registro.getCampos()
                                                        .get(reg.getCampos()
                                                                        .get(PERIODO).toString())
                                        : 0;
                    cellAux.setCellValue(Double.parseDouble(value.toString()));
                    style.setDataFormat(workbook.createDataFormat()
                                    .getFormat("#,##0.00"));
                    cellAux.setCellStyle(style);
                    index++;
                    
                }
                salto++;
                Cell cellTotal = row.createCell(index);
                CellReference cellRefIni = new CellReference(
                                referencia, 1);
                CellReference cellRefFin = new CellReference(
                                referencia, index - 1);
                String celdaIni = cellRefIni.formatAsString();
                String celdaFin = cellRefFin.formatAsString();
                cellTotal.setCellFormula(
                                "SUM(" + celdaIni + ":" + celdaFin + ")");
                cellTotal.setCellStyle(style);
                
                
            }


            workbook.setActiveSheet(workbook.getSheetIndex(sheet));
            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            contArchivoselectorPlantilla.getArchivo()
                                            .getName());
            fileIs.close();
        }
        catch (IOException | JRException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    /**
     * Crea las columnas del emcabezado del reporte Excel
     *
     * @param lista
     * Lista de elementos del encabezado
     * @param encabezado
     * Fila delencabezado
     * @param inicio
     * posicion inicial de le los encabezados
     * @param style
     * Estilo de las celdas de los encabezados
     */
    public void crearEncabezadoExcel(List<Registro> lista, Row encabezado,
        int inicio, CellStyle style) {
        int index = inicio;
        for (Registro registro : lista) {
            Cell cell = encabezado.createCell(index);

            StringBuilder nombreMes = new StringBuilder();
            nombreMes.append(
                            obtenerNombreMes((registro
                                            .getCampos()
                                            .get(PERIODO).toString()).substring(
                                                            4,
                                                            6)));

            cell.setCellValue(nombreMes.append("-").append((registro
                            .getCampos()
                            .get(PERIODO).toString()).substring(0,4)).toString());
            cell.setCellStyle(style);
            index++;
        }
        Cell total = encabezado.createCell(index);
        total.setCellValue(idioma.getString("TG_TOTAL10"));
        total.setCellStyle(style);
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
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoselectorPlantilla.getArchivo());) {
            workbook = new HSSFWorkbook(fileIs);
            cargarNombreHojas(workbook);
            for (int i = 0; i < listaNombreHoja.size(); i++) {
                if (NOMBRE_HOJA.equals(listaNombreHoja.get(i)
                                .getCampos().get("NOMBREHOJA"))) {
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

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control AnoInicial
     *
     * Recarga la lista periodoInicial filtrando por el anio inicial
     * seleccionado
     *
     */
    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoInicial();
        anoFinal=null;
        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial
     *
     * Recarga la lista periodoFinal filtrando por el anio final y
     * periodo inicial seleccionados
     *
     */
    public void cambiarPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoFinal
     *
     * Recarga la lista periodoFinal filtrando por el anio final y
     * periodo inicial seleccionados
     *
     */
    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
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
            Sheet sheet = workbook.getSheetAt(i);
            String hoja = sheet.getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put("NOMBREHOJA", hoja);
            listaNombreHoja.add(reg);
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
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
     * Retorna la variable anoInicial
     *
     * @return anoInicial
     */
    public String getAnoInicial() {
        return anoInicial;
    }

    /**
     * Asigna la variable anoInicial
     *
     * @param anoInicial
     * Variable a asignar en anoInicial
     */
    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    /**
     * Retorna la variable periodoInicial
     *
     * @return periodoInicial
     */
    public String getPeriodoInicial() {
        return periodoInicial;
    }

    /**
     * Asigna la variable periodoInicial
     *
     * @param periodoInicial
     * Variable a asignar en periodoInicial
     */
    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    /**
     * Retorna la variable anoFinal
     *
     * @return anoFinal
     */
    public String getAnoFinal() {
        return anoFinal;
    }

    /**
     * Asigna la variable anoFinal
     *
     * @param anoFinal
     * Variable a asignar en anoFinal
     */
    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    /**
     * Retorna la variable periodoFinal
     *
     * @return periodoFinal
     */
    public String getPeriodoFinal() {
        return periodoFinal;
    }

    /**
     * Asigna la variable periodoFinal
     *
     * @param periodoFinal
     * Variable a asignar en periodoFinal
     */
    public void setPeriodoFinal(String periodoFinal) {
        this.periodoFinal = periodoFinal;
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
     * Retorna la variable nombreArchivo
     *
     * @return nombreArchivo
     */
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    /**
     * Asigna la variable nombreArchivo
     *
     * @param nombreArchivo
     * Variable a asignar en nombreArchivo
     */
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoselectorPlantilla
     *
     * @return contArchivoselectorPlantilla
     */
    public ContenedorArchivo getContArchivoselectorPlantilla() {
        return contArchivoselectorPlantilla;
    }

    /**
     * Asigna el objeto contArchivoselectorPlantilla
     *
     * @param contArchivoselectorPlantilla
     * Variable a asignar en contArchivoselectorPlantilla
     */
    public void setContArchivoselectorPlantilla(
        ContenedorArchivo contArchivoselectorPlantilla) {
        this.contArchivoselectorPlantilla = contArchivoselectorPlantilla;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoInicial
     *
     * @return listaAnoInicial
     */
    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    /**
     * Asigna la lista listaAnoInicial
     *
     * @param listaAnoInicial
     * Variable a asignar en listaAnoInicial
     */
    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    /**
     * Retorna la lista listaPeriodoInicial
     *
     * @return listaPeriodoInicial
     */
    public List<Registro> getListaPeriodoInicial() {
        return listaPeriodoInicial;
    }

    /**
     * Asigna la lista listaPeriodoInicial
     *
     * @param listaPeriodoInicial
     * Variable a asignar en listaPeriodoInicial
     */
    public void setListaPeriodoInicial(List<Registro> listaPeriodoInicial) {
        this.listaPeriodoInicial = listaPeriodoInicial;
    }

    /**
     * Retorna la lista listaAnoFinal
     *
     * @return listaAnoFinal
     */
    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    /**
     * Asigna la lista listaAnoFinal
     *
     * @param listaAnoFinal
     * Variable a asignar en listaAnoFinal
     */
    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
     *
     * @return listaPeriodoFinal
     */
    public List<Registro> getListaPeriodoFinal() {
        return listaPeriodoFinal;
    }

    /**
     * Asigna la lista listaPeriodoFinal
     *
     * @param listaPeriodoFinal
     * Variable a asignar en listaPeriodoFinal
     */
    public void setListaPeriodoFinal(List<Registro> listaPeriodoFinal) {
        this.listaPeriodoFinal = listaPeriodoFinal;
    }

    /**
     * Retorna la lista listaPeriodoFinal
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
