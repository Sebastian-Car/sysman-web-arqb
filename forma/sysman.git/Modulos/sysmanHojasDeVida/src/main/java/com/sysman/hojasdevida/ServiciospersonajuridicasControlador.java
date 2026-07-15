/*-
 * ServiciospersonajuridicasControlador.java
 *
 * 1.0
 * 
 * 06/02/2018
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Clase encargada de gestionar los servicios de los tercero con
 * naturaleza juridica
 *
 * @version 1.0, 06/02/2018
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class ServiciospersonajuridicasControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    @EJB
    EjbSysmanUtil ejbsysmanUtl;

    /**
     * Variable encargada de almacenar temporalemnte la sucursal del
     * tercero con naturaleza juridica
     */
    private String sucursalSociedad;
    /**
     * Variable encargada de almacenar temporalemnte NIT del tercero
     * con naturaleza juridica
     */
    private String nitSociedad;
    /**
     * Variable encargada de almacenar temporalmente el nombre de la
     * razon social
     */
    private String nombreSociedad;

    private final String nombreSociedadCons;
    private final String nitSociedadCons;
    private String accion;

    private boolean bloqueaOperacines;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * ServiciospersonajuridicasControlador
     */
    public ServiciospersonajuridicasControlador() {
        super();
        nitSociedadCons = "NITSOCIEDAD";
        nombreSociedadCons = "NOMBRE_SOCIEDAD";
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            nitSociedad = (String) parametros.get("nitSociedad");
            sucursalSociedad = (String) parametros.get("sucursal");
            nombreSociedad = (String) parametros.get("nombreSociedad");
            accion = (String) parametros.get("accion");
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.SERVICIOSPERSONAJURIDICAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        validarAccion();

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
        enumBase = GenericUrlEnum.NAT_JURSERVICIOS;

        reasignarOrigen();
        buscarLlave();

        registro = new Registro();
        registro.getCampos().put(nitSociedadCons, nitSociedad);
        registro.getCampos().put(nombreSociedadCons, nombreSociedad);

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
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursalSociedad);
        parametrosListado.put("NIT", nitSociedad);

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
        /*
         * FR1702-AL_ABRIR Private Sub Form_Open(Cancel As Integer) '
         * ','DoCmd.Maximize ' formularioAbrir 0, Me.Name End Sub
         */
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

        registro.getCampos().put(nitSociedadCons, nitSociedad);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursalSociedad);

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(),
                        generarConsecutivo());
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
        registro.getCampos().remove(nombreSociedadCons);
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
        //
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("KEY_NITSOCIEDAD", nitSociedad);

        parametros.put("KEY_SUCURSAL", sucursalSociedad);
        parametros.put("KEY_COMPANIA", compania);
        Map<String, Object> rid = new HashMap<>();
        rid.put("rid", parametros);
        rid.put("accion", accion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.JURIDENTIFICACIONS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(rid);

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
        registro.getCampos().put(nombreSociedadCons, nombreSociedad);
        registro.getCampos().put(nitSociedadCons, nitSociedad);
    }

    private long generarConsecutivo() {
        String criterio = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND  NITSOCIEDAD = ''", nitSociedad,
                        "'' AND SUCURSAL = ''", sucursalSociedad, "'' ");
        long consecutivo = 0;
        try {
            consecutivo = ejbsysmanUtl.generarConsecutivoConValorInicial(
                            "NAT_JURSERVICIOS", criterio, "NUMERO", "1");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return consecutivo;

    }

    private void validarAccion() {
        if ("v".equals(accion)) {
            bloqueaOperacines = false;

        }
        else {

            bloqueaOperacines = true;
        }
    }

    public boolean isBloqueaOperacines() {
        return bloqueaOperacines;
    }

    public void setBloqueaOperacines(boolean bloqueaOperacines) {
        this.bloqueaOperacines = bloqueaOperacines;
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
