package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.planeacion.enums.InformeplancomprasControladorEnum;
import com.sysman.planeacion.enums.InformeplancomprasControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 05/01/2016
 * 
 * @version 2, 08/09/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class InformeplancomprasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el nombre de la compania
     * desde la cual el usuario inicio sesion
     */
    private final String nombreCompania;

    /**
     * Constante a nivel de clase que aloja el nit de la compania
     * desde la cual el usuario inicio sesion
     */
    private final String nitCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>Calibri</code>
     */
    private final String cCalibri;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    private String anio;
    private String tipoIdentificacion;
    private String identificacion;
    private StreamedContent archivoDescarga;
    private List<Registro> listacmbAno;

    /**
     * TODO DOCUMENTACION NECESARIA
     */
    private List<Registro> listacmbTipContrato;

    /**
     * Creates a new instance of InformeplancomprasControlador
     */
    public InformeplancomprasControlador() {
        super();

        compania = SessionUtil.getCompania();
        nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
        nitCompania = SessionUtil.getCompaniaIngreso().getNit();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cCalibri = InformeplancomprasControladorEnum.CALIBRI.getValue();
        cAno = GeneralParameterEnum.ANO.getName();

        try {
            // 441
            numFormulario = GeneralCodigoFormaEnum.INFORMEPLANCOMPRAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.DETALLE_PLAN_COMPRAS.getTable();
        registro = new Registro(new HashMap<String, Object>());

        buscarLlave();
        reasignarOrigen();
        cargarListacmbAno();
        cargarListacmbTipContrato();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(InformeplancomprasControladorUrlEnum.URL2407
                                        .getValue());

        urlActualizacion = UrlServiceUtil
                        .getUrlBeanById(InformeplancomprasControladorUrlEnum.URL0003
                                        .getValue());

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cAno, anio);
    }

    /**
     * Carga la lista: <code>listacmbTipContrato</code> asociada al
     * combo Tipo Contratacion en la grilla.
     */
    public void cargarListacmbTipContrato() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listacmbTipContrato = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeplancomprasControladorUrlEnum.URL0004
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        /*-
        listacmbTipContrato = service.getListado(conectorPool, "SELECT " +
            "   CODIGO" +
            "  ,DESCRIPCION" +
            " FROM TIPOADJUDICACION" +
            " WHERE COMPANIA = '" + compania + "'" +
            " ORDER BY DESCRIPCION");*/
    }

    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public void cargarListacmbAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeplancomprasControladorUrlEnum.URL6632
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirExcel(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (listaInicial.getRowCount() == 0) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3525")
                            .replace("#ANIO#", anio));

            return;
        }
        else {
            armarExcel();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcmbAno() {
        tipoIdentificacion = null;
        identificacion = null;

        reasignarOrigen();
    }

    public void armarExcel() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAno, anio);

        Registro regTotal = null;
        List<Registro> listDetalles = null;

        try {
            // Consulta para sacar el Valor Total [Máximo 20
            // digitos]No
            // utilice comas, puntos ni signo $
            regTotal = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeplancomprasControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                            param));

            // Consulta para sacar datos de la contratacion
            listDetalles = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeplancomprasControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        try (Workbook workbook = new HSSFWorkbook();) {

            Sheet sheet = workbook.createSheet("PlandeCompras");

            Font font = workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setFontName(cCalibri);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            Font fondo = workbook.createFont();
            fondo.setFontHeightInPoints((short) 12);
            fondo.setFontName(cCalibri);

            CellStyle styless = workbook.createCellStyle();
            styless.setFont(fondo);

            CellStyle style = workbook.createCellStyle();
            style.setFont(font);

            Font fontDetalle = workbook.createFont();
            fontDetalle.setFontHeightInPoints((short) 11);
            fontDetalle.setFontName(cCalibri);
            fontDetalle.setColor(HSSFColor.WHITE.index);
            fontDetalle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            CellStyle styles = workbook.createCellStyle();
            styles.setFont(fontDetalle);
            styles.setFillForegroundColor(HSSFColor.DARK_BLUE.index);
            styles.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            styles.setAlignment(CellStyle.ALIGN_CENTER);

            Row rowTitulo = sheet.createRow(0);
            Cell cellTitulo = rowTitulo.createCell(1);
            cellTitulo.setCellValue(idioma.getString("TB_TB3527"));
            sheet.setColumnWidth(1, 12000);
            cellTitulo.setCellStyle(style);

            Row rowTitulo1 = sheet.createRow(1);
            Cell cellTitulo1 = rowTitulo1.createCell(0);
            cellTitulo1.setCellValue(idioma.getString("TB_TB3528"));
            sheet.setColumnWidth(0, 12000);
            cellTitulo1.setCellStyle(styless);

            // Encabezado Primera consulta
            Row encabezado = sheet.createRow(2);
            Cell cellnombre = encabezado.createCell(0);
            cellnombre.setCellValue(idioma.getString("TB_TB3529"));
            cellnombre.setCellStyle(styles);

            Cell cellnit = encabezado.createCell(1);
            cellnit.setCellValue(idioma.getString("TB_TB3530"));
            cellnit.setCellStyle(styles);

            Cell celltipoIdentificacion = encabezado.createCell(2);
            celltipoIdentificacion.setCellValue(idioma.getString("TB_TB3531"));
            sheet.setColumnWidth(2, 11000);
            celltipoIdentificacion.setCellStyle(styles);

            Cell cellCedula = encabezado.createCell(3);
            cellCedula.setCellValue(idioma.getString("TB_TB3532"));
            sheet.setColumnWidth(3, 14000);
            cellCedula.setCellStyle(styles);

            Cell cellAno = encabezado.createCell(4);
            cellAno.setCellValue(idioma.getString("TB_TB3533"));
            sheet.setColumnWidth(4, 5000);
            cellAno.setCellStyle(styles);

            Cell cellValortotal = encabezado.createCell(5);
            cellValortotal.setCellValue(idioma.getString("TB_TB3534"));
            sheet.setColumnWidth(5, 15000);
            cellValortotal.setCellStyle(styles);

            // Asignando valores a las celdas de la consulta
            Row rowValor = sheet.createRow(3);
            Cell cellNombreCompania = rowValor.createCell(0);
            cellNombreCompania.setCellValue(this.nombreCompania);
            cellNombreCompania.setCellStyle(styless);

            Cell cellNitCompania = rowValor.createCell(1);
            cellNitCompania.setCellValue(this.nitCompania);
            cellNitCompania.setCellStyle(styless);

            Cell tipoI = rowValor.createCell(2);
            tipoI.setCellValue(tipoIdentificacion);
            tipoI.setCellStyle(styless);

            Cell cedu = rowValor.createCell(3);
            cedu.setCellValue(identificacion);
            cedu.setCellStyle(styless);

            Cell aniio = rowValor.createCell(4);
            aniio.setCellType(Cell.CELL_TYPE_NUMERIC);
            aniio.setCellValue(anio);
            aniio.setCellStyle(styless);

            // Asignando valor Total a la celda
            Cell valorTotal = rowValor.createCell(5);
            valorTotal.setCellStyle(styless);

            valorTotal.setCellValue(regTotal == null ? "0"
                : regTotal.getCampos().get("TOTAL").toString());

            // ENCABEZADO PARA TIPO_CONTRATACION
            Row tipoContratacion = sheet.createRow(5);
            Cell cellCodigoCubs = tipoContratacion.createCell(0);
            cellCodigoCubs.setCellValue(idioma.getString("TB_TB3535"));
            cellCodigoCubs.setCellStyle(styles);

            Cell celltipoContratacion = tipoContratacion.createCell(1);
            celltipoContratacion.setCellValue(idioma.getString("TB_TB3536"));
            celltipoContratacion.setCellStyle(styles);

            Cell cellMes = tipoContratacion.createCell(2);
            cellMes.setCellValue(idioma.getString("TB_TB3537"));
            cellMes.setCellStyle(styles);

            Cell cellCantidad = tipoContratacion.createCell(3);
            cellCantidad.setCellValue(idioma.getString("TB_TB3538"));
            cellCantidad.setCellStyle(styles);

            Cell cellValorComprar = tipoContratacion.createCell(4);
            cellValorComprar.setCellValue(idioma.getString("TB_TB3539"));
            sheet.setColumnWidth(4, 8000);
            cellValorComprar.setCellStyle(styles);

            Cell cellDescripcion = tipoContratacion.createCell(5);
            cellDescripcion.setCellValue(
                            idioma.getString("TG_DESCRIPCION_DEL_ELEMENTO2"));
            cellDescripcion.setCellStyle(styles);

            int cont = 6;
            for (Registro valor : listDetalles) {
                Row rowContratacion = sheet.createRow(cont);
                Cell codigoC = rowContratacion.createCell(0);
                codigoC.setCellStyle(styless);

                if (valor.getCampos().get("CODIGOCUBS") == null
                    || valor.getCampos().get("TIPO_CONTRATACION") == null) {
                    codigoC.setCellValue("");
                }
                else {
                    codigoC.setCellValue(valor.getCampos().get("CODIGOCUBS")
                                    .toString());
                }

                Cell modalidad = rowContratacion.createCell(1);
                modalidad.setCellValue(SysmanFunciones
                                .nvl(valor.getCampos().get("TIPO_CONTRATACION"),
                                                "")
                                .toString());

                modalidad.setCellStyle(styless);

                Cell mesProyectado = rowContratacion.createCell(2);
                mesProyectado.setCellType(Cell.CELL_TYPE_NUMERIC);
                mesProyectado.setCellValue(
                                valor.getCampos().get("MES").toString());
                mesProyectado.setCellStyle(styless);

                Cell cantidad = rowContratacion.createCell(3);
                cantidad.setCellType(Cell.CELL_TYPE_NUMERIC);
                cantidad.setCellValue(Double.parseDouble(
                                valor.getCampos().get("CANTIDAD").toString()));
                cantidad.setCellStyle(styless);

                Cell iva = rowContratacion.createCell(4);
                iva.setCellType(Cell.CELL_TYPE_NUMERIC);

                iva.setCellValue(Double.parseDouble(valor.getCampos()
                                .get("VALORACOMPRAR").toString()));

                iva.setCellStyle(styless);

                Cell descripcion = rowContratacion.createCell(5);

                descripcion.setCellValue(valor.getCampos().get("DESCRIPCION")
                                .toString());

                descripcion.setCellStyle(styless);
                cont++;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);
            out.close();

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "InformePlandeCompras.xls");
        }
        catch (IOException | JRException ex) {
            Logger.getLogger(InformeplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cAno);
        registro.getCampos().remove("RUBRO");
        registro.getCampos().remove("CODIGO");
        registro.getCampos().remove("DEPENDENCIA");
        registro.getCampos().remove("MES");
        registro.getCampos().remove("CODIGOCUBS");
        registro.getCampos().remove("NOMBRECORTO");
        registro.getCampos().remove("NOMTIPOCONTRATACION");

        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListacmbTipContrato() {
        return listacmbTipContrato;
    }

    public void setListacmbTipContrato(List<Registro> listacmbTipContrato) {
        this.listacmbTipContrato = listacmbTipContrato;
    }

}
