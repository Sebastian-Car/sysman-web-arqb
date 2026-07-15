/*-
 * ResumenRecaudosConceptoControlador.java
 *
 * 1.0
 * 
 * 13/12/2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma resumenrecaudosconcepto asociada al
 * formulario Resumen de recaudos por concepto. Que permite generar un
 * informe normal, agrupado por servicios, por financiaciones,
 * unificado por abonos, unificado por abonos y financiaciones o
 * unificado por bancos, abonos y financiamientos, de los recaudos en
 * un periodo definido entre una fecha inicial y una final.
 *
 * @version 1.0, 13/12/2016
 * @author jlramirez
 * 
 * @version 2, 15/06/2017
 * @author jreina se realizaron los cambios de refactoring y ejb.
 * 
 */

@ManagedBean
@ViewScoped
public class ResumenRecaudosConceptoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente.
     */
    private final String compania;
    
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que contiene el valor asignado al control de tipo de
     * informe que se quiere generar en la forma del formulario.
     */
    private String tipoInforme;
    /**
     * Variable encargada de almacenar temporalmente la fecha
     * seleccionada en el campo Fecha Inicial del formulario.
     */
    private Date fechaInicial;
    /**
     * Variable encargada de almacenar temporalmente la fecha
     * seleccionada en el campo Fecha Final del formulario.
     */
    private Date fechaFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    private StreamedContent archivoDescarga;

    private String cierrePeriodo;
    private String calidad;
    private String mostrarBanco;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ResumenRecaudosConceptoControlador
     */
    public ResumenRecaudosConceptoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo=SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_RECAUDOS_CONCEPTO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            tipoInforme = "1";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenRecaudosConceptoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
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
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     * Metodo ejecutado cuando se oprime el boton de Excel en el
     * formulario
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     * Metodo ejecutado cuando se oprime el boton de PDF en el
     * formulario
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método que genera un reporte con el formato elegido por el
     * usuario y de los valores de los parámetros
     * "CIERRE DE PERIODO DISCRIMINADO", "FORMATO CALIDAD" y
     * "NO MOSTRAR BANCO RELIQUIDACIONES 099".
     * 
     * @param formato
     * Extension o tipo de reporte a generar.
     */
    private void generarReporte(FORMATOS formato) {
        evaluarParametros();
        String reporte = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reporte = seleccionarReporte(cierrePeriodo, calidad);
            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("fechaInicial", formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", formatearFecha(fechaFinal));
            reemplazar.put("condicion", evaluarCondicion(mostrarBanco));
            // </REEMPLAZAR VARIABLES EN CONSULTA>
            // <ENVIAR PARAMETROS AL REPORTE>
            parametros.put("PR_FECHAINICIAL", convertirFecha(fechaInicial));
            parametros.put("PR_FECHAFINAL", convertirFecha(fechaFinal));
            // </ENVIAR PARAMETROS AL REPORTE>
            if ("001319LResumenRecaudosConceptoAbonosFin".equals(reporte)) {
                Reporteador.resuelveConsulta(
                                "001316LResumenRecaudosConceptoAbonosFinCos",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
            }
            else {
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
            }
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), "<br>",
                            e.getMessage()));
        }
    }

    /**
     * Método en el que se obtiene el valor de determinados parámetros
     * para su posterior evaluación
     */
    private void evaluarParametros() {
        try {
            cierrePeriodo = ejbSysmanUtilRemote.consultarParametro(compania,
                            "CIERRE DE PERIODO DISCRIMINADO", modulo,
                            new Date(), true);

            calidad = ejbSysmanUtilRemote.consultarParametro(compania,
                            "FORMATO CALIDAD", modulo, new Date(), true);

            mostrarBanco = ejbSysmanUtilRemote.consultarParametro(compania,
                            "NO MOSTRAR BANCO RELIQUIDACIONES 099", modulo,
                            new Date(), true);

            if (cierrePeriodo == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2727"));
                return;
            }
            if (calidad == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2730"));
                return;
            }

            if (mostrarBanco == null) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2729"));
                return;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(ResumenRecaudosConceptoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Método el el que se determina si se agrega una condición
     * adicional a la consulta del reporte, teniendo en cuenta el
     * parámetro del sistema "NO MOSTRAR BANCO RELIQUIDACIONES 099"
     * 
     * @param mostrarBanco
     * Valor obtenido de analizar el parámetro
     * "NO MOSTRAR BANCO RELIQUIDACIONES 099"
     * @return cadena de caracteres con la condicion que se debe
     * agregar a la consulta del reporte
     */
    private String evaluarCondicion(String mostrarBanco) {
        String condicion = "";
        if ("6".equals(tipoInforme)) {

            if ("SI".equals(mostrarBanco)) {
                condicion = "WHERE CNSRECAUPAGOSPERIODOBANCOPER.BANCO<>'099'";
            }
            else if ("NO".equals(mostrarBanco)) {
                condicion = "";
            }

        }
        return condicion;
    }

    /**
     * Método que determina cual reporte se debe generar dependiendo
     * del valor de la variable tipoInforme.
     * 
     * @return El nombre del reporte a generar.
     */
    public String seleccionarReporte(String cierrePeriodo, String calidad) {
        String reporte;
        String finCos = "001316LResumenRecaudosConceptoAbonosFinCos";
        switch (tipoInforme) {
        case "1":
            reporte = evaluarReporte1(cierrePeriodo, calidad, finCos);
            break;
        case "2":
            reporte = "001291LResumenRecaudosAgrupado";
            break;
        case "3":
            reporte = "001297LResumenRecaudosConceptoDiscriminado";
            break;
        case "4":
            reporte = evaluarReporte4(cierrePeriodo);
            break;
        case "5":
            reporte = evaluarReporte5(cierrePeriodo, calidad, finCos);
            break;
        default:
            reporte = "001309LResRecConAboFinBan";
            break;
        }
        return reporte;
    }

    /**
     * Método que evalúa cual reporte se debe generar en caso de que
     * se haya seleccionado el indicador Normal, también se tienen en
     * cuenta los parámetros del sistema
     * "CIERRE DE PERIODO DISCRIMINADO" y "FORMATO CALIDAD"
     * 
     * @param cierrePeriodo
     * Valor obtenido del parámetro "CIERRE DE PERIODO DISCRIMINADO"
     * @param calidad
     * Valor obtenido del parámetro "FORMATO CALIDAD"
     * @param finCos
     * Nombre de un reporte
     * @return nombre del reporte que se va a generar
     */
    private String evaluarReporte1(String cierrePeriodo, String calidad,
        String finCos) {
        String reporte = "";
        if ("SI".equals(cierrePeriodo)) {
            if ("SI".equals(calidad)) {
                reporte = finCos;
            }
            else if ("NO".equals(calidad)) {
                reporte = "001312LResumenRecaudosConcepto";
            }
        }
        else if ("NO".equals(cierrePeriodo)) {
            reporte = "001284LResumenRecaudosConceptoAnt";
        }
        return reporte;
    }

    /**
     * Método que evalúa cual reporte se debe generar en caso de que
     * se haya seleccionado el indicador Unificado con abonos, también
     * se tiene en cuenta el parámetro del sistema
     * "CIERRE DE PERIODO DISCRIMINADO"
     * 
     * @param cierrePeriodo
     * Valor obtenido del parámetro "CIERRE DE PERIODO DISCRIMINADO"
     * @return nombre del reporte que se va a generar
     */
    private String evaluarReporte4(String cierrePeriodo) {
        String reporte = "";
        if ("SI".equals(cierrePeriodo)) {
            reporte = "001317LResumenRecaudosConceptoAbonos";
        }
        else if ("NO".equals(cierrePeriodo)) {
            reporte = "001300LResumenRecaudosConceptoAbonosAnt";
        }
        return reporte;
    }

    /**
     * Método que evalúa cual reporte se debe generar en caso de que
     * se haya seleccionado el indicador Unificado con abonos y
     * Financiaciones, también se tienen en cuenta los parámetros del
     * sistema "CIERRE DE PERIODO DISCRIMINADO" y "FORMATO CALIDAD"
     * 
     * @param cierrePeriodo
     * Valor obtenido del parámetro "CIERRE DE PERIODO DISCRIMINADO"
     * @param calidad
     * Valor obtenido del parámetro "FORMATO CALIDAD"
     * @param finCos
     * Nombre de un reporte
     * @return nombre del reporte que se va a generar
     */
    private String evaluarReporte5(String cierrePeriodo, String calidad,
        String finCos) {
        String reporte = "";
        if ("SI".equals(cierrePeriodo)) {
            if ("SI".equals(calidad)) {
                reporte = finCos;
            }
            else if ("NO".equals(calidad)) {
                reporte = "001319LResumenRecaudosConceptoAbonosFin";
            }
        }
        else if ("NO".equals(cierrePeriodo)) {
            reporte = "001302LResumenRecaudosConceptoAbonosFinanAnt";
        }
        return reporte;
    }

    /**
     * Asigna el formato TO_DATE(DD/MM/YYYY) a la fecha ingresada por
     * parámetro.
     * 
     * @param fechaDate
     * Fecha a la cual se le hace casting.
     * @return Fecha formateada.
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
     * @return Fecha cadena en formato DD/MM/YYYY.
     */
    public String convertirFecha(Date fechaDate) {
        String fechaStr = " ";
        try {
            fechaStr = SysmanFunciones.convertirAFechaCadena(fechaDate);
        }
        catch (ParseException e) {
            Logger.getLogger(ResumenRecaudosConceptoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return fechaStr;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
