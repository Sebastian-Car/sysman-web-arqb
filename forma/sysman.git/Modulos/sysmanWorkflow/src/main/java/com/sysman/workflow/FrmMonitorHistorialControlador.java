/*-
 * FrmMonitorHistorialControlador.java
 *
 * 1.0
 * 
 * 08/05/2018
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.workflow.enums.DTramiteVariablesControladorEnum;
import com.sysman.workflow.enums.FrmMonitorHistorialControladorEnum;
import com.sysman.workflow.enums.FrmMonitorHistorialControladorUrlEnum;
import com.sysman.workflow.enums.FrmTramitesControladorEnum;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Controlador de la forma: <code>frmmonitorhistorial</code>.
 *
 * @version 1.0, 08/05/2018
 * @author pespitia
 */
@ManagedBean
@ViewScoped
public class FrmMonitorHistorialControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania = SessionUtil.getCompania();

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual se accede al formulario.
     */
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que almacena el codigo del menu
     * desde el cual se accede al formulario.
     */
    private final String menuActual = SessionUtil.getMenuActual();

    // <DECLARAR_ATRIBUTOS>
    /** Variable que almacena el codigo del proceso. */
    private String proceso;

    /** Variable que almacena el codigo del tipo de tramite. */
    private String tipoTramite;

    /** Variable que almacena el codigo del tramite. */
    private String tramite;

    /**
     * Variable que contiene la llave del tramite desde el cual se
     * accede a este formulario, con el fin de utilizarlo para
     * redireccionar.
     */
    private Map<String, Object> ridTramite;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmMonitorHistorialControlador
     */
    @SuppressWarnings("unchecked")
    public FrmMonitorHistorialControlador() {
        super();

        try {
            // 1786
            numFormulario = GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> paramEntrada = SessionUtil.getFlash();

            if (paramEntrada != null) {
                proceso = paramEntrada
                                .get(FrmMonitorHistorialControladorEnum.PR_PROCESO
                                                .getValue())
                                .toString();

                tipoTramite = paramEntrada
                                .get(FrmMonitorHistorialControladorEnum.PR_TIPO_TRAMITE
                                                .getValue())
                                .toString();

                tramite = paramEntrada
                                .get(FrmMonitorHistorialControladorEnum.PR_TRAMITE
                                                .getValue())
                                .toString();

                ridTramite = (Map<String, Object>) paramEntrada
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
        tabla = GenericUrlEnum.D_TRAMITES.getTable();

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asignan las urls asociadas a las operaciones
     * basicas (CRUD) del formulario y los parametros de la grilla.
     */
    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil
                        .getUrlBeanById(FrmMonitorHistorialControladorUrlEnum.URL0001
                                        .getValue());

        parametrosListado.put("COMPANIA", compania);
        parametrosListado.put("PROCESO", proceso);
        parametrosListado.put("TIPO_TRAMITE", tipoTramite);
        parametrosListado.put("TRAMITE", tramite);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Inf. Detallada (BT3129).
     * Redirecciona al formulario Informacion Detallada del Tramite
     * (1780).
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirBtInfDetallada(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();

        param.put(DTramiteVariablesControladorEnum.PR_PROCESO.getValue(),
                        proceso);

        param.put(DTramiteVariablesControladorEnum.PR_TIPO_TRAMITE.getValue(),
                        tipoTramite);

        param.put(DTramiteVariablesControladorEnum.PR_TRAMITE.getValue(),
                        tramite);

        param.put(DTramiteVariablesControladorEnum.PR_D_TRAMITE.getValue(),
                        reg.getCampos().get("CONSECUTIVO"));

        param.put(DTramiteVariablesControladorEnum.PR_NODO.getValue(),
                        reg.getCampos().get("NODO_DESTINO"));

        param.put(DTramiteVariablesControladorEnum.PR_COD_FORM.getValue(),
                        GeneralCodigoFormaEnum.FRM_MONITOR_HISTORIAL_CONTROLADOR
                                        .getCodigo());

        param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), ridTramite);

        // Direccionar
        Direccionador dir = new Direccionador();

        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.D_TRAMITE_VARIABLES_CONTROLADOR
                                        .getCodigo()));
        dir.setParametros(param);

        SessionUtil.redireccionarForma(dir, modulo);
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
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro.
     * 
     * @return true -> Permite realizar la insercion.
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
     * Metodo ejecutado antes de realizar la insercion o actualizacion
     * del registro.
     * 
     * @return true -> Permite realizar la insercion o actualizacion
     * del resgitro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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
     * @return true -> Permite realizar la eliminacion del registro.
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario. Redirecciona al formulario de tramites o al menu
     * dependiendo de la opcion de menu desde la cual se accedio al
     * formulario.
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        switch (menuActual) {
        case "350201":
        case "350202":
            redireccionarATramite();
            break;
        default:
            SessionUtil.redireccionarMenu();
            break;
        }
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
     * Metodo utilizado para redireccionar al formulario de Tramites
     * (1763)
     */
    private void redireccionarATramite() {
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmTramitesControladorEnum.PR_ROWKEY.getValue(), ridTramite);

        Direccionador dir = new Direccionador();
        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_TRAMITES_CONTROLADOR
                                        .getCodigo()));
        dir.setParametros(param);

        SessionUtil.redireccionarForma(dir, modulo);
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
