package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFRegionUtil;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 28/08/2015
 * 
 * @author eamaya
 * @version 2.0, 03/10/2017, Cambio de numero de formulario por enum y
 * Manejo de EJBs
 */
@ManagedBean
@ViewScoped
public class FrmMayorizaPondeControlador extends BeanBaseModal {

    private final String compania;

    private final String usuario;

    private final String modulo;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private String vigencia;

    private static final String CALIBRI = "Calibri";
    private StreamedContent archivoDescarga;

    @EJB
    private EjbBancoProyectoCuatroRemote ejbBancoProyectoCuatro;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Creates a new instance of FrmMayorizaPonde
     */
    public FrmMayorizaPondeControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_MAYORIZA_PONDE_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmMayorizaPondeControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        try {
            // <CODIGO_DESARROLLADO>
            vigencia = ejbSysmanUtl.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", modulo, new Date(),
                            false);

            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmMayorizaPondeControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirComando25() {
        // </CODIGO_DESARROLLADO>

        String aux;
        int fila = 8;
        HSSFWorkbook workbook = new HSSFWorkbook();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            HSSFSheet sheet = workbook
                            .createSheet("COMPARACION PONDERACION");

            // Region0 del logo del reporte
            CellRangeAddress range0 = new CellRangeAddress(0, 5, 0, 1);

            sheet.addMergedRegion(range0);

            // Adicion del logo

            InputStream is = new FileInputStream(
                            SessionUtil.getCompaniaIngreso().getRutaImagen());
            byte[] bytes = IOUtils.toByteArray(is);
            int pictureIdx = workbook.addPicture(bytes,
                            Workbook.PICTURE_TYPE_PNG);
            is.close();
            CreationHelper helper = workbook.getCreationHelper();
            Drawing drawing = sheet.createDrawingPatriarch();

            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            anchor.setCol2(2);
            anchor.setRow2(6);
            drawing.createPicture(anchor, pictureIdx);

            // Define el formato del texto de la celda titulo
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 14);
            font.setFontName(CALIBRI);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle styleTitulo = workbook.createCellStyle();
            styleTitulo.setFont(font);
            styleTitulo.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleTitulo.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            styleTitulo.setVerticalAlignment(HSSFCellStyle.VERTICAL_JUSTIFY);
            styleTitulo.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            HSSFRow titulo = sheet.createRow(0);

            HSSFCell cellTitulo1 = titulo.createCell(2);
            cellTitulo1.setCellStyle(styleTitulo);
            cellTitulo1.setCellValue(SysmanFunciones.concatenar(
                            idioma.getString("TB_TB3676"), " ", vigencia));

