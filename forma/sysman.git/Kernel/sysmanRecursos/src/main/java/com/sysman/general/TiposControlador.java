/*-
 * TiposControlador.java
 *
 * 1.0
 * 
 * 16/04/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.TiposControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Controlador de la forma: <code>tipos</code>.
 *
 * @version 1.0, 16/04/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class TiposControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion.
     */
    private final String modulo = SessionUtil.getModulo();

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el id del tipo seleccionado en la grilla.
     */
    private String idTipo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que contiene los detalles del combo Tipo Padre (CB5885).
     */
    private List<Registro> listaIdTipoPadre;

    /**
     * Lista que contiene los detalles del combo Categoria (CB5886).
     */
    private List<Registro> listaCategoria;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_EJBs>
    /**
     * Instancia que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_EJBs>
    /**
     * Crea una nueva instancia de TiposControlador
     */
    public TiposControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1765
            numFormulario = GeneralCodigoFormaEnum.TIPOS_CONTROLADOR
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
        enumBase = GenericUrlEnum.TIPOS;
        registro = new Registro();

        reasignarOrigen();
        buscarLlave();

        // <CARGAR_LISTA>
        cargarListaCategoria();
        cargarListaIdTipoPadre();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Se asignan los DSS a las URLs de las operaciones basicas (CRUD)
     * del formulario.
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista: <code>listaIdTipoPadre</code> asociada al combo
     * Tipo Padre (CB5885).
     */
    public void cargarListaIdTipoPadre() {
        try {
            listaIdTipoPadre = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiposControladorUrlEnum.URL4416
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista: <code>listaCategoria</code> asociada al combo
     * Categoria (CB5886).
     */
    public void cargarListaCategoria() {
        try {
            listaCategoria = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TiposControladorUrlEnum.URL4820
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Categorias (BT3091) en la
     * vista. Redirecciona al formulario Categorias (1766).
     */
    public void oprimirBtCategorias() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cargarModalDatos(Integer
                        .toString(GeneralCodigoFormaEnum.TIPO_CATEGORIAS_CONTROLADOR
                                        .getCodigo()),
                        modulo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo que ejecuta los procesos subsecuentes de finalizar los
     * procesos del boton Categorias (BT3091).
     */
    public void retornarFormularioBtCategorias(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        cargarListaCategoria();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * Asigna el consecutivo ID de la tabla TIPOS.
     * 
     * @return true -> Permite realizar la insercion del registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        try {
            /*-Consecutivo ID*/
            long consecutivoId = ejbSysmanUtil.generarSiguienteConsecutivo(
                            GenericUrlEnum.TIPOS.getTable(), "", "ID");

            idTipo = Long.toString(consecutivoId);

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
     * Metodo ejecutado despues de realizar la insercion del registro.
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion o actualizacion
     * del registro.
     * 
     * @return true -> Permie la insercion o actualizacion del
     * registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        String idPadre = SysmanFunciones
                        .nvl(registro.getCampos().get("ID_TIPO_PADRE"), "")
                        .toString();

        if (idTipo.equals(idPadre)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4065"));
            return false;
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion o
     * actualizacion del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        /*-Actualizar combo Tipo Padre.*/
        cargarListaIdTipoPadre();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro.
     * 
     * @return true -> Permite eliminar el registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro.
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        /*-Actualizar combo Tipo Padre.*/
        cargarListaIdTipoPadre();

        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        idTipo = registro.getCampos().remove("ID").toString();
        registro.getCampos().remove("CATEGORIA_NOM");
        registro.getCampos().remove("TIPO_PADRE_NOM");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaIdTipoPadre
     * 
     * @return listaIdTipoPadre
     */
    public List<Registro> getListaIdTipoPadre() {
        return listaIdTipoPadre;
    }

    /**
     * Asigna la lista listaIdTipoPadre
     * 
     * @param listaIdTipoPadre
     * Variable a asignar en listaIdTipoPadre
     */
    public void setListaIdTipoPadre(List<Registro> listaIdTipoPadre) {
        this.listaIdTipoPadre = listaIdTipoPadre;
    }

    /**
     * Retorna la lista listaCategoria
     * 
     * @return listaCategoria
     */
    public List<Registro> getListaCategoria() {
        return listaCategoria;
    }

    /**
     * Asigna la lista listaCategoria
     * 
     * @param listaCategoria
     * Variable a asignar en listaCategoria
     */
    public void setListaCategoria(List<Registro> listaCategoria) {
        this.listaCategoria = listaCategoria;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
