/*-
 * FrmAmortizacionControlador.java
 *
 * 1.0
 * 
 * 07/02/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.enums.FrmAmortizacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite ver la amortizacion de una factura..
 *
 * @version 1.0, 07/02/2019
 * @author jreina
 */
@ManagedBean
@ViewScoped
public class FrmAmortizacionControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    private String tipo;
    private String nroAbono;
    private String deudaTotal;
    private String tasaInteres;
    private String cuotas;
    private String cuotaInicial;
    private String tercero;
    private String factura;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmAmortizacionControlador
     */
    public FrmAmortizacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_AMORTIZACION_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            if (parametros != null) {
                tipo = (String) parametros.get("tipoCobro");
                nroAbono = (String) parametros.get("nroAbono");
            }
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
        tabla = "SF_DETALLE_ABONO";
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), nroAbono);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        FrmAmortizacionControladorUrlEnum.URL4247.getValue());

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando73 en la vista
     *
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = null;
            String reporte = "001991InfAmortizacion";
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("abono", nroAbono);

            parametros.put("PR_TERCERO", tercero);
            parametros.put("PR_FACTURA", factura);
            parametros.put("PR_CUOTAINICIAL", cuotaInicial);
            parametros.put("PR_CAPITAL", deudaTotal);
            parametros.put("PR_TASAINTERES", tasaInteres);
            parametros.put("PR_CUOTAS", cuotas);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Comando75 en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("abono", nroAbono);
            String strSql = Reporteador.resuelveConsulta(
                            "001991InfAmortizacion",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            Sheet sheet = workbook.getSheet("Report");
            sheet.shiftRows(0, sheet.getLastRowNum(), 9);
            sheet.createFreezePane(0, 10);

            Font font2 = workbook.createFont();
            font2.setFontName("Arial");
            font2.setFontHeightInPoints((short) 10);
            font2.setBold(true);

            CellStyle style2 = workbook.createCellStyle();
            style2.setFont(font2);

            Row row = sheet.createRow(1);
            Cell cell = row.createCell(2);
            cell.setCellValue("AMORTIZACIÓN");
            cell.setCellStyle(style2);

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue("TERCERO:");
            cell.setCellStyle(style2);

            cell = row.createCell(1);
            cell.setCellValue(tercero);

            row = sheet.createRow(4);
            cell = row.createCell(0);
            cell.setCellValue("Nº FACTURA:");
            cell.setCellStyle(style2);

            cell = row.createCell(1);
            cell.setCellValue(factura);

            cell = row.createCell(3);
            cell.setCellValue("CUOTA INICIAL:");
            cell.setCellStyle(style2);

            cell = row.createCell(4);
            cell.setCellValue(cuotaInicial);

            row = sheet.createRow(5);
            cell = row.createCell(0);
            cell.setCellValue("CAPITAL:");
            cell.setCellStyle(style2);

            cell = row.createCell(1);
            cell.setCellValue(deudaTotal);

            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell.setCellValue("TASA INTERES:");
            cell.setCellStyle(style2);

            cell = row.createCell(1);
            cell.setCellValue(tasaInteres);

            row = sheet.createRow(7);
            cell = row.createCell(0);
            cell.setCellValue("Nº CUOTAS:");
            cell.setCellStyle(style2);

            cell = row.createCell(1);
            cell.setCellValue(cuotas);

            sheet.autoSizeColumn(0);

            workbook.write(out);

            archivoDescarga = JsfUtil
                            .getArchivoDescarga(new ByteArrayInputStream(
                                            out.toByteArray()),
                                            "Amortizacion" + ".xls");
            workbook.close();
        }
        catch (IOException | JRException | SQLException | DRException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.TIPO.getName(), tipo);
            param.put(GeneralParameterEnum.CODIGO.getName(), nroAbono);

            Registro reg = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmAmortizacionControladorUrlEnum.URL5471
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if(reg != null) {
                deudaTotal = reg.getCampos().get("DEUDA_TOTAL").toString();
                tasaInteres = reg.getCampos().get("TASA_INTERES").toString();
                cuotas = reg.getCampos().get("CUOTAS").toString();
                cuotaInicial = reg.getCampos().get("CUOTA_INICIAL").toString();
                tercero = reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();
                factura = reg.getCampos().get("NRO_FACT").toString();
            }
            

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }
    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable deudaTotal
     * 
     * @return deudaTotal
     */
    public String getDeudaTotal() {
        return deudaTotal;
    }

    /**
     * Asigna la variable deudaTotal
     * 
     * @param deudaTotal
     * Variable a asignar en deudaTotal
     */
    public void setDeudaTotal(String deudaTotal) {
        this.deudaTotal = deudaTotal;
    }

    /**
     * Retorna la variable tasaInteres
     * 
     * @return tasaInteres
     */
    public String getTasaInteres() {
        return tasaInteres;
    }

    /**
     * Asigna la variable tasaInteres
     * 
     * @param tasaInteres
     * Variable a asignar en tasaInteres
     */
    public void setTasaInteres(String tasaInteres) {
        this.tasaInteres = tasaInteres;
    }

    /**
     * Retorna la variable cuotas
     * 
     * @return cuotas
     */
    public String getCuotas() {
        return cuotas;
    }

    /**
     * Asigna la variable cuotas
     * 
     * @param cuotas
     * Variable a asignar en cuotas
     */
    public void setCuotas(String cuotas) {
        this.cuotas = cuotas;
    }

    /**
     * Retorna la variable cuotaInicial
     * 
     * @return cuotaInicial
     */
    public String getCuotaInicial() {
        return cuotaInicial;
    }

    /**
     * Asigna la variable cuotaInicial
     * 
     * @param cuotaInicial
     * Variable a asignar en cuotaInicial
     */
    public void setCuotaInicial(String cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
