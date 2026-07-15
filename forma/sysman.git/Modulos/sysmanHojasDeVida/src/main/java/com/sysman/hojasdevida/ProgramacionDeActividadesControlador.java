/*-
 * ProgramacionDeActividadesControlador.java
 *
 * 1.0
 * 
 * 02/02/2018
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
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.hojasdevida.enums.ProgramacionDeActividadesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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
 * 
 * @version 1.0, 02/02/2018
 * @author jcaceres
 * 
 */
@ManagedBean
@ViewScoped
public class ProgramacionDeActividadesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que contiene el valor asignado a la fecha inicial en
     * la forma del formulario.
     */
    private Date fechaPrimera;
    /**
     * Atributo que contiene el valor asignado a la fecha final en la
     * forma del formulario.
     */
    private Date fechaFinal;

    /**
     * Atributo que contiene el valor asignado a el codigo de una
     * actividad seleccionada en el formulario
     * 
     * 
     */
    private String tipoActividad;

    /**
     * Atributo que contiene el valor asignado al nombre de una
     * actividad seleccionada en el formulario
     * 
     */
    private String nombreActividad;
    /** Lista que contiene los detalles del combo actividad . */
    private RegistroDataModelImpl listaCmbTipoActividad;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de
     * ProgramacionDeActividadesControlador
     */
    public ProgramacionDeActividadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        fechaPrimera = fechaFinal = new Date();
        try {
            numFormulario = GeneralCodigoFormaEnum.PROGRAMACION_DE_ACTIVIDADES_CONTROLADOR
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
        cargarListaCmbTipoActividad();
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

    /**
     * Este metodo es invocado al momento de seleccionar el alguna
     * actividad, en donde visualizara el código y el nombre de la
     * actividad
     */
    public void cargarListaCmbTipoActividad() {

        if (SessionUtil.getMenuActual().equals("21040301")) {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramacionDeActividadesControladorUrlEnum.URL5181
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("MENU", "2104");

            listaCmbTipoActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());

        }
        else {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ProgramacionDeActividadesControladorUrlEnum.URL5181
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("MENU", "2108");

            listaCmbTipoActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, true,
                            GeneralParameterEnum.CODIGO.getName());

        }
    }

    /**
     * metodo de seleccion de la actividad con su código y en donde se
     * visualizara la opción elegida
     */

    public void seleccionarFilaCmbTipoActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroAux.getCampos();
        tipoActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        nombreActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

    }

    /**
     * 
     * se invoca el titulo del formulario segun la opcion seleccionada
     * en donde se Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Extension o tipo de documento a generar.
     * @throws com.sysman.exc.kernel.api.commons.util.exceptions.
     * SysmanException
     */

    public void generarReporte(FORMATOS formato)

    {
        String reporte = "001692RptActividades";
        String consulta = "001692RptActividades";
        // se asigna a una constante el titulo1 el cual es un
        // complemento para el titulo del formulario
        String titulo1 = idioma.getString("TG_PROGRAMACION_DE_ACTIVIDADES");
        String titulo2 = idioma.getString("TB_TB4050");
        String titulo3 = idioma.getString("TB_TB4049");
        String titulo4 = ("PROGRAMACIÓN DE TODAS LAS ACTIVIDADES SGSST");
        String titulo = null;
        // se realiza un switch para asignar el reporte segun
        // corresponda la elección
        switch (tipoActividad) {
        case "2":
        case "3":
        case "4":
        case "5":
        case "6":
        case "7":
            titulo = SysmanFunciones.concatenar(titulo1, " ",
                            nombreActividad);
            break;
        case "1":
            reporte = "001688RptActSalud";
            consulta = "001688RptActSalud";
            titulo = SysmanFunciones.concatenar(titulo1, " ",
                            nombreActividad);
            break;
        case "8":
            reporte = "001694RptActTodas";
            consulta = "001694RptActTodas";
            titulo = SysmanFunciones.concatenar(titulo3, " ",
                            nombreActividad, " ", titulo2);
            break;

        case "104":
            reporte = "001694RptActTodas";
            consulta = "001898RptActTodasSgsst";
            titulo = SysmanFunciones.concatenar(titulo4);
            break;
        default:

        }
        try {
            // reemplazos son la variables asignadas que se traen de
            // access
            Map<String, Object> reemplazos = new HashMap<>();
            // parametro son asignados a los correspondientes capos
            // del reporte
            Map<String, Object> parametro = new HashMap<>();
            reemplazos.put("compania", compania);

            reemplazos.put("tipoActividad", tipoActividad);
            reemplazos.put("fechaPrimera",
                            SysmanFunciones.formatearFechaCadena(fechaPrimera,
                                            "DD/MM/YYYY"));
            reemplazos.put("fechaFinal",
                            SysmanFunciones.formatearFechaCadena(fechaFinal,
                                            "DD/MM/YYYY"));
            parametro.put("PR_FORMS_PROGRAMACIONDEACTIVIDADES_TXTFECHAINICIAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaPrimera,
                                            "dd/MM/yyyy"));
            parametro.put("COMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametro.put("PR_TITULO", titulo);

            parametro.put("PR_FORMS_PROGRAMACIONDEACTIVIDADES_TXTFECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaFinal,
                                            "dd/MM/yyyy"));

            Reporteador.resuelveConsulta(consulta,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametro);

            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametro,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | ParseException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validarCampo(String campo) {
        if ((campo == null) || ("".equals(campo))) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TI_MS_ERROR_VALIDACION"));
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPreview en la vista
     * 
     * @throws com.sysman.exc.kernel.api.commons.util.exceptions.
     * SysmanException
     *
     *
     */
    public void oprimirCmdPreview()
                    throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CmdPrint en la vista
     * 
     * @throws com.sysman.exc.kernel.api.commons.util.exceptions.
     * SysmanException
     *
     *
     */
    public void oprimirCmdPrint()
                    throws com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException {
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL);
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    /**
     * @return the fechaPrimera
     */
    public Date getFechaPrimera() {
        return fechaPrimera;
    }

    /**
     * @param fechaPrimera
     * the fechaPrimera to set
     */
    public void setFechaPrimera(Date fechaPrimera) {
        this.fechaPrimera = fechaPrimera;
    }

    /**
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * @return the tipoActividad
     */
    public String getTipoActividad() {
        return tipoActividad;
    }

    /**
     * @param tipoActividad
     * the tipoActividad to set
     */
    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }

    /**
     * @return the ListaCmbTipoActividad
     */
    public RegistroDataModelImpl getListaCmbTipoActividad() {
        return listaCmbTipoActividad;
    }

    /**
     * @param ListaCmbTipoActividad
     * the ListaCmbTipoActividad to set
     */
    public void setListaCmbTipoActividad(
        RegistroDataModelImpl listaCmbTipoActividad) {
        this.listaCmbTipoActividad = listaCmbTipoActividad;
    }

    /**
     * @return the NombreActividad
     */
    public String getNombreActividad() {
        return nombreActividad;
    }

    /**
     * @param NombreActividad
     * the NombreActividad to set
     */
    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

}