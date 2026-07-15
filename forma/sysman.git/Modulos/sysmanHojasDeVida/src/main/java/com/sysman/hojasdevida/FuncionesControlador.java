/*-
 * FuncionesControlador.java
 *
 * 1.0
 * 
 * 28/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped; import java.util.Map;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @version 1.0, 28/02/2017
 * @author spina
 */
@ManagedBean
@ViewScoped
public class FuncionesControlador extends BeanBaseContinuoAcme {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * lista para el combo de areas
     */
    private List<Registro> listaarea;

    /**
     * codigo funcion por parametro
     */
    private String codigoFunciones;
    /**
     * grado de registro por parametro
     */
    private String gradoFunciones;
    /**
     * valor que habilita o deshabilita la edicion de los registro
     */
    private String accion;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FuncionesControlador
     */
    public FuncionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1314;
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
        try {
            tabla = "NAT_FUNCIONES";

            codigoFunciones = JsfUtil.getParametros().get("CODIGO");
            gradoFunciones = JsfUtil.getParametros().get("GRADO");
            accion = JsfUtil.getParametros().get("ACCION");

            reasignarOrigen();
            buscarLlave();
            conectorPool.conectar(nombreConexion);
            registro = new Registro();
            // <CARGAR_LISTA>
            cargarListaarea();
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
            cargarValoresIniciales();

        }
        catch (NamingException | SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        finally {
            try {
                conectorPool.getConection().close();
            }
            catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
        }
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        origenDatos = "SELECT  " +
            " NAT_FUNCIONES.CODIGO, " +
            " NAT_FUNCIONES.GRADO, " +

            " NAT_FUNCIONES.COMPANIA, " +
            "    TO_DATE(TO_CHAR(NAT_FUNCIONES.FECHACREACION, 'dd/MM/yyyy'), 'dd/MM/yyyy') FECHACREACION, "
            +
            "     NAT_FUNCIONES.IDFUNCION, " +
            "     NAT_FUNCIONES.AREA, " +
            "     NAT_FUNCIONES.FUNCION " +
            " FROM " +
            "     NAT_FUNCIONES"
            + " WHERE COMPANIA = '" + compania + "'"
            + " AND CODIGO = '" + codigoFunciones + "' "
            + " AND GRADO = '" + gradoFunciones + "'"
            + " ORDER BY IDFUNCION ";
        if (listaInicial != null) {
            listaInicial.setOrigen(origenDatos);
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaarea
     *
     */
    public void cargarListaarea() {
        listaarea = service.getListado(conectorPool, "SELECT " +
            "     DEPENDENCIA.CODIGO, " +
            "     DEPENDENCIA.NOMBRE " +
            " FROM " +
            "     DEPENDENCIA"
            + " WHERE COMPANIA ='" + compania + "'");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("CODIGO", codigoFunciones);
        registro.getCampos().put("GRADO", gradoFunciones);

        cargarValoresIniciales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private void cargarValoresIniciales() {
        try {
            registro.getCampos().put("FECHACREACION",
                            Acciones.getSysDate(
                                            conectorPool));
            registro.getCampos().put("IDFUNCION",
                            Acciones.genConsecutivo(conectorPool,
                                            "NAT_FUNCIONES",
                                            " COMPANIA = '" + compania
                                                + "'"
                                                + " AND CODIGO = '"
                                                + codigoFunciones + "' "
                                                + " AND GRADO = '"
                                                + gradoFunciones
                                                + "'",
                                            "IDFUNCION", "1"));
        }
        catch (SQLException | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
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
        // Metodo heredado
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        cargarValoresIniciales();
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaarea
     * 
     * @return listaarea
     */
    public List<Registro> getListaarea() {
        return listaarea;
    }

    /**
     * Asigna la lista listaarea
     * 
     * @param listaarea
     * Variable a asignar en listaarea
     */
    public void setListaarea(List<Registro> listaarea) {
        this.listaarea = listaarea;
    }

    public String getCodigoFunciones() {
        return codigoFunciones;
    }

    public void setCodigoFunciones(String codigoFunciones) {
        this.codigoFunciones = codigoFunciones;
    }

    public String getGradoFunciones() {
        return gradoFunciones;
    }

    public void setGradoFunciones(String gradoFunciones) {
        this.gradoFunciones = gradoFunciones;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
