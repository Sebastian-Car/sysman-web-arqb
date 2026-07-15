/*-
 * LAbonosWebControlador.java
 *
 * 1.0
 * 
 * 15/12/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma LAbonosWeb asociado al formulario
 * "Abonos de Pagos Web".
 *
 * @version 1.0, 15/12/2016
 * @author yrojas
 * 
 * @version 2, 05/06/2017
 * @author jreina se eliminaron los llamados a Acciones.
 */

@ManagedBean
@ViewScoped
public class LAbonosWebControlador extends BeanBaseModal {

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo asociado a la fecha inicial del formulario y que es
     * usado para la generación del reporte correspondiente.
     */
    private Date fechaInicial;

    /**
     * Atributo asociado a la fecha final del formulario y que es
     * usado para la generación del reporte correspondiente.
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
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LAbonosWebControlador
     */
    public LAbonosWebControlador() {
        super();

        try {
            numFormulario = GeneralCodigoFormaEnum.L_ABONOS_WEB_CONTROLADOR
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
     * Método que genera reporte de acuerdo a un formato recibido.
     * 
     * @param formato
     * Parámetro que determina la extensión y formato del reporte que
     * se va a generar
     */
    public void generarReporte(FORMATOS formato) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("fechaInicial", formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal", formatearFecha(fechaFinal));

            parametros.put("PR_FECHAINICIAL", convertirFecha(fechaInicial));
            parametros.put("PR_FECHAFINAL", convertirFecha(fechaFinal));
            parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());

            Reporteador.resuelveConsulta("001298INFAbonosWEB",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001298INFAbonosWEB", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Método que al ser ejecutado determina que el reporte a generar
     * será creado con formato PDF.
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método que al ser ejecutado determina que el reporte a generar
     * será creado con formato de Excel.
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método que formatea una fecha convirtiéndola en una fecha de
     * tipo (DD/MM/YYYY).
     * 
     * @param fecha
     * Parámetro de tipo DATE a formatear.
     * @return Fecha formateada
     */
    public String formatearFecha(Date fecha) {
        return SysmanFunciones.formatearFecha(fecha);
    }

    /**
     * Método que formatea una fecha convirtiéndola en una cadena de
     * texto.
     * 
     * @param fecha
     * Parámetro de tipo DATE a convertir.
     * @return Fecha en formato "DD/MM/YYYY"
     */
    public String convertirFecha(Date fecha) {
        String fechaStr = " ";
        try {
            fechaStr = SysmanFunciones.convertirAFechaCadena(fecha);
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
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
     * Método que retorna la variable fechaInicial
     * 
     * @return Variable de la fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Método que asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Método que retorna la variable fechaFinal
     * 
     * @return Variable de la fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * Método que asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Método que retorna la variable archivoDescarga
     * 
     * @return Variable de archivoDescarga
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
