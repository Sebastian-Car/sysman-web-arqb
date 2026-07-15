/*-
 * FrminformeinsceventosControlador.java
 *
 * 1.0
 * 
 * 01/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.FrminformeinsceventosControladorEnum;
import com.sysman.hojasdevida.enums.FrminformeinsceventosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase para imprimir el informe de los asistentes a un evento.
 *
 * @version 1.0, 01/02/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class FrminformeinsceventosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compañía en la cual inició sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente.
     */
    private final String compania;

    private String nombreTipoActividad;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Check que indica si se han de imprimir el listado de los
     * asistentes o no.
     */
    private boolean asistente;
    /**
     * Combo tipo de evento.
     */
    private String tipoActividad;
    /**
     * Datapicker calendario de fecha inicio.
     */
    private Date fechaInicio;
    /**
     * Datepicker calendario fecha final.
     */
    private Date fechaFin;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    private StreamedContent archivoDescarga;

    /**
     * Constante a nivel de clase que aloja el código del módulo desde
     * el cual el usuario inició sesión.
     */
    private final String modulo = SessionUtil.getModulo();

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de tipo actividad.
     */
    private RegistroDataModelImpl listacmbTipoActividad;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrminformeinsceventosControlador.
     */
    public FrminformeinsceventosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            /** Formulario número 1676 */
            numFormulario = GeneralCodigoFormaEnum.FRM_INFORME_INSCRITOS_EVENTOS_CONTROLADOR
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
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado. En este se realizan las
     * asignaciones iniciales necesarias para la visualización del
     * formulario, como son tablas, origenes de datos, inicialización
     * de listas y demas necesarios.
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbTipoActividad();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este método es invocado el método inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicio = fechaFin = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listacmbTipoActividad
     *
     */
    public void cargarListacmbTipoActividad() {

        if (SessionUtil.getMenuActual().equals("21040305")) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminformeinsceventosControladorUrlEnum.URL170
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(FrminformeinsceventosControladorEnum.COMPANIA.getValue(),
                            compania);
            param.put(FrminformeinsceventosControladorEnum.CODIGOINI.getValue(),
                            1);
            param.put(FrminformeinsceventosControladorEnum.CODIGOFIN.getValue(),
                            4);

            listacmbTipoActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, FrminformeinsceventosControladorEnum.CODIGO
                                            .getValue());

        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrminformeinsceventosControladorUrlEnum.URL170
                                                            .getValue());

            Map<String, Object> param = new TreeMap<>();
            param.put(FrminformeinsceventosControladorEnum.COMPANIA.getValue(),
                            compania);
            param.put(FrminformeinsceventosControladorEnum.CODIGOINI.getValue(),
                            101);
            param.put(FrminformeinsceventosControladorEnum.CODIGOFIN.getValue(),
                            103);

            listacmbTipoActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, FrminformeinsceventosControladorEnum.CODIGO
                                            .getValue());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = "001681RPTINFORMEINSCEVENTOS";

        archivoDescarga = null;

        String cadenaTipoEvento;
        String cadenaFechaEvento;
        String cadenaAsistente;

        // <REEMPLAZAR VARIABLES EN CONSULTA>

        cadenaTipoEvento = " AND AP.TIPOEVENTO = " + tipoActividad + " ";
        cadenaFechaEvento = " AND AP.FECHAINICIAL BETWEEN "
            + SysmanFunciones.formatearFecha(fechaInicio) + " AND "
            + SysmanFunciones.formatearFecha(fechaFin) + " ";

        cadenaAsistente = asistente
            ? " AND (AI.ASISTIO NOT IN(0) AND (AI.ASISTIO) IS NOT NULL) "
            : " ";

        reemplazar.put("cadenaTipoEvento", cadenaTipoEvento);
        reemplazar.put("cadenaFechaEvento", cadenaFechaEvento);
        reemplazar.put("cadenaAsistente", cadenaAsistente);

        // </REEMPLAZAR VARIABLES EN CONSULTA>
        try {
            // <ENVIAR PARAMETROS AL REPORTE>
            // </ENVIAR PARAMETROS AL REPORTE>

            /*-aqui reporte hace referencia al nombre del reporte*/

            if (SessionUtil.getMenuActual().equals("21040305")) {
                Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                                reemplazar, parametros);

                reporte = "001690RPTINFORMEINSCEVENTOS";
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else {
                Reporteador.resuelveConsulta("001900RPTINFORMEINSCEVENTOS",
                                Integer.parseInt(modulo),
                                reemplazar, parametros);

                reporte = "001690RPTINFORMEINSCEVENTOS";
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }

        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton generarPdf en la vista
     *
     */
    public void oprimirgenerarPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Método ejecutado al oprimir el botón generarExcel en la vista.
     *
     */
    public void oprimirgenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Método ejecutado al seleccionar una fila de la lista
     * listacmbTipoActividad.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        tipoActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrminformeinsceventosControladorEnum.CODIGO
                                                        .getValue()),
                                        "")
                        .toString();

        nombreTipoActividad = (String) registroAux.getCampos()
                        .get(FrminformeinsceventosControladorEnum.NOMBRE
                                        .getValue());

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable asistente
     * 
     * @return asistente
     */
    public boolean getAsistente() {
        return asistente;
    }

    /**
     * Asigna la variable asistente
     * 
     * @param asistente
     * Variable a asignar en asistente
     */
    public void setAsistente(boolean asistente) {
        this.asistente = asistente;
    }

    /**
     * Retorna la variable fechaInicio
     * 
     * @return fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Asigna la variable fechaInicio
     * 
     * @param fechaInicio
     * Variable a asignar en fechaInicio
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Retorna la variable fechaFin
     * 
     * @return fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * Asigna la variable fechaFin
     * 
     * @param fechaFin
     * Variable a asignar en fechaFin
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getNombreTipoActividad() {
        return nombreTipoActividad;
    }

    public void setNombreTipoActividad(String nombreTipoActividad) {
        this.nombreTipoActividad = nombreTipoActividad;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbTipoActividad
     * 
     * @return listacmbTipoActividad
     */
    public RegistroDataModelImpl getListacmbTipoActividad() {
        return listacmbTipoActividad;
    }

    /**
     * Asigna la lista listacmbTipoActividad
     * 
     * @param listacmbTipoActividad
     * Variable a asignar en listacmbTipoActividad
     */
    public void setListacmbTipoActividad(
        RegistroDataModelImpl listacmbTipoActividad) {
        this.listacmbTipoActividad = listacmbTipoActividad;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