            // Region1 del titulo del reporte
            CellRangeAddress range1 = new CellRangeAddress(0, 4, 2, 12);
            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range1,
                            sheet, workbook);
            sheet.addMergedRegion(range1);

            // Define el formato de texto de los nombres de las
            // columnas
            HSSFFont fontDos = workbook.createFont();
            fontDos.setFontHeightInPoints((short) 11);
            fontDos.setFontName(CALIBRI);
            fontDos.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle styleColumna = workbook.createCellStyle();
            styleColumna.setFont(fontDos);
            styleColumna.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleColumna.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleColumna.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleColumna.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleColumna.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            HSSFCellStyle styleColumnaComb = workbook.createCellStyle();
            styleColumnaComb.setFont(fontDos);
            styleColumnaComb.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleColumnaComb.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleColumnaComb.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleColumnaComb.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleColumnaComb.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            // Crear celda ponderacion
            HSSFRow ponderacion = sheet.createRow(6);

            HSSFCell cellPonderacion = ponderacion.createCell(11);
            cellPonderacion.setCellStyle(styleColumnaComb);
            cellPonderacion.setCellValue(idioma.getString("TB_TB3675"));

            // Region2 de ponderacion

            CellRangeAddress range2 = new CellRangeAddress(6, 6, 11, 12);
            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN, range2,
                            sheet, workbook);

            sheet.addMergedRegion(range2);

            // Creacion columnas del reporte
            HSSFRow columnas = sheet.createRow(7);

            HSSFCell cellDescripcion = columnas.createCell(1);
            cellDescripcion.setCellStyle(styleColumnaComb);
            cellDescripcion.setCellValue(idioma.getString("TB_TB3680"));

            // Region3 descripcion

            CellRangeAddress range3 = new CellRangeAddress(7, 7, 1, 10);
            HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                            range3,
                            sheet, workbook);
            HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,
                            range3,
                            sheet, workbook);
            HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,
                            range3,
                            sheet, workbook);
            HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,
                            range3,
                            sheet, workbook);

            sheet.addMergedRegion(range3);

            HSSFCell cellCodigo = columnas.createCell(0);
            cellCodigo.setCellStyle(styleColumna);
            cellCodigo.setCellValue(idioma.getString("TB_TB3679"));

            HSSFCell cellInicial = columnas.createCell(11);
            cellInicial.setCellStyle(styleColumna);
            cellInicial.setCellValue(
                            idioma.getString("TG_INICIAL").toUpperCase());

            HSSFCell cellFinal = columnas.createCell(12);
            cellFinal.setCellStyle(styleColumna);
            cellFinal.setCellValue(idioma.getString("TG_FINAL").toUpperCase());

            // Define el formato del texto del detalle
            HSSFFont fontTres = workbook.createFont();
            fontTres.setFontHeightInPoints((short) 10);
            fontTres.setFontName(CALIBRI);

            HSSFCellStyle styleDetalle = workbook.createCellStyle();
            styleDetalle.setFont(fontTres);
            styleDetalle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setBorderRight(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            styleDetalle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            aux = ejbBancoProyectoCuatro.mayorizarPonderacion(compania,
                            Integer.parseInt(vigencia),
                            true, null, true, usuario);

            String[] filaAux = aux.split(SysmanConstantes.SEPARADOR_REG);

            for (int i = 1; i < filaAux.length; i++) {
                String[] columnaAux = filaAux[i]
                                .split(SysmanConstantes.SEPARADOR_COL);

                HSSFRow rowDetalleTemp = sheet.createRow(fila);

                HSSFCell cellCodigoDetalle = rowDetalleTemp
                                .createCell(0);
                cellCodigoDetalle.setCellStyle(styleDetalle);
                cellCodigoDetalle.setCellValue(columnaAux[0]);

                HSSFCell cellDescripcionDetalle = rowDetalleTemp
                                .createCell(1);
                cellDescripcionDetalle.setCellStyle(styleDetalle);
                cellDescripcionDetalle.setCellValue(columnaAux[1]);

                HSSFCell cellDescripcionInicial = rowDetalleTemp
                                .createCell(11);
                cellDescripcionInicial.setCellStyle(styleDetalle);
                cellDescripcionInicial.setCellValue(columnaAux[2]);

                HSSFCell cellDescripcionFinal = rowDetalleTemp
                                .createCell(12);
                cellDescripcionFinal.setCellStyle(styleDetalle);
                cellDescripcionFinal.setCellValue(columnaAux[3]);

                CellRangeAddress range4 = new CellRangeAddress(fila, fila, 1,
                                10);

                HSSFRegionUtil.setBorderBottom(HSSFCellStyle.BORDER_THIN,
                                range4,
                                sheet, workbook);
                HSSFRegionUtil.setBorderTop(HSSFCellStyle.BORDER_THIN,
                                range4,
                                sheet, workbook);
                HSSFRegionUtil.setBorderLeft(HSSFCellStyle.BORDER_THIN,
                                range4,
                                sheet, workbook);
                HSSFRegionUtil.setBorderRight(HSSFCellStyle.BORDER_THIN,
                                range4,
                                sheet, workbook);

                sheet.addMergedRegion(range4);

                fila++;
            }

            workbook.write(out);
            out.close();

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "rptCompPonderacion.xls");

        }
        catch (IOException | JRException | NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
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

    public void oprimirAceptar() {
        try {
            // <CODIGO_DESARROLLADO>

            ejbBancoProyectoCuatro.mayorizarPonderacion(compania,
                            Integer.parseInt(vigencia), false, null, false,
                            usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1269"));
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(FrmMayorizaPondeControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirComando26() {

        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme1(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void genInforme1(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("vigencia", vigencia);
            // MANEJO DE PARAMETROS DEL REPORTE

            Reporteador.resuelveConsulta("000204rptPondeMayor100",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000204rptPondeMayor100", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (SysmanException | JRException | IOException ex) {
            Logger.getLogger(FrmMayorizaPondeControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            ex.getMessage()));
        }

    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
}