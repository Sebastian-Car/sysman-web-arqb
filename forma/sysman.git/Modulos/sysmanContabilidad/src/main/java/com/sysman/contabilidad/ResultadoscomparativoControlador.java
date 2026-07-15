package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ResultadoscomparativoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 10/05/2016
 *
 * @author eamaya
 * @version 2, 04/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 *
 * @author jlramirez
 * @version 3, 20/04/2017 Cambios de sysdate a new Date()
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente.
 */
@ManagedBean
@ViewScoped

public class ResultadoscomparativoControlador extends BeanBaseModal {
    private final String compania;
    private String mesTrabajo;
    private String mesComparar;
    private String anioTrabajo;
    private String anioComparar;
    private StreamedContent archivoDescarga;
    private ContenedorArchivo contArchivoarchivoModelo;
    private ContenedorArchivo contArchivonombreArchivo;
    private UploadedFile archivoCargarefNomArchivo;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaAnoComparar;
    private Workbook workbook;

    /**
     * Creates a new instance of ResultadoscomparativoControlador
     */
    public ResultadoscomparativoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESULTADOSCOMPARATIVO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResultadoscomparativoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            contArchivoarchivoModelo = new ContenedorArchivo();
            contArchivonombreArchivo = new ContenedorArchivo();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaAnoTrabajo();
        cargarListaAnoComparar();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        anioComparar = String.valueOf(SysmanFunciones.ano(new Date()));
        anioTrabajo = String.valueOf(Integer.parseInt(anioComparar) - 1);
        int mes = SysmanFunciones.mes(new Date());
        if ((mes > 0) && (mes <= 3)) {
            mesComparar = "1";
        }
        else if ((mes > 3) && (mes <= 6)) {
            mesComparar = "2";
        }
        else if ((mes > 6) && (mes <= 9)) {
            mesComparar = "3";
        }
        else if ((mes > 9) && (mes <= 12)) {
            mesComparar = "4";
        }
        mesTrabajo = mesComparar;
        // <CODIGO_DESARROLLADO>
        /*
         * FR691-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResultadoscomparativoControladorUrlEnum.URL4317
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoComparar() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAnoComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResultadoscomparativoControladorUrlEnum.URL4811
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validadVacios() {
        if (SysmanFunciones.validarVariableVacio(anioTrabajo)
            || SysmanFunciones.validarVariableVacio(mesTrabajo)
            || SysmanFunciones.validarVariableVacio(anioComparar)
            || SysmanFunciones.validarVariableVacio(mesComparar)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB468"));
            return true;
        }
        return false;
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (validadVacios()) {

            return;
        }
        if (Integer.parseInt(anioComparar) < Integer.parseInt(anioTrabajo))

        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB469"));
            return;
        }
        if (contArchivoarchivoModelo.getArchivo() == null)

        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB470"));
            return;
        }

        // Reemplazos consulta
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("anioTrabajo", anioTrabajo);
        reemplazar.put("anioComparar", anioComparar);
        reemplazar.put("cSaldo",

                        cSaldo());
        String strSql = Reporteador.resuelveConsulta("800045ERIFORMULADO",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoarchivoModelo.getArchivo())) {

            workbook = new HSSFWorkbook(fileIs);
            Sheet sheet = workbook.getSheet("Hoja1");

            if (sheet == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB471"));
                return;
            }
            String valorAmCompara = "";
            String valorAmTrabajo = "";
            Cell cellAC = sheet.getRow(7).createCell(4);
            Cell cellAT = sheet.getRow(7).createCell(6);
            switch (Integer.parseInt(mesComparar)) {
            case 1:
                valorAmCompara = idioma.getString("TB_TB472") + anioComparar;
                valorAmTrabajo = idioma.getString("TB_TB472") + anioTrabajo;
                break;
            case 2:
                valorAmCompara = idioma.getString("TB_TB473") + anioComparar;
                valorAmTrabajo = idioma.getString("TB_TB473") + anioTrabajo;
                break;
            case 3:
                valorAmCompara = idioma.getString("TB_TB474") + anioComparar;
                valorAmTrabajo = idioma.getString("TB_TB474") + anioTrabajo;
                break;
            case 4:
                valorAmCompara = idioma.getString("TB_TB475") + anioComparar;
                valorAmTrabajo = idioma.getString("TB_TB475") + anioTrabajo;
                break;
            default:
                break;
            }
            cellAC.setCellValue(valorAmCompara);
            cellAT.setCellValue(valorAmTrabajo);

            prepararInforme(sheet, strSql, "A", "FORMULAID");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.setForceFormulaRecalculation(true);
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "ERI FORMULADO.xls");
        }
        catch (IOException | JRException e) {
            Logger.getLogger(ResultadoscomparativoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private Object cSaldo() {
        int cSaldo = 0;
        if ("1".equals(mesTrabajo)) {
            cSaldo = 3;
        }
        else if ("2".equals(mesTrabajo)) {
            cSaldo = 6;
        }
        else if ("3".equals(mesTrabajo)) {
            cSaldo = 9;
        }
        else if ("4".equals(mesTrabajo)) {
            cSaldo = 12;
        }
        return cSaldo;
    }

    private void prepararInforme(Sheet sheet, String strSql,
        String strcolumnareferencia, String strCampoBuscar) {
        List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        strSql);
        if (!rs.isEmpty()) {

            envioDetalle(sheet, rs, strcolumnareferencia, strCampoBuscar);
        }

    }

    public void envioDetalle(Sheet sheet, List<Registro> rsBase,
        String strcolumnareferencia, String strCampoBuscar) {
        String signo1;
        String signo2;

        for (Registro registro : rsBase) {
            signo1 = "+";
            signo2 = "+";
            String busqueda = signo1 + registro.getCampos().get(strCampoBuscar)
                + signo2;
            int column = CellRangeAddress.valueOf("" + strcolumnareferencia
                + ":" + strcolumnareferencia + "").getFirstColumn();
            Cell cell = searchColumnSheet(busqueda, column, 0,
                            sheet.getLastRowNum(), sheet);
            if (cell != null) {
                String aux = sheet.getRow(cell.getRowIndex()).getCell(1)
                                .getNumericCellValue()
                    + (registro.getCampos().get("CODIGO_NIIF").toString());
                Cell newCell = sheet.getRow(cell.getRowIndex()).createCell(1);
                newCell.setCellValue(aux);
                double aux1 = sheet.getRow(cell.getRowIndex()).getCell(4)
                                .getNumericCellValue()
                    + ((BigDecimal) registro.getCampos().get("VALORACTUAL"))
                                    .doubleValue();
                Cell newCell1 = sheet.getRow(cell.getRowIndex()).createCell(4);
                newCell1.setCellValue(aux1);
                aux1 = sheet.getRow(cell.getRowIndex()).getCell(6)
                                .getNumericCellValue()
                    + ((BigDecimal) registro.getCampos().get("VALORCOMPARA"))
                                    .doubleValue();
                Cell newCell2 = sheet.getRow(cell.getRowIndex()).createCell(6);
                newCell2.setCellValue(aux1);

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

    public String getMesTrabajo() {
        return mesTrabajo;
    }

    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    public String getMesComparar() {
        return mesComparar;
    }

    public void setMesComparar(String mesComparar) {
        this.mesComparar = mesComparar;
    }

    public String getAnioTrabajo() {
        return anioTrabajo;
    }

    public void setAnioTrabajo(String anioTrabajo) {
        this.anioTrabajo = anioTrabajo;
    }

    public String getAnioComparar() {
        return anioComparar;
    }

    public void setAnioComparar(String anioComparar) {
        this.anioComparar = anioComparar;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public ContenedorArchivo getContArchivoarchivoModelo() {
        return contArchivoarchivoModelo;
    }

    public void setContArchivoarchivoModelo(
        ContenedorArchivo contArchivoarchivoModelo) {
        this.contArchivoarchivoModelo = contArchivoarchivoModelo;
    }

    public ContenedorArchivo getContArchivonombreArchivo() {
        return contArchivonombreArchivo;
    }

    public void setContArchivonombreArchivo(
        ContenedorArchivo contArchivonombreArchivo) {
        this.contArchivonombreArchivo = contArchivonombreArchivo;
    }

    public UploadedFile getArchivoCargarefNomArchivo() {
        return archivoCargarefNomArchivo;
    }

    public void setArchivoCargarefNomArchivo(
        UploadedFile archivoCargarefNomArchivo) {
        this.archivoCargarefNomArchivo = archivoCargarefNomArchivo;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public List<Registro> getListaAnoComparar() {
        return listaAnoComparar;
    }

    public void setListaAnoComparar(List<Registro> listaAnoComparar) {
        this.listaAnoComparar = listaAnoComparar;
    }
}
