/*-
 * PlusvaliaNormaUrbanisticaControlador.java
 *
 * 1.0
 * 
 * 03/04/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.enums.PlusvaliaNormaUrbanisticaControladorUrlEnum;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 03/04/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaNormaUrbanisticaControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Esta variable se valida desde la forma para determinar el
     * comportamiento del boton volver
     */
    private boolean varVolver;
    // <DECLARAR_ATRIBUTOS>

    private Map<String, Object> ridProyecto;
    private Map<String, Object> parametrosEntrada;
    private BigInteger idHechos;
    private String codigoHecho;
    private String clase;
    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaUsoSuelo;
    private RegistroDataModelImpl listaTratamiento;
    private RegistroDataModelImpl listaActividad;

    @EJB
    EjbSysmanUtil ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * PlusvaliaNormaUrbanisticaControlador
     */
    public PlusvaliaNormaUrbanisticaControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {

            idHechos = (BigInteger) parametrosEntrada
                            .get("idHechos");

            codigoHecho = (String) parametrosEntrada
                            .get("codigoHecho");
            clase = (String) parametrosEntrada.get("clase");
            ridProyecto = (Map<String, Object>) parametrosEntrada.get("rid");
        }
        try {
            // 2057
            numFormulario = GeneralCodigoFormaEnum.PLUSVALIA_NORMA_URBANISTICA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
        }
    }

    /**
     * Retorna la variable varVolver
     * 
     * @return var
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     * 
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaUsoSuelo();
        cargarListaTratamiento();
        cargarListaActividad();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.VP_NORMA_URBANISTICA;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     * 
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaUsoSuelo
     *
     */
    public void cargarListaUsoSuelo() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "USO");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaNormaUrbanisticaControladorUrlEnum.URL1785
                                                        .getValue());

        listaUsoSuelo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaTratamiento
     *
     */
    public void cargarListaTratamiento() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "TRATAMIENTO");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaNormaUrbanisticaControladorUrlEnum.URL1785
                                                        .getValue());

        listaTratamiento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaActividad
     *
     */
    public void cargarListaActividad() {
        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASE.getName(), "ACTIVIDAD");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaNormaUrbanisticaControladorUrlEnum.URL1785
                                                        .getValue());

        listaActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaUsoSuelo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaUsoSuelo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("USO_SUELO",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("NOMBRE_USO_SUELO",
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTratamiento
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTratamiento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TRATAMIENTO",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("NOMBRE_TRATAMIENTO",
                        registroAux.getCampos().get("NOMBRE"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaActividad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ACTIVIDAD",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("NOMBRE_ACTIVIDAD",
                        registroAux.getCampos().get("NOMBRE"));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ((rid != null) && !rid.isEmpty()) {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_HECHOS_PROYECTOS", idHechos);
        registro.getCampos().put("CODIGO_HECHO", codigoHecho);
        registro.getCampos().remove("NOMBRE_USO_SUELO");
        registro.getCampos().remove("NOMBRE_TRATAMIENTO");
        registro.getCampos().remove("NOMBRE_ACTIVIDAD");

        try {
            /*-Consecutivo ID*/

            long consecutivoId = ejbSysmanUtil.generarSiguienteConsecutivo(
                            GenericUrlEnum.VP_NORMA_URBANISTICA.getTable(), "",
                            "ID");

            registro.getCampos().put("ID", consecutivoId);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE_USO_SUELO");
        registro.getCampos().remove("NOMBRE_TRATAMIENTO");
        registro.getCampos().remove("NOMBRE_ACTIVIDAD");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * @return the listaUsoSuelo
     */
    public RegistroDataModelImpl getListaUsoSuelo() {
        return listaUsoSuelo;
    }

    /**
     * @param listaUsoSuelo
     * the listaUsoSuelo to set
     */
    public void setListaUsoSuelo(RegistroDataModelImpl listaUsoSuelo) {
        this.listaUsoSuelo = listaUsoSuelo;
    }

    /**
     * @return the listaTratamiento
     */
    public RegistroDataModelImpl getListaTratamiento() {
        return listaTratamiento;
    }

    /**
     * @param listaTratamiento
     * the listaTratamiento to set
     */
    public void setListaTratamiento(RegistroDataModelImpl listaTratamiento) {
        this.listaTratamiento = listaTratamiento;
    }

    /**
     * @return the listaActividad
     */
    public RegistroDataModelImpl getListaActividad() {
        return listaActividad;
    }

    /**
     * @param listaActividad
     * the listaActividad to set
     */
    public void setListaActividad(RegistroDataModelImpl listaActividad) {
        this.listaActividad = listaActividad;
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}
