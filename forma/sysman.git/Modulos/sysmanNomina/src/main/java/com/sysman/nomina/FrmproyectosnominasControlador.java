/*-
 * FrmproyectosnominasControlador.java
 *
 * 1.0
 * 
 * 07/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.nomina.enums.FrmproyectosnominasControladorEnum;

/**
 * Realiza el CRUD para la tabla PROYECTOSNOM.
 *
 * @version 1.0, 07/02/2018
 * @author fperez
 */
@ManagedBean
@ViewScoped
public class FrmproyectosnominasControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compañía en la cual inició sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente.
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
     * Crea una nueva instancia de FrmproyectosnominasControlador.
     */
    public FrmproyectosnominasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            /** Formulario no 1712. */
            numFormulario = GeneralCodigoFormaEnum.PROYECTOS_NOMINA_CONTROLADOR
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
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualización del
     * formulario, como son tablas, origenes de datos, inicialización
     * de listas y demas necesarios.
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PROYECTOSNOM;
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
     * En este método se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. También carga la lista
     * del formulario por primera vez.
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(
                        FrmproyectosnominasControladorEnum.COMPANIA
                                        .getValue(),
                        compania);
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
     * Este método es invocado el método inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método ejecutado cuando se cancela la edición del registro.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Método ejecutado antes de realizar la inserción del registro.
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(
                        FrmproyectosnominasControladorEnum.COMPANIA.getValue(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción del registro.
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la inserción y actualización
     * del registro.
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción y
     * actualización del registro.
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la eliminación del registro.
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la eliminación del
     * registro.
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este método se ejecuta antes enviar la acción de actualización,
     * en él se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro.
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(FrmproyectosnominasControladorEnum.COMPANIA
                        .getValue());
        registro.getCampos().remove(
                        FrmproyectosnominasControladorEnum.ID_DE_PROYECTO
                                        .getValue());
    }

    /**
     * Este método es ejecutado despues de finalizar la inserción y
     * edición del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones.
     */
    @Override
    public void asignarValoresRegistro() {
        // No hay código aquí.
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
