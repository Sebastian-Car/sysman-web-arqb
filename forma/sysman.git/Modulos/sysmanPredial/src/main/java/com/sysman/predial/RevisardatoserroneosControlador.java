/*-
 * RevisardatoserroneosControlador.java
 *
 * 1.0
 * 
 * 14/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbGeneralesRemote;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Por medio de este controlador se revisan datos nulos que generan
 * conflicto en los procesos
 *
 * @version 1.0, 14/02/2017
 * @author jcrodriguez
 * 
 * @modifier amonroy
 * @version 2, 19/07/2017 Se realiza el Proceso de Refactoring e
 * implementacion de EJBs para las funciones y procedimientos que son
 * llamadas en el controlador
 */
@ManagedBean
@ViewScoped
public class RevisardatoserroneosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Implementacion del EJB de EjbGeneralesRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_GENERALES
     */
    @EJB
    private EjbGeneralesRemote ejbGenerales;

    /**
     * Crea una nueva instancia de RevisardatoserroneosControlador
     */
    public RevisardatoserroneosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.REVISARDATOSERRONEOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RevisardatoserroneosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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

    /**
     * Realiza el llamado a la funcion
     * PCK_GENERALES.FC_ACT_DATOSERROR_GENERAL
     * 
     * @param tabla
     * Tabla en la que se desea evaluar una columna
     * @param campo
     * Columna especifica en la que se desea realizar la revision de
     * datos
     * @return Cantidad de registros afectados
     */
    private int actPredial(String tabla, String campo) {
        int actualizar = 0;
        try {
            actualizar = ejbGenerales.actDatosErrorGeneral(tabla,
                            campo,
                            SessionUtil.getUser().getCodigo());

            return actualizar;
        }
        catch (SystemException e) {

            Logger.getLogger(RevisardatoserroneosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return actualizar;
    }

    /**
     * Realiza el llamado al metodo actPerdial() para hacer la
     * revision de datos en las columnas nombre y direccion de la
     * tabla IP_USUARIOS_PREDIAL
     */
    public void oprimirCmdIniciar() {
        int actualizarAux;
        actualizarAux = actPredial("IP_USUARIOS_PREDIAL", "DIRECCION")
            + actPredial("IP_USUARIOS_PREDIAL", "NOMBRE");
        if (actualizarAux > 0) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2821").replace("s$contar$s",
                                            Integer.toString(actualizarAux)));
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2822"));
        }
    }

    public void oprimirCmdCancelar() {
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
    }

    public String getCompania() {
        return compania;
    }

}
