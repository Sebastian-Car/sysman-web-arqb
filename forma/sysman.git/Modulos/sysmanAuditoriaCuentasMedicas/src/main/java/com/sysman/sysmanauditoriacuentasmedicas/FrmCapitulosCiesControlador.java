/*-
 * FrmCapitulosCiesControlador.java
 *
 * 1.0
 * 
 * 09/10/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.sysmanauditoriacuentasmedicas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que permite configurar los capitulos cies
 *
 * @version 1.0, 09/10/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmCapitulosCiesControlador extends BeanBaseContinuoAcmeImpl {
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
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmCapitulosCiesControlador
     */
    public FrmCapitulosCiesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2116
            numFormulario = GeneralCodigoFormaEnum.FRM_CAPTULOSCIE_CONTROLADOR
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
        enumBase = GenericUrlEnum.CM_CAPITULOS_CIE;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
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
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO

    }

    @Override
    public void removerCombos() {
        // METODO_NO_IMPLEMENTADO

    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

    }

    @Override
    public boolean insertarAntes() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // METODO_NO_IMPLEMENTADO
        return true;
    }
}
