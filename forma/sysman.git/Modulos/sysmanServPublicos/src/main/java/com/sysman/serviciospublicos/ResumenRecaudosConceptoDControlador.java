/*-
 * ResumenRecaudosConceptoDControlador.java
 *
 * 1.0
 * 
 * 04/11/2016
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
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.ResumenRecaudosConceptoDControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma resumenrecaudosconceptod asociada al
 * formulario Abonos por Concepto.
 *
 * @version 1.0, 04/11/2016
 * @author Pablo Andres Espitia Cuca
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author eamaya
 * @version 3.0, Proceso de Refactoring DSS
 */
@ManagedBean
@ViewScoped

public class ResumenRecaudosConceptoDControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que contiene el valor asignado al control de tipo de
     * informe en la forma del formulario.
     */
    private String tipoInforme;

    /**
     * Atributo que contiene el valor asignado al periodo inicial en
     * la forma del formulario.
     */
    private String periodoInicial;

    /**
     * Atributo que contiene el valor asignado al periodo final en la
     * forma del formulario.
     */
    private String periodoFinal;

    /**
     * Atributo que contiene el valor asignado a la fecha inicial en
     * la forma del formulario.
     */
    private Date fechaInicial;

    /**
     * Atributo que contiene el valor asignado a la fecha final en la
     * forma del formulario.
     */
    private Date fechaFinal;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /** Lista que contiene los detalles del combo periodo inicial. */
    private List<Registro> listaPeriodoInicial;

    /** Lista que contiene los detalles del combo periodo final. */
    private List<Registro> listaPeriodoFinal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ResumenRecaudosConceptoDControlador
     */
    public ResumenRecaudosConceptoDControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_RECAUDOS_CONCEPTO_DCONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            tipoInforme = "1";

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
        cargarListaPeriodoInicial();
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
    /** Carga los detalles en la lista del combo periodo inicial. */
    public void cargarListaPeriodoInicial() {

        try {
            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaPeriodoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenRecaudosConceptoDControladorUrlEnum.URL5292
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /** Carga los detalles en la lista del combo periodo final. */
    public void cargarListaPeriodoFinal() {

        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.PERIODO.getName(), periodoInicial);

            listaPeriodoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenRecaudosConceptoDControladorUrlEnum.URL6363
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
     * Metodo ejecutado al oprimir el boton Pdf en la vista. Gestiona
     * el evento de la accion.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista.
     * Gestiona el evento de la accion.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Extension o tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String reporte = seleccionarReporte();

            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("fechaInicial", formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", formatearFecha(fechaFinal));
            reemplazar.put("periodoInicial", "'" + periodoInicial + "'");
            reemplazar.put("periodoFinal", "'" + periodoFinal + "'");

            // </REEMPLAZAR VARIABLES EN CONSULTA>

            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_FECHAINICIAL", convertirFecha(fechaInicial));
            parametros.put("PR_FECHAFINAL", convertirFecha(fechaFinal));
            parametros.put("PR_PERIODOINICIAL", extraerPeriodoInicial());
            parametros.put("PR_PERIODOFINAL", extraerPeriodoFinal());

            // </ENVIAR PARAMETROS AL REPORTE>

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex) {
            Logger.getLogger(
                            ResumenRecaudosConceptoDControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Util para recuperar el nombre del periodo inicial seleccionado
     * en el formulario.
     * 
     * @return El nombre del periodo en formato Month/YYYY.
     */
    public String extraerPeriodoInicial() {
        return service.buscarEnLista(periodoInicial, "PERCOMPLETO",
                        "NOMBREDEPERIODO", listaPeriodoInicial);
    }

    /**
     * Util para recuperar el nombre del periodo final seleccionado en
     * el formulario.
     * 
     * @return El nombre del periodo en formato Month/YYYY.
     */
    public String extraerPeriodoFinal() {
        return service.buscarEnLista(periodoFinal, "PERCOMPLETO",
                        "NOMBREDEPERIODO", listaPeriodoFinal);
    }

    /**
     * Determina cual reporte se debe generar dependiendo del valor de
     * la variable tipoInforme.
     * 
     * @return El nombre del reporte a generar.
     */
    public String seleccionarReporte() {
        String reporte;

        switch (tipoInforme) {
        case "1":
            reporte = "001216LRecaudosAbonos";
            break;
        case "2":
            reporte = "001223LRecaudosAbonosUsu";
            break;
        default:
            reporte = "001224LrecaudosAbonosBanco";
            break;
        }

        return reporte;
    }

    /**
     * Asigna el formato TO_DATE(DD/MM/YYYY) a la fecha ingresada por
     * parametro.
     * 
     * @param fechaDate
     * Fecha a la cual se le hace el casting.
     * @return La fecha formateada.
     */
    public String formatearFecha(Date fechaDate) {
        return SysmanFunciones.formatearFecha(fechaDate);
    }

    /**
     * Parsea la fecha que ingresa por parametro de tipo Date a tipo
     * String.
     * 
     * @param fechaDate
     * Variable de tipo Date a la cual se le va a hacer el casting.
     * @return La fecha cadena en formato DD/MM/YYYY.
     */
    public String convertirFecha(Date fechaDate) {
        String fechaStr = " ";
        try {
            fechaStr = SysmanFunciones.convertirAFechaCadena(fechaDate);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return fechaStr;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control PeriodoInicial en el
     * formulario.
     */
    public void cambiarPeriodoInicial() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodoFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoInforme
     * 
     * @return tipoInforme
     */
    public String getTipoInforme() {
        return tipoInforme;
    }

    /**
     * Asigna la variable tipoInforme
     * 
     * @param tipoInforme
     * Variable a asignar en tipoInforme
     */
    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
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
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
