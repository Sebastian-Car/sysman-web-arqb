/*-
 * RingresosConsumo.java
 *
 * 1.0
 * 
 * 04/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * TODO Ingrese una descripcion para la clase. Se a migrado formulario
 * 1845 moviviento de almacen modulo SysmanAl2018.05.12
 * 
 * @version 1, 04/07/2018 08:25:21 -- Modificado por bcardenas
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class RingresosConsumo extends BeanBaseModal {
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
     * Crea una nueva instancia de RingresosConsumo
     */
    public RingresosConsumo() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = 1845;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        /*
         * FR1845-AL_ABRIR Private Sub Form_Load() 'formularioAbrir
         * 10, Me.Name DoCmd.Restore Select Case Me.OpenArgs Case
         * "RIC" Me.lblTitulo =
         * UCase("Relación de Ingresos de Consumo") Me.txtClase = "E"
         * Me.txtElemento = "C" Case "REC" Me.lblTitulo =
         * UCase("Relación de Egresos de Consumo") 'Me.txtClase = "E"
         * 'Me.txtElemento = "C" Me.txtClase = "S" Me.txtElemento =
         * "C" Case "RID" Me.lblTitulo =
         * UCase("Relación de Ingresos de Devolutivos") Me.txtClase =
         * "E" Me.txtElemento = "D" If par("MANEJA NIIF EN ALMACEN",
         * Getcompany()) = "SI" Then Me.NIIF.visible = True End If
         * Case "RED" Me.lblTitulo =
         * UCase("Relación de Egresos de Devolutivos") Me.txtClase =
         * "S" Me.txtElemento = "D" Case "RIN" Me.lblTitulo =
         * UCase("Relación de Elementos Recibidos en Comodato")
         * Me.txtClase = "E" Me.txtElemento = "E" Case "REDS"
         * Me.lblTitulo = UCase("Relación de Salidas del Servicio")
         * Me.txtElemento = "D" Me.txtClase = "E" Case "RIDS"
         * Me.lblTitulo = UCase("Relación de Entradas al Servicio ")
         * Me.txtElemento = "D" Me.txtClase = "S" End Select End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton NIIF en la vista
     * 
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirNIIF() {
        // <CODIGO_DESARROLLADO>
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
