/*-
 * RptIncentivosControlador.java
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
import com.sysman.hojasdevida.enums.RptIncentivosControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
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
 * Formulario que permite generar el informe de los incentivos con
 * respecto a los empleados
 *
 * @version 1.0, 13/12/2017
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class RptIncentivosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para almacenar codigo del empleado inicial
     */
    private String empleadoInicial;
    /**
     * Atributo usado para almacenar codigo del empleado final
     */
    private String empleadoFinal;
    /**
     * Atributo usado para almacenar codigo del centro de costo
     * inicial
     */
    private String centroInicial;
    /**
     * Atributo usado para almacenar codigo del centro de costo final
     */
    private String centroFinal;
    /**
     * Atributo usado para almacenar nombre del empleado inicial
     */
    private String nombreEmpleadoIni;
    /**
     * Atributo usado para almacenar nombre del empleado final
     */
    private String nombreEmpleadoFin;
    /**
     * Atributo usado para almacenar fecha inicial
     */
    private Date fechaInicial;
    /**
     * Atributo usado para almacenar fecha final
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

    private RegistroDataModelImpl listaCarpetaInicial;
    private RegistroDataModelImpl listaCarpetaFinal;
    private RegistroDataModelImpl listaCCINICIAL;
    private RegistroDataModelImpl listaCCFINAL;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de RptIncentivosControlador
     */
    public RptIncentivosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.RPT_INCENTIVOS_CONTROLADOR
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
        cargarListaCarpetaInicial();
        cargarListaCarpetaFinal();
        cargarListaCCINICIAL();
        cargarListaCCFINAL();
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
        fechaInicial = fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCarpetaInicial
     *
     */
    public void cargarListaCarpetaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptIncentivosControladorUrlEnum.URL6170
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCarpetaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    /**
     *
     * Carga la lista listaCarpetaFinal
     *
     */
    public void cargarListaCarpetaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptIncentivosControladorUrlEnum.URL4507
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), empleadoInicial);

        listaCarpetaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    /**
     *
     * Carga la lista listaCCINICIAL
     *
     */
    public void cargarListaCCINICIAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptIncentivosControladorUrlEnum.URL1547
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCCINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaCCFINAL
     *
     */
    public void cargarListaCCFINAL() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RptIncentivosControladorUrlEnum.URL4986
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        listaCCFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton CmdPrevia en la vista
     *
     *
     */
    public void oprimirCmdPrevia() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdImprimir en la vista
     *
     *
     */
    public void oprimirCmdImprimir() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        try {
            archivoDescarga = null;
            String reporte = "001558rhvIncentivos";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);
            reemplazar.put("empleadoInicial", empleadoInicial);
            reemplazar.put("empleadoFinal", empleadoFinal);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
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
     * listaCarpetaInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                        .toString();
        nombreEmpleadoIni = registroAux.getCampos().get("NOMBRECOMPLETO")
                        .toString();
        empleadoFinal = null;
        nombreEmpleadoFin = null;
        cargarListaCarpetaFinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCarpetaFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCarpetaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleadoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                        .toString();
        nombreEmpleadoFin = registroAux.getCampos().get("NOMBRECOMPLETO")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCCINICIAL
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCCINICIAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        centroFinal = null;
        cargarListaCCFINAL();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCCFINAL
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCCFINAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
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
     * Retorna la variable centroInicial
     *
     * @return centroInicial
     */
    public String getCentroInicial() {
        return centroInicial;
    }

    /**
     * Asigna la variable centroInicial
     *
     * @param centroInicial
     * Variable a asignar en centroInicial
     */
    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    /**
     * Retorna la variable centroFinal
     *
     * @return centroFinal
     */
    public String getCentroFinal() {
        return centroFinal;
    }

    /**
     * Asigna la variable centroFinal
     *
     * @param centroFinal
     * Variable a asignar en centroFinal
     */
    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
    }

    /**
     * Retorna la variable nombreEmpleadoIni
     *
     * @return nombreEmpleadoIni
     */
    public String getNombreEmpleadoIni() {
        return nombreEmpleadoIni;
    }

    /**
     * Asigna la variable nombreEmpleadoIni
     *
     * @param nombreEmpleadoIni
     * Variable a asignar en nombreEmpleadoIni
     */
    public void setNombreEmpleadoIni(String nombreEmpleadoIni) {
        this.nombreEmpleadoIni = nombreEmpleadoIni;
    }

    /**
     * Retorna la variable nombreEmpleadoFin
     *
     * @return nombreEmpleadoFin
     */
    public String getNombreEmpleadoFin() {
        return nombreEmpleadoFin;
    }

    /**
     * Asigna la variable nombreEmpleadoFin
     *
     * @param nombreEmpleadoFin
     * Variable a asignar en nombreEmpleadoFin
     */
    public void setNombreEmpleadoFin(String nombreEmpleadoFin) {
        this.nombreEmpleadoFin = nombreEmpleadoFin;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
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
     * Retorna la lista listaCarpetaInicial
     *
     * @return listaCarpetaInicial
     */
    public RegistroDataModelImpl getListaCarpetaInicial() {
        return listaCarpetaInicial;
    }

    /**
     * Asigna la lista listaCarpetaInicial
     *
     * @param listaCarpetaInicial
     * Variable a asignar en listaCarpetaInicial
     */
    public void setListaCarpetaInicial(
        RegistroDataModelImpl listaCarpetaInicial) {
        this.listaCarpetaInicial = listaCarpetaInicial;
    }

    /**
     * Retorna la lista listaCarpetaFinal
     *
     * @return listaCarpetaFinal
     */
    public RegistroDataModelImpl getListaCarpetaFinal() {
        return listaCarpetaFinal;
    }

    /**
     * Asigna la lista listaCarpetaFinal
     *
     * @param listaCarpetaFinal
     * Variable a asignar en listaCarpetaFinal
     */
    public void setListaCarpetaFinal(RegistroDataModelImpl listaCarpetaFinal) {
        this.listaCarpetaFinal = listaCarpetaFinal;
    }

    /**
     * Retorna la lista listaCCINICIAL
     *
     * @return listaCCINICIAL
     */
    public RegistroDataModelImpl getListaCCINICIAL() {
        return listaCCINICIAL;
    }

    /**
     * Asigna la lista listaCCINICIAL
     *
     * @param listaCCINICIAL
     * Variable a asignar en listaCCINICIAL
     */
    public void setListaCCINICIAL(RegistroDataModelImpl listaCCINICIAL) {
        this.listaCCINICIAL = listaCCINICIAL;
    }

    /**
     * Retorna la lista listaCCFINAL
     *
     * @return listaCCFINAL
     */
    public RegistroDataModelImpl getListaCCFINAL() {
        return listaCCFINAL;
    }

    /**
     * Asigna la lista listaCCFINAL
     *
     * @param listaCCFINAL
     * Variable a asignar en listaCCFINAL
     */
    public void setListaCCFINAL(RegistroDataModelImpl listaCCFINAL) {
        this.listaCCFINAL = listaCCFINAL;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
