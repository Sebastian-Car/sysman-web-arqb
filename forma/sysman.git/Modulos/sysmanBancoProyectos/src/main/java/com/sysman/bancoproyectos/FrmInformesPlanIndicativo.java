package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.FrmInformesPlanIndicativoUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 28/08/2015
 * 
 * @author eamaya
 * @version 2.0, 14/09/2017, Proceso de Refactoring DSS,Manejo de
 * EJBs,cambio numero de formulario por enum y creacion de Textos en
 * Bean
 * 
 */
@ManagedBean
@ViewScoped

public class FrmInformesPlanIndicativo extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String dptoCompania;
    private final String ciudadCompania;
    private String vigencia;
    private String informe;
    private String nivel;
    private String periodoGub;
    private List<Registro> listaVIGENCIAINICIAL;
    private List<Registro> listaNivel;
    private StreamedContent archivoDescarga;
    private static final String NIVELS = "NIVEL";
    private static final String MSM = "MSM_TRANS_INTERRUMPIDA";
    private static final String TODAS = "TODAS";
    private static final String TAHOMA = "Tahoma";
    private static final String CELNOMBRE = "VIGENCIA ";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbBancoProyectoTresRemote ejbBancoPRoyectosTres;

    /**
     * Creates a new instance of FrmInformesPlanIndicativo
     */
    public FrmInformesPlanIndicativo() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        dptoCompania = SessionUtil.getCompaniaIngreso().getDepartamento();
        ciudadCompania = SessionUtil.getCompaniaIngreso().getCiudad();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORMES_PLAN_INDICATIVO
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmInformesPlanIndicativo.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init() {
        cargarVigencia();
        informe = "1";
        cargarListaVIGENCIAINICIAL();
        cargarListanivel();
        abrirFormulario();
    }

    public void cargarListaVIGENCIAINICIAL() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaVIGENCIAINICIAL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInformesPlanIndicativoUrlEnum.URL4040
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarListaPeriodo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaVIGENCIAINICIAL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInformesPlanIndicativoUrlEnum.URL4512
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListanivel() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        try {
            listaNivel = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInformesPlanIndicativoUrlEnum.URL4842
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private HSSFCell nombreCelda(HSSFCell cellMetas, int i) {

        if (vigencia.equals(TODAS)) {
            cellMetas.setCellValue(
                            CELNOMBRE + (Integer.parseInt(periodoGub) + i));
        }
        else {
            cellMetas.setCellValue(
                            CELNOMBRE + (Integer.parseInt(vigencia) + i));
        }
        return cellMetas;
    }

    private void generarExcel() {
        // Ejecuta procedimiento para generar el informe
        List<Registro> lista = null;
        String planDesarrollo = "";
        String aux;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.VIGENCIA.getName(), periodoGub);

            lista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInformesPlanIndicativoUrlEnum.URL7182
                                                                            .getValue())
                                            .getUrl(), param));

            planDesarrollo = ejbSysmanUtl.consultarParametro(compania,
                            "PLAN DE DESARROLLO", modulo, new Date(), false);

            // Asigna la posicion inicial y final de las columnas de
            // vigencias
            int coliniciall = 1;
            int colfinall = coliniciall + lista.size() + 1;
            int columnaFin = 0;
            int vigencias = 1;
            StringBuilder niveles = new StringBuilder("");
            int cantidadNiveles = lista.size();
            int vigenciaAnios = 1;
            boolean conTotal = false;

            // Asigna el numero de vigencias a partir de la vigencia o
            // el informe
            if (TODAS.equals(vigencia) || "3".equals(informe)) {
                vigencias = 8;
            }
            if ("2".equals(informe)) {

                if (vigencias == 1) {
                    columnaFin = 8;
                }
                else {
                    columnaFin = vigencias * 5 - 1;
                }
            }
            if ("3".equals(informe)) {
                columnaFin = 8;
            }

            // calcular las vigencias
            if (TODAS.equals(vigencia) || "3".equals(informe)) {
                vigenciaAnios = 4;
                conTotal = true;
            }

            String nombreArchivo;
            Calendar calendario = new GregorianCalendar();
            if (vigencia.equals(TODAS) || "2".equals(informe)) {
                nombreArchivo = "SEGUIMIENTO METAS DE PRODUCTO_";
            }
            else {
                nombreArchivo = "RESUMEN CUATRENIO_";
            }
            nombreArchivo = nombreArchivo
                + SysmanFunciones.convertirAFechaCadena(new Date(), "ddMMYYYY")
                + "_"
                + calendario.get(Calendar.HOUR_OF_DAY)
                + calendario.get(Calendar.MINUTE)
                + calendario.get(Calendar.SECOND) + ".xls";

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook
                            .createSheet("SEGUIMIENTO METAS DE PRODUCTO");

            // Define el formato del texto de las celdas
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 7);
            font.setFontName(TAHOMA);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle styleTitulo = workbook.createCellStyle();
            styleTitulo.setFont(font);
            styleTitulo.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            styleTitulo.setVerticalAlignment(HSSFCellStyle.VERTICAL_JUSTIFY);

            HSSFFont fontDetalle = workbook.createFont();
            fontDetalle.setFontHeightInPoints((short) 10);
            fontDetalle.setFontName(TAHOMA);

            HSSFCellStyle styleDetalle = workbook.createCellStyle();
            styleDetalle.setFont(fontDetalle);
            styleDetalle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setBorderTop(HSSFCellStyle.BORDER_THIN);

            HSSFRow rowTituloDetalle = sheet.createRow(1);
            rowTituloDetalle.setHeightInPoints(60);
            HSSFCell cellTitulo1 = rowTituloDetalle.createCell(1);
            cellTitulo1.setCellStyle(styleTitulo);
            cellTitulo1.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TT_LB24694"), " ",
                            planDesarrollo));
            CellUtil.setAlignment(cellTitulo1, workbook,
                            CellStyle.ALIGN_CENTER);

            // Region1 del titulo del detalle
            CellRangeAddress range1 = new CellRangeAddress(1, 1, coliniciall,
                            colfinall);
            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            sheet.addMergedRegion(range1);
            sheet.createFreezePane(9, 6);

            HSSFCell cellTitulo2 = rowTituloDetalle.createCell(colfinall + 1);
            cellTitulo2.setCellStyle(styleTitulo);
            cellTitulo2.setCellType(Cell.CELL_TYPE_STRING);
            cellTitulo2.setCellValue(idioma.getString("TB_TB3590"));
            CellUtil.setAlignment(cellTitulo2, workbook,
                            CellStyle.ALIGN_CENTER);

            CellRangeAddress range2;
            // Rango2 del titulo del detalle
            if ("2".equals(informe)) {
                range2 = new CellRangeAddress(1, 1, colfinall + 1,
                                colfinall + columnaFin);
            }
            else {
                range2 = new CellRangeAddress(1, 1, colfinall + 1,
                                colfinall + columnaFin - 1);
            }

            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            sheet.addMergedRegion(range2);

            HSSFRow rowTitulo = sheet.createRow(3);
            HSSFCell cellPlan = rowTitulo.createCell(1);
            cellPlan.setCellStyle(styleTitulo);
            cellPlan.setCellValue(idioma.getString("TT_LB5023"));
            CellUtil.setAlignment(cellPlan, workbook, CellStyle.ALIGN_CENTER);

            // Rango3 del titulo del detalle
            CellRangeAddress range3 = new CellRangeAddress(3, 4, coliniciall,
                            colfinall);
            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range3,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range3,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range3,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range3,
                            sheet, workbook);
            sheet.addMergedRegion(range3);

            HSSFRow rowTitulo1 = sheet.createRow(4);

            int auxCol1 = colfinall + 1;
            int auxCol2 = colfinall + 8;

            if ("2".equals(informe)) {
                for (int i = 0; i < vigenciaAnios; i++) {
                    HSSFCell cellMetas = rowTitulo.createCell(auxCol1);
                    cellMetas.setCellStyle(styleTitulo);
                    cellMetas = nombreCelda(cellMetas, i);
                    CellUtil.setAlignment(cellMetas, workbook,
                                    CellStyle.ALIGN_CENTER);

                    CellRangeAddress range4 = new CellRangeAddress(3, 3,
                                    auxCol1, auxCol2);
                    HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                    range4, sheet, workbook);
                    HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,
                                    range4, sheet, workbook);
                    HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,
                                    range4, sheet, workbook);
                    HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,
                                    range4, sheet, workbook);
                    sheet.addMergedRegion(range4);

                    HSSFCell cellSeguimientoF = rowTitulo1.createCell(auxCol1);
                    cellSeguimientoF.setCellStyle(styleTitulo);
                    cellSeguimientoF.setCellValue(
                                    idioma.getString("TB_TB3594"));
                    CellUtil.setAlignment(cellSeguimientoF, workbook,
                                    CellStyle.ALIGN_CENTER);

                    CellRangeAddress range5 = new CellRangeAddress(4, 4,
                                    auxCol1, auxCol1 + 3);
                    HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                    range5, sheet, workbook);
                    HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,
                                    range5, sheet, workbook);
                    HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,
                                    range5, sheet, workbook);
                    HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,
                                    range5, sheet, workbook);
                    sheet.addMergedRegion(range5);

                    HSSFCell cellSeguimientoFin = rowTitulo1
                                    .createCell(auxCol1 + 4);
                    cellSeguimientoFin.setCellStyle(styleTitulo);
                    cellSeguimientoFin.setCellValue(
                                    idioma.getString("TB_TB3592"));
                    CellUtil.setAlignment(cellSeguimientoFin, workbook,
                                    CellStyle.ALIGN_CENTER);

                    CellRangeAddress range6 = new CellRangeAddress(4, 4,
                                    auxCol1 + 4, auxCol2);
                    HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                    range6, sheet, workbook);
                    HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,
                                    range6, sheet, workbook);
                    HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,
                                    range6, sheet, workbook);
                    HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,
                                    range6, sheet, workbook);
                    sheet.addMergedRegion(range6);

                    auxCol1 = auxCol2 + 1;
                    auxCol2 = auxCol2 + 8;
                }

            }

            if (conTotal) {
                HSSFCell cellMetas = rowTitulo.createCell(auxCol1);
                cellMetas.setCellStyle(styleTitulo);
                cellMetas.setCellValue(idioma.getString("TB_TB3593"));
                CellUtil.setAlignment(cellMetas, workbook,
                                CellStyle.ALIGN_CENTER);

                CellRangeAddress range4 = new CellRangeAddress(3, 3, auxCol1,
                                auxCol2 - 1);
                HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                range4, sheet, workbook);
                HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range4,
                                sheet, workbook);
                HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range4,
                                sheet, workbook);
                HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range4,
                                sheet, workbook);
                sheet.addMergedRegion(range4);

                HSSFCell cellSeguimientoF = rowTitulo1.createCell(auxCol1);
                cellSeguimientoF.setCellStyle(styleTitulo);
                cellSeguimientoF.setCellValue(idioma.getString("TB_TB3594"));
                CellUtil.setAlignment(cellSeguimientoF, workbook,
                                CellStyle.ALIGN_CENTER);

                CellRangeAddress range5 = new CellRangeAddress(4, 4, auxCol1,
                                auxCol1 + 2);
                HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                range5, sheet, workbook);
                HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range5,
                                sheet, workbook);
                HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range5,
                                sheet, workbook);
                HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range5,
                                sheet, workbook);
                sheet.addMergedRegion(range5);

                HSSFCell cellSeguimientoFin = rowTitulo1
                                .createCell(auxCol1 + 3);
                cellSeguimientoFin.setCellStyle(styleTitulo);
                cellSeguimientoFin.setCellValue(idioma.getString("TB_TB3592"));
                CellUtil.setAlignment(cellSeguimientoFin, workbook,
                                CellStyle.ALIGN_CENTER);

                CellRangeAddress range6 = new CellRangeAddress(4, 4,
                                auxCol1 + 3, auxCol2 - 1);
                HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                range6, sheet, workbook);
                HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range6,
                                sheet, workbook);
                HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range6,
                                sheet, workbook);
                HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range6,
                                sheet, workbook);
                sheet.addMergedRegion(range6);

            }

            // Rango4 vigencia 1
            // Agrega las columnas de las vigencias a partir de la
            // consulta
            // inicial
            HSSFRow rowDetalle1 = sheet.createRow(5);
            rowDetalle1.setHeightInPoints(50);
            int i;
            for (i = 0; i < cantidadNiveles; i++) {
                HSSFCell cellD1 = rowDetalle1.createCell(1 + i);
                cellD1.setCellStyle(styleTitulo);
                cellD1.setCellValue(String.valueOf(
                                lista.get(i).getCampos().get(idioma.getString(
                                                "TG_DESCRIPCION2"))));
                CellUtil.setAlignment(cellD1, workbook, CellStyle.ALIGN_CENTER);
                sheet.setColumnWidth(i + 1, 4000);

            }
            HSSFCell cellD2 = rowDetalle1.createCell(1 + i);
            cellD2.setCellStyle(styleTitulo);
            cellD2.setCellValue(idioma.getString("TB_TB3595"));
            CellUtil.setAlignment(cellD2, workbook, CellStyle.ALIGN_CENTER);
            i++;
            sheet.setColumnWidth(i, 4000);

            HSSFCell cellD3 = rowDetalle1.createCell(1 + i);
            cellD3.setCellStyle(styleTitulo);
            cellD3.setCellValue(idioma.getString("TC_CB3421"));
            CellUtil.setAlignment(cellD3, workbook, CellStyle.ALIGN_CENTER);
            i++;
            sheet.setColumnWidth(i, 4000);
            if ("2".equals(informe)) {
                for (int j = 0; j < vigenciaAnios; j++) {
                    HSSFCell cellA1 = rowDetalle1.createCell(1 + i);
                    cellA1.setCellStyle(styleTitulo);
                    cellA1.setCellValue(idioma.getString("TB_TB3596"));
                    CellUtil.setAlignment(cellA1, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA2 = rowDetalle1.createCell(1 + i);
                    cellA2.setCellStyle(styleTitulo);
                    cellA2.setCellValue(idioma.getString("TB_TB3597"));
                    CellUtil.setAlignment(cellA2, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA3 = rowDetalle1.createCell(1 + i);
                    cellA3.setCellStyle(styleTitulo);
                    cellA3.setCellValue(idioma.getString("TB_TB3598"));
                    cellA3.setCellType(Cell.CELL_TYPE_STRING);
                    CellUtil.setAlignment(cellA3, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA4 = rowDetalle1.createCell(1 + i);
                    cellA4.setCellStyle(styleTitulo);
                    cellA4.setCellValue(idioma.getString("TB_TB3599"));
                    CellUtil.setAlignment(cellA4, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA5 = rowDetalle1.createCell(1 + i);
                    cellA5.setCellStyle(styleTitulo);
                    cellA5.setCellValue(idioma.getString("TB_TB3600"));
                    CellUtil.setAlignment(cellA5, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA6 = rowDetalle1.createCell(1 + i);
                    cellA6.setCellStyle(styleTitulo);
                    cellA6.setCellValue(idioma.getString("TB_TB3601"));
                    CellUtil.setAlignment(cellA6, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA7 = rowDetalle1.createCell(1 + i);
                    cellA7.setCellStyle(styleTitulo);
                    cellA7.setCellValue(idioma.getString("TB_TB3602"));
                    CellUtil.setAlignment(cellA7, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);

                    HSSFCell cellA8 = rowDetalle1.createCell(1 + i);
                    cellA8.setCellStyle(styleTitulo);
                    cellA8.setCellValue(idioma.getString("TG_ANEXOS")
                                    .toUpperCase());
                    CellUtil.setAlignment(cellA8, workbook,
                                    CellStyle.ALIGN_CENTER);
                    i++;
                    sheet.setColumnWidth(i, 4000);
                }

            }
            if (conTotal) {
                HSSFCell cellA8 = rowDetalle1.createCell(1 + i);
                cellA8.setCellStyle(styleTitulo);
                cellA8.setCellValue(idioma.getString("TB_TB603"));
                CellUtil.setAlignment(cellA8, workbook, CellStyle.ALIGN_CENTER);
                i++;
                sheet.setColumnWidth(i, 4000);

                HSSFCell cellA9 = rowDetalle1.createCell(1 + i);
                cellA9.setCellStyle(styleTitulo);
                cellA9.setCellValue(idioma.getString("TB_TB604"));
                CellUtil.setAlignment(cellA9, workbook, CellStyle.ALIGN_CENTER);
                i++;
                sheet.setColumnWidth(i, 4000);

                HSSFCell cellA10 = rowDetalle1.createCell(1 + i);
                cellA10.setCellStyle(styleTitulo);
                cellA10.setCellValue(idioma.getString("TB_TB3599"));
                CellUtil.setAlignment(cellA10, workbook,
                                CellStyle.ALIGN_CENTER);
                i++;
                sheet.setColumnWidth(i, 4000);

                HSSFCell cellA11 = rowDetalle1.createCell(1 + i);
                cellA11.setCellStyle(styleTitulo);
                cellA11.setCellValue(idioma.getString("TB_TB3600"));
                CellUtil.setAlignment(cellA11, workbook,
                                CellStyle.ALIGN_CENTER);
                i++;
                sheet.setColumnWidth(i, 4000);

                HSSFCell cellA13 = rowDetalle1.createCell(1 + i);
                cellA13.setCellStyle(styleTitulo);
                cellA13.setCellValue(idioma.getString("TB_TB3601"));
                CellUtil.setAlignment(cellA13, workbook,
                                CellStyle.ALIGN_CENTER);
                i++;
                sheet.setColumnWidth(i, 4000);

                HSSFCell cellA12 = rowDetalle1.createCell(1 + i);
                cellA12.setCellStyle(styleTitulo);
                cellA12.setCellValue(idioma.getString("TB_TB3602"));
                CellUtil.setAlignment(cellA12, workbook,
                                CellStyle.ALIGN_CENTER);
                i++;
                sheet.setColumnWidth(i, 4000);

                HSSFCell cellA14 = rowDetalle1.createCell(1 + i);
                cellA14.setCellStyle(styleTitulo);
                cellA14.setCellValue(idioma.getString("TG_ANEXOS")
                                .toUpperCase());
                CellUtil.setAlignment(cellA14, workbook,
                                CellStyle.ALIGN_CENTER);
                sheet.setColumnWidth(i + 1, 4000);

            }
            niveles.append(" DESCRIPCION_INDICADOR");

            int col = 6;
            int auxNiveles;

            aux = ejbBancoPRoyectosTres.calculcarSegumientoPlanIndicativo(
                            compania, Integer.parseInt(periodoGub),
                            Integer.parseInt(informe), vigencia,
                            cantidadNiveles);

            String[] filaAux = aux.split(SysmanConstantes.SEPARADOR_REG);

            for (int j = 1; j < filaAux.length; j++) {
                String[] columnaAux = filaAux[j]
                                .split(SysmanConstantes.SEPARADOR_COL);
                HSSFRow rowDetalleTemp = sheet.createRow(col);
                rowDetalleTemp.setHeightInPoints(20);
                for (int k = 0; k < cantidadNiveles; k++) {
                    HSSFCell cellD1 = rowDetalleTemp.createCell(k + 1);
                    cellD1.setCellStyle(styleDetalle);
                    cellD1.setCellValue(columnaAux[k]);
                }

                HSSFCell cellD1 = rowDetalleTemp
                                .createCell(cantidadNiveles + 1);
                cellD1.setCellStyle(styleDetalle);
                cellD1.setCellValue(columnaAux[cantidadNiveles]);

                HSSFCell cellA1 = rowDetalleTemp
                                .createCell(cantidadNiveles + 2);
                cellA1.setCellStyle(styleDetalle);
                cellA1.setCellValue(columnaAux[cantidadNiveles + 1]);

                auxNiveles = cantidadNiveles + 2;

                if ("2".equals(informe)) {

                    Object[] rowCeldas = rowCell(vigenciaAnios, rowDetalleTemp,
                                    columnaAux, auxNiveles, styleDetalle);
                    rowDetalleTemp = (HSSFRow) rowCeldas[0];
                    auxNiveles = (int) rowCeldas[1];

                }

                if (conTotal) {
                    HSSFCell cellB8 = rowDetalleTemp.createCell(auxNiveles);
                    cellB8.setCellStyle(styleDetalle);
                    cellB8.setCellValue(columnaAux[auxNiveles]);

                    HSSFCell cellB2 = rowDetalleTemp.createCell(auxNiveles + 1);
                    cellB2.setCellStyle(styleDetalle);
                    cellB2.setCellValue(columnaAux[auxNiveles + 1]);

                    HSSFCell cellB3 = rowDetalleTemp.createCell(auxNiveles + 2);
                    cellB3.setCellStyle(styleDetalle);
                    cellB3.setCellValue(columnaAux[auxNiveles + 2]);

                    HSSFCell cellB4 = rowDetalleTemp.createCell(auxNiveles + 3);
                    cellB4.setCellStyle(styleDetalle);
                    cellB4.setCellValue(columnaAux[auxNiveles + 3]);

                    HSSFCell cellB5 = rowDetalleTemp.createCell(auxNiveles + 4);
                    cellB5.setCellStyle(styleDetalle);
                    cellB5.setCellValue(columnaAux[auxNiveles + 4]);

                    HSSFCell cellB6 = rowDetalleTemp.createCell(auxNiveles + 5);
                    cellB6.setCellStyle(styleDetalle);
                    cellB6.setCellValue(columnaAux[auxNiveles + 5]);

                    HSSFCell cellB7 = rowDetalleTemp.createCell(auxNiveles + 6);
                    cellB7.setCellStyle(styleDetalle);
                    cellB7.setCellValue(columnaAux[auxNiveles + 6]);
                }

                col++;
            }

            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            nombreArchivo);

            workbook.close();
        }
        catch (IOException | JRException | ParseException
                        | SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(MSM) + ex.getMessage());
            Logger.getLogger(FrmInformesPlanIndicativo.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    private Object[] rowCell(int vigenciaAnios, HSSFRow rowDetalleTemp,
        String[] columnaAux, int auxNiveles, HSSFCellStyle styleDetalle) {
        Object[] vectorAux = new Object[2];
        int aux = auxNiveles;
        for (int k = 0; k < vigenciaAnios; k++) {

            HSSFCell cellB1 = rowDetalleTemp.createCell(auxNiveles);
            cellB1.setCellStyle(styleDetalle);
            cellB1.setCellValue(columnaAux[auxNiveles + k]);

            HSSFCell cellB2 = rowDetalleTemp.createCell(auxNiveles + 1);
            cellB2.setCellValue(columnaAux[(auxNiveles + 1) + k]);
            cellB2.setCellStyle(styleDetalle);

            HSSFCell cellB3 = rowDetalleTemp.createCell(auxNiveles + 2);
            cellB3.setCellStyle(styleDetalle);
            cellB3.setCellValue(columnaAux[(auxNiveles + 2) + k]);

            HSSFCell cellB4 = rowDetalleTemp.createCell(auxNiveles + 3);
            cellB4.setCellValue(columnaAux[(auxNiveles + 3) + k]);
            cellB4.setCellStyle(styleDetalle);

            HSSFCell cellB5 = rowDetalleTemp.createCell(auxNiveles + 4);
            cellB5.setCellValue(columnaAux[(auxNiveles + 4) + k]);
            cellB5.setCellStyle(styleDetalle);

            HSSFCell cellB6 = rowDetalleTemp.createCell(auxNiveles + 5);
            cellB6.setCellValue(columnaAux[(auxNiveles + 5) + k]);
            cellB6.setCellStyle(styleDetalle);

            HSSFCell cellB7 = rowDetalleTemp.createCell(auxNiveles + 6);
            cellB7.setCellValue(columnaAux[(auxNiveles + 6) + k]);
            cellB7.setCellStyle(styleDetalle);

            HSSFCell cellB8 = rowDetalleTemp.createCell(auxNiveles + 7);
            cellB8.setCellStyle(styleDetalle);
            cellB8.setCellValue("");

            aux = aux + 8;

        }
        vectorAux[0] = rowDetalleTemp;
        vectorAux[1] = auxNiveles;
        return vectorAux;
    }

    private void seguimientoPlanPorVigencia() {
        // <CODIGO_DESARROLLADO>
        HSSFWorkbook workbook = new HSSFWorkbook();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String aux;
            String planDesarrollo = "";
            String nombreAlcalde = "";
            int contador = 10;
            String nombreArchivo = null;

            planDesarrollo = ejbSysmanUtl.consultarParametro(compania,
                            "PLAN DE DESARROLLO", modulo, new Date(), false);

            nombreAlcalde = ejbSysmanUtl.consultarParametro(compania,
                            "ALCALDE MUNICIPAL", modulo, new Date(), false);

            Calendar calendario = new GregorianCalendar();
            nombreArchivo = "SEGUIMIENTO PLAN INDICATIVO_"
                + SysmanFunciones.convertirAFechaCadena(new Date(), "ddMMYYYY")
                + "_"
                + calendario.get(Calendar.HOUR_OF_DAY)
                + calendario.get(Calendar.MINUTE)
                + calendario.get(Calendar.SECOND) + ".xls";

            HSSFSheet sheet = workbook
                            .createSheet("SEGUIMIENTO PLAN INDICATIVO");

            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName(TAHOMA);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle styleTitulo = workbook.createCellStyle();
            styleTitulo.setFont(font);

            HSSFRow rowDpto = sheet.createRow(1);
            HSSFCell cellDepartamento = rowDpto.createCell(0);
            cellDepartamento.setCellStyle(styleTitulo);
            cellDepartamento.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3320"), " ", dptoCompania));

            HSSFRow rowCiudad = sheet.createRow(2);
            HSSFCell cellCiudad = rowCiudad.createCell(0);
            cellCiudad.setCellStyle(styleTitulo);
            cellCiudad.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3605"), " ",
                            ciudadCompania));

            HSSFRow rowPlan = sheet.createRow(3);
            HSSFCell cellPlan = rowPlan.createCell(0);
            cellPlan.setCellStyle(styleTitulo);
            cellPlan.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TT_LB24694"), " ",
                            planDesarrollo));

            HSSFRow rowAlcalde = sheet.createRow(4);
            HSSFCell cellAlcalde = rowAlcalde.createCell(0);
            cellAlcalde.setCellStyle(styleTitulo);
            cellAlcalde.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3606"), " ", nombreAlcalde));

            HSSFRow rowPlanI = sheet.createRow(5);
            HSSFCell cellPlanI = rowPlanI.createCell(0);
            cellPlanI.setCellStyle(styleTitulo);
            cellPlanI.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3607"), " ",
                            planDesarrollo));

            HSSFRow rowIndicador = sheet.createRow(8);
            HSSFCell cellIndicador = rowIndicador.createCell(5);

            HSSFFont fontDetalle = workbook.createFont();
            fontDetalle.setFontHeightInPoints((short) 10);
            fontDetalle.setFontName(TAHOMA);
            fontDetalle.setBoldweight((short) 3);

            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyle.setFont(fontDetalle);

            HSSFFont fontCabecera = workbook.createFont();
            fontCabecera.setFontHeightInPoints((short) 10);
            fontCabecera.setFontName(TAHOMA);
            fontCabecera.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle cellStyleCabecera = workbook.createCellStyle();
            cellStyleCabecera.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            cellStyleCabecera.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            cellStyleCabecera.setBorderRight(HSSFCellStyle.BORDER_THIN);
            cellStyleCabecera.setBorderTop(HSSFCellStyle.BORDER_THIN);
            cellStyleCabecera.setFont(fontCabecera);
            cellStyleCabecera.setAlignment((short) 4);

            CellRangeAddress range1 = new CellRangeAddress(8, 8, 5, 9);

            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);

            CellRangeAddress range2 = new CellRangeAddress(8, 8, 10, 14);

            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);

            cellIndicador.setCellValue(idioma.getString("TB_TB3608"));
            sheet.addMergedRegion(range1);
            cellIndicador.setCellStyle(cellStyleCabecera);
            CellUtil.setAlignment(cellIndicador, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellRecursos = rowIndicador.createCell(10);
            cellRecursos.setCellValue(idioma.getString("TB_TB3609"));
            cellRecursos.setCellStyle(cellStyleCabecera);
            sheet.addMergedRegion(range2);
            CellUtil.setAlignment(cellRecursos, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFRow rowNivel = sheet.createRow(9);
            HSSFCell cellNivel = rowNivel.createCell(0);
            cellNivel.setCellStyle(cellStyleCabecera);
            cellNivel.setCellValue(NIVELS);
            CellUtil.setAlignment(cellNivel, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellConcepto = rowNivel.createCell(1);
            cellConcepto.setCellStyle(cellStyleCabecera);
            cellConcepto.setCellValue(idioma.getString("TG_CONCEPTO2"));
            CellUtil.setAlignment(cellConcepto, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellIndicadores = rowNivel.createCell(2);
            cellIndicadores.setCellStyle(cellStyleCabecera);
            cellIndicadores.setCellValue(idioma.getString("TG_INDICADORES"));
            CellUtil.setAlignment(cellIndicadores, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellPonderacion = rowNivel.createCell(3);
            cellPonderacion.setCellStyle(cellStyleCabecera);
            cellPonderacion.setCellValue(idioma.getString("TG_PONDERACION"));
            CellUtil.setAlignment(cellPonderacion, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellLB = rowNivel.createCell(4);
            cellLB.setCellStyle(cellStyleCabecera);
            cellLB.setCellValue("LINEA BASE");
            CellUtil.setAlignment(cellLB, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellAnio1 = rowNivel.createCell(5);
            cellAnio1.setCellStyle(cellStyleCabecera);
            cellAnio1.setCellValue(periodoGub);
            CellUtil.setAlignment(cellAnio1, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellAnio2 = rowNivel.createCell(6);
            cellAnio2.setCellStyle(cellStyleCabecera);
            cellAnio2.setCellValue(
                            String.valueOf(Integer.parseInt(periodoGub) + 1));
            CellUtil.setAlignment(cellAnio2, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellAnio3 = rowNivel.createCell(7);
            cellAnio3.setCellStyle(cellStyleCabecera);
            cellAnio3.setCellValue(
                            String.valueOf(Integer.parseInt(periodoGub) + 2));
            CellUtil.setAlignment(cellAnio3, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellAnio4 = rowNivel.createCell(8);
            cellAnio4.setCellStyle(cellStyleCabecera);
            cellAnio4.setCellValue(
                            String.valueOf(Integer.parseInt(periodoGub) + 3));
            CellUtil.setAlignment(cellAnio4, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellTotalF = rowNivel.createCell(9);
            cellTotalF.setCellStyle(cellStyleCabecera);
            cellTotalF.setCellValue(idioma.getString("TB_TB3610"));
            CellUtil.setAlignment(cellTotalF, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellRecAnio1 = rowNivel.createCell(10);
            cellRecAnio1.setCellStyle(cellStyleCabecera);
            cellRecAnio1.setCellValue(periodoGub);
            CellUtil.setAlignment(cellRecAnio1, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellRecAnio2 = rowNivel.createCell(11);
            cellRecAnio2.setCellStyle(cellStyleCabecera);
            cellRecAnio2.setCellValue(
                            String.valueOf(Integer.parseInt(periodoGub) + 1));
            CellUtil.setAlignment(cellRecAnio2, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellRecAnio3 = rowNivel.createCell(12);
            cellRecAnio3.setCellStyle(cellStyleCabecera);
            cellRecAnio3.setCellValue(
                            String.valueOf(Integer.parseInt(periodoGub) + 2));
            CellUtil.setAlignment(cellRecAnio3, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellRecAnio4 = rowNivel.createCell(13);
            cellRecAnio4.setCellStyle(cellStyleCabecera);
            cellRecAnio4.setCellValue(
                            String.valueOf(Integer.parseInt(periodoGub) + 3));
            CellUtil.setAlignment(cellRecAnio4, workbook,
                            CellStyle.ALIGN_CENTER);

            HSSFCell cellTotal = rowNivel.createCell(14);
            cellTotal.setCellStyle(cellStyleCabecera);
            cellTotal.setCellValue(idioma.getString("TG_TOTAL"));
            CellUtil.setAlignment(cellTotal, workbook, CellStyle.ALIGN_CENTER);

            HSSFCell cellDependencia = rowNivel.createCell(15);
            cellDependencia.setCellStyle(cellStyleCabecera);
            cellDependencia.setCellValue(idioma.getString("TG_DEPENDENCIA2"));
            CellUtil.setAlignment(cellDependencia, workbook,
                            CellStyle.ALIGN_CENTER);

            aux = ejbBancoPRoyectosTres
                            .calculcarSegumientoPlanIndicativo(
                                            compania,
                                            Integer.parseInt(periodoGub),
                                            Integer.parseInt(informe),
                                            periodoGub, 0);

            String[] filaAux = aux.split(SysmanConstantes.SEPARADOR_REG);

            for (int i = 0; i < filaAux.length; i++) {
                String[] columnaAux = filaAux[i]
                                .split(SysmanConstantes.SEPARADOR_COL);

                HSSFRow row = sheet.createRow(contador);

                HSSFCell cellN = row.createCell(0);
                cellN.setCellStyle(cellStyle);
                cellN.setCellValue(columnaAux[0]);

                HSSFCell cellC = row.createCell(1);
                cellC.setCellStyle(cellStyle);
                cellC.setCellValue(columnaAux[1]);

                HSSFCell cellI = row.createCell(2);
                cellI.setCellStyle(cellStyle);
                cellI.setCellValue(columnaAux[2]);

                HSSFCell cellPonde = row.createCell(3);
                cellPonde.setCellStyle(cellStyle);
                cellPonde.setCellValue(columnaAux[3]);

                HSSFCell cellLinea = row.createCell(4);
                cellLinea.setCellStyle(cellStyle);
                cellLinea.setCellValue(columnaAux[4]);

                HSSFCell cellA1 = row.createCell(5);
                cellA1.setCellStyle(cellStyle);
                cellA1.setCellValue(columnaAux[5]);

                HSSFCell cellA2 = row.createCell(6);
                cellA2.setCellStyle(cellStyle);
                cellA2.setCellValue(columnaAux[6]);

                HSSFCell cellA3 = row.createCell(7);
                cellA3.setCellStyle(cellStyle);
                cellA3.setCellValue(columnaAux[7]);

                HSSFCell cellA4 = row.createCell(8);
                cellA4.setCellStyle(cellStyle);
                cellA4.setCellValue(columnaAux[8]);

                HSSFCell cellTotalFisico = row.createCell(9);
                cellTotalFisico.setCellStyle(cellStyle);
                cellTotalFisico.setCellValue(columnaAux[9]);

                HSSFCell cellRecAno1 = row.createCell(10);
                cellRecAno1.setCellStyle(cellStyle);
                cellRecAno1.setCellValue(columnaAux[10]);

                HSSFCell cellRecAno2 = row.createCell(11);
                cellRecAno2.setCellStyle(cellStyle);
                cellRecAno2.setCellValue(columnaAux[11]);

                HSSFCell cellRecAno3 = row.createCell(12);
                cellRecAno3.setCellStyle(cellStyle);
                cellRecAno3.setCellValue(columnaAux[12]);

                HSSFCell cellRecAno4 = row.createCell(13);
                cellRecAno4.setCellStyle(cellStyle);
                cellRecAno4.setCellValue(columnaAux[13]);

                HSSFCell cellTotall = row.createCell(14);
                cellTotall.setCellStyle(cellStyle);
                cellTotall.setCellValue(columnaAux[14]);

                HSSFCell cellDepend = row.createCell(15);
                cellDepend.setCellStyle(cellStyle);
                cellDepend.setCellValue(columnaAux[15]);
                contador += 1;
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(9);
            sheet.autoSizeColumn(14);
            sheet.autoSizeColumn(15);

            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            nombreArchivo);

        }
        catch (ParseException | IOException | JRException
                        | SystemException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(MSM) + ex.getMessage());
            Logger.getLogger(FrmInformesPlanIndicativo.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        finally {
            try {
                workbook.close();
            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void oprimirImprimir() {
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(informe)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1914"));
            return;
        }
        if ("4".equals(informe)) {
            generarInforme();
        }
        else if ("2".equals(informe)) {
            if (SysmanFunciones.validarVariableVacio(vigencia)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB250"));
                return;
            }
            generarExcel();
        }
        else if ("1".equals(informe)) {
            seguimientoPlanPorVigencia();
        }
        else if ("3".equals(informe)) {
            generarExcel();
        }
    }

    private void cargarVigencia() {
        try {
            periodoGub = ejbSysmanUtl.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", modulo, new Date(),
                            false);

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmInformesPlanIndicativo.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void cambiarInforme() {
        // <CODIGO_DESARROLLADO>
        if ("2".equals(informe)) {
            vigencia = periodoGub;
        }
        if ("4".equals(informe)) {
            vigencia = periodoGub;
            cargarListaPeriodo();
        }
        if ("3".equals(informe)) {
            vigencia = periodoGub;
        }
        nivel = null;
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVIGENCIAINICIAL() {
        nivel = null;
        cargarListanivel();
    }

    private void generarInforme() {
        if (SysmanFunciones.validarVariableVacio(vigencia)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB670"));
            return;
        }
        if (SysmanFunciones.validarVariableVacio(nivel)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2327"));
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        try {
            parametros.put("PR_PERIODO", vigencia);
            parametros.put("PR_NIVEL", service.buscarEnLista(nivel, "DIGITOS",
                            "DESCRIPCION", listaNivel));
            parametros.put("PR_CIUDADCOMPANIA", ciudadCompania.toUpperCase());
            parametros.put("PR_DEPARTAMENTOCOMPANIA",
                            dptoCompania.toUpperCase());
            String nombreReporte = "000213RptInformePlanind";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("nivel", nivel);
            reemplazar.put("periodoGub", vigencia);
            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);

        }
        catch (SysmanException | JRException
                        | IOException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmInformesPlanIndicativo.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void abrirFormulario() {
        // HEREDADO DEL BEAN BASE
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public List<Registro> getListaVIGENCIAINICIAL() {
        return listaVIGENCIAINICIAL;
    }

    public void setListaVIGENCIAINICIAL(List<Registro> listaVIGENCIAINICIAL) {
        this.listaVIGENCIAINICIAL = listaVIGENCIAINICIAL;
    }

    public List<Registro> getListaNivel() {
        return listaNivel;
    }

    public void setListaNivel(List<Registro> listanivel) {
        this.listaNivel = listanivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

}
