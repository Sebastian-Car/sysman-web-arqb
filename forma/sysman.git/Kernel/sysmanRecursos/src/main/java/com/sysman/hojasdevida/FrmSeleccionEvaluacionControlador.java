/*-
 * FrmSeleccionEvaluacionControlador.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroGeneralRemote;
import com.sysman.hojasdevida.enums.FrmSeleccionEvaluacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite calificar las evaluaciones.
 *
 * @version 1.0, 31/01/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class FrmSeleccionEvaluacionControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Atributo que contiene el valor asignado a la clase de
     * evaluacion
     */
    private String claseEvaluacion;
    /**
     * Atributo que contiene el valor asignado a la evaluacion en la
     * forma del formulario.
     */
    private String evaluacion;
    /**
     * Atributo que contiene el valor asignado al empleado en la forma
     * del formulario.
     */
    private String empleado;
    /**
     * Atributo que contiene el valor asignado al empleado en la forma
     * del formulario.
     */
    private String nombreEmpleado;

    /**
     * Atributo que contiene el valor asignado a la cedula en la forma
     * del formulario.
     */
    private String cedula;

    /**
     * Atributo que contiene el valor asignado a la descripcionen la
     * forma del formulario.
     */
    private String descripcion;

    private boolean visibleEmpleado;

    private String titulo;

    private String tipo;

    private String nombreTipo;

    private String periodo;

    private String anio;

    private String cedulaEvaluado;

    private String sucursalEvaluado;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /** Lista que contiene los detalles del combo evaluacion */
    private RegistroDataModelImpl listacmbEvaluacion;

    /** Lista que contiene los detalles del combo empleado */
    private RegistroDataModelImpl listacmbEmpleado;

    private RegistroDataModelImpl listaTipo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbHojasDeVidaCeroGeneralRemote ejbHojasDeVidaCeroGeneral;

    /**
     * Crea una nueva instancia de FrmSeleccionEvaluacionControlador
     */
    public FrmSeleccionEvaluacionControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            numFormulario = GeneralCodigoFormaEnum.FRM_SELECCIONEVALUACION_CONTROLADOR
                            .getCodigo();
            claseEvaluacion = (String) SessionUtil.getSessionVar(
                            ConstantesHojasDeVidaEnum.CLASE_EVALUACION
                                            .getValue());

            if ("21100203".equals(SessionUtil.getMenuActual())) {
                titulo = idioma.getString("TB_TB4213");
            }
            else {
                titulo = idioma.getString("TT_FR1667");
            }
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
        cargarListacmbEvaluacion();
        cargarListacmbEmpleado();
        cargarListaTipo();
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
        visibleEmpleado = "2".equals(claseEvaluacion);
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbEvaluacion
     *
     */
    public void cargarListacmbEvaluacion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSeleccionEvaluacionControladorUrlEnum.URL4191
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseEvaluacion);
        param.put(GeneralParameterEnum.TIPO.getName(), tipo);
        param.put("CEDULA", SessionUtil.getUser().getCedula());

        listacmbEvaluacion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CONSECUTIVO");
    }

    /**
     * 
     * Carga la lista listaTipo
     *
     */
    public void cargarListaTipo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), claseEvaluacion);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSeleccionEvaluacionControladorUrlEnum.URL8571
                                                        .getValue());
        listaTipo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listacmbEmpleado
     *
     */
    public void cargarListacmbEmpleado() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmSeleccionEvaluacionControladorUrlEnum.URL5262
                                                        .getValue());
        listacmbEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdAceptar en la vista
     *
     *
     */
    public void oprimircmdAceptar() {
        // <CODIGO_DESARROLLADO>

        try {

            if ("2".equals(claseEvaluacion)) {
                ejbHojasDeVidaCeroGeneral.calificarEvaluacion(compania,
                                Integer.parseInt(empleado),
                                new BigInteger(evaluacion),
                                Integer.parseInt(claseEvaluacion),
                                SessionUtil.getUser().getCodigo());

                Direccionador direccionador = new Direccionador();
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("cerrar", "0");
                parametros.put("cedulaEvaluado", cedula);
                parametros.put("cedulaEvaluador", cedula);
                parametros.put("tipo", "001");
                parametros.put("evaluacion", evaluacion);
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESSUBDETPRINCIPAL_CONTROLADOR
                                                .getCodigo()));
                direccionador.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(direccionador);
            }
            else {
                if ("2".equals(periodo)) {
                    ejbHojasDeVidaCeroGeneral.heredarEvaluacion(compania,
                                    new BigInteger(evaluacion),
                                    Integer.parseInt(claseEvaluacion),
                                    Integer.parseInt(anio), cedulaEvaluado,
                                    sucursalEvaluado,
                                    SessionUtil.getUser().getCedula(),
                                    SessionUtil.getUser().getSucursal(), tipo,
                                    SessionUtil.getUser().getCodigo());
                }
                Direccionador direccionador = new Direccionador();
                Map<String, Object> parametros = new HashMap<>();
                parametros.put("tipo", tipo);
                parametros.put("evaluacion", evaluacion);
                direccionador.setNumForm(Integer
                                .toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR
                                                .getCodigo()));
                direccionador.setParametros(parametros);
                RequestContext.getCurrentInstance().closeDialog(direccionador);
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbCancelar en la vista
     *
     *
     */
    public void oprimircmbCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbEvaluacion
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbEvaluacion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        evaluacion = registroAux.getCampos().get("CONSECUTIVO").toString();
        descripcion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.DESCRIPCION
                                                        .getName()),
                                        "")
                        .toString();
        periodo = registroAux.getCampos()
                        .get(GeneralParameterEnum.PERIODO.getName()).toString();
        anio = registroAux.getCampos().get(GeneralParameterEnum.ANO.getName())
                        .toString();
        cedulaEvaluado = registroAux.getCampos()
                        .get(GeneralParameterEnum.CEDULA.getName()).toString();
        sucursalEvaluado = registroAux.getCampos()
                        .get(GeneralParameterEnum.SUCURSAL.getName())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaTipo
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipo = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreTipo = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListacmbEvaluacion();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = registroAux.getCampos().get("ID_DE_EMPLEADO").toString();
        nombreEmpleado = registroAux.getCampos().get("NOMBRECOMPLETO")
                        .toString();
        cedula = registroAux.getCampos()
                        .get(GeneralParameterEnum.NUMERO_DCTO.getName())
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable evaluacion
     * 
     * @return evaluacion
     */
    public String getEvaluacion() {
        return evaluacion;
    }

    /**
     * Asigna la variable evaluacion
     * 
     * @param evaluacion
     * Variable a asignar en evaluacion
     */
    public void setEvaluacion(String evaluacion) {
        this.evaluacion = evaluacion;
    }

    /**
     * Retorna la variable empleado
     * 
     * @return empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * Asigna la variable empleado
     * 
     * @param empleado
     * Variable a asignar en empleado
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    /**
     * Retorna la variable Descripcion
     * 
     * @return Descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asigna la variable Descripcion
     * 
     * @param Descripcion
     * Variable a asignar en Descripcion
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public boolean isVisibleEmpleado() {
        return visibleEmpleado;
    }

    public void setVisibleEmpleado(boolean visibleEmpleado) {
        this.visibleEmpleado = visibleEmpleado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbEvaluacion
     * 
     * @return listacmbEvaluacion
     */
    public RegistroDataModelImpl getListacmbEvaluacion() {
        return listacmbEvaluacion;
    }

    /**
     * Asigna la lista listacmbEvaluacion
     * 
     * @param listacmbEvaluacion
     * Variable a asignar en listacmbEvaluacion
     */
    public void setListacmbEvaluacion(
        RegistroDataModelImpl listacmbEvaluacion) {
        this.listacmbEvaluacion = listacmbEvaluacion;
    }

    /**
     * Retorna la lista listacmbEmpleado
     * 
     * @return listacmbEmpleado
     */
    public RegistroDataModelImpl getListacmbEmpleado() {
        return listacmbEmpleado;
    }

    /**
     * Asigna la lista listacmbEmpleado
     * 
     * @param listacmbEmpleado
     * Variable a asignar en listacmbEmpleado
     */
    public void setListacmbEmpleado(RegistroDataModelImpl listacmbEmpleado) {
        this.listacmbEmpleado = listacmbEmpleado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public RegistroDataModelImpl getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(RegistroDataModelImpl listaTipo) {
        this.listaTipo = listaTipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
