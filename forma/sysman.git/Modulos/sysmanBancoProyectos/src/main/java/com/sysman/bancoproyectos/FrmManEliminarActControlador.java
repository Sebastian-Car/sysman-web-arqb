/*-
 * FrmManEliminiarActControlador.java
 *
 * 1.0
 * 
 * 31/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.bancoproyectos;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
import com.sysman.bancoproyectos.enums.FrmManEliminarActControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 * Formulario que elimina las actividades del proyecto
 *
 * @version 1.0, 31/05/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmManEliminarActControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el numero del modulo
     * por el que el usuario inicio sesion
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     * que inicia sesion en la aplicacion
     */
    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo del proyecto seleccionado
     */
    private String proyecto;
    /**
     * Atributo que almacena el codigo de la activadad seleccionada
     */
    private String actividad;
    /**
     * Atributo que almacena la vigencia de la actividad seleccionada
     */
    private String vigenciaActividad;

    /**
     * Atributo que almacena la vigencia gubernamental actual
     */
    private String vigenciaGubernamental;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los proyectos
     */
    private RegistroDataModelImpl listaProyecto;
    /**
     * Lsita que carga las actividades por proyecto
     */
    private RegistroDataModelImpl listaActividad;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbBancoProyectoCuatroRemote ejbBancoProyectoCuatro;

    /**
     * Crea una nueva instancia de FrmManEliminarActControlador
     */

    public FrmManEliminarActControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        try {
            numFormulario = 1808;
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
        cargarListaProyecto();

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
        try {
            vigenciaGubernamental = ejbSysmanUtil.consultarParametro(compania,
                            "VIGENCIA GUBERNAMENTAL ACTUAL", modulo, new Date(),
                            false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaProyecto
     *
     */
    public void cargarListaProyecto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmManEliminarActControladorUrlEnum.URL4779
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");

        //
    }

    /**
     * 
     * Carga la lista listaActividad
     *
     */
    public void cargarListaActividad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmManEliminarActControladorUrlEnum.URL5792
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.PROYECTO.getName(),
                        proyecto);

        listaActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ACTIVIDAD");

        //
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     */
    public void oprimirAceptar() {

        try {

            ejbBancoProyectoCuatro.eliminarActividades(compania, proyecto,
                            actividad, usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
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
     * listaProyecto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaProyecto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();

        actividad = null;
        cargarListaActividad();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        actividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ACTIVIDAD"), "")
                        .toString();

        vigenciaActividad = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.VIGENCIA
                                                        .getName()),
                                        "0")
                        .toString();

        validarVigencias();
    }

    private void validarVigencias() {
        if (Integer.parseInt(vigenciaActividad) < Integer
                        .parseInt(vigenciaGubernamental)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4106"));

            actividad = null;

        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable proyecto
     * 
     * @return proyecto
     */
    public String getProyecto() {
        return proyecto;
    }

    /**
     * Asigna la variable proyecto
     * 
     * @param proyecto
     * Variable a asignar en proyecto
     */
    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    /**
     * Retorna la variable actividad
     * 
     * @return actividad
     */
    public String getActividad() {
        return actividad;
    }

    /**
     * Asigna la variable actividad
     * 
     * @param actividad
     * Variable a asignar en actividad
     */
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaProyecto
     * 
     * @return listaProyecto
     */
    public RegistroDataModelImpl getListaProyecto() {
        return listaProyecto;
    }

    /**
     * Asigna la lista listaProyecto
     * 
     * @param listaProyecto
     * Variable a asignar en listaProyecto
     */
    public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    /**
     * Retorna la lista listaActividad
     * 
     * @return listaActividad
     */
    public RegistroDataModelImpl getListaActividad() {
        return listaActividad;
    }

    /**
     * Asigna la lista listaActividad
     * 
     * @param listaActividad
     * Variable a asignar en listaActividad
     */
    public void setListaActividad(RegistroDataModelImpl listaActividad) {
        this.listaActividad = listaActividad;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
