/*-
 * BancofgsControlador.java
 *
 * 1.0
 * 
 * 14/11/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.facturaciongeneral.enums.BancofgsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Formulario que lista los bancos
 *
 * @version 1.0, 14/11/2017
 * @author jromero
 */
@ManagedBean
@ViewScoped

public class BancofgsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String ano;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de BancofgsControlador
     */
    public BancofgsControlador() {
        super();
        compania = SessionUtil.getCompania();
        ano = SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue())
                        .toString();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ACTUALIZAR_FACTURA_CONTROLADOR
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.BANCO.getTable();
        buscarLlave();
        reasignarOrigen();
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void reasignarOrigen() {
        parametrosListado.put("COMPANIA", compania);
        parametrosListado.put("ANO", ano);
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        BancofgsControladorUrlEnum.URL0001.getValue());
    }

    @Override
    public void asignarValoresRegistro() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void removerCombos() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public boolean insertarAntes() {
        // METODO NO IMPLEMENTADO
        return false;
    }

    @Override
    public boolean insertarDespues() {
        // METODO NO IMPLEMENTADO
        return false;

    }

    @Override
    public boolean actualizarAntes() {
        return false;
        // METODO NO IMPLEMENTADO
    }

    @Override
    public boolean actualizarDespues() {
        return false;
        // METODO NO IMPLEMENTADO
    }

    @Override
    public boolean eliminarAntes() {
        return false;
        // METODO NO IMPLEMENTADO
    }

    @Override
    public boolean eliminarDespues() {
        return false;
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void abrirFormulario() {
        // METODO NO IMPLEMENTADO
    }
}
