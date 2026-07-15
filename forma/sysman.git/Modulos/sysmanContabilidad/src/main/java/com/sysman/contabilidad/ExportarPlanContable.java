/*-
 * ExportarPlanContable.java
 *
 * 1.0
 *
 * 25/11/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.ExportarPlanContableUrlEnum;
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
import com.sysman.reportes.Reporteador;
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

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador que permite exportar el plan contable base en formato
 * Excel para una compania y anio seleccionados
 *
 * @author jlozano
 * @version 1.0, 25/11/2016
 * @modifier amonroy
 * @version 2, 07/04/2017 Proceso de Refactoring para los listados de
 * anio y compania
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped
public class ExportarPlanContable extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio seleccionado
     */
    private String ano;
    /**
     * Atributo que almacena la compania seleccionada
     */
    private String companiaTrabajo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de anios disponibles
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Lista de companias disponibles
     */
    private List<Registro> listacompania;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ExportarPlanContable
     */
    public ExportarPlanContable() {
        super();
        compania = SessionUtil.getCompania();
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        companiaTrabajo = compania;
        try {
            numFormulario = GeneralCodigoFormaEnum.EXPORTAR_PLAN_CONTABLE
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
        cargarListaAnoTrabajo();
        cargarListacompania();
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
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExportarPlanContableUrlEnum.URL4723
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
     * Carga la lista listacompania
     *
     */
    public void cargarListacompania() {
        try {
            listacompania = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ExportarPlanContableUrlEnum.URL5327
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
     * Metodo ejecutado al oprimir el boton Comando76 en la vista
     *
     * Exporta los datos del plan contable base en formato Excel
     *
     */
    public void oprimirComando76() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    public void generarExcel() {
        HashMap<String, Object> reemplazar = new HashMap<>();

        reemplazar.put("companiaTrabajo", companiaTrabajo);
        reemplazar.put("anoTrabajo", ano);
        String strSql = Reporteador.resuelveConsulta("800072PlanContableBase",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar);

        try (Workbook workbook = new HSSFWorkbook(
                        JsfUtil.exportarHojaDatosStreamed(strSql,
                                        ConectorPool.ESQUEMA_SYSMAN,
                                        FORMATOS.EXCEL97).getStream())) {
            if (workbook.getSheetIndex("Report") >= 0) {
                workbook.setSheetName(workbook.getSheetIndex("Report"),
                                "PLAN_CONTABLE_BASE");
                workbook.setForceFormulaRecalculation(true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                out.close();
                workbook.close();
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                "PLAN_CONTABLE_BASE.xls");
            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2470"));
            }
        }
        catch (SQLException | IOException | JRException
                        | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable companiaTrabajo
     *
     * @return companiaTrabajo
     */
    public String getCompaniaTrabajo() {
        return companiaTrabajo;
    }

    /**
     * Asigna la variable companiaTrabajo
     *
     * @param companiaTrabajo
     * Variable a asignar en companiaTrabajo
     */
    public void setCompaniaTrabajo(String companiaTrabajo) {
        this.companiaTrabajo = companiaTrabajo;
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
     * Retorna la lista listaAnoTrabajo
     *
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     *
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listacompania
     *
     * @return listacompania
     */
    public List<Registro> getListacompania() {
        return listacompania;
    }

    /**
     * Asigna la lista listacompania
     *
     * @param listacompania
     * Variable a asignar en listacompania
     */
    public void setListacompania(List<Registro> listacompania) {
        this.listacompania = listacompania;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
