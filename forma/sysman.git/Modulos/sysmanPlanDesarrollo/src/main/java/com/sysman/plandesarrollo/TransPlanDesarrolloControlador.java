/*-
 * TransPlanDesarrolloControlador.java
 *
 * 1.0
 * 
 * 06/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroRemote;
import com.sysman.plandesarrollo.enums.TransPlanDesarrolloControladorEnum;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para gestionar las transacciones de plan de
 * desarrollo
 *
 * @version 1.0, 06/03/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class TransPlanDesarrolloControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena si el usuario loggeado
     * es jefe de unidad
     */
    private final boolean jefeUnidad;
    /**
     * Constante a nivel de clase que almacena si el grupo segun el
     * modulo seleccionado tiene el indicador de si es administrador
     * seleccionado
     */
    private final boolean administrador;

    /**
     * Constante a nivel de clase que almacena la dependencia que se
     * encuentra asociada al usuario loggeado
     */
    private final String dependenciaAsociada;
    /**
     * Constante a nivel de clase que almacena el responsable que se
     * encuenta asociado al usuario loggeado
     */
    private final String responsableAsociado;

    /**
     * Constante a nivel de clase que almacena la sucural del
     * responsable que se encuentra asociado al usuario loggueado
     */
    private final String sucursalResponsable;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atribito que almacena el valor del tipo recibido por parametro
     * desde el formulario FRMPITIPO
     */
    private String tipo;
    /**
     * Atributo que almacena el valor de la vigencia recibida por
     * parametro desde el formulario FRMPITIPO
     */
    private String vigencia;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbPlanDesarrolloCeroRemote ejbPlanDesarrolloCero;

    /**
     * Crea una nueva instancia de TransPlanDesarrolloControlador
     */
    public TransPlanDesarrolloControlador() {
        super();
        compania = SessionUtil.getCompania();
        jefeUnidad = SessionUtil.getUser().getResponsableAso().isJefeUnidad();
        administrador = SessionUtil.getGrupo(SessionUtil.getModulo())
                        .isEsAdministrador();
        dependenciaAsociada = SessionUtil.getUser().getDependencia()
                        .getCodigo();
        responsableAsociado = SessionUtil.getUser().getResponsableAso()
                        .getResponsable();
        sucursalResponsable = SessionUtil.getUser().getResponsableAso()
                        .getSucursal();
        try {
            // 1735
            numFormulario = GeneralCodigoFormaEnum.TRANSPLANDESARROLLO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                tipo = parametrosEntrada.get("tipo").toString();
                vigencia = parametrosEntrada.get(GeneralParameterEnum.VIGENCIA
                                .getName().toLowerCase()).toString();
            }
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
        enumBase = GenericUrlEnum.PI_TRANSACCION;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(
                        GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);
        parametrosListado.put(
                        TransPlanDesarrolloControladorEnum.TIPO.getValue(),
                        tipo);

        if (!administrador) {
            parametrosListado.put(
                            TransPlanDesarrolloControladorEnum.ESADMINISTRADOR
                                            .getValue(),
                            0);
            parametrosListado.put(
                            GeneralParameterEnum.DEPENDENCIA.getName(),
                            dependenciaAsociada);
            if (!jefeUnidad) {
                parametrosListado
                                .put(TransPlanDesarrolloControladorEnum.ESJEFEUNIDAD
                                                .getValue(), 0);
                parametrosListado.put(
                                GeneralParameterEnum.RESPONSABLE.getName(),
                                responsableAsociado);
            }
            else {
                parametrosListado
                                .put(TransPlanDesarrolloControladorEnum.ESJEFEUNIDAD
                                                .getValue(), 1);
                parametrosListado.put(
                                GeneralParameterEnum.RESPONSABLE.getName(),
                                "");
            }

        }
        else {
            parametrosListado.put(
                            TransPlanDesarrolloControladorEnum.ESADMINISTRADOR
                                            .getValue(),
                            1);
            parametrosListado.put(
                            GeneralParameterEnum.DEPENDENCIA.getName(),
                            "");
            parametrosListado
                            .put(TransPlanDesarrolloControladorEnum.ESJEFEUNIDAD
                                            .getValue(), 1);
            parametrosListado.put(
                            GeneralParameterEnum.RESPONSABLE.getName(),
                            "");

        }

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Nuevo en la vista
     *
     *
     */
    public void oprimirNuevo() {
        // <CODIGO_DESARROLLADO>
        try {
            long numero = ejbPlanDesarrolloCero.generarTransaccion(compania,
                            tipo,
                            Integer.valueOf(vigencia), dependenciaAsociada,
                            responsableAsociado, sucursalResponsable,
                            SessionUtil.getUser().getCodigo());

            Map<String, Object> param = new TreeMap<>();
            param.put("vigencia", vigencia);
            param.put("tipo", tipo);
            param.put("administrador", administrador);
            param.put("dependencia", dependenciaAsociada);
            param.put("jefeUnidad", jefeUnidad);
            param.put("numero", numero);
            Direccionador direccionador = new Direccionador();
            // 1737
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR
                                            .getCodigo()));
            direccionador.setParametros(param);
            SessionUtil.redireccionarForma(direccionador,
                            SessionUtil.getModulo());
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Detalle
     * 
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirDetalle(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put("vigencia", vigencia);
        param.put("tipo", tipo);
        param.put("administrador", administrador);
        param.put("dependencia", reg.getCampos()
                        .get(GeneralParameterEnum.DEPENDENCIA.getName())
                        .toString());
        param.put("jefeUnidad", jefeUnidad);
        param.put("numero", reg.getCampos()
                        .get(GeneralParameterEnum.NUMERO.getName()).toString());
        Direccionador direccionador = new Direccionador();
        // 1737
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PLAN_INDICATIVO_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(param);
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

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
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     * @return true
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
     * 
     * @return true
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
     * 
     * @return true
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
     * @return true
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
        // <CODIGO_DESARROLLADO>
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
