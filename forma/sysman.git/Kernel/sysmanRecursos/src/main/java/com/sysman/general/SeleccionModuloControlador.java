/*-
 * SeleccionModuloControlador.java
 *
 * 1.0
 * 
 * 21/05/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SeleccionModuloControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 * Formulario que seleccionar el modulo de trabajo para el generador de reportes.
 *
 * @version 1.0, 21/05/2018
 * @author jreina
 */


@ManagedBean
@ViewScoped
public class SeleccionModuloControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String aplicacion;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>

    private List<Registro> listaAplicaciones;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SeleccionModuloControlador
     */
    public SeleccionModuloControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SELECCIONMODULO_CONTROLADOR.getCodigo();
            SessionUtil.getModulo();
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
        cargarListaAplicaciones();
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
     * Carga la lista listaAplicaciones
     *
     */
    public void cargarListaAplicaciones() {
        try {
            listaAplicaciones = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SeleccionModuloControladorUrlEnum.URL3642
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
     * Metodo ejecutado al oprimir el boton BT2896 en la vista
     *
     *
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
 	   Direccionador direccionador = new Direccionador();

	    if(SessionUtil.getMenuActual().equals("99931")) {
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("aplicacion", aplicacion);
				parametros.put("menu", SessionUtil.getMenuActual());

				 direccionador.setNumForm(Integer
	                        .toString(GeneralCodigoFormaEnum.PLANTILLASWORDS_CONTROLADOR
	                                        .getCodigo()));
				 
		        direccionador.setParametros(parametros);
		        RequestContext.getCurrentInstance().closeDialog(direccionador);
	    	
	    }else{
	        if (SysmanFunciones.validarVariableVacio(aplicacion)) {
	            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3934"));
	            return;
	        }
	
	        try {
	            SessionUtil.setSessionVarContainer("aplicacion",
	                            aplicacion);

	            SessionUtil.setSessionVarContainer("menu", "99915");

	            direccionador.setRuta("/menu.sysman");

	        }
	        catch (NamingException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	
	        RequestContext.getCurrentInstance().closeDialog(direccionador);
	    	}
	        // </CODIGO_DESARROLLADO>
	    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BT2897 en la vista
     *
     *
     */
    public void oprimirCancelar() {
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
    /**
     * Retorna la variable aplicacion
     * 
     * @return aplicacion
     */
    public String getAplicacion() {
        return aplicacion;
    }

    /**
     * Asigna la variable aplicacion
     * 
     * @param aplicacion
     * Variable a asignar en aplicacion
     */
    public void setAplicacion(String aplicacion) {
        this.aplicacion = aplicacion;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAplicaciones
     * 
     * @return listaAplicaciones
     */
    public List<Registro> getListaAplicaciones() {
        return listaAplicaciones;
    }

    /**
     * Asigna la lista listaAplicaciones
     * 
     * @param listaAplicaciones
     * Variable a asignar en listaAplicaciones
     */
    public void setListaAplicaciones(List<Registro> listaAplicaciones) {
        this.listaAplicaciones = listaAplicaciones;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
