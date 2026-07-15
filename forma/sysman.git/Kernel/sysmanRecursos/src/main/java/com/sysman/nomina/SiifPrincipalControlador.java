/*-
 * SiifPrincipalControlador.java
 *
 * 1.0
 * 
 * 20/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 * Formulario que permite gestionar el proceso de nomina siif.
 *
 * @version 1.0, 20/02/2018
 * @author jreina
 */

@ManagedBean
@ViewScoped
public class SiifPrincipalControlador extends BeanBaseModal
{
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
     * Crea una nueva instancia de SiifPrincipalControlador
     */
    public SiifPrincipalControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.SIIFPRINCIPAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Salir en la vista
     *
     */
    public void oprimirSalir()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton InfRes5544 en la vista
     *
     */
    public void oprimirConfiguracionPersonal()
    {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionadorDocum = new Direccionador();
        direccionadorDocum.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.PERSONA_SIIF_CONTROLADOR
                                        .getCodigo()));

        RequestContext.getCurrentInstance().closeDialog(direccionadorDocum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton DOCUMETNOSS en la vista
     *
     */
    public void oprimirConfiguracionDocumentos()
    {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionadorDocum = new Direccionador();
        direccionadorDocum.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.DOCUMENTOS_CONTROLADOR
                                        .getCodigo()));

        RequestContext.getCurrentInstance().closeDialog(direccionadorDocum);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Configuracion Conceptos de
     * Nomina en la vista (BT3002). Redirecciona al formulario:
     * <code>conceptosb</code>.
     */
    public void oprimirConfiguracionConceptos()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new HashMap<>();

        Direccionador dir = new Direccionador();
        dir.setParametros(param);

        dir.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.CONCEPTOSBS_CONTROLADOR
                                        .getCodigo()));

        RequestContext.getCurrentInstance().closeDialog(dir);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton bancos en la vista
     *
     */
    public void oprimirConfiguracionBancos()
    {
        // <CODIGO_DESARROLLADO>

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.BANCOS_CONTROLADOR.getCodigo()));

        RequestContext.getCurrentInstance().closeDialog(direccionador);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton pension en la vista
     *
     */
    public void oprimirConfiguracionFondos()
    {

        try
        {
            SessionUtil.setSessionVarContainer("origen",
                            "true");
            SessionUtil.setSessionVarContainer("retorno",
                            SessionUtil.getMenuActual());
            RequestContext.getCurrentInstance().closeDialog(null);

            SessionUtil.redireccionarModalAModalMenu(SysmanFunciones.toString(
                            GeneralCodigoFormaEnum.SELECCIONAR_FONDOS_CONTROLADOR
                                            .getCodigo()),
                            "60106");
        }
        catch (NamingException e)
        {
            Logger.getLogger(SessionUtil.class.getName()).log(Level.SEVERE,
                            null, e);
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton salud en la vista
     *
     */
    public void oprimirGeneracionInforme()
    {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.INFORMACION_NOMINASIIF_CONTROLADOR
                                        .getCodigo()));

        RequestContext.getCurrentInstance().closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
