/*-
 * SeleccionarClaseVpControlador.java
 *
 * 1.0
 * 
 * 06/03/2019
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
import com.sysman.general.enums.SeleccionarClaseVpControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 06/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class SeleccionarClaseVpControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    private String claseVp;
    private String categoria;
    private String Clase;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaClase;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    public SeleccionarClaseVpControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            numFormulario = GeneralCodigoFormaEnum.SELECCIONAR_CLASE_VP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            categoria = "16";
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
        cargarListaClase();
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
     * Carga la lista listaClase
     *
     */
    public void cargarListaClase() {
        Map<String, Object> param = new HashMap<>();

        param.put("CATEGORIA", categoria);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeleccionarClaseVpControladorUrlEnum.URL1032
                                                        .getValue());

        listaClase = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "ID");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnAceptar en la vista
     *
     *
     */
    public void oprimirBtnAceptar() {

        try {
            SessionUtil.setSessionVarContainer("claseVp",
                            Clase);

            SessionUtil.setSessionVarContainer("nombreClase",
                            claseVp);

            if ("61".equals(SessionUtil.getMenuActual())) {

                SessionUtil.setSessionVarContainer("menu", "61");
            }
        }
        catch (NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta("/menu.sysman");
        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BtnCancelar en la vista
     *
     *
     */
    public void oprimirBtnCancelar() {
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
     * Metodo ejecutado al seleccionar una fila de la lista listaClase
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClase(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        claseVp = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        Clase = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable claseVp
     * 
     * @return claseVp
     */
    public String getClaseVp() {
        return claseVp;
    }

    /**
     * Asigna la variable claseVp
     * 
     * @param claseVp
     * Variable a asignar en claseVp
     */
    public void setClaseVp(String claseVp) {
        this.claseVp = claseVp;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaClase
     * 
     * @return listaClase
     */
    public RegistroDataModelImpl getListaClase() {
        return listaClase;
    }

    /**
     * Asigna la lista listaClase
     * 
     * @param listaClase
     * Variable a asignar en listaClase
     */
    public void setListaClase(RegistroDataModelImpl listaClase) {
        this.listaClase = listaClase;
    }

    /**
     * @return the categoria
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * @param categoria
     * the categoria to set
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * @return the clase
     */
    public String getClase() {
        return Clase;
    }

    /**
     * @param clase
     * the clase to set
     */
    public void setClase(String clase) {
        Clase = clase;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}
