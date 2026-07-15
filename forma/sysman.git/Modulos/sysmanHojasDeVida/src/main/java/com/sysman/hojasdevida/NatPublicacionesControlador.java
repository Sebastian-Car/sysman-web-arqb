/*-
 * NatPublicacionesControlador.java
 *
 * 1.0
 * 
 * 16/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.NatPublicacionesControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Clase migrada para administrar las publicaciones de una persona
 *
 * @version 1.0, 04/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 * @version 2.0 22/06/2018, Se realiza PY.ASEGURAMIENTO E INTEGRACION
 * descrito en el tar 1000081152.Se crea validacion de campos
 * obligatorios
 * @author eamaya
 * 
 * @version 2.2 24/08/2018, Se creo el metodo generarConsecutivo para
 * que cuando al abrir el formulario llene automaticamente el campo de
 * consecutivo, se agrego en el metodo eliminarDespues
 * (listaInicial.load();)
 * 
 */
@ManagedBean
@ViewScoped
public class NatPublicacionesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * variable definida para almacenar el numero de documento del
     * empleado recibido por parametro
     */
    private String numeroDcto;
    /**
     * variable definida para almacenar la sucursal del empleado
     * recibido por parametro
     */
    private String sucursal;

    /**
     * variable definida para almacenar el codigo del empleado
     * recibido por parametro
     */
    private String codigo;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de NatpublicacionesControlador
     */
    public NatPublicacionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {

            // 1523
            numFormulario = GeneralCodigoFormaEnum.PUBLICACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            numeroDcto = (String) parametrosEntrada.get("numeroDcto");
            sucursal = (String) parametrosEntrada.get("sucursal");
            codigo = (String) parametrosEntrada.get("codigo");
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

        enumBase = GenericUrlEnum.NAT_PUBLICACIONES;
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
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        numeroDcto);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

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
        generarConsecutivo();
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
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        numeroDcto);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put("PB_CODIGOPERSONA", codigo);

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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove("PB_CODIGOPERSONA");
        registro.getCampos()
                        .remove(NatPublicacionesControladorEnum.PU_CONSECUTIVO
                                        .getValue());

        // </CODIGO_DESARROLLADO>
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
        // <CODIGO_DESARROLLADO>
        generarConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    public void generarConsecutivo() {
        try {
            Long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            enumBase.getTable(),
                            SysmanFunciones.concatenar("COMPANIA = ''",
                                            compania,
                                            "'' AND DP_NUMEDOCU = ''",
                                            numeroDcto,
                                            "'' AND SUCURSAL = ''", sucursal,
                                            "''"),
                            NatPublicacionesControladorEnum.PU_CONSECUTIVO
                                            .getValue(),
                            "1");

            registro.getCampos()
                            .put(NatPublicacionesControladorEnum.PU_CONSECUTIVO
                                            .getValue(), consecutivo);

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

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
