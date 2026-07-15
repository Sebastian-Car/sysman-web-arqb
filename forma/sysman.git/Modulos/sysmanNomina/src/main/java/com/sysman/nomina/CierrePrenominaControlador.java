/*-
 * CierrePrenominaControlador.java
 *
 * 1.0
 *
 * 20/09/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.nomina.ejb.EjbNominaUnoRemote;
import com.sysman.util.SysmanFunciones;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Bean para el cierre de prenomina el cual guarda una copia del periodo de nomina actual.
 *
 * @version 1.0, 20/09/2018
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class CierrePrenominaControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private int procesoNomina;
    private int anioNomina;
    private int mesNomina;
    private int periodoNomina;

    @EJB
    private EjbNominaUnoRemote ejbNominaUno;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CierrePrenominaControlador
     */
    public CierrePrenominaControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {

            numFormulario = GeneralCodigoFormaEnum.CIERRE_PRENOMINA_CONTROLADOR.getCodigo();
            validarPermisos();

            procesoNomina = Integer.parseInt(validarSessionCadena(SessionUtil.getSessionVar("procesoNomina")));
            anioNomina = Integer.parseInt(validarSessionCadena(SessionUtil.getSessionVar("anioNomina")));
            mesNomina = Integer.parseInt(validarSessionCadena(SessionUtil.getSessionVar("mesNomina")));
            periodoNomina = Integer.parseInt(validarSessionCadena(SessionUtil.getSessionVar("periodoNomina")));
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
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
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
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
     * Metodo ejecutado al oprimir el boton BT2533 en la vista
     *
     * Boton para cerrar la prenomina del perioda ctual
     *
     */
    public void oprimirBtCerrar()
    {
        try
        {
            JsfUtil.agregarMensajeInformativo(
                            ejbNominaUno.cierreNominaPreliminar(compania, procesoNomina, anioNomina, mesNomina, periodoNomina,
                                            SessionUtil.getUser().getCodigo()));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    private String validarSessionCadena(Object objeto)
    {
        return SysmanFunciones.validarVariableVacio(objeto.toString()) ? ""
            : objeto.toString();
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
