/*-
 * FrmEjePptalgMetControlador.java
 *
 * 1.0
 * 
 * 11/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

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
import com.sysman.presupuesto.enums.EjecucionMensualGastosControladorEnum;
import com.sysman.presupuesto.enums.FrmEjePptalgMetControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar los gastos detallados por mes
 *
 * @version 1.0, 11/09/2018
 * @author jgomezp
 */
@ManagedBean
@ViewScoped
public class FrmEjePptalgMetControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compania
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena la cuenta inicial
     */
    private String cuentaInicial;
    /**
     * variable que almacena la cuenta final
     */
    private String cuentaFinal;
    /**
     * variable que almacena la mes inicial
     */
    private int mesInicial;
    /**
     * variable que almacena la mes final
     */
    private int mesFinal;
    /**
     * variable que almacena ano
     */
    private int ano;
    /**
     * variable que almacena el reporte
     */
    private String strSql;
    /**
     * variable que almacena la observacion
     */
    private String observacion;
    /**
     * varible que alacmena el nivel
     */
    private int nivel;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listas que almacenan el mes inicial y final
     */
    private List<Registro> listaMesInicial;

    private List<Registro> listaMesFinal;
    /**
     * Lista que almacena el ano
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que almancena la cuenta incial
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * Lista que almacena la cuenta final
     */
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmEjePptalgMetControlador
     */
    public FrmEjePptalgMetControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_EJE_PPTALG_MET_CONTROLADOR
                            .getCodigo();
            validarPermisos();

        }
        catch (Exception ex)
        {
            Logger.getLogger(AimregistroejecucgastosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        cargarListaAno();
        ano = SysmanFunciones.ano(new Date());
        cargarListaCuentaInicial();
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {

        nivel = 16;
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     */
    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEjePptalgMetControladorUrlEnum.URL0008
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEjePptalgMetControladorUrlEnum.URL0037
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        param.put(EjecucionMensualGastosControladorEnum.PARAM1.getValue(),
                        "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");

    }

    /**
     * 
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmEjePptalgMetControladorUrlEnum.URL0035
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("ANO", ano);
        param.put("CUENTAINICIAL", cuentaInicial);
        param.put("NATURALEZA", "D");
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "CODIGO");

    }

    /**
     * 
     * Carga la lista listaMesInicial
     *
     */
    public void cargarListaMesInicial()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("ANIO", ano);
        try
        {
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEjePptalgMetControladorUrlEnum.URL0017
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Carga la lista listaMesFinal
     *
     */
    public void cargarListaMesFinal()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put("COMPANIA", compania);
        param.put("ANIO", ano);
        try
        {
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmEjePptalgMetControladorUrlEnum.URL0018
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaEXCEL(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    @SuppressWarnings("deprecation")
    public void generaEXCEL(FORMATOS formato)
    {
    	
    	String nombreCompania = SessionUtil.getCompaniaIngreso().getNombre();
    	
        try (ByteArrayOutputStream out = new ByteArrayOutputStream())
        {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", ano);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("nivel", nivel);
            String str = Reporteador.resuelveConsulta("800194EjecucionGastosMes",
                            Integer.parseInt(SessionUtil.getModulo()), reemplazar);
            Workbook workbook = new XSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(str,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato).getStream());

            Sheet sheet = workbook.getSheetAt(0);

            sheet.shiftRows(0, sheet.getLastRowNum(),
                            2);

            sheet.createFreezePane(0, 3);

            Font font2 = workbook.createFont();
            font2.setFontName("Calibri");
            font2.setFontHeightInPoints((short) 14);
            font2.setBold(false);

            CellStyle style2 = workbook.createCellStyle();
            style2.setAlignment(CellStyle.ALIGN_CENTER);
            style2.setFont(font2);
            style2.setBorderBottom(CellStyle.BORDER_THIN);
            style2.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style2.setBorderLeft(CellStyle.BORDER_THIN);
            style2.setLeftBorderColor(IndexedColors.BLACK.getIndex());

            Row r = sheet.createRow(0);

            Cell cell1 = r.createCell(7); // 0
            cell1.setCellValue(nombreCompania.toUpperCase());

            Row r1 = sheet.createRow(1);

            cell1 = r1.createCell(6);// 0
            cell1.setCellValue("EJECUCION PRESUPUESTAL DE GASTOS VIGENCIA FISCAL " + ano);

            for (int i = 3; i < sheet.getLastRowNum() + 1; i++)
            {
                Row r3 = sheet.getRow(i);
                for (int j = 0; j < 11; j++)
                {
                    @SuppressWarnings("unused")
                    Cell cell2 = r3.getCell(j);
                }
            }

            workbook.write(out);

            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()), "EjecucionGastosMes.xlsx");
            workbook.close();

        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage()
                            + ". Verifique que no hayan sido generados previamente.");
        }

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CODIGO"), " ").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("CODIGO"), " ").toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaMesInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMesInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        mesInicial = (int) registroAux.getCampos().get("NUMERO");

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaMesFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaMesFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        mesFinal = (int) registroAux.getCampos().get("NUMERO");
    }

    public void cambiarAno()
    {
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable cuentaInicial
     * 
     * @return cuentaInicial
     */
    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    /**
     * Asigna la variable cuentaInicial
     * 
     * @param cuentaInicial
     * Variable a asignar en cuentaInicial
     */
    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    /**
     * Retorna la variable cuentaFinal
     * 
     * @return cuentaFinal
     */
    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    /**
     * Asigna la variable cuentaFinal
     * 
     * @param cuentaFinal
     * Variable a asignar en cuentaFinal
     */
    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    /**
     * Retorna la variable mesInicial
     * 
     * @return mesInicial
     */
    public int getMesInicial()
    {
        return mesInicial;
    }

    /**
     * Asigna la variable mesInicial
     * 
     * @param mesInicial
     * Variable a asignar en mesInicial
     */
    public void setMesInicial(int mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    /**
     * Retorna la variable mesFinal
     * 
     * @return mesFinal
     */
    public int getMesFinal()
    {
        return mesFinal;
    }

    /**
     * Asigna la variable mesFinal
     * 
     * @param mesFinal
     * Variable a asignar en mesFinal
     */
    public void setMesFinal(int mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    /**
     * Retorna la variable observacion
     * 
     * @return observacion
     */
    public String getObservacion()
    {
        return observacion;
    }

    /**
     * Asigna la variable observacion
     * 
     * @param observacion
     * Variable a asignar en observacion
     */
    public void setObservacion(String observacion)
    {
        this.observacion = observacion;
    }

    /**
     * 
     * 
     * /** Retorna la variable nivel
     * 
     * @return nivel
     */
    public int getNivel()
    {
        return nivel;
    }

    /**
     * Asigna la variable nivel
     * 
     * @param nivel
     * Variable a asignar en nivel
     */
    public void setNivel(int nivel)
    {
        this.nivel = nivel;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCuentaInicial
     * 
     * @return listaCuentaInicial
     */

    /**
     * Retorna la lista listaMesInicial
     * 
     * @return listaMesInicial
     */
    public List<Registro> getListaMesInicial()
    {
        return listaMesInicial;
    }

    /**
     * @return the listaCuentaInicial
     */
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    /**
     * @param listaCuentaInicial
     * the listaCuentaInicial to set
     */
    public void setListaCuentaInicial(
                    RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    /**
     * @return the listaCuentaFinal
     */
    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    /**
     * @param listaCuentaFinal
     * the listaCuentaFinal to set
     */
    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    /**
     * Asigna la lista listaMesInicial
     * 
     * @param listaMesInicial
     * Variable a asignar en listaMesInicial
     */
    public void setListaMesInicial(List<Registro> listaMesInicial)
    {
        this.listaMesInicial = listaMesInicial;
    }

    /**
     * Retorna la lista listaMesFinal
     * 
     * @return listaMesFinal
     */
    public List<Registro> getListaMesFinal()
    {
        return listaMesFinal;
    }

    /**
     * Asigna la lista listaMesFinal
     * 
     * @param listaMesFinal
     * Variable a asignar en listaMesFinal
     */
    public void setListaMesFinal(List<Registro> listaMesFinal)
    {
        this.listaMesFinal = listaMesFinal;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getStrSql()
    {
        return strSql;
    }

    public void setStrSql(String strSql)
    {
        this.strSql = strSql;
    }

    public String getCompania()
    {
        return compania;
    }

    public String getModulo()
    {
        return modulo;
    }
}