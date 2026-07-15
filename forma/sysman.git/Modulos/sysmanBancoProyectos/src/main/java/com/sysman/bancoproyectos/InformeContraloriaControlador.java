
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.InformeContraloriaControladorEnum;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCell;
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
 * @version 1, 23/05/2016
 * 
 * @author jcrodriguez,Refactoring y depuracion del controlador
 * @version 2, 25/09/2017
 */
@ManagedBean
@ViewScoped

public class InformeContraloriaControlador extends BeanBaseModal
{
    private final String compania;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoseleccionaExcel;

    /**
     * Creates a new instance of InformeContraloriaControlador
     */
    public InformeContraloriaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INFORME_CONTRALORIA_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(InformeContraloriaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        contArchivoseleccionaExcel = new ContenedorArchivo();
    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        fechaInicial = fechaFinal = new Date();

    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        if (contArchivoseleccionaExcel.getArchivo() == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1901"));
            return;
        }

        FileInputStream file = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            String rutaArchivo = contArchivoseleccionaExcel.getArchivo()
                            .getPath();
            file = new FileInputStream(new File(rutaArchivo));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            CellStyle style = workbook.createCellStyle();
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setAlignment(CellStyle.ALIGN_RIGHT);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());

            String fechaIni = SysmanFunciones
                            .convertirAFechaCadena(fechaInicial);
            String fechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("fechaInicial", fechaIni);
            reemplazos.put("fechaFinal", fechaFin);
            String strSql = Reporteador
                            .resuelveConsulta(InformeContraloriaControladorEnum.CONSULTA800049.getValue(),
                                            Integer.parseInt(
                                                            SessionUtil.getModulo()),
                                            reemplazos);
            List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                            strSql);

            int rowNum;
            Cell start = searchColumnSheet(InformeContraloriaControladorEnum.FICHA_EBI.getValue(), 0, 0,
                            sheet.getLastRowNum(), sheet);
            if (start != null)
            {
                rowNum = start.getRowIndex() + 1;
                if (!rs.isEmpty())
                {
                    llenarCeldas(rs, rowNum, workbook, sheet, style);
                    workbook.setForceFormulaRecalculation(true);
                    workbook.write(out);
                    out.close();
                    archivoDescarga = JsfUtil.getArchivoDescarga(
                                    new ByteArrayInputStream(out.toByteArray()), SysmanFunciones.concatenar(
                                                    idioma.getString("EM_FR772"), " ", "-",
                                                    String.valueOf(SysmanFunciones.ano(fechaInicial)), ".xlsx"));
                }
                else
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2361"));
                }
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2364"));
            }

        }
        catch (IOException | ParseException | JRException e)
        {
            Logger.getLogger(InformeContraloriaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally
        {
            try
            {
                if (file != null)
                {
                    file.close();
                }
            }
            catch (IOException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void llenarCeldas(List<Registro> rs, int aux, Workbook workbook, Sheet sheet, CellStyle style)
    {
        int rowNum = aux;
        for (int i = 0; i < rs.size(); i++)
        {
            Row row = sheet.createRow(rowNum);
            Cell nCell = row.createCell(0);
            nCell.setCellValue(rs.get(i).getCampos()
                            .get(InformeContraloriaControladorEnum.CODIGOBPIM.getValue()).toString());
            nCell.setCellStyle(style);

            nCell = row.createCell(1);
            nCell.setCellValue(rs.get(i).getCampos()
                            .get(InformeContraloriaControladorEnum.NOMBREPROYECTO.getValue()).toString());
            nCell.setCellStyle(style);

            nCell = row.createCell(2);
            nCell.setCellValue(Double.parseDouble(
                            rs.get(i).getCampos().get(InformeContraloriaControladorEnum.VALORTOTAL.getValue())
                                            .toString()));
            style.setDataFormat(workbook.createDataFormat()
                            .getFormat("$ #,##0.00"));
            nCell.setCellStyle(style);

            nCell = row.createCell(3);
            nCell.setCellValue(rs.get(i).getCampos()
                            .get(InformeContraloriaControladorEnum.FECHAREGISTRO.getValue()).toString());
            nCell.setCellStyle(style);

            nCell = row.createCell(4);
            nCell.setCellValue(rs.get(i).getCampos()
                            .get(InformeContraloriaControladorEnum.VIGENCIAFIN.getValue()).toString());
            nCell.setCellStyle(style);

            nCell = row.createCell(5);
            nCell.setCellValue(Double.parseDouble(rs.get(i)
                            .getCampos().get(InformeContraloriaControladorEnum.VALOREJECUTADO.getValue())
                            .toString()));
            style.setDataFormat(workbook.createDataFormat()
                            .getFormat("$ #,##0.00"));
            nCell.setCellStyle(style);

            nCell = row.createCell(6);
            nCell.setCellValue(Double.parseDouble(rs.get(i)
                            .getCampos().get(InformeContraloriaControladorEnum.AVANCE.getValue()).toString()));
            style.setDataFormat(workbook.createDataFormat()
                            .getFormat("#,##0.00"));
            nCell.setCellStyle(style);

            if (rs.get(i).getCampos().get(InformeContraloriaControladorEnum.OBJETO.getValue()) != null)
            {
                nCell = row.createCell(7);
                nCell.setCellValue(rs.get(i).getCampos().get(InformeContraloriaControladorEnum.OBJETO.getValue())
                                .toString());
            }
            nCell.setCellStyle(style);

            rowNum++;
        }
    }

    public Cell cellString(String searchText, int column, Row r)
    {
        Cell c = null;
        if ((searchText != null)
            && r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                            .getStringCellValue()
                            .contains(searchText))
        {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
        }
        return c;
    }

    public Cell cellFormula(String searchText, int column, Row r)
    {
        Cell c = null;
        if ((searchText != null)
            && r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                            .getCellFormula()
                            .contains(searchText))
        {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
        }
        return c;
    }

    public Cell cellDefault(String searchText, int column, Row r)
    {
        Cell c = null;
        if ((searchText != null)
            && searchText.equals(
                            r.getCell(column,
                                            Row.RETURN_BLANK_AS_NULL)
                                            .getStringCellValue()))
        {
            c = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
        }
        return c;
    }

    public Cell searchColumnSheet(String searchText, int column, int rowStart,
        int rowEnd, Sheet sheet)
    {
        Cell c = null;
        for (int rowNum = rowStart; rowNum < rowEnd; rowNum++)
        {
            if ((sheet.getRow(rowNum) != null)
                && (sheet.getRow(rowNum).getCell(column) != null))
            {
                Row r = sheet.getRow(rowNum);
                if (r.getCell(column, Row.RETURN_BLANK_AS_NULL) == null)
                {
                    continue;
                }
                switch (r.getCell(column, Row.RETURN_BLANK_AS_NULL)
                                .getCellType())
                {
                case HSSFCell.CELL_TYPE_STRING:
                    return cellString(searchText, column, r);

                case HSSFCell.CELL_TYPE_FORMULA:
                    return cellFormula(searchText, column, r);

                default:
                    return cellDefault(searchText, column, r);
                }
            }
        }
        return c;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoseleccionaExcel()
    {
        return contArchivoseleccionaExcel;
    }

    public void setContArchivoseleccionaExcel(
        ContenedorArchivo contArchivoseleccionaExcel)
    {
        this.contArchivoseleccionaExcel = contArchivoseleccionaExcel;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCompania()
    {
        return compania;
    }

}