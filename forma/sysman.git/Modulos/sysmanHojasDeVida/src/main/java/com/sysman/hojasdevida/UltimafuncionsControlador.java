/*-
 * UltimafuncionsControlador.java
 *
 * 1.0
 * 
 * 08/03/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.hojasdevida.enums.UltimafuncionsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Clase encargada de visualizar las funciones de cada empleado
 *
 * @version 1.0, 09/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class UltimafuncionsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable necargada de almacenar las llaves del formulario
     * principal
     */
    private Map<String, Object> ridPrincipal;

    /**
     * Variable necargada de almacenar las llaves del formulario
     * principal
     */
    private Map<String, Object> ridNombramiento;
    /**
     * Variable encargdad de almacenar temporalmente el numero
     * Documento de la persona
     */
    private String nroDocumento;
    /**
     * Varibale encargada de almacenar la sucursal de la persona
     */
    private String sucursal;
    /**
     * Varibale encargada de almacenar la sucursal de la persona
     */
    private String codigo;
    private String cargo;
    private String dependencia;
    /**
     * Variable encargada de almacenar el String "EV_FUNCIONES"
     */
    private final String evFuncionesCons;

    private String redireccion;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de UltimafuncionsControlador
     */
    @SuppressWarnings("unchecked")
    public UltimafuncionsControlador() {
        super();
        evFuncionesCons = "EV_FUNCIONES";
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            ridPrincipal = (Map<String, Object>) parametros.get("ridDatos");

            nroDocumento = (String) parametros.get("nroDocumento");
            codigo = (String) parametros.get("codigo");
            cargo = (String) parametros.get("cargo");
            dependencia = parametros.get("dependencia").toString();
            redireccion = (String) SysmanFunciones
                            .nvl(parametros.get("redireccion"), "0");
            if ("-1".equals(redireccion)) {
                ridNombramiento = (Map<String, Object>) parametros.get("rid");
            }

            sucursal = (String) parametros.get("sucursal");
        }

        try {
            numFormulario = GeneralCodigoFormaEnum.ULTIMAFUNCIONS_CONTROLADOR
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

        tabla = evFuncionesCons;
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

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        if ("-1".equals(redireccion)) {
            parametrosListado.put("CARGO", cargo);
            parametrosListado.put("DEPENDENCIA", dependencia);

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            UltimafuncionsControladorUrlEnum.URL6231
                                                            .getValue());
        }
        else {
            parametrosListado.put("NRODOCUMENTO", nroDocumento);
            parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                            sucursal);

            urlListado = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            UltimafuncionsControladorUrlEnum.URL6229
                                                            .getValue());
        }

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
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
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
        //
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ridDatos", ridPrincipal);
        parametros.put("rid", ridNombramiento);
        parametros.put("sucursal", sucursal);
        parametros.put("dp_numedocu", nroDocumento);
        parametros.put("codigo", codigo);
        parametros.put("redireccion", redireccion);
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(String
                        .valueOf("-1".equals(redireccion)
                            ? GeneralCodigoFormaEnum.SUB_NOMBRAMIENTOS_CONTROLADOR
                                            .getCodigo()
                            : GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                            .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }
    // <SET_GET_ATRIBUTOS>

    /**
     * @return the dependencia
     */
    public String getDependencia() {
        return dependencia;
    }

    /**
     * @param dependencia
     * the dependencia to set
     */
    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
