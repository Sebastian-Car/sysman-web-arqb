/*-
 * RptIncapacidadesControlador.java
 *
 * 1.0
 * 
 * 13/12/2017
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
import com.sysman.hojasdevida.enums.RptIncapacidadesControladorEnum;
import com.sysman.hojasdevida.enums.RptIncapacidadesControladorUrlEnum;
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
 * Formulario que permite generar el informe de las incapacidades de
 * los empleados
 *
 * @version 1.0, 13/12/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class RptIncapacidadesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena la cedula del emplado inicial
     */
    private String empleadoInicial;
    /**
     * Atributo que almacena la cedula del emplado final
     */
    private String empleadoFinal;
    /**
     * Atributo que almacena el nombre del emplado inicial
     */
    private String nombreEmpleadoInicial;
    /**
     * Atributo que almacena el nombre del emplado final
     */
    private String nombreEmpleadoFinal;
    /**
     * Atributo que almacena la fecha inicial
     */
    private Date fechaInicial;
    /**
     * Atributo que almacena la fecha final
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
    /**
     * Lista que almacena los empleados iniciales
     */
    private RegistroDataModelImpl listaempleadoInicial;
    /**
     * Lista que almacena los empleados finales
     */
    private RegistroDataModelImpl listaempleadoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptIncapacidadesControlador
     */
    public RptIncapacidadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPT_INCAPACIDADES_CONTROLADOR
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
        cargarListaempleadoInicial();
        cargarListaempleadoFinal();
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
     * Carga la lista listaempleadoInicial
     *
     */
    public void cargarListaempleadoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptIncapacidadesControladorUrlEnum.URL5555
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaempleadoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());

    }

    /**
     * 
     * Carga la lista listaempleadoFinal
     *
     */
    public void cargarListaempleadoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptIncapacidadesControladorUrlEnum.URL6666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(RptIncapacidadesControladorEnum.EMPLEADOINICIAL.getValue(),
                        empleadoInicial);

        listaempleadoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton PDF en la vista
     *
     *
     */
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {
        archivoDescarga = null;
        String reporte = "001557rhvIncapacidades";

        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();
        try {
            reemplazar.put("compania", compania);
            reemplazar.put("empleadoInicial", empleadoInicial);
            reemplazar.put("empleadoFinal", empleadoFinal);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaempleadoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaempleadoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreEmpleadoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();

        empleadoFinal = null;
        nombreEmpleadoFinal = null;
        cargarListaempleadoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaempleadoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaempleadoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreEmpleadoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable empleadoInicial
     * 
     * @return empleadoInicial
     */
    public String getEmpleadoInicial() {
        return empleadoInicial;
    }

    /**
     * Asigna la variable empleadoInicial
     * 
     * @param empleadoInicial
     * Variable a asignar en empleadoInicial
     */
    public void setEmpleadoInicial(String empleadoInicial) {
        this.empleadoInicial = empleadoInicial;
    }

    /**
     * Retorna la variable empleadoFinal
     * 
     * @return empleadoFinal
     */
    public String getEmpleadoFinal() {
        return empleadoFinal;
    }

    /**
     * Asigna la variable empleadoFinal
     * 
     * @param empleadoFinal
     * Variable a asignar en empleadoFinal
     */
    public void setEmpleadoFinal(String empleadoFinal) {
        this.empleadoFinal = empleadoFinal;
    }

    /**
     * Retorna la variable nombreEmpleadoInicial
     * 
     * @return nombreEmpleadoInicial
     */
    public String getNombreEmpleadoInicial() {
        return nombreEmpleadoInicial;
    }

    /**
     * Asigna la variable nombreEmpleadoInicial
     * 
     * @param nombreEmpleadoInicial
     * Variable a asignar en nombreEmpleadoInicial
     */
    public void setNombreEmpleadoInicial(String nombreEmpleadoInicial) {
        this.nombreEmpleadoInicial = nombreEmpleadoInicial;
    }

    /**
     * Retorna la variable nombreEmpleadoFinal
     * 
     * @return nombreEmpleadoFinal
     */
    public String getNombreEmpleadoFinal() {
        return nombreEmpleadoFinal;
    }

    /**
     * Asigna la variable nombreEmpleadoFinal
     * 
     * @param nombreEmpleadoFinal
     * Variable a asignar en nombreEmpleadoFinal
     */
    public void setNombreEmpleadoFinal(String nombreEmpleadoFinal) {
        this.nombreEmpleadoFinal = nombreEmpleadoFinal;
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
    /**
     * Retorna la lista listaempleadoInicial
     * 
     * @return listaempleadoInicial
     */
    public RegistroDataModelImpl getListaempleadoInicial() {
        return listaempleadoInicial;
    }

    /**
     * Asigna la lista listaempleadoInicial
     * 
     * @param listaempleadoInicial
     * Variable a asignar en listaempleadoInicial
     */
    public void setListaempleadoInicial(
        RegistroDataModelImpl listaempleadoInicial) {
        this.listaempleadoInicial = listaempleadoInicial;
    }

    /**
     * Retorna la lista listaempleadoFinal
     * 
     * @return listaempleadoFinal
     */
    public RegistroDataModelImpl getListaempleadoFinal() {
        return listaempleadoFinal;
    }

    /**
     * Asigna la lista listaempleadoFinal
     * 
     * @param listaempleadoFinal
     * Variable a asignar en listaempleadoFinal
     */
    public void setListaempleadoFinal(
        RegistroDataModelImpl listaempleadoFinal) {
        this.listaempleadoFinal = listaempleadoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
