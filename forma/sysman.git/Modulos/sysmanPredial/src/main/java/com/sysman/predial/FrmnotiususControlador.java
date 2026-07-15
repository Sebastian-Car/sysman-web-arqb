/*-
 * FrmnotiususControlador.java
 *
 * 1.0
 * 
 * 07/02/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Controlador de la forma Frmnotiusus asociado al formulario
 * "Notificaciones de Cobro".
 *
 * @version 1.0, 07/02/2017
 * @author yrojas
 * 
 * @author ybecerra
 * @version 2, 06/07/2017, proceso de Refactoring
 */
@ManagedBean
@ViewScoped

public class FrmnotiususControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante que contiene el campo de observacion nueva.
     */
    private final String observacionNueva;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    /**
     * Variable de tipo String que guarda el codigo del predio enviado
     * desde el controlador Usuariopredials.
     */
    private String codigoPredio;

    /**
     * Variable de tipo HashMap que recibe los parametros de entrada
     * enviados desde Usuariopredials.
     */
    private Map<String, Object> parametrosEntrada;

    private Registro reg;

    /**
     * Crea una nueva instancia de FrmnotiususControlador
     */
    public FrmnotiususControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        observacionNueva = "OBSERVACIONNUEVA";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMNOTIUSUS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                codigoPredio = (String) parametrosEntrada.get("codigoPredio");
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
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
    public void inicializar()
    {
        enumBase = GenericUrlEnum.IP_NOTIFICACIONES;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.PREDIO.getName(),
                        codigoPredio);

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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues()
    {
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
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (!validarObservacionNueva())
        {
            return false;

        }
        registro.getCampos().remove(observacionNueva);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo que valida si el campo de observacion nueva esta o no}
     * vacio para evitar hacer la actualizacion.
     * 
     * @return variable booleana
     */
    public boolean validarObservacionNueva()
    {
        String aux = registro.getCampos().get(observacionNueva).toString();

        if (!SysmanFunciones.validarVariableVacio(aux))
        {
            registro.getCampos().put("OBSERVACIONES",
                            (String) registro.getCampos().get("OBSERVACIONES")
                                + " - " + (String) registro.getCampos()
                                                .get(observacionNueva));
        }
        else
        {
            JsfUtil.agregarMensajeAlerta("Agregue alguna observación nueva");
            return false;
        }
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return variable booleana
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return variable booleana
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return variable booleana
     */
    @Override
    public boolean eliminarDespues()
    {
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
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
        registro.getCampos().remove("FECHANOTIFICACION");
        registro.getCampos().remove("DOCSOPORTE");
        registro.getCampos().remove("NOTIFICADOR");

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro)
    {
        indice = listaInicial.getRowIndex();
        reg = new Registro(new HashMap<>(registro.getCampos()));

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // NO ESTA IMPLEMENTADO
    }
    // <SET_GET_ATRIBUTOS>

    public void setIndice(int indice)
    {
        this.indice = indice;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice()
    {
        return indice;
    }

}
