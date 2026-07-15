/*-
 * DatostercerofinanciablesControlador.java
 *
 * 1.0
 *
 * 09/02/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.DatostercerofinanciablesControladorUrlEnum;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 * Clase migrada que permite actualizar los campos nombres, identificacion,lugar expedicion,celular y rol de la tabla SP_FINANCIABLESDEDEUDA
 *
 * Se visualiza la forma en el boton ver/editar del formulario Financiables de deuda
 *
 * @version 1.0, 09/02/2017
 * @author ybecerra
 * @version 2, 18/05/2017
 * @author spina - se realizar refactoring para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class DatostercerofinanciablesControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo que almacena el consecutivo recibido por parametro
     */
    private String consecutivo;
    Map<String, Object> ridFinanciable;
    /**
     * Crea una nueva instancia de DatostercerofinanciablesControlador
     */
    @SuppressWarnings("unchecked")
    public DatostercerofinanciablesControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {

                ridFinanciable = (Map<String, Object>) parametrosEntrada
                                .get("ridFinanciableDeuda");
                consecutivo = ridFinanciable.get("CONSECUTIVO").toString();
            }
            registro = new Registro(new HashMap<String, Object>());

            numFormulario = GeneralCodigoFormaEnum.DATOSTERCEROFINANCIABLES_CONTROLADOR.getCodigo();
            validarPermisos();  
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
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        tabla = "SP_FINANCIABLESDEDEUDA";
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        urlLectura = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatostercerofinanciablesControladorUrlEnum.URL5304
                                                        .getValue());
        parametrosListado.put("KEY_COMPANIA",
                        compania);
        parametrosListado.put("KEY_CONSECUTIVO", consecutivo);
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatostercerofinanciablesControladorUrlEnum.URL5305
                                        .getValue());

    }

    /**
     * Se realiza la asignacion de la variable origenGrilla por la consulta correspondiente de la grilla del formulario, se hace la asignacion de dicha consulta a los objetos listaInicial y
     * listaInicialF
     *
     *
     */
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        cargarRegistro(parametrosListado, ACCION_MODIFICAR);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     * @return true
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
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     *
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
            
        }

        // </CODIGO_DESARROLLADO>
        return true;
    }
    
    
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     * @return true
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
     *
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     *
     */
    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    // <SET_GET_ATRIBUTOS>

    public String getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(String consecutivo)
    {
        this.consecutivo = consecutivo;
    }
 
}
