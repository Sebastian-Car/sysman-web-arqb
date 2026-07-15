/*-
 * FrmProyeccionesTramitesControlador.java
 *
 * 1.0
 * 
 * 25/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.workflow;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;
import com.sysman.workflow.ejb.EjbWorkflowCeroRemote;
import com.sysman.workflow.enums.FrmProyeccionesTramitesControladorEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Controlador de la forma <code>frmproyeccionestramite</code>.
 *
 * @version 1.0, 25/07/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmProyeccionesTramitesControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual se accede al formulario.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /** Variable que almacena el tipo de tramite. */
    private String tipoTramite;

    /** Variable que almacena el codigo del proceso */
    private String proceso;

    /** Variable que almacena el numero del tramite. */
    private String tramite;

    /**
     * Variable que almacena la llave del tramite desde el que se
     * accede a este formulario.
     */
    private Map<String, Object> ridTramite;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete <code>PCK_WORKFLOW</code>.
     */
    @EJB
    private EjbWorkflowCeroRemote ejbWorkflowCero;
    // </DECLARAR_EJBs>

    /**
     * Crea una nueva instancia de FrmProyeccionesTramitesControlador
     */
    @SuppressWarnings("unchecked")
    public FrmProyeccionesTramitesControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1869
            numFormulario = GeneralCodigoFormaEnum.FRM_PROYECCIONES_TRAMITES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramIn = SessionUtil.getFlash();

            if (paramIn != null) {
                tipoTramite = recuperarValorCampo(paramIn,
                                FrmProyeccionesTramitesControladorEnum.PR_TIPO_TRAMITE);

                proceso = recuperarValorCampo(paramIn,
                                FrmProyeccionesTramitesControladorEnum.PR_PROCESO);

                tramite = recuperarValorCampo(paramIn,
                                FrmProyeccionesTramitesControladorEnum.PR_TRAMITE);

                ridTramite = (Map<String, Object>) paramIn
                                .get(FrmTramitesControladorEnum.PR_ROWKEY
                                                .getValue());
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
        enumBase = GenericUrlEnum.PROYECCIONES_TRAMITE;
        registro = new Registro();

        reasignarOrigen();
        buscarLlave();

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

        parametrosListado.put(GeneralParameterEnum.TIPO_TRAMITE.getName(),
                        tipoTramite);

        parametrosListado.put(FrmProyeccionesTramitesControladorEnum.PROCESO
                        .getValue(), proceso);

        parametrosListado.put(FrmProyeccionesTramitesControladorEnum.TRAMITE
                        .getValue(), tramite);
    }

    // <METODOS_CARGAR_LISTA>
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
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
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
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro.
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        String estimadoDias = SysmanFunciones
                        .nvl(registro.getCampos().get(
                                        FrmProyeccionesTramitesControladorEnum.ESTIMADO_DIAS
                                                        .getValue()),
                                        "")
                        .toString();

        if (estimadoDias.isEmpty() || "0".equals(estimadoDias)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4179"));
            return false;
        }

        try {
            registro.getCampos().put(
                            FrmProyeccionesTramitesControladorEnum.ESTIMADO_FECHA
                                            .getValue(),
                            SysmanFunciones.sumarRestarDiasFecha(SysmanFunciones
                                            .convertirAFecha(registro
                                                            .getCampos()
                                                            .get(FrmProyeccionesTramitesControladorEnum.ESTIMADO_FECHA
                                                                            .getValue())
                                                            .toString()),
                                            Integer.parseInt(estimadoDias)));
        }
        catch (NumberFormatException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

        try {
            ejbWorkflowCero.proyectarTramite(compania, tipoTramite, proceso,
                            new BigInteger(tramite),
                            registro.getCampos().get(
                                            FrmProyeccionesTramitesControladorEnum.NODO
                                                            .getValue())
                                            .toString(),
                            Integer.parseInt(estimadoDias), usuario);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

        registro.getCampos().remove(
                        FrmProyeccionesTramitesControladorEnum.NODO.getValue());

        return true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro.
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
     * Metodo ejecutado antes de realizar la eliminacion del registro.
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
     * registro.
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
        registro.getCampos()
                        .remove(FrmProyeccionesTramitesControladorEnum.TRAMITE
                                        .getValue());

        registro.getCampos()
                        .remove(FrmProyeccionesTramitesControladorEnum.REAL_DIAS
                                        .getValue());

        registro.getCampos().remove(
                        FrmProyeccionesTramitesControladorEnum.REAL_FECHA
                                        .getValue());

        registro.getCampos()
                        .remove(FrmProyeccionesTramitesControladorEnum.NODO_NOM
                                        .getValue());

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

        registro.getCampos()
                        .remove(FrmProyeccionesTramitesControladorEnum.PROCESO
                                        .getValue());

        registro.getCampos()
                        .remove(GeneralParameterEnum.TIPO_TRAMITE.getName());
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

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario. Retorna al formulario de Tramites (1763).
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), ridTramite);

        Direccionador dir = new Direccionador();

        dir.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
                                        .getCodigo()));

        dir.setParametros(param);

        SessionUtil.redireccionarForma(dir, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Recupera el valor de un campo en una coleccion.
     * 
     * @param map
     * -> Coleccion.
     * @param clave
     * -> Enumerado que contiene el nombre del campo.
     * @return -> El valor del campo en la coleccion.
     */
    private String recuperarValorCampo(Map<String, Object> map,
        FrmProyeccionesTramitesControladorEnum campo) {
        return map.get(campo.getValue()).toString();
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
