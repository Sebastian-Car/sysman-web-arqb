/*-
 * ClaseEvaluacionControlador.java
 *
 * 1.0
 * 
 * 24/01/2018
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
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.ClaseEvaluacionControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.session.utl.ConstantesHojasDeVidaEnum;
import com.sysman.util.SysmanFunciones;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 * Migracion del formulario access FRM_CLASEEVALUACION a web con el
 * controlador ClaseEvaluacionControlador forma
 * frmclaseevaluacion.xhtml creacion de menu para abrir el formulario
 * modal, creacion de properties para el formulario modal.
 *
 * @version 1.0, 24/01/2018
 * @author crodriguez
 */
@ManagedBean
@ViewScoped
public class ClaseEvaluacionControlador extends BeanBaseModal {
    /**
     * Atributo que almacena el codigo del tipo de clase de evaluacion
     * en el combo: <code>CB5487</code>.
     */
    private String claseEvaluacion;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listacmbClaseEvaluacion;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ClaseEvaluacionControlador
     */
    public ClaseEvaluacionControlador() {
        super();
        SessionUtil.getCompania();
        try {
            // 1641
            numFormulario = GeneralCodigoFormaEnum.CLASE_EVALUACION_CONTROLADOR
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
        cargarListacmbClaseEvaluacion();
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
    /**
     * 
     * Carga la lista listacmbClaseEvaluacion
     *
     */
    public void cargarListacmbClaseEvaluacion() {

        try {
            listacmbClaseEvaluacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ClaseEvaluacionControladorUrlEnum.URL128
                                                                            .getValue())
                                            .getUrl(),
                                            null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

        if (SysmanFunciones.validarVariableVacio(claseEvaluacion)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3934"));
            return;
        }

        Direccionador direccionador = new Direccionador();
        try {
            SessionUtil.setSessionVarContainer(
                            ConstantesHojasDeVidaEnum.CLASE_EVALUACION
                                            .getValue(),
                            claseEvaluacion);

            SessionUtil.setSessionVarContainer("menu", "2110");

            direccionador.setRuta("/menu.sysman");

        }
        catch (NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        RequestContext.getCurrentInstance().closeDialog(direccionador);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton cmbCancelar en la vista
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
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbClaseEvaluacion
     * 
     * @return listacmbClaseEvaluacion
     */
    public List<Registro> getListacmbClaseEvaluacion() {
        return listacmbClaseEvaluacion;
    }

    /**
     * Asigna la lista listacmbClaseEvaluacion
     * 
     * @param listacmbClaseEvaluacion
     * Variable a asignar en listacmbClaseEvaluacion
     */
    public void setListacmbClaseEvaluacion(
        List<Registro> listacmbClaseEvaluacion) {
        this.listacmbClaseEvaluacion = listacmbClaseEvaluacion;
    }

    public String getClaseEvaluacion() {
        return claseEvaluacion;
    }

    public void setClaseEvaluacion(String claseEvaluacion) {
        this.claseEvaluacion = claseEvaluacion;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
